/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.robot.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.annotation.Resource;

import org.seasar.framework.util.StringUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.entity.AccessResult;
import org.seasar.robot.entity.UrlQueue;
import org.seasar.robot.entity.UrlQueueImpl;
import org.seasar.robot.helper.MemoryDataHelper;
import org.seasar.robot.service.UrlQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 * 
 */
public class UrlQueueServiceImpl implements UrlQueueService {

    private static final Logger logger = LoggerFactory // NOPMD
        .getLogger(UrlQueueServiceImpl.class);

    @Resource
    protected MemoryDataHelper dataHelper;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.service.UrlQueueService#updateSessionId(java.lang.String
     * , java.lang.String)
     */
    @Override
    public void updateSessionId(final String oldSessionId,
            final String newSessionId) {
        // not MT-safe
        final Queue<UrlQueue> urlQueueList =
            dataHelper.getUrlQueueList(oldSessionId);
        // overwrite
        dataHelper.addUrlQueueList(newSessionId, urlQueueList);
        dataHelper.removeUrlQueueList(oldSessionId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.service.UrlQueueService#add(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void add(final String sessionId, final String url) {
        final Queue<UrlQueue> urlQueueList =
            dataHelper.getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            final UrlQueue urlQueue = new UrlQueueImpl();
            urlQueue.setSessionId(sessionId);
            urlQueue.setMethod(Constants.GET_METHOD);
            urlQueue.setUrl(url);
            urlQueue.setDepth(0);
            urlQueue.setCreateTime(new Timestamp(new Date().getTime()));
            urlQueueList.add(urlQueue);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.service.UrlQueueService#insert(org.seasar.robot.entity
     * .UrlQueue)
     */
    @Override
    public void insert(final UrlQueue urlQueue) {
        final Queue<UrlQueue> urlQueueList =
            dataHelper.getUrlQueueList(urlQueue.getSessionId());
        synchronized (urlQueueList) {
            urlQueueList.add(urlQueue);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.service.UrlQueueService#delete(java.lang.String)
     */
    @Override
    public void delete(final String sessionId) {
        dataHelper.removeUrlQueueList(sessionId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.service.UrlQueueService#deleteAll()
     */
    @Override
    public void deleteAll() {
        dataHelper.clearUrlQueueList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.service.UrlQueueService#offerAll(java.lang.String,
     * java.util.List)
     */
    @Override
    public void offerAll(final String sessionId,
            final List<UrlQueue> newUrlQueueList) {
        final Queue<UrlQueue> urlQueueList =
            dataHelper.getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            final List<UrlQueueImpl> targetList = new ArrayList<UrlQueueImpl>();
            for (final UrlQueue urlQueue : newUrlQueueList) {
                if (isNewUrl(urlQueue, urlQueueList)) {
                    targetList.add((UrlQueueImpl) urlQueue);
                }
            }
            urlQueueList.addAll(targetList);
        }

    }

    protected boolean isNewUrl(final UrlQueue urlQueue,
            final Queue<UrlQueue> urlQueueList) {

        final String url = urlQueue.getUrl();
        if (StringUtil.isBlank(url)) {
            if (logger.isDebugEnabled()) {
                logger.debug("URL is a blank: " + url);
            }
            return false;
        }

        // check it in queue
        for (final UrlQueue urlInQueue : urlQueueList) {
            if (url.equals(urlInQueue.getUrl())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("URL exists in a queue: " + url);
                }
                return false;
            }
        }

        // check it in result
        final AccessResult accessResult =
            dataHelper.getAccessResultMap(urlQueue.getSessionId()).get(url);
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
     * @see org.seasar.robot.service.UrlQueueService#poll(java.lang.String)
     */
    @Override
    public UrlQueue poll(final String sessionId) {
        final Queue<UrlQueue> urlQueueList =
            dataHelper.getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            return urlQueueList.poll();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.service.UrlQueueService#saveSession(java.lang.String)
     */
    @Override
    public void saveSession(final String sessionId) {
        // NOP
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.service.UrlQueueService#visited(UrlQueue)
     */
    @Override
    public boolean visited(final UrlQueue urlQueue) {
        final Queue<UrlQueue> urlQueueList =
            dataHelper.getUrlQueueList(urlQueue.getSessionId());
        synchronized (urlQueueList) {
            return !isNewUrl(urlQueue, urlQueueList);
        }
    }

    @Override
    public void generateUrlQueues(final String previousSessionId,
            final String sessionId) {
        final Queue<UrlQueue> urlQueueList =
            dataHelper.getUrlQueueList(sessionId);
        final Map<String, AccessResult> arMap =
            dataHelper.getAccessResultMap(previousSessionId);
        for (final Map.Entry<String, AccessResult> entry : arMap.entrySet()) {
            synchronized (urlQueueList) {
                final UrlQueue urlQueue = new UrlQueueImpl();
                urlQueue.setSessionId(sessionId);
                urlQueue.setMethod(entry.getValue().getMethod());
                urlQueue.setUrl(entry.getValue().getUrl());
                urlQueue.setParentUrl(entry.getValue().getParentUrl());
                urlQueue.setDepth(0);
                urlQueue.setLastModified(entry.getValue().getLastModified());
                urlQueue.setCreateTime(new Timestamp(new Date().getTime()));
                urlQueueList.add(urlQueue);
            }
        }
    }
}
