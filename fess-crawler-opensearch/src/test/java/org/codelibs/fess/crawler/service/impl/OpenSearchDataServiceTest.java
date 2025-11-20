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

import org.codelibs.fess.crawler.client.FesenClient;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.OpenSearchAccessResult;
import org.codelibs.opensearch.runner.OpenSearchRunner;
import org.dbflute.utflute.lastadi.LastaDiTestCase;
import org.opensearch.index.query.QueryBuilders;

import jakarta.annotation.Resource;

/**
 * @author shinsuke
 *
 */
public class OpenSearchDataServiceTest extends LastaDiTestCase {
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

    public void test_insert_deleteTx() {
        final OpenSearchAccessResult accessResult1 = new OpenSearchAccessResult();
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

        final OpenSearchAccessResult accessResult2 = dataService.getAccessResult("id1", "http://www.id1.com/");
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

    public void test_insert_delete_multiTx() {
        final OpenSearchAccessResult accessResult1 = new OpenSearchAccessResult();
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

        final OpenSearchAccessResult accessResult2 = new OpenSearchAccessResult();
        accessResult2.setContentLength(Long.valueOf(10));
        accessResult2.setCreateTime(System.currentTimeMillis());
        accessResult2.setExecutionTime(10);
        accessResult2.setHttpStatusCode(200);
        accessResult2.setLastModified(System.currentTimeMillis());
        accessResult2.setMethod("GET");
        accessResult2.setMimeType("text/plain");
        accessResult2.setParentUrl("http://www.parent.com/");
        accessResult2.setRuleId("htmlRule");
        accessResult2.setSessionId("id2");
        accessResult2.setStatus(200);
        accessResult2.setUrl("http://www.id2.com/");

        dataService.store(accessResult2);

        final OpenSearchAccessResult accessResult3 = dataService.getAccessResult("id1", "http://www.id1.com/");
        final OpenSearchAccessResult accessResult4 = dataService.getAccessResult("id2", "http://www.id2.com/");
        assertNotNull(accessResult3);
        assertNotNull(accessResult4);

        final List<OpenSearchAccessResult> accessResultList = new ArrayList<>();
        accessResult3.setMimeType("text/html");
        accessResult4.setMimeType("text/html");
        accessResultList.add(accessResult3);
        accessResultList.add(accessResult4);
        dataService.update(accessResultList);

        final AccessResult accessResult5 = dataService.getAccessResult("id1", "http://www.id1.com/");
        final AccessResult accessResult6 = dataService.getAccessResult("id2", "http://www.id2.com/");
        assertNotNull(accessResult5);
        assertNotNull(accessResult6);
        assertEquals("text/html", accessResult5.getMimeType());
        assertEquals("text/html", accessResult6.getMimeType());

        dataService.delete("id1");

        assertNull(dataService.getAccessResult("id1", "http://www.id1.com/"));
        assertNotNull(dataService.getAccessResult("id2", "http://www.id2.com/"));

        dataService.store(accessResult1);
        assertNotNull(dataService.getAccessResult("id1", "http://www.id1.com/"));

        dataService.deleteAll();

        assertNull(dataService.getAccessResult("id1", "http://www.id1.com/"));
        assertNull(dataService.getAccessResult("id2", "http://www.id2.com/"));
    }

    public void test_getAccessResultList_withCallbackTx() {
        final String sessionId = "callback_session1";

        // Insert test data
        for (int i = 1; i <= 5; i++) {
            final OpenSearchAccessResult accessResult = new OpenSearchAccessResult();
            accessResult.setContentLength(Long.valueOf(100 * i));
            accessResult.setCreateTime(System.currentTimeMillis());
            accessResult.setExecutionTime(10 * i);
            accessResult.setHttpStatusCode(200);
            accessResult.setLastModified(System.currentTimeMillis());
            accessResult.setMethod("GET");
            accessResult.setMimeType("text/html");
            accessResult.setParentUrl("http://www.parent.com/");
            accessResult.setRuleId("htmlRule");
            accessResult.setSessionId(sessionId);
            accessResult.setStatus(200);
            accessResult.setUrl("http://www.example.com/page" + i);

            dataService.store(accessResult);
        }

        // Test getAccessResultList with callback
        final List<OpenSearchAccessResult> results = dataService.getAccessResultList(builder -> {
            builder.setQuery(QueryBuilders.termQuery("sessionId", sessionId));
            builder.setSize(10);
        });

        assertNotNull(results);
        assertEquals(5, results.size());

        // Verify fields are properly fetched
        for (final OpenSearchAccessResult result : results) {
            assertNotNull(result.getUrl());
            assertNotNull(result.getSessionId());
            assertNotNull(result.getMimeType());
            assertNotNull(result.getMethod());
            assertEquals("text/html", result.getMimeType());
            assertEquals("GET", result.getMethod());
            assertEquals(sessionId, result.getSessionId());
        }

        dataService.delete(sessionId);
    }

    public void test_getAccessResultList_emptyResultTx() {
        final String sessionId = "callback_session2";

        // Test with non-existent session
        final List<OpenSearchAccessResult> results = dataService.getAccessResultList(builder -> {
            builder.setQuery(QueryBuilders.termQuery("sessionId", sessionId));
            builder.setSize(10);
        });

        assertNotNull(results);
        assertEquals(0, results.size());
    }

    public void test_getAccessResultList_withFilterTx() {
        final String sessionId = "callback_session3";

        // Insert test data with different MIME types
        final OpenSearchAccessResult htmlResult = new OpenSearchAccessResult();
        htmlResult.setContentLength(Long.valueOf(100));
        htmlResult.setCreateTime(System.currentTimeMillis());
        htmlResult.setExecutionTime(10);
        htmlResult.setHttpStatusCode(200);
        htmlResult.setLastModified(System.currentTimeMillis());
        htmlResult.setMethod("GET");
        htmlResult.setMimeType("text/html");
        htmlResult.setParentUrl("http://www.parent.com/");
        htmlResult.setRuleId("htmlRule");
        htmlResult.setSessionId(sessionId);
        htmlResult.setStatus(200);
        htmlResult.setUrl("http://www.example.com/page1.html");
        dataService.store(htmlResult);

        final OpenSearchAccessResult pdfResult = new OpenSearchAccessResult();
        pdfResult.setContentLength(Long.valueOf(200));
        pdfResult.setCreateTime(System.currentTimeMillis());
        pdfResult.setExecutionTime(20);
        pdfResult.setHttpStatusCode(200);
        pdfResult.setLastModified(System.currentTimeMillis());
        pdfResult.setMethod("GET");
        pdfResult.setMimeType("application/pdf");
        pdfResult.setParentUrl("http://www.parent.com/");
        pdfResult.setRuleId("pdfRule");
        pdfResult.setSessionId(sessionId);
        pdfResult.setStatus(200);
        pdfResult.setUrl("http://www.example.com/document.pdf");
        dataService.store(pdfResult);

        // Filter by MIME type
        final List<OpenSearchAccessResult> htmlResults = dataService.getAccessResultList(builder -> {
            builder.setQuery(QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery("sessionId", sessionId))
                    .must(QueryBuilders.termQuery("mimeType", "text/html")));
            builder.setSize(10);
        });

        assertNotNull(htmlResults);
        assertEquals(1, htmlResults.size());
        assertEquals("text/html", htmlResults.get(0).getMimeType());

        dataService.delete(sessionId);
    }

    public void test_getAccessResultList_withPaginationTx() {
        final String sessionId = "callback_session4";

        // Insert 10 test data
        for (int i = 1; i <= 10; i++) {
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
            accessResult.setUrl("http://www.example.com/page" + i);

            dataService.store(accessResult);
        }

        // Get first page (5 items)
        final List<OpenSearchAccessResult> page1 = dataService.getAccessResultList(builder -> {
            builder.setQuery(QueryBuilders.termQuery("sessionId", sessionId));
            builder.setFrom(0);
            builder.setSize(5);
        });

        assertEquals(5, page1.size());

        // Get second page (5 items)
        final List<OpenSearchAccessResult> page2 = dataService.getAccessResultList(builder -> {
            builder.setQuery(QueryBuilders.termQuery("sessionId", sessionId));
            builder.setFrom(5);
            builder.setSize(5);
        });

        assertEquals(5, page2.size());

        dataService.delete(sessionId);
    }

    public void test_iterate_withCallbackTx() {
        final String sessionId = "iterate_session1";

        // Insert test data
        for (int i = 1; i <= 10; i++) {
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
            accessResult.setUrl("http://www.example.com/page" + i);

            dataService.store(accessResult);
        }

        // Test iterate
        final List<String> urls = new ArrayList<>();
        dataService.iterate(sessionId, accessResult -> {
            urls.add(accessResult.getUrl());
        });

        assertEquals(10, urls.size());
        for (int i = 1; i <= 10; i++) {
            assertTrue(urls.contains("http://www.example.com/page" + i));
        }

        dataService.delete(sessionId);
    }

    public void test_getCount_Tx() {
        final String sessionId = "count_session1";

        // Initially no data
        assertEquals(0, dataService.getCount(sessionId));

        // Insert test data
        for (int i = 1; i <= 5; i++) {
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
            accessResult.setUrl("http://www.example.com/page" + i);

            dataService.store(accessResult);
        }

        // Verify count
        assertEquals(5, dataService.getCount(sessionId));

        dataService.delete(sessionId);

        // After delete, count should be 0
        assertEquals(0, dataService.getCount(sessionId));
    }

}
