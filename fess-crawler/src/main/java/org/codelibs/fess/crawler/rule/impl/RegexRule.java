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
package org.codelibs.fess.crawler.rule.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.crawler.entity.ResponseData;

/**
 * @author shinsuke
 *
 */
public class RegexRule extends AbstractRule {

    private static final long serialVersionUID = 1L;

    protected boolean defaultRule = false;

    protected boolean allRequired = true;

    protected Map<String, Pattern> regexMap = new HashMap<>();

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.rule.impl.AbstractRule#match(org.codelibs.fess.crawler.entity.ResponseData)
     */
    @Override
    public boolean match(final ResponseData responseData) {
        if (defaultRule) {
            return true;
        }

        final Map<String, Object> map = new HashMap<>();
        BeanUtil.copyBeanToMap(responseData, map,
                option -> option.excludeWhitespace());
        for (final Map.Entry<String, Pattern> entry : regexMap.entrySet()) {
            String value = "";
            final Object obj = map.get(entry.getKey());
            if (obj != null) {
                value = obj.toString();
            }
            final Matcher matcher = entry.getValue().matcher(value);
            if (allRequired) {
                if (!matcher.matches()) {
                    return false;
                }
            } else {
                if (matcher.matches()) {
                    return true;
                }
            }
        }

        return allRequired;
    }

    public void addRule(final String key, final String regex) {
        regexMap.put(key, Pattern.compile(regex));
    }

    public void addRule(final String key, final Pattern pattern) {
        regexMap.put(key, pattern);
    }

    public boolean isDefaultRule() {
        return defaultRule;
    }

    public void setDefaultRule(final boolean defaultRule) {
        this.defaultRule = defaultRule;
    }

    public boolean isAllRequired() {
        return allRequired;
    }

    public void setAllRequired(final boolean allRequired) {
        this.allRequired = allRequired;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof RegexRule) {
            final RegexRule rule = (RegexRule) obj;
            if (allRequired == rule.isAllRequired()
                    && defaultRule == rule.isDefaultRule()
                    && regexMap.equals(rule.regexMap)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = regexMap.hashCode();
        if (defaultRule) {
            hash = hash * 31 + 1;
        }
        if (allRequired) {
            hash = hash * 31 + 2;
        }
        return hash;
    }

    public void setRegexMap(final Map<String, Pattern> regexMap) {
        this.regexMap = regexMap;
    }
}
