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
package org.seasar.robot.db.cbean.cq.bs;

import java.util.Map;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.chelper.*;
import org.seasar.dbflute.cbean.coption.*;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.robot.db.cbean.cq.ciq.*;
import org.seasar.robot.db.cbean.*;
import org.seasar.robot.db.cbean.cq.*;

/**
 * The base condition-query of URL_FILTER.
 * @author DBFlute(AutoGenerator)
 */
public class BsUrlFilterCQ extends AbstractBsUrlFilterCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected UrlFilterCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsUrlFilterCQ(ConditionQuery referrerQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from URL_FILTER) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public UrlFilterCIQ inline() {
        if (_inlineQuery == null) { _inlineQuery = xcreateCIQ(); }
        _inlineQuery.xsetOnClause(false); return _inlineQuery;
    }

    protected UrlFilterCIQ xcreateCIQ() {
        UrlFilterCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected UrlFilterCIQ xnewCIQ() {
        return new UrlFilterCIQ(xgetReferrerQuery(), xgetSqlClause(), xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join URL_FILTER on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public UrlFilterCIQ on() {
        if (isBaseQuery()) { throw new IllegalConditionBeanOperationException("OnClause for local table is unavailable!"); }
        UrlFilterCIQ inlineQuery = inline(); inlineQuery.xsetOnClause(true); return inlineQuery;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    protected ConditionValue _id;
    public ConditionValue getId() {
        if (_id == null) { _id = nCV(); }
        return _id;
    }
    protected ConditionValue getCValueId() { return getId(); }

    /** 
     * Add order-by as ascend. <br />
     * ID: {PK, NotNull, NUMBER(19)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_Id_Asc() { regOBA("ID"); return this; }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, NotNull, NUMBER(19)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_Id_Desc() { regOBD("ID"); return this; }

    protected ConditionValue _sessionId;
    public ConditionValue getSessionId() {
        if (_sessionId == null) { _sessionId = nCV(); }
        return _sessionId;
    }
    protected ConditionValue getCValueSessionId() { return getSessionId(); }

    /** 
     * Add order-by as ascend. <br />
     * SESSION_ID: {IX+, NotNull, VARCHAR2(20)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_SessionId_Asc() { regOBA("SESSION_ID"); return this; }

    /**
     * Add order-by as descend. <br />
     * SESSION_ID: {IX+, NotNull, VARCHAR2(20)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_SessionId_Desc() { regOBD("SESSION_ID"); return this; }

    protected ConditionValue _url;
    public ConditionValue getUrl() {
        if (_url == null) { _url = nCV(); }
        return _url;
    }
    protected ConditionValue getCValueUrl() { return getUrl(); }

    /** 
     * Add order-by as ascend. <br />
     * URL: {NotNull, VARCHAR2(4000)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_Url_Asc() { regOBA("URL"); return this; }

    /**
     * Add order-by as descend. <br />
     * URL: {NotNull, VARCHAR2(4000)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_Url_Desc() { regOBD("URL"); return this; }

    protected ConditionValue _filterType;
    public ConditionValue getFilterType() {
        if (_filterType == null) { _filterType = nCV(); }
        return _filterType;
    }
    protected ConditionValue getCValueFilterType() { return getFilterType(); }

    /** 
     * Add order-by as ascend. <br />
     * FILTER_TYPE: {NotNull, VARCHAR2(1)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_FilterType_Asc() { regOBA("FILTER_TYPE"); return this; }

    /**
     * Add order-by as descend. <br />
     * FILTER_TYPE: {NotNull, VARCHAR2(1)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_FilterType_Desc() { regOBD("FILTER_TYPE"); return this; }

    protected ConditionValue _createTime;
    public ConditionValue getCreateTime() {
        if (_createTime == null) { _createTime = nCV(); }
        return _createTime;
    }
    protected ConditionValue getCValueCreateTime() { return getCreateTime(); }

    /** 
     * Add order-by as ascend. <br />
     * CREATE_TIME: {NotNull, TIMESTAMP(6)(11, 6)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_CreateTime_Asc() { regOBA("CREATE_TIME"); return this; }

    /**
     * Add order-by as descend. <br />
     * CREATE_TIME: {NotNull, TIMESTAMP(6)(11, 6)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_CreateTime_Desc() { regOBD("CREATE_TIME"); return this; }

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
     * }, <span style="color: #DD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #DD4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #DD4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addSpecifiedDerivedOrderBy_Asc(String aliasName)
    { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }

    /**
     * Add order-by for specified derived column as descend.
     * <pre>
     * cb.specify().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime();
     *     }
     * }, <span style="color: #DD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #DD4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #DD4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addSpecifiedDerivedOrderBy_Desc(String aliasName)
    { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    public void reflectRelationOnUnionQuery(ConditionQuery bqs, ConditionQuery uqs) {
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    protected Map<String, UrlFilterCQ> _scalarConditionMap;
    public Map<String, UrlFilterCQ> getScalarCondition() { return _scalarConditionMap; }
    public String keepScalarCondition(UrlFilterCQ sq) {
        if (_scalarConditionMap == null) { _scalarConditionMap = newLinkedHashMapSized(4); }
        String ky = "subQueryMapKey" + (_scalarConditionMap.size() + 1);
        _scalarConditionMap.put(ky, sq); return "scalarCondition." + ky;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    protected Map<String, UrlFilterCQ> _specifyMyselfDerivedMap;
    public Map<String, UrlFilterCQ> getSpecifyMyselfDerived() { return _specifyMyselfDerivedMap; }
    public String keepSpecifyMyselfDerived(UrlFilterCQ sq) {
        if (_specifyMyselfDerivedMap == null) { _specifyMyselfDerivedMap = newLinkedHashMapSized(4); }
        String ky = "subQueryMapKey" + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(ky, sq); return "specifyMyselfDerived." + ky;
    }

    protected Map<String, UrlFilterCQ> _queryMyselfDerivedMap;
    public Map<String, UrlFilterCQ> getQueryMyselfDerived() { return _queryMyselfDerivedMap; }
    public String keepQueryMyselfDerived(UrlFilterCQ sq) {
        if (_queryMyselfDerivedMap == null) { _queryMyselfDerivedMap = newLinkedHashMapSized(4); }
        String ky = "subQueryMapKey" + (_queryMyselfDerivedMap.size() + 1);
        _queryMyselfDerivedMap.put(ky, sq); return "queryMyselfDerived." + ky;
    }
    protected Map<String, Object> _qyeryMyselfDerivedParameterMap;
    public Map<String, Object> getQueryMyselfDerivedParameter() { return _qyeryMyselfDerivedParameterMap; }
    public String keepQueryMyselfDerivedParameter(Object vl) {
        if (_qyeryMyselfDerivedParameterMap == null) { _qyeryMyselfDerivedParameterMap = newLinkedHashMapSized(4); }
        String ky = "subQueryParameterKey" + (_qyeryMyselfDerivedParameterMap.size() + 1);
        _qyeryMyselfDerivedParameterMap.put(ky, vl); return "queryMyselfDerivedParameter." + ky;
    }

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    protected Map<String, UrlFilterCQ> _myselfExistsMap;
    public Map<String, UrlFilterCQ> getMyselfExists() { return _myselfExistsMap; }
    public String keepMyselfExists(UrlFilterCQ sq) {
        if (_myselfExistsMap == null) { _myselfExistsMap = newLinkedHashMapSized(4); }
        String ky = "subQueryMapKey" + (_myselfExistsMap.size() + 1);
        _myselfExistsMap.put(ky, sq); return "myselfExists." + ky;
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    protected Map<String, UrlFilterCQ> _myselfInScopeMap;
    public Map<String, UrlFilterCQ> getMyselfInScope() { return _myselfInScopeMap; }
    public String keepMyselfInScope(UrlFilterCQ sq) {
        if (_myselfInScopeMap == null) { _myselfInScopeMap = newLinkedHashMapSized(4); }
        String ky = "subQueryMapKey" + (_myselfInScopeMap.size() + 1);
        _myselfInScopeMap.put(ky, sq); return "myselfInScope." + ky;
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() { return UrlFilterCB.class.getName(); }
    protected String xCQ() { return UrlFilterCQ.class.getName(); }
    protected String xCHp() { return HpCalculator.class.getName(); }
    protected String xCOp() { return ConditionOption.class.getName(); }
    protected String xMap() { return Map.class.getName(); }
}
