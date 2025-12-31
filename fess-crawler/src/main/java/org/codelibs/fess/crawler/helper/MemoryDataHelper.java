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
package org.codelibs.fess.crawler.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.codelibs.fess.crawler.entity.AccessResultImpl;
import org.codelibs.fess.crawler.entity.UrlQueueImpl;

/**
 * The {@code MemoryDataHelper} class provides a helper for managing crawler data in memory.
 * It stores URL queues, access results, and URL patterns for inclusion and exclusion.
 * This class is designed to be used in a single-threaded environment or with external synchronization.
 *
 * <p>It uses {@code Map} and {@code Queue} data structures to hold the data. The class provides
 * methods to add, remove, clear, and retrieve data from these structures.
 *
 * <p>The class is thread-safe due to the use of {@code synchronized} keyword on methods that
 * modify the internal data structures. The internal maps are also declared as {@code volatile}
 * to ensure visibility of changes across threads.
 *
 * <p>The class also provides methods to manage URL patterns for inclusion and exclusion, which
 * are stored as {@code Pattern} objects.
 */
public class MemoryDataHelper {
    /** Map of session IDs to URL queues for managing crawling queues. Uses ConcurrentHashMap for reduced lock contention. */
    protected final Map<String, Queue<UrlQueueImpl<Long>>> urlQueueMap = new ConcurrentHashMap<>();

    /** Map of session IDs to sets of URLs currently in queue for O(1) duplicate detection. */
    protected final Map<String, Set<String>> urlInQueueSetMap = new ConcurrentHashMap<>();

    /** Map of session IDs to access result maps for storing crawling results. Uses ConcurrentHashMap for reduced lock contention. */
    protected final Map<String, Map<String, AccessResultImpl<Long>>> sessionMap = new ConcurrentHashMap<>();

    /** Map of session IDs to include URL patterns for filtering URLs. */
    protected final Map<String, List<Pattern>> includeUrlPatternMap = new ConcurrentHashMap<>();

    /** Map of session IDs to exclude URL patterns for filtering URLs. */
    protected final Map<String, List<Pattern>> excludeUrlPatternMap = new ConcurrentHashMap<>();

    /**
     * Creates a new MemoryDataHelper instance.
     */
    public MemoryDataHelper() {
        super();
    }

    /**
     * Clears all URL queues and session data.
     */
    public void clear() {
        urlQueueMap.clear();
        urlInQueueSetMap.clear();
        sessionMap.clear();
    }

    /**
     * Returns the URL queue for the specified session ID.
     * Creates a new queue if one doesn't exist.
     * Uses ConcurrentHashMap.computeIfAbsent for thread-safe lazy initialization.
     * @param sessionId the session ID
     * @return the URL queue for the session
     */
    public Queue<UrlQueueImpl<Long>> getUrlQueueList(final String sessionId) {
        return urlQueueMap.computeIfAbsent(sessionId, k -> new LinkedList<>());
    }

    /**
     * Returns the set of URLs currently in the queue for the specified session ID.
     * This enables O(1) duplicate detection instead of O(n) queue iteration.
     * Creates a new set if one doesn't exist.
     * Uses ConcurrentHashMap.computeIfAbsent for thread-safe lazy initialization.
     * @param sessionId the session ID
     * @return the set of URLs in queue for the session
     */
    public Set<String> getUrlInQueueSet(final String sessionId) {
        return urlInQueueSetMap.computeIfAbsent(sessionId, k -> new HashSet<>());
    }

    /**
     * Adds the provided URL queue to the existing queue for the specified session.
     * @param sessionId the session ID
     * @param urlQueueList the URL queue to add
     */
    public void addUrlQueueList(final String sessionId, final Queue<UrlQueueImpl<Long>> urlQueueList) {
        final Queue<UrlQueueImpl<Long>> uqList = getUrlQueueList(sessionId);
        final Set<String> urlSet = getUrlInQueueSet(sessionId);
        synchronized (uqList) {
            for (final UrlQueueImpl<Long> urlQueue : urlQueueList) {
                uqList.add(urlQueue);
                urlSet.add(urlQueue.getUrl());
            }
        }
    }

    /**
     * Removes the URL queue for the specified session ID.
     * @param sessionId the session ID
     */
    public void removeUrlQueueList(final String sessionId) {
        urlQueueMap.remove(sessionId);
        urlInQueueSetMap.remove(sessionId);
    }

    /**
     * Clears all URL queues for all sessions.
     */
    public void clearUrlQueueList() {
        urlQueueMap.clear();
        urlInQueueSetMap.clear();
    }

    /**
     * Returns the access result map for the specified session ID.
     * Creates a new map if one doesn't exist.
     * Uses ConcurrentHashMap.computeIfAbsent for thread-safe lazy initialization.
     * @param sessionId the session ID
     * @return the access result map for the session
     */
    public Map<String, AccessResultImpl<Long>> getAccessResultMap(final String sessionId) {
        return sessionMap.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>());
    }

    /**
     * Deletes the access result map for the specified session ID.
     * @param sessionId the session ID
     */
    public void deleteAccessResultMap(final String sessionId) {
        sessionMap.remove(sessionId);
    }

    /**
     * Deletes all access result maps for all sessions.
     */
    public void deleteAllAccessResultMap() {
        sessionMap.clear();
    }

    /**
     * Returns a list of access results for the specified URL across all sessions.
     * @param url the URL to search for
     * @return the list of access results for the URL
     */
    public List<AccessResultImpl<Long>> getAccessResultList(final String url) {
        final List<AccessResultImpl<Long>> acList = new ArrayList<>();
        for (final Map.Entry<String, Map<String, AccessResultImpl<Long>>> entry : sessionMap.entrySet()) {
            if (entry.getValue() != null) {
                final AccessResultImpl<Long> ar = entry.getValue().get(url);
                if (ar != null) {
                    acList.add(ar);
                }
            }
        }
        // TODO order
        return acList;
    }

    /**
     * Adds an include URL pattern for the specified session.
     * @param sessionId the session ID
     * @param url the URL pattern to include
     */
    public void addIncludeUrlPattern(final String sessionId, final String url) {
        final List<Pattern> patternList = getIncludeUrlPatternList(sessionId);
        synchronized (patternList) {
            patternList.add(Pattern.compile(url));
        }
    }

    /**
     * Returns the list of include URL patterns for the specified session.
     * Creates a new list if one doesn't exist.
     * Uses ConcurrentHashMap.computeIfAbsent for thread-safe lazy initialization.
     * @param sessionId the session ID
     * @return the list of include URL patterns
     */
    public List<Pattern> getIncludeUrlPatternList(final String sessionId) {
        return includeUrlPatternMap.computeIfAbsent(sessionId, k -> new ArrayList<>());
    }

    /**
     * Adds an exclude URL pattern for the specified session.
     * @param sessionId the session ID
     * @param url the URL pattern to exclude
     */
    public void addExcludeUrlPattern(final String sessionId, final String url) {
        final List<Pattern> patternList = getExcludeUrlPatternList(sessionId);
        synchronized (patternList) {
            patternList.add(Pattern.compile(url));
        }
    }

    /**
     * Returns the list of exclude URL patterns for the specified session.
     * Creates a new list if one doesn't exist.
     * Uses ConcurrentHashMap.computeIfAbsent for thread-safe lazy initialization.
     * @param sessionId the session ID
     * @return the list of exclude URL patterns
     */
    public List<Pattern> getExcludeUrlPatternList(final String sessionId) {
        return excludeUrlPatternMap.computeIfAbsent(sessionId, k -> new ArrayList<>());
    }

    /**
     * Clears all URL patterns for the specified session.
     * @param sessionId the session ID
     */
    public void clearUrlPattern(final String sessionId) {
        includeUrlPatternMap.remove(sessionId);
        excludeUrlPatternMap.remove(sessionId);
    }

    /**
     * Clears all URL patterns.
     */
    public void clearUrlPattern() {
        includeUrlPatternMap.clear();
        excludeUrlPatternMap.clear();
    }
}
