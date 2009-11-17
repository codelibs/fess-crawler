/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.robot.helper.impl;

import java.util.Set;

import org.seasar.robot.RobotCrawlAccessException;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.UrlQueue;
import org.seasar.robot.helper.LogHelper;
import org.seasar.robot.log.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class LogHelperImpl implements LogHelper {
    private static final Logger logger = LoggerFactory
            .getLogger(LogHelperImpl.class);

    /* (non-Javadoc)
     * @see org.seasar.robot.helper.LogHelper#log(org.seasar.robot.log.LogType, java.lang.Object)
     */
    public void log(LogType key, Object... objs) {
        switch (key) {
        case START_THREAD: {
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            break;
        }
        case START_CRAWLING: {
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            UrlQueue urlQueue = (UrlQueue) objs[1];
            if (logger.isInfoEnabled()) {
                logger.info("Crawling URL: " + urlQueue.getUrl());
            }
            break;
        }
        case UNSUPPORTED_URL_AT_CRAWLING_STARTED: {
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            UrlQueue urlQueue = (UrlQueue) objs[1];
            if (logger.isInfoEnabled()) {
                logger.info("Unsupported URL: " + urlQueue.getUrl());
            }
            break;
        }
        case CHECK_LAST_MODIFIED: {
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            UrlQueue urlQueue = (UrlQueue) objs[1];
            if (logger.isDebugEnabled()) {
                logger.debug("Checking the last modified: "
                        + urlQueue.getLastModified());
            }
            break;
        }
        case NOT_MODIFIED: {
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            UrlQueue urlQueue = (UrlQueue) objs[1];
            if (logger.isInfoEnabled()) {
                logger.info("Not modified URL: " + urlQueue.getUrl());
            }
            break;
        }
        case GET_CONTENT: {
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            UrlQueue urlQueue = (UrlQueue) objs[1];
            if (logger.isDebugEnabled()) {
                logger.debug("Getting the content from URL: "
                        + urlQueue.getUrl());
            }
            break;
        }
        case REDIRECT_LOCATION: {
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            UrlQueue urlQueue = (UrlQueue) objs[1];
            // ResponseData responseData = (ResponseData) objs[2];
            if (logger.isInfoEnabled()) {
                logger.info("Redirect to URL: " + urlQueue.getUrl());
            }
            break;
        }
        case PROCESS_RESPONSE: {
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            // UrlQueue urlQueue = (UrlQueue) objs[1];
            ResponseData responseData = (ResponseData) objs[2];
            if (logger.isDebugEnabled()) {
                logger.debug("Processing the response. Http Status: "
                        + responseData.getHttpStatusCode() + ", Exec Time: "
                        + responseData.getExecutionTime());
            }
            break;
        }
        case FINISHED_CRAWLING: {
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            UrlQueue urlQueue = (UrlQueue) objs[1];
            if (logger.isDebugEnabled()) {
                logger.debug("Finished " + urlQueue.getUrl());
            }
            break;
        }
        case PROCESS_CHILD_URLS_BY_EXCEPTION: {
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            UrlQueue urlQueue = (UrlQueue) objs[1];
            Set<String> childUrlSet = (Set<String>) objs[2];
            if (logger.isDebugEnabled()) {
                for (String url : childUrlSet) {
                    logger.debug("Child URL: " + url + " from "
                            + urlQueue.getUrl());
                }
            }
            break;
        }
        case CRAWLING_ACCESS_EXCEPTION: {
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            UrlQueue urlQueue = (UrlQueue) objs[1];
            RobotCrawlAccessException e = (RobotCrawlAccessException) objs[2];
            if (e.isDebugEnabled()) {
                logger.debug("Crawling Access Exception at "
                        + urlQueue.getUrl(), e);
            } else if (e.isInfoEnabled()) {
                logger.info(e.getMessage());
            } else if (e.isWarnEnabled()) {
                logger.warn(
                        "Crawling Access Exception at " + urlQueue.getUrl(), e);
            } else if (e.isErrorEnabled()) {
                logger.error("Crawling Access Exception at "
                        + urlQueue.getUrl(), e);
            }
            break;
        }
        case CRAWLING_EXCETPION: {
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            UrlQueue urlQueue = (UrlQueue) objs[1];
            Exception e = (Exception) objs[2];
            logger.error("Crawling Exception at " + urlQueue.getUrl(), e);
            break;
        }
        case NO_URL_IN_QUEUE: {
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            // UrlQueue urlQueue = (UrlQueue) objs[1];
            Integer threadCheckCount = (Integer) objs[2];
            if (logger.isDebugEnabled()) {
                logger.debug("No valid url in a queue. (" + threadCheckCount
                        + ")");
            }
            break;
        }
        case FINISHED_THREAD: {
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            break;
        }
        case NO_RESPONSE_PROCESSOR: {
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            // UrlQueue urlQueue = (UrlQueue) objs[1];
            ResponseData responseData = (ResponseData) objs[2];
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
            // S2RobotContext robotContext = (S2RobotContext) objs[0];
            // UrlQueue urlQueue = (UrlQueue) objs[1];
            ResponseData responseData = (ResponseData) objs[2];
            if (logger.isDebugEnabled()) {
                logger.debug("No rule for (" + responseData.getUrl() + ", "
                        + responseData.getMimeType()
                        + "). PLEASE CHECK YOUR CONFIGURATION.");
            }
            break;
        }
        default:
            break;
        }
    }
}
