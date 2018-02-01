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
package org.codelibs.fess.crawler.entity;

import org.codelibs.core.lang.StringUtil;

/**
 * @author shinsuke
 *
 */
public class SitemapUrl implements Sitemap {

    private static final long serialVersionUID = 1L;

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

    @Override
    public String getLoc() {
        return loc;
    }

    public void setLoc(final String loc) {
        this.loc = loc;
    }

    @Override
    public String getLastmod() {
        return lastmod;
    }

    public void setLastmod(final String lastmod) {
        this.lastmod = lastmod;
    }

    public String getChangefreq() {
        return changefreq;
    }

    public void setChangefreq(final String changefreq) {
        this.changefreq = changefreq;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(final String priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof SitemapUrl)) {
            return false;
        }
        final SitemapUrl sitemapUrl = (SitemapUrl) obj;
        if (StringUtil.equals(loc, sitemapUrl.loc)
                && StringUtil.equals(changefreq, sitemapUrl.changefreq)
                && StringUtil.equals(lastmod, sitemapUrl.lastmod)
                && StringUtil.equals(priority, sitemapUrl.priority)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return loc.hashCode() + changefreq.hashCode() + lastmod.hashCode()
                + priority.hashCode();
    }

    @Override
    public String toString() {
        return "SitemapUrl [loc=" + loc + ", lastmod=" + lastmod
                + ", changefreq=" + changefreq + ", priority=" + priority + "]";
    }

}
