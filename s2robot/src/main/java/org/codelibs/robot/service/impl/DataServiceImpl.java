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
package org.codelibs.robot.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.robot.Constants;
import org.codelibs.robot.entity.AccessResult;
import org.codelibs.robot.entity.AccessResultData;
import org.codelibs.robot.entity.AccessResultDataImpl;
import org.codelibs.robot.exception.RobotSystemException;
import org.codelibs.robot.helper.MemoryDataHelper;
import org.codelibs.robot.service.DataService;
import org.codelibs.robot.util.AccessResultCallback;

/**
 * @author shinsuke
 *
 */
public class DataServiceImpl implements DataService {

    protected static volatile long idCount = 0L; // NOPMD

    private static Object idCountLock = new Object();

    @Resource
    protected MemoryDataHelper dataHelper;

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.service.DataService#store(org.codelibs.robot.entity.
     * AccessResult)
     */
    @Override
    public void store(final AccessResult accessResult) {
        if (accessResult == null) {
            throw new RobotSystemException("AccessResult is null.");
        }

        synchronized (idCountLock) {
            idCount++;
            accessResult.setId(idCount);
            AccessResultData accessResultData = accessResult
                    .getAccessResultData();
            if (accessResultData == null) {
                accessResultData = new AccessResultDataImpl();
                accessResultData.setTransformerName(Constants.NO_TRANSFORMER);
                accessResult.setAccessResultData(accessResultData);
            }
            accessResultData.setId(accessResult.getId());

            final Map<String, AccessResult> arMap = dataHelper
                    .getAccessResultMap(accessResult.getSessionId());
            if (arMap.containsKey(accessResult.getUrl())) {
                throw new RobotSystemException(accessResult.getUrl()
                        + " already exists.");
            }
            arMap.put(accessResult.getUrl(), accessResult);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.service.DataService#getCount(java.lang.String)
     */
    @Override
    public int getCount(final String sessionId) {
        return dataHelper.getAccessResultMap(sessionId).size();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.service.DataService#delete(java.lang.String)
     */
    @Override
    public void delete(final String sessionId) {
        dataHelper.deleteAccessResultMap(sessionId);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.service.DataService#deleteAll()
     */
    @Override
    public void deleteAll() {
        dataHelper.clearUrlQueueList();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.robot.service.DataService#getAccessResult(java.lang.String,
     * java.lang.String)
     */
    @Override
    public AccessResult getAccessResult(final String sessionId, final String url) {
        return dataHelper.getAccessResultMap(sessionId).get(url);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.robot.service.DataService#getAccessResultList(java.lang.String
     * , boolean)
     */
    @Override
    public List<AccessResult> getAccessResultList(final String url,
            final boolean hasData) {
        return dataHelper.getAccessResultList(url);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.service.DataService#iterate(java.lang.String,
     * org.codelibs.robot.util.AccessResultCallback)
     */
    @Override
    public void iterate(final String sessionId,
            final AccessResultCallback accessResultCallback) {
        final Map<String, AccessResult> arMap = dataHelper
                .getAccessResultMap(sessionId);
        for (final Map.Entry<String, AccessResult> entry : arMap.entrySet()) {
            accessResultCallback.iterate(entry.getValue());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.robot.service.DataService#iterateUrlDiff(java.lang.String,
     * java.lang.String, org.codelibs.robot.util.AccessResultCallback)
     */
    @Override
    public void iterateUrlDiff(final String oldSessionId,
            final String newSessionId,
            final AccessResultCallback accessResultCallback) {
        final Map<String, AccessResult> oldAccessResultMap = dataHelper
                .getAccessResultMap(oldSessionId);
        final Map<String, AccessResult> newAccessResultMap = dataHelper
                .getAccessResultMap(newSessionId);
        for (final Map.Entry<String, AccessResult> newEntry : newAccessResultMap
                .entrySet()) {
            if (!oldAccessResultMap.keySet().contains(newEntry.getKey())) {
                accessResultCallback.iterate(newEntry.getValue());
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.service.DataService#update(org.codelibs.robot.entity.
     * AccessResult)
     */
    @Override
    public void update(final AccessResult accessResult) {
        final Map<String, AccessResult> arMap = dataHelper
                .getAccessResultMap(accessResult.getSessionId());
        if (!arMap.containsKey(accessResult.getUrl())) {
            throw new RobotSystemException(accessResult.getUrl()
                    + " is not found.");
        }
        arMap.put(accessResult.getUrl(), accessResult);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.service.DataService#update(java.util.List)
     */
    @Override
    public void update(final List<AccessResult> accessResultList) {
        for (final AccessResult accessResult : accessResultList) {
            update(accessResult);
        }
    }
}
