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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResultImpl;
import org.codelibs.fess.crawler.entity.UrlQueueImpl;
import org.codelibs.fess.crawler.helper.MemoryDataHelper;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test class for {@link UrlQueueServiceImpl}.
 */
public class UrlQueueServiceImplTest extends PlainTestCase {

    @InjectMocks
    private UrlQueueServiceImpl service;

    @Mock
    private MemoryDataHelper dataHelper;

    @Override
    @BeforeEach
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        MockitoAnnotations.openMocks(this);
    }

    public void test_add() {
        // Setup
        String sessionId = "session123";
        String url = "https://example.com/page";
        Queue<UrlQueueImpl<Long>> mockQueue = new LinkedList<>();
        Set<String> mockUrlSet = new HashSet<>();

        when(dataHelper.getUrlQueueList(sessionId)).thenReturn(mockQueue);
        when(dataHelper.getUrlInQueueSet(sessionId)).thenReturn(mockUrlSet);

        // Execute
        service.add(sessionId, url);

        // Verify
        assertEquals(1, mockQueue.size());
        assertTrue(mockUrlSet.contains(url));
        UrlQueueImpl<Long> added = mockQueue.peek();
        assertNotNull(added);
        assertEquals(sessionId, added.getSessionId());
        assertEquals(url, added.getUrl());
        assertEquals(Constants.GET_METHOD, added.getMethod());
        assertEquals(Integer.valueOf(0), added.getDepth());
        assertNotNull(added.getCreateTime());
    }

    public void test_insert() {
        // Setup
        String sessionId = "session123";
        String url = "https://example.com";
        UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setSessionId(sessionId);
        urlQueue.setUrl(url);

        Queue<UrlQueueImpl<Long>> mockQueue = new LinkedList<>();
        Set<String> mockUrlSet = new HashSet<>();
        when(dataHelper.getUrlQueueList(sessionId)).thenReturn(mockQueue);
        when(dataHelper.getUrlInQueueSet(sessionId)).thenReturn(mockUrlSet);

        // Execute
        service.insert(urlQueue);

        // Verify
        assertEquals(1, mockQueue.size());
        assertTrue(mockQueue.contains(urlQueue));
        assertTrue(mockUrlSet.contains(url));
    }

    public void test_delete() {
        // Setup
        String sessionId = "session123";

        // Execute
        service.delete(sessionId);

        // Verify
        verify(dataHelper).removeUrlQueueList(sessionId);
    }

    public void test_deleteAll() {
        // Execute
        service.deleteAll();

        // Verify
        verify(dataHelper).clearUrlQueueList();
    }

    public void test_updateSessionId() {
        // Setup
        String oldSessionId = "oldSession";
        String newSessionId = "newSession";
        Queue<UrlQueueImpl<Long>> mockQueue = new LinkedList<>();

        when(dataHelper.getUrlQueueList(oldSessionId)).thenReturn(mockQueue);

        // Execute
        service.updateSessionId(oldSessionId, newSessionId);

        // Verify
        verify(dataHelper).getUrlQueueList(oldSessionId);
        verify(dataHelper).addUrlQueueList(newSessionId, mockQueue);
        verify(dataHelper).removeUrlQueueList(oldSessionId);
    }

    public void test_poll() {
        // Setup
        String sessionId = "session123";
        String url = "https://example.com";
        UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl(url);

        Queue<UrlQueueImpl<Long>> mockQueue = new LinkedList<>();
        mockQueue.add(urlQueue);
        Set<String> mockUrlSet = new HashSet<>();
        mockUrlSet.add(url);

        when(dataHelper.getUrlQueueList(sessionId)).thenReturn(mockQueue);
        when(dataHelper.getUrlInQueueSet(sessionId)).thenReturn(mockUrlSet);

        // Execute
        UrlQueueImpl<Long> result = service.poll(sessionId);

        // Verify
        assertNotNull(result);
        assertEquals(url, result.getUrl());
        assertTrue(mockQueue.isEmpty());
        assertFalse(mockUrlSet.contains(url));
    }

    public void test_pollEmptyQueue() {
        // Setup
        String sessionId = "session123";
        Queue<UrlQueueImpl<Long>> mockQueue = new LinkedList<>();
        Set<String> mockUrlSet = new HashSet<>();

        when(dataHelper.getUrlQueueList(sessionId)).thenReturn(mockQueue);
        when(dataHelper.getUrlInQueueSet(sessionId)).thenReturn(mockUrlSet);

        // Execute
        UrlQueueImpl<Long> result = service.poll(sessionId);

        // Verify
        assertNull(result);
    }

    public void test_offerAllNewUrls() {
        // Setup
        String sessionId = "session123";
        Queue<UrlQueueImpl<Long>> existingQueue = new LinkedList<>();
        Set<String> urlInQueueSet = new HashSet<>();
        Map<String, AccessResultImpl<Long>> accessResultMap = new HashMap<>();

        UrlQueueImpl<Long> newUrl1 = new UrlQueueImpl<>();
        newUrl1.setSessionId(sessionId);
        newUrl1.setUrl("https://example.com/new1");

        UrlQueueImpl<Long> newUrl2 = new UrlQueueImpl<>();
        newUrl2.setSessionId(sessionId);
        newUrl2.setUrl("https://example.com/new2");

        List<UrlQueueImpl<Long>> newUrlList = new ArrayList<>();
        newUrlList.add(newUrl1);
        newUrlList.add(newUrl2);

        when(dataHelper.getUrlQueueList(sessionId)).thenReturn(existingQueue);
        when(dataHelper.getUrlInQueueSet(sessionId)).thenReturn(urlInQueueSet);
        when(dataHelper.getAccessResultMap(sessionId)).thenReturn(accessResultMap);

        // Execute
        service.offerAll(sessionId, newUrlList);

        // Verify
        assertEquals(2, existingQueue.size());
        assertTrue(urlInQueueSet.contains("https://example.com/new1"));
        assertTrue(urlInQueueSet.contains("https://example.com/new2"));
    }

    public void test_offerAllWithDuplicatesInQueue() {
        // Setup
        String sessionId = "session123";
        Queue<UrlQueueImpl<Long>> existingQueue = new LinkedList<>();
        Set<String> urlInQueueSet = new HashSet<>();

        UrlQueueImpl<Long> existing = new UrlQueueImpl<>();
        existing.setUrl("https://example.com/existing");
        existingQueue.add(existing);
        urlInQueueSet.add("https://example.com/existing");

        UrlQueueImpl<Long> newUrl = new UrlQueueImpl<>();
        newUrl.setSessionId(sessionId);
        newUrl.setUrl("https://example.com/new");

        UrlQueueImpl<Long> duplicate = new UrlQueueImpl<>();
        duplicate.setSessionId(sessionId);
        duplicate.setUrl("https://example.com/existing");

        List<UrlQueueImpl<Long>> newUrlList = new ArrayList<>();
        newUrlList.add(newUrl);
        newUrlList.add(duplicate);

        Map<String, AccessResultImpl<Long>> accessResultMap = new HashMap<>();

        when(dataHelper.getUrlQueueList(sessionId)).thenReturn(existingQueue);
        when(dataHelper.getUrlInQueueSet(sessionId)).thenReturn(urlInQueueSet);
        when(dataHelper.getAccessResultMap(sessionId)).thenReturn(accessResultMap);

        // Execute
        service.offerAll(sessionId, newUrlList);

        // Verify - only the new URL should be added, duplicate should be skipped
        assertEquals(2, existingQueue.size());
        assertTrue(urlInQueueSet.contains("https://example.com/new"));
    }

    public void test_offerAllWithDuplicatesInAccessResult() {
        // Setup
        String sessionId = "session123";
        Queue<UrlQueueImpl<Long>> existingQueue = new LinkedList<>();
        Set<String> urlInQueueSet = new HashSet<>();
        Map<String, AccessResultImpl<Long>> accessResultMap = new HashMap<>();

        AccessResultImpl<Long> accessResult = new AccessResultImpl<>();
        accessResult.setUrl("https://example.com/visited");
        accessResultMap.put("https://example.com/visited", accessResult);

        UrlQueueImpl<Long> newUrl = new UrlQueueImpl<>();
        newUrl.setSessionId(sessionId);
        newUrl.setUrl("https://example.com/new");

        UrlQueueImpl<Long> visited = new UrlQueueImpl<>();
        visited.setSessionId(sessionId);
        visited.setUrl("https://example.com/visited");

        List<UrlQueueImpl<Long>> newUrlList = new ArrayList<>();
        newUrlList.add(newUrl);
        newUrlList.add(visited);

        when(dataHelper.getUrlQueueList(sessionId)).thenReturn(existingQueue);
        when(dataHelper.getUrlInQueueSet(sessionId)).thenReturn(urlInQueueSet);
        when(dataHelper.getAccessResultMap(sessionId)).thenReturn(accessResultMap);

        // Execute
        service.offerAll(sessionId, newUrlList);

        // Verify - only new URL should be added, visited should be skipped
        assertEquals(1, existingQueue.size());
        assertTrue(urlInQueueSet.contains("https://example.com/new"));
    }

    public void test_offerAllWithBlankUrl() {
        // Setup
        String sessionId = "session123";
        Queue<UrlQueueImpl<Long>> existingQueue = new LinkedList<>();
        Set<String> urlInQueueSet = new HashSet<>();
        Map<String, AccessResultImpl<Long>> accessResultMap = new HashMap<>();

        UrlQueueImpl<Long> blankUrl = new UrlQueueImpl<>();
        blankUrl.setSessionId(sessionId);
        blankUrl.setUrl("");

        UrlQueueImpl<Long> nullUrl = new UrlQueueImpl<>();
        nullUrl.setSessionId(sessionId);
        nullUrl.setUrl(null);

        UrlQueueImpl<Long> validUrl = new UrlQueueImpl<>();
        validUrl.setSessionId(sessionId);
        validUrl.setUrl("https://example.com");

        List<UrlQueueImpl<Long>> newUrlList = new ArrayList<>();
        newUrlList.add(blankUrl);
        newUrlList.add(nullUrl);
        newUrlList.add(validUrl);

        when(dataHelper.getUrlQueueList(sessionId)).thenReturn(existingQueue);
        when(dataHelper.getUrlInQueueSet(sessionId)).thenReturn(urlInQueueSet);
        when(dataHelper.getAccessResultMap(sessionId)).thenReturn(accessResultMap);

        // Execute
        service.offerAll(sessionId, newUrlList);

        // Verify - only valid URL should be added
        assertEquals(1, existingQueue.size());
        assertTrue(urlInQueueSet.contains("https://example.com"));
    }

    public void test_visited() {
        // Setup
        String sessionId = "session123";
        Queue<UrlQueueImpl<Long>> existingQueue = new LinkedList<>();
        Set<String> urlInQueueSet = new HashSet<>();

        UrlQueueImpl<Long> existing = new UrlQueueImpl<>();
        existing.setUrl("https://example.com/existing");
        existingQueue.add(existing);
        urlInQueueSet.add("https://example.com/existing");

        UrlQueueImpl<Long> visitedUrl = new UrlQueueImpl<>();
        visitedUrl.setSessionId(sessionId);
        visitedUrl.setUrl("https://example.com/existing");

        Map<String, AccessResultImpl<Long>> accessResultMap = new HashMap<>();

        when(dataHelper.getUrlQueueList(sessionId)).thenReturn(existingQueue);
        when(dataHelper.getUrlInQueueSet(sessionId)).thenReturn(urlInQueueSet);
        when(dataHelper.getAccessResultMap(sessionId)).thenReturn(accessResultMap);

        // Execute
        boolean result = service.visited(visitedUrl);

        // Verify
        assertTrue(result);
    }

    public void test_visitedNewUrl() {
        // Setup
        String sessionId = "session123";
        Queue<UrlQueueImpl<Long>> existingQueue = new LinkedList<>();
        Set<String> urlInQueueSet = new HashSet<>();
        Map<String, AccessResultImpl<Long>> accessResultMap = new HashMap<>();

        UrlQueueImpl<Long> newUrl = new UrlQueueImpl<>();
        newUrl.setSessionId(sessionId);
        newUrl.setUrl("https://example.com/new");

        when(dataHelper.getUrlQueueList(sessionId)).thenReturn(existingQueue);
        when(dataHelper.getUrlInQueueSet(sessionId)).thenReturn(urlInQueueSet);
        when(dataHelper.getAccessResultMap(sessionId)).thenReturn(accessResultMap);

        // Execute
        boolean result = service.visited(newUrl);

        // Verify
        assertFalse(result);
    }

    public void test_saveSession() {
        // Execute - should be a no-op
        service.saveSession("session123");

        // No verification needed as it's a no-op method
    }

    public void test_generateUrlQueues() {
        // Setup
        String previousSessionId = "prevSession";
        String sessionId = "newSession";
        Queue<UrlQueueImpl<Long>> newQueue = new LinkedList<>();
        Set<String> urlInQueueSet = new HashSet<>();
        Map<String, AccessResultImpl<Long>> accessResultMap = new HashMap<>();

        AccessResultImpl<Long> result1 = new AccessResultImpl<>();
        result1.setUrl("https://example.com/page1");
        result1.setMethod("GET");
        result1.setParentUrl("https://example.com");
        result1.setLastModified(123456789L);
        accessResultMap.put("https://example.com/page1", result1);

        AccessResultImpl<Long> result2 = new AccessResultImpl<>();
        result2.setUrl("https://example.com/page2");
        result2.setMethod("POST");
        result2.setParentUrl("https://example.com");
        result2.setLastModified(123456790L);
        accessResultMap.put("https://example.com/page2", result2);

        when(dataHelper.getUrlQueueList(sessionId)).thenReturn(newQueue);
        when(dataHelper.getUrlInQueueSet(sessionId)).thenReturn(urlInQueueSet);
        when(dataHelper.getAccessResultMap(previousSessionId)).thenReturn(accessResultMap);

        // Execute
        service.generateUrlQueues(previousSessionId, sessionId);

        // Verify
        assertEquals(2, newQueue.size());
        assertTrue(urlInQueueSet.contains("https://example.com/page1"));
        assertTrue(urlInQueueSet.contains("https://example.com/page2"));

        // Verify the generated queues have correct properties
        List<UrlQueueImpl<Long>> queueList = new ArrayList<>(newQueue);
        boolean foundPage1 = false;
        boolean foundPage2 = false;

        for (UrlQueueImpl<Long> queue : queueList) {
            assertEquals(sessionId, queue.getSessionId());
            assertEquals(Integer.valueOf(0), queue.getDepth());
            assertNotNull(queue.getCreateTime());

            if ("https://example.com/page1".equals(queue.getUrl())) {
                foundPage1 = true;
                assertEquals("GET", queue.getMethod());
                assertEquals("https://example.com", queue.getParentUrl());
                assertEquals(Long.valueOf(123456789L), queue.getLastModified());
            } else if ("https://example.com/page2".equals(queue.getUrl())) {
                foundPage2 = true;
                assertEquals("POST", queue.getMethod());
                assertEquals("https://example.com", queue.getParentUrl());
                assertEquals(Long.valueOf(123456790L), queue.getLastModified());
            }
        }

        assertTrue(foundPage1);
        assertTrue(foundPage2);
    }

    public void test_generateUrlQueuesEmpty() {
        // Setup
        String previousSessionId = "prevSession";
        String sessionId = "newSession";
        Queue<UrlQueueImpl<Long>> newQueue = new LinkedList<>();
        Set<String> urlInQueueSet = new HashSet<>();
        Map<String, AccessResultImpl<Long>> accessResultMap = new HashMap<>();

        when(dataHelper.getUrlQueueList(sessionId)).thenReturn(newQueue);
        when(dataHelper.getUrlInQueueSet(sessionId)).thenReturn(urlInQueueSet);
        when(dataHelper.getAccessResultMap(previousSessionId)).thenReturn(accessResultMap);

        // Execute
        service.generateUrlQueues(previousSessionId, sessionId);

        // Verify
        assertEquals(0, newQueue.size());
        assertTrue(urlInQueueSet.isEmpty());
    }
}
