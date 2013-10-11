/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.robot.db.bsbhv;

import java.util.List;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.bhv.AbstractBehaviorWritable;
import org.seasar.dbflute.bhv.DeleteOption;
import org.seasar.dbflute.bhv.InsertOption;
import org.seasar.dbflute.bhv.QueryInsertSetupper;
import org.seasar.dbflute.bhv.UpdateOption;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.EntityRowHandler;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.outsidesql.executor.OutsideSqlBasicExecutor;
import org.seasar.robot.db.bsentity.dbmeta.AccessResultDbm;
import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.exbhv.AccessResultBhv;
import org.seasar.robot.db.exentity.AccessResult;
import org.seasar.robot.db.exentity.AccessResultData;

/**
 * The behavior of ACCESS_RESULT as TABLE. <br />
 * 
 * <pre>
 * [primary key]
 *     ID
 * 
 * [column]
 *     ID, SESSION_ID, RULE_ID, URL, PARENT_URL, STATUS, HTTP_STATUS_CODE, METHOD, MIME_TYPE, CONTENT_LENGTH, EXECUTION_TIME, LAST_MODIFIED, CREATE_TIME
 * 
 * [sequence]
 *     ACCESS_RESULT_SEQ
 * 
 * [identity]
 *     
 * 
 * [version-no]
 *     
 * 
 * [foreign table]
 *     ACCESS_RESULT_DATA(AsOne)
 * 
 * [referrer table]
 *     ACCESS_RESULT_DATA
 * 
 * [foreign property]
 *     accessResultDataAsOne
 * 
 * [referrer property]
 *     
 * </pre>
 * 
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsAccessResultBhv extends AbstractBehaviorWritable {

    // ===================================================================================
    // Definition
    // ==========
    /* df:beginQueryPath */
    public static final String PATH_selectListByUrlDiff = "selectListByUrlDiff";

    public static final String PATH_deleteAll = "deleteAll";

    public static final String PATH_deleteBySessionId = "deleteBySessionId";

    /* df:endQueryPath */

    // ===================================================================================
    // Table name
    // ==========
    /** @return The name on database of table. (NotNull) */
    @Override
    public String getTableDbName() {
        return "ACCESS_RESULT";
    }

    // ===================================================================================
    // DBMeta
    // ======
    /** @return The instance of DBMeta. (NotNull) */
    @Override
    public DBMeta getDBMeta() {
        return AccessResultDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public AccessResultDbm getMyDBMeta() {
        return AccessResultDbm.getInstance();
    }

    // ===================================================================================
    // New Instance
    // ============
    /** {@inheritDoc} */
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    /** {@inheritDoc} */
    @Override
    public ConditionBean newConditionBean() {
        return newMyConditionBean();
    }

    /** @return The instance of new entity as my table type. (NotNull) */
    public AccessResult newMyEntity() {
        return new AccessResult();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public AccessResultCB newMyConditionBean() {
        return new AccessResultCB();
    }

    // ===================================================================================
    // Count Select
    // ============
    /**
     * Select the count of uniquely-selected records by the condition-bean.
     * {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type
     * column for union's distinct.
     * 
     * <pre>
     * AccessResultCB cb = new AccessResultCB();
     * cb.query().setFoo...(value);
     * int count = accessResultBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * 
     * @param cb
     *            The condition-bean of AccessResult. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(final AccessResultCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final AccessResultCB cb) { // called by
                                                                   // selectCount(cb)
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final AccessResultCB cb) { // called by
                                                                  // selectPage(cb)
        assertCBStateValid(cb);
        return delegateSelectCountPlainly(cb);
    }

    @Override
    protected int doReadCount(final ConditionBean cb) {
        return selectCount(downcast(cb));
    }

    // ===================================================================================
    // Entity Select
    // =============
    /**
     * Select the entity by the condition-bean.
     * 
     * <pre>
     * AccessResultCB cb = new AccessResultCB();
     * cb.query().setFoo...(value);
     * AccessResult accessResult = accessResultBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (accessResult != null) {
     *     ... = accessResult.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * 
     * @param cb
     *            The condition-bean of AccessResult. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data,
     *         it returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException
     *                When the condition for selecting an entity is not found.
     */
    public AccessResult selectEntity(final AccessResultCB cb) {
        return doSelectEntity(cb, AccessResult.class);
    }

    protected <ENTITY extends AccessResult> ENTITY doSelectEntity(
            final AccessResultCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityInternally(
            cb,
            entityType,
            new InternalSelectEntityCallback<ENTITY, AccessResultCB>() {
                @Override
                public List<ENTITY> callbackSelectList(final AccessResultCB cb,
                        final Class<ENTITY> entityType) {
                    return doSelectList(cb, entityType);
                }
            });
    }

    @Override
    protected Entity doReadEntity(final ConditionBean cb) {
        return selectEntity(downcast(cb));
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * 
     * <pre>
     * AccessResultCB cb = new AccessResultCB();
     * cb.query().setFoo...(value);
     * AccessResult accessResult = accessResultBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = accessResult.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * 
     * @param cb
     *            The condition-bean of AccessResult. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data,
     *         throws exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException
     *                When the condition for selecting an entity is not found.
     */
    public AccessResult selectEntityWithDeletedCheck(final AccessResultCB cb) {
        return doSelectEntityWithDeletedCheck(cb, AccessResult.class);
    }

    protected <ENTITY extends AccessResult> ENTITY doSelectEntityWithDeletedCheck(
            final AccessResultCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        return helpSelectEntityWithDeletedCheckInternally(
            cb,
            entityType,
            new InternalSelectEntityWithDeletedCheckCallback<ENTITY, AccessResultCB>() {
                @Override
                public List<ENTITY> callbackSelectList(final AccessResultCB cb,
                        final Class<ENTITY> entityType) {
                    return doSelectList(cb, entityType);
                }
            });
    }

    @Override
    protected Entity doReadEntityWithDeletedCheck(final ConditionBean cb) {
        return selectEntityWithDeletedCheck(downcast(cb));
    }

    /**
     * Select the entity by the primary-key value.
     * 
     * @param id
     *            The one of primary key. (NotNull)
     * @return The entity selected by the PK. (NullAllowed: if no data, it
     *         returns null)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException
     *                When the condition for selecting an entity is not found.
     */
    public AccessResult selectByPKValue(final Long id) {
        return doSelectByPKValue(id, AccessResult.class);
    }

    protected <ENTITY extends AccessResult> ENTITY doSelectByPKValue(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntity(buildPKCB(id), entityType);
    }

    /**
     * Select the entity by the primary-key value with deleted check.
     * 
     * @param id
     *            The one of primary key. (NotNull)
     * @return The entity selected by the PK. (NotNull: if no data, throws
     *         exception)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.SelectEntityConditionNotFoundException
     *                When the condition for selecting an entity is not found.
     */
    public AccessResult selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKValueWithDeletedCheck(id, AccessResult.class);
    }

    protected <ENTITY extends AccessResult> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private AccessResultCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final AccessResultCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    // List Select
    // ===========
    /**
     * Select the list as result bean.
     * 
     * <pre>
     * AccessResultCB cb = new AccessResultCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;AccessResult&gt; accessResultList = accessResultBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (AccessResult accessResult : accessResultList) {
     *     ... = accessResult.get...();
     * }
     * </pre>
     * 
     * @param cb
     *            The condition-bean of AccessResult. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns
     *         empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException
     *                When the result size is over the specified safety size.
     */
    public ListResultBean<AccessResult> selectList(final AccessResultCB cb) {
        return doSelectList(cb, AccessResult.class);
    }

    protected <ENTITY extends AccessResult> ListResultBean<ENTITY> doSelectList(
            final AccessResultCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(
            cb,
            entityType,
            new InternalSelectListCallback<ENTITY, AccessResultCB>() {
                @Override
                public List<ENTITY> callbackSelectList(final AccessResultCB cb,
                        final Class<ENTITY> entityType) {
                    return delegateSelectList(cb, entityType);
                }
            });
    }

    @Override
    protected ListResultBean<? extends Entity> doReadList(final ConditionBean cb) {
        return selectList(downcast(cb));
    }

    // ===================================================================================
    // Page Select
    // ===========
    /**
     * Select the page as result bean. <br />
     * (both count-select and paging-select are executed)
     * 
     * <pre>
     * AccessResultCB cb = new AccessResultCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;AccessResult&gt; page = accessResultBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (AccessResult accessResult : page) {
     *     ... = accessResult.get...();
     * }
     * </pre>
     * 
     * @param cb
     *            The condition-bean of AccessResult. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns
     *         bean as empty list)
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException
     *                When the result size is over the specified safety size.
     */
    public PagingResultBean<AccessResult> selectPage(final AccessResultCB cb) {
        return doSelectPage(cb, AccessResult.class);
    }

    protected <ENTITY extends AccessResult> PagingResultBean<ENTITY> doSelectPage(
            final AccessResultCB cb, final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(
            cb,
            entityType,
            new InternalSelectPageCallback<ENTITY, AccessResultCB>() {
                @Override
                public int callbackSelectCount(final AccessResultCB cb) {
                    return doSelectCountPlainly(cb);
                }

                @Override
                public List<ENTITY> callbackSelectList(final AccessResultCB cb,
                        final Class<ENTITY> entityType) {
                    return doSelectList(cb, entityType);
                }
            });
    }

    @Override
    protected PagingResultBean<? extends Entity> doReadPage(
            final ConditionBean cb) {
        return selectPage(downcast(cb));
    }

    // ===================================================================================
    // Cursor Select
    // =============
    /**
     * Select the cursor by the condition-bean.
     * 
     * <pre>
     * AccessResultCB cb = new AccessResultCB();
     * cb.query().setFoo...(value);
     * accessResultBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;AccessResult&gt;() {
     *     public void handle(AccessResult entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * 
     * @param cb
     *            The condition-bean of AccessResult. (NotNull)
     * @param entityRowHandler
     *            The handler of entity row of AccessResult. (NotNull)
     */
    public void selectCursor(final AccessResultCB cb,
            final EntityRowHandler<AccessResult> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, AccessResult.class);
    }

    protected <ENTITY extends AccessResult> void doSelectCursor(
            final AccessResultCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityRowHandler<AccessResult>", entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        helpSelectCursorInternally(
            cb,
            entityRowHandler,
            entityType,
            new InternalSelectCursorCallback<ENTITY, AccessResultCB>() {
                @Override
                public void callbackSelectCursor(final AccessResultCB cb,
                        final EntityRowHandler<ENTITY> entityRowHandler,
                        final Class<ENTITY> entityType) {
                    delegateSelectCursor(cb, entityRowHandler, entityType);
                }

                @Override
                public List<ENTITY> callbackSelectList(final AccessResultCB cb,
                        final Class<ENTITY> entityType) {
                    return doSelectList(cb, entityType);
                }
            });
    }

    // ===================================================================================
    // Scalar Select
    // =============
    /**
     * Select the scalar value derived by a function from uniquely-selected
     * records. <br />
     * You should call a function method after this method called like as
     * follows:
     * 
     * <pre>
     * accessResultBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(AccessResultCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * 
     * @param <RESULT>
     *            The type of result.
     * @param resultType
     *            The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<AccessResultCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends AccessResultCB> SLFunction<CB, RESULT> doScalarSelect(
            final Class<RESULT> resultType, final CB cb) {
        assertObjectNotNull("resultType", resultType);
        assertCBStateValid(cb);
        cb.xsetupForScalarSelect();
        cb.getSqlClause().disableSelectIndex(); // for when you use union
        return new SLFunction<CB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    // Sequence
    // ========
    /**
     * Select the next value as sequence. <br />
     * This method is called when insert() and set to primary-key automatically.
     * So you don't need to call this as long as you need to get next value
     * before insert().
     * 
     * @return The next value. (NotNull)
     */
    public Long selectNextVal() {
        return doSelectNextVal(Long.class);
    }

    protected <RESULT> RESULT doSelectNextVal(final Class<RESULT> resultType) {
        return delegateSelectNextVal(resultType);
    }

    @Override
    protected Number doReadNextVal() {
        return selectNextVal();
    }

    // ===================================================================================
    // Pull out Relation
    // =================
    /**
     * Pull out the list of referrer-as-one table 'AccessResultData'.
     * 
     * @param accessResultList
     *            The list of accessResult. (NotNull, EmptyAllowed)
     * @return The list of referrer-as-one table. (NotNull, EmptyAllowed,
     *         NotNullElement)
     */
    public List<AccessResultData> pulloutAccessResultDataAsOne(
            final List<AccessResult> accessResultList) {
        return helpPulloutInternally(
            accessResultList,
            new InternalPulloutCallback<AccessResult, AccessResultData>() {
                @Override
                public AccessResultData getFr(final AccessResult e) {
                    return e.getAccessResultDataAsOne();
                }

                @Override
                public boolean hasRf() {
                    return true;
                }

                @Override
                public void setRfLs(final AccessResultData e,
                        final List<AccessResult> ls) {
                    if (!ls.isEmpty()) {
                        e.setAccessResult(ls.get(0));
                    }
                }
            });
    }

    // ===================================================================================
    // Extract Column
    // ==============
    /**
     * Extract the value list of (single) primary key id.
     * 
     * @param accessResultList
     *            The list of accessResult. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed,
     *         NotNullElement)
     */
    public List<Long> extractIdList(final List<AccessResult> accessResultList) {
        return helpExtractListInternally(
            accessResultList,
            new InternalExtractCallback<AccessResult, Long>() {
                @Override
                public Long getCV(final AccessResult e) {
                    return e.getId();
                }
            });
    }

    // ===================================================================================
    // Entity Update
    // =============
    /**
     * Insert the entity modified-only. (DefaultConstraintsEnabled)
     * 
     * <pre>
     * AccessResult accessResult = new AccessResult();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * accessResult.setFoo...(value);
     * accessResult.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//accessResult.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//accessResult.set...;</span>
     * accessResultBhv.<span style="color: #FD4747">insert</span>(accessResult);
     * ... = accessResult.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * <p>
     * While, when the entity is created by select, all columns are registered.
     * </p>
     * 
     * @param accessResult
     *            The entity of insert target. (NotNull, PrimaryKeyNullAllowed:
     *            when auto-increment)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (unique constraint
     *                violation)
     */
    public void insert(final AccessResult accessResult) {
        doInsert(accessResult, null);
    }

    protected void doInsert(final AccessResult accessResult,
            final InsertOption<AccessResultCB> option) {
        assertObjectNotNull("accessResult", accessResult);
        prepareInsertOption(option);
        delegateInsert(accessResult, option);
    }

    protected void prepareInsertOption(final InsertOption<AccessResultCB> option) {
        if (option == null) {
            return;
        }
        assertInsertOptionStatus(option);
        if (option.hasSpecifiedInsertColumn()) {
            option
                .resolveInsertColumnSpecification(createCBForSpecifiedUpdate());
        }
    }

    @Override
    protected void doCreate(final Entity entity,
            final InsertOption<? extends ConditionBean> option) {
        if (option == null) {
            insert(downcast(entity));
        } else {
            varyingInsert(downcast(entity), downcast(option));
        }
    }

    /**
     * Update the entity modified-only. (ZeroUpdateException,
     * NonExclusiveControl)
     * 
     * <pre>
     * AccessResult accessResult = new AccessResult();
     * accessResult.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * accessResult.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//accessResult.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//accessResult.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * accessResult.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     accessResultBhv.<span style="color: #FD4747">update</span>(accessResult);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * 
     * @param accessResult
     *            The entity of update target. (NotNull, PrimaryKeyNotNull,
     *            ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (unique constraint
     *                violation)
     */
    public void update(final AccessResult accessResult) {
        doUpdate(accessResult, null);
    }

    protected void doUpdate(final AccessResult accessResult,
            final UpdateOption<AccessResultCB> option) {
        assertObjectNotNull("accessResult", accessResult);
        prepareUpdateOption(option);
        helpUpdateInternally(
            accessResult,
            new InternalUpdateCallback<AccessResult>() {
                @Override
                public int callbackDelegateUpdate(final AccessResult entity) {
                    return delegateUpdate(entity, option);
                }
            });
    }

    protected void prepareUpdateOption(final UpdateOption<AccessResultCB> option) {
        if (option == null) {
            return;
        }
        assertUpdateOptionStatus(option);
        if (option.hasSelfSpecification()) {
            option.resolveSelfSpecification(createCBForVaryingUpdate());
        }
        if (option.hasSpecifiedUpdateColumn()) {
            option
                .resolveUpdateColumnSpecification(createCBForSpecifiedUpdate());
        }
    }

    protected AccessResultCB createCBForVaryingUpdate() {
        final AccessResultCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected AccessResultCB createCBForSpecifiedUpdate() {
        final AccessResultCB cb = newMyConditionBean();
        cb.xsetupForSpecifiedUpdate();
        return cb;
    }

    @Override
    protected void doModify(final Entity entity,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            update(downcast(entity));
        } else {
            varyingUpdate(downcast(entity), downcast(option));
        }
    }

    @Override
    protected void doModifyNonstrict(final Entity entity,
            final UpdateOption<? extends ConditionBean> option) {
        doModify(entity, option);
    }

    /**
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled,
     * NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data,
     * insert() } <br />
     * <p>
     * <span style="color: #FD4747; font-size: 120%">Attention, you cannot
     * update by unique keys instead of PK.</span>
     * </p>
     * 
     * @param accessResult
     *            The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (unique constraint
     *                violation)
     */
    public void insertOrUpdate(final AccessResult accessResult) {
        doInesrtOrUpdate(accessResult, null, null);
    }

    protected void doInesrtOrUpdate(final AccessResult accessResult,
            final InsertOption<AccessResultCB> insertOption,
            final UpdateOption<AccessResultCB> updateOption) {
        helpInsertOrUpdateInternally(
            accessResult,
            new InternalInsertOrUpdateCallback<AccessResult, AccessResultCB>() {
                @Override
                public void callbackInsert(final AccessResult entity) {
                    doInsert(entity, insertOption);
                }

                @Override
                public void callbackUpdate(final AccessResult entity) {
                    doUpdate(entity, updateOption);
                }

                @Override
                public AccessResultCB callbackNewMyConditionBean() {
                    return newMyConditionBean();
                }

                @Override
                public int callbackSelectCount(final AccessResultCB cb) {
                    return selectCount(cb);
                }
            });
    }

    @Override
    protected void doCreateOrModify(final Entity entity,
            InsertOption<? extends ConditionBean> insertOption,
            UpdateOption<? extends ConditionBean> updateOption) {
        if (insertOption == null && updateOption == null) {
            insertOrUpdate(downcast(entity));
        } else {
            insertOption =
                insertOption == null ? new InsertOption<AccessResultCB>()
                    : insertOption;
            updateOption =
                updateOption == null ? new UpdateOption<AccessResultCB>()
                    : updateOption;
            varyingInsertOrUpdate(
                downcast(entity),
                downcast(insertOption),
                downcast(updateOption));
        }
    }

    @Override
    protected void doCreateOrModifyNonstrict(final Entity entity,
            final InsertOption<? extends ConditionBean> insertOption,
            final UpdateOption<? extends ConditionBean> updateOption) {
        doCreateOrModify(entity, insertOption, updateOption);
    }

    /**
     * Delete the entity. (ZeroUpdateException, NonExclusiveControl)
     * 
     * <pre>
     * AccessResult accessResult = new AccessResult();
     * accessResult.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * accessResult.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     accessResultBhv.<span style="color: #FD4747">delete</span>(accessResult);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * 
     * @param accessResult
     *            The entity of delete target. (NotNull, PrimaryKeyNotNull,
     *            ConcurrencyColumnRequired)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     */
    public void delete(final AccessResult accessResult) {
        doDelete(accessResult, null);
    }

    protected void doDelete(final AccessResult accessResult,
            final DeleteOption<AccessResultCB> option) {
        assertObjectNotNull("accessResult", accessResult);
        prepareDeleteOption(option);
        helpDeleteInternally(
            accessResult,
            new InternalDeleteCallback<AccessResult>() {
                @Override
                public int callbackDelegateDelete(final AccessResult entity) {
                    return delegateDelete(entity, option);
                }
            });
    }

    protected void prepareDeleteOption(final DeleteOption<AccessResultCB> option) {
        if (option == null) {
            return;
        }
        assertDeleteOptionStatus(option);
    }

    @Override
    protected void doRemove(final Entity entity,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            delete(downcast(entity));
        } else {
            varyingDelete(downcast(entity), downcast(option));
        }
    }

    @Override
    protected void doRemoveNonstrict(final Entity entity,
            final DeleteOption<? extends ConditionBean> option) {
        doRemove(entity, option);
    }

    // ===================================================================================
    // Batch Update
    // ============
    /**
     * Batch-insert the entity list modified-only of same-set columns.
     * (DefaultConstraintsEnabled) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <p>
     * <span style="color: #FD4747; font-size: 120%">The columns of least common
     * multiple are registered like this:</span>
     * </p>
     * 
     * <pre>
     * for (... : ...) {
     *     AccessResult accessResult = new AccessResult();
     *     accessResult.setFooName("foo");
     *     if (...) {
     *         accessResult.setFooPrice(123);
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are registered</span>
     *     <span style="color: #3F7E5E">// FOO_PRICE not-called in any entities are registered as null without default value</span>
     *     <span style="color: #3F7E5E">// columns not-called in all entities are registered as null or default value</span>
     *     accessResultList.add(accessResult);
     * }
     * accessResultBhv.<span style="color: #FD4747">batchInsert</span>(accessResultList);
     * </pre>
     * <p>
     * While, when the entities are created by select, all columns are
     * registered.
     * </p>
     * <p>
     * And if the table has an identity, entities after the process don't have
     * incremented values. (When you use the (normal) insert(), you can get the
     * incremented value from your entity)
     * </p>
     * 
     * @param accessResultList
     *            The list of the entity. (NotNull, EmptyAllowed,
     *            PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(final List<AccessResult> accessResultList) {
        final InsertOption<AccessResultCB> option = createInsertUpdateOption();
        return doBatchInsert(accessResultList, option);
    }

    protected int[] doBatchInsert(final List<AccessResult> accessResultList,
            final InsertOption<AccessResultCB> option) {
        assertObjectNotNull("accessResultList", accessResultList);
        prepareBatchInsertOption(accessResultList, option);
        return delegateBatchInsert(accessResultList, option);
    }

    protected void prepareBatchInsertOption(
            final List<AccessResult> accessResultList,
            final InsertOption<AccessResultCB> option) {
        option.xallowInsertColumnModifiedPropertiesFragmented();
        option.xacceptInsertColumnModifiedPropertiesIfNeeds(accessResultList);
        prepareInsertOption(option);
    }

    @Override
    protected int[] doLumpCreate(final List<Entity> ls,
            final InsertOption<? extends ConditionBean> option) {
        if (option == null) {
            return batchInsert(downcast(ls));
        } else {
            return varyingBatchInsert(downcast(ls), downcast(option));
        }
    }

    /**
     * Batch-update the entity list modified-only of same-set columns.
     * (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #FD4747; font-size: 120%">You should specify same-set
     * columns to all entities like this:</span>
     * 
     * <pre>
     * for (... : ...) {
     *     AccessResult accessResult = new AccessResult();
     *     accessResult.setFooName("foo");
     *     if (...) {
     *         accessResult.setFooPrice(123);
     *     } else {
     *         accessResult.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//accessResult.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     accessResultList.add(accessResult);
     * }
     * accessResultBhv.<span style="color: #FD4747">batchUpdate</span>(accessResultList);
     * </pre>
     * 
     * @param accessResultList
     *            The list of the entity. (NotNull, EmptyAllowed,
     *            PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(final List<AccessResult> accessResultList) {
        final UpdateOption<AccessResultCB> option = createPlainUpdateOption();
        return doBatchUpdate(accessResultList, option);
    }

    protected int[] doBatchUpdate(final List<AccessResult> accessResultList,
            final UpdateOption<AccessResultCB> option) {
        assertObjectNotNull("accessResultList", accessResultList);
        prepareBatchUpdateOption(accessResultList, option);
        return delegateBatchUpdate(accessResultList, option);
    }

    protected void prepareBatchUpdateOption(
            final List<AccessResult> accessResultList,
            final UpdateOption<AccessResultCB> option) {
        option.xacceptUpdateColumnModifiedPropertiesIfNeeds(accessResultList);
        prepareUpdateOption(option);
    }

    @Override
    protected int[] doLumpModify(final List<Entity> ls,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return batchUpdate(downcast(ls));
        } else {
            return varyingBatchUpdate(downcast(ls), downcast(option));
        }
    }

    /**
     * Batch-update the entity list specified-only. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * 
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span> 
     * accessResultBhv.<span style="color: #FD4747">batchUpdate</span>(accessResultList, new SpecifyQuery<AccessResultCB>() {
     *     public void specify(AccessResultCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #FD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #FD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span> 
     * accessResultBhv.<span style="color: #FD4747">batchUpdate</span>(accessResultList, new SpecifyQuery<AccessResultCB>() {
     *     public void specify(AccessResultCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #FD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>
     * You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update and an
     * optimistic lock column because they are specified implicitly.
     * </p>
     * <p>
     * And you should specify columns that are modified in any entities (at
     * least one entity). But if you specify every column, it has no check.
     * </p>
     * 
     * @param accessResultList
     *            The list of the entity. (NotNull, EmptyAllowed,
     *            PrimaryKeyNotNull)
     * @param updateColumnSpec
     *            The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(final List<AccessResult> accessResultList,
            final SpecifyQuery<AccessResultCB> updateColumnSpec) {
        return doBatchUpdate(
            accessResultList,
            createSpecifiedUpdateOption(updateColumnSpec));
    }

    @Override
    protected int[] doLumpModifyNonstrict(final List<Entity> ls,
            final UpdateOption<? extends ConditionBean> option) {
        return doLumpModify(ls, option);
    }

    /**
     * Batch-delete the entity list. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * 
     * @param accessResultList
     *            The list of the entity. (NotNull, EmptyAllowed,
     *            PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted. (not found)
     */
    public int[] batchDelete(final List<AccessResult> accessResultList) {
        return doBatchDelete(accessResultList, null);
    }

    protected int[] doBatchDelete(final List<AccessResult> accessResultList,
            final DeleteOption<AccessResultCB> option) {
        assertObjectNotNull("accessResultList", accessResultList);
        prepareDeleteOption(option);
        return delegateBatchDelete(accessResultList, option);
    }

    @Override
    protected int[] doLumpRemove(final List<Entity> ls,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return batchDelete(downcast(ls));
        } else {
            return varyingBatchDelete(downcast(ls), downcast(option));
        }
    }

    @Override
    protected int[] doLumpRemoveNonstrict(final List<Entity> ls,
            final DeleteOption<? extends ConditionBean> option) {
        return doLumpRemove(ls, option);
    }

    // ===================================================================================
    // Query Update
    // ============
    /**
     * Insert the several entities by query (modified-only for fixed value).
     * 
     * <pre>
     * accessResultBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;AccessResult, AccessResultCB&gt;() {
     *     public ConditionBean setup(accessResult entity, AccessResultCB intoCB) {
     *         FooCB cb = FooCB();
     *         cb.setupSelect_Bar();
     * 
     *         <span style="color: #3F7E5E">// mapping</span>
     *         intoCB.specify().columnMyName().mappedFrom(cb.specify().columnFooName());
     *         intoCB.specify().columnMyCount().mappedFrom(cb.specify().columnFooCount());
     *         intoCB.specify().columnMyDate().mappedFrom(cb.specify().specifyBar().columnBarDate());
     *         entity.setMyFixedValue("foo"); <span style="color: #3F7E5E">// fixed value</span>
     *         <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     *         <span style="color: #3F7E5E">//entity.setRegisterUser(value);</span>
     *         <span style="color: #3F7E5E">//entity.set...;</span>
     *         <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     *         <span style="color: #3F7E5E">//entity.setVersionNo(value);</span>
     * 
     *         return cb;
     *     }
     * });
     * </pre>
     * 
     * @param setupper
     *            The setup-per of query-insert. (NotNull)
     * @return The inserted count.
     */
    public int queryInsert(
            final QueryInsertSetupper<AccessResult, AccessResultCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<AccessResult, AccessResultCB> setupper,
            final InsertOption<AccessResultCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final AccessResult entity = new AccessResult();
        final AccessResultCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected AccessResultCB createCBForQueryInsert() {
        final AccessResultCB cb = newMyConditionBean();
        cb.xsetupForQueryInsert();
        return cb;
    }

    @Override
    protected int doRangeCreate(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> setupper,
            final InsertOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryInsert(downcast(setupper));
        } else {
            return varyingQueryInsert(downcast(setupper), downcast(option));
        }
    }

    /**
     * Update the several entities by query non-strictly modified-only.
     * (NonExclusiveControl)
     * 
     * <pre>
     * AccessResult accessResult = new AccessResult();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//accessResult.setPK...(value);</span>
     * accessResult.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//accessResult.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//accessResult.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//accessResult.setVersionNo(value);</span>
     * AccessResultCB cb = new AccessResultCB();
     * cb.query().setFoo...(value);
     * accessResultBhv.<span style="color: #FD4747">queryUpdate</span>(accessResult, cb);
     * </pre>
     * 
     * @param accessResult
     *            The entity that contains update values. (NotNull,
     *            PrimaryKeyNullAllowed)
     * @param cb
     *            The condition-bean of AccessResult. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException
     *                When the query has no condition.
     */
    public int queryUpdate(final AccessResult accessResult,
            final AccessResultCB cb) {
        return doQueryUpdate(accessResult, cb, null);
    }

    protected int doQueryUpdate(final AccessResult accessResult,
            final AccessResultCB cb, final UpdateOption<AccessResultCB> option) {
        assertObjectNotNull("accessResult", accessResult);
        assertCBStateValid(cb);
        prepareUpdateOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(
            accessResult,
            cb,
            option) : 0;
    }

    @Override
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (AccessResultCB) cb);
        } else {
            return varyingQueryUpdate(
                downcast(entity),
                (AccessResultCB) cb,
                downcast(option));
        }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * 
     * <pre>
     * AccessResultCB cb = new AccessResultCB();
     * cb.query().setFoo...(value);
     * accessResultBhv.<span style="color: #FD4747">queryDelete</span>(accessResult, cb);
     * </pre>
     * 
     * @param cb
     *            The condition-bean of AccessResult. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException
     *                When the query has no condition.
     */
    public int queryDelete(final AccessResultCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final AccessResultCB cb,
            final DeleteOption<AccessResultCB> option) {
        assertCBStateValid(cb);
        prepareDeleteOption(option);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(
            cb,
            option) : 0;
    }

    @Override
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((AccessResultCB) cb);
        } else {
            return varyingQueryDelete((AccessResultCB) cb, downcast(option));
        }
    }

    // ===================================================================================
    // Varying Update
    // ==============
    // -----------------------------------------------------
    // Entity Update
    // -------------
    /**
     * Insert the entity with varying requests. <br />
     * For example, disableCommonColumnAutoSetup(), disablePrimaryKeyIdentity(). <br />
     * Other specifications are same as insert(entity).
     * 
     * <pre>
     * AccessResult accessResult = new AccessResult();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * accessResult.setFoo...(value);
     * accessResult.setBar...(value);
     * InsertOption<AccessResultCB> option = new InsertOption<AccessResultCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * accessResultBhv.<span style="color: #FD4747">varyingInsert</span>(accessResult, option);
     * ... = accessResult.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * 
     * @param accessResult
     *            The entity of insert target. (NotNull, PrimaryKeyNullAllowed:
     *            when auto-increment)
     * @param option
     *            The option of insert for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (unique constraint
     *                violation)
     */
    public void varyingInsert(final AccessResult accessResult,
            final InsertOption<AccessResultCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(accessResult, option);
    }

    /**
     * Update the entity with varying requests modified-only.
     * (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification),
     * specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * 
     * <pre>
     * AccessResult accessResult = new AccessResult();
     * accessResult.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * accessResult.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * accessResult.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;AccessResultCB&gt; option = new UpdateOption&lt;AccessResultCB&gt;();
     *     option.self(new SpecifyQuery&lt;AccessResultCB&gt;() {
     *         public void specify(AccessResultCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     accessResultBhv.<span style="color: #FD4747">varyingUpdate</span>(accessResult, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * 
     * @param accessResult
     *            The entity of update target. (NotNull, PrimaryKeyNotNull,
     *            ConcurrencyColumnRequired)
     * @param option
     *            The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (unique constraint
     *                violation)
     */
    public void varyingUpdate(final AccessResult accessResult,
            final UpdateOption<AccessResultCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(accessResult, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl:
     * when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * 
     * @param accessResult
     *            The entity of insert or update target. (NotNull)
     * @param insertOption
     *            The option of insert for varying requests. (NotNull)
     * @param updateOption
     *            The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (unique constraint
     *                violation)
     */
    public void varyingInsertOrUpdate(final AccessResult accessResult,
            final InsertOption<AccessResultCB> insertOption,
            final UpdateOption<AccessResultCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(accessResult, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException,
     * NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * 
     * @param accessResult
     *            The entity of delete target. (NotNull, PrimaryKeyNotNull,
     *            ConcurrencyColumnRequired)
     * @param option
     *            The option of update for varying requests. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted. (not found)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     */
    public void varyingDelete(final AccessResult accessResult,
            final DeleteOption<AccessResultCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(accessResult, option);
    }

    // -----------------------------------------------------
    // Batch Update
    // ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup() ,
     * disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * 
     * @param accessResultList
     *            The list of the entity. (NotNull, EmptyAllowed,
     *            PrimaryKeyNotNull)
     * @param option
     *            The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(final List<AccessResult> accessResultList,
            final InsertOption<AccessResultCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(accessResultList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification),
     * specify(updateColumnSpecification) , disableCommonColumnAutoSetup(),
     * limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * 
     * @param accessResultList
     *            The list of the entity. (NotNull, EmptyAllowed,
     *            PrimaryKeyNotNull)
     * @param option
     *            The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(final List<AccessResult> accessResultList,
            final UpdateOption<AccessResultCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(accessResultList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * 
     * @param accessResultList
     *            The list of the entity. (NotNull, EmptyAllowed,
     *            PrimaryKeyNotNull)
     * @param option
     *            The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(final List<AccessResult> accessResultList,
            final DeleteOption<AccessResultCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(accessResultList, option);
    }

    // -----------------------------------------------------
    // Query Update
    // ------------
    /**
     * Insert the several entities by query with varying requests (modified-only
     * for fixed value). <br />
     * For example, disableCommonColumnAutoSetup(), disablePrimaryKeyIdentity(). <br />
     * Other specifications are same as queryInsert(entity, setupper).
     * 
     * @param setupper
     *            The setup-per of query-insert. (NotNull)
     * @param option
     *            The option of insert for varying requests. (NotNull)
     * @return The inserted count.
     */
    public int varyingQueryInsert(
            final QueryInsertSetupper<AccessResult, AccessResultCB> setupper,
            final InsertOption<AccessResultCB> option) {
        assertInsertOptionNotNull(option);
        return doQueryInsert(setupper, option);
    }

    /**
     * Update the several entities by query with varying requests non-strictly
     * modified-only. {NonExclusiveControl} <br />
     * For example, self(selfCalculationSpecification),
     * specify(updateColumnSpecification) , disableCommonColumnAutoSetup(),
     * allowNonQueryUpdate(). <br />
     * Other specifications are same as queryUpdate(entity, cb).
     * 
     * <pre>
     * <span style="color: #3F7E5E">// ex) you can update by self calculation values</span>
     * AccessResult accessResult = new AccessResult();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//accessResult.setPK...(value);</span>
     * accessResult.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//accessResult.setVersionNo(value);</span>
     * AccessResultCB cb = new AccessResultCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;AccessResultCB&gt; option = new UpdateOption&lt;AccessResultCB&gt;();
     * option.self(new SpecifyQuery&lt;AccessResultCB&gt;() {
     *     public void specify(AccessResultCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * accessResultBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(accessResult, cb, option);
     * </pre>
     * 
     * @param accessResult
     *            The entity that contains update values. (NotNull)
     *            {PrimaryKeyNotRequired}
     * @param cb
     *            The condition-bean of AccessResult. (NotNull)
     * @param option
     *            The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.dbflute.exception.NonQueryUpdateNotAllowedException
     *                When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final AccessResult accessResult,
            final AccessResultCB cb, final UpdateOption<AccessResultCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(accessResult, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * 
     * @param cb
     *            The condition-bean of AccessResult. (NotNull)
     * @param option
     *            The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.dbflute.exception.NonQueryDeleteNotAllowedException
     *                When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final AccessResultCB cb,
            final DeleteOption<AccessResultCB> option) {
        assertDeleteOptionNotNull(option);
        return doQueryDelete(cb, option);
    }

    // ===================================================================================
    // OutsideSql
    // ==========
    /**
     * Prepare the basic executor of outside-SQL to execute it. <br />
     * The invoker of behavior command should be not null when you call this
     * method.
     * 
     * <pre>
     * You can use the methods for outside-SQL are as follows:
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
     * 
     * @return The basic executor of outside-SQL. (NotNull)
     */
    public OutsideSqlBasicExecutor<AccessResultBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    // Delegate Method
    // ===============
    // [Behavior Command]
    // -----------------------------------------------------
    // Select
    // ------
    protected int delegateSelectCountUniquely(final AccessResultCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(final AccessResultCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends AccessResult> void delegateSelectCursor(
            final AccessResultCB cb, final EntityRowHandler<ENTITY> erh,
            final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends AccessResult> List<ENTITY> delegateSelectList(
            final AccessResultCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    protected <RESULT> RESULT delegateSelectNextVal(final Class<RESULT> rt) {
        return invoke(createSelectNextValCommand(rt));
    }

    // -----------------------------------------------------
    // Update
    // ------
    protected int delegateInsert(final AccessResult e,
            final InsertOption<AccessResultCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final AccessResult e,
            final UpdateOption<AccessResultCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return delegateUpdateNonstrict(e, op);
    }

    protected int delegateUpdateNonstrict(final AccessResult e,
            final UpdateOption<AccessResultCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final AccessResult e,
            final DeleteOption<AccessResultCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return delegateDeleteNonstrict(e, op);
    }

    protected int delegateDeleteNonstrict(final AccessResult e,
            final DeleteOption<AccessResultCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(final List<AccessResult> ls,
            final InsertOption<AccessResultCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(
            processBatchInternally(ls, op),
            op));
    }

    protected int[] delegateBatchUpdate(final List<AccessResult> ls,
            final UpdateOption<AccessResultCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchUpdateNonstrict(ls, op);
    }

    protected int[] delegateBatchUpdateNonstrict(final List<AccessResult> ls,
            final UpdateOption<AccessResultCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
            processBatchInternally(ls, op, true),
            op));
    }

    protected int[] delegateBatchDelete(final List<AccessResult> ls,
            final DeleteOption<AccessResultCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchDeleteNonstrict(ls, op);
    }

    protected int[] delegateBatchDeleteNonstrict(final List<AccessResult> ls,
            final DeleteOption<AccessResultCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
            processBatchInternally(ls, op, true),
            op));
    }

    protected int delegateQueryInsert(final AccessResult e,
            final AccessResultCB inCB, final ConditionBean resCB,
            final InsertOption<AccessResultCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final AccessResult e,
            final AccessResultCB cb, final UpdateOption<AccessResultCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final AccessResultCB cb,
            final DeleteOption<AccessResultCB> op) {
        if (!processBeforeQueryDelete(cb, op)) {
            return 0;
        }
        return invoke(createQueryDeleteCBCommand(cb, op));
    }

    // ===================================================================================
    // Optimistic Lock Info
    // ====================
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasVersionNoValue(final Entity entity) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasUpdateDateValue(final Entity entity) {
        return false;
    }

    // ===================================================================================
    // Downcast Helper
    // ===============
    protected AccessResult downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity, AccessResult.class);
    }

    protected AccessResultCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb, AccessResultCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<AccessResult> downcast(
            final List<? extends Entity> entityList) {
        return (List<AccessResult>) entityList;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<AccessResultCB> downcast(
            final InsertOption<? extends ConditionBean> option) {
        return (InsertOption<AccessResultCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<AccessResultCB> downcast(
            final UpdateOption<? extends ConditionBean> option) {
        return (UpdateOption<AccessResultCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<AccessResultCB> downcast(
            final DeleteOption<? extends ConditionBean> option) {
        return (DeleteOption<AccessResultCB>) option;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<AccessResult, AccessResultCB> downcast(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> option) {
        return (QueryInsertSetupper<AccessResult, AccessResultCB>) option;
    }
}
