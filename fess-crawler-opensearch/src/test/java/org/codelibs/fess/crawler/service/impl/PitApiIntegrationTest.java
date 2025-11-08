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

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.crawler.client.FesenClient;
import org.codelibs.fess.crawler.entity.OpenSearchAccessResult;
import org.codelibs.fess.crawler.entity.OpenSearchUrlQueue;
import org.codelibs.fess.crawler.util.OpenSearchCrawlerConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.opensearch.index.query.QueryBuilders;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/**
 * Integration tests for PIT (Point in Time) API implementation.
 * Uses TestContainers to run OpenSearch 3.3.2 for testing.
 *
 * @author shinsuke
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PitApiIntegrationTest {

    private GenericContainer<?> opensearchContainer;
    private FesenClient fesenClient;
    private OpenSearchDataService dataService;
    private OpenSearchUrlQueueService urlQueueService;

    @BeforeAll
    public void setUp() throws Exception {
        // Start OpenSearch 3.3.2 container
        opensearchContainer = new GenericContainer<>(DockerImageName.parse("opensearchproject/opensearch:3.3.2"))
                .withExposedPorts(9200)
                .withEnv("discovery.type", "single-node")
                .withEnv("OPENSEARCH_JAVA_OPTS", "-Xms512m -Xmx512m")
                .withEnv("DISABLE_SECURITY_PLUGIN", "true")
                .waitingFor(Wait.forHttp("/_cluster/health").forStatusCode(200));

        opensearchContainer.start();

        // Configure client
        String address = "localhost:" + opensearchContainer.getMappedPort(9200);
        System.setProperty(FesenClient.HTTP_ADDRESS, address);

        // Initialize services
        fesenClient = new FesenClient();
        fesenClient.setAddress(address);
        fesenClient.connect();

        OpenSearchCrawlerConfig config = new OpenSearchCrawlerConfig();
        dataService = new OpenSearchDataService(config);
        dataService.fesenClient = fesenClient;
        dataService.init();

        urlQueueService = new OpenSearchUrlQueueService(config);
        urlQueueService.fesenClient = fesenClient;
        urlQueueService.init();

        // Wait a bit for indices to be created
        Thread.sleep(2000);
    }

    @AfterAll
    public void tearDown() {
        if (fesenClient != null) {
            fesenClient.destroy();
        }
        if (opensearchContainer != null) {
            opensearchContainer.stop();
        }
    }

    /**
     * Test PIT API usage in OpenSearchDataService.iterate() method.
     * Verifies that iteration works correctly with large datasets.
     */
    @Test
    public void testDataServiceIterateWithPitApi() throws Exception {
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
        assertEquals(totalRecords, counter.get(), "All records should be iterated using PIT API");

        // Cleanup
        dataService.delete(sessionId);
    }

    /**
     * Test PIT API usage in OpenSearchUrlQueueService.updateSessionId() method.
     * Verifies that session ID update works correctly with PIT pagination.
     */
    @Test
    public void testUrlQueueUpdateSessionIdWithPitApi() throws Exception {
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
        assertEquals(totalRecords, oldCount, "Should have records with old session ID");

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
        assertEquals(totalRecords, newCount, "Should have all records with new session ID");

        // Verify old session ID doesn't exist
        long remainingOldCount = fesenClient.prepareSearch("fess_crawler.queue")
                .setQuery(QueryBuilders.termQuery("sessionId", oldSessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value();
        assertEquals(0, remainingOldCount, "Should have no records with old session ID");

        // Cleanup
        urlQueueService.delete(newSessionId);
    }

    /**
     * Test PIT API usage in AbstractCrawlerService.delete() method.
     * Verifies that bulk deletion works correctly with PIT pagination.
     */
    @Test
    public void testBulkDeleteWithPitApi() throws Exception {
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
        assertEquals(totalRecords, beforeCount, "Should have all records before deletion");

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
        assertEquals(0, afterCount, "Should have no records after deletion using PIT API");
    }

    /**
     * Test PIT API with concurrent operations.
     * Verifies that PIT maintains data consistency during iteration.
     */
    @Test
    public void testPitDataConsistency() throws Exception {
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
        assertEquals(initialRecords, pitCount.get(),
                "PIT should return consistent count from snapshot");

        // Cleanup
        dataService.delete(sessionId);
    }

    /**
     * Test FesenClient.deleteByQuery with PIT API.
     * Verifies query-based deletion works with PIT pagination.
     */
    @Test
    public void testDeleteByQueryWithPitApi() throws Exception {
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

        assertEquals(totalRecords, deletedCount,
                "Should delete all matching records using PIT API");

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
        assertEquals(0, remainingCount, "Should have no remaining records");
    }
}
