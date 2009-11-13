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
package org.seasar.robot.dbflute.cbean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;
import org.seasar.robot.dbflute.cbean.ckey.ConditionKeyInScope;
import org.seasar.robot.dbflute.cbean.coption.ConditionOption;
import org.seasar.robot.dbflute.cbean.coption.FromToOption;
import org.seasar.robot.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseMySql;
import org.seasar.robot.dbflute.cbean.sqlclause.OrderByClause.ManumalOrderInfo;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.robot.dbflute.exception.RequiredOptionNotFoundException;
import org.seasar.robot.dbflute.util.DfCollectionUtil;
import org.seasar.robot.dbflute.util.DfStringUtil;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.DfTraceViewUtil;

/**
 * The abstract class of condition-query.
 * @author jflute
 */
public abstract class AbstractConditionQuery implements ConditionQuery {
// Don't format!

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final ConditionKey CK_EQ = ConditionKey.CK_EQUAL;
    protected static final ConditionKey CK_NE = ConditionKey.CK_NOT_EQUAL;
    protected static final ConditionKey CK_GE = ConditionKey.CK_GREATER_EQUAL;
    protected static final ConditionKey CK_GT = ConditionKey.CK_GREATER_THAN;
    protected static final ConditionKey CK_LE = ConditionKey.CK_LESS_EQUAL;
    protected static final ConditionKey CK_LT = ConditionKey.CK_LESS_THAN;
    protected static final ConditionKey CK_INS = ConditionKey.CK_IN_SCOPE;
    protected static final ConditionKey CK_NINS = ConditionKey.CK_NOT_IN_SCOPE;
    protected static final ConditionKey CK_LS = ConditionKey.CK_LIKE_SEARCH;
    protected static final ConditionKey CK_NLS = ConditionKey.CK_NOT_LIKE_SEARCH;
    protected static final ConditionKey CK_ISN = ConditionKey.CK_IS_NULL;
    protected static final ConditionKey CK_ISNN = ConditionKey.CK_IS_NOT_NULL;

    /** Object for DUMMY. */
    protected static final Object DOBJ = new Object();

    /** The property of condition-query. */
    protected static final String CQ_PROPERTY = "conditionQuery";
    
    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** SQL clause. */
    protected final SqlClause _sqlClause;

    /** My alias name. */
    protected final String _aliasName;

    /** The level of nest. */
    protected final int _nestLevel;
    
    /** The level of subQuery. */
    protected int _subQueryLevel;

    // -----------------------------------------------------
    //                                          Foreign Info
    //                                          ------------
    /** The property name of foreign. */
    protected String _foreignPropertyName;

    /** The path of relation. */
    protected String _relationPath;

    /** The referrer query. */
    protected final ConditionQuery _referrerQuery;

    // -----------------------------------------------------
    //                                                Inline
    //                                                ------
    /** Is it the inline for on-clause. (Property for Inline Only) */
    protected boolean _onClauseInline;
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param referrerQuery The instance of referrer query. (Nullable: If null, this is base query)
     * @param sqlClause The instance of SQL clause. (NotNull)
     * @param aliasName The alias name for this query. (NotNull)
     * @param nestLevel The nest level of this query. (If zero, this is base query)
     */
    public AbstractConditionQuery(ConditionQuery referrerQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        _referrerQuery = referrerQuery;
        _sqlClause = sqlClause;
        _aliasName = aliasName;
        _nestLevel = nestLevel;
    }

    // ===================================================================================
    //                                                                     DBMeta Provider
    //                                                                     ===============
    /**
     * Get the provider of DB meta.
     * @return The provider of DB meta. (NotNull)
     */
    protected abstract DBMetaProvider getDBMetaProvider();

    protected DBMeta findDBMeta(String tableFlexibleName) {
        return getDBMetaProvider().provideDBMetaChecked(tableFlexibleName);
    }

    // ===================================================================================
    //                                                                  Important Accessor
    //                                                                  ==================
    /**
     * {@inheritDoc}
     */
    public ConditionQuery getReferrerQuery() {
        return _referrerQuery;
    }

    /**
     * Get sql clause.
     * @return Sql clause. (NotNull)
     */
    public SqlClause getSqlClause() {
        return _sqlClause;
    }

    /**
     * Get alias name.
     * @return Alias name. (NotNull)
     */
    public String getAliasName() {
        return _aliasName;
    }

    /**
     * Get nest level.
     * @return Nest level.
     */
    public int getNestLevel() {
        return _nestLevel;
    }

    /**
     * Get next nest level.
     * @return Next nest level.
     */
    public int getNextNestLevel() {
        return _nestLevel+1;
    }

    /**
     * Is base query?
     * @param query Condition query. (NotNull)
     * @return Determination.
     */
    public boolean isBaseQuery(ConditionQuery query) {
        return (query.getReferrerQuery() == null);
    }

    /**
     * Get the level of subQuery.
     * @return The level of subQuery.
     */
    public int getSubQueryLevel() {
        return _subQueryLevel;
    }
    
    // -----------------------------------------------------
    //                                             Real Name
    //                                             ---------
    /**
     * Get real alias name(that has nest level mark).
     * @return Real alias name.
     */
    public String getRealAliasName() {
        return getAliasName();
    }

    /**
     * Get real column name(with real alias name).
     * @param columnName Column name without alias name. This should not contain comma. (NotNull)
     * @return Real column name.
     */
    public String getRealColumnName(String columnName) {
        assertColumnName(columnName);
        return buildRealColumnName(getRealAliasName(), columnName);
    }

    /**
     * Build real column name.
     * @param aliasName Alias name. (NotNull)
     * @param columnName Column name. (NotNull)
     * @return Real column name. (NotNull)
     */
    protected String buildRealColumnName(String aliasName, String columnName) {
        return aliasName + "." + columnName;
    }

    // -----------------------------------------------------
    //                                          Foreign Info
    //                                          ------------
    public String getForeignPropertyName() {
        return _foreignPropertyName;
    }

    public void xsetForeignPropertyName(String foreignPropertyName) {
        this._foreignPropertyName = foreignPropertyName;
    }

    public String getRelationPath() {
        return _relationPath;
    }

    public void xsetRelationPath(String relationPath) {
        this._relationPath = relationPath;
    }

    // -----------------------------------------------------
    //                                                Inline
    //                                                ------
    public void xsetOnClauseInline(boolean onClauseInline) {
        _onClauseInline = onClauseInline;
    }
    
    // ===================================================================================
    //                                                                            Location
    //                                                                            ========
    /**
     * Get location.
     * @param columnPropertyName Column property name.
     * @param key Condition key.
     * @return Next nest level.
     */
    protected String getLocation(String columnPropertyName, ConditionKey key) {
        return getLocationBase(columnPropertyName) + "." + key.getConditionKey();
    }

    protected String getLocationBase() {
        final StringBuffer sb = new StringBuffer();
        ConditionQuery query = this;
        while (true) {
            if (query.isBaseQuery(query)) {
                sb.insert(0, CQ_PROPERTY + ".");
                break;
            } else {
                final String foreignPropertyName = query.getForeignPropertyName();
                if (foreignPropertyName == null) {
                    String msg = "The foreignPropertyName of the query should not be null:";
                    msg = msg + " query=" + query;
                    throw new IllegalStateException(msg);
                }
                sb.insert(0, CQ_PROPERTY + initCap(foreignPropertyName) + ".");
            }
            query = query.getReferrerQuery();
        }
        return sb.toString();
    }

    protected String getLocationBase(String columnPropertyName) {
        return getLocationBase() + columnPropertyName;
    }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    /** The map of union query. */
    protected Map<String, ConditionQuery> _unionQueryMap;

    /**
     * Get the map of union query.
     * @return The map of union query. (NotNull)
     */
    public Map<String, ConditionQuery> getUnionQueryMap() {// for Internal
        if (_unionQueryMap == null) {
            _unionQueryMap = new LinkedHashMap<String, ConditionQuery>();
        }
        return _unionQueryMap;
    }

    /**
     * Set union query. {Internal}
     * @param unionQuery Union query. (NotNull)
     */
    public void xsetUnionQuery(ConditionQuery unionQuery) {
        xsetupUnion(unionQuery, false, getUnionQueryMap());
    }

    /** The map of union all query. */
    protected Map<String, ConditionQuery> _unionAllQueryMap;

    /**
     * Get the map of union all query.
     * @return The map of union all query. (NotNull)
     */
    public Map<String, ConditionQuery> getUnionAllQueryMap() {// for Internal
        if (_unionAllQueryMap == null) {
            _unionAllQueryMap = new LinkedHashMap<String, ConditionQuery>();
        }
        return _unionAllQueryMap;
    }

    /**
     * Set union all query. {Internal}
     * @param unionAllQuery Union all query. (NotNull)
     */
    public void xsetUnionAllQuery(ConditionQuery unionAllQuery) {
        xsetupUnion(unionAllQuery, true, getUnionAllQueryMap());
    }

    protected void xsetupUnion(ConditionQuery unionQuery, boolean unionAll, Map<String, ConditionQuery> unionQueryMap) {
        if (unionQuery == null) {
            String msg = "The argument[unionQuery] should not be null.";
            throw new IllegalArgumentException(msg);
        }
        reflectRelationOnUnionQuery(this, unionQuery); // Reflect Relation!
        String key = (unionAll ? "unionAllQuery" : "unionQuery") + unionQueryMap.size();
        unionQueryMap.put(key, unionQuery);
        registerUnionQuery(unionQuery, unionAll, (unionAll ? "unionAllQueryMap" : "unionQueryMap") + "." + key);
    }

    /**
     * Reflect relation on union query.
     * @param baseQueryAsSuper Base query as super. (NotNull)
     * @param unionQueryAsSuper Union query as super. (NotNull)
     */
    protected abstract void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper, ConditionQuery unionQueryAsSuper);

    /**
     * Has union query or union all query?
     * @return Determination.
     */
    public boolean hasUnionQueryOrUnionAllQuery() {
        return (_unionQueryMap != null && !_unionQueryMap.isEmpty()) || (_unionAllQueryMap != null && !_unionAllQueryMap.isEmpty());
    }

    /**
     * Get the list of union query.
     * @return The list of union query. (NotNull)
     */
    public List<ConditionQuery> getUnionQueryList() {
        if (_unionQueryMap == null) { return new ArrayList<ConditionQuery>(); }
        return new ArrayList<ConditionQuery>(_unionQueryMap.values());
    }

    /**
     * Get the list of union all query.
     * @return The list of union all query. (NotNull)
     */
    public List<ConditionQuery> getUnionAllQueryList() {
        if (_unionAllQueryMap == null) { return new ArrayList<ConditionQuery>(); }
        return new ArrayList<ConditionQuery>(_unionAllQueryMap.values());
    }

    // ===================================================================================
    //                                                                            Register
    //                                                                            ========
    // -----------------------------------------------------
    //                                                 Query
    //                                                 -----
    protected void regQ(ConditionKey key, Object value, ConditionValue cvalue, String colName) {
        if (key.isValidRegistration(cvalue, value, key.getConditionKey() + " of " + getRealAliasName() + "." + colName)) {
            setupConditionValueAndRegisterWhereClause(key, value, cvalue, colName);
        }
    }
    
    protected void regQ(ConditionKey key, Object value, ConditionValue cvalue, String colName, ConditionOption option) {
        if (key.isValidRegistration(cvalue, value, key.getConditionKey() + " of " + getRealAliasName() + "." + colName)) {
            setupConditionValueAndRegisterWhereClause(key, value, cvalue, colName, option);
        }
    }

    // -----------------------------------------------------
    //                                         InScope Query
    //                                         -------------
    protected void regINS(ConditionKey key, List<?> value, ConditionValue cvalue, String colName) {
        final int inScopeLimit = getSqlClause().getInScopeLimit();
        if (key.isValidRegistration(cvalue, value, key.getConditionKey() + " of " + getRealAliasName() + "." + colName)) {
            if (inScopeLimit > 0 && value.size() > inScopeLimit) {
                // If the key is for inScope, it should be split as 'or'.
                // (If the key is for notInScope, it should be split as 'and'.)
                final boolean asOr = isConditionKeyInScope(key);
                
                // Split the condition!
                @SuppressWarnings("unchecked")
                final List<Object> objectList = (List<Object>)value;
                final List<List<Object>> valueList = DfCollectionUtil.splitByLimit(objectList, inScopeLimit);
                for (int i = 0; i < valueList.size(); i++) {
                    final List<Object> currentValue = valueList.get(i);
                    if (i == 0) {
                        setupConditionValueAndRegisterWhereClause(key, currentValue, cvalue, colName);
                    } else {
                        if (asOr) { // As 'or' Condition
                            getSqlClause().makeOrQueryEffective();
                        }
                        invokeQuery(colName, key.getConditionKey(), currentValue);
                    }
                }
                if (asOr) {
                    getSqlClause().ignoreOrQuery();
                }
            } else {
                setupConditionValueAndRegisterWhereClause(key, value, cvalue, colName);
            }
        }
    }

    static boolean isConditionKeyInScope(ConditionKey key) { // default scope for test 
        return ConditionKeyInScope.class.isAssignableFrom(key.getClass());
    }
    
    // -----------------------------------------------------
    //                                          FromTo Query
    //                                          ------------
    protected void regFTQ(java.util.Date fromDate, java.util.Date toDate, ConditionValue cvalue
                        , String colName, FromToOption option) {
        {
            final java.util.Date filteredFromDate = option.filterFromDate(fromDate);
            final ConditionKey fromKey = option.getFromDateConditionKey();
            if (fromKey.isValidRegistration(cvalue, filteredFromDate, fromKey.getConditionKey() + " of " + getRealAliasName() + "." + colName)) {
                setupConditionValueAndRegisterWhereClause(fromKey, filteredFromDate, cvalue, colName);
            }
        }
        {
            final java.util.Date filteredToDate = option.filterToDate(toDate);
            final ConditionKey toKey = option.getToDateConditionKey();
            if (toKey.isValidRegistration(cvalue, filteredToDate, toKey.getConditionKey() + " of " + getRealAliasName() + "." + colName)) {
                setupConditionValueAndRegisterWhereClause(toKey, filteredToDate, cvalue, colName);
            }
        }
    }

    // -----------------------------------------------------
    //                                      LikeSearch Query
    //                                      ----------------
    protected void regLSQ(ConditionKey key
                        , String value
                        , ConditionValue cvalue
                        , String colName
                        , LikeSearchOption option) {
        registerLikeSearchQuery(key, value, cvalue, colName, option);
    }

    protected void registerLikeSearchQuery(ConditionKey key
                                         , String value
                                         , ConditionValue cvalue
                                         , String colName
                                         , LikeSearchOption option) {
        final String validationMsg = key.getConditionKey() + " of " + getRealAliasName() + "." + colName;
        if (!key.isValidRegistration(cvalue, value, validationMsg)) {
            return;
        }
        if (option == null) {
            throwLikeSearchOptionNotFoundException(colName, value);
            return;// unreachable
        }
        if (xsuppressEscape()) {
            option.notEscape();
        }
        if (value == null || !option.isSplit()) {
            // As Normal Condition.
            setupConditionValueAndRegisterWhereClause(key, value, cvalue, colName, option);
            return;
        }
        // - - - - - - - - -
        // Use splitByXxx().
        // - - - - - - - - -
        final String[] strArray = option.generateSplitValueArray(value);
        if (!option.isAsOrSplit()) {
            // As 'and' Condition
            for (int i = 0; i < strArray.length; i++) {
                final String currentValue = strArray[i];
                setupConditionValueAndRegisterWhereClause(key, currentValue, cvalue, colName, option);
            }
        } else {
            // As 'or' Condition
            final boolean orQueryEffective = getSqlClause().isOrQueryEffective();
            for (int i = 0; i < strArray.length; i++) {
                final String currentValue = strArray[i];
                if (i == 0) {
                    setupConditionValueAndRegisterWhereClause(key, currentValue, cvalue, colName, option);
                } else {
                    getSqlClause().makeOrQueryEffective();
                    invokeQueryLikeSearch(colName, currentValue, option);
                }
            }
            if (!orQueryEffective) {
                getSqlClause().ignoreOrQuery();
            }
        }
    }

    protected boolean xsuppressEscape() { // for override
        return false; // as default
    }

    protected void throwLikeSearchOptionNotFoundException(String colName, String value) {
        DBMeta dbmeta = getDBMetaProvider().provideDBMeta(getTableDbName());
        String capPropName = initCap(dbmeta.findPropertyName(colName));
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The likeSearchOption was Not Found! (Should not be null!)" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your method call:"  + ln();
        final String beanName = getClass().getSimpleName();
        final String methodName = "set" + capPropName + "_LikeSearch('" + value + "', likeSearchOption);";
        msg = msg + "    " + beanName + "." + methodName + ln();
        msg = msg + "* * * * * * * * * */" + ln();
        throw new RequiredOptionNotFoundException(msg);
    }

    protected void invokeQueryLikeSearch(String columnFlexibleName, Object value, LikeSearchOption option) {
        if (value == null) {
            return;
        }
        final DBMeta dbmeta = findDBMeta(getTableDbName());
        final String columnCapPropName = initCap(dbmeta.findPropertyName(columnFlexibleName));
        final String methodName = "set" + columnCapPropName + "_LikeSearch";
        Method method = null;
        try {
            method = this.getClass().getMethod(methodName, new Class[]{value.getClass(), LikeSearchOption.class});
        } catch (NoSuchMethodException e) {
            String msg = "The columnFlexibleName is not existing in this table:";
            msg = msg + " columnFlexibleName=" + columnFlexibleName;
            msg = msg + " tableName=" + getTableDbName() + " methodName=" + methodName;
            throw new RuntimeException(msg, e);
        }
        try {
            method.invoke(this, new Object[]{value, option});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    // -----------------------------------------------------
    //                                          Inline Query
    //                                          ------------
    protected void regIQ(ConditionKey key, Object value, ConditionValue cvalue, String colName) {
        DBMeta dbmeta = getDBMetaProvider().provideDBMetaChecked(getTableDbName());
        String propertyName = dbmeta.findPropertyName(colName);
        String uncapPropName = initUncap(propertyName);
        if (key.isValidRegistration(cvalue, value, key.getConditionKey() + " of " + getRealAliasName() + "." + colName)) {
            key.setupConditionValue(cvalue, value, getLocation(uncapPropName, key));// If Java, it is necessary to use uncapPropName!
            if (isBaseQuery(this)) {
                getSqlClause().registerBaseTableInlineWhereClause(colName, key, cvalue);
            } else {
                getSqlClause().registerOuterJoinInlineWhereClause(getRealAliasName(), colName, key, cvalue, _onClauseInline);
            }
        }
    }

    protected void regIQ(ConditionKey key, Object value, ConditionValue cvalue, String colName, ConditionOption option) {
        DBMeta dbmeta = getDBMetaProvider().provideDBMetaChecked(getTableDbName());
        String propertyName = dbmeta.findPropertyName(colName);
        String uncapPropName = initUncap(propertyName);
        if (key.isValidRegistration(cvalue, value, key.getConditionKey() + " of " + getRealAliasName() + "." + colName)) {
            key.setupConditionValue(cvalue, value, getLocation(uncapPropName, key), option);// If Java, it is necessary to use uncapPropName!
            if (isBaseQuery(this)) {
                getSqlClause().registerBaseTableInlineWhereClause(colName, key, cvalue, option);
            } else {
                getSqlClause().registerOuterJoinInlineWhereClause(getRealAliasName(), colName, key, cvalue, option, _onClauseInline);
            }
        }
    }

    // -----------------------------------------------------
    //                                       InScopeSubQuery
    //                                       ---------------
    // {Modified at DBFlute-0.7.5}
    protected void registerInScopeSubQuery(ConditionQuery subQuery
                                         , String columnName
                                         , String relatedColumnName
                                         , String propertyName) {
        registerInScopeSubQuery(subQuery, columnName, relatedColumnName, propertyName, null);
    }

    protected void registerNotInScopeSubQuery(ConditionQuery subQuery
                                            , String columnName
                                            , String relatedColumnName
                                            , String propertyName) {
        registerInScopeSubQuery(subQuery, columnName, relatedColumnName, propertyName, "not");
    }

    protected void registerInScopeSubQuery(ConditionQuery subQuery
                                         , String columnName
                                         , String relatedColumnName
                                         , String propertyName
                                         , String inScopeOption) {
        assertObjectNotNull("InScopeSubQyery(" + columnName + ")", subQuery);
        inScopeOption = inScopeOption != null ? inScopeOption + " " : "";
        String realColumnName = getInScopeSubQueryRealColumnName(columnName);
        xincrementLocalSubQueryLevelIfNeeds(subQuery);
        String subQueryClause = getInScopeSubQuerySql(subQuery, relatedColumnName, propertyName);
        int subQueryLevel = subQuery.getSubQueryLevel();
        String subQueryIdentity = propertyName + "[" + subQueryLevel + "]";
        String beginMark = getSqlClause().resolveSubQueryBeginMark(subQueryIdentity) + ln();
        String endMark = getSqlClause().resolveSubQueryEndMark(subQueryIdentity);
        String endIndent = "       ";
        String clause = realColumnName + " " + inScopeOption
                      + "in (" + beginMark + subQueryClause + ln() + endIndent + ")" + endMark;
        registerWhereClause(clause);
    }

    protected String getInScopeSubQueryRealColumnName(String columnName) {
        return getRealColumnName(columnName);
    }

    protected String getInScopeSubQuerySql(ConditionQuery subQuery
                                         , String relatedColumnName
                                         , String propertyName) {
        String tableAliasName = getSqlClause().getLocalTableAliasName();
        String selectClause = "select " + tableAliasName+ "." + relatedColumnName;
        String fromWhereClause = buildPlainSubQueryFromWhereClause(subQuery, relatedColumnName, propertyName
                                                                 , selectClause, tableAliasName);
        return selectClause + " " + fromWhereClause;
    }

    // -----------------------------------------------------
    //                                        ExistsSubQuery
    //                                        --------------
    // {Modified at DBFlute-0.7.5}
    protected void registerExistsSubQuery(ConditionQuery subQuery
                                        , String columnName
                                        , String relatedColumnName
                                        , String propertyName) {
        registerExistsSubQuery(subQuery, columnName, relatedColumnName, propertyName, null);
    }

    protected void registerNotExistsSubQuery(ConditionQuery subQuery
                                           , String columnName
                                           , String relatedColumnName
                                           , String propertyName) {
        registerExistsSubQuery(subQuery, columnName, relatedColumnName, propertyName, "not");
    }

    protected void registerExistsSubQuery(ConditionQuery subQuery
                                        , String columnName
                                        , String relatedColumnName
                                        , String propertyName
                                        , String existsOption) {
        assertObjectNotNull("ExistsSubQyery(" + columnName + ")", subQuery);
        existsOption = existsOption != null ? existsOption + " " : "";
        xincrementLocalSubQueryLevelIfNeeds(subQuery);
        
        String subQueryClause;
        if (columnName.contains(",") && relatedColumnName.contains(",")) {
            // Two-or-More Primary Keys
            List<String> columnNameSplit = DfStringUtil.splitList(columnName, ",");
            String[] realColumnNames = new String[columnNameSplit.size()];
            for (int i=0; i < columnNameSplit.size(); i++) {
                realColumnNames[i] = getExistsSubQueryRealColumnName(columnNameSplit.get(i).trim());
            }
            List<String> relatedColumnSplit = DfStringUtil.splitList(relatedColumnName, ",");
            String[] relatedColumnNames = new String[columnNameSplit.size()];
            for (int i=0; i < relatedColumnSplit.size(); i++) {
                relatedColumnNames[i] = relatedColumnSplit.get(i).trim();
            }
            subQueryClause = getExistsSubQuerySql(subQuery, realColumnNames, relatedColumnNames, propertyName);
        } else {
            // Normal
            String realColumnName = getExistsSubQueryRealColumnName(columnName);
            subQueryClause = getExistsSubQuerySql(subQuery, realColumnName, relatedColumnName, propertyName);
        }
        
        int subQueryLevel = subQuery.getSubQueryLevel();
        String subQueryIdentity = propertyName + "[" + subQueryLevel + "]";
        String beginMark = getSqlClause().resolveSubQueryBeginMark(subQueryIdentity) + ln();
        String endMark = getSqlClause().resolveSubQueryEndMark(subQueryIdentity);
        String endIndent = "       ";
        String clause = existsOption + "exists (" + beginMark + subQueryClause + ln() + endIndent + ")" + endMark;
        registerWhereClause(clause);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // *Unsupport ExistsSubQuery as inline because it's so dangerous.
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    protected String getExistsSubQueryRealColumnName(String columnName) {
        return getRealColumnName(columnName);
    }

    protected String getExistsSubQuerySql(ConditionQuery subQuery
                                        , String realColumnName
                                        , String relatedColumnName
                                        , String propertyName) {
        int subQueryLevel = subQuery.getSubQueryLevel();
        String tableAliasName = "dfsublocal_" + subQueryLevel;
        String selectClause = "select " + tableAliasName + "." + relatedColumnName;
        String fromWhereClause = buildCorrelationSubQueryFromWhereClause(subQuery, relatedColumnName, propertyName
                                                                       , selectClause, tableAliasName, realColumnName);
        return selectClause + " " + fromWhereClause;
    }
    
    protected String getExistsSubQuerySql(ConditionQuery subQuery
                                        , String[] realColumnNames
                                        , String[] relatedColumnNames
                                        , String propertyName) {
        int subQueryLevel = subQuery.getSubQueryLevel();
        String tableAliasName = "dfsublocal_" + subQueryLevel;
        
        // Because sub-query may be only allowed to return a single column.
        String selectClause = "select " + tableAliasName + "." + relatedColumnNames[0];
        
        String fromWhereClause = buildCorrelationSubQueryFromWhereClause(subQuery, relatedColumnNames, propertyName
                                                                       , selectClause, tableAliasName, realColumnNames);
        return selectClause + " " + fromWhereClause;
    }

    // [DBFlute-0.7.4]
    // -----------------------------------------------------
    //                              (Specify)DerivedReferrer
    //                              ------------------------
    protected void registerSpecifyDerivedReferrer(String function, ConditionQuery subQuery
                                                , String columnName, String relatedColumnName
                                                , String propertyName, String aliasName) {
        assertObjectNotNull("SpecifyDerivedReferrer(function)", function);
        assertObjectNotNull("SpecifyDerivedReferrer(" + columnName + ")", subQuery);
        String realColumnName = getSpecifyDerivedReferrerRealColumnName(columnName);
        xincrementLocalSubQueryLevelIfNeeds(subQuery);
        String subQueryClause = getSpecifyDerivedReferrerSubQuerySql(function, subQuery, realColumnName
                                                                   , relatedColumnName, propertyName, aliasName);
        int subQueryLevel = subQuery.getSubQueryLevel();
        String subQueryIdentity = propertyName + "[" + subQueryLevel + "]";
        String beginMark = getSqlClause().resolveSubQueryBeginMark(subQueryIdentity) + ln();
        String endMark = getSqlClause().resolveSubQueryEndMark(subQueryIdentity);
        String endIndent = "       ";
        String clause = "(" + beginMark + subQueryClause + ln() + endIndent + ") as " + aliasName + endMark;
        getSqlClause().specifyDeriveSubQuery(aliasName, clause);
    }

    protected String getSpecifyDerivedReferrerRealColumnName(String columnName) {
        return getRealColumnName(columnName);
    }

    protected String getSpecifyDerivedReferrerSubQuerySql(String function, ConditionQuery subQuery
                                                        , String realColumnName, String relatedColumnName
                                                        , String propertyName, String aliasName) {
        int subQueryLevel = subQuery.getSubQueryLevel();
        String tableAliasName = "dfsublocal_" + subQueryLevel;
        String specifiedColumnName = subQuery.getSqlClause().getSpecifiedColumnNameAsOne();
        if (specifiedColumnName == null || specifiedColumnName.trim().length() == 0) {
            throwSpecifyDerivedReferrerInvalidColumnSpecificationException(function, aliasName);
        }
        String deriveColumnName;
        {
            String specifiedColumnRealName = subQuery.getSqlClause().getSpecifiedColumnRealNameAsOne();
            if (!specifiedColumnRealName.startsWith(subQuery.getSqlClause().getLocalTableAliasName())) {
                // The column is on related table.
                deriveColumnName = specifiedColumnRealName;
            } else {
                // The column is on local table.
                deriveColumnName = tableAliasName + "." + specifiedColumnName;
                
                // Assert about column type when local table only.
                assertSpecifyDerivedReferrerColumnType(function, subQuery, deriveColumnName);
            }
        }
        subQuery.getSqlClause().clearSpecifiedSelectColumn(); // specified columns disappear at this timing
        String connect = xbuildFunctionConnector(function);
        if (subQuery.getSqlClause().hasUnionQuery()) {
            String subQueryIdentity = propertyName + "[" + subQueryLevel + ":subquerymain]";
            String beginMark = getSqlClause().resolveSubQueryBeginMark(subQueryIdentity) + ln();
            String endMark = getSqlClause().resolveSubQueryEndMark(subQueryIdentity);
            DBMeta dbmeta = findDBMeta(subQuery.getTableDbName());
            if (!dbmeta.hasPrimaryKey() || dbmeta.hasTwoOrMorePrimaryKeys()) {
                String msg = "The derived-referrer is unavailable when no primary key or two-or-more primary keys:";
                msg = msg + " table=" + subQuery.getTableDbName();
                throw new IllegalConditionBeanOperationException(msg);
            }
            String primaryKeyName = dbmeta.getPrimaryUniqueInfo().getFirstColumn().getColumnDbName();
            String selectClause = "select " + tableAliasName + "." + primaryKeyName 
                                     + ", " + tableAliasName + "." + relatedColumnName
                                     + ", " + deriveColumnName;
            String fromWhereClause = buildPlainSubQueryFromWhereClause(subQuery, relatedColumnName, propertyName
                                                                     , selectClause, tableAliasName);
            String mainSql = selectClause + " " + fromWhereClause;
            String joinCondition = "dfsubquerymain." + relatedColumnName + " = " + realColumnName;
            return "select " + function + connect + "dfsubquerymain." + specifiedColumnName + ")" + ln()
                 + "  from (" + beginMark
                 + mainSql + ln()
                 + "       ) dfsubquerymain" + endMark + ln() + " where " + joinCondition;
        } else {
            String selectClause = "select " + function + connect + deriveColumnName + ")";
            String fromWhereClause = buildCorrelationSubQueryFromWhereClause(subQuery, relatedColumnName, propertyName
                                                                           , selectClause, tableAliasName, realColumnName);
            return selectClause + " " + fromWhereClause;
        }
    }

    protected void throwSpecifyDerivedReferrerInvalidColumnSpecificationException(String function, String aliasName) {
        ConditionBeanContext.throwSpecifyDerivedReferrerInvalidColumnSpecificationException(function, aliasName);
    }

    protected void assertSpecifyDerivedReferrerColumnType(String function, ConditionQuery subQuery, String deriveColumnName) {
        if (deriveColumnName.contains(".")) {
            deriveColumnName = deriveColumnName.substring(deriveColumnName.lastIndexOf(".") + ".".length());
        }
        final DBMeta dbmeta = findDBMeta(subQuery.getTableDbName());
        final Class<?> deriveColumnType = dbmeta.findColumnInfo(deriveColumnName).getPropertyType();
        if ("sum".equalsIgnoreCase(function) || "avg".equalsIgnoreCase(function)) {
            if (!Number.class.isAssignableFrom(deriveColumnType)) {
                throwSpecifyDerivedReferrerUnmatchedColumnTypeException(function, deriveColumnName, deriveColumnType);
            }
        }
    }

    protected void throwSpecifyDerivedReferrerUnmatchedColumnTypeException(String function, String deriveColumnName, Class<?> deriveColumnType) {
        ConditionBeanContext.throwSpecifyDerivedReferrerUnmatchedColumnTypeException(function, deriveColumnName, deriveColumnType);
    }

    // [DBFlute-0.8.8.1]
    // -----------------------------------------------------
    //                                (Query)DerivedReferrer
    //                                ----------------------
    protected void registerQueryDerivedReferrer(String function, ConditionQuery subQuery
                                              , String columnName, String relatedColumnName, String propertyName
                                              , String operand, Object value, String parameterPropertyName) {
        assertObjectNotNull("QueryDerivedReferrer(function)", function);
        assertObjectNotNull("QueryDerivedReferrer(" + columnName + ")", subQuery);
        String realColumnName = getQueryDerivedReferrerRealColumnName(columnName);
        xincrementLocalSubQueryLevelIfNeeds(subQuery);
        String subQueryClause = getQueryDerivedReferrerSubQuerySql(function, subQuery, realColumnName
                                                                 , relatedColumnName, propertyName, value);
        int subQueryLevel = subQuery.getSubQueryLevel();
        String subQueryIdentity = propertyName + "[" + subQueryLevel + "]";
        String beginMark = getSqlClause().resolveSubQueryBeginMark(subQueryIdentity) + ln();
        String endMark = getSqlClause().resolveSubQueryEndMark(subQueryIdentity);
        String endIndent = "       ";
        String parameter = "/*pmb." + getLocationBase(parameterPropertyName) + "*/null";
        String clause = "(" + beginMark
                      + subQueryClause + ln() + endIndent
                      + ") " + operand + " " + parameter + " " + endMark;
        registerWhereClause(clause);
    }

    protected String getQueryDerivedReferrerRealColumnName(String columnName) {
        return getRealColumnName(columnName);
    }

    protected String getQueryDerivedReferrerSubQuerySql(String function, ConditionQuery subQuery
                                                      , String realColumnName, String relatedColumnName
                                                      , String propertyName, Object value) {
        int subQueryLevel = subQuery.getSubQueryLevel();
        String tableAliasName = "dfsublocal_" + subQueryLevel;
        String specifiedColumnName = subQuery.getSqlClause().getSpecifiedColumnNameAsOne();
        if (specifiedColumnName == null || specifiedColumnName.trim().length() == 0) {
            throwQueryDerivedReferrerInvalidColumnSpecificationException(function);
        }
        String deriveColumnName;
        {
            String specifiedColumnRealName = subQuery.getSqlClause().getSpecifiedColumnRealNameAsOne();
            if (!specifiedColumnRealName.startsWith(subQuery.getSqlClause().getLocalTableAliasName())) {
                // the column is on related table.
                deriveColumnName = specifiedColumnRealName;
            } else {
                // the column is on local table.
                deriveColumnName = tableAliasName + "." + specifiedColumnName;
                
                // Assert about column type when local table only.
                assertQueryDerivedReferrerColumnType(function, subQuery, deriveColumnName, value);
            }
        }
        subQuery.getSqlClause().clearSpecifiedSelectColumn(); // specified columns disappear at this timing
        String connect = xbuildFunctionConnector(function);
        if (subQuery.getSqlClause().hasUnionQuery()) {
            String subQueryIdentity = propertyName + "[" + subQueryLevel + ":subquerymain]";
            String beginMark = getSqlClause().resolveSubQueryBeginMark(subQueryIdentity) + ln();
            String endMark = getSqlClause().resolveSubQueryEndMark(subQueryIdentity);
            DBMeta dbmeta = findDBMeta(subQuery.getTableDbName());
            if (!dbmeta.hasPrimaryKey() || dbmeta.hasTwoOrMorePrimaryKeys()) {
                String msg = "The derived-referrer is unavailable when no primary key or two-or-more primary keys:";
                msg = msg + " table=" + subQuery.getTableDbName();
                throw new IllegalConditionBeanOperationException(msg);
            }
            String primaryKeyName = dbmeta.getPrimaryUniqueInfo().getFirstColumn().getColumnDbName();
            String selectClause = "select " + tableAliasName + "." + primaryKeyName 
                                     + ", " + tableAliasName + "." + relatedColumnName
                                     + ", " + deriveColumnName;
            String fromWhereClause = buildPlainSubQueryFromWhereClause(subQuery, relatedColumnName, propertyName
                                                                     , selectClause, tableAliasName);
            String mainSql = selectClause + " " + fromWhereClause;
            String joinCondition = "dfsubquerymain." + relatedColumnName + " = " + realColumnName;
            return "select " + function + connect + "dfsubquerymain." + specifiedColumnName + ")" + ln()
                 + "  from (" + beginMark
                 + mainSql + ln()
                 + "       ) dfsubquerymain" + endMark + ln() + " where " + joinCondition;
        } else {
            String selectClause = "select " + function + connect + deriveColumnName + ")";
            String fromWhereClause = buildCorrelationSubQueryFromWhereClause(subQuery, relatedColumnName, propertyName
                                                                           , selectClause, tableAliasName, realColumnName);
            return selectClause + " " + fromWhereClause;
        }
    }

    protected void throwQueryDerivedReferrerInvalidColumnSpecificationException(String function) {
        ConditionBeanContext.throwQueryDerivedReferrerInvalidColumnSpecificationException(function);
    }

    protected void assertQueryDerivedReferrerColumnType(String function, ConditionQuery subQuery, String deriveColumnName, Object value) {
        if (deriveColumnName.contains(".")) {
            deriveColumnName = deriveColumnName.substring(deriveColumnName.lastIndexOf(".") + ".".length());
        }
        DBMeta dbmeta = findDBMeta(subQuery.getTableDbName());
        Class<?> deriveColumnType = dbmeta.findColumnInfo(deriveColumnName).getPropertyType();
        if ("sum".equalsIgnoreCase(function) || "avg".equalsIgnoreCase(function)) {
            if (!Number.class.isAssignableFrom(deriveColumnType)) {
                throwQueryDerivedReferrerUnmatchedColumnTypeException(function, deriveColumnName, deriveColumnType, value);
            }
        }
        if (value != null) {
            Class<?> parameterType = value.getClass();
            if (String.class.isAssignableFrom(deriveColumnType)) {
                if (!String.class.isAssignableFrom(parameterType)) {
                    throwQueryDerivedReferrerUnmatchedColumnTypeException(function, deriveColumnName, deriveColumnType, value);
                }
            }
            if (Number.class.isAssignableFrom(deriveColumnType)) {
                if (!Number.class.isAssignableFrom(parameterType)) {
                    throwQueryDerivedReferrerUnmatchedColumnTypeException(function, deriveColumnName, deriveColumnType, value);
                }
            }
            if (java.util.Date.class.isAssignableFrom(deriveColumnType)) {
                if (!java.util.Date.class.isAssignableFrom(parameterType)) {
                    throwQueryDerivedReferrerUnmatchedColumnTypeException(function, deriveColumnName, deriveColumnType, value);
                }
            }
        }
    }

    protected void throwQueryDerivedReferrerUnmatchedColumnTypeException(String function, String deriveColumnName, Class<?> deriveColumnType, Object value) {
        ConditionBeanContext.throwQueryDerivedReferrerUnmatchedColumnTypeException(function, deriveColumnName, deriveColumnType, value);
    }

    // [DBFlute-0.8.8]
    // -----------------------------------------------------
    //                                        ScalarSubQuery
    //                                        --------------
    protected void registerScalarSubQuery(String function, ConditionQuery subQuery
                                        , String propertyName, String operand) {
        assertObjectNotNull("ScalarSubQuery(" + propertyName + ")", subQuery);
        
        // Get the specified column before it disappears at sub-query making.
        String deriveRealColumnName;
        {
            String deriveColumnName = subQuery.getSqlClause().getSpecifiedColumnNameAsOne();
            if (deriveColumnName == null || deriveColumnName.trim().length() == 0) {
                throwScalarSubQueryInvalidColumnSpecificationException(function);
            }
            deriveRealColumnName = getScalarSubQueryRealColumnName(deriveColumnName);
        }

        xincrementLocalSubQueryLevelIfNeeds(subQuery);
        String subQueryClause = getScalarSubQuerySql(function, subQuery, propertyName);
        int subQueryLevel = subQuery.getSubQueryLevel();
        String subQueryIdentity = propertyName + "[" + subQueryLevel + "]";
        String beginMark = getSqlClause().resolveSubQueryBeginMark(subQueryIdentity) + ln();
        String endMark = getSqlClause().resolveSubQueryEndMark(subQueryIdentity);
        String endIndent = "       ";
        String clause = deriveRealColumnName + " " + operand + " ("
                      + beginMark + subQueryClause + ln() + endIndent
                      + ") " + endMark;
        registerWhereClause(clause);
    }

    protected String getScalarSubQueryRealColumnName(String columnName) {
        return getRealColumnName(columnName);
    }

    protected String getScalarSubQuerySql(String function, ConditionQuery subQuery
                                        , String propertyName) {
        int subQueryLevel = subQuery.getSubQueryLevel();
        String tableAliasName = "dfsublocal_" + subQueryLevel;
        String deriveColumnName = subQuery.getSqlClause().getSpecifiedColumnNameAsOne();
        if (deriveColumnName == null || deriveColumnName.trim().length() == 0) {
            throwScalarSubQueryInvalidColumnSpecificationException(function);
        }
        assertScalarSubQueryColumnType(function, subQuery, deriveColumnName);
        subQuery.getSqlClause().clearSpecifiedSelectColumn(); // specified columns disappear at this timing
        DBMeta dbmeta = findDBMeta(subQuery.getTableDbName());
        if (!dbmeta.hasPrimaryKey() || dbmeta.hasTwoOrMorePrimaryKeys()) {
            String msg = "The scalar-sub-query is unavailable when no primary key or two-or-more primary keys:";
            msg = msg + " table=" + subQuery.getTableDbName();
            throw new IllegalConditionBeanOperationException(msg);
        }
        String primaryKeyName = dbmeta.getPrimaryUniqueInfo().getFirstColumn().getColumnDbName();
        if (subQuery.getSqlClause().hasUnionQuery()) {
            String subQueryIdentity = propertyName + "[" + subQueryLevel + ":subquerymain]";
            String beginMark = getSqlClause().resolveSubQueryBeginMark(subQueryIdentity) + ln();
            String endMark = getSqlClause().resolveSubQueryEndMark(subQueryIdentity);
            String selectClause = "select " + tableAliasName + "." + primaryKeyName
                                     + ", " + tableAliasName + "." + deriveColumnName;
            String fromWhereClause = buildPlainSubQueryFromWhereClause(subQuery, primaryKeyName, propertyName
                                                                     , selectClause, tableAliasName);
            String mainSql = selectClause + " " + fromWhereClause;
            return "select " + function + "(dfsubquerymain." + deriveColumnName + ")" + ln()
                 + "  from (" + beginMark
                 + mainSql + ln()
                 + "       ) dfsubquerymain" + endMark;
        } else {
            String selectClause = "select " + function + "(" + tableAliasName + "." + deriveColumnName + ")";
            String fromWhereClause = buildPlainSubQueryFromWhereClause(subQuery, primaryKeyName, propertyName
                                                                     , selectClause, tableAliasName);
            return selectClause + " " + fromWhereClause;
        }
    }

    protected void throwScalarSubQueryInvalidColumnSpecificationException(String function) {
        ConditionBeanContext.throwScalarSubQueryInvalidColumnSpecificationException(function);
    }

    protected void assertScalarSubQueryColumnType(String function, ConditionQuery subQuery, String deriveColumnName) {
        DBMeta dbmeta = findDBMeta(subQuery.getTableDbName());
        Class<?> deriveColumnType = dbmeta.findColumnInfo(deriveColumnName).getPropertyType();
        if ("sum".equalsIgnoreCase(function) || "avg".equalsIgnoreCase(function)) {
            if (!Number.class.isAssignableFrom(deriveColumnType)) {
                throwScalarSubQueryUnmatchedColumnTypeException(function, deriveColumnName, deriveColumnType);
            }
        }
    }

    protected void throwScalarSubQueryUnmatchedColumnTypeException(String function, String deriveColumnName, Class<?> deriveColumnType) {
        ConditionBeanContext.throwScalarSubQueryUnmatchedColumnTypeException(function, deriveColumnName, deriveColumnType);
    }

    // -----------------------------------------------------
    //                                       SubQuery Common
    //                                       ---------------
    protected String buildPlainSubQueryFromWhereClause(ConditionQuery subQuery
                                                     , String relatedColumnName
                                                     , String propertyName
                                                     , String selectClause
                                                     , String tableAliasName) {
        String fromWhereClause = subQuery.getSqlClause().getClauseFromWhereWithUnionTemplate();

        // Replace the alias names for local table with alias name of sub-query unique.
        // However when it's inScope this replacement is unnecessary so comment out here. 
        // (Override base alias name at sub-query on SQL)
        // So if the argument 'tableAliasName' is not null, replace it. 
        if (tableAliasName != null) {
            fromWhereClause = replaceString(fromWhereClause, "dflocal", tableAliasName);
        }

        // Resolve the location path for the condition-query of sub-query. 
        fromWhereClause = replaceString(fromWhereClause, ".conditionQuery.", "." + getLocationBase(propertyName) + ".");

        // Replace template marks. These are very important!
        fromWhereClause = replaceString(fromWhereClause, getSqlClause().getUnionSelectClauseMark(), selectClause);
        fromWhereClause = replaceString(fromWhereClause, getSqlClause().getUnionWhereClauseMark(), "");
        fromWhereClause = replaceString(fromWhereClause, getSqlClause().getUnionWhereFirstConditionMark(), "");
        return fromWhereClause;
    }

    protected String buildCorrelationSubQueryFromWhereClause(ConditionQuery subQuery
                                                           , String relatedColumnName
                                                           , String propertyName
                                                           , String selectClause
                                                           , String tableAliasName
                                                           , String realColumnName) {
        String clause = xprepareCorrelationSubQueryFromWhereClause(subQuery, propertyName, tableAliasName);
        String joinCondition = tableAliasName + "." + relatedColumnName + " = " + realColumnName;
        clause = xreplaceCorrelationSubQueryFromWhereClause(clause, selectClause, joinCondition);
        return clause;
    }
    
    protected String buildCorrelationSubQueryFromWhereClause(ConditionQuery subQuery
                                                           , String[] relatedColumnNames
                                                           , String propertyName
                                                           , String selectClause
                                                           , String tableAliasName
                                                           , String[] realColumnNames) {
        String clause = xprepareCorrelationSubQueryFromWhereClause(subQuery, propertyName, tableAliasName);
        
        String joinCondition;
        StringBuilder sb = new StringBuilder();
        for (int i=0; i < relatedColumnNames.length; i++) {
            if (sb.length() > 0) {
                sb.append(ln()).append("   and ");
            }
            sb.append(tableAliasName).append(".").append(relatedColumnNames[i]);
            sb.append(" = ").append(realColumnNames[i]);
        }
        joinCondition = sb.toString();
        
        clause = xreplaceCorrelationSubQueryFromWhereClause(clause, selectClause, joinCondition);
        return clause;
    }
    
    protected String xprepareCorrelationSubQueryFromWhereClause(ConditionQuery subQuery
                                                              , String propertyName
                                                              , String tableAliasName) {
        String clause = subQuery.getSqlClause().getClauseFromWhereWithWhereUnionTemplate();
        
        // Replace the alias names for local table with alias name of sub-query unique. 
        clause = replaceString(clause, "dflocal", tableAliasName);
        
        // Resolve the location path for the condition-query of sub-query. 
        clause = replaceString(clause, ".conditionQuery.", "." + getLocationBase(propertyName) + ".");
        
        return clause;
    }
    
    protected String xreplaceCorrelationSubQueryFromWhereClause(String clause, String selectClause, String joinCondition) {
        // Replace template marks. These are very important!
        String firstConditionAfter = ln() + "   and ";
        SqlClause sc = getSqlClause();
        clause = replaceString(clause, sc.getWhereClauseMark(), "where " + joinCondition);
        clause = replaceString(clause, sc.getWhereFirstConditionMark(), joinCondition + firstConditionAfter);
        clause = replaceString(clause, sc.getUnionSelectClauseMark(), selectClause);
        clause = replaceString(clause, sc.getUnionWhereClauseMark(), "where " + joinCondition);
        clause = replaceString(clause, sc.getUnionWhereFirstConditionMark(), joinCondition + firstConditionAfter);
        return clause;
    }

    protected void xincrementLocalSubQueryLevelIfNeeds(ConditionQuery subQuery) { // Very Internal
        int subQueryLevel = subQuery.getSubQueryLevel();
        if (_subQueryLevel <= subQueryLevel) {
            _subQueryLevel = subQueryLevel + 1;
        }
    }

    protected String xbuildFunctionConnector(String function) {
        if (function != null && function.endsWith("(distinct")) { // For example 'count(distinct'
            return " ";
        } else {
            return "(";
        }
    }

    // -----------------------------------------------------
    //                                          Where Clause
    //                                          ------------
    protected void setupConditionValueAndRegisterWhereClause(ConditionKey key, Object value, ConditionValue cvalue, String colName) {
        DBMeta dbmeta = getDBMetaProvider().provideDBMetaChecked(getTableDbName());
        String propertyName = dbmeta.findPropertyName(colName);
        String uncapPropName = initUncap(propertyName);
        key.setupConditionValue(cvalue, value, getLocation(uncapPropName, key));// If Java, it is necessary to use uncapPropName!
        getSqlClause().registerWhereClause(getRealColumnName(colName), key, cvalue);
    }

    protected void setupConditionValueAndRegisterWhereClause(ConditionKey key, Object value, ConditionValue cvalue
                                                           , String colName, ConditionOption option) {
        DBMeta dbmeta = getDBMetaProvider().provideDBMetaChecked(getTableDbName());
        String propertyName = dbmeta.findPropertyName(colName);
        String uncapPropName = initUncap(propertyName);
        key.setupConditionValue(cvalue, value, getLocation(uncapPropName, key), option);// If Java, it is necessary to use uncapPropName!
        getSqlClause().registerWhereClause(getRealColumnName(colName), key, cvalue, option);
    }

    protected void registerWhereClause(String whereClause) {
        getSqlClause().registerWhereClause(whereClause);
    }

    protected void registerInlineWhereClause(String whereClause) {
        if (isBaseQuery(this)) {
            getSqlClause().registerBaseTableInlineWhereClause(whereClause);
        } else {
            getSqlClause().registerOuterJoinInlineWhereClause(getRealAliasName(), whereClause, _onClauseInline);
        }
    }

    // -----------------------------------------------------
    //                                           Union Query
    //                                           -----------
    public void registerUnionQuery(ConditionQuery unionQuery, boolean unionAll, String unionQueryPropertyName) {
        final String unionQueryClause = getUnionQuerySql(unionQuery, unionQueryPropertyName);
        
        // At the future, building SQL will be moved to sqlClause.
        getSqlClause().registerUnionQuery(unionQueryClause, unionAll);
    }

    protected String getUnionQuerySql(ConditionQuery unionQuery, String unionQueryPropertyName) {
        final String fromClause = unionQuery.getSqlClause().getFromClause();
        final String whereClause = unionQuery.getSqlClause().getWhereClause();
        final String unionQueryClause;
        if (whereClause.trim().length() <= 0) {
            unionQueryClause = fromClause + " " + getSqlClause().getUnionWhereClauseMark();
        } else {
            final int whereIndex = whereClause.indexOf("where ");
            if (whereIndex < 0) {
                String msg = "The whereClause should have 'where' string: " + whereClause;
                throw new IllegalStateException(msg);
            }
            final int clauseIndex = whereIndex + "where ".length();
            final String mark = getSqlClause().getUnionWhereFirstConditionMark();
            unionQueryClause = fromClause + " " + whereClause.substring(0, clauseIndex) + mark + whereClause.substring(clauseIndex);
        }
        final String oldStr = ".conditionQuery.";
        final String newStr = ".conditionQuery." + unionQueryPropertyName + ".";
        return replaceString(unionQueryClause, oldStr, newStr);// Very Important!
    }

    // -----------------------------------------------------
    //                                            Inner Join
    //                                            ----------
    /**
     * Change the join type for this relation to inner join. <br />
     * This method is for PERFORMANCE TUNING basically.
     */
    public void innerJoin() {
        if (isBaseQuery(this)) {
            String msg = "Look! Read the message below." + ln();
            msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
            msg = msg + "The method 'innerJoin()' should be called for a relation query!" + ln();
            msg = msg + ln();
            msg = msg + "[Advice]" + ln();
            msg = msg + "Please confirm your program. " + ln();
            msg = msg + "  For example:" + ln();
            msg = msg + "    (x) - cb.query().innerJoin();" + ln();
            msg = msg + "    (o) - cb.query().queryMemberStatusCode().innerJoin();" + ln();
            msg = msg + "* * * * * * * * * */";
            throw new IllegalStateException(msg);
        }
        getSqlClause().changeToInnerJoin(getRealAliasName());
    }
    
    // -----------------------------------------------------
    //                                               OrderBy
    //                                               -------
    /**
     * Order with the keyword 'nulls first'.
     */
    public void withNullsFirst() { // is User Public!
        getSqlClause().addNullsFirstToPreviousOrderBy();
    }
    
    /**
     * Order with the keyword 'nulls last'.
     */
    public void withNullsLast() { // is User Public!
        getSqlClause().addNullsLastToPreviousOrderBy();
    }
    
    /**
     * Order with the list of manual value. <br />
     * This with Union is unsupported!
     * @param manualValueList The list of manual value. (NotNull)
     */
    public void withManualOrder(List<? extends Object> manualValueList) { // is User Public!
        assertObjectNotNull("withManualOrder(manualValueList)", manualValueList);
        ManumalOrderInfo manumalOrderInfo = new ManumalOrderInfo();
        manumalOrderInfo.setManualValueList(manualValueList);
        getSqlClause().addManualOrderToPreviousOrderByElement(manumalOrderInfo);
    }

    protected void registerSpecifiedDerivedOrderBy_Asc(String aliasName) {
        if (!getSqlClause().hasSpecifiedDeriveSubQuery(aliasName)) {
            throwSpecifiedDerivedOrderByAliasNameNotFoundException(aliasName);
        }
        getSqlClause().registerOrderBy(aliasName, null, true);
    }

    protected void registerSpecifiedDerivedOrderBy_Desc(String aliasName) {
        if (!getSqlClause().hasSpecifiedDeriveSubQuery(aliasName)) {
            throwSpecifiedDerivedOrderByAliasNameNotFoundException(aliasName);
        }
        getSqlClause().registerOrderBy(aliasName, null, false);
    }

    protected void throwSpecifiedDerivedOrderByAliasNameNotFoundException(String aliasName) {
        ConditionBeanContext.throwSpecifiedDerivedOrderByAliasNameNotFoundException(aliasName);
    }

    protected void registerOrderBy(String columnName, boolean ascOrDesc) {
        getSqlClause().registerOrderBy(getRealColumnName(columnName), null, ascOrDesc);
    }

    protected void regOBA(String columnName) {
        registerOrderBy(columnName, true);
    }

    protected void regOBD(String columnName) {
        registerOrderBy(columnName, false);
    }
    
    // ===================================================================================
    //                                                                       Name Resolver
    //                                                                       =============
    /**
     * Resolve join alias name.
     * @param relationPath Relation path. (NotNull)
     * @param nestLevel Nest level.
     * @return Resolved join alias name. (NotNull)
     */
    protected String resolveJoinAliasName(String relationPath, int nestLevel) {
        return getSqlClause().resolveJoinAliasName(relationPath, nestLevel);
    }

    protected String resolveNestLevelExpression(String name) {
        return getSqlClause().resolveNestLevelExpression(name, getNestLevel());
    }

    protected String resolveNextRelationPath(String tableName, String relationPropertyName) {
        final int relationNo = getSqlClause().resolveRelationNo(tableName, relationPropertyName);
        String nextRelationPath = "_" + relationNo;
        if (_relationPath != null) {
            nextRelationPath = _relationPath + nextRelationPath;
        }
        return nextRelationPath;
    }
    
    // ===================================================================================
    //                                                                     Fixed Condition
    //                                                                     ===============
    protected String ppFxCd(String fixedCondition, String localAliasName, String foreignAliasName) { // prepareFixedCondition
        fixedCondition = replaceString(fixedCondition, "$$alias$$", foreignAliasName);
        fixedCondition = replaceString(fixedCondition, "$$foreignAlias$$", foreignAliasName);
        fixedCondition = replaceString(fixedCondition, "$$localAlias$$", localAliasName);
        fixedCondition = replaceString(fixedCondition, "$$locationBase$$.", "pmb." + getLocationBase());
        return fixedCondition;
    }
    
    // ===================================================================================
    //                                                                 Reflection Invoking
    //                                                                 ===================
    /**
     * {@inheritDoc}
     */
    public ConditionValue invokeValue(String columnFlexibleName) {
        assertStringNotNullAndNotTrimmedEmpty("columnFlexibleName", columnFlexibleName);
        final DBMeta dbmeta = findDBMeta(getTableDbName());
        final String columnCapPropName = initCap(dbmeta.findPropertyName(columnFlexibleName));
        final String methodName = "get" + columnCapPropName;
        final Method method = helpGettingCQMethod(this, methodName, new Class<?>[]{}, columnFlexibleName);
        return (ConditionValue)helpInvokingCQMethod(this, method, new Object[]{});
    }

    /**
     * {@inheritDoc}
     */
    public void invokeQuery(String columnFlexibleName, String conditionKeyName, Object value) {
        assertStringNotNullAndNotTrimmedEmpty("columnFlexibleName", columnFlexibleName);
        assertStringNotNullAndNotTrimmedEmpty("conditionKeyName", conditionKeyName);
        if (value == null) {
            return;
        }
        final PropertyNameCQContainer container = helpExtractingPropertyNameCQContainer(columnFlexibleName);
        final String propertyName = container.getPropertyName();
        final ConditionQuery cq = container.getConditionQuery();
        final DBMeta dbmeta = findDBMeta(cq.getTableDbName());
        final String columnCapPropName = initCap(dbmeta.findPropertyName(propertyName));
        final String methodName = "set" + columnCapPropName + "_" + initCap(conditionKeyName);
        final Method method = helpGettingCQMethod(cq, methodName, new Class<?>[]{value.getClass()}, propertyName);
        helpInvokingCQMethod(cq, method, new Object[]{value});
    }

    /**
     * {@inheritDoc}
     */
    public void invokeOrderBy(String columnFlexibleName, boolean isAsc) {
        assertStringNotNullAndNotTrimmedEmpty("columnFlexibleName", columnFlexibleName);
        final PropertyNameCQContainer container = helpExtractingPropertyNameCQContainer(columnFlexibleName);
        final String propertyName = container.getPropertyName();
        final ConditionQuery cq = container.getConditionQuery();
        final String ascDesc = isAsc ? "Asc" : "Desc";
        final DBMeta dbmeta = findDBMeta(cq.getTableDbName());
        final String columnCapPropName = initCap(dbmeta.findPropertyName(propertyName));
        final String methodName = "addOrderBy_" + columnCapPropName + "_" + ascDesc;
        final Method method = helpGettingCQMethod(cq, methodName, new Class<?>[]{}, propertyName);
        helpInvokingCQMethod(cq, method, new Object[]{});
    }

    /**
     * {@inheritDoc}
     */
    public ConditionQuery invokeForeignCQ(String foreignPropertyName) {
        assertStringNotNullAndNotTrimmedEmpty("foreignPropertyName", foreignPropertyName);
        final String methodName = "query" + initCap(foreignPropertyName);
        final Method method = helpGettingCQMethod(this, methodName, new Class<?>[]{}, foreignPropertyName);
        return (ConditionQuery)helpInvokingCQMethod(this, method, new Object[]{});
    }

    private PropertyNameCQContainer helpExtractingPropertyNameCQContainer(String name) {
        final String[] strings = name.split("\\.");
        final int length = strings.length;
        String propertyName = null;
        ConditionQuery cq = this;
        int index = 0;
        for (String element : strings) {
            if (length == (index+1)) {// at last loop!
                propertyName = element;
                break;
            }
            cq = cq.invokeForeignCQ(element);
            ++index;
        }
        return new PropertyNameCQContainer(propertyName, cq);
    }

    private static class PropertyNameCQContainer {
        protected String _propertyName;
        protected ConditionQuery _cq;
        public PropertyNameCQContainer(String propertyName, ConditionQuery cq) {
            this._propertyName = propertyName;
            this._cq = cq;
        }
        public String getPropertyName() {
            return _propertyName;
        }
        public ConditionQuery getConditionQuery() {
            return _cq;
        }
    }

    private Method helpGettingCQMethod(ConditionQuery cq, String methodName, Class<?>[] argTypes, String property) {
        try {
            return cq.getClass().getMethod(methodName, argTypes);
        } catch (NoSuchMethodException e) {
            if (argTypes != null && argTypes.length == 1) {
                Class<?> argType = argTypes[0];
                if (List.class.isAssignableFrom(argType)) {
                    try {
                        return cq.getClass().getMethod(methodName, new Class<?>[]{Collection.class});
                    } catch (NoSuchMethodException ignored) {
                    }
                }
                Class<?>[] infs = argType.getInterfaces();
                for (Class<?> inf : infs) {
                    try {
                        return cq.getClass().getMethod(methodName, new Class<?>[]{inf});
                    } catch (NoSuchMethodException ignored) {
                    }
                }
            }
            String msg = "The method is not existing:";
            msg = msg + " methodName=" + methodName;
            msg = msg + " argTypes=" + convertObjectArrayToStringView(argTypes);
            msg = msg + " tableName=" + cq.getTableDbName();
            msg = msg + " property=" + property;
            throw new IllegalStateException(msg, e);
        }
    }

    private Object helpInvokingCQMethod(ConditionQuery cq, Method method, Object[] args) {
        try {
            return method.invoke(cq, args);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected ConditionValue nCV() {
        ConditionValue conditionValue = new ConditionValue();
        if (getSqlClause() instanceof SqlClauseMySql) { // Is it MySQL?
            // MySQL does not automatically resolve java.util.Date time parts problem in its JDBC.
            // So java.util.Date should be treated as java.sql.Date in condition-bean.
            conditionValue.enableUtilDateToSqlDate();
        }
        return conditionValue;
    }
    
    /**
     * @param value Query-value-string. (Nullable)
     * @return Filtered value. (Nullable)
     */
    protected String fRES(String value) {
        return filterRemoveEmptyString(value);
    }

    /**
     * @param value Query-value-string. (Nullable)
     * @return Filtered value. (Nullable)
     */
    private String filterRemoveEmptyString(String value) {
        return ((value != null && !"".equals(value)) ? value : null);
    }
    
    /**
     * create the option of like search as prefix search.
     * @return The option of like search as prefix search. (NotNull)
     */
    protected LikeSearchOption cLSOP() {
        return new LikeSearchOption().likePrefix();
    }
    
    /**
     * @param col Target collection. (Nullable)
     * @param <PROPERTY_TYPE> The type of property.
     * @return List. (Nullable: If the argument is null, returns null.)
     */
    protected <PROPERTY_TYPE> List<PROPERTY_TYPE> cTL(Collection<PROPERTY_TYPE> col) {
        return convertToList(col);
    }
    
    /**
     * @param col Target collection. (Nullable)
     * @param <PROPERTY_TYPE> The type of property.
     * @return List. (Nullable: If the argument is null, returns null.)
     */
    private <PROPERTY_TYPE> List<PROPERTY_TYPE> convertToList(Collection<PROPERTY_TYPE> col) {
        if (col == null) {
            return null;
        }
        if (col instanceof List<?>) {
            return filterRemoveNullOrEmptyValueFromList((List<PROPERTY_TYPE>)col);
        }
        return filterRemoveNullOrEmptyValueFromList(new ArrayList<PROPERTY_TYPE>(col));
    }

    private <PROPERTY_TYPE> List<PROPERTY_TYPE> filterRemoveNullOrEmptyValueFromList(List<PROPERTY_TYPE> ls) {
        if (ls == null) {
            return null;
        }
        List<PROPERTY_TYPE> newList = new ArrayList<PROPERTY_TYPE>();
        for (Iterator<PROPERTY_TYPE> ite = ls.iterator(); ite.hasNext(); ) {
            final PROPERTY_TYPE element = ite.next();
            if (element == null) {
                continue;
            }
            if (element instanceof String) {
                if (((String)element).length() == 0) {
                    continue;
                }
            }
            newList.add(element);
        }
        return newList;
    }
    
    public void doNss(NssCall callback) { // Very Internal
        String foreignPropertyName = callback.qf().getForeignPropertyName();
        String foreignTableAliasName = callback.qf().getRealAliasName();
        getSqlClause().registerSelectedSelectColumn(foreignTableAliasName, getTableDbName(), foreignPropertyName, getRelationPath());
        getSqlClause().registerSelectedForeignInfo(callback.qf().getRelationPath(), foreignPropertyName);
    }
    
    public static interface NssCall { // Very Internal
        public ConditionQuery qf();
    }

    protected void registerOuterJoin(ConditionQuery cq, Map<String, String> joinOnMap) {
        getSqlClause().registerOuterJoin(cq.getTableSqlName(), cq.getRealAliasName(), joinOnMap);
    }
    
    protected String fxcKey() { // getFixedConditionKey()
        return getSqlClause().getFixedConditionKey();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected final String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }

    protected String initCap(String str) {
        return DfStringUtil.initCap(str);
    }
    
    protected String initUncap(String str) {
        return DfStringUtil.initUncap(str);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    protected String convertObjectArrayToStringView(Object[] objArray) {
        return DfTraceViewUtil.convertObjectArrayToStringView(objArray);
    }

    // -----------------------------------------------------
    //                                  Collection Generator
    //                                  --------------------
    protected <KEY, VALUE> LinkedHashMap<KEY, VALUE> newLinkedHashMap() {
        return new LinkedHashMap<KEY, VALUE>();
    }
    
    protected <ELEMENT> ArrayList<ELEMENT> newArrayList() {
        return new ArrayList<ELEMENT>();
    }

    protected <ELEMENT> ArrayList<ELEMENT> newArrayList(ELEMENT element) {
        ArrayList<ELEMENT> arrayList = new ArrayList<ELEMENT>();
        arrayList.add(element);
        return arrayList;
    }

    protected <ELEMENT> ArrayList<ELEMENT> newArrayList(Collection<ELEMENT> collection) {
        return new ArrayList<ELEMENT>(collection);
    }

    // -----------------------------------------------------
    //                                         Assert Object
    //                                         -------------
    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert that the column-name is not null and is not empty and does not contain comma.
     * @param columnName Column-name. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertColumnName(String columnName) {
        if (columnName == null) {
            String msg = "The columnName should not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (columnName.trim().length() == 0) {
            String msg = "The columnName should not be empty-string.";
            throw new IllegalArgumentException(msg);
        }
        if (columnName.indexOf(",") >= 0) {
            String msg = "The columnName should not contain comma ',': " + columnName;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert that the alias-name is not null and is not empty and does not contain comma.
     * @param aliasName Alias-name. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertAliasName(String aliasName) {
        if (aliasName == null) {
            String msg = "The aliasName should not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (aliasName.trim().length() == 0) {
            String msg = "The aliasName should not be empty-string.";
            throw new IllegalArgumentException(msg);
        }
        if (aliasName.indexOf(",") >= 0) {
            String msg = "The aliasName should not contain comma ',': " + aliasName;
            throw new IllegalArgumentException(msg);
        }
    }

    // -----------------------------------------------------
    //                                         Assert String
    //                                         -------------
    /**
     * Assert that the entity is not null and not trimmed empty.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     */
    protected void assertStringNotNullAndNotTrimmedEmpty(String variableName, String value) {
        assertObjectNotNull("variableName", variableName);
        assertObjectNotNull("value", value);
        if (value.trim().length() ==0) {
            String msg = "The value should not be empty: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
    }
    
    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return getClass().getSimpleName() + ":{aliasName=" + _aliasName + ", nestLevel=" + _nestLevel
             + ", subQueryLevel=" + _subQueryLevel + ", foreignPropertyName=" + _foreignPropertyName
             + ", relationPath=" + _relationPath + ", onClauseInline=" + _onClauseInline + "}";
    }
}
