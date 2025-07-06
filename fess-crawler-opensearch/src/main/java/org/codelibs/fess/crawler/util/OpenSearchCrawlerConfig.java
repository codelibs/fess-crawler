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

/**
 * Configuration class for OpenSearch crawler settings.
 * This class provides configuration for index names, shards, and replicas
 * for the queue, data, and filter indices used by the crawler.
 */
public class OpenSearchCrawlerConfig {
    /**
     * Constructs a new OpenSearchCrawlerConfig.
     */
    public OpenSearchCrawlerConfig() {
        // Default constructor
    }

    /**
     * Queue index name.
     */
    protected String queueIndex = ".crawler.queue";

    /**
     * Data index name.
     */
    protected String dataIndex = ".crawler.data";

    /**
     * Filter index name.
     */
    protected String filterIndex = ".crawler.filter";

    /**
     * Number of shards for the queue index.
     */
    protected int queueShards = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * Number of shards for the data index.
     */
    protected int dataShards = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * Number of shards for the filter index.
     */
    protected int filterShards = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * Number of replicas for the queue index.
     */
    protected int queueReplicas = 1;

    /**
     * Number of replicas for the data index.
     */
    protected int dataReplicas = 1;

    /**
     * Number of replicas for the filter index.
     */
    protected int filterReplicas = 1;

    /**
     * Returns the queue index name.
     * @return The queue index name.
     */
    public String getQueueIndex() {
        return queueIndex;
    }

    /**
     * Sets the queue index name.
     * @param queueIndex The queue index name.
     */
    public void setQueueIndex(final String queueIndex) {
        this.queueIndex = queueIndex;
    }

    /**
     * Returns the data index name.
     * @return The data index name.
     */
    public String getDataIndex() {
        return dataIndex;
    }

    /**
     * Sets the data index name.
     * @param dataIndex The data index name.
     */
    public void setDataIndex(final String dataIndex) {
        this.dataIndex = dataIndex;
    }

    /**
     * Returns the filter index name.
     * @return The filter index name.
     */
    public String getFilterIndex() {
        return filterIndex;
    }

    /**
     * Sets the filter index name.
     * @param filterIndex The filter index name.
     */
    public void setFilterIndex(final String filterIndex) {
        this.filterIndex = filterIndex;
    }

    /**
     * Returns the number of queue shards.
     * @return The number of queue shards.
     */
    public int getQueueShards() {
        return queueShards;
    }

    /**
     * Sets the number of queue shards.
     * @param queueShards The number of queue shards.
     */
    public void setQueueShards(final int queueShards) {
        this.queueShards = queueShards;
    }

    /**
     * Returns the number of data shards.
     * @return The number of data shards.
     */
    public int getDataShards() {
        return dataShards;
    }

    /**
     * Sets the number of data shards.
     * @param dataShards The number of data shards.
     */
    public void setDataShards(final int dataShards) {
        this.dataShards = dataShards;
    }

    /**
     * Returns the number of filter shards.
     * @return The number of filter shards.
     */
    public int getFilterShards() {
        return filterShards;
    }

    /**
     * Sets the number of filter shards.
     * @param filterShards The number of filter shards.
     */
    public void setFilterShards(final int filterShards) {
        this.filterShards = filterShards;
    }

    /**
     * Returns the number of queue replicas.
     * @return The number of queue replicas.
     */
    public int getQueueReplicas() {
        return queueReplicas;
    }

    /**
     * Sets the number of queue replicas.
     * @param queueReplicas The number of queue replicas.
     */
    public void setQueueReplicas(final int queueReplicas) {
        this.queueReplicas = queueReplicas;
    }

    /**
     * Returns the number of data replicas.
     * @return The number of data replicas.
     */
    public int getDataReplicas() {
        return dataReplicas;
    }

    /**
     * Sets the number of data replicas.
     * @param dataReplicas The number of data replicas.
     */
    public void setDataReplicas(final int dataReplicas) {
        this.dataReplicas = dataReplicas;
    }

    /**
     * Returns the number of filter replicas.
     * @return The number of filter replicas.
     */
    public int getFilterReplicas() {
        return filterReplicas;
    }

    /**
     * Sets the number of filter replicas.
     * @param filterReplicas The number of filter replicas.
     */
    public void setFilterReplicas(final int filterReplicas) {
        this.filterReplicas = filterReplicas;
    }

}
