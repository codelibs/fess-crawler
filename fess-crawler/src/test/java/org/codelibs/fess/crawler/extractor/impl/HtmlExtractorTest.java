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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author shinsuke
 *
 */
public class HtmlExtractorTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(HtmlExtractorTest.class);

    public HtmlExtractor htmlExtractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("htmlExtractor", HtmlExtractor.class);
        htmlExtractor = container.getComponent("htmlExtractor");
        htmlExtractor.addMetadata("title", "//TITLE");
    }

    @Test
    public void test_getHtml_utf8() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        final ExtractData data = htmlExtractor.getText(in, null);
        final String content = data.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertEquals("タイトル", data.getValues("title")[0]);
    }

    @Test
    public void test_getHtml_sjis() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_sjis.html");
        final ExtractData data = htmlExtractor.getText(in, null);
        final String content = data.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertEquals("タイトル", data.getValues("title")[0]);
    }

    @Test
    public void test_getHtml_empty() {
        final InputStream in = new ByteArrayInputStream("".getBytes());
        final String content = htmlExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertEquals("", content);
    }

    @Test
    public void test_getEncoding_utf8() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        final BufferedInputStream bis = new BufferedInputStream(in);
        final String encoding = htmlExtractor.getEncoding(bis);
        CloseableUtil.closeQuietly(bis);
        assertEquals("UTF-8", encoding);
    }

    @Test
    public void test_getEncoding_sjis() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_sjis.html");
        final BufferedInputStream bis = new BufferedInputStream(in);
        final String encoding = htmlExtractor.getEncoding(bis);
        CloseableUtil.closeQuietly(bis);
        assertEquals("Shift_JIS", encoding);
    }

    @Test
    public void test_getHtml_null() {
        try {
            htmlExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }

    @Test
    public void test_getHtml_stringMetadata() {
        // A STRING-typed metadata XPath (string(...)) is extracted as a single trimmed value.
        htmlExtractor.addMetadata("titleStr", "string(//TITLE)");
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        final ExtractData data = htmlExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        assertEquals(1, data.getValues("titleStr").length);
        assertEquals("タイトル", data.getValues("titleStr")[0]);
    }

    @Test
    public void test_getHtml_booleanMetadata() {
        // A BOOLEAN-typed metadata XPath (boolean(...)) is rendered as "true"/"false".
        htmlExtractor.addMetadata("hasBody", "boolean(//BODY)");
        htmlExtractor.addMetadata("hasTable", "boolean(//TABLE)");
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        final ExtractData data = htmlExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        assertEquals("true", data.getValues("hasBody")[0]);
        assertEquals("false", data.getValues("hasTable")[0]);
    }

    @Test
    public void test_getHtml_numberMetadata() {
        // A NUMBER-typed metadata XPath (count(...)) is rendered as the number's string form.
        htmlExtractor.addMetadata("divCount", "count(//DIV)");
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        final ExtractData data = htmlExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        assertEquals("1.0", data.getValues("divCount")[0]);
    }

    @Test
    public void test_getHtml_invalidMetadataXPath() {
        // An invalid metadata XPath must not break content extraction; it yields no values.
        htmlExtractor.addMetadata("bad", "//TITLE[1");
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        final ExtractData data = htmlExtractor.getText(in, null);
        final String content = data.getContent();
        CloseableUtil.closeQuietly(in);
        assertTrue(content.contains("テスト"));
        assertEquals("タイトル", data.getValues("title")[0]);
        assertEquals(0, data.getValues("bad").length);
    }
}
