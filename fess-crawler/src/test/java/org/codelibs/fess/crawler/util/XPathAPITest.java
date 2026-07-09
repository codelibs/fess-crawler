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
package org.codelibs.fess.crawler.util;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathEvaluationResult;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathNodes;

import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Unit tests for {@link XPathAPI}, the shared XPath evaluation engine used by the
 * HTML/XML transformers and extractors. Covers both the happy paths (each result
 * type, matching/non-matching selections) and the error paths (invalid expressions,
 * factory configuration failures).
 *
 * @author CodeLibs
 */
public class XPathAPITest extends PlainTestCase {

    private static final String XML = "<root><title>Hello</title><item>a</item><item>b</item></root>";

    private static final String INVALID_EXPRESSION = "//a[1";

    private Document toDocument(final String xml) throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

    // -----------------------------------------------------
    //                                                  eval
    //                                                  ----
    @Test
    public void test_eval_nodeset() throws Exception {
        final Document document = toDocument(XML);
        final XPathAPI xPathAPI = new XPathAPI();

        final XPathEvaluationResult<?> result = xPathAPI.eval(document, "//item");
        assertEquals(XPathEvaluationResult.XPathResultType.NODESET, result.type());
        final XPathNodes nodes = (XPathNodes) result.value();
        assertEquals(2, nodes.size());
        assertEquals("a", nodes.get(0).getTextContent());
        assertEquals("b", nodes.get(1).getTextContent());
    }

    @Test
    public void test_eval_number() throws Exception {
        final Document document = toDocument(XML);
        final XPathAPI xPathAPI = new XPathAPI();

        final XPathEvaluationResult<?> result = xPathAPI.eval(document, "count(//item)");
        assertEquals(XPathEvaluationResult.XPathResultType.NUMBER, result.type());
        assertEquals(2, ((Number) result.value()).intValue());
    }

    @Test
    public void test_eval_boolean() throws Exception {
        final Document document = toDocument(XML);
        final XPathAPI xPathAPI = new XPathAPI();

        final XPathEvaluationResult<?> resultTrue = xPathAPI.eval(document, "boolean(//title)");
        assertEquals(XPathEvaluationResult.XPathResultType.BOOLEAN, resultTrue.type());
        assertTrue((Boolean) resultTrue.value());

        final XPathEvaluationResult<?> resultFalse = xPathAPI.eval(document, "boolean(//nosuch)");
        assertEquals(XPathEvaluationResult.XPathResultType.BOOLEAN, resultFalse.type());
        assertFalse((Boolean) resultFalse.value());
    }

    @Test
    public void test_eval_string() throws Exception {
        final Document document = toDocument(XML);
        final XPathAPI xPathAPI = new XPathAPI();

        final XPathEvaluationResult<?> result = xPathAPI.eval(document, "string(//title)");
        assertEquals(XPathEvaluationResult.XPathResultType.STRING, result.type());
        assertEquals("Hello", result.value());
    }

    @Test
    public void test_eval_invalidExpression() throws Exception {
        final Document document = toDocument(XML);
        final XPathAPI xPathAPI = new XPathAPI();

        try {
            xPathAPI.eval(document, INVALID_EXPRESSION);
            fail();
        } catch (final XPathExpressionException e) {
            // NOP
        }
    }

    // -----------------------------------------------------
    //                                        selectNodeList
    //                                        --------------
    @Test
    public void test_selectNodeList_match() throws Exception {
        final Document document = toDocument(XML);
        final XPathAPI xPathAPI = new XPathAPI();

        final XPathNodes nodes = xPathAPI.selectNodeList(document, "//item");
        assertEquals(2, nodes.size());
        assertEquals("a", nodes.get(0).getTextContent());
        assertEquals("b", nodes.get(1).getTextContent());
    }

    @Test
    public void test_selectNodeList_noMatch() throws Exception {
        final Document document = toDocument(XML);
        final XPathAPI xPathAPI = new XPathAPI();

        final XPathNodes nodes = xPathAPI.selectNodeList(document, "//nosuch");
        assertNotNull(nodes);
        assertEquals(0, nodes.size());
    }

    @Test
    public void test_selectNodeList_invalidExpression() throws Exception {
        final Document document = toDocument(XML);
        final XPathAPI xPathAPI = new XPathAPI();

        try {
            xPathAPI.selectNodeList(document, INVALID_EXPRESSION);
            fail();
        } catch (final XPathExpressionException e) {
            // NOP
        }
    }

    @Test
    public void test_selectNodeList_namespaceContextDoesNotLeak() throws Exception {
        final Document document = toDocument("<root xmlns:hoge=\"http://example.com/hoge\"><hoge:item>value</hoge:item></root>");
        final XPathAPI xPathAPI = new XPathAPI();
        final Field xPathField = XPathAPI.class.getDeclaredField("xPath");
        xPathField.setAccessible(true);
        final XPath xPath = (XPath) xPathField.get(xPathAPI);
        final NamespaceContext correctNamespaceContext = new NamespaceContext() {
            @Override
            public String getNamespaceURI(final String prefix) {
                return "hoge".equals(prefix) ? "http://example.com/hoge" : XMLConstants.NULL_NS_URI;
            }

            @Override
            public String getPrefix(final String namespaceURI) {
                return null;
            }

            @Override
            public Iterator<String> getPrefixes(final String namespaceURI) {
                return null;
            }
        };
        xPath.setNamespaceContext(correctNamespaceContext);

        final XPathNodes nodes = xPathAPI.selectNodeList(document, "//hoge:item", new NamespaceContext() {
            @Override
            public String getNamespaceURI(final String prefix) {
                return "hoge".equals(prefix) ? "http://example.com/other" : XMLConstants.NULL_NS_URI;
            }

            @Override
            public String getPrefix(final String namespaceURI) {
                return null;
            }

            @Override
            public Iterator<String> getPrefixes(final String namespaceURI) {
                return null;
            }
        });
        assertEquals(0, nodes.size());

        final XPathNodes restoredNodes = xPathAPI.selectNodeList(document, "//hoge:item");
        assertEquals(1, restoredNodes.size());
        assertEquals("value", restoredNodes.get(0).getTextContent());
    }

    // -----------------------------------------------------
    //                                      selectSingleNode
    //                                      ----------------
    @Test
    public void test_selectSingleNode_match() throws Exception {
        final Document document = toDocument(XML);
        final XPathAPI xPathAPI = new XPathAPI();

        final Node node = xPathAPI.selectSingleNode(document, "//title");
        assertNotNull(node);
        assertEquals("Hello", node.getTextContent());
    }

    @Test
    public void test_selectSingleNode_noMatch() throws Exception {
        final Document document = toDocument(XML);
        final XPathAPI xPathAPI = new XPathAPI();

        final Node node = xPathAPI.selectSingleNode(document, "//nosuch");
        assertNull(node);
    }

    @Test
    public void test_selectSingleNode_invalidExpression() throws Exception {
        final Document document = toDocument(XML);
        final XPathAPI xPathAPI = new XPathAPI();

        try {
            xPathAPI.selectSingleNode(document, INVALID_EXPRESSION);
            fail();
        } catch (final XPathExpressionException e) {
            // NOP
        }
    }

    // -----------------------------------------------------
    //                                           createXPath
    //                                           -----------
    @Test
    public void test_createXPath_valid() throws Exception {
        final XPathAPI xPathAPI = new XPathAPI();

        final XPath xPath = xPathAPI.createXPath(factory -> {});
        assertNotNull(xPath);
        // The created instance must be usable for evaluation.
        final Document document = toDocument(XML);
        assertEquals("Hello", xPath.evaluate("string(//title)", document));
    }

    @Test
    public void test_createXPath_builderThrows() {
        final XPathAPI xPathAPI = new XPathAPI();

        try {
            xPathAPI.createXPath(factory -> {
                throw new RuntimeException("Failed to configure factory.");
            });
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }
}
