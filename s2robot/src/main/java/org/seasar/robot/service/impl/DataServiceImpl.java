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

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.AccessResult;
import org.seasar.robot.entity.AccessResultData;
import org.seasar.robot.entity.AccessResultDataImpl;
import org.seasar.robot.helper.MemoryDataHelper;
import org.seasar.robot.service.DataService;
import org.seasar.robot.util.AccessResultCallback;

/**
 * @author shinsuke
 *
 */
public class DataServiceImpl implements DataService {

    protected static volatile long idCount = 0L; // NOPMD

    private static Object idCountLock = new Object();

    @Resource
    protected MemoryDataHelper dataHelper;

    public DataServiceImpl() {
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#store(org.seasar.robot.entity.AccessResult)
     */
    public void store(AccessResult accessResult) {
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

            Map<String, AccessResult> arMap = dataHelper
                    .getAccessResultMap(accessResult.getSessionId());
            if (arMap.containsKey(accessResult.getUrl())) {
                throw new RobotSystemException(accessResult.getUrl()
                        + " already exists.");
            }
            arMap.put(accessResult.getUrl(), accessResult);
        }

    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#getCount(java.lang.String)
     */
    public int getCount(String sessionId) {
        return dataHelper.getAccessResultMap(sessionId).size();
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#delete(java.lang.String)
     */
    public void delete(String sessionId) {
        dataHelper.deleteAccessResultMap(sessionId);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#deleteAll()
     */
    public void deleteAll() {
        dataHelper.clearUrlQueueList();
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#getAccessResult(java.lang.String, java.lang.String)
     */
    public AccessResult getAccessResult(String sessionId, String url) {
        return dataHelper.getAccessResultMap(sessionId).get(url);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#getAccessResultList(java.lang.String, boolean)
     */
    public List<AccessResult> getAccessResultList(String url, boolean hasData) {
        return dataHelper.getAccessResultList(url);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#iterate(java.lang.String, org.seasar.robot.util.AccessResultCallback)
     */
    public void iterate(String sessionId,
            final AccessResultCallback accessResultCallback) {
        Map<String, AccessResult> arMap = dataHelper
                .getAccessResultMap(sessionId);
        for (Map.Entry<String, AccessResult> entry : arMap.entrySet()) {
            accessResultCallback.iterate(entry.getValue());
        }
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#iterateUrlDiff(java.lang.String, java.lang.String, org.seasar.robot.util.AccessResultCallback)
     */
    public void iterateUrlDiff(String oldSessionId, String newSessionId,
            AccessResultCallback accessResultCallback) {
        Map<String, AccessResult> oldAccessResultMap = dataHelper
                .getAccessResultMap(oldSessionId);
        Map<String, AccessResult> newAccessResultMap = dataHelper
                .getAccessResultMap(newSessionId);
        for (Map.Entry<String, AccessResult> newEntry : newAccessResultMap
                .entrySet()) {
            if (!oldAccessResultMap.keySet().contains(newEntry.getKey())) {
                accessResultCallback.iterate(newEntry.getValue());
            }
        }
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#update(org.seasar.robot.entity.AccessResult)
     */
    public void update(AccessResult accessResult) {
        Map<String, AccessResult> arMap = dataHelper
                .getAccessResultMap(accessResult.getSessionId());
        if (!arMap.containsKey(accessResult.getUrl())) {
            throw new RobotSystemException(accessResult.getUrl()
                    + " is not found.");
        }
        arMap.put(accessResult.getUrl(), accessResult);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#update(java.util.List)
     */
    public void update(List<AccessResult> accessResultList) {
        for (AccessResult accessResult : accessResultList) {
            update(accessResult);
        }
    }
}
