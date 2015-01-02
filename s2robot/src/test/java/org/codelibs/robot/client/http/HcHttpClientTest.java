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

import java.util.Date;

import org.codelibs.robot.S2RobotContext;
import org.codelibs.robot.container.StandardRobotContainer;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.filter.UrlFilter;
import org.codelibs.robot.filter.impl.UrlFilterImpl;
import org.codelibs.robot.helper.MemoryDataHelper;
import org.codelibs.robot.helper.RobotsTxtHelper;
import org.codelibs.robot.helper.impl.MimeTypeHelperImpl;
import org.codelibs.robot.service.impl.UrlFilterServiceImpl;
import org.codelibs.robot.util.CrawlingParameterUtil;
import org.codelibs.robot.util.S2RobotWebServer;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class HcHttpClientTest extends PlainTestCase {
    public HcHttpClient httpClient;

    public UrlFilter urlFilter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardRobotContainer container = new StandardRobotContainer()
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class)//
                .singleton("dataHelper", MemoryDataHelper.class)//
                .singleton("urlFilterService", UrlFilterServiceImpl.class)//
                .singleton("urlFilter", UrlFilterImpl.class)//
                .singleton("robotsTxtHelper", RobotsTxtHelper.class)//
                .singleton("httpClient", HcHttpClient.class);
        httpClient = container.getComponent("httpClient");
        urlFilter = container.getComponent("urlFilter");
    }

    public void test_doGet() {
        final S2RobotWebServer server = new S2RobotWebServer(7070);
        server.start();

        final String url = "http://localhost:7070/";
        try {
            final ResponseData responseData = httpClient.doGet(url);
            assertEquals(200, responseData.getHttpStatusCode());
        } finally {
            server.stop();
        }
    }

    public void test_parseLastModified() {
        final String value = "Mon, 01 Jun 2009 21:02:45 GMT";
        final Date date = httpClient.parseLastModified(value);
        assertNotNull(date);
    }

    public void test_processRobotsTxt() {
        final S2RobotWebServer server = new S2RobotWebServer(7070);
        server.start();

        final String url = "http://localhost:7070/hoge.html";
        try {
            final S2RobotContext robotContext = new S2RobotContext();
            final String sessionId = "id1";
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
        assertEquals("/index\\.html$",
                httpClient.convertRobotsTxtPathPattern("/index.html$"));
        assertEquals(".*index\\.html$",
                httpClient.convertRobotsTxtPathPattern("index.html$"));
        assertEquals("/\\..*", httpClient.convertRobotsTxtPathPattern("/."));
        assertEquals("/.*", httpClient.convertRobotsTxtPathPattern("/*"));
        assertEquals(".*\\..*", httpClient.convertRobotsTxtPathPattern("."));
        assertEquals(".*", httpClient.convertRobotsTxtPathPattern("*"));
    }

    public void test_doHead() throws Exception {
        final S2RobotWebServer server = new S2RobotWebServer(7070);
        server.start();

        final String url = "http://localhost:7070/";
        try {
            final ResponseData responseData = httpClient.doHead(url);
            Thread.sleep(100);
            assertNotNull(responseData.getLastModified());
            assertTrue(responseData.getLastModified().getTime() < new Date()
                    .getTime());
        } finally {
            server.stop();
        }
    }

    // public void test_doGet_mt() throws Exception {
    // ExecutorService executorService = Executors.newFixedThreadPool(1);
    //
    // // HttpClient Parameters
    // Map<String, Object> paramMap = new HashMap<String, Object>();
    // httpClient.setInitParameterMap(paramMap);
    //
    // DigestScheme digestScheme = new DigestScheme();
    // List<Authentication> basicAuthList = new ArrayList<Authentication>();
    // basicAuthList.add(new AuthenticationImpl(
    // new AuthScope("www.hoge.com", 80),
    // new UsernamePasswordCredentials("username", "password"),
    // digestScheme));
    // paramMap.put(
    // HcHttpClient.BASIC_AUTHENTICATIONS_PROPERTY,
    // basicAuthList.toArray(new Authentication[basicAuthList.size()]));
    //
    // List<Callable<ResponseData>> list =
    // new ArrayList<Callable<ResponseData>>();
    // for (int i = 0; i < 100; i++) {
    // list.add(new Callable<ResponseData>() {
    // public ResponseData call() throws Exception {
    // String[] urls =
    // new String[] {
    // "http://.../",
    // "http://.../test.pdf",
    // "http://.../test.doc",
    // "http://.../test.xls",
    // "http://.../test.ppt",
    // "http://.../test.txt", };
    // for (String url : urls) {
    // ResponseData responseData = httpClient.doGet(url);
    // // assertEquals(200, responseData.getHttpStatusCode());
    // if (responseData.getHttpStatusCode() != 200) {
    // return responseData;
    // }
    // }
    // return null;
    // }
    // });
    // }
    // List<Future<ResponseData>> futureList = executorService.invokeAll(list);
    // for (Future<ResponseData> future : futureList) {
    // ResponseData responseData = future.get();
    // if (responseData != null) {
    // System.out.println("status: "
    // + responseData.getHttpStatusCode()
    // + " content: "
    // + new String(InputStreamUtil.getBytes(responseData
    // .getResponseBody()), "UTF-8"));
    // } else {
    // System.out.println("OK");
    // }
    // }
    // }
}
