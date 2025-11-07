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
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.crawler.client.FesenClient;
import org.codelibs.fess.crawler.entity.OpenSearchAccessResult;
import org.codelibs.fess.crawler.entity.OpenSearchUrlQueue;
import org.codelibs.opensearch.runner.OpenSearchRunner;
import org.dbflute.utflute.lastadi.LastaDiTestCase;
import org.opensearch.index.query.QueryBuilders;

import jakarta.annotation.Resource;

/**
 * Integration test for PIT API migration.
 * Tests the new PIT-based implementations of updateSessionId, delete, and iterate methods.
 *
 * @author shinsuke
 */
public class PitApiIntegrationTest extends LastaDiTestCase {
    @Resource
    private OpenSearchUrlQueueService urlQueueService;

    @Resource
    private OpenSearchDataService dataService;

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
    public void setUp() throws Exception {
        // create runner instance with OpenSearch 3.3.2 compatible settings
        runner = new OpenSearchRunner();
        // create OpenSearch node
        final String clusterName = UUID.randomUUID().toString();
        runner.onBuild((number, settingsBuilder) -> {
            settingsBuilder.put("http.cors.enabled", true);
            settingsBuilder.put("discovery.type", "single-node");
            // PIT settings
            settingsBuilder.put("point_in_time.max_keep_alive", "24h");
            settingsBuilder.put("search.max_open_pit_context", "300");
        }).build(newConfigs().clusterName(clusterName).numOfNode(1));

        // wait for yellow status
        runner.ensureYellow();

        System.setProperty(FesenClient.HTTP_ADDRESS, "localhost:" + runner.node().settings().get("http.port", "9201"));

        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        // close runner
        runner.close();
        // delete all files
        runner.clean();
    }

    /**
     * Test updateSessionId with PIT API - should handle large datasets properly
     */
    public void test_updateSessionId_withPitApi() throws Exception {
        final String oldSessionId = "old-session-" + System.currentTimeMillis();
        final String newSessionId = "new-session-" + System.currentTimeMillis();

        // Insert multiple URL queue entries with old session ID
        for (int i = 0; i < 150; i++) { // More than scrollSize (100) to test pagination
            final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
            urlQueue.setCreateTime(System.currentTimeMillis());
            urlQueue.setDepth(1);
            urlQueue.setMethod("GET");
            urlQueue.setSessionId(oldSessionId);
            urlQueue.setUrl("http://www.example.com/page" + i);
            urlQueueService.insert(urlQueue);
        }

        // Wait for indexing
        Thread.sleep(2000);

        // Verify old session exists
        long oldSessionCount = fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", oldSessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value();
        assertEquals(150, oldSessionCount);

        // Update session ID using PIT API
        urlQueueService.updateSessionId(oldSessionId, newSessionId);

        // Wait for indexing
        Thread.sleep(2000);

        // Verify old session is gone
        oldSessionCount = fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", oldSessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value();
        assertEquals(0, oldSessionCount);

        // Verify new session has all entries
        final long newSessionCount = fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", newSessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value();
        assertEquals(150, newSessionCount);

        // Clean up
        urlQueueService.delete(newSessionId);
    }

    /**
     * Test delete with PIT API - should delete all matching documents
     */
    public void test_delete_withPitApi() throws Exception {
        final String sessionId = "delete-test-" + System.currentTimeMillis();

        // Insert multiple URL queue entries
        for (int i = 0; i < 120; i++) { // More than scrollSize to test pagination
            final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
            urlQueue.setCreateTime(System.currentTimeMillis());
            urlQueue.setDepth(1);
            urlQueue.setMethod("GET");
            urlQueue.setSessionId(sessionId);
            urlQueue.setUrl("http://www.example.com/delete" + i);
            urlQueueService.insert(urlQueue);
        }

        // Wait for indexing
        Thread.sleep(2000);

        // Verify entries exist
        long count = fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value();
        assertEquals(120, count);

        // Delete using PIT API
        urlQueueService.delete(sessionId);

        // Wait for deletion
        Thread.sleep(2000);

        // Verify all entries are deleted
        count = fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value();
        assertEquals(0, count);
    }

    /**
     * Test iterate with PIT API - should iterate through all results consistently
     */
    public void test_iterate_withPitApi() throws Exception {
        final String sessionId = "iterate-test-" + System.currentTimeMillis();

        // Insert multiple access results
        final List<String> insertedUrls = new ArrayList<>();
        for (int i = 0; i < 130; i++) { // More than scrollSize to test pagination
            final OpenSearchAccessResult accessResult = new OpenSearchAccessResult();
            accessResult.setContentLength(Long.valueOf(100 + i));
            accessResult.setCreateTime(System.currentTimeMillis());
            accessResult.setExecutionTime(10);
            accessResult.setHttpStatusCode(200);
            accessResult.setLastModified(System.currentTimeMillis());
            accessResult.setMethod("GET");
            accessResult.setMimeType("text/html");
            accessResult.setParentUrl("http://www.parent.com/");
            accessResult.setRuleId("htmlRule");
            accessResult.setSessionId(sessionId);
            accessResult.setStatus(200);
            final String url = "http://www.example.com/iterate" + i;
            accessResult.setUrl(url);
            insertedUrls.add(url);
            dataService.store(accessResult);
        }

        // Wait for indexing
        Thread.sleep(2000);

        // Iterate using PIT API
        final List<String> iteratedUrls = new ArrayList<>();
        final AtomicInteger iterateCount = new AtomicInteger(0);
        dataService.iterate(sessionId, accessResult -> {
            iteratedUrls.add(accessResult.getUrl());
            iterateCount.incrementAndGet();
        });

        // Verify all results were iterated
        assertEquals(130, iterateCount.get());
        assertEquals(130, iteratedUrls.size());

        // Verify all URLs were found (order may differ)
        assertTrue(iteratedUrls.containsAll(insertedUrls));
        assertTrue(insertedUrls.containsAll(iteratedUrls));

        // Clean up
        dataService.delete(sessionId);
    }

    /**
     * Test that PIT handles concurrent index changes properly
     */
    public void test_pitConsistency_withConcurrentChanges() throws Exception {
        final String sessionId = "consistency-test-" + System.currentTimeMillis();

        // Insert initial data
        for (int i = 0; i < 50; i++) {
            final OpenSearchAccessResult accessResult = new OpenSearchAccessResult();
            accessResult.setContentLength(Long.valueOf(100));
            accessResult.setCreateTime(System.currentTimeMillis());
            accessResult.setExecutionTime(10);
            accessResult.setHttpStatusCode(200);
            accessResult.setLastModified(System.currentTimeMillis());
            accessResult.setMethod("GET");
            accessResult.setMimeType("text/html");
            accessResult.setParentUrl("http://www.parent.com/");
            accessResult.setRuleId("htmlRule");
            accessResult.setSessionId(sessionId);
            accessResult.setStatus(200);
            accessResult.setUrl("http://www.example.com/consistency" + i);
            dataService.store(accessResult);
        }

        // Wait for indexing
        Thread.sleep(2000);

        // Start iteration (which creates a PIT)
        final List<String> iteratedUrls = new ArrayList<>();
        final AtomicInteger iterateCount = new AtomicInteger(0);

        // Iterate - the PIT should see the snapshot at creation time
        dataService.iterate(sessionId, accessResult -> {
            iteratedUrls.add(accessResult.getUrl());
            iterateCount.incrementAndGet();
        });

        // Verify we got the original 50 records
        assertEquals(50, iterateCount.get());

        // Clean up
        dataService.delete(sessionId);
    }
}
