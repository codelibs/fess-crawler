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

import org.seasar.robot.dbflute.cbean.*;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.exception.IllegalConditionBeanOperationException;
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
    public BsUrlFilterCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from URL_FILTER) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public UrlFilterCIQ inline() {
        if (_inlineQuery == null) { _inlineQuery = createInlineQuery(); }
        _inlineQuery.xsetOnClauseInline(false); return _inlineQuery;
    }

    protected UrlFilterCIQ createInlineQuery()
    { return new UrlFilterCIQ(getReferrerQuery(), getSqlClause(), getAliasName(), getNestLevel(), this); }

    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join URL_FILTER on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public UrlFilterCIQ on() {
        if (isBaseQuery(this)) { throw new IllegalConditionBeanOperationException("On-clause for local table is unavailable!"); }
        UrlFilterCIQ inlineQuery = inline(); inlineQuery.xsetOnClauseInline(true); return inlineQuery;
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

    public BsUrlFilterCQ addOrderBy_Id_Asc() { regOBA("ID"); return this; }
    public BsUrlFilterCQ addOrderBy_Id_Desc() { regOBD("ID"); return this; }

    protected ConditionValue _sessionId;
    public ConditionValue getSessionId() {
        if (_sessionId == null) { _sessionId = nCV(); }
        return _sessionId;
    }
    protected ConditionValue getCValueSessionId() { return getSessionId(); }

    public BsUrlFilterCQ addOrderBy_SessionId_Asc() { regOBA("SESSION_ID"); return this; }
    public BsUrlFilterCQ addOrderBy_SessionId_Desc() { regOBD("SESSION_ID"); return this; }

    protected ConditionValue _url;
    public ConditionValue getUrl() {
        if (_url == null) { _url = nCV(); }
        return _url;
    }
    protected ConditionValue getCValueUrl() { return getUrl(); }

    public BsUrlFilterCQ addOrderBy_Url_Asc() { regOBA("URL"); return this; }
    public BsUrlFilterCQ addOrderBy_Url_Desc() { regOBD("URL"); return this; }

    protected ConditionValue _filterType;
    public ConditionValue getFilterType() {
        if (_filterType == null) { _filterType = nCV(); }
        return _filterType;
    }
    protected ConditionValue getCValueFilterType() { return getFilterType(); }

    public BsUrlFilterCQ addOrderBy_FilterType_Asc() { regOBA("FILTER_TYPE"); return this; }
    public BsUrlFilterCQ addOrderBy_FilterType_Desc() { regOBD("FILTER_TYPE"); return this; }

    protected ConditionValue _createTime;
    public ConditionValue getCreateTime() {
        if (_createTime == null) { _createTime = nCV(); }
        return _createTime;
    }
    protected ConditionValue getCValueCreateTime() { return getCreateTime(); }

    public BsUrlFilterCQ addOrderBy_CreateTime_Asc() { regOBA("CREATE_TIME"); return this; }
    public BsUrlFilterCQ addOrderBy_CreateTime_Desc() { regOBD("CREATE_TIME"); return this; }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsUrlFilterCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }
    public BsUrlFilterCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper, ConditionQuery unionQueryAsSuper) {
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    protected Map<String, UrlFilterCQ> _scalarSubQueryMap;
    public Map<String, UrlFilterCQ> getScalarSubQuery() { return _scalarSubQueryMap; }
    public String keepScalarSubQuery(UrlFilterCQ subQuery) {
        if (_scalarSubQueryMap == null) { _scalarSubQueryMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_scalarSubQueryMap.size() + 1);
        _scalarSubQueryMap.put(key, subQuery); return "scalarSubQuery." + key;
    }

    // ===================================================================================
    //                                                             MySelf InScope SubQuery
    //                                                             =======================
    protected Map<String, UrlFilterCQ> _myselfInScopeSubQueryMap;
    public Map<String, UrlFilterCQ> getMyselfInScopeSubQuery() { return _myselfInScopeSubQueryMap; }
    public String keepMyselfInScopeSubQuery(UrlFilterCQ subQuery) {
        if (_myselfInScopeSubQueryMap == null) { _myselfInScopeSubQueryMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_myselfInScopeSubQueryMap.size() + 1);
        _myselfInScopeSubQueryMap.put(key, subQuery); return "myselfInScopeSubQuery." + key;
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    String xCB() { return UrlFilterCB.class.getName(); }
    String xCQ() { return UrlFilterCQ.class.getName(); }
    String xMap() { return Map.class.getName(); }
}
