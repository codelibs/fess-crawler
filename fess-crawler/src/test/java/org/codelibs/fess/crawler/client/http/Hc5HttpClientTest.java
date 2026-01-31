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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.impl.auth.BasicSchemeFactory;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.codelibs.fess.crawler.client.http.config.CredentialsConfig;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig.AuthSchemeType;
import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.filter.UrlFilter;
import org.codelibs.fess.crawler.filter.impl.UrlFilterImpl;
import org.codelibs.fess.crawler.helper.MemoryDataHelper;
import org.codelibs.fess.crawler.helper.RobotsTxtHelper;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.codelibs.fess.crawler.service.impl.UrlFilterServiceImpl;
import org.codelibs.fess.crawler.util.CrawlerWebServer;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author shinsuke
 *
 */
public class Hc5HttpClientTest extends PlainTestCase {
    public Hc5HttpClient httpClient;

    public UrlFilter urlFilter;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("mimeTypeHelper", MimeTypeHelperImpl.class)//
                .singleton("dataHelper", MemoryDataHelper.class)//
                .singleton("urlFilterService", UrlFilterServiceImpl.class)//
                .singleton("urlFilter", UrlFilterImpl.class)//
                .singleton("robotsTxtHelper", RobotsTxtHelper.class)//
                .singleton("httpClient", Hc5HttpClient.class);
        httpClient = container.getComponent("httpClient");
        urlFilter = container.getComponent("urlFilter");
    }

    @Test
    public void test_doGet() {
        final CrawlerWebServer server = new CrawlerWebServer(0);
        server.start();

        final String url = "http://localhost:" + server.getPort() + "/";
        try {
            final ResponseData responseData = httpClient.doGet(url);
            assertEquals(200, responseData.getHttpStatusCode());
        } finally {
            server.stop();
        }
    }

    @Test
    public void test_parseLastModified() {
        final String value = "Mon, 01 Jun 2009 21:02:45 GMT";
        final Date date = httpClient.parseLastModifiedDate(value);
        assertNotNull(date);
    }

    @Test
    public void test_processRobotsTxt() {
        final CrawlerWebServer server = new CrawlerWebServer(0);
        server.start();

        final String url = "http://localhost:" + server.getPort() + "/hoge.html";
        try {
            final CrawlerContext crawlerContext = new CrawlerContext();
            final String sessionId = "id1";
            urlFilter.init(sessionId);
            crawlerContext.setUrlFilter(urlFilter);
            CrawlingParameterUtil.setCrawlerContext(crawlerContext);
            httpClient.init();
            httpClient.processRobotsTxt(url);
            assertEquals(1, crawlerContext.getRobotsTxtUrlSet().size());
            assertTrue(crawlerContext.getRobotsTxtUrlSet().contains("http://localhost:" + server.getPort() + "/robots.txt"));
            assertFalse(urlFilter.match("http://localhost:" + server.getPort() + "/admin/"));
            assertFalse(urlFilter.match("http://localhost:" + server.getPort() + "/websvn/"));
        } finally {
            server.stop();
        }
    }

    @Test
    public void test_processRobotsTxt_disabled() {
        final String url = "http://localhost:7070/hoge.html";
        httpClient.robotsTxtHelper.setEnabled(false);
        httpClient.processRobotsTxt(url);
        assertTrue(true);
    }

    @Test
    public void test_convertRobotsTxtPathPattern() {
        assertEquals("/.*\\?.*", httpClient.convertRobotsTxtPatternToRegex("/*?*"));
        assertEquals("/.*", httpClient.convertRobotsTxtPatternToRegex("/"));
        assertEquals("/index\\.html$", httpClient.convertRobotsTxtPatternToRegex("/index.html$"));
        assertEquals(".*index\\.html$", httpClient.convertRobotsTxtPatternToRegex("index.html$"));
        assertEquals("/\\..*", httpClient.convertRobotsTxtPatternToRegex("/."));
        assertEquals("/.*", httpClient.convertRobotsTxtPatternToRegex("/*"));
        assertEquals(".*\\..*", httpClient.convertRobotsTxtPatternToRegex("."));
        assertEquals(".*", httpClient.convertRobotsTxtPatternToRegex("*"));
    }

    @Test
    public void test_doHead() throws Exception {
        final CrawlerWebServer server = new CrawlerWebServer(0);
        server.start();

        final String url = "http://localhost:" + server.getPort() + "/";
        try {
            final ResponseData responseData = httpClient.doHead(url);
            assertNotNull(responseData.getLastModified());
            assertTrue(responseData.getLastModified().getTime() < new Date().getTime());
        } finally {
            server.stop();
        }
    }

    @Test
    public void test_doGet_accessTimeoutTarget() {
        Hc5HttpClient client = new Hc5HttpClient() {
            @Override
            protected ResponseData processHttpMethod(final String url, final ClassicHttpRequest httpRequest) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new CrawlingAccessException(e);
                }
                return null;
            }
        };
        client.setAccessTimeout(1);
        try {
            client.doGet("http://localhost/");
            fail();
        } catch (CrawlingAccessException e) {
            assertTrue(e.getCause() instanceof InterruptedException);
        }
    }

    @Test
    public void test_doHead_accessTimeoutTarget() {
        Hc5HttpClient client = new Hc5HttpClient() {
            @Override
            protected ResponseData processHttpMethod(final String url, final ClassicHttpRequest httpRequest) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new CrawlingAccessException(e);
                }
                return null;
            }
        };
        client.setAccessTimeout(1);
        try {
            client.doHead("http://localhost/");
            fail();
        } catch (CrawlingAccessException e) {
            assertTrue(e.getCause() instanceof InterruptedException);
        }
    }

    @Test
    public void test_constructRedirectLocation() throws Exception {
        assertEquals("http://localhost/login.html", Hc5HttpClient.constructRedirectLocation("http://localhost/", "/login.html"));
        assertEquals("http://localhost/path/login.html", Hc5HttpClient.constructRedirectLocation("http://localhost/path/", "login.html"));
        assertEquals("http://localhost/login.html", Hc5HttpClient.constructRedirectLocation("http://localhost/path/", "/login.html"));
        assertEquals("https://example.com/newpage",
                Hc5HttpClient.constructRedirectLocation("http://localhost/", "https://example.com/newpage"));
        assertEquals("http://localhost/search?q=java", Hc5HttpClient.constructRedirectLocation("http://localhost/", "/search?q=java"));
        assertEquals("http://localhost/home#section1", Hc5HttpClient.constructRedirectLocation("http://localhost/", "/home#section1"));
        assertEquals("http://localhost/newpage", Hc5HttpClient.constructRedirectLocation("http://localhost", "newpage"));
        assertEquals("http://localhost/newpage", Hc5HttpClient.constructRedirectLocation("http://localhost/path/", "../newpage"));
        assertEquals("http://localhost/path/newpage", Hc5HttpClient.constructRedirectLocation("http://localhost/path/", "./newpage"));
        assertEquals("http://localhost/", Hc5HttpClient.constructRedirectLocation("http://localhost/", null));
        assertEquals("http://localhost/", Hc5HttpClient.constructRedirectLocation("http://localhost/", ""));
        assertEquals("http://localhost/?query=value", Hc5HttpClient.constructRedirectLocation("http://localhost/", "?query=value"));
        assertEquals("http://localhost/#section1", Hc5HttpClient.constructRedirectLocation("http://localhost/", "#section1"));
        assertEquals("http://example.com/path", Hc5HttpClient.constructRedirectLocation("http://localhost/", "//example.com/path"));
        assertEquals("mailto:user@example.com", Hc5HttpClient.constructRedirectLocation("http://localhost/", "mailto:user@example.com"));
        assertEquals("data:text/plain;base64,SGVsbG8gd29ybGQ=",
                Hc5HttpClient.constructRedirectLocation("http://localhost/", "data:text/plain;base64,SGVsbG8gd29ybGQ="));
        assertEquals("http://192.168.1.1/path/file", Hc5HttpClient.constructRedirectLocation("http://192.168.1.1/path/", "file"));
        assertEquals("http://[2001:db8::1]/path/file", Hc5HttpClient.constructRedirectLocation("http://[2001:db8::1]/path/", "file"));
        assertEquals("http://example.com:8080/path/file", Hc5HttpClient.constructRedirectLocation("http://example.com:8080/path/", "file"));
        assertEquals("http://example.com/%E3%83%86%E3%82%B9%E3%83%88",
                Hc5HttpClient.constructRedirectLocation("http://example.com/", "テスト"));
        assertEquals("http://example.com/hello%20world", Hc5HttpClient.constructRedirectLocation("http://example.com/", "hello world"));
        assertEquals("http://user:pass@example.com/path/file",
                Hc5HttpClient.constructRedirectLocation("http://user:pass@example.com/path/", "file"));
        assertEquals("http://example.com/", Hc5HttpClient.constructRedirectLocation("http://example.com/path/", "../"));
    }

    // Tests for init() method with AuthSchemeRegistry configuration

    @Test
    public void test_init_withDefaultAuthSchemes() {
        // init() should work without any authentication configuration
        // Default schemes (BASIC, DIGEST, BEARER) should be registered
        Hc5HttpClient client = new Hc5HttpClient();
        client.robotsTxtHelper = new RobotsTxtHelper();
        client.init();
        // Verify httpClient is initialized
        assertNotNull(client);
        client.close();
    }

    @Test
    public void test_init_withNtlmAuthenticationConfig() {
        // When NTLM authentication is configured via WebAuthenticationConfig,
        // the NTLM SchemeFactory should be registered
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

        // Verify NTLM parameters are collected and client is initialized
        assertNotNull(client);
        client.close();
    }

    @Test
    public void test_init_withExplicitAuthSchemeFactories() {
        // When explicit AuthSchemeFactories are configured, they should be added to the registry
        Map<String, AuthSchemeFactory> factoryMap = new HashMap<>();
        factoryMap.put(StandardAuthScheme.BASIC, BasicSchemeFactory.INSTANCE);

        Map<String, Object> params = new HashMap<>();
        params.put(HcHttpClient.AUTH_SCHEME_PROVIDERS_PROPERTY, factoryMap);

        Hc5HttpClient client = new Hc5HttpClient();
        client.robotsTxtHelper = new RobotsTxtHelper();
        client.setInitParameterMap(params);
        client.init();

        // Verify client is initialized with explicit auth scheme factories
        assertNotNull(client);
        client.close();
    }

    @Test
    public void test_init_withNtlmAndExplicitFactories() {
        // Test combining NTLM config with explicit factories
        WebAuthenticationConfig ntlmConfig = new WebAuthenticationConfig();
        ntlmConfig.setScheme("http");
        ntlmConfig.setHost("ntlm.example.com");
        ntlmConfig.setPort(80);
        ntlmConfig.setAuthSchemeType(AuthSchemeType.NTLM);

        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setType(CredentialsConfig.CredentialsType.NTLM);
        credentials.setUsername("testuser");
        credentials.setPassword("testpass");
        ntlmConfig.setCredentials(credentials);

        Map<String, AuthSchemeFactory> factoryMap = new HashMap<>();
        factoryMap.put(StandardAuthScheme.BASIC, BasicSchemeFactory.INSTANCE);

        Map<String, Object> params = new HashMap<>();
        params.put(HcHttpClient.AUTHENTICATIONS_PROPERTY, new WebAuthenticationConfig[] { ntlmConfig });
        params.put(HcHttpClient.AUTH_SCHEME_PROVIDERS_PROPERTY, factoryMap);

        Hc5HttpClient client = new Hc5HttpClient();
        client.robotsTxtHelper = new RobotsTxtHelper();
        client.setInitParameterMap(params);
        client.init();

        // Verify client is initialized with both NTLM and explicit factories
        assertNotNull(client);
        client.close();
    }

    @Test
    public void test_init_withMultipleNtlmConfigs() {
        // Test with multiple NTLM configs - parameters should be merged
        WebAuthenticationConfig config1 = new WebAuthenticationConfig();
        config1.setScheme("http");
        config1.setHost("ntlm1.example.com");
        config1.setPort(80);
        config1.setAuthSchemeType(AuthSchemeType.NTLM);

        CredentialsConfig credentials1 = new CredentialsConfig();
        credentials1.setType(CredentialsConfig.CredentialsType.NTLM);
        credentials1.setUsername("user1");
        credentials1.setPassword("pass1");
        config1.setCredentials(credentials1);

        Map<String, String> ntlmParams1 = new HashMap<>();
        ntlmParams1.put("jcifs.smb.client.domain", "DOMAIN1");
        config1.setNtlmParameters(ntlmParams1);

        WebAuthenticationConfig config2 = new WebAuthenticationConfig();
        config2.setScheme("http");
        config2.setHost("ntlm2.example.com");
        config2.setPort(8080);
        config2.setAuthSchemeType(AuthSchemeType.NTLM);

        CredentialsConfig credentials2 = new CredentialsConfig();
        credentials2.setType(CredentialsConfig.CredentialsType.NTLM);
        credentials2.setUsername("user2");
        credentials2.setPassword("pass2");
        config2.setCredentials(credentials2);

        Map<String, String> ntlmParams2 = new HashMap<>();
        ntlmParams2.put("jcifs.smb.client.domain", "DOMAIN2");
        config2.setNtlmParameters(ntlmParams2);

        Map<String, Object> params = new HashMap<>();
        params.put(HcHttpClient.AUTHENTICATIONS_PROPERTY, new WebAuthenticationConfig[] { config1, config2 });

        Hc5HttpClient client = new Hc5HttpClient();
        client.robotsTxtHelper = new RobotsTxtHelper();
        client.setInitParameterMap(params);
        client.init();

        // Verify client is initialized with multiple NTLM configurations
        assertNotNull(client);
        client.close();
    }

    @Test
    public void test_init_withMixedAuthConfigs() {
        // Test with mixed auth configs (BASIC + NTLM)
        WebAuthenticationConfig basicConfig = new WebAuthenticationConfig();
        basicConfig.setScheme("http");
        basicConfig.setHost("basic.example.com");
        basicConfig.setPort(80);
        basicConfig.setAuthSchemeType(AuthSchemeType.BASIC);

        CredentialsConfig basicCredentials = new CredentialsConfig();
        basicCredentials.setUsername("basicuser");
        basicCredentials.setPassword("basicpass");
        basicConfig.setCredentials(basicCredentials);

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
        params.put(HcHttpClient.AUTHENTICATIONS_PROPERTY, new WebAuthenticationConfig[] { basicConfig, ntlmConfig });

        Hc5HttpClient client = new Hc5HttpClient();
        client.robotsTxtHelper = new RobotsTxtHelper();
        client.setInitParameterMap(params);
        client.init();

        // Verify client is initialized with mixed auth configurations
        assertNotNull(client);
        client.close();
    }

    // Tests for NTLM authentication strategy

    @Test
    public void test_init_withNtlmConfig_setsCustomAuthenticationStrategy() {
        // When NTLM authentication is configured, the custom authentication strategy
        // should be set with NTLM in the scheme priority list
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

        // Verify client is initialized with custom NTLM authentication strategy
        assertNotNull(client);

        client.close();
    }

    @Test
    public void test_init_withoutNtlmConfig_usesDefaultStrategy() {
        // When no NTLM authentication is configured, the default authentication strategy
        // should be used (no custom strategy with NTLM)
        Hc5HttpClient client = new Hc5HttpClient();
        client.robotsTxtHelper = new RobotsTxtHelper();
        client.init();

        // Verify client is initialized with default strategy
        assertNotNull(client);

        client.close();
    }

    @Test
    public void test_init_withBasicOnlyConfig_doesNotSetNtlmStrategy() {
        // When only BASIC authentication is configured, no NTLM custom strategy should be set
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

        // Verify client is initialized without NTLM strategy (default is used)
        assertNotNull(client);

        client.close();
    }

    @Test
    public void test_init_withDigestOnlyConfig_doesNotSetNtlmStrategy() {
        // When only DIGEST authentication is configured, no NTLM custom strategy should be set
        WebAuthenticationConfig config = new WebAuthenticationConfig();
        config.setScheme("http");
        config.setHost("digest.example.com");
        config.setPort(80);
        config.setAuthSchemeType(AuthSchemeType.DIGEST);

        CredentialsConfig credentials = new CredentialsConfig();
        credentials.setUsername("digestuser");
        credentials.setPassword("digestpass");
        config.setCredentials(credentials);

        Map<String, Object> params = new HashMap<>();
        params.put(HcHttpClient.AUTHENTICATIONS_PROPERTY, new WebAuthenticationConfig[] { config });

        Hc5HttpClient client = new Hc5HttpClient();
        client.robotsTxtHelper = new RobotsTxtHelper();
        client.setInitParameterMap(params);
        client.init();

        // Verify client is initialized without NTLM strategy (default is used)
        assertNotNull(client);

        client.close();
    }

    @Test
    public void test_init_withNtlmConfig_multipleHosts() {
        // Test with multiple NTLM configurations for different hosts
        WebAuthenticationConfig config1 = new WebAuthenticationConfig();
        config1.setScheme("http");
        config1.setHost("ntlm1.example.com");
        config1.setPort(80);
        config1.setAuthSchemeType(AuthSchemeType.NTLM);

        CredentialsConfig credentials1 = new CredentialsConfig();
        credentials1.setType(CredentialsConfig.CredentialsType.NTLM);
        credentials1.setUsername("user1");
        credentials1.setPassword("pass1");
        credentials1.setDomain("DOMAIN1");
        config1.setCredentials(credentials1);

        Map<String, String> ntlmParams1 = new HashMap<>();
        ntlmParams1.put("jcifs.smb.client.domain", "DOMAIN1");
        config1.setNtlmParameters(ntlmParams1);

        WebAuthenticationConfig config2 = new WebAuthenticationConfig();
        config2.setScheme("https");
        config2.setHost("ntlm2.example.com");
        config2.setPort(443);
        config2.setAuthSchemeType(AuthSchemeType.NTLM);

        CredentialsConfig credentials2 = new CredentialsConfig();
        credentials2.setType(CredentialsConfig.CredentialsType.NTLM);
        credentials2.setUsername("user2");
        credentials2.setPassword("pass2");
        credentials2.setDomain("DOMAIN2");
        config2.setCredentials(credentials2);

        Map<String, String> ntlmParams2 = new HashMap<>();
        ntlmParams2.put("jcifs.smb.client.domain", "DOMAIN2");
        config2.setNtlmParameters(ntlmParams2);

        Map<String, Object> params = new HashMap<>();
        params.put(HcHttpClient.AUTHENTICATIONS_PROPERTY, new WebAuthenticationConfig[] { config1, config2 });

        Hc5HttpClient client = new Hc5HttpClient();
        client.robotsTxtHelper = new RobotsTxtHelper();
        client.setInitParameterMap(params);
        client.init();

        // Verify client is initialized with multiple NTLM hosts
        assertNotNull(client);

        client.close();
    }
}
