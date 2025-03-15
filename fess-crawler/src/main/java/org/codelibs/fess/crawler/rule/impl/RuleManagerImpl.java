/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.rule.Rule;
import org.codelibs.fess.crawler.rule.RuleManager;

/**
 * Implementation of the {@link RuleManager} interface.
 * Manages a list of rules for the crawler.
 * Provides methods to add, remove, check, and retrieve rules.
 *
 */
public class RuleManagerImpl implements RuleManager {

    protected final List<Rule> ruleList = new ArrayList<>();

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.rule.RuleManager#getRule(org.codelibs.fess.crawler.entity.ResponseData)
     */
    @Override
    public Rule getRule(final ResponseData responseData) {
        for (final Rule rule : ruleList) {
            if (rule.match(responseData)) {
                return rule;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.rule.RuleManager#addRule(org.codelibs.fess.crawler.rule.Rule)
     */
    @Override
    public void addRule(final Rule rule) {
        ruleList.add(rule);
    }

    @Override
    public void addRule(final int index, final Rule rule) {
        ruleList.add(index, rule);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.rule.RuleManager#hasRule(org.codelibs.fess.crawler.rule.Rule)
     */
    @Override
    public boolean hasRule(final Rule rule) {
        return ruleList.contains(rule);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.rule.RuleManager#removeRule(org.codelibs.fess.crawler.rule.Rule)
     */
    @Override
    public boolean removeRule(final Rule rule) {
        return ruleList.remove(rule);
    }
}
