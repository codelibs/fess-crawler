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
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author shinsuke
 *
 */
public class HtmlXpathExtractorTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(HtmlXpathExtractorTest.class);

    public HtmlXpathExtractor htmlXpathExtractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("htmlXpathExtractor", HtmlXpathExtractor.class);
        htmlXpathExtractor = container.getComponent("htmlXpathExtractor");
        htmlXpathExtractor.init();
    }

    @Test
    public void test_getHtml_utf8() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        final String content = htmlXpathExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getHtml_sjis() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_sjis.html");
        final String content = htmlXpathExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getHtml_attr() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_attr.html");
        final String content = htmlXpathExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("本文1"));
        assertTrue(content.contains("本文2"));
        assertTrue(content.contains("画像1"));
        assertTrue(content.contains("画像2"));
        assertTrue(content.contains("タイトル1"));
        assertTrue(content.contains("タイトル2"));
        assertTrue(content.contains("リンク1"));
    }

    @Test
    public void test_getHtml_empty() {
        final InputStream in = new ByteArrayInputStream("".getBytes());
        final String content = htmlXpathExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertEquals("", content);
    }

    @Test
    public void test_getEncoding_utf8() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        final BufferedInputStream bis = new BufferedInputStream(in);
        final String encoding = htmlXpathExtractor.getEncoding(bis);
        CloseableUtil.closeQuietly(bis);
        assertEquals("UTF-8", encoding);
    }

    @Test
    public void test_getEncoding_sjis() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_sjis.html");
        final BufferedInputStream bis = new BufferedInputStream(in);
        final String encoding = htmlXpathExtractor.getEncoding(bis);
        CloseableUtil.closeQuietly(bis);
        assertEquals("Shift_JIS", encoding);
    }

    @Test
    public void test_getEncoding_html5ShortForm() {
        // This extractor reads the same documents as HtmlExtractor and carried the same pattern, so
        // it too saw no declaration at all in the HTML5 short form.
        assertEquals("Shift_JIS", detectEncoding("<meta charset=\"Shift_JIS\">"));
        assertEquals("EUC-JP", detectEncoding("<html><head><meta charset=EUC-JP></head>"));
    }

    @Test
    public void test_getEncoding_contentTypeForm() {
        // The http-equiv spelling was the only one recognised before and must keep working.
        assertEquals("Shift_JIS", detectEncoding("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=Shift_JIS\">"));
    }

    @Test
    public void test_getEncoding_notADeclaration() {
        // A distinctive default makes the fall-through visible when nothing is declared.
        htmlXpathExtractor.setEncoding("ISO-8859-1");
        assertEquals("ISO-8859-1", detectEncoding("<meta data-charset=\"Shift_JIS\">"));
        assertEquals("ISO-8859-1", detectEncoding("<p>write charset=Shift_JIS to declare it</p>"));
    }

    private String detectEncoding(final String head) {
        final BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(head.getBytes(StandardCharsets.UTF_8)));
        try {
            return htmlXpathExtractor.getEncoding(bis);
        } finally {
            CloseableUtil.closeQuietly(bis);
        }
    }

    @Test
    public void test_getHtml_null() {
        try {
            htmlXpathExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }

    @Test
    public void test_getHtml_customTargetNodePath() {
        // A custom targetNodePath narrows extraction to the selected nodes only.
        htmlXpathExtractor.setTargetNodePath("//A");
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_attr.html");
        final String content = htmlXpathExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertEquals("リンク1", content);
    }

    @Test
    public void test_getHtml_invalidTargetNodePath() {
        // An invalid targetNodePath surfaces as an ExtractException.
        htmlXpathExtractor.setTargetNodePath("//A[1");
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_attr.html");
        try {
            htmlXpathExtractor.getText(in, null);
            fail();
        } catch (final ExtractException e) {
            // NOP
        } finally {
            CloseableUtil.closeQuietly(in);
        }
    }
}
