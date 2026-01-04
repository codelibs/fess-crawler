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

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.timer.TimeoutTarget;

/**
 * Hc4ConnectionMonitorTarget is responsible for monitoring and managing HTTP client connections
 * for Apache HttpComponents 4.x. It implements the TimeoutTarget interface to handle connection
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
public class Hc4ConnectionMonitorTarget implements TimeoutTarget {
    private static final Logger logger = LogManager.getLogger(Hc4ConnectionMonitorTarget.class);

    private final HttpClientConnectionManager clientConnectionManager;

    /**
     * The timeout duration (in milliseconds) for idle connections.
     */
    private final long idleConnectionTimeout;

    /**
     * Constructs a new Hc4ConnectionMonitorTarget.
     * @param clientConnectionManager The HttpClientConnectionManager to monitor.
     * @param idleConnectionTimeout The idle connection timeout in milliseconds.
     */
    public Hc4ConnectionMonitorTarget(final HttpClientConnectionManager clientConnectionManager, final long idleConnectionTimeout) {
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
            clientConnectionManager.closeExpiredConnections();
            // Close idle connections
            clientConnectionManager.closeIdleConnections(idleConnectionTimeout, TimeUnit.MILLISECONDS);
        } catch (final Exception e) {
            logger.warn("A connection monitoring exception occurs.", e);
        }
    }

}
