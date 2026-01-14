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
package org.codelibs.fess.crawler.util;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for CrawlingParameterUtil.
 *
 * @author shinsuke
 */
public class CrawlingParameterUtilTest extends PlainTestCase {

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        // Clean up ThreadLocal variables after each test
        CrawlingParameterUtil.setUrlQueue(null);
        CrawlingParameterUtil.setCrawlerContext(null);
        CrawlingParameterUtil.setUrlQueueService(null);
        CrawlingParameterUtil.setDataService(null);
        super.tearDown(testInfo);
    }

    // Helper method to create a simple mock UrlQueue
    private UrlQueue<?> createMockUrlQueue(final String sessionId, final String url) {
        return new UrlQueue<Object>() {
            @Override
            public Object getId() {
                return null;
            }

            @Override
            public void setId(Object id) {
            }

            @Override
            public String getSessionId() {
                return sessionId;
            }

            @Override
            public void setSessionId(String sid) {
            }

            @Override
            public String getMethod() {
                return null;
            }

            @Override
            public void setMethod(String method) {
            }

            @Override
            public String getUrl() {
                return url;
            }

            @Override
            public void setUrl(String u) {
            }

            @Override
            public String getMetaData() {
                return null;
            }

            @Override
            public void setMetaData(String metaData) {
            }

            @Override
            public String getEncoding() {
                return null;
            }

            @Override
            public void setEncoding(String encoding) {
            }

            @Override
            public String getParentUrl() {
                return null;
            }

            @Override
            public void setParentUrl(String parentUrl) {
            }

            @Override
            public Integer getDepth() {
                return null;
            }

            @Override
            public void setDepth(Integer depth) {
            }

            @Override
            public Long getLastModified() {
                return null;
            }

            @Override
            public void setLastModified(Long lastModified) {
            }

            @Override
            public Long getCreateTime() {
                return null;
            }

            @Override
            public void setCreateTime(Long createTime) {
            }

            @Override
            public float getWeight() {
                return 0;
            }

            @Override
            public void setWeight(float weight) {
            }
        };
    }

    @Test
    public void test_urlQueue_setAndGet() {
        // Initially should be null
        assertNull(CrawlingParameterUtil.getUrlQueue());

        // Create and set a mock UrlQueue
        UrlQueue<?> urlQueue = createMockUrlQueue("test-session", "http://example.com");
        CrawlingParameterUtil.setUrlQueue(urlQueue);

        // Verify it can be retrieved
        UrlQueue<?> retrieved = CrawlingParameterUtil.getUrlQueue();
        assertNotNull(retrieved);
        assertEquals("test-session", retrieved.getSessionId());
        assertEquals("http://example.com", retrieved.getUrl());
    }

    @Test
    public void test_urlQueue_setNull() {
        // Set a UrlQueue first
        UrlQueue<?> urlQueue = createMockUrlQueue("test", "http://test.com");
        CrawlingParameterUtil.setUrlQueue(urlQueue);
        assertNotNull(CrawlingParameterUtil.getUrlQueue());

        // Set to null
        CrawlingParameterUtil.setUrlQueue(null);

        // Should be null now
        assertNull(CrawlingParameterUtil.getUrlQueue());
    }

    @Test
    public void test_crawlerContext_setAndGet() {
        // Initially should be null
        assertNull(CrawlingParameterUtil.getCrawlerContext());

        // Create and set a CrawlerContext
        CrawlerContext context = new CrawlerContext();
        context.setSessionId("test-context-session");

        CrawlingParameterUtil.setCrawlerContext(context);

        // Verify it can be retrieved
        CrawlerContext retrieved = CrawlingParameterUtil.getCrawlerContext();
        assertNotNull(retrieved);
        assertEquals("test-context-session", retrieved.getSessionId());
    }

    @Test
    public void test_crawlerContext_setNull() {
        // Set a CrawlerContext first
        CrawlerContext context = new CrawlerContext();
        context.setSessionId("test");
        CrawlingParameterUtil.setCrawlerContext(context);
        assertNotNull(CrawlingParameterUtil.getCrawlerContext());

        // Set to null
        CrawlingParameterUtil.setCrawlerContext(null);

        // Should be null now
        assertNull(CrawlingParameterUtil.getCrawlerContext());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_urlQueueService_setAndGet() {
        // Initially should be null
        assertNull(CrawlingParameterUtil.getUrlQueueService());

        // Create a mock UrlQueueService
        UrlQueueService<UrlQueue<?>> service = new UrlQueueService<UrlQueue<?>>() {
            @Override
            public void updateSessionId(String oldSessionId, String newSessionId) {
            }

            @Override
            public void add(String sessionId, String url) {
            }

            @Override
            public void insert(UrlQueue<?> urlQueue) {
            }

            @Override
            public void delete(String sessionId) {
            }

            @Override
            public void deleteAll() {
            }

            @Override
            public void offerAll(String sessionId, List<UrlQueue<?>> newUrlQueueList) {
            }

            @Override
            public UrlQueue<?> poll(String sessionId) {
                return null;
            }

            @Override
            public void saveSession(String sessionId) {
            }

            @Override
            public boolean visited(UrlQueue<?> urlQueue) {
                return false;
            }

            @Override
            public void generateUrlQueues(String previousSessionId, String sessionId) {
            }
        };

        CrawlingParameterUtil.setUrlQueueService(service);

        // Verify it can be retrieved
        UrlQueueService<UrlQueue<?>> retrieved = CrawlingParameterUtil.getUrlQueueService();
        assertNotNull(retrieved);
        assertTrue(service == retrieved);
    }

    @Test
    public void test_urlQueueService_setNull() {
        // Set a service first
        @SuppressWarnings("unchecked")
        UrlQueueService<UrlQueue<?>> service = new UrlQueueService<UrlQueue<?>>() {
            @Override
            public void updateSessionId(String oldSessionId, String newSessionId) {
            }

            @Override
            public void add(String sessionId, String url) {
            }

            @Override
            public void insert(UrlQueue<?> urlQueue) {
            }

            @Override
            public void delete(String sessionId) {
            }

            @Override
            public void deleteAll() {
            }

            @Override
            public void offerAll(String sessionId, List<UrlQueue<?>> newUrlQueueList) {
            }

            @Override
            public UrlQueue<?> poll(String sessionId) {
                return null;
            }

            @Override
            public void saveSession(String sessionId) {
            }

            @Override
            public boolean visited(UrlQueue<?> urlQueue) {
                return false;
            }

            @Override
            public void generateUrlQueues(String previousSessionId, String sessionId) {
            }
        };
        CrawlingParameterUtil.setUrlQueueService(service);
        assertNotNull(CrawlingParameterUtil.getUrlQueueService());

        // Set to null
        CrawlingParameterUtil.setUrlQueueService(null);

        // Should be null now
        assertNull(CrawlingParameterUtil.getUrlQueueService());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_dataService_setAndGet() {
        // Initially should be null
        assertNull(CrawlingParameterUtil.getDataService());

        // Create a mock DataService
        DataService<AccessResult<?>> service = new DataService<AccessResult<?>>() {
            @Override
            public void store(AccessResult<?> accessResult) {
            }

            @Override
            public void update(AccessResult<?> accessResult) {
            }

            @Override
            public void update(List<AccessResult<?>> accessResults) {
            }

            @Override
            public int getCount(String sessionId) {
                return 0;
            }

            @Override
            public void delete(String sessionId) {
            }

            @Override
            public void deleteAll() {
            }

            @Override
            public AccessResult<?> getAccessResult(String sessionId, String url) {
                return null;
            }

            @Override
            public List<AccessResult<?>> getAccessResultList(String url, boolean hasData) {
                return null;
            }

            @Override
            public void iterate(String sessionId, AccessResultCallback<AccessResult<?>> callback) {
            }
        };

        CrawlingParameterUtil.setDataService(service);

        // Verify it can be retrieved
        DataService<AccessResult<?>> retrieved = CrawlingParameterUtil.getDataService();
        assertNotNull(retrieved);
        assertTrue(service == retrieved);
    }

    @Test
    public void test_dataService_setNull() {
        // Set a service first
        @SuppressWarnings("unchecked")
        DataService<AccessResult<?>> service = new DataService<AccessResult<?>>() {
            @Override
            public void store(AccessResult<?> accessResult) {
            }

            @Override
            public void update(AccessResult<?> accessResult) {
            }

            @Override
            public void update(List<AccessResult<?>> accessResults) {
            }

            @Override
            public int getCount(String sessionId) {
                return 0;
            }

            @Override
            public void delete(String sessionId) {
            }

            @Override
            public void deleteAll() {
            }

            @Override
            public AccessResult<?> getAccessResult(String sessionId, String url) {
                return null;
            }

            @Override
            public List<AccessResult<?>> getAccessResultList(String url, boolean hasData) {
                return null;
            }

            @Override
            public void iterate(String sessionId, AccessResultCallback<AccessResult<?>> callback) {
            }
        };
        CrawlingParameterUtil.setDataService(service);
        assertNotNull(CrawlingParameterUtil.getDataService());

        // Set to null
        CrawlingParameterUtil.setDataService(null);

        // Should be null now
        assertNull(CrawlingParameterUtil.getDataService());
    }

    @Test
    public void test_threadLocal_isolation() throws Exception {
        // Set values in main thread
        UrlQueue<?> mainUrlQueue = createMockUrlQueue("main-thread", "http://main.com");
        CrawlingParameterUtil.setUrlQueue(mainUrlQueue);

        CrawlerContext mainContext = new CrawlerContext();
        mainContext.setSessionId("main-context");
        CrawlingParameterUtil.setCrawlerContext(mainContext);

        // Verify main thread values
        assertEquals("main-thread", CrawlingParameterUtil.getUrlQueue().getSessionId());
        assertEquals("main-context", CrawlingParameterUtil.getCrawlerContext().getSessionId());

        // Test in a different thread
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String> otherThreadUrlQueueSessionId = new AtomicReference<>();
        final AtomicReference<String> otherThreadContextSessionId = new AtomicReference<>();

        Thread otherThread = new Thread(() -> {
            try {
                // Should be null in other thread (ThreadLocal isolation)
                assertNull(CrawlingParameterUtil.getUrlQueue());
                assertNull(CrawlingParameterUtil.getCrawlerContext());

                // Set different values in other thread
                UrlQueue<?> otherUrlQueue = createMockUrlQueue("other-thread", "http://other.com");
                CrawlingParameterUtil.setUrlQueue(otherUrlQueue);

                CrawlerContext otherContext = new CrawlerContext();
                otherContext.setSessionId("other-context");
                CrawlingParameterUtil.setCrawlerContext(otherContext);

                // Store values for verification
                otherThreadUrlQueueSessionId.set(CrawlingParameterUtil.getUrlQueue().getSessionId());
                otherThreadContextSessionId.set(CrawlingParameterUtil.getCrawlerContext().getSessionId());
            } finally {
                latch.countDown();
            }
        });

        otherThread.start();
        latch.await();

        // Verify other thread had different values
        assertEquals("other-thread", otherThreadUrlQueueSessionId.get());
        assertEquals("other-context", otherThreadContextSessionId.get());

        // Verify main thread values are unchanged
        assertEquals("main-thread", CrawlingParameterUtil.getUrlQueue().getSessionId());
        assertEquals("main-context", CrawlingParameterUtil.getCrawlerContext().getSessionId());
    }
}
