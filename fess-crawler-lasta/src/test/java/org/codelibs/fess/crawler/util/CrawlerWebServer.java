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
package org.codelibs.fess.crawler.util;

import java.io.File;
import java.nio.file.Path;

import org.codelibs.core.io.FileUtil;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class CrawlerWebServer {
    private static final Logger logger = LoggerFactory.getLogger(CrawlerWebServer.class);

    private File docRoot;

    private Server server;

    private boolean tempDocRoot = false;

    public CrawlerWebServer(final int port) {
        this(port, createDocRoot(3, port));
        tempDocRoot = true;
    }

    public CrawlerWebServer(final int port, final File docRoot) {
        this.docRoot = docRoot;

        server = new Server(port);

        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setWelcomeFiles("index.html");
        resourceHandler.setBaseResource(ResourceFactory.of(resourceHandler).newResource(Path.of(docRoot.getAbsolutePath())));
        logger.info("serving {}", docRoot.getAbsolutePath());
        server.setHandler(new Handler.Sequence(resourceHandler, new DefaultHandler()));
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

    /**
     * Get the actual port the server is listening on.
     * This is useful when starting the server with port 0 to get a random available port.
     *
     * @return the actual port number
     */
    public int getPort() {
        return ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }

    /**
     * Update the port in sitemaps files after the server has started.
     * This is needed when using port 0 (random port) because the actual port
     * is not known until the server starts.
     */
    public void updateSitemapsPort() {
        final int actualPort = getPort();
        try {
            // Update sitemaps.xml
            final File sitemapsXmlFile = new File(docRoot, "sitemaps.xml");
            if (sitemapsXmlFile.exists()) {
                String content = new String(java.nio.file.Files.readAllBytes(sitemapsXmlFile.toPath()), "UTF-8");
                content = content.replaceAll("localhost:\\d+", "localhost:" + actualPort);
                java.nio.file.Files.write(sitemapsXmlFile.toPath(), content.getBytes("UTF-8"));
            }

            // Update sitemaps.txt
            final File sitemapsTxtFile = new File(docRoot, "sitemaps.txt");
            if (sitemapsTxtFile.exists()) {
                String content = new String(java.nio.file.Files.readAllBytes(sitemapsTxtFile.toPath()), "UTF-8");
                content = content.replaceAll("localhost:\\d+", "localhost:" + actualPort);
                java.nio.file.Files.write(sitemapsTxtFile.toPath(), content.getBytes("UTF-8"));
            }
        } catch (final Exception e) {
            throw new CrawlerSystemException(e);
        }
    }

    protected static File createDocRoot(final int count, final int port) {
        try {
            final File tempDir = File.createTempFile("crawlerDocRoot", "");
            tempDir.delete();
            tempDir.mkdirs();

            // robots.txt
            StringBuilder buf = new StringBuilder();
            buf.append("User-agent: *").append('\n');
            buf.append("Disallow: /admin/").append('\n');
            buf.append("Disallow: /websvn/").append('\n');
            final File robotTxtFile = new File(tempDir, "robots.txt");
            FileUtil.writeBytes(robotTxtFile.getAbsolutePath(), buf.toString().getBytes("UTF-8"));
            robotTxtFile.deleteOnExit();

            // sitemaps.xml
            buf = new StringBuilder();
            buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append('\n');
            buf.append("<urlset ").append("xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">").append('\n');
            buf.append("<url>").append('\n');
            buf.append("<loc>http://localhost:").append(port).append("/index.html</loc>").append('\n');
            buf.append("<loc>http://localhost:").append(port).append("/file").append(count).append("-1.html").append("</loc>").append('\n');
            buf.append("</url>").append('\n');
            buf.append("</urlset>").append('\n');
            File sitemapsFile = new File(tempDir, "sitemaps.xml");
            FileUtil.writeBytes(sitemapsFile.getAbsolutePath(), buf.toString().getBytes("UTF-8"));
            robotTxtFile.deleteOnExit();

            // sitemaps.txt
            buf = new StringBuilder();
            buf.append("http://localhost:").append(port).append("/index.html").append('\n');
            buf.append("http://localhost:").append(port).append("/file").append(count).append("-1.html").append('\n');
            sitemapsFile = new File(tempDir, "sitemaps.txt");
            FileUtil.writeBytes(sitemapsFile.getAbsolutePath(), buf.toString().getBytes("UTF-8"));
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
