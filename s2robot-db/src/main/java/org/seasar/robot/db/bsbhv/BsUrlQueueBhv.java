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
import org.seasar.robot.db.bsentity.dbmeta.UrlQueueDbm;
import org.seasar.robot.db.cbean.UrlQueueCB;
import org.seasar.robot.db.exentity.UrlQueue;

/**
 * The behavior of URL_QUEUE that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, SESSION_ID, METHOD, URL, PARENT_URL, DEPTH, CREATE_TIME
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
public abstract class BsUrlQueueBhv extends
        org.seasar.dbflute.bhv.AbstractBehaviorWritable {

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
        return "URL_QUEUE";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() {
        return UrlQueueDbm.getInstance();
    }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public UrlQueueDbm getMyDBMeta() {
        return UrlQueueDbm.getInstance();
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

    public UrlQueue newMyEntity() {
        return new UrlQueue();
    }

    public UrlQueueCB newMyConditionBean() {
        return new UrlQueueCB();
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
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @return The selected count.
     */
    public int selectCount(UrlQueueCB cb) {
        assertCBNotNull(cb);
        return delegateSelectCount(cb);
    }

    // ===================================================================================
    //                                                                       Cursor Select
    //                                                                       =============
    /**
     * Select the cursor by the condition-bean. <br />
     * Attention: It has a mapping cost from result set to entity.
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @param entityRowHandler The handler of entity row of UrlQueue. (NotNull)
     */
    public void selectCursor(UrlQueueCB cb,
            EntityRowHandler<UrlQueue> entityRowHandler) {
        assertCBNotNull(cb);
        assertObjectNotNull("entityRowHandler<UrlQueue>", entityRowHandler);
        delegateSelectCursor(cb, entityRowHandler);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @return The selected entity. (Nullalble)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public UrlQueue selectEntity(final UrlQueueCB cb) {
        return helpSelectEntityInternally(cb,
                new InternalSelectEntityCallback<UrlQueue, UrlQueueCB>() {
                    public List<UrlQueue> callbackSelectList(UrlQueueCB cb) {
                        return selectList(cb);
                    }
                });
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public UrlQueue selectEntityWithDeletedCheck(final UrlQueueCB cb) {
        return helpSelectEntityWithDeletedCheckInternally(
                cb,
                new InternalSelectEntityWithDeletedCheckCallback<UrlQueue, UrlQueueCB>() {
                    public List<UrlQueue> callbackSelectList(UrlQueueCB cb) {
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
    public UrlQueue selectByPKValueWithDeletedCheck(Long id) {
        UrlQueue entity = new UrlQueue();
        entity.setId(id);
        final UrlQueueCB cb = newMyConditionBean();
        cb.acceptPrimaryKeyMapString(getDBMeta().extractPrimaryKeyMapString(
                entity));
        return selectEntityWithDeletedCheck(cb);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @return The result bean of selected list. (NotNull)
     */
    public ListResultBean<UrlQueue> selectList(UrlQueueCB cb) {
        assertCBNotNull(cb);
        return new ResultBeanBuilder<UrlQueue>(getTableDbName())
                .buildListResultBean(cb, delegateSelectList(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean.
     * @param cb The condition-bean of UrlQueue. (NotNull)
     * @return The result bean of selected page. (NotNull)
     */
    public PagingResultBean<UrlQueue> selectPage(final UrlQueueCB cb) {
        assertCBNotNull(cb);
        final PagingInvoker<UrlQueue> invoker = new PagingInvoker<UrlQueue>(
                getTableDbName());
        final PagingHandler<UrlQueue> handler = new PagingHandler<UrlQueue>() {
            public PagingBean getPagingBean() {
                return cb;
            }

            public int count() {
                return selectCount(cb);
            }

            public List<UrlQueue> paging() {
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
     * urlQueueBhv.scalarSelect(Date.class).max(new ScalarQuery(UrlQueueCB cb) {
     *     cb.specify().columnXxxDatetime(); // the required specification of target column
     *     cb.query().setXxxName_PrefixSearch("S"); // query as you like it
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (Nullable)
     */
    public <RESULT> SLFunction<UrlQueueCB, RESULT> scalarSelect(
            Class<RESULT> resultType) {
        UrlQueueCB cb = newMyConditionBean();
        cb.xsetupForScalarSelect();
        cb.getSqlClause().disableSelectIndex(); // for when you use union
        return new SLFunction<UrlQueueCB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity.
     * @param urlQueue The entity of insert target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(UrlQueue urlQueue) {
        assertEntityNotNull(urlQueue);
        delegateInsert(urlQueue);
    }

    @Override
    protected void doCreate(Entity urlQueue) {
        insert((UrlQueue) urlQueue);
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param urlQueue The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final UrlQueue urlQueue) {
        helpUpdateInternally(urlQueue, new InternalUpdateCallback<UrlQueue>() {
            public int callbackDelegateUpdate(UrlQueue entity) {
                return delegateUpdate(entity);
            }
        });
    }

    @Override
    protected void doModify(Entity entity) {
        update((UrlQueue) entity);
    }

    @Override
    protected void doModifyNonstrict(Entity entity) {
        update((UrlQueue) entity);
    }

    /**
     * Insert or update the entity modified-only. {ConcurrencyControl(when update)}
     * @param urlQueue The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(final UrlQueue urlQueue) {
        helpInsertOrUpdateInternally(urlQueue,
                new InternalInsertOrUpdateCallback<UrlQueue, UrlQueueCB>() {
                    public void callbackInsert(UrlQueue entity) {
                        insert(entity);
                    }

                    public void callbackUpdate(UrlQueue entity) {
                        update(entity);
                    }

                    public UrlQueueCB callbackNewMyConditionBean() {
                        return newMyConditionBean();
                    }

                    public int callbackSelectCount(UrlQueueCB cb) {
                        return selectCount(cb);
                    }
                });
    }

    @Override
    protected void doCreateOrUpdate(Entity urlQueue) {
        insertOrUpdate((UrlQueue) urlQueue);
    }

    @Override
    protected void doCreateOrUpdateNonstrict(Entity entity) {
        insertOrUpdate((UrlQueue) entity);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ConcurrencyControl}
     * @param urlQueue The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(UrlQueue urlQueue) {
        helpDeleteInternally(urlQueue, new InternalDeleteCallback<UrlQueue>() {
            public int callbackDelegateDelete(UrlQueue entity) {
                return delegateDelete(entity);
            }
        });
    }

    @Override
    protected void doRemove(Entity urlQueue) {
        delete((UrlQueue) urlQueue);
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch insert the list. This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param urlQueueList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<UrlQueue> urlQueueList) {
        assertObjectNotNull("urlQueueList", urlQueueList);
        return delegateInsertList(urlQueueList);
    }

    /**
     * Batch update the list. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param urlQueueList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdate(List<UrlQueue> urlQueueList) {
        assertObjectNotNull("urlQueueList", urlQueueList);
        return delegateUpdateList(urlQueueList);
    }

    /**
     * Batch delete the list. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param urlQueueList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDelete(List<UrlQueue> urlQueueList) {
        assertObjectNotNull("urlQueueList", urlQueueList);
        return delegateDeleteList(urlQueueList);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Query update the several entities. {NoConcurrencyControl}
     * @param urlQueue Entity. (NotNull) {PrimaryKeyNotRequired}
     * @param cb Condition-bean. (NotNull)
     * @return The updated count.
     */
    public int queryUpdate(UrlQueue urlQueue, UrlQueueCB cb) {
        assertObjectNotNull("urlQueue", urlQueue);
        assertCBNotNull(cb);
        setupCommonColumnOfUpdateIfNeeds(urlQueue);
        filterEntityOfUpdate(urlQueue);
        assertEntityOfUpdate(urlQueue);
        return invoke(createQueryUpdateEntityCBCommand(urlQueue, cb));
    }

    /**
     * Query delete the several entities. {NoConcurrencyControl}
     * @param cb Condition-bean. (NotNull)
     * @return The deleted count.
     */
    public int queryDelete(UrlQueueCB cb) {
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
    protected int delegateSelectCount(UrlQueueCB cb) {
        return invoke(createSelectCountCBCommand(cb));
    }

    protected void delegateSelectCursor(UrlQueueCB cb,
            EntityRowHandler<UrlQueue> entityRowHandler) {
        invoke(createSelectCursorCBCommand(cb, entityRowHandler, UrlQueue.class));
    }

    protected int doCallReadCount(ConditionBean cb) {
        return delegateSelectCount((UrlQueueCB) cb);
    }

    protected List<UrlQueue> delegateSelectList(UrlQueueCB cb) {
        return invoke(createSelectListCBCommand(cb, UrlQueue.class));
    }

    @SuppressWarnings("unchecked")
    protected List<Entity> doCallReadList(ConditionBean cb) {
        return (List) delegateSelectList((UrlQueueCB) cb);
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(UrlQueue e) {
        if (!processBeforeInsert(e)) {
            return 1;
        }
        return invoke(createInsertEntityCommand(e));
    }

    protected int doCallCreate(Entity entity) {
        return delegateInsert(downcast(entity));
    }

    protected int delegateUpdate(UrlQueue e) {
        if (!processBeforeUpdate(e)) {
            return 1;
        }
        return invoke(createUpdateEntityCommand(e));
    }

    protected int doCallModify(Entity entity) {
        return delegateUpdate(downcast(entity));
    }

    protected int delegateDelete(UrlQueue e) {
        if (!processBeforeDelete(e)) {
            return 1;
        }
        return invoke(createDeleteEntityCommand(e));
    }

    protected int doCallRemove(Entity entity) {
        return delegateDelete(downcast(entity));
    }

    protected int[] delegateInsertList(List<UrlQueue> ls) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchInsertEntityCommand(helpFilterBeforeInsertInternally(ls)));
    }

    @SuppressWarnings("unchecked")
    protected int[] doCreateList(List<Entity> ls) {
        return delegateInsertList((List) ls);
    }

    protected int[] delegateUpdateList(List<UrlQueue> ls) {
        if (ls.isEmpty()) {
            return new int[] {};
        }
        return invoke(createBatchUpdateEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }

    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) {
        return delegateUpdateList((List) ls);
    }

    protected int[] delegateDeleteList(List<UrlQueue> ls) {
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
    protected UrlQueue downcast(Entity entity) {
        return helpDowncastInternally(entity, UrlQueue.class);
    }
}
