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
package org.codelibs.robot.client;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.robot.client.fs.FileSystemClient;
import org.codelibs.robot.client.http.HcHttpClient;
import org.codelibs.robot.client.smb.SmbClient;
import org.codelibs.robot.container.StandardRobotContainer;
import org.codelibs.robot.entity.RequestData;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class S2RobotClientFactoryTest extends PlainTestCase {
    public S2RobotClientFactory clientFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardRobotContainer container = new StandardRobotContainer()
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class)//
                .singleton("httpClient", FaultTolerantClient.class)//
                .singleton("fsClient", FileSystemClient.class)//
                .singleton("smbClient", SmbClient.class)//
                .singleton("clientFactory", S2RobotClientFactory.class);
        clientFactory = container.getComponent("clientFactory");
        FaultTolerantClient httpClient = container.getComponent("httpClient");
        httpClient.setRobotClient(new HcHttpClient());
        clientFactory.addClient("http:.*", httpClient);
        clientFactory.addClient("https:.*", httpClient);
        clientFactory.addClient("file:.*", container.getComponent("fsClient"));
        clientFactory.addClient("smb:.*", container.getComponent("smbClient"));
    }

    public void test_getClient() {
        String url;
        S2RobotClient client;

        url = "http://hoge.com/";
        client = clientFactory.getClient(url);
        assertNotNull(client);
        assertTrue(client instanceof FaultTolerantClient);
        assertTrue(((FaultTolerantClient) client).getRobotClient() instanceof HcHttpClient);

        url = "https://hoge.com/";
        client = clientFactory.getClient(url);
        assertNotNull(client);
        assertTrue(client instanceof FaultTolerantClient);
        assertTrue(((FaultTolerantClient) client).getRobotClient() instanceof HcHttpClient);

        url = "file:/home/hoge";
        client = clientFactory.getClient(url);
        assertNotNull(client);
        assertTrue(client instanceof FileSystemClient);

    }

    public void test_setInitParameterMap() {
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("hoge", "test");

        final StringBuilder buf = new StringBuilder();
        clientFactory.addClient("test:.*", new S2RobotClient() {
            @Override
            public ResponseData execute(final RequestData request) {
                return null;
            }

            @Override
            public void setInitParameterMap(final Map<String, Object> params) {
                buf.append("value=").append(params.get("hoge"));
            }

        });

        clientFactory.setInitParameterMap(paramMap);

        assertEquals("value=test", buf.toString());
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
