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
package org.seasar.robot.dbflute.bhv;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.robot.dbflute.BehaviorSelector;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.bhv.batch.TokenFileOutputOption;
import org.seasar.robot.dbflute.bhv.batch.TokenFileOutputResult;
import org.seasar.robot.dbflute.bhv.core.BehaviorCommand;
import org.seasar.robot.dbflute.bhv.core.BehaviorCommandInvoker;
import org.seasar.robot.dbflute.bhv.core.command.AbstractBehaviorCommand;
import org.seasar.robot.dbflute.bhv.core.command.SelectCountCBCommand;
import org.seasar.robot.dbflute.bhv.core.command.SelectCursorCBCommand;
import org.seasar.robot.dbflute.bhv.core.command.SelectListCBCommand;
import org.seasar.robot.dbflute.bhv.core.command.SelectNextValCommand;
import org.seasar.robot.dbflute.bhv.core.command.SelectScalarCBCommand;
import org.seasar.robot.dbflute.bhv.outsidesql.OutsideSqlBasicExecutor;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.cbean.EntityRowHandler;
import org.seasar.robot.dbflute.cbean.ListResultBean;
import org.seasar.robot.dbflute.cbean.PagingResultBean;
import org.seasar.robot.dbflute.cbean.ScalarQuery;
import org.seasar.robot.dbflute.cbean.UnionQuery;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.exception.DangerousResultSizeException;
import org.seasar.robot.dbflute.helper.token.file.FileMakingHeaderInfo;
import org.seasar.robot.dbflute.helper.token.file.FileMakingOption;
import org.seasar.robot.dbflute.helper.token.file.FileMakingSimpleFacade;
import org.seasar.robot.dbflute.helper.token.file.impl.FileMakingSimpleFacadeImpl;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * The abstract class of readable behavior.
 * @author jflute
 */
public abstract class AbstractBehaviorReadable implements BehaviorReadable {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Behavior-selector instance. It's basically referred at loadReferrer. (Required for loadReferrer) */
    protected BehaviorCommandInvoker _behaviorCommandInvoker;

    /** Behavior-selector instance. It's basically referred at loadReferrer. (Required for loadReferrer) */
    protected BehaviorSelector _behaviorSelector;

    // ===================================================================================
    //                                                                       Basic Get All
    //                                                                       =============
    /**
     * Get count all.
     * @return Count all.
     * @deprecated Sorry! Please use selectCount(emptyCB)
     */
    public int getCountAll() {
        return readCount(newConditionBean());
    }

    // ===================================================================================
    //                                                                          Count Read
    //                                                                          ==========
    /**
     * {@inheritDoc}
     */
    public int readCount(ConditionBean cb) {
        assertCBNotNull(cb);
        return doReadCount(cb);
    }

    protected abstract int doReadCount(ConditionBean cb);

    // ===================================================================================
    //                                                                         Entity Read 
    //                                                                         ===========
    /**
     * {@inheritDoc}
     */
    public Entity readEntity(ConditionBean cb) {
        assertCBNotNull(cb);
        return doReadEntity(cb);
    }

    protected abstract Entity doReadEntity(ConditionBean cb);

    /**
     * {@inheritDoc}
     */
    public Entity readEntityWithDeletedCheck(ConditionBean cb) {
        assertCBNotNull(cb);
        return doReadEntityWithDeletedCheck(cb);
    }

    protected abstract Entity doReadEntityWithDeletedCheck(ConditionBean cb);

    // -----------------------------------------------------
    //                                       Internal Helper
    //                                       ---------------
    protected <ENTITY extends Entity, CB extends ConditionBean> ENTITY helpSelectEntityInternally(CB cb,
            InternalSelectEntityCallback<ENTITY, CB> callback) {
        assertCBNotNull(cb);
        final int preSafetyMaxResultSize = xcheckSafetyResultAsOne(cb);
        final List<ENTITY> ls;
        try {
            ls = callback.callbackSelectList(cb);
        } catch (DangerousResultSizeException e) {
            throwEntityDuplicatedException("{over safetyMaxResultSize '1'}", cb, e);
            return null; // unreachable
        } finally {
            xrestoreSafetyResult(cb, preSafetyMaxResultSize);
        }
        if (ls.isEmpty()) {
            return null;
        }
        assertEntitySelectedAsOne(ls, cb);
        return (ENTITY) ls.get(0);
    }

    protected static interface InternalSelectEntityCallback<ENTITY extends Entity, CB extends ConditionBean> {
        public List<ENTITY> callbackSelectList(CB cb);
    }

    protected <ENTITY extends Entity, CB extends ConditionBean> ENTITY helpSelectEntityWithDeletedCheckInternally(
            CB cb, final InternalSelectEntityWithDeletedCheckCallback<ENTITY, CB> callback) {
        assertCBNotNull(cb);
        final ENTITY entity = helpSelectEntityInternally(cb, new InternalSelectEntityCallback<ENTITY, CB>() {
            public List<ENTITY> callbackSelectList(CB cb) {
                return callback.callbackSelectList(cb);
            }
        });
        assertEntityNotDeleted(entity, cb);
        return entity;
    }

    protected static interface InternalSelectEntityWithDeletedCheckCallback<ENTITY extends Entity, CB extends ConditionBean> {
        public List<ENTITY> callbackSelectList(CB cb);
    }

    protected int xcheckSafetyResultAsOne(ConditionBean cb) {
        final int safetyMaxResultSize = cb.getSafetyMaxResultSize();
        cb.checkSafetyResult(1);
        return safetyMaxResultSize;
    }

    protected void xrestoreSafetyResult(ConditionBean cb, int preSafetyMaxResultSize) {
        cb.checkSafetyResult(preSafetyMaxResultSize);
    }

    // ===================================================================================
    //                                                                           List Read
    //                                                                           =========
    /**
     * {@inheritDoc}
     */
    public ListResultBean<? extends Entity> readList(ConditionBean cb) {
        assertCBNotNull(cb);
        return doReadList(cb);
    }

    protected abstract ListResultBean<? extends Entity> doReadList(ConditionBean cb);

    // ===================================================================================
    //                                                                           Page Read
    //                                                                           =========
    /**
     * {@inheritDoc}
     */
    public PagingResultBean<? extends Entity> readPage(final ConditionBean cb) {
        assertCBNotNull(cb);
        return doReadPage(cb);
    }

    protected abstract PagingResultBean<? extends Entity> doReadPage(ConditionBean cb);

    // ===================================================================================
    //                                                              Entity Result Handling
    //                                                              ======================
    /**
     * Assert that the entity is not deleted.
     * @param entity Selected entity. (Nullable)
     * @param searchKey4Log Search-key for Logging.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    protected void assertEntityNotDeleted(Entity entity, Object searchKey4Log) {
        if (entity == null) {
            throwEntityAlreadyDeletedException(searchKey4Log);
        }
    }

    /**
     * Assert that the entity is not deleted.
     * @param ls Selected list. (Nullable)
     * @param searchKey4Log Search-key for Logging. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     */
    protected void assertEntityNotDeleted(List<? extends Entity> ls, Object searchKey4Log) {
        if (ls == null || ls.isEmpty()) {
            throwEntityAlreadyDeletedException(searchKey4Log);
        }
    }

    /**
     * Assert that the entity is selected as one.
     * @param ls Selected list. (NotNull)
     * @param searchKey4Log Search-key for Logging. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException
     */
    protected void assertEntitySelectedAsOne(List<? extends Entity> ls, Object searchKey4Log) {
        if (ls == null || ls.isEmpty()) {
            throwEntityAlreadyDeletedException(searchKey4Log);
        }
        if (ls.size() > 1) {
            throwEntityDuplicatedException(String.valueOf(ls.size()), searchKey4Log, null);
        }
    }

    private void throwEntityAlreadyDeletedException(Object searchKey4Log) {
        ConditionBeanContext.throwEntityAlreadyDeletedException(searchKey4Log);
    }

    private void throwEntityDuplicatedException(String resultCountString, Object searchKey4Log, Throwable cause) {
        ConditionBeanContext.throwEntityDuplicatedException(resultCountString, searchKey4Log, cause);
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    /**
     * The scalar function. <br />
     * This is not static class because this uses the method 'invoke(BehaviorCommand)'
     * @param <CB> The type of condition-bean.
     * @param <RESULT> The type of result.
     */
    public class SLFunction<CB extends ConditionBean, RESULT> { // SL: ScaLar

        /** The condition-bean for scalar select. (NotNull) */
        protected CB _conditionBean;

        /** The condition-bean for scalar select. (NotNull) */
        protected Class<RESULT> _resultType;

        /**
         * @param conditionBean The condition-bean initialized only for scalar select. (NotNull)
         * @param resultType The type os result. (NotNull)
         */
        public SLFunction(CB conditionBean, Class<RESULT> resultType) {
            _conditionBean = conditionBean;
            _resultType = resultType;
        }

        /**
         * Select the maximum value. <br />
         * <pre>
         * memberBhv.scalarSelect(Date.class).max(new ScalarQuery(MemberCB cb) {
         *     cb.specify().columnMemberBirthday(); // the required specification of target column
         *     cb.query().setMemberStatusCode_Equal_Formalized(); // query as you like it
         * });
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @return The maximum value. (Nullable)
         */
        public RESULT max(ScalarQuery<CB> scalarQuery) {
            assertObjectNotNull("scalarQuery", scalarQuery);
            return exec(scalarQuery, SqlClause.SelectClauseType.MAX);
        }

        /**
         * Select the minimum value. <br />
         * <pre>
         * memberBhv.scalarSelect(Date.class).min(new ScalarQuery(MemberCB cb) {
         *     cb.specify().columnMemberBirthday(); // the required specification of target column
         *     cb.query().setMemberStatusCode_Equal_Formalized(); // query as you like it
         * });
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @return The minimum value. (Nullable)
         */
        public RESULT min(ScalarQuery<CB> scalarQuery) {
            assertObjectNotNull("scalarQuery", scalarQuery);
            return exec(scalarQuery, SqlClause.SelectClauseType.MIN);
        }

        /**
         * Select the summary value. <br />
         * <pre>
         * purchaseBhv.scalarSelect(Integer.class).sum(new ScalarQuery(PurchaseCB cb) {
         *     cb.specify().columnPurchaseCount(); // the required specification of target column
         *     cb.query().setPurchaseDatetime_GreaterEqual(date); // query as you like it
         * });
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @return The summary value. (Nullable)
         */
        public RESULT sum(ScalarQuery<CB> scalarQuery) {
            assertObjectNotNull("scalarQuery", scalarQuery);
            return exec(scalarQuery, SqlClause.SelectClauseType.SUM);
        }

        /**
         * Select the average value. <br />
         * <pre>
         * purchaseBhv.scalarSelect(Integer.class).avg(new ScalarQuery(PurchaseCB cb) {
         *     cb.specify().columnPurchaseCount(); // the required specification of target column
         *     cb.query().setPurchaseDatetime_GreaterEqual(date); // query as you like it
         * });
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @return The average value. (Nullable)
         */
        public RESULT avg(ScalarQuery<CB> scalarQuery) {
            assertObjectNotNull("scalarQuery", scalarQuery);
            return exec(scalarQuery, SqlClause.SelectClauseType.AVG);
        }

        protected RESULT exec(ScalarQuery<CB> scalarQuery, SqlClause.SelectClauseType selectClauseType) {
            assertObjectNotNull("scalarQuery", scalarQuery);
            assertObjectNotNull("selectClauseType", selectClauseType);
            assertObjectNotNull("conditionBean", _conditionBean);
            assertObjectNotNull("resultType", _resultType);
            scalarQuery.query(_conditionBean);
            assertScalarSelectRequiredSpecifyColumn();
            return invoke(createSelectScalarCBCommand(_conditionBean, _resultType, selectClauseType));
        }

        protected void assertScalarSelectRequiredSpecifyColumn() {
            final String columnName = _conditionBean.getSqlClause().getSpecifiedColumnNameAsOne();
            if (columnName == null) {
                throwScalarSelectInvalidColumnSpecificationException();
            }
        }

        protected void throwScalarSelectInvalidColumnSpecificationException() {
            String msg = "Look! Read the message below." + ln();
            msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
            msg = msg + "The specified column for scalar select was Invalid!" + ln();
            msg = msg + ln();
            msg = msg + "[Advice]" + ln();
            msg = msg + " You should call specify().column[TargetColumn]() only once." + ln();
            msg = msg + "  For example:" + ln();
            msg = msg + "    " + ln();
            msg = msg + "    [Wrong]" + ln();
            msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
            msg = msg + "    memberBhv.scalarSelect(Date.class).max(new ScalarQuery<MemberCB>() {" + ln();
            msg = msg + "        public void query(MemberCB cb) {" + ln();
            msg = msg + "            // *No! It's empty!" + ln();
            msg = msg + "        }" + ln();
            msg = msg + "    });" + ln();
            msg = msg + "    - - - - - - - - - -/" + ln();
            msg = msg + "    " + ln();
            msg = msg + "    [Wrong]" + ln();
            msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
            msg = msg + "    memberBhv.scalarSelect(Date.class).max(new ScalarQuery<MemberCB>() {" + ln();
            msg = msg + "        public void query(MemberCB cb) {" + ln();
            msg = msg + "            cb.specify().columnMemberBirthday();" + ln();
            msg = msg + "            cb.specify().columnRegisterDatetime(); // *No! It's duplicated!" + ln();
            msg = msg + "        }" + ln();
            msg = msg + "    });" + ln();
            msg = msg + "    - - - - - - - - - -/" + ln();
            msg = msg + "    " + ln();
            msg = msg + "    [Good!]" + ln();
            msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
            msg = msg + "    memberBhv.scalarSelect(Date.class).max(new ScalarQuery<MemberCB>() {" + ln();
            msg = msg + "        public void query(MemberCB cb) {" + ln();
            msg = msg + "            cb.specify().columnMemberBirthday(); // *Point!" + ln();
            msg = msg + "        }" + ln();
            msg = msg + "    });" + ln();
            msg = msg + "    - - - - - - - - - -/" + ln();
            msg = msg + ln();
            msg = msg + "[ConditionBean Type]" + ln() + _conditionBean.getClass().getName() + ln();
            msg = msg + ln();
            msg = msg + "[Result Type]" + ln() + _resultType.getName() + ln();
            msg = msg + "* * * * * * * * * */";
            throw new ScalarSelectInvalidColumnSpecificationException(msg);
        }
    }

    public static class ScalarSelectInvalidColumnSpecificationException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ScalarSelectInvalidColumnSpecificationException(String msg) {
            super(msg);
        }
    }

    // ===================================================================================
    //                                                                          OutsideSql
    //                                                                          ==========
    /**
     * Get the basic executor of outside-SQL. <br />
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
     * {Cursor}
     *   o cursorHandling().selectCursor()
     * 
     * {Paging}
     *   o autoPaging().selectList()
     *   o autoPaging().selectPage()
     *   o manualPaging().selectList()
     *   o manualPaging().selectPage()
     * 
     * {Option -- Dynamic}
     *   o dynamicBinding().selectList()
     * </pre>
     * @return The basic executor of outside-SQL. (NotNull) 
     */
    public OutsideSqlBasicExecutor outsideSql() {
        assertBehaviorCommandInvoker("outsideSql");
        return _behaviorCommandInvoker.createOutsideSqlBasicExecutor(getTableDbName());
    }

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    /**
     * {@inheritDoc}
     */
    public java.math.BigDecimal readNextVal() {
        try {
            final Method method = getClass().getMethod("selectNextVal", new Class[] {});
            Object sequenceObject = method.invoke(this, new Object[] {});
            if (sequenceObject instanceof java.math.BigDecimal) {
                return (java.math.BigDecimal) sequenceObject;
            }
            return (java.math.BigDecimal) helpConvertingSequenceObject(java.math.BigDecimal.class, sequenceObject);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("The table does not have sequence: " + getTableDbName(), e);
        } catch (Exception e) {
            throw new RuntimeException("The selectNextVal() of the table threw the exception: " + getTableDbName(), e);
        }
    }

    protected Object helpConvertingSequenceObject(Class<?> resultClass, Object sequenceObject) {
        try {
            final Constructor<?> constructor = resultClass.getConstructor(new Class[] { String.class });
            return constructor.newInstance(new Object[] { sequenceObject.toString() });
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
            throw new RuntimeException("The readNextVal() of the table threw the exception: " + getTableDbName(), e);
        }
        try {
            final Method method = resultClass.getMethod("valueOf", new Class[] { long.class });
            return method.invoke(null, new Object[] { Long.valueOf(sequenceObject.toString()) });
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
            throw new RuntimeException("The readNextVal() of the table threw the exception: " + getTableDbName(), e);
        }
        String msg = "Cannot convert sequenceObject to resultClass:";
        msg = msg + " resultClass=" + resultClass + " sequenceObjectType=" + sequenceObject.getClass();
        throw new IllegalStateException(msg);
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    /**
     * Help load referrer internally.
     * About internal policy, the value of primary key(and others too) is treated as CaseInsensitive.
     * @param <LOCAL_ENTITY> The type of base entity.
     * @param <PK> The type of primary key.
     * @param <REFERRER_CB> The type of referrer condition-bean.
     * @param <REFERRER_ENTITY> The type of referrer entity.
     * @param localEntityList The list of local entity. (NotNull)
     * @param loadReferrerOption The option of loadReferrer. (NotNull)
     * @param callback The internal call-back of loadReferrer. (NotNull) 
     */
    protected <LOCAL_ENTITY extends Entity, PK, REFERRER_CB extends ConditionBean, REFERRER_ENTITY extends Entity> void helpLoadReferrerInternally(
            List<LOCAL_ENTITY> localEntityList, LoadReferrerOption<REFERRER_CB, REFERRER_ENTITY> loadReferrerOption,
            InternalLoadReferrerCallback<LOCAL_ENTITY, PK, REFERRER_CB, REFERRER_ENTITY> callback) {
        doHelpLoadReferrerInternally(localEntityList, loadReferrerOption, callback);
    }

    /**
     * Do help load referrer internally.
     * About internal policy, the value of primary key(and others too) is treated as CaseInsensitive.
     * @param <LOCAL_ENTITY> The type of base entity.
     * @param <PK> The type of primary key.
     * @param <REFERRER_CB> The type of referrer condition-bean.
     * @param <REFERRER_ENTITY> The type of referrer entity.
     * @param localEntityList The list of local entity. (NotNull)
     * @param loadReferrerOption The option of loadReferrer. (NotNull)
     * @param callback The internal call-back of loadReferrer. (NotNull) 
     */
    protected <LOCAL_ENTITY extends Entity, PK, REFERRER_CB extends ConditionBean, REFERRER_ENTITY extends Entity> void doHelpLoadReferrerInternally(
            List<LOCAL_ENTITY> localEntityList, LoadReferrerOption<REFERRER_CB, REFERRER_ENTITY> loadReferrerOption,
            final InternalLoadReferrerCallback<LOCAL_ENTITY, PK, REFERRER_CB, REFERRER_ENTITY> callback) {

        // - - - - - - - - - - -
        // Assert pre-condition
        // - - - - - - - - - - -
        assertBehaviorSelectorNotNull("loadReferrer");
        assertObjectNotNull("localEntityList", localEntityList);
        assertObjectNotNull("loadReferrerOption", loadReferrerOption);
        if (localEntityList.isEmpty()) {
            return;
        }

        // - - - - - - - - - - - - - -
        // Prepare temporary container
        // - - - - - - - - - - - - - -
        final Map<PK, LOCAL_ENTITY> pkLocalEntityMap = new LinkedHashMap<PK, LOCAL_ENTITY>();
        final List<PK> pkList = new ArrayList<PK>();
        for (LOCAL_ENTITY localEntity : localEntityList) {
            final PK primaryKeyValue = callback.getPKVal(localEntity);
            pkList.add(primaryKeyValue);
            pkLocalEntityMap.put(toLowerCasePrimaryKeyIfString(primaryKeyValue), localEntity);
        }

        // - - - - - - - - - - - - - - - -
        // Prepare referrer condition bean
        // - - - - - - - - - - - - - - - -
        final REFERRER_CB cb;
        if (loadReferrerOption.getReferrerConditionBean() != null) {
            cb = loadReferrerOption.getReferrerConditionBean();
        } else {
            cb = callback.newMyCB();
        }

        // - - - - - - - - - - - - - -
        // Select the list of referrer
        // - - - - - - - - - - - - - -
        callback.qyFKIn(cb, pkList);
        cb.xregisterUnionQuerySynchronizer(new UnionQuery<ConditionBean>() {
            public void query(ConditionBean unionCB) {
                @SuppressWarnings("unchecked")
                REFERRER_CB referrerUnionCB = (REFERRER_CB) unionCB;
                // for when application uses union query in condition-bean set-upper.
                callback.qyFKIn(referrerUnionCB, pkList);
            }
        });
        loadReferrerOption.delegateKeyConditionExchangingFirstWhereClauseForLastOne(cb);
        if (!loadReferrerOption.isStopOrderByKey() && pkList.size() > 1) {
            callback.qyOdFKAsc(cb);
            cb.getSqlComponentOfOrderByClause().exchangeFirstOrderByElementForLastOne();
        }
        loadReferrerOption.delegateConditionBeanSettingUp(cb);
        final List<REFERRER_ENTITY> referrerList = callback.selRfLs(cb);
        loadReferrerOption.delegateEntitySettingUp(referrerList);

        // - - - - - - - - - - - - - - - - - - - - - - - -
        // Create the map of {primary key / referrer list}
        // - - - - - - - - - - - - - - - - - - - - - - - -
        final Map<PK, List<REFERRER_ENTITY>> pkReferrerListMap = new LinkedHashMap<PK, List<REFERRER_ENTITY>>();
        for (REFERRER_ENTITY referrerEntity : referrerList) {
            final PK referrerListKey;
            {
                final PK foreignKeyValue = callback.getFKVal(referrerEntity);
                referrerListKey = toLowerCasePrimaryKeyIfString(foreignKeyValue);
            }
            if (!pkReferrerListMap.containsKey(referrerListKey)) {
                pkReferrerListMap.put(referrerListKey, new ArrayList<REFERRER_ENTITY>());
            }
            (pkReferrerListMap.get(referrerListKey)).add(referrerEntity);

            // for Reverse Reference.
            final LOCAL_ENTITY localEntity = pkLocalEntityMap.get(referrerListKey);
            callback.setlcEt(referrerEntity, localEntity);
        }

        // - - - - - - - - - - - - - - - - - -
        // Relate referrer list to base entity
        // - - - - - - - - - - - - - - - - - -
        for (LOCAL_ENTITY localEntity : localEntityList) {
            final PK referrerListKey;
            {
                final PK primaryKey = callback.getPKVal(localEntity);
                referrerListKey = toLowerCasePrimaryKeyIfString(primaryKey);
            }
            if (pkReferrerListMap.containsKey(referrerListKey)) {
                callback.setRfLs(localEntity, pkReferrerListMap.get(referrerListKey));
            } else {
                callback.setRfLs(localEntity, new ArrayList<REFERRER_ENTITY>());
            }
        }
    }

    /**
     * To lower case for primary key if the value is string.
     * @param <PK> The type of primary key.
     * @param value The value of primary key. (Nullable)
     * @return The value of primary key. (Nullable)
     */
    @SuppressWarnings("unchecked")
    protected <PK> PK toLowerCasePrimaryKeyIfString(PK value) {
        return (PK) toLowerCaseIfString(value);
    }

    /**
     * @param <LOCAL_ENTITY> The type of base entity.
     * @param <PK> The type of primary key.
     * @param <REFERRER_CB> The type of referrer conditionBean.
     * @param <REFERRER_ENTITY> The type of referrer entity.
     */
    protected static interface InternalLoadReferrerCallback<LOCAL_ENTITY extends Entity, PK, REFERRER_CB extends ConditionBean, REFERRER_ENTITY extends Entity> {
        // For Base
        public PK getPKVal(LOCAL_ENTITY entity); // getPrimaryKeyValue()

        public void setRfLs(LOCAL_ENTITY entity, List<REFERRER_ENTITY> referrerList); // setReferrerList()

        // For Referrer
        public REFERRER_CB newMyCB(); // newMyConditionBean()

        public void qyFKIn(REFERRER_CB cb, List<PK> pkList); // queryForeignKeyInScope()

        public void qyOdFKAsc(REFERRER_CB cb); // queryAddOrderByForeignKeyAsc() 

        public List<REFERRER_ENTITY> selRfLs(REFERRER_CB cb); // selectReferrerList() 

        public PK getFKVal(REFERRER_ENTITY entity); // getForeignKeyValue()

        public void setlcEt(REFERRER_ENTITY referrerEntity, LOCAL_ENTITY localEntity); // setLocalEntity()
    }

    // assertLoadReferrerArgument() as Internal
    protected void xassLRArg(Entity entity, ConditionBeanSetupper<? extends ConditionBean> conditionBeanSetupper) {
        assertObjectNotNull("entity(" + getDBMeta().getEntityType().getSimpleName() + ")", entity);
        assertObjectNotNull("conditionBeanSetupper", conditionBeanSetupper);
    }

    protected void xassLRArg(List<? extends Entity> entityList,
            ConditionBeanSetupper<? extends ConditionBean> conditionBeanSetupper) {
        assertObjectNotNull("List<" + getDBMeta().getEntityType().getSimpleName() + ">", entityList);
        assertObjectNotNull("conditionBeanSetupper", conditionBeanSetupper);
    }

    protected void xassLRArg(Entity entity,
            LoadReferrerOption<? extends ConditionBean, ? extends Entity> loadReferrerOption) {
        assertObjectNotNull("entity(" + getDBMeta().getEntityType().getSimpleName() + ")", entity);
        assertObjectNotNull("loadReferrerOption", loadReferrerOption);
    }

    protected void xassLRArg(List<? extends Entity> entityList,
            LoadReferrerOption<? extends ConditionBean, ? extends Entity> loadReferrerOption) {
        assertObjectNotNull("List<" + getDBMeta().getEntityType().getSimpleName() + ">", entityList);
        assertObjectNotNull("loadReferrerOption", loadReferrerOption);
    }

    protected BehaviorSelector xgetBSFLR() { // getBehaviorSelectorForLoadReferrer() as Internal
        assertBehaviorSelectorNotNull("loadReferrer");
        return getBehaviorSelector();
    }

    private void assertBehaviorSelectorNotNull(String methodName) {
        if (_behaviorSelector == null) {
            String msg = "Look! Read the message below." + ln();
            msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
            msg = msg + "Not found the selector of behavior as behavior's attribute!" + ln();
            msg = msg + ln();
            msg = msg + "[Advice]" + ln();
            msg = msg + "Please confirm the definition of the selector at your component configuration of DBFlute."
                    + ln();
            msg = msg + "It is precondition that '" + methodName + "()' needs the selector instance." + ln();
            msg = msg + ln();
            msg = msg + "[Your Behavior's Attributes]" + ln();
            msg = msg + "  _behaviorCommandInvoker : " + _behaviorCommandInvoker + ln();
            msg = msg + "  _behaviorSelector       : " + _behaviorSelector + ln();
            msg = msg + "* * * * * * * * * */";
            throw new IllegalStateException(msg);
        }
    }

    protected <ELEMENT> List<ELEMENT> xnewLRLs(ELEMENT element) { // newLoadReferrerList() as Internal
        List<ELEMENT> ls = new ArrayList<ELEMENT>(1);
        ls.add(element);
        return ls;
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    protected <LOCAL_ENTITY extends Entity, FOREIGN_ENTITY extends Entity> List<FOREIGN_ENTITY> helpPulloutInternally(
            List<LOCAL_ENTITY> localEntityList, InternalPulloutCallback<LOCAL_ENTITY, FOREIGN_ENTITY> callback) {
        assertObjectNotNull("localEntityList", localEntityList);
        assertObjectNotNull("callback", callback);
        final Set<FOREIGN_ENTITY> foreignSet = new LinkedHashSet<FOREIGN_ENTITY>();
        final Map<FOREIGN_ENTITY, List<LOCAL_ENTITY>> foreignReferrerMap = new LinkedHashMap<FOREIGN_ENTITY, List<LOCAL_ENTITY>>();
        final boolean existsReferrer = callback.hasRf();
        for (LOCAL_ENTITY entity : localEntityList) {
            final FOREIGN_ENTITY foreignEntity = callback.getFr(entity);
            if (foreignEntity == null) {
                continue;
            }
            if (!foreignSet.contains(foreignEntity)) {
                foreignSet.add(foreignEntity);
            }
            if (existsReferrer) {
                if (!foreignReferrerMap.containsKey(foreignEntity)) {
                    foreignReferrerMap.put(foreignEntity, new ArrayList<LOCAL_ENTITY>());
                }
                foreignReferrerMap.get(foreignEntity).add(entity);
            }
        }
        final Set<Entry<FOREIGN_ENTITY, List<LOCAL_ENTITY>>> entrySet = foreignReferrerMap.entrySet();
        for (Entry<FOREIGN_ENTITY, List<LOCAL_ENTITY>> entry : entrySet) {
            callback.setRfLs(entry.getKey(), entry.getValue());
        }
        return new ArrayList<FOREIGN_ENTITY>(foreignSet);
    }

    protected static interface InternalPulloutCallback<LOCAL_ENTITY extends Entity, FOREIGN_ENTITY extends Entity> {
        FOREIGN_ENTITY getFr(LOCAL_ENTITY entity); // getForeignEntity()

        boolean hasRf(); // hasReferrer()

        void setRfLs(FOREIGN_ENTITY foreignEntity, List<LOCAL_ENTITY> localList); // setReferrerList()
    }

    // ===================================================================================
    //                                                                          Token File
    //                                                                          ==========
    /**
     * Get the executor of token file output.
     * @return The executor of token file output. (NotNull)
     */
    public TokenFileOutputExecutor tokenFileOutput() {
        return new TokenFileOutputExecutor();
    }

    /**
     * The executor of token file output.
     */
    public class TokenFileOutputExecutor {

        /**
         * Output token file from the table records. <br />
         * The supported column types are String, Number and Date. <br />
         * The search result is on memory temporarily so don't use this method if you have enormous records.
         * @param cb The condition-bean. (NotNull: The setupSelect_Xxx() is ignored.)
         * @param filename The name of the file. (NotNull and NotEmpty)
         * @param tokenFileOutputOption The option of token file output. (NotNull and Required{delimiter and encoding})
         * @return The result of token file output. (NotNull)
         * @throws java.io.FileNotFoundException The file is not found.
         * @throws java.io.IOException The IO exception occurred.
         */
        public TokenFileOutputResult outputTokenFile(ConditionBean cb, String filename,
                TokenFileOutputOption tokenFileOutputOption) throws java.io.FileNotFoundException, java.io.IOException {
            assertCBNotNull(cb);
            assertStringNotNullAndNotTrimmedEmpty("filename", filename);
            assertObjectNotNull("tokenFileOutputOption", tokenFileOutputOption);

            final List<? extends Entity> entityList = readList(cb);
            final List<List<String>> rowList = new ArrayList<List<String>>();
            for (Entity entity : entityList) {
                final List<String> valueList = getDBMeta().convertToColumnStringValueList(entity);
                rowList.add(valueList);
            }
            final FileMakingSimpleFacade fileMakingSimpleFacade = new FileMakingSimpleFacadeImpl();
            final FileMakingOption fileMakingOption = tokenFileOutputOption.getFileMakingOption();
            final FileMakingHeaderInfo fileMakingHeaderInfo = new FileMakingHeaderInfo();
            final List<String> columnDbNameList = new ArrayList<String>();
            for (final java.util.Iterator<ColumnInfo> ite = getDBMeta().getColumnInfoList().iterator(); ite.hasNext();) {
                final ColumnInfo columnInfo = ite.next();
                columnDbNameList.add(columnInfo.getColumnDbName());
            }
            fileMakingHeaderInfo.setColumnNameList(columnDbNameList);
            fileMakingOption.setFileMakingHeaderInfo(fileMakingHeaderInfo);
            fileMakingSimpleFacade.makeFromRowList(filename, rowList, fileMakingOption);
            final TokenFileOutputResult tokeFileOutputResult = new TokenFileOutputResult();
            tokeFileOutputResult.setSelectedList(entityList);
            return tokeFileOutputResult;
        }
    }

    // ===================================================================================
    //                                                                      Process Method
    //                                                                      ==============
    /**
     * Filter the entity of insert.
     * @param targetEntity Target entity that the type is entity interface. (NotNull)
     */
    protected void filterEntityOfInsert(Entity targetEntity) { // for isAvailableNonPrimaryKeyWritable
    }

    // ===================================================================================
    //                                                                    Behavior Command
    //                                                                    ================
    public void warmUpCommand() {
        {
            SelectCountCBCommand cmd = createSelectCountCBCommand(newConditionBean());
            cmd.setInitializeOnly(true);
            invoke(cmd);
        }
        {
            SelectListCBCommand<? extends Entity> cmd = createSelectListCBCommand(newConditionBean(), getDBMeta()
                    .getEntityType());
            cmd.setInitializeOnly(true);
            invoke(cmd);
        }
    }

    protected SelectCountCBCommand createSelectCountCBCommand(ConditionBean cb) {
        assertBehaviorCommandInvoker("createSelectCountCBCommand");
        final SelectCountCBCommand command = xsetupSelectCommand(new SelectCountCBCommand());
        command.setConditionBeanType(cb.getClass());
        command.setConditionBean(cb);
        return command;
    }

    protected <ENTITY extends Entity> SelectCursorCBCommand<ENTITY> createSelectCursorCBCommand(ConditionBean cb,
            EntityRowHandler<ENTITY> entityRowHandler, Class<ENTITY> entityType) {
        assertBehaviorCommandInvoker("createSelectCursorCBCommand");
        final SelectCursorCBCommand<ENTITY> command = xsetupSelectCommand(new SelectCursorCBCommand<ENTITY>());
        command.setConditionBeanType(cb.getClass());
        command.setConditionBean(cb);
        command.setEntityType(entityType);
        command.setEntityRowHandler(entityRowHandler);
        return command;
    }

    protected <ENTITY extends Entity> SelectListCBCommand<ENTITY> createSelectListCBCommand(ConditionBean cb,
            Class<ENTITY> entityType) {
        assertBehaviorCommandInvoker("createSelectListCBCommand");
        final SelectListCBCommand<ENTITY> command = xsetupSelectCommand(new SelectListCBCommand<ENTITY>());
        command.setConditionBeanType(cb.getClass());
        command.setConditionBean(cb);
        command.setEntityType(entityType);
        return command;
    }

    protected <RESULT> SelectNextValCommand<RESULT> createSelectNextValCommand(Class<RESULT> resultType) {
        assertBehaviorCommandInvoker("createSelectNextValCommand");
        final SelectNextValCommand<RESULT> command = xsetupSelectCommand(new SelectNextValCommand<RESULT>());
        command.setResultType(resultType);
        command.setDBMeta(getDBMeta());
        return command;
    }

    protected <RESULT> SelectScalarCBCommand<RESULT> createSelectScalarCBCommand(ConditionBean cb,
            Class<RESULT> resultType, SqlClause.SelectClauseType selectClauseType) {
        assertBehaviorCommandInvoker("createSelectScalarCBCommand");
        final SelectScalarCBCommand<RESULT> command = xsetupSelectCommand(new SelectScalarCBCommand<RESULT>());
        command.setConditionBeanType(cb.getClass());
        command.setConditionBean(cb);
        command.setResultType(resultType);
        command.setSelectClauseType(selectClauseType);
        return command;
    }

    private <COMMAND extends AbstractBehaviorCommand<?>> COMMAND xsetupSelectCommand(COMMAND command) {
        command.setTableDbName(getTableDbName());
        _behaviorCommandInvoker.injectComponentProperty(command);
        return command;
    }

    /**
     * Invoke the command of behavior.
     * @param <RESULT> The type of result.
     * @param behaviorCommand The command of behavior. (NotNull)
     * @return The instance of result. (Nullable)
     */
    protected <RESULT> RESULT invoke(BehaviorCommand<RESULT> behaviorCommand) {
        return _behaviorCommandInvoker.invoke(behaviorCommand);
    }

    protected void assertBehaviorCommandInvoker(String methodName) {
        if (_behaviorCommandInvoker == null) {
            String msg = "Look! Read the message below." + ln();
            msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
            msg = msg + "Not found the invoker of behavior command as behavior's attributed!" + ln();
            msg = msg + ln();
            msg = msg + "[Advice]" + ln();
            msg = msg + "Please confirm the definition of the invoker at your 'dbflute.dicon'." + ln();
            msg = msg + "It is precondition that '" + methodName + "()' needs the invoker instance." + ln();
            msg = msg + ln();
            msg = msg + "[Your Behavior's Attributes]" + ln();
            msg = msg + "  _behaviorCommandInvoker : " + _behaviorCommandInvoker + ln();
            msg = msg + "  _behaviorSelector       : " + _behaviorSelector + ln();
            msg = msg + "* * * * * * * * * */";
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                Optimistic Lock Info
    //                                                                ====================
    /**
     * Does the entity have a value of version-no? 
     * @param entity The instance of entity. (NotNull)
     * @return Determination.
     */
    protected abstract boolean hasVersionNoValue(Entity entity);

    /**
     * Does the entity have a value of update-date? 
     * @param entity The instance of entity. (NotNull)
     * @return Determination.
     */
    protected abstract boolean hasUpdateDateValue(Entity entity);

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    /**
     * To lower case if the type is String.
     * @param obj Object. (Nullable)
     * @return Lower object. (Nullable)
     */
    protected Object toLowerCaseIfString(Object obj) {
        if (obj != null && obj instanceof String) {
            return ((String) obj).toLowerCase();
        }
        return obj;
    }

    /**
     * Get the value of line separator.
     * @return The value of line separator. (NotNull)
     */
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                     Downcast Helper
    //                                                                     ===============
    @SuppressWarnings("unchecked")
    protected <ENTITY extends Entity> ENTITY helpEntityDowncastInternally(Entity entity, Class<ENTITY> clazz) {
        assertObjectNotNull("entity", entity);
        assertObjectNotNull("clazz", clazz);
        try {
            return (ENTITY) entity;
        } catch (ClassCastException e) {
            String msg = "The entity should be " + clazz.getSimpleName();
            msg = msg + " but it was: " + entity.getClass();
            throw new RuntimeException(msg, e);
        }
    }

    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> CB helpConditionBeanDowncastInternally(ConditionBean cb, Class<CB> clazz) {
        assertObjectNotNull("cb", cb);
        assertObjectNotNull("clazz", clazz);
        try {
            return (CB) cb;
        } catch (ClassCastException e) {
            String msg = "The condition-bean should be " + clazz.getSimpleName();
            msg = msg + " but it was: " + cb.getClass();
            throw new RuntimeException(msg, e);
        }
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    // -----------------------------------------------------
    //                                         Assert Object
    //                                         -------------
    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert that the entity is not null.
     * @param entity Entity. (NotNull)
     */
    protected void assertEntityNotNull(Entity entity) {
        assertObjectNotNull("entity", entity);
    }

    /**
     * Assert that the condition-bean is not null.
     * @param cb Condition-bean. (NotNull)
     */
    protected void assertConditionBeanNotNull(ConditionBean cb) {
        assertCBNotNull(cb);
    }

    /**
     * Assert that the condition-bean is not null.
     * @param cb Condition-bean. (NotNull)
     */
    protected void assertCBNotNull(ConditionBean cb) {
        assertObjectNotNull("cb", cb);
    }

    /**
     * Assert that the entity has primary-key value.
     * @param entity Entity. (NotNull)
     */
    protected void assertEntityNotNullAndHasPrimaryKeyValue(Entity entity) {
        assertEntityNotNull(entity);
        if (!entity.hasPrimaryKeyValue()) {
            String msg = "The entity must should primary-key: entity=" + entity;
            throw new IllegalArgumentException(msg + entity);
        }
    }

    // -----------------------------------------------------
    //                                         Assert String
    //                                         -------------
    /**
     * Assert that the entity is not null and not trimmed empty.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     */
    protected void assertStringNotNullAndNotTrimmedEmpty(String variableName, String value) {
        assertObjectNotNull("variableName", variableName);
        assertObjectNotNull(variableName, value);
        if (value.trim().length() == 0) {
            String msg = "The value should not be empty: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
    }

    // -----------------------------------------------------
    //                                           Assert List
    //                                           -----------
    /**
     * Assert that the list is empty.
     * @param ls List. (NotNull)
     */
    protected void assertListNotNullAndEmpty(List<?> ls) {
        assertObjectNotNull("ls", ls);
        if (!ls.isEmpty()) {
            String msg = "The list should be empty: ls=" + ls.toString();
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert that the list is not empty.
     * @param ls List. (NotNull)
     */
    protected void assertListNotNullAndNotEmpty(List<?> ls) {
        assertObjectNotNull("ls", ls);
        if (ls.isEmpty()) {
            String msg = "The list should not be empty: ls=" + ls.toString();
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert that the list having only one.
     * @param ls List. (NotNull)
     */
    protected void assertListNotNullAndHasOnlyOne(List<?> ls) {
        assertObjectNotNull("ls", ls);
        if (ls.size() != 1) {
            String msg = "The list should contain only one object: ls=" + ls.toString();
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the invoker of behavior command.
     * @return The invoker of behavior command. (Nullable: But normally NotNull)
     */
    protected BehaviorCommandInvoker getBehaviorCommandInvoker() {
        return _behaviorCommandInvoker;
    }

    /**
     * Set the invoker of behavior command.
     * @param behaviorCommandInvoker The invoker of behavior command. (NotNull)
     */
    public void setBehaviorCommandInvoker(BehaviorCommandInvoker behaviorCommandInvoker) {
        this._behaviorCommandInvoker = behaviorCommandInvoker;
    }

    /**
     * Get the selector of behavior.
     * @return The select of behavior. (Nullable: But normally NotNull)
     */
    protected BehaviorSelector getBehaviorSelector() {
        return _behaviorSelector;
    }

    /**
     * Set the selector of behavior.
     * @param behaviorSelector The selector of behavior. (NotNull)
     */
    public void setBehaviorSelector(BehaviorSelector behaviorSelector) {
        this._behaviorSelector = behaviorSelector;
    }
}
