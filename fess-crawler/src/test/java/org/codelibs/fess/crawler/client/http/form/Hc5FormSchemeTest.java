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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

public class Hc5FormSchemeTest extends PlainTestCase {

    @Test
    public void test_getTokenValue() {
        Hc5FormScheme formScheme = new Hc5FormScheme(Collections.emptyMap());

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
    public void test_getName() {
        Hc5FormScheme formScheme = new Hc5FormScheme(Collections.emptyMap());
        assertEquals("form", formScheme.getName());
    }

    @Test
    public void test_getParameter() {
        Map<String, String> params = new HashMap<>();
        params.put("token_url", "http://example.com/token");
        params.put("login_url", "http://example.com/login");

        Hc5FormScheme formScheme = new Hc5FormScheme(params);

        assertEquals("http://example.com/token", formScheme.getParameter("token_url"));
        assertEquals("http://example.com/login", formScheme.getParameter("login_url"));
        assertNull(formScheme.getParameter("nonexistent"));
    }

    @Test
    public void test_replaceCredentials_username() {
        Hc5FormScheme formScheme = new Hc5FormScheme(Collections.emptyMap());
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("testuser", "testpass".toCharArray());

        String result = formScheme.replaceCredentials(credentials, "user=${username}");
        assertEquals("user=testuser", result);
    }

    @Test
    public void test_replaceCredentials_password() {
        Hc5FormScheme formScheme = new Hc5FormScheme(Collections.emptyMap());
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("testuser", "testpass".toCharArray());

        String result = formScheme.replaceCredentials(credentials, "pass=${password}");
        assertEquals("pass=testpass", result);
    }

    @Test
    public void test_replaceCredentials_both() {
        Hc5FormScheme formScheme = new Hc5FormScheme(Collections.emptyMap());
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("admin", "secret123".toCharArray());

        String result = formScheme.replaceCredentials(credentials, "username=${username}&password=${password}");
        assertEquals("username=admin&password=secret123", result);
    }

    @Test
    public void test_replaceCredentials_blankValue() {
        Hc5FormScheme formScheme = new Hc5FormScheme(Collections.emptyMap());
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("testuser", "testpass".toCharArray());

        String result = formScheme.replaceCredentials(credentials, "");
        assertEquals("", result);

        result = formScheme.replaceCredentials(credentials, null);
        assertEquals("", result);
    }

    @Test
    public void test_getRealm() {
        Hc5FormScheme formScheme = new Hc5FormScheme(Collections.emptyMap());
        assertNull(formScheme.getRealm());
    }

    @Test
    public void test_isConnectionBased() {
        Hc5FormScheme formScheme = new Hc5FormScheme(Collections.emptyMap());
        assertFalse(formScheme.isConnectionBased());
    }

    @Test
    public void test_isChallengeComplete() {
        Hc5FormScheme formScheme = new Hc5FormScheme(Collections.emptyMap());
        assertFalse(formScheme.isChallengeComplete());
    }

    @Test
    public void test_getPrincipal() {
        Hc5FormScheme formScheme = new Hc5FormScheme(Collections.emptyMap());
        assertNull(formScheme.getPrincipal());
    }

    @Test
    public void test_toString() {
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        Hc5FormScheme formScheme = new Hc5FormScheme(params);

        String result = formScheme.toString();
        assertTrue(result.contains("Hc5FormScheme"));
        assertTrue(result.contains("parameterMap"));
    }

    @Test
    public void test_isLoginFormReturned() {
        Hc5FormScheme formScheme = new Hc5FormScheme(Collections.emptyMap());

        assertTrue(formScheme.isLoginFormReturned(new StringEntity("<form><input type=\"password\" name=\"p\"></form>")));
        assertTrue(formScheme.isLoginFormReturned(new StringEntity("<INPUT name=p\n  TYPE = 'Password'>")));
        assertTrue(formScheme.isLoginFormReturned(new StringEntity("<input name=\"p\" type=password>")));
        assertFalse(formScheme.isLoginFormReturned(new StringEntity("<input type=\"text\" name=\"password\">")));
        assertFalse(formScheme.isLoginFormReturned(new StringEntity("<p>Welcome. type=\"password\" is not a field</p>")));
        assertFalse(formScheme.isLoginFormReturned(new StringEntity("")));
        assertFalse(formScheme.isLoginFormReturned(null));
    }

    @Test
    public void test_isLoginFormReturned_checks_only_the_head() {
        Hc5FormScheme formScheme = new Hc5FormScheme(Collections.emptyMap());
        final String padding = "x".repeat(Hc5FormScheme.LOGIN_RESPONSE_MAX_BYTES);

        assertFalse(formScheme.isLoginFormReturned(new StringEntity(padding + "<input type=\"password\">")));
    }

    @Test
    public void test_authenticate_warns_when_the_login_form_is_returned() {
        final List<String> warns = authenticate(200, "<form><input type=\"password\" name=\"pw\"></form>");

        assertEquals(List.of("http://example.com/login"), warns);
    }

    @Test
    public void test_authenticate_warning_does_not_have_the_user_info_of_the_login_url() {
        final Map<String, String> params = new HashMap<>();
        params.put("login_url", "http://user:secret@example.com/login");
        params.put("login_method", "POST");
        params.put("login_parameters", "u=${username}&p=${password}");

        final List<String> warns = authenticate(params, 200, "<input type=\"password\">");

        assertEquals(List.of("http://example.com/login"), warns);
    }

    @Test
    public void test_authenticate_does_not_warn_when_the_login_succeeded() {
        assertEquals(0, authenticate(200, "<h1>Welcome</h1>").size());
        assertEquals(0, authenticate(302, "").size());
    }

    @Test
    public void test_authenticate_does_not_report_the_login_form_for_an_error_status() {
        // the http status of 400 or more is reported by its own warning
        assertEquals(0, authenticate(401, "<input type=\"password\">").size());
    }

    private List<String> authenticate(final int status, final String body) {
        final Map<String, String> params = new HashMap<>();
        params.put("login_url", "http://example.com/login");
        params.put("login_method", "POST");
        params.put("login_parameters", "u=${username}&p=${password}");
        return authenticate(params, status, body);
    }

    private List<String> authenticate(final Map<String, String> params, final int status, final String body) {
        final List<String> warns = new ArrayList<>();
        final Hc5FormScheme formScheme = new Hc5FormScheme(params) {
            @Override
            protected void warnLoginFormReturned(final String loginUrl) {
                warns.add(loginUrl);
            }
        };
        final BiConsumer<ClassicHttpRequest, BiConsumer<ClassicHttpResponse, HttpEntity>> executor = (request, consumer) -> {
            final ClassicHttpResponse response = new BasicClassicHttpResponse(status);
            final HttpEntity entity = new StringEntity(body);
            response.setEntity(entity);
            consumer.accept(response, entity);
        };
        formScheme.authenticate(new UsernamePasswordCredentials("testuser", "wrong".toCharArray()), executor);
        return warns;
    }
}
