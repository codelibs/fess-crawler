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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shinsuke
 *
 */
public class UrlFilterImpl extends AbstractUrlFilter {
    public String urlPattern = "^(.*:/+)([^/]*)(.*)$";

    public String includeFilteringPattern;

    public String excludeFilteringPattern;

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

    /* (non-Javadoc)
     * @see org.seasar.robot.filter.impl.AbstractUrlFilter#processUrl(java.lang.String)
     */
    @Override
    public void processUrl(String url) {
        if (includeFilteringPattern != null) {
            addInclude(url.replaceAll(urlPattern, includeFilteringPattern));
        }
        if (excludeFilteringPattern != null) {
            addExclude(url.replaceAll(urlPattern, excludeFilteringPattern));
        }
    }
}
