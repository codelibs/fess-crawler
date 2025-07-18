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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathNodes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.util.XPathAPI;
import org.codelibs.fess.crawler.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import jakarta.annotation.Resource;

/**
 * <p>
 * XmlTransformer is a class that extends AbstractTransformer to transform XML documents into a specific format for indexing.
 * It uses XPath expressions to extract data from the XML and stores it in a ResultData object.
 * </p>
 *
 * <p>
 * This class provides several configuration options to customize the XML parsing process, such as:
 * </p>
 * <ul>
 *   <li>Namespace awareness</li>
 *   <li>Coalescing</li>
 *   <li>Entity expansion</li>
 *   <li>Ignoring comments and whitespace</li>
 *   <li>Validation</li>
 *   <li>XInclude awareness</li>
 * </ul>
 *
 * <p>
 * It also allows defining field rules using XPath expressions to extract specific data from the XML document and map it to fields in the ResultData.
 * The extracted data is then formatted into an XML structure suitable for indexing.
 * </p>
 *
 * <p>
 * The class uses a cache for XPathAPI objects to improve performance. The cache duration is configurable.
 * </p>
 *
 * <p>
 * The transform method takes a ResponseData object containing the XML content and returns a ResultData object with the extracted and formatted data.
 * </p>
 *
 * <p>
 * The getData method returns the data extracted from AccessResultData. It can return either a String representation of the XML or a Map/Bean representation based on the configured dataClass.
 * </p>
 *
 * <p>
 * Example Usage:
 * </p>
 *
 * <pre>
 * XmlTransformer transformer = new XmlTransformer();
 * transformer.setNamespaceAware(true);
 * transformer.setCacheDuration(30);
 * transformer.addFieldRule("title", "/book/title/text()");
 * transformer.addFieldRule("author", "/book/author/name/text()");
 *
 * ResponseData responseData = new ResponseData();
 * responseData.setResponseBody(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
 * responseData.setEncoding("UTF-8");
 *
 * ResultData resultData = transformer.transform(responseData);
 * String extractedData = new String(resultData.getData(), StandardCharsets.UTF_8);
 * System.out.println(extractedData);
 * </pre>
 */
public class XmlTransformer extends AbstractTransformer {
    private static final Logger logger = LogManager.getLogger(XmlTransformer.class);

    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+", Pattern.MULTILINE);

    /**
     * If true, the parser will be namespace aware.
     */
    protected boolean namespaceAware;

    /**
     * If true, the parser will convert CDATA nodes to Text nodes and append them to the adjacent Text node.
     */
    protected boolean coalescing;

    /**
     * If true, the parser will expand entity reference nodes.
     */
    protected boolean expandEntityRef = false;

    /**
     * If true, the parser will ignore comments.
     */
    protected boolean ignoringComments;

    /**
     * If true, the parser will ignore ignorable whitespace in element content.
     */
    protected boolean ignoringElementContentWhitespace;

    /**
     * If true, the parser will validate the document against its grammar.
     */
    protected boolean validating;

    /**
     * If true, the parser will be XInclude aware.
     */
    protected boolean includeAware;

    /**
     * A map of attributes.
     */
    protected final Map<String, Object> attributeMap = new HashMap<>();

    /**
     * A map of features.
     */
    protected final Map<String, String> featureMap = new HashMap<>();

    /**
     * A map of field rules.
     */
    protected Map<String, String> fieldRuleMap = new LinkedHashMap<>();

    /** a flag to trim a space characters. */
    protected boolean trimSpaceEnabled = true;

    /**
     * The charset name.
     */
    protected String charsetName = Constants.UTF_8;

    /**
     * Class type returned by getData() method. The default is null(XML content
     * of String).
     */
    protected Class<?> dataClass = null;

    /**
     * The XPathAPI cache.
     */
    protected LoadingCache<String, XPathAPI> xpathAPICache;

    /**
     * The cache duration in minutes.
     */
    protected long cacheDuration = 10; // min

    /**
     * Constructs a new instance of {@code XmlTransformer}.
     * This constructor initializes the transformer with default settings.
     */
    public XmlTransformer() {
        super();
    }

    /**
     * Initializes this component.
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
                throw new CrawlerSystemException("Transformer is invalid. Use " + accessResultData.getTransformerName()
                        + ". This transformer is " + getName() + ".");
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
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(Constants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature(Constants.FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
            factory.setFeature(Constants.FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, StringUtil.EMPTY);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, StringUtil.EMPTY);

            for (final Map.Entry<String, Object> entry : attributeMap.entrySet()) {
                factory.setAttribute(entry.getKey(), entry.getValue());
            }

            for (final Map.Entry<String, String> entry : featureMap.entrySet()) {
                factory.setFeature(entry.getKey(), "true".equalsIgnoreCase(entry.getValue()));
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
            for (final Map.Entry<String, String> entry : fieldRuleMap.entrySet()) {
                final List<String> nodeStrList = new ArrayList<>();
                try {
                    final XPathNodes nodeList = getNodeList(doc, entry.getValue());
                    for (int i = 0; i < nodeList.size(); i++) {
                        final Node node = nodeList.get(i);
                        nodeStrList.add(node.getTextContent());
                    }
                } catch (final XPathExpressionException e) {
                    logger.warn("Could not parse a value of " + entry.getKey() + ":" + entry.getValue(), e);
                }
                if (nodeStrList.size() == 1) {
                    buf.append(getResultDataBody(entry.getKey(), nodeStrList.get(0)));
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
                    logger.info("Invalid charsetName: " + charsetName + ". Changed to " + Constants.UTF_8, e);
                }
                charsetName = Constants.UTF_8_CHARSET.name();
                resultData.setData(data.getBytes(Constants.UTF_8_CHARSET));
            }
            resultData.setEncoding(charsetName);

            return resultData;
        } catch (final CrawlerSystemException e) {
            throw e;
        } catch (final Exception e) {
            throw new CrawlerSystemException("Could not store data.", e);
        }
    }

    /**
     * Retrieves a list of XPath nodes from the document.
     *
     * @param doc The XML document.
     * @param xpath The XPath expression.
     * @return A list of XPath nodes.
     * @throws XPathExpressionException if an XPath expression error occurs.
     */
    protected XPathNodes getNodeList(final Document doc, final String xpath) throws XPathExpressionException {
        final XPath xPathApi = getXPathAPI().createXPath(f -> {});
        xPathApi.setNamespaceContext(new DefaultNamespaceContext(doc.getNodeType() == Node.DOCUMENT_NODE ? doc.getDocumentElement() : doc));
        return xPathApi.evaluateExpression(xpath, doc, XPathNodes.class);
    }

    /**
     * Retrieves an XPathAPI instance from the cache or creates a new one.
     * @return An XPathAPI instance.
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
     * Returns the header for the result data.
     * @return The result data header.
     */
    protected String getResultDataHeader() {
        // TODO support other type
        return "<?xml version=\"1.0\"?>\n<doc>\n";
    }

    /**
     * Returns the body of the result data for a single value.
     * @param name The name of the field.
     * @param value The value of the field.
     * @return The result data body.
     */
    protected String getResultDataBody(final String name, final String value) {
        // TODO support other type
        // TODO trim(default)
        return "<field name=\"" + XmlUtil.escapeXml(name) + "\">" + trimSpace(XmlUtil.escapeXml(value != null ? value : "")) + "</field>\n";
    }

    /**
     * Returns the body of the result data for multiple values.
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
     * Returns additional data for the result.
     * @param responseData The response data.
     * @param document The XML document.
     * @return Additional data as a string.
     */
    protected String getAdditionalData(final ResponseData responseData, final Document document) {
        return "";
    }

    /**
     * Returns the footer for the result data.
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
     * Adds an attribute to the factory.
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     */
    public void addAttribute(final String name, final Object value) {
        attributeMap.put(name, value);
    }

    /**
     * Adds a feature to the factory.
     * @param key The key of the feature.
     * @param value The value of the feature.
     */
    public void addFeature(final String key, final String value) {
        featureMap.put(key, value);
    }

    /**
     * Adds a field rule.
     * @param name The name of the field.
     * @param xpath The XPath expression for the field.
     */
    public void addFieldRule(final String name, final String xpath) {
        fieldRuleMap.put(name, xpath);
    }

    /**
     * Returns the fieldRuleMap.
     *
     * @return the fieldRuleMap
     */
    public Map<String, String> getFieldRuleMap() {
        return fieldRuleMap;
    }

    /**
     * Sets the fieldRuleMap.
     *
     * @param fieldRuleMap the fieldRuleMap to set
     */
    public void setFieldRuleMap(final Map<String, String> fieldRuleMap) {
        this.fieldRuleMap = fieldRuleMap;
    }

    /**
     * Returns the trimSpace.
     *
     * @return the trimSpace
     */
    public boolean isTrimSpace() {
        return trimSpaceEnabled;
    }

    /**
     * Sets the trimSpace.
     *
     * @param trimSpace the trimSpace to set
     */
    public void setTrimSpace(final boolean trimSpace) {
        trimSpaceEnabled = trimSpace;
    }

    /**
     * Returns the charsetName.
     *
     * @return the charsetName
     */
    public String getCharsetName() {
        return charsetName;
    }

    /**
     * Sets the charsetName.
     *
     * @param charsetName the charsetName to set
     */
    public void setCharsetName(final String charsetName) {
        this.charsetName = charsetName;
    }

    /**
     * Returns the dataClass.
     *
     * @return the dataClass
     */
    public Class<?> getDataClass() {
        return dataClass;
    }

    /**
     * Sets the dataClass.
     *
     * @param dataClass the dataClass to set
     */
    public void setDataClass(final Class<?> dataClass) {
        this.dataClass = dataClass;
    }

    /**
     * Returns the namespaceAware.
     *
     * @return the namespaceAware
     */
    public boolean isNamespaceAware() {
        return namespaceAware;
    }

    /**
     * Sets the namespaceAware.
     *
     * @param namespaceAware the namespaceAware to set
     */
    public void setNamespaceAware(final boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }

    /**
     * Returns the coalescing.
     *
     * @return the coalescing
     */
    public boolean isCoalescing() {
        return coalescing;
    }

    /**
     * Sets the coalescing.
     *
     * @param coalescing the coalescing to set
     */
    public void setCoalescing(final boolean coalescing) {
        this.coalescing = coalescing;
    }

    /**
     * Returns the expandEntityRef.
     *
     * @return the expandEntityRef
     */
    public boolean isExpandEntityRef() {
        return expandEntityRef;
    }

    /**
     * Sets the expandEntityRef.
     *
     * @param expandEntityRef the expandEntityRef to set
     */
    public void setExpandEntityRef(final boolean expandEntityRef) {
        this.expandEntityRef = expandEntityRef;
    }

    /**
     * Returns the ignoringComments.
     *
     * @return the ignoringComments
     */
    public boolean isIgnoringComments() {
        return ignoringComments;
    }

    /**
     * Sets the ignoringComments.
     *
     * @param ignoringComments the ignoringComments to set
     */
    public void setIgnoringComments(final boolean ignoringComments) {
        this.ignoringComments = ignoringComments;
    }

    /**
     * Returns the ignoringElementContentWhitespace.
     *
     * @return the ignoringElementContentWhitespace
     */
    public boolean isIgnoringElementContentWhitespace() {
        return ignoringElementContentWhitespace;
    }

    /**
     * Sets the ignoringElementContentWhitespace.
     *
     * @param ignoringElementContentWhitespace the ignoringElementContentWhitespace to set
     */
    public void setIgnoringElementContentWhitespace(final boolean ignoringElementContentWhitespace) {
        this.ignoringElementContentWhitespace = ignoringElementContentWhitespace;
    }

    /**
     * Returns the validating.
     *
     * @return the validating
     */
    public boolean isValidating() {
        return validating;
    }

    /**
     * Sets the validating.
     *
     * @param validating the validating to set
     */
    public void setValidating(final boolean validating) {
        this.validating = validating;
    }

    /**
     * Returns the includeAware.
     *
     * @return the includeAware
     */
    public boolean isIncludeAware() {
        return includeAware;
    }

    /**
     * Sets the includeAware.
     *
     * @param includeAware the includeAware to set
     */
    public void setIncludeAware(final boolean includeAware) {
        this.includeAware = includeAware;
    }

    /**
     * Sets the cache duration.
     * @param cacheDuration The cache duration in minutes.
     */
    public void setCacheDuration(final long cacheDuration) {
        this.cacheDuration = cacheDuration;
    }

    /**
     * DefaultNamespaceContext is a custom implementation of NamespaceContext.
     * It is used to resolve namespace URIs for XML elements and attributes
     * within the context of the provided XML document node.
     */
    private static final class DefaultNamespaceContext implements NamespaceContext {

        private final Node doc;

        public DefaultNamespaceContext(final Node doc) {
            this.doc = doc;
        }

        @Override
        public String getNamespaceURI(final String prefix) {
            return getNamespaceForPrefix(prefix, doc);
        }

        private String getNamespaceForPrefix(final String prefix, final Node namespaceContext) {
            Node parent = namespaceContext;
            String namespace = null;

            if ("xml".equals(prefix)) {
                namespace = "http://www.w3.org/XML/1998/namespace";
            } else {
                int type;
                while (null != parent && null == namespace
                        && ((type = parent.getNodeType()) == Node.ELEMENT_NODE || type == Node.ENTITY_REFERENCE_NODE)) {
                    if (type == Node.ELEMENT_NODE) {
                        if (parent.getNodeName().indexOf(prefix + ":") == 0) {
                            return parent.getNamespaceURI();
                        }
                        final NamedNodeMap nnm = parent.getAttributes();

                        for (int i = 0; i < nnm.getLength(); i++) {
                            final Node attr = nnm.item(i);
                            final String aname = attr.getNodeName();
                            final boolean isPrefix = aname.startsWith("xmlns:");

                            if (isPrefix || "xmlns".equals(aname)) {
                                final int index = aname.indexOf(':');
                                final String p = isPrefix ? aname.substring(index + 1) : StringUtil.EMPTY;
                                if (p.equals(prefix)) {
                                    namespace = attr.getNodeValue();
                                    break;
                                }
                            }
                        }
                    }

                    parent = parent.getParentNode();
                }
            }

            if (StringUtil.isNotBlank(namespace)) {
                return namespace;
            }
            return "http://crawler.codelibs.org/namespace/" + prefix;
        }

        @Override
        public Iterator<String> getPrefixes(final String val) {
            return null; // not used
        }

        @Override
        public String getPrefix(final String uri) {
            return null; // not used
        }
    }

}
