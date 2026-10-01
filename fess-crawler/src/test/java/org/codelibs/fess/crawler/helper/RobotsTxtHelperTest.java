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

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.lang.SystemUtil;
import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.HostState;
import org.codelibs.fess.crawler.entity.HostState.RobotsTxtStatus;
import org.codelibs.fess.crawler.entity.RobotsTxt;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.exception.RobotsTxtDisallowedException;
import org.codelibs.fess.crawler.exception.RobotsTxtException;
import org.codelibs.fess.crawler.exception.RobotsTxtUnavailableException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class RobotsTxtHelperTest extends PlainTestCase {
    public RobotsTxtHelper robotsTxtHelper;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("robotsTxtHelper", RobotsTxtHelper.class);
        robotsTxtHelper = container.getComponent("robotsTxtHelper");
        clock.set(NOW);
        SystemUtil.setTimeProvider(clock::get);
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        SystemUtil.setTimeProvider(null);
        super.tearDown(testInfo);
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
    public void testParse_malformed() {
        RobotsTxt robotsTxt;
        final InputStream in = RobotsTxtHelperTest.class.getResourceAsStream("robots_malformed.txt");
        try {
            robotsTxt = robotsTxtHelper.parse(in);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        // Should not throw exception and return a valid RobotsTxt object
        assertNotNull(robotsTxt);

        // Test that orphaned directives (before any User-agent) are ignored
        // These should not affect any bot
        assertTrue(robotsTxt.allows("/orphaned1/", "AnyBot"));
        assertTrue(robotsTxt.allows("/orphaned2/", "AnyBot"));

        // Test GoodBot - should parse valid directives and ignore invalid ones
        assertNotNull(robotsTxt.getDirective("goodbot"));
        assertFalse(robotsTxt.allows("/admin/", "GoodBot"));
        assertTrue(robotsTxt.allows("/public/", "GoodBot"));
        // Invalid directives should not cause parsing to fail

        // Test crawl-delay with invalid values
        // Invalid number is ignored, "-10" clamps to 0, and the last valid value wins.
        // "Crawl-delay: 5.5" is now a valid fractional delay (5500 ms), so the int API reports 5.
        assertEquals(5500L, robotsTxt.getCrawlDelayMillis("GoodBot"));
        assertEquals(5, robotsTxt.getCrawlDelay("GoodBot"));

        // Test MultiColonBot - colons in paths should be preserved
        assertFalse(robotsTxt.allows("http://example.com:8080/path", "MultiColonBot"));
        assertTrue(robotsTxt.allows("/path:with:colons", "MultiColonBot"));

        // Test ExtraSpaceBot - extra whitespace should be handled
        assertFalse(robotsTxt.allows("/spaced/", "ExtraSpaceBot"));
        assertTrue(robotsTxt.allows("/also-spaced/", "ExtraSpaceBot"));

        // Test MixedCaseBot - mixed case directives should work
        assertFalse(robotsTxt.allows("/test1/", "MixedCaseBot"));
        assertTrue(robotsTxt.allows("/test2/", "MixedCaseBot"));
        assertEquals(2, robotsTxt.getCrawlDelay("MixedCaseBot"));

        // Test CommentBot - inline comments should be stripped
        assertFalse(robotsTxt.allows("/path1/", "CommentBot"));
        assertTrue(robotsTxt.allows("/path2/", "CommentBot"));

        // Test EmptyLineBot - empty lines should not cause issues
        assertFalse(robotsTxt.allows("/test/", "EmptyLineBot"));
        assertTrue(robotsTxt.allows("/public/", "EmptyLineBot"));

        // Test VeryLongBotNameThatExceedsNormalLengthAndShouldStillBeProcessedCorrectlyWithoutAnyIssuesEvenThoughItIsExtremelyLongAndUnusual
        String longBotName =
                "VeryLongBotNameThatExceedsNormalLengthAndShouldStillBeProcessedCorrectlyWithoutAnyIssuesEvenThoughItIsExtremelyLongAndUnusual";
        assertFalse(robotsTxt.allows("/test/", longBotName));

        // Test SpecialCharBot - special characters in paths
        assertFalse(robotsTxt.allows("/path with spaces/", "SpecialCharBot"));
        assertFalse(robotsTxt.allows("/path%20encoded/", "SpecialCharBot"));
        assertFalse(robotsTxt.allows("/path?query=value", "SpecialCharBot"));

        // Test multiple User-agents in sequence (Bot1, Bot2, Bot3 should share the same rules)
        assertFalse(robotsTxt.allows("/shared/", "Bot1"));
        assertFalse(robotsTxt.allows("/shared/", "Bot2"));
        assertFalse(robotsTxt.allows("/shared/", "Bot3"));

        // Test sitemaps - should parse valid sitemaps and ignore invalid ones
        String[] sitemaps = robotsTxt.getSitemaps();
        assertTrue(sitemaps.length >= 3); // At least the valid ones should be parsed

        // Test NumericBot - various crawl-delay formats
        // Should handle edge cases gracefully
        // "1.23e10" seconds overflows an int, so the int API is capped instead of going negative
        assertTrue(robotsTxt.getCrawlDelay("NumericBot") >= 0);
        assertEquals(Integer.MAX_VALUE, robotsTxt.getCrawlDelay("NumericBot"));

        // Test TabBot - tab characters should be treated as whitespace
        assertFalse(robotsTxt.allows("/tab1/", "TabBot"));
        assertTrue(robotsTxt.allows("/tab2/", "TabBot"));

        // Test bots with special characters - should be normalized to lowercase
        assertFalse(robotsTxt.allows("/trademark/", "Bot™"));
        assertFalse(robotsTxt.allows("/registered/", "Bot®"));

        // Test wildcard user-agent
        assertFalse(robotsTxt.allows("/default/", "UnknownRandomBot"));
    }

    @Test
    public void testParse_emptyFile() {
        RobotsTxt robotsTxt;
        final InputStream in = RobotsTxtHelperTest.class.getResourceAsStream("robots_empty.txt");
        try {
            robotsTxt = robotsTxtHelper.parse(in);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        // Should not throw exception for file with only comments
        assertNotNull(robotsTxt);
        // Everything should be allowed by default
        assertTrue(robotsTxt.allows("/anything", "AnyBot"));
    }

    @Test
    public void testParse_onlyWhitespace() {
        RobotsTxt robotsTxt;
        final InputStream in = RobotsTxtHelperTest.class.getResourceAsStream("robots_only_whitespace.txt");
        try {
            robotsTxt = robotsTxtHelper.parse(in);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        // Should not throw exception for file with only whitespace
        assertNotNull(robotsTxt);
        // Everything should be allowed by default
        assertTrue(robotsTxt.allows("/anything", "AnyBot"));
    }

    @Test
    public void testParse_malformedCrawlDelay() {
        String robotsTxtContent = "User-agent: TestBot\n" + "Crawl-delay: abc\n" + "Disallow: /test/\n";

        RobotsTxt robotsTxt;
        final InputStream in = new java.io.ByteArrayInputStream(robotsTxtContent.getBytes());
        try {
            robotsTxt = robotsTxtHelper.parse(in);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        // Should not throw exception for invalid crawl-delay
        assertNotNull(robotsTxt);
        // Invalid crawl-delay should be ignored (default 0)
        assertEquals(0, robotsTxt.getCrawlDelay("TestBot"));
        // Other directives should still work
        assertFalse(robotsTxt.allows("/test/", "TestBot"));
    }

    @Test
    public void testParse_negativeCrawlDelay() {
        String robotsTxtContent = "User-agent: TestBot\n" + "Crawl-delay: -100\n" + "Disallow: /test/\n";

        RobotsTxt robotsTxt;
        final InputStream in = new java.io.ByteArrayInputStream(robotsTxtContent.getBytes());
        try {
            robotsTxt = robotsTxtHelper.parse(in);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        // Negative crawl-delay should be converted to 0
        assertNotNull(robotsTxt);
        assertEquals(0, robotsTxt.getCrawlDelay("TestBot"));
    }

    @Test
    public void testParse_floatingPointCrawlDelay() {
        String robotsTxtContent = "User-agent: TestBot\n" + "Crawl-delay: 2.5\n" + "Disallow: /test/\n";

        RobotsTxt robotsTxt;
        final InputStream in = new java.io.ByteArrayInputStream(robotsTxtContent.getBytes());
        try {
            robotsTxt = robotsTxtHelper.parse(in);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        // Fractional crawl-delay is accepted; the int API rounds down to whole seconds
        assertNotNull(robotsTxt);
        assertEquals(2500L, robotsTxt.getCrawlDelayMillis("TestBot"));
        assertEquals(2, robotsTxt.getCrawlDelay("TestBot"));
    }

    private RobotsTxt parseRobotsTxt(final String content) {
        final InputStream in = new java.io.ByteArrayInputStream(content.getBytes());
        try {
            return robotsTxtHelper.parse(in);
        } finally {
            CloseableUtil.closeQuietly(in);
        }
    }

    @Test
    public void testParse_crawlDelayMillis() {
        assertEquals(500L, parseRobotsTxt("User-agent: TestBot\nCrawl-delay: 0.5\n").getCrawlDelayMillis("TestBot"));

        final RobotsTxt whole = parseRobotsTxt("User-agent: TestBot\nCrawl-delay: 2\n");
        assertEquals(2000L, whole.getCrawlDelayMillis("TestBot"));
        assertEquals(2, whole.getCrawlDelay("TestBot"));

        assertEquals(0L, parseRobotsTxt("User-agent: TestBot\nCrawl-delay: abc\n").getCrawlDelayMillis("TestBot"));
        assertEquals(0L, parseRobotsTxt("User-agent: TestBot\nCrawl-delay: -1\n").getCrawlDelayMillis("TestBot"));
        assertEquals(0L, parseRobotsTxt("User-agent: TestBot\nCrawl-delay: NaN\n").getCrawlDelayMillis("TestBot"));
        assertEquals(0L, parseRobotsTxt("User-agent: TestBot\nCrawl-delay: Infinity\n").getCrawlDelayMillis("TestBot"));
    }

    @Test
    public void testParse_directivesBeforeUserAgent() {
        String robotsTxtContent = "Disallow: /before/\n" + "Allow: /also-before/\n" + "User-agent: TestBot\n" + "Disallow: /test/\n";

        RobotsTxt robotsTxt;
        final InputStream in = new java.io.ByteArrayInputStream(robotsTxtContent.getBytes());
        try {
            robotsTxt = robotsTxtHelper.parse(in);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        // Directives before User-agent should be ignored
        assertNotNull(robotsTxt);
        assertTrue(robotsTxt.allows("/before/", "TestBot"));
        assertTrue(robotsTxt.allows("/also-before/", "TestBot"));
        // Valid directives should still work
        assertFalse(robotsTxt.allows("/test/", "TestBot"));
    }

    @Test
    public void testParse_mixedValidAndInvalidDirectives() {
        String robotsTxtContent = "User-agent: TestBot\n" + "Disallow: /valid1/\n" + "InvalidDirective: value\n" + "Disallow: /valid2/\n"
                + "Another-Invalid: test\n" + "Allow: /valid3/\n" + "NoColon\n" + "Disallow: /valid4/\n";

        RobotsTxt robotsTxt;
        final InputStream in = new java.io.ByteArrayInputStream(robotsTxtContent.getBytes());
        try {
            robotsTxt = robotsTxtHelper.parse(in);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        // Should parse valid directives and ignore invalid ones
        assertNotNull(robotsTxt);
        assertFalse(robotsTxt.allows("/valid1/", "TestBot"));
        assertFalse(robotsTxt.allows("/valid2/", "TestBot"));
        assertTrue(robotsTxt.allows("/valid3/", "TestBot"));
        assertFalse(robotsTxt.allows("/valid4/", "TestBot"));
    }

    @Test
    public void testParse_emptyValues() {
        String robotsTxtContent = "User-agent: TestBot\n" + "Disallow:\n" + "Allow:\n" + "Crawl-delay:\n";

        RobotsTxt robotsTxt;
        final InputStream in = new java.io.ByteArrayInputStream(robotsTxtContent.getBytes());
        try {
            robotsTxt = robotsTxtHelper.parse(in);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        // Empty Disallow means allow all
        assertNotNull(robotsTxt);
        assertTrue(robotsTxt.allows("/anything", "TestBot"));
    }

    @Test
    public void testParse_unicodeContent() {
        String robotsTxtContent =
                "# コメント\n" + "User-agent: 日本語Bot\n" + "Disallow: /日本語/\n" + "User-agent: TestBot\n" + "Disallow: /test/\n";

        RobotsTxt robotsTxt;
        final InputStream in = new java.io.ByteArrayInputStream(robotsTxtContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        try {
            robotsTxt = robotsTxtHelper.parse(in, "UTF-8");
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        // Should handle unicode content
        assertNotNull(robotsTxt);
        assertFalse(robotsTxt.allows("/test/", "TestBot"));
    }

    @Test
    public void testParse_sitemapDoesNotBreakGroup() {
        RobotsTxt robotsTxt;
        final InputStream in = RobotsTxtHelperTest.class.getResourceAsStream("robots_sitemap_group.txt");
        try {
            robotsTxt = robotsTxtHelper.parse(in);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        assertNotNull(robotsTxt);

        // Bot1, Bot2, Bot3 should all share the same rules because
        // Sitemap between User-agent lines should not break the group
        assertFalse(robotsTxt.allows("/secret/", "Bot1"));
        assertFalse(robotsTxt.allows("/secret/", "Bot2"));
        assertFalse(robotsTxt.allows("/secret/", "Bot3"));
        assertTrue(robotsTxt.allows("/secret/public/", "Bot1"));
        assertTrue(robotsTxt.allows("/secret/public/", "Bot2"));
        assertTrue(robotsTxt.allows("/secret/public/", "Bot3"));

        // Bot4, Bot5 should share the same rules - unknown directives also don't break the group
        assertFalse(robotsTxt.allows("/admin/", "Bot4"));
        assertFalse(robotsTxt.allows("/admin/", "Bot5"));

        // Sitemap should be parsed
        String[] sitemaps = robotsTxt.getSitemaps();
        assertEquals(1, sitemaps.length);
        assertEquals("http://www.example.com/sitemap.xml", sitemaps[0]);
    }

    @Test
    public void testParse_percentEncodedPaths() {
        RobotsTxt robotsTxt;
        final InputStream in = RobotsTxtHelperTest.class.getResourceAsStream("robots_percent_encoding.txt");
        try {
            robotsTxt = robotsTxtHelper.parse(in);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        assertNotNull(robotsTxt);

        // Percent-encoded pattern should match encoded URL path (case-insensitive hex)
        assertFalse(robotsTxt.allows("/dir/%E4%B8%AD%E6%96%87/", "PercentBot"));
        assertFalse(robotsTxt.allows("/dir/%e4%b8%ad%e6%96%87/", "PercentBot"));
        assertTrue(robotsTxt.allows("/dir/%E4%B8%AD%E6%96%87/public/", "PercentBot"));

        // Percent-encoded space (%20) should match encoded URL path
        assertFalse(robotsTxt.allows("/path/file%20name/", "DecodedBot"));
        assertTrue(robotsTxt.allows("/path/file%20name/public/", "DecodedBot"));
    }

    private static final String UA = "FessCrawler/1.0";

    private static final RobotsTxtPolicy POLICY = new RobotsTxtPolicy(true, true, false, 3);

    private static final long NOW = 1_700_000_000_000L;

    /** The fake clock read through SystemUtil by checkRobotsTxt. */
    private final AtomicLong clock = new AtomicLong(NOW);

    /** Serves scripted responses per URL; the last response of a URL is repeated. */
    static class ScriptedFetcher implements RobotsTxtFetcher {
        final Map<String, List<Object>> script = new HashMap<>();

        final List<String> fetched = new ArrayList<>();

        ScriptedFetcher on(final String url, final Object... responses) {
            script.computeIfAbsent(url, k -> new ArrayList<>()).addAll(List.of(responses));
            return this;
        }

        @Override
        public RobotsTxtResponse fetch(final String robotsTxtUrl) throws Exception {
            fetched.add(robotsTxtUrl);
            final List<Object> list = script.get(robotsTxtUrl);
            if (list == null || list.isEmpty()) {
                return new RobotsTxtResponse(404, null, null, null, null);
            }
            final Object value = list.size() > 1 ? list.remove(0) : list.get(0);
            if (value instanceof Exception) {
                throw (Exception) value;
            }
            return (RobotsTxtResponse) value;
        }

        int count() {
            return fetched.size();
        }

        int count(final String url) {
            return (int) fetched.stream().filter(url::equals).count();
        }
    }

    private static RobotsTxtResponse ok(final String body) {
        return new RobotsTxtResponse(200, null, null, body.getBytes(StandardCharsets.UTF_8), null);
    }

    private static RobotsTxtResponse redirect(final int status, final String location) {
        return new RobotsTxtResponse(status, location, null, null, null);
    }

    private static RobotsTxtResponse status(final int status) {
        return new RobotsTxtResponse(status, null, null, null, null);
    }

    private void assertAllowed(final CrawlerContext context, final String url, final RobotsTxtFetcher fetcher,
            final RobotsTxtPolicy policy) {
        robotsTxtHelper.checkRobotsTxt(context, url, UA, fetcher, policy);
    }

    private void assertDisallowed(final CrawlerContext context, final String url, final RobotsTxtFetcher fetcher,
            final RobotsTxtPolicy policy) {
        try {
            robotsTxtHelper.checkRobotsTxt(context, url, UA, fetcher, policy);
            fail();
        } catch (final RobotsTxtDisallowedException e) {
            assertTrue(e.isInfoEnabled());
            assertTrue(e.getMessage().contains(url));
        }
    }

    private RobotsTxtUnavailableException assertUnavailable(final CrawlerContext context, final String url, final RobotsTxtFetcher fetcher,
            final RobotsTxtPolicy policy) {
        try {
            robotsTxtHelper.checkRobotsTxt(context, url, UA, fetcher, policy);
            fail();
            return null;
        } catch (final RobotsTxtUnavailableException e) {
            assertTrue(e.getMessage().contains(url));
            return e;
        }
    }

    @Test
    public void testCheckRobotsTxt_parsedRules() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher =
                new ScriptedFetcher().on("http://example.com/robots.txt", ok("User-agent: *\nDisallow: /a/\nAllow: /a/b\n"));

        assertAllowed(context, "http://example.com/a/b", fetcher, POLICY);
        assertDisallowed(context, "http://example.com/a/c", fetcher, POLICY);
        assertAllowed(context, "http://example.com/x", fetcher, POLICY);
        assertEquals(1, fetcher.count());
        assertEquals(RobotsTxtStatus.PARSED, context.getHostState("http://example.com/").getRobotsTxtStatus());
    }

    @Test
    public void testCheckRobotsTxt_fetchedOncePerOrigin() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", ok("User-agent: *\nDisallow: /a/\n"))
                .on("http://other.example.com:8080/robots.txt", ok("User-agent: *\nDisallow: /b/\n"));

        assertAllowed(context, "http://example.com/1", fetcher, POLICY);
        assertAllowed(context, "http://example.com/2?q=1", fetcher, POLICY);
        assertAllowed(context, "http://other.example.com:8080/a/1", fetcher, POLICY);
        assertDisallowed(context, "http://other.example.com:8080/b/1", fetcher, POLICY);
        assertEquals(1, fetcher.count("http://example.com/robots.txt"));
        assertEquals(1, fetcher.count("http://other.example.com:8080/robots.txt"));
        assertEquals(2, fetcher.count());
    }

    @Test
    public void testCheckRobotsTxt_redirectsFollowed() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", redirect(301, "/moved/robots.txt"))
                .on("http://example.com/moved/robots.txt", redirect(302, "https://cdn.example.net/robots.txt"))
                .on("https://cdn.example.net/robots.txt", ok("User-agent: *\nDisallow: /private/\n"));

        assertDisallowed(context, "http://example.com/private/a.html", fetcher, POLICY);
        assertAllowed(context, "http://example.com/public/a.html", fetcher, POLICY);
        assertEquals(List.of("http://example.com/robots.txt", "http://example.com/moved/robots.txt", "https://cdn.example.net/robots.txt"),
                fetcher.fetched);
    }

    @Test
    public void testCheckRobotsTxt_fiveRedirectsFollowed() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", redirect(301, "/r1"));
        for (int i = 1; i < 5; i++) {
            fetcher.on("http://example.com/r" + i, redirect(301, "/r" + (i + 1)));
        }
        fetcher.on("http://example.com/r5", ok("User-agent: *\nDisallow: /\n"));

        assertDisallowed(context, "http://example.com/a", fetcher, POLICY);
        assertEquals(6, fetcher.count());
    }

    @Test
    public void testCheckRobotsTxt_sixRedirectsAllowAll() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", redirect(301, "/r1"));
        for (int i = 1; i <= 5; i++) {
            fetcher.on("http://example.com/r" + i, redirect(301, "/r" + (i + 1)));
        }
        fetcher.on("http://example.com/r6", ok("User-agent: *\nDisallow: /\n"));

        assertAllowed(context, "http://example.com/a", fetcher, POLICY);
        assertEquals(6, fetcher.count());
        assertEquals(0, fetcher.count("http://example.com/r6"));
        assertEquals(RobotsTxtStatus.ALLOW_ALL, context.getHostState("http://example.com/").getRobotsTxtStatus());
    }

    @Test
    public void testCheckRobotsTxt_redirectLoopAllowAll() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", redirect(301, "/b"))
                .on("http://example.com/b", redirect(301, "http://example.com/robots.txt"));

        assertAllowed(context, "http://example.com/a", fetcher, POLICY);
        assertTrue(fetcher.count() <= 6);
        assertEquals(RobotsTxtStatus.ALLOW_ALL, context.getHostState("http://example.com/").getRobotsTxtStatus());
    }

    @Test
    public void testCheckRobotsTxt_redirectWithoutLocationAllowAll() {
        for (final String location : new String[] { null, "", "http://exa mple.com/[bad" }) {
            final CrawlerContext context = new CrawlerContext();
            final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", redirect(302, location));

            assertAllowed(context, "http://example.com/a", fetcher, POLICY);
            assertEquals(1, fetcher.count());
            assertEquals(RobotsTxtStatus.ALLOW_ALL, context.getHostState("http://example.com/").getRobotsTxtStatus());
        }
    }

    @Test
    public void testCheckRobotsTxt_clientErrorAllowAll() {
        for (final int code : new int[] { 400, 401, 403, 404, 410 }) {
            final CrawlerContext context = new CrawlerContext();
            final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", status(code));

            assertAllowed(context, "http://example.com/a", fetcher, POLICY);
            assertEquals(RobotsTxtStatus.ALLOW_ALL, context.getHostState("http://example.com/").getRobotsTxtStatus());
        }
    }

    @Test
    public void testCheckRobotsTxt_unresolvableHostCarriesCause() {
        final CrawlerContext context = new CrawlerContext();
        final UnknownHostException uhe = new UnknownHostException("failure.url");
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://failure.url/robots.txt", uhe);

        final RobotsTxtUnavailableException e = assertUnavailable(context, "http://failure.url/", fetcher, POLICY);
        // a failure URL is named after the cause, so the network error must be the cause
        assertTrue(e.getCause() == uhe);
        assertTrue(e.isFetchAttempted());
        assertTrue(e.isFetchFailed());
        assertEquals("robots.txt of http://failure.url is unavailable (UnknownHostException: failure.url): http://failure.url/",
                e.getMessage());
        final HostState hostState = context.getHostState("http://failure.url/");
        assertEquals(1, hostState.getRobotsTxtLastFailureAttempts());
        assertEquals("UnknownHostException: failure.url", hostState.getRobotsTxtLastFailureReason());
        assertTrue(hostState.getRobotsTxtLastFailure() == uhe);
    }

    @Test
    public void testCheckRobotsTxt_failureStatusNamedInMessage() {
        for (final int code : new int[] { 429, 500, 503 }) {
            final CrawlerContext context = new CrawlerContext();
            final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", status(code));

            final RobotsTxtUnavailableException e = assertUnavailable(context, "http://example.com/a", fetcher, POLICY);
            assertNull(e.getCause());
            assertTrue(e.isFetchFailed());
            assertEquals("robots.txt of http://example.com is unavailable (HTTP " + code + "): http://example.com/a", e.getMessage());
            final HostState hostState = context.getHostState("http://example.com/");
            assertEquals("HTTP " + code, hostState.getRobotsTxtLastFailureReason());
            assertNull(hostState.getRobotsTxtLastFailure());
        }
    }

    @Test
    public void testCheckRobotsTxt_serviceUnavailableWithRetryAfter() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher =
                new ScriptedFetcher().on("http://example.com/robots.txt", new RobotsTxtResponse(503, null, "120", null, null));

        final RobotsTxtUnavailableException e = assertUnavailable(context, "http://example.com/a", fetcher, POLICY);
        assertEquals(120000L, e.getRetryAfterMillis());
        final HostState hostState = context.getHostState("http://example.com/");
        assertEquals(RobotsTxtStatus.UNAVAILABLE, hostState.getRobotsTxtStatus());
        assertTrue(hostState.isAllowedByRobotsTxt("http://example.com/a"));
        assertEquals(NOW + 120000L, hostState.getBackoffUntil());
    }

    @Test
    public void testCheckRobotsTxt_retryAfterCappedByMaxBackoff() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher =
                new ScriptedFetcher().on("http://example.com/robots.txt", new RobotsTxtResponse(503, null, "86400", null, null));

        final RobotsTxtUnavailableException e = assertUnavailable(context, "http://example.com/a", fetcher, POLICY);
        assertEquals(86400000L, e.getRetryAfterMillis());
        assertEquals(NOW + context.getMaxBackoffMillis(), context.getHostState("http://example.com/").getBackoffUntil());
    }

    @Test
    public void testCheckRobotsTxt_tooManyRequestsUnavailable() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", status(429));

        final RobotsTxtUnavailableException e = assertUnavailable(context, "http://example.com/a", fetcher, POLICY);
        assertEquals(0L, e.getRetryAfterMillis());
        // no Retry-After: the exponential backoff starts at the base
        assertEquals(NOW + context.getBackoffBaseMillis(), context.getHostState("http://example.com/").getBackoffUntil());
    }

    @Test
    public void testCheckRobotsTxt_unavailableUntilMaxRetriesThenDisallowAll() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", status(503));
        final HostState hostState = context.getHostState("http://example.com/");

        for (int i = 0; i < 3; i++) {
            assertUnavailable(context, "http://example.com/a", fetcher, POLICY);
            assertEquals(RobotsTxtStatus.UNAVAILABLE, hostState.getRobotsTxtStatus());
            // let the backoff recorded by checkRobotsTxt elapse
            clock.set(hostState.getBackoffUntil());
        }
        assertDisallowed(context, "http://example.com/a", fetcher, POLICY);
        assertEquals(RobotsTxtStatus.DISALLOW_ALL, hostState.getRobotsTxtStatus());
        assertEquals(4, fetcher.count());
        // the last failure is kept so that the dropped URLs can be reported with it
        assertEquals(4, hostState.getRobotsTxtLastFailureAttempts());
        assertEquals("HTTP 503", hostState.getRobotsTxtLastFailureReason());
        assertNull(hostState.getRobotsTxtLastFailure());

        // given up: no more fetches
        assertDisallowed(context, "http://example.com/b", fetcher, POLICY);
        assertEquals(4, fetcher.count());
    }

    @Test
    public void testCheckRobotsTxt_unavailableThenParsed() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher =
                new ScriptedFetcher().on("http://example.com/robots.txt", status(503), ok("User-agent: *\nDisallow: /a/\n"));
        final HostState hostState = context.getHostState("http://example.com/");

        assertUnavailable(context, "http://example.com/b", fetcher, POLICY);
        clock.set(hostState.getBackoffUntil());

        assertAllowed(context, "http://example.com/b", fetcher, POLICY);
        assertDisallowed(context, "http://example.com/a/1", fetcher, POLICY);
        assertEquals(2, fetcher.count());
        assertEquals(RobotsTxtStatus.PARSED, hostState.getRobotsTxtStatus());
        // the failure count and the last failure were reset by the successful fetch
        assertEquals(0, hostState.getRobotsTxtLastFailureAttempts());
        assertNull(hostState.getRobotsTxtLastFailureReason());
        assertEquals(1, hostState.incrementAndGetRobotsTxtFailureCount());
    }

    @Test
    public void testCheckRobotsTxt_unavailableBeforeBackoffElapsedNotFetched() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", status(503));
        final HostState hostState = context.getHostState("http://example.com/");

        assertTrue(assertUnavailable(context, "http://example.com/a", fetcher, POLICY).isFetchAttempted());
        // checkRobotsTxt records the backoff itself; nobody else calls recordFailure
        final long backoffUntil = hostState.getBackoffUntil();
        assertEquals(NOW + context.getBackoffBaseMillis(), backoffUntil);

        final RobotsTxtUnavailableException e = assertUnavailable(context, "http://example.com/b", fetcher, POLICY);
        assertEquals(0L, e.getRetryAfterMillis());
        assertNull(e.getCause());
        // nothing was fetched, so the URL must not use up a retry, and there is no failure to report
        assertFalse(e.isFetchAttempted());
        assertFalse(e.isFetchFailed());
        assertEquals(1, fetcher.count());
        assertEquals(backoffUntil, hostState.getBackoffUntil());

        clock.set(backoffUntil - 1);
        assertUnavailable(context, "http://example.com/c", fetcher, POLICY);
        assertEquals(1, fetcher.count());

        clock.set(backoffUntil);
        assertUnavailable(context, "http://example.com/d", fetcher, POLICY);
        assertEquals(2, fetcher.count());
        // only the two attempted fetches were counted
        assertEquals(3, hostState.incrementAndGetRobotsTxtFailureCount());
    }

    @Test
    public void testCheckRobotsTxt_interruptedNotCounted() {
        final CrawlerContext context = new CrawlerContext();
        final InterruptedException interrupted = new InterruptedException();
        final ScriptedFetcher fetcher =
                new ScriptedFetcher().on("http://example.com/robots.txt", interrupted, ok("User-agent: *\nDisallow: /a/\n"));
        final HostState hostState = context.getHostState("http://example.com/");

        final RobotsTxtUnavailableException e;
        try {
            e = assertUnavailable(context, "http://example.com/a/1", fetcher, POLICY);
        } finally {
            assertTrue(Thread.interrupted());
        }
        assertTrue(e.getCause() == interrupted);
        assertTrue(e.isFetchAttempted());
        // an interrupted request is not a failure of the site
        assertFalse(e.isFetchFailed());
        assertEquals(0L, e.getRetryAfterMillis());
        assertEquals(0L, hostState.getBackoffUntil());
        assertNull(hostState.getRobotsTxtStatus());

        // the next call fetches again at once
        assertDisallowed(context, "http://example.com/a/1", fetcher, POLICY);
        assertEquals(2, fetcher.count());
        assertEquals(1, hostState.incrementAndGetRobotsTxtFailureCount());
    }

    @Test
    public void testCheckRobotsTxt_interruptedIoNotCounted() {
        final CrawlerContext context = new CrawlerContext();
        final InterruptedIOException interrupted = new InterruptedIOException("request aborted");
        final ScriptedFetcher fetcher =
                new ScriptedFetcher().on("http://example.com/robots.txt", interrupted, ok("User-agent: *\nDisallow: /a/\n"));
        final HostState hostState = context.getHostState("http://example.com/");

        final RobotsTxtUnavailableException e = assertUnavailable(context, "http://example.com/a/1", fetcher, POLICY);
        assertFalse(Thread.currentThread().isInterrupted());
        assertTrue(e.getCause() == interrupted);
        assertEquals(0L, hostState.getBackoffUntil());
        assertNull(hostState.getRobotsTxtStatus());

        assertDisallowed(context, "http://example.com/a/1", fetcher, POLICY);
        assertEquals(2, fetcher.count());
        assertEquals(1, hostState.incrementAndGetRobotsTxtFailureCount());
    }

    @Test
    public void testCheckRobotsTxt_failureOfInterruptedThreadNotCounted() {
        final CrawlerContext context = new CrawlerContext();
        final SocketTimeoutException timeout = new SocketTimeoutException("Read timed out");
        // an access timeout interrupts the thread, but a blocking read only ends with its own timeout
        final RobotsTxtFetcher fetcher = robotsTxtUrl -> {
            Thread.currentThread().interrupt();
            throw timeout;
        };
        final HostState hostState = context.getHostState("http://example.com/");

        final RobotsTxtUnavailableException e;
        try {
            e = assertUnavailable(context, "http://example.com/a", fetcher, POLICY);
        } finally {
            assertTrue(Thread.interrupted());
        }
        assertTrue(e.getCause() == timeout);
        assertFalse(e.isFetchFailed());
        assertEquals(0L, hostState.getBackoffUntil());
        assertNull(hostState.getRobotsTxtStatus());
        assertNull(hostState.getRobotsTxtLastFailureReason());
        assertEquals(1, hostState.incrementAndGetRobotsTxtFailureCount());
    }

    @Test
    public void testCheckRobotsTxt_timeoutsCounted() {
        final Exception[] timeouts =
                { new SocketTimeoutException("Read timed out"), new org.apache.hc.client5.http.ConnectTimeoutException("Connect timed out"),
                        new org.apache.http.conn.ConnectTimeoutException("Connect timed out"),
                        new org.apache.http.conn.ConnectionPoolTimeoutException("Timeout waiting for connection from pool"),
                        new org.apache.hc.core5.http.ConnectionRequestTimeoutException("Timeout deadline") };
        for (final Exception timeout : timeouts) {
            final CrawlerContext context = new CrawlerContext();
            final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", timeout);
            final HostState hostState = context.getHostState("http://example.com/");

            final RobotsTxtUnavailableException e = assertUnavailable(context, "http://example.com/a", fetcher, POLICY);
            assertFalse(Thread.currentThread().isInterrupted());
            assertTrue(e.isFetchAttempted());
            assertTrue(e.getCause() == timeout);
            assertEquals(RobotsTxtStatus.UNAVAILABLE, hostState.getRobotsTxtStatus());
            assertEquals(NOW + context.getBackoffBaseMillis(), hostState.getBackoffUntil());
            assertEquals(2, hostState.incrementAndGetRobotsTxtFailureCount());
        }
    }

    @Test
    public void testCheckRobotsTxt_allowOnUnavailable() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", status(503));

        assertAllowed(context, "http://example.com/a", fetcher, new RobotsTxtPolicy(true, true, true, 3));
        assertEquals(RobotsTxtStatus.ALLOW_ALL, context.getHostState("http://example.com/").getRobotsTxtStatus());
    }

    @Test
    public void testCheckRobotsTxt_fetchExceptionUnavailable() {
        final CrawlerContext context = new CrawlerContext();
        final IOException ioe = new IOException("connection reset");
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", ioe);

        final RobotsTxtUnavailableException e = assertUnavailable(context, "http://example.com/a", fetcher, POLICY);
        assertTrue(e.getCause() == ioe);
        assertTrue(e.isFetchFailed());
        assertEquals(0L, e.getRetryAfterMillis());
        assertEquals(RobotsTxtStatus.UNAVAILABLE, context.getHostState("http://example.com/").getRobotsTxtStatus());
    }

    @Test
    public void testCheckRobotsTxt_maxLengthExceededAllowAll() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher =
                new ScriptedFetcher().on("http://example.com/robots.txt", new MaxLengthExceededException("too large"));

        assertAllowed(context, "http://example.com/a", fetcher, POLICY);
        assertEquals(RobotsTxtStatus.ALLOW_ALL, context.getHostState("http://example.com/").getRobotsTxtStatus());
    }

    @Test
    public void testCheckRobotsTxt_unknownCharsetFallsBackToUtf8() {
        for (final String charset : new String[] { "no-such-charset", "bad charset!" }) {
            final CrawlerContext context = new CrawlerContext();
            final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt",
                    new RobotsTxtResponse(200, null, null, "User-agent: *\nDisallow: /a/\n".getBytes(StandardCharsets.UTF_8), charset));

            assertDisallowed(context, "http://example.com/a/1", fetcher, POLICY);
            assertAllowed(context, "http://example.com/b/1", fetcher, POLICY);
            assertEquals(RobotsTxtStatus.PARSED, context.getHostState("http://example.com/").getRobotsTxtStatus());
        }
    }

    @Test
    public void testCheckRobotsTxt_parseFailureAllowAll() {
        final RobotsTxtHelper helper = new RobotsTxtHelper() {
            @Override
            public RobotsTxt parse(final InputStream stream, final String charsetName) {
                throw new RobotsTxtException("broken");
            }
        };
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", ok("User-agent: *\nDisallow: /\n"));

        helper.checkRobotsTxt(context, "http://example.com/a", UA, fetcher, POLICY);
        assertEquals(RobotsTxtStatus.ALLOW_ALL, context.getHostState("http://example.com/").getRobotsTxtStatus());
    }

    @Test
    public void testCheckRobotsTxt_useDisallowsFalseAllowAll() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt",
                ok("User-agent: *\nDisallow: /\nSitemap: http://example.com/sitemap.xml\n"));

        assertAllowed(context, "http://example.com/a", fetcher, new RobotsTxtPolicy(true, false, false, 3));
        assertEquals(RobotsTxtStatus.ALLOW_ALL, context.getHostState("http://example.com/").getRobotsTxtStatus());
        final String[] sitemaps = context.removeSitemaps();
        assertNotNull(sitemaps);
        assertEquals(1, sitemaps.length);
    }

    @Test
    public void testCheckRobotsTxt_useAllowsFalseIgnoresAllows() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher =
                new ScriptedFetcher().on("http://example.com/robots.txt", ok("User-agent: *\nDisallow: /a/\nAllow: /a/b\n"));

        assertDisallowed(context, "http://example.com/a/b", fetcher, new RobotsTxtPolicy(false, true, false, 3));
        assertAllowed(context, "http://example.com/x", fetcher, new RobotsTxtPolicy(false, true, false, 3));
    }

    @Test
    public void testCheckRobotsTxt_crawlDelayStored() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt",
                ok("User-agent: FessCrawler\nCrawl-delay: 1.5\nDisallow: /a/\n\nUser-agent: *\nCrawl-delay: 9\n"));

        assertAllowed(context, "http://example.com/x", fetcher, POLICY);
        assertEquals(1500L, context.getHostState("http://example.com/").getCrawlDelayMillis());
    }

    @Test
    public void testCheckRobotsTxt_sitemaps() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt",
                ok("Sitemap: http://example.com/s1.xml\nUser-agent: *\nDisallow: /a/\nSitemap: http://example.com/s2.xml\n"));

        assertAllowed(context, "http://example.com/x", fetcher, POLICY);
        final String[] sitemaps = context.removeSitemaps();
        assertNotNull(sitemaps);
        assertEquals(2, sitemaps.length);
        assertEquals("http://example.com/s1.xml", sitemaps[0]);
        assertEquals("http://example.com/s2.xml", sitemaps[1]);
    }

    @Test
    public void testCheckRobotsTxt_charsetHonoured() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt",
                new RobotsTxtResponse(200, null, null, "User-agent: *\nDisallow: /a/\n".getBytes(StandardCharsets.UTF_16LE), "UTF-16LE"));

        assertDisallowed(context, "http://example.com/a/1", fetcher, POLICY);
        assertAllowed(context, "http://example.com/b/1", fetcher, POLICY);
    }

    @Test
    public void testCheckRobotsTxt_disabledOrNoHost() {
        final CrawlerContext context = new CrawlerContext();
        final ScriptedFetcher fetcher = new ScriptedFetcher().on("http://example.com/robots.txt", ok("User-agent: *\nDisallow: /\n"));

        assertAllowed(context, "file:///tmp/a.txt", fetcher, POLICY);
        assertEquals(0, fetcher.count());

        robotsTxtHelper.setEnabled(false);
        assertAllowed(context, "http://example.com/a", fetcher, POLICY);
        assertEquals(0, fetcher.count());
        assertNull(context.peekHostState("http://example.com/"));
    }

    @Test
    public void testParseRetryAfter() {
        final long now = 1_700_000_000_000L;
        final DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC);

        assertEquals(120000L, RobotsTxtHelper.parseRetryAfter("120", now));
        assertEquals(120000L, RobotsTxtHelper.parseRetryAfter(" 120 ", now));
        assertEquals(60000L, RobotsTxtHelper.parseRetryAfter(formatter.format(Instant.ofEpochMilli(now + 60000L)), now));
        assertEquals(60000L, RobotsTxtHelper.parseRetryAfter("Tue, 14 Nov 2023 22:14:20 GMT", now));
        assertEquals(0L, RobotsTxtHelper.parseRetryAfter(formatter.format(Instant.ofEpochMilli(now - 60000L)), now));
        assertEquals(0L, RobotsTxtHelper.parseRetryAfter("abc", now));
        assertEquals(0L, RobotsTxtHelper.parseRetryAfter("-5", now));
        assertEquals(0L, RobotsTxtHelper.parseRetryAfter("0", now));
        assertEquals(0L, RobotsTxtHelper.parseRetryAfter("1.5", now));
        assertEquals(0L, RobotsTxtHelper.parseRetryAfter("", now));
        assertEquals(0L, RobotsTxtHelper.parseRetryAfter(null, now));
        assertEquals(Long.MAX_VALUE, RobotsTxtHelper.parseRetryAfter("99999999999999999999", now));
    }
}
