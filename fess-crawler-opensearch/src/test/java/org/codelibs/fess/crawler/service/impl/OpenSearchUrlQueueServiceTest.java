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

import static org.codelibs.fess.crawler.util.OpenSearchRunnerUtil.findFreePort;
import static org.codelibs.opensearch.runner.OpenSearchRunner.newConfigs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.crawler.client.FesenClient;
import org.codelibs.fess.crawler.entity.OpenSearchUrlQueue;
import org.codelibs.fess.crawler.order.UrlQueueOrder;
import org.codelibs.fess.crawler.order.impl.DepthFirstUrlQueueOrder;
import org.codelibs.fess.crawler.order.impl.SequentialUrlQueueOrder;
import org.codelibs.opensearch.runner.OpenSearchRunner;
import org.dbflute.utflute.lastadi.LastaDiTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.codelibs.fesen.opensearch.search.SearchHit;

import jakarta.annotation.Resource;

/**
 * @author shinsuke
 *
 */
public class OpenSearchUrlQueueServiceTest extends LastaDiTestCase {
    private static final String QUEUE_INDEX = "fess_crawler.queue";

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
        final int httpPort = findFreePort();
        // OpenSearchRunner binds the first node to (baseHttpPort + 1) and selects the port via an
        // unreliable connect-based scan. Pin it to the reserved free port and disable the scan to
        // avoid intermittent "Address already in use" failures.
        runner.setMaxHttpPort(-1);
        runner.onBuild((number, settingsBuilder) -> {
            settingsBuilder.put("http.cors.enabled", true);
            settingsBuilder.put("discovery.type", "single-node");
        }).build(newConfigs().clusterName(clusterName).numOfNode(1).baseHttpPort(httpPort - 1));

        // wait for yellow status
        runner.ensureYellow();

        System.setProperty(FesenClient.HTTP_ADDRESS, "localhost:" + runner.node().settings().get("http.port", String.valueOf(httpPort)));

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
    public void test_updateSessionIdTx() {
        final String oldSessionId = "update_session_old";
        final String newSessionId = "update_session_new";
        final String otherSessionId = "update_session_other";

        // A page size below the document count forces the search_after walk over more than one page.
        urlQueueService.setScrollSize(2);

        final List<String> urls = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
            urlQueue.setCreateTime(System.currentTimeMillis());
            urlQueue.setDepth(1);
            urlQueue.setMethod("GET");
            urlQueue.setSessionId(oldSessionId);
            urlQueue.setUrl("http://www.example.com/update" + i);
            urlQueueService.insert(urlQueue);
            urls.add(urlQueue.getUrl());
        }

        final OpenSearchUrlQueue otherQueue = new OpenSearchUrlQueue();
        otherQueue.setCreateTime(System.currentTimeMillis());
        otherQueue.setDepth(1);
        otherQueue.setMethod("GET");
        otherQueue.setSessionId(otherSessionId);
        otherQueue.setUrl("http://www.example.com/other");
        urlQueueService.insert(otherQueue);

        urlQueueService.updateSessionId(oldSessionId, newSessionId);
        refreshQueueIndex();

        // Every document of the old session moved exactly once, and no other session was touched.
        assertEquals(0L, countBySessionId(oldSessionId));
        assertEquals(5L, countBySessionId(newSessionId));
        assertEquals(1L, countBySessionId(otherSessionId));

        final List<String> movedUrls = new ArrayList<>();
        for (final SearchHit hit : fesenClient.prepareSearch(QUEUE_INDEX)
                .setQuery(QueryBuilders.termQuery("sessionId", newSessionId))
                .setSize(10)
                .execute()
                .actionGet()
                .getHits()) {
            movedUrls.add(hit.getSourceAsMap().get("url").toString());
        }
        Collections.sort(urls);
        Collections.sort(movedUrls);
        assertEquals(urls, movedUrls);

        urlQueueService.delete(newSessionId);
        urlQueueService.delete(otherSessionId);
    }

    @Test
    public void test_updateSessionId_noMatchTx() {
        final String sessionId = "update_session_keep";

        final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
        urlQueue.setCreateTime(System.currentTimeMillis());
        urlQueue.setDepth(1);
        urlQueue.setMethod("GET");
        urlQueue.setSessionId(sessionId);
        urlQueue.setUrl("http://www.example.com/keep");
        urlQueueService.insert(urlQueue);

        // An empty result set must release the point in time and leave the index untouched.
        urlQueueService.updateSessionId("update_session_missing", "update_session_created");
        refreshQueueIndex();

        assertEquals(1L, countBySessionId(sessionId));
        assertEquals(0L, countBySessionId("update_session_created"));

        urlQueueService.delete(sessionId);
    }

    private void refreshQueueIndex() {
        fesenClient.admin().indices().prepareRefresh(QUEUE_INDEX).execute().actionGet();
    }

    private long countBySessionId(final String sessionId) {
        return fesenClient.prepareSearch(QUEUE_INDEX)
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value();
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

    @Test
    public void test_getList_boolQueryIsFilteredBySessionId() {
        final OpenSearchUrlQueue target = new OpenSearchUrlQueue();
        target.setSessionId("session-a");
        target.setUrl("http://www.example.com/a");
        target.setCreateTime(System.currentTimeMillis());
        target.setDepth(1);
        target.setMethod("GET");
        urlQueueService.insert(target);

        final OpenSearchUrlQueue other = new OpenSearchUrlQueue();
        other.setSessionId("session-b");
        other.setUrl("http://www.example.com/b");
        other.setCreateTime(System.currentTimeMillis());
        other.setDepth(1);
        other.setMethod("GET");
        urlQueueService.insert(other);

        final List<OpenSearchUrlQueue> list = urlQueueService.getList(OpenSearchUrlQueue.class, "session-a",
                QueryBuilders.boolQuery().filter(QueryBuilders.rangeQuery(OpenSearchUrlQueue.DEPTH).gte(0)), 0, 10);

        assertEquals(1, list.size());
        assertEquals("session-a", list.get(0).getSessionId());
        assertEquals("http://www.example.com/a", list.get(0).getUrl());
    }

    @Test
    public void test_poll_followsTheConfiguredOrder() {
        final String sessionId = "order-session";
        for (int depth = 1; depth <= 3; depth++) {
            final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
            urlQueue.setSessionId(sessionId);
            urlQueue.setUrl("http://www.example.com/depth" + depth);
            urlQueue.setCreateTime(System.currentTimeMillis() + depth);
            urlQueue.setDepth(depth);
            urlQueue.setMethod("GET");
            urlQueueService.insert(urlQueue);
        }

        urlQueueService.setUrlQueueOrder(new DepthFirstUrlQueueOrder());
        try {
            for (int depth = 3; depth >= 1; depth--) {
                final OpenSearchUrlQueue polled = urlQueueService.poll(sessionId);
                assertNotNull(polled);
                assertEquals("http://www.example.com/depth" + depth, polled.getUrl());
            }
        } finally {
            urlQueueService.setUrlQueueOrder(new SequentialUrlQueueOrder());
            urlQueueService.clearCache();
        }
    }

    @Test
    public void test_poll_reevaluatesTheOrderOncePerBatch() {
        final String sessionId = "batch-session";
        final long base = System.currentTimeMillis();
        insertUrlQueue(sessionId, "http://www.example.com/shallow1", 1, base);
        insertUrlQueue(sessionId, "http://www.example.com/shallow2", 1, base + 1L);

        final int defaultFetchSize = urlQueueService.pollingFetchSize;
        urlQueueService.setUrlQueueOrder(new DepthFirstUrlQueueOrder());
        urlQueueService.setPollingFetchSize(2);
        try {
            // Both shallow URLs are fetched as one batch. Equal depth, so the newer wins.
            assertEquals("http://www.example.com/shallow2", urlQueueService.poll(sessionId).getUrl());

            // A deeper URL turns up while that batch is still being handed out.
            insertUrlQueue(sessionId, "http://www.example.com/deep", 5, base + 2L);

            // The batch is drained before the queue is consulted again, so the deeper URL
            // waits even though the order asks for the deepest first.
            assertEquals("http://www.example.com/shallow1", urlQueueService.poll(sessionId).getUrl());
            assertEquals("http://www.example.com/deep", urlQueueService.poll(sessionId).getUrl());
        } finally {
            urlQueueService.setUrlQueueOrder(new SequentialUrlQueueOrder());
            urlQueueService.setPollingFetchSize(defaultFetchSize);
            urlQueueService.clearCache();
        }
    }

    private void insertUrlQueue(final String sessionId, final String url, final int depth, final long createTime) {
        final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
        urlQueue.setSessionId(sessionId);
        urlQueue.setUrl(url);
        urlQueue.setCreateTime(createTime);
        urlQueue.setDepth(depth);
        urlQueue.setMethod("GET");
        urlQueueService.insert(urlQueue);
    }

    @Test
    public void test_di_registersTheBuiltInOrders() {
        for (final String name : new String[] { "sequentialUrlQueueOrder", "randomUrlQueueOrder", "depthFirstUrlQueueOrder",
                "newestFirstUrlQueueOrder", "weightFirstUrlQueueOrder" }) {
            final Object component = getComponent(name);
            assertNotNull(component);
            assertTrue(component instanceof UrlQueueOrder);
        }
    }
}
