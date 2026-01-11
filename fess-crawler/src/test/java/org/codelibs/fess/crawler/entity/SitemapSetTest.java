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
import java.io.Serializable;

import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for SitemapSet.
 * Tests add/remove, type management, and serialization.
 */
public class SitemapSetTest extends PlainTestCase {

    /**
     * Test default values
     */
    public void test_defaultValues() {
        SitemapSet set = new SitemapSet();

        assertEquals(0, set.getSitemaps().length);
        assertTrue(set.isUrlSet());
        assertFalse(set.isIndex());
    }

    /**
     * Test type constants
     */
    public void test_typeConstants() {
        assertEquals("UrlSet", SitemapSet.URLSET);
        assertEquals("Index", SitemapSet.INDEX);
    }

    /**
     * Test addSitemap
     */
    public void test_addSitemap() {
        SitemapSet set = new SitemapSet();

        SitemapUrl url1 = new SitemapUrl();
        url1.setLoc("http://example.com/page1");
        set.addSitemap(url1);

        assertEquals(1, set.getSitemaps().length);
        assertTrue(url1 == set.getSitemaps()[0]);

        SitemapUrl url2 = new SitemapUrl();
        url2.setLoc("http://example.com/page2");
        set.addSitemap(url2);

        assertEquals(2, set.getSitemaps().length);
    }

    /**
     * Test removeSitemap
     */
    public void test_removeSitemap() {
        SitemapSet set = new SitemapSet();

        SitemapUrl url1 = new SitemapUrl();
        url1.setLoc("http://example.com/page1");
        SitemapUrl url2 = new SitemapUrl();
        url2.setLoc("http://example.com/page2");

        set.addSitemap(url1);
        set.addSitemap(url2);
        assertEquals(2, set.getSitemaps().length);

        set.removeSitemap(url1);
        assertEquals(1, set.getSitemaps().length);
        assertTrue(url2 == set.getSitemaps()[0]);

        set.removeSitemap(url2);
        assertEquals(0, set.getSitemaps().length);
    }

    /**
     * Test removeSitemap with non-existent element
     */
    public void test_removeSitemap_nonExistent() {
        SitemapSet set = new SitemapSet();

        SitemapUrl url1 = new SitemapUrl();
        url1.setLoc("http://example.com/page1");
        set.addSitemap(url1);

        SitemapUrl url2 = new SitemapUrl();
        url2.setLoc("http://example.com/page2");

        // Removing non-existent should not throw
        set.removeSitemap(url2);
        assertEquals(1, set.getSitemaps().length);
    }

    /**
     * Test setType to INDEX
     */
    public void test_setType_index() {
        SitemapSet set = new SitemapSet();

        assertTrue(set.isUrlSet());
        assertFalse(set.isIndex());

        set.setType(SitemapSet.INDEX);

        assertFalse(set.isUrlSet());
        assertTrue(set.isIndex());
    }

    /**
     * Test setType to URLSET
     */
    public void test_setType_urlset() {
        SitemapSet set = new SitemapSet();
        set.setType(SitemapSet.INDEX);

        assertTrue(set.isIndex());

        set.setType(SitemapSet.URLSET);

        assertTrue(set.isUrlSet());
        assertFalse(set.isIndex());
    }

    /**
     * Test setType with custom value
     */
    public void test_setType_customValue() {
        SitemapSet set = new SitemapSet();
        set.setType("Custom");

        assertFalse(set.isUrlSet());
        assertFalse(set.isIndex());
    }

    /**
     * Test getSitemaps returns array copy
     */
    public void test_getSitemaps_returnsArray() {
        SitemapSet set = new SitemapSet();

        SitemapUrl url = new SitemapUrl();
        url.setLoc("http://example.com/page");
        set.addSitemap(url);

        Sitemap[] sitemaps = set.getSitemaps();
        assertEquals(1, sitemaps.length);
        assertTrue(sitemaps instanceof Sitemap[]);
    }

    /**
     * Test with mixed sitemap types (SitemapUrl and SitemapFile)
     */
    public void test_mixedSitemapTypes() {
        SitemapSet set = new SitemapSet();

        SitemapUrl url = new SitemapUrl();
        url.setLoc("http://example.com/page");
        set.addSitemap(url);

        SitemapFile file = new SitemapFile();
        file.setLoc("http://example.com/sitemap_child.xml");
        set.addSitemap(file);

        assertEquals(2, set.getSitemaps().length);
        assertTrue(set.getSitemaps()[0] instanceof SitemapUrl);
        assertTrue(set.getSitemaps()[1] instanceof SitemapFile);
    }

    /**
     * Test toString
     */
    public void test_toString() {
        SitemapSet set = new SitemapSet();
        set.setType(SitemapSet.INDEX);

        SitemapFile file = new SitemapFile();
        file.setLoc("http://example.com/sitemap.xml");
        set.addSitemap(file);

        String result = set.toString();

        assertTrue(result.contains("SitemapSet"));
        assertTrue(result.contains("Index"));
    }

    /**
     * Test implements Serializable
     */
    public void test_implementsSerializable() {
        SitemapSet set = new SitemapSet();
        assertTrue(set instanceof Serializable);
    }

    /**
     * Test serialization
     */
    public void test_serialization() throws Exception {
        SitemapSet original = new SitemapSet();
        original.setType(SitemapSet.INDEX);

        SitemapFile file = new SitemapFile();
        file.setLoc("http://example.com/sitemap.xml");
        original.addSitemap(file);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        SitemapSet deserialized = (SitemapSet) ois.readObject();
        ois.close();

        assertTrue(deserialized.isIndex());
        assertEquals(1, deserialized.getSitemaps().length);
        assertEquals("http://example.com/sitemap.xml", deserialized.getSitemaps()[0].getLoc());
    }

    /**
     * Test large number of sitemaps
     */
    public void test_largeSitemapSet() {
        SitemapSet set = new SitemapSet();

        for (int i = 0; i < 1000; i++) {
            SitemapUrl url = new SitemapUrl();
            url.setLoc("http://example.com/page" + i);
            set.addSitemap(url);
        }

        assertEquals(1000, set.getSitemaps().length);
    }

    /**
     * Test typical sitemap index use case
     */
    public void test_sitemapIndexUseCase() {
        SitemapSet indexSet = new SitemapSet();
        indexSet.setType(SitemapSet.INDEX);

        // Add child sitemaps
        for (int i = 1; i <= 5; i++) {
            SitemapFile child = new SitemapFile();
            child.setLoc("http://example.com/sitemap_" + i + ".xml.gz");
            child.setLastmod("2024-01-" + String.format("%02d", i));
            indexSet.addSitemap(child);
        }

        assertTrue(indexSet.isIndex());
        assertEquals(5, indexSet.getSitemaps().length);
    }

    /**
     * Test typical URL set use case
     */
    public void test_urlSetUseCase() {
        SitemapSet urlSet = new SitemapSet();
        // Default is URLSET

        // Add URLs
        SitemapUrl url1 = new SitemapUrl();
        url1.setLoc("http://example.com/");
        url1.setPriority("1.0");
        url1.setChangefreq("daily");
        urlSet.addSitemap(url1);

        SitemapUrl url2 = new SitemapUrl();
        url2.setLoc("http://example.com/about");
        url2.setPriority("0.8");
        url2.setChangefreq("monthly");
        urlSet.addSitemap(url2);

        assertTrue(urlSet.isUrlSet());
        assertEquals(2, urlSet.getSitemaps().length);
    }
}
