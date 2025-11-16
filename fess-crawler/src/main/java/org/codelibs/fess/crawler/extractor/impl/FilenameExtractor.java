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
 * Extracts the filename from the parameters as the content.
 *
 * <p>This extractor is useful when you want to index only the filename or resource name
 * without processing the actual file content. The input stream is validated but not
 * read - only the filename from the parameters is used as the extracted content.</p>
 *
 * <p>The filename is retrieved from the {@link ExtractData#RESOURCE_NAME_KEY} parameter.
 * If this parameter is not present, an empty string is returned as the content.</p>
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
     *
     * <p>Note: The input stream is validated for consistency with the Extractor interface,
     * but is not actually read. Only the resource name from the parameters is used.</p>
     *
     * @param in The input stream (validated but not read)
     * @param params The parameters map, expected to contain ExtractData.RESOURCE_NAME_KEY
     * @return An ExtractData object containing the filename as content, or empty string if not found
     * @throws CrawlerSystemException if the input stream is null
     * @throws ExtractException if an unexpected error occurs during extraction
     */
    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        validateInputStream(in);
        try {
            final String content = params != null ? params.getOrDefault(ExtractData.RESOURCE_NAME_KEY, StringUtil.EMPTY) : StringUtil.EMPTY;
            return new ExtractData(content);
        } catch (final Exception e) {
            throw new ExtractException("Failed to extract filename from parameters.", e);
        }
    }
}
