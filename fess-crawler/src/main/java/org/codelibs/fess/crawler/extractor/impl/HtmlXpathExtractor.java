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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathNodes;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.util.XPathAPI;
import org.codelibs.nekohtml.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import jakarta.annotation.Resource;

/**
 * {@link HtmlXpathExtractor} is an implementation of the {@link org.codelibs.fess.crawler.extractor.Extractor} interface.
 * It uses XPath expressions to extract text content from HTML documents.
 * <p>
 * This class provides methods to configure the XPath expressions, parser features, and properties.
 * It also includes caching mechanism for XPathAPI instances to improve performance.
 * </p>
 * <p>
 * The extracted text is obtained from the nodes selected by the {@code targetNodePath} XPath expression.
 * The default value for {@code targetNodePath} is "//HTML/BODY | //@alt | //@title", which selects the body of the HTML document,
 * as well as the alt and title attributes.
 * </p>
 * <p>
 * The class uses {@link DOMParser} to parse HTML documents and {@link XPathAPI} to execute XPath queries.
 * It also provides methods to add custom features and properties to the {@link DOMParser}.
 * </p>
 * <p>
 * The encoding of the HTML document is automatically detected using a regular expression that matches the charset attribute in the meta tag.
 * </p>
 *
 */
public class HtmlXpathExtractor extends AbstractXmlExtractor {
    /**
     * Regular expression pattern to match the charset attribute in the meta tag of HTML documents.
     * The pattern captures the charset value specified in the content attribute of the meta tag.
     * Example: &lt;meta http-equiv="Content-Type" content="text/html; charset=UTF-8"&gt;
     */
    protected Pattern metaCharsetPattern = Pattern.compile("<meta.*content\\s*=\\s*['\"].*;\\s*charset=([\\w\\d\\-_]*)['\"]\\s*/?>",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    /**
     * Map of features for the DOM parser.
     */
    protected Map<String, String> featureMap = new HashMap<>();

    /** Map of properties for the DOM parser. */
    protected Map<String, String> propertyMap = new HashMap<>();

    /** XPath expression to select target nodes for text extraction. */
    protected String targetNodePath = "//HTML/BODY | //@alt | //@title";

    /** Cache for XPathAPI instances to improve performance. */
    protected LoadingCache<String, XPathAPI> xpathAPICache;

    /** Cache duration in minutes for XPathAPI instances. */
    protected long cacheDuration = 10; // min

    /**
     * Creates a new HtmlXpathExtractor instance.
     */
    public HtmlXpathExtractor() {
        super();
    }

    /**
     * Initializes the XPathAPI cache with a specified cache duration.
     * This method is called to set up the cache for XPathAPI instances.
     */
    @Resource
    public void init() {
        xpathAPICache =
                CacheBuilder.newBuilder().expireAfterAccess(cacheDuration, TimeUnit.MINUTES).build(new CacheLoader<String, XPathAPI>() {
                    @Override
                    public XPathAPI load(final String key) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("created XPathAPI by {}", key);
                        }
                        return new XPathAPI();
                    }
                });
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.extractor.Extractor#getText(java.io.InputStream,
     * java.util.Map)
     */
    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        if (in == null) {
            throw new CrawlerSystemException("The inputstream is null.");
        }
        try {
            final BufferedInputStream bis = new BufferedInputStream(in);
            final String enc = getEncoding(bis);

            final DOMParser parser = getDomParser();
            final InputSource inputSource = new InputSource(bis);
            inputSource.setEncoding(enc);
            parser.parse(inputSource);
            final Document document = parser.getDocument();

            final StringBuilder buf = new StringBuilder(255);
            final XPathNodes nodeList = getXPathAPI().selectNodeList(document, targetNodePath);
            for (int i = 0; i < nodeList.size(); i++) {
                final Node node = nodeList.get(i);
                buf.append(node.getTextContent()).append(' ');
            }
            return new ExtractData(buf.toString().replaceAll("\\s+", " ").trim());
        } catch (final Exception e) {
            throw new ExtractException(e);
        }
    }

    /**
     * Gets an XPathAPI instance from the cache for the current thread.
     *
     * @return the XPathAPI instance
     */
    protected XPathAPI getXPathAPI() {
        try {
            return xpathAPICache.get(Thread.currentThread().getName());
        } catch (final ExecutionException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to retrieval a cache.", e);
            }
            return new XPathAPI();
        }
    }

    /**
     * Creates and configures a DOM parser with the specified features and properties.
     *
     * @return a configured DOM parser instance
     * @throws CrawlerSystemException if the parser configuration is invalid
     */
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
        // not used
        return null;
    }

    /**
     * Adds a feature to the extractor.
     *
     * @param key   the key of the feature
     * @param value the value of the feature
     */
    public void addFeature(final String key, final String value) {
        if (StringUtil.isBlank(key) || StringUtil.isBlank(value)) {
            throw new CrawlerSystemException("key or value is null.");
        }

        featureMap.put(key, value);
    }

    /**
     * Adds a property to the extractor.
     *
     * @param key   the key of the property
     * @param value the value of the property
     */
    public void addProperty(final String key, final String value) {
        if (StringUtil.isBlank(key) || StringUtil.isBlank(value)) {
            throw new CrawlerSystemException("key or value is null.");
        }

        propertyMap.put(key, value);
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

    /**
     * Gets the pattern for extracting charset from meta tags.
     *
     * @return the meta charset pattern
     */
    public Pattern getMetaCharsetPattern() {
        return metaCharsetPattern;
    }

    /**
     * Sets the pattern for extracting charset from meta tags.
     *
     * @param metaCharsetPattern the meta charset pattern to set
     */
    public void setMetaCharsetPattern(final Pattern metaCharsetPattern) {
        this.metaCharsetPattern = metaCharsetPattern;
    }

    /**
     * Gets the XPath expression for selecting target nodes.
     *
     * @return the target node path
     */
    public String getTargetNodePath() {
        return targetNodePath;
    }

    /**
     * Sets the XPath expression for selecting target nodes.
     *
     * @param targetNodePath the target node path to set
     */
    public void setTargetNodePath(final String targetNodePath) {
        this.targetNodePath = targetNodePath;
    }

    /**
     * Sets the cache duration for XPathAPI instances.
     *
     * @param cacheDuration the cache duration in minutes
     */
    public void setCacheDuration(final long cacheDuration) {
        this.cacheDuration = cacheDuration;
    }
}
