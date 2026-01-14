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

import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.interval.IntervalController;
import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test cases for {@link AbstractIntervalController}
 *
 * @author shinsuke
 */
public class AbstractIntervalControllerTest extends PlainTestCase {

    /**
     * Test implementation of AbstractIntervalController for testing
     */
    private static class TestIntervalController extends AbstractIntervalController {
        int beforeProcessingCount = 0;
        int afterProcessingCount = 0;
        int noUrlInQueueCount = 0;
        int waitNewUrlCount = 0;
        boolean shouldThrowException = false;

        @Override
        protected void delayBeforeProcessing() {
            beforeProcessingCount++;
            if (shouldThrowException) {
                throw new RuntimeException("Test exception in delayBeforeProcessing");
            }
        }

        @Override
        protected void delayAfterProcessing() {
            afterProcessingCount++;
            if (shouldThrowException) {
                throw new RuntimeException("Test exception in delayAfterProcessing");
            }
        }

        @Override
        protected void delayAtNoUrlInQueue() {
            noUrlInQueueCount++;
            if (shouldThrowException) {
                throw new RuntimeException("Test exception in delayAtNoUrlInQueue");
            }
        }

        @Override
        protected void delayForWaitingNewUrl() {
            waitNewUrlCount++;
            if (shouldThrowException) {
                throw new RuntimeException("Test exception in delayForWaitingNewUrl");
            }
        }

        void reset() {
            beforeProcessingCount = 0;
            afterProcessingCount = 0;
            noUrlInQueueCount = 0;
            waitNewUrlCount = 0;
            shouldThrowException = false;
        }
    }

    /**
     * Test PRE_PROCESSING delay type
     */
    @Test
    public void test_delay_preProcessing() {
        final TestIntervalController controller = new TestIntervalController();
        controller.delay(IntervalController.PRE_PROCESSING);

        assertEquals(1, controller.beforeProcessingCount);
        assertEquals(0, controller.afterProcessingCount);
        assertEquals(0, controller.noUrlInQueueCount);
        assertEquals(0, controller.waitNewUrlCount);
    }

    /**
     * Test POST_PROCESSING delay type
     */
    @Test
    public void test_delay_postProcessing() {
        final TestIntervalController controller = new TestIntervalController();
        controller.delay(IntervalController.POST_PROCESSING);

        assertEquals(0, controller.beforeProcessingCount);
        assertEquals(1, controller.afterProcessingCount);
        assertEquals(0, controller.noUrlInQueueCount);
        assertEquals(0, controller.waitNewUrlCount);
    }

    /**
     * Test NO_URL_IN_QUEUE delay type
     */
    @Test
    public void test_delay_noUrlInQueue() {
        final TestIntervalController controller = new TestIntervalController();
        controller.delay(IntervalController.NO_URL_IN_QUEUE);

        assertEquals(0, controller.beforeProcessingCount);
        assertEquals(0, controller.afterProcessingCount);
        assertEquals(1, controller.noUrlInQueueCount);
        assertEquals(0, controller.waitNewUrlCount);
    }

    /**
     * Test WAIT_NEW_URL delay type
     */
    @Test
    public void test_delay_waitNewUrl() {
        final TestIntervalController controller = new TestIntervalController();
        controller.delay(IntervalController.WAIT_NEW_URL);

        assertEquals(0, controller.beforeProcessingCount);
        assertEquals(0, controller.afterProcessingCount);
        assertEquals(0, controller.noUrlInQueueCount);
        assertEquals(1, controller.waitNewUrlCount);
    }

    /**
     * Test unknown delay type (should not throw exception, just log)
     */
    @Test
    public void test_delay_unknownType() {
        final TestIntervalController controller = new TestIntervalController();
        controller.delay(999); // Unknown type

        assertEquals(0, controller.beforeProcessingCount);
        assertEquals(0, controller.afterProcessingCount);
        assertEquals(0, controller.noUrlInQueueCount);
        assertEquals(0, controller.waitNewUrlCount);
        // Should not throw exception, just logged
    }

    /**
     * Test exception handling with ignoreException = true (default)
     */
    @Test
    public void test_exceptionHandling_ignoreTrue() {
        final TestIntervalController controller = new TestIntervalController();
        controller.shouldThrowException = true;
        assertTrue(controller.isIgnoreException()); // default is true

        // Should not throw exception
        controller.delay(IntervalController.PRE_PROCESSING);
        assertEquals(1, controller.beforeProcessingCount);
    }

    /**
     * Test exception handling with ignoreException = false
     */
    @Test
    public void test_exceptionHandling_ignoreFalse() {
        final TestIntervalController controller = new TestIntervalController();
        controller.shouldThrowException = true;
        controller.setIgnoreException(false);
        assertFalse(controller.isIgnoreException());

        // Should throw CrawlerSystemException
        try {
            controller.delay(IntervalController.PRE_PROCESSING);
            fail();
        } catch (final CrawlerSystemException e) {
            // Expected
            assertTrue(e.getMessage().contains("Could not stop a process"));
        }
    }

    /**
     * Test CrawlerSystemException is re-thrown when ignoreException = false
     */
    @Test
    public void test_crawlerSystemExceptionRethrown() {
        final AbstractIntervalController controller = new AbstractIntervalController() {
            @Override
            protected void delayBeforeProcessing() {
                throw new CrawlerSystemException("Test CrawlerSystemException");
            }

            @Override
            protected void delayAfterProcessing() {
            }

            @Override
            protected void delayAtNoUrlInQueue() {
            }

            @Override
            protected void delayForWaitingNewUrl() {
            }
        };
        controller.setIgnoreException(false);

        try {
            controller.delay(IntervalController.PRE_PROCESSING);
            fail();
        } catch (final CrawlerSystemException e) {
            // Expected
            assertEquals("Test CrawlerSystemException", e.getMessage());
        }
    }

    /**
     * Test CrawlerSystemException is ignored when ignoreException = true
     */
    @Test
    public void test_crawlerSystemExceptionIgnored() {
        final AbstractIntervalController controller = new AbstractIntervalController() {
            @Override
            protected void delayBeforeProcessing() {
                throw new CrawlerSystemException("Test CrawlerSystemException");
            }

            @Override
            protected void delayAfterProcessing() {
            }

            @Override
            protected void delayAtNoUrlInQueue() {
            }

            @Override
            protected void delayForWaitingNewUrl() {
            }
        };
        controller.setIgnoreException(true);

        // Should not throw exception
        controller.delay(IntervalController.PRE_PROCESSING);
        // If we reach here, exception was ignored
        assertTrue(true);
    }

    /**
     * Test setter for ignoreException
     */
    @Test
    public void test_setIgnoreException() {
        final TestIntervalController controller = new TestIntervalController();

        assertTrue(controller.isIgnoreException()); // default

        controller.setIgnoreException(false);
        assertFalse(controller.isIgnoreException());

        controller.setIgnoreException(true);
        assertTrue(controller.isIgnoreException());
    }

    /**
     * Test multiple delay calls
     */
    @Test
    public void test_multipleDelayCalls() {
        final TestIntervalController controller = new TestIntervalController();

        controller.delay(IntervalController.PRE_PROCESSING);
        controller.delay(IntervalController.POST_PROCESSING);
        controller.delay(IntervalController.NO_URL_IN_QUEUE);
        controller.delay(IntervalController.WAIT_NEW_URL);

        assertEquals(1, controller.beforeProcessingCount);
        assertEquals(1, controller.afterProcessingCount);
        assertEquals(1, controller.noUrlInQueueCount);
        assertEquals(1, controller.waitNewUrlCount);
    }

    /**
     * Test all delay types in sequence
     */
    @Test
    public void test_allDelayTypesInSequence() {
        final TestIntervalController controller = new TestIntervalController();

        int[] types = { IntervalController.PRE_PROCESSING, IntervalController.POST_PROCESSING, IntervalController.NO_URL_IN_QUEUE,
                IntervalController.WAIT_NEW_URL };

        for (int type : types) {
            controller.reset();
            controller.delay(type);

            int totalCalls = controller.beforeProcessingCount + controller.afterProcessingCount + controller.noUrlInQueueCount
                    + controller.waitNewUrlCount;
            assertEquals(1, totalCalls);
        }
    }
}
