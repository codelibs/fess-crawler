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
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for ExecutionTimeoutException.
 * Tests all constructors and inheritance.
 */
public class ExecutionTimeoutExceptionTest extends PlainTestCase {

    /**
     * Test constructor with message only
     */
    @Test
    public void test_constructor_withMessage() {
        String message = "Execution timed out after 30 seconds";
        ExecutionTimeoutException exception = new ExecutionTimeoutException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with null message
     */
    @Test
    public void test_constructor_withNullMessage() {
        ExecutionTimeoutException exception = new ExecutionTimeoutException((String) null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    /**
     * Test constructor with cause only
     */
    @Test
    public void test_constructor_withCause() {
        TimeoutException cause = new TimeoutException("Timed out");
        ExecutionTimeoutException exception = new ExecutionTimeoutException(cause);

        assertNotNull(exception);
        assertTrue(cause == exception.getCause());
        assertTrue(exception.getMessage().contains("TimeoutException"));
    }

    /**
     * Test constructor with null cause
     */
    @Test
    public void test_constructor_withNullCause() {
        ExecutionTimeoutException exception = new ExecutionTimeoutException((Throwable) null);

        assertNotNull(exception);
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with message and cause
     */
    @Test
    public void test_constructor_withMessageAndCause() {
        String message = "Command execution timeout";
        IOException cause = new IOException("Process killed");
        ExecutionTimeoutException exception = new ExecutionTimeoutException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(cause == exception.getCause());
    }

    /**
     * Test inheritance
     */
    @Test
    public void test_inheritance() {
        ExecutionTimeoutException exception = new ExecutionTimeoutException("Test");

        assertTrue(exception instanceof ExtractException);
        assertTrue(exception instanceof CrawlerSystemException);
        assertTrue(exception instanceof RuntimeException);
    }

    /**
     * Test throwing and catching
     */
    @Test
    public void test_throwAndCatch() {
        try {
            throw new ExecutionTimeoutException("Timeout exceeded");
        } catch (ExecutionTimeoutException e) {
            assertEquals("Timeout exceeded", e.getMessage());
        }
    }

    /**
     * Test catching as parent type
     */
    @Test
    public void test_catchAsParentType() {
        try {
            throw new ExecutionTimeoutException("Timeout");
        } catch (ExtractException e) {
            assertTrue(e instanceof ExecutionTimeoutException);
        }
    }

    /**
     * Test timeout messages with duration
     */
    @Test
    public void test_timeoutMessageWithDuration() {
        ExecutionTimeoutException e1 = new ExecutionTimeoutException("Timeout after 5000ms");
        assertTrue(e1.getMessage().contains("5000"));

        ExecutionTimeoutException e2 = new ExecutionTimeoutException("Command exceeded 60 second limit");
        assertTrue(e2.getMessage().contains("60"));
    }

    /**
     * Test with InterruptedException cause
     */
    @Test
    public void test_withInterruptedException() {
        InterruptedException cause = new InterruptedException("Thread interrupted");
        ExecutionTimeoutException exception = new ExecutionTimeoutException("Interrupted during execution", cause);

        assertEquals("Interrupted during execution", exception.getMessage());
        assertTrue(exception.getCause() instanceof InterruptedException);
    }

    /**
     * Test exception chaining
     */
    @Test
    public void test_exceptionChaining() {
        Exception root = new IllegalStateException("Process hung");
        IOException middle = new IOException("Cannot kill process", root);
        ExecutionTimeoutException top = new ExecutionTimeoutException("Execution timeout", middle);

        assertEquals("Execution timeout", top.getMessage());
        assertTrue(middle == top.getCause());
        assertTrue(root == top.getCause().getCause());
    }
}
