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
package org.codelibs.fess.crawler.extractor.impl;

import java.util.regex.Pattern;

/**
 * @author shinsuke
 *
 */
public class XmlExtractor extends AbstractXmlExtractor {
    protected Pattern xmlEncodingPattern = Pattern.compile(
            "<\\?xml.*encoding\\s*=\\s*['\"]([\\w\\d\\-_]*)['\"]\\s*\\?>",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    protected Pattern xmlTagPattern = Pattern.compile("<[^>]+>");

    @Override
    protected Pattern getEncodingPattern() {
        return xmlEncodingPattern;
    }

    @Override
    protected Pattern getTagPattern() {
        return xmlTagPattern;
    }

    public Pattern getXmlEncodingPattern() {
        return xmlEncodingPattern;
    }

    public void setXmlEncodingPattern(final Pattern metaCharsetPattern) {
        xmlEncodingPattern = metaCharsetPattern;
    }

    public Pattern getXmlTagPattern() {
        return xmlTagPattern;
    }

    public void setXmlTagPattern(final Pattern htmlTagPattern) {
        xmlTagPattern = htmlTagPattern;
    }
}
