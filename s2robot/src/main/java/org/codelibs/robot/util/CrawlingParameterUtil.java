/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.robot.util;

import org.codelibs.robot.S2RobotContext;
import org.codelibs.robot.entity.UrlQueue;
import org.codelibs.robot.service.DataService;
import org.codelibs.robot.service.UrlQueueService;

/**
 * @author shinsuke
 *
 */
public final class CrawlingParameterUtil {
    private static final ThreadLocal<UrlQueue> URL_QUEUE_THREAD_LOCAL = new ThreadLocal<UrlQueue>();

    private static final ThreadLocal<S2RobotContext> ROBOT_CONTEXT_THREAD_LOCAL = new ThreadLocal<S2RobotContext>();

    private static final ThreadLocal<UrlQueueService> URL_QUEUE_SERVICE_THREAD_LOCAL = new ThreadLocal<UrlQueueService>();

    private static final ThreadLocal<DataService> DATA_SERVICE_THREAD_LOCAL = new ThreadLocal<DataService>();

    private CrawlingParameterUtil() {
    }

    public static UrlQueue getUrlQueue() {
        if (URL_QUEUE_THREAD_LOCAL != null) {
            return URL_QUEUE_THREAD_LOCAL.get();
        }
        return null;
    }

    public static void setUrlQueue(final UrlQueue urlQueue) {
        if (URL_QUEUE_THREAD_LOCAL != null) {
            if (urlQueue == null) {
                URL_QUEUE_THREAD_LOCAL.remove();
            } else {
                URL_QUEUE_THREAD_LOCAL.set(urlQueue);
            }
        }
    }

    public static S2RobotContext getRobotContext() {
        if (ROBOT_CONTEXT_THREAD_LOCAL != null) {
            return ROBOT_CONTEXT_THREAD_LOCAL.get();
        }
        return null;
    }

    public static void setRobotContext(final S2RobotContext robotContext) {
        if (ROBOT_CONTEXT_THREAD_LOCAL != null) {
            if (robotContext == null) {
                ROBOT_CONTEXT_THREAD_LOCAL.remove();
            } else {
                ROBOT_CONTEXT_THREAD_LOCAL.set(robotContext);
            }
        }
    }

    public static UrlQueueService getUrlQueueService() {
        if (URL_QUEUE_SERVICE_THREAD_LOCAL != null) {
            return URL_QUEUE_SERVICE_THREAD_LOCAL.get();
        }
        return null;
    }

    public static void setUrlQueueService(final UrlQueueService urlQueueService) {
        if (URL_QUEUE_SERVICE_THREAD_LOCAL != null) {
            if (urlQueueService == null) {
                URL_QUEUE_SERVICE_THREAD_LOCAL.remove();
            } else {
                URL_QUEUE_SERVICE_THREAD_LOCAL.set(urlQueueService);
            }
        }
    }

    public static DataService getDataService() {
        if (DATA_SERVICE_THREAD_LOCAL != null) {
            return DATA_SERVICE_THREAD_LOCAL.get();
        }
        return null;
    }

    public static void setDataService(final DataService dataService) {
        if (DATA_SERVICE_THREAD_LOCAL != null) {
            if (dataService == null) {
                DATA_SERVICE_THREAD_LOCAL.remove();
            } else {
                DATA_SERVICE_THREAD_LOCAL.set(dataService);
            }
        }
    }

    // TODO others?
}
