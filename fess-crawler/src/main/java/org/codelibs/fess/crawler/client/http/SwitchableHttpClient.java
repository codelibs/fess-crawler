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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.client.CrawlerClient;
import org.codelibs.fess.crawler.client.FaultTolerantClient;

/**
 * SwitchableHttpClient is a switchable HTTP client that can use either Apache HttpComponents 4.x or 5.x.
 * The client implementation is selected based on the system property {@code fess.crawler.http.client}.
 *
 * <p>Supported values:</p>
 * <ul>
 *   <li>{@code hc4} - Use Apache HttpComponents 4.x ({@link Hc4HttpClient})</li>
 *   <li>{@code hc5} or not set - Use Apache HttpComponents 5.x ({@link Hc5HttpClient}) - default</li>
 * </ul>
 *
 * <p>This class extends {@link FaultTolerantClient} to provide automatic retry functionality
 * with the selected HTTP client implementation.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * // Use HC4 client
 * java -Dfess.crawler.http.client=hc4 ...
 *
 * // Use HC5 client (default)
 * java -Dfess.crawler.http.client=hc5 ...
 * // or simply
 * java ...
 * }
 * </pre>
 *
 * @author shinsuke
 */
public class SwitchableHttpClient extends FaultTolerantClient {

    private static final Logger logger = LogManager.getLogger(SwitchableHttpClient.class);

    /** System property name to select HTTP client implementation. */
    public static final String HTTP_CLIENT_PROPERTY = "fess.crawler.http.client";

    /** Value for HC4 client selection. */
    public static final String HC4_CLIENT = "hc4";

    /** Value for HC5 client selection. */
    public static final String HC5_CLIENT = "hc5";

    /** The HC4 HTTP client instance. */
    protected CrawlerClient hc4Client;

    /** The HC5 HTTP client instance. */
    protected CrawlerClient hc5Client;

    /** Flag indicating which client is selected (true = hc5, false = hc4). */
    private boolean useHc5 = true;

    /**
     * Constructs a new SwitchableHttpClient.
     * The client implementation is determined by the system property.
     */
    public SwitchableHttpClient() {
        super();
        selectClient();
    }

    /**
     * Selects the appropriate HTTP client based on system property.
     */
    protected void selectClient() {
        final String clientType = System.getProperty(HTTP_CLIENT_PROPERTY);
        if (HC4_CLIENT.equalsIgnoreCase(clientType)) {
            useHc5 = false;
            if (logger.isInfoEnabled()) {
                logger.info("Using HC4 HTTP client (Apache HttpComponents 4.x)");
            }
        } else {
            useHc5 = true;
            if (logger.isInfoEnabled()) {
                logger.info("Using HC5 HTTP client (Apache HttpComponents 5.x)");
            }
        }
    }

    /**
     * Initializes the selected HTTP client.
     * This method should be called after the DI container has injected both clients.
     */
    public void init() {
        if (useHc5) {
            if (hc5Client != null) {
                setCrawlerClient(hc5Client);
            } else {
                logger.warn("HC5 client is null, falling back to HC4 client");
                setCrawlerClient(hc4Client);
            }
        } else {
            if (hc4Client != null) {
                setCrawlerClient(hc4Client);
            } else {
                logger.warn("HC4 client is null, falling back to HC5 client");
                setCrawlerClient(hc5Client);
            }
        }
    }

    /**
     * Sets the HC4 HTTP client instance.
     *
     * @param hc4Client the HC4 HTTP client
     */
    public void setHc4Client(final CrawlerClient hc4Client) {
        this.hc4Client = hc4Client;
    }

    /**
     * Gets the HC4 HTTP client instance.
     *
     * @return the HC4 HTTP client
     */
    public CrawlerClient getHc4Client() {
        return hc4Client;
    }

    /**
     * Sets the HC5 HTTP client instance.
     *
     * @param hc5Client the HC5 HTTP client
     */
    public void setHc5Client(final CrawlerClient hc5Client) {
        this.hc5Client = hc5Client;
    }

    /**
     * Gets the HC5 HTTP client instance.
     *
     * @return the HC5 HTTP client
     */
    public CrawlerClient getHc5Client() {
        return hc5Client;
    }

    /**
     * Returns whether the HC5 client is being used.
     *
     * @return true if HC5 client is used, false if HC4 client is used
     */
    public boolean isUseHc5() {
        return useHc5;
    }
}
