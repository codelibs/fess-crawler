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
package org.codelibs.fess.crawler.filter.impl;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.helper.MemoryDataHelper;
import org.codelibs.fess.crawler.service.impl.DataServiceImpl;
import org.codelibs.fess.crawler.service.impl.UrlFilterServiceImpl;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 *
 */
public class UrlFilterImplTest extends PlainTestCase {
    public UrlFilterImpl urlFilter;

    public MemoryDataHelper dataHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer()
                .singleton("dataHelper", MemoryDataHelper.class)//
                .singleton("urlFilterService", UrlFilterServiceImpl.class)//
                .singleton("urlFilter", UrlFilterImpl.class)//
                .singleton("dataService", DataServiceImpl.class);
        urlFilter = container.getComponent("urlFilter");
        dataHelper = container.getComponent("dataHelper");
    }

    public void test_addInclude1() {
        assertEquals(0, urlFilter.cachedIncludeSet.size());
        assertEquals(0, urlFilter.cachedExcludeSet.size());

        urlFilter.addInclude(".*test.*");

        assertEquals(1, urlFilter.cachedIncludeSet.size());
        assertEquals(0, urlFilter.cachedExcludeSet.size());

        final String sessionId = "id1";
        urlFilter.init(sessionId);
        assertEquals(0, urlFilter.cachedIncludeSet.size());
        assertEquals(0, urlFilter.cachedExcludeSet.size());
        assertEquals(1, dataHelper.getIncludeUrlPatternList(sessionId).size());
        assertEquals(0, dataHelper.getExcludeUrlPatternList(sessionId).size());
    }

    public void test_addInclude2() {
        assertEquals(0, urlFilter.cachedIncludeSet.size());
        assertEquals(0, urlFilter.cachedExcludeSet.size());

        urlFilter.addInclude(".*[test.*");

        assertEquals(0, urlFilter.cachedIncludeSet.size());
        assertEquals(0, urlFilter.cachedExcludeSet.size());

        final String sessionId = "id1";
        urlFilter.init(sessionId);
        assertEquals(0, urlFilter.cachedIncludeSet.size());
        assertEquals(0, urlFilter.cachedExcludeSet.size());
        assertEquals(0, dataHelper.getIncludeUrlPatternList(sessionId).size());
        assertEquals(0, dataHelper.getExcludeUrlPatternList(sessionId).size());
    }

    public void test_addExclude1() {
        assertEquals(0, urlFilter.cachedIncludeSet.size());
        assertEquals(0, urlFilter.cachedExcludeSet.size());

        urlFilter.addExclude(".*test.*");

        assertEquals(0, urlFilter.cachedIncludeSet.size());
        assertEquals(1, urlFilter.cachedExcludeSet.size());

        final String sessionId = "id1";
        urlFilter.init(sessionId);
        assertEquals(0, urlFilter.cachedIncludeSet.size());
        assertEquals(0, urlFilter.cachedExcludeSet.size());
        assertEquals(0, dataHelper.getIncludeUrlPatternList(sessionId).size());
        assertEquals(1, dataHelper.getExcludeUrlPatternList(sessionId).size());
    }

    public void test_addExclude2() {
        assertEquals(0, urlFilter.cachedIncludeSet.size());
        assertEquals(0, urlFilter.cachedExcludeSet.size());

        urlFilter.addExclude(".*[test.*");

        assertEquals(0, urlFilter.cachedIncludeSet.size());
        assertEquals(0, urlFilter.cachedExcludeSet.size());

        final String sessionId = "id1";
        urlFilter.init(sessionId);
        assertEquals(0, urlFilter.cachedIncludeSet.size());
        assertEquals(0, urlFilter.cachedExcludeSet.size());
        assertEquals(0, dataHelper.getIncludeUrlPatternList(sessionId).size());
        assertEquals(0, dataHelper.getExcludeUrlPatternList(sessionId).size());
    }

    public void test_match_include_case1() {
        urlFilter.addInclude("http://example.com/.*");

        final String sessionId = "id1";
        urlFilter.init(sessionId);

        assertTrue(urlFilter.match("http://example.com/"));
        assertTrue(urlFilter.match("http://example.com/a"));
        assertFalse(urlFilter.match("http://test.com/"));
        assertFalse(urlFilter.match("http://test.com/a"));

    }

    public void test_match_include_case2() {
        urlFilter.addInclude("http://example.com/.*");
        urlFilter.addInclude("http://test.com/.*");

        final String sessionId = "id1";
        urlFilter.init(sessionId);

        assertTrue(urlFilter.match("http://example.com/"));
        assertTrue(urlFilter.match("http://example.com/a"));
        assertTrue(urlFilter.match("http://test.com/"));
        assertTrue(urlFilter.match("http://test.com/a"));
    }

    public void test_match_exclude_case1() {
        urlFilter.addExclude("http://example.com/.*");

        final String sessionId = "id1";
        urlFilter.init(sessionId);

        assertFalse(urlFilter.match("http://example.com/"));
        assertFalse(urlFilter.match("http://example.com/a"));
        assertTrue(urlFilter.match("http://test.com/"));
        assertTrue(urlFilter.match("http://test.com/a"));

    }

    public void test_match_exclude_case2() {
        urlFilter.addExclude("http://example.com/.*");
        urlFilter.addExclude("http://test.com/.*");

        final String sessionId = "id1";
        urlFilter.init(sessionId);

        assertFalse(urlFilter.match("http://example.com/"));
        assertFalse(urlFilter.match("http://example.com/a"));
        assertFalse(urlFilter.match("http://test.com/"));
        assertFalse(urlFilter.match("http://test.com/a"));
    }

    public void test_match_both() {
        urlFilter.addInclude("http://example.com/.*");
        urlFilter.addExclude("http://example.com/a.*");

        final String sessionId = "id1";
        urlFilter.init(sessionId);

        assertTrue(urlFilter.match("http://example.com/"));
        assertFalse(urlFilter.match("http://example.com/a"));
        assertFalse(urlFilter.match("http://test.com/"));
        assertFalse(urlFilter.match("http://test.com/a"));
    }

    public void test_processUrl() {
        assertEquals(0, urlFilter.cachedIncludeSet.size());
        assertEquals(0, urlFilter.cachedExcludeSet.size());

        urlFilter.processUrl("http://example.com/");

        assertEquals(0, urlFilter.cachedIncludeSet.size());
        assertEquals(0, urlFilter.cachedExcludeSet.size());
    }
}
