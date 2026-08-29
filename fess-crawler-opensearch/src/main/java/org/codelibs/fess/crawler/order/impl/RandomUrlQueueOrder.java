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

import org.codelibs.fess.crawler.order.UrlQueueOrder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.opensearch.index.query.functionscore.RandomScoreFunctionBuilder;
import org.opensearch.search.sort.SortBuilder;
import org.opensearch.search.sort.SortBuilders;
import org.opensearch.search.sort.SortOrder;

/**
 * Fetches queued URLs in a random order, seeded per session.
 */
public class RandomUrlQueueOrder implements UrlQueueOrder {

    /**
     * Creates a new RandomUrlQueueOrder instance.
     */
    public RandomUrlQueueOrder() {
        // NOP
    }

    @Override
    public QueryBuilder buildQuery(final String sessionId) {
        return QueryBuilders.functionScoreQuery(QueryBuilders.matchAllQuery(), new FunctionScoreQueryBuilder.FilterFunctionBuilder[] {
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(new RandomScoreFunctionBuilder().seed(sessionId.hashCode())) });
    }

    @Override
    public SortBuilder<?>[] buildSorts(final String sessionId) {
        return new SortBuilder<?>[] { SortBuilders.scoreSort().order(SortOrder.DESC) };
    }
}
