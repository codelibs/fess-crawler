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
package org.codelibs.fess.crawler;

import java.io.File;

import org.codelibs.fess.crawler.Crawler;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.filter.impl.UrlFilterImpl;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.service.UrlFilterService;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.codelibs.fess.crawler.transformer.impl.FileTransformer;
import org.codelibs.fess.crawler.util.AccessResultCallback;
import org.codelibs.fess.crawler.util.CrawlerWebServer;
import org.dbflute.utflute.lastadi.LastaDiTestCase;

public class CrawlerTest extends LastaDiTestCase {

    public Crawler crawler;

    public DataService dataService;

    public UrlQueueService urlQueueService;

    public UrlFilterService urlFilterService;

    public FileTransformer fileTransformer;

    @Override
    protected String prepareConfigFile() {
        return "app.xml";
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

            final Crawler s2Robot1 = (Crawler) getComponent(Crawler.class);
            s2Robot1.setBackground(true);
            ((UrlFilterImpl) s2Robot1.urlFilter).setIncludeFilteringPattern("$1$2$3.*");
            s2Robot1.addUrl(url1);
            s2Robot1.getCrawlerContext().setMaxAccessCount(maxCount);
            s2Robot1.getCrawlerContext().setNumOfThread(numOfThread);

            Thread.sleep(100);

            final Crawler s2Robot2 = (Crawler) getComponent(Crawler.class);
            s2Robot2.setBackground(true);
            ((UrlFilterImpl) s2Robot2.urlFilter).setIncludeFilteringPattern("$1$2$3.*");
            s2Robot2.addUrl(url2);
            s2Robot2.getCrawlerContext().setMaxAccessCount(maxCount);
            s2Robot2.getCrawlerContext().setNumOfThread(numOfThread);

            final String sessionId1 = s2Robot1.execute();
            final String sessionId2 = s2Robot2.execute();

            assertNotSame(sessionId1, sessionId2);
            assertNotSame(s2Robot1.crawlerContext, s2Robot2.crawlerContext);

            for (int i = 0; i < 10; i++) {
                if (s2Robot1.crawlerContext.running) {
                    break;
                }
                Thread.sleep(500);
            }
            assertTrue(s2Robot1.crawlerContext.running);
            for (int i = 0; i < 10; i++) {
                if (s2Robot2.crawlerContext.running) {
                    break;
                }
                Thread.sleep(500);
            }
            assertTrue(s2Robot2.crawlerContext.running);

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

            dataService.iterate(sessionId1, new AccessResultCallback() {
                public void iterate(final AccessResult accessResult) {
                    assertTrue(accessResult.getUrl().startsWith(url1));
                }
            });
            dataService.iterate(sessionId2, new AccessResultCallback() {
                public void iterate(final AccessResult accessResult) {
                    assertTrue(accessResult.getUrl().startsWith(url2));
                }
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
