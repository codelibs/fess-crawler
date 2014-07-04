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
public class XmlExtractorTest extends S2TestCase {
    private static final Logger logger = LoggerFactory
            .getLogger(XmlExtractorTest.class);

    public XmlExtractor xmlExtractor;

    @Override
    protected String getRootDicon() throws Throwable {
        return "org/seasar/robot/extractor/extractor.dicon";
    }

    public void test_getXml_utf8() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_utf8.xml");
        final String content = xmlExtractor.getText(in, null).getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("コメント"));
    }

    public void test_getXml_utf8_ignoreCommentTag() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_utf8.xml");
        xmlExtractor.setIgnoreCommentTag(true);
        final String content = xmlExtractor.getText(in, null).getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertFalse(content.contains("コメント"));
        xmlExtractor.setIgnoreCommentTag(false);
    }

    public void test_getXml_sjis() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_sjis.xml");
        final String content = xmlExtractor.getText(in, null).getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getXml_entity() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_entity.xml");
        final String content = xmlExtractor.getText(in, null).getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getXml_mm() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.mm");
        final String content = xmlExtractor.getText(in, null).getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getXml_empty() {
        final InputStream in = new ByteArrayInputStream("".getBytes());
        final String content = xmlExtractor.getText(in, null).getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertEquals("", content);
    }

    public void test_getEncoding_utf8() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_utf8.xml");
        final BufferedInputStream bis = new BufferedInputStream(in);
        final String encoding = xmlExtractor.getEncoding(bis);
        IOUtils.closeQuietly(bis);
        assertEquals("UTF-8", encoding);
    }

    public void test_getEncoding_sjis() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_sjis.xml");
        final BufferedInputStream bis = new BufferedInputStream(in);
        final String encoding = xmlExtractor.getEncoding(bis);
        IOUtils.closeQuietly(bis);
        assertEquals("Shift_JIS", encoding);
    }

    public void test_getEncoding_none() {
        final InputStream in = new ByteArrayInputStream("<hoge></hoge>".getBytes());
        final BufferedInputStream bis = new BufferedInputStream(in);
        final String encoding = xmlExtractor.getEncoding(bis);
        IOUtils.closeQuietly(bis);
        assertEquals("UTF-8", encoding);
    }

    public void test_getRdf() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test.rdf");
        final String content = xmlExtractor.getText(in, null).getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("コメント"));
    }

    public void test_getXml_null() {
        try {
            xmlExtractor.getText(null, null);
            fail();
        } catch (final RobotSystemException e) {
            // NOP
        }
    }
}
