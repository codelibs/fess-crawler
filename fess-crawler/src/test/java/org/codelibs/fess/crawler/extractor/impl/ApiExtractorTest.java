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
import java.io.File;
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

import org.apache.commons.io.output.DeferredFileOutputStream;
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
        final SimpleHttpServer overrideServer = new SimpleHttpServer();
        final AtomicInteger overrideHits = new AtomicInteger();
        overrideServer.setHandler(exchange -> {
            overrideHits.incrementAndGet();
            drain(exchange.getRequestBody());
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        overrideServer.start();
        try {
            final ApiExtractor extractor = new ApiExtractor();
            extractor.setUrl("http://127.0.0.1:" + port + "/");
            extractor.init();
            try {
                final Map<String, String> params = new HashMap<>();
                params.put(ApiExtractor.PARAM_EXTRACTOR_URL, "http://127.0.0.1:" + overrideServer.port() + "/");
                final ExtractData text = extractor.getText(new ByteArrayInputStream("def".getBytes()), params);
                assertEquals(ATTR_NAME + ",def", text.getContent());
                assertEquals(0, overrideHits.get());
            } finally {
                extractor.destroy();
            }
        } finally {
            overrideServer.stop();
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
            // Large default backoff: if Retry-After is ignored, retry would take ~5s.
            retrying.setRetryBackoffMs(5000L);
            retrying.init();
            try {
                final long t0 = System.currentTimeMillis();
                final ExtractData data = retrying.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
                final long elapsed = System.currentTimeMillis() - t0;
                assertNotNull(data);
                assertEquals("ok", data.getContent());
                assertEquals(2, calls.get());
                // Retry-After: 0 must beat the 5s default backoff.
                org.junit.jupiter.api.Assertions.assertTrue(elapsed < 2500L,
                        "Retry-After:0 must override default backoff (elapsed=" + elapsed + "ms)");
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

    /**
     * When the connection pool is exhausted by a slow upstream, a new caller must surface a
     * pool-checkout timeout instead of blocking indefinitely. Without
     * {@link RequestConfig#setConnectionRequestTimeout(int)} the second call would hang for
     * the full duration of the slow handler.
     */
    @Test
    public void test_connectionRequestTimeout_failsFastWhenPoolExhausted() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        // Slow handler holds the only available connection for several seconds.
        simple.setHandler(exchange -> {
            drain(exchange.getRequestBody());
            try {
                Thread.sleep(3_000L);
            } catch (final InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        simple.start();
        try {
            final ApiExtractor capped = new ApiExtractor();
            capped.setUrl("http://127.0.0.1:" + simple.port() + "/");
            capped.setMaxConnections(1);
            capped.setMaxConnectionsPerRoute(1);
            capped.setConnectionRequestTimeout(200);
            capped.setMaxRetries(0);
            capped.init();
            try {
                // First caller occupies the only pooled connection in a background thread.
                final Thread occupier = new Thread(() -> {
                    try {
                        capped.getText(new ByteArrayInputStream("a".getBytes()), new HashMap<>());
                    } catch (final Exception ignore) {
                        // ignore
                    }
                });
                occupier.setDaemon(true);
                occupier.start();
                // Give the occupier a moment to issue its request and acquire the connection.
                Thread.sleep(300L);

                // Second caller must fail quickly because the pool is exhausted.
                final long t0 = System.currentTimeMillis();
                try {
                    capped.getText(new ByteArrayInputStream("b".getBytes()), new HashMap<>());
                    fail();
                } catch (final ExtractException e) {
                    final long elapsed = System.currentTimeMillis() - t0;
                    org.junit.jupiter.api.Assertions.assertTrue(elapsed < 2_000L,
                            "second call must fail fast on pool exhaustion (was " + elapsed + "ms)");
                }
                occupier.interrupt();
                occupier.join(2_000L);
            } finally {
                capped.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    /**
     * A request body larger than the spool threshold must succeed (proving the spool-to-disk
     * path works) and the server must observe the exact uploaded bytes. This exercises the
     * branch that streams from a temp file rather than a {@code byte[]}.
     */
    @Test
    public void test_requestSpoolThreshold_spillsToFileAndUploadsSuccessfully() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        final int payloadSize = 32 * 1024;
        final byte[] payload = new byte[payloadSize];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) ('A' + (i % 26));
        }
        final java.util.concurrent.atomic.AtomicReference<byte[]> received = new java.util.concurrent.atomic.AtomicReference<>();
        simple.setHandler(exchange -> {
            // Read the complete multipart body verbatim so the test can verify the payload bytes.
            final java.io.ByteArrayOutputStream sink = new java.io.ByteArrayOutputStream();
            final byte[] buf = new byte[4096];
            int n;
            while ((n = exchange.getRequestBody().read(buf)) >= 0) {
                sink.write(buf, 0, n);
            }
            received.set(sink.toByteArray());
            final byte[] body = "spooled".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        simple.start();
        try {
            final ApiExtractor spooled = new ApiExtractor();
            spooled.setUrl("http://127.0.0.1:" + simple.port() + "/");
            // Force every body to spill to a temp file by setting threshold smaller than the payload.
            spooled.setRequestSpoolThreshold(1024);
            spooled.setMaxRetries(0);
            spooled.init();
            try {
                final ExtractData data = spooled.getText(new ByteArrayInputStream(payload), new HashMap<>());
                assertNotNull(data);
                assertEquals("spooled", data.getContent());
                final byte[] body = received.get();
                assertNotNull(body);
                // Multipart framing wraps the payload, so just verify the payload bytes are embedded
                // verbatim somewhere inside the multipart body.
                org.junit.jupiter.api.Assertions.assertTrue(body.length >= payload.length,
                        "received body must be at least the payload size");
                org.junit.jupiter.api.Assertions.assertTrue(indexOf(body, payload) >= 0,
                        "spooled payload bytes must appear in the multipart body");
            } finally {
                spooled.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    /**
     * A hostile {@code Retry-After} value must be clamped to {@code maxRetryAfterMs} so a
     * 24-hour value cannot stall the extractor thread.
     */
    @Test
    public void test_retryAfterClampedToMaxRetryAfter() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        final AtomicInteger calls = new AtomicInteger();
        simple.setHandler(exchange -> {
            drain(exchange.getRequestBody());
            final int n = calls.incrementAndGet();
            if (n == 1) {
                // 24 hours — without clamping this would block the test forever.
                exchange.getResponseHeaders().add("Retry-After", "86400");
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
            final ApiExtractor clamped = new ApiExtractor();
            clamped.setUrl("http://127.0.0.1:" + simple.port() + "/");
            clamped.setMaxRetries(1);
            clamped.setRetryBackoffMs(10L);
            clamped.setMaxRetryAfterMs(150L);
            clamped.init();
            try {
                final long t0 = System.currentTimeMillis();
                final ExtractData data = clamped.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
                final long elapsed = System.currentTimeMillis() - t0;
                assertNotNull(data);
                assertEquals("ok", data.getContent());
                assertEquals(2, calls.get());
                org.junit.jupiter.api.Assertions.assertTrue(elapsed < 5_000L,
                        "Retry-After must be clamped, not honored as 24h (elapsed=" + elapsed + "ms)");
            } finally {
                clamped.destroy();
            }
        } finally {
            simple.stop();
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

    @Test
    public void test_overrideRejects_ControlCharacters() throws Exception {
        final SimpleHttpServer overrideServer = new SimpleHttpServer();
        final AtomicInteger overrideHits = new AtomicInteger();
        overrideServer.setHandler(exchange -> {
            overrideHits.incrementAndGet();
            drain(exchange.getRequestBody());
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        overrideServer.start();
        try {
            final ApiExtractor extractor = new ApiExtractor();
            extractor.setUrl("http://127.0.0.1:" + port + "/");
            extractor.setAllowExtractorUrlOverride(true);
            extractor.init();
            try {
                // URL with embedded newline must be rejected even when override is enabled.
                final Map<String, String> params = new HashMap<>();
                params.put(ApiExtractor.PARAM_EXTRACTOR_URL, "http://127.0.0.1:" + overrideServer.port() + "/\r\nX-Injected: yes");
                final ExtractData text = extractor.getText(new ByteArrayInputStream("ctl".getBytes()), params);
                assertEquals(ATTR_NAME + ",ctl", text.getContent());
                assertEquals(0, overrideHits.get());
            } finally {
                extractor.destroy();
            }
        } finally {
            overrideServer.stop();
        }
    }

    @Test
    public void test_overrideRejects_Userinfo() throws Exception {
        final SimpleHttpServer overrideServer = new SimpleHttpServer();
        final AtomicInteger overrideHits = new AtomicInteger();
        overrideServer.setHandler(exchange -> {
            overrideHits.incrementAndGet();
            drain(exchange.getRequestBody());
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        overrideServer.start();
        try {
            final ApiExtractor extractor = new ApiExtractor();
            extractor.setUrl("http://127.0.0.1:" + port + "/");
            extractor.setAllowExtractorUrlOverride(true);
            extractor.init();
            try {
                final Map<String, String> params = new HashMap<>();
                params.put(ApiExtractor.PARAM_EXTRACTOR_URL, "http://user:pass@127.0.0.1:" + overrideServer.port() + "/");
                final ExtractData text = extractor.getText(new ByteArrayInputStream("ui".getBytes()), params);
                assertEquals(ATTR_NAME + ",ui", text.getContent());
                assertEquals(0, overrideHits.get());
            } finally {
                extractor.destroy();
            }
        } finally {
            overrideServer.stop();
        }
    }

    @Test
    public void test_overrideRejects_OpaqueUri() throws Exception {
        final ApiExtractor extractor = new ApiExtractor();
        extractor.setUrl("http://127.0.0.1:" + port + "/");
        extractor.setAllowExtractorUrlOverride(true);
        extractor.init();
        try {
            final Map<String, String> params = new HashMap<>();
            params.put(ApiExtractor.PARAM_EXTRACTOR_URL, "http:opaque");
            final ExtractData text = extractor.getText(new ByteArrayInputStream("op".getBytes()), params);
            assertEquals(ATTR_NAME + ",op", text.getContent());
        } finally {
            extractor.destroy();
        }
    }

    @Test
    public void test_overrideAccepts_HttpsScheme() throws Exception {
        // We can't easily stand up an HTTPS server in the test, so just confirm that the SCHEME
        // gate accepts https by trying to call an unreachable https URL. The call should fail
        // attempting to connect (not be silently rejected as disallowed scheme — that would
        // succeed against the configured URL).
        final ApiExtractor extractor = new ApiExtractor();
        extractor.setUrl("http://127.0.0.1:" + port + "/");
        extractor.setAllowExtractorUrlOverride(true);
        extractor.setMaxRetries(0);
        extractor.setConnectionTimeout(300);
        extractor.setSoTimeout(300);
        extractor.init();
        try {
            final Map<String, String> params = new HashMap<>();
            // Port 1 is reserved; connection refused is expected.
            params.put(ApiExtractor.PARAM_EXTRACTOR_URL, "https://127.0.0.1:1/blackhole");
            try {
                extractor.getText(new ByteArrayInputStream("hs".getBytes()), params);
                org.junit.jupiter.api.Assertions.fail(
                        "Override to unreachable https should have raised ExtractException, not silently fallen back to configured URL.");
            } catch (final ExtractException expected) {
                // expected: the override scheme passes, so we tried to connect and failed.
            }
        } finally {
            extractor.destroy();
        }
    }

    @Test
    public void test_retryExhausted_5xx_throwsWithCauseAndAttemptCount() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        final AtomicInteger calls = new AtomicInteger();
        simple.setHandler(exchange -> {
            calls.incrementAndGet();
            drain(exchange.getRequestBody());
            exchange.sendResponseHeaders(503, -1);
            exchange.close();
        });
        simple.start();
        try {
            final ApiExtractor retrying = new ApiExtractor();
            retrying.setUrl("http://127.0.0.1:" + simple.port() + "/");
            retrying.setMaxRetries(2);
            retrying.setRetryBackoffMs(10L);
            retrying.init();
            try {
                retrying.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
                org.junit.jupiter.api.Assertions.fail("Expected ExtractException after 3 failed attempts");
            } catch (final ExtractException e) {
                org.junit.jupiter.api.Assertions.assertTrue(e.getMessage().contains("3 attempts"),
                        "message should contain attempt count: " + e.getMessage());
                org.junit.jupiter.api.Assertions.assertTrue(e.getMessage().contains("status=503"),
                        "message should contain status code: " + e.getMessage());
                org.junit.jupiter.api.Assertions.assertNotNull(e.getCause(), "exhaustion ExtractException must carry a cause");
                assertEquals(3, calls.get());
            } finally {
                retrying.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    @Test
    public void test_unknownHost_notRetried() throws Exception {
        final ApiExtractor extractor = new ApiExtractor();
        // .invalid is reserved per RFC 2606 — should always be unresolvable.
        extractor.setUrl("http://this-host-must-not-resolve.invalid/");
        extractor.setMaxRetries(5);
        extractor.setRetryBackoffMs(1000L);
        extractor.setConnectionTimeout(2000);
        extractor.init();
        try {
            final long t0 = System.currentTimeMillis();
            try {
                extractor.getText(new ByteArrayInputStream("uh".getBytes()), new HashMap<>());
                org.junit.jupiter.api.Assertions.fail("Expected ExtractException for unknown host");
            } catch (final ExtractException e) {
                final long elapsed = System.currentTimeMillis() - t0;
                org.junit.jupiter.api.Assertions.assertTrue(elapsed < 3000L,
                        "UnknownHost should not be retried (elapsed=" + elapsed + "ms)");
                org.junit.jupiter.api.Assertions.assertTrue(e.getMessage().contains("non-transient"),
                        "message should mark error as non-transient: " + e.getMessage());
            }
        } finally {
            extractor.destroy();
        }
    }

    @Test
    public void test_getTextAfterDestroy_throwsExtractException() throws Exception {
        final ApiExtractor extractor = new ApiExtractor();
        extractor.setUrl("http://127.0.0.1:" + port + "/");
        extractor.init();
        extractor.destroy();
        try {
            extractor.getText(new ByteArrayInputStream("post-destroy".getBytes()), new HashMap<>());
            org.junit.jupiter.api.Assertions.fail("Expected ExtractException after destroy()");
        } catch (final ExtractException e) {
            org.junit.jupiter.api.Assertions.assertTrue(e.getMessage().contains("destroyed"),
                    "message should explain why: " + e.getMessage());
        }
    }

    @Test
    public void test_destroyIsIdempotent() throws Exception {
        final ApiExtractor extractor = new ApiExtractor();
        extractor.setUrl("http://127.0.0.1:" + port + "/");
        extractor.init();
        extractor.destroy();
        extractor.destroy(); // must not throw
    }

    @Test
    public void test_setterValidation_rejectsNegative() throws Exception {
        final ApiExtractor extractor = new ApiExtractor();
        try {
            extractor.setMaxRetries(-1);
            fail();
        } catch (final IllegalArgumentException expected) {
            // ok
        }
        try {
            extractor.setMaxResponseSize(-1L);
            fail();
        } catch (final IllegalArgumentException expected) {
            // ok
        }
        try {
            extractor.setMaxRequestSize(-1L);
            fail();
        } catch (final IllegalArgumentException expected) {
            // ok
        }
        try {
            extractor.setMaxConnections(0);
            fail();
        } catch (final IllegalArgumentException expected) {
            // ok
        }
        try {
            extractor.setMaxConnectionsPerRoute(0);
            fail();
        } catch (final IllegalArgumentException expected) {
            // ok
        }
        try {
            extractor.setRetryBackoffMs(-1L);
            fail();
        } catch (final IllegalArgumentException expected) {
            // ok
        }
        try {
            extractor.setMaxRetryAfterMs(-1L);
            fail();
        } catch (final IllegalArgumentException expected) {
            // ok
        }
        try {
            extractor.setRequestSpoolThreshold(-1);
            fail();
        } catch (final IllegalArgumentException expected) {
            // ok
        }
    }

    @Test
    public void test_responseBody_emptyEntity_returnsEmptyContent() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        simple.setHandler(exchange -> {
            drain(exchange.getRequestBody());
            // Status 200 with explicit zero-length body — exercises the entity-present-but-empty path.
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        simple.start();
        try {
            final ApiExtractor extractor = new ApiExtractor();
            extractor.setUrl("http://127.0.0.1:" + simple.port() + "/");
            extractor.setMaxRetries(0);
            extractor.init();
            try {
                final ExtractData data = extractor.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
                assertNotNull(data);
                assertEquals("", data.getContent());
            } finally {
                extractor.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    @Test
    public void test_parseRetryAfter_garbageFallsBackToDefaultBackoff() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        final AtomicInteger calls = new AtomicInteger();
        simple.setHandler(exchange -> {
            drain(exchange.getRequestBody());
            final int n = calls.incrementAndGet();
            if (n == 1) {
                // Garbage Retry-After: not a number, not an HTTP-date.
                exchange.getResponseHeaders().add("Retry-After", "tomorrow-please");
                exchange.sendResponseHeaders(503, -1);
                exchange.close();
            } else {
                final byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
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
                final ExtractData data = retrying.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
                assertNotNull(data);
                assertEquals("ok", data.getContent());
                assertEquals(2, calls.get());
            } finally {
                retrying.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    @Test
    public void test_parseRetryAfter_negativeFallsBackToDefaultBackoff() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        final AtomicInteger calls = new AtomicInteger();
        simple.setHandler(exchange -> {
            drain(exchange.getRequestBody());
            final int n = calls.incrementAndGet();
            if (n == 1) {
                exchange.getResponseHeaders().add("Retry-After", "-5");
                exchange.sendResponseHeaders(503, -1);
                exchange.close();
            } else {
                final byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
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
                final ExtractData data = retrying.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
                assertNotNull(data);
                assertEquals("ok", data.getContent());
                assertEquals(2, calls.get());
            } finally {
                retrying.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    @Test
    public void test_resolveCharset_shiftJis() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        final String greeting = "こんにちは";
        final byte[] sjis = greeting.getBytes(java.nio.charset.Charset.forName("Shift_JIS"));
        simple.setHandler(exchange -> {
            drain(exchange.getRequestBody());
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=Shift_JIS");
            exchange.sendResponseHeaders(200, sjis.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(sjis);
            }
        });
        simple.start();
        try {
            final ApiExtractor extractor = new ApiExtractor();
            extractor.setUrl("http://127.0.0.1:" + simple.port() + "/");
            extractor.setMaxRetries(0);
            extractor.init();
            try {
                final ExtractData data = extractor.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
                assertNotNull(data);
                assertEquals(greeting, data.getContent());
            } finally {
                extractor.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    /**
     * Regression for the temp-file leak on the spill-then-fail path: a body that both spills to a
     * temp file (threshold below payload) AND exceeds the request-size limit (limit below payload)
     * must still have its spool file cleaned up. Previously the spool file was registered for
     * deletion only after the limit check passed, so oversize uploads leaked ~payload-sized temp
     * files forever. No HTTP call must be made for an oversize body.
     */
    @Test
    public void test_uploadSpillAndExceedsLimit_cleansUpTempFile() throws Exception {
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
            // Threshold below the payload forces a spill to a temp file; the limit below the payload
            // forces the oversize throw AFTER the body has already spilled to disk.
            capped.setRequestSpoolThreshold(10);
            capped.setMaxRequestSize(100L);
            capped.setMaxRetries(0);
            capped.init();
            try {
                final byte[] tooBig = new byte[500];
                for (int i = 0; i < tooBig.length; i++) {
                    tooBig[i] = (byte) ('a' + (i % 26));
                }
                final java.util.Set<String> before = listApiExtractorTempFiles();
                try {
                    capped.getText(new ByteArrayInputStream(tooBig), new HashMap<>());
                    fail();
                } catch (final ExtractException e) {
                    org.junit.jupiter.api.Assertions.assertTrue(e.getMessage().contains("request body exceeded limit"),
                            "message should mention request body limit: " + e.getMessage());
                }
                // Oversize body must be rejected before any HTTP call.
                assertEquals(0, calls.get());
                // FileUtil.deleteInBackground is asynchronous (TimeoutManager polls at ~1s cadence),
                // so poll for up to ~5s for the spool file to disappear.
                final long deadline = System.currentTimeMillis() + 5_000L;
                java.util.Set<String> leaked = listApiExtractorTempFiles();
                leaked.removeAll(before);
                while (!leaked.isEmpty() && System.currentTimeMillis() < deadline) {
                    Thread.sleep(100L);
                    leaked = listApiExtractorTempFiles();
                    leaked.removeAll(before);
                }
                org.junit.jupiter.api.Assertions.assertTrue(leaked.isEmpty(),
                        "spooled temp file must be deleted after oversize failure, leaked=" + leaked);
            } finally {
                capped.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    /**
     * {@link ApiExtractor#incrementWithoutOverflow(long)} must add one for normal values and must
     * saturate at {@link Long#MAX_VALUE} instead of overflowing to a negative "unbounded" value.
     */
    @Test
    public void test_incrementWithoutOverflow() throws Exception {
        assertEquals(1L, ApiExtractor.incrementWithoutOverflow(0L));
        assertEquals(101L, ApiExtractor.incrementWithoutOverflow(100L));
        assertEquals(Long.MAX_VALUE, ApiExtractor.incrementWithoutOverflow(Long.MAX_VALUE));
    }

    /**
     * Guard 2a: a thread whose interrupt flag is already set must abort the retry loop before any
     * HTTP attempt is made — even against a server that would otherwise return a retryable 500.
     */
    @Test
    public void test_preInterrupted_abortsBeforeFirstAttempt() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        final AtomicInteger calls = new AtomicInteger();
        simple.setHandler(exchange -> {
            calls.incrementAndGet();
            drain(exchange.getRequestBody());
            exchange.sendResponseHeaders(500, -1);
            exchange.close();
        });
        simple.start();
        try {
            final ApiExtractor extractor = new ApiExtractor();
            extractor.setUrl("http://127.0.0.1:" + simple.port() + "/");
            extractor.setMaxRetries(3);
            extractor.setRetryBackoffMs(10L);
            extractor.init();
            try {
                Thread.currentThread().interrupt();
                try {
                    extractor.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
                    fail();
                } catch (final ExtractException expected) {
                    // expected: guard 2a short-circuits before any HTTP attempt
                }
                assertEquals(0, calls.get());
            } finally {
                // Drain the interrupt flag so it does not leak into subsequent tests.
                Thread.interrupted();
                extractor.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    /**
     * Guard 2c: an already-interrupted thread must abort {@link ApiExtractor#sleepQuietly(long)}
     * even for a zero or negative backoff, so an interrupted retry loop configured with
     * {@code retryBackoffMs=0} cannot spin through further attempts. Uses {@code isInterrupted()}
     * so the flag is preserved across the successive calls.
     */
    @Test
    public void test_sleepQuietly_preInterrupted_zeroOrNegativeBackoffStillAborts() throws Exception {
        final ApiExtractor extractor = new ApiExtractor();
        extractor.setUrl("http://127.0.0.1:1/unused");
        extractor.init();
        try {
            Thread.currentThread().interrupt();
            try {
                extractor.sleepQuietly(0L);
                fail();
            } catch (final ExtractException expected) {
                // ok
            }
            org.junit.jupiter.api.Assertions.assertTrue(Thread.currentThread().isInterrupted(),
                    "interrupt flag must remain set after a zero-backoff abort");
            try {
                extractor.sleepQuietly(-1L);
                fail();
            } catch (final ExtractException expected) {
                // ok
            }
            try {
                extractor.sleepQuietly(5L);
                fail();
            } catch (final ExtractException expected) {
                // ok
            }
        } finally {
            // Drain the interrupt flag so it does not leak into subsequent tests.
            Thread.interrupted();
            extractor.destroy();
        }
    }

    /**
     * No-regression proof for the interrupt fix: an ordinary {@link java.net.SocketTimeoutException}
     * (a subclass of {@link java.io.InterruptedIOException}) is a transient read-timeout and MUST
     * still be retried. The interrupt flag is left unset, matching a real {@code soTimeout}. Uses a
     * subclass that throws from {@code executeOnce} so the retry classification is exercised
     * deterministically without depending on server timing.
     */
    @Test
    public void test_socketTimeout_isRetriedAsTransient() throws Exception {
        final AtomicInteger attempts = new AtomicInteger();
        final ApiExtractor extractor = new ApiExtractor() {
            @Override
            protected ExtractData executeOnce(final DeferredFileOutputStream dfos, final String targetUrl, final int attempt)
                    throws IOException {
                attempts.incrementAndGet();
                throw new java.net.SocketTimeoutException("read timed out");
            }
        };
        extractor.setUrl("http://127.0.0.1:1/unused");
        extractor.setMaxRetries(2);
        extractor.setRetryBackoffMs(1L);
        extractor.init();
        try {
            try {
                extractor.getText(new ByteArrayInputStream("x".getBytes()), new HashMap<>());
                fail();
            } catch (final ExtractException expected) {
                // expected after exhausting retries
            }
            // maxRetries=2 => 3 total attempts. Proves SocketTimeout is treated as transient, not as
            // a genuine interrupt (which would abort after the first attempt).
            assertEquals(3, attempts.get());
        } finally {
            extractor.destroy();
        }
    }

    /**
     * Wire-format regression: the {@code filedata} multipart part must carry {@code name="filedata"}
     * and MUST NOT carry a {@code filename=} attribute, matching the pre-PR two-argument
     * {@code addBinaryBody} form. Captures the raw multipart request body to assert on the header.
     */
    @Test
    public void test_multipartWireFormat_hasNoFilename() throws Exception {
        final SimpleHttpServer simple = new SimpleHttpServer();
        final java.util.concurrent.atomic.AtomicReference<byte[]> received = new java.util.concurrent.atomic.AtomicReference<>();
        simple.setHandler(exchange -> {
            final java.io.ByteArrayOutputStream sink = new java.io.ByteArrayOutputStream();
            final byte[] buf = new byte[4096];
            int n;
            while ((n = exchange.getRequestBody().read(buf)) >= 0) {
                sink.write(buf, 0, n);
            }
            received.set(sink.toByteArray());
            final byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        simple.start();
        try {
            final ApiExtractor extractor = new ApiExtractor();
            extractor.setUrl("http://127.0.0.1:" + simple.port() + "/");
            extractor.setMaxRetries(0);
            extractor.init();
            try {
                final ExtractData data = extractor.getText(new ByteArrayInputStream("payload".getBytes()), new HashMap<>());
                assertNotNull(data);
                final byte[] body = received.get();
                assertNotNull(body);
                // Decode byte-for-byte so multipart framing survives intact for the header assertions.
                final String wire = new String(body, StandardCharsets.ISO_8859_1);
                org.junit.jupiter.api.Assertions.assertTrue(wire.contains("name=\"filedata\""),
                        "multipart part must carry the filedata field name");
                org.junit.jupiter.api.Assertions.assertFalse(wire.contains("filename="),
                        "multipart part must NOT carry a filename (matches pre-PR wire format)");
            } finally {
                extractor.destroy();
            }
        } finally {
            simple.stop();
        }
    }

    private static java.util.Set<String> listApiExtractorTempFiles() {
        final File dir = org.apache.commons.lang3.SystemUtils.getJavaIoTmpDir();
        final File[] files = dir.listFiles((d, name) -> name.startsWith("apiExtractor-") && name.endsWith(".tmp"));
        final java.util.Set<String> names = new java.util.HashSet<>();
        if (files != null) {
            for (final File f : files) {
                names.add(f.getName());
            }
        }
        return names;
    }

    private static void drain(final InputStream in) throws IOException {
        final byte[] buf = new byte[4096];
        while (in.read(buf) >= 0) {
            // discard
        }
    }

    private static int indexOf(final byte[] haystack, final byte[] needle) {
        if (needle.length == 0 || haystack.length < needle.length) {
            return -1;
        }
        outer: for (int i = 0; i <= haystack.length - needle.length; i++) {
            for (int j = 0; j < needle.length; j++) {
                if (haystack[i + j] != needle[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
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
