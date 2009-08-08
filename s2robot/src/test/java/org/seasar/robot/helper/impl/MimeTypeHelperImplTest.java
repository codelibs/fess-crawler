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
package org.seasar.robot.helper.impl;

import java.io.InputStream;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.robot.helper.MimeTypeException;
import org.seasar.robot.helper.MimeTypeHelper;

/**
 * @author shinsuke
 *
 */
public class MimeTypeHelperImplTest extends S2TestCase {

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_getContentType() {
        MimeTypeHelper mimeTypeHelper = SingletonS2Container
                .getComponent(MimeTypeHelperImpl.class);
        InputStream is = ResourceUtil.getResourceAsStream("test/text1.txt");
        InputStream msWordStream = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.doc");
        InputStream msExcelStream = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.xls");
        InputStream msPowerPointStream = ResourceUtil
                .getResourceAsStream("extractor/msoffice/test.ppt");
        InputStream zipStream = ResourceUtil
                .getResourceAsStream("extractor/zip/test.zip");
        InputStream gzStream = ResourceUtil
                .getResourceAsStream("extractor/gz/test.tar.gz");
        InputStream pdfStream = ResourceUtil
                .getResourceAsStream("extractor/test.pdf");

        assertEquals("text/plain", mimeTypeHelper
                .getContentType(is, "hoge.txt"));
        assertEquals("text/html", mimeTypeHelper
                .getContentType(is, "hoge.html"));
        assertEquals("text/html", mimeTypeHelper.getContentType(is, "hoge.htm"));

        assertEquals("text/plain", mimeTypeHelper
                .getContentType(is, "hoge.doc"));
        assertEquals("application/msword", mimeTypeHelper.getContentType(
                msWordStream, "hoge.doc"));
        assertEquals("text/plain", mimeTypeHelper
                .getContentType(is, "hoge.xls"));
        assertEquals("application/vnd.ms-excel", mimeTypeHelper.getContentType(
                msExcelStream, "hoge.xls"));
        assertEquals("text/plain", mimeTypeHelper
                .getContentType(is, "hoge.ppt"));
        assertEquals("application/vnd.ms-powerpoint", mimeTypeHelper
                .getContentType(msPowerPointStream, "hoge.ppt"));

        assertEquals("image/jpeg", mimeTypeHelper.getContentType(null,
                "hoge.jpg"));
        assertEquals("image/gif", mimeTypeHelper.getContentType(null,
                "hoge.gif"));

        assertEquals("application/pdf", mimeTypeHelper.getContentType(
                pdfStream, "hoge.pdf"));

        assertEquals("application/x-gzip", mimeTypeHelper.getContentType(
                gzStream, "hoge.tar.gz"));
        assertEquals("application/zip", mimeTypeHelper.getContentType(
                zipStream, "hoge.zip"));

        assertEquals("application/octet-stream", mimeTypeHelper.getContentType(
                null, "hoge"));

    }

    public void test_getContentType_null() {
        MimeTypeHelper mimeTypeHelper = SingletonS2Container
                .getComponent(MimeTypeHelperImpl.class);
        InputStream is = ResourceUtil.getResourceAsStream("test/text1.txt");

        try {
            mimeTypeHelper.getContentType(null, "");
            fail();
        } catch (MimeTypeException e) {
        }

        try {
            mimeTypeHelper.getContentType(is, "");
            fail();
        } catch (MimeTypeException e) {
        }

        assertEquals("text/plain", mimeTypeHelper.getContentType(is, " "));
    }

}
