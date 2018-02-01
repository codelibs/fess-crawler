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
package org.codelibs.fess.crawler.service.impl;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.AccessResultImpl;
import org.codelibs.fess.crawler.helper.MemoryDataHelper;
import org.codelibs.fess.crawler.service.DataService;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 *
 */
public class DataServiceImplTest extends PlainTestCase {
    public DataService dataService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer()
                .singleton("dataHelper", MemoryDataHelper.class)//
                .singleton("dataService", DataServiceImpl.class);
        dataService = container.getComponent("dataService");
    }

    public void test_insert_deleteTx() {
        final AccessResult accessResult1 = new AccessResultImpl();
        accessResult1.setContentLength(Long.valueOf(10));
        accessResult1.setCreateTime(System.currentTimeMillis());
        accessResult1.setExecutionTime(10);
        accessResult1.setHttpStatusCode(200);
        accessResult1.setLastModified(System.currentTimeMillis());
        accessResult1.setMethod("GET");
        accessResult1.setMimeType("text/plain");
        accessResult1.setParentUrl("http://www.parent.com/");
        accessResult1.setRuleId("htmlRule");
        accessResult1.setSessionId("id1");
        accessResult1.setStatus(200);
        accessResult1.setUrl("http://www.id1.com/");

        dataService.store(accessResult1);

        final AccessResult accessResult2 = dataService.getAccessResult("id1", "http://www.id1.com/");
        assertNotNull(accessResult2);

        accessResult2.setMimeType("text/html");
        dataService.update(accessResult2);

        final AccessResult accessResult3 = dataService.getAccessResult("id1", "http://www.id1.com/");
        assertNotNull(accessResult3);
        assertEquals("text/html", accessResult3.getMimeType());

        dataService.delete("id1");

        final AccessResult accessResult4 = dataService.getAccessResult("id1", "http://www.id1.com/");
        assertNull(accessResult4);
    }
}
