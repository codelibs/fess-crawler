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
package org.codelibs.fess.crawler.order.impl;

import org.codelibs.fess.crawler.entity.OpenSearchUrlQueue;
import org.codelibs.fess.crawler.order.UrlQueueOrder;
import org.opensearch.search.sort.SortBuilder;
import org.opensearch.search.sort.SortBuilders;
import org.opensearch.search.sort.SortOrder;

/**
 * Fetches queued URLs by descending weight, then by discovery order. This is the default.
 */
public class SequentialUrlQueueOrder implements UrlQueueOrder {

    /**
     * Creates a new SequentialUrlQueueOrder instance.
     */
    public SequentialUrlQueueOrder() {
        // NOP
    }

    @Override
    public SortBuilder<?>[] buildSorts(final String sessionId) {
        return new SortBuilder<?>[] { SortBuilders.fieldSort(OpenSearchUrlQueue.WEIGHT).order(SortOrder.DESC),
                SortBuilders.fieldSort(OpenSearchUrlQueue.CREATE_TIME).order(SortOrder.ASC) };
    }
}
