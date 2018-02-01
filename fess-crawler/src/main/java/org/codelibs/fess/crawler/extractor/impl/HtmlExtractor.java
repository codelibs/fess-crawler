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
public class HtmlExtractor extends AbstractXmlExtractor {

    protected Pattern metaCharsetPattern = Pattern
            .compile(
                    "<meta.*content\\s*=\\s*['\"].*;\\s*charset=([\\w\\d\\-_]*)['\"]\\s*/?>",
                    Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    protected Pattern htmlTagPattern = Pattern.compile("<[^>]+>");

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.extractor.impl.AbstractXmlExtractor#getEncodingPattern()
     */
    @Override
    protected Pattern getEncodingPattern() {
        return metaCharsetPattern;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.extractor.impl.AbstractXmlExtractor#getTagPattern()
     */
    @Override
    protected Pattern getTagPattern() {
        return htmlTagPattern;
    }

    public Pattern getMetaCharsetPattern() {
        return metaCharsetPattern;
    }

    public void setMetaCharsetPattern(final Pattern metaCharsetPattern) {
        this.metaCharsetPattern = metaCharsetPattern;
    }

    public Pattern getHtmlTagPattern() {
        return htmlTagPattern;
    }

    public void setHtmlTagPattern(final Pattern htmlTagPattern) {
        this.htmlTagPattern = htmlTagPattern;
    }

}
