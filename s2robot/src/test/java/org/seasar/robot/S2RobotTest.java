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
import org.seasar.framework.util.ResourceUtil;
import org.seasar.robot.entity.AccessResult;
import org.seasar.robot.entity.UrlQueue;
import org.seasar.robot.filter.impl.UrlFilterImpl;
import org.seasar.robot.service.DataService;
import org.seasar.robot.service.UrlQueueService;
import org.seasar.robot.transformer.impl.FileTransformer;
import org.seasar.robot.util.AccessResultCallback;

public class S2RobotTest extends S2TestCase {

    public S2Robot s2Robot;

    public DataService dataService;

    public UrlQueueService urlQueueService;

    public FileTransformer fileTransformer;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_execute_web() throws Exception {
        String url = "http://s2robot.sandbox.seasar.org/";
        int maxCount = 50;
        int numOfThread = 10;

        File file = File.createTempFile("s2robot-", "");
        file.delete();
        file.mkdirs();
        file.deleteOnExit();
        fileTransformer.setPath(file.getAbsolutePath());
        // TODO use a local server(ex. jetty)
        s2Robot.addUrl(url);
        s2Robot.robotContext.setMaxAccessCount(maxCount);
        s2Robot.robotContext.setNumOfThread(numOfThread);
        s2Robot.urlFilter.addInclude(url + ".*");
        String sessionId = s2Robot.execute();
        assertEquals(maxCount, dataService.getCount(sessionId));
        dataService.delete(sessionId);
    }

    public void test_execute_file_maxCount() throws Exception {
        File targetFile = ResourceUtil.getResourceAsFile("test");
        String path = targetFile.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        String url = "file:" + path;

        int maxCount = 3;
        int numOfThread = 2;

        File file = File.createTempFile("s2robot-", "");
        file.delete();
        file.mkdirs();
        file.deleteOnExit();
        fileTransformer.setPath(file.getAbsolutePath());
        s2Robot.addUrl(url);
        s2Robot.robotContext.setMaxThreadCheckCount(3);
        s2Robot.robotContext.setMaxAccessCount(maxCount);
        s2Robot.robotContext.setNumOfThread(numOfThread);
        s2Robot.urlFilter.addInclude(url + ".*");
        String sessionId = s2Robot.execute();
        assertEquals(maxCount, dataService.getCount(sessionId));
        dataService.delete(sessionId);
    }

    public void test_execute_file_depth() throws Exception {
        File targetFile = ResourceUtil.getResourceAsFile("test");
        String path = targetFile.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        String url = "file:" + path;

        int maxCount = 3;
        int numOfThread = 2;

        File file = File.createTempFile("s2robot-", "");
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
        String sessionId = s2Robot.execute();
        assertEquals(maxCount, dataService.getCount(sessionId));
        dataService.delete(sessionId);
    }

    public void test_execute_file_filtered() throws Exception {
        File targetFile = ResourceUtil.getResourceAsFile("test");
        String path = targetFile.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        String url = "file:" + path;

        int maxCount = 3;
        int numOfThread = 2;

        File file = File.createTempFile("s2robot-", "");
        file.delete();
        file.mkdirs();
        file.deleteOnExit();
        fileTransformer.setPath(file.getAbsolutePath());
        s2Robot.addUrl(url);
        s2Robot.robotContext.setMaxThreadCheckCount(3);
        // s2Robot.robotContext.setMaxAccessCount(maxCount);
        s2Robot.robotContext.setNumOfThread(numOfThread);
        s2Robot.urlFilter.addInclude(url + ".*");
        s2Robot.urlFilter.addExclude(url + "/dir1/.*");
        String sessionId = s2Robot.execute();
        assertEquals(maxCount, dataService.getCount(sessionId));
        dataService.delete(sessionId);
    }

    public void test_execute_bg() throws Exception {
        String url = "http://s2robot.sandbox.seasar.org/";
        int maxCount = 50;
        int numOfThread = 10;

        File file = File.createTempFile("s2robot-", "");
        file.delete();
        file.mkdirs();
        file.deleteOnExit();
        fileTransformer.setPath(file.getAbsolutePath());
        // TODO use a local server(ex. jetty)
        s2Robot.setBackground(true);
        ((UrlFilterImpl) s2Robot.urlFilter)
                .setIncludeFilteringPattern("$1$2$3.*");
        s2Robot.addUrl(url);
        s2Robot.getRobotContext().setMaxAccessCount(maxCount);
        s2Robot.getRobotContext().setNumOfThread(numOfThread);
        String sessionId = s2Robot.execute();
        Thread.sleep(3000);
        assertTrue(s2Robot.robotContext.running);
        s2Robot.awaitTermination();
        assertEquals(maxCount, dataService.getCount(sessionId));
        dataService.delete(sessionId);
    }

    public void test_execute_2instance() throws Exception {
        final String url1 = "http://s2robot.sandbox.seasar.org/";
        final String url2 = "http://s2portlet.seasar.org/";
        int maxCount = 10;
        int numOfThread = 10;

        File file = File.createTempFile("s2robot-", "");
        file.delete();
        file.mkdirs();
        file.deleteOnExit();
        fileTransformer.setPath(file.getAbsolutePath());
        // TODO use a local server(ex. jetty)

        S2Robot s2Robot1 = (S2Robot) getComponent(S2Robot.class);
        s2Robot1.setSessionId(s2Robot1.getSessionId() + "1");
        s2Robot1.setBackground(true);
        ((UrlFilterImpl) s2Robot1.urlFilter)
                .setIncludeFilteringPattern("$1$2$3.*");
        s2Robot1.addUrl(url1);
        s2Robot1.getRobotContext().setMaxAccessCount(maxCount);
        s2Robot1.getRobotContext().setNumOfThread(numOfThread);

        S2Robot s2Robot2 = (S2Robot) getComponent(S2Robot.class);
        s2Robot2.setSessionId(s2Robot2.getSessionId() + "2");
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
    }
}
