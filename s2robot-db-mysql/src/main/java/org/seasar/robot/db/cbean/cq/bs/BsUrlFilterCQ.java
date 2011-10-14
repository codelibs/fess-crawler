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
package org.seasar.robot.db.cbean.cq.bs;

import java.util.Map;

import org.seasar.robot.db.cbean.UrlFilterCB;
import org.seasar.robot.db.cbean.cq.UrlFilterCQ;
import org.seasar.robot.db.cbean.cq.ciq.UrlFilterCIQ;
import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.exception.IllegalConditionBeanOperationException;

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
    public BsUrlFilterCQ(ConditionQuery childQuery, SqlClause sqlClause,
            String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from URL_FILTER) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public UrlFilterCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = createInlineQuery();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected UrlFilterCIQ createInlineQuery() {
        return new UrlFilterCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join URL_FILTER on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public UrlFilterCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        UrlFilterCIQ inlineQuery = inline();
        inlineQuery.xsetOnClause(true);
        return inlineQuery;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    protected ConditionValue _id;

    public ConditionValue getId() {
        if (_id == null) {
            _id = nCV();
        }
        return _id;
    }

    protected ConditionValue getCValueId() {
        return getId();
    }

    /** 
     * Add order-by as ascend.
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _sessionId;

    public ConditionValue getSessionId() {
        if (_sessionId == null) {
            _sessionId = nCV();
        }
        return _sessionId;
    }

    protected ConditionValue getCValueSessionId() {
        return getSessionId();
    }

    /** 
     * Add order-by as ascend.
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_SessionId_Asc() {
        regOBA("SESSION_ID");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_SessionId_Desc() {
        regOBD("SESSION_ID");
        return this;
    }

    protected ConditionValue _url;

    public ConditionValue getUrl() {
        if (_url == null) {
            _url = nCV();
        }
        return _url;
    }

    protected ConditionValue getCValueUrl() {
        return getUrl();
    }

    /** 
     * Add order-by as ascend.
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_Url_Asc() {
        regOBA("URL");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_Url_Desc() {
        regOBD("URL");
        return this;
    }

    protected ConditionValue _filterType;

    public ConditionValue getFilterType() {
        if (_filterType == null) {
            _filterType = nCV();
        }
        return _filterType;
    }

    protected ConditionValue getCValueFilterType() {
        return getFilterType();
    }

    /** 
     * Add order-by as ascend.
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_FilterType_Asc() {
        regOBA("FILTER_TYPE");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_FilterType_Desc() {
        regOBD("FILTER_TYPE");
        return this;
    }

    protected ConditionValue _createTime;

    public ConditionValue getCreateTime() {
        if (_createTime == null) {
            _createTime = nCV();
        }
        return _createTime;
    }

    protected ConditionValue getCValueCreateTime() {
        return getCreateTime();
    }

    /** 
     * Add order-by as ascend.
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_CreateTime_Asc() {
        regOBA("CREATE_TIME");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_CreateTime_Desc() {
        regOBD("CREATE_TIME");
        return this;
    }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    /**
     * Add order-by for specified derived column as ascend.
     * <pre>
     * cb.specify().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime();
     *     }
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #FD4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) {
        registerSpecifiedDerivedOrderBy_Asc(aliasName);
        return this;
    }

    /**
     * Add order-by for specified derived column as descend.
     * <pre>
     * cb.specify().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime();
     *     }
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #FD4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) {
        registerSpecifiedDerivedOrderBy_Desc(aliasName);
        return this;
    }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper,
            ConditionQuery unionQueryAsSuper) {
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    protected Map<String, UrlFilterCQ> _scalarConditionMap;

    public Map<String, UrlFilterCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    public String keepScalarCondition(UrlFilterCQ subQuery) {
        if (_scalarConditionMap == null) {
            _scalarConditionMap = newLinkedHashMap();
        }
        String key = "subQueryMapKey" + (_scalarConditionMap.size() + 1);
        _scalarConditionMap.put(key, subQuery);
        return "scalarCondition." + key;
    }

    // ===================================================================================
    //                                                             MySelf InScope SubQuery
    //                                                             =======================
    protected Map<String, UrlFilterCQ> _myselfInScopeRelationMap;

    public Map<String, UrlFilterCQ> getMyselfInScopeRelation() {
        return _myselfInScopeRelationMap;
    }

    public String keepMyselfInScopeRelation(UrlFilterCQ subQuery) {
        if (_myselfInScopeRelationMap == null) {
            _myselfInScopeRelationMap = newLinkedHashMap();
        }
        String key = "subQueryMapKey" + (_myselfInScopeRelationMap.size() + 1);
        _myselfInScopeRelationMap.put(key, subQuery);
        return "myselfInScopeRelation." + key;
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return UrlFilterCB.class.getName();
    }

    protected String xCQ() {
        return UrlFilterCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
