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

import java.io.BufferedInputStream;
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
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
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

    // the password of test_pass.pdf is "pass"; the PasswordProvider of the parse context hands it to Tika
    @Test
    public void test_getTika_pdf_pass() {
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "http://example.com/test_pass.pdf");
        try {
            tikaExtractor.getText(ResourceUtil.getResourceAsStream("extractor/test_pass.pdf"), params);
            fail();
        } catch (final ExtractException e) {
            logger.info(e.getMessage());
        }

        tikaExtractor.addPassword(".*test_pass.pdf", "pass");
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_pass.pdf");
        final String content = tikaExtractor.getText(in, params).getContent();
        CloseableUtil.closeQuietly(in);
        assertTrue(content.contains("テスト"));
    }

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
        assertEquals("false", extractData.getValues("msoffice:excel:protected-worksheet")[0]);
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
            // the limit is what stops it, not another failure of the parse
            boolean zipBomb = false;
            for (Throwable t = e; t != null; t = t.getCause()) {
                zipBomb |= t.getMessage() != null && t.getMessage().contains("Suspected zip bomb");
            }
            Assertions.assertTrue(zipBomb, "no 'Suspected zip bomb' in the cause chain of " + e);
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
    public void test_getTika_gz() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/gz/test.txt.gz");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("fcgziptoken"));
    }

    @Test
    public void test_getTika_zst() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/zst/test.txt.zst");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("fczstdtoken"));
    }

    @Test
    public void test_getTika_tarzst() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/zst/test.tar.zst");
        final ExtractData extractData = tikaExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("fcztarzstdtoken"));
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
     * Verifies that bytes written to {@link System#out}/{@link System#err} while the
     * streams are muted are not silently discarded. The previous fix swapped the
     * streams to a {@code NullOutputStream}; we now buffer them and re-emit through
     * the logger when extraction finishes so operators still see Tika parser
     * diagnostics (PDFBox font warnings, JBIG2 warnings, legacy POI debug, ...).
     */
    @Test
    public void test_systemStreamsCapturedAndReplayed() throws Exception {
        final List<String> replayedOut = java.util.Collections.synchronizedList(new ArrayList<>());
        final List<String> replayedErr = java.util.Collections.synchronizedList(new ArrayList<>());

        // Subclass that injects writes to System.out/System.err while the muting is
        // active and observes the replayed bytes through the test hook.
        final TikaExtractor spy = new TikaExtractor() {
            @Override
            protected TikaInputStream openTikaInput(final byte[] bytes, final java.io.File tempFile) throws IOException {
                System.out.println("HELLO_FROM_PARSER_OUT");
                System.err.println("HELLO_FROM_PARSER_ERR");
                return super.openTikaInput(bytes, tempFile);
            }

            @Override
            protected void onReplayCaptured(final String text, final boolean fromStderr) {
                if (fromStderr) {
                    replayedErr.add(text);
                } else {
                    replayedOut.add(text);
                }
            }
        };
        spy.init();

        final PrintStream originalOut = System.out;
        final PrintStream originalErr = System.err;

        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.txt");
        try {
            final ExtractData result = spy.getText(in, null);
            assertNotNull(result);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        // streams must be restored
        Assertions.assertSame(originalOut, System.out);
        Assertions.assertSame(originalErr, System.err);

        // captured stdout/stderr must have been replayed via the hook
        Assertions.assertFalse(replayedOut.isEmpty(), "captured stdout was not replayed");
        Assertions.assertFalse(replayedErr.isEmpty(), "captured stderr was not replayed");
        Assertions.assertTrue(String.join("\n", replayedOut).contains("HELLO_FROM_PARSER_OUT"),
                "expected captured stdout to contain the marker, got: " + replayedOut);
        Assertions.assertTrue(String.join("\n", replayedErr).contains("HELLO_FROM_PARSER_ERR"),
                "expected captured stderr to contain the marker, got: " + replayedErr);
    }

    /**
     * Locks down the encoding contract for the muted-stream capture/replay path:
     * regardless of {@link TikaExtractor#setOutputEncoding(String)}, bytes
     * written to {@link System#out}/{@link System#err} during extraction must be
     * decoded with {@link java.nio.charset.Charset#defaultCharset()} so that the
     * replayed text is a lossless round-trip of what Tika/PDFBox/POI emitted
     * (which they emit through {@link PrintStream}s the JVM wraps with the JVM
     * default charset). A different output encoding here would risk character
     * substitution for diagnostics containing non-ASCII text.
     */
    @Test
    public void test_capturedSystemStreams_useDefaultCharsetRegardlessOfOutputEncoding() throws Exception {
        final String marker = "Türkçe-日本語-✓";
        // Round-trip is only meaningful when the JVM default charset can actually
        // represent the marker. On platforms whose default is e.g. ISO-8859-1
        // (rare on Java 21+), skip rather than report a false failure.
        org.junit.jupiter.api.Assumptions.assumeTrue(java.nio.charset.Charset.defaultCharset().newEncoder().canEncode(marker),
                "skipping: JVM default charset " + java.nio.charset.Charset.defaultCharset() + " cannot represent the test marker");
        final List<String> replayedOut = java.util.Collections.synchronizedList(new ArrayList<>());

        final TikaExtractor spy = new TikaExtractor() {
            @Override
            protected TikaInputStream openTikaInput(final byte[] bytes, final java.io.File tempFile) throws IOException {
                System.out.println(marker);
                return super.openTikaInput(bytes, tempFile);
            }

            @Override
            protected void onReplayCaptured(final String text, final boolean fromStderr) {
                if (!fromStderr) {
                    replayedOut.add(text);
                }
            }
        };
        // Pick an outputEncoding that cannot represent every code point in the
        // marker; if the capture/replay path were (incorrectly) using
        // outputEncoding, characters such as '日' would be substituted with '?'.
        spy.setOutputEncoding("ISO-8859-1");
        spy.init();

        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.txt");
        try {
            spy.getText(in, null);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        Assertions.assertFalse(replayedOut.isEmpty(), "captured stdout was not replayed");
        final String joined = String.join("\n", replayedOut);
        Assertions.assertTrue(joined.contains(marker),
                "replayed stdout must round-trip non-ASCII text via the JVM default charset, got: " + joined);
    }

    /**
     * Verifies that the bounded capture buffer caps at its configured size and
     * appends a truncation marker once instead of growing without bound.
     */
    @Test
    public void test_boundedCaptureBuffer_truncatesAtCapacity() throws Exception {
        final TikaExtractor.BoundedByteArrayOutputStream buf = new TikaExtractor.BoundedByteArrayOutputStream(16);
        // Write more than the cap in a single call.
        final byte[] payload = new byte[64];
        java.util.Arrays.fill(payload, (byte) 'x');
        buf.write(payload, 0, payload.length);
        // Subsequent writes are dropped.
        buf.write("MORE".getBytes(StandardCharsets.UTF_8));
        buf.write('!');

        final String text = new String(buf.toByteArray(), StandardCharsets.UTF_8);
        // 16 'x' bytes + the truncation marker; "MORE" / '!' must be discarded.
        Assertions.assertTrue(text.startsWith("xxxxxxxxxxxxxxxx"), "expected 16 bytes of payload, got: " + text);
        Assertions.assertTrue(text.contains("truncated"), "expected truncation marker, got: " + text);
        Assertions.assertFalse(text.contains("MORE"), "post-cap writes must be dropped");
        Assertions.assertFalse(text.contains("!"), "post-cap byte writes must be dropped");
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

    @Test
    public void test_createParseContext_tesseractOCRConfig() throws Exception {
        assertNull(tikaExtractor.createParseContext(null, null).get(TesseractOCRConfig.class));

        final TesseractOCRConfig defaultConfig = new TesseractOCRConfig();
        defaultConfig.setLanguage("jpn");
        tikaExtractor.setTesseractOCRConfig(defaultConfig);
        Assertions.assertSame(defaultConfig, tikaExtractor.createParseContext(null, null).get(TesseractOCRConfig.class));
        Assertions.assertSame(defaultConfig, tikaExtractor.createParseContext(null, new HashMap<>()).get(TesseractOCRConfig.class));

        final Map<String, String> params = new HashMap<>();
        params.put(TikaExtractor.TIKA_TESSERACT_CONFIG, "extractor/tesseract.properties");
        final TesseractOCRConfig config = tikaExtractor.createParseContext(null, params).get(TesseractOCRConfig.class);
        assertEquals("eng", config.getLanguage());
        assertTrue(config.isSkipOcr());
    }

    @Test
    public void test_init_tesseractOCRConfigComponent() {
        final StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.<TesseractOCRConfig> singleton("tesseractOCRConfig", TesseractOCRConfig.class, config -> config.setSkipOcr(true))
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class)
                .singleton("tikaExtractor", TikaExtractor.class);
        final TikaExtractor extractor = container.getComponent("tikaExtractor");
        final TesseractOCRConfig config = extractor.createParseContext(null, null).get(TesseractOCRConfig.class);
        Assertions.assertSame(container.getComponent("tesseractOCRConfig"), config);
        assertTrue(config.isSkipOcr());
    }

    @Test
    public void test_getText_retrySkipsOcr() {
        final List<ParseContext> contexts = new ArrayList<>();
        final TikaExtractor extractor = new TikaExtractor() {
            @Override
            protected ParseContext createParseContext(final Parser parser, final Map<String, String> params) {
                final ParseContext context = super.createParseContext(parser, params);
                contexts.add(context);
                return context;
            }
        };
        extractor.init();
        final TesseractOCRConfig defaultConfig = new TesseractOCRConfig();
        extractor.setTesseractOCRConfig(defaultConfig);
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "blank.txt");
        params.put(ExtractData.CONTENT_TYPE, "text/plain");
        extractor.getText(new ByteArrayInputStream(" ".getBytes(StandardCharsets.UTF_8)), params);
        assertEquals(2, contexts.size());
        Assertions.assertSame(defaultConfig, contexts.get(0).get(TesseractOCRConfig.class));
        assertTrue(contexts.get(1).get(TesseractOCRConfig.class).isSkipOcr());
    }

    // The only ASCII byte runs of this text are the CRLF of the line ends, 2 bytes each. Tika 4 alone takes this text
    // in Shift_JIS for UTF-16 (the lead bytes of all the characters are in the same column of 2 bytes).
    private static final String JAPANESE_TEXT =
            "日本語のテキストを検索システムに登録するための文章です。\r\n" + "検索エンジンはクロールした文書から文字を取り出します。\r\n" + "これはシフトジスで保存された普通のテキストファイルの例です。\r\n";

    private byte[] readResource(final String path) throws IOException {
        try (InputStream in = ResourceUtil.getResourceAsStream(path)) {
            return in.readAllBytes();
        }
    }

    /**
     * Extracts a text the way the transformers do, from an in-memory stream and from a stream that is
     * materialized into a temp file, and checks that both give the expected text.
     */
    private void assertExtracted(final String expected, final byte[] data, final String resourceName) {
        assertExtracted(expected, data, resourceName, null);
    }

    private void assertExtracted(final String expected, final byte[] data, final String resourceName, final String contentEncoding) {
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, resourceName);
        if (contentEncoding != null) {
            params.put(ExtractData.CONTENT_ENCODING, contentEncoding);
        }
        final String inMemory = tikaExtractor.getText(new ByteArrayInputStream(data), params).getContent();
        Assertions.assertTrue(inMemory.contains(expected), resourceName + " (in memory): " + inMemory);
        final String onDisk = tikaExtractor.getText(new BufferedInputStream(new ByteArrayInputStream(data)), params).getContent();
        Assertions.assertTrue(onDisk.contains(expected), resourceName + " (temp file): " + onDisk);
    }

    // Tika 4 ignores a plain "resourceName" key, and then leaves a text in a legacy charset empty
    @Test
    public void test_getText_resourceName_legacyCharsetText() throws Exception {
        final String text = JAPANESE_TEXT;
        assertExtracted("日本語のテキスト", text.getBytes("windows-31j"), "sjis.txt");
        assertExtracted("日本語のテキスト", text.getBytes("EUC-JP"), "eucjp.txt");
        final String csv = "名前,備考\n山田太郎,検索エンジンFessは日本語のテキストです\n鈴木花子,正しく取り出します\n";
        assertExtracted("山田太郎", csv.getBytes("windows-31j"), "sjis.csv");
        assertExtracted("山田太郎", csv.getBytes("EUC-JP"), "eucjp.csv");
    }

    // the crawler clients send UTF-8 as the Content-Encoding unless they are configured otherwise, whatever the charset of the file
    @Test
    public void test_getText_legacyCharsetText_utf8Hint() throws Exception {
        final String text = JAPANESE_TEXT;
        assertExtracted("日本語のテキスト", text.getBytes("windows-31j"), "sjis.txt", "UTF-8");
        assertExtracted("日本語のテキスト", text.getBytes("EUC-JP"), "eucjp.txt", "UTF-8");
        final String lf = text.replace("\r\n", "\n");
        assertExtracted("日本語のテキスト", lf.getBytes("windows-31j"), "sjis_lf.txt", "UTF-8");
        assertExtracted("日本語のテキスト", lf.getBytes("EUC-JP"), "eucjp_lf.txt", "UTF-8");
        final String russian = "Это обычный текст на русском языке для проверки поиска.\n".repeat(3);
        assertExtracted("обычный текст", russian.getBytes("windows-1251"), "ru.txt", "UTF-8");
        assertExtracted("日本語のテキスト", text.getBytes(StandardCharsets.UTF_8), "utf8.txt", "UTF-8");
    }

    // Tika 4 takes a Shift_JIS text for UTF-16 when the lead bytes of all the characters are in the same column
    @Test
    public void test_getText_shiftJisText_evenAsciiRuns() throws Exception {
        final String text = JAPANESE_TEXT;
        for (final String hint : new String[] { null, "UTF-8", "Shift_JIS" }) {
            assertExtracted("日本語のテキスト", text.getBytes("windows-31j"), "sjis.txt", hint);
        }
    }

    // the odd ASCII runs ("10", "3") of this text already mix the columns, and a space inserted in the middle would line them up
    @Test
    public void test_getText_shiftJisText_oddAsciiRuns() throws Exception {
        final String text = "売上は前年度と比較して大幅に増加しました。検索エンジンのためのクローラが文書から文字を取り出します。\r\n" + "本日の会議では、予算について検討しました。\r\n"
                + "お問い合わせは担当者までご連絡ください。製品の仕様は予告なく変更される場合があります。（10月3日）\r\n" + "売上は前年度と比較して大幅に増加しました。お問い合わせは担当者までご連絡ください。\r\n"
                + "検索エンジンのためのクローラが文書から文字を取り出します。本日の会議では、予算について検討しました。\r\n";
        for (final String hint : new String[] { null, "UTF-8" }) {
            assertExtracted("売上は前年度と比較して", text.getBytes("windows-31j"), "sjis.txt", hint);
        }
    }

    // The HTML encoding detector (tika-encoding-detector-html) reads the <meta charset> of the page. Without it, an HTML page
    // that declares its charset is reported as windows-1252 (the content-type charset, too) and a short text in another
    // charset than UTF-8 is decoded as that one.
    @Test
    public void test_getText_html_metaCharset_reported() {
        final ExtractData extractData = tikaExtractor.getText(ResourceUtil.getResourceAsStream("ajax/index.html"), null);
        assertEquals("UTF-8", extractData.getValues(ExtractData.CONTENT_ENCODING)[0]);
        assertEquals("text/html; charset=UTF-8", extractData.getValues(ExtractData.CONTENT_TYPE)[0]);
    }

    @Test
    public void test_getText_html_metaCharset_shortText() throws Exception {
        for (final String hint : new String[] { null, "UTF-8" }) {
            assertExtracted("Москва",
                    "<html><head><meta charset=\"windows-1251\"></head><body>Москва</body></html>".getBytes("windows-1251"), "ru.html",
                    hint);
            assertExtracted("naïve résumé",
                    "<html><head><meta charset=\"windows-1252\"></head><body>naïve résumé</body></html>".getBytes("windows-1252"),
                    "fr.html", hint);
        }
    }

    @Test
    public void test_getParser_afterInit() {
        final TikaExtractor extractor = new TikaExtractor();
        extractor.init();
        assertNotNull(extractor.getParser());
    }

    @Test
    public void test_getText_utf16BomText() throws Exception {
        final byte[] body = JAPANESE_TEXT.getBytes(StandardCharsets.UTF_16LE);
        final byte[] data = new byte[body.length + 2];
        data[0] = (byte) 0xFF;
        data[1] = (byte) 0xFE;
        System.arraycopy(body, 0, data, 2, body.length);
        assertExtracted("日本語のテキスト", data, "utf16.txt");
    }

    // a truncated OOXML file is salvaged only when the parse is announced with ParsingIntent
    @Test
    public void test_getText_truncatedOoxml() throws Exception {
        for (final String name : new String[] { "test.docx", "test.pptx" }) {
            final byte[] data = readResource("extractor/msoffice/" + name);
            assertExtracted("テスト", java.util.Arrays.copyOf(data, data.length * 6 / 10), name);
        }
    }

    @Test
    public void test_getText_noTikaInternalMetadata() {
        for (final String path : new String[] { "msoffice/test.docx", "msoffice/test.xlsx", "msoffice/test.pptx", "test.pdf",
                "test.txt" }) {
            final String name = path.substring(path.lastIndexOf('/') + 1);
            final Map<String, String> params = new HashMap<>();
            params.put(ExtractData.RESOURCE_NAME_KEY, name);
            final ExtractData extractData = tikaExtractor.getText(ResourceUtil.getResourceAsStream("extractor/" + path), params);
            for (final String key : extractData.getKeySet()) {
                Assertions.assertFalse(key.startsWith("tk:") || key.startsWith("X-TIKA:") || key.startsWith("zip:"), name + ": " + key);
            }
            Assertions.assertNotNull(extractData.getValues(ExtractData.CONTENT_TYPE), name);
            // Tika 4 writes it as tk:resource-name, and the legacy key is restored for the data stores
            assertEquals(name, extractData.getValues(ExtractData.RESOURCE_NAME_KEY)[0]);
        }
    }

    // the image parsers report the temp file the source is spooled to, unless the source is an in-memory stream
    @Test
    public void test_getText_noTempFileMetadata() {
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "test.jpg");
        final ExtractData extractData = tikaExtractor.getText(ResourceUtil.getResourceAsStream("extractor/image/test.jpg"), params);
        assertEquals("image/jpeg", extractData.getValues(ExtractData.CONTENT_TYPE)[0]);
        assertNotNull(extractData.getValues("Image Width"));
        for (final String name : new String[] { "File Name", "File Size", "File Modified Date" }) {
            Assertions.assertNull(extractData.getValues(name), name);
        }
    }

    // test_size.zip is rejected with the default limits, so only the configured limits can let it through
    @Test
    public void test_getText_zipBombLimitsAreConfigurable() {
        tikaExtractor.maxCompressionRatio = 100000;
        tikaExtractor.maxUncompressionSize = 1000000000L;
        final String content = tikaExtractor.getText(ResourceUtil.getResourceAsStream("extractor/zip/test_size.zip"), null).getContent();
        assertTrue(content.length() > 1000000);
    }

    @Test
    public void test_getText_emptyStream() {
        assertEquals("", tikaExtractor.getText(new ByteArrayInputStream(new byte[0]), null).getContent());
    }

    @Test
    public void test_createParseContext_tesseractTimeoutSeconds() {
        final Map<String, String> params = new HashMap<>();
        params.put(TikaExtractor.TIKA_TESSERACT_CONFIG, "extractor/tesseract_legacy.properties");
        final TesseractOCRConfig config = tikaExtractor.createParseContext(null, params).get(TesseractOCRConfig.class);
        assertEquals("eng", config.getLanguage());
        // timeoutSeconds is in seconds, timeoutMillis in milliseconds
        assertEquals(120000L, config.getTimeoutMillis());
    }

    @Test
    public void test_createParseContext_pdfParserConfig() {
        // Tika 4 turned extractIncrementalUpdateInfo on, Fess keeps it off unless it is configured
        assertFalse(tikaExtractor.createParseContext(null, null).get(PDFParserConfig.class).isExtractIncrementalUpdateInfo());

        final Map<String, String> params = new HashMap<>();
        params.put(TikaExtractor.TIKA_PDF_CONFIG, "extractor/pdf.properties");
        final PDFParserConfig config = tikaExtractor.createParseContext(null, params).get(PDFParserConfig.class);
        assertTrue(config.isSortByPosition());
        assertFalse(config.isExtractIncrementalUpdateInfo());

        params.put(TikaExtractor.TIKA_PDF_CONFIG, "extractor/pdf_incremental.properties");
        assertTrue(tikaExtractor.createParseContext(null, params).get(PDFParserConfig.class).isExtractIncrementalUpdateInfo());
    }
}
