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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author shinsuke
 *
 */
public class TikaExtractorTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(TikaExtractorTest.class);

    public TikaExtractor tikaExtractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.singleton("mimeTypeHelper", MimeTypeHelperImpl.class)
                .singleton("tikaExtractor", TikaExtractor.class)
                .<ExtractorFactory> singleton("extractorFactory", ExtractorFactory.class, factory -> {
                    TikaExtractor tikaExtractor = container.getComponent("tikaExtractor");
                    factory.addExtractor("text/plain", tikaExtractor);
                    factory.addExtractor("text/html", tikaExtractor);
                })//
        ;

        tikaExtractor = container.getComponent("tikaExtractor");
    }

    @Test
    public void test_getTika_text() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.txt");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
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

    @Test
    public void test_getTika_html() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_html_sjis() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_sjis.html");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_html_fragment() {
        final String htmlFragment = "<p>これはテストです</p><div>追加テキスト</div>";
        final InputStream in = new ByteArrayInputStream(htmlFragment.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("これはテストです"));
        assertTrue(content.contains("追加テキスト"));
    }

    @Test
    public void test_getTika_html_fragment_strip_tags() {
        final String htmlFragment =
                "<p style=\"text-align: right;\"><button class=\"aui-button\">Create</button></p><h2>Title</h2><table><tr><td>Cell</td></tr></table>";
        final InputStream in = new ByteArrayInputStream(htmlFragment.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        final Map<String, String> params = new HashMap<>();
        params.put(TikaExtractor.STRIP_HTML_TAGS, "true");
        final ExtractData extractData = tikaExtractor.getText(in, params);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        // Log detected content type for debugging
        final String contentType =
                extractData.getValues(ExtractData.CONTENT_TYPE) != null ? String.join(",", extractData.getValues(ExtractData.CONTENT_TYPE))
                        : "null";
        logger.info("Detected content type: {}", contentType);
        logger.info("Stripped content: {}", content);
        assertFalse(content.contains("<"));
        assertFalse(content.contains(">"));
        assertTrue(content.contains("Create"));
        assertTrue(content.contains("Title"));
        assertTrue(content.contains("Cell"));
    }

    @Test
    public void test_getTika_html_fragment_strip_tags_disabled() {
        final String htmlFragment = "<p style=\"text-align: right;\"><button class=\"aui-button\">Create</button></p><h2>Title</h2>";
        final InputStream in = new ByteArrayInputStream(htmlFragment.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        // No STRIP_HTML_TAGS param - default behavior
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info("Content without stripping: {}", content);
        // Content may or may not contain tags depending on Tika's behavior
        assertTrue(content.contains("Create"));
        assertTrue(content.contains("Title"));
    }

    @Test
    public void test_getTika_msword() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/msoffice/test.doc");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (final String key : extractData.getKeySet()) {
            logger.info("{}={}", key, String.join("|", extractData.getValues(key)));
        }
        assertEquals("4", extractData.getValues("cp:revision")[0]);
        assertEquals("こめんと", extractData.getValues("w:Comments")[0]);
        assertEquals("たぐ|さぶたいとる", String.join("|", extractData.getValues("dc:subject")));
        assertEquals("Microsoft Office Word", extractData.getValues("extended-properties:Application")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("太郎", extractData.getValues("dc:creator")[0]);
        assertEquals("かいしゃ", extractData.getValues("extended-properties:Company")[0]);
        assertEquals("1", extractData.getValues("xmpTPg:NPages")[0]);
        assertEquals("2009-06-26T21:41:00Z", extractData.getValues("dcterms:created")[0]);
        assertEquals("2012-05-18T22:45:00Z", extractData.getValues("dcterms:modified")[0]);
        assertEquals("3", extractData.getValues("meta:character-count")[0]);
        assertEquals("Normal", extractData.getValues("extended-properties:Template")[0]);
        assertEquals("たいとる", extractData.getValues("dc:title")[0]);
        assertEquals("3000000000", extractData.getValues("extended-properties:TotalTime")[0]);
        assertEquals("たぐ", extractData.getValues("meta:keyword")[0]);
        assertEquals("花子", extractData.getValues("extended-properties:Manager")[0]);
        assertEquals("ぶんるい", extractData.getValues("cp:category")[0]);
        assertEquals("1", extractData.getValues("meta:page-count")[0]);
        assertEquals("application/msword", extractData.getValues("Content-Type")[0]);
    }

    @Test
    public void test_getTika_mswordx() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/msoffice/test.docx");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (final String key : extractData.getKeySet()) {
            logger.info("{}={}", key, String.join("|", extractData.getValues(key)));
        }
        assertEquals("2", extractData.getValues("cp:revision")[0]);
        assertEquals("こめんと", extractData.getValues("dc:description")[0]);
        assertEquals("14.0000", extractData.getValues("extended-properties:AppVersion")[0]);
        assertEquals("1", extractData.getValues("meta:paragraph-count")[0]);
        assertEquals("太郎", extractData.getValues("dc:creator")[0]);
        assertEquals("かいしゃ", extractData.getValues("extended-properties:Company")[0]);
        assertEquals("2010-07-22T00:21:00Z", extractData.getValues("dcterms:created")[0]);
        assertEquals("1", extractData.getValues("meta:line-count")[0]);
        assertEquals("2012-05-18T22:44:00Z", extractData.getValues("dcterms:modified")[0]);
        assertEquals("3", extractData.getValues("meta:character-count")[0]);
        assertEquals("じょうたい", extractData.getValues("cp:contentStatus")[0]);
        assertEquals("3", extractData.getValues("meta:character-count-with-spaces")[0]);
        assertEquals("たいとる", extractData.getValues("dc:title")[0]);
        assertEquals("2", extractData.getValues("extended-properties:TotalTime")[0]);
        assertEquals("花子", extractData.getValues("extended-properties:Manager")[0]);
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", extractData.getValues("Content-Type")[0]);
        assertEquals("さぶたいとる|たぐ", String.join("|", extractData.getValues("dc:subject")));
        assertEquals("Microsoft Office Word", extractData.getValues("extended-properties:Application")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("1", extractData.getValues("xmpTPg:NPages")[0]);
        assertEquals("Normal", extractData.getValues("extended-properties:Template")[0]);
        assertEquals("None", extractData.getValues("extended-properties:DocSecurityString")[0]);
        assertEquals("たぐ", extractData.getValues("meta:keyword")[0]);
        assertEquals("ぶんるい", extractData.getValues("cp:category")[0]);
        assertEquals("1", extractData.getValues("meta:page-count")[0]);
        assertEquals("かいしゃ", extractData.getValues("dc:publisher")[0]);
    }

    @Test
    public void test_getTika_msexcel() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/msoffice/test.xls");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (final String key : extractData.getKeySet()) {
            logger.info("{}={}", key, String.join("|", extractData.getValues(key)));
        }
        assertEquals("こめんと", extractData.getValues("w:Comments")[0]);
        assertEquals("たぐ|さぶたいとる", String.join("|", extractData.getValues("dc:subject")));
        assertEquals("Microsoft Excel", extractData.getValues("extended-properties:Application")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("太郎", extractData.getValues("dc:creator")[0]);
        assertEquals("かいしゃ", extractData.getValues("extended-properties:Company")[0]);
        assertEquals("1997-01-08T22:48:59Z", extractData.getValues("dcterms:created")[0]);
        assertEquals("2012-05-18T22:48:52Z", extractData.getValues("dcterms:modified")[0]);
        assertEquals("たいとる", extractData.getValues("dc:title")[0]);
        assertEquals("0", extractData.getValues("extended-properties:TotalTime")[0]);
        assertEquals("たぐ", extractData.getValues("meta:keyword")[0]);
        assertEquals("花子", extractData.getValues("extended-properties:Manager")[0]);
        assertEquals("ぶんるい", extractData.getValues("cp:category")[0]);
        assertEquals("application/vnd.ms-excel", extractData.getValues("Content-Type")[0]);

    }

    @Test
    public void test_getTika_msexcelx() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/msoffice/test.xlsx");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (final String key : extractData.getKeySet()) {
            logger.info("{}={}", key, String.join("|", extractData.getValues(key)));
        }
        assertEquals("こめんと", extractData.getValues("dc:description")[0]);
        assertEquals("14.0300", extractData.getValues("extended-properties:AppVersion")[0]);
        assertEquals("さぶたいとる|たぐ", String.join("|", extractData.getValues("dc:subject")));
        assertEquals("Microsoft Excel", extractData.getValues("extended-properties:Application")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("太郎", extractData.getValues("dc:creator")[0]);
        assertEquals("かいしゃ", extractData.getValues("extended-properties:Company")[0]);
        assertEquals("1997-01-08T22:48:59Z", extractData.getValues("dcterms:created")[0]);
        assertEquals("2012-05-18T22:50:00Z", extractData.getValues("dcterms:modified")[0]);
        assertEquals("false", extractData.getValues("protected")[0]);
        assertEquals("じょうたい", extractData.getValues("cp:contentStatus")[0]);
        assertEquals("たいとる", extractData.getValues("dc:title")[0]);
        assertEquals("None", extractData.getValues("extended-properties:DocSecurityString")[0]);
        assertEquals("たぐ", extractData.getValues("meta:keyword")[0]);
        assertEquals("花子", extractData.getValues("extended-properties:Manager")[0]);
        assertEquals("ぶんるい", extractData.getValues("cp:category")[0]);
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", extractData.getValues("Content-Type")[0]);
        assertEquals("かいしゃ", extractData.getValues("dc:publisher")[0]);
    }

    @Test
    public void test_getTika_mspowerpoint() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/msoffice/test.ppt");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (final String key : extractData.getKeySet()) {
            logger.info("{}={}", key, String.join("|", extractData.getValues(key)));
        }
        assertEquals("2", extractData.getValues("cp:revision")[0]);
        assertEquals("こめんと", extractData.getValues("w:Comments")[0]);
        assertEquals("1", extractData.getValues("meta:word-count")[0]);
        assertEquals("たぐ|さぶたいとる", String.join("|", extractData.getValues("dc:subject")));
        assertEquals("Microsoft PowerPoint", extractData.getValues("extended-properties:Application")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("太郎", extractData.getValues("dc:creator")[0]);
        assertEquals("かいしゃ", extractData.getValues("extended-properties:Company")[0]);
        assertEquals("1", extractData.getValues("meta:slide-count")[0]);
        assertEquals("1", extractData.getValues("xmpTPg:NPages")[0]);
        assertEquals("2009-06-26T21:44:55Z", extractData.getValues("dcterms:created")[0]);
        assertEquals("2012-05-18T22:46:36Z", extractData.getValues("dcterms:modified")[0]);
        assertEquals("たいとる", extractData.getValues("dc:title")[0]);
        assertEquals("1126220000", extractData.getValues("extended-properties:TotalTime")[0]);
        assertEquals("たぐ", extractData.getValues("meta:keyword")[0]);
        assertEquals("花子", extractData.getValues("extended-properties:Manager")[0]);
        assertEquals("ぶんるい", extractData.getValues("cp:category")[0]);
        assertEquals("application/vnd.ms-powerpoint", extractData.getValues("Content-Type")[0]);
    }

    @Test
    public void test_getTika_mspowerpointx() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/msoffice/test.pptx");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (final String key : extractData.getKeySet()) {
            logger.info("{}={}", key, String.join("|", extractData.getValues(key)));
        }
        assertEquals("2", extractData.getValues("cp:revision")[0]);
        assertEquals("こめんと", extractData.getValues("dc:description")[0]);
        assertEquals("14.0000", extractData.getValues("extended-properties:AppVersion")[0]);
        assertEquals("1", extractData.getValues("meta:paragraph-count")[0]);
        assertEquals("1", extractData.getValues("meta:word-count")[0]);
        assertEquals("さぶたいとる|たぐ", String.join("|", extractData.getValues("dc:subject")));
        assertEquals("画面に合わせる (4:3)", extractData.getValues("extended-properties:PresentationFormat")[0]);
        assertEquals("Microsoft Office PowerPoint", extractData.getValues("extended-properties:Application")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("太郎", extractData.getValues("dc:creator")[0]);
        assertEquals("かいしゃ", extractData.getValues("extended-properties:Company")[0]);
        assertEquals("1", extractData.getValues("meta:slide-count")[0]);
        assertEquals("1", extractData.getValues("xmpTPg:NPages")[0]);
        assertEquals("2009-06-26T21:44:55Z", extractData.getValues("dcterms:created")[0]);
        assertEquals("2012-05-18T22:47:45Z", extractData.getValues("dcterms:modified")[0]);
        assertEquals("じょうたい", extractData.getValues("cp:contentStatus")[0]);
        assertEquals("たいとる", extractData.getValues("dc:title")[0]);
        assertEquals("None", extractData.getValues("extended-properties:DocSecurityString")[0]);
        assertEquals("たぐ", extractData.getValues("meta:keyword")[0]);
        assertEquals("花子", extractData.getValues("extended-properties:Manager")[0]);
        assertEquals("ぶんるい", extractData.getValues("cp:category")[0]);
        assertEquals("application/vnd.openxmlformats-officedocument.presentationml.presentation", extractData.getValues("Content-Type")[0]);
        assertEquals("かいしゃ", extractData.getValues("dc:publisher")[0]);
    }

    @Test
    public void test_getTika_zip() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/zip/test.zip");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("テキスト"));
    }

    @Test
    public void test_getTika_zip_bom() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/zip/test_size.zip");
        tikaExtractor.maxCompressionRatio = 1;
        tikaExtractor.maxUncompressionSize = 10000;
        try {
            tikaExtractor.getText(in, null);
            fail();
        } catch (final ExtractException e) {
            logger.info(e.getMessage());
        }
    }

    @Test
    public void test_getTika_tar() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/tar/test.tar");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("テキスト"));
    }

    @Test
    public void test_getTika_targz() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/gz/test.tar.gz");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("テキスト"));
    }

    @Test
    public void test_getTika_xml() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.xml");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertEquals(extractData.getValues("dc:title")[0], "タイトル");
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_xml_sjis() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_sjis.xml");
        final Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test_sjis.xml");
        final ExtractData extractData = tikaExtractor.getText(in, params);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_xml_entity() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_entity.xml");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_xml_broken() throws UnsupportedEncodingException {
        final InputStream in = new ByteArrayInputStream("<?xml encoding=\"UTF-8\"/><hoge>テスト<br></hoge>".getBytes(Constants.UTF_8));
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_java() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/program/test.java");
        final Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "text/plain");
        params.put("resourceName", "test.java");
        final ExtractData extractData = tikaExtractor.getText(in, params);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_java_1() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/program/test.java");
        final Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "text/plain");
        final ExtractData extractData = tikaExtractor.getText(in, params);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_java_2() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/program/test.java");
        final Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test.java");
        final ExtractData extractData = tikaExtractor.getText(in, params);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_java_3() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/program/test.java");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_java_4() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/program/test.java");
        final Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "text/x-java-source");
        final ExtractData extractData = tikaExtractor.getText(in, params);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_js() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/program/test.js");
        final Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "text/plain");
        params.put("resourceName", "test.js");
        final ExtractData extractData = tikaExtractor.getText(in, params);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_c() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/program/test.c");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_cpp() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/program/test.cpp");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_h() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/program/test.h");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_hpp() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/program/test.hpp");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_sh() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.sh");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_rtf() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.rtf");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getTika_gif() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/image/test.gif");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Test"));
    }

    @Test
    public void test_getTika_jpg() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/image/test.jpg");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Test"));
    }

    @Test
    public void test_getTika_png() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/image/test.png");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Test"));
    }

    @Test
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
        params.put(ExtractData.RESOURCE_NAME_KEY, resoureName);
        return params;
    }

    @Test
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

    @Test
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

    /**
     * Verifies that running many concurrent extractions does not leave
     * {@link System#out}/{@link System#err} permanently redirected. The previous
     * implementation swapped the JVM streams without synchronization, so two
     * threads racing through the swap could lose the original references.
     */
    @Test
    public void test_concurrentExtraction_doesNotCorruptSystemStreams() throws Exception {
        final PrintStream originalOut = System.out;
        final PrintStream originalErr = System.err;

        final int threadCount = 16;
        final int iterations = 8;
        final ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch start = new CountDownLatch(1);
        final List<Throwable> failures = java.util.Collections.synchronizedList(new ArrayList<>());
        try {
            for (int i = 0; i < threadCount; i++) {
                pool.submit(() -> {
                    try {
                        start.await();
                        for (int j = 0; j < iterations; j++) {
                            final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.txt");
                            try {
                                tikaExtractor.getText(in, null);
                            } finally {
                                CloseableUtil.closeQuietly(in);
                            }
                        }
                    } catch (final Throwable t) {
                        failures.add(t);
                    }
                });
            }
            start.countDown();
            pool.shutdown();
            assertTrue(pool.awaitTermination(60, TimeUnit.SECONDS));
        } finally {
            pool.shutdownNow();
        }
        Assertions.assertTrue(failures.isEmpty(), "concurrent extractions threw: " + failures);
        Assertions.assertSame(originalOut, System.out, "System.out must be restored to its original reference");
        Assertions.assertSame(originalErr, System.err, "System.err must be restored to its original reference");
    }

    /**
     * Verifies that an exception thrown during extraction still restores
     * {@link System#out}/{@link System#err}.
     */
    @Test
    public void test_systemStreamsRestoredOnException() {
        final PrintStream originalOut = System.out;
        final PrintStream originalErr = System.err;
        try {
            tikaExtractor.getText(null, null);
            Assertions.fail("expected CrawlerSystemException");
        } catch (final CrawlerSystemException expected) {
            // null input always throws synchronously, before muting; this exercises
            // the simplest restore path.
        }
        Assertions.assertSame(originalOut, System.out);
        Assertions.assertSame(originalErr, System.err);

        // Also exercise the muted path: feed a non-byte stream so the muting branch
        // runs, but force the underlying stream to throw mid-read.
        final InputStream broken = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("boom");
            }
        };
        try {
            tikaExtractor.getText(broken, null);
            Assertions.fail("expected ExtractException");
        } catch (final ExtractException expected) {
            // ok
        }
        Assertions.assertSame(originalOut, System.out, "System.out must be restored after a thrown extraction");
        Assertions.assertSame(originalErr, System.err, "System.err must be restored after a thrown extraction");
    }

    /**
     * When the input is large enough for the on-disk staging file to be created, the
     * old implementation also let Tika spool the same bytes into a second temp file
     * (apache-tika-*). With the fix only the outer staging file should appear.
     */
    @Test
    public void test_dfosSpilledToDisk_noDoubleTempFile() throws Exception {
        final Path tempDir = Files.createTempDirectory("tikaExtractor-test-");
        final String originalTmp = System.getProperty("java.io.tmpdir");
        System.setProperty("java.io.tmpdir", tempDir.toAbsolutePath().toString());
        try {
            // Build an input large enough to ensure the staging file is created. The
            // payload is plain text repeated to about 4 MB.
            final byte[] chunk = "Concurrent extraction stress payload テスト 1234567890\n".getBytes(StandardCharsets.UTF_8);
            final int targetBytes = 4 * 1024 * 1024;
            final int repeats = (targetBytes / chunk.length) + 1;
            final byte[] data = new byte[chunk.length * repeats];
            for (int i = 0; i < repeats; i++) {
                System.arraycopy(chunk, 0, data, i * chunk.length, chunk.length);
            }

            // BufferedInputStream is not a ByteArrayInputStream, so the on-disk path runs.
            final InputStream in = new java.io.BufferedInputStream(new java.io.ByteArrayInputStream(data));
            final ExtractData result = tikaExtractor.getText(in, null);
            assertNotNull(result);
            CloseableUtil.closeQuietly(in);

            final List<String> tikaTempFilesSeen = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(tempDir)) {
                for (final Path p : stream) {
                    final String name = p.getFileName().toString();
                    if (name.startsWith("apache-tika-")) {
                        tikaTempFilesSeen.add(name);
                    }
                }
            }
            Assertions.assertTrue(tikaTempFilesSeen.isEmpty(),
                    "Tika should not have spooled a second temp file, but saw: " + tikaTempFilesSeen);
        } finally {
            if (originalTmp != null) {
                System.setProperty("java.io.tmpdir", originalTmp);
            }
            // Clean up any leftover files (deleteInBackground may still be racing).
            Thread.sleep(200);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(tempDir)) {
                for (final Path p : stream) {
                    try {
                        Files.deleteIfExists(p);
                    } catch (final IOException ignore) {
                        // best effort
                    }
                }
            }
            Files.deleteIfExists(tempDir);
        }
    }

    /**
     * For a small {@link ByteArrayInputStream}, neither the outer staging file nor
     * Tika's internal spool should be created.
     */
    @Test
    public void test_dfosInMemory_noTempFileCreated() throws Exception {
        final Path tempDir = Files.createTempDirectory("tikaExtractor-test-");
        final String originalTmp = System.getProperty("java.io.tmpdir");
        System.setProperty("java.io.tmpdir", tempDir.toAbsolutePath().toString());
        try {
            final byte[] data = "Small inline payload テスト".getBytes(StandardCharsets.UTF_8);
            final ByteArrayInputStream in = new ByteArrayInputStream(data);
            final ExtractData result = tikaExtractor.getText(in, null);
            assertNotNull(result);
            CloseableUtil.closeQuietly(in);

            final List<String> leftover = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(tempDir)) {
                for (final Path p : stream) {
                    final String name = p.getFileName().toString();
                    if (name.startsWith("tikaExtractor-") || name.startsWith("apache-tika-")) {
                        leftover.add(name);
                    }
                }
            }
            Assertions.assertTrue(leftover.isEmpty(), "In-memory path must not create temp files, but saw: " + leftover);
        } finally {
            if (originalTmp != null) {
                System.setProperty("java.io.tmpdir", originalTmp);
            }
            Thread.sleep(100);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(tempDir)) {
                for (final Path p : stream) {
                    try {
                        Files.deleteIfExists(p);
                    } catch (final IOException ignore) {
                        // best effort
                    }
                }
            }
            Files.deleteIfExists(tempDir);
        }
    }

    /**
     * When muting is disabled, {@link System#out}/{@link System#err} must remain the
     * caller-visible streams throughout extraction.
     */
    @Test
    public void test_setMuteSystemStreams_false_doesNotMute() throws Exception {
        final PrintStream originalOut = System.out;
        final PrintStream originalErr = System.err;
        tikaExtractor.setMuteSystemStreams(false);
        try {
            final java.io.ByteArrayOutputStream capture = new java.io.ByteArrayOutputStream();
            final PrintStream tap = new PrintStream(capture, true);
            System.setOut(tap);
            System.setErr(tap);
            try {
                final PrintStream beforeOut = System.out;
                final PrintStream beforeErr = System.err;
                final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.txt");
                try {
                    tikaExtractor.getText(in, null);
                } finally {
                    CloseableUtil.closeQuietly(in);
                }
                Assertions.assertSame(beforeOut, System.out, "System.out must not be swapped when muting is disabled");
                Assertions.assertSame(beforeErr, System.err, "System.err must not be swapped when muting is disabled");
            } finally {
                System.setOut(originalOut);
                System.setErr(originalErr);
            }
        } finally {
            tikaExtractor.setMuteSystemStreams(true);
        }
        Assertions.assertSame(originalOut, System.out);
        Assertions.assertSame(originalErr, System.err);
    }
}
