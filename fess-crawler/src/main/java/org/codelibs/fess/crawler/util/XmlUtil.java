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
package org.codelibs.fess.crawler.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Utility class for XML-related operations.
 *
 * This class provides methods to escape special characters in XML strings,
 * strip invalid XML characters, and parse XML content into a map of data.
 *
 * <p>
 * The class is final and cannot be instantiated.
 * </p>
 *
 * <h2>Methods:</h2>
 * <ul>
 *   <li>{@link #escapeXml(String)}: Escapes special characters in an XML string.</li>
 *   <li>{@link #stripInvalidXMLCharacters(String)}: Strips invalid XML characters from a string.</li>
 *   <li>{@link #getDataMap(AccessResultData)}: Parses XML content from {@link AccessResultData} and returns a map of the data.</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 * {@code
 * String escapedXml = XmlUtil.escapeXml("<tag>value</tag>");
 * Map<String, Object> dataMap = XmlUtil.getDataMap(accessResultData);
 * }
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is thread-safe as it does not maintain any state.
 * </p>
 *
 * <h2>Dependencies:</h2>
 * <ul>
 *   <li>org.apache.logging.log4j.Logger</li>
 *   <li>org.apache.logging.log4j.LogManager</li>
 *   <li>org.xml.sax.InputSource</li>
 *   <li>javax.xml.parsers.SAXParser</li>
 *   <li>javax.xml.parsers.SAXParserFactory</li>
 *   <li>org.xml.sax.helpers.DefaultHandler</li>
 *   <li>org.codelibs.fess.crawler.Constants</li>
 *   <li>org.codelibs.fess.crawler.exception.CrawlerSystemException</li>
 *   <li>org.codelibs.fess.crawler.entity.AccessResultData</li>
 *   <li>org.codelibs.core.lang.StringUtil</li>
 * </ul>
 *
 * @see org.codelibs.fess.crawler.Constants
 * @see org.codelibs.fess.crawler.exception.CrawlerSystemException
 * @see org.codelibs.fess.crawler.entity.AccessResultData
 * @see org.codelibs.core.lang.StringUtil
 */
public final class XmlUtil {

    private static final Logger logger = LogManager.getLogger(XmlUtil.class);

    private XmlUtil() {
    }

    /**
     * Escapes special characters in the given XML string to their corresponding
     * XML entities. This method replaces the following characters:
     * <ul>
     *   <li>&amp; with &amp;amp;</li>
     *   <li>&lt; with &amp;lt;</li>
     *   <li>&gt; with &amp;gt;</li>
     *   <li>" with &amp;quot;</li>
     *   <li>' with &amp;apos;</li>
     * </ul>
     * Additionally, it strips invalid XML characters from the input string.
     *
     * @param value the input string to be escaped
     * @return the escaped XML string with invalid characters removed
     */
    public static String escapeXml(final String value) {
        return stripInvalidXMLCharacters(//
                value//
                        .replace("&", "&amp;") //
                        .replace("<", "&lt;")//
                        .replace(">", "&gt;")//
                        .replace("\"", "&quot;")//
                        .replace("\'", "&apos;")//
        );
    }

    /**
     * Strips invalid XML characters from the input string.
     *
     * This method removes characters that are not allowed in XML documents
     * according to the XML 1.0 specification. Valid characters include:
     * - Tab (0x9)
     * - Line feed (0xA)
     * - Carriage return (0xD)
     * - Any character between 0x20 and 0xD7FF
     * - Any character between 0xE000 and 0xFFFD
     * - Any character between 0x10000 and 0x10FFFF
     *
     * @param in the input string to be processed
     * @return a new string with invalid XML characters removed, or the original
     *         string if it is empty
     */
    public static String stripInvalidXMLCharacters(final String in) {
        if (StringUtil.isEmpty(in)) {
            return in;
        }

        final StringBuilder buf = new StringBuilder(in.length());
        char c;
        for (int i = 0; i < in.length(); i++) {
            c = in.charAt(i);
            if (c == 0x9 || c == 0xA || c == 0xD || c >= 0x20 && c <= 0xD7FF || c >= 0xE000 && c <= 0xFFFD
                    || c >= 0x10000 && c <= 0x10FFFF) {
                buf.append(c);
            }
        }
        return buf.toString().trim();
    }

    /**
     * Parses the XML content from the provided {@link AccessResultData} and returns a map of the data.
     *
     * @param accessResultData the data containing the XML content to be parsed
     * @return a map containing the parsed data from the XML content
     * @throws CrawlerSystemException if an error occurs while parsing the XML content
     */
    public static Map<String, Object> getDataMap(final AccessResultData<?> accessResultData) {
        // create input source
        final InputSource is = new InputSource(new ByteArrayInputStream(accessResultData.getData()));
        if (StringUtil.isNotBlank(accessResultData.getEncoding())) {
            is.setEncoding(accessResultData.getEncoding());
        }

        // create handler
        final DocHandler handler = new DocHandler();

        // create a sax instance
        final SAXParserFactory spfactory = SAXParserFactory.newInstance();
        try {
            spfactory.setFeature(Constants.FEATURE_SECURE_PROCESSING, true);
            spfactory.setFeature(Constants.FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
            spfactory.setFeature(Constants.FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);
            // create a sax parser
            final SAXParser parser = spfactory.newSAXParser();
            try {
                parser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, StringUtil.EMPTY);
                parser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, StringUtil.EMPTY);
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to set a property.", e);
                }
            }
            // parse a content
            parser.parse(is, handler);

            return handler.getDataMap();
        } catch (final Exception e) {
            throw new CrawlerSystemException("Could not create a data map from XML content.", e);
        }
    }

    private static class DocHandler extends DefaultHandler {
        private final Map<String, Object> dataMap = new HashMap<>();

        private String fieldName;

        private final StringBuilder buffer = new StringBuilder(1000);

        @Override
        public void startDocument() {
            dataMap.clear();
        }

        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) {
            if ("field".equals(qName)) {
                fieldName = attributes.getValue("name");
                if (StringUtil.isBlank(fieldName)) {
                    fieldName = null;
                }
                buffer.setLength(0);
            } else if ("list".equals(qName)) {
                if (fieldName != null && !dataMap.containsKey(fieldName)) {
                    dataMap.put(fieldName, new ArrayList<>());
                }
            } else if ("item".equals(qName)) {
                buffer.setLength(0);
            }
        }

        @Override
        public void characters(final char[] ch, final int offset, final int length) {
            buffer.append(new String(ch, offset, length));
        }

        @Override
        public void endElement(final String uri, final String localName, final String qName) {
            if ("field".equals(qName)) {
                if (fieldName != null) {
                    final Object obj = dataMap.get(fieldName);
                    if (obj == null) {
                        dataMap.put(fieldName, buffer.toString());
                    }
                    fieldName = null;
                }
                // } else if ("list".equals(qName)) {
                // nothing
            } else if ("item".equals(qName) && fieldName != null) {
                final Object obj = dataMap.get(fieldName);
                if (obj instanceof List) {
                    @SuppressWarnings("unchecked")
                    final List<String> list = (List<String>) obj;
                    list.add(buffer.toString());
                }
            }
        }

        @Override
        public void endDocument() {
            // nothing
        }

        public Map<String, Object> getDataMap() {
            return dataMap;
        }
    }
}
