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
package org.seasar.robot.extractor.impl;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.xpath.CachedXPathAPI;
import org.cyberneko.html.parsers.DOMParser;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ExtractData;
import org.seasar.robot.extractor.ExtractException;
import org.seasar.robot.extractor.Extractor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author shinsuke
 *
 */
public class HtmlXpathExtractor extends AbstractXmlExtractor implements
        Extractor {
    protected Pattern metaCharsetPattern = Pattern
            .compile(
                    "<meta.*content\\s*=\\s*['\"].*;\\s*charset=([\\w\\d\\-_]*)['\"]\\s*/?>",
                    Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    protected Map<String, String> featureMap = new HashMap<String, String>();

    protected Map<String, String> propertyMap = new HashMap<String, String>();

    protected String targetNodePath = "//HTML/BODY | //@alt | //@title";

    private ThreadLocal<CachedXPathAPI> xpathAPI = new ThreadLocal<CachedXPathAPI>();

    /* (non-Javadoc)
     * @see org.seasar.robot.extractor.Extractor#getText(java.io.InputStream, java.util.Map)
     */
    public ExtractData getText(InputStream in, Map<String, String> params) {
        if (in == null) {
            throw new RobotSystemException("The inputstream is null.");
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(in);
            String enc = getEncoding(bis);

            DOMParser parser = getDomParser();
            InputSource inputSource = new InputSource(bis);
            inputSource.setEncoding(enc);
            parser.parse(inputSource);
            Document document = parser.getDocument();

            StringBuilder buf = new StringBuilder(255);
            NodeList nodeList = getXPathAPI().selectNodeList(document,
                    targetNodePath);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                buf.append(node.getTextContent()).append(' ');
            }
            return new ExtractData(buf.toString().replaceAll("\\s+", " ")
                    .trim());
        } catch (Exception e) {
            throw new ExtractException(e);
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

    /* (non-Javadoc)
     * @see org.seasar.robot.extractor.impl.AbstractXmlExtractor#getEncodingPattern()
     */
    @Override
    protected Pattern getEncodingPattern() {
        return metaCharsetPattern;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.extractor.impl.AbstractXmlExtractor#getTagPattern()
     */
    @Override
    protected Pattern getTagPattern() {
        // not used
        return null;
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

    /**
     * @return Returns the metaCharsetPattern.
     */
    public Pattern getMetaCharsetPattern() {
        return metaCharsetPattern;
    }

    /**
     * @param metaCharsetPattern The metaCharsetPattern to set.
     */
    public void setMetaCharsetPattern(Pattern metaCharsetPattern) {
        this.metaCharsetPattern = metaCharsetPattern;
    }

    /**
     * @return Returns the targetNodePath.
     */
    public String getTargetNodePath() {
        return targetNodePath;
    }

    /**
     * @param targetNodePath The targetNodePath to set.
     */
    public void setTargetNodePath(String targetNodePath) {
        this.targetNodePath = targetNodePath;
    }
}
