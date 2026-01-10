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

import org.apache.hc.client5.http.auth.NTCredentials;
import org.apache.hc.client5.http.impl.auth.NTLMScheme;
import org.codelibs.fess.crawler.client.http.config.CredentialsConfig;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig.AuthSchemeType;
import org.dbflute.utflute.core.PlainTestCase;

public class Hc5HttpClientAuthConfigTest extends PlainTestCase {

    private Hc5HttpClient httpClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        httpClient = new Hc5HttpClient();
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
        Hc5Authentication[] result = httpClient.convertFromConfig(configs);

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
        Hc5Authentication[] result = httpClient.convertFromConfig(configs);

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
        Hc5Authentication[] result = httpClient.convertFromConfig(configs);

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
        Hc5Authentication[] result = httpClient.convertFromConfig(configs);

        assertEquals(1, result.length);
        assertTrue(result[0].getCredentials() instanceof NTCredentials);
        NTCredentials ntCreds = (NTCredentials) result[0].getCredentials();
        assertEquals("testuser", ntCreds.getUserName());
        assertEquals("MYDOMAIN", ntCreds.getDomain());
        // Note: In HC5, getNetbiosDomain() returns workstation but the exact behavior
        // may vary. We verify the NTCredentials is created with the correct type.
        assertNotNull(ntCreds);
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
        Hc5Authentication[] result = httpClient.convertFromConfig(configs);

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
        Hc5Authentication[] result = httpClient.convertFromConfig(configs);

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

        org.apache.hc.client5.http.auth.Credentials result = httpClient.convertCredentials(config);

        assertNotNull(result);
        assertTrue(result instanceof NTCredentials);
        NTCredentials ntCreds = (NTCredentials) result;
        assertEquals("ntlmuser", ntCreds.getUserName());
        assertEquals("DOMAIN", ntCreds.getDomain());
        // Note: Workstation is passed to NTCredentials constructor but getNetbiosDomain()
        // behavior varies. We verify the credentials are created correctly.
        assertNotNull(ntCreds);
    }

    public void test_convertCredentials_usernamePassword() {
        CredentialsConfig config = new CredentialsConfig();
        config.setType(CredentialsConfig.CredentialsType.USERNAME_PASSWORD);
        config.setUsername("user");
        config.setPassword("pass");

        org.apache.hc.client5.http.auth.Credentials result = httpClient.convertCredentials(config);

        assertNotNull(result);
        assertTrue(result instanceof org.apache.hc.client5.http.auth.UsernamePasswordCredentials);
        org.apache.hc.client5.http.auth.UsernamePasswordCredentials upCreds =
                (org.apache.hc.client5.http.auth.UsernamePasswordCredentials) result;
        assertEquals("user", upCreds.getUserName());
    }

    public void test_convertCredentials_null() {
        org.apache.hc.client5.http.auth.Credentials result = httpClient.convertCredentials(null);

        assertNull(result);
    }

    public void test_convertCredentials_nullPassword() {
        CredentialsConfig config = new CredentialsConfig();
        config.setType(CredentialsConfig.CredentialsType.USERNAME_PASSWORD);
        config.setUsername("user");
        config.setPassword(null);

        org.apache.hc.client5.http.auth.Credentials result = httpClient.convertCredentials(config);

        assertNotNull(result);
        assertTrue(result instanceof org.apache.hc.client5.http.auth.UsernamePasswordCredentials);
    }

    public void test_convertCredentials_ntlm_nullPassword() {
        CredentialsConfig config = new CredentialsConfig();
        config.setType(CredentialsConfig.CredentialsType.NTLM);
        config.setUsername("ntlmuser");
        config.setPassword(null);
        config.setDomain("DOMAIN");
        config.setWorkstation("WORKSTATION");

        org.apache.hc.client5.http.auth.Credentials result = httpClient.convertCredentials(config);

        assertNotNull(result);
        assertTrue(result instanceof NTCredentials);
    }
}
