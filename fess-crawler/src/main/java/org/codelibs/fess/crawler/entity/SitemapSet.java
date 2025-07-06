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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of Sitemaps, which can be either a UrlSet or an Index.
 * This class provides methods to manage a list of Sitemap objects and determine the type of the SitemapSet.
 *
 */
public class SitemapSet implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Constant for UrlSet type. */
    public static final String URLSET = "UrlSet";

    /** Constant for Index type. */
    public static final String INDEX = "Index";

    /** The list of sitemaps in this set. */
    private final List<Sitemap> sitemapList = new ArrayList<>();

    /** The type of this sitemap set (URLSET or INDEX). */
    private String type = URLSET;

    /**
     * Creates a new SitemapSet instance with default type URLSET.
     */
    public SitemapSet() {
        // Default constructor
    }

    /**
     * Adds a sitemap to this set.
     * @param sitemap the sitemap to add
     */
    public void addSitemap(final Sitemap sitemap) {
        sitemapList.add(sitemap);
    }

    /**
     * Removes a sitemap from this set.
     * @param sitemap the sitemap to remove
     */
    public void removeSitemap(final Sitemap sitemap) {
        sitemapList.remove(sitemap);
    }

    /**
     * Gets all sitemaps in this set as an array.
     * @return an array of sitemaps
     */
    public Sitemap[] getSitemaps() {
        return sitemapList.toArray(new Sitemap[sitemapList.size()]);
    }

    /**
     * Sets the type of this sitemap set.
     * @param type the type to set (URLSET or INDEX)
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Checks if this sitemap set is of type URLSET.
     * @return true if this is a URLSET, false otherwise
     */
    public boolean isUrlSet() {
        return URLSET.equals(type);
    }

    /**
     * Checks if this sitemap set is of type INDEX.
     * @return true if this is an INDEX, false otherwise
     */
    public boolean isIndex() {
        return INDEX.equals(type);
    }

    /**
     * Returns a string representation of this object.
     * @return A string representation.
     */
    @Override
    public String toString() {
        return "SitemapSet [sitemapList=" + sitemapList + ", type=" + type + "]";
    }
}
