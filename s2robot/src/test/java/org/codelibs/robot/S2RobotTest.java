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
package org.codelibs.robot;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.robot.client.FaultTolerantClient;
import org.codelibs.robot.client.S2RobotClientFactory;
import org.codelibs.robot.client.fs.FileSystemClient;
import org.codelibs.robot.client.http.HcHttpClient;
import org.codelibs.robot.container.SimpleComponentContainer;
import org.codelibs.robot.entity.AccessResultImpl;
import org.codelibs.robot.entity.UrlQueue;
import org.codelibs.robot.entity.UrlQueueImpl;
import org.codelibs.robot.extractor.ExtractorFactory;
import org.codelibs.robot.extractor.impl.TikaExtractor;
import org.codelibs.robot.filter.impl.UrlFilterImpl;
import org.codelibs.robot.helper.ContentLengthHelper;
import org.codelibs.robot.helper.EncodingHelper;
import org.codelibs.robot.helper.MemoryDataHelper;
import org.codelibs.robot.helper.RobotsTxtHelper;
import org.codelibs.robot.helper.SitemapsHelper;
import org.codelibs.robot.helper.UrlConvertHelper;
import org.codelibs.robot.helper.impl.LogHelperImpl;
import org.codelibs.robot.helper.impl.MimeTypeHelperImpl;
import org.codelibs.robot.interval.impl.DefaultIntervalController;
import org.codelibs.robot.processor.impl.DefaultResponseProcessor;
import org.codelibs.robot.processor.impl.SitemapsResponseProcessor;
import org.codelibs.robot.rule.impl.RegexRule;
import org.codelibs.robot.rule.impl.RuleManagerImpl;
import org.codelibs.robot.rule.impl.SitemapsRule;
import org.codelibs.robot.service.DataService;
import org.codelibs.robot.service.UrlQueueService;
import org.codelibs.robot.service.impl.DataServiceImpl;
import org.codelibs.robot.service.impl.UrlFilterServiceImpl;
import org.codelibs.robot.service.impl.UrlQueueServiceImpl;
import org.codelibs.robot.transformer.impl.FileTransformer;
import org.codelibs.robot.util.S2RobotWebServer;
import org.dbflute.utflute.core.PlainTestCase;

public class S2RobotTest extends PlainTestCase {

    public S2Robot s2Robot;

    public DataService dataService;

    public UrlQueueService urlQueueService;

    public FileTransformer fileTransformer;

    private SimpleComponentContainer container;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final Map<String, String> featureMap = newHashMap();
        featureMap.put("http://xml.org/sax/features/namespaces", "false");
        final Map<String, String> propertyMap = newHashMap();
        final Map<String, String> childUrlRuleMap = newHashMap();
        childUrlRuleMap.put("//A", "href");
        childUrlRuleMap.put("//AREA", "href");
        childUrlRuleMap.put("//FRAME", "src");
        childUrlRuleMap.put("//IFRAME", "src");
        childUrlRuleMap.put("//IMG", "src");
        childUrlRuleMap.put("//LINK", "href");
        childUrlRuleMap.put("//SCRIPT", "src");

        container = new SimpleComponentContainer();
        container
                .<HcHttpClient> prototype(
                        "internalHttpClient",
                        HcHttpClient.class,
                        client -> {
                            client.cookieSpec = CookiePolicy.BEST_MATCH;
                            client.clientConnectionManager = container
                                    .getComponent("clientConnectionManager");
                        })
                .prototype(
                        "httpClient",
                        FaultTolerantClient.class,
                        client -> {
                            client.setRobotClient(container
                                    .getComponent("internalHttpClient"));
                            client.setMaxRetryCount(5);
                            client.setRetryInterval(500);
                        })
                .prototype("fsClient", FileSystemClient.class)
                .prototype("ruleManager", RuleManagerImpl.class, manager -> {
                    manager.addRule(container.getComponent("sitemapsRule"));
                    manager.addRule(container.getComponent("fileRule"));
                })
                .prototype("accessResult", AccessResultImpl.class)
                .prototype("urlQueue", UrlQueueImpl.class)
                .prototype("robotThread", S2RobotThread.class)
                .prototype("s2Robot", S2Robot.class)
                .prototype("urlFilterService", UrlFilterServiceImpl.class)
                .prototype("urlQueueService", UrlQueueServiceImpl.class)
                .prototype("dataService", DataServiceImpl.class)
                .prototype("urlFilter", UrlFilterImpl.class)
                .singleton("urlConvertHelper", UrlConvertHelper.class)
                .singleton("intervalController",
                        DefaultIntervalController.class)
                .singleton("sitemapsHelper", SitemapsHelper.class)
                .singleton("logHelper", LogHelperImpl.class)
                .singleton("encodingHelper", EncodingHelper.class)
                .singleton("contentLengthHelper", ContentLengthHelper.class)
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class)
                .<FileTransformer> singleton("fileTransformer",
                        FileTransformer.class, transformer -> {
                            transformer.setName("fileTransformer");
                            transformer.setFeatureMap(featureMap);
                            transformer.setPropertyMap(propertyMap);
                            transformer.setChildUrlRuleMap(childUrlRuleMap);
                        })
                .singleton("dataHelper", MemoryDataHelper.class)
                .singleton("robotsTxtHelper", RobotsTxtHelper.class)
                .<S2RobotClientFactory> singleton(
                        "clientFactory",
                        S2RobotClientFactory.class,
                        factory -> {
                            factory.addClient("http:.*",
                                    container.getComponent("httpClient"));
                            factory.addClient("file:.*",
                                    container.getComponent("fsClient"));
                        })
                .singleton("tikaExtractor", TikaExtractor.class)
                .<ExtractorFactory> singleton(
                        "extractorFactory",
                        ExtractorFactory.class,
                        factory -> {
                            TikaExtractor tikaExtractor = container
                                    .getComponent("tikaExtractor");
                            factory.addExtractor("text/plain", tikaExtractor);
                            factory.addExtractor("text/html", tikaExtractor);
                        })//
                .singleton("httpClient", HcHttpClient.class)//
                .singleton("sitemapsResponseProcessor",
                        SitemapsResponseProcessor.class)//
                .<SitemapsRule> singleton(
                        "sitemapsRule",
                        SitemapsRule.class,
                        rule -> {
                            rule.setResponseProcessor(container
                                    .getComponent("sitemapsResponseProcessor"));
                            rule.setRuleId("sitemapsRule");
                            rule.addRule("url", ".*sitemap.*");
                        })//
                .<DefaultResponseProcessor> singleton(
                        "defaultResponseProcessor",
                        DefaultResponseProcessor.class,
                        processor -> {
                            processor.setTransformer(container
                                    .getComponent("fileTransformer"));
                            processor.setSuccessfulHttpCodes(new int[] { 200 });
                            processor
                                    .setNotModifiedHttpCodes(new int[] { 304 });
                        })//
                .<RegexRule> singleton(
                        "fileRule",
                        RegexRule.class,
                        rule -> {
                            rule.setRuleId("fileRule");
                            rule.setDefaultRule(true);
                            rule.setResponseProcessor(container
                                    .getComponent("defaultResponseProcessor"));
                        })//
                .<PoolingHttpClientConnectionManager> singleton(
                        "clientConnectionManager",
                        new PoolingHttpClientConnectionManager(5,
                                TimeUnit.MINUTES), manager -> {
                            manager.setMaxTotal(200);
                            manager.setDefaultMaxPerRoute(20);
                        });

        s2Robot = container.getComponent("s2Robot");
        dataService = container.getComponent("dataService");
        urlQueueService = container.getComponent("urlQueueService");
        fileTransformer = container.getComponent("fileTransformer");

    }

    public void test_execute_web() throws Exception {
        final S2RobotWebServer server = new S2RobotWebServer(7070);
        server.start();

        final String url = "http://localhost:7070/";
        try {
            final int maxCount = 50;
            final int numOfThread = 10;

            final File file = File.createTempFile("s2robot-", "");
            file.delete();
            file.mkdirs();
            file.deleteOnExit();
            fileTransformer.setPath(file.getAbsolutePath());
            s2Robot.addUrl(url);
            s2Robot.robotContext.setMaxAccessCount(maxCount);
            s2Robot.robotContext.setNumOfThread(numOfThread);
            s2Robot.urlFilter.addInclude(url + ".*");
            final String sessionId = s2Robot.execute();
            assertEquals(maxCount, dataService.getCount(sessionId));
            dataService.delete(sessionId);
        } finally {
            server.stop();
        }
    }

    public void test_execute_xmlSitemaps() throws Exception {
        final S2RobotWebServer server = new S2RobotWebServer(7070);
        server.start();

        final String url = "http://localhost:7070/";
        try {
            final int maxCount = 50;
            final int numOfThread = 10;

            final File file = File.createTempFile("s2robot-", "");
            file.delete();
            file.mkdirs();
            file.deleteOnExit();
            fileTransformer.setPath(file.getAbsolutePath());
            s2Robot.addUrl(url + "sitemaps.xml");
            s2Robot.robotContext.setMaxAccessCount(maxCount);
            s2Robot.robotContext.setNumOfThread(numOfThread);
            s2Robot.urlFilter.addInclude(url + ".*");
            final String sessionId = s2Robot.execute();
            assertEquals(maxCount, dataService.getCount(sessionId));
            dataService.delete(sessionId);
        } finally {
            server.stop();
        }
    }

    public void test_execute_textSitemaps() throws Exception {
        final S2RobotWebServer server = new S2RobotWebServer(7070);
        server.start();

        final String url = "http://localhost:7070/";
        try {
            final int maxCount = 50;
            final int numOfThread = 10;

            final File file = File.createTempFile("s2robot-", "");
            file.delete();
            file.mkdirs();
            file.deleteOnExit();
            fileTransformer.setPath(file.getAbsolutePath());
            s2Robot.addUrl(url + "sitemaps.xml");
            s2Robot.robotContext.setMaxAccessCount(maxCount);
            s2Robot.robotContext.setNumOfThread(numOfThread);
            s2Robot.urlFilter.addInclude(url + ".*");
            final String sessionId = s2Robot.execute();
            assertEquals(maxCount, dataService.getCount(sessionId));
            dataService.delete(sessionId);
        } finally {
            server.stop();
        }
    }

    public void test_execute_file_maxCount() throws Exception {
        final File targetFile = ResourceUtil.getResourceAsFile("test");
        String path = targetFile.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        final String url = "file:" + path;

        final int maxCount = 3;
        final int numOfThread = 2;

        final File file = File.createTempFile("s2robot-", "");
        file.delete();
        file.mkdirs();
        file.deleteOnExit();
        fileTransformer.setPath(file.getAbsolutePath());
        s2Robot.addUrl(url);
        s2Robot.robotContext.setMaxThreadCheckCount(3);
        s2Robot.robotContext.setMaxAccessCount(maxCount);
        s2Robot.robotContext.setNumOfThread(numOfThread);
        s2Robot.urlFilter.addInclude(url + ".*");
        final String sessionId = s2Robot.execute();
        assertEquals(maxCount, dataService.getCount(sessionId));
        dataService.delete(sessionId);
    }

    public void test_execute_file_depth() throws Exception {
        final File targetFile = ResourceUtil.getResourceAsFile("test");
        String path = targetFile.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        final String url = "file:" + path;

        final int maxCount = 3;
        final int numOfThread = 2;

        final File file = File.createTempFile("s2robot-", "");
        file.delete();
        file.mkdirs();
        file.deleteOnExit();
        fileTransformer.setPath(file.getAbsolutePath());
        s2Robot.addUrl(url);
        s2Robot.robotContext.setMaxThreadCheckCount(3);
        // s2Robot.robotContext.setMaxAccessCount(maxCount);
        s2Robot.robotContext.setNumOfThread(numOfThread);
        s2Robot.robotContext.setMaxDepth(1);
        s2Robot.urlFilter.addInclude(url + ".*");
        final String sessionId = s2Robot.execute();
        assertEquals(maxCount, dataService.getCount(sessionId));
        dataService.delete(sessionId);
    }

    public void test_execute_file_filtered() throws Exception {
        final File targetFile = ResourceUtil.getResourceAsFile("test");
        String path = targetFile.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        final String url = "file:" + path;

        final int maxCount = 3;
        final int numOfThread = 2;

        final File file = File.createTempFile("s2robot-", "");
        file.delete();
        file.mkdirs();
        file.deleteOnExit();
        fileTransformer.setPath(file.getAbsolutePath());
        s2Robot.addUrl(url);
        s2Robot.robotContext.setMaxThreadCheckCount(3);
        s2Robot.robotContext.setMaxAccessCount(maxCount);
        s2Robot.robotContext.setNumOfThread(numOfThread);
        s2Robot.urlFilter.addInclude(url + ".*");
        s2Robot.urlFilter.addExclude(url + "/dir1/.*");
        final String sessionId = s2Robot.execute();
        assertEquals(maxCount, dataService.getCount(sessionId));
        dataService.delete(sessionId);
    }

    public void test_execute_bg() throws Exception {
        final S2RobotWebServer server = new S2RobotWebServer(7070);
        server.start();

        try {
            final String url = "http://localhost:7070/";
            final int maxCount = 50;
            final int numOfThread = 10;

            final File file = File.createTempFile("s2robot-", "");
            file.delete();
            file.mkdirs();
            file.deleteOnExit();
            fileTransformer.setPath(file.getAbsolutePath());
            s2Robot.setBackground(true);
            ((UrlFilterImpl) s2Robot.urlFilter)
                    .setIncludeFilteringPattern("$1$2$3.*");
            s2Robot.addUrl(url);
            s2Robot.getRobotContext().setMaxAccessCount(maxCount);
            s2Robot.getRobotContext().setNumOfThread(numOfThread);
            final String sessionId = s2Robot.execute();
            Thread.sleep(3000);
            assertTrue(s2Robot.robotContext.running);
            s2Robot.awaitTermination();
            assertEquals(maxCount, dataService.getCount(sessionId));
            dataService.delete(sessionId);
        } finally {
            server.stop();
        }
    }

    public void test_execute_2instance() throws Exception {
        final S2RobotWebServer server1 = new S2RobotWebServer(7070);
        server1.start();
        final S2RobotWebServer server2 = new S2RobotWebServer(7071);
        server2.start();

        final String url1 = "http://localhost:7070/";
        final String url2 = "http://localhost:7071/";
        try {
            final int maxCount = 10;
            final int numOfThread = 10;

            final File file = File.createTempFile("s2robot-", "");
            file.delete();
            file.mkdirs();
            file.deleteOnExit();
            fileTransformer.setPath(file.getAbsolutePath());

            final S2Robot s2Robot1 = container.getComponent("s2Robot");
            s2Robot1.setSessionId(s2Robot1.getSessionId() + "1");
            s2Robot1.setBackground(true);
            ((UrlFilterImpl) s2Robot1.urlFilter)
                    .setIncludeFilteringPattern("$1$2$3.*");
            s2Robot1.addUrl(url1);
            s2Robot1.getRobotContext().setMaxAccessCount(maxCount);
            s2Robot1.getRobotContext().setNumOfThread(numOfThread);

            final S2Robot s2Robot2 = container.getComponent("s2Robot");
            s2Robot2.setSessionId(s2Robot2.getSessionId() + "2");
            s2Robot2.setBackground(true);
            ((UrlFilterImpl) s2Robot2.urlFilter)
                    .setIncludeFilteringPattern("$1$2$3.*");
            s2Robot2.addUrl(url2);
            s2Robot2.getRobotContext().setMaxAccessCount(maxCount);
            s2Robot2.getRobotContext().setNumOfThread(numOfThread);

            final String sessionId1 = s2Robot1.execute();
            final String sessionId2 = s2Robot2.execute();

            assertNotSame(sessionId1, sessionId2);
            assertNotSame(s2Robot1.robotContext, s2Robot2.robotContext);

            Thread.sleep(1000);

            assertTrue(s2Robot1.robotContext.running);
            assertTrue(s2Robot2.robotContext.running);

            s2Robot1.awaitTermination();
            s2Robot2.awaitTermination();

            assertEquals(maxCount, dataService.getCount(sessionId1));
            assertEquals(maxCount, dataService.getCount(sessionId2));

            UrlQueue urlQueue;
            while ((urlQueue = urlQueueService.poll(sessionId1)) != null) {
                assertTrue(urlQueue.getUrl().startsWith(url1));
            }
            while ((urlQueue = urlQueueService.poll(sessionId2)) != null) {
                assertTrue(urlQueue.getUrl().startsWith(url2));
            }

            dataService.iterate(sessionId1, accessResult -> {
                assertTrue(accessResult.getUrl().startsWith(url1));
                assertEquals(Constants.GET_METHOD, accessResult.getMethod());
            });
            dataService.iterate(sessionId2, accessResult -> {
                assertTrue(accessResult.getUrl().startsWith(url2));
                assertEquals(Constants.GET_METHOD, accessResult.getMethod());
            });

            dataService.delete(sessionId1);
            dataService.delete(sessionId2);
        } finally {
            try {
                server1.stop();
            } finally {
                server2.stop();
            }
        }
    }

    /*
     * TODO: needs to review/reconsider this feature public void
     * test_execute_web_diffcrawl() throws Exception { S2RobotWebServer server =
     * new S2RobotWebServer(7070); server.start();
     *
     * String url = "http://localhost:7070/"; try { int maxCount = 50; int
     * numOfThread = 10;
     *
     * File file = File.createTempFile("s2robot-", ""); file.delete();
     * file.mkdirs(); file.deleteOnExit();
     * fileTransformer.setPath(file.getAbsolutePath()); s2Robot.addUrl(url);
     * s2Robot.robotContext.setMaxAccessCount(maxCount);
     * s2Robot.robotContext.setNumOfThread(numOfThread);
     * s2Robot.urlFilter.addInclude(url + ".*"); String sessionId =
     * s2Robot.execute(); assertEquals(maxCount,
     * dataService.getCount(sessionId));
     *
     * String sessionId2 = sessionId + "X"; urlQueueService.delete(sessionId);
     * s2Robot = SingletonS2Container.getComponent("s2Robot");
     * s2Robot.setSessionId(sessionId2);
     * urlQueueService.generateUrlQueues(sessionId, sessionId2);
     * dataService.delete(sessionId);
     *
     * s2Robot.execute(); assertEquals(maxCount,
     * dataService.getCount(sessionId2));
     *
     * dataService.iterate(sessionId2, new AccessResultCallback() { public void
     * iterate(AccessResult accessResult) {
     * assertEquals(Constants.NOT_MODIFIED_STATUS, accessResult
     * .getStatus().intValue()); assertEquals(Constants.HEAD_METHOD,
     * accessResult .getMethod()); } }); } finally { server.stop(); } }
     */
}
