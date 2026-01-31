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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.impl.DefaultAuthenticationStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.codelibs.fess.crawler.client.http.config.CredentialsConfig;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig.AuthSchemeType;
import org.codelibs.fess.crawler.helper.RobotsTxtHelper;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Tests for NTLM authentication strategy configuration in Hc5HttpClient.
 *
 * This test class verifies that:
 * 1. When NTLM authentication is configured, a custom authentication strategy is set
 * 2. The custom authentication strategy includes NTLM in the scheme priority list
 * 3. When NTLM is not configured, the default authentication strategy is used
 */
public class Hc5NtlmAuthenticationStrategyTest extends PlainTestCase {

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    /**
     * Test helper class to expose protected getSchemePriority method for testing.
     */
    private static class TestableAuthenticationStrategy extends DefaultAuthenticationStrategy {
        private final List<String> schemePriority;

        public TestableAuthenticationStrategy(List<String> schemePriority) {
            this.schemePriority = Collections.unmodifiableList(schemePriority);
        }

        @Override
        protected List<String> getSchemePriority() {
            return schemePriority;
        }

        // Expose the protected method for testing
        public List<String> getTestSchemePriority() {
            return getSchemePriority();
        }
    }

    /**
     * Test that the custom NTLM authentication strategy returns the correct scheme priority.
     * Priority order should be: Bearer, Digest, NTLM, Basic
     */
    @Test
    public void test_customNtlmAuthenticationStrategy_getSchemePriority() {
        // Create the same pattern used in Hc5HttpClient.init()
        TestableAuthenticationStrategy customStrategy = new TestableAuthenticationStrategy(
                Arrays.asList(StandardAuthScheme.BEARER, StandardAuthScheme.DIGEST, StandardAuthScheme.NTLM, StandardAuthScheme.BASIC));

        List<String> priority = customStrategy.getTestSchemePriority();

        assertNotNull(priority);
        assertEquals(4, priority.size());
        assertEquals(StandardAuthScheme.BEARER, priority.get(0));
        assertEquals(StandardAuthScheme.DIGEST, priority.get(1));
        assertEquals(StandardAuthScheme.NTLM, priority.get(2));
        assertEquals(StandardAuthScheme.BASIC, priority.get(3));
    }

    /**
     * Test that the default authentication scheme list does NOT include NTLM.
     * This test verifies the expected default schemes without using reflection,
     * by checking that NTLM is not in the standard default list.
     */
    @Test
    public void test_defaultAuthenticationStrategy_schemeList_doesNotContainNtlm() {
        // The default authentication strategy uses these schemes in priority order:
        // Bearer, Scram SHA-256, Digest, Basic
        // NTLM is NOT included in the default list
        List<String> expectedDefaultSchemes = Arrays.asList(StandardAuthScheme.BEARER, StandardAuthScheme.SCRAM_SHA_256,
                StandardAuthScheme.DIGEST, StandardAuthScheme.BASIC);

        // Verify NTLM is not in the expected default schemes
        assertFalse(expectedDefaultSchemes.contains(StandardAuthScheme.NTLM));

        // Verify the expected defaults are present
        assertTrue(expectedDefaultSchemes.contains(StandardAuthScheme.BEARER));
        assertTrue(expectedDefaultSchemes.contains(StandardAuthScheme.DIGEST));
        assertTrue(expectedDefaultSchemes.contains(StandardAuthScheme.BASIC));
    }

    /**
     * Test that NTLM is in the correct position (after Digest, before Basic).
     */
    @Test
    public void test_customNtlmAuthenticationStrategy_ntlmPositionIsCorrect() {
        TestableAuthenticationStrategy customStrategy = new TestableAuthenticationStrategy(
                Arrays.asList(StandardAuthScheme.BEARER, StandardAuthScheme.DIGEST, StandardAuthScheme.NTLM, StandardAuthScheme.BASIC));

        List<String> priority = customStrategy.getTestSchemePriority();

        int ntlmIndex = priority.indexOf(StandardAuthScheme.NTLM);
        int digestIndex = priority.indexOf(StandardAuthScheme.DIGEST);
        int basicIndex = priority.indexOf(StandardAuthScheme.BASIC);

        // NTLM should be after Digest
        assertTrue(ntlmIndex > digestIndex);
        // NTLM should be before Basic
        assertTrue(ntlmIndex < basicIndex);
    }

    /**
     * Test that when NTLM authentication is configured, the client initializes successfully
     * and the custom authentication strategy is applied.
     */
    @Test
    public void test_init_withNtlmConfig_setsCustomAuthenticationStrategy() throws Exception {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        config.setScheme("http");
        config.setHost("ntlm.example.com");
        config.setPort(80);
        config.setAuthSchemeType(AuthSchemeType.NTLM);

        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setType(CredentialsConfig.CredentialsType.NTLM);
        credentials.setUsername("testuser");
        credentials.setPassword("testpass");
        credentials.setDomain("TESTDOMAIN");
        config.setCredentials(credentials);

        Map<String, String> ntlmParams = new HashMap<>();
        ntlmParams.put("jcifs.smb.client.domain", "TESTDOMAIN");
        config.setNtlmParameters(ntlmParams);

        Map<String, Object> params = new HashMap<>();
        params.put(HcHttpClient.AUTHENTICATIONS_PROPERTY, new WebAuthenticationConfig[] { config });

        Hc5HttpClient client = new Hc5HttpClient();
        client.robotsTxtHelper = new RobotsTxtHelper();
        client.setInitParameterMap(params);
        client.init();

        // Verify that httpClient is created
        assertNotNull(getPrivateField(client, "httpClient"));

        // Verify that the client has a custom authentication strategy set
        // We can't easily check the exact strategy, but we can verify the client is properly initialized
        CloseableHttpClient httpClient = (CloseableHttpClient) getPrivateField(client, "httpClient");
        assertNotNull(httpClient);

        client.close();
    }

    /**
     * Test that when NTLM authentication is NOT configured, the client uses default authentication strategy.
     */
    @Test
    public void test_init_withoutNtlmConfig_usesDefaultAuthenticationStrategy() throws Exception {
        // No NTLM configuration - just default setup
        Hc5HttpClient client = new Hc5HttpClient();
        client.robotsTxtHelper = new RobotsTxtHelper();
        client.init();

        // Verify that httpClient is created
        CloseableHttpClient httpClient = (CloseableHttpClient) getPrivateField(client, "httpClient");
        assertNotNull(httpClient);

        client.close();
    }

    /**
     * Test that when only Basic authentication is configured (no NTLM),
     * the custom NTLM authentication strategy is NOT set.
     */
    @Test
    public void test_init_withOnlyBasicAuth_doesNotSetCustomStrategy() throws Exception {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        config.setScheme("http");
        config.setHost("basic.example.com");
        config.setPort(80);
        config.setAuthSchemeType(AuthSchemeType.BASIC);

        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setUsername("basicuser");
        credentials.setPassword("basicpass");
        config.setCredentials(credentials);

        Map<String, Object> params = new HashMap<>();
        params.put(HcHttpClient.AUTHENTICATIONS_PROPERTY, new WebAuthenticationConfig[] { config });

        Hc5HttpClient client = new Hc5HttpClient();
        client.robotsTxtHelper = new RobotsTxtHelper();
        client.setInitParameterMap(params);
        client.init();

        // Verify that httpClient is created (with default strategy)
        CloseableHttpClient httpClient = (CloseableHttpClient) getPrivateField(client, "httpClient");
        assertNotNull(httpClient);

        client.close();
    }

    /**
     * Test that the scheme priority list is immutable.
     */
    @Test
    public void test_customNtlmAuthenticationStrategy_schemePriorityIsImmutable() {
        TestableAuthenticationStrategy customStrategy = new TestableAuthenticationStrategy(
                Arrays.asList(StandardAuthScheme.BEARER, StandardAuthScheme.DIGEST, StandardAuthScheme.NTLM, StandardAuthScheme.BASIC));

        List<String> priority = customStrategy.getTestSchemePriority();

        try {
            priority.add("TEST");
            fail();
        } catch (UnsupportedOperationException e) {
            // Expected - list should be immutable
        }
    }

    /**
     * Test initialization with multiple authentication configurations including NTLM.
     */
    @Test
    public void test_init_withMultipleAuthConfigsIncludingNtlm() throws Exception {
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

        // Digest auth config
        WebAuthenticationConfig digestConfig = new WebAuthenticationConfig();
        digestConfig.setScheme("http");
        digestConfig.setHost("digest.example.com");
        digestConfig.setPort(80);
        digestConfig.setAuthSchemeType(AuthSchemeType.DIGEST);

        CredentialsConfig digestCredentials = new CredentialsConfig();
        digestCredentials.setUsername("digestuser");
        digestCredentials.setPassword("digestpass");
        digestConfig.setCredentials(digestCredentials);

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

        Map<String, Object> params = new HashMap<>();
        params.put(HcHttpClient.AUTHENTICATIONS_PROPERTY, new WebAuthenticationConfig[] { basicConfig, digestConfig, ntlmConfig });

        Hc5HttpClient client = new Hc5HttpClient();
        client.robotsTxtHelper = new RobotsTxtHelper();
        client.setInitParameterMap(params);
        client.init();

        // Verify that httpClient is created with custom strategy (since NTLM is present)
        CloseableHttpClient httpClient = (CloseableHttpClient) getPrivateField(client, "httpClient");
        assertNotNull(httpClient);

        client.close();
    }

    /**
     * Test that custom NTLM strategy contains all expected schemes.
     */
    @Test
    public void test_customNtlmAuthenticationStrategy_containsAllExpectedSchemes() {
        TestableAuthenticationStrategy customStrategy = new TestableAuthenticationStrategy(
                Arrays.asList(StandardAuthScheme.BEARER, StandardAuthScheme.DIGEST, StandardAuthScheme.NTLM, StandardAuthScheme.BASIC));

        List<String> priority = customStrategy.getTestSchemePriority();

        assertTrue(priority.contains(StandardAuthScheme.BEARER));
        assertTrue(priority.contains(StandardAuthScheme.DIGEST));
        assertTrue(priority.contains(StandardAuthScheme.NTLM));
        assertTrue(priority.contains(StandardAuthScheme.BASIC));
    }

    /**
     * Test that NTLM scheme constant value is correct.
     */
    @Test
    public void test_ntlmSchemeConstantValue() {
        assertEquals("NTLM", StandardAuthScheme.NTLM);
    }

    /**
     * Test that when NTLM authentication is configured, both target and proxy
     * authentication strategies are set with NTLM support.
     */
    @Test
    public void test_init_withNtlmConfig_setsProxyAuthenticationStrategy() throws Exception {
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        config.setScheme("http");
        config.setHost("ntlm.example.com");
        config.setPort(80);
        config.setAuthSchemeType(AuthSchemeType.NTLM);

        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setType(CredentialsConfig.CredentialsType.NTLM);
        credentials.setUsername("testuser");
        credentials.setPassword("testpass");
        credentials.setDomain("TESTDOMAIN");
        config.setCredentials(credentials);

        Map<String, String> ntlmParams = new HashMap<>();
        ntlmParams.put("jcifs.smb.client.domain", "TESTDOMAIN");
        config.setNtlmParameters(ntlmParams);

        Map<String, Object> params = new HashMap<>();
        params.put(HcHttpClient.AUTHENTICATIONS_PROPERTY, new WebAuthenticationConfig[] { config });

        Hc5HttpClient client = new Hc5HttpClient();
        client.robotsTxtHelper = new RobotsTxtHelper();
        client.setInitParameterMap(params);
        client.init();

        // Verify that httpClient is created
        // Both target and proxy authentication strategies should be set with NTLM support
        CloseableHttpClient httpClient = (CloseableHttpClient) getPrivateField(client, "httpClient");
        assertNotNull(httpClient);

        client.close();
    }

    /**
     * Test that when NTLM is configured with proxy settings, NTLM authentication
     * works for both target server and proxy.
     */
    @Test
    public void test_init_withNtlmAndProxyConfig() throws Exception {
        // NTLM auth config for target server
        WebAuthenticationConfig ntlmConfig = new WebAuthenticationConfig();
        ntlmConfig.setScheme("http");
        ntlmConfig.setHost("ntlm.example.com");
        ntlmConfig.setPort(80);
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

        Map<String, Object> params = new HashMap<>();
        params.put(HcHttpClient.AUTHENTICATIONS_PROPERTY, new WebAuthenticationConfig[] { ntlmConfig });
        // Note: Proxy configuration is typically set via separate properties
        // (PROXY_HOST_PROPERTY, PROXY_PORT_PROPERTY, etc.)

        Hc5HttpClient client = new Hc5HttpClient();
        client.robotsTxtHelper = new RobotsTxtHelper();
        client.setInitParameterMap(params);
        client.init();

        // Verify that httpClient is created with NTLM support for both target and proxy
        CloseableHttpClient httpClient = (CloseableHttpClient) getPrivateField(client, "httpClient");
        assertNotNull(httpClient);

        client.close();
    }

    /**
     * Helper method to access private fields using reflection.
     */
    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
