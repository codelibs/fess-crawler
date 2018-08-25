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

import static org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.newConfigs;

import java.io.File;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.annotation.Resource;

import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.codelibs.fess.crawler.Crawler;
import org.codelibs.fess.crawler.client.EsClient;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.filter.impl.UrlFilterImpl;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.service.UrlFilterService;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.codelibs.fess.crawler.transformer.impl.FileTransformer;
import org.codelibs.fess.crawler.util.CrawlerWebServer;
import org.dbflute.utflute.lastadi.LastaDiTestCase;

public class CrawlerTest extends LastaDiTestCase {

    @Resource
    private Crawler crawler;

    @Resource
    private DataService dataService;

    @Resource
    private UrlQueueService urlQueueService;

    @Resource
    private UrlFilterService urlFilterService;

    @Resource
    private FileTransformer fileTransformer;

    @Resource
    private EsClient esClient;

    private ElasticsearchClusterRunner runner;

    @Override
    protected String prepareConfigFile() {
        return "app.xml";
    }

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Override
    public void setUp() throws Exception {
        // create runner instance
        runner = new ElasticsearchClusterRunner();
        // create ES nodes
        final String clusterName = UUID.randomUUID().toString();
        runner.onBuild((number, settingsBuilder) -> {
            settingsBuilder.put("http.cors.enabled", true);
            settingsBuilder.put("http.cors.allow-origin", "*");
            settingsBuilder.putList("discovery.zen.ping.unicast.hosts", "localhost:9301-9399");
        }).build(newConfigs().clusterName(clusterName).numOfNode(1));

        // wait for yellow status
        runner.ensureYellow();

        System.setProperty(EsClient.HTTP_ADDRESS, "localhost:" + runner.node().settings().get("http.port", "9201"));

        super.setUp();

        // logging
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        rootLogger.addHandler(handler);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        // close runner
        runner.close();
        // delete all files
        runner.clean();
    }

    public void test_executeTx() throws Exception {
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
            crawler.getCrawlerContext().setMaxAccessCount(maxCount);
            crawler.getCrawlerContext().setNumOfThread(numOfThread);
            crawler.urlFilter.addInclude(url + ".*");
            final String sessionId = crawler.execute();
            assertEquals(maxCount, dataService.getCount(sessionId));
            dataService.delete(sessionId);
        } finally {
            server.stop();
        }
    }

    public void test_execute_2instanceTx() throws Exception {
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

            final Crawler crawler1 = getComponent(Crawler.class);
            crawler1.setBackground(true);
            ((UrlFilterImpl) crawler1.urlFilter).setIncludeFilteringPattern("$1$2$3.*");
            crawler1.addUrl(url1);
            crawler1.getCrawlerContext().setMaxAccessCount(maxCount);
            crawler1.getCrawlerContext().setNumOfThread(numOfThread);

            Thread.sleep(100);

            final Crawler crawler2 = getComponent(Crawler.class);
            crawler2.setBackground(true);
            ((UrlFilterImpl) crawler2.urlFilter).setIncludeFilteringPattern("$1$2$3.*");
            crawler2.addUrl(url2);
            crawler2.getCrawlerContext().setMaxAccessCount(maxCount);
            crawler2.getCrawlerContext().setNumOfThread(numOfThread);

            final String sessionId1 = crawler1.execute();
            final String sessionId2 = crawler2.execute();

            assertNotSame(sessionId1, sessionId2);
            assertNotSame(crawler1.crawlerContext, crawler2.crawlerContext);

            for (int i = 0; i < 10; i++) {
                if (crawler1.crawlerContext.getStatus() == CrawlerStatus.RUNNING) {
                    break;
                }
                Thread.sleep(500);
            }
            assertEquals(CrawlerStatus.RUNNING, crawler1.crawlerContext.getStatus());
            for (int i = 0; i < 10; i++) {
                if (crawler2.crawlerContext.getStatus() == CrawlerStatus.RUNNING) {
                    break;
                }
                Thread.sleep(500);
            }
            assertEquals(CrawlerStatus.RUNNING, crawler2.crawlerContext.getStatus());

            crawler1.awaitTermination();
            crawler2.awaitTermination();

            assertEquals(maxCount, dataService.getCount(sessionId1));
            assertEquals(maxCount, dataService.getCount(sessionId2));

            UrlQueue urlQueue;
            while ((urlQueue = urlQueueService.poll(sessionId1)) != null) {
                assertTrue(urlQueue.getUrl() + "=>" + url1, urlQueue.getUrl().startsWith(url1));
            }
            while ((urlQueue = urlQueueService.poll(sessionId2)) != null) {
                assertTrue(urlQueue.getUrl() + "=>" + url2, urlQueue.getUrl().startsWith(url2));
            }

            dataService.iterate(sessionId1, accessResult -> assertTrue(accessResult.getUrl().startsWith(url1)));
            dataService.iterate(sessionId2, accessResult -> assertTrue(accessResult.getUrl().startsWith(url2)));

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
