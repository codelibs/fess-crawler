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
package org.codelibs.fess.crawler.client.http;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.HttpClientConnectionManager;
import org.codelibs.core.timer.TimeoutTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class HcConnectionMonitorTarget implements TimeoutTarget {
    private static final Logger logger = LoggerFactory
            .getLogger(HcConnectionMonitorTarget.class);

    private final HttpClientConnectionManager clientConnectionManager;

    private final long idleConnectionTimeout;

    public HcConnectionMonitorTarget(
            final HttpClientConnectionManager clientConnectionManager,
            final long idleConnectionTimeout) {
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
            clientConnectionManager.closeIdleConnections(idleConnectionTimeout,
                    TimeUnit.MILLISECONDS);
        } catch (final Exception e) {
            logger.warn("A connection monitoring exception occurs.", e);
        }
    }

}
