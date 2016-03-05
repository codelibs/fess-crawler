/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.db.cbean.cq.bs;

import java.util.Map;

import org.dbflute.cbean.*;
import org.dbflute.cbean.chelper.*;
import org.dbflute.cbean.coption.*;
import org.dbflute.cbean.cvalue.ConditionValue;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.dbflute.exception.IllegalConditionBeanOperationException;
import org.codelibs.fess.crawler.db.cbean.cq.ciq.*;
import org.codelibs.fess.crawler.db.cbean.*;
import org.codelibs.fess.crawler.db.cbean.cq.*;

/**
 * The base condition-query of ACCESS_RESULT_DATA.
 * @author DBFlute(AutoGenerator)
 */
public class BsAccessResultDataCQ extends AbstractBsAccessResultDataCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected AccessResultDataCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsAccessResultDataCQ(ConditionQuery referrerQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br>
     * {select ... from ... left outer join (select * from ACCESS_RESULT_DATA) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #CC4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public AccessResultDataCIQ inline() {
        if (_inlineQuery == null) { _inlineQuery = xcreateCIQ(); }
        _inlineQuery.xsetOnClause(false); return _inlineQuery;
    }

    protected AccessResultDataCIQ xcreateCIQ() {
        AccessResultDataCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected AccessResultDataCIQ xnewCIQ() {
        return new AccessResultDataCIQ(xgetReferrerQuery(), xgetSqlClause(), xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br>
     * {select ... from ... left outer join ACCESS_RESULT_DATA on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #CC4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public AccessResultDataCIQ on() {
        if (isBaseQuery()) { throw new IllegalConditionBeanOperationException("OnClause for local table is unavailable!"); }
        AccessResultDataCIQ inlineQuery = inline(); inlineQuery.xsetOnClause(true); return inlineQuery;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    protected ConditionValue _id;
    public ConditionValue xdfgetId()
    { if (_id == null) { _id = nCV(); }
      return _id; }
    protected ConditionValue xgetCValueId() { return xdfgetId(); }

    /** 
     * Add order-by as ascend. <br>
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Id_Asc() { regOBA("ID"); return this; }

    /**
     * Add order-by as descend. <br>
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Id_Desc() { regOBD("ID"); return this; }

    protected ConditionValue _transformerName;
    public ConditionValue xdfgetTransformerName()
    { if (_transformerName == null) { _transformerName = nCV(); }
      return _transformerName; }
    protected ConditionValue xgetCValueTransformerName() { return xdfgetTransformerName(); }

    /** 
     * Add order-by as ascend. <br>
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_TransformerName_Asc() { regOBA("TRANSFORMER_NAME"); return this; }

    /**
     * Add order-by as descend. <br>
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_TransformerName_Desc() { regOBD("TRANSFORMER_NAME"); return this; }

    protected ConditionValue _data;
    public ConditionValue xdfgetData()
    { if (_data == null) { _data = nCV(); }
      return _data; }
    protected ConditionValue xgetCValueData() { return xdfgetData(); }

    /** 
     * Add order-by as ascend. <br>
     * DATA: {LONGBLOB(2147483647)}
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Data_Asc() { regOBA("DATA"); return this; }

    /**
     * Add order-by as descend. <br>
     * DATA: {LONGBLOB(2147483647)}
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Data_Desc() { regOBD("DATA"); return this; }

    protected ConditionValue _encoding;
    public ConditionValue xdfgetEncoding()
    { if (_encoding == null) { _encoding = nCV(); }
      return _encoding; }
    protected ConditionValue xgetCValueEncoding() { return xdfgetEncoding(); }

    /** 
     * Add order-by as ascend. <br>
     * ENCODING: {VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Encoding_Asc() { regOBA("ENCODING"); return this; }

    /**
     * Add order-by as descend. <br>
     * ENCODING: {VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Encoding_Desc() { regOBD("ENCODING"); return this; }

    // ===================================================================================
    //                                                             SpecifiedDerivedOrderBy
    //                                                             =======================
    /**
     * Add order-by for specified derived column as ascend.
     * <pre>
     * cb.specify().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime();
     *     }
     * }, <span style="color: #CC4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #CC4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #CC4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }

    /**
     * Add order-by for specified derived column as descend.
     * <pre>
     * cb.specify().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime();
     *     }
     * }, <span style="color: #CC4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #CC4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #CC4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    public void reflectRelationOnUnionQuery(ConditionQuery bqs, ConditionQuery uqs) {
        AccessResultDataCQ bq = (AccessResultDataCQ)bqs;
        AccessResultDataCQ uq = (AccessResultDataCQ)uqs;
        if (bq.hasConditionQueryAccessResult()) {
            uq.queryAccessResult().reflectRelationOnUnionQuery(bq.queryAccessResult(), uq.queryAccessResult());
        }
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    /**
     * Get the condition-query for relation table. <br>
     * ACCESS_RESULT by my ID, named 'accessResult'.
     * @return The instance of condition-query. (NotNull)
     */
    public AccessResultCQ queryAccessResult() {
        return xdfgetConditionQueryAccessResult();
    }
    public AccessResultCQ xdfgetConditionQueryAccessResult() {
        String prop = "accessResult";
        if (!xhasQueRlMap(prop)) { xregQueRl(prop, xcreateQueryAccessResult()); xsetupOuterJoinAccessResult(); }
        return xgetQueRlMap(prop);
    }
    protected AccessResultCQ xcreateQueryAccessResult() {
        String nrp = xresolveNRP("ACCESS_RESULT_DATA", "accessResult"); String jan = xresolveJAN(nrp, xgetNNLvl());
        return xinitRelCQ(new AccessResultCQ(this, xgetSqlClause(), jan, xgetNNLvl()), _baseCB, "accessResult", nrp);
    }
    protected void xsetupOuterJoinAccessResult() { xregOutJo("accessResult"); }
    public boolean hasConditionQueryAccessResult() { return xhasQueRlMap("accessResult"); }

    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    public Map<String, AccessResultDataCQ> xdfgetScalarCondition() { return xgetSQueMap("scalarCondition"); }
    public String keepScalarCondition(AccessResultDataCQ sq) { return xkeepSQue("scalarCondition", sq); }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, AccessResultDataCQ> xdfgetSpecifyMyselfDerived() { return xgetSQueMap("specifyMyselfDerived"); }
    public String keepSpecifyMyselfDerived(AccessResultDataCQ sq) { return xkeepSQue("specifyMyselfDerived", sq); }

    public Map<String, AccessResultDataCQ> xdfgetQueryMyselfDerived() { return xgetSQueMap("queryMyselfDerived"); }
    public String keepQueryMyselfDerived(AccessResultDataCQ sq) { return xkeepSQue("queryMyselfDerived", sq); }
    public Map<String, Object> xdfgetQueryMyselfDerivedParameter() { return xgetSQuePmMap("queryMyselfDerived"); }
    public String keepQueryMyselfDerivedParameter(Object pm) { return xkeepSQuePm("queryMyselfDerived", pm); }

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    protected Map<String, AccessResultDataCQ> _myselfExistsMap;
    public Map<String, AccessResultDataCQ> xdfgetMyselfExists() { return xgetSQueMap("myselfExists"); }
    public String keepMyselfExists(AccessResultDataCQ sq) { return xkeepSQue("myselfExists", sq); }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, AccessResultDataCQ> xdfgetMyselfInScope() { return xgetSQueMap("myselfInScope"); }
    public String keepMyselfInScope(AccessResultDataCQ sq) { return xkeepSQue("myselfInScope", sq); }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() { return AccessResultDataCB.class.getName(); }
    protected String xCQ() { return AccessResultDataCQ.class.getName(); }
    protected String xCHp() { return HpQDRFunction.class.getName(); }
    protected String xCOp() { return ConditionOption.class.getName(); }
    protected String xMap() { return Map.class.getName(); }
}
