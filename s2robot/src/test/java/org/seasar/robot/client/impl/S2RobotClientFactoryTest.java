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
package org.seasar.robot.client.impl;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.robot.client.S2RobotClient;
import org.seasar.robot.client.S2RobotClientFactory;

/**
 * @author shinsuke
 *
 */
public class S2RobotClientFactoryTest extends S2TestCase {
    public S2RobotClientFactory clientFactory;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_getClient() {
        String url;
        S2RobotClient client;

        url = "http://hoge.com/";
        client = clientFactory.getClient(url);
        assertNotNull(client);
        assertTrue(client instanceof CommonsHttpClient);

        url = "https://hoge.com/";
        client = clientFactory.getClient(url);
        assertNotNull(client);
        assertTrue(client instanceof CommonsHttpClient);

    }

    public void test_getClient_null() {
        String url;
        S2RobotClient client;

        url = null;
        client = clientFactory.getClient(url);
        assertNull(client);

        url = "";
        client = clientFactory.getClient(url);
        assertNull(client);

        url = " ";
        client = clientFactory.getClient(url);
        assertNull(client);
    }
}