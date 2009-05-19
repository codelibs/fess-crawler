package org.seasar.robot.db.cbean.cq.bs;

import java.util.Collection;

import org.seasar.dbflute.cbean.AbstractConditionQuery;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.DateFromToOption;
import org.seasar.dbflute.cbean.coption.FromToOption;
import org.seasar.dbflute.cbean.coption.LikeSearchOption;
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
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsAccessResultCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

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
    protected DBMetaProvider getDBMetaProvider() {
        return _dbmetaProvider;
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "ACCESS_RESULT";
    }

    public String getTableSqlName() {
        return "ACCESS_RESULT";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {PK : ID : NotNull : BIGINT}
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

    public void inScopeAccessResultDataAsOne(
            SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForInScopeSubQuery();
        subQuery.query(cb);
        String subQueryPropertyName = keepId_InScopeSubQuery_AccessResultDataAsOne(cb
                .query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "ID", "ID", subQueryPropertyName);
    }

    public abstract String keepId_InScopeSubQuery_AccessResultDataAsOne(
            AccessResultDataCQ subQuery);

    public void notInScopeAccessResultDataAsOne(
            SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForInScopeSubQuery();
        subQuery.query(cb);
        String subQueryPropertyName = keepId_NotInScopeSubQuery_AccessResultDataAsOne(cb
                .query()); // for saving query-value.
        registerNotInScopeSubQuery(cb.query(), "ID", "ID", subQueryPropertyName);
    }

    public abstract String keepId_NotInScopeSubQuery_AccessResultDataAsOne(
            AccessResultDataCQ subQuery);

    /**
     * Set up 'exists' sub-query. {exists (select ID from ACCESS_RESULT_DATA where ...)}
     * @param subQuery The sub-query of Id_ExistsSubQuery_AccessResultDataAsOne for 'exists'. (NotNull)
     */
    public void existsAccessResultDataAsOne(
            SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForExistsSubQuery();
        subQuery.query(cb);
        String subQueryPropertyName = keepId_ExistsSubQuery_AccessResultDataAsOne(cb
                .query()); // for saving query-value.
        registerExistsSubQuery(cb.query(), "ID", "ID", subQueryPropertyName);
    }

    public abstract String keepId_ExistsSubQuery_AccessResultDataAsOne(
            AccessResultDataCQ subQuery);

    /**
     * Set up 'not exists' sub-query. {not exists (select ID from ACCESS_RESULT_DATA where ...)}
     * @param subQuery The sub-query of Id_NotExistsSubQuery_AccessResultDataAsOne for 'not exists'. (NotNull)
     */
    public void notExistsAccessResultDataAsOne(
            SubQuery<AccessResultDataCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultDataCB>", subQuery);
        AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForExistsSubQuery();
        subQuery.query(cb);
        String subQueryPropertyName = keepId_NotExistsSubQuery_AccessResultDataAsOne(cb
                .query()); // for saving query-value.
        registerNotExistsSubQuery(cb.query(), "ID", "ID", subQueryPropertyName);
    }

    public abstract String keepId_NotExistsSubQuery_AccessResultDataAsOne(
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
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : VARCHAR(20)}
     * @param sessionId The value of sessionId as equal.
     */
    public void setSessionId_Equal(String sessionId) {
        regSessionId(CK_EQ, fRES(sessionId));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param sessionId The value of sessionId as notEqual.
     */
    public void setSessionId_NotEqual(String sessionId) {
        regSessionId(CK_NE, fRES(sessionId));
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
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param sessionId The value of sessionId as prefixSearch.
     */
    public void setSessionId_PrefixSearch(String sessionId) {
        setSessionId_LikeSearch(sessionId, cLSOP());
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param sessionIdList The collection of sessionId as inScope.
     */
    public void setSessionId_InScope(Collection<String> sessionIdList) {
        regINS(CK_INS, cTL(sessionIdList), getCValueSessionId(), "SESSION_ID");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param sessionIdList The collection of sessionId as notInScope.
     */
    public void setSessionId_NotInScope(Collection<String> sessionIdList) {
        regINS(CK_NINS, cTL(sessionIdList), getCValueSessionId(), "SESSION_ID");
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param sessionId The value of sessionId as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setSessionId_LikeSearch(String sessionId,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(sessionId), getCValueSessionId(), "SESSION_ID",
                likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
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
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {VARCHAR(20)}
     * @param ruleId The value of ruleId as equal.
     */
    public void setRuleId_Equal(String ruleId) {
        regRuleId(CK_EQ, fRES(ruleId));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param ruleId The value of ruleId as notEqual.
     */
    public void setRuleId_NotEqual(String ruleId) {
        regRuleId(CK_NE, fRES(ruleId));
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
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param ruleId The value of ruleId as prefixSearch.
     */
    public void setRuleId_PrefixSearch(String ruleId) {
        setRuleId_LikeSearch(ruleId, cLSOP());
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param ruleIdList The collection of ruleId as inScope.
     */
    public void setRuleId_InScope(Collection<String> ruleIdList) {
        regINS(CK_INS, cTL(ruleIdList), getCValueRuleId(), "RULE_ID");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param ruleIdList The collection of ruleId as notInScope.
     */
    public void setRuleId_NotInScope(Collection<String> ruleIdList) {
        regINS(CK_NINS, cTL(ruleIdList), getCValueRuleId(), "RULE_ID");
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param ruleId The value of ruleId as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setRuleId_LikeSearch(String ruleId,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(ruleId), getCValueRuleId(), "RULE_ID",
                likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
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
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : VARCHAR(65536)}
     * @param url The value of url as equal.
     */
    public void setUrl_Equal(String url) {
        regUrl(CK_EQ, fRES(url));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param url The value of url as notEqual.
     */
    public void setUrl_NotEqual(String url) {
        regUrl(CK_NE, fRES(url));
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
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param url The value of url as prefixSearch.
     */
    public void setUrl_PrefixSearch(String url) {
        setUrl_LikeSearch(url, cLSOP());
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param urlList The collection of url as inScope.
     */
    public void setUrl_InScope(Collection<String> urlList) {
        regINS(CK_INS, cTL(urlList), getCValueUrl(), "URL");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param urlList The collection of url as notInScope.
     */
    public void setUrl_NotInScope(Collection<String> urlList) {
        regINS(CK_NINS, cTL(urlList), getCValueUrl(), "URL");
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param url The value of url as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setUrl_LikeSearch(String url, LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(url), getCValueUrl(), "URL", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
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
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {VARCHAR(65536)}
     * @param parentUrl The value of parentUrl as equal.
     */
    public void setParentUrl_Equal(String parentUrl) {
        regParentUrl(CK_EQ, fRES(parentUrl));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param parentUrl The value of parentUrl as notEqual.
     */
    public void setParentUrl_NotEqual(String parentUrl) {
        regParentUrl(CK_NE, fRES(parentUrl));
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
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param parentUrl The value of parentUrl as prefixSearch.
     */
    public void setParentUrl_PrefixSearch(String parentUrl) {
        setParentUrl_LikeSearch(parentUrl, cLSOP());
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param parentUrlList The collection of parentUrl as inScope.
     */
    public void setParentUrl_InScope(Collection<String> parentUrlList) {
        regINS(CK_INS, cTL(parentUrlList), getCValueParentUrl(), "PARENT_URL");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param parentUrlList The collection of parentUrl as notInScope.
     */
    public void setParentUrl_NotInScope(Collection<String> parentUrlList) {
        regINS(CK_NINS, cTL(parentUrlList), getCValueParentUrl(), "PARENT_URL");
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param parentUrl The value of parentUrl as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setParentUrl_LikeSearch(String parentUrl,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(parentUrl), getCValueParentUrl(), "PARENT_URL",
                likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
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
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : INTEGER}
     * @param status The value of status as equal.
     */
    public void setStatus_Equal(Integer status) {
        regStatus(CK_EQ, status);
    }

    /**
     * NotEqual(!=). And NullIgnored, OnlyOnceRegistered.
     * @param status The value of status as notEqual.
     */
    public void setStatus_NotEqual(Integer status) {
        regStatus(CK_NE, status);
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
        regINS(CK_INS, cTL(statusList), getCValueStatus(), "STATUS");
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param statusList The collection of status as notInScope.
     */
    public void setStatus_NotInScope(Collection<Integer> statusList) {
        regINS(CK_NINS, cTL(statusList), getCValueStatus(), "STATUS");
    }

    protected void regStatus(ConditionKey k, Object v) {
        regQ(k, v, getCValueStatus(), "STATUS");
    }

    abstract protected ConditionValue getCValueStatus();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : INTEGER}
     * @param httpStatusCode The value of httpStatusCode as equal.
     */
    public void setHttpStatusCode_Equal(Integer httpStatusCode) {
        regHttpStatusCode(CK_EQ, httpStatusCode);
    }

    /**
     * NotEqual(!=). And NullIgnored, OnlyOnceRegistered.
     * @param httpStatusCode The value of httpStatusCode as notEqual.
     */
    public void setHttpStatusCode_NotEqual(Integer httpStatusCode) {
        regHttpStatusCode(CK_NE, httpStatusCode);
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
        regINS(CK_INS, cTL(httpStatusCodeList), getCValueHttpStatusCode(),
                "HTTP_STATUS_CODE");
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param httpStatusCodeList The collection of httpStatusCode as notInScope.
     */
    public void setHttpStatusCode_NotInScope(
            Collection<Integer> httpStatusCodeList) {
        regINS(CK_NINS, cTL(httpStatusCodeList), getCValueHttpStatusCode(),
                "HTTP_STATUS_CODE");
    }

    protected void regHttpStatusCode(ConditionKey k, Object v) {
        regQ(k, v, getCValueHttpStatusCode(), "HTTP_STATUS_CODE");
    }

    abstract protected ConditionValue getCValueHttpStatusCode();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : VARCHAR(10)}
     * @param method The value of method as equal.
     */
    public void setMethod_Equal(String method) {
        regMethod(CK_EQ, fRES(method));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param method The value of method as notEqual.
     */
    public void setMethod_NotEqual(String method) {
        regMethod(CK_NE, fRES(method));
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
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param method The value of method as prefixSearch.
     */
    public void setMethod_PrefixSearch(String method) {
        setMethod_LikeSearch(method, cLSOP());
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param methodList The collection of method as inScope.
     */
    public void setMethod_InScope(Collection<String> methodList) {
        regINS(CK_INS, cTL(methodList), getCValueMethod(), "METHOD");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param methodList The collection of method as notInScope.
     */
    public void setMethod_NotInScope(Collection<String> methodList) {
        regINS(CK_NINS, cTL(methodList), getCValueMethod(), "METHOD");
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param method The value of method as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setMethod_LikeSearch(String method,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(method), getCValueMethod(), "METHOD",
                likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
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
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {NotNull : VARCHAR(100)}
     * @param mimeType The value of mimeType as equal.
     */
    public void setMimeType_Equal(String mimeType) {
        regMimeType(CK_EQ, fRES(mimeType));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param mimeType The value of mimeType as notEqual.
     */
    public void setMimeType_NotEqual(String mimeType) {
        regMimeType(CK_NE, fRES(mimeType));
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
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param mimeType The value of mimeType as prefixSearch.
     */
    public void setMimeType_PrefixSearch(String mimeType) {
        setMimeType_LikeSearch(mimeType, cLSOP());
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param mimeTypeList The collection of mimeType as inScope.
     */
    public void setMimeType_InScope(Collection<String> mimeTypeList) {
        regINS(CK_INS, cTL(mimeTypeList), getCValueMimeType(), "MIME_TYPE");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param mimeTypeList The collection of mimeType as notInScope.
     */
    public void setMimeType_NotInScope(Collection<String> mimeTypeList) {
        regINS(CK_NINS, cTL(mimeTypeList), getCValueMimeType(), "MIME_TYPE");
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param mimeType The value of mimeType as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setMimeType_LikeSearch(String mimeType,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(mimeType), getCValueMimeType(), "MIME_TYPE",
                likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
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
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : INTEGER}
     * @param executionTime The value of executionTime as equal.
     */
    public void setExecutionTime_Equal(Integer executionTime) {
        regExecutionTime(CK_EQ, executionTime);
    }

    /**
     * NotEqual(!=). And NullIgnored, OnlyOnceRegistered.
     * @param executionTime The value of executionTime as notEqual.
     */
    public void setExecutionTime_NotEqual(Integer executionTime) {
        regExecutionTime(CK_NE, executionTime);
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
        regINS(CK_INS, cTL(executionTimeList), getCValueExecutionTime(),
                "EXECUTION_TIME");
    }

    /**
     * NotInScope(not in (1, 2)). And NullIgnored, NullElementIgnored, SeveralRegistered.
     * @param executionTimeList The collection of executionTime as notInScope.
     */
    public void setExecutionTime_NotInScope(
            Collection<Integer> executionTimeList) {
        regINS(CK_NINS, cTL(executionTimeList), getCValueExecutionTime(),
                "EXECUTION_TIME");
    }

    protected void regExecutionTime(ConditionKey k, Object v) {
        regQ(k, v, getCValueExecutionTime(), "EXECUTION_TIME");
    }

    abstract protected ConditionValue getCValueExecutionTime();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {NotNull : TIMESTAMP}
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
     * LessThan(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param createTime The value of createTime as lessThan.
     */
    public void setCreateTime_LessThan(java.sql.Timestamp createTime) {
        regCreateTime(CK_LT, createTime);
    }

    /**
     * GreaterEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param createTime The value of createTime as greaterEqual.
     */
    public void setCreateTime_GreaterEqual(java.sql.Timestamp createTime) {
        regCreateTime(CK_GE, createTime);
    }

    /**
     * LessEqual(&gt;). And NullIgnored, OnlyOnceRegistered.
     * @param createTime The value of createTime as lessEqual.
     */
    public void setCreateTime_LessEqual(java.sql.Timestamp createTime) {
        regCreateTime(CK_LE, createTime);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt;= $toDate). And NullIgnored, OnlyOnceRegistered. {NotNull : TIMESTAMP}
     * @param fromDate The from-date of createTime. (Nullable)
     * @param toDate The to-date of createTime. (Nullable)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setCreateTime_FromTo(java.util.Date fromDate,
            java.util.Date toDate, FromToOption fromToOption) {
        regFTQ((fromDate != null ? new java.sql.Timestamp(fromDate.getTime())
                : null), (toDate != null ? new java.sql.Timestamp(toDate
                .getTime()) : null), getCValueCreateTime(), "CREATE_TIME",
                fromToOption);
    }

    /**
     * FromTo($fromDate &lt;= COLUMN_NAME &lt; $toDate + 1). And NullIgnored, OnlyOnceRegistered. {NotNull : TIMESTAMP}
     * @param fromDate The from-date of createTime. (Nullable)
     * @param toDate The to-date of createTime. (Nullable)
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
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public SSQFunction<AccessResultCB> scalar_Equal() {
        return xcreateSSQFunction("=");
    }

    public SSQFunction<AccessResultCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(">=");
    }

    public SSQFunction<AccessResultCB> scalar_GreaterThan() {
        return xcreateSSQFunction(">");
    }

    public SSQFunction<AccessResultCB> scalar_LessEqual() {
        return xcreateSSQFunction("<=");
    }

    public SSQFunction<AccessResultCB> scalar_LessThan() {
        return xcreateSSQFunction("<");
    }

    protected SSQFunction<AccessResultCB> xcreateSSQFunction(
            final String operand) {
        return new SSQFunction<AccessResultCB>(
                new SSQSetupper<AccessResultCB>() {
                    public void setup(String function,
                            SubQuery<AccessResultCB> subQuery) {
                        xscalarSubQuery(function, subQuery, operand);
                    }
                });
    }

    protected void xscalarSubQuery(String function,
            SubQuery<AccessResultCB> subQuery, String operand) {
        assertObjectNotNull("subQuery<AccessResultCB>", subQuery);
        AccessResultCB cb = new AccessResultCB();
        cb.xsetupForScalarSubQuery();
        subQuery.query(cb);
        String subQueryPropertyName = keepScalarSubQuery(cb.query()); // for saving query-value.
        registerScalarSubQuery(function, cb.query(), subQueryPropertyName,
                operand);
    }

    public abstract String keepScalarSubQuery(AccessResultCQ subQuery);

    // ===================================================================================
    //                                                             MySelf InScope SubQuery
    //                                                             =======================
    /**
     * Myself InScope SubQuery. {mainly for CLOB and Union}
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfInScope(SubQuery<AccessResultCB> subQuery) {
        assertObjectNotNull("subQuery<AccessResultCB>", subQuery);
        AccessResultCB cb = new AccessResultCB();
        cb.xsetupForInScopeSubQuery();
        subQuery.query(cb);
        String subQueryPropertyName = keepMyselfInScopeSubQuery(cb.query()); // for saving query-value.
        registerInScopeSubQuery(cb.query(), "ID", "ID", subQueryPropertyName);
    }

    public abstract String keepMyselfInScopeSubQuery(AccessResultCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    String xCB() {
        return AccessResultCB.class.getName();
    }

    String xCQ() {
        return AccessResultCQ.class.getName();
    }

    String xLSO() {
        return LikeSearchOption.class.getName();
    }
}
