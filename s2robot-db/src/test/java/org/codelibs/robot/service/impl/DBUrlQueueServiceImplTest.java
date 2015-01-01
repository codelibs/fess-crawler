/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.codelibs.robot.service.impl;

import org.codelibs.core.lang.SystemUtil;
import org.codelibs.robot.container.RobotContainer;
import org.codelibs.robot.container.SpringRobotContainer;
import org.codelibs.robot.db.exbhv.UrlQueueBhv;
import org.codelibs.robot.db.exentity.UrlQueue;
import org.codelibs.robot.service.UrlQueueService;
import org.codelibs.robot.util.ProfileUtil;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class DBUrlQueueServiceImplTest extends PlainTestCase {
    public UrlQueueService urlQueueService;

    private RobotContainer container;

    private UrlQueueBhv urlQueueBhv;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        ProfileUtil.setup();

        container = SpringRobotContainer.create("robotDb.xml");
        urlQueueService = container.getComponent("urlQueueService");
        urlQueueBhv = container.getComponent("urlQueueBhv");
    }

    @Override
    public void tearDown() throws Exception {
        urlQueueService.deleteAll();
        container.destroy();
        super.tearDown();
    }

    public void test_insert_update_deleteTx() {
        final UrlQueue urlQueue = new UrlQueue();
        urlQueue.setCreateTime(SystemUtil.currentTimeMillis());
        urlQueue.setDepth(1);
        urlQueue.setMethod("GET");
        urlQueue.setSessionId("sessionId");
        urlQueue.setUrl("http://www.example.com/");

        urlQueueService.insert(urlQueue);

        final UrlQueue urlQueue2 = urlQueueBhv.selectEntity(cb -> {
            cb.query().setSessionId_Equal("sessionId");
        }).get();
        assertNotNull(urlQueue2);

        urlQueueService.delete("sessionId");
        final UrlQueue urlQueue3 = urlQueueBhv.selectEntity(cb -> {
            cb.query().setSessionId_Equal("sessionId");
        }).orElse(null);
        assertNull(urlQueue3);

    }

    public void test_insert_update_delete_multiTx() {
        final UrlQueue urlQueue = new UrlQueue();
        urlQueue.setCreateTime(SystemUtil.currentTimeMillis());
        urlQueue.setDepth(1);
        urlQueue.setMethod("GET");
        urlQueue.setSessionId("id1");
        urlQueue.setUrl("http://www.id1.com/");

        urlQueueService.insert(urlQueue);

        final UrlQueue urlQueue2 = new UrlQueue();
        urlQueue2.setCreateTime(SystemUtil.currentTimeMillis());
        urlQueue2.setDepth(1);
        urlQueue2.setMethod("GET");
        urlQueue2.setSessionId("id2");
        urlQueue2.setUrl("http://www.id2.com/");

        urlQueueService.insert(urlQueue2);

        assertNotNull(urlQueueBhv.selectEntity(cb -> {
            cb.query().setSessionId_Equal("id1");
        }));

        assertNotNull(urlQueueBhv.selectEntity(cb -> {
            cb.query().setSessionId_Equal("id2");
        }));

        urlQueueService.delete("id1");

        assertNull(urlQueueBhv.selectEntity(cb -> {
            cb.query().setSessionId_Equal("id1");
        }).orElse(null));

        assertNotNull(urlQueueBhv.selectEntity(cb -> {
            cb.query().setSessionId_Equal("id2");
        }).orElse(null));

        urlQueueService.deleteAll();

        assertNull(urlQueueBhv.selectEntity(cb -> {
            cb.query().setSessionId_Equal("id2");
        }).orElse(null));
    }
}
