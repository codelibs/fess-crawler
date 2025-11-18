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
package org.codelibs.fess.crawler.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.entity.AccessResultDataImpl;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for XmlUtil.
 *
 * @author shinsuke
 */
public class XmlUtilTest extends PlainTestCase {

    public void test_escapeXml_null() {
        // Test null input
        assertEquals(StringUtil.EMPTY, XmlUtil.escapeXml(null));
    }

    public void test_escapeXml_empty() {
        // Test empty string
        assertEquals("", XmlUtil.escapeXml(""));
    }

    public void test_escapeXml_basic() {
        // Test basic XML escaping
        assertEquals("&lt;tag&gt;value&lt;/tag&gt;", XmlUtil.escapeXml("<tag>value</tag>"));
    }

    public void test_escapeXml_allSpecialChars() {
        // Test all special characters
        String input = "Test & < > \"quote\" 'apos'";
        String expected = "Test &amp; &lt; &gt; &quot;quote&quot; &apos;apos&apos;";
        assertEquals(expected, XmlUtil.escapeXml(input));
    }

    public void test_escapeXml_noSpecialChars() {
        // Test string without special characters
        String input = "Simple text without special characters";
        assertEquals(input, XmlUtil.escapeXml(input));
    }

    public void test_stripInvalidXMLCharacters_null() {
        // Test null input
        assertNull(XmlUtil.stripInvalidXMLCharacters(null));
    }

    public void test_stripInvalidXMLCharacters_empty() {
        // Test empty string
        assertEquals("", XmlUtil.stripInvalidXMLCharacters(""));
    }

    public void test_stripInvalidXMLCharacters_valid() {
        // Test valid XML characters
        String input = "Valid XML text 123\t\n\r";
        String result = XmlUtil.stripInvalidXMLCharacters(input);
        assertNotNull(result);
        assertTrue(result.contains("Valid XML text 123"));
    }

    public void test_stripInvalidXMLCharacters_withInvalidChars() {
        // Test with invalid control characters (0x1-0x8, 0xB, 0xC, 0xE-0x1F)
        String input = "Test\u0001\u0002\u0008Valid\u000B\u000C\u000EText";
        String result = XmlUtil.stripInvalidXMLCharacters(input);
        assertEquals("TestValidText", result);
    }

    public void test_stripInvalidXMLCharacters_validRanges() {
        // Test valid character ranges
        String input = "Test\u0020\u0009\u000A\u000D"
                + String.valueOf((char)0xD7FF)
                + String.valueOf((char)0xE000)
                + String.valueOf((char)0xFFFD);
        String result = XmlUtil.stripInvalidXMLCharacters(input);
        assertNotNull(result);
    }

    public void test_getDataMap_simpleField() throws Exception {
        // Test parsing simple field
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<doc>\n"
                + "  <field name=\"title\">Test Title</field>\n"
                + "</doc>";

        AccessResultDataImpl<?> accessResultData = new AccessResultDataImpl<>();
        accessResultData.setData(xml.getBytes("UTF-8"));
        accessResultData.setEncoding("UTF-8");

        Map<String, Object> dataMap = XmlUtil.getDataMap(accessResultData);
        assertNotNull(dataMap);
        assertEquals("Test Title", dataMap.get("title"));
    }

    public void test_getDataMap_multipleFields() throws Exception {
        // Test parsing multiple fields
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<doc>\n"
                + "  <field name=\"title\">Test Title</field>\n"
                + "  <field name=\"content\">Test Content</field>\n"
                + "  <field name=\"author\">Test Author</field>\n"
                + "</doc>";

        AccessResultDataImpl<?> accessResultData = new AccessResultDataImpl<>();
        accessResultData.setData(xml.getBytes("UTF-8"));
        accessResultData.setEncoding("UTF-8");

        Map<String, Object> dataMap = XmlUtil.getDataMap(accessResultData);
        assertNotNull(dataMap);
        assertEquals(3, dataMap.size());
        assertEquals("Test Title", dataMap.get("title"));
        assertEquals("Test Content", dataMap.get("content"));
        assertEquals("Test Author", dataMap.get("author"));
    }

    public void test_getDataMap_withList() throws Exception {
        // Test parsing field with list
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<doc>\n"
                + "  <field name=\"tags\">\n"
                + "    <list>\n"
                + "      <item>tag1</item>\n"
                + "      <item>tag2</item>\n"
                + "      <item>tag3</item>\n"
                + "    </list>\n"
                + "  </field>\n"
                + "</doc>";

        AccessResultDataImpl<?> accessResultData = new AccessResultDataImpl<>();
        accessResultData.setData(xml.getBytes("UTF-8"));
        accessResultData.setEncoding("UTF-8");

        Map<String, Object> dataMap = XmlUtil.getDataMap(accessResultData);
        assertNotNull(dataMap);
        assertTrue(dataMap.get("tags") instanceof List);
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) dataMap.get("tags");
        assertEquals(3, tags.size());
        assertEquals("tag1", tags.get(0));
        assertEquals("tag2", tags.get(1));
        assertEquals("tag3", tags.get(2));
    }

    public void test_getDataMap_emptyField() throws Exception {
        // Test parsing empty field
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<doc>\n"
                + "  <field name=\"empty\"></field>\n"
                + "</doc>";

        AccessResultDataImpl<?> accessResultData = new AccessResultDataImpl<>();
        accessResultData.setData(xml.getBytes("UTF-8"));
        accessResultData.setEncoding("UTF-8");

        Map<String, Object> dataMap = XmlUtil.getDataMap(accessResultData);
        assertNotNull(dataMap);
        assertEquals("", dataMap.get("empty"));
    }

    public void test_getDataMap_noNameAttribute() throws Exception {
        // Test parsing field without name attribute (should be ignored)
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<doc>\n"
                + "  <field>No Name</field>\n"
                + "  <field name=\"valid\">Valid Field</field>\n"
                + "</doc>";

        AccessResultDataImpl<?> accessResultData = new AccessResultDataImpl<>();
        accessResultData.setData(xml.getBytes("UTF-8"));
        accessResultData.setEncoding("UTF-8");

        Map<String, Object> dataMap = XmlUtil.getDataMap(accessResultData);
        assertNotNull(dataMap);
        assertEquals(1, dataMap.size());
        assertEquals("Valid Field", dataMap.get("valid"));
        assertFalse(dataMap.containsKey(null));
    }

    public void test_getDataMap_mixedFieldsAndLists() throws Exception {
        // Test parsing mixed fields and lists
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<doc>\n"
                + "  <field name=\"title\">Test Title</field>\n"
                + "  <field name=\"tags\">\n"
                + "    <list>\n"
                + "      <item>java</item>\n"
                + "      <item>xml</item>\n"
                + "    </list>\n"
                + "  </field>\n"
                + "  <field name=\"content\">Test Content</field>\n"
                + "</doc>";

        AccessResultDataImpl<?> accessResultData = new AccessResultDataImpl<>();
        accessResultData.setData(xml.getBytes("UTF-8"));
        accessResultData.setEncoding("UTF-8");

        Map<String, Object> dataMap = XmlUtil.getDataMap(accessResultData);
        assertNotNull(dataMap);
        assertEquals(3, dataMap.size());
        assertEquals("Test Title", dataMap.get("title"));
        assertEquals("Test Content", dataMap.get("content"));
        assertTrue(dataMap.get("tags") instanceof List);
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) dataMap.get("tags");
        assertEquals(2, tags.size());
    }
}
