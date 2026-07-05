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
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
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

        // Expose depth helpers for testing.
        public int testGetCurrentDepth(final Map<String, String> params) {
            return getCurrentDepth(params);
        }

        public Map<String, String> testIncrementDepth(final Map<String, String> params) {
            return incrementDepth(params);
        }

        public void testCheckDepth(final Map<String, String> params, final int maxDepth) {
            checkDepth(params, maxDepth);
        }

        // Expose static helpers for testing.
        public boolean testIsPathTraversal(final String name) {
            return isPathTraversal(name);
        }

        public long testAddOneSaturating(final long value) {
            return addOneSaturating(value);
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

    /** Recursion-depth helper: missing/null params return 0. */
    @Test
    public void test_getCurrentDepth_returnsZeroForMissing() {
        assertEquals(0, extractor.testGetCurrentDepth(null));
        assertEquals(0, extractor.testGetCurrentDepth(new HashMap<>()));
        final Map<String, String> blank = new HashMap<>();
        blank.put(AbstractExtractor.EXTRACTOR_DEPTH_KEY, "");
        assertEquals(0, extractor.testGetCurrentDepth(blank));
        final Map<String, String> garbage = new HashMap<>();
        garbage.put(AbstractExtractor.EXTRACTOR_DEPTH_KEY, "not-a-number");
        assertEquals(0, extractor.testGetCurrentDepth(garbage));
    }

    /** Recursion-depth helper: depth value is parsed and clamped to >= 0. */
    @Test
    public void test_getCurrentDepth_parsesValidValue() {
        final Map<String, String> params = new HashMap<>();
        params.put(AbstractExtractor.EXTRACTOR_DEPTH_KEY, "3");
        assertEquals(3, extractor.testGetCurrentDepth(params));

        params.put(AbstractExtractor.EXTRACTOR_DEPTH_KEY, "-5");
        assertEquals(0, extractor.testGetCurrentDepth(params));
    }

    /** incrementDepth must return a NEW map and not mutate the input. */
    @Test
    public void test_incrementDepth_returnsNewMap() {
        final Map<String, String> original = new HashMap<>();
        original.put("foo", "bar");
        final Map<String, String> next = extractor.testIncrementDepth(original);

        assertFalse(original == next);
        // original is unchanged
        assertFalse(original.containsKey(AbstractExtractor.EXTRACTOR_DEPTH_KEY));
        assertEquals("bar", next.get("foo"));
        assertEquals("1", next.get(AbstractExtractor.EXTRACTOR_DEPTH_KEY));

        final Map<String, String> after = extractor.testIncrementDepth(next);
        assertEquals("2", after.get(AbstractExtractor.EXTRACTOR_DEPTH_KEY));
        // first map still says "1"
        assertEquals("1", next.get(AbstractExtractor.EXTRACTOR_DEPTH_KEY));
    }

    /** incrementDepth on null produces depth=1. */
    @Test
    public void test_incrementDepth_nullInput() {
        final Map<String, String> next = extractor.testIncrementDepth(null);
        assertNotNull(next);
        assertEquals("1", next.get(AbstractExtractor.EXTRACTOR_DEPTH_KEY));
    }

    /** checkDepth allows depths below the limit. */
    @Test
    public void test_checkDepth_belowLimit_passes() {
        final Map<String, String> params = new HashMap<>();
        params.put(AbstractExtractor.EXTRACTOR_DEPTH_KEY, "3");
        extractor.testCheckDepth(params, 10); // no throw
        extractor.testCheckDepth(null, 10);
    }

    /** checkDepth rejects depths at or above the limit. */
    @Test
    public void test_checkDepth_atOrAboveLimit_throws() {
        final Map<String, String> params = new HashMap<>();
        params.put(AbstractExtractor.EXTRACTOR_DEPTH_KEY, "10");
        try {
            extractor.testCheckDepth(params, 10);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("recursion depth"));
        }

        params.put(AbstractExtractor.EXTRACTOR_DEPTH_KEY, "99");
        try {
            extractor.testCheckDepth(params, 10);
            fail();
        } catch (final MaxLengthExceededException e) {
            // pass
        }
    }

    // -----------------------------------------------------------------------
    // isPathTraversal tests (C3 fix validation)
    // -----------------------------------------------------------------------

    /** null and empty are always traversals. */
    @Test
    public void test_isPathTraversal_nullAndEmpty() {
        assertTrue(extractor.testIsPathTraversal(null));
        assertTrue(extractor.testIsPathTraversal(""));
    }

    /** Drive letter prefix is always rejected. */
    @Test
    public void test_isPathTraversal_driveLetter() {
        assertTrue(extractor.testIsPathTraversal("C:\\foo"));
        assertTrue(extractor.testIsPathTraversal("C:foo"));
    }

    /** Leading slash (Unix absolute) is rejected. */
    @Test
    public void test_isPathTraversal_leadingSlash() {
        assertTrue(extractor.testIsPathTraversal("/etc/passwd"));
    }

    /** Leading backslash is rejected. */
    @Test
    public void test_isPathTraversal_leadingBackslash() {
        assertTrue(extractor.testIsPathTraversal("\\foo\\bar"));
    }

    /** Lone ".." is rejected. */
    @Test
    public void test_isPathTraversal_loneDotDot() {
        assertTrue(extractor.testIsPathTraversal(".."));
    }

    /** Classic traversal sequences are rejected. */
    @Test
    public void test_isPathTraversal_classicTraversal() {
        assertTrue(extractor.testIsPathTraversal("../../etc/passwd"));
        assertTrue(extractor.testIsPathTraversal("foo/../../etc/passwd"));
    }

    /** Safe name that resolves inside the root is allowed. */
    @Test
    public void test_isPathTraversal_safeRelativePath() {
        assertFalse(extractor.testIsPathTraversal("foo/bar.txt"));
        assertFalse(extractor.testIsPathTraversal("foo/../bar.txt")); // normalises to bar.txt
    }

    /**
     * Single-segment backslash traversal (C3 regression).
     * On Linux the path "a\..\..\etc" is a single opaque filename when
     * Paths.get() is called without pre-normalisation, so ".." segments are
     * not detected.  After unifying backslash to forward-slash before
     * Paths.get(), "a/../../etc" normalises to "../etc" which starts with
     * ".." and is correctly rejected.
     * Note: "a\.." unifies to "a/.." which normalises to the empty path
     * (current dir, i.e. the archive root) — that is safe and is NOT rejected.
     */
    @Test
    public void test_isPathTraversal_backslashSingleSegment() {
        // "a\..\..\etc" must be caught — escapes the archive root.
        assertTrue(extractor.testIsPathTraversal("a\\..\\..\\etc"));
        // Three levels up — definitely escapes.
        assertTrue(extractor.testIsPathTraversal("a\\..\\..\\..")); // escapes
        // "a\.." normalises to the archive root (current dir) — safe.
        assertFalse(extractor.testIsPathTraversal("a\\.."));
        // A purely safe backslash path: "foo\\bar.txt" → "foo/bar.txt" — safe.
        assertFalse(extractor.testIsPathTraversal("foo\\bar.txt"));
    }

    /** NUL character in path — should be rejected (InvalidPathException path). */
    @Test
    public void test_isPathTraversal_nulCharacter() {
        assertTrue(extractor.testIsPathTraversal("a\0b"));
    }

    // -----------------------------------------------------------------------
    // addOneSaturating (C2 fix validation)
    // -----------------------------------------------------------------------

    /** addOneSaturating returns value+1 for normal inputs. */
    @Test
    public void test_addOneSaturating_normalIncrement() {
        assertEquals(1L, extractor.testAddOneSaturating(0L));
        assertEquals(101L, extractor.testAddOneSaturating(100L));
        assertEquals(Long.MAX_VALUE - 1L, extractor.testAddOneSaturating(Long.MAX_VALUE - 2L));
    }

    /** addOneSaturating returns Long.MAX_VALUE when already at max. */
    @Test
    public void test_addOneSaturating_saturatesAtMax() {
        assertEquals(Long.MAX_VALUE, extractor.testAddOneSaturating(Long.MAX_VALUE));
        // (MAX-1)+1 = MAX naturally — not overflow.
        assertEquals(Long.MAX_VALUE, extractor.testAddOneSaturating(Long.MAX_VALUE - 1L));
        // Verify the result is positive (not wrapped to negative).
        assertTrue(extractor.testAddOneSaturating(Long.MAX_VALUE) > 0);
    }
}
