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

import org.seasar.robot.db.cbean.AccessResultDataCB;
import org.seasar.robot.db.cbean.cq.AccessResultCQ;
import org.seasar.robot.db.cbean.cq.AccessResultDataCQ;
import org.seasar.robot.db.cbean.cq.ciq.AccessResultDataCIQ;
import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.exception.IllegalConditionBeanOperationException;

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
    public BsAccessResultDataCQ(final ConditionQuery childQuery, final SqlClause sqlClause,
            final String aliasName, final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    // Inline
    // ======
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
            _inlineQuery = createInlineQuery();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected AccessResultDataCIQ createInlineQuery() {
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
    public String keepId_InScopeRelation_AccessResult(final AccessResultCQ subQuery) {
        if (_id_InScopeRelation_AccessResultMap == null) {
            _id_InScopeRelation_AccessResultMap = newLinkedHashMap();
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
    public String keepId_NotInScopeRelation_AccessResult(final AccessResultCQ subQuery) {
        if (_id_NotInScopeRelation_AccessResultMap == null) {
            _id_NotInScopeRelation_AccessResultMap = newLinkedHashMap();
        }
        final String key =
            "subQueryMapKey"
                + (_id_NotInScopeRelation_AccessResultMap.size() + 1);
        _id_NotInScopeRelation_AccessResultMap.put(key, subQuery);
        return "id_NotInScopeRelation_AccessResult." + key;
    }

    /**
     * Add order-by as ascend.
     * 
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend.
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
     * Add order-by as ascend.
     * 
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_TransformerName_Asc() {
        regOBA("TRANSFORMER_NAME");
        return this;
    }

    /**
     * Add order-by as descend.
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
     * Add order-by as ascend.
     * 
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Data_Asc() {
        regOBA("DATA");
        return this;
    }

    /**
     * Add order-by as descend.
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
     * Add order-by as ascend.
     * 
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Encoding_Asc() {
        regOBA("ENCODING");
        return this;
    }

    /**
     * Add order-by as descend.
     * 
     * @return this. (NotNull)
     */
    public BsAccessResultDataCQ addOrderBy_Encoding_Desc() {
        regOBD("ENCODING");
        return this;
    }

    // ===================================================================================
    // Specified Derived OrderBy
    // =========================
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
    public BsAccessResultDataCQ addSpecifiedDerivedOrderBy_Asc(final String aliasName) {
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
    public BsAccessResultDataCQ addSpecifiedDerivedOrderBy_Desc(final String aliasName) {
        registerSpecifiedDerivedOrderBy_Desc(aliasName);
        return this;
    }

    // ===================================================================================
    // Union Query
    // ===========
    @Override
    protected void reflectRelationOnUnionQuery(final ConditionQuery baseQueryAsSuper,
            final ConditionQuery unionQueryAsSuper) {
        final AccessResultDataCQ baseQuery = (AccessResultDataCQ) baseQueryAsSuper;
        final AccessResultDataCQ unionQuery = (AccessResultDataCQ) unionQueryAsSuper;
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
     * ACCESS_RESULT as 'accessResult'.
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
        cq.xsetForeignPropertyName("accessResult");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinAccessResult() {
        final AccessResultCQ cq = getConditionQueryAccessResult();
        final Map<String, String> joinOnMap = newLinkedHashMap();
        joinOnMap.put("ID", "ID");
        registerOuterJoin(cq, joinOnMap);
    }

    public boolean hasConditionQueryAccessResult() {
        return _conditionQueryAccessResult != null;
    }

    // ===================================================================================
    // Scalar SubQuery
    // ===============
    protected Map<String, AccessResultDataCQ> _scalarConditionMap;

    public Map<String, AccessResultDataCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final AccessResultDataCQ subQuery) {
        if (_scalarConditionMap == null) {
            _scalarConditionMap = newLinkedHashMap();
        }
        final String key = "subQueryMapKey" + (_scalarConditionMap.size() + 1);
        _scalarConditionMap.put(key, subQuery);
        return "scalarCondition." + key;
    }

    // ===================================================================================
    // MySelf InScope SubQuery
    // =======================
    protected Map<String, AccessResultDataCQ> _myselfInScopeRelationMap;

    public Map<String, AccessResultDataCQ> getMyselfInScopeRelation() {
        return _myselfInScopeRelationMap;
    }

    @Override
    public String keepMyselfInScopeRelation(final AccessResultDataCQ subQuery) {
        if (_myselfInScopeRelationMap == null) {
            _myselfInScopeRelationMap = newLinkedHashMap();
        }
        final String key = "subQueryMapKey" + (_myselfInScopeRelationMap.size() + 1);
        _myselfInScopeRelationMap.put(key, subQuery);
        return "myselfInScopeRelation." + key;
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
