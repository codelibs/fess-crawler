package org.codelibs.fess.crawler.entity;

import java.io.IOException;

import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;

public class EsUrlFilter implements ToXContent {

    public static final String SESSION_ID = "sessionId";

    public static final String FILTER_TYPE = "filterType";

    public static final String URL = "url";

    private String id;

    private String sessionId;

    private String filterType;

    private String url;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

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
