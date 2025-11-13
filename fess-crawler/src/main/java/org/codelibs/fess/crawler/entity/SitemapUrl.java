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

import java.util.ArrayList;
import java.util.List;

import org.codelibs.core.lang.StringUtil;

/**
 * Represents a URL entry within a sitemap.
 *
 * <p>
 * This class encapsulates the properties of a URL as defined in the sitemap XML format,
 * including its location, last modification date, change frequency, and priority.
 * It also supports sitemap extensions such as images, videos, news, and alternate links.
 * It implements the {@link Sitemap} interface.
 * </p>
 *
 * <p>
 * The {@code SitemapUrl} class provides getter and setter methods for each of these properties,
 * as well as implementations for {@code equals()}, {@code hashCode()}, and {@code toString()} methods.
 * </p>
 *
 */
public class SitemapUrl implements Sitemap {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new SitemapUrl instance.
     */
    public SitemapUrl() {
        super();
    }

    /**
     * URL of the page. This URL must begin with the protocol (such as http) and
     * end with a trailing slash, if your web server requires it. This value
     * must be less than 2,048 characters.
     */
    private String loc;

    /**
     * The date of last modification of the file. This date should be in W3C
     * Datetime format. This format allows you to omit the time portion, if
     * desired, and use YYYY-MM-DD.
     *
     * Note that this tag is separate from the If-Modified-Since (304) header
     * the server can return, and search engines may use the information from
     * both sources differently.
     */
    private String lastmod;

    /**
     * How frequently the page is likely to change. This value provides general
     * information to search engines and may not correlate exactly to how often
     * they crawl the page. Valid values are:
     * <ul>
     * <li>always</li>
     * <li>hourly</li>
     * <li>daily</li>
     * <li>weekly</li>
     * <li>monthly</li>
     * <li>yearly</li>
     * <li>never</li>
     * </ul>
     * The value "always" should be used to describe documents that change each
     * time they are accessed. The value "never" should be used to describe
     * archived URLs.
     *
     * Please note that the value of this tag is considered a hint and not a
     * command. Even though search engine crawlers may consider this information
     * when making decisions, they may crawl pages marked "hourly" less
     * frequently than that, and they may crawl pages marked "yearly" more
     * frequently than that. Crawlers may periodically crawl pages marked
     * "never" so that they can handle unexpected changes to those pages.
     */
    private String changefreq;

    /**
     * The priority of this URL relative to other URLs on your site. Valid
     * values range from 0.0 to 1.0. This value does not affect how your pages
     * are compared to pages on other sites—it only lets the search engines know
     * which pages you deem most important for the crawlers.
     *
     * The default priority of a page is 0.5.
     *
     * Please note that the priority you assign to a page is not likely to
     * influence the position of your URLs in a search engine's result pages.
     * Search engines may use this information when selecting between URLs on
     * the same site, so you can use this tag to increase the likelihood that
     * your most important pages are present in a search index.
     *
     * Also, please note that assigning a high priority to all of the URLs on
     * your site is not likely to help you. Since the priority is relative, it
     * is only used to select between URLs on your site.
     */
    private String priority;

    /**
     * List of images associated with this URL.
     * Used for Google Image Sitemap extension.
     */
    private List<SitemapImage> images;

    /**
     * List of videos associated with this URL.
     * Used for Video Sitemap extension.
     */
    private List<SitemapVideo> videos;

    /**
     * News information associated with this URL.
     * Used for Google News Sitemap extension.
     */
    private SitemapNews news;

    /**
     * List of alternate language/region versions of this URL.
     * Used for hreflang annotation.
     */
    private List<SitemapAlternateLink> alternateLinks;

    /**
     * Returns the location URL of this sitemap entry.
     * @return the location URL
     */
    @Override
    public String getLoc() {
        return loc;
    }

    /**
     * Sets the location URL of this sitemap entry.
     * @param loc the location URL to set
     */
    public void setLoc(final String loc) {
        this.loc = loc;
    }

    /**
     * Returns the last modification date of this sitemap entry.
     * @return the last modification date
     */
    @Override
    public String getLastmod() {
        return lastmod;
    }

    /**
     * Sets the last modification date of this sitemap entry.
     * @param lastmod the last modification date to set
     */
    public void setLastmod(final String lastmod) {
        this.lastmod = lastmod;
    }

    /**
     * Returns the change frequency of this sitemap entry.
     * @return the change frequency
     */
    public String getChangefreq() {
        return changefreq;
    }

    /**
     * Sets the change frequency of this sitemap entry.
     * @param changefreq the change frequency to set
     */
    public void setChangefreq(final String changefreq) {
        this.changefreq = changefreq;
    }

    /**
     * Returns the priority of this sitemap entry.
     * @return the priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the priority of this sitemap entry.
     * @param priority the priority to set
     */
    public void setPriority(final String priority) {
        this.priority = priority;
    }

    /**
     * Gets the list of images associated with this URL.
     * @return the list of images, or an empty list if none
     */
    public List<SitemapImage> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    /**
     * Adds an image to this sitemap URL.
     * @param image the image to add
     */
    public void addImage(final SitemapImage image) {
        getImages().add(image);
    }

    /**
     * Gets the list of videos associated with this URL.
     * @return the list of videos, or an empty list if none
     */
    public List<SitemapVideo> getVideos() {
        if (videos == null) {
            videos = new ArrayList<>();
        }
        return videos;
    }

    /**
     * Adds a video to this sitemap URL.
     * @param video the video to add
     */
    public void addVideo(final SitemapVideo video) {
        getVideos().add(video);
    }

    /**
     * Gets the news information associated with this URL.
     * @return the news information, or null if none
     */
    public SitemapNews getNews() {
        return news;
    }

    /**
     * Sets the news information for this sitemap URL.
     * @param news the news information to set
     */
    public void setNews(final SitemapNews news) {
        this.news = news;
    }

    /**
     * Gets the list of alternate links associated with this URL.
     * @return the list of alternate links, or an empty list if none
     */
    public List<SitemapAlternateLink> getAlternateLinks() {
        if (alternateLinks == null) {
            alternateLinks = new ArrayList<>();
        }
        return alternateLinks;
    }

    /**
     * Adds an alternate link to this sitemap URL.
     * @param alternateLink the alternate link to add
     */
    public void addAlternateLink(final SitemapAlternateLink alternateLink) {
        getAlternateLinks().add(alternateLink);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof final SitemapUrl sitemapUrl)) {
            return false;
        }
        if (StringUtil.equals(loc, sitemapUrl.loc) && StringUtil.equals(changefreq, sitemapUrl.changefreq)
                && StringUtil.equals(lastmod, sitemapUrl.lastmod) && StringUtil.equals(priority, sitemapUrl.priority)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(loc, changefreq, lastmod, priority);
    }

    /**
     * Returns a string representation of this SitemapUrl.
     * @return A string representation.
     */
    @Override
    public String toString() {
        return "SitemapUrl [loc=" + loc + ", lastmod=" + lastmod + ", changefreq=" + changefreq + ", priority=" + priority + "]";
    }

}
