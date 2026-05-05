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
package org.codelibs.fess.crawler.extractor.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.dbflute.utflute.core.PlainTestCase;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.util.Callback;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * @author shinsuke
 *
 */
public class ApiExtractorTest extends PlainTestCase {
    final int port = 9876;

    final String ATTR_NAME = "filedata";

    private TestApiExtractorServer server;

    private ApiExtractor extractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        server = new TestApiExtractorServer(port);
        server.start();

        extractor = new ApiExtractor();
        extractor.setUrl("http://127.0.0.1:" + port + "/");
        extractor.init();
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        server.stop();
        extractor.destroy();

        super.tearDown(testInfo);
    }

    @Test
    public void test_getText() throws Exception {
        final String testStr = "testdata";
        final String content = ATTR_NAME + "," + testStr;
        final Map<String, String> params = new HashMap<String, String>();
        //final ExtractData text = extractor.getText(new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(filePath))), params);
        final ExtractData text = extractor.getText(new ByteArrayInputStream(testStr.getBytes()), params);
        assertEquals(content, text.getContent());
    }

    /**
     * Connection reuse / pooling sanity check: 10 sequential calls share the pool without
     * exhausting sockets.
     */
    @Test
    public void test_connectionReuse() throws Exception {
        final String testStr = "abc";
        final String expected = ATTR_NAME + "," + testStr;
        for (int i = 0; i < 10; i++) {
            final ExtractData text = extractor.getText(new ByteArrayInputStream(testStr.getBytes()), new HashMap<>());
            assertEquals(expected, text.getContent());
        }
    }

    /**
     * Per-call URL override via {@code params.extractorUrl}.
     */
    @Test
    public void test_extractorUrlOverride() throws Exception {
        // Misconfigure the default URL; the override in params should be used instead.
        final ApiExtractor overrideExtractor = new ApiExtractor();
        overrideExtractor.setUrl("http://127.0.0.1:1/does-not-exist");
        overrideExtractor.setAllowExtractorUrlOverride(true);
        overrideExtractor.init();
        try {
            final Map<String, String> params = new HashMap<>();
            params.put(ApiExtractor.PARAM_EXTRACTOR_URL, "http://127.0.0.1:" + port + "/");
            final ExtractData text = overrideExtractor.getText(new ByteArrayInputStream("ovr".getBytes()), params);
            assertEquals(ATTR_NAME + ",ovr", text.getContent());
        } finally {
            overrideExtractor.destroy();
        }
    }

    @Test
    public void test_extractorUrlOverride_disabledByDefault_isIgnored() throws Exception {
        // With allowExtractorUrlOverride defaulting to false, the params override must be ignored
        // and the configured (working) URL must be used regardless of the bogus override.
        final ApiExtractor extractor = new ApiExtractor();
        extractor.setUrl("http://127.0.0.1:" + port + "/");
        extractor.init();
        try {
            final Map<String, String> params = new HashMap<>();
            params.put(ApiExtractor.PARAM_EXTRACTOR_URL, "http://127.0.0.1:1/should-be-ignored");
            final ExtractData text = extractor.getText(new ByteArrayInputStream("def".getBytes()), params);
            // Successful extraction proves the configured URL was used, not the unreachable override.
            assertEquals(ATTR_NAME + ",def", text.getContent());
        } finally {
            extractor.destroy();
        }
    }

    @Test
    public void test_extractorUrlOverride_disallowedSchemeRejected() throws Exception {
        // Even when override is enabled, non-http(s) schemes must be rejected. The configured URL
        // is used instead, so the request still succeeds against the in-process server.
        final ApiExtractor extractor = new ApiExtractor();
        extractor.setUrl("http://127.0.0.1:" + port + "/");
        extractor.setAllowExtractorUrlOverride(true);
        extractor.init();
        try {
            final Map<String, String> params = new HashMap<>();
            params.put(ApiExtractor.PARAM_EXTRACTOR_URL, "file:///etc/passwd");
            final ExtractData text = extractor.getText(new ByteArrayInputStream("rej".getBytes()), params);
            assertEquals(ATTR_NAME + ",rej", text.getContent());
        } finally {
            extractor.destroy();
        }
    }

    /**
     * Response larger than {@code maxResponseSize} must throw {@link ExtractException}.
     */
    @Test
    public void test_responseExceedsMaxSize_throwsExtractException() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        // Always respond with 1 KiB of payload regardless of input.
        final byte[] payload = new byte[1024];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) ('a' + (i % 26));
        }
        simple.setHandler(exchange -> {
            drain(exchange.getRequestBody());
            final byte[] body = payload;
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        simple.start();
        try {
            final ApiExtractor capped = new ApiExtractor();
            capped.setUrl("http://127.0.0.1:" + simple.port() + "/");
            capped.setMaxResponseSize(64L);
            capped.setMaxRetries(0);
            capped.init();
            try {
                capped.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
                fail();
            } catch (final ExtractException e) {
                org.junit.jupiter.api.Assertions.assertTrue(e.getMessage().contains("exceeded limit"),
                        "message should mention limit: " + e.getMessage());
            } finally {
                capped.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    /**
     * Two 5xx responses followed by a 200 must succeed via the retry loop.
     */
    @Test
    public void test_retryOn5xx_succeedsOn3rdAttempt() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        final AtomicInteger calls = new AtomicInteger();
        simple.setHandler(exchange -> {
            drain(exchange.getRequestBody());
            final int n = calls.incrementAndGet();
            if (n < 3) {
                exchange.sendResponseHeaders(503, -1);
                exchange.close();
            } else {
                final byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream out = exchange.getResponseBody()) {
                    out.write(body);
                }
            }
        });
        simple.start();
        try {
            final ApiExtractor retrying = new ApiExtractor();
            retrying.setUrl("http://127.0.0.1:" + simple.port() + "/");
            retrying.setMaxRetries(2);
            retrying.setRetryBackoffMs(10L);
            retrying.init();
            try {
                final ExtractData data = retrying.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
                assertNotNull(data);
                assertEquals("ok", data.getContent());
                assertEquals(3, calls.get());
            } finally {
                retrying.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    /**
     * 4xx (other than 408/429) must NOT be retried; the extractor returns null after a single call.
     */
    @Test
    public void test_noRetryOn4xx_returnsNullImmediately() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        final AtomicInteger calls = new AtomicInteger();
        simple.setHandler(exchange -> {
            drain(exchange.getRequestBody());
            calls.incrementAndGet();
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        });
        simple.start();
        try {
            final ApiExtractor retrying = new ApiExtractor();
            retrying.setUrl("http://127.0.0.1:" + simple.port() + "/");
            retrying.setMaxRetries(3);
            retrying.setRetryBackoffMs(10L);
            retrying.init();
            try {
                final ExtractData data = retrying.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
                // Existing contract: non-OK non-retryable status returns null.
                assertNull(data);
                assertEquals(1, calls.get());
            } finally {
                retrying.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    /**
     * Honors the {@code Retry-After} header (delta-seconds form) for HTTP 429 responses.
     */
    @Test
    public void test_retryOn429_respectsRetryAfter() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        final AtomicInteger calls = new AtomicInteger();
        simple.setHandler(exchange -> {
            drain(exchange.getRequestBody());
            final int n = calls.incrementAndGet();
            if (n == 1) {
                exchange.getResponseHeaders().add("Retry-After", "0");
                exchange.sendResponseHeaders(429, -1);
                exchange.close();
            } else {
                final byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream out = exchange.getResponseBody()) {
                    out.write(body);
                }
            }
        });
        simple.start();
        try {
            final ApiExtractor retrying = new ApiExtractor();
            retrying.setUrl("http://127.0.0.1:" + simple.port() + "/");
            retrying.setMaxRetries(1);
            retrying.setRetryBackoffMs(10L);
            retrying.init();
            try {
                final long t0 = System.currentTimeMillis();
                final ExtractData data = retrying.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
                final long elapsed = System.currentTimeMillis() - t0;
                assertNotNull(data);
                assertEquals("ok", data.getContent());
                assertEquals(2, calls.get());
                // Retry-After: 0 means we should not artificially delay.
                org.junit.jupiter.api.Assertions.assertTrue(elapsed < 2000,
                        "retry should not block when Retry-After=0 (was " + elapsed + "ms)");
            } finally {
                retrying.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    /**
     * A server that accepts the connection but never responds must trigger a socket-timeout
     * surfaced as {@link ExtractException}.
     */
    @Test
    public void test_timeoutOnSlowResponse_throwsExtractException() throws Exception {
        // ServerSocket-based "black hole" server: accepts and then sleeps.
        final ServerSocket serverSocket = new ServerSocket(0);
        serverSocket.setSoTimeout(0);
        final int p = serverSocket.getLocalPort();
        final Thread acceptor = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final Socket s = serverSocket.accept();
                    // Leak the socket on purpose — we just want the client to time out.
                    new Thread(() -> {
                        try {
                            Thread.sleep(60_000L);
                        } catch (final InterruptedException ignore) {
                            Thread.currentThread().interrupt();
                        } finally {
                            try {
                                s.close();
                            } catch (final IOException ignore) {
                                // ignore
                            }
                        }
                    }).start();
                } catch (final IOException e) {
                    return;
                }
            }
        }, "slow-loris-acceptor");
        acceptor.setDaemon(true);
        acceptor.start();
        try {
            final ApiExtractor slow = new ApiExtractor();
            slow.setUrl("http://127.0.0.1:" + p + "/");
            slow.setSoTimeout(300);
            slow.setConnectionTimeout(300);
            slow.setMaxRetries(0);
            slow.init();
            try {
                slow.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
                fail();
            } catch (final ExtractException e) {
                // expected
            } finally {
                slow.destroy();
            }
        } finally {
            try {
                serverSocket.close();
            } catch (final IOException ignore) {
                // ignore
            }
            acceptor.interrupt();
        }
    }

    /**
     * Upload bigger than {@code maxRequestSize} must throw {@link ExtractException} before any
     * HTTP call is made. Asserts no request reaches the server.
     */
    @Test
    public void test_uploadExceedsMaxRequestSize_throws() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        final AtomicInteger calls = new AtomicInteger();
        simple.setHandler(exchange -> {
            calls.incrementAndGet();
            drain(exchange.getRequestBody());
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        simple.start();
        try {
            final ApiExtractor capped = new ApiExtractor();
            capped.setUrl("http://127.0.0.1:" + simple.port() + "/");
            capped.setMaxRequestSize(16L);
            capped.setMaxRetries(0);
            capped.init();
            try {
                final byte[] tooBig = new byte[64];
                for (int i = 0; i < tooBig.length; i++) {
                    tooBig[i] = (byte) ('a' + (i % 26));
                }
                capped.getText(new ByteArrayInputStream(tooBig), new HashMap<>());
                fail();
            } catch (final ExtractException e) {
                org.junit.jupiter.api.Assertions.assertTrue(e.getMessage().contains("request body exceeded limit"),
                        "message should mention request body limit: " + e.getMessage());
                assertEquals(0, calls.get());
            } finally {
                capped.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    /**
     * {@code Retry-After} header in the HTTP-date form (RFC 7231) must be parsed correctly: the
     * extractor must wait approximately the indicated delta before retrying.
     */
    @Test
    public void test_retryAfterHttpDate_parsedCorrectly() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        final AtomicInteger calls = new AtomicInteger();
        // Pre-compute an HTTP-date ~3 seconds into the future for the first response.
        // HTTP-date has 1-second resolution (RFC 7231), so a naive `now + N seconds` value
        // can truncate to ~(N-1) seconds in the worst case. Round UP to the next whole
        // second to keep the effective wait close to the intended delta.
        final SimpleDateFormat httpDateFmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        httpDateFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        simple.setHandler(exchange -> {
            drain(exchange.getRequestBody());
            final int n = calls.incrementAndGet();
            if (n == 1) {
                final long futureMs = System.currentTimeMillis() + 3_000L;
                final long futureCeilMs = (futureMs + 999L) / 1000L * 1000L;
                final String httpDate = httpDateFmt.format(new Date(futureCeilMs));
                exchange.getResponseHeaders().add("Retry-After", httpDate);
                exchange.sendResponseHeaders(429, -1);
                exchange.close();
            } else {
                final byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream out = exchange.getResponseBody()) {
                    out.write(body);
                }
            }
        });
        simple.start();
        try {
            final ApiExtractor retrying = new ApiExtractor();
            retrying.setUrl("http://127.0.0.1:" + simple.port() + "/");
            retrying.setMaxRetries(1);
            retrying.setRetryBackoffMs(10L);
            retrying.init();
            try {
                final long t0 = System.currentTimeMillis();
                final ExtractData data = retrying.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
                final long elapsed = System.currentTimeMillis() - t0;
                assertNotNull(data);
                assertEquals("ok", data.getContent());
                assertEquals(2, calls.get());
                // Must wait at least ~2.5s (allow some slack), well above the default ~10ms backoff.
                org.junit.jupiter.api.Assertions.assertTrue(elapsed >= 2_500L,
                        "retry must respect HTTP-date Retry-After (elapsed=" + elapsed + "ms)");
                // And not block longer than is reasonable.
                org.junit.jupiter.api.Assertions.assertTrue(elapsed < 10_000L, "retry must not stall (elapsed=" + elapsed + "ms)");
            } finally {
                retrying.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    /**
     * Probabilistic check that {@link ApiExtractor#computeBackoff(int, long)} adds a jitter
     * within {@code [base, 2*base)} for {@code attempt=0}. Runs many iterations so the bounds
     * are exercised, and asserts at least two distinct values are observed (i.e., it isn't a
     * constant).
     */
    @Test
    public void test_jitterAppliedToBackoff() throws Exception {
        final ApiExtractor jitterExtractor = new ApiExtractor();
        jitterExtractor.setUrl("http://127.0.0.1:1/unused");
        final long base = 100L;
        jitterExtractor.setRetryBackoffMs(base);
        jitterExtractor.init();
        try {
            long min = Long.MAX_VALUE;
            long max = Long.MIN_VALUE;
            final java.util.HashSet<Long> distinct = new java.util.HashSet<>();
            for (int i = 0; i < 200; i++) {
                final long v = jitterExtractor.computeBackoff(0, -1L);
                if (v < min) {
                    min = v;
                }
                if (v > max) {
                    max = v;
                }
                distinct.add(v);
            }
            // Each value must lie within [base, 2*base) — base + jitter where jitter ∈ [0, base).
            org.junit.jupiter.api.Assertions.assertTrue(min >= base, "min should be >= base: min=" + min + ", base=" + base);
            org.junit.jupiter.api.Assertions.assertTrue(max < 2L * base, "max should be < 2*base: max=" + max + ", base=" + base);
            // Probabilistic: with 200 samples and 100 buckets the chance of a single repeated
            // value is astronomically low — at least 2 distinct values is a near-certainty.
            org.junit.jupiter.api.Assertions.assertTrue(distinct.size() >= 2,
                    "jitter should produce varied values, got distinct=" + distinct.size());
        } finally {
            jitterExtractor.destroy();
        }
    }

    @Test
    public void test_sleepQuietly_interruptedThrowsAndPreservesFlag() throws Exception {
        final ApiExtractor extractor = new ApiExtractor();
        extractor.setUrl("http://127.0.0.1:1/unused");
        extractor.init();
        try {
            // Pre-interrupt the thread; sleepQuietly should observe it and bail.
            Thread.currentThread().interrupt();
            try {
                extractor.sleepQuietly(100L);
                fail();
            } catch (final ExtractException e) {
                org.junit.jupiter.api.Assertions.assertTrue(Thread.currentThread().isInterrupted(),
                        "interrupt flag must be preserved for upstream observers");
            }
        } finally {
            // Drain the interrupt flag so it does not leak into subsequent tests.
            Thread.interrupted();
            extractor.destroy();
        }
    }

    @Test
    public void test_executeWithRetries_interruptStopsRetryLoop() throws Exception {
        final SimpleHttpServer server = new SimpleHttpServer();
        final AtomicInteger attempts = new AtomicInteger();
        try {
            // Always reply 503 with a long Retry-After so the extractor enters sleepQuietly.
            server.setHandler(new HttpHandler() {
                @Override
                public void handle(final HttpExchange exchange) throws IOException {
                    attempts.incrementAndGet();
                    drain(exchange.getRequestBody());
                    exchange.getResponseHeaders().add("Retry-After", "30");
                    exchange.sendResponseHeaders(503, 0);
                    exchange.getResponseBody().close();
                }
            });
            server.start();

            final ApiExtractor extractor = new ApiExtractor();
            extractor.setUrl("http://127.0.0.1:" + server.port() + "/");
            extractor.setMaxRetries(5);
            extractor.setRetryBackoffMs(50L);
            extractor.init();
            try {
                final Thread target = Thread.currentThread();
                final Thread interrupter = new Thread(() -> {
                    try {
                        // Wait for the extractor to issue its first request and start sleeping.
                        Thread.sleep(500L);
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    target.interrupt();
                });
                interrupter.setDaemon(true);
                interrupter.start();

                final long startMs = System.currentTimeMillis();
                try {
                    extractor.getText(new ByteArrayInputStream("payload".getBytes(StandardCharsets.UTF_8)), new HashMap<String, String>());
                    fail();
                } catch (final ExtractException e) {
                    // expected
                }
                final long elapsed = System.currentTimeMillis() - startMs;

                // Bail out should be prompt: well under one full Retry-After (30s).
                org.junit.jupiter.api.Assertions.assertTrue(elapsed < 5_000L,
                        "interrupt must short-circuit the retry loop, elapsed=" + elapsed + "ms");
                // Only one HTTP attempt should have reached the server before the interrupt fired.
                org.junit.jupiter.api.Assertions.assertTrue(attempts.get() <= 2,
                        "retry loop must stop on interrupt, attempts=" + attempts.get());

                interrupter.join(1_000L);
            } finally {
                Thread.interrupted();
                extractor.destroy();
            }
        } finally {
            server.stop();
        }
    }

    private static void drain(final InputStream in) throws IOException {
        final byte[] buf = new byte[4096];
        while (in.read(buf) >= 0) {
            // discard
        }
    }

    /** Lightweight HTTP server used for retry / size / status tests. */
    private static class SimpleHttpServer {
        private HttpServer http;
        private int boundPort;

        void setHandler(final HttpHandler handler) throws IOException {
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

    static class TestApiExtractorServer {
        private Server server;

        public TestApiExtractorServer(final int port) {
            server = new Server(port);

            final RequestHandlerImpl requestHandler = new RequestHandlerImpl();
            server.setHandler(new Handler.Sequence(requestHandler, new DefaultHandler()));
        }

        public void start() {
            try {
                server.start();
            } catch (final Exception e) {
                throw new CrawlerSystemException(e);
            }
        }

        public void stop() {
            try {
                server.stop();
                server.join();
            } catch (final Exception e) {
                throw new CrawlerSystemException(e);
            }
        }

        private static class RequestHandlerImpl extends Handler.Abstract {
            public static final String MULTIPART_FORMDATA_TYPE = "multipart/form-data";

            public static boolean isMultipartRequest(Request request) {
                String contentType = request.getHeaders().get(HttpHeader.CONTENT_TYPE);
                return contentType != null && contentType.startsWith(MULTIPART_FORMDATA_TYPE);
            }

            @Override
            public boolean handle(Request request, Response response, Callback callback) throws Exception {
                if (!isMultipartRequest(request) || !HttpMethod.POST.is(request.getMethod())) {
                    response.setStatus(400);
                    response.write(true, ByteBuffer.wrap("400".getBytes(StandardCharsets.UTF_8)), callback);
                    return true;
                }

                try {
                    // Read the content from the request
                    String contentType = request.getHeaders().get(HttpHeader.CONTENT_TYPE);
                    String body = Content.Source.asString(request);

                    // Parse multipart form data manually
                    // Look for the boundary in content type
                    String boundary = null;
                    if (contentType != null && contentType.contains("boundary=")) {
                        boundary = contentType.substring(contentType.indexOf("boundary=") + 9);
                        if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
                            boundary = boundary.substring(1, boundary.length() - 1);
                        }
                    }

                    if (boundary != null && body != null) {
                        // Parse the multipart body
                        String[] parts = body.split("--" + boundary);
                        for (String part : parts) {
                            if (part.contains("Content-Disposition: form-data")) {
                                // Extract name
                                String name = null;
                                int nameStart = part.indexOf("name=\"");
                                if (nameStart >= 0) {
                                    nameStart += 6;
                                    int nameEnd = part.indexOf("\"", nameStart);
                                    if (nameEnd >= 0) {
                                        name = part.substring(nameStart, nameEnd);
                                    }
                                }

                                // Extract value (after the double newline)
                                int valueStart = part.indexOf("\r\n\r\n");
                                if (valueStart < 0) {
                                    valueStart = part.indexOf("\n\n");
                                    if (valueStart >= 0) {
                                        valueStart += 2;
                                    }
                                } else {
                                    valueStart += 4;
                                }

                                if (valueStart >= 0 && name != null) {
                                    String value = part.substring(valueStart);
                                    // Find the end of the actual content (before trailing newlines and boundary markers)
                                    int valueEnd = value.indexOf("\r\n--");
                                    if (valueEnd < 0) {
                                        valueEnd = value.indexOf("\n--");
                                    }
                                    if (valueEnd >= 0) {
                                        value = value.substring(0, valueEnd);
                                    } else {
                                        value = value.trim();
                                        // Remove trailing boundary markers
                                        if (value.endsWith("--")) {
                                            value = value.substring(0, value.length() - 2).trim();
                                        }
                                    }

                                    response.write(true, ByteBuffer.wrap((name + "," + value).getBytes(StandardCharsets.UTF_8)), callback);
                                    return true;
                                }
                            }
                        }
                    }

                    response.setStatus(400);
                    response.write(true, ByteBuffer.wrap("400".getBytes(StandardCharsets.UTF_8)), callback);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.setStatus(500);
                    response.write(true, ByteBuffer.wrap("500".getBytes(StandardCharsets.UTF_8)), callback);
                }
                return true;
            }
        }
    }
}
