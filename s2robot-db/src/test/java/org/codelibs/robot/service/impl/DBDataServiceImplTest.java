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

import java.util.ArrayList;
import java.util.List;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.SystemUtil;
import org.codelibs.robot.Constants;
import org.codelibs.robot.container.RobotContainer;
import org.codelibs.robot.container.SpringRobotContainer;
import org.codelibs.robot.entity.AccessResult;
import org.codelibs.robot.service.DataService;
import org.codelibs.robot.util.AccessResultCallback;
import org.codelibs.robot.util.ProfileUtil;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class DBDataServiceImplTest extends PlainTestCase {
    public DataService dataService;

    private RobotContainer container;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        ProfileUtil.setup();

        container = SpringRobotContainer.create("robotDb.xml");
        dataService = container.getComponent("dataService");
    }

    @Override
    public void tearDown() throws Exception {
        dataService.deleteAll();
        container.destroy();
        super.tearDown();
    }

    public void test_iterateUrlDiffTx() {
        final org.codelibs.robot.db.exentity.AccessResult accessResult = new org.codelibs.robot.db.exentity.AccessResult();
        accessResult.setSessionId("1");
        accessResult.setUrl("http://www.example.com/a");
        accessResult.setCreateTime(SystemUtil.currentTimeMillis());
        accessResult.setExecutionTime(10);
        accessResult.setHttpStatusCode(200);
        accessResult.setMethod(Constants.GET_METHOD);
        accessResult.setMimeType("text/html");
        accessResult.setRuleId("html");
        accessResult.setStatus(Constants.OK_STATUS);
        accessResult.setContentLength(100L);
        accessResult.setLastModified(SystemUtil.currentTimeMillis());
        dataService.store(accessResult);

        final org.codelibs.robot.db.exentity.AccessResult accessResult1a = new org.codelibs.robot.db.exentity.AccessResult();
        BeanUtil.copyBeanToBean(accessResult, accessResult1a,
                option -> option.exclude("id"));
        dataService.store(accessResult1a);

        final org.codelibs.robot.db.exentity.AccessResult accessResult1b = new org.codelibs.robot.db.exentity.AccessResult();
        BeanUtil.copyBeanToBean(accessResult, accessResult1b,
                option -> option.exclude("id"));
        accessResult1b.setUrl("http://www.example.com/b");
        dataService.store(accessResult1b);

        final org.codelibs.robot.db.exentity.AccessResult accessResult2a = new org.codelibs.robot.db.exentity.AccessResult();
        BeanUtil.copyBeanToBean(accessResult, accessResult2a,
                option -> option.exclude("id"));
        accessResult2a.setSessionId("2");
        dataService.store(accessResult2a);

        final org.codelibs.robot.db.exentity.AccessResult accessResult2c = new org.codelibs.robot.db.exentity.AccessResult();
        BeanUtil.copyBeanToBean(accessResult, accessResult2c,
                option -> option.exclude("id"));
        accessResult2c.setSessionId("2");
        accessResult2c.setUrl("http://www.example.com/c");
        dataService.store(accessResult2c);

        dataService.iterateUrlDiff("1", "2", new AccessResultCallback() {
            public void iterate(final AccessResult accessResult) {
                assertEquals(accessResult2c, accessResult);
            }
        });

        dataService.iterateUrlDiff("2", "1", new AccessResultCallback() {
            public void iterate(final AccessResult accessResult) {
                assertEquals(accessResult1b, accessResult);
            }
        });
    }

    public void test_insert_deleteTx() {
        final org.codelibs.robot.db.exentity.AccessResult accessResult1 = new org.codelibs.robot.db.exentity.AccessResult();
        accessResult1.setContentLength(Long.valueOf(10));
        accessResult1.setCreateTime(SystemUtil.currentTimeMillis());
        accessResult1.setExecutionTime(10);
        accessResult1.setHttpStatusCode(200);
        accessResult1.setLastModified(SystemUtil.currentTimeMillis());
        accessResult1.setMethod("GET");
        accessResult1.setMimeType("text/plain");
        accessResult1.setParentUrl("http://www.parent.com/");
        accessResult1.setRuleId("htmlRule");
        accessResult1.setSessionId("id1");
        accessResult1.setStatus(200);
        accessResult1.setUrl("http://www.id1.com/");

        dataService.store(accessResult1);

        final AccessResult accessResult2 = dataService.getAccessResult("id1",
                "http://www.id1.com/");
        assertNotNull(accessResult2);

        accessResult2.setMimeType("text/html");
        dataService.update(accessResult2);

        final AccessResult accessResult3 = dataService.getAccessResult("id1",
                "http://www.id1.com/");
        assertNotNull(accessResult3);
        assertEquals("text/html", accessResult3.getMimeType());

        dataService.delete("id1");

        final AccessResult accessResult4 = dataService.getAccessResult("id1",
                "http://www.id1.com/");
        assertNull(accessResult4);
    }

    public void test_insert_delete_multiTx() {
        final org.codelibs.robot.db.exentity.AccessResult accessResult1 = new org.codelibs.robot.db.exentity.AccessResult();
        accessResult1.setContentLength(Long.valueOf(10));
        accessResult1.setCreateTime(SystemUtil.currentTimeMillis());
        accessResult1.setExecutionTime(10);
        accessResult1.setHttpStatusCode(200);
        accessResult1.setLastModified(SystemUtil.currentTimeMillis());
        accessResult1.setMethod("GET");
        accessResult1.setMimeType("text/plain");
        accessResult1.setParentUrl("http://www.parent.com/");
        accessResult1.setRuleId("htmlRule");
        accessResult1.setSessionId("id1");
        accessResult1.setStatus(200);
        accessResult1.setUrl("http://www.id1.com/");

        dataService.store(accessResult1);

        final org.codelibs.robot.db.exentity.AccessResult accessResult2 = new org.codelibs.robot.db.exentity.AccessResult();
        accessResult2.setContentLength(Long.valueOf(10));
        accessResult2.setCreateTime(SystemUtil.currentTimeMillis());
        accessResult2.setExecutionTime(10);
        accessResult2.setHttpStatusCode(200);
        accessResult2.setLastModified(SystemUtil.currentTimeMillis());
        accessResult2.setMethod("GET");
        accessResult2.setMimeType("text/plain");
        accessResult2.setParentUrl("http://www.parent.com/");
        accessResult2.setRuleId("htmlRule");
        accessResult2.setSessionId("id2");
        accessResult2.setStatus(200);
        accessResult2.setUrl("http://www.id2.com/");

        dataService.store(accessResult2);

        final AccessResult accessResult3 = dataService.getAccessResult("id1",
                "http://www.id1.com/");
        final AccessResult accessResult4 = dataService.getAccessResult("id2",
                "http://www.id2.com/");
        assertNotNull(accessResult3);
        assertNotNull(accessResult4);

        final List<AccessResult> accessResultList = new ArrayList<AccessResult>();
        accessResult3.setMimeType("text/html");
        accessResult4.setMimeType("text/html");
        accessResultList.add(accessResult3);
        accessResultList.add(accessResult4);
        dataService.update(accessResultList);

        final AccessResult accessResult5 = dataService.getAccessResult("id1",
                "http://www.id1.com/");
        final AccessResult accessResult6 = dataService.getAccessResult("id2",
                "http://www.id2.com/");
        assertNotNull(accessResult5);
        assertNotNull(accessResult6);
        assertEquals("text/html", accessResult5.getMimeType());
        assertEquals("text/html", accessResult6.getMimeType());

        dataService.delete("id1");

        assertNull(dataService.getAccessResult("id1", "http://www.id1.com/"));
        assertNotNull(dataService.getAccessResult("id2", "http://www.id2.com/"));

        dataService.store(accessResult1);
        assertNotNull(dataService.getAccessResult("id1", "http://www.id1.com/"));

        dataService.deleteAll();

        assertNull(dataService.getAccessResult("id1", "http://www.id1.com/"));
        assertNull(dataService.getAccessResult("id2", "http://www.id2.com/"));
    }

}
