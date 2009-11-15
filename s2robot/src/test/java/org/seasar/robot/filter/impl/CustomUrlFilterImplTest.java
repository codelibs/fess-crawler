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
public class CustomUrlFilterImplTest extends S2TestCase {
    public UrlFilterImpl includeFilter;

    public UrlFilterImpl excludeFilter;

    public UrlFilterImpl domainFilter;

    @Override
    protected String getRootDicon() throws Throwable {
        return "org/seasar/robot/filter/impl/custom_url_filter.dicon";
    }

    public void test_include_processUrl() {
        assertEquals(0, includeFilter.cachedIncludeList.size());
        assertEquals(0, includeFilter.cachedExcludeList.size());

        includeFilter.processUrl("http://example.com/");

        assertEquals(1, includeFilter.cachedIncludeList.size());
        assertEquals(0, includeFilter.cachedExcludeList.size());
        assertEquals("http://example.com/.*", includeFilter.cachedIncludeList
                .get(0));

        includeFilter.processUrl("https://test.com");

        assertEquals(2, includeFilter.cachedIncludeList.size());
        assertEquals(0, includeFilter.cachedExcludeList.size());
        assertEquals("https://test.com.*", includeFilter.cachedIncludeList
                .get(1));
    }

    public void test_exclude_processUrl() {
        assertEquals(0, excludeFilter.cachedIncludeList.size());
        assertEquals(0, excludeFilter.cachedExcludeList.size());

        excludeFilter.processUrl("http://example.com/");

        assertEquals(0, excludeFilter.cachedIncludeList.size());
        assertEquals(1, excludeFilter.cachedExcludeList.size());
        assertEquals("http://example.com/.*", excludeFilter.cachedExcludeList
                .get(0));

        excludeFilter.processUrl("https://test.com");

        assertEquals(0, excludeFilter.cachedIncludeList.size());
        assertEquals(2, excludeFilter.cachedExcludeList.size());
        assertEquals("https://test.com.*", excludeFilter.cachedExcludeList
                .get(1));
    }

    public void test_domain_processUrl() {
        assertEquals(0, domainFilter.cachedIncludeList.size());
        assertEquals(0, domainFilter.cachedExcludeList.size());

        domainFilter.processUrl("http://example.com/");

        assertEquals(1, domainFilter.cachedIncludeList.size());
        assertEquals(1, domainFilter.cachedExcludeList.size());
        assertEquals("http://example.com/.*", domainFilter.cachedIncludeList
                .get(0));
        assertEquals("http://example.com/.*", domainFilter.cachedExcludeList
                .get(0));

        domainFilter.processUrl("https://test.com");

        assertEquals(2, domainFilter.cachedIncludeList.size());
        assertEquals(2, domainFilter.cachedExcludeList.size());
        assertEquals("http://test.com/.*", domainFilter.cachedIncludeList
                .get(1));
        assertEquals("http://test.com/.*", domainFilter.cachedExcludeList
                .get(1));
    }
}
