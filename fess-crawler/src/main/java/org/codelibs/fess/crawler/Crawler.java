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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import jakarta.annotation.Resource;

/**
 * The Crawler class is the main class for web crawling. It manages the crawling process,
 * including adding URLs to the queue, filtering URLs, managing crawler threads,
 * and handling the overall crawling lifecycle.
 *
 * <p>It implements the Runnable interface to be executed in a separate thread,
 * and the AutoCloseable interface to ensure resources are properly released after use.
 *
 * <p>The crawler uses several services and components, such as UrlQueueService, DataService,
 * UrlFilter, RuleManager, CrawlerContainer, IntervalController, and CrawlerClientFactory,
 * to perform its tasks.
 *
 * <p>The crawling process involves the following steps:
 * <ol>
 *   <li>Initialization: Sets up the crawler context and initializes the URL filter.</li>
 *   <li>Thread Creation: Creates a group of crawler threads to perform the actual crawling.</li>
 *   <li>Execution: Starts the crawler threads and waits for them to complete.</li>
 *   <li>Cleanup: Deletes the crawled data and clears the URL filter.</li>
 * </ol>
 *
 * <p>The crawler can be configured with various parameters, such as the number of threads,
 * the maximum depth of crawling, and URL filters.
 *
 * <p>Example usage:
 * <pre>
 *   Crawler crawler = new Crawler();
 *   crawler.addUrl("http://example.com/");
 *   crawler.execute();
 *   crawler.close();
 * </pre>
 */
public class Crawler implements Runnable, AutoCloseable {

    private static final Logger logger = LogManager.getLogger(Crawler.class);

    /**
     * Service for managing URL queues during crawling.
     */
    @Resource
    protected UrlQueueService<UrlQueue<?>> urlQueueService;

    /**
     * Service for managing access result data.
     */
    @Resource
    protected DataService<AccessResult<?>> dataService;

    /**
     * Filter for URLs to control which URLs are crawled.
     */
    @Resource
    protected UrlFilter urlFilter;

    /**
     * Manager for crawling rules and configurations.
     */
    @Resource
    protected RuleManager ruleManager;

    /**
     * Container for managing crawler components.
     */
    @Resource
    protected CrawlerContainer crawlerContainer;

    /**
     * Controller for managing crawling intervals and delays.
     */
    @Resource
    protected IntervalController intervalController;

    /**
     * Factory for creating crawler clients.
     */
    @Resource
    protected CrawlerClientFactory clientFactory;

    /**
     * Context object containing crawler state and configuration.
     */
    protected CrawlerContext crawlerContext;

    /**
     * Flag indicating whether the crawler runs in background mode.
     */
    protected boolean background = false;

    /**
     * Flag indicating whether crawler threads run as daemon threads.
     */
    protected boolean daemon = false;

    /**
     * Priority for crawler threads.
     */
    protected int threadPriority = Thread.NORM_PRIORITY;

    /**
     * Reference to the parent thread that started the crawler.
     */
    protected Thread parentThread;

    /**
     * The thread group for crawler threads.
     */
    protected ThreadGroup crawlerThreadGroup;

    /**
     * Constructs a new Crawler instance.
     * Initializes the crawler context with a new session ID based on the current timestamp.
     */
    public Crawler() {
        crawlerContext = new CrawlerContext();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH);
        crawlerContext.sessionId = sdf.format(new Date());
    }

    /**
     * Adds a URL to the crawling queue.
     * @param url The URL to add.
     */
    public void addUrl(final String url) {
        try {
            urlQueueService.add(crawlerContext.sessionId, url);
            if (logger.isDebugEnabled()) {
                logger.debug("Added URL to queue: url={}, sessionId={}", url, crawlerContext.sessionId);
            }
        } catch (final Exception e) {
            logger.warn("Failed to add URL to crawling queue: url={}, sessionId={}", url, crawlerContext.sessionId, e);
        }
        urlFilter.processUrl(url);
    }

    /**
     * Returns the current session ID.
     * @return The session ID.
     */
    public String getSessionId() {
        return crawlerContext.sessionId;
    }

    /**
     * Sets the session ID.
     * If the new session ID is different from the current one, it updates the session ID in the URL queue service.
     * @param sessionId The new session ID.
     */
    public void setSessionId(final String sessionId) {
        if (StringUtil.isNotBlank(sessionId) && !sessionId.equals(crawlerContext.sessionId)) {
            final String oldSessionId = crawlerContext.sessionId;
            urlQueueService.updateSessionId(crawlerContext.sessionId, sessionId);
            crawlerContext.sessionId = sessionId;
            if (logger.isInfoEnabled()) {
                logger.info("Updated crawler session ID: oldSessionId={}, newSessionId={}", oldSessionId, sessionId);
            }
        }
    }

    /**
     * Executes the crawling process.
     * Starts a new thread for the crawler and optionally waits for its termination.
     * @return The session ID of the crawling process.
     */
    public String execute() {
        if (logger.isInfoEnabled()) {
            logger.info("Starting crawler execution: sessionId={}, background={}, daemon={}", crawlerContext.sessionId, background,
                    daemon);
        }
        parentThread = new Thread(this, "Crawler-" + crawlerContext.sessionId);
        parentThread.setDaemon(daemon);
        parentThread.start();
        if (!background) {
            awaitTermination();
        }
        return crawlerContext.sessionId;
    }

    /**
     * Waits for the crawling process to terminate indefinitely.
     */
    public void awaitTermination() {
        awaitTermination(0);
    }

    /**
     * Waits for the crawling process to terminate for a specified duration.
     * @param millis The maximum time to wait in milliseconds. A value of 0 means wait indefinitely.
     */
    public void awaitTermination(final long millis) {
        if (parentThread != null) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Waiting for crawler termination: thread={}, timeout={}ms", parentThread.getName(), millis);
                }
                parentThread.join(millis);
            } catch (final InterruptedException e) {
                logger.warn("Crawler thread was interrupted while waiting for termination: thread={}, sessionId={}", parentThread.getName(),
                        crawlerContext.sessionId, e);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Cleans up the crawled data for a given session.
     * Deletes URL queue data and access result data, and clears the URL filter.
     * @param sessionId The session ID to clean up.
     */
    public void cleanup(final String sessionId) {
        if (logger.isInfoEnabled()) {
            logger.info("Starting crawler cleanup: sessionId={}", sessionId);
        }
        try {
            // TODO transaction?
            urlQueueService.delete(sessionId);
            dataService.delete(sessionId);
            urlFilter.clear();
            if (logger.isInfoEnabled()) {
                logger.info("Completed crawler cleanup: sessionId={}", sessionId);
            }
        } catch (final Exception e) {
            logger.error("Failed to cleanup crawler data: sessionId={}", sessionId, e);
        }
    }

    /**
     * Closes the crawler, releasing resources.
     * This method is called automatically when the crawler is used in a try-with-resources statement.
     */
    @Override
    public void close() {
        clientFactory.close();
    }

    /**
     * Adds an include filter for URLs.
     * Only URLs matching this regular expression will be crawled.
     * @param regexp The regular expression for the include filter.
     */
    public void addIncludeFilter(final String regexp) {
        if (StringUtil.isNotBlank(regexp)) {
            urlFilter.addInclude(regexp);
        }
    }

    /**
     * Adds an exclude filter for URLs.
     * URLs matching this regular expression will not be crawled.
     * @param regexp The regular expression for the exclude filter.
     */
    public void addExcludeFilter(final String regexp) {
        if (StringUtil.isNotBlank(regexp)) {
            urlFilter.addExclude(regexp);
        }
    }

    /**
     * Stops the crawling process.
     * Sets the crawler status to DONE and interrupts all crawler threads.
     */
    public void stop() {
        if (logger.isInfoEnabled()) {
            logger.info("Stopping crawler: sessionId={}, status={}", crawlerContext.sessionId, crawlerContext.getStatus());
        }
        crawlerContext.setStatus(CrawlerStatus.DONE);
        try {
            if (crawlerThreadGroup != null) {
                crawlerThreadGroup.interrupt();
                if (logger.isDebugEnabled()) {
                    logger.debug("Interrupted all crawler threads: sessionId={}, activeCount={}", crawlerContext.sessionId,
                            crawlerThreadGroup.activeCount());
                }
            }
        } catch (final Exception e) {
            logger.warn("Failed to interrupt crawler thread group: sessionId={}", crawlerContext.sessionId, e);
        }
    }

    /**
     * Returns the URL filter.
     * @return The UrlFilter instance.
     */
    public UrlFilter getUrlFilter() {
        return urlFilter;
    }

    /**
     * Sets the URL filter.
     * @param urlFilter The UrlFilter instance to set.
     */
    public void setUrlFilter(final UrlFilter urlFilter) {
        this.urlFilter = urlFilter;
    }

    /**
     * Returns the rule manager.
     * @return The RuleManager instance.
     */
    public RuleManager getRuleManager() {
        return ruleManager;
    }

    /**
     * Sets the rule manager.
     * @param ruleManager The RuleManager instance to set.
     */
    public void setRuleManager(final RuleManager ruleManager) {
        this.ruleManager = ruleManager;
    }

    /**
     * Returns the interval controller.
     * @return The IntervalController instance.
     */
    public IntervalController getIntervalController() {
        return intervalController;
    }

    /**
     * Sets the interval controller.
     * @param intervalController The IntervalController instance to set.
     */
    public void setIntervalController(final IntervalController intervalController) {
        this.intervalController = intervalController;
    }

    /**
     * Returns the crawler client factory.
     * @return The CrawlerClientFactory instance.
     */
    public CrawlerClientFactory getClientFactory() {
        return clientFactory;
    }

    /**
     * Checks if the crawler is running in background mode.
     * @return true if in background mode, false otherwise.
     */
    public boolean isBackground() {
        return background;
    }

    /**
     * Sets the background mode for the crawler.
     * If true, the execute method will not wait for termination.
     * @param background true to run in background, false otherwise.
     */
    public void setBackground(final boolean background) {
        this.background = background;
    }

    /**
     * Checks if the crawler threads are daemon threads.
     * @return true if daemon threads, false otherwise.
     */
    public boolean isDaemon() {
        return daemon;
    }

    /**
     * Sets whether the crawler threads should be daemon threads.
     * @param daemon true to make threads daemon, false otherwise.
     */
    public void setDaemon(final boolean daemon) {
        this.daemon = daemon;
    }

    /**
     * Runs the crawling process in a separate thread.
     * This method initializes the crawler context, creates and starts crawler threads,
     * and waits for their completion.
     */
    @Override
    public void run() {
        if (logger.isInfoEnabled()) {
            logger.info("Initializing crawler: sessionId={}, numOfThread={}, maxDepth={}, maxAccessCount={}", crawlerContext.sessionId,
                    crawlerContext.getNumOfThread(), crawlerContext.maxDepth, crawlerContext.maxAccessCount);
        }

        // context
        crawlerContext.urlFilter = urlFilter;
        crawlerContext.ruleManager = ruleManager;
        crawlerContext.intervalController = intervalController;

        urlFilter.init(crawlerContext.sessionId);

        crawlerThreadGroup = new ThreadGroup("Crawler-" + crawlerContext.sessionId);
        final Thread[] threads = new Thread[crawlerContext.getNumOfThread()];
        for (int i = 0; i < crawlerContext.getNumOfThread(); i++) {
            final CrawlerThread crawlerThread = crawlerContainer.getComponent("crawlerThread");
            crawlerThread.setCrawlerContext(crawlerContext);
            crawlerThread.setClientFactory(clientFactory);
            threads[i] =
                    new Thread(crawlerThreadGroup, crawlerThread, "Crawler-" + crawlerContext.sessionId + "-" + Integer.toString(i + 1));
            threads[i].setDaemon(daemon);
            threads[i].setPriority(threadPriority);
        }

        // run
        crawlerContext.setStatus(CrawlerStatus.RUNNING);
        if (logger.isInfoEnabled()) {
            logger.info("Starting crawler threads: sessionId={}, numOfThread={}", crawlerContext.sessionId, crawlerContext.numOfThread);
        }
        for (int i = 0; i < crawlerContext.numOfThread; i++) {
            threads[i].start();
        }

        // join
        for (int i = 0; i < crawlerContext.numOfThread; i++) {
            try {
                threads[i].join();
            } catch (final InterruptedException e) {
                logger.warn("Crawler thread was interrupted while waiting for completion: thread={}, sessionId={}", threads[i].getName(),
                        crawlerContext.sessionId, e);
                Thread.currentThread().interrupt();
            }
        }
        crawlerContext.setStatus(CrawlerStatus.DONE);
        if (logger.isInfoEnabled()) {
            logger.info("Crawler execution completed: sessionId={}, status={}", crawlerContext.sessionId, CrawlerStatus.DONE);
        }

        try {
            urlQueueService.saveSession(crawlerContext.sessionId);
            if (logger.isDebugEnabled()) {
                logger.debug("Saved crawler session: sessionId={}", crawlerContext.sessionId);
            }
        } catch (final Exception e) {
            logger.error("Failed to save crawler session: sessionId={}", crawlerContext.sessionId, e);
        }
    }

    /**
     * Returns the crawler context.
     * @return The CrawlerContext instance.
     */
    public CrawlerContext getCrawlerContext() {
        return crawlerContext;
    }

    /**
     * Sets the number of threads.
     * @param numOfThread The number of threads.
     */
    public void setNumOfThread(final int numOfThread) {
        crawlerContext.numOfThread = numOfThread;
    }

    /**
     * Sets the maximum thread check count.
     * @param maxThreadCheckCount The maximum thread check count.
     */
    public void setMaxThreadCheckCount(final int maxThreadCheckCount) {
        crawlerContext.maxThreadCheckCount = maxThreadCheckCount;
    }

    /**
     * Sets the maximum depth.
     * @param maxDepth The maximum depth.
     */
    public void setMaxDepth(final int maxDepth) {
        crawlerContext.maxDepth = maxDepth;
    }

    /**
     * Sets the maximum access count.
     * @param maxAccessCount The maximum access count.
     */
    public void setMaxAccessCount(final long maxAccessCount) {
        crawlerContext.maxAccessCount = maxAccessCount;
    }

    /**
     * Sets the priority for crawler threads.
     * @param threadPriority The thread priority.
     */
    public void setThreadPriority(final int threadPriority) {
        this.threadPriority = threadPriority;
    }
}
