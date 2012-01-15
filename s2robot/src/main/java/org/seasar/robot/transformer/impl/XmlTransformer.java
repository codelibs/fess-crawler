/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.apache.xpath.CachedXPathAPI;
import org.seasar.framework.beans.util.Beans;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotCrawlAccessException;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.AccessResultData;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;
import org.seasar.robot.util.StreamUtil;
import org.seasar.robot.util.XmlUtil;
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
    private static final Logger logger = LoggerFactory // NOPMD
        .getLogger(XmlTransformer.class);

    private static final Pattern SPACE_PATTERN = Pattern.compile(
        "\\s+",
        Pattern.MULTILINE);

    protected Map<String, String> fieldRuleMap =
        new LinkedHashMap<String, String>();

    /** a flag to trim a space characters. */
    protected boolean trimSpace = true;

    protected String charsetName = Constants.UTF_8;

    /**
     * Class type returned by getData() method. The default is null(XML content
     * of String).
     */
    protected Class<?> dataClass = null;

    private ThreadLocal<CachedXPathAPI> xpathAPI =
        new ThreadLocal<CachedXPathAPI>();

    /**
     * Returns data as XML content of String.
     * 
     * @return XML content of String.
     */
    public Object getData(final AccessResultData accessResultData) {
        if (dataClass == null) {
            // check transformer name
            if (!getName().equals(accessResultData.getTransformerName())) {
                throw new RobotSystemException("Transformer is invalid. Use "
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
            } catch (UnsupportedEncodingException e) {
                if (logger.isInfoEnabled()) {
                    logger.info("Invalid charsetName: " + encoding
                        + ". Changed to " + Constants.UTF_8, e);
                }
                try {
                    return new String(data, Constants.UTF_8);
                } catch (UnsupportedEncodingException e1) {
                    throw new RobotSystemException("Unexpected exception", e1);
                }
            }
        }

        final Map<String, Object> dataMap =
            XmlUtil.getDataMap(accessResultData);
        if (Map.class.equals(dataClass)) {
            return dataMap;
        }

        try {
            final Object obj = dataClass.newInstance();
            Beans.copy(dataMap, obj).execute();
            return obj;
        } catch (Exception e) {
            throw new RobotSystemException(
                "Could not create/copy a data map to " + dataClass,
                e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.transformer.impl.AbstractTransformer#transform(org.seasar
     * .robot.entity.ResponseData)
     */
    @Override
    public ResultData transform(ResponseData responseData) {
        if (responseData == null || responseData.getResponseBody() == null) {
            throw new RobotCrawlAccessException("No response body.");
        }

        final File tempFile = createResponseBodyFile(responseData);

        FileInputStream fis = null;

        try {
            fis = new FileInputStream(tempFile);
            DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(fis);

            final StringBuilder buf = new StringBuilder(1000);
            buf.append(getResultDataHeader());
            for (Map.Entry<String, String> entry : fieldRuleMap.entrySet()) {
                final List<String> nodeStrList = new ArrayList<String>();
                try {
                    final NodeList nodeList =
                        getXPathAPI().selectNodeList(doc, entry.getValue());
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        final Node node = nodeList.item(i);
                        nodeStrList.add(node.getTextContent());
                    }
                } catch (TransformerException e) {
                    logger.warn("Could not parse a value of " + entry.getKey()
                        + ":" + entry.getValue());
                }
                if (nodeStrList.size() == 1) {
                    buf.append(getResultDataBody(
                        entry.getKey(),
                        nodeStrList.get(0)));
                } else if (nodeStrList.size() > 1) {
                    buf.append(getResultDataBody(entry.getKey(), nodeStrList));
                }
            }
            buf.append(getAdditionalData(responseData, doc));
            buf.append(getResultDataFooter());

            final ResultData resultData = new ResultData();
            resultData.setTransformerName(getName());

            try {
                resultData.setData(buf.toString().getBytes(charsetName));
            } catch (UnsupportedEncodingException e) {
                if (logger.isInfoEnabled()) {
                    logger.info("Invalid charsetName: " + charsetName
                        + ". Changed to " + Constants.UTF_8, e);
                }
                charsetName = Constants.UTF_8;
                try {
                    resultData.setData(buf.toString().getBytes(charsetName));
                } catch (UnsupportedEncodingException e1) {
                    throw new RobotSystemException("Unexpected exception", e);
                }
            }
            resultData.setEncoding(charsetName);

            return resultData;
        } catch (RobotSystemException e) {
            throw e;
        } catch (Exception e) {
            throw new RobotSystemException("Could not store data.", e);
        } finally {
            IOUtils.closeQuietly(fis);
            // clean up
            if (!tempFile.delete()) {
                logger.warn("Could not delete a temp file: " + tempFile);
            }
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

    protected File createResponseBodyFile(final ResponseData responseData) {
        File tempFile = null;
        final InputStream is = responseData.getResponseBody();
        FileOutputStream fos = null;
        try {
            tempFile = File.createTempFile("s2robot-XmlTransformer-", ".xml");
            fos = new FileOutputStream(tempFile);
            StreamUtil.drain(is, fos);
        } catch (Exception e) {
            IOUtils.closeQuietly(fos);
            // clean up
            if (tempFile != null && !tempFile.delete()) {
                logger.warn("Could not delete a temp file: " + tempFile);
            }
            throw new RobotCrawlAccessException(
                "Could not read a response body: " + responseData.getUrl(),
                e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(fos);
        }
        return tempFile;
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
            for (String value : values) {
                buf.append("<item>");
                buf.append(trimSpace(XmlUtil.escapeXml(value)));
                buf.append("</item>");
            }
        }
        buf.append("</list>");
        // TODO support other type
        // TODO trim(default)
        return "<field name=\"" + XmlUtil.escapeXml(name) + "\">"
            + buf.toString() + "</field>\n";
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
     * @param fieldRuleMap
     *            the fieldRuleMap to set
     */
    public void setFieldRuleMap(Map<String, String> fieldRuleMap) {
        this.fieldRuleMap = fieldRuleMap;
    }

    /**
     * @return the trimSpace
     */
    public boolean isTrimSpace() {
        return trimSpace;
    }

    /**
     * @param trimSpace
     *            the trimSpace to set
     */
    public void setTrimSpace(boolean trimSpace) {
        this.trimSpace = trimSpace;
    }

    /**
     * @return the charsetName
     */
    public String getCharsetName() {
        return charsetName;
    }

    /**
     * @param charsetName
     *            the charsetName to set
     */
    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    /**
     * @return the dataClass
     */
    public Class<?> getDataClass() {
        return dataClass;
    }

    /**
     * @param dataClass
     *            the dataClass to set
     */
    public void setDataClass(Class<?> dataClass) {
        this.dataClass = dataClass;
    }
}
