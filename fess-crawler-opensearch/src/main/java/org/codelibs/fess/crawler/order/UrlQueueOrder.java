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
package org.codelibs.fess.crawler.order;

import org.opensearch.index.query.QueryBuilder;
import org.opensearch.search.sort.SortBuilder;

/**
 * Decides which queued URLs are fetched next, and in what order.
 *
 * <p>
 * The session filter and the fetch size are applied by the queue service, so an
 * implementation only describes the additional narrowing and the sort order.
 * </p>
 */
public interface UrlQueueOrder {

    /**
     * Returns an additional query that narrows the candidates.
     *
     * @param sessionId the crawling session ID
     * @return the additional query, or {@literal null} to match all queued URLs
     */
    default QueryBuilder buildQuery(final String sessionId) {
        return null;
    }

    /**
     * Returns the sort conditions that decide the fetch order.
     *
     * @param sessionId the crawling session ID
     * @return the sort conditions, or an empty array to use the default order
     */
    SortBuilder<?>[] buildSorts(String sessionId);
}
