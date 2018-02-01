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
package org.codelibs.fess.crawler.helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.SitemapFile;
import org.codelibs.fess.crawler.entity.SitemapSet;
import org.codelibs.fess.crawler.entity.SitemapUrl;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.SitemapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author shinsuke
 *
 */
public class SitemapsHelper {
    private static final Logger logger = LoggerFactory
            .getLogger(SitemapsHelper.class);

    public int preloadSize = 512;

    public boolean isValid(final InputStream in) {
        return isValid(in, true);
    }

    protected boolean isValid(final InputStream in, final boolean recursive) {
        final BufferedInputStream bis = new BufferedInputStream(in);
        bis.mark(preloadSize);

        final byte[] bytes = new byte[preloadSize];
        try {
            if (bis.read(bytes) == -1) {
                return false;
            }

            final String preloadDate = new String(bytes, Constants.UTF_8);
            if (preloadDate.indexOf("<urlset") >= 0) {
                // XML Sitemaps
                return true;
            } else if (preloadDate.indexOf("<sitemapindex") >= 0) {
                // XML Sitemaps Index
                return true;
            } else if (preloadDate.startsWith("http://")
                    || preloadDate.startsWith("https://")) {
                // Text Sitemaps Index
                return true;
            } else {
                // gz
                bis.reset();
                return isValid(new GZIPInputStream(bis), false);
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to validate a file.", e);
            }
        }
        return false;
    }

    /**
     * Generates SitemapSet instance.
     *
     * This method does not close the input stream.
     *
     * @param in Input stream for a sitemap
     * @return a sitemap set
     */
    public SitemapSet parse(final InputStream in) {
        return parse(in, true);
    }

    protected SitemapSet parse(final InputStream in, final boolean recursive) {
        final BufferedInputStream bis = new BufferedInputStream(in);
        bis.mark(preloadSize);

        String preloadDate = StringUtil.EMPTY;
        final byte[] bytes = new byte[preloadSize];
        try {
            if (bis.read(bytes) == -1) {
                throw new CrawlingAccessException("No sitemaps data.");
            }

            preloadDate = new String(bytes, Constants.UTF_8);
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
        } catch (final CrawlingAccessException e) {
            throw e;
        } catch (final Exception e) {
            throw new CrawlingAccessException("Could not parse Sitemaps: " + preloadDate, e);
        }
    }

    protected SitemapSet parseTextSitemaps(final InputStream in) {
        final SitemapSet sitemapSet = new SitemapSet();
        sitemapSet.setType(SitemapSet.URLSET);

        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(
                    in, Constants.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                final String url = line.trim();
                if (StringUtil.isNotBlank(url)
                        && (url.startsWith("http://") || url
                                .startsWith("https://"))) {
                    final SitemapUrl sitemapUrl = new SitemapUrl();
                    sitemapUrl.setLoc(url);
                    sitemapSet.addSitemap(sitemapUrl);
                }
            }
            return sitemapSet;
        } catch (final Exception e) {
            throw new SitemapsException("Could not parse Text Sitemaps.",
                    e);
        }

    }

    protected SitemapSet parseXmlSitemaps(final InputStream in) {
        final XmlSitemapsHandler handler = new XmlSitemapsHandler();
        try {
            final SAXParserFactory spfactory = SAXParserFactory.newInstance();
            final SAXParser parser = spfactory.newSAXParser();
            parser.parse(in, handler);
        } catch (final Exception e) {
            throw new SitemapsException("Could not parse XML Sitemaps.", e);
        }
        return handler.getSitemapSet();
    }

    protected static class XmlSitemapsHandler extends DefaultHandler {

        private static final String PRIORITY_ELEMENT = "priority";

        private static final String CHANGEFREQ_ELEMENT = "changefreq";

        private static final String LASTMOD_ELEMENT = "lastmod";

        private static final String LOC_ELEMENT = "loc";

        private static final String URL_ELEMENT = "url";

        private SitemapSet sitemapSet;

        private SitemapUrl sitemapUrl;

        private StringBuilder buf;

        @Override
        public void startDocument() {
            sitemapSet = new SitemapSet();
            sitemapSet.setType(SitemapSet.URLSET);
        }

        @Override
        public void startElement(final String uri, final String localName,
                final String qName, final Attributes attributes) {
            if (URL_ELEMENT.equals(qName)) {
                sitemapUrl = new SitemapUrl();
            } else if (LOC_ELEMENT.equals(qName)) {
                buf = new StringBuilder();
            } else if (LASTMOD_ELEMENT.equals(qName)) {
                buf = new StringBuilder();
            } else if (CHANGEFREQ_ELEMENT.equals(qName)) {
                buf = new StringBuilder();
            } else if (PRIORITY_ELEMENT.equals(qName)) {
                buf = new StringBuilder();
            }
        }

        @Override
        public void characters(final char[] ch, final int offset,
                final int length) {
            if (buf != null) {
                buf.append(new String(ch, offset, length));
            }
        }

        @Override
        public void endElement(final String uri, final String localName,
                final String qName) {
            if (URL_ELEMENT.equals(qName)) {
                if (sitemapUrl != null) {
                    sitemapSet.addSitemap(sitemapUrl);
                }
                sitemapUrl = null;
            } else if (LOC_ELEMENT.equals(qName)) {
                if (buf != null) {
                    sitemapUrl.setLoc(buf.toString().trim());
                    buf = null;
                }
            } else if (LASTMOD_ELEMENT.equals(qName)) {
                if (buf != null) {
                    sitemapUrl.setLastmod(buf.toString().trim());
                    buf = null;
                }
            } else if (CHANGEFREQ_ELEMENT.equals(qName)) {
                if (buf != null) {
                    sitemapUrl.setChangefreq(buf.toString().trim());
                    buf = null;
                }
            } else if (PRIORITY_ELEMENT.equals(qName)) {
                if (buf != null) {
                    sitemapUrl.setPriority(buf.toString().trim());
                    buf = null;
                }
            }
        }

        @Override
        public void endDocument() {
            // nothing
        }

        public SitemapSet getSitemapSet() {
            return sitemapSet;
        }

    }

    protected SitemapSet parseXmlSitemapsIndex(final InputStream in) {
        final XmlSitemapsIndexHandler handler = new XmlSitemapsIndexHandler();
        try {
            final SAXParserFactory spfactory = SAXParserFactory.newInstance();
            final SAXParser parser = spfactory.newSAXParser();
            parser.parse(in, handler);
        } catch (final Exception e) {
            throw new SitemapsException(
                    "Could not parse XML Sitemaps Index.", e);
        }
        return handler.getSitemapSet();
    }

    protected static class XmlSitemapsIndexHandler extends DefaultHandler {

        private SitemapSet sitemapSet;

        private SitemapFile sitemapFile;

        private StringBuilder buf;

        @Override
        public void startDocument() {
            sitemapSet = new SitemapSet();
            sitemapSet.setType(SitemapSet.INDEX);
        }

        @Override
        public void startElement(final String uri, final String localName,
                final String qName, final Attributes attributes) {
            if ("sitemap".equals(qName)) {
                sitemapFile = new SitemapFile();
            } else if ("loc".equals(qName)) {
                buf = new StringBuilder();
            } else if ("lastmod".equals(qName)) {
                buf = new StringBuilder();
            }
        }

        @Override
        public void characters(final char[] ch, final int offset,
                final int length) {
            if (buf != null) {
                buf.append(new String(ch, offset, length));
            }
        }

        @Override
        public void endElement(final String uri, final String localName,
                final String qName) {
            if ("sitemap".equals(qName)) {
                if (sitemapFile != null) {
                    sitemapSet.addSitemap(sitemapFile);
                }
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

        @Override
        public void endDocument() {
            // nothing
        }

        public SitemapSet getSitemapSet() {
            return sitemapSet;
        }

    }
}
