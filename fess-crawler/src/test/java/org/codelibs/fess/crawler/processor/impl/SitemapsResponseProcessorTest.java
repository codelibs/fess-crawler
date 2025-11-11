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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.Sitemap;
import org.codelibs.fess.crawler.entity.SitemapSet;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.helper.SitemapsHelper;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.dbflute.utflute.core.PlainTestCase;
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
    protected void setUp() throws Exception {
        super.setUp();
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

        Sitemap sitemap = new Sitemap();
        sitemap.setLoc("https://example.com/page1");

        List<Sitemap> sitemapList = new ArrayList<>();
        sitemapList.add(sitemap);

        SitemapSet sitemapSet = new SitemapSet();
        sitemapSet.setSitemaps(sitemapList);

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        try {
            when(sitemapsHelper.parse(any(InputStream.class))).thenReturn(sitemapSet);
        } catch (IOException e) {
            fail("Should not throw IOException in test setup");
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

        Sitemap sitemap1 = new Sitemap();
        sitemap1.setLoc("https://example.com/page1");

        Sitemap sitemap2 = new Sitemap();
        sitemap2.setLoc("https://example.com/page2");

        Sitemap sitemap3 = new Sitemap();
        sitemap3.setLoc("https://example.com/page3");

        List<Sitemap> sitemapList = new ArrayList<>();
        sitemapList.add(sitemap1);
        sitemapList.add(sitemap2);
        sitemapList.add(sitemap3);

        SitemapSet sitemapSet = new SitemapSet();
        sitemapSet.setSitemaps(sitemapList);

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        try {
            when(sitemapsHelper.parse(any(InputStream.class))).thenReturn(sitemapSet);
        } catch (IOException e) {
            fail("Should not throw IOException in test setup");
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
        sitemapSet.setSitemaps(new ArrayList<>());

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        try {
            when(sitemapsHelper.parse(any(InputStream.class))).thenReturn(sitemapSet);
        } catch (IOException e) {
            fail("Should not throw IOException in test setup");
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

        Sitemap sitemap1 = new Sitemap();
        sitemap1.setLoc("https://example.com/page1");

        List<Sitemap> sitemapList = new ArrayList<>();
        sitemapList.add(sitemap1);
        sitemapList.add(null); // Null entry
        sitemapList.add(new Sitemap()); // Sitemap with null loc

        SitemapSet sitemapSet = new SitemapSet();
        sitemapSet.setSitemaps(sitemapList);

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        try {
            when(sitemapsHelper.parse(any(InputStream.class))).thenReturn(sitemapSet);
        } catch (IOException e) {
            fail("Should not throw IOException in test setup");
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

    public void test_processIOException() {
        // Setup - test IOException handling
        ResponseData responseData = new ResponseData();
        byte[] content = "invalid xml".getBytes();
        responseData.setResponseBody(content);

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        try {
            when(sitemapsHelper.parse(any(InputStream.class))).thenThrow(new IOException("Parse error"));
        } catch (IOException e) {
            fail("Should not throw IOException in test setup");
        }

        // Execute and verify IORuntimeException is thrown
        try {
            processor.process(responseData);
            fail("Should throw IORuntimeException");
        } catch (IORuntimeException e) {
            // Expected exception
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof IOException);
            assertEquals("Parse error", e.getCause().getMessage());
        }
    }

    public void test_childUrlsExceptionMessage() {
        // Test that ChildUrlsException contains correct message
        ResponseData responseData = new ResponseData();
        byte[] content = "<sitemap></sitemap>".getBytes();
        responseData.setResponseBody(content);

        Sitemap sitemap = new Sitemap();
        sitemap.setLoc("https://example.com/test");

        List<Sitemap> sitemapList = new ArrayList<>();
        sitemapList.add(sitemap);

        SitemapSet sitemapSet = new SitemapSet();
        sitemapSet.setSitemaps(sitemapList);

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        try {
            when(sitemapsHelper.parse(any(InputStream.class))).thenReturn(sitemapSet);
        } catch (IOException e) {
            fail("Should not throw IOException in test setup");
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

        Sitemap sitemap = new Sitemap();
        sitemap.setLoc("https://example.com/page");

        List<Sitemap> sitemapList = new ArrayList<>();
        sitemapList.add(sitemap);

        SitemapSet sitemapSet = new SitemapSet();
        sitemapSet.setSitemaps(sitemapList);

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        try {
            when(sitemapsHelper.parse(any(InputStream.class))).thenReturn(sitemapSet);
        } catch (IOException e) {
            fail("Should not throw IOException in test setup");
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

        Sitemap sitemap1 = new Sitemap();
        sitemap1.setLoc("https://example.com/duplicate");

        Sitemap sitemap2 = new Sitemap();
        sitemap2.setLoc("https://example.com/duplicate");

        Sitemap sitemap3 = new Sitemap();
        sitemap3.setLoc("https://example.com/unique");

        List<Sitemap> sitemapList = new ArrayList<>();
        sitemapList.add(sitemap1);
        sitemapList.add(sitemap2);
        sitemapList.add(sitemap3);

        SitemapSet sitemapSet = new SitemapSet();
        sitemapSet.setSitemaps(sitemapList);

        when(crawlerContainer.getComponent("sitemapsHelper")).thenReturn(sitemapsHelper);
        try {
            when(sitemapsHelper.parse(any(InputStream.class))).thenReturn(sitemapSet);
        } catch (IOException e) {
            fail("Should not throw IOException in test setup");
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
