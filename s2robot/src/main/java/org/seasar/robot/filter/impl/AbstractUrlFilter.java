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
import java.util.regex.Pattern;

import org.seasar.framework.util.StringUtil;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.filter.UrlFilter;

/**
 * @author shinsuke
 *
 */
public abstract class AbstractUrlFilter implements UrlFilter {

    protected List<Pattern> includeList = new ArrayList<Pattern>();

    protected List<Pattern> excludeList = new ArrayList<Pattern>();

    public AbstractUrlFilter() {
    }

    public void addInclude(String urlPattern) {
        if (StringUtil.isBlank(urlPattern)) {
            throw new RobotSystemException("urlPattern is null");
        }
        synchronized (includeList) {
            List<Pattern> list = new ArrayList<Pattern>();
            list.addAll(includeList);
            list.add(Pattern.compile(urlPattern));
            includeList = list;
        }
    }

    public void addExclude(String urlPattern) {
        if (StringUtil.isBlank(urlPattern)) {
            throw new RobotSystemException("urlPattern is null");
        }
        synchronized (excludeList) {
            List<Pattern> list = new ArrayList<Pattern>();
            list.addAll(excludeList);
            list.add(Pattern.compile(urlPattern));
            excludeList = list;
        }
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.filter.UrlFilter#match(java.lang.String)
     */
    public abstract boolean match(String url);

    /* (non-Javadoc)
     * @see org.seasar.robot.filter.UrlFilter#processUrl(java.lang.String)
     */
    public abstract void processUrl(String url);
}