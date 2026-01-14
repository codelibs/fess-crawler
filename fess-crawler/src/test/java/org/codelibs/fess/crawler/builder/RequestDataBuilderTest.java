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
package org.codelibs.fess.crawler.builder;

import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.RequestData.Method;
import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for {@link RequestDataBuilder}.
 */
public class RequestDataBuilderTest extends PlainTestCase {

    @Test
    public void test_newRequestData() {
        // Test newRequestData factory method
        RequestDataBuilder.RequestDataContext context = RequestDataBuilder.newRequestData();

        assertNotNull(context);
    }

    @Test
    public void test_buildEmptyRequestData() {
        // Test building empty RequestData
        RequestData data = RequestDataBuilder.newRequestData().build();

        assertNotNull(data);
        assertNull(data.getMethod());
        assertNull(data.getUrl());
        assertEquals(Float.valueOf(1.0f), Float.valueOf(data.getWeight()));
    }

    @Test
    public void test_methodWithStringGet() {
        // Test method(String) with GET
        RequestData data = RequestDataBuilder.newRequestData().method(Constants.GET_METHOD).build();

        assertEquals(Method.GET, data.getMethod());
    }

    @Test
    public void test_methodWithStringPost() {
        // Test method(String) with POST
        RequestData data = RequestDataBuilder.newRequestData().method(Constants.POST_METHOD).build();

        assertEquals(Method.POST, data.getMethod());
    }

    @Test
    public void test_methodWithStringHead() {
        // Test method(String) with HEAD
        RequestData data = RequestDataBuilder.newRequestData().method(Constants.HEAD_METHOD).build();

        assertEquals(Method.HEAD, data.getMethod());
    }

    @Test
    public void test_methodWithStringCaseInsensitive() {
        // Test method(String) case-insensitive
        RequestData data1 = RequestDataBuilder.newRequestData().method("get").build();
        assertEquals(Method.GET, data1.getMethod());

        RequestData data2 = RequestDataBuilder.newRequestData().method("GET").build();
        assertEquals(Method.GET, data2.getMethod());

        RequestData data3 = RequestDataBuilder.newRequestData().method("post").build();
        assertEquals(Method.POST, data3.getMethod());

        RequestData data4 = RequestDataBuilder.newRequestData().method("HEAD").build();
        assertEquals(Method.HEAD, data4.getMethod());
    }

    @Test
    public void test_methodWithStringUnknown() {
        // Test method(String) with unknown method defaults to GET
        RequestData data = RequestDataBuilder.newRequestData().method("PUT").build();

        assertEquals(Method.GET, data.getMethod());
    }

    @Test
    public void test_methodWithEnum() {
        // Test method(Method) with enum
        RequestData data1 = RequestDataBuilder.newRequestData().method(Method.GET).build();
        assertEquals(Method.GET, data1.getMethod());

        RequestData data2 = RequestDataBuilder.newRequestData().method(Method.POST).build();
        assertEquals(Method.POST, data2.getMethod());

        RequestData data3 = RequestDataBuilder.newRequestData().method(Method.HEAD).build();
        assertEquals(Method.HEAD, data3.getMethod());
    }

    @Test
    public void test_getMethod() {
        // Test get() convenience method
        RequestData data = RequestDataBuilder.newRequestData().get().build();

        assertEquals(Method.GET, data.getMethod());
    }

    @Test
    public void test_postMethod() {
        // Test post() convenience method
        RequestData data = RequestDataBuilder.newRequestData().post().build();

        assertEquals(Method.POST, data.getMethod());
    }

    @Test
    public void test_headMethod() {
        // Test head() convenience method
        RequestData data = RequestDataBuilder.newRequestData().head().build();

        assertEquals(Method.HEAD, data.getMethod());
    }

    @Test
    public void test_url() {
        // Test url() method
        String url = "https://example.com/path";
        RequestData data = RequestDataBuilder.newRequestData().url(url).build();

        assertEquals(url, data.getUrl());
    }

    @Test
    public void test_weight() {
        // Test weight() method
        RequestData data = RequestDataBuilder.newRequestData().weight(2.5f).build();

        assertEquals(Float.valueOf(2.5f), Float.valueOf(data.getWeight()));
    }

    @Test
    public void test_fluentApiChaining() {
        // Test fluent API method chaining
        RequestData data =
                RequestDataBuilder.newRequestData().method(Constants.GET_METHOD).url("https://example.com/page").weight(1.5f).build();

        assertEquals(Method.GET, data.getMethod());
        assertEquals("https://example.com/page", data.getUrl());
        assertEquals(Float.valueOf(1.5f), Float.valueOf(data.getWeight()));
    }

    @Test
    public void test_fluentApiWithConvenienceMethods() {
        // Test fluent API with convenience methods
        RequestData data1 = RequestDataBuilder.newRequestData().get().url("https://example.com/get").weight(1.0f).build();

        assertEquals(Method.GET, data1.getMethod());
        assertEquals("https://example.com/get", data1.getUrl());

        RequestData data2 = RequestDataBuilder.newRequestData().post().url("https://example.com/post").weight(2.0f).build();

        assertEquals(Method.POST, data2.getMethod());
        assertEquals("https://example.com/post", data2.getUrl());

        RequestData data3 = RequestDataBuilder.newRequestData().head().url("https://example.com/head").build();

        assertEquals(Method.HEAD, data3.getMethod());
        assertEquals("https://example.com/head", data3.getUrl());
    }

    @Test
    public void test_buildMultipleTimes() {
        // Test that context can be used to build multiple times
        RequestDataBuilder.RequestDataContext context = RequestDataBuilder.newRequestData().get().url("https://example.com");

        RequestData data1 = context.build();
        RequestData data2 = context.build();

        // Should return the same underlying object
        assertTrue(data1 == data2);
    }

    @Test
    public void test_modifyAfterBuild() {
        // Test modifying context after build
        RequestDataBuilder.RequestDataContext context = RequestDataBuilder.newRequestData().get().url("https://example.com");

        RequestData data1 = context.build();
        assertEquals("https://example.com", data1.getUrl());

        // Modify context
        context.url("https://modified.com");

        RequestData data2 = context.build();
        assertEquals("https://modified.com", data2.getUrl());

        // data1 should also be modified (same object)
        assertEquals("https://modified.com", data1.getUrl());
    }

    @Test
    public void test_complexScenarioWithGet() {
        // Test complex scenario with GET method
        RequestData data = RequestDataBuilder.newRequestData().get().url("https://api.example.com/users/123").weight(0.8f).build();

        assertEquals(Method.GET, data.getMethod());
        assertEquals("https://api.example.com/users/123", data.getUrl());
        assertEquals(Float.valueOf(0.8f), Float.valueOf(data.getWeight()));
    }

    @Test
    public void test_complexScenarioWithPost() {
        // Test complex scenario with POST method
        RequestData data = RequestDataBuilder.newRequestData().post().url("https://api.example.com/users").weight(1.2f).build();

        assertEquals(Method.POST, data.getMethod());
        assertEquals("https://api.example.com/users", data.getUrl());
        assertEquals(Float.valueOf(1.2f), Float.valueOf(data.getWeight()));
    }

    @Test
    public void test_orderIndependence() {
        // Test that order of method calls doesn't matter
        RequestData data1 = RequestDataBuilder.newRequestData().url("https://example.com").weight(1.5f).get().build();

        RequestData data2 = RequestDataBuilder.newRequestData().get().url("https://example.com").weight(1.5f).build();

        RequestData data3 = RequestDataBuilder.newRequestData().weight(1.5f).get().url("https://example.com").build();

        assertEquals(data1.getMethod(), data2.getMethod());
        assertEquals(data1.getUrl(), data2.getUrl());
        assertEquals(Float.valueOf(data1.getWeight()), Float.valueOf(data2.getWeight()));

        assertEquals(data2.getMethod(), data3.getMethod());
        assertEquals(data2.getUrl(), data3.getUrl());
        assertEquals(Float.valueOf(data2.getWeight()), Float.valueOf(data3.getWeight()));
    }

    @Test
    public void test_overwritingValues() {
        // Test that later values overwrite earlier ones
        RequestData data = RequestDataBuilder.newRequestData()
                .get()
                .post()
                .url("https://first.com")
                .url("https://second.com")
                .weight(1.0f)
                .weight(2.0f)
                .build();

        assertEquals(Method.POST, data.getMethod());
        assertEquals("https://second.com", data.getUrl());
        assertEquals(Float.valueOf(2.0f), Float.valueOf(data.getWeight()));
    }

    @Test
    public void test_minimalUsage() {
        // Test minimal usage (just URL)
        RequestData data = RequestDataBuilder.newRequestData().url("https://example.com").build();

        assertNull(data.getMethod());
        assertEquals("https://example.com", data.getUrl());
        assertEquals(Float.valueOf(1.0f), Float.valueOf(data.getWeight()));
    }

    @Test
    public void test_buildWithNullValues() {
        // Test building with null values
        RequestData data = RequestDataBuilder.newRequestData().url(null).build();

        assertNull(data.getUrl());
    }

    @Test
    public void test_realWorldUsageExample1() {
        // Real-world example: crawling a web page
        RequestData data = RequestDataBuilder.newRequestData().get().url("https://example.com/article/12345").weight(1.0f).build();

        assertNotNull(data);
        assertEquals(Method.GET, data.getMethod());
        assertEquals("https://example.com/article/12345", data.getUrl());
    }

    @Test
    public void test_realWorldUsageExample2() {
        // Real-world example: posting data
        RequestData data = RequestDataBuilder.newRequestData().post().url("https://api.example.com/data").weight(2.0f).build();

        assertNotNull(data);
        assertEquals(Method.POST, data.getMethod());
        assertEquals("https://api.example.com/data", data.getUrl());
        assertEquals(Float.valueOf(2.0f), Float.valueOf(data.getWeight()));
    }

    @Test
    public void test_realWorldUsageExample3() {
        // Real-world example: checking headers
        RequestData data = RequestDataBuilder.newRequestData().head().url("https://example.com/large-file.zip").build();

        assertNotNull(data);
        assertEquals(Method.HEAD, data.getMethod());
        assertEquals("https://example.com/large-file.zip", data.getUrl());
    }
}
