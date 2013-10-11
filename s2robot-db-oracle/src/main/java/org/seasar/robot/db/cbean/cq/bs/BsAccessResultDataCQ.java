/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.robot.db.cbean.AccessResultDataCB;
import org.seasar.robot.db.cbean.cq.AccessResultCQ;
import org.seasar.robot.db.cbean.cq.AccessResultDataCQ;
import org.seasar.robot.db.cbean.cq.ciq.AccessResultDataCIQ;

/**
 * The base condition-query of ACCESS_RESULT_DATA.
 * 
 * @author DBFlute(AutoGenerator)
 */
public class BsAccessResultDataCQ extends AbstractBsAccessResultDataCQ {

    // ===================================================================================
    // Attribute
    // =========
    protected AccessResultDataCIQ _inlineQuery;

    // ===================================================================================
    // Constructor
    // ===========
    public BsAccessResultDataCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    // InlineView/OrClause
    // ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from ACCESS_RESULT_DATA)
     * where FOO = [value] ...}
     * 
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * 
     * @return The condition-query for InlineView query. (NotNull)
     */
    public AccessResultDataCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected AccessResultDataCIQ xcreateCIQ() {
        final AccessResultDataCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected AccessResultDataCIQ xnewCIQ() {
        return new AccessResultDataCIQ(
            xgetReferrerQuery(),
            xgetSqlClause(),
            xgetAliasName(),
            xgetNestLevel(),
            this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join ACCESS_RESULT_DATA on ... and FOO =
     * [value] ...}
     * 
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * 
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException
     *             When this condition-query is base query.
     */
    public AccessResultDataCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                "OnClause for local table is unavailable!");
        }
        final AccessResultDataCIQ inlineQuery = inline();
        inlineQuery.xsetOnClause(true);
        return inlineQuery;
    }

    // ===================================================================================
    // Query
    // =====

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

    protected Map<String, AccessResultCQ> _id_InScopeRelation_AccessResultMap;

    public Map<String, AccessResultCQ> getId_InScopeRelation_AccessResult() {
        return _id_InScopeRelation_AccessResultMap;
    }

    @Override
    public String keepId_InScopeRelation_AccessResult(
            final AccessResultCQ subQuery) {
        if (_id_InScopeRelation_AccessResultMap == null) {
            _id_InScopeRelation_AccessResultMap = newLinkedHashMapSized(4);
        }
        final String key =
            "subQueryMapKey" + (_id_InScopeRelation_AccessResultMap.size() + 1);
        _id_InScopeRelation_AccessResultMap.put(key, subQuery);
        return "id_InScopeRelation_AccessResult." + key;
    }

    protected Map<String, AccessResultCQ> _id_NotInScopeRelation_AccessResultMap;

    public Map<String, AccessResultCQ> getId_NotInScopeRelation_AccessResult() {
        return _id_NotInScopeRelation_AccessResultMap;
    }

    @Override
    public String keepId_NotInScopeRelation_AccessResult(
            final AccessResultCQ subQuery) {
        if (_id_NotInScopeRelation_AccessResultMap == null) {
            _id_NotInScopeRelation_AccessResultMap = newLinkedHashMapSized(4);
        }
        final String key =
            "subQueryMapKey"
                + (_id_NotInScopeRelation_AccessResultMap.size() + 1);
        _id_NotInScopeRelation_AccessResultMap.put(key, subQuery);
        return "id_NotInScopeRelation_AccessResult." + key;
    }

    /**
     * Add order-by as ascend. <br />
     * ID: {PK, NotNull, NUMBER(19), FK to ACCESS_RESULT}
     * 
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, NotNull, NUMBER(19), FK to ACCESS_RESULT}
     * 
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _transformerName;

    public ConditionValue getTransformerName() {
        if (_transformerName == null) {
            _transformerName = nCV();
        }
        return _transformerName;
    }

    @Override
    protected ConditionValue getCValueTransformerName() {
        return getTransformerName();
    }

    /**
     * Add order-by as ascend. <br />
     * TRANSFORMER_NAME: {NotNull, VARCHAR2(255)}
     * 
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_TransformerName_Asc() {
        regOBA("TRANSFORMER_NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * TRANSFORMER_NAME: {NotNull, VARCHAR2(255)}
     * 
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_TransformerName_Desc() {
        regOBD("TRANSFORMER_NAME");
        return this;
    }

    protected ConditionValue _data;

    public ConditionValue getData() {
        if (_data == null) {
            _data = nCV();
        }
        return _data;
    }

    @Override
    protected ConditionValue getCValueData() {
        return getData();
    }

    /**
     * Add order-by as ascend. <br />
     * DATA: {BLOB(4000)}
     * 
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Data_Asc() {
        regOBA("DATA");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DATA: {BLOB(4000)}
     * 
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Data_Desc() {
        regOBD("DATA");
        return this;
    }

    protected ConditionValue _encoding;

    public ConditionValue getEncoding() {
        if (_encoding == null) {
            _encoding = nCV();
        }
        return _encoding;
    }

    @Override
    protected ConditionValue getCValueEncoding() {
        return getEncoding();
    }

    /**
     * Add order-by as ascend. <br />
     * ENCODING: {VARCHAR2(20)}
     * 
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Encoding_Asc() {
        regOBA("ENCODING");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ENCODING: {VARCHAR2(20)}
     * 
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Encoding_Desc() {
        regOBD("ENCODING");
        return this;
    }

    // ===================================================================================
    // SpecifiedDerivedOrderBy
    // =======================
    /**
     * Add order-by for specified derived column as ascend.
     * 
     * <pre>
     * cb.specify().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime();
     *     }
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #FD4747">aliasName</span>);
     * </pre>
     * 
     * @param aliasName
     *            The alias name specified at (Specify)DerivedReferrer.
     *            (NotNull)
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addSpecifiedDerivedOrderBy_Asc(
            final String aliasName) {
        registerSpecifiedDerivedOrderBy_Asc(aliasName);
        return this;
    }

    /**
     * Add order-by for specified derived column as descend.
     * 
     * <pre>
     * cb.specify().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime();
     *     }
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #FD4747">aliasName</span>);
     * </pre>
     * 
     * @param aliasName
     *            The alias name specified at (Specify)DerivedReferrer.
     *            (NotNull)
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addSpecifiedDerivedOrderBy_Desc(
            final String aliasName) {
        registerSpecifiedDerivedOrderBy_Desc(aliasName);
        return this;
    }

    // ===================================================================================
    // Union Query
    // ===========
    @Override
    protected void reflectRelationOnUnionQuery(
            final ConditionQuery baseQueryAsSuper,
            final ConditionQuery unionQueryAsSuper) {
        final AccessResultDataCQ baseQuery =
            (AccessResultDataCQ) baseQueryAsSuper;
        final AccessResultDataCQ unionQuery =
            (AccessResultDataCQ) unionQueryAsSuper;
        if (baseQuery.hasConditionQueryAccessResult()) {
            unionQuery.queryAccessResult().reflectRelationOnUnionQuery(
                baseQuery.queryAccessResult(),
                unionQuery.queryAccessResult());
        }
    }

    // ===================================================================================
    // Foreign Query
    // =============
    /**
     * Get the condition-query for relation table. <br />
     * ACCESS_RESULT by my ID, named 'accessResult'.
     * 
     * @return The instance of condition-query. (NotNull)
     */
    public AccessResultCQ queryAccessResult() {
        return getConditionQueryAccessResult();
    }

    protected AccessResultCQ _conditionQueryAccessResult;

    public AccessResultCQ getConditionQueryAccessResult() {
        if (_conditionQueryAccessResult == null) {
            _conditionQueryAccessResult = xcreateQueryAccessResult();
            xsetupOuterJoinAccessResult();
        }
        return _conditionQueryAccessResult;
    }

    protected AccessResultCQ xcreateQueryAccessResult() {
        final String nrp =
            resolveNextRelationPath("ACCESS_RESULT_DATA", "accessResult");
        final String jan = resolveJoinAliasName(nrp, xgetNextNestLevel());
        final AccessResultCQ cq =
            new AccessResultCQ(this, xgetSqlClause(), jan, xgetNextNestLevel());
        cq.xsetBaseCB(_baseCB);
        cq.xsetForeignPropertyName("accessResult");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinAccessResult() {
        final AccessResultCQ cq = getConditionQueryAccessResult();
        final Map<String, String> joinOnMap = newLinkedHashMapSized(4);
        joinOnMap.put("ID", "ID");
        registerOuterJoin(cq, joinOnMap, "accessResult");
    }

    public boolean hasConditionQueryAccessResult() {
        return _conditionQueryAccessResult != null;
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    // ScalarCondition
    // ===============
    protected Map<String, AccessResultDataCQ> _scalarConditionMap;

    public Map<String, AccessResultDataCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final AccessResultDataCQ subQuery) {
        if (_scalarConditionMap == null) {
            _scalarConditionMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey" + (_scalarConditionMap.size() + 1);
        _scalarConditionMap.put(key, subQuery);
        return "scalarCondition." + key;
    }

    // ===================================================================================
    // MyselfDerived
    // =============
    protected Map<String, AccessResultDataCQ> _specifyMyselfDerivedMap;

    public Map<String, AccessResultDataCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final AccessResultDataCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key =
            "subQueryMapKey" + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, AccessResultDataCQ> _queryMyselfDerivedMap;

    public Map<String, AccessResultDataCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final AccessResultDataCQ subQuery) {
        if (_queryMyselfDerivedMap == null) {
            _queryMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key =
            "subQueryMapKey" + (_queryMyselfDerivedMap.size() + 1);
        _queryMyselfDerivedMap.put(key, subQuery);
        return "queryMyselfDerived." + key;
    }

    protected Map<String, Object> _qyeryMyselfDerivedParameterMap;

    public Map<String, Object> getQueryMyselfDerivedParameter() {
        return _qyeryMyselfDerivedParameterMap;
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object parameterValue) {
        if (_qyeryMyselfDerivedParameterMap == null) {
            _qyeryMyselfDerivedParameterMap = newLinkedHashMapSized(4);
        }
        final String key =
            "subQueryParameterKey"
                + (_qyeryMyselfDerivedParameterMap.size() + 1);
        _qyeryMyselfDerivedParameterMap.put(key, parameterValue);
        return "queryMyselfDerivedParameter." + key;
    }

    // ===================================================================================
    // MyselfExists
    // ============
    protected Map<String, AccessResultDataCQ> _myselfExistsMap;

    public Map<String, AccessResultDataCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final AccessResultDataCQ subQuery) {
        if (_myselfExistsMap == null) {
            _myselfExistsMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey" + (_myselfExistsMap.size() + 1);
        _myselfExistsMap.put(key, subQuery);
        return "myselfExists." + key;
    }

    // ===================================================================================
    // MyselfInScope
    // =============
    protected Map<String, AccessResultDataCQ> _myselfInScopeMap;

    public Map<String, AccessResultDataCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final AccessResultDataCQ subQuery) {
        if (_myselfInScopeMap == null) {
            _myselfInScopeMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey" + (_myselfInScopeMap.size() + 1);
        _myselfInScopeMap.put(key, subQuery);
        return "myselfInScope." + key;
    }

    // ===================================================================================
    // Very Internal
    // =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return AccessResultDataCB.class.getName();
    }

    protected String xCQ() {
        return AccessResultDataCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
