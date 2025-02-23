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

import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.interval.IntervalController;

/**
 * An abstract base class for implementing {@link IntervalController}.
 * Provides a common structure for handling delays at different stages of the crawling process.
 * It encapsulates the delay logic and exception handling, allowing subclasses to focus on
 * defining the specific delay behavior for each stage.
 *
 * <p>
 * This class defines the contract for delaying the crawling process at various points, such as:
 * </p>
 * <ul>
 *   <li>Before processing a URL ({@link #delayBeforeProcessing()})</li>
 *   <li>After processing a URL ({@link #delayAfterProcessing()})</li>
 *   <li>When there are no URLs in the queue ({@link #delayAtNoUrlInQueue()})</li>
 *   <li>While waiting for new URLs to be added to the queue ({@link #delayForWaitingNewUrl()})</li>
 * </ul>
 *
 * <p>
 * Subclasses are responsible for implementing the abstract methods to define the actual delay
 * mechanism for each of these stages.
 * </p>
 *
 * <p>
 * The class also provides a mechanism to ignore exceptions that may occur during the delay process.
 * If {@link #ignoreException} is set to true, any exceptions thrown during the delay will be caught
 * and ignored. Otherwise, they will be re-thrown as {@link CrawlerSystemException}.
 * </p>
 *
 */
public abstract class AbstractIntervalController implements IntervalController {

    /**
     * Indicates whether exceptions during the delay process should be ignored.
     * If set to true, exceptions will be caught and ignored. If set to false,
     * exceptions will be re-thrown as {@link CrawlerSystemException}.
     * Default value is true.
     */
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
                // No operation
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
