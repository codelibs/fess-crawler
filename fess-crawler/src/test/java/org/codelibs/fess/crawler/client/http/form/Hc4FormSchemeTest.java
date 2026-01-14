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
package org.codelibs.fess.crawler.client.http.form;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

public class Hc4FormSchemeTest extends PlainTestCase {

    @Test
    public void test_getTokenValue() {
        Hc4FormScheme formScheme = new Hc4FormScheme(Collections.emptyMap());

        String tokenPattern = "name=\"authenticity_token\" +value=\"([^\"]+)\"";
        String content = "<input name=\"authenticity_token\" value=\"abcdefg\">";
        assertEquals("abcdefg", formScheme.getTokenValue(tokenPattern, content));

        tokenPattern = "name=\"authenticity_token\" +value=\"[^\"]+\"";
        content = "<input name=\"authenticity_token\" value=\"abcdefg\">";
        assertNull(formScheme.getTokenValue(tokenPattern, content));

        tokenPattern = "name=\"authenticity_token\" +value=\"([^\"]+)\"";
        content = "<input name=\"authenticity_token\" hoge value=\"abcdefg\">";
        assertNull(formScheme.getTokenValue(tokenPattern, content));
    }

    @Test
    public void test_getSchemeName() {
        Hc4FormScheme formScheme = new Hc4FormScheme(Collections.emptyMap());
        assertEquals("form", formScheme.getSchemeName());
    }

    @Test
    public void test_getParameter() {
        Map<String, String> params = new HashMap<>();
        params.put("token_url", "http://example.com/token");
        params.put("login_url", "http://example.com/login");

        Hc4FormScheme formScheme = new Hc4FormScheme(params);

        assertEquals("http://example.com/token", formScheme.getParameter("token_url"));
        assertEquals("http://example.com/login", formScheme.getParameter("login_url"));
        assertNull(formScheme.getParameter("nonexistent"));
    }

    @Test
    public void test_replaceCredentials_username() {
        Hc4FormScheme formScheme = new Hc4FormScheme(Collections.emptyMap());
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("testuser", "testpass");

        String result = formScheme.replaceCredentials(credentials, "user=${username}");
        assertEquals("user=testuser", result);
    }

    @Test
    public void test_replaceCredentials_password() {
        Hc4FormScheme formScheme = new Hc4FormScheme(Collections.emptyMap());
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("testuser", "testpass");

        String result = formScheme.replaceCredentials(credentials, "pass=${password}");
        assertEquals("pass=testpass", result);
    }

    @Test
    public void test_replaceCredentials_both() {
        Hc4FormScheme formScheme = new Hc4FormScheme(Collections.emptyMap());
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("admin", "secret123");

        String result = formScheme.replaceCredentials(credentials, "username=${username}&password=${password}");
        assertEquals("username=admin&password=secret123", result);
    }

    @Test
    public void test_replaceCredentials_blankValue() {
        Hc4FormScheme formScheme = new Hc4FormScheme(Collections.emptyMap());
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("testuser", "testpass");

        String result = formScheme.replaceCredentials(credentials, "");
        assertEquals("", result);

        result = formScheme.replaceCredentials(credentials, null);
        assertEquals("", result);
    }

    @Test
    public void test_getRealm() {
        Hc4FormScheme formScheme = new Hc4FormScheme(Collections.emptyMap());
        assertNull(formScheme.getRealm());
    }

    @Test
    public void test_isConnectionBased() {
        Hc4FormScheme formScheme = new Hc4FormScheme(Collections.emptyMap());
        assertFalse(formScheme.isConnectionBased());
    }

    @Test
    public void test_isComplete() {
        Hc4FormScheme formScheme = new Hc4FormScheme(Collections.emptyMap());
        assertFalse(formScheme.isComplete());
    }

    @Test
    public void test_toString() {
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        Hc4FormScheme formScheme = new Hc4FormScheme(params);

        String result = formScheme.toString();
        assertTrue(result.contains("FormScheme"));
        assertTrue(result.contains("parameterMap"));
    }
}
