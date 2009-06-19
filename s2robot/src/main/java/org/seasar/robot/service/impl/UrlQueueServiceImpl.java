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
package org.seasar.robot.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    private static final Logger logger = LoggerFactory
            .getLogger(UrlQueueServiceImpl.class);

    @Resource
    protected MemoryDataHelper dataHelper;

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#updateSessionId(java.lang.String, java.lang.String)
     */
    public void updateSessionId(String oldSessionId, String newSessionId) {
        // not MT-safe
        Queue<UrlQueue> urlQueueList = dataHelper.getUrlQueueList(oldSessionId);
        // overwrite
        dataHelper.addUrlQueueList(newSessionId, urlQueueList);
        dataHelper.removeUrlQueueList(oldSessionId);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#add(java.lang.String, java.lang.String)
     */
    public void add(String sessionId, String url) {
        Queue<UrlQueue> urlQueueList = dataHelper.getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            UrlQueue urlQueue = new UrlQueueImpl();
            urlQueue.setSessionId(sessionId);
            urlQueue.setMethod(Constants.GET_METHOD);
            urlQueue.setUrl(url);
            urlQueue.setDepth(0);
            urlQueue.setCreateTime(new Timestamp(new Date().getTime()));
            urlQueueList.add(urlQueue);
        }
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#insert(org.seasar.robot.entity.UrlQueue)
     */
    public void insert(UrlQueue urlQueue) {
        Queue<UrlQueue> urlQueueList = dataHelper.getUrlQueueList(urlQueue
                .getSessionId());
        synchronized (urlQueueList) {
            urlQueueList.add(urlQueue);
        }
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#delete(java.lang.String)
     */
    public void delete(String sessionId) {
        dataHelper.removeUrlQueueList(sessionId);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#deleteAll()
     */
    public void deleteAll() {
        dataHelper.clearUrlQueueList();
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#offerAll(java.lang.String, java.util.List)
     */
    public void offerAll(String sessionId, List<UrlQueue> newUrlQueueList) {
        Queue<UrlQueue> urlQueueList = dataHelper.getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            List<UrlQueueImpl> targetList = new ArrayList<UrlQueueImpl>();
            for (UrlQueue urlQueue : newUrlQueueList) {
                if (isNewUrl(urlQueue, urlQueueList)) {
                    targetList.add((UrlQueueImpl) urlQueue);
                }
            }
            urlQueueList.addAll(targetList);
        }

    }

    protected boolean isNewUrl(UrlQueue urlQueue, Queue<UrlQueue> urlQueueList) {

        String url = urlQueue.getUrl();
        if (StringUtil.isBlank(url)) {
            if (logger.isDebugEnabled()) {
                logger.debug("URL is a blank: " + url);
            }
            return false;
        }

        // check it in queue
        for (UrlQueue urlInQueue : urlQueueList) {
            if (url.equals(urlInQueue.getUrl())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("URL exists in a queue: " + url);
                }
                return false;
            }
        }

        // check it in result
        AccessResult accessResult = dataHelper.getAccessResultMap(
                urlQueue.getSessionId()).get(url);
        if (accessResult != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("URL exists in a result: " + url);
            }
            return false;
        }

        return true;

    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#poll(java.lang.String)
     */
    public UrlQueue poll(String sessionId) {
        Queue<UrlQueue> urlQueueList = dataHelper.getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            return urlQueueList.poll();
        }
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#saveSession(java.lang.String)
     */
    public void saveSession(String sessionId) {
        // NOP
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#visited(UrlQueue)
     */
    public boolean visited(UrlQueue urlQueue) {
        Queue<UrlQueue> urlQueueList = dataHelper.getUrlQueueList(urlQueue
                .getSessionId());
        synchronized (urlQueueList) {
            return !isNewUrl(urlQueue, urlQueueList);
        }
    }

}
