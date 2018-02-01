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
package org.codelibs.fess.crawler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.filter.UrlFilter;
import org.codelibs.fess.crawler.interval.IntervalController;
import org.codelibs.fess.crawler.rule.RuleManager;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Crawler manages/controls a crawling information.
 *
 * @author shinsuke
 *
 */
public class Crawler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    @Resource
    protected UrlQueueService<UrlQueue<?>> urlQueueService;

    @Resource
    protected DataService<AccessResult<?>> dataService;

    @Resource
    protected UrlFilter urlFilter;

    @Resource
    protected RuleManager ruleManager;

    @Resource
    protected CrawlerContainer crawlerContainer;

    @Resource
    protected IntervalController intervalController;

    @Resource
    protected CrawlerClientFactory clientFactory;

    protected CrawlerContext crawlerContext;

    protected boolean background = false;

    protected boolean daemon = false;

    protected int threadPriority = Thread.NORM_PRIORITY;

    protected Thread parentThread;

    protected ThreadGroup crawlerThreadGroup;

    public Crawler() {
        crawlerContext = new CrawlerContext();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS",
                Locale.ENGLISH);
        crawlerContext.sessionId = sdf.format(new Date());
    }

    public void addUrl(final String url) {
        try {
            urlQueueService.add(crawlerContext.sessionId, url);
        } catch (final Exception e) {
            logger.warn("Failed to add url: " + url, e);
        }
        urlFilter.processUrl(url);
    }

    public String getSessionId() {
        return crawlerContext.sessionId;
    }

    public void setSessionId(final String sessionId) {
        if (StringUtil.isNotBlank(sessionId)
                && !sessionId.equals(crawlerContext.sessionId)) {
            urlQueueService.updateSessionId(crawlerContext.sessionId, sessionId);
            crawlerContext.sessionId = sessionId;
        }
    }

    public String execute() {
        parentThread = new Thread(this, "Crawler-" + crawlerContext.sessionId);
        parentThread.setDaemon(daemon);
        parentThread.start();
        if (!background) {
            awaitTermination();
        }
        return crawlerContext.sessionId;
    }

    public void awaitTermination() {
        awaitTermination(0);
    }

    public void awaitTermination(final long millis) {
        if (parentThread != null) {
            try {
                parentThread.join(millis);
            } catch (final InterruptedException e) {
                logger.warn("Interrupted job at " + parentThread.getName());
            }
        }
    }

    public void cleanup(final String sessionId) {
        // TODO transaction?
        urlQueueService.delete(sessionId);
        dataService.delete(sessionId);
        urlFilter.clear();
    }

    public void addIncludeFilter(final String regexp) {
        if (StringUtil.isNotBlank(regexp)) {
            urlFilter.addInclude(regexp);
        }
    }

    public void addExcludeFilter(final String regexp) {
        if (StringUtil.isNotBlank(regexp)) {
            urlFilter.addExclude(regexp);
        }
    }

    public void stop() {
        crawlerContext.setStatus(CrawlerStatus.DONE);
        try {
            if (crawlerThreadGroup != null) {
                crawlerThreadGroup.interrupt();
            }
        } catch (final Exception e) {
            // ignore
        }
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

    public CrawlerClientFactory getClientFactory() {
        return clientFactory;
    }

    public boolean isBackground() {
        return background;
    }

    public void setBackground(final boolean background) {
        this.background = background;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(final boolean daemon) {
        this.daemon = daemon;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        // context
        crawlerContext.urlFilter = urlFilter;
        crawlerContext.ruleManager = ruleManager;
        crawlerContext.intervalController = intervalController;

        urlFilter.init(crawlerContext.sessionId);

        crawlerThreadGroup = new ThreadGroup("Crawler-" + crawlerContext.sessionId);
        final Thread[] threads = new Thread[crawlerContext.getNumOfThread()];
        for (int i = 0; i < crawlerContext.getNumOfThread(); i++) {
            final CrawlerThread crawlerThread = crawlerContainer
                    .getComponent("crawlerThread");
            crawlerThread.crawlerContext = crawlerContext;
            crawlerThread.clientFactory = clientFactory;
            threads[i] = new Thread(crawlerThreadGroup, crawlerThread, "Crawler-"
                    + crawlerContext.sessionId + "-" + Integer.toString(i + 1));
            threads[i].setDaemon(daemon);
            threads[i].setPriority(threadPriority);
        }

        // run
        crawlerContext.setStatus(CrawlerStatus.RUNNING);
        for (int i = 0; i < crawlerContext.numOfThread; i++) {
            threads[i].start();
        }

        // join
        for (int i = 0; i < crawlerContext.numOfThread; i++) {
            try {
                threads[i].join();
            } catch (final InterruptedException e) {
                logger.warn("Interrupted job at " + threads[i].getName());
            }
        }
        crawlerContext.setStatus(CrawlerStatus.DONE);

        urlQueueService.saveSession(crawlerContext.sessionId);
    }

    public CrawlerContext getCrawlerContext() {
        return crawlerContext;
    }

    public void setNumOfThread(final int numOfThread) {
        crawlerContext.numOfThread = numOfThread;
    }

    public void setMaxThreadCheckCount(final int maxThreadCheckCount) {
        crawlerContext.maxThreadCheckCount = maxThreadCheckCount;
    }

    public void setMaxDepth(final int maxDepth) {
        crawlerContext.maxDepth = maxDepth;
    }

    public void setMaxAccessCount(final long maxAccessCount) {
        crawlerContext.maxAccessCount = maxAccessCount;
    }

    public void setThreadPriority(final int threadPriority) {
        this.threadPriority = threadPriority;
    }
}
