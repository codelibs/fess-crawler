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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ExtractData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 * 
 */
public class PdfExtractorTest extends S2TestCase {
    private static final Logger logger = LoggerFactory
        .getLogger(PdfExtractorTest.class);

    public PdfExtractor pdfExtractor;

    @Override
    protected String getRootDicon() throws Throwable {
        return "org/seasar/robot/extractor/extractor.dicon";
    }

    public void test_getText() {
        InputStream in = ResourceUtil.getResourceAsStream("extractor/test.pdf");
        ExtractData extractData = pdfExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertEquals("Writer", extractData.getValues("Creator")[0]);
        assertEquals("OpenOffice.org 3.0", extractData.getValues("Producer")[0]);
        assertEquals(
            "D:20090627222631+09'00'",
            extractData.getValues("CreationDate")[0]);
    }

    public void test_getText_pass() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/test_pass.pdf");
        Map<String, String> params = new HashMap<String, String>();
        params.put(ExtractData.URL, "http://example.com/test_pass.pdf");
        String content = pdfExtractor.getText(in, params).getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_null() {
        try {
            pdfExtractor.getText(null, null);
            fail();
        } catch (RobotSystemException e) {
            // NOP
        }
    }

    public void test_getPassword_null() {
        String url;
        String resourceName;

        url = null;
        resourceName = null;
        assertNull(pdfExtractor.getPassword(url, resourceName));

        url = "http://test.com/hoge1.pdf";
        resourceName = null;
        assertNull(pdfExtractor.getPassword(url, resourceName));

        url = "http://test.com/hoge1.pdf";
        resourceName = "hoge2.pdf";
        assertNull(pdfExtractor.getPassword(url, resourceName));

        url = null;
        resourceName = "hoge2.pdf";
        assertNull(pdfExtractor.getPassword(url, resourceName));
    }

    public void test_getPassword() {
        String url;
        String resourceName;
        pdfExtractor.addPassword(".*hoge1.pdf", "password");
        pdfExtractor.addPassword("fuga.pdf", "PASSWORD");

        url = null;
        resourceName = null;
        assertNull(pdfExtractor.getPassword(url, resourceName));

        url = "http://test.com/hoge1.pdf";
        resourceName = null;
        assertEquals("password", pdfExtractor.getPassword(url, resourceName));

        url = "http://test.com/hoge1.pdf";
        resourceName = "hoge2.pdf";
        assertEquals("password", pdfExtractor.getPassword(url, resourceName));

        url = null;
        resourceName = "hoge2.pdf";
        assertNull(pdfExtractor.getPassword(url, resourceName));

        url = null;
        resourceName = "hoge1.pdf";
        assertEquals("password", pdfExtractor.getPassword(url, resourceName));

        url = "http://test.com/fuga.pdf";
        resourceName = null;
        assertNull(pdfExtractor.getPassword(url, resourceName));

        url = null;
        resourceName = "fuga.pdf";
        assertEquals("PASSWORD", pdfExtractor.getPassword(url, resourceName));
    }
}
