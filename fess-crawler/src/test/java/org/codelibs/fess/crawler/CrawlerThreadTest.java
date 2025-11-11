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
package org.codelibs.fess.crawler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.CrawlerClient;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.entity.UrlQueueImpl;
import org.codelibs.fess.crawler.filter.UrlFilter;
import org.codelibs.fess.crawler.helper.LogHelper;
import org.codelibs.fess.crawler.interval.IntervalController;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.codelibs.fess.crawler.rule.Rule;
import org.codelibs.fess.crawler.rule.RuleManager;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test case for CrawlerThread class.
 */
public class CrawlerThreadTest extends PlainTestCase {

    private CrawlerThread crawlerThread;
    private CrawlerContext crawlerContext;
    private UrlQueueService<UrlQueue<?>> urlQueueService;
    private DataService dataService;
    private CrawlerContainer crawlerContainer;
    private LogHelper logHelper;
    private CrawlerClientFactory clientFactory;
    private UrlFilter urlFilter;
    private RuleManager ruleManager;

    @Override
    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();

        crawlerThread = new CrawlerThread();
        crawlerContext = new CrawlerContext();
        crawlerContext.sessionId = "test-session";
        crawlerContext.numOfThread = 1;
        crawlerContext.maxThreadCheckCount = 10;
        crawlerContext.maxDepth = 3;
        crawlerContext.maxAccessCount = 0;

        urlQueueService = mock(UrlQueueService.class);
        dataService = mock(DataService.class);
        crawlerContainer = mock(CrawlerContainer.class);
        logHelper = mock(LogHelper.class);
        clientFactory = mock(CrawlerClientFactory.class);
        urlFilter = mock(UrlFilter.class);
        ruleManager = mock(RuleManager.class);

        crawlerContext.urlFilter = urlFilter;
        crawlerContext.ruleManager = ruleManager;

        crawlerThread.urlQueueService = urlQueueService;
        crawlerThread.dataService = dataService;
        crawlerThread.crawlerContainer = crawlerContainer;
        crawlerThread.logHelper = logHelper;
        crawlerThread.setClientFactory(clientFactory);
        crawlerThread.setCrawlerContext(crawlerContext);

        when(crawlerContainer.available()).thenReturn(true);
    }

    /**
     * Test isValid method with a valid URL queue.
     */
    public void test_isValid_validUrlQueue() throws Exception {
        final UrlQueue<?> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/");
        urlQueue.setDepth(1);

        when(urlFilter.match(anyString())).thenReturn(true);

        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("isValid", UrlQueue.class);
        method.setAccessible(true);
        final boolean result = (boolean) method.invoke(crawlerThread, urlQueue);

        assertTrue(result);
    }

    /**
     * Test isValid method with a null URL queue.
     */
    public void test_isValid_nullUrlQueue() throws Exception {
        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("isValid", UrlQueue.class);
        method.setAccessible(true);
        final boolean result = (boolean) method.invoke(crawlerThread, (UrlQueue<?>) null);

        assertFalse(result);
    }

    /**
     * Test isValid method with a blank URL.
     */
    public void test_isValid_blankUrl() throws Exception {
        final UrlQueue<?> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("");
        urlQueue.setDepth(1);

        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("isValid", UrlQueue.class);
        method.setAccessible(true);
        final boolean result = (boolean) method.invoke(crawlerThread, urlQueue);

        assertFalse(result);
    }

    /**
     * Test isValid method with depth exceeding maxDepth.
     */
    public void test_isValid_exceedsMaxDepth() throws Exception {
        final UrlQueue<?> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/");
        urlQueue.setDepth(5); // Exceeds maxDepth of 3

        when(urlFilter.match(anyString())).thenReturn(true);

        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("isValid", UrlQueue.class);
        method.setAccessible(true);
        final boolean result = (boolean) method.invoke(crawlerThread, urlQueue);

        assertFalse(result);
    }

    /**
     * Test isValid method with URL not matching filter.
     */
    public void test_isValid_urlNotMatchingFilter() throws Exception {
        final UrlQueue<?> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/");
        urlQueue.setDepth(1);

        when(urlFilter.match(anyString())).thenReturn(false);

        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("isValid", UrlQueue.class);
        method.setAccessible(true);
        final boolean result = (boolean) method.invoke(crawlerThread, urlQueue);

        assertFalse(result);
    }

    /**
     * Test isContinue method when thread check count is below max.
     */
    public void test_isContinue_belowMaxThreadCheckCount() throws Exception {
        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("isContinue", int.class);
        method.setAccessible(true);
        final boolean result = (boolean) method.invoke(crawlerThread, 5);

        assertTrue(result);
    }

    /**
     * Test isContinue method when thread check count exceeds max.
     */
    public void test_isContinue_exceedsMaxThreadCheckCount() throws Exception {
        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("isContinue", int.class);
        method.setAccessible(true);
        final boolean result = (boolean) method.invoke(crawlerThread, 15);

        assertFalse(result);
    }

    /**
     * Test isContinue method when max access count is reached.
     */
    public void test_isContinue_maxAccessCountReached() throws Exception {
        crawlerContext.maxAccessCount = 10;
        crawlerContext.incrementAndGetAccessCount();
        crawlerContext.incrementAndGetAccessCount();
        crawlerContext.incrementAndGetAccessCount();
        crawlerContext.incrementAndGetAccessCount();
        crawlerContext.incrementAndGetAccessCount();
        crawlerContext.incrementAndGetAccessCount();
        crawlerContext.incrementAndGetAccessCount();
        crawlerContext.incrementAndGetAccessCount();
        crawlerContext.incrementAndGetAccessCount();
        crawlerContext.incrementAndGetAccessCount(); // accessCount = 10

        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("isContinue", int.class);
        method.setAccessible(true);
        final boolean result = (boolean) method.invoke(crawlerThread, 5);

        assertFalse(result);
    }

    /**
     * Test isContinue when container is not available.
     */
    public void test_isContinue_containerNotAvailable() throws Exception {
        when(crawlerContainer.available()).thenReturn(false);

        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("isContinue", int.class);
        method.setAccessible(true);
        final boolean result = (boolean) method.invoke(crawlerThread, 5);

        assertFalse(result);
    }

    /**
     * Test startCrawling increments active thread count.
     */
    public void test_startCrawling() throws Exception {
        assertEquals(Integer.valueOf(0), crawlerContext.getActiveThreadCount());

        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("startCrawling");
        method.setAccessible(true);
        method.invoke(crawlerThread);

        assertEquals(Integer.valueOf(1), crawlerContext.getActiveThreadCount());
    }

    /**
     * Test finishCrawling decrements active thread count.
     */
    public void test_finishCrawling() throws Exception {
        crawlerContext.setActiveThreadCount(1);

        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("finishCrawling");
        method.setAccessible(true);
        method.invoke(crawlerThread);

        assertEquals(Integer.valueOf(0), crawlerContext.getActiveThreadCount());
    }

    /**
     * Test storeChildUrl with a valid URL.
     */
    @SuppressWarnings("unchecked")
    public void test_storeChildUrl_validUrl() throws Exception {
        when(urlFilter.match("http://example.com/child")).thenReturn(true);
        when(crawlerContainer.getComponent("urlQueue")).thenReturn(new UrlQueueImpl<>());

        // Use reflection to access protected method
        final java.lang.reflect.Method method =
                CrawlerThread.class.getDeclaredMethod("storeChildUrl", String.class, String.class, float.class, int.class);
        method.setAccessible(true);
        method.invoke(crawlerThread, "http://example.com/child", "http://example.com/", 1.0f, 2);

        verify(urlQueueService, times(1)).offerAll(anyString(), any());
    }

    /**
     * Test storeChildUrl with depth exceeding maxDepth.
     */
    public void test_storeChildUrl_exceedsMaxDepth() throws Exception {
        when(urlFilter.match("http://example.com/child")).thenReturn(true);

        // Use reflection to access protected method
        final java.lang.reflect.Method method =
                CrawlerThread.class.getDeclaredMethod("storeChildUrl", String.class, String.class, float.class, int.class);
        method.setAccessible(true);
        method.invoke(crawlerThread, "http://example.com/child", "http://example.com/", 1.0f, 5); // Exceeds maxDepth

        verify(urlQueueService, times(0)).offerAll(anyString(), any());
    }

    /**
     * Test storeChildUrl with blank URL.
     */
    public void test_storeChildUrl_blankUrl() throws Exception {
        // Use reflection to access protected method
        final java.lang.reflect.Method method =
                CrawlerThread.class.getDeclaredMethod("storeChildUrl", String.class, String.class, float.class, int.class);
        method.setAccessible(true);
        method.invoke(crawlerThread, "", "http://example.com/", 1.0f, 2);

        verify(urlQueueService, times(0)).offerAll(anyString(), any());
    }

    /**
     * Test storeChildUrls with valid URLs.
     */
    @SuppressWarnings("unchecked")
    public void test_storeChildUrls_validUrls() throws Exception {
        final Set<RequestData> childUrlList = new HashSet<>();
        childUrlList.add(RequestDataBuilder.newRequestData().url("http://example.com/child1").build());
        childUrlList.add(RequestDataBuilder.newRequestData().url("http://example.com/child2").build());

        when(urlFilter.match(anyString())).thenReturn(true);
        when(crawlerContainer.getComponent("urlQueue")).thenReturn(new UrlQueueImpl<>(), new UrlQueueImpl<>());

        // Use reflection to access protected method
        final java.lang.reflect.Method method =
                CrawlerThread.class.getDeclaredMethod("storeChildUrls", Set.class, String.class, int.class);
        method.setAccessible(true);
        method.invoke(crawlerThread, childUrlList, "http://example.com/", 2);

        verify(urlQueueService, times(1)).offerAll(anyString(), any());
    }

    /**
     * Test storeChildUrls with depth exceeding maxDepth.
     */
    public void test_storeChildUrls_exceedsMaxDepth() throws Exception {
        final Set<RequestData> childUrlList = new HashSet<>();
        childUrlList.add(RequestDataBuilder.newRequestData().url("http://example.com/child1").build());

        when(urlFilter.match(anyString())).thenReturn(true);

        // Use reflection to access protected method
        final java.lang.reflect.Method method =
                CrawlerThread.class.getDeclaredMethod("storeChildUrls", Set.class, String.class, int.class);
        method.setAccessible(true);
        method.invoke(crawlerThread, childUrlList, "http://example.com/", 5); // Exceeds maxDepth

        verify(urlQueueService, times(0)).offerAll(anyString(), any());
    }

    /**
     * Test getClient method.
     */
    public void test_getClient() throws Exception {
        final CrawlerClient client = mock(CrawlerClient.class);
        when(clientFactory.getClient("http://example.com/")).thenReturn(client);

        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("getClient", String.class);
        method.setAccessible(true);
        final CrawlerClient result = (CrawlerClient) method.invoke(crawlerThread, "http://example.com/");

        assertNotNull(result);
        assertEquals(client, result);
    }

    /**
     * Test processResponse method.
     */
    public void test_processResponse() throws Exception {
        final UrlQueue<?> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/");

        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");

        final Rule rule = mock(Rule.class);
        final ResponseProcessor responseProcessor = mock(ResponseProcessor.class);

        when(ruleManager.getRule(responseData)).thenReturn(rule);
        when(rule.getRuleId()).thenReturn("test-rule");
        when(rule.getResponseProcessor()).thenReturn(responseProcessor);

        // Use reflection to access protected method
        final java.lang.reflect.Method method =
                CrawlerThread.class.getDeclaredMethod("processResponse", UrlQueue.class, ResponseData.class);
        method.setAccessible(true);
        method.invoke(crawlerThread, urlQueue, responseData);

        verify(responseProcessor, times(1)).process(responseData);
        assertEquals("test-rule", responseData.getRuleId());
    }

    /**
     * Test processResponse when no rule is found.
     */
    public void test_processResponse_noRule() throws Exception {
        final UrlQueue<?> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/");

        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");

        when(ruleManager.getRule(responseData)).thenReturn(null);

        // Use reflection to access protected method
        final java.lang.reflect.Method method =
                CrawlerThread.class.getDeclaredMethod("processResponse", UrlQueue.class, ResponseData.class);
        method.setAccessible(true);
        method.invoke(crawlerThread, urlQueue, responseData);

        // Should not throw exception, just log
    }

    /**
     * Test run method with no URLs in queue.
     */
    public void test_run_noUrlsInQueue() throws Exception {
        when(urlQueueService.poll(anyString())).thenReturn(null);
        crawlerContext.setStatus(CrawlerStatus.RUNNING);
        crawlerContext.maxThreadCheckCount = 1; // Will exit after 1 check

        crawlerThread.run();

        verify(urlQueueService, times(1)).poll(anyString());
    }

    /**
     * Test run method with crawler status DONE.
     */
    public void test_run_statusDone() throws Exception {
        crawlerContext.setStatus(CrawlerStatus.DONE);

        crawlerThread.run();

        verify(urlQueueService, times(0)).poll(anyString());
    }

    /**
     * Test setNoWaitOnFolder.
     */
    public void test_setNoWaitOnFolder() {
        assertFalse(crawlerThread.isNoWaitOnFolder());

        crawlerThread.setNoWaitOnFolder(true);
        assertTrue(crawlerThread.isNoWaitOnFolder());

        crawlerThread.setNoWaitOnFolder(false);
        assertFalse(crawlerThread.isNoWaitOnFolder());
    }

    /**
     * Test run with interval controller.
     */
    public void test_run_withIntervalController() throws Exception {
        final IntervalController intervalController = mock(IntervalController.class);
        crawlerContext.intervalController = intervalController;

        when(urlQueueService.poll(anyString())).thenReturn(null);
        crawlerContext.setStatus(CrawlerStatus.RUNNING);
        crawlerContext.maxThreadCheckCount = 1; // Will exit after 1 check

        crawlerThread.run();

        verify(intervalController, times(1)).delay(IntervalController.NO_URL_IN_QUEUE);
    }

    /**
     * Test isContinue with active threads still running.
     */
    public void test_isContinue_withActiveThreads() throws Exception {
        crawlerContext.setActiveThreadCount(2);

        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("isContinue", int.class);
        method.setAccessible(true);
        final boolean result = (boolean) method.invoke(crawlerThread, 15); // Exceeds maxThreadCheckCount

        assertTrue(result); // Should continue because active threads > 0
    }
}
