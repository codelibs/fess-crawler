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

import org.seasar.extension.unit.S2TestCase;
import org.seasar.robot.entity.AccessResult;
import org.seasar.robot.entity.AccessResultImpl;
import org.seasar.robot.service.DataService;
import org.seasar.robot.util.AccessResultCallback;

/**
 * @author shinsuke
 *
 */
public class DataServiceImplTest extends S2TestCase {
    public DataService dataService;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_iterateUrlDiff() {
        final AccessResultImpl accessResult1a = new AccessResultImpl();
        accessResult1a.setSessionId("1");
        accessResult1a.setUrl("http://www.example.com/a");
        dataService.store(accessResult1a);

        final AccessResultImpl accessResult1b = new AccessResultImpl();
        accessResult1b.setSessionId("1");
        accessResult1b.setUrl("http://www.example.com/b");
        dataService.store(accessResult1b);

        final AccessResultImpl accessResult2a = new AccessResultImpl();
        accessResult2a.setSessionId("2");
        accessResult2a.setUrl("http://www.example.com/a");
        dataService.store(accessResult2a);

        final AccessResultImpl accessResult2c = new AccessResultImpl();
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
}
