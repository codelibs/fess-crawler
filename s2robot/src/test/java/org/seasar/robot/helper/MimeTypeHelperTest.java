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
}
