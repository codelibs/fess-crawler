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

import org.codelibs.fess.crawler.entity.RobotsTxt.Directive;
import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for {@link RobotsTxt} and {@link Directive}.
 */
public class RobotsTxtTest extends PlainTestCase {

    @Test
    public void test_defaultConstructor() {
        // Test default constructor
        RobotsTxt robotsTxt = new RobotsTxt();
        assertNotNull(robotsTxt);
    }

    @Test
    public void test_allowsWithNoDirectives() {
        // Test allows method when no directives are set
        RobotsTxt robotsTxt = new RobotsTxt();

        // Should return true when no directives match
        assertTrue(robotsTxt.allows("/path", "MyBot"));
        assertTrue(robotsTxt.allows("/admin", "GoogleBot"));
    }

    @Test
    public void test_allowsWithAllowedPath() {
        // Test allows method with allowed path
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("MyBot");
        directive.addAllow("/public/");
        robotsTxt.addDirective(directive);

        assertTrue(robotsTxt.allows("/public/page.html", "MyBot"));
        assertTrue(robotsTxt.allows("/public/", "MyBot"));
    }

    @Test
    public void test_allowsWithDisallowedPath() {
        // Test allows method with disallowed path
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("MyBot");
        directive.addDisallow("/admin/");
        robotsTxt.addDirective(directive);

        assertFalse(robotsTxt.allows("/admin/", "MyBot"));
        assertFalse(robotsTxt.allows("/admin/secret.html", "MyBot"));
        assertTrue(robotsTxt.allows("/public/", "MyBot")); // Not disallowed
    }

    @Test
    public void test_allowsWithAllowOverridingDisallow() {
        // Test that allow takes precedence over disallow
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("MyBot");
        directive.addDisallow("/admin/");
        directive.addAllow("/admin/public/");
        robotsTxt.addDirective(directive);

        // Allow should take precedence
        assertTrue(robotsTxt.allows("/admin/public/page.html", "MyBot"));
        assertFalse(robotsTxt.allows("/admin/secret.html", "MyBot"));
    }

    @Test
    public void test_getCrawlDelayWithNoDirective() {
        // Test getCrawlDelay when no directive matches
        RobotsTxt robotsTxt = new RobotsTxt();

        assertEquals(0, robotsTxt.getCrawlDelay("MyBot"));
    }

    @Test
    public void test_getCrawlDelayWithDirective() {
        // Test getCrawlDelay with directive
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("MyBot");
        directive.setCrawlDelay(5);
        robotsTxt.addDirective(directive);

        assertEquals(5, robotsTxt.getCrawlDelay("MyBot"));
    }

    @Test
    public void test_getDirective() {
        // Test getDirective method
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("MyBot");
        robotsTxt.addDirective(directive);

        Directive retrieved = robotsTxt.getDirective("MyBot");
        assertNotNull(retrieved);
        assertEquals("MyBot", retrieved.getUserAgent());
    }

    @Test
    public void test_getDirectiveNotFound() {
        // Test getDirective when directive doesn't exist
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("MyBot");
        robotsTxt.addDirective(directive);

        Directive retrieved = robotsTxt.getDirective("OtherBot");
        assertNull(retrieved);
    }

    @Test
    public void test_getDirectiveWithNull() {
        // Test getDirective with null user agent
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive retrieved = robotsTxt.getDirective(null);
        assertNull(retrieved);
    }

    @Test
    public void test_getMatchedDirectiveWithExactMatch() {
        // Test getMatchedDirective with exact match
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("MyBot");
        directive.addDisallow("/admin/");
        robotsTxt.addDirective(directive);

        Directive matched = robotsTxt.getMatchedDirective("MyBot");
        assertNotNull(matched);
        assertEquals("MyBot", matched.getUserAgent());
    }

    @Test
    public void test_getMatchedDirectiveWithWildcard() {
        // Test getMatchedDirective with wildcard
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("*");
        directive.addDisallow("/private/");
        robotsTxt.addDirective(directive);

        Directive matched = robotsTxt.getMatchedDirective("AnyBot");
        assertNotNull(matched);
        assertEquals("*", matched.getUserAgent());
    }

    @Test
    public void test_getMatchedDirectiveMostSpecific() {
        // Test that getMatchedDirective returns most specific match
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive wildcardDirective = new Directive("*");
        wildcardDirective.addDisallow("/");
        robotsTxt.addDirective(wildcardDirective);

        Directive specificDirective = new Directive("GoogleBot");
        specificDirective.addDisallow("/admin/");
        robotsTxt.addDirective(specificDirective);

        // Should match the more specific directive
        Directive matched = robotsTxt.getMatchedDirective("GoogleBot");
        assertNotNull(matched);
        assertEquals("GoogleBot", matched.getUserAgent());
    }

    @Test
    public void test_getMatchedDirectiveWithPartialMatch() {
        // Test getMatchedDirective with partial pattern match
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("Google*");
        directive.addDisallow("/private/");
        robotsTxt.addDirective(directive);

        Directive matched = robotsTxt.getMatchedDirective("GoogleBot");
        assertNotNull(matched);
        assertEquals("Google*", matched.getUserAgent());
    }

    @Test
    public void test_getMatchedDirectiveWithNullUserAgent() {
        // Test getMatchedDirective with null user agent
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("*");
        robotsTxt.addDirective(directive);

        Directive matched = robotsTxt.getMatchedDirective(null);
        assertNotNull(matched);
    }

    @Test
    public void test_addSitemap() {
        // Test addSitemap method
        RobotsTxt robotsTxt = new RobotsTxt();

        robotsTxt.addSitemap("https://example.com/sitemap.xml");
        robotsTxt.addSitemap("https://example.com/sitemap2.xml");

        String[] sitemaps = robotsTxt.getSitemaps();
        assertEquals(2, sitemaps.length);
        assertEquals("https://example.com/sitemap.xml", sitemaps[0]);
        assertEquals("https://example.com/sitemap2.xml", sitemaps[1]);
    }

    @Test
    public void test_addSitemapNoDuplicates() {
        // Test that addSitemap doesn't add duplicates
        RobotsTxt robotsTxt = new RobotsTxt();

        robotsTxt.addSitemap("https://example.com/sitemap.xml");
        robotsTxt.addSitemap("https://example.com/sitemap.xml");

        String[] sitemaps = robotsTxt.getSitemaps();
        assertEquals(1, sitemaps.length);
    }

    @Test
    public void test_getSitemapsEmpty() {
        // Test getSitemaps when no sitemaps are added
        RobotsTxt robotsTxt = new RobotsTxt();

        String[] sitemaps = robotsTxt.getSitemaps();
        assertNotNull(sitemaps);
        assertEquals(0, sitemaps.length);
    }

    @Test
    public void test_toString() {
        // Test toString method
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("MyBot");
        robotsTxt.addDirective(directive);
        robotsTxt.addSitemap("https://example.com/sitemap.xml");

        String result = robotsTxt.toString();
        assertNotNull(result);
        assertTrue(result.contains("RobotsTxt"));
    }

    // Directive tests
    @Test
    public void test_directiveConstructor() {
        // Test Directive constructor
        Directive directive = new Directive("MyBot");

        assertNotNull(directive);
        assertEquals("MyBot", directive.getUserAgent());
        assertEquals(0, directive.getCrawlDelay());
    }

    @Test
    public void test_directiveCrawlDelay() {
        // Test Directive crawl delay
        Directive directive = new Directive("MyBot");

        directive.setCrawlDelay(10);
        assertEquals(10, directive.getCrawlDelay());

        directive.setCrawlDelay(0);
        assertEquals(0, directive.getCrawlDelay());
    }

    @Test
    public void test_directiveAddAllow() {
        // Test Directive addAllow
        Directive directive = new Directive("MyBot");

        directive.addAllow("/public/");
        directive.addAllow("/images/");

        String[] allows = directive.getAllows();
        assertEquals(2, allows.length);
        assertEquals("/public/", allows[0]);
        assertEquals("/images/", allows[1]);
    }

    @Test
    public void test_directiveAddAllowNoDuplicates() {
        // Test that addAllow doesn't add duplicates
        Directive directive = new Directive("MyBot");

        directive.addAllow("/public/");
        directive.addAllow("/public/");

        String[] allows = directive.getAllows();
        assertEquals(1, allows.length);
    }

    @Test
    public void test_directiveAddDisallow() {
        // Test Directive addDisallow
        Directive directive = new Directive("MyBot");

        directive.addDisallow("/admin/");
        directive.addDisallow("/private/");

        String[] disallows = directive.getDisallows();
        assertEquals(2, disallows.length);
        assertEquals("/admin/", disallows[0]);
        assertEquals("/private/", disallows[1]);
    }

    @Test
    public void test_directiveAddDisallowNoDuplicates() {
        // Test that addDisallow doesn't add duplicates
        Directive directive = new Directive("MyBot");

        directive.addDisallow("/admin/");
        directive.addDisallow("/admin/");

        String[] disallows = directive.getDisallows();
        assertEquals(1, disallows.length);
    }

    @Test
    public void test_directiveAllowsAllowedPath() {
        // Test Directive allows with allowed path
        Directive directive = new Directive("MyBot");
        directive.addAllow("/public/");

        assertTrue(directive.allows("/public/page.html"));
        assertTrue(directive.allows("/public/"));
    }

    @Test
    public void test_directiveAllowsDisallowedPath() {
        // Test Directive allows with disallowed path
        Directive directive = new Directive("MyBot");
        directive.addDisallow("/admin/");

        assertFalse(directive.allows("/admin/page.html"));
        assertFalse(directive.allows("/admin/"));
    }

    @Test
    public void test_directiveAllowsDefaultAllow() {
        // Test that paths are allowed by default
        Directive directive = new Directive("MyBot");

        assertTrue(directive.allows("/anything"));
        assertTrue(directive.allows("/public/"));
        assertTrue(directive.allows("/admin/"));
    }

    @Test
    public void test_directiveAllowsPrecedence() {
        // Test that allow takes precedence over disallow
        Directive directive = new Directive("MyBot");
        directive.addDisallow("/admin/");
        directive.addAllow("/admin/public/");

        assertTrue(directive.allows("/admin/public/page.html"));
        assertFalse(directive.allows("/admin/secret.html"));
    }

    @Test
    public void test_complexScenario() {
        // Test complex robots.txt scenario
        RobotsTxt robotsTxt = new RobotsTxt();

        // Add directive for all bots
        Directive allBotsDirective = new Directive("*");
        allBotsDirective.addDisallow("/admin/");
        allBotsDirective.addDisallow("/private/");
        allBotsDirective.setCrawlDelay(1);
        robotsTxt.addDirective(allBotsDirective);

        // Add directive for GoogleBot
        Directive googleBotDirective = new Directive("GoogleBot");
        googleBotDirective.addDisallow("/admin/");
        googleBotDirective.addAllow("/admin/public/");
        googleBotDirective.setCrawlDelay(0);
        robotsTxt.addDirective(googleBotDirective);

        // Add directive for BingBot
        Directive bingBotDirective = new Directive("BingBot");
        bingBotDirective.addDisallow("/api/");
        bingBotDirective.setCrawlDelay(2);
        robotsTxt.addDirective(bingBotDirective);

        // Add sitemaps
        robotsTxt.addSitemap("https://example.com/sitemap.xml");
        robotsTxt.addSitemap("https://example.com/sitemap-images.xml");

        // Test GoogleBot
        assertFalse(robotsTxt.allows("/admin/secret.html", "GoogleBot"));
        assertTrue(robotsTxt.allows("/admin/public/info.html", "GoogleBot"));
        assertTrue(robotsTxt.allows("/public/", "GoogleBot"));
        assertEquals(0, robotsTxt.getCrawlDelay("GoogleBot"));

        // Test BingBot
        assertFalse(robotsTxt.allows("/api/endpoint", "BingBot"));
        assertTrue(robotsTxt.allows("/admin/", "BingBot")); // BingBot directive is more specific than * directive
        assertTrue(robotsTxt.allows("/public/", "BingBot"));
        assertEquals(2, robotsTxt.getCrawlDelay("BingBot"));

        // Test unknown bot (should match * directive)
        assertFalse(robotsTxt.allows("/admin/", "UnknownBot"));
        assertFalse(robotsTxt.allows("/private/", "UnknownBot"));
        assertTrue(robotsTxt.allows("/public/", "UnknownBot"));
        assertEquals(1, robotsTxt.getCrawlDelay("UnknownBot"));

        // Test sitemaps
        String[] sitemaps = robotsTxt.getSitemaps();
        assertEquals(2, sitemaps.length);
        assertEquals("https://example.com/sitemap.xml", sitemaps[0]);
    }

    @Test
    public void test_caseInsensitiveUserAgent() {
        // Test case-insensitive user agent matching
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("googlebot");
        directive.addDisallow("/private/");
        robotsTxt.addDirective(directive);

        // Should match case-insensitively
        assertFalse(robotsTxt.allows("/private/", "GoogleBot"));
        assertFalse(robotsTxt.allows("/private/", "GOOGLEBOT"));
        assertFalse(robotsTxt.allows("/private/", "googlebot"));
    }

    @Test
    public void test_userAgentWithSpecialRegexChars() {
        // Test that special regex characters in user-agent are escaped properly
        RobotsTxt robotsTxt = new RobotsTxt();

        // "Bot.v2" should not match "Botxv2" (dot should be literal)
        Directive directive = new Directive("bot.v2");
        directive.addDisallow("/private/");
        robotsTxt.addDirective(directive);

        assertFalse(robotsTxt.allows("/private/", "Bot.v2"));
        assertTrue(robotsTxt.allows("/private/", "Botxv2"));

        // "Bot+Plus" should not match "BotPlus"
        RobotsTxt robotsTxt2 = new RobotsTxt();
        Directive directive2 = new Directive("bot+plus");
        directive2.addDisallow("/admin/");
        robotsTxt2.addDirective(directive2);

        assertFalse(robotsTxt2.allows("/admin/", "Bot+Plus"));
        assertTrue(robotsTxt2.allows("/admin/", "BotPlus"));
    }

    @Test
    public void test_percentEncodedPathMatching() {
        // Test RFC 9309 percent-encoded path matching
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("*");
        directive.addDisallow("/dir/%E4%B8%AD%E6%96%87/");
        robotsTxt.addDirective(directive);

        // Should match the encoded form directly
        assertFalse(robotsTxt.allows("/dir/%E4%B8%AD%E6%96%87/", "AnyBot"));
        // Should match case-insensitive percent encoding
        assertFalse(robotsTxt.allows("/dir/%e4%b8%ad%e6%96%87/", "AnyBot"));
    }

    @Test
    public void test_percentEncodedUnreservedCharacters() {
        // RFC 9309: unreserved percent-encoded characters should be decoded for matching
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("*");
        directive.addDisallow("/path/file~name/");
        robotsTxt.addDirective(directive);

        // Unreserved character '~' encoded as %7E should match literal '~'
        assertFalse(robotsTxt.allows("/path/file%7Ename/", "AnyBot"));
        assertFalse(robotsTxt.allows("/path/file~name/", "AnyBot"));
    }

    @Test
    public void test_reservedCharactersStayEncoded() {
        // RFC 9309: reserved characters must stay encoded - %2F, %3F, %23 must not be decoded
        RobotsTxt robotsTxt = new RobotsTxt();

        // %2F is encoded '/' - should NOT match literal '/'
        Directive directive = new Directive("*");
        directive.addDisallow("/path%2Fhidden/");
        robotsTxt.addDirective(directive);

        assertFalse(robotsTxt.allows("/path%2Fhidden/", "AnyBot"));
        // Should NOT match decoded form since %2F is a reserved character
        assertTrue(robotsTxt.allows("/path/hidden/", "AnyBot"));
    }

    @Test
    public void test_encodedMetacharactersNotReinterpreted() {
        // %2A (encoded '*') must NOT become a wildcard
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("*");
        directive.addDisallow("/path/file%2A.html");
        robotsTxt.addDirective(directive);

        // Should match encoded form literally
        assertFalse(robotsTxt.allows("/path/file%2A.html", "AnyBot"));
        // Should NOT act as wildcard matching arbitrary strings
        assertTrue(robotsTxt.allows("/path/fileXYZ.html", "AnyBot"));

        // %24 (encoded '$') must NOT become end-of-path anchor
        RobotsTxt robotsTxt2 = new RobotsTxt();
        Directive directive2 = new Directive("*");
        directive2.addDisallow("/path/cost%24");
        robotsTxt2.addDirective(directive2);

        assertFalse(robotsTxt2.allows("/path/cost%24", "AnyBot"));
        assertFalse(robotsTxt2.allows("/path/cost%24extra", "AnyBot"));
    }

    @Test
    public void test_plusSignNotDecodedAsSpace() {
        // RFC 3986: '+' is a literal character in URI paths, not a space.
        RobotsTxt robotsTxt = new RobotsTxt();

        Directive directive = new Directive("*");
        directive.addDisallow("/search?q=a+b");
        robotsTxt.addDirective(directive);

        assertFalse(robotsTxt.allows("/search?q=a+b", "AnyBot"));
        assertTrue(robotsTxt.allows("/search?q=a b", "AnyBot"));
    }
}
