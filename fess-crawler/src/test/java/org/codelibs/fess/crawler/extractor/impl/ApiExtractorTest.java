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
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.Callback;

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
    protected void setUp() throws Exception {
        super.setUp();

        server = new TestApiExtractorServer(port);
        server.start();

        extractor = new ApiExtractor();
        extractor.setUrl("http://127.0.0.1:" + port + "/");
        extractor.init();
    }

    @Override
    protected void tearDown() throws Exception {
        server.stop();
        extractor.destroy();

        super.tearDown();
    }

    public void test_getText() throws Exception {
        final String testStr = "testdata";
        final String content = ATTR_NAME + "," + testStr;
        final Map<String, String> params = new HashMap<String, String>();
        //final ExtractData text = extractor.getText(new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(filePath))), params);
        final ExtractData text = extractor.getText(new ByteArrayInputStream(testStr.getBytes()), params);
        assertEquals(content, text.getContent());
    }

    // TODO other tests

    static class TestApiExtractorServer {
        private Server server;

        public TestApiExtractorServer(final int port) {
            server = new Server(port);

            final RequestHandlerImpl requestHandler = new RequestHandlerImpl();
            final HandlerList handlers = new HandlerList();
            handlers.addHandler(requestHandler);
            handlers.addHandler(new DefaultHandler());
            server.setHandler(handlers);
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
                                    String value = part.substring(valueStart).trim();
                                    // Remove trailing boundary markers
                                    if (value.endsWith("--")) {
                                        value = value.substring(0, value.length() - 2).trim();
                                    }
                                    if (value.endsWith("\r\n")) {
                                        value = value.substring(0, value.length() - 2);
                                    }
                                    if (value.endsWith("\n")) {
                                        value = value.substring(0, value.length() - 1);
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
