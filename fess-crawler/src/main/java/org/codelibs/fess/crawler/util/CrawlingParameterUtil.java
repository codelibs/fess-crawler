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
package org.codelibs.fess.crawler.util;

import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.service.UrlQueueService;

/**
 * @author shinsuke
 *
 */
public final class CrawlingParameterUtil {
    private static final ThreadLocal<UrlQueue<?>> URL_QUEUE_THREAD_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<CrawlerContext> ROBOT_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<UrlQueueService<UrlQueue<?>>> URL_QUEUE_SERVICE_THREAD_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<DataService<AccessResult<?>>> DATA_SERVICE_THREAD_LOCAL = new ThreadLocal<>();

    private CrawlingParameterUtil() {
    }

    public static UrlQueue<?> getUrlQueue() {
        return URL_QUEUE_THREAD_LOCAL.get();
    }

    public static void setUrlQueue(final UrlQueue<?> urlQueue) {
        if (urlQueue == null) {
            URL_QUEUE_THREAD_LOCAL.remove();
        } else {
            URL_QUEUE_THREAD_LOCAL.set(urlQueue);
        }
    }

    public static CrawlerContext getCrawlerContext() {
        return ROBOT_CONTEXT_THREAD_LOCAL.get();
    }

    public static void setCrawlerContext(final CrawlerContext crawlerContext) {
        if (crawlerContext == null) {
            ROBOT_CONTEXT_THREAD_LOCAL.remove();
        } else {
            ROBOT_CONTEXT_THREAD_LOCAL.set(crawlerContext);
        }
    }

    public static UrlQueueService<UrlQueue<?>> getUrlQueueService() {
        return URL_QUEUE_SERVICE_THREAD_LOCAL.get();
    }

    public static void setUrlQueueService(final UrlQueueService<UrlQueue<?>> urlQueueService) {
        if (urlQueueService == null) {
            URL_QUEUE_SERVICE_THREAD_LOCAL.remove();
        } else {
            URL_QUEUE_SERVICE_THREAD_LOCAL.set(urlQueueService);
        }
    }

    public static DataService<AccessResult<?>> getDataService() {
        return DATA_SERVICE_THREAD_LOCAL.get();
    }

    public static void setDataService(final DataService<AccessResult<?>> dataService) {
        if (dataService == null) {
            DATA_SERVICE_THREAD_LOCAL.remove();
        } else {
            DATA_SERVICE_THREAD_LOCAL.set(dataService);
        }
    }
}
