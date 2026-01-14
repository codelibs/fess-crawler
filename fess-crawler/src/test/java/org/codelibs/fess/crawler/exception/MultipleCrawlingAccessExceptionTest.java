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
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for MultipleCrawlingAccessException.
 * Tests constructor, getCauses, and printStackTrace functionality.
 */
public class MultipleCrawlingAccessExceptionTest extends PlainTestCase {

    /**
     * Test constructor with message and empty array
     */
    @Test
    public void test_constructor_emptyArray() {
        Throwable[] causes = new Throwable[0];
        MultipleCrawlingAccessException exception = new MultipleCrawlingAccessException("No causes", causes);

        assertNotNull(exception);
        assertEquals("No causes", exception.getMessage());
        assertEquals(0, exception.getCauses().length);
    }

    /**
     * Test constructor with message and null array
     */
    @Test
    public void test_constructor_nullArray() {
        MultipleCrawlingAccessException exception = new MultipleCrawlingAccessException("Null causes", null);

        assertNotNull(exception);
        assertEquals("Null causes", exception.getMessage());
        assertNotNull(exception.getCauses());
        assertEquals(0, exception.getCauses().length);
    }

    /**
     * Test constructor with single cause
     */
    @Test
    public void test_constructor_singleCause() {
        Throwable[] causes = new Throwable[] { new IOException("IO error") };
        MultipleCrawlingAccessException exception = new MultipleCrawlingAccessException("Single cause", causes);

        assertNotNull(exception);
        assertEquals("Single cause", exception.getMessage());
        assertEquals(1, exception.getCauses().length);
        assertEquals("IO error", exception.getCauses()[0].getMessage());
    }

    /**
     * Test constructor with multiple causes
     */
    @Test
    public void test_constructor_multipleCauses() {
        Throwable[] causes = new Throwable[] { new IOException("IO error 1"), new IllegalArgumentException("Illegal arg"),
                new RuntimeException("Runtime error") };
        MultipleCrawlingAccessException exception = new MultipleCrawlingAccessException("Multiple causes", causes);

        assertNotNull(exception);
        assertEquals("Multiple causes", exception.getMessage());
        assertEquals(3, exception.getCauses().length);
        assertTrue(exception.getCauses()[0] instanceof IOException);
        assertTrue(exception.getCauses()[1] instanceof IllegalArgumentException);
        assertTrue(exception.getCauses()[2] instanceof RuntimeException);
    }

    /**
     * Test getCauses returns same array
     */
    @Test
    public void test_getCauses_returnsSameArray() {
        Throwable[] causes = new Throwable[] { new IOException("Test") };
        MultipleCrawlingAccessException exception = new MultipleCrawlingAccessException("Test", causes);

        assertTrue(causes == exception.getCauses());
    }

    /**
     * Test printStackTrace with PrintWriter
     */
    @Test
    public void test_printStackTrace_printWriter() {
        Throwable[] causes = new Throwable[] { new IOException("IO error"), new IllegalArgumentException("Arg error") };
        MultipleCrawlingAccessException exception = new MultipleCrawlingAccessException("Multiple errors", causes);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        pw.flush();

        String output = sw.toString();
        assertTrue(output.contains("MultipleCrawlingAccessException"));
        assertTrue(output.contains("Multiple errors"));
        assertTrue(output.contains("Caused 1:"));
        assertTrue(output.contains("IO error"));
        assertTrue(output.contains("Caused 2:"));
        assertTrue(output.contains("Arg error"));
    }

    /**
     * Test printStackTrace with PrintStream
     */
    @Test
    public void test_printStackTrace_printStream() {
        Throwable[] causes = new Throwable[] { new IOException("IO error"), new IllegalArgumentException("Arg error") };
        MultipleCrawlingAccessException exception = new MultipleCrawlingAccessException("Multiple errors", causes);

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        exception.printStackTrace(ps);
        ps.flush();

        String output = baos.toString();
        assertTrue(output.contains("MultipleCrawlingAccessException"));
        assertTrue(output.contains("Multiple errors"));
        assertTrue(output.contains("Cause #1:"));
        assertTrue(output.contains("IO error"));
        assertTrue(output.contains("Cause #2:"));
        assertTrue(output.contains("Arg error"));
    }

    /**
     * Test printStackTrace with empty causes array
     */
    @Test
    public void test_printStackTrace_emptyCauses() {
        MultipleCrawlingAccessException exception = new MultipleCrawlingAccessException("No causes", new Throwable[0]);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        pw.flush();

        String output = sw.toString();
        assertTrue(output.contains("MultipleCrawlingAccessException"));
        assertTrue(output.contains("No causes"));
        // Should not contain "Caused" since there are no causes
        assertFalse(output.contains("Caused 1:"));
    }

    /**
     * Test inheritance
     */
    @Test
    public void test_inheritance() {
        MultipleCrawlingAccessException exception = new MultipleCrawlingAccessException("Test", new Throwable[0]);

        assertTrue(exception instanceof CrawlingAccessException);
        assertTrue(exception instanceof CrawlerSystemException);
        assertTrue(exception instanceof RuntimeException);
    }

    /**
     * Test with nested exceptions
     */
    @Test
    public void test_nestedExceptions() {
        Exception root = new IllegalStateException("Root cause");
        IOException nested = new IOException("Nested", root);
        Throwable[] causes = new Throwable[] { nested };

        MultipleCrawlingAccessException exception = new MultipleCrawlingAccessException("Nested test", causes);

        assertEquals(1, exception.getCauses().length);
        assertEquals("Nested", exception.getCauses()[0].getMessage());
        assertEquals("Root cause", exception.getCauses()[0].getCause().getMessage());
    }

    /**
     * Test throwing and catching
     */
    @Test
    public void test_throwAndCatch() {
        Throwable[] causes = new Throwable[] { new IOException("Cause 1"), new IOException("Cause 2") };

        try {
            throw new MultipleCrawlingAccessException("Test throw", causes);
        } catch (MultipleCrawlingAccessException e) {
            assertEquals("Test throw", e.getMessage());
            assertEquals(2, e.getCauses().length);
        }
    }

    /**
     * Test with large number of causes
     */
    @Test
    public void test_largeCausesArray() {
        Throwable[] causes = new Throwable[100];
        for (int i = 0; i < 100; i++) {
            causes[i] = new IOException("Error " + i);
        }

        MultipleCrawlingAccessException exception = new MultipleCrawlingAccessException("Many errors", causes);

        assertEquals(100, exception.getCauses().length);
    }

    /**
     * Test causes array contains null elements
     */
    @Test
    public void test_causesWithNullElements() {
        Throwable[] causes = new Throwable[] { new IOException("Error 1"), null, new IOException("Error 3") };

        MultipleCrawlingAccessException exception = new MultipleCrawlingAccessException("Null element", causes);

        assertEquals(3, exception.getCauses().length);
        assertNotNull(exception.getCauses()[0]);
        assertNull(exception.getCauses()[1]);
        assertNotNull(exception.getCauses()[2]);
    }

    /**
     * Test log level functionality inherited from CrawlingAccessException
     */
    @Test
    public void test_logLevelInheritance() {
        MultipleCrawlingAccessException exception = new MultipleCrawlingAccessException("Test", new Throwable[0]);

        // Default should be INFO
        assertTrue(exception.isInfoEnabled());
        assertFalse(exception.isErrorEnabled());

        exception.setLogLevel(CrawlingAccessException.ERROR);
        assertTrue(exception.isErrorEnabled());
        assertFalse(exception.isInfoEnabled());
    }
}
