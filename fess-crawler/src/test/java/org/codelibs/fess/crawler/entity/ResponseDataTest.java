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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.RequestData.Method;
import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for {@link ResponseData}.
 */
public class ResponseDataTest extends PlainTestCase {

    @Test
    public void test_defaultConstructor() {
        // Test default constructor
        ResponseData data = new ResponseData();
        assertNotNull(data);
        assertNull(data.getUrl());
        assertEquals(0, data.getHttpStatusCode());
        assertEquals(Constants.OK_STATUS, data.getStatus());
        assertFalse(data.isNoFollow());
        assertFalse(data.hasResponseBody());
    }

    @Test
    public void test_urlGetterSetter() {
        // Test URL getter/setter
        ResponseData data = new ResponseData();
        String url = "https://example.com/path";

        data.setUrl(url);
        assertEquals(url, data.getUrl());

        data.setUrl(null);
        assertNull(data.getUrl());
    }

    @Test
    public void test_httpStatusCodeGetterSetter() {
        // Test HTTP status code getter/setter
        ResponseData data = new ResponseData();

        data.setHttpStatusCode(200);
        assertEquals(200, data.getHttpStatusCode());

        data.setHttpStatusCode(404);
        assertEquals(404, data.getHttpStatusCode());

        data.setHttpStatusCode(500);
        assertEquals(500, data.getHttpStatusCode());
    }

    @Test
    public void test_charSetGetterSetter() {
        // Test charset getter/setter
        ResponseData data = new ResponseData();

        data.setCharSet("UTF-8");
        assertEquals("UTF-8", data.getCharSet());

        data.setCharSet("ISO-8859-1");
        assertEquals("ISO-8859-1", data.getCharSet());

        data.setCharSet(null);
        assertNull(data.getCharSet());
    }

    @Test
    public void test_contentLengthGetterSetter() {
        // Test content length getter/setter
        ResponseData data = new ResponseData();

        data.setContentLength(1024L);
        assertEquals(1024L, data.getContentLength());

        data.setContentLength(0L);
        assertEquals(0L, data.getContentLength());
    }

    @Test
    public void test_mimeTypeGetterSetter() {
        // Test MIME type getter/setter
        ResponseData data = new ResponseData();

        data.setMimeType("text/html");
        assertEquals("text/html", data.getMimeType());

        data.setMimeType("application/json");
        assertEquals("application/json", data.getMimeType());

        data.setMimeType(null);
        assertNull(data.getMimeType());
    }

    @Test
    public void test_methodGetterSetter() {
        // Test method getter/setter
        ResponseData data = new ResponseData();

        data.setMethod("GET");
        assertEquals("GET", data.getMethod());

        data.setMethod("POST");
        assertEquals("POST", data.getMethod());
    }

    @Test
    public void test_parentUrlGetterSetter() {
        // Test parent URL getter/setter
        ResponseData data = new ResponseData();

        String parentUrl = "https://example.com";
        data.setParentUrl(parentUrl);
        assertEquals(parentUrl, data.getParentUrl());
    }

    @Test
    public void test_ruleIdGetterSetter() {
        // Test rule ID getter/setter
        ResponseData data = new ResponseData();

        String ruleId = "rule123";
        data.setRuleId(ruleId);
        assertEquals(ruleId, data.getRuleId());
    }

    @Test
    public void test_sessionIdGetterSetter() {
        // Test session ID getter/setter
        ResponseData data = new ResponseData();

        String sessionId = "session456";
        data.setSessionId(sessionId);
        assertEquals(sessionId, data.getSessionId());
    }

    @Test
    public void test_executionTimeGetterSetter() {
        // Test execution time getter/setter
        ResponseData data = new ResponseData();

        data.setExecutionTime(1500L);
        assertEquals(1500L, data.getExecutionTime());
    }

    @Test
    public void test_lastModifiedGetterSetter() {
        // Test last modified getter/setter
        ResponseData data = new ResponseData();

        Date date = new Date();
        data.setLastModified(date);
        assertEquals(date, data.getLastModified());
    }

    @Test
    public void test_redirectLocationGetterSetter() {
        // Test redirect location getter/setter
        ResponseData data = new ResponseData();

        String redirectUrl = "https://redirect.com";
        data.setRedirectLocation(redirectUrl);
        assertEquals(redirectUrl, data.getRedirectLocation());
    }

    @Test
    public void test_statusGetterSetter() {
        // Test status getter/setter
        ResponseData data = new ResponseData();

        // Default status
        assertEquals(Constants.OK_STATUS, data.getStatus());

        data.setStatus(Constants.NOT_MODIFIED_STATUS);
        assertEquals(Constants.NOT_MODIFIED_STATUS, data.getStatus());
    }

    @Test
    public void test_noFollowGetterSetter() {
        // Test noFollow getter/setter
        ResponseData data = new ResponseData();

        // Default should be false
        assertFalse(data.isNoFollow());

        data.setNoFollow(true);
        assertTrue(data.isNoFollow());

        data.setNoFollow(false);
        assertFalse(data.isNoFollow());
    }

    @Test
    public void test_responseBodyBytes() {
        // Test response body with byte array
        ResponseData data = new ResponseData();

        byte[] content = "Test content".getBytes(StandardCharsets.UTF_8);
        data.setResponseBody(content);

        assertTrue(data.hasResponseBody());
        assertNotNull(data.getResponseBody());

        try (InputStream is = data.getResponseBody()) {
            byte[] buffer = new byte[content.length];
            int bytesRead = is.read(buffer);
            assertEquals(content.length, bytesRead);
            assertTrue(Arrays.equals(content, buffer));
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void test_responseBodyFile() throws IOException {
        // Test response body with file
        ResponseData data = new ResponseData();

        File tempFile = File.createTempFile("test", ".txt");
        try {
            String content = "File content";
            Files.write(tempFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

            data.setResponseBody(tempFile, false);

            assertTrue(data.hasResponseBody());
            assertNotNull(data.getResponseBody());

            try (InputStream is = data.getResponseBody()) {
                byte[] buffer = new byte[1024];
                int bytesRead = is.read(buffer);
                String result = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                assertEquals(content, result);
            }
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void test_responseBodyFileTemporary() throws IOException {
        // Test response body with temporary file that gets deleted on close
        ResponseData data = new ResponseData();

        File tempFile = File.createTempFile("test_temp", ".txt");
        String content = "Temporary file content";
        Files.write(tempFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

        data.setResponseBody(tempFile, true);

        assertTrue(data.hasResponseBody());

        // Close should delete the temporary file
        data.close();
    }

    @Test
    public void test_hasResponseBodyNoBody() {
        // Test hasResponseBody when no body is set
        ResponseData data = new ResponseData();
        assertFalse(data.hasResponseBody());
    }

    @Test
    public void test_getResponseBodyNull() {
        // Test getResponseBody when no body is set
        ResponseData data = new ResponseData();
        assertNull(data.getResponseBody());
    }

    @Test
    public void test_metaDataOperations() {
        // Test metadata operations
        ResponseData data = new ResponseData();

        // Add metadata
        data.addMetaData("key1", "value1");
        data.addMetaData("key2", 123);
        data.addMetaData("key3", true);

        Map<String, Object> metaDataMap = data.getMetaDataMap();
        assertNotNull(metaDataMap);
        assertEquals(3, metaDataMap.size());

        assertEquals("value1", metaDataMap.get("key1"));
        assertEquals(123, metaDataMap.get("key2"));
        assertEquals(true, metaDataMap.get("key3"));
    }

    @Test
    public void test_metaDataMapModifiable() {
        // Test that metadata map is modifiable
        ResponseData data = new ResponseData();

        data.addMetaData("key1", "value1");

        Map<String, Object> metaDataMap = data.getMetaDataMap();
        metaDataMap.put("key2", "value2");

        assertEquals(2, data.getMetaDataMap().size());
    }

    @Test
    public void test_childUrlOperations() {
        // Test child URL operations
        ResponseData data = new ResponseData();

        RequestData child1 = new RequestData();
        child1.setMethod(Method.GET);
        child1.setUrl("https://example.com/page1");

        RequestData child2 = new RequestData();
        child2.setMethod(Method.GET);
        child2.setUrl("https://example.com/page2");

        // Add child URLs
        data.addChildUrl(child1);
        data.addChildUrl(child2);

        Set<RequestData> childUrlSet = data.getChildUrlSet();
        assertEquals(2, childUrlSet.size());
        assertTrue(childUrlSet.contains(child1));
        assertTrue(childUrlSet.contains(child2));
    }

    @Test
    public void test_removeChildUrl() {
        // Test removing child URL
        ResponseData data = new ResponseData();

        RequestData child1 = new RequestData();
        child1.setMethod(Method.GET);
        child1.setUrl("https://example.com/page1");

        RequestData child2 = new RequestData();
        child2.setMethod(Method.GET);
        child2.setUrl("https://example.com/page2");

        data.addChildUrl(child1);
        data.addChildUrl(child2);
        assertEquals(2, data.getChildUrlSet().size());

        // Remove one child URL
        data.removeChildUrl(child1);
        assertEquals(1, data.getChildUrlSet().size());
        assertFalse(data.getChildUrlSet().contains(child1));
        assertTrue(data.getChildUrlSet().contains(child2));
    }

    @Test
    public void test_childUrlSetNoDuplicates() {
        // Test that child URL set does not allow duplicates
        ResponseData data = new ResponseData();

        RequestData child = new RequestData();
        child.setMethod(Method.GET);
        child.setUrl("https://example.com/page1");

        data.addChildUrl(child);
        data.addChildUrl(child);

        assertEquals(1, data.getChildUrlSet().size());
    }

    @Test
    public void test_getRequestData() {
        // Test getRequestData method
        ResponseData data = new ResponseData();
        data.setUrl("https://example.com/test");
        data.setMethod("POST");

        RequestData requestData = data.getRequestData();
        assertNotNull(requestData);
        assertEquals("https://example.com/test", requestData.getUrl());
        assertEquals(Method.POST, requestData.getMethod());
    }

    @Test
    public void test_closeWithNonTemporaryFile() throws IOException {
        // Test close with non-temporary file
        ResponseData data = new ResponseData();

        File tempFile = File.createTempFile("test_non_temp", ".txt");
        try {
            Files.write(tempFile.toPath(), "content".getBytes(StandardCharsets.UTF_8));

            data.setResponseBody(tempFile, false);

            // Close should not delete non-temporary file
            data.close();

            assertTrue(tempFile.exists());
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void test_closeWithoutFile() throws IOException {
        // Test close without any file
        ResponseData data = new ResponseData();
        data.close(); // Should not throw exception
    }

    @Test
    public void test_complexScenario() {
        // Test complex scenario with multiple operations
        ResponseData data = new ResponseData();

        // Set various properties
        data.setUrl("https://example.com/page");
        data.setHttpStatusCode(200);
        data.setMethod("GET");
        data.setParentUrl("https://example.com");
        data.setSessionId("session123");
        data.setRuleId("rule456");
        data.setMimeType("text/html");
        data.setCharSet("UTF-8");
        data.setContentLength(2048L);
        data.setExecutionTime(500L);
        data.setStatus(Constants.OK_STATUS);
        data.setNoFollow(false);

        byte[] content = "HTML content".getBytes(StandardCharsets.UTF_8);
        data.setResponseBody(content);

        // Add metadata
        data.addMetaData("title", "Test Page");
        data.addMetaData("description", "Test description");

        // Add child URLs
        RequestData child1 = new RequestData();
        child1.setUrl("https://example.com/child1");
        data.addChildUrl(child1);

        // Verify all properties
        assertEquals("https://example.com/page", data.getUrl());
        assertEquals(200, data.getHttpStatusCode());
        assertEquals("GET", data.getMethod());
        assertEquals("https://example.com", data.getParentUrl());
        assertEquals("session123", data.getSessionId());
        assertEquals("rule456", data.getRuleId());
        assertEquals("text/html", data.getMimeType());
        assertEquals("UTF-8", data.getCharSet());
        assertEquals(2048L, data.getContentLength());
        assertEquals(500L, data.getExecutionTime());
        assertEquals(Constants.OK_STATUS, data.getStatus());
        assertFalse(data.isNoFollow());
        assertTrue(data.hasResponseBody());
        assertEquals(2, data.getMetaDataMap().size());
        assertEquals(1, data.getChildUrlSet().size());
    }
}
