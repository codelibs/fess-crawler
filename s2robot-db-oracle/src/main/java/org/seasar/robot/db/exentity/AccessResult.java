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
package org.seasar.robot.db.exentity;

import java.sql.Timestamp;
import java.util.Date;

import org.seasar.framework.beans.util.Beans;
import org.seasar.robot.db.bsentity.BsAccessResult;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;

/**
 * The entity of ACCESS_RESULT.
 * <p>
 * You can implement your original methods here. This class remains when
 * re-generating.
 * </p>
 * 
 * @author DBFlute(AutoGenerator)
 */
public class AccessResult extends BsAccessResult implements
        org.seasar.robot.entity.AccessResult {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    @Override
    public void init(final ResponseData responseData,
            final ResultData resultData) {

        setCreateTime(new Timestamp(new Date().getTime()));
        // TODO response_time
        Beans.copy(responseData, this).execute();

        final AccessResultData accessResultData = new AccessResultData();
        Beans.copy(resultData, accessResultData).execute();
        setAccessResultDataAsOne(accessResultData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.entity.AccessResult#getAccessResultData()
     */
    @Override
    public AccessResultData getAccessResultData() {
        return getAccessResultDataAsOne();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.seasar.robot.entity.AccessResult#setAccessResultData(org.seasar.robot
     * .db.exentity.AccessResultData)
     */
    @Override
    public void setAccessResultData(
            final org.seasar.robot.entity.AccessResultData accessResultData) {
        setAccessResultDataAsOne((AccessResultData) accessResultData);
    }
}
