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
import org.codelibs.fess.crawler.entity.SitemapAlternateLink;
import org.codelibs.fess.crawler.entity.SitemapFile;
import org.codelibs.fess.crawler.entity.SitemapImage;
import org.codelibs.fess.crawler.entity.SitemapNews;
import org.codelibs.fess.crawler.entity.SitemapSet;
import org.codelibs.fess.crawler.entity.SitemapUrl;
import org.codelibs.fess.crawler.entity.SitemapVideo;
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

    /** Enable validation of sitemap entries. */
    protected boolean enableValidation = false;

    /** Maximum URL length according to sitemap specification. */
    protected static final int MAX_URL_LENGTH = 2048;

    /** Valid changefreq values. */
    protected static final String[] VALID_CHANGEFREQ = { "always", "hourly", "daily", "weekly", "monthly", "yearly", "never" };

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
            spfactory.setNamespaceAware(true); // Enable namespace awareness
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
     * SAX handler for parsing XML sitemaps with extension support.
     */
    protected static class XmlSitemapsHandler extends DefaultHandler {

        private static final String PRIORITY_ELEMENT = "priority";

        private static final String CHANGEFREQ_ELEMENT = "changefreq";

        private static final String LASTMOD_ELEMENT = "lastmod";

        private static final String LOC_ELEMENT = "loc";

        private static final String URL_ELEMENT = "url";

        // Image extension elements
        private static final String IMAGE_IMAGE_ELEMENT = "image:image";

        private static final String IMAGE_LOC_ELEMENT = "image:loc";

        private static final String IMAGE_CAPTION_ELEMENT = "image:caption";

        private static final String IMAGE_GEO_LOCATION_ELEMENT = "image:geo_location";

        private static final String IMAGE_TITLE_ELEMENT = "image:title";

        private static final String IMAGE_LICENSE_ELEMENT = "image:license";

        // Video extension elements
        private static final String VIDEO_VIDEO_ELEMENT = "video:video";

        private static final String VIDEO_THUMBNAIL_LOC_ELEMENT = "video:thumbnail_loc";

        private static final String VIDEO_TITLE_ELEMENT = "video:title";

        private static final String VIDEO_DESCRIPTION_ELEMENT = "video:description";

        private static final String VIDEO_CONTENT_LOC_ELEMENT = "video:content_loc";

        private static final String VIDEO_PLAYER_LOC_ELEMENT = "video:player_loc";

        private static final String VIDEO_DURATION_ELEMENT = "video:duration";

        private static final String VIDEO_PUBLICATION_DATE_ELEMENT = "video:publication_date";

        private static final String VIDEO_CATEGORY_ELEMENT = "video:category";

        private static final String VIDEO_FAMILY_FRIENDLY_ELEMENT = "video:family_friendly";

        private static final String VIDEO_RESTRICTION_ELEMENT = "video:restriction";

        private static final String VIDEO_PRICE_ELEMENT = "video:price";

        private static final String VIDEO_REQUIRES_SUBSCRIPTION_ELEMENT = "video:requires_subscription";

        private static final String VIDEO_UPLOADER_ELEMENT = "video:uploader";

        private static final String VIDEO_PLATFORM_ELEMENT = "video:platform";

        private static final String VIDEO_LIVE_ELEMENT = "video:live";

        // News extension elements
        private static final String NEWS_NEWS_ELEMENT = "news:news";

        private static final String NEWS_PUBLICATION_ELEMENT = "news:publication";

        private static final String NEWS_NAME_ELEMENT = "news:name";

        private static final String NEWS_LANGUAGE_ELEMENT = "news:language";

        private static final String NEWS_PUBLICATION_DATE_ELEMENT = "news:publication_date";

        private static final String NEWS_TITLE_ELEMENT = "news:title";

        private static final String NEWS_KEYWORDS_ELEMENT = "news:keywords";

        private static final String NEWS_STOCK_TICKERS_ELEMENT = "news:stock_tickers";

        // Alternate link element (hreflang)
        private static final String XHTML_LINK_ELEMENT = "xhtml:link";

        private SitemapSet sitemapSet;

        private SitemapUrl sitemapUrl;

        private SitemapImage currentImage;

        private SitemapVideo currentVideo;

        private SitemapNews currentNews;

        private boolean inNewsPublication;

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
            // Support both with and without namespace prefixes
            final String elementName = getElementName(localName, qName);

            if (URL_ELEMENT.equals(elementName)) {
                sitemapUrl = new SitemapUrl();
            } else if (LOC_ELEMENT.equals(elementName) || LASTMOD_ELEMENT.equals(elementName) || CHANGEFREQ_ELEMENT.equals(elementName)
                    || PRIORITY_ELEMENT.equals(elementName)) {
                buf = new StringBuilder();
            } else if (IMAGE_IMAGE_ELEMENT.equals(elementName)) {
                currentImage = new SitemapImage();
            } else if (IMAGE_LOC_ELEMENT.equals(elementName) || IMAGE_CAPTION_ELEMENT.equals(elementName)
                    || IMAGE_GEO_LOCATION_ELEMENT.equals(elementName) || IMAGE_TITLE_ELEMENT.equals(elementName)
                    || IMAGE_LICENSE_ELEMENT.equals(elementName)) {
                buf = new StringBuilder();
            } else if (VIDEO_VIDEO_ELEMENT.equals(elementName)) {
                currentVideo = new SitemapVideo();
            } else if (VIDEO_THUMBNAIL_LOC_ELEMENT.equals(elementName) || VIDEO_TITLE_ELEMENT.equals(elementName)
                    || VIDEO_DESCRIPTION_ELEMENT.equals(elementName) || VIDEO_CONTENT_LOC_ELEMENT.equals(elementName)
                    || VIDEO_PLAYER_LOC_ELEMENT.equals(elementName) || VIDEO_DURATION_ELEMENT.equals(elementName)
                    || VIDEO_PUBLICATION_DATE_ELEMENT.equals(elementName) || VIDEO_CATEGORY_ELEMENT.equals(elementName)
                    || VIDEO_FAMILY_FRIENDLY_ELEMENT.equals(elementName) || VIDEO_RESTRICTION_ELEMENT.equals(elementName)
                    || VIDEO_PRICE_ELEMENT.equals(elementName) || VIDEO_REQUIRES_SUBSCRIPTION_ELEMENT.equals(elementName)
                    || VIDEO_UPLOADER_ELEMENT.equals(elementName) || VIDEO_PLATFORM_ELEMENT.equals(elementName)
                    || VIDEO_LIVE_ELEMENT.equals(elementName)) {
                buf = new StringBuilder();
            } else if (NEWS_NEWS_ELEMENT.equals(elementName)) {
                currentNews = new SitemapNews();
                inNewsPublication = false;
            } else if (NEWS_PUBLICATION_ELEMENT.equals(elementName)) {
                inNewsPublication = true;
            } else if (NEWS_NAME_ELEMENT.equals(elementName) || NEWS_LANGUAGE_ELEMENT.equals(elementName)
                    || NEWS_PUBLICATION_DATE_ELEMENT.equals(elementName) || NEWS_TITLE_ELEMENT.equals(elementName)
                    || NEWS_KEYWORDS_ELEMENT.equals(elementName) || NEWS_STOCK_TICKERS_ELEMENT.equals(elementName)) {
                buf = new StringBuilder();
            } else if (XHTML_LINK_ELEMENT.equals(elementName)) {
                // Handle hreflang alternate links
                final String rel = attributes.getValue("rel");
                final String hreflang = attributes.getValue("hreflang");
                final String href = attributes.getValue("href");
                if ("alternate".equals(rel) && hreflang != null && href != null) {
                    final SitemapAlternateLink alternateLink = new SitemapAlternateLink();
                    alternateLink.setHreflang(hreflang);
                    alternateLink.setHref(href);
                    if (sitemapUrl != null) {
                        sitemapUrl.addAlternateLink(alternateLink);
                    }
                }
            }
        }

        /**
         * Gets the element name to use for comparison.
         * Prefers qName (which includes namespace prefix) but falls back to localName if qName is empty.
         * @param localName the local name without namespace prefix
         * @param qName the qualified name with namespace prefix
         * @return the element name to use
         */
        private String getElementName(final String localName, final String qName) {
            return StringUtil.isNotBlank(qName) ? qName : localName;
        }

        @Override
        public void characters(final char[] ch, final int offset, final int length) {
            if (buf != null) {
                buf.append(new String(ch, offset, length));
            }
        }

        @Override
        public void endElement(final String uri, final String localName, final String qName) {
            // Support both with and without namespace prefixes
            final String elementName = getElementName(localName, qName);

            if (URL_ELEMENT.equals(elementName)) {
                if (sitemapUrl != null) {
                    sitemapSet.addSitemap(sitemapUrl);
                }
                sitemapUrl = null;
            } else if (LOC_ELEMENT.equals(elementName)) {
                if (buf != null && sitemapUrl != null) {
                    sitemapUrl.setLoc(buf.toString().trim());
                    buf = null;
                }
            } else if (LASTMOD_ELEMENT.equals(elementName)) {
                if (buf != null && sitemapUrl != null) {
                    sitemapUrl.setLastmod(buf.toString().trim());
                    buf = null;
                }
            } else if (CHANGEFREQ_ELEMENT.equals(elementName)) {
                if (buf != null && sitemapUrl != null) {
                    sitemapUrl.setChangefreq(buf.toString().trim());
                    buf = null;
                }
            } else if (PRIORITY_ELEMENT.equals(elementName) && buf != null && sitemapUrl != null) {
                sitemapUrl.setPriority(buf.toString().trim());
                buf = null;
            } else if (IMAGE_IMAGE_ELEMENT.equals(elementName)) {
                if (currentImage != null && sitemapUrl != null) {
                    sitemapUrl.addImage(currentImage);
                }
                currentImage = null;
            } else if (IMAGE_LOC_ELEMENT.equals(elementName)) {
                if (buf != null && currentImage != null) {
                    currentImage.setLoc(buf.toString().trim());
                    buf = null;
                }
            } else if (IMAGE_CAPTION_ELEMENT.equals(elementName)) {
                if (buf != null && currentImage != null) {
                    currentImage.setCaption(buf.toString().trim());
                    buf = null;
                }
            } else if (IMAGE_GEO_LOCATION_ELEMENT.equals(elementName)) {
                if (buf != null && currentImage != null) {
                    currentImage.setGeoLocation(buf.toString().trim());
                    buf = null;
                }
            } else if (IMAGE_TITLE_ELEMENT.equals(elementName)) {
                if (buf != null && currentImage != null) {
                    currentImage.setTitle(buf.toString().trim());
                    buf = null;
                }
            } else if (IMAGE_LICENSE_ELEMENT.equals(elementName)) {
                if (buf != null && currentImage != null) {
                    currentImage.setLicense(buf.toString().trim());
                    buf = null;
                }
            } else if (VIDEO_VIDEO_ELEMENT.equals(elementName)) {
                if (currentVideo != null && sitemapUrl != null) {
                    sitemapUrl.addVideo(currentVideo);
                }
                currentVideo = null;
            } else if (VIDEO_THUMBNAIL_LOC_ELEMENT.equals(elementName)) {
                if (buf != null && currentVideo != null) {
                    currentVideo.setThumbnailLoc(buf.toString().trim());
                    buf = null;
                }
            } else if (VIDEO_TITLE_ELEMENT.equals(elementName)) {
                if (buf != null && currentVideo != null) {
                    currentVideo.setTitle(buf.toString().trim());
                    buf = null;
                }
            } else if (VIDEO_DESCRIPTION_ELEMENT.equals(elementName)) {
                if (buf != null && currentVideo != null) {
                    currentVideo.setDescription(buf.toString().trim());
                    buf = null;
                }
            } else if (VIDEO_CONTENT_LOC_ELEMENT.equals(elementName)) {
                if (buf != null && currentVideo != null) {
                    currentVideo.setContentLoc(buf.toString().trim());
                    buf = null;
                }
            } else if (VIDEO_PLAYER_LOC_ELEMENT.equals(elementName)) {
                if (buf != null && currentVideo != null) {
                    currentVideo.setPlayerLoc(buf.toString().trim());
                    buf = null;
                }
            } else if (VIDEO_DURATION_ELEMENT.equals(elementName)) {
                if (buf != null && currentVideo != null) {
                    currentVideo.setDuration(buf.toString().trim());
                    buf = null;
                }
            } else if (VIDEO_PUBLICATION_DATE_ELEMENT.equals(elementName)) {
                if (buf != null && currentVideo != null) {
                    currentVideo.setPublicationDate(buf.toString().trim());
                    buf = null;
                }
            } else if (VIDEO_CATEGORY_ELEMENT.equals(elementName)) {
                if (buf != null && currentVideo != null) {
                    currentVideo.setCategory(buf.toString().trim());
                    buf = null;
                }
            } else if (VIDEO_FAMILY_FRIENDLY_ELEMENT.equals(elementName)) {
                if (buf != null && currentVideo != null) {
                    currentVideo.setFamilyFriendly(buf.toString().trim());
                    buf = null;
                }
            } else if (VIDEO_RESTRICTION_ELEMENT.equals(elementName)) {
                if (buf != null && currentVideo != null) {
                    currentVideo.setRestriction(buf.toString().trim());
                    buf = null;
                }
            } else if (VIDEO_PRICE_ELEMENT.equals(elementName)) {
                if (buf != null && currentVideo != null) {
                    currentVideo.setPrice(buf.toString().trim());
                    buf = null;
                }
            } else if (VIDEO_REQUIRES_SUBSCRIPTION_ELEMENT.equals(elementName)) {
                if (buf != null && currentVideo != null) {
                    currentVideo.setRequiresSubscription(buf.toString().trim());
                    buf = null;
                }
            } else if (VIDEO_UPLOADER_ELEMENT.equals(elementName)) {
                if (buf != null && currentVideo != null) {
                    currentVideo.setUploader(buf.toString().trim());
                    buf = null;
                }
            } else if (VIDEO_PLATFORM_ELEMENT.equals(elementName)) {
                if (buf != null && currentVideo != null) {
                    currentVideo.setPlatform(buf.toString().trim());
                    buf = null;
                }
            } else if (VIDEO_LIVE_ELEMENT.equals(elementName)) {
                if (buf != null && currentVideo != null) {
                    currentVideo.setLive(buf.toString().trim());
                    buf = null;
                }
            } else if (NEWS_NEWS_ELEMENT.equals(elementName)) {
                if (currentNews != null && sitemapUrl != null) {
                    sitemapUrl.setNews(currentNews);
                }
                currentNews = null;
                inNewsPublication = false;
            } else if (NEWS_PUBLICATION_ELEMENT.equals(elementName)) {
                inNewsPublication = false;
            } else if (NEWS_NAME_ELEMENT.equals(elementName)) {
                if (buf != null && currentNews != null) {
                    currentNews.setPublicationName(buf.toString().trim());
                    buf = null;
                }
            } else if (NEWS_LANGUAGE_ELEMENT.equals(elementName)) {
                if (buf != null && currentNews != null) {
                    currentNews.setPublicationLanguage(buf.toString().trim());
                    buf = null;
                }
            } else if (NEWS_PUBLICATION_DATE_ELEMENT.equals(elementName)) {
                if (buf != null && currentNews != null) {
                    currentNews.setPublicationDate(buf.toString().trim());
                    buf = null;
                }
            } else if (NEWS_TITLE_ELEMENT.equals(elementName)) {
                if (buf != null && currentNews != null) {
                    currentNews.setTitle(buf.toString().trim());
                    buf = null;
                }
            } else if (NEWS_KEYWORDS_ELEMENT.equals(elementName)) {
                if (buf != null && currentNews != null) {
                    currentNews.setKeywords(buf.toString().trim());
                    buf = null;
                }
            } else if (NEWS_STOCK_TICKERS_ELEMENT.equals(elementName)) {
                if (buf != null && currentNews != null) {
                    currentNews.setStockTickers(buf.toString().trim());
                    buf = null;
                }
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
            spfactory.setNamespaceAware(true); // Enable namespace awareness
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

    /**
     * Enables or disables validation of sitemap entries.
     * @param enableValidation true to enable validation, false to disable
     */
    public void setEnableValidation(final boolean enableValidation) {
        this.enableValidation = enableValidation;
    }

    /**
     * Validates a URL length according to sitemap specification.
     * @param url the URL to validate
     * @return true if valid, false otherwise
     */
    protected boolean isValidUrl(final String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        if (url.length() > MAX_URL_LENGTH) {
            if (logger.isDebugEnabled()) {
                logger.debug("URL exceeds maximum length of {} characters: {}", MAX_URL_LENGTH, url);
            }
            return false;
        }
        return url.startsWith("http://") || url.startsWith("https://");
    }

    /**
     * Validates a priority value according to sitemap specification.
     * @param priority the priority to validate
     * @return true if valid, false otherwise
     */
    protected boolean isValidPriority(final String priority) {
        if (priority == null || priority.isEmpty()) {
            return true; // Priority is optional
        }
        try {
            final double value = Double.parseDouble(priority);
            if (value < 0.0 || value > 1.0) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Priority value out of range (0.0-1.0): {}", priority);
                }
                return false;
            }
            return true;
        } catch (final NumberFormatException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid priority format: {}", priority);
            }
            return false;
        }
    }

    /**
     * Validates a changefreq value according to sitemap specification.
     * @param changefreq the changefreq to validate
     * @return true if valid, false otherwise
     */
    protected boolean isValidChangefreq(final String changefreq) {
        if (changefreq == null || changefreq.isEmpty()) {
            return true; // Changefreq is optional
        }
        for (final String valid : VALID_CHANGEFREQ) {
            if (valid.equals(changefreq)) {
                return true;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Invalid changefreq value: {}", changefreq);
        }
        return false;
    }

    /**
     * Validates a date format (W3C Datetime format).
     * @param date the date to validate
     * @return true if valid, false otherwise
     */
    protected boolean isValidDateFormat(final String date) {
        if (date == null || date.isEmpty()) {
            return true; // Date is optional
        }
        // Basic validation for W3C Datetime format (YYYY-MM-DD or YYYY-MM-DDThh:mm:ss+00:00)
        // This is a simplified validation
        if (date.length() < 10) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid date format (too short): {}", date);
            }
            return false;
        }
        // Check if it starts with a valid year format
        if (!date.matches("^\\d{4}-\\d{2}-\\d{2}.*")) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid date format: {}", date);
            }
            return false;
        }
        return true;
    }

    /**
     * Validates a sitemap URL entry.
     * @param sitemapUrl the sitemap URL to validate
     * @return true if valid, false otherwise
     */
    protected boolean validateSitemapUrl(final SitemapUrl sitemapUrl) {
        if (!enableValidation) {
            return true;
        }
        if (sitemapUrl == null) {
            return false;
        }
        // Validate required fields
        if (!isValidUrl(sitemapUrl.getLoc())) {
            return false;
        }
        // Validate optional fields
        if (!isValidPriority(sitemapUrl.getPriority())) {
            return false;
        }
        if (!isValidChangefreq(sitemapUrl.getChangefreq())) {
            return false;
        }
        if (!isValidDateFormat(sitemapUrl.getLastmod())) {
            return false;
        }
        return true;
    }
}
