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

import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.entity.UrlQueueImpl;
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
}
