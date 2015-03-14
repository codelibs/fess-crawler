package org.codelibs.robot.db.cbean.nss;

import org.codelibs.robot.db.cbean.cq.AccessResultCQ;

/**
 * The nest select set-upper of ACCESS_RESULT.
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultNss {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final AccessResultCQ _query;
    public AccessResultNss(AccessResultCQ query) { _query = query; }
    public boolean hasConditionQuery() { return _query != null; }

    // ===================================================================================
    //                                                                     Nested Relation
    //                                                                     ===============
    /**
     * With nested relation columns to select clause. <br>
     * ACCESS_RESULT_DATA by ID, named 'accessResultDataAsOne'.
     * @return The set-upper of more nested relation. {...with[nested-relation].with[more-nested-relation]} (NotNull)
     */
    public AccessResultDataNss withAccessResultDataAsOne() {
        _query.xdoNss(() -> _query.queryAccessResultDataAsOne());
        return new AccessResultDataNss(_query.queryAccessResultDataAsOne());
    }
}
