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
package org.seasar.robot.db.exbhv;

import org.seasar.robot.db.cbean.AccessResultDataCB;

/**
 * The behavior of ACCESS_RESULT_DATA.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDataBhv extends
        org.seasar.robot.db.bsbhv.BsAccessResultDataBhv {
    public int deleteBySessionId(String sessionId) {
        AccessResultDataCB cb1 = new AccessResultDataCB();
        cb1.query().queryAccessResult().setSessionId_Equal(sessionId);
        return queryDelete(cb1);

        //        return outsideSql().execute(AccessResultDataBhv.PATH_deleteBySessionId,
        //                sessionId);
    }

    public int deleteAll() {
        //        AccessResultDataCB cb1 = new AccessResultDataCB();
        //        accessResultDataBhv.queryDelete(cb1);
        String pmb = null;
        return outsideSql().execute(AccessResultDataBhv.PATH_deleteAll, pmb);
    }
}
