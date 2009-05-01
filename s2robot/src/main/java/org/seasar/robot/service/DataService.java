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

import java.util.List;

import javax.annotation.Resource;

import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.cbean.AccessResultDataCB;
import org.seasar.robot.db.exbhv.AccessResultBhv;
import org.seasar.robot.db.exbhv.AccessResultDataBhv;
import org.seasar.robot.db.exentity.AccessResult;
import org.seasar.robot.db.exentity.AccessResultData;

/**
 * @author shinsuke
 *
 */
public class DataService {

    @Resource
    protected AccessResultBhv accessResultBhv;

    @Resource
    protected AccessResultDataBhv accessResultDataBhv;

    public void store(AccessResult accessResult) {
        if (accessResult == null) {
            throw new RobotSystemException("AccessResult is null.");
        }

        accessResultBhv.insert(accessResult);

        AccessResultData accessResultData = accessResult
                .getAccessResultDataAsOne();
        if (accessResultData == null) {
            accessResultData = new AccessResultData();
            accessResultData.setTransformerName(Constants.NO_TRANSFORMER);
        }
        accessResultData.setId(accessResult.getId());
        accessResultDataBhv.insert(accessResultData);
    }

    public int getCount(String sessionId) {
        AccessResultCB cb = new AccessResultCB();
        cb.query().setSessionId_Equal(sessionId);
        return accessResultBhv.selectCount(cb);
    }

    public void delete(String sessionId) {
        AccessResultDataCB cb1 = new AccessResultDataCB();
        cb1.query().queryAccessResult().setSessionId_Equal(sessionId);
        accessResultDataBhv.queryDelete(cb1);

        AccessResultCB cb2 = new AccessResultCB();
        cb2.query().setSessionId_Equal(sessionId);
        accessResultBhv.queryDelete(cb2);
    }

    public void deleteAll() {
        AccessResultDataCB cb1 = new AccessResultDataCB();
        accessResultDataBhv.queryDelete(cb1);

        AccessResultCB cb2 = new AccessResultCB();
        accessResultBhv.queryDelete(cb2);
    }

    public AccessResult getAccessResult(String sessionId, String url) {
        AccessResultCB cb = new AccessResultCB();
        cb.setupSelect_AccessResultDataAsOne();
        cb.query().setSessionId_Equal(sessionId);
        cb.query().setUrl_Equal(url);
        return accessResultBhv.selectEntity(cb);
    }

    public List<AccessResult> getAccessResultList(String url, boolean hasData) {
        AccessResultCB cb = new AccessResultCB();
        if (hasData) {
            cb.setupSelect_AccessResultDataAsOne();
        }
        cb.query().setUrl_Equal(url);
        cb.query().addOrderBy_CreateTime_Desc();
        return accessResultBhv.selectList(cb);
    }

    // TODO callback get
}
