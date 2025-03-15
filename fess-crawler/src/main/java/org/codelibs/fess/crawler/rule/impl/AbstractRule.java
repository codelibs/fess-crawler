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

import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.codelibs.fess.crawler.rule.Rule;
import org.codelibs.fess.crawler.rule.RuleManager;

import jakarta.annotation.Resource;

/**
 * Abstract base class for implementing {@link Rule} interfaces.
 * Provides common functionality and properties for crawler rules.
 *
 * <p>
 * This class handles the registration of rules with the {@link RuleManager}
 * and provides getter and setter methods for common properties such as
 * {@code ruleId} and {@code responseProcessor}.
 * </p>
 *
 * <p>
 * Subclasses should extend this class and implement the abstract methods
 * defined in the {@link Rule} interface to provide specific rule logic.
 * </p>
 *
 */
public abstract class AbstractRule implements Rule {

    private static final long serialVersionUID = 1L;

    protected String ruleId;

    protected ResponseProcessor responseProcessor;

    @Resource
    protected CrawlerContainer crawlerContainer;

    /**
     * Registers this rule with the {@link RuleManager}.
     *
     * @param index the index at which the rule should be registered
     */
    public void register(final int index) {
        final RuleManager ruleManager = crawlerContainer.getComponent("ruleManager");
        ruleManager.addRule(index, this);
    }

    @Override
    public String getRuleId() {
        return ruleId;
    }

    /**
     * Sets the rule ID for this rule.
     *
     * @param ruleId the rule ID to set
     */
    public void setRuleId(final String ruleId) {
        this.ruleId = ruleId;
    }

    @Override
    public ResponseProcessor getResponseProcessor() {
        return responseProcessor;
    }

    /**
     * Sets the response processor for this rule.
     *
     * @param responseProcessor the response processor to set
     */
    public void setResponseProcessor(final ResponseProcessor responseProcessor) {
        this.responseProcessor = responseProcessor;
    }
}
