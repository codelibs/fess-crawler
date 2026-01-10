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
 * Test class for RobotsTxtException.
 * Tests constructors and inheritance.
 */
public class RobotsTxtExceptionTest extends PlainTestCase {

    /**
     * Test constructor with message only
     */
    public void test_constructor_withMessage() {
        String message = "Failed to parse robots.txt";
        RobotsTxtException exception = new RobotsTxtException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with null message
     */
    public void test_constructor_withNullMessage() {
        RobotsTxtException exception = new RobotsTxtException((String) null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    /**
     * Test constructor with message and cause
     */
    public void test_constructor_withMessageAndCause() {
        String message = "robots.txt parsing error";
        IOException cause = new IOException("Cannot read file");
        RobotsTxtException exception = new RobotsTxtException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    /**
     * Test constructor with null message and valid cause
     */
    public void test_constructor_withNullMessageAndValidCause() {
        IOException cause = new IOException("IO error");
        RobotsTxtException exception = new RobotsTxtException(null, cause);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    /**
     * Test inheritance
     */
    public void test_inheritance() {
        RobotsTxtException exception = new RobotsTxtException("Test");

        assertTrue(exception instanceof CrawlerSystemException);
        assertTrue(exception instanceof RuntimeException);
    }

    /**
     * Test throwing and catching
     */
    public void test_throwAndCatch() {
        try {
            throw new RobotsTxtException("robots.txt not found");
        } catch (RobotsTxtException e) {
            assertEquals("robots.txt not found", e.getMessage());
        }
    }

    /**
     * Test catching as parent type
     */
    public void test_catchAsParentType() {
        try {
            throw new RobotsTxtException("Parse error");
        } catch (CrawlerSystemException e) {
            assertTrue(e instanceof RobotsTxtException);
        }
    }

    /**
     * Test various robots.txt error messages
     */
    public void test_robotsTxtErrorMessages() {
        RobotsTxtException e1 = new RobotsTxtException("Invalid directive: Disalow");
        assertTrue(e1.getMessage().contains("Disalow"));

        RobotsTxtException e2 = new RobotsTxtException("robots.txt too large: 512KB > 500KB");
        assertTrue(e2.getMessage().contains("512KB"));

        RobotsTxtException e3 = new RobotsTxtException("Malformed User-agent directive");
        assertTrue(e3.getMessage().contains("User-agent"));
    }

    /**
     * Test message with URL
     */
    public void test_messageWithUrl() {
        String message = "Failed to fetch http://example.com/robots.txt";
        RobotsTxtException exception = new RobotsTxtException(message);

        assertTrue(exception.getMessage().contains("http://example.com"));
        assertTrue(exception.getMessage().contains("robots.txt"));
    }

    /**
     * Test exception chaining
     */
    public void test_exceptionChaining() {
        Exception root = new IllegalArgumentException("Invalid character");
        IOException middle = new IOException("Parse error", root);
        RobotsTxtException top = new RobotsTxtException("robots.txt error", middle);

        assertEquals("robots.txt error", top.getMessage());
        assertSame(middle, top.getCause());
        assertSame(root, top.getCause().getCause());
    }
}
