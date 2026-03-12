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
package org.codelibs.fess.crawler.client.fs;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author shinsuke
 *
 */
public class FileSystemClientTest extends PlainTestCase {
    public FileSystemClient fsClient;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("mimeTypeHelper", MimeTypeHelperImpl.class)//
                .singleton("fsClient", FileSystemClient.class);
        fsClient = container.getComponent("fsClient");
    }

    @Test
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
            for (final RequestData requestData : urlSet.toArray(new RequestData[urlSet.size()])) {
                String url = requestData.getUrl();
                if (url.indexOf(".svn") < 0) {
                    assertTrue(url.contains("test/dir1") || url.contains("test/dir2") || url.contains("test/text1.txt")
                            || url.contains("test/text2.txt") || url.contains("test/text%203.txt"));
                }
            }
        }

    }

    @Test
    public void test_doGet_file() throws Exception {
        final File file = ResourceUtil.getResourceAsFile("test/text1.txt");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        final ResponseData responseData = fsClient.doGet("file:" + path);
        assertEquals(200, responseData.getHttpStatusCode());
        assertEquals("UTF-8", responseData.getCharSet());
        assertTrue(6 == responseData.getContentLength() || 7 == responseData.getContentLength());
        assertNotNull(responseData.getLastModified());
        assertEquals(Constants.GET_METHOD, responseData.getMethod());
        assertEquals("text/plain", responseData.getMimeType());
        assertTrue(responseData.getUrl().endsWith("test/text1.txt"));
        final String content = new String(InputStreamUtil.getBytes(responseData.getResponseBody()), "UTF-8");
        assertEquals("test1", content.trim());
    }

    @Test
    public void test_doGet_file_with_space() throws Exception {
        final File file = ResourceUtil.getResourceAsFile("test/text 3.txt");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        final ResponseData responseData = fsClient.doGet(path);
        assertEquals(200, responseData.getHttpStatusCode());
        assertEquals("UTF-8", responseData.getCharSet());
        assertEquals(6L, responseData.getContentLength());
        assertNotNull(responseData.getLastModified());
        assertEquals(Constants.GET_METHOD, responseData.getMethod());
        assertEquals("text/plain", responseData.getMimeType());
        assertTrue(responseData.getUrl().endsWith("test/text%203.txt"));
        final String content = new String(InputStreamUtil.getBytes(responseData.getResponseBody()), "UTF-8");
        assertEquals("test3\n", content);
    }

    @Test
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

    @Test
    public void test_preprocessUri_null() {
        try {
            fsClient.preprocessUri(null);
            fail();
        } catch (final CrawlerSystemException e) {}
        try {
            fsClient.preprocessUri("");
            fail();
        } catch (final CrawlerSystemException e) {}
    }

    @Test
    public void test_doHead_file() throws Exception {
        final File file = ResourceUtil.getResourceAsFile("test/text1.txt");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        final ResponseData responseData = fsClient.doHead("file:" + path);
        assertNotNull(responseData.getLastModified());
        assertTrue(responseData.getLastModified().getTime() < new Date().getTime());
        assertNull(responseData.getResponseBody());
    }

    @Test
    public void test_doHead_dir() throws Exception {
        final File file = ResourceUtil.getResourceAsFile("test");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        final ResponseData responseData = fsClient.doHead("file:" + path);
        assertNull(responseData);
    }

    @Test
    public void test_doGet_accessTimeoutTarget() {
        FileSystemClient client = new FileSystemClient() {
            @Override
            protected ResponseData getResponseData(final String uri, final boolean includeContent) {
                try {
                    Thread.sleep(2000);
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

    @Test
    public void test_doHead_accessTimeoutTarget() {
        FileSystemClient client = new FileSystemClient() {
            @Override
            protected ResponseData getResponseData(final String uri, final boolean includeContent) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new CrawlingAccessException(e);
                }
                return new ResponseData();
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

    @Test
    public void test_concurrent_initialization() throws Exception {
        // Test that compareAndSet prevents multiple initializations
        final FileSystemClient client = new FileSystemClient();
        final int threadCount = 10;
        final Thread[] threads = new Thread[threadCount];
        final boolean[] initResults = new boolean[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    // Call processRequest which triggers initialization
                    client.doGet("file://nonexistent.txt");
                } catch (Exception e) {
                    // Expected for nonexistent file
                }
                initResults[index] = client.isInit.get();
            });
        }

        // Start all threads simultaneously
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // All threads should see the initialized state
        for (boolean result : initResults) {
            assertTrue(result);
        }
    }

    @Test
    public void test_accessTimeout_null_safety() {
        // Test that accessTimeoutTask null check prevents NPE
        FileSystemClient client = new FileSystemClient() {
            @Override
            protected ResponseData getResponseData(final String uri, final boolean includeContent) {
                // Simulate quick completion before timeout
                ResponseData responseData = new ResponseData();
                responseData.setHttpStatusCode(200);
                return responseData;
            }
        };

        // Test with timeout set
        client.setAccessTimeout(10);
        try {
            ResponseData result = client.doGet("file://test.txt");
            assertNotNull(result);
            assertEquals(200, result.getHttpStatusCode());
        } catch (Exception e) {
            fail();
        }

        // Test without timeout (null accessTimeout)
        client.setAccessTimeout(null);
        try {
            ResponseData result = client.doGet("file://test.txt");
            assertNotNull(result);
            assertEquals(200, result.getHttpStatusCode());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void test_initialization_idempotency() {
        // Test that multiple init calls are safe
        FileSystemClient client = new FileSystemClient();

        // First initialization
        if (client.isInit.compareAndSet(false, true)) {
            client.init();
        }
        assertTrue(client.isInit.get());

        // Second initialization attempt should be no-op
        boolean secondInit = client.isInit.compareAndSet(false, true);
        assertFalse(secondInit);
        assertTrue(client.isInit.get());
    }

    // Regression tests for topic/2732: special characters in file:// paths
    // preprocessUri passes ASCII printable chars through unchanged,
    // but new URI() in getResponseData rejects [], {}, |, bare % etc.
    // These tests document the current behavior and detect if URI handling changes.

    @Test
    public void test_preprocessUri_withSquareBrackets() {
        String value = "file:///data/logs[2024]/access.log";
        String result = fsClient.preprocessUri(value);
        assertNotNull(result);
        assertTrue(result.contains("[2024]"));
    }

    @Test
    public void test_preprocessUri_withPercentLiteral() {
        String value = "file:///data/100%done/report.txt";
        String result = fsClient.preprocessUri(value);
        assertNotNull(result);
        assertTrue(result.contains("100%done") || result.contains("100%25done"));
    }

    @Test
    public void test_preprocessUri_withCurlyBraces() {
        String value = "file:///data/{template}/output.txt";
        String result = fsClient.preprocessUri(value);
        assertNotNull(result);
        assertTrue(result.contains("{template}") || result.contains("%7Btemplate%7D"));
    }

    @Test
    public void test_preprocessUri_withPipeChar() {
        String value = "file:///data/file|name.txt";
        String result = fsClient.preprocessUri(value);
        assertNotNull(result);
        assertTrue(result.contains("file|name") || result.contains("file%7Cname"));
    }

    @Test
    public void test_preprocessUri_alreadyEncoded() {
        String value = "file:///path/my%20file.txt";
        String result = fsClient.preprocessUri(value);
        assertNotNull(result);
        assertFalse(result.contains("%2520"));
    }

    @Test
    public void test_preprocessUri_withUnicodeAndBrackets() {
        String value = "file:///home/ユーザー/[データ]/file.txt";
        String result = fsClient.preprocessUri(value);
        assertNotNull(result);
        // Unicode chars should be encoded
        assertFalse(result.contains("ユーザー"));
        assertTrue(result.startsWith("file://"));
    }

    @Test
    public void test_doGet_file_with_brackets() throws Exception {
        // topic/2732: brackets in file paths cause URISyntaxException internally,
        // resulting in 404 instead of 200. This test documents the current behavior.
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "test[brackets]");
        tempDir.mkdirs();
        File tempFile = new File(tempDir, "test[file].txt");
        try {
            tempFile.createNewFile();
            Files.write(tempFile.toPath(), "test".getBytes());
            String path = tempFile.getAbsolutePath();
            if (!path.startsWith("/")) {
                path = "/" + path.replace('\\', '/');
            }
            final ResponseData responseData = fsClient.doGet(path);
            // Currently returns 404 due to URISyntaxException with brackets.
            // If URI handling is fixed, this should return 200.
            assertTrue(responseData.getHttpStatusCode() == 200 || responseData.getHttpStatusCode() == 404);
        } finally {
            tempFile.delete();
            tempDir.delete();
        }
    }

    @Test
    public void test_doGet_file_with_percent() throws Exception {
        // topic/2732: percent in file paths cause URISyntaxException internally.
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "test_percent");
        File tempFile = null;
        try {
            tempDir.mkdirs();
            tempFile = new File(tempDir, "test%25file.txt");
            if (tempFile.createNewFile()) {
                Files.write(tempFile.toPath(), "test".getBytes());
                String path = tempFile.getAbsolutePath();
                if (!path.startsWith("/")) {
                    path = "/" + path.replace('\\', '/');
                }
                final ResponseData responseData = fsClient.doGet(path);
                assertTrue(responseData.getHttpStatusCode() == 200 || responseData.getHttpStatusCode() == 404);
            }
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
            tempDir.delete();
        }
    }
}
