package org.codelibs.robot.entity;

import java.io.IOException;

import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;

public class EsUrlFilter implements ToXContent {
    private String sessionId;

    private String filterType;

    private String url;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(final String filterType) {
        this.filterType = filterType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public XContentBuilder toXContent(final XContentBuilder builder, final Params params) throws IOException {
        builder.startObject();
        if (sessionId != null) {
            builder.field("sessionId", sessionId);
        }
        if (filterType != null) {
            builder.field("filterType", filterType);
        }
        if (url != null) {
            builder.field("url", url);
        }
        builder.endObject();
        return builder;
    }
}
