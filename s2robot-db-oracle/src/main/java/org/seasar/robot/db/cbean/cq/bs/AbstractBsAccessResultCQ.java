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
import org.seasar.robot.dbflute.cbean.coption.DateFromToOption;
import org.seasar.robot.dbflute.cbean.coption.FromToOption;
import org.seasar.robot.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;

/**
 * The abstract condition-query of ACCESS_RESULT.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsAccessResultCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsAccessResultCQ(ConditionQuery childQuery,
            SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
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
        return "ACCESS_RESULT";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(12)}
     * @param id The value of id as equal.
     */
    public void setId_Equal(Long id) {
        doSetId_Equal(id);
    }

    protected void doSetId_Equal(Long id) {
        regId(CK_EQ, id);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param id The value of id as notEqual.
     */
    public void setId_NotEqual(Long id) {
        doSetId_NotEqual(id);
    }

    protected void doSetId_NotEqual(Long id) {
        regId(CK_NES, id);
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
        doSetId_InScope(idList);
    }

    protected void doSetId_InScope(Collection<Long> idList) {
        regINS(CK_INS, cTL(idList), getCValueId(), "ID");
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param idList The collection of id as notInScope.
     */
    public void setId_NotInScope(Collection<Long> idList) {
        doSetId_NotInScope(idList);
    }

    protected void doSetId_NotInScope(Collection<Long> idList) {
        regINS(CK_NINS, cTL(idList), getCValueId(), "ID");
    }

    /**
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select ID from ACCESS_RESULT_DATA where ...)} <br />
     * ACCESS_RESULT_DATA as 'accessResultDataList'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsAccessResultDataAsOne</span>(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of AccessResultDataAsOne for 'exists'. (NotNull)
     */
    public void existsAccessResultDataAsOne(
            SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        String subQueryPropertyName = keepId_ExistsReferrer_AccessResultDataAsOne(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "ID", subQueryPropertyName);
    }

    public abstract String keepId_ExistsReferrer_AccessResultDataAsOne(
            AccessResultDataCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select ID from ACCESS_RESULT_DATA where ...)} <br />
     * ACCESS_RESULT_DATA as 'accessResultDataList'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsAccessResultDataAsOne</span>(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_AccessResultDataAsOne for 'not exists'. (NotNull)
     */
    public void notExistsAccessResultDataAsOne(
            SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        String subQueryPropertyName = keepId_NotExistsReferrer_AccessResultDataAsOne(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "ID", subQueryPropertyName);
    }

    public abstract String keepId_NotExistsReferrer_AccessResultDataAsOne(
            AccessResultDataCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select ID from ACCESS_RESULT_DATA where ...)} <br />
     * ACCESS_RESULT_DATA as 'accessResultDataList'.
     * @param subQuery The sub-query of AccessResultDataAsOne for 'in-scope'. (NotNull)
     */
    public void inScopeAccessResultDataAsOne(
            SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        String subQueryPropertyName = keepId_InScopeRelation_AccessResultDataAsOne(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "ID", subQueryPropertyName);
    }

    public abstract String keepId_InScopeRelation_AccessResultDataAsOne(
            AccessResultDataCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select ID from ACCESS_RESULT_DATA where ...)} <br />
     * ACCESS_RESULT_DATA as 'accessResultDataList'.
     * @param subQuery The sub-query of AccessResultDataAsOne for 'not in-scope'. (NotNull)
     */
    public void notInScopeAccessResultDataAsOne(
            SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        String subQueryPropertyName = keepId_NotInScopeRelation_AccessResultDataAsOne(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "ID", subQueryPropertyName);
    }

    public abstract String keepId_NotInScopeRelation_AccessResultDataAsOne(
            AccessResultDataCQ subQuery);

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
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SESSION_ID: {IX, NotNull, VARCHAR2(20)}
     * @param sessionId The value of sessionId as equal.
     */
    public void setSessionId_Equal(String sessionId) {
        doSetSessionId_Equal(fRES(sessionId));
    }

    protected void doSetSessionId_Equal(String sessionId) {
        regSessionId(CK_EQ, sessionId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param sessionId The value of sessionId as notEqual.
     */
    public void setSessionId_NotEqual(String sessionId) {
        doSetSessionId_NotEqual(fRES(sessionId));
    }

    protected void doSetSessionId_NotEqual(String sessionId) {
        regSessionId(CK_NES, sessionId);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param sessionId The value of sessionId as greaterThan.
     */
    public void setSessionId_GreaterThan(String sessionId) {
        regSessionId(CK_GT, fRES(sessionId));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param sessionId The value of sessionId as lessThan.
     */
    public void setSessionId_LessThan(String sessionId) {
        regSessionId(CK_LT, fRES(sessionId));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param sessionId The value of sessionId as greaterEqual.
     */
    public void setSessionId_GreaterEqual(String sessionId) {
        regSessionId(CK_GE, fRES(sessionId));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param sessionId The value of sessionId as lessEqual.
     */
    public void setSessionId_LessEqual(String sessionId) {
        regSessionId(CK_LE, fRES(sessionId));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param sessionIdList The collection of sessionId as inScope.
     */
    public void setSessionId_InScope(Collection<String> sessionIdList) {
        doSetSessionId_InScope(sessionIdList);
    }

    public void doSetSessionId_InScope(Collection<String> sessionIdList) {
        regINS(CK_INS, cTL(sessionIdList), getCValueSessionId(), "SESSION_ID");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param sessionIdList The collection of sessionId as notInScope.
     */
    public void setSessionId_NotInScope(Collection<String> sessionIdList) {
        doSetSessionId_NotInScope(sessionIdList);
    }

    public void doSetSessionId_NotInScope(Collection<String> sessionIdList) {
        regINS(CK_NINS, cTL(sessionIdList), getCValueSessionId(), "SESSION_ID");
    }

    /**
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param sessionId The value of sessionId as prefixSearch.
     */
    public void setSessionId_PrefixSearch(String sessionId) {
        setSessionId_LikeSearch(sessionId, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * @param sessionId The value of sessionId as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setSessionId_LikeSearch(String sessionId,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(sessionId), getCValueSessionId(), "SESSION_ID",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * @param sessionId The value of sessionId as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setSessionId_NotLikeSearch(String sessionId,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(sessionId), getCValueSessionId(), "SESSION_ID",
                likeSearchOption);
    }

    protected void regSessionId(ConditionKey k, Object v) {
        regQ(k, v, getCValueSessionId(), "SESSION_ID");
    }

    abstract protected ConditionValue getCValueSessionId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * RULE_ID: {VARCHAR2(20)}
     * @param ruleId The value of ruleId as equal.
     */
    public void setRuleId_Equal(String ruleId) {
        doSetRuleId_Equal(fRES(ruleId));
    }

    protected void doSetRuleId_Equal(String ruleId) {
        regRuleId(CK_EQ, ruleId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param ruleId The value of ruleId as notEqual.
     */
    public void setRuleId_NotEqual(String ruleId) {
        doSetRuleId_NotEqual(fRES(ruleId));
    }

    protected void doSetRuleId_NotEqual(String ruleId) {
        regRuleId(CK_NES, ruleId);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param ruleId The value of ruleId as greaterThan.
     */
    public void setRuleId_GreaterThan(String ruleId) {
        regRuleId(CK_GT, fRES(ruleId));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param ruleId The value of ruleId as lessThan.
     */
    public void setRuleId_LessThan(String ruleId) {
        regRuleId(CK_LT, fRES(ruleId));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param ruleId The value of ruleId as greaterEqual.
     */
    public void setRuleId_GreaterEqual(String ruleId) {
        regRuleId(CK_GE, fRES(ruleId));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param ruleId The value of ruleId as lessEqual.
     */
    public void setRuleId_LessEqual(String ruleId) {
        regRuleId(CK_LE, fRES(ruleId));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param ruleIdList The collection of ruleId as inScope.
     */
    public void setRuleId_InScope(Collection<String> ruleIdList) {
        doSetRuleId_InScope(ruleIdList);
    }

    public void doSetRuleId_InScope(Collection<String> ruleIdList) {
        regINS(CK_INS, cTL(ruleIdList), getCValueRuleId(), "RULE_ID");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param ruleIdList The collection of ruleId as notInScope.
     */
    public void setRuleId_NotInScope(Collection<String> ruleIdList) {
        doSetRuleId_NotInScope(ruleIdList);
    }

    public void doSetRuleId_NotInScope(Collection<String> ruleIdList) {
        regINS(CK_NINS, cTL(ruleIdList), getCValueRuleId(), "RULE_ID");
    }

    /**
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param ruleId The value of ruleId as prefixSearch.
     */
    public void setRuleId_PrefixSearch(String ruleId) {
        setRuleId_LikeSearch(ruleId, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * @param ruleId The value of ruleId as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setRuleId_LikeSearch(String ruleId,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(ruleId), getCValueRuleId(), "RULE_ID",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * @param ruleId The value of ruleId as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setRuleId_NotLikeSearch(String ruleId,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(ruleId), getCValueRuleId(), "RULE_ID",
                likeSearchOption);
    }

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setRuleId_IsNull() {
        regRuleId(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setRuleId_IsNotNull() {
        regRuleId(CK_ISNN, DOBJ);
    }

    protected void regRuleId(ConditionKey k, Object v) {
        regQ(k, v, getCValueRuleId(), "RULE_ID");
    }

    abstract protected ConditionValue getCValueRuleId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {IX+, NotNull, VARCHAR2(4000)}
     * @param url The value of url as equal.
     */
    public void setUrl_Equal(String url) {
        doSetUrl_Equal(fRES(url));
    }

    protected void doSetUrl_Equal(String url) {
        regUrl(CK_EQ, url);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param url The value of url as notEqual.
     */
    public void setUrl_NotEqual(String url) {
        doSetUrl_NotEqual(fRES(url));
    }

    protected void doSetUrl_NotEqual(String url) {
        regUrl(CK_NES, url);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param url The value of url as greaterThan.
     */
    public void setUrl_GreaterThan(String url) {
        regUrl(CK_GT, fRES(url));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param url The value of url as lessThan.
     */
    public void setUrl_LessThan(String url) {
        regUrl(CK_LT, fRES(url));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param url The value of url as greaterEqual.
     */
    public void setUrl_GreaterEqual(String url) {
        regUrl(CK_GE, fRES(url));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param url The value of url as lessEqual.
     */
    public void setUrl_LessEqual(String url) {
        regUrl(CK_LE, fRES(url));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param urlList The collection of url as inScope.
     */
    public void setUrl_InScope(Collection<String> urlList) {
        doSetUrl_InScope(urlList);
    }

    public void doSetUrl_InScope(Collection<String> urlList) {
        regINS(CK_INS, cTL(urlList), getCValueUrl(), "URL");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param urlList The collection of url as notInScope.
     */
    public void setUrl_NotInScope(Collection<String> urlList) {
        doSetUrl_NotInScope(urlList);
    }

    public void doSetUrl_NotInScope(Collection<String> urlList) {
        regINS(CK_NINS, cTL(urlList), getCValueUrl(), "URL");
    }

    /**
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param url The value of url as prefixSearch.
     */
    public void setUrl_PrefixSearch(String url) {
        setUrl_LikeSearch(url, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * @param url The value of url as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setUrl_LikeSearch(String url, LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(url), getCValueUrl(), "URL", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * @param url The value of url as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setUrl_NotLikeSearch(String url,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(url), getCValueUrl(), "URL", likeSearchOption);
    }

    protected void regUrl(ConditionKey k, Object v) {
        regQ(k, v, getCValueUrl(), "URL");
    }

    abstract protected ConditionValue getCValueUrl();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * PARENT_URL: {VARCHAR2(4000)}
     * @param parentUrl The value of parentUrl as equal.
     */
    public void setParentUrl_Equal(String parentUrl) {
        doSetParentUrl_Equal(fRES(parentUrl));
    }

    protected void doSetParentUrl_Equal(String parentUrl) {
        regParentUrl(CK_EQ, parentUrl);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param parentUrl The value of parentUrl as notEqual.
     */
    public void setParentUrl_NotEqual(String parentUrl) {
        doSetParentUrl_NotEqual(fRES(parentUrl));
    }

    protected void doSetParentUrl_NotEqual(String parentUrl) {
        regParentUrl(CK_NES, parentUrl);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param parentUrl The value of parentUrl as greaterThan.
     */
    public void setParentUrl_GreaterThan(String parentUrl) {
        regParentUrl(CK_GT, fRES(parentUrl));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param parentUrl The value of parentUrl as lessThan.
     */
    public void setParentUrl_LessThan(String parentUrl) {
        regParentUrl(CK_LT, fRES(parentUrl));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param parentUrl The value of parentUrl as greaterEqual.
     */
    public void setParentUrl_GreaterEqual(String parentUrl) {
        regParentUrl(CK_GE, fRES(parentUrl));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param parentUrl The value of parentUrl as lessEqual.
     */
    public void setParentUrl_LessEqual(String parentUrl) {
        regParentUrl(CK_LE, fRES(parentUrl));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param parentUrlList The collection of parentUrl as inScope.
     */
    public void setParentUrl_InScope(Collection<String> parentUrlList) {
        doSetParentUrl_InScope(parentUrlList);
    }

    public void doSetParentUrl_InScope(Collection<String> parentUrlList) {
        regINS(CK_INS, cTL(parentUrlList), getCValueParentUrl(), "PARENT_URL");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param parentUrlList The collection of parentUrl as notInScope.
     */
    public void setParentUrl_NotInScope(Collection<String> parentUrlList) {
        doSetParentUrl_NotInScope(parentUrlList);
    }

    public void doSetParentUrl_NotInScope(Collection<String> parentUrlList) {
        regINS(CK_NINS, cTL(parentUrlList), getCValueParentUrl(), "PARENT_URL");
    }

    /**
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param parentUrl The value of parentUrl as prefixSearch.
     */
    public void setParentUrl_PrefixSearch(String parentUrl) {
        setParentUrl_LikeSearch(parentUrl, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * @param parentUrl The value of parentUrl as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setParentUrl_LikeSearch(String parentUrl,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(parentUrl), getCValueParentUrl(), "PARENT_URL",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * @param parentUrl The value of parentUrl as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setParentUrl_NotLikeSearch(String parentUrl,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(parentUrl), getCValueParentUrl(), "PARENT_URL",
                likeSearchOption);
    }

    /**
     * IsNull(is null). And OnlyOnceRegistered.
     */
    public void setParentUrl_IsNull() {
        regParentUrl(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull(is not null). And OnlyOnceRegistered.
     */
    public void setParentUrl_IsNotNull() {
        regParentUrl(CK_ISNN, DOBJ);
    }

    protected void regParentUrl(ConditionKey k, Object v) {
        regQ(k, v, getCValueParentUrl(), "PARENT_URL");
    }

    abstract protected ConditionValue getCValueParentUrl();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * STATUS: {NotNull, NUMBER(4)}
     * @param status The value of status as equal.
     */
    public void setStatus_Equal(Integer status) {
        doSetStatus_Equal(status);
    }

    protected void doSetStatus_Equal(Integer status) {
        regStatus(CK_EQ, status);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param status The value of status as notEqual.
     */
    public void setStatus_NotEqual(Integer status) {
        doSetStatus_NotEqual(status);
    }

    protected void doSetStatus_NotEqual(Integer status) {
        regStatus(CK_NES, status);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param status The value of status as greaterThan.
     */
    public void setStatus_GreaterThan(Integer status) {
        regStatus(CK_GT, status);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param status The value of status as lessThan.
     */
    public void setStatus_LessThan(Integer status) {
        regStatus(CK_LT, status);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param status The value of status as greaterEqual.
     */
    public void setStatus_GreaterEqual(Integer status) {
        regStatus(CK_GE, status);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param status The value of status as lessEqual.
     */
    public void setStatus_LessEqual(Integer status) {
        regStatus(CK_LE, status);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param statusList The collection of status as inScope.
     */
    public void setStatus_InScope(Collection<Integer> statusList) {
        doSetStatus_InScope(statusList);
    }

    protected void doSetStatus_InScope(Collection<Integer> statusList) {
        regINS(CK_INS, cTL(statusList), getCValueStatus(), "STATUS");
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param statusList The collection of status as notInScope.
     */
    public void setStatus_NotInScope(Collection<Integer> statusList) {
        doSetStatus_NotInScope(statusList);
    }

    protected void doSetStatus_NotInScope(Collection<Integer> statusList) {
        regINS(CK_NINS, cTL(statusList), getCValueStatus(), "STATUS");
    }

    protected void regStatus(ConditionKey k, Object v) {
        regQ(k, v, getCValueStatus(), "STATUS");
    }

    abstract protected ConditionValue getCValueStatus();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * HTTP_STATUS_CODE: {NotNull, NUMBER(4)}
     * @param httpStatusCode The value of httpStatusCode as equal.
     */
    public void setHttpStatusCode_Equal(Integer httpStatusCode) {
        doSetHttpStatusCode_Equal(httpStatusCode);
    }

    protected void doSetHttpStatusCode_Equal(Integer httpStatusCode) {
        regHttpStatusCode(CK_EQ, httpStatusCode);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param httpStatusCode The value of httpStatusCode as notEqual.
     */
    public void setHttpStatusCode_NotEqual(Integer httpStatusCode) {
        doSetHttpStatusCode_NotEqual(httpStatusCode);
    }

    protected void doSetHttpStatusCode_NotEqual(Integer httpStatusCode) {
        regHttpStatusCode(CK_NES, httpStatusCode);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param httpStatusCode The value of httpStatusCode as greaterThan.
     */
    public void setHttpStatusCode_GreaterThan(Integer httpStatusCode) {
        regHttpStatusCode(CK_GT, httpStatusCode);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param httpStatusCode The value of httpStatusCode as lessThan.
     */
    public void setHttpStatusCode_LessThan(Integer httpStatusCode) {
        regHttpStatusCode(CK_LT, httpStatusCode);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param httpStatusCode The value of httpStatusCode as greaterEqual.
     */
    public void setHttpStatusCode_GreaterEqual(Integer httpStatusCode) {
        regHttpStatusCode(CK_GE, httpStatusCode);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param httpStatusCode The value of httpStatusCode as lessEqual.
     */
    public void setHttpStatusCode_LessEqual(Integer httpStatusCode) {
        regHttpStatusCode(CK_LE, httpStatusCode);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param httpStatusCodeList The collection of httpStatusCode as inScope.
     */
    public void setHttpStatusCode_InScope(Collection<Integer> httpStatusCodeList) {
        doSetHttpStatusCode_InScope(httpStatusCodeList);
    }

    protected void doSetHttpStatusCode_InScope(
            Collection<Integer> httpStatusCodeList) {
        regINS(CK_INS, cTL(httpStatusCodeList), getCValueHttpStatusCode(),
                "HTTP_STATUS_CODE");
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param httpStatusCodeList The collection of httpStatusCode as notInScope.
     */
    public void setHttpStatusCode_NotInScope(
            Collection<Integer> httpStatusCodeList) {
        doSetHttpStatusCode_NotInScope(httpStatusCodeList);
    }

    protected void doSetHttpStatusCode_NotInScope(
            Collection<Integer> httpStatusCodeList) {
        regINS(CK_NINS, cTL(httpStatusCodeList), getCValueHttpStatusCode(),
                "HTTP_STATUS_CODE");
    }

    protected void regHttpStatusCode(ConditionKey k, Object v) {
        regQ(k, v, getCValueHttpStatusCode(), "HTTP_STATUS_CODE");
    }

    abstract protected ConditionValue getCValueHttpStatusCode();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * METHOD: {NotNull, VARCHAR2(10)}
     * @param method The value of method as equal.
     */
    public void setMethod_Equal(String method) {
        doSetMethod_Equal(fRES(method));
    }

    protected void doSetMethod_Equal(String method) {
        regMethod(CK_EQ, method);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param method The value of method as notEqual.
     */
    public void setMethod_NotEqual(String method) {
        doSetMethod_NotEqual(fRES(method));
    }

    protected void doSetMethod_NotEqual(String method) {
        regMethod(CK_NES, method);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param method The value of method as greaterThan.
     */
    public void setMethod_GreaterThan(String method) {
        regMethod(CK_GT, fRES(method));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param method The value of method as lessThan.
     */
    public void setMethod_LessThan(String method) {
        regMethod(CK_LT, fRES(method));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param method The value of method as greaterEqual.
     */
    public void setMethod_GreaterEqual(String method) {
        regMethod(CK_GE, fRES(method));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param method The value of method as lessEqual.
     */
    public void setMethod_LessEqual(String method) {
        regMethod(CK_LE, fRES(method));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param methodList The collection of method as inScope.
     */
    public void setMethod_InScope(Collection<String> methodList) {
        doSetMethod_InScope(methodList);
    }

    public void doSetMethod_InScope(Collection<String> methodList) {
        regINS(CK_INS, cTL(methodList), getCValueMethod(), "METHOD");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param methodList The collection of method as notInScope.
     */
    public void setMethod_NotInScope(Collection<String> methodList) {
        doSetMethod_NotInScope(methodList);
    }

    public void doSetMethod_NotInScope(Collection<String> methodList) {
        regINS(CK_NINS, cTL(methodList), getCValueMethod(), "METHOD");
    }

    /**
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param method The value of method as prefixSearch.
     */
    public void setMethod_PrefixSearch(String method) {
        setMethod_LikeSearch(method, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * @param method The value of method as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setMethod_LikeSearch(String method,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(method), getCValueMethod(), "METHOD",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * @param method The value of method as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setMethod_NotLikeSearch(String method,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(method), getCValueMethod(), "METHOD",
                likeSearchOption);
    }

    protected void regMethod(ConditionKey k, Object v) {
        regQ(k, v, getCValueMethod(), "METHOD");
    }

    abstract protected ConditionValue getCValueMethod();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * MIME_TYPE: {NotNull, VARCHAR2(100)}
     * @param mimeType The value of mimeType as equal.
     */
    public void setMimeType_Equal(String mimeType) {
        doSetMimeType_Equal(fRES(mimeType));
    }

    protected void doSetMimeType_Equal(String mimeType) {
        regMimeType(CK_EQ, mimeType);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param mimeType The value of mimeType as notEqual.
     */
    public void setMimeType_NotEqual(String mimeType) {
        doSetMimeType_NotEqual(fRES(mimeType));
    }

    protected void doSetMimeType_NotEqual(String mimeType) {
        regMimeType(CK_NES, mimeType);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param mimeType The value of mimeType as greaterThan.
     */
    public void setMimeType_GreaterThan(String mimeType) {
        regMimeType(CK_GT, fRES(mimeType));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param mimeType The value of mimeType as lessThan.
     */
    public void setMimeType_LessThan(String mimeType) {
        regMimeType(CK_LT, fRES(mimeType));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param mimeType The value of mimeType as greaterEqual.
     */
    public void setMimeType_GreaterEqual(String mimeType) {
        regMimeType(CK_GE, fRES(mimeType));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param mimeType The value of mimeType as lessEqual.
     */
    public void setMimeType_LessEqual(String mimeType) {
        regMimeType(CK_LE, fRES(mimeType));
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param mimeTypeList The collection of mimeType as inScope.
     */
    public void setMimeType_InScope(Collection<String> mimeTypeList) {
        doSetMimeType_InScope(mimeTypeList);
    }

    public void doSetMimeType_InScope(Collection<String> mimeTypeList) {
        regINS(CK_INS, cTL(mimeTypeList), getCValueMimeType(), "MIME_TYPE");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param mimeTypeList The collection of mimeType as notInScope.
     */
    public void setMimeType_NotInScope(Collection<String> mimeTypeList) {
        doSetMimeType_NotInScope(mimeTypeList);
    }

    public void doSetMimeType_NotInScope(Collection<String> mimeTypeList) {
        regINS(CK_NINS, cTL(mimeTypeList), getCValueMimeType(), "MIME_TYPE");
    }

    /**
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param mimeType The value of mimeType as prefixSearch.
     */
    public void setMimeType_PrefixSearch(String mimeType) {
        setMimeType_LikeSearch(mimeType, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * @param mimeType The value of mimeType as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setMimeType_LikeSearch(String mimeType,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(mimeType), getCValueMimeType(), "MIME_TYPE",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered.
     * @param mimeType The value of mimeType as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setMimeType_NotLikeSearch(String mimeType,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(mimeType), getCValueMimeType(), "MIME_TYPE",
                likeSearchOption);
    }

    protected void regMimeType(ConditionKey k, Object v) {
        regQ(k, v, getCValueMimeType(), "MIME_TYPE");
    }

    abstract protected ConditionValue getCValueMimeType();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * CONTENT_LENGTH: {NotNull, NUMBER(12)}
     * @param contentLength The value of contentLength as equal.
     */
    public void setContentLength_Equal(Long contentLength) {
        doSetContentLength_Equal(contentLength);
    }

    protected void doSetContentLength_Equal(Long contentLength) {
        regContentLength(CK_EQ, contentLength);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param contentLength The value of contentLength as notEqual.
     */
    public void setContentLength_NotEqual(Long contentLength) {
        doSetContentLength_NotEqual(contentLength);
    }

    protected void doSetContentLength_NotEqual(Long contentLength) {
        regContentLength(CK_NES, contentLength);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param contentLength The value of contentLength as greaterThan.
     */
    public void setContentLength_GreaterThan(Long contentLength) {
        regContentLength(CK_GT, contentLength);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param contentLength The value of contentLength as lessThan.
     */
    public void setContentLength_LessThan(Long contentLength) {
        regContentLength(CK_LT, contentLength);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param contentLength The value of contentLength as greaterEqual.
     */
    public void setContentLength_GreaterEqual(Long contentLength) {
        regContentLength(CK_GE, contentLength);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param contentLength The value of contentLength as lessEqual.
     */
    public void setContentLength_LessEqual(Long contentLength) {
        regContentLength(CK_LE, contentLength);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param contentLengthList The collection of contentLength as inScope.
     */
    public void setContentLength_InScope(Collection<Long> contentLengthList) {
        doSetContentLength_InScope(contentLengthList);
    }

    protected void doSetContentLength_InScope(Collection<Long> contentLengthList) {
        regINS(CK_INS, cTL(contentLengthList), getCValueContentLength(),
                "CONTENT_LENGTH");
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param contentLengthList The collection of contentLength as notInScope.
     */
    public void setContentLength_NotInScope(Collection<Long> contentLengthList) {
        doSetContentLength_NotInScope(contentLengthList);
    }

    protected void doSetContentLength_NotInScope(
            Collection<Long> contentLengthList) {
        regINS(CK_NINS, cTL(contentLengthList), getCValueContentLength(),
                "CONTENT_LENGTH");
    }

    protected void regContentLength(ConditionKey k, Object v) {
        regQ(k, v, getCValueContentLength(), "CONTENT_LENGTH");
    }

    abstract protected ConditionValue getCValueContentLength();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * EXECUTION_TIME: {NotNull, NUMBER(9)}
     * @param executionTime The value of executionTime as equal.
     */
    public void setExecutionTime_Equal(Integer executionTime) {
        doSetExecutionTime_Equal(executionTime);
    }

    protected void doSetExecutionTime_Equal(Integer executionTime) {
        regExecutionTime(CK_EQ, executionTime);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param executionTime The value of executionTime as notEqual.
     */
    public void setExecutionTime_NotEqual(Integer executionTime) {
        doSetExecutionTime_NotEqual(executionTime);
    }

    protected void doSetExecutionTime_NotEqual(Integer executionTime) {
        regExecutionTime(CK_NES, executionTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param executionTime The value of executionTime as greaterThan.
     */
    public void setExecutionTime_GreaterThan(Integer executionTime) {
        regExecutionTime(CK_GT, executionTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param executionTime The value of executionTime as lessThan.
     */
    public void setExecutionTime_LessThan(Integer executionTime) {
        regExecutionTime(CK_LT, executionTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param executionTime The value of executionTime as greaterEqual.
     */
    public void setExecutionTime_GreaterEqual(Integer executionTime) {
        regExecutionTime(CK_GE, executionTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param executionTime The value of executionTime as lessEqual.
     */
    public void setExecutionTime_LessEqual(Integer executionTime) {
        regExecutionTime(CK_LE, executionTime);
    }

    /**
     * InScope(in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param executionTimeList The collection of executionTime as inScope.
     */
    public void setExecutionTime_InScope(Collection<Integer> executionTimeList) {
        doSetExecutionTime_InScope(executionTimeList);
    }

    protected void doSetExecutionTime_InScope(
            Collection<Integer> executionTimeList) {
        regINS(CK_INS, cTL(executionTimeList), getCValueExecutionTime(),
                "EXECUTION_TIME");
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param executionTimeList The collection of executionTime as notInScope.
     */
    public void setExecutionTime_NotInScope(
            Collection<Integer> executionTimeList) {
        doSetExecutionTime_NotInScope(executionTimeList);
    }

    protected void doSetExecutionTime_NotInScope(
            Collection<Integer> executionTimeList) {
        regINS(CK_NINS, cTL(executionTimeList), getCValueExecutionTime(),
                "EXECUTION_TIME");
    }

    protected void regExecutionTime(ConditionKey k, Object v) {
        regQ(k, v, getCValueExecutionTime(), "EXECUTION_TIME");
    }

    abstract protected ConditionValue getCValueExecutionTime();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * LAST_MODIFIED: {NotNull, TIMESTAMP(6)(11, 6)}
     * @param lastModified The value of lastModified as equal.
     */
    public void setLastModified_Equal(java.sql.Timestamp lastModified) {
        regLastModified(CK_EQ, lastModified);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param lastModified The value of lastModified as greaterThan.
     */
    public void setLastModified_GreaterThan(java.sql.Timestamp lastModified) {
        regLastModified(CK_GT, lastModified);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param lastModified The value of lastModified as lessThan.
     */
    public void setLastModified_LessThan(java.sql.Timestamp lastModified) {
        regLastModified(CK_LT, lastModified);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param lastModified The value of lastModified as greaterEqual.
     */
    public void setLastModified_GreaterEqual(java.sql.Timestamp lastModified) {
        regLastModified(CK_GE, lastModified);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param lastModified The value of lastModified as lessEqual.
     */
    public void setLastModified_LessEqual(java.sql.Timestamp lastModified) {
        regLastModified(CK_LE, lastModified);
    }

    /**
     * FromTo with various options. (versatile) <br />
     * {(default) fromDatetime &lt;= column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered.
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of lastModified. (NullAllowed)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of lastModified. (NullAllowed)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setLastModified_FromTo(java.util.Date fromDatetime,
            java.util.Date toDatetime, FromToOption fromToOption) {
        regFTQ((fromDatetime != null ? new java.sql.Timestamp(
                fromDatetime.getTime()) : null),
                (toDatetime != null ? new java.sql.Timestamp(toDatetime
                        .getTime()) : null), getCValueLastModified(),
                "LAST_MODIFIED", fromToOption);
    }

    /**
     * DateFromTo. (Date means yyyy/MM/dd) <br />
     * {fromDate &lt;= column &lt; toDate + 1 day} <br />
     * And NullIgnored, OnlyOnceRegistered.
     * <pre>
     * ex) from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     *     --&gt; column &gt;= '2007/04/10 00:00:00'
     *     and column <span style="color: #FD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of lastModified. (NullAllowed)
     * @param toDate The to-date(yyyy/MM/dd) of lastModified. (NullAllowed)
     */
    public void setLastModified_DateFromTo(java.util.Date fromDate,
            java.util.Date toDate) {
        setLastModified_FromTo(fromDate, toDate, new DateFromToOption());
    }

    protected void regLastModified(ConditionKey k, Object v) {
        regQ(k, v, getCValueLastModified(), "LAST_MODIFIED");
    }

    abstract protected ConditionValue getCValueLastModified();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATE_TIME: {IX+, NotNull, TIMESTAMP(6)(11, 6)}
     * @param createTime The value of createTime as equal.
     */
    public void setCreateTime_Equal(java.sql.Timestamp createTime) {
        regCreateTime(CK_EQ, createTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param createTime The value of createTime as greaterThan.
     */
    public void setCreateTime_GreaterThan(java.sql.Timestamp createTime) {
        regCreateTime(CK_GT, createTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered.
     * @param createTime The value of createTime as lessThan.
     */
    public void setCreateTime_LessThan(java.sql.Timestamp createTime) {
        regCreateTime(CK_LT, createTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered.
     * @param createTime The value of createTime as greaterEqual.
     */
    public void setCreateTime_GreaterEqual(java.sql.Timestamp createTime) {
        regCreateTime(CK_GE, createTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered.
     * @param createTime The value of createTime as lessEqual.
     */
    public void setCreateTime_LessEqual(java.sql.Timestamp createTime) {
        regCreateTime(CK_LE, createTime);
    }

    /**
     * FromTo with various options. (versatile) <br />
     * {(default) fromDatetime &lt;= column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered.
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of createTime. (NullAllowed)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of createTime. (NullAllowed)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setCreateTime_FromTo(java.util.Date fromDatetime,
            java.util.Date toDatetime, FromToOption fromToOption) {
        regFTQ((fromDatetime != null ? new java.sql.Timestamp(
                fromDatetime.getTime()) : null),
                (toDatetime != null ? new java.sql.Timestamp(toDatetime
                        .getTime()) : null), getCValueCreateTime(),
                "CREATE_TIME", fromToOption);
    }

    /**
     * DateFromTo. (Date means yyyy/MM/dd) <br />
     * {fromDate &lt;= column &lt; toDate + 1 day} <br />
     * And NullIgnored, OnlyOnceRegistered.
     * <pre>
     * ex) from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     *     --&gt; column &gt;= '2007/04/10 00:00:00'
     *     and column <span style="color: #FD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of createTime. (NullAllowed)
     * @param toDate The to-date(yyyy/MM/dd) of createTime. (NullAllowed)
     */
    public void setCreateTime_DateFromTo(java.util.Date fromDate,
            java.util.Date toDate) {
        setCreateTime_FromTo(fromDate, toDate, new DateFromToOption());
    }

    protected void regCreateTime(ConditionKey k, Object v) {
        regQ(k, v, getCValueCreateTime(), "CREATE_TIME");
    }

    abstract protected ConditionValue getCValueCreateTime();

    // ===================================================================================
    //                                                                    Scalar Condition
    //                                                                    ================
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_Equal()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ.getOperand());
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessThan()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE.getOperand());
    }

    protected HpSSQFunction<AccessResultCB> xcreateSSQFunction(
            final String operand) {
        return new HpSSQFunction<AccessResultCB>(
                new HpSSQSetupper<AccessResultCB>() {
                    public void setup(String function,
                            SubQuery<AccessResultCB> subQuery) {
                        xscalarCondition(function, subQuery, operand);
                    }
                });
    }

    protected void xscalarCondition(String function,
            SubQuery<AccessResultCB> subQuery, String operand) {
        assertObjectNotNull("subQuery<AccessResultCB>", subQuery);
        AccessResultCB cb = new AccessResultCB();
        cb.xsetupForScalarCondition(this);
        subQuery.query(cb);
        String subQueryPropertyName = keepScalarCondition(cb.query()); // for saving query-value.
        registerScalarCondition(function, cb.query(), subQueryPropertyName,
                operand);
    }

    public abstract String keepScalarCondition(AccessResultCQ subQuery);

    // ===================================================================================
    //                                                                      Myself InScope
    //                                                                      ==============
    /**
     * Myself InScope (SubQuery). {mainly for CLOB and Union}
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfInScope(SubQuery<AccessResultCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultCB>", subQuery);
        AccessResultCB cb = new AccessResultCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        String subQueryPropertyName = keepMyselfInScopeRelation(cb.query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "ID", subQueryPropertyName);
    }

    public abstract String keepMyselfInScopeRelation(AccessResultCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
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
