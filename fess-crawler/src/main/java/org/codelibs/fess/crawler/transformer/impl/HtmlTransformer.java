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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathNodes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.helper.EncodingHelper;
import org.codelibs.fess.crawler.helper.UrlConvertHelper;
import org.codelibs.fess.crawler.util.CharUtil;
import org.codelibs.fess.crawler.util.XPathAPI;
import org.codelibs.nekohtml.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import jakarta.annotation.Resource;

/**
 * The {@code HtmlTransformer} class is responsible for transforming HTML responses
 * during the crawling process. It extracts data, identifies child URLs, and handles
 * character set encoding.
 * <p>
 * This class extends {@link AbstractTransformer} and utilizes various helper classes
 * and components from the Fess Crawler framework, such as {@link CrawlerContainer},
 * {@link UrlConvertHelper}, and {@link EncodingHelper}.
 * </p>
 * <p>
 * The transformation process involves:
 * </p>
 * <ol>
 *   <li>Determining the character set encoding of the HTML content.</li>
 *   <li>Storing the HTML content as data in the {@link ResultData}.</li>
 *   <li>Extracting child URLs from the HTML content based on configured rules.</li>
 *   <li>Handling redirect URLs specified in the response headers.</li>
 * </ol>
 * <p>
 * The class also provides methods for configuring features and properties of the
 * underlying DOM parser, as well as defining rules for extracting child URLs
 * from specific HTML tags and attributes.
 * </p>
 *
 * <p>
 * <b>Configuration:</b>
 * </p>
 * <ul>
 *   <li><b>featureMap:</b> A map of features to be set on the DOM parser.</li>
 *   <li><b>propertyMap:</b> A map of properties to be set on the DOM parser.</li>
 *   <li><b>childUrlRuleMap:</b> A map of HTML tag names to attribute names, used
 *       to extract child URLs.</li>
 *   <li><b>defaultEncoding:</b> The default character encoding to use if none is
 *       specified in the HTML content.</li>
 *   <li><b>preloadSizeForCharset:</b> The number of bytes to read from the input
 *       stream to determine the character set encoding.</li>
 *   <li><b>invalidUrlPattern:</b> A regular expression pattern used to identify
 *       invalid URLs.</li>
 * </ul>
 *
 * <p>
 * <b>Usage:</b>
 * </p>
 * <p>
 * The {@code transform} method is the main entry point for transforming an HTML
 * response. It takes a {@link ResponseData} object as input and returns a
 * {@link ResultData} object containing the extracted data and child URLs.
 * </p>
 */
public class HtmlTransformer extends AbstractTransformer {

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(HtmlTransformer.class);

    /**
     * Constructs a new HtmlTransformer.
     */
    public HtmlTransformer() {
        // Default constructor
    }

    /** Header name for location redirects. */
    protected static final String LOCATION_HEADER = "Location";

    /** The crawler container for dependency injection. */
    @Resource
    protected CrawlerContainer crawlerContainer;

    /** Map of parser features to configure the DOM parser. */
    protected Map<String, String> featureMap = new HashMap<>();

    /** Map of parser properties to configure the DOM parser. */
    protected Map<String, String> propertyMap = new HashMap<>();

    /** Map of HTML tag names to attribute names for extracting child URLs. */
    protected Map<String, String> childUrlRuleMap = new LinkedHashMap<>();

    /** Default character encoding to use when none is specified. */
    protected String defaultEncoding;

    /** Number of bytes to read from input stream to determine character set encoding.
     * If you want to follow a html spec, use 512. */
    protected int preloadSizeForCharset = 2048;

    /**
     * Pattern for invalid URLs.
     */
    protected Pattern invalidUrlPattern = Pattern.compile("^\\s*javascript:|" //
            + "^\\s*mailto:|" //
            + "^\\s*irc:|" //
            + "^\\s*skype:|" //
            + "^\\s*about:|" + "^\\s*fscommand:|" //
            + "^\\s*aim:|" //
            + "^\\s*msnim:|" //
            + "^\\s*news:|" //
            + "^\\s*tel:|" //
            + "^\\s*unsaved:|" //
            + "^\\s*data:|" //
            + "^\\s*android-app:|" //
            + "^\\s*ios-app:|" //
            + "^\\s*callto:", Pattern.CASE_INSENSITIVE);

    /** Thread-local XPathAPI instance for thread-safe XPath operations. */
    private final ThreadLocal<XPathAPI> xpathAPI = new ThreadLocal<>();

    @Override
    public ResultData transform(final ResponseData responseData) {
        if (responseData == null || !responseData.hasResponseBody()) {
            throw new CrawlingAccessException("No response body.");
        }

        // encoding
        updateCharset(responseData);

        final ResultData resultData = new ResultData();
        resultData.setTransformerName(getName());

        try {
            // data
            storeData(responseData, resultData);

            if (isHtml(responseData) && !responseData.isNoFollow()) {
                // urls
                storeChildUrls(responseData, resultData);
            }
        } finally {
            xpathAPI.remove();
        }

        final Object redirectUrlObj = responseData.getMetaDataMap().get(LOCATION_HEADER);
        if (redirectUrlObj instanceof String) {
            final UrlConvertHelper urlConvertHelper = crawlerContainer.getComponent("urlConvertHelper");
            final String redirectUrl;
            if (urlConvertHelper != null) {
                redirectUrl = urlConvertHelper.convert(redirectUrlObj.toString());
            } else {
                logger.warn("urlConvertHelper is null.");
                redirectUrl = redirectUrlObj.toString();
            }
            resultData.addUrl(RequestDataBuilder.newRequestData().get().url(redirectUrl).build());
        }

        return resultData;
    }

    /**
     * Checks if the response data represents HTML content.
     *
     * @param responseData the response data to check
     * @return true if the content is HTML, false otherwise
     */
    protected boolean isHtml(final ResponseData responseData) {
        final String mimeType = responseData.getMimeType();
        if ("text/html".equals(mimeType) || "application/xhtml+xml".equals(mimeType)) {
            return true;
        }
        return false;
    }

    /**
     * Adds a rule for extracting child URLs from HTML tags.
     *
     * @param tagName the HTML tag name
     * @param attrName the attribute name to extract URLs from
     */
    public void addChildUrlRule(final String tagName, final String attrName) {
        if (StringUtil.isNotBlank(tagName) && StringUtil.isNotBlank(attrName)) {
            childUrlRuleMap.put(tagName, attrName);
        }
    }

    /**
     * Gets the XPath API instance for this thread.
     *
     * @return the XPath API instance
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
     * Stores child URLs found in the HTML content.
     *
     * @param responseData the response data containing the HTML content
     * @param resultData the result data to store child URLs in
     * @throws CrawlingAccessException if URL extraction fails
     */
    protected void storeChildUrls(final ResponseData responseData, final ResultData resultData) {
        try (final InputStream is = responseData.getResponseBody()) {
            final DOMParser parser = getDomParser();
            parser.parse(new InputSource(is));
            final Document document = parser.getDocument();
            // base href
            final String baseHref = getBaseHref(document);
            URI uri;
            try {
                uri = new URI(baseHref == null ? responseData.getUrl() : baseHref);
            } catch (final Exception e) {
                uri = new URI(responseData.getUrl());
            }
            final URL url = uri.toURL();
            getChildUrlRules(responseData, resultData).forEach(entry -> {
                List<RequestData> requestDataList = new ArrayList<>();
                for (final String childUrl : getUrlFromTagAttribute(url, document, entry.getFirst(), entry.getSecond(),
                        responseData.getCharSet())) {
                    requestDataList.add(RequestDataBuilder.newRequestData().get().url(childUrl).build());
                }
                requestDataList = convertChildUrlList(requestDataList);
                resultData.addAllUrl(requestDataList);
            });

            resultData.addAllUrl(responseData.getChildUrlSet());

            final RequestData requestData = responseData.getRequestData();
            resultData.removeUrl(requestData);
            resultData.removeUrl(getDuplicateUrl(requestData));
        } catch (final CrawlerSystemException e) {
            throw e;
        } catch (final Exception e) {
            throw new CrawlerSystemException("Could not store data.", e);
        }
    }

    /**
     * Gets the child URL extraction rules as a stream of tag-attribute pairs.
     *
     * @param responseData the response data
     * @param resultData the result data
     * @return a stream of tag-attribute pairs
     */
    protected Stream<Pair<String, String>> getChildUrlRules(final ResponseData responseData, final ResultData resultData) {
        return childUrlRuleMap.entrySet().stream().map(e -> new Pair<>(e.getKey(), e.getValue()));
    }

    /**
     * Converts child URLs using the URL convert helper.
     *
     * @param requestDataList the list of request data to convert
     * @return the converted list of request data
     */
    protected List<RequestData> convertChildUrlList(final List<RequestData> requestDataList) {
        try {
            final UrlConvertHelper urlConvertHelper = crawlerContainer.getComponent("urlConvertHelper");
            for (final RequestData requestData : requestDataList) {
                requestData.setUrl(urlConvertHelper.convert(requestData.getUrl()));
            }
            return requestDataList;
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to convert child URLs.", e);
            }
        }
        return requestDataList;
    }

    /**
     * Stores the response data content in the result data.
     *
     * @param responseData the response data containing the content
     * @param resultData the result data to store the content in
     * @throws CrawlerSystemException if storing data fails
     */
    protected void storeData(final ResponseData responseData, final ResultData resultData) {
        try (final InputStream is = responseData.getResponseBody()) {
            final byte[] data = InputStreamUtil.getBytes(is);
            resultData.setData(data);
            resultData.setEncoding(responseData.getCharSet());
        } catch (final CrawlerSystemException e) {
            throw e;
        } catch (final Exception e) {
            throw new CrawlerSystemException("Could not store data.", e);
        }
    }

    /**
     * Updates the character set of the response data by detecting it from the content.
     *
     * @param responseData the response data to update
     */
    protected void updateCharset(final ResponseData responseData) {
        try (final InputStream is = responseData.getResponseBody()) {
            final String encoding = loadCharset(is);
            if (encoding == null) {
                if (defaultEncoding == null) {
                    responseData.setCharSet(Constants.UTF_8);
                } else if (responseData.getCharSet() == null) {
                    responseData.setCharSet(defaultEncoding);
                }
            } else {
                responseData.setCharSet(encoding.trim());
            }

            if (!isSupportedCharset(responseData.getCharSet())) {
                responseData.setCharSet(Constants.UTF_8);
            }
        } catch (final CrawlerSystemException e) {
            throw e;
        } catch (final Exception e) {
            throw new CrawlerSystemException("Could not load response data: " + responseData.getUrl(), e);
        }
    }

    /**
     * Checks if the specified charset is supported.
     *
     * @param charsetName the charset name to check
     * @return true if the charset is supported, false otherwise
     */
    protected boolean isSupportedCharset(final String charsetName) {
        if (charsetName == null) {
            return false;
        }
        try {
            Charset.forName(charsetName);
        } catch (final Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Loads and detects the character set from the input stream.
     *
     * @param inputStream the input stream to read from
     * @return the detected character set name, or null if not found
     * @throws CrawlingAccessException if an error occurs while reading the content
     */
    protected String loadCharset(final InputStream inputStream) {
        BufferedInputStream bis = null;
        String encoding = null;
        try {
            bis = new BufferedInputStream(inputStream);
            final byte[] buffer = new byte[preloadSizeForCharset];
            final int size = bis.read(buffer);
            if (size != -1) {
                final String content = new String(buffer, 0, size);
                encoding = parseCharset(content);
            }
        } catch (final IOException e) {
            throw new CrawlingAccessException("Could not load a content.", e);
        }

        return normalizeEncoding(encoding);
    }

    /**
     * Normalizes the encoding name using the encoding helper.
     *
     * @param encoding the encoding name to normalize
     * @return the normalized encoding name
     */
    protected String normalizeEncoding(final String encoding) {
        try {
            final EncodingHelper encodingHelper = crawlerContainer.getComponent("encodingHelper");
            return encodingHelper.normalize(encoding);
        } catch (final Exception e) {
            // NOP
        }
        return encoding;
    }

    /**
     * Parses the charset from the content string.
     *
     * @param content the content to parse
     * @return the parsed charset name, or null if not found
     */
    protected String parseCharset(final String content) {
        final Pattern pattern = Pattern.compile("; *charset *= *([a-zA-Z0-9\\-_]+)", Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Gets a duplicate URL by adding or removing a trailing slash.
     *
     * @param requestData the request data to create a duplicate for
     * @return the request data with the duplicate URL
     */
    protected RequestData getDuplicateUrl(final RequestData requestData) {
        final String url = requestData.getUrl();
        if (url.endsWith("/")) {
            requestData.setUrl(url.substring(0, url.length() - 1));
        } else {
            requestData.setUrl(url + "/");
        }
        return requestData;
    }

    /**
     * Creates and configures a DOM parser with the specified features and properties.
     *
     * @return a configured DOM parser
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
     * Gets the base href from the document's BASE tag.
     *
     * @param document the document to extract base href from
     * @return the base href URL, or null if not found
     */
    protected String getBaseHref(final Document document) {
        try {
            final XPathNodes list = getXPathAPI().selectNodeList(document, "//BASE");
            if (list.size() > 0) {
                final Node node = list.get(0);
                final Node attrNode = node.getAttributes().getNamedItem("href");
                if (attrNode != null) {
                    String attrValue = attrNode.getNodeValue();
                    if (StringUtil.isNotBlank(attrValue)) {
                        // if starting with www, append a protocol
                        if (attrValue.startsWith("www.")) {
                            attrValue = "http://" + attrValue;
                        }
                        return attrValue;
                    }
                }
            }
        } catch (final Exception e) {
            logger.warn("Could not get a base tag. ", e);
        }
        return null;
    }

    /**
     * Extracts URLs from HTML tag attributes using XPath.
     *
     * @param url the base URL for resolving relative URLs
     * @param document the document to extract URLs from
     * @param xpath the XPath expression to select elements
     * @param attr the attribute name to extract URLs from
     * @param encoding the character encoding to use
     * @return a list of extracted URLs
     */
    protected List<String> getUrlFromTagAttribute(final URL url, final Document document, final String xpath, final String attr,
            final String encoding) {
        if (logger.isDebugEnabled()) {
            logger.debug("Base URL: {}", url);
        }
        final List<String> urlList = new ArrayList<>();
        try {
            final XPathNodes list = getXPathAPI().selectNodeList(document, xpath);
            for (int i = 0; i < list.size(); i++) {
                final Node node = list.get(i);
                final Node attrNode = node.getAttributes().getNamedItem(attr);
                if (attrNode != null) {
                    final String attrValue = attrNode.getNodeValue();
                    if (isValidPath(attrValue)) {
                        addChildUrlFromTagAttribute(urlList, url, attrValue, encoding);
                    }
                }
            }
        } catch (final XPathException e) {
            logger.warn("Could not get urls: (" + xpath + ", " + attr + ")", e);
        }
        return urlList;
    }

    /**
     * Adds a child URL to the URL list after processing and validation.
     *
     * @param urlList the list to add the URL to
     * @param url the base URL for resolving relative URLs
     * @param attrValue the attribute value containing the URL
     * @param encoding the character encoding to use
     */
    protected void addChildUrlFromTagAttribute(final List<String> urlList, final URL url, final String attrValue, final String encoding) {
        try {
            final String childUrlValue = attrValue.trim();
            final URL childUrl =
                    childUrlValue.startsWith("?") ? new URL(url.toExternalForm() + childUrlValue) : new URL(url, childUrlValue);
            final String u = encodeUrl(normalizeUrl(childUrl.toExternalForm()), encoding);
            if (logger.isDebugEnabled()) {
                logger.debug("{} -> {}", attrValue, u);
            }
            if (StringUtil.isNotBlank(u)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Add Child: {}", u);
                }
                urlList.add(u);
            } else if (logger.isDebugEnabled()) {
                logger.debug("Skip Child: {}", u);
            }
        } catch (final MalformedURLException e) {
            logger.warn("Malformed URL: " + attrValue, e);
        }
    }

    /**
     * Encodes a URL using the specified character encoding.
     *
     * @param url the URL to encode
     * @param enc the character encoding to use
     * @return the encoded URL
     */
    protected String encodeUrl(final String url, final String enc) {
        if (StringUtil.isBlank(url) || StringUtil.isBlank(enc)) {
            return url;
        }

        final StringBuilder buf = new StringBuilder(url.length() + 100);
        for (final char c : url.toCharArray()) {
            if (CharUtil.isUrlChar(c)) {
                buf.append(c);
            } else {
                try {
                    buf.append(URLEncoder.encode(String.valueOf(c), enc));
                } catch (final UnsupportedEncodingException e) {
                    // NOP
                }
            }
        }
        return buf.toString();
    }

    /**
     * Normalizes a URL by removing fragments, resolving relative paths, and cleaning up.
     *
     * @param u the URL to normalize
     * @return the normalized URL
     */
    protected String normalizeUrl(final String u) {
        if (u == null) {
            return null;
        }

        // trim
        String url = u.trim();

        int idx = url.indexOf('#');
        if (idx >= 0) {
            url = url.substring(0, idx);
        }

        url = url.replace("/./", "/");

        idx = url.indexOf(";jsessionid");
        if (idx >= 0) {
            url = url.replaceFirst(";jsessionid=[a-zA-Z0-9\\.]*", "");
        }

        if (url.indexOf(' ') >= 0) {
            url = url.replace(" ", "%20");
        }

        String oldUrl = null;
        while (url.indexOf("/../") >= 0 && !url.equals(oldUrl)) {
            oldUrl = url;
            url = url.replaceFirst("/[^/]+/\\.\\./", "/");
        }

        return url.replaceAll("([^:])/+", "$1/");
    }

    /**
     * Checks if a path is valid for crawling (not a JavaScript, mailto, or other invalid URL).
     *
     * @param path the path to validate
     * @return true if the path is valid, false otherwise
     */
    protected boolean isValidPath(final String path) {
        if (StringUtil.isBlank(path)) {
            return false;
        }

        final Matcher matcher = invalidUrlPattern.matcher(path);
        if (matcher.find()) {
            return false;
        }

        return true;
    }

    /**
     * Adds a parser feature configuration.
     *
     * @param key the feature key
     * @param value the feature value
     * @throws CrawlerSystemException if key or value is null
     */
    public void addFeature(final String key, final String value) {
        if (StringUtil.isBlank(key) || StringUtil.isBlank(value)) {
            throw new CrawlerSystemException("key or value is null.");
        }

        featureMap.put(key, value);
    }

    /**
     * Adds a parser property configuration.
     *
     * @param key the property key
     * @param value the property value
     * @throws CrawlerSystemException if key or value is null
     */
    public void addProperty(final String key, final String value) {
        if (StringUtil.isBlank(key) || StringUtil.isBlank(value)) {
            throw new CrawlerSystemException("key or value is null.");
        }

        propertyMap.put(key, value);
    }

    /**
     * Returns data as HTML content of String.
     *
     */
    @Override
    public Object getData(final AccessResultData<?> accessResultData) {
        // check transformer name
        if (!getName().equals(accessResultData.getTransformerName())) {
            throw new CrawlerSystemException(
                    "Transformer is invalid. Use " + accessResultData.getTransformerName() + ". This transformer is " + getName() + ".");
        }

        final byte[] data = accessResultData.getData();
        if (data == null) {
            return null;
        }
        final String encoding = accessResultData.getEncoding();
        try {
            return new String(data, encoding == null ? Constants.UTF_8 : encoding);
        } catch (final UnsupportedEncodingException e) {
            if (logger.isInfoEnabled()) {
                logger.info("Invalid charsetName: " + encoding + ". Changed to " + Constants.UTF_8, e);
            }
            return new String(data, Constants.UTF_8_CHARSET);
        }
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
     * Gets the map of child URL extraction rules.
     *
     * @return the child URL rule map
     */
    public Map<String, String> getChildUrlRuleMap() {
        return childUrlRuleMap;
    }

    /**
     * Sets the map of child URL extraction rules.
     *
     * @param childUrlRuleMap the child URL rule map to set
     */
    public void setChildUrlRuleMap(final Map<String, String> childUrlRuleMap) {
        this.childUrlRuleMap = childUrlRuleMap;
    }

    /**
     * Gets the default character encoding.
     *
     * @return the default encoding
     */
    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    /**
     * Sets the default character encoding.
     *
     * @param defaultEncoding the default encoding to set
     */
    public void setDefaultEncoding(final String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    /**
     * Gets the preload size for charset detection.
     *
     * @return the preload size in bytes
     */
    public int getPreloadSizeForCharset() {
        return preloadSizeForCharset;
    }

    /**
     * Sets the preload size for charset detection.
     *
     * @param preloadSizeForCharset the preload size in bytes to set
     */
    public void setPreloadSizeForCharset(final int preloadSizeForCharset) {
        this.preloadSizeForCharset = preloadSizeForCharset;
    }

    /**
     * Gets the pattern for matching invalid URLs.
     *
     * @return the invalid URL pattern
     */
    public Pattern getInvalidUrlPattern() {
        return invalidUrlPattern;
    }

    /**
     * Sets the pattern for matching invalid URLs.
     *
     * @param invalidUrlPattern the invalid URL pattern to set
     */
    public void setInvalidUrlPattern(final Pattern invalidUrlPattern) {
        this.invalidUrlPattern = invalidUrlPattern;
    }
}
