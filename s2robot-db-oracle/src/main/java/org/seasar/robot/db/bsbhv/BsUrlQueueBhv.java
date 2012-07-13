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

import org.seasar.robot.db.bsentity.dbmeta.UrlQueueDbm;
import org.seasar.robot.db.cbean.UrlQueueCB;
import org.seasar.robot.db.exentity.UrlQueue;
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
 * The behavior of URL_QUEUE as TABLE. <br />
 * 
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, SESSION_ID, METHOD, URL, PARENT_URL, DEPTH, LAST_MODIFIED, CREATE_TIME
 * 
 * [sequence]
 *     URL_QUEUE_SEQ
 * 
 * [identity]
 *     
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
 * 
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsUrlQueueBhv extends AbstractBehaviorWritable {

    // ===================================================================================
    // Definition
    // ==========
    /* df:BehaviorQueryPathBegin */
    public static final String PATH_deleteBySessionId = "deleteBySessionId";

    public static final String PATH_deleteAll = "deleteAll";

    /* df:BehaviorQueryPathEnd */

    // ===================================================================================
    // Table name
    // ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() {
        return "URL_QUEUE";
    }

    // ===================================================================================
    // DBMeta
    // ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() {
        return UrlQueueDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public UrlQueueDbm getMyDBMeta() {
        return UrlQueueDbm.getInstance();
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
    public UrlQueue newMyEntity() {
        return new UrlQueue();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public UrlQueueCB newMyConditionBean() {
        return new UrlQueueCB();
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
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * int count = urlQueueBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * 
     * @param cb
     *            The condition-bean of UrlQueue. (NotNull)
     * @return The selected count.
     */
    public int selectCount(final UrlQueueCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final UrlQueueCB cb) { // called by
                                                         // selectCount(cb)
        assertCBNotNull(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final UrlQueueCB cb) { // called by
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
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * urlQueueBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;UrlQueue&gt;() {
     *     public void handle(UrlQueue entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * 
     * @param cb
     *            The condition-bean of UrlQueue. (NotNull)
     * @param entityRowHandler
     *            The handler of entity row of UrlQueue. (NotNull)
     */
    public void selectCursor(final UrlQueueCB cb,
            final EntityRowHandler<UrlQueue> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, UrlQueue.class);
    }

    protected <ENTITY extends UrlQueue> void doSelectCursor(final UrlQueueCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler, final Class<ENTITY> entityType) {
        assertCBNotNull(cb);
        assertObjectNotNull("entityRowHandler<UrlQueue>", entityRowHandler);
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
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * UrlQueue urlQueue = urlQueueBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (urlQueue != null) {
     *     ... = urlQueue.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * 
     * @param cb
     *            The condition-bean of UrlQueue. (NotNull)
     * @return The selected entity. (NullAllowed: If the condition has no data,
     *         it returns null)
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.SelectEntityConditionNotFoundException
     *                When the condition for selecting an entity is not found.
     */
    public UrlQueue selectEntity(final UrlQueueCB cb) {
        return doSelectEntity(cb, UrlQueue.class);
    }

    protected <ENTITY extends UrlQueue> ENTITY doSelectEntity(
            final UrlQueueCB cb, final Class<ENTITY> entityType) {
        return helpSelectEntityInternally(
            cb,
            new InternalSelectEntityCallback<ENTITY, UrlQueueCB>() {
                public List<ENTITY> callbackSelectList(final UrlQueueCB cb) {
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
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * UrlQueue urlQueue = urlQueueBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = urlQueue.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * 
     * @param cb
     *            The condition-bean of UrlQueue. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.SelectEntityConditionNotFoundException
     *                When the condition for selecting an entity is not found.
     */
    public UrlQueue selectEntityWithDeletedCheck(final UrlQueueCB cb) {
        return doSelectEntityWithDeletedCheck(cb, UrlQueue.class);
    }

    protected <ENTITY extends UrlQueue> ENTITY doSelectEntityWithDeletedCheck(
            final UrlQueueCB cb, final Class<ENTITY> entityType) {
        return helpSelectEntityWithDeletedCheckInternally(
            cb,
            new InternalSelectEntityWithDeletedCheckCallback<ENTITY, UrlQueueCB>() {
                public List<ENTITY> callbackSelectList(final UrlQueueCB cb) {
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
    public UrlQueue selectByPKValue(final Long id) {
        return doSelectByPKValue(id, UrlQueue.class);
    }

    protected <ENTITY extends UrlQueue> ENTITY doSelectByPKValue(final Long id,
            final Class<ENTITY> entityType) {
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
    public UrlQueue selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKValueWithDeletedCheck(id, UrlQueue.class);
    }

    protected <ENTITY extends UrlQueue> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private UrlQueueCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final UrlQueueCB cb = newMyConditionBean();
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
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;UrlQueue&gt; urlQueueList = urlQueueBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (UrlQueue urlQueue : urlQueueList) {
     *     ... = urlQueue.get...();
     * }
     * </pre>
     * 
     * @param cb
     *            The condition-bean of UrlQueue. (NotNull)
     * @return The result bean of selected list. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.DangerousResultSizeException
     *                When the result size is over the specified safety size.
     */
    public ListResultBean<UrlQueue> selectList(final UrlQueueCB cb) {
        return doSelectList(cb, UrlQueue.class);
    }

    protected <ENTITY extends UrlQueue> ListResultBean<ENTITY> doSelectList(
            final UrlQueueCB cb, final Class<ENTITY> entityType) {
        assertCBNotNull(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(
            cb,
            entityType,
            new InternalSelectListCallback<ENTITY, UrlQueueCB>() {
                public List<ENTITY> callbackSelectList(final UrlQueueCB cb,
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
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;UrlQueue&gt; page = urlQueueBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (UrlQueue urlQueue : page) {
     *     ... = urlQueue.get...();
     * }
     * </pre>
     * 
     * @param cb
     *            The condition-bean of UrlQueue. (NotNull)
     * @return The result bean of selected page. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.DangerousResultSizeException
     *                When the result size is over the specified safety size.
     */
    public PagingResultBean<UrlQueue> selectPage(final UrlQueueCB cb) {
        return doSelectPage(cb, UrlQueue.class);
    }

    protected <ENTITY extends UrlQueue> PagingResultBean<ENTITY> doSelectPage(
            final UrlQueueCB cb, final Class<ENTITY> entityType) {
        assertCBNotNull(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(
            cb,
            entityType,
            new InternalSelectPageCallback<ENTITY, UrlQueueCB>() {
                public int callbackSelectCount(final UrlQueueCB cb) {
                    return doSelectCountPlainly(cb);
                }

                public List<ENTITY> callbackSelectList(final UrlQueueCB cb,
                        final Class<ENTITY> entityType) {
                    return doSelectList(cb, entityType);
                }
            });
    }

    @Override
    protected PagingResultBean<? extends Entity> doReadPage(final ConditionBean cb) {
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
     * urlQueueBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(UrlQueueCB cb) {
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
    public <RESULT> SLFunction<UrlQueueCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends UrlQueueCB> SLFunction<CB, RESULT> doScalarSelect(
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
    // Pull out Foreign
    // ================

    // ===================================================================================
    // Entity Update
    // =============
    /**
     * Insert the entity.
     * 
     * <pre>
     * UrlQueue urlQueue = new UrlQueue();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * urlQueue.setFoo...(value);
     * urlQueue.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//urlQueue.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//urlQueue.set...;</span>
     * urlQueueBhv.<span style="color: #FD4747">insert</span>(urlQueue);
     * ... = urlQueue.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * 
     * @param urlQueue
     *            The entity of insert target. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (Unique Constraint
     *                Violation)
     */
    public void insert(final UrlQueue urlQueue) {
        doInsert(urlQueue, null);
    }

    protected void doInsert(final UrlQueue urlQueue, final InsertOption<UrlQueueCB> option) {
        assertObjectNotNull("urlQueue", urlQueue);
        prepareInsertOption(option);
        delegateInsert(urlQueue, option);
    }

    protected void prepareInsertOption(final InsertOption<UrlQueueCB> option) {
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
     * UrlQueue urlQueue = new UrlQueue();
     * urlQueue.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * urlQueue.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//urlQueue.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//urlQueue.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * urlQueue.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     urlQueueBhv.<span style="color: #FD4747">update</span>(urlQueue);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * 
     * @param urlQueue
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
    public void update(final UrlQueue urlQueue) {
        doUpdate(urlQueue, null);
    }

    protected void doUpdate(final UrlQueue urlQueue,
            final UpdateOption<UrlQueueCB> option) {
        assertObjectNotNull("urlQueue", urlQueue);
        prepareUpdateOption(option);
        helpUpdateInternally(urlQueue, new InternalUpdateCallback<UrlQueue>() {
            public int callbackDelegateUpdate(final UrlQueue entity) {
                return delegateUpdate(entity, option);
            }
        });
    }

    protected void prepareUpdateOption(final UpdateOption<UrlQueueCB> option) {
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

    protected UrlQueueCB createCBForVaryingUpdate() {
        final UrlQueueCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected UrlQueueCB createCBForSpecifiedUpdate() {
        final UrlQueueCB cb = newMyConditionBean();
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
     * @param urlQueue
     *            The entity of insert or update target. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (Unique Constraint
     *                Violation)
     */
    public void insertOrUpdate(final UrlQueue urlQueue) {
        doInesrtOrUpdate(urlQueue, null, null);
    }

    protected void doInesrtOrUpdate(final UrlQueue urlQueue,
            final InsertOption<UrlQueueCB> insertOption,
            final UpdateOption<UrlQueueCB> updateOption) {
        helpInsertOrUpdateInternally(
            urlQueue,
            new InternalInsertOrUpdateCallback<UrlQueue, UrlQueueCB>() {
                public void callbackInsert(final UrlQueue entity) {
                    doInsert(entity, insertOption);
                }

                public void callbackUpdate(final UrlQueue entity) {
                    doUpdate(entity, updateOption);
                }

                public UrlQueueCB callbackNewMyConditionBean() {
                    return newMyConditionBean();
                }

                public int callbackSelectCount(final UrlQueueCB cb) {
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
     * UrlQueue urlQueue = new UrlQueue();
     * urlQueue.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * urlQueue.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     urlQueueBhv.<span style="color: #FD4747">delete</span>(urlQueue);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * 
     * @param urlQueue
     *            The entity of delete target. (NotNull) {PrimaryKeyRequired,
     *            ConcurrencyColumnRequired}
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     */
    public void delete(final UrlQueue urlQueue) {
        doDelete(urlQueue, null);
    }

    protected void doDelete(final UrlQueue urlQueue,
            final DeleteOption<UrlQueueCB> option) {
        assertObjectNotNull("urlQueue", urlQueue);
        prepareDeleteOption(option);
        helpDeleteInternally(urlQueue, new InternalDeleteCallback<UrlQueue>() {
            public int callbackDelegateDelete(final UrlQueue entity) {
                return delegateDelete(entity, option);
            }
        });
    }

    protected void prepareDeleteOption(final DeleteOption<UrlQueueCB> option) {
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
     * @param urlQueueList
     *            The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(final List<UrlQueue> urlQueueList) {
        return doBatchInsert(urlQueueList, null);
    }

    protected int[] doBatchInsert(final List<UrlQueue> urlQueueList,
            final InsertOption<UrlQueueCB> option) {
        assertObjectNotNull("urlQueueList", urlQueueList);
        prepareInsertOption(option);
        return delegateBatchInsert(urlQueueList, option);
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
     * @param urlQueueList
     *            The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     */
    public int[] batchUpdate(final List<UrlQueue> urlQueueList) {
        return doBatchUpdate(urlQueueList, null);
    }

    protected int[] doBatchUpdate(final List<UrlQueue> urlQueueList,
            final UpdateOption<UrlQueueCB> option) {
        assertObjectNotNull("urlQueueList", urlQueueList);
        prepareUpdateOption(option);
        return delegateBatchUpdate(urlQueueList, option);
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
     * @param urlQueueList
     *            The list of the entity. (NotNull)
     * @param updateColumnSpec
     *            The specification of update columns. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     */
    public int[] batchUpdate(final List<UrlQueue> urlQueueList,
            final SpecifyQuery<UrlQueueCB> updateColumnSpec) {
        return doBatchUpdate(
            urlQueueList,
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
     * @param urlQueueList
     *            The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     */
    public int[] batchDelete(final List<UrlQueue> urlQueueList) {
        return doBatchDelete(urlQueueList, null);
    }

    protected int[] doBatchDelete(final List<UrlQueue> urlQueueList,
            final DeleteOption<UrlQueueCB> option) {
        assertObjectNotNull("urlQueueList", urlQueueList);
        prepareDeleteOption(option);
        return delegateBatchDelete(urlQueueList, option);
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
     * urlQueueBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;urlQueue, UrlQueueCB&gt;() {
     *     public ConditionBean setup(urlQueue entity, UrlQueueCB intoCB) {
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
    public int queryInsert(final QueryInsertSetupper<UrlQueue, UrlQueueCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<UrlQueue, UrlQueueCB> setupper,
            final InsertOption<UrlQueueCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final UrlQueue entity = new UrlQueue();
        final UrlQueueCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected UrlQueueCB createCBForQueryInsert() {
        final UrlQueueCB cb = newMyConditionBean();
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
     * UrlQueue urlQueue = new UrlQueue();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//urlQueue.setPK...(value);</span>
     * urlQueue.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//urlQueue.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//urlQueue.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//urlQueue.setVersionNo(value);</span>
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * urlQueueBhv.<span style="color: #FD4747">queryUpdate</span>(urlQueue, cb);
     * </pre>
     * 
     * @param urlQueue
     *            The entity that contains update values. (NotNull,
     *            PrimaryKeyNullAllowed)
     * @param cb
     *            The condition-bean of UrlQueue. (NotNull)
     * @return The updated count.
     * @exception org.seasar.robot.dbflute.exception.NonQueryUpdateNotAllowedException
     *                When the query has no condition.
     */
    public int queryUpdate(final UrlQueue urlQueue, final UrlQueueCB cb) {
        return doQueryUpdate(urlQueue, cb, null);
    }

    protected int doQueryUpdate(final UrlQueue urlQueue, final UrlQueueCB cb,
            final UpdateOption<UrlQueueCB> option) {
        assertObjectNotNull("urlQueue", urlQueue);
        assertCBNotNull(cb);
        prepareUpdateOption(option);
        return delegateQueryUpdate(urlQueue, cb, option);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (UrlQueueCB) cb);
        } else {
            return varyingQueryUpdate(
                downcast(entity),
                (UrlQueueCB) cb,
                (UpdateOption) option);
        }
    }

    /**
     * Delete the several entities by query. {NonExclusiveControl}
     * 
     * <pre>
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * urlQueueBhv.<span style="color: #FD4747">queryDelete</span>(urlQueue, cb);
     * </pre>
     * 
     * @param cb
     *            The condition-bean of UrlQueue. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.robot.dbflute.exception.NonQueryDeleteNotAllowedException
     *                When the query has no condition.
     */
    public int queryDelete(final UrlQueueCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final UrlQueueCB cb, final DeleteOption<UrlQueueCB> option) {
        assertCBNotNull(cb);
        prepareDeleteOption(option);
        return delegateQueryDelete(cb, option);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((UrlQueueCB) cb);
        } else {
            return varyingQueryDelete((UrlQueueCB) cb, (DeleteOption) option);
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
     * UrlQueue urlQueue = new UrlQueue();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * urlQueue.setFoo...(value);
     * urlQueue.setBar...(value);
     * InsertOption<UrlQueueCB> option = new InsertOption<UrlQueueCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * urlQueueBhv.<span style="color: #FD4747">varyingInsert</span>(urlQueue, option);
     * ... = urlQueue.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * 
     * @param urlQueue
     *            The entity of insert target. (NotNull)
     * @param option
     *            The option of insert for varying requests. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (Unique Constraint
     *                Violation)
     */
    public void varyingInsert(final UrlQueue urlQueue, final InsertOption<UrlQueueCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(urlQueue, option);
    }

    /**
     * Update the entity with varying requests modified-only.
     * {UpdateCountZeroException, ExclusiveControl} <br />
     * For example, self(selfCalculationSpecification),
     * specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * 
     * <pre>
     * UrlQueue urlQueue = new UrlQueue();
     * urlQueue.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * urlQueue.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * urlQueue.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;UrlQueueCB&gt; option = new UpdateOption&lt;UrlQueueCB&gt;();
     *     option.self(new SpecifyQuery&lt;UrlQueueCB&gt;() {
     *         public void specify(UrlQueueCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     urlQueueBhv.<span style="color: #FD4747">varyingUpdate</span>(urlQueue, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * 
     * @param urlQueue
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
    public void varyingUpdate(final UrlQueue urlQueue, final UpdateOption<UrlQueueCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(urlQueue, option);
    }

    /**
     * Insert or update the entity with varying requests. {ExclusiveControl(when
     * update)}<br />
     * Other specifications are same as insertOrUpdate(entity).
     * 
     * @param urlQueue
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
    public void varyingInsertOrUpdate(final UrlQueue urlQueue,
            final InsertOption<UrlQueueCB> insertOption,
            final UpdateOption<UrlQueueCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(urlQueue, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. {UpdateCountZeroException,
     * ExclusiveControl} <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * 
     * @param urlQueue
     *            The entity of delete target. (NotNull) {PrimaryKeyRequired,
     *            ConcurrencyColumnRequired}
     * @param option
     *            The option of update for varying requests. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     */
    public void varyingDelete(final UrlQueue urlQueue, final DeleteOption<UrlQueueCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(urlQueue, option);
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
     * @param urlQueueList
     *            The list of the entity. (NotNull)
     * @param option
     *            The option of insert for varying requests. (NotNull)
     * @return The array of inserted count.
     */
    public int[] varyingBatchInsert(final List<UrlQueue> urlQueueList,
            final InsertOption<UrlQueueCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(urlQueueList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification),
     * specify(updateColumnSpecification) , disableCommonColumnAutoSetup(),
     * limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * 
     * @param urlQueueList
     *            The list of the entity. (NotNull)
     * @param option
     *            The option of update for varying requests. (NotNull)
     * @return The array of updated count.
     */
    public int[] varyingBatchUpdate(final List<UrlQueue> urlQueueList,
            final UpdateOption<UrlQueueCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(urlQueueList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * 
     * @param urlQueueList
     *            The list of the entity. (NotNull)
     * @param option
     *            The option of delete for varying requests. (NotNull)
     * @return The array of deleted count.
     */
    public int[] varyingBatchDelete(final List<UrlQueue> urlQueueList,
            final DeleteOption<UrlQueueCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(urlQueueList, option);
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
            final QueryInsertSetupper<UrlQueue, UrlQueueCB> setupper,
            final InsertOption<UrlQueueCB> option) {
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
     * UrlQueue urlQueue = new UrlQueue();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//urlQueue.setPK...(value);</span>
     * urlQueue.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//urlQueue.setVersionNo(value);</span>
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;UrlQueueCB&gt; option = new UpdateOption&lt;UrlQueueCB&gt;();
     * option.self(new SpecifyQuery&lt;UrlQueueCB&gt;() {
     *     public void specify(UrlQueueCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * urlQueueBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(urlQueue, cb, option);
     * </pre>
     * 
     * @param urlQueue
     *            The entity that contains update values. (NotNull)
     *            {PrimaryKeyNotRequired}
     * @param cb
     *            The condition-bean of UrlQueue. (NotNull)
     * @param option
     *            The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.robot.dbflute.exception.NonQueryUpdateNotAllowedException
     *                When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final UrlQueue urlQueue, final UrlQueueCB cb,
            final UpdateOption<UrlQueueCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(urlQueue, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * 
     * @param cb
     *            The condition-bean of UrlQueue. (NotNull)
     * @param option
     *            The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.robot.dbflute.exception.NonQueryDeleteNotAllowedException
     *                When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final UrlQueueCB cb, final DeleteOption<UrlQueueCB> option) {
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
    protected int delegateSelectCountUniquely(final UrlQueueCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(final UrlQueueCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends UrlQueue> void delegateSelectCursor(
            final UrlQueueCB cb, final EntityRowHandler<ENTITY> erh, final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends UrlQueue> List<ENTITY> delegateSelectList(
            final UrlQueueCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    protected <RESULT> RESULT delegateSelectNextVal(final Class<RESULT> rt) {
        return invoke(createSelectNextValCommand(rt));
    }

    // -----------------------------------------------------
    // Update
    // ------
    protected int delegateInsert(final UrlQueue e, final InsertOption<UrlQueueCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final UrlQueue e, final UpdateOption<UrlQueueCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return delegateUpdateNonstrict(e, op);
    }

    protected int delegateUpdateNonstrict(final UrlQueue e,
            final UpdateOption<UrlQueueCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final UrlQueue e, final DeleteOption<UrlQueueCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return delegateDeleteNonstrict(e, op);
    }

    protected int delegateDeleteNonstrict(final UrlQueue e,
            final DeleteOption<UrlQueueCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(final List<UrlQueue> ls,
            final InsertOption<UrlQueueCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(
            processBatchInternally(ls, op),
            op));
    }

    protected int[] delegateBatchUpdate(final List<UrlQueue> ls,
            final UpdateOption<UrlQueueCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchUpdateNonstrict(ls, op);
    }

    protected int[] delegateBatchUpdateNonstrict(final List<UrlQueue> ls,
            final UpdateOption<UrlQueueCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
            processBatchInternally(ls, op, true),
            op));
    }

    protected int[] delegateBatchDelete(final List<UrlQueue> ls,
            final DeleteOption<UrlQueueCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchDeleteNonstrict(ls, op);
    }

    protected int[] delegateBatchDeleteNonstrict(final List<UrlQueue> ls,
            final DeleteOption<UrlQueueCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
            processBatchInternally(ls, op, true),
            op));
    }

    protected int delegateQueryInsert(final UrlQueue e, final UrlQueueCB inCB,
            final ConditionBean resCB, final InsertOption<UrlQueueCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final UrlQueue e, final UrlQueueCB cb,
            final UpdateOption<UrlQueueCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final UrlQueueCB cb, final DeleteOption<UrlQueueCB> op) {
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
    protected UrlQueue downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity, UrlQueue.class);
    }

    protected UrlQueueCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb, UrlQueueCB.class);
    }
}
