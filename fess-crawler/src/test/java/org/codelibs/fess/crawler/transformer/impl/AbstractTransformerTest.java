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
package org.codelibs.fess.crawler.transformer.impl;

import java.util.ArrayList;
import java.util.List;
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
 * Test class for AbstractTransformer.
 * Tests the abstract transformer implementation and its name management functionality.
 */
public class AbstractTransformerTest extends PlainTestCase {

    /**
     * Concrete implementation of AbstractTransformer for testing
     */
    public static class TestTransformer extends AbstractTransformer {
        private int transformCallCount = 0;
        private ResponseData lastResponseData = null;
        private ResultData returnedResultData = null;
        private boolean throwException = false;

        @Override
        public ResultData transform(ResponseData responseData) {
            transformCallCount++;
            lastResponseData = responseData;

            if (throwException) {
                throw new CrawlerSystemException("Test exception");
            }

            if (returnedResultData != null) {
                return returnedResultData;
            }

            // Create default ResultData
            ResultData resultData = new ResultData();
            resultData.setTransformerName(getName());
            return resultData;
        }

        @Override
        public Object getData(AccessResultData<?> accessResultData) {
            return accessResultData != null ? accessResultData.getData() : null;
        }

        public int getTransformCallCount() {
            return transformCallCount;
        }

        public ResponseData getLastResponseData() {
            return lastResponseData;
        }

        public void setReturnedResultData(ResultData resultData) {
            this.returnedResultData = resultData;
        }

        public void setThrowException(boolean throwException) {
            this.throwException = throwException;
        }

        public void reset() {
            transformCallCount = 0;
            lastResponseData = null;
            returnedResultData = null;
            throwException = false;
        }
    }

    /**
     * Another concrete implementation for testing different scenarios
     */
    public static class NameTrackingTransformer extends AbstractTransformer {
        private final List<String> nameHistory = new ArrayList<>();

        @Override
        public ResultData transform(ResponseData responseData) {
            nameHistory.add(getName());
            ResultData resultData = new ResultData();
            resultData.setTransformerName(getName());
            return resultData;
        }

        @Override
        public Object getData(AccessResultData<?> accessResultData) {
            return accessResultData;
        }

        public List<String> getNameHistory() {
            return new ArrayList<>(nameHistory);
        }

        public void clearHistory() {
            nameHistory.clear();
        }
    }

    private TestTransformer testTransformer;

    @Override
    @BeforeEach
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        testTransformer = new TestTransformer();
    }

    /**
     * Test default constructor
     */
    public void test_constructor() {
        TestTransformer transformer = new TestTransformer();
        assertNotNull(transformer);
        assertNull(transformer.getName());
    }

    /**
     * Test getName and setName with normal values
     */
    public void test_name_getterSetter() {
        // Initial state
        assertNull(testTransformer.getName());

        // Set name
        testTransformer.setName("testTransformer");
        assertEquals("testTransformer", testTransformer.getName());

        // Change name
        testTransformer.setName("changedName");
        assertEquals("changedName", testTransformer.getName());

        // Set null name
        testTransformer.setName(null);
        assertNull(testTransformer.getName());

        // Set empty name
        testTransformer.setName("");
        assertEquals("", testTransformer.getName());
    }

    /**
     * Test name with special characters
     */
    public void test_name_specialCharacters() {
        // Test with spaces
        testTransformer.setName("name with spaces");
        assertEquals("name with spaces", testTransformer.getName());

        // Test with special characters
        testTransformer.setName("name-with_special.chars#123");
        assertEquals("name-with_special.chars#123", testTransformer.getName());

        // Test with Unicode characters
        testTransformer.setName("変換器の名前");
        assertEquals("変換器の名前", testTransformer.getName());

        // Test with mixed characters
        testTransformer.setName("Transformer-変換器-123");
        assertEquals("Transformer-変換器-123", testTransformer.getName());
    }

    /**
     * Test name with very long string
     */
    public void test_name_veryLongString() {
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longName.append("verylongname");
        }
        String longNameStr = longName.toString();

        testTransformer.setName(longNameStr);
        assertEquals(longNameStr, testTransformer.getName());
        assertEquals(120000, testTransformer.getName().length());
    }

    /**
     * Test name with whitespace variations
     */
    public void test_name_whitespace() {
        // Leading whitespace
        testTransformer.setName("  leadingSpaces");
        assertEquals("  leadingSpaces", testTransformer.getName());

        // Trailing whitespace
        testTransformer.setName("trailingSpaces  ");
        assertEquals("trailingSpaces  ", testTransformer.getName());

        // Both leading and trailing
        testTransformer.setName("  bothSpaces  ");
        assertEquals("  bothSpaces  ", testTransformer.getName());

        // Only whitespace
        testTransformer.setName("   ");
        assertEquals("   ", testTransformer.getName());

        // Tab characters
        testTransformer.setName("\tname\twith\ttabs\t");
        assertEquals("\tname\twith\ttabs\t", testTransformer.getName());

        // Newline characters
        testTransformer.setName("name\nwith\nnewlines");
        assertEquals("name\nwith\nnewlines", testTransformer.getName());
    }

    /**
     * Test multiple name changes
     */
    public void test_multipleNameChanges() {
        String[] names = { "first", "second", "third", "fourth", "fifth" };

        for (String name : names) {
            testTransformer.setName(name);
            assertEquals(name, testTransformer.getName());
        }

        // Verify final state
        assertEquals("fifth", testTransformer.getName());
    }

    /**
     * Test transform method implementation
     */
    public void test_transform_implementation() {
        testTransformer.setName("myTransformer");

        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com");

        ResultData resultData = testTransformer.transform(responseData);

        assertNotNull(resultData);
        assertEquals("myTransformer", resultData.getTransformerName());
        assertEquals(1, testTransformer.getTransformCallCount());
        assertTrue(responseData == testTransformer.getLastResponseData());
    }

    /**
     * Test transform with null name
     */
    public void test_transform_withNullName() {
        // Don't set name (remains null)
        ResponseData responseData = new ResponseData();

        ResultData resultData = testTransformer.transform(responseData);

        assertNotNull(resultData);
        assertNull(resultData.getTransformerName());
    }

    /**
     * Test transform with exception
     */
    public void test_transform_withException() {
        testTransformer.setName("exceptionTransformer");
        testTransformer.setThrowException(true);

        ResponseData responseData = new ResponseData();

        try {
            testTransformer.transform(responseData);
            fail();
        } catch (CrawlerSystemException e) {
            assertEquals("Test exception", e.getMessage());
        }

        assertEquals(1, testTransformer.getTransformCallCount());
    }

    /**
     * Test getData implementation
     */
    public void test_getData_implementation() {
        AccessResultData<String> accessResultData = new AccessResultData<String>() {
            private byte[] data = "test data".getBytes();
            private String encoding = "UTF-8";
            private String transformerName = "test";
            private String id;

            @Override
            public String getId() {
                return id;
            }

            @Override
            public void setId(String id) {
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
                return new String(data);
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
        };

        Object data = testTransformer.getData(accessResultData);
        byte[] expected = "test data".getBytes();
        byte[] actual = (byte[]) data;
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }

        // Test with null
        assertNull(testTransformer.getData(null));
    }

    /**
     * Test name tracking transformer
     */
    public void test_nameTrackingTransformer() {
        NameTrackingTransformer tracker = new NameTrackingTransformer();
        ResponseData responseData = new ResponseData();

        // Transform with different names
        tracker.setName("name1");
        tracker.transform(responseData);

        tracker.setName("name2");
        tracker.transform(responseData);

        tracker.setName("name3");
        tracker.transform(responseData);

        List<String> history = tracker.getNameHistory();
        assertEquals(3, history.size());
        assertEquals("name1", history.get(0));
        assertEquals("name2", history.get(1));
        assertEquals("name3", history.get(2));
    }

    /**
     * Test concurrent name access
     */
    public void test_concurrentNameAccess() throws Exception {
        final int threadCount = 100;
        final int operationsPerThread = 100;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        final AtomicInteger errorCount = new AtomicInteger(0);

        testTransformer.setName("concurrentName");

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.await();
                        for (int j = 0; j < operationsPerThread; j++) {
                            // Read name
                            String name = testTransformer.getName();
                            if (!"concurrentName".equals(name)) {
                                errorCount.incrementAndGet();
                            }

                            // Occasionally change name (10% of threads)
                            if (threadId % 10 == 0 && j % 10 == 0) {
                                testTransformer.setName("concurrentName");
                            }
                        }
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                    } finally {
                        endLatch.countDown();
                    }
                }
            });
        }

        startLatch.countDown();
        endLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(0, errorCount.get());
        assertEquals("concurrentName", testTransformer.getName());
    }

    /**
     * Test concurrent name changes
     */
    public void test_concurrentNameChanges() throws Exception {
        final int threadCount = 10;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        final List<Exception> exceptions = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.await();
                        for (int j = 0; j < 100; j++) {
                            testTransformer.setName("thread" + threadId + "_iteration" + j);
                            String name = testTransformer.getName();
                            assertNotNull(name);
                            assertTrue(name.startsWith("thread"));
                        }
                    } catch (Exception e) {
                        synchronized (exceptions) {
                            exceptions.add(e);
                        }
                    } finally {
                        endLatch.countDown();
                    }
                }
            });
        }

        startLatch.countDown();
        endLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(exceptions.isEmpty());
        // Final name should be from one of the threads
        assertNotNull(testTransformer.getName());
        assertTrue(testTransformer.getName().startsWith("thread"));
    }

    /**
     * Test inheritance behavior
     */
    public void test_inheritanceBehavior() {
        // Test that subclasses inherit name management
        TestTransformer transformer1 = new TestTransformer();
        NameTrackingTransformer transformer2 = new NameTrackingTransformer();

        transformer1.setName("transformer1");
        transformer2.setName("transformer2");

        assertEquals("transformer1", transformer1.getName());
        assertEquals("transformer2", transformer2.getName());

        // Both should have independent name storage
        transformer1.setName("changed1");
        assertEquals("changed1", transformer1.getName());
        assertEquals("transformer2", transformer2.getName());
    }

    /**
     * Test protected field access
     */
    public void test_protectedFieldAccess() {
        TestTransformer transformer = new TestTransformer();

        // Direct field access (simulating subclass behavior)
        transformer.name = "directFieldAccess";
        assertEquals("directFieldAccess", transformer.getName());

        // Setter should update the field
        transformer.setName("setterAccess");
        assertEquals("setterAccess", transformer.name);

        // Field modification should be reflected in getter
        transformer.name = "modifiedField";
        assertEquals("modifiedField", transformer.getName());
    }

    /**
     * Test name persistence across operations
     */
    public void test_namePersistence() {
        testTransformer.setName("persistentName");

        // Perform multiple transforms
        for (int i = 0; i < 10; i++) {
            ResponseData responseData = new ResponseData();
            ResultData resultData = testTransformer.transform(responseData);
            assertEquals("persistentName", resultData.getTransformerName());
        }

        // Name should remain unchanged
        assertEquals("persistentName", testTransformer.getName());
        assertEquals(10, testTransformer.getTransformCallCount());
    }

    /**
     * Test name with control characters
     */
    public void test_name_controlCharacters() {
        // Test with various control characters
        testTransformer.setName("name\0with\0null");
        assertEquals("name\0with\0null", testTransformer.getName());

        testTransformer.setName("name\rwith\rcarriage\rreturn");
        assertEquals("name\rwith\rcarriage\rreturn", testTransformer.getName());

        testTransformer.setName("name\bwith\bbackspace");
        assertEquals("name\bwith\bbackspace", testTransformer.getName());

        testTransformer.setName("name\fwith\fform\ffeed");
        assertEquals("name\fwith\fform\ffeed", testTransformer.getName());
    }

    /**
     * Test multiple transformer instances
     */
    public void test_multipleInstances() {
        TestTransformer transformer1 = new TestTransformer();
        TestTransformer transformer2 = new TestTransformer();
        TestTransformer transformer3 = new TestTransformer();

        transformer1.setName("instance1");
        transformer2.setName("instance2");
        transformer3.setName("instance3");

        assertEquals("instance1", transformer1.getName());
        assertEquals("instance2", transformer2.getName());
        assertEquals("instance3", transformer3.getName());

        // Change one instance's name
        transformer2.setName("changed2");

        // Others should remain unchanged
        assertEquals("instance1", transformer1.getName());
        assertEquals("changed2", transformer2.getName());
        assertEquals("instance3", transformer3.getName());
    }

    /**
     * Test name boundary cases
     */
    public void test_name_boundaryCases() {
        // Single character
        testTransformer.setName("a");
        assertEquals("a", testTransformer.getName());

        // Single space
        testTransformer.setName(" ");
        assertEquals(" ", testTransformer.getName());

        // Single Unicode character
        testTransformer.setName("あ");
        assertEquals("あ", testTransformer.getName());

        // Maximum practical length (avoid OutOfMemoryError)
        StringBuilder maxName = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            maxName.append("0123456789");
        }
        testTransformer.setName(maxName.toString());
        assertEquals(10000, testTransformer.getName().length());
        assertEquals(maxName.toString(), testTransformer.getName());
    }

    /**
     * Test complete workflow
     */
    public void test_completeWorkflow() {
        // Create transformer with name
        TestTransformer transformer = new TestTransformer();
        transformer.setName("workflowTransformer");

        // Create custom result data
        ResultData customResult = new ResultData();
        customResult.setTransformerName("customName");
        transformer.setReturnedResultData(customResult);

        // Perform transform
        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/test");

        ResultData result = transformer.transform(responseData);

        // Verify
        assertNotNull(result);
        assertEquals("customName", result.getTransformerName());
        assertEquals(1, transformer.getTransformCallCount());
        assertEquals("http://example.com/test", transformer.getLastResponseData().getUrl());

        // Reset and verify
        transformer.reset();
        assertEquals(0, transformer.getTransformCallCount());
        assertNull(transformer.getLastResponseData());

        // Name should persist after reset
        assertEquals("workflowTransformer", transformer.getName());
    }
}
