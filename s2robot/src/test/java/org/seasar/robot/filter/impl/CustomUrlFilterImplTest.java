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
        assertEquals(0, includeFilter.includeList.size());
        assertEquals(0, includeFilter.excludeList.size());

        includeFilter.processUrl("http://example.com/");

        assertEquals(1, includeFilter.includeList.size());
        assertEquals(0, includeFilter.excludeList.size());
        assertEquals("http://example.com/.*", includeFilter.includeList.get(0)
                .pattern());

        includeFilter.processUrl("https://test.com");

        assertEquals(2, includeFilter.includeList.size());
        assertEquals(0, includeFilter.excludeList.size());
        assertEquals("https://test.com.*", includeFilter.includeList.get(1)
                .pattern());
    }

    public void test_exclude_processUrl() {
        assertEquals(0, excludeFilter.includeList.size());
        assertEquals(0, excludeFilter.excludeList.size());

        excludeFilter.processUrl("http://example.com/");

        assertEquals(0, excludeFilter.includeList.size());
        assertEquals(1, excludeFilter.excludeList.size());
        assertEquals("http://example.com/.*", excludeFilter.excludeList.get(0)
                .pattern());

        excludeFilter.processUrl("https://test.com");

        assertEquals(0, excludeFilter.includeList.size());
        assertEquals(2, excludeFilter.excludeList.size());
        assertEquals("https://test.com.*", excludeFilter.excludeList.get(1)
                .pattern());
    }

    public void test_domain_processUrl() {
        assertEquals(0, domainFilter.includeList.size());
        assertEquals(0, domainFilter.excludeList.size());

        domainFilter.processUrl("http://example.com/");

        assertEquals(1, domainFilter.includeList.size());
        assertEquals(1, domainFilter.excludeList.size());
        assertEquals("http://example.com/.*", domainFilter.includeList.get(0)
                .pattern());
        assertEquals("http://example.com/.*", domainFilter.excludeList.get(0)
                .pattern());

        domainFilter.processUrl("https://test.com");

        assertEquals(2, domainFilter.includeList.size());
        assertEquals(2, domainFilter.excludeList.size());
        assertEquals("http://test.com/.*", domainFilter.includeList.get(1)
                .pattern());
        assertEquals("http://test.com/.*", domainFilter.excludeList.get(1)
                .pattern());
    }
}
