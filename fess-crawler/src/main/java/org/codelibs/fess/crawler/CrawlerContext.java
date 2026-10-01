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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.codelibs.core.collection.LruHashMap;
import org.codelibs.core.collection.LruHashSet;
import org.codelibs.fess.crawler.entity.HostState;
import org.codelibs.fess.crawler.filter.UrlFilter;
import org.codelibs.fess.crawler.interval.IntervalController;
import org.codelibs.fess.crawler.rule.RuleManager;

/**
 * The {@link CrawlerContext} class holds the context information for a crawler execution.
 * It contains various attributes related to the crawler's state, configuration, and runtime data.
 * This class provides methods to access and modify these attributes, allowing for control and monitoring
 * of the crawler's behavior.
 *
 * <p>
 * The context includes information such as the session ID, active thread count, access count, crawler status,
 * URL filter, rule manager, interval controller, robots.txt URL set, sitemaps, number of threads,
 * maximum thread check count, maximum depth, and maximum access count.
 * </p>
 *
 * <p>
 * It also provides thread-local storage for sitemaps, allowing each thread to have its own set of sitemaps.
 * </p>
 */
public class CrawlerContext {
    /**
     * Constructs a new CrawlerContext.
     */
    public CrawlerContext() {
        // Default constructor
    }

    /**
     * Session identifier for the crawling session.
     */
    protected String sessionId;

    /**
     * Atomic counter for tracking active crawler threads without lock contention.
     */
    protected final AtomicInteger activeThreadCount = new AtomicInteger(0);

    /**
     * Atomic counter for tracking the number of accesses made.
     */
    protected AtomicLong accessCount = new AtomicLong(0);

    /**
     * Current status of the crawler.
     */
    protected volatile CrawlerStatus status = CrawlerStatus.INITIALIZING;

    /**
     * Filter for URLs to control which URLs are processed.
     */
    protected UrlFilter urlFilter;

    /**
     * Manager for crawling rules and configurations.
     */
    protected RuleManager ruleManager;

    /**
     * Controller for managing crawling intervals and delays.
     */
    protected IntervalController intervalController;

    /**
     * Set of robots.txt URLs that have been processed.
     * <p>
     * Wrapped with {@link Collections#synchronizedSet(Set)} because {@link LruHashSet} (backed by a
     * plain, non-thread-safe {@code LruHashMap}) is shared across all crawler threads. HTTP clients
     * collapse their check-then-add into a single {@code add(url)} call, which under this wrapper is
     * synchronized as one atomic operation -- avoiding both backing-map corruption from concurrent
     * structural modification and duplicate robots.txt fetches. The 10000-entry LRU bound and
     * eviction behavior are unchanged.
     * </p>
     */
    protected Set<String> robotsTxtUrlSet = Collections.synchronizedSet(new LruHashSet<>(10000));

    /**
     * Politeness state (robots.txt, Crawl-delay, backoff) per origin.
     * <p>
     * Wrapped with {@link Collections#synchronizedMap(Map)} because {@link LruHashMap} is not thread-safe
     * and is shared across all crawler threads. The 10000-entry LRU bound evicts the least recently used origin.
     * </p>
     */
    protected Map<String, HostState> hostStateMap = Collections.synchronizedMap(new LruHashMap<>(10000));

    /**
     * Number of retries performed so far per URL, for 429/503 responses.
     * Wrapped with {@link Collections#synchronizedMap(Map)} for the same reason as {@link #hostStateMap}.
     */
    protected Map<String, Integer> retryCountMap = Collections.synchronizedMap(new LruHashMap<>(10000));

    /**
     * URLs whose robots.txt outage has been reported as a crawling failure in this crawl.
     * Wrapped with {@link Collections#synchronizedSet(Set)} for the same reason as {@link #robotsTxtUrlSet}.
     */
    protected Set<String> robotsTxtFailureReportedSet = Collections.synchronizedSet(new LruHashSet<>(10000));

    /** The upper limit of the robots.txt Crawl-delay in milliseconds. */
    protected long maxCrawlDelayMillis = 60000L;

    /** The first exponential backoff wait in milliseconds. */
    protected long backoffBaseMillis = 10000L;

    /** The upper limit of a backoff wait in milliseconds. */
    protected long maxBackoffMillis = 300000L;

    /** The maximum number of retries of one URL after a 429/503 response. */
    protected int maxRetryCount = 3;

    /**
     * The number of retries after the first failed robots.txt fetch of one origin; when they fail as well,
     * no URL of the origin is crawled.
     */
    protected int robotsTxtMaxRetries = 3;

    /**
     * Thread-local storage for sitemaps.
     */
    protected ThreadLocal<String[]> sitemapsLocal = new ThreadLocal<>();

    /** The number of threads used by the crawler */
    protected int numOfThread = 10;

    /** The maximum number of times to check for active threads. */
    protected int maxThreadCheckCount = 20;

    /** The maximum depth for crawling. A value of -1 indicates no depth check. */
    protected int maxDepth = -1;

    /** The maximum number of URLs to access. A value of 0 indicates no limit. */
    protected long maxAccessCount = 0;

    /**
     * Returns the session ID.
     * @return The session ID.
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session ID.
     * @param sessionId The session ID.
     */
    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Returns the current active thread count.
     * @return The active thread count.
     */
    public int getActiveThreadCount() {
        return activeThreadCount.get();
    }

    /**
     * Increments the active thread count and returns the new value.
     * @return The incremented active thread count.
     */
    public int incrementAndGetActiveThreadCount() {
        return activeThreadCount.incrementAndGet();
    }

    /**
     * Decrements the active thread count and returns the new value.
     * @return The decremented active thread count.
     */
    public int decrementAndGetActiveThreadCount() {
        return activeThreadCount.decrementAndGet();
    }

    /**
     * Returns the access count.
     * @return The access count.
     */
    public long getAccessCount() {
        return accessCount.get();
    }

    /**
     * Increments the access count and returns the new value.
     * @return The incremented access count.
     */
    public long incrementAndGetAccessCount() {
        return accessCount.incrementAndGet();
    }

    /**
     * Decrements the access count and returns the new value.
     * @return The decremented access count.
     */
    public long decrementAndGetAccessCount() {
        return accessCount.decrementAndGet();
    }

    /**
     * Returns the crawler status.
     * @return The CrawlerStatus.
     */
    public CrawlerStatus getStatus() {
        return status;
    }

    /**
     * Sets the crawler status.
     * @param status The CrawlerStatus.
     */
    public void setStatus(final CrawlerStatus status) {
        this.status = status;
    }

    /**
     * Returns the URL filter.
     * @return The UrlFilter.
     */
    public UrlFilter getUrlFilter() {
        return urlFilter;
    }

    /**
     * Sets the URL filter.
     * @param urlFilter The UrlFilter.
     */
    public void setUrlFilter(final UrlFilter urlFilter) {
        this.urlFilter = urlFilter;
    }

    /**
     * Returns the rule manager.
     * @return The RuleManager.
     */
    public RuleManager getRuleManager() {
        return ruleManager;
    }

    /**
     * Sets the rule manager.
     * @param ruleManager The RuleManager.
     */
    public void setRuleManager(final RuleManager ruleManager) {
        this.ruleManager = ruleManager;
    }

    /**
     * Returns the interval controller.
     * @return The IntervalController.
     */
    public IntervalController getIntervalController() {
        return intervalController;
    }

    /**
     * Sets the interval controller.
     * @param intervalController The IntervalController.
     */
    public void setIntervalController(final IntervalController intervalController) {
        this.intervalController = intervalController;
    }

    /**
     * Returns the set of robots.txt URLs.
     * @return The set of robots.txt URLs.
     */
    public Set<String> getRobotsTxtUrlSet() {
        return robotsTxtUrlSet;
    }

    /**
     * Sets the set of robots.txt URLs.
     * @param robotsTxtUrlSet The set of robots.txt URLs.
     */
    public void setRobotsTxtUrlSet(final Set<String> robotsTxtUrlSet) {
        this.robotsTxtUrlSet = robotsTxtUrlSet;
    }

    /**
     * Returns the politeness state of the origin of a URL, creating it if absent.
     * @param url The URL.
     * @return The HostState, or null if the URL has no host.
     */
    public HostState getHostState(final String url) {
        final String origin = HostState.toOrigin(url);
        if (origin == null) {
            return null;
        }
        synchronized (hostStateMap) {
            return hostStateMap.computeIfAbsent(origin, k -> new HostState());
        }
    }

    /**
     * Returns the politeness state of the origin of a URL without creating it.
     * @param url The URL.
     * @return The HostState, or null if there is none or the URL has no host.
     */
    public HostState peekHostState(final String url) {
        final String origin = HostState.toOrigin(url);
        return origin == null ? null : hostStateMap.get(origin);
    }

    /**
     * Returns the map of politeness states by origin.
     * @return The map of HostState.
     */
    public Map<String, HostState> getHostStateMap() {
        return hostStateMap;
    }

    /**
     * Sets the map of politeness states by origin.
     * @param hostStateMap The map of HostState.
     */
    public void setHostStateMap(final Map<String, HostState> hostStateMap) {
        this.hostStateMap = hostStateMap;
    }

    /**
     * Increments the retry count of a URL and returns the new value.
     * @param url The URL.
     * @return The incremented retry count.
     */
    public int incrementAndGetRetryCount(final String url) {
        synchronized (retryCountMap) {
            final int count = retryCountMap.getOrDefault(url, 0) + 1;
            retryCountMap.put(url, count);
            return count;
        }
    }

    /**
     * Marks the robots.txt outage of a URL as reported, so that it is reported as a crawling failure at most once in a crawl
     * however many times robots.txt fails for it.
     * @param url The URL.
     * @return true the first time it is called for the URL, false afterwards.
     */
    public boolean markRobotsTxtFailureReported(final String url) {
        return robotsTxtFailureReportedSet.add(url);
    }

    /**
     * Returns the map of retry counts by URL.
     * @return The map of retry counts.
     */
    public Map<String, Integer> getRetryCountMap() {
        return retryCountMap;
    }

    /**
     * Sets the map of retry counts by URL.
     * @param retryCountMap The map of retry counts.
     */
    public void setRetryCountMap(final Map<String, Integer> retryCountMap) {
        this.retryCountMap = retryCountMap;
    }

    /**
     * Returns the upper limit of the robots.txt Crawl-delay.
     * @return The maximum Crawl-delay in milliseconds.
     */
    public long getMaxCrawlDelayMillis() {
        return maxCrawlDelayMillis;
    }

    /**
     * Sets the upper limit of the robots.txt Crawl-delay.
     * @param maxCrawlDelayMillis The maximum Crawl-delay in milliseconds.
     */
    public void setMaxCrawlDelayMillis(final long maxCrawlDelayMillis) {
        this.maxCrawlDelayMillis = maxCrawlDelayMillis;
    }

    /**
     * Returns the first exponential backoff wait.
     * @return The backoff base in milliseconds.
     */
    public long getBackoffBaseMillis() {
        return backoffBaseMillis;
    }

    /**
     * Sets the first exponential backoff wait.
     * @param backoffBaseMillis The backoff base in milliseconds.
     */
    public void setBackoffBaseMillis(final long backoffBaseMillis) {
        this.backoffBaseMillis = backoffBaseMillis;
    }

    /**
     * Returns the upper limit of a backoff wait.
     * @return The maximum backoff in milliseconds.
     */
    public long getMaxBackoffMillis() {
        return maxBackoffMillis;
    }

    /**
     * Sets the upper limit of a backoff wait.
     * @param maxBackoffMillis The maximum backoff in milliseconds.
     */
    public void setMaxBackoffMillis(final long maxBackoffMillis) {
        this.maxBackoffMillis = maxBackoffMillis;
    }

    /**
     * Returns the maximum number of retries of one URL.
     * @return The maximum retry count.
     */
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    /**
     * Sets the maximum number of retries of one URL.
     * @param maxRetryCount The maximum retry count.
     */
    public void setMaxRetryCount(final int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    /**
     * Returns the number of retries after the first failed robots.txt fetch of an origin.
     * @return The number of robots.txt retries.
     */
    public int getRobotsTxtMaxRetries() {
        return robotsTxtMaxRetries;
    }

    /**
     * Sets the number of retries after the first failed robots.txt fetch of an origin.
     * @param robotsTxtMaxRetries The number of robots.txt retries.
     */
    public void setRobotsTxtMaxRetries(final int robotsTxtMaxRetries) {
        this.robotsTxtMaxRetries = robotsTxtMaxRetries;
    }

    /**
     * Returns the number of threads.
     * @return The number of threads.
     */
    public int getNumOfThread() {
        return numOfThread;
    }

    /**
     * Sets the number of threads.
     * @param numOfThread The number of threads.
     */
    public void setNumOfThread(final int numOfThread) {
        this.numOfThread = numOfThread;
    }

    /**
     * Returns the maximum thread check count.
     * @return The maximum thread check count.
     */
    public int getMaxThreadCheckCount() {
        return maxThreadCheckCount;
    }

    /**
     * Sets the maximum thread check count.
     * @param maxThreadCheckCount The maximum thread check count.
     */
    public void setMaxThreadCheckCount(final int maxThreadCheckCount) {
        this.maxThreadCheckCount = maxThreadCheckCount;
    }

    /**
     * Returns the maximum depth.
     * @return The maximum depth.
     */
    public int getMaxDepth() {
        return maxDepth;
    }

    /**
     * Sets the maximum depth.
     * @param maxDepth The maximum depth.
     */
    public void setMaxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * Returns the maximum access count.
     * @return The maximum access count.
     */
    public long getMaxAccessCount() {
        return maxAccessCount;
    }

    /**
     * Sets the maximum access count.
     * @param maxAccessCount The maximum access count.
     */
    public void setMaxAccessCount(final long maxAccessCount) {
        this.maxAccessCount = maxAccessCount;
    }

    /**
     * Adds sitemaps to the thread-local storage.
     * @param sitemaps An array of sitemap URLs.
     */
    public void addSitemaps(final String[] sitemaps) {
        sitemapsLocal.set(sitemaps);
    }

    /**
     * Removes sitemaps from the thread-local storage and returns them.
     * @return An array of sitemap URLs, or null if none were present.
     */
    public String[] removeSitemaps() {
        final String[] sitemaps = sitemapsLocal.get();
        if (sitemaps != null) {
            sitemapsLocal.remove();
        }
        return sitemaps;
    }
}
