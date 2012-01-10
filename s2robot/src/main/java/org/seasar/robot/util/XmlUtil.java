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
package org.seasar.robot.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.seasar.framework.util.StringUtil;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.AccessResultData;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author shinsuke
 * 
 */
public class XmlUtil {

    private XmlUtil() {
    }

    public static String escapeXml(final String value) {
        // return StringEscapeUtils.escapeXml(value);
        return stripInvalidXMLCharacters(//
        value//
            .replaceAll("&", "&amp;")
            //
            .replaceAll("<", "&lt;")
            //
            .replaceAll(">", "&gt;")
            //
            .replaceAll("\"", "&quot;")
            //
            .replaceAll("\'", "&apos;")//
        );
    }

    public static String stripInvalidXMLCharacters(final String in) {
        if (StringUtil.isEmpty(in)) {
            return in;
        }

        final StringBuilder buf = new StringBuilder();
        char c;
        for (int i = 0; i < in.length(); i++) {
            c = in.charAt(i);
            if ((c == 0x9) || (c == 0xA) || (c == 0xD)
                || ((c >= 0x20) && (c <= 0xD7FF))
                || ((c >= 0xE000) && (c <= 0xFFFD))
                || ((c >= 0x10000) && (c <= 0x10FFFF))) {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    public static Map<String, Object> getDataMap(
            final AccessResultData accessResultData) {
        // create input source
        final InputSource is =
            new InputSource(
                new ByteArrayInputStream(accessResultData.getData()));
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
        } catch (Exception e) {
            throw new RobotSystemException(
                "Could not create a data map from XML content.",
                e);
        }
    }

    private static class DocHandler extends DefaultHandler {
        private Map<String, Object> dataMap = new HashMap<String, Object>();

        private String fieldName;

        private boolean listData = false;

        private boolean itemData = false;

        public void startDocument() {
            dataMap.clear();
        }

        public void startElement(final String uri, final String localName,
                final String qName, final Attributes attributes) {
            if ("field".equals(qName)) {
                fieldName = attributes.getValue("name");
            } else if ("list".equals(qName)) {
                listData = true;
                if (!dataMap.containsKey(fieldName)) {
                    dataMap.put(fieldName, new ArrayList<String>());
                }
            } else if ("item".equals(qName)) {
                itemData = true;
            }
        }

        public void characters(final char[] ch, final int offset,
                final int length) {
            if (fieldName != null) {
                final Object value = dataMap.get(fieldName);
                if (listData && itemData) {
                    if (value != null) {
                        ((List<String>) value).add(new String(
                            ch,
                            offset,
                            length));
                    }
                } else {
                    if (value == null) {
                        dataMap.put(fieldName, new String(ch, offset, length));
                    } else {
                        dataMap.put(fieldName, value
                            + new String(ch, offset, length));
                    }
                }
            }
        }

        public void endElement(final String uri, final String localName,
                final String qName) {
            if ("field".equals(qName)) {
                fieldName = null;
            } else if ("list".equals(qName)) {
                listData = false;
            } else if ("item".equals(qName)) {
                itemData = false;
            }
        }

        public void endDocument() {
            // nothing
        }

        public Map<String, Object> getDataMap() {
            return dataMap;
        }
    }
}
