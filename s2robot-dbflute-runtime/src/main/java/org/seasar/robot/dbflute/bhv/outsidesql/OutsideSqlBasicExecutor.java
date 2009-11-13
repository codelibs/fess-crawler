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
package org.seasar.robot.dbflute.bhv.outsidesql;

import java.util.List;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.bhv.core.BehaviorCommand;
import org.seasar.robot.dbflute.bhv.core.BehaviorCommandInvoker;
import org.seasar.robot.dbflute.bhv.core.command.AbstractOutsideSqlCommand;
import org.seasar.robot.dbflute.bhv.core.command.OutsideSqlCallCommand;
import org.seasar.robot.dbflute.bhv.core.command.OutsideSqlExecuteCommand;
import org.seasar.robot.dbflute.bhv.core.command.OutsideSqlSelectListCommand;
import org.seasar.robot.dbflute.cbean.ListResultBean;
import org.seasar.robot.dbflute.cbean.ResultBeanBuilder;
import org.seasar.robot.dbflute.jdbc.StatementConfig;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.robot.dbflute.outsidesql.ProcedurePmb;


/**
 * The executor of outside-SQL. <br />
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
 * {Cursor}
 *   o cursorHandling().selectCursor()
 * 
 * {Paging}
 *   o autoPaging().selectList()
 *   o autoPaging().selectPage()
 *   o manualPaging().selectList()
 *   o manualPaging().selectPage()
 * 
 * {Option -- Dynamic}
 *   o dynamicBinding().selectList()
 * </pre>
 * 
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
	
	/** The default configuration of statement. (Nullable) */
	protected final StatementConfig _defaultStatementConfig;

    /** Is it dynamic binding? */
    protected boolean _dynamicBinding;
	
	/** The configuration of statement. (Nullable) */
	protected StatementConfig _statementConfig;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OutsideSqlBasicExecutor(BehaviorCommandInvoker behaviorCommandInvoker
                                 , String tableDbName
                                 , DBDef currentDBDef
                                 , StatementConfig defaultStatementConfig) {
        this._behaviorCommandInvoker = behaviorCommandInvoker;
        this._tableDbName = tableDbName;
        this._currentDBDef = currentDBDef;
        this._defaultStatementConfig = defaultStatementConfig;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    /**
     * Select the list of the entity.
     * @param <ENTITY> The type of entity for element.
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (Nullable)
     * @param entityType The element type of entity. (NotNull)
     * @return The result bean of selected list. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     */
    public <ENTITY> ListResultBean<ENTITY> selectList(String path, Object pmb, Class<ENTITY> entityType) {
        List<ENTITY> resultList = invoke(createSelectListCommand(path, pmb, entityType));
        return new ResultBeanBuilder<ENTITY>(_tableDbName).buildListResultBean(resultList);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    /**
     * Execute. {Insert/Update/Delete/Etc...}
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (Nullable)
     * @return The count of execution.
     * @exception org.seasar.robot.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     */
    public int execute(String path, Object pmb) {
        return invoke(createExecuteCommand(path, pmb));
    }

    // [DBFlute-0.7.5]
    // ===================================================================================
    //                                                                      Procedure Call
    //                                                                      ==============
    /**
     * Call stored procedure.
     * @param pmb The parameter-bean for procedure. (NotNull)
     */
    public void call(ProcedurePmb pmb) {
        if (pmb == null) { throw new IllegalArgumentException("The argument of call() 'pmb' should not be null!"); }
        invoke(createCallCommand(pmb.getProcedureName(), pmb));
    }

    // ===================================================================================
    //                                                                    Behavior Command
    //                                                                    ================
    protected <ENTITY> BehaviorCommand<List<ENTITY>> createSelectListCommand(String path, Object pmb, Class<ENTITY> entityType) {
        final OutsideSqlSelectListCommand<ENTITY> cmd = xsetupCommand(new OutsideSqlSelectListCommand<ENTITY>(), path, pmb);
        cmd.setEntityType(entityType);
        return cmd;
    }

    protected BehaviorCommand<Integer> createExecuteCommand(String path, Object pmb) {
        return xsetupCommand(new OutsideSqlExecuteCommand(), path, pmb);
    }

    protected BehaviorCommand<Void> createCallCommand(String path, Object pmb) {
        return xsetupCommand(new OutsideSqlCallCommand(), path, pmb);
    }

    private <COMMAND extends AbstractOutsideSqlCommand<?>> COMMAND xsetupCommand(COMMAND command, String path, Object pmb) {
        command.setTableDbName(_tableDbName);
        _behaviorCommandInvoker.injectComponentProperty(command);
        command.setOutsideSqlPath(path);
        command.setParameterBean(pmb);
        command.setOutsideSqlOption(createOutsideSqlOption());
        command.setCurrentDBDef(_currentDBDef);
        return command;
    }

    /**
     * Invoke the command of behavior.
     * @param <RESULT> The type of result.
     * @param behaviorCommand The command of behavior. (NotNull)
     * @return The instance of result. (Nullable)
     */
    protected <RESULT> RESULT invoke(BehaviorCommand<RESULT> behaviorCommand) {
        return _behaviorCommandInvoker.invoke(behaviorCommand);
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    // -----------------------------------------------------
    //                                       Result Handling
    //                                       ---------------
    /**
     * Specify cursor handling. <br />
     * <pre>
     * # ex) Your Program
     * #
     * # executor.cursorHandling().selectCursor(path, pmb, handler);
     * #
     * </pre>
     * @return The cursor executor of outside-SQL. (NotNull)
     */
    public OutsideSqlCursorExecutor<Object> cursorHandling() {
        return new OutsideSqlCursorExecutor<Object>(_behaviorCommandInvoker, createOutsideSqlOption(), _tableDbName, _currentDBDef);
    }

    /**
     * Specify entity handling. <br />
     * <pre>
     * # ex) Your Program
     * #
     * # executor.entityHandling().selectEntityWithDeletedCheck(path, pmb, Xxx.class);
     * #
     * </pre>
     * @return The cursor executor of outside-SQL. (NotNull)
     */
    public OutsideSqlEntityExecutor<Object> entityHandling() {
        return new OutsideSqlEntityExecutor<Object>(_behaviorCommandInvoker, createOutsideSqlOption(), _tableDbName, _currentDBDef);
    }

    // -----------------------------------------------------
    //                                                Paging
    //                                                ------
    /**
     * Option of autoPaging. <br />
     * If you invoke this, you don't need to write paging condition on your SQL. <br />
     * <pre>
     * # ex) Your SQL {MySQL}
     * #
     * # select member.MEMBER_ID, member...
     * #   from Member member
     * #  where ...
     * #  order by ...
     * # -- limit 40, 20        *Here is unnecessary!
     * #
     * </pre>
     * @return The executor of paging that the paging mode is auto. (NotNull)
     */
    public OutsideSqlPagingExecutor autoPaging() {
        final OutsideSqlOption option = createOutsideSqlOption();
        option.autoPaging();
        return new OutsideSqlPagingExecutor(_behaviorCommandInvoker, option, _tableDbName
                                          , _currentDBDef, _defaultStatementConfig);
    }

    /**
     * Option of manualPaging. <br />
     * If you invoke this, you need to write paging condition on your SQL. <br />
     * <pre>
     * # ex) Your SQL {MySQL}
     * #
     * # select member.MEMBER_ID, member...
     * #   from Member member
     * #  where ...
     * #  order by ...
     * #  limit 40, 20        *Here is necessary!
     * #
     * </pre>
     * @return The executor of paging that the paging mode is manual. (NotNull)
     */
    public OutsideSqlPagingExecutor manualPaging() {
        final OutsideSqlOption option = createOutsideSqlOption();
        option.manualPaging();
        return new OutsideSqlPagingExecutor(_behaviorCommandInvoker, option, _tableDbName
                                          , _currentDBDef, _defaultStatementConfig);
    }

    // -----------------------------------------------------
    //                                       Dynamic Binding
    //                                       ---------------
    public OutsideSqlBasicExecutor dynamicBinding() {
        _dynamicBinding = true;
        return this;
    }

    // -----------------------------------------------------
    //                                      Statement Config
    //                                      ----------------
    public OutsideSqlBasicExecutor configure(StatementConfig statementConfig) {
        _statementConfig = statementConfig;
        return this;
    }
	
    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected OutsideSqlOption createOutsideSqlOption() {
        final OutsideSqlOption option = new OutsideSqlOption();
		option.setStatementConfig(_statementConfig);
        if (_dynamicBinding) {
            option.dynamicBinding();
        }
		option.setTableDbName(_tableDbName);// as information
        return option;
    }
}
