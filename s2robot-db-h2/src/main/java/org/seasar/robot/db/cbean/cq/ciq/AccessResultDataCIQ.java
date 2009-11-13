package org.seasar.robot.db.cbean.cq.ciq;

import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;
import org.seasar.robot.dbflute.cbean.coption.ConditionOption;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.robot.db.cbean.AccessResultDataCB;
import org.seasar.robot.db.cbean.cq.AccessResultCQ;
import org.seasar.robot.db.cbean.cq.AccessResultDataCQ;
import org.seasar.robot.db.cbean.cq.bs.AbstractBsAccessResultDataCQ;
import org.seasar.robot.db.cbean.cq.bs.BsAccessResultDataCQ;

/**
 * The condition-inline-query of ACCESS_RESULT_DATA.
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDataCIQ extends AbstractBsAccessResultDataCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsAccessResultDataCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AccessResultDataCIQ(ConditionQuery childQuery, SqlClause sqlClause,
            String aliasName, int nestLevel, BsAccessResultDataCQ myCQ) {
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
        String msg = "InlineQuery must not need UNION method: "
                + baseQueryAsSuper + " : " + unionQueryAsSuper;
        throw new IllegalConditionBeanOperationException(msg);
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
            String msg = "Sorry! InScopeSubQuery of on-clause is unavailable";
            throw new IllegalConditionBeanOperationException(msg);
        }
        return _onClauseInline ? getRealAliasName() + "." + columnName
                : columnName;
    }

    @Override
    protected void registerExistsSubQuery(ConditionQuery subQuery,
            String columnName, String relatedColumnName, String propertyName) {
        String msg = "Sorry! ExistsSubQuery at in-line view is unavailable. So please use InScopeSubQyery.";
        throw new IllegalConditionBeanOperationException(msg);
    }

    // ===================================================================================
    //                                                                Override about Query
    //                                                                ====================
    protected ConditionValue getCValueId() {
        return _myCQ.getId();
    }

    public String keepId_InScopeSubQuery_AccessResult(AccessResultCQ sq) {
        return _myCQ.keepId_InScopeSubQuery_AccessResult(sq);
    }

    protected ConditionValue getCValueTransformerName() {
        return _myCQ.getTransformerName();
    }

    protected ConditionValue getCValueData() {
        return _myCQ.getData();
    }

    protected ConditionValue getCValueEncoding() {
        return _myCQ.getEncoding();
    }

    public String keepScalarSubQuery(AccessResultDataCQ subQuery) {
        throwIICBOE("ScalarSubQuery");
        return null;
    }

    public String keepMyselfInScopeSubQuery(AccessResultDataCQ subQuery) {
        throwIICBOE("MyselfInScopeSubQuery");
        return null;
    }

    protected void throwIICBOE(String name) { // throwInlineIllegalConditionBeanOperationException()
        throw new IllegalConditionBeanOperationException("Sorry! " + name
                + " at in-line view is unavailable!");
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    String xiCB() {
        return AccessResultDataCB.class.getName();
    }

    String xiCQ() {
        return AccessResultDataCQ.class.getName();
    }
}
