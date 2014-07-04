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
 * The abstract condition-query of URL_FILTER.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsUrlFilterCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsUrlFilterCQ(ConditionQuery referrerQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
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
        return "URL_FILTER";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19)}
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
     * ID: {PK, NotNull, NUMBER(19)}
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
     * ID: {PK, NotNull, NUMBER(19)}
     * @param id The value of id as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setId_GreaterThan(Long id) {
        regId(CK_GT, id);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19)}
     * @param id The value of id as lessThan. (NullAllowed: if null, no condition)
     */
    public void setId_LessThan(Long id) {
        regId(CK_LT, id);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19)}
     * @param id The value of id as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setId_GreaterEqual(Long id) {
        regId(CK_GE, id);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19)}
     * @param id The value of id as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setId_LessEqual(Long id) {
        regId(CK_LE, id);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19)}
     * @param minNumber The min number of id. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of id. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setId_RangeOf(Long minNumber, Long maxNumber, RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueId(), "ID", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19)}
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
     * ID: {PK, NotNull, NUMBER(19)}
     * @param idList The collection of id as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setId_NotInScope(Collection<Long> idList) {
        doSetId_NotInScope(idList);
    }

    protected void doSetId_NotInScope(Collection<Long> idList) {
        regINS(CK_NINS, cTL(idList), getCValueId(), "ID");
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19)}
     */
    public void setId_IsNull() { regId(CK_ISN, DOBJ); }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * ID: {PK, NotNull, NUMBER(19)}
     */
    public void setId_IsNotNull() { regId(CK_ISNN, DOBJ); }

    protected void regId(ConditionKey ky, Object vl) { regQ(ky, vl, getCValueId(), "ID"); }
    protected abstract ConditionValue getCValueId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SESSION_ID: {IX+, NotNull, VARCHAR2(20)}
     * @param sessionId The value of sessionId as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_Equal(String sessionId) {
        doSetSessionId_Equal(fRES(sessionId));
    }

    protected void doSetSessionId_Equal(String sessionId) {
        regSessionId(CK_EQ, sessionId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SESSION_ID: {IX+, NotNull, VARCHAR2(20)}
     * @param sessionId The value of sessionId as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_NotEqual(String sessionId) {
        doSetSessionId_NotEqual(fRES(sessionId));
    }

    protected void doSetSessionId_NotEqual(String sessionId) {
        regSessionId(CK_NES, sessionId);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SESSION_ID: {IX+, NotNull, VARCHAR2(20)}
     * @param sessionId The value of sessionId as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_GreaterThan(String sessionId) {
        regSessionId(CK_GT, fRES(sessionId));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SESSION_ID: {IX+, NotNull, VARCHAR2(20)}
     * @param sessionId The value of sessionId as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_LessThan(String sessionId) {
        regSessionId(CK_LT, fRES(sessionId));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SESSION_ID: {IX+, NotNull, VARCHAR2(20)}
     * @param sessionId The value of sessionId as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_GreaterEqual(String sessionId) {
        regSessionId(CK_GE, fRES(sessionId));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * SESSION_ID: {IX+, NotNull, VARCHAR2(20)}
     * @param sessionId The value of sessionId as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_LessEqual(String sessionId) {
        regSessionId(CK_LE, fRES(sessionId));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * SESSION_ID: {IX+, NotNull, VARCHAR2(20)}
     * @param sessionIdList The collection of sessionId as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_InScope(Collection<String> sessionIdList) {
        doSetSessionId_InScope(sessionIdList);
    }

    public void doSetSessionId_InScope(Collection<String> sessionIdList) {
        regINS(CK_INS, cTL(sessionIdList), getCValueSessionId(), "SESSION_ID");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * SESSION_ID: {IX+, NotNull, VARCHAR2(20)}
     * @param sessionIdList The collection of sessionId as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_NotInScope(Collection<String> sessionIdList) {
        doSetSessionId_NotInScope(sessionIdList);
    }

    public void doSetSessionId_NotInScope(Collection<String> sessionIdList) {
        regINS(CK_NINS, cTL(sessionIdList), getCValueSessionId(), "SESSION_ID");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SESSION_ID: {IX+, NotNull, VARCHAR2(20)}
     * @param sessionId The value of sessionId as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setSessionId_PrefixSearch(String sessionId) {
        setSessionId_LikeSearch(sessionId, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SESSION_ID: {IX+, NotNull, VARCHAR2(20)} <br />
     * <pre>e.g. setSessionId_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param sessionId The value of sessionId as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setSessionId_LikeSearch(String sessionId, LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(sessionId), getCValueSessionId(), "SESSION_ID", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * SESSION_ID: {IX+, NotNull, VARCHAR2(20)}
     * @param sessionId The value of sessionId as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setSessionId_NotLikeSearch(String sessionId, LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(sessionId), getCValueSessionId(), "SESSION_ID", likeSearchOption);
    }

    protected void regSessionId(ConditionKey ky, Object vl) { regQ(ky, vl, getCValueSessionId(), "SESSION_ID"); }
    protected abstract ConditionValue getCValueSessionId();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {NotNull, VARCHAR2(4000)}
     * @param url The value of url as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_Equal(String url) {
        doSetUrl_Equal(fRES(url));
    }

    protected void doSetUrl_Equal(String url) {
        regUrl(CK_EQ, url);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {NotNull, VARCHAR2(4000)}
     * @param url The value of url as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_NotEqual(String url) {
        doSetUrl_NotEqual(fRES(url));
    }

    protected void doSetUrl_NotEqual(String url) {
        regUrl(CK_NES, url);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {NotNull, VARCHAR2(4000)}
     * @param url The value of url as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_GreaterThan(String url) {
        regUrl(CK_GT, fRES(url));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {NotNull, VARCHAR2(4000)}
     * @param url The value of url as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_LessThan(String url) {
        regUrl(CK_LT, fRES(url));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {NotNull, VARCHAR2(4000)}
     * @param url The value of url as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_GreaterEqual(String url) {
        regUrl(CK_GE, fRES(url));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * URL: {NotNull, VARCHAR2(4000)}
     * @param url The value of url as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_LessEqual(String url) {
        regUrl(CK_LE, fRES(url));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * URL: {NotNull, VARCHAR2(4000)}
     * @param urlList The collection of url as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_InScope(Collection<String> urlList) {
        doSetUrl_InScope(urlList);
    }

    public void doSetUrl_InScope(Collection<String> urlList) {
        regINS(CK_INS, cTL(urlList), getCValueUrl(), "URL");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * URL: {NotNull, VARCHAR2(4000)}
     * @param urlList The collection of url as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_NotInScope(Collection<String> urlList) {
        doSetUrl_NotInScope(urlList);
    }

    public void doSetUrl_NotInScope(Collection<String> urlList) {
        regINS(CK_NINS, cTL(urlList), getCValueUrl(), "URL");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * URL: {NotNull, VARCHAR2(4000)}
     * @param url The value of url as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setUrl_PrefixSearch(String url) {
        setUrl_LikeSearch(url, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * URL: {NotNull, VARCHAR2(4000)} <br />
     * <pre>e.g. setUrl_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param url The value of url as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setUrl_LikeSearch(String url, LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(url), getCValueUrl(), "URL", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * URL: {NotNull, VARCHAR2(4000)}
     * @param url The value of url as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setUrl_NotLikeSearch(String url, LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(url), getCValueUrl(), "URL", likeSearchOption);
    }

    protected void regUrl(ConditionKey ky, Object vl) { regQ(ky, vl, getCValueUrl(), "URL"); }
    protected abstract ConditionValue getCValueUrl();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * FILTER_TYPE: {NotNull, VARCHAR2(1)}
     * @param filterType The value of filterType as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_Equal(String filterType) {
        doSetFilterType_Equal(fRES(filterType));
    }

    protected void doSetFilterType_Equal(String filterType) {
        regFilterType(CK_EQ, filterType);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * FILTER_TYPE: {NotNull, VARCHAR2(1)}
     * @param filterType The value of filterType as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_NotEqual(String filterType) {
        doSetFilterType_NotEqual(fRES(filterType));
    }

    protected void doSetFilterType_NotEqual(String filterType) {
        regFilterType(CK_NES, filterType);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * FILTER_TYPE: {NotNull, VARCHAR2(1)}
     * @param filterType The value of filterType as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_GreaterThan(String filterType) {
        regFilterType(CK_GT, fRES(filterType));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * FILTER_TYPE: {NotNull, VARCHAR2(1)}
     * @param filterType The value of filterType as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_LessThan(String filterType) {
        regFilterType(CK_LT, fRES(filterType));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * FILTER_TYPE: {NotNull, VARCHAR2(1)}
     * @param filterType The value of filterType as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_GreaterEqual(String filterType) {
        regFilterType(CK_GE, fRES(filterType));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * FILTER_TYPE: {NotNull, VARCHAR2(1)}
     * @param filterType The value of filterType as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_LessEqual(String filterType) {
        regFilterType(CK_LE, fRES(filterType));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * FILTER_TYPE: {NotNull, VARCHAR2(1)}
     * @param filterTypeList The collection of filterType as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_InScope(Collection<String> filterTypeList) {
        doSetFilterType_InScope(filterTypeList);
    }

    public void doSetFilterType_InScope(Collection<String> filterTypeList) {
        regINS(CK_INS, cTL(filterTypeList), getCValueFilterType(), "FILTER_TYPE");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * FILTER_TYPE: {NotNull, VARCHAR2(1)}
     * @param filterTypeList The collection of filterType as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_NotInScope(Collection<String> filterTypeList) {
        doSetFilterType_NotInScope(filterTypeList);
    }

    public void doSetFilterType_NotInScope(Collection<String> filterTypeList) {
        regINS(CK_NINS, cTL(filterTypeList), getCValueFilterType(), "FILTER_TYPE");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * FILTER_TYPE: {NotNull, VARCHAR2(1)}
     * @param filterType The value of filterType as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setFilterType_PrefixSearch(String filterType) {
        setFilterType_LikeSearch(filterType, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * FILTER_TYPE: {NotNull, VARCHAR2(1)} <br />
     * <pre>e.g. setFilterType_LikeSearch("xxx", new <span style="color: #DD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param filterType The value of filterType as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setFilterType_LikeSearch(String filterType, LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(filterType), getCValueFilterType(), "FILTER_TYPE", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * FILTER_TYPE: {NotNull, VARCHAR2(1)}
     * @param filterType The value of filterType as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setFilterType_NotLikeSearch(String filterType, LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(filterType), getCValueFilterType(), "FILTER_TYPE", likeSearchOption);
    }

    protected void regFilterType(ConditionKey ky, Object vl) { regQ(ky, vl, getCValueFilterType(), "FILTER_TYPE"); }
    protected abstract ConditionValue getCValueFilterType();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATE_TIME: {NotNull, TIMESTAMP(6)(11, 6)}
     * @param createTime The value of createTime as equal. (NullAllowed: if null, no condition)
     */
    public void setCreateTime_Equal(java.sql.Timestamp createTime) {
        regCreateTime(CK_EQ,  createTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATE_TIME: {NotNull, TIMESTAMP(6)(11, 6)}
     * @param createTime The value of createTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setCreateTime_GreaterThan(java.sql.Timestamp createTime) {
        regCreateTime(CK_GT,  createTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATE_TIME: {NotNull, TIMESTAMP(6)(11, 6)}
     * @param createTime The value of createTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setCreateTime_LessThan(java.sql.Timestamp createTime) {
        regCreateTime(CK_LT,  createTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATE_TIME: {NotNull, TIMESTAMP(6)(11, 6)}
     * @param createTime The value of createTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setCreateTime_GreaterEqual(java.sql.Timestamp createTime) {
        regCreateTime(CK_GE,  createTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATE_TIME: {NotNull, TIMESTAMP(6)(11, 6)}
     * @param createTime The value of createTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setCreateTime_LessEqual(java.sql.Timestamp createTime) {
        regCreateTime(CK_LE, createTime);
    }

    /**
     * FromTo with various options. (versatile) {(default) fromDatetime &lt;= column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * CREATE_TIME: {NotNull, TIMESTAMP(6)(11, 6)}
     * <pre>e.g. setCreateTime_FromTo(fromDate, toDate, new <span style="color: #DD4747">FromToOption</span>().compareAsDate());</pre>
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of createTime. (NullAllowed: if null, no from-condition)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of createTime. (NullAllowed: if null, no to-condition)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setCreateTime_FromTo(Date fromDatetime, Date toDatetime, FromToOption fromToOption) {
        regFTQ((fromDatetime != null ? new java.sql.Timestamp(fromDatetime.getTime()) : null), (toDatetime != null ? new java.sql.Timestamp(toDatetime.getTime()) : null), getCValueCreateTime(), "CREATE_TIME", fromToOption);
    }

    /**
     * DateFromTo. (Date means yyyy/MM/dd) {fromDate &lt;= column &lt; toDate + 1 day} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * CREATE_TIME: {NotNull, TIMESTAMP(6)(11, 6)}
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #DD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of createTime. (NullAllowed: if null, no from-condition)
     * @param toDate The to-date(yyyy/MM/dd) of createTime. (NullAllowed: if null, no to-condition)
     */
    public void setCreateTime_DateFromTo(Date fromDate, Date toDate) {
        setCreateTime_FromTo(fromDate, toDate, new FromToOption().compareAsDate());
    }

    protected void regCreateTime(ConditionKey ky, Object vl) { regQ(ky, vl, getCValueCreateTime(), "CREATE_TIME"); }
    protected abstract ConditionValue getCValueCreateTime();

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_Equal()</span>.max(new SubQuery&lt;UrlFilterCB&gt;() {
     *     public void query(UrlFilterCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UrlFilterCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ.getOperand(), UrlFilterCB.class);
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;UrlFilterCB&gt;() {
     *     public void query(UrlFilterCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UrlFilterCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES.getOperand(), UrlFilterCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;UrlFilterCB&gt;() {
     *     public void query(UrlFilterCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UrlFilterCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT.getOperand(), UrlFilterCB.class);
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_LessThan()</span>.max(new SubQuery&lt;UrlFilterCB&gt;() {
     *     public void query(UrlFilterCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UrlFilterCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT.getOperand(), UrlFilterCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;UrlFilterCB&gt;() {
     *     public void query(UrlFilterCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UrlFilterCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE.getOperand(), UrlFilterCB.class);
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;UrlFilterCB&gt;() {
     *     public void query(UrlFilterCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<UrlFilterCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE.getOperand(), UrlFilterCB.class);
    }

    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xscalarCondition(String fn, SubQuery<CB> sq, String rd, HpSSQOption<CB> op) {
        assertObjectNotNull("subQuery", sq);
        UrlFilterCB cb = xcreateScalarConditionCB(); sq.query((CB)cb);
        String pp = keepScalarCondition(cb.query()); // for saving query-value
        op.setPartitionByCBean((CB)xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(fn, cb.query(), pp, rd, op);
    }
    public abstract String keepScalarCondition(UrlFilterCQ sq);

    protected UrlFilterCB xcreateScalarConditionCB() {
        UrlFilterCB cb = newMyCB(); cb.xsetupForScalarCondition(this); return cb;
    }

    protected UrlFilterCB xcreateScalarConditionPartitionByCB() {
        UrlFilterCB cb = newMyCB(); cb.xsetupForScalarConditionPartitionBy(this); return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(String fn, SubQuery<UrlFilterCB> sq, String al, DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        UrlFilterCB cb = new UrlFilterCB(); cb.xsetupForDerivedReferrer(this);
        try { lock(); sq.query(cb); } finally { unlock(); }
        String pp = keepSpecifyMyselfDerived(cb.query());
        String pk = "ID";
        registerSpecifyMyselfDerived(fn, cb.query(), pk, pk, pp, "myselfDerived", al, op);
    }
    public abstract String keepSpecifyMyselfDerived(UrlFilterCQ sq);

    /**
     * Prepare for (Query)MyselfDerived (correlated sub-query).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<UrlFilterCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived(UrlFilterCB.class);
    }
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xqderiveMyselfDerived(String fn, SubQuery<CB> sq, String rd, Object vl, DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        UrlFilterCB cb = new UrlFilterCB(); cb.xsetupForDerivedReferrer(this); sq.query((CB)cb);
        String pk = "ID";
        String sqpp = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        String prpp = keepQueryMyselfDerivedParameter(vl);
        registerQueryMyselfDerived(fn, cb.query(), pk, pk, sqpp, "myselfDerived", rd, vl, prpp, op);
    }
    public abstract String keepQueryMyselfDerived(UrlFilterCQ sq);
    public abstract String keepQueryMyselfDerivedParameter(Object vl);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (correlated sub-query).
     * @param subQuery The implementation of sub-query. (NotNull)
     */
    public void myselfExists(SubQuery<UrlFilterCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        UrlFilterCB cb = new UrlFilterCB(); cb.xsetupForMyselfExists(this);
        try { lock(); subQuery.query(cb); } finally { unlock(); }
        String pp = keepMyselfExists(cb.query());
        registerMyselfExists(cb.query(), pp);
    }
    public abstract String keepMyselfExists(UrlFilterCQ sq);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (sub-query).
     * @param subQuery The implementation of sub-query. (NotNull)
     */
    public void myselfInScope(SubQuery<UrlFilterCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        UrlFilterCB cb = new UrlFilterCB(); cb.xsetupForMyselfInScope(this);
        try { lock(); subQuery.query(cb); } finally { unlock(); }
        String pp = keepMyselfInScope(cb.query());
        registerMyselfInScope(cb.query(), pp);
    }
    public abstract String keepMyselfInScope(UrlFilterCQ sq);

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
    protected UrlFilterCB newMyCB() {
        return new UrlFilterCB();
    }
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCQ() { return UrlFilterCQ.class.getName(); }
    protected String xabLSO() { return LikeSearchOption.class.getName(); }
    protected String xabSSQS() { return HpSSQSetupper.class.getName(); }
}
