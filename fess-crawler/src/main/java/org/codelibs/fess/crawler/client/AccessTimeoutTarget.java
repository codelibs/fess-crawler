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
package org.codelibs.fess.crawler.client;

import java.util.concurrent.atomic.AtomicBoolean;

import org.codelibs.core.timer.TimeoutTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessTimeoutTarget implements TimeoutTarget {

    private static final Logger logger = LoggerFactory
            .getLogger(AccessTimeoutTarget.class);

    private static final int MAX_LOOP_COUNT = 10;

    protected Thread runningThread;

    protected AtomicBoolean running = new AtomicBoolean();

    public AccessTimeoutTarget(final Thread thread) {
        runningThread = thread;
        running.set(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.seasar.extension.timer.TimeoutTarget#expired()
     */
    @Override
    public void expired() {
        int count = 0;
        while (running.get() && count < MAX_LOOP_COUNT) {
            if (logger.isDebugEnabled()) {
                logger.debug("Interrupt " + runningThread);
            }
            runningThread.interrupt();
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                // ignore
            }
            count++;
        }
    }

    public void stop() {
        running.set(false);
    }
}