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
import org.codelibs.fess.crawler.service.impl.UrlFilterServiceImpl;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 *
 */
public class CustomUrlFilterImplTest extends PlainTestCase {
    public UrlFilterImpl includeFilter;

    public UrlFilterImpl excludeFilter;

    public UrlFilterImpl domainFilter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer()
                .singleton("dataHelper", MemoryDataHelper.class)//
                .singleton("urlFilterService", UrlFilterServiceImpl.class)//
                .singleton("includeFilter", UrlFilterImpl.class)//
                .singleton("excludeFilter", UrlFilterImpl.class)//
                .singleton("domainFilter", UrlFilterImpl.class)//
        ;
        includeFilter = container.getComponent("includeFilter");
        includeFilter.setIncludeFilteringPattern("$1$2$3.*");
        excludeFilter = container.getComponent("excludeFilter");
        excludeFilter.setExcludeFilteringPattern("$1$2$3.*");
        domainFilter = container.getComponent("domainFilter");
        domainFilter.setIncludeFilteringPattern("http://$2/.*");
        domainFilter.setExcludeFilteringPattern("http://$2/.*");
    }

    public void test_include_processUrl() {
        assertEquals(0, includeFilter.cachedIncludeSet.size());
        assertEquals(0, includeFilter.cachedExcludeSet.size());

        includeFilter.processUrl("http://example.com/");

        assertEquals(1, includeFilter.cachedIncludeSet.size());
        assertEquals(0, includeFilter.cachedExcludeSet.size());
        assertEquals("http://example.com/.*",
                includeFilter.cachedIncludeSet.toArray()[0]);

        includeFilter.processUrl("https://test.com");

        assertEquals(2, includeFilter.cachedIncludeSet.size());
        assertEquals(0, includeFilter.cachedExcludeSet.size());
        assertEquals("https://test.com.*",
                includeFilter.cachedIncludeSet.toArray()[1]);
    }

    public void test_exclude_processUrl() {
        assertEquals(0, excludeFilter.cachedIncludeSet.size());
        assertEquals(0, excludeFilter.cachedExcludeSet.size());

        excludeFilter.processUrl("http://example.com/");

        assertEquals(0, excludeFilter.cachedIncludeSet.size());
        assertEquals(1, excludeFilter.cachedExcludeSet.size());
        assertEquals("http://example.com/.*",
                excludeFilter.cachedExcludeSet.toArray()[0]);

        excludeFilter.processUrl("https://test.com");

        assertEquals(0, excludeFilter.cachedIncludeSet.size());
        assertEquals(2, excludeFilter.cachedExcludeSet.size());
        assertEquals("https://test.com.*",
                excludeFilter.cachedExcludeSet.toArray()[1]);
    }

    public void test_domain_processUrl() {
        assertEquals(0, domainFilter.cachedIncludeSet.size());
        assertEquals(0, domainFilter.cachedExcludeSet.size());

        domainFilter.processUrl("http://example.com/");

        assertEquals(1, domainFilter.cachedIncludeSet.size());
        assertEquals(1, domainFilter.cachedExcludeSet.size());
        assertEquals("http://example.com/.*",
                domainFilter.cachedIncludeSet.toArray()[0]);
        assertEquals("http://example.com/.*",
                domainFilter.cachedExcludeSet.toArray()[0]);

        domainFilter.processUrl("https://test.com");

        assertEquals(2, domainFilter.cachedIncludeSet.size());
        assertEquals(2, domainFilter.cachedExcludeSet.size());
        assertEquals("http://test.com/.*",
                domainFilter.cachedIncludeSet.toArray()[1]);
        assertEquals("http://test.com/.*",
                domainFilter.cachedExcludeSet.toArray()[1]);
    }
}
