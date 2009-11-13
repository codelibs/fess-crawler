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
import org.seasar.robot.dbflute.cbean.ListResultBean;
import org.seasar.robot.dbflute.cbean.PagingBean;
import org.seasar.robot.dbflute.cbean.PagingHandler;
import org.seasar.robot.dbflute.cbean.PagingInvoker;
import org.seasar.robot.dbflute.cbean.PagingResultBean;
import org.seasar.robot.dbflute.cbean.ResultBeanBuilder;
import org.seasar.robot.dbflute.jdbc.StatementConfig;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlOption;


/**
 * The paging executor of outside-SQL.
 * @author jflute
 */
public class OutsideSqlPagingExecutor {

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
    protected final DBDef _currentDBDef;
	
	/** The default configuration of statement. (Nullable) */
	protected final StatementConfig _defaultStatementConfig;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OutsideSqlPagingExecutor(BehaviorCommandInvoker behaviorCommandInvoker
                                  , OutsideSqlOption outsideSqlOption
                                  , String tableDbName
                                  , DBDef currentDBDef
                                  , StatementConfig defaultStatementConfig) {
        this._behaviorCommandInvoker = behaviorCommandInvoker;
        this._outsideSqlOption = outsideSqlOption;
        this._tableDbName = tableDbName;
        this._currentDBDef = currentDBDef;
        this._defaultStatementConfig = defaultStatementConfig;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    /**
     * Select list with paging.
     * <p>
     * The SQL should have Paging without Count. <br />
     * You do not need to use pagingBean's isPaging() method on your 'Parameter Comment'. <br />
     * <pre>
     * - - - - - - - - - - - - - - - - - - - - - - -
     * ex) Your Correct SQL {MySQL and manualPaging}
     * - - - - - - - - - - - - - - - - - - - - - - -
     * # select member.MEMBER_ID
     * #      , member.MEMBER_NAME
     * #      , memberStatus.MEMBER_STATUS_NAME
     * #   from MEMBER member
     * #     left outer join MEMBER_STATUS memberStatus
     * #       on member.MEMBER_STATUS_CODE = memberStatus.MEMBER_STATUS_CODE
     * #  /*BEGIN&#42;/where
     * #    /*IF pmb.memberId != null&#42;/member.MEMBER_ID = /*pmb.memberId&#42;/'123'/*END&#42;/
     * #    /*IF pmb.memberName != null&#42;/and member.MEMBER_NAME like /*pmb.memberName&#42;/'Billy' || '%'/*END&#42;/
     * #  /*END&#42;/
     * #  order by member.UPDATE_DATETIME desc
     * #  limit /*$pmb.pageStartIndex&#42;/80, /*$pmb.fetchSize&#42;/20
     * # 
     * o If it's autoPaging, the line of 'limit 80, 20' is unnecessary!
     * </pre>
     * @param <ENTITY> The type of entity.
     * @param path The path of SQL that executes count and paging. (NotNull)
     * @param pmb The bean of paging parameter. (NotNull)
     * @param entityType The type of result entity. (NotNull)
     * @return The result bean of paged list. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     */
    public <ENTITY> ListResultBean<ENTITY> selectList(String path, PagingBean pmb, Class<ENTITY> entityType) {
        setupScrollableCursorIfNeeds();
        List<ENTITY> resultList = invoke(createSelectListCommand(path, pmb, entityType));
        return new ResultBeanBuilder<ENTITY>(_tableDbName).buildListResultBean(resultList);
    }

    /**
     * Select page.
     * <p>
     * The SQL should have Count and Paging. <br />
     * You can realize by pagingBean's isPaging() method on your 'Parameter Comment'. For example, 'IF Comment'. <br />
     * It returns false when it executes Count. And it returns true when it executes Paging. <br />
     * <pre>
     * - - - - - - - - - - - - - - - - - - - - - - -
     * ex) Your Correct SQL {MySQL and manualPaging}
     * - - - - - - - - - - - - - - - - - - - - - - -
     * # /*IF pmb.isPaging()&#42;/
     * # select member.MEMBER_ID
     * #      , member.MEMBER_NAME
     * #      , memberStatus.MEMBER_STATUS_NAME
     * # -- ELSE select count(*)
     * # /*END&#42;/
     * #   from MEMBER member
     * #     /*IF pmb.isPaging()&#42;/
     * #     left outer join MEMBER_STATUS memberStatus
     * #       on member.MEMBER_STATUS_CODE = memberStatus.MEMBER_STATUS_CODE
     * #     /*END&#42;/
     * #  /*BEGIN&#42;/where
     * #    /*IF pmb.memberId != null&#42;/member.MEMBER_ID = /*pmb.memberId&#42;/'123'/*END&#42;/
     * #    /*IF pmb.memberName != null&#42;/and member.MEMBER_NAME like /*pmb.memberName&#42;/'Billy' || '%'/*END&#42;/
     * #  /*END&#42;/
     * #  /*IF pmb.isPaging()&#42;/
     * #  order by member.UPDATE_DATETIME desc
     * #  /*END&#42;/
     * #  /*IF pmb.isPaging()&#42;/
     * #  limit /*$pmb.pageStartIndex&#42;/80, /*$pmb.fetchSize&#42;/20
     * #  /*END&#42;/
     * # 
     * o If it's autoPaging, the line of 'limit 80, 20' is unnecessary!
     * 
     * - - - - - - - - - - - - - - - - - - - - - - - - -
     * ex) Wrong SQL {part 1}
     *     -- Line comment before ELSE comment --
     * - - - - - - - - - - - - - - - - - - - - - - - - -
     * # /*IF pmb.isPaging()&#42;/
     * # select member.MEMBER_ID
     * #      , member.MEMBER_NAME -- The name of member...    *NG
     * #      -- The status name of member...                  *NG
     * #      , memberStatus.MEMBER_STATUS_NAME
     * # -- ELSE select count(*)
     * # /*END&#42;/
     * # ...
     * o It's restriction...Sorry
     * </pre>
     * @param <ENTITY> The type of entity.
     * @param path The path of SQL that executes count and paging. (NotNull)
     * @param pmb The bean of paging parameter. (NotNull)
     * @param entityType The type of result entity. (NotNull)
     * @return The result bean of paging. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     */
    public <ENTITY> PagingResultBean<ENTITY> selectPage(final String path
                                                      , final PagingBean pmb
                                                      , final Class<ENTITY> entityType) {
        final OutsideSqlEntityExecutor<PagingBean> countExecutor = createCountExecutor();
        final PagingHandler<ENTITY> handler = new PagingHandler<ENTITY>() {
            public PagingBean getPagingBean() {
                return pmb;
            }
            public int count() {
                pmb.xsetPaging(false);
                return countExecutor.selectEntityWithDeletedCheck(path, pmb, Integer.class);
            }
            public List<ENTITY> paging() {
                pmb.xsetPaging(true);
                return selectList(path, pmb, entityType);
            }
        };
        final PagingInvoker<ENTITY> invoker = new PagingInvoker<ENTITY>(_tableDbName);
        return invoker.invokePaging(handler);
    }

    protected OutsideSqlEntityExecutor<PagingBean> createCountExecutor() {
        final OutsideSqlOption countOption = _outsideSqlOption.copyOptionWithoutPaging();
        return new OutsideSqlEntityExecutor<PagingBean>(_behaviorCommandInvoker, countOption, _tableDbName, _currentDBDef);
    }

    protected void setupScrollableCursorIfNeeds() {
        if (!_outsideSqlOption.isAutoPaging()) {
            return;
        }
        StatementConfig statementConfig = _outsideSqlOption.getStatementConfig();
        if (statementConfig != null && statementConfig.hasResultSetType()) {
            return;
        }
        if (_defaultStatementConfig != null && _defaultStatementConfig.hasResultSetType()) {
            return;
        }
        if (statementConfig == null) {
            statementConfig = new StatementConfig();
            configure(statementConfig);
        }
        statementConfig.typeScrollInsensitive();
    }

    // ===================================================================================
    //                                                                    Behavior Command
    //                                                                    ================
    protected <ENTITY> BehaviorCommand<List<ENTITY>> createSelectListCommand(String path, Object pmb, Class<ENTITY> entityType) {
        return xsetupCommand(new OutsideSqlSelectListCommand<ENTITY>(), path, pmb, entityType);
    }

    private <ENTITY> OutsideSqlSelectListCommand<ENTITY> xsetupCommand(OutsideSqlSelectListCommand<ENTITY> command, String path, Object pmb, Class<ENTITY> entityType) {
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
    public OutsideSqlPagingExecutor configure(StatementConfig statementConfig) {
		_outsideSqlOption.setStatementConfig(statementConfig);
        return this;
    }

    public OutsideSqlPagingExecutor dynamicBinding() {
        _outsideSqlOption.dynamicBinding();
        return this;
    }
}
