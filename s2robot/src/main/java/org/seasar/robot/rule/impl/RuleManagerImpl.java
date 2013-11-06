/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.robot.rule.impl;

import java.util.ArrayList;
import java.util.List;

import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.rule.Rule;
import org.seasar.robot.rule.RuleManager;

/**
 * @author shinsuke
 * 
 */
public class RuleManagerImpl implements RuleManager {

    private static final long serialVersionUID = 1L;

    protected List<Rule> ruleList;

    public RuleManagerImpl() {
        ruleList = new ArrayList<Rule>();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.seasar.robot.rule.RuleManager#getRule(org.seasar.robot.entity.
     * ResponseData)
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
     * org.seasar.robot.rule.RuleManager#addRule(org.seasar.robot.rule.Rule)
     */
    @Override
    public void addRule(final Rule rule) {
        ruleList.add(rule);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.rule.RuleManager#hasRule(org.seasar.robot.rule.Rule)
     */
    @Override
    public boolean hasRule(final Rule rule) {
        return ruleList.contains(rule);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.rule.RuleManager#removeRule(org.seasar.robot.rule.Rule)
     */
    @Override
    public boolean removeRule(final Rule rule) {
        return ruleList.remove(rule);
    }
}
