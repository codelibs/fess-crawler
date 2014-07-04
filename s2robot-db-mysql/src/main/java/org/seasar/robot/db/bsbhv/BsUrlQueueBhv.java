/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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

import org.seasar.dbflute.*;
import org.seasar.dbflute.bhv.*;
import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.exception.*;
import org.seasar.dbflute.optional.OptionalEntity;
import org.seasar.dbflute.outsidesql.executor.*;
import org.seasar.robot.db.exbhv.*;
import org.seasar.robot.db.exentity.*;
import org.seasar.robot.db.bsentity.dbmeta.*;
import org.seasar.robot.db.cbean.*;

/**
 * The behavior of URL_QUEUE as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, SESSION_ID, METHOD, URL, META_DATA, ENCODING, PARENT_URL, DEPTH, LAST_MODIFIED, CREATE_TIME
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
 * [foreign table]
 *     
 *
 * [referrer table]
 *     
 *
 * [foreign property]
 *     
 *
 * [referrer property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsUrlQueueBhv extends AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:beginQueryPath*/
    public static final String PATH_deleteAll = "deleteAll";
    public static final String PATH_deleteBySessionId = "deleteBySessionId";
    /*df:endQueryPath*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() { return "URL_QUEUE"; }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() { return UrlQueueDbm.getInstance(); }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public UrlQueueDbm getMyDBMeta() { return UrlQueueDbm.getInstance(); }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    /** {@inheritDoc} */
    public Entity newEntity() { return newMyEntity(); }

    /** {@inheritDoc} */
    public ConditionBean newConditionBean() { return newMyConditionBean(); }

    /** @return The instance of new entity as my table type. (NotNull) */
    public UrlQueue newMyEntity() { return new UrlQueue(); }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public UrlQueueCB newMyConditionBean() { return new UrlQueueCB(); }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * int count = urlQueueBhv.<span style="color: #DD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(UrlQueueCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(UrlQueueCB cb) { // called by selectCount(cb)
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(UrlQueueCB cb) { // called by selectPage(cb)
        assertCBStateValid(cb);
        return delegateSelectCountPlainly(cb);
    }

    @Override
    protected int doReadCount(ConditionBean cb) {
        return selectCount(downcast(cb));
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean. #beforejava8 <br />
     * <span style="color: #AD4747; font-size: 120%">The return might be null if no data, so you should have null check.</span> <br />
     * <span style="color: #AD4747; font-size: 120%">If the data always exists as your business rule, use selectEntityWithDeletedCheck().</span>
     * <pre>
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * UrlQueue urlQueue = urlQueueBhv.<span style="color: #DD4747">selectEntity</span>(cb);
     * if (urlQueue != null) { <span style="color: #3F7E5E">// null check</span>
     *     ... = urlQueue.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public UrlQueue selectEntity(UrlQueueCB cb) {
        return doSelectEntity(cb, UrlQueue.class);
    }

    protected <ENTITY extends UrlQueue> ENTITY doSelectEntity(UrlQueueCB cb, Class<ENTITY> tp) {
        assertCBStateValid(cb); assertObjectNotNull("entityType", tp);
        return helpSelectEntityInternally(cb, tp, new InternalSelectEntityCallback<ENTITY, UrlQueueCB>() {
            public List<ENTITY> callbackSelectList(UrlQueueCB lcb, Class<ENTITY> ltp) { return doSelectList(lcb, ltp); } });
    }

    protected <ENTITY extends UrlQueue> OptionalEntity<ENTITY> doSelectOptionalEntity(UrlQueueCB cb, Class<ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return selectEntity(downcast(cb));
    }

    /**
     * Select the entity by the condition-bean with deleted check. <br />
     * <span style="color: #AD4747; font-size: 120%">If the data always exists as your business rule, this method is good.</span>
     * <pre>
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * UrlQueue urlQueue = urlQueueBhv.<span style="color: #DD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = urlQueue.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public UrlQueue selectEntityWithDeletedCheck(UrlQueueCB cb) {
        return doSelectEntityWithDeletedCheck(cb, UrlQueue.class);
    }

    protected <ENTITY extends UrlQueue> ENTITY doSelectEntityWithDeletedCheck(UrlQueueCB cb, Class<ENTITY> tp) {
        assertCBStateValid(cb); assertObjectNotNull("entityType", tp);
        return helpSelectEntityWithDeletedCheckInternally(cb, tp, new InternalSelectEntityWithDeletedCheckCallback<ENTITY, UrlQueueCB>() {
            public List<ENTITY> callbackSelectList(UrlQueueCB lcb, Class<ENTITY> ltp) { return doSelectList(lcb, ltp); } });
    }

    @Override
    protected Entity doReadEntityWithDeletedCheck(ConditionBean cb) {
        return selectEntityWithDeletedCheck(downcast(cb));
    }

    /**
     * Select the entity by the primary-key value.
     * @param id : PK, ID, NotNull, BIGINT(19). (NotNull)
     * @return The entity selected by the PK. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public UrlQueue selectByPKValue(Long id) {
        return doSelectByPK(id, UrlQueue.class);
    }

    protected <ENTITY extends UrlQueue> ENTITY doSelectByPK(Long id, Class<ENTITY> entityType) {
        return doSelectEntity(xprepareCBAsPK(id), entityType);
    }

    protected <ENTITY extends UrlQueue> OptionalEntity<ENTITY> doSelectOptionalByPK(Long id, Class<ENTITY> entityType) {
        return createOptionalEntity(doSelectByPK(id, entityType), id);
    }

    /**
     * Select the entity by the primary-key value with deleted check.
     * @param id : PK, ID, NotNull, BIGINT(19). (NotNull)
     * @return The entity selected by the PK. (NotNull: if no data, throws exception)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public UrlQueue selectByPKValueWithDeletedCheck(Long id) {
        return doSelectByPKWithDeletedCheck(id, UrlQueue.class);
    }

    protected <ENTITY extends UrlQueue> ENTITY doSelectByPKWithDeletedCheck(Long id, Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(xprepareCBAsPK(id), entityType);
    }

    protected UrlQueueCB xprepareCBAsPK(Long id) {
        assertObjectNotNull("id", id);
        UrlQueueCB cb = newMyConditionBean(); cb.acceptPrimaryKey(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;UrlQueue&gt; urlQueueList = urlQueueBhv.<span style="color: #DD4747">selectList</span>(cb);
     * for (UrlQueue urlQueue : urlQueueList) {
     *     ... = urlQueue.get...();
     * }
     * </pre>
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<UrlQueue> selectList(UrlQueueCB cb) {
        return doSelectList(cb, UrlQueue.class);
    }

    protected <ENTITY extends UrlQueue> ListResultBean<ENTITY> doSelectList(UrlQueueCB cb, Class<ENTITY> tp) {
        assertCBStateValid(cb); assertObjectNotNull("entityType", tp);
        assertSpecifyDerivedReferrerEntityProperty(cb, tp);
        return helpSelectListInternally(cb, tp, new InternalSelectListCallback<ENTITY, UrlQueueCB>() {
            public List<ENTITY> callbackSelectList(UrlQueueCB lcb, Class<ENTITY> ltp) { return delegateSelectList(lcb, ltp); } });
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
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #DD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;UrlQueue&gt; page = urlQueueBhv.<span style="color: #DD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (UrlQueue urlQueue : page) {
     *     ... = urlQueue.get...();
     * }
     * </pre>
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<UrlQueue> selectPage(UrlQueueCB cb) {
        return doSelectPage(cb, UrlQueue.class);
    }

    protected <ENTITY extends UrlQueue> PagingResultBean<ENTITY> doSelectPage(UrlQueueCB cb, Class<ENTITY> tp) {
        assertCBStateValid(cb); assertObjectNotNull("entityType", tp);
        return helpSelectPageInternally(cb, tp, new InternalSelectPageCallback<ENTITY, UrlQueueCB>() {
            public int callbackSelectCount(UrlQueueCB cb) { return doSelectCountPlainly(cb); }
            public List<ENTITY> callbackSelectList(UrlQueueCB cb, Class<ENTITY> tp) { return doSelectList(cb, tp); }
        });
    }

    @Override
    protected PagingResultBean<? extends Entity> doReadPage(ConditionBean cb) {
        return selectPage(downcast(cb));
    }

    // ===================================================================================
    //                                                                       Cursor Select
    //                                                                       =============
    /**
     * Select the cursor by the condition-bean.
     * <pre>
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * urlQueueBhv.<span style="color: #DD4747">selectCursor</span>(cb, new EntityRowHandler&lt;UrlQueue&gt;() {
     *     public void handle(UrlQueue entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @param entityRowHandler The handler of entity row of UrlQueue. (NotNull)
     */
    public void selectCursor(UrlQueueCB cb, EntityRowHandler<UrlQueue> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, UrlQueue.class);
    }

    protected <ENTITY extends UrlQueue> void doSelectCursor(UrlQueueCB cb, EntityRowHandler<ENTITY> handler, Class<ENTITY> tp) {
        assertCBStateValid(cb); assertObjectNotNull("entityRowHandler", handler); assertObjectNotNull("entityType", tp);
        assertSpecifyDerivedReferrerEntityProperty(cb, tp);
        helpSelectCursorInternally(cb, handler, tp, new InternalSelectCursorCallback<ENTITY, UrlQueueCB>() {
            public void callbackSelectCursor(UrlQueueCB cb, EntityRowHandler<ENTITY> handler, Class<ENTITY> tp) { delegateSelectCursor(cb, handler, tp); }
            public List<ENTITY> callbackSelectList(UrlQueueCB cb, Class<ENTITY> tp) { return doSelectList(cb, tp); }
        });
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    /**
     * Select the scalar value derived by a function from uniquely-selected records. <br />
     * You should call a function method after this method called like as follows:
     * <pre>
     * urlQueueBhv.<span style="color: #DD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(UrlQueueCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar function object to specify function for scalar value. (NotNull)
     */
    public <RESULT> SLFunction<UrlQueueCB, RESULT> scalarSelect(Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends UrlQueueCB> SLFunction<CB, RESULT> doScalarSelect(Class<RESULT> tp, CB cb) {
        assertObjectNotNull("resultType", tp); assertCBStateValid(cb);
        cb.xsetupForScalarSelect(); cb.getSqlClause().disableSelectIndex(); // for when you use union
        return createSLFunction(cb, tp);
    }

    protected <RESULT, CB extends UrlQueueCB> SLFunction<CB, RESULT> createSLFunction(CB cb, Class<RESULT> tp) {
        return new SLFunction<CB, RESULT>(cb, tp);
    }

    protected <RESULT> SLFunction<? extends ConditionBean, RESULT> doReadScalar(Class<RESULT> tp) {
        return doScalarSelect(tp, newMyConditionBean());
    }

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    @Override
    protected Number doReadNextVal() {
        String msg = "This table is NOT related to sequence: " + getTableDbName();
        throw new UnsupportedOperationException(msg);
    }

    // ===================================================================================
    //                                                                   Pull out Relation
    //                                                                   =================

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param urlQueueList The list of urlQueue. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(List<UrlQueue> urlQueueList) {
        return helpExtractListInternally(urlQueueList, new InternalExtractCallback<UrlQueue, Long>() {
            public Long getCV(UrlQueue et) { return et.getId(); }
        });
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity modified-only. (DefaultConstraintsEnabled)
     * <pre>
     * UrlQueue urlQueue = new UrlQueue();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * urlQueue.setFoo...(value);
     * urlQueue.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//urlQueue.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//urlQueue.set...;</span>
     * urlQueueBhv.<span style="color: #DD4747">insert</span>(urlQueue);
     * ... = urlQueue.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * <p>While, when the entity is created by select, all columns are registered.</p>
     * @param urlQueue The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(UrlQueue urlQueue) {
        doInsert(urlQueue, null);
    }

    protected void doInsert(UrlQueue urlQueue, InsertOption<UrlQueueCB> op) {
        assertObjectNotNull("urlQueue", urlQueue);
        prepareInsertOption(op);
        delegateInsert(urlQueue, op);
    }

    protected void prepareInsertOption(InsertOption<UrlQueueCB> op) {
        if (op == null) { return; }
        assertInsertOptionStatus(op);
        if (op.hasSpecifiedInsertColumn()) {
            op.resolveInsertColumnSpecification(createCBForSpecifiedUpdate());
        }
    }

    @Override
    protected void doCreate(Entity et, InsertOption<? extends ConditionBean> op) {
        if (op == null) { insert(downcast(et)); }
        else { varyingInsert(downcast(et), downcast(op)); }
    }

    /**
     * Update the entity modified-only. (ZeroUpdateException, NonExclusiveControl)
     * <pre>
     * UrlQueue urlQueue = new UrlQueue();
     * urlQueue.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * urlQueue.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//urlQueue.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//urlQueue.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * urlQueue.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     urlQueueBhv.<span style="color: #DD4747">update</span>(urlQueue);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param urlQueue The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final UrlQueue urlQueue) {
        doUpdate(urlQueue, null);
    }

    protected void doUpdate(UrlQueue urlQueue, final UpdateOption<UrlQueueCB> op) {
        assertObjectNotNull("urlQueue", urlQueue);
        prepareUpdateOption(op);
        helpUpdateInternally(urlQueue, new InternalUpdateCallback<UrlQueue>() {
            public int callbackDelegateUpdate(UrlQueue et) { return delegateUpdate(et, op); } });
    }

    protected void prepareUpdateOption(UpdateOption<UrlQueueCB> op) {
        if (op == null) { return; }
        assertUpdateOptionStatus(op);
        if (op.hasSelfSpecification()) {
            op.resolveSelfSpecification(createCBForVaryingUpdate());
        }
        if (op.hasSpecifiedUpdateColumn()) {
            op.resolveUpdateColumnSpecification(createCBForSpecifiedUpdate());
        }
    }

    protected UrlQueueCB createCBForVaryingUpdate() {
        UrlQueueCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected UrlQueueCB createCBForSpecifiedUpdate() {
        UrlQueueCB cb = newMyConditionBean();
        cb.xsetupForSpecifiedUpdate();
        return cb;
    }

    @Override
    protected void doModify(Entity et, UpdateOption<? extends ConditionBean> op) {
        if (op == null) { update(downcast(et)); }
        else { varyingUpdate(downcast(et), downcast(op)); }
    }

    @Override
    protected void doModifyNonstrict(Entity et, UpdateOption<? extends ConditionBean> op) {
        doModify(et, op);
    }

    /**
     * Insert or update the entity modified-only. (DefaultConstraintsEnabled, NonExclusiveControl) <br />
     * if (the entity has no PK) { insert() } else { update(), but no data, insert() } <br />
     * <p><span style="color: #DD4747; font-size: 120%">Attention, you cannot update by unique keys instead of PK.</span></p>
     * @param urlQueue The entity of insert or update target. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(UrlQueue urlQueue) {
        doInesrtOrUpdate(urlQueue, null, null);
    }

    protected void doInesrtOrUpdate(UrlQueue urlQueue, final InsertOption<UrlQueueCB> iop, final UpdateOption<UrlQueueCB> uop) {
        helpInsertOrUpdateInternally(urlQueue, new InternalInsertOrUpdateCallback<UrlQueue, UrlQueueCB>() {
            public void callbackInsert(UrlQueue et) { doInsert(et, iop); }
            public void callbackUpdate(UrlQueue et) { doUpdate(et, uop); }
            public UrlQueueCB callbackNewMyConditionBean() { return newMyConditionBean(); }
            public int callbackSelectCount(UrlQueueCB cb) { return selectCount(cb); }
        });
    }

    @Override
    protected void doCreateOrModify(Entity et, InsertOption<? extends ConditionBean> iop, UpdateOption<? extends ConditionBean> uop) {
        if (iop == null && uop == null) { insertOrUpdate(downcast(et)); }
        else {
            iop = iop != null ? iop : new InsertOption<UrlQueueCB>();
            uop = uop != null ? uop : new UpdateOption<UrlQueueCB>();
            varyingInsertOrUpdate(downcast(et), downcast(iop), downcast(uop));
        }
    }

    @Override
    protected void doCreateOrModifyNonstrict(Entity et, InsertOption<? extends ConditionBean> iop, UpdateOption<? extends ConditionBean> uop) {
        doCreateOrModify(et, iop, uop);
    }

    /**
     * Delete the entity. (ZeroUpdateException, NonExclusiveControl)
     * <pre>
     * UrlQueue urlQueue = new UrlQueue();
     * urlQueue.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * urlQueue.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     urlQueueBhv.<span style="color: #DD4747">delete</span>(urlQueue);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param urlQueue The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(UrlQueue urlQueue) {
        doDelete(urlQueue, null);
    }

    protected void doDelete(UrlQueue urlQueue, final DeleteOption<UrlQueueCB> op) {
        assertObjectNotNull("urlQueue", urlQueue);
        prepareDeleteOption(op);
        helpDeleteInternally(urlQueue, new InternalDeleteCallback<UrlQueue>() {
            public int callbackDelegateDelete(UrlQueue et) { return delegateDelete(et, op); } });
    }

    protected void prepareDeleteOption(DeleteOption<UrlQueueCB> op) {
        if (op == null) { return; }
        assertDeleteOptionStatus(op);
    }

    @Override
    protected void doRemove(Entity et, DeleteOption<? extends ConditionBean> op) {
        if (op == null) { delete(downcast(et)); }
        else { varyingDelete(downcast(et), downcast(op)); }
    }

    @Override
    protected void doRemoveNonstrict(Entity et, DeleteOption<? extends ConditionBean> op) {
        doRemove(et, op);
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch-insert the entity list modified-only of same-set columns. (DefaultConstraintsEnabled) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <p><span style="color: #DD4747; font-size: 120%">The columns of least common multiple are registered like this:</span></p>
     * <pre>
     * for (... : ...) {
     *     UrlQueue urlQueue = new UrlQueue();
     *     urlQueue.setFooName("foo");
     *     if (...) {
     *         urlQueue.setFooPrice(123);
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are registered</span>
     *     <span style="color: #3F7E5E">// FOO_PRICE not-called in any entities are registered as null without default value</span>
     *     <span style="color: #3F7E5E">// columns not-called in all entities are registered as null or default value</span>
     *     urlQueueList.add(urlQueue);
     * }
     * urlQueueBhv.<span style="color: #DD4747">batchInsert</span>(urlQueueList);
     * </pre>
     * <p>While, when the entities are created by select, all columns are registered.</p>
     * <p>And if the table has an identity, entities after the process don't have incremented values.
     * (When you use the (normal) insert(), you can get the incremented value from your entity)</p>
     * @param urlQueueList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(List<UrlQueue> urlQueueList) {
        InsertOption<UrlQueueCB> op = createInsertUpdateOption();
        return doBatchInsert(urlQueueList, op);
    }

    protected int[] doBatchInsert(List<UrlQueue> urlQueueList, InsertOption<UrlQueueCB> op) {
        assertObjectNotNull("urlQueueList", urlQueueList);
        prepareBatchInsertOption(urlQueueList, op);
        return delegateBatchInsert(urlQueueList, op);
    }

    protected void prepareBatchInsertOption(List<UrlQueue> urlQueueList, InsertOption<UrlQueueCB> op) {
        op.xallowInsertColumnModifiedPropertiesFragmented();
        op.xacceptInsertColumnModifiedPropertiesIfNeeds(urlQueueList);
        prepareInsertOption(op);
    }

    @Override
    protected int[] doLumpCreate(List<Entity> ls, InsertOption<? extends ConditionBean> op) {
        if (op == null) { return batchInsert(downcast(ls)); }
        else { return varyingBatchInsert(downcast(ls), downcast(op)); }
    }

    /**
     * Batch-update the entity list modified-only of same-set columns. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement. <br />
     * <span style="color: #DD4747; font-size: 120%">You should specify same-set columns to all entities like this:</span>
     * <pre>
     * for (... : ...) {
     *     UrlQueue urlQueue = new UrlQueue();
     *     urlQueue.setFooName("foo");
     *     if (...) {
     *         urlQueue.setFooPrice(123);
     *     } else {
     *         urlQueue.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//urlQueue.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     urlQueueList.add(urlQueue);
     * }
     * urlQueueBhv.<span style="color: #DD4747">batchUpdate</span>(urlQueueList);
     * </pre>
     * @param urlQueueList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(List<UrlQueue> urlQueueList) {
        UpdateOption<UrlQueueCB> op = createPlainUpdateOption();
        return doBatchUpdate(urlQueueList, op);
    }

    protected int[] doBatchUpdate(List<UrlQueue> urlQueueList, UpdateOption<UrlQueueCB> op) {
        assertObjectNotNull("urlQueueList", urlQueueList);
        prepareBatchUpdateOption(urlQueueList, op);
        return delegateBatchUpdate(urlQueueList, op);
    }

    protected void prepareBatchUpdateOption(List<UrlQueue> urlQueueList, UpdateOption<UrlQueueCB> op) {
        op.xacceptUpdateColumnModifiedPropertiesIfNeeds(urlQueueList);
        prepareUpdateOption(op);
    }

    @Override
    protected int[] doLumpModify(List<Entity> ls, UpdateOption<? extends ConditionBean> op) {
        if (op == null) { return batchUpdate(downcast(ls)); }
        else { return varyingBatchUpdate(downcast(ls), downcast(op)); }
    }

    /**
     * Batch-update the entity list specified-only. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * <pre>
     * <span style="color: #3F7E5E">// e.g. update two columns only</span>
     * urlQueueBhv.<span style="color: #DD4747">batchUpdate</span>(urlQueueList, new SpecifyQuery<UrlQueueCB>() {
     *     public void specify(UrlQueueCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #DD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #DD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * urlQueueBhv.<span style="color: #DD4747">batchUpdate</span>(urlQueueList, new SpecifyQuery<UrlQueueCB>() {
     *     public void specify(UrlQueueCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #DD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param urlQueueList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(List<UrlQueue> urlQueueList, SpecifyQuery<UrlQueueCB> updateColumnSpec) {
        return doBatchUpdate(urlQueueList, createSpecifiedUpdateOption(updateColumnSpec));
    }

    @Override
    protected int[] doLumpModifyNonstrict(List<Entity> ls, UpdateOption<? extends ConditionBean> op) {
        return doLumpModify(ls, op);
    }

    /**
     * Batch-delete the entity list. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param urlQueueList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDelete(List<UrlQueue> urlQueueList) {
        return doBatchDelete(urlQueueList, null);
    }

    protected int[] doBatchDelete(List<UrlQueue> urlQueueList, DeleteOption<UrlQueueCB> op) {
        assertObjectNotNull("urlQueueList", urlQueueList);
        prepareDeleteOption(op);
        return delegateBatchDelete(urlQueueList, op);
    }

    @Override
    protected int[] doLumpRemove(List<Entity> ls, DeleteOption<? extends ConditionBean> op) {
        if (op == null) { return batchDelete(downcast(ls)); }
        else { return varyingBatchDelete(downcast(ls), downcast(op)); }
    }

    @Override
    protected int[] doLumpRemoveNonstrict(List<Entity> ls, DeleteOption<? extends ConditionBean> op) {
        return doLumpRemove(ls, op);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Insert the several entities by query (modified-only for fixed value).
     * <pre>
     * urlQueueBhv.<span style="color: #DD4747">queryInsert</span>(new QueryInsertSetupper&lt;UrlQueue, UrlQueueCB&gt;() {
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
     * @param setupper The setup-per of query-insert. (NotNull)
     * @return The inserted count.
     */
    public int queryInsert(QueryInsertSetupper<UrlQueue, UrlQueueCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(QueryInsertSetupper<UrlQueue, UrlQueueCB> sp, InsertOption<UrlQueueCB> op) {
        assertObjectNotNull("setupper", sp);
        prepareInsertOption(op);
        UrlQueue e = new UrlQueue();
        UrlQueueCB cb = createCBForQueryInsert();
        return delegateQueryInsert(e, cb, sp.setup(e, cb), op);
    }

    protected UrlQueueCB createCBForQueryInsert() {
        UrlQueueCB cb = newMyConditionBean();
        cb.xsetupForQueryInsert();
        return cb;
    }

    @Override
    protected int doRangeCreate(QueryInsertSetupper<? extends Entity, ? extends ConditionBean> setupper, InsertOption<? extends ConditionBean> option) {
        if (option == null) { return queryInsert(downcast(setupper)); }
        else { return varyingQueryInsert(downcast(setupper), downcast(option)); }
    }

    /**
     * Update the several entities by query non-strictly modified-only. (NonExclusiveControl)
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
     * urlQueueBhv.<span style="color: #DD4747">queryUpdate</span>(urlQueue, cb);
     * </pre>
     * @param urlQueue The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(UrlQueue urlQueue, UrlQueueCB cb) {
        return doQueryUpdate(urlQueue, cb, null);
    }

    protected int doQueryUpdate(UrlQueue urlQueue, UrlQueueCB cb, UpdateOption<UrlQueueCB> op) {
        assertObjectNotNull("urlQueue", urlQueue); assertCBStateValid(cb);
        prepareUpdateOption(op);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(urlQueue, cb, op) : 0;
    }

    @Override
    protected int doRangeModify(Entity et, ConditionBean cb, UpdateOption<? extends ConditionBean> op) {
        if (op == null) { return queryUpdate(downcast(et), (UrlQueueCB)cb); }
        else { return varyingQueryUpdate(downcast(et), (UrlQueueCB)cb, downcast(op)); }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * UrlQueueCB cb = new UrlQueueCB();
     * cb.query().setFoo...(value);
     * urlQueueBhv.<span style="color: #DD4747">queryDelete</span>(urlQueue, cb);
     * </pre>
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(UrlQueueCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(UrlQueueCB cb, DeleteOption<UrlQueueCB> op) {
        assertCBStateValid(cb);
        prepareDeleteOption(op);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb, op) : 0;
    }

    @Override
    protected int doRangeRemove(ConditionBean cb, DeleteOption<? extends ConditionBean> op) {
        if (op == null) { return queryDelete((UrlQueueCB)cb); }
        else { return varyingQueryDelete((UrlQueueCB)cb, downcast(op)); }
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
     * UrlQueue urlQueue = new UrlQueue();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * urlQueue.setFoo...(value);
     * urlQueue.setBar...(value);
     * InsertOption<UrlQueueCB> option = new InsertOption<UrlQueueCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * urlQueueBhv.<span style="color: #DD4747">varyingInsert</span>(urlQueue, option);
     * ... = urlQueue.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param urlQueue The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(UrlQueue urlQueue, InsertOption<UrlQueueCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(urlQueue, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * UrlQueue urlQueue = new UrlQueue();
     * urlQueue.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * urlQueue.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * urlQueue.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;UrlQueueCB&gt; option = new UpdateOption&lt;UrlQueueCB&gt;();
     *     option.self(new SpecifyQuery&lt;UrlQueueCB&gt;() {
     *         public void specify(UrlQueueCB cb) {
     *             cb.specify().<span style="color: #DD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     urlQueueBhv.<span style="color: #DD4747">varyingUpdate</span>(urlQueue, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param urlQueue The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(UrlQueue urlQueue, UpdateOption<UrlQueueCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(urlQueue, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param urlQueue The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(UrlQueue urlQueue, InsertOption<UrlQueueCB> insertOption, UpdateOption<UrlQueueCB> updateOption) {
        assertInsertOptionNotNull(insertOption); assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(urlQueue, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param urlQueue The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(UrlQueue urlQueue, DeleteOption<UrlQueueCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(urlQueue, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param urlQueueList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(List<UrlQueue> urlQueueList, InsertOption<UrlQueueCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(urlQueueList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param urlQueueList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(List<UrlQueue> urlQueueList, UpdateOption<UrlQueueCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(urlQueueList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param urlQueueList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(List<UrlQueue> urlQueueList, DeleteOption<UrlQueueCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(urlQueueList, option);
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
    public int varyingQueryInsert(QueryInsertSetupper<UrlQueue, UrlQueueCB> setupper, InsertOption<UrlQueueCB> option) {
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
     *         cb.specify().<span style="color: #DD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * urlQueueBhv.<span style="color: #DD4747">varyingQueryUpdate</span>(urlQueue, cb, option);
     * </pre>
     * @param urlQueue The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(UrlQueue urlQueue, UrlQueueCB cb, UpdateOption<UrlQueueCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(urlQueue, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(UrlQueueCB cb, DeleteOption<UrlQueueCB> option) {
        assertDeleteOptionNotNull(option);
        return doQueryDelete(cb, option);
    }

    // ===================================================================================
    //                                                                          OutsideSql
    //                                                                          ==========
    /**
     * Prepare the basic executor of outside-SQL to execute it. <br />
     * The invoker of behavior command should be not null when you call this method.
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
     * @return The basic executor of outside-SQL. (NotNull)
     */
    public OutsideSqlBasicExecutor<UrlQueueBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(UrlQueueCB cb) { return invoke(createSelectCountCBCommand(cb, true)); }
    protected int delegateSelectCountPlainly(UrlQueueCB cb) { return invoke(createSelectCountCBCommand(cb, false)); }
    protected <ENTITY extends UrlQueue> void delegateSelectCursor(UrlQueueCB cb, EntityRowHandler<ENTITY> rh, Class<ENTITY> tp)
    { invoke(createSelectCursorCBCommand(cb, rh, tp)); }
    protected <ENTITY extends UrlQueue> List<ENTITY> delegateSelectList(UrlQueueCB cb, Class<ENTITY> tp)
    { return invoke(createSelectListCBCommand(cb, tp)); }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(UrlQueue et, InsertOption<UrlQueueCB> op)
    { if (!processBeforeInsert(et, op)) { return 0; }
      return invoke(createInsertEntityCommand(et, op)); }
    protected int delegateUpdate(UrlQueue et, UpdateOption<UrlQueueCB> op)
    { if (!processBeforeUpdate(et, op)) { return 0; }
      return delegateUpdateNonstrict(et, op); }
    protected int delegateUpdateNonstrict(UrlQueue et, UpdateOption<UrlQueueCB> op)
    { if (!processBeforeUpdate(et, op)) { return 0; }
      return invoke(createUpdateNonstrictEntityCommand(et, op)); }
    protected int delegateDelete(UrlQueue et, DeleteOption<UrlQueueCB> op)
    { if (!processBeforeDelete(et, op)) { return 0; }
      return delegateDeleteNonstrict(et, op); }
    protected int delegateDeleteNonstrict(UrlQueue et, DeleteOption<UrlQueueCB> op)
    { if (!processBeforeDelete(et, op)) { return 0; }
      return invoke(createDeleteNonstrictEntityCommand(et, op)); }

    protected int[] delegateBatchInsert(List<UrlQueue> ls, InsertOption<UrlQueueCB> op)
    { if (ls.isEmpty()) { return new int[]{}; }
      return invoke(createBatchInsertCommand(processBatchInternally(ls, op), op)); }
    protected int[] delegateBatchUpdate(List<UrlQueue> ls, UpdateOption<UrlQueueCB> op)
    { if (ls.isEmpty()) { return new int[]{}; }
      return delegateBatchUpdateNonstrict(ls, op); }
    protected int[] delegateBatchUpdateNonstrict(List<UrlQueue> ls, UpdateOption<UrlQueueCB> op)
    { if (ls.isEmpty()) { return new int[]{}; }
      return invoke(createBatchUpdateNonstrictCommand(processBatchInternally(ls, op, true), op)); }
    protected int[] delegateBatchDelete(List<UrlQueue> ls, DeleteOption<UrlQueueCB> op)
    { if (ls.isEmpty()) { return new int[]{}; }
      return delegateBatchDeleteNonstrict(ls, op); }
    protected int[] delegateBatchDeleteNonstrict(List<UrlQueue> ls, DeleteOption<UrlQueueCB> op)
    { if (ls.isEmpty()) { return new int[]{}; }
      return invoke(createBatchDeleteNonstrictCommand(processBatchInternally(ls, op, true), op)); }

    protected int delegateQueryInsert(UrlQueue et, UrlQueueCB inCB, ConditionBean resCB, InsertOption<UrlQueueCB> op)
    { if (!processBeforeQueryInsert(et, inCB, resCB, op)) { return 0; } return invoke(createQueryInsertCBCommand(et, inCB, resCB, op));  }
    protected int delegateQueryUpdate(UrlQueue et, UrlQueueCB cb, UpdateOption<UrlQueueCB> op)
    { if (!processBeforeQueryUpdate(et, cb, op)) { return 0; } return invoke(createQueryUpdateCBCommand(et, cb, op));  }
    protected int delegateQueryDelete(UrlQueueCB cb, DeleteOption<UrlQueueCB> op)
    { if (!processBeforeQueryDelete(cb, op)) { return 0; } return invoke(createQueryDeleteCBCommand(cb, op));  }

    // ===================================================================================
    //                                                                Optimistic Lock Info
    //                                                                ====================
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasVersionNoValue(Entity et) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasUpdateDateValue(Entity et) {
        return false;
    }

    // ===================================================================================
    //                                                                     Downcast Helper
    //                                                                     ===============
    protected UrlQueue downcast(Entity et) {
        return helpEntityDowncastInternally(et, UrlQueue.class);
    }

    protected UrlQueueCB downcast(ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb, UrlQueueCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<UrlQueue> downcast(List<? extends Entity> ls) {
        return (List<UrlQueue>)ls;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<UrlQueueCB> downcast(InsertOption<? extends ConditionBean> op) {
        return (InsertOption<UrlQueueCB>)op;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<UrlQueueCB> downcast(UpdateOption<? extends ConditionBean> op) {
        return (UpdateOption<UrlQueueCB>)op;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<UrlQueueCB> downcast(DeleteOption<? extends ConditionBean> op) {
        return (DeleteOption<UrlQueueCB>)op;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<UrlQueue, UrlQueueCB> downcast(QueryInsertSetupper<? extends Entity, ? extends ConditionBean> sp) {
        return (QueryInsertSetupper<UrlQueue, UrlQueueCB>)sp;
    }
}
