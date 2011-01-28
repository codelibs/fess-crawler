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
package org.seasar.robot.dbflute.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public interface ValueType {

    Object getValue(ResultSet rs, int index) throws SQLException;

    Object getValue(ResultSet rs, String columnName) throws SQLException;

    Object getValue(CallableStatement cs, int index) throws SQLException;

    Object getValue(CallableStatement cs, String parameterName) throws SQLException;

    // /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
    // *binding may need a connection
    //  (for example, Oracle ARRAY type needs its connection to bind)
    // = = = = = = = = = =/

    /**
     * Bind the value. <br />
     * Also used to procedures instead of bindValue(CallableStatement).
     * @param conn The connection for the database. (NotNull)
     * @param ps The prepared statement. (NotNull)
     * @param index The parameter index.
     * @param value The parameter value. (NullAllowed)
     * @throws SQLException
     */
    void bindValue(Connection conn, PreparedStatement ps, int index, Object value) throws SQLException;

    void bindValue(Connection conn, CallableStatement cs, String parameterName, Object value) throws SQLException;

    void registerOutParameter(Connection conn, CallableStatement cs, int index) throws SQLException;

    void registerOutParameter(Connection conn, CallableStatement cs, String parameterName) throws SQLException;

    /**
     * The SQL type of JDBC.
     * @return The integer definition of java.sql.Types.
     */
    int getSqlType();
}