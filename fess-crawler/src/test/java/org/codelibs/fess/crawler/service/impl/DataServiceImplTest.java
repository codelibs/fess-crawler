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
package org.codelibs.fess.crawler.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.AccessResultImpl;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.helper.MemoryDataHelper;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.util.AccessResultCallback;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author shinsuke
 *
 */
public class DataServiceImplTest extends PlainTestCase {
    public DataService dataService;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("dataHelper", MemoryDataHelper.class)//
                .singleton("dataService", DataServiceImpl.class);
        dataService = container.getComponent("dataService");
    }

    @Test
    public void test_insert_deleteTx() {
        final AccessResult accessResult1 = new AccessResultImpl();
        accessResult1.setContentLength(Long.valueOf(10));
        accessResult1.setCreateTime(System.currentTimeMillis());
        accessResult1.setExecutionTime(10);
        accessResult1.setHttpStatusCode(200);
        accessResult1.setLastModified(System.currentTimeMillis());
        accessResult1.setMethod("GET");
        accessResult1.setMimeType("text/plain");
        accessResult1.setParentUrl("http://www.parent.com/");
        accessResult1.setRuleId("htmlRule");
        accessResult1.setSessionId("id1");
        accessResult1.setStatus(200);
        accessResult1.setUrl("http://www.id1.com/");

        dataService.store(accessResult1);

        final AccessResult accessResult2 = dataService.getAccessResult("id1", "http://www.id1.com/");
        assertNotNull(accessResult2);

        accessResult2.setMimeType("text/html");
        dataService.update(accessResult2);

        final AccessResult accessResult3 = dataService.getAccessResult("id1", "http://www.id1.com/");
        assertNotNull(accessResult3);
        assertEquals("text/html", accessResult3.getMimeType());

        dataService.delete("id1");

        final AccessResult accessResult4 = dataService.getAccessResult("id1", "http://www.id1.com/");
        assertNull(accessResult4);
    }

    @Test
    public void test_concurrentStore() throws Exception {
        // Test that AtomicLong generates unique IDs under concurrent access
        final int threadCount = 10;
        final int operationsPerThread = 100;
        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(threadCount);
        final Set<Long> ids = Collections.synchronizedSet(new HashSet<>());
        final List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

        for (int t = 0; t < threadCount; t++) {
            final int threadIndex = t;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < operationsPerThread; i++) {
                        final AccessResult accessResult = new AccessResultImpl();
                        accessResult.setContentLength(Long.valueOf(10));
                        accessResult.setCreateTime(System.currentTimeMillis());
                        accessResult.setExecutionTime(10);
                        accessResult.setHttpStatusCode(200);
                        accessResult.setLastModified(System.currentTimeMillis());
                        accessResult.setMethod("GET");
                        accessResult.setMimeType("text/plain");
                        accessResult.setSessionId("concurrent-" + threadIndex);
                        accessResult.setStatus(200);
                        accessResult.setUrl("http://example.com/" + threadIndex + "/" + i);
                        dataService.store(accessResult);
                        ids.add((Long) accessResult.getId());
                    }
                } catch (final Throwable e) {
                    errors.add(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS));
        executor.shutdown();

        // Verify no errors occurred
        assertTrue(errors.isEmpty());

        // Verify all IDs are unique (no duplicates)
        assertEquals(threadCount * operationsPerThread, ids.size());

        // Cleanup
        for (int t = 0; t < threadCount; t++) {
            dataService.delete("concurrent-" + t);
        }
    }

    @Test
    public void test_store_null() {
        try {
            dataService.store(null);
            fail("Expected CrawlerSystemException");
        } catch (final CrawlerSystemException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void test_store_duplicateUrl() {
        final AccessResult accessResult1 = createAccessResult("dup-session", "http://www.example.com/");
        dataService.store(accessResult1);

        final AccessResult accessResult2 = createAccessResult("dup-session", "http://www.example.com/");
        try {
            dataService.store(accessResult2);
            fail("Expected CrawlerSystemException for duplicate URL");
        } catch (final CrawlerSystemException e) {
            assertTrue(e.getMessage().contains("already exists"));
        }

        dataService.delete("dup-session");
    }

    @Test
    public void test_getCount() {
        final String sessionId = "count-session";

        assertEquals(0, dataService.getCount(sessionId));

        dataService.store(createAccessResult(sessionId, "http://www.example1.com/"));
        assertEquals(1, dataService.getCount(sessionId));

        dataService.store(createAccessResult(sessionId, "http://www.example2.com/"));
        assertEquals(2, dataService.getCount(sessionId));

        dataService.store(createAccessResult(sessionId, "http://www.example3.com/"));
        assertEquals(3, dataService.getCount(sessionId));

        dataService.delete(sessionId);
        assertEquals(0, dataService.getCount(sessionId));
    }

    @Test
    public void test_getAccessResult_notFound() {
        final AccessResult result = dataService.getAccessResult("nonexistent-session", "http://www.notfound.com/");
        assertNull(result);
    }

    @Test
    public void test_getAccessResultList() {
        final String url = "http://www.shared-url.com/";

        dataService.store(createAccessResult("session1", url));
        dataService.store(createAccessResult("session2", "http://www.other.com/"));

        final List<AccessResultImpl<Long>> results = dataService.getAccessResultList(url, true);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(url, results.get(0).getUrl());

        dataService.delete("session1");
        dataService.delete("session2");
    }

    @Test
    public void test_iterate() {
        final String sessionId = "iterate-session";

        dataService.store(createAccessResult(sessionId, "http://www.example1.com/"));
        dataService.store(createAccessResult(sessionId, "http://www.example2.com/"));
        dataService.store(createAccessResult(sessionId, "http://www.example3.com/"));

        final AtomicInteger count = new AtomicInteger(0);
        final Set<String> urls = new HashSet<>();

        dataService.iterate(sessionId, new AccessResultCallback<AccessResultImpl<Long>>() {
            @Override
            public void iterate(final AccessResultImpl<Long> accessResult) {
                count.incrementAndGet();
                urls.add(accessResult.getUrl());
            }
        });

        assertEquals(3, count.get());
        assertTrue(urls.contains("http://www.example1.com/"));
        assertTrue(urls.contains("http://www.example2.com/"));
        assertTrue(urls.contains("http://www.example3.com/"));

        dataService.delete(sessionId);
    }

    @Test
    public void test_iterate_emptySession() {
        final AtomicInteger count = new AtomicInteger(0);

        dataService.iterate("empty-session", new AccessResultCallback<AccessResultImpl<Long>>() {
            @Override
            public void iterate(final AccessResultImpl<Long> accessResult) {
                count.incrementAndGet();
            }
        });

        assertEquals(0, count.get());
    }

    @Test
    public void test_update_notFound() {
        final AccessResult accessResult = createAccessResult("update-session", "http://www.notfound.com/");

        try {
            dataService.update(accessResult);
            fail("Expected CrawlerSystemException");
        } catch (final CrawlerSystemException e) {
            assertTrue(e.getMessage().contains("not found"));
        }
    }

    @Test
    public void test_update_list() {
        final String sessionId = "update-list-session";

        final AccessResultImpl<Long> result1 = (AccessResultImpl<Long>) createAccessResult(sessionId, "http://www.example1.com/");
        final AccessResultImpl<Long> result2 = (AccessResultImpl<Long>) createAccessResult(sessionId, "http://www.example2.com/");

        dataService.store(result1);
        dataService.store(result2);

        result1.setMimeType("application/json");
        result2.setMimeType("application/xml");

        final List<AccessResultImpl<Long>> updateList = new ArrayList<>();
        updateList.add(result1);
        updateList.add(result2);

        dataService.update(updateList);

        final AccessResult updated1 = dataService.getAccessResult(sessionId, "http://www.example1.com/");
        final AccessResult updated2 = dataService.getAccessResult(sessionId, "http://www.example2.com/");

        assertEquals("application/json", updated1.getMimeType());
        assertEquals("application/xml", updated2.getMimeType());

        dataService.delete(sessionId);
    }

    @Test
    public void test_delete_nonexistentSession() {
        // Should not throw exception
        dataService.delete("nonexistent-session");
    }

    @Test
    public void test_deleteAll() {
        dataService.store(createAccessResult("deleteAll-1", "http://www.example1.com/"));
        dataService.store(createAccessResult("deleteAll-2", "http://www.example2.com/"));

        dataService.deleteAll();

        // Note: deleteAll clears URL queue list, not access result maps
        // This test verifies the method doesn't throw
    }

    @Test
    public void test_accessResultData_autoCreated() {
        final AccessResultImpl<Long> accessResult = new AccessResultImpl<>();
        accessResult.setSessionId("auto-data-session");
        accessResult.setUrl("http://www.example.com/");
        accessResult.setMethod("GET");
        accessResult.setStatus(200);
        // Don't set accessResultData

        dataService.store(accessResult);

        final AccessResult stored = dataService.getAccessResult("auto-data-session", "http://www.example.com/");
        assertNotNull(stored.getAccessResultData());
        assertEquals(stored.getId(), stored.getAccessResultData().getId());

        dataService.delete("auto-data-session");
    }

    @Test
    public void test_multipleSessions_isolation() {
        final String session1 = "isolation-1";
        final String session2 = "isolation-2";

        dataService.store(createAccessResult(session1, "http://www.example.com/page1"));
        dataService.store(createAccessResult(session1, "http://www.example.com/page2"));
        dataService.store(createAccessResult(session2, "http://www.example.com/page1"));

        assertEquals(2, dataService.getCount(session1));
        assertEquals(1, dataService.getCount(session2));

        dataService.delete(session1);

        assertEquals(0, dataService.getCount(session1));
        assertEquals(1, dataService.getCount(session2));

        dataService.delete(session2);
    }

    private AccessResult createAccessResult(final String sessionId, final String url) {
        final AccessResultImpl<Long> accessResult = new AccessResultImpl<>();
        accessResult.setContentLength(Long.valueOf(100));
        accessResult.setCreateTime(System.currentTimeMillis());
        accessResult.setExecutionTime(50);
        accessResult.setHttpStatusCode(200);
        accessResult.setLastModified(System.currentTimeMillis());
        accessResult.setMethod("GET");
        accessResult.setMimeType("text/html");
        accessResult.setSessionId(sessionId);
        accessResult.setStatus(200);
        accessResult.setUrl(url);
        return accessResult;
    }
}
