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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.cyberneko.html.parsers.DOMParser;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * @author shinsuke
 *
 */
public class XpathTransformer extends HtmlTransformer {
    private static final Logger logger = LoggerFactory
            .getLogger(XpathTransformer.class);

    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+",
            Pattern.MULTILINE);

    public Map<String, String> fieldRuleMap = new LinkedHashMap<String, String>();

    public boolean trimSpace = true;

    @Override
    protected void storeData(ResponseData responseData, ResultData resultData) {
        DOMParser parser = getDomParser();
        try {
            InputSource is = new InputSource(responseData.getResponseBody());
            if (responseData.getCharSet() != null) {
                is.setEncoding(responseData.getCharSet());
            }
            parser.parse(is);
        } catch (Exception e) {
            throw new RobotSystemException("Could not parse "
                    + responseData.getUrl(), e);
        }
        Document document = parser.getDocument();

        StringBuilder buf = new StringBuilder();
        buf.append(getResultDataHeader());
        for (Map.Entry<String, String> entry : fieldRuleMap.entrySet()) {
            Node value = null;
            try {
                value = getXPathAPI().selectSingleNode(document,
                        entry.getValue());
            } catch (TransformerException e) {
                logger.warn("Could not parse a value of " + entry.getKey()
                        + ":" + entry.getValue());
            }
            buf.append(getResultDataBody(entry.getKey(), value != null ? value
                    .getTextContent() : null));
        }
        buf.append(getResultDataFooter());

        resultData.setData(buf.toString());
    }

    protected String getResultDataHeader() {
        // TODO support other type
        return "<?xml version=\"1.0\"?>\n<doc>\n";
    }

    protected String getResultDataBody(String name, String value) {
        if (value == null) {
            value = "";
        }
        // TODO support other type
        // TODO trim(default)
        return "<field name=\"" + escapeXml(name) + "\">"
                + trimSpace(escapeXml(value)) + "</field>\n";
    }

    protected String getResultDataFooter() {
        // TODO support other type
        return "</doc>";
    }

    protected String escapeXml(String value) {
        //        return StringEscapeUtils.escapeXml(value);
        return stripInvalidXMLCharacters(//
        value//
                .replaceAll("&", "&amp;")//
                .replaceAll("<", "&lt;")//
                .replaceAll(">", "&gt;")//
                .replaceAll("\"", "&quot;")//
                .replaceAll("\'", "&apos;")//
        );
    }

    private String stripInvalidXMLCharacters(String in) {
        if (StringUtil.isEmpty(in)) {
            return in;
        }

        StringBuilder buf = new StringBuilder();
        char c;
        for (int i = 0; i < in.length(); i++) {
            c = in.charAt(i);
            if ((c == 0x9) || (c == 0xA) || (c == 0xD)
                    || ((c >= 0x20) && (c <= 0xD7FF))
                    || ((c >= 0xE000) && (c <= 0xFFFD))
                    || ((c >= 0x10000) && (c <= 0x10FFFF)))
                buf.append(c);
        }
        return buf.toString();
    }

    protected String trimSpace(String value) {
        if (trimSpace) {
            Matcher matcher = SPACE_PATTERN.matcher(value);
            return matcher.replaceAll(" ").trim();
        }
        return value;
    }

    public void addFieldRule(String name, String xpath) {
        fieldRuleMap.put(name, xpath);
    }
}
