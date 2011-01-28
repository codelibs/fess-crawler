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

import java.util.List;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.bhv.core.BehaviorCommand;
import org.seasar.robot.dbflute.bhv.core.BehaviorCommandInvoker;
import org.seasar.robot.dbflute.bhv.core.command.AbstractOutsideSqlCommand;
import org.seasar.robot.dbflute.bhv.core.command.OutsideSqlCallCommand;
import org.seasar.robot.dbflute.bhv.core.command.OutsideSqlExecuteCommand;
import org.seasar.robot.dbflute.bhv.core.command.OutsideSqlSelectListCommand;
import org.seasar.robot.dbflute.bhv.outsidesql.factory.OutsideSqlContextFactory;
import org.seasar.robot.dbflute.bhv.outsidesql.factory.OutsideSqlExecutorFactory;
import org.seasar.robot.dbflute.cbean.ListResultBean;
import org.seasar.robot.dbflute.cbean.ResultBeanBuilder;
import org.seasar.robot.dbflute.exception.FetchingOverSafetySizeException;
import org.seasar.robot.dbflute.exception.thrower.BehaviorExceptionThrower;
import org.seasar.robot.dbflute.jdbc.FetchBean;
import org.seasar.robot.dbflute.jdbc.StatementConfig;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlFilter;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.robot.dbflute.outsidesql.ProcedurePmb;

/**
 * The executor of outside-SQL.
 * <pre>
 * {Basic}
 *   o selectList()
 *   o execute()
 *   o call()
 * 
 * {Entity}
 *   o entityHandling().selectEntity()
 *   o entityHandling().selectEntityWithDeletedCheck()
 * 
 * {Paging}
 *   o autoPaging().selectList()
 *   o autoPaging().selectPage()
 *   o manualPaging().selectList()
 *   o manualPaging().selectPage()
 * 
 * {Cursor}
 *   o cursorHandling().selectCursor()
 * 
 * {Option}
 *   o dynamicBinding().selectList()
 *   o removeBlockComment().selectList()
 *   o removeLineComment().selectList()
 *   o formatSql().selectList()
 * </pre>
 * @author jflute
 */
public class OutsideSqlBasicExecutor {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The invoker of behavior command. (NotNull) */
    protected final BehaviorCommandInvoker _behaviorCommandInvoker;

    /** Table DB name. (NotNull) */
    protected final String _tableDbName;

    /** The current database definition. (NotNull) */
    protected final DBDef _currentDBDef;

    /** The default configuration of statement. (NullAllowed) */
    protected final StatementConfig _defaultStatementConfig;

    /** The option of outside-SQL. (NotNull) */
    protected final OutsideSqlOption _outsideSqlOption;

    /** The factory of outside-SQL context. (NotNull) */
    protected final OutsideSqlContextFactory _outsideSqlContextFactory;

    /** The filter of outside-SQL. (NullAllowed) */
    protected final OutsideSqlFilter _outsideSqlFilter;

    /** The factory of outside-SQL executor. (NotNull) */
    protected final OutsideSqlExecutorFactory _outsideSqlExecutorFactory;

    /** Does it remove block comments from the SQL? */
    protected boolean _removeBlockComment;

    /** Does it remove line comments from the SQL? */
    protected boolean _removeLineComment;

    /** Does it format the SQL? */
    protected boolean _formatSql;

    /** The configuration of statement. (NullAllowed) */
    protected StatementConfig _statementConfig;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OutsideSqlBasicExecutor(BehaviorCommandInvoker behaviorCommandInvoker, String tableDbName,
            DBDef currentDBDef, StatementConfig defaultStatementConfig, OutsideSqlOption outsideSqlOption,
            OutsideSqlContextFactory outsideSqlContextFactory, OutsideSqlFilter outsideSqlFilter,
            OutsideSqlExecutorFactory outsideSqlExecutorFactory) {
        _behaviorCommandInvoker = behaviorCommandInvoker;
        _tableDbName = tableDbName;
        _currentDBDef = currentDBDef;
        _defaultStatementConfig = defaultStatementConfig;
        if (outsideSqlOption != null) { // for nested call (inherits options)
            _outsideSqlOption = outsideSqlOption;
        } else { // for entry call (initializes an option instance)
            _outsideSqlOption = new OutsideSqlOption();
            _outsideSqlOption.setTableDbName(tableDbName); // as information
        }
        _outsideSqlContextFactory = outsideSqlContextFactory;
        _outsideSqlFilter = outsideSqlFilter;
        _outsideSqlExecutorFactory = outsideSqlExecutorFactory;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    /**
     * Select the list of the entity by the outsideSql.
     * <pre>
     * String path = MemberBhv.PATH_selectSimpleMember;
     * SimpleMemberPmb pmb = new SimpleMemberPmb();
     * pmb.setMemberName_PrefixSearch("S");
     * Class&lt;SimpleMember&gt; entityType = SimpleMember.class;
     * ListResultBean&lt;SimpleMember&gt; memberList
     *     = memberBhv.outsideSql().<span style="color: #FD4747">selectList</span>(path, pmb, entityType);
     * for (SimpleMember member : memberList) {
     *     ... = member.get...();
     * }
     * </pre>
     * It needs to use customize-entity and parameter-bean.
     * The way to generate them is following:
     * <pre>
     * -- #df:entity#
     * -- !df:pmb!
     * -- !!Integer memberId!!
     * -- !!String memberName!!
     * -- !!...!!
     * </pre>
     * @param <ENTITY> The type of entity for element.
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (NullAllowed)
     * @param entityType The element type of entity. (NotNull)
     * @return The result bean of selected list. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.OutsideSqlNotFoundException When the outsideSql is not found.
     * @exception org.seasar.robot.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public <ENTITY> ListResultBean<ENTITY> selectList(String path, Object pmb, Class<ENTITY> entityType) {
        return doSelectList(path, pmb, entityType);
    }

    protected <ENTITY> ListResultBean<ENTITY> doSelectList(String path, Object pmb, Class<ENTITY> entityType) {
        try {
            List<ENTITY> resultList = invoke(createSelectListCommand(path, pmb, entityType));
            return createListResultBean(resultList);
        } catch (FetchingOverSafetySizeException e) { // occurs only when fetch-bean
            throwDangerousResultSizeException(pmb, e);
            return null; // unreachable
        }
    }

    protected <ENTITY> ListResultBean<ENTITY> createListResultBean(List<ENTITY> selectedList) {
        return new ResultBeanBuilder<ENTITY>(_tableDbName).buildListResultBean(selectedList);
    }

    protected void throwDangerousResultSizeException(Object pmb, FetchingOverSafetySizeException e) {
        if (!(pmb instanceof FetchBean)) { // no way
            String msg = "The exception should be thrown only when the parameter-bean is instance of fetch-bean:";
            msg = msg + " pmb=" + (pmb != null ? pmb.getClass().getName() : null);
            throw new IllegalStateException(msg, e);
        }
        createBhvExThrower().throwDangerousResultSizeException((FetchBean) pmb, e);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    /**
     * Execute the outsideSql. {insert, update, delete, etc...}
     * <pre>
     * String path = MemberBhv.PATH_selectSimpleMember;
     * SimpleMemberPmb pmb = new SimpleMemberPmb();
     * pmb.setMemberId(3);
     * int count = memberBhv.outsideSql().<span style="color: #FD4747">execute</span>(path, pmb);
     * </pre>
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (NullAllowed)
     * @return The count of execution.
     * @exception org.seasar.robot.dbflute.exception.OutsideSqlNotFoundException When the outsideSql is not found.
     */
    public int execute(String path, Object pmb) {
        return invoke(createExecuteCommand(path, pmb));
    }

    // [DBFlute-0.7.5]
    // ===================================================================================
    //                                                                      Procedure Call
    //                                                                      ==============
    /**
     * Call the procedure.
     * <pre>
     * SpInOutParameterPmb pmb = new SpInOutParameterPmb();
     * pmb.setVInVarchar("foo");
     * pmb.setVInOutVarchar("bar");
     * memberBhv.outsideSql().<span style="color: #FD4747">call</span>(pmb);
     * String outVar = pmb.getVOutVarchar();
     * </pre>
     * It needs to use parameter-bean for procedure (ProcedurePmb).
     * The way to generate is to set the option of DBFlute property and execute Sql2Entity.
     * @param pmb The parameter-bean for procedure. (NotNull)
     */
    public void call(ProcedurePmb pmb) {
        if (pmb == null) {
            throw new IllegalArgumentException("The argument of call() 'pmb' should not be null!");
        }
        try {
            invoke(createCallCommand(pmb.getProcedureName(), pmb));
        } catch (FetchingOverSafetySizeException e) { // occurs only when fetch-bean
            throwDangerousResultSizeException(pmb, e);
        }
    }

    // ===================================================================================
    //                                                                    Behavior Command
    //                                                                    ================
    protected <ENTITY> BehaviorCommand<List<ENTITY>> createSelectListCommand(String path, Object pmb,
            Class<ENTITY> entityType) {
        final OutsideSqlSelectListCommand<ENTITY> cmd;
        {
            final OutsideSqlSelectListCommand<ENTITY> newed = newOutsideSqlSelectListCommand();
            cmd = xsetupCommand(newed, path, pmb); // has a little generic headache...
        }
        cmd.setEntityType(entityType);
        return cmd;
    }

    protected <ENTITY> OutsideSqlSelectListCommand<ENTITY> newOutsideSqlSelectListCommand() {
        return new OutsideSqlSelectListCommand<ENTITY>();
    }

    protected BehaviorCommand<Integer> createExecuteCommand(String path, Object pmb) {
        return xsetupCommand(newOutsideSqlExecuteCommand(), path, pmb);
    }

    protected OutsideSqlExecuteCommand newOutsideSqlExecuteCommand() {
        return new OutsideSqlExecuteCommand();
    }

    protected BehaviorCommand<Void> createCallCommand(String path, Object pmb) {
        return xsetupCommand(newOutsideSqlCallCommand(), path, pmb);
    }

    protected OutsideSqlCallCommand newOutsideSqlCallCommand() {
        return new OutsideSqlCallCommand();
    }

    protected <COMMAND extends AbstractOutsideSqlCommand<?>> COMMAND xsetupCommand(COMMAND cmd, String path, Object pmb) {
        cmd.setTableDbName(_tableDbName);
        _behaviorCommandInvoker.injectComponentProperty(cmd);
        cmd.setOutsideSqlPath(path);
        cmd.setParameterBean(pmb);
        cmd.setOutsideSqlOption(_outsideSqlOption);
        cmd.setCurrentDBDef(_currentDBDef);
        cmd.setOutsideSqlContextFactory(_outsideSqlContextFactory);
        cmd.setOutsideSqlFilter(_outsideSqlFilter);
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
    //                                                                              Paging
    //                                                                              ======
    /**
     * Prepare the paging as manualPaging.
     * <pre>
     * memberBhv.outsideSql().<span style="color: #FD4747">manualPaging()</span>.selectPage(path, pmb, SimpleMember.class);
     * </pre>
     * If you call this, you need to write paging condition on your SQL.
     * <pre>
     * ex) MySQL
     * select member.MEMBER_ID, member...
     *   from Member member
     *  where ...
     *  order by ...
     *  limit 40, 20 <span style="color: #3F7E5E">-- is necessary!</span>
     * </pre>
     * @return The executor of paging that the paging mode is manual. (NotNull)
     */
    public OutsideSqlPagingExecutor manualPaging() {
        _outsideSqlOption.manualPaging();
        return createOutsideSqlPagingExecutor();
    }

    /**
     * Prepare the paging as autoPaging.
     * <pre>
     * memberBhv.outsideSql().<span style="color: #FD4747">autoPaging()</span>.selectPage(path, pmb, SimpleMember.class);
     * </pre>
     * If you call this, you don't need to write paging condition on your SQL.
     * <pre>
     * ex) MySQL
     * select member.MEMBER_ID, member...
     *   from Member member
     *  where ...
     *  order by ...
     * <span style="color: #3F7E5E">-- limit 40, 20 -- is unnecessary!</span>
     * </pre>
     * @return The executor of paging that the paging mode is auto. (NotNull)
     */
    public OutsideSqlPagingExecutor autoPaging() {
        _outsideSqlOption.autoPaging();
        return createOutsideSqlPagingExecutor();
    }

    protected OutsideSqlPagingExecutor createOutsideSqlPagingExecutor() {
        return _outsideSqlExecutorFactory.createPaging(_behaviorCommandInvoker, _tableDbName, _currentDBDef,
                _defaultStatementConfig, _outsideSqlOption);
    }

    // ===================================================================================
    //                                                                              Cursor
    //                                                                              ======
    /**
     * Prepare cursor handling.
     * <pre>
     * memberBhv.outsideSql().<span style="color: #FD4747">cursorHandling()</span>.selectCursor(path, pmb, handler);
     * </pre>
     * @return The cursor executor of outsideSql. (NotNull)
     */
    public OutsideSqlCursorExecutor<Object> cursorHandling() {
        return createOutsideSqlCursorExecutor();
    }

    protected OutsideSqlCursorExecutor<Object> createOutsideSqlCursorExecutor() {
        return _outsideSqlExecutorFactory.createCursor(_behaviorCommandInvoker, _tableDbName, _currentDBDef,
                _outsideSqlOption);
    }

    /**
     * Prepare entity handling.
     * <pre>
     * memberBhv.outsideSql().<span style="color: #FD4747">entityHandling()</span>.selectEntityWithDeletedCheck(path, pmb, SimpleMember.class);
     * </pre>
     * @return The cursor executor of outsideSql. (NotNull)
     */
    public OutsideSqlEntityExecutor<Object> entityHandling() {
        return createOutsideSqlEntityExecutor();
    }

    protected OutsideSqlEntityExecutor<Object> createOutsideSqlEntityExecutor() {
        return _outsideSqlExecutorFactory.createEntity(_behaviorCommandInvoker, _tableDbName, _currentDBDef,
                _defaultStatementConfig, _outsideSqlOption);
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    // -----------------------------------------------------
    //                                       Remove from SQL
    //                                       ---------------
    /**
     * Set up remove-block-comment for this outsideSql.
     * @return this. (NotNull)
     */
    public OutsideSqlBasicExecutor removeBlockComment() {
        _outsideSqlOption.removeBlockComment();
        return this;
    }

    /**
     * Set up remove-line-comment for this outsideSql.
     * @return this. (NotNull)
     */
    public OutsideSqlBasicExecutor removeLineComment() {
        _outsideSqlOption.removeLineComment();
        return this;
    }

    // -----------------------------------------------------
    //                                            Format SQL
    //                                            ----------
    /**
     * Set up format-SQL for this outsideSql. <br />
     * (For example, empty lines removed)
     * @return this. (NotNull)
     */
    public OutsideSqlBasicExecutor formatSql() {
        _outsideSqlOption.formatSql();
        return this;
    }

    // -----------------------------------------------------
    //                                      Statement Config
    //                                      ----------------
    /**
     * Configure statement JDBC options. (For example, queryTimeout, fetchSize, ...)
     * @param statementConfig The configuration of statement. (NullAllowed)
     * @return this. (NotNull)
     */
    public OutsideSqlBasicExecutor configure(StatementConfig statementConfig) {
        _outsideSqlOption.setStatementConfig(statementConfig);
        return this;
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected BehaviorExceptionThrower createBhvExThrower() {
        return _behaviorCommandInvoker.createBehaviorExceptionThrower();
    }
}
