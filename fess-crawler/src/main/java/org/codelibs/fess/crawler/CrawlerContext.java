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

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.codelibs.core.collection.LruHashSet;
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
     * Current number of active crawler threads.
     */
    protected Integer activeThreadCount = 0;

    /**
     * Lock object for synchronizing access to active thread count.
     */
    protected Object activeThreadCountLock = new Object();

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
     */
    protected Set<String> robotsTxtUrlSet = new LruHashSet<>(10000);

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
     * Returns the active thread count.
     * @return The active thread count.
     */
    public Integer getActiveThreadCount() {
        return activeThreadCount;
    }

    /**
     * Sets the active thread count.
     * @param activeThreadCount The active thread count.
     */
    public void setActiveThreadCount(final Integer activeThreadCount) {
        this.activeThreadCount = activeThreadCount;
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
     * Returns the lock object for active thread count.
     * @return The lock object.
     */
    public Object getActiveThreadCountLock() {
        return activeThreadCountLock;
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
