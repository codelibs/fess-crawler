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
import org.seasar.robot.dbflute.outsidesql.OutsideSqlOption;

/**
 * @author jflute
 */
public interface OutsideSqlExecutorFactory {

    /**
     * Create the basic executor of outside-SQL.
     * @param behaviorCommandInvoker The invoker of behavior command. (NotNull)
     * @param tableDbName The DB name of table. (NotNull)
     * @param currentDBDef The definition of current DBMS. (NotNull)
     * @param defaultStatementConfig The default configuration of statement. (NullAllowed)
     * @param outsideSqlOption The option of outsideSql. (NullAllowed: if null, means for an entry instance)
     * @return The instance of executor. (NotNull)
     */
    OutsideSqlBasicExecutor createBasic(BehaviorCommandInvoker behaviorCommandInvoker, String tableDbName,
            DBDef currentDBDef, StatementConfig defaultStatementConfig, OutsideSqlOption outsideSqlOption);

    /**
     * Create the cursor executor of outside-SQL.
     * @param <PARAMETER_BEAN> The type of parameter-bean.
     * @param behaviorCommandInvoker The invoker of behavior command. (NotNull)
     * @param tableDbName The DB name of table. (NotNull)
     * @param currentDBDef The definition of current DBMS. (NotNull)
     * @param outsideSqlOption The option of outsideSql. (NotNull)
     * @return The instance of executor. (NotNull)
     */
    <PARAMETER_BEAN> OutsideSqlCursorExecutor<PARAMETER_BEAN> createCursor(
            BehaviorCommandInvoker behaviorCommandInvoker, String tableDbName, DBDef currentDBDef,
            OutsideSqlOption outsideSqlOption);

    /**
     * Create the entity executor of outside-SQL.
     * @param <PARAMETER_BEAN> The type of parameter-bean.
     * @param behaviorCommandInvoker The invoker of behavior command. (NotNull)
     * @param tableDbName The DB name of table. (NotNull)
     * @param currentDBDef The definition of DBMS. (NotNull)
     * @param defaultStatementConfig The default configuration of statement. (NullAllowed)
     * @param outsideSqlOption The option of outsideSql. (NotNull)
     * @return The instance of executor. (NotNull)
     */
    <PARAMETER_BEAN> OutsideSqlEntityExecutor<PARAMETER_BEAN> createEntity(
            BehaviorCommandInvoker behaviorCommandInvoker, String tableDbName, DBDef currentDBDef,
            StatementConfig defaultStatementConfig, OutsideSqlOption outsideSqlOption);

    /**
     * Create the paging executor of outside-SQL.
     * @param behaviorCommandInvoker The invoker of behavior command. (NotNull)
     * @param tableDbName The DB name of table. (NotNull)
     * @param currentDBDef The definition of current DBMS. (NotNull)
     * @param defaultStatementConfig The default configuration of statement. (NullAllowed)
     * @param outsideSqlOption The option of outsideSql. (NotNull)
     * @return The instance of executor. (NotNull)
     */
    OutsideSqlPagingExecutor createPaging(BehaviorCommandInvoker behaviorCommandInvoker, String tableDbName,
            DBDef currentDBDef, StatementConfig defaultStatementConfig, OutsideSqlOption outsideSqlOption);
}
