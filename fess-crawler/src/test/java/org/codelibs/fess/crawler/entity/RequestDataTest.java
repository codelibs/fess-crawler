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
import org.codelibs.fess.crawler.entity.RequestData.Method;
import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for {@link RequestData}.
 */
public class RequestDataTest extends PlainTestCase {

    @Test
    public void test_defaultConstructor() {
        // Test default constructor
        RequestData data = new RequestData();
        assertNotNull(data);
        assertNull(data.getMethod());
        assertNull(data.getUrl());
        assertEquals(Float.valueOf(1.0f), Float.valueOf(data.getWeight()));
    }

    @Test
    public void test_methodGetterSetter() {
        // Test Method enum getter/setter
        RequestData data = new RequestData();

        data.setMethod(Method.GET);
        assertEquals(Method.GET, data.getMethod());

        data.setMethod(Method.POST);
        assertEquals(Method.POST, data.getMethod());

        data.setMethod(Method.HEAD);
        assertEquals(Method.HEAD, data.getMethod());
    }

    @Test
    public void test_methodStringSetterGet() {
        // Test String-based method setter with GET
        RequestData data = new RequestData();
        data.setMethod(Constants.GET_METHOD);
        assertEquals(Method.GET, data.getMethod());
    }

    @Test
    public void test_methodStringSetterPost() {
        // Test String-based method setter with POST
        RequestData data = new RequestData();
        data.setMethod(Constants.POST_METHOD);
        assertEquals(Method.POST, data.getMethod());
    }

    @Test
    public void test_methodStringSetterHead() {
        // Test String-based method setter with HEAD
        RequestData data = new RequestData();
        data.setMethod(Constants.HEAD_METHOD);
        assertEquals(Method.HEAD, data.getMethod());
    }

    @Test
    public void test_methodStringSetterUnknown() {
        // Test String-based method setter with unknown method defaults to GET
        RequestData data = new RequestData();
        data.setMethod("UNKNOWN");
        assertEquals(Method.GET, data.getMethod());

        data.setMethod("PUT");
        assertEquals(Method.GET, data.getMethod());

        data.setMethod("");
        assertEquals(Method.GET, data.getMethod());
    }

    @Test
    public void test_urlGetterSetter() {
        // Test URL getter/setter
        RequestData data = new RequestData();

        String url = "https://example.com/path";
        data.setUrl(url);
        assertEquals(url, data.getUrl());

        String url2 = "http://test.org/";
        data.setUrl(url2);
        assertEquals(url2, data.getUrl());

        data.setUrl(null);
        assertNull(data.getUrl());
    }

    @Test
    public void test_weightGetterSetter() {
        // Test weight getter/setter
        RequestData data = new RequestData();

        // Default weight should be 1.0
        assertEquals(Float.valueOf(1.0f), Float.valueOf(data.getWeight()));

        data.setWeight(0.5f);
        assertEquals(Float.valueOf(0.5f), Float.valueOf(data.getWeight()));

        data.setWeight(2.5f);
        assertEquals(Float.valueOf(2.5f), Float.valueOf(data.getWeight()));

        data.setWeight(0.0f);
        assertEquals(Float.valueOf(0.0f), Float.valueOf(data.getWeight()));
    }

    @Test
    public void test_equalsAndHashCode() {
        // Test equals and hashCode methods
        RequestData data1 = new RequestData();
        data1.setMethod(Method.GET);
        data1.setUrl("https://example.com");
        data1.setWeight(1.0f);

        RequestData data2 = new RequestData();
        data2.setMethod(Method.GET);
        data2.setUrl("https://example.com");
        data2.setWeight(1.0f);

        // Test equality
        assertTrue(data1.equals(data2));
        assertTrue(data2.equals(data1));
        assertEquals(data1.hashCode(), data2.hashCode());

        // Test reflexivity
        assertTrue(data1.equals(data1));

        // Test with null
        assertFalse(data1.equals(null));

        // Test with different class
        assertFalse(data1.equals("string"));
    }

    @Test
    public void test_equalsWithDifferentMethod() {
        // Test equals with different methods
        RequestData data1 = new RequestData();
        data1.setMethod(Method.GET);
        data1.setUrl("https://example.com");
        data1.setWeight(1.0f);

        RequestData data2 = new RequestData();
        data2.setMethod(Method.POST);
        data2.setUrl("https://example.com");
        data2.setWeight(1.0f);

        assertFalse(data1.equals(data2));
    }

    @Test
    public void test_equalsWithDifferentUrl() {
        // Test equals with different URLs
        RequestData data1 = new RequestData();
        data1.setMethod(Method.GET);
        data1.setUrl("https://example.com");
        data1.setWeight(1.0f);

        RequestData data2 = new RequestData();
        data2.setMethod(Method.GET);
        data2.setUrl("https://different.com");
        data2.setWeight(1.0f);

        assertFalse(data1.equals(data2));
    }

    @Test
    public void test_equalsWithDifferentWeight() {
        // Test equals with different weights
        RequestData data1 = new RequestData();
        data1.setMethod(Method.GET);
        data1.setUrl("https://example.com");
        data1.setWeight(1.0f);

        RequestData data2 = new RequestData();
        data2.setMethod(Method.GET);
        data2.setUrl("https://example.com");
        data2.setWeight(2.0f);

        assertFalse(data1.equals(data2));
    }

    @Test
    public void test_equalsWithNullFields() {
        // Test equals with null fields
        RequestData data1 = new RequestData();
        RequestData data2 = new RequestData();

        // Both with null fields should be equal
        assertTrue(data1.equals(data2));
        assertEquals(data1.hashCode(), data2.hashCode());

        // One with URL, one without
        data1.setUrl("https://example.com");
        assertFalse(data1.equals(data2));
    }

    @Test
    public void test_toString() {
        // Test toString method
        RequestData data = new RequestData();
        data.setMethod(Method.GET);
        data.setUrl("https://example.com");
        data.setWeight(1.5f);

        String result = data.toString();
        assertNotNull(result);
        assertTrue(result.contains("RequestData"));
        assertTrue(result.contains("GET"));
        assertTrue(result.contains("https://example.com"));
        assertTrue(result.contains("1.5"));
    }

    @Test
    public void test_toStringWithNullValues() {
        // Test toString with null values
        RequestData data = new RequestData();

        String result = data.toString();
        assertNotNull(result);
        assertTrue(result.contains("RequestData"));
    }

    @Test
    public void test_methodEnumValues() {
        // Test Method enum values
        Method[] methods = Method.values();
        assertEquals(3, methods.length);

        assertEquals(Method.GET, Method.valueOf("GET"));
        assertEquals(Method.POST, Method.valueOf("POST"));
        assertEquals(Method.HEAD, Method.valueOf("HEAD"));
    }

    @Test
    public void test_complexScenario() {
        // Test a complex scenario with multiple operations
        RequestData data = new RequestData();

        // Set values
        data.setMethod("POST");
        data.setUrl("https://api.example.com/endpoint");
        data.setWeight(0.8f);

        // Verify all values
        assertEquals(Method.POST, data.getMethod());
        assertEquals("https://api.example.com/endpoint", data.getUrl());
        assertEquals(Float.valueOf(0.8f), Float.valueOf(data.getWeight()));

        // Change method
        data.setMethod(Method.HEAD);
        assertEquals(Method.HEAD, data.getMethod());

        // Create identical copy
        RequestData copy = new RequestData();
        copy.setMethod(Method.HEAD);
        copy.setUrl("https://api.example.com/endpoint");
        copy.setWeight(0.8f);

        assertTrue(data.equals(copy));
        assertEquals(data.hashCode(), copy.hashCode());
    }
}
