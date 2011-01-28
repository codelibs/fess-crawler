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

import java.util.List;

import org.seasar.robot.dbflute.cbean.sqlclause.orderby.OrderByClause;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.robot.dbflute.dbway.WayOfMySQL.FullTextSearchModifier;

/**
 * SqlClause for MySQL.
 * @author jflute
 */
public class SqlClauseMySql extends AbstractSqlClause {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** String of fetch-scope as sql-suffix. */
    protected String _fetchScopeSqlSuffix = "";

    /** String of lock as sql-suffix. */
    protected String _lockSqlSuffix = "";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param tableDbName The DB name of table. (NotNull)
     **/
    public SqlClauseMySql(String tableDbName) {
        super(tableDbName);
    }

    // ===================================================================================
    //                                                                    OrderBy Override
    //                                                                    ================
    @Override
    protected OrderByClause.OrderByNullsSetupper createOrderByNullsSetupper() {
        return createOrderByNullsSetupperByCaseWhen();
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
        _fetchScopeSqlSuffix = " limit " + getPageStartIndex() + ", " + getFetchSize();
    }

    /**
     * {@inheritDoc}
     */
    protected void doClearFetchPageClause() {
        _fetchScopeSqlSuffix = "";
    }

    // ===================================================================================
    //                                                                       Lock Override
    //                                                                       =============
    /**
     * {@inheritDoc}
     */
    public SqlClause lockForUpdate() {
        _lockSqlSuffix = " for update";
        return this;
    }

    // ===================================================================================
    //                                                                       Hint Override
    //                                                                       =============
    /**
     * {@inheritDoc}
     */
    protected String createSelectHint() {
        return "";
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

    // [DBFlute-0.7.5]
    // ===================================================================================
    //                                                               Query Update Override
    //                                                               =====================
    @Override
    protected boolean isUpdateSubQueryUseLocalTableSupported() {
        return false;
    }

    // [DBFlute-0.9.5]
    // ===================================================================================
    //                                                                    Full-Text Search
    //                                                                    ================
    /**
     * Build a condition string of match statement for full-text search. <br />
     * Bind variable is unused because the condition value should be literal in MySQL.
     * @param textColumnList The list of text column. (NotNull, NotEmpty, StringColumn, TargetTableColumn)
     * @param conditionValue The condition value. (NotNull)
     * @param modifier The modifier of full-text search. (NullAllowed: If the value is null, No modifier specified)
     * @param tableDbName The DB name of the target table. (NotNull)
     * @param aliasName The alias name of the target table. (NotNull)
     * @return The condition string of match statement. (NotNull)
     */
    public String buildMatchCondition(List<ColumnInfo> textColumnList, String conditionValue,
            FullTextSearchModifier modifier, String tableDbName, String aliasName) {
        if (textColumnList == null) {
            throw new IllegalArgumentException("The argument 'textColumnList' should not be null!");
        }
        if (textColumnList.isEmpty()) {
            throw new IllegalArgumentException("The argument 'textColumnList' should not be empty list!");
        }
        if (conditionValue == null || conditionValue.length() == 0) {
            throw new IllegalArgumentException("The argument 'conditionValue' should not be null or empty: "
                    + conditionValue);
        }
        if (tableDbName == null || tableDbName.trim().length() == 0) {
            throw new IllegalArgumentException("The argument 'tableDbName' should not be null or trimmed-empty: "
                    + tableDbName);
        }
        if (aliasName == null || aliasName.trim().length() == 0) {
            throw new IllegalArgumentException("The argument 'aliasName' should not be null or trimmed-empty: "
                    + aliasName);
        }
        final StringBuilder sb = new StringBuilder();
        int index = 0;
        for (ColumnInfo columnInfo : textColumnList) {
            if (columnInfo == null) {
                continue;
            }
            final String tableOfColumn = columnInfo.getDBMeta().getTableDbName();
            if (!tableOfColumn.equalsIgnoreCase(tableDbName)) {
                String msg = "The table of the text column should be '" + tableDbName + "'";
                msg = msg + " but the table is '" + tableOfColumn + "': column=" + columnInfo;
                throw new IllegalArgumentException(msg);
            }
            if (!columnInfo.isPropertyTypeString()) {
                String msg = "The text column should be String type:";
                msg = msg + " column=" + columnInfo;
                throw new IllegalArgumentException(msg);
            }
            final ColumnSqlName columnSqlName = columnInfo.getColumnSqlName();
            if (index > 0) {
                sb.append(",");
            }
            sb.append(aliasName).append(".").append(columnSqlName);
            ++index;
        }
        sb.insert(0, "match(").append(") against ('").append(conditionValue).append("'");
        if (modifier != null) {
            sb.append(" ").append(modifier.code());
        }
        sb.append(")");
        return sb.toString();
    }
}
