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

import org.seasar.robot.db.bsentity.dbmeta.AccessResultDbm;
import org.seasar.robot.db.cbean.AccessResultCB;
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
 * The behavior of ACCESS_RESULT that is TABLE. <br />
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
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsAccessResultBhv extends AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:BehaviorQueryPathBegin*/
    public static final String PATH_selectListByUrlDiff = "selectListByUrlDiff";

    public static final String PATH_deleteBySessionId = "deleteBySessionId";

    /*df:BehaviorQueryPathEnd*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() {
        return "ACCESS_RESULT";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() {
        return AccessResultDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public AccessResultDbm getMyDBMeta() {
        return AccessResultDbm.getInstance();
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
    public AccessResult newMyEntity() {
        return new AccessResult();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public AccessResultCB newMyConditionBean() {
        return new AccessResultCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count by the condition-bean. {IgnorePagingCondition}
     * @param cb The condition-bean of AccessResult. (NotNull)
     * @return The selected count.
     */
    public int selectCount(AccessResultCB cb) {
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
     * @param cb The condition-bean of AccessResult. (NotNull)
     * @param entityRowHandler The handler of entity row of AccessResult. (NotNull)
     */
    public void selectCursor(AccessResultCB cb,
            EntityRowHandler<AccessResult> entityRowHandler) {
        assertCBNotNull(cb);
        assertObjectNotNull("entityRowHandler<AccessResult>", entityRowHandler);
        delegateSelectCursor(cb, entityRowHandler);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * @param cb The condition-bean of AccessResult. (NotNull)
     * @return The selected entity. (Nullable: If the condition has no data, it returns null)
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public AccessResult selectEntity(final AccessResultCB cb) {
        return helpSelectEntityInternally(
                cb,
                new InternalSelectEntityCallback<AccessResult, AccessResultCB>() {
                    public List<AccessResult> callbackSelectList(
                            AccessResultCB cb) {
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
     * @param cb The condition-bean of AccessResult. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public AccessResult selectEntityWithDeletedCheck(final AccessResultCB cb) {
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                new InternalSelectEntityWithDeletedCheckCallback<AccessResult, AccessResultCB>() {
                    public List<AccessResult> callbackSelectList(
                            AccessResultCB cb) {
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
    public AccessResult selectByPKValue(Long id) {
        return selectEntity(buildPKCB(id));
    }

    /**
     * Select the entity by the primary-key value with deleted check.
     * @param id The one of primary key. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public AccessResult selectByPKValueWithDeletedCheck(Long id) {
        return selectEntityWithDeletedCheck(buildPKCB(id));
    }

    private AccessResultCB buildPKCB(Long id) {
        assertObjectNotNull("id", id);
        AccessResultCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * @param cb The condition-bean of AccessResult. (NotNull)
     * @return The result bean of selected list. (NotNull)
     */
    public ListResultBean<AccessResult> selectList(AccessResultCB cb) {
        assertCBNotNull(cb);
        return new ResultBeanBuilder<AccessResult>(getTableDbName())
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
     * @param cb The condition-bean of AccessResult. (NotNull)
     * @return The result bean of selected page. (NotNull)
     */
    public PagingResultBean<AccessResult> selectPage(final AccessResultCB cb) {
        assertCBNotNull(cb);
        final PagingInvoker<AccessResult> invoker = new PagingInvoker<AccessResult>(
                getTableDbName());
        final PagingHandler<AccessResult> handler = new PagingHandler<AccessResult>() {
            public PagingBean getPagingBean() {
                return cb;
            }

            public int count() {
                return selectCount(cb);
            }

            public List<AccessResult> paging() {
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
     * accessResultBhv.scalarSelect(Date.class).max(new ScalarQuery(AccessResultCB cb) {
     *     cb.specify().columnXxxDatetime(); // the required specification of target column
     *     cb.query().setXxxName_PrefixSearch("S"); // query as you like it
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (Nullable)
     */
    public <RESULT> SLFunction<AccessResultCB, RESULT> scalarSelect(
            Class<RESULT> resultType) {
        AccessResultCB cb = newMyConditionBean();
        cb.xsetupForScalarSelect();
        cb.getSqlClause().disableSelectIndex(); // for when you use union
        return new SLFunction<AccessResultCB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    /**
     * Pull out the list of referrer-as-one table 'AccessResultData'.
     * @param accessResultList The list of accessResult. (NotNull)
     * @return The list of referrer-as-one table. (NotNull)
     */
    public List<AccessResultData> pulloutAccessResultDataAsOne(
            List<AccessResult> accessResultList) {
        return helpPulloutInternally(accessResultList,
                new InternalPulloutCallback<AccessResult, AccessResultData>() {
                    public AccessResultData getFr(AccessResult e) {
                        return e.getAccessResultDataAsOne();
                    }

                    public boolean hasRf() {
                        return true;
                    }

                    public void setRfLs(AccessResultData e,
                            List<AccessResult> ls) {
                        if (!ls.isEmpty()) {
                            e.setAccessResult(ls.get(0));
                        }
                    }
                });
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity.
     * @param accessResult The entity of insert target. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(AccessResult accessResult) {
        assertEntityNotNull(accessResult);
        delegateInsert(accessResult);
    }

    @Override
    protected void doCreate(Entity entity) {
        insert(downcast(entity));
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param accessResult The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final AccessResult accessResult) {
        helpUpdateInternally(accessResult,
                new InternalUpdateCallback<AccessResult>() {
                    public int callbackDelegateUpdate(AccessResult entity) {
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
     * @param accessResult The entity of insert or update target. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(final AccessResult accessResult) {
        helpInsertOrUpdateInternally(
                accessResult,
                new InternalInsertOrUpdateCallback<AccessResult, AccessResultCB>() {
                    public void callbackInsert(AccessResult entity) {
                        insert(entity);
                    }

                    public void callbackUpdate(AccessResult entity) {
                        update(entity);
                    }

                    public AccessResultCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    public int callbackSelectCount(AccessResultCB cb) {
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
     * @param accessResult The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(AccessResult accessResult) {
        helpDeleteInternally(accessResult,
                new InternalDeleteCallback<AccessResult>() {
                    public int callbackDelegateDelete(AccessResult entity) {
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
     * @param accessResultList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<AccessResult> accessResultList) {
        assertObjectNotNull("accessResultList", accessResultList);
        return delegateInsertList(accessResultList);
    }

    /**
     * Batch update the list. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param accessResultList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdate(List<AccessResult> accessResultList) {
        assertObjectNotNull("accessResultList", accessResultList);
        return delegateUpdateList(accessResultList);
    }

    /**
     * Batch delete the list. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param accessResultList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDelete(List<AccessResult> accessResultList) {
        assertObjectNotNull("accessResultList", accessResultList);
        return delegateDeleteList(accessResultList);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Query update the several entities. {NoConcurrencyControl}
     * @param accessResult The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of AccessResult. (NotNull)
     * @return The updated count.
     */
    public int queryUpdate(AccessResult accessResult, AccessResultCB cb) {
        assertObjectNotNull("accessResult", accessResult);
        assertCBNotNull(cb);
        setupCommonColumnOfUpdateIfNeeds(accessResult);
        filterEntityOfUpdate(accessResult);
        assertEntityOfUpdate(accessResult);
        return invoke(createQueryUpdateEntityCBCommand(accessResult, cb));
    }

    /**
     * Query delete the several entities. {NoConcurrencyControl}
     * @param cb The condition-bean of AccessResult. (NotNull)
     * @return The deleted count.
     */
    public int queryDelete(AccessResultCB cb) {
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
    protected int delegateSelectCount(AccessResultCB cb) {
        return invoke(createSelectCountCBCommand(cb));
    }

    protected void delegateSelectCursor(AccessResultCB cb,
            EntityRowHandler<AccessResult> entityRowHandler) {
        invoke(createSelectCursorCBCommand(cb, entityRowHandler,
                AccessResult.class));
    }

    protected List<AccessResult> delegateSelectList(AccessResultCB cb) {
        return invoke(createSelectListCBCommand(cb, AccessResult.class));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(AccessResult e) {
        if (!processBeforeInsert(e)) {
            return 1;
        }
        return invoke(createInsertEntityCommand(e));
    }

    protected int delegateUpdate(AccessResult e) {
        if (!processBeforeUpdate(e)) {
            return 1;
        }
        return invoke(createUpdateNonstrictEntityCommand(e));
    }

    protected int delegateDelete(AccessResult e) {
        if (!processBeforeDelete(e)) {
            return 1;
        }
        return invoke(createDeleteNonstrictEntityCommand(e));
    }

    protected int[] delegateInsertList(List<AccessResult> ls) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertEntityCommand(helpFilterBeforeInsertInternally(ls)));
    }

    @SuppressWarnings("unchecked")
    protected int[] doCreateList(List<Entity> ls) {
        return delegateInsertList((List) ls);
    }

    protected int[] delegateUpdateList(List<AccessResult> ls) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }

    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) {
        return delegateUpdateList((List) ls);
    }

    protected int[] delegateDeleteList(List<AccessResult> ls) {
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
    protected AccessResult downcast(Entity entity) {
        return helpEntityDowncastInternally(entity, AccessResult.class);
    }

    protected AccessResultCB downcast(ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb, AccessResultCB.class);
    }
}
