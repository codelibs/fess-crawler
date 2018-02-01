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
package org.codelibs.fess.crawler.entity;

import java.io.IOException;

import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;

public class EsUrlQueue extends UrlQueueImpl<String> implements ToXContent {

    public static final String ID = "id";

    public static final String SESSION_ID = "sessionId";

    public static final String METHOD = "method";

    public static final String URL = "url";

    public static final String PARENT_URL = "parentUrl";

    public static final String DEPTH = "depth";

    public static final String LAST_MODIFIED = "lastModified";

    public static final String CREATE_TIME = "createTime";

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
        builder.endObject();
        return builder;
    }

}
