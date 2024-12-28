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
package org.codelibs.fess.crawler.transformer.impl;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathEvaluationResult;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathNodes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.util.XmlUtil;
import org.codelibs.nekohtml.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * XpathTransformer stores WEB data as XML content.
 *
 * @author shinsuke
 *
 */
public class XpathTransformer extends HtmlTransformer {
    private static final Logger logger = LogManager.getLogger(XpathTransformer.class);

    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+", Pattern.MULTILINE);

    protected Map<String, String> fieldRuleMap = new LinkedHashMap<>();

    /** a flag to trim a space characters. */
    protected boolean trimSpaceEnabled = true;

    protected String charsetName = Constants.UTF_8;

    /**
     * Class type returned by getData() method. The default is null(XML content
     * of String).
     */
    protected Class<?> dataClass = null;

    @Override
    protected void storeData(final ResponseData responseData, final ResultData resultData) {
        final DOMParser parser = getDomParser();
        try (final InputStream in = responseData.getResponseBody()) {
            final InputSource is = new InputSource(in);
            if (responseData.getCharSet() != null) {
                is.setEncoding(responseData.getCharSet());
            }
            parser.parse(is);
        } catch (final Exception e) {
            throw new CrawlingAccessException("Could not parse " + responseData.getUrl(), e);
        }
        final Document document = parser.getDocument();

        final StringBuilder buf = new StringBuilder(1000);
        buf.append(getResultDataHeader());
        for (final Map.Entry<String, String> entry : fieldRuleMap.entrySet()) {
            final String path = entry.getValue();
            try {
                final XPathEvaluationResult<?> xObj = getXPathAPI().eval(document, path);
                switch (xObj.type()) {
                case BOOLEAN:
                    final Boolean b = (Boolean) xObj.value();
                    buf.append(getResultDataBody(entry.getKey(), b.toString()));
                    break;
                case NUMBER:
                    final Number d = (Number) xObj.value();
                    buf.append(getResultDataBody(entry.getKey(), d.toString()));
                    break;
                case STRING:
                    final String str = (String) xObj.value();
                    buf.append(getResultDataBody(entry.getKey(), str.trim()));
                    break;
                case NODESET:
                    final XPathNodes nodeList = (XPathNodes) xObj.value();
                    final List<String> strList = new ArrayList<>();
                    for (int i = 0; i < nodeList.size(); i++) {
                        final Node node = nodeList.get(i);
                        strList.add(node.getTextContent());
                    }
                    buf.append(getResultDataBody(entry.getKey(), strList));
                    break;
                case NODE:
                    final Node node = (Node) xObj.value();
                    buf.append(getResultDataBody(entry.getKey(), node.getTextContent()));
                    break;
                default:
                    Object obj = xObj.value();
                    if (obj == null) {
                        obj = "";
                    }
                    buf.append(getResultDataBody(entry.getKey(), obj.toString()));
                    break;
                }
            } catch (final XPathException e) {
                logger.warn("Could not parse a value of " + entry.getKey() + ":" + entry.getValue(), e);
            }
        }
        buf.append(getAdditionalData(responseData, document));
        buf.append(getResultDataFooter());

        final String data = buf.toString().trim();
        try {
            resultData.setData(data.getBytes(charsetName));
        } catch (final UnsupportedEncodingException e) {
            if (logger.isInfoEnabled()) {
                logger.info("Invalid charsetName: " + charsetName + ". Changed to " + Constants.UTF_8, e);
            }
            charsetName = Constants.UTF_8_CHARSET.name();
            resultData.setData(data.getBytes(Constants.UTF_8_CHARSET));
        }
        resultData.setEncoding(charsetName);
    }

    protected String getResultDataHeader() {
        // TODO support other type
        return "<?xml version=\"1.0\"?>\n<doc>\n";
    }

    protected String getResultDataBody(final String name, final String value) {
        // TODO support other type
        // TODO trim(default)
        return "<field name=\"" + XmlUtil.escapeXml(name) + "\">" + trimSpace(XmlUtil.escapeXml(value != null ? value : "")) + "</field>\n";
    }

    protected String getResultDataBody(final String name, final List<String> values) {
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
        return "<field name=\"" + XmlUtil.escapeXml(name) + "\">" + buf.toString().trim() + "</field>\n";
    }

    protected String getAdditionalData(final ResponseData responseData, final Document document) {
        return "";
    }

    protected String getResultDataFooter() {
        // TODO support other type
        return "</doc>";
    }

    protected String trimSpace(final String value) {
        if (trimSpaceEnabled) {
            final Matcher matcher = SPACE_PATTERN.matcher(value);
            return matcher.replaceAll(" ").trim();
        }
        return value;
    }

    public void addFieldRule(final String name, final String xpath) {
        fieldRuleMap.put(name, xpath);
    }

    /**
     * Returns data as XML content of String.
     *
     * @return XML content of String.
     */
    @Override
    public Object getData(final AccessResultData<?> accessResultData) {
        if (dataClass == null) {
            return super.getData(accessResultData);
        }

        final Map<String, Object> dataMap = XmlUtil.getDataMap(accessResultData);
        if (Map.class.equals(dataClass)) {
            return dataMap;
        }

        try {
            final Object obj = dataClass.getDeclaredConstructor().newInstance();
            BeanUtil.copyMapToBean(dataMap, obj);
            return obj;
        } catch (final Exception e) {
            throw new CrawlerSystemException("Could not create/copy a data map to " + dataClass, e);
        }
    }

    public Map<String, String> getFieldRuleMap() {
        return fieldRuleMap;
    }

    public void setFieldRuleMap(final Map<String, String> fieldRuleMap) {
        this.fieldRuleMap = fieldRuleMap;
    }

    public boolean isTrimSpace() {
        return trimSpaceEnabled;
    }

    public void setTrimSpace(final boolean trimSpace) {
        trimSpaceEnabled = trimSpace;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(final String charsetName) {
        this.charsetName = charsetName;
    }

    public Class<?> getDataClass() {
        return dataClass;
    }

    public void setDataClass(final Class<?> dataClass) {
        this.dataClass = dataClass;
    }

}
