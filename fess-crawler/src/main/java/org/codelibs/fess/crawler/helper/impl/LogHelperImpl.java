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
package org.codelibs.fess.crawler.helper.impl;

import java.util.Set;

import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.helper.LogHelper;
import org.codelibs.fess.crawler.log.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class LogHelperImpl implements LogHelper {
    private static final Logger logger = LoggerFactory
            .getLogger(LogHelperImpl.class);

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.helper.LogHelper#log(org.codelibs.fess.crawler.log.LogType,
     * java.lang.Object)
     */
    @Override
    public void log(final LogType key, final Object... objs) {
        switch (key) {
            case START_THREAD: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                break;
            }
            case START_CRAWLING: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                if (logger.isInfoEnabled()) {
                    logger.info("Crawling URL: " + urlQueue.getUrl());
                }
                break;
            }
            case UNSUPPORTED_URL_AT_CRAWLING_STARTED: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                if (logger.isInfoEnabled()) {
                    logger.info("Unsupported URL: " + urlQueue.getUrl());
                }
                break;
            }
            case CHECK_LAST_MODIFIED: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                if (logger.isDebugEnabled()) {
                    logger.debug("Checking the last modified: "
                            + urlQueue.getLastModified());
                }
                break;
            }
            case NOT_MODIFIED: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                if (logger.isInfoEnabled()) {
                    logger.info("Not modified URL: " + urlQueue.getUrl());
                }
                break;
            }
            case GET_CONTENT: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                if (logger.isDebugEnabled()) {
                    logger.debug("Getting the content from URL: "
                            + urlQueue.getUrl());
                }
                break;
            }
            case REDIRECT_LOCATION: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                // final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                final ResponseData responseData = (ResponseData) objs[2];
                if (logger.isInfoEnabled()) {
                    logger.info("Redirect to URL: " + responseData.getRedirectLocation());
                }
                break;
            }
            case PROCESS_RESPONSE: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                // UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                final ResponseData responseData = (ResponseData) objs[2];
                if (logger.isDebugEnabled()) {
                    logger.debug("Processing the response. Http Status: "
                            + responseData.getHttpStatusCode()
                            + ", Exec Time: " + responseData.getExecutionTime());
                }
                break;
            }
            case FINISHED_CRAWLING: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                if (logger.isDebugEnabled()) {
                    logger.debug("Finished " + urlQueue.getUrl());
                }
                break;
            }
            case PROCESS_CHILD_URLS_BY_EXCEPTION: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                @SuppressWarnings("unchecked")
                final Set<RequestData> requestDataSet = (Set<RequestData>) objs[2];
                if (logger.isDebugEnabled()) {
                    for (final RequestData requestData : requestDataSet) {
                        logger.debug("Child URL: " + requestData.getUrl()
                                + " from " + urlQueue.getUrl());
                    }
                }
                break;
            }
            case PROCESS_CHILD_URL_BY_EXCEPTION: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                final String url = (String) objs[2];
                final Throwable e = (Throwable) objs[3];
                if (logger.isDebugEnabled()) {
                    logger.debug(
                            "Child URL: " + url + " from " + urlQueue.getUrl(),
                            e);
                }
                break;
            }
            case CRAWLING_ACCESS_EXCEPTION: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                final CrawlingAccessException e = (CrawlingAccessException) objs[2];
                if (e.isDebugEnabled()) {
                    logger.debug(
                            "Crawling Access Exception at " + urlQueue.getUrl(),
                            e);
                } else if (e.isInfoEnabled()) {
                    logger.info(e.getMessage());
                } else if (e.isWarnEnabled()) {
                    logger.warn(
                            "Crawling Access Exception at " + urlQueue.getUrl(),
                            e);
                } else if (e.isErrorEnabled()) {
                    logger.error(
                            "Crawling Access Exception at " + urlQueue.getUrl(),
                            e);
                }
                break;
            }
            case CRAWLING_EXCETPION: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                final Throwable e = (Throwable) objs[2];
                logger.error("Crawling Exception at " + urlQueue.getUrl(), e);
                break;
            }
            case NO_URL_IN_QUEUE: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                final Integer threadCheckCount = (Integer) objs[2];
                if (logger.isDebugEnabled()) {
                    if (urlQueue != null && urlQueue.getUrl() != null) {
                        logger.debug(urlQueue.getUrl()
                                + " is not a target url. (" + threadCheckCount
                                + ")");
                    } else {
                        logger.debug("The url is null. (" + threadCheckCount
                                + ")");
                    }
                }
                break;
            }
            case FINISHED_THREAD: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                break;
            }
            case NO_RESPONSE_PROCESSOR: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                // UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                final ResponseData responseData = (ResponseData) objs[2];
                // Rule rule = (Rule) objs[3];
                if (logger.isDebugEnabled()) {
                    logger.debug("No ResponseProcessor for ("
                            + responseData.getUrl() + ", "
                            + responseData.getMimeType()
                            + "). PLEASE CHECK YOUR CONFIGURATION.");
                }
                break;
            }
            case NO_RULE: {
                // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
                // UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
                final ResponseData responseData = (ResponseData) objs[2];
                if (logger.isDebugEnabled()) {
                    logger.debug("No rule for (" + responseData.getUrl() + ", "
                            + responseData.getMimeType()
                            + "). PLEASE CHECK YOUR CONFIGURATION.");
                }
                break;
            }
            case SYSTEM_ERROR: {
                final Throwable t = (Throwable) objs[0];
                if (logger.isErrorEnabled()) {
                    logger.error("System Error.", t);
                }
                break;
            }
            default:
                break;
        }
    }
}
