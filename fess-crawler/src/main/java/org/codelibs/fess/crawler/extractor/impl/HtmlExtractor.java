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
package org.codelibs.fess.crawler.extractor.impl;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.xpath.XPathEvaluationResult;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathNodes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.util.XPathAPI;
import org.codelibs.nekohtml.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Extracts text content from HTML documents.
 */
public class HtmlExtractor extends AbstractXmlExtractor {
    /** Logger for this class. */
    protected static final Logger logger = LogManager.getLogger(HtmlExtractor.class);

    /** Pattern for extracting charset from meta tags. */
    protected Pattern metaCharsetPattern = Pattern.compile("<meta.*content\\s*=\\s*['\"].*;\\s*charset=([\\w\\d\\-_]*)['\"]\\s*/?>",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for HTML tags.
     */
    protected Pattern htmlTagPattern = Pattern.compile("<[^>]+>");

    /** Map of parser features. */
    protected Map<String, String> featureMap = new HashMap<>();

    /** Map of parser properties. */
    protected Map<String, String> propertyMap = new HashMap<>();

    /** XPath expression for extracting content from the document body. */
    protected String contentXpath = "//BODY";

    /** Map of metadata field names to their corresponding XPath expressions. */
    protected Map<String, String> metadataXpathMap = new HashMap<>();

    /** Thread-local instance of XPathAPI for thread-safe XPath evaluation. */
    private final ThreadLocal<XPathAPI> xpathAPI = new ThreadLocal<>();

    /**
     * Creates a new HtmlExtractor instance.
     */
    public HtmlExtractor() {
        super();
    }

    @Override
    protected ExtractData createExtractData(final String content) {
        final DOMParser parser = getDomParser();
        try (final Reader reader = new StringReader(content)) {
            parser.parse(new InputSource(reader));
        } catch (final Exception e) {
            logger.warn("Failed to parse the content.", e);
            return new ExtractData(extractString(content));
        }

        final Document document = parser.getDocument();
        try {
            final ExtractData extractData = new ExtractData(
                    StreamUtil.stream(getStringsByXPath(document, contentXpath)).get(stream -> stream.collect(Collectors.joining(" "))));
            metadataXpathMap.entrySet().stream().forEach(e -> {
                extractData.putValues(e.getKey(), getStringsByXPath(document, e.getValue()));
            });
            return extractData;
        } finally {
            xpathAPI.remove();
        }
    }

    /**
     * Extracts strings from a document using the specified XPath expression.
     *
     * @param document the DOM document to extract strings from
     * @param path the XPath expression to evaluate
     * @return an array of strings extracted from the document
     */
    protected String[] getStringsByXPath(final Document document, final String path) {
        try {
            final XPathEvaluationResult<?> xObj = getXPathAPI().eval(document, path);
            switch (xObj.type()) {
            case BOOLEAN:
                final Boolean b = (Boolean) xObj.value();
                return new String[] { b.toString() };
            case NUMBER:
                final Number d = (Number) xObj.value();
                return new String[] { d.toString() };
            case STRING:
                final String str = (String) xObj.value();
                return new String[] { str.trim() };
            case NODESET:
                final XPathNodes nodeList = (XPathNodes) xObj.value();
                final List<String> strList = new ArrayList<>();
                for (int i = 0; i < nodeList.size(); i++) {
                    final Node node = nodeList.get(i);
                    strList.add(node.getTextContent());
                }
                return strList.toArray(n -> new String[n]);
            case NODE:
                final Node node = (Node) xObj.value();
                return new String[] { node.getTextContent() };
            default:
                Object obj = xObj.value();
                if (obj == null) {
                    obj = "";
                }
                return new String[] { obj.toString() };
            }
        } catch (final XPathException e) {
            logger.warn("Failed to parse the content by {}", path, e);
            return StringUtil.EMPTY_STRINGS;
        }

    }

    /**
     * Creates and configures a DOM parser for parsing HTML content.
     *
     * @return a configured DOMParser instance
     * @throws CrawlerSystemException if the parser configuration is invalid
     */
    protected DOMParser getDomParser() {
        try {
            final DOMParser parser = new DOMParser();
            // feature
            for (final Map.Entry<String, String> entry : featureMap.entrySet()) {
                parser.setFeature(entry.getKey(), "true".equalsIgnoreCase(entry.getValue()));
            }

            // property
            for (final Map.Entry<String, String> entry : propertyMap.entrySet()) {
                parser.setProperty(entry.getKey(), entry.getValue());
            }

            return parser;
        } catch (final Exception e) {
            throw new CrawlerSystemException("Invalid parser configuration.", e);
        }
    }

    /**
     * Gets a thread-local XPathAPI instance for thread-safe XPath evaluation.
     *
     * @return the XPathAPI instance for the current thread
     */
    protected XPathAPI getXPathAPI() {
        XPathAPI cachedXPathAPI = xpathAPI.get();
        if (cachedXPathAPI == null) {
            cachedXPathAPI = new XPathAPI();
            xpathAPI.set(cachedXPathAPI);
        }
        return cachedXPathAPI;
    }

    /**
     * Adds a metadata field with its corresponding XPath expression for extraction.
     *
     * @param name the name of the metadata field
     * @param xpath the XPath expression to extract the metadata value
     */
    public void addMetadata(final String name, final String xpath) {
        metadataXpathMap.put(name, xpath);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.extractor.impl.AbstractXmlExtractor#getEncodingPattern()
     */
    @Override
    protected Pattern getEncodingPattern() {
        return metaCharsetPattern;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.extractor.impl.AbstractXmlExtractor#getTagPattern()
     */
    @Override
    protected Pattern getTagPattern() {
        return htmlTagPattern;
    }

    /**
     * Gets the pattern used for extracting charset from meta tags.
     *
     * @return the meta charset pattern
     */
    public Pattern getMetaCharsetPattern() {
        return metaCharsetPattern;
    }

    /**
     * Sets the pattern used for extracting charset from meta tags.
     *
     * @param metaCharsetPattern the meta charset pattern to set
     */
    public void setMetaCharsetPattern(final Pattern metaCharsetPattern) {
        this.metaCharsetPattern = metaCharsetPattern;
    }

    /**
     * Gets the pattern used for matching HTML tags.
     *
     * @return the HTML tag pattern
     */
    public Pattern getHtmlTagPattern() {
        return htmlTagPattern;
    }

    /**
     * Sets the pattern used for matching HTML tags.
     *
     * @param htmlTagPattern the HTML tag pattern to set
     */
    public void setHtmlTagPattern(final Pattern htmlTagPattern) {
        this.htmlTagPattern = htmlTagPattern;
    }

    /**
     * Gets the map of parser features.
     *
     * @return the feature map
     */
    public Map<String, String> getFeatureMap() {
        return featureMap;
    }

    /**
     * Sets the map of parser features.
     *
     * @param featureMap the feature map to set
     */
    public void setFeatureMap(final Map<String, String> featureMap) {
        this.featureMap = featureMap;
    }

    /**
     * Gets the map of parser properties.
     *
     * @return the property map
     */
    public Map<String, String> getPropertyMap() {
        return propertyMap;
    }

    /**
     * Sets the map of parser properties.
     *
     * @param propertyMap the property map to set
     */
    public void setPropertyMap(final Map<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }
}
