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

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.codelibs.core.lang.SystemUtil;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.CrawlerClient;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.HostState;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.RobotsTxt;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.entity.UrlQueueImpl;
import org.codelibs.fess.crawler.exception.RobotsTxtDisallowedException;
import org.codelibs.fess.crawler.exception.RobotsTxtUnavailableException;
import org.codelibs.fess.crawler.filter.UrlFilter;
import org.codelibs.fess.crawler.helper.LogHelper;
import org.codelibs.fess.crawler.helper.RobotsTxtFetcher;
import org.codelibs.fess.crawler.helper.RobotsTxtHelper;
import org.codelibs.fess.crawler.helper.RobotsTxtPolicy;
import org.codelibs.fess.crawler.helper.RobotsTxtResponse;
import org.codelibs.fess.crawler.interval.IntervalController;
import org.codelibs.fess.crawler.interval.impl.DefaultIntervalController;
import org.codelibs.fess.crawler.log.LogType;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.codelibs.fess.crawler.rule.Rule;
import org.codelibs.fess.crawler.rule.RuleManager;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.codelibs.fess.crawler.weight.impl.DefaultUrlQueueWeigher;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

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
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

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
        crawlerThread.urlQueueWeigher = new DefaultUrlQueueWeigher();
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void test_startCrawling() throws Exception {
        assertEquals(0, crawlerContext.getActiveThreadCount());

        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("startCrawling");
        method.setAccessible(true);
        method.invoke(crawlerThread);

        assertEquals(1, crawlerContext.getActiveThreadCount());
    }

    /**
     * Test finishCrawling decrements active thread count.
     */
    @Test
    public void test_finishCrawling() throws Exception {
        crawlerContext.incrementAndGetActiveThreadCount();

        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("finishCrawling");
        method.setAccessible(true);
        method.invoke(crawlerThread);

        assertEquals(0, crawlerContext.getActiveThreadCount());
    }

    /**
     * Test storeChildUrl with a valid URL.
     */
    @SuppressWarnings("unchecked")
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void test_storeChildUrls_validUrls() throws Exception {
        final Set<RequestData> childUrlList = new HashSet<>();
        childUrlList.add(RequestDataBuilder.newRequestData().url("http://example.com/child1").build());
        childUrlList.add(RequestDataBuilder.newRequestData().url("http://example.com/child2").build());

        when(urlFilter.match(anyString())).thenReturn(true);
        when(crawlerContainer.getComponent("urlQueue")).thenReturn(new UrlQueueImpl<>(), new UrlQueueImpl<>());

        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("storeChildUrls", Set.class, String.class, int.class);
        method.setAccessible(true);
        method.invoke(crawlerThread, childUrlList, "http://example.com/", 2);

        verify(urlQueueService, times(1)).offerAll(anyString(), any());
    }

    /**
     * Test storeChildUrls with depth exceeding maxDepth.
     */
    @Test
    public void test_storeChildUrls_exceedsMaxDepth() throws Exception {
        final Set<RequestData> childUrlList = new HashSet<>();
        childUrlList.add(RequestDataBuilder.newRequestData().url("http://example.com/child1").build());

        when(urlFilter.match(anyString())).thenReturn(true);

        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("storeChildUrls", Set.class, String.class, int.class);
        method.setAccessible(true);
        method.invoke(crawlerThread, childUrlList, "http://example.com/", 5); // Exceeds maxDepth

        verify(urlQueueService, times(0)).offerAll(anyString(), any());
    }

    /**
     * Test that the weigher is applied to child URLs before they are queued.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test_storeChildUrls_appliesWeigher() throws Exception {
        final Set<RequestData> childUrlList = new HashSet<>();
        childUrlList.add(RequestDataBuilder.newRequestData().url("http://example.com/child1").build());

        when(urlFilter.match(anyString())).thenReturn(true);
        when(crawlerContainer.getComponent("urlQueue")).thenReturn(new UrlQueueImpl<>());

        final List<UrlQueue<?>> weighed = new ArrayList<>();
        crawlerThread.urlQueueWeigher = (sessionId, childList) -> {
            weighed.addAll(childList);
            childList.forEach(uq -> uq.setWeight(7.5f));
        };

        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("storeChildUrls", Set.class, String.class, int.class);
        method.setAccessible(true);
        method.invoke(crawlerThread, childUrlList, "http://example.com/", 2);

        assertEquals(1, weighed.size());
        assertEquals("test-session", weighed.get(0).getSessionId());
        assertEquals(Float.valueOf(7.5f), Float.valueOf(weighed.get(0).getWeight()));
        verify(urlQueueService, times(1)).offerAll(anyString(), any());
    }

    /**
     * Test that a throwing weigher does not lose the child URLs: they are still offered to the
     * queue with their inherited weights instead of the whole batch being dropped.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test_storeChildUrls_throwingWeigherKeepsInheritedWeights() throws Exception {
        final Set<RequestData> childUrlList = new HashSet<>();
        childUrlList.add(RequestDataBuilder.newRequestData().url("http://example.com/child1").weight(3.0f).build());

        when(urlFilter.match(anyString())).thenReturn(true);
        when(crawlerContainer.getComponent("urlQueue")).thenReturn(new UrlQueueImpl<>());

        crawlerThread.urlQueueWeigher = (sessionId, childList) -> {
            throw new RuntimeException("weigher failure");
        };

        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("storeChildUrls", Set.class, String.class, int.class);
        method.setAccessible(true);
        method.invoke(crawlerThread, childUrlList, "http://example.com/", 2);

        final org.mockito.ArgumentCaptor<List<UrlQueue<?>>> captor = org.mockito.ArgumentCaptor.forClass(List.class);
        verify(urlQueueService, times(1)).offerAll(anyString(), captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals(Float.valueOf(3.0f), Float.valueOf(captor.getValue().get(0).getWeight()));
    }

    /**
     * Test that the default weigher leaves the inherited weight untouched.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test_storeChildUrl_defaultWeigherKeepsInheritedWeight() throws Exception {
        when(urlFilter.match("http://example.com/child")).thenReturn(true);
        final UrlQueueImpl<Long> queued = new UrlQueueImpl<>();
        when(crawlerContainer.getComponent("urlQueue")).thenReturn(queued);

        crawlerThread.urlQueueWeigher = new DefaultUrlQueueWeigher();

        final java.lang.reflect.Method method =
                CrawlerThread.class.getDeclaredMethod("storeChildUrl", String.class, String.class, float.class, int.class);
        method.setAccessible(true);
        method.invoke(crawlerThread, "http://example.com/child", "http://example.com/", 3.0f, 2);

        assertEquals(Float.valueOf(3.0f), Float.valueOf(queued.getWeight()));
        verify(urlQueueService, times(1)).offerAll(anyString(), any());
    }

    /**
     * Test getClient method.
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void test_run_statusDone() throws Exception {
        crawlerContext.setStatus(CrawlerStatus.DONE);

        crawlerThread.run();

        verify(urlQueueService, times(0)).poll(anyString());
    }

    /**
     * Test setNoWaitOnFolder.
     */
    @Test
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
    @Test
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
    @Test
    public void test_isContinue_withActiveThreads() throws Exception {
        crawlerContext.incrementAndGetActiveThreadCount();
        crawlerContext.incrementAndGetActiveThreadCount();

        // Use reflection to access protected method
        final java.lang.reflect.Method method = CrawlerThread.class.getDeclaredMethod("isContinue", int.class);
        method.setAccessible(true);
        final boolean result = (boolean) method.invoke(crawlerThread, 15); // Exceeds maxThreadCheckCount

        assertTrue(result); // Should continue because active threads > 0
    }

    // -----------------------------------------------------------------------
    // robots.txt admission, 429/503 backoff and re-queue
    // -----------------------------------------------------------------------

    private static final String URL = "http://example.com/page";

    private final List<LogType> loggedTypes = new ArrayList<>();

    private CrawlerClient client;

    private ResponseProcessor responseProcessor;

    private List<UrlQueue<?>> inserted;

    /**
     * Wires the mocks so that {@link CrawlerThread#run()} processes the given queue entries once each and then stops.
     */
    @SuppressWarnings("unchecked")
    private void prepareRun(final UrlQueue<?>... queues) {
        crawlerContext.setStatus(CrawlerStatus.RUNNING);
        crawlerContext.maxThreadCheckCount = 1;
        crawlerThread.logHelper = (key, objs) -> loggedTypes.add(key);

        final Object[] rest = new Object[queues.length];
        System.arraycopy(queues, 1, rest, 0, queues.length - 1);
        org.mockito.Mockito.doReturn(queues[0], rest).when(urlQueueService).poll(anyString());
        inserted = new ArrayList<>();
        org.mockito.Mockito.doAnswer(invocation -> {
            inserted.add(invocation.getArgument(0));
            return null;
        }).when(urlQueueService).insert(any());

        when(urlFilter.match(anyString())).thenReturn(true);
        client = mock(CrawlerClient.class);
        when(clientFactory.getClient(anyString())).thenReturn(client);
        when(crawlerContainer.getComponent("urlQueue")).thenAnswer(invocation -> new UrlQueueImpl<Long>());

        final Rule rule = mock(Rule.class);
        responseProcessor = mock(ResponseProcessor.class);
        when(ruleManager.getRule(any())).thenReturn(rule);
        when(rule.getRuleId()).thenReturn("test-rule");
        when(rule.getResponseProcessor()).thenReturn(responseProcessor);
    }

    private static UrlQueueImpl<Long> newUrlQueue() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setId(42L);
        urlQueue.setUrl(URL);
        urlQueue.setMethod(Constants.GET_METHOD);
        urlQueue.setMetaData("meta");
        urlQueue.setEncoding("UTF-8");
        urlQueue.setParentUrl("http://example.com/");
        urlQueue.setDepth(1);
        urlQueue.setWeight(2.5f);
        urlQueue.setSessionId("test-session");
        urlQueue.setCreateTime(123L);
        return urlQueue;
    }

    private static ResponseData newResponse(final int status) {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl(URL);
        responseData.setMethod(Constants.GET_METHOD);
        responseData.setHttpStatusCode(status);
        return responseData;
    }

    @Test
    public void test_run_429WithRetryAfterRequeuesAndSkipsProcessing() throws Exception {
        final UrlQueueImpl<Long> urlQueue = newUrlQueue();
        prepareRun(urlQueue);
        final ResponseData response = newResponse(429);
        response.addMetaData("retry-after", "5");
        when(client.execute(any())).thenReturn(response);

        final long before = System.currentTimeMillis();
        crawlerThread.run();

        assertEquals(1, inserted.size());
        final UrlQueue<?> copy = inserted.get(0);
        assertFalse(urlQueue == copy);
        assertEquals(Long.valueOf(42L), copy.getId());
        assertEquals(URL, copy.getUrl());
        assertEquals(Constants.GET_METHOD, copy.getMethod());
        assertEquals("meta", copy.getMetaData());
        assertEquals("UTF-8", copy.getEncoding());
        assertEquals("http://example.com/", copy.getParentUrl());
        assertEquals(Integer.valueOf(1), copy.getDepth());
        assertNull(copy.getLastModified());
        assertEquals(Float.valueOf(2.5f), Float.valueOf(copy.getWeight()));
        assertEquals("test-session", copy.getSessionId());
        assertEquals(Long.valueOf(123L), copy.getCreateTime());
        verify(responseProcessor, times(0)).process(any());
        assertTrue(crawlerContext.peekHostState(URL).getBackoffUntil() >= before + 4000);
        assertFalse(loggedTypes.contains(LogType.CRAWLING_ACCESS_EXCEPTION));
    }

    @Test
    public void test_run_503RetriedUntilMaxRetryCountThenProcessed() throws Exception {
        crawlerContext.setMaxRetryCount(3);
        prepareRun(newUrlQueue(), newUrlQueue(), newUrlQueue(), newUrlQueue());
        when(client.execute(any())).thenAnswer(invocation -> newResponse(503));

        crawlerThread.run();

        assertEquals(3, inserted.size());
        verify(responseProcessor, times(1)).process(any());
    }

    @Test
    public void test_run_successAfter429ResetsExponentialBackoff() throws Exception {
        prepareRun(newUrlQueue(), newUrlQueue());
        when(client.execute(any())).thenReturn(newResponse(429), newResponse(200));

        crawlerThread.run();

        assertEquals(1, inserted.size());
        verify(responseProcessor, times(1)).process(any());
        final long wait = crawlerContext.peekHostState(URL)
                .recordFailure(System.currentTimeMillis(), 0L, crawlerContext.getBackoffBaseMillis(), crawlerContext.getMaxBackoffMillis());
        assertEquals(crawlerContext.getBackoffBaseMillis(), wait);
    }

    @Test
    public void test_run_successDoesNotCreateHostState() throws Exception {
        prepareRun(newUrlQueue());
        when(client.execute(any())).thenReturn(newResponse(200));

        crawlerThread.run();

        verify(responseProcessor, times(1)).process(any());
        assertNull(crawlerContext.peekHostState(URL));
    }

    @Test
    public void test_run_robotsTxtDisallowedIsNotAFailure() throws Exception {
        prepareRun(newUrlQueue());
        when(client.execute(any())).thenThrow(new RobotsTxtDisallowedException(URL));

        crawlerThread.run();

        assertEquals(0, inserted.size());
        verify(responseProcessor, times(0)).process(any());
        assertFalse(loggedTypes.contains(LogType.CRAWLING_ACCESS_EXCEPTION));
        assertFalse(loggedTypes.contains(LogType.CRAWLING_EXCEPTION));
    }

    @Test
    public void test_run_robotsTxtUnavailableRequeuesWithoutRecordingFailure() throws Exception {
        prepareRun(newUrlQueue());
        final HostState hostState = crawlerContext.getHostState(URL);
        when(client.execute(any())).thenThrow(new RobotsTxtUnavailableException(URL, 5000L, null));

        crawlerThread.run();

        assertEquals(1, inserted.size());
        assertEquals(URL, inserted.get(0).getUrl());
        verify(responseProcessor, times(0)).process(any());
        assertFalse(loggedTypes.contains(LogType.CRAWLING_ACCESS_EXCEPTION));
        // checkRobotsTxt has already recorded the failure; the thread must not record it a second time
        assertEquals(0L, hostState.getBackoffUntil());
        assertEquals(crawlerContext.getBackoffBaseMillis(), hostState.recordFailure(System.currentTimeMillis(), 0L,
                crawlerContext.getBackoffBaseMillis(), crawlerContext.getMaxBackoffMillis()));
    }

    @Test
    public void test_run_requeueFailureAfterRobotsTxtUnavailableDoesNotStopThread() throws Exception {
        final UrlQueueImpl<Long> next = newUrlQueue();
        next.setUrl("http://example.com/next");
        prepareRun(newUrlQueue(), next);
        org.mockito.Mockito.doThrow(new IllegalStateException("queue is down")).when(urlQueueService).insert(any());
        when(client.execute(any())).thenThrow(new RobotsTxtUnavailableException(URL, 0L, null)).thenReturn(newResponse(200));

        crawlerThread.run();

        verify(urlQueueService, times(1)).insert(any());
        verify(client, times(2)).execute(any());
        verify(responseProcessor, times(1)).process(any());
        assertTrue(loggedTypes.contains(LogType.CRAWLING_EXCEPTION));
        assertFalse(loggedTypes.contains(LogType.SYSTEM_ERROR));
    }

    @Test
    public void test_run_dequeuedDisallowedUrlIsNotAnEmptyPoll() throws Exception {
        final IntervalController intervalController = mock(IntervalController.class);
        crawlerContext.intervalController = intervalController;
        crawlerContext.getHostState(URL).setRobotsTxt(HostState.RobotsTxtStatus.DISALLOW_ALL, null, 0L);
        // maxThreadCheckCount is 1: if the disallowed entry counted as an empty poll, the loop would stop after it
        prepareRun(newUrlQueue(), newUrlQueue());

        crawlerThread.run();

        verify(urlQueueService, times(3)).poll(anyString());
        verify(intervalController, times(1)).delay(IntervalController.NO_URL_IN_QUEUE);
        verify(intervalController, times(1)).delay(IntervalController.WAIT_NEW_URL);
        verify(intervalController, times(0)).delay(IntervalController.PRE_PROCESSING);
        verify(client, times(0)).execute(any());
        assertFalse(loggedTypes.contains(LogType.CRAWLING_ACCESS_EXCEPTION));
    }

    private static HostState disallowPrivate(final HostState hostState) {
        final RobotsTxt.Directive directive = new RobotsTxt.Directive("*");
        directive.addDisallow("/private/");
        hostState.setRobotsTxt(HostState.RobotsTxtStatus.PARSED, directive, 0L);
        return hostState;
    }

    @Test
    public void test_storeChildUrls_skipsUrlsDisallowedByRobotsTxt() throws Exception {
        when(urlFilter.match(anyString())).thenReturn(true);
        when(crawlerContainer.getComponent("urlQueue")).thenAnswer(invocation -> new UrlQueueImpl<Long>());
        disallowPrivate(crawlerContext.getHostState("http://example.com/"));
        crawlerContext.getHostState("http://blocked.example.com/").setRobotsTxt(HostState.RobotsTxtStatus.DISALLOW_ALL, null, 0L);

        final Set<RequestData> childUrlList = new HashSet<>();
        childUrlList.add(RequestDataBuilder.newRequestData().url("http://example.com/private/a").build());
        childUrlList.add(RequestDataBuilder.newRequestData().url("http://blocked.example.com/b").build());
        childUrlList.add(RequestDataBuilder.newRequestData().url("http://example.com/public/c").build());
        childUrlList.add(RequestDataBuilder.newRequestData().url("http://unknown.example.com/d").build());
        crawlerThread.storeChildUrls(childUrlList, "http://example.com/", 2);

        @SuppressWarnings("unchecked")
        final org.mockito.ArgumentCaptor<List<UrlQueue<?>>> captor = org.mockito.ArgumentCaptor.forClass(List.class);
        verify(urlQueueService, times(1)).offerAll(anyString(), captor.capture());
        final Set<String> stored = new HashSet<>();
        captor.getValue().forEach(uq -> stored.add(uq.getUrl()));
        assertEquals(Set.of("http://example.com/public/c", "http://unknown.example.com/d"), stored);
        assertNull(crawlerContext.peekHostState("http://unknown.example.com/d"));
    }

    @Test
    public void test_storeChildUrl_skipsUrlDisallowedByRobotsTxt() throws Exception {
        when(urlFilter.match(anyString())).thenReturn(true);
        when(crawlerContainer.getComponent("urlQueue")).thenAnswer(invocation -> new UrlQueueImpl<Long>());
        disallowPrivate(crawlerContext.getHostState("http://example.com/"));
        crawlerContext.getHostState("http://blocked.example.com/").setRobotsTxt(HostState.RobotsTxtStatus.DISALLOW_ALL, null, 0L);

        crawlerThread.storeChildUrl("http://example.com/private/a", "http://example.com/", 1.0f, 2);
        crawlerThread.storeChildUrl("http://blocked.example.com/b", "http://example.com/", 1.0f, 2);
        verify(urlQueueService, times(0)).offerAll(anyString(), any());

        crawlerThread.storeChildUrl("http://example.com/public/c", "http://example.com/", 1.0f, 2);
        verify(urlQueueService, times(1)).offerAll(anyString(), any());
    }

    @Test
    public void test_run_robotsTxtUnavailableGivesUpAfterMaxRetryCount() throws Exception {
        crawlerContext.setMaxRetryCount(1);
        prepareRun(newUrlQueue(), newUrlQueue());
        when(client.execute(any())).thenThrow(new RobotsTxtUnavailableException(URL, 0L, null));

        crawlerThread.run();

        assertEquals(1, inserted.size());
        verify(responseProcessor, times(0)).process(any());
        assertFalse(loggedTypes.contains(LogType.CRAWLING_ACCESS_EXCEPTION));
    }

    @Test
    public void test_run_robotsTxtBackoffPendingDoesNotUseUpRetries() throws Exception {
        crawlerContext.setMaxRetryCount(1);
        prepareRun(newUrlQueue(), newUrlQueue(), newUrlQueue(), newUrlQueue());
        // robots.txt was not requested because the backoff of the origin has not ended: the URL has not been tried
        final RobotsTxtUnavailableException pending = new RobotsTxtUnavailableException(URL, 0L, null, false);
        when(client.execute(any())).thenThrow(pending, pending, pending).thenReturn(newResponse(200));

        crawlerThread.run();

        assertEquals(3, inserted.size());
        verify(responseProcessor, times(1)).process(any());
        assertNull(crawlerContext.getRetryCountMap().get(URL));
        assertFalse(loggedTypes.contains(LogType.CRAWLING_ACCESS_EXCEPTION));
    }

    /**
     * robots.txt answers 503 and then 200 while the interval controller does not wait for the backoff of the origin
     * (the default {@link DefaultIntervalController}): the URL comes back many times before the backoff ends, and it
     * must still be fetched once robots.txt is available instead of being dropped.
     */
    @Test
    public void test_run_robotsTxt503ThenOkWithNonWaitingIntervalControllerFetchesUrl() throws Exception {
        final AtomicLong clock = new AtomicLong(1_700_000_000_000L);
        SystemUtil.setTimeProvider(clock::get);
        try {
            final DefaultIntervalController intervalController = new DefaultIntervalController();
            intervalController.setDelayMillisAtNoUrlInQueue(0L);
            intervalController.setDelayMillisForWaitingNewUrl(0L);
            crawlerContext.intervalController = intervalController;
            prepareRun(newUrlQueue());

            // a queue that serves what is re-queued; each poll takes one second of the fake clock
            final Deque<UrlQueue<?>> queue = new ArrayDeque<>();
            queue.add(newUrlQueue());
            org.mockito.Mockito.doAnswer(invocation -> {
                clock.addAndGet(1000L);
                return queue.poll();
            }).when(urlQueueService).poll(anyString());
            org.mockito.Mockito.doAnswer(invocation -> {
                final UrlQueue<?> urlQueue = invocation.getArgument(0);
                inserted.add(urlQueue);
                queue.add(urlQueue);
                return null;
            }).when(urlQueueService).insert(any());

            final List<String> robotsTxtFetched = new ArrayList<>();
            final List<RobotsTxtResponse> robotsTxtResponses = new ArrayList<>(List.of(new RobotsTxtResponse(503, null, null, null, null),
                    new RobotsTxtResponse(200, null, null, "User-agent: *\nDisallow: /private/\n".getBytes(StandardCharsets.UTF_8), null)));
            final RobotsTxtFetcher fetcher = robotsTxtUrl -> {
                robotsTxtFetched.add(robotsTxtUrl);
                return robotsTxtResponses.size() > 1 ? robotsTxtResponses.remove(0) : robotsTxtResponses.get(0);
            };
            final RobotsTxtHelper robotsTxtHelper = new RobotsTxtHelper();
            final RobotsTxtPolicy policy = new RobotsTxtPolicy(true, true, false, crawlerContext.getRobotsTxtMaxRetries());
            final List<String> fetched = new ArrayList<>();
            when(client.execute(any())).thenAnswer(invocation -> {
                final RequestData requestData = invocation.getArgument(0);
                robotsTxtHelper.checkRobotsTxt(crawlerContext, requestData.getUrl(), "FessCrawler", fetcher, policy);
                fetched.add(requestData.getUrl());
                return newResponse(200);
            });

            crawlerThread.run();

            assertEquals(List.of(URL), fetched);
            verify(responseProcessor, times(1)).process(any());
            assertEquals(List.of("http://example.com/robots.txt", "http://example.com/robots.txt"), robotsTxtFetched);
            assertEquals(HostState.RobotsTxtStatus.PARSED, crawlerContext.peekHostState(URL).getRobotsTxtStatus());
            // only the failed robots.txt fetch used up a retry of the URL
            assertEquals(Integer.valueOf(1), crawlerContext.getRetryCountMap().get(URL));
            assertTrue(inserted.size() > crawlerContext.getMaxRetryCount());
        } finally {
            SystemUtil.setTimeProvider(null);
        }
    }

    @Test
    public void test_run_robotsTxtUnavailableFromHeadRequestIsRequeued() throws Exception {
        final UrlQueueImpl<Long> urlQueue = newUrlQueue();
        urlQueue.setLastModified(1000L);
        prepareRun(urlQueue);
        when(client.execute(any())).thenThrow(new RobotsTxtUnavailableException(URL, 0L, null));

        crawlerThread.run();

        assertEquals(1, inserted.size());
        assertEquals(Long.valueOf(1000L), inserted.get(0).getLastModified());
        verify(client, times(1)).execute(any());
        assertFalse(loggedTypes.contains(LogType.CRAWLING_ACCESS_EXCEPTION));
    }

    @Test
    public void test_isValid_robotsTxtDisallowAll() throws Exception {
        when(urlFilter.match(anyString())).thenReturn(true);
        crawlerContext.getHostState("http://blocked.example.com/").setRobotsTxt(HostState.RobotsTxtStatus.DISALLOW_ALL, null, 0L);

        final UrlQueueImpl<Long> blocked = new UrlQueueImpl<>();
        blocked.setUrl("http://blocked.example.com/page");
        blocked.setDepth(1);
        final UrlQueueImpl<Long> unknown = new UrlQueueImpl<>();
        unknown.setUrl("http://unknown.example.com/page");
        unknown.setDepth(1);

        assertFalse(crawlerThread.isValid(blocked));
        assertTrue(crawlerThread.isValid(unknown));
        assertNull(crawlerContext.peekHostState("http://unknown.example.com/page"));
    }

    @Test
    public void test_isRetryableStatus() {
        assertTrue(crawlerThread.isRetryableStatus(429));
        assertTrue(crawlerThread.isRetryableStatus(503));
        assertFalse(crawlerThread.isRetryableStatus(200));
        assertFalse(crawlerThread.isRetryableStatus(500));
        assertFalse(crawlerThread.isRetryableStatus(0));
    }

    @Test
    public void test_getRetryAfter() {
        final ResponseData responseData = newResponse(429);
        assertNull(crawlerThread.getRetryAfter(responseData));
        responseData.addMetaData("RETRY-AFTER", Integer.valueOf(7));
        assertEquals("7", crawlerThread.getRetryAfter(responseData));
    }
}
