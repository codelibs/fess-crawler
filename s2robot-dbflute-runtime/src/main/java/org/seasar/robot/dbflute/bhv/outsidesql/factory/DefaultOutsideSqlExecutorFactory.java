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
package org.seasar.robot.dbflute.bhv.outsidesql.factory;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.bhv.core.BehaviorCommandInvoker;
import org.seasar.robot.dbflute.bhv.outsidesql.OutsideSqlBasicExecutor;
import org.seasar.robot.dbflute.bhv.outsidesql.OutsideSqlCursorExecutor;
import org.seasar.robot.dbflute.bhv.outsidesql.OutsideSqlEntityExecutor;
import org.seasar.robot.dbflute.bhv.outsidesql.OutsideSqlPagingExecutor;
import org.seasar.robot.dbflute.jdbc.StatementConfig;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlFilter;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlOption;

/**
 * @author jflute
 */
public class DefaultOutsideSqlExecutorFactory implements OutsideSqlExecutorFactory {

    /**
     * {@inheritDoc}
     */
    public OutsideSqlBasicExecutor createBasic(BehaviorCommandInvoker behaviorCommandInvoker, String tableDbName,
            DBDef currentDBDef, StatementConfig defaultStatementConfig, OutsideSqlOption outsideSqlOption) {
        final OutsideSqlContextFactory outsideSqlContextFactory = createOutsideSqlContextFactory();
        final OutsideSqlFilter outsideSqlFilter = createOutsideSqlExecutionFilter();
        return new OutsideSqlBasicExecutor(behaviorCommandInvoker, tableDbName, currentDBDef, defaultStatementConfig,
                outsideSqlOption, outsideSqlContextFactory, outsideSqlFilter, this);
    }

    /**
     * {@inheritDoc}
     */
    public <PARAMETER_BEAN> OutsideSqlCursorExecutor<PARAMETER_BEAN> createCursor(
            BehaviorCommandInvoker behaviorCommandInvoker, String tableDbName, DBDef currentDBDef,
            OutsideSqlOption outsideSqlOption) {
        final OutsideSqlContextFactory outsideSqlContextFactory = createOutsideSqlContextFactory();
        final OutsideSqlFilter outsideSqlFilter = createOutsideSqlExecutionFilter();
        return new OutsideSqlCursorExecutor<PARAMETER_BEAN>(behaviorCommandInvoker, tableDbName, currentDBDef,
                outsideSqlOption, outsideSqlContextFactory, outsideSqlFilter, this);
    }

    /**
     * Create the factory of outside-SQL context. <br />
     * This is the very point for an extension of the outside-SQL context. 
     * @return The instance of the factory. (NotNull)
     */
    protected OutsideSqlContextFactory createOutsideSqlContextFactory() { // extension point
        return new DefaultOutsideSqlContextFactory();
    }

    /**
     * Create the filter of outside-SQL. <br />
     * This is the very point for an extension of the outside-SQL filtering. 
     * @return The instance of the filter. (NullAllowed)
     */
    protected OutsideSqlFilter createOutsideSqlExecutionFilter() { // extension point
        return null; // as default (no filter)
    }

    /**
     * {@inheritDoc}
     */
    public <PARAMETER_BEAN> OutsideSqlEntityExecutor<PARAMETER_BEAN> createEntity(
            BehaviorCommandInvoker behaviorCommandInvoker, String tableDbName, DBDef currentDBDef,
            StatementConfig defaultStatementConfig, OutsideSqlOption outsideSqlOption) {
        return new OutsideSqlEntityExecutor<PARAMETER_BEAN>(behaviorCommandInvoker, tableDbName, currentDBDef,
                defaultStatementConfig, outsideSqlOption, this);
    }

    /**
     * {@inheritDoc}
     */
    public OutsideSqlPagingExecutor createPaging(BehaviorCommandInvoker behaviorCommandInvoker, String tableDbName,
            DBDef currentDBDef, StatementConfig defaultStatementConfig, OutsideSqlOption outsideSqlOption) {
        return new OutsideSqlPagingExecutor(behaviorCommandInvoker, tableDbName, currentDBDef, defaultStatementConfig,
                outsideSqlOption, this);
    }
}
