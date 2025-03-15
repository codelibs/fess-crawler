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
package org.codelibs.fess.crawler.client;

import java.util.Map;

import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;

/**
 * Interface representing a client for a web crawler.
 * This client is responsible for executing requests and handling responses.
 * It extends {@link AutoCloseable} to allow for resource management.
 */
public interface CrawlerClient extends AutoCloseable {

    /**
     * Sets the initialization parameters for the crawler client.
     *
     * @param params a map containing the initialization parameters
     */
    void setInitParameterMap(Map<String, Object> params);

    /**
     * Executes a request and returns the response data.
     *
     * @param data the request data to be executed
     * @return the response data from the executed request
     */
    ResponseData execute(RequestData data);

    /**
     * Closes the crawler client and releases any resources associated with it.
     * This default implementation does nothing.
     *
     * @throws Exception if an error occurs during closing
     */
    @Override
    default void close() throws Exception {
        // nothing
    }
}
