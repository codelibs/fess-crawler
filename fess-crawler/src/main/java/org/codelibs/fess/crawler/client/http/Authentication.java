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

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;

/**
 * The Authentication interface provides methods to retrieve authentication details
 * required for HTTP client authentication.
 */
public interface Authentication {

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
