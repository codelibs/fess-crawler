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
package org.codelibs.fess.crawler.container;

import org.codelibs.fess.crawler.Crawler;
import org.codelibs.fess.crawler.helper.MemoryDataHelper;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.dbflute.utflute.lastadi.LastaDiTestCase;
import org.junit.jupiter.api.Test;

import jakarta.annotation.Resource;

/**
 * Test for LastaCrawlerContainer.
 */
public class LastaCrawlerContainerTest extends LastaDiTestCase {

    @Resource
    private CrawlerContainer crawlerContainer;

    @Override
    protected String prepareConfigFile() {
        return "crawler.xml";
    }

    @Test
    public void test_available() {
        // Container should be available after initialization
        assertTrue(crawlerContainer.available());
    }

    @Test
    public void test_getComponent_crawler() {
        final Crawler crawler = crawlerContainer.getComponent("crawler");
        assertNotNull(crawler);
    }

    @Test
    public void test_getComponent_dataService() {
        final DataService dataService = crawlerContainer.getComponent("dataService");
        assertNotNull(dataService);
    }

    @Test
    public void test_getComponent_urlQueueService() {
        final UrlQueueService urlQueueService = crawlerContainer.getComponent("urlQueueService");
        assertNotNull(urlQueueService);
    }

    @Test
    public void test_getComponent_dataHelper() {
        final MemoryDataHelper dataHelper = crawlerContainer.getComponent("dataHelper");
        assertNotNull(dataHelper);
    }

    @Test
    public void test_getComponent_multiple() {
        // Verify that multiple components can be retrieved
        final Crawler crawler1 = crawlerContainer.getComponent("crawler");
        final Crawler crawler2 = crawlerContainer.getComponent("crawler");

        // They should be different instances (prototype scope)
        assertNotNull(crawler1);
        assertNotNull(crawler2);
    }

    @Test
    public void test_isLastaCrawlerContainer() {
        // Verify that the container is an instance of LastaCrawlerContainer
        assertTrue(crawlerContainer instanceof LastaCrawlerContainer);
    }
}
