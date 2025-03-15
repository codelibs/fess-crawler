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
package org.codelibs.fess.crawler.rule;

import org.codelibs.fess.crawler.entity.ResponseData;

/**
 * The RuleManager interface provides methods to manage rules for processing response data.
 * It allows adding, retrieving, and removing rules, as well as checking for their existence.
 */
public interface RuleManager {

    /**
     * Retrieves the rule associated with the given response data.
     *
     * @param responseData the response data for which the rule is to be retrieved
     * @return the rule associated with the given response data
     */
    Rule getRule(ResponseData responseData);

    /**
     * Adds a new rule to the rule manager.
     *
     * @param rule the rule to be added
     */
    void addRule(Rule rule);

    /**
     * Adds a rule to the specified index.
     *
     * @param index the position at which the rule should be added
     * @param rule the rule to be added
     */
    void addRule(int index, Rule rule);

    /**
     * Removes the specified rule from the rule manager.
     *
     * @param rule the rule to be removed
     * @return true if the rule was successfully removed, false otherwise
     */
    boolean removeRule(Rule rule);

    /**
     * Checks if the specified rule exists.
     *
     * @param rule the rule to check for existence
     * @return true if the rule exists, false otherwise
     */
    boolean hasRule(Rule rule);
}
