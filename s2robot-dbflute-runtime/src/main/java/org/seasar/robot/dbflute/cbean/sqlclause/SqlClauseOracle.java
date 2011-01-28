/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import org.seasar.robot.dbflute.cbean.sqlclause.query.QueryClauseArranger;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.robot.dbflute.dbmeta.name.ColumnSqlName;

/**
 * SqlClause for Oracle.
 * @author jflute
 */
public class SqlClauseOracle extends AbstractSqlClause {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** String of fetch-scope as select-hint. */
    protected String _fetchScopeSelectHint = "";

    /** String of fetch-scope as sql-suffix. */
    protected String _fetchScopeSqlSuffix = "";

    /** String of lock as sql-suffix. */
    protected String _lockSqlSuffix = "";

    /** The bind value for paging as 'from'. */
    protected Integer _pagingBindFrom;

    /** The bind value for paging as 'to'. */
    protected Integer _pagingBindTo;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param tableDbName The DB name of table. (NotNull)
     **/
    public SqlClauseOracle(String tableDbName) {
        super(tableDbName);
    }

    // ===================================================================================
    //                                                                Main Clause Override
    //                                                                ====================
    @Override
    protected String prepareUnionClause(String selectClause) {
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // Remove select-hint comment from select clause of union
        // for fetch-scope with union().
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        selectClause = replace(selectClause, SELECT_HINT, "");
        return super.prepareUnionClause(selectClause);
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
        final RownumPagingProcessor processor = createRownumPagingProcessor(getRownumExpression());
        processor.processRowNumberPaging();
        _fetchScopeSelectHint = processor.getSelectHint();
        _fetchScopeSqlSuffix = processor.getSqlSuffix();
        _pagingBindFrom = processor.getPagingBindFrom();
        _pagingBindTo = processor.getPagingBindTo();
    }

    protected RownumPagingProcessor createRownumPagingProcessor(String expression) {
        final RownumPagingProcessor processor = new RownumPagingProcessor(expression);
        if (isBindPagingCondition()) {
            processor.useBindVariable();
        }
        return processor;
    }

    protected boolean isBindPagingCondition() {
        return true; // as default
    }

    protected String getRownumExpression() {
        return "rownum";
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
     */
    public SqlClause lockForUpdate() {
        final DBMeta dbmeta = findDBMeta(_tableDbName);
        final String basePointAliasName = getBasePointAliasName();
        if (dbmeta.hasPrimaryKey()) {
            final ColumnSqlName primaryKeyName = dbmeta.getPrimaryUniqueInfo().getFirstColumn().getColumnSqlName();
            _lockSqlSuffix = " for update of " + basePointAliasName + "." + primaryKeyName;
        } else {
            final ColumnSqlName randomColumnName = ((ColumnInfo) dbmeta.getColumnInfoList().get(0)).getColumnSqlName();
            _lockSqlSuffix = " for update of " + basePointAliasName + "." + randomColumnName;
        }
        return this;
    }

    // ===================================================================================
    //                                                                       Hint Override
    //                                                                       =============
    /**
     * {@inheritDoc}
     */
    protected String createSelectHint() {
        return _fetchScopeSelectHint;
    }

    /**
     * {@inheritDoc}
     */
    protected String createFromBaseTableHint() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    protected String createFromHint() {
        return "";
    }

    /**
     * {@inheritDoc}
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
    public QueryClauseArranger createFullTextSearchClauseArranger() {
        return new FullTextSearchClauseArranger();
    }

    protected static class FullTextSearchClauseArranger implements QueryClauseArranger {
        public String arrange(ColumnRealName columnRealName, String operand, String bindExpression, String rearOption) {
            return "contains(" + columnRealName + ", " + bindExpression + ") > 0";
        }
    }

    public String escapeFullTextSearchValue(String conditionValue) {
        if (conditionValue.contains("}")) {
            conditionValue = replace(conditionValue, "}", "}}");
        }
        conditionValue = "{" + conditionValue + "}";
        return conditionValue;
    }

    // [DBFlute-0.9.6.9]
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Integer getPagingBindFrom() { // for parameter comment
        return _pagingBindFrom;
    }

    public Integer getPagingBindTo() { // for parameter comment
        return _pagingBindTo;
    }
}
