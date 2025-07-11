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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.exception.InterruptedRuntimeException;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.ThreadUtil;
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

    /** The maximum number of lines to buffer from command output. */
    protected int maxOutputLine = 1000;

    /** Whether to redirect standard output to a file. */
    protected boolean standardOutput = false;

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

            // store to a file
            CopyUtil.copy(in, inputFile);

            executeCommand(inputFile, outputFile);

            final ExtractData extractData = new ExtractData(new String(FileUtil.readBytes(outputFile), outputEncoding));
            if (StringUtil.isNotBlank(resourceName)) {
                extractData.putValues("resourceName", new String[] { resourceName });
            }

            return extractData;
        } catch (final IOException e) {
            throw new ExtractException("Could not extract a content.", e);
        } finally {
            FileUtil.deleteInBackground(inputFile);
            FileUtil.deleteInBackground(outputFile);
        }
    }

    String getFileName(final String resourceName) {
        final String name = resourceName.replaceAll("/+$", "");
        final int pos = name.lastIndexOf('/');
        if (pos >= 0) {
            return name.substring(pos + 1);
        }
        return name;
    }

    private void executeCommand(final File inputFile, final File outputFile) {

        if (StringUtil.isBlank(command)) {
            throw new CrawlerSystemException("command is empty.");
        }

        final Map<String, String> params = new HashMap<>();
        params.put("$INPUT_FILE", inputFile.getAbsolutePath());
        params.put("$OUTPUT_FILE", outputFile.getAbsolutePath());

        final List<String> cmdList = parseCommand(command, params);
        if (logger.isInfoEnabled()) {
            logger.info("Command: {}", cmdList);
        }

        final ProcessBuilder pb = new ProcessBuilder(cmdList);
        if (workingDirectory != null) {
            pb.directory(workingDirectory);
        }
        if (standardOutput) {
            pb.redirectOutput(outputFile);
        } else {
            pb.redirectErrorStream(true);
        }

        Process currentProcess = null;
        MonitorThread mt = null;
        try {
            currentProcess = pb.start();

            // monitoring
            mt = new MonitorThread(currentProcess, executionTimeout);
            mt.start();

            final InputStreamThread it = new InputStreamThread(currentProcess.getInputStream(), commandOutputEncoding, maxOutputLine);
            it.start();

            currentProcess.waitFor();
            it.join(5000);

            if (mt.isTeminated()) {
                throw new ExecutionTimeoutException("The command execution is timeout: " + cmdList);
            }

            final int exitValue = currentProcess.exitValue();

            if (logger.isInfoEnabled()) {
                if (standardOutput) {
                    logger.info("Exit Code: {}", exitValue);
                } else {
                    logger.info("Exit Code: {} - Process Output:\n{}", exitValue, it.getOutput());
                }
            }
            if (exitValue == 143 && mt.isTeminated()) {
                throw new ExecutionTimeoutException("The command execution is timeout: " + cmdList);
            }
        } catch (final CrawlerSystemException e) {
            throw e;
        } catch (final InterruptedException e) {
            if (mt != null && mt.isTeminated()) {
                throw new ExecutionTimeoutException("The command execution is timeout: " + cmdList, e);
            }
            throw new InterruptedRuntimeException(e);
        } catch (final Exception e) {
            throw new CrawlerSystemException("Process terminated.", e);
        } finally {
            if (mt != null) {
                mt.setFinished(true);
                try {
                    mt.interrupt();
                } catch (final Exception e) {}
            }
            if (currentProcess != null) {
                try {
                    currentProcess.destroy();
                } catch (final Exception e) {}
            }
            currentProcess = null;

        }
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
     * Thread to monitor and terminate processes that exceed the timeout.
     */
    protected static class MonitorThread extends Thread {
        private final Process process;

        private final long timeout;

        private boolean finished = false;

        private boolean teminated = false;

        /**
         * Constructs a new MonitorThread.
         * @param process The process to monitor.
         * @param timeout The timeout for the process.
         */
        public MonitorThread(final Process process, final long timeout) {
            this.process = process;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            ThreadUtil.sleepQuietly(timeout);

            if (!finished) {
                try {
                    process.destroy();
                    teminated = true;
                } catch (final Exception e) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Could not kill the subprocess.", e);
                    }
                }
            }
        }

        /**
         * Sets the finished flag.
         * @param finished The finished flag to set.
         */
        public void setFinished(final boolean finished) {
            this.finished = finished;
        }

        /**
         * Returns whether the process was terminated.
         * @return true if terminated, false otherwise.
         */
        public boolean isTeminated() {
            return teminated;
        }
    }

    /**
     * Thread to read and buffer output from an input stream.
     */
    protected static class InputStreamThread extends Thread {

        private BufferedReader br;

        private final List<String> list = new LinkedList<>();

        private final int maxLineBuffer;

        /**
         * Constructs a new InputStreamThread.
         * @param is The InputStream to read from.
         * @param charset The charset to use for reading.
         * @param maxOutputLineBuffer The maximum number of lines to buffer.
         */
        public InputStreamThread(final InputStream is, final String charset, final int maxOutputLineBuffer) {
            try {
                br = new BufferedReader(new InputStreamReader(is, charset));
            } catch (final UnsupportedEncodingException e) {
                br = new BufferedReader(new InputStreamReader(is, Constants.UTF_8_CHARSET));
            }
            maxLineBuffer = maxOutputLineBuffer;
        }

        @Override
        public void run() {
            for (;;) {
                try {
                    final String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug(line);
                    }
                    list.add(line);
                    if (list.size() > maxLineBuffer) {
                        list.remove(0);
                    }
                } catch (final IOException e) {
                    throw new CrawlerSystemException(e);
                }
            }
        }

        /**
         * Returns the output as a String.
         * @return The output string.
         */
        public String getOutput() {
            final StringBuilder buf = new StringBuilder(100);
            for (final String value : list) {
                buf.append(value).append("\n");
            }
            return buf.toString();
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
     * Sets the maximum number of output lines to process.
     * @param maxOutputLine The maximum output lines to set.
     */
    public void setMaxOutputLine(final int maxOutputLine) {
        this.maxOutputLine = maxOutputLine;
    }

    /**
     * Sets whether to redirect standard output.
     * @param standardOutput true to redirect standard output, false otherwise.
     */
    public void setStandardOutput(final boolean standardOutput) {
        this.standardOutput = standardOutput;
    }
}
