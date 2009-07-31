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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.apache.xpath.CachedXPathAPI;
import org.cyberneko.html.parsers.DOMParser;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.AccessResultData;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;
import org.seasar.robot.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * HtmlTransformer stores WEB data as HTML content.
 * 
 * @author shinsuke
 * 
 */
public class HtmlTransformer extends AbstractTransformer {
    private static final Logger logger = LoggerFactory
            .getLogger(HtmlTransformer.class);

    protected Map<String, String> featureMap = new HashMap<String, String>();

    protected Map<String, String> propertyMap = new HashMap<String, String>();

    protected Map<String, String> childUrlRuleMap = new LinkedHashMap<String, String>();

    @Binding(bindingType = BindingType.MAY)
    protected String defaultEncoding;

    // If you want to follow a html spec, use 512.
    @Binding(bindingType = BindingType.MAY)
    protected int preloadSizeForCharset = 1024;

    @Binding(bindingType = BindingType.MAY)
    protected Pattern invalidUrlPattern = Pattern.compile(
            "\\s*javascript:|\\s*mailto:|\\s*irc:", Pattern.CASE_INSENSITIVE);

    private ThreadLocal<CachedXPathAPI> xpathAPI = new ThreadLocal<CachedXPathAPI>();

    public ResultData transform(ResponseData responseData) {
        if (responseData == null || responseData.getResponseBody() == null) {
            throw new RobotSystemException("No response body.");
        }

        InputStream is = responseData.getResponseBody();
        File tempFile = null;
        FileOutputStream fos = null;
        try {
            tempFile = File.createTempFile("s2robot-HtmlTransformer-", ".html");
            tempFile.deleteOnExit();
            fos = new FileOutputStream(tempFile);
            StreamUtil.drain(is, fos);
        } catch (Exception e) {
            // clean up
            if (tempFile != null && !tempFile.delete()) {
                logger.warn("Could not delete a temp file: " + tempFile);
            }
            throw new RobotSystemException("Could not read a response body.", e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(fos);
        }

        FileInputStream fis = null;

        // encoding
        try {
            fis = new FileInputStream(tempFile);
            responseData.setResponseBody(fis);
            updateCharset(responseData);
        } catch (RobotSystemException e) {
            // clean up
            if (!tempFile.delete()) {
                logger.warn("Could not delete a temp file: " + tempFile);
            }
            throw e;
        } catch (Exception e) {
            // clean up
            if (!tempFile.delete()) {
                logger.warn("Could not delete a temp file: " + tempFile);
            }
            throw new RobotSystemException(
                    "Could not load a charset in a response.", e);
        } finally {
            IOUtils.closeQuietly(fis);
        }

        ResultData resultData = new ResultData();
        resultData.setTransformerName(getName());

        // data
        try {
            fis = new FileInputStream(tempFile);
            responseData.setResponseBody(fis);
            storeData(responseData, resultData);
        } catch (RobotSystemException e) {
            // clean up
            if (!tempFile.delete()) {
                logger.warn("Could not delete a temp file: " + tempFile);
            }
            throw e;
        } catch (Exception e) {
            // clean up
            if (!tempFile.delete()) {
                logger.warn("Could not delete a temp file: " + tempFile);
            }
            throw new RobotSystemException("Could not store data.", e);
        } finally {
            IOUtils.closeQuietly(fis);
        }

        if (isHtml(responseData)) {
            // urls
            try {
                fis = new FileInputStream(tempFile);
                responseData.setResponseBody(fis);
                storeChildUrls(responseData, resultData);
            } catch (RobotSystemException e) {
                // clean up
                if (!tempFile.delete()) {
                    logger.warn("Could not delete a temp file: " + tempFile);
                }
                throw e;
            } catch (Exception e) {
                // clean up
                if (!tempFile.delete()) {
                    logger.warn("Could not delete a temp file: " + tempFile);
                }
                throw new RobotSystemException("Could not store data.", e);
            } finally {
                IOUtils.closeQuietly(fis);
            }
        }

        // clean up
        if (!tempFile.delete()) {
            logger.warn("Could not delete a temp file: " + tempFile);
        }

        return resultData;
    }

    protected boolean isHtml(ResponseData responseData) {
        String mimeType = responseData.getMimeType();
        if ("text/html".equals(mimeType)
                || "application/xhtml+xml".equals(mimeType)) {
            return true;
        }
        return false;
    }

    public void addChildUrlRule(String tagName, String attrName) {
        if (StringUtil.isNotBlank(tagName) && StringUtil.isNotBlank(attrName)) {
            childUrlRuleMap.put(tagName, attrName);
        }
    }

    protected CachedXPathAPI getXPathAPI() {
        CachedXPathAPI cachedXPathAPI = xpathAPI.get();
        if (cachedXPathAPI == null) {
            cachedXPathAPI = new CachedXPathAPI();
            xpathAPI.set(cachedXPathAPI);
        }
        return cachedXPathAPI;
    }

    protected void storeChildUrls(ResponseData responseData,
            ResultData resultData) {
        List<String> urlList = new ArrayList<String>();
        try {
            DOMParser parser = getDomParser();
            parser.parse(new InputSource(responseData.getResponseBody()));
            Document document = parser.getDocument();
            // base href
            String baseHref = getBaseHref(document);
            URL url = new URL(baseHref != null ? baseHref : responseData
                    .getUrl());
            for (Map.Entry<String, String> entry : childUrlRuleMap.entrySet()) {
                urlList.addAll(getUrlFromTagAttribute(url, document, entry
                        .getKey(), entry.getValue()));
            }
        } catch (Exception e) {
            logger.warn("Could not create child urls.", e);
        } finally {
            xpathAPI.remove();
        }
        resultData.addAllUrl(urlList);

        String u = responseData.getUrl();
        resultData.removeUrl(u);
        resultData.removeUrl(getDuplicateUrl(u));
    }

    protected void storeData(ResponseData responseData, ResultData resultData) {
        byte[] data = InputStreamUtil.getBytes(responseData.getResponseBody());
        resultData.setData(data);
        resultData.setEncoding(responseData.getCharSet());
    }

    protected void updateCharset(ResponseData responseData) {
        String encoding = loadCharset(responseData.getResponseBody());
        if (encoding == null) {
            if (defaultEncoding != null) {
                responseData.setCharSet(defaultEncoding);
            } else if (responseData.getCharSet() == null) {
                responseData.setCharSet(Constants.UTF_8);
            }
        } else {
            responseData.setCharSet(encoding);
        }
    }

    protected String loadCharset(InputStream inputStream) {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[preloadSizeForCharset];
            int size = bis.read(buffer);
            if (size != -1) {
                String content = new String(buffer, 0, size);
                String encoding = parseCharset(content);
                if (encoding != null) {
                    return encoding;
                }
            }
            return null;
        } catch (IOException e) {
            throw new RobotSystemException("Could not load a content.", e);
        }
    }

    protected String parseCharset(String content) {
        Pattern pattern = Pattern.compile("; *charset *= *([a-zA-Z0-9\\-_]+)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    protected String getDuplicateUrl(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        } else {
            return url + "/";
        }

    }

    protected DOMParser getDomParser() {
        DOMParser parser = new DOMParser();
        try {
            // feature
            for (Map.Entry<String, String> entry : featureMap.entrySet()) {
                parser.setFeature(entry.getKey(), "true".equalsIgnoreCase(entry
                        .getValue()) ? true : false);
            }

            // property
            for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
                parser.setProperty(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            throw new RobotSystemException("Invalid parser configuration.", e);
        }

        return parser;
    }

    protected String getBaseHref(Document document) {
        NodeList list;
        try {
            list = getXPathAPI().selectNodeList(document, "//BASE");
        } catch (Exception e) {
            logger.warn("Could not get a base tag. ", e);
            return null;
        }
        if (list.getLength() > 0) {
            Element element = (Element) list.item(0);
            String attrValue = element.getAttribute("href");
            if (StringUtil.isNotBlank(attrValue)) {
                return attrValue;
            }
        }
        return null;
    }

    protected List<String> getUrlFromTagAttribute(URL url, Document document,
            String xpath, String attr) {
        if (logger.isDebugEnabled()) {
            logger.debug("Base URL: " + url);
        }
        List<String> urlList = new ArrayList<String>();
        try {
            NodeList list = getXPathAPI().selectNodeList(document, xpath);
            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                String attrValue = element.getAttribute(attr);
                if (isValidPath(attrValue)) {
                    try {
                        URL childUrl = new URL(url, attrValue.trim());
                        String u = normalizeUrl(childUrl.toString());
                        if (logger.isDebugEnabled()) {
                            logger.debug(attrValue + " -> " + u);
                        }
                        if (StringUtil.isNotBlank(u)) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("ADD: " + u);
                            }
                            urlList.add(u);
                        }
                    } catch (MalformedURLException e) {
                        logger.warn("Malformed URL: " + attrValue, e);
                    }
                }
            }
        } catch (TransformerException e) {
            logger.warn("Could not get urls: (" + xpath + ", " + attr + ")", e);
        }
        return urlList;
    }

    protected String normalizeUrl(String url) {
        if (url == null) {
            return null;
        }

        // trim
        url = url.trim();

        int idx = url.indexOf("#");
        if (idx >= 0) {
            url = url.substring(0, idx);
        }

        idx = url.indexOf(";jsessionid");
        if (idx >= 0) {
            url = url.substring(0, idx);
        }

        if (url.indexOf("/../") >= 0 || url.indexOf(" ") >= 0) {
            // invalid URL
            if (logger.isDebugEnabled()) {
                logger.debug("INVALID URL: " + url);
            }
            return null;
        }

        return url;
    }

    protected boolean isValidPath(String path) {
        if (StringUtil.isBlank(path)) {
            return false;
        }

        Matcher matcher = invalidUrlPattern.matcher(path);
        if (matcher.find()) {
            return false;
        }

        return true;
    }

    public void addFeature(String key, String value) {
        if (StringUtil.isBlank(key) || StringUtil.isBlank(value)) {
            throw new RobotSystemException("key or value is null.");
        }

        featureMap.put(key, value);
    }

    public void addProperty(String key, String value) {
        if (StringUtil.isBlank(key) || StringUtil.isBlank(value)) {
            throw new RobotSystemException("key or value is null.");
        }

        propertyMap.put(key, value);
    }

    /** 
     * Returns data as HTML content of String.
     * 
     */
    public Object getData(AccessResultData accessResultData) {
        // check transformer name
        if (!getName().equals(accessResultData.getTransformerName())) {
            throw new RobotSystemException("Transformer is invalid. Use "
                    + accessResultData.getTransformerName()
                    + ". This transformer is " + getName() + ".");
        }

        byte[] data = accessResultData.getData();
        if (data == null) {
            return null;
        }
        String encoding = accessResultData.getEncoding();
        try {
            return new String(data, encoding != null ? encoding
                    : Constants.UTF_8);
        } catch (UnsupportedEncodingException e) {
            try {
                return new String(data, Constants.UTF_8);
            } catch (UnsupportedEncodingException e1) {
                throw new RobotSystemException("Unexpected exception");
            }
        }
    }

    public Map<String, String> getFeatureMap() {
        return featureMap;
    }

    public void setFeatureMap(Map<String, String> featureMap) {
        this.featureMap = featureMap;
    }

    public Map<String, String> getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(Map<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }

    public Map<String, String> getChildUrlRuleMap() {
        return childUrlRuleMap;
    }

    public void setChildUrlRuleMap(Map<String, String> childUrlRuleMap) {
        this.childUrlRuleMap = childUrlRuleMap;
    }

    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    public int getPreloadSizeForCharset() {
        return preloadSizeForCharset;
    }

    public void setPreloadSizeForCharset(int preloadSizeForCharset) {
        this.preloadSizeForCharset = preloadSizeForCharset;
    }

    public Pattern getInvalidUrlPattern() {
        return invalidUrlPattern;
    }

    public void setInvalidUrlPattern(Pattern invalidUrlPattern) {
        this.invalidUrlPattern = invalidUrlPattern;
    }
}
