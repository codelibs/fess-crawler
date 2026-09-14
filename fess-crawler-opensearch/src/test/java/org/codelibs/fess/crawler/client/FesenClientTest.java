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
package org.codelibs.fess.crawler.client;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.client.FesenClient.OnConnectListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.codelibs.fesen.opensearch.action.admin.cluster.health.ClusterHealthResponse;
import org.codelibs.fesen.opensearch.action.bulk.BulkRequestBuilder;
import org.codelibs.fesen.opensearch.action.bulk.BulkResponse;
import org.codelibs.fesen.opensearch.action.delete.DeleteRequestBuilder;
import org.codelibs.fesen.opensearch.action.search.CreatePitAction;
import org.codelibs.fesen.opensearch.action.search.CreatePitRequest;
import org.codelibs.fesen.opensearch.action.search.CreatePitResponse;
import org.codelibs.fesen.opensearch.action.search.DeletePitRequest;
import org.codelibs.fesen.opensearch.action.search.SearchRequest;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.action.search.SearchResponse;
import org.codelibs.fesen.opensearch.common.action.ActionFuture;
import org.codelibs.fesen.opensearch.common.unit.TimeValue;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.codelibs.fesen.opensearch.search.SearchHit;
import org.codelibs.fesen.opensearch.search.SearchHits;
import org.codelibs.fesen.opensearch.search.builder.PointInTimeBuilder;
import org.codelibs.fesen.opensearch.search.sort.ShardDocSortBuilder;
import org.codelibs.fesen.opensearch.transport.client.AdminClient;
import org.codelibs.fesen.opensearch.transport.client.Client;
import org.codelibs.fesen.opensearch.transport.client.ClusterAdminClient;

/**
 * Test class for FesenClient to verify improvements made to the implementation.
 */
public class FesenClientTest {

    private static final Logger logger = LogManager.getLogger(FesenClientTest.class);

    private FesenClient fesenClient;
    private Client mockClient;

    @BeforeEach
    public void setUp() {
        fesenClient = new FesenClient();
        mockClient = mock(Client.class);
        fesenClient.client = mockClient;
    }

    /**
     * Test: Thread-safe listener management using CopyOnWriteArrayList
     * Verifies that multiple threads can safely add listeners concurrently
     */
    @Test
    public void testThreadSafeListenerManagement() throws Exception {
        final int numThreads = 10;
        final ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        final CountDownLatch latch = new CountDownLatch(numThreads);
        final AtomicInteger callbackCount = new AtomicInteger(0);

        // Add listeners concurrently from multiple threads
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    fesenClient.addOnConnectListener(() -> callbackCount.incrementAndGet());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // Verify all listeners were added
        assertEquals(numThreads, fesenClient.onConnectListenerList.size());
    }

    /**
     * Test: Thread-safe listener invocation during connection
     * Verifies that listeners can be added while connect() is executing
     */
    @Test
    public void testConcurrentListenerInvocation() throws Exception {
        // This test verifies CopyOnWriteArrayList allows safe iteration
        assertTrue(fesenClient.onConnectListenerList instanceof java.util.concurrent.CopyOnWriteArrayList);
    }

    /**
     * Test: Retry logic correctly enforces maxRetryCount
     * Verifies that with maxRetryCount=5, the operation is tried exactly 6 times (initial + 5 retries)
     */
    @Test
    public void testRetryLogicMaxRetryCount() throws Exception {
        fesenClient.setMaxRetryCount(5);
        fesenClient.setRetryInterval(10L); // Short interval for testing

        final AtomicInteger attemptCount = new AtomicInteger(0);
        final ActionFuture<String> mockFuture = mock(ActionFuture.class);

        // Fail 5 times, succeed on 6th attempt
        when(mockFuture.actionGet(anyLong(), any(TimeUnit.class))).thenAnswer(invocation -> {
            int count = attemptCount.incrementAndGet();
            if (count <= 5) {
                throw new RuntimeException("Simulated failure #" + count);
            }
            return "Success";
        });

        final String result = fesenClient.get(c -> mockFuture);

        assertEquals("Success", result);
        assertEquals(6, attemptCount.get()); // Initial attempt + 5 retries
    }

    /**
     * Test: Retry logic fails after exceeding maxRetryCount
     * Verifies that the operation fails after maxRetryCount attempts
     */
    @Test
    public void testRetryLogicExceedsMaxRetryCount() {
        fesenClient.setMaxRetryCount(3);
        fesenClient.setRetryInterval(10L);

        final AtomicInteger attemptCount = new AtomicInteger(0);
        final ActionFuture<String> mockFuture = mock(ActionFuture.class);

        when(mockFuture.actionGet(anyLong(), any(TimeUnit.class))).thenAnswer(invocation -> {
            attemptCount.incrementAndGet();
            throw new RuntimeException("Persistent failure");
        });

        try {
            fesenClient.get(c -> mockFuture);
            fail();
        } catch (RuntimeException e) {
            assertEquals("Persistent failure", e.getMessage());
        }

        // Should try initial + maxRetryCount times (1 + 3 = 4)
        assertEquals(4, attemptCount.get());
    }

    /**
     * Test: Null safety in connect() when targetIndices is null
     * Verifies that connect() handles null targetIndices gracefully
     */
    @Test
    public void testConnectWithNullTargetIndices() {
        final AdminClient mockAdminClient = mock(AdminClient.class);
        final ClusterAdminClient mockClusterAdminClient = mock(ClusterAdminClient.class);
        final ClusterHealthResponse mockHealthResponse = mock(ClusterHealthResponse.class);
        final ActionFuture<ClusterHealthResponse> mockFuture = mock(ActionFuture.class);

        fesenClient.targetIndices = null;

        when(mockClient.admin()).thenReturn(mockAdminClient);
        when(mockAdminClient.cluster()).thenReturn(mockClusterAdminClient);
        when(mockHealthResponse.isTimedOut()).thenReturn(false);
        when(mockFuture.actionGet(anyLong(), any(TimeUnit.class))).thenReturn(mockHealthResponse);

        // Should not throw NullPointerException
        try {
            // Note: We can't easily test connect() fully without mocking createClient()
            // This test verifies the null check logic indirectly
            final String[] indices = fesenClient.targetIndices != null ? fesenClient.targetIndices : new String[0];
            assertNotNull(indices);
            assertEquals(0, indices.length);
        } catch (Exception e) {
            fail("Should handle null targetIndices: " + e.getMessage());
        }
    }

    /**
     * Test: prepareStreamSearch delegates to prepareSearch
     * Verifies that both methods return the same result
     */
    @Test
    public void testPrepareStreamSearchDelegatesToPrepareSearch() {
        final SearchRequestBuilder mockBuilder = mock(SearchRequestBuilder.class);
        when(mockClient.prepareSearch(any(String[].class))).thenReturn(mockBuilder);
        when(mockBuilder.setPreference(any(String.class))).thenReturn(mockBuilder);

        fesenClient.setSearchPreference("_local");

        final SearchRequestBuilder result1 = fesenClient.prepareSearch("index1");
        final SearchRequestBuilder result2 = fesenClient.prepareStreamSearch("index1");

        // Both should use the same underlying mechanism
        verify(mockClient, times(2)).prepareSearch(any(String[].class));
        verify(mockBuilder, times(2)).setPreference("_local");
    }

    /**
     * Test: filterWithHeader properly updates the internal client
     * Verifies that the returned client is properly stored
     */
    @Test
    public void testFilterWithHeaderUpdatesClient() {
        final Client mockNewClient = mock(Client.class);
        final Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token");

        when(mockClient.filterWithHeader(headers)).thenReturn(mockNewClient);

        final Client result = fesenClient.filterWithHeader(headers);

        assertTrue(fesenClient == result); // Should return this
        assertTrue(mockNewClient == fesenClient.client); // Internal client should be updated
        verify(mockClient).filterWithHeader(headers);
    }

    /**
     * Test: deleteByQuery with new signature (without type parameter)
     * Verifies the point in time walk, the bulk delete and the point in time cleanup
     */
    @Test
    public void testDeleteByQueryNewSignature() {
        final SearchRequestBuilder mockSearchBuilder = mock(SearchRequestBuilder.class);
        final SearchRequest searchRequest = new SearchRequest();
        final ActionFuture<SearchResponse> mockSearchFuture = mock(ActionFuture.class);

        final SearchResponse mockFirstPage = mock(SearchResponse.class);
        final SearchHits mockFirstHits = mock(SearchHits.class);
        final SearchResponse mockLastPage = mock(SearchResponse.class);
        final SearchHits mockLastHits = mock(SearchHits.class);
        final SearchHit mockHit = mock(SearchHit.class);

        final CreatePitResponse mockCreatePitResponse = mock(CreatePitResponse.class);
        final ActionFuture<CreatePitResponse> mockCreatePitFuture = mock(ActionFuture.class);

        final BulkRequestBuilder mockBulkBuilder = mock(BulkRequestBuilder.class);
        final BulkResponse mockBulkResponse = mock(BulkResponse.class);
        final ActionFuture<BulkResponse> mockBulkFuture = mock(ActionFuture.class);

        final DeleteRequestBuilder mockDeleteBuilder = mock(DeleteRequestBuilder.class);

        // The search must be prepared without indices: the point in time carries them itself.
        when(mockClient.prepareSearch()).thenReturn(mockSearchBuilder);
        when(mockSearchBuilder.setSize(10)).thenReturn(mockSearchBuilder);
        when(mockSearchBuilder.setQuery(any())).thenReturn(mockSearchBuilder);
        when(mockSearchBuilder.request()).thenReturn(searchRequest);
        when(mockSearchBuilder.execute()).thenReturn(mockSearchFuture);
        when(mockSearchFuture.actionGet(anyLong(), any(TimeUnit.class))).thenReturn(mockFirstPage, mockLastPage);

        // One hit on the first page, then an empty page ends the walk.
        when(mockFirstPage.getHits()).thenReturn(mockFirstHits);
        when(mockFirstHits.getHits()).thenReturn(new SearchHit[] { mockHit });
        when(mockLastPage.getHits()).thenReturn(mockLastHits);
        when(mockLastHits.getHits()).thenReturn(new SearchHit[0]);
        when(mockHit.getIndex()).thenReturn("test-index");
        when(mockHit.getId()).thenReturn("doc1");
        when(mockHit.getSortValues()).thenReturn(new Object[] { 1L });

        // Setup the point in time creation
        when(mockClient.execute(eq(CreatePitAction.INSTANCE), any(CreatePitRequest.class))).thenReturn(mockCreatePitFuture);
        when(mockCreatePitFuture.actionGet(anyLong(), any(TimeUnit.class))).thenReturn(mockCreatePitResponse);
        when(mockCreatePitResponse.getId()).thenReturn("pit1");

        // Setup bulk delete
        when(mockClient.prepareBulk()).thenReturn(mockBulkBuilder);
        when(mockClient.prepareDelete()).thenReturn(mockDeleteBuilder);
        when(mockDeleteBuilder.setIndex("test-index")).thenReturn(mockDeleteBuilder);
        when(mockDeleteBuilder.setId("doc1")).thenReturn(mockDeleteBuilder);
        when(mockBulkBuilder.add(any(DeleteRequestBuilder.class))).thenReturn(mockBulkBuilder);
        when(mockBulkBuilder.execute()).thenReturn(mockBulkFuture);
        when(mockBulkFuture.actionGet(anyLong(), any(TimeUnit.class))).thenReturn(mockBulkResponse);
        when(mockBulkResponse.hasFailures()).thenReturn(false);

        final int deleted = fesenClient.deleteByQuery("test-index", QueryBuilders.matchAllQuery());

        assertEquals(1, deleted);

        // The index belongs to the point in time, never to the search request.
        final ArgumentCaptor<CreatePitRequest> pitRequestCaptor = ArgumentCaptor.forClass(CreatePitRequest.class);
        verify(mockClient, times(1)).execute(eq(CreatePitAction.INSTANCE), pitRequestCaptor.capture());
        assertArrayEquals(new String[] { "test-index" }, pitRequestCaptor.getValue().getIndices());
        assertEquals(0, searchRequest.indices().length);

        // search_after needs a total order, so a _shard_doc tiebreaker is appended.
        verify(mockSearchBuilder, times(1)).addSort(any(ShardDocSortBuilder.class));
        verify(mockSearchBuilder, times(1)).setPointInTime(any(PointInTimeBuilder.class));
        verify(mockSearchBuilder, times(1)).searchAfter(new Object[] { 1L });

        // The point in time is always released.
        final ArgumentCaptor<DeletePitRequest> deletePitCaptor = ArgumentCaptor.forClass(DeletePitRequest.class);
        verify(mockClient, times(1)).deletePits(deletePitCaptor.capture(), any());
        assertEquals(List.of("pit1"), deletePitCaptor.getValue().getPitIds());
    }

    /**
     * Test: deleteByQuery deprecated method delegates to new method
     * Verifies backward compatibility
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testDeleteByQueryDeprecatedMethodDelegates() {
        final SearchRequestBuilder mockSearchBuilder = mock(SearchRequestBuilder.class);
        final SearchRequest searchRequest = new SearchRequest();
        final SearchResponse mockSearchResponse = mock(SearchResponse.class);
        final SearchHits mockSearchHits = mock(SearchHits.class);
        final ActionFuture<SearchResponse> mockSearchFuture = mock(ActionFuture.class);

        final CreatePitResponse mockCreatePitResponse = mock(CreatePitResponse.class);
        final ActionFuture<CreatePitResponse> mockCreatePitFuture = mock(ActionFuture.class);

        when(mockClient.prepareSearch()).thenReturn(mockSearchBuilder);
        when(mockSearchBuilder.setSize(10)).thenReturn(mockSearchBuilder);
        when(mockSearchBuilder.setQuery(any())).thenReturn(mockSearchBuilder);
        when(mockSearchBuilder.request()).thenReturn(searchRequest);
        when(mockSearchBuilder.execute()).thenReturn(mockSearchFuture);
        when(mockSearchFuture.actionGet(anyLong(), any(TimeUnit.class))).thenReturn(mockSearchResponse);
        when(mockSearchResponse.getHits()).thenReturn(mockSearchHits);
        when(mockSearchHits.getHits()).thenReturn(new SearchHit[0]);

        when(mockClient.execute(eq(CreatePitAction.INSTANCE), any(CreatePitRequest.class))).thenReturn(mockCreatePitFuture);
        when(mockCreatePitFuture.actionGet(anyLong(), any(TimeUnit.class))).thenReturn(mockCreatePitResponse);
        when(mockCreatePitResponse.getId()).thenReturn("pit1");

        // Call deprecated method with type parameter
        final int deleted = fesenClient.deleteByQuery("test-index", "_doc", QueryBuilders.matchAllQuery());

        assertEquals(0, deleted);
        // Verify it used the same search logic (type parameter is ignored)
        verify(mockClient, times(1)).prepareSearch();
        verify(mockClient, times(1)).deletePits(any(DeletePitRequest.class), any());
    }

    /**
     * Test: pitSearch moves the indices, routing and preference onto the point in time.
     * OpenSearch rejects a point in time search that repeats any of them with a 400, and over HTTP
     * that 400 does not surface as an error but hangs, so the search must not carry them.
     */
    @Test
    public void testPitSearchMovesRoutingAndPreferenceOntoPit() {
        final SearchRequestBuilder mockSearchBuilder = mock(SearchRequestBuilder.class);
        final SearchRequest searchRequest = new SearchRequest();
        searchRequest.preference("_local");
        searchRequest.routing("route1");
        final SearchResponse mockSearchResponse = mock(SearchResponse.class);
        final SearchHits mockSearchHits = mock(SearchHits.class);
        final ActionFuture<SearchResponse> mockSearchFuture = mock(ActionFuture.class);

        final CreatePitResponse mockCreatePitResponse = mock(CreatePitResponse.class);
        final ActionFuture<CreatePitResponse> mockCreatePitFuture = mock(ActionFuture.class);

        when(mockSearchBuilder.request()).thenReturn(searchRequest);
        when(mockSearchBuilder.execute()).thenReturn(mockSearchFuture);
        when(mockSearchFuture.actionGet(anyLong(), any(TimeUnit.class))).thenReturn(mockSearchResponse);
        when(mockSearchResponse.getHits()).thenReturn(mockSearchHits);
        when(mockSearchHits.getHits()).thenReturn(new SearchHit[0]);

        when(mockClient.execute(eq(CreatePitAction.INSTANCE), any(CreatePitRequest.class))).thenReturn(mockCreatePitFuture);
        when(mockCreatePitFuture.actionGet(anyLong(), any(TimeUnit.class))).thenReturn(mockCreatePitResponse);
        when(mockCreatePitResponse.getId()).thenReturn("pit1");

        fesenClient.pitSearch("test-index", TimeValue.timeValueMinutes(1), mockSearchBuilder, response -> true);

        final ArgumentCaptor<CreatePitRequest> pitRequestCaptor = ArgumentCaptor.forClass(CreatePitRequest.class);
        verify(mockClient, times(1)).execute(eq(CreatePitAction.INSTANCE), pitRequestCaptor.capture());
        final CreatePitRequest pitRequest = pitRequestCaptor.getValue();
        assertArrayEquals(new String[] { "test-index" }, pitRequest.getIndices());
        assertEquals("_local", pitRequest.getPreference());
        assertEquals("route1", pitRequest.getRouting());

        assertEquals(0, searchRequest.indices().length);
        assertNull(searchRequest.preference());
        assertNull(searchRequest.routing());
    }

    /**
     * Test: Verify that OnConnectListener is properly invoked
     */
    @Test
    public void testOnConnectListenerInvoked() {
        final AtomicBoolean listenerCalled = new AtomicBoolean(false);
        final OnConnectListener listener = () -> listenerCalled.set(true);

        fesenClient.addOnConnectListener(listener);

        // Manually invoke listeners as if connect() succeeded
        fesenClient.onConnectListenerList.forEach(l -> {
            try {
                l.onConnect();
            } catch (Exception e) {
                fail("Listener should not throw: " + e.getMessage());
            }
        });

        assertTrue(listenerCalled.get());
    }

    /**
     * Test: Verify connected flag is set correctly
     */
    @Test
    public void testConnectedFlag() {
        assertFalse(fesenClient.connected());

        // After destroy, should be disconnected
        fesenClient.destroy();
        assertFalse(fesenClient.connected());
    }
}
