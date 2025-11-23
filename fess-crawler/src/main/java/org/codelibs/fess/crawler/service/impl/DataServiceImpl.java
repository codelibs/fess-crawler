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
package org.codelibs.fess.crawler.service.impl;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.AccessResultDataImpl;
import org.codelibs.fess.crawler.entity.AccessResultImpl;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.helper.MemoryDataHelper;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.util.AccessResultCallback;

import jakarta.annotation.Resource;

/**
 * Implementation of the {@link DataService} interface for managing access result data.
 * This class provides methods to store, retrieve, update, and delete access results,
 * as well as to iterate over them. It uses a {@link MemoryDataHelper} to store the data in memory.
 *
 * <p>
 * The class uses a static {@code idCount} to generate unique IDs for each access result.
 * The {@code idCountLock} object is used to synchronize access to the {@code idCount} variable,
 * ensuring that IDs are generated in a thread-safe manner.
 * </p>
 *
 * <p>
 * The class also provides methods to get the count of access results for a given session,
 * to delete all access results for a given session, and to delete all access results.
 * </p>
 *
 * <p>
 * The class is a singleton, and is injected using the {@link Resource} annotation.
 * </p>
 *
 */
public class DataServiceImpl implements DataService<AccessResultImpl<Long>> {

    /** Counter for generating unique IDs */
    protected static volatile long idCount = 0L;

    /** Lock object for synchronizing access to idCount */
    private static Object idCountLock = new Object();

    /** Helper for managing access result data in memory */
    @Resource
    protected MemoryDataHelper dataHelper;

    /**
     * Constructs a new DataServiceImpl.
     */
    public DataServiceImpl() {
        // Default constructor
    }

    /**
     * Stores an access result in the data store.
     *
     * @param accessResult the access result to store
     * @throws CrawlerSystemException if the access result is null or already exists
     */
    @Override
    public void store(final AccessResultImpl<Long> accessResult) {
        if (accessResult == null) {
            throw new CrawlerSystemException("AccessResult is null. Cannot store null access result.");
        }

        synchronized (idCountLock) {
            idCount++;
            accessResult.setId(idCount);
            AccessResultData<Long> accessResultData = accessResult.getAccessResultData();
            if (accessResultData == null) {
                accessResultData = new AccessResultDataImpl<>();
                accessResultData.setTransformerName(Constants.NO_TRANSFORMER);
                accessResult.setAccessResultData(accessResultData);
            }
            accessResultData.setId(accessResult.getId());

            final Map<String, AccessResultImpl<Long>> arMap = dataHelper.getAccessResultMap(accessResult.getSessionId());
            if (arMap.containsKey(accessResult.getUrl())) {
                throw new CrawlerSystemException("AccessResult for URL '" + accessResult.getUrl() + "' already exists. Duplicate URLs are not allowed.");
            }
            arMap.put(accessResult.getUrl(), accessResult);
        }

    }

    /**
     * Gets the count of access results for the specified session.
     *
     * @param sessionId the session ID
     * @return the count of access results
     */
    @Override
    public int getCount(final String sessionId) {
        return dataHelper.getAccessResultMap(sessionId).size();
    }

    /**
     * Deletes all access results for the specified session.
     *
     * @param sessionId the session ID
     */
    @Override
    public void delete(final String sessionId) {
        dataHelper.deleteAccessResultMap(sessionId);
    }

    /**
     * Deletes all access results from all sessions.
     */
    @Override
    public void deleteAll() {
        dataHelper.clearUrlQueueList();
    }

    /**
     * Gets an access result for the specified session and URL.
     *
     * @param sessionId the session ID
     * @param url the URL
     * @return the access result or null if not found
     */
    @Override
    public AccessResultImpl<Long> getAccessResult(final String sessionId, final String url) {
        return dataHelper.getAccessResultMap(sessionId).get(url);
    }

    /**
     * Gets a list of access results for the specified URL.
     *
     * @param url the URL
     * @param hasData whether the result should have data
     * @return the list of access results
     */
    @Override
    public List<AccessResultImpl<Long>> getAccessResultList(final String url, final boolean hasData) {
        return dataHelper.getAccessResultList(url);
    }

    /**
     * Iterates over all access results for the specified session.
     *
     * @param sessionId the session ID
     * @param accessResultCallback the callback to invoke for each access result
     */
    @Override
    public void iterate(final String sessionId, final AccessResultCallback<AccessResultImpl<Long>> accessResultCallback) {
        final Map<String, AccessResultImpl<Long>> arMap = dataHelper.getAccessResultMap(sessionId);
        for (final Map.Entry<String, AccessResultImpl<Long>> entry : arMap.entrySet()) {
            accessResultCallback.iterate(entry.getValue());
        }
    }

    /**
     * Updates an access result in the data store.
     *
     * @param accessResult the access result to update
     * @throws CrawlerSystemException if the access result is not found
     */
    @Override
    public void update(final AccessResultImpl<Long> accessResult) {
        final Map<String, AccessResultImpl<Long>> arMap = dataHelper.getAccessResultMap(accessResult.getSessionId());
        if (!arMap.containsKey(accessResult.getUrl())) {
            throw new CrawlerSystemException(accessResult.getUrl() + " is not found.");
        }
        arMap.put(accessResult.getUrl(), accessResult);
    }

    /**
     * Updates the given list of access results.
     * @param accessResultList The list of access results to update.
     */
    @Override
    public void update(final List<AccessResultImpl<Long>> accessResultList) {
        for (final AccessResultImpl<Long> accessResult : accessResultList) {
            update(accessResult);
        }
    }
}
