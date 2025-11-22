/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
 * Utility class for managing crawling parameters using ThreadLocal variables.
 * This class provides methods to set and get various parameters related to the crawling process.
 *
 * <p>This class is final and cannot be instantiated.</p>
 *
 * <p>The following parameters are managed:</p>
 * <ul>
 *   <li>{@link UrlQueue} - The queue of URLs to be crawled.</li>
 *   <li>{@link CrawlerContext} - The context of the current crawling process.</li>
 *   <li>{@link UrlQueueService} - The service for managing the URL queue.</li>
 *   <li>{@link DataService} - The service for managing access results.</li>
 * </ul>
 *
 * <p>Each parameter is stored in a ThreadLocal variable to ensure thread safety.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * UrlQueue<?> urlQueue = CrawlingParameterUtil.getUrlQueue();
 * CrawlingParameterUtil.setUrlQueue(newUrlQueue);
 *
 * CrawlerContext context = CrawlingParameterUtil.getCrawlerContext();
 * CrawlingParameterUtil.setCrawlerContext(newContext);
 *
 * UrlQueueService<UrlQueue<?>> urlQueueService = CrawlingParameterUtil.getUrlQueueService();
 * CrawlingParameterUtil.setUrlQueueService(newUrlQueueService);
 *
 * DataService<AccessResult<?>> dataService = CrawlingParameterUtil.getDataService();
 * CrawlingParameterUtil.setDataService(newDataService);
 * }
 * </pre>
 *
 * <p>Note: If a parameter is set to {@code null}, the corresponding ThreadLocal variable is removed.</p>
 */
public final class CrawlingParameterUtil {
    private static final ThreadLocal<UrlQueue<?>> URL_QUEUE_THREAD_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<CrawlerContext> CRAWLER_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<UrlQueueService<UrlQueue<?>>> URL_QUEUE_SERVICE_THREAD_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<DataService<AccessResult<?>>> DATA_SERVICE_THREAD_LOCAL = new ThreadLocal<>();

    private CrawlingParameterUtil() {
    }

    /**
     * Retrieves the current thread's {@link UrlQueue} instance.
     *
     * @return the {@link UrlQueue} instance associated with the current thread, or {@code null} if none is set.
     */
    public static UrlQueue<?> getUrlQueue() {
        return URL_QUEUE_THREAD_LOCAL.get();
    }

    /**
     * Sets the URL queue for the current thread. If the provided URL queue is null,
     * the URL queue for the current thread is removed.
     *
     * @param urlQueue the URL queue to be set for the current thread, or null to remove the URL queue
     */
    public static void setUrlQueue(final UrlQueue<?> urlQueue) {
        if (urlQueue == null) {
            URL_QUEUE_THREAD_LOCAL.remove();
        } else {
            URL_QUEUE_THREAD_LOCAL.set(urlQueue);
        }
    }

    /**
     * Retrieves the current {@link CrawlerContext} associated with the current thread.
     *
     * @return the {@link CrawlerContext} for the current thread, or {@code null} if no context is set.
     */
    public static CrawlerContext getCrawlerContext() {
        return CRAWLER_CONTEXT_THREAD_LOCAL.get();
    }

    /**
     * Sets the current {@link CrawlerContext} for the current thread.
     * If the provided {@code crawlerContext} is {@code null}, the context is removed from the thread-local storage.
     * Otherwise, the provided {@code crawlerContext} is set in the thread-local storage.
     *
     * @param crawlerContext the {@link CrawlerContext} to be set for the current thread, or {@code null} to remove the context.
     */
    public static void setCrawlerContext(final CrawlerContext crawlerContext) {
        if (crawlerContext == null) {
            CRAWLER_CONTEXT_THREAD_LOCAL.remove();
        } else {
            CRAWLER_CONTEXT_THREAD_LOCAL.set(crawlerContext);
        }
    }

    /**
     * Retrieves the current thread-local instance of the UrlQueueService.
     *
     * @return the UrlQueueService instance associated with the current thread.
     */
    public static UrlQueueService<UrlQueue<?>> getUrlQueueService() {
        return URL_QUEUE_SERVICE_THREAD_LOCAL.get();
    }

    /**
     * Sets the UrlQueueService instance to the thread-local variable.
     * If the provided UrlQueueService is null, it removes the current instance from the thread-local variable.
     *
     * @param urlQueueService the UrlQueueService instance to be set, or null to remove the current instance
     */
    public static void setUrlQueueService(final UrlQueueService<UrlQueue<?>> urlQueueService) {
        if (urlQueueService == null) {
            URL_QUEUE_SERVICE_THREAD_LOCAL.remove();
        } else {
            URL_QUEUE_SERVICE_THREAD_LOCAL.set(urlQueueService);
        }
    }

    /**
     * Retrieves the current thread-local instance of the DataService for AccessResult.
     *
     * @return the DataService instance associated with the current thread, or null if none is set.
     */
    public static DataService<AccessResult<?>> getDataService() {
        return DATA_SERVICE_THREAD_LOCAL.get();
    }

    /**
     * Sets the DataService instance for the current thread.
     * If the provided DataService is null, it removes the DataService from the current thread's local storage.
     * Otherwise, it sets the provided DataService to the current thread's local storage.
     *
     * @param dataService the DataService instance to be set for the current thread, or null to remove it
     */
    public static void setDataService(final DataService<AccessResult<?>> dataService) {
        if (dataService == null) {
            DATA_SERVICE_THREAD_LOCAL.remove();
        } else {
            DATA_SERVICE_THREAD_LOCAL.set(dataService);
        }
    }
}
