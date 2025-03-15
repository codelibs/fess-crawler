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

import java.io.Serializable;

import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.processor.ResponseProcessor;

/**
 * The Rule interface defines the contract for implementing rules that can be applied to
 * response data in a web crawler. Implementations of this interface should provide logic
 * to determine if a given response data matches the rule, retrieve the rule's identifier,
 * and obtain the associated response processor.
 */
public interface Rule extends Serializable {

    /**
     * Determines if the given response data matches the rule.
     *
     * @param responseData the response data to be evaluated
     * @return true if the response data matches the rule, false otherwise
     */
    boolean match(ResponseData responseData);

    /**
     * Retrieves the unique identifier for the rule.
     *
     * @return the rule's unique identifier as a String.
     */
    String getRuleId();

    /**
     * Retrieves the response processor associated with this rule.
     *
     * @return the response processor
     */
    ResponseProcessor getResponseProcessor();

}
