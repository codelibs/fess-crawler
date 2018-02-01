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
package org.codelibs.fess.crawler.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author shinsuke
 *
 */
public final class XmlUtil {

    private XmlUtil() {
    }

    public static String escapeXml(final String value) {
        return stripInvalidXMLCharacters(//
                value//
                        .replaceAll("&", "&amp;") //
                        .replaceAll("<", "&lt;")//
                        .replaceAll(">", "&gt;")//
                        .replaceAll("\"", "&quot;")//
                        .replaceAll("\'", "&apos;")//
        );
    }

    public static String stripInvalidXMLCharacters(final String in) {
        if (StringUtil.isEmpty(in)) {
            return in;
        }

        final StringBuilder buf = new StringBuilder(in.length());
        char c;
        for (int i = 0; i < in.length(); i++) {
            c = in.charAt(i);
            if (c == 0x9 || c == 0xA || c == 0xD || c >= 0x20 && c <= 0xD7FF
                    || c >= 0xE000 && c <= 0xFFFD || c >= 0x10000
                    && c <= 0x10FFFF) {
                buf.append(c);
            }
        }
        return buf.toString().trim();
    }

    public static Map<String, Object> getDataMap(
            final AccessResultData<?> accessResultData) {
        // create input source
        final InputSource is = new InputSource(new ByteArrayInputStream(
                accessResultData.getData()));
        if (StringUtil.isNotBlank(accessResultData.getEncoding())) {
            is.setEncoding(accessResultData.getEncoding());
        }

        // create handler
        final DocHandler handler = new DocHandler();

        // create a sax instance
        final SAXParserFactory spfactory = SAXParserFactory.newInstance();
        try {
            // create a sax parser
            final SAXParser parser = spfactory.newSAXParser();
            // parse a content
            parser.parse(is, handler);

            return handler.getDataMap();
        } catch (final Exception e) {
            throw new CrawlerSystemException(
                    "Could not create a data map from XML content.", e);
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
        public void startElement(final String uri, final String localName,
                final String qName, final Attributes attributes) {
            if ("field".equals(qName)) {
                fieldName = attributes.getValue("name");
                if (StringUtil.isBlank(fieldName)) {
                    fieldName = null;
                }
                buffer.setLength(0);
            } else if ("list".equals(qName)) {
                if (fieldName != null && !dataMap.containsKey(fieldName)) {
                    dataMap.put(fieldName, new ArrayList<String>());
                }
            } else if ("item".equals(qName)) {
                buffer.setLength(0);
            }
        }

        @Override
        public void characters(final char[] ch, final int offset,
                final int length) {
            buffer.append(new String(ch, offset, length));
        }

        @Override
        public void endElement(final String uri, final String localName,
                final String qName) {
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
