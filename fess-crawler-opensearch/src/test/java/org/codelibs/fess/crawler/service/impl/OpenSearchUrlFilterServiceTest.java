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
package org.codelibs.fess.crawler.service.impl;

import static org.codelibs.opensearch.runner.OpenSearchRunner.newConfigs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.codelibs.fess.crawler.client.FesenClient;
import org.codelibs.opensearch.runner.OpenSearchRunner;
import org.dbflute.utflute.lastadi.LastaDiTestCase;
import org.opensearch.index.query.QueryBuilders;

import jakarta.annotation.Resource;

/**
 * Test class for OpenSearchUrlFilterService.
 *
 * @author shinsuke
 */
public class OpenSearchUrlFilterServiceTest extends LastaDiTestCase {
    @Resource
    private OpenSearchUrlFilterService urlFilterService;

    @Resource
    private FesenClient fesenClient;

    private OpenSearchRunner runner;

    @Override
    protected String prepareConfigFile() {
        return "app.xml";
    }

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Override
    public void setUp() throws Exception {
        // create runner instance
        runner = new OpenSearchRunner();
        // create ES nodes
        final String clusterName = UUID.randomUUID().toString();
        runner.onBuild((number, settingsBuilder) -> {
            settingsBuilder.put("http.cors.enabled", true);
            settingsBuilder.put("discovery.type", "single-node");
        }).build(newConfigs().clusterName(clusterName).numOfNode(1));

        // wait for yellow status
        runner.ensureYellow();

        System.setProperty(FesenClient.HTTP_ADDRESS, "localhost:" + runner.node().settings().get("http.port", "9201"));

        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        // close runner
        runner.close();
        // delete all files
        runner.clean();
    }

    public void test_addIncludeUrlFilter_singleTx() {
        final String sessionId = "session1";
        final String urlPattern = "http://example.com/.*";

        urlFilterService.addIncludeUrlFilter(sessionId, urlPattern);

        // Verify the filter is stored
        assertTrue(fesenClient.prepareSearch("fess_crawler.filter")
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value() > 0);

        // Verify pattern can be retrieved
        final List<Pattern> patterns = urlFilterService.getIncludeUrlPatternList(sessionId);
        assertEquals(1, patterns.size());
        assertTrue(patterns.get(0).matcher("http://example.com/page1").matches());
        assertFalse(patterns.get(0).matcher("http://other.com/page1").matches());

        urlFilterService.delete(sessionId);
    }

    public void test_addExcludeUrlFilter_singleTx() {
        final String sessionId = "session2";
        final String urlPattern = "http://example.com/admin/.*";

        urlFilterService.addExcludeUrlFilter(sessionId, urlPattern);

        // Verify the filter is stored
        assertTrue(fesenClient.prepareSearch("fess_crawler.filter")
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value() > 0);

        // Verify pattern can be retrieved
        final List<Pattern> patterns = urlFilterService.getExcludeUrlPatternList(sessionId);
        assertEquals(1, patterns.size());
        assertTrue(patterns.get(0).matcher("http://example.com/admin/users").matches());
        assertFalse(patterns.get(0).matcher("http://example.com/public/users").matches());

        urlFilterService.delete(sessionId);
    }

    public void test_addIncludeUrlFilter_multiTx() {
        final String sessionId = "session3";
        final List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("http://example.com/.*");
        urlPatterns.add("http://test.com/.*");
        urlPatterns.add("http://sample.org/.*");

        urlFilterService.addIncludeUrlFilter(sessionId, urlPatterns);

        // Verify all filters are stored
        assertEquals(3, fesenClient.prepareSearch("fess_crawler.filter")
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value());

        // Verify all patterns can be retrieved
        final List<Pattern> patterns = urlFilterService.getIncludeUrlPatternList(sessionId);
        assertEquals(3, patterns.size());

        urlFilterService.delete(sessionId);
    }

    public void test_addExcludeUrlFilter_multiTx() {
        final String sessionId = "session4";
        final List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("http://example.com/admin/.*");
        urlPatterns.add("http://example.com/private/.*");

        urlFilterService.addExcludeUrlFilter(sessionId, urlPatterns);

        // Verify all filters are stored
        assertEquals(2, fesenClient.prepareSearch("fess_crawler.filter")
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value());

        // Verify all patterns can be retrieved
        final List<Pattern> patterns = urlFilterService.getExcludeUrlPatternList(sessionId);
        assertEquals(2, patterns.size());

        urlFilterService.delete(sessionId);
    }

    public void test_addIncludeUrlFilter_emptyListTx() {
        final String sessionId = "session5";
        final List<String> emptyList = Collections.emptyList();

        // Should not throw exception and should not create any entries
        urlFilterService.addIncludeUrlFilter(sessionId, emptyList);

        // Verify no filters are stored
        assertEquals(0, fesenClient.prepareSearch("fess_crawler.filter")
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value());
    }

    public void test_addExcludeUrlFilter_emptyListTx() {
        final String sessionId = "session6";
        final List<String> emptyList = new ArrayList<>();

        // Should not throw exception and should not create any entries
        urlFilterService.addExcludeUrlFilter(sessionId, emptyList);

        // Verify no filters are stored
        assertEquals(0, fesenClient.prepareSearch("fess_crawler.filter")
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value());
    }

    public void test_delete_multipleSessions() {
        final String sessionId1 = "session7";
        final String sessionId2 = "session8";

        urlFilterService.addIncludeUrlFilter(sessionId1, "http://example.com/.*");
        urlFilterService.addIncludeUrlFilter(sessionId2, "http://test.com/.*");

        // Verify both are stored
        assertTrue(fesenClient.prepareSearch("fess_crawler.filter")
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId1))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value() > 0);
        assertTrue(fesenClient.prepareSearch("fess_crawler.filter")
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId2))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value() > 0);

        // Delete session1
        urlFilterService.delete(sessionId1);

        // Verify session1 is deleted but session2 remains
        assertEquals(0, fesenClient.prepareSearch("fess_crawler.filter")
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId1))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value());
        assertTrue(fesenClient.prepareSearch("fess_crawler.filter")
                .setQuery(QueryBuilders.termQuery("sessionId", sessionId2))
                .setSize(0)
                .execute()
                .actionGet()
                .getHits()
                .getTotalHits()
                .value() > 0);

        urlFilterService.delete(sessionId2);
    }

    public void test_cacheInvalidation() {
        final String sessionId = "session9";

        // Add initial filter
        urlFilterService.addIncludeUrlFilter(sessionId, "http://example.com/.*");
        List<Pattern> patterns = urlFilterService.getIncludeUrlPatternList(sessionId);
        assertEquals(1, patterns.size());

        // Add more filters
        final List<String> additionalPatterns = new ArrayList<>();
        additionalPatterns.add("http://test.com/.*");
        additionalPatterns.add("http://sample.org/.*");
        urlFilterService.addIncludeUrlFilter(sessionId, additionalPatterns);

        // Cache should be invalidated and new patterns should be retrieved
        patterns = urlFilterService.getIncludeUrlPatternList(sessionId);
        assertEquals(3, patterns.size());

        urlFilterService.delete(sessionId);
    }

    public void test_mixedIncludeExcludeFilters() {
        final String sessionId = "session10";

        // Add both include and exclude filters
        urlFilterService.addIncludeUrlFilter(sessionId, "http://example.com/.*");
        urlFilterService.addExcludeUrlFilter(sessionId, "http://example.com/admin/.*");

        final List<Pattern> includePatterns = urlFilterService.getIncludeUrlPatternList(sessionId);
        final List<Pattern> excludePatterns = urlFilterService.getExcludeUrlPatternList(sessionId);

        assertEquals(1, includePatterns.size());
        assertEquals(1, excludePatterns.size());

        // Verify patterns work correctly
        assertTrue(includePatterns.get(0).matcher("http://example.com/page").matches());
        assertTrue(excludePatterns.get(0).matcher("http://example.com/admin/users").matches());
        assertFalse(excludePatterns.get(0).matcher("http://example.com/page").matches());

        urlFilterService.delete(sessionId);
    }
}
