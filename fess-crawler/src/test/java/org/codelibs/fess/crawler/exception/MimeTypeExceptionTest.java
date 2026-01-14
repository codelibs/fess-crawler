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

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for MimeTypeException.
 * Tests all constructors and inheritance.
 */
public class MimeTypeExceptionTest extends PlainTestCase {

    /**
     * Test constructor with message only
     */
    @Test
    public void test_constructor_withMessage() {
        String message = "Unknown MIME type: application/x-custom";
        MimeTypeException exception = new MimeTypeException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with null message
     */
    @Test
    public void test_constructor_withNullMessage() {
        MimeTypeException exception = new MimeTypeException((String) null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    /**
     * Test constructor with cause only
     */
    @Test
    public void test_constructor_withCause() {
        IOException cause = new IOException("Cannot detect MIME type");
        MimeTypeException exception = new MimeTypeException(cause);

        assertNotNull(exception);
        assertTrue(cause == exception.getCause());
        assertTrue(exception.getMessage().contains("IOException"));
    }

    /**
     * Test constructor with null cause
     */
    @Test
    public void test_constructor_withNullCause() {
        MimeTypeException exception = new MimeTypeException((Throwable) null);

        assertNotNull(exception);
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with message and cause
     */
    @Test
    public void test_constructor_withMessageAndCause() {
        String message = "MIME type detection failed";
        IOException cause = new IOException("File not readable");
        MimeTypeException exception = new MimeTypeException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(cause == exception.getCause());
    }

    /**
     * Test inheritance
     */
    @Test
    public void test_inheritance() {
        MimeTypeException exception = new MimeTypeException("Test");

        assertTrue(exception instanceof CrawlerSystemException);
        assertTrue(exception instanceof RuntimeException);
    }

    /**
     * Test throwing and catching
     */
    @Test
    public void test_throwAndCatch() {
        try {
            throw new MimeTypeException("Invalid MIME type");
        } catch (MimeTypeException e) {
            assertEquals("Invalid MIME type", e.getMessage());
        }
    }

    /**
     * Test catching as parent type
     */
    @Test
    public void test_catchAsParentType() {
        try {
            throw new MimeTypeException("MIME error");
        } catch (CrawlerSystemException e) {
            assertTrue(e instanceof MimeTypeException);
        }
    }

    /**
     * Test various MIME type error messages
     */
    @Test
    public void test_mimeTypeErrorMessages() {
        MimeTypeException e1 = new MimeTypeException("Unsupported MIME type: video/x-matroska");
        assertTrue(e1.getMessage().contains("video/x-matroska"));

        MimeTypeException e2 = new MimeTypeException("Failed to detect MIME type for file.xyz");
        assertTrue(e2.getMessage().contains("file.xyz"));

        MimeTypeException e3 = new MimeTypeException("Invalid Content-Type header: text/html; charset=");
        assertTrue(e3.getMessage().contains("Content-Type"));
    }

    /**
     * Test with common MIME types
     */
    @Test
    public void test_commonMimeTypes() {
        MimeTypeException e1 = new MimeTypeException("Cannot handle: application/pdf");
        assertTrue(e1.getMessage().contains("application/pdf"));

        MimeTypeException e2 = new MimeTypeException("Blocked MIME type: application/javascript");
        assertTrue(e2.getMessage().contains("application/javascript"));

        MimeTypeException e3 = new MimeTypeException("Invalid image type: image/webp");
        assertTrue(e3.getMessage().contains("image/webp"));
    }

    /**
     * Test exception chaining
     */
    @Test
    public void test_exceptionChaining() {
        Exception root = new IllegalArgumentException("Invalid format");
        IOException middle = new IOException("Detection failed", root);
        MimeTypeException top = new MimeTypeException("MIME type error", middle);

        assertEquals("MIME type error", top.getMessage());
        assertTrue(middle == top.getCause());
        assertTrue(root == top.getCause().getCause());
    }
}
