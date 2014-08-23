/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.codelibs.robot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.codelibs.robot.builder.RequestDataBuilder;
import org.codelibs.robot.client.S2RobotClient;
import org.codelibs.robot.client.S2RobotClientFactory;
import org.codelibs.robot.client.fs.ChildUrlsException;
import org.codelibs.robot.entity.RequestData;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.entity.UrlQueue;
import org.codelibs.robot.helper.LogHelper;
import org.codelibs.robot.interval.IntervalController;
import org.codelibs.robot.log.LogType;
import org.codelibs.robot.processor.ResponseProcessor;
import org.codelibs.robot.rule.Rule;
import org.codelibs.robot.service.DataService;
import org.codelibs.robot.service.UrlQueueService;
import org.codelibs.robot.util.CrawlingParameterUtil;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.StringUtil;

/**
 * @author shinsuke
 * 
 */
public class S2RobotThread implements Runnable {

    @Resource
    protected UrlQueueService urlQueueService;

    @Resource
    protected DataService dataService;

    @Resource
    protected S2Container container;

    @Binding(bindingType = BindingType.MAY)
    @Resource
    protected LogHelper logHelper;

    protected S2RobotClientFactory clientFactory;

    protected S2RobotContext robotContext;

    protected boolean noWaitOnFolder = false;

    protected void startCrawling() {
        synchronized (robotContext.activeThreadCountLock) {
            robotContext.activeThreadCount++;
        }
    }

    protected void finishCrawling() {
        synchronized (robotContext.activeThreadCountLock) {
            robotContext.activeThreadCount--;
        }
    }

    protected boolean isContinue(final int tcCount) {
        if (!SingletonS2ContainerFactory.hasContainer()) {
            // system shutdown
            return false;
        }

        boolean isContinue = false;
        if (tcCount < robotContext.maxThreadCheckCount) {
            if (robotContext.maxAccessCount > 0
                && robotContext.accessCount >= robotContext.maxAccessCount) {
                return false;
            }
            isContinue = true;
        }

        if (!isContinue && robotContext.activeThreadCount > 0) {
            // still running..
            return true;
        }

        return isContinue;
    }

    protected void log(final LogHelper logHelper, final LogType key,
            final Object... objs) {
        if (logHelper != null) {
            logHelper.log(key, objs);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        log(logHelper, LogType.START_THREAD, robotContext);
        int threadCheckCount = 0;
        // set urlQueue to thread
        CrawlingParameterUtil.setRobotContext(robotContext);
        CrawlingParameterUtil.setUrlQueueService(urlQueueService);
        CrawlingParameterUtil.setDataService(dataService);
        try {
            while (robotContext.running && isContinue(threadCheckCount)) {
                final UrlQueue urlQueue =
                    urlQueueService.poll(robotContext.sessionId);
                if (isValid(urlQueue)) {
                    ResponseData responseData = null;
                    log(
                        logHelper,
                        LogType.START_CRAWLING,
                        robotContext,
                        urlQueue);
                    try {
                        final S2RobotClient client =
                            getClient(urlQueue.getUrl());
                        if (client == null) {
                            log(
                                logHelper,
                                LogType.UNSUPPORTED_URL_AT_CRAWLING_STARTED,
                                robotContext,
                                urlQueue);
                            continue;
                        }

                        startCrawling();

                        // set urlQueue to thread
                        CrawlingParameterUtil.setUrlQueue(urlQueue);

                        if (robotContext.intervalController != null) {
                            robotContext.intervalController
                                .delay(IntervalController.PRE_PROCESSING);
                        }

                        final boolean contentUpdated =
                            isContentUpdated(client, urlQueue);

                        if (contentUpdated) {
                            log(
                                logHelper,
                                LogType.GET_CONTENT,
                                robotContext,
                                urlQueue);
                            // access an url
                            final long startTime = System.currentTimeMillis();
                            responseData =
                                client.execute(RequestDataBuilder
                                    .newRequestData()
                                    .method(urlQueue.getMethod())
                                    .url(urlQueue.getUrl())
                                    .build());
                            responseData.setExecutionTime(System
                                .currentTimeMillis() - startTime);
                            responseData.setParentUrl(urlQueue.getParentUrl());
                            responseData.setSessionId(robotContext.sessionId);

                            if (responseData.getRedirectLocation() == null) {
                                log(
                                    logHelper,
                                    LogType.PROCESS_RESPONSE,
                                    robotContext,
                                    urlQueue,
                                    responseData);
                                processResponse(urlQueue, responseData);
                            } else {
                                log(
                                    logHelper,
                                    LogType.REDIRECT_LOCATION,
                                    robotContext,
                                    urlQueue,
                                    responseData);
                                // redirect
                                synchronized (robotContext.accessCountLock) {
                                    // add an url
                                    storeChildUrl(
                                        responseData.getRedirectLocation(),
                                        urlQueue.getUrl(),
                                        null,
                                        urlQueue.getDepth() == null ? 1
                                            : urlQueue.getDepth() + 1);
                                }
                            }
                        }

                        log(
                            logHelper,
                            LogType.FINISHED_CRAWLING,
                            robotContext,
                            urlQueue);
                    } catch (final ChildUrlsException e) {
                        try {
                            final Set<RequestData> childUrlSet =
                                e.getChildUrlList();
                            log(
                                logHelper,
                                LogType.PROCESS_CHILD_URLS_BY_EXCEPTION,
                                robotContext,
                                urlQueue,
                                childUrlSet);
                            synchronized (robotContext.accessCountLock) {
                                // add an url
                                storeChildUrls(
                                    childUrlSet,
                                    urlQueue.getUrl(),
                                    urlQueue.getDepth() == null ? 1 : urlQueue
                                        .getDepth() + 1);
                            }
                        } catch (final Exception e1) {
                            log(
                                logHelper,
                                LogType.CRAWLING_EXCETPION,
                                robotContext,
                                urlQueue,
                                e1);
                        }
                        if (noWaitOnFolder) {
                            continue;
                        }
                    } catch (final RobotCrawlAccessException e) {
                        log(
                            logHelper,
                            LogType.CRAWLING_ACCESS_EXCEPTION,
                            robotContext,
                            urlQueue,
                            e);
                    } catch (final Throwable e) {
                        log(
                            logHelper,
                            LogType.CRAWLING_EXCETPION,
                            robotContext,
                            urlQueue,
                            e);
                    } finally {
                        addSitemapsFromRobotsTxt(urlQueue);

                        if (responseData != null) {
                            IOUtils
                                .closeQuietly(responseData.getResponseBody());
                        }
                        if (robotContext.intervalController != null) {
                            robotContext.intervalController
                                .delay(IntervalController.POST_PROCESSING);
                        }
                        threadCheckCount = 0; // clear
                        // remove urlQueue from thread
                        CrawlingParameterUtil.setUrlQueue(null);
                        finishCrawling();
                    }
                } else {
                    log(
                        logHelper,
                        LogType.NO_URL_IN_QUEUE,
                        robotContext,
                        urlQueue,
                        Integer.valueOf(threadCheckCount));

                    if (robotContext.intervalController != null) {
                        robotContext.intervalController
                            .delay(IntervalController.NO_URL_IN_QUEUE);
                    }

                    threadCheckCount++;
                }

                // interval
                if (robotContext.intervalController != null) {
                    robotContext.intervalController
                        .delay(IntervalController.WAIT_NEW_URL);
                }
            }
        } catch (final Throwable t) { // NOPMD
            log(logHelper, LogType.SYSTEM_ERROR, t);
        } finally {
            // remove robotContext from thread
            CrawlingParameterUtil.setRobotContext(null);
            CrawlingParameterUtil.setUrlQueueService(null);
            CrawlingParameterUtil.setDataService(null);
        }
        log(logHelper, LogType.FINISHED_THREAD, robotContext);
    }

    protected void addSitemapsFromRobotsTxt(final UrlQueue urlQueue) {
        final String[] sitemaps = robotContext.removeSitemaps();
        if (sitemaps != null) {
            for (final String childUrl : sitemaps) {
                try {
                    storeChildUrl(
                        childUrl,
                        urlQueue.getUrl(),
                        null,
                        urlQueue.getDepth() == null ? 1
                            : urlQueue.getDepth() + 1);
                } catch (final Exception e) {
                    log(
                        logHelper,
                        LogType.PROCESS_CHILD_URL_BY_EXCEPTION,
                        robotContext,
                        urlQueue,
                        childUrl,
                        e);
                }
            }
        }
    }

    protected S2RobotClient getClient(final String url) {
        return clientFactory.getClient(url);
    }

    protected boolean isContentUpdated(final S2RobotClient client,
            final UrlQueue urlQueue) {
        if (urlQueue.getLastModified() != null) {
            log(logHelper, LogType.CHECK_LAST_MODIFIED, robotContext, urlQueue);
            final long startTime = System.currentTimeMillis();
            ResponseData responseData = null;
            try {
                // head method
                responseData =
                    client.execute(RequestDataBuilder
                        .newRequestData()
                        .head()
                        .url(urlQueue.getUrl())
                        .build());
                if (responseData != null
                    && responseData.getLastModified() != null
                    && responseData.getLastModified().getTime() <= urlQueue
                        .getLastModified()
                        .getTime() && responseData.getHttpStatusCode() == 200) {
                    log(logHelper, LogType.NOT_MODIFIED, robotContext, urlQueue);

                    responseData.setExecutionTime(System.currentTimeMillis()
                        - startTime);
                    responseData.setParentUrl(urlQueue.getParentUrl());
                    responseData.setSessionId(robotContext.sessionId);
                    responseData.setStatus(Constants.NOT_MODIFIED_STATUS);
                    responseData
                        .setHttpStatusCode(Constants.NOT_MODIFIED_STATUS_CODE);
                    processResponse(urlQueue, responseData);

                    return false;
                }
            } finally {
                if (responseData != null) {
                    IOUtils.closeQuietly(responseData.getResponseBody());
                }
            }
        }
        return true;
    }

    protected void processResponse(final UrlQueue urlQueue,
            final ResponseData responseData) {
        // get a rule
        final Rule rule = robotContext.ruleManager.getRule(responseData);
        if (rule == null) {
            log(
                logHelper,
                LogType.NO_RULE,
                robotContext,
                urlQueue,
                responseData);
        } else {
            responseData.setRuleId(rule.getRuleId());
            final ResponseProcessor responseProcessor =
                rule.getResponseProcessor();
            if (responseProcessor == null) {
                log(
                    logHelper,
                    LogType.NO_RESPONSE_PROCESSOR,
                    robotContext,
                    urlQueue,
                    responseData,
                    rule);
            } else {
                responseProcessor.process(responseData);
            }
        }

    }

    protected void storeChildUrls(final Set<RequestData> childUrlList,
            final String url, final int depth) {
        if (robotContext.getMaxDepth() >= 0
            && depth > robotContext.getMaxDepth()) {
            return;
        }

        // add url and filter
        final List<UrlQueue> childList = new ArrayList<UrlQueue>();
        for (final RequestData requestData : childUrlList) {
            if (robotContext.urlFilter.match(requestData.getUrl())) {
                final UrlQueue uq =
                    (UrlQueue) container.getComponent(UrlQueue.class);
                uq.setCreateTime(new Timestamp(System.currentTimeMillis()));
                uq.setDepth(depth);
                uq.setMethod(Constants.GET_METHOD);
                uq.setParentUrl(url);
                uq.setSessionId(robotContext.sessionId);
                uq.setUrl(requestData.getUrl());
                uq.setMetaData(requestData.getMetaData());
                childList.add(uq);
            }
        }
        urlQueueService.offerAll(robotContext.sessionId, childList);
    }

    protected void storeChildUrl(final String childUrl, final String parentUrl,
            String metaData, final int depth) {
        if (robotContext.getMaxDepth() >= 0
            && depth > robotContext.getMaxDepth()) {
            return;
        }

        // add url and filter
        if (robotContext.urlFilter.match(childUrl)) {
            final List<UrlQueue> childList = new ArrayList<UrlQueue>(1);
            final UrlQueue uq =
                (UrlQueue) container.getComponent(UrlQueue.class);
            uq.setCreateTime(new Timestamp(new Date().getTime()));
            uq.setDepth(depth);
            uq.setMethod(Constants.GET_METHOD);
            uq.setParentUrl(parentUrl);
            uq.setSessionId(robotContext.sessionId);
            uq.setUrl(childUrl);
            uq.setMetaData(metaData);
            childList.add(uq);
            urlQueueService.offerAll(robotContext.sessionId, childList);
        }
    }

    protected boolean isValid(final UrlQueue urlQueue) {
        if (urlQueue == null) {
            return false;
        }

        if (StringUtil.isBlank(urlQueue.getUrl())) {
            return false;
        }

        if (robotContext.getMaxDepth() >= 0
            && urlQueue.getDepth() > robotContext.getMaxDepth()) {
            return false;
        }

        // url filter
        if (robotContext.urlFilter.match(urlQueue.getUrl())) {
            return true;
        }

        return false;
    }

    public boolean isNoWaitOnFolder() {
        return noWaitOnFolder;
    }

    public void setNoWaitOnFolder(final boolean noWaitOnFolder) {
        this.noWaitOnFolder = noWaitOnFolder;
    }
}
