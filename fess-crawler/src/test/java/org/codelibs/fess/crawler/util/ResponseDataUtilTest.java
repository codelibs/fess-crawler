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
package org.codelibs.fess.crawler.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for ResponseDataUtil.
 *
 * @author shinsuke
 */
public class ResponseDataUtilTest extends PlainTestCase {

    @Test
    public void test_createResponseBodyFile_basic() throws Exception {
        // Create a mock ResponseData
        ResponseData responseData = new ResponseData() {
            @Override
            public InputStream getResponseBody() {
                return new ByteArrayInputStream("Test response body".getBytes());
            }

            @Override
            public String getUrl() {
                return "http://example.com/test";
            }
        };

        File tempFile = ResponseDataUtil.createResponseBodyFile(responseData);

        assertNotNull(tempFile);
        assertTrue(tempFile.exists());

        // Read and verify content
        try (FileInputStream fis = new FileInputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead = fis.read(buffer);
            String content = new String(buffer, 0, bytesRead);
            assertEquals("Test response body", content);
        } finally {
            // Clean up
            tempFile.delete();
        }
    }

    @Test
    public void test_createResponseBodyFile_emptyBody() throws Exception {
        // Create a mock ResponseData with empty body
        ResponseData responseData = new ResponseData() {
            @Override
            public InputStream getResponseBody() {
                return new ByteArrayInputStream(new byte[0]);
            }

            @Override
            public String getUrl() {
                return "http://example.com/empty";
            }
        };

        File tempFile = ResponseDataUtil.createResponseBodyFile(responseData);

        assertNotNull(tempFile);
        assertTrue(tempFile.exists());
        assertEquals(0L, tempFile.length());

        // Clean up
        tempFile.delete();
    }

    @Test
    public void test_createResponseBodyFile_largeBody() throws Exception {
        // Create a large response body
        byte[] largeData = new byte[10000];
        for (int i = 0; i < largeData.length; i++) {
            largeData[i] = (byte) (i % 256);
        }

        ResponseData responseData = new ResponseData() {
            @Override
            public InputStream getResponseBody() {
                return new ByteArrayInputStream(largeData);
            }

            @Override
            public String getUrl() {
                return "http://example.com/large";
            }
        };

        File tempFile = ResponseDataUtil.createResponseBodyFile(responseData);

        assertNotNull(tempFile);
        assertTrue(tempFile.exists());
        assertEquals(10000L, tempFile.length());

        // Verify content
        try (FileInputStream fis = new FileInputStream(tempFile)) {
            byte[] buffer = new byte[10000];
            int bytesRead = fis.read(buffer);
            assertEquals(10000, bytesRead);
            for (int i = 0; i < largeData.length; i++) {
                assertEquals(largeData[i], buffer[i]);
            }
        } finally {
            // Clean up
            tempFile.delete();
        }
    }

    @Test
    public void test_createResponseBodyFile_filePermissions() throws Exception {
        // Test that file permissions are set correctly
        ResponseData responseData = new ResponseData() {
            @Override
            public InputStream getResponseBody() {
                return new ByteArrayInputStream("Test permissions".getBytes());
            }

            @Override
            public String getUrl() {
                return "http://example.com/permissions";
            }
        };

        File tempFile = ResponseDataUtil.createResponseBodyFile(responseData);

        assertNotNull(tempFile);
        assertTrue(tempFile.exists());

        // Verify file permissions (readable and writable by owner)
        assertTrue(tempFile.canRead());
        assertTrue(tempFile.canWrite());

        // Clean up
        tempFile.delete();
    }

    @Test
    public void test_createResponseBodyFile_exceptionOnRead() {
        // Create a mock ResponseData that throws exception
        ResponseData responseData = new ResponseData() {
            @Override
            public InputStream getResponseBody() {
                return new InputStream() {
                    @Override
                    public int read() throws IOException {
                        throw new IOException("Simulated read error");
                    }
                };
            }

            @Override
            public String getUrl() {
                return "http://example.com/error";
            }
        };

        try {
            ResponseDataUtil.createResponseBodyFile(responseData);
            fail();
        } catch (CrawlingAccessException e) {
            // Expected
            assertTrue(e.getMessage().contains("Could not read a response body"));
            assertTrue(e.getMessage().contains("http://example.com/error"));
            assertNotNull(e.getCause());
        }
    }

    @Test
    public void test_createResponseBodyFile_binaryContent() throws Exception {
        // Test with binary content
        byte[] binaryData = new byte[] { 0x00, 0x01, 0x02, (byte) 0xFF, (byte) 0xFE, (byte) 0xFD };

        ResponseData responseData = new ResponseData() {
            @Override
            public InputStream getResponseBody() {
                return new ByteArrayInputStream(binaryData);
            }

            @Override
            public String getUrl() {
                return "http://example.com/binary";
            }
        };

        File tempFile = ResponseDataUtil.createResponseBodyFile(responseData);

        assertNotNull(tempFile);
        assertTrue(tempFile.exists());
        assertEquals(6L, tempFile.length());

        // Verify binary content
        try (FileInputStream fis = new FileInputStream(tempFile)) {
            byte[] buffer = new byte[6];
            int bytesRead = fis.read(buffer);
            assertEquals(6, bytesRead);
            for (int i = 0; i < binaryData.length; i++) {
                assertEquals(binaryData[i], buffer[i]);
            }
        } finally {
            // Clean up
            tempFile.delete();
        }
    }

    @Test
    public void test_createResponseBodyFile_tempFilePrefix() throws Exception {
        // Test that temp file has correct prefix
        ResponseData responseData = new ResponseData() {
            @Override
            public InputStream getResponseBody() {
                return new ByteArrayInputStream("Test".getBytes());
            }

            @Override
            public String getUrl() {
                return "http://example.com/prefix";
            }
        };

        File tempFile = ResponseDataUtil.createResponseBodyFile(responseData);

        assertNotNull(tempFile);
        assertTrue(tempFile.getName().startsWith("crawler-"));
        assertTrue(tempFile.getName().endsWith(".tmp"));

        // Clean up
        tempFile.delete();
    }
}
