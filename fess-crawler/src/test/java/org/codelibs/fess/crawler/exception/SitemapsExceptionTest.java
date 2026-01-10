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
 * Test class for SitemapsException.
 * Tests all constructors and inheritance.
 */
public class SitemapsExceptionTest extends PlainTestCase {

    /**
     * Test constructor with message only
     */
    public void test_constructor_withMessage() {
        String message = "Failed to parse sitemap";
        SitemapsException exception = new SitemapsException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with null message
     */
    public void test_constructor_withNullMessage() {
        SitemapsException exception = new SitemapsException((String) null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    /**
     * Test constructor with cause only
     */
    public void test_constructor_withCause() {
        IOException cause = new IOException("XML parse error");
        SitemapsException exception = new SitemapsException(cause);

        assertNotNull(exception);
        assertSame(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("IOException"));
    }

    /**
     * Test constructor with null cause
     */
    public void test_constructor_withNullCause() {
        SitemapsException exception = new SitemapsException((Throwable) null);

        assertNotNull(exception);
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with message and cause
     */
    public void test_constructor_withMessageAndCause() {
        String message = "Sitemap XML invalid";
        IOException cause = new IOException("Malformed XML");
        SitemapsException exception = new SitemapsException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    /**
     * Test inheritance
     */
    public void test_inheritance() {
        SitemapsException exception = new SitemapsException("Test");

        assertTrue(exception instanceof CrawlerSystemException);
        assertTrue(exception instanceof RuntimeException);
    }

    /**
     * Test throwing and catching
     */
    public void test_throwAndCatch() {
        try {
            throw new SitemapsException("Sitemap not found");
        } catch (SitemapsException e) {
            assertEquals("Sitemap not found", e.getMessage());
        }
    }

    /**
     * Test catching as parent type
     */
    public void test_catchAsParentType() {
        try {
            throw new SitemapsException("Parse error");
        } catch (CrawlerSystemException e) {
            assertTrue(e instanceof SitemapsException);
        }
    }

    /**
     * Test various sitemap error messages
     */
    public void test_sitemapErrorMessages() {
        SitemapsException e1 = new SitemapsException("Invalid sitemap index");
        assertTrue(e1.getMessage().contains("sitemap index"));

        SitemapsException e2 = new SitemapsException("URL element missing loc tag");
        assertTrue(e2.getMessage().contains("loc"));

        SitemapsException e3 = new SitemapsException("Invalid lastmod date format: 2024-13-45");
        assertTrue(e3.getMessage().contains("lastmod"));
    }

    /**
     * Test message with URL
     */
    public void test_messageWithUrl() {
        String message = "Failed to parse http://example.com/sitemap.xml";
        SitemapsException exception = new SitemapsException(message);

        assertTrue(exception.getMessage().contains("http://example.com"));
        assertTrue(exception.getMessage().contains("sitemap.xml"));
    }

    /**
     * Test exception chaining
     */
    public void test_exceptionChaining() {
        Exception root = new org.xml.sax.SAXException("XML error");
        IOException middle = new IOException("Parse failed", root);
        SitemapsException top = new SitemapsException("Sitemap error", middle);

        assertEquals("Sitemap error", top.getMessage());
        assertSame(middle, top.getCause());
        assertSame(root, top.getCause().getCause());
    }

    /**
     * Test with gzip sitemap error
     */
    public void test_gzipSitemapError() {
        IOException cause = new IOException("Not in GZIP format");
        SitemapsException exception = new SitemapsException("Failed to decompress sitemap.xml.gz", cause);

        assertTrue(exception.getMessage().contains("gz"));
        assertTrue(exception.getCause().getMessage().contains("GZIP"));
    }
}
