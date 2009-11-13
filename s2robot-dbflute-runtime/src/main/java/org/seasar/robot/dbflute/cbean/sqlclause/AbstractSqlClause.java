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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;
import org.seasar.robot.dbflute.cbean.coption.ConditionOption;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.OrderByClause.ManumalOrderInfo;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.robot.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.robot.dbflute.util.DfAssertUtil;
import org.seasar.robot.dbflute.util.DfStringUtil;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * The abstract class of SQL clause.
 * @author jflute
 */
public abstract class AbstractSqlClause implements SqlClause {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final SelectClauseType DEFAULT_SELECT_CLAUSE_TYPE = SelectClauseType.COLUMNS;
    protected static final String SELECT_HINT = "/*$pmb.selectHint*/";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    /** The name of table for SQL. */
    protected final String _tableName;

    /** The DB meta of target table. */
    protected DBMetaProvider _dbmetaProvider;

    // -----------------------------------------------------
    //                                       Clause Resource
    //                                       ---------------
    // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // The resources that are not often used to are lazy-loaded for performance.
    // - - - - - - - - - -/
    /** Selected select column map. map:{tableAliasName : map:{columnName : selectColumnInfo}} */
    protected Map<String, Map<String, SelectedSelectColumnInfo>> _selectedSelectColumnMap = new LinkedHashMap<String, Map<String, SelectedSelectColumnInfo>>();

    /** Specified select column map. map:{ tableAliasName = map:{ columnName : null } } (Nullable: This is lazy-loaded) */
    protected Map<String, Map<String, String>> _specifiedSelectColumnMap; // [DBFlute-0.7.4]

    /** Specified select column map for backup. map:{ tableAliasName = map:{ columnName : null } } (Nullable: This is lazy-loaded) */
    protected Map<String, Map<String, String>> _backupSpecifiedSelectColumnMap; // [DBFlute-0.9.5.3]

    /** Specified derive sub-query map. (Nullable: This is lazy-loaded) */
    protected Map<String, String> _specifiedDeriveSubQueryMap; // [DBFlute-0.7.4]

    /** The map of real column and alias of select clause. map:{realColumnName : aliasName} */
    protected Map<String, String> _selectClauseRealColumnAliasMap = new HashMap<String, String>(); // Without linked!

    /** The type of select clause. (NotNull) */
    protected SelectClauseType _selectClauseType = DEFAULT_SELECT_CLAUSE_TYPE;

    /** The previous type of select clause. (Nullable: The default is null) */
    protected SelectClauseType _previousSelectClauseType;

    /** The map of select index. {key:columnName, value:selectIndex} (Nullable) */
    protected Map<String, Integer> _selectIndexMap;

    /** Is use select index? Default value is true. */
    protected boolean _useSelectIndex = true;

    /** The map of outer join. */
    protected Map<String, LeftOuterJoinInfo> _outerJoinMap = new LinkedHashMap<String, LeftOuterJoinInfo>();

    /** Is inner-join effective? Default value is false. */
    protected boolean _innerJoinEffective = false;

    /** The list of where clause. */
    protected List<String> _whereList = new ArrayList<String>();

    /** The list of in-line where clause for base table. */
    protected List<String> _baseTableInlineWhereList = new ArrayList<String>();

    /** The clause of order-by. (NotNull) */
    protected final OrderByClause _orderByClause = new OrderByClause();

    /** The list of union clause. (Nullable: This is lazy-loaded) */
    protected List<UnionQueryInfo> _unionQueryInfoList;

    /** Is order-by effective? Default value is false. */
    protected boolean _orderByEffective = false;

    // -----------------------------------------------------
    //                                        Fetch Property
    //                                        --------------
    /** Fetch start index. (for fetchXxx()) */
    protected int _fetchStartIndex = 0;

    /** Fetch size. (for fetchXxx()) */
    protected int _fetchSize = 0;

    /** Fetch page number. (for fetchXxx()) This value should be plus. */
    protected int _fetchPageNumber = 1;

    /** Is fetch-narrowing effective? Default value is false. */
    protected boolean _fetchScopeEffective = false;

    // -----------------------------------------------------
    //                                               OrQuery
    //                                               -------
    /** Is or-query effective?*/
    protected boolean _orQueryEffective = false;

    // -----------------------------------------------------
    //                               WhereClauseSimpleFilter
    //                               -----------------------
    /** The filter for where clause. */
    protected List<WhereClauseSimpleFilter> _whereClauseSimpleFilterList;

    // -----------------------------------------------------
    //                                 Selected Foreign Info
    //                                 ---------------------
    /** The information of selected foreign table. */
    protected Map<String, String> _selectedForeignInfo;

    // -----------------------------------------------------
    //                                         Optional Info
    //                                         -------------
    protected boolean _formatClause = true;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractSqlClause(String tableName) {
        if (tableName == null) {
            String msg = "Argument[tableName] should not be null.";
            throw new IllegalArgumentException(msg);
        }
        _tableName = tableName;
    }

    public SqlClause provider(DBMetaProvider dbmetaProvider) {
        _dbmetaProvider = dbmetaProvider;
        return this;
    }

    // ===================================================================================
    //                                                                              Clause
    //                                                                              ======
    // -----------------------------------------------------
    //                                       Complete Clause
    //                                       ---------------
    public String getClause() {
        StringBuilder sb = new StringBuilder(512);
        sb.append(getSelectClause());
        sb.append(" ");
        sb.append(buildClauseWithoutMainSelect());
        String sql = sb.toString();
        sql = filterUnionCountOrScalar(sql);
        sql = filterSubQueryIndent(sql);
        return sql;
    }

    protected String buildClauseWithoutMainSelect() {
        StringBuilder sb = new StringBuilder(512);
        sb.append(getFromClause());
        sb.append(getFromHint());
        sb.append(" ");
        sb.append(getWhereClause());
        String unionClause = buildUnionClause(getSelectClause());

        // Delete template mark! (At the future this will be unnecessary.)
        unionClause = replaceString(unionClause, getUnionWhereClauseMark(), "");// Required!
        unionClause = replaceString(unionClause, getUnionWhereFirstConditionMark(), "");// Required!

        sb.append(unionClause);
        if (_orderByEffective && !_orderByClause.isEmpty()) {
            sb.append(" ");
            sb.append(getOrderByClause());
        }
        sb.append(" ");
        sb.append(getSqlSuffix());
        return sb.toString();
    }

    // -----------------------------------------------------
    //                                       Fragment Clause
    //                                       ---------------
    public String getClauseFromWhereWithUnionTemplate() {
        return buildClauseFromWhereAsTemplate(false);
    }

    public String getClauseFromWhereWithWhereUnionTemplate() {
        return buildClauseFromWhereAsTemplate(true);
    }

    protected String buildClauseFromWhereAsTemplate(boolean template) {
        StringBuilder sb = new StringBuilder(512);
        sb.append(getFromClause());
        sb.append(getFromHint());
        sb.append(" ");
        sb.append(buildWhereClause(template));
        sb.append(buildUnionClause(getUnionSelectClauseMark()));
        return sb.toString();
    }

    protected String buildUnionClause(String selectClause) {
        StringBuilder sb = new StringBuilder();
        if (hasUnionQuery()) {
            for (Iterator<UnionQueryInfo> ite = _unionQueryInfoList.iterator(); ite.hasNext();) {
                UnionQueryInfo unionQueryInfo = (UnionQueryInfo) ite.next();
                String unionQueryClause = unionQueryInfo.getUnionQueryClause();
                boolean unionAll = unionQueryInfo.isUnionAll();
                sb.append(ln());
                sb.append(unionAll ? " union all " : " union ");
                sb.append(ln());
                sb.append(selectClause).append(" ").append(unionQueryClause);
            }
        }
        return sb.toString();
    }

    protected String filterUnionCountOrScalar(String sql) {
        if (!isSelectClauseTypeCountOrScalar() || !hasUnionQuery()) {
            return sql;
        }
        String selectClause = buildSelectClauseCountOrScalar("dfmain");
        String ln = ln();
        String beginMark = resolveSubQueryBeginMark("dfmain") + ln;
        String endMark = resolveSubQueryEndMark("dfmain");
        return selectClause + ln + "  from (" + beginMark + sql + ln + "       ) dfmain" + endMark;
    }

    // ===================================================================================
    //                                                                        Clause Parts
    //                                                                        ============
    public String getSelectClause() {
        // [DBFlute-0.8.6]
        if (isSelectClauseTypeCountOrScalar() && !hasUnionQuery()) {
            return buildSelectClauseCountOrScalar("dflocal");
        }
        // /- - - - - - - - - - - - - - - - - - - - - - - - 
        // The type of select clause is COLUMNS since here.
        // - - - - - - - - - -/
        StringBuilder sb = new StringBuilder();
        DBMeta dbmeta = findDBMeta(_tableName);
        List<ColumnInfo> columnInfoList = dbmeta.getColumnInfoList();

        Map<String, String> localSpecifiedMap = _specifiedSelectColumnMap != null ? _specifiedSelectColumnMap
                .get(getLocalTableAliasName()) : null;
        boolean existsSpecifiedLocal = localSpecifiedMap != null && !localSpecifiedMap.isEmpty();

        Integer selectIndex = 0;
        if (_useSelectIndex) {
            _selectIndexMap = new HashMap<String, Integer>();
        }

        // Columns of local table.
        for (ColumnInfo columnInfo : columnInfoList) {
            String columnName = columnInfo.getColumnDbName();

            // [DBFlute-0.7.4]
            if (existsSpecifiedLocal && !localSpecifiedMap.containsKey(columnName)) {
                if (isSelectClauseTypeCountOrScalar() && hasUnionQuery()) {
                    // Here it must be with union query.
                    // So the primary Key is target for saving unique.
                    // But if it does not have primary keys, all column is target.
                    if (dbmeta.hasPrimaryKey()) {
                        if (!columnInfo.isPrimary()) {
                            continue;
                        }
                    }
                } else {
                    continue;
                }
            }

            if (sb.length() > 0) {
                sb.append(", ");
            } else {
                sb.append("select").append(SELECT_HINT).append(" ");
            }
            String realColumnName = getLocalTableAliasName() + "." + columnName;
            String onQueryName;
            ++selectIndex;
            if (_useSelectIndex) {
                _selectIndexMap.put(columnName, selectIndex);
                onQueryName = buildSelectIndexAliasName(selectIndex);
            } else {
                onQueryName = columnName;
            }
            sb.append(realColumnName).append(" as ").append(onQueryName);
            _selectClauseRealColumnAliasMap.put(realColumnName, onQueryName);
        }

        // Columns of foreign tables.
        Set<String> tableAliasNameSet = _selectedSelectColumnMap.keySet();
        for (String tableAliasName : tableAliasNameSet) {
            Map<String, SelectedSelectColumnInfo> map = _selectedSelectColumnMap.get(tableAliasName);
            Collection<SelectedSelectColumnInfo> selectColumnInfoList = map.values();
            Map<String, String> foreginSpecifiedMap = null;
            if (_specifiedSelectColumnMap != null) {
                foreginSpecifiedMap = _specifiedSelectColumnMap.get(tableAliasName);
            }
            boolean existsSpecifiedForeign = foreginSpecifiedMap != null && !foreginSpecifiedMap.isEmpty();
            boolean finishedForeignIndent = false;
            for (SelectedSelectColumnInfo selectColumnInfo : selectColumnInfoList) {
                if (existsSpecifiedForeign && !foreginSpecifiedMap.containsKey(selectColumnInfo.getColumnName())) {
                    continue;
                }

                String realColumnName = selectColumnInfo.buildRealColumnName();
                String columnAliasName = selectColumnInfo.getColumnAliasName();
                String onQueryName;
                ++selectIndex;
                if (_useSelectIndex) {
                    _selectIndexMap.put(columnAliasName, selectIndex);
                    onQueryName = buildSelectIndexAliasName(selectIndex);
                } else {
                    onQueryName = columnAliasName;
                }
                if (!finishedForeignIndent) {
                    sb.append(ln()).append("     ");
                    finishedForeignIndent = true;
                }
                sb.append(", ").append(realColumnName).append(" as ").append(onQueryName);
                _selectClauseRealColumnAliasMap.put(realColumnName, onQueryName);
            }
        }

        // [DBFlute-0.7.4]
        if (_specifiedDeriveSubQueryMap != null && !_specifiedDeriveSubQueryMap.isEmpty()) {
            Collection<String> deriveSubQuerySet = _specifiedDeriveSubQueryMap.values();
            for (String deriveSubQuery : deriveSubQuerySet) {
                sb.append(ln()).append("     ");
                sb.append(", ").append(deriveSubQuery);

                // [DBFlute-0.8.3]
                int beginIndex = deriveSubQuery.lastIndexOf(" as ");
                if (beginIndex >= 0) { // basically true
                    String aliasName = deriveSubQuery.substring(beginIndex + " as ".length());
                    int endIndex = aliasName.indexOf("--df:");
                    if (endIndex >= 0) { // basically true
                        aliasName = aliasName.substring(0, endIndex);
                    }
                    // for SpecifiedDerivedOrderBy
                    _selectClauseRealColumnAliasMap.put(aliasName, aliasName);
                }
            }
        }

        return sb.toString();
    }

    protected boolean isSelectClauseTypeCountOrScalar() {
        if (_selectClauseType.equals(SelectClauseType.COUNT)) {
            return true;
        } else if (_selectClauseType.equals(SelectClauseType.MAX)) {
            return true;
        } else if (_selectClauseType.equals(SelectClauseType.MIN)) {
            return true;
        } else if (_selectClauseType.equals(SelectClauseType.SUM)) {
            return true;
        } else if (_selectClauseType.equals(SelectClauseType.AVG)) {
            return true;
        }
        return false;
    }

    protected String buildSelectClauseCountOrScalar(String aliasName) {
        if (_selectClauseType.equals(SelectClauseType.COUNT)) {
            return buildSelectClauseCount();
        } else if (_selectClauseType.equals(SelectClauseType.MAX)) {
            return buildSelectClauseMax(aliasName);
        } else if (_selectClauseType.equals(SelectClauseType.MIN)) {
            return buildSelectClauseMin(aliasName);
        } else if (_selectClauseType.equals(SelectClauseType.SUM)) {
            return buildSelectClauseSum(aliasName);
        } else if (_selectClauseType.equals(SelectClauseType.AVG)) {
            return buildSelectClauseAvg(aliasName);
        }
        String msg = "The type of select clause is not for scalar:";
        msg = msg + " type=" + _selectClauseType;
        throw new IllegalStateException(msg);
    }

    protected String buildSelectClauseCount() {
        return "select count(*)";
    }

    protected String buildSelectClauseMax(String aliasName) {
        String columnName = getSpecifiedColumnNameAsOne();
        assertScalarSelectSpecifiedColumnOnlyOne(columnName);
        return "select max(" + aliasName + "." + columnName + ")";
    }

    protected String buildSelectClauseMin(String aliasName) {
        String columnName = getSpecifiedColumnNameAsOne();
        assertScalarSelectSpecifiedColumnOnlyOne(columnName);
        return "select min(" + aliasName + "." + columnName + ")";
    }

    protected String buildSelectClauseSum(String aliasName) {
        String columnName = getSpecifiedColumnNameAsOne();
        assertScalarSelectSpecifiedColumnOnlyOne(columnName);
        return "select sum(" + aliasName + "." + columnName + ")";
    }

    protected String buildSelectClauseAvg(String aliasName) {
        String columnName = getSpecifiedColumnNameAsOne();
        assertScalarSelectSpecifiedColumnOnlyOne(columnName);
        return "select avg(" + aliasName + "." + columnName + ")";
    }

    protected void assertScalarSelectSpecifiedColumnOnlyOne(String columnName) {
        if (columnName != null) {
            return;
        }
        String msg = "The specified column exists one";
        msg = msg + " when the type of select clause is for scalar:";
        msg = msg + " specifiedSelectColumnMap=" + _specifiedSelectColumnMap;
        throw new IllegalStateException(msg);
    }

    public Map<String, Integer> getSelectIndexMap() {
        return _selectIndexMap;
    }

    public Map<String, String> getSelectIndexReverseMap() {
        if (_selectIndexMap == null) {
            return null;
        }
        final Map<String, String> selectIndexReverseMap = new HashMap<String, String>();
        for (String columnName : _selectIndexMap.keySet()) {
            Integer selectIndex = _selectIndexMap.get(columnName);
            selectIndexReverseMap.put(buildSelectIndexAliasName(selectIndex), columnName);
        }
        return selectIndexReverseMap;
    }

    public void disableSelectIndex() {
        _useSelectIndex = false;
    }

    protected String buildSelectIndexAliasName(Integer selectIndex) {
        return "c" + selectIndex;
    }

    public String getSelectHint() {
        return createSelectHint();
    }

    public String getFromClause() {
        StringBuilder sb = new StringBuilder();
        sb.append(ln()).append("  ");
        sb.append("from ");
        if (_baseTableInlineWhereList.isEmpty()) {
            sb.append(_tableName).append(" dflocal");
        } else {
            sb.append(getInlineViewClause(_tableName, _baseTableInlineWhereList)).append(" dflocal");
        }
        sb.append(getFromBaseTableHint());
        sb.append(getLeftOuterJoinClause());
        return sb.toString();
    }

    protected String getLeftOuterJoinClause() {
        String fixedConditionKey = getFixedConditionKey();
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> ite = _outerJoinMap.keySet().iterator(); ite.hasNext();) {
            String aliasName = ite.next();
            LeftOuterJoinInfo joinInfo = (LeftOuterJoinInfo) _outerJoinMap.get(aliasName);
            String joinTableName = joinInfo.getJoinTableName();
            List<String> inlineWhereClauseList = joinInfo.getInlineWhereClauseList();
            List<String> additionalOnClauseList = joinInfo.getAdditionalOnClauseList();
            Map<String, String> joinOnMap = joinInfo.getJoinOnMap();
            assertJoinOnMapNotEmpty(joinOnMap, aliasName);

            sb.append(ln()).append("   ");
            if (joinInfo.isInnerJoin()) {
                sb.append(" inner join ");
            } else {
                sb.append(" left outer join "); // is main!
            }
            if (inlineWhereClauseList.isEmpty()) {
                sb.append(joinTableName);
            } else {
                sb.append(getInlineViewClause(joinTableName, inlineWhereClauseList));
            }
            sb.append(" ").append(aliasName).append(" on ");
            int count = 0;
            Set<Entry<String, String>> entrySet = joinOnMap.entrySet();
            for (Entry<String, String> entry : entrySet) {
                String localColumnName = entry.getKey();
                String foreignColumnName = entry.getValue();
                if (count > 0) {
                    sb.append(" and ");
                }
                if (localColumnName.equals(fixedConditionKey)) {
                    sb.append(foreignColumnName);
                } else {
                    sb.append(localColumnName).append(" = ").append(foreignColumnName);
                }
                ++count;
            }
            for (String additionalOnClause : additionalOnClauseList) {
                sb.append(" and ").append(additionalOnClause);
            }
        }
        return sb.toString();
    }

    protected String getInlineViewClause(String joinTableName, List<String> inlineWhereClauseList) {
        StringBuilder sb = new StringBuilder();
        sb.append("(select * from ").append(joinTableName).append(" where ");
        int count = 0;
        for (final Iterator<String> ite = inlineWhereClauseList.iterator(); ite.hasNext();) {
            String clauseElement = ite.next();
            clauseElement = filterWhereClauseSimply(clauseElement);
            if (count > 0) {
                sb.append(" and ");
            }
            sb.append(clauseElement);
            ++count;
        }
        sb.append(")");
        return sb.toString();
    }

    public String getFromBaseTableHint() {
        return createFromBaseTableHint();
    }

    public String getFromHint() {
        return createFromHint();
    }

    public String getWhereClause() {
        return buildWhereClause(false);
    }

    protected String buildWhereClause(boolean template) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Iterator<String> ite = _whereList.iterator(); ite.hasNext(); count++) {
            String clauseElement = (String) ite.next();
            clauseElement = filterWhereClauseSimply(clauseElement);
            if (count == 0) {
                sb.append(ln()).append(" ");
                sb.append("where ").append(template ? getWhereFirstConditionMark() : "").append(clauseElement);
            } else {
                sb.append(ln()).append("  ");
                sb.append(" and ").append(clauseElement);
            }
        }
        if (template && sb.length() == 0) {
            sb.append(getWhereClauseMark());
        }
        return sb.toString();
    }

    public String getOrderByClause() {
        String orderByClause = null;
        if (hasUnionQuery()) {
            if (_selectClauseRealColumnAliasMap == null || _selectClauseRealColumnAliasMap.isEmpty()) {
                String msg = "The selectClauseColumnAliasMap should not be null or empty when union query exists: "
                        + toString();
                throw new IllegalStateException(msg);
            }
            orderByClause = _orderByClause.getOrderByClause(_selectClauseRealColumnAliasMap);
        } else {
            orderByClause = _orderByClause.getOrderByClause();
        }
        if (orderByClause != null && orderByClause.trim().length() > 0) {
            return ln() + " " + orderByClause;
        } else {
            return orderByClause;
        }
    }

    public String getSqlSuffix() {
        String sqlSuffix = createSqlSuffix();
        if (sqlSuffix != null && sqlSuffix.trim().length() > 0) {
            return ln() + sqlSuffix;
        } else {
            return sqlSuffix;
        }
    }

    // ===================================================================================
    //                                                                SelectedSelectColumn
    //                                                                ====================
    /**
     * Register selected select column.
     * 
     * @param foreignTableAliasName The alias name of foreign table. (NotNull)
     * @param localTableName The table name of local. (NotNull)
     * @param foreignPropertyName The property name of foreign table. (NotNull)
     * @param localRelationPath The path of local relation. (Nullable)
     */
    public void registerSelectedSelectColumn(String foreignTableAliasName, String localTableName,
            String foreignPropertyName, String localRelationPath) {
        _selectedSelectColumnMap.put(foreignTableAliasName, createSelectedSelectColumnInfo(foreignTableAliasName,
                localTableName, foreignPropertyName, localRelationPath));
    }

    protected Map<String, SelectedSelectColumnInfo> createSelectedSelectColumnInfo(String foreignTableAliasName,
            String localTableName, String foreignPropertyName, String localRelationPath) {
        final DBMeta dbmeta = findDBMeta(localTableName);
        final ForeignInfo foreignInfo = dbmeta.findForeignInfo(foreignPropertyName);
        final int relationNo = foreignInfo.getRelationNo();
        String nextRelationPath = "_" + relationNo;
        if (localRelationPath != null) {
            nextRelationPath = localRelationPath + nextRelationPath;
        }
        final Map<String, SelectedSelectColumnInfo> resultMap = new LinkedHashMap<String, SelectedSelectColumnInfo>();
        final DBMeta foreignDBMeta = foreignInfo.getForeignDBMeta();
        final List<ColumnInfo> columnInfoList = foreignDBMeta.getColumnInfoList();
        for (ColumnInfo columnInfo : columnInfoList) {
            final String columnDbName = columnInfo.getColumnDbName();
            final SelectedSelectColumnInfo selectColumnInfo = new SelectedSelectColumnInfo();
            selectColumnInfo.setTableAliasName(foreignTableAliasName);
            selectColumnInfo.setColumnName(columnDbName);
            selectColumnInfo.setColumnAliasName(columnDbName + nextRelationPath);
            resultMap.put(columnDbName, selectColumnInfo);
        }
        return resultMap;
    }

    public static class SelectedSelectColumnInfo {
        protected String tableAliasName;
        protected String columnName;
        protected String columnAliasName;

        public String buildRealColumnName() {
            if (tableAliasName != null) {
                return tableAliasName + "." + columnName;
            } else {
                return columnName;
            }
        }

        public String getTableAliasName() {
            return tableAliasName;
        }

        public void setTableAliasName(String tableAliasName) {
            this.tableAliasName = tableAliasName;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnAliasName() {
            return columnAliasName;
        }

        public void setColumnAliasName(String columnAliasName) {
            this.columnAliasName = columnAliasName;
        }
    }

    // ===================================================================================
    //                                                                           OuterJoin
    //                                                                           =========
    /**
     * {@inheritDoc}
     */
    public void registerOuterJoin(String joinTableName, String aliasName, Map<String, String> joinOnMap) {
        assertAlreadyOuterJoin(aliasName);
        assertJoinOnMapNotEmpty(joinOnMap, aliasName);
        final LeftOuterJoinInfo joinInfo = new LeftOuterJoinInfo();
        joinInfo.setAliasName(aliasName);
        joinInfo.setJoinTableName(joinTableName);
        joinInfo.setJoinOnMap(joinOnMap);
        if (_innerJoinEffective) { // basically false
            joinInfo.setInnerJoin(true);
        }
        _outerJoinMap.put(aliasName, joinInfo);
    }

    /**
     * {@inheritDoc}
     */
    public void changeToInnerJoin(String aliasName) {
        final LeftOuterJoinInfo joinInfo = _outerJoinMap.get(aliasName);
        if (joinInfo == null) {
            String msg = "The aliasName should be registered:";
            msg = msg + " aliasName=" + aliasName + " outerJoinMap=" + _outerJoinMap.keySet();
            throw new IllegalStateException(msg);
        }
        joinInfo.setInnerJoin(true);
    }

    public SqlClause makeInnerJoinEffective() {
        _innerJoinEffective = true;
        return this;
    }

    public SqlClause backToOuterJoin() {
        _innerJoinEffective = false;
        return this;
    }

    public String getFixedConditionKey() {
        return "$$fixedCondition$$";
    }

    protected static class LeftOuterJoinInfo {
        protected String _aliasName;
        protected String _joinTableName;
        protected List<String> _inlineWhereClauseList = new ArrayList<String>();
        protected List<String> _additionalOnClauseList = new ArrayList<String>();
        protected Map<String, String> _joinOnMap;
        protected boolean _innerJoin;

        public String getAliasName() {
            return _aliasName;
        }

        public void setAliasName(String value) {
            _aliasName = value;
        }

        public String getJoinTableName() {
            return _joinTableName;
        }

        public void setJoinTableName(String value) {
            _joinTableName = value;
        }

        public List<String> getInlineWhereClauseList() {
            return _inlineWhereClauseList;
        }

        public void addInlineWhereClause(String value) {
            _inlineWhereClauseList.add(value);
        }

        public List<String> getAdditionalOnClauseList() {
            return _additionalOnClauseList;
        }

        public void addAdditionalOnClause(String value) {
            _additionalOnClauseList.add(value);
        }

        public Map<String, String> getJoinOnMap() {
            return _joinOnMap;
        }

        public void setJoinOnMap(Map<String, String> value) {
            _joinOnMap = value;
        }

        public boolean isInnerJoin() {
            return _innerJoin;
        }

        public void setInnerJoin(boolean value) {
            _innerJoin = value;
        }
    }

    protected void assertAlreadyOuterJoin(String aliasName) {
        if (_outerJoinMap.containsKey(aliasName)) {
            String msg = "The alias name have already registered in outer join: " + aliasName;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertJoinOnMapNotEmpty(Map<String, String> joinOnMap, String aliasName) {
        if (joinOnMap.isEmpty()) {
            String msg = "The joinOnMap should not be empty: aliasName=" + aliasName;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                               Where
    //                                                                               =====
    public void registerWhereClause(String columnFullName, ConditionKey key, ConditionValue value) {
        assertStringNotNullAndNotTrimmedEmpty("columnFullName", columnFullName);
        key.addWhereClause(_whereList, columnFullName, value);
        arrangeWhereListAsOrQuery(_whereList);
    }

    public void registerWhereClause(String columnFullName, ConditionKey key, ConditionValue value,
            ConditionOption option) {
        assertStringNotNullAndNotTrimmedEmpty("columnFullName", columnFullName);
        assertObjectNotNull("option of " + columnFullName, option);
        key.addWhereClause(_whereList, columnFullName, value, option);
        arrangeWhereListAsOrQuery(_whereList);
    }

    public void registerWhereClause(String clause) {
        assertStringNotNullAndNotTrimmedEmpty("clause", clause);
        _whereList.add(clause);
        arrangeWhereListAsOrQuery(_whereList);
    }

    public void exchangeFirstWhereClauseForLastOne() {
        if (_whereList.size() > 1) {
            final String first = (String) _whereList.get(0);
            final String last = (String) _whereList.get(_whereList.size() - 1);
            _whereList.set(0, last);
            _whereList.set(_whereList.size() - 1, first);
        }
    }

    public boolean hasWhereClause() {
        return _whereList != null && !_whereList.isEmpty();
    }

    // ===================================================================================
    //                                                                         InlineWhere
    //                                                                         ===========
    public void registerBaseTableInlineWhereClause(String columnName, ConditionKey key, ConditionValue value) {
        assertStringNotNullAndNotTrimmedEmpty("columnName", columnName);
        key.addWhereClause(_baseTableInlineWhereList, columnName, value);
        arrangeWhereListAsOrQuery(_baseTableInlineWhereList);
    }

    public void registerBaseTableInlineWhereClause(String columnName, ConditionKey key, ConditionValue value,
            ConditionOption option) {
        assertStringNotNullAndNotTrimmedEmpty("columnName", columnName);
        assertObjectNotNull("option of " + columnName, option);
        key.addWhereClause(_baseTableInlineWhereList, columnName, value, option);
        arrangeWhereListAsOrQuery(_baseTableInlineWhereList);
    }

    public void registerBaseTableInlineWhereClause(String value) {
        _baseTableInlineWhereList.add(value);
    }

    public void registerOuterJoinInlineWhereClause(String aliasName, String columnName, ConditionKey key,
            ConditionValue value, boolean onClauseInline) {
        assertNotYetOuterJoin(aliasName);
        assertStringNotNullAndNotTrimmedEmpty("columnName", columnName);
        final LeftOuterJoinInfo joinInfo = (LeftOuterJoinInfo) _outerJoinMap.get(aliasName);
        if (onClauseInline) {
            key.addWhereClause(joinInfo.getAdditionalOnClauseList(), aliasName + "." + columnName, value);
        } else {
            key.addWhereClause(joinInfo.getInlineWhereClauseList(), columnName, value);
        }
        arrangeWhereListAsOrQuery(joinInfo.getInlineWhereClauseList());
    }

    public void registerOuterJoinInlineWhereClause(String aliasName, String columnName, ConditionKey key,
            ConditionValue value, ConditionOption option, boolean onClauseInline) {
        assertNotYetOuterJoin(aliasName);
        assertStringNotNullAndNotTrimmedEmpty("columnName", columnName);
        final LeftOuterJoinInfo joinInfo = (LeftOuterJoinInfo) _outerJoinMap.get(aliasName);
        if (onClauseInline) {
            key.addWhereClause(joinInfo.getAdditionalOnClauseList(), aliasName + "." + columnName, value, option);
            arrangeWhereListAsOrQuery(joinInfo.getAdditionalOnClauseList());
        } else {
            key.addWhereClause(joinInfo.getInlineWhereClauseList(), columnName, value, option);
            arrangeWhereListAsOrQuery(joinInfo.getInlineWhereClauseList());
        }
    }

    public void registerOuterJoinInlineWhereClause(String aliasName, String value, boolean onClauseInline) {
        assertNotYetOuterJoin(aliasName);
        final LeftOuterJoinInfo joinInfo = (LeftOuterJoinInfo) _outerJoinMap.get(aliasName);
        if (onClauseInline) {
            joinInfo.addAdditionalOnClause(value);
            arrangeWhereListAsOrQuery(joinInfo.getAdditionalOnClauseList());
        } else {
            joinInfo.addInlineWhereClause(value);
            arrangeWhereListAsOrQuery(joinInfo.getInlineWhereClauseList());
        }
    }

    protected void assertNotYetOuterJoin(String aliasName) {
        if (!_outerJoinMap.containsKey(aliasName)) {
            String msg = "The alias name have not registered in outer join yet: " + aliasName;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                             OrQuery
    //                                                                             =======
    public void makeOrQueryEffective() {
        _orQueryEffective = true;
    }

    public void ignoreOrQuery() {
        _orQueryEffective = false;
    }

    public boolean isOrQueryEffective() {
        return _orQueryEffective;
    }

    protected void arrangeWhereListAsOrQuery(List<String> whereList) {
        if (!_orQueryEffective) {
            return;
        }
        if (whereList.size() < 2) {
            return;
        }
        final String or = ln() + "    or ";
        final String newClause = (String) whereList.remove(whereList.size() - 1);
        final String preClause = (String) whereList.remove(whereList.size() - 1);
        if (preClause.startsWith("(") && preClause.contains(or) && preClause.endsWith(")")) {
            // Since the second times
            final int beginLen = "(".length();
            final int endLen = ")".length();
            final String plainClause = preClause.substring(beginLen, preClause.length() - endLen);
            whereList.add("(" + plainClause + or + newClause + ")");
        } else {
            // At first
            whereList.add("(" + preClause + or + newClause + ")");
        }
    }

    // ===================================================================================
    //                                                                             OrderBy
    //                                                                             =======
    public OrderByClause getSqlComponentOfOrderByClause() {
        return _orderByClause;
    }

    public SqlClause clearOrderBy() {
        _orderByEffective = false;
        _orderByClause.clear();
        return this;
    }

    public SqlClause makeOrderByEffective() {
        if (!_orderByClause.isEmpty()) {
            _orderByEffective = true;
        }
        return this;
    }

    public SqlClause ignoreOrderBy() {
        _orderByEffective = false;
        return this;
    }

    public void reverseOrderBy_Or_OverrideOrderBy(String orderByProperty, String registeredOrderByProperty,
            boolean ascOrDesc) {
        _orderByEffective = true;
        if (!_orderByClause.isSameOrderByColumn(orderByProperty)) {
            clearOrderBy();
            registerOrderBy(orderByProperty, registeredOrderByProperty, ascOrDesc);
        } else {
            _orderByClause.reverseAll();
        }
    }

    public void registerOrderBy(String orderByProperty, String registeredOrderByProperty, boolean ascOrDesc) {
        try {
            _orderByEffective = true;
            final List<String> orderByList = new ArrayList<String>();
            {
                final StringTokenizer st = new StringTokenizer(orderByProperty, "/");
                while (st.hasMoreElements()) {
                    orderByList.add(st.nextToken());
                }
            }

            if (registeredOrderByProperty == null || registeredOrderByProperty.trim().length() == 0) {
                registeredOrderByProperty = orderByProperty;
            }

            final List<String> registeredOrderByList = new ArrayList<String>();
            {
                final StringTokenizer st = new StringTokenizer(registeredOrderByProperty, "/");
                while (st.hasMoreElements()) {
                    registeredOrderByList.add(st.nextToken());
                }
            }

            int count = 0;
            for (final Iterator<String> ite = orderByList.iterator(); ite.hasNext();) {
                String orderBy = ite.next();
                String registeredOrderBy = (String) registeredOrderByList.get(count);

                _orderByEffective = true;
                String aliasName = null;
                String columnName = null;
                String registeredAliasName = null;
                String registeredColumnName = null;

                if (orderBy.indexOf(".") < 0) {
                    columnName = orderBy;
                } else {
                    aliasName = orderBy.substring(0, orderBy.lastIndexOf("."));
                    columnName = orderBy.substring(orderBy.lastIndexOf(".") + 1);
                }

                if (registeredOrderBy.indexOf(".") < 0) {
                    registeredColumnName = registeredOrderBy;
                } else {
                    registeredAliasName = registeredOrderBy.substring(0, registeredOrderBy.lastIndexOf("."));
                    registeredColumnName = registeredOrderBy.substring(registeredOrderBy.lastIndexOf(".") + 1);
                }

                OrderByElement element = new OrderByElement();
                element.setAliasName(aliasName);
                element.setColumnName(columnName);
                element.setRegisteredAliasName(registeredAliasName);
                element.setRegisteredColumnName(registeredColumnName);
                if (ascOrDesc) {
                    element.setupAsc();
                } else {
                    element.setupDesc();
                }
                _orderByClause.addOrderByElement(element);

                count++;
            }
        } catch (RuntimeException e) {
            String msg = "registerOrderBy() threw the exception: orderByProperty=" + orderByProperty;
            msg = msg + " registeredColumnFullName=" + registeredOrderByProperty;
            msg = msg + " ascOrDesc=" + ascOrDesc;
            msg = msg + " sqlClause=" + this.toString();
            throw new RuntimeException(msg, e);
        }
    }

    public void addNullsFirstToPreviousOrderBy() {
        _orderByClause.addNullsFirstToPreviousOrderByElement(createOrderByNullsSetupper());
    }

    public void addNullsLastToPreviousOrderBy() {
        _orderByClause.addNullsLastToPreviousOrderByElement(createOrderByNullsSetupper());
    }

    protected OrderByClause.OrderByNullsSetupper createOrderByNullsSetupper() {// As Default
        return new OrderByClause.OrderByNullsSetupper() {
            public String setup(String columnName, String orderByElementClause, boolean nullsFirst) {
                return orderByElementClause + " nulls " + (nullsFirst ? "first" : "last");
            }
        };
    }

    protected OrderByClause.OrderByNullsSetupper createOrderByNullsSetupperByCaseWhen() {// Helper For Nulls Unsupported Database
        return new OrderByClause.OrderByNullsSetupper() {
            public String setup(String columnName, String orderByElementClause, boolean nullsFirst) {
                final String thenNumber = nullsFirst ? "1" : "0";
                final String elseNumber = nullsFirst ? "0" : "1";
                final String caseWhen = "case when " + columnName + " is not null then " + thenNumber + " else "
                        + elseNumber + " end asc";
                return caseWhen + ", " + orderByElementClause;
            }
        };
    }

    public void addManualOrderToPreviousOrderByElement(ManumalOrderInfo manumalOrderInfo) {
        assertObjectNotNull("manumalOrderInfo", manumalOrderInfo);
        if (hasUnionQuery()) {
            String msg = "Manual Order with Union is unavailable: " + manumalOrderInfo.getManualValueList();
            throw new IllegalConditionBeanOperationException(msg);
        }
        _orderByClause.addManualOrderByElement(manumalOrderInfo);
    }

    public boolean hasOrderByClause() {
        return _orderByClause != null && !_orderByClause.isEmpty();
    }

    // ===================================================================================
    //                                                                          UnionQuery
    //                                                                          ==========
    public void registerUnionQuery(String unionQueryClause, boolean unionAll) {
        assertStringNotNullAndNotTrimmedEmpty("unionQueryClause", unionQueryClause);
        UnionQueryInfo unionQueryInfo = new UnionQueryInfo();
        unionQueryInfo.setUnionQueryClause(unionQueryClause);
        unionQueryInfo.setUnionAll(unionAll);
        addUnionQueryInfo(unionQueryInfo);
    }

    protected void addUnionQueryInfo(UnionQueryInfo unionQueryInfo) {
        if (_unionQueryInfoList == null) {
            _unionQueryInfoList = new ArrayList<UnionQueryInfo>();
        }
        _unionQueryInfoList.add(unionQueryInfo);
    }

    public boolean hasUnionQuery() {
        return _unionQueryInfoList != null && !_unionQueryInfoList.isEmpty();
    }

    protected static class UnionQueryInfo {
        protected String _unionQueryClause;
        protected boolean _unionAll;

        public String getUnionQueryClause() {
            return _unionQueryClause;
        }

        public void setUnionQueryClause(String unionQueryClause) {
            _unionQueryClause = unionQueryClause;
        }

        public boolean isUnionAll() {
            return _unionAll;
        }

        public void setUnionAll(boolean unionAll) {
            _unionAll = unionAll;
        }
    }

    // ===================================================================================
    //                                                                          FetchScope
    //                                                                          ==========
    /**
     * @param fetchSize Fetch-size. (NotMinus & NotZero)
     * @return this. (NotNull)
     */
    public SqlClause fetchFirst(int fetchSize) {
        _fetchScopeEffective = true;
        if (fetchSize <= 0) {
            String msg = "Argument[fetchSize] should be plus: " + fetchSize;
            throw new IllegalArgumentException(msg);
        }
        _fetchStartIndex = 0;
        _fetchSize = fetchSize;
        _fetchPageNumber = 1;
        doClearFetchPageClause();
        doFetchFirst();
        return this;
    }

    /**
     * @param fetchStartIndex Fetch-start-index. 0 origin. (NotMinus)
     * @param fetchSize Fetch size. (NotMinus)
     * @return this. (NotNull)
     */
    public SqlClause fetchScope(int fetchStartIndex, int fetchSize) {
        _fetchScopeEffective = true;
        if (fetchStartIndex < 0) {
            String msg = "Argument[fetchStartIndex] must be plus or zero: " + fetchStartIndex;
            throw new IllegalArgumentException(msg);
        }
        if (fetchSize <= 0) {
            String msg = "Argument[fetchSize] should be plus: " + fetchSize;
            throw new IllegalArgumentException(msg);
        }
        _fetchStartIndex = fetchStartIndex;
        _fetchSize = fetchSize;
        return fetchPage(1);
    }

    /**
     * @param fetchPageNumber Page-number. 1 origin. (NotMinus & NotZero: If minus or zero, set one.)
     * @return this. (NotNull)
     */
    public SqlClause fetchPage(int fetchPageNumber) {
        _fetchScopeEffective = true;
        if (fetchPageNumber <= 0) {
            fetchPageNumber = 1;
        }
        if (_fetchSize <= 0) {
            throwFetchSizeNotPlusException(fetchPageNumber);
        }
        _fetchPageNumber = fetchPageNumber;
        if (_fetchPageNumber == 1 && _fetchStartIndex == 0) {
            return fetchFirst(_fetchSize);
        }
        doClearFetchPageClause();
        doFetchPage();
        return this;
    }

    protected void throwFetchSizeNotPlusException(int fetchPageNumber) { // as system exception
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "Fetch size should not be minus or zero!" + ln();
        msg = msg + ln();
        msg = msg + "[Fetch Size]" + ln();
        msg = msg + "fetchSize=" + _fetchSize + ln();
        msg = msg + ln();
        msg = msg + "[Fetch Page Number]" + ln();
        msg = msg + "fetchPageNumber=" + fetchPageNumber + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalStateException(msg);
    }

    abstract protected void doFetchFirst();

    abstract protected void doFetchPage();

    abstract protected void doClearFetchPageClause();

    public int getFetchStartIndex() {
        return _fetchStartIndex;
    }

    public int getFetchSize() {
        return _fetchSize;
    }

    public int getFetchPageNumber() {
        return _fetchPageNumber;
    }

    /**
     * @return Page start index. 0 origin. (NotMinus)
     */
    public int getPageStartIndex() {
        if (_fetchPageNumber <= 0) {
            String msg = "_fetchPageNumber must be plus: " + _fetchPageNumber;
            throw new IllegalStateException(msg);
        }
        return _fetchStartIndex + (_fetchSize * (_fetchPageNumber - 1));
    }

    /**
     * @return Page end index. 0 origin. (NotMinus)
     */
    public int getPageEndIndex() {
        if (_fetchPageNumber <= 0) {
            String msg = "_fetchPageNumber must be plus: " + _fetchPageNumber;
            throw new IllegalStateException(msg);
        }
        return _fetchStartIndex + (_fetchSize * _fetchPageNumber);
    }

    public boolean isFetchScopeEffective() {
        return _fetchScopeEffective;
    }

    public SqlClause ignoreFetchScope() {
        _fetchScopeEffective = false;
        doClearFetchPageClause();
        return this;
    }

    public SqlClause makeFetchScopeEffective() {
        if (getFetchSize() > 0 && getFetchPageNumber() > 0) {
            fetchPage(getFetchPageNumber());
        }
        return this;
    }

    public boolean isFetchStartIndexSupported() {
        return true; // Default
    }

    public boolean isFetchSizeSupported() {
        return true; // Default
    }

    abstract protected String createSelectHint();

    abstract protected String createFromBaseTableHint();

    abstract protected String createFromHint();

    abstract protected String createSqlSuffix();

    // ===================================================================================
    //                                                                     Fetch Narrowing
    //                                                                     ===============
    /**
     * {@inheritDoc}
     */
    public int getFetchNarrowingSkipStartIndex() {
        return getPageStartIndex();
    }

    /**
     * {@inheritDoc}
     */
    public int getFetchNarrowingLoopCount() {
        return getFetchSize();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFetchNarrowingEffective() {
        return _fetchScopeEffective;
    }

    // ===================================================================================
    //                                                                            Resolver
    //                                                                            ========
    public String resolveJoinAliasName(String relationPath, int cqNestNo) {
        return resolveNestLevelExpression("dfrelation" + relationPath, cqNestNo);
    }

    public String resolveNestLevelExpression(String name, int cqNestNo) {
        // *comment out old style 
        //if (cqNestNo > 1) {
        //    return name + "_n" + cqNestNo;
        //} else {
        //    return name;
        //}
        return name;
    }

    public int resolveRelationNo(String localTableName, String foreignPropertyName) {
        final DBMeta dbmeta = findDBMeta(localTableName);
        final ForeignInfo foreignInfo = dbmeta.findForeignInfo(foreignPropertyName);
        return foreignInfo.getRelationNo();
    }

    // ===================================================================================
    //                                                                    Table Alias Info
    //                                                                    ================
    public String getLocalTableAliasName() {
        return "dflocal";
    }

    public String getForeignTableAliasPrefix() {
        return "dfrelation";
    }

    // ===================================================================================
    //                                                                       Template Mark
    //                                                                       =============
    public String getWhereClauseMark() {
        return "#df:whereClause#";
    }

    public String getWhereFirstConditionMark() {
        return "#df:whereFirstCondition#";
    }

    public String getUnionSelectClauseMark() {
        return "#df:unionSelectClause#";
    }

    public String getUnionWhereClauseMark() {
        return "#df:unionWhereClause#";
    }

    public String getUnionWhereFirstConditionMark() {
        return "#df:unionWhereFirstCondition#";
    }

    // =====================================================================================
    //                                                            Where Clause Simple Filter
    //                                                            ==========================
    public void addWhereClauseSimpleFilter(WhereClauseSimpleFilter whereClauseSimpleFilter) {
        if (_whereClauseSimpleFilterList == null) {
            _whereClauseSimpleFilterList = new ArrayList<WhereClauseSimpleFilter>();
        }
        _whereClauseSimpleFilterList.add(whereClauseSimpleFilter);
    }

    protected String filterWhereClauseSimply(String clauseElement) {
        if (_whereClauseSimpleFilterList == null || _whereClauseSimpleFilterList.isEmpty()) {
            return clauseElement;
        }
        for (final Iterator<WhereClauseSimpleFilter> ite = _whereClauseSimpleFilterList.iterator(); ite.hasNext();) {
            final WhereClauseSimpleFilter filter = ite.next();
            if (filter == null) {
                String msg = "The list of filter should not have null: _whereClauseSimpleFilterList="
                        + _whereClauseSimpleFilterList;
                throw new IllegalStateException(msg);
            }
            clauseElement = filter.filterClauseElement(clauseElement);
        }
        return clauseElement;
    }

    // =====================================================================================
    //                                                                 Selected Foreign Info
    //                                                                 =====================
    public boolean isSelectedForeignInfoEmpty() {
        if (_selectedForeignInfo == null) {
            return true;
        }
        return _selectedForeignInfo.isEmpty();
    }

    public boolean hasSelectedForeignInfo(String relationPath) {
        if (_selectedForeignInfo == null) {
            return false;
        }
        return _selectedForeignInfo.containsKey(relationPath);
    }

    public void registerSelectedForeignInfo(String relationPath, String foreignPropertyName) {
        if (_selectedForeignInfo == null) {
            _selectedForeignInfo = new HashMap<String, String>();
        }
        _selectedForeignInfo.put(relationPath, foreignPropertyName);
    }

    // ===================================================================================
    //                                                                    Sub Query Indent
    //                                                                    ================
    public String resolveSubQueryBeginMark(String subQueryIdentity) {
        return getSubQueryBeginMarkPrefix() + subQueryIdentity + getSubQueryIdentityTerminal();
    }

    public String resolveSubQueryEndMark(String subQueryIdentity) {
        return getSubQueryEndMarkPrefix() + subQueryIdentity + getSubQueryIdentityTerminal();
    }

    protected String getSubQueryBeginMarkPrefix() {
        return "--df:SubQueryBegin#";
    }

    protected String getSubQueryEndMarkPrefix() {
        return "--df:SubQueryEnd#";
    }

    protected String getSubQueryIdentityTerminal() {
        return "#IdentityTerminal#";
    }

    public String filterSubQueryIndent(String sql) {
        return filterSubQueryIndent(sql, "", sql);
    }

    protected String filterSubQueryIndent(String sql, String preIndent, String originalSql) {
        final String lineSeparator = ln();
        if (!sql.contains(getSubQueryBeginMarkPrefix())) {
            return sql;
        }
        final String[] lines = sql.split(lineSeparator);
        final String beginMarkPrefix = getSubQueryBeginMarkPrefix();
        final String endMarkPrefix = getSubQueryEndMarkPrefix();
        final String identityTerminal = getSubQueryIdentityTerminal();
        final int terminalLength = identityTerminal.length();
        StringBuilder mainSb = new StringBuilder();
        StringBuilder subSb = null;
        boolean throughBegin = false;
        boolean throughBeginFirst = false;
        String subQueryIdentity = null;
        String indent = null;
        for (String line : lines) {
            if (!throughBegin) {
                if (line.contains(beginMarkPrefix)) {
                    throughBegin = true;
                    subSb = new StringBuilder();
                    final int markIndex = line.indexOf(beginMarkPrefix);
                    final int terminalIndex = line.indexOf(identityTerminal);
                    if (terminalIndex < 0) {
                        String msg = "Identity terminal was Not Found at the begin line: [" + line + "]";
                        throw new SubQueryIndentFailureException(msg);
                    }
                    final String clause = line.substring(0, markIndex) + line.substring(terminalIndex + terminalLength);
                    subQueryIdentity = line.substring(markIndex + beginMarkPrefix.length(), terminalIndex);
                    subSb.append(clause);
                    indent = buildSpaceBar(markIndex - preIndent.length());
                } else {
                    mainSb.append(line).append(ln());
                }
            } else {
                // - - - - - - - -
                // In begin to end
                // - - - - - - - -
                if (line.contains(endMarkPrefix + subQueryIdentity)) { // The end
                    final int markIndex = line.indexOf(endMarkPrefix);
                    final int terminalIndex = line.indexOf(identityTerminal);
                    if (terminalIndex < 0) {
                        String msg = "Identity terminal was Not Found at the begin line: [" + line + "]";
                        throw new SubQueryIndentFailureException(msg);
                    }
                    final String clause = line.substring(0, markIndex) + line.substring(terminalIndex + terminalLength);
                    subSb.append(clause).append(ln());
                    final String currentSql = filterSubQueryIndent(subSb.toString(), preIndent + indent, originalSql);
                    mainSb.append(currentSql);
                    throughBegin = false;
                    throughBeginFirst = false;
                } else {
                    if (!throughBeginFirst) {
                        subSb.append(line.trim()).append(ln());
                        throughBeginFirst = true;
                    } else {
                        subSb.append(indent).append(line).append(ln());
                    }
                }
            }
        }
        final String filteredSql = mainSb.toString();

        if (throughBegin) {
            String msg = "End Mark Not Found!";
            msg = msg + ln() + "[Current SubQueryIdentity]" + ln();
            msg = msg + subQueryIdentity + ln();
            msg = msg + ln() + "[Before Filter]" + ln() + sql;
            msg = msg + ln() + "[After Filter]" + ln() + filteredSql;
            msg = msg + ln() + "[Original SQL]" + ln() + originalSql;
            throw new SubQueryIndentFailureException(msg);
        }
        if (filteredSql.contains(beginMarkPrefix)) {
            String msg = "Any begin marks are not filtered!";
            msg = msg + ln() + "[Current SubQueryIdentity]" + ln();
            msg = msg + subQueryIdentity + ln();
            msg = msg + ln() + "[Before Filter]" + ln() + sql;
            msg = msg + ln() + "[After Filter]" + ln() + filteredSql;
            msg = msg + ln() + "[Original SQL]" + ln() + originalSql;
            throw new SubQueryIndentFailureException(msg);
        }
        return filteredSql;
    }

    protected String buildSpaceBar(int size) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static class SubQueryIndentFailureException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public SubQueryIndentFailureException(String msg) {
            super(msg);
        }
    }

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                       Specification
    //                                                                       =============
    public void specifySelectColumn(String tableAliasName, String columnName) {
        if (_specifiedSelectColumnMap == null) {
            _specifiedSelectColumnMap = new HashMap<String, Map<String, String>>();
        }
        if (!_specifiedSelectColumnMap.containsKey(tableAliasName)) {
            _specifiedSelectColumnMap.put(tableAliasName, new LinkedHashMap<String, String>());
        }
        Map<String, String> elementMap = _specifiedSelectColumnMap.get(tableAliasName);
        elementMap.put(columnName, null); // The value is dummy for extension at the future.
        _specifiedSelectColumnMap.put(tableAliasName, elementMap);
    }

    public void specifyDeriveSubQuery(String aliasName, String deriveSubQuery) {
        if (_specifiedDeriveSubQueryMap == null) {
            _specifiedDeriveSubQueryMap = new LinkedHashMap<String, String>();
        }
        _specifiedDeriveSubQueryMap.put(aliasName, deriveSubQuery);
    }

    public boolean hasSpecifiedDeriveSubQuery(String aliasName) {
        if (_specifiedDeriveSubQueryMap == null) {
            return false;
        }
        return _specifiedDeriveSubQueryMap.containsKey(aliasName);
    }

    public String getSpecifiedColumnNameAsOne() {
        if (_specifiedSelectColumnMap != null && _specifiedSelectColumnMap.size() == 1) {
            String tableAliasName = _specifiedSelectColumnMap.keySet().iterator().next();
            Map<String, String> elementMap = _specifiedSelectColumnMap.get(tableAliasName);
            if (elementMap != null && elementMap.size() == 1) {
                return elementMap.keySet().iterator().next();
            }
        }
        return null;
    }

    public String getSpecifiedColumnRealNameAsOne() {
        return doGetSpecifiedColumnRealNameAsOne(false);
    }

    public String removeSpecifiedColumnRealNameAsOne() {
        return doGetSpecifiedColumnRealNameAsOne(true);
    }

    private String doGetSpecifiedColumnRealNameAsOne(boolean remove) {
        if (_specifiedSelectColumnMap != null && _specifiedSelectColumnMap.size() == 1) {
            String tableAliasName = _specifiedSelectColumnMap.keySet().iterator().next();
            Map<String, String> elementMap = _specifiedSelectColumnMap.get(tableAliasName);
            if (elementMap != null && elementMap.size() == 1) {
                String columnName = elementMap.keySet().iterator().next();
                String realName = tableAliasName + "." + columnName;
                if (remove) {
                    elementMap.remove(columnName);
                }
                return realName;
            }
        }
        return null;
    }

    public void backupSpecifiedSelectColumn() {
        _backupSpecifiedSelectColumnMap = _specifiedSelectColumnMap;
    }

    public void restoreSpecifiedSelectColumn() {
        _specifiedSelectColumnMap = _backupSpecifiedSelectColumnMap;
        _backupSpecifiedSelectColumnMap = null;
    }

    public void clearSpecifiedSelectColumn() {
        if (_specifiedSelectColumnMap != null) {
            _specifiedSelectColumnMap.clear();
            _specifiedSelectColumnMap = null;
        }
    }

    // [DBFlute-0.7.5]
    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    public String getClauseQueryUpdate(Map<String, String> columnParameterMap) {
        if (columnParameterMap.isEmpty()) {
            return null;
        }
        final String aliasName = getLocalTableAliasName();
        final DBMeta dbmeta = findDBMeta(_tableName);
        final String primaryKeyName = dbmeta.getPrimaryUniqueInfo().getFirstColumn().getColumnDbName();
        final String selectClause = "select " + aliasName + "." + primaryKeyName;
        String fromWhereClause = getClauseFromWhereWithUnionTemplate();

        // Replace template marks. These are very important!
        fromWhereClause = replaceString(fromWhereClause, getUnionSelectClauseMark(), selectClause);
        fromWhereClause = replaceString(fromWhereClause, getUnionWhereClauseMark(), "");
        fromWhereClause = replaceString(fromWhereClause, getUnionWhereFirstConditionMark(), "");

        final StringBuilder sb = new StringBuilder();
        String ln = ln();
        sb.append("update ").append(_tableName).append(ln);
        int index = 0;
        // It is guaranteed that the map has one or more elements.
        final Set<Entry<String, String>> entrySet = columnParameterMap.entrySet();
        for (Entry<String, String> entry : entrySet) {
            final String columnName = entry.getKey();
            final String parameter = entry.getValue();
            if (index == 0) {
                sb.append("   set ").append(columnName).append(" = ").append(parameter).append(ln);
            } else {
                sb.append("     , ").append(columnName).append(" = ").append(parameter).append(ln);
            }
            ++index;
        }
        if (isUpdateSubQueryUseLocalTableSupported() && !dbmeta.hasTwoOrMorePrimaryKeys()) {
            final String subQuery = filterSubQueryIndent(selectClause + " " + fromWhereClause);
            sb.append(" where ").append(primaryKeyName);
            sb.append(" in (").append(ln).append(subQuery).append(ln).append(")");
            return sb.toString();
        } else {
            if (_outerJoinMap != null && !_outerJoinMap.isEmpty()) {
                String msg = "The queryUpdate() with outer join is unavailable";
                msg = msg + " because your DB does not support it or the table has two-or-more primary keys:";
                msg = msg + " tableName=" + _tableName;
                throw new IllegalConditionBeanOperationException(msg);
            }
            if (_unionQueryInfoList != null && !_unionQueryInfoList.isEmpty()) {
                String msg = "The queryUpdate() with union is unavailable";
                msg = msg + " because your DB does not support it or the table has two-or-more primary keys:";
                msg = msg + " tableName=" + _tableName;
                throw new IllegalConditionBeanOperationException(msg);
            }
            String subQuery = filterSubQueryIndent(fromWhereClause);
            subQuery = replaceString(subQuery, aliasName + ".", "");
            subQuery = replaceString(subQuery, " " + aliasName + " ", " ");
            int whereIndex = subQuery.indexOf("where ");
            if (whereIndex < 0) {
                return sb.toString();
            }
            subQuery = subQuery.substring(whereIndex);
            sb.append(" ").append(subQuery);
            return sb.toString();
        }
    }

    public String getClauseQueryDelete() {
        final String aliasName = getLocalTableAliasName();
        final DBMeta dbmeta = findDBMeta(_tableName);
        final String primaryKeyName = dbmeta.getPrimaryUniqueInfo().getFirstColumn().getColumnDbName();
        final String selectClause = "select " + aliasName + "." + primaryKeyName;
        String fromWhereClause = getClauseFromWhereWithUnionTemplate();

        // Replace template marks. These are very important!
        fromWhereClause = replaceString(fromWhereClause, getUnionSelectClauseMark(), selectClause);
        fromWhereClause = replaceString(fromWhereClause, getUnionWhereClauseMark(), "");
        fromWhereClause = replaceString(fromWhereClause, getUnionWhereFirstConditionMark(), "");

        if (isUpdateSubQueryUseLocalTableSupported() && !dbmeta.hasTwoOrMorePrimaryKeys()) {
            final String subQuery = filterSubQueryIndent(selectClause + " " + fromWhereClause);
            final StringBuilder sb = new StringBuilder();
            String ln = ln();
            sb.append("delete from ").append(_tableName).append(ln);
            sb.append(" where ").append(primaryKeyName);
            sb.append(" in (").append(ln).append(subQuery).append(ln).append(")");
            return sb.toString();
        } else { // unsupported or two-or-more primary keys
            if (_outerJoinMap != null && !_outerJoinMap.isEmpty()) {
                String msg = "The queryDelete() with outer join is unavailable";
                msg = msg + " because your DB does not support it or the table has two-or-more primary keys:";
                msg = msg + " tableName=" + _tableName;
                throw new IllegalConditionBeanOperationException(msg);
            }
            if (_unionQueryInfoList != null && !_unionQueryInfoList.isEmpty()) {
                String msg = "The queryDelete() with union is unavailable";
                msg = msg + " because your DB does not support it or the table has two-or-more primary keys:";
                msg = msg + " tableName=" + _tableName;
                throw new IllegalConditionBeanOperationException(msg);
            }
            String subQuery = filterSubQueryIndent(fromWhereClause);
            subQuery = replaceString(subQuery, aliasName + ".", "");
            subQuery = replaceString(subQuery, " " + aliasName + " ", " ");
            subQuery = subQuery.substring(subQuery.indexOf("from "));
            return "delete " + subQuery;
        }
    }

    protected boolean isUpdateSubQueryUseLocalTableSupported() {
        return true;
    }

    // [DBFlute-0.8.6]
    // ===================================================================================
    //                                                                  Select Clause Type
    //                                                                  ==================
    public void classifySelectClauseType(SelectClauseType selectClauseType) {
        changeSelectClauseType(selectClauseType);
    }

    protected void changeSelectClauseType(SelectClauseType selectClauseType) {
        savePreviousSelectClauseType();
        _selectClauseType = selectClauseType;
    }

    protected void savePreviousSelectClauseType() {
        _previousSelectClauseType = _selectClauseType;
    }

    public void rollbackSelectClauseType() {
        _selectClauseType = _previousSelectClauseType != null ? _previousSelectClauseType : DEFAULT_SELECT_CLAUSE_TYPE;
    }

    // [DBFlute-0.9.4]
    // ===================================================================================
    //                                                                       InScope Limit
    //                                                                       =============
    public int getInScopeLimit() {
        return 0; // as default
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected DBMeta findDBMeta(String tableName) {
        if (_dbmetaProvider == null) {
            String msg = "The DB meta provider should not be null when using findDBMeta(): ";
            msg = msg + " tableName=" + tableName;
            throw new IllegalStateException(msg);
        }
        return _dbmetaProvider.provideDBMetaChecked(tableName);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // -----------------------------------------------------
    //                                         Assert Object
    //                                         -------------
    protected void assertObjectNotNull(String variableName, Object value) {
        DfAssertUtil.assertObjectNotNull(variableName, value);
    }

    // -----------------------------------------------------
    //                                         Assert String
    //                                         -------------
    protected void assertStringNotNullAndNotTrimmedEmpty(String variableName, String value) {
        DfAssertUtil.assertStringNotNullAndNotTrimmedEmpty(variableName, value);
    }
}
