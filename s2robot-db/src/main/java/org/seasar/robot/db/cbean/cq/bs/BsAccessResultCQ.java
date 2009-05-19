package org.seasar.robot.db.cbean.cq.bs;

import java.util.Map;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
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
    public BsAccessResultCQ(ConditionQuery childQuery, SqlClause sqlClause,
            String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from ACCESS_RESULT) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public AccessResultCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = new AccessResultCIQ(getChildQuery(), getSqlClause(),
                    getAliasName(), getNestLevel(), this);
        }
        _inlineQuery.xsetOnClauseInline(false);
        return _inlineQuery;
    }

    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join ACCESS_RESULT on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public AccessResultCIQ on() {
        if (isBaseQuery(this)) {
            throw new UnsupportedOperationException(
                    "Unsupported on-clause for local table!");
        }
        AccessResultCIQ inlineQuery = inline();
        inlineQuery.xsetOnClauseInline(true);
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

    protected Map<String, AccessResultDataCQ> _id_InScopeSubQuery_AccessResultDataAsOneMap;

    public Map<String, AccessResultDataCQ> getId_InScopeSubQuery_AccessResultDataAsOne() {
        return _id_InScopeSubQuery_AccessResultDataAsOneMap;
    }

    public String keepId_InScopeSubQuery_AccessResultDataAsOne(
            AccessResultDataCQ subQuery) {
        if (_id_InScopeSubQuery_AccessResultDataAsOneMap == null) {
            _id_InScopeSubQuery_AccessResultDataAsOneMap = newLinkedHashMap();
        }
        String key = "subQueryMapKey"
                + (_id_InScopeSubQuery_AccessResultDataAsOneMap.size() + 1);
        _id_InScopeSubQuery_AccessResultDataAsOneMap.put(key, subQuery);
        return "id_InScopeSubQuery_AccessResultDataAsOne." + key;
    }

    protected Map<String, AccessResultDataCQ> _id_NotInScopeSubQuery_AccessResultDataAsOneMap;

    public Map<String, AccessResultDataCQ> getId_NotInScopeSubQuery_AccessResultDataAsOne() {
        return _id_NotInScopeSubQuery_AccessResultDataAsOneMap;
    }

    public String keepId_NotInScopeSubQuery_AccessResultDataAsOne(
            AccessResultDataCQ subQuery) {
        if (_id_NotInScopeSubQuery_AccessResultDataAsOneMap == null) {
            _id_NotInScopeSubQuery_AccessResultDataAsOneMap = newLinkedHashMap();
        }
        String key = "subQueryMapKey"
                + (_id_NotInScopeSubQuery_AccessResultDataAsOneMap.size() + 1);
        _id_NotInScopeSubQuery_AccessResultDataAsOneMap.put(key, subQuery);
        return "id_NotInScopeSubQuery_AccessResultDataAsOne." + key;
    }

    protected Map<String, AccessResultDataCQ> _id_ExistsSubQuery_AccessResultDataAsOneMap;

    public Map<String, AccessResultDataCQ> getId_ExistsSubQuery_AccessResultDataAsOne() {
        return _id_ExistsSubQuery_AccessResultDataAsOneMap;
    }

    public String keepId_ExistsSubQuery_AccessResultDataAsOne(
            AccessResultDataCQ subQuery) {
        if (_id_ExistsSubQuery_AccessResultDataAsOneMap == null) {
            _id_ExistsSubQuery_AccessResultDataAsOneMap = newLinkedHashMap();
        }
        String key = "subQueryMapKey"
                + (_id_ExistsSubQuery_AccessResultDataAsOneMap.size() + 1);
        _id_ExistsSubQuery_AccessResultDataAsOneMap.put(key, subQuery);
        return "id_ExistsSubQuery_AccessResultDataAsOne." + key;
    }

    protected Map<String, AccessResultDataCQ> _id_NotExistsSubQuery_AccessResultDataAsOneMap;

    public Map<String, AccessResultDataCQ> getId_NotExistsSubQuery_AccessResultDataAsOne() {
        return _id_NotExistsSubQuery_AccessResultDataAsOneMap;
    }

    public String keepId_NotExistsSubQuery_AccessResultDataAsOne(
            AccessResultDataCQ subQuery) {
        if (_id_NotExistsSubQuery_AccessResultDataAsOneMap == null) {
            _id_NotExistsSubQuery_AccessResultDataAsOneMap = newLinkedHashMap();
        }
        String key = "subQueryMapKey"
                + (_id_NotExistsSubQuery_AccessResultDataAsOneMap.size() + 1);
        _id_NotExistsSubQuery_AccessResultDataAsOneMap.put(key, subQuery);
        return "id_NotExistsSubQuery_AccessResultDataAsOne." + key;
    }

    public BsAccessResultCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

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

    public BsAccessResultCQ addOrderBy_SessionId_Asc() {
        regOBA("SESSION_ID");
        return this;
    }

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

    public BsAccessResultCQ addOrderBy_RuleId_Asc() {
        regOBA("RULE_ID");
        return this;
    }

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

    public BsAccessResultCQ addOrderBy_Url_Asc() {
        regOBA("URL");
        return this;
    }

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

    public BsAccessResultCQ addOrderBy_ParentUrl_Asc() {
        regOBA("PARENT_URL");
        return this;
    }

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

    public BsAccessResultCQ addOrderBy_Status_Asc() {
        regOBA("STATUS");
        return this;
    }

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

    public BsAccessResultCQ addOrderBy_HttpStatusCode_Asc() {
        regOBA("HTTP_STATUS_CODE");
        return this;
    }

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

    public BsAccessResultCQ addOrderBy_Method_Asc() {
        regOBA("METHOD");
        return this;
    }

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

    public BsAccessResultCQ addOrderBy_MimeType_Asc() {
        regOBA("MIME_TYPE");
        return this;
    }

    public BsAccessResultCQ addOrderBy_MimeType_Desc() {
        regOBD("MIME_TYPE");
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

    public BsAccessResultCQ addOrderBy_ExecutionTime_Asc() {
        regOBA("EXECUTION_TIME");
        return this;
    }

    public BsAccessResultCQ addOrderBy_ExecutionTime_Desc() {
        regOBD("EXECUTION_TIME");
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

    public BsAccessResultCQ addOrderBy_CreateTime_Asc() {
        regOBA("CREATE_TIME");
        return this;
    }

    public BsAccessResultCQ addOrderBy_CreateTime_Desc() {
        regOBD("CREATE_TIME");
        return this;
    }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsAccessResultCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) {
        registerSpecifiedDerivedOrderBy_Asc(aliasName);
        return this;
    }

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
        String jan = resolveJoinAliasName(nrp, getNextNestLevel());
        AccessResultDataCQ cq = new AccessResultDataCQ(this, getSqlClause(),
                jan, getNextNestLevel());
        cq.xsetForeignPropertyName("accessResultDataAsOne");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinAccessResultDataAsOne() {
        AccessResultDataCQ cq = getConditionQueryAccessResultDataAsOne();
        Map<String, String> joinOnMap = newLinkedHashMap();
        joinOnMap.put(getRealColumnName("ID"), cq.getRealColumnName("ID"));
        registerOuterJoin(cq, joinOnMap);
    }

    public boolean hasConditionQueryAccessResultDataAsOne() {
        return _conditionQueryAccessResultDataAsOne != null;
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    protected Map<String, AccessResultCQ> _scalarSubQueryMap;

    public Map<String, AccessResultCQ> getScalarSubQuery() {
        return _scalarSubQueryMap;
    }

    public String keepScalarSubQuery(AccessResultCQ subQuery) {
        if (_scalarSubQueryMap == null) {
            _scalarSubQueryMap = newLinkedHashMap();
        }
        String key = "subQueryMapKey" + (_scalarSubQueryMap.size() + 1);
        _scalarSubQueryMap.put(key, subQuery);
        return "scalarSubQuery." + key;
    }

    // ===================================================================================
    //                                                             MySelf InScope SubQuery
    //                                                             =======================
    protected Map<String, AccessResultCQ> _myselfInScopeSubQueryMap;

    public Map<String, AccessResultCQ> getMyselfInScopeSubQuery() {
        return _myselfInScopeSubQueryMap;
    }

    public String keepMyselfInScopeSubQuery(AccessResultCQ subQuery) {
        if (_myselfInScopeSubQueryMap == null) {
            _myselfInScopeSubQueryMap = newLinkedHashMap();
        }
        String key = "subQueryMapKey" + (_myselfInScopeSubQueryMap.size() + 1);
        _myselfInScopeSubQueryMap.put(key, subQuery);
        return "myselfInScopeSubQuery." + key;
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    String xCB() {
        return AccessResultCB.class.getName();
    }

    String xCQ() {
        return AccessResultCQ.class.getName();
    }

    String xMap() {
        return Map.class.getName();
    }
}
