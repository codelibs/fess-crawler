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
package org.seasar.robot.client.http;

import java.util.Date;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.robot.S2RobotContext;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.filter.UrlFilter;
import org.seasar.robot.util.CrawlingParameterUtil;
import org.seasar.robot.util.S2RobotWebServer;

/**
 * @author shinsuke
 *
 */
public class CommonsHttpClientTest extends S2TestCase {
    public CommonsHttpClient httpClient;

    public UrlFilter urlFilter;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_doGet() {
        S2RobotWebServer server = new S2RobotWebServer(7070);
        server.start();

        String url = "http://localhost:7070/";
        try {
            ResponseData responseData = httpClient.doGet(url);
            assertEquals(200, responseData.getHttpStatusCode());
        } finally {
            server.stop();
        }
    }

    public void test_parseLastModified() {
        String value = "Mon, 01 Jun 2009 21:02:45 GMT";
        Date date = httpClient.parseLastModified(value);
        assertNotNull(date);
    }

    public void test_processRobotsTxt() {
        S2RobotWebServer server = new S2RobotWebServer(7070);
        server.start();

        String url = "http://localhost:7070/hoge.html";
        try {
            S2RobotContext robotContext = new S2RobotContext();
            String sessionId = "id1";
            urlFilter.init(sessionId);
            robotContext.setUrlFilter(urlFilter);
            CrawlingParameterUtil.setRobotContext(robotContext);
            httpClient.init();
            httpClient.processRobotsTxt(url);
            assertEquals(1, robotContext.getRobotTxtUrlSet().size());
            assertTrue(robotContext.getRobotTxtUrlSet().contains(
                    "http://localhost:7070/robots.txt"));
            assertFalse(urlFilter.match("http://localhost:7070/admin/"));
            assertFalse(urlFilter.match("http://localhost:7070/websvn/"));
        } finally {
            server.stop();
        }
    }

    public void test_convertRobotsTxtPathPattern() {
        assertEquals("/.*", httpClient.convertRobotsTxtPathPattern("/"));
        assertEquals("/index\\.html$", httpClient
                .convertRobotsTxtPathPattern("/index.html$"));
        assertEquals(".*index\\.html$", httpClient
                .convertRobotsTxtPathPattern("index.html$"));
        assertEquals("/\\..*", httpClient.convertRobotsTxtPathPattern("/."));
        assertEquals("/.*", httpClient.convertRobotsTxtPathPattern("/*"));
        assertEquals(".*\\..*", httpClient.convertRobotsTxtPathPattern("."));
        assertEquals(".*", httpClient.convertRobotsTxtPathPattern("*"));
    }

    public void test_doHead() throws Exception {
        S2RobotWebServer server = new S2RobotWebServer(7070);
        server.start();

        String url = "http://localhost:7070/";
        try {
            ResponseData responseData = httpClient.doHead(url);
            Thread.sleep(100);
            assertNotNull(responseData.getLastModified());
            assertTrue(responseData.getLastModified().getTime() < new Date()
                    .getTime());
        } finally {
            server.stop();
        }
    }
}
