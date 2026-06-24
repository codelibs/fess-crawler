/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.crawler.extractor.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.exception.InterruptedRuntimeException;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExecutionTimeoutException;
import org.codelibs.fess.crawler.exception.ExtractException;

/**
 * Extracts text content by executing an external command.
 */
public class CommandExtractor extends AbstractExtractor {
    private static final Logger logger = LogManager.getLogger(CommandExtractor.class);

    /** The encoding for the output. */
    protected String outputEncoding = Constants.UTF_8;

    /** The extension for the output file. */
    protected String outputExtension = null;

    /** The temporary directory for input/output files. */
    protected File tempDir = null;

    /** The command to execute. */
    protected String command;

    /** The timeout for command execution in milliseconds. */
    protected long executionTimeout = 30L * 1000L; // 30sec

    /** The working directory for the command. */
    protected File workingDirectory = null;

    /** The encoding for the command's output. */
    protected String commandOutputEncoding = Charset.defaultCharset().displayName();

    /**
     * The (formerly) maximum number of lines to buffer from command output.
     *
     * @deprecated The line-count cap has been removed in favor of a byte-count cap.
     *             This field is no longer consulted; use {@link #setMaxOutputSize(long)}
     *             to bound how many bytes are read from stdout/stderr. The field is
     *             retained only for source/binary compatibility with callers that set
     *             it via {@link #setMaxOutputLine(int)} or via reflection.
     */
    @Deprecated
    protected int maxOutputLine = 1000;

    /** Whether to redirect standard output to a file. */
    protected boolean standardOutput = false;

    /** Maximum bytes the subprocess is allowed to write to a single drained stream (stdout/stderr). */
    protected long maxOutputSize = 10L * 1024L * 1024L; // 10 MiB

    /** Whether {@link #setMaxOutputSize(long)} has been called explicitly, preventing {@link #setMaxOutputLine(int)} from overriding it. */
    private boolean maxOutputSizeExplicit = false;

    /** Maximum bytes copied from the input stream into the temporary input file. */
    protected long maxInputSize = 100L * 1024L * 1024L; // 100 MiB

    /** Grace period (ms) given to a process after destroy() before destroyForcibly() is invoked. */
    protected long destroyGracePeriodMillis = 2000L;

    /**
     * Whether to append captured stderr text to the extracted content when
     * {@link #standardOutput} is {@code false}. Defaults to {@code false} to
     * match pre-3.x behavior where stderr was only routed to logs, never to
     * extracted text. (The original {@code ProcessBuilder.redirectErrorStream(true)}
     * call only merged the streams for log draining; it never caused stderr to
     * appear in {@code ExtractData}.)
     *
     * <p>Set to {@code true} to append captured stderr after the file content in
     * the extracted body when {@code standardOutput=false}.
     */
    protected boolean includeStderrInOutput = false;

    /**
     * Constructs a new CommandExtractor.
     */
    public CommandExtractor() {
        // NOP
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.extractor.Extractor#getText(java.io.InputStream,
     * java.util.Map)
     */
    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        final String resourceName = params == null ? null : params.get(ExtractData.RESOURCE_NAME_KEY);

        String extention;
        String filePrefix;
        if (StringUtil.isNotBlank(resourceName)) {
            final String name = getFileName(resourceName);
            final String[] strings = name.split("\\.");
            final StringBuilder buf = new StringBuilder(100);
            if (strings.length > 1) {
                for (int i = 0; i < strings.length - 1; i++) {
                    if (buf.length() != 0) {
                        buf.append('.');
                    }
                    buf.append(strings[i]);
                }
                filePrefix = buf.toString();
                extention = strings[strings.length - 1];
            } else {
                filePrefix = name;
                extention = "";
            }
            filePrefix = filePrefix.replaceAll("\\p{Zs}", "_");
        } else {
            filePrefix = "none";
            extention = "";
        }
        // Sanitize prefix and extension to prevent argument-injection via crafted resourceName
        // when downstream commands re-expand args through a shell wrapper.
        filePrefix = filePrefix.replaceAll("[^A-Za-z0-9._-]", "_");
        if (filePrefix.startsWith("-")) {
            filePrefix = "_" + filePrefix;
        }
        if (!extention.isEmpty()) {
            extention = extention.replaceAll("[^A-Za-z0-9]", "");
            if (extention.length() > 16) {
                extention = extention.substring(0, 16);
            }
        }
        File inputFile = null;
        File outputFile = null;
        try {
            inputFile =
                    createTempFile("cmdextin_" + filePrefix + "_", StringUtil.isNotBlank(extention) ? "." + extention : extention, tempDir);
            String ext;
            if (outputExtension == null) {
                if (StringUtil.isNotBlank(extention)) {
                    ext = "." + extention;
                } else {
                    ext = extention;
                }
            } else {
                ext = outputExtension;
            }
            outputFile = createTempFile("cmdextout_" + filePrefix + "_", ext, tempDir);

            // store to a file (bounded by maxInputSize)
            copyToFileBounded(in, inputFile, maxInputSize);

            final String stderrText = executeCommand(inputFile, outputFile);

            // For standardOutput=false this is the only guard against the external command writing
            // an unbounded $OUTPUT_FILE — bounded readers cover stdout/stderr only.
            if (outputFile.length() > maxOutputSize) {
                logger.warn("output file size exceeded limit: size={} limit={} command={}", outputFile.length(), maxOutputSize, command);
                throw new ExtractException("output file size exceeded limit: limit=" + maxOutputSize);
            }

            final StringBuilder contentBuf = new StringBuilder();
            contentBuf.append(new String(FileUtil.readBytes(outputFile), outputEncoding));
            // For backward compatibility with the legacy implementation that used
            // ProcessBuilder.redirectErrorStream(true) when standardOutput=false,
            // append captured stderr text to the extracted content.
            if (!standardOutput && includeStderrInOutput && StringUtil.isNotEmpty(stderrText)) {
                contentBuf.append(stderrText);
            }
            final ExtractData extractData = new ExtractData(contentBuf.toString());
            if (StringUtil.isNotBlank(resourceName)) {
                extractData.putValues("resourceName", new String[] { resourceName });
            }

            return extractData;
        } catch (final IOException e) {
            throw new ExtractException("Could not extract content: resourceName=" + resourceName + " command=" + command, e);
        } finally {
            FileUtil.deleteInBackground(inputFile);
            FileUtil.deleteInBackground(outputFile);
        }
    }

    /**
     * Copies an input stream into the destination file but stops and throws if the
     * number of bytes read exceeds {@code limit}.
     *
     * @param in the input stream to read from
     * @param dest the destination file
     * @param limit the maximum number of bytes allowed
     * @throws IOException if an I/O error occurs
     * @throws ExtractException if the input stream exceeds {@code limit}
     */
    protected void copyToFileBounded(final InputStream in, final File dest, final long limit) throws IOException {
        long total = 0L;
        final byte[] buffer = new byte[8192];
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
            int n;
            while ((n = in.read(buffer)) != -1) {
                total += n;
                if (total > limit) {
                    logger.warn("input size exceeded limit: limit={} command={}", limit, command);
                    throw new ExtractException("input size exceeded limit: limit=" + limit);
                }
                out.write(buffer, 0, n);
            }
        }
    }

    String getFileName(final String resourceName) {
        final String name = resourceName.replaceAll("[/\\\\]+$", "");
        final int pos = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (pos >= 0) {
            return name.substring(pos + 1);
        }
        return name;
    }

    private String executeCommand(final File inputFile, final File outputFile) {

        if (StringUtil.isBlank(command)) {
            throw new CrawlerSystemException("External command is empty. Please configure a valid command for CommandExtractor.");
        }

        final Map<String, String> params = new HashMap<>();
        params.put("$INPUT_FILE", inputFile.getAbsolutePath());
        params.put("$OUTPUT_FILE", outputFile.getAbsolutePath());

        final List<String> cmdList = parseCommand(command, params);
        if (logger.isInfoEnabled()) {
            logger.info("executing command: command={}", cmdList);
        }

        final ProcessBuilder pb = new ProcessBuilder(cmdList);
        if (workingDirectory != null) {
            pb.directory(workingDirectory);
        }
        // Do not use pb.redirectOutput(outputFile) for standardOutput=true: OS-level redirection
        // bypasses the BoundedStreamReader, allowing unbounded writes. Instead, pipe stdout through
        // a BoundedFileWriter that enforces maxOutputSize.

        // Note: do not redirect error stream; we want stderr drained separately always.

        Process currentProcess = null;
        ExecutorService streamPool = null;
        Charset outCharset;
        try {
            outCharset = Charset.forName(commandOutputEncoding);
        } catch (final IllegalCharsetNameException | UnsupportedCharsetException e) {
            logger.warn("invalid commandOutputEncoding, falling back to UTF-8: encoding={} command={}", commandOutputEncoding, command, e);
            outCharset = StandardCharsets.UTF_8;
        }

        final AtomicBoolean shuttingDown = new AtomicBoolean(false);
        try {
            currentProcess = pb.start();

            streamPool = Executors.newFixedThreadPool(2, new DaemonThreadFactory("CommandExtractor-stream"));

            final Process processRef = currentProcess;
            final Charset charset = outCharset;
            final long limit = maxOutputSize;
            final AtomicBoolean overflowFlag = new AtomicBoolean(false);
            final Future<String> stdoutFuture;
            if (standardOutput) {
                // Drain stdout into outputFile with a byte-count bound to honour maxOutputSize.
                stdoutFuture = streamPool.submit(
                        new BoundedFileWriter(processRef.getInputStream(), outputFile, limit, "stdout", overflowFlag, shuttingDown));
            } else {
                stdoutFuture = streamPool
                        .submit(new BoundedStreamReader(processRef.getInputStream(), charset, limit, "stdout", overflowFlag, shuttingDown));
            }
            final Future<String> stderrFuture = streamPool
                    .submit(new BoundedStreamReader(processRef.getErrorStream(), charset, limit, "stderr", overflowFlag, shuttingDown));

            // Poll for process exit so that an overflow detected by a reader thread can
            // break out immediately rather than blocking for the full executionTimeout.
            final long deadline = System.currentTimeMillis() + executionTimeout;
            boolean exited = false;
            while (System.currentTimeMillis() < deadline) {
                if (overflowFlag.get()) {
                    // Reader thread detected overflow; kill the process tree immediately.
                    break;
                }
                if (currentProcess.waitFor(200, TimeUnit.MILLISECONDS)) {
                    exited = true;
                    break;
                }
            }

            if (!exited) {
                if (overflowFlag.get()) {
                    // Overflow path: kill the process tree, then surface the reader's exception.
                    shuttingDown.set(true);
                    destroyProcessTree(currentProcess);
                    // Collect exceptions from both futures with a short timeout.
                    for (final Future<String> f : List.of(stdoutFuture, stderrFuture)) {
                        try {
                            f.get(2, TimeUnit.SECONDS);
                        } catch (final ExecutionException ee) {
                            final Throwable cause = ee.getCause();
                            if (cause instanceof OutputSizeExceededException) {
                                logger.warn("command output exceeded limit: limit={} command={}", maxOutputSize, cmdList);
                                throw new ExtractException("command output exceeded limit: limit=" + maxOutputSize, cause);
                            }
                            logger.warn("unexpected error draining stream during overflow handling: command={}", cmdList, cause);
                            throw new CrawlerSystemException("Failed to drain command output during overflow.", cause);
                        } catch (final TimeoutException te) {
                            // overflow already detected; surface the ExtractException below
                        } catch (final InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    // guard: overflow flag set but no overflow exception observed
                    throw new ExtractException("command output exceeded limit: limit=" + maxOutputSize);
                } else {
                    // Timeout path.
                    logger.warn("command timed out: timeout={}ms command={}", executionTimeout, cmdList);
                    shuttingDown.set(true);
                    destroyProcessTree(currentProcess);
                    throw new ExecutionTimeoutException("The command execution is timeout: " + cmdList);
                }
            }

            // Process exited; collect drained output. Use a short timeout to avoid waiting forever
            // if the reader threads are stuck (they shouldn't be since the streams are now closed).
            String stdoutText = "";
            String stderrText = "";
            try {
                stdoutText = stdoutFuture.get(5, TimeUnit.SECONDS);
                stderrText = stderrFuture.get(5, TimeUnit.SECONDS);
            } catch (final TimeoutException te) {
                logger.warn("timed out collecting drained output: command={}", cmdList);
                stdoutFuture.cancel(true);
                stderrFuture.cancel(true);
                destroyProcessTree(currentProcess);
                throw new ExtractException("Failed to drain command output within 5s: command=" + cmdList, te);
            } catch (final ExecutionException ee) {
                final Throwable cause = ee.getCause();
                if (cause instanceof OutputSizeExceededException) {
                    logger.warn("command output exceeded limit: limit={} command={}", maxOutputSize, cmdList);
                    shuttingDown.set(true);
                    destroyProcessTree(currentProcess);
                    throw new ExtractException("command output exceeded limit: limit=" + maxOutputSize, cause);
                }
                if (cause instanceof IOException) {
                    throw new CrawlerSystemException("Failed to drain command output.", cause);
                }
                throw new CrawlerSystemException("Failed to drain command output.", ee);
            }

            final int exitValue = currentProcess.exitValue();
            if (exitValue != 0) {
                logger.warn("command exited non-zero: exitCode={} command={}", exitValue, cmdList);
            }

            if (logger.isInfoEnabled()) {
                if (standardOutput) {
                    logger.info("command exited: exitCode={}", exitValue);
                } else {
                    logger.info("command exited: exitCode={} stdout={}", exitValue, truncateForLog(stdoutText));
                }
                if (StringUtil.isNotEmpty(stderrText)) {
                    logger.info("command stderr: stderr={}", truncateForLog(stderrText));
                }
            }
            return stderrText;
        } catch (final CrawlerSystemException e) {
            throw e;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            if (currentProcess != null) {
                shuttingDown.set(true);
                destroyProcessTree(currentProcess);
            }
            throw new InterruptedRuntimeException(e);
        } catch (final Exception e) {
            logger.warn("unexpected error executing command: command={}", cmdList, e);
            throw new CrawlerSystemException("Failed to execute command: " + cmdList, e);
        } finally {
            if (currentProcess != null && currentProcess.isAlive()) {
                shuttingDown.set(true);
                destroyProcessTree(currentProcess);
            }
            if (streamPool != null) {
                streamPool.shutdownNow();
                try {
                    if (!streamPool.awaitTermination(1, TimeUnit.SECONDS)) {
                        logger.warn("stream pool did not terminate within 1s: command={}", cmdList);
                    }
                } catch (final InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Sends SIGTERM, waits a grace period, then SIGKILL to the process and any descendants.
     * Descendants are snapshotted before sending SIGTERM so that children reparented to
     * PID 1 after the parent exits are still reachable.
     *
     * @param process the process to terminate
     */
    protected void destroyProcessTree(final Process process) {
        if (process == null) {
            return;
        }
        // Snapshot descendants before sending SIGTERM. If the parent shell receives SIGTERM and
        // exits gracefully its children may be reparented to PID 1, making them invisible via
        // process.descendants() after the fact. Taking the snapshot first ensures we still reach them.
        final List<ProcessHandle> descendantSnapshot;
        try {
            descendantSnapshot = process.descendants().collect(Collectors.toList());
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to snapshot descendants.", e);
            }
            // Fall back to an empty list; we will still kill the parent below.
            try {
                process.destroy();
            } catch (final Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("destroy step failed: pid={}", process == null ? -1 : process.pid(), ex);
                }
            }
            try {
                process.destroyForcibly();
            } catch (final Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("destroy step failed: pid={}", process == null ? -1 : process.pid(), ex);
                }
            }
            return;
        }
        try {
            process.destroy();
        } catch (final Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("destroy step failed: pid={}", process == null ? -1 : process.pid(), ex);
            }
        }
        try {
            process.waitFor(destroyGracePeriodMillis, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        // Forcibly kill parent and all snapshotted descendants regardless of whether the parent
        // exited cleanly during the grace period.
        for (final ProcessHandle h : descendantSnapshot) {
            try {
                if (h.isAlive()) {
                    h.destroyForcibly();
                }
            } catch (final Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("destroy step failed: pid={}", h.pid(), ex);
                }
            }
        }
        try {
            process.destroyForcibly();
        } catch (final Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("destroy step failed: pid={}", process == null ? -1 : process.pid(), ex);
            }
        }
        // Bounded re-scan loop to catch fork-after-snapshot.
        for (int attempt = 0; attempt < 3; attempt++) {
            final List<ProcessHandle> live;
            try {
                live = process.descendants().filter(ProcessHandle::isAlive).collect(Collectors.toList());
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("rescan failed: pid={}", process.pid(), e);
                }
                break;
            }
            if (live.isEmpty()) {
                break;
            }
            for (final ProcessHandle h : live) {
                try {
                    h.destroyForcibly();
                } catch (final Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("destroy descendant failed: pid={}", h.pid(), e);
                    }
                }
            }
            try {
                Thread.sleep(50);
            } catch (final InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static String truncateForLog(final String s) {
        if (s == null) {
            return "";
        }
        final int max = 4000;
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max) + "... [truncated]";
    }

    /**
     * @param command2
     * @param params
     * @return
     */
    List<String> parseCommand(final String command, final Map<String, String> params) {
        final String cmd = command.trim();
        final List<String> cmdList = new ArrayList<>();
        final StringBuilder buf = new StringBuilder(100);
        boolean singleQuote = false;
        boolean doubleQuote = false;
        char prevChar = ' ';
        for (int i = 0; i < cmd.length(); i++) {
            final char c = cmd.charAt(i);
            if (c == ' ' && !singleQuote && !doubleQuote && buf.length() > 0) {
                cmdList.add(getCommandValue(buf.toString(), params));
                buf.delete(0, buf.length());
            } else if (c == '\"' && !singleQuote && prevChar != '\\') {
                if (doubleQuote) {
                    if (buf.length() > 0) {
                        cmdList.add(getCommandValue(buf.toString(), params));
                        buf.delete(0, buf.length());
                    }

                    doubleQuote = false;
                } else {
                    doubleQuote = true;
                }
            } else if (c == '\'' && !doubleQuote && prevChar != '\\') {
                if (singleQuote) {
                    if (buf.length() > 0) {
                        cmdList.add(getCommandValue(buf.toString(), params));
                        buf.delete(0, buf.length());
                    }

                    singleQuote = false;
                } else {
                    singleQuote = true;
                }
            } else {
                buf.append(c);
            }
            prevChar = c;
        }
        if (buf.length() > 0) {
            cmdList.add(getCommandValue(buf.toString(), params));
        }
        return cmdList;
    }

    private String getCommandValue(final String key, final Map<String, String> params) {
        final String value = params.get(key);
        if (value == null) {
            return key;
        }
        return value;
    }

    /**
     * Daemon thread factory for stream-drain workers.
     */
    protected static class DaemonThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final AtomicInteger counter = new AtomicInteger();

        /**
         * Constructs a new DaemonThreadFactory.
         *
         * @param namePrefix the prefix used to name threads created by this factory
         */
        public DaemonThreadFactory(final String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = new Thread(r, namePrefix + "-" + counter.incrementAndGet());
            t.setDaemon(true);
            return t;
        }
    }

    /**
     * Reads a stream fully (up to {@code limit} bytes) and returns it as a String.
     * If the stream produces more than {@code limit} bytes, {@code overflowFlag} is
     * set to {@code true} and an {@link OutputSizeExceededException} is thrown so
     * that the main thread can detect the overflow without waiting for the full
     * {@code executionTimeout}.
     */
    protected static class BoundedStreamReader implements Callable<String> {
        private final InputStream stream;
        private final Charset charset;
        private final long limit;
        private final String streamName;
        private final AtomicBoolean overflowFlag;
        private final AtomicBoolean shuttingDown;

        /**
         * Constructs a new BoundedStreamReader.
         *
         * @param stream the input stream to drain
         * @param charset the charset used to decode the bytes
         * @param limit the maximum number of bytes accepted before the process is killed
         * @param streamName a label used in error messages and logs
         * @param overflowFlag shared flag that is set to {@code true} just before
         *                     {@link OutputSizeExceededException} is thrown, allowing the main
         *                     thread's polling loop to break early and kill the process tree
         * @param shuttingDown shared flag indicating the main thread is in the process of killing
         *                     the subprocess; IOExceptions observed while this flag is set are
         *                     logged at debug level rather than surfaced as ExecutionExceptions
         */
        public BoundedStreamReader(final InputStream stream, final Charset charset, final long limit, final String streamName,
                final AtomicBoolean overflowFlag, final AtomicBoolean shuttingDown) {
            this.stream = stream;
            this.charset = charset;
            this.limit = limit;
            this.streamName = streamName;
            this.overflowFlag = overflowFlag;
            this.shuttingDown = shuttingDown;
        }

        @Override
        public String call() throws IOException {
            final byte[] buf = new byte[8192];
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            long total = 0L;
            try (InputStream is = stream) {
                int n;
                while ((n = is.read(buf)) != -1) {
                    total += n;
                    if (total > limit) {
                        // Signal the main thread's polling loop before throwing so it can
                        // break immediately and kill the process tree rather than blocking
                        // for the remainder of executionTimeout.
                        overflowFlag.set(true);
                        throw new OutputSizeExceededException(
                                "command output exceeded limit on " + streamName + ": limit=" + limit + " bytes");
                    }
                    baos.write(buf, 0, n);
                }
            } catch (final OutputSizeExceededException e) {
                throw e;
            } catch (final IOException ioe) {
                if (shuttingDown.get()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("stream closed during shutdown: stream={}", streamName, ioe);
                    }
                } else {
                    logger.warn("unexpected I/O error on stream: stream={} drainedBytes={}", streamName, total, ioe);
                    throw ioe;
                }
            }
            return new String(baos.toByteArray(), charset);
        }
    }

    /**
     * Internal signal that the subprocess produced more output than allowed.
     */
    protected static class OutputSizeExceededException extends IOException {
        private static final long serialVersionUID = 1L;

        /**
         * Constructs a new OutputSizeExceededException.
         *
         * @param message the detail message
         */
        public OutputSizeExceededException(final String message) {
            super(message);
        }
    }

    /**
     * Copies bytes from an {@link InputStream} to a {@link File} up to {@code limit} bytes.
     * When the limit is exceeded an {@link OutputSizeExceededException} is thrown so that the
     * caller can invoke {@link CommandExtractor#destroyProcessTree(Process)} to terminate the
     * subprocess and its descendants. The return value is always an empty string; the written
     * content is in the output file.
     */
    protected static class BoundedFileWriter implements Callable<String> {
        private final InputStream stream;
        private final File outputFile;
        private final long limit;
        private final String streamName;
        private final AtomicBoolean overflowFlag;
        private final AtomicBoolean shuttingDown;

        /**
         * Constructs a new BoundedFileWriter.
         *
         * @param stream the input stream to drain
         * @param outputFile the file to write output to
         * @param limit the maximum number of bytes accepted before the process is killed
         * @param streamName a label used in error messages and logs
         * @param overflowFlag shared flag set to {@code true} just before
         *                     {@link OutputSizeExceededException} is thrown so the main thread's
         *                     polling loop breaks out early instead of blocking until executionTimeout
         * @param shuttingDown shared flag indicating the main thread is in the process of killing
         *                     the subprocess; IOExceptions observed while this flag is set are
         *                     logged at debug level rather than surfaced as ExecutionExceptions
         */
        public BoundedFileWriter(final InputStream stream, final File outputFile, final long limit, final String streamName,
                final AtomicBoolean overflowFlag, final AtomicBoolean shuttingDown) {
            this.stream = stream;
            this.outputFile = outputFile;
            this.limit = limit;
            this.streamName = streamName;
            this.overflowFlag = overflowFlag;
            this.shuttingDown = shuttingDown;
        }

        @Override
        public String call() throws IOException {
            final byte[] buf = new byte[8192];
            long total = 0L;
            try (InputStream is = stream; BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                int n;
                while ((n = is.read(buf)) != -1) {
                    total += n;
                    if (total > limit) {
                        overflowFlag.set(true);
                        throw new OutputSizeExceededException(
                                "command output exceeded limit on " + streamName + ": limit=" + limit + " bytes");
                    }
                    out.write(buf, 0, n);
                }
            } catch (final OutputSizeExceededException e) {
                throw e;
            } catch (final IOException ioe) {
                if (shuttingDown.get()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("stream closed during shutdown: stream={}", streamName, ioe);
                    }
                } else {
                    logger.warn("unexpected I/O error on stream: stream={} drainedBytes={}", streamName, total, ioe);
                    throw ioe;
                }
            }
            return "";
        }
    }

    /**
     * Legacy thread that used to monitor and terminate processes exceeding the
     * timeout.
     *
     * @deprecated The timeout/kill machinery is now handled inline by
     *             {@link CommandExtractor#executeCommand(File, File)} using
     *             {@link Process#waitFor(long, TimeUnit)} and
     *             {@link CommandExtractor#destroyProcessTree(Process)}. This class is
     *             unused internally and is retained only as an empty stub so that
     *             existing third-party subclasses or callers that referenced
     *             {@code CommandExtractor.MonitorThread} continue to compile and
     *             link. New code should not extend or instantiate it.
     */
    @Deprecated
    protected static class MonitorThread extends Thread {

        /**
         * Constructs a new MonitorThread.
         *
         * @param process the process to monitor (ignored; retained for source compat)
         * @param timeout the timeout (ignored; retained for source compat)
         */
        @Deprecated
        public MonitorThread(final Process process, final long timeout) {
            // NOP - retained only for source compatibility.
        }

        /**
         * No-op stub. Subclasses cannot override this — it is intentionally final to make
         * legacy override attempts compile-fail (rather than silently no-op).
         */
        @Deprecated
        @Override
        public final void run() {
            // NOP
        }

        /**
         * Sets the finished flag.
         *
         * <p>Subclasses cannot override this — it is intentionally final to make legacy
         * override attempts compile-fail (rather than silently no-op).
         *
         * @param finished the finished flag (ignored)
         */
        @Deprecated
        public final void setFinished(final boolean finished) {
            // NOP - retained only for source compatibility.
        }

        /**
         * Returns whether the process was terminated.
         *
         * <p>Subclasses cannot override this — it is intentionally final to make legacy
         * override attempts compile-fail (rather than silently no-op).
         *
         * <p>Note: the typo is preserved intentionally for binary compatibility with the
         * legacy API.
         *
         * @return always {@code false}; this stub never terminates anything.
         */
        @Deprecated
        public final boolean isTeminated() {
            return false;
        }
    }

    /**
     * Legacy thread that used to read and buffer output from an input stream.
     *
     * @deprecated Stream draining is now performed by
     *             {@link CommandExtractor.BoundedStreamReader} which is byte-bounded
     *             rather than line-bounded. This class is unused internally and is
     *             retained only as an empty stub so that existing third-party
     *             subclasses or callers that referenced
     *             {@code CommandExtractor.InputStreamThread} continue to compile and
     *             link. New code should not extend or instantiate it.
     */
    @Deprecated
    protected static class InputStreamThread extends Thread {

        /**
         * Constructs a new InputStreamThread.
         *
         * @param is the input stream (ignored; retained for source compat)
         * @param charset the charset (ignored; retained for source compat)
         * @param maxOutputLineBuffer the line buffer size (ignored; retained for source compat)
         */
        @Deprecated
        public InputStreamThread(final InputStream is, final String charset, final int maxOutputLineBuffer) {
            // NOP - retained only for source compatibility.
        }

        /**
         * No-op stub. Subclasses cannot override this — it is intentionally final to make
         * legacy override attempts compile-fail (rather than silently no-op).
         */
        @Deprecated
        @Override
        public final void run() {
            // NOP
        }

        /**
         * Returns the buffered output as a String.
         *
         * <p>Subclasses cannot override this — it is intentionally final to make legacy
         * override attempts compile-fail (rather than silently no-op).
         *
         * @return always an empty string; this stub never reads anything.
         */
        @Deprecated
        public final String getOutput() {
            return "";
        }
    }

    /**
     * Sets the encoding for the output.
     * @param outputEncoding The output encoding to set.
     */
    public void setOutputEncoding(final String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    /**
     * Sets the output file extension.
     * @param outputExtension The output file extension to set.
     */
    public void setOutputExtension(final String outputExtension) {
        this.outputExtension = outputExtension;
    }

    /**
     * Sets the temporary directory for file operations.
     * @param tempDir The temporary directory to set.
     */
    public void setTempDir(final File tempDir) {
        this.tempDir = tempDir;
    }

    /**
     * Sets the command to execute for text extraction.
     * @param command The command to set.
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    /**
     * Sets the timeout for command execution.
     * @param executionTimeout The execution timeout in milliseconds.
     */
    public void setExecutionTimeout(final long executionTimeout) {
        this.executionTimeout = executionTimeout;
    }

    /**
     * Sets the working directory for the command.
     * @param workingDirectory The working directory.
     */
    public void setWorkingDirectory(final File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /**
     * Sets the encoding for command output.
     * @param commandOutputEncoding The command output encoding to set.
     */
    public void setCommandOutputEncoding(final String commandOutputEncoding) {
        this.commandOutputEncoding = commandOutputEncoding;
    }

    /**
     * Sets the maximum number of output lines to buffer.
     *
     * <p>This setter is deprecated; use {@link #setMaxOutputSize(long)} instead.
     * For backward compatibility, calling this method conservatively shrinks
     * {@link #maxOutputSize} to {@code max(64 KiB, n * 1024)} bytes (1 line ≈ 1 KiB),
     * but only when {@link #setMaxOutputSize(long)} has <em>not</em> been called
     * explicitly on this instance. When both setters are called, the explicit
     * {@link #setMaxOutputSize(long)} value always wins regardless of call order.
     *
     * @param maxOutputLine The maximum output lines to set.
     * @deprecated Use {@link #setMaxOutputSize(long)} to set the byte-count bound directly.
     */
    @Deprecated
    public void setMaxOutputLine(final int maxOutputLine) {
        this.maxOutputLine = maxOutputLine;
        final long inferred = Math.max(64L * 1024L, (long) maxOutputLine * 1024L);
        if (!maxOutputSizeExplicit) {
            this.maxOutputSize = Math.min(this.maxOutputSize, inferred);
        }
    }

    /**
     * Sets whether to redirect standard output.
     * @param standardOutput true to redirect standard output, false otherwise.
     */
    public void setStandardOutput(final boolean standardOutput) {
        this.standardOutput = standardOutput;
    }

    /**
     * Sets the maximum number of bytes the subprocess may write to a single stream
     * (stdout or stderr) before the process is killed. Calling this method marks the
     * size as explicitly set, preventing a subsequent {@link #setMaxOutputLine(int)}
     * call from overriding it.
     *
     * <p>Note: the entire output may be loaded into memory during extraction
     * (transient 2–4× heap usage). Values above 512 MiB log a warning.
     *
     * @param maxOutputSize the limit in bytes
     */
    public void setMaxOutputSize(final long maxOutputSize) {
        if (maxOutputSize > 512L * 1024L * 1024L) {
            logger.warn("maxOutputSize is large; extraction may transiently allocate 2-4x this in heap: maxOutputSize={}", maxOutputSize);
        }
        this.maxOutputSize = maxOutputSize;
        this.maxOutputSizeExplicit = true;
    }

    /**
     * Returns the current maximum output size limit in bytes.
     *
     * @return the maximum number of bytes the subprocess may write to a single stream
     */
    public long getMaxOutputSize() {
        return maxOutputSize;
    }

    /**
     * Sets the maximum number of bytes copied from the input stream into the
     * temporary input file. If the input exceeds this size an {@link ExtractException}
     * is thrown before the command is invoked.
     *
     * @param maxInputSize the limit in bytes
     */
    public void setMaxInputSize(final long maxInputSize) {
        this.maxInputSize = maxInputSize;
    }

    /**
     * Sets the grace period in milliseconds between {@code Process.destroy()} (SIGTERM)
     * and the fallback {@code Process.destroyForcibly()} (SIGKILL).
     *
     * @param destroyGracePeriodMillis the grace period
     */
    public void setDestroyGracePeriodMillis(final long destroyGracePeriodMillis) {
        this.destroyGracePeriodMillis = destroyGracePeriodMillis;
    }

    /**
     * Sets whether captured stderr text should be appended to the extracted content
     * when {@link #standardOutput} is {@code false}. Defaults to {@code false} to
     * match pre-3.x behavior where stderr was only logged, never included in
     * {@code ExtractData}.
     *
     * <p>When set to {@code true} and {@code standardOutput=false}, captured stderr
     * text is appended after the file content in the extracted body.
     *
     * @param includeStderrInOutput {@code true} to append stderr to the extracted
     *                              content; {@code false} to keep the file content only
     */
    public void setIncludeStderrInOutput(final boolean includeStderrInOutput) {
        this.includeStderrInOutput = includeStderrInOutput;
    }
}
