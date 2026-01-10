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
package org.codelibs.fess.crawler.exception;

import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for UnsupportedExtractException.
 * Tests constructor and performance optimizations.
 */
public class UnsupportedExtractExceptionTest extends PlainTestCase {

    /**
     * Test constructor with message
     */
    public void test_constructor_withMessage() {
        String message = "Unsupported MIME type: application/octet-stream";
        UnsupportedExtractException exception = new UnsupportedExtractException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with null message
     */
    public void test_constructor_withNullMessage() {
        UnsupportedExtractException exception = new UnsupportedExtractException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    /**
     * Test constructor with empty message
     */
    public void test_constructor_withEmptyMessage() {
        UnsupportedExtractException exception = new UnsupportedExtractException("");

        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }

    /**
     * Test stack trace is disabled (performance optimization)
     */
    public void test_stackTraceDisabled() {
        UnsupportedExtractException exception = new UnsupportedExtractException("Test");

        // Stack trace should be empty
        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertEquals(0, stackTrace.length);
    }

    /**
     * Test suppression is disabled (performance optimization)
     */
    public void test_suppressionDisabled() {
        UnsupportedExtractException exception = new UnsupportedExtractException("Test");

        // Add suppressed exception
        exception.addSuppressed(new RuntimeException("Suppressed"));

        // Should not be added
        Throwable[] suppressed = exception.getSuppressed();
        assertEquals(0, suppressed.length);
    }

    /**
     * Test inheritance
     */
    public void test_inheritance() {
        UnsupportedExtractException exception = new UnsupportedExtractException("Test");

        assertTrue(exception instanceof ExtractException);
        assertTrue(exception instanceof CrawlerSystemException);
        assertTrue(exception instanceof RuntimeException);
    }

    /**
     * Test throwing and catching
     */
    public void test_throwAndCatch() {
        try {
            throw new UnsupportedExtractException("Cannot extract: unknown format");
        } catch (UnsupportedExtractException e) {
            assertEquals("Cannot extract: unknown format", e.getMessage());
        }
    }

    /**
     * Test catching as parent types
     */
    public void test_catchAsParentTypes() {
        try {
            throw new UnsupportedExtractException("Unsupported");
        } catch (ExtractException e) {
            assertTrue(e instanceof UnsupportedExtractException);
        }

        try {
            throw new UnsupportedExtractException("Unsupported");
        } catch (CrawlerSystemException e) {
            assertTrue(e instanceof UnsupportedExtractException);
        }
    }

    /**
     * Test with MIME type messages
     */
    public void test_mimeTypeMessages() {
        UnsupportedExtractException e1 = new UnsupportedExtractException("Unsupported MIME type: video/mp4");
        assertTrue(e1.getMessage().contains("video/mp4"));

        UnsupportedExtractException e2 = new UnsupportedExtractException("No extractor for: application/x-binary");
        assertTrue(e2.getMessage().contains("application/x-binary"));

        UnsupportedExtractException e3 = new UnsupportedExtractException("Cannot handle: image/raw");
        assertTrue(e3.getMessage().contains("image/raw"));
    }

    /**
     * Test performance - creation should be fast without stack trace
     */
    public void test_creationPerformance() {
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            new UnsupportedExtractException("Test " + i);
        }
        long elapsed = System.nanoTime() - start;

        // Should complete quickly (less than 1 second)
        assertTrue(elapsed < 1_000_000_000L);
    }
}
