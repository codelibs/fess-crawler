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
 * Represents an alternate link entry within a sitemap URL.
 * This class encapsulates the properties of an alternate link as defined in the hreflang annotation.
 *
 * <p>
 * The hreflang attribute tells search engines about alternate versions of a page in different languages
 * or for different regions. This helps search engines serve the correct language or regional URL
 * to searchers.
 * </p>
 *
 * @see <a href="https://developers.google.com/search/docs/specialty/international/localized-versions">Multi-regional and multilingual sites</a>
 */
public class SitemapAlternateLink implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The language/region code for this alternate link.
     * Should be in ISO 639-1 format (language) optionally followed by ISO 3166-1 Alpha 2 format (region).
     * For example: "en", "en-US", "de-CH", "x-default"
     */
    private String hreflang;

    /**
     * The URL of the alternate version of the page.
     */
    private String href;

    /**
     * Creates a new SitemapAlternateLink instance.
     */
    public SitemapAlternateLink() {
        // Default constructor
    }

    /**
     * Gets the hreflang attribute.
     * @return the hreflang value
     */
    public String getHreflang() {
        return hreflang;
    }

    /**
     * Sets the hreflang attribute.
     * @param hreflang the hreflang value to set
     */
    public void setHreflang(final String hreflang) {
        this.hreflang = hreflang;
    }

    /**
     * Gets the href URL.
     * @return the href URL
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the href URL.
     * @param href the href URL to set
     */
    public void setHref(final String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return "SitemapAlternateLink [hreflang=" + hreflang + ", href=" + href + "]";
    }
}
