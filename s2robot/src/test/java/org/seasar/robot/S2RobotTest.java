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
import org.seasar.robot.filter.impl.UrlFilterImpl;
import org.seasar.robot.service.DataService;
import org.seasar.robot.transformer.impl.FileTransformer;

public class S2RobotTest extends S2TestCase {

    public S2Robot s2Robot;

    public DataService dataService;

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

    public void test_execute_file() throws Exception {
        File targetFile = ResourceUtil.getResourceAsFile("test");
        String url = "file://" + targetFile.getAbsolutePath();
        int maxCount = 3;
        int numOfThread = 2;

        File file = File.createTempFile("s2robot-", "");
        file.delete();
        file.mkdirs();
        file.deleteOnExit();
        fileTransformer.setPath(file.getAbsolutePath());
        // TODO use a local server(ex. jetty)
        s2Robot.addUrl(url);
        s2Robot.robotContext.setMaxThreadCheckCount(3);
        s2Robot.robotContext.setMaxAccessCount(maxCount);
        s2Robot.robotContext.setNumOfThread(numOfThread);
        s2Robot.urlFilter.addInclude(url + ".*");
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
        s2Robot.robotContext.setMaxAccessCount(maxCount);
        s2Robot.robotContext.setNumOfThread(numOfThread);
        String sessionId = s2Robot.execute();
        Thread.sleep(3000);
        assertTrue(s2Robot.robotContext.running);
        s2Robot.awaitTermination();
        assertEquals(maxCount, dataService.getCount(sessionId));
        dataService.delete(sessionId);
    }
}
