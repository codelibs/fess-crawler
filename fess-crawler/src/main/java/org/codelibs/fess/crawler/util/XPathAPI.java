/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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

import java.util.function.Consumer;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathEvaluationResult;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathNodes;

import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.w3c.dom.Node;

/**
 * The XPathAPI class provides methods to evaluate XPath expressions and select nodes or node lists from an XML document.
 * It uses the javax.xml.xpath.XPath and javax.xml.xpath.XPathFactory classes to create and evaluate XPath expressions.
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * XPathAPI xPathAPI = new XPathAPI();
 * NodeList nodes = xPathAPI.selectNodeList(contextNode, "//example");
 * Node singleNode = xPathAPI.selectSingleNode(contextNode, "//example[1]");
 * XPathEvaluationResult<?> result = xPathAPI.eval(contextNode, "count(//example)");
 * }
 * </pre>
 *
 * <p>Note: This class is not thread-safe. Each thread should create its own instance of XPathAPI.</p>
 *
 * @see javax.xml.xpath.XPath
 * @see javax.xml.xpath.XPathFactory
 * @see javax.xml.xpath.XPathExpressionException
 */
public class XPathAPI {

    private final XPath xPath;

    public XPathAPI() {
        xPath = createXPath(f -> {});
    }

    /**
     * Creates an instance of {@link XPath} using the provided {@link Consumer} to configure the {@link XPathFactory}.
     *
     * @param builder a {@link Consumer} that accepts an {@link XPathFactory} and configures it.
     * @return a configured instance of {@link XPath}.
     * @throws CrawlerSystemException if an error occurs while creating the {@link XPath} instance.
     */
    public XPath createXPath(final Consumer<XPathFactory> builder) {
        try {
            final XPathFactory factory = XPathFactory.newInstance();
            builder.accept(factory);
            return factory.newXPath();
        } catch (final Exception e) {
            throw new CrawlerSystemException("Failed to create XPath instance.", e);
        }
    }

    /**
     *  Use an XPath string to select a nodelist.
     *  XPath namespace prefixes are resolved from the contextNode.
     *
     *  @param contextNode The node to start searching from.
     *  @param expression A valid XPath string.
     *  @return A XPathNodes, should never be null.
     *
     * @throws XPathExpressionException
     */
    public XPathNodes selectNodeList(final Node contextNode, final String expression) throws XPathExpressionException {
        return xPath.evaluateExpression(expression, contextNode, XPathNodes.class);
    }

    /**
     *  Evaluate XPath string to an XObject.
     *  XPath namespace prefixes are resolved from the namespaceNode.
     *  The implementation of this is a little slow, since it creates
     *  a number of objects each time it is called.  This could be optimized
     *  to keep the same objects around, but then thread-safety issues would arise.
     *
     *  @param contextNode The node to start searching from.
     *  @param expression A valid XPath string.
     *  @return An XPathEvaluationResult, which can be used to obtain a string, number, nodelist, etc, should never be null.
     *
     * @throws XPathExpressionException
     */
    public XPathEvaluationResult<?> eval(final Node contextNode, final String expression) throws XPathExpressionException {
        return xPath.evaluateExpression(expression, contextNode);
    }

    /**
     * Use an XPath string to select a single node.
     *
     * @param contextNode The node to start searching from.
     * @param expression A valid XPath string.
     * @return The first node found that matches the XPath, or null.
     *
     * @throws XPathExpressionException
     */
    public Node selectSingleNode(final Node contextNode, final String expression) throws XPathExpressionException {
        return xPath.evaluateExpression(expression, contextNode, Node.class);
    }
}
