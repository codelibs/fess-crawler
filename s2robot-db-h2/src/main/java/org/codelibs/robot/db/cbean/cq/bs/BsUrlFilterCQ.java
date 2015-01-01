package org.codelibs.robot.db.cbean.cq.bs;

import java.util.Map;

import org.codelibs.robot.db.cbean.UrlFilterCB;
import org.codelibs.robot.db.cbean.cq.UrlFilterCQ;
import org.codelibs.robot.db.cbean.cq.ciq.UrlFilterCIQ;
import org.dbflute.cbean.ConditionQuery;
import org.dbflute.cbean.chelper.HpQDRFunction;
import org.dbflute.cbean.coption.ConditionOption;
import org.dbflute.cbean.cvalue.ConditionValue;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.dbflute.exception.IllegalConditionBeanOperationException;

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
    public BsUrlFilterCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br>
     * {select ... from ... left outer join (select * from URL_FILTER) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #CC4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public UrlFilterCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected UrlFilterCIQ xcreateCIQ() {
        final UrlFilterCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected UrlFilterCIQ xnewCIQ() {
        return new UrlFilterCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br>
     * {select ... from ... left outer join URL_FILTER on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #CC4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public UrlFilterCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final UrlFilterCIQ inlineQuery = inline();
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
    public BsUrlFilterCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_Id_Desc() {
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
    public BsUrlFilterCQ addOrderBy_SessionId_Asc() {
        regOBA("SESSION_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_SessionId_Desc() {
        regOBD("SESSION_ID");
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
     * URL: {NotNull, VARCHAR(65536)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_Url_Asc() {
        regOBA("URL");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * URL: {NotNull, VARCHAR(65536)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_Url_Desc() {
        regOBD("URL");
        return this;
    }

    protected ConditionValue _filterType;

    public ConditionValue xdfgetFilterType() {
        if (_filterType == null) {
            _filterType = nCV();
        }
        return _filterType;
    }

    @Override
    protected ConditionValue xgetCValueFilterType() {
        return xdfgetFilterType();
    }

    /**
     * Add order-by as ascend. <br>
     * FILTER_TYPE: {NotNull, VARCHAR(1)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_FilterType_Asc() {
        regOBA("FILTER_TYPE");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * FILTER_TYPE: {NotNull, VARCHAR(1)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_FilterType_Desc() {
        regOBD("FILTER_TYPE");
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
    public BsUrlFilterCQ addOrderBy_CreateTime_Asc() {
        regOBA("CREATE_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br>
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsUrlFilterCQ addOrderBy_CreateTime_Desc() {
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
    public BsUrlFilterCQ addSpecifiedDerivedOrderBy_Asc(final String aliasName) {
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
    public BsUrlFilterCQ addSpecifiedDerivedOrderBy_Desc(final String aliasName) {
        registerSpecifiedDerivedOrderBy_Desc(aliasName);
        return this;
    }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    @Override
    public void reflectRelationOnUnionQuery(final ConditionQuery bqs,
            final ConditionQuery uqs) {
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    public Map<String, UrlFilterCQ> xdfgetScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final UrlFilterCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, UrlFilterCQ> xdfgetSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(final UrlFilterCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, UrlFilterCQ> xdfgetQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final UrlFilterCQ sq) {
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
    protected Map<String, UrlFilterCQ> _myselfExistsMap;

    public Map<String, UrlFilterCQ> xdfgetMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final UrlFilterCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, UrlFilterCQ> xdfgetMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    public String keepMyselfInScope(final UrlFilterCQ sq) {
        return xkeepSQue("myselfInScope", sq);
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
