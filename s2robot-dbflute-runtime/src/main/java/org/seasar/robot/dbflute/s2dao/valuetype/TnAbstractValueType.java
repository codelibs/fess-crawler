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
package org.seasar.robot.dbflute.s2dao.valuetype;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.seasar.robot.dbflute.jdbc.ValueType;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public abstract class TnAbstractValueType implements ValueType {

    private int sqlType;

    public TnAbstractValueType(int sqlType) {
        this.sqlType = sqlType;
    }

    protected void setNull(PreparedStatement ps, int index) throws SQLException {
        ps.setNull(index, sqlType);
    }

    protected void setNull(CallableStatement cs, String parameterName) throws SQLException {
        cs.setNull(parameterName, sqlType);
    }

    public void registerOutParameter(CallableStatement cs, int index) throws SQLException {
        cs.registerOutParameter(index, sqlType);
    }

    public void registerOutParameter(CallableStatement cs, String parameterName) throws SQLException {
        cs.registerOutParameter(parameterName, sqlType);
    }

    public int getSqlType() {
        return sqlType;
    }
}
