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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ExtractData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 * 
 */
public class TikaExtractorTest extends S2TestCase {
    private static final Logger logger = LoggerFactory
        .getLogger(TikaExtractorTest.class);

    public TikaExtractor tikaExtractor;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_getTika_text() {
        InputStream in = ResourceUtil.getResourceAsStream("extractor/test.txt");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_pdf() {
        InputStream in = ResourceUtil.getResourceAsStream("extractor/test.pdf");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

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
    // IOUtils.closeQuietly(in);
    // logger.info(content);
    // assertTrue(content.contains("テスト"));
    // }

    public void test_getTika_html() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_html_sjis() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/test_sjis.html");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_msword() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/msoffice/test.doc");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (String key : extractData.getKeySet()) {
            logger.info(key + "="
                + StringUtils.join(extractData.getValues(key), "|"));
        }
        assertEquals("こめんと", extractData.getValues("Comments")[0]);
        assertEquals("さぶたいとる", extractData.getValues("cp:subject")[0]);
        assertEquals("太郎", extractData.getValues("Author")[0]);
        assertEquals(
            "2009-06-26T21:41:00Z",
            extractData.getValues("Creation-Date")[0]);
        assertEquals("たいとる", extractData.getValues("title")[0]);
        assertEquals("かいしゃ", extractData.getValues("Company")[0]);
        assertEquals("たぐ", extractData.getValues("Keywords")[0]);
        assertEquals(
            "2012-05-18T22:45:00Z",
            extractData.getValues("Last-Save-Date")[0]);
        assertEquals("4", extractData.getValues("Revision-Number")[0]);
        assertEquals("Normal", extractData.getValues("Template")[0]);
        assertEquals(
            "Microsoft Office Word",
            extractData.getValues("Application-Name")[0]);
        assertEquals("1", extractData.getValues("xmpTPg:NPages")[0]);
        assertEquals("3", extractData.getValues("Character Count")[0]);
        assertEquals(
            "application/msword",
            extractData.getValues("Content-Type")[0]);
    }

    public void test_getTika_mswordx() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/msoffice/test.docx");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (String key : extractData.getKeySet()) {
            logger.info(key + "="
                + StringUtils.join(extractData.getValues(key), "|"));
        }
        assertEquals("じょうたい", extractData.getValues("Content-Status")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("さぶたいとる", extractData.getValues("cp:subject")[0]);
        assertEquals(
            "Microsoft Office Word",
            extractData.getValues("Application-Name")[0]);
        assertEquals("太郎", extractData.getValues("Author")[0]);
        assertEquals("14.0000", extractData.getValues("Application-Version")[0]);
        assertEquals(
            "3",
            extractData.getValues("Character-Count-With-Spaces")[0]);
        assertEquals("2010-07-22T00:21:00Z", extractData.getValues("date")[0]);
        assertEquals("2", extractData.getValues("Total-Time")[0]);
        assertEquals("太郎", extractData.getValues("creator")[0]);
        assertEquals("かいしゃ", extractData.getValues("publisher")[0]);
        assertEquals(
            "2010-07-22T00:21:00Z",
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
        assertEquals(
            "2012-05-18T22:44:00Z",
            extractData.getValues("Last-Modified")[0]);
        assertEquals("1", extractData.getValues("xmpTPg:NPages")[0]);
        assertEquals("ぶんるい", extractData.getValues("Category")[0]);
        assertEquals("3", extractData.getValues("Character Count")[0]);
        assertEquals(
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            extractData.getValues("Content-Type")[0]);

    }

    public void test_getTika_msexcel() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/msoffice/test.xls");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (String key : extractData.getKeySet()) {
            logger.info(key + "="
                + StringUtils.join(extractData.getValues(key), "|"));
        }
        assertEquals("こめんと", extractData.getValues("Comments")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("さぶたいとる", extractData.getValues("cp:subject")[0]);
        assertEquals(
            "Microsoft Excel",
            extractData.getValues("Application-Name")[0]);
        assertEquals("太郎", extractData.getValues("Author")[0]);
        assertEquals(
            "1997-01-08T22:48:59Z",
            extractData.getValues("Creation-Date")[0]);
        assertEquals("ぶんるい", extractData.getValues("Category")[0]);
        assertEquals("たいとる", extractData.getValues("title")[0]);
        assertEquals("花子", extractData.getValues("Manager")[0]);
        assertEquals("かいしゃ", extractData.getValues("Company")[0]);
        assertEquals(
            "application/vnd.ms-excel",
            extractData.getValues("Content-Type")[0]);
        assertEquals("たぐ", extractData.getValues("Keywords")[0]);
        assertEquals(
            "2012-05-18T22:48:52Z",
            extractData.getValues("Last-Save-Date")[0]);
    }

    public void test_getTika_msexcelx() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/msoffice/test.xlsx");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (String key : extractData.getKeySet()) {
            logger.info(key + "="
                + StringUtils.join(extractData.getValues(key), "|"));
        }
        assertEquals("じょうたい", extractData.getValues("Content-Status")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("さぶたいとる", extractData.getValues("cp:subject")[0]);
        assertEquals(
            "Microsoft Excel",
            extractData.getValues("Application-Name")[0]);
        assertEquals("太郎", extractData.getValues("Author")[0]);
        assertEquals(
            "2012-05-18T22:50:00Z",
            extractData.getValues("Last-Modified")[0]);
        assertEquals("14.0300", extractData.getValues("Application-Version")[0]);
        assertEquals("1997-01-08T22:48:59Z", extractData.getValues("date")[0]);
        assertEquals("かいしゃ", extractData.getValues("publisher")[0]);
        assertEquals("太郎", extractData.getValues("creator")[0]);
        assertEquals(
            "1997-01-08T22:48:59Z",
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
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/msoffice/test.ppt");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (String key : extractData.getKeySet()) {
            logger.info(key + "="
                + StringUtils.join(extractData.getValues(key), "|"));
        }
        assertEquals("2", extractData.getValues("Revision-Number")[0]);
        assertEquals("こめんと", extractData.getValues("Comments")[0]);
        assertEquals("1", extractData.getValues("Slide-Count")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("さぶたいとる", extractData.getValues("cp:subject")[0]);
        assertEquals(
            "Microsoft PowerPoint",
            extractData.getValues("Application-Name")[0]);
        assertEquals("太郎", extractData.getValues("Author")[0]);
        assertEquals("1", extractData.getValues("xmpTPg:NPages")[0]);
        assertEquals("1", extractData.getValues("Word-Count")[0]);
        assertEquals("1126220000", extractData.getValues("Edit-Time")[0]);
        assertEquals(
            "2009-06-26T21:44:55Z",
            extractData.getValues("Creation-Date")[0]);
        assertEquals("たいとる", extractData.getValues("title")[0]);
        assertEquals("ぶんるい", extractData.getValues("Category")[0]);
        assertEquals("花子", extractData.getValues("Manager")[0]);
        assertEquals("かいしゃ", extractData.getValues("Company")[0]);
        assertEquals(
            "application/vnd.ms-powerpoint",
            extractData.getValues("Content-Type")[0]);
        assertEquals("たぐ", extractData.getValues("Keywords")[0]);
        assertEquals(
            "2012-05-18T22:46:36Z",
            extractData.getValues("Last-Save-Date")[0]);
    }

    public void test_getTika_mspowerpointx() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/msoffice/test.pptx");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        for (String key : extractData.getKeySet()) {
            logger.info(key + "="
                + StringUtils.join(extractData.getValues(key), "|"));
        }
        assertEquals("2", extractData.getValues("Revision-Number")[0]);
        assertEquals("じょうたい", extractData.getValues("Content-Status")[0]);
        assertEquals("1", extractData.getValues("Slide-Count")[0]);
        assertEquals("sugaya", extractData.getValues("meta:last-author")[0]);
        assertEquals("さぶたいとる", extractData.getValues("cp:subject")[0]);
        assertEquals(
            "Microsoft Office PowerPoint",
            extractData.getValues("Application-Name")[0]);
        assertEquals("太郎", extractData.getValues("Author")[0]);
        assertEquals(
            "2012-05-18T22:47:45Z",
            extractData.getValues("Last-Modified")[0]);
        assertEquals("14.0000", extractData.getValues("Application-Version")[0]);
        assertEquals("2009-06-26T21:44:55Z", extractData.getValues("date")[0]);
        assertEquals("かいしゃ", extractData.getValues("publisher")[0]);
        assertEquals("太郎", extractData.getValues("creator")[0]);
        assertEquals("1", extractData.getValues("xmpTPg:NPages")[0]);
        assertEquals("1", extractData.getValues("Word-Count")[0]);
        assertEquals(
            "2009-06-26T21:44:55Z",
            extractData.getValues("Creation-Date")[0]);
        assertEquals("たいとる", extractData.getValues("title")[0]);
        assertEquals("ぶんるい", extractData.getValues("Category")[0]);
        assertEquals("こめんと", extractData.getValues("description")[0]);
        assertEquals("花子", extractData.getValues("Manager")[0]);
        assertEquals(
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            extractData.getValues("Content-Type")[0]);
        assertEquals("たぐ", extractData.getValues("Keywords")[0]);
        assertEquals(
            "画面に合わせる (4:3)",
            extractData.getValues("Presentation-Format")[0]);
        assertEquals("1", extractData.getValues("Paragraph-Count")[0]);
    }

    public void test_getTika_zip() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/zip/test.zip");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("テキスト"));
    }

    public void test_getTika_tar() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/tar/test.tar");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("テキスト"));
    }

    public void test_getTika_targz() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/gz/test.tar.gz");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("テキスト"));
    }

    public void test_getTika_xml() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/test_utf8.xml");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertEquals(extractData.getValues("title")[0], "タイトル");
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_xml_sjis() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/test_sjis.xml");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test_sjis.xml");
        ExtractData extractData = tikaExtractor.getText(in, params);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_xml_entity() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/test_entity.xml");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_xml_broken() throws UnsupportedEncodingException {
        InputStream in =
            new ByteArrayInputStream(
                "<?xml encoding=\"UTF-8\"/><hoge>テスト<br></hoge>"
                    .getBytes(Constants.UTF_8));
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_java() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/program/test.java");
        Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "text/plain");
        params.put("resourceName", "test.java");
        ExtractData extractData = tikaExtractor.getText(in, params);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_java_1() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/program/test.java");
        Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "text/plain");
        ExtractData extractData = tikaExtractor.getText(in, params);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_java_2() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/program/test.java");
        Map<String, String> params = new HashMap<String, String>();
        params.put("resourceName", "test.java");
        ExtractData extractData = tikaExtractor.getText(in, params);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_java_3() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/program/test.java");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_java_4() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/program/test.java");
        Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "text/x-java-source");
        ExtractData extractData = tikaExtractor.getText(in, params);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_js() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/program/test.js");
        Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "text/plain");
        params.put("resourceName", "test.js");
        ExtractData extractData = tikaExtractor.getText(in, params);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_c() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/program/test.c");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_cpp() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/program/test.cpp");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_h() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/program/test.h");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_hpp() {
        InputStream in =
            ResourceUtil.getResourceAsStream("extractor/program/test.hpp");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_sh() {
        InputStream in = ResourceUtil.getResourceAsStream("extractor/test.sh");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_rtf() {
        InputStream in = ResourceUtil.getResourceAsStream("extractor/test.rtf");
        ExtractData extractData = tikaExtractor.getText(in, null);
        String content = extractData.getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getTika_null() {
        try {
            tikaExtractor.getText(null, null);
            fail();
        } catch (RobotSystemException e) {
            // NOP
        }
    }

    public void test_getPdfPassword_null() {
        String url;
        String resourceName;

        url = null;
        resourceName = null;
        assertNull(tikaExtractor.getPdfPassword(url, resourceName));

        url = "http://test.com/hoge1.pdf";
        resourceName = null;
        assertNull(tikaExtractor.getPdfPassword(url, resourceName));

        url = "http://test.com/hoge1.pdf";
        resourceName = "hoge2.pdf";
        assertNull(tikaExtractor.getPdfPassword(url, resourceName));

        url = null;
        resourceName = "hoge2.pdf";
        assertNull(tikaExtractor.getPdfPassword(url, resourceName));
    }

    public void test_getPdfPassword() {
        String url;
        String resourceName;
        tikaExtractor.addPdfPassword(".*hoge1.pdf", "password");
        tikaExtractor.addPdfPassword("fuga.pdf", "PASSWORD");

        url = null;
        resourceName = null;
        assertNull(tikaExtractor.getPdfPassword(url, resourceName));

        url = "http://test.com/hoge1.pdf";
        resourceName = null;
        assertEquals(
            "password",
            tikaExtractor.getPdfPassword(url, resourceName));

        url = "http://test.com/hoge1.pdf";
        resourceName = "hoge2.pdf";
        assertEquals(
            "password",
            tikaExtractor.getPdfPassword(url, resourceName));

        url = null;
        resourceName = "hoge2.pdf";
        assertNull(tikaExtractor.getPdfPassword(url, resourceName));

        url = null;
        resourceName = "hoge1.pdf";
        assertEquals(
            "password",
            tikaExtractor.getPdfPassword(url, resourceName));

        url = "http://test.com/fuga.pdf";
        resourceName = null;
        assertNull(tikaExtractor.getPdfPassword(url, resourceName));

        url = null;
        resourceName = "fuga.pdf";
        assertEquals(
            "PASSWORD",
            tikaExtractor.getPdfPassword(url, resourceName));
    }
}
