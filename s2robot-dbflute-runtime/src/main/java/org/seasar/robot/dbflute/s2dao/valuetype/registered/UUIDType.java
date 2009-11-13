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
package org.seasar.robot.dbflute.s2dao.valuetype.registered;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.seasar.robot.dbflute.s2dao.valuetype.TnAbstractValueType;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * The value type of UUID.
 * @author jflute
 */
public class UUIDType extends TnAbstractValueType {

    public UUIDType() {
        super(Types.OTHER);
    }

    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        String string = resultSet.getString(index);
        return string != null ? UUID.fromString(string) : string;
    }

    public Object getValue(ResultSet resultSet, String columnName) throws SQLException {
        String string = resultSet.getString(columnName);
        return string != null ? UUID.fromString(string) : string;
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        String string = cs.getString(index);
        return string != null ? UUID.fromString(string) : string;
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        String string = cs.getString(parameterName);
        return string != null ? UUID.fromString(string) : string;
    }

    public void bindValue(PreparedStatement ps, int index, Object value) throws SQLException {
        if (value == null) {
            setNull(ps, index);
        } else {
            ps.setObject(index, value, getSqlType());
        }
    }

    public void bindValue(CallableStatement cs, String parameterName, Object value) throws SQLException {
        if (value == null) {
            setNull(cs, parameterName);
        } else {
            cs.setObject(parameterName, value, getSqlType());
        }
    }

    public String toText(Object value) {
        if (value == null) {
            return DfTypeUtil.nullText();
        }
        return DfTypeUtil.toText(value);
    }
}