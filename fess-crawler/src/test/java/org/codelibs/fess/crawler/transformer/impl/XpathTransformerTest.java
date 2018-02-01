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
public class XpathTransformerTest extends PlainTestCase {
    public XpathTransformer xpathTransformer;

    public XpathTransformer xpathMapTransformer;

    public XpathTransformer xpathEntityTransformer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        {
            xpathTransformer = new XpathTransformer();
            xpathTransformer.setName("xpathTransformer");
            Map<String, String> featureMap = newHashMap();
            featureMap.put("http://xml.org/sax/features/namespaces", "false");
            xpathTransformer.setFeatureMap(featureMap);
            Map<String, String> propertyMap = newHashMap();
            xpathTransformer.setPropertyMap(propertyMap);
            Map<String, String> childUrlRuleMap = newHashMap();
            childUrlRuleMap.put("//A", "href");
            childUrlRuleMap.put("//AREA", "href");
            childUrlRuleMap.put("//FRAME", "src");
            childUrlRuleMap.put("//IFRAME", "src");
            childUrlRuleMap.put("//IMG", "src");
            childUrlRuleMap.put("//LINK", "href");
            childUrlRuleMap.put("//SCRIPT", "src");
            xpathTransformer.setChildUrlRuleMap(childUrlRuleMap);
            Map<String, String> fieldRuleMap = newLinkedHashMap();
            fieldRuleMap.put("title", "//TITLE");
            fieldRuleMap.put("body", "//BODY");
            fieldRuleMap.put("pcount", "count(//P)");
            fieldRuleMap.put("true", "true()");
            fieldRuleMap.put("false", "false()");
            xpathTransformer.setFieldRuleMap(fieldRuleMap);
        }
        {
            xpathMapTransformer = new XpathTransformer();
            xpathMapTransformer.setName("xpathMapTransformer");
            Map<String, String> featureMap = newHashMap();
            featureMap.put("http://xml.org/sax/features/namespaces", "false");
            xpathMapTransformer.setFeatureMap(featureMap);
            Map<String, String> propertyMap = newHashMap();
            xpathMapTransformer.setPropertyMap(propertyMap);
            Map<String, String> childUrlRuleMap = newHashMap();
            childUrlRuleMap.put("//A", "href");
            childUrlRuleMap.put("//AREA", "href");
            childUrlRuleMap.put("//FRAME", "src");
            childUrlRuleMap.put("//IFRAME", "src");
            childUrlRuleMap.put("//IMG", "src");
            childUrlRuleMap.put("//LINK", "href");
            childUrlRuleMap.put("//SCRIPT", "src");
            xpathMapTransformer.setChildUrlRuleMap(childUrlRuleMap);
            Map<String, String> fieldRuleMap = newLinkedHashMap();
            fieldRuleMap.put("title", "//TITLE");
            fieldRuleMap.put("body", "//BODY");
            xpathMapTransformer.setFieldRuleMap(fieldRuleMap);
            xpathMapTransformer.setDataClass(Map.class);
        }
        {
            xpathEntityTransformer = new XpathTransformer();
            xpathEntityTransformer.setName("xpathMapTransformer");
            Map<String, String> featureMap = newHashMap();
            featureMap.put("http://xml.org/sax/features/namespaces", "false");
            xpathEntityTransformer.setFeatureMap(featureMap);
            Map<String, String> propertyMap = newHashMap();
            xpathEntityTransformer.setPropertyMap(propertyMap);
            Map<String, String> childUrlRuleMap = newHashMap();
            childUrlRuleMap.put("//A", "href");
            childUrlRuleMap.put("//AREA", "href");
            childUrlRuleMap.put("//FRAME", "src");
            childUrlRuleMap.put("//IFRAME", "src");
            childUrlRuleMap.put("//IMG", "src");
            childUrlRuleMap.put("//LINK", "href");
            childUrlRuleMap.put("//SCRIPT", "src");
            xpathEntityTransformer.setChildUrlRuleMap(childUrlRuleMap);
            Map<String, String> fieldRuleMap = newLinkedHashMap();
            fieldRuleMap.put("title", "//TITLE");
            fieldRuleMap.put("body", "//BODY");
            xpathEntityTransformer.setFieldRuleMap(fieldRuleMap);
            xpathEntityTransformer.setDataClass(TestEntity.class);
        }
    }

    public void test_storeData() throws Exception {
        final String result = "<?xml version=\"1.0\"?>\n"//
                + "<doc>\n"//
                + "<field name=\"title\"><list><item>タイトル</item></list></field>\n"//
                + "<field name=\"body\"><list><item>第一章 第一節 ほげほげふがふが LINK 第2章 第2節</item></list></field>\n"//
                + "<field name=\"pcount\">2.0</field>\n"//
                + "<field name=\"true\">true</field>\n"//
                + "<field name=\"false\">false</field>\n"//
                + "</doc>";

        final ResponseData responseData = new ResponseData();
        responseData.setResponseBody(ResourceUtil
                .getResourceAsFile("html/test1.html"), false);
        responseData.setCharSet(Constants.UTF_8);
        final ResultData resultData = new ResultData();
        xpathTransformer.storeData(responseData, resultData);
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
        accessResultDataImpl.setTransformerName("xpathTransformer");

        final Object obj = xpathTransformer.getData(accessResultDataImpl);
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
            final Object obj = xpathTransformer.getData(accessResultDataImpl);
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
        accessResultDataImpl.setTransformerName("xpathTransformer");

        final Object obj = xpathTransformer.getData(accessResultDataImpl);
        assertNull(obj);
    }

    public void test_dataClass() {
        assertNull(xpathTransformer.dataClass);
        assertEquals(Map.class, xpathMapTransformer.dataClass);
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
        accessResultDataImpl.setTransformerName("xpathMapTransformer");

        final Object obj = xpathMapTransformer.getData(accessResultDataImpl);
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
        accessResultDataImpl.setTransformerName("xpathEntityTransformer");

        final Object obj = xpathEntityTransformer.getData(accessResultDataImpl);
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
        accessResultDataImpl.setTransformerName("xpathEntityTransformer");

        final Object obj = xpathEntityTransformer.getData(accessResultDataImpl);
        assertTrue(obj instanceof TestEntity);
        final TestEntity entity = (TestEntity) obj;
        assertEquals("タイトル", entity.getTitle());
        assertEquals("第一章 第一節 ほげほげふがふが LINK 第2章 第2節", entity.getBody());
        final List<String> list = new ArrayList<String>();
        assertEquals(list, entity.getList());
    }
}
