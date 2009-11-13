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
import org.seasar.robot.dbflute.bhv.core.command.OutsideSqlSelectListCommand;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.cbean.FetchBean;
import org.seasar.robot.dbflute.exception.DangerousResultSizeException;
import org.seasar.robot.dbflute.jdbc.StatementConfig;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * The cursor executor of outside-SQL.
 * @param <PARAMETER_BEAN> The type of parameter-bean.
 * @author jflute
 */
public class OutsideSqlEntityExecutor<PARAMETER_BEAN> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The invoker of behavior command. (NotNull) */
    protected final BehaviorCommandInvoker _behaviorCommandInvoker;

    /** The option of outside-SQL. (NotNull) */
    protected final OutsideSqlOption _outsideSqlOption;

    /** The DB name of table. (NotNull) */
    protected final String _tableDbName;

    /** The current database definition. (NotNull) */
    protected DBDef _currentDBDef;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OutsideSqlEntityExecutor(BehaviorCommandInvoker behaviorCommandInvoker, OutsideSqlOption outsideSqlOption,
            String tableDbName, DBDef currentDBDef) {
        this._behaviorCommandInvoker = behaviorCommandInvoker;
        this._outsideSqlOption = outsideSqlOption;
        this._tableDbName = tableDbName;
        this._currentDBDef = currentDBDef;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    /**
     * Select entity.
     * @param <ENTITY> The type of entity.
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (Nullable)
     * @param entityType The type of entity. (NotNull)
     * @return The selected entity. (Nullable)
     * @exception org.seasar.robot.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity is duplicated.
     */
    public <ENTITY> ENTITY selectEntity(String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        final int preSafetyMaxResultSize = xcheckSafetyResultAsOneIfNeed(pmb);
        final List<ENTITY> ls;
        try {
            ls = invoke(createSelectListCommand(path, pmb, entityType));
        } catch (DangerousResultSizeException e) {
            String searchKey4Log = buildSearchKey4Log(path, pmb, entityType);
            throwEntityDuplicatedException("{over safetyMaxResultSize '1'}", searchKey4Log, e);
            return null; // unreachable
        } finally {
            xrestoreSafetyResultIfNeed(pmb, preSafetyMaxResultSize);
        }
        if (ls == null || ls.isEmpty()) {
            return null;
        }
        if (ls.size() > 1) {
            String searchKey4Log = buildSearchKey4Log(path, pmb, entityType);
            throwEntityDuplicatedException(String.valueOf(ls.size()), searchKey4Log, null);
        }
        return ls.get(0);
    }

    /**
     * Select entity with deleted check.
     * @param <ENTITY> The type of entity.
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (Nullable)
     * @param entityType The type of entity. (NotNull)
     * @return The selected entity. (Nullable)
     * @exception org.seasar.robot.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted(not found).
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity is duplicated.
     */
    public <ENTITY> ENTITY selectEntityWithDeletedCheck(String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        final ENTITY entity = selectEntity(path, pmb, entityType);
        if (entity == null) {
            String searchKey4Log = buildSearchKey4Log(path, pmb, entityType);
            throwEntityAlreadyDeletedException(searchKey4Log);
        }
        return entity;
    }

    protected <ENTITY> String buildSearchKey4Log(String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        String tmp = "Table  = " + _outsideSqlOption.getTableDbName() + ln();
        tmp = tmp + "Path   = " + path + ln();
        tmp = tmp + "Pmb    = " + (pmb != null ? pmb.getClass().getSimpleName() : "null") + ":" + pmb + ln();
        tmp = tmp + "Entity = " + (entityType != null ? entityType.getSimpleName() : "null") + ln();
        tmp = tmp + "Option = " + _outsideSqlOption;
        return tmp;
    }

    protected int xcheckSafetyResultAsOneIfNeed(PARAMETER_BEAN pmb) {
        if (pmb instanceof FetchBean) {
            final int safetyMaxResultSize = ((FetchBean) pmb).getSafetyMaxResultSize();
            ((FetchBean) pmb).checkSafetyResult(1);
            return safetyMaxResultSize;
        }
        return 0;
    }

    protected void xrestoreSafetyResultIfNeed(PARAMETER_BEAN pmb, int preSafetyMaxResultSize) {
        if (pmb instanceof FetchBean) {
            ((FetchBean) pmb).checkSafetyResult(preSafetyMaxResultSize);
        }
    }

    // -----------------------------------------------------
    //                                                Helper
    //                                                ------
    protected void throwEntityAlreadyDeletedException(Object searchKey4Log) {
        ConditionBeanContext.throwEntityAlreadyDeletedException(searchKey4Log);
    }

    protected void throwEntityDuplicatedException(String resultCountString, Object searchKey4Log, Throwable cause) {
        ConditionBeanContext.throwEntityDuplicatedException(resultCountString, searchKey4Log, cause);
    }

    // ===================================================================================
    //                                                                    Behavior Command
    //                                                                    ================
    protected <ENTITY> BehaviorCommand<List<ENTITY>> createSelectListCommand(String path, PARAMETER_BEAN pmb,
            Class<ENTITY> entityType) {
        return xsetupCommand(new OutsideSqlSelectListCommand<ENTITY>(), path, pmb, entityType);
    }

    private <ENTITY> OutsideSqlSelectListCommand<ENTITY> xsetupCommand(OutsideSqlSelectListCommand<ENTITY> command,
            String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        command.setTableDbName(_tableDbName);
        _behaviorCommandInvoker.injectComponentProperty(command);
        command.setOutsideSqlPath(path);
        command.setParameterBean(pmb);
        command.setOutsideSqlOption(_outsideSqlOption);
        command.setCurrentDBDef(_currentDBDef);
        command.setEntityType(entityType);
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
    public OutsideSqlEntityExecutor<PARAMETER_BEAN> configure(StatementConfig statementConfig) {
        _outsideSqlOption.setStatementConfig(statementConfig);
        return this;
    }

    public OutsideSqlEntityExecutor<PARAMETER_BEAN> dynamicBinding() {
        _outsideSqlOption.dynamicBinding();
        return this;
    }

    // ===================================================================================
    //                                                                              Helper
    //                                                                              ======
    /**
     * Get the value of line separator.
     * @return The value of line separator. (NotNull)
     */
    protected static String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
