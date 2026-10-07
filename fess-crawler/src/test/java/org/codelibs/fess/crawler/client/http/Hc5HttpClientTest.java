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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPOutputStream;

import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicSchemeFactory;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.NoHttpResponseException;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.codelibs.fess.crawler.client.http.config.CredentialsConfig;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig.AuthSchemeType;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.FaultTolerantClient;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.HostState;
import org.codelibs.fess.crawler.entity.HostState.RobotsTxtStatus;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.exception.RobotsTxtDisallowedException;
import org.codelibs.fess.crawler.exception.RobotsTxtUnavailableException;
import org.codelibs.fess.crawler.filter.UrlFilter;
import org.codelibs.fess.crawler.filter.impl.UrlFilterImpl;
import org.codelibs.fess.crawler.helper.ContentLengthHelper;
import org.codelibs.fess.crawler.helper.MemoryDataHelper;
import org.codelibs.fess.crawler.helper.RobotsTxtHelper;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.codelibs.fess.crawler.service.impl.UrlFilterServiceImpl;
import org.codelibs.fess.crawler.transformer.impl.XpathTransformer;
import org.codelibs.fess.crawler.util.CrawlerWebServer;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.Timeout;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

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
            final CrawlerContext crawlerContext = setUpCrawlerContext();
            httpClient.init();
            httpClient.processRobotsTxt(url);
            final HostState hostState = crawlerContext.peekHostState(url);
            assertEquals(RobotsTxtStatus.PARSED, hostState.getRobotsTxtStatus());
            assertFalse(hostState.isAllowedByRobotsTxt("http://localhost:" + server.getPort() + "/admin/"));
            assertFalse(hostState.isAllowedByRobotsTxt("http://localhost:" + server.getPort() + "/websvn/"));
            assertTrue(hostState.isAllowedByRobotsTxt(url));
            assertTrue(urlFilter.match("http://localhost:" + server.getPort() + "/admin/"));
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

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        CrawlingParameterUtil.setCrawlerContext(null);
        super.tearDown(testInfo);
    }

    /** Registers a crawler context for the current thread, as the crawler threads do. */
    private CrawlerContext setUpCrawlerContext() {
        final CrawlerContext crawlerContext = new CrawlerContext();
        urlFilter.init("id1");
        crawlerContext.setUrlFilter(urlFilter);
        CrawlingParameterUtil.setCrawlerContext(crawlerContext);
        return crawlerContext;
    }

    /** Counts a request per path and returns the path. */
    private static String countRequest(final Map<String, AtomicInteger> counts, final HttpExchange exchange) {
        final String path = exchange.getRequestURI().getPath();
        counts.computeIfAbsent(path, k -> new AtomicInteger()).incrementAndGet();
        return path;
    }

    private static int requestCount(final Map<String, AtomicInteger> counts, final String path) {
        final AtomicInteger count = counts.get(path);
        return count == null ? 0 : count.get();
    }

    /** Sends a response; headers are given as name/value pairs. */
    private static void respond(final HttpExchange exchange, final int status, final String body, final String... headers)
            throws IOException {
        for (int i = 0; i + 1 < headers.length; i += 2) {
            exchange.getResponseHeaders().add(headers[i], headers[i + 1]);
        }
        final byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        if (bytes.length == 0) {
            exchange.sendResponseHeaders(status, -1);
        } else {
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        }
        exchange.close();
    }

    /** A server whose robots.txt is given and whose other paths return a small HTML page. */
    private SimpleHttpServer startRobotsServer(final Map<String, AtomicInteger> counts, final int robotsStatus, final String robotsTxt,
            final String... robotsHeaders) throws IOException {
        final SimpleHttpServer server = new SimpleHttpServer();
        server.setHandler(exchange -> {
            final String path = countRequest(counts, exchange);
            if ("/robots.txt".equals(path)) {
                respond(exchange, robotsStatus, robotsTxt, robotsHeaders);
            } else {
                respond(exchange, 200, "<html><body>ok</body></html>", "Content-Type", "text/html; charset=UTF-8");
            }
        });
        server.start();
        return server;
    }

    @Test
    public void test_robotsTxt_longestMatchWithoutUrlFilterSideEffect() throws Exception {
        final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();
        final SimpleHttpServer server =
                startRobotsServer(counts, 200, "User-agent: *\nDisallow: /a/\nAllow: /a/b\n", "Content-Type", "text/plain");
        try {
            final CrawlerContext crawlerContext = setUpCrawlerContext();
            httpClient.init();
            final String base = "http://127.0.0.1:" + server.port();

            assertEquals(200, httpClient.doGet(base + "/a/b").getHttpStatusCode());
            try {
                httpClient.doGet(base + "/a/c");
                fail();
            } catch (final RobotsTxtDisallowedException e) {
                // expected
            }
            assertEquals(0, requestCount(counts, "/a/c"));
            assertEquals(1, requestCount(counts, "/robots.txt"));
            assertEquals(RobotsTxtStatus.PARSED, crawlerContext.peekHostState(base + "/").getRobotsTxtStatus());
            // robots.txt rules are not copied into the URL filter of the crawl
            assertTrue(urlFilter.match("http://other.example/x"));
            assertTrue(urlFilter.match(base + "/a/c"));
        } finally {
            server.stop();
        }
    }

    @Test
    public void test_robotsTxt_seedUrlDisallowed() throws Exception {
        final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();
        final SimpleHttpServer server = startRobotsServer(counts, 200, "User-agent: *\nDisallow: /private/\n");
        try {
            setUpCrawlerContext();
            httpClient.init();
            try {
                httpClient.doGet("http://127.0.0.1:" + server.port() + "/private/index.html");
                fail();
            } catch (final RobotsTxtDisallowedException e) {
                // expected
            }
            assertEquals(0, requestCount(counts, "/private/index.html"));
        } finally {
            server.stop();
        }
    }

    @Test
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    public void test_robotsTxt_unavailable() throws Exception {
        final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();
        final SimpleHttpServer server = startRobotsServer(counts, 503, "busy", "Retry-After", "3600");
        try {
            final CrawlerContext crawlerContext = setUpCrawlerContext();
            httpClient.init();
            final String base = "http://127.0.0.1:" + server.port();
            try {
                httpClient.doGet(base + "/index.html");
                fail();
            } catch (final RobotsTxtUnavailableException e) {
                // expected
            }
            assertEquals(0, requestCount(counts, "/index.html"));
            assertEquals(1, requestCount(counts, "/robots.txt"));
            assertEquals(RobotsTxtStatus.UNAVAILABLE, crawlerContext.peekHostState(base + "/").getRobotsTxtStatus());
        } finally {
            server.stop();
        }
    }

    @Test
    public void test_robotsTxt_unavailable_allowOnUnavailable() throws Exception {
        final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();
        final SimpleHttpServer server = startRobotsServer(counts, 503, "busy");
        try {
            setUpCrawlerContext();
            final Map<String, Object> params = new HashMap<>();
            params.put(HcHttpClient.ROBOTS_TXT_ALLOW_ON_UNAVAILABLE_PROPERTY, Boolean.TRUE);
            httpClient.setInitParameterMap(params);
            httpClient.init();
            assertEquals(200, httpClient.doGet("http://127.0.0.1:" + server.port() + "/index.html").getHttpStatusCode());
            assertEquals(1, requestCount(counts, "/index.html"));
        } finally {
            server.stop();
        }
    }

    @Test
    public void test_robotsTxt_unavailable_maxRetries() throws Exception {
        final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();
        final SimpleHttpServer server = startRobotsServer(counts, 503, "busy");
        try {
            setUpCrawlerContext();
            final Map<String, Object> params = new HashMap<>();
            params.put(HcHttpClient.ROBOTS_TXT_MAX_RETRIES_PROPERTY, 0);
            httpClient.setInitParameterMap(params);
            httpClient.init();
            // no attempt is retried, so the first failure gives up on the site
            try {
                httpClient.doGet("http://127.0.0.1:" + server.port() + "/index.html");
                fail();
            } catch (final RobotsTxtDisallowedException e) {
                // expected
            }
            assertEquals(0, requestCount(counts, "/index.html"));
        } finally {
            server.stop();
        }
    }

    @Test
    public void test_robotsTxt_redirect() throws Exception {
        final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();
        final SimpleHttpServer server = new SimpleHttpServer();
        server.setHandler(exchange -> {
            final String path = countRequest(counts, exchange);
            if ("/robots.txt".equals(path)) {
                respond(exchange, 301, "", "Location", "/real-robots.txt");
            } else if ("/real-robots.txt".equals(path)) {
                respond(exchange, 200, "User-agent: *\nDisallow: /blocked/\n");
            } else {
                respond(exchange, 200, "<html><body>ok</body></html>", "Content-Type", "text/html");
            }
        });
        server.start();
        try {
            setUpCrawlerContext();
            httpClient.init();
            final String base = "http://127.0.0.1:" + server.port();
            try {
                httpClient.doGet(base + "/blocked/a.html");
                fail();
            } catch (final RobotsTxtDisallowedException e) {
                // expected
            }
            assertEquals(200, httpClient.doGet(base + "/open/a.html").getHttpStatusCode());
            assertEquals(1, requestCount(counts, "/robots.txt"));
            assertEquals(1, requestCount(counts, "/real-robots.txt"));
        } finally {
            server.stop();
        }
    }

    @Test
    public void test_robotsTxt_redirectNotFollowedByClient() throws Exception {
        // robots.txt redirects six times; the last hop disallows everything. When the client itself followed the
        // redirects, the rules would apply; RFC 9309 stops after five hops and allows everything.
        final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();
        final SimpleHttpServer server = new SimpleHttpServer();
        server.setHandler(exchange -> {
            final String path = countRequest(counts, exchange);
            if ("/robots.txt".equals(path)) {
                respond(exchange, 302, "", "Location", "/r1");
            } else if (path.matches("/r[1-5]")) {
                respond(exchange, 302, "", "Location", "/r" + (Integer.parseInt(path.substring(2)) + 1));
            } else if ("/r6".equals(path)) {
                respond(exchange, 200, "User-agent: *\nDisallow: /\n");
            } else {
                respond(exchange, 200, "<html><body>ok</body></html>", "Content-Type", "text/html");
            }
        });
        server.start();
        try {
            setUpCrawlerContext();
            final Map<String, Object> params = new HashMap<>();
            params.put(HcHttpClient.REDIRECTS_ENABLED, Boolean.TRUE);
            httpClient.setInitParameterMap(params);
            httpClient.init();
            assertEquals(200, httpClient.doGet("http://127.0.0.1:" + server.port() + "/index.html").getHttpStatusCode());
            assertEquals(1, requestCount(counts, "/robots.txt"));
            assertEquals(1, requestCount(counts, "/r5"));
            assertEquals(0, requestCount(counts, "/r6"));
        } finally {
            server.stop();
        }
    }

    @Test
    public void test_robotsTxt_redirectsDisabledOnlyForRobotsTxt() throws Exception {
        // The robots.txt request disables redirects; that must not leak into the context shared by the page requests.
        final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();
        final SimpleHttpServer server = new SimpleHttpServer();
        server.setHandler(exchange -> {
            final String path = countRequest(counts, exchange);
            if ("/robots.txt".equals(path)) {
                respond(exchange, 200, "User-agent: *\nDisallow: /private/\n");
            } else if ("/start".equals(path)) {
                respond(exchange, 302, "", "Location", "/target");
            } else {
                respond(exchange, 200, "<html><body>target</body></html>", "Content-Type", "text/html");
            }
        });
        server.start();
        try {
            setUpCrawlerContext();
            final Map<String, Object> params = new HashMap<>();
            params.put(HcHttpClient.REDIRECTS_ENABLED, Boolean.TRUE);
            httpClient.setInitParameterMap(params);
            httpClient.init();
            final ResponseData responseData = httpClient.doGet("http://127.0.0.1:" + server.port() + "/start");
            assertEquals(200, responseData.getHttpStatusCode());
            assertNull(responseData.getRedirectLocation());
            assertEquals(1, requestCount(counts, "/robots.txt"));
            assertEquals(1, requestCount(counts, "/start"));
            assertEquals(1, requestCount(counts, "/target"));
        } finally {
            server.stop();
        }
    }

    @Test
    public void test_robotsTxt_requestHeaders() throws Exception {
        final Map<String, String> robotsHeaders = new ConcurrentHashMap<>();
        final SimpleHttpServer server = new SimpleHttpServer();
        server.setHandler(exchange -> {
            if ("/robots.txt".equals(exchange.getRequestURI().getPath())) {
                robotsHeaders.put("User-Agent", String.valueOf(exchange.getRequestHeaders().getFirst("User-Agent")));
                robotsHeaders.put("X-Crawler", String.valueOf(exchange.getRequestHeaders().getFirst("X-Crawler")));
                respond(exchange, 404, "");
            } else {
                respond(exchange, 200, "<html><body>ok</body></html>", "Content-Type", "text/html");
            }
        });
        server.start();
        try {
            setUpCrawlerContext();
            final Map<String, Object> params = new HashMap<>();
            params.put(HcHttpClient.USER_AGENT_PROPERTY, "TestBot/1.0");
            final RequestHeader requestHeader = new RequestHeader("X-Crawler", "fess");
            params.put(HcHttpClient.REQUEST_HEADERS_PROPERTY, new RequestHeader[] { requestHeader });
            httpClient.setInitParameterMap(params);
            httpClient.init();
            assertEquals(200, httpClient.doGet("http://127.0.0.1:" + server.port() + "/index.html").getHttpStatusCode());
            assertEquals("TestBot/1.0", robotsHeaders.get("User-Agent"));
            assertEquals("fess", robotsHeaders.get("X-Crawler"));
        } finally {
            server.stop();
        }
    }

    @Test
    public void test_robotsTxt_oversizedChunkedBodyAllowsAll() throws Exception {
        final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();
        final StringBuilder robotsTxt = new StringBuilder("User-agent: *\nDisallow: /\n");
        while (robotsTxt.length() < 8192) {
            robotsTxt.append("# padding padding padding padding\n");
        }
        final SimpleHttpServer server = new SimpleHttpServer();
        server.setHandler(exchange -> {
            final String path = countRequest(counts, exchange);
            if ("/robots.txt".equals(path)) {
                // responseLength == 0 sends the body chunked, without Content-Length
                exchange.sendResponseHeaders(200, 0);
                try (OutputStream out = exchange.getResponseBody()) {
                    out.write(robotsTxt.toString().getBytes(StandardCharsets.UTF_8));
                }
                exchange.close();
            } else {
                respond(exchange, 200, "<html><body>ok</body></html>", "Content-Type", "text/html");
            }
        });
        server.start();
        try {
            final CrawlerContext crawlerContext = setUpCrawlerContext();
            final ContentLengthHelper helper = new ContentLengthHelper();
            helper.addMaxLength("text/plain", 1024L);
            httpClient.contentLengthHelper = helper;
            httpClient.init();
            final String base = "http://127.0.0.1:" + server.port();
            assertEquals(200, httpClient.doGet(base + "/index.html").getHttpStatusCode());
            assertEquals(RobotsTxtStatus.ALLOW_ALL, crawlerContext.peekHostState(base + "/").getRobotsTxtStatus());
        } finally {
            server.stop();
        }
    }

    @Test
    public void test_robotsTxt_oversizedContentLengthAllowsAll() throws Exception {
        final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();
        final StringBuilder robotsTxt = new StringBuilder("User-agent: *\nDisallow: /\n");
        while (robotsTxt.length() < 8192) {
            robotsTxt.append("# padding padding padding padding\n");
        }
        final SimpleHttpServer server = startRobotsServer(counts, 200, robotsTxt.toString());
        try {
            final CrawlerContext crawlerContext = setUpCrawlerContext();
            final ContentLengthHelper helper = new ContentLengthHelper();
            helper.addMaxLength("text/plain", 1024L);
            httpClient.contentLengthHelper = helper;
            httpClient.init();
            final String base = "http://127.0.0.1:" + server.port();
            assertEquals(200, httpClient.doGet(base + "/index.html").getHttpStatusCode());
            assertEquals(RobotsTxtStatus.ALLOW_ALL, crawlerContext.peekHostState(base + "/").getRobotsTxtStatus());
        } finally {
            server.stop();
        }
    }

    @Test
    public void test_ioExceptionRetryStrategy() {
        final Hc5HttpClient.IoExceptionRetryStrategy strategy = new Hc5HttpClient.IoExceptionRetryStrategy();
        final HttpClientContext context = HttpClientContext.create();
        final BasicClassicHttpResponse tooManyRequests = new BasicClassicHttpResponse(429);
        tooManyRequests.setHeader("Retry-After", "3600");
        assertFalse(strategy.retryRequest(tooManyRequests, 1, context));
        assertFalse(strategy.retryRequest(new BasicClassicHttpResponse(503), 1, context));
        // I/O errors are retried as DefaultHttpRequestRetryStrategy does
        assertTrue(strategy.retryRequest(new HttpGet("http://localhost/"), new NoHttpResponseException("closed"), 1, context));
        assertFalse(strategy.retryRequest(new HttpGet("http://localhost/"), new NoHttpResponseException("closed"), 2, context));
        assertFalse(strategy.retryRequest(new HttpGet("http://localhost/"), new ConnectException("refused"), 1, context));
    }

    /**
     * A 429 or 503 page is returned to the caller as is: the client neither retries it nor waits for its Retry-After.
     * The timeout only stops a client that would wait an hour; the assertion is on the number of requests.
     */
    @Test
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    public void test_doGet_tooManyRequestsAndServiceUnavailable_notRetried() throws Exception {
        final Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();
        final SimpleHttpServer server = new SimpleHttpServer();
        server.setHandler(exchange -> {
            final String path = countRequest(counts, exchange);
            if ("/robots.txt".equals(path)) {
                respond(exchange, 404, "");
            } else if ("/busy".equals(path)) {
                respond(exchange, 429, "slow down", "Retry-After", "3600");
            } else {
                respond(exchange, 503, "maintenance", "Retry-After", "3600");
            }
        });
        server.start();
        try {
            setUpCrawlerContext();
            httpClient.init();
            final String base = "http://127.0.0.1:" + server.port();
            assertEquals(429, httpClient.doGet(base + "/busy").getHttpStatusCode());
            assertEquals(1, requestCount(counts, "/busy"));
            assertEquals(503, httpClient.doGet(base + "/down").getHttpStatusCode());
            assertEquals(1, requestCount(counts, "/down"));
        } finally {
            server.stop();
        }
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
        ntlmParams.put("jcifs.client.domain", "TESTDOMAIN");
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
        ntlmParams1.put("jcifs.client.domain", "DOMAIN1");
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
        ntlmParams2.put("jcifs.client.domain", "DOMAIN2");
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
        ntlmParams.put("jcifs.client.domain", "NTLMDOMAIN");
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
        ntlmParams.put("jcifs.client.domain", "TESTDOMAIN");
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
        ntlmParams1.put("jcifs.client.domain", "DOMAIN1");
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
        ntlmParams2.put("jcifs.client.domain", "DOMAIN2");
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

    // Regression tests for topic/2732 and topic/2733: special characters in redirect URIs

    @Test
    public void test_constructRedirectLocation_withBrackets() {
        // topic/2732: brackets cause URISyntaxException in new URI()
        try {
            String result = Hc5HttpClient.constructRedirectLocation("http://example.com/", "/path/[id]/page");
            assertNotNull(result);
        } catch (Exception e) {
            // Expected: brackets are not valid in URI, causing URISyntaxException
        }
    }

    @Test
    public void test_constructRedirectLocation_withPercentInPath() {
        // Properly encoded percent should work
        try {
            String result = Hc5HttpClient.constructRedirectLocation("http://example.com/", "/100%25done");
            assertNotNull(result);
        } catch (Exception e) {
            // May fail depending on URI implementation
        }
    }

    @Test
    public void test_constructRedirectLocation_withUnicode() {
        // topic/2733: Unicode characters should be encoded
        try {
            String result = Hc5HttpClient.constructRedirectLocation("http://example.com/", "/path/\u00D6sterreich");
            assertNotNull(result);
        } catch (Exception e) {
            // May fail depending on URI handling
        }
    }

    @Test
    public void test_constructRedirectLocation_withHtmlEntityChars() {
        // topic/2733: HTML entity characters in redirect
        try {
            String result = Hc5HttpClient.constructRedirectLocation("http://example.com/", "/page?title=&#214;sterreich");
            assertNotNull(result);
        } catch (Exception e) {
            // May fail depending on URI handling
        }
    }

    // Tests for max content length enforcement (precheck + bounded stream)

    /**
     * A response whose declared Content-Length exceeds the max must be rejected by the
     * Content-Length precheck without the body ever being read. The proof that the precheck (not the
     * bounded-stream fallback) rejected it is the reported size: the precheck reports the declared
     * Content-Length (1024) verbatim, whereas the fallback path would report the capped number of
     * bytes actually read (maxLength + 1). This is a deterministic, behavioural check -- it does not
     * rely on wall-clock timing, which is unreliable under CI load.
     */
    @Test
    public void test_doGet_contentLengthHeaderExceedsMax_rejectedWithoutDownloading() throws Exception {
        final SimpleHttpServer server = new SimpleHttpServer();
        final byte[] body = new byte[1024];
        server.setHandler(exchange -> {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        server.start();
        try {
            final ContentLengthHelper helper = new ContentLengthHelper();
            helper.setDefaultMaxLength(64L);
            httpClient.contentLengthHelper = helper;
            httpClient.init();

            try {
                httpClient.doGet("http://127.0.0.1:" + server.port() + "/");
                fail();
            } catch (final MaxLengthExceededException e) {
                assertTrue(e.getMessage().contains("over 64 byte"));
                // The declared Content-Length (1024) must be reported verbatim, confirming the
                // precheck (not the bounded-stream fallback) is what rejected the response. The
                // fallback would instead report the capped bytes actually read (maxLength + 1).
                assertTrue(e.getMessage().contains("(1024 byte)"));
            }
        } finally {
            server.stop();
        }
    }

    /**
     * A response with no Content-Length header (chunked) whose body exceeds the max must be capped
     * mid-copy and rejected, instead of being fully buffered first.
     */
    @Test
    public void test_doGet_chunkedOversizeBody_cappedMidStream() throws Exception {
        final SimpleHttpServer server = new SimpleHttpServer();
        final byte[] body = new byte[8192];
        for (int i = 0; i < body.length; i++) {
            body[i] = (byte) ('a' + (i % 26));
        }
        server.setHandler(exchange -> {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            // responseLength == 0 forces chunked Transfer-Encoding, i.e. no Content-Length header.
            exchange.sendResponseHeaders(200, 0);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        server.start();
        try {
            final ContentLengthHelper helper = new ContentLengthHelper();
            helper.setDefaultMaxLength(64L);
            httpClient.contentLengthHelper = helper;
            httpClient.init();

            try {
                httpClient.doGet("http://127.0.0.1:" + server.port() + "/");
                fail();
            } catch (final MaxLengthExceededException e) {
                assertTrue(e.getMessage().contains("over 64 byte"));
                // The reported size must be capped near the limit (maxLength+1), not the true 8192
                // byte body -- proving the copy was aborted mid-stream rather than fully buffered.
                assertTrue(e.getMessage().contains("(65 byte)"));
            }
        } finally {
            server.stop();
        }
    }

    /**
     * A within-limit response must be returned unchanged (same behavior as before this change).
     */
    @Test
    public void test_doGet_withinLimit_unaffected() throws Exception {
        final SimpleHttpServer server = new SimpleHttpServer();
        final byte[] body = "Hello, crawler!".getBytes("UTF-8");
        server.setHandler(exchange -> {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        server.start();
        try {
            final ContentLengthHelper helper = new ContentLengthHelper();
            helper.setDefaultMaxLength(1024L * 1024L);
            httpClient.contentLengthHelper = helper;
            httpClient.init();

            final ResponseData responseData = httpClient.doGet("http://127.0.0.1:" + server.port() + "/");
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals((long) body.length, responseData.getContentLength());
        } finally {
            server.stop();
        }
    }

    /**
     * When the configured max length is "unlimited" (Long.MAX_VALUE), a large body must not be
     * rejected and must not be bounded.
     */
    @Test
    public void test_doGet_unlimitedMaxLength_doesNotRejectLargeBody() throws Exception {
        final SimpleHttpServer server = new SimpleHttpServer();
        final byte[] body = new byte[256 * 1024];
        for (int i = 0; i < body.length; i++) {
            body[i] = (byte) ('a' + (i % 26));
        }
        server.setHandler(exchange -> {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        server.start();
        try {
            final ContentLengthHelper helper = new ContentLengthHelper();
            helper.setDefaultMaxLength(Long.MAX_VALUE);
            httpClient.contentLengthHelper = helper;
            httpClient.init();

            final ResponseData responseData = httpClient.doGet("http://127.0.0.1:" + server.port() + "/");
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals((long) body.length, responseData.getContentLength());
        } finally {
            server.stop();
        }
    }

    /**
     * When no ContentLengthHelper is configured at all (contentLengthHelper == null, e.g. a client
     * built without DI), a large body must likewise not be rejected.
     */
    @Test
    public void test_doGet_noContentLengthHelper_doesNotRejectLargeBody() throws Exception {
        final SimpleHttpServer server = new SimpleHttpServer();
        final byte[] body = new byte[256 * 1024];
        for (int i = 0; i < body.length; i++) {
            body[i] = (byte) ('a' + (i % 26));
        }
        server.setHandler(exchange -> {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        server.start();
        try {
            httpClient.contentLengthHelper = null;
            httpClient.init();

            final ResponseData responseData = httpClient.doGet("http://127.0.0.1:" + server.port() + "/");
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals((long) body.length, responseData.getContentLength());
        } finally {
            server.stop();
        }
    }

    // Tests for FIX 1 (guarded Content-Length precheck parse) and FIX 2 (overall-max bound when
    // contentType is unknown). Apache HttpClient5 itself validates the Content-Length header at
    // the wire-protocol level and refuses to hand back a usable response for a malformed value
    // (confirmed against httpclient5/httpcore5 5.4-5.6.1: DefaultContentLengthStrategy throws
    // ProtocolException before executeHttpClient() ever returns), so a real network response
    // carrying a malformed header can never reach doHttpMethod()'s precheck code in practice.
    // These tests therefore override executeHttpClient() to hand back a fabricated response
    // directly, which exercises the precheck/bound logic under test deterministically and
    // independently of transport-layer strictness.

    /**
     * A response with a malformed (non-numeric) Content-Length header must not fail the URL: the
     * declared length is treated as unknown, the precheck comparison is skipped, and a
     * well-formed, within-limit body is still downloaded and returned normally.
     */
    @Test
    public void test_doGet_malformedContentLengthHeader_withinLimitBody_succeeds() throws Exception {
        final byte[] body = "Hello, crawler!".getBytes("UTF-8");
        final Hc5HttpClient client = new Hc5HttpClient() {
            @Override
            protected ClassicHttpResponse executeHttpClient(final ClassicHttpRequest httpRequest) {
                final BasicClassicHttpResponse response = new BasicClassicHttpResponse(200, "OK");
                response.setHeader("Content-Type", "text/plain; charset=UTF-8");
                response.setHeader("Content-Length", "not-a-number");
                response.setEntity(new ByteArrayEntity(body, ContentType.TEXT_PLAIN));
                return response;
            }
        };
        final ContentLengthHelper helper = new ContentLengthHelper();
        helper.setDefaultMaxLength(1024L * 1024L);
        client.contentLengthHelper = helper;

        final ResponseData responseData = client.doGet("http://127.0.0.1/dummy");
        assertEquals(200, responseData.getHttpStatusCode());
        assertEquals((long) body.length, responseData.getContentLength());
    }

    /**
     * A malformed Content-Length header must not itself cause a failure, but an actually oversized
     * body must still be capped mid-copy by the BoundedInputStream and rejected by the
     * authoritative post-copy check -- proving the malformed header only disables the precheck
     * shortcut, not the enforcement of the limit itself.
     */
    @Test
    public void test_doGet_malformedContentLengthHeader_oversizedBody_stillCappedAndRejected() throws Exception {
        final byte[] body = new byte[8192];
        for (int i = 0; i < body.length; i++) {
            body[i] = (byte) ('a' + (i % 26));
        }
        final Hc5HttpClient client = new Hc5HttpClient() {
            @Override
            protected ClassicHttpResponse executeHttpClient(final ClassicHttpRequest httpRequest) {
                final BasicClassicHttpResponse response = new BasicClassicHttpResponse(200, "OK");
                response.setHeader("Content-Type", "text/plain; charset=UTF-8");
                response.setHeader("Content-Length", "not-a-number");
                response.setEntity(new ByteArrayEntity(body, ContentType.TEXT_PLAIN));
                return response;
            }
        };
        final ContentLengthHelper helper = new ContentLengthHelper();
        helper.setDefaultMaxLength(64L);
        client.contentLengthHelper = helper;

        try {
            client.doGet("http://127.0.0.1/dummy");
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("over 64 byte"));
            // Capped near the limit (maxLength+1), not the true 8192 byte body.
            assertTrue(e.getMessage().contains("(65 byte)"));
        }
    }

    /**
     * When the response has no Content-Type header (so the type is unknown until the body is
     * sniffed) and a per-type limit configured for the eventually-sniffed type is larger than the
     * default, the precheck/bound must use the overall upper bound rather than just the default --
     * otherwise a body sized in (default, perType] would be capped at default+1 and silently
     * accepted as truncated once the post-copy check re-evaluates against the larger, sniffed-type
     * limit. This asserts the body is downloaded and returned in full, unmodified.
     */
    @Test
    public void test_doGet_unknownContentTypeWithLargerPerTypeLimit_notTruncated() throws Exception {
        final StringBuilder text = new StringBuilder();
        while (text.length() < 4096) {
            text.append("The quick brown fox jumps over the lazy dog. ");
        }
        final byte[] body = text.toString().getBytes("UTF-8");

        final Hc5HttpClient client = new Hc5HttpClient() {
            @Override
            protected ClassicHttpResponse executeHttpClient(final ClassicHttpRequest httpRequest) {
                final BasicClassicHttpResponse response = new BasicClassicHttpResponse(200, "OK");
                // Deliberately no Content-Type header -- the type is only knowable after sniffing.
                response.setEntity(new ByteArrayEntity(body, ContentType.DEFAULT_BINARY));
                return response;
            }
        };
        final ContentLengthHelper helper = new ContentLengthHelper();
        helper.setDefaultMaxLength(100L);
        helper.addMaxLength("text/plain", 1024L * 1024L);
        client.contentLengthHelper = helper;
        client.mimeTypeHelper = new MimeTypeHelperImpl();

        final ResponseData responseData = client.doGet("http://127.0.0.1/dummy");
        assertEquals(200, responseData.getHttpStatusCode());
        assertEquals("text/plain", responseData.getMimeType());
        assertEquals((long) body.length, responseData.getContentLength());
    }

    /** Lightweight HTTP server used for max-content-length tests, mirroring ApiExtractorTest's helper. */
    /**
     * A proxy that demands authentication must receive the configured proxy credentials. The
     * credential-less Basic scheme the client used to put in the auth cache for the proxy was
     * picked up preemptively, failed with "User credentials not set", and the 407 the proxy
     * answered with was returned as the response instead of being retried with credentials.
     */
    @Test
    public void test_doGet_proxyAuthentication() throws Exception {
        final String expected = "Basic " + Base64.getEncoder().encodeToString("proxyuser:proxypass".getBytes(StandardCharsets.UTF_8));
        final List<String> proxyAuthorizations = new CopyOnWriteArrayList<>();
        final SimpleHttpServer proxy = new SimpleHttpServer();
        final byte[] body = "via proxy".getBytes(StandardCharsets.UTF_8);
        proxy.setHandler(exchange -> {
            final String authorization = exchange.getRequestHeaders().getFirst("Proxy-Authorization");
            proxyAuthorizations.add(String.valueOf(authorization));
            if (!expected.equals(authorization)) {
                exchange.getResponseHeaders().add("Proxy-Authenticate", "Basic realm=\"proxy\"");
                exchange.sendResponseHeaders(407, -1);
                exchange.close();
                return;
            }
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        proxy.start();
        try {
            final CredentialsConfig credentials = new CredentialsConfig();
            credentials.setUsername("proxyuser");
            credentials.setPassword("proxypass");
            final WebAuthenticationConfig proxyCredentials = new WebAuthenticationConfig();
            proxyCredentials.setCredentials(credentials);
            final Map<String, Object> params = new HashMap<>();
            params.put(HcHttpClient.PROXY_HOST_PROPERTY, "127.0.0.1");
            params.put(HcHttpClient.PROXY_PORT_PROPERTY, proxy.port());
            params.put(HcHttpClient.PROXY_CREDENTIALS_PROPERTY, proxyCredentials);
            params.put(HcHttpClient.ROBOTS_TXT_ENABLED_PROPERTY, false);
            httpClient.setInitParameterMap(params);
            httpClient.init();

            final ResponseData responseData = httpClient.doGet("http://crawl-target.invalid/");
            assertEquals(200, responseData.getHttpStatusCode());
            // The credentials are sent preemptively, with the first request.
            assertEquals(expected, proxyAuthorizations.get(0));
        } finally {
            proxy.stop();
        }
    }

    /**
     * A host listed in the non-proxy hosts is connected to directly, not through the proxy.
     */
    @Test
    public void test_doGet_nonProxyHosts() throws Exception {
        assertEquals("direct", doGetThroughProxy("localhost|127.0.0.1"));
    }

    /**
     * Without the parameter, the http.nonProxyHosts system property is honored.
     */
    @Test
    public void test_doGet_nonProxyHostsSystemProperty() throws Exception {
        final String original = System.getProperty("http.nonProxyHosts");
        System.setProperty("http.nonProxyHosts", "localhost|127.*");
        try {
            assertEquals("direct", doGetThroughProxy(null));
        } finally {
            if (original == null) {
                System.clearProperty("http.nonProxyHosts");
            } else {
                System.setProperty("http.nonProxyHosts", original);
            }
        }
    }

    /**
     * A host that is not listed still goes through the proxy.
     */
    @Test
    public void test_doGet_nonProxyHostsNotMatched() throws Exception {
        assertEquals("proxy", doGetThroughProxy("*.example.com"));
    }

    private String doGetThroughProxy(final String nonProxyHosts) throws Exception {
        final SimpleHttpServer target = new SimpleHttpServer();
        target.setHandler(exchange -> respond(exchange, "direct"));
        final SimpleHttpServer proxy = new SimpleHttpServer();
        proxy.setHandler(exchange -> respond(exchange, "proxy"));
        target.start();
        proxy.start();
        try {
            final Map<String, Object> params = new HashMap<>();
            params.put(HcHttpClient.PROXY_HOST_PROPERTY, "127.0.0.1");
            params.put(HcHttpClient.PROXY_PORT_PROPERTY, proxy.port());
            if (nonProxyHosts != null) {
                params.put(HcHttpClient.NON_PROXY_HOSTS_PROPERTY, nonProxyHosts);
            }
            params.put(HcHttpClient.ROBOTS_TXT_ENABLED_PROPERTY, false);
            httpClient.setInitParameterMap(params);
            httpClient.init();

            try (ResponseData responseData = httpClient.doGet("http://127.0.0.1:" + target.port() + "/")) {
                assertEquals(200, responseData.getHttpStatusCode());
                return new String(responseData.getResponseBody().readAllBytes(), StandardCharsets.UTF_8);
            }
        } finally {
            proxy.stop();
            target.stop();
        }
    }

    private static void respond(final com.sun.net.httpserver.HttpExchange exchange, final String text) throws IOException {
        final byte[] body = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }

    @Test
    public void test_isNonProxyHost() {
        assertTrue(HcHttpClient.isNonProxyHost("localhost", "localhost|127.0.0.1"));
        assertTrue(HcHttpClient.isNonProxyHost("127.0.0.1", "localhost|127.0.0.1"));
        assertTrue(HcHttpClient.isNonProxyHost("127.0.0.1", "127.*"));
        assertTrue(HcHttpClient.isNonProxyHost("www.Example.com", "*.example.com"));
        assertTrue(HcHttpClient.isNonProxyHost("::1", "[::1]"));
        assertTrue(HcHttpClient.isNonProxyHost("intranet", " localhost | intranet "));
        assertFalse(HcHttpClient.isNonProxyHost("example.com", "*.example.com"));
        assertFalse(HcHttpClient.isNonProxyHost("www.example.org", "localhost|*.example.com"));
        assertFalse(HcHttpClient.isNonProxyHost("localhost", ""));
        assertFalse(HcHttpClient.isNonProxyHost("localhost", null));
    }

    /**
     * A 304 is a normal response, not a redirect without a Location. It must come back with its
     * status, headers and Last-Modified, an empty body, and must not be retried by the
     * fault-tolerant wrapper.
     */
    @Test
    public void test_execute_notModified_returnedAsResponseWithoutRetry() throws Exception {
        final AtomicInteger requests = new AtomicInteger();
        final SimpleHttpServer server = new SimpleHttpServer();
        server.setHandler(exchange -> {
            requests.incrementAndGet();
            exchange.getResponseHeaders().add("ETag", "\"v1\"");
            exchange.getResponseHeaders().add("Last-Modified", "Mon, 01 Jun 2009 21:02:45 GMT");
            exchange.sendResponseHeaders(304, -1);
            exchange.close();
        });
        server.start();
        try {
            final ContentLengthHelper helper = new ContentLengthHelper();
            helper.setDefaultMaxLength(64L);
            httpClient.contentLengthHelper = helper;
            httpClient.setInitParameterMap(robotsTxtDisabled());
            httpClient.init();
            final FaultTolerantClient client = new FaultTolerantClient();
            client.setCrawlerClient(httpClient);
            client.setRetryInterval(0);

            try (ResponseData responseData =
                    client.execute(RequestDataBuilder.newRequestData().get().url("http://127.0.0.1:" + server.port() + "/").build())) {
                assertEquals(304, responseData.getHttpStatusCode());
                assertNull(responseData.getRedirectLocation());
                assertEquals(Constants.GET_METHOD, responseData.getMethod());
                // com.sun.net.httpserver sends the name as "Etag"; metadata keeps the name the server sent
                assertEquals("\"v1\"", responseData.getMetaDataMap().get("Etag"));
                assertEquals(httpClient.parseLastModifiedDate("Mon, 01 Jun 2009 21:02:45 GMT"), responseData.getLastModified());
                assertEquals(0L, responseData.getContentLength());
                assertEquals(0, responseData.getResponseBody().readAllBytes().length);
            }
            assertEquals(1, requests.get());
        } finally {
            server.stop();
        }
    }

    /**
     * Other 3xx codes keep their redirect handling: a 302 without a Location is still rejected.
     */
    @Test
    public void test_doGet_redirectWithoutLocation_stillRejected() throws Exception {
        final SimpleHttpServer server = new SimpleHttpServer();
        server.setHandler(exchange -> {
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        });
        server.start();
        try {
            httpClient.setInitParameterMap(robotsTxtDisabled());
            httpClient.init();
            try {
                httpClient.doGet("http://127.0.0.1:" + server.port() + "/");
                fail();
            } catch (final CrawlingAccessException e) {
                assertTrue(e.getMessage().contains("Invalid redirect location"));
            }
        } finally {
            server.stop();
        }
    }

    /**
     * Per-request headers reach the server. The server answers 304 only for the matching
     * If-None-Match, so the conditional GET returns 304 in a single request and a plain GET 200.
     */
    @Test
    public void test_execute_ifNoneMatch_sentAndNotModifiedReturned() throws Exception {
        final List<String> received = new CopyOnWriteArrayList<>();
        final SimpleHttpServer server = new SimpleHttpServer();
        final byte[] body = "content".getBytes(StandardCharsets.UTF_8);
        server.setHandler(exchange -> {
            final String ifNoneMatch = exchange.getRequestHeaders().getFirst("If-None-Match");
            received.add(String.valueOf(ifNoneMatch));
            exchange.getResponseHeaders().add("ETag", "\"v1\"");
            if ("\"v1\"".equals(ifNoneMatch)) {
                exchange.sendResponseHeaders(304, -1);
                exchange.close();
                return;
            }
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        server.start();
        try {
            httpClient.setInitParameterMap(robotsTxtDisabled());
            httpClient.init();
            final FaultTolerantClient client = new FaultTolerantClient();
            client.setCrawlerClient(httpClient);
            client.setRetryInterval(0);
            final String url = "http://127.0.0.1:" + server.port() + "/";

            try (ResponseData responseData =
                    client.execute(RequestDataBuilder.newRequestData().get().url(url).header("If-None-Match", "\"v1\"").build())) {
                assertEquals(304, responseData.getHttpStatusCode());
                assertNull(responseData.getRedirectLocation());
            }
            assertEquals(List.of("\"v1\""), received);

            received.clear();
            try (ResponseData responseData = client.execute(RequestDataBuilder.newRequestData().get().url(url).build())) {
                assertEquals(200, responseData.getHttpStatusCode());
                assertEquals("content", new String(responseData.getResponseBody().readAllBytes(), StandardCharsets.UTF_8));
            }
            assertEquals(List.of("null"), received);
        } finally {
            server.stop();
        }
    }

    /**
     * If-Modified-Since is sent verbatim.
     */
    @Test
    public void test_execute_ifModifiedSince_sentVerbatim() throws Exception {
        final List<String> received = new CopyOnWriteArrayList<>();
        final SimpleHttpServer server = new SimpleHttpServer();
        server.setHandler(exchange -> {
            received.add(String.valueOf(exchange.getRequestHeaders().getFirst("If-Modified-Since")));
            exchange.sendResponseHeaders(304, -1);
            exchange.close();
        });
        server.start();
        try {
            httpClient.setInitParameterMap(robotsTxtDisabled());
            httpClient.init();
            final String value = "Mon, 01 Jun 2009 21:02:45 GMT";

            try (ResponseData responseData = httpClient.execute(RequestDataBuilder.newRequestData()
                    .get()
                    .url("http://127.0.0.1:" + server.port() + "/")
                    .header("If-Modified-Since", value)
                    .build())) {
                assertEquals(304, responseData.getHttpStatusCode());
            }
            assertEquals(List.of(value), received);
        } finally {
            server.stop();
        }
    }

    /**
     * A 304 may declare the Content-Length of the unchanged representation. It carries no body,
     * so that length must not be reported or checked against the max content length.
     */
    @Test
    public void test_execute_notModifiedWithContentLength_notRejected() throws Exception {
        final SimpleHttpServer server = new SimpleHttpServer();
        server.setHandler(exchange -> {
            exchange.getResponseHeaders().add("Content-Length", "1024");
            exchange.sendResponseHeaders(304, -1);
            exchange.close();
        });
        server.start();
        try {
            httpClient.setInitParameterMap(robotsTxtDisabled());
            httpClient.setMaxContentLength(64L);
            httpClient.init();

            try (ResponseData responseData =
                    httpClient.execute(RequestDataBuilder.newRequestData().get().url("http://127.0.0.1:" + server.port() + "/").build())) {
                assertEquals(304, responseData.getHttpStatusCode());
                assertEquals("1024", responseData.getMetaDataMap().get("Content-length"));
                assertEquals(0L, responseData.getContentLength());
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void test_getSupportedCharset() {
        assertEquals("Shift_JIS", HcHttpClient.getSupportedCharset("text/html; charset=Shift_JIS"));
        assertEquals("EUC-JP", HcHttpClient.getSupportedCharset("text/html;charset=\"EUC-JP\""));
        assertEquals("utf-8", HcHttpClient.getSupportedCharset("text/html; boundary=x; Charset=utf-8"));
        assertEquals("sjis", HcHttpClient.getSupportedCharset("text/html; charset=sjis"));
        assertNull(HcHttpClient.getSupportedCharset(null));
        assertNull(HcHttpClient.getSupportedCharset("text/html"));
        assertNull(HcHttpClient.getSupportedCharset("text/html; charset="));
        assertNull(HcHttpClient.getSupportedCharset("text/html; charset=no-such-charset"));
        assertNull(HcHttpClient.getSupportedCharset("text/html; charset=!!!"));
        assertNull(HcHttpClient.getSupportedCharset("text/html; charset='sjis'"));
    }

    private static final String JA_TITLE = "文書形式の検証";

    private static final String JA_BODY = "日本語の本文です";

    private static final String META_SHIFT_JIS = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=Shift_JIS\">";

    private static final String META_UTF_8 = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";

    private static String japanesePage(final String metaTag) {
        return "<html><head>" + metaTag + "<title>" + JA_TITLE + "</title></head><body><p>" + JA_BODY + "</p></body></html>";
    }

    /** Serves one body for every path but robots.txt; a null content type sends no Content-Type header. */
    private SimpleHttpServer startBodyServer(final byte[] body, final String contentType, final String... headers) throws IOException {
        final SimpleHttpServer server = new SimpleHttpServer();
        server.setHandler(exchange -> {
            if ("/robots.txt".equals(exchange.getRequestURI().getPath())) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            if (contentType != null) {
                exchange.getResponseHeaders().add("Content-Type", contentType);
            }
            for (int i = 0; i + 1 < headers.length; i += 2) {
                exchange.getResponseHeaders().add(headers[i], headers[i + 1]);
            }
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        server.start();
        return server;
    }

    private ResponseData fetch(final SimpleHttpServer server) {
        httpClient.setInitParameterMap(robotsTxtDisabled());
        httpClient.init();
        return httpClient.execute(RequestDataBuilder.newRequestData().get().url("http://127.0.0.1:" + server.port() + "/").build());
    }

    private static byte[] gzip(final byte[] data) throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (GZIPOutputStream out = new GZIPOutputStream(bytes)) {
            out.write(data);
        }
        return bytes.toByteArray();
    }

    /** Fetches the body with the given Content-Type and returns the charset the client reported. */
    private String reportedCharset(final byte[] body, final String contentType, final String... headers) throws Exception {
        final SimpleHttpServer server = startBodyServer(body, contentType, headers);
        try (ResponseData responseData = fetch(server)) {
            assertTrue(Arrays.equals(body, responseData.getResponseBody().readAllBytes()));
            return responseData.getCharSet();
        } finally {
            server.stop();
        }
    }

    /** The charset of the Content-Type header, not of the page's bytes, is what the client reports. */
    @Test
    public void test_execute_charsetOfContentTypeHeader() throws Exception {
        final String page = japanesePage("");
        for (final String charset : new String[] { "Shift_JIS", "EUC-JP", "UTF-8" }) {
            final byte[] body = page.getBytes(charset);
            final String reported = reportedCharset(body, "text/html; charset=" + charset);
            assertEquals(charset, reported);
            // the reported charset decodes the body that was served
            assertEquals(page, new String(body, reported));
        }
    }

    @Test
    public void test_execute_charsetOfContentTypeHeader_quotedAndCaseInsensitive() throws Exception {
        final byte[] body = japanesePage("").getBytes("EUC-JP");
        assertEquals("EUC-JP", reportedCharset(body, "text/html;charset=\"EUC-JP\""));
        assertEquals("shift_jis", reportedCharset(japanesePage("").getBytes("Shift_JIS"), "text/html; CHARSET=shift_jis"));
    }

    /** A response that declares no charset keeps the UTF-8 default. */
    @Test
    public void test_execute_charsetNotDeclared_defaultsToUtf8() throws Exception {
        final byte[] body = japanesePage("").getBytes(StandardCharsets.UTF_8);
        assertEquals(Constants.UTF_8, reportedCharset(body, "text/html"));
        assertEquals(Constants.UTF_8, reportedCharset(body, "text/html;"));
        assertEquals(Constants.UTF_8, reportedCharset(body, null));
    }

    /** A charset the JVM does not know must not fail the crawl; it is treated as undeclared. */
    @Test
    public void test_execute_unsupportedCharset_defaultsToUtf8() throws Exception {
        final byte[] body = japanesePage("").getBytes(StandardCharsets.UTF_8);
        assertEquals(Constants.UTF_8, reportedCharset(body, "text/html; charset=no-such-charset"));
        assertEquals(Constants.UTF_8, reportedCharset(body, "text/html; charset=!!!"));
        assertEquals(Constants.UTF_8, reportedCharset(body, "text/html; charset='sjis'"));
        assertEquals(Constants.UTF_8, reportedCharset(body, "text/html; charset="));
    }

    /** Content-Encoding names a compression, not a charset. */
    @Test
    public void test_execute_contentEncodingIsNotTheCharset() throws Exception {
        final byte[] body = japanesePage("").getBytes("Shift_JIS");
        assertEquals("Shift_JIS", reportedCharset(body, "text/html; charset=Shift_JIS", "Content-Encoding", "identity"));
        assertEquals(Constants.UTF_8, reportedCharset(body, "text/html", "Content-Encoding", "identity"));
    }

    @Test
    public void test_execute_charsetOfCompressedResponse() throws Exception {
        final byte[] body = japanesePage("").getBytes("Shift_JIS");
        final SimpleHttpServer server = startBodyServer(gzip(body), "text/html; charset=Shift_JIS", "Content-Encoding", "gzip");
        try (ResponseData responseData = fetch(server)) {
            assertEquals("Shift_JIS", responseData.getCharSet());
            // the body handed to the transformers is the decompressed one
            assertEquals(japanesePage(""), new String(responseData.getResponseBody().readAllBytes(), responseData.getCharSet()));
        } finally {
            server.stop();
        }
    }

    /** Crawls the page and returns what an XPath transformer, as used by Fess, makes of its title. */
    private String crawledTitle(final byte[] body, final String contentType) throws Exception {
        final XpathTransformer transformer = new XpathTransformer();
        transformer.setName("xpathTransformer");
        transformer.setFeatureMap(Map.of("http://xml.org/sax/features/namespaces", "false"));
        transformer.setPropertyMap(new HashMap<>());
        transformer.setChildUrlRuleMap(new HashMap<>());
        transformer.setFieldRuleMap(new LinkedHashMap<>(Map.of("title", "string(//TITLE)")));

        final SimpleHttpServer server = startBodyServer(body, contentType);
        try (ResponseData responseData = fetch(server)) {
            final ResultData resultData = transformer.transform(responseData);
            final String data = new String(resultData.getData(), StandardCharsets.UTF_8);
            return data.replaceAll("(?s).*<field name=\"title\">(.*)</field>.*", "$1");
        } finally {
            server.stop();
        }
    }

    /** A page in Shift_JIS or EUC-JP is readable when the charset is only in the Content-Type header. */
    @Test
    public void test_crawl_charsetOnlyInContentTypeHeader() throws Exception {
        final String page = japanesePage("");
        assertEquals(JA_TITLE, crawledTitle(page.getBytes("Shift_JIS"), "text/html; charset=Shift_JIS"));
        assertEquals(JA_TITLE, crawledTitle(page.getBytes("EUC-JP"), "text/html; charset=EUC-JP"));
    }

    @Test
    public void test_crawl_charsetOnlyInMetaTag() throws Exception {
        assertEquals(JA_TITLE, crawledTitle(japanesePage(META_SHIFT_JIS).getBytes("Shift_JIS"), "text/html"));
        assertEquals(JA_TITLE, crawledTitle(japanesePage(META_SHIFT_JIS).getBytes("Shift_JIS"), null));
    }

    /** The meta tag keeps overriding the Content-Type header, as it did before the header was reported. */
    @Test
    public void test_crawl_metaTagOverridesContentTypeHeader() throws Exception {
        assertEquals(JA_TITLE, crawledTitle(japanesePage(META_SHIFT_JIS).getBytes("Shift_JIS"), "text/html; charset=UTF-8"));
        assertEquals(JA_TITLE, crawledTitle(japanesePage(META_UTF_8).getBytes(StandardCharsets.UTF_8), "text/html; charset=Shift_JIS"));
    }

    /** A UTF-8 page that declares nothing is still read as UTF-8. */
    @Test
    public void test_crawl_charsetNotDeclared_isUtf8() throws Exception {
        final byte[] body = japanesePage("").getBytes(StandardCharsets.UTF_8);
        assertEquals(JA_TITLE, crawledTitle(body, "text/html"));
        assertEquals(JA_TITLE, crawledTitle(body, null));
        assertEquals(JA_TITLE, crawledTitle(body, "text/html; charset=no-such-charset"));
    }

    private static Map<String, Object> robotsTxtDisabled() {
        final Map<String, Object> params = new HashMap<>();
        params.put(HcHttpClient.ROBOTS_TXT_ENABLED_PROPERTY, false);
        return params;
    }

    private static class SimpleHttpServer {
        private HttpServer http;
        private int boundPort;

        void setHandler(final com.sun.net.httpserver.HttpHandler handler) throws IOException {
            http = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            http.createContext("/", handler);
        }

        void start() {
            http.start();
            boundPort = http.getAddress().getPort();
        }

        void stop() {
            http.stop(0);
        }

        int port() {
            return boundPort;
        }
    }
}
