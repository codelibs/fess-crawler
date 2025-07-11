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

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;

/**
 * Extracts the filename from the parameters.
 */
public class FilenameExtractor extends AbstractExtractor {

    /**
     * Constructs a new FilenameExtractor.
     */
    public FilenameExtractor() {
        // Default constructor
    }

    /**
     * Extracts the filename from the parameters.
     * @param in The input stream (not used).
     * @param params The parameters, expected to contain ExtractData.RESOURCE_NAME_KEY.
     * @return An ExtractData object containing the filename as content.
     */
    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        if (in == null) {
            throw new CrawlerSystemException("The inputstream is null.");
        }
        final String content = params.getOrDefault(ExtractData.RESOURCE_NAME_KEY, StringUtil.EMPTY);
        try {
            return new ExtractData(content);
        } catch (final Exception e) {
            throw new ExtractException(e);
        }
    }
}
