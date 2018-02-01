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
package org.codelibs.fess.crawler.client.http.impl;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.codelibs.fess.crawler.client.http.Authentication;

/**
 * @author shinsuke
 *
 */
public class AuthenticationImpl implements Authentication {
    private AuthScope authScope;

    private Credentials credentials;

    private AuthScheme authScheme;

    public AuthenticationImpl(final AuthScope authScope,
            final Credentials credentials) {
        this(authScope, credentials, null);
    }

    public AuthenticationImpl(final AuthScope authScope,
            final Credentials credentials, final AuthScheme authScheme) {
        this.authScope = authScope;
        this.credentials = credentials;
        this.authScheme = authScheme;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.client.http.Authentication#getAuthScope()
     */
    @Override
    public AuthScope getAuthScope() {
        return authScope;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.client.http.Authentication#getCredentials()
     */
    @Override
    public Credentials getCredentials() {
        return credentials;
    }

    public void setAuthScope(final AuthScope authScope) {
        this.authScope = authScope;
    }

    public void setCredentials(final Credentials credentials) {
        this.credentials = credentials;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.client.http.Authentication#getAuthScheme()
     */
    @Override
    public AuthScheme getAuthScheme() {
        return authScheme;
    }

    public void setAuthScheme(final AuthScheme authScheme) {
        this.authScheme = authScheme;
    }

}
