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
package org.codelibs.fess.crawler.processor.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.SystemUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.codelibs.fess.crawler.transformer.Transformer;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;

import jakarta.annotation.Resource;

/**
 * <p>
 * {@link DefaultResponseProcessor} is a default implementation of {@link ResponseProcessor}.
 * It processes the response data based on the HTTP status code and configured transformer.
 * </p>
 *
 * <p>
 * It handles successful responses by transforming the data using a {@link Transformer}
 * and storing the result. It also handles "Not Modified" responses by creating an empty
 * result and storing it. Unsuccessful responses are logged for debugging purposes.
 * </p>
 *
 * <p>
 * The class uses {@link CrawlerContainer} to access components like {@link AccessResult}
 * and {@link UrlQueue}. It also uses {@link CrawlingParameterUtil} to access services
 * like {@link UrlQueueService} and DataService, as well as the {@link CrawlerContext}.
 * </p>
 *
 * <p>
 * The class provides methods to check if a response is successful or not modified based on
 * configured HTTP status codes. It also handles the storage of child URLs found in the
 * response data, respecting the maximum depth and access count limits.
 * </p>
 *
 */
public class DefaultResponseProcessor implements ResponseProcessor {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(DefaultResponseProcessor.class);

    /** Container for managing crawler components */
    @Resource
    protected CrawlerContainer crawlerContainer;

    /** Transformer used to transform response data */
    protected Transformer transformer;

    /** HTTP status codes considered successful */
    protected int[] successfulHttpCodes;

    /** HTTP status codes considered not modified */
    protected int[] notModifiedHttpCodes;

    /**
     * Constructs a new DefaultResponseProcessor.
     */
    public DefaultResponseProcessor() {
        // Default constructor
    }

    /**
     * Processes response data based on HTTP status code and configured transformer.
     *
     * @param responseData the response data to process
     */
    @Override
    public void process(final ResponseData responseData) {
        if (isNotModified(responseData)) {
            final UrlQueue<?> urlQueue = CrawlingParameterUtil.getUrlQueue();
            final ResultData resultData = new ResultData();
            resultData.setData(new byte[0]);
            resultData.setEncoding(Constants.UTF_8);
            resultData.setTransformerName(Constants.NO_TRANSFORMER);
            processResult(urlQueue, responseData, resultData);
        } else if (isSuccessful(responseData)) {
            if (transformer == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("No Transformer for ({}). PLEASE CHECK YOUR CONFIGURATION.", responseData.getUrl());
                }
            } else {
                final ResultData resultData = transformer.transform(responseData);
                if (resultData == null) {
                    logger.warn("No data for ({}, {})", responseData.getUrl(), responseData.getMimeType());
                } else {
                    final UrlQueue<?> urlQueue = CrawlingParameterUtil.getUrlQueue();
                    processResult(urlQueue, responseData, resultData);
                }
            }
        } else if (logger.isDebugEnabled()) {
            logger.debug("Ignore a response({}): {}", responseData.getStatus(), responseData.getUrl());
        }
    }

    /**
     * Checks if the response is successful based on configured HTTP status codes.
     *
     * @param responseData the response data to check
     * @return true if successful, false otherwise
     */
    protected boolean isSuccessful(final ResponseData responseData) {
        if (successfulHttpCodes == null) {
            return true;
        }
        final int httpStatusCode = responseData.getHttpStatusCode();
        for (final int code : successfulHttpCodes) {
            if (code == httpStatusCode) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the response is not modified based on configured HTTP status codes.
     *
     * @param responseData the response data to check
     * @return true if not modified, false otherwise
     */
    protected boolean isNotModified(final ResponseData responseData) {
        if (notModifiedHttpCodes == null) {
            return false;
        }
        final int httpStatusCode = responseData.getHttpStatusCode();
        for (final int code : notModifiedHttpCodes) {
            if (code == httpStatusCode) {
                return true;
            }
        }
        return false;
    }

    /**
     * Processes the result data and stores it along with child URLs.
     *
     * @param urlQueue the URL queue entry
     * @param responseData the response data
     * @param resultData the result data
     */
    protected void processResult(final UrlQueue<?> urlQueue, final ResponseData responseData, final ResultData resultData) {
        final CrawlerContext crawlerContext = CrawlingParameterUtil.getCrawlerContext();
        final UrlQueueService<UrlQueue<?>> urlQueueService = CrawlingParameterUtil.getUrlQueueService();
        if (!urlQueueService.visited(urlQueue)) {
            if (checkAccessCount(crawlerContext)) {
                final AccessResult<?> accessResult = createAccessResult(responseData, resultData);
                if (logger.isDebugEnabled()) {
                    logger.debug("Storing accessResult: {}", accessResult);
                }
                try {
                    // store
                    CrawlingParameterUtil.getDataService().store(accessResult);
                } catch (final Exception e) {
                    crawlerContext.decrementAndGetAccessCount();
                    if (urlQueueService.visited(urlQueue)) {
                        // document already exists
                        if (logger.isDebugEnabled()) {
                            logger.debug("{} exists.", urlQueue.getUrl(), e);
                        }
                        return;
                    }
                    throw e;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Storing child urls: {}", resultData.getChildUrlSet());
                }
                final int depth = urlQueue.getDepth() == null ? 1 : urlQueue.getDepth() + 1;
                if (crawlerContext.getMaxDepth() < 0 || depth <= crawlerContext.getMaxDepth()) {
                    // add and filter urls
                    storeChildUrls(crawlerContext, resultData.getChildUrlSet(), urlQueue.getUrl(), depth, resultData.getEncoding());
                }
            } else if (crawlerContext.getMaxDepth() < 0 || urlQueue.getDepth() <= crawlerContext.getMaxDepth()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Canceled urlQueue: {}", urlQueue);
                }
                // cancel crawling
                crawlerContext.decrementAndGetAccessCount();
                final List<UrlQueue<?>> newUrlQueueList = new ArrayList<>();
                newUrlQueueList.add(urlQueue);
                urlQueueService.offerAll(crawlerContext.getSessionId(), newUrlQueueList);
            }
        } else if (logger.isDebugEnabled()) {
            logger.debug("Visited urlQueue: {}", urlQueue);
        }
    }

    /**
     * Creates an access result from response data and result data.
     *
     * @param responseData the response data
     * @param resultData the result data
     * @return the created access result
     */
    protected AccessResult<?> createAccessResult(final ResponseData responseData, final ResultData resultData) {
        final AccessResult<?> accessResult = crawlerContainer.getComponent("accessResult");
        accessResult.init(responseData, resultData);
        return accessResult;
    }

    /**
     * Checks if the access count is within the allowed limit.
     *
     * @param crawlerContext the crawler context
     * @return true if access count is within limit, false otherwise
     */
    protected boolean checkAccessCount(final CrawlerContext crawlerContext) {
        if (crawlerContext.getMaxAccessCount() > 0) {
            return crawlerContext.incrementAndGetAccessCount() <= crawlerContext.getMaxAccessCount();
        }
        return true;
    }

    /**
     * Stores child URLs found in the response data.
     *
     * @param crawlerContext the crawler context
     * @param childUrlList the set of child URLs
     * @param url the parent URL
     * @param depth the depth of the child URLs
     * @param encoding the encoding of the child URLs
     */
    protected void storeChildUrls(final CrawlerContext crawlerContext, final Set<RequestData> childUrlList, final String url,
            final int depth, final String encoding) {
        // add url and filter
        final Set<String> urlSet = new HashSet<>();
        final List<UrlQueue<?>> childList = childUrlList.stream()
                .filter(d -> StringUtil.isNotBlank(d.getUrl()) && urlSet.add(d.getUrl()) && crawlerContext.getUrlFilter().match(d.getUrl()))
                .map(d -> {
                    final UrlQueue<?> uq = crawlerContainer.getComponent("urlQueue");
                    uq.setCreateTime(SystemUtil.currentTimeMillis());
                    uq.setDepth(depth);
                    uq.setMethod(d.getMethod().name());
                    uq.setEncoding(encoding);
                    uq.setParentUrl(url);
                    uq.setSessionId(crawlerContext.getSessionId());
                    uq.setUrl(d.getUrl());
                    uq.setWeight(d.getWeight());
                    return uq;
                }).collect(Collectors.toList());

        if (!childList.isEmpty()) {
            CrawlingParameterUtil.getUrlQueueService().offerAll(crawlerContext.getSessionId(), childList);
        }
    }

    /**
     * Gets the transformer.
     *
     * @return the transformer
     */
    public Transformer getTransformer() {
        return transformer;
    }

    /**
     * Sets the transformer.
     *
     * @param transformer the transformer to set
     */
    public void setTransformer(final Transformer transformer) {
        this.transformer = transformer;
    }

    /**
     * Gets the successful HTTP codes.
     *
     * @return the successful HTTP codes
     */
    public int[] getSuccessfulHttpCodes() {
        return successfulHttpCodes;
    }

    /**
     * Sets the successful HTTP codes.
     *
     * @param successfulHttpCodes the successful HTTP codes to set
     */
    public void setSuccessfulHttpCodes(final int[] successfulHttpCodes) {
        this.successfulHttpCodes = successfulHttpCodes;
    }

    /**
     * Gets the not modified HTTP codes.
     *
     * @return the not modified HTTP codes
     */
    public int[] getNotModifiedHttpCodes() {
        return notModifiedHttpCodes;
    }

    /**
     * Sets the not modified HTTP codes.
     * @param notModifiedHttpCodes The not modified HTTP codes to set.
     */
    public void setNotModifiedHttpCodes(final int[] notModifiedHttpCodes) {
        this.notModifiedHttpCodes = notModifiedHttpCodes;
    }
}
