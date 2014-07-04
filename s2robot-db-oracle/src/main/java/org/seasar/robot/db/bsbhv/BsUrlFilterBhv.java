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
 * The behavior of URL_FILTER as TABLE. <br />
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, SESSION_ID, URL, FILTER_TYPE, CREATE_TIME
 *
 * [sequence]
 *     URL_FILTER_SEQ
 *
 * [identity]
 *     
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
public abstract class BsUrlFilterBhv extends AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:beginQueryPath*/
    /*df:endQueryPath*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() { return "URL_FILTER"; }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() { return UrlFilterDbm.getInstance(); }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public UrlFilterDbm getMyDBMeta() { return UrlFilterDbm.getInstance(); }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    /** {@inheritDoc} */
    public Entity newEntity() { return newMyEntity(); }

    /** {@inheritDoc} */
    public ConditionBean newConditionBean() { return newMyConditionBean(); }

    /** @return The instance of new entity as my table type. (NotNull) */
    public UrlFilter newMyEntity() { return new UrlFilter(); }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public UrlFilterCB newMyConditionBean() { return new UrlFilterCB(); }

    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of uniquely-selected records by the condition-bean. {IgnorePagingCondition, IgnoreSpecifyColumn}<br />
     * SpecifyColumn is ignored but you can use it only to remove text type column for union's distinct.
     * <pre>
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * int count = urlFilterBhv.<span style="color: #DD4747">selectCount</span>(cb);
     * </pre>
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @return The count for the condition. (NotMinus)
     */
    public int selectCount(UrlFilterCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(UrlFilterCB cb) { // called by selectCount(cb)
        assertCBStateValid(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(UrlFilterCB cb) { // called by selectPage(cb)
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
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * UrlFilter urlFilter = urlFilterBhv.<span style="color: #DD4747">selectEntity</span>(cb);
     * if (urlFilter != null) { <span style="color: #3F7E5E">// null check</span>
     *     ... = urlFilter.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @return The entity selected by the condition. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public UrlFilter selectEntity(UrlFilterCB cb) {
        return doSelectEntity(cb, UrlFilter.class);
    }

    protected <ENTITY extends UrlFilter> ENTITY doSelectEntity(UrlFilterCB cb, Class<ENTITY> tp) {
        assertCBStateValid(cb); assertObjectNotNull("entityType", tp);
        return helpSelectEntityInternally(cb, tp, new InternalSelectEntityCallback<ENTITY, UrlFilterCB>() {
            public List<ENTITY> callbackSelectList(UrlFilterCB lcb, Class<ENTITY> ltp) { return doSelectList(lcb, ltp); } });
    }

    protected <ENTITY extends UrlFilter> OptionalEntity<ENTITY> doSelectOptionalEntity(UrlFilterCB cb, Class<ENTITY> tp) {
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
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * UrlFilter urlFilter = urlFilterBhv.<span style="color: #DD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = urlFilter.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @return The entity selected by the condition. (NotNull: if no data, throws exception)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public UrlFilter selectEntityWithDeletedCheck(UrlFilterCB cb) {
        return doSelectEntityWithDeletedCheck(cb, UrlFilter.class);
    }

    protected <ENTITY extends UrlFilter> ENTITY doSelectEntityWithDeletedCheck(UrlFilterCB cb, Class<ENTITY> tp) {
        assertCBStateValid(cb); assertObjectNotNull("entityType", tp);
        return helpSelectEntityWithDeletedCheckInternally(cb, tp, new InternalSelectEntityWithDeletedCheckCallback<ENTITY, UrlFilterCB>() {
            public List<ENTITY> callbackSelectList(UrlFilterCB lcb, Class<ENTITY> ltp) { return doSelectList(lcb, ltp); } });
    }

    @Override
    protected Entity doReadEntityWithDeletedCheck(ConditionBean cb) {
        return selectEntityWithDeletedCheck(downcast(cb));
    }

    /**
     * Select the entity by the primary-key value.
     * @param id : PK, NotNull, NUMBER(19). (NotNull)
     * @return The entity selected by the PK. (NullAllowed: if no data, it returns null)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public UrlFilter selectByPKValue(Long id) {
        return doSelectByPK(id, UrlFilter.class);
    }

    protected <ENTITY extends UrlFilter> ENTITY doSelectByPK(Long id, Class<ENTITY> entityType) {
        return doSelectEntity(xprepareCBAsPK(id), entityType);
    }

    protected <ENTITY extends UrlFilter> OptionalEntity<ENTITY> doSelectOptionalByPK(Long id, Class<ENTITY> entityType) {
        return createOptionalEntity(doSelectByPK(id, entityType), id);
    }

    /**
     * Select the entity by the primary-key value with deleted check.
     * @param id : PK, NotNull, NUMBER(19). (NotNull)
     * @return The entity selected by the PK. (NotNull: if no data, throws exception)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception SelectEntityConditionNotFoundException When the condition for selecting an entity is not found.
     */
    public UrlFilter selectByPKValueWithDeletedCheck(Long id) {
        return doSelectByPKWithDeletedCheck(id, UrlFilter.class);
    }

    protected <ENTITY extends UrlFilter> ENTITY doSelectByPKWithDeletedCheck(Long id, Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(xprepareCBAsPK(id), entityType);
    }

    protected UrlFilterCB xprepareCBAsPK(Long id) {
        assertObjectNotNull("id", id);
        UrlFilterCB cb = newMyConditionBean(); cb.acceptPrimaryKey(id);
        return cb;
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * <pre>
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;UrlFilter&gt; urlFilterList = urlFilterBhv.<span style="color: #DD4747">selectList</span>(cb);
     * for (UrlFilter urlFilter : urlFilterList) {
     *     ... = urlFilter.get...();
     * }
     * </pre>
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @return The result bean of selected list. (NotNull: if no data, returns empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public ListResultBean<UrlFilter> selectList(UrlFilterCB cb) {
        return doSelectList(cb, UrlFilter.class);
    }

    protected <ENTITY extends UrlFilter> ListResultBean<ENTITY> doSelectList(UrlFilterCB cb, Class<ENTITY> tp) {
        assertCBStateValid(cb); assertObjectNotNull("entityType", tp);
        assertSpecifyDerivedReferrerEntityProperty(cb, tp);
        return helpSelectListInternally(cb, tp, new InternalSelectListCallback<ENTITY, UrlFilterCB>() {
            public List<ENTITY> callbackSelectList(UrlFilterCB lcb, Class<ENTITY> ltp) { return delegateSelectList(lcb, ltp); } });
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
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #DD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;UrlFilter&gt; page = urlFilterBhv.<span style="color: #DD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (UrlFilter urlFilter : page) {
     *     ... = urlFilter.get...();
     * }
     * </pre>
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @return The result bean of selected page. (NotNull: if no data, returns bean as empty list)
     * @exception DangerousResultSizeException When the result size is over the specified safety size.
     */
    public PagingResultBean<UrlFilter> selectPage(UrlFilterCB cb) {
        return doSelectPage(cb, UrlFilter.class);
    }

    protected <ENTITY extends UrlFilter> PagingResultBean<ENTITY> doSelectPage(UrlFilterCB cb, Class<ENTITY> tp) {
        assertCBStateValid(cb); assertObjectNotNull("entityType", tp);
        return helpSelectPageInternally(cb, tp, new InternalSelectPageCallback<ENTITY, UrlFilterCB>() {
            public int callbackSelectCount(UrlFilterCB cb) { return doSelectCountPlainly(cb); }
            public List<ENTITY> callbackSelectList(UrlFilterCB cb, Class<ENTITY> tp) { return doSelectList(cb, tp); }
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
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * urlFilterBhv.<span style="color: #DD4747">selectCursor</span>(cb, new EntityRowHandler&lt;UrlFilter&gt;() {
     *     public void handle(UrlFilter entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @param entityRowHandler The handler of entity row of UrlFilter. (NotNull)
     */
    public void selectCursor(UrlFilterCB cb, EntityRowHandler<UrlFilter> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, UrlFilter.class);
    }

    protected <ENTITY extends UrlFilter> void doSelectCursor(UrlFilterCB cb, EntityRowHandler<ENTITY> handler, Class<ENTITY> tp) {
        assertCBStateValid(cb); assertObjectNotNull("entityRowHandler", handler); assertObjectNotNull("entityType", tp);
        assertSpecifyDerivedReferrerEntityProperty(cb, tp);
        helpSelectCursorInternally(cb, handler, tp, new InternalSelectCursorCallback<ENTITY, UrlFilterCB>() {
            public void callbackSelectCursor(UrlFilterCB cb, EntityRowHandler<ENTITY> handler, Class<ENTITY> tp) { delegateSelectCursor(cb, handler, tp); }
            public List<ENTITY> callbackSelectList(UrlFilterCB cb, Class<ENTITY> tp) { return doSelectList(cb, tp); }
        });
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    /**
     * Select the scalar value derived by a function from uniquely-selected records. <br />
     * You should call a function method after this method called like as follows:
     * <pre>
     * urlFilterBhv.<span style="color: #DD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(UrlFilterCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooDatetime()</span>; <span style="color: #3F7E5E">// required for a function</span>
     *         cb.query().setBarName_PrefixSearch("S");
     *     }
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar function object to specify function for scalar value. (NotNull)
     */
    public <RESULT> SLFunction<UrlFilterCB, RESULT> scalarSelect(Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends UrlFilterCB> SLFunction<CB, RESULT> doScalarSelect(Class<RESULT> tp, CB cb) {
        assertObjectNotNull("resultType", tp); assertCBStateValid(cb);
        cb.xsetupForScalarSelect(); cb.getSqlClause().disableSelectIndex(); // for when you use union
        return createSLFunction(cb, tp);
    }

    protected <RESULT, CB extends UrlFilterCB> SLFunction<CB, RESULT> createSLFunction(CB cb, Class<RESULT> tp) {
        return new SLFunction<CB, RESULT>(cb, tp);
    }

    protected <RESULT> SLFunction<? extends ConditionBean, RESULT> doReadScalar(Class<RESULT> tp) {
        return doScalarSelect(tp, newMyConditionBean());
    }

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    /**
     * Select the next value as sequence. <br />
     * This method is called when insert() and set to primary-key automatically.
     * So you don't need to call this as long as you need to get next value before insert().
     * @return The next value. (NotNull)
     */
    public Long selectNextVal() {
        return doSelectNextVal(Long.class);
    }

    protected <RESULT> RESULT doSelectNextVal(Class<RESULT> tp) {
        return delegateSelectNextVal(tp);
    }

    @Override
    protected Number doReadNextVal() {
        return selectNextVal();
    }

    // ===================================================================================
    //                                                                   Pull out Relation
    //                                                                   =================

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    /**
     * Extract the value list of (single) primary key id.
     * @param urlFilterList The list of urlFilter. (NotNull, EmptyAllowed)
     * @return The list of the column value. (NotNull, EmptyAllowed, NotNullElement)
     */
    public List<Long> extractIdList(List<UrlFilter> urlFilterList) {
        return helpExtractListInternally(urlFilterList, new InternalExtractCallback<UrlFilter, Long>() {
            public Long getCV(UrlFilter et) { return et.getId(); }
        });
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity modified-only. (DefaultConstraintsEnabled)
     * <pre>
     * UrlFilter urlFilter = new UrlFilter();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * urlFilter.setFoo...(value);
     * urlFilter.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//urlFilter.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//urlFilter.set...;</span>
     * urlFilterBhv.<span style="color: #DD4747">insert</span>(urlFilter);
     * ... = urlFilter.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * <p>While, when the entity is created by select, all columns are registered.</p>
     * @param urlFilter The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insert(UrlFilter urlFilter) {
        doInsert(urlFilter, null);
    }

    protected void doInsert(UrlFilter urlFilter, InsertOption<UrlFilterCB> op) {
        assertObjectNotNull("urlFilter", urlFilter);
        prepareInsertOption(op);
        delegateInsert(urlFilter, op);
    }

    protected void prepareInsertOption(InsertOption<UrlFilterCB> op) {
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
     * UrlFilter urlFilter = new UrlFilter();
     * urlFilter.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * urlFilter.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//urlFilter.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//urlFilter.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * urlFilter.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     urlFilterBhv.<span style="color: #DD4747">update</span>(urlFilter);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param urlFilter The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void update(final UrlFilter urlFilter) {
        doUpdate(urlFilter, null);
    }

    protected void doUpdate(UrlFilter urlFilter, final UpdateOption<UrlFilterCB> op) {
        assertObjectNotNull("urlFilter", urlFilter);
        prepareUpdateOption(op);
        helpUpdateInternally(urlFilter, new InternalUpdateCallback<UrlFilter>() {
            public int callbackDelegateUpdate(UrlFilter et) { return delegateUpdate(et, op); } });
    }

    protected void prepareUpdateOption(UpdateOption<UrlFilterCB> op) {
        if (op == null) { return; }
        assertUpdateOptionStatus(op);
        if (op.hasSelfSpecification()) {
            op.resolveSelfSpecification(createCBForVaryingUpdate());
        }
        if (op.hasSpecifiedUpdateColumn()) {
            op.resolveUpdateColumnSpecification(createCBForSpecifiedUpdate());
        }
    }

    protected UrlFilterCB createCBForVaryingUpdate() {
        UrlFilterCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected UrlFilterCB createCBForSpecifiedUpdate() {
        UrlFilterCB cb = newMyConditionBean();
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
     * @param urlFilter The entity of insert or update target. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void insertOrUpdate(UrlFilter urlFilter) {
        doInesrtOrUpdate(urlFilter, null, null);
    }

    protected void doInesrtOrUpdate(UrlFilter urlFilter, final InsertOption<UrlFilterCB> iop, final UpdateOption<UrlFilterCB> uop) {
        helpInsertOrUpdateInternally(urlFilter, new InternalInsertOrUpdateCallback<UrlFilter, UrlFilterCB>() {
            public void callbackInsert(UrlFilter et) { doInsert(et, iop); }
            public void callbackUpdate(UrlFilter et) { doUpdate(et, uop); }
            public UrlFilterCB callbackNewMyConditionBean() { return newMyConditionBean(); }
            public int callbackSelectCount(UrlFilterCB cb) { return selectCount(cb); }
        });
    }

    @Override
    protected void doCreateOrModify(Entity et, InsertOption<? extends ConditionBean> iop, UpdateOption<? extends ConditionBean> uop) {
        if (iop == null && uop == null) { insertOrUpdate(downcast(et)); }
        else {
            iop = iop != null ? iop : new InsertOption<UrlFilterCB>();
            uop = uop != null ? uop : new UpdateOption<UrlFilterCB>();
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
     * UrlFilter urlFilter = new UrlFilter();
     * urlFilter.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * urlFilter.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     urlFilterBhv.<span style="color: #DD4747">delete</span>(urlFilter);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param urlFilter The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(UrlFilter urlFilter) {
        doDelete(urlFilter, null);
    }

    protected void doDelete(UrlFilter urlFilter, final DeleteOption<UrlFilterCB> op) {
        assertObjectNotNull("urlFilter", urlFilter);
        prepareDeleteOption(op);
        helpDeleteInternally(urlFilter, new InternalDeleteCallback<UrlFilter>() {
            public int callbackDelegateDelete(UrlFilter et) { return delegateDelete(et, op); } });
    }

    protected void prepareDeleteOption(DeleteOption<UrlFilterCB> op) {
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
     *     UrlFilter urlFilter = new UrlFilter();
     *     urlFilter.setFooName("foo");
     *     if (...) {
     *         urlFilter.setFooPrice(123);
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are registered</span>
     *     <span style="color: #3F7E5E">// FOO_PRICE not-called in any entities are registered as null without default value</span>
     *     <span style="color: #3F7E5E">// columns not-called in all entities are registered as null or default value</span>
     *     urlFilterList.add(urlFilter);
     * }
     * urlFilterBhv.<span style="color: #DD4747">batchInsert</span>(urlFilterList);
     * </pre>
     * <p>While, when the entities are created by select, all columns are registered.</p>
     * <p>And if the table has an identity, entities after the process don't have incremented values.
     * (When you use the (normal) insert(), you can get the incremented value from your entity)</p>
     * @param urlFilterList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNullAllowed: when auto-increment)
     * @return The array of inserted count. (NotNull, EmptyAllowed)
     */
    public int[] batchInsert(List<UrlFilter> urlFilterList) {
        InsertOption<UrlFilterCB> op = createInsertUpdateOption();
        return doBatchInsert(urlFilterList, op);
    }

    protected int[] doBatchInsert(List<UrlFilter> urlFilterList, InsertOption<UrlFilterCB> op) {
        assertObjectNotNull("urlFilterList", urlFilterList);
        prepareBatchInsertOption(urlFilterList, op);
        return delegateBatchInsert(urlFilterList, op);
    }

    protected void prepareBatchInsertOption(List<UrlFilter> urlFilterList, InsertOption<UrlFilterCB> op) {
        op.xallowInsertColumnModifiedPropertiesFragmented();
        op.xacceptInsertColumnModifiedPropertiesIfNeeds(urlFilterList);
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
     *     UrlFilter urlFilter = new UrlFilter();
     *     urlFilter.setFooName("foo");
     *     if (...) {
     *         urlFilter.setFooPrice(123);
     *     } else {
     *         urlFilter.setFooPrice(null); <span style="color: #3F7E5E">// updated as null</span>
     *         <span style="color: #3F7E5E">//urlFilter.setFooDate(...); // *not allowed, fragmented</span>
     *     }
     *     <span style="color: #3F7E5E">// FOO_NAME and FOO_PRICE (and record meta columns) are updated</span>
     *     <span style="color: #3F7E5E">// (others are not updated: their values are kept)</span>
     *     urlFilterList.add(urlFilter);
     * }
     * urlFilterBhv.<span style="color: #DD4747">batchUpdate</span>(urlFilterList);
     * </pre>
     * @param urlFilterList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(List<UrlFilter> urlFilterList) {
        UpdateOption<UrlFilterCB> op = createPlainUpdateOption();
        return doBatchUpdate(urlFilterList, op);
    }

    protected int[] doBatchUpdate(List<UrlFilter> urlFilterList, UpdateOption<UrlFilterCB> op) {
        assertObjectNotNull("urlFilterList", urlFilterList);
        prepareBatchUpdateOption(urlFilterList, op);
        return delegateBatchUpdate(urlFilterList, op);
    }

    protected void prepareBatchUpdateOption(List<UrlFilter> urlFilterList, UpdateOption<UrlFilterCB> op) {
        op.xacceptUpdateColumnModifiedPropertiesIfNeeds(urlFilterList);
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
     * urlFilterBhv.<span style="color: #DD4747">batchUpdate</span>(urlFilterList, new SpecifyQuery<UrlFilterCB>() {
     *     public void specify(UrlFilterCB cb) { <span style="color: #3F7E5E">// the two only updated</span>
     *         cb.specify().<span style="color: #DD4747">columnFooStatusCode()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *         cb.specify().<span style="color: #DD4747">columnBarDate()</span>; <span style="color: #3F7E5E">// should be modified in any entities</span>
     *     }
     * });
     * <span style="color: #3F7E5E">// e.g. update every column in the table</span>
     * urlFilterBhv.<span style="color: #DD4747">batchUpdate</span>(urlFilterList, new SpecifyQuery<UrlFilterCB>() {
     *     public void specify(UrlFilterCB cb) { <span style="color: #3F7E5E">// all columns are updated</span>
     *         cb.specify().<span style="color: #DD4747">columnEveryColumn()</span>; <span style="color: #3F7E5E">// no check of modified properties</span>
     *     }
     * });
     * </pre>
     * <p>You can specify update columns used on set clause of update statement.
     * However you do not need to specify common columns for update
     * and an optimistic lock column because they are specified implicitly.</p>
     * <p>And you should specify columns that are modified in any entities (at least one entity).
     * But if you specify every column, it has no check.</p>
     * @param urlFilterList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param updateColumnSpec The specification of update columns. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchUpdate(List<UrlFilter> urlFilterList, SpecifyQuery<UrlFilterCB> updateColumnSpec) {
        return doBatchUpdate(urlFilterList, createSpecifiedUpdateOption(updateColumnSpec));
    }

    @Override
    protected int[] doLumpModifyNonstrict(List<Entity> ls, UpdateOption<? extends ConditionBean> op) {
        return doLumpModify(ls, op);
    }

    /**
     * Batch-delete the entity list. (NonExclusiveControl) <br />
     * This method uses executeBatch() of java.sql.PreparedStatement.
     * @param urlFilterList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     */
    public int[] batchDelete(List<UrlFilter> urlFilterList) {
        return doBatchDelete(urlFilterList, null);
    }

    protected int[] doBatchDelete(List<UrlFilter> urlFilterList, DeleteOption<UrlFilterCB> op) {
        assertObjectNotNull("urlFilterList", urlFilterList);
        prepareDeleteOption(op);
        return delegateBatchDelete(urlFilterList, op);
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
     * urlFilterBhv.<span style="color: #DD4747">queryInsert</span>(new QueryInsertSetupper&lt;UrlFilter, UrlFilterCB&gt;() {
     *     public ConditionBean setup(urlFilter entity, UrlFilterCB intoCB) {
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
    public int queryInsert(QueryInsertSetupper<UrlFilter, UrlFilterCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(QueryInsertSetupper<UrlFilter, UrlFilterCB> sp, InsertOption<UrlFilterCB> op) {
        assertObjectNotNull("setupper", sp);
        prepareInsertOption(op);
        UrlFilter e = new UrlFilter();
        UrlFilterCB cb = createCBForQueryInsert();
        return delegateQueryInsert(e, cb, sp.setup(e, cb), op);
    }

    protected UrlFilterCB createCBForQueryInsert() {
        UrlFilterCB cb = newMyConditionBean();
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
     * UrlFilter urlFilter = new UrlFilter();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//urlFilter.setPK...(value);</span>
     * urlFilter.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//urlFilter.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//urlFilter.set...;</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//urlFilter.setVersionNo(value);</span>
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * urlFilterBhv.<span style="color: #DD4747">queryUpdate</span>(urlFilter, cb);
     * </pre>
     * @param urlFilter The entity that contains update values. (NotNull, PrimaryKeyNullAllowed)
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition.
     */
    public int queryUpdate(UrlFilter urlFilter, UrlFilterCB cb) {
        return doQueryUpdate(urlFilter, cb, null);
    }

    protected int doQueryUpdate(UrlFilter urlFilter, UrlFilterCB cb, UpdateOption<UrlFilterCB> op) {
        assertObjectNotNull("urlFilter", urlFilter); assertCBStateValid(cb);
        prepareUpdateOption(op);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryUpdate(urlFilter, cb, op) : 0;
    }

    @Override
    protected int doRangeModify(Entity et, ConditionBean cb, UpdateOption<? extends ConditionBean> op) {
        if (op == null) { return queryUpdate(downcast(et), (UrlFilterCB)cb); }
        else { return varyingQueryUpdate(downcast(et), (UrlFilterCB)cb, downcast(op)); }
    }

    /**
     * Delete the several entities by query. (NonExclusiveControl)
     * <pre>
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * urlFilterBhv.<span style="color: #DD4747">queryDelete</span>(urlFilter, cb);
     * </pre>
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition.
     */
    public int queryDelete(UrlFilterCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(UrlFilterCB cb, DeleteOption<UrlFilterCB> op) {
        assertCBStateValid(cb);
        prepareDeleteOption(op);
        return checkCountBeforeQueryUpdateIfNeeds(cb) ? delegateQueryDelete(cb, op) : 0;
    }

    @Override
    protected int doRangeRemove(ConditionBean cb, DeleteOption<? extends ConditionBean> op) {
        if (op == null) { return queryDelete((UrlFilterCB)cb); }
        else { return varyingQueryDelete((UrlFilterCB)cb, downcast(op)); }
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
     * UrlFilter urlFilter = new UrlFilter();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * urlFilter.setFoo...(value);
     * urlFilter.setBar...(value);
     * InsertOption<UrlFilterCB> option = new InsertOption<UrlFilterCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * urlFilterBhv.<span style="color: #DD4747">varyingInsert</span>(urlFilter, option);
     * ... = urlFilter.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * @param urlFilter The entity of insert target. (NotNull, PrimaryKeyNullAllowed: when auto-increment)
     * @param option The option of insert for varying requests. (NotNull)
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsert(UrlFilter urlFilter, InsertOption<UrlFilterCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(urlFilter, option);
    }

    /**
     * Update the entity with varying requests modified-only. (ZeroUpdateException, NonExclusiveControl) <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * <pre>
     * UrlFilter urlFilter = new UrlFilter();
     * urlFilter.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * urlFilter.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * urlFilter.<span style="color: #DD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;UrlFilterCB&gt; option = new UpdateOption&lt;UrlFilterCB&gt;();
     *     option.self(new SpecifyQuery&lt;UrlFilterCB&gt;() {
     *         public void specify(UrlFilterCB cb) {
     *             cb.specify().<span style="color: #DD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     urlFilterBhv.<span style="color: #DD4747">varyingUpdate</span>(urlFilter, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * @param urlFilter The entity of update target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingUpdate(UrlFilter urlFilter, UpdateOption<UrlFilterCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(urlFilter, option);
    }

    /**
     * Insert or update the entity with varying requests. (ExclusiveControl: when update) <br />
     * Other specifications are same as insertOrUpdate(entity).
     * @param urlFilter The entity of insert or update target. (NotNull)
     * @param insertOption The option of insert for varying requests. (NotNull)
     * @param updateOption The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     * @exception EntityAlreadyExistsException When the entity already exists. (unique constraint violation)
     */
    public void varyingInsertOrUpdate(UrlFilter urlFilter, InsertOption<UrlFilterCB> insertOption, UpdateOption<UrlFilterCB> updateOption) {
        assertInsertOptionNotNull(insertOption); assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(urlFilter, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. (ZeroUpdateException, NonExclusiveControl) <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * @param urlFilter The entity of delete target. (NotNull, PrimaryKeyNotNull, ConcurrencyColumnRequired)
     * @param option The option of update for varying requests. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity has already been deleted. (not found)
     * @exception EntityDuplicatedException When the entity has been duplicated.
     */
    public void varyingDelete(UrlFilter urlFilter, DeleteOption<UrlFilterCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(urlFilter, option);
    }

    // -----------------------------------------------------
    //                                          Batch Update
    //                                          ------------
    /**
     * Batch-insert the list with varying requests. <br />
     * For example, disableCommonColumnAutoSetup()
     * , disablePrimaryKeyIdentity(), limitBatchInsertLogging(). <br />
     * Other specifications are same as batchInsert(entityList).
     * @param urlFilterList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of insert for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchInsert(List<UrlFilter> urlFilterList, InsertOption<UrlFilterCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(urlFilterList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification), specify(updateColumnSpecification)
     * , disableCommonColumnAutoSetup(), limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * @param urlFilterList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The array of updated count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchUpdate(List<UrlFilter> urlFilterList, UpdateOption<UrlFilterCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(urlFilterList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * @param urlFilterList The list of the entity. (NotNull, EmptyAllowed, PrimaryKeyNotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The array of deleted count. (NotNull, EmptyAllowed)
     */
    public int[] varyingBatchDelete(List<UrlFilter> urlFilterList, DeleteOption<UrlFilterCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(urlFilterList, option);
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
    public int varyingQueryInsert(QueryInsertSetupper<UrlFilter, UrlFilterCB> setupper, InsertOption<UrlFilterCB> option) {
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
     * UrlFilter urlFilter = new UrlFilter();
     * <span style="color: #3F7E5E">// you don't need to set PK value</span>
     * <span style="color: #3F7E5E">//urlFilter.setPK...(value);</span>
     * urlFilter.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set a value of exclusive control column</span>
     * <span style="color: #3F7E5E">// (auto-increment for version number is valid though non-exclusive control)</span>
     * <span style="color: #3F7E5E">//urlFilter.setVersionNo(value);</span>
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * UpdateOption&lt;UrlFilterCB&gt; option = new UpdateOption&lt;UrlFilterCB&gt;();
     * option.self(new SpecifyQuery&lt;UrlFilterCB&gt;() {
     *     public void specify(UrlFilterCB cb) {
     *         cb.specify().<span style="color: #DD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * urlFilterBhv.<span style="color: #DD4747">varyingQueryUpdate</span>(urlFilter, cb, option);
     * </pre>
     * @param urlFilter The entity that contains update values. (NotNull) {PrimaryKeyNotRequired}
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @param option The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception NonQueryUpdateNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(UrlFilter urlFilter, UrlFilterCB cb, UpdateOption<UrlFilterCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(urlFilter, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * @param cb The condition-bean of UrlFilter. (NotNull)
     * @param option The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception NonQueryDeleteNotAllowedException When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(UrlFilterCB cb, DeleteOption<UrlFilterCB> option) {
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
    public OutsideSqlBasicExecutor<UrlFilterBhv> outsideSql() {
        return doOutsideSql();
    }

    // ===================================================================================
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCountUniquely(UrlFilterCB cb) { return invoke(createSelectCountCBCommand(cb, true)); }
    protected int delegateSelectCountPlainly(UrlFilterCB cb) { return invoke(createSelectCountCBCommand(cb, false)); }
    protected <ENTITY extends UrlFilter> void delegateSelectCursor(UrlFilterCB cb, EntityRowHandler<ENTITY> rh, Class<ENTITY> tp)
    { invoke(createSelectCursorCBCommand(cb, rh, tp)); }
    protected <ENTITY extends UrlFilter> List<ENTITY> delegateSelectList(UrlFilterCB cb, Class<ENTITY> tp)
    { return invoke(createSelectListCBCommand(cb, tp)); }
    protected <RESULT> RESULT delegateSelectNextVal(Class<RESULT> tp) { return invoke(createSelectNextValCommand(tp)); }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(UrlFilter et, InsertOption<UrlFilterCB> op)
    { if (!processBeforeInsert(et, op)) { return 0; }
      return invoke(createInsertEntityCommand(et, op)); }
    protected int delegateUpdate(UrlFilter et, UpdateOption<UrlFilterCB> op)
    { if (!processBeforeUpdate(et, op)) { return 0; }
      return delegateUpdateNonstrict(et, op); }
    protected int delegateUpdateNonstrict(UrlFilter et, UpdateOption<UrlFilterCB> op)
    { if (!processBeforeUpdate(et, op)) { return 0; }
      return invoke(createUpdateNonstrictEntityCommand(et, op)); }
    protected int delegateDelete(UrlFilter et, DeleteOption<UrlFilterCB> op)
    { if (!processBeforeDelete(et, op)) { return 0; }
      return delegateDeleteNonstrict(et, op); }
    protected int delegateDeleteNonstrict(UrlFilter et, DeleteOption<UrlFilterCB> op)
    { if (!processBeforeDelete(et, op)) { return 0; }
      return invoke(createDeleteNonstrictEntityCommand(et, op)); }

    protected int[] delegateBatchInsert(List<UrlFilter> ls, InsertOption<UrlFilterCB> op)
    { if (ls.isEmpty()) { return new int[]{}; }
      return invoke(createBatchInsertCommand(processBatchInternally(ls, op), op)); }
    protected int[] delegateBatchUpdate(List<UrlFilter> ls, UpdateOption<UrlFilterCB> op)
    { if (ls.isEmpty()) { return new int[]{}; }
      return delegateBatchUpdateNonstrict(ls, op); }
    protected int[] delegateBatchUpdateNonstrict(List<UrlFilter> ls, UpdateOption<UrlFilterCB> op)
    { if (ls.isEmpty()) { return new int[]{}; }
      return invoke(createBatchUpdateNonstrictCommand(processBatchInternally(ls, op, true), op)); }
    protected int[] delegateBatchDelete(List<UrlFilter> ls, DeleteOption<UrlFilterCB> op)
    { if (ls.isEmpty()) { return new int[]{}; }
      return delegateBatchDeleteNonstrict(ls, op); }
    protected int[] delegateBatchDeleteNonstrict(List<UrlFilter> ls, DeleteOption<UrlFilterCB> op)
    { if (ls.isEmpty()) { return new int[]{}; }
      return invoke(createBatchDeleteNonstrictCommand(processBatchInternally(ls, op, true), op)); }

    protected int delegateQueryInsert(UrlFilter et, UrlFilterCB inCB, ConditionBean resCB, InsertOption<UrlFilterCB> op)
    { if (!processBeforeQueryInsert(et, inCB, resCB, op)) { return 0; } return invoke(createQueryInsertCBCommand(et, inCB, resCB, op));  }
    protected int delegateQueryUpdate(UrlFilter et, UrlFilterCB cb, UpdateOption<UrlFilterCB> op)
    { if (!processBeforeQueryUpdate(et, cb, op)) { return 0; } return invoke(createQueryUpdateCBCommand(et, cb, op));  }
    protected int delegateQueryDelete(UrlFilterCB cb, DeleteOption<UrlFilterCB> op)
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
    protected UrlFilter downcast(Entity et) {
        return helpEntityDowncastInternally(et, UrlFilter.class);
    }

    protected UrlFilterCB downcast(ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb, UrlFilterCB.class);
    }

    @SuppressWarnings("unchecked")
    protected List<UrlFilter> downcast(List<? extends Entity> ls) {
        return (List<UrlFilter>)ls;
    }

    @SuppressWarnings("unchecked")
    protected InsertOption<UrlFilterCB> downcast(InsertOption<? extends ConditionBean> op) {
        return (InsertOption<UrlFilterCB>)op;
    }

    @SuppressWarnings("unchecked")
    protected UpdateOption<UrlFilterCB> downcast(UpdateOption<? extends ConditionBean> op) {
        return (UpdateOption<UrlFilterCB>)op;
    }

    @SuppressWarnings("unchecked")
    protected DeleteOption<UrlFilterCB> downcast(DeleteOption<? extends ConditionBean> op) {
        return (DeleteOption<UrlFilterCB>)op;
    }

    @SuppressWarnings("unchecked")
    protected QueryInsertSetupper<UrlFilter, UrlFilterCB> downcast(QueryInsertSetupper<? extends Entity, ? extends ConditionBean> sp) {
        return (QueryInsertSetupper<UrlFilter, UrlFilterCB>)sp;
    }
}
