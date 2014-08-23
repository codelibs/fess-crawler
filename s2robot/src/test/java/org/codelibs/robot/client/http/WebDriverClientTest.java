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
package org.codelibs.robot.client.http;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.codelibs.robot.Constants;
import org.codelibs.robot.builder.RequestDataBuilder;
import org.codelibs.robot.entity.RequestData;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.util.S2RobotWebServer;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.ResourceUtil;

/**
 * @author shinsuke
 * 
 */
public class WebDriverClientTest extends S2TestCase {
    public WebDriverClient webDriverClient;

    @Override
    protected String getRootDicon() throws Throwable {
        return "org/codelibs/robot/client/http/webdriver.dicon";
    }

    public void test_doGet() {
        File docRootDir =
            new File(ResourceUtil.getBuildDir("ajax/index.html"), "ajax");
        final S2RobotWebServer server = new S2RobotWebServer(7070, docRootDir);

        final String url = "http://localhost:7070/";
        try {
            server.start();
            final ResponseData responseData =
                webDriverClient.execute(RequestDataBuilder
                    .newRequestData()
                    .get()
                    .url(url)
                    .build());
            assertEquals(200, responseData.getHttpStatusCode());
            assertTrue(new String(InputStreamUtil.getBytes(responseData
                .getResponseBody()), Constants.UTF_8_CHARSET)
                .contains("Ajax Test"));
            Set<RequestData> childUrlSet = responseData.getChildUrlSet();
            assertEquals(6, childUrlSet.size());
            Iterator<RequestData> requestDataIter = childUrlSet.iterator();
            ResponseData responseData1 =
                webDriverClient.execute(requestDataIter.next());
            assertEquals(Constants.GET_METHOD, responseData1.getMethod());
            assertEquals(
                "http://localhost:7070/#menu-1-1.html",
                responseData1.getUrl());
            assertTrue(new String(InputStreamUtil.getBytes(responseData1
                .getResponseBody()), Constants.UTF_8_CHARSET)
                .contains("MENU 11"));
            ResponseData responseData2 =
                webDriverClient.execute(requestDataIter.next());
            assertEquals(Constants.GET_METHOD, responseData2.getMethod());
            assertEquals(
                "http://localhost:7070/#menu-1-2.html",
                responseData2.getUrl());
            assertTrue(new String(InputStreamUtil.getBytes(responseData2
                .getResponseBody()), Constants.UTF_8_CHARSET)
                .contains("MENU 12"));
            ResponseData responseData3 =
                webDriverClient.execute(requestDataIter.next());
            assertEquals(Constants.GET_METHOD, responseData3.getMethod());
            assertEquals(
                "http://localhost:7070/#menu-2-1.html",
                responseData3.getUrl());
            assertTrue(new String(InputStreamUtil.getBytes(responseData3
                .getResponseBody()), Constants.UTF_8_CHARSET)
                .contains("MENU 21"));
            ResponseData responseData4 =
                webDriverClient.execute(requestDataIter.next());
            assertEquals(Constants.GET_METHOD, responseData4.getMethod());
            assertEquals(
                "http://localhost:7070/#menu-2-2.html",
                responseData4.getUrl());
            assertTrue(new String(InputStreamUtil.getBytes(responseData4
                .getResponseBody()), Constants.UTF_8_CHARSET)
                .contains("MENU 22"));
            ResponseData responseData5 =
                webDriverClient.execute(requestDataIter.next());
            assertEquals(Constants.GET_METHOD, responseData5.getMethod());
            assertEquals("http://localhost:7070/#", responseData5.getUrl());
            assertTrue(new String(InputStreamUtil.getBytes(responseData5
                .getResponseBody()), Constants.UTF_8_CHARSET)
                .contains("Ajax Test"));
            ResponseData responseData6 =
                webDriverClient.execute(requestDataIter.next());
            assertEquals(Constants.POST_METHOD, responseData6.getMethod());
            assertEquals(
                "http://localhost:7070/form.html",
                responseData6.getUrl());

        } finally {
            server.stop();
        }
    }

    public void test_doHead() throws Exception {
        File docRootDir =
            new File(ResourceUtil.getBuildDir("ajax/index.html"), "ajax");
        final S2RobotWebServer server = new S2RobotWebServer(7070, docRootDir);

        final String url = "http://localhost:7070/";
        try {
            server.start();
            final ResponseData responseData =
                webDriverClient.execute(RequestDataBuilder
                    .newRequestData()
                    .head()
                    .url(url)
                    .build());
            Thread.sleep(100);
            assertNotNull(responseData.getLastModified());
            assertTrue(responseData.getLastModified().getTime() < System
                .currentTimeMillis());
        } finally {
            server.stop();
        }
    }
}
