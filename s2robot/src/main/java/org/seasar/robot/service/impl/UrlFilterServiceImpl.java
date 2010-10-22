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

import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.seasar.robot.helper.MemoryDataHelper;
import org.seasar.robot.service.UrlFilterService;

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
     * org.seasar.robot.service.impl.UrlFilterService#addIncludeUrlFilter(java
     * .lang.String, java.lang.String)
     */
    public void addIncludeUrlFilter(final String sessionId, final String url) {
        dataHelper.addIncludeUrlPattern(sessionId, url);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.service.impl.UrlFilterService#addIncludeUrlFilter(java
     * .lang.String, java.util.List)
     */
    public void addIncludeUrlFilter(final String sessionId,
            final List<String> urlList) {
        for (String url : urlList) {
            dataHelper.addIncludeUrlPattern(sessionId, url);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.service.impl.UrlFilterService#addExcludeUrlFilter(java
     * .lang.String, java.lang.String)
     */
    public void addExcludeUrlFilter(final String sessionId, final String url) {
        dataHelper.addExcludeUrlPattern(sessionId, url);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.service.impl.UrlFilterService#addExcludeUrlFilter(java
     * .lang.String, java.util.List)
     */
    public void addExcludeUrlFilter(final String sessionId,
            final List<String> urlList) {
        for (String url : urlList) {
            dataHelper.addExcludeUrlPattern(sessionId, url);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.service.impl.UrlFilterService#delete(java.lang.String)
     */
    public void delete(final String sessionId) {
        dataHelper.clearUrlPattern(sessionId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.service.impl.UrlFilterService#deleteAll()
     */
    public void deleteAll() {
        dataHelper.clearUrlPattern();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.service.impl.UrlFilterService#getIncludeUrlPatternList
     * (java.lang.String)
     */
    public List<Pattern> getIncludeUrlPatternList(final String sessionId) {
        return dataHelper.getIncludeUrlPatternList(sessionId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.service.impl.UrlFilterService#getExcludeUrlPatternList
     * (java.lang.String)
     */
    public List<Pattern> getExcludeUrlPatternList(final String sessionId) {
        return dataHelper.getExcludeUrlPatternList(sessionId);
    }

}
