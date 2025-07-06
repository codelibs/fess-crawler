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
package org.codelibs.fess.crawler.entity;

import org.codelibs.core.lang.StringUtil;

/**
 * Represents a Sitemap file entry, conforming to the Sitemap XML format.
 * This class holds information about a single Sitemap, including its location and last modification timestamp.
 * It implements the {@link Sitemap} interface.
 *
 * <p>
 * A Sitemap file provides search engines with a list of URLs available for crawling.
 * This class encapsulates the essential attributes of a Sitemap entry, allowing for efficient management
 * and processing of Sitemap data.
 * </p>
 *
 * <p>
 * The {@code loc} attribute specifies the URL of the Sitemap, while the {@code lastmod} attribute
 * indicates the last time the Sitemap file was modified.  The {@code lastmod} attribute is used by crawlers
 * to incrementally fetch sitemaps that have been updated since a certain date.
 * </p>
 *
 * <p>
 * This class also provides implementations for {@code equals}, {@code hashCode}, and {@code toString} methods
 * to facilitate object comparison and representation.
 * </p>
 *
 */
public class SitemapFile implements Sitemap {

    private static final long serialVersionUID = 1L;

    /**
     * Identifies the location of the Sitemap. This location can be a Sitemap,
     * an Atom file, RSS file or a simple text file.
     */
    private String loc;

    /**
     * Identifies the time that the corresponding Sitemap file was modified. It
     * does not correspond to the time that any of the pages listed in that
     * Sitemap were changed. The value for the lastmod tag should be in W3C
     * Datetime format.
     *
     * By providing the last modification timestamp, you enable search engine
     * crawlers to retrieve only a subset of the Sitemaps in the index i.e. a
     * crawler may only retrieve Sitemaps that were modified since a certain
     * date. This incremental Sitemap fetching mechanism allows for the rapid
     * discovery of new URLs on very large sites.
     */
    private String lastmod;

    /**
     * Creates a new SitemapFile instance.
     */
    public SitemapFile() {
        // Default constructor
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.Sitemap#getLoc()
     */
    @Override
    public String getLoc() {
        return loc;
    }

    /**
     * Sets the location of the sitemap.
     * @param loc the location URL to set
     */
    public void setLoc(final String loc) {
        this.loc = loc;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.Sitemap#getLastmod()
     */
    @Override
    public String getLastmod() {
        return lastmod;
    }

    /**
     * Sets the last modification time of the sitemap.
     * @param lastmod the last modification time in W3C Datetime format
     */
    public void setLastmod(final String lastmod) {
        this.lastmod = lastmod;
    }

    /**
     * Checks if this SitemapFile is equal to another object.
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof final SitemapFile sitemapUrl)) {
            return false;
        }
        if (StringUtil.equals(loc, sitemapUrl.loc) && StringUtil.equals(lastmod, sitemapUrl.lastmod)) {
            return true;
        }
        return false;
    }

    /**
     * Returns the hash code value for this SitemapFile.
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(loc, lastmod);
    }

    /**
     * Returns a string representation of this SitemapFile.
     * @return A string representation.
     */
    @Override
    public String toString() {
        return "SitemapFile [loc=" + loc + ", lastmod=" + lastmod + "]";
    }
}
