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

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
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
public class JodExtractorTest extends PlainTestCase {
    private static final Logger logger = LoggerFactory
            .getLogger(JodExtractorTest.class);

    public JodExtractor jodExtractor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer()
                .singleton("officeManagerConfiguration",
                        DefaultOfficeManagerConfiguration.class)//
                .singleton("jodExtractor", JodExtractor.class);
        jodExtractor = container.getComponent("jodExtractor");
        DefaultOfficeManagerConfiguration officeManagerConfiguration = container
                .getComponent("officeManagerConfiguration");
        jodExtractor.officeManager = officeManagerConfiguration.setPortNumber(
                12002).buildOfficeManager();
    }

    public void test_getText_ooow() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/ooo/test.odt");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test.odt");
        ExtractData extractData = jodExtractor.getText(in, params);
        String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_ooow_as() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/ooo/test_as.odt");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test_as.odt");
        ExtractData extractData = jodExtractor.getText(in, params);
        String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_ooos() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/ooo/test.ods");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test.ods");
        ExtractData extractData = jodExtractor.getText(in, params);
        String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_ooos_as() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/ooo/test_as.ods");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test_as.ods");
        ExtractData extractData = jodExtractor.getText(in, params);
        String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_ooop() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/ooo/test.odp");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test.odp");
        ExtractData extractData = jodExtractor.getText(in, params);
        String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_msword() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.doc");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test.doc");
        ExtractData extractData = jodExtractor.getText(in, params);
        String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_msword_as() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test_as.doc");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test_as.doc");
        ExtractData extractData = jodExtractor.getText(in, params);
        String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_mswordx() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.docx");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test.docx");
        ExtractData extractData = jodExtractor.getText(in, params);
        String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    /*
     * TODO not work... public void test_getText_mswordx_as() { InputStream in =
     * ResourceUtil.getResourceAsStream("extractor/msoffice/test_as.docx");
     * Map<String, String> params = new HashMap<String, String>();
     * params.put("resourceName", "test_as.docx"); ExtractData extractData =
     * jodExtractor.getText(in, params); String content =
     * extractData.getContent(); CloseableUtil.closeQuietly(in); logger.info(content);
     * assertTrue(content.contains("テスト")); }
     */

    public void test_getText_msexcel() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.xls");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test.xls");
        ExtractData extractData = jodExtractor.getText(in, params);
        String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_msexcel_as() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test_as.xls");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test_as.xls");
        ExtractData extractData = jodExtractor.getText(in, params);
        String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_msexcelx() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.xlsx");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test.xlsx");
        ExtractData extractData = jodExtractor.getText(in, params);
        String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_msexcelx_as() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test_as.xlsx");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test_as.xlsx");
        ExtractData extractData = jodExtractor.getText(in, params);
        String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_mspowerpoint() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.ppt");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test.ppt");
        ExtractData extractData = jodExtractor.getText(in, params);
        String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_mspowerpointx() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.pptx");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test.pptx");
        ExtractData extractData = jodExtractor.getText(in, params);
        String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_rtf() {
        InputStream in = ResourceUtil.getResourceAsStream("extractor/test.rtf");
        ExtractData extractData = jodExtractor.getText(in, null);
        String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_null() {
        try {
            jodExtractor.getText(null, null);
            fail();
        } catch (CrawlerSystemException e) {
            // NOP
        }
    }

}
