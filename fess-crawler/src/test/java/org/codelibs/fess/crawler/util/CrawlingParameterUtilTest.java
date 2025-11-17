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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for CrawlingParameterUtil.
 *
 * @author shinsuke
 */
public class CrawlingParameterUtilTest extends PlainTestCase {

    @Override
    protected void tearDown() throws Exception {
        // Clean up ThreadLocal variables after each test
        CrawlingParameterUtil.setUrlQueue(null);
        CrawlingParameterUtil.setCrawlerContext(null);
        CrawlingParameterUtil.setUrlQueueService(null);
        CrawlingParameterUtil.setDataService(null);
        super.tearDown();
    }

    public void test_urlQueue_setAndGet() {
        // Initially should be null
        assertNull(CrawlingParameterUtil.getUrlQueue());

        // Create and set a mock UrlQueue
        UrlQueue<?> urlQueue = new UrlQueue<Object>() {
            @Override
            public String getSessionId() {
                return "test-session";
            }

            @Override
            public String getUrl() {
                return "http://example.com";
            }
        };

        CrawlingParameterUtil.setUrlQueue(urlQueue);

        // Verify it can be retrieved
        UrlQueue<?> retrieved = CrawlingParameterUtil.getUrlQueue();
        assertNotNull(retrieved);
        assertEquals("test-session", retrieved.getSessionId());
        assertEquals("http://example.com", retrieved.getUrl());
    }

    public void test_urlQueue_setNull() {
        // Set a UrlQueue first
        UrlQueue<?> urlQueue = new UrlQueue<Object>() {
            @Override
            public String getSessionId() {
                return "test";
            }

            @Override
            public String getUrl() {
                return "http://test.com";
            }
        };
        CrawlingParameterUtil.setUrlQueue(urlQueue);
        assertNotNull(CrawlingParameterUtil.getUrlQueue());

        // Set to null
        CrawlingParameterUtil.setUrlQueue(null);

        // Should be null now
        assertNull(CrawlingParameterUtil.getUrlQueue());
    }

    public void test_crawlerContext_setAndGet() {
        // Initially should be null
        assertNull(CrawlingParameterUtil.getCrawlerContext());

        // Create and set a mock CrawlerContext
        CrawlerContext context = new CrawlerContext() {
            private String sessionId = "test-context-session";

            @Override
            public String getSessionId() {
                return sessionId;
            }

            @Override
            public void setSessionId(String sessionId) {
                this.sessionId = sessionId;
            }
        };

        CrawlingParameterUtil.setCrawlerContext(context);

        // Verify it can be retrieved
        CrawlerContext retrieved = CrawlingParameterUtil.getCrawlerContext();
        assertNotNull(retrieved);
        assertEquals("test-context-session", retrieved.getSessionId());
    }

    public void test_crawlerContext_setNull() {
        // Set a CrawlerContext first
        CrawlerContext context = new CrawlerContext() {
            @Override
            public String getSessionId() {
                return "test";
            }
        };
        CrawlingParameterUtil.setCrawlerContext(context);
        assertNotNull(CrawlingParameterUtil.getCrawlerContext());

        // Set to null
        CrawlingParameterUtil.setCrawlerContext(null);

        // Should be null now
        assertNull(CrawlingParameterUtil.getCrawlerContext());
    }

    @SuppressWarnings("unchecked")
    public void test_urlQueueService_setAndGet() {
        // Initially should be null
        assertNull(CrawlingParameterUtil.getUrlQueueService());

        // Create a mock UrlQueueService
        UrlQueueService<UrlQueue<?>> service = new UrlQueueService<UrlQueue<?>>() {
            @Override
            public void insert(UrlQueue<?> urlQueue) {
                // Mock implementation
            }
        };

        CrawlingParameterUtil.setUrlQueueService(service);

        // Verify it can be retrieved
        UrlQueueService<UrlQueue<?>> retrieved = CrawlingParameterUtil.getUrlQueueService();
        assertNotNull(retrieved);
        assertSame(service, retrieved);
    }

    public void test_urlQueueService_setNull() {
        // Set a service first
        @SuppressWarnings("unchecked")
        UrlQueueService<UrlQueue<?>> service = new UrlQueueService<UrlQueue<?>>() {
            @Override
            public void insert(UrlQueue<?> urlQueue) {
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
    public void test_dataService_setAndGet() {
        // Initially should be null
        assertNull(CrawlingParameterUtil.getDataService());

        // Create a mock DataService
        DataService<AccessResult<?>> service = new DataService<AccessResult<?>>() {
            @Override
            public void store(AccessResult<?> accessResult) {
                // Mock implementation
            }
        };

        CrawlingParameterUtil.setDataService(service);

        // Verify it can be retrieved
        DataService<AccessResult<?>> retrieved = CrawlingParameterUtil.getDataService();
        assertNotNull(retrieved);
        assertSame(service, retrieved);
    }

    public void test_dataService_setNull() {
        // Set a service first
        @SuppressWarnings("unchecked")
        DataService<AccessResult<?>> service = new DataService<AccessResult<?>>() {
            @Override
            public void store(AccessResult<?> accessResult) {
            }
        };
        CrawlingParameterUtil.setDataService(service);
        assertNotNull(CrawlingParameterUtil.getDataService());

        // Set to null
        CrawlingParameterUtil.setDataService(null);

        // Should be null now
        assertNull(CrawlingParameterUtil.getDataService());
    }

    public void test_threadLocal_isolation() throws Exception {
        // Set values in main thread
        UrlQueue<?> mainUrlQueue = new UrlQueue<Object>() {
            @Override
            public String getSessionId() {
                return "main-thread";
            }

            @Override
            public String getUrl() {
                return "http://main.com";
            }
        };
        CrawlingParameterUtil.setUrlQueue(mainUrlQueue);

        CrawlerContext mainContext = new CrawlerContext() {
            @Override
            public String getSessionId() {
                return "main-context";
            }
        };
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
                UrlQueue<?> otherUrlQueue = new UrlQueue<Object>() {
                    @Override
                    public String getSessionId() {
                        return "other-thread";
                    }

                    @Override
                    public String getUrl() {
                        return "http://other.com";
                    }
                };
                CrawlingParameterUtil.setUrlQueue(otherUrlQueue);

                CrawlerContext otherContext = new CrawlerContext() {
                    @Override
                    public String getSessionId() {
                        return "other-context";
                    }
                };
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
