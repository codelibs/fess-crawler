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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.CachedXPathAPI;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.util.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author shinsuke
 *
 */
public class XmlTransformer extends AbstractTransformer {
    private static final Logger logger = LoggerFactory
            .getLogger(XmlTransformer.class);

    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+",
            Pattern.MULTILINE);

    protected boolean namespaceAware;

    protected boolean coalescing;

    protected boolean expandEntityRef = true;

    protected boolean ignoringComments;

    protected boolean ignoringElementContentWhitespace;

    protected boolean validating;

    protected boolean includeAware;

    protected final Map<String, Object> attributeMap = new HashMap<>();

    protected final Map<String, String> featureMap = new HashMap<>();

    protected Map<String, String> fieldRuleMap = new LinkedHashMap<>();

    /** a flag to trim a space characters. */
    protected boolean trimSpace = true;

    protected String charsetName = Constants.UTF_8;

    /**
     * Class type returned by getData() method. The default is null(XML content
     * of String).
     */
    protected Class<?> dataClass = null;

    private final ThreadLocal<CachedXPathAPI> xpathAPI = new ThreadLocal<>();

    /**
     * Returns data as XML content of String.
     *
     * @return XML content of String.
     */
    @Override
    public Object getData(final AccessResultData<?> accessResultData) {
        if (dataClass == null) {
            // check transformer name
            if (!getName().equals(accessResultData.getTransformerName())) {
                throw new CrawlerSystemException("Transformer is invalid. Use "
                        + accessResultData.getTransformerName()
                        + ". This transformer is " + getName() + ".");
            }

            final byte[] data = accessResultData.getData();
            if (data == null) {
                return null;
            }
            final String encoding = accessResultData.getEncoding();
            try {
                return new String(data, encoding == null ? Constants.UTF_8
                        : encoding);
            } catch (final UnsupportedEncodingException e) {
                if (logger.isInfoEnabled()) {
                    logger.info("Invalid charsetName: " + encoding
                            + ". Changed to " + Constants.UTF_8, e);
                }
                return new String(data, Constants.UTF_8_CHARSET);
            }
        }

        final Map<String, Object> dataMap = XmlUtil
                .getDataMap(accessResultData);
        if (Map.class.equals(dataClass)) {
            return dataMap;
        }

        try {
            final Object obj = dataClass.newInstance();
            BeanUtil.copyMapToBean(dataMap, obj);
            return obj;
        } catch (final Exception e) {
            throw new CrawlerSystemException(
                    "Could not create/copy a data map to " + dataClass, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.transformer.impl.AbstractTransformer#transform(org.codelibs.fess.crawler.entity.ResponseData)
     */
    @Override
    public ResultData transform(final ResponseData responseData) {
        if (responseData == null || !responseData.hasResponseBody()) {
            throw new CrawlingAccessException("No response body.");
        }

        try (final InputStream is = responseData.getResponseBody()) {
            final DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();

            for (final Map.Entry<String, Object> entry : attributeMap
                    .entrySet()) {
                factory.setAttribute(entry.getKey(), entry.getValue());
            }

            for (final Map.Entry<String, String> entry : featureMap.entrySet()) {
                factory.setFeature(entry.getKey(),
                        "true".equalsIgnoreCase(entry.getValue()));
            }

            factory.setCoalescing(coalescing);
            factory.setExpandEntityReferences(expandEntityRef);
            factory.setIgnoringComments(ignoringComments);
            factory.setIgnoringElementContentWhitespace(ignoringElementContentWhitespace);
            factory.setNamespaceAware(namespaceAware);
            factory.setValidating(validating);
            factory.setXIncludeAware(includeAware);

            final DocumentBuilder builder = factory.newDocumentBuilder();

            final Document doc = builder.parse(is);

            final StringBuilder buf = new StringBuilder(1000);
            buf.append(getResultDataHeader());
            for (final Map.Entry<String, String> entry : fieldRuleMap
                    .entrySet()) {
                final List<String> nodeStrList = new ArrayList<>();
                try {
                    final NodeList nodeList = getNodeList(doc, entry.getValue());
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        final Node node = nodeList.item(i);
                        nodeStrList.add(node.getTextContent());
                    }
                } catch (final TransformerException e) {
                    logger.warn("Could not parse a value of " + entry.getKey()
                            + ":" + entry.getValue(), e);
                }
                if (nodeStrList.size() == 1) {
                    buf.append(getResultDataBody(entry.getKey(),
                            nodeStrList.get(0)));
                } else if (nodeStrList.size() > 1) {
                    buf.append(getResultDataBody(entry.getKey(), nodeStrList));
                }
            }
            buf.append(getAdditionalData(responseData, doc));
            buf.append(getResultDataFooter());

            final ResultData resultData = new ResultData();
            resultData.setTransformerName(getName());

            final String data = buf.toString().trim();
            try {
                resultData.setData(data.getBytes(charsetName));
            } catch (final UnsupportedEncodingException e) {
                if (logger.isInfoEnabled()) {
                    logger.info("Invalid charsetName: " + charsetName
                            + ". Changed to " + Constants.UTF_8, e);
                }
                charsetName = Constants.UTF_8_CHARSET.name();
                resultData.setData(data.getBytes(
                        Constants.UTF_8_CHARSET));
            }
            resultData.setEncoding(charsetName);

            return resultData;
        } catch (final CrawlerSystemException e) {
            throw e;
        } catch (final Exception e) {
            throw new CrawlerSystemException("Could not store data.", e);
        }
    }

    protected NodeList getNodeList(final Document doc, final String xpath)
            throws TransformerException {
        final DefaultPrefixResolver prefixResolver = new DefaultPrefixResolver(
                doc.getNodeType() == Node.DOCUMENT_NODE ? doc
                        .getDocumentElement() : doc);
        return getXPathAPI().eval(doc, xpath, prefixResolver).nodelist();
    }

    protected CachedXPathAPI getXPathAPI() {
        CachedXPathAPI cachedXPathAPI = xpathAPI.get();
        if (cachedXPathAPI == null) {
            cachedXPathAPI = new CachedXPathAPI();
            xpathAPI.set(cachedXPathAPI);
        }
        return cachedXPathAPI;
    }

    protected String getResultDataHeader() {
        // TODO support other type
        return "<?xml version=\"1.0\"?>\n<doc>\n";
    }

    protected String getResultDataBody(final String name, final String value) {
        // TODO support other type
        // TODO trim(default)
        return "<field name=\"" + XmlUtil.escapeXml(name) + "\">"
                + trimSpace(XmlUtil.escapeXml(value != null ? value : ""))
                + "</field>\n";
    }

    protected String getResultDataBody(final String name,
            final List<String> values) {
        final StringBuilder buf = new StringBuilder();
        buf.append("<list>");
        if (values != null && !values.isEmpty()) {
            for (final String value : values) {
                buf.append("<item>");
                buf.append(trimSpace(XmlUtil.escapeXml(value)));
                buf.append("</item>");
            }
        }
        buf.append("</list>");
        // TODO support other type
        // TODO trim(default)
        return "<field name=\"" + XmlUtil.escapeXml(name) + "\">"
                + buf.toString().trim() + "</field>\n";
    }

    protected String getAdditionalData(final ResponseData responseData,
            final Document document) {
        return "";
    }

    protected String getResultDataFooter() {
        // TODO support other type
        return "</doc>";
    }

    protected String trimSpace(final String value) {
        if (trimSpace) {
            final Matcher matcher = SPACE_PATTERN.matcher(value);
            return matcher.replaceAll(" ").trim();
        }
        return value;
    }

    public void addAttribute(final String name, final Object value) {
        attributeMap.put(name, value);
    }

    public void addFeature(final String key, final String value) {
        featureMap.put(key, value);
    }

    public void addFieldRule(final String name, final String xpath) {
        fieldRuleMap.put(name, xpath);
    }

    /**
     * @return the fieldRuleMap
     */
    public Map<String, String> getFieldRuleMap() {
        return fieldRuleMap;
    }

    /**
     * @param fieldRuleMap the fieldRuleMap to set
     */
    public void setFieldRuleMap(final Map<String, String> fieldRuleMap) {
        this.fieldRuleMap = fieldRuleMap;
    }

    /**
     * @return the trimSpace
     */
    public boolean isTrimSpace() {
        return trimSpace;
    }

    /**
     * @param trimSpace the trimSpace to set
     */
    public void setTrimSpace(final boolean trimSpace) {
        this.trimSpace = trimSpace;
    }

    /**
     * @return the charsetName
     */
    public String getCharsetName() {
        return charsetName;
    }

    /**
     * @param charsetName the charsetName to set
     */
    public void setCharsetName(final String charsetName) {
        this.charsetName = charsetName;
    }

    /**
     * @return the dataClass
     */
    public Class<?> getDataClass() {
        return dataClass;
    }

    /**
     * @param dataClass the dataClass to set
     */
    public void setDataClass(final Class<?> dataClass) {
        this.dataClass = dataClass;
    }

    /**
     * @return the namespaceAware
     */
    public boolean isNamespaceAware() {
        return namespaceAware;
    }

    /**
     * @param namespaceAware the namespaceAware to set
     */
    public void setNamespaceAware(final boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }

    /**
     * @return the coalescing
     */
    public boolean isCoalescing() {
        return coalescing;
    }

    /**
     * @param coalescing the coalescing to set
     */
    public void setCoalescing(final boolean coalescing) {
        this.coalescing = coalescing;
    }

    /**
     * @return the expandEntityRef
     */
    public boolean isExpandEntityRef() {
        return expandEntityRef;
    }

    /**
     * @param expandEntityRef the expandEntityRef to set
     */
    public void setExpandEntityRef(final boolean expandEntityRef) {
        this.expandEntityRef = expandEntityRef;
    }

    /**
     * @return the ignoringComments
     */
    public boolean isIgnoringComments() {
        return ignoringComments;
    }

    /**
     * @param ignoringComments the ignoringComments to set
     */
    public void setIgnoringComments(final boolean ignoringComments) {
        this.ignoringComments = ignoringComments;
    }

    /**
     * @return the ignoringElementContentWhitespace
     */
    public boolean isIgnoringElementContentWhitespace() {
        return ignoringElementContentWhitespace;
    }

    /**
     * @param ignoringElementContentWhitespace the ignoringElementContentWhitespace to set
     */
    public void setIgnoringElementContentWhitespace(
            final boolean ignoringElementContentWhitespace) {
        this.ignoringElementContentWhitespace = ignoringElementContentWhitespace;
    }

    /**
     * @return the validating
     */
    public boolean isValidating() {
        return validating;
    }

    /**
     * @param validating the validating to set
     */
    public void setValidating(final boolean validating) {
        this.validating = validating;
    }

    /**
     * @return the includeAware
     */
    public boolean isIncludeAware() {
        return includeAware;
    }

    /**
     * @param includeAware the includeAware to set
     */
    public void setIncludeAware(final boolean includeAware) {
        this.includeAware = includeAware;
    }

    public static class DefaultPrefixResolver extends PrefixResolverDefault {

        public DefaultPrefixResolver(final Node xpathExpressionContext) {
            super(xpathExpressionContext);
        }

        /*
         * (non-Javadoc)
         *
         * @see org.apache.xml.utils.PrefixResolverDefault#getNamespaceForPrefix(java.lang.String, org.w3c.dom.Node)
         */
        @Override
        public String getNamespaceForPrefix(final String prefix,
                final Node namespaceContext) {
            final String namespace = super.getNamespaceForPrefix(prefix,
                    namespaceContext);
            if (StringUtil.isNotBlank(namespace)) {
                return namespace;
            }
            return "http://crawler.codelibs.org/namespace/" + prefix;
        }

    }
}
