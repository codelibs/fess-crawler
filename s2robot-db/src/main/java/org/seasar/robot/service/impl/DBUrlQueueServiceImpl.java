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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.seasar.framework.util.LruHashMap;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.cbean.UrlQueueCB;
import org.seasar.robot.db.exbhv.AccessResultBhv;
import org.seasar.robot.db.exbhv.UrlQueueBhv;
import org.seasar.robot.dbflute.cbean.EntityRowHandler;
import org.seasar.robot.entity.UrlQueue;
import org.seasar.robot.service.UrlQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class DBUrlQueueServiceImpl implements UrlQueueService {

    private static final String EMPTY_STRING = "";

    private static final Logger logger = LoggerFactory
            .getLogger(DBUrlQueueServiceImpl.class);

    protected static volatile Map<String, LinkedList<UrlQueue>> URL_QUEUE_MAP = new HashMap<String, LinkedList<UrlQueue>>();

    private static Map<String, LruHashMap> VISITED_URL_CACHE_MAP = new ConcurrentHashMap<String, LruHashMap>();

    public int cacheSize = 1000;

    public int visitedUrlCacheSize = 1000;

    @Resource
    protected UrlQueueBhv urlQueueBhv;

    @Resource
    protected AccessResultBhv accessResultBhv;

    private LinkedList<UrlQueue> getUrlQueueList(String sessionId) {
        LinkedList<UrlQueue> urlQueueList = URL_QUEUE_MAP.get(sessionId);
        if (urlQueueList == null) {
            synchronized (URL_QUEUE_MAP) {
                urlQueueList = URL_QUEUE_MAP.get(sessionId);
                if (urlQueueList == null) {
                    urlQueueList = new LinkedList<UrlQueue>();
                    URL_QUEUE_MAP.put(sessionId, urlQueueList);
                }
            }
        }
        return urlQueueList;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#updateSessionId(java.lang.String, java.lang.String)
     */
    public void updateSessionId(String oldSessionId, String newSessionId) {
        // not MT-safe
        LinkedList<UrlQueue> urlQueueList = getUrlQueueList(oldSessionId);
        // overwrite
        URL_QUEUE_MAP.put(newSessionId, urlQueueList);
        URL_QUEUE_MAP.remove(oldSessionId);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#add(java.lang.String, java.lang.String)
     */
    public void add(String sessionId, String url) {
        LinkedList<UrlQueue> urlQueueList = getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            UrlQueue urlQueue = new org.seasar.robot.db.exentity.UrlQueue();
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
        urlQueueBhv.insert((org.seasar.robot.db.exentity.UrlQueue) urlQueue);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#delete(java.lang.String)
     */
    public void delete(String sessionId) {
        //        UrlQueueCB cb = new UrlQueueCB();
        //        cb.query().setSessionId_Equal(sessionId);
        //        urlQueueBhv.queryDelete(cb);
        String pmb = sessionId;
        int count = urlQueueBhv.outsideSql().execute(
                UrlQueueBhv.PATH_deleteBySessionId, pmb);

        if (logger.isDebugEnabled()) {
            logger.debug("Deleted urls in queue: " + count);
        }

        synchronized (URL_QUEUE_MAP) { // clear cache
            URL_QUEUE_MAP.remove(sessionId);
            VISITED_URL_CACHE_MAP.remove(sessionId);
        }
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#deleteAll()
     */
    public void deleteAll() {
        //        UrlQueueCB cb = new UrlQueueCB();
        //        urlQueueBhv.queryDelete(cb);
        String pmb = null;
        int count = urlQueueBhv.outsideSql().execute(
                UrlQueueBhv.PATH_deleteBySessionId, pmb);

        if (logger.isDebugEnabled()) {
            logger.debug("Deleted urls in queue: " + count);
        }

        synchronized (URL_QUEUE_MAP) { // clear cache
            URL_QUEUE_MAP.clear();
            VISITED_URL_CACHE_MAP.clear();
        }
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#offerAll(java.lang.String, java.util.List)
     */
    public void offerAll(String sessionId, List<UrlQueue> newUrlQueueList) {
        LinkedList<UrlQueue> urlQueueList = getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            List<org.seasar.robot.db.exentity.UrlQueue> targetList = new ArrayList<org.seasar.robot.db.exentity.UrlQueue>();
            for (UrlQueue urlQueue : newUrlQueueList) {
                if (isNewUrl(urlQueue, urlQueueList, true)) {
                    targetList
                            .add((org.seasar.robot.db.exentity.UrlQueue) urlQueue);
                }
            }
            urlQueueBhv.batchInsert(targetList);
        }
    }

    private LruHashMap getVisitedUrlCache(String sessionId) {
        LruHashMap visitedUrlMap = VISITED_URL_CACHE_MAP.get(sessionId);
        if (visitedUrlMap == null) {
            visitedUrlMap = new LruHashMap(visitedUrlCacheSize);
            VISITED_URL_CACHE_MAP.put(sessionId, visitedUrlMap);
        }
        return visitedUrlMap;
    }

    protected boolean isNewUrl(UrlQueue urlQueue, List<UrlQueue> urlQueueList,
            boolean cache) {

        String url = urlQueue.getUrl();
        if (StringUtil.isBlank(url)) {
            if (logger.isDebugEnabled()) {
                logger.debug("URL is a blank: " + url);
            }
            return false;
        }

        if (cache) {
            String sessionId = urlQueue.getSessionId();
            // cache
            if (getVisitedUrlCache(sessionId).containsKey(url)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("URL exists in a cache: " + url);
                }
                return false;
            }
            getVisitedUrlCache(sessionId).put(urlQueue.getUrl(), EMPTY_STRING);
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

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#poll(java.lang.String)
     */
    public UrlQueue poll(String sessionId) {
        LinkedList<UrlQueue> urlQueueList = getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            if (urlQueueList.isEmpty()) {
                UrlQueueCB cb = new UrlQueueCB();
                cb.paging(cacheSize, 1);
                cb.query().setSessionId_Equal(sessionId);
                List<org.seasar.robot.db.exentity.UrlQueue> uqList = urlQueueBhv
                        .selectPage(cb);
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

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#saveSession(java.lang.String)
     */
    public void saveSession(String sessionId) {
        LinkedList<UrlQueue> urlQueueList = getUrlQueueList(sessionId);
        synchronized (urlQueueList) {
            List<org.seasar.robot.db.exentity.UrlQueue> targetUrlQueueList = new ArrayList<org.seasar.robot.db.exentity.UrlQueue>();
            for (UrlQueue urlQueue : urlQueueList) {
                // clear id
                urlQueue.setId(null);
                targetUrlQueueList
                        .add((org.seasar.robot.db.exentity.UrlQueue) urlQueue);
            }
            urlQueueBhv.batchInsert(targetUrlQueueList);
            urlQueueList.clear();
        }
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#visited(UrlQueue)
     */
    public boolean visited(UrlQueue urlQueue) {
        LinkedList<UrlQueue> urlQueueList = getUrlQueueList(urlQueue
                .getSessionId());
        synchronized (urlQueueList) {
            return !isNewUrl(urlQueue, urlQueueList, false);
        }
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.UrlQueueService#generateUrlQueues(java.lang.String, java.lang.String)
     */
    public void generateUrlQueues(String previousSessionId,
            final String sessionId) {
        AccessResultCB cb = new AccessResultCB();
        cb.query().setSessionId_Equal(previousSessionId);
        cb.query().addOrderBy_CreateTime_Asc();
        accessResultBhv
                .selectCursor(
                        cb,
                        new EntityRowHandler<org.seasar.robot.db.exentity.AccessResult>() {
                            public void handle(
                                    org.seasar.robot.db.exentity.AccessResult entity) {
                                org.seasar.robot.db.exentity.UrlQueue urlQueue = new org.seasar.robot.db.exentity.UrlQueue();
                                urlQueue.setSessionId(sessionId);
                                urlQueue.setMethod(entity.getMethod());
                                urlQueue.setUrl(entity.getUrl());
                                urlQueue.setParentUrl(entity.getParentUrl());
                                urlQueue.setDepth(0);
                                urlQueue.setLastModified(entity
                                        .getLastModified());
                                urlQueue.setCreateTime(new Timestamp(new Date()
                                        .getTime()));
                                urlQueueBhv.insert(urlQueue);
                            }
                        });
    }
}
