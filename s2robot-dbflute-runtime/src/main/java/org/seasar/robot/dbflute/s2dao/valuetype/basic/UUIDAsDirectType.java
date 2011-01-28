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
import java.util.UUID;

import org.seasar.robot.dbflute.s2dao.valuetype.TnAbstractValueType;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * The value type of UUID as direct handling. <br />
 * This value type is available if the JDBC driver
 * allows UUID type, for example when binding.
 * @author jflute
 */
public class UUIDAsDirectType extends TnAbstractValueType {

    public UUIDAsDirectType() {
        this(Types.OTHER);
    }

    protected UUIDAsDirectType(int sqlType) { // for extension
        super(sqlType);
    }

    public Object getValue(ResultSet rs, int index) throws SQLException {
        final String string = rs.getString(index);
        return string != null ? toUUID(string) : string;
    }

    public Object getValue(ResultSet rs, String columnName) throws SQLException {
        final String string = rs.getString(columnName);
        return string != null ? toUUID(string) : string;
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        final String string = cs.getString(index);
        return string != null ? toUUID(string) : string;
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        final String string = cs.getString(parameterName);
        return string != null ? toUUID(string) : string;
    }

    public void bindValue(Connection conn, PreparedStatement ps, int index, Object value) throws SQLException {
        if (value == null) {
            setNull(ps, index);
        } else {
            ps.setObject(index, toBindingValue(value), getSqlType());
        }
    }

    public void bindValue(Connection conn, CallableStatement cs, String parameterName, Object value)
            throws SQLException {
        if (value == null) {
            setNull(cs, parameterName);
        } else {
            cs.setObject(parameterName, toBindingValue(value), getSqlType());
        }
    }

    protected Object toBindingValue(Object value) {
        return toUUID(value);
    }

    protected UUID toUUID(Object value) {
        return DfTypeUtil.toUUID(value);
    }
}