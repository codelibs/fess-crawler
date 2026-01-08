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

import org.codelibs.fess.crawler.client.AbstractCrawlerClient;

/**
 * HcHttpClient is the abstract base class for HTTP client implementations
 * using Apache HttpComponents. This class provides common constants and
 * configurations shared between HC4 and HC5 implementations.
 *
 * <p>Key properties and configurations:</p>
 * <ul>
 *   <li>CONNECTION_TIMEOUT_PROPERTY: Connection timeout setting.</li>
 *   <li>SO_TIMEOUT_PROPERTY: Socket timeout setting.</li>
 *   <li>PROXY_HOST_PROPERTY: Proxy host setting.</li>
 *   <li>PROXY_PORT_PROPERTY: Proxy port setting.</li>
 *   <li>PROXY_AUTH_SCHEME_PROPERTY: Proxy authentication scheme.</li>
 *   <li>PROXY_CREDENTIALS_PROPERTY: Proxy credentials.</li>
 *   <li>USER_AGENT_PROPERTY: User agent string.</li>
 *   <li>ROBOTS_TXT_ENABLED_PROPERTY: Enable or disable robots.txt parsing.</li>
 *   <li>AUTHENTICATIONS_PROPERTY: Web authentications.</li>
 *   <li>REQUEST_HEADERS_PROPERTY: Custom request headers.</li>
 *   <li>REDIRECTS_ENABLED: Enable or disable HTTP redirects.</li>
 *   <li>COOKIES_PROPERTY: Cookie settings.</li>
 *   <li>AUTH_SCHEME_PROVIDERS_PROPERTY: Authentication scheme providers.</li>
 *   <li>IGNORE_SSL_CERTIFICATE_PROPERTY: Ignore SSL certificate validation.</li>
 *   <li>DEFAULT_MAX_CONNECTION_PER_ROUTE_PROPERTY: Default maximum connections per route.</li>
 *   <li>MAX_TOTAL_CONNECTION_PROPERTY: Maximum total connections.</li>
 *   <li>TIME_TO_LIVE_TIME_UNIT_PROPERTY: Time to live unit for connections.</li>
 *   <li>TIME_TO_LIVE_PROPERTY: Time to live for connections.</li>
 * </ul>
 *
 * @author shinsuke
 */
public abstract class HcHttpClient extends AbstractCrawlerClient {

    /** Property name for connection timeout setting */
    public static final String CONNECTION_TIMEOUT_PROPERTY = "connectionTimeout";

    /** Property name for socket timeout setting */
    public static final String SO_TIMEOUT_PROPERTY = "soTimeout";

    /** Property name for proxy host setting */
    public static final String PROXY_HOST_PROPERTY = "proxyHost";

    /** Property name for proxy port setting */
    public static final String PROXY_PORT_PROPERTY = "proxyPort";

    /** Property name for proxy authentication scheme setting */
    public static final String PROXY_AUTH_SCHEME_PROPERTY = "proxyAuthScheme";

    /** Property name for proxy credentials setting */
    public static final String PROXY_CREDENTIALS_PROPERTY = "proxyCredentials";

    /** Property name for user agent setting */
    public static final String USER_AGENT_PROPERTY = "userAgent";

    /** Property name for robots.txt enabled setting */
    public static final String ROBOTS_TXT_ENABLED_PROPERTY = "robotsTxtEnabled";

    /** Property name for web authentications setting */
    public static final String AUTHENTICATIONS_PROPERTY = "webAuthentications";

    /** Property name for request headers setting */
    public static final String REQUEST_HEADERS_PROPERTY = "requestHeaders";

    /** Property name for redirects enabled setting */
    public static final String REDIRECTS_ENABLED = "redirectsEnabled";

    /** Property name for cookies setting */
    public static final String COOKIES_PROPERTY = "cookies";

    /** Property name for authentication scheme providers setting */
    public static final String AUTH_SCHEME_PROVIDERS_PROPERTY = "authSchemeProviders";

    /** Property name for ignore SSL certificate setting */
    public static final String IGNORE_SSL_CERTIFICATE_PROPERTY = "ignoreSslCertificate";

    /** Property name for default maximum connections per route setting */
    public static final String DEFAULT_MAX_CONNECTION_PER_ROUTE_PROPERTY = "defaultMaxConnectionPerRoute";

    /** Property name for maximum total connections setting */
    public static final String MAX_TOTAL_CONNECTION_PROPERTY = "maxTotalConnection";

    /** Property name for time to live time unit setting */
    public static final String TIME_TO_LIVE_TIME_UNIT_PROPERTY = "timeToLiveTimeUnit";

    /** Property name for time to live setting */
    public static final String TIME_TO_LIVE_PROPERTY = "timeToLive";

    /**
     * Constructs a new HcHttpClient.
     */
    protected HcHttpClient() {
        // Default constructor
    }
}
