package org.seasar.robot.db.cbean.cq;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.db.cbean.cq.bs.BsAccessResultDataCQ;

/**
 * The condition-query of ACCESS_RESULT_DATA.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDataCQ extends BsAccessResultDataCQ {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param childQuery Child query as abstract class. (Nullable: If null, this is base instance.)
     * @param sqlClause SQL clause instance. (NotNull)
     * @param aliasName My alias name. (NotNull)
     * @param nestLevel Nest level.
     */
    public AccessResultDataCQ(ConditionQuery childQuery, SqlClause sqlClause,
            String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                      Arrange Method
    //                                                                      ==============
    // You can make original arrange query methods here.
    // public void arranegeXxx() {
    //     ...
    // }
}
