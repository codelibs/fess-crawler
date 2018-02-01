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

import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.codelibs.fess.crawler.rule.Rule;

/**
 * @author shinsuke
 *
 */
public abstract class AbstractRule implements Rule {

    private static final long serialVersionUID = 1L;

    protected String ruleId;

    protected ResponseProcessor responseProcessor;

    @Override
    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(final String ruleId) {
        this.ruleId = ruleId;
    }

    @Override
    public ResponseProcessor getResponseProcessor() {
        return responseProcessor;
    }

    public void setResponseProcessor(final ResponseProcessor responseProcessor) {
        this.responseProcessor = responseProcessor;
    }

}
