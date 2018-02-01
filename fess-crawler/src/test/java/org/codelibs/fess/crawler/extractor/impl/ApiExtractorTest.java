/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.HttpMethods;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;

/**
 * @author shinsuke
 * 
 */
public class ApiExtractorTest extends PlainTestCase {
    final int port = 8080;

    final String ATTR_NAME = "filedata";

    private TestApiExtractorServer server;

    private ApiExtractor extractor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        server = new TestApiExtractorServer(port);
        server.start();

        extractor = new ApiExtractor();
        extractor.setUrl("http://127.0.0.1:8080/");
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

            final RequestHandlerImpl request_handler = new RequestHandlerImpl();
            final HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[] { request_handler,
                    new DefaultHandler() });
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

        private static class RequestHandlerImpl extends AbstractHandler {
            public static final String MULTIPART_FORMDATA_TYPE = "multipart/form-data";

            public static boolean isMultipartRequest(HttpServletRequest request) {
                return request.getContentType() != null
                        && request.getContentType().startsWith(MULTIPART_FORMDATA_TYPE);
            }

            @Override
            public void handle(String target, HttpServletRequest request,
                    HttpServletResponse response, int dispatch)
                    throws java.io.IOException, ServletException {
                
                if(!isMultipartRequest(request) || !request.getMethod().equals(HttpMethods.POST)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST);
                    HttpConnection.getCurrentConnection().getRequest().setHandled(true);
                    return;
                }
                try {
                    List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory())
                            .parseRequest(request);
                    for (FileItem item : multiparts) {
                        if (!item.isFormField()) {
                            // item is not form field.
                        } else {
                            String name = item.getFieldName();
                            String value = item.getString();
                            response.getWriter().write(name + "," + value);
                        }
                    }
                } catch (FileUploadException e) {
                    e.printStackTrace();
                }
                HttpConnection.getCurrentConnection().getRequest().setHandled(true);
            }
        }
    }
}
