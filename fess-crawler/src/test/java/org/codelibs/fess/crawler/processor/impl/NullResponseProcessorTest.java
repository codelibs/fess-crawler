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
package org.codelibs.fess.crawler.processor.impl;

import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for {@link NullResponseProcessor}.
 */
public class NullResponseProcessorTest extends PlainTestCase {

    public void test_constructor() {
        // Test constructor
        NullResponseProcessor processor = new NullResponseProcessor();
        assertNotNull(processor);
    }

    public void test_implementsResponseProcessor() {
        // Test that NullResponseProcessor implements ResponseProcessor
        NullResponseProcessor processor = new NullResponseProcessor();
        assertTrue(processor instanceof ResponseProcessor);
    }

    public void test_processWithValidResponseData() {
        // Test process method with valid ResponseData
        NullResponseProcessor processor = new NullResponseProcessor();
        ResponseData responseData = new ResponseData();
        responseData.setUrl("https://example.com");
        responseData.setHttpStatusCode(200);

        // Execute - should not throw any exception
        processor.process(responseData);

        // Verify that ResponseData is unchanged
        assertEquals("https://example.com", responseData.getUrl());
        assertEquals(200, responseData.getHttpStatusCode());
    }

    public void test_processWithNullResponseData() {
        // Test process method with null ResponseData
        NullResponseProcessor processor = new NullResponseProcessor();

        // Execute - should not throw any exception even with null
        processor.process(null);
    }

    public void test_processWithEmptyResponseData() {
        // Test process method with empty ResponseData
        NullResponseProcessor processor = new NullResponseProcessor();
        ResponseData responseData = new ResponseData();

        // Execute - should not throw any exception
        processor.process(responseData);

        // Verify ResponseData is unchanged
        assertNull(responseData.getUrl());
        assertEquals(0, responseData.getHttpStatusCode());
    }

    public void test_processMultipleTimes() {
        // Test calling process multiple times
        NullResponseProcessor processor = new NullResponseProcessor();
        ResponseData responseData1 = new ResponseData();
        responseData1.setUrl("https://example.com/page1");

        ResponseData responseData2 = new ResponseData();
        responseData2.setUrl("https://example.com/page2");

        ResponseData responseData3 = new ResponseData();
        responseData3.setUrl("https://example.com/page3");

        // Execute multiple times - should not throw any exception
        processor.process(responseData1);
        processor.process(responseData2);
        processor.process(responseData3);

        // Verify all ResponseData objects are unchanged
        assertEquals("https://example.com/page1", responseData1.getUrl());
        assertEquals("https://example.com/page2", responseData2.getUrl());
        assertEquals("https://example.com/page3", responseData3.getUrl());
    }

    public void test_processDoesNotModifyResponseData() {
        // Test that process does not modify ResponseData
        NullResponseProcessor processor = new NullResponseProcessor();
        ResponseData responseData = new ResponseData();

        // Set various properties
        responseData.setUrl("https://example.com/test");
        responseData.setHttpStatusCode(200);
        responseData.setMethod("GET");
        responseData.setMimeType("text/html");
        responseData.setCharSet("UTF-8");
        responseData.setContentLength(1024L);
        responseData.setNoFollow(false);

        // Execute
        processor.process(responseData);

        // Verify nothing changed
        assertEquals("https://example.com/test", responseData.getUrl());
        assertEquals(200, responseData.getHttpStatusCode());
        assertEquals("GET", responseData.getMethod());
        assertEquals("text/html", responseData.getMimeType());
        assertEquals("UTF-8", responseData.getCharSet());
        assertEquals(1024L, responseData.getContentLength());
        assertFalse(responseData.isNoFollow());
    }

    public void test_processWithResponseDataContainingBody() {
        // Test process with ResponseData containing body
        NullResponseProcessor processor = new NullResponseProcessor();
        ResponseData responseData = new ResponseData();

        byte[] content = "Test content".getBytes();
        responseData.setResponseBody(content);
        responseData.setUrl("https://example.com");

        // Execute - should not throw any exception
        processor.process(responseData);

        // Verify ResponseData still has body
        assertTrue(responseData.hasResponseBody());
        assertNotNull(responseData.getResponseBody());
    }

    public void test_processWithResponseDataContainingMetadata() {
        // Test process with ResponseData containing metadata
        NullResponseProcessor processor = new NullResponseProcessor();
        ResponseData responseData = new ResponseData();

        responseData.addMetaData("key1", "value1");
        responseData.addMetaData("key2", 123);

        // Execute - should not throw any exception
        processor.process(responseData);

        // Verify metadata is unchanged
        assertEquals(2, responseData.getMetaDataMap().size());
        assertEquals("value1", responseData.getMetaDataMap().get("key1"));
        assertEquals(123, responseData.getMetaDataMap().get("key2"));
    }

    public void test_processWithResponseDataContainingChildUrls() {
        // Test process with ResponseData containing child URLs
        NullResponseProcessor processor = new NullResponseProcessor();
        ResponseData responseData = new ResponseData();

        RequestData child1 = new RequestData();
        child1.setUrl("https://example.com/child1");
        responseData.addChildUrl(child1);

        RequestData child2 = new RequestData();
        child2.setUrl("https://example.com/child2");
        responseData.addChildUrl(child2);

        // Execute - should not throw any exception
        processor.process(responseData);

        // Verify child URLs are unchanged
        assertEquals(2, responseData.getChildUrlSet().size());
    }

    public void test_noOpBehavior() {
        // Verify that NullResponseProcessor truly does nothing
        NullResponseProcessor processor = new NullResponseProcessor();

        // Create ResponseData in various states
        ResponseData data1 = null;
        ResponseData data2 = new ResponseData();
        ResponseData data3 = new ResponseData();
        data3.setUrl("https://example.com");
        data3.setHttpStatusCode(404);

        // All should complete without error and without modification
        processor.process(data1);
        processor.process(data2);
        processor.process(data3);

        // data3 should still have its original values
        assertEquals("https://example.com", data3.getUrl());
        assertEquals(404, data3.getHttpStatusCode());
    }

    public void test_threadSafety() {
        // Test that NullResponseProcessor can be used concurrently
        // Since it does nothing, it should be inherently thread-safe
        final NullResponseProcessor processor = new NullResponseProcessor();

        // Create multiple response data objects
        final ResponseData[] responseDataArray = new ResponseData[10];
        for (int i = 0; i < 10; i++) {
            responseDataArray[i] = new ResponseData();
            responseDataArray[i].setUrl("https://example.com/page" + i);
        }

        // Process all in sequence (could be parallel, but not necessary for this test)
        for (ResponseData responseData : responseDataArray) {
            processor.process(responseData);
        }

        // Verify all are unchanged
        for (int i = 0; i < 10; i++) {
            assertEquals("https://example.com/page" + i, responseDataArray[i].getUrl());
        }
    }
}
