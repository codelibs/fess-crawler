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

import org.codelibs.fess.crawler.Constants;
import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for {@link AccessResultImpl}.
 */
public class AccessResultImplTest extends PlainTestCase {

    @Test
    public void test_defaultConstructor() {
        // Test default constructor
        AccessResultImpl<Long> result = new AccessResultImpl<>();
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getSessionId());
        assertNull(result.getRuleId());
        assertNull(result.getUrl());
        assertNull(result.getParentUrl());
        assertEquals(Constants.OK_STATUS, result.getStatus());
        assertNull(result.getHttpStatusCode());
        assertNull(result.getMethod());
        assertNull(result.getMimeType());
        assertNull(result.getCreateTime());
        assertNull(result.getExecutionTime());
        assertNull(result.getContentLength());
        assertNull(result.getLastModified());
        assertNull(result.getAccessResultData());
    }

    @Test
    public void test_idGetterSetter() {
        // Test ID getter/setter
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        result.setId(123L);
        assertEquals(Long.valueOf(123L), result.getId());

        result.setId(null);
        assertNull(result.getId());
    }

    @Test
    public void test_sessionIdGetterSetter() {
        // Test session ID getter/setter
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        String sessionId = "session123";
        result.setSessionId(sessionId);
        assertEquals(sessionId, result.getSessionId());
    }

    @Test
    public void test_ruleIdGetterSetter() {
        // Test rule ID getter/setter
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        String ruleId = "rule456";
        result.setRuleId(ruleId);
        assertEquals(ruleId, result.getRuleId());
    }

    @Test
    public void test_urlGetterSetter() {
        // Test URL getter/setter
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        String url = "https://example.com/page";
        result.setUrl(url);
        assertEquals(url, result.getUrl());
    }

    @Test
    public void test_parentUrlGetterSetter() {
        // Test parent URL getter/setter
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        String parentUrl = "https://example.com";
        result.setParentUrl(parentUrl);
        assertEquals(parentUrl, result.getParentUrl());
    }

    @Test
    public void test_statusGetterSetter() {
        // Test status getter/setter
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        // Default should be OK_STATUS
        assertEquals(Constants.OK_STATUS, result.getStatus());

        result.setStatus(Constants.NOT_MODIFIED_STATUS);
        assertEquals(Integer.valueOf(Constants.NOT_MODIFIED_STATUS), result.getStatus());
    }

    @Test
    public void test_httpStatusCodeGetterSetter() {
        // Test HTTP status code getter/setter
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        result.setHttpStatusCode(200);
        assertEquals(Integer.valueOf(200), result.getHttpStatusCode());

        result.setHttpStatusCode(404);
        assertEquals(Integer.valueOf(404), result.getHttpStatusCode());

        result.setHttpStatusCode(500);
        assertEquals(Integer.valueOf(500), result.getHttpStatusCode());
    }

    @Test
    public void test_methodGetterSetter() {
        // Test method getter/setter
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        result.setMethod("GET");
        assertEquals("GET", result.getMethod());

        result.setMethod("POST");
        assertEquals("POST", result.getMethod());
    }

    @Test
    public void test_mimeTypeGetterSetter() {
        // Test MIME type getter/setter
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        result.setMimeType("text/html");
        assertEquals("text/html", result.getMimeType());

        result.setMimeType("application/json");
        assertEquals("application/json", result.getMimeType());
    }

    @Test
    public void test_createTimeGetterSetter() {
        // Test create time getter/setter
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        Long createTime = System.currentTimeMillis();
        result.setCreateTime(createTime);
        assertEquals(createTime, result.getCreateTime());
    }

    @Test
    public void test_executionTimeGetterSetter() {
        // Test execution time getter/setter
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        result.setExecutionTime(1500);
        assertEquals(Integer.valueOf(1500), result.getExecutionTime());

        result.setExecutionTime(0);
        assertEquals(Integer.valueOf(0), result.getExecutionTime());
    }

    @Test
    public void test_contentLengthGetterSetter() {
        // Test content length getter/setter
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        result.setContentLength(2048L);
        assertEquals(Long.valueOf(2048L), result.getContentLength());

        result.setContentLength(0L);
        assertEquals(Long.valueOf(0L), result.getContentLength());
    }

    @Test
    public void test_lastModifiedGetterSetter() {
        // Test last modified getter/setter
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        Long lastModified = System.currentTimeMillis();
        result.setLastModified(lastModified);
        assertEquals(lastModified, result.getLastModified());
    }

    @Test
    public void test_accessResultDataGetterSetter() {
        // Test access result data getter/setter
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        AccessResultData<Long> data = new AccessResultDataImpl<>();
        result.setAccessResultData(data);
        assertEquals(data, result.getAccessResultData());
    }

    @Test
    public void test_initWithResponseDataOnly() {
        // Test init method with ResponseData only
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        ResponseData responseData = new ResponseData();
        responseData.setUrl("https://example.com/page");
        responseData.setHttpStatusCode(200);
        responseData.setMethod("GET");
        responseData.setMimeType("text/html");
        responseData.setSessionId("session123");
        responseData.setRuleId("rule456");
        responseData.setParentUrl("https://example.com");
        responseData.setStatus(Constants.OK_STATUS);
        responseData.setExecutionTime(1000L);
        responseData.setContentLength(2048L);

        result.init(responseData, null);

        // Verify fields were copied from ResponseData
        assertEquals("https://example.com/page", result.getUrl());
        assertEquals(Integer.valueOf(200), result.getHttpStatusCode());
        assertEquals("GET", result.getMethod());
        assertEquals("text/html", result.getMimeType());
        assertEquals("session123", result.getSessionId());
        assertEquals("rule456", result.getRuleId());
        assertEquals("https://example.com", result.getParentUrl());
        assertEquals(Integer.valueOf(Constants.OK_STATUS), result.getStatus());

        // CreateTime should be set
        assertNotNull(result.getCreateTime());

        // AccessResultData should be created (empty)
        assertNotNull(result.getAccessResultData());
    }

    @Test
    public void test_initWithResultDataOnly() {
        // Test init method with ResultData only
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        ResultData resultData = new ResultData();
        resultData.setTransformerName("TestTransformer");
        resultData.setData(new byte[0]); // Set data to avoid getData() error

        result.init(null, resultData);

        // CreateTime should be set
        assertNotNull(result.getCreateTime());

        // AccessResultData should be created with ResultData fields
        assertNotNull(result.getAccessResultData());
    }

    @Test
    public void test_initWithBothResponseAndResultData() {
        // Test init method with both ResponseData and ResultData
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        ResponseData responseData = new ResponseData();
        responseData.setUrl("https://example.com/page");
        responseData.setHttpStatusCode(200);
        responseData.setMethod("GET");

        ResultData resultData = new ResultData();
        resultData.setTransformerName("TestTransformer");
        resultData.setData(new byte[0]); // Set data to avoid getData() error

        result.init(responseData, resultData);

        // Verify fields from ResponseData
        assertEquals("https://example.com/page", result.getUrl());
        assertEquals(Integer.valueOf(200), result.getHttpStatusCode());
        assertEquals("GET", result.getMethod());

        // CreateTime should be set
        assertNotNull(result.getCreateTime());

        // AccessResultData should be created
        assertNotNull(result.getAccessResultData());
    }

    @Test
    public void test_initWithNullValues() {
        // Test init method with null values
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        result.init(null, null);

        // CreateTime should still be set
        assertNotNull(result.getCreateTime());

        // AccessResultData should be created
        assertNotNull(result.getAccessResultData());
    }

    @Test
    public void test_toString() {
        // Test toString method
        AccessResultImpl<Long> result = new AccessResultImpl<>();
        result.setId(123L);
        result.setSessionId("session456");
        result.setRuleId("rule789");
        result.setUrl("https://example.com/test");
        result.setParentUrl("https://example.com");
        result.setStatus(Constants.OK_STATUS);
        result.setHttpStatusCode(200);
        result.setMethod("GET");
        result.setMimeType("text/html");

        String resultString = result.toString();
        assertNotNull(resultString);
        assertTrue(resultString.contains("AccessResultImpl"));
        assertTrue(resultString.contains("123"));
        assertTrue(resultString.contains("session456"));
        assertTrue(resultString.contains("https://example.com/test"));
    }

    @Test
    public void test_complexScenario() {
        // Test complex scenario with full initialization
        AccessResultImpl<Long> result = new AccessResultImpl<>();

        // Create ResponseData with comprehensive data
        ResponseData responseData = new ResponseData();
        responseData.setUrl("https://api.example.com/endpoint");
        responseData.setHttpStatusCode(200);
        responseData.setMethod("POST");
        responseData.setMimeType("application/json");
        responseData.setSessionId("crawl-session-999");
        responseData.setRuleId("api-rule");
        responseData.setParentUrl("https://api.example.com");
        responseData.setStatus(Constants.OK_STATUS);
        responseData.setExecutionTime(2500L);
        responseData.setContentLength(4096L);

        // Create ResultData
        ResultData resultData = new ResultData();
        resultData.setTransformerName("JsonTransformer");
        resultData.setData(new byte[0]); // Set data to avoid getData() error

        // Initialize
        long beforeInit = System.currentTimeMillis();
        result.init(responseData, resultData);
        long afterInit = System.currentTimeMillis();

        // Verify all fields
        assertEquals("https://api.example.com/endpoint", result.getUrl());
        assertEquals(Integer.valueOf(200), result.getHttpStatusCode());
        assertEquals("POST", result.getMethod());
        assertEquals("application/json", result.getMimeType());
        assertEquals("crawl-session-999", result.getSessionId());
        assertEquals("api-rule", result.getRuleId());
        assertEquals("https://api.example.com", result.getParentUrl());
        assertEquals(Integer.valueOf(Constants.OK_STATUS), result.getStatus());
        assertNotNull(result.getCreateTime());
        assertTrue(result.getCreateTime() >= beforeInit);
        assertTrue(result.getCreateTime() <= afterInit);
        assertNotNull(result.getAccessResultData());
    }

    @Test
    public void test_interfaceImplementation() {
        // Test that AccessResultImpl implements AccessResult
        AccessResultImpl<Long> result = new AccessResultImpl<>();
        assertTrue(result instanceof AccessResult);
    }

    @Test
    public void test_genericTypeFlexibility() {
        // Test with different generic types
        AccessResultImpl<Integer> intResult = new AccessResultImpl<>();
        intResult.setId(42);
        assertEquals(Integer.valueOf(42), intResult.getId());

        AccessResultImpl<String> stringResult = new AccessResultImpl<>();
        stringResult.setId("result-id-123");
        assertEquals("result-id-123", stringResult.getId());

        AccessResultImpl<Long> longResult = new AccessResultImpl<>();
        longResult.setId(987654321L);
        assertEquals(Long.valueOf(987654321L), longResult.getId());
    }
}
