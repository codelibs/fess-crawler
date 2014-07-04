/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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

import java.util.*;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.chelper.*;
import org.seasar.dbflute.cbean.ckey.*;
import org.seasar.dbflute.cbean.coption.*;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.db.allcommon.*;
import org.seasar.robot.db.cbean.*;
import org.seasar.robot.db.cbean.cq.*;

/**
 * The abstract condition-query of ACCESS_RESULT_DATA.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsAccessResultDataCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsAccessResultDataCQ(ConditionQuery referrerQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                     DBMeta Provider
    //                                                                     ===============
    @Override
    protected DBMetaProvider xgetDBMetaProvider() {
        return DBMetaInstanceHandler.getProvider();
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "ACCESS_RESULT_DATA";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19), FK to ACCESS_RESULT}
     * @param id The value of id as equal. (NullAllowed: if null, no condition)
     */
    public void setId_Equal(Long id) {
        doSetId_Equal(id);
    }

    protected void doSetId_Equal(Long id) {
        regId(CK_EQ, id);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19), FK to ACCESS_RESULT}
     * @param id The value of id as notEqual. (NullAllowed: if null, no condition)
     */
    public void setId_NotEqual(Long id) {
        doSetId_NotEqual(id);
    }

    protected void doSetId_NotEqual(Long id) {
        regId(CK_NES, id);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19), FK to ACCESS_RESULT}
     * @param id The value of id as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setId_GreaterThan(Long id) {
        regId(CK_GT, id);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19), FK to ACCESS_RESULT}
     * @param id The value of id as lessThan. (NullAllowed: if null, no condition)
     */
    public void setId_LessThan(Long id) {
        regId(CK_LT, id);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19), FK to ACCESS_RESULT}
     * @param id The value of id as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setId_GreaterEqual(Long id) {
        regId(CK_GE, id);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19), FK to ACCESS_RESULT}
     * @param id The value of id as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setId_LessEqual(Long id) {
        regId(CK_LE, id);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19), FK to ACCESS_RESULT}
     * @param minNumber The min number of id. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of id. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setId_RangeOf(Long minNumber, Long maxNumber, RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueId(), "ID", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19), FK to ACCESS_RESULT}
     * @param idList The collection of id as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setId_InScope(Collection<Long> idList) {
        doSetId_InScope(idList);
    }

    protected void doSetId_InScope(Collection<Long> idList) {
        regINS(CK_INS, cTL(idList), getCValueId(), "ID");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19), FK to ACCESS_RESULT}
     * @param idList The collection of id as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setId_NotInScope(Collection<Long> idList) {
        doSetId_NotInScope(idList);
    }

    protected void doSetId_NotInScope(Collection<Long> idList) {
        regINS(CK_NINS, cTL(idList), getCValueId(), "ID");
    }

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select ID from ACCESS_RESULT where ...)} <br />
     * ACCESS_RESULT by my ID, named 'accessResult'.
     * @param subQuery The sub-query of AccessResult for 'in-scope'. (NotNull)
     */
    public void inScopeAccessResult(SubQuery<AccessResultCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        AccessResultCB cb = new AccessResultCB(); cb.xsetupForInScopeRelation(this);
        try { lock(); subQuery.query(cb); } finally { unlock(); }
        String pp = keepId_InScopeRelation_AccessResult(cb.query());
        registerInScopeRelation(cb.query(), "ID", "ID", pp, "accessResult");
    }
    public abstract String keepId_InScopeRelation_AccessResult(AccessResultCQ sq);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select ID from ACCESS_RESULT where ...)} <br />
     * ACCESS_RESULT by my ID, named 'accessResult'.
     * @param subQuery The sub-query of AccessResult for 'not in-scope'. (NotNull)
     */
    public void notInScopeAccessResult(SubQuery<AccessResultCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        AccessResultCB cb = new AccessResultCB(); cb.xsetupForInScopeRelation(this);
        try { lock(); subQuery.query(cb); } finally { unlock(); }
        String pp = keepId_NotInScopeRelation_AccessResult(cb.query());
        registerNotInScopeRelation(cb.query(), "ID", "ID", pp, "accessResult");
    }
    public abstract String keepId_NotInScopeRelation_AccessResult(AccessResultCQ sq);

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19), FK to ACCESS_RESULT}
     */
    public void setId_IsNull() { regId(CK_ISN, DOBJ); }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19), FK to ACCESS_RESULT}
     */
    public void setId_IsNotNull() { regId(CK_ISNN, DOBJ); }

    protected void regId(ConditionKey ky, Object vl) { regQ(ky, vl, getCValueId(), "ID"); }
    protected abstract ConditionValue getCValueId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * TRANSFORMER_NAME: {NotNull, VARCHAR2(255)}
     * @param transformerName The value of transformerName as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_Equal(String transformerName) {
        doSetTransformerName_Equal(fRES(transformerName));
    }

    protected void doSetTransformerName_Equal(String transformerName) {
        regTransformerName(CK_EQ, transformerName);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * TRANSFORMER_NAME: {NotNull, VARCHAR2(255)}
     * @param transformerName The value of transformerName as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_NotEqual(String transformerName) {
        doSetTransformerName_NotEqual(fRES(transformerName));
    }

    protected void doSetTransformerName_NotEqual(String transformerName) {
        regTransformerName(CK_NES, transformerName);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * TRANSFORMER_NAME: {NotNull, VARCHAR2(255)}
     * @param transformerName The value of transformerName as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_GreaterThan(String transformerName) {
        regTransformerName(CK_GT, fRES(transformerName));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * TRANSFORMER_NAME: {NotNull, VARCHAR2(255)}
     * @param transformerName The value of transformerName as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_LessThan(String transformerName) {
        regTransformerName(CK_LT, fRES(transformerName));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * TRANSFORMER_NAME: {NotNull, VARCHAR2(255)}
     * @param transformerName The value of transformerName as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_GreaterEqual(String transformerName) {
        regTransformerName(CK_GE, fRES(transformerName));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * TRANSFORMER_NAME: {NotNull, VARCHAR2(255)}
     * @param transformerName The value of transformerName as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_LessEqual(String transformerName) {
        regTransformerName(CK_LE, fRES(transformerName));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * TRANSFORMER_NAME: {NotNull, VARCHAR2(255)}
     * @param transformerNameList The collection of transformerName as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_InScope(Collection<String> transformerNameList) {
        doSetTransformerName_InScope(transformerNameList);
    }

    public void doSetTransformerName_InScope(Collection<String> transformerNameList) {
        regINS(CK_INS, cTL(transformerNameList), getCValueTransformerName(), "TRANSFORMER_NAME");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * TRANSFORMER_NAME: {NotNull, VARCHAR2(255)}
     * @param transformerNameList The collection of transformerName as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_NotInScope(Collection<String> transformerNameList) {
        doSetTransformerName_NotInScope(transformerNameList);
    }

    public void doSetTransformerName_NotInScope(Collection<String> transformerNameList) {
        regINS(CK_NINS, cTL(transformerNameList), getCValueTransformerName(), "TRANSFORMER_NAME");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * TRANSFORMER_NAME: {NotNull, VARCHAR2(255)}
     * @param transformerName The value of transformerName as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_PrefixSearch(String transformerName) {
        setTransformerName_LikeSearch(transformerName, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * TRANSFORMER_NAME: {NotNull, VARCHAR2(255)} <br />
     * <pre>e.g. setTransformerName_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param transformerName The value of transformerName as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setTransformerName_LikeSearch(String transformerName, LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(transformerName), getCValueTransformerName(), "TRANSFORMER_NAME", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * TRANSFORMER_NAME: {NotNull, VARCHAR2(255)}
     * @param transformerName The value of transformerName as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setTransformerName_NotLikeSearch(String transformerName, LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(transformerName), getCValueTransformerName(), "TRANSFORMER_NAME", likeSearchOption);
    }

    protected void regTransformerName(ConditionKey ky, Object vl) { regQ(ky, vl, getCValueTransformerName(), "TRANSFORMER_NAME"); }
    protected abstract ConditionValue getCValueTransformerName();

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * DATA: {BLOB(4000)}
     */
    public void setData_IsNull() { regData(CK_ISN, DOBJ); }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * DATA: {BLOB(4000)}
     */
    public void setData_IsNotNull() { regData(CK_ISNN, DOBJ); }

    protected void regData(ConditionKey ky, Object vl) { regQ(ky, vl, getCValueData(), "DATA"); }
    protected abstract ConditionValue getCValueData();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ENCODING: {VARCHAR2(20)}
     * @param encoding The value of encoding as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_Equal(String encoding) {
        doSetEncoding_Equal(fRES(encoding));
    }

    protected void doSetEncoding_Equal(String encoding) {
        regEncoding(CK_EQ, encoding);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ENCODING: {VARCHAR2(20)}
     * @param encoding The value of encoding as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_NotEqual(String encoding) {
        doSetEncoding_NotEqual(fRES(encoding));
    }

    protected void doSetEncoding_NotEqual(String encoding) {
        regEncoding(CK_NES, encoding);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ENCODING: {VARCHAR2(20)}
     * @param encoding The value of encoding as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_GreaterThan(String encoding) {
        regEncoding(CK_GT, fRES(encoding));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ENCODING: {VARCHAR2(20)}
     * @param encoding The value of encoding as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_LessThan(String encoding) {
        regEncoding(CK_LT, fRES(encoding));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ENCODING: {VARCHAR2(20)}
     * @param encoding The value of encoding as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_GreaterEqual(String encoding) {
        regEncoding(CK_GE, fRES(encoding));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ENCODING: {VARCHAR2(20)}
     * @param encoding The value of encoding as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_LessEqual(String encoding) {
        regEncoding(CK_LE, fRES(encoding));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * ENCODING: {VARCHAR2(20)}
     * @param encodingList The collection of encoding as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_InScope(Collection<String> encodingList) {
        doSetEncoding_InScope(encodingList);
    }

    public void doSetEncoding_InScope(Collection<String> encodingList) {
        regINS(CK_INS, cTL(encodingList), getCValueEncoding(), "ENCODING");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * ENCODING: {VARCHAR2(20)}
     * @param encodingList The collection of encoding as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_NotInScope(Collection<String> encodingList) {
        doSetEncoding_NotInScope(encodingList);
    }

    public void doSetEncoding_NotInScope(Collection<String> encodingList) {
        regINS(CK_NINS, cTL(encodingList), getCValueEncoding(), "ENCODING");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * ENCODING: {VARCHAR2(20)}
     * @param encoding The value of encoding as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_PrefixSearch(String encoding) {
        setEncoding_LikeSearch(encoding, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * ENCODING: {VARCHAR2(20)} <br />
     * <pre>e.g. setEncoding_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param encoding The value of encoding as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setEncoding_LikeSearch(String encoding, LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(encoding), getCValueEncoding(), "ENCODING", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * ENCODING: {VARCHAR2(20)}
     * @param encoding The value of encoding as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setEncoding_NotLikeSearch(String encoding, LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(encoding), getCValueEncoding(), "ENCODING", likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * ENCODING: {VARCHAR2(20)}
     */
    public void setEncoding_IsNull() { regEncoding(CK_ISN, DOBJ); }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * ENCODING: {VARCHAR2(20)}
     */
    public void setEncoding_IsNullOrEmpty() { regEncoding(CK_ISNOE, DOBJ); }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * ENCODING: {VARCHAR2(20)}
     */
    public void setEncoding_IsNotNull() { regEncoding(CK_ISNN, DOBJ); }

    protected void regEncoding(ConditionKey ky, Object vl) { regQ(ky, vl, getCValueEncoding(), "ENCODING"); }
    protected abstract ConditionValue getCValueEncoding();

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_Equal()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ.getOperand(), AccessResultDataCB.class);
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES.getOperand(), AccessResultDataCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT.getOperand(), AccessResultDataCB.class);
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_LessThan()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT.getOperand(), AccessResultDataCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE.getOperand(), AccessResultDataCB.class);
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE.getOperand(), AccessResultDataCB.class);
    }

    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xscalarCondition(String fn, SubQuery<CB> sq, String rd, HpSSQOption<CB> op) {
        assertObjectNotNull("subQuery", sq);
        AccessResultDataCB cb = xcreateScalarConditionCB(); sq.query((CB)cb);
        String pp = keepScalarCondition(cb.query()); // for saving query-value
        op.setPartitionByCBean((CB)xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(fn, cb.query(), pp, rd, op);
    }
    public abstract String keepScalarCondition(AccessResultDataCQ sq);

    protected AccessResultDataCB xcreateScalarConditionCB() {
        AccessResultDataCB cb = newMyCB(); cb.xsetupForScalarCondition(this); return cb;
    }

    protected AccessResultDataCB xcreateScalarConditionPartitionByCB() {
        AccessResultDataCB cb = newMyCB(); cb.xsetupForScalarConditionPartitionBy(this); return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(String fn, SubQuery<AccessResultDataCB> sq, String al, DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        AccessResultDataCB cb = new AccessResultDataCB(); cb.xsetupForDerivedReferrer(this);
        try { lock(); sq.query(cb); } finally { unlock(); }
        String pp = keepSpecifyMyselfDerived(cb.query());
        String pk = "ID";
        registerSpecifyMyselfDerived(fn, cb.query(), pk, pk, pp, "myselfDerived", al, op);
    }
    public abstract String keepSpecifyMyselfDerived(AccessResultDataCQ sq);

    /**
     * Prepare for (Query)MyselfDerived (correlated sub-query).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<AccessResultDataCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived(AccessResultDataCB.class);
    }
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xqderiveMyselfDerived(String fn, SubQuery<CB> sq, String rd, Object vl, DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        AccessResultDataCB cb = new AccessResultDataCB(); cb.xsetupForDerivedReferrer(this); sq.query((CB)cb);
        String pk = "ID";
        String sqpp = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        String prpp = keepQueryMyselfDerivedParameter(vl);
        registerQueryMyselfDerived(fn, cb.query(), pk, pk, sqpp, "myselfDerived", rd, vl, prpp, op);
    }
    public abstract String keepQueryMyselfDerived(AccessResultDataCQ sq);
    public abstract String keepQueryMyselfDerivedParameter(Object vl);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (correlated sub-query).
     * @param subQuery The implementation of sub-query. (NotNull)
     */
    public void myselfExists(SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        AccessResultDataCB cb = new AccessResultDataCB(); cb.xsetupForMyselfExists(this);
        try { lock(); subQuery.query(cb); } finally { unlock(); }
        String pp = keepMyselfExists(cb.query());
        registerMyselfExists(cb.query(), pp);
    }
    public abstract String keepMyselfExists(AccessResultDataCQ sq);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (sub-query).
     * @param subQuery The implementation of sub-query. (NotNull)
     */
    public void myselfInScope(SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        AccessResultDataCB cb = new AccessResultDataCB(); cb.xsetupForMyselfInScope(this);
        try { lock(); subQuery.query(cb); } finally { unlock(); }
        String pp = keepMyselfInScope(cb.query());
        registerMyselfInScope(cb.query(), pp);
    }
    public abstract String keepMyselfInScope(AccessResultDataCQ sq);

    // ===================================================================================
    //                                                                          Compatible
    //                                                                          ==========
    /**
     * Order along the list of manual values. #beforejava8 <br />
     * This function with Union is unsupported! <br />
     * The order values are bound (treated as bind parameter).
     * <pre>
     * MemberCB cb = new MemberCB();
     * List&lt;CDef.MemberStatus&gt; orderValueList = new ArrayList&lt;CDef.MemberStatus&gt;();
     * orderValueList.add(CDef.MemberStatus.Withdrawal);
     * orderValueList.add(CDef.MemberStatus.Formalized);
     * orderValueList.add(CDef.MemberStatus.Provisional);
     * cb.query().addOrderBy_MemberStatusCode_Asc().<span style="color: #DD4747">withManualOrder(orderValueList)</span>;
     * <span style="color: #3F7E5E">// order by </span>
     * <span style="color: #3F7E5E">//   case</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'WDL' then 0</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'FML' then 1</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'PRV' then 2</span>
     * <span style="color: #3F7E5E">//     else 3</span>
     * <span style="color: #3F7E5E">//   end asc, ...</span>
     * </pre>
     * @param orderValueList The list of order values for manual ordering. (NotNull)
     */
    public void withManualOrder(List<? extends Object> orderValueList) { // is user public!
        assertObjectNotNull("withManualOrder(orderValueList)", orderValueList);
        final ManualOrderBean manualOrderBean = new ManualOrderBean();
        manualOrderBean.acceptOrderValueList(orderValueList);
        withManualOrder(manualOrderBean);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    protected AccessResultDataCB newMyCB() {
        return new AccessResultDataCB();
    }
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCQ() { return AccessResultDataCQ.class.getName(); }
    protected String xabLSO() { return LikeSearchOption.class.getName(); }
    protected String xabSSQS() { return HpSSQSetupper.class.getName(); }
}
