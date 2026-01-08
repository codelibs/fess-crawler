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

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.dbflute.utflute.core.PlainTestCase;

public class Hc5AuthenticationTest extends PlainTestCase {

    public void test_constructor_twoArgs() {
        AuthScope authScope = new AuthScope("example.com", 80);
        Credentials credentials = new UsernamePasswordCredentials("user", "password".toCharArray());

        Hc5Authentication auth = new Hc5Authentication(authScope, credentials);

        assertEquals(authScope, auth.getAuthScope());
        assertEquals(credentials, auth.getCredentials());
        assertNull(auth.getAuthScheme());
    }

    public void test_constructor_threeArgs() {
        AuthScope authScope = new AuthScope("example.com", 443);
        Credentials credentials = new UsernamePasswordCredentials("admin", "secret".toCharArray());
        BasicScheme authScheme = new BasicScheme();

        Hc5Authentication auth = new Hc5Authentication(authScope, credentials, authScheme);

        assertEquals(authScope, auth.getAuthScope());
        assertEquals(credentials, auth.getCredentials());
        assertEquals(authScheme, auth.getAuthScheme());
    }

    public void test_getters() {
        AuthScope authScope = new AuthScope("test.example.com", 8080);
        Credentials credentials = new UsernamePasswordCredentials("testuser", "testpass".toCharArray());
        BasicScheme authScheme = new BasicScheme();

        Hc5Authentication auth = new Hc5Authentication(authScope, credentials, authScheme);

        assertSame(authScope, auth.getAuthScope());
        assertSame(credentials, auth.getCredentials());
        assertSame(authScheme, auth.getAuthScheme());
    }

    public void test_setters() {
        AuthScope initialScope = new AuthScope("initial.com", 80);
        Credentials initialCreds = new UsernamePasswordCredentials("initial", "init".toCharArray());

        Hc5Authentication auth = new Hc5Authentication(initialScope, initialCreds);

        // Update values using setters
        AuthScope newScope = new AuthScope("updated.com", 443);
        Credentials newCreds = new UsernamePasswordCredentials("updated", "upd".toCharArray());
        BasicScheme newScheme = new BasicScheme();

        auth.setAuthScope(newScope);
        auth.setCredentials(newCreds);
        auth.setAuthScheme(newScheme);

        assertEquals(newScope, auth.getAuthScope());
        assertEquals(newCreds, auth.getCredentials());
        assertEquals(newScheme, auth.getAuthScheme());
    }

    public void test_getAuthScheme_null() {
        AuthScope authScope = new AuthScope("example.com", 80);
        Credentials credentials = new UsernamePasswordCredentials("user", "password".toCharArray());

        Hc5Authentication auth = new Hc5Authentication(authScope, credentials);

        assertNull(auth.getAuthScheme());
    }

    public void test_constructor_withNullValues() {
        // Constructor accepts null values
        Hc5Authentication auth = new Hc5Authentication(null, null, null);

        assertNull(auth.getAuthScope());
        assertNull(auth.getCredentials());
        assertNull(auth.getAuthScheme());
    }
}
