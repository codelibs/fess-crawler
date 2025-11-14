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
package org.codelibs.fess.crawler.helper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.Sitemap;
import org.codelibs.fess.crawler.entity.SitemapAlternateLink;
import org.codelibs.fess.crawler.entity.SitemapImage;
import org.codelibs.fess.crawler.entity.SitemapNews;
import org.codelibs.fess.crawler.entity.SitemapSet;
import org.codelibs.fess.crawler.entity.SitemapUrl;
import org.codelibs.fess.crawler.entity.SitemapVideo;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 *
 */
public class SitemapsHelperTest extends PlainTestCase {
    public SitemapsHelper sitemapsHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("sitemapsHelper", SitemapsHelper.class);
        sitemapsHelper = container.getComponent("sitemapsHelper");
    }

    public void test_parseXmlSitemaps() {
        final InputStream in = ResourceUtil.getResourceAsStream("sitemaps/sitemap1.xml");
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();
        assertEquals(5, sitemaps.length);
        assertTrue(sitemapSet.isUrlSet());
        assertFalse(sitemapSet.isIndex());

        assertEquals("2005-01-01", sitemaps[0].getLastmod());
        assertEquals("http://www.example.com/", sitemaps[0].getLoc());
        assertEquals("monthly", ((SitemapUrl) sitemaps[0]).getChangefreq());
        assertEquals("0.8", ((SitemapUrl) sitemaps[0]).getPriority());

        assertNull(sitemaps[1].getLastmod());
        assertEquals("http://www.example.com/catalog?item=12&desc=vacation_hawaii", sitemaps[1].getLoc());
        assertEquals("weekly", ((SitemapUrl) sitemaps[1]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[1]).getPriority());

        assertEquals("2004-12-23", sitemaps[2].getLastmod());
        assertEquals("http://www.example.com/catalog?item=73&desc=vacation_new_zealand", sitemaps[2].getLoc());
        assertEquals("weekly", ((SitemapUrl) sitemaps[2]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[2]).getPriority());

        assertEquals("2004-12-23T18:00:15+00:00", sitemaps[3].getLastmod());
        assertEquals("http://www.example.com/catalog?item=74&desc=vacation_newfoundland", sitemaps[3].getLoc());
        assertNull(((SitemapUrl) sitemaps[3]).getChangefreq());
        assertEquals("0.3", ((SitemapUrl) sitemaps[3]).getPriority());

        assertEquals("2004-11-23", sitemaps[4].getLastmod());
        assertEquals("http://www.example.com/catalog?item=83&desc=vacation_usa", sitemaps[4].getLoc());
        assertNull(((SitemapUrl) sitemaps[4]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[4]).getPriority());
    }

    public void test_parseXmlSitemapsGz() {
        final InputStream in = ResourceUtil.getResourceAsStream("sitemaps/sitemap1.xml.gz");
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();
        assertEquals(5, sitemaps.length);
        assertTrue(sitemapSet.isUrlSet());
        assertFalse(sitemapSet.isIndex());

        assertEquals("2005-01-01", sitemaps[0].getLastmod());
        assertEquals("http://www.example.com/", sitemaps[0].getLoc());
        assertEquals("monthly", ((SitemapUrl) sitemaps[0]).getChangefreq());
        assertEquals("0.8", ((SitemapUrl) sitemaps[0]).getPriority());

        assertNull(sitemaps[1].getLastmod());
        assertEquals("http://www.example.com/catalog?item=12&desc=vacation_hawaii", sitemaps[1].getLoc());
        assertEquals("weekly", ((SitemapUrl) sitemaps[1]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[1]).getPriority());

        assertEquals("2004-12-23", sitemaps[2].getLastmod());
        assertEquals("http://www.example.com/catalog?item=73&desc=vacation_new_zealand", sitemaps[2].getLoc());
        assertEquals("weekly", ((SitemapUrl) sitemaps[2]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[2]).getPriority());

        assertEquals("2004-12-23T18:00:15+00:00", sitemaps[3].getLastmod());
        assertEquals("http://www.example.com/catalog?item=74&desc=vacation_newfoundland", sitemaps[3].getLoc());
        assertNull(((SitemapUrl) sitemaps[3]).getChangefreq());
        assertEquals("0.3", ((SitemapUrl) sitemaps[3]).getPriority());

        assertEquals("2004-11-23", sitemaps[4].getLastmod());
        assertEquals("http://www.example.com/catalog?item=83&desc=vacation_usa", sitemaps[4].getLoc());
        assertNull(((SitemapUrl) sitemaps[4]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[4]).getPriority());
    }

    public void test_parseTextSitemaps() {
        final InputStream in = ResourceUtil.getResourceAsStream("sitemaps/sitemap1.txt");
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();
        assertEquals(5, sitemaps.length);
        assertTrue(sitemapSet.isUrlSet());
        assertFalse(sitemapSet.isIndex());

        assertNull(sitemaps[0].getLastmod());
        assertEquals("http://www.example.com/", sitemaps[0].getLoc());
        assertNull(((SitemapUrl) sitemaps[0]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[0]).getPriority());

        assertNull(sitemaps[1].getLastmod());
        assertEquals("http://www.example.com/catalog?item=12&desc=vacation_hawaii", sitemaps[1].getLoc());
        assertNull(((SitemapUrl) sitemaps[1]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[1]).getPriority());

        assertNull(sitemaps[2].getLastmod());
        assertEquals("http://www.example.com/catalog?item=73&desc=vacation_new_zealand", sitemaps[2].getLoc());
        assertNull(((SitemapUrl) sitemaps[2]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[2]).getPriority());

        assertNull(sitemaps[3].getLastmod());
        assertEquals("http://www.example.com/catalog?item=74&desc=vacation_newfoundland", sitemaps[3].getLoc());
        assertNull(((SitemapUrl) sitemaps[3]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[3]).getPriority());

        assertNull(sitemaps[4].getLastmod());
        assertEquals("http://www.example.com/catalog?item=83&desc=vacation_usa", sitemaps[4].getLoc());
        assertNull(((SitemapUrl) sitemaps[4]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[4]).getPriority());
    }

    public void test_parseXmlSitemapsIndex() {
        final InputStream in = ResourceUtil.getResourceAsStream("sitemaps/sitemap2.xml");
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();
        assertEquals(2, sitemaps.length);
        assertFalse(sitemapSet.isUrlSet());
        assertTrue(sitemapSet.isIndex());

        assertEquals("2004-10-01T18:23:17+00:00", sitemaps[0].getLastmod());
        assertEquals("http://www.example.com/sitemap1.xml.gz", sitemaps[0].getLoc());

        assertEquals("2005-01-01", sitemaps[1].getLastmod());
        assertEquals("http://www.example.com/sitemap2.xml.gz", sitemaps[1].getLoc());

    }

    public void test_parseXmlSitemapsIndexGz() {
        final InputStream in = ResourceUtil.getResourceAsStream("sitemaps/sitemap2.xml.gz");
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();
        assertEquals(2, sitemaps.length);
        assertFalse(sitemapSet.isUrlSet());
        assertTrue(sitemapSet.isIndex());

        assertEquals("2004-10-01T18:23:17+00:00", sitemaps[0].getLastmod());
        assertEquals("http://www.example.com/sitemap1.xml.gz", sitemaps[0].getLoc());

        assertEquals("2005-01-01", sitemaps[1].getLastmod());
        assertEquals("http://www.example.com/sitemap2.xml.gz", sitemaps[1].getLoc());

    }

    public void test_parseXmlSitemaps_invalid1() {
        final byte[] bytes = "".getBytes();
        final InputStream in = new ByteArrayInputStream(bytes);

        try {
            sitemapsHelper.parse(in);
            fail();
        } catch (final CrawlingAccessException e) {
            // NOP
        }
    }

    public void test_parseXmlSitemaps_invalid2() {
        final byte[] bytes = "test".getBytes();
        final InputStream in = new ByteArrayInputStream(bytes);

        try {
            sitemapsHelper.parse(in);
            fail();
        } catch (final CrawlingAccessException e) {
            // NOP
        }
    }

    public void test_parseXmlSitemaps_invalid3() {
        final byte[] bytes = "<urlset".getBytes();
        final InputStream in = new ByteArrayInputStream(bytes);

        try {
            sitemapsHelper.parse(in);
            fail();
        } catch (final CrawlingAccessException e) {
            // NOP
        }
    }

    public void test_parseXmlSitemaps_invalid4() {
        final byte[] bytes = "<sitemap".getBytes();
        final InputStream in = new ByteArrayInputStream(bytes);

        try {
            sitemapsHelper.parse(in);
            fail();
        } catch (final CrawlingAccessException e) {
            // NOP
        }
    }

    public void test_parseXmlSitemapsWithImages() {
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n"
                + "        xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\">\n" + "  <url>\n"
                + "    <loc>http://www.example.com/sample.html</loc>\n" + "    <image:image>\n"
                + "      <image:loc>http://www.example.com/image.jpg</image:loc>\n"
                + "      <image:caption>Sample image caption</image:caption>\n"
                + "      <image:title>Sample image title</image:title>\n"
                + "      <image:geo_location>Tokyo, Japan</image:geo_location>\n"
                + "      <image:license>http://www.example.com/license.txt</image:license>\n" + "    </image:image>\n" + "  </url>\n"
                + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();
        assertEquals(1, sitemaps.length);

        final SitemapUrl sitemapUrl = (SitemapUrl) sitemaps[0];
        assertEquals("http://www.example.com/sample.html", sitemapUrl.getLoc());
        assertEquals(1, sitemapUrl.getImages().size());

        final SitemapImage image = sitemapUrl.getImages().get(0);
        assertEquals("http://www.example.com/image.jpg", image.getLoc());
        assertEquals("Sample image caption", image.getCaption());
        assertEquals("Sample image title", image.getTitle());
        assertEquals("Tokyo, Japan", image.getGeoLocation());
        assertEquals("http://www.example.com/license.txt", image.getLicense());
    }

    public void test_parseXmlSitemapsWithVideos() {
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n"
                + "        xmlns:video=\"http://www.google.com/schemas/sitemap-video/1.1\">\n" + "  <url>\n"
                + "    <loc>http://www.example.com/videos/sample.html</loc>\n" + "    <video:video>\n"
                + "      <video:thumbnail_loc>http://www.example.com/thumbs/123.jpg</video:thumbnail_loc>\n"
                + "      <video:title>Sample video title</video:title>\n"
                + "      <video:description>Sample video description</video:description>\n"
                + "      <video:content_loc>http://www.example.com/video123.mp4</video:content_loc>\n"
                + "      <video:duration>600</video:duration>\n" + "    </video:video>\n" + "  </url>\n" + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();
        assertEquals(1, sitemaps.length);

        final SitemapUrl sitemapUrl = (SitemapUrl) sitemaps[0];
        assertEquals("http://www.example.com/videos/sample.html", sitemapUrl.getLoc());
        assertEquals(1, sitemapUrl.getVideos().size());

        final SitemapVideo video = sitemapUrl.getVideos().get(0);
        assertEquals("http://www.example.com/thumbs/123.jpg", video.getThumbnailLoc());
        assertEquals("Sample video title", video.getTitle());
        assertEquals("Sample video description", video.getDescription());
        assertEquals("http://www.example.com/video123.mp4", video.getContentLoc());
        assertEquals("600", video.getDuration());
    }

    public void test_parseXmlSitemapsWithNews() {
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n"
                + "        xmlns:news=\"http://www.google.com/schemas/sitemap-news/0.9\">\n" + "  <url>\n"
                + "    <loc>http://www.example.com/news/article.html</loc>\n" + "    <news:news>\n" + "      <news:publication>\n"
                + "        <news:name>Example Times</news:name>\n" + "        <news:language>en</news:language>\n"
                + "      </news:publication>\n" + "      <news:publication_date>2025-01-01</news:publication_date>\n"
                + "      <news:title>Sample news title</news:title>\n" + "      <news:keywords>sample, news, test</news:keywords>\n"
                + "    </news:news>\n" + "  </url>\n" + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();
        assertEquals(1, sitemaps.length);

        final SitemapUrl sitemapUrl = (SitemapUrl) sitemaps[0];
        assertEquals("http://www.example.com/news/article.html", sitemapUrl.getLoc());
        assertNotNull(sitemapUrl.getNews());

        final SitemapNews news = sitemapUrl.getNews();
        assertEquals("Example Times", news.getPublicationName());
        assertEquals("en", news.getPublicationLanguage());
        assertEquals("2025-01-01", news.getPublicationDate());
        assertEquals("Sample news title", news.getTitle());
        assertEquals("sample, news, test", news.getKeywords());
    }

    public void test_parseXmlSitemapsWithAlternateLinks() {
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n"
                + "        xmlns:xhtml=\"http://www.w3.org/1999/xhtml\">\n" + "  <url>\n"
                + "    <loc>http://www.example.com/page.html</loc>\n"
                + "    <xhtml:link rel=\"alternate\" hreflang=\"en\" href=\"http://www.example.com/en/page.html\" />\n"
                + "    <xhtml:link rel=\"alternate\" hreflang=\"ja\" href=\"http://www.example.com/ja/page.html\" />\n"
                + "    <xhtml:link rel=\"alternate\" hreflang=\"x-default\" href=\"http://www.example.com/page.html\" />\n"
                + "  </url>\n" + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();
        assertEquals(1, sitemaps.length);

        final SitemapUrl sitemapUrl = (SitemapUrl) sitemaps[0];
        assertEquals("http://www.example.com/page.html", sitemapUrl.getLoc());
        assertEquals(3, sitemapUrl.getAlternateLinks().size());

        final SitemapAlternateLink link1 = sitemapUrl.getAlternateLinks().get(0);
        assertEquals("en", link1.getHreflang());
        assertEquals("http://www.example.com/en/page.html", link1.getHref());

        final SitemapAlternateLink link2 = sitemapUrl.getAlternateLinks().get(1);
        assertEquals("ja", link2.getHreflang());
        assertEquals("http://www.example.com/ja/page.html", link2.getHref());

        final SitemapAlternateLink link3 = sitemapUrl.getAlternateLinks().get(2);
        assertEquals("x-default", link3.getHreflang());
        assertEquals("http://www.example.com/page.html", link3.getHref());
    }

    public void test_validation() {
        sitemapsHelper.setEnableValidation(true);

        // Valid URL
        assertTrue(sitemapsHelper.isValidUrl("http://www.example.com"));
        assertTrue(sitemapsHelper.isValidUrl("https://www.example.com"));

        // Invalid URL
        assertFalse(sitemapsHelper.isValidUrl(null));
        assertFalse(sitemapsHelper.isValidUrl(""));
        assertFalse(sitemapsHelper.isValidUrl("ftp://www.example.com"));
        assertFalse(sitemapsHelper.isValidUrl("http://" + "a".repeat(2048)));

        // Valid priority
        assertTrue(sitemapsHelper.isValidPriority("0.0"));
        assertTrue(sitemapsHelper.isValidPriority("0.5"));
        assertTrue(sitemapsHelper.isValidPriority("1.0"));
        assertTrue(sitemapsHelper.isValidPriority(null));
        assertTrue(sitemapsHelper.isValidPriority(""));

        // Invalid priority
        assertFalse(sitemapsHelper.isValidPriority("-0.1"));
        assertFalse(sitemapsHelper.isValidPriority("1.1"));
        assertFalse(sitemapsHelper.isValidPriority("abc"));

        // Valid changefreq
        assertTrue(sitemapsHelper.isValidChangefreq("always"));
        assertTrue(sitemapsHelper.isValidChangefreq("hourly"));
        assertTrue(sitemapsHelper.isValidChangefreq("daily"));
        assertTrue(sitemapsHelper.isValidChangefreq("weekly"));
        assertTrue(sitemapsHelper.isValidChangefreq("monthly"));
        assertTrue(sitemapsHelper.isValidChangefreq("yearly"));
        assertTrue(sitemapsHelper.isValidChangefreq("never"));
        assertTrue(sitemapsHelper.isValidChangefreq(null));
        assertTrue(sitemapsHelper.isValidChangefreq(""));

        // Invalid changefreq
        assertFalse(sitemapsHelper.isValidChangefreq("sometimes"));
        assertFalse(sitemapsHelper.isValidChangefreq("invalid"));

        // Valid date format
        assertTrue(sitemapsHelper.isValidDateFormat("2025-01-01"));
        assertTrue(sitemapsHelper.isValidDateFormat("2025-01-01T12:00:00+00:00"));
        assertTrue(sitemapsHelper.isValidDateFormat(null));
        assertTrue(sitemapsHelper.isValidDateFormat(""));

        // Invalid date format
        assertFalse(sitemapsHelper.isValidDateFormat("2025-1-1"));
        assertFalse(sitemapsHelper.isValidDateFormat("01-01-2025"));
        assertFalse(sitemapsHelper.isValidDateFormat("invalid"));
    }

    // ========== Error Tolerance Tests ==========

    public void test_parseXmlSitemaps_missingLocElement() {
        // URL entry without loc element should be skipped, but others should be parsed
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n"
                + "  <url>\n"
                + "    <lastmod>2025-01-01</lastmod>\n"
                + "    <changefreq>daily</changefreq>\n"
                + "    <priority>0.8</priority>\n"
                + "  </url>\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/valid.html</loc>\n"
                + "    <lastmod>2025-01-02</lastmod>\n"
                + "  </url>\n"
                + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();

        // Should parse successfully, skipping the entry without loc
        assertTrue(sitemaps.length >= 1);
        assertEquals("http://www.example.com/valid.html", sitemaps[sitemaps.length - 1].getLoc());
    }

    public void test_parseXmlSitemaps_emptyLocElement() {
        // URL entry with empty loc element should be skipped
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n"
                + "  <url>\n"
                + "    <loc></loc>\n"
                + "    <lastmod>2025-01-01</lastmod>\n"
                + "  </url>\n"
                + "  <url>\n"
                + "    <loc>   </loc>\n"
                + "    <lastmod>2025-01-02</lastmod>\n"
                + "  </url>\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/valid.html</loc>\n"
                + "    <lastmod>2025-01-03</lastmod>\n"
                + "  </url>\n"
                + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();

        // Should parse successfully, skipping entries with empty loc
        assertTrue(sitemaps.length >= 1);
        assertEquals("http://www.example.com/valid.html", sitemaps[sitemaps.length - 1].getLoc());
    }

    public void test_parseXmlSitemaps_mixedValidInvalid() {
        // Mix of valid and invalid entries should parse valid ones
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page1.html</loc>\n"
                + "    <lastmod>2025-01-01</lastmod>\n"
                + "    <priority>0.8</priority>\n"
                + "  </url>\n"
                + "  <url>\n"
                + "    <loc></loc>\n"
                + "  </url>\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page2.html</loc>\n"
                + "    <changefreq>daily</changefreq>\n"
                + "  </url>\n"
                + "  <url>\n"
                + "    <lastmod>2025-01-03</lastmod>\n"
                + "  </url>\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page3.html</loc>\n"
                + "  </url>\n"
                + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();

        // Should parse at least the 3 valid entries
        assertTrue(sitemaps.length >= 3);
        boolean foundPage1 = false;
        boolean foundPage2 = false;
        boolean foundPage3 = false;
        for (Sitemap sitemap : sitemaps) {
            if ("http://www.example.com/page1.html".equals(sitemap.getLoc())) {
                foundPage1 = true;
            } else if ("http://www.example.com/page2.html".equals(sitemap.getLoc())) {
                foundPage2 = true;
            } else if ("http://www.example.com/page3.html".equals(sitemap.getLoc())) {
                foundPage3 = true;
            }
        }
        assertTrue(foundPage1);
        assertTrue(foundPage2);
        assertTrue(foundPage3);
    }

    public void test_parseXmlSitemaps_withInvalidPriority() {
        // Invalid priority values should be preserved (not validated unless validation is enabled)
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page1.html</loc>\n"
                + "    <priority>1.5</priority>\n"
                + "  </url>\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page2.html</loc>\n"
                + "    <priority>-0.5</priority>\n"
                + "  </url>\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page3.html</loc>\n"
                + "    <priority>abc</priority>\n"
                + "  </url>\n"
                + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();

        // Should parse all entries and preserve priority values
        assertEquals(3, sitemaps.length);
        assertEquals("1.5", ((SitemapUrl) sitemaps[0]).getPriority());
        assertEquals("-0.5", ((SitemapUrl) sitemaps[1]).getPriority());
        assertEquals("abc", ((SitemapUrl) sitemaps[2]).getPriority());
    }

    public void test_parseXmlSitemaps_withInvalidChangefreq() {
        // Invalid changefreq values should be preserved
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page1.html</loc>\n"
                + "    <changefreq>sometimes</changefreq>\n"
                + "  </url>\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page2.html</loc>\n"
                + "    <changefreq>rarely</changefreq>\n"
                + "  </url>\n"
                + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();

        // Should parse all entries and preserve changefreq values
        assertEquals(2, sitemaps.length);
        assertEquals("sometimes", ((SitemapUrl) sitemaps[0]).getChangefreq());
        assertEquals("rarely", ((SitemapUrl) sitemaps[1]).getChangefreq());
    }

    public void test_parseXmlSitemaps_withInvalidDate() {
        // Invalid date formats should be preserved
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page1.html</loc>\n"
                + "    <lastmod>2025-1-1</lastmod>\n"
                + "  </url>\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page2.html</loc>\n"
                + "    <lastmod>01-01-2025</lastmod>\n"
                + "  </url>\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page3.html</loc>\n"
                + "    <lastmod>invalid-date</lastmod>\n"
                + "  </url>\n"
                + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();

        // Should parse all entries and preserve lastmod values
        assertEquals(3, sitemaps.length);
        assertEquals("2025-1-1", sitemaps[0].getLastmod());
        assertEquals("01-01-2025", sitemaps[1].getLastmod());
        assertEquals("invalid-date", sitemaps[2].getLastmod());
    }

    public void test_parseXmlSitemaps_withoutNamespace() {
        // Sitemap without namespace declaration should still be parsed
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset>\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page1.html</loc>\n"
                + "    <lastmod>2025-01-01</lastmod>\n"
                + "  </url>\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page2.html</loc>\n"
                + "  </url>\n"
                + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();

        // Should parse successfully even without namespace
        assertEquals(2, sitemaps.length);
        assertEquals("http://www.example.com/page1.html", sitemaps[0].getLoc());
        assertEquals("http://www.example.com/page2.html", sitemaps[1].getLoc());
    }

    public void test_parseXmlSitemaps_withUnknownElements() {
        // Sitemap with unknown/custom elements should ignore them and parse known elements
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n"
                + "        xmlns:custom=\"http://www.example.com/custom\">\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page1.html</loc>\n"
                + "    <custom:tag>Some custom value</custom:tag>\n"
                + "    <unknown>Unknown element</unknown>\n"
                + "    <lastmod>2025-01-01</lastmod>\n"
                + "  </url>\n"
                + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();

        // Should parse successfully, ignoring unknown elements
        assertEquals(1, sitemaps.length);
        assertEquals("http://www.example.com/page1.html", sitemaps[0].getLoc());
        assertEquals("2025-01-01", sitemaps[0].getLastmod());
    }

    public void test_parseXmlSitemaps_withExtraWhitespace() {
        // Sitemap with extra whitespace should be trimmed
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n"
                + "  <url>\n"
                + "    <loc>  http://www.example.com/page1.html  </loc>\n"
                + "    <lastmod>  2025-01-01  </lastmod>\n"
                + "    <changefreq>  daily  </changefreq>\n"
                + "    <priority>  0.8  </priority>\n"
                + "  </url>\n"
                + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();

        // Should parse and trim whitespace
        assertEquals(1, sitemaps.length);
        assertEquals("http://www.example.com/page1.html", sitemaps[0].getLoc());
        assertEquals("2025-01-01", sitemaps[0].getLastmod());
        assertEquals("daily", ((SitemapUrl) sitemaps[0]).getChangefreq());
        assertEquals("0.8", ((SitemapUrl) sitemaps[0]).getPriority());
    }

    public void test_parseXmlSitemaps_withBOM() {
        // Sitemap with UTF-8 BOM should be parsed correctly
        final byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page1.html</loc>\n"
                + "  </url>\n"
                + "</urlset>";
        final byte[] xmlBytes = xml.getBytes();
        final byte[] dataWithBOM = new byte[bom.length + xmlBytes.length];
        System.arraycopy(bom, 0, dataWithBOM, 0, bom.length);
        System.arraycopy(xmlBytes, 0, dataWithBOM, bom.length, xmlBytes.length);

        final InputStream in = new ByteArrayInputStream(dataWithBOM);
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();

        // Should parse successfully with BOM
        assertEquals(1, sitemaps.length);
        assertEquals("http://www.example.com/page1.html", sitemaps[0].getLoc());
    }

    public void test_parseXmlSitemaps_partiallyBrokenImage() {
        // Image extension with missing required fields should still be added
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n"
                + "        xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\">\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/page.html</loc>\n"
                + "    <image:image>\n"
                + "      <image:loc>http://www.example.com/image1.jpg</image:loc>\n"
                + "    </image:image>\n"
                + "    <image:image>\n"
                + "      <image:caption>Caption without loc</image:caption>\n"
                + "    </image:image>\n"
                + "    <image:image>\n"
                + "      <image:loc>http://www.example.com/image2.jpg</image:loc>\n"
                + "      <image:title>Valid image</image:title>\n"
                + "    </image:image>\n"
                + "  </url>\n"
                + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();

        // Should parse URL and all images (even incomplete ones)
        assertEquals(1, sitemaps.length);
        final SitemapUrl sitemapUrl = (SitemapUrl) sitemaps[0];
        assertEquals("http://www.example.com/page.html", sitemapUrl.getLoc());
        // All image entries should be preserved
        assertTrue(sitemapUrl.getImages().size() >= 2);
    }

    public void test_parseXmlSitemaps_partiallyBrokenVideo() {
        // Video extension with missing fields should still be added
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n"
                + "        xmlns:video=\"http://www.google.com/schemas/sitemap-video/1.1\">\n"
                + "  <url>\n"
                + "    <loc>http://www.example.com/video.html</loc>\n"
                + "    <video:video>\n"
                + "      <video:title>Video Title</video:title>\n"
                + "    </video:video>\n"
                + "    <video:video>\n"
                + "      <video:thumbnail_loc>http://www.example.com/thumb.jpg</video:thumbnail_loc>\n"
                + "      <video:title>Complete Video</video:title>\n"
                + "      <video:description>Description</video:description>\n"
                + "    </video:video>\n"
                + "  </url>\n"
                + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();

        // Should parse URL and all videos (even incomplete ones)
        assertEquals(1, sitemaps.length);
        final SitemapUrl sitemapUrl = (SitemapUrl) sitemaps[0];
        assertEquals("http://www.example.com/video.html", sitemapUrl.getLoc());
        assertTrue(sitemapUrl.getVideos().size() >= 1);
    }

    public void test_parseTextSitemaps_withInvalidLines() {
        // Text sitemap with invalid lines should skip them and parse valid ones
        final String text = "http://www.example.com/page1.html\n"
                + "not-a-url\n"
                + "ftp://invalid-protocol.com\n"
                + "http://www.example.com/page2.html\n"
                + "\n"
                + "   \n"
                + "http://www.example.com/page3.html\n"
                + "mailto:test@example.com\n"
                + "https://www.example.com/page4.html";
        final InputStream in = new ByteArrayInputStream(text.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();

        // Should parse only valid http/https URLs
        assertEquals(4, sitemaps.length);
        assertEquals("http://www.example.com/page1.html", sitemaps[0].getLoc());
        assertEquals("http://www.example.com/page2.html", sitemaps[1].getLoc());
        assertEquals("http://www.example.com/page3.html", sitemaps[2].getLoc());
        assertEquals("https://www.example.com/page4.html", sitemaps[3].getLoc());
    }

    public void test_parseXmlSitemapsIndex_missingLoc() {
        // Sitemap index with missing loc should skip that entry
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n"
                + "  <sitemap>\n"
                + "    <loc>http://www.example.com/sitemap1.xml</loc>\n"
                + "    <lastmod>2025-01-01</lastmod>\n"
                + "  </sitemap>\n"
                + "  <sitemap>\n"
                + "    <lastmod>2025-01-02</lastmod>\n"
                + "  </sitemap>\n"
                + "  <sitemap>\n"
                + "    <loc>http://www.example.com/sitemap2.xml</loc>\n"
                + "  </sitemap>\n"
                + "</sitemapindex>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();

        // Should parse valid entries
        assertTrue(sitemaps.length >= 2);
        assertTrue(sitemapSet.isIndex());
    }

    public void test_parseXmlSitemaps_withCDATA() {
        // Sitemap with CDATA sections should be parsed correctly
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n"
                + "  <url>\n"
                + "    <loc><![CDATA[http://www.example.com/page?foo=bar&baz=qux]]></loc>\n"
                + "    <lastmod>2025-01-01</lastmod>\n"
                + "  </url>\n"
                + "</urlset>";
        final InputStream in = new ByteArrayInputStream(xml.getBytes());
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();

        // Should parse CDATA correctly
        assertEquals(1, sitemaps.length);
        assertEquals("http://www.example.com/page?foo=bar&baz=qux", sitemaps[0].getLoc());
    }
}
