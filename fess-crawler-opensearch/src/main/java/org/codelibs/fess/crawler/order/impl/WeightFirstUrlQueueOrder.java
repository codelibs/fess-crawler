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
 * Fetches queued URLs by descending weight only, leaving ties in the index order.
 *
 * <p>
 * This differs from the default {@link SequentialUrlQueueOrder} only in what happens between
 * entries of equal weight: that order falls back to discovery order, this one lets the search
 * engine return them however it likes. Choose it when a queue carries a large backlog scored
 * by a {@code UrlQueueWeigher} and only the score should decide what is crawled next, with no
 * bias towards whatever was discovered first.
 * </p>
 *
 * <p>
 * Without a weigher every entry is at the default weight, every entry ties, and the fetch
 * order is whatever the index hands back - so this order only means something once weights
 * differ.
 * </p>
 */
public class WeightFirstUrlQueueOrder implements UrlQueueOrder {

    /**
     * Creates a new WeightFirstUrlQueueOrder instance.
     */
    public WeightFirstUrlQueueOrder() {
        // NOP
    }

    @Override
    public SortBuilder<?>[] buildSorts(final String sessionId) {
        return new SortBuilder<?>[] { SortBuilders.fieldSort(OpenSearchUrlQueue.WEIGHT).order(SortOrder.DESC) };
    }
}
