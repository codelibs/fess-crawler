/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.robot.dbflute.s2dao.rshandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;

/**
 * @author jflute
 */
public class TnScalarDynamicResultSetHandler implements TnResultSetHandler {

    private ValueType valueType;

    public TnScalarDynamicResultSetHandler(ValueType valueType) {
        this.valueType = valueType;
    }

    public Object handle(ResultSet rs) throws SQLException {
        List<Object> retList = null;
        Object ret = null;
        int index = 0;
        while (rs.next()) {
            if (index == 1) { // second loop
                retList = newArrayList();
                retList.add(ret);
            }
            ret = valueType.getValue(rs, 1);
            if (retList != null) { // true at second or more loop
                retList.add(ret);
            }
            ++index;
        }
        return retList != null ? retList : ret;
    }

    protected <ELEMENT> List<ELEMENT> newArrayList() {
        return new ArrayList<ELEMENT>();
    }
}
