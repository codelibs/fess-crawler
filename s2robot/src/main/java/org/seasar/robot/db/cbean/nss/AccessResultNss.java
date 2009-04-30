package org.seasar.robot.db.cbean.nss;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.robot.db.cbean.cq.AccessResultCQ;

/**
 * The nest select set-upper of ACCESS_RESULT.
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultNss {

    protected AccessResultCQ _query;
    public AccessResultNss(AccessResultCQ query) { _query = query; }
    public boolean hasConditionQuery() { return _query != null; }

    // ===================================================================================
    //                                                           With Nested Foreign Table
    //                                                           =========================

    // ===================================================================================
    //                                                          With Nested Referrer Table
    //                                                          ==========================
    public AccessResultDataNss withAccessResultDataAsOne() {
        _query.doNss(new AccessResultCQ.NssCall() { public ConditionQuery qf() { return _query.queryAccessResultDataAsOne(); }});
		return new AccessResultDataNss(_query.queryAccessResultDataAsOne());
    }
}
