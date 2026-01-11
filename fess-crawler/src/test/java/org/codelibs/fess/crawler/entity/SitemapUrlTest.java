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
 * Test class for SitemapUrl.
 * Tests all getters, setters, equals, hashCode, toString, and extensions.
 */
public class SitemapUrlTest extends PlainTestCase {

    /**
     * Test default values
     */
    public void test_defaultValues() {
        SitemapUrl url = new SitemapUrl();

        assertNull(url.getLoc());
        assertNull(url.getLastmod());
        assertNull(url.getChangefreq());
        assertNull(url.getPriority());
        assertNull(url.getNews());
        assertNotNull(url.getImages());
        assertTrue(url.getImages().isEmpty());
        assertNotNull(url.getVideos());
        assertTrue(url.getVideos().isEmpty());
        assertNotNull(url.getAlternateLinks());
        assertTrue(url.getAlternateLinks().isEmpty());
    }

    /**
     * Test loc getter and setter
     */
    public void test_loc() {
        SitemapUrl url = new SitemapUrl();

        url.setLoc("http://example.com/page1");
        assertEquals("http://example.com/page1", url.getLoc());

        url.setLoc("https://example.com/path/to/page?query=value");
        assertEquals("https://example.com/path/to/page?query=value", url.getLoc());

        url.setLoc(null);
        assertNull(url.getLoc());
    }

    /**
     * Test lastmod getter and setter
     */
    public void test_lastmod() {
        SitemapUrl url = new SitemapUrl();

        url.setLastmod("2024-01-15");
        assertEquals("2024-01-15", url.getLastmod());

        url.setLastmod("2024-01-15T10:30:00+00:00");
        assertEquals("2024-01-15T10:30:00+00:00", url.getLastmod());

        url.setLastmod(null);
        assertNull(url.getLastmod());
    }

    /**
     * Test changefreq getter and setter
     */
    public void test_changefreq() {
        SitemapUrl url = new SitemapUrl();

        String[] validValues = {"always", "hourly", "daily", "weekly", "monthly", "yearly", "never"};
        for (String value : validValues) {
            url.setChangefreq(value);
            assertEquals(value, url.getChangefreq());
        }

        url.setChangefreq(null);
        assertNull(url.getChangefreq());
    }

    /**
     * Test priority getter and setter
     */
    public void test_priority() {
        SitemapUrl url = new SitemapUrl();

        url.setPriority("0.5");
        assertEquals("0.5", url.getPriority());

        url.setPriority("1.0");
        assertEquals("1.0", url.getPriority());

        url.setPriority("0.0");
        assertEquals("0.0", url.getPriority());

        url.setPriority(null);
        assertNull(url.getPriority());
    }

    /**
     * Test images list management
     */
    public void test_images() {
        SitemapUrl url = new SitemapUrl();

        assertTrue(url.getImages().isEmpty());

        SitemapImage image1 = new SitemapImage();
        image1.setLoc("http://example.com/image1.jpg");
        url.addImage(image1);

        assertEquals(1, url.getImages().size());
        assertTrue(image1 == url.getImages().get(0));

        SitemapImage image2 = new SitemapImage();
        image2.setLoc("http://example.com/image2.jpg");
        url.addImage(image2);

        assertEquals(2, url.getImages().size());
    }

    /**
     * Test videos list management
     */
    public void test_videos() {
        SitemapUrl url = new SitemapUrl();

        assertTrue(url.getVideos().isEmpty());

        SitemapVideo video1 = new SitemapVideo();
        video1.setContentLoc("http://example.com/video1.mp4");
        url.addVideo(video1);

        assertEquals(1, url.getVideos().size());
        assertTrue(video1 == url.getVideos().get(0));
    }

    /**
     * Test news getter and setter
     */
    public void test_news() {
        SitemapUrl url = new SitemapUrl();

        assertNull(url.getNews());

        SitemapNews news = new SitemapNews();
        news.setTitle("Breaking News");
        url.setNews(news);

        assertTrue(news == url.getNews());
        assertEquals("Breaking News", url.getNews().getTitle());

        url.setNews(null);
        assertNull(url.getNews());
    }

    /**
     * Test alternate links management
     */
    public void test_alternateLinks() {
        SitemapUrl url = new SitemapUrl();

        assertTrue(url.getAlternateLinks().isEmpty());

        SitemapAlternateLink link1 = new SitemapAlternateLink();
        link1.setHref("http://example.com/en/page");
        link1.setHreflang("en");
        url.addAlternateLink(link1);

        assertEquals(1, url.getAlternateLinks().size());
        assertTrue(link1 == url.getAlternateLinks().get(0));
    }

    /**
     * Test equals - same object
     */
    public void test_equals_sameObject() {
        SitemapUrl url = new SitemapUrl();
        url.setLoc("http://example.com");

        assertTrue(url.equals(url));
    }

    /**
     * Test equals - equal objects
     */
    public void test_equals_equalObjects() {
        SitemapUrl url1 = new SitemapUrl();
        url1.setLoc("http://example.com");
        url1.setLastmod("2024-01-15");
        url1.setChangefreq("daily");
        url1.setPriority("0.8");

        SitemapUrl url2 = new SitemapUrl();
        url2.setLoc("http://example.com");
        url2.setLastmod("2024-01-15");
        url2.setChangefreq("daily");
        url2.setPriority("0.8");

        assertTrue(url1.equals(url2));
        assertTrue(url2.equals(url1));
    }

    /**
     * Test equals - different loc
     */
    public void test_equals_differentLoc() {
        SitemapUrl url1 = new SitemapUrl();
        url1.setLoc("http://example.com/page1");

        SitemapUrl url2 = new SitemapUrl();
        url2.setLoc("http://example.com/page2");

        assertFalse(url1.equals(url2));
    }

    /**
     * Test equals - null comparison
     */
    public void test_equals_null() {
        SitemapUrl url = new SitemapUrl();
        assertFalse(url.equals(null));
    }

    /**
     * Test equals - different type
     */
    public void test_equals_differentType() {
        SitemapUrl url = new SitemapUrl();
        url.setLoc("http://example.com");

        assertFalse(url.equals("http://example.com"));
        assertFalse(url.equals(new SitemapFile()));
    }

    /**
     * Test hashCode consistency
     */
    public void test_hashCode() {
        SitemapUrl url1 = new SitemapUrl();
        url1.setLoc("http://example.com");
        url1.setLastmod("2024-01-15");
        url1.setChangefreq("daily");
        url1.setPriority("0.8");

        SitemapUrl url2 = new SitemapUrl();
        url2.setLoc("http://example.com");
        url2.setLastmod("2024-01-15");
        url2.setChangefreq("daily");
        url2.setPriority("0.8");

        assertEquals(url1.hashCode(), url2.hashCode());
    }

    /**
     * Test toString
     */
    public void test_toString() {
        SitemapUrl url = new SitemapUrl();
        url.setLoc("http://example.com/page");
        url.setLastmod("2024-01-15");
        url.setChangefreq("weekly");
        url.setPriority("0.7");

        String result = url.toString();

        assertTrue(result.contains("SitemapUrl"));
        assertTrue(result.contains("http://example.com/page"));
        assertTrue(result.contains("2024-01-15"));
        assertTrue(result.contains("weekly"));
        assertTrue(result.contains("0.7"));
    }

    /**
     * Test implements Sitemap interface
     */
    public void test_implementsSitemap() {
        SitemapUrl url = new SitemapUrl();
        assertTrue(url instanceof Sitemap);
    }

    /**
     * Test serialization
     */
    public void test_serialization() throws Exception {
        SitemapUrl original = new SitemapUrl();
        original.setLoc("http://example.com/page");
        original.setLastmod("2024-01-15");
        original.setChangefreq("daily");
        original.setPriority("0.5");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        SitemapUrl deserialized = (SitemapUrl) ois.readObject();
        ois.close();

        assertEquals(original.getLoc(), deserialized.getLoc());
        assertEquals(original.getLastmod(), deserialized.getLastmod());
        assertEquals(original.getChangefreq(), deserialized.getChangefreq());
        assertEquals(original.getPriority(), deserialized.getPriority());
    }
}
