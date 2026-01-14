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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;

import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for CrawlerSystemException.
 * Tests all constructors and exception functionality including message, cause, suppression, and stack trace.
 */
public class CrawlerSystemExceptionTest extends PlainTestCase {

    /**
     * Test constructor with message only
     */
    @Test
    public void test_constructor_withMessage() {
        String message = "Test error message";
        CrawlerSystemException exception = new CrawlerSystemException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Throwable);
    }

    /**
     * Test constructor with null message
     */
    @Test
    public void test_constructor_withNullMessage() {
        CrawlerSystemException exception = new CrawlerSystemException((String) null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with empty message
     */
    @Test
    public void test_constructor_withEmptyMessage() {
        CrawlerSystemException exception = new CrawlerSystemException("");

        assertNotNull(exception);
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with very long message
     */
    @Test
    public void test_constructor_withVeryLongMessage() {
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longMessage.append("Very long error message ");
        }
        String message = longMessage.toString();

        CrawlerSystemException exception = new CrawlerSystemException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(240000, exception.getMessage().length());
    }

    /**
     * Test constructor with special characters in message
     */
    @Test
    public void test_constructor_withSpecialCharactersInMessage() {
        String message = "Error: \n\t\r\0\b\f with special chars #@!$%^&*()[]{}";
        CrawlerSystemException exception = new CrawlerSystemException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    /**
     * Test constructor with cause only
     */
    @Test
    public void test_constructor_withCause() {
        Exception cause = new IllegalArgumentException("Root cause");
        CrawlerSystemException exception = new CrawlerSystemException(cause);

        assertNotNull(exception);
        assertTrue(cause == exception.getCause());
        // When constructed with cause only, message should be cause's toString()
        assertEquals("java.lang.IllegalArgumentException: Root cause", exception.getMessage());
    }

    /**
     * Test constructor with null cause
     */
    @Test
    public void test_constructor_withNullCause() {
        CrawlerSystemException exception = new CrawlerSystemException((Throwable) null);

        assertNotNull(exception);
        assertNull(exception.getCause());
        assertNull(exception.getMessage());
    }

    /**
     * Test constructor with various cause types
     */
    @Test
    public void test_constructor_withVariousCauseTypes() {
        // With Exception cause
        Exception exceptionCause = new Exception("Exception cause");
        CrawlerSystemException exception1 = new CrawlerSystemException(exceptionCause);
        assertTrue(exceptionCause == exception1.getCause());

        // With Error cause
        Error errorCause = new Error("Error cause");
        CrawlerSystemException exception2 = new CrawlerSystemException(errorCause);
        assertTrue(errorCause == exception2.getCause());

        // With RuntimeException cause
        RuntimeException runtimeCause = new RuntimeException("Runtime cause");
        CrawlerSystemException exception3 = new CrawlerSystemException(runtimeCause);
        assertTrue(runtimeCause == exception3.getCause());

        // With another CrawlerSystemException cause
        CrawlerSystemException crawlerCause = new CrawlerSystemException("Crawler cause");
        CrawlerSystemException exception4 = new CrawlerSystemException(crawlerCause);
        assertTrue(crawlerCause == exception4.getCause());
    }

    /**
     * Test constructor with message and cause
     */
    @Test
    public void test_constructor_withMessageAndCause() {
        String message = "Custom error message";
        Exception cause = new IOException("IO error occurred");
        CrawlerSystemException exception = new CrawlerSystemException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(cause == exception.getCause());
    }

    /**
     * Test constructor with null message and valid cause
     */
    @Test
    public void test_constructor_withNullMessageAndValidCause() {
        Exception cause = new IllegalStateException("State error");
        CrawlerSystemException exception = new CrawlerSystemException(null, cause);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertTrue(cause == exception.getCause());
    }

    /**
     * Test constructor with valid message and null cause
     */
    @Test
    public void test_constructor_withValidMessageAndNullCause() {
        String message = "Error without cause";
        CrawlerSystemException exception = new CrawlerSystemException(message, null);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with both null message and null cause
     */
    @Test
    public void test_constructor_withNullMessageAndNullCause() {
        CrawlerSystemException exception = new CrawlerSystemException(null, null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test protected constructor with suppression and stack trace control
     */
    @Test
    public void test_protectedConstructor_withSuppressionAndStackTrace() throws Exception {
        // Access protected constructor via reflection
        Constructor<CrawlerSystemException> constructor =
                CrawlerSystemException.class.getDeclaredConstructor(String.class, boolean.class, boolean.class);
        constructor.setAccessible(true);

        // Test with suppression enabled and writable stack trace
        String message1 = "Test with suppression enabled";
        CrawlerSystemException exception1 = constructor.newInstance(message1, true, true);
        assertNotNull(exception1);
        assertEquals(message1, exception1.getMessage());
        assertNull(exception1.getCause());

        // Test with suppression disabled and writable stack trace
        String message2 = "Test with suppression disabled";
        CrawlerSystemException exception2 = constructor.newInstance(message2, false, true);
        assertNotNull(exception2);
        assertEquals(message2, exception2.getMessage());

        // Test with suppression enabled and non-writable stack trace
        String message3 = "Test with non-writable stack trace";
        CrawlerSystemException exception3 = constructor.newInstance(message3, true, false);
        assertNotNull(exception3);
        assertEquals(message3, exception3.getMessage());

        // Test with both disabled
        String message4 = "Test with both disabled";
        CrawlerSystemException exception4 = constructor.newInstance(message4, false, false);
        assertNotNull(exception4);
        assertEquals(message4, exception4.getMessage());
    }

    /**
     * Test protected constructor with null message
     */
    @Test
    public void test_protectedConstructor_withNullMessage() throws Exception {
        Constructor<CrawlerSystemException> constructor =
                CrawlerSystemException.class.getDeclaredConstructor(String.class, boolean.class, boolean.class);
        constructor.setAccessible(true);

        CrawlerSystemException exception = constructor.newInstance(null, true, true);
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test stack trace functionality
     */
    @Test
    public void test_stackTrace() {
        CrawlerSystemException exception = new CrawlerSystemException("Stack trace test");

        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);

        // First element should be from this test class
        StackTraceElement firstElement = stackTrace[0];
        assertEquals(this.getClass().getName(), firstElement.getClassName());
        assertEquals("test_stackTrace", firstElement.getMethodName());
    }

    /**
     * Test stack trace with cause
     */
    @Test
    public void test_stackTraceWithCause() {
        Exception cause = new IllegalArgumentException("Cause exception");
        CrawlerSystemException exception = new CrawlerSystemException("Main exception", cause);

        StackTraceElement[] mainStackTrace = exception.getStackTrace();
        StackTraceElement[] causeStackTrace = cause.getStackTrace();

        assertNotNull(mainStackTrace);
        assertNotNull(causeStackTrace);
        assertTrue(mainStackTrace.length > 0);
        assertTrue(causeStackTrace.length > 0);

        // Stack traces should be different
        assertFalse(mainStackTrace == causeStackTrace);
    }

    /**
     * Test printStackTrace functionality
     */
    @Test
    public void test_printStackTrace() {
        CrawlerSystemException exception = new CrawlerSystemException("Print stack trace test");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);

        String stackTraceOutput = stringWriter.toString();
        assertNotNull(stackTraceOutput);
        assertTrue(stackTraceOutput.contains("CrawlerSystemException"));
        assertTrue(stackTraceOutput.contains("Print stack trace test"));
        assertTrue(stackTraceOutput.contains(this.getClass().getName()));
    }

    /**
     * Test printStackTrace with cause
     */
    @Test
    public void test_printStackTraceWithCause() {
        Exception cause = new IOException("IO error");
        CrawlerSystemException exception = new CrawlerSystemException("Wrapper exception", cause);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);

        String stackTraceOutput = stringWriter.toString();
        assertNotNull(stackTraceOutput);
        assertTrue(stackTraceOutput.contains("CrawlerSystemException"));
        assertTrue(stackTraceOutput.contains("Wrapper exception"));
        assertTrue(stackTraceOutput.contains("Caused by:"));
        assertTrue(stackTraceOutput.contains("IOException"));
        assertTrue(stackTraceOutput.contains("IO error"));
    }

    /**
     * Test exception chaining
     */
    @Test
    public void test_exceptionChaining() {
        Exception level3 = new IllegalArgumentException("Level 3 - Root cause");
        Exception level2 = new IllegalStateException("Level 2", level3);
        Exception level1 = new IOException("Level 1", level2);
        CrawlerSystemException topLevel = new CrawlerSystemException("Top level", level1);

        // Verify chain
        assertTrue(level1 == topLevel.getCause());
        assertTrue(level2 == topLevel.getCause().getCause());
        assertTrue(level3 == topLevel.getCause().getCause().getCause());
        assertNull(topLevel.getCause().getCause().getCause().getCause());

        // Verify messages
        assertEquals("Top level", topLevel.getMessage());
        assertEquals("Level 1", level1.getMessage());
        assertEquals("Level 2", level2.getMessage());
        assertEquals("Level 3 - Root cause", level3.getMessage());
    }

    /**
     * Test serialization
     */
    @Test
    public void test_serialization() throws Exception {
        String message = "Serialization test";
        Exception cause = new IOException("IO cause");
        CrawlerSystemException original = new CrawlerSystemException(message, cause);

        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        CrawlerSystemException deserialized = (CrawlerSystemException) ois.readObject();
        ois.close();

        // Verify
        assertNotNull(deserialized);
        assertEquals(original.getMessage(), deserialized.getMessage());
        assertNotNull(deserialized.getCause());
        assertEquals(original.getCause().getMessage(), deserialized.getCause().getMessage());
        assertEquals(original.getCause().getClass(), deserialized.getCause().getClass());
    }

    /**
     * Test throwing and catching the exception
     */
    @Test
    public void test_throwAndCatch() {
        try {
            throw new CrawlerSystemException("Test throw");
        } catch (CrawlerSystemException e) {
            assertEquals("Test throw", e.getMessage());
            assertNull(e.getCause());
        }

        try {
            throw new CrawlerSystemException("Test with cause", new IllegalArgumentException("Cause"));
        } catch (CrawlerSystemException e) {
            assertEquals("Test with cause", e.getMessage());
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    /**
     * Test exception in method that declares throws
     */
    @Test
    public void test_exceptionInMethod() {
        try {
            methodThatThrowsException();
            fail();
        } catch (CrawlerSystemException e) {
            assertEquals("Method exception", e.getMessage());
        }
    }

    private void methodThatThrowsException() {
        throw new CrawlerSystemException("Method exception");
    }

    /**
     * Test exception inheritance
     */
    @Test
    public void test_inheritance() {
        CrawlerSystemException exception = new CrawlerSystemException("Test");

        // Should be instance of RuntimeException
        assertTrue(exception instanceof RuntimeException);

        // Should be instance of Exception
        assertTrue(exception instanceof Exception);

        // Should be instance of Throwable
        assertTrue(exception instanceof Throwable);

        // Should not require try-catch (unchecked exception)
        throwUnchecked(); // This compiles without try-catch
    }

    private void throwUnchecked() {
        if (Math.random() > 1) { // Never true, but compiler doesn't know
            throw new CrawlerSystemException("Unchecked");
        }
    }

    /**
     * Test suppressed exceptions
     */
    @Test
    public void test_suppressedExceptions() throws Exception {
        // Create exception with suppression enabled
        Constructor<CrawlerSystemException> constructor =
                CrawlerSystemException.class.getDeclaredConstructor(String.class, boolean.class, boolean.class);
        constructor.setAccessible(true);

        CrawlerSystemException mainException = constructor.newInstance("Main exception", true, true);

        // Add suppressed exceptions
        Exception suppressed1 = new IllegalArgumentException("Suppressed 1");
        Exception suppressed2 = new IOException("Suppressed 2");
        mainException.addSuppressed(suppressed1);
        mainException.addSuppressed(suppressed2);

        Throwable[] suppressedExceptions = mainException.getSuppressed();
        assertEquals(2, suppressedExceptions.length);
        assertTrue(suppressed1 == suppressedExceptions[0]);
        assertTrue(suppressed2 == suppressedExceptions[1]);
    }

    /**
     * Test suppressed exceptions with suppression disabled
     */
    @Test
    public void test_suppressedExceptions_disabled() throws Exception {
        Constructor<CrawlerSystemException> constructor =
                CrawlerSystemException.class.getDeclaredConstructor(String.class, boolean.class, boolean.class);
        constructor.setAccessible(true);

        // Create exception with suppression disabled
        CrawlerSystemException exception = constructor.newInstance("No suppression", false, true);

        // Try to add suppressed exception
        Exception suppressed = new IllegalArgumentException("Should not be added");
        exception.addSuppressed(suppressed);

        // Suppressed exceptions should not be added when suppression is disabled
        Throwable[] suppressedExceptions = exception.getSuppressed();
        assertEquals(0, suppressedExceptions.length);
    }

    /**
     * Test fillInStackTrace
     */
    @Test
    public void test_fillInStackTrace() {
        CrawlerSystemException exception = new CrawlerSystemException("Fill stack trace test");

        StackTraceElement[] originalStackTrace = exception.getStackTrace();
        assertNotNull(originalStackTrace);
        assertTrue(originalStackTrace.length > 0);

        // Fill in stack trace again
        Throwable filled = exception.fillInStackTrace();
        assertTrue(exception == filled);

        StackTraceElement[] newStackTrace = exception.getStackTrace();
        assertNotNull(newStackTrace);
        assertTrue(newStackTrace.length > 0);
    }

    /**
     * Test with non-writable stack trace
     */
    @Test
    public void test_nonWritableStackTrace() throws Exception {
        Constructor<CrawlerSystemException> constructor =
                CrawlerSystemException.class.getDeclaredConstructor(String.class, boolean.class, boolean.class);
        constructor.setAccessible(true);

        // Create exception with non-writable stack trace
        CrawlerSystemException exception = constructor.newInstance("Non-writable stack", true, false);

        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertEquals(0, stackTrace.length); // Stack trace should be empty when not writable
    }

    /**
     * Test toString method
     */
    @Test
    public void test_toString() {
        CrawlerSystemException exception1 = new CrawlerSystemException("Test message");
        String toString1 = exception1.toString();
        assertNotNull(toString1);
        assertTrue(toString1.contains("CrawlerSystemException"));
        assertTrue(toString1.contains("Test message"));

        CrawlerSystemException exception2 = new CrawlerSystemException((String) null);
        String toString2 = exception2.toString();
        assertNotNull(toString2);
        assertTrue(toString2.contains("CrawlerSystemException"));
    }

    /**
     * Test getLocalizedMessage
     */
    @Test
    public void test_getLocalizedMessage() {
        String message = "Localized message test";
        CrawlerSystemException exception = new CrawlerSystemException(message);

        assertEquals(message, exception.getLocalizedMessage());

        // With null message
        CrawlerSystemException nullMessageException = new CrawlerSystemException((String) null);
        assertNull(nullMessageException.getLocalizedMessage());
    }
}
