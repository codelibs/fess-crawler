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

import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for MaxLengthExceededException.
 * Tests constructor and inheritance.
 */
public class MaxLengthExceededExceptionTest extends PlainTestCase {

    /**
     * Test constructor with message
     */
    @Test
    public void test_constructor_withMessage() {
        String message = "Content length exceeds maximum: 1000000 > 500000";
        MaxLengthExceededException exception = new MaxLengthExceededException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with null message
     */
    @Test
    public void test_constructor_withNullMessage() {
        MaxLengthExceededException exception = new MaxLengthExceededException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    /**
     * Test constructor with empty message
     */
    @Test
    public void test_constructor_withEmptyMessage() {
        MaxLengthExceededException exception = new MaxLengthExceededException("");

        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }

    /**
     * Test inheritance chain
     */
    @Test
    public void test_inheritance() {
        MaxLengthExceededException exception = new MaxLengthExceededException("Test");

        assertTrue(exception instanceof CrawlingAccessException);
        assertTrue(exception instanceof CrawlerSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    /**
     * Test log level functionality from parent
     */
    @Test
    public void test_logLevelFromParent() {
        MaxLengthExceededException exception = new MaxLengthExceededException("Test");

        // Default is INFO from CrawlingAccessException
        assertTrue(exception.isInfoEnabled());

        exception.setLogLevel(CrawlingAccessException.WARN);
        assertTrue(exception.isWarnEnabled());
        assertFalse(exception.isInfoEnabled());
    }

    /**
     * Test throwing and catching
     */
    @Test
    public void test_throwAndCatch() {
        try {
            throw new MaxLengthExceededException("File too large: 10MB > 5MB");
        } catch (MaxLengthExceededException e) {
            assertEquals("File too large: 10MB > 5MB", e.getMessage());
        }
    }

    /**
     * Test catching as parent type
     */
    @Test
    public void test_catchAsParentType() {
        try {
            throw new MaxLengthExceededException("Content too large");
        } catch (CrawlingAccessException e) {
            assertTrue(e instanceof MaxLengthExceededException);
            assertEquals("Content too large", e.getMessage());
        }
    }

    /**
     * Test message with size details
     */
    @Test
    public void test_messageWithSizeDetails() {
        long actualSize = 10_000_000L;
        long maxSize = 5_000_000L;
        String message = String.format("Content length %d exceeds maximum allowed %d", actualSize, maxSize);

        MaxLengthExceededException exception = new MaxLengthExceededException(message);

        assertTrue(exception.getMessage().contains("10000000"));
        assertTrue(exception.getMessage().contains("5000000"));
    }

    /**
     * Test message with URL
     */
    @Test
    public void test_messageWithUrl() {
        String message = "http://example.com/large-file.pdf: Content length exceeds limit";
        MaxLengthExceededException exception = new MaxLengthExceededException(message);

        assertTrue(exception.getMessage().contains("http://example.com"));
        assertTrue(exception.getMessage().contains("large-file.pdf"));
    }
}
