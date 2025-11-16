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

import java.io.InputStream;
import java.util.Map;

import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;

/**
 * Extracts text content from an input stream as plain text.
 */
public class TextExtractor extends AbstractExtractor {

    /**
     * The encoding for text.
     */
    protected String encoding = Constants.UTF_8;

    /**
     * Creates a new TextExtractor instance.
     */
    public TextExtractor() {
        super();
    }

    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        validateInputStream(in);
        try {
            final String content = new String(InputStreamUtil.getBytes(in), getEncoding());
            return new ExtractData(content);
        } catch (final Exception e) {
            throw new ExtractException("Failed to extract text content using encoding: " + getEncoding(), e);
        }
    }

    /**
     * Returns the encoding used for text extraction.
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the encoding.
     * @param encoding The encoding to set.
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
}
