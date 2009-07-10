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
        String url = "http://s2robot.sandbox.seasar.org/";
        ResponseData responseData = httpClient.doGet(url);
        assertEquals(200, responseData.getHttpStatusCode());
    }

    public void test_parseLastModified() {
        String value = "Mon, 01 Jun 2009 21:02:45 GMT";
        Date date = httpClient.parseLastModified(value);
        assertNotNull(date);
    }

    public void test_processRobotsTxt() {
        String url = "http://www.seasar.org/hoge/fuga.html";
        S2RobotContext robotContext = new S2RobotContext();
        robotContext.setUrlFilter(urlFilter);
        CrawlingParameterUtil.setRobotContext(robotContext);
        httpClient.init();
        httpClient.processRobotsTxt(url);
        assertEquals(1, robotContext.getRobotTxtUrlSet().size());
        assertTrue(robotContext.getRobotTxtUrlSet().contains(
                "http://www.seasar.org/robots.txt"));
        assertFalse(urlFilter.match("http://www.seasar.org/admin/"));
        assertFalse(urlFilter.match("http://www.seasar.org/websvn/"));
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
}
