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
package org.seasar.robot.dbflute.exception.thrower;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.chelper.HpCBPurpose;
import org.seasar.robot.dbflute.cbean.chelper.HpInvalidQueryInfo;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.exception.ColumnQueryInvalidColumnSpecificationException;
import org.seasar.robot.dbflute.exception.InvalidQueryRegisteredException;
import org.seasar.robot.dbflute.exception.OrScopeQueryAndPartAlreadySetupException;
import org.seasar.robot.dbflute.exception.OrScopeQueryAndPartNotOrScopeException;
import org.seasar.robot.dbflute.exception.OrderByIllegalPurposeException;
import org.seasar.robot.dbflute.exception.PagingPageSizeNotPlusException;
import org.seasar.robot.dbflute.exception.QueryDerivedReferrerInvalidColumnSpecificationException;
import org.seasar.robot.dbflute.exception.QueryDerivedReferrerUnmatchedColumnTypeException;
import org.seasar.robot.dbflute.exception.QueryIllegalPurposeException;
import org.seasar.robot.dbflute.exception.RequiredOptionNotFoundException;
import org.seasar.robot.dbflute.exception.ScalarConditionInvalidColumnSpecificationException;
import org.seasar.robot.dbflute.exception.ScalarConditionUnmatchedColumnTypeException;
import org.seasar.robot.dbflute.exception.ScalarSelectInvalidColumnSpecificationException;
import org.seasar.robot.dbflute.exception.SetupSelectAfterUnionException;
import org.seasar.robot.dbflute.exception.SetupSelectIllegalPurposeException;
import org.seasar.robot.dbflute.exception.SpecifiedDerivedOrderByAliasNameNotFoundException;
import org.seasar.robot.dbflute.exception.SpecifyColumnNotSetupSelectColumnException;
import org.seasar.robot.dbflute.exception.SpecifyColumnTwoOrMoreColumnException;
import org.seasar.robot.dbflute.exception.SpecifyColumnWithDerivedReferrerException;
import org.seasar.robot.dbflute.exception.SpecifyDerivedReferrerEntityPropertyNotFoundException;
import org.seasar.robot.dbflute.exception.SpecifyDerivedReferrerIllegalPurposeException;
import org.seasar.robot.dbflute.exception.SpecifyDerivedReferrerInvalidAliasNameException;
import org.seasar.robot.dbflute.exception.SpecifyDerivedReferrerInvalidColumnSpecificationException;
import org.seasar.robot.dbflute.exception.SpecifyDerivedReferrerTwoOrMoreException;
import org.seasar.robot.dbflute.exception.SpecifyDerivedReferrerUnmatchedColumnTypeException;
import org.seasar.robot.dbflute.exception.SpecifyIllegalPurposeException;
import org.seasar.robot.dbflute.exception.SpecifyRelationIllegalPurposeException;
import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.DfTypeUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * @author jflute
 */
public class ConditionBeanExceptionThrower {

    // ===================================================================================
    //                                                                       Set up Select
    //                                                                       =============
    public void throwSetupSelectIllegalPurposeException(HpCBPurpose purpose, ConditionBean baseCB,
            String foreignPropertyName) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The purpose was illegal for setting up select.");
        br.addItem("Advice");
        br.addElement("This condition-bean is not allowed to set up select.");
        br.addElement("Because this is for " + purpose + ".");
        br.addElement("For example:");
        br.addElement("  (x): (ExistsReferrer)");
        br.addElement("    cb.query().existsXxxList(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.setupSelect_Product(); // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (x): (Union)");
        br.addElement("    cb.union(new UnionQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB unionCB) {");
        br.addElement("            unionCB.setupSelect_MemberStatus(); // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (o): (Normal Use)");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.setupSelect_MemberStatus(); // OK");
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        br.addItem("Setup Relation");
        br.addElement(foreignPropertyName);
        final String msg = br.buildExceptionMessage();
        throw new SetupSelectIllegalPurposeException(msg);
    }

    public void throwSetupSelectAfterUnionException(ConditionBean baseCB, String foreignPropertyName) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The setup-select was called after union.");
        br.addItem("Advice");
        br.addElement("The setup-select should be called before calling union().");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.query().setXxx...;");
        br.addElement("    cb.union(new UnionQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB unionCB) {");
        br.addElement("            unionCB.query().setXxx...;");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("    cb.setupSelect_MemberStatus(); // *no!");
        br.addElement("  (o):");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.setupSelect_MemberStatus(); // you should call here");
        br.addElement("    cb.query().setXxx...;");
        br.addElement("    cb.union(new UnionQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB unionCB) {");
        br.addElement("            unionCB.query().setXxx...;");
        br.addElement("        }");
        br.addElement("    });");
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        br.addItem("Setup Relation");
        br.addElement(foreignPropertyName);
        final String msg = br.buildExceptionMessage();
        throw new SetupSelectAfterUnionException(msg);
    }

    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    public void throwSpecifyIllegalPurposeException(HpCBPurpose purpose, ConditionBean baseCB) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The purpose was illegal to specify.");
        br.addItem("Advice");
        br.addElement("This condition-bean is not allowed to specify.");
        br.addElement("Because this is for " + purpose + ".");
        br.addElement("For example:");
        br.addElement("  (x): (ExistsReferrer)");
        br.addElement("    cb.query().existsPurchaseList(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.specify()... // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (x): (Union)");
        br.addElement("    cb.union(new UnionQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB unionCB) {");
        br.addElement("            unionCB.specify()... // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (o): (ExistsReferrer)");
        br.addElement("    cb.query().existsPurchaseList(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.query().setPurchaseCount_GreaterEqual(3); // OK");
        br.addElement("        }");
        br.addElement("    });");
        // don't use displaySql because of illegal CB's state
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        final String msg = br.buildExceptionMessage();
        throw new SpecifyIllegalPurposeException(msg);
    }

    public void throwSpecifyColumnTwoOrMoreColumnException(HpCBPurpose purpose, ConditionBean baseCB, String columnName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("You specified two or more columns!");
        br.addItem("Advice");
        br.addElement("This condition-bean is not allowed to specify two or more columns.");
        br.addElement("Because the conditoin-bean is for " + purpose + ".");
        br.addElement("For example:");
        br.addElement("  (x): (DerivedReferrer)");
        br.addElement("    cb.specify().derivedPurchaseList().max(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.specify().columnPurchaseCount();");
        br.addElement("            subCB.specify().columnPurchasePrice(); // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (x): (ColumnQuery)");
        br.addElement("    cb.columnQuery(new SpecifyQuery<MemberCB>() {");
        br.addElement("        public void specify(MemberCB cb) {");
        br.addElement("            cb.specify().columnMemberName();");
        br.addElement("            cb.specify().columnBirthdate(); // *no!");
        br.addElement("        }");
        br.addElement("    })...");
        br.addElement("  (o): (DerivedReferrer)");
        br.addElement("    cb.specify().derivedPurchaseList().max(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.specify().columnPurchaseCount();");
        br.addElement("        }");
        br.addElement("    });");
        // don't use displaySql because of illegal CB's state
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        br.addItem("Specified Column");
        br.addElement(baseCB.getTableDbName() + "." + columnName);
        final String msg = br.buildExceptionMessage();
        throw new SpecifyColumnTwoOrMoreColumnException(msg);
    }

    public void throwSpecifyColumnNotSetupSelectColumnException(ConditionBean baseCB, String columnName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("You specified the column that had not been set up!");
        br.addItem("Advice");
        br.addElement("You should call setupSelect_[ForeignTable]()");
        br.addElement("before calling specify[ForeignTable]().column[TargetColumn]().");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.specify().specifyMemberStatus().columnMemberStatusName(); // *no!");
        br.addElement("  (o):");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.setupSelect_MemberStatus(); // *point!");
        br.addElement("    cb.specify().specifyMemberStatus().columnMemberStatusName();");
        // don't use displaySql because of illegal CB's state
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        br.addItem("Specified Column");
        br.addElement(baseCB.getTableDbName() + "." + columnName);
        final String msg = br.buildExceptionMessage();
        throw new SpecifyColumnNotSetupSelectColumnException(msg);
    }

    public void throwSpecifyColumnWithDerivedReferrerException(HpCBPurpose purpose, ConditionBean baseCB,
            String columnName, String referrerName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("You specified both SpecifyColumn and (Specify)DerivedReferrer!");
        br.addItem("Advice");
        br.addElement("This condition-bean is not allowed to specify both functions.");
        br.addElement("Because the conditoin-bean is for " + purpose + ".");
        br.addElement("For example:");
        br.addElement("  (x): (ColumnQuery)");
        br.addElement("    cb.columnQuery(new SpecifyQuery<MemberCB> {");
        br.addElement("        public void query(MemberCB cb) {");
        br.addElement("            cb.specify().columnBirthdate();");
        br.addElement("            cb.specify().derivedPurchaseList().max(...); // *no!");
        br.addElement("        }");
        br.addElement("    }).greaterEqual(...);");
        br.addElement("  (o): (ColumnQuery)");
        br.addElement("    cb.columnQuery(new SpecifyQuery<MemberCB> {");
        br.addElement("        public void query(MemberCB cb) {");
        br.addElement("            cb.specify().columnBirthdate(); // OK");
        br.addElement("        }");
        br.addElement("    }).greaterEqual(...);");
        br.addElement("  (o): (ColumnQuery)");
        br.addElement("    cb.columnQuery(new SpecifyQuery<MemberCB> {");
        br.addElement("        public void query(MemberCB cb) {");
        br.addElement("            cb.specify().derivedPurchaseList().max(...); // OK");
        br.addElement("        }");
        br.addElement("    }).greaterEqual(...);");
        // don't use displaySql because of illegal CB's state
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        br.addItem("Specified Column");
        br.addElement(baseCB.getTableDbName() + "." + columnName);
        br.addItem("Derived Referrer");
        br.addElement(referrerName);
        final String msg = br.buildExceptionMessage();
        throw new SpecifyColumnWithDerivedReferrerException(msg);
    }

    public void throwSpecifyRelationIllegalPurposeException(HpCBPurpose purpose, ConditionBean baseCB,
            String relationName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("You specified the relation in the purpose that is not allowed to do it.");
        br.addItem("Advice");
        br.addElement("This condition-bean is not allowed to specify a relation.");
        br.addElement("Because this is for " + purpose + ".");
        br.addElement("For example:");
        br.addElement("  (x): (ScalarSelect)");
        br.addElement("    memberBhv.scalarSelect(Date.class).max(new ScalarQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB cb) {");
        br.addElement("            cb.specify().specifyMemberStatus().col.. // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (x): (ScalarCondition)");
        br.addElement("    cb.query().scalar_Equal().max(Date.class).max(new SubQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB subCB) {");
        br.addElement("            subCB.specify().specifyMemberStatusName().col..; // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (x): (VaryingUpdate)");
        br.addElement("    UpdateOption option = new UpdateOption().self(new SpecifyQuery<MemberCB>() {");
        br.addElement("        public void specify(MemberCB cb) {");
        br.addElement("            cb.specify().specifyMemberStatus().col.. // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (o): (ScalarSelect)");
        br.addElement("    memberBhv.scalarSelect(Date.class).max(new ScalarQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB cb) {");
        br.addElement("            cb.specify().columnBirthdate(); // OK");
        br.addElement("        }");
        br.addElement("    });");
        // don't use displaySql because of illegal CB's state
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        br.addItem("Specified Relation");
        br.addElement(relationName);
        final String msg = br.buildExceptionMessage();
        throw new SpecifyRelationIllegalPurposeException(msg);
    }

    public void throwSpecifyDerivedReferrerIllegalPurposeException(HpCBPurpose purpose, ConditionBean baseCB,
            String referrerName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The purpose was illegal for derived-referrer specification.");
        br.addItem("Advice");
        br.addElement("This condition-bean is not allowed to specify a derived referrer.");
        br.addElement("Because this is for " + purpose + ".");
        br.addElement("For example:");
        br.addElement("  (x): (DerivedReferrer)");
        br.addElement("    cb.specify().derivedPurchaseList().max(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.specify().derivedPurchaseList()...; // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (o): (DerivedReferrer)");
        br.addElement("    cb.specify().derivedPurchaseList().max(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.specify().columnPurchaseCount(); // OK");
        br.addElement("        }");
        br.addElement("    });");
        // don't use displaySql because of illegal CB's state
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        br.addItem("Specified Referrer");
        br.addElement(referrerName);
        final String msg = br.buildExceptionMessage();
        throw new SpecifyDerivedReferrerIllegalPurposeException(msg);
    }

    public void throwSpecifyDerivedReferrerTwoOrMoreException(HpCBPurpose purpose, ConditionBean baseCB,
            String referrerName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The two-or-more derived-referrers was specifed.");
        br.addItem("Advice");
        br.addElement("This condition-bean is not allowed to specify two-or-more derived referrers.");
        br.addElement("Because this is for " + purpose + ".");
        br.addElement("For example:");
        br.addElement("  (x): (ColumnQuery)");
        br.addElement("    cb.columnQuery(new SpecifyQuery<MemberCB> {");
        br.addElement("        public void query(MemberCB cb) {");
        br.addElement("            cb.specify().derivedPurchaseList().max(...);");
        br.addElement("            cb.specify().derivedPurchaseList().max(...); // *no!");
        br.addElement("        }");
        br.addElement("    }).greaterEqual(...);");
        br.addElement("  (o): (ColumnQuery)");
        br.addElement("    cb.columnQuery(new SpecifyQuery<MemberCB> {");
        br.addElement("        public void query(MemberCB cb) {");
        br.addElement("            cb.specify().derivedPurchaseList().max(...); // OK");
        br.addElement("        }");
        br.addElement("    }).greaterEqual(...);");
        // don't use displaySql because of illegal CB's state
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        br.addItem("Specified Referrer");
        br.addElement(referrerName);
        br.addItem("Already Registered Referrer");
        br.addElement(baseCB.getSqlClause().getSpecifiedDerivingAliasList());
        final String msg = br.buildExceptionMessage();
        throw new SpecifyDerivedReferrerTwoOrMoreException(msg);
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    public void throwScalarSelectInvalidColumnSpecificationException(ConditionBean cb, Class<?> resultType) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The specified column for scalar select was invalid.");
        br.addItem("Advice");
        br.addElement("You should call specify().column[TargetColumn]() only once.");
        br.addElement("For example:");
        br.addElement("");
        br.addElement("  (x): (empty)");
        br.addElement("    memberBhv.scalarSelect(Date.class).max(new ScalarQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB cb) {");
        br.addElement("            // *No! It's empty!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("");
        br.addElement("  (x): (duplicated)");
        br.addElement("    memberBhv.scalarSelect(Date.class).max(new ScalarQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB cb) {");
        br.addElement("            cb.specify().columnMemberBirthday();");
        br.addElement("            cb.specify().columnRegisterDatetime(); // *No! It's duplicated!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("");
        br.addElement("  (o):");
        br.addElement("    memberBhv.scalarSelect(Date.class).max(new ScalarQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB cb) {");
        br.addElement("            cb.specify().columnMemberBirthday(); // *point");
        br.addElement("        }");
        br.addElement("    });");
        br.addItem("ConditionBean"); // don't use displaySql because of illegal CB's state
        br.addElement(cb.getClass().getName());
        br.addItem("Result Type");
        br.addElement(resultType.getName());
        final String msg = br.buildExceptionMessage();
        throw new ScalarSelectInvalidColumnSpecificationException(msg);
    }

    // ===================================================================================
    //                                                            Specify Derived Referrer
    //                                                            ========================
    public void throwSpecifyDerivedReferrerInvalidAliasNameException(ConditionQuery localCQ) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The alias name for specify derived-referrer was INVALID!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should set valid alias name. {NotNull, NotEmpty}" + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "      MemberCB cb = new MemberCB();" + ln();
        msg = msg + "      cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "          public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "              subCB.specify().columnPurchaseDatetime();" + ln();
        msg = msg + "          }" + ln();
        msg = msg + "      }, null); // *No! {null, \"\", \"   \"} are NG!" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "      MemberCB cb = new MemberCB();" + ln();
        msg = msg + "      cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "          public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "              subCB.specify().columnPurchaseDatetime();" + ln();
        msg = msg + "          }" + ln();
        msg = msg + "      }, \"LATEST_PURCHASE_DATETIME\"); // *Point!" + ln();
        msg = msg + ln();
        msg = msg + "[Local Table]" + ln() + localCQ.getTableDbName() + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifyDerivedReferrerInvalidAliasNameException(msg);
    }

    public void throwSpecifyDerivedReferrerEntityPropertyNotFoundException(String aliasName, Class<?> entityType) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "A property for derived-referrer was NOT FOUND in the entity!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should implement a property(setter and getter) in the entity." + ln();
        msg = msg + "Or you should confirm whether the alias name has typo or not." + ln();
        msg = msg + "For example:" + ln();
        msg = msg + "  ConditionBean Invoking:" + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + ln();
        msg = msg + "  Extended Entity:" + ln();
        msg = msg + "    // At the entity of Purchase..." + ln();
        msg = msg + "    protected Date _latestPurchaseDatetime;" + ln();
        msg = msg + "    public Date getLatestPurchaseDatetime() {" + ln();
        msg = msg + "        return _latestPurchaseDatetime;" + ln();
        msg = msg + "    }" + ln();
        msg = msg + "    public void setLatestPurchaseDatetime(Date latestPurchaseDatetime) {" + ln();
        msg = msg + "        _latestPurchaseDatetime = latestPurchaseDatetime;" + ln();
        msg = msg + "    }" + ln();
        msg = msg + ln();
        msg = msg + "[Alias Name]" + ln() + aliasName + ln();
        msg = msg + ln();
        msg = msg + "[Target Entity]" + ln() + entityType + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifyDerivedReferrerEntityPropertyNotFoundException(msg);
    }

    public void throwSpecifyDerivedReferrerInvalidColumnSpecificationException(String function, String aliasName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The specified the column for derived-referrer was INVALID!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should call specify().column[TargetColumn]() only once." + ln();
        msg = msg + "(If your function is count(), the target column should be primary key.)" + ln();
        msg = msg + "For example:" + ln();
        msg = msg + "  (x): (empty)" + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            // *No! It's empty!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + ln();
        msg = msg + "  (x): (duplicated)" + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();" + ln();
        msg = msg + "            subCB.specify().columnPurchaseCount(); // *No! It's duplicated!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + ln();
        msg = msg + "  (o):" + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime(); // *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + ln();
        msg = msg + "[Function Method]" + ln() + xconvertFunctionToMethod(function) + ln();
        msg = msg + "[Alias Name]" + ln() + aliasName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifyDerivedReferrerInvalidColumnSpecificationException(msg);
    }

    public void throwSpecifyDerivedReferrerUnmatchedColumnTypeException(String function, String derivedColumnDbName,
            Class<?> derivedColumnType) {
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
        msg = msg + "[Derive Column]" + ln() + derivedColumnDbName + "(" + derivedColumnType.getName() + ")" + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifyDerivedReferrerUnmatchedColumnTypeException(msg);
    }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public void throwSpecifiedDerivedOrderByAliasNameNotFoundException(String aliasName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The aliasName was not found in specified alias names." + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should specified an alias name that is the same as one in specify-derived-referrer." + ln();
        msg = msg + "For example:" + ln();
        msg = msg + "  (x):" + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().specifyProduct().columnProductName(); // *No!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    cb.query().addSpecifiedDerivedOrderBy_Desc(\"WRONG_NAME_DATETIME\");" + ln();
        msg = msg + ln();
        msg = msg + "  (o):" + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime(); // *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    cb.query().addSpecifiedDerivedOrderBy_Desc(\"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + ln();
        msg = msg + "[not found Alias Name]" + ln() + aliasName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifiedDerivedOrderByAliasNameNotFoundException(msg);
    }

    // -----------------------------------------------------
    //                                Query Derived Referrer
    //                                ----------------------
    public void throwQueryDerivedReferrerInvalidColumnSpecificationException(String function) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The specified the column for derived-referrer was INVALID!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + " You should call specify().column[TargetColumn]() only once." + ln();
        msg = msg + " (If your function is count(), the target column should be primary key.)" + ln();
        msg = msg + "For example:" + ln();
        msg = msg + "  (x):" + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalarPurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            // *No! It's empty!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).greaterEqual(123);" + ln();
        msg = msg + ln();
        msg = msg + "  (x):" + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalarPurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();" + ln();
        msg = msg + "            subCB.specify().columnPurchaseCount(); // *No! It's duplicated!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).greaterEqual(123);" + ln();
        msg = msg + ln();
        msg = msg + "  (o):" + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalarPurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime(); // *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).greaterEqual(123);" + ln();
        msg = msg + ln();
        msg = msg + "[Function Method]" + ln() + xconvertFunctionToMethod(function) + ln();
        msg = msg + "* * * * * * * * * */";
        throw new QueryDerivedReferrerInvalidColumnSpecificationException(msg);
    }

    public void throwQueryDerivedReferrerUnmatchedColumnTypeException(String function, String derivedColumnDbName,
            Class<?> derivedColumnType, Object value) {
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
        msg = msg + "[Derived Column]" + ln() + derivedColumnDbName + "(" + derivedColumnType.getName() + ")" + ln();
        msg = msg + ln();
        msg = msg + "[Parameter Type]" + ln() + (value != null ? value.getClass() : null) + ln();
        msg = msg + "* * * * * * * * * */";
        throw new QueryDerivedReferrerUnmatchedColumnTypeException(msg);
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public void throwQueryIllegalPurposeException(HpCBPurpose purpose, ConditionBean baseCB) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The purpose was illegal for query.");
        br.addItem("Advice");
        br.addElement("This condition-bean is not allowed to set query.");
        br.addElement("(contains OrScopeQuery and ColumnQuery)");
        br.addElement("Because this is for " + purpose + ".");
        br.addElement("For example:");
        br.addElement("  (x): (ColumnQuery)");
        br.addElement("    cb.columnQuery(new SpecifyQuery<MemberCB>() {");
        br.addElement("        public void specify(MemberCB cb) {");
        br.addElement("            cb.query().set...();  // *no!");
        br.addElement("            cb.columnQuery(...);  // *no!");
        br.addElement("            cb.orScopeQuery(...); // *no!");
        br.addElement("        }");
        br.addElement("    })...");
        br.addElement("  (x): (VaryingUpdate)");
        br.addElement("    UpdateOption option = new UpdateOption().self(new SpecifyQuery<MemberCB>() {");
        br.addElement("        public void specify(MemberCB cb) {");
        br.addElement("            cb.query().set...();  // *no!");
        br.addElement("            cb.columnQuery(...);  // *no!");
        br.addElement("            cb.orScopeQuery(...); // *no!");
        br.addElement("        }");
        br.addElement("    });");
        // don't use displaySql because of illegal CB's state
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        final String msg = br.buildExceptionMessage();
        throw new QueryIllegalPurposeException(msg);
    }

    public void throwInvalidQueryRegisteredException(HpInvalidQueryInfo invalidQueryInfo) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("An invalid query was registered. (check is working)");
        br.addItem("Advice");
        br.addElement("You should not set an invalid query when the check is valid.");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.checkInvalidQuery();");
        br.addElement("    cb.query().setMemberId_Equal(null); // exception");
        br.addElement("  (o):");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.checkInvalidQuery();");
        br.addElement("    cb.query().setMemberId_Equal(3);");
        br.addElement("  (o):");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.query().setMemberId_Equal(null);");
        br.addItem("Invalid Query");
        br.addElement(invalidQueryInfo.buildDisplay());
        final String msg = br.buildExceptionMessage();
        throw new InvalidQueryRegisteredException(msg);
    }

    public void throwLikeSearchOptionNotFoundException(String colName, String value, DBMeta dbmeta) {
        final String capPropName = initCap(dbmeta.findPropertyName(colName));
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The likeSearchOption was not found! (Should not be null!)" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your method call:" + ln();
        final String beanName = DfTypeUtil.toClassTitle(this);
        final String methodName = "set" + capPropName + "_LikeSearch('" + value + "', likeSearchOption);";
        msg = msg + "    " + beanName + "." + methodName + ln();
        msg = msg + "* * * * * * * * * */" + ln();
        throw new RequiredOptionNotFoundException(msg);
    }

    public void throwOrderByIllegalPurposeException(HpCBPurpose purpose, String tableDbName, String columnName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The purpose was illegal for order-by.");
        br.addItem("Advice");
        br.addElement("This condition-bean is not allowed to order.");
        br.addElement("Because this is for " + purpose + ".");
        br.addElement("For example:");
        br.addElement("  (x): (ExistsReferrer)");
        br.addElement("    cb.query().existsXxxList(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.query().addOrderBy...; // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (x): (Union)");
        br.addElement("    cb.union(new UnionQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB unionCB) {");
        br.addElement("            unionCB.query().addOrderBy...; // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (x): (DerivedReferrer)");
        br.addElement("    cb.specify().derivedPurchaseList().max(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.query().addOrderBy...; // *no!");
        br.addElement("        }");
        br.addElement("    });");
        // don't use displaySql because of illegal CB's state
        br.addItem("Order-By Column");
        br.addElement(tableDbName + "." + columnName);
        final String msg = br.buildExceptionMessage();
        throw new OrderByIllegalPurposeException(msg);
    }

    // ===================================================================================
    //                                                                        Column Query
    //                                                                        ============
    public void throwColumnQueryInvalidColumnSpecificationException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The specified the column for column query was INVALID!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should call specify().column[TargetColumn]() only once." + ln();
        msg = msg + "For example:" + ln();
        msg = msg + "  (x):" + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.columnQuery(new SpecifyQuery<MemberCB>() {" + ln();
        msg = msg + "        public void specify(MemberCB cb) {" + ln();
        msg = msg + "            // *No! It's empty!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).lessThan...;" + ln();
        msg = msg + ln();
        msg = msg + "  (x):" + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.columnQuery(new SpecifyQuery<MemberCB>() {" + ln();
        msg = msg + "        public void specify(MemberCB cb) {" + ln();
        msg = msg + "            cb.specify().columnMemberName();" + ln();
        msg = msg + "            cb.specify().columnBirthdate();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).lessThan...;" + ln();
        msg = msg + ln();
        msg = msg + "  (o):" + ln();
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
        msg = msg + "* * * * * * * * * */";
        throw new ColumnQueryInvalidColumnSpecificationException(msg);
    }

    // -----------------------------------------------------
    //                                       Function Helper
    //                                       ---------------
    protected String xconvertFunctionToMethod(String function) {
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
    //                                                                        OrScopeQuery
    //                                                                        ============
    public void throwOrScopeQueryAndPartNotOrScopeException(ConditionBean cb) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The or-scope query was not set up.");
        br.addItem("Advice");
        br.addElement("The and-part of or-scope query works only in or-scope query.");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    cb.orScopeQueryAndPart(new AndQuery<MemberCB>() { // *no!");
        br.addElement("        public void query(MemberCB andCB) {");
        br.addElement("            ...");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (o):");
        br.addElement("    cb.orScopeQuery(new OrQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB orCB) {");
        br.addElement("            orCB.orScopeQueryAndPart(new AndQuery(MemberCB andCB) {");
        br.addElement("                public void query(MemberCB andCB) {");
        br.addElement("                    andCB.query().set...();");
        br.addElement("                    andCB.query().set...();");
        br.addElement("                }");
        br.addElement("            });");
        br.addElement("        }");
        br.addElement("    });");
        br.addItem("ConditionBean");
        br.addElement(cb.getClass().getName());
        final String msg = br.buildExceptionMessage();
        throw new OrScopeQueryAndPartNotOrScopeException(msg);
    }

    public void throwOrScopeQueryAndPartAlreadySetupException(ConditionBean cb) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The and-part of or-scope has already been set up.");
        br.addItem("Advice");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    cb.orScopeQuery(new OrQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB orCB) {");
        br.addElement("            orCB.orScopeQueryAndPart(new AndQuery(MemberCB andCB) {");
        br.addElement("                public void query(MemberCB andCB) {");
        br.addElement("                    andCB.orScopeQueryAndPart(new AndQuery(MemberCB andCB) { // *no!");
        br.addElement("                        ...");
        br.addElement("                    }");
        br.addElement("                }");
        br.addElement("            });");
        br.addElement("        }");
        br.addElement("    });");
        br.addItem("ConditionBean");
        br.addElement(cb.getClass().getName());
        final String msg = br.buildExceptionMessage();
        throw new OrScopeQueryAndPartAlreadySetupException(msg);
    }

    // ===================================================================================
    //                                                                    Scalar Condition
    //                                                                    ================
    public void throwScalarConditionInvalidColumnSpecificationException(String function) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The specified the column for scalar-condition was INVALID!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + " You should call specify().column[TargetColumn]() only once." + ln();
        msg = msg + " (If your function is count(), the target column should be primary key.)" + ln();
        msg = msg + "For example:" + ln();
        msg = msg + "  (x): (empty)" + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalar_Equal().max(new SubQuery<MemberCB>() {" + ln();
        msg = msg + "        public void query(MemberCB subCB) {" + ln();
        msg = msg + "            // *No! It's empty!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + ln();
        msg = msg + "  (x): (duplicated)" + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalar_Equal().max(new SubQuery<MemberCB>() {" + ln();
        msg = msg + "        public void query(MemberCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnMemberBirthday();" + ln();
        msg = msg + "            subCB.specify().columnMemberName(); // *No! It's duplicated!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + ln();
        msg = msg + "  (o):" + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalar_Equal().max(new SubQuery<MemberCB>() {" + ln();
        msg = msg + "        public void query(MemberCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime(); // *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + ln();
        msg = msg + "[Function Method]" + ln() + xconvertFunctionToMethod(function) + ln();
        msg = msg + "* * * * * * * * * */";
        throw new ScalarConditionInvalidColumnSpecificationException(msg);
    }

    public void throwScalarConditionUnmatchedColumnTypeException(String function, String deriveColumnName,
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
        throw new ScalarConditionUnmatchedColumnTypeException(msg);
    }

    // ===================================================================================
    //                                                                              Paging
    //                                                                              ======
    public void throwPagingPageSizeNotPlusException(ConditionBean cb, int pageSize, int pageNumber) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Page size for paging should not be minus or zero.");
        br.addItem("Advice");
        br.addElement("Confirm the value of your parameter 'pageSize'.");
        br.addElement("The first parameter of paging() should be a plus value.");
        br.addElement("For example:");
        br.addElement("  (x): cb.paging(0, 1);");
        br.addElement("  (x): cb.paging(-3, 2);");
        br.addElement("  (o): cb.paging(20, 3);");
        br.addItem("ConditionBean");
        br.addElement(cb.getClass().getName());
        br.addItem("Page Size");
        br.addElement(pageSize);
        br.addItem("Page Number");
        br.addElement(pageNumber);
        final String msg = br.buildExceptionMessage();
        throw new PagingPageSizeNotPlusException(msg);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    /**
     * Get the value of line separator.
     * @return The value of line separator. (NotNull)
     */
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    protected String initCap(String str) {
        return Srl.initCap(str);
    }

    protected String initUncap(String str) {
        return Srl.initUncap(str);
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected ExceptionMessageBuilder createExceptionMessageBuilder() {
        return new ExceptionMessageBuilder();
    }
}
