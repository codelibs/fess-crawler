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
import java.util.HashMap;
import java.util.Map;
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
