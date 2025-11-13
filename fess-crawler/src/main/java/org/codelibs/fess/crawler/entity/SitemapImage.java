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

/**
 * Represents an image entry within a sitemap URL.
 * This class encapsulates the properties of an image as defined in the Google Image Sitemap extension.
 *
 * <p>
 * The image extension allows you to provide additional information about images on your pages.
 * This can help Google index your images and display them in Google Images search results.
 * </p>
 *
 * @see <a href="https://developers.google.com/search/docs/crawling-indexing/sitemaps/image-sitemaps">Google Image Sitemaps</a>
 */
public class SitemapImage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The URL of the image.
     * In some cases, the image URL may not be on the same domain as your main site.
     */
    private String loc;

    /**
     * The caption of the image.
     */
    private String caption;

    /**
     * The geographic location of the image.
     * For example, "Limerick, Ireland".
     */
    private String geoLocation;

    /**
     * The title of the image.
     */
    private String title;

    /**
     * A URL to the license of the image.
     */
    private String license;

    /**
     * Creates a new SitemapImage instance.
     */
    public SitemapImage() {
        // Default constructor
    }

    /**
     * Gets the location URL of the image.
     * @return the image URL
     */
    public String getLoc() {
        return loc;
    }

    /**
     * Sets the location URL of the image.
     * @param loc the image URL to set
     */
    public void setLoc(final String loc) {
        this.loc = loc;
    }

    /**
     * Gets the caption of the image.
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption of the image.
     * @param caption the caption to set
     */
    public void setCaption(final String caption) {
        this.caption = caption;
    }

    /**
     * Gets the geographic location of the image.
     * @return the geographic location
     */
    public String getGeoLocation() {
        return geoLocation;
    }

    /**
     * Sets the geographic location of the image.
     * @param geoLocation the geographic location to set
     */
    public void setGeoLocation(final String geoLocation) {
        this.geoLocation = geoLocation;
    }

    /**
     * Gets the title of the image.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the image.
     * @param title the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Gets the license URL of the image.
     * @return the license URL
     */
    public String getLicense() {
        return license;
    }

    /**
     * Sets the license URL of the image.
     * @param license the license URL to set
     */
    public void setLicense(final String license) {
        this.license = license;
    }

    @Override
    public String toString() {
        return "SitemapImage [loc=" + loc + ", caption=" + caption + ", geoLocation=" + geoLocation + ", title=" + title + ", license="
                + license + "]";
    }
}
