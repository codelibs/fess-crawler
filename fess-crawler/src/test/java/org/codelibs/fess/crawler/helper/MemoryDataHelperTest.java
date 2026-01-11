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
package org.codelibs.fess.crawler.helper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.codelibs.fess.crawler.entity.AccessResultImpl;
import org.codelibs.fess.crawler.entity.UrlQueueImpl;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Test class for MemoryDataHelper.
 * Tests thread-safety of ConcurrentHashMap operations and urlInQueueSet functionality.
 */
public class MemoryDataHelperTest extends PlainTestCase {

    private MemoryDataHelper helper;

    @Override
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        helper = new MemoryDataHelper();
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        helper.clear();
        super.tearDown();
    }

    /**
     * Test getUrlQueueList creates queue lazily
     */
    public void test_getUrlQueueList() {
        String sessionId = "session1";

        // First call should create a new queue
        Queue<UrlQueueImpl<Long>> queue1 = helper.getUrlQueueList(sessionId);
        assertNotNull(queue1);
        assertTrue(queue1.isEmpty());

        // Second call should return the same queue
        Queue<UrlQueueImpl<Long>> queue2 = helper.getUrlQueueList(sessionId);
        assertSame(queue1, queue2);

        // Different session should get a different queue
        Queue<UrlQueueImpl<Long>> queue3 = helper.getUrlQueueList("session2");
        assertNotSame(queue1, queue3);
    }

    /**
     * Test getUrlInQueueSet creates set lazily
     */
    public void test_getUrlInQueueSet() {
        String sessionId = "session1";

        // First call should create a new set
        Set<String> set1 = helper.getUrlInQueueSet(sessionId);
        assertNotNull(set1);
        assertTrue(set1.isEmpty());

        // Second call should return the same set
        Set<String> set2 = helper.getUrlInQueueSet(sessionId);
        assertSame(set1, set2);

        // Different session should get a different set
        Set<String> set3 = helper.getUrlInQueueSet("session2");
        assertNotSame(set1, set3);
    }

    /**
     * Test addUrlQueueList adds to queue and urlInQueueSet
     */
    public void test_addUrlQueueList() {
        String sessionId = "session1";
        Queue<UrlQueueImpl<Long>> urlQueueList = new LinkedList<>();

        UrlQueueImpl<Long> url1 = new UrlQueueImpl<>();
        url1.setUrl("https://example.com/page1");
        urlQueueList.add(url1);

        UrlQueueImpl<Long> url2 = new UrlQueueImpl<>();
        url2.setUrl("https://example.com/page2");
        urlQueueList.add(url2);

        helper.addUrlQueueList(sessionId, urlQueueList);

        Queue<UrlQueueImpl<Long>> resultQueue = helper.getUrlQueueList(sessionId);
        assertEquals(2, resultQueue.size());

        Set<String> urlSet = helper.getUrlInQueueSet(sessionId);
        assertEquals(2, urlSet.size());
        assertTrue(urlSet.contains("https://example.com/page1"));
        assertTrue(urlSet.contains("https://example.com/page2"));
    }

    /**
     * Test removeUrlQueueList removes both queue and urlInQueueSet
     */
    public void test_removeUrlQueueList() {
        String sessionId = "session1";

        // Add some data
        Queue<UrlQueueImpl<Long>> queue = helper.getUrlQueueList(sessionId);
        UrlQueueImpl<Long> url = new UrlQueueImpl<>();
        url.setUrl("https://example.com");
        queue.add(url);
        helper.getUrlInQueueSet(sessionId).add("https://example.com");

        // Remove
        helper.removeUrlQueueList(sessionId);

        // Verify new queue and set are created
        Queue<UrlQueueImpl<Long>> newQueue = helper.getUrlQueueList(sessionId);
        assertTrue(newQueue.isEmpty());

        Set<String> newSet = helper.getUrlInQueueSet(sessionId);
        assertTrue(newSet.isEmpty());
    }

    /**
     * Test getAccessResultMap creates map lazily and returns ConcurrentHashMap
     */
    public void test_getAccessResultMap() {
        String sessionId = "session1";

        // First call should create a new map
        Map<String, AccessResultImpl<Long>> map1 = helper.getAccessResultMap(sessionId);
        assertNotNull(map1);
        assertTrue(map1.isEmpty());

        // Second call should return the same map
        Map<String, AccessResultImpl<Long>> map2 = helper.getAccessResultMap(sessionId);
        assertSame(map1, map2);

        // Verify it's a ConcurrentHashMap
        assertTrue(map1 instanceof java.util.concurrent.ConcurrentHashMap);
    }

    /**
     * Test concurrent access to getUrlQueueList
     */
    public void test_concurrentGetUrlQueueList() throws Exception {
        final int threadCount = 20;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        final List<Queue<UrlQueueImpl<Long>>> queues = new ArrayList<>();
        final List<Throwable> errors = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    Queue<UrlQueueImpl<Long>> queue = helper.getUrlQueueList("concurrent-session");
                    synchronized (queues) {
                        queues.add(queue);
                    }
                } catch (Throwable e) {
                    synchronized (errors) {
                        errors.add(e);
                    }
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(endLatch.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        assertTrue("Errors occurred: " + errors, errors.isEmpty());
        assertEquals(threadCount, queues.size());

        // All threads should get the same queue instance
        Queue<UrlQueueImpl<Long>> firstQueue = queues.get(0);
        for (Queue<UrlQueueImpl<Long>> queue : queues) {
            assertSame(firstQueue, queue);
        }
    }

    /**
     * Test concurrent access to getAccessResultMap
     */
    public void test_concurrentGetAccessResultMap() throws Exception {
        final int threadCount = 20;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        final List<Map<String, AccessResultImpl<Long>>> maps = new ArrayList<>();
        final List<Throwable> errors = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    Map<String, AccessResultImpl<Long>> map = helper.getAccessResultMap("concurrent-session");
                    synchronized (maps) {
                        maps.add(map);
                    }
                } catch (Throwable e) {
                    synchronized (errors) {
                        errors.add(e);
                    }
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(endLatch.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        assertTrue("Errors occurred: " + errors, errors.isEmpty());
        assertEquals(threadCount, maps.size());

        // All threads should get the same map instance
        Map<String, AccessResultImpl<Long>> firstMap = maps.get(0);
        for (Map<String, AccessResultImpl<Long>> map : maps) {
            assertSame(firstMap, map);
        }
    }

    /**
     * Test concurrent writes to access result map
     */
    public void test_concurrentWriteToAccessResultMap() throws Exception {
        final int threadCount = 10;
        final int operationsPerThread = 100;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        final List<Throwable> errors = new ArrayList<>();

        Map<String, AccessResultImpl<Long>> map = helper.getAccessResultMap("concurrent-session");
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < operationsPerThread; i++) {
                        AccessResultImpl<Long> result = new AccessResultImpl<>();
                        result.setUrl("https://example.com/" + threadId + "/" + i);
                        map.put(result.getUrl(), result);
                    }
                } catch (Throwable e) {
                    synchronized (errors) {
                        errors.add(e);
                    }
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(endLatch.await(30, TimeUnit.SECONDS));
        executor.shutdown();

        assertTrue("Errors occurred: " + errors, errors.isEmpty());
        assertEquals(threadCount * operationsPerThread, map.size());
    }

    /**
     * Test clear method clears all data
     */
    public void test_clear() {
        // Add data
        helper.getUrlQueueList("session1").add(new UrlQueueImpl<>());
        helper.getUrlInQueueSet("session1").add("https://example.com");
        helper.getAccessResultMap("session1").put("url", new AccessResultImpl<>());

        // Clear
        helper.clear();

        // Verify new collections are created (empty)
        assertTrue(helper.getUrlQueueList("session1").isEmpty());
        assertTrue(helper.getUrlInQueueSet("session1").isEmpty());
        assertTrue(helper.getAccessResultMap("session1").isEmpty());
    }

    /**
     * Test URL pattern methods
     */
    public void test_urlPatterns() {
        String sessionId = "session1";

        // Add include patterns
        helper.addIncludeUrlPattern(sessionId, "https://example\\.com/.*");
        List<Pattern> includePatterns = helper.getIncludeUrlPatternList(sessionId);
        assertEquals(1, includePatterns.size());
        assertTrue(includePatterns.get(0).matcher("https://example.com/page").matches());

        // Add exclude patterns
        helper.addExcludeUrlPattern(sessionId, ".*\\.pdf$");
        List<Pattern> excludePatterns = helper.getExcludeUrlPatternList(sessionId);
        assertEquals(1, excludePatterns.size());
        assertTrue(excludePatterns.get(0).matcher("document.pdf").matches());

        // Clear patterns for session
        helper.clearUrlPattern(sessionId);
        assertTrue(helper.getIncludeUrlPatternList(sessionId).isEmpty());
        assertTrue(helper.getExcludeUrlPatternList(sessionId).isEmpty());
    }

    /**
     * Test getAccessResultList across sessions
     */
    public void test_getAccessResultList() {
        String url = "https://example.com/shared";

        // Add access result to session1
        AccessResultImpl<Long> result1 = new AccessResultImpl<>();
        result1.setUrl(url);
        result1.setSessionId("session1");
        helper.getAccessResultMap("session1").put(url, result1);

        // Add access result to session2
        AccessResultImpl<Long> result2 = new AccessResultImpl<>();
        result2.setUrl(url);
        result2.setSessionId("session2");
        helper.getAccessResultMap("session2").put(url, result2);

        // Get all access results for the URL
        List<AccessResultImpl<Long>> results = helper.getAccessResultList(url);
        assertEquals(2, results.size());
    }

    /**
     * Test deleteAccessResultMap
     */
    public void test_deleteAccessResultMap() {
        String sessionId = "session1";

        // Add data
        helper.getAccessResultMap(sessionId).put("url", new AccessResultImpl<>());

        // Delete
        helper.deleteAccessResultMap(sessionId);

        // Verify new map is created (empty)
        assertTrue(helper.getAccessResultMap(sessionId).isEmpty());
    }

    /**
     * Test deleteAllAccessResultMap
     */
    public void test_deleteAllAccessResultMap() {
        // Add data to multiple sessions
        helper.getAccessResultMap("session1").put("url1", new AccessResultImpl<>());
        helper.getAccessResultMap("session2").put("url2", new AccessResultImpl<>());

        // Delete all
        helper.deleteAllAccessResultMap();

        // Verify new maps are created (empty)
        assertTrue(helper.getAccessResultMap("session1").isEmpty());
        assertTrue(helper.getAccessResultMap("session2").isEmpty());
    }

    /**
     * Test clearUrlQueueList
     */
    public void test_clearUrlQueueList() {
        // Add data
        helper.getUrlQueueList("session1").add(new UrlQueueImpl<>());
        helper.getUrlInQueueSet("session1").add("url1");
        helper.getUrlQueueList("session2").add(new UrlQueueImpl<>());
        helper.getUrlInQueueSet("session2").add("url2");

        // Clear all queues
        helper.clearUrlQueueList();

        // Verify new queues and sets are created (empty)
        assertTrue(helper.getUrlQueueList("session1").isEmpty());
        assertTrue(helper.getUrlInQueueSet("session1").isEmpty());
        assertTrue(helper.getUrlQueueList("session2").isEmpty());
        assertTrue(helper.getUrlInQueueSet("session2").isEmpty());
    }

    /**
     * Test clearUrlPattern for all sessions
     */
    public void test_clearUrlPatternAll() {
        // Add patterns to multiple sessions
        helper.addIncludeUrlPattern("session1", "pattern1");
        helper.addExcludeUrlPattern("session1", "pattern2");
        helper.addIncludeUrlPattern("session2", "pattern3");

        // Clear all patterns
        helper.clearUrlPattern();

        // Verify new lists are created (empty)
        assertTrue(helper.getIncludeUrlPatternList("session1").isEmpty());
        assertTrue(helper.getExcludeUrlPatternList("session1").isEmpty());
        assertTrue(helper.getIncludeUrlPatternList("session2").isEmpty());
    }
}
