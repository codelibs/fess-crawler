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

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.EsUrlQueue;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.exception.EsAccessException;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsUrlQueueService extends AbstractCrawlerService implements UrlQueueService<EsUrlQueue> {
    private static final Logger logger = LoggerFactory.getLogger(EsUrlQueueService.class);

    @Resource
    protected EsDataService dataService;

    protected Queue<UrlQueue<String>> crawlingUrlQueue = new ConcurrentLinkedQueue<>();

    public int pollingFetchSize = 20;

    public int maxCrawlingQueueSize = 100;

    @PostConstruct
    public void init() {
        esClient.addOnConnectListener(() -> {
            createMapping("queue");
        });
    }

    @Override
    public void updateSessionId(final String oldSessionId, final String newSessionId) {
        SearchResponse response =
                getClient().prepareSearch(index).setTypes(type).setSearchType(SearchType.SCAN).setScroll(new TimeValue(scrollTimeout))
                        .setQuery(QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(SESSION_ID, oldSessionId))).setSize(scrollSize)
                        .execute().actionGet();
        while (true) {
            response =
                    getClient().prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(scrollTimeout)).execute().actionGet();

            final SearchHits searchHits = response.getHits();
            if (searchHits.hits().length == 0) {
                break;
            }

            final BulkRequestBuilder builder = getClient().prepareBulk();
            for (final SearchHit searchHit : searchHits) {
                final UpdateRequestBuilder updateRequest =
                        getClient().prepareUpdate(index, type, searchHit.getId()).setDoc(SESSION_ID, newSessionId);
                builder.add(updateRequest);
            }
            final BulkResponse bulkResponse = builder.execute().actionGet();
            if (bulkResponse.hasFailures()) {
                throw new EsAccessException(bulkResponse.buildFailureMessage());
            }
        }
    }

    @Override
    public void add(final String sessionId, final String url) {
        final EsUrlQueue urlQueue = new EsUrlQueue();
        urlQueue.setSessionId(sessionId);
        urlQueue.setUrl(url);
        urlQueue.setCreateTime(System.currentTimeMillis());
        urlQueue.setLastModified(0L);
        urlQueue.setDepth(0);
        urlQueue.setMethod(Constants.GET_METHOD);
        insert(urlQueue);
    }

    @Override
    public void insert(final EsUrlQueue urlQueue) {
        super.insert(urlQueue, urlQueue.getId() == null ? OpType.CREATE : OpType.INDEX);
    }

    @Override
    public void delete(final String sessionId) {
        deleteBySessionId(sessionId);
    }

    @Override
    public void offerAll(final String sessionId, final List<EsUrlQueue> urlQueueList) {
        if (logger.isDebugEnabled()) {
            logger.debug("Offering URL: Session ID: {}, UrlQueue: {}", sessionId, urlQueueList);
        }
        final List<UrlQueue<String>> targetList = new ArrayList<>(urlQueueList.size());
        for (final UrlQueue<String> urlQueue : urlQueueList) {
            if (!exists(sessionId, urlQueue.getUrl()) && !dataService.exists(sessionId, urlQueue.getUrl())) {
                urlQueue.setSessionId(sessionId);
                targetList.add(urlQueue);
            } else if (logger.isDebugEnabled()) {
                logger.debug("Existed URL: Session ID: {}, UrlQueue: {}", sessionId, urlQueue);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Offered URL: Session ID: {}, UrlQueue: {}", sessionId, targetList);
        }
        if (!targetList.isEmpty()) {
            insertAll(targetList.stream()
                    .filter(urlQueue -> StringUtil.isNotBlank(urlQueue.getSessionId()) && StringUtil.isNotBlank(urlQueue.getUrl()))
                    .collect(Collectors.toList()), OpType.CREATE);
        }
    }

    @Override
    public EsUrlQueue poll(final String sessionId) {
        final List<EsUrlQueue> urlQueueList =
                getList(EsUrlQueue.class, sessionId, null, 0, pollingFetchSize, SortBuilders.fieldSort(CREATE_TIME).order(SortOrder.ASC));
        if (urlQueueList.isEmpty()) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Queued URL: {}", urlQueueList);
        }
        for (final EsUrlQueue urlQueue : urlQueueList) {
            final String url = urlQueue.getUrl();
            if (crawlingUrlQueue.size() > maxCrawlingQueueSize) {
                crawlingUrlQueue.poll();
            }
            if (super.delete(sessionId, url)) {
                crawlingUrlQueue.add(urlQueue);
                return urlQueue;
            } else if (logger.isDebugEnabled()) {
                logger.debug("Already Deleted: {}", urlQueue);
            }
        }
        return null;
    }

    @Override
    public void saveSession(final String sessionId) {
        // TODO use cache
    }

    @Override
    public boolean visited(final EsUrlQueue urlQueue) {
        final String url = urlQueue.getUrl();
        if (StringUtil.isBlank(url)) {
            if (logger.isDebugEnabled()) {
                logger.debug("URL is a blank: " + url);
            }
            return false;
        }

        final String sessionId = urlQueue.getSessionId();
        if (super.exists(sessionId, url)) {
            return true;
        }

        final AccessResult<String> accessResult = dataService.getAccessResult(sessionId, url);
        if (accessResult != null) {
            return true;
        }

        return false;
    }

    @Override
    protected boolean exists(final String sessionId, final String url) {
        final boolean ret = super.exists(sessionId, url);
        if (!ret) {
            for (final UrlQueue<String> urlQueue : crawlingUrlQueue) {
                if (sessionId.equals(urlQueue.getSessionId()) && url.equals(urlQueue.getUrl())) {
                    return true;
                }
            }
        }
        return ret;
    }

    @Override
    public void generateUrlQueues(final String previousSessionId, final String sessionId) {
        dataService.iterate(previousSessionId, accessResult -> {
            final EsUrlQueue urlQueue = new EsUrlQueue();
            urlQueue.setSessionId(sessionId);
            urlQueue.setMethod(accessResult.getMethod());
            urlQueue.setUrl(accessResult.getUrl());
            urlQueue.setParentUrl(accessResult.getParentUrl());
            urlQueue.setDepth(0);
            urlQueue.setLastModified(accessResult.getLastModified());
            urlQueue.setCreateTime(System.currentTimeMillis());
            insert(urlQueue);
        });
    }

}
