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
package org.codelibs.fess.crawler.filter;

/**
 * UrlFilter checks if a given url is a target one.
 *
 * @author shinsuke
 *
 */
public interface UrlFilter {

    /**
     * Initialize a url filter by sessionId.
     *
     * @param sessionId Session ID
     */
    void init(String sessionId);

    /**
     * Check if a given url is a target.
     *
     * @param url URL
     * @return true if url is matched
     */
    boolean match(String url);

    /**
     * Add an url pattern as a target.
     *
     * @param urlPattern Regular expression that is crawled
     */
    void addInclude(String urlPattern);

    /**
     * Add an url pattern as a non-target.
     *
     * @param urlPattern Regular expression that is not crawled
     */
    void addExclude(String urlPattern);

    /**
     * Process an url when it's added as a seed url.
     *
     * @param url URL
     */
    void processUrl(String url);

    /**
     * Clear this filter.
     */
    void clear();
}
