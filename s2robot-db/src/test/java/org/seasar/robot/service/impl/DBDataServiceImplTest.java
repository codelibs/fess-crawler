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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.beans.util.Beans;
import org.seasar.robot.Constants;
import org.seasar.robot.entity.AccessResult;
import org.seasar.robot.util.AccessResultCallback;

/**
 * @author shinsuke
 *
 */
public class DBDataServiceImplTest extends S2TestCase {
    public DBDataServiceImpl dataService;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_iterateUrlDiffTx() {
        final org.seasar.robot.db.exentity.AccessResult accessResult = new org.seasar.robot.db.exentity.AccessResult();
        accessResult.setSessionId("1");
        accessResult.setUrl("http://www.example.com/a");
        accessResult.setCreateTime(new Timestamp(System.currentTimeMillis()));
        accessResult.setExecutionTime(10);
        accessResult.setHttpStatusCode(200);
        accessResult.setMethod(Constants.GET_METHOD);
        accessResult.setMimeType("text/html");
        accessResult.setRuleId("html");
        accessResult.setStatus(Constants.OK_STATUS);
        accessResult.setContentLength(100L);
        accessResult.setLastModified(new Timestamp(new Date().getTime()));
        dataService.store(accessResult);

        final org.seasar.robot.db.exentity.AccessResult accessResult1a = new org.seasar.robot.db.exentity.AccessResult();
        Beans.copy(accessResult, accessResult1a).execute();
        dataService.store(accessResult1a);

        final org.seasar.robot.db.exentity.AccessResult accessResult1b = new org.seasar.robot.db.exentity.AccessResult();
        Beans.copy(accessResult, accessResult1b).execute();
        accessResult1b.setUrl("http://www.example.com/b");
        dataService.store(accessResult1b);

        final org.seasar.robot.db.exentity.AccessResult accessResult2a = new org.seasar.robot.db.exentity.AccessResult();
        Beans.copy(accessResult, accessResult2a).execute();
        accessResult2a.setSessionId("2");
        dataService.store(accessResult2a);

        final org.seasar.robot.db.exentity.AccessResult accessResult2c = new org.seasar.robot.db.exentity.AccessResult();
        Beans.copy(accessResult, accessResult2c).execute();
        accessResult2c.setSessionId("2");
        accessResult2c.setUrl("http://www.example.com/c");
        dataService.store(accessResult2c);

        dataService.iterateUrlDiff("1", "2", new AccessResultCallback() {
            public void iterate(AccessResult accessResult) {
                assertEquals(accessResult2c, accessResult);
            }
        });

        dataService.iterateUrlDiff("2", "1", new AccessResultCallback() {
            public void iterate(AccessResult accessResult) {
                assertEquals(accessResult1b, accessResult);
            }
        });
    }

    public void test_insert_deleteTx() {
        org.seasar.robot.db.exentity.AccessResult accessResult1 = new org.seasar.robot.db.exentity.AccessResult();
        accessResult1.setContentLength(Long.valueOf(10));
        accessResult1.setCreateTime(new Timestamp(new Date().getTime()));
        accessResult1.setExecutionTime(10);
        accessResult1.setHttpStatusCode(200);
        accessResult1.setLastModified(new Timestamp(new Date().getTime()));
        accessResult1.setMethod("GET");
        accessResult1.setMimeType("text/plain");
        accessResult1.setParentUrl("http://www.parent.com/");
        accessResult1.setRuleId("htmlRule");
        accessResult1.setSessionId("id1");
        accessResult1.setStatus(200);
        accessResult1.setUrl("http://www.id1.com/");

        dataService.store(accessResult1);

        AccessResult accessResult2 = dataService.getAccessResult("id1",
                "http://www.id1.com/");
        assertNotNull(accessResult2);

        accessResult2.setMimeType("text/html");
        dataService.update(accessResult2);

        AccessResult accessResult3 = dataService.getAccessResult("id1",
                "http://www.id1.com/");
        assertNotNull(accessResult3);
        assertEquals("text/html", accessResult3.getMimeType());

        dataService.delete("id1");

        AccessResult accessResult4 = dataService.getAccessResult("id1",
                "http://www.id1.com/");
        assertNull(accessResult4);
    }

    public void test_insert_delete_multiTx() {
        org.seasar.robot.db.exentity.AccessResult accessResult1 = new org.seasar.robot.db.exentity.AccessResult();
        accessResult1.setContentLength(Long.valueOf(10));
        accessResult1.setCreateTime(new Timestamp(new Date().getTime()));
        accessResult1.setExecutionTime(10);
        accessResult1.setHttpStatusCode(200);
        accessResult1.setLastModified(new Timestamp(new Date().getTime()));
        accessResult1.setMethod("GET");
        accessResult1.setMimeType("text/plain");
        accessResult1.setParentUrl("http://www.parent.com/");
        accessResult1.setRuleId("htmlRule");
        accessResult1.setSessionId("id1");
        accessResult1.setStatus(200);
        accessResult1.setUrl("http://www.id1.com/");

        dataService.store(accessResult1);

        org.seasar.robot.db.exentity.AccessResult accessResult2 = new org.seasar.robot.db.exentity.AccessResult();
        accessResult2.setContentLength(Long.valueOf(10));
        accessResult2.setCreateTime(new Timestamp(new Date().getTime()));
        accessResult2.setExecutionTime(10);
        accessResult2.setHttpStatusCode(200);
        accessResult2.setLastModified(new Timestamp(new Date().getTime()));
        accessResult2.setMethod("GET");
        accessResult2.setMimeType("text/plain");
        accessResult2.setParentUrl("http://www.parent.com/");
        accessResult2.setRuleId("htmlRule");
        accessResult2.setSessionId("id2");
        accessResult2.setStatus(200);
        accessResult2.setUrl("http://www.id2.com/");

        dataService.store(accessResult2);

        AccessResult accessResult3 = dataService.getAccessResult("id1",
                "http://www.id1.com/");
        AccessResult accessResult4 = dataService.getAccessResult("id2",
                "http://www.id2.com/");
        assertNotNull(accessResult3);
        assertNotNull(accessResult4);

        List<AccessResult> accessResultList = new ArrayList<AccessResult>();
        accessResult3.setMimeType("text/html");
        accessResult4.setMimeType("text/html");
        accessResultList.add(accessResult3);
        accessResultList.add(accessResult4);
        dataService.update(accessResultList);

        AccessResult accessResult5 = dataService.getAccessResult("id1",
                "http://www.id1.com/");
        AccessResult accessResult6 = dataService.getAccessResult("id2",
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
