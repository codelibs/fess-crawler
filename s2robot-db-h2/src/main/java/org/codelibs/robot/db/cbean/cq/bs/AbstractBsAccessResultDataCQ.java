package org.codelibs.robot.db.cbean.cq.bs;

import java.util.Collection;
import java.util.Date;

import org.codelibs.robot.db.allcommon.DBMetaInstanceHandler;
import org.codelibs.robot.db.cbean.AccessResultDataCB;
import org.codelibs.robot.db.cbean.cq.AccessResultDataCQ;
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
 * The abstract condition-query of ACCESS_RESULT_DATA.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsAccessResultDataCQ extends
        AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsAccessResultDataCQ(final ConditionQuery referrerQuery,
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
        return "ACCESS_RESULT_DATA";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br>
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
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
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
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
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
     * @param id The value of id as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setId_GreaterThan(final Long id) {
        regId(CK_GT, id);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br>
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
     * @param id The value of id as lessThan. (NullAllowed: if null, no condition)
     */
    public void setId_LessThan(final Long id) {
        regId(CK_LT, id);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
     * @param id The value of id as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setId_GreaterEqual(final Long id) {
        regId(CK_GE, id);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br>
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
     * @param id The value of id as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setId_LessEqual(final Long id) {
        regId(CK_LE, id);
    }

    /**
     * RangeOf with various options. (versatile) <br>
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br>
     * And NullIgnored, OnlyOnceRegistered. <br>
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
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
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
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
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
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
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
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
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
     */
    public void setId_IsNull() {
        regId(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br>
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
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
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)}
     * @param transformerName The value of transformerName as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_Equal(final String transformerName) {
        doSetTransformerName_Equal(fRES(transformerName));
    }

    protected void doSetTransformerName_Equal(final String transformerName) {
        regTransformerName(CK_EQ, transformerName);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)}
     * @param transformerName The value of transformerName as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_NotEqual(final String transformerName) {
        doSetTransformerName_NotEqual(fRES(transformerName));
    }

    protected void doSetTransformerName_NotEqual(final String transformerName) {
        regTransformerName(CK_NES, transformerName);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)}
     * @param transformerName The value of transformerName as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_GreaterThan(final String transformerName) {
        regTransformerName(CK_GT, fRES(transformerName));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)}
     * @param transformerName The value of transformerName as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_LessThan(final String transformerName) {
        regTransformerName(CK_LT, fRES(transformerName));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)}
     * @param transformerName The value of transformerName as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_GreaterEqual(final String transformerName) {
        regTransformerName(CK_GE, fRES(transformerName));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)}
     * @param transformerName The value of transformerName as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_LessEqual(final String transformerName) {
        regTransformerName(CK_LE, fRES(transformerName));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)}
     * @param transformerNameList The collection of transformerName as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_InScope(
            final Collection<String> transformerNameList) {
        doSetTransformerName_InScope(transformerNameList);
    }

    protected void doSetTransformerName_InScope(
            final Collection<String> transformerNameList) {
        regINS(CK_INS, cTL(transformerNameList), xgetCValueTransformerName(),
                "TRANSFORMER_NAME");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)}
     * @param transformerNameList The collection of transformerName as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setTransformerName_NotInScope(
            final Collection<String> transformerNameList) {
        doSetTransformerName_NotInScope(transformerNameList);
    }

    protected void doSetTransformerName_NotInScope(
            final Collection<String> transformerNameList) {
        regINS(CK_NINS, cTL(transformerNameList), xgetCValueTransformerName(),
                "TRANSFORMER_NAME");
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)} <br>
     * <pre>e.g. setTransformerName_LikeSearch("xxx", op <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> op.<span style="color: #CC4747">likeContain()</span>);</pre>
     * @param transformerName The value of transformerName as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setTransformerName_LikeSearch(final String transformerName,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setTransformerName_LikeSearch(transformerName, xcLSOP(opLambda));
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)} <br>
     * <pre>e.g. setTransformerName_LikeSearch("xxx", new <span style="color: #CC4747">LikeSearchOption</span>().likeContain());</pre>
     * @param transformerName The value of transformerName as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    protected void setTransformerName_LikeSearch(final String transformerName,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(transformerName), xgetCValueTransformerName(),
                "TRANSFORMER_NAME", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)}
     * @param transformerName The value of transformerName as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setTransformerName_NotLikeSearch(final String transformerName,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setTransformerName_NotLikeSearch(transformerName, xcLSOP(opLambda));
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)}
     * @param transformerName The value of transformerName as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    protected void setTransformerName_NotLikeSearch(
            final String transformerName,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(transformerName), xgetCValueTransformerName(),
                "TRANSFORMER_NAME", likeSearchOption);
    }

    protected void regTransformerName(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueTransformerName(), "TRANSFORMER_NAME");
    }

    protected abstract ConditionValue xgetCValueTransformerName();

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br>
     * DATA: {BLOB(2147483647)}
     */
    public void setData_IsNull() {
        regData(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br>
     * DATA: {BLOB(2147483647)}
     */
    public void setData_IsNotNull() {
        regData(CK_ISNN, DOBJ);
    }

    protected void regData(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueData(), "DATA");
    }

    protected abstract ConditionValue xgetCValueData();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * ENCODING: {VARCHAR(20)}
     * @param encoding The value of encoding as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_Equal(final String encoding) {
        doSetEncoding_Equal(fRES(encoding));
    }

    protected void doSetEncoding_Equal(final String encoding) {
        regEncoding(CK_EQ, encoding);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * ENCODING: {VARCHAR(20)}
     * @param encoding The value of encoding as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_NotEqual(final String encoding) {
        doSetEncoding_NotEqual(fRES(encoding));
    }

    protected void doSetEncoding_NotEqual(final String encoding) {
        regEncoding(CK_NES, encoding);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * ENCODING: {VARCHAR(20)}
     * @param encoding The value of encoding as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_GreaterThan(final String encoding) {
        regEncoding(CK_GT, fRES(encoding));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * ENCODING: {VARCHAR(20)}
     * @param encoding The value of encoding as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_LessThan(final String encoding) {
        regEncoding(CK_LT, fRES(encoding));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * ENCODING: {VARCHAR(20)}
     * @param encoding The value of encoding as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_GreaterEqual(final String encoding) {
        regEncoding(CK_GE, fRES(encoding));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br>
     * ENCODING: {VARCHAR(20)}
     * @param encoding The value of encoding as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_LessEqual(final String encoding) {
        regEncoding(CK_LE, fRES(encoding));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * ENCODING: {VARCHAR(20)}
     * @param encodingList The collection of encoding as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_InScope(final Collection<String> encodingList) {
        doSetEncoding_InScope(encodingList);
    }

    protected void doSetEncoding_InScope(final Collection<String> encodingList) {
        regINS(CK_INS, cTL(encodingList), xgetCValueEncoding(), "ENCODING");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br>
     * ENCODING: {VARCHAR(20)}
     * @param encodingList The collection of encoding as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setEncoding_NotInScope(final Collection<String> encodingList) {
        doSetEncoding_NotInScope(encodingList);
    }

    protected void doSetEncoding_NotInScope(
            final Collection<String> encodingList) {
        regINS(CK_NINS, cTL(encodingList), xgetCValueEncoding(), "ENCODING");
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * ENCODING: {VARCHAR(20)} <br>
     * <pre>e.g. setEncoding_LikeSearch("xxx", op <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> op.<span style="color: #CC4747">likeContain()</span>);</pre>
     * @param encoding The value of encoding as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setEncoding_LikeSearch(final String encoding,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setEncoding_LikeSearch(encoding, xcLSOP(opLambda));
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br>
     * ENCODING: {VARCHAR(20)} <br>
     * <pre>e.g. setEncoding_LikeSearch("xxx", new <span style="color: #CC4747">LikeSearchOption</span>().likeContain());</pre>
     * @param encoding The value of encoding as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    protected void setEncoding_LikeSearch(final String encoding,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(encoding), xgetCValueEncoding(), "ENCODING",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * ENCODING: {VARCHAR(20)}
     * @param encoding The value of encoding as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param opLambda The callback for option of like-search. (NotNull)
     */
    public void setEncoding_NotLikeSearch(final String encoding,
            final ConditionOptionCall<LikeSearchOption> opLambda) {
        setEncoding_NotLikeSearch(encoding, xcLSOP(opLambda));
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br>
     * And NullOrEmptyIgnored, SeveralRegistered. <br>
     * ENCODING: {VARCHAR(20)}
     * @param encoding The value of encoding as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    protected void setEncoding_NotLikeSearch(final String encoding,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(encoding), xgetCValueEncoding(), "ENCODING",
                likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br>
     * ENCODING: {VARCHAR(20)}
     */
    public void setEncoding_IsNull() {
        regEncoding(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br>
     * ENCODING: {VARCHAR(20)}
     */
    public void setEncoding_IsNullOrEmpty() {
        regEncoding(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br>
     * ENCODING: {VARCHAR(20)}
     */
    public void setEncoding_IsNotNull() {
        regEncoding(CK_ISNN, DOBJ);
    }

    protected void regEncoding(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, xgetCValueEncoding(), "ENCODING");
    }

    protected abstract ConditionValue xgetCValueEncoding();

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br>
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_Equal()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ, AccessResultDataCB.class);
    }

    /**
     * Prepare ScalarCondition as equal. <br>
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_NotEqual()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES, AccessResultDataCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br>
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT, AccessResultDataCB.class);
    }

    /**
     * Prepare ScalarCondition as lessThan. <br>
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_LessThan()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT, AccessResultDataCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br>
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE, AccessResultDataCB.class);
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br>
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #CC4747">scalar_LessEqual()</span>.max(new SubQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<AccessResultDataCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE, AccessResultDataCB.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xscalarCondition(final String fn,
            final SubQuery<CB> sq, final String rd, final HpSSQOption<CB> op) {
        assertObjectNotNull("subQuery", sq);
        final AccessResultDataCB cb = xcreateScalarConditionCB();
        sq.query((CB) cb);
        final String pp = keepScalarCondition(cb.query()); // for saving query-value
        op.setPartitionByCBean((CB) xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(fn, cb.query(), pp, rd, op);
    }

    public abstract String keepScalarCondition(AccessResultDataCQ sq);

    protected AccessResultDataCB xcreateScalarConditionCB() {
        final AccessResultDataCB cb = newMyCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected AccessResultDataCB xcreateScalarConditionPartitionByCB() {
        final AccessResultDataCB cb = newMyCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String fn,
            final SubQuery<AccessResultDataCB> sq, final String al,
            final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForDerivedReferrer(this);
        lockCall(() -> sq.query(cb));
        final String pp = keepSpecifyMyselfDerived(cb.query());
        final String pk = "ID";
        registerSpecifyMyselfDerived(fn, cb.query(), pk, pk, pp,
                "myselfDerived", al, op);
    }

    public abstract String keepSpecifyMyselfDerived(AccessResultDataCQ sq);

    /**
     * Prepare for (Query)MyselfDerived (correlated sub-query).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<AccessResultDataCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived(AccessResultDataCB.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xqderiveMyselfDerived(
            final String fn, final SubQuery<CB> sq, final String rd,
            final Object vl, final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForDerivedReferrer(this);
        sq.query((CB) cb);
        final String pk = "ID";
        final String sqpp = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String prpp = keepQueryMyselfDerivedParameter(vl);
        registerQueryMyselfDerived(fn, cb.query(), pk, pk, sqpp,
                "myselfDerived", rd, vl, prpp, op);
    }

    public abstract String keepQueryMyselfDerived(AccessResultDataCQ sq);

    public abstract String keepQueryMyselfDerivedParameter(Object vl);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (correlated sub-query).
     * @param subCBLambda The implementation of sub-query. (NotNull)
     */
    public void myselfExists(final SubQuery<AccessResultDataCB> subCBLambda) {
        assertObjectNotNull("subCBLambda", subCBLambda);
        final AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForMyselfExists(this);
        lockCall(() -> subCBLambda.query(cb));
        final String pp = keepMyselfExists(cb.query());
        registerMyselfExists(cb.query(), pp);
    }

    public abstract String keepMyselfExists(AccessResultDataCQ sq);

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
    protected AccessResultDataCB newMyCB() {
        return new AccessResultDataCB();
    }

    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabUDT() {
        return Date.class.getName();
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

    protected String xabSCP() {
        return SubQuery.class.getName();
    }
}
