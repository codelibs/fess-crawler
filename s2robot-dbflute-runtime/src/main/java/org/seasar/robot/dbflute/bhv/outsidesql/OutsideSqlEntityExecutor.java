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
import org.seasar.robot.dbflute.bhv.core.BehaviorCommandInvoker;
import org.seasar.robot.dbflute.bhv.outsidesql.factory.OutsideSqlExecutorFactory;
import org.seasar.robot.dbflute.exception.DangerousResultSizeException;
import org.seasar.robot.dbflute.exception.thrower.BehaviorExceptionThrower;
import org.seasar.robot.dbflute.jdbc.FetchBean;
import org.seasar.robot.dbflute.jdbc.StatementConfig;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.DfTypeUtil;

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

    /** The DB name of table. (NotNull) */
    protected final String _tableDbName;

    /** The current database definition. (NotNull) */
    protected DBDef _currentDBDef;

    /** The default configuration of statement. (NullAllowed) */
    protected final StatementConfig _defaultStatementConfig;

    /** The option of outside-SQL. (NotNull) */
    protected final OutsideSqlOption _outsideSqlOption;

    /** The factory of outside-SQL executor. (NotNull) */
    protected final OutsideSqlExecutorFactory _outsideSqlExecutorFactory;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OutsideSqlEntityExecutor(BehaviorCommandInvoker behaviorCommandInvoker, String tableDbName,
            DBDef currentDBDef, StatementConfig defaultStatementConfig, OutsideSqlOption outsideSqlOption,
            OutsideSqlExecutorFactory outsideSqlExecutorFactory) {
        _behaviorCommandInvoker = behaviorCommandInvoker;
        _tableDbName = tableDbName;
        _currentDBDef = currentDBDef;
        _defaultStatementConfig = defaultStatementConfig;
        _outsideSqlOption = outsideSqlOption;
        _outsideSqlExecutorFactory = outsideSqlExecutorFactory;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    /**
     * Select entity by the outside-SQL.
     * <pre>
     * String path = MemberBhv.PATH_selectSimpleMember;
     * SimpleMemberPmb pmb = new SimpleMemberPmb();
     * pmb.setMemberId(3);
     * Class&lt;SimpleMember&gt; entityType = SimpleMember.class;
     * SimpleMember member
     *     = memberBhv.outsideSql().entityHandling().<span style="color: #FD4747">selectEntity</span>(path, pmb, entityType);
     * if (member != null) {
     *     ... = member.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param <ENTITY> The type of entity.
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (NullAllowed)
     * @param entityType The type of entity. (NotNull)
     * @return The selected entity. (NullAllowed)
     * @exception org.seasar.robot.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity is duplicated.
     */
    public <ENTITY> ENTITY selectEntity(String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        final int preSafetyMaxResultSize = xcheckSafetyResultAsOneIfNeed(pmb);
        final List<ENTITY> ls;
        try {
            ls = doSelectList(path, pmb, entityType);
        } catch (DangerousResultSizeException e) {
            final String searchKey4Log = buildSearchKey4Exception(path, pmb, entityType);
            throwSelectEntityDuplicatedException("{over safetyMaxResultSize '1'}", searchKey4Log, e);
            return null; // unreachable
        } finally {
            xrestoreSafetyResultIfNeed(pmb, preSafetyMaxResultSize);
        }
        if (ls == null || ls.isEmpty()) {
            return null;
        }
        if (ls.size() > 1) {
            final String searchKey4Log = buildSearchKey4Exception(path, pmb, entityType);
            throwSelectEntityDuplicatedException(String.valueOf(ls.size()), searchKey4Log, null);
        }
        return ls.get(0);
    }

    protected <ENTITY> List<ENTITY> doSelectList(String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        return createBasicExecutor().selectList(path, pmb, entityType);
    }

    protected OutsideSqlBasicExecutor createBasicExecutor() {
        return _outsideSqlExecutorFactory.createBasic(_behaviorCommandInvoker, _tableDbName, _currentDBDef,
                _defaultStatementConfig, _outsideSqlOption);
    }

    /**
     * Select entity with deleted check by the outside-SQL.
     * <pre>
     * String path = MemberBhv.PATH_selectSimpleMember;
     * SimpleMemberPmb pmb = new SimpleMemberPmb();
     * pmb.setMemberId(3);
     * Class&lt;SimpleMember&gt; entityType = SimpleMember.class;
     * SimpleMember member
     *     = memberBhv.outsideSql().entityHandling().<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(path, pmb, entityType);
     * ... = member.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param <ENTITY> The type of entity.
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (NullAllowed)
     * @param entityType The type of entity. (NotNull)
     * @return The selected entity. (NullAllowed)
     * @exception org.seasar.robot.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted(not found).
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity is duplicated.
     */
    public <ENTITY> ENTITY selectEntityWithDeletedCheck(String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        final ENTITY entity = selectEntity(path, pmb, entityType);
        if (entity == null) {
            throwSelectEntityAlreadyDeletedException(buildSearchKey4Exception(path, pmb, entityType));
        }
        return entity;
    }

    protected <ENTITY> String buildSearchKey4Exception(String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        String tmp = "table  = " + _outsideSqlOption.getTableDbName() + ln();
        tmp = tmp + "path   = " + path + ln();
        tmp = tmp + "pmbean = " + DfTypeUtil.toClassTitle(pmb) + ":" + pmb + ln();
        tmp = tmp + "entity = " + DfTypeUtil.toClassTitle(entityType) + ln();
        tmp = tmp + "option = " + _outsideSqlOption;
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

    protected void throwSelectEntityAlreadyDeletedException(Object searchKey) {
        createBhvExThrower().throwSelectEntityAlreadyDeletedException(searchKey);
    }

    protected void throwSelectEntityDuplicatedException(String resultCountExp, Object searchKey, Throwable cause) {
        createBhvExThrower().throwSelectEntityDuplicatedException(resultCountExp, searchKey, cause);
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    /**
     * Set up remove-block-comment for this outside-SQL.
     * @return this. (NotNull)
     */
    public OutsideSqlEntityExecutor<PARAMETER_BEAN> removeBlockComment() {
        _outsideSqlOption.removeBlockComment();
        return this;
    }

    /**
     * Set up remove-line-comment for this outside-SQL.
     * @return this. (NotNull)
     */
    public OutsideSqlEntityExecutor<PARAMETER_BEAN> removeLineComment() {
        _outsideSqlOption.removeLineComment();
        return this;
    }

    /**
     * Set up format-SQL for this outside-SQL. <br />
     * (For example, empty lines removed)
     * @return this. (NotNull)
     */
    public OutsideSqlEntityExecutor<PARAMETER_BEAN> formatSql() {
        _outsideSqlOption.formatSql();
        return this;
    }

    /**
     * Configure statement JDBC options. (For example, queryTimeout, fetchSize, ...)
     * @param statementConfig The configuration of statement. (NullAllowed)
     * @return this. (NotNull)
     */
    public OutsideSqlEntityExecutor<PARAMETER_BEAN> configure(StatementConfig statementConfig) {
        _outsideSqlOption.setStatementConfig(statementConfig);
        return this;
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected BehaviorExceptionThrower createBhvExThrower() {
        return _behaviorCommandInvoker.createBehaviorExceptionThrower();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    /**
     * Get the value of line separator.
     * @return The value of line separator. (NotNull)
     */
    protected static String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
