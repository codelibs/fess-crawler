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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.core.collection.LruHashSet;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.filter.UrlFilter;
import org.codelibs.fess.crawler.interval.IntervalController;
import org.codelibs.fess.crawler.rule.Rule;
import org.codelibs.fess.crawler.rule.RuleManager;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for CrawlerContext.
 * Tests all functionality including thread safety and concurrent operations.
 */
public class CrawlerContextTest extends PlainTestCase {

    private CrawlerContext crawlerContext;

    @Override
    @BeforeEach
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        crawlerContext = new CrawlerContext();
    }

    /**
     * Test implementation of UrlFilter for testing
     */
    private static class TestUrlFilter implements UrlFilter {
        @Override
        public void init(String sessionId) {
        }

        @Override
        public void addInclude(String urlPattern) {
        }

        @Override
        public void addExclude(String urlPattern) {
        }

        @Override
        public boolean match(String url) {
            return true;
        }

        @Override
        public void processUrl(String url) {
        }

        @Override
        public void clear() {
        }
    }

    /**
     * Test implementation of RuleManager for testing
     */
    private static class TestRuleManager implements RuleManager {
        @Override
        public Rule getRule(ResponseData responseData) {
            return null;
        }

        @Override
        public void addRule(Rule rule) {
        }

        @Override
        public void addRule(int index, Rule rule) {
        }

        @Override
        public boolean removeRule(Rule rule) {
            return false;
        }

        @Override
        public boolean hasRule(Rule rule) {
            return false;
        }
    }

    /**
     * Test implementation of IntervalController for testing
     */
    private static class TestIntervalController implements IntervalController {
        @Override
        public void delay(int type) {
        }

        public void delayBeforeProcessing() {
        }

        public void delayAfterProcessing() {
        }

        public void delayAtNoUrlInQueue() {
        }

        public void delayForWaitingNewUrl() {
        }
    }

    /**
     * Test default constructor
     */
    public void test_constructor() {
        CrawlerContext context = new CrawlerContext();
        assertNotNull(context);
        assertNull(context.getSessionId());
        assertEquals(0, context.getActiveThreadCount());
        assertEquals(0L, context.getAccessCount());
        assertEquals(CrawlerStatus.INITIALIZING, context.getStatus());
        assertNull(context.getUrlFilter());
        assertNull(context.getRuleManager());
        assertNull(context.getIntervalController());
        assertNotNull(context.getRobotsTxtUrlSet());
        assertEquals(10, context.getNumOfThread());
        assertEquals(20, context.getMaxThreadCheckCount());
        assertEquals(-1, context.getMaxDepth());
        assertEquals(0L, context.getMaxAccessCount());
    }

    /**
     * Test sessionId getter and setter
     */
    public void test_sessionId() {
        assertNull(crawlerContext.getSessionId());

        crawlerContext.setSessionId("test-session-001");
        assertEquals("test-session-001", crawlerContext.getSessionId());

        crawlerContext.setSessionId(null);
        assertNull(crawlerContext.getSessionId());

        crawlerContext.setSessionId("");
        assertEquals("", crawlerContext.getSessionId());

        // Test with special characters
        crawlerContext.setSessionId("session-with-特殊文字-#123");
        assertEquals("session-with-特殊文字-#123", crawlerContext.getSessionId());
    }

    /**
     * Test activeThreadCount getter and atomic operations
     */
    public void test_activeThreadCount() {
        assertEquals(0, crawlerContext.getActiveThreadCount());

        // Test increment
        assertEquals(1, crawlerContext.incrementAndGetActiveThreadCount());
        assertEquals(1, crawlerContext.getActiveThreadCount());

        assertEquals(2, crawlerContext.incrementAndGetActiveThreadCount());
        assertEquals(2, crawlerContext.getActiveThreadCount());

        // Test decrement
        assertEquals(1, crawlerContext.decrementAndGetActiveThreadCount());
        assertEquals(1, crawlerContext.getActiveThreadCount());

        assertEquals(0, crawlerContext.decrementAndGetActiveThreadCount());
        assertEquals(0, crawlerContext.getActiveThreadCount());

        // Test decrement below zero
        assertEquals(-1, crawlerContext.decrementAndGetActiveThreadCount());
        assertEquals(-1, crawlerContext.getActiveThreadCount());
    }

    /**
     * Test concurrent activeThreadCount operations
     */
    public void test_activeThreadCount_concurrent() throws Exception {
        final int threadCount = 100;
        final int operationsPerThread = 100;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.await();
                        for (int j = 0; j < operationsPerThread; j++) {
                            crawlerContext.incrementAndGetActiveThreadCount();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        endLatch.countDown();
                    }
                }
            });
        }

        startLatch.countDown();
        endLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(threadCount * operationsPerThread, crawlerContext.getActiveThreadCount());
    }

    /**
     * Test accessCount operations
     */
    public void test_accessCount() {
        assertEquals(0L, crawlerContext.getAccessCount());

        // Test increment
        assertEquals(1L, crawlerContext.incrementAndGetAccessCount());
        assertEquals(1L, crawlerContext.getAccessCount());

        assertEquals(2L, crawlerContext.incrementAndGetAccessCount());
        assertEquals(2L, crawlerContext.getAccessCount());

        // Test decrement
        assertEquals(1L, crawlerContext.decrementAndGetAccessCount());
        assertEquals(1L, crawlerContext.getAccessCount());

        assertEquals(0L, crawlerContext.decrementAndGetAccessCount());
        assertEquals(0L, crawlerContext.getAccessCount());

        // Test decrement below zero
        assertEquals(-1L, crawlerContext.decrementAndGetAccessCount());
        assertEquals(-1L, crawlerContext.getAccessCount());
    }

    /**
     * Test concurrent access count operations
     */
    public void test_accessCount_concurrent() throws Exception {
        final int threadCount = 100;
        final int operationsPerThread = 1000;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.await();
                        for (int j = 0; j < operationsPerThread; j++) {
                            crawlerContext.incrementAndGetAccessCount();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        endLatch.countDown();
                    }
                }
            });
        }

        startLatch.countDown();
        endLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(threadCount * operationsPerThread, crawlerContext.getAccessCount());
    }

    /**
     * Test status getter and setter
     */
    public void test_status() {
        assertEquals(CrawlerStatus.INITIALIZING, crawlerContext.getStatus());

        crawlerContext.setStatus(CrawlerStatus.RUNNING);
        assertEquals(CrawlerStatus.RUNNING, crawlerContext.getStatus());

        crawlerContext.setStatus(CrawlerStatus.DONE);
        assertEquals(CrawlerStatus.DONE, crawlerContext.getStatus());

        crawlerContext.setStatus(null);
        assertNull(crawlerContext.getStatus());
    }

    /**
     * Test urlFilter getter and setter
     */
    public void test_urlFilter() {
        assertNull(crawlerContext.getUrlFilter());

        TestUrlFilter filter = new TestUrlFilter();
        crawlerContext.setUrlFilter(filter);
        assertSame(filter, crawlerContext.getUrlFilter());

        crawlerContext.setUrlFilter(null);
        assertNull(crawlerContext.getUrlFilter());
    }

    /**
     * Test ruleManager getter and setter
     */
    public void test_ruleManager() {
        assertNull(crawlerContext.getRuleManager());

        TestRuleManager manager = new TestRuleManager();
        crawlerContext.setRuleManager(manager);
        assertSame(manager, crawlerContext.getRuleManager());

        crawlerContext.setRuleManager(null);
        assertNull(crawlerContext.getRuleManager());
    }

    /**
     * Test intervalController getter and setter
     */
    public void test_intervalController() {
        assertNull(crawlerContext.getIntervalController());

        TestIntervalController controller = new TestIntervalController();
        crawlerContext.setIntervalController(controller);
        assertSame(controller, crawlerContext.getIntervalController());

        crawlerContext.setIntervalController(null);
        assertNull(crawlerContext.getIntervalController());
    }

    /**
     * Test robotsTxtUrlSet getter and setter
     */
    public void test_robotsTxtUrlSet() {
        Set<String> urlSet = crawlerContext.getRobotsTxtUrlSet();
        assertNotNull(urlSet);
        assertTrue(urlSet instanceof LruHashSet);
        assertTrue(urlSet.isEmpty());

        // Add URLs to default set
        urlSet.add("http://example.com/robots.txt");
        urlSet.add("http://test.com/robots.txt");
        assertEquals(2, crawlerContext.getRobotsTxtUrlSet().size());

        // Set new set
        Set<String> newSet = new HashSet<>();
        newSet.add("http://new.com/robots.txt");
        crawlerContext.setRobotsTxtUrlSet(newSet);
        assertSame(newSet, crawlerContext.getRobotsTxtUrlSet());
        assertEquals(1, crawlerContext.getRobotsTxtUrlSet().size());

        // Set null
        crawlerContext.setRobotsTxtUrlSet(null);
        assertNull(crawlerContext.getRobotsTxtUrlSet());
    }

    /**
     * Test LRU behavior of robotsTxtUrlSet
     */
    public void test_robotsTxtUrlSet_lru() {
        Set<String> urlSet = crawlerContext.getRobotsTxtUrlSet();

        // Add URLs up to capacity (10000)
        for (int i = 0; i < 10000; i++) {
            urlSet.add("http://example" + i + ".com/robots.txt");
        }
        assertEquals(10000, urlSet.size());

        // Add one more should maintain size at 10000 (LRU eviction)
        urlSet.add("http://overflow.com/robots.txt");
        assertEquals(10000, urlSet.size());
        assertTrue(urlSet.contains("http://overflow.com/robots.txt"));
        assertFalse(urlSet.contains("http://example0.com/robots.txt")); // First one should be evicted
    }

    /**
     * Test numOfThread getter and setter
     */
    public void test_numOfThread() {
        assertEquals(10, crawlerContext.getNumOfThread());

        crawlerContext.setNumOfThread(5);
        assertEquals(5, crawlerContext.getNumOfThread());

        crawlerContext.setNumOfThread(0);
        assertEquals(0, crawlerContext.getNumOfThread());

        crawlerContext.setNumOfThread(-1);
        assertEquals(-1, crawlerContext.getNumOfThread());

        crawlerContext.setNumOfThread(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, crawlerContext.getNumOfThread());
    }

    /**
     * Test maxThreadCheckCount getter and setter
     */
    public void test_maxThreadCheckCount() {
        assertEquals(20, crawlerContext.getMaxThreadCheckCount());

        crawlerContext.setMaxThreadCheckCount(50);
        assertEquals(50, crawlerContext.getMaxThreadCheckCount());

        crawlerContext.setMaxThreadCheckCount(0);
        assertEquals(0, crawlerContext.getMaxThreadCheckCount());

        crawlerContext.setMaxThreadCheckCount(-1);
        assertEquals(-1, crawlerContext.getMaxThreadCheckCount());

        crawlerContext.setMaxThreadCheckCount(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, crawlerContext.getMaxThreadCheckCount());
    }

    /**
     * Test maxDepth getter and setter
     */
    public void test_maxDepth() {
        assertEquals(-1, crawlerContext.getMaxDepth());

        crawlerContext.setMaxDepth(5);
        assertEquals(5, crawlerContext.getMaxDepth());

        crawlerContext.setMaxDepth(0);
        assertEquals(0, crawlerContext.getMaxDepth());

        crawlerContext.setMaxDepth(-1);
        assertEquals(-1, crawlerContext.getMaxDepth());

        crawlerContext.setMaxDepth(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, crawlerContext.getMaxDepth());
    }

    /**
     * Test maxAccessCount getter and setter
     */
    public void test_maxAccessCount() {
        assertEquals(0L, crawlerContext.getMaxAccessCount());

        crawlerContext.setMaxAccessCount(1000L);
        assertEquals(1000L, crawlerContext.getMaxAccessCount());

        crawlerContext.setMaxAccessCount(0L);
        assertEquals(0L, crawlerContext.getMaxAccessCount());

        crawlerContext.setMaxAccessCount(-1L);
        assertEquals(-1L, crawlerContext.getMaxAccessCount());

        crawlerContext.setMaxAccessCount(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, crawlerContext.getMaxAccessCount());
    }

    /**
     * Test sitemaps add and remove operations
     */
    public void test_sitemaps() {
        // Initial state
        assertNull(crawlerContext.removeSitemaps());

        // Add sitemaps
        String[] sitemaps = new String[] { "http://example.com/sitemap.xml", "http://test.com/sitemap.xml" };
        crawlerContext.addSitemaps(sitemaps);

        // Remove and verify
        String[] removedSitemaps = crawlerContext.removeSitemaps();
        assertNotNull(removedSitemaps);
        assertEquals(2, removedSitemaps.length);
        assertEquals("http://example.com/sitemap.xml", removedSitemaps[0]);
        assertEquals("http://test.com/sitemap.xml", removedSitemaps[1]);

        // Should be null after removal
        assertNull(crawlerContext.removeSitemaps());

        // Test with null
        crawlerContext.addSitemaps(null);
        assertNull(crawlerContext.removeSitemaps());

        // Test with empty array
        crawlerContext.addSitemaps(new String[0]);
        String[] emptyArray = crawlerContext.removeSitemaps();
        assertNotNull(emptyArray);
        assertEquals(0, emptyArray.length);
    }

    /**
     * Test thread-local nature of sitemaps
     */
    public void test_sitemaps_threadLocal() throws Exception {
        final String[] thread1Sitemaps = new String[] { "http://thread1.com/sitemap.xml" };
        final String[] thread2Sitemaps = new String[] { "http://thread2.com/sitemap.xml" };
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(2);
        final AtomicInteger successCount = new AtomicInteger(0);

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startLatch.await();
                    crawlerContext.addSitemaps(thread1Sitemaps);
                    String[] retrieved = crawlerContext.removeSitemaps();
                    if (retrieved != null && retrieved.length == 1 && "http://thread1.com/sitemap.xml".equals(retrieved[0])) {
                        successCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startLatch.await();
                    crawlerContext.addSitemaps(thread2Sitemaps);
                    String[] retrieved = crawlerContext.removeSitemaps();
                    if (retrieved != null && retrieved.length == 1 && "http://thread2.com/sitemap.xml".equals(retrieved[0])) {
                        successCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }
        });

        thread1.start();
        thread2.start();
        startLatch.countDown();
        endLatch.await(5, TimeUnit.SECONDS);

        assertEquals(2, successCount.get());
    }

    /**
     * Test concurrent operations on different fields
     */
    public void test_concurrentOperations() throws Exception {
        final int threadCount = 10;
        final int operationsPerThread = 100;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        final List<Exception> exceptions = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.await();
                        for (int j = 0; j < operationsPerThread; j++) {
                            // Perform various operations
                            crawlerContext.setSessionId("session-" + threadId + "-" + j);
                            crawlerContext.incrementAndGetActiveThreadCount();
                            crawlerContext.decrementAndGetActiveThreadCount();
                            crawlerContext.incrementAndGetAccessCount();
                            crawlerContext.setStatus(CrawlerStatus.RUNNING);
                            crawlerContext.setNumOfThread(threadId);
                            crawlerContext.setMaxDepth(j);
                            crawlerContext.setMaxAccessCount(threadId * 1000L + j);

                            // Add and remove sitemaps
                            String[] sitemaps = new String[] { "http://thread" + threadId + ".com/sitemap" + j + ".xml" };
                            crawlerContext.addSitemaps(sitemaps);
                            crawlerContext.removeSitemaps();

                            // Access robots.txt URL set
                            crawlerContext.getRobotsTxtUrlSet().add("http://thread" + threadId + ".com/robots.txt");
                        }
                    } catch (Exception e) {
                        synchronized (exceptions) {
                            exceptions.add(e);
                        }
                    } finally {
                        endLatch.countDown();
                    }
                }
            });
        }

        startLatch.countDown();
        endLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(exceptions.isEmpty());
        assertEquals(threadCount * operationsPerThread, crawlerContext.getAccessCount());
    }

    /**
     * Test complete workflow scenario
     */
    public void test_completeWorkflow() {
        // Initialize context
        crawlerContext.setSessionId("workflow-session");
        crawlerContext.setStatus(CrawlerStatus.INITIALIZING);
        crawlerContext.setNumOfThread(5);
        crawlerContext.setMaxDepth(3);
        crawlerContext.setMaxAccessCount(1000L);
        crawlerContext.setMaxThreadCheckCount(30);

        // Set components
        crawlerContext.setUrlFilter(new TestUrlFilter());
        crawlerContext.setRuleManager(new TestRuleManager());
        crawlerContext.setIntervalController(new TestIntervalController());

        // Start crawling
        crawlerContext.setStatus(CrawlerStatus.RUNNING);
        // Simulate 5 threads starting
        for (int i = 0; i < 5; i++) {
            crawlerContext.incrementAndGetActiveThreadCount();
        }
        assertEquals(5, crawlerContext.getActiveThreadCount());

        // Simulate crawling progress
        for (int i = 0; i < 100; i++) {
            crawlerContext.incrementAndGetAccessCount();
            crawlerContext.getRobotsTxtUrlSet().add("http://site" + i + ".com/robots.txt");
        }

        // Add sitemaps
        crawlerContext.addSitemaps(new String[] { "http://example.com/sitemap.xml" });

        // Complete crawling - all threads finish
        for (int i = 0; i < 5; i++) {
            crawlerContext.decrementAndGetActiveThreadCount();
        }
        crawlerContext.setStatus(CrawlerStatus.DONE);

        // Verify final state
        assertEquals("workflow-session", crawlerContext.getSessionId());
        assertEquals(CrawlerStatus.DONE, crawlerContext.getStatus());
        assertEquals(0, crawlerContext.getActiveThreadCount());
        assertEquals(100L, crawlerContext.getAccessCount());
        assertEquals(100, crawlerContext.getRobotsTxtUrlSet().size());
        assertNotNull(crawlerContext.removeSitemaps());
    }

    /**
     * Test boundary values
     */
    public void test_boundaryValues() {
        // Test with maximum values
        crawlerContext.setNumOfThread(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, crawlerContext.getNumOfThread());

        crawlerContext.setMaxThreadCheckCount(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, crawlerContext.getMaxThreadCheckCount());

        crawlerContext.setMaxDepth(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, crawlerContext.getMaxDepth());

        crawlerContext.setMaxAccessCount(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, crawlerContext.getMaxAccessCount());

        // Test with minimum values
        crawlerContext.setNumOfThread(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, crawlerContext.getNumOfThread());

        crawlerContext.setMaxThreadCheckCount(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, crawlerContext.getMaxThreadCheckCount());

        crawlerContext.setMaxDepth(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, crawlerContext.getMaxDepth());

        crawlerContext.setMaxAccessCount(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, crawlerContext.getMaxAccessCount());
    }

    /**
     * Test access count atomic operations
     */
    public void test_accessCountAtomicOperations() {
        // Verify atomic nature by checking return values
        assertEquals(1L, crawlerContext.incrementAndGetAccessCount());
        assertEquals(2L, crawlerContext.incrementAndGetAccessCount());
        assertEquals(3L, crawlerContext.incrementAndGetAccessCount());

        assertEquals(2L, crawlerContext.decrementAndGetAccessCount());
        assertEquals(1L, crawlerContext.decrementAndGetAccessCount());

        // Test large increments
        for (int i = 0; i < 1000; i++) {
            crawlerContext.incrementAndGetAccessCount();
        }
        assertEquals(1001L, crawlerContext.getAccessCount());

        // Test large decrements
        for (int i = 0; i < 500; i++) {
            crawlerContext.decrementAndGetAccessCount();
        }
        assertEquals(501L, crawlerContext.getAccessCount());
    }

    /**
     * Test volatile status field behavior
     */
    public void test_statusVolatile() throws Exception {
        final CountDownLatch statusSetLatch = new CountDownLatch(1);
        final CountDownLatch statusReadLatch = new CountDownLatch(1);
        final AtomicInteger successCount = new AtomicInteger(0);

        crawlerContext.setStatus(CrawlerStatus.INITIALIZING);

        Thread writer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    statusSetLatch.await();
                    crawlerContext.setStatus(CrawlerStatus.DONE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    statusSetLatch.countDown();
                    // Poll for status change instead of fixed sleep
                    long startTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startTime < 500) {
                        if (CrawlerStatus.DONE == crawlerContext.getStatus()) {
                            successCount.incrementAndGet();
                            break;
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    statusReadLatch.countDown();
                } catch (Exception e) {
                    statusReadLatch.countDown();
                }
            }
        });

        writer.start();
        reader.start();

        statusReadLatch.await(5, TimeUnit.SECONDS);

        assertEquals(1, successCount.get());
        assertEquals(CrawlerStatus.DONE, crawlerContext.getStatus());
    }
}
