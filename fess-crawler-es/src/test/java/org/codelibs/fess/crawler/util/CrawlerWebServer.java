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
package org.codelibs.fess.crawler.util;

import java.io.File;

import org.codelibs.core.io.FileUtil;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.log.Log;

/**
 * @author shinsuke
 * 
 */
public class CrawlerWebServer {
    private final File docRoot;

    private final Server server;

    private boolean tempDocRoot = false;

    public CrawlerWebServer(final int port) {
        this(port, createDocRoot(3));
        tempDocRoot = true;
    }

    public CrawlerWebServer(final int port, final File docRoot) {
        this.docRoot = docRoot;

        server = new Server(port);

        final ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setWelcomeFiles(new String[] { "index.html" });
        resource_handler.setResourceBase(docRoot.getAbsolutePath());
        Log.info("serving " + resource_handler.getBaseResource());
        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
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
        } finally {
            if (tempDocRoot) {
                docRoot.delete();
            }
        }
    }

    protected static File createDocRoot(final int count) {
        try {
            final File tempDir = File.createTempFile("crawlerDocRoot", "");
            tempDir.delete();
            tempDir.mkdirs();

            final StringBuilder buf = new StringBuilder();
            buf.append("User-agent: *").append('\n');
            buf.append("Disallow: /admin/").append('\n');
            buf.append("Disallow: /websvn/").append('\n');
            final File robotTxtFile = new File(tempDir, "robots.txt");
            FileUtil.writeBytes(robotTxtFile.getAbsolutePath(), buf.toString().getBytes("UTF-8"));
            robotTxtFile.deleteOnExit();

            generateContents(tempDir, count);

            return tempDir;
        } catch (final Exception e) {
            throw new CrawlerSystemException(e);
        }
    }

    private static void generateContents(final File dir, final int count) throws Exception {
        if (count <= 0) {
            return;
        }

        final String content = getHtmlContent(count);

        final File indexFile = new File(dir, "index.html");
        indexFile.deleteOnExit();
        FileUtil.writeBytes(indexFile.getAbsolutePath(), content.getBytes("UTF-8"));

        for (int i = 1; i <= 10; i++) {
            final File file = new File(dir, "file" + count + "-" + i + ".html");
            file.deleteOnExit();
            FileUtil.writeBytes(file.getAbsolutePath(), content.getBytes("UTF-8"));
            final File childDir = new File(dir, "dir" + count + "-" + i);
            childDir.mkdirs();
            generateContents(childDir, count - 1);
        }
    }

    private static String getHtmlContent(final int count) {
        final StringBuilder buf = new StringBuilder();
        buf.append("<html><head><title>Title ");
        buf.append(count);
        buf.append("</title></head><body><h1>Content ");
        buf.append(count);
        buf.append("</h1><br>");
        buf.append("<a href=\"index.html\">Index</a><br>");
        for (int i = 1; i <= 10; i++) {
            buf.append("<a href=\"file");
            buf.append(count);
            buf.append("-");
            buf.append(i);
            buf.append(".html\">File ");
            buf.append(count);
            buf.append("-");
            buf.append(i);
            buf.append("</a><br>");
            buf.append("<a href=\"dir");
            buf.append(count);
            buf.append("-");
            buf.append(i);
            buf.append("/index.html\">Dir ");
            buf.append(count);
            buf.append("-");
            buf.append(i);
            buf.append("</a><br>");
        }
        buf.append("</body></html>");
        return buf.toString();
    }
}
