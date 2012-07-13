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
 * 
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsAccessResultDataCQ extends
        AbstractConditionQuery {

    // ===================================================================================
    // Constructor
    // ===========
    public AbstractBsAccessResultDataCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName, final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    // DBMeta Provider
    // ===============
    @Override
    protected DBMetaProvider xgetDBMetaProvider() {
        return DBMetaInstanceHandler.getProvider();
    }

    // ===================================================================================
    // Table Name
    // ==========
    public String getTableDbName() {
        return "ACCESS_RESULT_DATA";
    }

    // ===================================================================================
    // Query
    // =====

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19), FK to ACCESS_RESULT}
     * 
     * @param id
     *            The value of id as equal.
     */
    public void setId_Equal(final Long id) {
        doSetId_Equal(id);
    }

    protected void doSetId_Equal(final Long id) {
        regId(CK_EQ, id);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered.
     * 
     * @param id
     *            The value of id as notEqual.
     */
    public void setId_NotEqual(final Long id) {
        doSetId_NotEqual(id);
    }

    protected void doSetId_NotEqual(final Long id) {
        regId(CK_NES, id);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * 
     * @param id
     *            The value of id as greaterThan.
     */
    public void setId_GreaterThan(final Long id) {
        regId(CK_GT, id);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * 
     * @param id
     *            The value of id as lessThan.
     */
    public void setId_LessThan(final Long id) {
        regId(CK_LT, id);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * 
     * @param id
     *            The value of id as greaterEqual.
     */
    public void setId_GreaterEqual(final Long id) {
        regId(CK_GE, id);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * 
     * @param id
     *            The value of id as lessEqual.
     */
    public void setId_LessEqual(final Long id) {
        regId(CK_LE, id);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored,
     * SeveralRegistered.
     * 
     * @param idList
     *            The collection of id as inScope.
     */
    public void setId_InScope(final Collection<Long> idList) {
        doSetId_InScope(idList);
    }

    protected void doSetId_InScope(final Collection<Long> idList) {
        regINS(CK_INS, cTL(idList), getCValueId(), "ID");
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored,
     * SeveralRegistered.
     * 
     * @param idList
     *            The collection of id as notInScope.
     */
    public void setId_NotInScope(final Collection<Long> idList) {
        doSetId_NotInScope(idList);
    }

    protected void doSetId_NotInScope(final Collection<Long> idList) {
        regINS(CK_NINS, cTL(idList), getCValueId(), "ID");
    }

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select ID from ACCESS_RESULT where ...)} <br />
     * ACCESS_RESULT as 'accessResult'.
     * 
     * @param subQuery
     *            The sub-query of AccessResult for 'in-scope'. (NotNull)
     */
    public void inScopeAccessResult(final SubQuery<AccessResultCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultCB>", subQuery);
        final AccessResultCB cb = new AccessResultCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName =
            keepId_InScopeRelation_AccessResult(cb.query()); // for saving
                                                             // query-value.
        registerInScopeRelation(cb.query(), "ID", "ID", subQueryPropertyName);
    }

    public abstract String keepId_InScopeRelation_AccessResult(
            AccessResultCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select ID from ACCESS_RESULT where ...)} <br />
     * ACCESS_RESULT as 'accessResult'.
     * 
     * @param subQuery
     *            The sub-query of AccessResult for 'not in-scope'. (NotNull)
     */
    public void notInScopeAccessResult(final SubQuery<AccessResultCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultCB>", subQuery);
        final AccessResultCB cb = new AccessResultCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName =
            keepId_NotInScopeRelation_AccessResult(cb.query()); // for saving
                                                                // query-value.
        registerNotInScopeRelation(cb.query(), "ID", "ID", subQueryPropertyName);
    }

    public abstract String keepId_NotInScopeRelation_AccessResult(
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

    protected void regId(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueId(), "ID");
    }

    abstract protected ConditionValue getCValueId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * TRANSFORMER_NAME: {NotNull, VARCHAR2(255)}
     * 
     * @param transformerName
     *            The value of transformerName as equal.
     */
    public void setTransformerName_Equal(final String transformerName) {
        doSetTransformerName_Equal(fRES(transformerName));
    }

    protected void doSetTransformerName_Equal(final String transformerName) {
        regTransformerName(CK_EQ, transformerName);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * 
     * @param transformerName
     *            The value of transformerName as notEqual.
     */
    public void setTransformerName_NotEqual(final String transformerName) {
        doSetTransformerName_NotEqual(fRES(transformerName));
    }

    protected void doSetTransformerName_NotEqual(final String transformerName) {
        regTransformerName(CK_NES, transformerName);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * 
     * @param transformerName
     *            The value of transformerName as greaterThan.
     */
    public void setTransformerName_GreaterThan(final String transformerName) {
        regTransformerName(CK_GT, fRES(transformerName));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * 
     * @param transformerName
     *            The value of transformerName as lessThan.
     */
    public void setTransformerName_LessThan(final String transformerName) {
        regTransformerName(CK_LT, fRES(transformerName));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * 
     * @param transformerName
     *            The value of transformerName as greaterEqual.
     */
    public void setTransformerName_GreaterEqual(final String transformerName) {
        regTransformerName(CK_GE, fRES(transformerName));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * 
     * @param transformerName
     *            The value of transformerName as lessEqual.
     */
    public void setTransformerName_LessEqual(final String transformerName) {
        regTransformerName(CK_LE, fRES(transformerName));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered.
     * 
     * @param transformerNameList
     *            The collection of transformerName as inScope.
     */
    public void setTransformerName_InScope(
            final Collection<String> transformerNameList) {
        doSetTransformerName_InScope(transformerNameList);
    }

    public void doSetTransformerName_InScope(
            final Collection<String> transformerNameList) {
        regINS(
            CK_INS,
            cTL(transformerNameList),
            getCValueTransformerName(),
            "TRANSFORMER_NAME");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered.
     * 
     * @param transformerNameList
     *            The collection of transformerName as notInScope.
     */
    public void setTransformerName_NotInScope(
            final Collection<String> transformerNameList) {
        doSetTransformerName_NotInScope(transformerNameList);
    }

    public void doSetTransformerName_NotInScope(
            final Collection<String> transformerNameList) {
        regINS(
            CK_NINS,
            cTL(transformerNameList),
            getCValueTransformerName(),
            "TRANSFORMER_NAME");
    }

    /**
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored,
     * SeveralRegistered.
     * 
     * @param transformerName
     *            The value of transformerName as prefixSearch.
     */
    public void setTransformerName_PrefixSearch(final String transformerName) {
        setTransformerName_LikeSearch(transformerName, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * 
     * @param transformerName
     *            The value of transformerName as likeSearch.
     * @param likeSearchOption
     *            The option of like-search. (NotNull)
     */
    public void setTransformerName_LikeSearch(final String transformerName,
            final LikeSearchOption likeSearchOption) {
        regLSQ(
            CK_LS,
            fRES(transformerName),
            getCValueTransformerName(),
            "TRANSFORMER_NAME",
            likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape
     * ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * 
     * @param transformerName
     *            The value of transformerName as notLikeSearch.
     * @param likeSearchOption
     *            The option of not-like-search. (NotNull)
     */
    public void setTransformerName_NotLikeSearch(final String transformerName,
            final LikeSearchOption likeSearchOption) {
        regLSQ(
            CK_NLS,
            fRES(transformerName),
            getCValueTransformerName(),
            "TRANSFORMER_NAME",
            likeSearchOption);
    }

    protected void regTransformerName(final ConditionKey k, final Object v) {
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

    protected void regData(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueData(), "DATA");
    }

    abstract protected ConditionValue getCValueData();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * ENCODING: {VARCHAR2(20)}
     * 
     * @param encoding
     *            The value of encoding as equal.
     */
    public void setEncoding_Equal(final String encoding) {
        doSetEncoding_Equal(fRES(encoding));
    }

    protected void doSetEncoding_Equal(final String encoding) {
        regEncoding(CK_EQ, encoding);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * 
     * @param encoding
     *            The value of encoding as notEqual.
     */
    public void setEncoding_NotEqual(final String encoding) {
        doSetEncoding_NotEqual(fRES(encoding));
    }

    protected void doSetEncoding_NotEqual(final String encoding) {
        regEncoding(CK_NES, encoding);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * 
     * @param encoding
     *            The value of encoding as greaterThan.
     */
    public void setEncoding_GreaterThan(final String encoding) {
        regEncoding(CK_GT, fRES(encoding));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * 
     * @param encoding
     *            The value of encoding as lessThan.
     */
    public void setEncoding_LessThan(final String encoding) {
        regEncoding(CK_LT, fRES(encoding));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * 
     * @param encoding
     *            The value of encoding as greaterEqual.
     */
    public void setEncoding_GreaterEqual(final String encoding) {
        regEncoding(CK_GE, fRES(encoding));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * 
     * @param encoding
     *            The value of encoding as lessEqual.
     */
    public void setEncoding_LessEqual(final String encoding) {
        regEncoding(CK_LE, fRES(encoding));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered.
     * 
     * @param encodingList
     *            The collection of encoding as inScope.
     */
    public void setEncoding_InScope(final Collection<String> encodingList) {
        doSetEncoding_InScope(encodingList);
    }

    public void doSetEncoding_InScope(final Collection<String> encodingList) {
        regINS(CK_INS, cTL(encodingList), getCValueEncoding(), "ENCODING");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered.
     * 
     * @param encodingList
     *            The collection of encoding as notInScope.
     */
    public void setEncoding_NotInScope(final Collection<String> encodingList) {
        doSetEncoding_NotInScope(encodingList);
    }

    public void doSetEncoding_NotInScope(final Collection<String> encodingList) {
        regINS(CK_NINS, cTL(encodingList), getCValueEncoding(), "ENCODING");
    }

    /**
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored,
     * SeveralRegistered.
     * 
     * @param encoding
     *            The value of encoding as prefixSearch.
     */
    public void setEncoding_PrefixSearch(final String encoding) {
        setEncoding_LikeSearch(encoding, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * 
     * @param encoding
     *            The value of encoding as likeSearch.
     * @param likeSearchOption
     *            The option of like-search. (NotNull)
     */
    public void setEncoding_LikeSearch(final String encoding,
            final LikeSearchOption likeSearchOption) {
        regLSQ(
            CK_LS,
            fRES(encoding),
            getCValueEncoding(),
            "ENCODING",
            likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape
     * ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * 
     * @param encoding
     *            The value of encoding as notLikeSearch.
     * @param likeSearchOption
     *            The option of not-like-search. (NotNull)
     */
    public void setEncoding_NotLikeSearch(final String encoding,
            final LikeSearchOption likeSearchOption) {
        regLSQ(
            CK_NLS,
            fRES(encoding),
            getCValueEncoding(),
            "ENCODING",
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

    protected void regEncoding(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueEncoding(), "ENCODING");
    }

    abstract protected ConditionValue getCValueEncoding();

    // ===================================================================================
    // Scalar Condition
    // ================
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * 
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_Equal()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * 
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ.getOperand());
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * 
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * 
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * 
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * 
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * 
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessThan()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * 
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * 
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * 
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * 
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * 
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE.getOperand());
    }

    protected HpSSQFunction<AccessResultDataCB> xcreateSSQFunction(
            final String operand) {
        return new HpSSQFunction<AccessResultDataCB>(
            new HpSSQSetupper<AccessResultDataCB>() {
                public void setup(final String function,
                        final SubQuery<AccessResultDataCB> subQuery) {
                    xscalarCondition(function, subQuery, operand);
                }
            });
    }

    protected void xscalarCondition(final String function,
            final SubQuery<AccessResultDataCB> subQuery, final String operand) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        final AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForScalarCondition(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepScalarCondition(cb.query()); // for
                                                                       // saving
                                                                       // query-value.
        registerScalarCondition(
            function,
            cb.query(),
            subQueryPropertyName,
            operand);
    }

    public abstract String keepScalarCondition(AccessResultDataCQ subQuery);

    // ===================================================================================
    // Myself InScope
    // ==============
    /**
     * Myself InScope (SubQuery). {mainly for CLOB and Union}
     * 
     * @param subQuery
     *            The implementation of sub query. (NotNull)
     */
    public void myselfInScope(final SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        final AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfInScopeRelation(cb.query()); // for
                                                                             // saving
                                                                             // query-value.
        registerInScopeRelation(cb.query(), "ID", "ID", subQueryPropertyName);
    }

    public abstract String keepMyselfInScopeRelation(AccessResultDataCQ subQuery);

    // ===================================================================================
    // Very Internal
    // =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCB() {
        return AccessResultDataCB.class.getName();
    }

    protected String xabCQ() {
        return AccessResultDataCQ.class.getName();
    }

    protected String xabLSO() {
        return LikeSearchOption.class.getName();
    }

    protected String xabSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
