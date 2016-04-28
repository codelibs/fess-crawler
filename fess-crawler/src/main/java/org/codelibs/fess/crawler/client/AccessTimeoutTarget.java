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