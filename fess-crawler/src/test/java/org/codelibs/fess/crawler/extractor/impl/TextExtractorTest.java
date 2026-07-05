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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
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

    @Test
    public void test_maxTextLength_zero_isUnlimited() {
        // maxTextLength=0 means unlimited (condition: maxTextLength > 0 is false).
        textExtractor.setMaxTextLength(0);
        final String text = "Hello full content.";
        final InputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = textExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        assertEquals(text, extractData.getContent());
        assertNull(extractData.getValues("truncated"));
    }

    @Test
    public void test_maxTextLength_negative_isUnlimited() {
        // Negative maxTextLength means unlimited.
        textExtractor.setMaxTextLength(-1);
        final String text = "Full content survives.";
        final InputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = textExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        assertEquals(text, extractData.getContent());
        assertNull(extractData.getValues("truncated"));
    }

    @Test
    public void test_maxTextLength_exactlyContentLength() {
        // When cap == content length, no truncation should occur.
        final String text = "ExactFit";
        textExtractor.setMaxTextLength(text.length());
        final InputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = textExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        assertEquals(text, extractData.getContent());
        assertNull(extractData.getValues("truncated"));
    }

    @Test
    public void test_maxTextLength_one() {
        textExtractor.setMaxTextLength(1);
        final String text = "ABCDEF";
        final InputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = textExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        assertEquals("A", extractData.getContent());
        final String[] truncated = extractData.getValues("truncated");
        assertNotNull(truncated);
        assertEquals("true", truncated[0]);
    }

    @Test
    public void test_maxTextLength_acrossBufferBoundary() {
        // READ_BUFFER_SIZE=8192; set cap=8200 with 16384-char content so the
        // truncation is detected in the second buffer read.
        final int cap = 8200;
        textExtractor.setMaxTextLength(cap);
        final char[] unit = "abcdefghij".toCharArray();
        final int totalChars = 16384;
        final StringBuilder builder = new StringBuilder(totalChars);
        for (int i = 0; i < totalChars; i++) {
            builder.append(unit[i % unit.length]);
        }
        final String text = builder.toString();
        final InputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = textExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        final String content = extractData.getContent();
        assertTrue(content.length() <= cap);
        assertTrue(content.length() > 0);
        final String[] truncated = extractData.getValues("truncated");
        assertNotNull(truncated);
        assertEquals("true", truncated[0]);
    }

    @Test
    public void test_truncatedMetadataFlag() {
        // When truncation occurs the ExtractData must carry truncated=true and maxTextLength metadata.
        textExtractor.setMaxTextLength(5);
        final String text = "0123456789";
        final InputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = textExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        assertEquals("01234", extractData.getContent());
        final String[] truncated = extractData.getValues("truncated");
        assertNotNull(truncated);
        assertEquals("true", truncated[0]);
        final String[] maxLen = extractData.getValues("maxTextLength");
        assertNotNull(maxLen);
        assertEquals("5", maxLen[0]);
    }

    @Test
    public void test_midStreamBomIsPreserved() {
        // A BOM in the middle of the stream (not at position 0) is NOT a real BOM;
        // it should be preserved as U+FEFF in the output.
        // We write plain ASCII then a BOM byte sequence (as UTF-8: EF BB BF).
        final String prefix = "abc";
        final byte[] bomBytes = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
        final String suffix = "xyz";
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(prefix.getBytes(StandardCharsets.UTF_8));
            baos.write(bomBytes);
            baos.write(suffix.getBytes(StandardCharsets.UTF_8));
        } catch (final Exception e) {
            throw new RuntimeException("Setup failed: " + e.getMessage(), e);
        }
        final InputStream in = new ByteArrayInputStream(baos.toByteArray());
        final String content = textExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        // The mid-stream BOM bytes decode as U+FEFF in UTF-8.
        assertTrue(content.startsWith("abc"));
        assertTrue(content.endsWith("xyz"));
        // U+FEFF (BOM) should appear in the middle.
        assertTrue(content.contains("﻿"));
    }

    @Test
    public void test_surrogatePairAtBoundary() {
        // Build a string where the last char before the cap is a high surrogate.
        // U+1F600 (GRINNING FACE) encodes as the surrogate pair 0xD83D 0xDE00.
        // We place it so the cap falls right after the high surrogate (0xD83D).
        // The extractor must drop the dangling high surrogate.
        final String emoji = "😀"; // U+1F600, 2 Java chars
        // Build: 8 ASCII chars + emoji = 10 Java chars. Set cap=9 so the boundary
        // falls at the high surrogate (char index 8).
        final String text = "12345678" + emoji + "trailing";
        textExtractor.setMaxTextLength(9); // cuts after char 9 — high surrogate at index 8 must be dropped
        final InputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = textExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        final String content = extractData.getContent();
        // High surrogate was dropped — result should be exactly the 8 ASCII chars.
        assertEquals("12345678", content);
        // Verify no unpaired high surrogate.
        for (int i = 0; i < content.length(); i++) {
            final char c = content.charAt(i);
            assertFalse(Character.isHighSurrogate(c));
        }
    }

    @Test
    public void test_extractsUtf32LeWithBom() throws Exception {
        textExtractor.setEncoding("UTF-8");
        final String text = "Hello UTF-32 LE";
        // UTF-32 LE BOM: FF FE 00 00
        final byte[] bom = { (byte) 0xFF, (byte) 0xFE, (byte) 0x00, (byte) 0x00 };
        // Encode text as UTF-32LE
        final byte[] textBytes = text.getBytes("UTF-32LE");
        final InputStream in = new SequenceInputStream(new ByteArrayInputStream(bom), new ByteArrayInputStream(textBytes));
        final String content = textExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        assertEquals(text, content);
    }

    @Test
    public void test_extractsUtf32BeWithBom() throws Exception {
        textExtractor.setEncoding("UTF-8");
        final String text = "Hello UTF-32 BE";
        // UTF-32 BE BOM: 00 00 FE FF
        final byte[] bom = { (byte) 0x00, (byte) 0x00, (byte) 0xFE, (byte) 0xFF };
        final byte[] textBytes = text.getBytes("UTF-32BE");
        final InputStream in = new SequenceInputStream(new ByteArrayInputStream(bom), new ByteArrayInputStream(textBytes));
        final String content = textExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        assertEquals(text, content);
    }
}
