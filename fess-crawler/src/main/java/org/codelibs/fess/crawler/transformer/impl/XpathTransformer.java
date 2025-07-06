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
 * {@link XpathTransformer} is a class that transforms HTML content into XML format based on XPath expressions.
 * It extracts data from an HTML document by applying XPath rules defined in {@link #fieldRuleMap}.
 * The extracted data is then formatted into an XML structure and stored in the {@link ResultData}.
 * <p>
 * This class extends {@link HtmlTransformer} and overrides the {@link #storeData(ResponseData, ResultData)} method
 * to parse the HTML content, evaluate XPath expressions, and generate the XML output.
 * </p>
 * <p>
 * The class supports various XPath result types, including BOOLEAN, NUMBER, STRING, NODESET, and NODE.
 * It also provides options to trim whitespace from extracted values and to specify the character encoding for the output.
 * </p>
 * <p>
 * The {@link #getData(AccessResultData)} method allows retrieving the transformed data as a String (XML content),
 * a Map, or an instance of a specified class.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>
 * XpathTransformer transformer = new XpathTransformer();
 * transformer.addFieldRule("title", "//title/text()");
 * transformer.addFieldRule("body", "//body/p/text()");
 *
 * ResponseData responseData = new ResponseData();
 * responseData.setBody(new ByteArrayInputStream("&lt;html&gt;&lt;head&gt;&lt;title&gt;Example&lt;/title&gt;&lt;/head&gt;&lt;body&gt;&lt;p&gt;Hello World&lt;/p&gt;&lt;/body&gt;&lt;/html&gt;".getBytes()));
 * responseData.setUrl("http://example.com");
 *
 * ResultData resultData = new ResultData();
 *
 * transformer.storeData(responseData, resultData);
 *
 * String xmlData = new String(resultData.getData(), resultData.getEncoding());
 * System.out.println(xmlData);
 * </pre>
 *
 * <p>
 * Configuration options:
 * </p>
 * <ul>
 *   <li><b>fieldRuleMap:</b> A map of field names to XPath expressions.</li>
 *   <li><b>trimSpaceEnabled:</b> A flag to enable or disable trimming of whitespace from extracted values.</li>
 *   <li><b>charsetName:</b> The character encoding for the output XML.</li>
 *   <li><b>dataClass:</b> The class type to return from the {@link #getData(AccessResultData)} method.</li>
 * </ul>
 *
 */
public class XpathTransformer extends HtmlTransformer {

    /**
     * Creates a new XpathTransformer instance.
     */
    public XpathTransformer() {
        super();
    }

    private static final Logger logger = LogManager.getLogger(XpathTransformer.class);

    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+", Pattern.MULTILINE);

    /**
     * A map of field rules, where the key is the field name and the value is the XPath expression.
     */
    protected Map<String, String> fieldRuleMap = new LinkedHashMap<>();

    /** Flag to enable or disable trimming of whitespace characters. */
    protected boolean trimSpaceEnabled = true;

    /**
     * The charset name for the output.
     */
    protected String charsetName = Constants.UTF_8;

    /**
     * Class type to be returned by the {@link #getData(AccessResultData)} method. Defaults to null, which returns XML content
     * as a String.
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
                    buf.append(getResultDataBody(entry.getKey(), trimSpaceEnabled ? str.trim() : str));
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
                logger.warn("Could not parse value for key: " + entry.getKey() + " with XPath: " + entry.getValue(), e);
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

    /**
     * Returns the result data header.
     * @return The result data header.
     */
    protected String getResultDataHeader() {
        // TODO: Support other XML header types
        return "<?xml version=\"1.0\"?>\n<doc>\n";
    }

    /**
     * Returns the result data body for a single value.
     * @param name The name of the field.
     * @param value The value of the field.
     * @return The result data body.
     */
    protected String getResultDataBody(final String name, final String value) {
        // TODO: Support other XML footer types
        // TODO: Support other field types and trimming options
        return "<field name=\"" + XmlUtil.escapeXml(name) + "\">" + trimSpace(XmlUtil.escapeXml(value != null ? value : "")) + "</field>\n";
    }

    /**
     * Returns the result data body for multiple values.
     * @param name The name of the field.
     * @param values The list of values for the field.
     * @return The result data body.
     */
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

    /**
     * Returns the additional data for the response.
     * @param responseData The response data.
     * @param document The document.
     * @return The additional data.
     */
    protected String getAdditionalData(final ResponseData responseData, final Document document) {
        return "";
    }

    /**
     * Returns the result data footer.
     * @return The result data footer.
     */
    protected String getResultDataFooter() {
        // TODO support other type
        return "</doc>";
    }

    /**
     * Trims space characters from the value.
     * @param value The value to trim.
     * @return The trimmed value.
     */
    protected String trimSpace(final String value) {
        if (trimSpaceEnabled) {
            final Matcher matcher = SPACE_PATTERN.matcher(value);
            return matcher.replaceAll(" ").trim();
        }
        return value;
    }

    /**
     * Adds a field rule to the transformer.
     * @param name The name of the field.
     * @param xpath The XPath expression for the field.
     */
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

    /**
     * Returns the field rule map.
     * @return The field rule map.
     */
    public Map<String, String> getFieldRuleMap() {
        return fieldRuleMap;
    }

    /**
     * Sets the field rule map.
     * @param fieldRuleMap The field rule map to set.
     */
    public void setFieldRuleMap(final Map<String, String> fieldRuleMap) {
        this.fieldRuleMap = fieldRuleMap;
    }

    /**
     * Returns whether space trimming is enabled.
     * @return True if space trimming is enabled, false otherwise.
     */
    public boolean isTrimSpace() {
        return trimSpaceEnabled;
    }

    /**
     * Sets whether space trimming is enabled.
     * @param trimSpace The trim space flag to set.
     */
    public void setTrimSpace(final boolean trimSpace) {
        trimSpaceEnabled = trimSpace;
    }

    /**
     * Returns the charset name.
     * @return The charset name.
     */
    public String getCharsetName() {
        return charsetName;
    }

    /**
     * Sets the charset name.
     * @param charsetName The charset name to set.
     */
    public void setCharsetName(final String charsetName) {
        this.charsetName = charsetName;
    }

    /**
     * Returns the data class.
     * @return The data class.
     */
    public Class<?> getDataClass() {
        return dataClass;
    }

    /**
     * Sets the data class.
     * @param dataClass The data class to set.
     */
    public void setDataClass(final Class<?> dataClass) {
        this.dataClass = dataClass;
    }

}
