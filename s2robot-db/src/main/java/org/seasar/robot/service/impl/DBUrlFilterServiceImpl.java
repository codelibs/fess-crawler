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
package org.seasar.robot.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.seasar.robot.db.cbean.UrlFilterCB;
import org.seasar.robot.db.exbhv.UrlFilterBhv;
import org.seasar.robot.db.exentity.UrlFilter;
import org.seasar.robot.service.UrlFilterService;

/**
 * @author shinsuke
 *
 */
public class DBUrlFilterServiceImpl implements UrlFilterService {

    private static final String INCLUDE_FILTER_TYPE = "I";

    private static final String EXCLUDE_FILTER_TYPE = "E";

    @Resource
    protected UrlFilterBhv urlFilterBhv;

    /* (non-Javadoc)
     * @see org.seasar.robot.service.impl.UrlFilterService#addIncludeUrlFilter(java.lang.String, java.lang.String)
     */
    public void addIncludeUrlFilter(String sessionId, String url) {
        addUrlFilter(sessionId, url, INCLUDE_FILTER_TYPE);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.impl.UrlFilterService#addIncludeUrlFilter(java.lang.String, java.util.List)
     */
    public void addIncludeUrlFilter(String sessionId, List<String> urlList) {
        addUrlFilter(sessionId, urlList, INCLUDE_FILTER_TYPE);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.impl.UrlFilterService#addExcludeUrlFilter(java.lang.String, java.lang.String)
     */
    public void addExcludeUrlFilter(String sessionId, String url) {
        addUrlFilter(sessionId, url, EXCLUDE_FILTER_TYPE);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.impl.UrlFilterService#addExcludeUrlFilter(java.lang.String, java.util.List)
     */
    public void addExcludeUrlFilter(String sessionId, List<String> urlList) {
        addUrlFilter(sessionId, urlList, EXCLUDE_FILTER_TYPE);
    }

    private void addUrlFilter(String sessionId, String url, String filterType) {
        UrlFilter urlFilter = new UrlFilter();
        urlFilter.setSessionId(sessionId);
        urlFilter.setUrl(url);
        urlFilter.setFilterType(filterType);
        urlFilter.setCreateTime(new Timestamp(System.currentTimeMillis()));
        urlFilterBhv.insert(urlFilter);
    }

    private void addUrlFilter(String sessionId, List<String> urlList,
            String filterType) {
        List<UrlFilter> urlFilterList = new ArrayList<UrlFilter>();
        for (String url : urlList) {
            UrlFilter urlFilter = new UrlFilter();
            urlFilter.setSessionId(sessionId);
            urlFilter.setUrl(url);
            urlFilter.setFilterType(filterType);
            urlFilter.setCreateTime(new Timestamp(System.currentTimeMillis()));
            urlFilterList.add(urlFilter);
        }
        urlFilterBhv.batchInsert(urlFilterList);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.impl.UrlFilterService#delete(java.lang.String)
     */
    public void delete(String sessionId) {
        urlFilterBhv.deleteBySessionId(sessionId);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.impl.UrlFilterService#deleteAll()
     */
    public void deleteAll() {
        urlFilterBhv.deleteAll();
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.impl.UrlFilterService#getIncludeUrlPatternList(java.lang.String)
     */
    public List<Pattern> getIncludeUrlPatternList(String sessionId) {
        // TODO performance
        return getUrlPatternList(sessionId, INCLUDE_FILTER_TYPE);

    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.impl.UrlFilterService#getExcludeUrlPatternList(java.lang.String)
     */
    public List<Pattern> getExcludeUrlPatternList(String sessionId) {
        // TODO performance
        return getUrlPatternList(sessionId, EXCLUDE_FILTER_TYPE);
    }

    private List<Pattern> getUrlPatternList(String sessionId, String filterType) {
        UrlFilterCB cb = new UrlFilterCB();
        cb.query().setSessionId_Equal(sessionId);
        cb.query().setFilterType_Equal(filterType);
        List<UrlFilter> urlFilterList = urlFilterBhv.selectList(cb);

        List<Pattern> urlPatternList = new ArrayList<Pattern>();
        for (UrlFilter urlFilter : urlFilterList) {
            urlPatternList.add(Pattern.compile(urlFilter.getUrl()));
        }
        return urlPatternList;
    }
}
