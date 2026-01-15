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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test for FilenameExtractor.
 */
public class FilenameExtractorTest extends PlainTestCase {

    private FilenameExtractor filenameExtractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("filenameExtractor", FilenameExtractor.class);
        filenameExtractor = container.getComponent("filenameExtractor");
    }

    @Test
    public void test_getText_withResourceName() {
        final InputStream in = new ByteArrayInputStream("dummy content".getBytes(StandardCharsets.UTF_8));
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "test-document.pdf");

        final ExtractData extractData = filenameExtractor.getText(in, params);

        assertNotNull(extractData);
        assertEquals("test-document.pdf", extractData.getContent());
    }

    @Test
    public void test_getText_withEmptyResourceName() {
        final InputStream in = new ByteArrayInputStream("dummy content".getBytes(StandardCharsets.UTF_8));
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "");

        final ExtractData extractData = filenameExtractor.getText(in, params);

        assertNotNull(extractData);
        assertEquals("", extractData.getContent());
    }

    @Test
    public void test_getText_withoutResourceName() {
        final InputStream in = new ByteArrayInputStream("dummy content".getBytes(StandardCharsets.UTF_8));
        final Map<String, String> params = new HashMap<>();

        final ExtractData extractData = filenameExtractor.getText(in, params);

        assertNotNull(extractData);
        assertEquals("", extractData.getContent());
    }

    @Test
    public void test_getText_nullParams() {
        final InputStream in = new ByteArrayInputStream("dummy content".getBytes(StandardCharsets.UTF_8));

        final ExtractData extractData = filenameExtractor.getText(in, null);

        assertNotNull(extractData);
        assertEquals("", extractData.getContent());
    }

    @Test
    public void test_getText_nullInputStream() {
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "test.pdf");

        try {
            filenameExtractor.getText(null, params);
            fail();
        } catch (final CrawlerSystemException e) {
            // Expected
        }
    }

    @Test
    public void test_getText_withSpecialCharactersInFilename() {
        final InputStream in = new ByteArrayInputStream("dummy content".getBytes(StandardCharsets.UTF_8));
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "特殊文字_ファイル名 (1).pdf");

        final ExtractData extractData = filenameExtractor.getText(in, params);

        assertNotNull(extractData);
        assertEquals("特殊文字_ファイル名 (1).pdf", extractData.getContent());
    }

    @Test
    public void test_getText_withPathInResourceName() {
        final InputStream in = new ByteArrayInputStream("dummy content".getBytes(StandardCharsets.UTF_8));
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "/path/to/document.pdf");

        final ExtractData extractData = filenameExtractor.getText(in, params);

        assertNotNull(extractData);
        assertEquals("/path/to/document.pdf", extractData.getContent());
    }

    @Test
    public void test_getText_withLongFilename() {
        final InputStream in = new ByteArrayInputStream("dummy content".getBytes(StandardCharsets.UTF_8));
        final Map<String, String> params = new HashMap<>();
        final String longFilename = "a".repeat(500) + ".pdf";
        params.put(ExtractData.RESOURCE_NAME_KEY, longFilename);

        final ExtractData extractData = filenameExtractor.getText(in, params);

        assertNotNull(extractData);
        assertEquals(longFilename, extractData.getContent());
    }

    @Test
    public void test_getText_inputStreamNotConsumed() throws Exception {
        final String originalContent = "This content should not be read";
        final InputStream in = new ByteArrayInputStream(originalContent.getBytes(StandardCharsets.UTF_8));
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "test.txt");

        filenameExtractor.getText(in, params);

        // Verify the input stream was not consumed
        final byte[] buffer = new byte[originalContent.length()];
        final int bytesRead = in.read(buffer);
        assertEquals(originalContent.length(), bytesRead);
        assertEquals(originalContent, new String(buffer, StandardCharsets.UTF_8));
    }

    @Test
    public void test_getText_withOtherParams() {
        final InputStream in = new ByteArrayInputStream("dummy content".getBytes(StandardCharsets.UTF_8));
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "document.pdf");
        params.put(ExtractData.URL, "http://example.com/document.pdf");
        params.put("custom_key", "custom_value");

        final ExtractData extractData = filenameExtractor.getText(in, params);

        assertNotNull(extractData);
        assertEquals("document.pdf", extractData.getContent());
    }

    @Test
    public void test_getText_withWhitespaceOnlyFilename() {
        final InputStream in = new ByteArrayInputStream("dummy content".getBytes(StandardCharsets.UTF_8));
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "   ");

        final ExtractData extractData = filenameExtractor.getText(in, params);

        assertNotNull(extractData);
        assertEquals("   ", extractData.getContent());
    }
}
