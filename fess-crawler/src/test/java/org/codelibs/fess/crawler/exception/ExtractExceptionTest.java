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
import java.lang.reflect.Constructor;

import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for ExtractException.
 * Tests all constructors including protected constructor.
 */
public class ExtractExceptionTest extends PlainTestCase {

    /**
     * Test constructor with message only
     */
    public void test_constructor_withMessage() {
        String message = "Failed to extract content";
        ExtractException exception = new ExtractException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with null message
     */
    public void test_constructor_withNullMessage() {
        ExtractException exception = new ExtractException((String) null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    /**
     * Test constructor with cause only
     */
    public void test_constructor_withCause() {
        IOException cause = new IOException("IO error");
        ExtractException exception = new ExtractException(cause);

        assertNotNull(exception);
        assertSame(cause, exception.getCause());
        assertEquals("java.io.IOException: IO error", exception.getMessage());
    }

    /**
     * Test constructor with null cause
     */
    public void test_constructor_withNullCause() {
        ExtractException exception = new ExtractException((Throwable) null);

        assertNotNull(exception);
        assertNull(exception.getCause());
        assertNull(exception.getMessage());
    }

    /**
     * Test constructor with message and cause
     */
    public void test_constructor_withMessageAndCause() {
        String message = "PDF extraction failed";
        IOException cause = new IOException("Cannot read file");
        ExtractException exception = new ExtractException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    /**
     * Test constructor with null message and valid cause
     */
    public void test_constructor_withNullMessageAndValidCause() {
        IOException cause = new IOException("IO error");
        ExtractException exception = new ExtractException(null, cause);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    /**
     * Test constructor with valid message and null cause
     */
    public void test_constructor_withValidMessageAndNullCause() {
        ExtractException exception = new ExtractException("Error without cause", null);

        assertNotNull(exception);
        assertEquals("Error without cause", exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test protected constructor via reflection
     */
    public void test_protectedConstructor() throws Exception {
        Constructor<ExtractException> constructor = ExtractException.class.getDeclaredConstructor(
                String.class, boolean.class, boolean.class);
        constructor.setAccessible(true);

        // Test with suppression and stack trace enabled
        ExtractException exception1 = constructor.newInstance("Test", true, true);
        assertNotNull(exception1);
        assertEquals("Test", exception1.getMessage());

        // Test with suppression disabled
        ExtractException exception2 = constructor.newInstance("Test", false, true);
        assertNotNull(exception2);
        exception2.addSuppressed(new RuntimeException("Suppressed"));
        assertEquals(0, exception2.getSuppressed().length);

        // Test with stack trace disabled
        ExtractException exception3 = constructor.newInstance("Test", true, false);
        assertNotNull(exception3);
        assertEquals(0, exception3.getStackTrace().length);
    }

    /**
     * Test inheritance
     */
    public void test_inheritance() {
        ExtractException exception = new ExtractException("Test");

        assertTrue(exception instanceof CrawlerSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    /**
     * Test throwing and catching
     */
    public void test_throwAndCatch() {
        try {
            throw new ExtractException("Extraction failed");
        } catch (ExtractException e) {
            assertEquals("Extraction failed", e.getMessage());
        }
    }

    /**
     * Test exception chaining
     */
    public void test_exceptionChaining() {
        Exception root = new IllegalStateException("Corrupted data");
        IOException middle = new IOException("File error", root);
        ExtractException top = new ExtractException("Extract failed", middle);

        assertEquals("Extract failed", top.getMessage());
        assertSame(middle, top.getCause());
        assertSame(root, top.getCause().getCause());
    }

    /**
     * Test with various extraction error messages
     */
    public void test_extractionErrorMessages() {
        ExtractException pdfError = new ExtractException("Failed to extract PDF content");
        assertTrue(pdfError.getMessage().contains("PDF"));

        ExtractException wordError = new ExtractException("Word document extraction failed: corrupted file");
        assertTrue(wordError.getMessage().contains("Word"));

        ExtractException excelError = new ExtractException("Cannot read Excel workbook");
        assertTrue(excelError.getMessage().contains("Excel"));
    }
}
