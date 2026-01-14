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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test cases for {@link DefaultIntervalController}
 *
 * @author shinsuke
 */
public class DefaultIntervalControllerTest extends PlainTestCase {

    /**
     * Test default constructor initializes with default values
     */
    @Test
    public void test_defaultConstructor() {
        final DefaultIntervalController controller = new DefaultIntervalController();

        assertEquals(0L, controller.getDelayMillisAfterProcessing());
        assertEquals(500L, controller.getDelayMillisAtNoUrlInQueue());
        assertEquals(0L, controller.getDelayMillisBeforeProcessing());
        assertEquals(1000L, controller.getDelayMillisForWaitingNewUrl());
    }

    /**
     * Test constructor with parameters
     */
    @Test
    public void test_constructorWithParams() {
        final Map<String, Long> params = new HashMap<>();
        params.put("delayMillisAfterProcessing", 100L);
        params.put("delayMillisAtNoUrlInQueue", 200L);
        params.put("delayMillisBeforeProcessing", 300L);
        params.put("delayMillisForWaitingNewUrl", 400L);

        final DefaultIntervalController controller = new DefaultIntervalController(params);

        assertEquals(100L, controller.getDelayMillisAfterProcessing());
        assertEquals(200L, controller.getDelayMillisAtNoUrlInQueue());
        assertEquals(300L, controller.getDelayMillisBeforeProcessing());
        assertEquals(400L, controller.getDelayMillisForWaitingNewUrl());
    }

    /**
     * Test constructor with partial parameters uses defaults for missing values
     */
    @Test
    public void test_constructorWithPartialParams() {
        final Map<String, Long> params = new HashMap<>();
        params.put("delayMillisAfterProcessing", 150L);
        params.put("delayMillisBeforeProcessing", 250L);
        // Not setting delayMillisAtNoUrlInQueue and delayMillisForWaitingNewUrl

        final DefaultIntervalController controller = new DefaultIntervalController(params);

        assertEquals(150L, controller.getDelayMillisAfterProcessing());
        assertEquals(500L, controller.getDelayMillisAtNoUrlInQueue()); // default
        assertEquals(250L, controller.getDelayMillisBeforeProcessing());
        assertEquals(1000L, controller.getDelayMillisForWaitingNewUrl()); // default
    }

    /**
     * Test constructor with empty parameters uses all defaults
     */
    @Test
    public void test_constructorWithEmptyParams() {
        final Map<String, Long> params = new HashMap<>();

        final DefaultIntervalController controller = new DefaultIntervalController(params);

        assertEquals(0L, controller.getDelayMillisAfterProcessing());
        assertEquals(500L, controller.getDelayMillisAtNoUrlInQueue());
        assertEquals(0L, controller.getDelayMillisBeforeProcessing());
        assertEquals(1000L, controller.getDelayMillisForWaitingNewUrl());
    }

    /**
     * Test setters
     */
    @Test
    public void test_setters() {
        final DefaultIntervalController controller = new DefaultIntervalController();

        controller.setDelayMillisAfterProcessing(111L);
        controller.setDelayMillisAtNoUrlInQueue(222L);
        controller.setDelayMillisBeforeProcessing(333L);
        controller.setDelayMillisForWaitingNewUrl(444L);

        assertEquals(111L, controller.getDelayMillisAfterProcessing());
        assertEquals(222L, controller.getDelayMillisAtNoUrlInQueue());
        assertEquals(333L, controller.getDelayMillisBeforeProcessing());
        assertEquals(444L, controller.getDelayMillisForWaitingNewUrl());
    }

    /**
     * Test delayAfterProcessing with zero delay
     */
    @Test
    public void test_delayAfterProcessing_zeroDelay() {
        final DefaultIntervalController controller = new DefaultIntervalController();
        controller.setDelayMillisAfterProcessing(0L);

        final long start = System.nanoTime();
        controller.delayAfterProcessing();
        final long elapsed = (System.nanoTime() - start) / 1000000;

        assertTrue(elapsed < 50); // Should be nearly instant
    }

    /**
     * Test delayAfterProcessing with actual delay
     */
    @Test
    public void test_delayAfterProcessing_withDelay() {
        final DefaultIntervalController controller = new DefaultIntervalController();
        controller.setDelayMillisAfterProcessing(100L);

        final long start = System.nanoTime();
        controller.delayAfterProcessing();
        final long elapsed = (System.nanoTime() - start) / 1000000;

        assertTrue(elapsed >= 90); // Allow some tolerance
    }

    /**
     * Test delayBeforeProcessing with zero delay
     */
    @Test
    public void test_delayBeforeProcessing_zeroDelay() {
        final DefaultIntervalController controller = new DefaultIntervalController();
        controller.setDelayMillisBeforeProcessing(0L);

        final long start = System.nanoTime();
        controller.delayBeforeProcessing();
        final long elapsed = (System.nanoTime() - start) / 1000000;

        assertTrue(elapsed < 50);
    }

    /**
     * Test delayBeforeProcessing with actual delay
     */
    @Test
    public void test_delayBeforeProcessing_withDelay() {
        final DefaultIntervalController controller = new DefaultIntervalController();
        controller.setDelayMillisBeforeProcessing(100L);

        final long start = System.nanoTime();
        controller.delayBeforeProcessing();
        final long elapsed = (System.nanoTime() - start) / 1000000;

        assertTrue(elapsed >= 90);
    }

    /**
     * Test delayAtNoUrlInQueue with default delay
     */
    @Test
    public void test_delayAtNoUrlInQueue_defaultDelay() {
        final DefaultIntervalController controller = new DefaultIntervalController();

        final long start = System.nanoTime();
        controller.delayAtNoUrlInQueue();
        final long elapsed = (System.nanoTime() - start) / 1000000;

        assertTrue(elapsed >= 450); // default is 500ms
    }

    /**
     * Test delayForWaitingNewUrl with default delay
     */
    @Test
    public void test_delayForWaitingNewUrl_defaultDelay() {
        final DefaultIntervalController controller = new DefaultIntervalController();

        final long start = System.nanoTime();
        controller.delayForWaitingNewUrl();
        final long elapsed = (System.nanoTime() - start) / 1000000;

        assertTrue(elapsed >= 950); // default is 1000ms
    }

    /**
     * Test delayForWaitingNewUrl with custom delay
     */
    @Test
    public void test_delayForWaitingNewUrl_customDelay() {
        final DefaultIntervalController controller = new DefaultIntervalController();
        controller.setDelayMillisForWaitingNewUrl(200L);

        final long start = System.nanoTime();
        controller.delayForWaitingNewUrl();
        final long elapsed = (System.nanoTime() - start) / 1000000;

        assertTrue(elapsed >= 180);
    }

    /**
     * Test that negative delay values are handled (treated as zero)
     */
    @Test
    public void test_negativeDelayValues() {
        final DefaultIntervalController controller = new DefaultIntervalController();
        controller.setDelayMillisAfterProcessing(-100L);

        final long start = System.nanoTime();
        controller.delayAfterProcessing();
        final long elapsed = (System.nanoTime() - start) / 1000000;

        assertTrue(elapsed < 50);
    }
}
