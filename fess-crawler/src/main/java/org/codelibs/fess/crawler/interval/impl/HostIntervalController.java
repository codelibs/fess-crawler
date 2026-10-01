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
package org.codelibs.fess.crawler.interval.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.exception.InterruptedRuntimeException;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.SystemUtil;
import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.entity.HostState;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * HostIntervalController is an implementation of {@link org.codelibs.fess.crawler.interval.IntervalController}
 * that controls the interval between requests to the same host.
 * It uses a Guava Cache to store the last access time for each host.
 * The delayBeforeProcessing method is overridden to introduce a delay before processing a URL,
 * ensuring that requests to the same host are not made too frequently.
 * The delay is calculated based on the configured delayMillisBeforeProcessing parameter.
 * If the time since the last request to the host is less than the configured delay,
 * the thread waits until the delay has elapsed.
 * In addition, when a {@link CrawlerContext} is available, the robots.txt Crawl-delay (capped at
 * {@link CrawlerContext#getMaxCrawlDelayMillis()}) and the failure backoff recorded in the
 * {@link HostState} of the origin of the URL are waited for.
 * This class is thread-safe.
 * The cache automatically evicts entries after 1 hour of inactivity to prevent memory leaks.
 */
public class HostIntervalController extends DefaultIntervalController {

    private static final Logger logger = LogManager.getLogger(HostIntervalController.class);

    /** Default cache expire duration in hours */
    private static final long DEFAULT_CACHE_EXPIRE_HOURS = 1L;

    /** Cache storing the last access time for each host. */
    private final Cache<String, AtomicLong> lastTimes;

    /**
     * Constructs a new HostIntervalController with default parameters.
     */
    public HostIntervalController() {
        this.lastTimes = CacheBuilder.newBuilder().expireAfterAccess(DEFAULT_CACHE_EXPIRE_HOURS, TimeUnit.HOURS).build();
    }

    /**
     * Constructs a new HostIntervalController with the specified parameters.
     *
     * @param params the parameters to configure the interval controller
     */
    public HostIntervalController(final Map<String, Long> params) {
        super(params);
        this.lastTimes = CacheBuilder.newBuilder().expireAfterAccess(DEFAULT_CACHE_EXPIRE_HOURS, TimeUnit.HOURS).build();
    }

    /**
     * Delays before processing a URL, ensuring that requests to the same host are not made too frequently.
     * This method extracts the host from the URL and enforces a delay based on the configured
     * delayMillisBeforeProcessing parameter; that delay is skipped for a URL that {@link URI} cannot parse.
     * Then the robots.txt Crawl-delay and the failure backoff of the origin are waited for, whether or not
     * the URL could be parsed.
     *
     * @throws InterruptedRuntimeException if the thread is interrupted during the delay
     * @throws CrawlerSystemException if an error occurs while processing the URL
     */
    @Override
    protected void delayBeforeProcessing() {
        final UrlQueue<?> urlQueue = CrawlingParameterUtil.getUrlQueue();
        if (urlQueue == null) {
            return;
        }

        final String url = urlQueue.getUrl();
        if (StringUtil.isBlank(url) || url.startsWith("file:")) {
            // not target
            return;
        }

        try {
            String host = null;
            try {
                host = new URI(url).getHost();
            } catch (final URISyntaxException e) {
                // such as "[", "]" or a stray "%" in the path: only the per-host delay below is skipped
                if (logger.isDebugEnabled()) {
                    logger.debug("Skipped the per-host delay for an unparsable URL: {}", url, e);
                }
            }
            if (host != null) {
                delayForHost(host);
            }

            delayForHostState(url);
        } catch (final InterruptedException e) {
            throw new InterruptedRuntimeException(e);
        } catch (final Exception e) {
            throw new CrawlerSystemException(e);
        }
    }

    /**
     * Waits until {@code delayMillisBeforeProcessing} has elapsed since the last access to a host.
     *
     * @param host the host name
     * @throws InterruptedException if the thread is interrupted while waiting
     * @throws ExecutionException if the entry of the host cannot be created
     */
    private void delayForHost(final String host) throws InterruptedException, ExecutionException {
        // Atomically get or create the AtomicLong for this host using Cache.get()
        // This ensures thread-safe, atomic get-or-create behavior
        // Initialize with 0 to mark uninitialized state
        final AtomicLong lastTime = lastTimes.get(host, () -> new AtomicLong(0));

        synchronized (lastTime) {
            final long lastValue = lastTime.get();
            if (lastValue == 0) {
                // First access to this host - no delay needed
                // Set current time to allow proper delay for next access
                lastTime.set(SystemUtil.currentTimeMillis());
            } else {
                long currentTime = SystemUtil.currentTimeMillis();
                long delayTime = lastValue + delayMillisBeforeProcessing - currentTime;
                while (delayTime > 0) {
                    lastTime.wait(delayTime);
                    currentTime = SystemUtil.currentTimeMillis();
                    delayTime = lastTime.get() + delayMillisBeforeProcessing - currentTime;
                }
                lastTime.set(currentTime);
            }
        }
    }

    /**
     * Waits for the robots.txt Crawl-delay and the failure backoff of the origin of a URL,
     * then records the access time. Does nothing without a {@link CrawlerContext} or when the URL has no origin.
     * Threads of the same origin are serialised on its {@link HostState}.
     *
     * @param url the URL about to be fetched
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    protected void delayForHostState(final String url) throws InterruptedException {
        final CrawlerContext crawlerContext = CrawlingParameterUtil.getCrawlerContext();
        if (crawlerContext == null) {
            return;
        }
        final HostState hs = crawlerContext.getHostState(url);
        if (hs == null) {
            return;
        }

        final long maxCrawlDelayMillis = crawlerContext.getMaxCrawlDelayMillis();
        final long crawlDelayMillis = hs.getCrawlDelayMillis();
        if (maxCrawlDelayMillis > 0 && crawlDelayMillis > maxCrawlDelayMillis && hs.markCrawlDelayCapLogged()) {
            logger.info("Crawl-delay {}ms of {} exceeds the maximum; using {}ms", crawlDelayMillis, HostState.toOrigin(url),
                    maxCrawlDelayMillis);
        }

        synchronized (hs) {
            long now = SystemUtil.currentTimeMillis();
            long waitMillis;
            while ((waitMillis = computeWaitMillis(hs, now, maxCrawlDelayMillis)) > 0) {
                hs.wait(waitMillis);
                now = SystemUtil.currentTimeMillis();
            }
            hs.setLastAccessTime(now);
        }
    }

    /**
     * Computes how long to wait before the next access to an origin: the larger of the remaining Crawl-delay
     * (capped at {@code maxCrawlDelayMillis}, counted from the last access) and the remaining failure backoff.
     *
     * @param hs the state of the origin
     * @param now the current time in milliseconds
     * @param maxCrawlDelayMillis the upper limit of the Crawl-delay; 0 or less disables Crawl-delay waiting
     * @return the wait in milliseconds, never negative
     */
    protected long computeWaitMillis(final HostState hs, final long now, final long maxCrawlDelayMillis) {
        long wait = 0L;
        final long lastAccess = hs.getLastAccessTime();
        if (lastAccess != 0 && maxCrawlDelayMillis > 0) {
            final long crawlDelay = Math.min(hs.getCrawlDelayMillis(), maxCrawlDelayMillis);
            if (crawlDelay > 0) {
                wait = lastAccess + crawlDelay - now;
            }
        }
        final long backoffUntil = hs.getBackoffUntil();
        if (backoffUntil != 0) {
            wait = Math.max(wait, backoffUntil - now);
        }
        return Math.max(0L, wait);
    }

}
