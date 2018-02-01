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
package org.codelibs.fess.crawler.transformer.impl;

import java.util.Map;

import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.entity.AccessResultDataImpl;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class HtmlTransformerTest extends PlainTestCase {
    public HtmlTransformer htmlTransformer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        htmlTransformer = new HtmlTransformer();
        htmlTransformer.setName("htmlTransformer");
        Map<String, String> featureMap = newHashMap();
        featureMap.put("http://xml.org/sax/features/namespaces", "false");
        htmlTransformer.setFeatureMap(featureMap);
        Map<String, String> propertyMap = newHashMap();
        htmlTransformer.setPropertyMap(propertyMap);
        Map<String, String> childUrlRuleMap = newHashMap();
        childUrlRuleMap.put("//A", "href");
        childUrlRuleMap.put("//AREA", "href");
        childUrlRuleMap.put("//FRAME", "src");
        childUrlRuleMap.put("//IFRAME", "src");
        childUrlRuleMap.put("//IMG", "src");
        childUrlRuleMap.put("//LINK", "href");
        childUrlRuleMap.put("//SCRIPT", "src");
        htmlTransformer.setChildUrlRuleMap(childUrlRuleMap);
    }

    public void test_name() {
        assertEquals("htmlTransformer", htmlTransformer.getName());
    }

    public void test_transform() {
        final byte[] data = new String("xyz").getBytes();
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://hoge/");
        responseData.setResponseBody(data);
        responseData.setCharSet("ISO-8859-1");
        final ResultData resultData = htmlTransformer.transform(responseData);
        assertEquals("xyz", new String(resultData.getData()));
    }

    public void test_transform_filelink() {
        String content = "<a href=\"test2.html\">test</a>";
        final byte[] data = new String(content).getBytes();
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://hoge/test.html");
        responseData.setResponseBody(data);
        responseData.setCharSet("ISO-8859-1");
        responseData.setMimeType("text/html");
        final ResultData resultData = htmlTransformer.transform(responseData);
        assertEquals(content, new String(resultData.getData()));
        assertEquals(1, resultData.getChildUrlSet().size());
        assertEquals("http://hoge/test2.html", resultData.getChildUrlSet()
                .iterator().next().getUrl());
    }

    public void test_transform_urllink() {
        String content = "<a href=\"http://fuga/test.html\">test</a>";
        final byte[] data = new String(content).getBytes();
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://hoge/test.html");
        responseData.setResponseBody(data);
        responseData.setCharSet("ISO-8859-1");
        responseData.setMimeType("text/html");
        final ResultData resultData = htmlTransformer.transform(responseData);
        assertEquals(content, new String(resultData.getData()));
        assertEquals(1, resultData.getChildUrlSet().size());
        assertEquals("http://fuga/test.html", resultData.getChildUrlSet()
                .iterator().next().getUrl());
    }

    public void test_transform_queryparam() {
        String content = "<a href=\"?q=hoge\">test</a>";
        final byte[] data = new String(content).getBytes();
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://hoge/test.html");
        responseData.setResponseBody(data);
        responseData.setCharSet("ISO-8859-1");
        responseData.setMimeType("text/html");
        final ResultData resultData = htmlTransformer.transform(responseData);
        assertEquals(content, new String(resultData.getData()));
        assertEquals(1, resultData.getChildUrlSet().size());
        assertEquals("http://hoge/test.html?q=hoge", resultData
                .getChildUrlSet().iterator().next().getUrl());
    }

    public void test_transform_null() {
        try {
            htmlTransformer.transform(null);
            fail();
        } catch (final CrawlerSystemException e) {
        }
    }

    public void test_parseCharset() {
        String content;

        content = "...;charset=UTF-8\"...";
        assertEquals("UTF-8", htmlTransformer.parseCharset(content));

        content = "...; charset=UTF-8\"...";
        assertEquals("UTF-8", htmlTransformer.parseCharset(content));

        content = "...;charset = UTF-8\"...";
        assertEquals("UTF-8", htmlTransformer.parseCharset(content));

        content = "...;charset=UTF-8 \"...";
        assertEquals("UTF-8", htmlTransformer.parseCharset(content));

        content = "...;charset=Shift_JIS\"...";
        assertEquals("Shift_JIS", htmlTransformer.parseCharset(content));

        content = "...;Charset=Shift_JIS\"...";
        assertEquals("Shift_JIS", htmlTransformer.parseCharset(content));

        content = "...;charset=EUC-JP\"...";
        assertEquals("EUC-JP", htmlTransformer.parseCharset(content));

    }

    public void test_getDuplicateUrl() {
        String url;

        url = "http://hoge/index.html";
        assertEquals(
                RequestDataBuilder.newRequestData().url(url + "/").build(),
                htmlTransformer.getDuplicateUrl(RequestDataBuilder
                        .newRequestData().url(url).build()));

        url = "http://hoge/";
        assertEquals(
                RequestDataBuilder.newRequestData().url("http://hoge").build(),
                htmlTransformer.getDuplicateUrl(RequestDataBuilder
                        .newRequestData().url(url).build()));

    }

    public void test_normalizeUrl() {
        String url;

        url = "http://hoge/index 123.html";
        assertEquals("http://hoge/index%20123.html", htmlTransformer.normalizeUrl(url));

        url = "http://hoge/index.html";
        assertEquals(url, htmlTransformer.normalizeUrl(url));

        url = "http://hoge/index.html?a=1";
        assertEquals(url, htmlTransformer.normalizeUrl(url));

        url = "http://hoge/index.html?a=1&b=2";
        assertEquals(url, htmlTransformer.normalizeUrl(url));

        url = "http://hoge/index.html#hoge";
        assertEquals("http://hoge/index.html",
                htmlTransformer.normalizeUrl(url));

        url = "http://hoge/index.html#";
        assertEquals("http://hoge/index.html",
                htmlTransformer.normalizeUrl(url));

        url = "http://hoge/index.html;jsessionid=hoge";
        assertEquals("http://hoge/index.html",
                htmlTransformer.normalizeUrl(url));

        url = "http://hoge/index.html;jsessionid=hoge.fuga";
        assertEquals("http://hoge/index.html",
                htmlTransformer.normalizeUrl(url));

        url = "http://hoge/index.html;jsessionid=hoge?a=1";
        assertEquals("http://hoge/index.html?a=1",
                htmlTransformer.normalizeUrl(url));

        url = "http://hoge/index.html;jsessionid=hoge.fuga?a=1";
        assertEquals("http://hoge/index.html?a=1",
                htmlTransformer.normalizeUrl(url));

        url = "http://hoge/index.html;jsessionid=hoge?a=1&b=2";
        assertEquals("http://hoge/index.html?a=1&b=2",
                htmlTransformer.normalizeUrl(url));

        url = "http://hoge/index.html;jsessionid=hoge#HOGE";
        assertEquals("http://hoge/index.html",
                htmlTransformer.normalizeUrl(url));

        url = "http://hoge/index.html;jsessionid=hoge?a=1#HOGE";
        assertEquals("http://hoge/index.html?a=1",
                htmlTransformer.normalizeUrl(url));

        url = "http://hoge/./index.html";
        assertEquals("http://hoge/index.html",
                htmlTransformer.normalizeUrl(url));

        url = "http://hoge/a/index.html";
        assertEquals("http://hoge/a/index.html",
                htmlTransformer.normalizeUrl(url));

        url = "://hoge/index.html";
        assertEquals("://hoge/index.html", htmlTransformer.normalizeUrl(url));

        url = "://hoge//index.html";
        assertEquals("://hoge/index.html", htmlTransformer.normalizeUrl(url));

        url = "http://hoge//index.html";
        assertEquals("http://hoge/index.html",
                htmlTransformer.normalizeUrl(url));

        url = "http://hoge//a/.././//index.html";
        assertEquals("http://hoge/index.html",
                htmlTransformer.normalizeUrl(url));

        // invalid cases
        url = "http://hoge/index.html;jsessionid";
        assertEquals("http://hoge/index.html;jsessionid",
                htmlTransformer.normalizeUrl(url));

        url = "http://hoge/index.html;jsessionid?a=1#HOGE";
        assertEquals("http://hoge/index.html;jsessionid?a=1",
                htmlTransformer.normalizeUrl(url));

        url = "http://hoge/aaa/../index.html";
        assertEquals("http://hoge/index.html",
                htmlTransformer.normalizeUrl(url));

        url = "http://hoge/aaa/bbb/../../index.html";
        assertEquals("http://hoge/index.html",
                htmlTransformer.normalizeUrl(url));

        url = "/../index.html";
        assertEquals(url, htmlTransformer.normalizeUrl(url));

        url = "/../../index.html";
        assertEquals("/index.html", htmlTransformer.normalizeUrl(url));

        url = "/../../../index.html";
        assertEquals("/../index.html", htmlTransformer.normalizeUrl(url));
    }

    public void test_getData() throws Exception {
        final String value = "<html><body>hoge</body></html>";
        final AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData(value.getBytes());
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("htmlTransformer");

        final Object obj = htmlTransformer.getData(accessResultDataImpl);
        assertEquals(value, obj);
    }

    public void test_getData_wrongName() throws Exception {
        final String value = "<html><body>hoge</body></html>";
        final AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData(value.getBytes());
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("transformer");

        try {
            htmlTransformer.getData(accessResultDataImpl);
            fail();
        } catch (final CrawlerSystemException e) {

        }
    }

    public void test_getData_nullData() throws Exception {
        final AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData(null);
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("htmlTransformer");

        final Object obj = htmlTransformer.getData(accessResultDataImpl);
        assertNull(obj);
    }

    public void test_isValidPath_valid() {
        String value;

        value = "hoge.html";
        assertTrue(htmlTransformer.isValidPath(value));

        value = "./hoge.html";
        assertTrue(htmlTransformer.isValidPath(value));

        value = "/hoge.html";
        assertTrue(htmlTransformer.isValidPath(value));

        value = "http://www.seasar.org/hoge.html";
        assertTrue(htmlTransformer.isValidPath(value));

        value = "a javascript:...";
        assertTrue(htmlTransformer.isValidPath(value));

    }

    public void test_isValidPath_invalid() {
        String value;

        value = "javascript:...";
        assertFalse(htmlTransformer.isValidPath(value));

        value = "mailto:...";
        assertFalse(htmlTransformer.isValidPath(value));

        value = "irc:...";
        assertFalse(htmlTransformer.isValidPath(value));

        value = " javascript:...";
        assertFalse(htmlTransformer.isValidPath(value));

        value = " mailto:...";
        assertFalse(htmlTransformer.isValidPath(value));

        value = " irc:...";
        assertFalse(htmlTransformer.isValidPath(value));

        value = "JAVASCRIPT:...";
        assertFalse(htmlTransformer.isValidPath(value));

        value = "MAILTO:...";
        assertFalse(htmlTransformer.isValidPath(value));

        value = "IRC:...";
        assertFalse(htmlTransformer.isValidPath(value));
    }

    public void test_isValidPath_blank() {
        String value;

        value = null;
        assertFalse(htmlTransformer.isValidPath(value));

        value = "";
        assertFalse(htmlTransformer.isValidPath(value));

        value = " ";
        assertFalse(htmlTransformer.isValidPath(value));
    }

    public void test_encodeUrl_valid() {
        String url = "http://TEST.com/hoge/;jsessionid?p=id&test=テスト&u=18718&v=123%3d#test";
        String result = "http://TEST.com/hoge/;jsessionid?p=id&test=%E3%83%86%E3%82%B9%E3%83%88&u=18718&v=123%3d#test";
        assertEquals(result, htmlTransformer.encodeUrl(url, "UTF-8"));

        url = ".-*_:/+%=&?#[]@~!$'(),;";
        result = ".-*_:/+%=&?#[]@~!$'(),;";
        assertEquals(result, htmlTransformer.encodeUrl(url, "UTF-8"));
    }

    public void test_isSupportedCharset_valid() {
        assertTrue(htmlTransformer.isSupportedCharset("UTF-8"));
        assertTrue(htmlTransformer.isSupportedCharset("EUC-JP"));
        assertTrue(htmlTransformer.isSupportedCharset("Shift_JIS"));
    }

    public void test_isSupportedCharset_invalid() {
        assertFalse(htmlTransformer.isSupportedCharset("aaa"));
        assertFalse(htmlTransformer.isSupportedCharset(" "));
        assertFalse(htmlTransformer.isSupportedCharset(null));
    }
}
