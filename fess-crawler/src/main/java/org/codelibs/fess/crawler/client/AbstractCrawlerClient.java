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
package org.codelibs.fess.crawler.client;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public abstract class AbstractCrawlerClient implements CrawlerClient {

    private static final Logger logger = LoggerFactory
            .getLogger(AbstractCrawlerClient.class);

    protected static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    public static final String ACCESS_TIMEOUT_PROPERTY = "accessTimeout";

    public static final String MAX_CONTENT_LENGTH = "maxContentLength";

    public static final String MAX_CACHED_CONTENT_SIZE = "maxCachedContentSize";

    @Resource
    protected CrawlerContainer crawlerContainer;

    private Map<String, Object> initParamMap;

    protected long maxCachedContentSize = 1024L * 1024L; //1mb

    protected Integer accessTimeout = null; // sec

    protected Long maxContentLength = null;

    public void init() {
        // max content length
        final Long maxContentLengthParam = getInitParameter(MAX_CONTENT_LENGTH, maxContentLength, Long.class);
        if (maxContentLengthParam != null) {
            maxContentLength = maxContentLengthParam.longValue();
        }

        // access timeout
        final Integer accessTimeoutParam = getInitParameter(ACCESS_TIMEOUT_PROPERTY, accessTimeout, Integer.class);
        if (accessTimeoutParam != null) {
            accessTimeout = accessTimeoutParam.intValue();
        }

        // max cached content size
        final Long maxCachedContentSizeParam = getInitParameter(MAX_CACHED_CONTENT_SIZE, null, Long.class);
        if (maxCachedContentSizeParam != null) {
            maxCachedContentSize = maxCachedContentSizeParam.longValue();
        }
    }

    protected <T> T getInitParameter(final String key, final T defaultValue, final Class<T> clazz) {
        if (initParamMap != null) {
            try {
                final Object paramValue = initParamMap.get(key);
                if (paramValue == null) {
                    return defaultValue;
                }

                return convertObj(paramValue, clazz);
            } catch (final Exception e) {
                logger.warn("Could not load init parameters: " + key + " from " + initParamMap, e);
            }
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    protected <T> T convertObj(final Object value, final Class<T> clazz) {
        if (clazz.isAssignableFrom(String.class)) {
            return (T) value.toString();
        } else if (clazz.isAssignableFrom(Long.class)) {
            if (value instanceof Long) {
                return (T) value;
            } else {
                return (T) Long.valueOf(value.toString());
            }
        } else if (clazz.isAssignableFrom(Integer.class)) {
            if (value instanceof Integer) {
                return (T) value;
            } else {
                return (T) Integer.valueOf(value.toString());
            }
        } else if (clazz.isAssignableFrom(Double.class)) {
            if (value instanceof Double) {
                return (T) value;
            } else {
                return (T) Double.valueOf(value.toString());
            }
        } else if (clazz.isAssignableFrom(Float.class)) {
            if (value instanceof Float) {
                return (T) value;
            } else {
                return (T) Float.valueOf(value.toString());
            }
        } else if (clazz.isAssignableFrom(Boolean.class)) {
            if (value instanceof Boolean) {
                return (T) value;
            } else {
                return (T) Boolean.valueOf(value.toString());
            }
        }
        return (T) value;
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

    protected void checkMaxContentLength(final ResponseData responseData) {
        if (maxContentLength != null && responseData.getContentLength() > maxContentLength.longValue()) {
            throw new MaxLengthExceededException("The content length (" + responseData.getContentLength() + " byte) is over "
                    + maxContentLength.longValue() + " byte. The url is " + responseData.getUrl());
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

    public void setMaxCachedContentSize(final long maxCachedContentSize) {
        this.maxCachedContentSize = maxCachedContentSize;
    }

    public void setAccessTimeout(final Integer accessTimeout) {
        this.accessTimeout = accessTimeout;
    }

    public void setMaxContentLength(final Long maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    public void register(final String regex) {
        CrawlerClientFactory clientFactory = crawlerContainer.getComponent("clientFactory");
        clientFactory.addClient(regex, this);
    }

    public void register(final List<String> regexList) {
        CrawlerClientFactory clientFactory = crawlerContainer.getComponent("clientFactory");
        clientFactory.addClient(regexList, this);
    }

    public void register(final String regex, final int pos) {
        CrawlerClientFactory clientFactory = crawlerContainer.getComponent("clientFactory");
        clientFactory.addClient(regex, this, pos);
    }
}
