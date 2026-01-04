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
package org.codelibs.fess.crawler.client.http.impl;

import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.codelibs.fess.crawler.client.http.Hc5Authentication;

/**
 * Implementation of the {@link Hc5Authentication} interface for Apache HttpComponents 5.x.
 * This class provides methods to handle authentication details such as
 * authentication scope, credentials, and authentication scheme.
 *
 * <p>
 * It includes constructors to initialize these details and getter and setter
 * methods to access and modify them.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 * <pre>
 * {@code
 * AuthScope authScope = new AuthScope("example.com", 80);
 * Credentials credentials = new UsernamePasswordCredentials("user", "password".toCharArray());
 * Hc5AuthenticationImpl auth = new Hc5AuthenticationImpl(authScope, credentials);
 * }
 * </pre>
 *
 * @see org.codelibs.fess.crawler.client.http.Hc5Authentication
 */
public class Hc5AuthenticationImpl implements Hc5Authentication {
    private AuthScope authScope;

    private Credentials credentials;

    private AuthScheme authScheme;

    /**
     * Initializes the Hc5AuthenticationImpl with the provided AuthScope and Credentials,
     * and sets the AuthScheme to null.
     * @param authScope The authentication scope.
     * @param credentials The credentials.
     */
    public Hc5AuthenticationImpl(final AuthScope authScope, final Credentials credentials) {
        this(authScope, credentials, null);
    }

    /**
     * Initializes the Hc5AuthenticationImpl with the provided AuthScope, Credentials, and AuthScheme.
     * @param authScope The authentication scope.
     * @param credentials The credentials.
     * @param authScheme The authentication scheme.
     */
    public Hc5AuthenticationImpl(final AuthScope authScope, final Credentials credentials, final AuthScheme authScheme) {
        this.authScope = authScope;
        this.credentials = credentials;
        this.authScheme = authScheme;
    }

    @Override
    public AuthScope getAuthScope() {
        return authScope;
    }

    @Override
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * Sets the authentication scope.
     *
     * @param authScope the authentication scope to set
     */
    public void setAuthScope(final AuthScope authScope) {
        this.authScope = authScope;
    }

    /**
     * Sets the credentials.
     * @param credentials The credentials to set.
     */
    public void setCredentials(final Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public AuthScheme getAuthScheme() {
        return authScheme;
    }

    /**
     * Sets the authentication scheme.
     * @param authScheme The authentication scheme to set.
     */
    public void setAuthScheme(final AuthScheme authScheme) {
        this.authScheme = authScheme;
    }

}
