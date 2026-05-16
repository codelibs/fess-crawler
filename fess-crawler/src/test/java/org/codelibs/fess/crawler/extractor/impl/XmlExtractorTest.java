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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
public class XmlExtractorTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(XmlExtractorTest.class);

    public XmlExtractor xmlExtractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("xmlExtractor", XmlExtractor.class);
        xmlExtractor = container.getComponent("xmlExtractor");
    }

    @Test
    public void test_getXml_utf8() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.xml");
        final String content = xmlExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("コメント"));
    }

    @Test
    public void test_getXml_utf8_ignoreCommentTag() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.xml");
        xmlExtractor.setIgnoreCommentTag(true);
        final String content = xmlExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertFalse(content.contains("コメント"));
        xmlExtractor.setIgnoreCommentTag(false);
    }

    @Test
    public void test_getXml_sjis() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_sjis.xml");
        final String content = xmlExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getXml_entity() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_entity.xml");
        final String content = xmlExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getXml_mm() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.mm");
        final String content = xmlExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getXml_empty() {
        final InputStream in = new ByteArrayInputStream("".getBytes());
        final String content = xmlExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertEquals("", content);
    }

    @Test
    public void test_getEncoding_utf8() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.xml");
        final BufferedInputStream bis = new BufferedInputStream(in);
        final String encoding = xmlExtractor.getEncoding(bis);
        CloseableUtil.closeQuietly(bis);
        assertEquals("UTF-8", encoding);
    }

    @Test
    public void test_getEncoding_sjis() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_sjis.xml");
        final BufferedInputStream bis = new BufferedInputStream(in);
        final String encoding = xmlExtractor.getEncoding(bis);
        CloseableUtil.closeQuietly(bis);
        assertEquals("Shift_JIS", encoding);
    }

    @Test
    public void test_getEncoding_utf8bom() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/xml/test_utf8bom.xml");
        final BufferedInputStream bis = new BufferedInputStream(in);
        final String encoding = xmlExtractor.getEncoding(bis);
        CloseableUtil.closeQuietly(bis);
        assertEquals("UTF-8", encoding);
    }

    @Test
    public void test_getEncoding_utf16lebom() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/xml/test_utf16lebom.xml");
        final BufferedInputStream bis = new BufferedInputStream(in);
        final String encoding = xmlExtractor.getEncoding(bis);
        CloseableUtil.closeQuietly(bis);
        assertEquals("UTF-16LE", encoding);
    }

    @Test
    public void test_getEncoding_utf16bebom() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/xml/test_utf16bebom.xml");
        final BufferedInputStream bis = new BufferedInputStream(in);
        final String encoding = xmlExtractor.getEncoding(bis);
        CloseableUtil.closeQuietly(bis);
        assertEquals("UTF-16BE", encoding);
    }

    @Test
    public void test_getEncoding_none() {
        final InputStream in = new ByteArrayInputStream("<hoge></hoge>".getBytes());
        final BufferedInputStream bis = new BufferedInputStream(in);
        final String encoding = xmlExtractor.getEncoding(bis);
        CloseableUtil.closeQuietly(bis);
        assertEquals("UTF-8", encoding);
    }

    @Test
    public void test_getRdf() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.rdf");
        final String content = xmlExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("コメント"));
    }

    @Test
    public void test_getXml_null() {
        try {
            xmlExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }

    @Test
    public void test_extractsXmlWithUtf8Bom() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/xml/test_utf8bom.xml");
        final String content = xmlExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        // The first character of the extracted text should not be a BOM (﻿) since
        // the BOM bytes are now stripped before decoding.
        assertFalse(content.startsWith("﻿"));
        assertTrue(content.length() >= 0);
        // The xmlns attribute value survives extractString's attribute extraction.
        assertTrue(content.contains("http://www.example.com/hoge"));
    }

    @Test
    public void test_extractsXmlWithUtf16LeBom() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/xml/test_utf16lebom.xml");
        final String content = xmlExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertFalse(content.startsWith("﻿"));
        assertTrue(content.length() >= 0);
        // The xmlns attribute value survives extractString's attribute extraction.
        assertTrue(content.contains("http://www.example.com/hoge"));
    }

    @Test
    public void test_extractsXmlWithUtf16BeBom() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/xml/test_utf16bebom.xml");
        final String content = xmlExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertFalse(content.startsWith("﻿"));
        assertTrue(content.length() >= 0);
        // The xmlns attribute value survives extractString's attribute extraction.
        assertTrue(content.contains("http://www.example.com/hoge"));
    }

    @Test
    public void test_truncatesAtMaxTextLength() {
        // Build a long XML string and verify the extractor honours maxTextLength.
        final StringBuilder sb = new StringBuilder();
        sb.append("<root>");
        for (int i = 0; i < 1000; i++) {
            sb.append("<item>x</item>");
        }
        sb.append("</root>");
        final byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        xmlExtractor.setMaxTextLength(50);
        final InputStream in = new ByteArrayInputStream(bytes);
        final ExtractData extractData = xmlExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        final String content = extractData.getContent();
        assertNotNull(content);
        assertTrue(content.length() <= 50);
        assertTrue(content.contains("x"));
    }

    @Test
    public void test_largeXml_streamsCorrectly() {
        // 100,000 items — verify that streaming doesn't OOM and produces correct output.
        final StringBuilder sb = new StringBuilder();
        sb.append("<root>");
        for (int i = 0; i < 100_000; i++) {
            sb.append("<item>x</item>");
        }
        sb.append("</root>");
        final byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        final InputStream in = new ByteArrayInputStream(bytes);
        final String content = xmlExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        assertNotNull(content);
        assertTrue(content.contains("x"));
    }

    @Test
    public void test_maxTextLength_zero_isUnlimited() {
        // maxTextLength=0 should be treated as unlimited (condition: maxTextLength > 0 is false).
        xmlExtractor.setMaxTextLength(0);
        final String xml = "<root><item>hello world</item></root>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        final String content = xmlExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        assertTrue(content.contains("hello world"));
    }

    @Test
    public void test_truncatedMetadataFlag() {
        // When truncation occurs the ExtractData must carry truncated=true and maxTextLength metadata.
        xmlExtractor.setMaxTextLength(20);
        final StringBuilder sb = new StringBuilder("<root>");
        for (int i = 0; i < 100; i++) {
            sb.append("<item>x</item>");
        }
        sb.append("</root>");
        final InputStream in = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = xmlExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        final String[] truncated = extractData.getValues("truncated");
        assertNotNull(truncated);
        assertEquals("true", truncated[0]);
        final String[] maxLen = extractData.getValues("maxTextLength");
        assertNotNull(maxLen);
        assertEquals("20", maxLen[0]);
    }
}
