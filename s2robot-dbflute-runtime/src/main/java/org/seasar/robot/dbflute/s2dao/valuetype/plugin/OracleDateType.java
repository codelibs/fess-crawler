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
package org.seasar.robot.dbflute.s2dao.valuetype.plugin;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.seasar.robot.dbflute.s2dao.valuetype.basic.UtilDateAsTimestampType;

/**
 * @author jflute
 */
public abstract class OracleDateType extends UtilDateAsTimestampType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final OracleAgent _agent;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OracleDateType() {
        _agent = createOracleAgent();
    }

    /**
     * Create the agent for Oracle.
     * @return The instance of agent. (NotNull)
     */
    protected abstract OracleAgent createOracleAgent();

    // ===================================================================================
    //                                                                          Bind Value
    //                                                                          ==========
    @Override
    public void bindValue(Connection conn, PreparedStatement ps, int index, Object value) throws SQLException {
        if (value == null) { // basically for insert and update
            super.bindValue(conn, ps, index, null);
        } else {
            ps.setObject(index, toOracleDate(toTimestamp(value)));
        }
    }

    @Override
    public void bindValue(Connection conn, CallableStatement cs, String parameterName, Object value)
            throws SQLException {
        if (value == null) {
            super.bindValue(conn, cs, parameterName, null);
        } else {
            cs.setObject(parameterName, toOracleDate(toTimestamp(value)));
        }
    }

    // ===================================================================================
    //                                                                       Oracle's Type
    //                                                                       =============
    /**
     * Convert the time-stamp to Oracle's date.
     * @param timestamp The value of time-stamp. (NotNull) 
     * @return The instance of oracle.sql.DATE for the time-stamp argument. (NotNull)
     */
    protected Object toOracleDate(Timestamp timestamp) {
        return _agent.toOracleDate(timestamp);
    }
}