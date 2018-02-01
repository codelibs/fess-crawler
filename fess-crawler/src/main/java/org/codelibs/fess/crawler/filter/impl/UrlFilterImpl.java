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
package org.codelibs.fess.crawler.filter.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.filter.UrlFilter;
import org.codelibs.fess.crawler.service.UrlFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class UrlFilterImpl implements UrlFilter {

    private static final Logger logger = LoggerFactory.getLogger(UrlFilterImpl.class);

    @Resource
    protected CrawlerContainer crawlerContainer;

    protected String urlPattern = "^(.*:/+)([^/]*)(.*)$";

    protected String includeFilteringPattern;

    protected String excludeFilteringPattern;

    protected Set<String> cachedIncludeSet = new LinkedHashSet<>();

    protected Set<String> cachedExcludeSet = new LinkedHashSet<>();

    protected String sessionId;

    protected UrlFilterService urlFilterService;

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.filter.UrlFilter#addExclude(java.lang.String)
     */
    @Override
    public void addExclude(final String urlPattern) {
        try {
            Pattern.compile(urlPattern);
        } catch (final Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Invalid exclude pattern: " + urlPattern);
            }
            return;
        }
        if (sessionId == null) {
            cachedExcludeSet.add(urlPattern);
        } else {
            getUrlFilterService().addExcludeUrlFilter(sessionId, urlPattern);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.filter.UrlFilter#addInclude(java.lang.String)
     */
    @Override
    public void addInclude(final String urlPattern) {
        try {
            Pattern.compile(urlPattern);
        } catch (final Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Invalid include pattern: " + urlPattern);
            }
            return;
        }
        if (sessionId == null) {
            cachedIncludeSet.add(urlPattern);
        } else {
            getUrlFilterService().addIncludeUrlFilter(sessionId, urlPattern);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.filter.UrlFilter#clear()
     */
    @Override
    public void clear() {
        cachedIncludeSet.clear();
        cachedExcludeSet.clear();
        if (sessionId != null) {
            getUrlFilterService().delete(sessionId);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.filter.UrlFilter#init(java.lang.String)
     */
    @Override
    public void init(final String sessionId) {
        this.sessionId = sessionId;
        if (!cachedIncludeSet.isEmpty()) {
            try {
                getUrlFilterService().addIncludeUrlFilter(sessionId, cachedIncludeSet.stream().collect(Collectors.toList()));
            } catch (final Exception e) {
                logger.warn("Failed to add include_urls on " + sessionId, e);
            }
            cachedIncludeSet.clear();
        }
        if (!cachedExcludeSet.isEmpty()) {
            try {
                getUrlFilterService().addExcludeUrlFilter(sessionId, cachedExcludeSet.stream().collect(Collectors.toList()));
            } catch (final Exception e) {
                logger.warn("Failed to add exclude_urls on " + sessionId, e);
            }
            cachedExcludeSet.clear();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.filter.UrlFilter#match(java.lang.String)
     */
    @Override
    public boolean match(final String url) {
        final List<Pattern> includeList = getUrlFilterService()
                .getIncludeUrlPatternList(sessionId);
        final List<Pattern> excludeList = getUrlFilterService()
                .getExcludeUrlPatternList(sessionId);

        if (!includeList.isEmpty()) {
            boolean match = false;
            for (final Pattern pattern : includeList) {
                final Matcher matcher = pattern.matcher(url);
                if (matcher.matches()) {
                    match = true;
                }
            }
            if (!match) {
                return false;
            }
        }

        if (!excludeList.isEmpty()) {
            boolean match = false;
            for (final Pattern pattern : excludeList) {
                final Matcher matcher = pattern.matcher(url);
                if (matcher.matches()) {
                    match = true;
                }
            }
            if (match) {
                return false;
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.filter.UrlFilter#processUrl(java.lang.String)
     */
    @Override
    public void processUrl(final String url) {
        if (includeFilteringPattern != null) {
            addInclude(url.replaceAll(urlPattern, includeFilteringPattern));
        }
        if (excludeFilteringPattern != null) {
            addExclude(url.replaceAll(urlPattern, excludeFilteringPattern));
        }
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(final String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getIncludeFilteringPattern() {
        return includeFilteringPattern;
    }

    public void setIncludeFilteringPattern(final String includeFilteringPattern) {
        this.includeFilteringPattern = includeFilteringPattern;
    }

    public String getExcludeFilteringPattern() {
        return excludeFilteringPattern;
    }

    public void setExcludeFilteringPattern(final String excludeFilteringPattern) {
        this.excludeFilteringPattern = excludeFilteringPattern;
    }

    public UrlFilterService getUrlFilterService() {
        if (urlFilterService == null) {
            urlFilterService = crawlerContainer.getComponent("urlFilterService");
            if (urlFilterService == null) {
                throw new CrawlerSystemException("urlFilterService is not found.");
            }
        }
        return urlFilterService;
    }

    @Override
    public String toString() {
        return "UrlFilterImpl [urlPattern=" + urlPattern
                + ", includeFilteringPattern=" + includeFilteringPattern
                + ", excludeFilteringPattern=" + excludeFilteringPattern
                + ", cachedIncludeSet=" + cachedIncludeSet
                + ", cachedExcludeSet=" + cachedExcludeSet + ", sessionId="
                + sessionId + ", urlFilterService=" + urlFilterService + "]";
    }

}
