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
package org.codelibs.fess.crawler.helper.impl;

import java.io.IOException;
import java.io.InputStream;

import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.exception.MimeTypeException;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;
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
        container = new StandardCrawlerContainer().singleton("mimeTypeHelper", MimeTypeHelperImpl.class);
    }

    public void test_getContentType() throws IOException {
        assertContentType("text/plain", "test/text1.txt", "hoge.txt");
        assertContentType("text/html", "html/test1.html", "hoge.html");
        assertContentType("text/html", "html/test1.html", "hoge.htm");
        assertContentType("text/html", "html/test1.shtml", "hoge.shtml");

        assertContentType("text/plain", "test/text1.txt", "hoge.doc");
        assertContentType("application/msword", "extractor/msoffice/test.doc", "hoge.doc");
        assertContentType("text/plain", "test/text1.txt", "hoge.xls");
        assertContentType("application/vnd.ms-excel", "extractor/msoffice/test.xls", "hoge.xls");
        assertContentType("text/plain", "test/text1.txt", "hoge.ppt");
        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "hoge.ppt");

        assertContentType("text/plain", "test/text1.txt", "hoge.docx");
        assertContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "extractor/msoffice/test.docx",
                "hoge.docx");
        assertContentType("text/plain", "test/text1.txt", "hoge.xlsx");
        assertContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "extractor/msoffice/test.xlsx", "hoge.xlsx");
        assertContentType("text/plain", "test/text1.txt", "hoge.pptx");
        assertContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation", "extractor/msoffice/test.pptx",
                "hoge.pptx");

        assertContentType("image/jpeg", null, "hoge.jpg");
        assertContentType("image/gif", null, "hoge.gif");

        assertContentType("application/pdf", "extractor/test.pdf", "hoge.pdf");

        assertContentType("application/gzip", "extractor/gz/test.tar.gz", "hoge.tar.gz");
        assertContentType("application/zip", "extractor/zip/test.zip", "hoge.zip");
        assertContentType("application/x-lharc", "extractor/lha/test.lzh", "hoge.lzh"); // TODO is it correct?

        assertContentType("application/xml", "extractor/test.mm", "hoge.mm");

        assertContentType("message/rfc822", "extractor/eml/sample1.eml", "sample1.eml");

        assertContentType("application/octet-stream", null, "hoge");

        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "h&oge.ppt");
        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "h?oge.ppt");
        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "h@oge.ppt");
        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "h:oge.ppt");
        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "h/oge.ppt");

        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_2000.dwg", "autocad_2000.dwg");
        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_2004.dwg", "autocad_2004.dwg");
        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_2007.dwg", "autocad_2007.dwg");
        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_2010.dwg", "autocad_2010.dwg");
        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_2013.dwg", "autocad_2013.dwg");
        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_97_98.dwg", "autocad_97_98.dwg");

        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_2000.dxf", "autocad_2000.dxf");
        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_2004.dxf", "autocad_2004.dxf");
        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_2007.dxf", "autocad_2007.dxf");
        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_2010.dxf", "autocad_2010.dxf");
        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_2013.dxf", "autocad_2013.dxf");
        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_R12_LT2.dxf", "autocad_R12_LT2.dxf");
    }

    private void assertContentType(final String expect, final String path, final String name) throws IOException {
        final MimeTypeHelper mimeTypeHelper = container.getComponent("mimeTypeHelper");
        if (path != null) {
            try (final InputStream is = ResourceUtil.getResourceAsStream(path)) {
                assertEquals(expect, mimeTypeHelper.getContentType(is, name));
            }
        } else {
            assertEquals(expect, mimeTypeHelper.getContentType(null, name));
        }
    }

    public void test_getContentType_null() {
        final MimeTypeHelper mimeTypeHelper = container.getComponent("mimeTypeHelper");
        final InputStream is = ResourceUtil.getResourceAsStream("test/text1.txt");

        try {
            mimeTypeHelper.getContentType(null, "");
            fail();
        } catch (final MimeTypeException e) {}

        try {
            mimeTypeHelper.getContentType(is, "");
            fail();
        } catch (final MimeTypeException e) {}

        assertEquals("text/plain", mimeTypeHelper.getContentType(is, " "));
    }

}
