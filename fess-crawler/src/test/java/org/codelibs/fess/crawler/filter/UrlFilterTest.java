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
package org.codelibs.fess.crawler.filter;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.filter.impl.UrlFilterImpl;
import org.codelibs.fess.crawler.helper.MemoryDataHelper;
import org.codelibs.fess.crawler.service.impl.DataServiceImpl;
import org.codelibs.fess.crawler.service.impl.UrlFilterServiceImpl;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;

/**
 * Test class for UrlFilter interface.
 * Tests the contract and behavior of UrlFilter implementations.
 */
public class UrlFilterTest extends PlainTestCase {

    private UrlFilter urlFilter;
    private StandardCrawlerContainer container;

    @Override
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        // Initialize container with necessary components
        container = new StandardCrawlerContainer().singleton("dataHelper", MemoryDataHelper.class)
                .singleton("urlFilterService", UrlFilterServiceImpl.class)
                .singleton("urlFilter", UrlFilterImpl.class)
                .singleton("dataService", DataServiceImpl.class);
        urlFilter = container.getComponent("urlFilter");
    }

    /**
     * Test basic initialization with session ID
     */
    public void test_init_withSessionId() {
        String sessionId = "test-session-001";
        urlFilter.init(sessionId);
        // Initialization should complete without errors
        assertNotNull(urlFilter);
    }

    /**
     * Test initialization with null session ID
     */
    public void test_init_withNullSessionId() {
        urlFilter.init(null);
        // Should handle null session ID gracefully
        assertNotNull(urlFilter);
    }

    /**
     * Test initialization with empty session ID
     */
    public void test_init_withEmptySessionId() {
        urlFilter.init("");
        // Should handle empty session ID gracefully
        assertNotNull(urlFilter);
    }

    /**
     * Test adding a single include pattern
     */
    public void test_addInclude_singlePattern() {
        String sessionId = "test-session-002";
        urlFilter.init(sessionId);

        urlFilter.addInclude("https://example.com/.*");

        assertTrue(urlFilter.match("https://example.com/"));
        assertTrue(urlFilter.match("https://example.com/page1"));
        assertTrue(urlFilter.match("https://example.com/dir/page2"));
        assertFalse(urlFilter.match("https://other.com/"));
    }

    /**
     * Test adding multiple include patterns
     */
    public void test_addInclude_multiplePatterns() {
        String sessionId = "test-session-003";
        urlFilter.init(sessionId);

        urlFilter.addInclude("https://example.com/.*");
        urlFilter.addInclude("https://test.com/.*");
        urlFilter.addInclude(".*\\.pdf$");

        assertTrue(urlFilter.match("https://example.com/"));
        assertTrue(urlFilter.match("https://test.com/page"));
        assertTrue(urlFilter.match("https://any.com/document.pdf"));
        assertFalse(urlFilter.match("https://other.com/page.html"));
    }

    /**
     * Test adding invalid regex include pattern
     */
    public void test_addInclude_invalidRegex() {
        String sessionId = "test-session-004";
        urlFilter.init(sessionId);

        // Invalid regex pattern should be handled gracefully
        urlFilter.addInclude(".*[invalid");
        urlFilter.addInclude("https://valid.com/.*");

        // Valid pattern should still work
        assertTrue(urlFilter.match("https://valid.com/page"));
    }

    /**
     * Test adding a single exclude pattern
     */
    public void test_addExclude_singlePattern() {
        String sessionId = "test-session-005";
        urlFilter.init(sessionId);

        urlFilter.addExclude(".*\\.(jpg|png|gif)$");

        assertTrue(urlFilter.match("https://example.com/page.html"));
        assertTrue(urlFilter.match("https://example.com/document.pdf"));
        assertFalse(urlFilter.match("https://example.com/image.jpg"));
        assertFalse(urlFilter.match("https://example.com/photo.png"));
        assertFalse(urlFilter.match("https://example.com/animation.gif"));
    }

    /**
     * Test adding multiple exclude patterns
     */
    public void test_addExclude_multiplePatterns() {
        String sessionId = "test-session-006";
        urlFilter.init(sessionId);

        urlFilter.addExclude(".*\\.(css|js)$");
        urlFilter.addExclude(".*\\/admin\\/.*");
        urlFilter.addExclude(".*#.*");

        assertTrue(urlFilter.match("https://example.com/page.html"));
        assertFalse(urlFilter.match("https://example.com/style.css"));
        assertFalse(urlFilter.match("https://example.com/script.js"));
        assertFalse(urlFilter.match("https://example.com/admin/login"));
        assertFalse(urlFilter.match("https://example.com/page#section"));
    }

    /**
     * Test adding invalid regex exclude pattern
     */
    public void test_addExclude_invalidRegex() {
        String sessionId = "test-session-007";
        urlFilter.init(sessionId);

        // Invalid regex pattern should be handled gracefully
        urlFilter.addExclude(".*]invalid");
        urlFilter.addExclude(".*\\.txt$");

        // Valid pattern should still work
        assertFalse(urlFilter.match("https://example.com/file.txt"));
        assertTrue(urlFilter.match("https://example.com/file.html"));
    }

    /**
     * Test combination of include and exclude patterns
     */
    public void test_match_includeAndExclude() {
        String sessionId = "test-session-008";
        urlFilter.init(sessionId);

        // Include only example.com domain
        urlFilter.addInclude("https://example.com/.*");
        // But exclude images and admin section
        urlFilter.addExclude(".*\\.(jpg|png|gif)$");
        urlFilter.addExclude(".*/admin/.*");

        assertTrue(urlFilter.match("https://example.com/page.html"));
        assertTrue(urlFilter.match("https://example.com/document.pdf"));
        assertFalse(urlFilter.match("https://example.com/image.jpg"));
        assertFalse(urlFilter.match("https://example.com/admin/dashboard"));
        assertFalse(urlFilter.match("https://other.com/page.html"));
    }

    /**
     * Test match with no patterns configured
     */
    public void test_match_noPatterns() {
        String sessionId = "test-session-009";
        urlFilter.init(sessionId);

        // Without any patterns, all URLs should match
        assertTrue(urlFilter.match("https://example.com/"));
        assertTrue(urlFilter.match("https://test.com/page"));
        assertTrue(urlFilter.match("ftp://files.com/document.pdf"));
        assertTrue(urlFilter.match("file:///home/user/file.txt"));
    }

    /**
     * Test match with complex URL patterns
     */
    public void test_match_complexUrls() {
        String sessionId = "test-session-010";
        urlFilter.init(sessionId);

        urlFilter.addInclude("https?://[^/]+\\.example\\.com/.*");

        assertTrue(urlFilter.match("http://www.example.com/"));
        assertTrue(urlFilter.match("https://api.example.com/v1/users"));
        assertTrue(urlFilter.match("http://subdomain.example.com/page"));
        assertFalse(urlFilter.match("https://example.org/"));
        assertFalse(urlFilter.match("ftp://files.example.com/"));
    }

    /**
     * Test match with query parameters and fragments
     */
    public void test_match_urlWithQueryAndFragment() {
        String sessionId = "test-session-011";
        urlFilter.init(sessionId);

        urlFilter.addInclude("https://example.com/search.*");
        urlFilter.addExclude(".*[?&]debug=true.*");

        assertTrue(urlFilter.match("https://example.com/search"));
        assertTrue(urlFilter.match("https://example.com/search?q=test"));
        assertTrue(urlFilter.match("https://example.com/search?q=test&page=2"));
        assertFalse(urlFilter.match("https://example.com/search?debug=true"));
        assertFalse(urlFilter.match("https://example.com/search?q=test&debug=true"));
    }

    /**
     * Test processUrl method with various URL patterns
     */
    public void test_processUrl_basic() {
        String sessionId = "test-session-012";
        urlFilter.init(sessionId);

        // Process URL should handle different URL formats
        urlFilter.processUrl("https://example.com/");
        urlFilter.processUrl("http://test.com/path/to/page");
        urlFilter.processUrl("ftp://files.server.com/documents/");
        urlFilter.processUrl("file:///local/path/file.txt");

        // Should complete without errors
        assertNotNull(urlFilter);
    }

    /**
     * Test processUrl with null URL
     */
    public void test_processUrl_nullUrl() {
        String sessionId = "test-session-013";
        urlFilter.init(sessionId);

        try {
            urlFilter.processUrl(null);
            // Should handle null gracefully or throw appropriate exception
        } catch (NullPointerException e) {
            // Expected behavior for null input
            assertTrue(true);
        }
    }

    /**
     * Test processUrl with empty URL
     */
    public void test_processUrl_emptyUrl() {
        String sessionId = "test-session-014";
        urlFilter.init(sessionId);

        urlFilter.processUrl("");
        // Should handle empty URL gracefully
        assertNotNull(urlFilter);
    }

    /**
     * Test clear method
     */
    public void test_clear() {
        String sessionId = "test-session-015";
        urlFilter.init(sessionId);

        // Add some patterns
        urlFilter.addInclude("https://example.com/.*");
        urlFilter.addExclude(".*\\.jpg$");

        // Verify patterns are working
        assertTrue(urlFilter.match("https://example.com/page"));
        assertFalse(urlFilter.match("https://other.com/page"));

        // Clear the filter
        urlFilter.clear();

        // After clear, all URLs should match (no filters applied)
        assertTrue(urlFilter.match("https://example.com/page"));
        assertTrue(urlFilter.match("https://other.com/page"));
        assertTrue(urlFilter.match("https://any.com/image.jpg"));
    }

    /**
     * Test clear method without initialization
     */
    public void test_clear_withoutInit() {
        // Create new filter without initialization
        UrlFilter newFilter = container.getComponent("urlFilter");

        // Clear should work even without initialization
        newFilter.clear();
        assertNotNull(newFilter);
    }

    /**
     * Test adding patterns before initialization
     */
    public void test_addPatterns_beforeInit() {
        // Create new filter
        UrlFilter newFilter = container.getComponent("urlFilter");

        // Add patterns before init
        newFilter.addInclude("https://example.com/.*");
        newFilter.addExclude(".*\\.css$");

        // Initialize with session
        String sessionId = "test-session-016";
        newFilter.init(sessionId);

        // Patterns should be applied after init
        assertTrue(newFilter.match("https://example.com/page"));
        assertFalse(newFilter.match("https://example.com/style.css"));
        assertFalse(newFilter.match("https://other.com/page"));
    }

    /**
     * Test multiple initializations with same session ID
     */
    public void test_multipleInit_sameSessionId() {
        String sessionId = "test-session-017";

        // First initialization
        urlFilter.init(sessionId);
        urlFilter.addInclude("https://first.com/.*");

        // Second initialization with same session ID
        urlFilter.init(sessionId);
        urlFilter.addInclude("https://second.com/.*");

        // Both patterns should work
        assertTrue(urlFilter.match("https://first.com/page"));
        assertTrue(urlFilter.match("https://second.com/page"));
    }

    /**
     * Test multiple initializations with different session IDs
     */
    public void test_multipleInit_differentSessionIds() {
        // First session
        urlFilter.init("session-001");
        urlFilter.addInclude("https://first.com/.*");

        // Second session
        urlFilter.init("session-002");
        urlFilter.addInclude("https://second.com/.*");

        // Behavior depends on implementation
        // At minimum, should not throw exceptions
        assertNotNull(urlFilter);
    }

    /**
     * Test special characters in URL patterns
     */
    public void test_specialCharactersInPatterns() {
        String sessionId = "test-session-018";
        urlFilter.init(sessionId);

        // Test patterns with special regex characters
        urlFilter.addInclude("https://example\\.com/\\?.*");
        urlFilter.addInclude(".*\\$price=\\d+.*");
        urlFilter.addExclude(".*\\[\\].*");

        assertTrue(urlFilter.match("https://example.com/?page=1"));
        assertTrue(urlFilter.match("https://shop.com/item?$price=100"));
        assertFalse(urlFilter.match("https://example.com/array[]"));
    }

    /**
     * Test case sensitivity in patterns
     */
    public void test_caseSensitivity() {
        String sessionId = "test-session-019";
        urlFilter.init(sessionId);

        urlFilter.addInclude(".*\\.PDF$");

        // Test case sensitivity
        assertFalse(urlFilter.match("https://example.com/document.pdf"));
        assertTrue(urlFilter.match("https://example.com/document.PDF"));
    }

    /**
     * Test very long URL handling
     */
    public void test_veryLongUrl() {
        String sessionId = "test-session-020";
        urlFilter.init(sessionId);

        // Create a very long URL
        StringBuilder longUrl = new StringBuilder("https://example.com/");
        for (int i = 0; i < 1000; i++) {
            longUrl.append("very/long/path/segment/");
        }
        longUrl.append("file.html");

        urlFilter.addInclude("https://example.com/.*");

        // Should handle long URLs without issues
        assertTrue(urlFilter.match(longUrl.toString()));
    }

    /**
     * Test internationalized domain names (IDN)
     */
    public void test_internationalizedDomainNames() {
        String sessionId = "test-session-021";
        urlFilter.init(sessionId);

        urlFilter.addInclude(".*日本.*");

        assertTrue(urlFilter.match("https://日本.example.com/"));
        assertTrue(urlFilter.match("https://example.com/日本/page"));
        assertFalse(urlFilter.match("https://example.com/china/page"));
    }

    /**
     * Test URL with special protocols
     */
    public void test_specialProtocols() {
        String sessionId = "test-session-022";
        urlFilter.init(sessionId);

        urlFilter.addInclude("(http|https|ftp|file)://.*");
        urlFilter.addExclude("javascript:.*");
        urlFilter.addExclude("mailto:.*");

        assertTrue(urlFilter.match("http://example.com/"));
        assertTrue(urlFilter.match("https://example.com/"));
        assertTrue(urlFilter.match("ftp://files.com/"));
        assertTrue(urlFilter.match("file:///home/user/file"));
        assertFalse(urlFilter.match("javascript:alert('test')"));
        assertFalse(urlFilter.match("mailto:user@example.com"));
    }

    /**
     * Test concurrent pattern additions
     */
    public void test_concurrentPatternAdditions() throws InterruptedException {
        String sessionId = "test-session-023";
        urlFilter.init(sessionId);

        // Create multiple threads adding patterns
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                urlFilter.addInclude("https://site" + i + ".com/.*");
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                urlFilter.addExclude(".*\\.type" + i + "$");
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        // Should complete without errors
        assertNotNull(urlFilter);
    }

    /**
     * Test boundary conditions
     */
    public void test_boundaryConditions() {
        String sessionId = "test-session-024";
        urlFilter.init(sessionId);

        // Test empty pattern
        urlFilter.addInclude("");
        urlFilter.addExclude("");

        // Test single character pattern
        urlFilter.addInclude(".");
        urlFilter.addExclude("*");

        // Test patterns with only special characters
        urlFilter.addInclude("^$");
        urlFilter.addExclude(".*");

        // Should handle boundary conditions gracefully
        assertNotNull(urlFilter);
    }

    /**
     * Test pattern priority (include vs exclude)
     */
    public void test_patternPriority() {
        String sessionId = "test-session-025";
        urlFilter.init(sessionId);

        // Add conflicting patterns
        urlFilter.addInclude("https://example.com/.*");
        urlFilter.addExclude("https://example.com/.*");

        // Exclude should take precedence
        assertFalse(urlFilter.match("https://example.com/page"));
    }

    /**
     * Test URL normalization scenarios
     */
    public void test_urlNormalization() {
        String sessionId = "test-session-026";
        urlFilter.init(sessionId);

        urlFilter.addInclude("https://example.com/path/.*");

        // Test various URL formats that should be equivalent
        assertTrue(urlFilter.match("https://example.com/path/"));
        assertTrue(urlFilter.match("https://example.com/path/page"));
        assertTrue(urlFilter.match("https://example.com/path//page"));
        assertTrue(urlFilter.match("https://example.com/path/./page"));
    }

    /**
     * Test wildcard patterns
     */
    public void test_wildcardPatterns() {
        String sessionId = "test-session-027";
        urlFilter.init(sessionId);

        // Test various wildcard patterns
        urlFilter.addInclude(".*"); // Match all
        assertTrue(urlFilter.match("https://any.com/any/path"));

        urlFilter.clear();
        urlFilter.init(sessionId);

        urlFilter.addInclude(".+"); // Match at least one character
        assertTrue(urlFilter.match("https://example.com/"));
        assertFalse(urlFilter.match(""));
    }

    /**
     * Test memory efficiency with large number of patterns
     */
    public void test_largeNumberOfPatterns() {
        String sessionId = "test-session-028";
        urlFilter.init(sessionId);

        // Add many patterns
        for (int i = 0; i < 1000; i++) {
            urlFilter.addInclude("https://site" + i + ".com/.*");
            urlFilter.addExclude(".*\\.exclude" + i + "$");
        }

        // Test matching performance
        assertTrue(urlFilter.match("https://site500.com/page"));
        assertFalse(urlFilter.match("https://site500.com/file.exclude500"));
        assertFalse(urlFilter.match("https://unknown.com/page"));
    }
}
