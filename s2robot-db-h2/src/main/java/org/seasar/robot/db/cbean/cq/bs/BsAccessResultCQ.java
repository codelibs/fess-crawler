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

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.cbean.cq.AccessResultCQ;
import org.seasar.robot.db.cbean.cq.AccessResultDataCQ;
import org.seasar.robot.db.cbean.cq.ciq.AccessResultCIQ;

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
    public BsAccessResultCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from ACCESS_RESULT) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public AccessResultCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected AccessResultCIQ xcreateCIQ() {
        final AccessResultCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected AccessResultCIQ xnewCIQ() {
        return new AccessResultCIQ(
            xgetReferrerQuery(),
            xgetSqlClause(),
            xgetAliasName(),
            xgetNestLevel(),
            this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join ACCESS_RESULT on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public AccessResultCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                "OnClause for local table is unavailable!");
        }
        final AccessResultCIQ inlineQuery = inline();
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

    @Override
    protected ConditionValue getCValueId() {
        return getId();
    }

    protected Map<String, AccessResultDataCQ> _id_ExistsReferrer_AccessResultDataAsOneMap;

    public Map<String, AccessResultDataCQ> getId_ExistsReferrer_AccessResultDataAsOne() {
        return _id_ExistsReferrer_AccessResultDataAsOneMap;
    }

    @Override
    public String keepId_ExistsReferrer_AccessResultDataAsOne(
            final AccessResultDataCQ sq) {
        if (_id_ExistsReferrer_AccessResultDataAsOneMap == null) {
            _id_ExistsReferrer_AccessResultDataAsOneMap =
                newLinkedHashMapSized(4);
        }
        final String ky =
            "subQueryMapKey"
                + (_id_ExistsReferrer_AccessResultDataAsOneMap.size() + 1);
        _id_ExistsReferrer_AccessResultDataAsOneMap.put(ky, sq);
        return "id_ExistsReferrer_AccessResultDataAsOne." + ky;
    }

    protected Map<String, AccessResultDataCQ> _id_NotExistsReferrer_AccessResultDataAsOneMap;

    public Map<String, AccessResultDataCQ> getId_NotExistsReferrer_AccessResultDataAsOne() {
        return _id_NotExistsReferrer_AccessResultDataAsOneMap;
    }

    @Override
    public String keepId_NotExistsReferrer_AccessResultDataAsOne(
            final AccessResultDataCQ sq) {
        if (_id_NotExistsReferrer_AccessResultDataAsOneMap == null) {
            _id_NotExistsReferrer_AccessResultDataAsOneMap =
                newLinkedHashMapSized(4);
        }
        final String ky =
            "subQueryMapKey"
                + (_id_NotExistsReferrer_AccessResultDataAsOneMap.size() + 1);
        _id_NotExistsReferrer_AccessResultDataAsOneMap.put(ky, sq);
        return "id_NotExistsReferrer_AccessResultDataAsOne." + ky;
    }

    protected Map<String, AccessResultDataCQ> _id_InScopeRelation_AccessResultDataAsOneMap;

    public Map<String, AccessResultDataCQ> getId_InScopeRelation_AccessResultDataAsOne() {
        return _id_InScopeRelation_AccessResultDataAsOneMap;
    }

    @Override
    public String keepId_InScopeRelation_AccessResultDataAsOne(
            final AccessResultDataCQ sq) {
        if (_id_InScopeRelation_AccessResultDataAsOneMap == null) {
            _id_InScopeRelation_AccessResultDataAsOneMap =
                newLinkedHashMapSized(4);
        }
        final String ky =
            "subQueryMapKey"
                + (_id_InScopeRelation_AccessResultDataAsOneMap.size() + 1);
        _id_InScopeRelation_AccessResultDataAsOneMap.put(ky, sq);
        return "id_InScopeRelation_AccessResultDataAsOne." + ky;
    }

    protected Map<String, AccessResultDataCQ> _id_NotInScopeRelation_AccessResultDataAsOneMap;

    public Map<String, AccessResultDataCQ> getId_NotInScopeRelation_AccessResultDataAsOne() {
        return _id_NotInScopeRelation_AccessResultDataAsOneMap;
    }

    @Override
    public String keepId_NotInScopeRelation_AccessResultDataAsOne(
            final AccessResultDataCQ sq) {
        if (_id_NotInScopeRelation_AccessResultDataAsOneMap == null) {
            _id_NotInScopeRelation_AccessResultDataAsOneMap =
                newLinkedHashMapSized(4);
        }
        final String ky =
            "subQueryMapKey"
                + (_id_NotInScopeRelation_AccessResultDataAsOneMap.size() + 1);
        _id_NotInScopeRelation_AccessResultDataAsOneMap.put(ky, sq);
        return "id_NotInScopeRelation_AccessResultDataAsOne." + ky;
    }

    /**
     * Add order-by as ascend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
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

    @Override
    protected ConditionValue getCValueSessionId() {
        return getSessionId();
    }

    /**
     * Add order-by as ascend. <br />
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_SessionId_Asc() {
        regOBA("SESSION_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
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

    @Override
    protected ConditionValue getCValueRuleId() {
        return getRuleId();
    }

    /**
     * Add order-by as ascend. <br />
     * RULE_ID: {VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_RuleId_Asc() {
        regOBA("RULE_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * RULE_ID: {VARCHAR(20)}
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

    @Override
    protected ConditionValue getCValueUrl() {
        return getUrl();
    }

    /**
     * Add order-by as ascend. <br />
     * URL: {IX+, NotNull, VARCHAR(65536)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Url_Asc() {
        regOBA("URL");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * URL: {IX+, NotNull, VARCHAR(65536)}
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

    @Override
    protected ConditionValue getCValueParentUrl() {
        return getParentUrl();
    }

    /**
     * Add order-by as ascend. <br />
     * PARENT_URL: {VARCHAR(65536)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_ParentUrl_Asc() {
        regOBA("PARENT_URL");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * PARENT_URL: {VARCHAR(65536)}
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

    @Override
    protected ConditionValue getCValueStatus() {
        return getStatus();
    }

    /**
     * Add order-by as ascend. <br />
     * STATUS: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Status_Asc() {
        regOBA("STATUS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * STATUS: {NotNull, INTEGER(10)}
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

    @Override
    protected ConditionValue getCValueHttpStatusCode() {
        return getHttpStatusCode();
    }

    /**
     * Add order-by as ascend. <br />
     * HTTP_STATUS_CODE: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_HttpStatusCode_Asc() {
        regOBA("HTTP_STATUS_CODE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * HTTP_STATUS_CODE: {NotNull, INTEGER(10)}
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

    @Override
    protected ConditionValue getCValueMethod() {
        return getMethod();
    }

    /**
     * Add order-by as ascend. <br />
     * METHOD: {NotNull, VARCHAR(10)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Method_Asc() {
        regOBA("METHOD");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * METHOD: {NotNull, VARCHAR(10)}
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

    @Override
    protected ConditionValue getCValueMimeType() {
        return getMimeType();
    }

    /**
     * Add order-by as ascend. <br />
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_MimeType_Asc() {
        regOBA("MIME_TYPE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * MIME_TYPE: {NotNull, VARCHAR(100)}
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

    @Override
    protected ConditionValue getCValueContentLength() {
        return getContentLength();
    }

    /**
     * Add order-by as ascend. <br />
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_ContentLength_Asc() {
        regOBA("CONTENT_LENGTH");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
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

    @Override
    protected ConditionValue getCValueExecutionTime() {
        return getExecutionTime();
    }

    /**
     * Add order-by as ascend. <br />
     * EXECUTION_TIME: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_ExecutionTime_Asc() {
        regOBA("EXECUTION_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * EXECUTION_TIME: {NotNull, INTEGER(10)}
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

    @Override
    protected ConditionValue getCValueLastModified() {
        return getLastModified();
    }

    /**
     * Add order-by as ascend. <br />
     * LAST_MODIFIED: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_LastModified_Asc() {
        regOBA("LAST_MODIFIED");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * LAST_MODIFIED: {NotNull, TIMESTAMP(23, 10)}
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

    @Override
    protected ConditionValue getCValueCreateTime() {
        return getCreateTime();
    }

    /**
     * Add order-by as ascend. <br />
     * CREATE_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_CreateTime_Asc() {
        regOBA("CREATE_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATE_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_CreateTime_Desc() {
        regOBD("CREATE_TIME");
        return this;
    }

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
    public BsAccessResultCQ addSpecifiedDerivedOrderBy_Asc(
            final String aliasName) {
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
     * }, <span style="color: #DD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #DD4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #DD4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addSpecifiedDerivedOrderBy_Desc(
            final String aliasName) {
        registerSpecifiedDerivedOrderBy_Desc(aliasName);
        return this;
    }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    @Override
    public void reflectRelationOnUnionQuery(final ConditionQuery bqs,
            final ConditionQuery uqs) {
        final AccessResultCQ bq = (AccessResultCQ) bqs;
        final AccessResultCQ uq = (AccessResultCQ) uqs;
        if (bq.hasConditionQueryAccessResultDataAsOne()) {
            uq.queryAccessResultDataAsOne().reflectRelationOnUnionQuery(
                bq.queryAccessResultDataAsOne(),
                uq.queryAccessResultDataAsOne());
        }
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    /**
     * Get the condition-query for relation table. <br />
     * ACCESS_RESULT_DATA by ID, named 'accessResultDataAsOne'.
     * @return The instance of condition-query. (NotNull)
     */
    public AccessResultDataCQ queryAccessResultDataAsOne() {
        return getConditionQueryAccessResultDataAsOne();
    }

    protected AccessResultDataCQ _conditionQueryAccessResultDataAsOne;

    public AccessResultDataCQ getConditionQueryAccessResultDataAsOne() {
        if (_conditionQueryAccessResultDataAsOne == null) {
            _conditionQueryAccessResultDataAsOne =
                xcreateQueryAccessResultDataAsOne();
            xsetupOuterJoinAccessResultDataAsOne();
        }
        return _conditionQueryAccessResultDataAsOne;
    }

    protected AccessResultDataCQ xcreateQueryAccessResultDataAsOne() {
        final String nrp =
            resolveNextRelationPath("ACCESS_RESULT", "accessResultDataAsOne");
        final String jan = resolveJoinAliasName(nrp, xgetNextNestLevel());
        final AccessResultDataCQ cq =
            new AccessResultDataCQ(
                this,
                xgetSqlClause(),
                jan,
                xgetNextNestLevel());
        cq.xsetBaseCB(_baseCB);
        cq.xsetForeignPropertyName("accessResultDataAsOne");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinAccessResultDataAsOne() {
        final AccessResultDataCQ cq = getConditionQueryAccessResultDataAsOne();
        final Map<String, String> joinOnMap = newLinkedHashMapSized(4);
        joinOnMap.put("ID", "ID");
        registerOuterJoin(cq, joinOnMap, "accessResultDataAsOne");
    }

    public boolean hasConditionQueryAccessResultDataAsOne() {
        return _conditionQueryAccessResultDataAsOne != null;
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    protected Map<String, AccessResultCQ> _scalarConditionMap;

    public Map<String, AccessResultCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final AccessResultCQ sq) {
        if (_scalarConditionMap == null) {
            _scalarConditionMap = newLinkedHashMapSized(4);
        }
        final String ky = "subQueryMapKey" + (_scalarConditionMap.size() + 1);
        _scalarConditionMap.put(ky, sq);
        return "scalarCondition." + ky;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    protected Map<String, AccessResultCQ> _specifyMyselfDerivedMap;

    public Map<String, AccessResultCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final AccessResultCQ sq) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String ky =
            "subQueryMapKey" + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(ky, sq);
        return "specifyMyselfDerived." + ky;
    }

    protected Map<String, AccessResultCQ> _queryMyselfDerivedMap;

    public Map<String, AccessResultCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final AccessResultCQ sq) {
        if (_queryMyselfDerivedMap == null) {
            _queryMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String ky =
            "subQueryMapKey" + (_queryMyselfDerivedMap.size() + 1);
        _queryMyselfDerivedMap.put(ky, sq);
        return "queryMyselfDerived." + ky;
    }

    protected Map<String, Object> _qyeryMyselfDerivedParameterMap;

    public Map<String, Object> getQueryMyselfDerivedParameter() {
        return _qyeryMyselfDerivedParameterMap;
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object vl) {
        if (_qyeryMyselfDerivedParameterMap == null) {
            _qyeryMyselfDerivedParameterMap = newLinkedHashMapSized(4);
        }
        final String ky =
            "subQueryParameterKey"
                + (_qyeryMyselfDerivedParameterMap.size() + 1);
        _qyeryMyselfDerivedParameterMap.put(ky, vl);
        return "queryMyselfDerivedParameter." + ky;
    }

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    protected Map<String, AccessResultCQ> _myselfExistsMap;

    public Map<String, AccessResultCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final AccessResultCQ sq) {
        if (_myselfExistsMap == null) {
            _myselfExistsMap = newLinkedHashMapSized(4);
        }
        final String ky = "subQueryMapKey" + (_myselfExistsMap.size() + 1);
        _myselfExistsMap.put(ky, sq);
        return "myselfExists." + ky;
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    protected Map<String, AccessResultCQ> _myselfInScopeMap;

    public Map<String, AccessResultCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final AccessResultCQ sq) {
        if (_myselfInScopeMap == null) {
            _myselfInScopeMap = newLinkedHashMapSized(4);
        }
        final String ky = "subQueryMapKey" + (_myselfInScopeMap.size() + 1);
        _myselfInScopeMap.put(ky, sq);
        return "myselfInScope." + ky;
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

    protected String xCHp() {
        return HpCalculator.class.getName();
    }

    protected String xCOp() {
        return ConditionOption.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
