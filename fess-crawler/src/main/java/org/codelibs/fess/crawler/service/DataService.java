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
package org.codelibs.fess.crawler.service;

import java.util.List;

import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.util.AccessResultCallback;

/**
 * Interface for data service operations related to access results.
 *
 * @param <RESULT> the type of access result
 */
public interface DataService<RESULT extends AccessResult<?>> {

    /**
     * Stores the given access result.
     *
     * @param accessResult the access result to store
     */
    void store(RESULT accessResult);

    /**
     * Updates the given access result.
     *
     * @param accessResult the access result to update
     */
    void update(RESULT accessResult);

    /**
     * Updates the given list of access results.
     *
     * @param accessResult the list of access results to update
     */
    void update(List<RESULT> accessResult);

    /**
     * Returns the count of access results for the given session ID.
     *
     * @param sessionId the session ID
     * @return the count of access results
     */
    int getCount(String sessionId);

    /**
     * Deletes access results for the given session ID.
     *
     * @param sessionId the session ID
     */
    void delete(String sessionId);

    /**
     * Deletes all access results.
     */
    void deleteAll();

    /**
     * Returns the access result for the given session ID and URL.
     *
     * @param sessionId the session ID
     * @param url the URL
     * @return the access result
     */
    RESULT getAccessResult(String sessionId, String url);

    /**
     * Returns a list of access results for the given URL.
     *
     * @param url the URL
     * @param hasData whether the access results should have data
     * @return the list of access results
     */
    List<RESULT> getAccessResultList(String url, boolean hasData);

    /**
     * Iterates over access results for the given session ID and applies the callback.
     *
     * @param sessionId the session ID
     * @param accessResultCallback the callback to apply to each access result
     */
    void iterate(String sessionId, final AccessResultCallback<RESULT> accessResultCallback);

}
