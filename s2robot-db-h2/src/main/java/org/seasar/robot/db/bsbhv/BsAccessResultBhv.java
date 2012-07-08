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
package org.seasar.robot.db.bsbhv;

import java.util.List;

import org.seasar.robot.db.bsentity.dbmeta.AccessResultDbm;
import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.exentity.AccessResult;
import org.seasar.robot.db.exentity.AccessResultData;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.bhv.AbstractBehaviorWritable;
import org.seasar.robot.dbflute.bhv.DeleteOption;
import org.seasar.robot.dbflute.bhv.InsertOption;
import org.seasar.robot.dbflute.bhv.QueryInsertSetupper;
import org.seasar.robot.dbflute.bhv.UpdateOption;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.EntityRowHandler;
import org.seasar.robot.dbflute.cbean.ListResultBean;
import org.seasar.robot.dbflute.cbean.PagingResultBean;
import org.seasar.robot.dbflute.cbean.SpecifyQuery;
import org.seasar.robot.dbflute.dbmeta.DBMeta;

/**
 * The behavior of ACCESS_RESULT as TABLE. <br />
 * 
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, SESSION_ID, RULE_ID, URL, PARENT_URL, STATUS, HTTP_STATUS_CODE, METHOD, MIME_TYPE, CONTENT_LENGTH, EXECUTION_TIME, LAST_MODIFIED, CREATE_TIME
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     ID
 * 
 * [version-no]
 *     
 * 
 * [foreign-table]
 *     ACCESS_RESULT_DATA(AsOne)
 * 
 * [referrer-table]
 *     ACCESS_RESULT_DATA
 * 
 * [foreign-property]
 *     accessResultDataAsOne
 * 
 * [referrer-property]
 *     
 * </pre>
 * 
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsAccessResultBhv extends AbstractBehaviorWritable {

    // ===================================================================================
    // Definition
    // ==========
    /* df:BehaviorQueryPathBegin */
    public static final String PATH_selectListByUrlDiff = "selectListByUrlDiff";

    public static final String PATH_deleteAll = "deleteAll";

    public static final String PATH_deleteBySessionId = "deleteBySessionId";

    /* df:BehaviorQueryPathEnd */

    // ===================================================================================
    // Table name
    // ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() {
        return "ACCESS_RESULT";
    }

    // ===================================================================================
    // DBMeta
    // ======
    /** @return The instance of DBMeta. (NotNull) */
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
    public Entity newEntity() {
        return newMyEntity();
    }

    /** {@inheritDoc} */
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
     * @return The selected count.
     */
    public int selectCount(final AccessResultCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final AccessResultCB cb) { // called by
                                                                   // selectCount(cb)
        assertCBNotNull(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final AccessResultCB cb) { // called by
                                                                  // selectPage(cb)
        assertCBNotNull(cb);
        return delegateSelectCountPlainly(cb);
    }

    @Override
    protected int doReadCount(final ConditionBean cb) {
        return selectCount(downcast(cb));
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
        assertCBNotNull(cb);
        assertObjectNotNull("entityRowHandler<AccessResult>", entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        delegateSelectCursor(cb, entityRowHandler, entityType);
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
     * @return The selected entity. (NullAllowed: If the condition has no data,
     *         it returns null)
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.SelectEntityConditionNotFoundException
     *                When the condition for selecting an entity is not found.
     */
    public AccessResult selectEntity(final AccessResultCB cb) {
        return doSelectEntity(cb, AccessResult.class);
    }

    protected <ENTITY extends AccessResult> ENTITY doSelectEntity(
            final AccessResultCB cb, final Class<ENTITY> entityType) {
        return helpSelectEntityInternally(
            cb,
            new InternalSelectEntityCallback<ENTITY, AccessResultCB>() {
                public List<ENTITY> callbackSelectList(final AccessResultCB cb) {
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
     * @return The selected entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.SelectEntityConditionNotFoundException
     *                When the condition for selecting an entity is not found.
     */
    public AccessResult selectEntityWithDeletedCheck(final AccessResultCB cb) {
        return doSelectEntityWithDeletedCheck(cb, AccessResult.class);
    }

    protected <ENTITY extends AccessResult> ENTITY doSelectEntityWithDeletedCheck(
            final AccessResultCB cb, final Class<ENTITY> entityType) {
        return helpSelectEntityWithDeletedCheckInternally(
            cb,
            new InternalSelectEntityWithDeletedCheckCallback<ENTITY, AccessResultCB>() {
                public List<ENTITY> callbackSelectList(final AccessResultCB cb) {
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
     * @return The selected entity. (NullAllowed: If the primary-key value has
     *         no data, it returns null)
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.SelectEntityConditionNotFoundException
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
     * @return The selected entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.SelectEntityConditionNotFoundException
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
     * @return The result bean of selected list. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.DangerousResultSizeException
     *                When the result size is over the specified safety size.
     */
    public ListResultBean<AccessResult> selectList(final AccessResultCB cb) {
        return doSelectList(cb, AccessResult.class);
    }

    protected <ENTITY extends AccessResult> ListResultBean<ENTITY> doSelectList(
            final AccessResultCB cb, final Class<ENTITY> entityType) {
        assertCBNotNull(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(
            cb,
            entityType,
            new InternalSelectListCallback<ENTITY, AccessResultCB>() {
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
     * @return The result bean of selected page. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.DangerousResultSizeException
     *                When the result size is over the specified safety size.
     */
    public PagingResultBean<AccessResult> selectPage(final AccessResultCB cb) {
        return doSelectPage(cb, AccessResult.class);
    }

    protected <ENTITY extends AccessResult> PagingResultBean<ENTITY> doSelectPage(
            final AccessResultCB cb, final Class<ENTITY> entityType) {
        assertCBNotNull(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(
            cb,
            entityType,
            new InternalSelectPageCallback<ENTITY, AccessResultCB>() {
                public int callbackSelectCount(final AccessResultCB cb) {
                    return doSelectCountPlainly(cb);
                }

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
        assertCBNotNull(cb);
        cb.xsetupForScalarSelect();
        cb.getSqlClause().disableSelectIndex(); // for when you use union
        return new SLFunction<CB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    // Sequence
    // ========
    @Override
    protected Number doReadNextVal() {
        final String msg =
            "This table is NOT related to sequence: " + getTableDbName();
        throw new UnsupportedOperationException(msg);
    }

    // ===================================================================================
    // Pull out Foreign
    // ================
    /**
     * Pull out the list of referrer-as-one table 'AccessResultData'.
     * 
     * @param accessResultList
     *            The list of accessResult. (NotNull)
     * @return The list of referrer-as-one table. (NotNull)
     */
    public List<AccessResultData> pulloutAccessResultDataAsOne(
            final List<AccessResult> accessResultList) {
        return helpPulloutInternally(
            accessResultList,
            new InternalPulloutCallback<AccessResult, AccessResultData>() {
                public AccessResultData getFr(final AccessResult e) {
                    return e.getAccessResultDataAsOne();
                }

                public boolean hasRf() {
                    return true;
                }

                public void setRfLs(final AccessResultData e,
                        final List<AccessResult> ls) {
                    if (!ls.isEmpty()) {
                        e.setAccessResult(ls.get(0));
                    }
                }
            });
    }

    // ===================================================================================
    // Entity Update
    // =============
    /**
     * Insert the entity.
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
     * 
     * @param accessResult
     *            The entity of insert target. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (Unique Constraint
     *                Violation)
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
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doCreate(final Entity entity,
            final InsertOption<? extends ConditionBean> option) {
        if (option == null) {
            insert(downcast(entity));
        } else {
            varyingInsert(downcast(entity), (InsertOption) option);
        }
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException,
     * ExclusiveControl}
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
     *            The entity of update target. (NotNull) {PrimaryKeyRequired,
     *            ConcurrencyColumnRequired}
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (Unique Constraint
     *                Violation)
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
    @SuppressWarnings("unchecked")
    protected void doModify(final Entity entity,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            update(downcast(entity));
        } else {
            varyingUpdate(downcast(entity), (UpdateOption) option);
        }
    }

    @Override
    protected void doModifyNonstrict(final Entity entity,
            final UpdateOption<? extends ConditionBean> option) {
        doModify(entity, option);
    }

    /**
     * Insert or update the entity modified-only. {ExclusiveControl(when
     * update)}
     * 
     * @param accessResult
     *            The entity of insert or update target. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (Unique Constraint
     *                Violation)
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
                public void callbackInsert(final AccessResult entity) {
                    doInsert(entity, insertOption);
                }

                public void callbackUpdate(final AccessResult entity) {
                    doUpdate(entity, updateOption);
                }

                public AccessResultCB callbackNewMyConditionBean() {
                    return newMyConditionBean();
                }

                public int callbackSelectCount(final AccessResultCB cb) {
                    return selectCount(cb);
                }
            });
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doCreateOrModify(final Entity entity,
            InsertOption<? extends ConditionBean> insertOption,
            UpdateOption<? extends ConditionBean> updateOption) {
        if (insertOption == null && updateOption == null) {
            insertOrUpdate(downcast(entity));
        } else {
            insertOption =
                insertOption == null ? new InsertOption() : insertOption;
            updateOption =
                updateOption == null ? new UpdateOption() : updateOption;
            varyingInsertOrUpdate(
                downcast(entity),
                (InsertOption) insertOption,
                (UpdateOption) updateOption);
        }
    }

    @Override
    protected void doCreateOrModifyNonstrict(final Entity entity,
            final InsertOption<? extends ConditionBean> insertOption,
            final UpdateOption<? extends ConditionBean> updateOption) {
        doCreateOrModify(entity, insertOption, updateOption);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ExclusiveControl}
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
     *            The entity of delete target. (NotNull) {PrimaryKeyRequired,
     *            ConcurrencyColumnRequired}
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
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
    @SuppressWarnings("unchecked")
    protected void doRemove(final Entity entity,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            delete(downcast(entity));
        } else {
            varyingDelete(downcast(entity), (DeleteOption) option);
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
     * Batch-insert the list. <br />
     * This method uses 'Batch Update' of java.sql.PreparedStatement. <br />
     * All columns are insert target. (so default constraints are not available) <br />
     * And if the table has an identity, entities after the process do not have
     * incremented values. (When you use the (normal) insert(), an entity after
     * the process has an incremented value)
     * 
     * @param accessResultList
     *            The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(final List<AccessResult> accessResultList) {
        return doBatchInsert(accessResultList, null);
    }

    protected int[] doBatchInsert(final List<AccessResult> accessResultList,
            final InsertOption<AccessResultCB> option) {
        assertObjectNotNull("accessResultList", accessResultList);
        prepareInsertOption(option);
        return delegateBatchInsert(accessResultList, option);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int[] doLumpCreate(final List<Entity> ls,
            final InsertOption<? extends ConditionBean> option) {
        if (option == null) {
            return batchInsert((List) ls);
        } else {
            return varyingBatchInsert((List) ls, (InsertOption) option);
        }
    }

    /**
     * Batch-update the list. <br />
     * This method uses 'Batch Update' of java.sql.PreparedStatement. <br />
     * All columns are update target. {NOT modified only}
     * 
     * @param accessResultList
     *            The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     */
    public int[] batchUpdate(final List<AccessResult> accessResultList) {
        return doBatchUpdate(accessResultList, null);
    }

    protected int[] doBatchUpdate(final List<AccessResult> accessResultList,
            final UpdateOption<AccessResultCB> option) {
        assertObjectNotNull("accessResultList", accessResultList);
        prepareUpdateOption(option);
        return delegateBatchUpdate(accessResultList, option);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int[] doLumpModify(final List<Entity> ls,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return batchUpdate((List) ls);
        } else {
            return varyingBatchUpdate((List) ls, (UpdateOption) option);
        }
    }

    /**
     * Batch-update the list. <br />
     * This method uses 'Batch Update' of java.sql.PreparedStatement. <br />
     * You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update and an
     * optimistick lock column because they are specified implicitly.
     * 
     * @param accessResultList
     *            The list of the entity. (NotNull)
     * @param updateColumnSpec
     *            The specification of update columns. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
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
     * Batch-delete the list. <br />
     * This method uses 'Batch Update' of java.sql.PreparedStatement.
     * 
     * @param accessResultList
     *            The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
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
    @SuppressWarnings("unchecked")
    protected int[] doLumpRemove(final List<Entity> ls,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return batchDelete((List) ls);
        } else {
            return varyingBatchDelete((List) ls, (DeleteOption) option);
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
     * accessResultBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;accessResult, AccessResultCB&gt;() {
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
    @SuppressWarnings("unchecked")
    protected int doRangeCreate(
            final QueryInsertSetupper<? extends Entity, ? extends ConditionBean> setupper,
            final InsertOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryInsert((QueryInsertSetupper) setupper);
        } else {
            return varyingQueryInsert(
                (QueryInsertSetupper) setupper,
                (InsertOption) option);
        }
    }

    /**
     * Update the several entities by query non-strictly modified-only.
     * {NonExclusiveControl}
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
     * @exception org.seasar.robot.dbflute.exception.NonQueryUpdateNotAllowedException
     *                When the query has no condition.
     */
    public int queryUpdate(final AccessResult accessResult,
            final AccessResultCB cb) {
        return doQueryUpdate(accessResult, cb, null);
    }

    protected int doQueryUpdate(final AccessResult accessResult,
            final AccessResultCB cb, final UpdateOption<AccessResultCB> option) {
        assertObjectNotNull("accessResult", accessResult);
        assertCBNotNull(cb);
        prepareUpdateOption(option);
        return delegateQueryUpdate(accessResult, cb, option);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (AccessResultCB) cb);
        } else {
            return varyingQueryUpdate(
                downcast(entity),
                (AccessResultCB) cb,
                (UpdateOption) option);
        }
    }

    /**
     * Delete the several entities by query. {NonExclusiveControl}
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
     * @exception org.seasar.robot.dbflute.exception.NonQueryDeleteNotAllowedException
     *                When the query has no condition.
     */
    public int queryDelete(final AccessResultCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final AccessResultCB cb,
            final DeleteOption<AccessResultCB> option) {
        assertCBNotNull(cb);
        prepareDeleteOption(option);
        return delegateQueryDelete(cb, option);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((AccessResultCB) cb);
        } else {
            return varyingQueryDelete(
                (AccessResultCB) cb,
                (DeleteOption) option);
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
     *            The entity of insert target. (NotNull)
     * @param option
     *            The option of insert for varying requests. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (Unique Constraint
     *                Violation)
     */
    public void varyingInsert(final AccessResult accessResult,
            final InsertOption<AccessResultCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(accessResult, option);
    }

    /**
     * Update the entity with varying requests modified-only.
     * {UpdateCountZeroException, ExclusiveControl} <br />
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
     *            The entity of update target. (NotNull) {PrimaryKeyRequired,
     *            ConcurrencyColumnRequired}
     * @param option
     *            The option of update for varying requests. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (Unique Constraint
     *                Violation)
     */
    public void varyingUpdate(final AccessResult accessResult,
            final UpdateOption<AccessResultCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(accessResult, option);
    }

    /**
     * Insert or update the entity with varying requests. {ExclusiveControl(when
     * update)}<br />
     * Other specifications are same as insertOrUpdate(entity).
     * 
     * @param accessResult
     *            The entity of insert or update target. (NotNull)
     * @param insertOption
     *            The option of insert for varying requests. (NotNull)
     * @param updateOption
     *            The option of update for varying requests. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (Unique Constraint
     *                Violation)
     */
    public void varyingInsertOrUpdate(final AccessResult accessResult,
            final InsertOption<AccessResultCB> insertOption,
            final UpdateOption<AccessResultCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(accessResult, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. {UpdateCountZeroException,
     * ExclusiveControl} <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * 
     * @param accessResult
     *            The entity of delete target. (NotNull) {PrimaryKeyRequired,
     *            ConcurrencyColumnRequired}
     * @param option
     *            The option of update for varying requests. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
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
     *            The list of the entity. (NotNull)
     * @param option
     *            The option of insert for varying requests. (NotNull)
     * @return The array of inserted count.
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
     *            The list of the entity. (NotNull)
     * @param option
     *            The option of update for varying requests. (NotNull)
     * @return The array of updated count.
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
     *            The list of the entity. (NotNull)
     * @param option
     *            The option of delete for varying requests. (NotNull)
     * @return The array of deleted count.
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
     * @exception org.seasar.robot.dbflute.exception.NonQueryUpdateNotAllowedException
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
     * @exception org.seasar.robot.dbflute.exception.NonQueryDeleteNotAllowedException
     *                When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final AccessResultCB cb,
            final DeleteOption<AccessResultCB> option) {
        assertDeleteOptionNotNull(option);
        return doQueryDelete(cb, option);
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
}
