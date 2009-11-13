/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.robot.dbflute.cbean.sqlclause;

import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;

/**
 * SqlClause for Oracle.
 * @author jflute
 */
public class SqlClauseOracle extends AbstractSqlClause {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** String of fetch-scope as select-hint. */
    protected String _fetchScopeSelectHint = "";

    /** String of fetch-scope as sql-suffix. */
    protected String _fetchScopeSqlSuffix = "";

    /** String of lock as sql-suffix. */
    protected String _lockSqlSuffix = "";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param tableName Table name. (NotNull)
     **/
    public SqlClauseOracle(String tableName) {
        super(tableName);
    }

    // ===================================================================================
    //                                                          Database Original Override
    //                                                          ==========================
    @Override
    protected String buildUnionClause(String selectClause) {

        // - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // Remove select-hint comment from select clause of union
        // for fetch-scope with union().
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - -

        selectClause = replaceString(selectClause, SELECT_HINT, "");
        return super.buildUnionClause(selectClause);
    }

    // ===================================================================================
    //                                                                 FetchScope Override
    //                                                                 ===================
    /**
     * {@inheritDoc}
     */
    protected void doFetchFirst() {
        doFetchPage();
    }

    /**
     * {@inheritDoc}
     */
    protected void doFetchPage() {
        if (!isFetchStartIndexSupported() && !isFetchSizeSupported()) {
            return;
        }
        String ln = ln();
        _fetchScopeSelectHint = " * from (select base.*, rownum as rn from (" + ln + "select";
        _fetchScopeSqlSuffix = "";
        if (isFetchStartIndexSupported()) {
            _fetchScopeSqlSuffix = ") base )" + ln + " where rn > " + getPageStartIndex();
        }
        if (isFetchSizeSupported()) {
            if (isFetchStartIndexSupported()) {
                _fetchScopeSqlSuffix = _fetchScopeSqlSuffix + " and rn <= " + getPageEndIndex();
            } else {
                _fetchScopeSqlSuffix = ") base )" + ln + " where rn <= " + getPageEndIndex();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void doClearFetchPageClause() {
        _fetchScopeSelectHint = "";
        _fetchScopeSqlSuffix = "";
    }

    // ===================================================================================
    //                                                                       Lock Override
    //                                                                       =============
    /**
     * {@inheritDoc}
     * @return this. (NotNull)
     */
    public SqlClause lockForUpdate() {
        final DBMeta dbmeta = findDBMeta(_tableName);
        if (dbmeta.hasPrimaryKey()) {
            final String primaryKeyColumnName = dbmeta.getPrimaryUniqueInfo().getFirstColumn().getColumnDbName();
            _lockSqlSuffix = " for update of " + getLocalTableAliasName() + "." + primaryKeyColumnName;
        } else {
            final String randomColumnName = ((ColumnInfo) dbmeta.getColumnInfoList().get(0)).getColumnDbName();
            _lockSqlSuffix = " for update of " + getLocalTableAliasName() + "." + randomColumnName;
        }
        return this;
    }

    // ===================================================================================
    //                                                                       Hint Override
    //                                                                       =============
    /**
     * {@inheritDoc}
     * @return Select-hint. (NotNull)
     */
    protected String createSelectHint() {
        return _fetchScopeSelectHint;
    }

    /**
     * {@inheritDoc}
     * @return From-base-table-hint. {select * from table [from-base-table-hint] where ...} (NotNull)
     */
    protected String createFromBaseTableHint() {
        return "";
    }

    /**
     * {@inheritDoc}
     * @return From-hint. (NotNull)
     */
    protected String createFromHint() {
        return "";
    }

    /**
     * {@inheritDoc}
     * @return Sql-suffix. (NotNull)
     */
    protected String createSqlSuffix() {
        return _fetchScopeSqlSuffix + _lockSqlSuffix;
    }

    // ===================================================================================
    //                                                                 Database Dependency
    //                                                                 ===================
    public SqlClause lockForUpdateNoWait() {
        lockForUpdate();
        _lockSqlSuffix = _lockSqlSuffix + " nowait";
        return this;
    }

    public SqlClause lockForUpdateWait(int waitSec) {
        lockForUpdate();
        _lockSqlSuffix = _lockSqlSuffix + " wait " + waitSec;
        return this;
    }

    // [DBFlute-0.9.4]
    // ===================================================================================
    //                                                                       InScope Limit
    //                                                                       =============
    @Override
    public int getInScopeLimit() {
        return 1000;
    }

    // [DBFlute-0.9.5]
    // ===================================================================================
    //                                                                    Full-Text Search
    //                                                                    ================
    public WhereClauseArranger createFullTextSearchClauseArranger() {
        return new FullTextSearchClauseArranger();
    }

    protected static class FullTextSearchClauseArranger implements WhereClauseArranger {
        public String arrange(String columnName, String operand, String bindExpression, String rearOption) {
            return "contains(" + columnName + ", " + bindExpression + ") > 0";
        }
    }

    public String escapeFullTextSearchValue(String conditionValue) {
        if (conditionValue.contains("}")) {
            conditionValue = replaceString(conditionValue, "}", "}}");
        }
        conditionValue = "{" + conditionValue + "}";
        return conditionValue;
    }
}
