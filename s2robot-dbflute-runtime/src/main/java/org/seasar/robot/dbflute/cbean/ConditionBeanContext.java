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
package org.seasar.robot.dbflute.cbean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.exception.ColumnQueryInvalidColumnSpecificationException;
import org.seasar.robot.dbflute.exception.DerivedReferrerInvalidForeignSpecificationException;
import org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.robot.dbflute.exception.EntityDuplicatedException;
import org.seasar.robot.dbflute.exception.QueryDerivedReferrerInvalidColumnSpecificationException;
import org.seasar.robot.dbflute.exception.QueryDerivedReferrerUnmatchedColumnTypeException;
import org.seasar.robot.dbflute.exception.ScalarSelectInvalidForeignSpecificationException;
import org.seasar.robot.dbflute.exception.ScalarSubQueryInvalidColumnSpecificationException;
import org.seasar.robot.dbflute.exception.ScalarSubQueryInvalidForeignSpecificationException;
import org.seasar.robot.dbflute.exception.ScalarSubQueryUnmatchedColumnTypeException;
import org.seasar.robot.dbflute.exception.SetupSelectAfterUnionException;
import org.seasar.robot.dbflute.exception.SpecifiedDerivedOrderByAliasNameNotFoundException;
import org.seasar.robot.dbflute.exception.SpecifyColumnNotSetupSelectColumnException;
import org.seasar.robot.dbflute.exception.SpecifyDerivedReferrerEntityPropertyNotFoundException;
import org.seasar.robot.dbflute.exception.SpecifyDerivedReferrerInvalidAliasNameException;
import org.seasar.robot.dbflute.exception.SpecifyDerivedReferrerInvalidColumnSpecificationException;
import org.seasar.robot.dbflute.exception.SpecifyDerivedReferrerUnmatchedColumnTypeException;
import org.seasar.robot.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.robot.dbflute.twowaysql.factory.SqlAnalyzerFactory;
import org.seasar.robot.dbflute.util.DfStringUtil;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * The context of condition-bean.
 * @author jflute
 */
public class ConditionBeanContext {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(ConditionBeanContext.class);

    // ===================================================================================
    //                                                             ConditionBean on Thread
    //                                                             =======================
    /** The thread-local for condition-bean. */
    private static final ThreadLocal<ConditionBean> _conditionBeanLocal = new ThreadLocal<ConditionBean>();

    /**
     * Get condition-bean on thread.
     * @return Condition-bean. (Nullable)
     */
    public static ConditionBean getConditionBeanOnThread() {
        return (ConditionBean) _conditionBeanLocal.get();
    }

    /**
     * Set condition-bean on thread.
     * @param cb Condition-bean. (NotNull)
     */
    public static void setConditionBeanOnThread(ConditionBean cb) {
        if (cb == null) {
            String msg = "The argument[cb] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _conditionBeanLocal.set(cb);
    }

    /**
     * Is existing condition-bean on thread?
     * @return Determination.
     */
    public static boolean isExistConditionBeanOnThread() {
        return (_conditionBeanLocal.get() != null);
    }

    /**
     * Clear condition-bean on thread.
     */
    public static void clearConditionBeanOnThread() {
        _conditionBeanLocal.set(null);
    }

    // ===================================================================================
    //                                                          EntityRowHandler on Thread
    //                                                          ==========================
    /** The thread-local for entity row handler. */
    private static final ThreadLocal<EntityRowHandler<? extends Entity>> _entityRowHandlerLocal = new ThreadLocal<EntityRowHandler<? extends Entity>>();

    /**
     * Get the handler of entity row. on thread.
     * @return The handler of entity row. (Nullable)
     */
    public static EntityRowHandler<? extends Entity> getEntityRowHandlerOnThread() {
        return (EntityRowHandler<? extends Entity>) _entityRowHandlerLocal.get();
    }

    /**
     * Set the handler of entity row on thread.
     * @param handler The handler of entity row. (NotNull)
     */
    public static void setEntityRowHandlerOnThread(EntityRowHandler<? extends Entity> handler) {
        if (handler == null) {
            String msg = "The argument[handler] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _entityRowHandlerLocal.set(handler);
    }

    /**
     * Is existing the handler of entity row on thread?
     * @return Determination.
     */
    public static boolean isExistEntityRowHandlerOnThread() {
        return (_entityRowHandlerLocal.get() != null);
    }

    /**
     * Clear the handler of entity row on thread.
     */
    public static void clearEntityRowHandlerOnThread() {
        _entityRowHandlerLocal.set(null);
    }

    // ===================================================================================
    //                                                                  Type Determination
    //                                                                  ==================
    /**
     * Is the argument condition-bean?
     * @param dtoInstance DTO instance.
     * @return Determination.
     */
    public static boolean isTheArgumentConditionBean(final Object dtoInstance) {
        return dtoInstance instanceof ConditionBean;
    }

    /**
     * Is the type condition-bean?
     * @param dtoClass DtoClass.
     * @return Determination.
     */
    public static boolean isTheTypeConditionBean(final Class<?> dtoClass) {
        return ConditionBean.class.isAssignableFrom(dtoClass);
    }

    // ===================================================================================
    //                                                                        Cool Classes
    //                                                                        ============
    public static void loadCoolClasses() {
        boolean debugEnabled = false; // If you watch the log, set this true.
        // Against the ClassLoader Headache!
        final StringBuilder sb = new StringBuilder();
        {
            final Class<?> clazz = org.seasar.robot.dbflute.cbean.SimplePagingBean.class;
            if (debugEnabled) {
                sb.append("  ...Loading class of " + clazz.getName() + " by " + clazz.getClassLoader().getClass())
                        .append(ln());
            }
        }
        {
            loadClass(org.seasar.robot.dbflute.AccessContext.class);
            loadClass(org.seasar.robot.dbflute.CallbackContext.class);
            loadClass(org.seasar.robot.dbflute.cbean.EntityRowHandler.class);
            loadClass(org.seasar.robot.dbflute.cbean.coption.FromToOption.class);
            loadClass(org.seasar.robot.dbflute.cbean.coption.LikeSearchOption.class);
            loadClass(org.seasar.robot.dbflute.cbean.coption.InScopeOption.class);
            loadClass(org.seasar.robot.dbflute.cbean.grouping.GroupingOption.class);
            loadClass(org.seasar.robot.dbflute.cbean.grouping.GroupingRowEndDeterminer.class);
            loadClass(org.seasar.robot.dbflute.cbean.grouping.GroupingRowResource.class);
            loadClass(org.seasar.robot.dbflute.cbean.grouping.GroupingRowSetupper.class);
            loadClass(org.seasar.robot.dbflute.cbean.pagenavi.PageNumberLink.class);
            loadClass(org.seasar.robot.dbflute.cbean.pagenavi.PageNumberLinkSetupper.class);
            loadClass(org.seasar.robot.dbflute.jdbc.CursorHandler.class);
            if (debugEnabled) {
                sb.append("  ...Loading class of ...and so on");
            }
        }
        if (debugEnabled) {
            _log.debug("{Initialize against the ClassLoader Headache}" + ln() + sb);
        }
    }

    protected static void loadClass(Class<?> clazz) { // for avoiding Find-Bugs warnings
        // do nothing
    }

    // ===================================================================================
    //                                                                  Exception Handling
    //                                                                  ==================
    // -----------------------------------------------------
    //                                                Entity
    //                                                ------
    public static void throwEntityAlreadyDeletedException(Object searchKey4Log) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The entity was NOT found! it has already been deleted!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm the existence of your target record on your database." + ln();
        msg = msg + "Does the target record really created before this operation?" + ln();
        msg = msg + "Has the target record been deleted by other thread?" + ln();
        msg = msg + "It is precondition that the record exists on your database." + ln();
        msg = msg + ln();
        if (searchKey4Log != null && searchKey4Log instanceof ConditionBean) {
            final ConditionBean cb = (ConditionBean) searchKey4Log;
            msg = msg + "[Display SQL]" + ln() + cb.toDisplaySql() + ln();
        } else {
            msg = msg + "[Search Condition]" + ln() + searchKey4Log + ln();
        }
        msg = msg + "* * * * * * * * * */";
        throw new EntityAlreadyDeletedException(msg);
    }

    public static void throwEntityDuplicatedException(String resultCountString, Object searchKey4Log, Throwable cause) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The entity was too many! it has been duplicated." + ln();
        msg = msg + "It should be the only one! But the resultCount: " + resultCountString + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Confirm your search condition. Does it really select the only one?" + ln();
        msg = msg + "And confirm your database. Does it really exist the only one?" + ln();
        msg = msg + ln();
        if (searchKey4Log != null && searchKey4Log instanceof ConditionBean) {
            final ConditionBean cb = (ConditionBean) searchKey4Log;
            msg = msg + "[Display SQL]" + ln() + cb.toDisplaySql() + ln();
        } else {
            msg = msg + "[Search Condition]" + ln() + searchKey4Log + ln();
        }
        msg = msg + "* * * * * * * * * */";
        if (cause != null) {
            throw new EntityDuplicatedException(msg, cause);
        } else {
            throw new EntityDuplicatedException(msg);
        }
    }

    // -----------------------------------------------------
    //                                         Set up Select
    //                                         -------------
    public static void throwSetupSelectAfterUnionException(String className, String foreignPropertyName,
            String displaySql) {
        String methodName = "setupSelect_" + initCap(foreignPropertyName) + "()";
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "You should NOT call " + methodName + " after calling union()!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + methodName + " should be called before calling union()." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    " + className + " cb = new " + className + "();" + ln();
        msg = msg + "    cb." + methodName + "; // You should call here!" + ln();
        msg = msg + "    cb.query().setXxx...;" + ln();
        msg = msg + "    cb.union(new UnionQuery<" + className + ">() {" + ln();
        msg = msg + "        public void query(" + className + " unionCB) {" + ln();
        msg = msg + "            unionCB.query().setXxx...;" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + "    // You should not call setupSelect after calling union()!" + ln();
        msg = msg + "    // cb." + methodName + ";" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[SetupSelect Method]" + ln() + methodName + ln();
        msg = msg + ln();
        msg = msg + "[ConditionBean SQL]" + ln() + displaySql + ln();
        msg = msg + "* * * * * * * * * */" + ln();
        throw new SetupSelectAfterUnionException(msg);
    }

    // -----------------------------------------------------
    //                                        Specify Column
    //                                        --------------
    public static void throwSpecifyColumnNotSetupSelectColumnException(ConditionBean baseCB, String tableDbName,
            String columnName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "You specified the column that had Not been Set up!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should call setupSelect_[ForeignTable]()"
                + " before calling specify[ForeignTable]().column[TargetColumn]()." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().specifyMemberStatus().columnMemberStatusName(); // *No!" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.setupSelect_MemberStatus(); // *Point!" + ln();
        msg = msg + "    cb.specify().specifyMemberStatus().columnMemberStatusName();" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[ConditionBean]" + ln() + baseCB.getClass().getName() + ln();
        msg = msg + ln();
        msg = msg + "[Specified Column]" + ln() + tableDbName + "." + columnName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifyColumnNotSetupSelectColumnException(msg);
    }

    // -----------------------------------------------------
    //                                         Scalar Select
    //                                         -------------
    public static void throwScalarSelectInvalidForeignSpecificationException(String foreignPropertyName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "You specified a foreign table column in spite of scalar select!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should specified a local table column at condition-bean for scalar select." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    memberBhv.scalarSelect(Integer.class).max(new ScalarSelect<MemberCB>() {" + ln();
        msg = msg + "        public void query(MemberCB cb) {" + ln();
        msg = msg + "            cb.specify().specifyMemberStatus().columnDisplayOrder(); // *No!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    memberBhv.scalarSelect(Date.class).max(new ScalarSelect() {" + ln();
        msg = msg + "        public void query(MemberCB cb) {" + ln();
        msg = msg + "            cb.specify().columnMemberBirthday(); // *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Specified Foreign Property]" + ln() + foreignPropertyName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new ScalarSelectInvalidForeignSpecificationException(msg);
    }

    // -----------------------------------------------------
    //                              Specify Derived Referrer
    //                              ------------------------
    public static void throwSpecifyDerivedReferrerInvalidAliasNameException(ConditionQuery localCQ) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The alias name for specify derived-referrer was INVALID!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should set valid alias name. {NotNull, NotEmpty}" + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, null); // *No! {null, \"\", \"   \"} are NG!" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\"); // *Point!" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Local Table]" + ln() + localCQ.getTableDbName() + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifyDerivedReferrerInvalidAliasNameException(msg);
    }

    public static void throwSpecifyDerivedReferrerEntityPropertyNotFoundException(String aliasName, Class<?> entityType) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "A property for derived-referrer was Not Found in the entity!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should implement a property(setter and getter) in the entity." + ln();
        msg = msg + "Or you should confirm whether the alias name has typo or not." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    ConditionBean Invoking:" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    Extended Entity:" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    // At the entity of Purchase..." + ln();
        msg = msg + "    protected Date _latestPurchaseDatetime;" + ln();
        msg = msg + "    public Date getLatestPurchaseDatetime() {" + ln();
        msg = msg + "        return _latestPurchaseDatetime;" + ln();
        msg = msg + "    }" + ln();
        msg = msg + "    public void setLatestPurchaseDatetime(Date latestPurchaseDatetime) {" + ln();
        msg = msg + "        _latestPurchaseDatetime = latestPurchaseDatetime;" + ln();
        msg = msg + "    }" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Alias Name]" + ln() + aliasName + ln();
        msg = msg + ln();
        msg = msg + "[Target Entity]" + ln() + entityType + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifyDerivedReferrerEntityPropertyNotFoundException(msg);
    }

    public static void throwSpecifyDerivedReferrerInvalidColumnSpecificationException(String function, String aliasName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The specified the column for derived-referrer was INVALID!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + " You should call specify().column[TargetColumn]() only once." + ln();
        msg = msg + " (If your function is count(), the target column should be primary key.)" + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            // *No! It's empty!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();" + ln();
        msg = msg + "            subCB.specify().columnPurchaseCount(); // *No! It's duplicated!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime(); // *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Function Method]" + ln() + xconvertFunctionToMethod(function) + ln();
        msg = msg + "[Alias Name]" + ln() + aliasName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifyDerivedReferrerInvalidColumnSpecificationException(msg);
    }

    public static void throwSpecifyDerivedReferrerUnmatchedColumnTypeException(String function,
            String deriveColumnName, Class<?> deriveColumnType) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The type of the specified the column unmatched with the function!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should confirm the list as follow:" + ln();
        msg = msg + "    count() : String, Number, Date *with distinct same" + ln();
        msg = msg + "    max()   : String, Number, Date" + ln();
        msg = msg + "    min()   : String, Number, Date" + ln();
        msg = msg + "    sum()   : Number" + ln();
        msg = msg + "    avg()   : Number" + ln();
        msg = msg + ln();
        msg = msg + "[Function]" + ln() + function + ln();
        msg = msg + ln();
        msg = msg + "[Derive Column]" + ln() + deriveColumnName + "(" + deriveColumnType.getName() + ")" + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifyDerivedReferrerUnmatchedColumnTypeException(msg);
    }

    public static void throwDerivedReferrerInvalidForeignSpecificationException(String foreignPropertyName) { // Query one uses too 
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "You specified a foreign table column in spite of derived-referrer!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should specified a local table column at condition-bean for derived-referrer." + ln();
        msg = msg + "  For example(for SpecifyDerivedReferrer):" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivedPurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().specifyProduct().columnProductName(); // *No!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivedPurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();// *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Specified Foreign Property]" + ln() + foreignPropertyName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DerivedReferrerInvalidForeignSpecificationException(msg);
    }

    // -----------------------------------------------------
    //                             Specified Derived OrderBy
    //                             -------------------------
    public static void throwSpecifiedDerivedOrderByAliasNameNotFoundException(String aliasName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The aliasName was Not Found in specified alias names." + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should specified an alias name that is the same as one in specify-derived-referrer." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().specifyProduct().columnProductName(); // *No!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    cb.query().addSpecifiedDerivedOrderBy_Desc(\"WRONG_NAME_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();// *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    cb.query().addSpecifiedDerivedOrderBy_Desc(\"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Not Found Alias Name]" + ln() + aliasName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifiedDerivedOrderByAliasNameNotFoundException(msg);
    }

    // -----------------------------------------------------
    //                                Query Derived Referrer
    //                                ----------------------
    public static void throwQueryDerivedReferrerInvalidColumnSpecificationException(String function) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The specified the column for derived-referrer was INVALID!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + " You should call specify().column[TargetColumn]() only once." + ln();
        msg = msg + " (If your function is count(), the target column should be primary key.)" + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalarPurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            // *No! It's empty!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).greaterEqual(123);" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalarPurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();" + ln();
        msg = msg + "            subCB.specify().columnPurchaseCount(); // *No! It's duplicated!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).greaterEqual(123);" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalarPurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime(); // *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).greaterEqual(123);" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Function Method]" + ln() + xconvertFunctionToMethod(function) + ln();
        msg = msg + "* * * * * * * * * */";
        throw new QueryDerivedReferrerInvalidColumnSpecificationException(msg);
    }

    public static void throwQueryDerivedReferrerUnmatchedColumnTypeException(String function, String deriveColumnName,
            Class<?> deriveColumnType, Object value) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The type of the specified the column unmatched with the function or the parameter!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should confirm the list as follow:" + ln();
        msg = msg + "    count() : String, Number, Date *with distinct same" + ln();
        msg = msg + "    max()   : String, Number, Date" + ln();
        msg = msg + "    min()   : String, Number, Date" + ln();
        msg = msg + "    sum()   : Number" + ln();
        msg = msg + "    avg()   : Number" + ln();
        msg = msg + ln();
        msg = msg + "[Function Method]" + ln() + xconvertFunctionToMethod(function) + ln();
        msg = msg + ln();
        msg = msg + "[Derive Column]" + ln() + deriveColumnName + "(" + deriveColumnType.getName() + ")" + ln();
        msg = msg + ln();
        msg = msg + "[Parameter Type]" + ln() + (value != null ? value.getClass() : null) + ln();
        msg = msg + "* * * * * * * * * */";
        throw new QueryDerivedReferrerUnmatchedColumnTypeException(msg);
    }

    // -----------------------------------------------------
    //                                       Scalar SubQuery
    //                                       ---------------
    public static void throwScalarSubQueryInvalidForeignSpecificationException(String foreignPropertyName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "You specified a foreign table column in spite of derived-query!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should specified a local table column at condition-bean for derived-query." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalar_Equal().max(new SubQuery<MemberCB>() {" + ln();
        msg = msg + "        public void query(MemberCB subCB) {" + ln();
        msg = msg + "            subCB.specify().specifyMemberStatusName().columnDisplayOrder(); // *No!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalar_Equal().max(new SubQuery<MemberCB>() {" + ln();
        msg = msg + "        public void query(MemberCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnMemberBirthday();// *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Specified Foreign Property]" + ln() + foreignPropertyName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new ScalarSubQueryInvalidForeignSpecificationException(msg);
    }

    public static void throwScalarSubQueryInvalidColumnSpecificationException(String function) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The specified the column for derived-referrer was INVALID!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + " You should call specify().column[TargetColumn]() only once." + ln();
        msg = msg + " (If your function is count(), the target column should be primary key.)" + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalar_Equal().max(new SubQuery<MemberCB>() {" + ln();
        msg = msg + "        public void query(MemberCB subCB) {" + ln();
        msg = msg + "            // *No! It's empty!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalar_Equal().max(new SubQuery<MemberCB>() {" + ln();
        msg = msg + "        public void query(MemberCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnMemberBirthday();" + ln();
        msg = msg + "            subCB.specify().columnMemberName(); // *No! It's duplicated!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalar_Equal().max(new SubQuery<MemberCB>() {" + ln();
        msg = msg + "        public void query(MemberCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime(); // *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Function Method]" + ln() + xconvertFunctionToMethod(function) + ln();
        msg = msg + "* * * * * * * * * */";
        throw new ScalarSubQueryInvalidColumnSpecificationException(msg);
    }

    public static void throwScalarSubQueryUnmatchedColumnTypeException(String function, String deriveColumnName,
            Class<?> deriveColumnType) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The type of the specified the column unmatched with the function!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should confirm the list as follow:" + ln();
        msg = msg + "    max()   : String, Number, Date" + ln();
        msg = msg + "    min()   : String, Number, Date" + ln();
        msg = msg + "    sum()   : Number" + ln();
        msg = msg + "    avg()   : Number" + ln();
        msg = msg + ln();
        msg = msg + "[Function Method]" + ln() + xconvertFunctionToMethod(function) + ln();
        msg = msg + ln();
        msg = msg + "[Derive Column]" + ln() + deriveColumnName + "(" + deriveColumnType.getName() + ")" + ln();
        msg = msg + "* * * * * * * * * */";
        throw new ScalarSubQueryUnmatchedColumnTypeException(msg);
    }

    // -----------------------------------------------------
    //                                          Column Query
    //                                          ------------
    public static void throwColumnQueryInvalidColumnSpecificationException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The specified the column for column query was INVALID!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + " You should call specify().column[TargetColumn]() only once." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.columnQuery(new SpecifyQuery<MemberCB>() {" + ln();
        msg = msg + "        public void specify(MemberCB cb) {" + ln();
        msg = msg + "            // *No! It's empty!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).lessThan...;" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.columnQuery(new SpecifyQuery<MemberCB>() {" + ln();
        msg = msg + "        public void specify(MemberCB cb) {" + ln();
        msg = msg + "            cb.specify().columnMemberName();" + ln();
        msg = msg + "            cb.specify().columnBirthdate();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).lessThan...;" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.columnQuery(new SpecifyQuery<MemberCB>() {" + ln();
        msg = msg + "        public void specify(MemberCB cb) {" + ln();
        msg = msg + "            cb.specify().columnBirthdate();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).lessThan(new SpecifyQuery<MemberCB>() {" + ln();
        msg = msg + "        public void specify(MemberCB cb) {" + ln();
        msg = msg + "            cb.specify().columnFormalizedDatetime();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + "* * * * * * * * * */";
        throw new ColumnQueryInvalidColumnSpecificationException(msg);
    }

    // -----------------------------------------------------
    //                                       Function Helper
    //                                       ---------------
    protected static String xconvertFunctionToMethod(String function) {
        if (function != null && function.contains("(")) { // For example 'count(distinct'
            int index = function.indexOf("(");
            String front = function.substring(0, index);
            if (function.length() > front.length() + "(".length()) {
                String rear = function.substring(index + "(".length());
                function = front + initCap(rear);
            } else {
                function = front;
            }
        }
        return function + "()";
    }

    // ===================================================================================
    //                                                                          DisplaySql
    //                                                                          ==========
    public static String convertConditionBean2DisplaySql(SqlAnalyzerFactory factory, ConditionBean cb,
            String logDateFormat, String logTimestampFormat) {
        final String twoWaySql = cb.getSqlClause().getClause();
        return SqlAnalyzer.convertTwoWaySql2DisplaySql(factory, twoWaySql, cb, logDateFormat, logTimestampFormat);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    /**
     * Get the value of line separator.
     * @return The value of line separator. (NotNull)
     */
    protected static String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    protected static String initCap(String str) {
        return DfStringUtil.initCap(str);
    }

    protected static String initUncap(String str) {
        return DfStringUtil.initUncap(str);
    }
}
