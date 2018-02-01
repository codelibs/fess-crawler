/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.client.fs;

import java.io.File;
import java.util.Date;
import java.util.Set;

import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class FileSystemClientTest extends PlainTestCase {
    public FileSystemClient fsClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer()
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class)//
                .singleton("fsClient", FileSystemClient.class);
        fsClient = container.getComponent("fsClient");
    }

    public void test_doGet_dir() {
        final File file = ResourceUtil.getResourceAsFile("test");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        try {
            fsClient.doGet("file://" + path);
            fail();
        } catch (final ChildUrlsException e) {
            final Set<RequestData> urlSet = e.getChildUrlList();
            for (final RequestData requestData : urlSet
                    .toArray(new RequestData[urlSet.size()])) {
                String url = requestData.getUrl();
                if (url.indexOf(".svn") < 0) {
                    assertTrue(url.contains("test/dir1")
                            || url.contains("test/dir2")
                            || url.contains("test/text1.txt")
                            || url.contains("test/text2.txt")
                            || url.contains("test/text%203.txt"));
                }
            }
        }

    }

    public void test_doGet_file() throws Exception {
        final File file = ResourceUtil.getResourceAsFile("test/text1.txt");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        final ResponseData responseData = fsClient.doGet("file:" + path);
        assertEquals(200, responseData.getHttpStatusCode());
        assertEquals("UTF-8", responseData.getCharSet());
        assertTrue(6 == responseData.getContentLength()
                || 7 == responseData.getContentLength());
        assertNotNull(responseData.getLastModified());
        assertEquals(Constants.GET_METHOD, responseData.getMethod());
        assertEquals("text/plain", responseData.getMimeType());
        assertTrue(responseData.getUrl().endsWith("test/text1.txt"));
        final String content = new String(InputStreamUtil.getBytes(responseData
                .getResponseBody()), "UTF-8");
        assertEquals("test1", content.trim());
    }

    public void test_doGet_file_with_space() throws Exception {
        final File file = ResourceUtil.getResourceAsFile("test/text 3.txt");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        final ResponseData responseData = fsClient.doGet(path);
        assertEquals(200, responseData.getHttpStatusCode());
        assertEquals("UTF-8", responseData.getCharSet());
        assertEquals(6, responseData.getContentLength());
        assertNotNull(responseData.getLastModified());
        assertEquals(Constants.GET_METHOD, responseData.getMethod());
        assertEquals("text/plain", responseData.getMimeType());
        assertTrue(responseData.getUrl().endsWith("test/text%203.txt"));
        final String content = new String(InputStreamUtil.getBytes(responseData
                .getResponseBody()), "UTF-8");
        assertEquals("test3\n", content);
    }

    public void test_preprocessUri() {
        String value;
        String result;

        value = "file://test.txt";
        result = "file://test.txt";
        assertEquals(result, fsClient.preprocessUri(value));

        value = "file://test test.txt";
        result = "file://test%20test.txt";
        assertEquals(result, fsClient.preprocessUri(value));

        value = "file://テスト.txt";
        result = "file://%E3%83%86%E3%82%B9%E3%83%88.txt";
        assertEquals(result, fsClient.preprocessUri(value));

        value = "test.txt";
        result = "file://test.txt";
        assertEquals(result, fsClient.preprocessUri(value));

        value = "テスト.txt";
        result = "file://%E3%83%86%E3%82%B9%E3%83%88.txt";
        assertEquals(result, fsClient.preprocessUri(value));
    }

    public void test_preprocessUri_null() {
        try {
            fsClient.preprocessUri(null);
            fail();
        } catch (final CrawlerSystemException e) {
        }
        try {
            fsClient.preprocessUri("");
            fail();
        } catch (final CrawlerSystemException e) {
        }
    }

    public void test_doHead_file() throws Exception {
        final File file = ResourceUtil.getResourceAsFile("test/text1.txt");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        final ResponseData responseData = fsClient.doHead("file:" + path);
        assertNotNull(responseData.getLastModified());
        assertTrue(responseData.getLastModified().getTime() < new Date()
                .getTime());
        assertNull(responseData.getResponseBody());
    }

    public void test_doHead_dir() throws Exception {
        final File file = ResourceUtil.getResourceAsFile("test");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        final ResponseData responseData = fsClient.doHead("file:" + path);
        assertNull(responseData);
    }

    public void test_doGet_accessTimeoutTarget() {
        FileSystemClient client = new FileSystemClient() {
            @Override
            protected ResponseData getResponseData(final String uri, final boolean includeContent) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new CrawlingAccessException(e);
                }
                return null;
            }
        };
        client.setAccessTimeout(1);
        try {
            client.doGet("file:/tmp/test.txt");
            fail();
        } catch (CrawlingAccessException e) {
            assertTrue(e.getCause() instanceof InterruptedException);
        }
    }

    public void test_doHead_accessTimeoutTarget() {
        FileSystemClient client = new FileSystemClient() {
            @Override
            protected ResponseData getResponseData(final String uri, final boolean includeContent) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new CrawlingAccessException(e);
                }
                return null;
            }
        };
        client.setAccessTimeout(1);
        try {
            client.doHead("file:/tmp/test.txt");
            fail();
        } catch (CrawlingAccessException e) {
            assertTrue(e.getCause() instanceof InterruptedException);
        }
    }
}
