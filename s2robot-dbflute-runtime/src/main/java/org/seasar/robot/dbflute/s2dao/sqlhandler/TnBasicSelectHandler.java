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

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.robot.dbflute.cbean.FetchAssistContext;
import org.seasar.robot.dbflute.cbean.FetchNarrowingBean;
import org.seasar.robot.dbflute.jdbc.FetchBean;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.jdbc.TnFetchAssistResultSet;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnBasicSelectHandler extends TnBasicParameterHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance for internal debug. (XLog is used instead for execute-status log) */
    private static final Log _log = LogFactory.getLog(TnBasicSelectHandler.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final TnResultSetHandler _resultSetHandler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnBasicSelectHandler(DataSource dataSource, String sql, TnResultSetHandler resultSetHandler,
            StatementFactory statementFactory) {
        super(dataSource, statementFactory, sql);
        _resultSetHandler = resultSetHandler;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected Object doExecute(Connection conn, Object[] args, Class<?>[] argTypes) {
        logSql(args, argTypes);
        PreparedStatement ps = null;
        try {
            ps = prepareStatement(conn);
            bindArgs(conn, ps, args, argTypes);
            return queryResult(ps);
        } catch (SQLException e) {
            handleSQLException(e, ps);
            return null; // unreachable
        } finally {
            close(ps);
        }
    }

    protected Object queryResult(PreparedStatement ps) throws SQLException {
        ResultSet rs = null;
        try {
            rs = executeQuery(ps);
            return _resultSetHandler.handle(rs);
        } finally {
            close(rs);
        }
    }

    protected ResultSet executeQuery(PreparedStatement ps) throws SQLException {
        // /- - - - - - - - - - - - - - - - - - - - - - - - - - -
        // All select statements on DBFlute use this result set. 
        // - - - - - - - - - -/
        final ResultSet rs = ps.executeQuery();
        if (!isUseFunctionalResultSet()) {
            return rs;
        }
        if (isInternalDebugEnabled()) {
            _log.debug("...Wrapping result set by functional one");
        }
        final FetchBean selbean = FetchAssistContext.getFetchBeanOnThread();
        final TnFetchAssistResultSet wrapper;
        if (OutsideSqlContext.isExistOutsideSqlContextOnThread()) {
            final OutsideSqlContext context = OutsideSqlContext.getOutsideSqlContextOnThread();
            final boolean offsetByCursorForcedly = context.isOffsetByCursorForcedly();
            final boolean limitByCursorForcedly = context.isLimitByCursorForcedly();
            wrapper = createFunctionalResultSet(rs, selbean, offsetByCursorForcedly, limitByCursorForcedly);
        } else {
            wrapper = createFunctionalResultSet(rs, selbean, false, false);
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

    protected TnFetchAssistResultSet createFunctionalResultSet(ResultSet rs, FetchBean fcbean, boolean offset,
            boolean limit) {
        return new TnFetchAssistResultSet(rs, fcbean, offset, limit);
    }

    // ===================================================================================
    //                                                                      Internal Debug
    //                                                                      ==============
    private boolean isInternalDebugEnabled() { // because log instance is private
        return ResourceContext.isInternalDebug() && _log.isDebugEnabled();
    }
}
