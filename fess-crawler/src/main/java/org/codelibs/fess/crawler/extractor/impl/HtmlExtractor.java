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
 * @author shinsuke
 *
 */
public class HtmlExtractor extends AbstractXmlExtractor {
    protected static final Logger logger = LogManager.getLogger(HtmlExtractor.class);

    protected Pattern metaCharsetPattern = Pattern.compile("<meta.*content\\s*=\\s*['\"].*;\\s*charset=([\\w\\d\\-_]*)['\"]\\s*/?>",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    protected Pattern htmlTagPattern = Pattern.compile("<[^>]+>");

    protected Map<String, String> featureMap = new HashMap<>();

    protected Map<String, String> propertyMap = new HashMap<>();

    protected String contentXpath = "//BODY";

    protected Map<String, String> metadataXpathMap = new HashMap<>();

    private final ThreadLocal<XPathAPI> xpathAPI = new ThreadLocal<>();

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

    protected DOMParser getDomParser() {
        final DOMParser parser = new DOMParser();
        try {
            // feature
            for (final Map.Entry<String, String> entry : featureMap.entrySet()) {
                parser.setFeature(entry.getKey(), "true".equalsIgnoreCase(entry.getValue()));
            }

            // property
            for (final Map.Entry<String, String> entry : propertyMap.entrySet()) {
                parser.setProperty(entry.getKey(), entry.getValue());
            }
        } catch (final Exception e) {
            throw new CrawlerSystemException("Invalid parser configuration.", e);
        }

        return parser;
    }

    protected XPathAPI getXPathAPI() {
        XPathAPI cachedXPathAPI = xpathAPI.get();
        if (cachedXPathAPI == null) {
            cachedXPathAPI = new XPathAPI();
            xpathAPI.set(cachedXPathAPI);
        }
        return cachedXPathAPI;
    }

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

    public Pattern getMetaCharsetPattern() {
        return metaCharsetPattern;
    }

    public void setMetaCharsetPattern(final Pattern metaCharsetPattern) {
        this.metaCharsetPattern = metaCharsetPattern;
    }

    public Pattern getHtmlTagPattern() {
        return htmlTagPattern;
    }

    public void setHtmlTagPattern(final Pattern htmlTagPattern) {
        this.htmlTagPattern = htmlTagPattern;
    }

    public Map<String, String> getFeatureMap() {
        return featureMap;
    }

    public void setFeatureMap(final Map<String, String> featureMap) {
        this.featureMap = featureMap;
    }

    public Map<String, String> getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(final Map<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }
}
