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

import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;

/**
 * The Hc5Authentication interface provides methods to retrieve authentication details
 * required for HTTP client authentication using Apache HttpComponents 5.x.
 *
 * <p>This interface is the HC5 equivalent of {@link Authentication}, using HC5 types
 * from the {@code org.apache.hc.client5.http.auth} package instead of HC4 types.</p>
 *
 * @see org.codelibs.fess.crawler.client.http.impl.Hc5AuthenticationImpl
 */
public interface Hc5Authentication {

    /**
     * Retrieves the authentication scope associated with this authentication.
     *
     * @return the {@link AuthScope} object representing the authentication scope.
     */
    AuthScope getAuthScope();

    /**
     * Retrieves the credentials associated with the current authentication.
     *
     * @return the credentials object containing authentication details.
     */
    Credentials getCredentials();

    /**
     * Retrieves the authentication scheme to be used for HTTP requests.
     *
     * @return the authentication scheme
     */
    AuthScheme getAuthScheme();

}
