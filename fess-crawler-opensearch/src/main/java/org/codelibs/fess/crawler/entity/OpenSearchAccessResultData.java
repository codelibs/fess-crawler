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
import java.util.Map;

import org.codelibs.core.misc.Base64Util;
import org.codelibs.fess.crawler.exception.OpenSearchAccessException;
import org.opensearch.core.xcontent.ToXContent;
import org.opensearch.core.xcontent.XContentBuilder;

public class OpenSearchAccessResultData extends AccessResultDataImpl<String> implements ToXContent {

    public static final String ID = "id";

    public static final String TRANSFORMER_NAME = "transformerName";

    public static final String DATA = "data";

    public static final String ENCODING = "encoding";

    public OpenSearchAccessResultData() {
    }

    public OpenSearchAccessResultData(final Map<String, Object> src) {
        setTransformerName((String) src.get(TRANSFORMER_NAME));
        setEncoding((String) src.get(ENCODING));
        final String dataStr = (String) src.get(DATA);
        if (dataStr != null) {
            try {
                setData(Base64Util.decode(dataStr));
            } catch (final Exception e) {
                throw new OpenSearchAccessException("data: " + dataStr, e);
            }
        }
    }

    @Override
    public XContentBuilder toXContent(final XContentBuilder builder, final Params params) throws IOException {
        builder.startObject();
        if (transformerName != null) {
            builder.field(TRANSFORMER_NAME, transformerName);
        }
        if (data != null) {
            builder.field(DATA, data);
        }
        if (encoding != null) {
            builder.field(ENCODING, encoding);
        }
        builder.endObject();
        return builder;
    }

}
