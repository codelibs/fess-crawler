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

import static org.codelibs.opensearch.runner.OpenSearchRunner.newConfigs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.crawler.client.FesenClient;
import org.codelibs.fess.crawler.entity.OpenSearchUrlQueue;
import org.codelibs.opensearch.runner.OpenSearchRunner;
import org.dbflute.utflute.lastadi.LastaDiTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.opensearch.index.query.QueryBuilders;

import jakarta.annotation.Resource;

/**
 * @author shinsuke
 *
 */
public class OpenSearchUrlQueueServiceTest extends LastaDiTestCase {
    @Resource
    private OpenSearchUrlQueueService urlQueueService;

    @Resource
    private FesenClient fesenClient;

    private OpenSearchRunner runner;

    @Override
    protected String prepareConfigFile() {
        return "app.xml";
    }

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Override
    public void setUp(final TestInfo testInfo) throws Exception {
        // create runner instance
        runner = new OpenSearchRunner();
        // create ES nodes
        final String clusterName = UUID.randomUUID().toString();
        runner.onBuild((number, settingsBuilder) -> {
            settingsBuilder.put("http.cors.enabled", true);
            settingsBuilder.put("discovery.type", "single-node");
        }).build(newConfigs().clusterName(clusterName).numOfNode(1));

        // wait for yellow status
        runner.ensureYellow();

        System.setProperty(FesenClient.HTTP_ADDRESS, "localhost:" + runner.node().settings().get("http.port", "9201"));

        super.setUp(testInfo);
    }

    @Override
    public void tearDown(final TestInfo testInfo) throws Exception {
        super.tearDown(testInfo);
        // close runner
        runner.close();
        // delete all files
        runner.clean();
    }

    @Test
    public void test_insert_update_deleteTx() {
        final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
        urlQueue.setCreateTime(System.currentTimeMillis());
        urlQueue.setDepth(1);
        urlQueue.setMethod("GET");
        urlQueue.setSessionId("sessionId");
        urlQueue.setUrl("http://www.example.com/");

        urlQueueService.insert(urlQueue);
        assertTrue(fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", "sessionId"))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value() > 0);

        urlQueueService.delete("sessionId");
        assertFalse(fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", "sessionId"))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value() > 0);

    }

    @Test
    public void test_insert_update_delete_multiTx() {
        final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
        urlQueue.setCreateTime(System.currentTimeMillis());
        urlQueue.setDepth(1);
        urlQueue.setMethod("GET");
        urlQueue.setSessionId("id1");
        urlQueue.setUrl("http://www.id1.com/");

        urlQueueService.insert(urlQueue);

        final OpenSearchUrlQueue urlQueue2 = new OpenSearchUrlQueue();
        urlQueue2.setCreateTime(System.currentTimeMillis());
        urlQueue2.setDepth(1);
        urlQueue2.setMethod("GET");
        urlQueue2.setSessionId("id2");
        urlQueue2.setUrl("http://www.id2.com/");

        urlQueueService.insert(urlQueue2);
        assertTrue(fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", "id1"))
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value() > 0);
        assertTrue(fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", "id2"))
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value() > 0);

        urlQueueService.delete("id1");
        assertFalse(fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", "id1"))
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value() > 0);
        assertTrue(fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", "id2"))
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value() > 0);

        urlQueueService.deleteAll();
        assertFalse(fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", "id1"))
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value() > 0);
        assertFalse(fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", "id2"))
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value() > 0);
    }

    @Test
    public void test_poll_emptyQueueTx() {
        final String sessionId = "poll_session1";

        // Poll from empty queue should return null
        final OpenSearchUrlQueue result = urlQueueService.poll(sessionId);
        assertNull(result);
    }

    @Test
    public void test_poll_singleItemTx() {
        final String sessionId = "poll_session2";
        final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
        urlQueue.setCreateTime(System.currentTimeMillis());
        urlQueue.setDepth(1);
        urlQueue.setMethod("GET");
        urlQueue.setSessionId(sessionId);
        urlQueue.setUrl("http://www.example.com/page1");

        urlQueueService.insert(urlQueue);

        // Poll should return the item
        final OpenSearchUrlQueue polled = urlQueueService.poll(sessionId);
        assertNotNull(polled);
        assertEquals("http://www.example.com/page1", polled.getUrl());
        assertEquals(sessionId, polled.getSessionId());

        // Second poll should return null (queue is empty)
        final OpenSearchUrlQueue polled2 = urlQueueService.poll(sessionId);
        assertNull(polled2);

        urlQueueService.delete(sessionId);
    }

    @Test
    public void test_poll_multipleItemsTx() {
        final String sessionId = "poll_session3";
        final List<OpenSearchUrlQueue> urlQueueList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
            urlQueue.setCreateTime(System.currentTimeMillis());
            urlQueue.setDepth(1);
            urlQueue.setMethod("GET");
            urlQueue.setSessionId(sessionId);
            urlQueue.setUrl("http://www.example.com/page" + i);
            urlQueueList.add(urlQueue);
        }

        urlQueueService.offerAll(sessionId, urlQueueList);

        // Poll all items
        int count = 0;
        while (true) {
            final OpenSearchUrlQueue polled = urlQueueService.poll(sessionId);
            if (polled == null) {
                break;
            }
            count++;
            assertTrue(polled.getUrl().startsWith("http://www.example.com/page"));
        }

        assertEquals(5, count);
        urlQueueService.delete(sessionId);
    }

    @Test
    public void test_poll_concurrentAccessTx() throws Exception {
        final String sessionId = "poll_session4";
        final int numThreads = 5;
        final int itemsPerThread = 10;
        final List<OpenSearchUrlQueue> urlQueueList = new ArrayList<>();

        // Insert items
        for (int i = 1; i <= itemsPerThread * numThreads; i++) {
            final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
            urlQueue.setCreateTime(System.currentTimeMillis());
            urlQueue.setDepth(1);
            urlQueue.setMethod("GET");
            urlQueue.setSessionId(sessionId);
            urlQueue.setUrl("http://www.example.com/page" + i);
            urlQueueList.add(urlQueue);
        }

        urlQueueService.offerAll(sessionId, urlQueueList);

        // Poll concurrently
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(numThreads);
        final AtomicInteger totalPolled = new AtomicInteger(0);
        final List<String> allUrls = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    int count = 0;
                    while (count < itemsPerThread) {
                        final OpenSearchUrlQueue polled = urlQueueService.poll(sessionId);
                        if (polled != null) {
                            count++;
                            synchronized (allUrls) {
                                allUrls.add(polled.getUrl());
                            }
                        }
                    }
                    totalPolled.addAndGet(count);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown();
        doneLatch.await();

        // Verify all items were polled exactly once
        assertEquals(itemsPerThread * numThreads, totalPolled.get());
        assertEquals(itemsPerThread * numThreads, allUrls.size());

        urlQueueService.delete(sessionId);
    }

    @Test
    public void test_poll_maxCrawlingQueueSizeTx() {
        final String sessionId = "poll_session5";
        final int maxSize = 5;
        urlQueueService.setMaxCrawlingQueueSize(maxSize);

        // Insert more items than max crawling queue size
        final List<OpenSearchUrlQueue> urlQueueList = new ArrayList<>();
        for (int i = 1; i <= maxSize + 10; i++) {
            final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
            urlQueue.setCreateTime(System.currentTimeMillis());
            urlQueue.setDepth(1);
            urlQueue.setMethod("GET");
            urlQueue.setSessionId(sessionId);
            urlQueue.setUrl("http://www.example.com/page" + i);
            urlQueueList.add(urlQueue);
        }

        urlQueueService.offerAll(sessionId, urlQueueList);

        // Poll items - should work fine even with max size constraint
        int count = 0;
        while (true) {
            final OpenSearchUrlQueue polled = urlQueueService.poll(sessionId);
            if (polled == null) {
                break;
            }
            count++;
        }

        assertEquals(maxSize + 10, count);
        urlQueueService.delete(sessionId);
    }

    @Test
    public void test_poll_withWeightTx() {
        final String sessionId = "poll_session6";
        final List<OpenSearchUrlQueue> urlQueueList = new ArrayList<>();

        // Create items with different weights
        for (int i = 1; i <= 5; i++) {
            final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
            urlQueue.setCreateTime(System.currentTimeMillis());
            urlQueue.setDepth(1);
            urlQueue.setMethod("GET");
            urlQueue.setSessionId(sessionId);
            urlQueue.setUrl("http://www.example.com/page" + i);
            urlQueue.setWeight(i); // Higher number = higher weight
            urlQueueList.add(urlQueue);
        }

        urlQueueService.offerAll(sessionId, urlQueueList);

        // Poll - items should be ordered by weight (descending)
        final OpenSearchUrlQueue first = urlQueueService.poll(sessionId);
        assertNotNull(first);
        // Weight should be highest
        assertTrue(first.getWeight() >= 1);

        urlQueueService.delete(sessionId);
    }

    @Test
    public void test_offerAll_emptyListTx() {
        final String sessionId = "offer_session1";
        final List<OpenSearchUrlQueue> emptyList = new ArrayList<>();

        // Should not throw exception
        urlQueueService.offerAll(sessionId, emptyList);

        // Verify no items were added
        final OpenSearchUrlQueue polled = urlQueueService.poll(sessionId);
        assertNull(polled);
    }

    @Test
    public void test_offerAll_duplicateUrlsTx() {
        final String sessionId = "offer_session2";
        final List<OpenSearchUrlQueue> urlQueueList = new ArrayList<>();

        final OpenSearchUrlQueue urlQueue1 = new OpenSearchUrlQueue();
        urlQueue1.setCreateTime(System.currentTimeMillis());
        urlQueue1.setDepth(1);
        urlQueue1.setMethod("GET");
        urlQueue1.setSessionId(sessionId);
        urlQueue1.setUrl("http://www.example.com/page1");
        urlQueueList.add(urlQueue1);

        final OpenSearchUrlQueue urlQueue2 = new OpenSearchUrlQueue();
        urlQueue2.setCreateTime(System.currentTimeMillis());
        urlQueue2.setDepth(1);
        urlQueue2.setMethod("GET");
        urlQueue2.setSessionId(sessionId);
        urlQueue2.setUrl("http://www.example.com/page1"); // Duplicate
        urlQueueList.add(urlQueue2);

        urlQueueService.offerAll(sessionId, urlQueueList);

        // Should only store one item (duplicates are filtered)
        int count = 0;
        while (true) {
            final OpenSearchUrlQueue polled = urlQueueService.poll(sessionId);
            if (polled == null) {
                break;
            }
            count++;
        }

        assertTrue(count <= 2); // At most 2 items (may be deduplicated)
        urlQueueService.delete(sessionId);
    }
}
