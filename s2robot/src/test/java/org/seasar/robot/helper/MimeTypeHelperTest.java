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
package org.seasar.robot.helper;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.container.SingletonS2Container;

/**
 * @author shinsuke
 *
 */
public class MimeTypeHelperTest extends S2TestCase {

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_getContentType() {
        MimeTypeHelper mimeTypeHelper = SingletonS2Container
                .getComponent(MimeTypeHelper.class);

        assertEquals("text/plain", mimeTypeHelper.getContentType("hoge.txt"));
        assertEquals("text/html", mimeTypeHelper.getContentType("hoge.html"));
        assertEquals("text/html", mimeTypeHelper.getContentType("hoge.htm"));
        assertEquals("application/msword", mimeTypeHelper
                .getContentType("hoge.doc"));
        assertEquals("application/vnd.ms-excel", mimeTypeHelper
                .getContentType("hoge.xls"));
        assertEquals("image/jpeg", mimeTypeHelper.getContentType("hoge.jpg"));
        assertEquals("image/gif", mimeTypeHelper.getContentType("hoge.gif"));
        assertEquals("application/vnd.ms-powerpoint", mimeTypeHelper
                .getContentType("hoge.ppt"));
        assertEquals("application/pdf", mimeTypeHelper
                .getContentType("hoge.pdf"));
        assertEquals("application/x-gzip", mimeTypeHelper
                .getContentType("hoge.tar.gz"));
        assertEquals("application/zip", mimeTypeHelper
                .getContentType("hoge.zip"));

        assertEquals("application/octet-stream", mimeTypeHelper
                .getContentType("hoge"));

    }

    public void test_getContentType_null() {
        MimeTypeHelper mimeTypeHelper = SingletonS2Container
                .getComponent(MimeTypeHelper.class);

        try {
            mimeTypeHelper.getContentType(null);
            fail();
        } catch (MimeTypeException e) {
        }
        try {
            mimeTypeHelper.getContentType("");
            fail();
        } catch (MimeTypeException e) {
        }

        assertEquals("application/octet-stream", mimeTypeHelper
                .getContentType(" "));
    }

    public void test_getContentTypes() {
        MimeTypeHelper mimeTypeHelper = SingletonS2Container
                .getComponent(MimeTypeHelper.class);

        assertEquals("text/plain",
                mimeTypeHelper.getContentTypes("hoge.txt")[0]);
        assertEquals("text/html",
                mimeTypeHelper.getContentTypes("hoge.html")[0]);
        assertEquals("text/html", mimeTypeHelper.getContentTypes("hoge.htm")[0]);
        assertEquals("application/msword", mimeTypeHelper
                .getContentTypes("hoge.doc")[0]);
        assertEquals("application/vnd.ms-excel", mimeTypeHelper
                .getContentTypes("hoge.xls")[0]);
        assertEquals("image/jpeg",
                mimeTypeHelper.getContentTypes("hoge.jpg")[0]);
        assertEquals("image/gif", mimeTypeHelper.getContentTypes("hoge.gif")[0]);
        assertEquals("application/vnd.ms-powerpoint", mimeTypeHelper
                .getContentTypes("hoge.ppt")[0]);
        assertEquals("application/pdf", mimeTypeHelper
                .getContentTypes("hoge.pdf")[0]);
        assertEquals("application/x-gzip", mimeTypeHelper
                .getContentTypes("hoge.tar.gz")[0]);
        assertEquals("application/x-tar", mimeTypeHelper
                .getContentTypes("hoge.tar.gz")[1]);
        assertEquals("application/zip", mimeTypeHelper
                .getContentTypes("hoge.zip")[0]);

        assertEquals("application/octet-stream", mimeTypeHelper
                .getContentTypes("hoge")[0]);

    }

    public void test_getContentTypes_null() {
        MimeTypeHelper mimeTypeHelper = SingletonS2Container
                .getComponent(MimeTypeHelper.class);

        try {
            mimeTypeHelper.getContentTypes(null);
            fail();
        } catch (MimeTypeException e) {
        }
        try {
            mimeTypeHelper.getContentTypes("");
            fail();
        } catch (MimeTypeException e) {
        }

        assertEquals("application/octet-stream", mimeTypeHelper
                .getContentTypes(" ")[0]);
        assertEquals("application/octet-stream", mimeTypeHelper
                .getContentTypes(".")[0]);
        assertEquals("application/octet-stream", mimeTypeHelper
                .getContentTypes(".bashrc")[0]);
    }
}
