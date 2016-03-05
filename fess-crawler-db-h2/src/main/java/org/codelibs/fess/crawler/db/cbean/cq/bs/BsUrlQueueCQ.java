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
 * The base condition-query of URL_QUEUE.
 * @author DBFlute(AutoGenerator)
 */
public class BsUrlQueueCQ extends AbstractBsUrlQueueCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected UrlQueueCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsUrlQueueCQ(ConditionQuery referrerQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br>
     * {select ... from ... left outer join (select * from URL_QUEUE) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #CC4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public UrlQueueCIQ inline() {
        if (_inlineQuery == null) { _inlineQuery = xcreateCIQ(); }
        _inlineQuery.xsetOnClause(false); return _inlineQuery;
    }

    protected UrlQueueCIQ xcreateCIQ() {
        UrlQueueCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected UrlQueueCIQ xnewCIQ() {
        return new UrlQueueCIQ(xgetReferrerQuery(), xgetSqlClause(), xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br>
     * {select ... from ... left outer join URL_QUEUE on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #CC4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public UrlQueueCIQ on() {
        if (isBaseQuery()) { throw new IllegalConditionBeanOperationException("OnClause for local table is unavailable!"); }
        UrlQueueCIQ inlineQuery = inline(); inlineQuery.xsetOnClause(true); return inlineQuery;
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
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_Id_Asc() { regOBA("ID"); return this; }

    /**
     * Add order-by as descend. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_Id_Desc() { regOBD("ID"); return this; }

    protected ConditionValue _sessionId;
    public ConditionValue xdfgetSessionId()
    { if (_sessionId == null) { _sessionId = nCV(); }
      return _sessionId; }
    protected ConditionValue xgetCValueSessionId() { return xdfgetSessionId(); }

    /** 
     * Add order-by as ascend. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_SessionId_Asc() { regOBA("SESSION_ID"); return this; }

    /**
     * Add order-by as descend. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_SessionId_Desc() { regOBD("SESSION_ID"); return this; }

    protected ConditionValue _method;
    public ConditionValue xdfgetMethod()
    { if (_method == null) { _method = nCV(); }
      return _method; }
    protected ConditionValue xgetCValueMethod() { return xdfgetMethod(); }

    /** 
     * Add order-by as ascend. <br>
     * METHOD: {NotNull, VARCHAR(10)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_Method_Asc() { regOBA("METHOD"); return this; }

    /**
     * Add order-by as descend. <br>
     * METHOD: {NotNull, VARCHAR(10)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_Method_Desc() { regOBD("METHOD"); return this; }

    protected ConditionValue _url;
    public ConditionValue xdfgetUrl()
    { if (_url == null) { _url = nCV(); }
      return _url; }
    protected ConditionValue xgetCValueUrl() { return xdfgetUrl(); }

    /** 
     * Add order-by as ascend. <br>
     * URL: {NotNull, VARCHAR(65536)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_Url_Asc() { regOBA("URL"); return this; }

    /**
     * Add order-by as descend. <br>
     * URL: {NotNull, VARCHAR(65536)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_Url_Desc() { regOBD("URL"); return this; }

    protected ConditionValue _metaData;
    public ConditionValue xdfgetMetaData()
    { if (_metaData == null) { _metaData = nCV(); }
      return _metaData; }
    protected ConditionValue xgetCValueMetaData() { return xdfgetMetaData(); }

    /** 
     * Add order-by as ascend. <br>
     * META_DATA: {VARCHAR(65536)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_MetaData_Asc() { regOBA("META_DATA"); return this; }

    /**
     * Add order-by as descend. <br>
     * META_DATA: {VARCHAR(65536)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_MetaData_Desc() { regOBD("META_DATA"); return this; }

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
    public BsUrlQueueCQ addOrderBy_Encoding_Asc() { regOBA("ENCODING"); return this; }

    /**
     * Add order-by as descend. <br>
     * ENCODING: {VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_Encoding_Desc() { regOBD("ENCODING"); return this; }

    protected ConditionValue _parentUrl;
    public ConditionValue xdfgetParentUrl()
    { if (_parentUrl == null) { _parentUrl = nCV(); }
      return _parentUrl; }
    protected ConditionValue xgetCValueParentUrl() { return xdfgetParentUrl(); }

    /** 
     * Add order-by as ascend. <br>
     * PARENT_URL: {VARCHAR(65536)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_ParentUrl_Asc() { regOBA("PARENT_URL"); return this; }

    /**
     * Add order-by as descend. <br>
     * PARENT_URL: {VARCHAR(65536)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_ParentUrl_Desc() { regOBD("PARENT_URL"); return this; }

    protected ConditionValue _depth;
    public ConditionValue xdfgetDepth()
    { if (_depth == null) { _depth = nCV(); }
      return _depth; }
    protected ConditionValue xgetCValueDepth() { return xdfgetDepth(); }

    /** 
     * Add order-by as ascend. <br>
     * DEPTH: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_Depth_Asc() { regOBA("DEPTH"); return this; }

    /**
     * Add order-by as descend. <br>
     * DEPTH: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_Depth_Desc() { regOBD("DEPTH"); return this; }

    protected ConditionValue _lastModified;
    public ConditionValue xdfgetLastModified()
    { if (_lastModified == null) { _lastModified = nCV(); }
      return _lastModified; }
    protected ConditionValue xgetCValueLastModified() { return xdfgetLastModified(); }

    /** 
     * Add order-by as ascend. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_LastModified_Asc() { regOBA("LAST_MODIFIED"); return this; }

    /**
     * Add order-by as descend. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_LastModified_Desc() { regOBD("LAST_MODIFIED"); return this; }

    protected ConditionValue _createTime;
    public ConditionValue xdfgetCreateTime()
    { if (_createTime == null) { _createTime = nCV(); }
      return _createTime; }
    protected ConditionValue xgetCValueCreateTime() { return xdfgetCreateTime(); }

    /** 
     * Add order-by as ascend. <br>
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_CreateTime_Asc() { regOBA("CREATE_TIME"); return this; }

    /**
     * Add order-by as descend. <br>
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsUrlQueueCQ addOrderBy_CreateTime_Desc() { regOBD("CREATE_TIME"); return this; }

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
    public BsUrlQueueCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }

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
    public BsUrlQueueCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

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
    public Map<String, UrlQueueCQ> xdfgetScalarCondition() { return xgetSQueMap("scalarCondition"); }
    public String keepScalarCondition(UrlQueueCQ sq) { return xkeepSQue("scalarCondition", sq); }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, UrlQueueCQ> xdfgetSpecifyMyselfDerived() { return xgetSQueMap("specifyMyselfDerived"); }
    public String keepSpecifyMyselfDerived(UrlQueueCQ sq) { return xkeepSQue("specifyMyselfDerived", sq); }

    public Map<String, UrlQueueCQ> xdfgetQueryMyselfDerived() { return xgetSQueMap("queryMyselfDerived"); }
    public String keepQueryMyselfDerived(UrlQueueCQ sq) { return xkeepSQue("queryMyselfDerived", sq); }
    public Map<String, Object> xdfgetQueryMyselfDerivedParameter() { return xgetSQuePmMap("queryMyselfDerived"); }
    public String keepQueryMyselfDerivedParameter(Object pm) { return xkeepSQuePm("queryMyselfDerived", pm); }

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    protected Map<String, UrlQueueCQ> _myselfExistsMap;
    public Map<String, UrlQueueCQ> xdfgetMyselfExists() { return xgetSQueMap("myselfExists"); }
    public String keepMyselfExists(UrlQueueCQ sq) { return xkeepSQue("myselfExists", sq); }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, UrlQueueCQ> xdfgetMyselfInScope() { return xgetSQueMap("myselfInScope"); }
    public String keepMyselfInScope(UrlQueueCQ sq) { return xkeepSQue("myselfInScope", sq); }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() { return UrlQueueCB.class.getName(); }
    protected String xCQ() { return UrlQueueCQ.class.getName(); }
    protected String xCHp() { return HpQDRFunction.class.getName(); }
    protected String xCOp() { return ConditionOption.class.getName(); }
    protected String xMap() { return Map.class.getName(); }
}
