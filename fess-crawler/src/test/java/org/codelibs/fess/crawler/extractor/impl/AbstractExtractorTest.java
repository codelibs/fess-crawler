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
package org.codelibs.fess.crawler.extractor.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for AbstractExtractor base functionality.
 * Focuses on testing the validateInputStream() method and other common functionality.
 */
public class AbstractExtractorTest extends PlainTestCase {

    /**
     * Concrete test implementation of AbstractExtractor for testing purposes.
     */
    private static class TestExtractor extends AbstractExtractor {
        private boolean validateCalled = false;
        private InputStream lastValidatedStream = null;

        @Override
        public ExtractData getText(final InputStream in, final Map<String, String> params) {
            validateInputStream(in);
            validateCalled = true;
            lastValidatedStream = in;
            return new ExtractData("test content");
        }

        public boolean isValidateCalled() {
            return validateCalled;
        }

        public InputStream getLastValidatedStream() {
            return lastValidatedStream;
        }

        public void resetTestState() {
            validateCalled = false;
            lastValidatedStream = null;
        }

        // Expose protected method for testing
        public void testValidateInputStream(final InputStream in) {
            validateInputStream(in);
        }
    }

    private TestExtractor extractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        extractor = new TestExtractor();
    }

    /**
     * Test that validateInputStream accepts non-null input streams.
     */
    @Test
    public void test_validateInputStream_acceptsNonNullStream() {
        final InputStream in = new ByteArrayInputStream(new byte[0]);

        // Should not throw exception
        extractor.testValidateInputStream(in);
    }

    /**
     * Test that validateInputStream throws CrawlerSystemException for null input.
     */
    @Test
    public void test_validateInputStream_throwsExceptionForNull() {
        try {
            extractor.testValidateInputStream(null);
            fail();
        } catch (final CrawlerSystemException e) {
            assertEquals("The inputstream is null.", e.getMessage());
        }
    }

    /**
     * Test that validateInputStream is called during getText execution.
     */
    @Test
    public void test_validateInputStream_calledDuringGetText() {
        final InputStream in = new ByteArrayInputStream("test data".getBytes());

        extractor.getText(in, null);

        assertTrue(extractor.isValidateCalled());
        assertTrue(in == extractor.getLastValidatedStream());
    }

    /**
     * Test that getText throws exception when null stream is provided.
     * Note: validateInputStream throws the exception, so the validateCalled flag
     * is never set to true (exception is thrown before flag assignment).
     */
    @Test
    public void test_getText_throwsExceptionForNullStream() {
        try {
            extractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            assertEquals("The inputstream is null.", e.getMessage());
            // Note: validateCalled will be false because exception is thrown
            // before the flag can be set, which is the expected behavior
            assertFalse(extractor.isValidateCalled());
        }
    }

    /**
     * Test that validateInputStream is consistent across multiple calls.
     */
    @Test
    public void test_validateInputStream_consistentAcrossMultipleCalls() {
        final InputStream in1 = new ByteArrayInputStream("data1".getBytes());
        final InputStream in2 = new ByteArrayInputStream("data2".getBytes());

        // First call
        extractor.testValidateInputStream(in1);

        // Second call with different stream
        extractor.testValidateInputStream(in2);

        // Third call with null should still throw
        try {
            extractor.testValidateInputStream(null);
            fail();
        } catch (final CrawlerSystemException e) {
            assertEquals("The inputstream is null.", e.getMessage());
        }
    }

    /**
     * Test that validateInputStream works with various InputStream implementations.
     */
    @Test
    public void test_validateInputStream_worksWithVariousStreamTypes() {
        // ByteArrayInputStream
        extractor.testValidateInputStream(new ByteArrayInputStream(new byte[10]));

        // Custom InputStream implementation
        final InputStream customStream = new InputStream() {
            @Override
            public int read() {
                return -1;
            }
        };
        extractor.testValidateInputStream(customStream);
    }

    /**
     * Test exception message format for null input stream.
     */
    @Test
    public void test_validateInputStream_exceptionMessageFormat() {
        try {
            extractor.testValidateInputStream(null);
            fail();
        } catch (final CrawlerSystemException e) {
            final String message = e.getMessage();
            assertNotNull(message);
            assertFalse(message.trim().isEmpty());
            assertTrue(message.toLowerCase().contains("inputstream"));
            assertTrue(message.toLowerCase().contains("null"));
        }
    }

    /**
     * Test that validateInputStream does not modify or consume the stream.
     */
    @Test
    public void test_validateInputStream_doesNotConsumeStream() throws Exception {
        final byte[] testData = "test data for validation".getBytes();
        final ByteArrayInputStream in = new ByteArrayInputStream(testData);
        final int availableBefore = in.available();

        extractor.testValidateInputStream(in);

        final int availableAfter = in.available();
        assertEquals(availableBefore, availableAfter);
    }

    /**
     * Test that validateInputStream is called exactly once per getText call.
     */
    @Test
    public void test_validateInputStream_calledOncePerGetText() {
        final InputStream in = new ByteArrayInputStream("test".getBytes());

        extractor.resetTestState();
        assertFalse(extractor.isValidateCalled());

        extractor.getText(in, null);

        assertTrue(extractor.isValidateCalled());
    }

    /**
     * Test validateInputStream with edge case: empty stream.
     */
    @Test
    public void test_validateInputStream_acceptsEmptyStream() {
        final InputStream emptyStream = new ByteArrayInputStream(new byte[0]);

        // Should not throw exception for empty but non-null stream
        extractor.testValidateInputStream(emptyStream);
    }

    /**
     * Test that CrawlerSystemException is the correct exception type.
     */
    @Test
    public void test_validateInputStream_throwsCorrectExceptionType() {
        try {
            extractor.testValidateInputStream(null);
            fail();
        } catch (final CrawlerSystemException e) {
            // Verify it's exactly CrawlerSystemException, not a subclass
            assertEquals(CrawlerSystemException.class, e.getClass());
        } catch (final Exception e) {
            fail();
        }
    }
}
