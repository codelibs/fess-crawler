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
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.opensearch.search.sort.FieldSortBuilder;
import org.opensearch.search.sort.ScoreSortBuilder;
import org.opensearch.search.sort.SortBuilder;
import org.opensearch.search.sort.SortOrder;

public class UrlQueueOrderTest extends PlainTestCase {

    private void assertFieldSort(final SortBuilder<?> sort, final String fieldName, final SortOrder order) {
        assertTrue(sort instanceof FieldSortBuilder);
        assertEquals(fieldName, ((FieldSortBuilder) sort).getFieldName());
        assertEquals(order, sort.order());
    }

    @Test
    public void test_sequential() {
        final UrlQueueOrder order = new SequentialUrlQueueOrder();
        assertNull(order.buildQuery("s1"));
        final SortBuilder<?>[] sorts = order.buildSorts("s1");
        assertEquals(2, sorts.length);
        assertFieldSort(sorts[0], "weight", SortOrder.DESC);
        assertFieldSort(sorts[1], "createTime", SortOrder.ASC);
    }

    @Test
    public void test_depthFirst() {
        final UrlQueueOrder order = new DepthFirstUrlQueueOrder();
        assertNull(order.buildQuery("s1"));
        final SortBuilder<?>[] sorts = order.buildSorts("s1");
        assertEquals(2, sorts.length);
        assertFieldSort(sorts[0], "depth", SortOrder.DESC);
        assertFieldSort(sorts[1], "createTime", SortOrder.DESC);
    }

    @Test
    public void test_newestFirst() {
        final UrlQueueOrder order = new NewestFirstUrlQueueOrder();
        assertNull(order.buildQuery("s1"));
        final SortBuilder<?>[] sorts = order.buildSorts("s1");
        assertEquals(1, sorts.length);
        assertFieldSort(sorts[0], "createTime", SortOrder.DESC);
    }

    @Test
    public void test_weightFirst() {
        final UrlQueueOrder order = new WeightFirstUrlQueueOrder();
        assertNull(order.buildQuery("s1"));
        final SortBuilder<?>[] sorts = order.buildSorts("s1");
        assertEquals(1, sorts.length);
        assertFieldSort(sorts[0], "weight", SortOrder.DESC);
    }

    @Test
    public void test_random_buildsFunctionScoreQuery() {
        final UrlQueueOrder order = new RandomUrlQueueOrder();
        assertTrue(order.buildQuery("s1") instanceof FunctionScoreQueryBuilder);
        final SortBuilder<?>[] sorts = order.buildSorts("s1");
        assertEquals(1, sorts.length);
        assertTrue(sorts[0] instanceof ScoreSortBuilder);
        assertEquals(SortOrder.DESC, sorts[0].order());
    }

    @Test
    public void test_random_seedIsStablePerSession() {
        final UrlQueueOrder order = new RandomUrlQueueOrder();
        assertEquals(order.buildQuery("s1").toString(), order.buildQuery("s1").toString());
        assertFalse(order.buildQuery("s1").toString().equals(order.buildQuery("s2").toString()));
    }
}
