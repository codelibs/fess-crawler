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
package org.codelibs.fess.crawler.processor.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class DefaultResponseProcessor implements ResponseProcessor {
    private static final Logger logger = LoggerFactory
            .getLogger(DefaultResponseProcessor.class);

    @Resource
    protected CrawlerContainer crawlerContainer;

    protected Transformer transformer;

    protected int[] successfulHttpCodes;

    protected int[] notModifiedHttpCodes;

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
                    logger.debug("No Transformer for (" + responseData.getUrl()
                            + "). PLEASE CHECK YOUR CONFIGURATION.");
                }
            } else {
                final ResultData resultData = transformer
                        .transform(responseData);
                if (resultData == null) {
                    logger.warn("No data for (" + responseData.getUrl() + ", "
                            + responseData.getMimeType() + ")");
                } else {
                    final UrlQueue<?> urlQueue = CrawlingParameterUtil
                            .getUrlQueue();
                    processResult(urlQueue, responseData, resultData);
                }
            }
        } else if (logger.isDebugEnabled()) {
            logger.debug("Ignore a response(" + responseData.getStatus()
                    + "): " + responseData.getUrl());
        }
    }

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

    protected void processResult(final UrlQueue<?> urlQueue,
            final ResponseData responseData, final ResultData resultData) {
        final AccessResult<?> accessResult = crawlerContainer
                .getComponent("accessResult");
        accessResult.init(responseData, resultData);

        final CrawlerContext crawlerContext = CrawlingParameterUtil
                .getCrawlerContext();
        final UrlQueueService<UrlQueue<?>> urlQueueService = CrawlingParameterUtil
                .getUrlQueueService();
        if (logger.isDebugEnabled()) {
            logger.debug("Processing accessResult: " + accessResult);
        }
        if (!urlQueueService.visited(urlQueue)) {
            if (checkAccessCount(crawlerContext)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Storing accessResult: " + accessResult);
                }
                try {
                    // store
                    CrawlingParameterUtil.getDataService().store(accessResult);
                } catch (final RuntimeException e) {
                    crawlerContext.decrementAndGetAccessCount();
                    if (urlQueueService.visited(urlQueue)) {
                        // document already exists
                        if (logger.isDebugEnabled()) {
                            logger.debug(urlQueue.getUrl() + " exists.", e);
                        }
                        return;
                    }
                    throw e;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Storing child urls: " + resultData.getChildUrlSet());
                }
                // add and filter urls
                storeChildUrls(crawlerContext, resultData.getChildUrlSet(), urlQueue.getUrl(),
                        urlQueue.getDepth() == null ? 1 : urlQueue.getDepth() + 1, resultData.getEncoding());
            } else if (crawlerContext.getMaxDepth() < 0 || urlQueue.getDepth() <= crawlerContext.getMaxDepth()) {
                // cancel crawling
                crawlerContext.decrementAndGetAccessCount();
                final List<UrlQueue<?>> newUrlQueueList = new ArrayList<>();
                newUrlQueueList.add(urlQueue);
                urlQueueService.offerAll(crawlerContext.getSessionId(), newUrlQueueList);
            }
        }
    }

    private boolean checkAccessCount(final CrawlerContext crawlerContext) {
        if (crawlerContext.getMaxAccessCount() > 0) {
            return crawlerContext.incrementAndGetAccessCount() <= crawlerContext.getMaxAccessCount();
        }
        return true;
    }

    private void storeChildUrls(final CrawlerContext crawlerContext,
            final Set<RequestData> childUrlList, final String url,
            final int depth, final String encoding) {
        if (crawlerContext.getMaxDepth() >= 0
                && depth > crawlerContext.getMaxDepth()) {
            return;
        }

        // add url and filter
        final Set<String> urlSet = new HashSet<>();
        final List<UrlQueue<?>> childList = childUrlList.stream().filter(d -> StringUtil.isNotBlank(d.getUrl())
                && urlSet.add(d.getUrl() + "\n" + d.getMetaData()) && crawlerContext.getUrlFilter().match(d.getUrl())).map(d -> {
                    final UrlQueue<?> uq = crawlerContainer.getComponent("urlQueue");
                    uq.setCreateTime(SystemUtil.currentTimeMillis());
                    uq.setDepth(depth);
                    uq.setMethod(d.getMethod().name());
                    uq.setEncoding(encoding);
                    uq.setParentUrl(url);
                    uq.setSessionId(crawlerContext.getSessionId());
                    uq.setUrl(d.getUrl());
                    uq.setMetaData(d.getMetaData());
                    return uq;
                }).collect(Collectors.toList());

        if (!childList.isEmpty()) {
            CrawlingParameterUtil.getUrlQueueService().offerAll(crawlerContext.getSessionId(), childList);
        }
    }

    public Transformer getTransformer() {
        return transformer;
    }

    public void setTransformer(final Transformer transformer) {
        this.transformer = transformer;
    }

    public int[] getSuccessfulHttpCodes() {
        return successfulHttpCodes;
    }

    public void setSuccessfulHttpCodes(final int[] successfulHttpCodes) {
        this.successfulHttpCodes = successfulHttpCodes;
    }

    public int[] getNotModifiedHttpCodes() {
        return notModifiedHttpCodes;
    }

    public void setNotModifiedHttpCodes(final int[] notModifiedHttpCodes) {
        this.notModifiedHttpCodes = notModifiedHttpCodes;
    }
}
