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
package org.seasar.robot.dbflute.bhv.core.execution;

import java.util.Map;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnBasicParameterHandler;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnBasicSelectHandler;

/**
 * The SQL execution of simple select by fixed SQL.
 * @author jflute
 */
public class SelectSimpleExecution extends AbstractFixedSqlExecution {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final TnResultSetHandler _resultSetHandler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param dataSource The data source for a database connection. (NotNull)
     * @param statementFactory The factory of statement. (NotNull)
     * @param argNameTypeMap The map of names and types for arguments. (NotNull)
     * @param twoWaySql The SQL string as 2Way-SQL. (NotNull)
     * @param resultSetHandler The handler of result set. (NotNull)
     */
    public SelectSimpleExecution(DataSource dataSource, StatementFactory statementFactory,
            Map<String, Class<?>> argNameTypeMap, String twoWaySql, TnResultSetHandler resultSetHandler) {
        super(dataSource, statementFactory, argNameTypeMap, twoWaySql);
        assertObjectNotNull("resultSetHandler", resultSetHandler);
        _resultSetHandler = resultSetHandler;
    }

    // ===================================================================================
    //                                                                             Handler
    //                                                                             =======
    @Override
    protected TnBasicParameterHandler newBasicParameterHandler(String sql) {
        return new TnBasicSelectHandler(_dataSource, sql, _resultSetHandler, _statementFactory);
    }

    // ===================================================================================
    //                                                                        SQL Handling
    //                                                                        ============
    @Override
    protected boolean isBlockNullParameter() {
        return true; // because the SQL is select
    }
}
