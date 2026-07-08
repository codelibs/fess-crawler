/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResultDataImpl;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.entity.TestEntity;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

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
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

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
            xmlTransformer.init();
        }
        {
            xmlNsTransformer = new XmlTransformer();
            xmlNsTransformer.setName("xmlNsTransformer");
            xmlNsTransformer.setNamespaceAware(true);
            Map<String, String> fieldRuleMap = newLinkedHashMap();
            fieldRuleMap.put("name", "//hoge:address/hoge:item/hoge:name");
            fieldRuleMap.put("access", "//hoge:address/hoge:item/hoge:access");
            fieldRuleMap.put("image", "//hoge:address/hoge:item/hoge:image/@file");
            fieldRuleMap.put("email", "//hoge:address/hoge:item/hoge:access[@kind='email']");
            fieldRuleMap.put("url", "//hoge:address/hoge:item/hoge:access[@kind='url']");
            fieldRuleMap.put("tel", "//hoge:address/hoge:item/hoge:access[@kind='tel']");
            xmlNsTransformer.setFieldRuleMap(fieldRuleMap);
            xmlNsTransformer.init();
        }
        {
            xmlMapTransformer = new XmlTransformer();
            xmlMapTransformer.setName("xmlMapTransformer");
            xmlMapTransformer.setDataClass(Map.class);
            Map<String, String> fieldRuleMap = newLinkedHashMap();
            fieldRuleMap.put("name", "//hoge:address/hoge:item/hoge:name");
            fieldRuleMap.put("access", "//hoge:address/hoge:item/hoge:access");
            fieldRuleMap.put("image", "//hoge:address/hoge:item/hoge:image/@file");
            fieldRuleMap.put("email", "//hoge:address/hoge:item/hoge:access[@kind='email']");
            fieldRuleMap.put("url", "//hoge:address/hoge:item/hoge:access[@kind='url']");
            fieldRuleMap.put("tel", "//hoge:address/hoge:item/hoge:access[@kind='tel']");
            xmlMapTransformer.setFieldRuleMap(fieldRuleMap);
            xmlMapTransformer.init();
        }
        {
            xmlEntityTransformer = new XmlTransformer();
            xmlEntityTransformer.setName("xmlEntityTransformer");
            xmlEntityTransformer.setDataClass(TestEntity.class);
            Map<String, String> fieldRuleMap = newLinkedHashMap();
            fieldRuleMap.put("name", "//hoge:address/hoge:item/hoge:name");
            fieldRuleMap.put("access", "//hoge:address/hoge:item/hoge:access");
            fieldRuleMap.put("image", "//hoge:address/hoge:item/hoge:image/@file");
            fieldRuleMap.put("email", "//hoge:address/hoge:item/hoge:access[@kind='email']");
            fieldRuleMap.put("url", "//hoge:address/hoge:item/hoge:access[@kind='url']");
            fieldRuleMap.put("tel", "//hoge:address/hoge:item/hoge:access[@kind='tel']");
            xmlEntityTransformer.setFieldRuleMap(fieldRuleMap);
            xmlEntityTransformer.init();
        }
    }

    @Test
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
        responseData.setResponseBody(ResourceUtil.getResourceAsFile("extractor/test.xml"), false);
        responseData.setCharSet(Constants.UTF_8);
        final ResultData resultData = xmlTransformer.transform(responseData);
        assertEquals(result, new String(resultData.getData(), Constants.UTF_8));
    }

    @Test
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
        responseData.setResponseBody(ResourceUtil.getResourceAsFile("extractor/test_ns.xml"), false);
        responseData.setCharSet(Constants.UTF_8);
        final ResultData resultData = xmlNsTransformer.transform(responseData);
        assertEquals(result, new String(resultData.getData(), Constants.UTF_8));
    }

    /**
     * Runs {@link XmlTransformer#transform(ResponseData)} on two different XML documents in
     * sequence on the same transformer instance. This locks down that the reused, per-thread
     * {@link javax.xml.parsers.DocumentBuilderFactory} and {@link org.codelibs.fess.crawler.util.XPathAPI}
     * do not leak configuration/state from one document to the next.
     */
    @Test
    public void test_transform_sequentialDocuments() throws Exception {
        final String result1 = "<?xml version=\"1.0\"?>\n"//
                + "<doc>\n"//
                + "<field name=\"name\"><list><item>鈴木太郎</item><item>佐藤二朗</item><item>田中花子</item></list></field>\n"//
                + "<field name=\"access\"><list><item></item><item>http://www.taro.com/</item><item>jiro@hoge.foo.bar</item><item>090-xxxx-xxxx</item></list></field>\n"//
                + "<field name=\"image\"><list><item>taro.png</item><item>jiro.png</item><item>hanako.png</item></list></field>\n"//
                + "<field name=\"email\"><list><item></item><item>jiro@hoge.foo.bar</item></list></field>\n"//
                + "<field name=\"url\">http://www.taro.com/</field>\n"//
                + "<field name=\"tel\">090-xxxx-xxxx</field>\n"//
                + "</doc>";
        final String result2 = "<?xml version=\"1.0\"?>\n"//
                + "<doc>\n"//
                + "<field name=\"name\">山田三郎</field>\n"//
                + "<field name=\"access\"><list><item>saburo@example.com</item><item>http://www.saburo.com/</item></list></field>\n"//
                + "<field name=\"image\">saburo.png</field>\n"//
                + "<field name=\"email\">saburo@example.com</field>\n"//
                + "<field name=\"url\">http://www.saburo.com/</field>\n"//
                + "</doc>";

        final ResponseData responseData1 = new ResponseData();
        responseData1.setResponseBody(ResourceUtil.getResourceAsFile("extractor/test.xml"), false);
        responseData1.setCharSet(Constants.UTF_8);
        final ResultData resultData1 = xmlTransformer.transform(responseData1);
        assertEquals(result1, new String(resultData1.getData(), Constants.UTF_8));

        final ResponseData responseData2 = new ResponseData();
        responseData2.setResponseBody(ResourceUtil.getResourceAsFile("extractor/test2.xml"), false);
        responseData2.setCharSet(Constants.UTF_8);
        final ResultData resultData2 = xmlTransformer.transform(responseData2);
        assertEquals(result2, new String(resultData2.getData(), Constants.UTF_8));

        // run the first document again to make sure the second call did not leave state behind either
        final ResponseData responseData3 = new ResponseData();
        responseData3.setResponseBody(ResourceUtil.getResourceAsFile("extractor/test.xml"), false);
        responseData3.setCharSet(Constants.UTF_8);
        final ResultData resultData3 = xmlTransformer.transform(responseData3);
        assertEquals(result1, new String(resultData3.getData(), Constants.UTF_8));
    }

    /**
     * Namespaced variant of {@link #test_transform_sequentialDocuments()}. The two documents
     * declare the SAME "hoge" prefix bound to DIFFERENT namespace URIs, which specifically
     * exercises {@link XmlTransformer#getNodeList(org.w3c.dom.Document, String)}: a compiled
     * {@code XPathExpression} resolves namespace prefixes once at compile time, so reusing a
     * compiled expression - or an XPath whose namespace context was rebound after compilation -
     * across documents with different namespace declarations would silently return no nodes for
     * the second document.
     */
    @Test
    public void test_transformNs_sequentialDocuments() throws Exception {
        final String result1 = "<?xml version=\"1.0\"?>\n"//
                + "<doc>\n"//
                + "<field name=\"name\"><list><item>鈴木太郎</item><item>佐藤二朗</item><item>田中花子</item></list></field>\n"//
                + "<field name=\"access\"><list><item></item><item>http://www.taro.com/</item><item>jiro@hoge.foo.bar</item><item>090-xxxx-xxxx</item></list></field>\n"//
                + "<field name=\"image\"><list><item>taro.png</item><item>jiro.png</item><item>hanako.png</item></list></field>\n"//
                + "<field name=\"email\"><list><item></item><item>jiro@hoge.foo.bar</item></list></field>\n"//
                + "<field name=\"url\">http://www.taro.com/</field>\n"//
                + "<field name=\"tel\">090-xxxx-xxxx</field>\n"//
                + "</doc>";
        final String result2 = "<?xml version=\"1.0\"?>\n"//
                + "<doc>\n"//
                + "<field name=\"name\">山田三郎</field>\n"//
                + "<field name=\"access\"><list><item>saburo@example.com</item><item>http://www.saburo.com/</item></list></field>\n"//
                + "<field name=\"image\">saburo.png</field>\n"//
                + "<field name=\"email\">saburo@example.com</field>\n"//
                + "<field name=\"url\">http://www.saburo.com/</field>\n"//
                + "</doc>";

        final ResponseData responseData1 = new ResponseData();
        responseData1.setResponseBody(ResourceUtil.getResourceAsFile("extractor/test_ns.xml"), false);
        responseData1.setCharSet(Constants.UTF_8);
        final ResultData resultData1 = xmlNsTransformer.transform(responseData1);
        assertEquals(result1, new String(resultData1.getData(), Constants.UTF_8));

        // test_ns2.xml re-declares the "hoge" prefix against a DIFFERENT namespace URI.
        final ResponseData responseData2 = new ResponseData();
        responseData2.setResponseBody(ResourceUtil.getResourceAsFile("extractor/test_ns2.xml"), false);
        responseData2.setCharSet(Constants.UTF_8);
        final ResultData resultData2 = xmlNsTransformer.transform(responseData2);
        assertEquals(result2, new String(resultData2.getData(), Constants.UTF_8));

        final ResponseData responseData3 = new ResponseData();
        responseData3.setResponseBody(ResourceUtil.getResourceAsFile("extractor/test_ns.xml"), false);
        responseData3.setCharSet(Constants.UTF_8);
        final ResultData resultData3 = xmlNsTransformer.transform(responseData3);
        assertEquals(result1, new String(resultData3.getData(), Constants.UTF_8));
    }

    /**
     * Concurrency smoke test: several threads share the same {@link XmlTransformer} instance and
     * each transform their own distinct, uniquely-namespaced XML document. Asserts no exception is
     * thrown and every thread observes its own correctly-resolved result, i.e. the per-thread
     * DocumentBuilderFactory/XPathAPI reuse does not leak state across threads or across the
     * repeated reuse of a pooled thread for different documents.
     */
    @Test
    public void test_transformNs_concurrentDocuments() throws Exception {
        final int taskCount = 20;
        final ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            final List<Future<Void>> futures = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                final int index = i;
                futures.add(executor.submit(() -> {
                    final String uri = "http://www.example.com/hoge" + index;
                    final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"//
                            + "<hoge:address xmlns:hoge=\"" + uri + "\">\n"//
                            + "<hoge:item><hoge:name>name-" + index + "</hoge:name>"//
                            + "<hoge:access kind=\"email\">user" + index + "@example.com</hoge:access>"//
                            + "<hoge:image file=\"img" + index + ".png\" /></hoge:item>\n"//
                            + "</hoge:address>";
                    final String expected = "<?xml version=\"1.0\"?>\n"//
                            + "<doc>\n"//
                            + "<field name=\"name\">name-" + index + "</field>\n"//
                            + "<field name=\"access\">user" + index + "@example.com</field>\n"//
                            + "<field name=\"image\">img" + index + ".png</field>\n"//
                            + "<field name=\"email\">user" + index + "@example.com</field>\n"//
                            + "</doc>";

                    final ResponseData responseData = new ResponseData();
                    responseData.setResponseBody(xml.getBytes(Constants.UTF_8_CHARSET));
                    responseData.setCharSet(Constants.UTF_8);
                    final ResultData resultData = xmlNsTransformer.transform(responseData);
                    assertEquals(expected, new String(resultData.getData(), Constants.UTF_8));
                    return null;
                }));
            }
            for (final Future<Void> future : futures) {
                future.get(30, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdown();
        }
    }

    @Test
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

    @Test
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
        } catch (final CrawlerSystemException e) {}
    }

    @Test
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

    @Test
    public void test_dataClass() {
        assertNull(xmlTransformer.dataClass);
        assertEquals(Map.class, xmlMapTransformer.dataClass);
    }

    @Test
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

    @Test
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

    @Test
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
