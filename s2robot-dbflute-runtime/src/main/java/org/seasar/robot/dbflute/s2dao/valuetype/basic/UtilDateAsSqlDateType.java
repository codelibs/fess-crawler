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
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public class UtilDateAsSqlDateType extends TnAbstractValueType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final SqlDateType _sqlDateType = new SqlDateType();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public UtilDateAsSqlDateType() {
        super(Types.DATE);
    }

    // ===================================================================================
    //                                                                           Get Value
    //                                                                           =========
    public Object getValue(ResultSet rs, int index) throws SQLException {
        return toUtilDate(_sqlDateType.getValue(rs, index));
    }

    public Object getValue(ResultSet rs, String columnName) throws SQLException {
        return toUtilDate(_sqlDateType.getValue(rs, columnName));
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return toUtilDate(_sqlDateType.getValue(cs, index));
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        return toUtilDate(_sqlDateType.getValue(cs, parameterName));
    }

    // ===================================================================================
    //                                                                          Bind Value
    //                                                                          ==========
    public void bindValue(Connection conn, PreparedStatement ps, int index, Object value) throws SQLException {
        _sqlDateType.bindValue(conn, ps, index, toSqlDate(value));
    }

    public void bindValue(Connection conn, CallableStatement cs, String parameterName, Object value)
            throws SQLException {
        _sqlDateType.bindValue(conn, cs, parameterName, toSqlDate(value));
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected java.util.Date toUtilDate(Object value) {
        return DfTypeUtil.toDate(value);
    }

    protected java.sql.Date toSqlDate(Object value) {
        return DfTypeUtil.toSqlDate(value);
    }
}