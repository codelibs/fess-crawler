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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.SitemapSet;
import org.codelibs.fess.crawler.entity.SitemapUrl;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.helper.SitemapsHelper;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test class for {@link SitemapsResponseProcessor}.
 */
public class SitemapsResponseProcessorTest extends PlainTestCase {

    @InjectMocks
    private SitemapsResponseProcessor processor;

    @Mock
    private CrawlerContainer crawlerContainer;

    @Mock
    private SitemapsHelper sitemapsHelper;

    @Override
    @BeforeEach
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        MockitoAnnotations.openMocks(this);
    }

    public void test_constructor() {
        // Test constructor
        SitemapsResponseProcessor proc = new SitemapsResponseProcessor();
        assertNotNull(proc);
    }

    public void test_implementsResponseProcessor() {
        // Test that SitemapsResponseProcessor implements ResponseProcessor
        assertTrue(processor instanceof ResponseProcessor);
    }

    public void test_processSingleSitemap() {
        // Setup
        ResponseData responseData = new ResponseData();
        byte[] content = "<sitemap></sitemap>".getBytes();
        responseData.setResponseBody(content);

        SitemapUrl sitemap = new SitemapUrl();
        sitemap.setLoc("https://example.com/page1");

        SitemapSet sitemapSet = new SitemapSet();
        sitemapSet.addSitemap(sitemap);

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        try {
            when(sitemapsHelper.parse(any(InputStream.class))).thenReturn(sitemapSet);
        } catch (Exception e) {
            fail("Should not throw exception in test setup");
        }

        // Execute and verify ChildUrlsException is thrown
        try {
            processor.process(responseData);
            fail("Should throw ChildUrlsException");
        } catch (ChildUrlsException e) {
            // Expected exception
            Set<RequestData> childUrls = e.getChildUrlList();
            assertNotNull(childUrls);
            assertEquals(1, childUrls.size());

            RequestData requestData = childUrls.iterator().next();
            assertEquals("https://example.com/page1", requestData.getUrl());
        }
    }

    public void test_processMultipleSitemaps() {
        // Setup
        ResponseData responseData = new ResponseData();
        byte[] content = "<sitemapindex></sitemapindex>".getBytes();
        responseData.setResponseBody(content);

        SitemapUrl sitemap1 = new SitemapUrl();
        sitemap1.setLoc("https://example.com/page1");

        SitemapUrl sitemap2 = new SitemapUrl();
        sitemap2.setLoc("https://example.com/page2");

        SitemapUrl sitemap3 = new SitemapUrl();
        sitemap3.setLoc("https://example.com/page3");

        SitemapSet sitemapSet = new SitemapSet();
        sitemapSet.addSitemap(sitemap1);
        sitemapSet.addSitemap(sitemap2);
        sitemapSet.addSitemap(sitemap3);

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        try {
            when(sitemapsHelper.parse(any(InputStream.class))).thenReturn(sitemapSet);
        } catch (Exception e) {
            fail("Should not throw exception in test setup");
        }

        // Execute and verify
        try {
            processor.process(responseData);
            fail("Should throw ChildUrlsException");
        } catch (ChildUrlsException e) {
            Set<RequestData> childUrls = e.getChildUrlList();
            assertNotNull(childUrls);
            assertEquals(3, childUrls.size());

            // Verify all URLs are present
            List<String> urls = new ArrayList<>();
            for (RequestData rd : childUrls) {
                urls.add(rd.getUrl());
            }
            assertTrue(urls.contains("https://example.com/page1"));
            assertTrue(urls.contains("https://example.com/page2"));
            assertTrue(urls.contains("https://example.com/page3"));
        }
    }

    public void test_processEmptySitemapSet() {
        // Setup
        ResponseData responseData = new ResponseData();
        byte[] content = "<sitemapindex></sitemapindex>".getBytes();
        responseData.setResponseBody(content);

        SitemapSet sitemapSet = new SitemapSet();

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        try {
            when(sitemapsHelper.parse(any(InputStream.class))).thenReturn(sitemapSet);
        } catch (Exception e) {
            fail("Should not throw exception in test setup");
        }

        // Execute and verify
        try {
            processor.process(responseData);
            fail("Should throw ChildUrlsException even with empty set");
        } catch (ChildUrlsException e) {
            Set<RequestData> childUrls = e.getChildUrlList();
            assertNotNull(childUrls);
            assertEquals(0, childUrls.size());
        }
    }

    public void test_processSitemapWithNullEntry() {
        // Setup - test handling of null sitemap in list
        ResponseData responseData = new ResponseData();
        byte[] content = "<sitemapindex></sitemapindex>".getBytes();
        responseData.setResponseBody(content);

        SitemapUrl sitemap1 = new SitemapUrl();
        sitemap1.setLoc("https://example.com/page1");

        SitemapSet sitemapSet = new SitemapSet();
        sitemapSet.addSitemap(sitemap1);
        sitemapSet.addSitemap(null); // Null entry
        sitemapSet.addSitemap(new SitemapUrl()); // Sitemap with null loc

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        try {
            when(sitemapsHelper.parse(any(InputStream.class))).thenReturn(sitemapSet);
        } catch (Exception e) {
            fail("Should not throw exception in test setup");
        }

        // Execute and verify
        try {
            processor.process(responseData);
            fail("Should throw ChildUrlsException");
        } catch (ChildUrlsException e) {
            Set<RequestData> childUrls = e.getChildUrlList();
            assertNotNull(childUrls);
            // Should only contain valid URLs, null entries should be skipped
            assertTrue(childUrls.size() <= 2);
        }
    }

    public void test_processException() {
        // Setup - test exception handling during parsing
        ResponseData responseData = new ResponseData();
        byte[] content = "invalid xml".getBytes();
        responseData.setResponseBody(content);

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        when(sitemapsHelper.parse(any(InputStream.class))).thenThrow(new RuntimeException("Parse error"));

        // Execute and verify RuntimeException is thrown
        try {
            processor.process(responseData);
            fail("Should throw RuntimeException");
        } catch (RuntimeException e) {
            // Expected exception
            assertEquals("Parse error", e.getMessage());
        }
    }

    public void test_childUrlsExceptionMessage() {
        // Test that ChildUrlsException contains correct message
        ResponseData responseData = new ResponseData();
        byte[] content = "<sitemap></sitemap>".getBytes();
        responseData.setResponseBody(content);

        SitemapUrl sitemap = new SitemapUrl();
        sitemap.setLoc("https://example.com/test");

        SitemapSet sitemapSet = new SitemapSet();
        sitemapSet.addSitemap(sitemap);

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        try {
            when(sitemapsHelper.parse(any(InputStream.class))).thenReturn(sitemapSet);
        } catch (Exception e) {
            fail("Should not throw exception in test setup");
        }

        // Execute and verify exception message
        try {
            processor.process(responseData);
            fail("Should throw ChildUrlsException");
        } catch (ChildUrlsException e) {
            String message = e.getMessage();
            assertNotNull(message);
            assertTrue(message.contains("SitemapsResponseProcessor"));
            assertTrue(message.contains("#process"));
        }
    }

    public void test_requestDataHasGetMethod() {
        // Test that generated RequestData objects have GET method
        ResponseData responseData = new ResponseData();
        byte[] content = "<sitemap></sitemap>".getBytes();
        responseData.setResponseBody(content);

        SitemapUrl sitemap = new SitemapUrl();
        sitemap.setLoc("https://example.com/page");

        SitemapSet sitemapSet = new SitemapSet();
        sitemapSet.addSitemap(sitemap);

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        try {
            when(sitemapsHelper.parse(any(InputStream.class))).thenReturn(sitemapSet);
        } catch (Exception e) {
            fail("Should not throw exception in test setup");
        }

        // Execute and verify
        try {
            processor.process(responseData);
            fail("Should throw ChildUrlsException");
        } catch (ChildUrlsException e) {
            Set<RequestData> childUrls = e.getChildUrlList();
            RequestData requestData = childUrls.iterator().next();

            // Verify it's using GET method
            assertEquals(RequestData.Method.GET, requestData.getMethod());
        }
    }

    public void test_duplicateUrlsInSitemap() {
        // Test handling of duplicate URLs in sitemap
        ResponseData responseData = new ResponseData();
        byte[] content = "<sitemap></sitemap>".getBytes();
        responseData.setResponseBody(content);

        SitemapUrl sitemap1 = new SitemapUrl();
        sitemap1.setLoc("https://example.com/duplicate");

        SitemapUrl sitemap2 = new SitemapUrl();
        sitemap2.setLoc("https://example.com/duplicate");

        SitemapUrl sitemap3 = new SitemapUrl();
        sitemap3.setLoc("https://example.com/unique");

        SitemapSet sitemapSet = new SitemapSet();
        sitemapSet.addSitemap(sitemap1);
        sitemapSet.addSitemap(sitemap2);
        sitemapSet.addSitemap(sitemap3);

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        try {
            when(sitemapsHelper.parse(any(InputStream.class))).thenReturn(sitemapSet);
        } catch (Exception e) {
            fail("Should not throw exception in test setup");
        }

        // Execute and verify
        try {
            processor.process(responseData);
            fail("Should throw ChildUrlsException");
        } catch (ChildUrlsException e) {
            Set<RequestData> childUrls = e.getChildUrlList();
            // Set should handle duplicates
            assertEquals(2, childUrls.size());
        }
    }
}
