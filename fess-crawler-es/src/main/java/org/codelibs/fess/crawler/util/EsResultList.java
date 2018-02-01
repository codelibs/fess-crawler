/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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

public class EsResultList<E> extends ArrayList<E> {

    private static final long serialVersionUID = 1L;

    private long totalHits;

    private long tookInMillis;

    public void setTotalHits(final long totalHits) {
        this.totalHits = totalHits;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public void setTookInMillis(final long tookInMillis) {
        this.tookInMillis = tookInMillis;
    }

    public long getTookInMillis() {
        return tookInMillis;
    }

}
