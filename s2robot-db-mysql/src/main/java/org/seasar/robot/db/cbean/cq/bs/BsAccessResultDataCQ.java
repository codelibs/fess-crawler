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
 * @author DBFlute(AutoGenerator)
 */
public class BsAccessResultDataCQ extends AbstractBsAccessResultDataCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected AccessResultDataCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsAccessResultDataCQ(ConditionQuery childQuery, SqlClause sqlClause,
            String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from ACCESS_RESULT_DATA) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public AccessResultDataCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = createInlineQuery();
        }
        _inlineQuery.xsetOnClauseInline(false);
        return _inlineQuery;
    }

    protected AccessResultDataCIQ createInlineQuery() {
        return new AccessResultDataCIQ(getReferrerQuery(), getSqlClause(),
                getAliasName(), getNestLevel(), this);
    }

    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join ACCESS_RESULT_DATA on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public AccessResultDataCIQ on() {
        if (isBaseQuery(this)) {
            throw new IllegalConditionBeanOperationException(
                    "On-clause for local table is unavailable!");
        }
        AccessResultDataCIQ inlineQuery = inline();
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

    protected Map<String, AccessResultCQ> _id_InScopeSubQuery_AccessResultMap;

    public Map<String, AccessResultCQ> getId_InScopeSubQuery_AccessResult() {
        return _id_InScopeSubQuery_AccessResultMap;
    }

    public String keepId_InScopeSubQuery_AccessResult(AccessResultCQ subQuery) {
        if (_id_InScopeSubQuery_AccessResultMap == null) {
            _id_InScopeSubQuery_AccessResultMap = newLinkedHashMap();
        }
        String key = "subQueryMapKey"
                + (_id_InScopeSubQuery_AccessResultMap.size() + 1);
        _id_InScopeSubQuery_AccessResultMap.put(key, subQuery);
        return "id_InScopeSubQuery_AccessResult." + key;
    }

    public BsAccessResultDataCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

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

    protected ConditionValue getCValueTransformerName() {
        return getTransformerName();
    }

    public BsAccessResultDataCQ addOrderBy_TransformerName_Asc() {
        regOBA("TRANSFORMER_NAME");
        return this;
    }

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

    protected ConditionValue getCValueData() {
        return getData();
    }

    public BsAccessResultDataCQ addOrderBy_Data_Asc() {
        regOBA("DATA");
        return this;
    }

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

    protected ConditionValue getCValueEncoding() {
        return getEncoding();
    }

    public BsAccessResultDataCQ addOrderBy_Encoding_Asc() {
        regOBA("ENCODING");
        return this;
    }

    public BsAccessResultDataCQ addOrderBy_Encoding_Desc() {
        regOBD("ENCODING");
        return this;
    }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsAccessResultDataCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) {
        registerSpecifiedDerivedOrderBy_Asc(aliasName);
        return this;
    }

    public BsAccessResultDataCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) {
        registerSpecifiedDerivedOrderBy_Desc(aliasName);
        return this;
    }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper,
            ConditionQuery unionQueryAsSuper) {
        AccessResultDataCQ baseQuery = (AccessResultDataCQ) baseQueryAsSuper;
        AccessResultDataCQ unionQuery = (AccessResultDataCQ) unionQueryAsSuper;
        if (baseQuery.hasConditionQueryAccessResult()) {
            unionQuery.queryAccessResult().reflectRelationOnUnionQuery(
                    baseQuery.queryAccessResult(),
                    unionQuery.queryAccessResult());
        }
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
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
        String nrp = resolveNextRelationPath("ACCESS_RESULT_DATA",
                "accessResult");
        String jan = resolveJoinAliasName(nrp, getNextNestLevel());
        AccessResultCQ cq = new AccessResultCQ(this, getSqlClause(), jan,
                getNextNestLevel());
        cq.xsetForeignPropertyName("accessResult");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinAccessResult() {
        AccessResultCQ cq = getConditionQueryAccessResult();
        Map<String, String> joinOnMap = newLinkedHashMap();
        joinOnMap.put(getRealColumnName("ID"), cq.getRealColumnName("ID"));
        registerOuterJoin(cq, joinOnMap);
    }

    public boolean hasConditionQueryAccessResult() {
        return _conditionQueryAccessResult != null;
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    protected Map<String, AccessResultDataCQ> _scalarSubQueryMap;

    public Map<String, AccessResultDataCQ> getScalarSubQuery() {
        return _scalarSubQueryMap;
    }

    public String keepScalarSubQuery(AccessResultDataCQ subQuery) {
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
    protected Map<String, AccessResultDataCQ> _myselfInScopeSubQueryMap;

    public Map<String, AccessResultDataCQ> getMyselfInScopeSubQuery() {
        return _myselfInScopeSubQueryMap;
    }

    public String keepMyselfInScopeSubQuery(AccessResultDataCQ subQuery) {
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
        return AccessResultDataCB.class.getName();
    }

    String xCQ() {
        return AccessResultDataCQ.class.getName();
    }

    String xMap() {
        return Map.class.getName();
    }
}
