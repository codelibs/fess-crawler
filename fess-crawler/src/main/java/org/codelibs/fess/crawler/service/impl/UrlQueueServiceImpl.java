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
package org.codelibs.fess.crawler.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.SystemUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.AccessResultImpl;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.entity.UrlQueueImpl;
import org.codelibs.fess.crawler.helper.MemoryDataHelper;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class UrlQueueServiceImpl implements UrlQueueService<UrlQueueImpl<Long>> {

    private static final Logger logger = LoggerFactory
            .getLogger(UrlQueueServiceImpl.class);

    @Resource
    protected MemoryDataHelper dataHelper;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.service.UrlQueueService#updateSessionId(java.lang.String
     * , java.lang.String)
     */
    @Override
    public void updateSessionId(final String oldSessionId,
            final String newSessionId) {
        // not MT-safe
        final Queue<UrlQueueImpl<Long>> urlQueueList = dataHelper
                .getUrlQueueList(oldSessionId);
        // overwrite
        dataHelper.addUrlQueueList(newSessionId, urlQueueList);
        dataHelper.removeUrlQueueList(oldSessionId);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.service.UrlQueueService#add(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void add(final String sessionId, final String url) {
        final Queue<UrlQueueImpl<Long>> urlQueueList = dataHelper
                .getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
            urlQueue.setSessionId(sessionId);
            urlQueue.setMethod(Constants.GET_METHOD);
            urlQueue.setUrl(url);
            urlQueue.setUrl(url);
            urlQueue.setDepth(0);
            urlQueue.setCreateTime(SystemUtil.currentTimeMillis());
            urlQueueList.add(urlQueue);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.service.UrlQueueService#insert(org.codelibs.fess.crawler.entity
     * .UrlQueue)
     */
    @Override
    public void insert(final UrlQueueImpl<Long> urlQueue) {
        final Queue<UrlQueueImpl<Long>> urlQueueList = dataHelper
                .getUrlQueueList(urlQueue.getSessionId());
        synchronized (urlQueueList) {
            urlQueueList.add(urlQueue);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.service.UrlQueueService#delete(java.lang.String)
     */
    @Override
    public void delete(final String sessionId) {
        dataHelper.removeUrlQueueList(sessionId);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.service.UrlQueueService#deleteAll()
     */
    @Override
    public void deleteAll() {
        dataHelper.clearUrlQueueList();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.service.UrlQueueService#offerAll(java.lang.String,
     * java.util.List)
     */
    @Override
    public void offerAll(final String sessionId,
            final List<UrlQueueImpl<Long>> newUrlQueueList) {
        final Queue<UrlQueueImpl<Long>> urlQueueList = dataHelper
                .getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            final List<UrlQueueImpl<Long>> targetList = new ArrayList<>();
            for (final UrlQueueImpl<Long> urlQueue : newUrlQueueList) {
                if (isNewUrl(urlQueue, urlQueueList)) {
                    targetList.add(urlQueue);
                }
            }
            urlQueueList.addAll(targetList);
        }

    }

    protected boolean isNewUrl(final UrlQueueImpl<Long> urlQueue,
            final Queue<UrlQueueImpl<Long>> urlQueueList) {

        final String url = urlQueue.getUrl();
        if (StringUtil.isBlank(url)) {
            if (logger.isDebugEnabled()) {
                logger.debug("URL is a blank: " + url);
            }
            return false;
        }

        // check it in queue
        for (final UrlQueue<Long> urlInQueue : urlQueueList) {
            if (url.equals(urlInQueue.getUrl())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("URL exists in a queue: " + url);
                }
                return false;
            }
        }

        // check it in result
        final AccessResult<Long> accessResult = dataHelper.getAccessResultMap(
                urlQueue.getSessionId()).get(url);
        if (accessResult != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("URL exists in a result: " + url);
            }
            return false;
        }

        return true;

    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.service.UrlQueueService#poll(java.lang.String)
     */
    @Override
    public UrlQueueImpl<Long> poll(final String sessionId) {
        final Queue<UrlQueueImpl<Long>> urlQueueList = dataHelper
                .getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            return urlQueueList.poll();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.service.UrlQueueService#saveSession(java.lang.String)
     */
    @Override
    public void saveSession(final String sessionId) {
        // NOP
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.service.UrlQueueService#visited(UrlQueue)
     */
    @Override
    public boolean visited(final UrlQueueImpl<Long> urlQueue) {
        final Queue<UrlQueueImpl<Long>> urlQueueList = dataHelper
                .getUrlQueueList(urlQueue.getSessionId());
        synchronized (urlQueueList) {
            return !isNewUrl(urlQueue, urlQueueList);
        }
    }

    @Override
    public void generateUrlQueues(final String previousSessionId,
            final String sessionId) {
        final Queue<UrlQueueImpl<Long>> urlQueueList = dataHelper
                .getUrlQueueList(sessionId);
        final Map<String, AccessResultImpl<Long>> arMap = dataHelper
                .getAccessResultMap(previousSessionId);
        for (final Map.Entry<String, AccessResultImpl<Long>> entry : arMap.entrySet()) {
            synchronized (urlQueueList) {
                final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
                urlQueue.setSessionId(sessionId);
                urlQueue.setMethod(entry.getValue().getMethod());
                urlQueue.setUrl(entry.getValue().getUrl());
                urlQueue.setParentUrl(entry.getValue().getParentUrl());
                urlQueue.setDepth(0);
                urlQueue.setLastModified(entry.getValue().getLastModified());
                urlQueue.setCreateTime(SystemUtil.currentTimeMillis());
                urlQueueList.add(urlQueue);
            }
        }
    }
}
