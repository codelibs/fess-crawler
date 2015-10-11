/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.helper.impl;

import java.io.InputStream;

import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.exception.MimeTypeException;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class MimeTypeHelperImplTest extends PlainTestCase {

    private StandardCrawlerContainer container;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        container = new StandardCrawlerContainer().singleton("mimeTypeHelper",
                MimeTypeHelperImpl.class);
    }

    public void test_getContentType() {
        final MimeTypeHelper mimeTypeHelper = container
                .getComponent("mimeTypeHelper");
        final InputStream is = ResourceUtil
                .getResourceAsStream("test/text1.txt");
        final InputStream htmlStream = ResourceUtil
                .getResourceAsStream("html/test1.html");
        final InputStream shtmlStream = ResourceUtil
                .getResourceAsStream("html/test1.shtml");
        final InputStream msWordStream = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.doc");
        final InputStream msExcelStream = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.xls");
        final InputStream msPowerPointStream = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.ppt");
        final InputStream msWordXStream = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.docx");
        final InputStream msExcelXStream = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.xlsx");
        final InputStream msPowerPointXStream = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.pptx");
        final InputStream zipStream = ResourceUtil
                .getResourceAsStream("extractor/zip/test.zip");
        final InputStream lhaStream = ResourceUtil
                .getResourceAsStream("extractor/lha/test.lzh");
        final InputStream gzStream = ResourceUtil
                .getResourceAsStream("extractor/gz/test.tar.gz");
        final InputStream pdfStream = ResourceUtil
                .getResourceAsStream("extractor/test.pdf");
        final InputStream freeMindStream = ResourceUtil
                .getResourceAsStream("extractor/test.mm");

        assertEquals("text/plain",
                mimeTypeHelper.getContentType(is, "hoge.txt"));
        assertEquals("text/html",
                mimeTypeHelper.getContentType(htmlStream, "hoge.html"));
        assertEquals("text/html",
                mimeTypeHelper.getContentType(htmlStream, "hoge.htm"));
        assertEquals("text/html",
                mimeTypeHelper.getContentType(shtmlStream, "hoge.shtml"));

        assertEquals("text/plain",
                mimeTypeHelper.getContentType(is, "hoge.doc"));
        assertEquals("application/msword",
                mimeTypeHelper.getContentType(msWordStream, "hoge.doc"));
        assertEquals("text/plain",
                mimeTypeHelper.getContentType(is, "hoge.xls"));
        assertEquals("application/vnd.ms-excel",
                mimeTypeHelper.getContentType(msExcelStream, "hoge.xls"));
        assertEquals("text/plain",
                mimeTypeHelper.getContentType(is, "hoge.ppt"));
        assertEquals("application/vnd.ms-powerpoint",
                mimeTypeHelper.getContentType(msPowerPointStream, "hoge.ppt"));

        assertEquals("text/plain",
                mimeTypeHelper.getContentType(is, "hoge.docx"));
        assertEquals(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                mimeTypeHelper.getContentType(msWordXStream, "hoge.docx"));
        assertEquals("text/plain",
                mimeTypeHelper.getContentType(is, "hoge.xlsx"));
        assertEquals(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                mimeTypeHelper.getContentType(msExcelXStream, "hoge.xlsx"));
        assertEquals("text/plain",
                mimeTypeHelper.getContentType(is, "hoge.pptx"));
        assertEquals(
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                mimeTypeHelper.getContentType(msPowerPointXStream, "hoge.pptx"));

        assertEquals("image/jpeg",
                mimeTypeHelper.getContentType(null, "hoge.jpg"));
        assertEquals("image/gif",
                mimeTypeHelper.getContentType(null, "hoge.gif"));

        assertEquals("application/pdf",
                mimeTypeHelper.getContentType(pdfStream, "hoge.pdf"));

        assertEquals("application/gzip",
                mimeTypeHelper.getContentType(gzStream, "hoge.tar.gz"));
        assertEquals("application/zip",
                mimeTypeHelper.getContentType(zipStream, "hoge.zip"));
        assertEquals("application/x-lharc", // TODO is it correct?
                mimeTypeHelper.getContentType(lhaStream, "hoge.lzh"));

        assertEquals("application/xml",
                mimeTypeHelper.getContentType(freeMindStream, "hoge.mm"));

        assertEquals("application/octet-stream",
                mimeTypeHelper.getContentType(null, "hoge"));

    }

    public void test_getContentType_null() {
        final MimeTypeHelper mimeTypeHelper = container
                .getComponent("mimeTypeHelper");
        final InputStream is = ResourceUtil
                .getResourceAsStream("test/text1.txt");

        try {
            mimeTypeHelper.getContentType(null, "");
            fail();
        } catch (final MimeTypeException e) {
        }

        try {
            mimeTypeHelper.getContentType(is, "");
            fail();
        } catch (final MimeTypeException e) {
        }

        assertEquals("text/plain", mimeTypeHelper.getContentType(is, " "));
    }

}
