/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import org.codelibs.fess.crawler.db.exentity.UrlQueue;
import org.codelibs.fess.crawler.service.impl.DBUrlQueueServiceImpl;
import org.dbflute.utflute.lastadi.LastaDiTestCase;

/**
 * @author shinsuke
 * 
 */
public class DBUrlQueueServiceImplTest extends LastaDiTestCase {
    public DBUrlQueueServiceImpl urlQueueService;

    @Override
    protected String prepareConfigFile() {
        return "app.xml";
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        urlQueueService.deleteAll();
    }

    public void test_insert_update_deleteTx() {
        final UrlQueue urlQueue = new UrlQueue();
        urlQueue.setCreateTime(System.currentTimeMillis());
        urlQueue.setDepth(1);
        urlQueue.setMethod("GET");
        urlQueue.setSessionId("sessionId");
        urlQueue.setUrl("http://www.example.com/");

        urlQueueService.insert(urlQueue);

        final UrlQueue urlQueue2 = urlQueueService.urlQueueBhv.selectEntity(cb->{
            cb.query().setSessionId_Equal("sessionId");
        }).get();
        assertNotNull(urlQueue2);

        urlQueueService.delete("sessionId");
        final UrlQueue urlQueue3 = urlQueueService.urlQueueBhv.selectEntity( cb -> {
            cb.query().setSessionId_Equal("sessionId");
        }).orElse(null);
        assertNull(urlQueue3);

    }

    public void test_insert_update_delete_multiTx() {
        final UrlQueue urlQueue = new UrlQueue();
        urlQueue.setCreateTime(System.currentTimeMillis());
        urlQueue.setDepth(1);
        urlQueue.setMethod("GET");
        urlQueue.setSessionId("id1");
        urlQueue.setUrl("http://www.id1.com/");

        urlQueueService.insert(urlQueue);

        final UrlQueue urlQueue2 = new UrlQueue();
        urlQueue2.setCreateTime(System.currentTimeMillis());
        urlQueue2.setDepth(1);
        urlQueue2.setMethod("GET");
        urlQueue2.setSessionId("id2");
        urlQueue2.setUrl("http://www.id2.com/");

        urlQueueService.insert(urlQueue2);

        assertNotNull(urlQueueService.urlQueueBhv.selectEntity(cb->{
            cb.query().setSessionId_Equal("id1");
        }).get());

        assertNotNull(urlQueueService.urlQueueBhv.selectEntity(cb->{
            cb.query().setSessionId_Equal("id2");
        }).get());

        urlQueueService.delete("id1");

        assertNull(urlQueueService.urlQueueBhv.selectEntity(cb->{
            cb.query().setSessionId_Equal("id1");
        }).orElse(null));

        assertNotNull(urlQueueService.urlQueueBhv.selectEntity(cb->{
            cb.query().setSessionId_Equal("id2");
        }).get());

        urlQueueService.deleteAll();

        assertNull(urlQueueService.urlQueueBhv.selectEntity(cb->{
            cb.query().setSessionId_Equal("id2");
        }).orElse(null));
    }
}
