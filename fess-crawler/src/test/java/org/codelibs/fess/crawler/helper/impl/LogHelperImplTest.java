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
package org.codelibs.fess.crawler.helper.impl;

import java.util.HashSet;
import java.util.Set;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.UrlQueueImpl;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.log.LogType;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test for LogHelperImpl.
 */
public class LogHelperImplTest extends PlainTestCase {

    private LogHelperImpl logHelper;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer()
                .singleton("logHelper", LogHelperImpl.class);
        logHelper = container.getComponent("logHelper");
    }

    @Test
    public void test_log_startThread() {
        // Should not throw exception
        logHelper.log(LogType.START_THREAD, (Object) null);
    }

    @Test
    public void test_log_startCrawling() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/");

        // Should not throw exception
        logHelper.log(LogType.START_CRAWLING, null, urlQueue);
    }

    @Test
    public void test_log_cleanupCrawling() {
        // Should not throw exception
        logHelper.log(LogType.CLEANUP_CRAWLING, (Object) null);
    }

    @Test
    public void test_log_unsupportedUrl() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("unsupported://example.com/");

        // Should not throw exception
        logHelper.log(LogType.UNSUPPORTED_URL_AT_CRAWLING_STARTED, null, urlQueue);
    }

    @Test
    public void test_log_checkLastModified() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/");
        urlQueue.setLastModified(System.currentTimeMillis());

        // Should not throw exception
        logHelper.log(LogType.CHECK_LAST_MODIFIED, null, urlQueue);
    }

    @Test
    public void test_log_notModified() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/");

        // Should not throw exception
        logHelper.log(LogType.NOT_MODIFIED, null, urlQueue);
    }

    @Test
    public void test_log_getContent() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/content");

        // Should not throw exception
        logHelper.log(LogType.GET_CONTENT, null, urlQueue);
    }

    @Test
    public void test_log_redirectLocation() {
        final ResponseData responseData = new ResponseData();
        responseData.setRedirectLocation("http://example.com/redirected");

        // Should not throw exception
        logHelper.log(LogType.REDIRECT_LOCATION, null, null, responseData);
    }

    @Test
    public void test_log_processResponse() {
        final ResponseData responseData = new ResponseData();
        responseData.setHttpStatusCode(200);
        responseData.setExecutionTime(100);

        // Should not throw exception
        logHelper.log(LogType.PROCESS_RESPONSE, null, null, responseData);
    }

    @Test
    public void test_log_finishedCrawling() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/finished");

        // Should not throw exception
        logHelper.log(LogType.FINISHED_CRAWLING, null, urlQueue);
    }

    @Test
    public void test_log_processChildUrlsByException() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/parent");

        final Set<RequestData> requestDataSet = new HashSet<>();
        final RequestData requestData = new RequestData();
        requestData.setUrl("http://example.com/child");
        requestDataSet.add(requestData);

        // Should not throw exception
        logHelper.log(LogType.PROCESS_CHILD_URLS_BY_EXCEPTION, null, urlQueue, requestDataSet);
    }

    @Test
    public void test_log_processChildUrlByException() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/parent");
        final String childUrl = "http://example.com/child";
        final Exception e = new RuntimeException("Test exception");

        // Should not throw exception
        logHelper.log(LogType.PROCESS_CHILD_URL_BY_EXCEPTION, null, urlQueue, childUrl, e);
    }

    @Test
    public void test_log_crawlingAccessException_debug() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/");
        final CrawlingAccessException e = new CrawlingAccessException("Test access exception") {
            @Override
            public boolean isDebugEnabled() {
                return true;
            }
        };

        // Should not throw exception
        logHelper.log(LogType.CRAWLING_ACCESS_EXCEPTION, null, urlQueue, e);
    }

    @Test
    public void test_log_crawlingAccessException_info() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/");
        final CrawlingAccessException e = new CrawlingAccessException("Test access exception") {
            @Override
            public boolean isInfoEnabled() {
                return true;
            }
        };

        // Should not throw exception
        logHelper.log(LogType.CRAWLING_ACCESS_EXCEPTION, null, urlQueue, e);
    }

    @Test
    public void test_log_crawlingAccessException_warn() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/");
        final CrawlingAccessException e = new CrawlingAccessException("Test access exception") {
            @Override
            public boolean isWarnEnabled() {
                return true;
            }
        };

        // Should not throw exception
        logHelper.log(LogType.CRAWLING_ACCESS_EXCEPTION, null, urlQueue, e);
    }

    @Test
    public void test_log_crawlingAccessException_error() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/");
        final CrawlingAccessException e = new CrawlingAccessException("Test access exception") {
            @Override
            public boolean isErrorEnabled() {
                return true;
            }
        };

        // Should not throw exception
        logHelper.log(LogType.CRAWLING_ACCESS_EXCEPTION, null, urlQueue, e);
    }

    @Test
    public void test_log_crawlingException() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/error");
        final Exception e = new RuntimeException("Test exception");

        // Should not throw exception
        logHelper.log(LogType.CRAWLING_EXCEPTION, null, urlQueue, e);
    }

    @Test
    public void test_log_noUrlInQueue_withUrl() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl("http://example.com/skipped");

        // Should not throw exception
        logHelper.log(LogType.NO_URL_IN_QUEUE, null, urlQueue, 5);
    }

    @Test
    public void test_log_noUrlInQueue_nullUrl() {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        // URL is null

        // Should not throw exception
        logHelper.log(LogType.NO_URL_IN_QUEUE, null, urlQueue, 3);
    }

    @Test
    public void test_log_noUrlInQueue_nullQueue() {
        // Should not throw exception
        logHelper.log(LogType.NO_URL_IN_QUEUE, null, null, 2);
    }

    @Test
    public void test_log_finishedThread() {
        // Should not throw exception
        logHelper.log(LogType.FINISHED_THREAD, (Object) null);
    }

    @Test
    public void test_log_noResponseProcessor() {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");
        responseData.setMimeType("application/unknown");

        // Should not throw exception
        logHelper.log(LogType.NO_RESPONSE_PROCESSOR, null, null, responseData, null);
    }

    @Test
    public void test_log_noRule() {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");
        responseData.setMimeType("application/unknown");

        // Should not throw exception
        logHelper.log(LogType.NO_RULE, null, null, responseData);
    }

    @Test
    public void test_log_systemError() {
        final Throwable t = new RuntimeException("System error");

        // Should not throw exception
        logHelper.log(LogType.SYSTEM_ERROR, t);
    }

    @Test
    public void test_constructor() {
        // Verify the helper can be instantiated
        final LogHelperImpl helper = new LogHelperImpl();
        assertNotNull(helper);
    }
}
