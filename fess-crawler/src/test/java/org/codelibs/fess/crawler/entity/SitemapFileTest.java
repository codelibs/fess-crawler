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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for SitemapFile.
 * Tests all getters, setters, equals, hashCode, and toString.
 */
public class SitemapFileTest extends PlainTestCase {

    /**
     * Test default values
     */
    public void test_defaultValues() {
        SitemapFile file = new SitemapFile();

        assertNull(file.getLoc());
        assertNull(file.getLastmod());
    }

    /**
     * Test loc getter and setter
     */
    public void test_loc() {
        SitemapFile file = new SitemapFile();

        file.setLoc("http://example.com/sitemap.xml");
        assertEquals("http://example.com/sitemap.xml", file.getLoc());

        file.setLoc("http://example.com/sitemap_1.xml.gz");
        assertEquals("http://example.com/sitemap_1.xml.gz", file.getLoc());

        file.setLoc(null);
        assertNull(file.getLoc());
    }

    /**
     * Test lastmod getter and setter
     */
    public void test_lastmod() {
        SitemapFile file = new SitemapFile();

        file.setLastmod("2024-01-15");
        assertEquals("2024-01-15", file.getLastmod());

        file.setLastmod("2024-01-15T10:30:00Z");
        assertEquals("2024-01-15T10:30:00Z", file.getLastmod());

        file.setLastmod(null);
        assertNull(file.getLastmod());
    }

    /**
     * Test equals - same object
     */
    public void test_equals_sameObject() {
        SitemapFile file = new SitemapFile();
        file.setLoc("http://example.com/sitemap.xml");

        assertTrue(file.equals(file));
    }

    /**
     * Test equals - equal objects
     */
    public void test_equals_equalObjects() {
        SitemapFile file1 = new SitemapFile();
        file1.setLoc("http://example.com/sitemap.xml");
        file1.setLastmod("2024-01-15");

        SitemapFile file2 = new SitemapFile();
        file2.setLoc("http://example.com/sitemap.xml");
        file2.setLastmod("2024-01-15");

        assertTrue(file1.equals(file2));
        assertTrue(file2.equals(file1));
    }

    /**
     * Test equals - different loc
     */
    public void test_equals_differentLoc() {
        SitemapFile file1 = new SitemapFile();
        file1.setLoc("http://example.com/sitemap1.xml");

        SitemapFile file2 = new SitemapFile();
        file2.setLoc("http://example.com/sitemap2.xml");

        assertFalse(file1.equals(file2));
    }

    /**
     * Test equals - different lastmod
     */
    public void test_equals_differentLastmod() {
        SitemapFile file1 = new SitemapFile();
        file1.setLoc("http://example.com/sitemap.xml");
        file1.setLastmod("2024-01-15");

        SitemapFile file2 = new SitemapFile();
        file2.setLoc("http://example.com/sitemap.xml");
        file2.setLastmod("2024-01-16");

        assertFalse(file1.equals(file2));
    }

    /**
     * Test equals - null comparison
     */
    public void test_equals_null() {
        SitemapFile file = new SitemapFile();
        assertFalse(file.equals(null));
    }

    /**
     * Test equals - different type
     */
    public void test_equals_differentType() {
        SitemapFile file = new SitemapFile();
        file.setLoc("http://example.com/sitemap.xml");

        assertFalse(file.equals("http://example.com/sitemap.xml"));
        assertFalse(file.equals(new SitemapUrl()));
    }

    /**
     * Test hashCode consistency
     */
    public void test_hashCode() {
        SitemapFile file1 = new SitemapFile();
        file1.setLoc("http://example.com/sitemap.xml");
        file1.setLastmod("2024-01-15");

        SitemapFile file2 = new SitemapFile();
        file2.setLoc("http://example.com/sitemap.xml");
        file2.setLastmod("2024-01-15");

        assertEquals(file1.hashCode(), file2.hashCode());
    }

    /**
     * Test hashCode with null values
     */
    public void test_hashCode_nullValues() {
        SitemapFile file = new SitemapFile();
        // Should not throw
        int hash = file.hashCode();
        assertNotNull(hash);
    }

    /**
     * Test toString
     */
    public void test_toString() {
        SitemapFile file = new SitemapFile();
        file.setLoc("http://example.com/sitemap.xml");
        file.setLastmod("2024-01-15");

        String result = file.toString();

        assertTrue(result.contains("SitemapFile"));
        assertTrue(result.contains("http://example.com/sitemap.xml"));
        assertTrue(result.contains("2024-01-15"));
    }

    /**
     * Test implements Sitemap interface
     */
    public void test_implementsSitemap() {
        SitemapFile file = new SitemapFile();
        assertTrue(file instanceof Sitemap);
    }

    /**
     * Test serialization
     */
    public void test_serialization() throws Exception {
        SitemapFile original = new SitemapFile();
        original.setLoc("http://example.com/sitemap.xml");
        original.setLastmod("2024-01-15");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        SitemapFile deserialized = (SitemapFile) ois.readObject();
        ois.close();

        assertEquals(original.getLoc(), deserialized.getLoc());
        assertEquals(original.getLastmod(), deserialized.getLastmod());
        assertTrue(original.equals(deserialized));
    }
}
