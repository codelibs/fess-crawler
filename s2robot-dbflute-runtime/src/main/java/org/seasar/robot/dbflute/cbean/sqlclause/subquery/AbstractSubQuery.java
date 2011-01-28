package org.seasar.robot.dbflute.cbean.sqlclause.subquery;

import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.robot.dbflute.dbmeta.name.ColumnRealNameProvider;
import org.seasar.robot.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.robot.dbflute.dbmeta.name.ColumnSqlNameProvider;
import org.seasar.robot.dbflute.exception.thrower.ConditionBeanExceptionThrower;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/20 Sunday)
 */
public abstract class AbstractSubQuery {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final SubQueryPath _subQueryPath;
    protected final ColumnRealNameProvider _localRealNameProvider;
    protected final ColumnSqlNameProvider _subQuerySqlNameProvider;
    protected final int _subQueryLevel;
    protected final SqlClause _subQuerySqlClause;
    protected final String _subQueryIdentity;
    protected final DBMeta _subQueryDBMeta;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param subQueryPath The property path of sub-query. (NotNull)
     * @param localRealNameProvider The provider of column real name for local table. (NotNull)
     * @param subQuerySqlNameProvider The provider of column real name for sub-query. (NotNull)
     * @param subQueryLevel The sub-query level for sub-query.
     * @param subQuerySqlClause The SQL clause for sub-query. (NotNull)
     * @param subQueryIdentity The identity string for sub-query. (NotNull)
     * @param subQueryDBMeta The DB meta for sub-query. (NotNull)
     */
    public AbstractSubQuery(SubQueryPath subQueryPath, ColumnRealNameProvider localRealNameProvider,
            ColumnSqlNameProvider subQuerySqlNameProvider, int subQueryLevel, SqlClause subQuerySqlClause,
            String subQueryIdentity, DBMeta subQueryDBMeta) {
        _subQueryPath = subQueryPath;
        _localRealNameProvider = localRealNameProvider;
        _subQuerySqlNameProvider = subQuerySqlNameProvider;
        _subQueryLevel = subQueryLevel;
        _subQuerySqlClause = subQuerySqlClause;
        _subQueryIdentity = subQueryIdentity;
        _subQueryDBMeta = subQueryDBMeta;
    }

    // ===================================================================================
    //                                                                          Alias Name
    //                                                                          ==========
    protected String getSubQueryLocalAliasName() {
        return _subQuerySqlClause.getBasePointAliasName();
    }

    protected String buildSubQueryMainAliasName() {
        return "sub" + _subQueryLevel + "main";
    }

    protected String resolveSubQueryLevelVariable(String subQueryClause) {
        return replace(subQueryClause, "${subQueryLevel}", String.valueOf(_subQueryLevel));
    }

    // ===================================================================================
    //                                                                        Build Clause
    //                                                                        ============
    /**
     * Build the clause of plain sub-query from from-where clause.
     * @param selectClause The clause of select for sub-query. (NotNull)
     * @param localAliasName The alias name of sub-query local table. (NotNull)
     * @return The clause string of plain sub-query. (NotNull)
     */
    protected String buildPlainFromWhereClause(String selectClause, String localAliasName) {
        final SubQueryClause clause = createSubQueryClause(selectClause, localAliasName);
        return clause.buildPlainSubQueryFromWhereClause();
    }

    /**
     * Build the clause of correlation sub-query from from-where clause.
     * @param selectClause The clause of select for sub-query. (NotNull)
     * @param localAliasName The alias name of sub-query local table. (NotNull)
     * @param correlatedColumnRealName The real name of correlated column that is main-query table's column. (NotNull)
     * @param relatedColumnSqlName The real name of related column that is sub-query table's column. (NotNull)
     * @return The clause string of correlation sub-query. (NotNull)
     */
    protected String buildCorrelationFromWhereClause(String selectClause, String localAliasName,
            ColumnRealName correlatedColumnRealName, ColumnSqlName relatedColumnSqlName) {
        final SubQueryClause clause = createSubQueryClause(selectClause, localAliasName);
        return clause.buildCorrelationSubQueryFromWhereClause(correlatedColumnRealName, relatedColumnSqlName);
    }

    /**
     * Build the clause of correlation sub-query from from-where clause.
     * @param selectClause The clause of select for sub-query. (NotNull)
     * @param localAliasName The alias name of sub-query local table. (NotNull)
     * @param correlatedColumnRealNames The real names of correlated column that is main-query table's column. (NotNull)
     * @param relatedColumnSqlNames The real names of related column that is sub-query table's column. (NotNull)
     * @return The clause string of correlation sub-query. (NotNull)
     */
    protected String buildCorrelationFromWhereClause(String selectClause, String localAliasName,
            ColumnRealName[] correlatedColumnRealNames, ColumnSqlName[] relatedColumnSqlNames) {
        final SubQueryClause clause = createSubQueryClause(selectClause, localAliasName);
        return clause.buildCorrelationSubQueryFromWhereClause(correlatedColumnRealNames, relatedColumnSqlNames);
    }

    protected SubQueryClause createSubQueryClause(String selectClause, String localAliasName) {
        return new SubQueryClause(_subQueryPath, selectClause, _subQuerySqlClause, localAliasName);
    }

    // ===================================================================================
    //                                                                     Indent Resolver
    //                                                                     ===============
    protected String resolveSubQueryBeginMark(String subQueryIdentity) {
        return _subQuerySqlClause.resolveSubQueryBeginMark(subQueryIdentity);
    }

    protected String resolveSubQueryEndMark(String subQueryIdentity) {
        return _subQuerySqlClause.resolveSubQueryEndMark(subQueryIdentity);
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected ConditionBeanExceptionThrower createCBExThrower() {
        return new ConditionBeanExceptionThrower();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected final String replace(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected final String initCap(String str) {
        return Srl.initCap(str);
    }

    protected final String initUncap(String str) {
        return Srl.initUncap(str);
    }

    protected final String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public SubQueryPath getSubQueryPath() {
        return _subQueryPath;
    }

    public int getSubQueryLevel() {
        return _subQueryLevel;
    }

    public SqlClause getSubQuerySqlClause() {
        return _subQuerySqlClause;
    }
}
