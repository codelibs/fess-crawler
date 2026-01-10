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
 * Test class for CrawlerLoginFailureException.
 * Tests constructor and inheritance.
 */
public class CrawlerLoginFailureExceptionTest extends PlainTestCase {

    /**
     * Test constructor with message
     */
    public void test_constructor_withMessage() {
        String message = "Login failed for user admin";
        CrawlerLoginFailureException exception = new CrawlerLoginFailureException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with null message
     */
    public void test_constructor_withNullMessage() {
        CrawlerLoginFailureException exception = new CrawlerLoginFailureException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    /**
     * Test constructor with empty message
     */
    public void test_constructor_withEmptyMessage() {
        CrawlerLoginFailureException exception = new CrawlerLoginFailureException("");

        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }

    /**
     * Test inheritance
     */
    public void test_inheritance() {
        CrawlerLoginFailureException exception = new CrawlerLoginFailureException("Test");

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
            throw new CrawlerLoginFailureException("Authentication failed");
        } catch (CrawlerLoginFailureException e) {
            assertEquals("Authentication failed", e.getMessage());
        }
    }

    /**
     * Test catching as parent type
     */
    public void test_catchAsParentType() {
        try {
            throw new CrawlerLoginFailureException("Login error");
        } catch (CrawlerSystemException e) {
            assertTrue(e instanceof CrawlerLoginFailureException);
            assertEquals("Login error", e.getMessage());
        }
    }

    /**
     * Test various login failure messages
     */
    public void test_loginFailureMessages() {
        CrawlerLoginFailureException e1 = new CrawlerLoginFailureException("Invalid credentials");
        assertEquals("Invalid credentials", e1.getMessage());

        CrawlerLoginFailureException e2 = new CrawlerLoginFailureException("Session expired");
        assertEquals("Session expired", e2.getMessage());

        CrawlerLoginFailureException e3 = new CrawlerLoginFailureException("Two-factor authentication required");
        assertEquals("Two-factor authentication required", e3.getMessage());

        CrawlerLoginFailureException e4 = new CrawlerLoginFailureException("Account locked");
        assertEquals("Account locked", e4.getMessage());
    }

    /**
     * Test message with URL
     */
    public void test_messageWithUrl() {
        String message = "Failed to login to http://example.com/admin: Invalid password";
        CrawlerLoginFailureException exception = new CrawlerLoginFailureException(message);

        assertTrue(exception.getMessage().contains("http://example.com"));
        assertTrue(exception.getMessage().contains("Invalid password"));
    }

    /**
     * Test message with username (should not include password)
     */
    public void test_messageWithUsername() {
        String message = "Login failed for user=testuser at http://example.com";
        CrawlerLoginFailureException exception = new CrawlerLoginFailureException(message);

        assertTrue(exception.getMessage().contains("testuser"));
        assertFalse(exception.getMessage().toLowerCase().contains("password"));
    }
}
