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

import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for CrawlingAccessException.
 * Tests constructors, log level functionality, and inheritance.
 */
public class CrawlingAccessExceptionTest extends PlainTestCase {

    /**
     * Test constructor with message only
     */
    public void test_constructor_withMessage() {
        String message = "Access error occurred";
        CrawlingAccessException exception = new CrawlingAccessException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof CrawlerSystemException);
    }

    /**
     * Test constructor with null message
     */
    public void test_constructor_withNullMessage() {
        CrawlingAccessException exception = new CrawlingAccessException((String) null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    /**
     * Test constructor with cause only
     */
    public void test_constructor_withCause() {
        IOException cause = new IOException("IO error");
        CrawlingAccessException exception = new CrawlingAccessException(cause);

        assertNotNull(exception);
        assertTrue(cause == exception.getCause());
        assertEquals("java.io.IOException: IO error", exception.getMessage());
    }

    /**
     * Test constructor with null cause
     */
    public void test_constructor_withNullCause() {
        CrawlingAccessException exception = new CrawlingAccessException((Throwable) null);

        assertNotNull(exception);
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with message and cause
     */
    public void test_constructor_withMessageAndCause() {
        String message = "Failed to access URL";
        IOException cause = new IOException("Connection refused");
        CrawlingAccessException exception = new CrawlingAccessException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(cause == exception.getCause());
    }

    /**
     * Test default log level is INFO
     */
    public void test_defaultLogLevel() {
        CrawlingAccessException exception = new CrawlingAccessException("Test");

        assertFalse(exception.isDebugEnabled());
        assertTrue(exception.isInfoEnabled());
        assertFalse(exception.isWarnEnabled());
        assertFalse(exception.isErrorEnabled());
    }

    /**
     * Test log level constants
     */
    public void test_logLevelConstants() {
        assertEquals("DEBUG", CrawlingAccessException.DEBUG);
        assertEquals("INFO", CrawlingAccessException.INFO);
        assertEquals("WARN", CrawlingAccessException.WARN);
        assertEquals("ERROR", CrawlingAccessException.ERROR);
    }

    /**
     * Test setLogLevel to DEBUG
     */
    public void test_setLogLevel_debug() {
        CrawlingAccessException exception = new CrawlingAccessException("Test");
        exception.setLogLevel(CrawlingAccessException.DEBUG);

        assertTrue(exception.isDebugEnabled());
        assertFalse(exception.isInfoEnabled());
        assertFalse(exception.isWarnEnabled());
        assertFalse(exception.isErrorEnabled());
    }

    /**
     * Test setLogLevel to INFO
     */
    public void test_setLogLevel_info() {
        CrawlingAccessException exception = new CrawlingAccessException("Test");
        exception.setLogLevel(CrawlingAccessException.INFO);

        assertFalse(exception.isDebugEnabled());
        assertTrue(exception.isInfoEnabled());
        assertFalse(exception.isWarnEnabled());
        assertFalse(exception.isErrorEnabled());
    }

    /**
     * Test setLogLevel to WARN
     */
    public void test_setLogLevel_warn() {
        CrawlingAccessException exception = new CrawlingAccessException("Test");
        exception.setLogLevel(CrawlingAccessException.WARN);

        assertFalse(exception.isDebugEnabled());
        assertFalse(exception.isInfoEnabled());
        assertTrue(exception.isWarnEnabled());
        assertFalse(exception.isErrorEnabled());
    }

    /**
     * Test setLogLevel to ERROR
     */
    public void test_setLogLevel_error() {
        CrawlingAccessException exception = new CrawlingAccessException("Test");
        exception.setLogLevel(CrawlingAccessException.ERROR);

        assertFalse(exception.isDebugEnabled());
        assertFalse(exception.isInfoEnabled());
        assertFalse(exception.isWarnEnabled());
        assertTrue(exception.isErrorEnabled());
    }

    /**
     * Test setLogLevel to null
     */
    public void test_setLogLevel_null() {
        CrawlingAccessException exception = new CrawlingAccessException("Test");
        exception.setLogLevel(null);

        assertFalse(exception.isDebugEnabled());
        assertFalse(exception.isInfoEnabled());
        assertFalse(exception.isWarnEnabled());
        assertFalse(exception.isErrorEnabled());
    }

    /**
     * Test setLogLevel to invalid value
     */
    public void test_setLogLevel_invalid() {
        CrawlingAccessException exception = new CrawlingAccessException("Test");
        exception.setLogLevel("INVALID");

        assertFalse(exception.isDebugEnabled());
        assertFalse(exception.isInfoEnabled());
        assertFalse(exception.isWarnEnabled());
        assertFalse(exception.isErrorEnabled());
    }

    /**
     * Test inheritance - should be RuntimeException
     */
    public void test_inheritance() {
        CrawlingAccessException exception = new CrawlingAccessException("Test");

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
            throw new CrawlingAccessException("Test throw");
        } catch (CrawlingAccessException e) {
            assertEquals("Test throw", e.getMessage());
            assertTrue(e.isInfoEnabled()); // default
        }
    }

    /**
     * Test exception chaining
     */
    public void test_exceptionChaining() {
        Exception root = new IllegalArgumentException("Root cause");
        IOException middle = new IOException("Middle", root);
        CrawlingAccessException top = new CrawlingAccessException("Top level", middle);

        assertEquals("Top level", top.getMessage());
        assertTrue(middle == top.getCause());
        assertTrue(root == top.getCause().getCause());
    }
}
