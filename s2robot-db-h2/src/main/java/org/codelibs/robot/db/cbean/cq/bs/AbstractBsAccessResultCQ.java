package org.codelibs.robot.db.cbean.cq.bs;

import java.util.Collection;
import java.util.Date;

import org.codelibs.robot.db.allcommon.DBMetaInstanceHandler;
import org.codelibs.robot.db.cbean.AccessResultCB;
import org.codelibs.robot.db.cbean.cq.AccessResultCQ;
import org.dbflute.cbean.AbstractConditionQuery;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.ConditionQuery;
import org.dbflute.cbean.chelper.HpQDRFunction;
import org.dbflute.cbean.chelper.HpSSQFunction;
import org.dbflute.cbean.chelper.HpSSQOption;
import org.dbflute.cbean.chelper.HpSSQSetupper;
import org.dbflute.cbean.ckey.ConditionKey;
import org.dbflute.cbean.coption.ConditionOptionCall;
import org.dbflute.cbean.coption.DerivedReferrerOption;
import org.dbflute.cbean.coption.LikeSearchOption;
import org.dbflute.cbean.coption.RangeOfOption;
import org.dbflute.cbean.cvalue.ConditionValue;
import org.dbflute.cbean.ordering.ManualOrderOptionCall;
import org.dbflute.cbean.scoping.SubQuery;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.dbflute.dbmeta.DBMetaProvider;

/**
 * The abstract condition-query of ACCESS_RESULT.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsAccessResultCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsAccessResultCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    protected DBMetaProvider xgetDBMetaProvider() {
        return DBMetaInstanceHandler.getProvider();
    }

    @Override
    public String asTableDbName() {
        return "ACCESS_RESULT";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as equal. (NullAllowed: if null, no condition)
     */
    public void setId_Equal(final Long id) {
        doSetId_Equal(id);
    }

    protected void doSetId_Equal(final Long id) {
        regId(CK_EQ, id);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as notEqual. (NullAllowed: if null, no condition)
     */
    public void setId_NotEqual(final Long id) {
        doSetId_NotEqual(id);
    }

    protected void doSetId_NotEqual(final Long id) {
        regId(CK_NES, id);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setId_GreaterThan(final Long id) {
        regId(CK_GT, id);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as lessThan. (NullAllowed: if null, no condition)
     */
    public void setId_LessThan(final Long id) {
        regId(CK_LT, id);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setId_GreaterEqual(final Long id) {
        regId(CK_GE, id);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setId_LessEqual(final Long id) {
        regId(CK_LE, id);
    }

    /**
     * RangeOf with various options. (versatile) <br>
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br>
     * And NullIgnored, OnlyOnceRegistered. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param minNumber The min number of id. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of id. (NullAllowed: if null, no to-condition)
     * @param opLambda The callback for option of range-of. (NotNull)
     */
    public void setId_RangeOf(final Long minNumber, final Long maxNumber,
            final ConditionOptionCall<RangeOfOption> opLambda) {
        setId_RangeOf(minNumber, maxNumber, xcROOP(opLambda));
    }

    /**
     * RangeOf with various options. (versatile) <br>
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br>
     * And NullIgnored, OnlyOnceRegistered. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param minNumber The min number of id. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of id. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    protected void setId_RangeOf(final Long minNumber, final Long maxNumber,
            final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, xgetCValueId(), "ID", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param idList The collection of id as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setId_InScope(final Collection<Long> idList) {
        doSetId_InScope(idList);
    }

    protected void doSetId_InScope(final Collection<Long> idList) {
        regINS(CK_INS, cTL(idList), xgetCValueId(), "ID");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param idList The collection of id as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setId_NotInScope(final Collection<Long> idList) {
        doSetId_NotInScope(idList);
    }

    protected void doSetId_NotInScope(final Collection<Long> idList) {
        regINS(CK_NINS, cTL(idList), xgetCValueId(), "ID");
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     */
    public void setId_IsNull() {
        regId(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br>
     * ID: {PK, ID, NotNull, BIGINT(19)}
     */
    public void setId_IsNotNull() {
        regId(CK_ISNN, DOBJ);
    }

    protected void regId(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueId(), "ID");
    }

    protected abstract ConditionValue xgetCValueId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @param sessionId The value of sessionId as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_Equal(final String sessionId) {
        doSetSessionId_Equal(fRES(sessionId));
    }

    protected void doSetSessionId_Equal(final String sessionId) {
        regSessionId(CK_EQ, sessionId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @param sessionId The value of sessionId as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_NotEqual(final String sessionId) {
        doSetSessionId_NotEqual(fRES(sessionId));
    }

    protected void doSetSessionId_NotEqual(final String sessionId) {
        regSessionId(CK_NES, sessionId);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @param sessionId The value of sessionId as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_GreaterThan(final String sessionId) {
        regSessionId(CK_GT, fRES(sessionId));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @param sessionId The value of sessionId as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_LessThan(final String sessionId) {
        regSessionId(CK_LT, fRES(sessionId));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @param sessionId The value of sessionId as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_GreaterEqual(final String sessionId) {
        regSessionId(CK_GE, fRES(sessionId));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @param sessionId The value of sessionId as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_LessEqual(final String sessionId) {
        regSessionId(CK_LE, fRES(sessionId));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @param sessionIdList The collection of sessionId as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_InScope(final Collection<String> sessionIdList) {
        doSetSessionId_InScope(sessionIdList);
    }

    protected void doSetSessionId_InScope(final Collection<String> sessionIdList) {
        regINS(CK_INS, cTL(sessionIdList), xgetCValueSessionId(), "SESSION_ID");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @param sessionIdList The collection of sessionId as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_NotInScope(final Collection<String> sessionIdList) {
        doSetSessionId_NotInScope(sessionIdList);
    }

    protected void doSetSessionId_NotInScope(
            final Collection<String> sessionIdList) {
        regINS(CK_NINS, cTL(sessionIdList), xgetCValueSessionId(), "SESSION_ID");
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)} <br>
     * <pre>e.g. setSessionId_LikeSearch("xxx", op <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> op.<span style="color: #CC4747">likeContain()</span>);</pre>
     * @param sessionId The value of sessionId as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setSessionId_LikeSearch(final String sessionId,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setSessionId_LikeSearch(sessionId, xcLSOP(opLambda));
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)} <br>
     * <pre>e.g. setSessionId_LikeSearch("xxx", new <span style="color: #CC4747">LikeSearchOption</span>().likeContain());</pre>
     * @param sessionId The value of sessionId as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    protected void setSessionId_LikeSearch(final String sessionId,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(sessionId), xgetCValueSessionId(), "SESSION_ID",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @param sessionId The value of sessionId as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setSessionId_NotLikeSearch(final String sessionId,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setSessionId_NotLikeSearch(sessionId, xcLSOP(opLambda));
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @param sessionId The value of sessionId as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    protected void setSessionId_NotLikeSearch(final String sessionId,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(sessionId), xgetCValueSessionId(), "SESSION_ID",
                likeSearchOption);
    }

    protected void regSessionId(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueSessionId(), "SESSION_ID");
    }

    protected abstract ConditionValue xgetCValueSessionId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * RULE_ID: {VARCHAR(20)}
     * @param ruleId The value of ruleId as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setRuleId_Equal(final String ruleId) {
        doSetRuleId_Equal(fRES(ruleId));
    }

    protected void doSetRuleId_Equal(final String ruleId) {
        regRuleId(CK_EQ, ruleId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * RULE_ID: {VARCHAR(20)}
     * @param ruleId The value of ruleId as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setRuleId_NotEqual(final String ruleId) {
        doSetRuleId_NotEqual(fRES(ruleId));
    }

    protected void doSetRuleId_NotEqual(final String ruleId) {
        regRuleId(CK_NES, ruleId);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * RULE_ID: {VARCHAR(20)}
     * @param ruleId The value of ruleId as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setRuleId_GreaterThan(final String ruleId) {
        regRuleId(CK_GT, fRES(ruleId));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * RULE_ID: {VARCHAR(20)}
     * @param ruleId The value of ruleId as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setRuleId_LessThan(final String ruleId) {
        regRuleId(CK_LT, fRES(ruleId));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * RULE_ID: {VARCHAR(20)}
     * @param ruleId The value of ruleId as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setRuleId_GreaterEqual(final String ruleId) {
        regRuleId(CK_GE, fRES(ruleId));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * RULE_ID: {VARCHAR(20)}
     * @param ruleId The value of ruleId as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setRuleId_LessEqual(final String ruleId) {
        regRuleId(CK_LE, fRES(ruleId));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * RULE_ID: {VARCHAR(20)}
     * @param ruleIdList The collection of ruleId as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setRuleId_InScope(final Collection<String> ruleIdList) {
        doSetRuleId_InScope(ruleIdList);
    }

    protected void doSetRuleId_InScope(final Collection<String> ruleIdList) {
        regINS(CK_INS, cTL(ruleIdList), xgetCValueRuleId(), "RULE_ID");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * RULE_ID: {VARCHAR(20)}
     * @param ruleIdList The collection of ruleId as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setRuleId_NotInScope(final Collection<String> ruleIdList) {
        doSetRuleId_NotInScope(ruleIdList);
    }

    protected void doSetRuleId_NotInScope(final Collection<String> ruleIdList) {
        regINS(CK_NINS, cTL(ruleIdList), xgetCValueRuleId(), "RULE_ID");
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * RULE_ID: {VARCHAR(20)} <br>
     * <pre>e.g. setRuleId_LikeSearch("xxx", op <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> op.<span style="color: #CC4747">likeContain()</span>);</pre>
     * @param ruleId The value of ruleId as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setRuleId_LikeSearch(final String ruleId,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setRuleId_LikeSearch(ruleId, xcLSOP(opLambda));
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * RULE_ID: {VARCHAR(20)} <br>
     * <pre>e.g. setRuleId_LikeSearch("xxx", new <span style="color: #CC4747">LikeSearchOption</span>().likeContain());</pre>
     * @param ruleId The value of ruleId as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    protected void setRuleId_LikeSearch(final String ruleId,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(ruleId), xgetCValueRuleId(), "RULE_ID",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * RULE_ID: {VARCHAR(20)}
     * @param ruleId The value of ruleId as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setRuleId_NotLikeSearch(final String ruleId,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setRuleId_NotLikeSearch(ruleId, xcLSOP(opLambda));
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * RULE_ID: {VARCHAR(20)}
     * @param ruleId The value of ruleId as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    protected void setRuleId_NotLikeSearch(final String ruleId,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(ruleId), xgetCValueRuleId(), "RULE_ID",
                likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br>
     * RULE_ID: {VARCHAR(20)}
     */
    public void setRuleId_IsNull() {
        regRuleId(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br>
     * RULE_ID: {VARCHAR(20)}
     */
    public void setRuleId_IsNullOrEmpty() {
        regRuleId(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br>
     * RULE_ID: {VARCHAR(20)}
     */
    public void setRuleId_IsNotNull() {
        regRuleId(CK_ISNN, DOBJ);
    }

    protected void regRuleId(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueRuleId(), "RULE_ID");
    }

    protected abstract ConditionValue xgetCValueRuleId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * URL: {IX+, NotNull, VARCHAR(65536)}
     * @param url The value of url as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_Equal(final String url) {
        doSetUrl_Equal(fRES(url));
    }

    protected void doSetUrl_Equal(final String url) {
        regUrl(CK_EQ, url);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * URL: {IX+, NotNull, VARCHAR(65536)}
     * @param url The value of url as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_NotEqual(final String url) {
        doSetUrl_NotEqual(fRES(url));
    }

    protected void doSetUrl_NotEqual(final String url) {
        regUrl(CK_NES, url);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * URL: {IX+, NotNull, VARCHAR(65536)}
     * @param url The value of url as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_GreaterThan(final String url) {
        regUrl(CK_GT, fRES(url));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * URL: {IX+, NotNull, VARCHAR(65536)}
     * @param url The value of url as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_LessThan(final String url) {
        regUrl(CK_LT, fRES(url));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * URL: {IX+, NotNull, VARCHAR(65536)}
     * @param url The value of url as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_GreaterEqual(final String url) {
        regUrl(CK_GE, fRES(url));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * URL: {IX+, NotNull, VARCHAR(65536)}
     * @param url The value of url as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_LessEqual(final String url) {
        regUrl(CK_LE, fRES(url));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * URL: {IX+, NotNull, VARCHAR(65536)}
     * @param urlList The collection of url as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_InScope(final Collection<String> urlList) {
        doSetUrl_InScope(urlList);
    }

    protected void doSetUrl_InScope(final Collection<String> urlList) {
        regINS(CK_INS, cTL(urlList), xgetCValueUrl(), "URL");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * URL: {IX+, NotNull, VARCHAR(65536)}
     * @param urlList The collection of url as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_NotInScope(final Collection<String> urlList) {
        doSetUrl_NotInScope(urlList);
    }

    protected void doSetUrl_NotInScope(final Collection<String> urlList) {
        regINS(CK_NINS, cTL(urlList), xgetCValueUrl(), "URL");
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * URL: {IX+, NotNull, VARCHAR(65536)} <br>
     * <pre>e.g. setUrl_LikeSearch("xxx", op <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> op.<span style="color: #CC4747">likeContain()</span>);</pre>
     * @param url The value of url as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setUrl_LikeSearch(final String url,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setUrl_LikeSearch(url, xcLSOP(opLambda));
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * URL: {IX+, NotNull, VARCHAR(65536)} <br>
     * <pre>e.g. setUrl_LikeSearch("xxx", new <span style="color: #CC4747">LikeSearchOption</span>().likeContain());</pre>
     * @param url The value of url as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    protected void setUrl_LikeSearch(final String url,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(url), xgetCValueUrl(), "URL", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * URL: {IX+, NotNull, VARCHAR(65536)}
     * @param url The value of url as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setUrl_NotLikeSearch(final String url,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setUrl_NotLikeSearch(url, xcLSOP(opLambda));
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * URL: {IX+, NotNull, VARCHAR(65536)}
     * @param url The value of url as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    protected void setUrl_NotLikeSearch(final String url,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(url), xgetCValueUrl(), "URL", likeSearchOption);
    }

    protected void regUrl(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueUrl(), "URL");
    }

    protected abstract ConditionValue xgetCValueUrl();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * PARENT_URL: {VARCHAR(65536)}
     * @param parentUrl The value of parentUrl as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setParentUrl_Equal(final String parentUrl) {
        doSetParentUrl_Equal(fRES(parentUrl));
    }

    protected void doSetParentUrl_Equal(final String parentUrl) {
        regParentUrl(CK_EQ, parentUrl);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * PARENT_URL: {VARCHAR(65536)}
     * @param parentUrl The value of parentUrl as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setParentUrl_NotEqual(final String parentUrl) {
        doSetParentUrl_NotEqual(fRES(parentUrl));
    }

    protected void doSetParentUrl_NotEqual(final String parentUrl) {
        regParentUrl(CK_NES, parentUrl);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * PARENT_URL: {VARCHAR(65536)}
     * @param parentUrl The value of parentUrl as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setParentUrl_GreaterThan(final String parentUrl) {
        regParentUrl(CK_GT, fRES(parentUrl));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * PARENT_URL: {VARCHAR(65536)}
     * @param parentUrl The value of parentUrl as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setParentUrl_LessThan(final String parentUrl) {
        regParentUrl(CK_LT, fRES(parentUrl));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * PARENT_URL: {VARCHAR(65536)}
     * @param parentUrl The value of parentUrl as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setParentUrl_GreaterEqual(final String parentUrl) {
        regParentUrl(CK_GE, fRES(parentUrl));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * PARENT_URL: {VARCHAR(65536)}
     * @param parentUrl The value of parentUrl as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setParentUrl_LessEqual(final String parentUrl) {
        regParentUrl(CK_LE, fRES(parentUrl));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * PARENT_URL: {VARCHAR(65536)}
     * @param parentUrlList The collection of parentUrl as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setParentUrl_InScope(final Collection<String> parentUrlList) {
        doSetParentUrl_InScope(parentUrlList);
    }

    protected void doSetParentUrl_InScope(final Collection<String> parentUrlList) {
        regINS(CK_INS, cTL(parentUrlList), xgetCValueParentUrl(), "PARENT_URL");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * PARENT_URL: {VARCHAR(65536)}
     * @param parentUrlList The collection of parentUrl as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setParentUrl_NotInScope(final Collection<String> parentUrlList) {
        doSetParentUrl_NotInScope(parentUrlList);
    }

    protected void doSetParentUrl_NotInScope(
            final Collection<String> parentUrlList) {
        regINS(CK_NINS, cTL(parentUrlList), xgetCValueParentUrl(), "PARENT_URL");
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * PARENT_URL: {VARCHAR(65536)} <br>
     * <pre>e.g. setParentUrl_LikeSearch("xxx", op <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> op.<span style="color: #CC4747">likeContain()</span>);</pre>
     * @param parentUrl The value of parentUrl as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setParentUrl_LikeSearch(final String parentUrl,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setParentUrl_LikeSearch(parentUrl, xcLSOP(opLambda));
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * PARENT_URL: {VARCHAR(65536)} <br>
     * <pre>e.g. setParentUrl_LikeSearch("xxx", new <span style="color: #CC4747">LikeSearchOption</span>().likeContain());</pre>
     * @param parentUrl The value of parentUrl as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    protected void setParentUrl_LikeSearch(final String parentUrl,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(parentUrl), xgetCValueParentUrl(), "PARENT_URL",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * PARENT_URL: {VARCHAR(65536)}
     * @param parentUrl The value of parentUrl as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setParentUrl_NotLikeSearch(final String parentUrl,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setParentUrl_NotLikeSearch(parentUrl, xcLSOP(opLambda));
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * PARENT_URL: {VARCHAR(65536)}
     * @param parentUrl The value of parentUrl as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    protected void setParentUrl_NotLikeSearch(final String parentUrl,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(parentUrl), xgetCValueParentUrl(), "PARENT_URL",
                likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br>
     * PARENT_URL: {VARCHAR(65536)}
     */
    public void setParentUrl_IsNull() {
        regParentUrl(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br>
     * PARENT_URL: {VARCHAR(65536)}
     */
    public void setParentUrl_IsNullOrEmpty() {
        regParentUrl(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br>
     * PARENT_URL: {VARCHAR(65536)}
     */
    public void setParentUrl_IsNotNull() {
        regParentUrl(CK_ISNN, DOBJ);
    }

    protected void regParentUrl(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueParentUrl(), "PARENT_URL");
    }

    protected abstract ConditionValue xgetCValueParentUrl();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br>
     * STATUS: {NotNull, INTEGER(10)}
     * @param status The value of status as equal. (NullAllowed: if null, no condition)
     */
    public void setStatus_Equal(final Integer status) {
        doSetStatus_Equal(status);
    }

    protected void doSetStatus_Equal(final Integer status) {
        regStatus(CK_EQ, status);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br>
     * STATUS: {NotNull, INTEGER(10)}
     * @param status The value of status as notEqual. (NullAllowed: if null, no condition)
     */
    public void setStatus_NotEqual(final Integer status) {
        doSetStatus_NotEqual(status);
    }

    protected void doSetStatus_NotEqual(final Integer status) {
        regStatus(CK_NES, status);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br>
     * STATUS: {NotNull, INTEGER(10)}
     * @param status The value of status as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setStatus_GreaterThan(final Integer status) {
        regStatus(CK_GT, status);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br>
     * STATUS: {NotNull, INTEGER(10)}
     * @param status The value of status as lessThan. (NullAllowed: if null, no condition)
     */
    public void setStatus_LessThan(final Integer status) {
        regStatus(CK_LT, status);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * STATUS: {NotNull, INTEGER(10)}
     * @param status The value of status as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setStatus_GreaterEqual(final Integer status) {
        regStatus(CK_GE, status);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * STATUS: {NotNull, INTEGER(10)}
     * @param status The value of status as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setStatus_LessEqual(final Integer status) {
        regStatus(CK_LE, status);
    }

    /**
     * RangeOf with various options. (versatile) <br>
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br>
     * And NullIgnored, OnlyOnceRegistered. <br>
     * STATUS: {NotNull, INTEGER(10)}
     * @param minNumber The min number of status. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of status. (NullAllowed: if null, no to-condition)
     * @param opLambda The callback for option of range-of. (NotNull)
     */
    public void setStatus_RangeOf(final Integer minNumber,
            final Integer maxNumber,
            final ConditionOptionCall<RangeOfOption> opLambda) {
        setStatus_RangeOf(minNumber, maxNumber, xcROOP(opLambda));
    }

    /**
     * RangeOf with various options. (versatile) <br>
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br>
     * And NullIgnored, OnlyOnceRegistered. <br>
     * STATUS: {NotNull, INTEGER(10)}
     * @param minNumber The min number of status. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of status. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    protected void setStatus_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, xgetCValueStatus(), "STATUS",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br>
     * STATUS: {NotNull, INTEGER(10)}
     * @param statusList The collection of status as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setStatus_InScope(final Collection<Integer> statusList) {
        doSetStatus_InScope(statusList);
    }

    protected void doSetStatus_InScope(final Collection<Integer> statusList) {
        regINS(CK_INS, cTL(statusList), xgetCValueStatus(), "STATUS");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br>
     * STATUS: {NotNull, INTEGER(10)}
     * @param statusList The collection of status as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setStatus_NotInScope(final Collection<Integer> statusList) {
        doSetStatus_NotInScope(statusList);
    }

    protected void doSetStatus_NotInScope(final Collection<Integer> statusList) {
        regINS(CK_NINS, cTL(statusList), xgetCValueStatus(), "STATUS");
    }

    protected void regStatus(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueStatus(), "STATUS");
    }

    protected abstract ConditionValue xgetCValueStatus();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br>
     * HTTP_STATUS_CODE: {NotNull, INTEGER(10)}
     * @param httpStatusCode The value of httpStatusCode as equal. (NullAllowed: if null, no condition)
     */
    public void setHttpStatusCode_Equal(final Integer httpStatusCode) {
        doSetHttpStatusCode_Equal(httpStatusCode);
    }

    protected void doSetHttpStatusCode_Equal(final Integer httpStatusCode) {
        regHttpStatusCode(CK_EQ, httpStatusCode);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br>
     * HTTP_STATUS_CODE: {NotNull, INTEGER(10)}
     * @param httpStatusCode The value of httpStatusCode as notEqual. (NullAllowed: if null, no condition)
     */
    public void setHttpStatusCode_NotEqual(final Integer httpStatusCode) {
        doSetHttpStatusCode_NotEqual(httpStatusCode);
    }

    protected void doSetHttpStatusCode_NotEqual(final Integer httpStatusCode) {
        regHttpStatusCode(CK_NES, httpStatusCode);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br>
     * HTTP_STATUS_CODE: {NotNull, INTEGER(10)}
     * @param httpStatusCode The value of httpStatusCode as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setHttpStatusCode_GreaterThan(final Integer httpStatusCode) {
        regHttpStatusCode(CK_GT, httpStatusCode);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br>
     * HTTP_STATUS_CODE: {NotNull, INTEGER(10)}
     * @param httpStatusCode The value of httpStatusCode as lessThan. (NullAllowed: if null, no condition)
     */
    public void setHttpStatusCode_LessThan(final Integer httpStatusCode) {
        regHttpStatusCode(CK_LT, httpStatusCode);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * HTTP_STATUS_CODE: {NotNull, INTEGER(10)}
     * @param httpStatusCode The value of httpStatusCode as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setHttpStatusCode_GreaterEqual(final Integer httpStatusCode) {
        regHttpStatusCode(CK_GE, httpStatusCode);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * HTTP_STATUS_CODE: {NotNull, INTEGER(10)}
     * @param httpStatusCode The value of httpStatusCode as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setHttpStatusCode_LessEqual(final Integer httpStatusCode) {
        regHttpStatusCode(CK_LE, httpStatusCode);
    }

    /**
     * RangeOf with various options. (versatile) <br>
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br>
     * And NullIgnored, OnlyOnceRegistered. <br>
     * HTTP_STATUS_CODE: {NotNull, INTEGER(10)}
     * @param minNumber The min number of httpStatusCode. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of httpStatusCode. (NullAllowed: if null, no to-condition)
     * @param opLambda The callback for option of range-of. (NotNull)
     */
    public void setHttpStatusCode_RangeOf(final Integer minNumber,
            final Integer maxNumber,
            final ConditionOptionCall<RangeOfOption> opLambda) {
        setHttpStatusCode_RangeOf(minNumber, maxNumber, xcROOP(opLambda));
    }

    /**
     * RangeOf with various options. (versatile) <br>
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br>
     * And NullIgnored, OnlyOnceRegistered. <br>
     * HTTP_STATUS_CODE: {NotNull, INTEGER(10)}
     * @param minNumber The min number of httpStatusCode. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of httpStatusCode. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    protected void setHttpStatusCode_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, xgetCValueHttpStatusCode(),
                "HTTP_STATUS_CODE", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br>
     * HTTP_STATUS_CODE: {NotNull, INTEGER(10)}
     * @param httpStatusCodeList The collection of httpStatusCode as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setHttpStatusCode_InScope(
            final Collection<Integer> httpStatusCodeList) {
        doSetHttpStatusCode_InScope(httpStatusCodeList);
    }

    protected void doSetHttpStatusCode_InScope(
            final Collection<Integer> httpStatusCodeList) {
        regINS(CK_INS, cTL(httpStatusCodeList), xgetCValueHttpStatusCode(),
                "HTTP_STATUS_CODE");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br>
     * HTTP_STATUS_CODE: {NotNull, INTEGER(10)}
     * @param httpStatusCodeList The collection of httpStatusCode as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setHttpStatusCode_NotInScope(
            final Collection<Integer> httpStatusCodeList) {
        doSetHttpStatusCode_NotInScope(httpStatusCodeList);
    }

    protected void doSetHttpStatusCode_NotInScope(
            final Collection<Integer> httpStatusCodeList) {
        regINS(CK_NINS, cTL(httpStatusCodeList), xgetCValueHttpStatusCode(),
                "HTTP_STATUS_CODE");
    }

    protected void regHttpStatusCode(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueHttpStatusCode(), "HTTP_STATUS_CODE");
    }

    protected abstract ConditionValue xgetCValueHttpStatusCode();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * METHOD: {NotNull, VARCHAR(10)}
     * @param method The value of method as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setMethod_Equal(final String method) {
        doSetMethod_Equal(fRES(method));
    }

    protected void doSetMethod_Equal(final String method) {
        regMethod(CK_EQ, method);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * METHOD: {NotNull, VARCHAR(10)}
     * @param method The value of method as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setMethod_NotEqual(final String method) {
        doSetMethod_NotEqual(fRES(method));
    }

    protected void doSetMethod_NotEqual(final String method) {
        regMethod(CK_NES, method);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * METHOD: {NotNull, VARCHAR(10)}
     * @param method The value of method as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setMethod_GreaterThan(final String method) {
        regMethod(CK_GT, fRES(method));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * METHOD: {NotNull, VARCHAR(10)}
     * @param method The value of method as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setMethod_LessThan(final String method) {
        regMethod(CK_LT, fRES(method));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * METHOD: {NotNull, VARCHAR(10)}
     * @param method The value of method as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setMethod_GreaterEqual(final String method) {
        regMethod(CK_GE, fRES(method));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * METHOD: {NotNull, VARCHAR(10)}
     * @param method The value of method as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setMethod_LessEqual(final String method) {
        regMethod(CK_LE, fRES(method));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * METHOD: {NotNull, VARCHAR(10)}
     * @param methodList The collection of method as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setMethod_InScope(final Collection<String> methodList) {
        doSetMethod_InScope(methodList);
    }

    protected void doSetMethod_InScope(final Collection<String> methodList) {
        regINS(CK_INS, cTL(methodList), xgetCValueMethod(), "METHOD");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * METHOD: {NotNull, VARCHAR(10)}
     * @param methodList The collection of method as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setMethod_NotInScope(final Collection<String> methodList) {
        doSetMethod_NotInScope(methodList);
    }

    protected void doSetMethod_NotInScope(final Collection<String> methodList) {
        regINS(CK_NINS, cTL(methodList), xgetCValueMethod(), "METHOD");
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * METHOD: {NotNull, VARCHAR(10)} <br>
     * <pre>e.g. setMethod_LikeSearch("xxx", op <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> op.<span style="color: #CC4747">likeContain()</span>);</pre>
     * @param method The value of method as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setMethod_LikeSearch(final String method,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setMethod_LikeSearch(method, xcLSOP(opLambda));
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * METHOD: {NotNull, VARCHAR(10)} <br>
     * <pre>e.g. setMethod_LikeSearch("xxx", new <span style="color: #CC4747">LikeSearchOption</span>().likeContain());</pre>
     * @param method The value of method as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    protected void setMethod_LikeSearch(final String method,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(method), xgetCValueMethod(), "METHOD",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * METHOD: {NotNull, VARCHAR(10)}
     * @param method The value of method as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setMethod_NotLikeSearch(final String method,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setMethod_NotLikeSearch(method, xcLSOP(opLambda));
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * METHOD: {NotNull, VARCHAR(10)}
     * @param method The value of method as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    protected void setMethod_NotLikeSearch(final String method,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(method), xgetCValueMethod(), "METHOD",
                likeSearchOption);
    }

    protected void regMethod(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueMethod(), "METHOD");
    }

    protected abstract ConditionValue xgetCValueMethod();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * @param mimeType The value of mimeType as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setMimeType_Equal(final String mimeType) {
        doSetMimeType_Equal(fRES(mimeType));
    }

    protected void doSetMimeType_Equal(final String mimeType) {
        regMimeType(CK_EQ, mimeType);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * @param mimeType The value of mimeType as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setMimeType_NotEqual(final String mimeType) {
        doSetMimeType_NotEqual(fRES(mimeType));
    }

    protected void doSetMimeType_NotEqual(final String mimeType) {
        regMimeType(CK_NES, mimeType);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * @param mimeType The value of mimeType as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setMimeType_GreaterThan(final String mimeType) {
        regMimeType(CK_GT, fRES(mimeType));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * @param mimeType The value of mimeType as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setMimeType_LessThan(final String mimeType) {
        regMimeType(CK_LT, fRES(mimeType));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * @param mimeType The value of mimeType as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setMimeType_GreaterEqual(final String mimeType) {
        regMimeType(CK_GE, fRES(mimeType));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * @param mimeType The value of mimeType as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setMimeType_LessEqual(final String mimeType) {
        regMimeType(CK_LE, fRES(mimeType));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * @param mimeTypeList The collection of mimeType as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setMimeType_InScope(final Collection<String> mimeTypeList) {
        doSetMimeType_InScope(mimeTypeList);
    }

    protected void doSetMimeType_InScope(final Collection<String> mimeTypeList) {
        regINS(CK_INS, cTL(mimeTypeList), xgetCValueMimeType(), "MIME_TYPE");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * @param mimeTypeList The collection of mimeType as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setMimeType_NotInScope(final Collection<String> mimeTypeList) {
        doSetMimeType_NotInScope(mimeTypeList);
    }

    protected void doSetMimeType_NotInScope(
            final Collection<String> mimeTypeList) {
        regINS(CK_NINS, cTL(mimeTypeList), xgetCValueMimeType(), "MIME_TYPE");
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * MIME_TYPE: {NotNull, VARCHAR(100)} <br>
     * <pre>e.g. setMimeType_LikeSearch("xxx", op <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> op.<span style="color: #CC4747">likeContain()</span>);</pre>
     * @param mimeType The value of mimeType as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setMimeType_LikeSearch(final String mimeType,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setMimeType_LikeSearch(mimeType, xcLSOP(opLambda));
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * MIME_TYPE: {NotNull, VARCHAR(100)} <br>
     * <pre>e.g. setMimeType_LikeSearch("xxx", new <span style="color: #CC4747">LikeSearchOption</span>().likeContain());</pre>
     * @param mimeType The value of mimeType as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    protected void setMimeType_LikeSearch(final String mimeType,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(mimeType), xgetCValueMimeType(), "MIME_TYPE",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * @param mimeType The value of mimeType as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setMimeType_NotLikeSearch(final String mimeType,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setMimeType_NotLikeSearch(mimeType, xcLSOP(opLambda));
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * @param mimeType The value of mimeType as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    protected void setMimeType_NotLikeSearch(final String mimeType,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(mimeType), xgetCValueMimeType(), "MIME_TYPE",
                likeSearchOption);
    }

    protected void regMimeType(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueMimeType(), "MIME_TYPE");
    }

    protected abstract ConditionValue xgetCValueMimeType();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br>
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * @param contentLength The value of contentLength as equal. (NullAllowed: if null, no condition)
     */
    public void setContentLength_Equal(final Long contentLength) {
        doSetContentLength_Equal(contentLength);
    }

    protected void doSetContentLength_Equal(final Long contentLength) {
        regContentLength(CK_EQ, contentLength);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br>
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * @param contentLength The value of contentLength as notEqual. (NullAllowed: if null, no condition)
     */
    public void setContentLength_NotEqual(final Long contentLength) {
        doSetContentLength_NotEqual(contentLength);
    }

    protected void doSetContentLength_NotEqual(final Long contentLength) {
        regContentLength(CK_NES, contentLength);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br>
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * @param contentLength The value of contentLength as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setContentLength_GreaterThan(final Long contentLength) {
        regContentLength(CK_GT, contentLength);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br>
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * @param contentLength The value of contentLength as lessThan. (NullAllowed: if null, no condition)
     */
    public void setContentLength_LessThan(final Long contentLength) {
        regContentLength(CK_LT, contentLength);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * @param contentLength The value of contentLength as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setContentLength_GreaterEqual(final Long contentLength) {
        regContentLength(CK_GE, contentLength);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * @param contentLength The value of contentLength as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setContentLength_LessEqual(final Long contentLength) {
        regContentLength(CK_LE, contentLength);
    }

    /**
     * RangeOf with various options. (versatile) <br>
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br>
     * And NullIgnored, OnlyOnceRegistered. <br>
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * @param minNumber The min number of contentLength. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of contentLength. (NullAllowed: if null, no to-condition)
     * @param opLambda The callback for option of range-of. (NotNull)
     */
    public void setContentLength_RangeOf(final Long minNumber,
            final Long maxNumber,
            final ConditionOptionCall<RangeOfOption> opLambda) {
        setContentLength_RangeOf(minNumber, maxNumber, xcROOP(opLambda));
    }

    /**
     * RangeOf with various options. (versatile) <br>
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br>
     * And NullIgnored, OnlyOnceRegistered. <br>
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * @param minNumber The min number of contentLength. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of contentLength. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    protected void setContentLength_RangeOf(final Long minNumber,
            final Long maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, xgetCValueContentLength(),
                "CONTENT_LENGTH", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br>
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * @param contentLengthList The collection of contentLength as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setContentLength_InScope(
            final Collection<Long> contentLengthList) {
        doSetContentLength_InScope(contentLengthList);
    }

    protected void doSetContentLength_InScope(
            final Collection<Long> contentLengthList) {
        regINS(CK_INS, cTL(contentLengthList), xgetCValueContentLength(),
                "CONTENT_LENGTH");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br>
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * @param contentLengthList The collection of contentLength as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setContentLength_NotInScope(
            final Collection<Long> contentLengthList) {
        doSetContentLength_NotInScope(contentLengthList);
    }

    protected void doSetContentLength_NotInScope(
            final Collection<Long> contentLengthList) {
        regINS(CK_NINS, cTL(contentLengthList), xgetCValueContentLength(),
                "CONTENT_LENGTH");
    }

    protected void regContentLength(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueContentLength(), "CONTENT_LENGTH");
    }

    protected abstract ConditionValue xgetCValueContentLength();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br>
     * EXECUTION_TIME: {NotNull, INTEGER(10)}
     * @param executionTime The value of executionTime as equal. (NullAllowed: if null, no condition)
     */
    public void setExecutionTime_Equal(final Integer executionTime) {
        doSetExecutionTime_Equal(executionTime);
    }

    protected void doSetExecutionTime_Equal(final Integer executionTime) {
        regExecutionTime(CK_EQ, executionTime);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br>
     * EXECUTION_TIME: {NotNull, INTEGER(10)}
     * @param executionTime The value of executionTime as notEqual. (NullAllowed: if null, no condition)
     */
    public void setExecutionTime_NotEqual(final Integer executionTime) {
        doSetExecutionTime_NotEqual(executionTime);
    }

    protected void doSetExecutionTime_NotEqual(final Integer executionTime) {
        regExecutionTime(CK_NES, executionTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br>
     * EXECUTION_TIME: {NotNull, INTEGER(10)}
     * @param executionTime The value of executionTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setExecutionTime_GreaterThan(final Integer executionTime) {
        regExecutionTime(CK_GT, executionTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br>
     * EXECUTION_TIME: {NotNull, INTEGER(10)}
     * @param executionTime The value of executionTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setExecutionTime_LessThan(final Integer executionTime) {
        regExecutionTime(CK_LT, executionTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * EXECUTION_TIME: {NotNull, INTEGER(10)}
     * @param executionTime The value of executionTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setExecutionTime_GreaterEqual(final Integer executionTime) {
        regExecutionTime(CK_GE, executionTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * EXECUTION_TIME: {NotNull, INTEGER(10)}
     * @param executionTime The value of executionTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setExecutionTime_LessEqual(final Integer executionTime) {
        regExecutionTime(CK_LE, executionTime);
    }

    /**
     * RangeOf with various options. (versatile) <br>
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br>
     * And NullIgnored, OnlyOnceRegistered. <br>
     * EXECUTION_TIME: {NotNull, INTEGER(10)}
     * @param minNumber The min number of executionTime. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of executionTime. (NullAllowed: if null, no to-condition)
     * @param opLambda The callback for option of range-of. (NotNull)
     */
    public void setExecutionTime_RangeOf(final Integer minNumber,
            final Integer maxNumber,
            final ConditionOptionCall<RangeOfOption> opLambda) {
        setExecutionTime_RangeOf(minNumber, maxNumber, xcROOP(opLambda));
    }

    /**
     * RangeOf with various options. (versatile) <br>
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br>
     * And NullIgnored, OnlyOnceRegistered. <br>
     * EXECUTION_TIME: {NotNull, INTEGER(10)}
     * @param minNumber The min number of executionTime. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of executionTime. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    protected void setExecutionTime_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, xgetCValueExecutionTime(),
                "EXECUTION_TIME", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br>
     * EXECUTION_TIME: {NotNull, INTEGER(10)}
     * @param executionTimeList The collection of executionTime as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setExecutionTime_InScope(
            final Collection<Integer> executionTimeList) {
        doSetExecutionTime_InScope(executionTimeList);
    }

    protected void doSetExecutionTime_InScope(
            final Collection<Integer> executionTimeList) {
        regINS(CK_INS, cTL(executionTimeList), xgetCValueExecutionTime(),
                "EXECUTION_TIME");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br>
     * EXECUTION_TIME: {NotNull, INTEGER(10)}
     * @param executionTimeList The collection of executionTime as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setExecutionTime_NotInScope(
            final Collection<Integer> executionTimeList) {
        doSetExecutionTime_NotInScope(executionTimeList);
    }

    protected void doSetExecutionTime_NotInScope(
            final Collection<Integer> executionTimeList) {
        regINS(CK_NINS, cTL(executionTimeList), xgetCValueExecutionTime(),
                "EXECUTION_TIME");
    }

    protected void regExecutionTime(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueExecutionTime(), "EXECUTION_TIME");
    }

    protected abstract ConditionValue xgetCValueExecutionTime();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     * @param lastModified The value of lastModified as equal. (NullAllowed: if null, no condition)
     */
    public void setLastModified_Equal(final Long lastModified) {
        doSetLastModified_Equal(lastModified);
    }

    protected void doSetLastModified_Equal(final Long lastModified) {
        regLastModified(CK_EQ, lastModified);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     * @param lastModified The value of lastModified as notEqual. (NullAllowed: if null, no condition)
     */
    public void setLastModified_NotEqual(final Long lastModified) {
        doSetLastModified_NotEqual(lastModified);
    }

    protected void doSetLastModified_NotEqual(final Long lastModified) {
        regLastModified(CK_NES, lastModified);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     * @param lastModified The value of lastModified as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setLastModified_GreaterThan(final Long lastModified) {
        regLastModified(CK_GT, lastModified);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     * @param lastModified The value of lastModified as lessThan. (NullAllowed: if null, no condition)
     */
    public void setLastModified_LessThan(final Long lastModified) {
        regLastModified(CK_LT, lastModified);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     * @param lastModified The value of lastModified as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setLastModified_GreaterEqual(final Long lastModified) {
        regLastModified(CK_GE, lastModified);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     * @param lastModified The value of lastModified as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setLastModified_LessEqual(final Long lastModified) {
        regLastModified(CK_LE, lastModified);
    }

    /**
     * RangeOf with various options. (versatile) <br>
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br>
     * And NullIgnored, OnlyOnceRegistered. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     * @param minNumber The min number of lastModified. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of lastModified. (NullAllowed: if null, no to-condition)
     * @param opLambda The callback for option of range-of. (NotNull)
     */
    public void setLastModified_RangeOf(final Long minNumber,
            final Long maxNumber,
            final ConditionOptionCall<RangeOfOption> opLambda) {
        setLastModified_RangeOf(minNumber, maxNumber, xcROOP(opLambda));
    }

    /**
     * RangeOf with various options. (versatile) <br>
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br>
     * And NullIgnored, OnlyOnceRegistered. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     * @param minNumber The min number of lastModified. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of lastModified. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    protected void setLastModified_RangeOf(final Long minNumber,
            final Long maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, xgetCValueLastModified(), "LAST_MODIFIED",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     * @param lastModifiedList The collection of lastModified as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setLastModified_InScope(final Collection<Long> lastModifiedList) {
        doSetLastModified_InScope(lastModifiedList);
    }

    protected void doSetLastModified_InScope(
            final Collection<Long> lastModifiedList) {
        regINS(CK_INS, cTL(lastModifiedList), xgetCValueLastModified(),
                "LAST_MODIFIED");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     * @param lastModifiedList The collection of lastModified as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setLastModified_NotInScope(
            final Collection<Long> lastModifiedList) {
        doSetLastModified_NotInScope(lastModifiedList);
    }

    protected void doSetLastModified_NotInScope(
            final Collection<Long> lastModifiedList) {
        regINS(CK_NINS, cTL(lastModifiedList), xgetCValueLastModified(),
                "LAST_MODIFIED");
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     */
    public void setLastModified_IsNull() {
        regLastModified(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br>
     * LAST_MODIFIED: {BIGINT(19)}
     */
    public void setLastModified_IsNotNull() {
        regLastModified(CK_ISNN, DOBJ);
    }

    protected void regLastModified(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueLastModified(), "LAST_MODIFIED");
    }

    protected abstract ConditionValue xgetCValueLastModified();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br>
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @param createTime The value of createTime as equal. (NullAllowed: if null, no condition)
     */
    public void setCreateTime_Equal(final Long createTime) {
        doSetCreateTime_Equal(createTime);
    }

    protected void doSetCreateTime_Equal(final Long createTime) {
        regCreateTime(CK_EQ, createTime);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br>
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @param createTime The value of createTime as notEqual. (NullAllowed: if null, no condition)
     */
    public void setCreateTime_NotEqual(final Long createTime) {
        doSetCreateTime_NotEqual(createTime);
    }

    protected void doSetCreateTime_NotEqual(final Long createTime) {
        regCreateTime(CK_NES, createTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br>
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @param createTime The value of createTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setCreateTime_GreaterThan(final Long createTime) {
        regCreateTime(CK_GT, createTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br>
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @param createTime The value of createTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setCreateTime_LessThan(final Long createTime) {
        regCreateTime(CK_LT, createTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @param createTime The value of createTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setCreateTime_GreaterEqual(final Long createTime) {
        regCreateTime(CK_GE, createTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @param createTime The value of createTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setCreateTime_LessEqual(final Long createTime) {
        regCreateTime(CK_LE, createTime);
    }

    /**
     * RangeOf with various options. (versatile) <br>
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br>
     * And NullIgnored, OnlyOnceRegistered. <br>
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @param minNumber The min number of createTime. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of createTime. (NullAllowed: if null, no to-condition)
     * @param opLambda The callback for option of range-of. (NotNull)
     */
    public void setCreateTime_RangeOf(final Long minNumber,
            final Long maxNumber,
            final ConditionOptionCall<RangeOfOption> opLambda) {
        setCreateTime_RangeOf(minNumber, maxNumber, xcROOP(opLambda));
    }

    /**
     * RangeOf with various options. (versatile) <br>
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br>
     * And NullIgnored, OnlyOnceRegistered. <br>
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @param minNumber The min number of createTime. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of createTime. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    protected void setCreateTime_RangeOf(final Long minNumber,
            final Long maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, xgetCValueCreateTime(), "CREATE_TIME",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br>
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @param createTimeList The collection of createTime as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreateTime_InScope(final Collection<Long> createTimeList) {
        doSetCreateTime_InScope(createTimeList);
    }

    protected void doSetCreateTime_InScope(final Collection<Long> createTimeList) {
        regINS(CK_INS, cTL(createTimeList), xgetCValueCreateTime(),
                "CREATE_TIME");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br>
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @param createTimeList The collection of createTime as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreateTime_NotInScope(final Collection<Long> createTimeList) {
        doSetCreateTime_NotInScope(createTimeList);
    }

    protected void doSetCreateTime_NotInScope(
            final Collection<Long> createTimeList) {
        regINS(CK_NINS, cTL(createTimeList), xgetCValueCreateTime(),
                "CREATE_TIME");
    }

    protected void regCreateTime(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueCreateTime(), "CREATE_TIME");
    }

    protected abstract ConditionValue xgetCValueCreateTime();

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br>
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_Equal()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ, AccessResultCB.class);
    }

    /**
     * Prepare ScalarCondition as equal. <br>
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_NotEqual()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES, AccessResultCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br>
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT, AccessResultCB.class);
    }

    /**
     * Prepare ScalarCondition as lessThan. <br>
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_LessThan()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT, AccessResultCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br>
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE, AccessResultCB.class);
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br>
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_LessEqual()</span>.max(new SubQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE, AccessResultCB.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xscalarCondition(final String fn,
            final SubQuery<CB> sq, final String rd, final HpSSQOption<CB> op) {
        assertObjectNotNull("subQuery", sq);
        final AccessResultCB cb = xcreateScalarConditionCB();
        sq.query((CB) cb);
        final String pp = keepScalarCondition(cb.query()); // for saving query-value
        op.setPartitionByCBean((CB) xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(fn, cb.query(), pp, rd, op);
    }

    public abstract String keepScalarCondition(AccessResultCQ sq);

    protected AccessResultCB xcreateScalarConditionCB() {
        final AccessResultCB cb = newMyCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected AccessResultCB xcreateScalarConditionPartitionByCB() {
        final AccessResultCB cb = newMyCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String fn,
            final SubQuery<AccessResultCB> sq, final String al,
            final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final AccessResultCB cb = new AccessResultCB();
        cb.xsetupForDerivedReferrer(this);
        lockCall(() -> sq.query(cb));
        final String pp = keepSpecifyMyselfDerived(cb.query());
        final String pk = "ID";
        registerSpecifyMyselfDerived(fn, cb.query(), pk, pk, pp,
                "myselfDerived", al, op);
    }

    public abstract String keepSpecifyMyselfDerived(AccessResultCQ sq);

    /**
     * Prepare for (Query)MyselfDerived (correlated sub-query).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<AccessResultCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived(AccessResultCB.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xqderiveMyselfDerived(
            final String fn, final SubQuery<CB> sq, final String rd,
            final Object vl, final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final AccessResultCB cb = new AccessResultCB();
        cb.xsetupForDerivedReferrer(this);
        sq.query((CB) cb);
        final String pk = "ID";
        final String sqpp = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String prpp = keepQueryMyselfDerivedParameter(vl);
        registerQueryMyselfDerived(fn, cb.query(), pk, pk, sqpp,
                "myselfDerived", rd, vl, prpp, op);
    }

    public abstract String keepQueryMyselfDerived(AccessResultCQ sq);

    public abstract String keepQueryMyselfDerivedParameter(Object vl);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (correlated sub-query).
     * @param subCBLambda The implementation of sub-query. (NotNull)
     */
    public void myselfExists(final SubQuery<AccessResultCB> subCBLambda) {
        assertObjectNotNull("subCBLambda", subCBLambda);
        final AccessResultCB cb = new AccessResultCB();
        cb.xsetupForMyselfExists(this);
        lockCall(() -> subCBLambda.query(cb));
        final String pp = keepMyselfExists(cb.query());
        registerMyselfExists(cb.query(), pp);
    }

    public abstract String keepMyselfExists(AccessResultCQ sq);

    // ===================================================================================
    //                                                                        Manual Order
    //                                                                        ============
    /**
     * Order along manual ordering information.
     * <pre>
     * cb.query().addOrderBy_Birthdate_Asc().<span style="color: #CC4747">withManualOrder</span>(<span style="color: #553000">op</span> <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *     <span style="color: #553000">op</span>.<span style="color: #CC4747">when_GreaterEqual</span>(priorityDate); <span style="color: #3F7E5E">// e.g. 2000/01/01</span>
     * });
     * <span style="color: #3F7E5E">// order by </span>
     * <span style="color: #3F7E5E">//   case</span>
     * <span style="color: #3F7E5E">//     when BIRTHDATE &gt;= '2000/01/01' then 0</span>
     * <span style="color: #3F7E5E">//     else 1</span>
     * <span style="color: #3F7E5E">//   end asc, ...</span>
     *
     * cb.query().addOrderBy_MemberStatusCode_Asc().<span style="color: #CC4747">withManualOrder</span>(<span style="color: #553000">op</span> <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *     <span style="color: #553000">op</span>.<span style="color: #CC4747">when_GreaterEqual</span>(priorityDate); <span style="color: #3F7E5E">// e.g. 2000/01/01</span>
     *     <span style="color: #553000">op</span>.<span style="color: #CC4747">when_Equal</span>(CDef.MemberStatus.Withdrawal);
     *     <span style="color: #553000">op</span>.<span style="color: #CC4747">when_Equal</span>(CDef.MemberStatus.Formalized);
     *     <span style="color: #553000">op</span>.<span style="color: #CC4747">when_Equal</span>(CDef.MemberStatus.Provisional);
     * });
     * <span style="color: #3F7E5E">// order by </span>
     * <span style="color: #3F7E5E">//   case</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'WDL' then 0</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'FML' then 1</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'PRV' then 2</span>
     * <span style="color: #3F7E5E">//     else 3</span>
     * <span style="color: #3F7E5E">//   end asc, ...</span>
     * </pre>
     * <p>This function with Union is unsupported!</p>
     * <p>The order values are bound (treated as bind parameter).</p>
     * @param opLambda The callback for option of manual-order containing order values. (NotNull)
     */
    public void withManualOrder(final ManualOrderOptionCall opLambda) { // is user public!
        xdoWithManualOrder(cMOO(opLambda));
    }

    // ===================================================================================
    //                                                                    Small Adjustment
    //                                                                    ================
    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    protected AccessResultCB newMyCB() {
        return new AccessResultCB();
    }

    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabUDT() {
        return Date.class.getName();
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

    protected String xabSCP() {
        return SubQuery.class.getName();
    }
}
