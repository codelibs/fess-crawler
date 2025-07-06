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

import org.codelibs.fess.crawler.entity.UrlQueue;

/**
 * Service interface for managing URL queues.
 * Provides methods for adding, retrieving, and managing URLs within a crawling session.
 *
 * @param <QUEUE> the type of URL queue
 */
public interface UrlQueueService<QUEUE extends UrlQueue<?>> {

    /**
     * Updates the session ID.
     *
     * @param oldSessionId The old session ID.
     * @param newSessionId The new session ID.
     */
    void updateSessionId(String oldSessionId, String newSessionId);

    /**
     * Adds a URL to the queue.
     *
     * @param sessionId The session ID.
     * @param url The URL.
     */
    void add(String sessionId, String url);

    /**
     * Inserts a URL queue.
     *
     * @param urlQueue The URL queue.
     */
    void insert(QUEUE urlQueue);

    /**
     * Deletes a URL queue.
     *
     * @param sessionId The session ID.
     */
    void delete(String sessionId);

    /**
     * Deletes all URL queues.
     */
    void deleteAll();

    /**
     * Offers all URL queues.
     *
     * @param sessionId The session ID.
     * @param newUrlQueueList The list of new URL queues.
     */
    void offerAll(String sessionId, List<QUEUE> newUrlQueueList);

    /**
     * Polls a URL queue.
     *
     * @param sessionId The session ID.
     * @return The URL queue.
     */
    QUEUE poll(String sessionId);

    /**
     * Saves the session.
     *
     * @param sessionId The session ID.
     */
    void saveSession(String sessionId);

    /**
     * Checks if a URL has been visited.
     *
     * @param urlQueue The URL queue.
     * @return true if the URL has been visited, otherwise false.
     */
    boolean visited(QUEUE urlQueue);

    /**
     * Generates URL queues.
     *
     * @param previousSessionId The previous session ID.
     * @param sessionId The session ID.
     */
    void generateUrlQueues(String previousSessionId, String sessionId);
}
