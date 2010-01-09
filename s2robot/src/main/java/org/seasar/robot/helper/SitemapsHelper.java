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
package org.seasar.robot.helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.seasar.framework.util.StringUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSitemapsException;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.SitemapFile;
import org.seasar.robot.entity.SitemapSet;
import org.seasar.robot.entity.SitemapUrl;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author shinsuke
 *
 */
public class SitemapsHelper {

    public int preloadSize = 512;

    /**
     * Generates SitemapSet instance.
     * 
     * This method does not close the input stream.
     * 
     * @param in
     * @return
     */
    public SitemapSet parse(InputStream in) {
        return parse(in, true);
    }

    protected SitemapSet parse(InputStream in, boolean recursive) {
        BufferedInputStream bis = new BufferedInputStream(in);
        bis.mark(preloadSize);

        byte[] bytes = new byte[preloadSize];
        try {
            if (bis.read(bytes) == -1) {
                throw new RobotSitemapsException("No sitemaps data.");
            }

            String preloadDate = new String(bytes, Constants.UTF_8);
            if (preloadDate.indexOf("<urlset") >= 0) {
                // XML Sitemaps
                bis.reset();
                return parseXmlSitemaps(bis);
            } else if (preloadDate.indexOf("<sitemapindex") >= 0) {
                // XML Sitemaps Index
                bis.reset();
                return parseXmlSitemapsIndex(bis);
            } else if (preloadDate.startsWith("http://")
                    || preloadDate.startsWith("https://")) {
                // Text Sitemaps Index
                bis.reset();
                return parseTextSitemaps(bis);
            } else {
                // gz
                bis.reset();
                return parse(new GZIPInputStream(bis), false);
            }
        } catch (RobotSystemException e) {
            throw e;
        } catch (Exception e) {
            throw new RobotSitemapsException("Could not parse Sitemaps.", e);
        }
    }

    protected SitemapSet parseTextSitemaps(InputStream in) {
        SitemapSet sitemapSet = new SitemapSet();
        sitemapSet.setType(SitemapSet.URLSET);

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in,
                    Constants.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                String url = line.trim();
                if (StringUtil.isNotBlank(url)
                        && (url.startsWith("http://") || url
                                .startsWith("https://"))) {
                    SitemapUrl sitemapUrl = new SitemapUrl();
                    sitemapUrl.setLoc(url);
                    sitemapSet.addSitemap(sitemapUrl);
                }
            }
            return sitemapSet;
        } catch (Exception e) {
            throw new RobotSitemapsException("Could not parse Text Sitemaps.",
                    e);
        }

    }

    protected SitemapSet parseXmlSitemaps(InputStream in) {
        XmlSitemapsHandler handler = new XmlSitemapsHandler();
        try {
            SAXParserFactory spfactory = SAXParserFactory.newInstance();
            SAXParser parser = spfactory.newSAXParser();
            parser.parse(in, handler);
        } catch (Exception e) {
            throw new RobotSitemapsException("Could not parse XML Sitemaps.", e);
        }
        return handler.getSitemapSet();
    }

    protected static class XmlSitemapsHandler extends DefaultHandler {

        private SitemapSet sitemapSet;

        private SitemapUrl sitemapUrl;

        private StringBuilder buf;

        public void startDocument() {
            sitemapSet = new SitemapSet();
            sitemapSet.setType(SitemapSet.URLSET);
        }

        public void startElement(String uri, String localName, String qName,
                Attributes attributes) {
            if ("url".equals(qName)) {
                sitemapUrl = new SitemapUrl();
            } else if ("loc".equals(qName)) {
                buf = new StringBuilder();
            } else if ("lastmod".equals(qName)) {
                buf = new StringBuilder();
            } else if ("changefreq".equals(qName)) {
                buf = new StringBuilder();
            } else if ("priority".equals(qName)) {
                buf = new StringBuilder();
            }
        }

        public void characters(char[] ch, int offset, int length) {
            if (buf != null) {
                buf.append(new String(ch, offset, length));
            }
        }

        public void endElement(String uri, String localName, String qName) {
            if ("url".equals(qName)) {
                sitemapSet.addSitemap(sitemapUrl);
                sitemapUrl = null;
            } else if ("loc".equals(qName)) {
                if (buf != null) {
                    sitemapUrl.setLoc(buf.toString().trim());
                    buf = null;
                }
            } else if ("lastmod".equals(qName)) {
                if (buf != null) {
                    sitemapUrl.setLastmod(buf.toString().trim());
                    buf = null;
                }
            } else if ("changefreq".equals(qName)) {
                if (buf != null) {
                    sitemapUrl.setChangefreq(buf.toString().trim());
                    buf = null;
                }
            } else if ("priority".equals(qName)) {
                if (buf != null) {
                    sitemapUrl.setPriority(buf.toString().trim());
                    buf = null;
                }
            }
        }

        public void endDocument() {

        }

        public SitemapSet getSitemapSet() {
            return sitemapSet;
        }

    }

    protected SitemapSet parseXmlSitemapsIndex(InputStream in) {
        XmlSitemapsIndexHandler handler = new XmlSitemapsIndexHandler();
        try {
            SAXParserFactory spfactory = SAXParserFactory.newInstance();
            SAXParser parser = spfactory.newSAXParser();
            parser.parse(in, handler);
        } catch (Exception e) {
            throw new RobotSitemapsException(
                    "Could not parse XML Sitemaps Index.", e);
        }
        return handler.getSitemapSet();
    }

    protected static class XmlSitemapsIndexHandler extends DefaultHandler {

        private SitemapSet sitemapSet;

        private SitemapFile sitemapFile;

        private StringBuilder buf;

        public void startDocument() {
            sitemapSet = new SitemapSet();
            sitemapSet.setType(SitemapSet.INDEX);
        }

        public void startElement(String uri, String localName, String qName,
                Attributes attributes) {
            if ("sitemap".equals(qName)) {
                sitemapFile = new SitemapFile();
            } else if ("loc".equals(qName)) {
                buf = new StringBuilder();
            } else if ("lastmod".equals(qName)) {
                buf = new StringBuilder();
            }
        }

        public void characters(char[] ch, int offset, int length) {
            if (buf != null) {
                buf.append(new String(ch, offset, length));
            }
        }

        public void endElement(String uri, String localName, String qName) {
            if ("sitemap".equals(qName)) {
                sitemapSet.addSitemap(sitemapFile);
                sitemapFile = null;
            } else if ("loc".equals(qName)) {
                if (buf != null) {
                    sitemapFile.setLoc(buf.toString().trim());
                    buf = null;
                }
            } else if ("lastmod".equals(qName)) {
                if (buf != null) {
                    sitemapFile.setLastmod(buf.toString().trim());
                    buf = null;
                }
            }
        }

        public void endDocument() {

        }

        public SitemapSet getSitemapSet() {
            return sitemapSet;
        }

    }
}
