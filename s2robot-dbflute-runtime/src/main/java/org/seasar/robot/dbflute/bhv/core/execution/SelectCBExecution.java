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

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnBasicParameterHandler;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnBasicSelectHandler;
import org.seasar.robot.dbflute.twowaysql.node.Node;

/**
 * The SQL execution of select by condition-bean. <br />
 * The first element of arguments should be condition-bean (and not null).
 * @author jflute
 */
public class SelectCBExecution extends AbstractFixedArgExecution {

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
     * @param resultSetHandler The handler of result set. (NotNull)
     */
    public SelectCBExecution(DataSource dataSource, StatementFactory statementFactory,
            Map<String, Class<?>> argNameTypeMap, TnResultSetHandler resultSetHandler) {
        super(dataSource, statementFactory, argNameTypeMap);
        assertObjectNotNull("resultSetHandler", resultSetHandler);
        _resultSetHandler = resultSetHandler;
    }

    // ===================================================================================
    //                                                                            Resource
    //                                                                            ========
    @Override
    protected Node getRootNode(Object[] args) {
        return analyzeTwoWaySql(extractTwoWaySql(args)); // dynamic analysis
    }

    protected String extractTwoWaySql(Object[] args) {
        assertArgsValid(args);
        final Object firstElement = args[0];
        assertObjectNotNull("args[0]", firstElement);
        assertFirstElementConditionBean(firstElement);
        final ConditionBean cb = (ConditionBean) firstElement;
        return cb.getSqlClause().getClause();
    }

    protected void assertArgsValid(Object[] args) {
        if (args == null) {
            String msg = "The argument 'args' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (args.length == 0) {
            String msg = "The argument 'args' should not be empty.";
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertFirstElementConditionBean(Object firstElement) {
        if (!(firstElement instanceof ConditionBean)) {
            String msg = "The first element of 'args' should be condition-bean: " + firstElement.getClass();
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                             Handler
    //                                                                             =======
    @Override
    protected TnBasicParameterHandler newBasicParameterHandler(String executedSql) {
        return new TnBasicSelectHandler(_dataSource, executedSql, _resultSetHandler, _statementFactory);
    }

    // ===================================================================================
    //                                                                        SQL Handling
    //                                                                        ============
    @Override
    protected boolean isBlockNullParameter() {
        return true; // because the SQL is select
    }
}
