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
package org.codelibs.fess.crawler.processor;

import org.codelibs.fess.crawler.entity.ResponseData;

/**
 * The ResponseProcessor interface defines a contract for processing response data.
 * Implementations of this interface are responsible for handling the response data
 * obtained during a crawling process.
 */
public interface ResponseProcessor {

    /**
     * Processes the given response data.
     *
     * @param responseData the response data to be processed
     */
    void process(ResponseData responseData);

}
