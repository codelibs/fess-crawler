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

/**
 * @author shinsuke
 *
 */
public class UrlFilterImplTest extends S2TestCase {
    public UrlFilterImpl urlFilter;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_addInclude() {
        assertEquals(0, urlFilter.includeList.size());
        assertEquals(0, urlFilter.excludeList.size());

        urlFilter.addInclude(".*test.*");

        assertEquals(1, urlFilter.includeList.size());
        assertEquals(0, urlFilter.excludeList.size());
    }

    public void test_addExclude() {
        assertEquals(0, urlFilter.includeList.size());
        assertEquals(0, urlFilter.excludeList.size());

        urlFilter.addExclude(".*test.*");

        assertEquals(0, urlFilter.includeList.size());
        assertEquals(1, urlFilter.excludeList.size());
    }

    public void test_match_include() {
        urlFilter.addInclude("http://example.com/.*");

        assertTrue(urlFilter.match("http://example.com/"));
        assertTrue(urlFilter.match("http://example.com/a"));
        assertFalse(urlFilter.match("http://test.com/"));
        assertFalse(urlFilter.match("http://test.com/a"));

        urlFilter.addInclude("http://test.com/.*");

        assertTrue(urlFilter.match("http://example.com/"));
        assertTrue(urlFilter.match("http://example.com/a"));
        assertTrue(urlFilter.match("http://test.com/"));
        assertTrue(urlFilter.match("http://test.com/a"));
    }

    public void test_match_exclude() {
        urlFilter.addExclude("http://example.com/.*");

        assertFalse(urlFilter.match("http://example.com/"));
        assertFalse(urlFilter.match("http://example.com/a"));
        assertTrue(urlFilter.match("http://test.com/"));
        assertTrue(urlFilter.match("http://test.com/a"));

        urlFilter.addExclude("http://test.com/.*");

        assertFalse(urlFilter.match("http://example.com/"));
        assertFalse(urlFilter.match("http://example.com/a"));
        assertFalse(urlFilter.match("http://test.com/"));
        assertFalse(urlFilter.match("http://test.com/a"));
    }

    public void test_match_both() {
        urlFilter.addInclude("http://example.com/.*");
        urlFilter.addExclude("http://example.com/a.*");

        assertTrue(urlFilter.match("http://example.com/"));
        assertFalse(urlFilter.match("http://example.com/a"));
        assertFalse(urlFilter.match("http://test.com/"));
        assertFalse(urlFilter.match("http://test.com/a"));
    }

    public void test_processUrl() {
        assertEquals(0, urlFilter.includeList.size());
        assertEquals(0, urlFilter.excludeList.size());

        urlFilter.processUrl("http://example.com/");

        assertEquals(0, urlFilter.includeList.size());
        assertEquals(0, urlFilter.excludeList.size());
    }
}
