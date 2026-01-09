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
package org.codelibs.fess.crawler.client.http.config;

import java.util.Map;

/**
 * POJO configuration for web authentication that can be converted to
 * either HC4 Hc4Authentication or HC5 Hc5Authentication.
 *
 * <p>This class provides a library-independent way to configure
 * HTTP authentication that works with both Apache HttpComponents 4.x
 * and 5.x clients.</p>
 *
 * <p>Example usage for Basic authentication:</p>
 * <pre>{@code
 * WebAuthenticationConfig config = new WebAuthenticationConfig();
 * config.setHost("example.com");
 * config.setPort(80);
 * config.setCredentials(credentialsConfig);
 * }</pre>
 *
 * <p>Example usage for Form-based authentication:</p>
 * <pre>{@code
 * WebAuthenticationConfig config = new WebAuthenticationConfig();
 * config.setHost("example.com");
 * config.setAuthSchemeType(AuthSchemeType.FORM);
 * config.setFormParameters(Map.of(
 *     "login_url", "http://example.com/login",
 *     "login_method", "POST",
 *     "login_parameters", "user=${username}&pass=${password}"
 * ));
 * config.setCredentials(credentialsConfig);
 * }</pre>
 */
public class WebAuthenticationConfig {

    /**
     * Type of authentication scheme.
     */
    public enum AuthSchemeType {
        /**
         * Let the HTTP client auto-detect the authentication scheme.
         */
        AUTO,
        /**
         * HTTP Basic authentication.
         */
        BASIC,
        /**
         * HTTP Digest authentication.
         */
        DIGEST,
        /**
         * NTLM authentication.
         */
        NTLM,
        /**
         * Form-based authentication.
         */
        FORM
    }

    private String scheme;
    private String host;
    private int port = -1;
    private String realm;

    private CredentialsConfig credentials;

    private AuthSchemeType authSchemeType = AuthSchemeType.AUTO;

    private Map<String, String> formParameters;

    /**
     * Gets the URL scheme (e.g., "http" or "https").
     *
     * @return the scheme
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Sets the URL scheme (e.g., "http" or "https").
     *
     * @param scheme the scheme
     */
    public void setScheme(final String scheme) {
        this.scheme = scheme;
    }

    /**
     * Gets the host name.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the host name.
     *
     * @param host the host
     */
    public void setHost(final String host) {
        this.host = host;
    }

    /**
     * Gets the port number. Returns -1 for any port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port number. Use -1 for any port.
     *
     * @param port the port
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * Gets the authentication realm.
     *
     * @return the realm
     */
    public String getRealm() {
        return realm;
    }

    /**
     * Sets the authentication realm.
     *
     * @param realm the realm
     */
    public void setRealm(final String realm) {
        this.realm = realm;
    }

    /**
     * Gets the credentials configuration.
     *
     * @return the credentials
     */
    public CredentialsConfig getCredentials() {
        return credentials;
    }

    /**
     * Sets the credentials configuration.
     *
     * @param credentials the credentials
     */
    public void setCredentials(final CredentialsConfig credentials) {
        this.credentials = credentials;
    }

    /**
     * Gets the authentication scheme type.
     *
     * @return the authentication scheme type
     */
    public AuthSchemeType getAuthSchemeType() {
        return authSchemeType;
    }

    /**
     * Sets the authentication scheme type.
     *
     * @param authSchemeType the authentication scheme type
     */
    public void setAuthSchemeType(final AuthSchemeType authSchemeType) {
        this.authSchemeType = authSchemeType;
    }

    /**
     * Gets the form authentication parameters.
     * Used when authSchemeType is FORM.
     *
     * <p>Supported parameters:</p>
     * <ul>
     * <li>encoding - Character encoding for request parameters</li>
     * <li>token_url - URL to request the token from</li>
     * <li>token_pattern - Regex pattern to extract the token</li>
     * <li>token_name - Name of the token parameter</li>
     * <li>token_method - HTTP method for token request (GET or POST)</li>
     * <li>token_parameters - Parameters for token request</li>
     * <li>login_url - URL for login request</li>
     * <li>login_method - HTTP method for login request (GET or POST)</li>
     * <li>login_parameters - Parameters for login request</li>
     * </ul>
     *
     * @return the form parameters
     */
    public Map<String, String> getFormParameters() {
        return formParameters;
    }

    /**
     * Sets the form authentication parameters.
     *
     * @param formParameters the form parameters
     */
    public void setFormParameters(final Map<String, String> formParameters) {
        this.formParameters = formParameters;
    }

    /**
     * NTLM-specific parameters (e.g., jcifs.* properties).
     * Used when authSchemeType is NTLM to configure the JCIFS engine.
     *
     * <p>Supported parameters include:</p>
     * <ul>
     * <li>jcifs.smb.client.SO_SNDBUF - TCP send buffer size</li>
     * <li>jcifs.smb.client.SO_RCVBUF - TCP receive buffer size</li>
     * <li>jcifs.smb.client.domain - Default domain</li>
     * <li>And other jcifs.* properties</li>
     * </ul>
     */
    private Map<String, String> ntlmParameters;

    /**
     * Gets the NTLM-specific parameters.
     * Used when authSchemeType is NTLM.
     *
     * @return the NTLM parameters
     */
    public Map<String, String> getNtlmParameters() {
        return ntlmParameters;
    }

    /**
     * Sets the NTLM-specific parameters.
     *
     * @param ntlmParameters the NTLM parameters
     */
    public void setNtlmParameters(final Map<String, String> ntlmParameters) {
        this.ntlmParameters = ntlmParameters;
    }

    @Override
    public String toString() {
        return "WebAuthenticationConfig [scheme=" + scheme + ", host=" + host + ", port=" + port + ", realm=" + realm + ", authSchemeType="
                + authSchemeType + "]";
    }
}
