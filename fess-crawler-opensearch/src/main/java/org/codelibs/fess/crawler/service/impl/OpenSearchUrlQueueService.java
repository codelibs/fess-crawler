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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.OpenSearchUrlQueue;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.exception.OpenSearchAccessException;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.codelibs.fess.crawler.util.OpenSearchCrawlerConfig;
import org.opensearch.action.DocWriteRequest.OpType;
import org.opensearch.action.bulk.BulkRequestBuilder;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.search.CreatePitRequest;
import org.opensearch.action.search.CreatePitResponse;
import org.opensearch.action.search.DeletePitRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.support.WriteRequest.RefreshPolicy;
import org.opensearch.action.update.UpdateRequestBuilder;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.core.action.ActionListener;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.opensearch.search.builder.PointInTimeBuilder;
import org.opensearch.search.sort.SortBuilders;
import org.opensearch.search.sort.SortOrder;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;

/**
 * OpenSearchUrlQueueService is an implementation of {@link UrlQueueService} for OpenSearch.
 *
 * @author shinsuke
 *
 */
public class OpenSearchUrlQueueService extends AbstractCrawlerService implements UrlQueueService<OpenSearchUrlQueue> {
    /**
     * Logger instance.
     */
    private static final Logger logger = LogManager.getLogger(OpenSearchUrlQueueService.class);

    /**
     * Data service for checking access results.
     */
    @Resource
    protected OpenSearchDataService dataService;

    /**
     * Cache for session queues.
     */
    protected Map<String, QueueHolder> sessionCache = new ConcurrentHashMap<>();

    /**
     * The number of URLs to fetch when polling.
     */
    protected int pollingFetchSize = 1000;

    /**
     * The maximum size of the crawling queue.
     */
    protected int maxCrawlingQueueSize = 100;

    /**
     * Creates a new instance of OpenSearchUrlQueueService.
     * @param crawlerConfig The crawler configuration.
     */
    public OpenSearchUrlQueueService(final OpenSearchCrawlerConfig crawlerConfig) {
        index = crawlerConfig.getQueueIndex();
        setNumberOfShards(crawlerConfig.getQueueShards());
        setNumberOfReplicas(crawlerConfig.getQueueReplicas());
    }

    /**
     * Creates a new instance of OpenSearchUrlQueueService.
     * @param name The name.
     * @param type The type.
     */
    public OpenSearchUrlQueueService(final String name, final String type) {
        index = name + "." + type;
    }

    /**
     * Initializes the service.
     */
    @PostConstruct
    public void init() {
        fesenClient.addOnConnectListener(() -> createMapping("queue"));
    }

    /**
     * Destroys the service.
     */
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

    /**
     * Clears the cache.
     */
    public void clearCache() {
        sessionCache.clear();
    }

    /**
     * Updates the session ID for all URL queue entries.
     *
     * @param oldSessionId The old session ID.
     * @param newSessionId The new session ID.
     */
    @Override
    public void updateSessionId(final String oldSessionId, final String newSessionId) {
        // Create PIT
        final CreatePitRequest createPitRequest = new CreatePitRequest(new TimeValue(scrollTimeout), true, index);
        final CreatePitResponse[] createPitResponseHolder = new CreatePitResponse[1];
        getClient().createPit(createPitRequest, new ActionListener<CreatePitResponse>() {
            @Override
            public void onResponse(CreatePitResponse response) {
                createPitResponseHolder[0] = response;
            }

            @Override
            public void onFailure(Exception e) {
                throw new OpenSearchAccessException("Failed to create PIT", e);
            }
        });

        // Wait for PIT creation (blocking call)
        int waitCount = 0;
        while (createPitResponseHolder[0] == null && waitCount < 100) {
            try {
                Thread.sleep(100);
                waitCount++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new OpenSearchAccessException("Interrupted while creating PIT", e);
            }
        }

        if (createPitResponseHolder[0] == null) {
            throw new OpenSearchAccessException("Failed to create PIT: timeout");
        }

        final String pitId = createPitResponseHolder[0].getId();

        try {
            Object[] searchAfter = null;
            while (true) {
                final Object[] currentSearchAfter = searchAfter;
                SearchResponse response = getClient().get(c -> {
                    final org.opensearch.action.search.SearchRequestBuilder builder = c.prepareSearch()
                            .setQuery(QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(SESSION_ID, oldSessionId)))
                            .setSize(scrollSize)
                            .addSort(SortBuilders.fieldSort("_id").order(SortOrder.ASC));

                    // Set PIT
                    builder.setPointInTime(new PointInTimeBuilder(pitId));

                    if (currentSearchAfter != null) {
                        builder.searchAfter(currentSearchAfter);
                    }

                    return builder.execute();
                });

                final SearchHits searchHits = response.getHits();
                if (searchHits.getHits().length == 0) {
                    break;
                }

                final BulkResponse bulkResponse = getClient().get(c -> {
                    final BulkRequestBuilder builder = c.prepareBulk();
                    for (final SearchHit searchHit : searchHits) {
                        final UpdateRequestBuilder updateRequest =
                                c.prepareUpdate().setIndex(index).setId(searchHit.getId()).setDoc(SESSION_ID, newSessionId);
                        builder.add(updateRequest);
                    }

                    return builder.execute();
                });
                if (bulkResponse.hasFailures()) {
                    throw new OpenSearchAccessException(bulkResponse.buildFailureMessage());
                }

                // Get the last hit's sort values for next iteration
                final SearchHit[] hits = searchHits.getHits();
                if (hits.length > 0) {
                    searchAfter = hits[hits.length - 1].getSortValues();
                } else {
                    break;
                }
            }
        } finally {
            // Delete PIT
            final DeletePitRequest deletePitRequest = new DeletePitRequest(pitId);
            getClient().deletePits(deletePitRequest, new ActionListener<org.opensearch.action.search.DeletePitResponse>() {
                @Override
                public void onResponse(org.opensearch.action.search.DeletePitResponse response) {
                    // PIT deleted successfully
                }

                @Override
                public void onFailure(Exception e) {
                    logger.warn("Failed to delete PIT: " + pitId, e);
                }
            });
        }
    }

    /**
     * Adds a URL to the queue for the specified session.
     *
     * @param sessionId The session ID.
     * @param url The URL to add.
     */
    @Override
    public void add(final String sessionId, final String url) {
        if (exists(sessionId, url)) {
            return;
        }
        final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
        urlQueue.setSessionId(sessionId);
        urlQueue.setUrl(url);
        urlQueue.setCreateTime(System.currentTimeMillis());
        urlQueue.setLastModified(0L);
        urlQueue.setDepth(0);
        urlQueue.setMethod(Constants.GET_METHOD);
        insert(urlQueue);
    }

    /**
     * Inserts a URL queue entry into the OpenSearch index.
     *
     * @param urlQueue The URL queue entry to insert.
     */
    @Override
    public void insert(final OpenSearchUrlQueue urlQueue) {
        try {
            super.insert(urlQueue, urlQueue.getId() == null ? OpType.CREATE : OpType.INDEX);
        } catch (final OpenSearchAccessException e) {
            final Throwable cause = e.getCause();
            if (cause != null && "VersionConflictEngineException".equals(cause.getClass().getSimpleName())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to insert {}", urlQueue, e);
                }
                return;
            }
            throw e;
        }
    }

    /**
     * Deletes all URL queue entries for the specified session.
     *
     * @param sessionId The session ID.
     */
    @Override
    public void delete(final String sessionId) {
        deleteBySessionId(sessionId);
    }

    /**
     * Offers multiple URL queue entries for the specified session.
     * Only URLs that don't already exist will be added.
     *
     * @param sessionId The session ID.
     * @param urlQueueList The list of URL queue entries to offer.
     */
    @Override
    public void offerAll(final String sessionId, final List<OpenSearchUrlQueue> urlQueueList) {
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

    /**
     * Polls the next URL queue entry for the specified session.
     * This method manages local caches and fetches from OpenSearch when needed.
     *
     * @param sessionId The session ID.
     * @return The next URL queue entry, or null if none available.
     */
    @Override
    public OpenSearchUrlQueue poll(final String sessionId) {
        final QueueHolder queueHolder = getQueueHolder(sessionId);
        final Queue<OpenSearchUrlQueue> waitingQueue = queueHolder.waitingQueue;
        final Queue<OpenSearchUrlQueue> crawlingQueue = queueHolder.crawlingQueue;
        OpenSearchUrlQueue urlQueue = waitingQueue.poll();
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
                final List<OpenSearchUrlQueue> urlQueueList = fetchUrlQueueList(sessionId);
                if (urlQueueList.isEmpty()) {
                    return null;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Queued URL: {}", urlQueueList);
                }

                if (!urlQueueList.isEmpty()) {
                    try {
                        // delete from es
                        final BulkResponse response = getClient().get(c -> {
                            final BulkRequestBuilder bulkBuilder = c.prepareBulk();
                            for (final OpenSearchUrlQueue uq : urlQueueList) {
                                bulkBuilder.add(c.prepareDelete().setIndex(index).setId(uq.getId()));
                            }

                            return bulkBuilder.setRefreshPolicy(RefreshPolicy.IMMEDIATE).execute();
                        });
                        if (response.hasFailures()) {
                            logger.warn(response.buildFailureMessage());
                        }
                    } catch (final Exception e) {
                        throw new OpenSearchAccessException("Failed to delete " + urlQueueList, e);
                    }
                }

                waitingQueue.addAll(urlQueueList);

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

    /**
     * Fetches a list of URL queues for a given session ID.
     * @param sessionId The session ID.
     * @return A list of OpenSearchUrlQueue objects.
     */
    protected List<OpenSearchUrlQueue> fetchUrlQueueList(final String sessionId) {
        return getList(OpenSearchUrlQueue.class, sessionId, null, 0, pollingFetchSize,
                SortBuilders.fieldSort(OpenSearchUrlQueue.WEIGHT).order(SortOrder.DESC),
                SortBuilders.fieldSort(CREATE_TIME).order(SortOrder.ASC));
    }

    /**
     * Saves the session state (currently not implemented).
     *
     * @param sessionId The session ID.
     */
    @Override
    public void saveSession(final String sessionId) {
        // TODO use cache
    }

    /**
     * Checks if a URL has been visited by looking in both the queue and access results.
     *
     * @param urlQueue The URL queue entry to check.
     * @return true if the URL has been visited, false otherwise.
     */
    @Override
    public boolean visited(final OpenSearchUrlQueue urlQueue) {
        final String url = urlQueue.getUrl();
        if (StringUtil.isBlank(url)) {
            if (logger.isDebugEnabled()) {
                logger.debug("URL is a blank: {}", url);
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

    /**
     * Checks if a URL exists in the queue by searching OpenSearch and local caches.
     *
     * @param sessionId The session ID.
     * @param url The URL to check.
     * @return true if the URL exists in the queue, false otherwise.
     */
    @Override
    protected boolean exists(final String sessionId, final String url) {
        final boolean ret = super.exists(sessionId, url);
        if (!ret) {
            final QueueHolder queueHolder = getQueueHolder(sessionId);
            final Queue<OpenSearchUrlQueue> waitingQueue = queueHolder.waitingQueue;
            final Queue<OpenSearchUrlQueue> crawlingQueue = queueHolder.crawlingQueue;

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

    /**
     * Generates URL queue entries for a new session based on access results from a previous session.
     *
     * @param previousSessionId The previous session ID.
     * @param sessionId The new session ID.
     */
    @Override
    public void generateUrlQueues(final String previousSessionId, final String sessionId) {
        dataService.iterate(previousSessionId, accessResult -> {
            final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
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

    /**
     * Returns the QueueHolder for a given session ID.
     * @param sessionId The session ID.
     * @return The QueueHolder.
     */
    protected QueueHolder getQueueHolder(final String sessionId) {
        QueueHolder queueHolder = sessionCache.get(sessionId);
        if (queueHolder == null) {
            queueHolder = new QueueHolder();
            final QueueHolder prevQueueHolder = sessionCache.putIfAbsent(sessionId, queueHolder);
            return prevQueueHolder == null ? queueHolder : prevQueueHolder;
        }
        return queueHolder;
    }

    /**
     * QueueHolder holds the waiting and crawling queues.
     */
    protected static class QueueHolder {
        /**
         * Constructs a new QueueHolder.
         */
        public QueueHolder() {
            // Default constructor
        }

        /**
         * The queue for URLs waiting to be crawled.
         */
        protected Queue<OpenSearchUrlQueue> waitingQueue = new ConcurrentLinkedQueue<>();

        /**
         * The queue for URLs currently being crawled.
         */
        protected Queue<OpenSearchUrlQueue> crawlingQueue = new ConcurrentLinkedQueue<>();
    }

    /**
     * Sets the polling fetch size.
     * @param pollingFetchSize The polling fetch size.
     */
    public void setPollingFetchSize(final int pollingFetchSize) {
        this.pollingFetchSize = pollingFetchSize;
    }

    /**
     * Sets the maximum crawling queue size.
     * @param maxCrawlingQueueSize The maximum crawling queue size.
     */
    public void setMaxCrawlingQueueSize(final int maxCrawlingQueueSize) {
        this.maxCrawlingQueueSize = maxCrawlingQueueSize;
    }
}
