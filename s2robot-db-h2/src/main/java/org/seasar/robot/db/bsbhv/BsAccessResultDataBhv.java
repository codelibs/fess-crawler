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
package org.seasar.robot.db.bsbhv;

import java.util.List;

import org.seasar.robot.db.bsentity.dbmeta.AccessResultDataDbm;
import org.seasar.robot.db.cbean.AccessResultDataCB;
import org.seasar.robot.db.exentity.AccessResult;
import org.seasar.robot.db.exentity.AccessResultData;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.bhv.AbstractBehaviorWritable;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.EntityRowHandler;
import org.seasar.robot.dbflute.cbean.ListResultBean;
import org.seasar.robot.dbflute.cbean.PagingBean;
import org.seasar.robot.dbflute.cbean.PagingHandler;
import org.seasar.robot.dbflute.cbean.PagingInvoker;
import org.seasar.robot.dbflute.cbean.PagingResultBean;
import org.seasar.robot.dbflute.cbean.ResultBeanBuilder;
import org.seasar.robot.dbflute.dbmeta.DBMeta;

/**
 * The behavior of ACCESS_RESULT_DATA that is TABLE. <br />
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
    public static final String PATH_deleteAll = "deleteAll";

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
     * Select the count by the condition-bean. {IgnorePagingCondition}
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @return The selected count.
     */
    public int selectCount(AccessResultDataCB cb) {
        assertCBNotNull(cb);
        return delegateSelectCount(cb);
    }

    @Override
    protected int doReadCount(ConditionBean cb) {
        return selectCount(downcast(cb));
    }

    // ===================================================================================
    //                                                                       Cursor Select
    //                                                                       =============
    /**
     * Select the cursor by the condition-bean. <br />
     * Attention: It has a mapping cost from result set to entity.
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @param entityRowHandler The handler of entity row of AccessResultData. (NotNull)
     */
    public void selectCursor(AccessResultDataCB cb,
            EntityRowHandler<AccessResultData> entityRowHandler) {
        assertCBNotNull(cb);
        assertObjectNotNull("entityRowHandler<AccessResultData>",
                entityRowHandler);
        delegateSelectCursor(cb, entityRowHandler);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @return The selected entity. (Nullable: If the condition has no data, it returns null)
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public AccessResultData selectEntity(final AccessResultDataCB cb) {
        return helpSelectEntityInternally(
                cb,
                new InternalSelectEntityCallback<AccessResultData, AccessResultDataCB>() {
                    public List<AccessResultData> callbackSelectList(
                            AccessResultDataCB cb) {
                        return selectList(cb);
                    }
                });
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return selectEntity(downcast(cb));
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public AccessResultData selectEntityWithDeletedCheck(
            final AccessResultDataCB cb) {
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                new InternalSelectEntityWithDeletedCheckCallback<AccessResultData, AccessResultDataCB>() {
                    public List<AccessResultData> callbackSelectList(
                            AccessResultDataCB cb) {
                        return selectList(cb);
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
     * @return The selected entity. (Nullable: If the primary-key value has no data, it returns null)
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public AccessResultData selectByPKValue(Long id) {
        return selectEntity(buildPKCB(id));
    }

    /**
     * Select the entity by the primary-key value with deleted check.
     * @param id The one of primary key. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public AccessResultData selectByPKValueWithDeletedCheck(Long id) {
        return selectEntityWithDeletedCheck(buildPKCB(id));
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
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @return The result bean of selected list. (NotNull)
     */
    public ListResultBean<AccessResultData> selectList(AccessResultDataCB cb) {
        assertCBNotNull(cb);
        return new ResultBeanBuilder<AccessResultData>(getTableDbName())
                .buildListResultBean(cb, delegateSelectList(cb));
    }

    @Override
    protected ListResultBean<? extends Entity> doReadList(ConditionBean cb) {
        return selectList(downcast(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean.
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @return The result bean of selected page. (NotNull)
     */
    public PagingResultBean<AccessResultData> selectPage(
            final AccessResultDataCB cb) {
        assertCBNotNull(cb);
        final PagingInvoker<AccessResultData> invoker = new PagingInvoker<AccessResultData>(
                getTableDbName());
        final PagingHandler<AccessResultData> handler = new PagingHandler<AccessResultData>() {
            public PagingBean getPagingBean() {
                return cb;
            }

            public int count() {
                return selectCount(cb);
            }

            public List<AccessResultData> paging() {
                return selectList(cb);
            }
        };
        return invoker.invokePaging(handler);
    }

    @Override
    protected PagingResultBean<? extends Entity> doReadPage(ConditionBean cb) {
        return selectPage(downcast(cb));
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    /**
     * Select the scalar value derived by a function. <br />
     * Call a function method after this method called like as follows:
     * <pre>
     * accessResultDataBhv.scalarSelect(Date.class).max(new ScalarQuery(AccessResultDataCB cb) {
     *     cb.specify().columnXxxDatetime(); // the required specification of target column
     *     cb.query().setXxxName_PrefixSearch("S"); // query as you like it
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (Nullable)
     */
    public <RESULT> SLFunction<AccessResultDataCB, RESULT> scalarSelect(
            Class<RESULT> resultType) {
        AccessResultDataCB cb = newMyConditionBean();
        cb.xsetupForScalarSelect();
        cb.getSqlClause().disableSelectIndex(); // for when you use union
        return new SLFunction<AccessResultDataCB, RESULT>(cb, resultType);
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
     * @param accessResultData The entity of insert target. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(AccessResultData accessResultData) {
        assertEntityNotNull(accessResultData);
        delegateInsert(accessResultData);
    }

    @Override
    protected void doCreate(Entity entity) {
        insert(downcast(entity));
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param accessResultData The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final AccessResultData accessResultData) {
        helpUpdateInternally(accessResultData,
                new InternalUpdateCallback<AccessResultData>() {
                    public int callbackDelegateUpdate(AccessResultData entity) {
                        return delegateUpdate(entity);
                    }
                });
    }

    @Override
    protected void doModify(Entity entity) {
        update(downcast(entity));
    }

    @Override
    protected void doModifyNonstrict(Entity entity) {
        update(downcast(entity));
    }

    /**
     * Insert or update the entity modified-only. {ConcurrencyControl(when update)}
     * @param accessResultData The entity of insert or update target. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(final AccessResultData accessResultData) {
        helpInsertOrUpdateInternally(
                accessResultData,
                new InternalInsertOrUpdateCallback<AccessResultData, AccessResultDataCB>() {
                    public void callbackInsert(AccessResultData entity) {
                        insert(entity);
                    }

                    public void callbackUpdate(AccessResultData entity) {
                        update(entity);
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
    protected void doCreateOrUpdate(Entity entity) {
        insertOrUpdate(downcast(entity));
    }

    @Override
    protected void doCreateOrUpdateNonstrict(Entity entity) {
        insertOrUpdate(downcast(entity));
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ConcurrencyControl}
     * @param accessResultData The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(AccessResultData accessResultData) {
        helpDeleteInternally(accessResultData,
                new InternalDeleteCallback<AccessResultData>() {
                    public int callbackDelegateDelete(AccessResultData entity) {
                        return delegateDelete(entity);
                    }
                });
    }

    @Override
    protected void doRemove(Entity entity) {
        delete(downcast(entity));
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch insert the list. This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param accessResultDataList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<AccessResultData> accessResultDataList) {
        assertObjectNotNull("accessResultDataList", accessResultDataList);
        return delegateInsertList(accessResultDataList);
    }

    /**
     * Batch update the list. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param accessResultDataList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdate(List<AccessResultData> accessResultDataList) {
        assertObjectNotNull("accessResultDataList", accessResultDataList);
        return delegateUpdateList(accessResultDataList);
    }

    /**
     * Batch delete the list. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param accessResultDataList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDelete(List<AccessResultData> accessResultDataList) {
        assertObjectNotNull("accessResultDataList", accessResultDataList);
        return delegateDeleteList(accessResultDataList);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Query update the several entities. {NoConcurrencyControl}
     * @param accessResultData The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @return The updated count.
     */
    public int queryUpdate(AccessResultData accessResultData,
            AccessResultDataCB cb) {
        assertObjectNotNull("accessResultData", accessResultData);
        assertCBNotNull(cb);
        setupCommonColumnOfUpdateIfNeeds(accessResultData);
        filterEntityOfUpdate(accessResultData);
        assertEntityOfUpdate(accessResultData);
        return invoke(createQueryUpdateEntityCBCommand(accessResultData, cb));
    }

    /**
     * Query delete the several entities. {NoConcurrencyControl}
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @return The deleted count.
     */
    public int queryDelete(AccessResultDataCB cb) {
        assertCBNotNull(cb);
        return invoke(createQueryDeleteCBCommand(cb));
    }

    // ===================================================================================
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCount(AccessResultDataCB cb) {
        return invoke(createSelectCountCBCommand(cb));
    }

    protected void delegateSelectCursor(AccessResultDataCB cb,
            EntityRowHandler<AccessResultData> entityRowHandler) {
        invoke(createSelectCursorCBCommand(cb, entityRowHandler,
                AccessResultData.class));
    }

    protected List<AccessResultData> delegateSelectList(AccessResultDataCB cb) {
        return invoke(createSelectListCBCommand(cb, AccessResultData.class));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(AccessResultData e) {
        if (!processBeforeInsert(e)) {
            return 1;
        }
        return invoke(createInsertEntityCommand(e));
    }

    protected int delegateUpdate(AccessResultData e) {
        if (!processBeforeUpdate(e)) {
            return 1;
        }
        return invoke(createUpdateNonstrictEntityCommand(e));
    }

    protected int delegateDelete(AccessResultData e) {
        if (!processBeforeDelete(e)) {
            return 1;
        }
        return invoke(createDeleteNonstrictEntityCommand(e));
    }

    protected int[] delegateInsertList(List<AccessResultData> ls) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertEntityCommand(helpFilterBeforeInsertInternally(ls)));
    }

    @SuppressWarnings("unchecked")
    protected int[] doCreateList(List<Entity> ls) {
        return delegateInsertList((List) ls);
    }

    protected int[] delegateUpdateList(List<AccessResultData> ls) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }

    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) {
        return delegateUpdateList((List) ls);
    }

    protected int[] delegateDeleteList(List<AccessResultData> ls) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictEntityCommand(helpFilterBeforeDeleteInternally(ls)));
    }

    @SuppressWarnings("unchecked")
    protected int[] doRemoveList(List<Entity> ls) {
        return delegateDeleteList((List) ls);
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
