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

import org.codelibs.core.io.ResourceUtil;
import org.codelibs.robot.container.RobotContainer;
import org.codelibs.robot.container.SpringRobotContainer;
import org.codelibs.robot.entity.UrlQueue;
import org.codelibs.robot.filter.impl.UrlFilterImpl;
import org.codelibs.robot.service.DataService;
import org.codelibs.robot.service.UrlQueueService;
import org.codelibs.robot.transformer.impl.FileTransformer;
import org.codelibs.robot.util.S2RobotWebServer;
import org.dbflute.utflute.core.PlainTestCase;

public class S2RobotTest extends PlainTestCase {

    public S2Robot s2Robot;

    public DataService dataService;

    public UrlQueueService urlQueueService;

    public FileTransformer fileTransformer;

    private RobotContainer container;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        container = SpringRobotContainer.create("robot.xml");

        s2Robot = container.getComponent("s2Robot");
        dataService = container.getComponent("dataService");
        urlQueueService = container.getComponent("urlQueueService");
        fileTransformer = container.getComponent("fileTransformer");

    }

    @Override
    public void tearDown() throws Exception {
        container.destroy();
        super.tearDown();
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

}
