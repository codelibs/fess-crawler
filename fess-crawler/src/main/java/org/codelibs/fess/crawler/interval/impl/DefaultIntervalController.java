/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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

public class DefaultIntervalController extends AbstractIntervalController {

    protected long delayMillisAfterProcessing = 0L;

    protected long delayMillisAtNoUrlInQueue = 500L;

    protected long delayMillisBeforeProcessing = 0L;

    protected long delayMillisForWaitingNewUrl = 1000L;

    public DefaultIntervalController() {
    }

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

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.interval.impl.AbstractIntervalController#
     * delayAfterProcessing()
     */
    @Override
    protected void delayAfterProcessing() {
        if (delayMillisAfterProcessing > 0) {
            ThreadUtil.sleep(delayMillisAfterProcessing);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.interval.impl.AbstractIntervalController#delayAtNoUrlInQueue
     * ()
     */
    @Override
    protected void delayAtNoUrlInQueue() {
        if (delayMillisAtNoUrlInQueue > 0) {
            ThreadUtil.sleep(delayMillisAtNoUrlInQueue);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.interval.impl.AbstractIntervalController#
     * delayBeforeProcessing()
     */
    @Override
    protected void delayBeforeProcessing() {
        if (delayMillisBeforeProcessing > 0) {
            ThreadUtil.sleep(delayMillisBeforeProcessing);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.interval.impl.AbstractIntervalController#
     * delayForWaitingNewUrl()
     */
    @Override
    protected void delayForWaitingNewUrl() {
        if (delayMillisForWaitingNewUrl > 0) {
            ThreadUtil.sleep(delayMillisForWaitingNewUrl);
        }
    }

}
