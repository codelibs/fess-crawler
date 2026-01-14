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
package org.codelibs.fess.crawler.entity;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for {@link ExtractData}.
 */
public class ExtractDataTest extends PlainTestCase {

    @Test
    public void test_defaultConstructor() {
        // Test default constructor
        ExtractData data = new ExtractData();
        assertNotNull(data);
        assertNull(data.getContent());
        assertNotNull(data.getKeySet());
        assertTrue(data.getKeySet().isEmpty());
    }

    @Test
    public void test_constructorWithContent() {
        // Test constructor with content
        String content = "Test content";
        ExtractData data = new ExtractData(content);

        assertNotNull(data);
        assertEquals(content, data.getContent());
        assertNotNull(data.getKeySet());
        assertTrue(data.getKeySet().isEmpty());
    }

    @Test
    public void test_contentGetterSetter() {
        // Test content getter/setter
        ExtractData data = new ExtractData();

        String content = "This is extracted content";
        data.setContent(content);
        assertEquals(content, data.getContent());

        String newContent = "New content";
        data.setContent(newContent);
        assertEquals(newContent, data.getContent());

        data.setContent(null);
        assertNull(data.getContent());
    }

    @Test
    public void test_putValue() {
        // Test putValue method
        ExtractData data = new ExtractData();

        data.putValue("title", "Test Title");
        data.putValue("author", "John Doe");

        String[] titleValues = data.getValues("title");
        assertNotNull(titleValues);
        assertEquals(1, titleValues.length);
        assertEquals("Test Title", titleValues[0]);

        String[] authorValues = data.getValues("author");
        assertNotNull(authorValues);
        assertEquals(1, authorValues.length);
        assertEquals("John Doe", authorValues[0]);
    }

    @Test
    public void test_putValues() {
        // Test putValues method
        ExtractData data = new ExtractData();

        String[] keywords = { "java", "crawler", "testing" };
        data.putValues("keywords", keywords);

        String[] retrievedKeywords = data.getValues("keywords");
        assertNotNull(retrievedKeywords);
        assertEquals(3, retrievedKeywords.length);
        assertEquals("java", retrievedKeywords[0]);
        assertEquals("crawler", retrievedKeywords[1]);
        assertEquals("testing", retrievedKeywords[2]);
    }

    @Test
    public void test_putValuesOverwrite() {
        // Test that putValues overwrites existing values
        ExtractData data = new ExtractData();

        data.putValue("key", "old_value");
        String[] oldValues = data.getValues("key");
        assertEquals(1, oldValues.length);
        assertEquals("old_value", oldValues[0]);

        String[] newValues = { "new_value1", "new_value2" };
        data.putValues("key", newValues);

        String[] retrievedValues = data.getValues("key");
        assertEquals(2, retrievedValues.length);
        assertEquals("new_value1", retrievedValues[0]);
        assertEquals("new_value2", retrievedValues[1]);
    }

    @Test
    public void test_getValuesNonExistentKey() {
        // Test getValues with non-existent key
        ExtractData data = new ExtractData();

        String[] values = data.getValues("nonexistent");
        assertNull(values);
    }

    @Test
    public void test_getKeySet() {
        // Test getKeySet method
        ExtractData data = new ExtractData();

        data.putValue("key1", "value1");
        data.putValue("key2", "value2");
        data.putValue("key3", "value3");

        Set<String> keySet = data.getKeySet();
        assertNotNull(keySet);
        assertEquals(3, keySet.size());
        assertTrue(keySet.contains("key1"));
        assertTrue(keySet.contains("key2"));
        assertTrue(keySet.contains("key3"));
    }

    @Test
    public void test_getKeySetEmpty() {
        // Test getKeySet on empty metadata
        ExtractData data = new ExtractData();

        Set<String> keySet = data.getKeySet();
        assertNotNull(keySet);
        assertTrue(keySet.isEmpty());
    }

    @Test
    public void test_predefinedConstants() {
        // Test predefined constants
        assertEquals("resourceName", ExtractData.RESOURCE_NAME_KEY);
        assertEquals("url", ExtractData.URL);
        assertEquals("file.passwords", ExtractData.FILE_PASSWORDS);
    }

    @Test
    public void test_usingPredefinedConstants() {
        // Test using predefined constants
        ExtractData data = new ExtractData();

        data.putValue(ExtractData.RESOURCE_NAME_KEY, "test.pdf");
        data.putValue(ExtractData.URL, "https://example.com/test.pdf");
        data.putValues(ExtractData.FILE_PASSWORDS, new String[] { "pass1", "pass2" });

        assertEquals("test.pdf", data.getValues(ExtractData.RESOURCE_NAME_KEY)[0]);
        assertEquals("https://example.com/test.pdf", data.getValues(ExtractData.URL)[0]);
        assertEquals(2, data.getValues(ExtractData.FILE_PASSWORDS).length);
    }

    @Test
    public void test_toString() {
        // Test toString method
        ExtractData data = new ExtractData("Test content");
        data.putValue("title", "Test Title");
        data.putValue("author", "John Doe");

        String result = data.toString();
        assertNotNull(result);
        assertTrue(result.contains("ExtractData"));
        assertTrue(result.contains("Test content"));
        assertTrue(result.contains("title"));
    }

    @Test
    public void test_toStringEmpty() {
        // Test toString with empty data
        ExtractData data = new ExtractData();

        String result = data.toString();
        assertNotNull(result);
        assertTrue(result.contains("ExtractData"));
    }

    @Test
    public void test_complexScenario() {
        // Test complex scenario with multiple operations
        ExtractData data = new ExtractData();

        // Set content
        String content = "This is a long extracted content from a PDF document. " + "It contains multiple paragraphs and rich text.";
        data.setContent(content);

        // Add single values
        data.putValue(ExtractData.RESOURCE_NAME_KEY, "document.pdf");
        data.putValue(ExtractData.URL, "https://example.com/docs/document.pdf");
        data.putValue("title", "Important Document");
        data.putValue("author", "Jane Smith");
        data.putValue("date", "2025-01-15");

        // Add multiple values
        String[] keywords = { "business", "report", "analysis", "2025" };
        data.putValues("keywords", keywords);

        String[] passwords = { "secret123", "pass456" };
        data.putValues(ExtractData.FILE_PASSWORDS, passwords);

        // Verify content
        assertEquals(content, data.getContent());

        // Verify single values
        assertEquals("document.pdf", data.getValues(ExtractData.RESOURCE_NAME_KEY)[0]);
        assertEquals("https://example.com/docs/document.pdf", data.getValues(ExtractData.URL)[0]);
        assertEquals("Important Document", data.getValues("title")[0]);
        assertEquals("Jane Smith", data.getValues("author")[0]);
        assertEquals("2025-01-15", data.getValues("date")[0]);

        // Verify multiple values
        String[] retrievedKeywords = data.getValues("keywords");
        assertEquals(4, retrievedKeywords.length);
        assertEquals("business", retrievedKeywords[0]);
        assertEquals("analysis", retrievedKeywords[2]);

        String[] retrievedPasswords = data.getValues(ExtractData.FILE_PASSWORDS);
        assertEquals(2, retrievedPasswords.length);

        // Verify key set
        Set<String> keySet = data.getKeySet();
        assertEquals(7, keySet.size());
        assertTrue(keySet.contains("title"));
        assertTrue(keySet.contains("author"));
        assertTrue(keySet.contains("keywords"));
    }

    @Test
    public void test_emptyArrayValues() {
        // Test with empty array
        ExtractData data = new ExtractData();

        String[] emptyArray = {};
        data.putValues("empty", emptyArray);

        String[] retrieved = data.getValues("empty");
        assertNotNull(retrieved);
        assertEquals(0, retrieved.length);
    }

    @Test
    public void test_nullValues() {
        // Test with null values
        ExtractData data = new ExtractData();

        data.putValue("key", null);

        String[] retrieved = data.getValues("key");
        assertNotNull(retrieved);
        assertEquals(1, retrieved.length);
        assertNull(retrieved[0]);
    }

    @Test
    public void test_multipleOperationsOnSameKey() {
        // Test multiple operations on the same key
        ExtractData data = new ExtractData();

        // First operation
        data.putValue("key", "value1");
        assertEquals("value1", data.getValues("key")[0]);

        // Second operation (overwrite)
        data.putValue("key", "value2");
        assertEquals("value2", data.getValues("key")[0]);
        assertEquals(1, data.getValues("key").length);

        // Third operation with array (overwrite)
        data.putValues("key", new String[] { "value3", "value4", "value5" });
        assertEquals(3, data.getValues("key").length);
        assertEquals("value3", data.getValues("key")[0]);
        assertEquals("value5", data.getValues("key")[2]);
    }

    @Test
    public void test_serializable() {
        // Test that ExtractData is Serializable
        ExtractData data = new ExtractData("content");
        assertTrue(data instanceof java.io.Serializable);
    }
}
