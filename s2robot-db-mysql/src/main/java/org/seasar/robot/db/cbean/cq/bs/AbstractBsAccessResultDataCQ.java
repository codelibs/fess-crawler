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

import java.util.Collection;

import org.seasar.robot.db.allcommon.DBMetaInstanceHandler;
import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.cbean.AccessResultDataCB;
import org.seasar.robot.db.cbean.cq.AccessResultCQ;
import org.seasar.robot.db.cbean.cq.AccessResultDataCQ;
import org.seasar.robot.dbflute.cbean.AbstractConditionQuery;
import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.SubQuery;
import org.seasar.robot.dbflute.cbean.chelper.HpSSQFunction;
import org.seasar.robot.dbflute.cbean.chelper.HpSSQSetupper;
import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;
import org.seasar.robot.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;

/**
 * The abstract condition-query of ACCESS_RESULT_DATA.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsAccessResultDataCQ extends
        AbstractConditionQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsAccessResultDataCQ(ConditionQuery childQuery,
            SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                     DBMeta Provider
    //                                                                     ===============
    @Override
    protected DBMetaProvider getDBMetaProvider() {
        return _dbmetaProvider;
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "ACCESS_RESULT_DATA";
    }

    public String getTableSqlName() {
        return "ACCESS_RESULT_DATA";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {PK : NotNull : BIGINT(19) : FK to ACCESS_RESULT}
     * @param id The value of id as equal.
     */
    public void setId_Equal(Long id) {
        regId(CK_EQ, id);
    }

    /**
     * NotEqual(!=). And NullIgnored, OnlyOnceRegistered.
     * @param id The value of id as notEqual.
     */
    public void setId_NotEqual(Long id) {
        regId(CK_NE, id);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param id The value of id as greaterThan.
     */
    public void setId_GreaterThan(Long id) {
        regId(CK_GT, id);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param id The value of id as lessThan.
     */
    public void setId_LessThan(Long id) {
        regId(CK_LT, id);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param id The value of id as greaterEqual.
     */
    public void setId_GreaterEqual(Long id) {
        regId(CK_GE, id);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param id The value of id as lessEqual.
     */
    public void setId_LessEqual(Long id) {
        regId(CK_LE, id);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param idList The collection of id as inScope.
     */
    public void setId_InScope(Collection<Long> idList) {
        regINS(CK_INS, cTL(idList), getCValueId(), "ID");
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param idList The collection of id as notInScope.
     */
    public void setId_NotInScope(Collection<Long> idList) {
        regINS(CK_NINS, cTL(idList), getCValueId(), "ID");
    }

    public void inScopeAccessResult(SubQuery<AccessResultCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultCB>", subQuery);
        AccessResultCB cb = new AccessResultCB();
        cb.xsetupForInScopeSubQuery();
        subQuery.query(cb);
        String subQueryPropertyName = keepId_InScopeSubQuery_AccessResult(cb
                .query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "ID", "ID", subQueryPropertyName);
    }

    public abstract String keepId_InScopeSubQuery_AccessResult(
            AccessResultCQ subQuery);

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setId_IsNull() {
        regId(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setId_IsNotNull() {
        regId(CK_ISNN, DOBJ);
    }

    protected void regId(ConditionKey k, Object v) {
        regQ(k, v, getCValueId(), "ID");
    }

    abstract protected ConditionValue getCValueId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : VARCHAR(255)}
     * @param transformerName The value of transformerName as equal.
     */
    public void setTransformerName_Equal(String transformerName) {
        doSetTransformerName_Equal(fRES(transformerName));
    }

    protected void doSetTransformerName_Equal(String transformerName) {
        regTransformerName(CK_EQ, transformerName);
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param transformerName The value of transformerName as notEqual.
     */
    public void setTransformerName_NotEqual(String transformerName) {
        doSetTransformerName_NotEqual(fRES(transformerName));
    }

    protected void doSetTransformerName_NotEqual(String transformerName) {
        regTransformerName(CK_NE, transformerName);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param transformerName The value of transformerName as greaterThan.
     */
    public void setTransformerName_GreaterThan(String transformerName) {
        regTransformerName(CK_GT, fRES(transformerName));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param transformerName The value of transformerName as lessThan.
     */
    public void setTransformerName_LessThan(String transformerName) {
        regTransformerName(CK_LT, fRES(transformerName));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param transformerName The value of transformerName as greaterEqual.
     */
    public void setTransformerName_GreaterEqual(String transformerName) {
        regTransformerName(CK_GE, fRES(transformerName));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param transformerName The value of transformerName as lessEqual.
     */
    public void setTransformerName_LessEqual(String transformerName) {
        regTransformerName(CK_LE, fRES(transformerName));
    }

    /**
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param transformerName The value of transformerName as prefixSearch.
     */
    public void setTransformerName_PrefixSearch(String transformerName) {
        setTransformerName_LikeSearch(transformerName, cLSOP());
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param transformerNameList The collection of transformerName as inScope.
     */
    public void setTransformerName_InScope(
            Collection<String> transformerNameList) {
        regINS(CK_INS, cTL(transformerNameList), getCValueTransformerName(),
                "TRANSFORMER_NAME");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param transformerNameList The collection of transformerName as notInScope.
     */
    public void setTransformerName_NotInScope(
            Collection<String> transformerNameList) {
        regINS(CK_NINS, cTL(transformerNameList), getCValueTransformerName(),
                "TRANSFORMER_NAME");
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param transformerName The value of transformerName as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setTransformerName_LikeSearch(String transformerName,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(transformerName), getCValueTransformerName(),
                "TRANSFORMER_NAME", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param transformerName The value of transformerName as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setTransformerName_NotLikeSearch(String transformerName,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(transformerName), getCValueTransformerName(),
                "TRANSFORMER_NAME", likeSearchOption);
    }

    protected void regTransformerName(ConditionKey k, Object v) {
        regQ(k, v, getCValueTransformerName(), "TRANSFORMER_NAME");
    }

    abstract protected ConditionValue getCValueTransformerName();

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setData_IsNull() {
        regData(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setData_IsNotNull() {
        regData(CK_ISNN, DOBJ);
    }

    protected void regData(ConditionKey k, Object v) {
        regQ(k, v, getCValueData(), "DATA");
    }

    abstract protected ConditionValue getCValueData();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {VARCHAR(20)}
     * @param encoding The value of encoding as equal.
     */
    public void setEncoding_Equal(String encoding) {
        doSetEncoding_Equal(fRES(encoding));
    }

    protected void doSetEncoding_Equal(String encoding) {
        regEncoding(CK_EQ, encoding);
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param encoding The value of encoding as notEqual.
     */
    public void setEncoding_NotEqual(String encoding) {
        doSetEncoding_NotEqual(fRES(encoding));
    }

    protected void doSetEncoding_NotEqual(String encoding) {
        regEncoding(CK_NE, encoding);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param encoding The value of encoding as greaterThan.
     */
    public void setEncoding_GreaterThan(String encoding) {
        regEncoding(CK_GT, fRES(encoding));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param encoding The value of encoding as lessThan.
     */
    public void setEncoding_LessThan(String encoding) {
        regEncoding(CK_LT, fRES(encoding));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param encoding The value of encoding as greaterEqual.
     */
    public void setEncoding_GreaterEqual(String encoding) {
        regEncoding(CK_GE, fRES(encoding));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param encoding The value of encoding as lessEqual.
     */
    public void setEncoding_LessEqual(String encoding) {
        regEncoding(CK_LE, fRES(encoding));
    }

    /**
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param encoding The value of encoding as prefixSearch.
     */
    public void setEncoding_PrefixSearch(String encoding) {
        setEncoding_LikeSearch(encoding, cLSOP());
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param encodingList The collection of encoding as inScope.
     */
    public void setEncoding_InScope(Collection<String> encodingList) {
        regINS(CK_INS, cTL(encodingList), getCValueEncoding(), "ENCODING");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param encodingList The collection of encoding as notInScope.
     */
    public void setEncoding_NotInScope(Collection<String> encodingList) {
        regINS(CK_NINS, cTL(encodingList), getCValueEncoding(), "ENCODING");
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param encoding The value of encoding as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setEncoding_LikeSearch(String encoding,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(encoding), getCValueEncoding(), "ENCODING",
                likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param encoding The value of encoding as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setEncoding_NotLikeSearch(String encoding,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(encoding), getCValueEncoding(), "ENCODING",
                likeSearchOption);
    }

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setEncoding_IsNull() {
        regEncoding(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setEncoding_IsNotNull() {
        regEncoding(CK_ISNN, DOBJ);
    }

    protected void regEncoding(ConditionKey k, Object v) {
        regQ(k, v, getCValueEncoding(), "ENCODING");
    }

    abstract protected ConditionValue getCValueEncoding();

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public HpSSQFunction<AccessResultDataCB> scalar_Equal() {
        return xcreateSSQFunction("=");
    }

    public HpSSQFunction<AccessResultDataCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(">=");
    }

    public HpSSQFunction<AccessResultDataCB> scalar_GreaterThan() {
        return xcreateSSQFunction(">");
    }

    public HpSSQFunction<AccessResultDataCB> scalar_LessEqual() {
        return xcreateSSQFunction("<=");
    }

    public HpSSQFunction<AccessResultDataCB> scalar_LessThan() {
        return xcreateSSQFunction("<");
    }

    protected HpSSQFunction<AccessResultDataCB> xcreateSSQFunction(
            final String operand) {
        return new HpSSQFunction<AccessResultDataCB>(
                new HpSSQSetupper<AccessResultDataCB>() {
                    public void setup(String function,
                            SubQuery<AccessResultDataCB> subQuery) {
                        xscalarSubQuery(function, subQuery, operand);
                    }
                });
    }

    protected void xscalarSubQuery(String function,
            SubQuery<AccessResultDataCB> subQuery, String operand) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForScalarSubQuery();
        subQuery.query(cb);
        String subQueryPropertyName = keepScalarSubQuery(cb.query()); // for saving query-value.
        registerScalarSubQuery(function, cb.query(), subQueryPropertyName,
                operand);
    }

    public abstract String keepScalarSubQuery(AccessResultDataCQ subQuery);

    // ===================================================================================
    //                                                             MySelf InScope SubQuery
    //                                                             =======================
    /**
     * Myself InScope SubQuery. {mainly for CLOB and Union}
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfInScope(SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForInScopeSubQuery();
        subQuery.query(cb);
        String subQueryPropertyName = keepMyselfInScopeSubQuery(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "ID", "ID", subQueryPropertyName);
    }

    public abstract String keepMyselfInScopeSubQuery(AccessResultDataCQ subQuery);

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

    String xLSO() {
        return LikeSearchOption.class.getName();
    }

    String xSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
