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

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Enhanced test class for FilenameExtractor covering edge cases and new functionality.
 */
public class FilenameExtractorEnhancedTest extends PlainTestCase {

    private FilenameExtractor filenameExtractor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final StandardCrawlerContainer container = new StandardCrawlerContainer()
                .singleton("filenameExtractor", FilenameExtractor.class);
        filenameExtractor = container.getComponent("filenameExtractor");
    }

    /**
     * Test extraction with valid filename in parameters.
     */
    public void test_getText_withValidFilename() {
        final InputStream in = new ByteArrayInputStream(new byte[0]);
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "test-document.pdf");

        final ExtractData result = filenameExtractor.getText(in, params);

        assertNotNull(result);
        assertEquals("test-document.pdf", result.getContent());
    }

    /**
     * Test extraction with null parameters map.
     */
    public void test_getText_withNullParams() {
        final InputStream in = new ByteArrayInputStream(new byte[0]);

        final ExtractData result = filenameExtractor.getText(in, null);

        assertNotNull(result);
        assertEquals("", result.getContent());
    }

    /**
     * Test extraction with empty parameters map.
     */
    public void test_getText_withEmptyParams() {
        final InputStream in = new ByteArrayInputStream(new byte[0]);
        final Map<String, String> params = new HashMap<>();

        final ExtractData result = filenameExtractor.getText(in, params);

        assertNotNull(result);
        assertEquals("", result.getContent());
    }

    /**
     * Test extraction with missing RESOURCE_NAME_KEY in parameters.
     */
    public void test_getText_withMissingResourceName() {
        final InputStream in = new ByteArrayInputStream(new byte[0]);
        final Map<String, String> params = new HashMap<>();
        params.put("other-key", "other-value");

        final ExtractData result = filenameExtractor.getText(in, params);

        assertNotNull(result);
        assertEquals("", result.getContent());
    }

    /**
     * Test extraction with null input stream throws CrawlerSystemException.
     */
    public void test_getText_withNullInputStream() {
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "test.txt");

        try {
            filenameExtractor.getText(null, params);
            fail("Expected CrawlerSystemException");
        } catch (final CrawlerSystemException e) {
            assertEquals("The inputstream is null.", e.getMessage());
        }
    }

    /**
     * Test extraction with filename containing special characters.
     */
    public void test_getText_withSpecialCharactersInFilename() {
        final InputStream in = new ByteArrayInputStream(new byte[0]);
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "ファイル名-2024年.docx");

        final ExtractData result = filenameExtractor.getText(in, params);

        assertNotNull(result);
        assertEquals("ファイル名-2024年.docx", result.getContent());
    }

    /**
     * Test extraction with very long filename.
     */
    public void test_getText_withLongFilename() {
        final InputStream in = new ByteArrayInputStream(new byte[0]);
        final StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 255; i++) {
            longName.append('a');
        }
        longName.append(".txt");

        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, longName.toString());

        final ExtractData result = filenameExtractor.getText(in, params);

        assertNotNull(result);
        assertEquals(longName.toString(), result.getContent());
    }

    /**
     * Test extraction with filename containing path separators.
     */
    public void test_getText_withPathInFilename() {
        final InputStream in = new ByteArrayInputStream(new byte[0]);
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "/path/to/document.pdf");

        final ExtractData result = filenameExtractor.getText(in, params);

        assertNotNull(result);
        assertEquals("/path/to/document.pdf", result.getContent());
    }

    /**
     * Test that input stream content is not read (only validated).
     */
    public void test_getText_doesNotReadInputStream() throws Exception {
        final byte[] testData = "This data should not be read".getBytes();
        final InputStream in = new ByteArrayInputStream(testData);
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "filename.txt");

        final ExtractData result = filenameExtractor.getText(in, params);

        assertNotNull(result);
        assertEquals("filename.txt", result.getContent());
        // Verify stream was not consumed
        assertEquals(testData.length, in.available());
    }

    /**
     * Test extraction with empty string filename.
     */
    public void test_getText_withEmptyFilename() {
        final InputStream in = new ByteArrayInputStream(new byte[0]);
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "");

        final ExtractData result = filenameExtractor.getText(in, params);

        assertNotNull(result);
        assertEquals("", result.getContent());
    }

    /**
     * Test extraction with whitespace-only filename.
     */
    public void test_getText_withWhitespaceFilename() {
        final InputStream in = new ByteArrayInputStream(new byte[0]);
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "   ");

        final ExtractData result = filenameExtractor.getText(in, params);

        assertNotNull(result);
        assertEquals("   ", result.getContent());
    }
}
