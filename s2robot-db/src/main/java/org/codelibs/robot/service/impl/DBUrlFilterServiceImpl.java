/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.codelibs.robot.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.codelibs.robot.db.cbean.UrlFilterCB;
import org.codelibs.robot.db.exbhv.UrlFilterBhv;
import org.codelibs.robot.db.exentity.UrlFilter;
import org.codelibs.robot.service.UrlFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class DBUrlFilterServiceImpl implements UrlFilterService {
    private final Logger logger = LoggerFactory
        .getLogger(DBUrlFilterServiceImpl.class);

    private static final String INCLUDE_FILTER_TYPE = "I";

    private static final String EXCLUDE_FILTER_TYPE = "E";

    @Resource
    protected UrlFilterBhv urlFilterBhv;

    protected List<Pattern> includeUrlPatternListCache = null;

    protected List<Pattern> excludeUrlPatternListCache = null;

    protected int maxCacheSize = 10000;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.robot.service.impl.UrlFilterService#addIncludeUrlFilter(java
     * .lang.String, java.lang.String)
     */
    @Override
    public void addIncludeUrlFilter(final String sessionId, final String url) {
        addUrlFilter(sessionId, url, INCLUDE_FILTER_TYPE);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.robot.service.impl.UrlFilterService#addIncludeUrlFilter(java
     * .lang.String, java.util.List)
     */
    @Override
    public void addIncludeUrlFilter(final String sessionId,
            final List<String> urlList) {
        addUrlFilter(sessionId, urlList, INCLUDE_FILTER_TYPE);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.robot.service.impl.UrlFilterService#addExcludeUrlFilter(java
     * .lang.String, java.lang.String)
     */
    @Override
    public void addExcludeUrlFilter(final String sessionId, final String url) {
        addUrlFilter(sessionId, url, EXCLUDE_FILTER_TYPE);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.robot.service.impl.UrlFilterService#addExcludeUrlFilter(java
     * .lang.String, java.util.List)
     */
    @Override
    public void addExcludeUrlFilter(final String sessionId,
            final List<String> urlList) {
        addUrlFilter(sessionId, urlList, EXCLUDE_FILTER_TYPE);
    }

    protected void addUrlFilter(final String sessionId, final String url,
            final String filterType) {
        try {
            Pattern.compile(url);
        } catch (final Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Invalid url filter pattern: " + url);
            }
            return;
        }
        clearCache();
        final UrlFilter urlFilter = new UrlFilter();
        urlFilter.setSessionId(sessionId);
        urlFilter.setUrl(url);
        urlFilter.setFilterType(filterType);
        urlFilter.setCreateTime(new Timestamp(System.currentTimeMillis()));
        urlFilterBhv.insert(urlFilter);
    }

    protected void addUrlFilter(final String sessionId,
            final List<String> urlList, final String filterType) {
        clearCache();
        final List<UrlFilter> urlFilterList = new ArrayList<>();
        for (final String url : urlList) {
            try {
                Pattern.compile(url);
            } catch (final Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Invalid url filter pattern: " + url);
                }
                continue;
            }
            final UrlFilter urlFilter = new UrlFilter();
            urlFilter.setSessionId(sessionId);
            urlFilter.setUrl(url);
            urlFilter.setFilterType(filterType);
            urlFilter.setCreateTime(new Timestamp(System.currentTimeMillis()));
            urlFilterList.add(urlFilter);
        }
        urlFilterBhv.batchInsert(urlFilterList);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.robot.service.impl.UrlFilterService#delete(java.lang.String)
     */
    @Override
    public void delete(final String sessionId) {
        clearCache();
        urlFilterBhv.deleteBySessionId(sessionId);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.service.impl.UrlFilterService#deleteAll()
     */
    @Override
    public void deleteAll() {
        clearCache();
        urlFilterBhv.deleteAll();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.robot.service.impl.UrlFilterService#getIncludeUrlPatternList
     * (java.lang.String)
     */
    @Override
    public List<Pattern> getIncludeUrlPatternList(final String sessionId) {
        if (includeUrlPatternListCache == null) {
            final List<Pattern> urlPatternList =
                getUrlPatternList(sessionId, INCLUDE_FILTER_TYPE);
            if (urlPatternList.size() < maxCacheSize) {
                includeUrlPatternListCache = urlPatternList;
            }
            return urlPatternList;
        }
        return includeUrlPatternListCache;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.robot.service.impl.UrlFilterService#getExcludeUrlPatternList
     * (java.lang.String)
     */
    @Override
    public List<Pattern> getExcludeUrlPatternList(final String sessionId) {
        if (excludeUrlPatternListCache == null) {
            final List<Pattern> urlPatternList =
                getUrlPatternList(sessionId, EXCLUDE_FILTER_TYPE);
            if (urlPatternList.size() < maxCacheSize) {
                excludeUrlPatternListCache = urlPatternList;
            }
            return urlPatternList;
        }
        return excludeUrlPatternListCache;
    }

    protected List<Pattern> getUrlPatternList(final String sessionId,
            final String filterType) {
        final UrlFilterCB cb = new UrlFilterCB();
        cb.query().setSessionId_Equal(sessionId);
        cb.query().setFilterType_Equal(filterType);
        final List<UrlFilter> urlFilterList = urlFilterBhv.selectList(cb);

        final List<Pattern> urlPatternList = new ArrayList<>();
        for (final UrlFilter urlFilter : urlFilterList) {
            urlPatternList.add(Pattern.compile(urlFilter.getUrl()));
        }
        return urlPatternList;
    }

    protected void clearCache() {
        includeUrlPatternListCache = null;
        excludeUrlPatternListCache = null;
    }

    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    public void setMaxCacheSize(final int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }
}
