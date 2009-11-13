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

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.util.DfReflectionUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class UserDefineType implements ValueType {

    private ValueType baseValueType;

    private Method valueOfMethod;

    private Method valueMethod;

    public UserDefineType(ValueType baseValueType, Method valueOfMethod, Method valueMethod) {
        this.baseValueType = baseValueType;
        this.valueOfMethod = valueOfMethod;
        this.valueMethod = valueMethod;
    }

    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        return fromDbToJava(baseValueType.getValue(resultSet, index));
    }

    public Object getValue(ResultSet resultSet, String columnName) throws SQLException {

        return fromDbToJava(baseValueType.getValue(resultSet, columnName));
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return fromDbToJava(baseValueType.getValue(cs, index));
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        return fromDbToJava(baseValueType.getValue(cs, parameterName));
    }

    public void bindValue(PreparedStatement ps, int index, Object value) throws SQLException {
        baseValueType.bindValue(ps, index, fromJavaToDb(value));
    }

    public void bindValue(CallableStatement cs, String parameterName, Object value) throws SQLException {
        baseValueType.bindValue(cs, parameterName, fromJavaToDb(value));
    }

    public void registerOutParameter(CallableStatement cs, int index) throws SQLException {
        baseValueType.registerOutParameter(cs, index);
    }

    public void registerOutParameter(CallableStatement cs, String parameterName) throws SQLException {
        baseValueType.registerOutParameter(cs, parameterName);
    }

    private Object fromDbToJava(Object value) {
        if (value == null) {
            return null;
        }
        return DfReflectionUtil.invoke(valueOfMethod, null, new Object[] { value });
    }

    private Object fromJavaToDb(Object value) {
        if (value == null) {
            return null;
        }
        return DfReflectionUtil.invoke(valueMethod, value, null);
    }

    public String toText(Object value) {
        return baseValueType.toText(fromJavaToDb(value));
    }

    public int getSqlType() {
        return baseValueType.getSqlType();
    }
}