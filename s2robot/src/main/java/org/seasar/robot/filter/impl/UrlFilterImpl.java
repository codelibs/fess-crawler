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

import org.seasar.framework.util.StringUtil;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.filter.UrlFilter;

/**
 * @author shinsuke
 *
 */
public class UrlFilterImpl implements UrlFilter {
    public List<Pattern> includeList;

    public List<Pattern> excludeList;

    public UrlFilterImpl() {
        includeList = new ArrayList<Pattern>();
        excludeList = new ArrayList<Pattern>();
    }

    public void addInclude(String urlPattern) {
        if (StringUtil.isBlank(urlPattern)) {
            throw new RobotSystemException("urlPattern is null");
        }
        includeList.add(Pattern.compile(urlPattern));
    }

    public void addExclude(String urlPattern) {
        if (StringUtil.isBlank(urlPattern)) {
            throw new RobotSystemException("urlPattern is null");
        }
        excludeList.add(Pattern.compile(urlPattern));
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.filter.UrlFilter#match(java.lang.String)
     */
    public boolean match(String url) {
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
}
