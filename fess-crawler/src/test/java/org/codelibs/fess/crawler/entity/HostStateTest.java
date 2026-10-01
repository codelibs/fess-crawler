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
package org.codelibs.fess.crawler.entity;

import org.codelibs.fess.crawler.entity.HostState.RobotsTxtStatus;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link HostState}.
 */
public class HostStateTest extends PlainTestCase {

    private static final long BASE = 10000L;

    private static final long MAX = 300000L;

    @Test
    public void test_toOrigin() {
        assertEquals("http://example.com", HostState.toOrigin("http://Example.com/a"));
        assertEquals("https://h:8443", HostState.toOrigin("https://h:8443/x"));
        assertEquals("http://h", HostState.toOrigin("HTTP://H"));
        assertEquals("http://h:80", HostState.toOrigin("http://h:80/a"));
        assertEquals("http://h", HostState.toOrigin("http://user:pw@h/a"));
        assertEquals("http://[::1]:8080", HostState.toOrigin("http://[::1]:8080/a"));
        assertEquals("http://h", HostState.toOrigin("http://h?q=1"));
        assertNull(HostState.toOrigin("file:/tmp/x"));
        assertEquals("http://h", HostState.toOrigin("http://h#f"));
        assertNull(HostState.toOrigin("file:///tmp/x"));
        assertNull(HostState.toOrigin("mailto:a@b"));
        assertNull(HostState.toOrigin("not a url"));
        assertNull(HostState.toOrigin(""));
        assertNull(HostState.toOrigin(null));
    }

    @Test
    public void test_toPathAndQuery() {
        assertEquals("/", HostState.toPathAndQuery("http://h"));
        assertEquals("/", HostState.toPathAndQuery("http://h/"));
        assertEquals("/a?b=c", HostState.toPathAndQuery("http://h/a?b=c"));
        assertEquals("/?b=c", HostState.toPathAndQuery("http://h?b=c"));
        assertEquals("/a%20b", HostState.toPathAndQuery("http://h/a%20b"));
        assertEquals("/a?b=c", HostState.toPathAndQuery("http://h:8080/a?b=c#frag"));
        assertNull(HostState.toPathAndQuery("mailto:a@b"));
        assertNull(HostState.toPathAndQuery(null));
    }

    @Test
    public void test_recordFailure_exponential() {
        final HostState state = new HostState();
        assertEquals(10000L, state.recordFailure(1000L, 0L, BASE, MAX));
        assertEquals(11000L, state.getBackoffUntil());
        assertEquals(20000L, state.recordFailure(2000L, 0L, BASE, MAX));
        assertEquals(22000L, state.getBackoffUntil());
        assertEquals(40000L, state.recordFailure(3000L, 0L, BASE, MAX));
        assertEquals(80000L, state.recordFailure(4000L, 0L, BASE, MAX));
        assertEquals(160000L, state.recordFailure(5000L, 0L, BASE, MAX));
        assertEquals(MAX, state.recordFailure(6000L, 0L, BASE, MAX));
        assertEquals(MAX, state.recordFailure(7000L, 0L, BASE, MAX));
        assertEquals(7000L + MAX, state.getBackoffUntil());
    }

    @Test
    public void test_recordFailure_manyFailuresDoNotOverflow() {
        final HostState state = new HostState();
        for (int i = 0; i < 200; i++) {
            final long wait = state.recordFailure(0L, 0L, BASE, MAX);
            assertTrue(wait > 0L && wait <= MAX);
        }
        assertEquals(MAX, state.recordFailure(0L, 0L, BASE, MAX));
    }

    @Test
    public void test_recordFailure_retryAfter() {
        final HostState state = new HostState();
        assertEquals(5000L, state.recordFailure(0L, 5000L, BASE, MAX));
        assertEquals(MAX, state.recordFailure(0L, 86400000L, BASE, MAX));
        assertEquals(5000L, state.getBackoffUntil() - 295000L);
    }

    @Test
    public void test_recordFailure_nonPositiveRetryAfterUsesExponential() {
        final HostState state = new HostState();
        assertEquals(10000L, state.recordFailure(0L, 0L, BASE, MAX));
        assertEquals(20000L, state.recordFailure(0L, -1L, BASE, MAX));
        assertEquals(40000L, state.recordFailure(0L, Long.MIN_VALUE, BASE, MAX));
    }

    @Test
    public void test_recordFailure_neverMovesBackoffEarlier() {
        final HostState state = new HostState();
        state.recordFailure(0L, 200000L, BASE, MAX);
        assertEquals(200000L, state.getBackoffUntil());
        state.recordFailure(0L, 1000L, BASE, MAX);
        assertEquals(200000L, state.getBackoffUntil());
    }

    @Test
    public void test_recordSuccess_resetsExponent() {
        final HostState state = new HostState();
        state.recordFailure(0L, 0L, BASE, MAX);
        state.recordFailure(0L, 0L, BASE, MAX);
        state.recordSuccess();
        assertEquals(10000L, state.recordFailure(0L, 0L, BASE, MAX));
    }

    @Test
    public void test_recordSuccess_keepsBackoffUntil() {
        final HostState state = new HostState();
        state.recordFailure(1000L, 0L, BASE, MAX);
        state.recordSuccess();
        assertEquals(11000L, state.getBackoffUntil());
    }

    @Test
    public void test_isAllowedByRobotsTxt() {
        final HostState state = new HostState();
        assertNull(state.getRobotsTxtStatus());
        assertTrue(state.isAllowedByRobotsTxt("http://h/private/a"));

        state.setRobotsTxt(RobotsTxtStatus.ALLOW_ALL, null, 0L);
        assertTrue(state.isAllowedByRobotsTxt("http://h/private/a"));

        state.setRobotsTxt(RobotsTxtStatus.UNAVAILABLE, null, 0L);
        assertTrue(state.isAllowedByRobotsTxt("http://h/private/a"));

        state.setRobotsTxt(RobotsTxtStatus.DISALLOW_ALL, null, 0L);
        assertFalse(state.isAllowedByRobotsTxt("http://h/a"));

        state.setRobotsTxt(RobotsTxtStatus.PARSED, null, 0L);
        assertTrue(state.isAllowedByRobotsTxt("http://h/private/a"));

        final RobotsTxt.Directive directive = new RobotsTxt.Directive("*");
        directive.addDisallow("/private/");
        directive.addAllow("/private/open");
        state.setRobotsTxt(RobotsTxtStatus.PARSED, directive, 0L);
        assertEquals(RobotsTxtStatus.PARSED, state.getRobotsTxtStatus());
        assertFalse(state.isAllowedByRobotsTxt("http://h/private/a"));
        assertFalse(state.isAllowedByRobotsTxt("http://h/private/a?x=1"));
        assertTrue(state.isAllowedByRobotsTxt("http://h/private/open"));
        assertTrue(state.isAllowedByRobotsTxt("http://h/public"));
    }

    @Test
    public void test_robotsTxtState() {
        final HostState state = new HostState();
        assertEquals(0L, state.getCrawlDelayMillis());
        state.setRobotsTxt(RobotsTxtStatus.PARSED, new RobotsTxt.Directive("*"), 1500L);
        assertEquals(1500L, state.getCrawlDelayMillis());
        assertEquals(1, state.incrementAndGetRobotsTxtFailureCount());
        assertEquals(2, state.incrementAndGetRobotsTxtFailureCount());
    }

    @Test
    public void test_setRobotsTxt_resolvedStatusResetsFailureCount() {
        final HostState state = new HostState();
        assertEquals(1, state.incrementAndGetRobotsTxtFailureCount());
        state.setRobotsTxt(RobotsTxtStatus.UNAVAILABLE, null, 0L);
        assertEquals(2, state.incrementAndGetRobotsTxtFailureCount());
        state.setRobotsTxt(RobotsTxtStatus.PARSED, null, 0L);
        assertEquals(1, state.incrementAndGetRobotsTxtFailureCount());
        state.setRobotsTxt(RobotsTxtStatus.ALLOW_ALL, null, 0L);
        assertEquals(1, state.incrementAndGetRobotsTxtFailureCount());
        state.setRobotsTxt(RobotsTxtStatus.DISALLOW_ALL, null, 0L);
        assertEquals(1, state.incrementAndGetRobotsTxtFailureCount());
    }

    @Test
    public void test_lastAccessTime() {
        final HostState state = new HostState();
        assertEquals(0L, state.getLastAccessTime());
        state.setLastAccessTime(1234L);
        assertEquals(1234L, state.getLastAccessTime());
    }
}
