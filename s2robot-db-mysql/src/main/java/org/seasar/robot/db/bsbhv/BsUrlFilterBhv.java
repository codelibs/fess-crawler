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

import org.seasar.robot.db.bsentity.dbmeta.UrlFilterDbm;
import org.seasar.robot.db.cbean.UrlFilterCB;
import org.seasar.robot.db.exentity.UrlFilter;
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
 * The behavior of URL_FILTER that is TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, SESSION_ID, URL, FILTER_TYPE, CREATE_TIME
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
 *     
 * 
 * [referrer-table]
 *     
 * 
 * [foreign-property]
 *     
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsUrlFilterBhv extends AbstractBehaviorWritable {

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
        return "URL_FILTER";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() {
        return UrlFilterDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public UrlFilterDbm getMyDBMeta() {
        return UrlFilterDbm.getInstance();
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
    public UrlFilter newMyEntity() {
        return new UrlFilter();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public UrlFilterCB newMyConditionBean() {
        return new UrlFilterCB();
    }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count by the condition-bean. {IgnorePagingCondition}
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @return The selected count.
     */
    public int selectCount(UrlFilterCB cb) {
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
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @param entityRowHandler The handler of entity row of UrlFilter. (NotNull)
     */
    public void selectCursor(UrlFilterCB cb,
            EntityRowHandler<UrlFilter> entityRowHandler) {
        assertCBNotNull(cb);
        assertObjectNotNull("entityRowHandler<UrlFilter>", entityRowHandler);
        delegateSelectCursor(cb, entityRowHandler);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @return The selected entity. (Nullable: If the condition has no data, it returns null)
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public UrlFilter selectEntity(final UrlFilterCB cb) {
        return helpSelectEntityInternally(cb,
                new InternalSelectEntityCallback<UrlFilter, UrlFilterCB>() {
                    public List<UrlFilter> callbackSelectList(UrlFilterCB cb) {
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
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public UrlFilter selectEntityWithDeletedCheck(final UrlFilterCB cb) {
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                new InternalSelectEntityWithDeletedCheckCallback<UrlFilter, UrlFilterCB>() {
                    public List<UrlFilter> callbackSelectList(UrlFilterCB cb) {
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
    public UrlFilter selectByPKValue(Long id) {
        return selectEntity(buildPKCB(id));
    }

    /**
     * Select the entity by the primary-key value with deleted check.
     * @param id The one of primary key. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public UrlFilter selectByPKValueWithDeletedCheck(Long id) {
        return selectEntityWithDeletedCheck(buildPKCB(id));
    }

    private UrlFilterCB buildPKCB(Long id) {
        assertObjectNotNull("id", id);
        UrlFilterCB cb = newMyConditionBean();
        cb.query().setId_Equal(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @return The result bean of selected list. (NotNull)
     */
    public ListResultBean<UrlFilter> selectList(UrlFilterCB cb) {
        assertCBNotNull(cb);
        return new ResultBeanBuilder<UrlFilter>(getTableDbName())
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
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @return The result bean of selected page. (NotNull)
     */
    public PagingResultBean<UrlFilter> selectPage(final UrlFilterCB cb) {
        assertCBNotNull(cb);
        final PagingInvoker<UrlFilter> invoker = new PagingInvoker<UrlFilter>(
                getTableDbName());
        final PagingHandler<UrlFilter> handler = new PagingHandler<UrlFilter>() {
            public PagingBean getPagingBean() {
                return cb;
            }

            public int count() {
                return selectCount(cb);
            }

            public List<UrlFilter> paging() {
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
     * urlFilterBhv.scalarSelect(Date.class).max(new ScalarQuery(UrlFilterCB cb) {
     *     cb.specify().columnXxxDatetime(); // the required specification of target column
     *     cb.query().setXxxName_PrefixSearch("S"); // query as you like it
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (Nullable)
     */
    public <RESULT> SLFunction<UrlFilterCB, RESULT> scalarSelect(
            Class<RESULT> resultType) {
        UrlFilterCB cb = newMyConditionBean();
        cb.xsetupForScalarSelect();
        cb.getSqlClause().disableSelectIndex(); // for when you use union
        return new SLFunction<UrlFilterCB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity.
     * @param urlFilter The entity of insert target. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(UrlFilter urlFilter) {
        assertEntityNotNull(urlFilter);
        delegateInsert(urlFilter);
    }

    @Override
    protected void doCreate(Entity entity) {
        insert(downcast(entity));
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param urlFilter The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final UrlFilter urlFilter) {
        helpUpdateInternally(urlFilter,
                new InternalUpdateCallback<UrlFilter>() {
                    public int callbackDelegateUpdate(UrlFilter entity) {
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
     * @param urlFilter The entity of insert or update target. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(final UrlFilter urlFilter) {
        helpInsertOrUpdateInternally(urlFilter,
                new InternalInsertOrUpdateCallback<UrlFilter, UrlFilterCB>() {
                    public void callbackInsert(UrlFilter entity) {
                        insert(entity);
                    }

                    public void callbackUpdate(UrlFilter entity) {
                        update(entity);
                    }

                    public UrlFilterCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    public int callbackSelectCount(UrlFilterCB cb) {
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
     * @param urlFilter The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(UrlFilter urlFilter) {
        helpDeleteInternally(urlFilter,
                new InternalDeleteCallback<UrlFilter>() {
                    public int callbackDelegateDelete(UrlFilter entity) {
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
     * @param urlFilterList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<UrlFilter> urlFilterList) {
        assertObjectNotNull("urlFilterList", urlFilterList);
        return delegateInsertList(urlFilterList);
    }

    /**
     * Batch update the list. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param urlFilterList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdate(List<UrlFilter> urlFilterList) {
        assertObjectNotNull("urlFilterList", urlFilterList);
        return delegateUpdateList(urlFilterList);
    }

    /**
     * Batch delete the list. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param urlFilterList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDelete(List<UrlFilter> urlFilterList) {
        assertObjectNotNull("urlFilterList", urlFilterList);
        return delegateDeleteList(urlFilterList);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Query update the several entities. {NoConcurrencyControl}
     * @param urlFilter The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @return The updated count.
     */
    public int queryUpdate(UrlFilter urlFilter, UrlFilterCB cb) {
        assertObjectNotNull("urlFilter", urlFilter);
        assertCBNotNull(cb);
        setupCommonColumnOfUpdateIfNeeds(urlFilter);
        filterEntityOfUpdate(urlFilter);
        assertEntityOfUpdate(urlFilter);
        return invoke(createQueryUpdateEntityCBCommand(urlFilter, cb));
    }

    /**
     * Query delete the several entities. {NoConcurrencyControl}
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @return The deleted count.
     */
    public int queryDelete(UrlFilterCB cb) {
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
    protected int delegateSelectCount(UrlFilterCB cb) {
        return invoke(createSelectCountCBCommand(cb));
    }

    protected void delegateSelectCursor(UrlFilterCB cb,
            EntityRowHandler<UrlFilter> entityRowHandler) {
        invoke(createSelectCursorCBCommand(cb, entityRowHandler,
                UrlFilter.class));
    }

    protected List<UrlFilter> delegateSelectList(UrlFilterCB cb) {
        return invoke(createSelectListCBCommand(cb, UrlFilter.class));
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(UrlFilter e) {
        if (!processBeforeInsert(e)) {
            return 1;
        }
        return invoke(createInsertEntityCommand(e));
    }

    protected int delegateUpdate(UrlFilter e) {
        if (!processBeforeUpdate(e)) {
            return 1;
        }
        return invoke(createUpdateNonstrictEntityCommand(e));
    }

    protected int delegateDelete(UrlFilter e) {
        if (!processBeforeDelete(e)) {
            return 1;
        }
        return invoke(createDeleteNonstrictEntityCommand(e));
    }

    protected int[] delegateInsertList(List<UrlFilter> ls) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertEntityCommand(helpFilterBeforeInsertInternally(ls)));
    }

    @SuppressWarnings("unchecked")
    protected int[] doCreateList(List<Entity> ls) {
        return delegateInsertList((List) ls);
    }

    protected int[] delegateUpdateList(List<UrlFilter> ls) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }

    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) {
        return delegateUpdateList((List) ls);
    }

    protected int[] delegateDeleteList(List<UrlFilter> ls) {
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
    protected UrlFilter downcast(Entity entity) {
        return helpEntityDowncastInternally(entity, UrlFilter.class);
    }

    protected UrlFilterCB downcast(ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb, UrlFilterCB.class);
    }
}
