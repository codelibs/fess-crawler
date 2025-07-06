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
package org.codelibs.fess.crawler.util;

import java.util.ArrayList;

/**
 * OpenSearchResultList is a list of OpenSearch results.
 *
 * @param <E> The type of elements in this list.
 */
public class OpenSearchResultList<E> extends ArrayList<E> {
    /**
     * Constructs a new OpenSearchResultList.
     */
    public OpenSearchResultList() {
        super();
    }

    private static final long serialVersionUID = 1L;

    /**
     * Total number of hits.
     */
    private long totalHits;

    /**
     * Time taken for the search in milliseconds.
     */
    private long tookInMillis;

    /**
     * Sets the total number of hits.
     * @param totalHits The total number of hits.
     */
    public void setTotalHits(final long totalHits) {
        this.totalHits = totalHits;
    }

    /**
     * Returns the total number of hits.
     * @return The total number of hits.
     */
    public long getTotalHits() {
        return totalHits;
    }

    /**
     * Sets the time taken for the search in milliseconds.
     * @param tookInMillis The time taken in milliseconds.
     */
    public void setTookInMillis(final long tookInMillis) {
        this.tookInMillis = tookInMillis;
    }

    /**
     * Returns the time taken for the search in milliseconds.
     * @return The time taken in milliseconds.
     */
    public long getTookInMillis() {
        return tookInMillis;
    }

}
