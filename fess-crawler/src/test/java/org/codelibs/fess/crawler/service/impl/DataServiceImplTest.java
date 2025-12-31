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

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.AccessResultImpl;
import org.codelibs.fess.crawler.helper.MemoryDataHelper;
import org.codelibs.fess.crawler.service.DataService;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 *
 */
public class DataServiceImplTest extends PlainTestCase {
    public DataService dataService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("dataHelper", MemoryDataHelper.class)//
                .singleton("dataService", DataServiceImpl.class);
        dataService = container.getComponent("dataService");
    }

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
                        ids.add(accessResult.getId());
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
        assertTrue("Errors occurred: " + errors, errors.isEmpty());

        // Verify all IDs are unique (no duplicates)
        assertEquals("IDs should be unique", threadCount * operationsPerThread, ids.size());

        // Cleanup
        for (int t = 0; t < threadCount; t++) {
            dataService.delete("concurrent-" + t);
        }
    }
}
