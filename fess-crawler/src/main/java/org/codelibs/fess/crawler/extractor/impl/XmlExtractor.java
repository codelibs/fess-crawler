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
package org.codelibs.fess.crawler.extractor.impl;

import java.util.regex.Pattern;

/**
 * Extracts text content from XML documents.
 */
public class XmlExtractor extends AbstractXmlExtractor {

    /**
     * Creates a new XmlExtractor instance.
     */
    public XmlExtractor() {
        super();
    }

    /**
     * Pattern for XML encoding.
     */
    protected Pattern xmlEncodingPattern =
            Pattern.compile("<\\?xml.*encoding\\s*=\\s*['\"]([\\w\\d\\-_]*)['\"]\\s*\\?>", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for XML tags.
     */
    protected Pattern xmlTagPattern = Pattern.compile("<[^>]+>");

    /**
     * Returns the encoding pattern.
     * @return The encoding pattern.
     */
    @Override
    protected Pattern getEncodingPattern() {
        return xmlEncodingPattern;
    }

    /**
     * Returns the precompiled {@link Pattern} used to match XML tags within the content.
     * This pattern is utilized by the extractor to identify and process XML elements.
     *
     * @return the {@link Pattern} instance for XML tag matching
     */
    @Override
    protected Pattern getTagPattern() {
        return xmlTagPattern;
    }

    /**
     * Returns the XML encoding pattern.
     * @return The XML encoding pattern.
     */
    public Pattern getXmlEncodingPattern() {
        return xmlEncodingPattern;
    }

    /**
     * Sets the XML encoding pattern.
     * @param metaCharsetPattern The XML encoding pattern.
     */
    public void setXmlEncodingPattern(final Pattern metaCharsetPattern) {
        xmlEncodingPattern = metaCharsetPattern;
    }

    /**
     * Returns the XML tag pattern.
     * @return The XML tag pattern.
     */
    public Pattern getXmlTagPattern() {
        return xmlTagPattern;
    }

    /**
     * Sets the XML tag pattern.
     * @param htmlTagPattern The XML tag pattern.
     */
    public void setXmlTagPattern(final Pattern htmlTagPattern) {
        xmlTagPattern = htmlTagPattern;
    }
}
