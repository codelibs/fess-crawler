package org.codelibs.robot.db.cbean.cq.ciq;

import java.util.Map;

import org.codelibs.robot.db.cbean.UrlFilterCB;
import org.codelibs.robot.db.cbean.cq.UrlFilterCQ;
import org.codelibs.robot.db.cbean.cq.bs.AbstractBsUrlFilterCQ;
import org.codelibs.robot.db.cbean.cq.bs.BsUrlFilterCQ;
import org.dbflute.cbean.ConditionQuery;
import org.dbflute.cbean.ckey.ConditionKey;
import org.dbflute.cbean.coption.ConditionOption;
import org.dbflute.cbean.cvalue.ConditionValue;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The condition-query for in-line of URL_FILTER.
 * @author DBFlute(AutoGenerator)
 */
public class UrlFilterCIQ extends AbstractBsUrlFilterCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsUrlFilterCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public UrlFilterCIQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel, final BsUrlFilterCQ myCQ) {
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
    protected ConditionValue xgetCValueUrl() {
        return _myCQ.xdfgetUrl();
    }

    @Override
    protected ConditionValue xgetCValueFilterType() {
        return _myCQ.xdfgetFilterType();
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
    public String keepScalarCondition(final UrlFilterCQ sq) {
        throwIICBOE("ScalarCondition");
        return null;
    }

    @Override
    public String keepSpecifyMyselfDerived(final UrlFilterCQ sq) {
        throwIICBOE("(Specify)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerived(final UrlFilterCQ sq) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object vl) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepMyselfExists(final UrlFilterCQ sq) {
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
        return UrlFilterCB.class.getName();
    }

    protected String xinCQ() {
        return UrlFilterCQ.class.getName();
    }
}
