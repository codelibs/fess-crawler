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
        Long millis = params.get("delayMillisAfterProcessing");
        if (millis != null) {
            delayMillisAfterProcessing = millis;
        }

        millis = params.get("delayMillisAtNoUrlInQueue");
        if (millis != null) {
            delayMillisAtNoUrlInQueue = millis;
        }

        millis = params.get("delayMillisBeforeProcessing");
        if (millis != null) {
            delayMillisBeforeProcessing = millis;
        }

        millis = params.get("delayMillisForWaitingNewUrl");
        if (millis != null) {
            delayMillisForWaitingNewUrl = millis;
        }

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

}
