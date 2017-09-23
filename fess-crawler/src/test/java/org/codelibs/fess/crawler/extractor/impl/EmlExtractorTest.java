/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class EmlExtractorTest extends PlainTestCase {
    private static final Logger logger = LoggerFactory.getLogger(EmlExtractorTest.class);

    public EmlExtractor emlExtractor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer()
                .singleton("emlExtractor", EmlExtractor.class);
        emlExtractor = container.getComponent("emlExtractor");
    }
    
    public void test_getText() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/eml/sample1.eml");
        ExtractData data = emlExtractor.getText(in, null);
        final String content = data.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("プレイステーション"));
        assertTrue(data.getValues("Subject")[0].contains("ダイジェスト"));
    }

    public void test_getMultipartText() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/eml/sample2.eml");
        ExtractData data = emlExtractor.getText(in, null);
        final String content = data.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("チンギス・ハン"));
        assertTrue(data.getValues("Subject")[0].contains("気象情報"));
    }

    public void test_getReceivedDate() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/eml/sample1.eml");
        ExtractData data = emlExtractor.getText(in, null);
        IOUtils.closeQuietly(in);
        final String[] receivedDate = data.getValues("Received-Date");
        logger.info("Received-Date: " + receivedDate[0]);
        assertEquals(receivedDate[0], "2012-11-11T02:39:59.000Z");
    }

    public void test_getDecodeText() throws Exception {
        assertEquals("", emlExtractor.getDecodeText(null));
        assertEquals("", emlExtractor.getDecodeText(""));
        assertEquals("abc123", emlExtractor.getDecodeText("abc123"));
        assertEquals("テスト", emlExtractor.getDecodeText("=?UTF-8?B?44OG44K544OI?="));
    }

    public void test_getText_null() {
        try {
            emlExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }
}
