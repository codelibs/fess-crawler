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

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.EntityRowHandler;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.PagingBean;
import org.seasar.dbflute.cbean.PagingHandler;
import org.seasar.dbflute.cbean.PagingInvoker;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.cbean.ResultBeanBuilder;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.jdbc.StatementConfig;
import org.seasar.robot.db.allcommon.DBCurrent;
import org.seasar.robot.db.allcommon.DBFluteConfig;
import org.seasar.robot.db.bsentity.dbmeta.AccessResultDataDbm;
import org.seasar.robot.db.cbean.AccessResultDataCB;
import org.seasar.robot.db.exentity.AccessResult;
import org.seasar.robot.db.exentity.AccessResultData;

/**
 * The behavior of ACCESS_RESULT_DATA that the type is TABLE. <br />
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
public abstract class BsAccessResultDataBhv extends
        org.seasar.dbflute.bhv.AbstractBehaviorWritable {

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
    public Entity newEntity() {
        return newMyEntity();
    }

    public ConditionBean newConditionBean() {
        return newMyConditionBean();
    }

    public AccessResultData newMyEntity() {
        return new AccessResultData();
    }

    public AccessResultDataCB newMyConditionBean() {
        return new AccessResultDataCB();
    }

    // ===================================================================================
    //                                                                       Current DBDef
    //                                                                       =============
    @Override
    protected DBDef getCurrentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
    }

    // ===================================================================================
    //                                                             Default StatementConfig
    //                                                             =======================
    @Override
    protected StatementConfig getDefaultStatementConfig() {
        return DBFluteConfig.getInstance().getDefaultStatementConfig();
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
     * @return The selected entity. (Nullalble)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
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

    /**
     * Select the entity by the condition-bean with deleted check.
     * @param cb The condition-bean of AccessResultData. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
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

    /* (non-javadoc)
     * Select the entity with deleted check. {by primary-key value}
     * @param primaryKey The keys of primary.
     * @return The selected entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public AccessResultData selectByPKValueWithDeletedCheck(Long id) {
        AccessResultData entity = new AccessResultData();
        entity.setId(id);
        final AccessResultDataCB cb = newMyConditionBean();
        cb.acceptPrimaryKeyMapString(getDBMeta().extractPrimaryKeyMapString(
                entity));
        return selectEntityWithDeletedCheck(cb);
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
    //                                                                       Load Referrer
    //                                                                       =============
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
                    public AccessResult callbackGetForeignEntity(
                            AccessResultData entity) {
                        return entity.getAccessResult();
                    }
                });
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity.
     * @param accessResultData The entity of insert target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(AccessResultData accessResultData) {
        assertEntityNotNull(accessResultData);
        delegateInsert(accessResultData);
    }

    @Override
    protected void doCreate(Entity accessResultData) {
        insert((AccessResultData) accessResultData);
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param accessResultData The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
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
        update((AccessResultData) entity);
    }

    @Override
    protected void doModifyNonstrict(Entity entity) {
        update((AccessResultData) entity);
    }

    /**
     * Insert or update the entity modified-only. {ConcurrencyControl(when update)}
     * @param accessResultData The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
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
    protected void doCreateOrUpdate(Entity accessResultData) {
        insertOrUpdate((AccessResultData) accessResultData);
    }

    @Override
    protected void doCreateOrUpdateNonstrict(Entity entity) {
        insertOrUpdate((AccessResultData) entity);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ConcurrencyControl}
     * @param accessResultData The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
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
    protected void doRemove(Entity accessResultData) {
        delete((AccessResultData) accessResultData);
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
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
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
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
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
     * @param accessResultData Entity. (NotNull) {PrimaryKeyNotRequired}
     * @param cb Condition-bean. (NotNull)
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
     * @param cb Condition-bean. (NotNull)
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

    protected int doCallReadCount(ConditionBean cb) {
        return delegateSelectCount((AccessResultDataCB) cb);
    }

    protected List<AccessResultData> delegateSelectList(AccessResultDataCB cb) {
        return invoke(createSelectListCBCommand(cb, AccessResultData.class));
    }

    @SuppressWarnings("unchecked")
    protected List<Entity> doCallReadList(ConditionBean cb) {
        return (List) delegateSelectList((AccessResultDataCB) cb);
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

    protected int doCallCreate(Entity entity) {
        return delegateInsert(downcast(entity));
    }

    protected int delegateUpdate(AccessResultData e) {
        if (!processBeforeUpdate(e)) {
            return 1;
        }
        return invoke(createUpdateEntityCommand(e));
    }

    protected int doCallModify(Entity entity) {
        return delegateUpdate(downcast(entity));
    }

    protected int delegateDelete(AccessResultData e) {
        if (!processBeforeDelete(e)) {
            return 1;
        }
        return invoke(createDeleteEntityCommand(e));
    }

    protected int doCallRemove(Entity entity) {
        return delegateDelete(downcast(entity));
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
        return invoke(createBatchUpdateEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }

    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) {
        return delegateUpdateList((List) ls);
    }

    protected int[] delegateDeleteList(List<AccessResultData> ls) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteEntityCommand(helpFilterBeforeDeleteInternally(ls)));
    }

    @SuppressWarnings("unchecked")
    protected int[] doRemoveList(List<Entity> ls) {
        return delegateDeleteList((List) ls);
    }

    // ===================================================================================
    //                                                                Optimistic Lock Info
    //                                                                ====================
    @Override
    protected boolean hasVersionNoValue(Entity entity) {
        return false;
    }

    @Override
    protected boolean hasUpdateDateValue(Entity entity) {
        return false;
    }

    // ===================================================================================
    //                                                                              Helper
    //                                                                              ======
    protected AccessResultData downcast(Entity entity) {
        return helpDowncastInternally(entity, AccessResultData.class);
    }
}
