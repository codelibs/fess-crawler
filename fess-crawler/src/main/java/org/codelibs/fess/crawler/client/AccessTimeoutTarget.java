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

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.core.timer.TimeoutTarget;

/**
 * AccessTimeoutTarget is a class that implements the TimeoutTarget interface.
 * It is used to interrupt a running thread when a timeout occurs.
 * The class provides methods to interrupt the thread and stop the timeout target.
 *
 * <p>
 * The class uses a logger to log debug messages.
 * It also uses an AtomicBoolean to track whether the thread is running.
 * </p>
 *
 * <p>
 * The expired method is called when a timeout occurs.
 * It interrupts the running thread up to a maximum number of times.
 * The stop method is called to stop the timeout target.
 * </p>
 */
public class AccessTimeoutTarget implements TimeoutTarget {

    private static final Logger logger = LogManager.getLogger(AccessTimeoutTarget.class);

    private static final int MAX_LOOP_COUNT = 10;

    /** The thread being monitored. */
    protected Thread runningThread;

    /** Flag indicating if the thread is still running. */
    protected AtomicBoolean running = new AtomicBoolean();

    /**
     * Constructs an AccessTimeoutTarget with the specified thread.
     * @param thread The thread to monitor.
     */
    public AccessTimeoutTarget(final Thread thread) {
        runningThread = thread;
        running.set(true);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.codelibs.core.timer.TimeoutTarget#expired()
     */
    @Override
    public void expired() {
        int count = 0;
        while (running.get() && count < MAX_LOOP_COUNT) {
            if (logger.isDebugEnabled()) {
                logger.debug("Interrupt {}", runningThread);
            }
            runningThread.interrupt();
            ThreadUtil.sleepQuietly(1000L);
            count++;
        }
    }

    /**
     * Stops the timeout target by setting the running flag to false.
     */
    public void stop() {
        if (logger.isDebugEnabled()) {
            logger.debug("Timeout target has been stopped.");
        }
        running.set(false);
    }
}
