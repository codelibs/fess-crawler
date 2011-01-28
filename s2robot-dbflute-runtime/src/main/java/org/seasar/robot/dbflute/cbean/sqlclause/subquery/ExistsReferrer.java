package org.seasar.robot.dbflute.cbean.sqlclause.subquery;

import java.util.List;

import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.robot.dbflute.dbmeta.name.ColumnRealNameProvider;
import org.seasar.robot.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.robot.dbflute.dbmeta.name.ColumnSqlNameProvider;
import org.seasar.robot.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/20 Sunday)
 */
public class ExistsReferrer extends AbstractSubQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ExistsReferrer(SubQueryPath subQueryPath, ColumnRealNameProvider localRealNameProvider,
            ColumnSqlNameProvider subQuerySqlNameProvider, int subQueryLevel, SqlClause subQuerySqlClause,
            String subQueryIdentity, DBMeta subQueryDBMeta) {
        super(subQueryPath, localRealNameProvider, subQuerySqlNameProvider, subQueryLevel, subQuerySqlClause,
                subQueryIdentity, subQueryDBMeta);
    }

    // ===================================================================================
    //                                                                        Build Clause
    //                                                                        ============
    /**
     * Build the clause of sub-query by single primary key.
     * @param correlatedColumnDbName The DB name of correlated column that is main-query table's column. (NotNull)
     * @param relatedColumnDbName The DB name of related column that is sub-query table's column. (NotNull)
     * @param existsOption The option of ExistsReferrer. (basically for NotExistsReferrer) (NullAllowed)
     * @return The clause of sub-query. (NotNull)
     */
    public String buildExistsReferrer(String correlatedColumnDbName, String relatedColumnDbName, String existsOption) {
        existsOption = existsOption != null ? existsOption + " " : "";
        final String subQueryClause;
        if (correlatedColumnDbName.contains(",") && relatedColumnDbName.contains(",")) {
            // compound primary keys
            final List<String> relatedColumnSplit = Srl.splitList(relatedColumnDbName, ",");
            final ColumnSqlName[] relatedColumnSqlNames = new ColumnSqlName[relatedColumnSplit.size()];
            for (int i = 0; i < relatedColumnSplit.size(); i++) {
                relatedColumnSqlNames[i] = _subQuerySqlNameProvider.provide(relatedColumnSplit.get(i).trim());
            }
            final List<String> columnDbNameSplit = Srl.splitList(correlatedColumnDbName, ",");
            final ColumnRealName[] correlatedColumnRealNames = new ColumnRealName[columnDbNameSplit.size()];
            for (int i = 0; i < columnDbNameSplit.size(); i++) {
                correlatedColumnRealNames[i] = _localRealNameProvider.provide(columnDbNameSplit.get(i).trim());
            }
            subQueryClause = getSubQueryClause(correlatedColumnRealNames, relatedColumnSqlNames);
        } else {
            // single primary key
            final ColumnSqlName relatedColumnSqlName = _subQuerySqlNameProvider.provide(relatedColumnDbName);
            final ColumnRealName correlatedColumnRealName = _localRealNameProvider.provide(correlatedColumnDbName);
            subQueryClause = getSubQueryClause(correlatedColumnRealName, relatedColumnSqlName);
        }
        final String beginMark = resolveSubQueryBeginMark(_subQueryIdentity) + ln();
        final String endMark = resolveSubQueryEndMark(_subQueryIdentity);
        final String endIndent = "       ";
        return existsOption + "exists (" + beginMark + subQueryClause + ln() + endIndent + ")" + endMark;
    }

    /**
     * Build the clause of sub-query by single primary key.
     * @param correlatedColumnRealName The real name of correlated column that is main-query table's column. (NotNull)
     * @param relatedColumnSqlName The real name of related column that is sub-query table's column. (NotNull)
     * @return The clause of sub-query. (NotNull)
     */
    protected String getSubQueryClause(ColumnRealName correlatedColumnRealName, ColumnSqlName relatedColumnSqlName) {
        final String localAliasName = getSubQueryLocalAliasName();
        final String selectClause;
        {
            final ColumnRealName relatedColumnRealName = new ColumnRealName(localAliasName, relatedColumnSqlName);
            selectClause = "select " + relatedColumnRealName;
        }
        final String fromWhereClause = buildCorrelationFromWhereClause(selectClause, localAliasName,
                correlatedColumnRealName, relatedColumnSqlName);
        final String subQueryClause = selectClause + " " + fromWhereClause;
        return resolveSubQueryLevelVariable(subQueryClause);
    }

    /**
     * Build the clause of sub-query by compound primary key.
     * @param correlatedColumnRealNames The real names of correlated column that is main-query table's column. (NotNull)
     * @param relatedColumnSqlNames The real names of related column that is sub-query table's column. (NotNull)
     * @return The clause of sub-query. (NotNull)
     */
    protected String getSubQueryClause(ColumnRealName[] correlatedColumnRealNames, ColumnSqlName[] relatedColumnSqlNames) {
        final String localAliasName = getSubQueryLocalAliasName();
        final String selectClause;
        {
            // because sub-query may be only allowed to return a single column.
            final ColumnRealName relatedColumnRealName = new ColumnRealName(localAliasName, relatedColumnSqlNames[0]);
            selectClause = "select " + relatedColumnRealName;
        }
        final String fromWhereClause = buildCorrelationFromWhereClause(selectClause, localAliasName,
                correlatedColumnRealNames, relatedColumnSqlNames);
        final String subQueryClause = selectClause + " " + fromWhereClause;
        return resolveSubQueryLevelVariable(subQueryClause);
    }
}
