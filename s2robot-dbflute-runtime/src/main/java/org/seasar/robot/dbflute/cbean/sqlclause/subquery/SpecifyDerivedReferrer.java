package org.seasar.robot.dbflute.cbean.sqlclause.subquery;

import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.robot.dbflute.dbmeta.name.ColumnRealNameProvider;
import org.seasar.robot.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.robot.dbflute.dbmeta.name.ColumnSqlNameProvider;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/20 Sunday)
 */
public class SpecifyDerivedReferrer extends DerivedReferrer {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The alias name for derived column. (NullAllowed: if null, means no alias expression) */
    protected final String _aliasName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public SpecifyDerivedReferrer(SubQueryPath subQueryPath, ColumnRealNameProvider localRealNameProvider,
            ColumnSqlNameProvider subQuerySqlNameProvider, int subQueryLevel, SqlClause subQuerySqlClause,
            String subQueryIdentity, DBMeta subQueryDBMeta, String mainSubQueryIdentity, String aliasName) {
        super(subQueryPath, localRealNameProvider, subQuerySqlNameProvider, subQueryLevel, subQuerySqlClause,
                subQueryIdentity, subQueryDBMeta, mainSubQueryIdentity);
        _aliasName = aliasName;
    }

    // ===================================================================================
    //                                                                        Build Clause
    //                                                                        ============
    @Override
    protected String doBuildDerivedReferrer(String function, ColumnRealName columnRealName,
            ColumnSqlName relatedColumnSqlName, String subQueryClause, String beginMark, String endMark,
            String endIndent) {
        final String aliasExp = _aliasName != null ? " as " + _aliasName : "";
        return "(" + beginMark + subQueryClause + ln() + endIndent + ")" + aliasExp + endMark;
    }

    @Override
    protected void throwDerivedReferrerInvalidColumnSpecificationException(String function) {
        createCBExThrower().throwSpecifyDerivedReferrerInvalidColumnSpecificationException(function, _aliasName);
    }

    @Override
    protected void doAssertDerivedReferrerColumnType(String function, String derivedColumnDbName,
            Class<?> derivedColumnType) {
        if ("sum".equalsIgnoreCase(function) || "avg".equalsIgnoreCase(function)) {
            if (!Number.class.isAssignableFrom(derivedColumnType)) {
                throwSpecifyDerivedReferrerUnmatchedColumnTypeException(function, derivedColumnDbName,
                        derivedColumnType);
            }
        }
    }

    protected void throwSpecifyDerivedReferrerUnmatchedColumnTypeException(String function, String derivedColumnDbName,
            Class<?> derivedColumnType) {
        createCBExThrower().throwSpecifyDerivedReferrerUnmatchedColumnTypeException(function, derivedColumnDbName,
                derivedColumnType);
    }
}
