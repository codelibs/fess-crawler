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

import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.timer.TimeoutTarget;

/**
 * Hc5ConnectionMonitorTarget is responsible for monitoring and managing HTTP client connections
 * for Apache HttpComponents 5.x. It implements the TimeoutTarget interface to handle connection
 * expiration events.
 *
 * <p>This class uses an instance of HttpClientConnectionManager to manage the connections.
 * It closes expired connections and idle connections that have exceeded a specified timeout.
 * </p>
 *
 * clientConnectionManager the HttpClientConnectionManager used to manage connections
 * idleConnectionTimeout the timeout duration (in milliseconds) for idle connections
 *
 * <p>Methods:</p>
 * <ul>
 *   <li>{@link #expired()}: Handles the expiration of connections by closing expired and idle connections.</li>
 * </ul>
 *
 * <p>Logging:</p>
 * <ul>
 *   <li>Logs a warning if the clientConnectionManager is null.</li>
 *   <li>Logs a warning if an exception occurs during connection monitoring.</li>
 * </ul>
 *
 */
public class Hc5ConnectionMonitorTarget implements TimeoutTarget {
    private static final Logger logger = LogManager.getLogger(Hc5ConnectionMonitorTarget.class);

    private final PoolingHttpClientConnectionManager clientConnectionManager;

    /**
     * The timeout duration (in milliseconds) for idle connections.
     */
    private final long idleConnectionTimeout;

    /**
     * Constructs a new Hc5ConnectionMonitorTarget.
     * @param clientConnectionManager The PoolingHttpClientConnectionManager to monitor.
     * @param idleConnectionTimeout The idle connection timeout in milliseconds.
     */
    public Hc5ConnectionMonitorTarget(final PoolingHttpClientConnectionManager clientConnectionManager, final long idleConnectionTimeout) {
        this.clientConnectionManager = clientConnectionManager;
        this.idleConnectionTimeout = idleConnectionTimeout;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.seasar.extension.timer.TimeoutTarget#expired()
     */
    @Override
    public void expired() {
        if (clientConnectionManager == null) {
            logger.warn("clientConnectionManager is null.");
            return;
        }

        try {
            // Close expired connections
            clientConnectionManager.closeExpired();
            // Close idle connections
            clientConnectionManager.closeIdle(TimeValue.ofMilliseconds(idleConnectionTimeout));
        } catch (final Exception e) {
            logger.warn("A connection monitoring exception occurs.", e);
        }
    }

}
