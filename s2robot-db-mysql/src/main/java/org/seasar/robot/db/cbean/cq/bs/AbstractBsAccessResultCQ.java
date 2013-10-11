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

import java.util.Collection;

import org.seasar.dbflute.cbean.AbstractConditionQuery;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.chelper.HpQDRFunction;
import org.seasar.dbflute.cbean.chelper.HpQDRSetupper;
import org.seasar.dbflute.cbean.chelper.HpSSQFunction;
import org.seasar.dbflute.cbean.chelper.HpSSQOption;
import org.seasar.dbflute.cbean.chelper.HpSSQSetupper;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.DerivedReferrerOption;
import org.seasar.dbflute.cbean.coption.FromToOption;
import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.cbean.coption.RangeOfOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.db.allcommon.DBMetaInstanceHandler;
import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.cbean.AccessResultDataCB;
import org.seasar.robot.db.cbean.cq.AccessResultCQ;
import org.seasar.robot.db.cbean.cq.AccessResultDataCQ;

/**
 * The abstract condition-query of ACCESS_RESULT.
 * 
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsAccessResultCQ extends AbstractConditionQuery {

    // ===================================================================================
    // Constructor
    // ===========
    public AbstractBsAccessResultCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
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
    @Override
    public String getTableDbName() {
        return "ACCESS_RESULT";
    }

    // ===================================================================================
    // Query
    // =====

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * 
     * @param id
     *            The value of id as equal. (NullAllowed: if null, no condition)
     */
    public void setId_Equal(final Long id) {
        doSetId_Equal(id);
    }

    protected void doSetId_Equal(final Long id) {
        regId(CK_EQ, id);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * 
     * @param id
     *            The value of id as notEqual. (NullAllowed: if null, no
     *            condition)
     */
    public void setId_NotEqual(final Long id) {
        doSetId_NotEqual(id);
    }

    protected void doSetId_NotEqual(final Long id) {
        regId(CK_NES, id);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * 
     * @param id
     *            The value of id as greaterThan. (NullAllowed: if null, no
     *            condition)
     */
    public void setId_GreaterThan(final Long id) {
        regId(CK_GT, id);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * 
     * @param id
     *            The value of id as lessThan. (NullAllowed: if null, no
     *            condition)
     */
    public void setId_LessThan(final Long id) {
        regId(CK_LT, id);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * 
     * @param id
     *            The value of id as greaterEqual. (NullAllowed: if null, no
     *            condition)
     */
    public void setId_GreaterEqual(final Long id) {
        regId(CK_GE, id);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * 
     * @param id
     *            The value of id as lessEqual. (NullAllowed: if null, no
     *            condition)
     */
    public void setId_LessEqual(final Long id) {
        regId(CK_LE, id);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * 
     * @param minNumber
     *            The min number of id. (NullAllowed: if null, no
     *            from-condition)
     * @param maxNumber
     *            The max number of id. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption
     *            The option of range-of. (NotNull)
     */
    public void setId_RangeOf(final Long minNumber, final Long maxNumber,
            final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueId(), "ID", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored,
     * SeveralRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * 
     * @param idList
     *            The collection of id as inScope. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setId_InScope(final Collection<Long> idList) {
        doSetId_InScope(idList);
    }

    protected void doSetId_InScope(final Collection<Long> idList) {
        regINS(CK_INS, cTL(idList), getCValueId(), "ID");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored,
     * SeveralRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * 
     * @param idList
     *            The collection of id as notInScope. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setId_NotInScope(final Collection<Long> idList) {
        doSetId_NotInScope(idList);
    }

    protected void doSetId_NotInScope(final Collection<Long> idList) {
        regINS(CK_NINS, cTL(idList), getCValueId(), "ID");
    }

    /**
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select ID from ACCESS_RESULT_DATA where ...)} <br />
     * ACCESS_RESULT_DATA by ID, named 'accessResultDataAsOne'.
     * 
     * <pre>
     * cb.query().<span style="color: #FD4747">existsAccessResultDataAsOne</span>(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * 
     * @param subQuery
     *            The sub-query of AccessResultDataAsOne for 'exists'. (NotNull)
     */
    public void existsAccessResultDataAsOne(
            final SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        final AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName =
            keepId_ExistsReferrer_AccessResultDataAsOne(cb.query()); // for
                                                                     // saving
                                                                     // query-value.
        registerExistsReferrer(
            cb.query(),
            "ID",
            "ID",
            subQueryPropertyName,
            "accessResultDataAsOne");
    }

    public abstract String keepId_ExistsReferrer_AccessResultDataAsOne(
            AccessResultDataCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select ID from ACCESS_RESULT_DATA where ...)} <br />
     * ACCESS_RESULT_DATA by ID, named 'accessResultDataAsOne'.
     * 
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsAccessResultDataAsOne</span>(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * 
     * @param subQuery
     *            The sub-query of Id_NotExistsReferrer_AccessResultDataAsOne
     *            for 'not exists'. (NotNull)
     */
    public void notExistsAccessResultDataAsOne(
            final SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        final AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName =
            keepId_NotExistsReferrer_AccessResultDataAsOne(cb.query()); // for
                                                                        // saving
                                                                        // query-value.
        registerNotExistsReferrer(
            cb.query(),
            "ID",
            "ID",
            subQueryPropertyName,
            "accessResultDataAsOne");
    }

    public abstract String keepId_NotExistsReferrer_AccessResultDataAsOne(
            AccessResultDataCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select ID from ACCESS_RESULT_DATA where ...)} <br />
     * ACCESS_RESULT_DATA by ID, named 'accessResultDataAsOne'.
     * 
     * @param subQuery
     *            The sub-query of AccessResultDataAsOne for 'in-scope'.
     *            (NotNull)
     */
    public void inScopeAccessResultDataAsOne(
            final SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        final AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName =
            keepId_InScopeRelation_AccessResultDataAsOne(cb.query()); // for
                                                                      // saving
                                                                      // query-value.
        registerInScopeRelation(
            cb.query(),
            "ID",
            "ID",
            subQueryPropertyName,
            "accessResultDataAsOne");
    }

    public abstract String keepId_InScopeRelation_AccessResultDataAsOne(
            AccessResultDataCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select ID from ACCESS_RESULT_DATA where ...)} <br />
     * ACCESS_RESULT_DATA by ID, named 'accessResultDataAsOne'.
     * 
     * @param subQuery
     *            The sub-query of AccessResultDataAsOne for 'not in-scope'.
     *            (NotNull)
     */
    public void notInScopeAccessResultDataAsOne(
            final SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        final AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName =
            keepId_NotInScopeRelation_AccessResultDataAsOne(cb.query()); // for
                                                                         // saving
                                                                         // query-value.
        registerNotInScopeRelation(
            cb.query(),
            "ID",
            "ID",
            subQueryPropertyName,
            "accessResultDataAsOne");
    }

    public abstract String keepId_NotInScopeRelation_AccessResultDataAsOne(
            AccessResultDataCQ subQuery);

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     */
    public void setId_IsNull() {
        regId(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
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
     * SESSION_ID: {IX, NotNull, VARCHAR(20)}
     * 
     * @param sessionId
     *            The value of sessionId as equal. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setSessionId_Equal(final String sessionId) {
        doSetSessionId_Equal(fRES(sessionId));
    }

    protected void doSetSessionId_Equal(final String sessionId) {
        regSessionId(CK_EQ, sessionId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SESSION_ID: {IX, NotNull, VARCHAR(20)}
     * 
     * @param sessionId
     *            The value of sessionId as notEqual. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setSessionId_NotEqual(final String sessionId) {
        doSetSessionId_NotEqual(fRES(sessionId));
    }

    protected void doSetSessionId_NotEqual(final String sessionId) {
        regSessionId(CK_NES, sessionId);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SESSION_ID: {IX, NotNull, VARCHAR(20)}
     * 
     * @param sessionId
     *            The value of sessionId as greaterThan. (NullAllowed: if null
     *            (or empty), no condition)
     */
    public void setSessionId_GreaterThan(final String sessionId) {
        regSessionId(CK_GT, fRES(sessionId));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SESSION_ID: {IX, NotNull, VARCHAR(20)}
     * 
     * @param sessionId
     *            The value of sessionId as lessThan. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setSessionId_LessThan(final String sessionId) {
        regSessionId(CK_LT, fRES(sessionId));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SESSION_ID: {IX, NotNull, VARCHAR(20)}
     * 
     * @param sessionId
     *            The value of sessionId as greaterEqual. (NullAllowed: if null
     *            (or empty), no condition)
     */
    public void setSessionId_GreaterEqual(final String sessionId) {
        regSessionId(CK_GE, fRES(sessionId));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SESSION_ID: {IX, NotNull, VARCHAR(20)}
     * 
     * @param sessionId
     *            The value of sessionId as lessEqual. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setSessionId_LessEqual(final String sessionId) {
        regSessionId(CK_LE, fRES(sessionId));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * SESSION_ID: {IX, NotNull, VARCHAR(20)}
     * 
     * @param sessionIdList
     *            The collection of sessionId as inScope. (NullAllowed: if null
     *            (or empty), no condition)
     */
    public void setSessionId_InScope(final Collection<String> sessionIdList) {
        doSetSessionId_InScope(sessionIdList);
    }

    public void doSetSessionId_InScope(final Collection<String> sessionIdList) {
        regINS(CK_INS, cTL(sessionIdList), getCValueSessionId(), "SESSION_ID");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * SESSION_ID: {IX, NotNull, VARCHAR(20)}
     * 
     * @param sessionIdList
     *            The collection of sessionId as notInScope. (NullAllowed: if
     *            null (or empty), no condition)
     */
    public void setSessionId_NotInScope(final Collection<String> sessionIdList) {
        doSetSessionId_NotInScope(sessionIdList);
    }

    public void doSetSessionId_NotInScope(final Collection<String> sessionIdList) {
        regINS(CK_NINS, cTL(sessionIdList), getCValueSessionId(), "SESSION_ID");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored,
     * SeveralRegistered. <br />
     * SESSION_ID: {IX, NotNull, VARCHAR(20)}
     * 
     * @param sessionId
     *            The value of sessionId as prefixSearch. (NullAllowed: if null
     *            (or empty), no condition)
     */
    public void setSessionId_PrefixSearch(final String sessionId) {
        setSessionId_LikeSearch(sessionId, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}.
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SESSION_ID: {IX, NotNull, VARCHAR(20)} <br />
     * 
     * <pre>e.g. setSessionId_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * 
     * @param sessionId
     *            The value of sessionId as likeSearch. (NullAllowed: if null
     *            (or empty), no condition)
     * @param likeSearchOption
     *            The option of like-search. (NotNull)
     */
    public void setSessionId_LikeSearch(final String sessionId,
            final LikeSearchOption likeSearchOption) {
        regLSQ(
            CK_LS,
            fRES(sessionId),
            getCValueSessionId(),
            "SESSION_ID",
            likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape
     * ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SESSION_ID: {IX, NotNull, VARCHAR(20)}
     * 
     * @param sessionId
     *            The value of sessionId as notLikeSearch. (NullAllowed: if null
     *            (or empty), no condition)
     * @param likeSearchOption
     *            The option of not-like-search. (NotNull)
     */
    public void setSessionId_NotLikeSearch(final String sessionId,
            final LikeSearchOption likeSearchOption) {
        regLSQ(
            CK_NLS,
            fRES(sessionId),
            getCValueSessionId(),
            "SESSION_ID",
            likeSearchOption);
    }

    protected void regSessionId(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueSessionId(), "SESSION_ID");
    }

    abstract protected ConditionValue getCValueSessionId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * RULE_ID: {VARCHAR(20)}
     * 
     * @param ruleId
     *            The value of ruleId as equal. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setRuleId_Equal(final String ruleId) {
        doSetRuleId_Equal(fRES(ruleId));
    }

    protected void doSetRuleId_Equal(final String ruleId) {
        regRuleId(CK_EQ, ruleId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * RULE_ID: {VARCHAR(20)}
     * 
     * @param ruleId
     *            The value of ruleId as notEqual. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setRuleId_NotEqual(final String ruleId) {
        doSetRuleId_NotEqual(fRES(ruleId));
    }

    protected void doSetRuleId_NotEqual(final String ruleId) {
        regRuleId(CK_NES, ruleId);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * RULE_ID: {VARCHAR(20)}
     * 
     * @param ruleId
     *            The value of ruleId as greaterThan. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setRuleId_GreaterThan(final String ruleId) {
        regRuleId(CK_GT, fRES(ruleId));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * RULE_ID: {VARCHAR(20)}
     * 
     * @param ruleId
     *            The value of ruleId as lessThan. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setRuleId_LessThan(final String ruleId) {
        regRuleId(CK_LT, fRES(ruleId));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * RULE_ID: {VARCHAR(20)}
     * 
     * @param ruleId
     *            The value of ruleId as greaterEqual. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setRuleId_GreaterEqual(final String ruleId) {
        regRuleId(CK_GE, fRES(ruleId));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * RULE_ID: {VARCHAR(20)}
     * 
     * @param ruleId
     *            The value of ruleId as lessEqual. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setRuleId_LessEqual(final String ruleId) {
        regRuleId(CK_LE, fRES(ruleId));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * RULE_ID: {VARCHAR(20)}
     * 
     * @param ruleIdList
     *            The collection of ruleId as inScope. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setRuleId_InScope(final Collection<String> ruleIdList) {
        doSetRuleId_InScope(ruleIdList);
    }

    public void doSetRuleId_InScope(final Collection<String> ruleIdList) {
        regINS(CK_INS, cTL(ruleIdList), getCValueRuleId(), "RULE_ID");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * RULE_ID: {VARCHAR(20)}
     * 
     * @param ruleIdList
     *            The collection of ruleId as notInScope. (NullAllowed: if null
     *            (or empty), no condition)
     */
    public void setRuleId_NotInScope(final Collection<String> ruleIdList) {
        doSetRuleId_NotInScope(ruleIdList);
    }

    public void doSetRuleId_NotInScope(final Collection<String> ruleIdList) {
        regINS(CK_NINS, cTL(ruleIdList), getCValueRuleId(), "RULE_ID");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored,
     * SeveralRegistered. <br />
     * RULE_ID: {VARCHAR(20)}
     * 
     * @param ruleId
     *            The value of ruleId as prefixSearch. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setRuleId_PrefixSearch(final String ruleId) {
        setRuleId_LikeSearch(ruleId, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}.
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * RULE_ID: {VARCHAR(20)} <br />
     * 
     * <pre>e.g. setRuleId_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * 
     * @param ruleId
     *            The value of ruleId as likeSearch. (NullAllowed: if null (or
     *            empty), no condition)
     * @param likeSearchOption
     *            The option of like-search. (NotNull)
     */
    public void setRuleId_LikeSearch(final String ruleId,
            final LikeSearchOption likeSearchOption) {
        regLSQ(
            CK_LS,
            fRES(ruleId),
            getCValueRuleId(),
            "RULE_ID",
            likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape
     * ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * RULE_ID: {VARCHAR(20)}
     * 
     * @param ruleId
     *            The value of ruleId as notLikeSearch. (NullAllowed: if null
     *            (or empty), no condition)
     * @param likeSearchOption
     *            The option of not-like-search. (NotNull)
     */
    public void setRuleId_NotLikeSearch(final String ruleId,
            final LikeSearchOption likeSearchOption) {
        regLSQ(
            CK_NLS,
            fRES(ruleId),
            getCValueRuleId(),
            "RULE_ID",
            likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * RULE_ID: {VARCHAR(20)}
     */
    public void setRuleId_IsNull() {
        regRuleId(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * RULE_ID: {VARCHAR(20)}
     */
    public void setRuleId_IsNullOrEmpty() {
        regRuleId(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * RULE_ID: {VARCHAR(20)}
     */
    public void setRuleId_IsNotNull() {
        regRuleId(CK_ISNN, DOBJ);
    }

    protected void regRuleId(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueRuleId(), "RULE_ID");
    }

    abstract protected ConditionValue getCValueRuleId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {IX, NotNull, TEXT(65535)}
     * 
     * @param url
     *            The value of url as equal. (NullAllowed: if null (or empty),
     *            no condition)
     */
    public void setUrl_Equal(final String url) {
        doSetUrl_Equal(fRES(url));
    }

    protected void doSetUrl_Equal(final String url) {
        regUrl(CK_EQ, url);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {IX, NotNull, TEXT(65535)}
     * 
     * @param url
     *            The value of url as notEqual. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setUrl_NotEqual(final String url) {
        doSetUrl_NotEqual(fRES(url));
    }

    protected void doSetUrl_NotEqual(final String url) {
        regUrl(CK_NES, url);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {IX, NotNull, TEXT(65535)}
     * 
     * @param url
     *            The value of url as greaterThan. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setUrl_GreaterThan(final String url) {
        regUrl(CK_GT, fRES(url));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {IX, NotNull, TEXT(65535)}
     * 
     * @param url
     *            The value of url as lessThan. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setUrl_LessThan(final String url) {
        regUrl(CK_LT, fRES(url));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {IX, NotNull, TEXT(65535)}
     * 
     * @param url
     *            The value of url as greaterEqual. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setUrl_GreaterEqual(final String url) {
        regUrl(CK_GE, fRES(url));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {IX, NotNull, TEXT(65535)}
     * 
     * @param url
     *            The value of url as lessEqual. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setUrl_LessEqual(final String url) {
        regUrl(CK_LE, fRES(url));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * URL: {IX, NotNull, TEXT(65535)}
     * 
     * @param urlList
     *            The collection of url as inScope. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setUrl_InScope(final Collection<String> urlList) {
        doSetUrl_InScope(urlList);
    }

    public void doSetUrl_InScope(final Collection<String> urlList) {
        regINS(CK_INS, cTL(urlList), getCValueUrl(), "URL");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * URL: {IX, NotNull, TEXT(65535)}
     * 
     * @param urlList
     *            The collection of url as notInScope. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setUrl_NotInScope(final Collection<String> urlList) {
        doSetUrl_NotInScope(urlList);
    }

    public void doSetUrl_NotInScope(final Collection<String> urlList) {
        regINS(CK_NINS, cTL(urlList), getCValueUrl(), "URL");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored,
     * SeveralRegistered. <br />
     * URL: {IX, NotNull, TEXT(65535)}
     * 
     * @param url
     *            The value of url as prefixSearch. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setUrl_PrefixSearch(final String url) {
        setUrl_LikeSearch(url, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}.
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * URL: {IX, NotNull, TEXT(65535)} <br />
     * 
     * <pre>e.g. setUrl_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * 
     * @param url
     *            The value of url as likeSearch. (NullAllowed: if null (or
     *            empty), no condition)
     * @param likeSearchOption
     *            The option of like-search. (NotNull)
     */
    public void setUrl_LikeSearch(final String url,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(url), getCValueUrl(), "URL", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape
     * ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * URL: {IX, NotNull, TEXT(65535)}
     * 
     * @param url
     *            The value of url as notLikeSearch. (NullAllowed: if null (or
     *            empty), no condition)
     * @param likeSearchOption
     *            The option of not-like-search. (NotNull)
     */
    public void setUrl_NotLikeSearch(final String url,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(url), getCValueUrl(), "URL", likeSearchOption);
    }

    protected void regUrl(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueUrl(), "URL");
    }

    abstract protected ConditionValue getCValueUrl();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * PARENT_URL: {TEXT(65535)}
     * 
     * @param parentUrl
     *            The value of parentUrl as equal. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setParentUrl_Equal(final String parentUrl) {
        doSetParentUrl_Equal(fRES(parentUrl));
    }

    protected void doSetParentUrl_Equal(final String parentUrl) {
        regParentUrl(CK_EQ, parentUrl);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * PARENT_URL: {TEXT(65535)}
     * 
     * @param parentUrl
     *            The value of parentUrl as notEqual. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setParentUrl_NotEqual(final String parentUrl) {
        doSetParentUrl_NotEqual(fRES(parentUrl));
    }

    protected void doSetParentUrl_NotEqual(final String parentUrl) {
        regParentUrl(CK_NES, parentUrl);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * PARENT_URL: {TEXT(65535)}
     * 
     * @param parentUrl
     *            The value of parentUrl as greaterThan. (NullAllowed: if null
     *            (or empty), no condition)
     */
    public void setParentUrl_GreaterThan(final String parentUrl) {
        regParentUrl(CK_GT, fRES(parentUrl));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * PARENT_URL: {TEXT(65535)}
     * 
     * @param parentUrl
     *            The value of parentUrl as lessThan. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setParentUrl_LessThan(final String parentUrl) {
        regParentUrl(CK_LT, fRES(parentUrl));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * PARENT_URL: {TEXT(65535)}
     * 
     * @param parentUrl
     *            The value of parentUrl as greaterEqual. (NullAllowed: if null
     *            (or empty), no condition)
     */
    public void setParentUrl_GreaterEqual(final String parentUrl) {
        regParentUrl(CK_GE, fRES(parentUrl));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * PARENT_URL: {TEXT(65535)}
     * 
     * @param parentUrl
     *            The value of parentUrl as lessEqual. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setParentUrl_LessEqual(final String parentUrl) {
        regParentUrl(CK_LE, fRES(parentUrl));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * PARENT_URL: {TEXT(65535)}
     * 
     * @param parentUrlList
     *            The collection of parentUrl as inScope. (NullAllowed: if null
     *            (or empty), no condition)
     */
    public void setParentUrl_InScope(final Collection<String> parentUrlList) {
        doSetParentUrl_InScope(parentUrlList);
    }

    public void doSetParentUrl_InScope(final Collection<String> parentUrlList) {
        regINS(CK_INS, cTL(parentUrlList), getCValueParentUrl(), "PARENT_URL");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * PARENT_URL: {TEXT(65535)}
     * 
     * @param parentUrlList
     *            The collection of parentUrl as notInScope. (NullAllowed: if
     *            null (or empty), no condition)
     */
    public void setParentUrl_NotInScope(final Collection<String> parentUrlList) {
        doSetParentUrl_NotInScope(parentUrlList);
    }

    public void doSetParentUrl_NotInScope(final Collection<String> parentUrlList) {
        regINS(CK_NINS, cTL(parentUrlList), getCValueParentUrl(), "PARENT_URL");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored,
     * SeveralRegistered. <br />
     * PARENT_URL: {TEXT(65535)}
     * 
     * @param parentUrl
     *            The value of parentUrl as prefixSearch. (NullAllowed: if null
     *            (or empty), no condition)
     */
    public void setParentUrl_PrefixSearch(final String parentUrl) {
        setParentUrl_LikeSearch(parentUrl, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}.
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * PARENT_URL: {TEXT(65535)} <br />
     * 
     * <pre>e.g. setParentUrl_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * 
     * @param parentUrl
     *            The value of parentUrl as likeSearch. (NullAllowed: if null
     *            (or empty), no condition)
     * @param likeSearchOption
     *            The option of like-search. (NotNull)
     */
    public void setParentUrl_LikeSearch(final String parentUrl,
            final LikeSearchOption likeSearchOption) {
        regLSQ(
            CK_LS,
            fRES(parentUrl),
            getCValueParentUrl(),
            "PARENT_URL",
            likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape
     * ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * PARENT_URL: {TEXT(65535)}
     * 
     * @param parentUrl
     *            The value of parentUrl as notLikeSearch. (NullAllowed: if null
     *            (or empty), no condition)
     * @param likeSearchOption
     *            The option of not-like-search. (NotNull)
     */
    public void setParentUrl_NotLikeSearch(final String parentUrl,
            final LikeSearchOption likeSearchOption) {
        regLSQ(
            CK_NLS,
            fRES(parentUrl),
            getCValueParentUrl(),
            "PARENT_URL",
            likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * PARENT_URL: {TEXT(65535)}
     */
    public void setParentUrl_IsNull() {
        regParentUrl(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * PARENT_URL: {TEXT(65535)}
     */
    public void setParentUrl_IsNullOrEmpty() {
        regParentUrl(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * PARENT_URL: {TEXT(65535)}
     */
    public void setParentUrl_IsNotNull() {
        regParentUrl(CK_ISNN, DOBJ);
    }

    protected void regParentUrl(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueParentUrl(), "PARENT_URL");
    }

    abstract protected ConditionValue getCValueParentUrl();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * STATUS: {NotNull, INT(10)}
     * 
     * @param status
     *            The value of status as equal. (NullAllowed: if null, no
     *            condition)
     */
    public void setStatus_Equal(final Integer status) {
        doSetStatus_Equal(status);
    }

    protected void doSetStatus_Equal(final Integer status) {
        regStatus(CK_EQ, status);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * STATUS: {NotNull, INT(10)}
     * 
     * @param status
     *            The value of status as notEqual. (NullAllowed: if null, no
     *            condition)
     */
    public void setStatus_NotEqual(final Integer status) {
        doSetStatus_NotEqual(status);
    }

    protected void doSetStatus_NotEqual(final Integer status) {
        regStatus(CK_NES, status);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * STATUS: {NotNull, INT(10)}
     * 
     * @param status
     *            The value of status as greaterThan. (NullAllowed: if null, no
     *            condition)
     */
    public void setStatus_GreaterThan(final Integer status) {
        regStatus(CK_GT, status);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * STATUS: {NotNull, INT(10)}
     * 
     * @param status
     *            The value of status as lessThan. (NullAllowed: if null, no
     *            condition)
     */
    public void setStatus_LessThan(final Integer status) {
        regStatus(CK_LT, status);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * STATUS: {NotNull, INT(10)}
     * 
     * @param status
     *            The value of status as greaterEqual. (NullAllowed: if null, no
     *            condition)
     */
    public void setStatus_GreaterEqual(final Integer status) {
        regStatus(CK_GE, status);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * STATUS: {NotNull, INT(10)}
     * 
     * @param status
     *            The value of status as lessEqual. (NullAllowed: if null, no
     *            condition)
     */
    public void setStatus_LessEqual(final Integer status) {
        regStatus(CK_LE, status);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * STATUS: {NotNull, INT(10)}
     * 
     * @param minNumber
     *            The min number of status. (NullAllowed: if null, no
     *            from-condition)
     * @param maxNumber
     *            The max number of status. (NullAllowed: if null, no
     *            to-condition)
     * @param rangeOfOption
     *            The option of range-of. (NotNull)
     */
    public void setStatus_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueStatus(), "STATUS", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored,
     * SeveralRegistered. <br />
     * STATUS: {NotNull, INT(10)}
     * 
     * @param statusList
     *            The collection of status as inScope. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setStatus_InScope(final Collection<Integer> statusList) {
        doSetStatus_InScope(statusList);
    }

    protected void doSetStatus_InScope(final Collection<Integer> statusList) {
        regINS(CK_INS, cTL(statusList), getCValueStatus(), "STATUS");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored,
     * SeveralRegistered. <br />
     * STATUS: {NotNull, INT(10)}
     * 
     * @param statusList
     *            The collection of status as notInScope. (NullAllowed: if null
     *            (or empty), no condition)
     */
    public void setStatus_NotInScope(final Collection<Integer> statusList) {
        doSetStatus_NotInScope(statusList);
    }

    protected void doSetStatus_NotInScope(final Collection<Integer> statusList) {
        regINS(CK_NINS, cTL(statusList), getCValueStatus(), "STATUS");
    }

    protected void regStatus(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueStatus(), "STATUS");
    }

    abstract protected ConditionValue getCValueStatus();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * HTTP_STATUS_CODE: {NotNull, INT(10)}
     * 
     * @param httpStatusCode
     *            The value of httpStatusCode as equal. (NullAllowed: if null,
     *            no condition)
     */
    public void setHttpStatusCode_Equal(final Integer httpStatusCode) {
        doSetHttpStatusCode_Equal(httpStatusCode);
    }

    protected void doSetHttpStatusCode_Equal(final Integer httpStatusCode) {
        regHttpStatusCode(CK_EQ, httpStatusCode);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * HTTP_STATUS_CODE: {NotNull, INT(10)}
     * 
     * @param httpStatusCode
     *            The value of httpStatusCode as notEqual. (NullAllowed: if
     *            null, no condition)
     */
    public void setHttpStatusCode_NotEqual(final Integer httpStatusCode) {
        doSetHttpStatusCode_NotEqual(httpStatusCode);
    }

    protected void doSetHttpStatusCode_NotEqual(final Integer httpStatusCode) {
        regHttpStatusCode(CK_NES, httpStatusCode);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * HTTP_STATUS_CODE: {NotNull, INT(10)}
     * 
     * @param httpStatusCode
     *            The value of httpStatusCode as greaterThan. (NullAllowed: if
     *            null, no condition)
     */
    public void setHttpStatusCode_GreaterThan(final Integer httpStatusCode) {
        regHttpStatusCode(CK_GT, httpStatusCode);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * HTTP_STATUS_CODE: {NotNull, INT(10)}
     * 
     * @param httpStatusCode
     *            The value of httpStatusCode as lessThan. (NullAllowed: if
     *            null, no condition)
     */
    public void setHttpStatusCode_LessThan(final Integer httpStatusCode) {
        regHttpStatusCode(CK_LT, httpStatusCode);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * HTTP_STATUS_CODE: {NotNull, INT(10)}
     * 
     * @param httpStatusCode
     *            The value of httpStatusCode as greaterEqual. (NullAllowed: if
     *            null, no condition)
     */
    public void setHttpStatusCode_GreaterEqual(final Integer httpStatusCode) {
        regHttpStatusCode(CK_GE, httpStatusCode);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * HTTP_STATUS_CODE: {NotNull, INT(10)}
     * 
     * @param httpStatusCode
     *            The value of httpStatusCode as lessEqual. (NullAllowed: if
     *            null, no condition)
     */
    public void setHttpStatusCode_LessEqual(final Integer httpStatusCode) {
        regHttpStatusCode(CK_LE, httpStatusCode);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * HTTP_STATUS_CODE: {NotNull, INT(10)}
     * 
     * @param minNumber
     *            The min number of httpStatusCode. (NullAllowed: if null, no
     *            from-condition)
     * @param maxNumber
     *            The max number of httpStatusCode. (NullAllowed: if null, no
     *            to-condition)
     * @param rangeOfOption
     *            The option of range-of. (NotNull)
     */
    public void setHttpStatusCode_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(
            minNumber,
            maxNumber,
            getCValueHttpStatusCode(),
            "HTTP_STATUS_CODE",
            rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored,
     * SeveralRegistered. <br />
     * HTTP_STATUS_CODE: {NotNull, INT(10)}
     * 
     * @param httpStatusCodeList
     *            The collection of httpStatusCode as inScope. (NullAllowed: if
     *            null (or empty), no condition)
     */
    public void setHttpStatusCode_InScope(
            final Collection<Integer> httpStatusCodeList) {
        doSetHttpStatusCode_InScope(httpStatusCodeList);
    }

    protected void doSetHttpStatusCode_InScope(
            final Collection<Integer> httpStatusCodeList) {
        regINS(
            CK_INS,
            cTL(httpStatusCodeList),
            getCValueHttpStatusCode(),
            "HTTP_STATUS_CODE");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored,
     * SeveralRegistered. <br />
     * HTTP_STATUS_CODE: {NotNull, INT(10)}
     * 
     * @param httpStatusCodeList
     *            The collection of httpStatusCode as notInScope. (NullAllowed:
     *            if null (or empty), no condition)
     */
    public void setHttpStatusCode_NotInScope(
            final Collection<Integer> httpStatusCodeList) {
        doSetHttpStatusCode_NotInScope(httpStatusCodeList);
    }

    protected void doSetHttpStatusCode_NotInScope(
            final Collection<Integer> httpStatusCodeList) {
        regINS(
            CK_NINS,
            cTL(httpStatusCodeList),
            getCValueHttpStatusCode(),
            "HTTP_STATUS_CODE");
    }

    protected void regHttpStatusCode(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueHttpStatusCode(), "HTTP_STATUS_CODE");
    }

    abstract protected ConditionValue getCValueHttpStatusCode();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * METHOD: {NotNull, VARCHAR(10)}
     * 
     * @param method
     *            The value of method as equal. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setMethod_Equal(final String method) {
        doSetMethod_Equal(fRES(method));
    }

    protected void doSetMethod_Equal(final String method) {
        regMethod(CK_EQ, method);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * METHOD: {NotNull, VARCHAR(10)}
     * 
     * @param method
     *            The value of method as notEqual. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setMethod_NotEqual(final String method) {
        doSetMethod_NotEqual(fRES(method));
    }

    protected void doSetMethod_NotEqual(final String method) {
        regMethod(CK_NES, method);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * METHOD: {NotNull, VARCHAR(10)}
     * 
     * @param method
     *            The value of method as greaterThan. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setMethod_GreaterThan(final String method) {
        regMethod(CK_GT, fRES(method));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * METHOD: {NotNull, VARCHAR(10)}
     * 
     * @param method
     *            The value of method as lessThan. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setMethod_LessThan(final String method) {
        regMethod(CK_LT, fRES(method));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * METHOD: {NotNull, VARCHAR(10)}
     * 
     * @param method
     *            The value of method as greaterEqual. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setMethod_GreaterEqual(final String method) {
        regMethod(CK_GE, fRES(method));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * METHOD: {NotNull, VARCHAR(10)}
     * 
     * @param method
     *            The value of method as lessEqual. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setMethod_LessEqual(final String method) {
        regMethod(CK_LE, fRES(method));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * METHOD: {NotNull, VARCHAR(10)}
     * 
     * @param methodList
     *            The collection of method as inScope. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setMethod_InScope(final Collection<String> methodList) {
        doSetMethod_InScope(methodList);
    }

    public void doSetMethod_InScope(final Collection<String> methodList) {
        regINS(CK_INS, cTL(methodList), getCValueMethod(), "METHOD");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * METHOD: {NotNull, VARCHAR(10)}
     * 
     * @param methodList
     *            The collection of method as notInScope. (NullAllowed: if null
     *            (or empty), no condition)
     */
    public void setMethod_NotInScope(final Collection<String> methodList) {
        doSetMethod_NotInScope(methodList);
    }

    public void doSetMethod_NotInScope(final Collection<String> methodList) {
        regINS(CK_NINS, cTL(methodList), getCValueMethod(), "METHOD");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored,
     * SeveralRegistered. <br />
     * METHOD: {NotNull, VARCHAR(10)}
     * 
     * @param method
     *            The value of method as prefixSearch. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setMethod_PrefixSearch(final String method) {
        setMethod_LikeSearch(method, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}.
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * METHOD: {NotNull, VARCHAR(10)} <br />
     * 
     * <pre>e.g. setMethod_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * 
     * @param method
     *            The value of method as likeSearch. (NullAllowed: if null (or
     *            empty), no condition)
     * @param likeSearchOption
     *            The option of like-search. (NotNull)
     */
    public void setMethod_LikeSearch(final String method,
            final LikeSearchOption likeSearchOption) {
        regLSQ(
            CK_LS,
            fRES(method),
            getCValueMethod(),
            "METHOD",
            likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape
     * ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * METHOD: {NotNull, VARCHAR(10)}
     * 
     * @param method
     *            The value of method as notLikeSearch. (NullAllowed: if null
     *            (or empty), no condition)
     * @param likeSearchOption
     *            The option of not-like-search. (NotNull)
     */
    public void setMethod_NotLikeSearch(final String method,
            final LikeSearchOption likeSearchOption) {
        regLSQ(
            CK_NLS,
            fRES(method),
            getCValueMethod(),
            "METHOD",
            likeSearchOption);
    }

    protected void regMethod(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueMethod(), "METHOD");
    }

    abstract protected ConditionValue getCValueMethod();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * 
     * @param mimeType
     *            The value of mimeType as equal. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setMimeType_Equal(final String mimeType) {
        doSetMimeType_Equal(fRES(mimeType));
    }

    protected void doSetMimeType_Equal(final String mimeType) {
        regMimeType(CK_EQ, mimeType);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * 
     * @param mimeType
     *            The value of mimeType as notEqual. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setMimeType_NotEqual(final String mimeType) {
        doSetMimeType_NotEqual(fRES(mimeType));
    }

    protected void doSetMimeType_NotEqual(final String mimeType) {
        regMimeType(CK_NES, mimeType);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * 
     * @param mimeType
     *            The value of mimeType as greaterThan. (NullAllowed: if null
     *            (or empty), no condition)
     */
    public void setMimeType_GreaterThan(final String mimeType) {
        regMimeType(CK_GT, fRES(mimeType));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * 
     * @param mimeType
     *            The value of mimeType as lessThan. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setMimeType_LessThan(final String mimeType) {
        regMimeType(CK_LT, fRES(mimeType));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * 
     * @param mimeType
     *            The value of mimeType as greaterEqual. (NullAllowed: if null
     *            (or empty), no condition)
     */
    public void setMimeType_GreaterEqual(final String mimeType) {
        regMimeType(CK_GE, fRES(mimeType));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * 
     * @param mimeType
     *            The value of mimeType as lessEqual. (NullAllowed: if null (or
     *            empty), no condition)
     */
    public void setMimeType_LessEqual(final String mimeType) {
        regMimeType(CK_LE, fRES(mimeType));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * 
     * @param mimeTypeList
     *            The collection of mimeType as inScope. (NullAllowed: if null
     *            (or empty), no condition)
     */
    public void setMimeType_InScope(final Collection<String> mimeTypeList) {
        doSetMimeType_InScope(mimeTypeList);
    }

    public void doSetMimeType_InScope(final Collection<String> mimeTypeList) {
        regINS(CK_INS, cTL(mimeTypeList), getCValueMimeType(), "MIME_TYPE");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored,
     * NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * 
     * @param mimeTypeList
     *            The collection of mimeType as notInScope. (NullAllowed: if
     *            null (or empty), no condition)
     */
    public void setMimeType_NotInScope(final Collection<String> mimeTypeList) {
        doSetMimeType_NotInScope(mimeTypeList);
    }

    public void doSetMimeType_NotInScope(final Collection<String> mimeTypeList) {
        regINS(CK_NINS, cTL(mimeTypeList), getCValueMimeType(), "MIME_TYPE");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored,
     * SeveralRegistered. <br />
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * 
     * @param mimeType
     *            The value of mimeType as prefixSearch. (NullAllowed: if null
     *            (or empty), no condition)
     */
    public void setMimeType_PrefixSearch(final String mimeType) {
        setMimeType_LikeSearch(mimeType, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}.
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * MIME_TYPE: {NotNull, VARCHAR(100)} <br />
     * 
     * <pre>e.g. setMimeType_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * 
     * @param mimeType
     *            The value of mimeType as likeSearch. (NullAllowed: if null (or
     *            empty), no condition)
     * @param likeSearchOption
     *            The option of like-search. (NotNull)
     */
    public void setMimeType_LikeSearch(final String mimeType,
            final LikeSearchOption likeSearchOption) {
        regLSQ(
            CK_LS,
            fRES(mimeType),
            getCValueMimeType(),
            "MIME_TYPE",
            likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape
     * ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * 
     * @param mimeType
     *            The value of mimeType as notLikeSearch. (NullAllowed: if null
     *            (or empty), no condition)
     * @param likeSearchOption
     *            The option of not-like-search. (NotNull)
     */
    public void setMimeType_NotLikeSearch(final String mimeType,
            final LikeSearchOption likeSearchOption) {
        regLSQ(
            CK_NLS,
            fRES(mimeType),
            getCValueMimeType(),
            "MIME_TYPE",
            likeSearchOption);
    }

    protected void regMimeType(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueMimeType(), "MIME_TYPE");
    }

    abstract protected ConditionValue getCValueMimeType();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * 
     * @param contentLength
     *            The value of contentLength as equal. (NullAllowed: if null, no
     *            condition)
     */
    public void setContentLength_Equal(final Long contentLength) {
        doSetContentLength_Equal(contentLength);
    }

    protected void doSetContentLength_Equal(final Long contentLength) {
        regContentLength(CK_EQ, contentLength);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * 
     * @param contentLength
     *            The value of contentLength as notEqual. (NullAllowed: if null,
     *            no condition)
     */
    public void setContentLength_NotEqual(final Long contentLength) {
        doSetContentLength_NotEqual(contentLength);
    }

    protected void doSetContentLength_NotEqual(final Long contentLength) {
        regContentLength(CK_NES, contentLength);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * 
     * @param contentLength
     *            The value of contentLength as greaterThan. (NullAllowed: if
     *            null, no condition)
     */
    public void setContentLength_GreaterThan(final Long contentLength) {
        regContentLength(CK_GT, contentLength);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * 
     * @param contentLength
     *            The value of contentLength as lessThan. (NullAllowed: if null,
     *            no condition)
     */
    public void setContentLength_LessThan(final Long contentLength) {
        regContentLength(CK_LT, contentLength);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * 
     * @param contentLength
     *            The value of contentLength as greaterEqual. (NullAllowed: if
     *            null, no condition)
     */
    public void setContentLength_GreaterEqual(final Long contentLength) {
        regContentLength(CK_GE, contentLength);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * 
     * @param contentLength
     *            The value of contentLength as lessEqual. (NullAllowed: if
     *            null, no condition)
     */
    public void setContentLength_LessEqual(final Long contentLength) {
        regContentLength(CK_LE, contentLength);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * 
     * @param minNumber
     *            The min number of contentLength. (NullAllowed: if null, no
     *            from-condition)
     * @param maxNumber
     *            The max number of contentLength. (NullAllowed: if null, no
     *            to-condition)
     * @param rangeOfOption
     *            The option of range-of. (NotNull)
     */
    public void setContentLength_RangeOf(final Long minNumber,
            final Long maxNumber, final RangeOfOption rangeOfOption) {
        regROO(
            minNumber,
            maxNumber,
            getCValueContentLength(),
            "CONTENT_LENGTH",
            rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored,
     * SeveralRegistered. <br />
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * 
     * @param contentLengthList
     *            The collection of contentLength as inScope. (NullAllowed: if
     *            null (or empty), no condition)
     */
    public void setContentLength_InScope(
            final Collection<Long> contentLengthList) {
        doSetContentLength_InScope(contentLengthList);
    }

    protected void doSetContentLength_InScope(
            final Collection<Long> contentLengthList) {
        regINS(
            CK_INS,
            cTL(contentLengthList),
            getCValueContentLength(),
            "CONTENT_LENGTH");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored,
     * SeveralRegistered. <br />
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * 
     * @param contentLengthList
     *            The collection of contentLength as notInScope. (NullAllowed:
     *            if null (or empty), no condition)
     */
    public void setContentLength_NotInScope(
            final Collection<Long> contentLengthList) {
        doSetContentLength_NotInScope(contentLengthList);
    }

    protected void doSetContentLength_NotInScope(
            final Collection<Long> contentLengthList) {
        regINS(
            CK_NINS,
            cTL(contentLengthList),
            getCValueContentLength(),
            "CONTENT_LENGTH");
    }

    protected void regContentLength(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueContentLength(), "CONTENT_LENGTH");
    }

    abstract protected ConditionValue getCValueContentLength();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * EXECUTION_TIME: {NotNull, INT(10)}
     * 
     * @param executionTime
     *            The value of executionTime as equal. (NullAllowed: if null, no
     *            condition)
     */
    public void setExecutionTime_Equal(final Integer executionTime) {
        doSetExecutionTime_Equal(executionTime);
    }

    protected void doSetExecutionTime_Equal(final Integer executionTime) {
        regExecutionTime(CK_EQ, executionTime);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * EXECUTION_TIME: {NotNull, INT(10)}
     * 
     * @param executionTime
     *            The value of executionTime as notEqual. (NullAllowed: if null,
     *            no condition)
     */
    public void setExecutionTime_NotEqual(final Integer executionTime) {
        doSetExecutionTime_NotEqual(executionTime);
    }

    protected void doSetExecutionTime_NotEqual(final Integer executionTime) {
        regExecutionTime(CK_NES, executionTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * EXECUTION_TIME: {NotNull, INT(10)}
     * 
     * @param executionTime
     *            The value of executionTime as greaterThan. (NullAllowed: if
     *            null, no condition)
     */
    public void setExecutionTime_GreaterThan(final Integer executionTime) {
        regExecutionTime(CK_GT, executionTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * EXECUTION_TIME: {NotNull, INT(10)}
     * 
     * @param executionTime
     *            The value of executionTime as lessThan. (NullAllowed: if null,
     *            no condition)
     */
    public void setExecutionTime_LessThan(final Integer executionTime) {
        regExecutionTime(CK_LT, executionTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * EXECUTION_TIME: {NotNull, INT(10)}
     * 
     * @param executionTime
     *            The value of executionTime as greaterEqual. (NullAllowed: if
     *            null, no condition)
     */
    public void setExecutionTime_GreaterEqual(final Integer executionTime) {
        regExecutionTime(CK_GE, executionTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * EXECUTION_TIME: {NotNull, INT(10)}
     * 
     * @param executionTime
     *            The value of executionTime as lessEqual. (NullAllowed: if
     *            null, no condition)
     */
    public void setExecutionTime_LessEqual(final Integer executionTime) {
        regExecutionTime(CK_LE, executionTime);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * EXECUTION_TIME: {NotNull, INT(10)}
     * 
     * @param minNumber
     *            The min number of executionTime. (NullAllowed: if null, no
     *            from-condition)
     * @param maxNumber
     *            The max number of executionTime. (NullAllowed: if null, no
     *            to-condition)
     * @param rangeOfOption
     *            The option of range-of. (NotNull)
     */
    public void setExecutionTime_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(
            minNumber,
            maxNumber,
            getCValueExecutionTime(),
            "EXECUTION_TIME",
            rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored,
     * SeveralRegistered. <br />
     * EXECUTION_TIME: {NotNull, INT(10)}
     * 
     * @param executionTimeList
     *            The collection of executionTime as inScope. (NullAllowed: if
     *            null (or empty), no condition)
     */
    public void setExecutionTime_InScope(
            final Collection<Integer> executionTimeList) {
        doSetExecutionTime_InScope(executionTimeList);
    }

    protected void doSetExecutionTime_InScope(
            final Collection<Integer> executionTimeList) {
        regINS(
            CK_INS,
            cTL(executionTimeList),
            getCValueExecutionTime(),
            "EXECUTION_TIME");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored,
     * SeveralRegistered. <br />
     * EXECUTION_TIME: {NotNull, INT(10)}
     * 
     * @param executionTimeList
     *            The collection of executionTime as notInScope. (NullAllowed:
     *            if null (or empty), no condition)
     */
    public void setExecutionTime_NotInScope(
            final Collection<Integer> executionTimeList) {
        doSetExecutionTime_NotInScope(executionTimeList);
    }

    protected void doSetExecutionTime_NotInScope(
            final Collection<Integer> executionTimeList) {
        regINS(
            CK_NINS,
            cTL(executionTimeList),
            getCValueExecutionTime(),
            "EXECUTION_TIME");
    }

    protected void regExecutionTime(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueExecutionTime(), "EXECUTION_TIME");
    }

    abstract protected ConditionValue getCValueExecutionTime();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * LAST_MODIFIED: {NotNull, DATETIME(19)}
     * 
     * @param lastModified
     *            The value of lastModified as equal. (NullAllowed: if null, no
     *            condition)
     */
    public void setLastModified_Equal(final java.sql.Timestamp lastModified) {
        regLastModified(CK_EQ, lastModified);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * LAST_MODIFIED: {NotNull, DATETIME(19)}
     * 
     * @param lastModified
     *            The value of lastModified as greaterThan. (NullAllowed: if
     *            null, no condition)
     */
    public void setLastModified_GreaterThan(
            final java.sql.Timestamp lastModified) {
        regLastModified(CK_GT, lastModified);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * LAST_MODIFIED: {NotNull, DATETIME(19)}
     * 
     * @param lastModified
     *            The value of lastModified as lessThan. (NullAllowed: if null,
     *            no condition)
     */
    public void setLastModified_LessThan(final java.sql.Timestamp lastModified) {
        regLastModified(CK_LT, lastModified);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * LAST_MODIFIED: {NotNull, DATETIME(19)}
     * 
     * @param lastModified
     *            The value of lastModified as greaterEqual. (NullAllowed: if
     *            null, no condition)
     */
    public void setLastModified_GreaterEqual(
            final java.sql.Timestamp lastModified) {
        regLastModified(CK_GE, lastModified);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * LAST_MODIFIED: {NotNull, DATETIME(19)}
     * 
     * @param lastModified
     *            The value of lastModified as lessEqual. (NullAllowed: if null,
     *            no condition)
     */
    public void setLastModified_LessEqual(final java.sql.Timestamp lastModified) {
        regLastModified(CK_LE, lastModified);
    }

    /**
     * FromTo with various options. (versatile) {(default) fromDatetime &lt;=
     * column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * LAST_MODIFIED: {NotNull, DATETIME(19)}
     * 
     * <pre>e.g. setLastModified_FromTo(fromDate, toDate, new <span style="color: #FD4747">FromToOption</span>().compareAsDate());</pre>
     * 
     * @param fromDatetime
     *            The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of lastModified.
     *            (NullAllowed: if null, no from-condition)
     * @param toDatetime
     *            The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of lastModified.
     *            (NullAllowed: if null, no to-condition)
     * @param fromToOption
     *            The option of from-to. (NotNull)
     */
    public void setLastModified_FromTo(final java.util.Date fromDatetime,
            final java.util.Date toDatetime, final FromToOption fromToOption) {
        regFTQ(
            fromDatetime != null ? new java.sql.Timestamp(
                fromDatetime.getTime()) : null,
            toDatetime != null ? new java.sql.Timestamp(toDatetime.getTime())
                : null,
            getCValueLastModified(),
            "LAST_MODIFIED",
            fromToOption);
    }

    /**
     * DateFromTo. (Date means yyyy/MM/dd) {fromDate &lt;= column &lt; toDate +
     * 1 day} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * LAST_MODIFIED: {NotNull, DATETIME(19)}
     * 
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #FD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * 
     * @param fromDate
     *            The from-date(yyyy/MM/dd) of lastModified. (NullAllowed: if
     *            null, no from-condition)
     * @param toDate
     *            The to-date(yyyy/MM/dd) of lastModified. (NullAllowed: if
     *            null, no to-condition)
     */
    public void setLastModified_DateFromTo(final java.util.Date fromDate,
            final java.util.Date toDate) {
        setLastModified_FromTo(
            fromDate,
            toDate,
            new FromToOption().compareAsDate());
    }

    protected void regLastModified(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueLastModified(), "LAST_MODIFIED");
    }

    abstract protected ConditionValue getCValueLastModified();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATE_TIME: {IX+, NotNull, DATETIME(19)}
     * 
     * @param createTime
     *            The value of createTime as equal. (NullAllowed: if null, no
     *            condition)
     */
    public void setCreateTime_Equal(final java.sql.Timestamp createTime) {
        regCreateTime(CK_EQ, createTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATE_TIME: {IX+, NotNull, DATETIME(19)}
     * 
     * @param createTime
     *            The value of createTime as greaterThan. (NullAllowed: if null,
     *            no condition)
     */
    public void setCreateTime_GreaterThan(final java.sql.Timestamp createTime) {
        regCreateTime(CK_GT, createTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATE_TIME: {IX+, NotNull, DATETIME(19)}
     * 
     * @param createTime
     *            The value of createTime as lessThan. (NullAllowed: if null, no
     *            condition)
     */
    public void setCreateTime_LessThan(final java.sql.Timestamp createTime) {
        regCreateTime(CK_LT, createTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATE_TIME: {IX+, NotNull, DATETIME(19)}
     * 
     * @param createTime
     *            The value of createTime as greaterEqual. (NullAllowed: if
     *            null, no condition)
     */
    public void setCreateTime_GreaterEqual(final java.sql.Timestamp createTime) {
        regCreateTime(CK_GE, createTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATE_TIME: {IX+, NotNull, DATETIME(19)}
     * 
     * @param createTime
     *            The value of createTime as lessEqual. (NullAllowed: if null,
     *            no condition)
     */
    public void setCreateTime_LessEqual(final java.sql.Timestamp createTime) {
        regCreateTime(CK_LE, createTime);
    }

    /**
     * FromTo with various options. (versatile) {(default) fromDatetime &lt;=
     * column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * CREATE_TIME: {IX+, NotNull, DATETIME(19)}
     * 
     * <pre>e.g. setCreateTime_FromTo(fromDate, toDate, new <span style="color: #FD4747">FromToOption</span>().compareAsDate());</pre>
     * 
     * @param fromDatetime
     *            The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of createTime.
     *            (NullAllowed: if null, no from-condition)
     * @param toDatetime
     *            The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of createTime.
     *            (NullAllowed: if null, no to-condition)
     * @param fromToOption
     *            The option of from-to. (NotNull)
     */
    public void setCreateTime_FromTo(final java.util.Date fromDatetime,
            final java.util.Date toDatetime, final FromToOption fromToOption) {
        regFTQ(
            fromDatetime != null ? new java.sql.Timestamp(
                fromDatetime.getTime()) : null,
            toDatetime != null ? new java.sql.Timestamp(toDatetime.getTime())
                : null,
            getCValueCreateTime(),
            "CREATE_TIME",
            fromToOption);
    }

    /**
     * DateFromTo. (Date means yyyy/MM/dd) {fromDate &lt;= column &lt; toDate +
     * 1 day} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * CREATE_TIME: {IX+, NotNull, DATETIME(19)}
     * 
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #FD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * 
     * @param fromDate
     *            The from-date(yyyy/MM/dd) of createTime. (NullAllowed: if
     *            null, no from-condition)
     * @param toDate
     *            The to-date(yyyy/MM/dd) of createTime. (NullAllowed: if null,
     *            no to-condition)
     */
    public void setCreateTime_DateFromTo(final java.util.Date fromDate,
            final java.util.Date toDate) {
        setCreateTime_FromTo(
            fromDate,
            toDate,
            new FromToOption().compareAsDate());
    }

    protected void regCreateTime(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueCreateTime(), "CREATE_TIME");
    }

    abstract protected ConditionValue getCValueCreateTime();

    // ===================================================================================
    // ScalarCondition
    // ===============
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * 
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_Equal()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * 
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ.getOperand());
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * 
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * 
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * 
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * 
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * 
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessThan()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * 
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * 
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * 
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * 
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * 
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE.getOperand());
    }

    protected HpSSQFunction<AccessResultCB> xcreateSSQFunction(
            final String operand) {
        return new HpSSQFunction<AccessResultCB>(
            new HpSSQSetupper<AccessResultCB>() {
                @Override
                public void setup(final String function,
                        final SubQuery<AccessResultCB> subQuery,
                        final HpSSQOption<AccessResultCB> option) {
                    xscalarCondition(function, subQuery, operand, option);
                }
            });
    }

    protected void xscalarCondition(final String function,
            final SubQuery<AccessResultCB> subQuery, final String operand,
            final HpSSQOption<AccessResultCB> option) {
        assertObjectNotNull("subQuery<AccessResultCB>", subQuery);
        final AccessResultCB cb = xcreateScalarConditionCB();
        subQuery.query(cb);
        final String subQueryPropertyName = keepScalarCondition(cb.query()); // for
                                                                             // saving
                                                                             // query-value
        option.setPartitionByCBean(xcreateScalarConditionPartitionByCB()); // for
                                                                           // using
                                                                           // partition-by
        registerScalarCondition(
            function,
            cb.query(),
            subQueryPropertyName,
            operand,
            option);
    }

    public abstract String keepScalarCondition(AccessResultCQ subQuery);

    protected AccessResultCB xcreateScalarConditionCB() {
        final AccessResultCB cb = new AccessResultCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected AccessResultCB xcreateScalarConditionPartitionByCB() {
        final AccessResultCB cb = new AccessResultCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    // MyselfDerived
    // =============
    public void xsmyselfDerive(final String function,
            final SubQuery<AccessResultCB> subQuery, final String aliasName,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<AccessResultCB>", subQuery);
        final AccessResultCB cb = new AccessResultCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName =
            keepSpecifyMyselfDerived(cb.query()); // for saving query-value.
        registerSpecifyMyselfDerived(
            function,
            cb.query(),
            "ID",
            "ID",
            subQueryPropertyName,
            "myselfDerived",
            aliasName,
            option);
    }

    public abstract String keepSpecifyMyselfDerived(AccessResultCQ subQuery);

    /**
     * Prepare for (Query)MyselfDerived (SubQuery).
     * 
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<AccessResultCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived();
    }

    protected HpQDRFunction<AccessResultCB> xcreateQDRFunctionMyselfDerived() {
        return new HpQDRFunction<AccessResultCB>(
            new HpQDRSetupper<AccessResultCB>() {
                @Override
                public void setup(final String function,
                        final SubQuery<AccessResultCB> subQuery,
                        final String operand, final Object value,
                        final DerivedReferrerOption option) {
                    xqderiveMyselfDerived(
                        function,
                        subQuery,
                        operand,
                        value,
                        option);
                }
            });
    }

    public void xqderiveMyselfDerived(final String function,
            final SubQuery<AccessResultCB> subQuery, final String operand,
            final Object value, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<AccessResultCB>", subQuery);
        final AccessResultCB cb = new AccessResultCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepQueryMyselfDerived(cb.query()); // for
                                                                                // saving
                                                                                // query-value.
        final String parameterPropertyName =
            keepQueryMyselfDerivedParameter(value);
        registerQueryMyselfDerived(
            function,
            cb.query(),
            "ID",
            "ID",
            subQueryPropertyName,
            "myselfDerived",
            operand,
            value,
            parameterPropertyName,
            option);
    }

    public abstract String keepQueryMyselfDerived(AccessResultCQ subQuery);

    public abstract String keepQueryMyselfDerivedParameter(Object parameterValue);

    // ===================================================================================
    // MyselfExists
    // ============
    /**
     * Prepare for MyselfExists (SubQuery).
     * 
     * @param subQuery
     *            The implementation of sub query. (NotNull)
     */
    public void myselfExists(final SubQuery<AccessResultCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultCB>", subQuery);
        final AccessResultCB cb = new AccessResultCB();
        cb.xsetupForMyselfExists(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfExists(cb.query()); // for
                                                                          // saving
                                                                          // query-value.
        registerMyselfExists(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfExists(AccessResultCQ subQuery);

    // ===================================================================================
    // MyselfInScope
    // =============
    /**
     * Prepare for MyselfInScope (SubQuery).
     * 
     * @param subQuery
     *            The implementation of sub query. (NotNull)
     */
    public void myselfInScope(final SubQuery<AccessResultCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultCB>", subQuery);
        final AccessResultCB cb = new AccessResultCB();
        cb.xsetupForMyselfInScope(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfInScope(cb.query()); // for
                                                                           // saving
                                                                           // query-value.
        registerMyselfInScope(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfInScope(AccessResultCQ subQuery);

    // ===================================================================================
    // Very Internal
    // =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCB() {
        return AccessResultCB.class.getName();
    }

    protected String xabCQ() {
        return AccessResultCQ.class.getName();
    }

    protected String xabLSO() {
        return LikeSearchOption.class.getName();
    }

    protected String xabSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
