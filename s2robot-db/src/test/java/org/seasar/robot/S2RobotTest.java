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
package org.seasar.robot;

import java.io.File;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.robot.entity.AccessResult;
import org.seasar.robot.entity.UrlQueue;
import org.seasar.robot.filter.impl.UrlFilterImpl;
import org.seasar.robot.service.DataService;
import org.seasar.robot.service.UrlQueueService;
import org.seasar.robot.transformer.impl.FileTransformer;
import org.seasar.robot.util.AccessResultCallback;
import org.seasar.robot.util.S2RobotWebServer;

public class S2RobotTest extends S2TestCase {

    public S2Robot s2Robot;

    public DataService dataService;

    public UrlQueueService urlQueueService;

    public FileTransformer fileTransformer;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_executeTx() throws Exception {
        S2RobotWebServer server = new S2RobotWebServer(7070);
        server.start();

        String url = "http://localhost:7070/";
        try {
            int maxCount = 50;
            int numOfThread = 10;

            File file = File.createTempFile("s2robot-", "");
            file.delete();
            file.mkdirs();
            file.deleteOnExit();
            fileTransformer.setPath(file.getAbsolutePath());
            s2Robot.addUrl(url);
            s2Robot.getRobotContext().setMaxAccessCount(maxCount);
            s2Robot.getRobotContext().setNumOfThread(numOfThread);
            s2Robot.urlFilter.addInclude(url + ".*");
            String sessionId = s2Robot.execute();
            assertEquals(maxCount, dataService.getCount(sessionId));
            dataService.delete(sessionId);
        } finally {
            server.stop();
        }
    }

    public void test_execute_2instanceTx() throws Exception {
        S2RobotWebServer server1 = new S2RobotWebServer(7070);
        server1.start();
        S2RobotWebServer server2 = new S2RobotWebServer(7071);
        server2.start();

        final String url1 = "http://localhost:7070/";
        final String url2 = "http://localhost:7071/";
        try {
            int maxCount = 10;
            int numOfThread = 10;

            File file = File.createTempFile("s2robot-", "");
            file.delete();
            file.mkdirs();
            file.deleteOnExit();
            fileTransformer.setPath(file.getAbsolutePath());

            S2Robot s2Robot1 = (S2Robot) getComponent(S2Robot.class);
            s2Robot1.setBackground(true);
            ((UrlFilterImpl) s2Robot1.urlFilter)
                    .setIncludeFilteringPattern("$1$2$3.*");
            s2Robot1.addUrl(url1);
            s2Robot1.getRobotContext().setMaxAccessCount(maxCount);
            s2Robot1.getRobotContext().setNumOfThread(numOfThread);

            Thread.sleep(100);

            S2Robot s2Robot2 = (S2Robot) getComponent(S2Robot.class);
            s2Robot2.setBackground(true);
            ((UrlFilterImpl) s2Robot2.urlFilter)
                    .setIncludeFilteringPattern("$1$2$3.*");
            s2Robot2.addUrl(url2);
            s2Robot2.getRobotContext().setMaxAccessCount(maxCount);
            s2Robot2.getRobotContext().setNumOfThread(numOfThread);

            String sessionId1 = s2Robot1.execute();
            String sessionId2 = s2Robot2.execute();

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

            dataService.iterate(sessionId1, new AccessResultCallback() {
                public void iterate(AccessResult accessResult) {
                    assertTrue(accessResult.getUrl().startsWith(url1));
                }
            });
            dataService.iterate(sessionId2, new AccessResultCallback() {
                public void iterate(AccessResult accessResult) {
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

    public void test_execute_web_diffcrawl() throws Exception {
        S2RobotWebServer server = new S2RobotWebServer(7070);
        server.start();

        String url = "http://localhost:7070/";
        try {
            int maxCount = 50;
            int numOfThread = 10;

            File file = File.createTempFile("s2robot-", "");
            file.delete();
            file.mkdirs();
            file.deleteOnExit();
            fileTransformer.setPath(file.getAbsolutePath());
            s2Robot.addUrl(url);
            s2Robot.robotContext.setMaxAccessCount(maxCount);
            s2Robot.robotContext.setNumOfThread(numOfThread);
            s2Robot.urlFilter.addInclude(url + ".*");
            String sessionId = s2Robot.execute();
            assertEquals(maxCount, dataService.getCount(sessionId));

            String sessionId2 = sessionId + "X";
            urlQueueService.delete(sessionId);
            s2Robot = SingletonS2Container.getComponent("s2Robot");
            s2Robot.setSessionId(sessionId2);
            urlQueueService.generateUrlQueues(sessionId, sessionId2);
            dataService.delete(sessionId);

            s2Robot.execute();
            assertEquals(maxCount, dataService.getCount(sessionId2));

            dataService.iterate(sessionId2, new AccessResultCallback() {
                public void iterate(AccessResult accessResult) {
                    assertEquals(Constants.NOT_UPDATED_STATUS, accessResult
                            .getStatus().intValue());
                }
            });
        } finally {
            server.stop();
        }
    }
}
