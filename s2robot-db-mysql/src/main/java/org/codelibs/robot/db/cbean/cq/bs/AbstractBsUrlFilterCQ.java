package org.codelibs.robot.db.cbean.cq.bs;

import java.util.Collection;
import java.util.Date;

import org.codelibs.robot.db.allcommon.DBMetaInstanceHandler;
import org.codelibs.robot.db.cbean.UrlFilterCB;
import org.codelibs.robot.db.cbean.cq.UrlFilterCQ;
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
 * The abstract condition-query of URL_FILTER.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsUrlFilterCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsUrlFilterCQ(final ConditionQuery referrerQuery,
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
        return "URL_FILTER";
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
     * URL: {NotNull, TEXT(65535)}
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
     * URL: {NotNull, TEXT(65535)}
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
     * URL: {NotNull, TEXT(65535)}
     * @param url The value of url as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_GreaterThan(final String url) {
        regUrl(CK_GT, fRES(url));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * URL: {NotNull, TEXT(65535)}
     * @param url The value of url as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_LessThan(final String url) {
        regUrl(CK_LT, fRES(url));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * URL: {NotNull, TEXT(65535)}
     * @param url The value of url as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_GreaterEqual(final String url) {
        regUrl(CK_GE, fRES(url));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * URL: {NotNull, TEXT(65535)}
     * @param url The value of url as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_LessEqual(final String url) {
        regUrl(CK_LE, fRES(url));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * URL: {NotNull, TEXT(65535)}
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
     * URL: {NotNull, TEXT(65535)}
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
     * URL: {NotNull, TEXT(65535)} <br>
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
     * URL: {NotNull, TEXT(65535)} <br>
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
     * URL: {NotNull, TEXT(65535)}
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
     * URL: {NotNull, TEXT(65535)}
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
     * FILTER_TYPE: {NotNull, VARCHAR(1)}
     * @param filterType The value of filterType as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_Equal(final String filterType) {
        doSetFilterType_Equal(fRES(filterType));
    }

    protected void doSetFilterType_Equal(final String filterType) {
        regFilterType(CK_EQ, filterType);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * FILTER_TYPE: {NotNull, VARCHAR(1)}
     * @param filterType The value of filterType as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_NotEqual(final String filterType) {
        doSetFilterType_NotEqual(fRES(filterType));
    }

    protected void doSetFilterType_NotEqual(final String filterType) {
        regFilterType(CK_NES, filterType);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * FILTER_TYPE: {NotNull, VARCHAR(1)}
     * @param filterType The value of filterType as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_GreaterThan(final String filterType) {
        regFilterType(CK_GT, fRES(filterType));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * FILTER_TYPE: {NotNull, VARCHAR(1)}
     * @param filterType The value of filterType as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_LessThan(final String filterType) {
        regFilterType(CK_LT, fRES(filterType));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * FILTER_TYPE: {NotNull, VARCHAR(1)}
     * @param filterType The value of filterType as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_GreaterEqual(final String filterType) {
        regFilterType(CK_GE, fRES(filterType));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * FILTER_TYPE: {NotNull, VARCHAR(1)}
     * @param filterType The value of filterType as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_LessEqual(final String filterType) {
        regFilterType(CK_LE, fRES(filterType));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * FILTER_TYPE: {NotNull, VARCHAR(1)}
     * @param filterTypeList The collection of filterType as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_InScope(final Collection<String> filterTypeList) {
        doSetFilterType_InScope(filterTypeList);
    }

    protected void doSetFilterType_InScope(
            final Collection<String> filterTypeList) {
        regINS(CK_INS, cTL(filterTypeList), xgetCValueFilterType(),
                "FILTER_TYPE");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * FILTER_TYPE: {NotNull, VARCHAR(1)}
     * @param filterTypeList The collection of filterType as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_NotInScope(final Collection<String> filterTypeList) {
        doSetFilterType_NotInScope(filterTypeList);
    }

    protected void doSetFilterType_NotInScope(
            final Collection<String> filterTypeList) {
        regINS(CK_NINS, cTL(filterTypeList), xgetCValueFilterType(),
                "FILTER_TYPE");
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * FILTER_TYPE: {NotNull, VARCHAR(1)} <br>
     * <pre>e.g. setFilterType_LikeSearch("xxx", op <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> op.<span style="color: #CC4747">likeContain()</span>);</pre>
     * @param filterType The value of filterType as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setFilterType_LikeSearch(final String filterType,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setFilterType_LikeSearch(filterType, xcLSOP(opLambda));
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * FILTER_TYPE: {NotNull, VARCHAR(1)} <br>
     * <pre>e.g. setFilterType_LikeSearch("xxx", new <span style="color: #CC4747">LikeSearchOption</span>().likeContain());</pre>
     * @param filterType The value of filterType as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    protected void setFilterType_LikeSearch(final String filterType,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(filterType), xgetCValueFilterType(), "FILTER_TYPE",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * FILTER_TYPE: {NotNull, VARCHAR(1)}
     * @param filterType The value of filterType as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setFilterType_NotLikeSearch(final String filterType,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setFilterType_NotLikeSearch(filterType, xcLSOP(opLambda));
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * FILTER_TYPE: {NotNull, VARCHAR(1)}
     * @param filterType The value of filterType as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    protected void setFilterType_NotLikeSearch(final String filterType,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(filterType), xgetCValueFilterType(), "FILTER_TYPE",
                likeSearchOption);
    }

    protected void regFilterType(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueFilterType(), "FILTER_TYPE");
    }

    protected abstract ConditionValue xgetCValueFilterType();

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
     * cb.query().<span style="color: #CC4747">scalar_Equal()</span>.max(new SubQuery&lt;UrlFilterCB&gt;() {
     *     public void query(UrlFilterCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UrlFilterCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ, UrlFilterCB.class);
    }

    /**
     * Prepare ScalarCondition as equal. <br>
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_NotEqual()</span>.max(new SubQuery&lt;UrlFilterCB&gt;() {
     *     public void query(UrlFilterCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UrlFilterCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES, UrlFilterCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br>
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;UrlFilterCB&gt;() {
     *     public void query(UrlFilterCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UrlFilterCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT, UrlFilterCB.class);
    }

    /**
     * Prepare ScalarCondition as lessThan. <br>
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_LessThan()</span>.max(new SubQuery&lt;UrlFilterCB&gt;() {
     *     public void query(UrlFilterCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UrlFilterCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT, UrlFilterCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br>
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;UrlFilterCB&gt;() {
     *     public void query(UrlFilterCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UrlFilterCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE, UrlFilterCB.class);
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br>
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_LessEqual()</span>.max(new SubQuery&lt;UrlFilterCB&gt;() {
     *     public void query(UrlFilterCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UrlFilterCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE, UrlFilterCB.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xscalarCondition(final String fn,
            final SubQuery<CB> sq, final String rd, final HpSSQOption<CB> op) {
        assertObjectNotNull("subQuery", sq);
        final UrlFilterCB cb = xcreateScalarConditionCB();
        sq.query((CB) cb);
        final String pp = keepScalarCondition(cb.query()); // for saving query-value
        op.setPartitionByCBean((CB) xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(fn, cb.query(), pp, rd, op);
    }

    public abstract String keepScalarCondition(UrlFilterCQ sq);

    protected UrlFilterCB xcreateScalarConditionCB() {
        final UrlFilterCB cb = newMyCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected UrlFilterCB xcreateScalarConditionPartitionByCB() {
        final UrlFilterCB cb = newMyCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String fn, final SubQuery<UrlFilterCB> sq,
            final String al, final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final UrlFilterCB cb = new UrlFilterCB();
        cb.xsetupForDerivedReferrer(this);
        lockCall(() -> sq.query(cb));
        final String pp = keepSpecifyMyselfDerived(cb.query());
        final String pk = "ID";
        registerSpecifyMyselfDerived(fn, cb.query(), pk, pk, pp,
                "myselfDerived", al, op);
    }

    public abstract String keepSpecifyMyselfDerived(UrlFilterCQ sq);

    /**
     * Prepare for (Query)MyselfDerived (correlated sub-query).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<UrlFilterCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived(UrlFilterCB.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xqderiveMyselfDerived(
            final String fn, final SubQuery<CB> sq, final String rd,
            final Object vl, final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final UrlFilterCB cb = new UrlFilterCB();
        cb.xsetupForDerivedReferrer(this);
        sq.query((CB) cb);
        final String pk = "ID";
        final String sqpp = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String prpp = keepQueryMyselfDerivedParameter(vl);
        registerQueryMyselfDerived(fn, cb.query(), pk, pk, sqpp,
                "myselfDerived", rd, vl, prpp, op);
    }

    public abstract String keepQueryMyselfDerived(UrlFilterCQ sq);

    public abstract String keepQueryMyselfDerivedParameter(Object vl);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (correlated sub-query).
     * @param subCBLambda The implementation of sub-query. (NotNull)
     */
    public void myselfExists(final SubQuery<UrlFilterCB> subCBLambda) {
        assertObjectNotNull("subCBLambda", subCBLambda);
        final UrlFilterCB cb = new UrlFilterCB();
        cb.xsetupForMyselfExists(this);
        lockCall(() -> subCBLambda.query(cb));
        final String pp = keepMyselfExists(cb.query());
        registerMyselfExists(cb.query(), pp);
    }

    public abstract String keepMyselfExists(UrlFilterCQ sq);

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
    protected UrlFilterCB newMyCB() {
        return new UrlFilterCB();
    }

    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabUDT() {
        return Date.class.getName();
    }

    protected String xabCQ() {
        return UrlFilterCQ.class.getName();
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
