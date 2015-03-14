package org.codelibs.robot.db.cbean.nss;

import org.codelibs.robot.db.cbean.cq.AccessResultDataCQ;

/**
 * The nest select set-upper of ACCESS_RESULT_DATA.
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDataNss {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final AccessResultDataCQ _query;
    public AccessResultDataNss(AccessResultDataCQ query) { _query = query; }
    public boolean hasConditionQuery() { return _query != null; }

    // ===================================================================================
    //                                                                     Nested Relation
    //                                                                     ===============
    /**
     * With nested relation columns to select clause. <br>
     * ACCESS_RESULT by my ID, named 'accessResult'.
     * @return The set-upper of more nested relation. {...with[nested-relation].with[more-nested-relation]} (NotNull)
     */
    public AccessResultNss withAccessResult() {
        _query.xdoNss(() -> _query.queryAccessResult());
        return new AccessResultNss(_query.queryAccessResult());
    }
}
