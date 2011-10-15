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

import org.seasar.robot.db.bsentity.dbmeta.AccessResultDataDbm;
import org.seasar.robot.db.cbean.AccessResultDataCB;
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
 * The behavior of ACCESS_RESULT_DATA as TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, TRANSFORMER_NAME, DATA, ENCODING
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     
 * 
 * [version-no]
 *     
 * 
 * [foreign-table]
 *     ACCESS_RESULT
 * 
 * [referrer-table]
 *     
 * 
 * [foreign-property]
 *     accessResult
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsAccessResultDataBhv extends AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:BehaviorQueryPathBegin*/
    /*df:BehaviorQueryPathEnd*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() {
        return "ACCESS_RESULT_DATA";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() {
        return AccessResultDataDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public AccessResultDataDbm getMyDBMeta() {
        return AccessResultDataDbm.getInstance();
    }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    /** {@inheritDoc} */
    public Entity newEntity() {
        return newMyEntity();
    }

    /** {@inheritDoc} */
    public ConditionBean newConditionBean() {
        return newMyConditionBean();
    }

    /** @return The instance of new entity as my table type. (NotNull) */
    public AccessResultData newMyEntity() {
        return new AccessResultData();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public AccessResultDataCB newMyConditionBean() {
        return new AccessResultDataCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * AccessResultDataCB cb = new AccessResultDataCB();
     * cb.query().setFoo...(value);
     * int count = accessResultDataBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @return The selected count.
     */
    public int selectCount(AccessResultDataCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(AccessResultDataCB cb) { // called by selectCount(cb) 
        assertCBNotNull(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(AccessResultDataCB cb) { // called by selectPage(cb)
        assertCBNotNull(cb);
        return delegateSelectCountPlainly(cb);
    }

    @Override
    protected int doReadCount(ConditionBean cb) {
        return selectCount(downcast(cb));
    }

    // ===================================================================================
    //                                                                       Cursor Select
    //                                                                       =============
    /**
     * Select the cursor by the condition-bean.
     * <pre>
     * AccessResultDataCB cb = new AccessResultDataCB();
     * cb.query().setFoo...(value);
     * accessResultDataBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;AccessResultData&gt;() {
     *     public void handle(AccessResultData entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @param entityRowHandler The handler of entity row of AccessResultData. (NotNull)
     */
    public void selectCursor(AccessResultDataCB cb,
            EntityRowHandler<AccessResultData> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, AccessResultData.class);
    }

    protected <ENTITY extends AccessResultData> void doSelectCursor(
            AccessResultDataCB cb, EntityRowHandler<ENTITY> entityRowHandler,
            Class<ENTITY> entityType) {
        assertCBNotNull(cb);
        assertObjectNotNull("entityRowHandler<AccessResultData>",
                entityRowHandler);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        delegateSelectCursor(cb, entityRowHandler, entityType);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * <pre>
     * AccessResultDataCB cb = new AccessResultDataCB();
     * cb.query().setFoo...(value);
     * AccessResultData accessResultData = accessResultDataBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (accessResultData != null) {
     *     ... = accessResultData.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @return The selected entity. (NullAllowed: If the condition has no data, it returns null)
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public AccessResultData selectEntity(AccessResultDataCB cb) {
        return doSelectEntity(cb, AccessResultData.class);
    }

    protected <ENTITY extends AccessResultData> ENTITY doSelectEntity(
            final AccessResultDataCB cb, final Class<ENTITY> entityType) {
        return helpSelectEntityInternally(cb,
                new InternalSelectEntityCallback<ENTITY, AccessResultDataCB>() {
                    public List<ENTITY> callbackSelectList(AccessResultDataCB cb) {
                        return doSelectList(cb, entityType);
                    }
                });
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return selectEntity(downcast(cb));
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * <pre>
     * AccessResultDataCB cb = new AccessResultDataCB();
     * cb.query().setFoo...(value);
     * AccessResultData accessResultData = accessResultDataBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = accessResultData.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public AccessResultData selectEntityWithDeletedCheck(AccessResultDataCB cb) {
        return doSelectEntityWithDeletedCheck(cb, AccessResultData.class);
    }

    protected <ENTITY extends AccessResultData> ENTITY doSelectEntityWithDeletedCheck(
            final AccessResultDataCB cb, final Class<ENTITY> entityType) {
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                new InternalSelectEntityWithDeletedCheckCallback<ENTITY, AccessResultDataCB>() {
                    public List<ENTITY> callbackSelectList(AccessResultDataCB cb) {
                        return doSelectList(cb, entityType);
                    }
                });
    }

    @Override
    protected Entity doReadEntityWithDeletedCheck(ConditionBean cb) {
        return selectEntityWithDeletedCheck(downcast(cb));
    }

    /**
     * Select the entity by the primary-key value.
     * @param id The one of primary key. (NotNull)
     * @return The selected entity. (NullAllowed: If the primary-key value has no data, it returns null)
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public AccessResultData selectByPKValue(Long id) {
        return doSelectByPKValue(id, AccessResultData.class);
    }

    protected <ENTITY extends AccessResultData> ENTITY doSelectByPKValue(
            Long id, Class<ENTITY> entityType) {
        return doSelectEntity(buildPKCB(id), entityType);
    }

    /**
     * Select the entity by the primary-key value with deleted check.
     * @param id The one of primary key. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public AccessResultData selectByPKValueWithDeletedCheck(Long id) {
        return doSelectByPKValueWithDeletedCheck(id, AccessResultData.class);
    }

    protected <ENTITY extends AccessResultData> ENTITY doSelectByPKValueWithDeletedCheck(
            Long id, Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private AccessResultDataCB buildPKCB(Long id) {
        assertObjectNotNull("id", id);
        AccessResultDataCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * AccessResultDataCB cb = new AccessResultDataCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;AccessResultData&gt; accessResultDataList = accessResultDataBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (AccessResultData accessResultData : accessResultDataList) {
     *     ... = accessResultData.get...();
     * }
     * </pre>
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @return The result bean of selected list. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<AccessResultData> selectList(AccessResultDataCB cb) {
        return doSelectList(cb, AccessResultData.class);
    }

    protected <ENTITY extends AccessResultData> ListResultBean<ENTITY> doSelectList(
            AccessResultDataCB cb, Class<ENTITY> entityType) {
        assertCBNotNull(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(cb, entityType,
                new InternalSelectListCallback<ENTITY, AccessResultDataCB>() {
                    public List<ENTITY> callbackSelectList(
                            AccessResultDataCB cb, Class<ENTITY> entityType) {
                        return delegateSelectList(cb, entityType);
                    }
                });
    }

    @Override
    protected ListResultBean<? extends Entity> doReadList(ConditionBean cb) {
        return selectList(downcast(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean. <br />
     * (both count-select and paging-select are executed)
     * <pre>
     * AccessResultDataCB cb = new AccessResultDataCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;AccessResultData&gt; page = accessResultDataBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (AccessResultData accessResultData : page) {
     *     ... = accessResultData.get...();
     * }
     * </pre>
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @return The result bean of selected page. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<AccessResultData> selectPage(AccessResultDataCB cb) {
        return doSelectPage(cb, AccessResultData.class);
    }

    protected <ENTITY extends AccessResultData> PagingResultBean<ENTITY> doSelectPage(
            AccessResultDataCB cb, Class<ENTITY> entityType) {
        assertCBNotNull(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(cb, entityType,
                new InternalSelectPageCallback<ENTITY, AccessResultDataCB>() {
                    public int callbackSelectCount(AccessResultDataCB cb) {
                        return doSelectCountPlainly(cb);
                    }

                    public List<ENTITY> callbackSelectList(
                            AccessResultDataCB cb, Class<ENTITY> entityType) {
                        return doSelectList(cb, entityType);
                    }
                });
    }

    @Override
    protected PagingResultBean<? extends Entity> doReadPage(ConditionBean cb) {
        return selectPage(downcast(cb));
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    /**
     * Select the scalar value derived by a function from uniquely-selected records. <br />
     * You should call a function method after this method called like as follows:
     * <pre>
     * accessResultDataBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(AccessResultDataCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (NullAllowed)
     */
    public <RESULT> SLFunction<AccessResultDataCB, RESULT> scalarSelect(
            Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends AccessResultDataCB> SLFunction<CB, RESULT> doScalarSelect(
            Class<RESULT> resultType, CB cb) {
        assertObjectNotNull("resultType", resultType);
        assertCBNotNull(cb);
        cb.xsetupForScalarSelect();
        cb.getSqlClause().disableSelectIndex(); // for when you use union
        return new SLFunction<CB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    @Override
    protected Number doReadNextVal() {
        String msg = "This table is NOT related to sequence: "
                + getTableDbName();
        throw new UnsupportedOperationException(msg);
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    /**
     * Pull out the list of foreign table 'AccessResult'.
     * @param accessResultDataList The list of accessResultData. (NotNull)
     * @return The list of foreign table. (NotNull)
     */
    public List<AccessResult> pulloutAccessResult(
            List<AccessResultData> accessResultDataList) {
        return helpPulloutInternally(accessResultDataList,
                new InternalPulloutCallback<AccessResultData, AccessResult>() {
                    public AccessResult getFr(AccessResultData e) {
                        return e.getAccessResult();
                    }

                    public boolean hasRf() {
                        return true;
                    }

                    public void setRfLs(AccessResult e,
                            List<AccessResultData> ls) {
                        if (!ls.isEmpty()) {
                            e.setAccessResultDataAsOne(ls.get(0));
                        }
                    }
                });
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity.
     * <pre>
     * AccessResultData accessResultData = new AccessResultData();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * accessResultData.setFoo...(value);
     * accessResultData.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//accessResultData.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//accessResultData.set...;</span>
     * accessResultDataBhv.<span style="color: #FD4747">insert</span>(accessResultData);
     * ... = accessResultData.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param accessResultData The entity of insert target. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(AccessResultData accessResultData) {
        doInsert(accessResultData, null);
    }

    protected void doInsert(AccessResultData accessResultData,
            InsertOption<AccessResultDataCB> option) {
        assertObjectNotNull("accessResultData", accessResultData);
        prepareInsertOption(option);
        delegateInsert(accessResultData, option);
    }

    protected void prepareInsertOption(InsertOption<AccessResultDataCB> option) {
        if (option == null) {
            return;
        }
        assertInsertOptionStatus(option);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doCreate(Entity entity,
            InsertOption<? extends ConditionBean> option) {
        if (option == null) {
            insert(downcast(entity));
        } else {
            varyingInsert(downcast(entity), (InsertOption) option);
        }
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ExclusiveControl}
     * <pre>
     * AccessResultData accessResultData = new AccessResultData();
     * accessResultData.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * accessResultData.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//accessResultData.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//accessResultData.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * accessResultData.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     accessResultDataBhv.<span style="color: #FD4747">update</span>(accessResultData);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param accessResultData The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final AccessResultData accessResultData) {
        doUpdate(accessResultData, null);
    }

    protected void doUpdate(AccessResultData accessResultData,
            final UpdateOption<AccessResultDataCB> option) {
        assertObjectNotNull("accessResultData", accessResultData);
        prepareUpdateOption(option);
        helpUpdateInternally(accessResultData,
                new InternalUpdateCallback<AccessResultData>() {
                    public int callbackDelegateUpdate(AccessResultData entity) {
                        return delegateUpdate(entity, option);
                    }
                });
    }

    protected void prepareUpdateOption(UpdateOption<AccessResultDataCB> option) {
        if (option == null) {
            return;
        }
        assertUpdateOptionStatus(option);
        if (option.hasSelfSpecification()) {
            option.resolveSelfSpecification(createCBForVaryingUpdate());
        }
        if (option.hasSpecifiedUpdateColumn()) {
            option.resolveUpdateColumnSpecification(createCBForSpecifiedUpdate());
        }
    }

    protected AccessResultDataCB createCBForVaryingUpdate() {
        AccessResultDataCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected AccessResultDataCB createCBForSpecifiedUpdate() {
        AccessResultDataCB cb = newMyConditionBean();
        cb.xsetupForSpecifiedUpdate();
        return cb;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doModify(Entity entity,
            UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            update(downcast(entity));
        } else {
            varyingUpdate(downcast(entity), (UpdateOption) option);
        }
    }

    @Override
    protected void doModifyNonstrict(Entity entity,
            UpdateOption<? extends ConditionBean> option) {
        doModify(entity, option);
    }

    /**
     * Insert or update the entity modified-only. {ExclusiveControl(when update)}
     * @param accessResultData The entity of insert or update target. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(AccessResultData accessResultData) {
        doInesrtOrUpdate(accessResultData, null, null);
    }

    protected void doInesrtOrUpdate(AccessResultData accessResultData,
            final InsertOption<AccessResultDataCB> insertOption,
            final UpdateOption<AccessResultDataCB> updateOption) {
        helpInsertOrUpdateInternally(
                accessResultData,
                new InternalInsertOrUpdateCallback<AccessResultData, AccessResultDataCB>() {
                    public void callbackInsert(AccessResultData entity) {
                        doInsert(entity, insertOption);
                    }

                    public void callbackUpdate(AccessResultData entity) {
                        doUpdate(entity, updateOption);
                    }

                    public AccessResultDataCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    public int callbackSelectCount(AccessResultDataCB cb) {
                        return selectCount(cb);
                    }
                });
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doCreateOrModify(Entity entity,
            InsertOption<? extends ConditionBean> insertOption,
            UpdateOption<? extends ConditionBean> updateOption) {
        if (insertOption == null && updateOption == null) {
            insertOrUpdate(downcast(entity));
        } else {
            insertOption = insertOption == null ? new InsertOption()
                    : insertOption;
            updateOption = updateOption == null ? new UpdateOption()
                    : updateOption;
            varyingInsertOrUpdate(downcast(entity),
                    (InsertOption) insertOption, (UpdateOption) updateOption);
        }
    }

    @Override
    protected void doCreateOrModifyNonstrict(Entity entity,
            InsertOption<? extends ConditionBean> insertOption,
            UpdateOption<? extends ConditionBean> updateOption) {
        doCreateOrModify(entity, insertOption, updateOption);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ExclusiveControl}
     * <pre>
     * AccessResultData accessResultData = new AccessResultData();
     * accessResultData.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * accessResultData.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     accessResultDataBhv.<span style="color: #FD4747">delete</span>(accessResultData);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * @param accessResultData The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(AccessResultData accessResultData) {
        doDelete(accessResultData, null);
    }

    protected void doDelete(AccessResultData accessResultData,
            final DeleteOption<AccessResultDataCB> option) {
        assertObjectNotNull("accessResultData", accessResultData);
        prepareDeleteOption(option);
        helpDeleteInternally(accessResultData,
                new InternalDeleteCallback<AccessResultData>() {
                    public int callbackDelegateDelete(AccessResultData entity) {
                        return delegateDelete(entity, option);
                    }
                });
    }

    protected void prepareDeleteOption(DeleteOption<AccessResultDataCB> option) {
        if (option == null) {
            return;
        }
        assertDeleteOptionStatus(option);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doRemove(Entity entity,
            DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            delete(downcast(entity));
        } else {
            varyingDelete(downcast(entity), (DeleteOption) option);
        }
    }

    @Override
    protected void doRemoveNonstrict(Entity entity,
            DeleteOption<? extends ConditionBean> option) {
        doRemove(entity, option);
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch-insert the list. <br />
     * This method uses 'Batch Update' of java.sql.PreparedStatement. <br />
     * All columns are insert target. (so default constraints are not available) <br />
     * And if the table has an identity, entities after the process do not have incremented values.
     * (When you use the (normal) insert(), an entity after the process has an incremented value)
     * @param accessResultDataList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<AccessResultData> accessResultDataList) {
        return doBatchInsert(accessResultDataList, null);
    }

    protected int[] doBatchInsert(List<AccessResultData> accessResultDataList,
            InsertOption<AccessResultDataCB> option) {
        assertObjectNotNull("accessResultDataList", accessResultDataList);
        prepareInsertOption(option);
        return delegateBatchInsert(accessResultDataList, option);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int[] doLumpCreate(List<Entity> ls,
            InsertOption<? extends ConditionBean> option) {
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
     * @param accessResultDataList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdate(List<AccessResultData> accessResultDataList) {
        return doBatchUpdate(accessResultDataList, null);
    }

    protected int[] doBatchUpdate(List<AccessResultData> accessResultDataList,
            UpdateOption<AccessResultDataCB> option) {
        assertObjectNotNull("accessResultDataList", accessResultDataList);
        prepareUpdateOption(option);
        return delegateBatchUpdate(accessResultDataList, option);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int[] doLumpModify(List<Entity> ls,
            UpdateOption<? extends ConditionBean> option) {
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
     * However you do not need to specify common columns for update
     * and an optimistick lock column because they are specified implicitly.
     * @param accessResultDataList The list of the entity. (NotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdate(List<AccessResultData> accessResultDataList,
            SpecifyQuery<AccessResultDataCB> updateColumnSpec) {
        return doBatchUpdate(accessResultDataList,
                createSpecifiedUpdateOption(updateColumnSpec));
    }

    @Override
    protected int[] doLumpModifyNonstrict(List<Entity> ls,
            UpdateOption<? extends ConditionBean> option) {
        return doLumpModify(ls, option);
    }

    /**
     * Batch-delete the list. <br />
     * This method uses 'Batch Update' of java.sql.PreparedStatement.
     * @param accessResultDataList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDelete(List<AccessResultData> accessResultDataList) {
        return doBatchDelete(accessResultDataList, null);
    }

    protected int[] doBatchDelete(List<AccessResultData> accessResultDataList,
            DeleteOption<AccessResultDataCB> option) {
        assertObjectNotNull("accessResultDataList", accessResultDataList);
        prepareDeleteOption(option);
        return delegateBatchDelete(accessResultDataList, option);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int[] doLumpRemove(List<Entity> ls,
            DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return batchDelete((List) ls);
        } else {
            return varyingBatchDelete((List) ls, (DeleteOption) option);
        }
    }

    @Override
    protected int[] doLumpRemoveNonstrict(List<Entity> ls,
            DeleteOption<? extends ConditionBean> option) {
        return doLumpRemove(ls, option);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Insert the several entities by query (modified-only for fixed value).
     * <pre>
     * accessResultDataBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;accessResultData, AccessResultDataCB&gt;() {
     *     public ConditionBean setup(accessResultData entity, AccessResultDataCB intoCB) {
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
     * @param setupper The setup-per of query-insert. (NotNull)
     * @return The inserted count.
     */
    public int queryInsert(
            QueryInsertSetupper<AccessResultData, AccessResultDataCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            QueryInsertSetupper<AccessResultData, AccessResultDataCB> setupper,
            InsertOption<AccessResultDataCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        AccessResultData entity = new AccessResultData();
        AccessResultDataCB intoCB = createCBForQueryInsert();
        ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected AccessResultDataCB createCBForQueryInsert() {
        AccessResultDataCB cb = newMyConditionBean();
        cb.xsetupForQueryInsert();
        return cb;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int doRangeCreate(
            QueryInsertSetupper<? extends Entity, ? extends ConditionBean> setupper,
            InsertOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryInsert((QueryInsertSetupper) setupper);
        } else {
            return varyingQueryInsert((QueryInsertSetupper) setupper,
                    (InsertOption) option);
        }
    }

    /**
     * Update the several entities by query non-strictly modified-only. {NonExclusiveControl}
     * <pre>
     * AccessResultData accessResultData = new AccessResultData();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//accessResultData.setPK...(value);</span>
     * accessResultData.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//accessResultData.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//accessResultData.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//accessResultData.setVersionNo(value);</span>
     * AccessResultDataCB cb = new AccessResultDataCB();
     * cb.query().setFoo...(value);
     * accessResultDataBhv.<span style="color: #FD4747">queryUpdate</span>(accessResultData, cb);
     * </pre>
     * @param accessResultData The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @return The updated count.
     * @exception org.seasar.robot.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(AccessResultData accessResultData,
            AccessResultDataCB cb) {
        return doQueryUpdate(accessResultData, cb, null);
    }

    protected int doQueryUpdate(AccessResultData accessResultData,
            AccessResultDataCB cb, UpdateOption<AccessResultDataCB> option) {
        assertObjectNotNull("accessResultData", accessResultData);
        assertCBNotNull(cb);
        prepareUpdateOption(option);
        return delegateQueryUpdate(accessResultData, cb, option);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int doRangeModify(Entity entity, ConditionBean cb,
            UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (AccessResultDataCB) cb);
        } else {
            return varyingQueryUpdate(downcast(entity),
                    (AccessResultDataCB) cb, (UpdateOption) option);
        }
    }

    /**
     * Delete the several entities by query. {NonExclusiveControl}
     * <pre>
     * AccessResultDataCB cb = new AccessResultDataCB();
     * cb.query().setFoo...(value);
     * accessResultDataBhv.<span style="color: #FD4747">queryDelete</span>(accessResultData, cb);
     * </pre>
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.robot.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(AccessResultDataCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(AccessResultDataCB cb,
            DeleteOption<AccessResultDataCB> option) {
        assertCBNotNull(cb);
        prepareDeleteOption(option);
        return delegateQueryDelete(cb, option);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int doRangeRemove(ConditionBean cb,
            DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((AccessResultDataCB) cb);
        } else {
            return varyingQueryDelete((AccessResultDataCB) cb,
                    (DeleteOption) option);
        }
    }

    // ===================================================================================
    //                                                                      Varying Update
    //                                                                      ==============
    // -----------------------------------------------------
    //                                         Entity Update
    //                                         -------------
    /**
     * Insert the entity with varying requests. <br />
     * For example, disableCommonColumnAutoSetup(), disablePrimaryKeyIdentity(). <br />
     * Other specifications are same as insert(entity).
     * <pre>
     * AccessResultData accessResultData = new AccessResultData();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * accessResultData.setFoo...(value);
     * accessResultData.setBar...(value);
     * InsertOption<AccessResultDataCB> option = new InsertOption<AccessResultDataCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * accessResultDataBhv.<span style="color: #FD4747">varyingInsert</span>(accessResultData, option);
     * ... = accessResultData.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param accessResultData The entity of insert target. (NotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void varyingInsert(AccessResultData accessResultData,
            InsertOption<AccessResultDataCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(accessResultData, option);
    }

    /**
     * Update the entity with varying requests modified-only. {UpdateCountZeroException, ExclusiveControl} <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * AccessResultData accessResultData = new AccessResultData();
     * accessResultData.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * accessResultData.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * accessResultData.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;AccessResultDataCB&gt; option = new UpdateOption&lt;AccessResultDataCB&gt;();
     *     option.self(new SpecifyQuery&lt;AccessResultDataCB&gt;() {
     *         public void specify(AccessResultDataCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     accessResultDataBhv.<span style="color: #FD4747">varyingUpdate</span>(accessResultData, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param accessResultData The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void varyingUpdate(AccessResultData accessResultData,
            UpdateOption<AccessResultDataCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(accessResultData, option);
    }

    /**
     * Insert or update the entity with varying requests. {ExclusiveControl(when update)}<br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param accessResultData The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void varyingInsertOrUpdate(AccessResultData accessResultData,
            InsertOption<AccessResultDataCB> insertOption,
            UpdateOption<AccessResultDataCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(accessResultData, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. {UpdateCountZeroException, ExclusiveControl} <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param accessResultData The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @param option The option of update for varying requests. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(AccessResultData accessResultData,
            DeleteOption<AccessResultDataCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(accessResultData, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param accessResultDataList The list of the entity. (NotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of inserted count.
     */
    public int[] varyingBatchInsert(
            List<AccessResultData> accessResultDataList,
            InsertOption<AccessResultDataCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(accessResultDataList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param accessResultDataList The list of the entity. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count.
     */
    public int[] varyingBatchUpdate(
            List<AccessResultData> accessResultDataList,
            UpdateOption<AccessResultDataCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(accessResultDataList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param accessResultDataList The list of the entity. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count.
     */
    public int[] varyingBatchDelete(
            List<AccessResultData> accessResultDataList,
            DeleteOption<AccessResultDataCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(accessResultDataList, option);
    }

    // -----------------------------------------------------
    //                                          Query Update
    //                                          ------------
    /**
     * Insert the several entities by query with varying requests (modified-only for fixed value). <br />
     * For example, disableCommonColumnAutoSetup(), disablePrimaryKeyIdentity(). <br />
     * Other specifications are same as queryInsert(entity, setupper). 
     * @param setupper The setup-per of query-insert. (NotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The inserted count.
     */
    public int varyingQueryInsert(
            QueryInsertSetupper<AccessResultData, AccessResultDataCB> setupper,
            InsertOption<AccessResultDataCB> option) {
        assertInsertOptionNotNull(option);
        return doQueryInsert(setupper, option);
    }

    /**
     * Update the several entities by query with varying requests non-strictly modified-only. {NonExclusiveControl} <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), allowNonQueryUpdate(). <br />
     * Other specifications are same as queryUpdate(entity, cb). 
     * <pre>
     * <span style="color: #3F7E5E">// ex) you can update by self calculation values</span>
     * AccessResultData accessResultData = new AccessResultData();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//accessResultData.setPK...(value);</span>
     * accessResultData.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//accessResultData.setVersionNo(value);</span>
     * AccessResultDataCB cb = new AccessResultDataCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;AccessResultDataCB&gt; option = new UpdateOption&lt;AccessResultDataCB&gt;();
     * option.self(new SpecifyQuery&lt;AccessResultDataCB&gt;() {
     *     public void specify(AccessResultDataCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * accessResultDataBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(accessResultData, cb, option);
     * </pre>
     * @param accessResultData The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.robot.dbflute.exception.NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(AccessResultData accessResultData,
            AccessResultDataCB cb, UpdateOption<AccessResultDataCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(accessResultData, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.robot.dbflute.exception.NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(AccessResultDataCB cb,
            DeleteOption<AccessResultDataCB> option) {
        assertDeleteOptionNotNull(option);
        return doQueryDelete(cb, option);
    }

    // ===================================================================================
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(AccessResultDataCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(AccessResultDataCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends AccessResultData> void delegateSelectCursor(
            AccessResultDataCB cb, EntityRowHandler<ENTITY> erh,
            Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends AccessResultData> List<ENTITY> delegateSelectList(
            AccessResultDataCB cb, Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(AccessResultData e,
            InsertOption<AccessResultDataCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(AccessResultData e,
            UpdateOption<AccessResultDataCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return delegateUpdateNonstrict(e, op);
    }

    protected int delegateUpdateNonstrict(AccessResultData e,
            UpdateOption<AccessResultDataCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(AccessResultData e,
            DeleteOption<AccessResultDataCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return delegateDeleteNonstrict(e, op);
    }

    protected int delegateDeleteNonstrict(AccessResultData e,
            DeleteOption<AccessResultDataCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(List<AccessResultData> ls,
            InsertOption<AccessResultDataCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(processBatchInternally(ls, op),
                op));
    }

    protected int[] delegateBatchUpdate(List<AccessResultData> ls,
            UpdateOption<AccessResultDataCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchUpdateNonstrict(ls, op);
    }

    protected int[] delegateBatchUpdateNonstrict(List<AccessResultData> ls,
            UpdateOption<AccessResultDataCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int[] delegateBatchDelete(List<AccessResultData> ls,
            DeleteOption<AccessResultDataCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchDeleteNonstrict(ls, op);
    }

    protected int[] delegateBatchDeleteNonstrict(List<AccessResultData> ls,
            DeleteOption<AccessResultDataCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
                processBatchInternally(ls, op, true), op));
    }

    protected int delegateQueryInsert(AccessResultData e,
            AccessResultDataCB inCB, ConditionBean resCB,
            InsertOption<AccessResultDataCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(AccessResultData e,
            AccessResultDataCB cb, UpdateOption<AccessResultDataCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(AccessResultDataCB cb,
            DeleteOption<AccessResultDataCB> op) {
        if (!processBeforeQueryDelete(cb, op)) {
            return 0;
        }
        return invoke(createQueryDeleteCBCommand(cb, op));
    }

    // ===================================================================================
    //                                                                Optimistic Lock Info
    //                                                                ====================
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasVersionNoValue(Entity entity) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasUpdateDateValue(Entity entity) {
        return false;
    }

    // ===================================================================================
    //                                                                     Downcast Helper
    //                                                                     ===============
    protected AccessResultData downcast(Entity entity) {
        return helpEntityDowncastInternally(entity, AccessResultData.class);
    }

    protected AccessResultDataCB downcast(ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb, AccessResultDataCB.class);
    }
}
