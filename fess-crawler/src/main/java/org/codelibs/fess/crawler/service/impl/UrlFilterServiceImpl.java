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
package org.codelibs.fess.crawler.service.impl;

import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.codelibs.fess.crawler.helper.MemoryDataHelper;
import org.codelibs.fess.crawler.service.UrlFilterService;

/**
 * @author shinsuke
 *
 */
public class UrlFilterServiceImpl implements UrlFilterService {

    @Resource
    protected MemoryDataHelper dataHelper;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.service.impl.UrlFilterService#addIncludeUrlFilter(java
     * .lang.String, java.lang.String)
     */
    @Override
    public void addIncludeUrlFilter(final String sessionId, final String url) {
        dataHelper.addIncludeUrlPattern(sessionId, url);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.service.impl.UrlFilterService#addIncludeUrlFilter(java
     * .lang.String, java.util.List)
     */
    @Override
    public void addIncludeUrlFilter(final String sessionId,
            final List<String> urlList) {
        for (final String url : urlList) {
            dataHelper.addIncludeUrlPattern(sessionId, url);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.service.impl.UrlFilterService#addExcludeUrlFilter(java
     * .lang.String, java.lang.String)
     */
    @Override
    public void addExcludeUrlFilter(final String sessionId, final String url) {
        dataHelper.addExcludeUrlPattern(sessionId, url);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.service.impl.UrlFilterService#addExcludeUrlFilter(java
     * .lang.String, java.util.List)
     */
    @Override
    public void addExcludeUrlFilter(final String sessionId,
            final List<String> urlList) {
        for (final String url : urlList) {
            dataHelper.addExcludeUrlPattern(sessionId, url);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.service.impl.UrlFilterService#delete(java.lang.String)
     */
    @Override
    public void delete(final String sessionId) {
        dataHelper.clearUrlPattern(sessionId);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.service.impl.UrlFilterService#deleteAll()
     */
    @Override
    public void deleteAll() {
        dataHelper.clearUrlPattern();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.service.impl.UrlFilterService#getIncludeUrlPatternList
     * (java.lang.String)
     */
    @Override
    public List<Pattern> getIncludeUrlPatternList(final String sessionId) {
        return dataHelper.getIncludeUrlPatternList(sessionId);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.service.impl.UrlFilterService#getExcludeUrlPatternList
     * (java.lang.String)
     */
    @Override
    public List<Pattern> getExcludeUrlPatternList(final String sessionId) {
        return dataHelper.getExcludeUrlPatternList(sessionId);
    }

}
