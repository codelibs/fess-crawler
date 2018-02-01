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
package org.codelibs.fess.crawler.helper;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 *
 */
public class UrlConvertHelperTest extends PlainTestCase {

    public UrlConvertHelper urlConvertHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer()
                .singleton("urlConvertHelper", UrlConvertHelper.class);
        urlConvertHelper = container.getComponent("urlConvertHelper");
    }

    public void test_convert() {
        String url;

        url = "http://hoge.com/http/fuga.html";
        assertEquals("http://hoge.com/http/fuga.html",
                urlConvertHelper.convert(url));

        url = "tp://hoge.com/http/fuga.html";
        assertEquals("tp://hoge.com/http/fuga.html",
                urlConvertHelper.convert(url));

        urlConvertHelper.add("^tp:", "http:");

        url = "http://hoge.com/http/fuga.html";
        assertEquals("http://hoge.com/http/fuga.html",
                urlConvertHelper.convert(url));

        url = "tp://hoge.com/http/fuga.html";
        assertEquals("http://hoge.com/http/fuga.html",
                urlConvertHelper.convert(url));

        urlConvertHelper.add("fuga", "hoge");
        urlConvertHelper.add("http/hoge", "peke");

        url = "http://hoge.com/http/fuga.html";
        assertEquals("http://hoge.com/peke.html", urlConvertHelper.convert(url));

        url = "tp://hoge.com/http/fuga.html";
        assertEquals("http://hoge.com/peke.html", urlConvertHelper.convert(url));
    }

    public void test_convert_null() {
        String url;

        url = null;
        assertNull(urlConvertHelper.convert(url));

        url = "";
        assertEquals("", urlConvertHelper.convert(url));

        url = " ";
        assertEquals(" ", urlConvertHelper.convert(url));

    }
}