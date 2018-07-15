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
import org.codelibs.core.security.MessageDigestUtil;
import org.codelibs.fess.crawler.client.EsClient;
import org.codelibs.fess.crawler.entity.EsAccessResult;
import org.codelibs.fess.crawler.entity.EsAccessResultData;
import org.codelibs.fess.crawler.exception.EsAccessException;
import org.codelibs.fess.crawler.util.EsResultList;
import org.elasticsearch.action.DocWriteRequest.OpType;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
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

    @Resource
    protected volatile EsClient esClient;

    protected String index;

    protected String type;

    protected int scrollTimeout = 60000;

    protected int scrollSize = 100;

    protected int bulkBufferSize = 10;

    protected int numberOfShards = 5;

    protected int numberOfReplicas = 1;

    protected int idPrefixLength = 445;

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
            final IndicesExistsResponse response = esClient.get(c -> c.admin().indices().prepareExists(index).execute());
            exists = response.isExists();
        } catch (final IndexNotFoundException e) {
            // ignore
        }
        if (!exists) {
            final CreateIndexResponse indexResponse =
                    esClient.get(c -> {
                        final String source;
                        if (numberOfReplicas > 0) {
                            source = "{\"settings\":{\"index\":{\"number_of_shards\":" + numberOfShards
                                    + ",\"number_of_replicas\":0,\"auto_expand_replicas\":\"0-" + numberOfReplicas + "\"}}}";
                        } else {
                            source = "{\"settings\":{\"index\":{\"number_of_shards\":" + numberOfShards + ",\"number_of_replicas\":"
                                    + numberOfReplicas + "}}}";
                        }
                        return c.admin().indices().prepareCreate(index).setSource(source, XContentType.JSON).execute();
                    });
            if (indexResponse.isAcknowledged()) {
                logger.info("Created " + index + " index.");
            } else if (logger.isDebugEnabled()) {
                logger.debug("Failed to create " + index + " index.");
            }
        }

        final GetMappingsResponse getMappingsResponse =
                esClient.get(c -> c.admin().indices().prepareGetMappings(index).setTypes(type).execute());
        final ImmutableOpenMap<String, MappingMetaData> indexMappings = getMappingsResponse.mappings().get(index);
        if (indexMappings == null || !indexMappings.containsKey(type)) {
            final PutMappingResponse putMappingResponse = esClient.get(c -> {
                final String source = FileUtil.readText("mapping/" + mappingName + ".json");
                return c.admin().indices().preparePutMapping(index).setType(type).setSource(source, XContentType.JSON)
                        .execute();
            });
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
            throw new EsAccessException("Failed to convert " + target + " to JSON.", e);
        }
    }

    protected RefreshResponse refresh() {
        try {
            return getClient().get(c -> c.admin().indices().prepareRefresh(index).execute());
        } catch (final Exception e) {
            throw new EsAccessException("Failed to refresh.", e);
        }
    }

    protected IndexResponse insert(final Object target, final OpType opType) {
        final String id = getId(getSessionId(target), getUrl(target));
        final XContentBuilder source = getXContentBuilder(target);
        try {
            final IndexResponse response = getClient().get(c -> c.prepareIndex(index, type, id).setSource(source).setOpType(opType)
                    .setRefreshPolicy(RefreshPolicy.IMMEDIATE).execute());
            setId(target, id);
            return response;
        } catch (final Exception e) {
            throw new EsAccessException("Failed to insert " + id, e);
        }
    }

    protected <T> void insertAll(final List<T> list, final OpType opType) {
        insertAll(list, opType, false);
    }

    protected <T> void insertAll(final List<T> list, final OpType opType, final boolean ignoreAlreadyExists) {
        final List<T> bufferedList = new ArrayList<>(bulkBufferSize);
        final StringBuilder failureBuf = new StringBuilder(100);
        list.stream().forEach(target -> {
            bufferedList.add(target);
            if (bufferedList.size() >= bulkBufferSize) {
                final BulkResponse response = doInsertAll(bufferedList, opType);
                if (response.hasFailures()) {
                    final String failureMessage = buildFailureMessage(response, ignoreAlreadyExists);
                    if (failureMessage.length() > 0) {
                        failureBuf.append(response.buildFailureMessage()).append('\n');
                    }
                }
                bufferedList.clear();
            }
        });
        if (!bufferedList.isEmpty()) {
            final BulkResponse response = doInsertAll(bufferedList, opType);
            if (response.hasFailures()) {
                final String failureMessage = buildFailureMessage(response, ignoreAlreadyExists);
                if (failureMessage.length() > 0) {
                    failureBuf.append(response.buildFailureMessage()).append('\n');
                }
            }
        }
        if (failureBuf.length() > 0) {
            throw new EsAccessException(failureBuf.toString());
        }
    }

    protected String buildFailureMessage(final BulkResponse bulkResponse, final boolean ignoreAlreadyExists) {
        final StringBuilder sb = new StringBuilder(100);
        final BulkItemResponse[] responses = bulkResponse.getItems();
        for (int i = 0; i < responses.length; i++) {
            final BulkItemResponse response = responses[i];
            if (response.isFailed()) {
                if (ignoreAlreadyExists) {
                    continue;
                }
                sb.append("\n[").append(i).append("]: index [").append(response.getIndex()).append("], type [").append(response.getType())
                        .append("], id [").append(response.getId()).append("], message [").append(response.getFailureMessage()).append("]");
            }
        }
        if (sb.length() > 0) {
            return "failure in bulk execution:" + sb.toString();
        }
        return StringUtil.EMPTY;
    }

    protected <T> BulkResponse doInsertAll(final List<T> list, final OpType opType) {
        try {
            return getClient().get(c -> {
                final BulkRequestBuilder bulkRequest = c.prepareBulk();
                for (final T target : list) {
                    final String id = getId(getSessionId(target), getUrl(target));
                    final XContentBuilder source = getXContentBuilder(target);
                    bulkRequest.add(c.prepareIndex(index, type, id).setSource(source).setOpType(opType));
                    setId(target, id);
                }

                return bulkRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE).execute();
            });
        } catch (final Exception e) {
            throw new EsAccessException("Failed to insert " + list, e);
        }
    }

    protected boolean exists(final String sessionId, final String url) {
        final String id = getId(sessionId, url);
        try {
            final SearchResponse response = getClient().get(c -> c.prepareSearch(index).setQuery(QueryBuilders.idsQuery(type).addIds(id))
                    .setSize(0).setTerminateAfter(1).execute());
            return response.getHits().getTotalHits() > 0;
        } catch (final Exception e) {
            throw new EsAccessException("Failed to check if " + sessionId + ":" + url + " exists.", e);
        }
    }

    public int getCount(final Consumer<SearchRequestBuilder> callback) {
        return (int) getClient().get(c -> {
            final SearchRequestBuilder builder = c.prepareSearch(index).setTypes(type).setSize(0);
            callback.accept(builder);
            return builder.execute();
        }).getHits().getTotalHits();
    }

    protected <T> T get(final Class<T> clazz, final String sessionId, final String url) {
        final String id = getId(sessionId, url);
        final GetResponse response = getClient().get(c -> c.prepareGet(index, type, id).execute());
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
            final Integer size, final SortBuilder<?> sortBuilder) {
        return getList(clazz, builder -> {
            if (StringUtil.isNotBlank(sessionId)) {
                if (queryBuilder instanceof BoolQueryBuilder) {
                    ((BoolQueryBuilder) queryBuilder).filter(QueryBuilders.termQuery(SESSION_ID, sessionId));
                } else {
                    final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(SESSION_ID, sessionId));
                    if (queryBuilder != null) {
                        boolQuery.must(queryBuilder);
                    }
                    builder.setQuery(boolQuery);
                }
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
        final SearchResponse response = getClient().get(c -> {
            final SearchRequestBuilder builder = c.prepareSearch(index).setTypes(type);
            callback.accept(builder);
            return builder.execute();
        });
        final EsResultList<T> targetList = new EsResultList<>();
        final SearchHits hits = response.getHits();
        targetList.setTotalHits(hits.getTotalHits());
        targetList.setTookInMillis(response.getTook().getMillis());
        if (hits.getTotalHits() != 0) {
            try {
                for (final SearchHit searchHit : hits.getHits()) {
                    final Map<String, Object> source = searchHit.getSourceAsMap();
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
        try {
            final DeleteResponse response =
                    getClient().get(c -> c.prepareDelete(index, type, id).setRefreshPolicy(RefreshPolicy.IMMEDIATE).execute());
            return response.getResult() == Result.DELETED;
        } catch (final Exception e) {
            throw new EsAccessException("Failed to delete " + sessionId + ":" + url, e);
        }
    }

    protected void deleteBySessionId(final String sessionId) {
        delete(builder -> builder.setQuery(QueryBuilders.termQuery(SESSION_ID, sessionId)));
    }

    public void deleteAll() {
        delete(builder -> builder.setQuery(QueryBuilders.matchAllQuery()));
    }

    public void delete(final Consumer<SearchRequestBuilder> callback) {
        SearchResponse response = null;
        while (true) {
            if (response == null) {
                response = getClient().get(c -> {
                    final SearchRequestBuilder builder =
                            c.prepareSearch(index).setTypes(type).setScroll(new TimeValue(scrollTimeout)).setSize(scrollSize);
                    callback.accept(builder);
                    return builder.execute();
                });
            } else {
                final String scrollId = response.getScrollId();
                response = getClient().get(c -> c.prepareSearchScroll(scrollId).setScroll(new TimeValue(scrollTimeout)).execute());
            }
            final SearchHits searchHits = response.getHits();
            if (searchHits.getHits().length == 0) {
                break;
            }

            final BulkResponse bulkResponse = getClient().get(c -> {
                final BulkRequestBuilder bulkBuilder = c.prepareBulk();
                for (final SearchHit searchHit : searchHits) {
                    bulkBuilder.add(c.prepareDelete(index, type, searchHit.getId()));
                }

                return bulkBuilder.execute();
            });
            if (bulkResponse.hasFailures()) {
                throw new EsAccessException(bulkResponse.buildFailureMessage());
            }
        }

        refresh();
    }

    private String getId(final String sessionId, final String url) {
        final String id = sessionId + ID_SEPARATOR + new String(Base64.getUrlEncoder().withoutPadding().encode(url.getBytes(UTF_8)), UTF_8);
        if (id.length() <= idPrefixLength) {
            return id;
        }
        return id.substring(0, idPrefixLength) + MessageDigestUtil.digest("SHA-256", id.substring(idPrefixLength));
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

    protected void setId(final Object target, final String id) {
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
        public static final DateTimeFormatter DEFAULT_DATE_PRINTER = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);

        @Override
        public String getAsString(final Object value) {
            if (value instanceof Date) {
                return DEFAULT_DATE_PRINTER.print(((Date) value).getTime());
            }
            return null;
        }

        @Override
        public Object getAsObject(final String value) {
            if (StringUtil.isEmpty(value)) {
                return null;
            }
            return new Timestamp(DEFAULT_DATE_PRINTER.parseMillis(value));
        }

        @Override
        public boolean isTarget(@SuppressWarnings("rawtypes") final Class clazz) {
            return clazz == Date.class;
        }

    }

    public int getBulkBufferSize() {
        return bulkBufferSize;
    }

    public void setBulkBufferSize(final int bulkBufferSize) {
        this.bulkBufferSize = bulkBufferSize;
    }

    public void setNumberOfShards(final int numberOfShards) {
        this.numberOfShards = numberOfShards;
    }

    public void setNumberOfReplicas(final int numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }

    public void setIdPrefixLength(final int idPrefixLength) {
        this.idPrefixLength = idPrefixLength;
    }

}
