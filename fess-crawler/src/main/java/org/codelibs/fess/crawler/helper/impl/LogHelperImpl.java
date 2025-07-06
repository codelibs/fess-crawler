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
 * Implementation of the {@link LogHelper} interface.
 * This class provides methods for logging various events during the crawling process.
 * It uses Log4j2 for logging.
 *
 * <p>
 * The class contains methods for logging different types of events, such as:
 * </p>
 * <ul>
 *   <li>Starting and finishing threads</li>
 *   <li>Starting and cleaning up crawling</li>
 *   <li>Handling unsupported URLs</li>
 *   <li>Checking last modified dates</li>
 *   <li>Getting content</li>
 *   <li>Handling redirects</li>
 *   <li>Processing responses</li>
 *   <li>Handling exceptions during crawling and child URL processing</li>
 *   <li>Handling cases where no URL is in the queue</li>
 *   <li>Handling cases where no response processor or rule is found</li>
 *   <li>Handling system errors</li>
 * </ul>
 *
 * <p>
 * Each logging method checks the log level before logging the message.
 * The log level can be configured in the Log4j2 configuration file.
 * </p>
 *
 */
public class LogHelperImpl implements LogHelper {

    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(LogHelperImpl.class);

    /**
     * Creates a new LogHelperImpl instance.
     */
    public LogHelperImpl() {
        super();
    }

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
        case CRAWLING_EXCEPTION:
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

    /**
     * Processes default log events (no specific handling).
     *
     * @param objs the log objects
     */
    protected void processDefault(final Object... objs) {
    }

    /**
     * Processes system error log events.
     *
     * @param objs the log objects (should contain a Throwable)
     */
    protected void processSystemError(final Object... objs) {
        final Throwable t = (Throwable) objs[0];
        if (logger.isErrorEnabled()) {
            logger.error("System Error.", t);
        }
    }

    /**
     * Processes no rule found log events.
     *
     * @param objs the log objects (should contain ResponseData)
     */
    protected void processNoRule(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        // UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        final ResponseData responseData = (ResponseData) objs[2];
        if (logger.isDebugEnabled()) {
            logger.debug("No rule for ({}, {}). PLEASE CHECK YOUR CONFIGURATION.", responseData.getUrl(), responseData.getMimeType());
        }
    }

    /**
     * Processes no response processor found log events.
     *
     * @param objs the log objects (should contain ResponseData)
     */
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

    /**
     * Processes finished thread log events.
     *
     * @param objs the log objects
     */
    protected void processFinishedThread(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
    }

    /**
     * Processes no URL in queue log events.
     *
     * @param objs the log objects (should contain UrlQueue and thread check count)
     */
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

    /**
     * Processes crawling exception log events.
     *
     * @param objs the log objects (should contain UrlQueue and Throwable)
     */
    protected void processCrawlingException(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        final Throwable e = (Throwable) objs[2];
        logger.error("Crawling Exception at " + urlQueue.getUrl(), e);
    }

    /**
     * Processes crawling access exception log events.
     *
     * @param objs the log objects (should contain UrlQueue and CrawlingAccessException)
     */
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

    /**
     * Processes child URL processing exception log events.
     *
     * @param objs the log objects (should contain UrlQueue, URL string, and Throwable)
     */
    protected void processProcessChildUrlByException(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        final String url = (String) objs[2];
        final Throwable e = (Throwable) objs[3];
        if (logger.isDebugEnabled()) {
            logger.debug("Child URL: {} from {}", url, urlQueue.getUrl(), e);
        }
    }

    /**
     * Processes child URLs processing exception log events.
     *
     * @param objs the log objects (should contain UrlQueue and Set of RequestData)
     */
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

    /**
     * Processes finished crawling log events.
     *
     * @param objs the log objects (should contain UrlQueue)
     */
    protected void processFinishedCrawling(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        if (logger.isDebugEnabled()) {
            logger.debug("Finished {}", urlQueue.getUrl());
        }
    }

    /**
     * Processes response processing log events.
     *
     * @param objs the log objects (should contain ResponseData)
     */
    protected void processProcessResponse(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        // UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        final ResponseData responseData = (ResponseData) objs[2];
        if (logger.isDebugEnabled()) {
            logger.debug("Processing the response. Http Status: {}, Exec Time: {}", responseData.getHttpStatusCode(),
                    responseData.getExecutionTime());
        }
    }

    /**
     * Processes redirect location log events.
     *
     * @param objs the log objects (should contain ResponseData)
     */
    protected void processRedirectLocation(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        // final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        final ResponseData responseData = (ResponseData) objs[2];
        if (logger.isInfoEnabled()) {
            logger.info("Redirect to URL: {}", responseData.getRedirectLocation());
        }
    }

    /**
     * Processes get content log events.
     *
     * @param objs the log objects (should contain UrlQueue)
     */
    protected void processGetContent(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        if (logger.isDebugEnabled()) {
            logger.debug("Getting the content from URL: {}", urlQueue.getUrl());
        }
    }

    /**
     * Processes not modified log events.
     *
     * @param objs the log objects (should contain UrlQueue)
     */
    protected void processNotModified(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        if (logger.isInfoEnabled()) {
            logger.info("Not modified URL: {}", urlQueue.getUrl());
        }
    }

    /**
     * Processes check last modified log events.
     *
     * @param objs the log objects (should contain UrlQueue)
     */
    protected void processCheckLastModified(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        if (logger.isDebugEnabled()) {
            logger.debug("Checking the last modified: {}", urlQueue.getLastModified());
        }
    }

    /**
     * Processes unsupported URL log events.
     *
     * @param objs the log objects (should contain UrlQueue)
     */
    protected void processUnsupportedUrlAtCrawlingStarted(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        if (logger.isInfoEnabled()) {
            logger.info("Unsupported URL: {}", urlQueue.getUrl());
        }
    }

    /**
     * Processes cleanup crawling log events.
     *
     * @param objs the log objects
     */
    protected void processCleanupCrawling(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
    }

    /**
     * Processes start crawling log events.
     *
     * @param objs the log objects (should contain UrlQueue)
     */
    protected void processStartCrawling(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
        final UrlQueue<?> urlQueue = (UrlQueue<?>) objs[1];
        if (logger.isInfoEnabled()) {
            logger.info("Crawling URL: {}", urlQueue.getUrl());
        }
    }

    /**
     * Processes start thread log events.
     *
     * @param objs the log objects
     */
    protected void processStartThread(final Object... objs) {
        // CrawlerContext crawlerContext = (CrawlerContext) objs[0];
    }
}
