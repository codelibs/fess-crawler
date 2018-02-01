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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResultDataImpl;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.entity.TestEntity;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class XmlTransformerTest extends PlainTestCase {
    public XmlTransformer xmlTransformer;

    public XmlTransformer xmlNsTransformer;

    public XmlTransformer xmlMapTransformer;

    public XmlTransformer xmlEntityTransformer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        {
            xmlTransformer = new XmlTransformer();
            xmlTransformer.setName("xmlTransformer");
            Map<String, String> fieldRuleMap = newLinkedHashMap();
            fieldRuleMap.put("name", "//address/item/name");
            fieldRuleMap.put("access", "//address/item/access");
            fieldRuleMap.put("image", "//address/item/image/@file");
            fieldRuleMap.put("email", "//address/item/access[@kind='email']");
            fieldRuleMap.put("url", "//address/item/access[@kind='url']");
            fieldRuleMap.put("tel", "//address/item/access[@kind='tel']");
            xmlTransformer.setFieldRuleMap(fieldRuleMap);
        }
        {
            xmlNsTransformer = new XmlTransformer();
            xmlNsTransformer.setName("xmlNsTransformer");
            xmlNsTransformer.setNamespaceAware(true);
            Map<String, String> fieldRuleMap = newLinkedHashMap();
            fieldRuleMap.put("name", "//hoge:address/hoge:item/hoge:name");
            fieldRuleMap.put("access", "//hoge:address/hoge:item/hoge:access");
            fieldRuleMap.put("image",
                    "//hoge:address/hoge:item/hoge:image/@file");
            fieldRuleMap.put("email",
                    "//hoge:address/hoge:item/hoge:access[@kind='email']");
            fieldRuleMap.put("url",
                    "//hoge:address/hoge:item/hoge:access[@kind='url']");
            fieldRuleMap.put("tel",
                    "//hoge:address/hoge:item/hoge:access[@kind='tel']");
            xmlNsTransformer.setFieldRuleMap(fieldRuleMap);
        }
        {
            xmlMapTransformer = new XmlTransformer();
            xmlMapTransformer.setName("xmlMapTransformer");
            xmlMapTransformer.setDataClass(Map.class);
            Map<String, String> fieldRuleMap = newLinkedHashMap();
            fieldRuleMap.put("name", "//hoge:address/hoge:item/hoge:name");
            fieldRuleMap.put("access", "//hoge:address/hoge:item/hoge:access");
            fieldRuleMap.put("image",
                    "//hoge:address/hoge:item/hoge:image/@file");
            fieldRuleMap.put("email",
                    "//hoge:address/hoge:item/hoge:access[@kind='email']");
            fieldRuleMap.put("url",
                    "//hoge:address/hoge:item/hoge:access[@kind='url']");
            fieldRuleMap.put("tel",
                    "//hoge:address/hoge:item/hoge:access[@kind='tel']");
            xmlMapTransformer.setFieldRuleMap(fieldRuleMap);
        }
        {
            xmlEntityTransformer = new XmlTransformer();
            xmlEntityTransformer.setName("xmlEntityTransformer");
            xmlEntityTransformer.setDataClass(TestEntity.class);
            Map<String, String> fieldRuleMap = newLinkedHashMap();
            fieldRuleMap.put("name", "//hoge:address/hoge:item/hoge:name");
            fieldRuleMap.put("access", "//hoge:address/hoge:item/hoge:access");
            fieldRuleMap.put("image",
                    "//hoge:address/hoge:item/hoge:image/@file");
            fieldRuleMap.put("email",
                    "//hoge:address/hoge:item/hoge:access[@kind='email']");
            fieldRuleMap.put("url",
                    "//hoge:address/hoge:item/hoge:access[@kind='url']");
            fieldRuleMap.put("tel",
                    "//hoge:address/hoge:item/hoge:access[@kind='tel']");
            xmlEntityTransformer.setFieldRuleMap(fieldRuleMap);
        }
    }

    public void test_transform() throws Exception {
        final String result = "<?xml version=\"1.0\"?>\n"//
                + "<doc>\n"//
                + "<field name=\"name\"><list><item>鈴木太郎</item><item>佐藤二朗</item><item>田中花子</item></list></field>\n"//
                + "<field name=\"access\"><list><item></item><item>http://www.taro.com/</item><item>jiro@hoge.foo.bar</item><item>090-xxxx-xxxx</item></list></field>\n"//
                + "<field name=\"image\"><list><item>taro.png</item><item>jiro.png</item><item>hanako.png</item></list></field>\n"//
                + "<field name=\"email\"><list><item></item><item>jiro@hoge.foo.bar</item></list></field>\n"//
                + "<field name=\"url\">http://www.taro.com/</field>\n"//
                + "<field name=\"tel\">090-xxxx-xxxx</field>\n"//
                + "</doc>";

        final ResponseData responseData = new ResponseData();
        responseData.setResponseBody(ResourceUtil
                .getResourceAsFile("extractor/test.xml"), false);
        responseData.setCharSet(Constants.UTF_8);
        final ResultData resultData = xmlTransformer.transform(responseData);
        assertEquals(result, new String(resultData.getData(), Constants.UTF_8));
    }

    public void test_transformNs() throws Exception {
        final String result = "<?xml version=\"1.0\"?>\n"//
                + "<doc>\n"//
                + "<field name=\"name\"><list><item>鈴木太郎</item><item>佐藤二朗</item><item>田中花子</item></list></field>\n"//
                + "<field name=\"access\"><list><item></item><item>http://www.taro.com/</item><item>jiro@hoge.foo.bar</item><item>090-xxxx-xxxx</item></list></field>\n"//
                + "<field name=\"image\"><list><item>taro.png</item><item>jiro.png</item><item>hanako.png</item></list></field>\n"//
                + "<field name=\"email\"><list><item></item><item>jiro@hoge.foo.bar</item></list></field>\n"//
                + "<field name=\"url\">http://www.taro.com/</field>\n"//
                + "<field name=\"tel\">090-xxxx-xxxx</field>\n"//
                + "</doc>";

        final ResponseData responseData = new ResponseData();
        responseData.setResponseBody(ResourceUtil
                .getResourceAsFile("extractor/test_ns.xml"), false);
        responseData.setCharSet(Constants.UTF_8);
        final ResultData resultData = xmlNsTransformer.transform(responseData);
        assertEquals(result, new String(resultData.getData(), Constants.UTF_8));
    }

    public void test_getData() throws Exception {
        final String value = "<?xml version=\"1.0\"?>\n"//
                + "<doc>\n"//
                + "<field name=\"title\">タイトル</field>\n"//
                + "<field name=\"body\">第一章 第一節 ほげほげふがふが LINK 第2章 第2節</field>\n"//
                + "<field name=\"list\"><list><item>リスト1</item><item>リスト2</item><item>リスト3</item></list></field>\n"//
                + "</doc>";

        final AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData(value.getBytes(Constants.UTF_8));
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("xmlTransformer");

        final Object obj = xmlTransformer.getData(accessResultDataImpl);
        assertEquals(value, obj);
    }

    public void test_getData_wrongName() throws Exception {
        final String value = "<?xml version=\"1.0\"?>\n"//
                + "<doc>\n"//
                + "<field name=\"title\">タイトル</field>\n"//
                + "<field name=\"body\">第一章 第一節 ほげほげふがふが LINK 第2章 第2節</field>\n"//
                + "</doc>";

        final AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData(value.getBytes(Constants.UTF_8));
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("transformer");

        try {
            final Object obj = xmlTransformer.getData(accessResultDataImpl);
            fail();
        } catch (final CrawlerSystemException e) {
        }
    }

    public void test_getData_nullData() throws Exception {
        final String value = "<?xml version=\"1.0\"?>\n"//
                + "<doc>\n"//
                + "<field name=\"title\">タイトル</field>\n"//
                + "<field name=\"body\">第一章 第一節 ほげほげふがふが LINK 第2章 第2節</field>\n"//
                + "</doc>";

        final AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData(null);
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("xmlTransformer");

        final Object obj = xmlTransformer.getData(accessResultDataImpl);
        assertNull(obj);
    }

    public void test_dataClass() {
        assertNull(xmlTransformer.dataClass);
        assertEquals(Map.class, xmlMapTransformer.dataClass);
    }

    public void test_getData_dataMap() throws Exception {
        final String value = "<?xml version=\"1.0\"?>\n"//
                + "<doc>\n"//
                + "<field name=\"title\">タイトル</field>\n"//
                + "<field name=\"body\">第一章 第一節 ほげほげふがふが LINK 第2章 第2節</field>\n"//
                + "<field name=\"list\"><list><item>リスト1</item><item>リスト2</item><item>リスト3</item></list></field>\n"//
                + "</doc>";

        final AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData(value.getBytes(Constants.UTF_8));
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("xmlMapTransformer");

        final Object obj = xmlMapTransformer.getData(accessResultDataImpl);
        assertTrue(obj instanceof Map);
        final Map<String, String> map = (Map) obj;
        assertEquals("タイトル", map.get("title"));
        assertEquals("第一章 第一節 ほげほげふがふが LINK 第2章 第2節", map.get("body"));
        final List<String> list = new ArrayList<String>();
        list.add("リスト1");
        list.add("リスト2");
        list.add("リスト3");
        assertEquals(list, map.get("list"));
    }

    public void test_getData_dataMap_entity() throws Exception {
        final String value = "<?xml version=\"1.0\"?>\n"//
                + "<doc>\n"//
                + "<field name=\"title\">タイトル</field>\n"//
                + "<field name=\"body\">第一章 第一節 ほげほげふがふが LINK 第2章 第2節</field>\n"//
                + "<field name=\"list\"><list><item>リスト1</item><item>リスト2</item><item>リスト3</item></list></field>\n"//
                + "</doc>";

        final AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData(value.getBytes(Constants.UTF_8));
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("xmlEntityTransformer");

        final Object obj = xmlEntityTransformer.getData(accessResultDataImpl);
        assertTrue(obj instanceof TestEntity);
        final TestEntity entity = (TestEntity) obj;
        assertEquals("タイトル", entity.getTitle());
        assertEquals("第一章 第一節 ほげほげふがふが LINK 第2章 第2節", entity.getBody());
        final List<String> list = new ArrayList<String>();
        list.add("リスト1");
        list.add("リスト2");
        list.add("リスト3");
        assertEquals(list, entity.getList());
    }

    public void test_getData_dataMap_entity_emptyList() throws Exception {
        final String value = "<?xml version=\"1.0\"?>\n"//
                + "<doc>\n"//
                + "<field name=\"title\">タイトル</field>\n"//
                + "<field name=\"body\">第一章 第一節 ほげほげふがふが LINK 第2章 第2節</field>\n"//
                + "<field name=\"list\"><list></list></field>\n"//
                + "</doc>";

        final AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData(value.getBytes(Constants.UTF_8));
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("xmlEntityTransformer");

        final Object obj = xmlEntityTransformer.getData(accessResultDataImpl);
        assertTrue(obj instanceof TestEntity);
        final TestEntity entity = (TestEntity) obj;
        assertEquals("タイトル", entity.getTitle());
        assertEquals("第一章 第一節 ほげほげふがふが LINK 第2章 第2節", entity.getBody());
        final List<String> list = new ArrayList<String>();
        assertEquals(list, entity.getList());
    }
}
