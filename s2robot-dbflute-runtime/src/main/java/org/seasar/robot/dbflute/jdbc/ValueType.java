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
package org.seasar.robot.dbflute.jdbc;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public interface ValueType {

    Object getValue(ResultSet resultSet, int index) throws SQLException;

    Object getValue(ResultSet resultSet, String columnName) throws SQLException;

    Object getValue(CallableStatement cs, int index) throws SQLException;

    Object getValue(CallableStatement cs, String parameterName) throws SQLException;

    void bindValue(PreparedStatement ps, int index, Object value) throws SQLException;

    void bindValue(CallableStatement cs, String parameterName, Object value) throws SQLException;

    void registerOutParameter(CallableStatement cs, int index) throws SQLException;

    void registerOutParameter(CallableStatement cs, String parameterName) throws SQLException;

    String toText(Object value);

    int getSqlType();
}