/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.codelibs.robot;

import java.util.Set;

import org.codelibs.robot.filter.UrlFilter;
import org.codelibs.robot.interval.IntervalController;
import org.codelibs.robot.rule.RuleManager;
import org.codelibs.robot.util.LruHashSet;

/**
 * @author shinsuke
 * 
 */
public class S2RobotContext {
    protected String sessionId;

    protected Integer activeThreadCount = 0;

    protected Object activeThreadCountLock = new Object();

    protected volatile Long accessCount = 0L; // NOPMD

    protected Object accessCountLock = new Object();

    protected volatile boolean running = false; // NOPMD

    protected UrlFilter urlFilter;

    protected RuleManager ruleManager;

    protected IntervalController intervalController;

    protected Set<String> robotTxtUrlSet = new LruHashSet<String>(10000);

    protected ThreadLocal<String[]> sitemapsLocal = new ThreadLocal<String[]>();

    /** The number of a thread */
    protected int numOfThread = 10;

    protected int maxThreadCheckCount = 20;

    /** a max depth for crawling. -1 is no depth check. */
    protected int maxDepth = -1;

    /** a max count to access urls. 0 is no limit to access it. */
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

    public Long getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(final Long accessCount) {
        this.accessCount = accessCount;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(final boolean running) {
        this.running = running;
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

    public void setIntervalController(
            final IntervalController intervalController) {
        this.intervalController = intervalController;
    }

    public Set<String> getRobotTxtUrlSet() {
        return robotTxtUrlSet;
    }

    public void setRobotTxtUrlSet(final Set<String> robotTxtUrlSet) {
        this.robotTxtUrlSet = robotTxtUrlSet;
    }

    public Object getActiveThreadCountLock() {
        return activeThreadCountLock;
    }

    public Object getAccessCountLock() {
        return accessCountLock;
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
