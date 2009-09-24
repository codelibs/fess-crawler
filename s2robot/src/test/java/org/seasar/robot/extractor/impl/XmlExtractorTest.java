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
public class XmlExtractorTest extends S2TestCase {
    private static final Logger logger = LoggerFactory
            .getLogger(XmlExtractorTest.class);

    public XmlExtractor xmlExtractor;

    @Override
    protected String getRootDicon() throws Throwable {
        return "org/seasar/robot/extractor/extractor.dicon";
    }

    public void test_getXml_utf8() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_utf8.xml");
        String content = xmlExtractor.getText(in, null).getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getXml_sjis() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_sjis.xml");
        String content = xmlExtractor.getText(in, null).getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getEncoding_utf8() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_utf8.xml");
        BufferedInputStream bis = new BufferedInputStream(in);
        String encoding = xmlExtractor.getEncoding(bis);
        IOUtils.closeQuietly(bis);
        assertEquals("UTF-8", encoding);
    }

    public void test_getEncoding_sjis() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_sjis.xml");
        BufferedInputStream bis = new BufferedInputStream(in);
        String encoding = xmlExtractor.getEncoding(bis);
        IOUtils.closeQuietly(bis);
        assertEquals("Shift_JIS", encoding);
    }

    public void test_getEncoding_none() {
        InputStream in = new ByteArrayInputStream("<hoge></hoge>".getBytes());
        BufferedInputStream bis = new BufferedInputStream(in);
        String encoding = xmlExtractor.getEncoding(bis);
        IOUtils.closeQuietly(bis);
        assertEquals("UTF-8", encoding);
    }

    public void test_getXml_null() {
        try {
            xmlExtractor.getText(null, null);
            fail();
        } catch (RobotSystemException e) {
            // NOP
        }
    }
}
