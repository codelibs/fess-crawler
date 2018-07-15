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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.EsUrlQueue;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.exception.EsAccessException;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.codelibs.fess.crawler.util.EsCrawlerConfig;
import org.elasticsearch.action.DocWriteRequest.OpType;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
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

    protected Map<String, QueueHolder> sessionCache = new ConcurrentHashMap<>();

    protected int pollingFetchSize = 1000;

    protected int maxCrawlingQueueSize = 100;

    public EsUrlQueueService(final EsCrawlerConfig crawlerConfig) {
        this.index = crawlerConfig.getQueueIndex();
        this.type = "queue";
        setNumberOfShards(crawlerConfig.getQueueShards());
        setNumberOfReplicas(crawlerConfig.getQueueReplicas());
    }

    public EsUrlQueueService(final String name, final String type) {
        this.index = name + "." + type;
        this.type = type;
    }

    @PostConstruct
    public void init() {
        esClient.addOnConnectListener(() -> createMapping("queue"));
    }

    @PreDestroy
    public void destroy() {
        sessionCache.entrySet().stream().map(e -> e.getValue().waitingQueue).forEach(q -> q.forEach(urlQueue -> {
            try {
                insert(urlQueue);
            } catch (final Exception e) {
                logger.warn("Failed to restore " + urlQueue, e);
            }
        }));
    }

    public void clearCache() {
        sessionCache.clear();
    }

    @Override
    public void updateSessionId(final String oldSessionId, final String newSessionId) {
        SearchResponse response = null;
        while (true) {
            if (response == null) {
                response = getClient().get(c -> c.prepareSearch(index).setTypes(type).setScroll(new TimeValue(scrollTimeout))
                        .setQuery(QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(SESSION_ID, oldSessionId))).setSize(scrollSize)
                        .execute());
            } else {
                final String scrollId = response.getScrollId();
                response = getClient().get(c -> c.prepareSearchScroll(scrollId).setScroll(new TimeValue(scrollTimeout)).execute());
            }

            final SearchHits searchHits = response.getHits();
            if (searchHits.getHits().length == 0) {
                break;
            }

            final BulkResponse bulkResponse = getClient().get(c -> {
                final BulkRequestBuilder builder = c.prepareBulk();
                for (final SearchHit searchHit : searchHits) {
                    final UpdateRequestBuilder updateRequest =
                            c.prepareUpdate(index, type, searchHit.getId()).setDoc(SESSION_ID, newSessionId);
                    builder.add(updateRequest);
                }

                return builder.execute();
            });
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
        try {
            super.insert(urlQueue, urlQueue.getId() == null ? OpType.CREATE : OpType.INDEX);
        } catch (final EsAccessException e) {
            final Throwable cause = e.getCause();
            if (cause != null && cause.getClass().getSimpleName().equals("VersionConflictEngineException")) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to insert " + urlQueue, e);
                }
                return;
            }
            throw e;
        }
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
        if (!targetList.isEmpty()) {
            insertAll(targetList.stream()
                    .filter(urlQueue -> StringUtil.isNotBlank(urlQueue.getSessionId()) && StringUtil.isNotBlank(urlQueue.getUrl()))
                    .collect(Collectors.toList()), OpType.CREATE, true);
            if (logger.isDebugEnabled()) {
                logger.debug("Offered URL: Session ID: {}, UrlQueue: {}", sessionId, targetList);
            }
        }
    }

    @Override
    public EsUrlQueue poll(final String sessionId) {
        final QueueHolder queueHolder = getQueueHolder(sessionId);
        final Queue<EsUrlQueue> waitingQueue = queueHolder.waitingQueue;
        final Queue<EsUrlQueue> crawlingQueue = queueHolder.crawlingQueue;
        EsUrlQueue urlQueue = waitingQueue.poll();
        if (urlQueue != null) {
            if (crawlingQueue.size() > maxCrawlingQueueSize) {
                crawlingQueue.poll();
            }
            crawlingQueue.add(urlQueue);
            return urlQueue;
        }

        synchronized (queueHolder) {
            urlQueue = waitingQueue.poll();
            if (urlQueue == null) {
                final List<EsUrlQueue> urlQueueList = getList(EsUrlQueue.class, sessionId, null, 0, pollingFetchSize,
                        SortBuilders.fieldSort(CREATE_TIME).order(SortOrder.ASC));
                if (urlQueueList.isEmpty()) {
                    return null;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Queued URL: {}", urlQueueList);
                }
                waitingQueue.addAll(urlQueueList);

                if (!urlQueueList.isEmpty()) {
                    try {
                        // delete from es
                        final BulkResponse response = getClient().get(c -> {
                            final BulkRequestBuilder bulkBuilder = c.prepareBulk();
                            for (final EsUrlQueue uq : urlQueueList) {
                                bulkBuilder.add(c.prepareDelete(index, type, uq.getId()));
                            }

                            return bulkBuilder.setRefreshPolicy(RefreshPolicy.IMMEDIATE).execute();
                        });
                        if (response.hasFailures()) {
                            logger.warn(response.buildFailureMessage());
                        }
                    } catch (final Exception e) {
                        throw new EsAccessException("Failed to delete " + urlQueueList, e);
                    }
                }

                urlQueue = waitingQueue.poll();
                if (urlQueue == null) {
                    return null;
                }
            }

        }

        if (crawlingQueue.size() > maxCrawlingQueueSize) {
            crawlingQueue.poll();
        }
        crawlingQueue.add(urlQueue);
        return urlQueue;
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
        return accessResult != null;
    }

    @Override
    protected boolean exists(final String sessionId, final String url) {
        final boolean ret = super.exists(sessionId, url);
        if (!ret) {
            final QueueHolder queueHolder = getQueueHolder(sessionId);
            final Queue<EsUrlQueue> waitingQueue = queueHolder.waitingQueue;
            final Queue<EsUrlQueue> crawlingQueue = queueHolder.crawlingQueue;

            for (final UrlQueue<String> urlQueue : crawlingQueue) {
                if (sessionId.equals(urlQueue.getSessionId()) && url.equals(urlQueue.getUrl())) {
                    return true;
                }
            }
            for (final UrlQueue<String> urlQueue : waitingQueue) {
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

    protected QueueHolder getQueueHolder(final String sessionId) {
        QueueHolder queueHolder = sessionCache.get(sessionId);
        if (queueHolder == null) {
            queueHolder = new QueueHolder();
            final QueueHolder prevQueueHolder = sessionCache.putIfAbsent(sessionId, queueHolder);
            return prevQueueHolder == null ? queueHolder : prevQueueHolder;
        }
        return queueHolder;
    }

    protected static class QueueHolder {
        protected Queue<EsUrlQueue> waitingQueue = new ConcurrentLinkedQueue<>();

        protected Queue<EsUrlQueue> crawlingQueue = new ConcurrentLinkedQueue<>();
    }

    public void setPollingFetchSize(final int pollingFetchSize) {
        this.pollingFetchSize = pollingFetchSize;
    }

    public void setMaxCrawlingQueueSize(final int maxCrawlingQueueSize) {
        this.maxCrawlingQueueSize = maxCrawlingQueueSize;
    }
}
