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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shinsuke
 *
 */
public class SitemapSet implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String URLSET = "UrlSet";

    public static final String INDEX = "Index";

    private List<Sitemap> sitemapList = new ArrayList<Sitemap>();

    private String type = URLSET;

    public void addSitemap(Sitemap sitemap) {
        sitemapList.add(sitemap);
    }

    public void removeSitemap(Sitemap sitemap) {
        sitemapList.remove(sitemap);
    }

    public Sitemap[] getSitemaps() {
        return sitemapList.toArray(new Sitemap[sitemapList.size()]);
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUrlSet() {
        return URLSET.equals(type);
    }

    public boolean isIndex() {
        return INDEX.equals(type);
    }
}
