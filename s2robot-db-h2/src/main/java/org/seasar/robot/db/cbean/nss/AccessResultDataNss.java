package org.seasar.robot.db.cbean.nss;

import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.db.cbean.cq.AccessResultDataCQ;

/**
 * The nest select set-upper of ACCESS_RESULT_DATA.
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDataNss {

    protected AccessResultDataCQ _query;

    public AccessResultDataNss(AccessResultDataCQ query) {
        _query = query;
    }

    public boolean hasConditionQuery() {
        return _query != null;
    }

    // ===================================================================================
    //                                                           With Nested Foreign Table
    //                                                           =========================
    public AccessResultNss withAccessResult() {
        _query.doNss(new AccessResultDataCQ.NssCall() {
            public ConditionQuery qf() {
                return _query.queryAccessResult();
            }
        });
        return new AccessResultNss(_query.queryAccessResult());
    }

    // ===================================================================================
    //                                                          With Nested Referrer Table
    //                                                          ==========================
}
