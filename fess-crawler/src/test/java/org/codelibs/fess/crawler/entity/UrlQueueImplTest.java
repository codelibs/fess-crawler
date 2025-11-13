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
package org.codelibs.fess.crawler.entity;

import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for {@link UrlQueueImpl}.
 */
public class UrlQueueImplTest extends PlainTestCase {

    public void test_defaultConstructor() {
        // Test default constructor
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();
        assertNotNull(queue);
        assertNull(queue.getId());
        assertNull(queue.getSessionId());
        assertNull(queue.getMethod());
        assertNull(queue.getUrl());
        assertNull(queue.getMetaData());
        assertNull(queue.getEncoding());
        assertNull(queue.getParentUrl());
        assertNull(queue.getDepth());
        assertNull(queue.getLastModified());
        assertNull(queue.getCreateTime());
        assertEquals(1.0f, queue.getWeight(), 0.001f);
    }

    public void test_idGetterSetter() {
        // Test ID getter/setter with Long
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();

        queue.setId(123L);
        assertEquals(Long.valueOf(123L), queue.getId());

        queue.setId(null);
        assertNull(queue.getId());
    }

    public void test_idWithStringType() {
        // Test ID getter/setter with String
        UrlQueueImpl<String> queue = new UrlQueueImpl<>();

        queue.setId("id123");
        assertEquals("id123", queue.getId());
    }

    public void test_sessionIdGetterSetter() {
        // Test session ID getter/setter
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();

        String sessionId = "session456";
        queue.setSessionId(sessionId);
        assertEquals(sessionId, queue.getSessionId());

        queue.setSessionId(null);
        assertNull(queue.getSessionId());
    }

    public void test_methodGetterSetter() {
        // Test method getter/setter
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();

        queue.setMethod("GET");
        assertEquals("GET", queue.getMethod());

        queue.setMethod("POST");
        assertEquals("POST", queue.getMethod());

        queue.setMethod(null);
        assertNull(queue.getMethod());
    }

    public void test_urlGetterSetter() {
        // Test URL getter/setter
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();

        String url = "https://example.com/path";
        queue.setUrl(url);
        assertEquals(url, queue.getUrl());

        queue.setUrl(null);
        assertNull(queue.getUrl());
    }

    public void test_metaDataGetterSetter() {
        // Test metadata getter/setter
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();

        String metaData = "{\"key\":\"value\"}";
        queue.setMetaData(metaData);
        assertEquals(metaData, queue.getMetaData());

        queue.setMetaData(null);
        assertNull(queue.getMetaData());
    }

    public void test_encodingGetterSetter() {
        // Test encoding getter/setter
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();

        queue.setEncoding("UTF-8");
        assertEquals("UTF-8", queue.getEncoding());

        queue.setEncoding("ISO-8859-1");
        assertEquals("ISO-8859-1", queue.getEncoding());

        queue.setEncoding(null);
        assertNull(queue.getEncoding());
    }

    public void test_parentUrlGetterSetter() {
        // Test parent URL getter/setter
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();

        String parentUrl = "https://example.com";
        queue.setParentUrl(parentUrl);
        assertEquals(parentUrl, queue.getParentUrl());

        queue.setParentUrl(null);
        assertNull(queue.getParentUrl());
    }

    public void test_depthGetterSetter() {
        // Test depth getter/setter
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();

        queue.setDepth(0);
        assertEquals(Integer.valueOf(0), queue.getDepth());

        queue.setDepth(5);
        assertEquals(Integer.valueOf(5), queue.getDepth());

        queue.setDepth(null);
        assertNull(queue.getDepth());
    }

    public void test_lastModifiedGetterSetter() {
        // Test lastModified getter/setter
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();

        Long timestamp = System.currentTimeMillis();
        queue.setLastModified(timestamp);
        assertEquals(timestamp, queue.getLastModified());

        queue.setLastModified(null);
        assertNull(queue.getLastModified());
    }

    public void test_createTimeGetterSetter() {
        // Test createTime getter/setter
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();

        Long timestamp = System.currentTimeMillis();
        queue.setCreateTime(timestamp);
        assertEquals(timestamp, queue.getCreateTime());

        queue.setCreateTime(null);
        assertNull(queue.getCreateTime());
    }

    public void test_weightGetterSetter() {
        // Test weight getter/setter
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();

        // Default weight should be 1.0
        assertEquals(1.0f, queue.getWeight(), 0.001f);

        queue.setWeight(0.5f);
        assertEquals(0.5f, queue.getWeight(), 0.001f);

        queue.setWeight(2.5f);
        assertEquals(2.5f, queue.getWeight(), 0.001f);

        queue.setWeight(0.0f);
        assertEquals(0.0f, queue.getWeight(), 0.001f);
    }

    public void test_toString() {
        // Test toString method
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();
        queue.setId(123L);
        queue.setSessionId("session456");
        queue.setMethod("GET");
        queue.setUrl("https://example.com");
        queue.setEncoding("UTF-8");
        queue.setParentUrl("https://parent.com");
        queue.setDepth(2);

        String result = queue.toString();
        assertNotNull(result);
        assertTrue(result.contains("UrlQueueImpl"));
        assertTrue(result.contains("123"));
        assertTrue(result.contains("session456"));
        assertTrue(result.contains("GET"));
        assertTrue(result.contains("https://example.com"));
        assertTrue(result.contains("UTF-8"));
    }

    public void test_toStringWithNullValues() {
        // Test toString with null values
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();

        String result = queue.toString();
        assertNotNull(result);
        assertTrue(result.contains("UrlQueueImpl"));
    }

    public void test_complexScenario() {
        // Test complex scenario with all fields
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();

        Long id = 999L;
        String sessionId = "crawl-session-123";
        String method = "POST";
        String url = "https://api.example.com/endpoint";
        String metaData = "{\"priority\":\"high\"}";
        String encoding = "UTF-8";
        String parentUrl = "https://api.example.com";
        Integer depth = 3;
        Long lastModified = System.currentTimeMillis() - 10000;
        Long createTime = System.currentTimeMillis();
        float weight = 1.5f;

        queue.setId(id);
        queue.setSessionId(sessionId);
        queue.setMethod(method);
        queue.setUrl(url);
        queue.setMetaData(metaData);
        queue.setEncoding(encoding);
        queue.setParentUrl(parentUrl);
        queue.setDepth(depth);
        queue.setLastModified(lastModified);
        queue.setCreateTime(createTime);
        queue.setWeight(weight);

        // Verify all values
        assertEquals(id, queue.getId());
        assertEquals(sessionId, queue.getSessionId());
        assertEquals(method, queue.getMethod());
        assertEquals(url, queue.getUrl());
        assertEquals(metaData, queue.getMetaData());
        assertEquals(encoding, queue.getEncoding());
        assertEquals(parentUrl, queue.getParentUrl());
        assertEquals(depth, queue.getDepth());
        assertEquals(lastModified, queue.getLastModified());
        assertEquals(createTime, queue.getCreateTime());
        assertEquals(weight, queue.getWeight(), 0.001f);
    }

    public void test_interfaceImplementation() {
        // Test that UrlQueueImpl implements UrlQueue
        UrlQueueImpl<Long> queue = new UrlQueueImpl<>();
        assertTrue(queue instanceof UrlQueue);
    }

    public void test_genericTypeFlexibility() {
        // Test with different generic types
        UrlQueueImpl<Integer> intQueue = new UrlQueueImpl<>();
        intQueue.setId(42);
        assertEquals(Integer.valueOf(42), intQueue.getId());

        UrlQueueImpl<String> stringQueue = new UrlQueueImpl<>();
        stringQueue.setId("string-id");
        assertEquals("string-id", stringQueue.getId());

        UrlQueueImpl<Long> longQueue = new UrlQueueImpl<>();
        longQueue.setId(123456789L);
        assertEquals(Long.valueOf(123456789L), longQueue.getId());
    }
}
