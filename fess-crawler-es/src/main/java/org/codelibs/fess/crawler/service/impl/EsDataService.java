/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import javax.annotation.PostConstruct;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.crawler.entity.EsAccessResult;
import org.codelibs.fess.crawler.entity.EsAccessResultData;
import org.codelibs.fess.crawler.exception.EsAccessException;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.util.AccessResultCallback;
import org.codelibs.fess.crawler.util.EsResultList;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;

public class EsDataService extends AbstractCrawlerService implements DataService<EsAccessResult> {

    @PostConstruct
    public void init() {
        esClient.addOnConnectListener(() -> {
            createMapping("data");
        });
    }

    @Override
    public void store(final EsAccessResult accessResult) {
        super.insert(accessResult, accessResult.getId() == null ? OpType.CREATE : OpType.INDEX);
    }

    @Override
    public void update(final EsAccessResult accessResult) {
        super.insert(accessResult, OpType.INDEX);
    }

    @Override
    public void update(final List<EsAccessResult> accessResultList) {
        insertAll(accessResultList, OpType.INDEX);
    }

    @Override
    public int getCount(final String sessionId) {
        return getCount(builder -> builder.setQuery(QueryBuilders.termQuery(SESSION_ID, sessionId)));
    }

    @Override
    public void delete(final String sessionId) {
        deleteBySessionId(sessionId);
    }

    @Override
    public EsAccessResult getAccessResult(final String sessionId, final String url) {
        return get(EsAccessResult.class, sessionId, url);
    }

    @Override
    public List<EsAccessResult> getAccessResultList(final String url, final boolean hasData) {
        return getList(EsAccessResult.class, builder -> builder.setQuery(QueryBuilders.termQuery(URL, url)));
    }

    public List<EsAccessResult> getAccessResultList(final Consumer<SearchRequestBuilder> callback) {
        final SearchResponse response = getClient().get(c -> {
            final SearchRequestBuilder builder = c.prepareSearch(index).setTypes(type);
            callback.accept(builder);
            builder.addFields(new String[] { "parentUrl", "method", "mimeType", "sessionId", "url", "executionTime", "createTime",
                    "contentLength", "lastModified", "ruleId", "httpStatusCode", "status" });
            return builder.execute();
        });
        final EsResultList<EsAccessResult> targetList = new EsResultList<>();
        final SearchHits hits = response.getHits();
        targetList.setTotalHits(hits.getTotalHits());
        targetList.setTookInMillis(response.getTookInMillis());
        if (hits.getTotalHits() != 0) {
            try {
                for (final SearchHit searchHit : hits.getHits()) {
                    final EsAccessResult target = new EsAccessResult();
                    final Map<String, SearchHitField> fields = searchHit.getFields();
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
                throw new EsAccessException("response: " + response, e);
            }
        }
        return targetList;
    }

    private <T> T getFieldValue(SearchHitField field, Class<T> clazz) {
        if (field == null) {
            return null;
        } else if (clazz.equals(Integer.class)) {
            Number value = field.getValue();
            if (value == null) {
                return null;
            } else {
                @SuppressWarnings("unchecked")
                T v = (T) Integer.valueOf(value.intValue());
                return v;
            }
        } else if (clazz.equals(Long.class)) {
            Number value = field.getValue();
            if (value == null) {
                return null;
            } else {
                @SuppressWarnings("unchecked")
                T v = (T) Long.valueOf(value.longValue());
                return v;
            }
        }
        return field.getValue();
    }

    @Override
    public void iterate(final String sessionId, final AccessResultCallback<EsAccessResult> callback) {
        SearchResponse response = null;
        while (true) {
            if (response == null) {
                response = getClient().get(c -> c.prepareSearch(index).setTypes(type).setScroll(new TimeValue(scrollTimeout))
                        .setQuery(QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(SESSION_ID, sessionId))).setSize(scrollSize)
                        .execute());
            } else {
                final String scrollId = response.getScrollId();
                response = getClient().get(c -> c.prepareSearchScroll(scrollId).setScroll(new TimeValue(scrollTimeout)).execute());
            }
            final SearchHits searchHits = response.getHits();
            if (searchHits.hits().length == 0) {
                break;
            }

            for (final SearchHit searchHit : searchHits) {
                final Map<String, Object> source = searchHit.getSource();
                final EsAccessResult accessResult = BeanUtil.copyMapToNewBean(source, EsAccessResult.class, option -> {
                    option.converter(new EsTimestampConverter(), timestampFields).excludeWhitespace();
                    option.exclude(EsAccessResult.ACCESS_RESULT_DATA);
                });
                @SuppressWarnings("unchecked")
                final Map<String, Object> data = (Map<String, Object>) source.get(EsAccessResult.ACCESS_RESULT_DATA);
                if (data != null) {
                    accessResult.setAccessResultData(new EsAccessResultData(data));
                }
                callback.iterate(accessResult);
            }
        }
    }
}
