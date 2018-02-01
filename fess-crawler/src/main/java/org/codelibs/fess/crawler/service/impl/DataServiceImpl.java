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

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.AccessResultDataImpl;
import org.codelibs.fess.crawler.entity.AccessResultImpl;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.helper.MemoryDataHelper;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.crawler.util.AccessResultCallback;

/**
 * @author shinsuke
 *
 */
public class DataServiceImpl implements DataService<AccessResultImpl<Long>> {

    protected static volatile long idCount = 0L;

    private static Object idCountLock = new Object();

    @Resource
    protected MemoryDataHelper dataHelper;

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.service.DataService#store(org.codelibs.fess.crawler.entity.
     * AccessResult)
     */
    @Override
    public void store(final AccessResultImpl<Long> accessResult) {
        if (accessResult == null) {
            throw new CrawlerSystemException("AccessResult is null.");
        }

        synchronized (idCountLock) {
            idCount++;
            accessResult.setId(idCount);
            AccessResultData<Long> accessResultData = accessResult
                    .getAccessResultData();
            if (accessResultData == null) {
                accessResultData = new AccessResultDataImpl<>();
                accessResultData.setTransformerName(Constants.NO_TRANSFORMER);
                accessResult.setAccessResultData(accessResultData);
            }
            accessResultData.setId(accessResult.getId());

            final Map<String, AccessResultImpl<Long>> arMap = dataHelper
                    .getAccessResultMap(accessResult.getSessionId());
            if (arMap.containsKey(accessResult.getUrl())) {
                throw new CrawlerSystemException(accessResult.getUrl()
                        + " already exists.");
            }
            arMap.put(accessResult.getUrl(), accessResult);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.service.DataService#getCount(java.lang.String)
     */
    @Override
    public int getCount(final String sessionId) {
        return dataHelper.getAccessResultMap(sessionId).size();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.service.DataService#delete(java.lang.String)
     */
    @Override
    public void delete(final String sessionId) {
        dataHelper.deleteAccessResultMap(sessionId);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.service.DataService#deleteAll()
     */
    @Override
    public void deleteAll() {
        dataHelper.clearUrlQueueList();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.service.DataService#getAccessResult(java.lang.String,
     * java.lang.String)
     */
    @Override
    public AccessResultImpl<Long> getAccessResult(final String sessionId, final String url) {
        return dataHelper.getAccessResultMap(sessionId).get(url);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.service.DataService#getAccessResultList(java.lang.String
     * , boolean)
     */
    @Override
    public List<AccessResultImpl<Long>> getAccessResultList(final String url,
            final boolean hasData) {
        return dataHelper.getAccessResultList(url);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.service.DataService#iterate(java.lang.String,
     * org.codelibs.fess.crawler.util.AccessResultCallback)
     */
    @Override
    public void iterate(final String sessionId,
            final AccessResultCallback<AccessResultImpl<Long>> accessResultCallback) {
        final Map<String, AccessResultImpl<Long>> arMap = dataHelper
                .getAccessResultMap(sessionId);
        for (final Map.Entry<String, AccessResultImpl<Long>> entry : arMap.entrySet()) {
            accessResultCallback.iterate(entry.getValue());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.service.DataService#update(org.codelibs.fess.crawler.entity.
     * AccessResult)
     */
    @Override
    public void update(final AccessResultImpl<Long> accessResult) {
        final Map<String, AccessResultImpl<Long>> arMap = dataHelper
                .getAccessResultMap(accessResult.getSessionId());
        if (!arMap.containsKey(accessResult.getUrl())) {
            throw new CrawlerSystemException(accessResult.getUrl()
                    + " is not found.");
        }
        arMap.put(accessResult.getUrl(), accessResult);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.service.DataService#update(java.util.List)
     */
    @Override
    public void update(final List<AccessResultImpl<Long>> accessResultList) {
        for (final AccessResultImpl<Long> accessResult : accessResultList) {
            update(accessResult);
        }
    }
}
