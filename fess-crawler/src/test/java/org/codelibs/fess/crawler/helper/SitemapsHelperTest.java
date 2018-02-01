/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
import org.codelibs.fess.crawler.entity.SitemapSet;
import org.codelibs.fess.crawler.entity.SitemapUrl;
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
        StandardCrawlerContainer container = new StandardCrawlerContainer()
                .singleton("sitemapsHelper", SitemapsHelper.class);
        sitemapsHelper = container.getComponent("sitemapsHelper");
    }

    public void test_parseXmlSitemaps() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("sitemaps/sitemap1.xml");
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
        assertEquals(
                "http://www.example.com/catalog?item=12&desc=vacation_hawaii",
                sitemaps[1].getLoc());
        assertEquals("weekly", ((SitemapUrl) sitemaps[1]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[1]).getPriority());

        assertEquals("2004-12-23", sitemaps[2].getLastmod());
        assertEquals(
                "http://www.example.com/catalog?item=73&desc=vacation_new_zealand",
                sitemaps[2].getLoc());
        assertEquals("weekly", ((SitemapUrl) sitemaps[2]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[2]).getPriority());

        assertEquals("2004-12-23T18:00:15+00:00", sitemaps[3].getLastmod());
        assertEquals(
                "http://www.example.com/catalog?item=74&desc=vacation_newfoundland",
                sitemaps[3].getLoc());
        assertNull(((SitemapUrl) sitemaps[3]).getChangefreq());
        assertEquals("0.3", ((SitemapUrl) sitemaps[3]).getPriority());

        assertEquals("2004-11-23", sitemaps[4].getLastmod());
        assertEquals(
                "http://www.example.com/catalog?item=83&desc=vacation_usa",
                sitemaps[4].getLoc());
        assertNull(((SitemapUrl) sitemaps[4]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[4]).getPriority());
    }

    public void test_parseXmlSitemapsGz() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("sitemaps/sitemap1.xml.gz");
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
        assertEquals(
                "http://www.example.com/catalog?item=12&desc=vacation_hawaii",
                sitemaps[1].getLoc());
        assertEquals("weekly", ((SitemapUrl) sitemaps[1]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[1]).getPriority());

        assertEquals("2004-12-23", sitemaps[2].getLastmod());
        assertEquals(
                "http://www.example.com/catalog?item=73&desc=vacation_new_zealand",
                sitemaps[2].getLoc());
        assertEquals("weekly", ((SitemapUrl) sitemaps[2]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[2]).getPriority());

        assertEquals("2004-12-23T18:00:15+00:00", sitemaps[3].getLastmod());
        assertEquals(
                "http://www.example.com/catalog?item=74&desc=vacation_newfoundland",
                sitemaps[3].getLoc());
        assertNull(((SitemapUrl) sitemaps[3]).getChangefreq());
        assertEquals("0.3", ((SitemapUrl) sitemaps[3]).getPriority());

        assertEquals("2004-11-23", sitemaps[4].getLastmod());
        assertEquals(
                "http://www.example.com/catalog?item=83&desc=vacation_usa",
                sitemaps[4].getLoc());
        assertNull(((SitemapUrl) sitemaps[4]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[4]).getPriority());
    }

    public void test_parseTextSitemaps() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("sitemaps/sitemap1.txt");
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
        assertEquals(
                "http://www.example.com/catalog?item=12&desc=vacation_hawaii",
                sitemaps[1].getLoc());
        assertNull(((SitemapUrl) sitemaps[1]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[1]).getPriority());

        assertNull(sitemaps[2].getLastmod());
        assertEquals(
                "http://www.example.com/catalog?item=73&desc=vacation_new_zealand",
                sitemaps[2].getLoc());
        assertNull(((SitemapUrl) sitemaps[2]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[2]).getPriority());

        assertNull(sitemaps[3].getLastmod());
        assertEquals(
                "http://www.example.com/catalog?item=74&desc=vacation_newfoundland",
                sitemaps[3].getLoc());
        assertNull(((SitemapUrl) sitemaps[3]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[3]).getPriority());

        assertNull(sitemaps[4].getLastmod());
        assertEquals(
                "http://www.example.com/catalog?item=83&desc=vacation_usa",
                sitemaps[4].getLoc());
        assertNull(((SitemapUrl) sitemaps[4]).getChangefreq());
        assertNull(((SitemapUrl) sitemaps[4]).getPriority());
    }

    public void test_parseXmlSitemapsIndex() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("sitemaps/sitemap2.xml");
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();
        assertEquals(2, sitemaps.length);
        assertFalse(sitemapSet.isUrlSet());
        assertTrue(sitemapSet.isIndex());

        assertEquals("2004-10-01T18:23:17+00:00", sitemaps[0].getLastmod());
        assertEquals("http://www.example.com/sitemap1.xml.gz",
                sitemaps[0].getLoc());

        assertEquals("2005-01-01", sitemaps[1].getLastmod());
        assertEquals("http://www.example.com/sitemap2.xml.gz",
                sitemaps[1].getLoc());

    }

    public void test_parseXmlSitemapsIndexGz() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("sitemaps/sitemap2.xml.gz");
        final SitemapSet sitemapSet = sitemapsHelper.parse(in);
        final Sitemap[] sitemaps = sitemapSet.getSitemaps();
        assertEquals(2, sitemaps.length);
        assertFalse(sitemapSet.isUrlSet());
        assertTrue(sitemapSet.isIndex());

        assertEquals("2004-10-01T18:23:17+00:00", sitemaps[0].getLastmod());
        assertEquals("http://www.example.com/sitemap1.xml.gz",
                sitemaps[0].getLoc());

        assertEquals("2005-01-01", sitemaps[1].getLastmod());
        assertEquals("http://www.example.com/sitemap2.xml.gz",
                sitemaps[1].getLoc());

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
}
