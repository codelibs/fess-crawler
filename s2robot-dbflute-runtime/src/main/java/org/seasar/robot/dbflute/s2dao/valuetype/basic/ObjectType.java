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
package org.seasar.robot.dbflute.s2dao.valuetype.basic;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.seasar.robot.dbflute.s2dao.valuetype.TnAbstractValueType;

/**
 * @author jflute
 */
public class ObjectType extends TnAbstractValueType {

    protected final boolean _defaultObject; // if false, means dynamic object

    public ObjectType() {
        super(Types.VARCHAR);
        _defaultObject = true;
    }

    public ObjectType(int sqlType) {
        super(sqlType);
        _defaultObject = false;
    }

    public Object getValue(ResultSet rs, int index) throws SQLException {
        return rs.getObject(index);
    }

    public Object getValue(ResultSet rs, String columnName) throws SQLException {
        return rs.getObject(columnName);
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return cs.getObject(index);
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        return cs.getObject(parameterName);
    }

    public void bindValue(Connection conn, PreparedStatement ps, int index, Object value) throws SQLException {
        if (value == null) {
            setNull(ps, index);
        } else {
            if (isDefaultObject()) {
                ps.setObject(index, value);
            } else {
                ps.setObject(index, value, getSqlType());
            }
        }
    }

    public void bindValue(Connection conn, CallableStatement cs, String parameterName, Object value)
            throws SQLException {
        if (value == null) {
            setNull(cs, parameterName);
        } else {
            if (isDefaultObject()) {
                cs.setObject(parameterName, value);
            } else {
                cs.setObject(parameterName, value, getSqlType());
            }
        }
    }

    public boolean isDefaultObject() {
        return _defaultObject;
    }
}