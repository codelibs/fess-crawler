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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.jdbc.FetchBean;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.s2dao.jdbc.TnFetchAssistResultSet;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.robot.dbflute.s2dao.metadata.TnProcedureMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnProcedureParameterType;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnProcedureHandler extends TnAbstractBasicSqlHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private TnProcedureMetaData _procedureMetaData;
    private TnProcedureResultSetHandlerProvider _resultSetHandlerProvider;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnProcedureHandler(DataSource dataSource, StatementFactory statementFactory, String sql,
            TnProcedureMetaData procedureMetaData, TnProcedureResultSetHandlerProvider resultSetHandlerProvider) {
        super(dataSource, statementFactory, sql);
        assertObjectNotNull("procedureMetaData", procedureMetaData);
        assertObjectNotNull("resultSetHandlerProvider", resultSetHandlerProvider);
        _procedureMetaData = procedureMetaData;
        _resultSetHandlerProvider = resultSetHandlerProvider;
    }

    public static interface TnProcedureResultSetHandlerProvider { // is needed to construct an instance
        TnResultSetHandler provideResultSetHandler(TnProcedureParameterType ppt);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(final Object[] args) {
        final Class<?>[] argTypes = getArgTypes(args);
        final Object pmb = getParameterBean(args);
        logSql(args, argTypes);
        Connection conn = null;
        CallableStatement cs = null;
        try {
            conn = getConnection();
            cs = prepareCallableStatement(conn);
            bindArgs(conn, cs, pmb);

            // Execute the procedure!
            // The return means whether the first result is a (not-parameter) result set.
            final boolean executed = cs.execute();

            handleNotParamResult(conn, cs, pmb, executed); // should be before out-parameter handling
            handleOutParameter(conn, cs, pmb, executed);
            return pmb;
        } catch (SQLException e) {
            handleSQLException(e, cs);
            return null; // unreachable
        } finally {
            close(cs);
            close(conn);
        }
    }

    protected Object getParameterBean(Object[] args) {
        if (args.length == 0) {
            return null;
        }
        if (args.length == 1) {
            if (args[0] == null) {
                throw new IllegalStateException("args[0] should not be null!");
            }
            return args[0];
        }
        throw new IllegalStateException("The size of args should be 1: " + args.length);
    }

    protected CallableStatement prepareCallableStatement(final Connection connection) {
        if (_sql == null) {
            throw new IllegalStateException("The SQL should not be null!");
        }
        return _statementFactory.createCallableStatement(connection, _sql);
    }

    protected void bindArgs(Connection conn, CallableStatement cs, Object dto) throws SQLException {
        if (dto == null) {
            return;
        }
        int i = 0;
        for (TnProcedureParameterType ppt : _procedureMetaData.getBindParameterTypeList()) {
            final ValueType valueType = ppt.getValueType();
            final int bindIndex = (i + 1);
            // if INOUT parameter, both are true
            if (ppt.isOutType()) {
                valueType.registerOutParameter(conn, cs, bindIndex);
            }
            if (ppt.isInType()) {
                // bind as PreparedStatement
                // because CallableStatement's setter might be unsupported
                // (for example, PostgreSQL JDBC Driver for JDBC 3.0)
                final Object value = ppt.getValue(dto);
                valueType.bindValue(conn, cs, bindIndex, value);
            }
            // either must be true
            ++i;
        }
    }

    /**
     * Handle not-parameter result set, for example, MySQL, DB2 and (MS) SQLServer.
     * @param conn The connection for the database. (NotNull)
     * @param cs The statement of procedure. (NotNull)
     * @param pmb The parameter bean from arguments. (NotNull)
     * @param executed The return value of execute() that means whether the first result is a result set. 
     * @throws SQLException
     */
    protected void handleNotParamResult(Connection conn, CallableStatement cs, Object pmb, boolean executed)
            throws SQLException {
        if (pmb == null) {
            return;
        }
        if (!executed) {
            if (!cs.getMoreResults()) { // just in case
                return;
            }
        }
        final List<TnProcedureParameterType> resultList = _procedureMetaData.getNotParamResultTypeList();
        ResultSet rs = null;
        for (TnProcedureParameterType ppt : resultList) {
            try {
                rs = cs.getResultSet();
                if (rs == null) {
                    break;
                }
                rs = wrapResultSetIfNeeds(pmb, rs);
                final TnResultSetHandler handler = createResultSetHandler(pmb, ppt);
                final Object beanList = handler.handle(rs);
                ppt.setValue(pmb, beanList);
                if (!cs.getMoreResults()) {
                    break;
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        }
    }

    /**
     * Handle result set for out-parameter.
     * @param conn The connection for the database. (NotNull)
     * @param cs The statement of procedure. (NotNull)
     * @param pmb The parameter bean from arguments. (NotNull)
     * @param executed The return value of execute() that means whether the first result is a result set.
     * @throws SQLException
     */
    protected void handleOutParameter(Connection conn, CallableStatement cs, Object pmb, boolean executed)
            throws SQLException {
        if (pmb == null) {
            return;
        }
        int index = 0;
        for (TnProcedureParameterType ppt : _procedureMetaData.getBindParameterTypeList()) {
            final ValueType valueType = ppt.getValueType();
            if (ppt.isOutType()) {
                Object value = valueType.getValue(cs, index + 1);
                if (value instanceof ResultSet) {
                    final ResultSet rs = wrapResultSetIfNeeds(pmb, (ResultSet) value);
                    final TnResultSetHandler handler = createResultSetHandler(pmb, ppt);
                    try {
                        value = handler.handle(rs);
                    } finally {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                }
                ppt.setValue(pmb, value);
            }
            ++index;
        }
    }

    // ===================================================================================
    //                                                                          DisplaySql
    //                                                                          ==========
    @Override
    protected String buildDisplaySql(final Object[] args) { // for procedure call
        final String sql = _sql;
        final Object dto = getParameterBean(args);
        if (args == null || dto == null) {
            return sql;
        }
        final StringBuilder sb = new StringBuilder(100);
        int pos = 0;
        int pos2 = 0;
        for (TnProcedureParameterType ppt : _procedureMetaData.getBindParameterTypeList()) {
            if ((pos2 = sql.indexOf('?', pos)) < 0) {
                break;
            }
            sb.append(sql.substring(pos, pos2));
            pos = pos2 + 1;
            if (ppt.isInType()) {
                sb.append(getBindVariableText(ppt.getValue(dto)));
            } else {
                sb.append(sql.substring(pos2, pos));
            }
        }
        sb.append(sql.substring(pos));
        return sb.toString();
    }

    // ===================================================================================
    //                                                                    ResultSetHandler
    //                                                                    ================
    protected TnResultSetHandler createResultSetHandler(Object pmb, TnProcedureParameterType ppt) {
        return _resultSetHandlerProvider.provideResultSetHandler(ppt);
    }

    protected ResultSet wrapResultSetIfNeeds(Object pmb, ResultSet rs) {
        if (pmb instanceof FetchBean) {
            final FetchBean fcbean = (FetchBean) pmb;
            final int safetyMaxResultSize = fcbean.getSafetyMaxResultSize();
            if (safetyMaxResultSize > 0) { // wrap for check safety
                return new TnFetchAssistResultSet(rs, fcbean, false, false);
            }
        }
        return rs;
    }
}
