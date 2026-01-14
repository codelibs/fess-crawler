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
package org.codelibs.fess.crawler.exception;

import java.util.HashSet;
import java.util.Set;

import org.codelibs.fess.crawler.entity.RequestData;
import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for ChildUrlsException.
 * Tests constructor, child URL list functionality, and message formatting.
 */
public class ChildUrlsExceptionTest extends PlainTestCase {

    /**
     * Test constructor with empty set
     */
    @Test
    public void test_constructor_emptySet() {
        Set<RequestData> childUrls = new HashSet<>();
        ChildUrlsException exception = new ChildUrlsException(childUrls, "No URLs found");

        assertNotNull(exception);
        assertEquals(childUrls, exception.getChildUrlList());
        assertTrue(exception.getMessage().contains("0"));
        assertTrue(exception.getMessage().contains("No URLs found"));
    }

    /**
     * Test constructor with single URL
     */
    @Test
    public void test_constructor_singleUrl() {
        Set<RequestData> childUrls = new HashSet<>();
        RequestData requestData = new RequestData();
        requestData.setUrl("http://example.com/page1");
        childUrls.add(requestData);

        ChildUrlsException exception = new ChildUrlsException(childUrls, "Found link");

        assertNotNull(exception);
        assertEquals(1, exception.getChildUrlList().size());
        assertTrue(exception.getMessage().contains("1"));
        assertTrue(exception.getMessage().contains("Found link"));
    }

    /**
     * Test constructor with multiple URLs
     */
    @Test
    public void test_constructor_multipleUrls() {
        Set<RequestData> childUrls = new HashSet<>();
        for (int i = 1; i <= 5; i++) {
            RequestData requestData = new RequestData();
            requestData.setUrl("http://example.com/page" + i);
            childUrls.add(requestData);
        }

        ChildUrlsException exception = new ChildUrlsException(childUrls, "Multiple pages found");

        assertNotNull(exception);
        assertEquals(5, exception.getChildUrlList().size());
        assertTrue(exception.getMessage().contains("5"));
        assertTrue(exception.getMessage().contains("Multiple pages found"));
    }

    /**
     * Test message format
     */
    @Test
    public void test_messageFormat() {
        Set<RequestData> childUrls = new HashSet<>();
        RequestData requestData = new RequestData();
        requestData.setUrl("http://example.com/test");
        childUrls.add(requestData);

        ChildUrlsException exception = new ChildUrlsException(childUrls, "Test description");

        String message = exception.getMessage();
        assertTrue(message.startsWith("Threw child urls("));
        assertTrue(message.contains("). "));
        assertTrue(message.contains("Test description"));
    }

    /**
     * Test getChildUrlList returns same reference
     */
    @Test
    public void test_getChildUrlList_returnsSameReference() {
        Set<RequestData> childUrls = new HashSet<>();
        RequestData requestData = new RequestData();
        requestData.setUrl("http://example.com/test");
        childUrls.add(requestData);

        ChildUrlsException exception = new ChildUrlsException(childUrls, "Test");

        assertTrue(childUrls == exception.getChildUrlList());
    }

    /**
     * Test inheritance
     */
    @Test
    public void test_inheritance() {
        Set<RequestData> childUrls = new HashSet<>();
        ChildUrlsException exception = new ChildUrlsException(childUrls, "Test");

        assertTrue(exception instanceof CrawlerSystemException);
        assertTrue(exception instanceof RuntimeException);
    }

    /**
     * Test with empty description
     */
    @Test
    public void test_emptyDescription() {
        Set<RequestData> childUrls = new HashSet<>();
        ChildUrlsException exception = new ChildUrlsException(childUrls, "");

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("0"));
        assertTrue(exception.getMessage().endsWith(". "));
    }

    /**
     * Test with null description
     */
    @Test
    public void test_nullDescription() {
        Set<RequestData> childUrls = new HashSet<>();
        ChildUrlsException exception = new ChildUrlsException(childUrls, null);

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("null"));
    }

    /**
     * Test stack trace is disabled (performance optimization)
     */
    @Test
    public void test_stackTraceDisabled() {
        Set<RequestData> childUrls = new HashSet<>();
        ChildUrlsException exception = new ChildUrlsException(childUrls, "Test");

        // Stack trace should be empty when writableStackTrace is false
        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertEquals(0, stackTrace.length);
    }

    /**
     * Test suppression is disabled (performance optimization)
     */
    @Test
    public void test_suppressionDisabled() {
        Set<RequestData> childUrls = new HashSet<>();
        ChildUrlsException exception = new ChildUrlsException(childUrls, "Test");

        // Add suppressed exception
        exception.addSuppressed(new RuntimeException("Suppressed"));

        // Should not be added when suppression is disabled
        Throwable[] suppressed = exception.getSuppressed();
        assertEquals(0, suppressed.length);
    }

    /**
     * Test throwing and catching
     */
    @Test
    public void test_throwAndCatch() {
        Set<RequestData> childUrls = new HashSet<>();
        RequestData requestData = new RequestData();
        requestData.setUrl("http://example.com/caught");
        childUrls.add(requestData);

        try {
            throw new ChildUrlsException(childUrls, "Throwing");
        } catch (ChildUrlsException e) {
            assertEquals(1, e.getChildUrlList().size());
            assertTrue(e.getMessage().contains("Throwing"));
        }
    }

    /**
     * Test with large number of URLs
     */
    @Test
    public void test_largeNumberOfUrls() {
        Set<RequestData> childUrls = new HashSet<>();
        for (int i = 1; i <= 1000; i++) {
            RequestData requestData = new RequestData();
            requestData.setUrl("http://example.com/page" + i);
            childUrls.add(requestData);
        }

        ChildUrlsException exception = new ChildUrlsException(childUrls, "Large crawl");

        assertEquals(1000, exception.getChildUrlList().size());
        assertTrue(exception.getMessage().contains("1000"));
    }

    /**
     * Test with RequestData containing different methods
     */
    @Test
    public void test_withDifferentMethods() {
        Set<RequestData> childUrls = new HashSet<>();

        RequestData getData = new RequestData();
        getData.setUrl("http://example.com/get");
        getData.setMethod(RequestData.Method.GET);
        childUrls.add(getData);

        RequestData postData = new RequestData();
        postData.setUrl("http://example.com/post");
        postData.setMethod(RequestData.Method.POST);
        childUrls.add(postData);

        ChildUrlsException exception = new ChildUrlsException(childUrls, "Mixed methods");

        assertEquals(2, exception.getChildUrlList().size());
    }
}
