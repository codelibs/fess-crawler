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
package org.codelibs.fess.crawler.helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.SitemapFile;
import org.codelibs.fess.crawler.entity.SitemapSet;
import org.codelibs.fess.crawler.entity.SitemapUrl;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.SitemapsException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Helper class for parsing and validating sitemaps.
 * It supports XML sitemaps, XML sitemap indexes, and text sitemaps,
 * and can handle GZIP compressed sitemaps.
 * The class provides methods to check if an input stream is a valid sitemap,
 * and to parse an input stream into a {@link SitemapSet} object.
 * It uses SAX parser for XML sitemaps and XML sitemap indexes,
 * and handles potential exceptions during parsing.
 * The class also includes inner classes for handling XML sitemap and sitemap index parsing.
 */
public class SitemapsHelper {
    private static final Logger logger = LogManager.getLogger(SitemapsHelper.class);

    /** The size of the preload buffer for checking file format. */
    protected int preloadSize = 512;

    /**
     * Creates a new SitemapsHelper instance.
     */
    public SitemapsHelper() {
        // Default constructor
    }

    /**
     * Checks if the given input stream contains valid sitemap data.
     * @param in the input stream to validate
     * @return true if the stream contains valid sitemap data, false otherwise
     */
    public boolean isValid(final InputStream in) {
        return isValid(in, true);
    }

    /**
     * Checks if the given input stream contains valid sitemap data.
     * @param in the input stream to validate
     * @param recursive whether to recursively check compressed files
     * @return true if the stream contains valid sitemap data, false otherwise
     */
    protected boolean isValid(final InputStream in, final boolean recursive) {
        final BufferedInputStream bis = new BufferedInputStream(in);
        bis.mark(preloadSize);

        final byte[] bytes = new byte[preloadSize];
        try {
            if (bis.read(bytes) == -1) {
                return false;
            }

            final String preloadDate = new String(bytes, Constants.UTF_8);
            if (preloadDate.indexOf("<urlset") >= 0 || preloadDate.indexOf("<sitemapindex") >= 0 || preloadDate.startsWith("http://")
                    || preloadDate.startsWith("https://")) {
                // XML Sitemaps
                return true;
            }
            // gz
            bis.reset();
            return isValid(new GZIPInputStream(bis), false);
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

    /**
     * Parses a sitemap from the given input stream.
     * @param in the input stream to parse
     * @param recursive whether to recursively parse compressed files
     * @return the parsed sitemap set
     */
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
            }
            if (preloadDate.indexOf("<sitemapindex") >= 0) {
                // XML Sitemaps Index
                bis.reset();
                return parseXmlSitemapsIndex(bis);
            }
            if (preloadDate.startsWith("http://") || preloadDate.startsWith("https://")) {
                // Text Sitemaps Index
                bis.reset();
                return parseTextSitemaps(bis);
            }
            // gz
            bis.reset();
            return parse(new GZIPInputStream(bis), false);
        } catch (final CrawlingAccessException e) {
            throw e;
        } catch (final Exception e) {
            throw new CrawlingAccessException("Could not parse Sitemaps: " + preloadDate, e);
        }
    }

    /**
     * Parses a text-based sitemap from the given input stream.
     * @param in the input stream to parse
     * @return the parsed sitemap set
     */
    protected SitemapSet parseTextSitemaps(final InputStream in) {
        final SitemapSet sitemapSet = new SitemapSet();
        sitemapSet.setType(SitemapSet.URLSET);

        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(in, Constants.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                final String url = line.trim();
                if (StringUtil.isNotBlank(url) && (url.startsWith("http://") || url.startsWith("https://"))) {
                    final SitemapUrl sitemapUrl = new SitemapUrl();
                    sitemapUrl.setLoc(url);
                    sitemapSet.addSitemap(sitemapUrl);
                }
            }
            return sitemapSet;
        } catch (final Exception e) {
            throw new SitemapsException("Could not parse Text Sitemaps.", e);
        }

    }

    /**
     * Parses an XML sitemap from the given input stream.
     * @param in the input stream to parse
     * @return the parsed sitemap set
     */
    protected SitemapSet parseXmlSitemaps(final InputStream in) {
        final XmlSitemapsHandler handler = new XmlSitemapsHandler();
        try {
            final SAXParserFactory spfactory = SAXParserFactory.newInstance();
            spfactory.setFeature(Constants.FEATURE_SECURE_PROCESSING, true);
            spfactory.setFeature(Constants.FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
            spfactory.setFeature(Constants.FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);
            final SAXParser parser = spfactory.newSAXParser();
            disableExternalResources(parser);
            parser.parse(in, handler);
        } catch (final Exception e) {
            throw new SitemapsException("Could not parse XML Sitemaps.", e);
        }
        return handler.getSitemapSet();
    }

    /**
     * Disables external resources for the SAX parser to prevent XXE attacks.
     * @param parser the SAX parser to configure
     * @throws SAXNotRecognizedException if the parser doesn't recognize the feature
     * @throws SAXNotSupportedException if the parser doesn't support the feature
     */
    protected void disableExternalResources(final SAXParser parser) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            parser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, StringUtil.EMPTY);
            parser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, StringUtil.EMPTY);
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to set a property.", e);
            }
        }
    }

    /**
     * SAX handler for parsing XML sitemaps.
     */
    protected static class XmlSitemapsHandler extends DefaultHandler {

        private static final String PRIORITY_ELEMENT = "priority";

        private static final String CHANGEFREQ_ELEMENT = "changefreq";

        private static final String LASTMOD_ELEMENT = "lastmod";

        private static final String LOC_ELEMENT = "loc";

        private static final String URL_ELEMENT = "url";

        private SitemapSet sitemapSet;

        private SitemapUrl sitemapUrl;

        private StringBuilder buf;

        /**
         * Creates a new XmlSitemapsHandler instance.
         */
        public XmlSitemapsHandler() {
            // Default constructor
        }

        @Override
        public void startDocument() {
            sitemapSet = new SitemapSet();
            sitemapSet.setType(SitemapSet.URLSET);
        }

        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) {
            if (URL_ELEMENT.equals(qName)) {
                sitemapUrl = new SitemapUrl();
            } else if (LOC_ELEMENT.equals(qName) || LASTMOD_ELEMENT.equals(qName) || CHANGEFREQ_ELEMENT.equals(qName)
                    || PRIORITY_ELEMENT.equals(qName)) {
                buf = new StringBuilder();
            }
        }

        @Override
        public void characters(final char[] ch, final int offset, final int length) {
            if (buf != null) {
                buf.append(new String(ch, offset, length));
            }
        }

        @Override
        public void endElement(final String uri, final String localName, final String qName) {
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
            } else if (PRIORITY_ELEMENT.equals(qName) && buf != null) {
                sitemapUrl.setPriority(buf.toString().trim());
                buf = null;
            }
        }

        @Override
        public void endDocument() {
            // nothing
        }

        /**
         * Gets the parsed sitemap set.
         * @return the sitemap set
         */
        public SitemapSet getSitemapSet() {
            return sitemapSet;
        }

    }

    /**
     * Parses an XML sitemap index from the given input stream.
     * @param in the input stream to parse
     * @return the parsed sitemap set
     */
    protected SitemapSet parseXmlSitemapsIndex(final InputStream in) {
        final XmlSitemapsIndexHandler handler = new XmlSitemapsIndexHandler();
        try {
            final SAXParserFactory spfactory = SAXParserFactory.newInstance();
            spfactory.setFeature(Constants.FEATURE_SECURE_PROCESSING, true);
            spfactory.setFeature(Constants.FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
            spfactory.setFeature(Constants.FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);
            final SAXParser parser = spfactory.newSAXParser();
            disableExternalResources(parser);
            parser.parse(in, handler);
        } catch (final Exception e) {
            throw new SitemapsException("Could not parse XML Sitemaps Index.", e);
        }
        return handler.getSitemapSet();
    }

    /**
     * SAX handler for parsing XML sitemap indexes.
     */
    protected static class XmlSitemapsIndexHandler extends DefaultHandler {

        private SitemapSet sitemapSet;

        private SitemapFile sitemapFile;

        private StringBuilder buf;

        /**
         * Creates a new XmlSitemapsIndexHandler instance.
         */
        public XmlSitemapsIndexHandler() {
            // Default constructor
        }

        @Override
        public void startDocument() {
            sitemapSet = new SitemapSet();
            sitemapSet.setType(SitemapSet.INDEX);
        }

        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) {
            if ("sitemap".equals(qName)) {
                sitemapFile = new SitemapFile();
            } else if ("loc".equals(qName) || "lastmod".equals(qName)) {
                buf = new StringBuilder();
            }
        }

        @Override
        public void characters(final char[] ch, final int offset, final int length) {
            if (buf != null) {
                buf.append(new String(ch, offset, length));
            }
        }

        @Override
        public void endElement(final String uri, final String localName, final String qName) {
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
            } else if ("lastmod".equals(qName) && buf != null) {
                sitemapFile.setLastmod(buf.toString().trim());
                buf = null;
            }
        }

        @Override
        public void endDocument() {
            // nothing
        }

        /**
         * Gets the parsed sitemap set.
         * @return the sitemap set
         */
        public SitemapSet getSitemapSet() {
            return sitemapSet;
        }

    }

    /**
     * Sets the preload size for checking file format.
     * @param preloadSize the preload size in bytes
     */
    public void setPreloadSize(final int preloadSize) {
        this.preloadSize = preloadSize;
    }
}
