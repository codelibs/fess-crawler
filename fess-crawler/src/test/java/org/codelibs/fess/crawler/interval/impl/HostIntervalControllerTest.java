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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.entity.UrlQueueImpl;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author hayato
 *
 */
public class HostIntervalControllerTest extends PlainTestCase {

    /**
     * Test that crawling intervals for the same host work correctly.
     */
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
        assertTrue(elapsed + " >= " + wait, elapsed + 1L >= wait);
    }
}
