/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.robot.client;

import java.util.List;
import java.util.Map;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotMultipleCrawlAccessException;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.client.FaultTolerantClient.RequestListener;
import org.seasar.robot.entity.ResponseData;

/**
 * @author shinsuke
 * 
 */
public class FaultTolerantClientTest extends S2TestCase {

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_doGet() {
        FaultTolerantClient client = new FaultTolerantClient();
        TestClient testClient = new TestClient();
        TestListener testListener = new TestListener();
        client.setRobotClient(testClient);
        client.setRequestListener(testListener);
        String url = "http://test.com/";
        ResponseData response = client.doGet(url);
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
        FaultTolerantClient client = new FaultTolerantClient();
        TestClient testClient = new TestClient();
        testClient.exceptionCount = 4;
        TestListener testListener = new TestListener();
        client.setRobotClient(testClient);
        client.setRequestListener(testListener);
        String url = "http://test.com/";
        ResponseData response = client.doGet(url);
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
        FaultTolerantClient client = new FaultTolerantClient();
        TestClient testClient = new TestClient();
        testClient.exceptionCount = 5;
        TestListener testListener = new TestListener();
        client.setRobotClient(testClient);
        client.setRequestListener(testListener);
        String url = "http://test.com/";
        try {
            client.doGet(url);
            fail();
        } catch (RobotMultipleCrawlAccessException e) {
            // ok
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
        FaultTolerantClient client = new FaultTolerantClient();
        client.setMaxRetryCount(2);
        TestClient testClient = new TestClient();
        testClient.exceptionCount = 4;
        TestListener testListener = new TestListener();
        client.setRobotClient(testClient);
        client.setRequestListener(testListener);
        String url = "http://test.com/";
        try {
            client.doGet(url);
            fail();
        } catch (RobotMultipleCrawlAccessException e) {
            // ok
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
        FaultTolerantClient client = new FaultTolerantClient();
        client.setRetryInterval(100);
        TestClient testClient = new TestClient();
        testClient.exceptionCount = 4;
        testClient.interval = 100;
        TestListener testListener = new TestListener();
        client.setRobotClient(testClient);
        client.setRequestListener(testListener);
        String url = "http://test.com/";
        ResponseData response = client.doGet(url);
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
        FaultTolerantClient client = new FaultTolerantClient();
        TestClient testClient = new TestClient();
        TestListener testListener = new TestListener();
        client.setRobotClient(testClient);
        client.setRequestListener(testListener);
        String url = "http://test.com/";
        ResponseData response = client.doHead(url);
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
        FaultTolerantClient client = new FaultTolerantClient();
        TestClient testClient = new TestClient();
        testClient.exceptionCount = 4;
        TestListener testListener = new TestListener();
        client.setRobotClient(testClient);
        client.setRequestListener(testListener);
        String url = "http://test.com/";
        ResponseData response = client.doHead(url);
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
        FaultTolerantClient client = new FaultTolerantClient();
        TestClient testClient = new TestClient();
        testClient.exceptionCount = 5;
        TestListener testListener = new TestListener();
        client.setRobotClient(testClient);
        client.setRequestListener(testListener);
        String url = "http://test.com/";
        try {
            client.doHead(url);
            fail();
        } catch (RobotMultipleCrawlAccessException e) {
            // ok
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
        FaultTolerantClient client = new FaultTolerantClient();
        client.setMaxRetryCount(2);
        TestClient testClient = new TestClient();
        testClient.exceptionCount = 4;
        TestListener testListener = new TestListener();
        client.setRobotClient(testClient);
        client.setRequestListener(testListener);
        String url = "http://test.com/";
        try {
            client.doHead(url);
            fail();
        } catch (RobotMultipleCrawlAccessException e) {
            // ok
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
        FaultTolerantClient client = new FaultTolerantClient();
        client.setRetryInterval(100);
        TestClient testClient = new TestClient();
        testClient.exceptionCount = 4;
        testClient.interval = 100;
        TestListener testListener = new TestListener();
        client.setRobotClient(testClient);
        client.setRequestListener(testListener);
        String url = "http://test.com/";
        ResponseData response = client.doHead(url);
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

    static class TestClient implements S2RobotClient {
        int count;

        int exceptionCount;

        long interval = 500;

        long previousTime;

        public void setInitParameterMap(Map<String, Object> params) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.seasar.robot.client.S2RobotClient#doGet(java.lang.String)
         */
        public ResponseData doGet(String url) {
            long now = System.currentTimeMillis();
            if (now - previousTime < interval) {
                throw new IllegalStateException();
            }
            previousTime = now;

            count++;
            if (count <= exceptionCount) {
                throw new RobotSystemException("exception " + count);
            }

            ResponseData responseData = new ResponseData();
            responseData.setUrl(url);
            responseData.setMethod(Constants.GET_METHOD);
            return responseData;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.seasar.robot.client.S2RobotClient#doHead(java.lang.String)
         */
        public ResponseData doHead(String url) {
            long now = System.currentTimeMillis();
            if (now - previousTime < interval) {
                throw new IllegalStateException();
            }
            previousTime = now;

            count++;
            if (count <= exceptionCount) {
                throw new RobotSystemException("exception " + count);
            }

            ResponseData responseData = new ResponseData();
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

        public void onRequestStart(FaultTolerantClient client, String method,
                String url) {
            startCount++;
        }

        public void onRequest(FaultTolerantClient client, String method,
                String url, int count) {
            requestCount++;
            requestUrl = url;
            requestMethod = method;
        }

        public void onRequestEnd(FaultTolerantClient client, String method,
                String url, List<Exception> exceptionList) {
            endCount++;
        }

        public void onException(FaultTolerantClient client, String method,
                String url, int count, Exception e) {
            exceptionCount++;
            exceptionUrl = url;
        }

    }
}
