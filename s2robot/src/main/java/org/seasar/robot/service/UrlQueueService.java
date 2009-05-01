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
package org.seasar.robot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.seasar.framework.util.LruHashMap;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.cbean.UrlQueueCB;
import org.seasar.robot.db.exbhv.AccessResultBhv;
import org.seasar.robot.db.exbhv.UrlQueueBhv;
import org.seasar.robot.db.exentity.UrlQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class UrlQueueService {

    private static final Logger logger = LoggerFactory
            .getLogger(UrlQueueService.class);

    public int cacheSize = 100;

    protected Map<String, LinkedList<UrlQueue>> urlQueueMap;

    public int visitedUrlCacheSize = 1000;

    private LruHashMap visitedUrlCache;

    @Resource
    protected UrlQueueBhv urlQueueBhv;

    @Resource
    protected AccessResultBhv accessResultBhv;

    public UrlQueueService() {
        urlQueueMap = new HashMap<String, LinkedList<UrlQueue>>();
    }

    private LinkedList<UrlQueue> getUrlQueueList(String sessionId) {
        LinkedList<UrlQueue> urlQueueList = urlQueueMap.get(sessionId);
        if (urlQueueList == null) {
            urlQueueList = new LinkedList<UrlQueue>();
            urlQueueMap.put(sessionId, urlQueueList);
        }
        return urlQueueList;
    }

    public void updateSessionId(String oldSessionId, String newSessionId) {
        // not MT-safe
        LinkedList<UrlQueue> urlQueueList = getUrlQueueList(oldSessionId);
        // overwrite
        urlQueueMap.put(newSessionId, urlQueueList);
        urlQueueMap.remove(oldSessionId);
    }

    public void add(String sessionId, String url) {
        LinkedList<UrlQueue> urlQueueList = getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            UrlQueue urlQueue = new UrlQueue();
            urlQueue.setMethod(Constants.GET_METHOD);
            urlQueue.setUrl(url);
            urlQueue.setDepth(0);
            urlQueueList.add(urlQueue);
        }
    }

    public void insert(UrlQueue urlQueue) {
        urlQueueBhv.insert(urlQueue);
    }

    public void delete(String sessionId) {
        UrlQueueCB cb = new UrlQueueCB();
        cb.query().setSessionId_Equal(sessionId);
        urlQueueBhv.queryDelete(cb);
    }

    public void deleteAll() {
        UrlQueueCB cb = new UrlQueueCB();
        urlQueueBhv.queryDelete(cb);
    }

    public void offerAll(String sessionId, List<UrlQueue> newUrlQueueList) {
        LinkedList<UrlQueue> urlQueueList = getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            List<UrlQueue> targetList = new ArrayList<UrlQueue>();
            for (UrlQueue urlQueue : newUrlQueueList) {
                if (isNewUrl(urlQueue, urlQueueList)) {
                    targetList.add(urlQueue);
                }
            }
            urlQueueBhv.batchInsert(targetList);
        }
    }

    private LruHashMap getVisitedUrlCache() {
        if (visitedUrlCache == null) {
            visitedUrlCache = new LruHashMap(visitedUrlCacheSize);
        }
        return visitedUrlCache;
    }

    protected boolean isNewUrl(UrlQueue urlQueue, List<UrlQueue> urlQueueList) {

        String url = urlQueue.getUrl();
        if (StringUtil.isBlank(url)) {
            if (logger.isDebugEnabled()) {
                logger.debug("URL is a blank: " + url);
            }
            return false;
        }

        // cache
        if (getVisitedUrlCache().containsKey(url)) {
            if (logger.isDebugEnabled()) {
                logger.debug("URL exists in a cache: " + url);
            }
            return false;
        }

        getVisitedUrlCache().put(url, "");

        // check it in queue
        for (UrlQueue urlInQueue : urlQueueList) {
            if (url.equals(urlInQueue.getUrl())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("URL exists in a queue: " + url);
                }
                return false;
            }
        }

        // check it in queue db
        UrlQueueCB cb1 = new UrlQueueCB();
        cb1.query().setUrl_Equal(url);
        cb1.query().setSessionId_Equal(urlQueue.getSessionId());
        int count1 = urlQueueBhv.selectCount(cb1);
        if (count1 > 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("URL exists in a queue db: " + url);
            }
            return false;
        }

        // check it in result
        AccessResultCB cb2 = new AccessResultCB();
        cb2.query().setUrl_Equal(url);
        cb2.query().setSessionId_Equal(urlQueue.getSessionId());
        int count2 = accessResultBhv.selectCount(cb2);
        if (count2 > 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("URL exists in a result: " + url);
            }
            return false;
        }

        return true;

    }

    public UrlQueue poll(String sessionId) {
        LinkedList<UrlQueue> urlQueueList = getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            if (urlQueueList.isEmpty()) {
                UrlQueueCB cb = new UrlQueueCB();
                cb.paging(cacheSize, 1);
                cb.query().setSessionId_Equal(sessionId);
                List<UrlQueue> uqList = urlQueueBhv.selectPage(cb);
                if (!uqList.isEmpty()) {
                    urlQueueList.addAll(uqList);

                    List<Long> idList = new ArrayList<Long>(cacheSize);
                    for (UrlQueue uq : uqList) {
                        idList.add(uq.getId());
                    }
                    cb = new UrlQueueCB();
                    cb.query().setId_InScope(idList);
                    urlQueueBhv.queryDelete(cb);
                }
            }

            return urlQueueList.poll();
        }
    }
}
