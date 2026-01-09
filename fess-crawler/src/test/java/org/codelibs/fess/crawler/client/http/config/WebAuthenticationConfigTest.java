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
package org.codelibs.fess.crawler.client.http.config;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig.AuthSchemeType;
import org.dbflute.utflute.core.PlainTestCase;

public class WebAuthenticationConfigTest extends PlainTestCase {

    public void test_defaultValues() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        assertNull(config.getScheme());
        assertNull(config.getHost());
        assertEquals(-1, config.getPort());
        assertNull(config.getRealm());
        assertNull(config.getCredentials());
        assertEquals(AuthSchemeType.AUTO, config.getAuthSchemeType());
        assertNull(config.getFormParameters());
        assertNull(config.getNtlmParameters());
    }

    public void test_settersAndGetters() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        config.setScheme("https");
        config.setHost("example.com");
        config.setPort(443);
        config.setRealm("TestRealm");

        assertEquals("https", config.getScheme());
        assertEquals("example.com", config.getHost());
        assertEquals(443, config.getPort());
        assertEquals("TestRealm", config.getRealm());
    }

    public void test_authSchemeType() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        config.setAuthSchemeType(AuthSchemeType.BASIC);
        assertEquals(AuthSchemeType.BASIC, config.getAuthSchemeType());

        config.setAuthSchemeType(AuthSchemeType.DIGEST);
        assertEquals(AuthSchemeType.DIGEST, config.getAuthSchemeType());

        config.setAuthSchemeType(AuthSchemeType.NTLM);
        assertEquals(AuthSchemeType.NTLM, config.getAuthSchemeType());

        config.setAuthSchemeType(AuthSchemeType.FORM);
        assertEquals(AuthSchemeType.FORM, config.getAuthSchemeType());
    }

    public void test_credentials() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setUsername("testuser");
        credentials.setPassword("testpass");

        config.setCredentials(credentials);

        assertNotNull(config.getCredentials());
        assertEquals("testuser", config.getCredentials().getUsername());
        assertEquals("testpass", config.getCredentials().getPassword());
    }

    public void test_formParameters() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        Map<String, String> formParams = new HashMap<>();
        formParams.put("login_url", "http://example.com/login");
        formParams.put("login_method", "POST");
        formParams.put("login_parameters", "user=${username}&pass=${password}");

        config.setFormParameters(formParams);

        assertNotNull(config.getFormParameters());
        assertEquals(3, config.getFormParameters().size());
        assertEquals("http://example.com/login", config.getFormParameters().get("login_url"));
        assertEquals("POST", config.getFormParameters().get("login_method"));
    }

    public void test_ntlmParameters() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        Map<String, String> ntlmParams = new HashMap<>();
        ntlmParams.put("jcifs.smb.client.domain", "TESTDOMAIN");
        ntlmParams.put("jcifs.smb.client.SO_SNDBUF", "65535");
        ntlmParams.put("jcifs.smb.client.SO_RCVBUF", "65535");

        config.setNtlmParameters(ntlmParams);

        assertNotNull(config.getNtlmParameters());
        assertEquals(3, config.getNtlmParameters().size());
        assertEquals("TESTDOMAIN", config.getNtlmParameters().get("jcifs.smb.client.domain"));
        assertEquals("65535", config.getNtlmParameters().get("jcifs.smb.client.SO_SNDBUF"));
        assertEquals("65535", config.getNtlmParameters().get("jcifs.smb.client.SO_RCVBUF"));
    }

    public void test_ntlmParameters_null() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        config.setNtlmParameters(null);

        assertNull(config.getNtlmParameters());
    }

    public void test_ntlmParameters_empty() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        Map<String, String> ntlmParams = new HashMap<>();

        config.setNtlmParameters(ntlmParams);

        assertNotNull(config.getNtlmParameters());
        assertTrue(config.getNtlmParameters().isEmpty());
    }

    public void test_ntlmConfiguration_complete() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        // Set up NTLM authentication
        config.setScheme("http");
        config.setHost("intranet.example.com");
        config.setPort(80);
        config.setAuthSchemeType(AuthSchemeType.NTLM);

        // Set credentials
        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setType(CredentialsConfig.CredentialsType.NTLM);
        credentials.setUsername("DOMAIN\\user");
        credentials.setPassword("password");
        credentials.setDomain("DOMAIN");
        credentials.setWorkstation("WORKSTATION");
        config.setCredentials(credentials);

        // Set NTLM parameters
        Map<String, String> ntlmParams = new HashMap<>();
        ntlmParams.put("jcifs.smb.client.domain", "DOMAIN");
        config.setNtlmParameters(ntlmParams);

        // Verify all settings
        assertEquals("http", config.getScheme());
        assertEquals("intranet.example.com", config.getHost());
        assertEquals(80, config.getPort());
        assertEquals(AuthSchemeType.NTLM, config.getAuthSchemeType());
        assertNotNull(config.getCredentials());
        assertEquals(CredentialsConfig.CredentialsType.NTLM, config.getCredentials().getType());
        assertEquals("DOMAIN\\user", config.getCredentials().getUsername());
        assertEquals("DOMAIN", config.getCredentials().getDomain());
        assertEquals("WORKSTATION", config.getCredentials().getWorkstation());
        assertNotNull(config.getNtlmParameters());
        assertEquals("DOMAIN", config.getNtlmParameters().get("jcifs.smb.client.domain"));
    }

    public void test_toString() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        config.setScheme("https");
        config.setHost("example.com");
        config.setPort(443);
        config.setRealm("TestRealm");
        config.setAuthSchemeType(AuthSchemeType.NTLM);

        String result = config.toString();

        assertTrue(result.contains("scheme=https"));
        assertTrue(result.contains("host=example.com"));
        assertTrue(result.contains("port=443"));
        assertTrue(result.contains("realm=TestRealm"));
        assertTrue(result.contains("authSchemeType=NTLM"));
    }

    public void test_authSchemeType_enumValues() {
        // Verify all enum values exist
        AuthSchemeType[] values = AuthSchemeType.values();
        assertEquals(5, values.length);

        assertEquals(AuthSchemeType.AUTO, AuthSchemeType.valueOf("AUTO"));
        assertEquals(AuthSchemeType.BASIC, AuthSchemeType.valueOf("BASIC"));
        assertEquals(AuthSchemeType.DIGEST, AuthSchemeType.valueOf("DIGEST"));
        assertEquals(AuthSchemeType.NTLM, AuthSchemeType.valueOf("NTLM"));
        assertEquals(AuthSchemeType.FORM, AuthSchemeType.valueOf("FORM"));
    }
}
