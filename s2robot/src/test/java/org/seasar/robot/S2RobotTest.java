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

    public void test_execute() throws Exception {
        File file = File.createTempFile("s2robot-", "");
        file.delete();
        file.mkdirs();
        file.deleteOnExit();
        fileTransformer.path = file.getAbsolutePath();
        // TODO use a local server(ex. jetty)
        s2Robot.addUrl("http://s2robot.sandbox.seasar.org/");
        s2Robot.robotConfig.setMaxAccessCount(50);
        s2Robot.robotConfig.setNumOfThread(10);
        s2Robot.urlFilter.addInclude("http://s2robot.sandbox.seasar.org/.*");
        String sessionId = s2Robot.execute();
        assertEquals(50, dataService.getCount(sessionId));
        dataService.delete(sessionId);
    }
}
