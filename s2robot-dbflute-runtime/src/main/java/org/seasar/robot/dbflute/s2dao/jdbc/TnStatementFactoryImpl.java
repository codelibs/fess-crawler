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
package org.seasar.robot.dbflute.s2dao.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.jdbc.StatementConfig;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.robot.dbflute.resource.SQLExceptionHandler;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnStatementFactoryImpl implements StatementFactory {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log-instance. */
    private static final Log _log = LogFactory.getLog(TnStatementFactoryImpl.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected StatementConfig defaultStatementConfig;
    protected boolean internalDebug;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnStatementFactoryImpl() {
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    public PreparedStatement createPreparedStatement(Connection conn, String sql) {
        try {
            final StatementConfig config = findStatementConfigOnThread();
            final int resultSetType;
            if (config != null && config.hasResultSetType()) {
                resultSetType = config.getResultSetType();
            } else if (defaultStatementConfig != null && defaultStatementConfig.hasResultSetType()) {
                resultSetType = defaultStatementConfig.getResultSetType();
            } else {
                resultSetType = java.sql.ResultSet.TYPE_FORWARD_ONLY;
            }
            final int resultSetConcurrency = java.sql.ResultSet.CONCUR_READ_ONLY;
            if (internalDebug) {
                _log.debug("...Creating prepareStatement(sql, " + resultSetType + ", " + resultSetConcurrency + ")");
            }
            final PreparedStatement ps = conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
            if (config != null && config.hasStatementOptions()) {
                if (internalDebug) {
                    _log.debug("...Setting statement config as request: " + config);
                }
                reflectStatementOptions(config, ps);
            } else {
                reflectDefaultOptionsToStatementIfNeeds(ps);
            }
            return ps;
        } catch (SQLException e) {
            handleSQLException(e, null);
            return null; // unreachable
        }
    }

    public CallableStatement createCallableStatement(Connection conn, String sql) {
        return prepareCall(conn, sql);
    }

    protected StatementConfig findStatementConfigOnThread() {
        final StatementConfig config;
        if (ConditionBeanContext.isExistConditionBeanOnThread()) {
            final ConditionBean cb = ConditionBeanContext.getConditionBeanOnThread();
            config = cb.getStatementConfig();
        } else if (OutsideSqlContext.isExistOutsideSqlContextOnThread()) {
            final OutsideSqlContext context = OutsideSqlContext.getOutsideSqlContextOnThread();
            config = context.getStatementConfig();
        } else {
            config = null;
        }
        return config;
    }

    protected void reflectDefaultOptionsToStatementIfNeeds(PreparedStatement ps) {
        if (defaultStatementConfig != null && defaultStatementConfig.hasStatementOptions()) {
            if (internalDebug) {
                _log.debug("...Setting statement config as default: " + defaultStatementConfig);
            }
            reflectStatementOptions(defaultStatementConfig, ps);
            return;
        }
    }

    protected void reflectStatementOptions(StatementConfig config, PreparedStatement ps) {
        try {
            if (config.hasQueryTimeout()) {
                ps.setQueryTimeout(config.getQueryTimeout());
            }
            if (config.hasFetchSize()) {
                ps.setFetchSize(config.getFetchSize());
            }
            if (config.hasMaxRows()) {
                ps.setMaxRows(config.getMaxRows());
            }
        } catch (SQLException e) {
            handleSQLException(e, ps);
        }
    }

    protected CallableStatement prepareCall(Connection conn, String sql) {
        try {
            return conn.prepareCall(sql);
        } catch (SQLException e) {
            handleSQLException(e, null);
            return null;// unreachable
        }
    }

    protected void handleSQLException(SQLException e, Statement statement) {
        new SQLExceptionHandler().handleSQLException(e, statement);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDefaultStatementConfig(StatementConfig defaultStatementConfig) {
        this.defaultStatementConfig = defaultStatementConfig;
    }

    public void setInternalDebug(boolean internalDebug) {
        this.internalDebug = internalDebug;
    }
}
