/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.robot.RobotSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class HtmlXpathExtractorTest extends S2TestCase {
    private static final Logger logger = LoggerFactory
            .getLogger(HtmlXpathExtractorTest.class);

    public HtmlXpathExtractor htmlXpathExtractor;

    @Override
    protected String getRootDicon() throws Throwable {
        return "org/seasar/robot/extractor/extractor.dicon";
    }

    public void test_getHtml_utf8() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_utf8.html");
        String content = htmlXpathExtractor.getText(in, null).getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getHtml_sjis() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_sjis.html");
        String content = htmlXpathExtractor.getText(in, null).getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getHtml_attr() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_attr.html");
        String content = htmlXpathExtractor.getText(in, null).getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("本文1"));
        assertTrue(content.contains("本文2"));
        assertTrue(content.contains("画像1"));
        assertTrue(content.contains("画像2"));
        assertTrue(content.contains("タイトル1"));
        assertTrue(content.contains("タイトル2"));
        assertTrue(content.contains("リンク1"));
    }

    public void test_getHtml_empty() {
        InputStream in = new ByteArrayInputStream("".getBytes());
        String content = htmlXpathExtractor.getText(in, null).getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertEquals("", content);
    }

    public void test_getEncoding_utf8() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_utf8.html");
        BufferedInputStream bis = new BufferedInputStream(in);
        String encoding = htmlXpathExtractor.getEncoding(bis);
        IOUtils.closeQuietly(bis);
        assertEquals("UTF-8", encoding);
    }

    public void test_getEncoding_sjis() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_sjis.html");
        BufferedInputStream bis = new BufferedInputStream(in);
        String encoding = htmlXpathExtractor.getEncoding(bis);
        IOUtils.closeQuietly(bis);
        assertEquals("Shift_JIS", encoding);
    }

    public void test_getHtml_null() {
        try {
            htmlXpathExtractor.getText(null, null);
            fail();
        } catch (RobotSystemException e) {
            // NOP
        }
    }
}
