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

import org.codelibs.fess.crawler.client.http.config.CredentialsConfig.CredentialsType;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig.AuthSchemeType;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for WebAuthenticationConfig.
 * Tests all getters, setters, enum values, and toString functionality.
 */
public class WebAuthenticationConfigTest extends PlainTestCase {

    /**
     * Test default values on new instance
     */
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

    /**
     * Test AuthSchemeType enum values
     */
    public void test_authSchemeTypeEnum() {
        assertEquals(5, AuthSchemeType.values().length);

        assertEquals(AuthSchemeType.AUTO, AuthSchemeType.valueOf("AUTO"));
        assertEquals(AuthSchemeType.BASIC, AuthSchemeType.valueOf("BASIC"));
        assertEquals(AuthSchemeType.DIGEST, AuthSchemeType.valueOf("DIGEST"));
        assertEquals(AuthSchemeType.NTLM, AuthSchemeType.valueOf("NTLM"));
        assertEquals(AuthSchemeType.FORM, AuthSchemeType.valueOf("FORM"));
    }

    /**
     * Test scheme getter and setter
     */
    public void test_scheme() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        config.setScheme("http");
        assertEquals("http", config.getScheme());

        config.setScheme("https");
        assertEquals("https", config.getScheme());

        config.setScheme(null);
        assertNull(config.getScheme());
    }

    /**
     * Test host getter and setter
     */
    public void test_host() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        config.setHost("example.com");
        assertEquals("example.com", config.getHost());

        config.setHost("192.168.1.1");
        assertEquals("192.168.1.1", config.getHost());

        config.setHost("localhost");
        assertEquals("localhost", config.getHost());

        config.setHost(null);
        assertNull(config.getHost());
    }

    /**
     * Test port getter and setter
     */
    public void test_port() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        assertEquals(-1, config.getPort()); // default

        config.setPort(80);
        assertEquals(80, config.getPort());

        config.setPort(443);
        assertEquals(443, config.getPort());

        config.setPort(8080);
        assertEquals(8080, config.getPort());

        config.setPort(-1); // any port
        assertEquals(-1, config.getPort());

        config.setPort(0);
        assertEquals(0, config.getPort());
    }

    /**
     * Test realm getter and setter
     */
    public void test_realm() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        config.setRealm("Secure Area");
        assertEquals("Secure Area", config.getRealm());

        config.setRealm("Protected Resource");
        assertEquals("Protected Resource", config.getRealm());

        config.setRealm(null);
        assertNull(config.getRealm());

        config.setRealm("");
        assertEquals("", config.getRealm());
    }

    /**
     * Test credentials getter and setter
     */
    public void test_credentials() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        assertNull(config.getCredentials());

        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setUsername("user");
        credentials.setPassword("pass");

        config.setCredentials(credentials);
        assertSame(credentials, config.getCredentials());
        assertEquals("user", config.getCredentials().getUsername());

        config.setCredentials(null);
        assertNull(config.getCredentials());
    }

    /**
     * Test authSchemeType getter and setter
     */
    public void test_authSchemeType() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        assertEquals(AuthSchemeType.AUTO, config.getAuthSchemeType());

        config.setAuthSchemeType(AuthSchemeType.BASIC);
        assertEquals(AuthSchemeType.BASIC, config.getAuthSchemeType());

        config.setAuthSchemeType(AuthSchemeType.DIGEST);
        assertEquals(AuthSchemeType.DIGEST, config.getAuthSchemeType());

        config.setAuthSchemeType(AuthSchemeType.NTLM);
        assertEquals(AuthSchemeType.NTLM, config.getAuthSchemeType());

        config.setAuthSchemeType(AuthSchemeType.FORM);
        assertEquals(AuthSchemeType.FORM, config.getAuthSchemeType());

        config.setAuthSchemeType(null);
        assertNull(config.getAuthSchemeType());
    }

    /**
     * Test formParameters getter and setter
     */
    public void test_formParameters() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        assertNull(config.getFormParameters());

        Map<String, String> params = new HashMap<>();
        params.put("login_url", "http://example.com/login");
        params.put("login_method", "POST");
        params.put("login_parameters", "user=${username}&pass=${password}");

        config.setFormParameters(params);
        assertEquals(params, config.getFormParameters());
        assertEquals("http://example.com/login", config.getFormParameters().get("login_url"));

        config.setFormParameters(null);
        assertNull(config.getFormParameters());
    }

    /**
     * Test ntlmParameters getter and setter
     */
    public void test_ntlmParameters() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        assertNull(config.getNtlmParameters());

        Map<String, String> params = new HashMap<>();
        params.put("jcifs.smb.client.domain", "MYDOMAIN");
        params.put("jcifs.smb.client.SO_SNDBUF", "65536");

        config.setNtlmParameters(params);
        assertEquals(params, config.getNtlmParameters());
        assertEquals("MYDOMAIN", config.getNtlmParameters().get("jcifs.smb.client.domain"));

        config.setNtlmParameters(null);
        assertNull(config.getNtlmParameters());
    }

    /**
     * Test basic authentication configuration
     */
    public void test_basicAuthConfiguration() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        config.setScheme("https");
        config.setHost("secure.example.com");
        config.setPort(443);
        config.setRealm("Secure Zone");
        config.setAuthSchemeType(AuthSchemeType.BASIC);

        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setUsername("admin");
        credentials.setPassword("secret");
        config.setCredentials(credentials);

        assertEquals("https", config.getScheme());
        assertEquals("secure.example.com", config.getHost());
        assertEquals(443, config.getPort());
        assertEquals("Secure Zone", config.getRealm());
        assertEquals(AuthSchemeType.BASIC, config.getAuthSchemeType());
        assertEquals("admin", config.getCredentials().getUsername());
    }

    /**
     * Test form authentication configuration
     */
    public void test_formAuthConfiguration() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        config.setHost("webapp.example.com");
        config.setAuthSchemeType(AuthSchemeType.FORM);

        Map<String, String> formParams = new HashMap<>();
        formParams.put("encoding", "UTF-8");
        formParams.put("login_url", "http://webapp.example.com/login");
        formParams.put("login_method", "POST");
        formParams.put("login_parameters", "username=${username}&password=${password}");
        config.setFormParameters(formParams);

        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setUsername("formuser");
        credentials.setPassword("formpass");
        config.setCredentials(credentials);

        assertEquals(AuthSchemeType.FORM, config.getAuthSchemeType());
        assertEquals("http://webapp.example.com/login", config.getFormParameters().get("login_url"));
        assertEquals("POST", config.getFormParameters().get("login_method"));
    }

    /**
     * Test NTLM authentication configuration
     */
    public void test_ntlmAuthConfiguration() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        config.setHost("intranet.corp.local");
        config.setPort(80);
        config.setAuthSchemeType(AuthSchemeType.NTLM);

        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setType(CredentialsType.NTLM);
        credentials.setUsername("ntlmuser");
        credentials.setPassword("ntlmpass");
        credentials.setDomain("CORP");
        credentials.setWorkstation("WORKSTATION01");
        config.setCredentials(credentials);

        Map<String, String> ntlmParams = new HashMap<>();
        ntlmParams.put("jcifs.smb.client.domain", "CORP");
        config.setNtlmParameters(ntlmParams);

        assertEquals(AuthSchemeType.NTLM, config.getAuthSchemeType());
        assertEquals(CredentialsType.NTLM, config.getCredentials().getType());
        assertEquals("CORP", config.getCredentials().getDomain());
        assertEquals("CORP", config.getNtlmParameters().get("jcifs.smb.client.domain"));
    }

    /**
     * Test toString method
     */
    public void test_toString() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        config.setScheme("https");
        config.setHost("example.com");
        config.setPort(443);
        config.setRealm("Test Realm");
        config.setAuthSchemeType(AuthSchemeType.BASIC);

        String result = config.toString();

        assertNotNull(result);
        assertTrue(result.contains("WebAuthenticationConfig"));
        assertTrue(result.contains("scheme=https"));
        assertTrue(result.contains("host=example.com"));
        assertTrue(result.contains("port=443"));
        assertTrue(result.contains("realm=Test Realm"));
        assertTrue(result.contains("authSchemeType=BASIC"));
    }

    /**
     * Test toString with null values
     */
    public void test_toString_withNullValues() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        String result = config.toString();

        assertNotNull(result);
        assertTrue(result.contains("WebAuthenticationConfig"));
        assertTrue(result.contains("scheme=null"));
        assertTrue(result.contains("host=null"));
        assertTrue(result.contains("port=-1"));
    }

    /**
     * Test toString excludes sensitive credentials info
     */
    public void test_toString_excludesCredentialsDetails() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        config.setHost("example.com");

        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setUsername("secretuser");
        credentials.setPassword("supersecretpassword");
        config.setCredentials(credentials);

        String result = config.toString();

        // toString should not include full credential details
        assertFalse(result.contains("supersecretpassword"));
    }

    /**
     * Test multiple instances are independent
     */
    public void test_multipleInstances() {
        WebAuthenticationConfig config1 = new WebAuthenticationConfig();
        WebAuthenticationConfig config2 = new WebAuthenticationConfig();

        config1.setHost("host1.com");
        config1.setPort(80);
        config1.setAuthSchemeType(AuthSchemeType.BASIC);

        config2.setHost("host2.com");
        config2.setPort(443);
        config2.setAuthSchemeType(AuthSchemeType.DIGEST);

        assertEquals("host1.com", config1.getHost());
        assertEquals(80, config1.getPort());
        assertEquals(AuthSchemeType.BASIC, config1.getAuthSchemeType());

        assertEquals("host2.com", config2.getHost());
        assertEquals(443, config2.getPort());
        assertEquals(AuthSchemeType.DIGEST, config2.getAuthSchemeType());
    }

    /**
     * Test form parameters with token authentication
     */
    public void test_formParametersWithToken() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        config.setAuthSchemeType(AuthSchemeType.FORM);

        Map<String, String> formParams = new HashMap<>();
        formParams.put("encoding", "UTF-8");
        formParams.put("token_url", "http://example.com/csrf");
        formParams.put("token_pattern", "<input.*?name=\"_csrf\".*?value=\"([^\"]+)\"");
        formParams.put("token_name", "_csrf");
        formParams.put("token_method", "GET");
        formParams.put("login_url", "http://example.com/login");
        formParams.put("login_method", "POST");
        formParams.put("login_parameters", "username=${username}&password=${password}&_csrf=${_csrf}");
        config.setFormParameters(formParams);

        assertEquals(8, config.getFormParameters().size());
        assertEquals("http://example.com/csrf", config.getFormParameters().get("token_url"));
        assertEquals("_csrf", config.getFormParameters().get("token_name"));
    }

    /**
     * Test empty map for parameters
     */
    public void test_emptyMaps() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        config.setFormParameters(new HashMap<>());
        assertNotNull(config.getFormParameters());
        assertTrue(config.getFormParameters().isEmpty());

        config.setNtlmParameters(new HashMap<>());
        assertNotNull(config.getNtlmParameters());
        assertTrue(config.getNtlmParameters().isEmpty());
    }

    /**
     * Test IPv6 host
     */
    public void test_ipv6Host() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        config.setHost("::1");
        assertEquals("::1", config.getHost());

        config.setHost("[2001:db8::1]");
        assertEquals("[2001:db8::1]", config.getHost());
    }

    /**
     * Test high port numbers
     */
    public void test_highPortNumbers() {
        WebAuthenticationConfig config = new WebAuthenticationConfig();

        config.setPort(65535);
        assertEquals(65535, config.getPort());

        config.setPort(49152);
        assertEquals(49152, config.getPort());
    }
}
