package org.seasar.robot.db.cbean.cq.bs;

import java.util.Map;

import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.robot.db.cbean.UrlQueueCB;
import org.seasar.robot.db.cbean.cq.UrlQueueCQ;
import org.seasar.robot.db.cbean.cq.ciq.UrlQueueCIQ;

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
    public BsUrlQueueCQ(ConditionQuery childQuery, SqlClause sqlClause,
            String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from URL_QUEUE) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public UrlQueueCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = createInlineQuery();
        }
        _inlineQuery.xsetOnClauseInline(false);
        return _inlineQuery;
    }

    protected UrlQueueCIQ createInlineQuery() {
        return new UrlQueueCIQ(getReferrerQuery(), getSqlClause(),
                getAliasName(), getNestLevel(), this);
    }

    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join URL_QUEUE on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public UrlQueueCIQ on() {
        if (isBaseQuery(this)) {
            throw new IllegalConditionBeanOperationException(
                    "On-clause for local table is unavailable!");
        }
        UrlQueueCIQ inlineQuery = inline();
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

    public BsUrlQueueCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    public BsUrlQueueCQ addOrderBy_Id_Desc() {
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

    public BsUrlQueueCQ addOrderBy_SessionId_Asc() {
        regOBA("SESSION_ID");
        return this;
    }

    public BsUrlQueueCQ addOrderBy_SessionId_Desc() {
        regOBD("SESSION_ID");
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

    public BsUrlQueueCQ addOrderBy_Method_Asc() {
        regOBA("METHOD");
        return this;
    }

    public BsUrlQueueCQ addOrderBy_Method_Desc() {
        regOBD("METHOD");
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

    public BsUrlQueueCQ addOrderBy_Url_Asc() {
        regOBA("URL");
        return this;
    }

    public BsUrlQueueCQ addOrderBy_Url_Desc() {
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

    public BsUrlQueueCQ addOrderBy_ParentUrl_Asc() {
        regOBA("PARENT_URL");
        return this;
    }

    public BsUrlQueueCQ addOrderBy_ParentUrl_Desc() {
        regOBD("PARENT_URL");
        return this;
    }

    protected ConditionValue _depth;

    public ConditionValue getDepth() {
        if (_depth == null) {
            _depth = nCV();
        }
        return _depth;
    }

    protected ConditionValue getCValueDepth() {
        return getDepth();
    }

    public BsUrlQueueCQ addOrderBy_Depth_Asc() {
        regOBA("DEPTH");
        return this;
    }

    public BsUrlQueueCQ addOrderBy_Depth_Desc() {
        regOBD("DEPTH");
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

    public BsUrlQueueCQ addOrderBy_CreateTime_Asc() {
        regOBA("CREATE_TIME");
        return this;
    }

    public BsUrlQueueCQ addOrderBy_CreateTime_Desc() {
        regOBD("CREATE_TIME");
        return this;
    }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsUrlQueueCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) {
        registerSpecifiedDerivedOrderBy_Asc(aliasName);
        return this;
    }

    public BsUrlQueueCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) {
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
    protected Map<String, UrlQueueCQ> _scalarSubQueryMap;

    public Map<String, UrlQueueCQ> getScalarSubQuery() {
        return _scalarSubQueryMap;
    }

    public String keepScalarSubQuery(UrlQueueCQ subQuery) {
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
    protected Map<String, UrlQueueCQ> _myselfInScopeSubQueryMap;

    public Map<String, UrlQueueCQ> getMyselfInScopeSubQuery() {
        return _myselfInScopeSubQueryMap;
    }

    public String keepMyselfInScopeSubQuery(UrlQueueCQ subQuery) {
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
        return UrlQueueCB.class.getName();
    }

    String xCQ() {
        return UrlQueueCQ.class.getName();
    }

    String xMap() {
        return Map.class.getName();
    }
}
