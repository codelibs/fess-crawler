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
package org.codelibs.fess.net.protocol.storage;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for {@link Handler} and its inner class {@link Handler.StorageURLConnection}.
 * This test covers URL parsing, connection state management, and thread-safety.
 */
public class HandlerTest extends PlainTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        new StandardCrawlerContainer();
    }

    /**
     * Test that the storage protocol is correctly recognized.
     */
    public void test_protocol() throws Exception {
        URL url = new URL("storage://bucket/object");
        assertEquals("storage", url.getProtocol());
    }

    /**
     * Test URL parsing with bucket and object path.
     */
    public void test_urlParsing_bucketAndObject() throws Exception {
        URL url = new URL("storage://mybucket/path/to/object.txt");
        Handler handler = new Handler();
        URLConnection conn = handler.openConnection(url);

        assertTrue(conn instanceof Handler.StorageURLConnection);
        Handler.StorageURLConnection storageConn = (Handler.StorageURLConnection) conn;

        // Use reflection to access private fields for testing
        assertEquals("mybucket", getField(storageConn, "bucketName"));
        assertEquals("path/to/object.txt", getField(storageConn, "objectName"));
    }

    /**
     * Test URL parsing with bucket only (no object path).
     */
    public void test_urlParsing_bucketOnly() throws Exception {
        URL url = new URL("storage://mybucket");
        Handler handler = new Handler();
        URLConnection conn = handler.openConnection(url);

        Handler.StorageURLConnection storageConn = (Handler.StorageURLConnection) conn;

        assertEquals("mybucket", getField(storageConn, "bucketName"));
        assertEquals("", getField(storageConn, "objectName"));
    }

    /**
     * Test URL parsing with bucket and root path.
     */
    public void test_urlParsing_bucketWithSlash() throws Exception {
        URL url = new URL("storage://mybucket/");
        Handler handler = new Handler();
        URLConnection conn = handler.openConnection(url);

        Handler.StorageURLConnection storageConn = (Handler.StorageURLConnection) conn;

        assertEquals("mybucket", getField(storageConn, "bucketName"));
        assertEquals("", getField(storageConn, "objectName"));
    }

    /**
     * Test URL parsing with complex object path.
     */
    public void test_urlParsing_complexPath() throws Exception {
        URL url = new URL("storage://mybucket/dir1/dir2/file.pdf");
        Handler handler = new Handler();
        URLConnection conn = handler.openConnection(url);

        Handler.StorageURLConnection storageConn = (Handler.StorageURLConnection) conn;

        assertEquals("mybucket", getField(storageConn, "bucketName"));
        assertEquals("dir1/dir2/file.pdf", getField(storageConn, "objectName"));
    }

    /**
     * Test URL parsing with special characters in object name.
     */
    public void test_urlParsing_specialCharacters() throws Exception {
        URL url = new URL("storage://mybucket/path/file%20with%20spaces.txt");
        Handler handler = new Handler();
        URLConnection conn = handler.openConnection(url);

        Handler.StorageURLConnection storageConn = (Handler.StorageURLConnection) conn;

        assertEquals("mybucket", getField(storageConn, "bucketName"));
        // URL decoding is handled by URL class
        assertEquals("path/file with spaces.txt", getField(storageConn, "objectName"));
    }

    /**
     * Test URL parsing with hyphenated bucket name.
     */
    public void test_urlParsing_hyphenatedBucket() throws Exception {
        URL url = new URL("storage://my-bucket-name/object.txt");
        Handler handler = new Handler();
        URLConnection conn = handler.openConnection(url);

        Handler.StorageURLConnection storageConn = (Handler.StorageURLConnection) conn;

        assertEquals("my-bucket-name", getField(storageConn, "bucketName"));
        assertEquals("object.txt", getField(storageConn, "objectName"));
    }

    /**
     * Test URL parsing with dots in bucket name.
     */
    public void test_urlParsing_dotsInBucket() throws Exception {
        URL url = new URL("storage://my.bucket.name/object.txt");
        Handler handler = new Handler();
        URLConnection conn = handler.openConnection(url);

        Handler.StorageURLConnection storageConn = (Handler.StorageURLConnection) conn;

        assertEquals("my.bucket.name", getField(storageConn, "bucketName"));
        assertEquals("object.txt", getField(storageConn, "objectName"));
    }

    /**
     * Test that MinIO client is initially null before connection.
     */
    public void test_connectionState_initiallyNotConnected() throws Exception {
        URL url = new URL("storage://mybucket/object.txt");
        Handler handler = new Handler();
        Handler.StorageURLConnection conn = (Handler.StorageURLConnection) handler.openConnection(url);

        // Verify MinIO client is not initialized before connect() is called
        assertNull(getField(conn, "minioClient"));
    }

    /**
     * Test that connect() method fails when STORAGE_ENDPOINT is not set.
     */
    public void test_connect_failsWithoutEndpoint() throws Exception {
        URL url = new URL("storage://mybucket/object.txt");
        Handler handler = new Handler();
        Handler.StorageURLConnection conn = (Handler.StorageURLConnection) handler.openConnection(url);

        // Ensure environment variables are not set (in real scenario)
        // In this test, we expect IOException when endpoint is blank
        try {
            conn.connect();
            fail("Should throw IOException when endpoint is not set");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("endpoint is blank"));
        }
    }

    /**
     * Test thread-safety of connect() method.
     * Multiple threads should be able to call connect() concurrently without issues.
     */
    public void test_connect_threadSafety() throws Exception {
        URL url = new URL("storage://mybucket/object.txt");
        Handler handler = new Handler();
        Handler.StorageURLConnection conn = (Handler.StorageURLConnection) handler.openConnection(url);

        final int threadCount = 10;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(threadCount);
        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger failureCount = new AtomicInteger(0);
        final List<Exception> exceptions = new ArrayList<>();

        // Create multiple threads that all try to connect simultaneously
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    startLatch.await(); // Wait for signal to start
                    conn.connect();
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    synchronized (exceptions) {
                        exceptions.add(e);
                    }
                    failureCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            }).start();
        }

        // Signal all threads to start at once
        startLatch.countDown();

        // Wait for all threads to complete
        assertTrue("Threads did not complete in time", doneLatch.await(5, TimeUnit.SECONDS));

        // All threads should fail with the same IOException (endpoint is blank)
        // But importantly, there should be no race condition errors
        assertEquals(threadCount, failureCount.get());
        assertEquals(0, successCount.get());

        // All exceptions should be about endpoint being blank
        for (Exception e : exceptions) {
            assertTrue(e instanceof IOException);
            assertTrue(e.getMessage().contains("endpoint is blank"));
        }
    }

    /**
     * Test that connect() is idempotent - calling it multiple times should be safe.
     */
    public void test_connect_idempotent() throws Exception {
        URL url = new URL("storage://mybucket/object.txt");
        Handler handler = new Handler();
        Handler.StorageURLConnection conn = (Handler.StorageURLConnection) handler.openConnection(url);

        // First call should fail (no endpoint set)
        try {
            conn.connect();
            fail("Should throw IOException");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("endpoint is blank"));
        }

        // Second call should also fail with the same error
        try {
            conn.connect();
            fail("Should throw IOException");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("endpoint is blank"));
        }
    }

    /**
     * Test URL with empty path returns empty object name.
     */
    public void test_urlParsing_emptyPath() throws Exception {
        URL url = new URL("storage://mybucket");
        Handler handler = new Handler();
        Handler.StorageURLConnection conn = (Handler.StorageURLConnection) handler.openConnection(url);

        assertEquals("mybucket", getField(conn, "bucketName"));
        assertEquals("", getField(conn, "objectName"));
    }

    /**
     * Test URL with multiple consecutive slashes.
     */
    public void test_urlParsing_multipleSlashes() throws Exception {
        URL url = new URL("storage://mybucket//path//to//object.txt");
        Handler handler = new Handler();
        Handler.StorageURLConnection conn = (Handler.StorageURLConnection) handler.openConnection(url);

        assertEquals("mybucket", getField(conn, "bucketName"));
        // URL class normalizes the path
        String objectName = (String) getField(conn, "objectName");
        assertTrue(objectName.contains("path") && objectName.contains("object.txt"));
    }

    /**
     * Test that getInputStream() attempts to connect if not connected.
     */
    public void test_getInputStream_autoConnect() throws Exception {
        URL url = new URL("storage://mybucket/object.txt");
        Handler handler = new Handler();
        Handler.StorageURLConnection conn = (Handler.StorageURLConnection) handler.openConnection(url);

        // getInputStream should try to auto-connect (and fail due to missing endpoint)
        try {
            conn.getInputStream();
            fail("Should throw IOException");
        } catch (IOException e) {
            // Expected - either "endpoint is blank" or connection failure
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Test that getContentLengthLong() attempts to connect if not connected.
     */
    public void test_getContentLengthLong_autoConnect() throws Exception {
        URL url = new URL("storage://mybucket/object.txt");
        Handler handler = new Handler();
        Handler.StorageURLConnection conn = (Handler.StorageURLConnection) handler.openConnection(url);

        // Should return -1 when auto-connect fails due to missing endpoint
        long length = conn.getContentLengthLong();
        assertEquals(-1, length);
    }

    /**
     * Test that getContentType() attempts to connect if not connected.
     */
    public void test_getContentType_autoConnect() throws Exception {
        URL url = new URL("storage://mybucket/object.txt");
        Handler handler = new Handler();
        Handler.StorageURLConnection conn = (Handler.StorageURLConnection) handler.openConnection(url);

        // Should return null when auto-connect fails due to missing endpoint
        String contentType = conn.getContentType();
        assertNull(contentType);
    }

    /**
     * Test that getLastModified() attempts to connect if not connected.
     */
    public void test_getLastModified_autoConnect() throws Exception {
        URL url = new URL("storage://mybucket/object.txt");
        Handler handler = new Handler();
        Handler.StorageURLConnection conn = (Handler.StorageURLConnection) handler.openConnection(url);

        // Should return 0 when auto-connect fails due to missing endpoint
        long lastModified = conn.getLastModified();
        assertEquals(0, lastModified);
    }

    /**
     * Test that getDate() delegates to getLastModified().
     */
    public void test_getDate_delegatesToGetLastModified() throws Exception {
        URL url = new URL("storage://mybucket/object.txt");
        Handler handler = new Handler();
        Handler.StorageURLConnection conn = (Handler.StorageURLConnection) handler.openConnection(url);

        // Both should return the same value
        long date = conn.getDate();
        long lastModified = conn.getLastModified();
        assertEquals(lastModified, date);
    }

    /**
     * Helper method to get private field value using reflection.
     * Searches the class hierarchy to find the field.
     */
    private Object getField(Object obj, String fieldName) throws Exception {
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            try {
                java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (NoSuchFieldException e) {
                // Try parent class
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + fieldName + " not found in class hierarchy");
    }
}
