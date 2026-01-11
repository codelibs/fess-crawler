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
package org.codelibs.fess.crawler.transformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for Transformer interface.
 * Tests the contract and behavior of Transformer implementations.
 */
public class TransformerTest extends PlainTestCase {

    /**
     * Basic test implementation of Transformer
     */
    public static class TestTransformer implements Transformer {
        private String name;
        private final AtomicInteger transformCallCount = new AtomicInteger(0);
        private final AtomicInteger getDataCallCount = new AtomicInteger(0);
        private ResponseData lastResponseData = null;
        private AccessResultData<?> lastAccessResultData = null;

        public TestTransformer() {
            this.name = null;
        }

        public TestTransformer(String name) {
            this.name = name;
        }

        @Override
        public ResultData transform(ResponseData responseData) {
            transformCallCount.incrementAndGet();
            lastResponseData = responseData;

            if (responseData == null) {
                return null;
            }

            ResultData resultData = new ResultData();
            resultData.setTransformerName(name);
            // Store some dummy data
            resultData.setData("test data".getBytes());

            return resultData;
        }

        @Override
        public Object getData(AccessResultData<?> accessResultData) {
            getDataCallCount.incrementAndGet();
            lastAccessResultData = accessResultData;

            if (accessResultData == null) {
                return null;
            }

            byte[] data = accessResultData.getData();
            return data != null ? new String(data) : null;
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTransformCallCount() {
            return transformCallCount.get();
        }

        public int getGetDataCallCount() {
            return getDataCallCount.get();
        }

        public ResponseData getLastResponseData() {
            return lastResponseData;
        }

        public AccessResultData<?> getLastAccessResultData() {
            return lastAccessResultData;
        }

        public void reset() {
            transformCallCount.set(0);
            getDataCallCount.set(0);
            lastResponseData = null;
            lastAccessResultData = null;
        }
    }

    /**
     * Transformer implementation that performs content transformation
     */
    public static class ContentTransformer implements Transformer {
        private final String name;
        private final Map<String, String> transformationRules = new HashMap<>();

        public ContentTransformer(String name) {
            this.name = name;
        }

        public void addTransformationRule(String pattern, String replacement) {
            transformationRules.put(pattern, replacement);
        }

        @Override
        public ResultData transform(ResponseData responseData) {
            if (responseData == null) {
                return null;
            }

            ResultData resultData = new ResultData();
            resultData.setTransformerName(name);
            // Apply transformation rules
            try (InputStream is = responseData.getResponseBody()) {
                byte[] bytes = is.readAllBytes();
                String content = new String(bytes);
                for (Map.Entry<String, String> rule : transformationRules.entrySet()) {
                    content = content.replaceAll(rule.getKey(), rule.getValue());
                }
                resultData.setData(content.getBytes());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return resultData;
        }

        @Override
        public Object getData(AccessResultData<?> accessResultData) {
            if (accessResultData == null) {
                return null;
            }

            byte[] data = accessResultData.getData();
            if (data != null) {
                String content = new String(data);
                // Apply transformation rules to retrieved data
                for (Map.Entry<String, String> rule : transformationRules.entrySet()) {
                    content = content.replaceAll(rule.getKey(), rule.getValue());
                }
                return content;
            }

            return null;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    /**
     * Transformer that throws exceptions for testing error handling
     */
    public static class ExceptionThrowingTransformer implements Transformer {
        private final String name;
        private boolean throwInTransform = false;
        private boolean throwInGetData = false;

        public ExceptionThrowingTransformer(String name) {
            this.name = name;
        }

        public void setThrowInTransform(boolean throwInTransform) {
            this.throwInTransform = throwInTransform;
        }

        public void setThrowInGetData(boolean throwInGetData) {
            this.throwInGetData = throwInGetData;
        }

        @Override
        public ResultData transform(ResponseData responseData) {
            if (throwInTransform) {
                throw new CrawlerSystemException("Transform exception");
            }

            ResultData resultData = new ResultData();
            resultData.setTransformerName(name);
            return resultData;
        }

        @Override
        public Object getData(AccessResultData<?> accessResultData) {
            if (throwInGetData) {
                throw new CrawlerSystemException("GetData exception");
            }

            if (accessResultData == null) {
                return null;
            }
            byte[] data = accessResultData.getData();
            return data != null ? new String(data) : null;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    /**
     * Stateful transformer for testing state management
     */
    public static class StatefulTransformer implements Transformer {
        private final String name;
        private final List<String> processedUrls = new ArrayList<>();
        private int state = 0;

        public StatefulTransformer(String name) {
            this.name = name;
        }

        @Override
        public ResultData transform(ResponseData responseData) {
            if (responseData != null) {
                processedUrls.add(responseData.getUrl());
                state++;
            }

            ResultData resultData = new ResultData();
            resultData.setTransformerName(name);
            resultData.setData(("State: " + state).getBytes());
            return resultData;
        }

        @Override
        public Object getData(AccessResultData<?> accessResultData) {
            if (accessResultData == null) {
                return null;
            }
            byte[] data = accessResultData.getData();
            return data != null ? new String(data) : null;
        }

        @Override
        public String getName() {
            return name;
        }

        public List<String> getProcessedUrls() {
            return new ArrayList<>(processedUrls);
        }

        public int getState() {
            return state;
        }

        public void reset() {
            processedUrls.clear();
            state = 0;
        }
    }

    /**
     * Test AccessResultData implementation
     */
    public static class TestAccessResultData<T> implements AccessResultData<T>, Serializable {
        private static final long serialVersionUID = 1L;
        private T id;
        private byte[] data;
        private String encoding;
        private String transformerName;
        private String url;

        public TestAccessResultData() {
        }

        public TestAccessResultData(byte[] data) {
            this.data = data;
        }

        @Override
        public T getId() {
            return id;
        }

        @Override
        public void setId(T id) {
            this.id = id;
        }

        @Override
        public String getTransformerName() {
            return transformerName;
        }

        @Override
        public void setTransformerName(String transformerName) {
            this.transformerName = transformerName;
        }

        @Override
        public byte[] getData() {
            return data;
        }

        @Override
        public String getDataAsString() {
            return data != null ? new String(data) : null;
        }

        @Override
        public void setData(byte[] data) {
            this.data = data;
        }

        @Override
        public String getEncoding() {
            return encoding;
        }

        @Override
        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    private TestTransformer testTransformer;

    @Override
    @BeforeEach
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        testTransformer = new TestTransformer("testTransformer");
    }

    /**
     * Test basic transform implementation
     */
    public void test_transform_basic() {
        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com");
        responseData.setParentUrl("http://parent.com");

        ResultData resultData = testTransformer.transform(responseData);

        assertNotNull(resultData);
        assertEquals("testTransformer", resultData.getTransformerName());
        assertNotNull(resultData.getData());
        assertEquals("test data", new String(resultData.getData()));
        assertEquals(1, testTransformer.getTransformCallCount());
        assertTrue(responseData == testTransformer.getLastResponseData());
    }

    /**
     * Test transform with null ResponseData
     */
    public void test_transform_nullResponseData() {
        ResultData resultData = testTransformer.transform(null);

        assertNull(resultData);
        assertEquals(1, testTransformer.getTransformCallCount());
        assertNull(testTransformer.getLastResponseData());
    }

    /**
     * Test transform with various ResponseData states
     */
    public void test_transform_variousResponseDataStates() {
        // Empty ResponseData - should return a valid ResultData with empty/null data
        ResponseData emptyData = new ResponseData();
        ResultData result1 = testTransformer.transform(emptyData);
        assertNotNull(result1);
        assertNotNull(result1.getData()); // Transform should return ResultData with some data

        // ResponseData with only URL
        ResponseData urlOnlyData = new ResponseData();
        urlOnlyData.setUrl("http://example.com");
        ResultData result2 = testTransformer.transform(urlOnlyData);
        assertNotNull(result2);
        assertNotNull(result2.getData());

        // ResponseData with response body
        ResponseData withBodyData = new ResponseData();
        withBodyData.setUrl("http://example.com");
        withBodyData.setResponseBody("Test content".getBytes());
        ResultData result3 = testTransformer.transform(withBodyData);
        assertNotNull(result3);
        assertNotNull(result3.getData());
    }

    /**
     * Test getData basic implementation
     */
    public void test_getData_basic() {
        TestAccessResultData<String> accessResultData = new TestAccessResultData<>();
        accessResultData.setData("test data".getBytes());
        accessResultData.setUrl("http://example.com");

        Object data = testTransformer.getData(accessResultData);

        assertEquals("test data", data);
        assertEquals(1, testTransformer.getGetDataCallCount());
        assertTrue(accessResultData == testTransformer.getLastAccessResultData());
    }

    /**
     * Test getData with null AccessResultData
     */
    public void test_getData_nullAccessResultData() {
        Object data = testTransformer.getData(null);

        assertNull(data);
        assertEquals(1, testTransformer.getGetDataCallCount());
        assertNull(testTransformer.getLastAccessResultData());
    }

    /**
     * Test getData with different data types
     */
    public void test_getData_differentDataTypes() {
        // String data
        TestAccessResultData<String> stringData = new TestAccessResultData<>("string value".getBytes());
        assertEquals("string value", testTransformer.getData(stringData));

        // Integer data
        TestAccessResultData<Integer> intData = new TestAccessResultData<>("123".getBytes());
        assertEquals("123", testTransformer.getData(intData));

        // List data
        List<String> list = new ArrayList<>();
        list.add("item1");
        list.add("item2");
        TestAccessResultData<String> listData = new TestAccessResultData<>(list.toString().getBytes());
        Object retrievedList = testTransformer.getData(listData);
        assertEquals(list.toString(), retrievedList);

        // Null data
        TestAccessResultData<Object> nullData = new TestAccessResultData<>();
        assertNull(testTransformer.getData(nullData));
    }

    /**
     * Test getName implementation
     */
    public void test_getName() {
        assertEquals("testTransformer", testTransformer.getName());

        // Test with null name
        TestTransformer nullNameTransformer = new TestTransformer();
        assertNull(nullNameTransformer.getName());

        // Test with empty name
        TestTransformer emptyNameTransformer = new TestTransformer("");
        assertEquals("", emptyNameTransformer.getName());

        // Test with special characters
        TestTransformer specialNameTransformer = new TestTransformer("trans-former_123#");
        assertEquals("trans-former_123#", specialNameTransformer.getName());
    }

    /**
     * Test ContentTransformer implementation
     */
    public void test_contentTransformer() {
        ContentTransformer transformer = new ContentTransformer("contentTransformer");
        transformer.addTransformationRule("old", "new");
        transformer.addTransformationRule("\\d+", "NUMBER");

        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com");
        responseData.setResponseBody("This is old text with 123 numbers".getBytes());

        ResultData resultData = transformer.transform(responseData);

        assertNotNull(resultData);
        assertEquals("contentTransformer", resultData.getTransformerName());
        assertEquals("This is new text with NUMBER numbers", new String(resultData.getData()));

        // Test getData with transformation
        TestAccessResultData<String> accessData = new TestAccessResultData<>("old value 456".getBytes());
        Object transformedData = transformer.getData(accessData);
        assertEquals("new value NUMBER", transformedData);
    }

    /**
     * Test exception handling in transform
     */
    public void test_exceptionHandling_transform() {
        ExceptionThrowingTransformer transformer = new ExceptionThrowingTransformer("exceptionTransformer");
        transformer.setThrowInTransform(true);

        ResponseData responseData = new ResponseData();

        try {
            transformer.transform(responseData);
            fail();
        } catch (CrawlerSystemException e) {
            assertEquals("Transform exception", e.getMessage());
        }
    }

    /**
     * Test exception handling in getData
     */
    public void test_exceptionHandling_getData() {
        ExceptionThrowingTransformer transformer = new ExceptionThrowingTransformer("exceptionTransformer");
        transformer.setThrowInGetData(true);

        TestAccessResultData<String> accessData = new TestAccessResultData<>("data".getBytes());

        try {
            transformer.getData(accessData);
            fail();
        } catch (CrawlerSystemException e) {
            assertEquals("GetData exception", e.getMessage());
        }
    }

    /**
     * Test stateful transformer
     */
    public void test_statefulTransformer() {
        StatefulTransformer transformer = new StatefulTransformer("statefulTransformer");

        // Process multiple URLs
        String[] urls = { "http://example1.com", "http://example2.com", "http://example3.com" };

        for (String url : urls) {
            ResponseData responseData = new ResponseData();
            responseData.setUrl(url);

            ResultData resultData = transformer.transform(responseData);
            assertNotNull(resultData);
        }

        // Verify state
        assertEquals(3, transformer.getState());
        List<String> processedUrls = transformer.getProcessedUrls();
        assertEquals(3, processedUrls.size());
        assertEquals("http://example1.com", processedUrls.get(0));
        assertEquals("http://example2.com", processedUrls.get(1));
        assertEquals("http://example3.com", processedUrls.get(2));

        // Reset and verify
        transformer.reset();
        assertEquals(0, transformer.getState());
        assertEquals(0, transformer.getProcessedUrls().size());
    }

    /**
     * Test multiple transformer instances
     */
    public void test_multipleTransformerInstances() {
        TestTransformer transformer1 = new TestTransformer("transformer1");
        TestTransformer transformer2 = new TestTransformer("transformer2");
        TestTransformer transformer3 = new TestTransformer("transformer3");

        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com");

        ResultData result1 = transformer1.transform(responseData);
        ResultData result2 = transformer2.transform(responseData);
        ResultData result3 = transformer3.transform(responseData);

        assertEquals("transformer1", result1.getTransformerName());
        assertEquals("transformer2", result2.getTransformerName());
        assertEquals("transformer3", result3.getTransformerName());

        assertEquals(1, transformer1.getTransformCallCount());
        assertEquals(1, transformer2.getTransformCallCount());
        assertEquals(1, transformer3.getTransformCallCount());
    }

    /**
     * Test concurrent transformer operations
     */
    public void test_concurrentOperations() throws Exception {
        final TestTransformer transformer = new TestTransformer("concurrentTransformer");
        final int threadCount = 5;
        final int operationsPerThread = 10;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        final AtomicInteger successCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.await();
                        for (int j = 0; j < operationsPerThread; j++) {
                            // Transform operation
                            ResponseData responseData = new ResponseData();
                            responseData.setUrl("http://thread" + threadId + ".com/page" + j);
                            ResultData resultData = transformer.transform(responseData);

                            if (resultData != null && "concurrentTransformer".equals(resultData.getTransformerName())) {
                                successCount.incrementAndGet();
                            }

                            // GetData operation
                            TestAccessResultData<String> accessData = new TestAccessResultData<>(("data" + j).getBytes());
                            Object data = transformer.getData(accessData);
                            if (data != null) {
                                successCount.incrementAndGet();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            });
        }

        startLatch.countDown();
        boolean completed = endLatch.await(30, TimeUnit.SECONDS);
        assertTrue(completed);
        executor.shutdown();

        assertEquals(threadCount * operationsPerThread * 2, successCount.get());
        assertEquals(threadCount * operationsPerThread, transformer.getTransformCallCount());
        assertEquals(threadCount * operationsPerThread, transformer.getGetDataCallCount());
    }

    /**
     * Test transformer chain
     */
    public void test_transformerChain() {
        // Create chain of transformers
        List<Transformer> transformerChain = new ArrayList<>();
        transformerChain.add(new TestTransformer("first"));
        transformerChain.add(new TestTransformer("second"));
        transformerChain.add(new TestTransformer("third"));

        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com");

        ResultData currentResult = null;
        for (Transformer transformer : transformerChain) {
            currentResult = transformer.transform(responseData);
            assertNotNull(currentResult);
            assertEquals(transformer.getName(), currentResult.getTransformerName());
        }

        // Final result should be from last transformer
        assertEquals("third", currentResult.getTransformerName());
    }

    /**
     * Test null name handling
     */
    public void test_nullNameHandling() {
        TestTransformer transformer = new TestTransformer(null);
        assertNull(transformer.getName());

        ResponseData responseData = new ResponseData();
        ResultData resultData = transformer.transform(responseData);

        assertNotNull(resultData);
        assertNull(resultData.getTransformerName());
    }

    /**
     * Test serialization of AccessResultData
     */
    public void test_accessResultDataSerialization() throws Exception {
        TestAccessResultData<String> original = new TestAccessResultData<>("test data".getBytes());
        original.setUrl("http://example.com");

        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        @SuppressWarnings("unchecked")
        TestAccessResultData<String> deserialized = (TestAccessResultData<String>) ois.readObject();
        ois.close();

        // Verify
        assertEquals("test data", new String(deserialized.getData()));
        assertNotNull(deserialized.getData());
    }

    /**
     * Test transformer with large data
     */
    public void test_transformerWithLargeData() {
        TestTransformer transformer = new TestTransformer("largeDataTransformer");

        // Create large response data
        byte[] largeBody = new byte[10 * 1024 * 1024]; // 10MB
        for (int i = 0; i < largeBody.length; i++) {
            largeBody[i] = (byte) (i % 256);
        }

        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/large");
        responseData.setResponseBody(largeBody);

        ResultData resultData = transformer.transform(responseData);

        assertNotNull(resultData);
        assertEquals("largeDataTransformer", resultData.getTransformerName());
        assertNotNull(resultData.getData());

        // Test with large AccessResultData
        StringBuilder largeString = new StringBuilder();
        for (int i = 0; i < 1000000; i++) {
            largeString.append("Large data content ");
        }
        TestAccessResultData<String> largeAccessData = new TestAccessResultData<>(largeString.toString().getBytes());

        Object retrievedData = transformer.getData(largeAccessData);
        assertNotNull(retrievedData);
        assertEquals(largeString.toString(), retrievedData);
    }

    /**
     * Test transformer reusability
     */
    public void test_transformerReusability() {
        TestTransformer transformer = new TestTransformer("reusableTransformer");

        // Use transformer multiple times
        for (int i = 0; i < 10; i++) {
            ResponseData responseData = new ResponseData();
            responseData.setUrl("http://example.com/page" + i);

            ResultData resultData = transformer.transform(responseData);
            assertNotNull(resultData);
            assertEquals("reusableTransformer", resultData.getTransformerName());
            assertNotNull(resultData.getData());

            TestAccessResultData<String> accessData = new TestAccessResultData<>(("data" + i).getBytes());
            Object data = transformer.getData(accessData);
            assertEquals("data" + i, data);
        }

        assertEquals(10, transformer.getTransformCallCount());
        assertEquals(10, transformer.getGetDataCallCount());
    }

    /**
     * Test complete workflow
     */
    public void test_completeWorkflow() {
        // Create transformer
        ContentTransformer transformer = new ContentTransformer("workflowTransformer");
        transformer.addTransformationRule("<[^>]+>", ""); // Remove HTML tags
        transformer.addTransformationRule("\\s+", " "); // Normalize whitespace

        // Simulate crawling response
        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/page.html");
        responseData.setParentUrl("http://example.com/");
        responseData.setResponseBody("<html><body>  Test   Content  </body></html>".getBytes());
        responseData.setHttpStatusCode(200);
        responseData.setMimeType("text/html");

        // Transform response
        ResultData resultData = transformer.transform(responseData);

        assertNotNull(resultData);
        assertEquals("workflowTransformer", resultData.getTransformerName());
        assertNotNull(resultData.getData());
        assertEquals(" Test Content ", new String(resultData.getData()));

        // Store and retrieve data
        TestAccessResultData<Object> accessData = new TestAccessResultData<>();
        accessData.setData(resultData.getData());
        // URL handling removed as ResultData doesn't have getUrl()

        Object retrievedData = transformer.getData(accessData);
        assertEquals(" Test Content ", retrievedData);
    }
}
