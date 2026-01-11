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

import org.apache.http.auth.NTCredentials;
import org.apache.http.impl.auth.NTLMScheme;
import org.codelibs.fess.crawler.client.http.config.CredentialsConfig;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig.AuthSchemeType;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

public class Hc4HttpClientAuthConfigTest extends PlainTestCase {

    private Hc4HttpClient httpClient;

    @Override
    @BeforeEach
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        httpClient = new Hc4HttpClient();
    }

    public void test_convertFromConfig_ntlm_withParameters() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        config.setScheme("http");
        config.setHost("intranet.example.com");
        config.setPort(80);
        config.setAuthSchemeType(AuthSchemeType.NTLM);

        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setType(CredentialsConfig.CredentialsType.NTLM);
        credentials.setUsername("testuser");
        credentials.setPassword("testpass");
        credentials.setDomain("TESTDOMAIN");
        credentials.setWorkstation("TESTWORKSTATION");
        config.setCredentials(credentials);

        Map<String, String> ntlmParams = new HashMap<>();
        ntlmParams.put("jcifs.smb.client.domain", "TESTDOMAIN");
        ntlmParams.put("jcifs.smb.client.SO_SNDBUF", "65535");
        config.setNtlmParameters(ntlmParams);

        WebAuthenticationConfig[] configs = new WebAuthenticationConfig[] { config };
        Hc4Authentication[] result = httpClient.convertFromConfig(configs);

        assertEquals(1, result.length);
        assertNotNull(result[0].getAuthScope());
        assertEquals("intranet.example.com", result[0].getAuthScope().getHost());
        assertEquals(80, result[0].getAuthScope().getPort());
        assertNotNull(result[0].getCredentials());
        assertTrue(result[0].getCredentials() instanceof NTCredentials);
        assertNotNull(result[0].getAuthScheme());
        assertTrue(result[0].getAuthScheme() instanceof NTLMScheme);
    }

    public void test_convertFromConfig_ntlm_withoutParameters() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        config.setScheme("http");
        config.setHost("intranet.example.com");
        config.setPort(80);
        config.setAuthSchemeType(AuthSchemeType.NTLM);

        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setType(CredentialsConfig.CredentialsType.NTLM);
        credentials.setUsername("testuser");
        credentials.setPassword("testpass");
        credentials.setDomain("TESTDOMAIN");
        credentials.setWorkstation("TESTWORKSTATION");
        config.setCredentials(credentials);

        // No NTLM parameters set (null)

        WebAuthenticationConfig[] configs = new WebAuthenticationConfig[] { config };
        Hc4Authentication[] result = httpClient.convertFromConfig(configs);

        assertEquals(1, result.length);
        assertNotNull(result[0].getAuthScheme());
        assertTrue(result[0].getAuthScheme() instanceof NTLMScheme);
    }

    public void test_convertFromConfig_ntlm_emptyParameters() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        config.setScheme("http");
        config.setHost("intranet.example.com");
        config.setPort(80);
        config.setAuthSchemeType(AuthSchemeType.NTLM);

        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setType(CredentialsConfig.CredentialsType.NTLM);
        credentials.setUsername("testuser");
        credentials.setPassword("testpass");
        config.setCredentials(credentials);

        // Empty NTLM parameters
        config.setNtlmParameters(new HashMap<>());

        WebAuthenticationConfig[] configs = new WebAuthenticationConfig[] { config };
        Hc4Authentication[] result = httpClient.convertFromConfig(configs);

        assertEquals(1, result.length);
        assertNotNull(result[0].getAuthScheme());
        assertTrue(result[0].getAuthScheme() instanceof NTLMScheme);
    }

    public void test_convertFromConfig_ntlm_credentials() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        config.setScheme("http");
        config.setHost("example.com");
        config.setPort(80);
        config.setAuthSchemeType(AuthSchemeType.NTLM);

        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setType(CredentialsConfig.CredentialsType.NTLM);
        credentials.setUsername("testuser");
        credentials.setPassword("testpass");
        credentials.setDomain("MYDOMAIN");
        credentials.setWorkstation("MYWORKSTATION");
        config.setCredentials(credentials);

        WebAuthenticationConfig[] configs = new WebAuthenticationConfig[] { config };
        Hc4Authentication[] result = httpClient.convertFromConfig(configs);

        assertEquals(1, result.length);
        assertTrue(result[0].getCredentials() instanceof NTCredentials);
        NTCredentials ntCreds = (NTCredentials) result[0].getCredentials();
        assertEquals("testuser", ntCreds.getUserName());
        assertEquals("testpass", ntCreds.getPassword());
        assertEquals("MYDOMAIN", ntCreds.getDomain());
        assertEquals("MYWORKSTATION", ntCreds.getWorkstation());
    }

    public void test_convertFromConfig_multipleConfigs_withNtlm() {
        // Basic auth config
        WebAuthenticationConfig basicConfig = new WebAuthenticationConfig();
        basicConfig.setScheme("http");
        basicConfig.setHost("basic.example.com");
        basicConfig.setPort(80);
        basicConfig.setAuthSchemeType(AuthSchemeType.BASIC);

        CredentialsConfig basicCredentials = new CredentialsConfig();
        basicCredentials.setUsername("basicuser");
        basicCredentials.setPassword("basicpass");
        basicConfig.setCredentials(basicCredentials);

        // NTLM auth config
        WebAuthenticationConfig ntlmConfig = new WebAuthenticationConfig();
        ntlmConfig.setScheme("http");
        ntlmConfig.setHost("ntlm.example.com");
        ntlmConfig.setPort(8080);
        ntlmConfig.setAuthSchemeType(AuthSchemeType.NTLM);

        CredentialsConfig ntlmCredentials = new CredentialsConfig();
        ntlmCredentials.setType(CredentialsConfig.CredentialsType.NTLM);
        ntlmCredentials.setUsername("ntlmuser");
        ntlmCredentials.setPassword("ntlmpass");
        ntlmCredentials.setDomain("NTLMDOMAIN");
        ntlmConfig.setCredentials(ntlmCredentials);

        Map<String, String> ntlmParams = new HashMap<>();
        ntlmParams.put("jcifs.smb.client.domain", "NTLMDOMAIN");
        ntlmConfig.setNtlmParameters(ntlmParams);

        WebAuthenticationConfig[] configs = new WebAuthenticationConfig[] { basicConfig, ntlmConfig };
        Hc4Authentication[] result = httpClient.convertFromConfig(configs);

        assertEquals(2, result.length);

        // First is basic (no auth scheme)
        assertEquals("basic.example.com", result[0].getAuthScope().getHost());
        assertNull(result[0].getAuthScheme());

        // Second is NTLM
        assertEquals("ntlm.example.com", result[1].getAuthScope().getHost());
        assertNotNull(result[1].getAuthScheme());
        assertTrue(result[1].getAuthScheme() instanceof NTLMScheme);
    }

    public void test_convertFromConfig_autoScheme_noNtlmScheme() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        config.setScheme("http");
        config.setHost("example.com");
        config.setPort(80);
        config.setAuthSchemeType(AuthSchemeType.AUTO);

        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setUsername("user");
        credentials.setPassword("pass");
        config.setCredentials(credentials);

        WebAuthenticationConfig[] configs = new WebAuthenticationConfig[] { config };
        Hc4Authentication[] result = httpClient.convertFromConfig(configs);

        assertEquals(1, result.length);
        assertNull(result[0].getAuthScheme());
    }

    public void test_convertCredentials_ntlm() {
        CredentialsConfig config = new CredentialsConfig();
        config.setType(CredentialsConfig.CredentialsType.NTLM);
        config.setUsername("ntlmuser");
        config.setPassword("ntlmpass");
        config.setDomain("DOMAIN");
        config.setWorkstation("WORKSTATION");

        org.apache.http.auth.Credentials result = httpClient.convertCredentials(config);

        assertNotNull(result);
        assertTrue(result instanceof NTCredentials);
        NTCredentials ntCreds = (NTCredentials) result;
        assertEquals("ntlmuser", ntCreds.getUserName());
        assertEquals("ntlmpass", ntCreds.getPassword());
        assertEquals("DOMAIN", ntCreds.getDomain());
        assertEquals("WORKSTATION", ntCreds.getWorkstation());
    }

    public void test_convertCredentials_usernamePassword() {
        CredentialsConfig config = new CredentialsConfig();
        config.setType(CredentialsConfig.CredentialsType.USERNAME_PASSWORD);
        config.setUsername("user");
        config.setPassword("pass");

        org.apache.http.auth.Credentials result = httpClient.convertCredentials(config);

        assertNotNull(result);
        assertTrue(result instanceof org.apache.http.auth.UsernamePasswordCredentials);
        org.apache.http.auth.UsernamePasswordCredentials upCreds = (org.apache.http.auth.UsernamePasswordCredentials) result;
        assertEquals("user", upCreds.getUserName());
        assertEquals("pass", upCreds.getPassword());
    }

    public void test_convertCredentials_null() {
        org.apache.http.auth.Credentials result = httpClient.convertCredentials(null);

        assertNull(result);
    }
}
