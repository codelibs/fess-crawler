/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.entity;

/**
 * The UrlQueue interface represents a queue of URLs to be processed by a web crawler.
 * It provides methods to get and set various properties of a URL queue entry.
 *
 * @param <IDTYPE> the type of the identifier for the URL queue entry
 */
public interface UrlQueue<IDTYPE> {

    /**
     * Retrieves the unique identifier of the URL queue.
     *
     * @return the unique identifier of type IDTYPE.
     */
    IDTYPE getId();

    /**
     * Sets the unique identifier for the URL queue.
     *
     * @param id the unique identifier to set
     */
    void setId(IDTYPE id);

    /**
     * Retrieves the session ID associated with this URL queue.
     *
     * @return the session ID as a String.
     */
    String getSessionId();

    /**
     * Sets the session ID for the URL queue.
     *
     * @param sessionId the session ID to set
     */
    void setSessionId(String sessionId);

    /**
     * Retrieves the HTTP method used for the URL in the queue.
     *
     * @return the HTTP method as a String.
     */
    String getMethod();

    /**
     * Sets the HTTP method for the URL queue.
     *
     * @param method the HTTP method to be set (e.g., GET, POST, etc.)
     */
    void setMethod(String method);

    /**
     * Retrieves the URL from the queue.
     *
     * @return the URL as a String.
     */
    String getUrl();

    /**
     * Sets the URL for this UrlQueue.
     *
     * @param url the URL to be set
     */
    void setUrl(String url);

    /**
     * Retrieves the metadata associated with the URL queue.
     *
     * @return a String representing the metadata.
     */
    String getMetaData();

    /**
     * Sets the metadata for the URL queue.
     *
     * @param metaData the metadata to set
     */
    void setMetaData(String metaData);

    /**
     * Retrieves the encoding of the URL queue.
     *
     * @return the encoding as a String.
     */
    String getEncoding();

    /**
     * Sets the encoding for the URL queue.
     *
     * @param encoding the encoding to be set
     */
    void setEncoding(String encoding);

    /**
     * Retrieves the parent URL of the current URL in the queue.
     *
     * @return the parent URL as a String.
     */
    String getParentUrl();

    /**
     * Sets the parent URL for this URL queue entry.
     *
     * @param parentUrl the parent URL to be set
     */
    void setParentUrl(String parentUrl);

    /**
     * Retrieves the depth of the URL in the queue.
     *
     * @return the depth of the URL as an Integer.
     */
    Integer getDepth();

    /**
     * Sets the depth of the URL in the queue.
     *
     * @param depth the depth to set, represented as an Integer.
     */
    void setDepth(Integer depth);

    /**
     * Returns the last modified timestamp of the URL in the queue.
     *
     * @return the last modified timestamp as a Long value.
     */
    Long getLastModified();

    /**
     * Sets the last modified timestamp for the URL queue entry.
     *
     * @param lastModified the timestamp of the last modification in milliseconds since epoch.
     */
    void setLastModified(Long lastModified);

    /**
     * Returns the creation time of the URL queue entry.
     *
     * @return the creation time as a Long value.
     */
    Long getCreateTime();

    /**
     * Sets the creation time of the URL queue.
     *
     * @param createTime the creation time to set, represented as a Long value.
     */
    void setCreateTime(Long createTime);

    /**
     * Retrieves the weight of the URL queue.
     *
     * @return the weight as a float value.
     */
    float getWeight();

    /**
     * Sets the weight of the URL queue.
     *
     * @param weight the weight to set
     */
    void setWeight(float weight);
}
