package org.codelibs.robot.db.cbean.cq.ciq;

import java.util.Map;

import org.codelibs.robot.db.cbean.UrlQueueCB;
import org.codelibs.robot.db.cbean.cq.UrlQueueCQ;
import org.codelibs.robot.db.cbean.cq.bs.AbstractBsUrlQueueCQ;
import org.codelibs.robot.db.cbean.cq.bs.BsUrlQueueCQ;
import org.dbflute.cbean.ConditionQuery;
import org.dbflute.cbean.ckey.ConditionKey;
import org.dbflute.cbean.coption.ConditionOption;
import org.dbflute.cbean.cvalue.ConditionValue;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The condition-query for in-line of URL_QUEUE.
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
    public UrlQueueCIQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel, final BsUrlQueueCQ myCQ) {
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
    protected ConditionValue xgetCValueMethod() {
        return _myCQ.xdfgetMethod();
    }

    @Override
    protected ConditionValue xgetCValueUrl() {
        return _myCQ.xdfgetUrl();
    }

    @Override
    protected ConditionValue xgetCValueMetaData() {
        return _myCQ.xdfgetMetaData();
    }

    @Override
    protected ConditionValue xgetCValueEncoding() {
        return _myCQ.xdfgetEncoding();
    }

    @Override
    protected ConditionValue xgetCValueParentUrl() {
        return _myCQ.xdfgetParentUrl();
    }

    @Override
    protected ConditionValue xgetCValueDepth() {
        return _myCQ.xdfgetDepth();
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
    public String keepScalarCondition(final UrlQueueCQ sq) {
        throwIICBOE("ScalarCondition");
        return null;
    }

    @Override
    public String keepSpecifyMyselfDerived(final UrlQueueCQ sq) {
        throwIICBOE("(Specify)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerived(final UrlQueueCQ sq) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object vl) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepMyselfExists(final UrlQueueCQ sq) {
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
        return UrlQueueCB.class.getName();
    }

    protected String xinCQ() {
        return UrlQueueCQ.class.getName();
    }
}
