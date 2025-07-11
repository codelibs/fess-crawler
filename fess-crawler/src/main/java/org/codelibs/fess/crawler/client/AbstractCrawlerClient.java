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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;

import jakarta.annotation.Resource;

/**
 * Abstract base class for CrawlerClient implementations.
 * Provides common functionality for handling initialization parameters,
 * content length checks, and default method implementations.
 * It defines the basic structure and configuration options for crawler clients.
 */
public abstract class AbstractCrawlerClient implements CrawlerClient {

    private static final Logger logger = LogManager.getLogger(AbstractCrawlerClient.class);

    /** The MIME type for application/octet-stream. */
    protected static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    /** The property name for access timeout. */
    public static final String ACCESS_TIMEOUT_PROPERTY = "accessTimeout";

    /** The property name for maximum content length. */
    public static final String MAX_CONTENT_LENGTH = "maxContentLength";

    /** The property name for maximum cached content size. */
    public static final String MAX_CACHED_CONTENT_SIZE = "maxCachedContentSize";

    /** The crawler container. */
    @Resource
    protected CrawlerContainer crawlerContainer;

    /** The initialization parameter map. */
    protected Map<String, Object> initParamMap;

    /** The maximum cached content size in bytes. Default is 1MB. */
    protected long maxCachedContentSize = 1024L * 1024L; // 1MB

    /** The access timeout in seconds. Default is null (no timeout). */
    protected Integer accessTimeout = null; // seconds

    /** The maximum content length in bytes. Default is null (no limit). */
    protected Long maxContentLength = null;

    /**
     * Constructs a new AbstractCrawlerClient.
     */
    public AbstractCrawlerClient() {
        // NOP
    }

    /**
     * Initializes the client with parameters from initParamMap.
     * Sets maxContentLength, accessTimeout, and maxCachedContentSize.
     */
    public void init() {
        // max content length
        final Long maxContentLengthParam = getInitParameter(MAX_CONTENT_LENGTH, maxContentLength, Long.class);
        if (maxContentLengthParam != null) {
            maxContentLength = maxContentLengthParam;
        }

        // access timeout
        final Integer accessTimeoutParam = getInitParameter(ACCESS_TIMEOUT_PROPERTY, accessTimeout, Integer.class);
        if (accessTimeoutParam != null) {
            accessTimeout = accessTimeoutParam;
        }

        // max cached content size
        final Long maxCachedContentSizeParam = getInitParameter(MAX_CACHED_CONTENT_SIZE, null, Long.class);
        if (maxCachedContentSizeParam != null) {
            maxCachedContentSize = maxCachedContentSizeParam;
        }
    }

    /**
     * Retrieves an initialization parameter, converting it to the specified class type.
     * @param <T> The type of the parameter.
     * @param key The key of the parameter.
     * @param defaultValue The default value if the parameter is not found.
     * @param clazz The class type to convert the parameter to.
     * @return The parameter value, or the default value if not found.
     */
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

    /**
     * Converts an object to the specified class type.
     * @param <T> The target type.
     * @param value The object to convert.
     * @param clazz The class type to convert to.
     * @return The converted object.
     */
    @SuppressWarnings("unchecked")
    protected <T> T convertObj(final Object value, final Class<T> clazz) {
        if (clazz.isAssignableFrom(String.class)) {
            return (T) value.toString();
        }
        if (clazz.isAssignableFrom(Long.class)) {
            if (!(value instanceof Long)) {
                return (T) Long.valueOf(value.toString());
            }
        } else if (clazz.isAssignableFrom(Integer.class)) {
            if (!(value instanceof Integer)) {
                return (T) Integer.valueOf(value.toString());
            }
        } else if (clazz.isAssignableFrom(Double.class)) {
            if (!(value instanceof Double)) {
                return (T) Double.valueOf(value.toString());
            }
        } else if (clazz.isAssignableFrom(Float.class)) {
            if (!(value instanceof Float)) {
                return (T) Float.valueOf(value.toString());
            }
        } else if (clazz.isAssignableFrom(Boolean.class) && !(value instanceof Boolean)) {
            return (T) Boolean.valueOf(value.toString());
        }
        return (T) value;
    }

    /**
     * Sets the initialization parameter map.
     * @param params The map of parameters.
     */
    @Override
    public void setInitParameterMap(final Map<String, Object> params) {
        initParamMap = params;
    }

    /**
     * Executes the request based on the HTTP method.
     * @param request The request data.
     * @return The response data.
     */
    @Override
    public ResponseData execute(final RequestData request) {
        return switch (request.getMethod()) {
        case GET -> doGet(request.getUrl());
        case HEAD -> doHead(request.getUrl());
        case POST -> doPost(request.getUrl());
        default -> throw new CrawlerSystemException(request.getMethod() + " method is not supported.");
        };
    }

    /**
     * Checks if the content length exceeds the maximum allowed length.
     * @param responseData The response data.
     */
    protected void checkMaxContentLength(final ResponseData responseData) {
        if (maxContentLength != null && responseData.getContentLength() > maxContentLength.longValue()) {
            throw new MaxLengthExceededException("The content length (" + responseData.getContentLength() + " byte) is over "
                    + maxContentLength.longValue() + " byte. The url is " + responseData.getUrl());
        }
    }

    /**
     * Performs a GET request.
     * @param url The URL to request.
     * @return The ResponseData.
     */
    protected ResponseData doGet(final String url) {
        throw new CrawlerSystemException("GET method is not supported.");
    }

    /**
     * Performs a HEAD request.
     * @param url The URL to request.
     * @return The ResponseData.
     */
    protected ResponseData doHead(final String url) {
        throw new CrawlerSystemException("HEAD method is not supported.");
    }

    /**
     * Performs a POST request.
     * @param url The URL to request.
     * @return The ResponseData.
     */
    protected ResponseData doPost(final String url) {
        throw new CrawlerSystemException("POST method is not supported.");
    }

    /**
     * Creates a temporary file.
     * @param prefix The prefix string to be used in generating the file's name.
     * @param suffix The suffix string to be used in generating the file's name.
     * @param directory The directory in which the file is to be created, or null if the default temporary-file directory is to be used.
     * @return The created temporary file.
     */
    protected File createTempFile(final String prefix, final String suffix, final File directory) {
        try {
            final File tempFile = File.createTempFile(prefix, suffix, directory);
            tempFile.setReadable(false, false);
            tempFile.setReadable(true, true);
            tempFile.setWritable(false, false);
            tempFile.setWritable(true, true);
            return tempFile;
        } catch (final IOException e) {
            throw new CrawlerSystemException("Could not create a temp file.", e);
        }
    }

    /**
     * Sets the maximum cached content size.
     * @param maxCachedContentSize The maximum cached content size in bytes.
     */
    public void setMaxCachedContentSize(final long maxCachedContentSize) {
        this.maxCachedContentSize = maxCachedContentSize;
    }

    /**
     * Sets the access timeout.
     * @param accessTimeout The access timeout in seconds.
     */
    public void setAccessTimeout(final Integer accessTimeout) {
        this.accessTimeout = accessTimeout;
    }

    /**
     * Sets the maximum content length.
     * @param maxContentLength The maximum content length in bytes.
     */
    public void setMaxContentLength(final Long maxContentLength) {
        this.maxContentLength = maxContentLength;
    }
}
