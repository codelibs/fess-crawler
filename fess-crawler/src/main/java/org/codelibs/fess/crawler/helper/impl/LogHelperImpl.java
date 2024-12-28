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
package org.codelibs.fess.crawler.helper.impl;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.helper.LogHelper;
import org.codelibs.fess.crawler.log.LogType;

/**
 * @author shinsuke
 *
 */
public class LogHelperImpl implements LogHelper {

    private static final Logger logger = LogManager.getLogger(LogHelperImpl.class);

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.helper.LogHelper#log(org.codelibs.fess.crawler.log.LogType,
     * java.lang.Object)
     */
    @Override
    public void log(final LogType key, final Object... objs) {
        switch (key) {
        case START_THREAD:
            processStartThread(objs);
            break;
        case START_CRAWLING:
            processStartCrawling(objs);
            break;
        case CLEANUP_CRAWLING:
            processCleanupCrawling(objs);
            break;
        case UNSUPPORTED_URL_AT_CRAWLING_STARTED:
            processUnsupportedUrlAtCrawlingStarted(objs);
            break;
        case CHECK_LAST_MODIFIED:
            processCheckLastModified(objs);
            break;
        case NOT_MODIFIED:
            processNotModified(objs);
            break;
        case GET_CONTENT:
            processGetContent(objs);
            break;
        case REDIRECT_LOCATION:
            processRedirectLocation(objs);
            break;
        case PROCESS_RESPONSE:
            processProcessResponse(objs);
            break;
        case FINISHED_CRAWLING:
            processFinishedCrawling(objs);
            break;
        case PROCESS_CHILD_URLS_BY_EXCEPTION:
            processProcessChildUrlsByException(objs);
            break;
        case PROCESS_CHILD_URL_BY_EXCEPTION:
            processProcessChildUrlByException(objs);
            break;
        case CRAWLING_ACCESS_EXCEPTION:
            processCrawlingAccessException(objs);
            break;
        case CRAWLING_EXCETPION:
            processCrawlingException(objs);
            break;
        case NO_URL_IN_QUEUE:
            processNoUrlInQueue(objs);
            break;
        case FINISHED_THREAD:
            processFinishedThread(objs);
            break;
        case NO_RESPONSE_PROCESSOR:
            processNoResponseProcessor(objs);
            break;
        case NO_RULE:
            processNoRule(objs);
            break;
        case SYSTEM_ERROR:
            processSystemError(objs);
            break;
        default:
            processDefault(objs);
            break;
        }
    }

    protected void processDefault(final Object... objs) {
    }

    protected void processSystemError(final Object... objs) {
        final Throwable t = (Throwable) objs[0];
        if (logger.isErrorEnabled()) {
            logger.error("System Error.", t);
        }
    }

    protected void processNoRule(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        // UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        final ResponseData responseData = (ResponseData) objs[2];
        if (logger.isDebugEnabled()) {
            logger.debug("No rule for ({}, {}). PLEASE CHECK YOUR CONFIGURATION.", responseData.getUrl(), responseData.getMimeType());
        }
    }

    protected void processNoResponseProcessor(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        // UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        final ResponseData responseData = (ResponseData) objs[2];
        // Rule rule = (Rule) objs[3];
        if (logger.isDebugEnabled()) {
            logger.debug("No ResponseProcessor for ({}, {}). PLEASE CHECK YOUR CONFIGURATION.", responseData.getUrl(),
                    responseData.getMimeType());
        }
    }

    protected void processFinishedThread(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
    }

    protected void processNoUrlInQueue(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        final Integer threadCheckCount = (Integer) objs[2];
        if (logger.isDebugEnabled()) {
            if (urlQueue != null && urlQueue.getUrl() != null) {
                logger.debug("{} is not a target url. ({})", urlQueue.getUrl(), threadCheckCount);
            } else {
                logger.debug("The url is null. ({})", threadCheckCount);
            }
        }
    }

    protected void processCrawlingException(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        final Throwable e = (Throwable) objs[2];
        logger.error("Crawling Exception at " + urlQueue.getUrl(), e);
    }

    protected void processCrawlingAccessException(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        final CrawlingAccessException e = (CrawlingAccessException) objs[2];
        if (e.isDebugEnabled()) {
            logger.debug("Crawling Access Exception at {}", urlQueue.getUrl(), e);
        } else if (e.isInfoEnabled()) {
            logger.info(e.getMessage());
        } else if (e.isWarnEnabled()) {
            logger.warn("Crawling Access Exception at " + urlQueue.getUrl(), e);
        } else if (e.isErrorEnabled()) {
            logger.error("Crawling Access Exception at " + urlQueue.getUrl(), e);
        }
    }

    protected void processProcessChildUrlByException(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        final String url = (String) objs[2];
        final Throwable e = (Throwable) objs[3];
        if (logger.isDebugEnabled()) {
            logger.debug("Child URL: {} from {}", url, urlQueue.getUrl(), e);
        }
    }

    protected void processProcessChildUrlsByException(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        @SuppressWarnings("unchecked")
        final Set<RequestData> requestDataSet = (Set<RequestData>) objs[2];
        if (logger.isDebugEnabled()) {
            for (final RequestData requestData : requestDataSet) {
                logger.debug("Child URL: {} from {}", requestData.getUrl(), urlQueue.getUrl());
            }
        }
    }

    protected void processFinishedCrawling(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        if (logger.isDebugEnabled()) {
            logger.debug("Finished {}", urlQueue.getUrl());
        }
    }

    protected void processProcessResponse(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        // UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        final ResponseData responseData = (ResponseData) objs[2];
        if (logger.isDebugEnabled()) {
            logger.debug("Processing the response. Http Status: {}, Exec Time: {}", responseData.getHttpStatusCode(),
                    responseData.getExecutionTime());
        }
    }

    protected void processRedirectLocation(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        // final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        final ResponseData responseData = (ResponseData) objs[2];
        if (logger.isInfoEnabled()) {
            logger.info("Redirect to URL: {}", responseData.getRedirectLocation());
        }
    }

    protected void processGetContent(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        if (logger.isDebugEnabled()) {
            logger.debug("Getting the content from URL: {}", urlQueue.getUrl());
        }
    }

    protected void processNotModified(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        if (logger.isInfoEnabled()) {
            logger.info("Not modified URL: {}", urlQueue.getUrl());
        }
    }

    protected void processCheckLastModified(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        if (logger.isDebugEnabled()) {
            logger.debug("Checking the last modified: {}", urlQueue.getLastModified());
        }
    }

    protected void processUnsupportedUrlAtCrawlingStarted(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        if (logger.isInfoEnabled()) {
            logger.info("Unsupported URL: {}", urlQueue.getUrl());
        }
    }

    protected void processCleanupCrawling(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
    }

    protected void processStartCrawling(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        if (logger.isInfoEnabled()) {
            logger.info("Crawling URL: {}", urlQueue.getUrl());
        }
    }

    protected void processStartThread(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
    }
}
