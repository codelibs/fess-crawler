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
package org.codelibs.fess.crawler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.SystemUtil;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.CrawlerClient;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.HostState;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.RobotsTxtDisallowedException;
import org.codelibs.fess.crawler.exception.RobotsTxtUnavailableException;
import org.codelibs.fess.crawler.helper.LogHelper;
import org.codelibs.fess.crawler.helper.RobotsTxtHelper;
import org.codelibs.fess.crawler.interval.IntervalController;
import org.codelibs.fess.crawler.log.LogType;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.codelibs.fess.crawler.rule.Rule;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;
import org.codelibs.fess.crawler.weight.UrlQueueWeigher;

import jakarta.annotation.Resource;

/**
 * The {@code CrawlerThread} class represents a thread that executes the crawling process.
 * It is responsible for fetching URLs from the queue, accessing the content,
 * processing the response, and extracting child URLs.
 *
 * <p>
 * This class implements the {@link Runnable} interface, allowing it to be executed in a separate thread.
 * It uses various services and components, such as {@link UrlQueueService}, {@link DataService},
 * {@link CrawlerContainer}, {@link LogHelper}, {@link CrawlerClientFactory}, and {@link CrawlerContext},
 * to perform its tasks.
 * </p>
 *
 * <p>
 * The crawling process involves the following steps:
 * </p>
 * <ol>
 *   <li>Fetching a URL from the queue using {@link UrlQueueService#poll(String)}.</li>
 *   <li>Checking if the URL is valid using {@link #isValid(UrlQueue)}.</li>
 *   <li>Accessing the content using a {@link CrawlerClient} obtained from {@link CrawlerClientFactory}.</li>
 *   <li>Processing the response using a {@link ResponseProcessor} associated with a {@link Rule}.</li>
 *   <li>Extracting child URLs and adding them to the queue using {@link #storeChildUrls(Set, String, int)}
 *       or {@link #storeChildUrl(String, String, float, int)}.</li>
 *   <li>Handling exceptions that may occur during the crawling process.</li>
 * </ol>
 *
 * <p>
 * The thread also manages the active thread count using atomic operations
 * and provides methods for logging messages using {@link LogHelper}.
 * </p>
 *
 * <p>
 * The crawling process continues until the crawler status is {@link CrawlerStatus#DONE} or the
 * {@link #isContinue(int)} method returns {@code false}.
 * </p>
 *
 */
public class CrawlerThread implements Runnable {
    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(CrawlerThread.class);

    /**
     * Constructs a new CrawlerThread.
     */
    public CrawlerThread() {
        // Default constructor
    }

    /**
     * Service for managing URL queues during crawling.
     */
    @Resource
    protected UrlQueueService<UrlQueue<?>> urlQueueService;

    /**
     * Weigher applied to child URLs before they are queued.
     */
    @Resource
    protected UrlQueueWeigher urlQueueWeigher;

    /**
     * Service for managing access result data.
     */
    @Resource
    protected DataService<AccessResult<?>> dataService;

    /**
     * Container for managing crawler components.
     */
    @Resource
    protected CrawlerContainer crawlerContainer;

    /**
     * Helper for logging crawler activities.
     */
    @Resource
    protected LogHelper logHelper;

    /**
     * Factory for creating crawler clients.
     */
    protected CrawlerClientFactory clientFactory;

    /**
     * Context object containing crawler state and configuration.
     */
    protected CrawlerContext crawlerContext;

    /**
     * Flag indicating whether to wait on folder operations.
     */
    protected boolean noWaitOnFolder = false;

    /**
     * Increments the active thread count using atomic operation.
     */
    protected void startCrawling() {
        crawlerContext.incrementAndGetActiveThreadCount();
    }

    /**
     * Decrements the active thread count using atomic operation.
     */
    protected void finishCrawling() {
        crawlerContext.decrementAndGetActiveThreadCount();
    }

    /**
     * Checks if the crawling process should continue.
     * @param tcCount The thread check count.
     * @return true if the crawling should continue, false otherwise.
     */
    protected boolean isContinue(final int tcCount) {
        if (!crawlerContainer.available()) {
            // system shutdown
            return false;
        }

        boolean isContinue = false;
        if (tcCount < crawlerContext.maxThreadCheckCount) {
            final long maxAccessCount = crawlerContext.getMaxAccessCount();
            if (maxAccessCount > 0 && crawlerContext.getAccessCount() >= maxAccessCount) {
                return false;
            }
            isContinue = true;
        }

        if (!isContinue && crawlerContext.getActiveThreadCount() > 0) {
            // still running..
            return true;
        }

        return isContinue;
    }

    /**
     * Logs a message using the provided LogHelper.
     * @param logHelper The LogHelper instance.
     * @param key The LogType key.
     * @param objs The objects to log.
     */
    protected void log(final LogHelper logHelper, final LogType key, final Object... objs) {
        if (logHelper != null) {
            logHelper.log(key, objs);
        }
    }

    /**
     * Runs the crawling process in a separate thread.
     * This method fetches URLs from the queue, accesses content, processes responses,
     * and extracts child URLs until the crawling process is done or no more URLs are available.
     */
    @Override
    public void run() {
        log(logHelper, LogType.START_THREAD, crawlerContext);
        int threadCheckCount = 0;
        // set urlQueue to thread
        CrawlingParameterUtil.setCrawlerContext(crawlerContext);
        CrawlingParameterUtil.setUrlQueueService(urlQueueService);
        CrawlingParameterUtil.setDataService(dataService);
        try {
            while (crawlerContext.getStatus() != CrawlerStatus.DONE && isContinue(threadCheckCount)) {
                final UrlQueue<?> urlQueue = urlQueueService.poll(crawlerContext.sessionId);
                if (urlQueue != null && isDisallowedByRobotsTxt(urlQueue.getUrl())) {
                    // a URL was dequeued, so this is not an empty poll; no request is made either
                    logDisallowedByRobotsTxt(urlQueue.getUrl());
                    continue;
                }
                if (isValid(urlQueue)) {
                    ResponseData responseData = null;
                    log(logHelper, LogType.START_CRAWLING, crawlerContext, urlQueue);
                    try {
                        final CrawlerClient client = getClient(urlQueue.getUrl());
                        if (client == null) {
                            log(logHelper, LogType.UNSUPPORTED_URL_AT_CRAWLING_STARTED, crawlerContext, urlQueue);
                            continue;
                        }

                        startCrawling();

                        // set urlQueue to thread
                        CrawlingParameterUtil.setUrlQueue(urlQueue);

                        if (crawlerContext.intervalController != null) {
                            crawlerContext.intervalController.delay(IntervalController.PRE_PROCESSING);
                        }

                        final boolean contentUpdated = isContentUpdated(client, urlQueue);

                        if (contentUpdated) {
                            log(logHelper, LogType.GET_CONTENT, crawlerContext, urlQueue);
                            // access an url
                            final long startTime = SystemUtil.currentTimeMillis();
                            responseData = client.execute(RequestDataBuilder.newRequestData()
                                    .method(urlQueue.getMethod())
                                    .url(urlQueue.getUrl())
                                    .weight(urlQueue.getWeight())
                                    .build());
                            responseData.setExecutionTime(SystemUtil.currentTimeMillis() - startTime);
                            responseData.setParentUrl(urlQueue.getParentUrl());
                            responseData.setSessionId(crawlerContext.sessionId);

                            boolean requeued = false;
                            final int httpStatusCode = responseData.getHttpStatusCode();
                            if (isRetryableStatus(httpStatusCode)) {
                                final long retryAfterMillis =
                                        RobotsTxtHelper.parseRetryAfter(getRetryAfter(responseData), SystemUtil.currentTimeMillis());
                                requeued = handleRetryableFailure(urlQueue, retryAfterMillis, "HTTP " + httpStatusCode, true);
                            } else if (httpStatusCode > 0) {
                                final HostState hostState = crawlerContext.peekHostState(urlQueue.getUrl());
                                if (hostState != null) {
                                    hostState.recordSuccess();
                                }
                            }

                            if (requeued) {
                                // retried later; the response is not processed
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Skipped processing the response of {} until it is retried.", urlQueue.getUrl());
                                }
                            } else if (responseData.getRedirectLocation() == null) {
                                log(logHelper, LogType.PROCESS_RESPONSE, crawlerContext, urlQueue, responseData);
                                processResponse(urlQueue, responseData);
                            } else {
                                log(logHelper, LogType.REDIRECT_LOCATION, crawlerContext, urlQueue, responseData);
                                // redirect
                                storeChildUrl(responseData.getRedirectLocation(), urlQueue.getUrl(), urlQueue.getWeight(),
                                        urlQueue.getDepth() == null ? 1 : urlQueue.getDepth() + 1);
                            }
                        }

                        log(logHelper, LogType.FINISHED_CRAWLING, crawlerContext, urlQueue);
                    } catch (final ChildUrlsException e) {
                        try {
                            final Set<RequestData> childUrlSet = e.getChildUrlList();
                            log(logHelper, LogType.PROCESS_CHILD_URLS_BY_EXCEPTION, crawlerContext, urlQueue, childUrlSet);
                            // add an url
                            storeChildUrls(childUrlSet, urlQueue.getUrl(), urlQueue.getDepth() == null ? 1 : urlQueue.getDepth() + 1);
                        } catch (final Exception e1) {
                            log(logHelper, LogType.CRAWLING_EXCEPTION, crawlerContext, urlQueue, e1);
                        }
                        if (noWaitOnFolder) {
                            continue;
                        }
                    } catch (final RobotsTxtDisallowedException e) {
                        logDisallowedByRobotsTxt(urlQueue.getUrl());
                    } catch (final RobotsTxtUnavailableException e) {
                        try {
                            if (e.isFetchAttempted()) {
                                // checkRobotsTxt has already recorded the backoff of the origin
                                handleRetryableFailure(urlQueue, e.getRetryAfterMillis(), "robots.txt unavailable", false);
                            } else {
                                // robots.txt was not requested because the backoff of the origin has not ended:
                                // the URL has not been tried, so this does not use up one of its retries
                                requeue(urlQueue);
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Re-queued {} until the robots.txt backoff of the host ends.", urlQueue.getUrl());
                                }
                            }
                        } catch (final Exception e1) {
                            log(logHelper, LogType.CRAWLING_EXCEPTION, crawlerContext, urlQueue, e1);
                        }
                    } catch (final CrawlingAccessException e) {
                        log(logHelper, LogType.CRAWLING_ACCESS_EXCEPTION, crawlerContext, urlQueue, e);
                    } catch (final Throwable e) {
                        log(logHelper, LogType.CRAWLING_EXCEPTION, crawlerContext, urlQueue, e);
                    } finally {
                        try {
                            addSitemapsFromRobotsTxt(urlQueue);

                            if (responseData != null) {
                                CloseableUtil.closeQuietly(responseData);
                            }
                            if (crawlerContext.intervalController != null) {
                                crawlerContext.intervalController.delay(IntervalController.POST_PROCESSING);
                            }
                            threadCheckCount = 0; // clear
                            // remove urlQueue from thread
                            CrawlingParameterUtil.setUrlQueue(null);
                            finishCrawling();
                        } finally {
                            log(logHelper, LogType.CLEANUP_CRAWLING, crawlerContext, urlQueue);
                        }
                    }
                } else {
                    log(logHelper, LogType.NO_URL_IN_QUEUE, crawlerContext, urlQueue, Integer.valueOf(threadCheckCount));

                    if (crawlerContext.intervalController != null) {
                        crawlerContext.intervalController.delay(IntervalController.NO_URL_IN_QUEUE);
                    }

                    threadCheckCount++;
                }

                // interval
                if (crawlerContext.intervalController != null) {
                    crawlerContext.intervalController.delay(IntervalController.WAIT_NEW_URL);
                }
            }
        } catch (final Throwable t) {
            log(logHelper, LogType.SYSTEM_ERROR, t);
        } finally {
            // remove crawlerContext from thread
            CrawlingParameterUtil.setCrawlerContext(null);
            CrawlingParameterUtil.setUrlQueueService(null);
            CrawlingParameterUtil.setDataService(null);
        }
        log(logHelper, LogType.FINISHED_THREAD, crawlerContext);
    }

    /**
     * Adds sitemaps from robots.txt to the crawling queue.
     * @param urlQueue The URL queue to add sitemaps to.
     */
    protected void addSitemapsFromRobotsTxt(final UrlQueue<?> urlQueue) {
        final String[] sitemaps = crawlerContext.removeSitemaps();
        if (sitemaps != null) {
            for (final String childUrl : sitemaps) {
                try {
                    storeChildUrl(childUrl, urlQueue.getUrl(), urlQueue.getWeight(),
                            urlQueue.getDepth() == null ? 1 : urlQueue.getDepth() + 1);
                } catch (final Exception e) {
                    log(logHelper, LogType.PROCESS_CHILD_URL_BY_EXCEPTION, crawlerContext, urlQueue, childUrl, e);
                }
            }
        }
    }

    /**
     * Gets the appropriate crawler client for the given URL.
     * @param url The URL to get a client for.
     * @return The crawler client.
     */
    protected CrawlerClient getClient(final String url) {
        return clientFactory.getClient(url);
    }

    /**
     * Checks if the content has been updated since the last crawl.
     * @param client The crawler client.
     * @param urlQueue The URL queue entry.
     * @return true if content is updated, false otherwise.
     */
    protected boolean isContentUpdated(final CrawlerClient client, final UrlQueue<?> urlQueue) {
        if (urlQueue.getLastModified() != null) {
            log(logHelper, LogType.CHECK_LAST_MODIFIED, crawlerContext, urlQueue);
            final long startTime = SystemUtil.currentTimeMillis();
            ResponseData responseData = null;
            try {
                // head method
                responseData = client
                        .execute(RequestDataBuilder.newRequestData().head().url(urlQueue.getUrl()).weight(urlQueue.getWeight()).build());
                if (responseData != null && responseData.getLastModified() != null
                        && responseData.getLastModified().getTime() <= urlQueue.getLastModified().longValue()
                        && responseData.getHttpStatusCode() == 200) {
                    log(logHelper, LogType.NOT_MODIFIED, crawlerContext, urlQueue);

                    responseData.setExecutionTime(SystemUtil.currentTimeMillis() - startTime);
                    responseData.setParentUrl(urlQueue.getParentUrl());
                    responseData.setSessionId(crawlerContext.sessionId);
                    responseData.setStatus(Constants.NOT_MODIFIED_STATUS);
                    responseData.setHttpStatusCode(Constants.NOT_MODIFIED_STATUS_CODE);
                    processResponse(urlQueue, responseData);

                    return false;
                }
            } finally {
                if (responseData != null) {
                    CloseableUtil.closeQuietly(responseData);
                }
            }
        }
        return true;
    }

    /**
     * Processes the response data using the appropriate rule processor.
     * @param urlQueue The URL queue entry.
     * @param responseData The response data to process.
     */
    protected void processResponse(final UrlQueue<?> urlQueue, final ResponseData responseData) {
        // get a rule
        final Rule rule = crawlerContext.ruleManager.getRule(responseData);
        if (rule == null) {
            log(logHelper, LogType.NO_RULE, crawlerContext, urlQueue, responseData);
        } else {
            responseData.setRuleId(rule.getRuleId());
            final ResponseProcessor responseProcessor = rule.getResponseProcessor();
            if (responseProcessor == null) {
                log(logHelper, LogType.NO_RESPONSE_PROCESSOR, crawlerContext, urlQueue, responseData, rule);
            } else {
                responseProcessor.process(responseData);
            }
        }

    }

    /**
     * Stores child URLs to the crawling queue.
     * @param childUrlList The set of child URLs to store.
     * @param url The parent URL.
     * @param depth The depth of the child URLs.
     */
    protected void storeChildUrls(final Set<RequestData> childUrlList, final String url, final int depth) {
        if (crawlerContext.getMaxDepth() >= 0 && depth > crawlerContext.getMaxDepth()) {
            return;
        }

        // add url and filter
        final Set<String> urlSet = HashSet.newHashSet(childUrlList.size());
        final List<UrlQueue<?>> childList = new ArrayList<>(childUrlList.size());
        for (final RequestData d : childUrlList) {
            final String childUrl = d.getUrl();
            if (StringUtil.isBlank(childUrl) || !urlSet.add(childUrl) || !crawlerContext.urlFilter.match(childUrl)
                    || isDisallowedByRobotsTxt(childUrl)) {
                continue;
            }
            final UrlQueue<?> uq = crawlerContainer.getComponent("urlQueue");
            uq.setCreateTime(SystemUtil.currentTimeMillis());
            uq.setDepth(depth);
            uq.setMethod(Constants.GET_METHOD);
            uq.setParentUrl(url);
            uq.setSessionId(crawlerContext.sessionId);
            uq.setUrl(childUrl);
            uq.setWeight(d.getWeight());
            childList.add(uq);
        }
        offerChildUrls(childList);
    }

    /**
     * Stores a single child URL to the crawling queue.
     * @param childUrl The child URL to store.
     * @param parentUrl The parent URL.
     * @param weight The weight of the child URL.
     * @param depth The depth of the child URL.
     */
    protected void storeChildUrl(final String childUrl, final String parentUrl, final float weight, final int depth) {
        if (crawlerContext.getMaxDepth() >= 0 && depth > crawlerContext.getMaxDepth()) {
            return;
        }

        // add url and filter
        if (StringUtil.isNotBlank(childUrl) && crawlerContext.urlFilter.match(childUrl) && !isDisallowedByRobotsTxt(childUrl)) {
            final List<UrlQueue<?>> childList = new ArrayList<>(1);
            final UrlQueue<?> uq = crawlerContainer.getComponent("urlQueue");
            uq.setCreateTime(SystemUtil.currentTimeMillis());
            uq.setDepth(depth);
            uq.setMethod(Constants.GET_METHOD);
            uq.setParentUrl(parentUrl);
            uq.setSessionId(crawlerContext.sessionId);
            uq.setUrl(childUrl);
            uq.setWeight(weight);
            childList.add(uq);
            offerChildUrls(childList);
        }
    }

    /**
     * Applies the weigher to the child URLs and adds them to the queue.
     * @param childList The child URLs to queue.
     */
    protected void offerChildUrls(final List<UrlQueue<?>> childList) {
        if (childList.isEmpty()) {
            return;
        }
        try {
            urlQueueWeigher.apply(crawlerContext.sessionId, childList);
        } catch (final Exception e) {
            logger.warn("Failed to apply weigher {} to {} child URL(s). Queueing them with whatever weights it left behind.",
                    urlQueueWeigher.getClass().getName(), childList.size());
            if (logger.isDebugEnabled()) {
                logger.debug("Weigher {} failed.", urlQueueWeigher.getClass().getName(), e);
            }
        }
        urlQueueService.offerAll(crawlerContext.sessionId, childList);
    }

    /**
     * Validates whether the URL queue entry is valid for crawling.
     * @param urlQueue The URL queue entry to validate.
     * @return true if valid, false otherwise.
     */
    protected boolean isValid(final UrlQueue<?> urlQueue) {
        if (urlQueue == null || StringUtil.isBlank(urlQueue.getUrl())
                || crawlerContext.getMaxDepth() >= 0 && urlQueue.getDepth() > crawlerContext.getMaxDepth()) {
            return false;
        }

        // url filter
        if (!crawlerContext.urlFilter.match(urlQueue.getUrl())) {
            return false;
        }

        return !isDisallowedByRobotsTxt(urlQueue.getUrl());
    }

    /**
     * Returns whether the robots.txt already resolved for the origin of a URL disallows it.
     * A URL whose origin has no state yet is not disallowed; no state is created for it.
     *
     * @param url the URL
     * @return true if the URL must not be crawled
     */
    protected boolean isDisallowedByRobotsTxt(final String url) {
        final HostState hostState = crawlerContext.peekHostState(url);
        return hostState != null && !hostState.isAllowedByRobotsTxt(url);
    }

    /**
     * Logs a URL that robots.txt does not allow, telling a Disallow rule from an origin whose robots.txt
     * stayed unavailable and was given up ({@link HostState.RobotsTxtStatus#DISALLOW_ALL}).
     *
     * @param url the URL
     */
    protected void logDisallowedByRobotsTxt(final String url) {
        final HostState hostState = crawlerContext.peekHostState(url);
        if (hostState != null && hostState.getRobotsTxtStatus() == HostState.RobotsTxtStatus.DISALLOW_ALL) {
            logger.info("Disallowed by robots.txt (unavailable, given up): {}", url);
        } else {
            logger.info("Disallowed by robots.txt: {}", url);
        }
    }

    /**
     * Returns whether an HTTP status asks the crawler to come back later: 429 (Too Many Requests)
     * and 503 (Service Unavailable).
     *
     * @param httpStatusCode the HTTP status code
     * @return true if the URL should be retried after a backoff
     */
    protected boolean isRetryableStatus(final int httpStatusCode) {
        return httpStatusCode == 429 || httpStatusCode == 503;
    }

    /**
     * Returns the Retry-After header of a response. The header name is matched case-insensitively.
     *
     * @param responseData the response
     * @return the header value, or null if absent
     */
    protected String getRetryAfter(final ResponseData responseData) {
        for (final Map.Entry<String, Object> entry : responseData.getMetaDataMap().entrySet()) {
            if ("Retry-After".equalsIgnoreCase(entry.getKey()) && entry.getValue() != null) {
                return entry.getValue().toString();
            }
        }
        return null;
    }

    /**
     * Handles a URL that must be retried later (429, 503 or an unavailable robots.txt): optionally extends the
     * backoff of its origin, then re-queues a copy of the entry unless the URL has used up its retries.
     * This method does not wait; the interval controller waits for the backoff before the next access.
     *
     * @param urlQueue the URL queue entry
     * @param retryAfterMillis the wait requested by the server in milliseconds, 0 or less if none
     * @param reason the failure for log messages, such as "HTTP 503"
     * @param recordHostFailure true to record the failure on the origin; false when it has already been recorded
     * @return true if the URL was re-queued, false if its retries are exhausted
     */
    protected boolean handleRetryableFailure(final UrlQueue<?> urlQueue, final long retryAfterMillis, final String reason,
            final boolean recordHostFailure) {
        final String url = urlQueue.getUrl();
        final long now = SystemUtil.currentTimeMillis();
        long wait = 0L;
        final HostState hostState = recordHostFailure ? crawlerContext.getHostState(url) : crawlerContext.peekHostState(url);
        if (hostState != null) {
            if (recordHostFailure) {
                wait = hostState.recordFailure(now, retryAfterMillis, crawlerContext.getBackoffBaseMillis(),
                        crawlerContext.getMaxBackoffMillis());
            } else {
                wait = Math.max(0L, hostState.getBackoffUntil() - now);
            }
        }

        final int retryCount = crawlerContext.incrementAndGetRetryCount(url);
        final int maxRetryCount = crawlerContext.getMaxRetryCount();
        if (retryCount > maxRetryCount) {
            logger.warn("Gave up retrying {} ({}) after {} attempt(s).", url, reason, retryCount);
            return false;
        }

        requeue(urlQueue);
        logger.info("Re-queued {} ({}; retry {}/{}); the host is backed off for {} ms.", url, reason, retryCount, maxRetryCount, wait);
        return true;
    }

    /**
     * Inserts a copy of a URL queue entry back into the queue, without counting a retry.
     *
     * @param urlQueue the URL queue entry
     */
    protected void requeue(final UrlQueue<?> urlQueue) {
        final String url = urlQueue.getUrl();
        @SuppressWarnings("unchecked")
        final UrlQueue<Object> copy = (UrlQueue<Object>) crawlerContainer.getComponent("urlQueue");
        if (urlQueue.getId() != null) {
            copy.setId(urlQueue.getId());
        }
        copy.setMethod(urlQueue.getMethod());
        copy.setUrl(url);
        copy.setMetaData(urlQueue.getMetaData());
        copy.setEncoding(urlQueue.getEncoding());
        copy.setParentUrl(urlQueue.getParentUrl());
        copy.setDepth(urlQueue.getDepth());
        copy.setLastModified(urlQueue.getLastModified());
        copy.setWeight(urlQueue.getWeight());
        copy.setSessionId(urlQueue.getSessionId());
        copy.setCreateTime(urlQueue.getCreateTime());
        urlQueueService.insert(copy);
    }

    /**
     * Returns whether the crawler waits on folder operations.
     * @return true if no wait on folder, false otherwise.
     */
    public boolean isNoWaitOnFolder() {
        return noWaitOnFolder;
    }

    /**
     * Sets whether the crawler waits on folder operations.
     * @param noWaitOnFolder true to disable waiting on folder operations.
     */
    public void setNoWaitOnFolder(final boolean noWaitOnFolder) {
        this.noWaitOnFolder = noWaitOnFolder;
    }

    /**
     * Sets the client factory.
     * @param clientFactory The client factory.
     */
    public void setClientFactory(final CrawlerClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    /**
     * Sets the crawler context.
     * @param crawlerContext The CrawlerContext instance.
     */
    public void setCrawlerContext(final CrawlerContext crawlerContext) {
        this.crawlerContext = crawlerContext;
    }
}
