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
import java.util.Map;

import org.seasar.robot.dbflute.cbean.chelper.HpCBPurpose;
import org.seasar.robot.dbflute.cbean.chelper.HpDerivingSubQueryInfo;
import org.seasar.robot.dbflute.cbean.chelper.HpInvalidQueryInfo;
import org.seasar.robot.dbflute.cbean.chelper.HpSpecifiedColumn;
import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;
import org.seasar.robot.dbflute.cbean.coption.ConditionOption;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.join.FixedConditionResolver;
import org.seasar.robot.dbflute.cbean.sqlclause.orderby.OrderByClause;
import org.seasar.robot.dbflute.cbean.sqlclause.orderby.OrderByClause.ManumalOrderInfo;
import org.seasar.robot.dbflute.cbean.sqlclause.query.QueryClause;
import org.seasar.robot.dbflute.cbean.sqlclause.query.QueryClauseFilter;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.robot.dbflute.dbmeta.name.ColumnSqlName;

/**
 * The interface of SQL clause. <br />
 * And this also has a role of a container for common info
 * between the top level condition-bean and related condition-queries.
 * @author jflute
 */
public interface SqlClause {

    // ===================================================================================
    //                                                                      SubQuery Level
    //                                                                      ==============
    /**
     * Get the hierarchy level of sub-query.
     * @return The hierarchy level of sub-query. (NotMinus: if zero, not for sub-query)
     */
    int getSubQueryLevel();

    /**
     * Set up this SQL for sub-query.
     * @param subQueryLevel The hierarchy level of sub-query. (NotMinus: if zero, not for sub-query)
     */
    void setupForSubQuery(int subQueryLevel);

    /**
     * Is this SQL for sub-query?
     * @return Determination.
     */
    boolean isForSubQuery();

    // ===================================================================================
    //                                                                              Clause
    //                                                                              ======
    // -----------------------------------------------------
    //                                       Complete Clause
    //                                       ---------------
    /**
     * Get the clause of all parts.
     * <pre>
     * select [base-table-columns], [join-table-columns]
     *   from [base-table] left outer join [join-table] [join-alias] on [join-condition]
     *  where [base-table].[column] = [value] and [join-alias].[column] is null
     *  order by [base-table].[column] asc, [join-alias].[column] desc
     *  for update
     * </pre>
     * @return The clause of all parts. (NotNull)
     */
    String getClause();

    // -----------------------------------------------------
    //                                       Fragment Clause
    //                                       ---------------
    /**
     * Get from-where clause without select and orderBy and sqlSuffix. <br />
     * Basically for subQuery and selectCount. <br />
     * You should handle UnionSelectClauseMark and UnionWhereClauseMark and UnionWhereFirstConditionMark in clause.
     * @return The 'from-where' clause(contains union) without 'select' and 'orderBy' and 'sqlSuffix'. (NotNull)
     */
    String getClauseFromWhereWithUnionTemplate();

    /**
     * Get from-where clause without select and orderBy and sqlSuffix as template. <br />
     * Basically for subQuery and selectCount. <br />
     * You should handle UnionSelectClauseMark and UnionWhereClauseMark and UnionWhereFirstConditionMark
     * and WhereClauseMark and WhereFirstConditionMark in clause.
     * @return The 'from-where' clause(contains union) without 'select' and 'orderBy' and 'sqlSuffix'. (NotNull)
     */
    String getClauseFromWhereWithWhereUnionTemplate();

    // ===================================================================================
    //                                                                        Clause Parts
    //                                                                        ============
    /**
     * Get the clause of 'select'. This is an internal method.
     * @return The clause of select. {[select ...] from table...} (NotNull)
     */
    String getSelectClause();

    /**
     * Get the map of select index. map:{columnAliasName = selectIndex(AliasName)}
     * @return The map of select index. (NullAllowed: null means select index is disabled)
     */
    Map<String, Integer> getSelectIndexMap();

    /**
     * Get the reverse map of select index. map:{selectIndex(AliasName) = columnAliasName}
     * @return The reverse map of select index. (NullAllowed: null means select index is disabled)
     */
    Map<String, String> getSelectIndexReverseMap();

    /**
     * Disable select index.
     */
    void disableSelectIndex();

    /**
     * Get the hint of 'select'. This is an internal method.
     * @return The hint of 'select'. {select [select-hint] * from table...} (NotNull)
     */
    String getSelectHint();

    /**
     * Get the clause of 'from'. This is an internal method.
     * @return The clause of 'from'. (NotNull)
     */
    String getFromClause();

    /**
     * Get the clause of from-base-table. This is an internal method.
     * @return The hint of from-base-table. {select * from table [from-base-table-hint] where ...} (NotNull)
     */
    String getFromBaseTableHint();

    /**
     * Get the hint of 'from'. This is an internal method.
     * @return The hint of 'from'. {select * from table left outer join ... on ... [from-hint] where ...} (NotNull)
     */
    String getFromHint();

    /**
     * Get the clause of 'where'. This is an internal method.
     * @return The clause of 'where'. (NotNull)
     */
    String getWhereClause();

    /**
     * Get the clause of 'order-by'. This is an internal method.
     * @return The clause of 'order-by'. (NotNull)
     */
    String getOrderByClause();

    /**
     * Get the suffix of SQL. This is an internal method.
     * @return The suffix of SQL. {select * from table where ... order by ... [sql-suffix]} (NotNull)
     */
    String getSqlSuffix();

    // ===================================================================================
    //                                                                   Selected Relation
    //                                                                   =================
    /**
     * Register selected relation.
     * @param foreignTableAliasName The alias name of foreign table. (NotNull)
     * @param localTableDbName The table DB name of local. (NotNull)
     * @param foreignPropertyName The property name of foreign table. (NotNull)
     * @param localRelationPath The path of local relation. (NullAllowed)
     * @param foreignRelationPath The path of foreign relation. (NullAllowed)
     */
    void registerSelectedRelation(String foreignTableAliasName, String localTableDbName, String foreignPropertyName,
            String localRelationPath, String foreignRelationPath);

    boolean isSelectedRelationEmpty();

    boolean hasSelectedRelation(String relationPath);

    // ===================================================================================
    //                                                                           OuterJoin
    //                                                                           =========
    /**
     * Register outer-join.
     * @param localTableDbName The DB name of local table. {[localTableDbName] left outer join} (NotNull)
     * @param foreignTableDbName The DB name of foreign table. {left outer join [foreignTableDbName]} (NotNull)
     * @param foreignAliasName The alias name of foreign table. {left outer join joinTableName [foreignAliasName]} (NotNull and Unique per invoking method)
     * @param joinOnMap The map of join condition on on-clause. (NotNull)
     * @param fixedCondition The fixed condition on on-clause. (NullAllowed: if null, means no fixed condition)
     * @param fixedConditionResolver The resolver for variables on fixed-condition. (NullAllowed) 
     */
    void registerOuterJoin(String localTableDbName, String foreignTableDbName, String foreignAliasName,
            Map<ColumnRealName, ColumnRealName> joinOnMap, String fixedCondition,
            FixedConditionResolver fixedConditionResolver);

    /**
     * Change the join type for the relation to inner join.
     * @param aliasName The registered alias name of join table. (NotNull and Unique per invoking method)
     */
    void changeToInnerJoin(String aliasName);

    SqlClause makeInnerJoinEffective();

    SqlClause backToOuterJoin();

    // ===================================================================================
    //                                                                               Where
    //                                                                               =====
    /**
     * Register 'where' clause.
     * @param columnRealName The real name of column. {[alias-name].[column-name]}. (NotNull)
     * @param key Condition-key. (NotNull)
     * @param value Condition-value. (NotNull)
     * @param option Condition-option. (NullAllowed)
     */
    void registerWhereClause(ColumnRealName columnRealName, ConditionKey key, ConditionValue value,
            ConditionOption option);

    /**
     * Register 'where' clause.
     * @param clause The string clause of 'where'. (NotNull)
     */
    void registerWhereClause(String clause);

    /**
     * Register 'where' clause.
     * @param clause The query clause of 'where'. (NotNull)
     */
    void registerWhereClause(QueryClause clause);

    /**
     * Exchange first The clause of 'where' for last one.
     */
    void exchangeFirstWhereClauseForLastOne();

    /**
     * Does it have where clauses? <br />
     * In-line where clause is NOT contained.
     * @return Determination.
     */
    boolean hasWhereClause();

    // ===================================================================================
    //                                                                         InlineWhere
    //                                                                         ===========
    void registerBaseTableInlineWhereClause(ColumnSqlName columnSqlName, ConditionKey key, ConditionValue value);

    void registerBaseTableInlineWhereClause(ColumnSqlName columnSqlName, ConditionKey key, ConditionValue value,
            ConditionOption option);

    void registerBaseTableInlineWhereClause(String value);

    void registerOuterJoinInlineWhereClause(String aliasName, ColumnSqlName columnSqlName, ConditionKey key,
            ConditionValue value, boolean onClause);

    void registerOuterJoinInlineWhereClause(String aliasName, ColumnSqlName columnSqlName, ConditionKey key,
            ConditionValue value, ConditionOption option, boolean onClause);

    void registerOuterJoinInlineWhereClause(String aliasName, String clause, boolean onClause);

    // ===================================================================================
    //                                                                        OrScopeQuery
    //                                                                        ============
    /**
     * Make or-scope query effective.
     */
    void makeOrScopeQueryEffective();

    /**
     * Close or-scope query.
     */
    void closeOrScopeQuery();

    /**
     * Begin or-scope query to and-part.
     */
    void beginOrScopeQueryAndPart();

    /**
     * End or-scope query and-part.
     */
    void endOrScopeQueryAndPart();

    /**
     * Is or-scope query effective?
     * @return Determination.
     */
    boolean isOrScopeQueryEffective();

    /**
     * Is and-part of or-scope effective?
     * @return Determination.
     */
    boolean isOrScopeQueryAndPartEffective();

    // ===================================================================================
    //                                                                             OrderBy
    //                                                                             =======
    OrderByClause getOrderByComponent();

    SqlClause clearOrderBy();

    SqlClause makeOrderByEffective();

    SqlClause ignoreOrderBy();

    /**
     * @param orderByProperty Order-by-property. 'aliasName.columnSqlName/aliasName.columnSqlName/...' (NotNull)
     * @param ascOrDesc Is it ascend or descend?
     */
    void registerOrderBy(String orderByProperty, boolean ascOrDesc);

    /**
     * @param orderByProperty Order-by-property. 'aliasName.columnSqlName/aliasName.columnSqlName/...' (NotNull)
     * @param ascOrDesc Is it ascend or descend?
     */
    void reverseOrderBy_Or_OverrideOrderBy(String orderByProperty, boolean ascOrDesc);

    void addNullsFirstToPreviousOrderBy();

    void addNullsLastToPreviousOrderBy();

    void addManualOrderToPreviousOrderByElement(ManumalOrderInfo manumalOrderInfo);

    /**
     * Does it have order-by clauses? <br />
     * Whether effective or not has no influence.
     * @return Determination.
     */
    boolean hasOrderByClause();

    // ===================================================================================
    //                                                                               Union
    //                                                                               =====
    void registerUnionQuery(String unionClause, boolean unionAll);

    boolean hasUnionQuery();

    // ===================================================================================
    //                                                                          FetchScope
    //                                                                          ==========
    /**
     * Fetch first.
     * @param fetchSize Fetch-size. (NotMinus)
     * @return this. (NotNull)
     */
    SqlClause fetchFirst(int fetchSize);

    /**
     * Fetch scope.
     * @param fetchStartIndex Fetch-start-index. 0 origin. (NotMinus)
     * @param fetchSize Fetch-size. (NotMinus)
     * @return this. (NotNull)
     */
    SqlClause fetchScope(int fetchStartIndex, int fetchSize);

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
    SqlClause fetchPage(int fetchPageNumber);

    /**
     * Get fetch start index.
     * @return Fetch start index.
     */
    int getFetchStartIndex();

    /**
     * Get fetch size.
     * @return Fetch size.
     */
    int getFetchSize();

    /**
     * Get fetch page number.
     * @return Fetch page number.
     */
    int getFetchPageNumber();

    /**
     * Get page start index.
     * @return Page start index. 0 origin. (NotMinus)
     */
    int getPageStartIndex();

    /**
     * Get page end index.
     * @return Page end index. 0 origin. (NotMinus)
     */
    int getPageEndIndex();

    /**
     * Is fetch scope effective?
     * @return Determiantion.
     */
    boolean isFetchScopeEffective();

    /**
     * Ignore fetch-scope.
     * @return this. (NotNull)
     */
    SqlClause ignoreFetchScope();

    /**
     * Make fetch-scope effective.
     * @return this. (NotNull)
     */
    SqlClause makeFetchScopeEffective();

    /**
     * Is fetch start index supported?
     * @return Determination.
     */
    boolean isFetchStartIndexSupported();

    /**
     * Is fetch size supported?
     * @return Determination.
     */
    boolean isFetchSizeSupported();

    // ===================================================================================
    //                                                                     Fetch Narrowing
    //                                                                     ===============
    /**
     * Is fetch-narrowing effective?
     * @return Determiantion.
     */
    boolean isFetchNarrowingEffective();

    /**
     * Get fetch-narrowing skip-start-index.
     * @return Skip-start-index.
     */
    int getFetchNarrowingSkipStartIndex();

    /**
     * Get fetch-narrowing loop-count.
     * @return Loop-count.
     */
    int getFetchNarrowingLoopCount();

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
    SqlClause lockForUpdate();

    // ===================================================================================
    //                                                                    Table Alias Info
    //                                                                    ================
    /**
     * Get the alias name for base point table. <br />
     * @return The string name for alias. (NotNull)
     */
    String getBasePointAliasName();

    /**
     * Resolve alias name for join table.
     * @param relationPath Relation path. (NotNull)
     * @param nestLevel The nest level of condition query.
     * @return The resolved name. (NotNull)
     */
    String resolveJoinAliasName(String relationPath, int nestLevel);

    /**
     * Resolve relation no.
     * @param localTableName The name of local table. (NotNull)
     * @param foreignPropertyName The property name of foreign relation. (NotNull)
     * @return The resolved relation No.
     */
    int resolveRelationNo(String localTableName, String foreignPropertyName);

    /**
     * Get the alias name for in-line view of union-query.
     * @return The string name for alias. (NotNull)
     */
    String getUnionQueryInlineViewAlias();

    /**
     * Get the alias name for specified column of scalar-select.
     * @return The string name for alias. (NotNull)
     */
    String getScalarSelectColumnAlias();

    /**
     * Get the alias name for derived column of nested DerivedReferrer.
     * @return The string name for alias. (NotNull)
     */
    String getDerivedReferrerNestedAlias();

    // ===================================================================================
    //                                                                       Template Mark
    //                                                                       =============
    String getWhereClauseMark();

    String getWhereFirstConditionMark();

    String getUnionSelectClauseMark();

    String getUnionWhereClauseMark();

    String getUnionWhereFirstConditionMark();

    // ===================================================================================
    //                                                          Where Clause Simple Filter
    //                                                          ==========================
    void addWhereClauseSimpleFilter(QueryClauseFilter whereClauseSimpleFilter);

    // ===================================================================================
    //                                                                    Sub Query Indent
    //                                                                    ================
    String resolveSubQueryBeginMark(String subQueryIdentity);

    String resolveSubQueryEndMark(String subQueryIdentity);

    String processSubQueryIndent(String sql);

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                       Specification
    //                                                                       =============
    // -----------------------------------------------------
    //                                        Specify Column
    //                                        --------------
    /**
     * Specify select columns. <br />
     * It is overridden when the specified column has already been specified.
     * @param specifiedColumn The info about column specification. (NotNull)
     */
    void specifySelectColumn(HpSpecifiedColumn specifiedColumn);

    /**
     * Does it have specified select columns?
     * @param tableAliasName The alias name of table. (NotNull)
     * @return Determination.
     */
    boolean hasSpecifiedSelectColumn(String tableAliasName);

    /**
     * Does it have the specified select column?
     * @param tableAliasName The alias name of table. (NotNull)
     * @param columnDbName The DB name of column. (NotNull)
     * @return Determination.
     */
    boolean hasSpecifiedSelectColumn(String tableAliasName, String columnDbName);

    /**
     * Back up specified select columns.
     */
    void backupSpecifiedSelectColumn();

    /**
     * Restore specified select columns.
     */
    void restoreSpecifiedSelectColumn();

    /**
     * Clear specified select columns.
     */
    void clearSpecifiedSelectColumn();

    // -----------------------------------------------------
    //                                      Specified as One
    //                                      ----------------
    /**
     * Get the DB name of only one specified column.
     * @return The instance as string. (NullAllowed: if not found or duplicated, returns null)
     */
    String getSpecifiedColumnDbNameAsOne();

    /**
     * Get the information of only one specified column.
     * @return An instance as a type for information of column. (NullAllowed: if not found or duplicated, returns null)
     */
    ColumnInfo getSpecifiedColumnInfoAsOne();

    /**
     * Get the real name of only one specified column.
     * @return An instance as a type for real name of column. (NullAllowed: if not found or duplicated, returns null)
     */
    ColumnRealName getSpecifiedColumnRealNameAsOne();

    /**
     * Get the SQL name of only one specified column.
     * @return An instance as a type for SQL name of column. (NullAllowed: if not found or duplicated, returns null)
     */
    ColumnSqlName getSpecifiedColumnSqlNameAsOne();

    // -----------------------------------------------------
    //                                      Specify Deriving
    //                                      ----------------
    /**
     * Specify deriving sub-query for DerivedReferrer. <br />
     * It is overridden when the specified column has already been specified. <br />
     * The aliasName is allowed to be null for (Specify)DerivedReferrer to be used in other functions.
     * @param subQueryInfo The info about deriving sub-query. (NotNull: aliasName is allowed to be null)
     */
    void specifyDerivingSubQuery(HpDerivingSubQueryInfo subQueryInfo);

    boolean hasSpecifiedDerivingSubQuery(String aliasName);

    List<String> getSpecifiedDerivingAliasList();

    // -----------------------------------------------------
    //                                       Deriving as One
    //                                       ---------------
    ColumnInfo getSpecifiedDerivingColumnInfoAsOne();

    String getSpecifiedDerivingAliasNameAsOne();

    String getSpecifiedDerivingSubQueryAsOne();

    void clearSpecifiedDerivingSubQuery();

    // ===================================================================================
    //                                                                  Invalid Query Info
    //                                                                  ==================
    boolean isEmptyStringQueryAllowed();

    void allowEmptyStringQuery();

    boolean isInvalidQueryChecked();

    void checkInvalidQuery();

    /**
     * Get the list of invalid query. (basically for logging)
     * @return The list of invalid query. (NotNull, ReadOnly)
     */
    List<HpInvalidQueryInfo> getInvalidQueryList();

    void saveInvalidQuery(HpInvalidQueryInfo invalidQueryInfo);

    // [DBFlute-0.7.5]
    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * @param fixedValueQueryExpMap The map of query expression for fixed values. (NotNull)
     * @param resourceSqlClause The SQL clause for resource. (NotNull)
     * @return The clause of query-insert. (NotNull)
     */
    String getClauseQueryInsert(Map<String, String> fixedValueQueryExpMap, SqlClause resourceSqlClause);

    /**
     * @param columnParameterMap The map of column parameters. (NotNull)
     * @return The clause of query-update. (NullAllowed: If columnParameterMap is empty, return null)
     */
    String getClauseQueryUpdate(Map<String, String> columnParameterMap);

    /**
     * @return The clause of query-delete. (NotNull)
     */
    String getClauseQueryDelete();

    // [DBFlute-0.8.6]
    // ===================================================================================
    //                                                                  Select Clause Type
    //                                                                  ==================
    /**
     * Classify the type of select clause into specified type.
     * @param selectClauseType The type of select clause. (NotNull)
     */
    void classifySelectClauseType(SelectClauseType selectClauseType);

    /**
     * Roll-back the type of select clause into previous one.
     * If it has no change, classify its type into default type.
     */
    void rollbackSelectClauseType();

    /**
     * The type of select clause.
     */
    public static enum SelectClauseType {
        COLUMNS(false, false, false, false) // normal
        // count (also scalar) mainly for Behavior.selectCount(cb) or selectPage(cb)
        , UNIQUE_COUNT(true, true, true, false), PLAIN_COUNT(true, true, false, false)
        // scalar mainly for Behavior.scalarSelect(cb)
        , MAX(false, true, true, true), MIN(false, true, true, true) // max(), min()
        , SUM(false, true, true, true), AVG(false, true, true, true); // sum(), avg()

        private final boolean _count;
        private final boolean _scalar;
        private final boolean _uniqueScalar;
        private final boolean _specifiedScalar;

        private SelectClauseType(boolean count, boolean scalar, boolean uniqueScalar, boolean specifiedScalar) {
            _count = count;
            _scalar = scalar;
            _uniqueScalar = uniqueScalar;
            _specifiedScalar = specifiedScalar;
        }

        public boolean isCount() {
            return _count;
        }

        public boolean isScalar() { // also contains count
            return _scalar;
        }

        /**
         * Should the scalar be selected uniquely?
         * @return Determination.
         */
        public boolean isUniqueScalar() { // not contains plain-count
            return _uniqueScalar;
        }

        /**
         * Does the scalar need specified only-one column?
         * @return Determination.
         */
        public boolean isSpecifiedScalar() { // not contains all-count
            return _specifiedScalar;
        }
    }

    // [DBFlute-0.9.7.2]
    // ===================================================================================
    //                                                                        Purpose Type
    //                                                                        ============
    HpCBPurpose getPurpose();

    void setPurpose(HpCBPurpose purpose);

    // [DBFlute-0.9.4]
    // ===================================================================================
    //                                                                       InScope Limit
    //                                                                       =============
    /**
     * Get the limit of inScope.
     * @return The limit of inScope. (If it's zero or minus, it means no limit)
     */
    int getInScopeLimit();
}
