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
package org.codelibs.fess.crawler.weight;

import java.util.List;

import org.codelibs.fess.crawler.entity.UrlQueue;

/**
 * Assigns the weight of child URLs just before they are added to the queue.
 *
 * <p>
 * The weight decides the fetch order under the weight-based {@code UrlQueueOrder}
 * implementations. Callers do not catch exceptions, so an implementation must not throw.
 * </p>
 */
public interface UrlQueueWeigher {

    /**
     * Sets the weight of the child URLs that are about to be queued.
     *
     * @param sessionId the crawling session ID
     * @param childList the child URLs extracted from a single parent page
     */
    void apply(String sessionId, List<UrlQueue<?>> childList);
}
