/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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

import java.util.List;
import java.util.Map;

import org.codelibs.core.lang.SystemUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.FaultTolerantClient.RequestListener;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.MultipleCrawlingAccessException;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class FaultTolerantClientTest extends PlainTestCase {

    public void test_doGet() {
        final FaultTolerantClient client = new FaultTolerantClient();
        final TestClient testClient = new TestClient();
        final TestListener testListener = new TestListener();
        client.setCrawlerClient(testClient);
        client.setRequestListener(testListener);
        final String url = "http://test.com/";
        final ResponseData response = client.execute(RequestDataBuilder
                .newRequestData().get().url(url).build());
        assertEquals(1, testListener.startCount);
        assertEquals(1, testListener.requestCount);
        assertEquals(0, testListener.exceptionCount);
        assertEquals(1, testListener.endCount);
        assertEquals(url, testListener.requestUrl);
        assertEquals(Constants.GET_METHOD, testListener.requestMethod);
        assertNull(testListener.exceptionUrl);
        assertEquals(1, testClient.count);
        assertEquals(url, response.getUrl());
        assertEquals(Constants.GET_METHOD, response.getMethod());
    }

    public void test_doGet_with4Exception() {
        final FaultTolerantClient client = new FaultTolerantClient();
        final TestClient testClient = new TestClient();
        testClient.exceptionCount = 4;
        final TestListener testListener = new TestListener();
        client.setCrawlerClient(testClient);
        client.setRequestListener(testListener);
        final String url = "http://test.com/";
        final ResponseData response = client.execute(RequestDataBuilder
                .newRequestData().get().url(url).build());
        assertEquals(1, testListener.startCount);
        assertEquals(5, testListener.requestCount);
        assertEquals(4, testListener.exceptionCount);
        assertEquals(1, testListener.endCount);
        assertEquals(url, testListener.requestUrl);
        assertEquals(Constants.GET_METHOD, testListener.requestMethod);
        assertEquals(url, testListener.exceptionUrl);
        assertEquals(5, testClient.count);
        assertEquals(url, response.getUrl());
        assertEquals(Constants.GET_METHOD, response.getMethod());
    }

    public void test_doGet_with5Exception() {
        final FaultTolerantClient client = new FaultTolerantClient();
        final TestClient testClient = new TestClient();
        testClient.exceptionCount = 5;
        final TestListener testListener = new TestListener();
        client.setCrawlerClient(testClient);
        client.setRequestListener(testListener);
        final String url = "http://test.com/";
        try {
            client.execute(RequestDataBuilder.newRequestData().get().url(url)
                    .build());
            fail();
        } catch (final MultipleCrawlingAccessException e) {
            // ok
            final Throwable[] causes = e.getCauses();
            assertEquals(5, causes.length);
        }
        assertEquals(1, testListener.startCount);
        assertEquals(5, testListener.requestCount);
        assertEquals(5, testListener.exceptionCount);
        assertEquals(1, testListener.endCount);
        assertEquals(url, testListener.requestUrl);
        assertEquals(Constants.GET_METHOD, testListener.requestMethod);
        assertEquals(url, testListener.exceptionUrl);
        assertEquals(5, testClient.count);
    }

    public void test_doGet_with4Exception_retryCount2() {
        final FaultTolerantClient client = new FaultTolerantClient();
        client.setMaxRetryCount(2);
        final TestClient testClient = new TestClient();
        testClient.exceptionCount = 4;
        final TestListener testListener = new TestListener();
        client.setCrawlerClient(testClient);
        client.setRequestListener(testListener);
        final String url = "http://test.com/";
        try {
            client.execute(RequestDataBuilder.newRequestData().get().url(url)
                    .build());
            fail();
        } catch (final MultipleCrawlingAccessException e) {
            // ok
            final Throwable[] causes = e.getCauses();
            assertEquals(2, causes.length);
        }
        assertEquals(1, testListener.startCount);
        assertEquals(2, testListener.requestCount);
        assertEquals(2, testListener.exceptionCount);
        assertEquals(1, testListener.endCount);
        assertEquals(url, testListener.requestUrl);
        assertEquals(Constants.GET_METHOD, testListener.requestMethod);
        assertEquals(url, testListener.exceptionUrl);
        assertEquals(2, testClient.count);
    }

    public void test_doGet_with4Exception_interval100() {
        final FaultTolerantClient client = new FaultTolerantClient();
        client.setRetryInterval(100);
        final TestClient testClient = new TestClient();
        testClient.exceptionCount = 4;
        testClient.interval = 100;
        final TestListener testListener = new TestListener();
        client.setCrawlerClient(testClient);
        client.setRequestListener(testListener);
        final String url = "http://test.com/";
        final ResponseData response = client.execute(RequestDataBuilder
                .newRequestData().get().url(url).build());
        assertEquals(1, testListener.startCount);
        assertEquals(5, testListener.requestCount);
        assertEquals(4, testListener.exceptionCount);
        assertEquals(1, testListener.endCount);
        assertEquals(url, testListener.requestUrl);
        assertEquals(Constants.GET_METHOD, testListener.requestMethod);
        assertEquals(url, testListener.exceptionUrl);
        assertEquals(5, testClient.count);
        assertEquals(url, response.getUrl());
        assertEquals(Constants.GET_METHOD, response.getMethod());
    }

    public void test_doHead() {
        final FaultTolerantClient client = new FaultTolerantClient();
        final TestClient testClient = new TestClient();
        final TestListener testListener = new TestListener();
        client.setCrawlerClient(testClient);
        client.setRequestListener(testListener);
        final String url = "http://test.com/";
        final ResponseData response = client.execute(RequestDataBuilder
                .newRequestData().head().url(url).build());
        assertEquals(1, testListener.startCount);
        assertEquals(1, testListener.requestCount);
        assertEquals(0, testListener.exceptionCount);
        assertEquals(1, testListener.endCount);
        assertEquals(url, testListener.requestUrl);
        assertEquals(Constants.HEAD_METHOD, testListener.requestMethod);
        assertNull(testListener.exceptionUrl);
        assertEquals(1, testClient.count);
        assertEquals(url, response.getUrl());
        assertEquals(Constants.HEAD_METHOD, response.getMethod());
    }

    public void test_doHead_with4Exception() {
        final FaultTolerantClient client = new FaultTolerantClient();
        final TestClient testClient = new TestClient();
        testClient.exceptionCount = 4;
        final TestListener testListener = new TestListener();
        client.setCrawlerClient(testClient);
        client.setRequestListener(testListener);
        final String url = "http://test.com/";
        final ResponseData response = client.execute(RequestDataBuilder
                .newRequestData().head().url(url).build());
        assertEquals(1, testListener.startCount);
        assertEquals(5, testListener.requestCount);
        assertEquals(4, testListener.exceptionCount);
        assertEquals(1, testListener.endCount);
        assertEquals(url, testListener.requestUrl);
        assertEquals(Constants.HEAD_METHOD, testListener.requestMethod);
        assertEquals(url, testListener.exceptionUrl);
        assertEquals(5, testClient.count);
        assertEquals(url, response.getUrl());
        assertEquals(Constants.HEAD_METHOD, response.getMethod());
    }

    public void test_doHead_with5Exception() {
        final FaultTolerantClient client = new FaultTolerantClient();
        final TestClient testClient = new TestClient();
        testClient.exceptionCount = 5;
        final TestListener testListener = new TestListener();
        client.setCrawlerClient(testClient);
        client.setRequestListener(testListener);
        final String url = "http://test.com/";
        try {
            client.execute(RequestDataBuilder.newRequestData().head().url(url)
                    .build());
            fail();
        } catch (final MultipleCrawlingAccessException e) {
            // ok
            final Throwable[] causes = e.getCauses();
            assertEquals(5, causes.length);
        }
        assertEquals(1, testListener.startCount);
        assertEquals(5, testListener.requestCount);
        assertEquals(5, testListener.exceptionCount);
        assertEquals(1, testListener.endCount);
        assertEquals(url, testListener.requestUrl);
        assertEquals(Constants.HEAD_METHOD, testListener.requestMethod);
        assertEquals(url, testListener.exceptionUrl);
        assertEquals(5, testClient.count);
    }

    public void test_doHead_with4Exception_retryCount2() {
        final FaultTolerantClient client = new FaultTolerantClient();
        client.setMaxRetryCount(2);
        final TestClient testClient = new TestClient();
        testClient.exceptionCount = 4;
        final TestListener testListener = new TestListener();
        client.setCrawlerClient(testClient);
        client.setRequestListener(testListener);
        final String url = "http://test.com/";
        try {
            client.execute(RequestDataBuilder.newRequestData().head().url(url)
                    .build());
            fail();
        } catch (final MultipleCrawlingAccessException e) {
            // ok
            final Throwable[] causes = e.getCauses();
            assertEquals(2, causes.length);
        }
        assertEquals(1, testListener.startCount);
        assertEquals(2, testListener.requestCount);
        assertEquals(2, testListener.exceptionCount);
        assertEquals(1, testListener.endCount);
        assertEquals(url, testListener.requestUrl);
        assertEquals(Constants.HEAD_METHOD, testListener.requestMethod);
        assertEquals(url, testListener.exceptionUrl);
        assertEquals(2, testClient.count);
    }

    public void test_doHead_with4Exception_interval100() {
        final FaultTolerantClient client = new FaultTolerantClient();
        client.setRetryInterval(100);
        final TestClient testClient = new TestClient();
        testClient.exceptionCount = 4;
        testClient.interval = 100;
        final TestListener testListener = new TestListener();
        client.setCrawlerClient(testClient);
        client.setRequestListener(testListener);
        final String url = "http://test.com/";
        final ResponseData response = client.execute(RequestDataBuilder
                .newRequestData().head().url(url).build());
        assertEquals(1, testListener.startCount);
        assertEquals(5, testListener.requestCount);
        assertEquals(4, testListener.exceptionCount);
        assertEquals(1, testListener.endCount);
        assertEquals(url, testListener.requestUrl);
        assertEquals(Constants.HEAD_METHOD, testListener.requestMethod);
        assertEquals(url, testListener.exceptionUrl);
        assertEquals(5, testClient.count);
        assertEquals(url, response.getUrl());
        assertEquals(Constants.HEAD_METHOD, response.getMethod());
    }

    static class TestClient implements CrawlerClient {
        int count;

        int exceptionCount;

        long interval = 500;

        long previousTime;

        @Override
        public void setInitParameterMap(final Map<String, Object> params) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.codelibs.fess.crawler.client.CrawlerClient#doGet(java.lang.String)
         */
        @Override
        public ResponseData execute(final RequestData request) {
            final long now = SystemUtil.currentTimeMillis();
            if (now - previousTime < interval) {
                throw new IllegalStateException();
            }
            previousTime = now;

            count++;
            if (count <= exceptionCount) {
                throw new CrawlerSystemException("exception " + count);
            }

            final ResponseData responseData = new ResponseData();
            responseData.setUrl(request.getUrl());
            responseData.setMethod(request.getMethod().toString());
            return responseData;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.codelibs.fess.crawler.client.CrawlerClient#doHead(java.lang.String)
         */
        public ResponseData doHead(final String url) {
            final long now = SystemUtil.currentTimeMillis();
            if (now - previousTime < interval) {
                throw new IllegalStateException();
            }
            previousTime = now;

            count++;
            if (count <= exceptionCount) {
                throw new CrawlerSystemException("exception " + count);
            }

            final ResponseData responseData = new ResponseData();
            responseData.setUrl(url);
            responseData.setMethod(Constants.HEAD_METHOD);
            return responseData;
        }

    }

    static class TestListener implements RequestListener {
        int startCount;

        int requestCount;

        int exceptionCount;

        int endCount;

        String requestUrl;

        String exceptionUrl;

        String requestMethod;

        @Override
        public void onRequestStart(final FaultTolerantClient client,
                final RequestData request) {
            startCount++;
        }

        @Override
        public void onRequest(final FaultTolerantClient client,
                final RequestData request, final int count) {
            requestCount++;
            requestUrl = request.getUrl();
            requestMethod = request.getMethod().toString();
        }

        @Override
        public void onRequestEnd(final FaultTolerantClient client,
                RequestData request, final List<Exception> exceptionList) {
            endCount++;
        }

        @Override
        public void onException(final FaultTolerantClient client,
                RequestData request, final int count, final Exception e) {
            exceptionCount++;
            exceptionUrl = request.getUrl();
        }

    }
}
