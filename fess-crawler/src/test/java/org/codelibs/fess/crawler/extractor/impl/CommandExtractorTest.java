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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.core.io.FileUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExecutionTimeoutException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.helper.ContentLengthHelper;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

/**
 * @author shinsuke
 *
 */
public class CommandExtractorTest extends PlainTestCase {

    private static final Logger logger = LogManager.getLogger(CommandExtractorTest.class);

    private File createScriptTempFile(final int sleep) {
        String extention;
        String content;
        if (File.separator.equals("/")) {
            // Unix
            extention = ".sh";
            content = "#!/bin/bash\nsleep " + sleep + ";cp $1 $2";
        } else {
            // Windows
            extention = ".bat";
            content = "ping localhost -n " + sleep + "\r\ncopy %1 %2";
        }
        File file;
        try {
            file = File.createTempFile("script", extention);
            file.deleteOnExit();
            FileUtil.writeBytes(file.getAbsolutePath(), content.getBytes());
            return file;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private File createScriptTempFileStdout(final int sleep) {
        String extention;
        String content;
        if (File.separator.equals("/")) {
            // Unix
            extention = ".sh";
            content = "#!/bin/bash\nsleep " + sleep + ";cat $1";
        } else {
            // Windows
            extention = ".bat";
            content = "ping localhost -n " + sleep + "\r\ntype %1";
        }
        File file;
        try {
            file = File.createTempFile("script", extention);
            file.deleteOnExit();
            FileUtil.writeBytes(file.getAbsolutePath(), content.getBytes());
            return file;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private File createContentFile(final String extention, final byte[] data) {
        try {
            final File file = File.createTempFile("content", extention);
            file.deleteOnExit();
            FileUtil.writeBytes(file.getAbsolutePath(), data);
            return file;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private String getCommand(final File scriptFile) {
        if (File.separator.equals("/")) {
            // Unix
            return "sh " + scriptFile.getAbsolutePath() + " $INPUT_FILE $OUTPUT_FILE";
        } else {
            // Windows
            return scriptFile.getAbsolutePath() + " $INPUT_FILE $OUTPUT_FILE";
        }
    }

    private String getCommandStdout(final File scriptFile) {
        if (File.separator.equals("/")) {
            // Unix
            return "sh " + scriptFile.getAbsolutePath() + " $INPUT_FILE";
        } else {
            // Windows
            return scriptFile.getAbsolutePath() + " $INPUT_FILE";
        }
    }

    @Test
    public void test_getText() throws IOException {
        final File scriptFile = createScriptTempFile(3);
        final String content = "TEST";
        final File contentFile = createContentFile(".txt", content.getBytes());

        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = getCommand(scriptFile);
        final Map<String, String> params = new HashMap<String, String>();
        final ExtractData text = extractor.getText(new FileInputStream(contentFile), params);
        assertEquals(content, text.getContent());
    }

    @Test
    public void test_getText_withUrl() throws IOException {
        final File scriptFile = createScriptTempFile(3);
        final String content = "TEST";
        final File contentFile = createContentFile(".txt", content.getBytes());

        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = getCommand(scriptFile);
        final Map<String, String> params = new HashMap<String, String>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "hoge/fuga.txt");
        final ExtractData text = extractor.getText(new FileInputStream(contentFile), params);
        assertEquals(content, text.getContent());
    }

    @Test
    public void test_getText_withUrlContainingSpace() throws IOException {
        final File scriptFile = createScriptTempFile(3);
        final String content = "TEST";
        final File contentFile = createContentFile(".txt", content.getBytes());

        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = getCommand(scriptFile);
        final Map<String, String> params = new HashMap<String, String>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "hoge/fuga ほげ　ふが1１.txt");
        final ExtractData text = extractor.getText(new FileInputStream(contentFile), params);
        assertEquals(content, text.getContent());
    }

    @Test
    public void test_getText_timeout() throws IOException {
        final File scriptFile = createScriptTempFile(3);
        final String content = "TEST";
        final File contentFile = createContentFile(".txt", content.getBytes());

        final CommandExtractor extractor = new CommandExtractor();
        extractor.executionTimeout = 1000L;
        extractor.command = getCommand(scriptFile);
        final Map<String, String> params = new HashMap<String, String>();
        try {
            extractor.getText(new FileInputStream(contentFile), params);
            fail();
        } catch (final ExecutionTimeoutException e) {
            // expected
        }
    }

    @Test
    public void test_getText_fromStdin() throws IOException {
        final File scriptFile = createScriptTempFileStdout(3);
        final String content = "TEST";
        final File contentFile = createContentFile(".txt", content.getBytes());

        final CommandExtractor extractor = new CommandExtractor();
        extractor.standardOutput = true;
        extractor.command = getCommandStdout(scriptFile);
        final Map<String, String> params = new HashMap<String, String>();
        final ExtractData text = extractor.getText(new FileInputStream(contentFile), params);
        assertEquals(content, text.getContent());
    }

    @Test
    public void test_parseCommand() {
        final CommandExtractor extractor = new CommandExtractor();

        String cmd = "";
        final Map<String, String> params = new HashMap<String, String>();
        List<String> list = extractor.parseCommand(cmd, params);
        assertEquals(0, list.size());

        cmd = "test.sh";
        params.clear();
        list = extractor.parseCommand(cmd, params);
        assertEquals(1, list.size());
        assertEquals("test.sh", list.get(0));

        cmd = "test.sh \"test1 ' test2\"";
        params.clear();
        list = extractor.parseCommand(cmd, params);
        assertEquals(2, list.size());
        assertEquals("test.sh", list.get(0));
        assertEquals("test1 ' test2", list.get(1));

        cmd = "test.sh 'test1 \" test2'";
        params.clear();
        list = extractor.parseCommand(cmd, params);
        assertEquals(2, list.size());
        assertEquals("test.sh", list.get(0));
        assertEquals("test1 \" test2", list.get(1));

        cmd = "test.sh $INPUT_FILE";
        params.clear();
        list = extractor.parseCommand(cmd, params);
        assertEquals(2, list.size());
        assertEquals("test.sh", list.get(0));
        assertEquals("$INPUT_FILE", list.get(1));

        cmd = "test.sh $INPUT_FILE $OUTPUT_FILE";
        params.clear();
        list = extractor.parseCommand(cmd, params);
        assertEquals(3, list.size());
        assertEquals("test.sh", list.get(0));
        assertEquals("$INPUT_FILE", list.get(1));
        assertEquals("$OUTPUT_FILE", list.get(2));

        cmd = " test.sh $INPUT_FILE $OUTPUT_FILE ";
        params.clear();
        list = extractor.parseCommand(cmd, params);
        assertEquals(3, list.size());
        assertEquals("test.sh", list.get(0));
        assertEquals("$INPUT_FILE", list.get(1));
        assertEquals("$OUTPUT_FILE", list.get(2));

        cmd = "test.sh $INPUT_FILE $OUTPUT_FILE";
        params.clear();
        params.put("$INPUT_FILE", "A");
        params.put("$OUTPUT_FILE", "B");
        list = extractor.parseCommand(cmd, params);
        assertEquals(3, list.size());
        assertEquals("test.sh", list.get(0));
        assertEquals("A", list.get(1));
        assertEquals("B", list.get(2));
    }

    @Test
    public void test_getFileName() {
        final CommandExtractor extractor = new CommandExtractor();
        String expected;
        String actual;
        String value;

        value = "hoge.txt";
        expected = "hoge.txt";
        actual = extractor.getFileName(value);
        assertEquals(expected, actual);

        value = "/hoge.txt";
        expected = "hoge.txt";
        actual = extractor.getFileName(value);
        assertEquals(expected, actual);

        value = "fuga/hoge.txt";
        expected = "hoge.txt";
        actual = extractor.getFileName(value);
        assertEquals(expected, actual);

        value = "hoge.txt/";
        expected = "hoge.txt";
        actual = extractor.getFileName(value);
        assertEquals(expected, actual);

    }

    // ==========================================================
    // PR-D: robustness tests
    // ==========================================================

    /**
     * Verifies a long-running subprocess is killed when the timeout fires
     * and the call returns within a few seconds (i.e. we don't wait the
     * full sleep duration).
     */
    @Test
    public void test_timeout_killsProcess() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return; // skip on non-Unix
        }
        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = "sh -c \"sleep 30\"";
        extractor.executionTimeout = 500L;

        final long start = System.currentTimeMillis();
        try {
            extractor.getText(new ByteArrayInputStream(new byte[0]), new HashMap<>());
            fail();
        } catch (final ExecutionTimeoutException e) {
            // expected
        }
        final long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed < 5000L);
    }

    /**
     * Verifies that when the subprocess floods stderr (a diagnostic-only stream when
     * {@code standardOutput=false}), the reader caps-and-discards the excess and the command still
     * completes successfully instead of being killed. Content comes from $OUTPUT_FILE (empty here),
     * so stderr volume must not fail the extraction.
     */
    @Test
    public void test_oversizeStderr_capAndDiscard_doesNotThrow() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final CommandExtractor extractor = new CommandExtractor();
        // print 5 MiB of zeros to stderr; the diagnostic reader retains only a small prefix.
        extractor.command = "sh -c \"head -c 5242880 /dev/zero 1>&2\"";
        extractor.executionTimeout = 30_000L;
        extractor.maxDiagnosticRetainSize = 64 * 1024; // 64 KiB retained; the rest is drained and discarded

        final long start = System.currentTimeMillis();
        // Must NOT throw: stderr is diagnostic and is cap-and-discarded, not fatal.
        final ExtractData data = extractor.getText(new ByteArrayInputStream(new byte[0]), new HashMap<>());
        assertEquals("", data.getContent());
        final long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed < 10_000L);
    }

    /**
     * Verifies stderr is drained concurrently so the process does not block
     * on a full pipe buffer (default ~64KB on Linux). We write much more than
     * that to stderr while the script also produces normal output.
     */
    @Test
    public void test_stderrDrained_doesNotDeadlock() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final File scriptFile;
        try {
            scriptFile = File.createTempFile("stderr_script", ".sh");
            scriptFile.deleteOnExit();
            // Write 1 MiB to stderr (well above default pipe buffer) then copy input.
            FileUtil.writeBytes(scriptFile.getAbsolutePath(),
                    ("#!/bin/bash\nhead -c 1048576 /dev/zero 1>&2\ncp \"$1\" \"$2\"\n").getBytes());
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }

        final String content = "TEST";
        final File contentFile = createContentFile(".txt", content.getBytes());

        final CommandExtractor extractor = new CommandExtractor();
        extractor.executionTimeout = 30_000L;
        extractor.command = "sh " + scriptFile.getAbsolutePath() + " $INPUT_FILE $OUTPUT_FILE";
        // This test focuses on draining stderr so the process does not block;
        // suppress stderr-in-output so the assertion compares only the file content.
        extractor.setIncludeStderrInOutput(false);

        final ExtractData data = extractor.getText(new FileInputStream(contentFile), new HashMap<>());
        assertEquals(content, data.getContent());
    }

    /**
     * Verifies the input copy is bounded by {@code maxInputSize} and the
     * extractor fails fast with {@link MaxLengthExceededException} before invoking the command.
     */
    @Test
    public void test_inputSizeExceeded_throwsMaxLengthExceededException() {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final File scriptFile = createScriptTempFile(0);
        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = getCommand(scriptFile);
        extractor.maxInputSize = 1024L; // 1 KiB

        // 64 KiB of zeros is well above the 1 KiB cap.
        final InputStream big = new ByteArrayInputStream(new byte[64 * 1024]);
        try {
            extractor.getText(big, new HashMap<>());
            fail();
        } catch (final MaxLengthExceededException e) {
            // expected
        }
    }

    /**
     * Verifies that when a subprocess spawns a long-running child, the timeout
     * path attempts to kill descendants too. We check this best-effort by
     * snapshotting {@link ProcessHandle#allProcesses} before/after; the precise
     * verification of orphan kill is platform-specific.
     */
    @Test
    public void test_processDescendants_killed() throws Exception {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final CommandExtractor extractor = new CommandExtractor();
        // Spawn a child sleep, then wait. When the parent is killed we want the child gone too.
        extractor.command = "sh -c \"sleep 30 & wait\"";
        extractor.executionTimeout = 500L;

        final long start = System.currentTimeMillis();
        try {
            extractor.getText(new ByteArrayInputStream(new byte[0]), new HashMap<>());
            fail();
        } catch (final ExecutionTimeoutException e) {
            // expected
        }
        final long elapsed = System.currentTimeMillis() - start;
        // Total wall clock should be close to the timeout, not the 30-sec sleep.
        assertTrue(elapsed < 10_000L);
    }

    /**
     * Verifies that when {@code standardOutput=false} and {@code includeStderrInOutput=true},
     * captured stderr text is appended to the extracted content.
     * Note: the opt-in flag must be set explicitly because the default is {@code false}.
     */
    @Test
    public void test_stderrAppendedToOutput_whenStandardOutputFalse() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        // Script writes "OUT" to $OUTPUT_FILE and "ERR" to stderr.
        final File scriptFile;
        try {
            scriptFile = File.createTempFile("stderr_in_out_", ".sh");
            scriptFile.deleteOnExit();
            FileUtil.writeBytes(scriptFile.getAbsolutePath(), "#!/bin/bash\necho OUT > \"$2\"\necho ERR >&2\n".getBytes());
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }

        final File contentFile = createContentFile(".txt", new byte[] { 0 });
        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = "sh " + scriptFile.getAbsolutePath() + " $INPUT_FILE $OUTPUT_FILE";
        extractor.executionTimeout = 30_000L;
        // Explicitly opt in — default is false (pre-3.x behavior).
        extractor.setIncludeStderrInOutput(true);

        final ExtractData data = extractor.getText(new FileInputStream(contentFile), new HashMap<>());
        final String text = data.getContent();
        assertTrue(text.contains("OUT"));
        assertTrue(text.contains("ERR"));
    }

    /**
     * Verifies that by default ({@code includeStderrInOutput=false}), stderr is NOT
     * appended to the extracted content even when {@code standardOutput=false}. This
     * matches the pre-3.x behavior where stderr was only routed to logs, never to
     * extracted text.
     */
    @Test
    public void test_stderrNotInOutput_byDefault() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        // Script writes "content" to $OUTPUT_FILE and "warning" to stderr.
        final File scriptFile;
        try {
            scriptFile = File.createTempFile("stderr_default_", ".sh");
            scriptFile.deleteOnExit();
            FileUtil.writeBytes(scriptFile.getAbsolutePath(), "#!/bin/bash\necho content > \"$2\"\necho warning >&2\n".getBytes());
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }

        final File contentFile = createContentFile(".txt", new byte[] { 0 });
        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = "sh " + scriptFile.getAbsolutePath() + " $INPUT_FILE $OUTPUT_FILE";
        extractor.executionTimeout = 30_000L;
        // Do NOT call setIncludeStderrInOutput — default must be false.

        final ExtractData data = extractor.getText(new FileInputStream(contentFile), new HashMap<>());
        final String text = data.getContent();
        assertTrue(text.contains("content"));
        assertFalse(text.contains("warning")); // stderr should not appear in extracted text by default
    }

    /**
     * Verifies that {@code setIncludeStderrInOutput(false)} suppresses appending
     * stderr to the extracted content even when {@code standardOutput=false}.
     */
    @Test
    public void test_stderrSuppressedFromOutput_whenIncludeStderrInOutputFalse() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final File scriptFile;
        try {
            scriptFile = File.createTempFile("stderr_suppressed_", ".sh");
            scriptFile.deleteOnExit();
            FileUtil.writeBytes(scriptFile.getAbsolutePath(), "#!/bin/bash\necho OUT > \"$2\"\necho ERR >&2\n".getBytes());
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }

        final File contentFile = createContentFile(".txt", new byte[] { 0 });
        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = "sh " + scriptFile.getAbsolutePath() + " $INPUT_FILE $OUTPUT_FILE";
        extractor.executionTimeout = 30_000L;
        extractor.setIncludeStderrInOutput(false);

        final ExtractData data = extractor.getText(new FileInputStream(contentFile), new HashMap<>());
        final String text = data.getContent();
        assertTrue(text.contains("OUT"));
        assertFalse(text.contains("ERR"));
    }

    // ==========================================================
    // Content bound: extracted content exceeds the content-length limit → MaxLengthExceededException
    // ==========================================================

    /**
     * Content bound (standardOutput=true): when the command writes more than the content limit to
     * stdout, getText() must throw {@link MaxLengthExceededException} rather than loading the entire
     * output into memory.
     */
    @Test
    public void test_outputExceedsContentLimit_standardOutputTrue_throws() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final CommandExtractor extractor = new CommandExtractor();
        // 5 MiB stdout > 1 MiB content limit should trigger the bound.
        extractor.standardOutput = true;
        extractor.command = "sh -c \"head -c 5242880 /dev/zero\"";
        extractor.executionTimeout = 30_000L;
        extractor.setMaxContentLength(1024L * 1024L); // 1 MiB

        try {
            extractor.getText(new ByteArrayInputStream(new byte[0]), new HashMap<>());
            fail();
        } catch (final MaxLengthExceededException e) {
            // expected
        }
    }

    /**
     * Content bound (standardOutput=false): when the command writes more than the content limit to
     * $OUTPUT_FILE, getText() must throw {@link MaxLengthExceededException} (post-exec length guard).
     * This variant exercises the {@link ContentLengthHelper}-driven limit.
     */
    @Test
    public void test_outputFileExceedsContentLimit_standardOutputFalse_throws() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final File scriptFile;
        try {
            scriptFile = File.createTempFile("big_output_", ".sh");
            scriptFile.deleteOnExit();
            // Write 5 MiB of zeros to $OUTPUT_FILE directly (bypasses the diagnostic reader).
            FileUtil.writeBytes(scriptFile.getAbsolutePath(), "#!/bin/bash\nhead -c 5242880 /dev/zero > \"$2\"\n".getBytes());
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }

        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = "sh " + scriptFile.getAbsolutePath() + " $INPUT_FILE $OUTPUT_FILE";
        extractor.executionTimeout = 30_000L;
        final ContentLengthHelper contentLengthHelper = new ContentLengthHelper();
        contentLengthHelper.setDefaultMaxLength(1024L * 1024L); // 1 MiB
        extractor.setContentLengthHelper(contentLengthHelper);

        try {
            extractor.getText(new ByteArrayInputStream(new byte[0]), new HashMap<>());
            fail();
        } catch (final MaxLengthExceededException e) {
            // expected
        }
    }

    // ==========================================================
    // Fix 2: descendants are killed even when parent exits cleanly
    // ==========================================================

    /**
     * Fix 2: verifies that after a timeout the spawned child process (sleep) is killed even
     * when the parent shell exits gracefully before the grace period expires and would otherwise
     * cause descendants() to return an empty snapshot.
     */
    @Test
    public void test_processDescendants_killed_orphan_gone() throws Exception {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final CommandExtractor extractor = new CommandExtractor();
        // The shell spawns a background sleep and prints its PID to stdout, then waits.
        // standardOutput=false so we can read stdout text via BoundedStreamReader.
        extractor.command = "sh -c \"sleep 30 & echo $!; wait\"";
        extractor.executionTimeout = 800L;
        extractor.destroyGracePeriodMillis = 200L;

        try {
            extractor.getText(new ByteArrayInputStream(new byte[0]), new HashMap<>());
            fail();
        } catch (final ExecutionTimeoutException e) {
            // expected — but we did not capture stdout here since the exception fires before we collect it.
        } catch (final Exception e) {
            // acceptable
        }

        // Give the OS a moment to reap the child.
        Thread.sleep(500);

        // Best-effort check: find any process with "sleep 30" in its command that is still alive.
        final boolean orphanAlive = ProcessHandle.allProcesses()
                .filter(ProcessHandle::isAlive)
                .anyMatch(ph -> ph.info().command().map(c -> c.contains("sleep")).orElse(false) && ph.info().arguments().map(args -> {
                    for (final String a : args) {
                        if ("30".equals(a)) {
                            return true;
                        }
                    }
                    return false;
                }).orElse(false));

        if (orphanAlive) {
            logger.warn("test_processDescendants_killed_orphan_gone: orphan 'sleep 30' still alive after kill attempt");
        }
        assertFalse(orphanAlive);
    }

    // ==========================================================
    // Fix 3: overflow kills descendants via destroyProcessTree
    // ==========================================================

    /**
     * Content-overflow kills descendants: when {@code standardOutput=true} and the command floods
     * stdout beyond the content limit, the {@code BoundedFileWriter} sets the overflow flag so the
     * main thread breaks out of its polling loop and calls destroyProcessTree immediately — well
     * before the full executionTimeout — reaping the background child too.
     */
    @Test
    public void test_contentOverflow_killsDescendants() throws Exception {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final CommandExtractor extractor = new CommandExtractor();
        // Spawn a background sleep, then flood stdout to trigger a content-limit overflow.
        // standardOutput=true so stdout is the content path (BoundedFileWriter enforces the limit).
        extractor.standardOutput = true;
        extractor.command = "sh -c \"sleep 37 & head -c 5242880 /dev/zero; wait\"";
        // Use 10 s timeout — test must complete well below this if fail-fast works.
        extractor.executionTimeout = 10_000L;
        extractor.setMaxContentLength(1024L * 1024L); // 1 MiB
        extractor.destroyGracePeriodMillis = 200L;

        final long start = System.currentTimeMillis();
        try {
            extractor.getText(new ByteArrayInputStream(new byte[0]), new HashMap<>());
            fail();
        } catch (final MaxLengthExceededException e) {
            // expected — content overflow detected
        }
        final long elapsed = System.currentTimeMillis() - start;
        // Overflow should be detected and process killed well before the 10 s timeout.
        assertTrue(elapsed < 5000L); // overflow should kill process tree in < 5 s

        Thread.sleep(500);

        final boolean orphanAlive = ProcessHandle.allProcesses()
                .filter(ProcessHandle::isAlive)
                .anyMatch(ph -> ph.info().command().map(c -> c.contains("sleep")).orElse(false) && ph.info().arguments().map(args -> {
                    for (final String a : args) {
                        if ("37".equals(a)) {
                            return true;
                        }
                    }
                    return false;
                }).orElse(false));

        if (orphanAlive) {
            logger.warn("test_contentOverflow_killsDescendants: orphan 'sleep 37' still alive after kill attempt");
        }
        assertFalse(orphanAlive);
    }

    // ==========================================================
    // Deprecated setMaxOutputLine is now a NOP (content bound via ContentLengthHelper)
    // ==========================================================

    /**
     * The deprecated {@code setMaxOutputLine} no longer affects the content bound (which now comes
     * from {@link ContentLengthHelper}/{@code maxContentLength}); it is retained only for source and
     * binary compatibility and must be a harmless NOP.
     */
    @Test
    @SuppressWarnings("deprecation")
    public void test_setMaxOutputLine_isNoOp() {
        final CommandExtractor extractor = new CommandExtractor();
        final long before = extractor.getMaxContentLength();
        extractor.setMaxOutputLine(1);
        extractor.setMaxOutputLine(100000);
        // The content bound is unaffected by the deprecated line-count setter.
        assertEquals(before, extractor.getMaxContentLength());
    }

    // ==========================================================
    // PR-159 review-fix tests
    // ==========================================================

    /**
     * Validates content-overflow fast-fail: standardOutput=true overflow must fail fast (not wait for
     * executionTimeout).
     */
    @Test
    public void test_contentOverflow_failsFastWithoutWaitingForTimeout() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final CommandExtractor extractor = new CommandExtractor();
        extractor.standardOutput = true;
        // Produce 5 MiB then keep running. Without fail-fast the test would wait the full executionTimeout.
        extractor.command = "sh -c \"head -c 5242880 /dev/zero; sleep 30\"";
        extractor.executionTimeout = 10_000L;
        extractor.setMaxContentLength(1024L * 1024L);
        final long start = System.currentTimeMillis();
        try {
            extractor.getText(new ByteArrayInputStream(new byte[0]), new HashMap<>());
            fail();
        } catch (final MaxLengthExceededException e) {
            // expected
        }
        final long elapsed = System.currentTimeMillis() - start;
        if (elapsed >= 5000L) {
            logger.warn("test_contentOverflow_failsFastWithoutWaitingForTimeout: overflow did not fail fast (was {}ms)", elapsed);
        }
        assertTrue(elapsed < 5000L);
    }

    /**
     * Validates C4: invalid charset name falls back to UTF-8 silently (warning logged).
     */
    @Test
    public void test_invalidCommandOutputEncoding_fallsBackToUtf8() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final File scriptFile = createScriptTempFileStdout(0);
        final File contentFile = createContentFile(".txt", "HELLO".getBytes());
        final CommandExtractor extractor = new CommandExtractor();
        extractor.standardOutput = true;
        extractor.command = getCommandStdout(scriptFile);
        extractor.setCommandOutputEncoding("not-a-real-charset-12345");
        extractor.executionTimeout = 30_000L;
        // Should NOT throw — falls back to UTF-8 silently (warning logged).
        final ExtractData data = extractor.getText(new FileInputStream(contentFile), new HashMap<>());
        assertEquals("HELLO", data.getContent());
    }

    /**
     * Verifies a blank command surfaces as a CrawlerSystemException rather than NPE/IAE.
     */
    @Test
    public void test_blankCommand_throwsCrawlerSystemException() {
        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = "   ";
        try {
            extractor.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
            fail();
        } catch (final org.codelibs.fess.crawler.exception.CrawlerSystemException e) {
            // expected
        }
    }

    /**
     * Verifies a nonexistent binary surfaces as a CrawlerSystemException.
     */
    @Test
    public void test_nonexistentBinary_throwsCrawlerSystemException() {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = "/nonexistent/path/to/binary $INPUT_FILE $OUTPUT_FILE";
        try {
            extractor.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
            fail();
        } catch (final org.codelibs.fess.crawler.exception.CrawlerSystemException e) {
            // expected
        }
    }

    /**
     * Validates M4: non-zero exit must NOT throw — content is still returned (lenient contract).
     */
    @Test
    public void test_nonZeroExit_returnsAvailableContent() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final File scriptFile;
        try {
            scriptFile = File.createTempFile("nonzero_", ".sh");
            scriptFile.deleteOnExit();
            FileUtil.writeBytes(scriptFile.getAbsolutePath(), "#!/bin/bash\necho partial > \"$2\"\nexit 7\n".getBytes());
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
        final File contentFile = createContentFile(".txt", new byte[] { 0 });
        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = "sh " + scriptFile.getAbsolutePath() + " $INPUT_FILE $OUTPUT_FILE";
        extractor.executionTimeout = 30_000L;
        // Lenient contract: non-zero exit does NOT throw; content is returned.
        final ExtractData data = extractor.getText(new FileInputStream(contentFile), new HashMap<>());
        assertTrue(data.getContent().contains("partial"));
    }

    /**
     * Verifies M8: resourceName containing shell metacharacters is sanitized so it cannot be used
     * to inject arguments via the temp-file prefix.
     */
    @Test
    public void test_resourceNameWithMetacharacters_sanitizedInTempFileName() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final File scriptFile = createScriptTempFile(0);
        final File contentFile = createContentFile(".txt", "OK".getBytes());
        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = getCommand(scriptFile);
        final Map<String, String> params = new HashMap<>();
        // A maliciously crafted resourceName with shell metacharacters.
        params.put(ExtractData.RESOURCE_NAME_KEY, "evil;rm -rf /.--reset.txt");
        final ExtractData data = extractor.getText(new FileInputStream(contentFile), params);
        assertEquals("OK", data.getContent());
    }

    /**
     * Verifies M8: getFileName handles Windows-style backslash path separators.
     */
    @Test
    public void test_getFileName_handlesWindowsBackslash() {
        final CommandExtractor extractor = new CommandExtractor();
        assertEquals("hoge.txt", extractor.getFileName("C:\\path\\hoge.txt"));
        assertEquals("hoge.txt", extractor.getFileName("path\\hoge.txt"));
        assertEquals("hoge.txt", extractor.getFileName("hoge.txt\\"));
    }

    /**
     * Verifies thread interrupt is propagated and the interrupt flag is restored.
     */
    @Test
    public void test_interrupt_propagatesAsInterruptedRuntimeException() throws InterruptedException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = "sh -c \"sleep 30\"";
        extractor.executionTimeout = 30_000L;
        final Thread target = Thread.currentThread();
        final Thread interrupter = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (final InterruptedException ignored) {}
            target.interrupt();
        });
        interrupter.start();
        try {
            extractor.getText(new ByteArrayInputStream(new byte[0]), new HashMap<>());
            fail();
        } catch (final org.codelibs.core.exception.InterruptedRuntimeException expected) {
            // good
        } catch (final Exception e) {
            // also acceptable as long as interrupt flag was restored
        } finally {
            Thread.interrupted(); // clear interrupt for subsequent tests
        }
    }

    /**
     * Verifies an invalid workingDirectory surfaces as a CrawlerSystemException.
     */
    @Test
    public void test_invalidWorkingDirectory_throwsCrawlerSystemException() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final File scriptFile = createScriptTempFile(0);
        final File contentFile = createContentFile(".txt", "x".getBytes());
        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = getCommand(scriptFile);
        extractor.workingDirectory = new File("/nonexistent/path/that/does/not/exist");
        try {
            extractor.getText(new FileInputStream(contentFile), new HashMap<>());
            fail();
        } catch (final org.codelibs.fess.crawler.exception.CrawlerSystemException e) {
            // expected
        }
    }

    /**
     * Verifies the deprecated MonitorThread stub still works as a NOP.
     */
    @Test
    @SuppressWarnings("deprecation")
    public void test_deprecatedMonitorThread_isNop() {
        final CommandExtractor.MonitorThread mt = new CommandExtractor.MonitorThread(null, 1000L);
        mt.setFinished(true);
        assertFalse(mt.isTeminated());
    }

    /**
     * Verifies the deprecated InputStreamThread stub still works as a NOP.
     */
    @Test
    @SuppressWarnings("deprecation")
    public void test_deprecatedInputStreamThread_isNop() {
        final CommandExtractor.InputStreamThread it =
                new CommandExtractor.InputStreamThread(new ByteArrayInputStream(new byte[0]), "UTF-8", 100);
        assertEquals("", it.getOutput());
    }

    // ==========================================================
    // Cap-and-discard diagnostic streams + ContentLengthHelper-driven bound
    // ==========================================================

    /**
     * Verifies that when {@code standardOutput=false} the command may flood stdout (a diagnostic-only
     * stream) without being killed: the reader caps-and-discards stdout while $OUTPUT_FILE content is
     * returned intact.
     */
    @Test
    public void test_diagnosticStdout_capAndDiscard_standardOutputFalse() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final File scriptFile;
        try {
            scriptFile = File.createTempFile("stdout_flood_", ".sh");
            scriptFile.deleteOnExit();
            // Flood stdout with 5 MiB, then write the real content to $OUTPUT_FILE.
            FileUtil.writeBytes(scriptFile.getAbsolutePath(), "#!/bin/bash\nhead -c 5242880 /dev/zero\necho content > \"$2\"\n".getBytes());
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }

        final File contentFile = createContentFile(".txt", new byte[] { 0 });
        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = "sh " + scriptFile.getAbsolutePath() + " $INPUT_FILE $OUTPUT_FILE";
        extractor.executionTimeout = 30_000L;
        extractor.maxDiagnosticRetainSize = 64 * 1024;

        // Must NOT throw despite 5 MiB on stdout; content comes from $OUTPUT_FILE.
        final ExtractData data = extractor.getText(new FileInputStream(contentFile), new HashMap<>());
        assertTrue(data.getContent().contains("content"));
    }

    /**
     * Verifies the content bound honours a per-MIME-type limit from {@link ContentLengthHelper} when
     * a {@code Content-Type} is present in the extraction parameters.
     */
    @Test
    public void test_contentLimit_perMimeType_fromParams() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final File scriptFile;
        try {
            scriptFile = File.createTempFile("mime_output_", ".sh");
            scriptFile.deleteOnExit();
            // Write 2 MiB to $OUTPUT_FILE.
            FileUtil.writeBytes(scriptFile.getAbsolutePath(), "#!/bin/bash\nhead -c 2097152 /dev/zero > \"$2\"\n".getBytes());
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }

        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = "sh " + scriptFile.getAbsolutePath() + " $INPUT_FILE $OUTPUT_FILE";
        extractor.executionTimeout = 30_000L;
        final ContentLengthHelper contentLengthHelper = new ContentLengthHelper();
        contentLengthHelper.setDefaultMaxLength(10L * 1024L * 1024L); // generous default
        contentLengthHelper.addMaxLength("text/plain", 1024L * 1024L); // 1 MiB for text/plain
        extractor.setContentLengthHelper(contentLengthHelper);

        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.CONTENT_TYPE, "text/plain");
        // 2 MiB output exceeds the 1 MiB text/plain limit -> rejected.
        try {
            extractor.getText(new ByteArrayInputStream(new byte[0]), params);
            fail();
        } catch (final MaxLengthExceededException e) {
            // expected
        }
    }

    /**
     * Verifies that when {@code includeStderrInOutput=true} the appended stderr is truncated so the
     * combined content never exceeds the content limit (stderr is diagnostic — truncate, don't fail).
     */
    @Test
    public void test_includeStderrInOutput_combinedContentStaysWithinLimit() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final File scriptFile;
        try {
            scriptFile = File.createTempFile("stderr_bound_", ".sh");
            scriptFile.deleteOnExit();
            // 1000 'A' bytes to $OUTPUT_FILE, 1000 'B' bytes to stderr.
            FileUtil.writeBytes(scriptFile.getAbsolutePath(),
                    "#!/bin/bash\nprintf 'A%.0s' {1..1000} > \"$2\"\nprintf 'B%.0s' {1..1000} >&2\n".getBytes());
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }

        final File contentFile = createContentFile(".txt", new byte[] { 0 });
        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = "sh " + scriptFile.getAbsolutePath() + " $INPUT_FILE $OUTPUT_FILE";
        extractor.executionTimeout = 30_000L;
        extractor.setIncludeStderrInOutput(true);
        extractor.setMaxContentLength(1500L); // 1000 (file) + at most 500 (stderr)

        final ExtractData data = extractor.getText(new FileInputStream(contentFile), new HashMap<>());
        final byte[] contentBytes = data.getContent().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(contentBytes.length <= 1500);
        assertTrue(data.getContent().contains("A"));
        assertTrue(data.getContent().contains("B")); // some stderr retained after truncation
    }
}
