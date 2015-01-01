package org.codelibs.robot.db.cbean.cq.ciq;

import java.util.Map;

import org.codelibs.robot.db.cbean.AccessResultCB;
import org.codelibs.robot.db.cbean.cq.AccessResultCQ;
import org.codelibs.robot.db.cbean.cq.bs.AbstractBsAccessResultCQ;
import org.codelibs.robot.db.cbean.cq.bs.BsAccessResultCQ;
import org.dbflute.cbean.ConditionQuery;
import org.dbflute.cbean.ckey.ConditionKey;
import org.dbflute.cbean.coption.ConditionOption;
import org.dbflute.cbean.cvalue.ConditionValue;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The condition-query for in-line of ACCESS_RESULT.
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
    public AccessResultCIQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel, final BsAccessResultCQ myCQ) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
        _myCQ = myCQ;
        _foreignPropertyName = _myCQ.xgetForeignPropertyName(); // accept foreign property name
        _relationPath = _myCQ.xgetRelationPath(); // accept relation path
        _inline = true;
    }

    // ===================================================================================
    //                                                             Override about Register
    //                                                             =======================
    @Override
    protected void reflectRelationOnUnionQuery(final ConditionQuery bq,
            final ConditionQuery uq) {
        throw new IllegalConditionBeanOperationException(
                "InlineView cannot use Union: " + bq + " : " + uq);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(
            final ConditionKey k, final Object v, final ConditionValue cv,
            final String col) {
        regIQ(k, v, cv, col);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(
            final ConditionKey k, final Object v, final ConditionValue cv,
            final String col, final ConditionOption op) {
        regIQ(k, v, cv, col, op);
    }

    @Override
    protected void registerWhereClause(final String wc) {
        registerInlineWhereClause(wc);
    }

    @Override
    protected boolean isInScopeRelationSuppressLocalAliasName() {
        if (_onClause) {
            throw new IllegalConditionBeanOperationException(
                    "InScopeRelation on OnClause is unsupported.");
        }
        return true;
    }

    // ===================================================================================
    //                                                                Override about Query
    //                                                                ====================
    @Override
    protected ConditionValue xgetCValueId() {
        return _myCQ.xdfgetId();
    }

    @Override
    protected ConditionValue xgetCValueSessionId() {
        return _myCQ.xdfgetSessionId();
    }

    @Override
    protected ConditionValue xgetCValueRuleId() {
        return _myCQ.xdfgetRuleId();
    }

    @Override
    protected ConditionValue xgetCValueUrl() {
        return _myCQ.xdfgetUrl();
    }

    @Override
    protected ConditionValue xgetCValueParentUrl() {
        return _myCQ.xdfgetParentUrl();
    }

    @Override
    protected ConditionValue xgetCValueStatus() {
        return _myCQ.xdfgetStatus();
    }

    @Override
    protected ConditionValue xgetCValueHttpStatusCode() {
        return _myCQ.xdfgetHttpStatusCode();
    }

    @Override
    protected ConditionValue xgetCValueMethod() {
        return _myCQ.xdfgetMethod();
    }

    @Override
    protected ConditionValue xgetCValueMimeType() {
        return _myCQ.xdfgetMimeType();
    }

    @Override
    protected ConditionValue xgetCValueContentLength() {
        return _myCQ.xdfgetContentLength();
    }

    @Override
    protected ConditionValue xgetCValueExecutionTime() {
        return _myCQ.xdfgetExecutionTime();
    }

    @Override
    protected ConditionValue xgetCValueLastModified() {
        return _myCQ.xdfgetLastModified();
    }

    @Override
    protected ConditionValue xgetCValueCreateTime() {
        return _myCQ.xdfgetCreateTime();
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String pp) {
        return null;
    }

    @Override
    public String keepScalarCondition(final AccessResultCQ sq) {
        throwIICBOE("ScalarCondition");
        return null;
    }

    @Override
    public String keepSpecifyMyselfDerived(final AccessResultCQ sq) {
        throwIICBOE("(Specify)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerived(final AccessResultCQ sq) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object vl) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepMyselfExists(final AccessResultCQ sq) {
        throwIICBOE("MyselfExists");
        return null;
    }

    protected void throwIICBOE(final String name) {
        throw new IllegalConditionBeanOperationException(name
                + " at InlineView is unsupported.");
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xinCB() {
        return AccessResultCB.class.getName();
    }

    protected String xinCQ() {
        return AccessResultCQ.class.getName();
    }
}
