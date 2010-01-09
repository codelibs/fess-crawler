/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.robot.util;

import java.io.File;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.log.Log;
import org.seasar.framework.util.FileUtil;
import org.seasar.robot.RobotSystemException;

/**
 * @author shinsuke
 *
 */
public class S2RobotWebServer {
    private int port = 8080;

    private File docRoot;

    private Server server;

    private boolean tempDocRoot = false;

    public S2RobotWebServer(int port) {
        this(port, createDocRoot(3));
        tempDocRoot = true;
    }

    public S2RobotWebServer(int port, File docRoot) {
        this.port = port;
        this.docRoot = docRoot;

        server = new Server(port);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setWelcomeFiles(new String[] { "index.html" });
        resource_handler.setResourceBase(docRoot.getAbsolutePath());
        Log.info("serving " + resource_handler.getBaseResource());
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler,
                new DefaultHandler() });
        server.setHandler(handlers);
    }

    public void start() {
        try {
            server.start();
        } catch (Exception e) {
            throw new RobotSystemException(e);
        }
    }

    public void stop() {
        try {
            server.stop();
            server.join();
        } catch (Exception e) {
            throw new RobotSystemException(e);
        } finally {
            if (tempDocRoot) {
                docRoot.delete();
            }
        }
    }

    protected static File createDocRoot(int count) {
        try {
            File tempDir = File.createTempFile("robotDocRoot", "");
            tempDir.delete();
            tempDir.mkdirs();

            // robots.txt
            StringBuilder buf = new StringBuilder();
            buf.append("User-agent: *").append('\n');
            buf.append("Disallow: /admin/").append('\n');
            buf.append("Disallow: /websvn/").append('\n');
            File robotTxtFile = new File(tempDir, "robots.txt");
            FileUtil.write(robotTxtFile.getAbsolutePath(), buf.toString()
                    .getBytes("UTF-8"));
            robotTxtFile.deleteOnExit();

            // sitemaps.xml
            buf = new StringBuilder();
            buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(
                    '\n');
            buf.append("<urlset ").append(
                    "xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">")
                    .append('\n');
            buf.append("<url>").append('\n');
            buf.append("<loc>http://localhost:7070/index.html</loc>").append(
                    '\n');
            buf.append("<loc>http://localhost:7070/file").append(count).append(
                    "-1.html").append("</loc>").append('\n');
            buf.append("</url>").append('\n');
            buf.append("</urlset>").append('\n');
            File sitemapsFile = new File(tempDir, "sitemaps.xml");
            FileUtil.write(sitemapsFile.getAbsolutePath(), buf.toString()
                    .getBytes("UTF-8"));
            robotTxtFile.deleteOnExit();

            // sitemaps.txt
            buf = new StringBuilder();
            buf.append("http://localhost:7070/index.html").append('\n');
            buf.append("http://localhost:7070/file").append(count).append(
                    "-1.html").append('\n');
            sitemapsFile = new File(tempDir, "sitemaps.txt");
            FileUtil.write(sitemapsFile.getAbsolutePath(), buf.toString()
                    .getBytes("UTF-8"));
            robotTxtFile.deleteOnExit();

            generateContents(tempDir, count);

            return tempDir;
        } catch (Exception e) {
            throw new RobotSystemException(e);
        }
    }

    private static void generateContents(File dir, int count) throws Exception {
        if (count <= 0) {
            return;
        }

        String content = getHtmlContent(count);

        File indexFile = new File(dir, "index.html");
        indexFile.deleteOnExit();
        FileUtil.write(indexFile.getAbsolutePath(), content.getBytes("UTF-8"));

        for (int i = 1; i <= 10; i++) {
            File file = new File(dir, "file" + count + "-" + i + ".html");
            file.deleteOnExit();
            FileUtil.write(file.getAbsolutePath(), content.getBytes("UTF-8"));
            File childDir = new File(dir, "dir" + count + "-" + i);
            childDir.mkdirs();
            generateContents(childDir, count - 1);
        }
    }

    private static String getHtmlContent(int count) {
        StringBuilder buf = new StringBuilder();
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
