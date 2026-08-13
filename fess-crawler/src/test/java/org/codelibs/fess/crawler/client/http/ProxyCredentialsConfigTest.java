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

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.crawler.client.http.config.CredentialsConfig;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

/**
 * Tests how the HTTP clients read the credentials configured for the proxy.
 *
 * <p>The shape that matters is the library-independent one a crawl config produces:
 * {@link WebAuthenticationConfig}. Both clients used to read the parameter as their own library's
 * {@code Credentials}, which threw {@link ClassCastException} at the call site - failing the crawl
 * rather than the proxy authentication - for every other shape, including the one the other library
 * uses.</p>
 */
public class ProxyCredentialsConfigTest extends PlainTestCase {

    private WebAuthenticationConfig createConfig(final String username, final String password) {
        final WebAuthenticationConfig config = new WebAuthenticationConfig();
        final CredentialsConfig credentials = new CredentialsConfig();
        credentials.setUsername(username);
        credentials.setPassword(password);
        config.setCredentials(credentials);
        return config;
    }

    private Map<String, Object> createInitParamMap(final Object proxyCredentials) {
        final Map<String, Object> initParamMap = new HashMap<>();
        if (proxyCredentials != null) {
            initParamMap.put(HcHttpClient.PROXY_CREDENTIALS_PROPERTY, proxyCredentials);
        }
        return initParamMap;
    }

    private Hc5HttpClient createHc5Client(final Object proxyCredentials) {
        final Hc5HttpClient client = new Hc5HttpClient();
        client.setInitParameterMap(createInitParamMap(proxyCredentials));
        return client;
    }

    private Hc4HttpClient createHc4Client(final Object proxyCredentials) {
        final Hc4HttpClient client = new Hc4HttpClient();
        client.setInitParameterMap(createInitParamMap(proxyCredentials));
        return client;
    }

    @Test
    public void test_hc5_config() {
        final org.apache.hc.client5.http.auth.Credentials credentials =
                createHc5Client(createConfig("proxyuser", "proxypass")).getProxyCredentials();

        assertNotNull(credentials);
        assertEquals("proxyuser", credentials.getUserPrincipal().getName());
        assertEquals("proxypass", new String(credentials.getPassword()));
    }

    @Test
    public void test_hc5_ntlmConfig() {
        final WebAuthenticationConfig config = createConfig("proxyuser", "proxypass");
        config.getCredentials().setType(CredentialsConfig.CredentialsType.NTLM);
        config.getCredentials().setDomain("MYDOMAIN");
        config.getCredentials().setWorkstation("MYWORKSTATION");

        final org.apache.hc.client5.http.auth.Credentials credentials = createHc5Client(config).getProxyCredentials();

        assertTrue(credentials instanceof org.apache.hc.client5.http.auth.NTCredentials);
        assertEquals("MYDOMAIN\\proxyuser", credentials.getUserPrincipal().getName());
    }

    /**
     * Credentials of the client's own library are still accepted.
     */
    @Test
    public void test_hc5_ownCredentials() {
        final org.apache.hc.client5.http.auth.UsernamePasswordCredentials configured =
                new org.apache.hc.client5.http.auth.UsernamePasswordCredentials("proxyuser", "proxypass".toCharArray());

        assertTrue(createHc5Client(configured).getProxyCredentials() == configured);
    }

    /**
     * The HC4 shape earlier callers handed over is converted rather than thrown on.
     */
    @Test
    public void test_hc5_hc4Credentials() {
        final org.apache.hc.client5.http.auth.Credentials credentials =
                createHc5Client(new org.apache.http.auth.UsernamePasswordCredentials("proxyuser", "proxypass")).getProxyCredentials();

        assertNotNull(credentials);
        assertEquals("proxyuser", credentials.getUserPrincipal().getName());
        assertEquals("proxypass", new String(credentials.getPassword()));
    }

    @Test
    public void test_hc5_unsupportedShape() {
        assertNull(createHc5Client("not a credential").getProxyCredentials());
    }

    @Test
    public void test_hc5_noParameter() {
        assertNull(createHc5Client(null).getProxyCredentials());

        final Hc5HttpClient clientWithoutMap = new Hc5HttpClient();
        clientWithoutMap.setInitParameterMap(null);
        assertNull(clientWithoutMap.getProxyCredentials());
    }

    @Test
    public void test_hc4_config() {
        final org.apache.http.auth.Credentials credentials = createHc4Client(createConfig("proxyuser", "proxypass")).getProxyCredentials();

        assertNotNull(credentials);
        assertEquals("proxyuser", credentials.getUserPrincipal().getName());
        assertEquals("proxypass", credentials.getPassword());
    }

    /**
     * Credentials of the client's own library are still accepted.
     */
    @Test
    public void test_hc4_ownCredentials() {
        final org.apache.http.auth.UsernamePasswordCredentials configured =
                new org.apache.http.auth.UsernamePasswordCredentials("proxyuser", "proxypass");

        assertTrue(createHc4Client(configured).getProxyCredentials() == configured);
    }

    @Test
    public void test_hc4_unsupportedShape() {
        assertNull(createHc4Client("not a credential").getProxyCredentials());
    }

    @Test
    public void test_hc4_noParameter() {
        assertNull(createHc4Client(null).getProxyCredentials());
    }
}
