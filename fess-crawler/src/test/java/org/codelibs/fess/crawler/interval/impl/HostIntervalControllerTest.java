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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.codelibs.core.lang.SystemUtil;
import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.entity.HostState;
import org.codelibs.fess.crawler.entity.HostState.RobotsTxtStatus;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.entity.UrlQueueImpl;
import org.codelibs.fess.crawler.interval.IntervalController;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;
import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author hayato
 *
 */
public class HostIntervalControllerTest extends PlainTestCase {

    /**
     * Test that crawling intervals for the same host work correctly.
     */
    @Test
    public void test_delayBeforeProcessing() {
        // Number of concurrent tasks
        final int numTasks = 100;
        // Interval in milliseconds
        final Long waittime = 100L;

        CrawlingParameterUtil.setUrlQueue(new UrlQueueImpl());
        final UrlQueue q = CrawlingParameterUtil.getUrlQueue();
        for (int i = 0; i < numTasks; i++) {
            q.setUrl("http://example.com");
        }

        final HostIntervalController controller = new HostIntervalController();
        controller.delayMillisBeforeProcessing = waittime;
        controller.delayMillisAfterProcessing = 0L;
        controller.delayMillisForWaitingNewUrl = 0L;
        controller.delayMillisAtNoUrlInQueue = 0L;

        final Callable<Integer> testCallable = new Callable<Integer>() {
            public Integer call() throws Exception {
                CrawlingParameterUtil.setUrlQueue(q);
                controller.delayBeforeProcessing();
                return 0;
            }
        };

        // Generate multiple callable tasks
        final List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();
        for (int i = 0; i < numTasks; i++) {
            tasks.add(testCallable);
        }

        // Get start time
        final long time = System.nanoTime();

        // Execute callable tasks concurrently
        final ExecutorService executor = Executors.newFixedThreadPool(numTasks);
        try {
            final List<Future<Integer>> futures = executor.invokeAll(tasks);
            for (final Future<Integer> future : futures) {
                future.get();
            }
        } catch (final InterruptedException e) {
            // Interrupted while waiting
        } catch (final ExecutionException e) {
            // Execution failed
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (final InterruptedException e) {
                executor.shutdownNow();
            }
        }

        long elapsed = (System.nanoTime() - time) / 1000000;
        long wait = waittime * (numTasks - 1);
        assertTrue(elapsed + 1L >= wait);
    }

    /**
     * Test that different hosts can be accessed concurrently without delay
     */
    @Test
    public void test_multipleHosts_concurrent() {
        final int numTasks = 10;
        final Long waittime = 100L;

        final HostIntervalController controller = new HostIntervalController();
        controller.delayMillisBeforeProcessing = waittime;
        controller.delayMillisAfterProcessing = 0L;
        controller.delayMillisForWaitingNewUrl = 0L;
        controller.delayMillisAtNoUrlInQueue = 0L;

        final List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();
        for (int i = 0; i < numTasks; i++) {
            final int index = i;
            tasks.add(new Callable<Integer>() {
                public Integer call() throws Exception {
                    final UrlQueue q = new UrlQueueImpl();
                    q.setUrl("http://example" + index + ".com");
                    CrawlingParameterUtil.setUrlQueue(q);
                    controller.delayBeforeProcessing();
                    return 0;
                }
            });
        }

        // Get start time
        final long time = System.nanoTime();

        // Execute callable tasks concurrently
        final ExecutorService executor = Executors.newFixedThreadPool(numTasks);
        try {
            final List<Future<Integer>> futures = executor.invokeAll(tasks);
            for (final Future<Integer> future : futures) {
                future.get();
            }
        } catch (final InterruptedException e) {
            // Interrupted while waiting
        } catch (final ExecutionException e) {
            // Execution failed
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (final InterruptedException e) {
                executor.shutdownNow();
            }
        }

        long elapsed = (System.nanoTime() - time) / 1000000;
        // Different hosts should NOT wait for each other
        assertTrue(elapsed < waittime * numTasks / 2);
    }

    /**
     * Test that file:// URLs are not delayed
     */
    @Test
    public void test_fileUrl_noDelay() {
        final HostIntervalController controller = new HostIntervalController();
        controller.delayMillisBeforeProcessing = 1000L;

        final UrlQueue q = new UrlQueueImpl();
        q.setUrl("file:///path/to/file.txt");
        CrawlingParameterUtil.setUrlQueue(q);

        final long start = System.nanoTime();
        controller.delayBeforeProcessing();
        final long elapsed = (System.nanoTime() - start) / 1000000;

        assertTrue(elapsed < 50);
    }

    /**
     * Test that null URL queue is handled gracefully
     */
    @Test
    public void test_nullUrlQueue() {
        final HostIntervalController controller = new HostIntervalController();
        controller.delayMillisBeforeProcessing = 1000L;

        CrawlingParameterUtil.setUrlQueue(null);

        final long start = System.nanoTime();
        controller.delayBeforeProcessing();
        final long elapsed = (System.nanoTime() - start) / 1000000;

        assertTrue(elapsed < 50);
    }

    /**
     * Test that blank URL is handled gracefully
     */
    @Test
    public void test_blankUrl() {
        final HostIntervalController controller = new HostIntervalController();
        controller.delayMillisBeforeProcessing = 1000L;

        final UrlQueue q = new UrlQueueImpl();
        q.setUrl("");
        CrawlingParameterUtil.setUrlQueue(q);

        final long start = System.nanoTime();
        controller.delayBeforeProcessing();
        final long elapsed = (System.nanoTime() - start) / 1000000;

        assertTrue(elapsed < 50);
    }

    /**
     * Test that URL without host is handled gracefully
     */
    @Test
    public void test_urlWithoutHost() {
        final HostIntervalController controller = new HostIntervalController();
        controller.delayMillisBeforeProcessing = 1000L;

        final UrlQueue q = new UrlQueueImpl();
        q.setUrl("mailto:test@example.com");
        CrawlingParameterUtil.setUrlQueue(q);

        final long start = System.nanoTime();
        controller.delayBeforeProcessing();
        final long elapsed = (System.nanoTime() - start) / 1000000;

        assertTrue(elapsed < 50);
    }

    /**
     * Test constructor with parameters
     */
    @Test
    public void test_constructorWithParams() {
        final Map<String, Long> params = new HashMap<>();
        params.put("delayMillisBeforeProcessing", 200L);

        final HostIntervalController controller = new HostIntervalController(params);

        assertEquals(200L, controller.getDelayMillisBeforeProcessing());
    }

    /**
     * Test that second access to same host is delayed
     */
    @Test
    public void test_sameHost_sequentialAccess() {
        final HostIntervalController controller = new HostIntervalController();
        controller.delayMillisBeforeProcessing = 100L;

        final UrlQueue q = new UrlQueueImpl();
        q.setUrl("http://example.com/page1");
        CrawlingParameterUtil.setUrlQueue(q);

        // First access - should not delay
        final long start1 = System.nanoTime();
        controller.delayBeforeProcessing();
        final long elapsed1 = (System.nanoTime() - start1) / 1000000;
        assertTrue(elapsed1 < 50);

        // Second access to same host - should delay
        q.setUrl("http://example.com/page2");
        final long start2 = System.nanoTime();
        controller.delayBeforeProcessing();
        final long elapsed2 = (System.nanoTime() - start2) / 1000000;
        assertTrue(elapsed2 >= 90);
    }

    /**
     * Test that cache is thread-safe with concurrent access
     */
    @Test
    public void test_cacheThreadSafety() {
        final int numTasks = 50;
        final Long waittime = 50L;

        final HostIntervalController controller = new HostIntervalController();
        controller.delayMillisBeforeProcessing = waittime;

        final List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();
        // Mix of same and different hosts
        for (int i = 0; i < numTasks; i++) {
            final int index = i;
            tasks.add(new Callable<Integer>() {
                public Integer call() throws Exception {
                    final UrlQueue q = new UrlQueueImpl();
                    // Use modulo to create multiple accesses to same hosts
                    q.setUrl("http://host" + (index % 5) + ".com/page" + index);
                    CrawlingParameterUtil.setUrlQueue(q);
                    controller.delayBeforeProcessing();
                    return 0;
                }
            });
        }

        final ExecutorService executor = Executors.newFixedThreadPool(numTasks);
        try {
            final List<Future<Integer>> futures = executor.invokeAll(tasks);
            for (final Future<Integer> future : futures) {
                future.get();
            }
            // If we reach here without exceptions, thread-safety is maintained
            assertTrue(true);
        } catch (final InterruptedException e) {
            fail();
        } catch (final ExecutionException e) {
            fail();
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(30, java.util.concurrent.TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (final InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }

    /**
     * Test that URLs with brackets in the path are handled gracefully
     */
    // Regression tests for topic/2732: special characters in URLs for host extraction

    // Regression tests for topic/2732: special characters in URLs for host extraction
    // HostIntervalController uses new URI(url).getHost(), which throws for invalid URIs.
    // The per-host delay is skipped for such URLs instead of failing the delay.

    @Test
    public void test_getHost_withBracketsInPath() {
        final HostIntervalController controller = new HostIntervalController();
        controller.delayMillisBeforeProcessing = 100L;
        controller.delayMillisAfterProcessing = 0L;
        controller.delayMillisForWaitingNewUrl = 0L;
        controller.delayMillisAtNoUrlInQueue = 0L;

        final UrlQueue q = new UrlQueueImpl();
        q.setUrl("http://example.com/[test]/page");
        CrawlingParameterUtil.setUrlQueue(q);

        controller.delayBeforeProcessing();
    }

    @Test
    public void test_getHost_withPercentInPath() {
        final HostIntervalController controller = new HostIntervalController();
        controller.delayMillisBeforeProcessing = 100L;
        controller.delayMillisAfterProcessing = 0L;
        controller.delayMillisForWaitingNewUrl = 0L;
        controller.delayMillisAtNoUrlInQueue = 0L;

        final UrlQueue q = new UrlQueueImpl();
        q.setUrl("http://example.com/100%25done");
        CrawlingParameterUtil.setUrlQueue(q);

        controller.delayBeforeProcessing();
    }

    @Test
    public void test_getHost_withInvalidUri() {
        final HostIntervalController controller = new HostIntervalController();
        controller.delayMillisBeforeProcessing = 100L;
        controller.delayMillisAfterProcessing = 0L;
        controller.delayMillisForWaitingNewUrl = 0L;
        controller.delayMillisAtNoUrlInQueue = 0L;

        final UrlQueue q = new UrlQueueImpl();
        q.setUrl("http://example.com/path with spaces/[and brackets]");
        CrawlingParameterUtil.setUrlQueue(q);

        controller.delayBeforeProcessing();
    }

    private static final long NOW = 1_000_000L;

    @Override
    protected void tearDown(final org.junit.jupiter.api.TestInfo testInfo) throws Exception {
        SystemUtil.setTimeProvider(null);
        CrawlingParameterUtil.setCrawlerContext(null);
        CrawlingParameterUtil.setUrlQueue(null);
        super.tearDown(testInfo);
    }

    @Test
    public void test_computeWaitMillis_firstAccess() {
        final HostIntervalController controller = new HostIntervalController();
        final HostState hs = new HostState();
        hs.setRobotsTxt(RobotsTxtStatus.PARSED, null, 2000L);
        assertEquals(0L, controller.computeWaitMillis(hs, NOW, 60000L));
    }

    @Test
    public void test_computeWaitMillis_crawlDelay() {
        final HostIntervalController controller = new HostIntervalController();
        final HostState hs = new HostState();
        hs.setRobotsTxt(RobotsTxtStatus.PARSED, null, 2000L);
        hs.setLastAccessTime(NOW - 500L);
        assertEquals(1500L, controller.computeWaitMillis(hs, NOW, 60000L));
        // already elapsed
        hs.setLastAccessTime(NOW - 5000L);
        assertEquals(0L, controller.computeWaitMillis(hs, NOW, 60000L));
    }

    @Test
    public void test_computeWaitMillis_crawlDelayCapped() {
        final HostIntervalController controller = new HostIntervalController();
        final HostState hs = new HostState();
        hs.setRobotsTxt(RobotsTxtStatus.PARSED, null, 120000L);
        hs.setLastAccessTime(NOW);
        assertEquals(60000L, controller.computeWaitMillis(hs, NOW, 60000L));
        // absurd value must not overflow
        hs.setRobotsTxt(RobotsTxtStatus.PARSED, null, Long.MAX_VALUE);
        assertEquals(60000L, controller.computeWaitMillis(hs, NOW, 60000L));
    }

    @Test
    public void test_computeWaitMillis_crawlDelayDisabledByMax() {
        final HostIntervalController controller = new HostIntervalController();
        final HostState hs = new HostState();
        hs.setRobotsTxt(RobotsTxtStatus.PARSED, null, 2000L);
        hs.setLastAccessTime(NOW);
        assertEquals(0L, controller.computeWaitMillis(hs, NOW, 0L));
        assertEquals(0L, controller.computeWaitMillis(hs, NOW, -1L));
    }

    @Test
    public void test_computeWaitMillis_backoff() {
        final HostIntervalController controller = new HostIntervalController();
        final HostState hs = new HostState();
        hs.recordFailure(NOW, 3000L, 10000L, 300000L);
        // backoff applies even on the first access
        assertEquals(3000L, controller.computeWaitMillis(hs, NOW, 60000L));
        // elapsed backoff never gives a negative wait
        assertEquals(0L, controller.computeWaitMillis(hs, NOW + 10000L, 60000L));
    }

    @Test
    public void test_computeWaitMillis_crawlDelayAndBackoff() {
        final HostIntervalController controller = new HostIntervalController();
        final HostState hs = new HostState();
        hs.setRobotsTxt(RobotsTxtStatus.PARSED, null, 2000L);
        hs.setLastAccessTime(NOW);
        hs.recordFailure(NOW, 3000L, 10000L, 300000L);
        assertEquals(3000L, controller.computeWaitMillis(hs, NOW, 60000L));
        hs.setLastAccessTime(NOW + 1500L);
        // crawl delay (2000) now exceeds the remaining backoff (1500)
        assertEquals(2000L, controller.computeWaitMillis(hs, NOW + 1500L, 60000L));
    }

    @Test
    public void test_delayBeforeProcessing_waitsForCrawlDelay() {
        final CrawlerContext context = new CrawlerContext();
        final String url = "http://crawldelay.example.com/a";
        context.getHostState(url).setRobotsTxt(RobotsTxtStatus.PARSED, null, 200L);
        CrawlingParameterUtil.setCrawlerContext(context);
        final UrlQueue<?> q = new UrlQueueImpl<>();
        q.setUrl(url);
        CrawlingParameterUtil.setUrlQueue(q);

        final HostIntervalController controller = new HostIntervalController();
        controller.delayMillisBeforeProcessing = 0L;

        controller.delayBeforeProcessing();
        final long start = System.nanoTime();
        controller.delayBeforeProcessing();
        final long elapsed = (System.nanoTime() - start) / 1000000;

        assertTrue(elapsed >= 150L);
        assertTrue(elapsed < 5000L);
    }

    /** URLs that java.net.URI rejects but that reach the queue (HtmlTransformer keeps [, ] and % as they are). */
    private static final String[] UNPARSEABLE_URLS =
            { "http://brackets.example.com/a[1].html", "http://percent.example.com/100%.html", "smb://host/share/My Documents/a b.pdf" };

    @Test
    public void test_delay_unparseableUrlWaitsForCrawlDelay() {
        for (final String url : UNPARSEABLE_URLS) {
            final CrawlerContext context = new CrawlerContext();
            context.getHostState(url).setRobotsTxt(RobotsTxtStatus.PARSED, null, 200L);
            CrawlingParameterUtil.setCrawlerContext(context);
            final UrlQueue<?> q = new UrlQueueImpl<>();
            q.setUrl(url);
            CrawlingParameterUtil.setUrlQueue(q);

            final HostIntervalController controller = new HostIntervalController();
            controller.delayMillisBeforeProcessing = 0L;

            controller.delay(IntervalController.PRE_PROCESSING);
            assertTrue(context.getHostState(url).getLastAccessTime() > 0L);
            final long start = System.nanoTime();
            controller.delay(IntervalController.PRE_PROCESSING);
            final long elapsed = (System.nanoTime() - start) / 1000000;

            assertTrue(elapsed >= 150L);
            assertTrue(elapsed < 5000L);
        }
    }

    @Test
    public void test_delay_unparseableUrlWaitsForBackoff() {
        for (final String url : UNPARSEABLE_URLS) {
            final CrawlerContext context = new CrawlerContext();
            context.getHostState(url).recordFailure(System.currentTimeMillis(), 200L, 10000L, 300000L);
            CrawlingParameterUtil.setCrawlerContext(context);
            final UrlQueue<?> q = new UrlQueueImpl<>();
            q.setUrl(url);
            CrawlingParameterUtil.setUrlQueue(q);

            final HostIntervalController controller = new HostIntervalController();
            controller.delayMillisBeforeProcessing = 0L;

            final long start = System.nanoTime();
            controller.delay(IntervalController.PRE_PROCESSING);
            final long elapsed = (System.nanoTime() - start) / 1000000;

            assertTrue(elapsed >= 150L);
            assertTrue(elapsed < 5000L);
        }
    }

    @Test
    public void test_delayBeforeProcessing_noContext() {
        final String url = "http://nocontext.example.com/a";
        final UrlQueue<?> q = new UrlQueueImpl<>();
        q.setUrl(url);
        CrawlingParameterUtil.setUrlQueue(q);
        CrawlingParameterUtil.setCrawlerContext(null);

        final HostIntervalController controller = new HostIntervalController();
        controller.delayMillisBeforeProcessing = 0L;
        controller.delayBeforeProcessing();
        controller.delayBeforeProcessing();
    }

    @Test
    public void test_delayBeforeProcessing_fileUrlWithContext() {
        final CrawlerContext context = new CrawlerContext();
        CrawlingParameterUtil.setCrawlerContext(context);
        final UrlQueue<?> q = new UrlQueueImpl<>();
        q.setUrl("file:///path/to/file.txt");
        CrawlingParameterUtil.setUrlQueue(q);

        final HostIntervalController controller = new HostIntervalController();
        controller.delayMillisBeforeProcessing = 0L;
        controller.delayBeforeProcessing();
        assertTrue(context.getHostStateMap().isEmpty());
    }
}
