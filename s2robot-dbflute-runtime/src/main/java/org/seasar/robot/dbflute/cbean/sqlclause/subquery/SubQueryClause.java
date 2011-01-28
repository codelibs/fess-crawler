package org.seasar.robot.dbflute.cbean.sqlclause.subquery;

import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.robot.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/20 Sunday)
 */
public class SubQueryClause {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final SubQueryPath _subQueryPath;
    protected final String _selectClause; // needed for union
    protected final SqlClause _subQuerySqlClause;
    protected final String _localAliasName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param subQueryPath The property path of sub-query. (NotNull)
     * @param selectClause The select clause of sub-query. (NotNull)
     * @param subQuerySqlClause The SQL clause for sub-query. (NotNull)
     * @param localAliasName The alias name of sub-query local table. (NullAllowed: if plain)
     */
    public SubQueryClause(SubQueryPath subQueryPath, String selectClause, SqlClause subQuerySqlClause,
            String localAliasName) {
        _subQueryPath = subQueryPath;
        _selectClause = selectClause;
        _subQuerySqlClause = subQuerySqlClause;
        _localAliasName = localAliasName;
    }

    // ===================================================================================
    //                                                                               Plain
    //                                                                               =====
    public String buildPlainSubQueryFromWhereClause() {
        String clause = _subQuerySqlClause.getClauseFromWhereWithUnionTemplate();
        clause = resolveParameterLocationPath(clause, _subQueryPath);
        clause = replaceString(clause, getUnionSelectClauseMark(), _selectClause);
        clause = replaceString(clause, getUnionWhereClauseMark(), "");
        clause = replaceString(clause, getUnionWhereFirstConditionMark(), "");
        return clause;
    }

    // ===================================================================================
    //                                                                         Correlation
    //                                                                         ===========
    /**
     * Build the clause of correlation sub-query from from-where clause.
     * @param correlatedColumnRealName The real name of correlated column that is main-query table's column. (NotNull)
     * @param relatedColumnSqlName The real name of related column that is sub-query table's column. (NotNull)
     * @return The clause string of correlation sub-query. (NotNull)
     */
    public String buildCorrelationSubQueryFromWhereClause(ColumnRealName correlatedColumnRealName,
            ColumnSqlName relatedColumnSqlName) {
        final String clause = xprepareCorrelationSubQueryFromWhereClause();
        final String joinCondition = _localAliasName + "." + relatedColumnSqlName + " = " + correlatedColumnRealName;
        return xreplaceCorrelationSubQueryFromWhereClause(clause, joinCondition);
    }

    /**
     * Build the clause of correlation sub-query from from-where clause.
     * @param correlatedColumnRealNames The real names of correlated column that is main-query table's column. (NotNull)
     * @param relatedColumnSqlNames The real names of related column that is sub-query table's column. (NotNull)
     * @return The clause string of correlation sub-query. (NotNull)
     */
    public String buildCorrelationSubQueryFromWhereClause(ColumnRealName[] correlatedColumnRealNames,
            ColumnSqlName[] relatedColumnSqlNames) {
        String clause = xprepareCorrelationSubQueryFromWhereClause();

        final String joinCondition;
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < relatedColumnSqlNames.length; i++) {
            if (sb.length() > 0) {
                sb.append(ln()).append("   and ");
            }
            sb.append(_localAliasName).append(".").append(relatedColumnSqlNames[i]);
            sb.append(" = ").append(correlatedColumnRealNames[i]);
        }
        joinCondition = sb.toString();

        clause = xreplaceCorrelationSubQueryFromWhereClause(clause, joinCondition);
        return clause;
    }

    protected String xprepareCorrelationSubQueryFromWhereClause() {
        final String clause = _subQuerySqlClause.getClauseFromWhereWithWhereUnionTemplate();
        return resolveParameterLocationPath(clause, _subQueryPath);
    }

    protected String xreplaceCorrelationSubQueryFromWhereClause(String clause, String joinCondition) {
        final String firstConditionAfter = ln() + "   and ";
        clause = replaceString(clause, getWhereClauseMark(), ln() + " where " + joinCondition);
        clause = replaceString(clause, getWhereFirstConditionMark(), joinCondition + firstConditionAfter);
        clause = replaceString(clause, getUnionSelectClauseMark(), _selectClause);
        clause = replaceString(clause, getUnionWhereClauseMark(), ln() + " where " + joinCondition);
        clause = replaceString(clause, getUnionWhereFirstConditionMark(), joinCondition + firstConditionAfter);
        return clause;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String resolveParameterLocationPath(String clause, SubQueryPath subQueryPath) {
        return subQueryPath.resolveParameterLocationPath(clause);
    }

    // ===================================================================================
    //                                                                          Alias Name
    //                                                                          ==========
    protected String getBasePointAliasName() {
        return _subQuerySqlClause.getBasePointAliasName();
    }

    // ===================================================================================
    //                                                                       Template Mark
    //                                                                       =============
    protected String getWhereClauseMark() {
        return _subQuerySqlClause.getWhereClauseMark();
    }

    protected String getWhereFirstConditionMark() {
        return _subQuerySqlClause.getWhereFirstConditionMark();
    }

    protected String getUnionSelectClauseMark() {
        return _subQuerySqlClause.getUnionSelectClauseMark();
    }

    protected String getUnionWhereClauseMark() {
        return _subQuerySqlClause.getUnionWhereClauseMark();
    }

    protected String getUnionWhereFirstConditionMark() {
        return _subQuerySqlClause.getUnionWhereFirstConditionMark();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected final String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected String initCap(String str) {
        return Srl.initCap(str);
    }

    protected String initUncap(String str) {
        return Srl.initUncap(str);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
