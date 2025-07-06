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
package org.codelibs.fess.crawler.entity;

import java.io.IOException;

import org.opensearch.core.xcontent.ToXContent;
import org.opensearch.core.xcontent.XContentBuilder;

/**
 * OpenSearchUrlFilter is an entity for URL filters in OpenSearch.
 */
public class OpenSearchUrlFilter implements ToXContent {

    /**
     * Creates a new instance of OpenSearchUrlFilter.
     */
    public OpenSearchUrlFilter() {
        // NOP
    }

    /**
     * Field name for session ID.
     */
    public static final String SESSION_ID = "sessionId";

    /**
     * Field name for filter type.
     */
    public static final String FILTER_TYPE = "filterType";

    /**
     * Field name for URL.
     */
    public static final String URL = "url";

    /**
     * The unique identifier for this URL filter.
     */
    private String id;

    /**
     * The session ID associated with this URL filter.
     */
    private String sessionId;

    /**
     * The type of filter (e.g., include, exclude).
     */
    private String filterType;

    /**
     * The URL pattern for this filter.
     */
    private String url;

    /**
     * Returns the ID.
     * @return The ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID.
     * @param id The ID.
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Returns the session ID.
     * @return The session ID.
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session ID.
     * @param sessionId The session ID.
     */
    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Returns the filter type.
     * @return The filter type.
     */
    public String getFilterType() {
        return filterType;
    }

    /**
     * Sets the filter type.
     * @param filterType The filter type.
     */
    public void setFilterType(final String filterType) {
        this.filterType = filterType;
    }

    /**
     * Returns the URL.
     * @return The URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL.
     * @param url The URL.
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * Converts this URL filter to XContent format for OpenSearch indexing.
     *
     * @param builder The XContentBuilder to write to.
     * @param params Additional parameters for the conversion.
     * @return The XContentBuilder with the URL filter data.
     * @throws IOException if the conversion fails.
     */
    @Override
    public XContentBuilder toXContent(final XContentBuilder builder, final Params params) throws IOException {
        builder.startObject();
        if (sessionId != null) {
            builder.field(SESSION_ID, sessionId);
        }
        if (filterType != null) {
            builder.field(FILTER_TYPE, filterType);
        }
        if (url != null) {
            builder.field(URL, url);
        }
        builder.endObject();
        return builder;
    }

}
