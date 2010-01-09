/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.robot.entity;

import org.apache.commons.lang.StringUtils;

/**
 * @author shinsuke
 *
 */
public class SitemapFile implements Sitemap {

    private static final long serialVersionUID = 1L;

    /**
     * Identifies the location of the Sitemap.
     * This location can be a Sitemap, an Atom file, RSS file or a simple text file.
     */
    private String loc;

    /**
     *  Identifies the time that the corresponding Sitemap file was modified. 
     *  It does not correspond to the time that any of the pages listed in that 
     *  Sitemap were changed. The value for the lastmod tag should be in  
     *  W3C Datetime format.
     *  
     *  By providing the last modification timestamp, you enable search engine crawlers 
     *  to retrieve only a subset of the Sitemaps in the index i.e. a crawler may only 
     *  retrieve Sitemaps that were modified since a certain date. This incremental Sitemap 
     *  fetching mechanism allows for the rapid discovery of new URLs on very large sites.
     */
    private String lastmod;

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.Sitemap#getLoc()
     */
    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.Sitemap#getLastmod()
     */
    public String getLastmod() {
        return lastmod;
    }

    public void setLastmod(String lastmod) {
        this.lastmod = lastmod;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SitemapFile)) {
            return false;
        }
        SitemapFile sitemapUrl = (SitemapFile) obj;
        if (StringUtils.equals(loc, sitemapUrl.loc)
                && StringUtils.equals(lastmod, sitemapUrl.lastmod)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return loc.hashCode() + lastmod.hashCode();
    }
}
