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
package org.seasar.robot.db.cbean.cq.bs;

import java.util.Map;

import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.cbean.cq.AccessResultCQ;
import org.seasar.robot.db.cbean.cq.AccessResultDataCQ;
import org.seasar.robot.db.cbean.cq.ciq.AccessResultCIQ;
import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of ACCESS_RESULT.
 * @author DBFlute(AutoGenerator)
 */
public class BsAccessResultCQ extends AbstractBsAccessResultCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected AccessResultCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsAccessResultCQ(ConditionQuery childQuery, SqlClause sqlClause,
            String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from ACCESS_RESULT) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public AccessResultCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = createInlineQuery();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected AccessResultCIQ createInlineQuery() {
        return new AccessResultCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join ACCESS_RESULT on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public AccessResultCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        AccessResultCIQ inlineQuery = inline();
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

    protected Map<String, AccessResultDataCQ> _id_ExistsReferrer_AccessResultDataAsOneMap;

    public Map<String, AccessResultDataCQ> getId_ExistsReferrer_AccessResultDataAsOne() {
        return _id_ExistsReferrer_AccessResultDataAsOneMap;
    }

    public String keepId_ExistsReferrer_AccessResultDataAsOne(
            AccessResultDataCQ subQuery) {
        if (_id_ExistsReferrer_AccessResultDataAsOneMap == null) {
            _id_ExistsReferrer_AccessResultDataAsOneMap = newLinkedHashMap();
        }
        String key = "subQueryMapKey"
                + (_id_ExistsReferrer_AccessResultDataAsOneMap.size() + 1);
        _id_ExistsReferrer_AccessResultDataAsOneMap.put(key, subQuery);
        return "id_ExistsReferrer_AccessResultDataAsOne." + key;
    }

    protected Map<String, AccessResultDataCQ> _id_NotExistsReferrer_AccessResultDataAsOneMap;

    public Map<String, AccessResultDataCQ> getId_NotExistsReferrer_AccessResultDataAsOne() {
        return _id_NotExistsReferrer_AccessResultDataAsOneMap;
    }

    public String keepId_NotExistsReferrer_AccessResultDataAsOne(
            AccessResultDataCQ subQuery) {
        if (_id_NotExistsReferrer_AccessResultDataAsOneMap == null) {
            _id_NotExistsReferrer_AccessResultDataAsOneMap = newLinkedHashMap();
        }
        String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_AccessResultDataAsOneMap.size() + 1);
        _id_NotExistsReferrer_AccessResultDataAsOneMap.put(key, subQuery);
        return "id_NotExistsReferrer_AccessResultDataAsOne." + key;
    }

    protected Map<String, AccessResultDataCQ> _id_InScopeRelation_AccessResultDataAsOneMap;

    public Map<String, AccessResultDataCQ> getId_InScopeRelation_AccessResultDataAsOne() {
        return _id_InScopeRelation_AccessResultDataAsOneMap;
    }

    public String keepId_InScopeRelation_AccessResultDataAsOne(
            AccessResultDataCQ subQuery) {
        if (_id_InScopeRelation_AccessResultDataAsOneMap == null) {
            _id_InScopeRelation_AccessResultDataAsOneMap = newLinkedHashMap();
        }
        String key = "subQueryMapKey"
                + (_id_InScopeRelation_AccessResultDataAsOneMap.size() + 1);
        _id_InScopeRelation_AccessResultDataAsOneMap.put(key, subQuery);
        return "id_InScopeRelation_AccessResultDataAsOne." + key;
    }

    protected Map<String, AccessResultDataCQ> _id_NotInScopeRelation_AccessResultDataAsOneMap;

    public Map<String, AccessResultDataCQ> getId_NotInScopeRelation_AccessResultDataAsOne() {
        return _id_NotInScopeRelation_AccessResultDataAsOneMap;
    }

    public String keepId_NotInScopeRelation_AccessResultDataAsOne(
            AccessResultDataCQ subQuery) {
        if (_id_NotInScopeRelation_AccessResultDataAsOneMap == null) {
            _id_NotInScopeRelation_AccessResultDataAsOneMap = newLinkedHashMap();
        }
        String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_AccessResultDataAsOneMap.size() + 1);
        _id_NotInScopeRelation_AccessResultDataAsOneMap.put(key, subQuery);
        return "id_NotInScopeRelation_AccessResultDataAsOne." + key;
    }

    /** 
     * Add order-by as ascend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Id_Desc() {
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
    public BsAccessResultCQ addOrderBy_SessionId_Asc() {
        regOBA("SESSION_ID");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_SessionId_Desc() {
        regOBD("SESSION_ID");
        return this;
    }

    protected ConditionValue _ruleId;

    public ConditionValue getRuleId() {
        if (_ruleId == null) {
            _ruleId = nCV();
        }
        return _ruleId;
    }

    protected ConditionValue getCValueRuleId() {
        return getRuleId();
    }

    /** 
     * Add order-by as ascend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_RuleId_Asc() {
        regOBA("RULE_ID");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_RuleId_Desc() {
        regOBD("RULE_ID");
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
    public BsAccessResultCQ addOrderBy_Url_Asc() {
        regOBA("URL");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Url_Desc() {
        regOBD("URL");
        return this;
    }

    protected ConditionValue _parentUrl;

    public ConditionValue getParentUrl() {
        if (_parentUrl == null) {
            _parentUrl = nCV();
        }
        return _parentUrl;
    }

    protected ConditionValue getCValueParentUrl() {
        return getParentUrl();
    }

    /** 
     * Add order-by as ascend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_ParentUrl_Asc() {
        regOBA("PARENT_URL");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_ParentUrl_Desc() {
        regOBD("PARENT_URL");
        return this;
    }

    protected ConditionValue _status;

    public ConditionValue getStatus() {
        if (_status == null) {
            _status = nCV();
        }
        return _status;
    }

    protected ConditionValue getCValueStatus() {
        return getStatus();
    }

    /** 
     * Add order-by as ascend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Status_Asc() {
        regOBA("STATUS");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Status_Desc() {
        regOBD("STATUS");
        return this;
    }

    protected ConditionValue _httpStatusCode;

    public ConditionValue getHttpStatusCode() {
        if (_httpStatusCode == null) {
            _httpStatusCode = nCV();
        }
        return _httpStatusCode;
    }

    protected ConditionValue getCValueHttpStatusCode() {
        return getHttpStatusCode();
    }

    /** 
     * Add order-by as ascend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_HttpStatusCode_Asc() {
        regOBA("HTTP_STATUS_CODE");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_HttpStatusCode_Desc() {
        regOBD("HTTP_STATUS_CODE");
        return this;
    }

    protected ConditionValue _method;

    public ConditionValue getMethod() {
        if (_method == null) {
            _method = nCV();
        }
        return _method;
    }

    protected ConditionValue getCValueMethod() {
        return getMethod();
    }

    /** 
     * Add order-by as ascend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Method_Asc() {
        regOBA("METHOD");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Method_Desc() {
        regOBD("METHOD");
        return this;
    }

    protected ConditionValue _mimeType;

    public ConditionValue getMimeType() {
        if (_mimeType == null) {
            _mimeType = nCV();
        }
        return _mimeType;
    }

    protected ConditionValue getCValueMimeType() {
        return getMimeType();
    }

    /** 
     * Add order-by as ascend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_MimeType_Asc() {
        regOBA("MIME_TYPE");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_MimeType_Desc() {
        regOBD("MIME_TYPE");
        return this;
    }

    protected ConditionValue _contentLength;

    public ConditionValue getContentLength() {
        if (_contentLength == null) {
            _contentLength = nCV();
        }
        return _contentLength;
    }

    protected ConditionValue getCValueContentLength() {
        return getContentLength();
    }

    /** 
     * Add order-by as ascend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_ContentLength_Asc() {
        regOBA("CONTENT_LENGTH");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_ContentLength_Desc() {
        regOBD("CONTENT_LENGTH");
        return this;
    }

    protected ConditionValue _executionTime;

    public ConditionValue getExecutionTime() {
        if (_executionTime == null) {
            _executionTime = nCV();
        }
        return _executionTime;
    }

    protected ConditionValue getCValueExecutionTime() {
        return getExecutionTime();
    }

    /** 
     * Add order-by as ascend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_ExecutionTime_Asc() {
        regOBA("EXECUTION_TIME");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_ExecutionTime_Desc() {
        regOBD("EXECUTION_TIME");
        return this;
    }

    protected ConditionValue _lastModified;

    public ConditionValue getLastModified() {
        if (_lastModified == null) {
            _lastModified = nCV();
        }
        return _lastModified;
    }

    protected ConditionValue getCValueLastModified() {
        return getLastModified();
    }

    /** 
     * Add order-by as ascend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_LastModified_Asc() {
        regOBA("LAST_MODIFIED");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_LastModified_Desc() {
        regOBD("LAST_MODIFIED");
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
    public BsAccessResultCQ addOrderBy_CreateTime_Asc() {
        regOBA("CREATE_TIME");
        return this;
    }

    /**
     * Add order-by as descend.
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_CreateTime_Desc() {
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
    public BsAccessResultCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) {
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
    public BsAccessResultCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) {
        registerSpecifiedDerivedOrderBy_Desc(aliasName);
        return this;
    }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper,
            ConditionQuery unionQueryAsSuper) {
        AccessResultCQ baseQuery = (AccessResultCQ) baseQueryAsSuper;
        AccessResultCQ unionQuery = (AccessResultCQ) unionQueryAsSuper;
        if (baseQuery.hasConditionQueryAccessResultDataAsOne()) {
            unionQuery.queryAccessResultDataAsOne()
                    .reflectRelationOnUnionQuery(
                            baseQuery.queryAccessResultDataAsOne(),
                            unionQuery.queryAccessResultDataAsOne());
        }
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    /**
     * Get the condition-query for relation table. <br />
     * ACCESS_RESULT_DATA as 'accessResultDataAsOne'.
     * @return The instance of condition-query. (NotNull)
     */
    public AccessResultDataCQ queryAccessResultDataAsOne() {
        return getConditionQueryAccessResultDataAsOne();
    }

    protected AccessResultDataCQ _conditionQueryAccessResultDataAsOne;

    public AccessResultDataCQ getConditionQueryAccessResultDataAsOne() {
        if (_conditionQueryAccessResultDataAsOne == null) {
            _conditionQueryAccessResultDataAsOne = xcreateQueryAccessResultDataAsOne();
            xsetupOuterJoinAccessResultDataAsOne();
        }
        return _conditionQueryAccessResultDataAsOne;
    }

    protected AccessResultDataCQ xcreateQueryAccessResultDataAsOne() {
        String nrp = resolveNextRelationPath("ACCESS_RESULT",
                "accessResultDataAsOne");
        String jan = resolveJoinAliasName(nrp, xgetNextNestLevel());
        AccessResultDataCQ cq = new AccessResultDataCQ(this, xgetSqlClause(),
                jan, xgetNextNestLevel());
        cq.xsetForeignPropertyName("accessResultDataAsOne");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinAccessResultDataAsOne() {
        AccessResultDataCQ cq = getConditionQueryAccessResultDataAsOne();
        Map<String, String> joinOnMap = newLinkedHashMap();
        joinOnMap.put("ID", "ID");
        registerOuterJoin(cq, joinOnMap);
    }

    public boolean hasConditionQueryAccessResultDataAsOne() {
        return _conditionQueryAccessResultDataAsOne != null;
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    protected Map<String, AccessResultCQ> _scalarConditionMap;

    public Map<String, AccessResultCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    public String keepScalarCondition(AccessResultCQ subQuery) {
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
    protected Map<String, AccessResultCQ> _myselfInScopeRelationMap;

    public Map<String, AccessResultCQ> getMyselfInScopeRelation() {
        return _myselfInScopeRelationMap;
    }

    public String keepMyselfInScopeRelation(AccessResultCQ subQuery) {
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
        return AccessResultCB.class.getName();
    }

    protected String xCQ() {
        return AccessResultCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
