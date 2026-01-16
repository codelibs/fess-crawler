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
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test for PasswordBasedExtractor abstract class.
 */
public class PasswordBasedExtractorTest extends PlainTestCase {

    private TestablePasswordBasedExtractor extractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        extractor = new TestablePasswordBasedExtractor();
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        if (extractor != null) {
            extractor.clearPasswords();
        }
        super.tearDown(testInfo);
    }

    @Test
    public void test_addPassword_and_getPassword_matchingUrl() {
        extractor.addPassword(".*\\.pdf$", "pdfPassword123");

        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "http://example.com/document.pdf");

        final String password = extractor.getPassword(params);
        assertEquals("pdfPassword123", password);
    }

    @Test
    public void test_addPassword_and_getPassword_matchingResourceName() {
        extractor.addPassword(".*secret.*", "secretPassword");

        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, "my-secret-file.docx");

        final String password = extractor.getPassword(params);
        assertEquals("secretPassword", password);
    }

    @Test
    public void test_getPassword_urlTakesPrecedenceOverResourceName() {
        extractor.addPassword(".*\\.pdf$", "pdfPassword");
        extractor.addPassword(".*\\.docx$", "docxPassword");

        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "http://example.com/document.pdf");
        params.put(ExtractData.RESOURCE_NAME_KEY, "file.docx");

        // URL should take precedence
        final String password = extractor.getPassword(params);
        assertEquals("pdfPassword", password);
    }

    @Test
    public void test_getPassword_noMatch_returnsNull() {
        extractor.addPassword(".*\\.pdf$", "pdfPassword");

        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "http://example.com/document.docx");

        final String password = extractor.getPassword(params);
        assertNull(password);
    }

    @Test
    public void test_getPassword_nullParams_returnsNull() {
        extractor.addPassword(".*\\.pdf$", "pdfPassword");

        final String password = extractor.getPassword(null);
        assertNull(password);
    }

    @Test
    public void test_getPassword_emptyParams_returnsNull() {
        extractor.addPassword(".*\\.pdf$", "pdfPassword");

        final Map<String, String> params = new HashMap<>();

        final String password = extractor.getPassword(params);
        assertNull(password);
    }

    @Test
    public void test_getPassword_emptyPasswordMap_returnsNull() {
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "http://example.com/document.pdf");

        final String password = extractor.getPassword(params);
        assertNull(password);
    }

    @Test
    public void test_getPassword_withFilePasswordsJson() {
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "http://example.com/secure.pdf");
        params.put(ExtractData.FILE_PASSWORDS, "{\".*\\\\.pdf$\": \"jsonPassword\"}");

        final String password = extractor.getPassword(params);
        assertEquals("jsonPassword", password);
    }

    @Test
    public void test_getPassword_filePasswordsJson_noMatch() {
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "http://example.com/document.docx");
        params.put(ExtractData.FILE_PASSWORDS, "{\".*\\\\.pdf$\": \"jsonPassword\"}");

        final String password = extractor.getPassword(params);
        assertNull(password);
    }

    @Test
    public void test_getPassword_filePasswordsJson_invalidJson() {
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "http://example.com/document.pdf");
        params.put(ExtractData.FILE_PASSWORDS, "invalid json");

        // Should not throw exception, just return null
        final String password = extractor.getPassword(params);
        assertNull(password);
    }

    @Test
    public void test_getPassword_staticPasswordTakesPrecedence() {
        extractor.addPassword(".*\\.pdf$", "staticPassword");

        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "http://example.com/document.pdf");
        params.put(ExtractData.FILE_PASSWORDS, "{\".*\\\\.pdf$\": \"jsonPassword\"}");

        // Static password should be checked first
        final String password = extractor.getPassword(params);
        assertEquals("staticPassword", password);
    }

    @Test
    public void test_getPassword_multiplePatterns_firstMatch() {
        extractor.addPassword(".*document.*", "docPassword");
        extractor.addPassword(".*\\.pdf$", "pdfPassword");

        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "http://example.com/document.pdf");

        // One of the matching patterns should be returned
        final String password = extractor.getPassword(params);
        assertTrue("docPassword".equals(password) || "pdfPassword".equals(password));
    }

    @Test
    public void test_clearPasswords() {
        extractor.addPassword(".*\\.pdf$", "pdfPassword");

        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "http://example.com/document.pdf");

        // Verify password is set
        assertEquals("pdfPassword", extractor.getPassword(params));

        // Clear passwords
        extractor.clearPasswords();

        // Verify password is cleared
        assertNull(extractor.getPassword(params));
    }

    @Test
    public void test_getPassword_caseInsensitiveRegex() {
        extractor.addPassword("(?i).*\\.PDF$", "pdfPassword");

        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "http://example.com/DOCUMENT.PDF");

        final String password = extractor.getPassword(params);
        assertEquals("pdfPassword", password);
    }

    @Test
    public void test_getPassword_complexRegex() {
        extractor.addPassword("https?://secure\\.example\\.com/protected/.*", "securePassword");

        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "https://secure.example.com/protected/file.pdf");

        final String password = extractor.getPassword(params);
        assertEquals("securePassword", password);
    }

    @Test
    public void test_getPassword_filePasswordsJsonCaching() {
        final String jsonPasswords = "{\".*\\\\.pdf$\": \"cachedPassword\"}";

        final Map<String, String> params1 = new HashMap<>();
        params1.put(ExtractData.URL, "http://example.com/doc1.pdf");
        params1.put(ExtractData.FILE_PASSWORDS, jsonPasswords);

        final Map<String, String> params2 = new HashMap<>();
        params2.put(ExtractData.URL, "http://example.com/doc2.pdf");
        params2.put(ExtractData.FILE_PASSWORDS, jsonPasswords);

        // Both calls should use the cached parsed passwords
        assertEquals("cachedPassword", extractor.getPassword(params1));
        assertEquals("cachedPassword", extractor.getPassword(params2));
    }

    @Test
    public void test_getPassword_urlWithSpecialCharacters() {
        extractor.addPassword(".*file%20name.*", "specialPassword");

        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "http://example.com/file%20name.pdf");

        final String password = extractor.getPassword(params);
        assertEquals("specialPassword", password);
    }

    @Test
    public void test_getPassword_emptyUrl() {
        extractor.addPassword(".*\\.pdf$", "pdfPassword");

        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "");
        params.put(ExtractData.RESOURCE_NAME_KEY, "document.pdf");

        // Should fall back to resource name when URL is empty
        final String password = extractor.getPassword(params);
        assertEquals("pdfPassword", password);
    }

    /**
     * Testable implementation of PasswordBasedExtractor for unit testing.
     */
    private static class TestablePasswordBasedExtractor extends PasswordBasedExtractor {
        @Override
        public ExtractData getText(final InputStream in, final Map<String, String> params) {
            // Simple implementation for testing
            return new ExtractData("test content");
        }
    }
}
