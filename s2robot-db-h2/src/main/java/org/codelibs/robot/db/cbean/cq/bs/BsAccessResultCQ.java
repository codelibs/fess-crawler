/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.robot.db.cbean.cq.bs;

import java.util.Map;

import org.codelibs.robot.db.cbean.AccessResultCB;
import org.codelibs.robot.db.cbean.cq.AccessResultCQ;
import org.codelibs.robot.db.cbean.cq.AccessResultDataCQ;
import org.codelibs.robot.db.cbean.cq.ciq.AccessResultCIQ;
import org.dbflute.cbean.ConditionQuery;
import org.dbflute.cbean.chelper.HpQDRFunction;
import org.dbflute.cbean.coption.ConditionOption;
import org.dbflute.cbean.cvalue.ConditionValue;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.dbflute.exception.IllegalConditionBeanOperationException;

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
     * Prepare InlineView query. <br>
     * {select ... from ... left outer join (select * from ACCESS_RESULT) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #CC4747">inline()</span>.setFoo...;
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
        return new AccessResultCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br>
     * {select ... from ... left outer join ACCESS_RESULT on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #CC4747">on()</span>.setFoo...;
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

    public ConditionValue xdfgetId() {
        if (_id == null) {
            _id = nCV();
        }
        return _id;
    }

    @Override
    protected ConditionValue xgetCValueId() {
        return xdfgetId();
    }

    /**
     * Add order-by as ascend. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _sessionId;

    public ConditionValue xdfgetSessionId() {
        if (_sessionId == null) {
            _sessionId = nCV();
        }
        return _sessionId;
    }

    @Override
    protected ConditionValue xgetCValueSessionId() {
        return xdfgetSessionId();
    }

    /**
     * Add order-by as ascend. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_SessionId_Asc() {
        regOBA("SESSION_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_SessionId_Desc() {
        regOBD("SESSION_ID");
        return this;
    }

    protected ConditionValue _ruleId;

    public ConditionValue xdfgetRuleId() {
        if (_ruleId == null) {
            _ruleId = nCV();
        }
        return _ruleId;
    }

    @Override
    protected ConditionValue xgetCValueRuleId() {
        return xdfgetRuleId();
    }

    /**
     * Add order-by as ascend. <br>
     * RULE_ID: {VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_RuleId_Asc() {
        regOBA("RULE_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * RULE_ID: {VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_RuleId_Desc() {
        regOBD("RULE_ID");
        return this;
    }

    protected ConditionValue _url;

    public ConditionValue xdfgetUrl() {
        if (_url == null) {
            _url = nCV();
        }
        return _url;
    }

    @Override
    protected ConditionValue xgetCValueUrl() {
        return xdfgetUrl();
    }

    /**
     * Add order-by as ascend. <br>
     * URL: {IX+, NotNull, VARCHAR(65536)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Url_Asc() {
        regOBA("URL");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * URL: {IX+, NotNull, VARCHAR(65536)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Url_Desc() {
        regOBD("URL");
        return this;
    }

    protected ConditionValue _parentUrl;

    public ConditionValue xdfgetParentUrl() {
        if (_parentUrl == null) {
            _parentUrl = nCV();
        }
        return _parentUrl;
    }

    @Override
    protected ConditionValue xgetCValueParentUrl() {
        return xdfgetParentUrl();
    }

    /**
     * Add order-by as ascend. <br>
     * PARENT_URL: {VARCHAR(65536)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_ParentUrl_Asc() {
        regOBA("PARENT_URL");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * PARENT_URL: {VARCHAR(65536)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_ParentUrl_Desc() {
        regOBD("PARENT_URL");
        return this;
    }

    protected ConditionValue _status;

    public ConditionValue xdfgetStatus() {
        if (_status == null) {
            _status = nCV();
        }
        return _status;
    }

    @Override
    protected ConditionValue xgetCValueStatus() {
        return xdfgetStatus();
    }

    /**
     * Add order-by as ascend. <br>
     * STATUS: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Status_Asc() {
        regOBA("STATUS");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * STATUS: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Status_Desc() {
        regOBD("STATUS");
        return this;
    }

    protected ConditionValue _httpStatusCode;

    public ConditionValue xdfgetHttpStatusCode() {
        if (_httpStatusCode == null) {
            _httpStatusCode = nCV();
        }
        return _httpStatusCode;
    }

    @Override
    protected ConditionValue xgetCValueHttpStatusCode() {
        return xdfgetHttpStatusCode();
    }

    /**
     * Add order-by as ascend. <br>
     * HTTP_STATUS_CODE: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_HttpStatusCode_Asc() {
        regOBA("HTTP_STATUS_CODE");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * HTTP_STATUS_CODE: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_HttpStatusCode_Desc() {
        regOBD("HTTP_STATUS_CODE");
        return this;
    }

    protected ConditionValue _method;

    public ConditionValue xdfgetMethod() {
        if (_method == null) {
            _method = nCV();
        }
        return _method;
    }

    @Override
    protected ConditionValue xgetCValueMethod() {
        return xdfgetMethod();
    }

    /**
     * Add order-by as ascend. <br>
     * METHOD: {NotNull, VARCHAR(10)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Method_Asc() {
        regOBA("METHOD");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * METHOD: {NotNull, VARCHAR(10)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_Method_Desc() {
        regOBD("METHOD");
        return this;
    }

    protected ConditionValue _mimeType;

    public ConditionValue xdfgetMimeType() {
        if (_mimeType == null) {
            _mimeType = nCV();
        }
        return _mimeType;
    }

    @Override
    protected ConditionValue xgetCValueMimeType() {
        return xdfgetMimeType();
    }

    /**
     * Add order-by as ascend. <br>
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_MimeType_Asc() {
        regOBA("MIME_TYPE");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_MimeType_Desc() {
        regOBD("MIME_TYPE");
        return this;
    }

    protected ConditionValue _contentLength;

    public ConditionValue xdfgetContentLength() {
        if (_contentLength == null) {
            _contentLength = nCV();
        }
        return _contentLength;
    }

    @Override
    protected ConditionValue xgetCValueContentLength() {
        return xdfgetContentLength();
    }

    /**
     * Add order-by as ascend. <br>
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_ContentLength_Asc() {
        regOBA("CONTENT_LENGTH");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_ContentLength_Desc() {
        regOBD("CONTENT_LENGTH");
        return this;
    }

    protected ConditionValue _executionTime;

    public ConditionValue xdfgetExecutionTime() {
        if (_executionTime == null) {
            _executionTime = nCV();
        }
        return _executionTime;
    }

    @Override
    protected ConditionValue xgetCValueExecutionTime() {
        return xdfgetExecutionTime();
    }

    /**
     * Add order-by as ascend. <br>
     * EXECUTION_TIME: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_ExecutionTime_Asc() {
        regOBA("EXECUTION_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * EXECUTION_TIME: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_ExecutionTime_Desc() {
        regOBD("EXECUTION_TIME");
        return this;
    }

    protected ConditionValue _lastModified;

    public ConditionValue xdfgetLastModified() {
        if (_lastModified == null) {
            _lastModified = nCV();
        }
        return _lastModified;
    }

    @Override
    protected ConditionValue xgetCValueLastModified() {
        return xdfgetLastModified();
    }

    /**
     * Add order-by as ascend. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_LastModified_Asc() {
        regOBA("LAST_MODIFIED");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_LastModified_Desc() {
        regOBD("LAST_MODIFIED");
        return this;
    }

    protected ConditionValue _createTime;

    public ConditionValue xdfgetCreateTime() {
        if (_createTime == null) {
            _createTime = nCV();
        }
        return _createTime;
    }

    @Override
    protected ConditionValue xgetCValueCreateTime() {
        return xdfgetCreateTime();
    }

    /**
     * Add order-by as ascend. <br>
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsAccessResultCQ addOrderBy_CreateTime_Asc() {
        regOBA("CREATE_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * CREATE_TIME: {NotNull, BIGINT(19)}
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
     * }, <span style="color: #CC4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #CC4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #CC4747">aliasName</span>);
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
     * }, <span style="color: #CC4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #CC4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #CC4747">aliasName</span>);
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
     * Get the condition-query for relation table. <br>
     * ACCESS_RESULT_DATA by ID, named 'accessResultDataAsOne'.
     * @return The instance of condition-query. (NotNull)
     */
    public AccessResultDataCQ queryAccessResultDataAsOne() {
        return xdfgetConditionQueryAccessResultDataAsOne();
    }

    public AccessResultDataCQ xdfgetConditionQueryAccessResultDataAsOne() {
        final String prop = "accessResultDataAsOne";
        if (!xhasQueRlMap(prop)) {
            xregQueRl(prop, xcreateQueryAccessResultDataAsOne());
            xsetupOuterJoinAccessResultDataAsOne();
        }
        return xgetQueRlMap(prop);
    }

    protected AccessResultDataCQ xcreateQueryAccessResultDataAsOne() {
        final String nrp = xresolveNRP("ACCESS_RESULT", "accessResultDataAsOne");
        final String jan = xresolveJAN(nrp, xgetNNLvl());
        return xinitRelCQ(new AccessResultDataCQ(this, xgetSqlClause(), jan,
                xgetNNLvl()), _baseCB, "accessResultDataAsOne", nrp);
    }

    protected void xsetupOuterJoinAccessResultDataAsOne() {
        xregOutJo("accessResultDataAsOne");
    }

    public boolean hasConditionQueryAccessResultDataAsOne() {
        return xhasQueRlMap("accessResultDataAsOne");
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    public Map<String, AccessResultCQ> xdfgetScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final AccessResultCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, AccessResultCQ> xdfgetSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(final AccessResultCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, AccessResultCQ> xdfgetQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final AccessResultCQ sq) {
        return xkeepSQue("queryMyselfDerived", sq);
    }

    public Map<String, Object> xdfgetQueryMyselfDerivedParameter() {
        return xgetSQuePmMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object pm) {
        return xkeepSQuePm("queryMyselfDerived", pm);
    }

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    protected Map<String, AccessResultCQ> _myselfExistsMap;

    public Map<String, AccessResultCQ> xdfgetMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final AccessResultCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, AccessResultCQ> xdfgetMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    public String keepMyselfInScope(final AccessResultCQ sq) {
        return xkeepSQue("myselfInScope", sq);
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
        return HpQDRFunction.class.getName();
    }

    protected String xCOp() {
        return ConditionOption.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
