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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.metadata.TikaMetadataKeys;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 * 
 */
public class TikaExtractorTest extends PlainTestCase {
    private static final Logger logger = LoggerFactory
            .getLogger(TikaExtractorTest.class);

    public TikaExtractor tikaExtractor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class)
                .singleton("tikaExtractor", TikaExtractor.class)
                .<ExtractorFactory> singleton(
                        "extractorFactory",
                        ExtractorFactory.class,
                        factory -> {
                            TikaExtractor tikaExtractor = container
                                    .getComponent("tikaExtractor");
                            factory.addExtractor("text/plain", tikaExtractor);
                            factory.addExtractor("text/html", tikaExtractor);
                        })//
        ;

        tikaExtractor = container.getComponent("tikaExtractor");
    }

    public void test_getTika_text() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test.txt");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_text_sjis() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_sjis.txt");
        Map<String, String> params = new HashMap<>();
        params.put("Content-Type", "text/plain");
        final ExtractData extractData = tikaExtractor.getText(in, params);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    // TODO tika needs to support pdfbox 2.0
//    public void test_getTika_pdf() {
//        final InputStream in = ResourceUtil
//                .getResourceAsStream("extractor/test.pdf");
//        final ExtractData extractData = tikaExtractor.getText(in, null);
//        final String content = extractData.getContent();
//        CloseableUtil.closeQuietly(in);
//        logger.info(content);
//        assertTrue(content.contains("テスト"));
//    }

    // public void test_getTika_pdf_pass() {
    // InputStream in =
    // ResourceUtil.getResourceAsStream("extractor/test_pass.pdf");
    // TikaExtractor extractor =
    // (TikaExtractor) getContainer().getComponent(
    // "tikaExtractorForPdfPassword");
    // Map<String, String> params = new HashMap<String, String>();
    // params.put(ExtractData.URL, "http://example.com/test_pass.pdf");
    // ExtractData extractData = extractor.getText(in, params);
    // String content = extractData.getContent();
    // CloseableUtil.closeQuietly(in);
    // logger.info(content);
    // assertTrue(content.contains("テスト"));
    // }

    public void test_getTika_html() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_utf8.html");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_html_sjis() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_sjis.html");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_msword() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.doc");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (final String key : extractData.getKeySet()) {
            logger.info(key + "="
                    + String.join("|", extractData.getValues(key)));
        }
        assertEquals("こめんと", extractData.getValues("Comments")[0]);
        assertEquals("さぶたいとる", extractData.getValues("cp:subject")[0]);
        assertEquals("太郎", extractData.getValues("Author")[0]);
        assertEquals("2009-06-26T21:41:00Z",
                extractData.getValues("Creation-Date")[0]);
        assertEquals("たいとる", extractData.getValues("title")[0]);
        assertEquals("かいしゃ", extractData.getValues("Company")[0]);
        assertEquals("たぐ", extractData.getValues("Keywords")[0]);
        assertEquals("2012-05-18T22:45:00Z",
                extractData.getValues("Last-Save-Date")[0]);
        assertEquals("4", extractData.getValues("Revision-Number")[0]);
        assertEquals("Normal", extractData.getValues("Template")[0]);
        assertEquals("Microsoft Office Word",
                extractData.getValues("Application-Name")[0]);
        assertEquals("1", extractData.getValues("xmpTPg:NPages")[0]);
        assertEquals("3", extractData.getValues("Character Count")[0]);
        assertEquals("application/msword",
                extractData.getValues("Content-Type")[0]);
    }

    public void test_getTika_mswordx() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.docx");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (final String key : extractData.getKeySet()) {
            logger.info(key + "="
                    + String.join("|", extractData.getValues(key)));
        }
        assertEquals("じょうたい", extractData.getValues("Content-Status")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("さぶたいとる", extractData.getValues("cp:subject")[0]);
        assertEquals("Microsoft Office Word",
                extractData.getValues("Application-Name")[0]);
        assertEquals("太郎", extractData.getValues("Author")[0]);
        assertEquals("14.0000", extractData.getValues("Application-Version")[0]);
        assertEquals("3",
                extractData.getValues("Character-Count-With-Spaces")[0]);
        assertEquals("2012-05-18T22:44:00Z", extractData.getValues("date")[0]);
        assertEquals("2", extractData.getValues("Total-Time")[0]);
        assertEquals("太郎", extractData.getValues("creator")[0]);
        assertEquals("かいしゃ", extractData.getValues("publisher")[0]);
        assertEquals("2010-07-22T00:21:00Z",
                extractData.getValues("Creation-Date")[0]);
        assertEquals("たいとる", extractData.getValues("title")[0]);
        assertEquals("1", extractData.getValues("Line-Count")[0]);
        assertEquals("花子", extractData.getValues("Manager")[0]);
        assertEquals("こめんと", extractData.getValues("description")[0]);
        assertEquals("たぐ", extractData.getValues("Keywords")[0]);
        assertEquals("1", extractData.getValues("Paragraph-Count")[0]);
        assertEquals("2", extractData.getValues("Revision-Number")[0]);
        assertEquals("Normal", extractData.getValues("Template")[0]);
        assertEquals("1", extractData.getValues("Page-Count")[0]);
        assertEquals("2012-05-18T22:44:00Z",
                extractData.getValues("Last-Modified")[0]);
        assertEquals("1", extractData.getValues("xmpTPg:NPages")[0]);
        assertEquals("ぶんるい", extractData.getValues("Category")[0]);
        assertEquals("3", extractData.getValues("Character Count")[0]);
        assertEquals(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                extractData.getValues("Content-Type")[0]);

    }

    public void test_getTika_msexcel() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.xls");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (final String key : extractData.getKeySet()) {
            logger.info(key + "="
                    + String.join("|", extractData.getValues(key)));
        }
        assertEquals("こめんと", extractData.getValues("Comments")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("さぶたいとる", extractData.getValues("cp:subject")[0]);
        assertEquals("Microsoft Excel",
                extractData.getValues("Application-Name")[0]);
        assertEquals("太郎", extractData.getValues("Author")[0]);
        assertEquals("1997-01-08T22:48:59Z",
                extractData.getValues("Creation-Date")[0]);
        assertEquals("ぶんるい", extractData.getValues("Category")[0]);
        assertEquals("たいとる", extractData.getValues("title")[0]);
        assertEquals("花子", extractData.getValues("Manager")[0]);
        assertEquals("かいしゃ", extractData.getValues("Company")[0]);
        assertEquals("application/vnd.ms-excel",
                extractData.getValues("Content-Type")[0]);
        assertEquals("たぐ", extractData.getValues("Keywords")[0]);
        assertEquals("2012-05-18T22:48:52Z",
                extractData.getValues("Last-Save-Date")[0]);
    }

    public void test_getTika_msexcelx() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.xlsx");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (final String key : extractData.getKeySet()) {
            logger.info(key + "="
                    + String.join("|", extractData.getValues(key)));
        }
        assertEquals("じょうたい", extractData.getValues("Content-Status")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("さぶたいとる", extractData.getValues("cp:subject")[0]);
        assertEquals("Microsoft Excel",
                extractData.getValues("Application-Name")[0]);
        assertEquals("太郎", extractData.getValues("Author")[0]);
        assertEquals("2012-05-18T22:50:00Z",
                extractData.getValues("Last-Modified")[0]);
        assertEquals("14.0300", extractData.getValues("Application-Version")[0]);
        assertEquals("2012-05-18T22:50:00Z", extractData.getValues("date")[0]);
        assertEquals("かいしゃ", extractData.getValues("publisher")[0]);
        assertEquals("太郎", extractData.getValues("creator")[0]);
        assertEquals("1997-01-08T22:48:59Z",
                extractData.getValues("Creation-Date")[0]);
        assertEquals("たいとる", extractData.getValues("title")[0]);
        assertEquals("ぶんるい", extractData.getValues("Category")[0]);
        assertEquals("false", extractData.getValues("protected")[0]);
        assertEquals("こめんと", extractData.getValues("description")[0]);
        assertEquals("花子", extractData.getValues("Manager")[0]);
        assertEquals(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                extractData.getValues("Content-Type")[0]);
        assertEquals("たぐ", extractData.getValues("Keywords")[0]);

    }

    public void test_getTika_mspowerpoint() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.ppt");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (final String key : extractData.getKeySet()) {
            logger.info(key + "="
                    + String.join("|", extractData.getValues(key)));
        }
        assertEquals("2", extractData.getValues("Revision-Number")[0]);
        assertEquals("こめんと", extractData.getValues("Comments")[0]);
        assertEquals("1", extractData.getValues("Slide-Count")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("さぶたいとる", extractData.getValues("cp:subject")[0]);
        assertEquals("Microsoft PowerPoint",
                extractData.getValues("Application-Name")[0]);
        assertEquals("太郎", extractData.getValues("Author")[0]);
        assertEquals("1", extractData.getValues("xmpTPg:NPages")[0]);
        assertEquals("1", extractData.getValues("Word-Count")[0]);
        assertEquals("1126220000", extractData.getValues("Edit-Time")[0]);
        assertEquals("2009-06-26T21:44:55Z",
                extractData.getValues("Creation-Date")[0]);
        assertEquals("たいとる", extractData.getValues("title")[0]);
        assertEquals("ぶんるい", extractData.getValues("Category")[0]);
        assertEquals("花子", extractData.getValues("Manager")[0]);
        assertEquals("かいしゃ", extractData.getValues("Company")[0]);
        assertEquals("application/vnd.ms-powerpoint",
                extractData.getValues("Content-Type")[0]);
        assertEquals("たぐ", extractData.getValues("Keywords")[0]);
        assertEquals("2012-05-18T22:46:36Z",
                extractData.getValues("Last-Save-Date")[0]);
    }

    public void test_getTika_mspowerpointx() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.pptx");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (final String key : extractData.getKeySet()) {
            logger.info(key + "="
                    + String.join("|", extractData.getValues(key)));
        }
        assertEquals("2", extractData.getValues("Revision-Number")[0]);
        assertEquals("じょうたい", extractData.getValues("Content-Status")[0]);
        assertEquals("1", extractData.getValues("Slide-Count")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("さぶたいとる", extractData.getValues("cp:subject")[0]);
        assertEquals("Microsoft Office PowerPoint",
                extractData.getValues("Application-Name")[0]);
        assertEquals("太郎", extractData.getValues("Author")[0]);
        assertEquals("2012-05-18T22:47:45Z",
                extractData.getValues("Last-Modified")[0]);
        assertEquals("14.0000", extractData.getValues("Application-Version")[0]);
        assertEquals("2012-05-18T22:47:45Z", extractData.getValues("date")[0]);
        assertEquals("かいしゃ", extractData.getValues("publisher")[0]);
        assertEquals("太郎", extractData.getValues("creator")[0]);
        assertEquals("1", extractData.getValues("xmpTPg:NPages")[0]);
        assertEquals("1", extractData.getValues("Word-Count")[0]);
        assertEquals("2009-06-26T21:44:55Z",
                extractData.getValues("Creation-Date")[0]);
        assertEquals("たいとる", extractData.getValues("title")[0]);
        assertEquals("ぶんるい", extractData.getValues("Category")[0]);
        assertEquals("こめんと", extractData.getValues("description")[0]);
        assertEquals("花子", extractData.getValues("Manager")[0]);
        assertEquals(
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                extractData.getValues("Content-Type")[0]);
        assertEquals("たぐ", extractData.getValues("Keywords")[0]);
        assertEquals("画面に合わせる (4:3)",
                extractData.getValues("Presentation-Format")[0]);
        assertEquals("1", extractData.getValues("Paragraph-Count")[0]);
    }

    public void test_getTika_zip() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/zip/test.zip");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("テキスト"));
    }

    public void test_getTika_zip_bom() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/zip/test_size.zip");
        tikaExtractor.maxCompressionRatio = 1;
        tikaExtractor.maxUncompressionSize = 10000;
        try {
            tikaExtractor.getText(in, null);
            fail();
        } catch (final ExtractException e) {
            logger.info(e.getMessage());
        }
    }

    public void test_getTika_tar() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/tar/test.tar");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("テキスト"));
    }

    public void test_getTika_targz() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/gz/test.tar.gz");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("テキスト"));
    }

    public void test_getTika_xml() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_utf8.xml");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertEquals(extractData.getValues("title")[0], "タイトル");
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_xml_sjis() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_sjis.xml");
        final Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test_sjis.xml");
        final ExtractData extractData = tikaExtractor.getText(in, params);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_xml_entity() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test_entity.xml");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_xml_broken() throws UnsupportedEncodingException {
        final InputStream in = new ByteArrayInputStream(
                "<?xml encoding=\"UTF-8\"/><hoge>テスト<br></hoge>"
                        .getBytes(Constants.UTF_8));
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_java() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/program/test.java");
        final Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "text/plain");
        params.put("resourceName", "test.java");
        final ExtractData extractData = tikaExtractor.getText(in, params);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_java_1() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/program/test.java");
        final Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "text/plain");
        final ExtractData extractData = tikaExtractor.getText(in, params);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_java_2() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/program/test.java");
        final Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test.java");
        final ExtractData extractData = tikaExtractor.getText(in, params);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_java_3() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/program/test.java");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_java_4() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/program/test.java");
        final Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "text/x-java-source");
        final ExtractData extractData = tikaExtractor.getText(in, params);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_js() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/program/test.js");
        final Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "text/plain");
        params.put("resourceName", "test.js");
        final ExtractData extractData = tikaExtractor.getText(in, params);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_c() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/program/test.c");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_cpp() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/program/test.cpp");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_h() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/program/test.h");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_hpp() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/program/test.hpp");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_sh() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test.sh");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_rtf() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/test.rtf");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_gif() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/image/test.gif");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Test"));
    }

    public void test_getTika_jpg() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/image/test.jpg");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Test"));
    }

    public void test_getTika_png() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/image/test.png");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Test"));
    }

    public void test_getTika_null() {
        try {
            tikaExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }

    private Map<String, String> createParams(String url, String resoureName) {
        Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, url);
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, resoureName);
        return params;
    }

    public void test_getPdfPassword_null() {
        String url;
        String resourceName;

        url = null;
        resourceName = null;
        assertNull(tikaExtractor.getPassword(createParams(url, resourceName)));

        url = "http://test.com/hoge1.pdf";
        resourceName = null;
        assertNull(tikaExtractor.getPassword(createParams(url, resourceName)));

        url = "http://test.com/hoge1.pdf";
        resourceName = "hoge2.pdf";
        assertNull(tikaExtractor.getPassword(createParams(url, resourceName)));

        url = null;
        resourceName = "hoge2.pdf";
        assertNull(tikaExtractor.getPassword(createParams(url, resourceName)));
    }

    public void test_getPdfPassword() {
        String url;
        String resourceName;
        tikaExtractor.addPassword(".*hoge1.pdf", "password");
        tikaExtractor.addPassword("fuga.pdf", "PASSWORD");

        url = null;
        resourceName = null;
        assertNull(tikaExtractor.getPassword(createParams(url, resourceName)));

        url = "http://test.com/hoge1.pdf";
        resourceName = null;
        assertEquals("password", tikaExtractor.getPassword(createParams(url, resourceName)));

        url = "http://test.com/hoge1.pdf";
        resourceName = "hoge2.pdf";
        assertEquals("password", tikaExtractor.getPassword(createParams(url, resourceName)));

        url = null;
        resourceName = "hoge2.pdf";
        assertNull(tikaExtractor.getPassword(createParams(url, resourceName)));

        url = null;
        resourceName = "hoge1.pdf";
        assertEquals("password", tikaExtractor.getPassword(createParams(url, resourceName)));

        url = "http://test.com/fuga.pdf";
        resourceName = null;
        assertNull(tikaExtractor.getPassword(createParams(url, resourceName)));

        url = null;
        resourceName = "fuga.pdf";
        assertEquals("PASSWORD", tikaExtractor.getPassword(createParams(url, resourceName)));
    }
}
