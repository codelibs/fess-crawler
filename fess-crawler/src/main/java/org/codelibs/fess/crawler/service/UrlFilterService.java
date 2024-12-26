/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Service interface for managing URL filters.
 * Provides methods to add and remove include/exclude URL filters,
 * as well as retrieve the patterns of these filters.
 */
public interface UrlFilterService {

    /**
     * Adds a URL to the include filter list for the specified session.
     *
     * @param sessionId the ID of the session for which the URL filter is being added
     * @param url the URL to be included in the filter list
     */
    void addIncludeUrlFilter(String sessionId, String url);

    /**
     * Adds a list of URLs to the include filter for a given session.
     *
     * @param sessionId the ID of the session for which the URLs should be included
     * @param urlList the list of URLs to be added to the include filter
     */
    void addIncludeUrlFilter(String sessionId, List<String> urlList);

    /**
     * Adds a URL to the exclude filter list for the specified session.
     *
     * @param sessionId the ID of the session for which the URL filter is being added
     * @param url the URL to be excluded
     */
    void addExcludeUrlFilter(String sessionId, String url);

    /**
     * Adds a list of URLs to be excluded from crawling for a specific session.
     *
     * @param sessionId the ID of the session for which the URLs should be excluded
     * @param urlList the list of URLs to be excluded
     */
    void addExcludeUrlFilter(String sessionId, List<String> urlList);

    /**
     * Deletes the URL filter associated with the specified session ID.
     *
     * @param sessionId the ID of the session whose URL filter is to be deleted
     */
    void delete(String sessionId);

    /**
     * Deletes all entries from the URL filter.
     */
    void deleteAll();

    /**
     * Retrieves a list of URL patterns to include for a given session.
     *
     * @param sessionId the ID of the session for which to retrieve the include URL patterns
     * @return a list of compiled regular expression patterns representing the URLs to include
     */
    List<Pattern> getIncludeUrlPatternList(String sessionId);

    /**
     * Retrieves a list of URL patterns to be excluded for a given session.
     *
     * @param sessionId the identifier of the session for which to retrieve the exclude URL patterns
     * @return a list of compiled regular expression patterns representing the URLs to be excluded
     */
    List<Pattern> getExcludeUrlPatternList(String sessionId);

}
