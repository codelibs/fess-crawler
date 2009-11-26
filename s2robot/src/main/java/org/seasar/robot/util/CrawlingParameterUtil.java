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
import org.seasar.robot.service.DataService;
import org.seasar.robot.service.UrlQueueService;

/**
 * @author shinsuke
 *
 */
public class CrawlingParameterUtil {
    private static final ThreadLocal<UrlQueue> URL_QUEUE_THREAD_LOCAL = new ThreadLocal<UrlQueue>();

    private static final ThreadLocal<S2RobotContext> ROBOT_CONTEXT_THREAD_LOCAL = new ThreadLocal<S2RobotContext>();

    private static final ThreadLocal<UrlQueueService> URL_QUEUE_SERVICE_THREAD_LOCAL = new ThreadLocal<UrlQueueService>();

    private static final ThreadLocal<DataService> DATA_SERVICE_THREAD_LOCAL = new ThreadLocal<DataService>();

    public static UrlQueue getUrlQueue() {
        if (URL_QUEUE_THREAD_LOCAL != null) {
            return URL_QUEUE_THREAD_LOCAL.get();
        }
        return null;
    }

    public static void setUrlQueue(UrlQueue urlQueue) {
        if (URL_QUEUE_THREAD_LOCAL != null) {
            if (urlQueue != null) {
                URL_QUEUE_THREAD_LOCAL.set(urlQueue);
            } else {
                URL_QUEUE_THREAD_LOCAL.remove();
            }
        }
    }

    public static S2RobotContext getRobotContext() {
        if (ROBOT_CONTEXT_THREAD_LOCAL != null) {
            return ROBOT_CONTEXT_THREAD_LOCAL.get();
        }
        return null;
    }

    public static void setRobotContext(S2RobotContext robotContext) {
        if (ROBOT_CONTEXT_THREAD_LOCAL != null) {
            if (robotContext != null) {
                ROBOT_CONTEXT_THREAD_LOCAL.set(robotContext);
            } else {
                ROBOT_CONTEXT_THREAD_LOCAL.remove();
            }
        }
    }

    public static UrlQueueService getUrlQueueService() {
        if (URL_QUEUE_SERVICE_THREAD_LOCAL != null) {
            return URL_QUEUE_SERVICE_THREAD_LOCAL.get();
        }
        return null;
    }

    public static void setUrlQueueService(UrlQueueService urlQueueService) {
        if (URL_QUEUE_SERVICE_THREAD_LOCAL != null) {
            if (urlQueueService != null) {
                URL_QUEUE_SERVICE_THREAD_LOCAL.set(urlQueueService);
            } else {
                URL_QUEUE_SERVICE_THREAD_LOCAL.remove();
            }
        }
    }

    public static DataService getDataService() {
        if (DATA_SERVICE_THREAD_LOCAL != null) {
            return DATA_SERVICE_THREAD_LOCAL.get();
        }
        return null;
    }

    public static void setDataService(DataService dataService) {
        if (DATA_SERVICE_THREAD_LOCAL != null) {
            if (dataService != null) {
                DATA_SERVICE_THREAD_LOCAL.set(dataService);
            } else {
                DATA_SERVICE_THREAD_LOCAL.remove();
            }
        }
    }

    // TODO others?
}
