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
package org.seasar.robot.util;

import org.seasar.robot.S2RobotContext;
import org.seasar.robot.entity.UrlQueue;

/**
 * @author shinsuke
 *
 */
public class CrawlingParameterUtil {
    private static ThreadLocal<UrlQueue> urlQueueThreadLocal = new ThreadLocal<UrlQueue>();

    private static ThreadLocal<S2RobotContext> robotContextThreadLocal = new ThreadLocal<S2RobotContext>();

    public static UrlQueue getUrlQueue() {
        return urlQueueThreadLocal.get();
    }

    public static void setUrlQueue(UrlQueue urlQueue) {
        if (urlQueue != null) {
            urlQueueThreadLocal.set(urlQueue);
        } else {
            urlQueueThreadLocal.remove();
        }
    }

    public static S2RobotContext getRobotContext() {
        return robotContextThreadLocal.get();
    }

    public static void setRobotContext(S2RobotContext robotContext) {
        if (robotContext != null) {
            robotContextThreadLocal.set(robotContext);
        } else {
            robotContextThreadLocal.remove();
        }
    }

    // TODO others?
}
