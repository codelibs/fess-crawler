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
package org.codelibs.fess.crawler.helper;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;

/**
 * Helper class for managing content length limits based on MIME types.
 * It allows setting default and MIME type-specific maximum content lengths.
 * The class provides methods to add, retrieve, and manage these limits.
 */
public class ContentLengthHelper {
    /**
     * Constructs a new ContentLengthHelper.
     */
    public ContentLengthHelper() {
        // Default constructor
    }

    /** Default maximum content length set to 10MB */
    protected long defaultMaxLength = 10L * 1024L * 1024L;

    /** Map to store maximum content lengths for specific MIME types */
    protected Map<String, Long> maxLengthMap = new HashMap<>();

    /**
     * Adds a maximum content length for a specific MIME type.
     * @param mimeType The MIME type for which to set the maximum length
     * @param maxLength The maximum content length in bytes
     * @throws CrawlerSystemException if the MIME type is blank or maxLength is negative
     */
    public void addMaxLength(final String mimeType, final long maxLength) {
        if (StringUtil.isBlank(mimeType)) {
            throw new CrawlerSystemException("MIME type is a blank.");
        }
        if (maxLength < 0) {
            throw new CrawlerSystemException("The value of maxLength is invalid.");
        }
        maxLengthMap.put(mimeType, maxLength);
    }

    /**
     * Gets the maximum content length for a specific MIME type.
     * If no specific length is set for the MIME type, returns the default maximum length.
     * @param mimeType The MIME type to get the maximum length for
     * @return The maximum content length in bytes
     */
    public long getMaxLength(final String mimeType) {
        if (StringUtil.isBlank(mimeType)) {
            return defaultMaxLength;
        }
        final Long maxLength = maxLengthMap.get(mimeType);
        if (maxLength != null && maxLength >= 0L) {
            return maxLength;
        }
        return defaultMaxLength;
    }

    /**
     * Returns the default maximum content length.
     * @return The default maximum content length in bytes.
     */
    public long getDefaultMaxLength() {
        return defaultMaxLength;
    }

    /**
     * Sets the default maximum content length.
     * @param defaultMaxLength The default maximum content length to set.
     */
    public void setDefaultMaxLength(final long defaultMaxLength) {
        if (defaultMaxLength < 0) {
            throw new CrawlerSystemException("The value of defaultMaxLength is invalid.");
        }
        this.defaultMaxLength = defaultMaxLength;
    }
}
