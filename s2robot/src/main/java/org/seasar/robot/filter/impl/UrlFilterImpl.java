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
package org.seasar.robot.filter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.seasar.framework.container.SingletonS2Container;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.filter.UrlFilter;
import org.seasar.robot.service.UrlFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class UrlFilterImpl implements UrlFilter {

    private static final Logger logger = LoggerFactory // NOPMD
            .getLogger(UrlFilterImpl.class);

    protected String urlPattern = "^(.*:/+)([^/]*)(.*)$";

    protected String includeFilteringPattern;

    protected String excludeFilteringPattern;

    protected List<String> cachedIncludeList = new ArrayList<String>();

    protected List<String> cachedExcludeList = new ArrayList<String>();

    protected String sessionId;

    protected UrlFilterService urlFilterService;

    /* (non-Javadoc)
     * @see org.seasar.robot.filter.UrlFilter#addExclude(java.lang.String)
     */
    public void addExclude(String urlPattern) {
        try {
            Pattern.compile(urlPattern);
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Invalid exclude pattern: " + urlPattern);
            }
            return;
        }
        if (sessionId == null) {
            cachedExcludeList.add(urlPattern);
        } else {
            getUrlFilterService().addExcludeUrlFilter(sessionId, urlPattern);
        }
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.filter.UrlFilter#addInclude(java.lang.String)
     */
    public void addInclude(String urlPattern) {
        try {
            Pattern.compile(urlPattern);
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Invalid include pattern: " + urlPattern);
            }
            return;
        }
        if (sessionId == null) {
            cachedIncludeList.add(urlPattern);
        } else {
            getUrlFilterService().addIncludeUrlFilter(sessionId, urlPattern);
        }
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.filter.UrlFilter#clear()
     */
    public void clear() {
        cachedIncludeList.clear();
        cachedExcludeList.clear();
        if (sessionId != null) {
            getUrlFilterService().delete(sessionId);
        }
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.filter.UrlFilter#init(java.lang.String)
     */
    public void init(String sessionId) {
        this.sessionId = sessionId;
        if (!cachedIncludeList.isEmpty()) {
            getUrlFilterService().addIncludeUrlFilter(sessionId,
                    cachedIncludeList);
            cachedIncludeList.clear();
        }
        if (!cachedExcludeList.isEmpty()) {
            getUrlFilterService().addExcludeUrlFilter(sessionId,
                    cachedExcludeList);
            cachedExcludeList.clear();
        }
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.filter.UrlFilter#match(java.lang.String)
     */
    public boolean match(String url) {
        List<Pattern> includeList = getUrlFilterService()
                .getIncludeUrlPatternList(sessionId);
        List<Pattern> excludeList = getUrlFilterService()
                .getExcludeUrlPatternList(sessionId);

        if (!includeList.isEmpty()) {
            boolean match = false;
            for (Pattern pattern : includeList) {
                Matcher matcher = pattern.matcher(url);
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
            for (Pattern pattern : excludeList) {
                Matcher matcher = pattern.matcher(url);
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

    /* (non-Javadoc)
     * @see org.seasar.robot.filter.UrlFilter#processUrl(java.lang.String)
     */
    public void processUrl(String url) {
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

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getIncludeFilteringPattern() {
        return includeFilteringPattern;
    }

    public void setIncludeFilteringPattern(String includeFilteringPattern) {
        this.includeFilteringPattern = includeFilteringPattern;
    }

    public String getExcludeFilteringPattern() {
        return excludeFilteringPattern;
    }

    public void setExcludeFilteringPattern(String excludeFilteringPattern) {
        this.excludeFilteringPattern = excludeFilteringPattern;
    }

    public UrlFilterService getUrlFilterService() {
        if (urlFilterService == null) {
            urlFilterService = SingletonS2Container
                    .getComponent("urlFilterService");
            if (urlFilterService == null) {
                throw new RobotSystemException("urlFilterService is not found.");
            }
        }
        return urlFilterService;
    }

}
