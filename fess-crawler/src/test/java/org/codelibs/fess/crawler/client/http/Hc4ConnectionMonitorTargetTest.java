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
package org.codelibs.fess.crawler.client.http;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.HttpClientConnectionManager;
import org.dbflute.utflute.core.PlainTestCase;

public class Hc4ConnectionMonitorTargetTest extends PlainTestCase {

    public void test_expired_closesExpiredConnections() {
        HttpClientConnectionManager mockManager = mock(HttpClientConnectionManager.class);
        long idleTimeout = 60000L;

        Hc4ConnectionMonitorTarget target = new Hc4ConnectionMonitorTarget(mockManager, idleTimeout);
        target.expired();

        verify(mockManager).closeExpiredConnections();
    }

    public void test_expired_closesIdleConnections() {
        HttpClientConnectionManager mockManager = mock(HttpClientConnectionManager.class);
        long idleTimeout = 30000L;

        Hc4ConnectionMonitorTarget target = new Hc4ConnectionMonitorTarget(mockManager, idleTimeout);
        target.expired();

        verify(mockManager).closeIdleConnections(eq(idleTimeout), eq(TimeUnit.MILLISECONDS));
    }

    public void test_expired_nullConnectionManager() {
        // Should not throw exception when clientConnectionManager is null
        Hc4ConnectionMonitorTarget target = new Hc4ConnectionMonitorTarget(null, 60000L);
        target.expired(); // Just verify it doesn't throw
    }

    public void test_expired_exceptionHandling() {
        HttpClientConnectionManager mockManager = mock(HttpClientConnectionManager.class);
        doThrow(new RuntimeException("Test exception")).when(mockManager).closeExpiredConnections();

        Hc4ConnectionMonitorTarget target = new Hc4ConnectionMonitorTarget(mockManager, 60000L);
        // Should not throw exception even if closeExpiredConnections throws
        target.expired();
    }

    public void test_constructor() {
        HttpClientConnectionManager mockManager = mock(HttpClientConnectionManager.class);
        long idleTimeout = 120000L;

        Hc4ConnectionMonitorTarget target = new Hc4ConnectionMonitorTarget(mockManager, idleTimeout);

        // Verify constructor works and expired() can be called
        assertNotNull(target);
        target.expired();
        verify(mockManager).closeExpiredConnections();
        verify(mockManager).closeIdleConnections(eq(idleTimeout), eq(TimeUnit.MILLISECONDS));
    }
}
