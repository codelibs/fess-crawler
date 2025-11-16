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
package org.codelibs.fess.crawler.interval.impl;

import java.util.Map;

import org.codelibs.core.lang.ThreadUtil;

/**
 * Default implementation of the IntervalController.
 * This class provides a default way to manage delays between crawler operations.
 * It allows setting delays before processing, after processing, when no URLs are in the queue,
 * and when waiting for new URLs.
 * The delays are configurable via constructor parameters.
 *
 */
public class DefaultIntervalController extends AbstractIntervalController {

    /** Delay in milliseconds after processing a URL */
    protected long delayMillisAfterProcessing = 0L;

    /** Delay in milliseconds when no URL is in the queue */
    protected long delayMillisAtNoUrlInQueue = 500L;

    /** Delay in milliseconds before processing a URL */
    protected long delayMillisBeforeProcessing = 0L;

    /** Delay in milliseconds for waiting for new URLs */
    protected long delayMillisForWaitingNewUrl = 1000L;

    /**
     * Default constructor with default delay values.
     */
    public DefaultIntervalController() {
    }

    /**
     * Constructor with configurable delay parameters.
     *
     * @param params map containing delay parameters
     */
    public DefaultIntervalController(final Map<String, Long> params) {
        delayMillisAfterProcessing = getParamValue(params, "delayMillisAfterProcessing", delayMillisAfterProcessing);
        delayMillisAtNoUrlInQueue = getParamValue(params, "delayMillisAtNoUrlInQueue", delayMillisAtNoUrlInQueue);
        delayMillisBeforeProcessing = getParamValue(params, "delayMillisBeforeProcessing", delayMillisBeforeProcessing);
        delayMillisForWaitingNewUrl = getParamValue(params, "delayMillisForWaitingNewUrl", delayMillisForWaitingNewUrl);
    }

    /**
     * Helper method to get parameter value from map with default fallback.
     *
     * @param params parameter map
     * @param key parameter key
     * @param defaultValue default value if parameter not found
     * @return parameter value or default value
     */
    private long getParamValue(final Map<String, Long> params, final String key, final long defaultValue) {
        final Long value = params.get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Delays after processing a URL.
     */
    @Override
    protected void delayAfterProcessing() {
        if (delayMillisAfterProcessing > 0) {
            ThreadUtil.sleep(delayMillisAfterProcessing);
        }
    }

    /**
     * Delays when no URL is in the queue.
     */
    @Override
    protected void delayAtNoUrlInQueue() {
        if (delayMillisAtNoUrlInQueue > 0) {
            ThreadUtil.sleep(delayMillisAtNoUrlInQueue);
        }
    }

    /**
     * Delays before processing a URL.
     */
    @Override
    protected void delayBeforeProcessing() {
        if (delayMillisBeforeProcessing > 0) {
            ThreadUtil.sleep(delayMillisBeforeProcessing);
        }
    }

    /**
     * Delays for waiting for new URLs.
     */
    @Override
    protected void delayForWaitingNewUrl() {
        if (delayMillisForWaitingNewUrl > 0) {
            ThreadUtil.sleep(delayMillisForWaitingNewUrl);
        }
    }

    /**
     * Gets the delay in milliseconds after processing a URL.
     * @return delay in milliseconds
     */
    public long getDelayMillisAfterProcessing() {
        return delayMillisAfterProcessing;
    }

    /**
     * Sets the delay in milliseconds after processing a URL.
     * @param delayMillisAfterProcessing delay in milliseconds
     */
    public void setDelayMillisAfterProcessing(final long delayMillisAfterProcessing) {
        this.delayMillisAfterProcessing = delayMillisAfterProcessing;
    }

    /**
     * Gets the delay in milliseconds when no URL is in the queue.
     * @return delay in milliseconds
     */
    public long getDelayMillisAtNoUrlInQueue() {
        return delayMillisAtNoUrlInQueue;
    }

    /**
     * Sets the delay in milliseconds when no URL is in the queue.
     * @param delayMillisAtNoUrlInQueue delay in milliseconds
     */
    public void setDelayMillisAtNoUrlInQueue(final long delayMillisAtNoUrlInQueue) {
        this.delayMillisAtNoUrlInQueue = delayMillisAtNoUrlInQueue;
    }

    /**
     * Gets the delay in milliseconds before processing a URL.
     * @return delay in milliseconds
     */
    public long getDelayMillisBeforeProcessing() {
        return delayMillisBeforeProcessing;
    }

    /**
     * Sets the delay in milliseconds before processing a URL.
     * @param delayMillisBeforeProcessing delay in milliseconds
     */
    public void setDelayMillisBeforeProcessing(final long delayMillisBeforeProcessing) {
        this.delayMillisBeforeProcessing = delayMillisBeforeProcessing;
    }

    /**
     * Gets the delay in milliseconds for waiting for new URLs.
     * @return delay in milliseconds
     */
    public long getDelayMillisForWaitingNewUrl() {
        return delayMillisForWaitingNewUrl;
    }

    /**
     * Sets the delay in milliseconds for waiting for new URLs.
     * @param delayMillisForWaitingNewUrl delay in milliseconds
     */
    public void setDelayMillisForWaitingNewUrl(final long delayMillisForWaitingNewUrl) {
        this.delayMillisForWaitingNewUrl = delayMillisForWaitingNewUrl;
    }

}
