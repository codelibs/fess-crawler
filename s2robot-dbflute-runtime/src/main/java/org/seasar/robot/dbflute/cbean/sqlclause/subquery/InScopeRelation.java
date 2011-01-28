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
public class InScopeRelation extends AbstractSubQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _suppressLocalAliasName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public InScopeRelation(SubQueryPath subQueryPath, ColumnRealNameProvider localRealNameProvider,
            ColumnSqlNameProvider subQuerySqlNameProvider, int subQueryLevel, SqlClause subQuerySqlClause,
            String subQueryIdentity, DBMeta subQueryDBMeta, boolean suppressLocalAliasName) {
        super(subQueryPath, localRealNameProvider, subQuerySqlNameProvider, subQueryLevel, subQuerySqlClause,
                subQueryIdentity, subQueryDBMeta);
        _suppressLocalAliasName = suppressLocalAliasName;
    }

    // ===================================================================================
    //                                                                        Build Clause
    //                                                                        ============
    public String buildInScopeRelation(String columnDbName, String relatedColumnDbName, String inScopeOption) {
        inScopeOption = inScopeOption != null ? inScopeOption + " " : "";
        final String subQueryClause;
        {
            final ColumnSqlName relatedColumnSqlName = _subQuerySqlNameProvider.provide(relatedColumnDbName);
            subQueryClause = getSubQueryClause(relatedColumnSqlName);
        }
        final String beginMark = resolveSubQueryBeginMark(_subQueryIdentity) + ln();
        final String endMark = resolveSubQueryEndMark(_subQueryIdentity);
        final String endIndent = "       ";
        final ColumnRealName columnRealName;
        {
            final ColumnRealName localRealName = _localRealNameProvider.provide(columnDbName);
            if (_suppressLocalAliasName) {
                columnRealName = new ColumnRealName(null, localRealName.getColumnSqlName());
            } else {
                columnRealName = localRealName;
            }
        }
        return columnRealName + " " + inScopeOption + "in (" + beginMark + subQueryClause + ln() + endIndent + ")"
                + endMark;
    }

    protected String getSubQueryClause(ColumnSqlName relatedColumnSqlName) {
        final String tableAliasName = getSubQueryLocalAliasName();
        final String selectClause;
        {
            final ColumnRealName relatedColumnRealName = new ColumnRealName(tableAliasName, relatedColumnSqlName);
            selectClause = "select " + relatedColumnRealName;
        }
        final String fromWhereClause = buildPlainFromWhereClause(selectClause, tableAliasName);
        final String subQueryClause = selectClause + " " + fromWhereClause;
        return resolveSubQueryLevelVariable(subQueryClause);
    }
}
