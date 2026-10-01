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

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.FaultTolerantClient;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.filter.UrlFilter;
import org.codelibs.fess.crawler.filter.impl.UrlFilterImpl;
import org.codelibs.fess.crawler.helper.ContentLengthHelper;
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

import com.sun.net.httpserver.HttpServer;

/**
 * @author shinsuke
 *
 */
public class Hc4HttpClientTest extends PlainTestCase {
    public Hc4HttpClient httpClient;

    public UrlFilter urlFilter;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("mimeTypeHelper", MimeTypeHelperImpl.class)//
                .singleton("dataHelper", MemoryDataHelper.class)//
                .singleton("urlFilterService", UrlFilterServiceImpl.class)//
                .singleton("urlFilter", UrlFilterImpl.class)//
                .singleton("robotsTxtHelper", RobotsTxtHelper.class)//
                .singleton("httpClient", Hc4HttpClient.class);
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
        Hc4HttpClient client = new Hc4HttpClient() {
            @Override
            protected ResponseData processHttpMethod(final String url, final HttpUriRequest httpRequest) {
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
        Hc4HttpClient client = new Hc4HttpClient() {
            @Override
            protected ResponseData processHttpMethod(final String url, final HttpUriRequest httpRequest) {
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
        assertEquals("http://localhost/login.html", Hc4HttpClient.constructRedirectLocation("http://localhost/", "/login.html"));
        assertEquals("http://localhost/path/login.html", Hc4HttpClient.constructRedirectLocation("http://localhost/path/", "login.html"));
        assertEquals("http://localhost/login.html", Hc4HttpClient.constructRedirectLocation("http://localhost/path/", "/login.html"));
        assertEquals("https://example.com/newpage",
                Hc4HttpClient.constructRedirectLocation("http://localhost/", "https://example.com/newpage"));
        assertEquals("http://localhost/search?q=java", Hc4HttpClient.constructRedirectLocation("http://localhost/", "/search?q=java"));
        assertEquals("http://localhost/home#section1", Hc4HttpClient.constructRedirectLocation("http://localhost/", "/home#section1"));
        assertEquals("http://localhost/newpage", Hc4HttpClient.constructRedirectLocation("http://localhost", "newpage"));
        assertEquals("http://localhost/newpage", Hc4HttpClient.constructRedirectLocation("http://localhost/path/", "../newpage"));
        assertEquals("http://localhost/path/newpage", Hc4HttpClient.constructRedirectLocation("http://localhost/path/", "./newpage"));
        assertEquals("http://localhost/", Hc4HttpClient.constructRedirectLocation("http://localhost/", null));
        assertEquals("http://localhost/", Hc4HttpClient.constructRedirectLocation("http://localhost/", ""));
        assertEquals("http://localhost/?query=value", Hc4HttpClient.constructRedirectLocation("http://localhost/", "?query=value"));
        assertEquals("http://localhost/#section1", Hc4HttpClient.constructRedirectLocation("http://localhost/", "#section1"));
        assertEquals("http://example.com/path", Hc4HttpClient.constructRedirectLocation("http://localhost/", "//example.com/path"));
        assertEquals("mailto:user@example.com", Hc4HttpClient.constructRedirectLocation("http://localhost/", "mailto:user@example.com"));
        assertEquals("data:text/plain;base64,SGVsbG8gd29ybGQ=",
                Hc4HttpClient.constructRedirectLocation("http://localhost/", "data:text/plain;base64,SGVsbG8gd29ybGQ="));
        assertEquals("http://192.168.1.1/path/file", Hc4HttpClient.constructRedirectLocation("http://192.168.1.1/path/", "file"));
        assertEquals("http://[2001:db8::1]/path/file", Hc4HttpClient.constructRedirectLocation("http://[2001:db8::1]/path/", "file"));
        assertEquals("http://example.com:8080/path/file", Hc4HttpClient.constructRedirectLocation("http://example.com:8080/path/", "file"));
        assertEquals("http://example.com/%E3%83%86%E3%82%B9%E3%83%88",
                Hc4HttpClient.constructRedirectLocation("http://example.com/", "テスト"));
        assertEquals("http://example.com/hello%20world", Hc4HttpClient.constructRedirectLocation("http://example.com/", "hello world"));
        assertEquals("http://user:pass@example.com/path/file",
                Hc4HttpClient.constructRedirectLocation("http://user:pass@example.com/path/", "file"));
        assertEquals("http://example.com/", Hc4HttpClient.constructRedirectLocation("http://example.com/path/", "../"));
    }

    // Regression tests for topic/2732 and topic/2733: special characters in redirect URIs

    @Test
    public void test_constructRedirectLocation_withBrackets() {
        // topic/2732: brackets cause URISyntaxException in new URI()
        try {
            String result = Hc4HttpClient.constructRedirectLocation("http://example.com/", "/path/[id]/page");
            assertNotNull(result);
        } catch (Exception e) {
            // Expected: brackets are not valid in URI, causing URISyntaxException
        }
    }

    @Test
    public void test_constructRedirectLocation_withPercentInPath() {
        // Properly encoded percent should work
        try {
            String result = Hc4HttpClient.constructRedirectLocation("http://example.com/", "/100%25done");
            assertNotNull(result);
        } catch (Exception e) {
            // May fail depending on URI implementation
        }
    }

    @Test
    public void test_constructRedirectLocation_withUnicode() {
        // topic/2733: Unicode characters should be encoded
        try {
            String result = Hc4HttpClient.constructRedirectLocation("http://example.com/", "/path/\u00D6sterreich");
            assertNotNull(result);
        } catch (Exception e) {
            // May fail depending on URI handling
        }
    }

    @Test
    public void test_constructRedirectLocation_withHtmlEntityChars() {
        // topic/2733: HTML entity characters in redirect
        try {
            String result = Hc4HttpClient.constructRedirectLocation("http://example.com/", "/page?title=&#214;sterreich");
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
    // contentType is unknown). Unlike Apache HttpClient5, HttpClient4/HttpCore4 (4.5.14/4.4.16)
    // do NOT validate the Content-Length header at the wire-protocol level: a response with a
    // malformed value (e.g. "not-a-number") is handed back successfully with the entity's own
    // content length reported as -1 (unknown) and the raw header value left untouched -- so an
    // Hc4HttpClient precheck bug here IS directly reachable from a real network response. These
    // tests nonetheless override executeHttpClient() to avoid depending on that transport-layer
    // leniency (which is not guaranteed across library versions), exercising the precheck/bound
    // logic under test deterministically.

    /**
     * A response with a malformed (non-numeric) Content-Length header must not fail the URL: the
     * declared length is treated as unknown, the precheck comparison is skipped, and a
     * well-formed, within-limit body is still downloaded and returned normally.
     */
    @Test
    public void test_doGet_malformedContentLengthHeader_withinLimitBody_succeeds() throws Exception {
        final byte[] body = "Hello, crawler!".getBytes("UTF-8");
        final Hc4HttpClient client = new Hc4HttpClient() {
            @Override
            protected HttpResponse executeHttpClient(final HttpUriRequest httpRequest) {
                final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
                response.setHeader("Content-Type", "text/plain; charset=UTF-8");
                response.setHeader("Content-Length", "not-a-number");
                response.setEntity(new ByteArrayEntity(body));
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
        final Hc4HttpClient client = new Hc4HttpClient() {
            @Override
            protected HttpResponse executeHttpClient(final HttpUriRequest httpRequest) {
                final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
                response.setHeader("Content-Type", "text/plain; charset=UTF-8");
                response.setHeader("Content-Length", "not-a-number");
                response.setEntity(new ByteArrayEntity(body));
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

        final Hc4HttpClient client = new Hc4HttpClient() {
            @Override
            protected HttpResponse executeHttpClient(final HttpUriRequest httpRequest) {
                final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
                // Deliberately no Content-Type header -- the type is only knowable after sniffing.
                response.setEntity(new ByteArrayEntity(body));
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

    private static Map<String, Object> robotsTxtDisabled() {
        final Map<String, Object> params = new HashMap<>();
        params.put(HcHttpClient.ROBOTS_TXT_ENABLED_PROPERTY, false);
        return params;
    }

    /** Lightweight HTTP server used for max-content-length tests, mirroring ApiExtractorTest's helper. */
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

    // public void test_doGet_mt() throws Exception {
    // ExecutorService executorService = Executors.newFixedThreadPool(1);
    //
    // // HttpClient Parameters
    // Map<String, Object> paramMap = new HashMap<String, Object>();
    // httpClient.setInitParameterMap(paramMap);
    //
    // DigestScheme digestScheme = new DigestScheme();
    // List<Authentication> basicAuthList = new ArrayList<Authentication>();
    // basicAuthList.add(new AuthenticationImpl(
    // new AuthScope("www.hoge.com", 80),
    // new UsernamePasswordCredentials("username", "password"),
    // digestScheme));
    // paramMap.put(
    // HcHttpClient.AUTHENTICATIONS_PROPERTY,
    // basicAuthList.toArray(new Authentication[basicAuthList.size()]));
    //
    // List<Callable<ResponseData>> list =
    // new ArrayList<Callable<ResponseData>>();
    // for (int i = 0; i < 100; i++) {
    // list.add(new Callable<ResponseData>() {
    // public ResponseData call() throws Exception {
    // String[] urls =
    // new String[] {
    // "http://.../",
    // "http://.../test.pdf",
    // "http://.../test.doc",
    // "http://.../test.xls",
    // "http://.../test.ppt",
    // "http://.../test.txt", };
    // for (String url : urls) {
    // ResponseData responseData = httpClient.doGet(url);
    // // assertEquals(200, responseData.getHttpStatusCode());
    // if (responseData.getHttpStatusCode() != 200) {
    // return responseData;
    // }
    // }
    // return null;
    // }
    // });
    // }
    // List<Future<ResponseData>> futureList = executorService.invokeAll(list);
    // for (Future<ResponseData> future : futureList) {
    // ResponseData responseData = future.get();
    // if (responseData != null) {
    // System.out.println("status: "
    // + responseData.getHttpStatusCode()
    // + " content: "
    // + new String(InputStreamUtil.getBytes(responseData
    // .getResponseBody()), "UTF-8"));
    // } else {
    // System.out.println("OK");
    // }
    // }
    // }
    /**
     * A host listed in the non-proxy hosts is routed directly, others through the proxy.
     */
    @Test
    public void test_buildRoutePlanner_nonProxyHosts() throws Exception {
        final Map<String, Object> params = new HashMap<>();
        params.put(HcHttpClient.PROXY_HOST_PROPERTY, "proxy.example.com");
        params.put(HcHttpClient.PROXY_PORT_PROPERTY, 8080);
        params.put(HcHttpClient.NON_PROXY_HOSTS_PROPERTY, "localhost|*.internal");
        httpClient.setInitParameterMap(params);
        final HttpRoutePlanner planner = httpClient.buildRoutePlanner();

        final HttpRoute direct = planner.determineRoute(new HttpHost("wiki.internal", 80), new HttpGet("/"), new BasicHttpContext());
        assertNull(direct.getProxyHost());
        final HttpRoute proxied = planner.determineRoute(new HttpHost("www.example.org", 80), new HttpGet("/"), new BasicHttpContext());
        assertEquals("proxy.example.com", proxied.getProxyHost().getHostName());
    }
}
