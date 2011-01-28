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
package org.seasar.robot.dbflute.s2dao.identity;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.exception.handler.SQLExceptionHandler;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnBasicSelectHandler;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnIdentifierAbstractGenerator implements TnIdentifierGenerator {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final TnPropertyType _propertyType;
    protected final TnResultSetHandler _resultSetHandler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnIdentifierAbstractGenerator(TnPropertyType propertyType) {
        if (propertyType == null) {
            String msg = "The arguement 'propertyType' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        _propertyType = propertyType;
        _resultSetHandler = new TnIdentifierResultSetHandler(propertyType.getValueType());
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isPrimaryKey() {
        return _propertyType.isPrimaryKey();
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected Object executeSql(DataSource ds, String sql, Object[] args) {
        TnBasicSelectHandler selectHandler = createSelectHandler(ds, sql);
        if (args != null) {
            selectHandler.setExceptionMessageSqlArgs(args);
        }
        return selectHandler.execute(args);
    }

    protected TnBasicSelectHandler createSelectHandler(DataSource ds, String sql) {
        // Use original statement factory for identifier generator.
        return new TnBasicSelectHandler(ds, sql, _resultSetHandler, createStatementFactory(ds, sql));
    }

    protected StatementFactory createStatementFactory(DataSource ds, String sql) {
        return new TnIdentifierGeneratorStatementFactory();
    }

    protected void reflectIdentifier(Object bean, Object value) {
        final DfPropertyDesc pd = _propertyType.getPropertyDesc();
        pd.setValue(bean, value); // setting by reflection here
    }

    // ===================================================================================
    //                                                                  Result Set Handler
    //                                                                  ==================
    protected static class TnIdentifierResultSetHandler implements TnResultSetHandler {
        private ValueType _valueType;

        public TnIdentifierResultSetHandler(ValueType valueType) {
            this._valueType = valueType;
        }

        public Object handle(ResultSet rs) throws SQLException {
            if (rs.next()) {
                return _valueType.getValue(rs, 1);
            }
            return null;
        }
    }

    // ===================================================================================
    //                                                                   Statement Factory
    //                                                                   =================
    protected static class TnIdentifierGeneratorStatementFactory implements StatementFactory {
        public PreparedStatement createPreparedStatement(Connection conn, String sql) {
            try {
                return conn.prepareStatement(sql);
            } catch (SQLException e) {
                handleSQLException(e, null);
                return null; // unreachable
            }
        }

        public CallableStatement createCallableStatement(Connection conn, String sql) {
            try {
                return conn.prepareCall(sql);
            } catch (SQLException e) {
                handleSQLException(e, null);
                return null; // unreachable
            }
        }

        protected void handleSQLException(SQLException e, Statement st) {
            createSQLExceptionHandler().handleSQLException(e, st);
        }

        protected SQLExceptionHandler createSQLExceptionHandler() {
            return ResourceContext.createSQLExceptionHandler();
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getPropertyName() {
        return _propertyType.getPropertyName();
    }
}
