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
import org.codelibs.fesen.opensearch.search.sort.SortBuilder;
import org.codelibs.fesen.opensearch.search.sort.SortBuilders;
import org.codelibs.fesen.opensearch.search.sort.SortOrder;

/**
 * Fetches queued URLs by descending weight, then by discovery order. This is the default.
 *
 * <p>
 * Weight is the primary key, so a {@code UrlQueueWeigher} takes effect under this order
 * without any {@code crawl.order} setting; discovery order only decides between entries of
 * equal weight. Weights are uniform out of the box, which leaves discovery order as the
 * effective sort. Use {@link WeightFirstUrlQueueOrder} instead when entries of equal weight
 * should not be held to discovery order.
 * </p>
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
