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

import javax.sql.DataSource;

import org.seasar.robot.dbflute.cbean.FetchAssistContext;
import org.seasar.robot.dbflute.cbean.FetchBean;
import org.seasar.robot.dbflute.cbean.FetchNarrowingBean;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.robot.dbflute.s2dao.jdbc.TnFetchAssistResultSet;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnBasicSelectHandler extends TnBasicHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private TnResultSetHandler resultSetHandler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnBasicSelectHandler(DataSource dataSource, String sql, TnResultSetHandler resultSetHandler,
            StatementFactory statementFactory) {
        super(dataSource, statementFactory);
        setSql(sql);
        setResultSetHandler(resultSetHandler);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) {
        return execute(args, getArgTypes(args));
    }

    public Object execute(Object[] args, Class<?>[] argTypes) {
        Connection conn = getConnection();
        try {
            return execute(conn, args, argTypes);
        } finally {
            close(conn);
        }
    }

    public Object execute(Connection conn, Object[] args, Class<?>[] argTypes) {
        logSql(args, argTypes);
        PreparedStatement ps = null;
        try {
            ps = prepareStatement(conn);
            bindArgs(ps, args, argTypes);
            return execute(ps);
        } catch (SQLException e) {
            handleSQLException(e, ps);
            return null; // unreachable
        } finally {
            close(ps);
        }
    }

    protected Object execute(PreparedStatement ps) throws SQLException {
        if (resultSetHandler == null) {
            throw new IllegalStateException("The resultSetHandler should not be null!");
        }
        ResultSet resultSet = null;
        try {
            resultSet = createResultSet(ps);
            return resultSetHandler.handle(resultSet);
        } finally {
            close(resultSet);
        }
    }

    protected ResultSet createResultSet(PreparedStatement ps) throws SQLException {
        // /- - - - - - - - - - - - - - - - - - - - - - - - - - -
        // All select statements on DBFlute use this result set. 
        // - - - - - - - - - -/
        final ResultSet resultSet = ps.executeQuery();
        if (!isUseFunctionalResultSet()) {
            return resultSet;
        }
        final FetchBean selbean = FetchAssistContext.getFetchBeanOnThread();
        final TnFetchAssistResultSet wrapper;
        if (OutsideSqlContext.isExistOutsideSqlContextOnThread()) {
            final OutsideSqlContext context = OutsideSqlContext.getOutsideSqlContextOnThread();
            final boolean offsetByCursorForcedly = context.isOffsetByCursorForcedly();
            final boolean limitByCursorForcedly = context.isLimitByCursorForcedly();
            wrapper = createFunctionalResultSet(resultSet, selbean, offsetByCursorForcedly, limitByCursorForcedly);
        } else {
            wrapper = createFunctionalResultSet(resultSet, selbean, false, false);
        }
        return wrapper;
    }

    protected boolean isUseFunctionalResultSet() {
        // for safety result
        final FetchBean fcbean = FetchAssistContext.getFetchBeanOnThread();
        if (fcbean != null && fcbean.getSafetyMaxResultSize() > 0) {
            return true; // priority one
        }

        final FetchNarrowingBean fnbean = FetchAssistContext.getFetchNarrowingBeanOnThread();
        if (fnbean != null && fnbean.isFetchNarrowingEffective()) {
            // for unsupported paging (ConditionBean)
            if (fnbean.isFetchNarrowingSkipStartIndexEffective() || fnbean.isFetchNarrowingLoopCountEffective()) {
                return true; // priority two
            }

            // for auto paging (OutsideSql)
            if (OutsideSqlContext.isExistOutsideSqlContextOnThread()) {
                final OutsideSqlContext outsideSqlContext = OutsideSqlContext.getOutsideSqlContextOnThread();
                if (outsideSqlContext.isOffsetByCursorForcedly() || outsideSqlContext.isLimitByCursorForcedly()) {
                    return true; // priority three
                }
            }
        }

        return false;
    }

    protected TnFetchAssistResultSet createFunctionalResultSet(ResultSet resultSet, FetchBean fcbean,
            boolean offsetByCursorForcedly, boolean limitByCursorForcedly) {
        return new TnFetchAssistResultSet(resultSet, fcbean, offsetByCursorForcedly, limitByCursorForcedly);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public TnResultSetHandler getResultSetHandler() {
        return resultSetHandler;
    }

    public void setResultSetHandler(TnResultSetHandler resultSetHandler) {
        this.resultSetHandler = resultSetHandler;
    }
}
