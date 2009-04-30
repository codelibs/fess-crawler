package org.seasar.robot.db.cbean.cq.ciq;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.ckey.*;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.db.cbean.*;
import org.seasar.robot.db.cbean.cq.bs.*;
import org.seasar.robot.db.cbean.cq.*;

/**
 * The condition-inline-query of URL_QUEUE.
 * @author DBFlute(AutoGenerator)
 */
public class UrlQueueCIQ extends AbstractBsUrlQueueCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsUrlQueueCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public UrlQueueCIQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel, BsUrlQueueCQ myCQ) {
        super(childQuery, sqlClause, aliasName, nestLevel);
        _myCQ = myCQ;
        _foreignPropertyName = _myCQ.getForeignPropertyName();// Accept foreign property name.
        _relationPath = _myCQ.getRelationPath();// Accept relation path.
    }

    // ===================================================================================
    //                                                             Override about Register
    //                                                             =======================
    @Override
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper, ConditionQuery unionQueryAsSuper) {
        throw new UnsupportedOperationException("InlineQuery must not need UNION method: " + baseQueryAsSuper + " : " + unionQueryAsSuper);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(ConditionKey k, Object v, ConditionValue cv, String col) {
        regIQ(k, v, cv, col);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(ConditionKey k, Object v, ConditionValue cv, String col, ConditionOption op) {
        regIQ(k, v, cv, col, op);
    }

    @Override
    protected void registerWhereClause(String whereClause) {
        registerInlineWhereClause(whereClause);
    }

    @Override
    protected String getInScopeSubQueryRealColumnName(String columnName) {
        if (_onClauseInline) {
            throw new UnsupportedOperationException("InScopeSubQuery of on-clause is unsupported");
        }
        return _onClauseInline ? getRealAliasName() + "." + columnName : columnName;
    }

    @Override
    protected void registerExistsSubQuery(ConditionQuery subQuery
            , String columnName, String relatedColumnName, String propertyName) {
        throw new UnsupportedOperationException("Sorry! ExistsSubQuery at inline view is unsupported. So please use InScopeSubQyery.");
    }

    // ===================================================================================
    //                                                                Override about Query
    //                                                                ====================
    protected ConditionValue getCValueId() {
        return _myCQ.getId();
    }
    protected ConditionValue getCValueSessionId() {
        return _myCQ.getSessionId();
    }
    protected ConditionValue getCValueMethod() {
        return _myCQ.getMethod();
    }
    protected ConditionValue getCValueUrl() {
        return _myCQ.getUrl();
    }
    protected ConditionValue getCValueParentUrl() {
        return _myCQ.getParentUrl();
    }
    protected ConditionValue getCValueDepth() {
        return _myCQ.getDepth();
    }
    protected ConditionValue getCValueCreateTime() {
        return _myCQ.getCreateTime();
    }

    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    public String keepScalarSubQuery(UrlQueueCQ subQuery) {
        throw new UnsupportedOperationException("ScalarSubQuery at inline() is unsupported! Sorry!");
    }

    // ===================================================================================
    //                                                             MySelf InScope SubQuery
    //                                                             =======================
    public String keepMyselfInScopeSubQuery(UrlQueueCQ subQuery) {
        throw new UnsupportedOperationException("MyselfInScopeSubQuery at inline() is unsupported! Sorry!");
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    String xiCB() { return UrlQueueCB.class.getName(); }
    String xiCQ() { return UrlQueueCQ.class.getName(); }
}
