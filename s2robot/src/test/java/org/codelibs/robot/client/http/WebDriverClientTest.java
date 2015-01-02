/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.robot.client.http;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.core.lang.SystemUtil;
import org.codelibs.robot.Constants;
import org.codelibs.robot.builder.RequestDataBuilder;
import org.codelibs.robot.client.http.action.AOnClickAction;
import org.codelibs.robot.client.http.action.FormAction;
import org.codelibs.robot.client.http.webdriver.S2WebDriver;
import org.codelibs.robot.container.StandardRobotContainer;
import org.codelibs.robot.entity.RequestData;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.helper.impl.MimeTypeHelperImpl;
import org.codelibs.robot.pool.S2PooledObjectFactory;
import org.codelibs.robot.util.S2RobotWebServer;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class WebDriverClientTest extends PlainTestCase {
    public WebDriverClient webDriverClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        S2PooledObjectFactory<S2WebDriver> pooledObjectFactory = new S2PooledObjectFactory<>();
        pooledObjectFactory.setComponentName("webDriver");
        pooledObjectFactory.setOnDestroyListener(p -> {
            final S2WebDriver driver = p.getObject();
            driver.quit();
        });

        final StandardRobotContainer container = new StandardRobotContainer();
        container
                .prototype("webDriver", S2WebDriver.class)
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class)
                .singleton("pooledObjectFactory", pooledObjectFactory)
                .singleton("webDriverPool",
                        new GenericObjectPool<>(pooledObjectFactory), null,
                        pool -> {
                            pool.close();
                        })
                .<AOnClickAction> singleton("aOnClickAction",
                        AOnClickAction.class)
                .<FormAction> singleton("formAction", FormAction.class)
                .<WebDriverClient> singleton(
                        "webDriverClient",
                        WebDriverClient.class,
                        client -> {
                            AOnClickAction aOnClick = container
                                    .getComponent("aOnClickAction");
                            aOnClick.setName("aOnClick");
                            aOnClick.setCssQuery("a");
                            client.addUrlAction(aOnClick);
                            FormAction formAction = container
                                    .getComponent("formAction");
                            formAction.setName("form");
                            formAction.setCssQuery("form");
                            client.addUrlAction(formAction);
                        });
        webDriverClient = container.getComponent("webDriverClient");
    }

    public void test_doGet() {
        File docRootDir = new File(ResourceUtil.getBuildDir("ajax/index.html"),
                "ajax");
        final S2RobotWebServer server = new S2RobotWebServer(7070, docRootDir);

        final String url = "http://localhost:7070/";
        try {
            server.start();
            final ResponseData responseData = webDriverClient
                    .execute(RequestDataBuilder.newRequestData().get().url(url)
                            .build());
            assertEquals(200, responseData.getHttpStatusCode());
            assertTrue(new String(InputStreamUtil.getBytes(responseData
                    .getResponseBody()), Constants.UTF_8_CHARSET)
                    .contains("Ajax Test"));
            Set<RequestData> childUrlSet = responseData.getChildUrlSet();
            assertEquals(6, childUrlSet.size());
            Iterator<RequestData> requestDataIter = childUrlSet.iterator();
            ResponseData responseData1 = webDriverClient
                    .execute(requestDataIter.next());
            assertEquals(Constants.GET_METHOD, responseData1.getMethod());
            assertEquals("http://localhost:7070/#menu-1-1.html",
                    responseData1.getUrl());
            assertTrue(new String(InputStreamUtil.getBytes(responseData1
                    .getResponseBody()), Constants.UTF_8_CHARSET)
                    .contains("MENU 11"));
            ResponseData responseData2 = webDriverClient
                    .execute(requestDataIter.next());
            assertEquals(Constants.GET_METHOD, responseData2.getMethod());
            assertEquals("http://localhost:7070/#menu-1-2.html",
                    responseData2.getUrl());
            assertTrue(new String(InputStreamUtil.getBytes(responseData2
                    .getResponseBody()), Constants.UTF_8_CHARSET)
                    .contains("MENU 12"));
            ResponseData responseData3 = webDriverClient
                    .execute(requestDataIter.next());
            assertEquals(Constants.GET_METHOD, responseData3.getMethod());
            assertEquals("http://localhost:7070/#menu-2-1.html",
                    responseData3.getUrl());
            assertTrue(new String(InputStreamUtil.getBytes(responseData3
                    .getResponseBody()), Constants.UTF_8_CHARSET)
                    .contains("MENU 21"));
            ResponseData responseData4 = webDriverClient
                    .execute(requestDataIter.next());
            assertEquals(Constants.GET_METHOD, responseData4.getMethod());
            assertEquals("http://localhost:7070/#menu-2-2.html",
                    responseData4.getUrl());
            assertTrue(new String(InputStreamUtil.getBytes(responseData4
                    .getResponseBody()), Constants.UTF_8_CHARSET)
                    .contains("MENU 22"));
            ResponseData responseData5 = webDriverClient
                    .execute(requestDataIter.next());
            assertEquals(Constants.GET_METHOD, responseData5.getMethod());
            assertEquals("http://localhost:7070/#", responseData5.getUrl());
            assertTrue(new String(InputStreamUtil.getBytes(responseData5
                    .getResponseBody()), Constants.UTF_8_CHARSET)
                    .contains("Ajax Test"));
            ResponseData responseData6 = webDriverClient
                    .execute(requestDataIter.next());
            assertEquals(Constants.POST_METHOD, responseData6.getMethod());
            assertEquals("http://localhost:7070/form.html",
                    responseData6.getUrl());

        } finally {
            server.stop();
        }
    }

    public void test_doHead() throws Exception {
        File docRootDir = new File(ResourceUtil.getBuildDir("ajax/index.html"),
                "ajax");
        final S2RobotWebServer server = new S2RobotWebServer(7070, docRootDir);

        final String url = "http://localhost:7070/";
        try {
            server.start();
            final ResponseData responseData = webDriverClient
                    .execute(RequestDataBuilder.newRequestData().head()
                            .url(url).build());
            Thread.sleep(100);
            assertNotNull(responseData.getLastModified());
            assertTrue(responseData.getLastModified().getTime() < SystemUtil
                    .currentTimeMillis());
        } finally {
            server.stop();
        }
    }
}
