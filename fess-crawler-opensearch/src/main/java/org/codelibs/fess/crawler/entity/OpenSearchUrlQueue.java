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
 * OpenSearchUrlQueue is an implementation of {@link UrlQueue} for OpenSearch.
 */
public class OpenSearchUrlQueue extends UrlQueueImpl<String> implements ToXContent {

    /**
     * Creates a new instance of OpenSearchUrlQueue.
     */
    public OpenSearchUrlQueue() {
        // NOP
    }

    /**
     * Field name for ID.
     */
    public static final String ID = "id";

    /**
     * Field name for session ID.
     */
    public static final String SESSION_ID = "sessionId";

    /**
     * Field name for method.
     */
    public static final String METHOD = "method";

    /**
     * Field name for URL.
     */
    public static final String URL = "url";

    /**
     * Field name for parent URL.
     */
    public static final String PARENT_URL = "parentUrl";

    /**
     * Field name for depth.
     */
    public static final String DEPTH = "depth";

    /**
     * Field name for last modified timestamp.
     */
    public static final String LAST_MODIFIED = "lastModified";

    /**
     * Field name for creation time.
     */
    public static final String CREATE_TIME = "createTime";

    /**
     * Field name for weight.
     */
    public static final String WEIGHT = "weight";

    /**
     * Converts this URL queue entry to XContent format for OpenSearch indexing.
     *
     * @param builder The XContentBuilder to write to.
     * @param params Additional parameters for the conversion.
     * @return The XContentBuilder with the URL queue data.
     * @throws IOException if the conversion fails.
     */
    @Override
    public XContentBuilder toXContent(final XContentBuilder builder, final Params params) throws IOException {
        builder.startObject();
        if (sessionId != null) {
            builder.field(SESSION_ID, sessionId);
        }
        if (method != null) {
            builder.field(METHOD, method);
        }
        if (url != null) {
            builder.field(URL, url);
        }
        if (parentUrl != null) {
            builder.field(PARENT_URL, parentUrl);
        }
        if (depth != null) {
            builder.field(DEPTH, depth);
        }
        if (lastModified != null) {
            builder.field(LAST_MODIFIED, lastModified);
        }
        if (createTime != null) {
            builder.field(CREATE_TIME, createTime);
        }
        builder.field(WEIGHT, weight);
        builder.endObject();
        return builder;
    }

}
