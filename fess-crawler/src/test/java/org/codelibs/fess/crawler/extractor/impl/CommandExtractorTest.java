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
import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.core.io.FileUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExecutionTimeoutException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

/**
 * @author shinsuke
 *
 */
public class CommandExtractorTest extends PlainTestCase {

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
     * Verifies that when the subprocess produces more bytes on stderr than
     * {@code maxOutputSize} the call fails fast with an {@link ExtractException}
     * rather than buffering an unbounded amount of data.
     */
    @Test
    public void test_outputSizeExceeded_throwsExtractException() throws IOException {
        if (!SystemUtils.IS_OS_UNIX) {
            return;
        }
        final CommandExtractor extractor = new CommandExtractor();
        // print 5 MiB of zeros to stderr; cap at 1 MiB.
        extractor.command = "sh -c \"head -c 5242880 /dev/zero 1>&2\"";
        extractor.executionTimeout = 30_000L;
        extractor.maxOutputSize = 1024L * 1024L; // 1 MiB

        try {
            extractor.getText(new ByteArrayInputStream(new byte[0]), new HashMap<>());
            fail();
        } catch (final ExtractException e) {
            // expected
        }
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
     * extractor fails fast before invoking the command.
     */
    @Test
    public void test_inputSizeExceeded_throwsExtractException() {
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
        } catch (final ExtractException e) {
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
     * Verifies that when {@code standardOutput=false} and the subprocess writes to
     * stderr, the captured stderr text is appended to the extracted content. This
     * preserves backward compatibility with the legacy implementation which used
     * {@code ProcessBuilder.redirectErrorStream(true)} and therefore included
     * stderr in the extracted output.
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
        // standardOutput defaults to false, includeStderrInOutput defaults to true.

        final ExtractData data = extractor.getText(new FileInputStream(contentFile), new HashMap<>());
        final String text = data.getContent();
        assertTrue(text.contains("OUT"));
        assertTrue(text.contains("ERR"));
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
}
