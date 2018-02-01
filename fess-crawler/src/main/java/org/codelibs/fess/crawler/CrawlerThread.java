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
package org.codelibs.fess.crawler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.SystemUtil;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.CrawlerClient;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.helper.LogHelper;
import org.codelibs.fess.crawler.interval.IntervalController;
import org.codelibs.fess.crawler.log.LogType;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.codelibs.fess.crawler.rule.Rule;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;

/**
 * @author shinsuke
 *
 */
public class CrawlerThread implements Runnable {

    @Resource
    protected UrlQueueService<UrlQueue<?>> urlQueueService;

    @Resource
    protected DataService<AccessResult<?>> dataService;

    @Resource
    protected CrawlerContainer crawlerContainer;

    @Resource
    protected LogHelper logHelper;

    protected CrawlerClientFactory clientFactory;

    protected CrawlerContext crawlerContext;

    protected boolean noWaitOnFolder = false;

    protected void startCrawling() {
        synchronized (crawlerContext.activeThreadCountLock) {
            crawlerContext.activeThreadCount++;
        }
    }

    protected void finishCrawling() {
        synchronized (crawlerContext.activeThreadCountLock) {
            crawlerContext.activeThreadCount--;
        }
    }

    protected boolean isContinue(final int tcCount) {
        if (!crawlerContainer.available()) {
            // system shutdown
            return false;
        }

        boolean isContinue = false;
        if (tcCount < crawlerContext.maxThreadCheckCount) {
            final long maxAccessCount = crawlerContext.getMaxAccessCount();
            if (maxAccessCount > 0 && crawlerContext.getAccessCount() >= maxAccessCount) {
                return false;
            }
            isContinue = true;
        }

        if (!isContinue && crawlerContext.activeThreadCount > 0) {
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
        log(logHelper, LogType.START_THREAD, crawlerContext);
        int threadCheckCount = 0;
        // set urlQueue to thread
        CrawlingParameterUtil.setCrawlerContext(crawlerContext);
        CrawlingParameterUtil.setUrlQueueService(urlQueueService);
        CrawlingParameterUtil.setDataService(dataService);
        try {
            while (crawlerContext.getStatus() != CrawlerStatus.DONE && isContinue(threadCheckCount)) {
                final UrlQueue<?> urlQueue = urlQueueService
                        .poll(crawlerContext.sessionId);
                if (isValid(urlQueue)) {
                    ResponseData responseData = null;
                    log(logHelper, LogType.START_CRAWLING, crawlerContext,
                            urlQueue);
                    try {
                        final CrawlerClient client = getClient(urlQueue
                                .getUrl());
                        if (client == null) {
                            log(logHelper,
                                    LogType.UNSUPPORTED_URL_AT_CRAWLING_STARTED,
                                    crawlerContext, urlQueue);
                            continue;
                        }

                        startCrawling();

                        // set urlQueue to thread
                        CrawlingParameterUtil.setUrlQueue(urlQueue);

                        if (crawlerContext.intervalController != null) {
                            crawlerContext.intervalController
                                    .delay(IntervalController.PRE_PROCESSING);
                        }

                        final boolean contentUpdated = isContentUpdated(client,
                                urlQueue);

                        if (contentUpdated) {
                            log(logHelper, LogType.GET_CONTENT, crawlerContext,
                                    urlQueue);
                            // access an url
                            final long startTime = SystemUtil
                                    .currentTimeMillis();
                            responseData = client.execute(RequestDataBuilder
                                    .newRequestData()
                                    .method(urlQueue.getMethod())
                                    .url(urlQueue.getUrl()).build());
                            responseData.setExecutionTime(SystemUtil
                                    .currentTimeMillis() - startTime);
                            responseData.setParentUrl(urlQueue.getParentUrl());
                            responseData.setSessionId(crawlerContext.sessionId);

                            if (responseData.getRedirectLocation() == null) {
                                log(logHelper, LogType.PROCESS_RESPONSE,
                                        crawlerContext, urlQueue, responseData);
                                processResponse(urlQueue, responseData);
                            } else {
                                log(logHelper, LogType.REDIRECT_LOCATION,
                                        crawlerContext, urlQueue, responseData);
                                // redirect
                                storeChildUrl(responseData.getRedirectLocation(), urlQueue.getUrl(), null,
                                        urlQueue.getDepth() == null ? 1 : urlQueue.getDepth() + 1);
                            }
                        }

                        log(logHelper, LogType.FINISHED_CRAWLING, crawlerContext,
                                urlQueue);
                    } catch (final ChildUrlsException e) {
                        try {
                            final Set<RequestData> childUrlSet = e
                                    .getChildUrlList();
                            log(logHelper,
                                    LogType.PROCESS_CHILD_URLS_BY_EXCEPTION,
                                    crawlerContext, urlQueue, childUrlSet);
                            // add an url
                            storeChildUrls(childUrlSet, urlQueue.getUrl(), urlQueue.getDepth() == null ? 1 : urlQueue.getDepth() + 1);
                        } catch (final Exception e1) {
                            log(logHelper, LogType.CRAWLING_EXCETPION,
                                    crawlerContext, urlQueue, e1);
                        }
                        if (noWaitOnFolder) {
                            continue;
                        }
                    } catch (final CrawlingAccessException e) {
                        log(logHelper, LogType.CRAWLING_ACCESS_EXCEPTION,
                                crawlerContext, urlQueue, e);
                    } catch (final Throwable e) {
                        log(logHelper, LogType.CRAWLING_EXCETPION,
                                crawlerContext, urlQueue, e);
                    } finally {
                        addSitemapsFromRobotsTxt(urlQueue);

                        if (responseData != null) {
                            CloseableUtil.closeQuietly(responseData);
                        }
                        if (crawlerContext.intervalController != null) {
                            crawlerContext.intervalController
                                    .delay(IntervalController.POST_PROCESSING);
                        }
                        threadCheckCount = 0; // clear
                        // remove urlQueue from thread
                        CrawlingParameterUtil.setUrlQueue(null);
                        finishCrawling();
                    }
                } else {
                    log(logHelper, LogType.NO_URL_IN_QUEUE, crawlerContext,
                            urlQueue, Integer.valueOf(threadCheckCount));

                    if (crawlerContext.intervalController != null) {
                        crawlerContext.intervalController
                                .delay(IntervalController.NO_URL_IN_QUEUE);
                    }

                    threadCheckCount++;
                }

                // interval
                if (crawlerContext.intervalController != null) {
                    crawlerContext.intervalController
                            .delay(IntervalController.WAIT_NEW_URL);
                }
            }
        } catch (final Throwable t) {
            log(logHelper, LogType.SYSTEM_ERROR, t);
        } finally {
            // remove crawlerContext from thread
            CrawlingParameterUtil.setCrawlerContext(null);
            CrawlingParameterUtil.setUrlQueueService(null);
            CrawlingParameterUtil.setDataService(null);
        }
        log(logHelper, LogType.FINISHED_THREAD, crawlerContext);
    }

    protected void addSitemapsFromRobotsTxt(final UrlQueue<?> urlQueue) {
        final String[] sitemaps = crawlerContext.removeSitemaps();
        if (sitemaps != null) {
            for (final String childUrl : sitemaps) {
                try {
                    storeChildUrl(
                            childUrl,
                            urlQueue.getUrl(),
                            null,
                            urlQueue.getDepth() == null ? 1 : urlQueue
                                    .getDepth() + 1);
                } catch (final Exception e) {
                    log(logHelper, LogType.PROCESS_CHILD_URL_BY_EXCEPTION,
                            crawlerContext, urlQueue, childUrl, e);
                }
            }
        }
    }

    protected CrawlerClient getClient(final String url) {
        return clientFactory.getClient(url);
    }

    protected boolean isContentUpdated(final CrawlerClient client,
            final UrlQueue<?> urlQueue) {
        if (urlQueue.getLastModified() != null) {
            log(logHelper, LogType.CHECK_LAST_MODIFIED, crawlerContext, urlQueue);
            final long startTime = SystemUtil.currentTimeMillis();
            ResponseData responseData = null;
            try {
                // head method
                responseData = client
                        .execute(RequestDataBuilder.newRequestData().head()
                                .url(urlQueue.getUrl()).build());
                if (responseData != null
                        && responseData.getLastModified() != null
                        && responseData.getLastModified().getTime() <= urlQueue
                                .getLastModified().longValue()
                        && responseData.getHttpStatusCode() == 200) {
                    log(logHelper, LogType.NOT_MODIFIED, crawlerContext, urlQueue);

                    responseData.setExecutionTime(SystemUtil
                            .currentTimeMillis() - startTime);
                    responseData.setParentUrl(urlQueue.getParentUrl());
                    responseData.setSessionId(crawlerContext.sessionId);
                    responseData.setStatus(Constants.NOT_MODIFIED_STATUS);
                    responseData
                            .setHttpStatusCode(Constants.NOT_MODIFIED_STATUS_CODE);
                    processResponse(urlQueue, responseData);

                    return false;
                }
            } finally {
                if (responseData != null) {
                    CloseableUtil.closeQuietly(responseData);
                }
            }
        }
        return true;
    }

    protected void processResponse(final UrlQueue<?> urlQueue,
            final ResponseData responseData) {
        // get a rule
        final Rule rule = crawlerContext.ruleManager.getRule(responseData);
        if (rule == null) {
            log(logHelper, LogType.NO_RULE, crawlerContext, urlQueue,
                    responseData);
        } else {
            responseData.setRuleId(rule.getRuleId());
            final ResponseProcessor responseProcessor = rule
                    .getResponseProcessor();
            if (responseProcessor == null) {
                log(logHelper, LogType.NO_RESPONSE_PROCESSOR, crawlerContext,
                        urlQueue, responseData, rule);
            } else {
                responseProcessor.process(responseData);
            }
        }

    }

    protected void storeChildUrls(final Set<RequestData> childUrlList,
            final String url, final int depth) {
        if (crawlerContext.getMaxDepth() >= 0
                && depth > crawlerContext.getMaxDepth()) {
            return;
        }

        // add url and filter
        final Set<String> urlSet = new HashSet<>();
        final List<UrlQueue<?>> childList = childUrlList.stream().filter(d -> StringUtil.isNotBlank(d.getUrl())
                && urlSet.add(d.getUrl() + "\n" + d.getMetaData()) && crawlerContext.urlFilter.match(d.getUrl())).map(d -> {
                    final UrlQueue<?> uq = crawlerContainer.getComponent("urlQueue");
                    uq.setCreateTime(SystemUtil.currentTimeMillis());
                    uq.setDepth(depth);
                    uq.setMethod(Constants.GET_METHOD);
                    uq.setParentUrl(url);
                    uq.setSessionId(crawlerContext.sessionId);
                    uq.setUrl(d.getUrl());
                    uq.setMetaData(d.getMetaData());
                    return uq;
                }).collect(Collectors.toList());
        urlQueueService.offerAll(crawlerContext.sessionId, childList);
    }

    protected void storeChildUrl(final String childUrl, final String parentUrl,
            final String metaData, final int depth) {
        if (crawlerContext.getMaxDepth() >= 0
                && depth > crawlerContext.getMaxDepth()) {
            return;
        }

        // add url and filter
        if (StringUtil.isNotBlank(childUrl) && crawlerContext.urlFilter.match(childUrl)) {
            final List<UrlQueue<?>> childList = new ArrayList< >(1);
            final UrlQueue<?> uq = crawlerContainer.getComponent("urlQueue");
            uq.setCreateTime(SystemUtil.currentTimeMillis());
            uq.setDepth(depth);
            uq.setMethod(Constants.GET_METHOD);
            uq.setParentUrl(parentUrl);
            uq.setSessionId(crawlerContext.sessionId);
            uq.setUrl(childUrl);
            uq.setMetaData(metaData);
            childList.add(uq);
            urlQueueService.offerAll(crawlerContext.sessionId, childList);
        }
    }

    protected boolean isValid(final UrlQueue<?> urlQueue) {
        if (urlQueue == null) {
            return false;
        }

        if (StringUtil.isBlank(urlQueue.getUrl())) {
            return false;
        }

        if (crawlerContext.getMaxDepth() >= 0
                && urlQueue.getDepth() > crawlerContext.getMaxDepth()) {
            return false;
        }

        // url filter
        if (crawlerContext.urlFilter.match(urlQueue.getUrl())) {
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
