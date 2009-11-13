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
package org.seasar.robot.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.CallbackContext;
import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.QLog;
import org.seasar.robot.dbflute.jdbc.SqlLogHandler;
import org.seasar.robot.dbflute.jdbc.SqlResultHandler;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.resource.InternalMapContext;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.resource.SQLExceptionHandler;
import org.seasar.robot.dbflute.s2dao.extension.TnSqlLogRegistry;
import org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes;
import org.seasar.robot.dbflute.twowaysql.DisplaySqlBuilder;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnBasicHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private DataSource dataSource;
    private String sql;
    private StatementFactory statementFactory;
    private Object[] loggingMessageSqlArgs;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnBasicHandler(DataSource ds, StatementFactory statementFactory) {
        setDataSource(ds);
        setStatementFactory(statementFactory);
    }

    public TnBasicHandler(DataSource ds, String sql, StatementFactory statementFactory) {
        setDataSource(ds);
        setSql(sql);
        setStatementFactory(statementFactory);
    }

    // ===================================================================================
    //                                                                        Common Logic
    //                                                                        ============
    // -----------------------------------------------------
    //                                    Arguments Handling
    //                                    ------------------
    /**
     * @param ps Prepared statement. (NotNull)
     * @param args The arguments for binding. (Nullable)
     * @param valueTypes The types of binding value. (NotNull)
     */
    protected void bindArgs(PreparedStatement ps, Object[] args, ValueType[] valueTypes) {
        if (args == null) {
            return;
        }
        try {
            for (int i = 0; i < args.length; ++i) {
                final ValueType valueType = valueTypes[i];
                valueType.bindValue(ps, i + 1, args[i]);
            }
        } catch (SQLException e) {
            handleSQLException(e, ps);
        }
    }

    /**
     * @param ps Prepared statement. (NotNull)
     * @param args The arguments for binding. (Nullable)
     * @param argTypes The types of arguments. (NotNull)
     */
    protected void bindArgs(PreparedStatement ps, Object[] args, Class<?>[] argTypes) {
        bindArgs(ps, args, argTypes, 0);
    }

    /**
     * @param ps Prepared statement. (NotNull)
     * @param args The arguments for binding. (Nullable)
     * @param argTypes The types of arguments. (NotNull)
     * @param beginIndex The index for beginning of binding.
     */
    protected void bindArgs(PreparedStatement ps, Object[] args, Class<?>[] argTypes, int beginIndex) {
        if (args == null) {
            return;
        }
        try {
            for (int i = beginIndex; i < args.length; ++i) {
                final ValueType valueType = findValueType(args[i], argTypes[i]);
                valueType.bindValue(ps, i + 1, args[i]);
            }
        } catch (SQLException e) {
            handleSQLException(e, ps);
        }
    }

    protected ValueType findValueType(Object arg, Class<?> argType) {
        ValueType valueType = TnValueTypes.getValueType(arg);
        if (valueType != null) {
            return valueType;
        }
        valueType = TnValueTypes.getValueType(argType);
        if (valueType != null) {
            return valueType;
        }
        String msg = "Unknown type: argType=" + argType + " args=" + arg;
        throw new IllegalStateException(msg);
    }

    protected Class<?>[] getArgTypes(Object[] args) {
        if (args == null) {
            return null;
        }
        Class<?>[] argTypes = new Class[args.length];
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
        // [SqlLogHandler]
        final SqlLogHandler sqlLogHandler = getSqlLogHander();
        final boolean existsSqlLogHandler = sqlLogHandler != null;

        // [SqlResultHandler]
        final SqlResultHandler sqlResultHander = getSqlResultHander();
        final boolean existsSqlResultHandler = sqlResultHander != null;

        // [SqlLogRegistry]
        final Object sqlLogRegistry = TnSqlLogRegistry.findContainerSqlLogRegistry();
        final boolean existsSqlLogRegistry = sqlLogRegistry != null;

        if (isLogEnabled() || existsSqlLogHandler || existsSqlResultHandler || existsSqlLogRegistry) {
            final String displaySql = getDisplaySql(args);
            if (isLogEnabled()) {
                log((isContainsLineSeparatorInSql() ? getLineSeparator() : "") + displaySql);
            }
            if (existsSqlLogHandler) { // DBFlute provides
                sqlLogHandler.handle(getSql(), displaySql, args, argTypes);
            }
            if (existsSqlLogRegistry) { // S2Container provides
                TnSqlLogRegistry.push(getSql(), displaySql, args, argTypes, sqlLogRegistry);
            }
            putObjectToMapContext("df:DisplaySql", displaySql);
        }
    }

    protected void putObjectToMapContext(String key, Object value) {
        InternalMapContext.setObject(key, value);
    }

    protected boolean isLogEnabled() {
        return QLog.isLogEnabled();
    }

    protected void log(String msg) {
        QLog.log(msg);
    }

    protected String getDisplaySql(Object[] args) {
        String logDateFormat = ResourceContext.getLogDateFormat();
        String logTimestampFormat = ResourceContext.getLogTimestampFormat();
        return DisplaySqlBuilder.buildDisplaySql(sql, args, logDateFormat, logTimestampFormat);
    }

    protected SqlLogHandler getSqlLogHander() {
        if (!CallbackContext.isExistCallbackContextOnThread()) {
            return null;
        }
        return CallbackContext.getCallbackContextOnThread().getSqlLogHandler();
    }

    protected SqlResultHandler getSqlResultHander() {
        if (!CallbackContext.isExistCallbackContextOnThread()) {
            return null;
        }
        return CallbackContext.getCallbackContextOnThread().getSqlResultHandler();
    }

    protected boolean isContainsLineSeparatorInSql() {
        return sql != null ? sql.contains(getLineSeparator()) : false;
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
    protected void handleSQLException(SQLException e, Statement statement) {
        handleSQLException(e, statement, false);
    }

    protected void handleSQLException(SQLException e, Statement statement, boolean uniqueConstraintValid) {
        String completeSql = buildLoggingMessageSql();
        new SQLExceptionHandler().handleSQLException(e, statement, uniqueConstraintValid, completeSql);
    }

    protected String buildLoggingMessageSql() {
        String completeSql = null;
        if (sql != null && loggingMessageSqlArgs != null) {
            try {
                completeSql = getDisplaySql(loggingMessageSqlArgs);
            } catch (RuntimeException ignored) {
            }
        }
        return completeSql;
    }

    // ===================================================================================
    //                                                                      JDBC Delegator
    //                                                                      ==============
    protected Connection getConnection() {
        if (dataSource == null) {
            throw new IllegalStateException("The dataSource should not be null!");
        }
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            handleSQLException(e, null);
            return null;// unreachable
        }
    }

    protected PreparedStatement prepareStatement(Connection conn) {
        if (sql == null) {
            throw new IllegalStateException("The sql should not be null!");
        }
        return statementFactory.createPreparedStatement(conn, sql);
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
    //                                                                       Assist Helper
    //                                                                       =============
    // It needs this method if the target database does not support line comment.
    protected String removeLineComment(final String sql) { // With removing CR!
        if (sql == null || sql.trim().length() == 0) {
            return sql;
        }
        final StringBuilder sb = new StringBuilder();
        final String[] lines = sql.split("\n");
        for (String line : lines) {
            if (line == null) {
                continue;
            }
            line = line.replaceAll("\r", ""); // Remove CR!
            if (line.startsWith("--")) {
                continue;
            }
            sb.append(line).append("\n");
        }
        final String filteredSql = sb.toString();
        return filteredSql.substring(0, filteredSql.lastIndexOf("\n"));
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String getLineSeparator() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        if (isRemoveLineCommentFromSql()) {
            sql = removeLineComment(sql);
        }
        this.sql = sql;
    }

    protected boolean isRemoveLineCommentFromSql() {
        // Because the MS-Access does not support line comments.
        return isCurrentDBDef(DBDef.MSAccess);
    }

    protected boolean isCurrentDBDef(DBDef currentDBDef) {
        return ResourceContext.isCurrentDBDef(currentDBDef);
    }

    public StatementFactory getStatementFactory() {
        return statementFactory;
    }

    public void setStatementFactory(StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    public void setLoggingMessageSqlArgs(Object[] loggingMessageSqlArgs) {
        this.loggingMessageSqlArgs = loggingMessageSqlArgs;
    }
}
