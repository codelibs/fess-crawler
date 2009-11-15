/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.robot.filter.impl;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.robot.helper.MemoryDataHelper;

/**
 * @author shinsuke
 *
 */
public class UrlFilterImplTest extends S2TestCase {
    public UrlFilterImpl urlFilter;

    public MemoryDataHelper dataHelper;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_addInclude() {
        assertEquals(0, urlFilter.cachedIncludeList.size());
        assertEquals(0, urlFilter.cachedExcludeList.size());

        urlFilter.addInclude(".*test.*");

        assertEquals(1, urlFilter.cachedIncludeList.size());
        assertEquals(0, urlFilter.cachedExcludeList.size());

        String sessionId = "id1";
        urlFilter.init(sessionId);
        assertEquals(0, urlFilter.cachedIncludeList.size());
        assertEquals(0, urlFilter.cachedExcludeList.size());
        assertEquals(1, dataHelper.getIncludeUrlPatternList(sessionId).size());
        assertEquals(0, dataHelper.getExcludeUrlPatternList(sessionId).size());
    }

    public void test_addExclude() {
        assertEquals(0, urlFilter.cachedIncludeList.size());
        assertEquals(0, urlFilter.cachedExcludeList.size());

        urlFilter.addExclude(".*test.*");

        assertEquals(0, urlFilter.cachedIncludeList.size());
        assertEquals(1, urlFilter.cachedExcludeList.size());

        String sessionId = "id1";
        urlFilter.init(sessionId);
        assertEquals(0, urlFilter.cachedIncludeList.size());
        assertEquals(0, urlFilter.cachedExcludeList.size());
        assertEquals(0, dataHelper.getIncludeUrlPatternList(sessionId).size());
        assertEquals(1, dataHelper.getExcludeUrlPatternList(sessionId).size());
    }

    public void test_match_include_case1() {
        urlFilter.addInclude("http://example.com/.*");

        String sessionId = "id1";
        urlFilter.init(sessionId);

        assertTrue(urlFilter.match("http://example.com/"));
        assertTrue(urlFilter.match("http://example.com/a"));
        assertFalse(urlFilter.match("http://test.com/"));
        assertFalse(urlFilter.match("http://test.com/a"));

    }

    public void test_match_include_case2() {
        urlFilter.addInclude("http://example.com/.*");
        urlFilter.addInclude("http://test.com/.*");

        String sessionId = "id1";
        urlFilter.init(sessionId);

        assertTrue(urlFilter.match("http://example.com/"));
        assertTrue(urlFilter.match("http://example.com/a"));
        assertTrue(urlFilter.match("http://test.com/"));
        assertTrue(urlFilter.match("http://test.com/a"));
    }

    public void test_match_exclude_case1() {
        urlFilter.addExclude("http://example.com/.*");

        String sessionId = "id1";
        urlFilter.init(sessionId);

        assertFalse(urlFilter.match("http://example.com/"));
        assertFalse(urlFilter.match("http://example.com/a"));
        assertTrue(urlFilter.match("http://test.com/"));
        assertTrue(urlFilter.match("http://test.com/a"));

    }

    public void test_match_exclude_case2() {
        urlFilter.addExclude("http://example.com/.*");
        urlFilter.addExclude("http://test.com/.*");

        String sessionId = "id1";
        urlFilter.init(sessionId);

        assertFalse(urlFilter.match("http://example.com/"));
        assertFalse(urlFilter.match("http://example.com/a"));
        assertFalse(urlFilter.match("http://test.com/"));
        assertFalse(urlFilter.match("http://test.com/a"));
    }

    public void test_match_both() {
        urlFilter.addInclude("http://example.com/.*");
        urlFilter.addExclude("http://example.com/a.*");

        String sessionId = "id1";
        urlFilter.init(sessionId);

        assertTrue(urlFilter.match("http://example.com/"));
        assertFalse(urlFilter.match("http://example.com/a"));
        assertFalse(urlFilter.match("http://test.com/"));
        assertFalse(urlFilter.match("http://test.com/a"));
    }

    public void test_processUrl() {
        assertEquals(0, urlFilter.cachedIncludeList.size());
        assertEquals(0, urlFilter.cachedExcludeList.size());

        urlFilter.processUrl("http://example.com/");

        assertEquals(0, urlFilter.cachedIncludeList.size());
        assertEquals(0, urlFilter.cachedExcludeList.size());
    }
}
