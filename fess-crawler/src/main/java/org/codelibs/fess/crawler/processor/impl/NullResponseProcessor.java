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
package org.codelibs.fess.crawler.processor.impl;

import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.processor.ResponseProcessor;

/**
 * NullResponseProcessor is a class that implements the ResponseProcessor interface.
 * It provides a no-op implementation for processing response data.
 * This class can be used when you want to skip the response processing step in a crawler.
 *
 * @author kuma
 */
public class NullResponseProcessor implements ResponseProcessor {

    /**
     * Processes the given response data.
     * This implementation does nothing.
     *
     * @param responseData the response data to process
     */
    @Override
    public void process(final ResponseData responseData) {
        // do nothing
    }

}
