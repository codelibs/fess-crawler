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
 * Represents a news entry within a sitemap URL.
 * This class encapsulates the properties of a news article as defined in the Google News Sitemap extension.
 *
 * <p>
 * The news extension allows you to provide additional information about news articles on your site.
 * This can help Google News index your articles and display them in Google News search results.
 * </p>
 *
 * @see <a href="https://developers.google.com/search/docs/crawling-indexing/sitemaps/news-sitemap">Google News Sitemaps</a>
 */
public class SitemapNews implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The name of the news publication.
     * It must exactly match the name as it appears on your articles.
     */
    private String publicationName;

    /**
     * The language of your publication.
     * It should be an ISO 639 Language Code (either 2 or 3 letters).
     */
    private String publicationLanguage;

    /**
     * The date of publication of the article.
     * The date must be in W3C format.
     */
    private String publicationDate;

    /**
     * The title of the news article.
     * The title may be up to 110 characters.
     */
    private String title;

    /**
     * A comma-separated list of keywords describing the topic of the article.
     * Keywords may be drawn from, but are not limited to, the list of existing Google News keywords.
     */
    private String keywords;

    /**
     * A comma-separated list of properties characterizing the content of the article.
     */
    private String stockTickers;

    /**
     * Creates a new SitemapNews instance.
     */
    public SitemapNews() {
        // Default constructor
    }

    /**
     * Gets the publication name.
     * @return the publication name
     */
    public String getPublicationName() {
        return publicationName;
    }

    /**
     * Sets the publication name.
     * @param publicationName the publication name to set
     */
    public void setPublicationName(final String publicationName) {
        this.publicationName = publicationName;
    }

    /**
     * Gets the publication language.
     * @return the publication language
     */
    public String getPublicationLanguage() {
        return publicationLanguage;
    }

    /**
     * Sets the publication language.
     * @param publicationLanguage the publication language to set
     */
    public void setPublicationLanguage(final String publicationLanguage) {
        this.publicationLanguage = publicationLanguage;
    }

    /**
     * Gets the publication date.
     * @return the publication date
     */
    public String getPublicationDate() {
        return publicationDate;
    }

    /**
     * Sets the publication date.
     * @param publicationDate the publication date to set
     */
    public void setPublicationDate(final String publicationDate) {
        this.publicationDate = publicationDate;
    }

    /**
     * Gets the title of the news article.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the news article.
     * @param title the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Gets the keywords.
     * @return the keywords
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * Sets the keywords.
     * @param keywords the keywords to set
     */
    public void setKeywords(final String keywords) {
        this.keywords = keywords;
    }

    /**
     * Gets the stock tickers.
     * @return the stock tickers
     */
    public String getStockTickers() {
        return stockTickers;
    }

    /**
     * Sets the stock tickers.
     * @param stockTickers the stock tickers to set
     */
    public void setStockTickers(final String stockTickers) {
        this.stockTickers = stockTickers;
    }

    @Override
    public String toString() {
        return "SitemapNews [publicationName=" + publicationName + ", publicationLanguage=" + publicationLanguage + ", publicationDate="
                + publicationDate + ", title=" + title + ", keywords=" + keywords + "]";
    }
}
