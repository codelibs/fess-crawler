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
package org.codelibs.fess.crawler.interval.impl;

import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.interval.IntervalController;

/**
 * @author shinsuke
 *
 */
public abstract class AbstractIntervalController implements IntervalController {

    protected boolean ignoreException = true;

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.interval.IntervalController#delay(int)
     */
    @Override
    public void delay(final int type) {
        try {
            switch (type) {
                case PRE_PROCESSING:
                    delayBeforeProcessing();
                    break;
                case POST_PROCESSING:
                    delayAfterProcessing();
                    break;
                case NO_URL_IN_QUEUE:
                    delayAtNoUrlInQueue();
                    break;
                case WAIT_NEW_URL:
                    delayForWaitingNewUrl();
                    break;
                default:
                    // NOP
                    break;
            }
        } catch (final CrawlerSystemException e) {
            if (!ignoreException) {
                throw e;
            }
        } catch (final Exception e) {
            if (!ignoreException) {
                throw new CrawlerSystemException("Could not stop a process.", e);
            }
        }
    }

    protected abstract void delayBeforeProcessing();

    protected abstract void delayAfterProcessing();

    protected abstract void delayAtNoUrlInQueue();

    protected abstract void delayForWaitingNewUrl();

    public boolean isIgnoreException() {
        return ignoreException;
    }

    public void setIgnoreException(final boolean ignoreException) {
        this.ignoreException = ignoreException;
    }
}
