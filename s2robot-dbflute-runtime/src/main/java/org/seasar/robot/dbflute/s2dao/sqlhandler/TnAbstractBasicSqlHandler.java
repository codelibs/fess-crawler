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
package org.seasar.robot.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.robot.dbflute.CallbackContext;
import org.seasar.robot.dbflute.QLog;
import org.seasar.robot.dbflute.exception.handler.SQLExceptionHandler;
import org.seasar.robot.dbflute.jdbc.SqlLogHandler;
import org.seasar.robot.dbflute.jdbc.SqlResultHandler;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.resource.InternalMapContext;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.extension.TnSqlLogRegistry;
import org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes;
import org.seasar.robot.dbflute.twowaysql.DisplaySqlBuilder;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * The basic handler to execute SQL. <br />
 * This is always created when executing so it's non thread safe. <br />
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnAbstractBasicSqlHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance for internal debug. (QLog should be used instead for query log) */
    private static final Log _log = LogFactory.getLog(TnAbstractBasicSqlHandler.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DataSource _dataSource;
    protected final StatementFactory _statementFactory;
    protected final String _sql;
    protected Object[] _exceptionMessageSqlArgs; // not required

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param dataSource The data source for a database connection. (NotNull)
     * @param statementFactory The factory of statement. (NotNull)
     * @param sql The executed SQL. (NotNull)
     */
    public TnAbstractBasicSqlHandler(DataSource dataSource, StatementFactory statementFactory, String sql) {
        assertObjectNotNull("dataSource", dataSource);
        assertObjectNotNull("statementFactory", statementFactory);
        assertObjectNotNull("sql", sql);
        _dataSource = dataSource;
        _statementFactory = statementFactory;
        _sql = sql;
    }

    // ===================================================================================
    //                                                                        Common Logic
    //                                                                        ============
    // -----------------------------------------------------
    //                                    Arguments Handling
    //                                    ------------------
    /**
     * @param conn The connection for the database. (NotNull)
     * @param ps The prepared statement for the SQL. (NotNull)
     * @param args The arguments for binding. (NullAllowed)
     * @param valueTypes The types of binding value. (NotNull)
     */
    protected void bindArgs(Connection conn, PreparedStatement ps, Object[] args, ValueType[] valueTypes) {
        if (args == null) {
            return;
        }
        try {
            for (int i = 0; i < args.length; ++i) {
                final ValueType valueType = valueTypes[i];
                valueType.bindValue(conn, ps, i + 1, args[i]);
            }
        } catch (SQLException e) {
            handleSQLException(e, ps);
        }
    }

    /**
     * @param conn The connection for the database. (NotNull)
     * @param ps The prepared statement for the SQL. (NotNull)
     * @param args The arguments for binding. (NullAllowed)
     * @param argTypes The types of arguments. (NullAllowed: if args is null, this is also null)
     */
    protected void bindArgs(Connection conn, PreparedStatement ps, Object[] args, Class<?>[] argTypes) {
        bindArgs(conn, ps, args, argTypes, 0);
    }

    /**
     * @param conn The connection for the database. (NotNull)
     * @param ps The prepared statement for the SQL. (NotNull)
     * @param args The arguments for binding. (NullAllowed)
     * @param argTypes The types of arguments. (NullAllowed: if args is null, this is also null)
     * @param beginIndex The index for beginning of binding.
     */
    protected void bindArgs(Connection conn, PreparedStatement ps, Object[] args, Class<?>[] argTypes, int beginIndex) {
        if (args == null) {
            return;
        }
        try {
            for (int i = beginIndex; i < args.length; ++i) {
                final ValueType valueType = findValueType(argTypes[i], args[i]);
                valueType.bindValue(conn, ps, i + 1, args[i]);
            }
        } catch (SQLException e) {
            handleSQLException(e, ps);
        }
    }

    protected ValueType findValueType(Class<?> type, Object instance) {
        return TnValueTypes.findByTypeOrValue(type, instance);
    }

    protected Class<?>[] getArgTypes(Object[] args) {
        if (args == null) {
            return null;
        }
        final Class<?>[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; ++i) {
            Object arg = args[i];
            if (arg != null) {
                argTypes[i] = arg.getClass();
            }
        }
        return argTypes;
    }

    // -----------------------------------------------------
    //                                           SQL Logging
    //                                           -----------
    protected void logSql(Object[] args, Class<?>[] argTypes) {
        final boolean existsSqlLogHandler = hasSqlLogHandler();
        final boolean existsSqlResultHandler = hasSqlResultHandler();
        final Object sqlLogRegistry = TnSqlLogRegistry.findContainerSqlLogRegistry();
        final boolean existsSqlLogRegistry = sqlLogRegistry != null;

        if (isLogEnabled() || existsSqlLogHandler || existsSqlResultHandler || existsSqlLogRegistry) {
            if (isInternalDebugEnabled()) {
                _log.debug("...Building displaySql because of " + isLogEnabled() + ", " + existsSqlLogHandler + ", "
                        + existsSqlResultHandler + ", " + existsSqlLogRegistry);
            }
            final String displaySql = buildDisplaySql(args);
            if (isLogEnabled()) {
                logDisplaySql(displaySql);
            }
            if (existsSqlLogHandler) { // DBFlute provides
                getSqlLogHander().handle(_sql, displaySql, args, argTypes);
            }
            if (existsSqlLogRegistry) { // S2Container provides
                TnSqlLogRegistry.push(_sql, displaySql, args, argTypes, sqlLogRegistry);
            }
            if (existsSqlResultHandler) {
                saveDisplaySqlForResultInfo(displaySql);
            }
        }
    }

    protected String buildDisplaySql(Object[] args) {
        final String logDateFormat = ResourceContext.getLogDateFormat();
        final String logTimestampFormat = ResourceContext.getLogTimestampFormat();
        return DisplaySqlBuilder.buildDisplaySql(_sql, args, logDateFormat, logTimestampFormat);
    }

    protected SqlLogHandler getSqlLogHander() {
        if (!CallbackContext.isExistCallbackContextOnThread()) {
            return null;
        }
        return CallbackContext.getCallbackContextOnThread().getSqlLogHandler();
    }

    protected boolean hasSqlLogHandler() {
        return getSqlLogHander() != null;
    }

    protected SqlResultHandler getSqlResultHander() {
        if (!CallbackContext.isExistCallbackContextOnThread()) {
            return null;
        }
        return CallbackContext.getCallbackContextOnThread().getSqlResultHandler();
    }

    protected boolean hasSqlResultHandler() {
        return getSqlResultHander() != null;
    }

    protected void logDisplaySql(String displaySql) {
        log((isContainsLineSeparatorInSql(displaySql) ? ln() : "") + displaySql);
    }

    protected boolean isContainsLineSeparatorInSql(String displaySql) {
        return displaySql != null ? displaySql.contains(ln()) : false;
    }

    protected void saveDisplaySqlForResultInfo(String displaySql) {
        InternalMapContext.setResultInfoDisplaySql(displaySql);
    }

    // -----------------------------------------------------
    //                                               Various
    //                                               -------
    protected String getBindVariableText(Object bindVariable) {
        String logDateFormat = ResourceContext.getLogDateFormat();
        String logTimestampFormat = ResourceContext.getLogTimestampFormat();
        return DisplaySqlBuilder.getBindVariableText(bindVariable, logDateFormat, logTimestampFormat);
    }

    // ===================================================================================
    //                                                                   Exception Handler
    //                                                                   =================
    protected void handleSQLException(SQLException e, Statement st) {
        handleSQLException(e, st, false);
    }

    protected void handleSQLException(SQLException e, Statement st, boolean uniqueConstraintValid) {
        final String executedSql = _sql;
        final String displaySql = buildExceptionMessageSql();
        createSQLExceptionHandler().handleSQLException(e, st, uniqueConstraintValid, executedSql, displaySql);
    }

    protected SQLExceptionHandler createSQLExceptionHandler() {
        return ResourceContext.createSQLExceptionHandler();
    }

    protected String buildExceptionMessageSql() {
        String displaySql = null;
        if (_sql != null && _exceptionMessageSqlArgs != null) {
            try {
                displaySql = buildDisplaySql(_exceptionMessageSqlArgs);
            } catch (RuntimeException continued) { // because of when exception occurs
                if (_log.isDebugEnabled()) {
                    _log.debug("*Failed to build SQL for an exception message: " + continued.getMessage());
                }
            }
        }
        return displaySql;
    }

    // ===================================================================================
    //                                                                      JDBC Delegator
    //                                                                      ==============
    protected Connection getConnection() {
        try {
            return _dataSource.getConnection();
        } catch (SQLException e) {
            handleSQLException(e, null);
            return null;// unreachable
        }
    }

    protected PreparedStatement prepareStatement(Connection conn) {
        return _statementFactory.createPreparedStatement(conn, _sql);
    }

    protected int executeUpdate(PreparedStatement ps) {
        try {
            return ps.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e, ps, true);
            return 0;// unreachable
        }
    }

    protected void setFetchSize(Statement statement, int fetchSize) {
        if (statement == null) {
            return;
        }
        try {
            statement.setFetchSize(fetchSize);
        } catch (SQLException e) {
            handleSQLException(e, statement);
        }
    }

    protected void setMaxRows(Statement statement, int maxRows) {
        if (statement == null) {
            return;
        }
        try {
            statement.setMaxRows(maxRows);
        } catch (SQLException e) {
            handleSQLException(e, statement);
        }
    }

    protected void close(Statement statement) {
        if (statement == null) {
            return;
        }
        try {
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, statement);
        }
    }

    protected void close(ResultSet resultSet) {
        if (resultSet == null) {
            return;
        }
        try {
            resultSet.close();
        } catch (SQLException e) {
            handleSQLException(e, null);
        }
    }

    protected void close(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.close();
        } catch (SQLException e) {
            handleSQLException(e, null);
        }
    }

    // ===================================================================================
    //                                                                           Query Log
    //                                                                           =========
    protected boolean isLogEnabled() {
        return QLog.isLogEnabled();
    }

    protected void log(String msg) {
        QLog.log(msg);
    }

    // ===================================================================================
    //                                                                      Internal Debug
    //                                                                      ==============
    private boolean isInternalDebugEnabled() { // because log instance is private
        return ResourceContext.isInternalDebug() && _log.isDebugEnabled();
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setExceptionMessageSqlArgs(Object[] exceptionMessageSqlArgs) {
        this._exceptionMessageSqlArgs = exceptionMessageSqlArgs;
    }
}
