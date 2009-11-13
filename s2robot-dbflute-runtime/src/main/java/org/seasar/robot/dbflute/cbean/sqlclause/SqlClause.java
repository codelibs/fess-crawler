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

import java.util.Map;

import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;
import org.seasar.robot.dbflute.cbean.coption.ConditionOption;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.OrderByClause.ManumalOrderInfo;

/**
 * The interface of SQL clause.
 * @author jflute
 */
public interface SqlClause {

    // =====================================================================================
    //                                                                                Clause
    //                                                                                ======
    // -----------------------------------------------------
    //                                       Complete Clause
    //                                       ---------------
    /**
     * Get the clause of all parts.
     * <pre>
     * # select [base-table-columns], [join-table-columns]
     * #   from [base-table] left outer join [join-table] [join-alias] on [join-condition]
     * #  where [base-table].[column] = [value] and [join-alias].[column] is null
     * #  order by [base-table].[column] asc, [join-alias].[column] desc
     * #  for update
     * </pre>
     * @return The clause of all parts. (NotNull)
     */
    public String getClause();

    // -----------------------------------------------------
    //                                       Fragment Clause
    //                                       ---------------
    /**
     * Get from-where clause without select and orderBy and sqlSuffix. 
     * For subQuery and selectCount.
     * <p>
     * You should handle UnionSelectClauseMark and UnionWhereClauseMark and UnionWhereFirstConditionMark in clause.
     * </p>
     * @return The 'from-where' clause(contains union) without 'select' and 'orderBy' and 'sqlSuffix'. (NotNull)
     */
    public String getClauseFromWhereWithUnionTemplate();

    /**
     * Get from-where clause without select and orderBy and sqlSuffix as template. 
     * For subQuery and selectCount.
     * <p>
     * You should handle UnionSelectClauseMark and UnionWhereClauseMark and UnionWhereFirstConditionMark
     * and WhereClauseMark and WhereFirstConditionMark in clause.
     * </p>
     * @return The 'from-where' clause(contains union) without 'select' and 'orderBy' and 'sqlSuffix'. (NotNull)
     */
    public String getClauseFromWhereWithWhereUnionTemplate();

    // =====================================================================================
    //                                                                          Clause Parts
    //                                                                          ============
    /**
     * Get the clause of 'select'. This is an internal method.
     * @return The clause of select. {[select ...] from table...} (NotNull)
     */
    public String getSelectClause();

    /**
     * Get the map of select index.
     * @return The map of select index. {key:columnName, value:selectIndex}
     *         (Nullable: Null means select index is disabled.)
     */
    public Map<String, Integer> getSelectIndexMap();

    /**
     * Get the reverse map of select index.
     * @return The reverse map of select index. {key:selectIndex(AliasName), value:columnName}
     *         (Nullable: Null means select index is disabled.)
     */
    public Map<String, String> getSelectIndexReverseMap();

    /**
     * Disable select index.
     */
    public void disableSelectIndex();

    /**
     * Get the hint of 'select'. This is an internal method.
     * @return The hint of 'select'. {select [select-hint] * from table...} (NotNull)
     */
    public String getSelectHint();

    /**
     * Get the clause of 'from'. This is an internal method.
     * @return The clause of 'from'. (NotNull)
     */
    public String getFromClause();

    /**
     * Get the clause of from-base-table. This is an internal method.
     * @return The hint of from-base-table. {select * from table [from-base-table-hint] where ...} (NotNull)
     */
    public String getFromBaseTableHint();

    /**
     * Get the hint of 'from'. This is an internal method.
     * @return The hint of 'from'. {select * from table left outer join ... on ... [from-hint] where ...} (NotNull)
     */
    public String getFromHint();

    /**
     * Get the clause of 'where'. This is an internal method.
     * @return The clause of 'where'. (NotNull)
     */
    public String getWhereClause();

    /**
     * Get the clause of 'order-by'. This is an internal method.
     * @return The clause of 'order-by'. (NotNull)
     */
    public String getOrderByClause();

    /**
     * Get the suffix of SQL. This is an internal method.
     * @return The suffix of SQL. {select * from table where ... order by ... [sql-suffix]} (NotNull)
     */
    public String getSqlSuffix();

    // ===================================================================================
    //                                                                SelectedSelectColumn
    //                                                                ====================
    /**
     * Register selected-select-column.
     * @param foreignTableAliasName The alias name of foreign table. (NotNull)
     * @param localTableName The table name of local. (NotNull)
     * @param foreignPropertyName The property name of foreign table. (NotNull)
     * @param localRelationPath The path of local relation. (Nullable)
     */
    public void registerSelectedSelectColumn(String foreignTableAliasName, String localTableName,
            String foreignPropertyName, String localRelationPath);

    // ===================================================================================
    //                                                                           OuterJoin
    //                                                                           =========
    /**
     * Register outer-join.
     * @param joinTableName The name of join table. {left outer join [joinTableName]} (NotNull)
     * @param aliasName The alias name of join table. {left outer join joinTableName [aliasName]} (NotNull and Unique per invoking method)
     * @param joinOnMap Map that has conditions of on-clause. (NotNull)
     */
    public void registerOuterJoin(String joinTableName, String aliasName, Map<String, String> joinOnMap);

    /**
     * Change the join type for the relation to inner join.
     * @param aliasName The registered alias name of join table. (NotNull and Unique per invoking method)
     */
    public void changeToInnerJoin(String aliasName);

    public SqlClause makeInnerJoinEffective();

    public SqlClause backToOuterJoin();

    public String getFixedConditionKey();

    // ===================================================================================
    //                                                                               Where
    //                                                                               =====
    /**
     * Register 'where' clause.
     * @param columnFullName The full name of column. {[table-name].[column-name]}. (NotNull)
     * @param key Condition-key. (NotNull)
     * @param value Condition-value. (NotNull)
     */
    public void registerWhereClause(String columnFullName, ConditionKey key, ConditionValue value);

    /**
     * Register 'where' clause.
     * @param columnFullName The full name of column. {[table-name].[column-name]}. (NotNull)
     * @param key Condition-key. (NotNull)
     * @param value Condition-value. (NotNull)
     * @param option Condition-option. (NotNull)
     */
    public void registerWhereClause(String columnFullName, ConditionKey key, ConditionValue value,
            ConditionOption option);

    /**
     * Register 'where' clause.
     * @param clause The clause of 'where'. (NotNull)
     */
    public void registerWhereClause(String clause);

    /**
     * Exchange first The clause of 'where' for last one.
     */
    public void exchangeFirstWhereClauseForLastOne();

    /**
     * Does it have where clauses? <br />
     * In-line where clause is NOT contained.
     * @return Determination.
     */
    public boolean hasWhereClause();

    // ===================================================================================
    //                                                                         InlineWhere
    //                                                                         ===========
    public void registerBaseTableInlineWhereClause(String columnName, ConditionKey key, ConditionValue value);

    public void registerBaseTableInlineWhereClause(String columnName, ConditionKey key, ConditionValue value,
            ConditionOption option);

    public void registerBaseTableInlineWhereClause(String value);

    public void registerOuterJoinInlineWhereClause(String aliasName, String columnName, ConditionKey key,
            ConditionValue value, boolean onClauseInline);

    public void registerOuterJoinInlineWhereClause(String aliasName, String columnName, ConditionKey key,
            ConditionValue value, ConditionOption option, boolean onClauseInline);

    public void registerOuterJoinInlineWhereClause(String aliasName, String value, boolean onClauseInline);

    // ===================================================================================
    //                                                                             OrQuery
    //                                                                             =======
    /**
     * Make or-query effective.
     */
    public void makeOrQueryEffective();

    /**
     * Ignore or-query.
     */
    public void ignoreOrQuery();

    /**
     * Is or-query effective?
     * @return Determination.
     */
    public boolean isOrQueryEffective();

    // ===================================================================================
    //                                                                             OrderBy
    //                                                                             =======
    public OrderByClause getSqlComponentOfOrderByClause();

    public SqlClause clearOrderBy();

    public SqlClause ignoreOrderBy();

    public SqlClause makeOrderByEffective();

    /**
     * @param orderByProperty Order-by-property. 'aliasName.columnName/aliasName.columnName/...' (NotNull)
     * @param registeredOrderByProperty Registered-order-by-property. ([table-name].[column-name]) (Nullable)
     * @param ascOrDesc Is it ascend or descend?
     */
    public void registerOrderBy(String orderByProperty, String registeredOrderByProperty, boolean ascOrDesc);

    /**
     * @param orderByProperty Order-by-property. 'aliasName.columnName/aliasName.columnName/...' (NotNull)
     * @param registeredOrderByProperty Registered-order-by-property. ([table-name].[column-name]) (Nullable)
     * @param ascOrDesc Is it ascend or descend?
     */
    public void reverseOrderBy_Or_OverrideOrderBy(String orderByProperty, String registeredOrderByProperty,
            boolean ascOrDesc);

    public void addNullsFirstToPreviousOrderBy();

    public void addNullsLastToPreviousOrderBy();

    public void addManualOrderToPreviousOrderByElement(ManumalOrderInfo manumalOrderInfo);

    /**
     * Does it have order-by clauses? <br />
     * Whether effective or not has no influence.
     * @return Determination.
     */
    public boolean hasOrderByClause();

    // ===================================================================================
    //                                                                               Union
    //                                                                               =====
    public void registerUnionQuery(String unionClause, boolean unionAll);

    public boolean hasUnionQuery();

    // ===================================================================================
    //                                                                          FetchScope
    //                                                                          ==========
    /**
     * Fetch first.
     * @param fetchSize Fetch-size. (NotMinus)
     * @return this. (NotNull)
     */
    public SqlClause fetchFirst(int fetchSize);

    /**
     * Fetch scope.
     * @param fetchStartIndex Fetch-start-index. 0 origin. (NotMinus)
     * @param fetchSize Fetch-size. (NotMinus)
     * @return this. (NotNull)
     */
    public SqlClause fetchScope(int fetchStartIndex, int fetchSize);

    /**
     * Fetch page.
     * <p>
     * When you invoke this, it is normally necessary to invoke 'fetchFirst()' or 'fetchScope()' ahead of that.
     * But you also can use default-fetch-size without invoking 'fetchFirst()' or 'fetchScope()'.
     * If you invoke this, your SQL returns [fetch-size] records from [fetch-start-index] calculated by [fetch-page-number].
     * </p>
     * @param fetchPageNumber Fetch-page-number. 1 origin. (NotMinus & NotZero: If minus or zero, set one.)
     * @return this. (NotNull)
     */
    public SqlClause fetchPage(int fetchPageNumber);

    /**
     * Get fetch start index.
     * @return Fetch start index.
     */
    public int getFetchStartIndex();

    /**
     * Get fetch size.
     * @return Fetch size.
     */
    public int getFetchSize();

    /**
     * Get fetch page number.
     * @return Fetch page number.
     */
    public int getFetchPageNumber();

    /**
     * Get page start index.
     * @return Page start index. 0 origin. (NotMinus)
     */
    public int getPageStartIndex();

    /**
     * Get page end index.
     * @return Page end index. 0 origin. (NotMinus)
     */
    public int getPageEndIndex();

    /**
     * Is fetch scope effective?
     * @return Determiantion.
     */
    public boolean isFetchScopeEffective();

    /**
     * Ignore fetch-scope.
     * @return this. (NotNull)
     */
    public SqlClause ignoreFetchScope();

    /**
     * Make fetch-scope effective.
     * @return this. (NotNull)
     */
    public SqlClause makeFetchScopeEffective();

    /**
     * Is fetch start index supported?
     * @return Determination.
     */
    public boolean isFetchStartIndexSupported();

    /**
     * Is fetch size supported?
     * @return Determination.
     */
    public boolean isFetchSizeSupported();

    // ===================================================================================
    //                                                                     Fetch Narrowing
    //                                                                     ===============
    /**
     * Is fetch-narrowing effective?
     * @return Determiantion.
     */
    public boolean isFetchNarrowingEffective();

    /**
     * Get fetch-narrowing skip-start-index.
     * @return Skip-start-index.
     */
    public int getFetchNarrowingSkipStartIndex();

    /**
     * Get fetch-narrowing loop-count.
     * @return Loop-count.
     */
    public int getFetchNarrowingLoopCount();

    // ===================================================================================
    //                                                                                Lock
    //                                                                                ====
    /**
     * Lock for update.
     * <p>
     * If you invoke this, your SQL lock target records for update.
     * It depends whether this method supports this on the database type.
     * </p>
     * @return this. (NotNull)
     */
    public SqlClause lockForUpdate();

    // ===================================================================================
    //                                                                            Resolver
    //                                                                            ========
    /**
     * Resolve join alias name.
     * @param relationPath Relation path. (NotNull)
     * @param cqNestNo The nest no of condition query.
     * @return Resolved join alias name. (NotNull)
     */
    public String resolveJoinAliasName(String relationPath, int cqNestNo);

    /**
     * Resolve nest level expression.
     * @param name Name. (NotNull)
     * @param cqNestNo The nest no of condition query.
     * @return Resolved name about nest level. (NotNull)
     */
    public String resolveNestLevelExpression(String name, int cqNestNo);

    /**
     * Resolve relation no.
     * @param baseTableName The table name of base. (NotNull)
     * @param foreignPropertyName The property name of foreign. (NotNull)
     * @return Resolved relation no.
     */
    public int resolveRelationNo(String baseTableName, String foreignPropertyName);

    // ===================================================================================
    //                                                                    Table Alias Info
    //                                                                    ================
    public String getLocalTableAliasName();

    public String getForeignTableAliasPrefix();

    // ===================================================================================
    //                                                                       Template Mark
    //                                                                       =============
    public String getWhereClauseMark();

    public String getWhereFirstConditionMark();

    public String getUnionSelectClauseMark();

    public String getUnionWhereClauseMark();

    public String getUnionWhereFirstConditionMark();

    // ===================================================================================
    //                                                          Where Clause Simple Filter
    //                                                          ==========================
    public void addWhereClauseSimpleFilter(WhereClauseSimpleFilter whereClauseSimpleFilter);

    // ===================================================================================
    //                                                               Selected Foreign Info
    //                                                               =====================
    public boolean isSelectedForeignInfoEmpty();

    public boolean hasSelectedForeignInfo(String relationPath);

    public void registerSelectedForeignInfo(String relationPath, String foreignPropertyName);

    // ===================================================================================
    //                                                                    Sub Query Indent
    //                                                                    ================
    public String resolveSubQueryBeginMark(String subQueryIdentity);

    public String resolveSubQueryEndMark(String subQueryIdentity);

    public String filterSubQueryIndent(String sql);

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                       Specification
    //                                                                       =============
    public void specifySelectColumn(String tableAliasName, String columnName);

    public void specifyDeriveSubQuery(String aliasName, String deriveSubQuery);

    public boolean hasSpecifiedDeriveSubQuery(String aliasName);

    /**
     * Get the name of only one specified column.
     * @return The name of only one specified column. (Nullable: If it's not found or duplicated, it returns null)
     */
    public String getSpecifiedColumnNameAsOne();

    /**
     * Get the name of only one specified column with alias name.
     * @return The name of only one specified column with alias name. (Nullable: If it's not found or duplicated, it returns null)
     */
    public String getSpecifiedColumnRealNameAsOne();

    /**
     * Remove the only one specified column.
     * @return The only one specified column with alias name. (Nullable: If it's not found or duplicated, it returns null)
     */
    public String removeSpecifiedColumnRealNameAsOne();

    /**
     * Back up specified select columns.
     */
    public void backupSpecifiedSelectColumn();

    /**
     * Restore specified select columns.
     */
    public void restoreSpecifiedSelectColumn();

    /**
     * Clear specified select columns.
     */
    public void clearSpecifiedSelectColumn();

    // [DBFlute-0.7.5]
    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * @param columnParameterMap The map of column parameters. (NotNull)
     * @return The clause of query update. (Nullable: If columnParameterMap is empty, return null)
     */
    public String getClauseQueryUpdate(Map<String, String> columnParameterMap);

    public String getClauseQueryDelete();

    // [DBFlute-0.8.6]
    // ===================================================================================
    //                                                                  Select Clause Type
    //                                                                  ==================
    /**
     * Classify the type of select clause into specified type.
     * @param selectClauseType The type of select clause. (NotNull)
     */
    public void classifySelectClauseType(SelectClauseType selectClauseType);

    /**
     * Roll-back the type of select clause into previous one.
     * If it has no change, classify its type into default type.
     */
    public void rollbackSelectClauseType();

    /**
     * The type of select clause.
     */
    public static enum SelectClauseType {
        COLUMNS, COUNT, MAX, MIN, SUM, AVG
    }

    // [DBFlute-0.9.4]
    // ===================================================================================
    //                                                                       InScope Limit
    //                                                                       =============
    /**
     * Get the limit of inScope.
     * @return The limit of inScope. (If it's zero or minus, it means no limit)
     */
    public int getInScopeLimit();
}
