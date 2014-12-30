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
package org.codelibs.robot.extractor.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.robot.RobotSystemException;
import org.codelibs.robot.container.StandardRobotContainer;
import org.codelibs.robot.entity.ExtractData;
import org.dbflute.utflute.core.PlainTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 * 
 */
public class PdfExtractorTest extends PlainTestCase {
    private static final Logger logger = LoggerFactory
            .getLogger(PdfExtractorTest.class);

    public PdfExtractor pdfExtractor;

    private PdfExtractor pdfExtractorForPdfPassword;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardRobotContainer container = new StandardRobotContainer()
                .singleton("pdfExtractor", PdfExtractor.class)//
                .singleton("pdfExtractorForPdfPassword", PdfExtractor.class);
        pdfExtractor = container.getComponent("pdfExtractor");
        pdfExtractorForPdfPassword = container
                .getComponent("pdfExtractorForPdfPassword");
        pdfExtractorForPdfPassword.addPassword(".*test_.*.pdf", "word");
    }

    public void test_getText() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test.pdf");
        final ExtractData extractData = pdfExtractor.getText(in, null);
        final String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertEquals("Writer", extractData.getValues("Creator")[0]);
        assertEquals("OpenOffice.org 3.0", extractData.getValues("Producer")[0]);
        assertEquals("D:20090627222631+09'00'",
                extractData.getValues("CreationDate")[0]);
    }

    public void test_getText_pass() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_pass.pdf");
        final Map<String, String> params = new HashMap<String, String>();
        params.put(ExtractData.URL, "http://example.com/test_pass.pdf");
        final String content = pdfExtractorForPdfPassword.getText(in, params)
                .getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_null() {
        try {
            pdfExtractor.getText(null, null);
            fail();
        } catch (final RobotSystemException e) {
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
