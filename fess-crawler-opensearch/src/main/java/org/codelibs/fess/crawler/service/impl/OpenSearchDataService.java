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
package org.codelibs.fess.crawler.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.TotalHits;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.crawler.entity.OpenSearchAccessResult;
import org.codelibs.fess.crawler.entity.OpenSearchAccessResultData;
import org.codelibs.fess.crawler.exception.OpenSearchAccessException;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.util.AccessResultCallback;
import org.codelibs.fess.crawler.util.OpenSearchCrawlerConfig;
import org.codelibs.fess.crawler.util.OpenSearchResultList;
import org.opensearch.action.DocWriteRequest.OpType;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;

import jakarta.annotation.PostConstruct;

/**
 * OpenSearchDataService is an implementation of {@link DataService} for OpenSearch.
 */
public class OpenSearchDataService extends AbstractCrawlerService implements DataService<OpenSearchAccessResult> {

    /**
     * Logger instance.
     */
    private static final Logger logger = LogManager.getLogger(OpenSearchDataService.class);

    /**
     * Creates a new instance of OpenSearchDataService.
     * @param crawlerConfig The crawler configuration.
     */
    public OpenSearchDataService(final OpenSearchCrawlerConfig crawlerConfig) {
        index = crawlerConfig.getDataIndex();
        setNumberOfShards(crawlerConfig.getDataShards());
        setNumberOfReplicas(crawlerConfig.getDataReplicas());
    }

    /**
     * Creates a new instance of OpenSearchDataService.
     * @param name The name.
     * @param type The type.
     */
    public OpenSearchDataService(final String name, final String type) {
        index = name + "." + type;
    }

    /**
     * Initializes the service.
     */
    @PostConstruct
    public void init() {
        fesenClient.addOnConnectListener(() -> createMapping("data"));
    }

    /**
     * Stores an access result in the OpenSearch index.
     *
     * @param accessResult The access result to store.
     */
    @Override
    public void store(final OpenSearchAccessResult accessResult) {
        super.insert(accessResult, accessResult.getId() == null ? OpType.CREATE : OpType.INDEX);
    }

    /**
     * Updates an access result in the OpenSearch index.
     *
     * @param accessResult The access result to update.
     */
    @Override
    public void update(final OpenSearchAccessResult accessResult) {
        super.insert(accessResult, OpType.INDEX);
    }

    /**
     * Updates multiple access results in the OpenSearch index.
     *
     * @param accessResultList The list of access results to update.
     */
    @Override
    public void update(final List<OpenSearchAccessResult> accessResultList) {
        insertAll(accessResultList, OpType.INDEX);
    }

    /**
     * Gets the count of access results for a specific session.
     *
     * @param sessionId The session ID.
     * @return The number of access results for the session.
     */
    @Override
    public int getCount(final String sessionId) {
        return getCount(builder -> builder.setQuery(QueryBuilders.termQuery(SESSION_ID, sessionId)));
    }

    /**
     * Deletes all access results for a specific session.
     *
     * @param sessionId The session ID.
     */
    @Override
    public void delete(final String sessionId) {
        deleteBySessionId(sessionId);
    }

    /**
     * Gets an access result by session ID and URL.
     *
     * @param sessionId The session ID.
     * @param url The URL.
     * @return The access result, or null if not found.
     */
    @Override
    public OpenSearchAccessResult getAccessResult(final String sessionId, final String url) {
        return get(OpenSearchAccessResult.class, sessionId, url);
    }

    /**
     * Gets a list of access results for a specific URL.
     *
     * @param url The URL to search for.
     * @param hasData Whether to include data (currently not used in this implementation).
     * @return The list of access results.
     */
    @Override
    public List<OpenSearchAccessResult> getAccessResultList(final String url, final boolean hasData) {
        return getList(OpenSearchAccessResult.class, builder -> builder.setQuery(QueryBuilders.termQuery(URL, url)));
    }

    /**
     * Returns a list of OpenSearchAccessResult objects.
     * @param callback The callback to build the search request.
     * @return A list of OpenSearchAccessResult objects.
     */
    public List<OpenSearchAccessResult> getAccessResultList(final Consumer<SearchRequestBuilder> callback) {
        final SearchResponse response = getClient().get(c -> {
            final SearchRequestBuilder builder = c.prepareSearch(index);
            callback.accept(builder);
            builder.setFetchSource(new String[] { "parentUrl", "method", "mimeType", "sessionId", "url", "executionTime", "createTime",
                    "contentLength", "lastModified", "ruleId", "httpStatusCode", "status" }, null);
            return builder.execute();
        });
        final OpenSearchResultList<OpenSearchAccessResult> targetList = new OpenSearchResultList<>();
        final SearchHits hits = response.getHits();
        final TotalHits totalHits = hits.getTotalHits();
        final long totalHitsValue = totalHits != null ? totalHits.value() : 0;
        targetList.setTotalHits(totalHitsValue);
        targetList.setTookInMillis(response.getTook().getMillis());
        if (totalHitsValue != 0) {
            try {
                for (final SearchHit searchHit : hits.getHits()) {
                    final OpenSearchAccessResult target = new OpenSearchAccessResult();
                    final Map<String, Object> fields = searchHit.getSourceAsMap();
                    target.setParentUrl(getFieldValue(fields.get("parentUrl"), String.class));
                    target.setMethod(getFieldValue(fields.get("method"), String.class));
                    target.setMimeType(getFieldValue(fields.get("mimeType"), String.class));
                    target.setSessionId(getFieldValue(fields.get("sessionId"), String.class));
                    target.setUrl(getFieldValue(fields.get("url"), String.class));
                    target.setExecutionTime(getFieldValue(fields.get("executionTime"), Integer.class));
                    target.setContentLength(getFieldValue(fields.get("contentLength"), Long.class));
                    target.setRuleId(getFieldValue(fields.get("ruleId"), String.class));
                    target.setHttpStatusCode(getFieldValue(fields.get("httpStatusCode"), Integer.class));
                    target.setStatus(getFieldValue(fields.get("status"), Integer.class));
                    target.setCreateTime(getFieldValue(fields.get("createTime"), Long.class));
                    target.setLastModified(getFieldValue(fields.get("lastModified"), Long.class));

                    setId(target, searchHit.getId());
                    targetList.add(target);
                }
            } catch (final Exception e) {
                throw new OpenSearchAccessException("response: " + response, e);
            }
        }
        return targetList;
    }

    /**
     * Extracts a field value from OpenSearch results and converts it to the specified type.
     *
     * @param <T> The target type.
     * @param field The field value from OpenSearch.
     * @param clazz The target class.
     * @return The converted field value.
     */
    @SuppressWarnings("unchecked")
    private <T> T getFieldValue(final Object field, final Class<T> clazz) {
        if (field == null) {
            return null;
        }
        if (clazz.equals(Integer.class)) {
            final Number value = (Number) field;
            return (T) Integer.valueOf(value.intValue());
        }
        if (clazz.equals(Long.class)) {
            final Number value = (Number) field;
            return (T) Long.valueOf(value.longValue());
        }
        return (T) field;
    }

    /**
     * Iterates through all access results for a session, calling the callback for each result.
     * Uses OpenSearch Scroll API for efficient iteration over large result sets.
     *
     * @param sessionId The session ID.
     * @param callback The callback to execute for each access result.
     */
    @Override
    public void iterate(final String sessionId, final AccessResultCallback<OpenSearchAccessResult> callback) {
        SearchResponse response = getClient().get(c -> c.prepareSearch(index)
                .setScroll(new TimeValue(scrollTimeout))
                .setQuery(QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(SESSION_ID, sessionId)))
                .setSize(scrollSize)
                .execute());
        String scrollId = response.getScrollId();
        try {
            while (scrollId != null) {
                final SearchHits searchHits = response.getHits();
                if (searchHits.getHits().length == 0) {
                    break;
                }

                for (final SearchHit searchHit : searchHits) {
                    final Map<String, Object> source = searchHit.getSourceAsMap();
                    final OpenSearchAccessResult accessResult = BeanUtil.copyMapToNewBean(source, OpenSearchAccessResult.class, option -> {
                        option.converter(new EsTimestampConverter(), timestampFields).excludeWhitespace();
                        option.exclude(OpenSearchAccessResult.ACCESS_RESULT_DATA);
                    });
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> data = (Map<String, Object>) source.get(OpenSearchAccessResult.ACCESS_RESULT_DATA);
                    if (data != null) {
                        accessResult.setAccessResultData(new OpenSearchAccessResultData(data));
                    }
                    callback.iterate(accessResult);
                }

                final String sid = scrollId;
                response = getClient().get(c -> c.prepareSearchScroll(sid).setScroll(new TimeValue(scrollTimeout)).execute());
                if (!scrollId.equals(response.getScrollId())) {
                    getClient().clearScroll(scrollId);
                }
                scrollId = response.getScrollId();
            }
        } finally {
            getClient().clearScroll(scrollId);
        }
    }
}
