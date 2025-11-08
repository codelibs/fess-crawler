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
 * Integration tests for PIT (Point in Time) API implementation.
 * Uses OpenSearchRunner to test PIT API functionality.
 *
 * @author shinsuke
 */
public class PitApiIntegrationTest extends LastaDiTestCase {

    @Resource
    private OpenSearchDataService dataService;

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
    public void setUp() throws Exception {
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
     * Test PIT API usage in OpenSearchDataService.iterate() method.
     * Verifies that iteration works correctly with large datasets.
     */
    public void test_DataServiceIterateWithPitApi() throws Exception {
        final String sessionId = "test-session-iterate";
        final int totalRecords = 250; // More than default scroll size

        // Insert test data
        for (int i = 0; i < totalRecords; i++) {
            OpenSearchAccessResult result = new OpenSearchAccessResult();
            result.setSessionId(sessionId);
            result.setUrl("http://example.com/page-" + i);
            result.setMethod("GET");
            result.setHttpStatusCode(200);
            result.setStatus(200);
            result.setCreateTime(System.currentTimeMillis());
            result.setLastModified(System.currentTimeMillis());
            result.setContentLength(1000L);
            result.setExecutionTime(100);
            result.setMimeType("text/html");
            dataService.store(result);
        }

        // Wait for indexing
        Thread.sleep(2000);

        // Iterate using PIT API
        AtomicInteger counter = new AtomicInteger(0);
        dataService.iterate(sessionId, accessResult -> {
            assertNotNull(accessResult);
            assertEquals(sessionId, accessResult.getSessionId());
            counter.incrementAndGet();
        });

        // Verify all records were iterated
        assertEquals("All records should be iterated using PIT API", totalRecords, counter.get());

        // Cleanup
        dataService.delete(sessionId);
    }

    /**
     * Test PIT API usage in OpenSearchUrlQueueService.updateSessionId() method.
     * Verifies that session ID update works correctly with PIT pagination.
     */
    public void test_UrlQueueUpdateSessionIdWithPitApi() throws Exception {
        final String oldSessionId = "test-session-old";
        final String newSessionId = "test-session-new";
        final int totalRecords = 150;

        // Insert test data with old session ID
        List<OpenSearchUrlQueue> queues = new ArrayList<>();
        for (int i = 0; i < totalRecords; i++) {
            OpenSearchUrlQueue queue = new OpenSearchUrlQueue();
            queue.setSessionId(oldSessionId);
            queue.setUrl("http://example.com/url-" + i);
            queue.setMethod("GET");
            queue.setDepth(1);
            queue.setCreateTime(System.currentTimeMillis());
            queues.add(queue);
        }
        urlQueueService.offerAll(oldSessionId, queues);

        // Wait for indexing
        Thread.sleep(2000);

        // Verify old session ID exists
        long oldCount = fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", oldSessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value();
        assertEquals("Should have records with old session ID", totalRecords, oldCount);

        // Update session ID using PIT API
        urlQueueService.updateSessionId(oldSessionId, newSessionId);

        // Wait for update
        Thread.sleep(2000);

        // Verify new session ID exists
        long newCount = fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", newSessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value();
        assertEquals("Should have all records with new session ID", totalRecords, newCount);

        // Verify old session ID doesn't exist
        long remainingOldCount = fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", oldSessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value();
        assertEquals("Should have no records with old session ID", 0L, remainingOldCount);

        // Cleanup
        urlQueueService.delete(newSessionId);
    }

    /**
     * Test PIT API usage in AbstractCrawlerService.delete() method.
     * Verifies that bulk deletion works correctly with PIT pagination.
     */
    public void test_BulkDeleteWithPitApi() throws Exception {
        final String sessionId = "test-session-delete";
        final int totalRecords = 300;

        // Insert test data
        for (int i = 0; i < totalRecords; i++) {
            OpenSearchAccessResult result = new OpenSearchAccessResult();
            result.setSessionId(sessionId);
            result.setUrl("http://example.com/delete-" + i);
            result.setMethod("GET");
            result.setHttpStatusCode(200);
            result.setStatus(200);
            result.setCreateTime(System.currentTimeMillis());
            result.setLastModified(System.currentTimeMillis());
            result.setContentLength(500L);
            result.setExecutionTime(50);
            result.setMimeType("text/plain");
            dataService.store(result);
        }

        // Wait for indexing
        Thread.sleep(2000);

        // Verify data exists
        long beforeCount = fesenClient.prepareSearch(dataService.getIndex())
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value();
        assertEquals("Should have all records before deletion", totalRecords, beforeCount);

        // Delete using PIT API
        dataService.delete(sessionId);

        // Wait for deletion
        Thread.sleep(2000);

        // Verify data is deleted
        long afterCount = fesenClient.prepareSearch(dataService.getIndex())
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value();
        assertEquals("Should have no records after deletion using PIT API", 0L, afterCount);
    }

    /**
     * Test PIT API with concurrent operations.
     * Verifies that PIT maintains data consistency during iteration.
     */
    public void test_PitDataConsistency() throws Exception {
        final String sessionId = "test-session-consistency";
        final int initialRecords = 100;

        // Insert initial data
        for (int i = 0; i < initialRecords; i++) {
            OpenSearchAccessResult result = new OpenSearchAccessResult();
            result.setSessionId(sessionId);
            result.setUrl("http://example.com/consistent-" + i);
            result.setMethod("GET");
            result.setHttpStatusCode(200);
            result.setStatus(200);
            result.setCreateTime(System.currentTimeMillis());
            result.setLastModified(System.currentTimeMillis());
            result.setContentLength(100L);
            result.setExecutionTime(10);
            result.setMimeType("text/html");
            dataService.store(result);
        }

        // Wait for indexing
        Thread.sleep(2000);

        // Count records with PIT - should get snapshot
        AtomicInteger pitCount = new AtomicInteger(0);
        dataService.iterate(sessionId, accessResult -> {
            pitCount.incrementAndGet();
        });

        // Verify PIT returned all records from the snapshot
        assertEquals("PIT should return consistent count from snapshot", initialRecords, pitCount.get());

        // Cleanup
        dataService.delete(sessionId);
    }

    /**
     * Test FesenClient.deleteByQuery with PIT API.
     * Verifies query-based deletion works with PIT pagination.
     */
    public void test_DeleteByQueryWithPitApi() throws Exception {
        final String sessionId = "test-session-query-delete";
        final String index = dataService.getIndex();
        final int totalRecords = 200;

        // Insert test data
        for (int i = 0; i < totalRecords; i++) {
            OpenSearchAccessResult result = new OpenSearchAccessResult();
            result.setSessionId(sessionId);
            result.setUrl("http://example.com/query-delete-" + i);
            result.setMethod("GET");
            result.setHttpStatusCode(200);
            result.setStatus(200);
            result.setCreateTime(System.currentTimeMillis());
            result.setLastModified(System.currentTimeMillis());
            result.setContentLength(200L);
            result.setExecutionTime(20);
            result.setMimeType("application/json");
            dataService.store(result);
        }

        // Wait for indexing
        Thread.sleep(2000);

        // Delete by query using PIT API
        int deletedCount = fesenClient.deleteByQuery(index, null,
                QueryBuilders.termQuery("sessionId", sessionId));

        assertEquals("Should delete all matching records using PIT API", totalRecords, deletedCount);

        // Wait for deletion
        Thread.sleep(2000);

        // Verify deletion
        long remainingCount = fesenClient.prepareSearch(index)
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value();
        assertEquals("Should have no remaining records", 0L, remainingCount);
    }
}
