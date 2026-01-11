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
import org.junit.jupiter.api.BeforeEach;

public class RobotsTxtHelperTest extends PlainTestCase {
    public RobotsTxtHelper robotsTxtHelper;

    @Override
    @BeforeEach
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
        // Invalid number should be ignored, valid ones should work
        assertEquals(0, robotsTxt.getCrawlDelay("GoodBot")); // invalid values ignored

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
        assertTrue(robotsTxt.getCrawlDelay("NumericBot") >= 0);

        // Test TabBot - tab characters should be treated as whitespace
        assertFalse(robotsTxt.allows("/tab1/", "TabBot"));
        assertTrue(robotsTxt.allows("/tab2/", "TabBot"));

        // Test bots with special characters - should be normalized to lowercase
        assertFalse(robotsTxt.allows("/trademark/", "Bot™"));
        assertFalse(robotsTxt.allows("/registered/", "Bot®"));

        // Test wildcard user-agent
        assertFalse(robotsTxt.allows("/default/", "UnknownRandomBot"));
    }

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

    public void testParse_floatingPointCrawlDelay() {
        String robotsTxtContent = "User-agent: TestBot\n" + "Crawl-delay: 2.5\n" + "Disallow: /test/\n";

        RobotsTxt robotsTxt;
        final InputStream in = new java.io.ByteArrayInputStream(robotsTxtContent.getBytes());
        try {
            robotsTxt = robotsTxtHelper.parse(in);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        // Floating point crawl-delay should be ignored (as it expects integer)
        assertNotNull(robotsTxt);
        // Should either parse as 2 or be ignored
        assertTrue(robotsTxt.getCrawlDelay("TestBot") >= 0);
    }

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
}
