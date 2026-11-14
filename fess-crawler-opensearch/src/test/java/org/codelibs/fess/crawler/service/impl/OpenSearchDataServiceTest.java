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
import java.util.List;
import java.util.UUID;

import org.codelibs.fess.crawler.client.FesenClient;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.OpenSearchAccessResult;
import org.codelibs.fess.crawler.entity.OpenSearchAccessResultData;
import org.codelibs.fess.crawler.util.OpenSearchResultList;
import org.codelibs.fesen.opensearch.search.sort.SortOrder;
import org.codelibs.opensearch.runner.OpenSearchRunner;
import org.dbflute.utflute.lastadi.LastaDiTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;

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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    protected OpenSearchAccessResult newAccessResult(final String sessionId, final String url, final long createTime, final byte[] data) {
        final OpenSearchAccessResult accessResult = new OpenSearchAccessResult();
        accessResult.setCreateTime(createTime);
        accessResult.setHttpStatusCode(200);
        accessResult.setMethod("GET");
        accessResult.setMimeType("text/html");
        accessResult.setSessionId(sessionId);
        accessResult.setStatus(0);
        accessResult.setUrl(url);
        if (data != null) {
            final OpenSearchAccessResultData accessResultData = new OpenSearchAccessResultData();
            accessResultData.setTransformerName("t");
            accessResultData.setData(data);
            accessResult.setAccessResultData(accessResultData);
        }
        return accessResult;
    }

    @Test
    public void test_store_oversizedDataIsDropped() {
        final String sessionId = "oversized";
        final long now = System.currentTimeMillis();
        // the first one cannot be read back when its data is stored: 38,000,000 bytes are 50,666,668 characters in base64
        dataService.store(newAccessResult(sessionId, "http://www.example.com/big", now - 2000, new byte[38_000_000]));
        dataService.store(newAccessResult(sessionId, "http://www.example.com/small", now - 1000, new byte[10]));

        final List<OpenSearchAccessResult> list = dataService.getAccessResultList(builder -> {
            builder.setQuery(QueryBuilders.termQuery("sessionId", sessionId));
            builder.setFrom(0);
            builder.setSize(10);
            builder.addSort("createTime", SortOrder.ASC);
        });

        assertEquals(2L, ((OpenSearchResultList<OpenSearchAccessResult>) list).getTotalHits());
        assertEquals(2, list.size());
        assertEquals("http://www.example.com/big", list.get(0).getUrl());
        assertEquals("http://www.example.com/small", list.get(1).getUrl());
        // the access result is kept, but without the data
        assertNull(dataService.getAccessResult(sessionId, "http://www.example.com/big").getAccessResultData());
        assertNotNull(dataService.getAccessResult(sessionId, "http://www.example.com/small").getAccessResultData());
    }

    protected List<OpenSearchAccessResult> getAccessResultList(final String sessionId, final int size) {
        return dataService.getAccessResultList(builder -> {
            builder.setQuery(QueryBuilders.termQuery("sessionId", sessionId));
            builder.setFrom(0);
            builder.setSize(size);
            builder.addSort("createTime", SortOrder.ASC);
        });
    }

    @Test
    public void test_getAccessResultList_reportsUnreadableAccessResults() {
        // a document stored before the limit existed, or by a data service without it
        dataService.setMaxDataSize(0);
        final String sessionId = "unreadable";
        final long now = System.currentTimeMillis();
        final OpenSearchAccessResult big = newAccessResult(sessionId, "http://www.example.com/big", now - 2000, new byte[38_000_000]);
        dataService.store(big);
        dataService.store(newAccessResult(sessionId, "http://www.example.com/small", now - 1000, new byte[10]));
        assertNotNull(big.getId());

        // OpenSearch leaves the unreadable one out and reports it only as a failed shard, so the first page is empty
        final OpenSearchResultList<OpenSearchAccessResult> firstPage =
                (OpenSearchResultList<OpenSearchAccessResult>) getAccessResultList(sessionId, 1);
        assertEquals(2L, firstPage.getTotalHits());
        assertEquals(0, firstPage.size());
        assertEquals(1, firstPage.getUnreadableList().size());
        assertEquals(big.getId(), firstPage.getUnreadableList().get(0).getId());

        final OpenSearchResultList<OpenSearchAccessResult> secondPage =
                (OpenSearchResultList<OpenSearchAccessResult>) getAccessResultList(sessionId, 10);
        assertEquals(1, secondPage.size());
        assertEquals("http://www.example.com/small", secondPage.get(0).getUrl());
        assertEquals(1, secondPage.getUnreadableList().size());

        // once they are deleted, the crawl is not blocked by them any more
        dataService.deleteAccessResults(secondPage.getUnreadableList());

        final OpenSearchResultList<OpenSearchAccessResult> cleaned =
                (OpenSearchResultList<OpenSearchAccessResult>) getAccessResultList(sessionId, 10);
        assertEquals(1L, cleaned.getTotalHits());
        assertEquals(1, cleaned.size());
        assertTrue(cleaned.getUnreadableList().isEmpty());
    }

    @Test
    public void test_getAccessResultList_noUnreadableAccessResults() {
        final String sessionId = "readable";
        final long now = System.currentTimeMillis();
        dataService.store(newAccessResult(sessionId, "http://www.example.com/a", now - 2000, new byte[10]));
        dataService.store(newAccessResult(sessionId, "http://www.example.com/b", now - 1000, null));

        final OpenSearchResultList<OpenSearchAccessResult> list =
                (OpenSearchResultList<OpenSearchAccessResult>) getAccessResultList(sessionId, 10);

        assertEquals(2, list.size());
        assertTrue(list.getUnreadableList().isEmpty());
    }

    @Test
    public void test_getAccessResultList_unreadableAccessResultDoesNotTakeTheReadableOnesOnTheSameShard() {
        // OpenSearch drops the hits of the whole shard when it cannot parse one source, so the readable ones that are
        // on the shard of the unreadable one are missing too. With 2 shards and 10 readable ones, there are some on both.
        // (When every shard fails, OpenSearch answers with an error, which is not what this is about.)
        dataService.setNumberOfShards(2);
        dataService.setMaxDataSize(0);
        final String sessionId = "twoshards";
        final long now = System.currentTimeMillis();
        final OpenSearchAccessResult big = newAccessResult(sessionId, "http://www.example.com/big", now - 20000, new byte[38_000_000]);
        dataService.store(big);
        final List<OpenSearchAccessResult> smallList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final OpenSearchAccessResult small =
                    newAccessResult(sessionId, "http://www.example.com/small" + i, now - 10000 + i, i % 2 == 0 ? new byte[10] : null);
            dataService.store(small);
            smallList.add(small);
        }

        final OpenSearchResultList<OpenSearchAccessResult> list =
                (OpenSearchResultList<OpenSearchAccessResult>) getAccessResultList(sessionId, 20);

        assertEquals(11L, list.getTotalHits());
        assertEquals(1, list.getUnreadableList().size());
        assertEquals(big.getId(), list.getUnreadableList().get(0).getId());
        // all the readable ones are in the page, including the ones that the failed shard took with it
        assertEquals(10, list.size());
        for (final OpenSearchAccessResult small : smallList) {
            assertTrue(list.stream().anyMatch(r -> small.getId().equals(r.getId()) && small.getUrl().equals(r.getUrl())));
        }
    }

    @Test
    public void test_update_oversizedDataIsDropped() {
        final String sessionId = "updated";
        final OpenSearchAccessResult accessResult =
                newAccessResult(sessionId, "http://www.example.com/big", System.currentTimeMillis(), new byte[10]);
        dataService.store(accessResult);
        assertNotNull(dataService.getAccessResult(sessionId, "http://www.example.com/big").getAccessResultData());

        dataService.setMaxDataSize(5);
        final OpenSearchAccessResult updated = dataService.getAccessResult(sessionId, "http://www.example.com/big");
        dataService.update(java.util.Collections.singletonList(updated));
        assertNull(dataService.getAccessResult(sessionId, "http://www.example.com/big").getAccessResultData());
    }
}
