package org.seasar.robot.db.cbean.cq.ciq;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.cbean.cq.AccessResultCQ;
import org.seasar.robot.db.cbean.cq.AccessResultDataCQ;
import org.seasar.robot.db.cbean.cq.bs.AbstractBsAccessResultCQ;
import org.seasar.robot.db.cbean.cq.bs.BsAccessResultCQ;

/**
 * The condition-inline-query of ACCESS_RESULT.
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultCIQ extends AbstractBsAccessResultCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsAccessResultCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AccessResultCIQ(ConditionQuery childQuery, SqlClause sqlClause,
            String aliasName, int nestLevel, BsAccessResultCQ myCQ) {
        super(childQuery, sqlClause, aliasName, nestLevel);
        _myCQ = myCQ;
        _foreignPropertyName = _myCQ.getForeignPropertyName();// Accept foreign property name.
        _relationPath = _myCQ.getRelationPath();// Accept relation path.
    }

    // ===================================================================================
    //                                                             Override about Register
    //                                                             =======================
    @Override
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper,
            ConditionQuery unionQueryAsSuper) {
        throw new UnsupportedOperationException(
                "InlineQuery must not need UNION method: " + baseQueryAsSuper
                        + " : " + unionQueryAsSuper);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(ConditionKey k,
            Object v, ConditionValue cv, String col) {
        regIQ(k, v, cv, col);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(ConditionKey k,
            Object v, ConditionValue cv, String col, ConditionOption op) {
        regIQ(k, v, cv, col, op);
    }

    @Override
    protected void registerWhereClause(String whereClause) {
        registerInlineWhereClause(whereClause);
    }

    @Override
    protected String getInScopeSubQueryRealColumnName(String columnName) {
        if (_onClauseInline) {
            throw new UnsupportedOperationException(
                    "InScopeSubQuery of on-clause is unsupported");
        }
        return _onClauseInline ? getRealAliasName() + "." + columnName
                : columnName;
    }

    @Override
    protected void registerExistsSubQuery(ConditionQuery subQuery,
            String columnName, String relatedColumnName, String propertyName) {
        throw new UnsupportedOperationException(
                "Sorry! ExistsSubQuery at inline view is unsupported. So please use InScopeSubQyery.");
    }

    // ===================================================================================
    //                                                                Override about Query
    //                                                                ====================
    protected ConditionValue getCValueId() {
        return _myCQ.getId();
    }

    public String keepId_InScopeSubQuery_AccessResultDataAsOne(
            AccessResultDataCQ subQuery) {
        return _myCQ.keepId_InScopeSubQuery_AccessResultDataAsOne(subQuery);
    }

    public String keepId_NotInScopeSubQuery_AccessResultDataAsOne(
            AccessResultDataCQ subQuery) {
        return _myCQ.keepId_NotInScopeSubQuery_AccessResultDataAsOne(subQuery);
    }

    public String keepId_ExistsSubQuery_AccessResultDataAsOne(
            AccessResultDataCQ subQuery) {
        throw new UnsupportedOperationException(
                "ExistsSubQuery at inline() is unsupported! Sorry!");
    }

    public String keepId_NotExistsSubQuery_AccessResultDataAsOne(
            AccessResultDataCQ subQuery) {
        throw new UnsupportedOperationException(
                "NotExistsSubQuery at inline() is unsupported! Sorry!");
    }

    protected ConditionValue getCValueSessionId() {
        return _myCQ.getSessionId();
    }

    protected ConditionValue getCValueRuleId() {
        return _myCQ.getRuleId();
    }

    protected ConditionValue getCValueUrl() {
        return _myCQ.getUrl();
    }

    protected ConditionValue getCValueParentUrl() {
        return _myCQ.getParentUrl();
    }

    protected ConditionValue getCValueStatus() {
        return _myCQ.getStatus();
    }

    protected ConditionValue getCValueHttpStatusCode() {
        return _myCQ.getHttpStatusCode();
    }

    protected ConditionValue getCValueMethod() {
        return _myCQ.getMethod();
    }

    protected ConditionValue getCValueMimeType() {
        return _myCQ.getMimeType();
    }

    protected ConditionValue getCValueExecutionTime() {
        return _myCQ.getExecutionTime();
    }

    protected ConditionValue getCValueCreateTime() {
        return _myCQ.getCreateTime();
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public String keepScalarSubQuery(AccessResultCQ subQuery) {
        throw new UnsupportedOperationException(
                "ScalarSubQuery at inline() is unsupported! Sorry!");
    }

    // ===================================================================================
    //                                                             MySelf InScope SubQuery
    //                                                             =======================
    public String keepMyselfInScopeSubQuery(AccessResultCQ subQuery) {
        throw new UnsupportedOperationException(
                "MyselfInScopeSubQuery at inline() is unsupported! Sorry!");
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    String xiCB() {
        return AccessResultCB.class.getName();
    }

    String xiCQ() {
        return AccessResultCQ.class.getName();
    }
}
