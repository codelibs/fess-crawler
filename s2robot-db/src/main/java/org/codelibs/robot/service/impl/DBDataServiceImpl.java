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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.robot.Constants;
import org.codelibs.robot.db.exbhv.AccessResultBhv;
import org.codelibs.robot.db.exbhv.AccessResultDataBhv;
import org.codelibs.robot.db.exbhv.cursor.AccessResultDiffCursor;
import org.codelibs.robot.db.exbhv.cursor.AccessResultDiffCursorHandler;
import org.codelibs.robot.db.exbhv.pmbean.AccessResultListByUrlDiffPmb;
import org.codelibs.robot.dbflute.cbean.result.ListResultBean;
import org.codelibs.robot.entity.AccessResult;
import org.codelibs.robot.entity.AccessResultData;
import org.codelibs.robot.exception.RobotSystemException;
import org.codelibs.robot.service.DataService;
import org.codelibs.robot.util.AccessResultCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

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
     * @see org.codelibs.robot.service.DataService#store(org.codelibs.robot.entity.AccessResult)
     */
    @Override
    @Transactional("robotTx")
    public void store(final AccessResult accessResult) {
        if (accessResult == null) {
            throw new RobotSystemException("AccessResult is null.");
        }

        accessResultBhv
                .insert((org.codelibs.robot.db.exentity.AccessResult) accessResult);

        AccessResultData accessResultData = accessResult.getAccessResultData();
        if (accessResultData == null) {
            accessResultData = new org.codelibs.robot.db.exentity.AccessResultData();
            accessResultData.setTransformerName(Constants.NO_TRANSFORMER);
        }
        accessResultData.setId(accessResult.getId());
        accessResultDataBhv
                .insert((org.codelibs.robot.db.exentity.AccessResultData) accessResultData);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.service.DataService#getCount(java.lang.String)
     */
    @Override
    @Transactional("robotTx")
    public int getCount(final String sessionId) {
        return accessResultBhv.selectCount(cb -> {
            cb.query().setSessionId_Equal(sessionId);
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.service.DataService#delete(java.lang.String)
     */
    @Override
    @Transactional("robotTx")
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
     * @see org.codelibs.robot.service.DataService#deleteAll()
     */
    @Override
    @Transactional("robotTx")
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
     * @see org.codelibs.robot.service.DataService#getAccessResult(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional("robotTx")
    public AccessResult getAccessResult(final String sessionId, final String url) {
        final ListResultBean<org.codelibs.robot.db.exentity.AccessResult> list = accessResultBhv
                .selectList(cb -> {
                    cb.setupSelect_AccessResultDataAsOne();
                    cb.query().setSessionId_Equal(sessionId);
                    cb.query().setUrl_Equal(url);
                });
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.service.DataService#getAccessResultList(java.lang.String, boolean)
     */
    @Override
    @Transactional("robotTx")
    public List<AccessResult> getAccessResultList(final String url,
            final boolean hasData) {
        final List<org.codelibs.robot.db.exentity.AccessResult> list = accessResultBhv
                .selectList(cb -> {
                    if (hasData) {
                        cb.setupSelect_AccessResultDataAsOne();
                    }
                    cb.query().setUrl_Equal(url);
                    cb.query().addOrderBy_CreateTime_Desc();
                });

        final List<AccessResult> accessResultList = new ArrayList<>();
        accessResultList.addAll(list);
        return accessResultList;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.service.DataService#iterate(java.lang.String, org.codelibs.robot.util.AccessResultCallback)
     */
    @Override
    @Transactional("robotTx")
    public void iterate(final String sessionId,
            final AccessResultCallback accessResultCallback) {
        accessResultBhv.selectCursor(cb -> {
            cb.setupSelect_AccessResultDataAsOne();
            cb.query().setSessionId_Equal(sessionId);
            cb.query().addOrderBy_CreateTime_Asc();
        }, entity -> accessResultCallback.iterate(entity));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.service.DataService#iterateUrlDiff(java.lang.String, java.lang.String, org.codelibs.robot.util.AccessResultCallback)
     */
    @Override
    @Transactional("robotTx")
    public void iterateUrlDiff(final String oldSessionId,
            final String newSessionId,
            final AccessResultCallback accessResultCallback) {

        final AccessResultListByUrlDiffPmb pmb = new AccessResultListByUrlDiffPmb();
        pmb.setOldSessionId(oldSessionId);
        pmb.setNewSessionId(newSessionId);
        final AccessResultDiffCursorHandler handler = new AccessResultDiffCursorHandler() {
            @Override
            public Object fetchCursor(final AccessResultDiffCursor cursor)
                    throws SQLException {
                while (cursor.next()) {
                    final AccessResult accessResult = new org.codelibs.robot.db.exentity.AccessResult();
                    BeanUtil.copyBeanToBean(cursor, accessResult);
                    accessResultCallback.iterate(accessResult);
                }
                return null;
            }
        };
        accessResultBhv.outsideSql().selectCursor(pmb, handler);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.service.DataService#update(org.codelibs.robot.entity.AccessResult)
     */
    @Override
    @Transactional("robotTx")
    public void update(final AccessResult accessResult) {
        accessResultBhv
                .update((org.codelibs.robot.db.exentity.AccessResult) accessResult);

        if (accessResult.getAccessResultData() != null) {
            accessResultDataBhv
                    .update((org.codelibs.robot.db.exentity.AccessResultData) accessResult
                            .getAccessResultData());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.service.DataService#update(java.util.List)
     */
    @Override
    @Transactional("robotTx")
    public void update(final List<AccessResult> accessResultList) {
        final List<org.codelibs.robot.db.exentity.AccessResult> arList = new ArrayList<>();
        final List<org.codelibs.robot.db.exentity.AccessResultData> ardList = new ArrayList<>();

        for (final AccessResult accessResult : accessResultList) {
            arList.add((org.codelibs.robot.db.exentity.AccessResult) accessResult);
            if (accessResult.getAccessResultData() != null) {
                ardList.add((org.codelibs.robot.db.exentity.AccessResultData) accessResult
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
