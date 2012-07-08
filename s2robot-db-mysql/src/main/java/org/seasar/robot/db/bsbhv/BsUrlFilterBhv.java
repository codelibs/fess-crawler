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

import org.seasar.robot.db.bsentity.dbmeta.UrlFilterDbm;
import org.seasar.robot.db.cbean.UrlFilterCB;
import org.seasar.robot.db.exentity.UrlFilter;
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
 * The behavior of URL_FILTER as TABLE. <br />
 * 
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
 * 
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsUrlFilterBhv extends AbstractBehaviorWritable {

    // ===================================================================================
    // Definition
    // ==========
    /* df:BehaviorQueryPathBegin */
    /* df:BehaviorQueryPathEnd */

    // ===================================================================================
    // Table name
    // ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() {
        return "URL_FILTER";
    }

    // ===================================================================================
    // DBMeta
    // ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() {
        return UrlFilterDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public UrlFilterDbm getMyDBMeta() {
        return UrlFilterDbm.getInstance();
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
    public UrlFilter newMyEntity() {
        return new UrlFilter();
    }

    /** @return The instance of new condition-bean as my table type. (NotNull) */
    public UrlFilterCB newMyConditionBean() {
        return new UrlFilterCB();
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
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * int count = urlFilterBhv.<span style="color: #FD4747">selectCount</span>(cb);
     * </pre>
     * 
     * @param cb
     *            The condition-bean of UrlFilter. (NotNull)
     * @return The selected count.
     */
    public int selectCount(final UrlFilterCB cb) {
        return doSelectCountUniquely(cb);
    }

    protected int doSelectCountUniquely(final UrlFilterCB cb) { // called by
        // selectCount(cb)
        assertCBNotNull(cb);
        return delegateSelectCountUniquely(cb);
    }

    protected int doSelectCountPlainly(final UrlFilterCB cb) { // called by
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
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * urlFilterBhv.<span style="color: #FD4747">selectCursor</span>(cb, new EntityRowHandler&lt;UrlFilter&gt;() {
     *     public void handle(UrlFilter entity) {
     *         ... = entity.getFoo...();
     *     }
     * });
     * </pre>
     * 
     * @param cb
     *            The condition-bean of UrlFilter. (NotNull)
     * @param entityRowHandler
     *            The handler of entity row of UrlFilter. (NotNull)
     */
    public void selectCursor(final UrlFilterCB cb,
            final EntityRowHandler<UrlFilter> entityRowHandler) {
        doSelectCursor(cb, entityRowHandler, UrlFilter.class);
    }

    protected <ENTITY extends UrlFilter> void doSelectCursor(
            final UrlFilterCB cb,
            final EntityRowHandler<ENTITY> entityRowHandler,
            final Class<ENTITY> entityType) {
        assertCBNotNull(cb);
        assertObjectNotNull("entityRowHandler<UrlFilter>", entityRowHandler);
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
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * UrlFilter urlFilter = urlFilterBhv.<span style="color: #FD4747">selectEntity</span>(cb);
     * if (urlFilter != null) {
     *     ... = urlFilter.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * 
     * @param cb
     *            The condition-bean of UrlFilter. (NotNull)
     * @return The selected entity. (NullAllowed: If the condition has no data,
     *         it returns null)
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.SelectEntityConditionNotFoundException
     *                When the condition for selecting an entity is not found.
     */
    public UrlFilter selectEntity(final UrlFilterCB cb) {
        return doSelectEntity(cb, UrlFilter.class);
    }

    protected <ENTITY extends UrlFilter> ENTITY doSelectEntity(
            final UrlFilterCB cb, final Class<ENTITY> entityType) {
        return helpSelectEntityInternally(
            cb,
            new InternalSelectEntityCallback<ENTITY, UrlFilterCB>() {
                public List<ENTITY> callbackSelectList(final UrlFilterCB cb) {
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
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * UrlFilter urlFilter = urlFilterBhv.<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(cb);
     * ... = urlFilter.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * 
     * @param cb
     *            The condition-bean of UrlFilter. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.SelectEntityConditionNotFoundException
     *                When the condition for selecting an entity is not found.
     */
    public UrlFilter selectEntityWithDeletedCheck(final UrlFilterCB cb) {
        return doSelectEntityWithDeletedCheck(cb, UrlFilter.class);
    }

    protected <ENTITY extends UrlFilter> ENTITY doSelectEntityWithDeletedCheck(
            final UrlFilterCB cb, final Class<ENTITY> entityType) {
        return helpSelectEntityWithDeletedCheckInternally(
            cb,
            new InternalSelectEntityWithDeletedCheckCallback<ENTITY, UrlFilterCB>() {
                public List<ENTITY> callbackSelectList(final UrlFilterCB cb) {
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
    public UrlFilter selectByPKValue(final Long id) {
        return doSelectByPKValue(id, UrlFilter.class);
    }

    protected <ENTITY extends UrlFilter> ENTITY doSelectByPKValue(
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
    public UrlFilter selectByPKValueWithDeletedCheck(final Long id) {
        return doSelectByPKValueWithDeletedCheck(id, UrlFilter.class);
    }

    protected <ENTITY extends UrlFilter> ENTITY doSelectByPKValueWithDeletedCheck(
            final Long id, final Class<ENTITY> entityType) {
        return doSelectEntityWithDeletedCheck(buildPKCB(id), entityType);
    }

    private UrlFilterCB buildPKCB(final Long id) {
        assertObjectNotNull("id", id);
        final UrlFilterCB cb = newMyConditionBean();
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
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * ListResultBean&lt;UrlFilter&gt; urlFilterList = urlFilterBhv.<span style="color: #FD4747">selectList</span>(cb);
     * for (UrlFilter urlFilter : urlFilterList) {
     *     ... = urlFilter.get...();
     * }
     * </pre>
     * 
     * @param cb
     *            The condition-bean of UrlFilter. (NotNull)
     * @return The result bean of selected list. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.DangerousResultSizeException
     *                When the result size is over the specified safety size.
     */
    public ListResultBean<UrlFilter> selectList(final UrlFilterCB cb) {
        return doSelectList(cb, UrlFilter.class);
    }

    protected <ENTITY extends UrlFilter> ListResultBean<ENTITY> doSelectList(
            final UrlFilterCB cb, final Class<ENTITY> entityType) {
        assertCBNotNull(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
        return helpSelectListInternally(
            cb,
            entityType,
            new InternalSelectListCallback<ENTITY, UrlFilterCB>() {
                public List<ENTITY> callbackSelectList(final UrlFilterCB cb,
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
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * cb.query().addOrderBy_Bar...();
     * cb.<span style="color: #FD4747">paging</span>(20, 3); <span style="color: #3F7E5E">// 20 records per a page and current page number is 3</span>
     * PagingResultBean&lt;UrlFilter&gt; page = urlFilterBhv.<span style="color: #FD4747">selectPage</span>(cb);
     * int allRecordCount = page.getAllRecordCount();
     * int allPageCount = page.getAllPageCount();
     * boolean isExistPrePage = page.isExistPrePage();
     * boolean isExistNextPage = page.isExistNextPage();
     * ...
     * for (UrlFilter urlFilter : page) {
     *     ... = urlFilter.get...();
     * }
     * </pre>
     * 
     * @param cb
     *            The condition-bean of UrlFilter. (NotNull)
     * @return The result bean of selected page. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.DangerousResultSizeException
     *                When the result size is over the specified safety size.
     */
    public PagingResultBean<UrlFilter> selectPage(final UrlFilterCB cb) {
        return doSelectPage(cb, UrlFilter.class);
    }

    protected <ENTITY extends UrlFilter> PagingResultBean<ENTITY> doSelectPage(
            final UrlFilterCB cb, final Class<ENTITY> entityType) {
        assertCBNotNull(cb);
        assertObjectNotNull("entityType", entityType);
        return helpSelectPageInternally(
            cb,
            entityType,
            new InternalSelectPageCallback<ENTITY, UrlFilterCB>() {
                public int callbackSelectCount(final UrlFilterCB cb) {
                    return doSelectCountPlainly(cb);
                }

                public List<ENTITY> callbackSelectList(final UrlFilterCB cb,
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
     * urlFilterBhv.<span style="color: #FD4747">scalarSelect</span>(Date.class).max(new ScalarQuery() {
     *     public void query(UrlFilterCB cb) {
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
    public <RESULT> SLFunction<UrlFilterCB, RESULT> scalarSelect(
            final Class<RESULT> resultType) {
        return doScalarSelect(resultType, newMyConditionBean());
    }

    protected <RESULT, CB extends UrlFilterCB> SLFunction<CB, RESULT> doScalarSelect(
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

    // ===================================================================================
    // Entity Update
    // =============
    /**
     * Insert the entity.
     * 
     * <pre>
     * UrlFilter urlFilter = new UrlFilter();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * urlFilter.setFoo...(value);
     * urlFilter.setBar...(value);
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//urlFilter.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//urlFilter.set...;</span>
     * urlFilterBhv.<span style="color: #FD4747">insert</span>(urlFilter);
     * ... = urlFilter.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * 
     * @param urlFilter
     *            The entity of insert target. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (Unique Constraint
     *                Violation)
     */
    public void insert(final UrlFilter urlFilter) {
        doInsert(urlFilter, null);
    }

    protected void doInsert(final UrlFilter urlFilter,
            final InsertOption<UrlFilterCB> option) {
        assertObjectNotNull("urlFilter", urlFilter);
        prepareInsertOption(option);
        delegateInsert(urlFilter, option);
    }

    protected void prepareInsertOption(final InsertOption<UrlFilterCB> option) {
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
     * UrlFilter urlFilter = new UrlFilter();
     * urlFilter.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * urlFilter.setFoo...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// you don't need to set values of common columns</span>
     * <span style="color: #3F7E5E">//urlFilter.setRegisterUser(value);</span>
     * <span style="color: #3F7E5E">//urlFilter.set...;</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * urlFilter.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     urlFilterBhv.<span style="color: #FD4747">update</span>(urlFilter);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * 
     * @param urlFilter
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
    public void update(final UrlFilter urlFilter) {
        doUpdate(urlFilter, null);
    }

    protected void doUpdate(final UrlFilter urlFilter,
            final UpdateOption<UrlFilterCB> option) {
        assertObjectNotNull("urlFilter", urlFilter);
        prepareUpdateOption(option);
        helpUpdateInternally(
            urlFilter,
            new InternalUpdateCallback<UrlFilter>() {
                public int callbackDelegateUpdate(final UrlFilter entity) {
                    return delegateUpdate(entity, option);
                }
            });
    }

    protected void prepareUpdateOption(final UpdateOption<UrlFilterCB> option) {
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

    protected UrlFilterCB createCBForVaryingUpdate() {
        final UrlFilterCB cb = newMyConditionBean();
        cb.xsetupForVaryingUpdate();
        return cb;
    }

    protected UrlFilterCB createCBForSpecifiedUpdate() {
        final UrlFilterCB cb = newMyConditionBean();
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
     * @param urlFilter
     *            The entity of insert or update target. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (Unique Constraint
     *                Violation)
     */
    public void insertOrUpdate(final UrlFilter urlFilter) {
        doInesrtOrUpdate(urlFilter, null, null);
    }

    protected void doInesrtOrUpdate(final UrlFilter urlFilter,
            final InsertOption<UrlFilterCB> insertOption,
            final UpdateOption<UrlFilterCB> updateOption) {
        helpInsertOrUpdateInternally(
            urlFilter,
            new InternalInsertOrUpdateCallback<UrlFilter, UrlFilterCB>() {
                public void callbackInsert(final UrlFilter entity) {
                    doInsert(entity, insertOption);
                }

                public void callbackUpdate(final UrlFilter entity) {
                    doUpdate(entity, updateOption);
                }

                public UrlFilterCB callbackNewMyConditionBean() {
                    return newMyConditionBean();
                }

                public int callbackSelectCount(final UrlFilterCB cb) {
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
     * UrlFilter urlFilter = new UrlFilter();
     * urlFilter.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * urlFilter.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     urlFilterBhv.<span style="color: #FD4747">delete</span>(urlFilter);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * } 
     * </pre>
     * 
     * @param urlFilter
     *            The entity of delete target. (NotNull) {PrimaryKeyRequired,
     *            ConcurrencyColumnRequired}
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     */
    public void delete(final UrlFilter urlFilter) {
        doDelete(urlFilter, null);
    }

    protected void doDelete(final UrlFilter urlFilter,
            final DeleteOption<UrlFilterCB> option) {
        assertObjectNotNull("urlFilter", urlFilter);
        prepareDeleteOption(option);
        helpDeleteInternally(
            urlFilter,
            new InternalDeleteCallback<UrlFilter>() {
                public int callbackDelegateDelete(final UrlFilter entity) {
                    return delegateDelete(entity, option);
                }
            });
    }

    protected void prepareDeleteOption(final DeleteOption<UrlFilterCB> option) {
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
     * @param urlFilterList
     *            The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(final List<UrlFilter> urlFilterList) {
        return doBatchInsert(urlFilterList, null);
    }

    protected int[] doBatchInsert(final List<UrlFilter> urlFilterList,
            final InsertOption<UrlFilterCB> option) {
        assertObjectNotNull("urlFilterList", urlFilterList);
        prepareInsertOption(option);
        return delegateBatchInsert(urlFilterList, option);
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
     * @param urlFilterList
     *            The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     */
    public int[] batchUpdate(final List<UrlFilter> urlFilterList) {
        return doBatchUpdate(urlFilterList, null);
    }

    protected int[] doBatchUpdate(final List<UrlFilter> urlFilterList,
            final UpdateOption<UrlFilterCB> option) {
        assertObjectNotNull("urlFilterList", urlFilterList);
        prepareUpdateOption(option);
        return delegateBatchUpdate(urlFilterList, option);
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
     * @param urlFilterList
     *            The list of the entity. (NotNull)
     * @param updateColumnSpec
     *            The specification of update columns. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     */
    public int[] batchUpdate(final List<UrlFilter> urlFilterList,
            final SpecifyQuery<UrlFilterCB> updateColumnSpec) {
        return doBatchUpdate(
            urlFilterList,
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
     * @param urlFilterList
     *            The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     */
    public int[] batchDelete(final List<UrlFilter> urlFilterList) {
        return doBatchDelete(urlFilterList, null);
    }

    protected int[] doBatchDelete(final List<UrlFilter> urlFilterList,
            final DeleteOption<UrlFilterCB> option) {
        assertObjectNotNull("urlFilterList", urlFilterList);
        prepareDeleteOption(option);
        return delegateBatchDelete(urlFilterList, option);
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
     * urlFilterBhv.<span style="color: #FD4747">queryInsert</span>(new QueryInsertSetupper&lt;urlFilter, UrlFilterCB&gt;() {
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
     * 
     * @param setupper
     *            The setup-per of query-insert. (NotNull)
     * @return The inserted count.
     */
    public int queryInsert(
            final QueryInsertSetupper<UrlFilter, UrlFilterCB> setupper) {
        return doQueryInsert(setupper, null);
    }

    protected int doQueryInsert(
            final QueryInsertSetupper<UrlFilter, UrlFilterCB> setupper,
            final InsertOption<UrlFilterCB> option) {
        assertObjectNotNull("setupper", setupper);
        prepareInsertOption(option);
        final UrlFilter entity = new UrlFilter();
        final UrlFilterCB intoCB = createCBForQueryInsert();
        final ConditionBean resourceCB = setupper.setup(entity, intoCB);
        return delegateQueryInsert(entity, intoCB, resourceCB, option);
    }

    protected UrlFilterCB createCBForQueryInsert() {
        final UrlFilterCB cb = newMyConditionBean();
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
     * urlFilterBhv.<span style="color: #FD4747">queryUpdate</span>(urlFilter, cb);
     * </pre>
     * 
     * @param urlFilter
     *            The entity that contains update values. (NotNull,
     *            PrimaryKeyNullAllowed)
     * @param cb
     *            The condition-bean of UrlFilter. (NotNull)
     * @return The updated count.
     * @exception org.seasar.robot.dbflute.exception.NonQueryUpdateNotAllowedException
     *                When the query has no condition.
     */
    public int queryUpdate(final UrlFilter urlFilter, final UrlFilterCB cb) {
        return doQueryUpdate(urlFilter, cb, null);
    }

    protected int doQueryUpdate(final UrlFilter urlFilter,
            final UrlFilterCB cb, final UpdateOption<UrlFilterCB> option) {
        assertObjectNotNull("urlFilter", urlFilter);
        assertCBNotNull(cb);
        prepareUpdateOption(option);
        return delegateQueryUpdate(urlFilter, cb, option);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int doRangeModify(final Entity entity, final ConditionBean cb,
            final UpdateOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryUpdate(downcast(entity), (UrlFilterCB) cb);
        } else {
            return varyingQueryUpdate(
                downcast(entity),
                (UrlFilterCB) cb,
                (UpdateOption) option);
        }
    }

    /**
     * Delete the several entities by query. {NonExclusiveControl}
     * 
     * <pre>
     * UrlFilterCB cb = new UrlFilterCB();
     * cb.query().setFoo...(value);
     * urlFilterBhv.<span style="color: #FD4747">queryDelete</span>(urlFilter, cb);
     * </pre>
     * 
     * @param cb
     *            The condition-bean of UrlFilter. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.robot.dbflute.exception.NonQueryDeleteNotAllowedException
     *                When the query has no condition.
     */
    public int queryDelete(final UrlFilterCB cb) {
        return doQueryDelete(cb, null);
    }

    protected int doQueryDelete(final UrlFilterCB cb,
            final DeleteOption<UrlFilterCB> option) {
        assertCBNotNull(cb);
        prepareDeleteOption(option);
        return delegateQueryDelete(cb, option);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int doRangeRemove(final ConditionBean cb,
            final DeleteOption<? extends ConditionBean> option) {
        if (option == null) {
            return queryDelete((UrlFilterCB) cb);
        } else {
            return varyingQueryDelete((UrlFilterCB) cb, (DeleteOption) option);
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
     * UrlFilter urlFilter = new UrlFilter();
     * <span style="color: #3F7E5E">// if auto-increment, you don't need to set the PK value</span>
     * urlFilter.setFoo...(value);
     * urlFilter.setBar...(value);
     * InsertOption<UrlFilterCB> option = new InsertOption<UrlFilterCB>();
     * <span style="color: #3F7E5E">// you can insert by your values for common columns</span>
     * option.disableCommonColumnAutoSetup();
     * urlFilterBhv.<span style="color: #FD4747">varyingInsert</span>(urlFilter, option);
     * ... = urlFilter.getPK...(); <span style="color: #3F7E5E">// if auto-increment, you can get the value after</span>
     * </pre>
     * 
     * @param urlFilter
     *            The entity of insert target. (NotNull)
     * @param option
     *            The option of insert for varying requests. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException
     *                When the entity already exists. (Unique Constraint
     *                Violation)
     */
    public void varyingInsert(final UrlFilter urlFilter,
            final InsertOption<UrlFilterCB> option) {
        assertInsertOptionNotNull(option);
        doInsert(urlFilter, option);
    }

    /**
     * Update the entity with varying requests modified-only.
     * {UpdateCountZeroException, ExclusiveControl} <br />
     * For example, self(selfCalculationSpecification),
     * specify(updateColumnSpecification), disableCommonColumnAutoSetup(). <br />
     * Other specifications are same as update(entity).
     * 
     * <pre>
     * UrlFilter urlFilter = new UrlFilter();
     * urlFilter.setPK...(value); <span style="color: #3F7E5E">// required</span>
     * urlFilter.setOther...(value); <span style="color: #3F7E5E">// you should set only modified columns</span>
     * <span style="color: #3F7E5E">// if exclusive control, the value of exclusive control column is required</span>
     * urlFilter.<span style="color: #FD4747">setVersionNo</span>(value);
     * try {
     *     <span style="color: #3F7E5E">// you can update by self calculation values</span>
     *     UpdateOption&lt;UrlFilterCB&gt; option = new UpdateOption&lt;UrlFilterCB&gt;();
     *     option.self(new SpecifyQuery&lt;UrlFilterCB&gt;() {
     *         public void specify(UrlFilterCB cb) {
     *             cb.specify().<span style="color: #FD4747">columnXxxCount()</span>;
     *         }
     *     }).plus(1); <span style="color: #3F7E5E">// XXX_COUNT = XXX_COUNT + 1</span>
     *     urlFilterBhv.<span style="color: #FD4747">varyingUpdate</span>(urlFilter, option);
     * } catch (EntityAlreadyUpdatedException e) { <span style="color: #3F7E5E">// if concurrent update</span>
     *     ...
     * }
     * </pre>
     * 
     * @param urlFilter
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
    public void varyingUpdate(final UrlFilter urlFilter,
            final UpdateOption<UrlFilterCB> option) {
        assertUpdateOptionNotNull(option);
        doUpdate(urlFilter, option);
    }

    /**
     * Insert or update the entity with varying requests. {ExclusiveControl(when
     * update)}<br />
     * Other specifications are same as insertOrUpdate(entity).
     * 
     * @param urlFilter
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
    public void varyingInsertOrUpdate(final UrlFilter urlFilter,
            final InsertOption<UrlFilterCB> insertOption,
            final UpdateOption<UrlFilterCB> updateOption) {
        assertInsertOptionNotNull(insertOption);
        assertUpdateOptionNotNull(updateOption);
        doInesrtOrUpdate(urlFilter, insertOption, updateOption);
    }

    /**
     * Delete the entity with varying requests. {UpdateCountZeroException,
     * ExclusiveControl} <br />
     * Now a valid option does not exist. <br />
     * Other specifications are same as delete(entity).
     * 
     * @param urlFilter
     *            The entity of delete target. (NotNull) {PrimaryKeyRequired,
     *            ConcurrencyColumnRequired}
     * @param option
     *            The option of update for varying requests. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     *                When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     *                When the entity has been duplicated.
     */
    public void varyingDelete(final UrlFilter urlFilter,
            final DeleteOption<UrlFilterCB> option) {
        assertDeleteOptionNotNull(option);
        doDelete(urlFilter, option);
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
     * @param urlFilterList
     *            The list of the entity. (NotNull)
     * @param option
     *            The option of insert for varying requests. (NotNull)
     * @return The array of inserted count.
     */
    public int[] varyingBatchInsert(final List<UrlFilter> urlFilterList,
            final InsertOption<UrlFilterCB> option) {
        assertInsertOptionNotNull(option);
        return doBatchInsert(urlFilterList, option);
    }

    /**
     * Batch-update the list with varying requests. <br />
     * For example, self(selfCalculationSpecification),
     * specify(updateColumnSpecification) , disableCommonColumnAutoSetup(),
     * limitBatchUpdateLogging(). <br />
     * Other specifications are same as batchUpdate(entityList).
     * 
     * @param urlFilterList
     *            The list of the entity. (NotNull)
     * @param option
     *            The option of update for varying requests. (NotNull)
     * @return The array of updated count.
     */
    public int[] varyingBatchUpdate(final List<UrlFilter> urlFilterList,
            final UpdateOption<UrlFilterCB> option) {
        assertUpdateOptionNotNull(option);
        return doBatchUpdate(urlFilterList, option);
    }

    /**
     * Batch-delete the list with varying requests. <br />
     * For example, limitBatchDeleteLogging(). <br />
     * Other specifications are same as batchDelete(entityList).
     * 
     * @param urlFilterList
     *            The list of the entity. (NotNull)
     * @param option
     *            The option of delete for varying requests. (NotNull)
     * @return The array of deleted count.
     */
    public int[] varyingBatchDelete(final List<UrlFilter> urlFilterList,
            final DeleteOption<UrlFilterCB> option) {
        assertDeleteOptionNotNull(option);
        return doBatchDelete(urlFilterList, option);
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
            final QueryInsertSetupper<UrlFilter, UrlFilterCB> setupper,
            final InsertOption<UrlFilterCB> option) {
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
     *         cb.specify().<span style="color: #FD4747">columnFooCount()</span>;
     *     }
     * }).plus(1); <span style="color: #3F7E5E">// FOO_COUNT = FOO_COUNT + 1</span>
     * urlFilterBhv.<span style="color: #FD4747">varyingQueryUpdate</span>(urlFilter, cb, option);
     * </pre>
     * 
     * @param urlFilter
     *            The entity that contains update values. (NotNull)
     *            {PrimaryKeyNotRequired}
     * @param cb
     *            The condition-bean of UrlFilter. (NotNull)
     * @param option
     *            The option of update for varying requests. (NotNull)
     * @return The updated count.
     * @exception org.seasar.robot.dbflute.exception.NonQueryUpdateNotAllowedException
     *                When the query has no condition (if not allowed).
     */
    public int varyingQueryUpdate(final UrlFilter urlFilter,
            final UrlFilterCB cb, final UpdateOption<UrlFilterCB> option) {
        assertUpdateOptionNotNull(option);
        return doQueryUpdate(urlFilter, cb, option);
    }

    /**
     * Delete the several entities by query with varying requests non-strictly. <br />
     * For example, allowNonQueryDelete(). <br />
     * Other specifications are same as batchUpdateNonstrict(entityList).
     * 
     * @param cb
     *            The condition-bean of UrlFilter. (NotNull)
     * @param option
     *            The option of delete for varying requests. (NotNull)
     * @return The deleted count.
     * @exception org.seasar.robot.dbflute.exception.NonQueryDeleteNotAllowedException
     *                When the query has no condition (if not allowed).
     */
    public int varyingQueryDelete(final UrlFilterCB cb,
            final DeleteOption<UrlFilterCB> option) {
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
    protected int delegateSelectCountUniquely(final UrlFilterCB cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(final UrlFilterCB cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends UrlFilter> void delegateSelectCursor(
            final UrlFilterCB cb, final EntityRowHandler<ENTITY> erh,
            final Class<ENTITY> et) {
        invoke(createSelectCursorCBCommand(cb, erh, et));
    }

    protected <ENTITY extends UrlFilter> List<ENTITY> delegateSelectList(
            final UrlFilterCB cb, final Class<ENTITY> et) {
        return invoke(createSelectListCBCommand(cb, et));
    }

    // -----------------------------------------------------
    // Update
    // ------
    protected int delegateInsert(final UrlFilter e,
            final InsertOption<UrlFilterCB> op) {
        if (!processBeforeInsert(e, op)) {
            return 0;
        }
        return invoke(createInsertEntityCommand(e, op));
    }

    protected int delegateUpdate(final UrlFilter e,
            final UpdateOption<UrlFilterCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return delegateUpdateNonstrict(e, op);
    }

    protected int delegateUpdateNonstrict(final UrlFilter e,
            final UpdateOption<UrlFilterCB> op) {
        if (!processBeforeUpdate(e, op)) {
            return 0;
        }
        return invoke(createUpdateNonstrictEntityCommand(e, op));
    }

    protected int delegateDelete(final UrlFilter e,
            final DeleteOption<UrlFilterCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return delegateDeleteNonstrict(e, op);
    }

    protected int delegateDeleteNonstrict(final UrlFilter e,
            final DeleteOption<UrlFilterCB> op) {
        if (!processBeforeDelete(e, op)) {
            return 0;
        }
        return invoke(createDeleteNonstrictEntityCommand(e, op));
    }

    protected int[] delegateBatchInsert(final List<UrlFilter> ls,
            final InsertOption<UrlFilterCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertCommand(
            processBatchInternally(ls, op),
            op));
    }

    protected int[] delegateBatchUpdate(final List<UrlFilter> ls,
            final UpdateOption<UrlFilterCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchUpdateNonstrict(ls, op);
    }

    protected int[] delegateBatchUpdateNonstrict(final List<UrlFilter> ls,
            final UpdateOption<UrlFilterCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateNonstrictCommand(
            processBatchInternally(ls, op, true),
            op));
    }

    protected int[] delegateBatchDelete(final List<UrlFilter> ls,
            final DeleteOption<UrlFilterCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return delegateBatchDeleteNonstrict(ls, op);
    }

    protected int[] delegateBatchDeleteNonstrict(final List<UrlFilter> ls,
            final DeleteOption<UrlFilterCB> op) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchDeleteNonstrictCommand(
            processBatchInternally(ls, op, true),
            op));
    }

    protected int delegateQueryInsert(final UrlFilter e,
            final UrlFilterCB inCB, final ConditionBean resCB,
            final InsertOption<UrlFilterCB> op) {
        if (!processBeforeQueryInsert(e, inCB, resCB, op)) {
            return 0;
        }
        return invoke(createQueryInsertCBCommand(e, inCB, resCB, op));
    }

    protected int delegateQueryUpdate(final UrlFilter e, final UrlFilterCB cb,
            final UpdateOption<UrlFilterCB> op) {
        if (!processBeforeQueryUpdate(e, cb, op)) {
            return 0;
        }
        return invoke(createQueryUpdateCBCommand(e, cb, op));
    }

    protected int delegateQueryDelete(final UrlFilterCB cb,
            final DeleteOption<UrlFilterCB> op) {
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
    protected UrlFilter downcast(final Entity entity) {
        return helpEntityDowncastInternally(entity, UrlFilter.class);
    }

    protected UrlFilterCB downcast(final ConditionBean cb) {
        return helpConditionBeanDowncastInternally(cb, UrlFilterCB.class);
    }
}
