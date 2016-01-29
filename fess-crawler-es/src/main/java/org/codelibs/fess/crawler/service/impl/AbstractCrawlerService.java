/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.codelibs.core.beans.BeanDesc;
import org.codelibs.core.beans.Converter;
import org.codelibs.core.beans.PropertyDesc;
import org.codelibs.core.beans.factory.BeanDescFactory;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.client.EsClient;
import org.codelibs.fess.crawler.entity.EsAccessResult;
import org.codelibs.fess.crawler.entity.EsAccessResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.EsAccessException;
import org.codelibs.fess.crawler.util.EsResultList;
import org.elasticsearch.action.WriteConsistencyLevel;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public abstract class AbstractCrawlerService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCrawlerService.class);

    private static final String ID_SEPARATOR = ".";

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    protected static final String ID = "id";

    protected static final String SESSION_ID = "sessionId";

    protected static final String URL = "url";

    protected static final String LAST_MODIFIED = "lastModified";

    protected static final String CREATE_TIME = "createTime";

    protected static final String[] timestampFields = new String[] { LAST_MODIFIED, CREATE_TIME };

    protected static final HashFunction murmur3Hash = Hashing.murmur3_128(0);

    protected String index;

    protected String type;

    protected int scrollTimeout = 60000;

    protected int scrollSize = 100;

    protected int bulkBufferSize = 10;

    @Resource
    protected EsClient esClient;

    protected WriteConsistencyLevel writeConsistencyLevel = WriteConsistencyLevel.ALL;

    protected EsClient getClient() {
        if (!esClient.connected()) {
            synchronized (esClient) {
                if (!esClient.connected()) {
                    esClient.connect();
                }
            }
        }
        return esClient;
    }

    protected void createMapping(final String mappingName) {
        boolean exists = false;
        try {
            esClient.prepareExists(index).execute().actionGet();
            exists = true;
        } catch (final IndexNotFoundException e) {
            // ignore
        }
        if (!exists) {
            try {
                final CreateIndexResponse indexResponse = esClient.admin().indices().prepareCreate(index).execute().actionGet();
                if (indexResponse.isAcknowledged()) {
                    logger.info("Created " + index + " index.");
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Failed to create " + index + " index.");
                }
            } catch (final IndexAlreadyExistsException e) {
                // ignore
            }
        }

        final GetMappingsResponse getMappingsResponse =
                esClient.admin().indices().prepareGetMappings(index).setTypes(type).execute().actionGet();
        final ImmutableOpenMap<String, MappingMetaData> indexMappings = getMappingsResponse.mappings().get(index);
        if (indexMappings == null || !indexMappings.containsKey(type)) {
            final PutMappingResponse putMappingResponse = esClient.admin().indices().preparePutMapping(index).setType(type)
                    .setSource(FileUtil.readText("mapping/" + mappingName + ".json")).execute().actionGet();
            if (putMappingResponse.isAcknowledged()) {
                logger.info("Created " + index + "/" + type + " mapping.");
            } else {
                logger.warn("Failed to create " + index + "/" + type + " mapping.");
            }
        } else if (logger.isDebugEnabled()) {
            logger.debug(index + "/" + type + " mapping exists.");
        }
    }

    protected Date getDateFromSource(final Map<String, Object> sourceMap, final String name) {
        final Object obj = sourceMap.get(name);
        if (obj instanceof Date) {
            return (Date) obj;
        } else if (obj instanceof Number) {
            return new Date(((Number) obj).longValue());
        } else if (obj instanceof String) {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                return sdf.parse(obj.toString());
            } catch (final ParseException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse " + obj, e);
                }
            }
        }
        return null;
    }

    protected XContentBuilder getXContentBuilder(final Object target) {
        try {
            return jsonBuilder().value(target);
        } catch (final IOException e) {
            throw new CrawlerSystemException("Failed to convert " + target + " to JSON.", e);
        }
    }

    protected void refresh() {
        getClient().admin().indices().prepareRefresh(index).execute().actionGet();
    }

    protected void insert(final Object target, final OpType opType) {
        final String id = getId(getSessionId(target), getUrl(target));
        final XContentBuilder source = getXContentBuilder(target);
        getClient().prepareIndex(index, type, id).setSource(source).setOpType(opType).setConsistencyLevel(writeConsistencyLevel)
                .setRefresh(true).execute().actionGet();
        setId(target, id);
    }

    protected <T> void insertAll(final List<T> list, final OpType opType) {
        final List<T> bufferedList = new ArrayList<>(bulkBufferSize);
        list.stream().forEach(target -> {
            bufferedList.add(target);
            if (bufferedList.size() >= bulkBufferSize) {
                doInsertAll(bufferedList, opType);
                bufferedList.clear();
            }
        });
        if (!bufferedList.isEmpty()) {
            doInsertAll(bufferedList, opType);
        }
    }

    protected <T> void doInsertAll(final List<T> list, final OpType opType) {
        final BulkRequestBuilder bulkRequest = getClient().prepareBulk();
        for (final T target : list) {
            final String id = getId(getSessionId(target), getUrl(target));
            final XContentBuilder source = getXContentBuilder(target);
            bulkRequest.add(getClient().prepareIndex(index, type, id).setSource(source).setOpType(opType)
                    .setConsistencyLevel(writeConsistencyLevel).setRefresh(true));
            setId(target, id);
        }
        final BulkResponse bulkResponse = bulkRequest.setConsistencyLevel(writeConsistencyLevel).setRefresh(true).execute().actionGet();
        if (bulkResponse.hasFailures()) {
            throw new EsAccessException(bulkResponse.buildFailureMessage());
        }
    }

    protected boolean exists(final String sessionId, final String url) {
        final String id = getId(sessionId, url);
        final GetResponse response = getClient().prepareGet(index, type, id).setRefresh(true).execute().actionGet();
        return response.isExists();
    }

    public int getCount(final Consumer<CountRequestBuilder> callback) {
        final CountRequestBuilder builder = getClient().prepareCount(index).setTypes(type);
        callback.accept(builder);
        return (int) builder.execute().actionGet().getCount();
    }

    protected <T> T get(final Class<T> clazz, final String sessionId, final String url) {
        final String id = getId(sessionId, url);
        final GetResponse response = getClient().prepareGet(index, type, id).execute().actionGet();
        if (response.isExists()) {
            final Map<String, Object> source = response.getSource();
            final T bean = BeanUtil.copyMapToNewBean(source, clazz, option -> {
                option.converter(new EsTimestampConverter(), timestampFields).excludeWhitespace();
                option.exclude(EsAccessResult.ACCESS_RESULT_DATA);
            });
            @SuppressWarnings("unchecked")
            final Map<String, Object> data = (Map<String, Object>) source.get(EsAccessResult.ACCESS_RESULT_DATA);
            if (data != null) {
                ((EsAccessResult) bean).setAccessResultData(new EsAccessResultData(data));
            }
            setId(bean, id);
            return bean;
        }
        return null;
    }

    protected <T> List<T> getList(final Class<T> clazz, final String sessionId, final QueryBuilder queryBuilder, final Integer from,
            final Integer size, final SortBuilder sortBuilder) {
        return getList(clazz, builder -> {
            if (StringUtil.isNotBlank(sessionId)) {
                final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(SESSION_ID, sessionId));
                if (queryBuilder != null) {
                    boolQuery.must(queryBuilder);
                }
                builder.setQuery(boolQuery);
            } else {
                if (queryBuilder != null) {
                    builder.setQuery(queryBuilder);
                } else {
                    builder.setQuery(QueryBuilders.matchAllQuery());
                }
            }
            if (sortBuilder != null) {
                builder.addSort(sortBuilder);
            }
            if (from != null) {
                builder.setFrom(from);
            }
            if (size != null) {
                builder.setSize(size);
            }
        });
    }

    protected <T> List<T> getList(final Class<T> clazz, final Consumer<SearchRequestBuilder> callback) {
        final EsResultList<T> targetList = new EsResultList<T>();
        final SearchRequestBuilder builder = getClient().prepareSearch(index).setTypes(type);
        callback.accept(builder);
        final SearchResponse response = builder.execute().actionGet();
        final SearchHits hits = response.getHits();
        targetList.setTotalHits(hits.getTotalHits());
        targetList.setTookInMillis(response.getTookInMillis());
        if (hits.getTotalHits() != 0) {
            try {
                for (final SearchHit searchHit : hits.getHits()) {
                    final Map<String, Object> source = searchHit.getSource();
                    final T target = BeanUtil.copyMapToNewBean(source, clazz, option -> {
                        option.converter(new EsTimestampConverter(), timestampFields).excludeWhitespace();
                        option.exclude(EsAccessResult.ACCESS_RESULT_DATA);
                    });
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> data = (Map<String, Object>) source.get(EsAccessResult.ACCESS_RESULT_DATA);
                    if (data != null) {
                        ((EsAccessResult) target).setAccessResultData(new EsAccessResultData(data));
                    }
                    setId(target, searchHit.getId());
                    targetList.add(target);
                }
            } catch (final Exception e) {
                throw new EsAccessException("response: " + response, e);
            }
        }
        return targetList;
    }

    protected boolean delete(final String sessionId, final String url) {
        final String id = getId(sessionId, url);
        final DeleteResponse response = getClient().prepareDelete(index, type, id).setConsistencyLevel(writeConsistencyLevel)
                .setRefresh(true).execute().actionGet();
        return response.isFound();
    }

    protected void deleteBySessionId(final String sessionId) {
        delete(builder -> builder.setQuery(QueryBuilders.termQuery(SESSION_ID, sessionId)));
    }

    public void deleteAll() {
        delete(builder -> builder.setQuery(QueryBuilders.matchAllQuery()));
    }

    public void delete(final Consumer<SearchRequestBuilder> callback) {
        final SearchRequestBuilder builder = getClient().prepareSearch(index).setTypes(type).setSearchType(SearchType.SCAN)
                .setScroll(new TimeValue(scrollTimeout)).setSize(scrollSize);
        callback.accept(builder);
        SearchResponse response = builder.execute().actionGet();

        while (true) {
            response =
                    getClient().prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(scrollTimeout)).execute().actionGet();

            final SearchHits searchHits = response.getHits();
            if (searchHits.hits().length == 0) {
                break;
            }

            final BulkRequestBuilder bulkBuilder = getClient().prepareBulk();
            for (final SearchHit searchHit : searchHits) {
                bulkBuilder.add(getClient().prepareDelete(index, type, searchHit.getId()));
            }

            final BulkResponse bulkResponse = bulkBuilder.execute().actionGet();
            if (bulkResponse.hasFailures()) {
                throw new EsAccessException(bulkResponse.buildFailureMessage());
            }
        }

        refresh();
    }

    private String getId(final String sessionId, final String url) {
        return sessionId + ID_SEPARATOR + new String(Base64.getUrlEncoder().withoutPadding().encode(url.getBytes(UTF_8)), UTF_8);
    }

    private String getUrl(final Object target) {
        final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(target.getClass());
        final PropertyDesc sessionIdProp = beanDesc.getPropertyDesc(URL);
        final Object sessionId = sessionIdProp.getValue(target);
        return sessionId == null ? null : sessionId.toString();
    }

    private String getSessionId(final Object target) {
        final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(target.getClass());
        final PropertyDesc sessionIdProp = beanDesc.getPropertyDesc(SESSION_ID);
        final Object sessionId = sessionIdProp.getValue(target);
        return sessionId == null ? null : sessionId.toString();
    }

    private void setId(final Object target, final String id) {
        final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(target.getClass());
        final PropertyDesc idProp = beanDesc.getPropertyDesc(ID);
        idProp.setValue(target, id);
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(final String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public int getScrollTimeout() {
        return scrollTimeout;
    }

    public void setScrollTimeout(final int scrollTimeout) {
        this.scrollTimeout = scrollTimeout;
    }

    public int getScrollSize() {
        return scrollSize;
    }

    public void setScrollSize(final int scrollSize) {
        this.scrollSize = scrollSize;
    }

    protected static class EsTimestampConverter implements Converter {

        @Override
        public String getAsString(final Object value) {
            if (value instanceof Date) {
                return XContentBuilder.defaultDatePrinter.print(((Date) value).getTime());
            }
            return null;
        }

        @Override
        public Object getAsObject(final String value) {
            if (StringUtil.isEmpty(value)) {
                return null;
            }
            return new Timestamp(XContentBuilder.defaultDatePrinter.parseMillis(value));
        }

        @Override
        public boolean isTarget(@SuppressWarnings("rawtypes") final Class clazz) {
            return clazz == Date.class;
        }

    }

    public int getBulkBufferSize() {
        return bulkBufferSize;
    }

    public void setBulkBufferSize(int bulkBufferSize) {
        this.bulkBufferSize = bulkBufferSize;
    }

    public WriteConsistencyLevel getWriteConsistencyLevel() {
        return writeConsistencyLevel;
    }

    public void setWriteConsistencyLevel(WriteConsistencyLevel writeConsistencyLevel) {
        this.writeConsistencyLevel = writeConsistencyLevel;
    }
}
