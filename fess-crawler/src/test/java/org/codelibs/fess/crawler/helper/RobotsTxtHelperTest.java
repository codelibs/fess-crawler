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

import java.io.InputStream;

import org.codelibs.core.io.CloseableUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.RobotsTxt;
import org.dbflute.utflute.core.PlainTestCase;

public class RobotsTxtHelperTest extends PlainTestCase {
    public RobotsTxtHelper robotsTxtHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("robotsTxtHelper", RobotsTxtHelper.class);
        robotsTxtHelper = container.getComponent("robotsTxtHelper");
    }

    public void testParse() {
        RobotsTxt robotsTxt;
        final InputStream in = RobotsTxtHelperTest.class.getResourceAsStream("robots.txt");
        try {
            robotsTxt = robotsTxtHelper.parse(in);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        for (String userAgent : new String[] { "FessCrawler", "FessCrawler/1.0", "Mozilla FessCrawler" }) {
            assertTrue(robotsTxt.allows("/aaa", userAgent));
            assertTrue(robotsTxt.allows("/private/", userAgent));
            assertTrue(robotsTxt.allows("/private/index.html", userAgent));
            assertTrue(robotsTxt.allows("/help/", userAgent));
            assertTrue(robotsTxt.allows("/help.html", userAgent));
            assertTrue(robotsTxt.allows("/help/faq.html", userAgent));
            assertTrue(robotsTxt.allows("/foo/bar/", userAgent));
            assertTrue(robotsTxt.allows("/foo/bar/index.html", userAgent));
            assertEquals(0, robotsTxt.getCrawlDelay(userAgent));
        }

        for (String userAgent : new String[] { "BruteBot", "FOO BruteBot/1.0" }) {
            assertFalse(robotsTxt.allows("/aaa", userAgent));
            assertFalse(robotsTxt.allows("/private/", userAgent));
            assertFalse(robotsTxt.allows("/private/index.html", userAgent));
            assertFalse(robotsTxt.allows("/help/", userAgent));
            assertFalse(robotsTxt.allows("/help.html", userAgent));
            assertFalse(robotsTxt.allows("/help/faq.html", userAgent));
            assertTrue(robotsTxt.allows("/foo/bar/", userAgent));
            assertTrue(robotsTxt.allows("/foo/bar/index.html", userAgent));
            assertEquals(1314000, robotsTxt.getCrawlDelay(userAgent));
        }

        for (String userAgent : new String[] { "GOOGLEBOT", "GoogleBot", "googlebot" }) {
            assertTrue(robotsTxt.allows("/aaa", userAgent));
            assertTrue(robotsTxt.allows("/private/", userAgent));
            assertTrue(robotsTxt.allows("/private/index.html", userAgent));
            assertTrue(robotsTxt.allows("/help/", userAgent));
            assertTrue(robotsTxt.allows("/help.html", userAgent));
            assertTrue(robotsTxt.allows("/help/faq.html", userAgent));
            assertTrue(robotsTxt.allows("/foo/bar/", userAgent));
            assertTrue(robotsTxt.allows("/foo/bar/index.html", userAgent));
            assertEquals(1, robotsTxt.getCrawlDelay(userAgent));
        }

        for (String userAgent : new String[] { "UnknownBot", "", " ", null }) {
            assertTrue(robotsTxt.allows("/aaa", userAgent));
            assertFalse(robotsTxt.allows("/private/", userAgent));
            assertFalse(robotsTxt.allows("/private/index.html", userAgent));
            assertFalse(robotsTxt.allows("/help/", userAgent));
            assertFalse(robotsTxt.allows("/help.html", userAgent));
            assertTrue(robotsTxt.allows("/help/faq.html", userAgent));
            assertTrue(robotsTxt.allows("/foo/bar/", userAgent));
            assertTrue(robotsTxt.allows("/foo/bar/index.html", userAgent));
            assertEquals(3, robotsTxt.getCrawlDelay(userAgent));
        }

        assertFalse(robotsTxt.allows("/aaa", "Crawler"));
        assertTrue(robotsTxt.allows("/bbb", "Crawler"));
        assertTrue(robotsTxt.allows("/ccc", "Crawler"));
        assertTrue(robotsTxt.allows("/ddd", "Crawler"));
        assertTrue(robotsTxt.allows("/aaa", "Crawler/1.0"));
        assertFalse(robotsTxt.allows("/bbb", "Crawler/1.0"));
        assertTrue(robotsTxt.allows("/ccc", "Crawler/1.0"));
        assertTrue(robotsTxt.allows("/ddd", "Crawler/1.0"));
        assertTrue(robotsTxt.allows("/aaa", "Crawler/2.0"));
        assertTrue(robotsTxt.allows("/bbb", "Crawler/2.0"));
        assertFalse(robotsTxt.allows("/ccc", "Crawler/2.0"));
        assertTrue(robotsTxt.allows("/ddd", "Crawler/2.0"));
        assertTrue(robotsTxt.allows("/aaa", "Hoge Crawler"));
        assertTrue(robotsTxt.allows("/bbb", "Hoge Crawler"));
        assertTrue(robotsTxt.allows("/ccc", "Hoge Crawler"));
        assertFalse(robotsTxt.allows("/ddd", "Hoge Crawler"));

        String[] sitemaps = robotsTxt.getSitemaps();
        assertEquals(2, sitemaps.length);
        assertEquals("http://www.example.com/sitmap.xml", sitemaps[0]);
        assertEquals("http://www.example.net/sitmap.xml", sitemaps[1]);

    }

    public void testParse_disable() {
        final InputStream in = RobotsTxtHelperTest.class.getResourceAsStream("robots.txt");
        robotsTxtHelper.setEnabled(false);
        try {
            assertNull(robotsTxtHelper.parse(in));
        } finally {
            robotsTxtHelper.setEnabled(true);
            CloseableUtil.closeQuietly(in);
        }
    }

    public void testParse_wildcard() {
        RobotsTxt robotsTxt;
        final InputStream in = RobotsTxtHelperTest.class.getResourceAsStream("robots_wildcard.txt");
        try {
            robotsTxt = robotsTxtHelper.parse(in);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        // Test WildcardBot - wildcard patterns
        // Disallow: /*.pdf$ - should block .pdf files but not .pdf with query params
        assertFalse(robotsTxt.allows("/document.pdf", "WildcardBot"));
        assertFalse(robotsTxt.allows("/files/report.pdf", "WildcardBot"));
        assertTrue(robotsTxt.allows("/document.pdf?download=true", "WildcardBot")); // $ means exact end

        // Disallow: /admin/*.php - should block PHP files in admin directory
        assertFalse(robotsTxt.allows("/admin/login.php", "WildcardBot"));
        assertFalse(robotsTxt.allows("/admin/users.php", "WildcardBot"));
        assertTrue(robotsTxt.allows("/admin/", "WildcardBot")); // no .php extension
        assertTrue(robotsTxt.allows("/admin/login.html", "WildcardBot")); // not .php

        // Disallow: /*/private/ - should block private directories under any parent
        assertFalse(robotsTxt.allows("/users/private/", "WildcardBot"));
        assertFalse(robotsTxt.allows("/admin/private/", "WildcardBot"));
        assertFalse(robotsTxt.allows("/users/private/data.txt", "WildcardBot"));
        assertTrue(robotsTxt.allows("/private/", "WildcardBot")); // no parent directory

        // Allow: /public/*.html - should allow HTML files in public directory
        assertTrue(robotsTxt.allows("/public/index.html", "WildcardBot"));
        assertTrue(robotsTxt.allows("/public/about.html", "WildcardBot"));

        // Test EndPathBot - end-of-path ($) patterns
        // Disallow: /fish$ - should block exactly /fish but not /fishing
        assertFalse(robotsTxt.allows("/fish", "EndPathBot"));
        assertTrue(robotsTxt.allows("/fishing", "EndPathBot"));
        assertTrue(robotsTxt.allows("/fish/", "EndPathBot"));

        // Disallow: /temp$ but Allow: /fishing
        assertFalse(robotsTxt.allows("/temp", "EndPathBot"));
        assertTrue(robotsTxt.allows("/temporary", "EndPathBot"));
        assertTrue(robotsTxt.allows("/fishing", "EndPathBot"));

        // Test ComplexBot - complex patterns
        // Disallow: / but Allow: /$ (only root), Allow: /index.html$, Allow: /public/
        assertFalse(robotsTxt.allows("/about", "ComplexBot"));
        assertTrue(robotsTxt.allows("/", "ComplexBot")); // Allow: /$
        assertTrue(robotsTxt.allows("/index.html", "ComplexBot")); // Allow: /index.html$
        assertFalse(robotsTxt.allows("/index.html?page=1", "ComplexBot")); // $ means exact end
        assertTrue(robotsTxt.allows("/public/", "ComplexBot"));
        assertTrue(robotsTxt.allows("/public/page.html", "ComplexBot"));

        // Test PriorityBot - longest match wins
        // Disallow: /store, Allow: /store/public, Disallow: /store/public/sale
        assertFalse(robotsTxt.allows("/store", "PriorityBot"));
        assertFalse(robotsTxt.allows("/store/items", "PriorityBot"));
        assertTrue(robotsTxt.allows("/store/public", "PriorityBot")); // Allow is more specific
        assertTrue(robotsTxt.allows("/store/public/items", "PriorityBot"));
        assertFalse(robotsTxt.allows("/store/public/sale", "PriorityBot")); // Most specific disallow
        assertFalse(robotsTxt.allows("/store/public/sale/item", "PriorityBot"));

        // Test SameLengthBot - Allow wins when same length as Disallow
        // Disallow: /page, Allow: /page
        assertTrue(robotsTxt.allows("/page", "SameLengthBot")); // Allow takes precedence
        assertTrue(robotsTxt.allows("/page.html", "SameLengthBot"));

        // Test MultiWildcardBot - multiple wildcards in pattern
        // Disallow: /*.cgi* - should block URLs with .cgi anywhere
        assertFalse(robotsTxt.allows("/script.cgi", "MultiWildcardBot"));
        assertFalse(robotsTxt.allows("/path/script.cgi?param=value", "MultiWildcardBot"));
        assertFalse(robotsTxt.allows("/test.cgi.bak", "MultiWildcardBot"));

        // Disallow: /*?*id=* - should block URLs with ?...id=...
        assertFalse(robotsTxt.allows("/page?id=123", "MultiWildcardBot"));
        assertFalse(robotsTxt.allows("/article?name=test&id=456", "MultiWildcardBot"));
        assertTrue(robotsTxt.allows("/page?name=test", "MultiWildcardBot")); // no id=

        // Test DollarBot - literal $ in middle of pattern
        // Disallow: /price$info - $ in middle should be treated as literal
        assertFalse(robotsTxt.allows("/price$info", "DollarBot"));
        assertTrue(robotsTxt.allows("/priceinfo", "DollarBot"));

        // Test sitemaps
        String[] sitemaps = robotsTxt.getSitemaps();
        assertEquals(1, sitemaps.length);
        assertEquals("http://www.example.com/sitemap.xml", sitemaps[0]);
    }
}
