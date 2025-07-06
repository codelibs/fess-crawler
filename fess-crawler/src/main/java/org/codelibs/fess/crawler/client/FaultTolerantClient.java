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
package org.codelibs.fess.crawler.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.exception.MultipleCrawlingAccessException;

/**
 * A fault-tolerant wrapper for CrawlerClient that implements retry logic for failed requests.
 * This client will attempt to execute requests multiple times before giving up, with configurable
 * retry counts and intervals between attempts.
 *
 * <p>The client supports a RequestListener interface to monitor the request lifecycle and handle
 * exceptions during retries.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Configurable maximum retry attempts</li>
 *   <li>Adjustable interval between retries</li>
 *   <li>Exception tracking and aggregation</li>
 *   <li>Request lifecycle monitoring through listener</li>
 * </ul>
 *
 * <p>By default, it will:</p>
 * <ul>
 *   <li>Retry up to 5 times</li>
 *   <li>Wait 500ms between retries</li>
 * </ul>
 *
 */
public class FaultTolerantClient implements CrawlerClient {

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(FaultTolerantClient.class);

    /** The underlying crawler client */
    protected CrawlerClient client;

    /** Maximum number of retry attempts */
    protected int maxRetryCount = 5;

    /** Interval between retry attempts in milliseconds */
    protected long retryInterval = 500;

    /** Request listener for monitoring request lifecycle */
    protected RequestListener listener;

    /**
     * Constructs a new FaultTolerantClient.
     */
    public FaultTolerantClient() {
        // Default constructor
    }

    @Override
    /**
     * Sets the initialization parameters for the underlying CrawlerClient.
     *
     * @param params a map of parameter names and values to be set
     */
    public void setInitParameterMap(final Map<String, Object> params) {
        client.setInitParameterMap(params);
    }

    /**
     * Executes the request with retry logic.
     * @param request The request data.
     * @return The response data.
     */
    @Override
    public ResponseData execute(final RequestData request) {
        if (listener != null) {
            listener.onRequestStart(this, request);
        }

        List<Exception> exceptionList = null;
        try {
            int count = 0;
            while (count < maxRetryCount) {
                if (listener != null) {
                    listener.onRequest(this, request, count);
                }

                try {
                    return client.execute(request);
                } catch (final MaxLengthExceededException e) {
                    throw e;
                } catch (final Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to access to {}", request.getUrl(), e);
                    }

                    if (listener != null) {
                        listener.onException(this, request, count, e);
                    }

                    if (exceptionList == null) {
                        exceptionList = new ArrayList<>();
                    }
                    exceptionList.add(e);
                }

                ThreadUtil.sleep(retryInterval);
                count++;
            }
            final String message = "Failed to access to " + request.getUrl()
                    + exceptionList.stream().map(e -> "; " + e.getMessage()).collect(Collectors.joining());
            throw new MultipleCrawlingAccessException(message, exceptionList.toArray(new Throwable[exceptionList.size()]));
        } finally {
            if (listener != null) {
                listener.onRequestEnd(this, request, exceptionList);
            }
        }
    }

    /**
     * Closes the underlying CrawlerClient and releases any resources associated with it.
     *
     * @throws Exception if an error occurs during the close operation
     */
    @Override
    public void close() throws Exception {
        client.close();
    }

    /**
     * Returns the underlying CrawlerClient.
     * @return The CrawlerClient instance.
     */
    public CrawlerClient getCrawlerClient() {
        return client;
    }

    /**
     * Sets the underlying CrawlerClient.
     * @param client The CrawlerClient instance to set.
     */
    public void setCrawlerClient(final CrawlerClient client) {
        this.client = client;
    }

    /**
     * Returns the maximum retry count.
     * @return The maximum retry count.
     */
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    /**
     * Sets the maximum retry count.
     * @param maxRetryCount The maximum retry count.
     */
    public void setMaxRetryCount(final int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    /**
     * Returns the retry interval.
     * @return The retry interval in milliseconds.
     */
    public long getRetryInterval() {
        return retryInterval;
    }

    /**
     * Sets the retry interval.
     * @param retryInterval The retry interval in milliseconds.
     */
    public void setRetryInterval(final long retryInterval) {
        this.retryInterval = retryInterval;
    }

    /**
     * Returns the request listener.
     * @return The RequestListener instance.
     */
    public RequestListener getRequestListener() {
        return listener;
    }

    /**
     * Sets the request listener.
     * @param listener The RequestListener instance to set.
     */
    public void setRequestListener(final RequestListener listener) {
        this.listener = listener;
    }

    /**
     * Interface for listening to request lifecycle events.
     */
    public interface RequestListener {

        /**
         * Called when a request starts.
         *
         * @param client the fault-tolerant client
         * @param request the request data
         */
        void onRequestStart(FaultTolerantClient client, RequestData request);

        /**
         * Called before each request attempt.
         *
         * @param client the fault-tolerant client
         * @param request the request data
         * @param count the current retry count
         */
        void onRequest(FaultTolerantClient client, RequestData request, int count);

        /**
         * Called when a request ends.
         *
         * @param client the fault-tolerant client
         * @param request the request data
         * @param exceptionList the list of exceptions that occurred during retries
         */
        void onRequestEnd(FaultTolerantClient client, RequestData request, List<Exception> exceptionList);

        /**
         * Called when an exception occurs during a request attempt.
         *
         * @param client the fault-tolerant client
         * @param request the request data
         * @param count the current retry count
         * @param e the exception that occurred
         */
        void onException(FaultTolerantClient client, RequestData request, int count, Exception e);

    }
}
