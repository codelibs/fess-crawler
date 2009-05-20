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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.seasar.dbflute.cbean.EntityRowHandler;
import org.seasar.framework.beans.util.Beans;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.cbean.AccessResultDataCB;
import org.seasar.robot.db.exbhv.AccessResultBhv;
import org.seasar.robot.db.exbhv.AccessResultDataBhv;
import org.seasar.robot.db.exbhv.cursor.AccessResultDiffCursor;
import org.seasar.robot.db.exbhv.cursor.AccessResultDiffCursorHandler;
import org.seasar.robot.db.exbhv.pmbean.AccessResultPmb;
import org.seasar.robot.entity.AccessResult;
import org.seasar.robot.entity.AccessResultData;
import org.seasar.robot.service.DataService;
import org.seasar.robot.util.AccessResultCallback;

/**
 * @author shinsuke
 *
 */
public class DBDataServiceImpl implements DataService {

    @Resource
    protected AccessResultBhv accessResultBhv;

    @Resource
    protected AccessResultDataBhv accessResultDataBhv;

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#store(org.seasar.robot.entity.AccessResult)
     */
    public void store(AccessResult accessResult) {
        if (accessResult == null) {
            throw new RobotSystemException("AccessResult is null.");
        }

        accessResultBhv
                .insert((org.seasar.robot.db.exentity.AccessResult) accessResult);

        AccessResultData accessResultData = accessResult.getAccessResultData();
        if (accessResultData == null) {
            accessResultData = new org.seasar.robot.db.exentity.AccessResultData();
            accessResultData.setTransformerName(Constants.NO_TRANSFORMER);
        }
        accessResultData.setId(accessResult.getId());
        accessResultDataBhv
                .insert((org.seasar.robot.db.exentity.AccessResultData) accessResultData);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#getCount(java.lang.String)
     */
    public int getCount(String sessionId) {
        AccessResultCB cb = new AccessResultCB();
        cb.query().setSessionId_Equal(sessionId);
        return accessResultBhv.selectCount(cb);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#delete(java.lang.String)
     */
    public void delete(String sessionId) {
        AccessResultDataCB cb1 = new AccessResultDataCB();
        cb1.query().queryAccessResult().setSessionId_Equal(sessionId);
        accessResultDataBhv.queryDelete(cb1);

        AccessResultCB cb2 = new AccessResultCB();
        cb2.query().setSessionId_Equal(sessionId);
        accessResultBhv.queryDelete(cb2);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#deleteAll()
     */
    public void deleteAll() {
        AccessResultDataCB cb1 = new AccessResultDataCB();
        accessResultDataBhv.queryDelete(cb1);

        AccessResultCB cb2 = new AccessResultCB();
        accessResultBhv.queryDelete(cb2);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#getAccessResult(java.lang.String, java.lang.String)
     */
    public AccessResult getAccessResult(String sessionId, String url) {
        AccessResultCB cb = new AccessResultCB();
        cb.setupSelect_AccessResultDataAsOne();
        cb.query().setSessionId_Equal(sessionId);
        cb.query().setUrl_Equal(url);
        return accessResultBhv.selectEntity(cb);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#getAccessResultList(java.lang.String, boolean)
     */
    public List<AccessResult> getAccessResultList(String url, boolean hasData) {
        AccessResultCB cb = new AccessResultCB();
        if (hasData) {
            cb.setupSelect_AccessResultDataAsOne();
        }
        cb.query().setUrl_Equal(url);
        cb.query().addOrderBy_CreateTime_Desc();
        List<org.seasar.robot.db.exentity.AccessResult> list = accessResultBhv
                .selectList(cb);

        List<AccessResult> accessResultList = new ArrayList<AccessResult>();
        accessResultList.addAll(list);
        return accessResultList;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#iterate(java.lang.String, org.seasar.robot.util.AccessResultCallback)
     */
    public void iterate(String sessionId,
            final AccessResultCallback accessResultCallback) {
        AccessResultCB cb = new AccessResultCB();
        cb.setupSelect_AccessResultDataAsOne();
        cb.query().setSessionId_Equal(sessionId);
        cb.query().addOrderBy_CreateTime_Asc();
        accessResultBhv
                .selectCursor(
                        cb,
                        new EntityRowHandler<org.seasar.robot.db.exentity.AccessResult>() {
                            public void handle(
                                    org.seasar.robot.db.exentity.AccessResult entity) {
                                accessResultCallback.iterate(entity);
                            }
                        });
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.service.DataService#iterateUrlDiff(java.lang.String, java.lang.String, org.seasar.robot.util.AccessResultCallback)
     */
    public void iterateUrlDiff(String oldSessionId, String newSessionId,
            final AccessResultCallback accessResultCallback) {

        AccessResultPmb pmb = new AccessResultPmb();
        pmb.setOldSessionId(oldSessionId);
        pmb.setNewSessionId(newSessionId);
        final AccessResultDiffCursorHandler handler = new AccessResultDiffCursorHandler() {
            public Object fetchCursor(AccessResultDiffCursor cursor)
                    throws SQLException {
                while (cursor.next()) {
                    AccessResult accessResult = new org.seasar.robot.db.exentity.AccessResult();
                    Beans.copy(cursor, accessResult).execute();
                    accessResultCallback.iterate(accessResult);
                }
                return null;
            }
        };
        accessResultBhv.outsideSql().cursorHandling().selectCursor(
                AccessResultBhv.PATH_selectListByUrlDiff, pmb, handler);

    }
}
