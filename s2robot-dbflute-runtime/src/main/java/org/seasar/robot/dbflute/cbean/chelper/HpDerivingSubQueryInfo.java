package org.seasar.robot.dbflute.cbean.chelper;

import org.seasar.robot.dbflute.cbean.sqlclause.subquery.DerivedReferrer;

/**
 * @author jflute
 */
public class HpDerivingSubQueryInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _aliasName;
    protected String _derivingSubQuery;
    protected DerivedReferrer _derivedReferrer;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpDerivingSubQueryInfo(String aliasName, String derivingSubQuery, DerivedReferrer derivedReferrer) {
        this._aliasName = aliasName;
        this._derivingSubQuery = derivingSubQuery;
        this._derivedReferrer = derivedReferrer;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getAliasName() {
        return _aliasName;
    }

    public String getDerivingSubQuery() {
        return _derivingSubQuery;
    }

    public DerivedReferrer getDerivedReferrer() {
        return _derivedReferrer;
    }
}
