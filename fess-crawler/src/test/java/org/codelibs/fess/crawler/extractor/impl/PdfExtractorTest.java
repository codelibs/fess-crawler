/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.metadata.TikaMetadataKeys;
import org.codelibs.core.io.CloseableUtil;
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
public class PdfExtractorTest extends PlainTestCase {
    private static final Logger logger = LoggerFactory
            .getLogger(PdfExtractorTest.class);

    public PdfExtractor pdfExtractor;

    private PdfExtractor pdfExtractorForPdfPassword;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer()
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
        CloseableUtil.closeQuietly(in);
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
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_null() {
        try {
            pdfExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }

    public void test_getPassword_null() {
        String url;
        String resourceName;
        HashMap<String, String> params = new HashMap<>();

        url = null;
        resourceName = null;
        params.put(ExtractData.URL, url);
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, resourceName);
        assertNull(pdfExtractor.getPassword(params));

        url = "http://test.com/hoge1.pdf";
        resourceName = null;
        params.put(ExtractData.URL, url);
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, resourceName);
        assertNull(pdfExtractor.getPassword(params));

        url = "http://test.com/hoge1.pdf";
        resourceName = "hoge2.pdf";
        params.put(ExtractData.URL, url);
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, resourceName);
        assertNull(pdfExtractor.getPassword(params));

        url = null;
        resourceName = "hoge2.pdf";
        params.put(ExtractData.URL, url);
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, resourceName);
        assertNull(pdfExtractor.getPassword(params));
    }

    public void test_getPassword() {
        String url;
        String resourceName;
        pdfExtractor.addPassword(".*hoge1.pdf", "password");
        pdfExtractor.addPassword("fuga.pdf", "PASSWORD");
        HashMap<String, String> params = new HashMap<>();

        url = null;
        resourceName = null;
        params.put(ExtractData.URL, url);
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, resourceName);
        assertNull(pdfExtractor.getPassword(params));

        url = "http://test.com/hoge1.pdf";
        resourceName = null;
        params.put(ExtractData.URL, url);
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, resourceName);
        assertEquals("password", pdfExtractor.getPassword(params));

        url = "http://test.com/hoge1.pdf";
        resourceName = "hoge2.pdf";
        params.put(ExtractData.URL, url);
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, resourceName);
        assertEquals("password", pdfExtractor.getPassword(params));

        url = null;
        resourceName = "hoge2.pdf";
        params.put(ExtractData.URL, url);
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, resourceName);
        assertNull(pdfExtractor.getPassword(params));

        url = null;
        resourceName = "hoge1.pdf";
        params.put(ExtractData.URL, url);
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, resourceName);
        assertEquals("password", pdfExtractor.getPassword(params));

        url = "http://test.com/fuga.pdf";
        resourceName = null;
        params.put(ExtractData.URL, url);
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, resourceName);
        assertNull(pdfExtractor.getPassword(params));

        url = null;
        resourceName = "fuga.pdf";
        params.put(ExtractData.URL, url);
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, resourceName);
        assertEquals("PASSWORD", pdfExtractor.getPassword(params));
    }

    public void test_getPassword_json() {
        String url;
        Map<String, String> params = new HashMap<>();
        params.put(ExtractData.FILE_PASSWORDS, "{\".*hoge1.pdf\":\"password\",\"fuga.pdf\":\"PASSWORD\"}");

        url = null;
        params.put(ExtractData.URL, url);
        assertNull(pdfExtractor.getPassword(params));

        url = "http://test.com/hoge1.pdf";
        params.put(ExtractData.URL, url);
        assertEquals("password", pdfExtractor.getPassword(params));

        url = "http://test.com/hoge1.pdf";
        params.put(ExtractData.URL, url);
        assertEquals("password", pdfExtractor.getPassword(params));

        url = "http://test.com/fuga.pdf";
        params.put(ExtractData.URL, url);
        assertNull(pdfExtractor.getPassword(params));
    }
}
