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
package org.seasar.robot.service.impl;

import java.sql.Timestamp;
import java.util.Date;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.robot.db.cbean.UrlQueueCB;
import org.seasar.robot.db.exentity.UrlQueue;

/**
 * @author shinsuke
 *
 */
public class DBUrlQueueServiceImplTest extends S2TestCase {
    public DBUrlQueueServiceImpl urlQueueService;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_insert_update_deleteTx() {
        UrlQueue urlQueue = new UrlQueue();
        urlQueue.setCreateTime(new Timestamp(new Date().getTime()));
        urlQueue.setDepth(1);
        urlQueue.setMethod("GET");
        urlQueue.setSessionId("sessionId");
        urlQueue.setUrl("http://www.example.com/");

        urlQueueService.insert(urlQueue);

        UrlQueueCB cb = new UrlQueueCB();
        cb.query().setSessionId_Equal("sessionId");
        UrlQueue urlQueue2 = urlQueueService.urlQueueBhv.selectEntity(cb);
        assertNotNull(urlQueue2);

        urlQueueService.delete("sessionId");
        UrlQueue urlQueue3 = urlQueueService.urlQueueBhv.selectEntity(cb);
        assertNull(urlQueue3);

    }

    public void test_insert_update_delete_multiTx() {
        UrlQueue urlQueue = new UrlQueue();
        urlQueue.setCreateTime(new Timestamp(new Date().getTime()));
        urlQueue.setDepth(1);
        urlQueue.setMethod("GET");
        urlQueue.setSessionId("id1");
        urlQueue.setUrl("http://www.id1.com/");

        urlQueueService.insert(urlQueue);

        UrlQueue urlQueue2 = new UrlQueue();
        urlQueue2.setCreateTime(new Timestamp(new Date().getTime()));
        urlQueue2.setDepth(1);
        urlQueue2.setMethod("GET");
        urlQueue2.setSessionId("id2");
        urlQueue2.setUrl("http://www.id2.com/");

        urlQueueService.insert(urlQueue2);

        UrlQueueCB cb = new UrlQueueCB();
        cb.query().setSessionId_Equal("id1");
        assertNotNull(urlQueueService.urlQueueBhv.selectEntity(cb));

        cb = new UrlQueueCB();
        cb.query().setSessionId_Equal("id2");
        assertNotNull(urlQueueService.urlQueueBhv.selectEntity(cb));

        urlQueueService.delete("id1");

        cb = new UrlQueueCB();
        cb.query().setSessionId_Equal("id1");
        assertNull(urlQueueService.urlQueueBhv.selectEntity(cb));

        cb = new UrlQueueCB();
        cb.query().setSessionId_Equal("id2");
        assertNotNull(urlQueueService.urlQueueBhv.selectEntity(cb));

        urlQueueService.deleteAll();

        cb = new UrlQueueCB();
        cb.query().setSessionId_Equal("id2");
        assertNull(urlQueueService.urlQueueBhv.selectEntity(cb));
    }
}
