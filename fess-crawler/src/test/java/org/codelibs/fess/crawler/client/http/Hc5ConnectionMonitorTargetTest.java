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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.dbflute.utflute.core.PlainTestCase;

public class Hc5ConnectionMonitorTargetTest extends PlainTestCase {

    public void test_expired_closesExpiredConnections() {
        PoolingHttpClientConnectionManager mockManager = mock(PoolingHttpClientConnectionManager.class);
        long idleTimeout = 60000L;

        Hc5ConnectionMonitorTarget target = new Hc5ConnectionMonitorTarget(mockManager, idleTimeout);
        target.expired();

        verify(mockManager).closeExpired();
    }

    public void test_expired_closesIdleConnections() {
        PoolingHttpClientConnectionManager mockManager = mock(PoolingHttpClientConnectionManager.class);
        long idleTimeout = 30000L;

        Hc5ConnectionMonitorTarget target = new Hc5ConnectionMonitorTarget(mockManager, idleTimeout);
        target.expired();

        verify(mockManager).closeIdle(any(TimeValue.class));
    }

    public void test_expired_nullConnectionManager() {
        // Should not throw exception when clientConnectionManager is null
        Hc5ConnectionMonitorTarget target = new Hc5ConnectionMonitorTarget(null, 60000L);
        target.expired(); // Just verify it doesn't throw
    }

    public void test_expired_exceptionHandling() {
        PoolingHttpClientConnectionManager mockManager = mock(PoolingHttpClientConnectionManager.class);
        doThrow(new RuntimeException("Test exception")).when(mockManager).closeExpired();

        Hc5ConnectionMonitorTarget target = new Hc5ConnectionMonitorTarget(mockManager, 60000L);
        // Should not throw exception even if closeExpired throws
        target.expired();
    }

    public void test_constructor() {
        PoolingHttpClientConnectionManager mockManager = mock(PoolingHttpClientConnectionManager.class);
        long idleTimeout = 120000L;

        Hc5ConnectionMonitorTarget target = new Hc5ConnectionMonitorTarget(mockManager, idleTimeout);

        // Verify constructor works and expired() can be called
        assertNotNull(target);
        target.expired();
        verify(mockManager).closeExpired();
        verify(mockManager).closeIdle(any(TimeValue.class));
    }
}
