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
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author shinsuke
 *
 */
public class TextExtractorTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(TextExtractorTest.class);

    public TextExtractor textExtractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("textExtractor", TextExtractor.class);
        textExtractor = container.getComponent("textExtractor");
    }

    @Test
    public void test_getText() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.txt");
        final String content = textExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getText_null() {
        try {
            textExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }

    @Test
    public void test_extractsUtf8() {
        final String text = "Hello, world! こんにちは";
        final InputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        final String content = textExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        assertEquals(text, content);
    }

    @Test
    public void test_extractsUtf8WithBom() {
        final String text = "BOM stripped テスト";
        final byte[] bom = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
        final InputStream in =
                new SequenceInputStream(new ByteArrayInputStream(bom), new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
        final String content = textExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        assertEquals(text, content);
        assertFalse(content.startsWith("﻿"));
    }

    @Test
    public void test_extractsUtf16LeWithBom() {
        // Configure a non-UTF16 encoding to prove the BOM (not the configured encoding) wins.
        textExtractor.setEncoding("UTF-8");
        final String text = "Hello UTF-16 LE テスト";
        final byte[] bom = { (byte) 0xFF, (byte) 0xFE };
        final InputStream in =
                new SequenceInputStream(new ByteArrayInputStream(bom), new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_16LE)));
        final String content = textExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        assertEquals(text, content);
    }

    @Test
    public void test_extractsUtf16BeWithBom() {
        textExtractor.setEncoding("UTF-8");
        final String text = "Hello UTF-16 BE テスト";
        final byte[] bom = { (byte) 0xFE, (byte) 0xFF };
        final InputStream in =
                new SequenceInputStream(new ByteArrayInputStream(bom), new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_16BE)));
        final String content = textExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        assertEquals(text, content);
    }

    @Test
    public void test_extractsShiftJis() {
        textExtractor.setEncoding("Shift_JIS");
        final String text = "シフトジステスト hello";
        final InputStream in = new ByteArrayInputStream(text.getBytes(java.nio.charset.Charset.forName("Shift_JIS")));
        final String content = textExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        assertEquals(text, content);
    }

    @Test
    public void test_truncatesAtMaxTextLength() {
        textExtractor.setMaxTextLength(10);
        final String text = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // 36 chars
        final InputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        final String content = textExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        assertEquals(10, content.length());
        assertEquals("0123456789", content);
    }

    @Test
    public void test_largeFile_streamsCorrectly() {
        // 10 MiB of repeating content; verify exact length and a sample.
        final char[] unit = "abcdefghij".toCharArray();
        final int unitLen = unit.length;
        final int totalChars = 10 * 1024 * 1024;
        final StringBuilder builder = new StringBuilder(totalChars);
        for (int i = 0; i < totalChars; i++) {
            builder.append(unit[i % unitLen]);
        }
        final String expected = builder.toString();
        final InputStream in = new ByteArrayInputStream(expected.getBytes(StandardCharsets.UTF_8));
        final String content = textExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        assertEquals(totalChars, content.length());
        // Avoid full equality string comparison (already covered by length); spot check first/last 32.
        assertEquals(expected.substring(0, 32), content.substring(0, 32));
        assertEquals(expected.substring(totalChars - 32), content.substring(totalChars - 32));
    }
}
