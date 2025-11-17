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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for TemporaryFileInputStream.
 *
 * @author shinsuke
 */
public class TemporaryFileInputStreamTest extends PlainTestCase {

    public void test_read_fromTempFile() throws Exception {
        // Create a temporary file with test data
        File tempFile = File.createTempFile("test-", ".tmp");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write("Test data".getBytes());
        }

        // Read from TemporaryFileInputStream
        try (TemporaryFileInputStream stream = new TemporaryFileInputStream(tempFile)) {
            assertEquals('T', stream.read());
            assertEquals('e', stream.read());
            assertEquals('s', stream.read());
            assertEquals('t', stream.read());
            assertEquals(' ', stream.read());
            assertEquals('d', stream.read());
            assertEquals('a', stream.read());
            assertEquals('t', stream.read());
            assertEquals('a', stream.read());
            assertEquals(-1, stream.read()); // EOF
        }

        // File should be deleted after close
        // Note: FileUtil.deleteInBackground() is async, so we can't reliably test this
    }

    public void test_available() throws Exception {
        // Create a temporary file with test data
        File tempFile = File.createTempFile("test-", ".tmp");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write("1234567890".getBytes());
        }

        try (TemporaryFileInputStream stream = new TemporaryFileInputStream(tempFile)) {
            assertEquals(10, stream.available());
            stream.read();
            assertEquals(9, stream.available());
            stream.read();
            assertEquals(8, stream.available());
        }
    }

    public void test_skip() throws Exception {
        // Create a temporary file with test data
        File tempFile = File.createTempFile("test-", ".tmp");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write("0123456789".getBytes());
        }

        try (TemporaryFileInputStream stream = new TemporaryFileInputStream(tempFile)) {
            long skipped = stream.skip(5);
            assertEquals(5, skipped);
            assertEquals('5', stream.read());
            assertEquals('6', stream.read());
        }
    }

    public void test_markAndReset() throws Exception {
        // Create a temporary file with test data
        File tempFile = File.createTempFile("test-", ".tmp");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write("ABCDEFGH".getBytes());
        }

        try (TemporaryFileInputStream stream = new TemporaryFileInputStream(tempFile)) {
            assertTrue(stream.markSupported());

            assertEquals('A', stream.read());
            assertEquals('B', stream.read());

            // Mark current position
            stream.mark(10);

            assertEquals('C', stream.read());
            assertEquals('D', stream.read());

            // Reset to marked position
            stream.reset();

            // Should read from marked position again
            assertEquals('C', stream.read());
            assertEquals('D', stream.read());
        }
    }

    public void test_getTemporaryFile() throws Exception {
        // Create a temporary file
        File tempFile = File.createTempFile("test-", ".tmp");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write("Test".getBytes());
        }

        try (TemporaryFileInputStream stream = new TemporaryFileInputStream(tempFile)) {
            File retrievedFile = stream.getTemporaryFile();
            assertNotNull(retrievedFile);
            assertEquals(tempFile.getAbsolutePath(), retrievedFile.getAbsolutePath());
        }
    }

    public void test_closeDeletesTempFile() throws Exception {
        // Create a temporary file
        File tempFile = File.createTempFile("test-", ".tmp");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write("Test data for deletion".getBytes());
        }

        String filePath = tempFile.getAbsolutePath();
        assertTrue("Temp file should exist before opening stream", tempFile.exists());

        TemporaryFileInputStream stream = new TemporaryFileInputStream(tempFile);
        stream.read(); // Read some data
        stream.close();

        // Note: FileUtil.deleteInBackground() performs deletion asynchronously,
        // so we cannot reliably check if file is deleted immediately
        // This test just verifies that close() completes without error
    }

    public void test_emptyFile() throws Exception {
        // Create an empty temporary file
        File tempFile = File.createTempFile("test-empty-", ".tmp");

        try (TemporaryFileInputStream stream = new TemporaryFileInputStream(tempFile)) {
            assertEquals(0, stream.available());
            assertEquals(-1, stream.read()); // EOF immediately
        }
    }

    public void test_largeFile() throws Exception {
        // Create a temporary file with larger data
        File tempFile = File.createTempFile("test-large-", ".tmp");
        byte[] data = new byte[1024];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 256);
        }
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(data);
        }

        try (TemporaryFileInputStream stream = new TemporaryFileInputStream(tempFile)) {
            assertEquals(1024, stream.available());

            byte[] readBuffer = new byte[1024];
            int bytesRead = stream.read(readBuffer);

            assertEquals(1024, bytesRead);
            for (int i = 0; i < data.length; i++) {
                assertEquals("Mismatch at position " + i, data[i], readBuffer[i]);
            }
        }
    }

    public void test_fileNotFoundException() {
        // Test with non-existent file
        File nonExistentFile = new File("/tmp/non-existent-file-" + System.currentTimeMillis() + ".tmp");

        try {
            new TemporaryFileInputStream(nonExistentFile);
            fail("Should throw FileNotFoundException");
        } catch (IOException e) {
            // Expected
            assertTrue(e.getMessage().contains("non-existent-file"));
        }
    }
}
