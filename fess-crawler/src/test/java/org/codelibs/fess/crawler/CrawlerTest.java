/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.client.FaultTolerantClient;
import org.codelibs.fess.crawler.client.fs.FileSystemClient;
import org.codelibs.fess.crawler.client.http.HcHttpClient;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.AccessResultImpl;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.entity.UrlQueueImpl;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.extractor.impl.TikaExtractor;
import org.codelibs.fess.crawler.filter.impl.UrlFilterImpl;
import org.codelibs.fess.crawler.helper.ContentLengthHelper;
import org.codelibs.fess.crawler.helper.EncodingHelper;
import org.codelibs.fess.crawler.helper.MemoryDataHelper;
import org.codelibs.fess.crawler.helper.RobotsTxtHelper;
import org.codelibs.fess.crawler.helper.SitemapsHelper;
import org.codelibs.fess.crawler.helper.UrlConvertHelper;
import org.codelibs.fess.crawler.helper.impl.LogHelperImpl;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.codelibs.fess.crawler.interval.impl.DefaultIntervalController;
import org.codelibs.fess.crawler.processor.impl.DefaultResponseProcessor;
import org.codelibs.fess.crawler.processor.impl.SitemapsResponseProcessor;
import org.codelibs.fess.crawler.rule.impl.RegexRule;
import org.codelibs.fess.crawler.rule.impl.RuleManagerImpl;
import org.codelibs.fess.crawler.rule.impl.SitemapsRule;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.codelibs.fess.crawler.service.impl.DataServiceImpl;
import org.codelibs.fess.crawler.service.impl.UrlFilterServiceImpl;
import org.codelibs.fess.crawler.service.impl.UrlQueueServiceImpl;
import org.codelibs.fess.crawler.transformer.impl.FileTransformer;
import org.codelibs.fess.crawler.util.CrawlerWebServer;
import org.dbflute.utflute.core.PlainTestCase;

public class CrawlerTest extends PlainTestCase {

    public Crawler crawler;

    public DataService dataService;

    public UrlQueueService urlQueueService;

    public FileTransformer fileTransformer;

    private StandardCrawlerContainer container;

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

        container = new StandardCrawlerContainer();
        container
                .<HcHttpClient> prototype(
                        "internalHttpClient",
                        HcHttpClient.class,
                        client -> {
                            client.setCookieSpec(CookieSpecs.BEST_MATCH);
                            client.setClientConnectionManager(container
                                    .getComponent("clientConnectionManager"));
                        })
                .prototype(
                        "httpClient",
                        FaultTolerantClient.class,
                        client -> {
                            client.setCrawlerClient(container
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
                .prototype("crawlerThread", CrawlerThread.class)
                .prototype("crawler", Crawler.class)
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
                .<CrawlerClientFactory> singleton(
                        "clientFactory",
                        CrawlerClientFactory.class,
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

        crawler = container.getComponent("crawler");
        dataService = container.getComponent("dataService");
        urlQueueService = container.getComponent("urlQueueService");
        fileTransformer = container.getComponent("fileTransformer");

    }

    public void test_execute_web() throws Exception {
        final CrawlerWebServer server = new CrawlerWebServer(7070);
        server.start();

        final String url = "http://localhost:7070/";
        try {
            final int maxCount = 50;
            final int numOfThread = 10;

            final File file = File.createTempFile("crawler-", "");
            file.delete();
            file.mkdirs();
            file.deleteOnExit();
            fileTransformer.setPath(file.getAbsolutePath());
            crawler.addUrl(url);
            crawler.crawlerContext.setMaxAccessCount(maxCount);
            crawler.crawlerContext.setNumOfThread(numOfThread);
            crawler.urlFilter.addInclude(url + ".*");
            final String sessionId = crawler.execute();
            assertEquals(maxCount, dataService.getCount(sessionId));
            dataService.delete(sessionId);
        } finally {
            server.stop();
        }
    }

    public void test_execute_xmlSitemaps() throws Exception {
        final CrawlerWebServer server = new CrawlerWebServer(7070);
        server.start();

        final String url = "http://localhost:7070/";
        try {
            final int maxCount = 50;
            final int numOfThread = 10;

            final File file = File.createTempFile("crawler-", "");
            file.delete();
            file.mkdirs();
            file.deleteOnExit();
            fileTransformer.setPath(file.getAbsolutePath());
            crawler.addUrl(url + "sitemaps.xml");
            crawler.crawlerContext.setMaxAccessCount(maxCount);
            crawler.crawlerContext.setNumOfThread(numOfThread);
            crawler.urlFilter.addInclude(url + ".*");
            final String sessionId = crawler.execute();
            assertEquals(maxCount, dataService.getCount(sessionId));
            dataService.delete(sessionId);
        } finally {
            server.stop();
        }
    }

    public void test_execute_textSitemaps() throws Exception {
        final CrawlerWebServer server = new CrawlerWebServer(7070);
        server.start();

        final String url = "http://localhost:7070/";
        try {
            final int maxCount = 50;
            final int numOfThread = 10;

            final File file = File.createTempFile("crawler-", "");
            file.delete();
            file.mkdirs();
            file.deleteOnExit();
            fileTransformer.setPath(file.getAbsolutePath());
            crawler.addUrl(url + "sitemaps.xml");
            crawler.crawlerContext.setMaxAccessCount(maxCount);
            crawler.crawlerContext.setNumOfThread(numOfThread);
            crawler.urlFilter.addInclude(url + ".*");
            final String sessionId = crawler.execute();
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

        final File file = File.createTempFile("crawler-", "");
        file.delete();
        file.mkdirs();
        file.deleteOnExit();
        fileTransformer.setPath(file.getAbsolutePath());
        crawler.addUrl(url);
        crawler.crawlerContext.setMaxThreadCheckCount(3);
        crawler.crawlerContext.setMaxAccessCount(maxCount);
        crawler.crawlerContext.setNumOfThread(numOfThread);
        crawler.urlFilter.addInclude(url + ".*");
        final String sessionId = crawler.execute();
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

        final File file = File.createTempFile("crawler-", "");
        file.delete();
        file.mkdirs();
        file.deleteOnExit();
        fileTransformer.setPath(file.getAbsolutePath());
        crawler.addUrl(url);
        crawler.crawlerContext.setMaxThreadCheckCount(3);
        // crawler.crawlerContext.setMaxAccessCount(maxCount);
        crawler.crawlerContext.setNumOfThread(numOfThread);
        crawler.crawlerContext.setMaxDepth(1);
        crawler.urlFilter.addInclude(url + ".*");
        final String sessionId = crawler.execute();
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

        final File file = File.createTempFile("crawler-", "");
        file.delete();
        file.mkdirs();
        file.deleteOnExit();
        fileTransformer.setPath(file.getAbsolutePath());
        crawler.addUrl(url);
        crawler.crawlerContext.setMaxThreadCheckCount(3);
        crawler.crawlerContext.setMaxAccessCount(maxCount);
        crawler.crawlerContext.setNumOfThread(numOfThread);
        crawler.urlFilter.addInclude(url + ".*");
        crawler.urlFilter.addExclude(url + "/dir1/.*");
        final String sessionId = crawler.execute();
        assertEquals(maxCount, dataService.getCount(sessionId));
        dataService.delete(sessionId);
    }

    public void test_execute_bg() throws Exception {
        final CrawlerWebServer server = new CrawlerWebServer(7070);
        server.start();

        try {
            final String url = "http://localhost:7070/";
            final int maxCount = 50;
            final int numOfThread = 10;

            final File file = File.createTempFile("crawler-", "");
            file.delete();
            file.mkdirs();
            file.deleteOnExit();
            fileTransformer.setPath(file.getAbsolutePath());
            crawler.setBackground(true);
            ((UrlFilterImpl) crawler.urlFilter)
                    .setIncludeFilteringPattern("$1$2$3.*");
            crawler.addUrl(url);
            crawler.getCrawlerContext().setMaxAccessCount(maxCount);
            crawler.getCrawlerContext().setNumOfThread(numOfThread);
            final String sessionId = crawler.execute();
            Thread.sleep(3000);
            assertEquals(CrawlerStatus.RUNNING, crawler.crawlerContext.getStatus());
            crawler.awaitTermination();
            assertEquals(maxCount, dataService.getCount(sessionId));
            dataService.delete(sessionId);
        } finally {
            server.stop();
        }
    }

    public void test_execute_2instance() throws Exception {
        final CrawlerWebServer server1 = new CrawlerWebServer(7070);
        server1.start();
        final CrawlerWebServer server2 = new CrawlerWebServer(7071);
        server2.start();

        final String url1 = "http://localhost:7070/";
        final String url2 = "http://localhost:7071/";
        try {
            final int maxCount = 10;
            final int numOfThread = 10;

            final File file = File.createTempFile("crawler-", "");
            file.delete();
            file.mkdirs();
            file.deleteOnExit();
            fileTransformer.setPath(file.getAbsolutePath());

            final Crawler crawler1 = container.getComponent("crawler");
            crawler1.setSessionId(crawler1.getSessionId() + "1");
            crawler1.setBackground(true);
            ((UrlFilterImpl) crawler1.urlFilter)
                    .setIncludeFilteringPattern("$1$2$3.*");
            crawler1.addUrl(url1);
            crawler1.getCrawlerContext().setMaxAccessCount(maxCount);
            crawler1.getCrawlerContext().setNumOfThread(numOfThread);

            final Crawler crawler2 = container.getComponent("crawler");
            crawler2.setSessionId(crawler2.getSessionId() + "2");
            crawler2.setBackground(true);
            ((UrlFilterImpl) crawler2.urlFilter)
                    .setIncludeFilteringPattern("$1$2$3.*");
            crawler2.addUrl(url2);
            crawler2.getCrawlerContext().setMaxAccessCount(maxCount);
            crawler2.getCrawlerContext().setNumOfThread(numOfThread);

            final String sessionId1 = crawler1.execute();
            final String sessionId2 = crawler2.execute();

            assertNotSame(sessionId1, sessionId2);
            assertNotSame(crawler1.crawlerContext, crawler2.crawlerContext);

            Thread.sleep(1000);

            assertEquals(CrawlerStatus.RUNNING, crawler1.crawlerContext.getStatus());
            assertEquals(CrawlerStatus.RUNNING, crawler2.crawlerContext.getStatus());

            crawler1.awaitTermination();
            crawler2.awaitTermination();

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

}
