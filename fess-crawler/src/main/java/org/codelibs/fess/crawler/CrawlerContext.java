/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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
    protected String sessionId;

    protected Integer activeThreadCount = 0;

    protected Object activeThreadCountLock = new Object();

    protected AtomicLong accessCount = new AtomicLong(0);

    protected volatile CrawlerStatus status = CrawlerStatus.INITIALIZING;

    protected UrlFilter urlFilter;

    protected RuleManager ruleManager;

    protected IntervalController intervalController;

    protected Set<String> robotsTxtUrlSet = new LruHashSet<>(10000);

    protected ThreadLocal<String[]> sitemapsLocal = new ThreadLocal<>();

    /** The number of threads used by the crawler */
    protected int numOfThread = 10;

    protected int maxThreadCheckCount = 20;

    /** The maximum depth for crawling. A value of -1 indicates no depth check. */
    protected int maxDepth = -1;

    /** The maximum number of URLs to access. A value of 0 indicates no limit. */
    protected long maxAccessCount = 0;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getActiveThreadCount() {
        return activeThreadCount;
    }

    public void setActiveThreadCount(final Integer activeThreadCount) {
        this.activeThreadCount = activeThreadCount;
    }

    public long getAccessCount() {
        return accessCount.get();
    }

    public long incrementAndGetAccessCount() {
        return accessCount.incrementAndGet();
    }

    public long decrementAndGetAccessCount() {
        return accessCount.decrementAndGet();
    }

    public CrawlerStatus getStatus() {
        return status;
    }

    public void setStatus(final CrawlerStatus status) {
        this.status = status;
    }

    public UrlFilter getUrlFilter() {
        return urlFilter;
    }

    public void setUrlFilter(final UrlFilter urlFilter) {
        this.urlFilter = urlFilter;
    }

    public RuleManager getRuleManager() {
        return ruleManager;
    }

    public void setRuleManager(final RuleManager ruleManager) {
        this.ruleManager = ruleManager;
    }

    public IntervalController getIntervalController() {
        return intervalController;
    }

    public void setIntervalController(final IntervalController intervalController) {
        this.intervalController = intervalController;
    }

    public Set<String> getRobotsTxtUrlSet() {
        return robotsTxtUrlSet;
    }

    public void setRobotsTxtUrlSet(final Set<String> robotsTxtUrlSet) {
        this.robotsTxtUrlSet = robotsTxtUrlSet;
    }

    public Object getActiveThreadCountLock() {
        return activeThreadCountLock;
    }

    public int getNumOfThread() {
        return numOfThread;
    }

    public void setNumOfThread(final int numOfThread) {
        this.numOfThread = numOfThread;
    }

    public int getMaxThreadCheckCount() {
        return maxThreadCheckCount;
    }

    public void setMaxThreadCheckCount(final int maxThreadCheckCount) {
        this.maxThreadCheckCount = maxThreadCheckCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public long getMaxAccessCount() {
        return maxAccessCount;
    }

    public void setMaxAccessCount(final long maxAccessCount) {
        this.maxAccessCount = maxAccessCount;
    }

    public void addSitemaps(final String[] sitemaps) {
        sitemapsLocal.set(sitemaps);
    }

    public String[] removeSitemaps() {
        final String[] sitemaps = sitemapsLocal.get();
        if (sitemaps != null) {
            sitemapsLocal.remove();
        }
        return sitemaps;
    }
}
