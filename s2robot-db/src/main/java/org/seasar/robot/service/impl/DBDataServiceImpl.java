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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.seasar.framework.beans.util.Beans;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.db.bsbhv.BsAccessResultBhv;
import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.exbhv.AccessResultBhv;
import org.seasar.robot.db.exbhv.AccessResultDataBhv;
import org.seasar.robot.db.exbhv.cursor.AccessResultDiffCursor;
import org.seasar.robot.db.exbhv.cursor.AccessResultDiffCursorHandler;
import org.seasar.robot.db.exbhv.pmbean.AccessResultPmb;
import org.seasar.robot.dbflute.cbean.EntityRowHandler;
import org.seasar.robot.entity.AccessResult;
import org.seasar.robot.entity.AccessResultData;
import org.seasar.robot.service.DataService;
import org.seasar.robot.util.AccessResultCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 * 
 */
public class DBDataServiceImpl implements DataService {
    private static final Logger logger = LoggerFactory
        .getLogger(DBDataServiceImpl.class);

    @Resource
    protected AccessResultBhv accessResultBhv;

    @Resource
    protected AccessResultDataBhv accessResultDataBhv;

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.service.DataService#store(org.seasar.robot.entity.
     * AccessResult)
     */
    @Override
    public void store(final AccessResult accessResult) {
        if (accessResult == null) {
            throw new RobotSystemException("AccessResult is null.");
        }

        accessResultBhv
            .insert((org.seasar.robot.db.exentity.AccessResult) accessResult);

        AccessResultData accessResultData = accessResult.getAccessResultData();
        if (accessResultData == null) {
            accessResultData =
                new org.seasar.robot.db.exentity.AccessResultData();
            accessResultData.setTransformerName(Constants.NO_TRANSFORMER);
        }
        accessResultData.setId(accessResult.getId());
        accessResultDataBhv
            .insert((org.seasar.robot.db.exentity.AccessResultData) accessResultData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.service.DataService#getCount(java.lang.String)
     */
    @Override
    public int getCount(final String sessionId) {
        final AccessResultCB cb = new AccessResultCB();
        cb.query().setSessionId_Equal(sessionId);
        return accessResultBhv.selectCount(cb);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.service.DataService#delete(java.lang.String)
     */
    @Override
    public void delete(final String sessionId) {
        int count = accessResultDataBhv.deleteBySessionId(sessionId);

        if (logger.isDebugEnabled()) {
            logger.debug("Deleted accessResultData: " + count);
        }

        count = accessResultBhv.deleteBySessionId(sessionId);

        if (logger.isDebugEnabled()) {
            logger.debug("Deleted accessResult: " + count);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.service.DataService#deleteAll()
     */
    @Override
    public void deleteAll() {
        int count = accessResultDataBhv.deleteAll();

        if (logger.isDebugEnabled()) {
            logger.debug("Deleted accessResultData: " + count);
        }

        count = accessResultBhv.deleteAll();

        if (logger.isDebugEnabled()) {
            logger.debug("Deleted accessResult: " + count);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.service.DataService#getAccessResult(java.lang.String,
     * java.lang.String)
     */
    @Override
    public AccessResult getAccessResult(final String sessionId, final String url) {
        final AccessResultCB cb = new AccessResultCB();
        cb.setupSelect_AccessResultDataAsOne();
        cb.query().setSessionId_Equal(sessionId);
        cb.query().setUrl_Equal(url);
        return accessResultBhv.selectEntity(cb);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.service.DataService#getAccessResultList(java.lang.String
     * , boolean)
     */
    @Override
    public List<AccessResult> getAccessResultList(final String url,
            final boolean hasData) {
        final AccessResultCB cb = new AccessResultCB();
        if (hasData) {
            cb.setupSelect_AccessResultDataAsOne();
        }
        cb.query().setUrl_Equal(url);
        cb.query().addOrderBy_CreateTime_Desc();
        final List<org.seasar.robot.db.exentity.AccessResult> list =
            accessResultBhv.selectList(cb);

        final List<AccessResult> accessResultList =
            new ArrayList<AccessResult>();
        accessResultList.addAll(list);
        return accessResultList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.service.DataService#iterate(java.lang.String,
     * org.seasar.robot.util.AccessResultCallback)
     */
    @Override
    public void iterate(final String sessionId,
            final AccessResultCallback accessResultCallback) {
        final AccessResultCB cb = new AccessResultCB();
        cb.setupSelect_AccessResultDataAsOne();
        cb.query().setSessionId_Equal(sessionId);
        cb.query().addOrderBy_CreateTime_Asc();
        accessResultBhv.selectCursor(
            cb,
            new EntityRowHandler<org.seasar.robot.db.exentity.AccessResult>() {
                @Override
                public void handle(
                        final org.seasar.robot.db.exentity.AccessResult entity) {
                    accessResultCallback.iterate(entity);
                }
            });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.service.DataService#iterateUrlDiff(java.lang.String,
     * java.lang.String, org.seasar.robot.util.AccessResultCallback)
     */
    @Override
    public void iterateUrlDiff(final String oldSessionId,
            final String newSessionId,
            final AccessResultCallback accessResultCallback) {

        final AccessResultPmb pmb = new AccessResultPmb();
        pmb.setOldSessionId(oldSessionId);
        pmb.setNewSessionId(newSessionId);
        final AccessResultDiffCursorHandler handler =
            new AccessResultDiffCursorHandler() {
                @Override
                public Object fetchCursor(final AccessResultDiffCursor cursor)
                        throws SQLException {
                    while (cursor.next()) {
                        final AccessResult accessResult =
                            new org.seasar.robot.db.exentity.AccessResult();
                        Beans.copy(cursor, accessResult).execute();
                        accessResultCallback.iterate(accessResult);
                    }
                    return null;
                }
            };
        accessResultBhv
            .outsideSql()
            .cursorHandling()
            .selectCursor(
                BsAccessResultBhv.PATH_selectListByUrlDiff,
                pmb,
                handler);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.service.DataService#update(org.seasar.robot.entity.
     * AccessResult)
     */
    @Override
    public void update(final AccessResult accessResult) {
        accessResultBhv
            .update((org.seasar.robot.db.exentity.AccessResult) accessResult);

        if (accessResult.getAccessResultData() != null) {
            accessResultDataBhv
                .update((org.seasar.robot.db.exentity.AccessResultData) accessResult
                    .getAccessResultData());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.service.DataService#update(java.util.List)
     */
    @Override
    public void update(final List<AccessResult> accessResultList) {
        final List<org.seasar.robot.db.exentity.AccessResult> arList =
            new ArrayList<org.seasar.robot.db.exentity.AccessResult>();
        final List<org.seasar.robot.db.exentity.AccessResultData> ardList =
            new ArrayList<org.seasar.robot.db.exentity.AccessResultData>();

        for (final AccessResult accessResult : accessResultList) {
            arList
                .add((org.seasar.robot.db.exentity.AccessResult) accessResult);
            if (accessResult.getAccessResultData() != null) {
                ardList
                    .add((org.seasar.robot.db.exentity.AccessResultData) accessResult
                        .getAccessResultData());
            }
        }
        accessResultBhv.batchUpdate(arList);
        if (!ardList.isEmpty()) {
            accessResultDataBhv.batchUpdate(ardList);
        }

        for (final AccessResult accessResult : accessResultList) {
            update(accessResult);
        }
    }
}
