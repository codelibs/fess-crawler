/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.seasar.robot.extractor.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tika.metadata.TikaMetadataKeys;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.util.FileUtil;
import org.seasar.robot.entity.ExtractData;
import org.seasar.robot.extractor.ExecutionTimeoutException;

/**
 * @author shinsuke
 * 
 */
public class CommandExtractorTest extends S2TestCase {

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
            FileUtil.write(file.getAbsolutePath(), content.getBytes());
            return file;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private File createContentFile(final String extention, final byte[] data) {
        try {
            final File file = File.createTempFile("content", extention);
            file.deleteOnExit();
            FileUtil.write(file.getAbsolutePath(), data);
            return file;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private String getCommand(final File scriptFile) {
        if (File.separator.equals("/")) {
            // Unix
            return "sh " + scriptFile.getAbsolutePath()
                + " $INPUT_FILE $OUTPUT_FILE";
        } else {
            // Windows
            return scriptFile.getAbsolutePath() + " $INPUT_FILE $OUTPUT_FILE";
        }
    }

    public void test_getText() throws IOException {
        final File scriptFile = createScriptTempFile(3);
        final String content = "TEST";
        final File contentFile = createContentFile(".txt", content.getBytes());

        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = getCommand(scriptFile);
        final Map<String, String> params = new HashMap<String, String>();
        final ExtractData text =
            extractor.getText(new FileInputStream(contentFile), params);
        assertEquals(content, text.getContent());
    }

    public void test_getText_withUrl() throws IOException {
        final File scriptFile = createScriptTempFile(3);
        final String content = "TEST";
        final File contentFile = createContentFile(".txt", content.getBytes());

        final CommandExtractor extractor = new CommandExtractor();
        extractor.command = getCommand(scriptFile);
        final Map<String, String> params = new HashMap<String, String>();
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, "hoge/fuga.txt");
        final ExtractData text =
            extractor.getText(new FileInputStream(contentFile), params);
        assertEquals(content, text.getContent());
    }

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
        }
    }

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
}
