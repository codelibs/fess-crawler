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
import org.seasar.robot.db.bsentity.dbmeta.AccessResultDbm;
import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.exentity.AccessResult;
import org.seasar.robot.db.exentity.AccessResultData;

/**
 * The behavior of ACCESS_RESULT that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, SESSION_ID, RULE_ID, URL, PARENT_URL, STATUS, HTTP_STATUS_CODE, METHOD, MIME_TYPE, CREATE_TIME
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
public abstract class BsAccessResultBhv extends
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
    public Entity newEntity() {
        return newMyEntity();
    }

    public ConditionBean newConditionBean() {
        return newMyConditionBean();
    }

    public AccessResult newMyEntity() {
        return new AccessResult();
    }

    public AccessResultCB newMyConditionBean() {
        return new AccessResultCB();
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
     * @param cb The condition-bean of AccessResult. (NotNull)
     * @return The selected count.
     */
    public int selectCount(AccessResultCB cb) {
        assertCBNotNull(cb);
        return delegateSelectCount(cb);
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
     * @return The selected entity. (Nullalble)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
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

    /**
     * Select the entity by the condition-bean with deleted check.
     * @param cb The condition-bean of AccessResult. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
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

    /* (non-javadoc)
     * Select the entity with deleted check. {by primary-key value}
     * @param primaryKey The keys of primary.
     * @return The selected entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public AccessResult selectByPKValueWithDeletedCheck(Long id) {
        AccessResult entity = new AccessResult();
        entity.setId(id);
        final AccessResultCB cb = newMyConditionBean();
        cb.acceptPrimaryKeyMapString(getDBMeta().extractPrimaryKeyMapString(
                entity));
        return selectEntityWithDeletedCheck(cb);
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
    //                                                                       Load Referrer
    //                                                                       =============
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
                    public AccessResultData callbackGetForeignEntity(
                            AccessResult entity) {
                        return entity.getAccessResultDataAsOne();
                    }
                });
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity.
     * @param accessResult The entity of insert target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(AccessResult accessResult) {
        assertEntityNotNull(accessResult);
        delegateInsert(accessResult);
    }

    @Override
    protected void doCreate(Entity accessResult) {
        insert((AccessResult) accessResult);
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param accessResult The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
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
        update((AccessResult) entity);
    }

    @Override
    protected void doModifyNonstrict(Entity entity) {
        update((AccessResult) entity);
    }

    /**
     * Insert or update the entity modified-only. {ConcurrencyControl(when update)}
     * @param accessResult The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
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
    protected void doCreateOrUpdate(Entity accessResult) {
        insertOrUpdate((AccessResult) accessResult);
    }

    @Override
    protected void doCreateOrUpdateNonstrict(Entity entity) {
        insertOrUpdate((AccessResult) entity);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ConcurrencyControl}
     * @param accessResult The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
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
    protected void doRemove(Entity accessResult) {
        delete((AccessResult) accessResult);
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
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
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
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
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
     * @param accessResult Entity. (NotNull) {PrimaryKeyNotRequired}
     * @param cb Condition-bean. (NotNull)
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
     * @param cb Condition-bean. (NotNull)
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

    protected int doCallReadCount(ConditionBean cb) {
        return delegateSelectCount((AccessResultCB) cb);
    }

    protected List<AccessResult> delegateSelectList(AccessResultCB cb) {
        return invoke(createSelectListCBCommand(cb, AccessResult.class));
    }

    @SuppressWarnings("unchecked")
    protected List<Entity> doCallReadList(ConditionBean cb) {
        return (List) delegateSelectList((AccessResultCB) cb);
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

    protected int doCallCreate(Entity entity) {
        return delegateInsert(downcast(entity));
    }

    protected int delegateUpdate(AccessResult e) {
        if (!processBeforeUpdate(e)) {
            return 1;
        }
        return invoke(createUpdateEntityCommand(e));
    }

    protected int doCallModify(Entity entity) {
        return delegateUpdate(downcast(entity));
    }

    protected int delegateDelete(AccessResult e) {
        if (!processBeforeDelete(e)) {
            return 1;
        }
        return invoke(createDeleteEntityCommand(e));
    }

    protected int doCallRemove(Entity entity) {
        return delegateDelete(downcast(entity));
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
        return invoke(createBatchUpdateEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }

    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) {
        return delegateUpdateList((List) ls);
    }

    protected int[] delegateDeleteList(List<AccessResult> ls) {
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
    protected AccessResult downcast(Entity entity) {
        return helpDowncastInternally(entity, AccessResult.class);
    }
}
