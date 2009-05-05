package org.seasar.robot.db.cbean.cq.bs;

import java.util.Collection;

import org.seasar.dbflute.cbean.AbstractConditionQuery;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
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
     * Equal(=). And NullIgnored, OnlyOnceRegistered. {PK : NotNull : BIGINT : FK to ACCESS_RESULT}
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
        regTransformerName(CK_EQ, fRES(transformerName));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param transformerName The value of transformerName as notEqual.
     */
    public void setTransformerName_NotEqual(String transformerName) {
        regTransformerName(CK_NE, fRES(transformerName));
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
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. {CLOB}
     * @param data The value of data as equal.
     */
    public void setData_Equal(String data) {
        regData(CK_EQ, fRES(data));
    }

    /**
     * NotEqual(!=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param data The value of data as notEqual.
     */
    public void setData_NotEqual(String data) {
        regData(CK_NE, fRES(data));
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param data The value of data as greaterThan.
     */
    public void setData_GreaterThan(String data) {
        regData(CK_GT, fRES(data));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param data The value of data as lessThan.
     */
    public void setData_LessThan(String data) {
        regData(CK_LT, fRES(data));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param data The value of data as greaterEqual.
     */
    public void setData_GreaterEqual(String data) {
        regData(CK_GE, fRES(data));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered.
     * @param data The value of data as lessEqual.
     */
    public void setData_LessEqual(String data) {
        regData(CK_LE, fRES(data));
    }

    /**
     * PrefixSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param data The value of data as prefixSearch.
     */
    public void setData_PrefixSearch(String data) {
        setData_LikeSearch(data, cLSOP());
    }

    /**
     * InScope(in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param dataList The collection of data as inScope.
     */
    public void setData_InScope(Collection<String> dataList) {
        regINS(CK_INS, cTL(dataList), getCValueData(), "DATA");
    }

    /**
     * NotInScope(not in ('a', 'b')). And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered.
     * @param dataList The collection of data as notInScope.
     */
    public void setData_NotInScope(Collection<String> dataList) {
        regINS(CK_NINS, cTL(dataList), getCValueData(), "DATA");
    }

    /**
     * LikeSearch(like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param data The value of data as likeSearch.
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setData_LikeSearch(String data,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(data), getCValueData(), "DATA", likeSearchOption);
    }

    /**
     * NotLikeSearch(not like 'xxx%' escape ...). And NullOrEmptyIgnored, SeveralRegistered.
     * @param data The value of data as notLikeSearch.
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setData_NotLikeSearch(String data,
            LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(data), getCValueData(), "DATA", likeSearchOption);
    }

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

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public SSQFunction<AccessResultDataCB> scalar_Equal() {
        return xcreateSSQFunction("=");
    }

    public SSQFunction<AccessResultDataCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(">=");
    }

    public SSQFunction<AccessResultDataCB> scalar_GreaterThan() {
        return xcreateSSQFunction(">");
    }

    public SSQFunction<AccessResultDataCB> scalar_LessEqual() {
        return xcreateSSQFunction("<=");
    }

    public SSQFunction<AccessResultDataCB> scalar_LessThan() {
        return xcreateSSQFunction("<");
    }

    protected SSQFunction<AccessResultDataCB> xcreateSSQFunction(
            final String operand) {
        return new SSQFunction<AccessResultDataCB>(
                new SSQSetupper<AccessResultDataCB>() {
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
}
