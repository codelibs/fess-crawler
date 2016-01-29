/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public abstract class AbstractCrawlerClient implements CrawlerClient {

    private static final Logger logger = LoggerFactory
            .getLogger(AbstractCrawlerClient.class);

    private Map<String, Object> initParamMap;

    protected <T> T getInitParameter(final String key, final T defaultValue) {
        if (initParamMap != null) {
            try {
                @SuppressWarnings("unchecked")
                final T value = (T) initParamMap.get(key);
                if (value != null) {
                    return value;
                }
            } catch (final Exception e) {
                logger.warn("Could not load init parameters: " + key + " from "
                        + initParamMap, e);
            }
        }
        return defaultValue;
    }

    @Override
    public void setInitParameterMap(final Map<String, Object> params) {
        initParamMap = params;
    }

    @Override
    public ResponseData execute(final RequestData request) {
        switch (request.getMethod()) {
            case GET:
                return doGet(request.getUrl());
            case HEAD:
                return doHead(request.getUrl());
            case POST:
                return doPost(request.getUrl());
            default:
                throw new CrawlerSystemException(request.getMethod()
                        + " method is not supported.");
        }
    }

    protected ResponseData doGet(final String url) {
        throw new CrawlerSystemException("GET method is not supported.");
    }

    protected ResponseData doHead(final String url) {
        throw new CrawlerSystemException("HEAD method is not supported.");
    }

    protected ResponseData doPost(final String url) {
        throw new CrawlerSystemException("POST method is not supported.");
    }

}
