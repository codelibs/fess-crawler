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

public class EsCrawlerConfig {
    protected String queueIndex = ".crawler.queue";

    protected String dataIndex = ".crawler.data";

    protected String filterIndex = ".crawler.filter";

    protected int queueShards = Runtime.getRuntime().availableProcessors() * 2;

    protected int dataShards = Runtime.getRuntime().availableProcessors() * 2;

    protected int filterShards = Runtime.getRuntime().availableProcessors() * 2;

    protected int queueReplicas = 1;

    protected int dataReplicas = 1;

    protected int filterReplicas = 1;

    public String getQueueIndex() {
        return queueIndex;
    }

    public void setQueueIndex(String queueIndex) {
        this.queueIndex = queueIndex;
    }

    public String getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(String dataIndex) {
        this.dataIndex = dataIndex;
    }

    public String getFilterIndex() {
        return filterIndex;
    }

    public void setFilterIndex(String filterIndex) {
        this.filterIndex = filterIndex;
    }

    public int getQueueShards() {
        return queueShards;
    }

    public void setQueueShards(int queueShards) {
        this.queueShards = queueShards;
    }

    public int getDataShards() {
        return dataShards;
    }

    public void setDataShards(int dataShards) {
        this.dataShards = dataShards;
    }

    public int getFilterShards() {
        return filterShards;
    }

    public void setFilterShards(int filterShards) {
        this.filterShards = filterShards;
    }

    public int getQueueReplicas() {
        return queueReplicas;
    }

    public void setQueueReplicas(int queueReplicas) {
        this.queueReplicas = queueReplicas;
    }

    public int getDataReplicas() {
        return dataReplicas;
    }

    public void setDataReplicas(int dataReplicas) {
        this.dataReplicas = dataReplicas;
    }

    public int getFilterReplicas() {
        return filterReplicas;
    }

    public void setFilterReplicas(int filterReplicas) {
        this.filterReplicas = filterReplicas;
    }

}
