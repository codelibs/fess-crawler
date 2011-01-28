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
package org.seasar.robot.dbflute.bhv.outsidesql;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.bhv.core.BehaviorCommand;
import org.seasar.robot.dbflute.bhv.core.BehaviorCommandInvoker;
import org.seasar.robot.dbflute.bhv.core.command.OutsideSqlSelectCursorCommand;
import org.seasar.robot.dbflute.bhv.outsidesql.factory.OutsideSqlContextFactory;
import org.seasar.robot.dbflute.bhv.outsidesql.factory.OutsideSqlExecutorFactory;
import org.seasar.robot.dbflute.jdbc.CursorHandler;
import org.seasar.robot.dbflute.jdbc.StatementConfig;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlFilter;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlOption;

/**
 * The cursor executor of outside-SQL.
 * @param <PARAMETER_BEAN> The type of parameter-bean.
 * @author jflute
 */
public class OutsideSqlCursorExecutor<PARAMETER_BEAN> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The invoker of behavior command. (NotNull) */
    protected final BehaviorCommandInvoker _behaviorCommandInvoker;

    /** The DB name of table. (NotNull) */
    protected final String _tableDbName;

    /** The current database definition. (NotNull) */
    protected DBDef _currentDBDef;

    /** The option of outside-SQL. (NotNull) */
    protected final OutsideSqlOption _outsideSqlOption;

    /** The factory of outside-SQL context. (NotNull) */
    protected final OutsideSqlContextFactory _outsideSqlContextFactory;

    /** The filter of outside-SQL. (NullAllowed) */
    protected final OutsideSqlFilter _outsideSqlFilter;

    /** The factory of outside-SQL executor. (NotNull) */
    protected final OutsideSqlExecutorFactory _outsideSqlExecutorFactory;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OutsideSqlCursorExecutor(BehaviorCommandInvoker behaviorCommandInvoker, String tableDbName,
            DBDef currentDBDef, OutsideSqlOption outsideSqlOption, OutsideSqlContextFactory outsideSqlContextFactory,
            OutsideSqlFilter outsideSqlFilter, OutsideSqlExecutorFactory outsideSqlExecutorFactory) {
        _behaviorCommandInvoker = behaviorCommandInvoker;
        _tableDbName = tableDbName;
        _currentDBDef = currentDBDef;
        _outsideSqlOption = outsideSqlOption;
        _outsideSqlContextFactory = outsideSqlContextFactory;
        _outsideSqlExecutorFactory = outsideSqlExecutorFactory;
        _outsideSqlFilter = outsideSqlFilter;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    /**
     * Select the cursor of the entity by outside-SQL.
     * <pre>
     * String path = MemberBhv.PATH_selectSimpleMember;
     * SimpleMemberPmb pmb = new SimpleMemberPmb();
     * pmb.setMemberName_PrefixSearch("S");
     * memberBhv.outsideSql().cursorHandling()
     *         .<span style="color: #FD4747">selectCursor</span>(path, pmb, new PurchaseSummaryMemberCursorHandler() {
     *     public void Object fetchCursor(PurchaseSummaryMemberCursor cursor) throws SQLException {
     *         while (cursor.next()) {
     *             Integer memberId = cursor.getMemberId();
     *             String memberName = cursor.getMemberName();
     *             ...
     *         }
     *         return null;
     *     }
     * });
     * </pre>
     * It needs to use type-safe-cursor instead of customize-entity.
     * The way to generate it is following:
     * <pre>
     * <span style="color: #3F7E5E">-- #df:entity#</span>
     * <span style="color: #3F7E5E">-- +cursor+</span>
     * </pre>
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (NullAllowed)
     * @param handler The handler of cursor. (NotNull)
     * @return The result object that the cursor handler returns. (NullAllowed)
     * @exception org.seasar.robot.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     */
    public Object selectCursor(String path, PARAMETER_BEAN pmb, CursorHandler handler) {
        return invoke(createSelectCursorCommand(path, pmb, handler));
    }

    // ===================================================================================
    //                                                                    Behavior Command
    //                                                                    ================
    protected BehaviorCommand<Object> createSelectCursorCommand(String path, PARAMETER_BEAN pmb, CursorHandler handler) {
        return xsetupCommand(newOutsideSqlSelectCursorCommand(), path, pmb, handler);
    }

    protected OutsideSqlSelectCursorCommand newOutsideSqlSelectCursorCommand() {
        return new OutsideSqlSelectCursorCommand();
    }

    protected OutsideSqlSelectCursorCommand xsetupCommand(OutsideSqlSelectCursorCommand cmd, String path,
            PARAMETER_BEAN pmb, CursorHandler handler) {
        cmd.setTableDbName(_tableDbName);
        _behaviorCommandInvoker.injectComponentProperty(cmd);
        cmd.setOutsideSqlPath(path);
        cmd.setParameterBean(pmb);
        cmd.setOutsideSqlOption(_outsideSqlOption);
        cmd.setCurrentDBDef(_currentDBDef);
        cmd.setOutsideSqlContextFactory(_outsideSqlContextFactory);
        cmd.setOutsideSqlFilter(_outsideSqlFilter);
        cmd.setCursorHandler(handler);
        return cmd;
    }

    /**
     * Invoke the command of behavior.
     * @param <RESULT> The type of result.
     * @param behaviorCommand The command of behavior. (NotNull)
     * @return The instance of result. (NullAllowed)
     */
    protected <RESULT> RESULT invoke(BehaviorCommand<RESULT> behaviorCommand) {
        return _behaviorCommandInvoker.invoke(behaviorCommand);
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    /**
     * Set up remove-block-comment for this outside-SQL.
     * @return this. (NotNull)
     */
    public OutsideSqlCursorExecutor<PARAMETER_BEAN> removeBlockComment() {
        _outsideSqlOption.removeBlockComment();
        return this;
    }

    /**
     * Set up remove-line-comment for this outside-SQL.
     * @return this. (NotNull)
     */
    public OutsideSqlCursorExecutor<PARAMETER_BEAN> removeLineComment() {
        _outsideSqlOption.removeLineComment();
        return this;
    }

    /**
     * Set up format-SQL for this outside-SQL. <br />
     * (For example, empty lines removed)
     * @return this. (NotNull)
     */
    public OutsideSqlCursorExecutor<PARAMETER_BEAN> formatSql() {
        _outsideSqlOption.formatSql();
        return this;
    }

    /**
     * Configure statement JDBC options. (For example, queryTimeout, fetchSize, ...)
     * @param statementConfig The configuration of statement. (NullAllowed)
     * @return this. (NotNull)
     */
    public OutsideSqlCursorExecutor<PARAMETER_BEAN> configure(StatementConfig statementConfig) {
        _outsideSqlOption.setStatementConfig(statementConfig);
        return this;
    }
}
