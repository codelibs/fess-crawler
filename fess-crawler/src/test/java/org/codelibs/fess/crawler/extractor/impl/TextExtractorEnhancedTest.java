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
import java.io.IOException;
import java.io.InputStream;

import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Enhanced test class for TextExtractor covering improved error messages and edge cases.
 */
public class TextExtractorEnhancedTest extends PlainTestCase {

    private TextExtractor textExtractor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("textExtractor", TextExtractor.class);
        textExtractor = container.getComponent("textExtractor");
    }

    /**
     * Test successful text extraction with default encoding (UTF-8).
     */
    public void test_getText_withDefaultEncoding() {
        final String testContent = "Test content テスト 日本語";
        final InputStream in = new ByteArrayInputStream(testContent.getBytes());

        final ExtractData result = textExtractor.getText(in, null);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().contains("Test content"));
    }

    /**
     * Test successful text extraction with custom encoding.
     */
    public void test_getText_withCustomEncoding() {
        textExtractor.setEncoding("UTF-8");
        final String testContent = "テスト内容";
        final InputStream in = new ByteArrayInputStream(testContent.getBytes());

        final ExtractData result = textExtractor.getText(in, null);

        assertNotNull(result);
        assertNotNull(result.getContent());
    }

    /**
     * Test that null input stream throws CrawlerSystemException with correct message.
     */
    public void test_getText_nullInputStream_throwsWithMessage() {
        try {
            textExtractor.getText(null, null);
            fail("Expected CrawlerSystemException");
        } catch (final CrawlerSystemException e) {
            assertEquals("The inputstream is null.", e.getMessage());
        }
    }

    /**
     * Test that extraction error includes encoding information in the error message.
     */
    public void test_getText_extractionError_includesEncodingInMessage() {
        // Create a stream that will cause an error during reading
        final InputStream errorStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Simulated read error");
            }
        };

        final String customEncoding = "ISO-8859-1";
        textExtractor.setEncoding(customEncoding);

        try {
            textExtractor.getText(errorStream, null);
            fail("Expected ExtractException");
        } catch (final ExtractException e) {
            assertTrue("Error message should contain encoding information", e.getMessage().contains("encoding"));
            assertTrue("Error message should contain the specific encoding", e.getMessage().contains(customEncoding));
            assertTrue("Error message should indicate extraction failure", e.getMessage().contains("Failed to extract"));
        } finally {
            // Reset to default encoding
            textExtractor.setEncoding("UTF-8");
        }
    }

    /**
     * Test extraction with empty input stream.
     */
    public void test_getText_emptyInputStream_returnsEmptyContent() {
        final InputStream in = new ByteArrayInputStream(new byte[0]);

        final ExtractData result = textExtractor.getText(in, null);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue("Empty input should produce empty content", result.getContent().isEmpty());
    }

    /**
     * Test extraction with large text content.
     */
    public void test_getText_largeContent_extractsSuccessfully() {
        final StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeContent.append("Line ").append(i).append(" with some content\n");
        }
        final InputStream in = new ByteArrayInputStream(largeContent.toString().getBytes());

        final ExtractData result = textExtractor.getText(in, null);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue("Should contain line 0", result.getContent().contains("Line 0"));
        assertTrue("Should contain line 9999", result.getContent().contains("Line 9999"));
    }

    /**
     * Test extraction with various Unicode characters.
     */
    public void test_getText_unicodeContent_extractsCorrectly() {
        final String unicodeContent = "Hello 世界 مرحبا мир שלום";
        final InputStream in = new ByteArrayInputStream(unicodeContent.getBytes());

        final ExtractData result = textExtractor.getText(in, null);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue("Should preserve Unicode characters", result.getContent().contains("世界"));
    }

    /**
     * Test that encoding can be changed and applied correctly.
     */
    public void test_encoding_canBeChangedAndApplied() {
        final String originalEncoding = textExtractor.getEncoding();
        assertEquals("UTF-8", originalEncoding);

        textExtractor.setEncoding("ISO-8859-1");
        assertEquals("ISO-8859-1", textExtractor.getEncoding());

        // Reset
        textExtractor.setEncoding("UTF-8");
        assertEquals("UTF-8", textExtractor.getEncoding());
    }

    /**
     * Test extraction with actual file resource.
     */
    public void test_getText_withFileResource_extractsSuccessfully() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.txt");

        final ExtractData result = textExtractor.getText(in, null);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue("Should contain test content", result.getContent().contains("テスト"));
    }

    /**
     * Test that error message format is consistent and descriptive.
     */
    public void test_errorMessage_formatIsDescriptive() {
        final InputStream errorStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Test error");
            }
        };

        try {
            textExtractor.getText(errorStream, null);
            fail("Expected ExtractException");
        } catch (final ExtractException e) {
            final String message = e.getMessage();
            assertNotNull("Error message should not be null", message);
            assertFalse("Error message should not be empty", message.trim().isEmpty());
            assertTrue("Error message should start with action verb", message.startsWith("Failed to extract"));
            assertTrue("Error message should contain 'text content'", message.contains("text content"));
        }
    }

    /**
     * Test extraction with special characters and control characters.
     */
    public void test_getText_withSpecialCharacters_handlesCorrectly() {
        final String specialContent = "Tab\there\nNewline\rCarriage return\0Null byte";
        final InputStream in = new ByteArrayInputStream(specialContent.getBytes());

        final ExtractData result = textExtractor.getText(in, null);

        assertNotNull(result);
        assertNotNull(result.getContent());
    }

    /**
     * Test that parameters map is accepted but not required.
     */
    public void test_getText_acceptsNullParameters() {
        final InputStream in = new ByteArrayInputStream("test".getBytes());

        final ExtractData result = textExtractor.getText(in, null);

        assertNotNull(result);
        assertNotNull(result.getContent());
    }

    /**
     * Test extraction with whitespace-only content.
     */
    public void test_getText_whitespaceOnly_extractsAsIs() {
        final String whitespaceContent = "   \t\n\r   ";
        final InputStream in = new ByteArrayInputStream(whitespaceContent.getBytes());

        final ExtractData result = textExtractor.getText(in, null);

        assertNotNull(result);
        assertNotNull(result.getContent());
    }
}
