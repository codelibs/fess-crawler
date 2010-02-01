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
package org.seasar.robot.transformer.impl;

import java.io.ByteArrayInputStream;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.AccessResultDataImpl;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;

/**
 * @author shinsuke
 *
 */
public class HtmlTransformerTest extends S2TestCase {
    public HtmlTransformer htmlTransformer;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_name() {
        assertEquals("htmlTransformer", htmlTransformer.getName());
    }

    public void test_transform() {
        byte[] data = new String("xyz").getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://hoge/");
        responseData.setResponseBody(bais);
        responseData.setCharSet("ISO-8859-1");
        ResultData resultData = htmlTransformer.transform(responseData);
        assertEquals("xyz", new String(resultData.getData()));
    }

    public void test_transform_null() {
        try {
            htmlTransformer.transform(null);
            fail();
        } catch (RobotSystemException e) {
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
        assertEquals(url + "/", htmlTransformer.getDuplicateUrl(url));

        url = "http://hoge/";
        assertEquals("http://hoge", htmlTransformer.getDuplicateUrl(url));

    }

    public void test_normalizeUrl() {
        String url;

        url = "http://hoge/index.html";
        assertEquals(url, htmlTransformer.normalizeUrl(url));

        url = "http://hoge/index.html#hoge";
        assertEquals("http://hoge/index.html", htmlTransformer
                .normalizeUrl(url));

        url = "http://hoge/index.html#";
        assertEquals("http://hoge/index.html", htmlTransformer
                .normalizeUrl(url));

        url = "http://hoge/index.html;jsessionid=hoge";
        assertEquals("http://hoge/index.html", htmlTransformer
                .normalizeUrl(url));

        url = "http://hoge/index.html;jsessionid";
        assertEquals("http://hoge/index.html", htmlTransformer
                .normalizeUrl(url));
    }

    public void test_getData() throws Exception {
        String value = "<html><body>hoge</body></html>";
        AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData(value.getBytes());
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("htmlTransformer");

        Object obj = htmlTransformer.getData(accessResultDataImpl);
        assertEquals(value, obj);
    }

    public void test_getData_wrongName() throws Exception {
        String value = "<html><body>hoge</body></html>";
        AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData(value.getBytes());
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("transformer");

        try {
            Object obj = htmlTransformer.getData(accessResultDataImpl);
            fail();
        } catch (RobotSystemException e) {

        }
    }

    public void test_getData_nullData() throws Exception {
        String value = "<html><body>hoge</body></html>";
        AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData(null);
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("htmlTransformer");

        Object obj = htmlTransformer.getData(accessResultDataImpl);
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
        String url = "http://test.com/hoge/;jsessionid?p=id&test=テスト&u=18718&v=123%3d#test";
        String result = "http://test.com/hoge/;jsessionid?p=id&test=%E3%83%86%E3%82%B9%E3%83%88&u=18718&v=123%3d#test";

        assertEquals(result, htmlTransformer.encodeUrl(url, "UTF-8"));
    }
}
