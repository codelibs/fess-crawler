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
package org.seasar.robot.dbflute.dbmeta;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.robot.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.robot.dbflute.dbmeta.info.RelationInfo;
import org.seasar.robot.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.robot.dbflute.exception.IllegalClassificationCodeException;
import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.helper.StringKeyMap;
import org.seasar.robot.dbflute.jdbc.Classification;
import org.seasar.robot.dbflute.jdbc.ClassificationMeta;
import org.seasar.robot.dbflute.util.DfAssertUtil;
import org.seasar.robot.dbflute.util.DfCollectionUtil;
import org.seasar.robot.dbflute.util.DfReflectionUtil;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.DfTypeUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * The abstract class of DB meta.
 * @author jflute
 */
public abstract class AbstractDBMeta implements DBMeta {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** The dummy value for internal map value. */
    protected static final Object DUMMY_VALUE = new Object();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                  Information Resource
    //                                  --------------------
    // Initialized at its getter.
    private volatile StringKeyMap<String> _tableDbNameFlexibleMap;
    private volatile StringKeyMap<String> _tablePropertyNameFlexibleMap;
    private volatile List<ColumnInfo> _columnInfoList;
    private volatile StringKeyMap<ColumnInfo> _columnInfoFlexibleMap;
    private volatile List<ForeignInfo> _foreignInfoList;
    private volatile StringKeyMap<ForeignInfo> _foreignInfoFlexibleMap;
    private volatile List<ReferrerInfo> _referrerInfoList;
    private volatile StringKeyMap<ReferrerInfo> _referrerInfoFlexibleMap;

    // Initialized at hasMethod().
    private final Map<String, Object> _methodNameMap = newConcurrentHashMap();

    // ===================================================================================
    //                                                             Resource Initialization
    //                                                             =======================
    protected void initializeInformationResource() { // for instance initializer of subclass.
        // Initialize the flexible map of table DB name.
        getTableDbNameFlexibleMap();

        // Initialize the flexible map of table property name.
        getTablePropertyNameFlexibleMap();

        // Initialize the list of column information.
        getColumnInfoList();

        // Initialize the flexible map of column information. 
        getColumnInfoFlexibleMap();

        // These should not be initialized here!
        // because the problem 'cyclic reference' occurred! 
        // So these are initialized as lazy.
        //getForeignInfoList();
        //getForeignInfoFlexibleMap();
        //getReferrerInfoList();
        //getReferrerInfoFlexibleMap();

        // Initialize the map of (public)method name. 
        hasMethod("dummy");
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    // These methods is expected to override if it needs.
    public String getTableAlias() {
        return null;
    }

    public String getTableComment() {
        return null;
    }

    // -----------------------------------------------------
    //                                          Flexible Map
    //                                          ------------
    /**
     * Get the flexible map of table DB name.
     * @return The flexible map of table DB name. (NotNull, NotEmpty)
     */
    protected Map<String, String> getTableDbNameFlexibleMap() {
        if (_tableDbNameFlexibleMap != null) {
            return _tableDbNameFlexibleMap;
        }
        synchronized (this) {
            if (_tableDbNameFlexibleMap != null) {
                return _tableDbNameFlexibleMap;
            }
            _tableDbNameFlexibleMap = StringKeyMap.createAsFlexibleConcurrent();
            _tableDbNameFlexibleMap.put(getTableDbName(), getTableDbName());
            return _tableDbNameFlexibleMap;
        }
    }

    /**
     * Get the flexible map of table property name.
     * @return The flexible map of table property name. (NotNull, NotEmpty)
     */
    protected Map<String, String> getTablePropertyNameFlexibleMap() {
        if (_tablePropertyNameFlexibleMap != null) {
            return _tablePropertyNameFlexibleMap;
        }
        synchronized (this) {
            if (_tablePropertyNameFlexibleMap != null) {
                return _tablePropertyNameFlexibleMap;
            }
            _tablePropertyNameFlexibleMap = StringKeyMap.createAsFlexibleConcurrent();
            _tablePropertyNameFlexibleMap.put(getTableDbName(), getTablePropertyName());
            return _tableDbNameFlexibleMap;
        }
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    /**
     * {@inheritDoc}
     */
    public boolean hasColumn(String columnFlexibleName) {
        assertStringNotNullAndNotTrimmedEmpty("columnFlexibleName", columnFlexibleName);
        return getColumnInfoFlexibleMap().containsKey(columnFlexibleName);
    }

    /**
     * {@inheritDoc}
     */
    public ColumnInfo findColumnInfo(String columnFlexibleName) {
        assertStringNotNullAndNotTrimmedEmpty("columnFlexibleName", columnFlexibleName);
        final ColumnInfo columnInfo = getColumnInfoFlexibleMap().get(columnFlexibleName);
        if (columnInfo == null) {
            String msg = "Not found column by columnFlexibleName: " + columnFlexibleName;
            msg = msg + " tableName=" + getTableDbName();
            throw new IllegalArgumentException(msg);
        }
        return columnInfo;
    }

    protected ColumnInfo cci(String columnDbName, String columnSqlName, String columnSynonymName, String columnAlias,
            boolean notNull, String propertyName, Class<?> propertyType, boolean primary, boolean autoIncrement,
            String columnDbType, Integer columnSize, Integer decimalDigits, boolean commonColumn,
            OptimisticLockType optimisticLockType, String columnComment, String foreignListExp, String referrerListExp,
            ClassificationMeta classificationMeta) { // createColumnInfo()
        final String delimiter = ",";
        List<String> foreignPropList = null;
        if (foreignListExp != null && foreignListExp.trim().length() > 0) {
            foreignPropList = splitListTrimmed(foreignListExp, delimiter);
        }
        List<String> referrerPropList = null;
        if (referrerListExp != null && referrerListExp.trim().length() > 0) {
            referrerPropList = splitListTrimmed(referrerListExp, delimiter);
        }
        return new ColumnInfo(this, columnDbName, columnSqlName, columnSynonymName, columnAlias, notNull, propertyName,
                propertyType, primary, autoIncrement, columnDbType, columnSize, decimalDigits, commonColumn,
                optimisticLockType, columnComment, foreignPropList, referrerPropList, classificationMeta);
    }

    /**
     * {@inheritDoc}
     */
    public List<ColumnInfo> getColumnInfoList() {
        if (_columnInfoList != null) {
            return _columnInfoList;
        }
        synchronized (this) {
            if (_columnInfoList != null) {
                return _columnInfoList;
            }
            _columnInfoList = ccil();
            return _columnInfoList;
        }
    }

    protected abstract List<ColumnInfo> ccil(); // createColumnInfoList()

    /**
     * Get the flexible map of column information.
     * @return The flexible map of column information. (NotNull, NotEmpty)
     */
    protected Map<String, ColumnInfo> getColumnInfoFlexibleMap() {
        if (_columnInfoFlexibleMap != null) {
            return _columnInfoFlexibleMap;
        }
        final List<ColumnInfo> columnInfoList = getColumnInfoList();
        synchronized (this) {
            if (_columnInfoFlexibleMap != null) {
                return _columnInfoFlexibleMap;
            }
            _columnInfoFlexibleMap = StringKeyMap.createAsFlexibleConcurrent();
            for (ColumnInfo columnInfo : columnInfoList) {
                _columnInfoFlexibleMap.put(columnInfo.getColumnDbName(), columnInfo);
                final String columnSynonym = columnInfo.getColumnSynonym();
                if (columnSynonym != null) {
                    _columnInfoFlexibleMap.put(columnSynonym, columnInfo); // to find by synonym name
                }
            }
            return _columnInfoFlexibleMap;
        }
    }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    protected UniqueInfo cpui(ColumnInfo uniqueColumnInfo) { // createPrimaryUniqueInfo()
        return cpui(Arrays.asList(uniqueColumnInfo));
    }

    protected UniqueInfo cpui(List<ColumnInfo> uniqueColumnInfoList) { // createPrimaryUniqueInfo()
        UniqueInfo uniqueInfo = new UniqueInfo(this, uniqueColumnInfoList, true);
        return uniqueInfo;
    }

    // ===================================================================================
    //                                                                       Relation Info
    //                                                                       =============
    /**
     * @param relationPropertyName The flexible name of the relation property. (NotNull)
     * @return The information of relation. (NotNull)
     */
    public RelationInfo findRelationInfo(String relationPropertyName) {
        assertStringNotNullAndNotTrimmedEmpty("relationPropertyName", relationPropertyName);
        return hasForeign(relationPropertyName) ? (RelationInfo) findForeignInfo(relationPropertyName)
                : (RelationInfo) findReferrerInfo(relationPropertyName);
    }

    // -----------------------------------------------------
    //                                       Foreign Element
    //                                       ---------------
    /**
     * {@inheritDoc}
     */
    public boolean hasForeign(String foreignPropertyName) {
        assertStringNotNullAndNotTrimmedEmpty("foreignPropertyName", foreignPropertyName);
        final String methodName = buildRelationInfoGetterMethodNameInitCap("foreign", foreignPropertyName);
        return hasMethod(methodName);
    }

    /**
     * {@inheritDoc}
     */
    public DBMeta findForeignDBMeta(String foreignPropertyName) {
        return findForeignInfo(foreignPropertyName).getForeignDBMeta();
    }

    /**
     * {@inheritDoc}
     */
    public ForeignInfo findForeignInfo(String foreignPropertyName) {
        assertStringNotNullAndNotTrimmedEmpty("foreignPropertyName", foreignPropertyName);
        final String methodName = buildRelationInfoGetterMethodNameInitCap("foreign", foreignPropertyName);
        final Method method = DfReflectionUtil.getPublicMethod(getClass(), methodName, null);
        return (ForeignInfo) DfReflectionUtil.invoke(method, this, null);
    }

    protected ForeignInfo cfi(String propName, DBMeta localDbm, DBMeta foreignDbm,
            Map<ColumnInfo, ColumnInfo> localForeignColumnInfoMap, int relNo, boolean oneToOne, boolean bizOneToOne) { // createForeignInfo()
        return new ForeignInfo(propName, localDbm, foreignDbm, localForeignColumnInfoMap, relNo, oneToOne, bizOneToOne);
    }

    /**
     * {@inheritDoc}
     */
    public List<ForeignInfo> getForeignInfoList() {
        if (_foreignInfoList != null) {
            return _foreignInfoList;
        }
        synchronized (this) {
            if (_foreignInfoList != null) {
                return _foreignInfoList;
            }
            final Method[] methods = this.getClass().getMethods();
            _foreignInfoList = newArrayList();
            final String prefix = "foreign";
            final Class<ForeignInfo> returnType = ForeignInfo.class;
            for (Method method : methods) {
                if (method.getName().startsWith(prefix) && returnType.equals(method.getReturnType())) {
                    _foreignInfoList.add((ForeignInfo) DfReflectionUtil.invoke(method, this, null));
                }
            }
            return _foreignInfoList;
        }
    }

    /**
     * Get the flexible map of foreign information.
     * @return The flexible map of foreign information. (NotNull, NotEmpty)
     */
    protected Map<String, ForeignInfo> getForeignInfoFlexibleMap() {
        if (_foreignInfoFlexibleMap != null) {
            return _foreignInfoFlexibleMap;
        }
        final List<ForeignInfo> foreignInfoList = getForeignInfoList();
        synchronized (this) {
            if (_foreignInfoFlexibleMap != null) {
                return _foreignInfoFlexibleMap;
            }
            _foreignInfoFlexibleMap = StringKeyMap.createAsFlexibleConcurrent();
            for (ForeignInfo foreignInfo : foreignInfoList) {
                _foreignInfoFlexibleMap.put(foreignInfo.getForeignPropertyName(), foreignInfo);
            }
            return _foreignInfoFlexibleMap;
        }
    }

    // -----------------------------------------------------
    //                                      Referrer Element
    //                                      ----------------
    /**
     * @param referrerPropertyName The flexible name of the referrer property. (NotNull)
     * @return Determination. (NotNull)
     */
    public boolean hasReferrer(String referrerPropertyName) {
        assertStringNotNullAndNotTrimmedEmpty("referrerPropertyName", referrerPropertyName);
        final String methodName = buildRelationInfoGetterMethodNameInitCap("referrer", referrerPropertyName);
        return hasMethod(methodName);
    }

    /**
     * @param referrerPropertyName The flexible name of the referrer property. (NotNull)
     * @return Referrer DBMeta. (NotNull)
     */
    public DBMeta findReferrerDBMeta(String referrerPropertyName) {
        assertStringNotNullAndNotTrimmedEmpty("referrerPropertyName", referrerPropertyName);
        return findReferrerInfo(referrerPropertyName).getReferrerDBMeta();
    }

    /**
     * @param referrerPropertyName The flexible name of the referrer property. (NotNull)
     * @return Referrer information. (NotNull)
     */
    public ReferrerInfo findReferrerInfo(String referrerPropertyName) {
        assertStringNotNullAndNotTrimmedEmpty("referrerPropertyName", referrerPropertyName);
        final String methodName = buildRelationInfoGetterMethodNameInitCap("referrer", referrerPropertyName);
        final Method method = DfReflectionUtil.getPublicMethod(getClass(), methodName, null);
        return (ReferrerInfo) DfReflectionUtil.invoke(method, this, null);
    }

    protected ReferrerInfo cri(String propName, DBMeta localDbm, DBMeta referrerDbm,
            Map<ColumnInfo, ColumnInfo> localReferrerColumnInfoMap, boolean oneToOne) { // createReferrerInfo()
        return new ReferrerInfo(propName, localDbm, referrerDbm, localReferrerColumnInfoMap, oneToOne);
    }

    /**
     * {@inheritDoc}
     */
    public List<ReferrerInfo> getReferrerInfoList() {
        if (_referrerInfoList != null) {
            return _referrerInfoList;
        }
        synchronized (this) {
            if (_referrerInfoList != null) {
                return _referrerInfoList;
            }
            final Method[] methods = this.getClass().getMethods();
            _referrerInfoList = newArrayList();
            final String prefix = "referrer";
            final Class<ReferrerInfo> returnType = ReferrerInfo.class;
            for (Method method : methods) {
                if (method.getName().startsWith(prefix) && returnType.equals(method.getReturnType())) {
                    _referrerInfoList.add((ReferrerInfo) DfReflectionUtil.invoke(method, this, null));
                }
            }
            return _referrerInfoList;
        }
    }

    /**
     * Get the flexible map of referrer information.
     * @return The flexible map of referrer information. (NotNull, NotEmpty)
     */
    protected Map<String, ReferrerInfo> getReferrerInfoFlexibleMap() {
        if (_referrerInfoFlexibleMap != null) {
            return _referrerInfoFlexibleMap;
        }
        final List<ReferrerInfo> referrerInfoList = getReferrerInfoList();
        synchronized (this) {
            if (_referrerInfoFlexibleMap != null) {
                return _referrerInfoFlexibleMap;
            }
            _referrerInfoFlexibleMap = StringKeyMap.createAsFlexibleConcurrent();
            for (ReferrerInfo referrerInfo : referrerInfoList) {
                _referrerInfoFlexibleMap.put(referrerInfo.getReferrerPropertyName(), referrerInfo);
            }
            return _referrerInfoFlexibleMap;
        }
    }

    // -----------------------------------------------------
    //                                          Common Logic
    //                                          ------------
    protected String buildRelationInfoGetterMethodNameInitCap(String targetName, String relationPropertyName) {
        return targetName + relationPropertyName.substring(0, 1).toUpperCase() + relationPropertyName.substring(1);
    }

    // -----------------------------------------------------
    //                                        Relation Trace
    //                                        --------------
    /**
     * Relation trace.
     */
    protected static abstract class AbstractRelationTrace implements RelationTrace {

        /** The list of relation. */
        protected List<RelationInfo> _relationList;

        /** The list of relation trace. */
        protected List<AbstractRelationTrace> _relationTraceList;

        /** The list of relation info as trace. */
        protected List<RelationInfo> _traceRelationInfoList;

        /** The column info as trace. */
        protected ColumnInfo _traceColumnInfo;

        /** The handler of fixed relation trace. */
        protected RelationTraceFixHandler _relationTraceFixHandler;

        /**
         * Constructor for first step.
         * @param relationTraceFixHandler The handler of fixed relation trace. (NullAllowed)
         */
        public AbstractRelationTrace(RelationTraceFixHandler relationTraceFixHandler) {
            this(new ArrayList<RelationInfo>(), new ArrayList<AbstractRelationTrace>());
            this._relationTraceFixHandler = relationTraceFixHandler;
        }

        /**
         * Constructor for relation step.
         * @param relationList The list of relation. (NotNull)
         * @param relationTraceList The list of relation trace. (NotNull)
         */
        public AbstractRelationTrace(List<RelationInfo> relationList, List<AbstractRelationTrace> relationTraceList) {
            this._relationList = relationList;
            this._relationTraceList = relationTraceList;
            this._relationTraceList.add(this);
        }

        /**
         * {@inheritDoc}
         */
        public List<RelationInfo> getTraceRelation() {
            return _traceRelationInfoList;
        }

        /**
         * {@inheritDoc}
         */
        public ColumnInfo getTraceColumn() {
            return _traceColumnInfo;
        }

        /**
         * Fix trace.
         * @param traceRelationInfoList The trace of relation as the list of relation info. (NotNull)
         * @param traceColumnInfo The trace of column as column info. (NullAllowed)
         * @return Relation trace(result). (NotNull)
         */
        protected RelationTrace fixTrace(List<RelationInfo> traceRelationInfoList, ColumnInfo traceColumnInfo) {
            final AbstractRelationTrace localRelationTrace = (AbstractRelationTrace) _relationTraceList.get(0);
            localRelationTrace.setTraceRelation(traceRelationInfoList);
            localRelationTrace.setTraceColumn(traceColumnInfo);
            localRelationTrace.recycle();
            localRelationTrace.handleFixedRelationTrace();
            return localRelationTrace;
        }

        protected void setTraceRelation(List<RelationInfo> traceRelationInfoList) {
            this._traceRelationInfoList = traceRelationInfoList;
        }

        protected void setTraceColumn(ColumnInfo traceColumn) {
            this._traceColumnInfo = traceColumn;
        }

        protected void recycle() {
            this._relationList = new ArrayList<RelationInfo>();
            this._relationTraceList = new ArrayList<AbstractRelationTrace>();
            this._relationTraceList.add(this);
        }

        protected void handleFixedRelationTrace() {
            if (_relationTraceFixHandler != null) {
                _relationTraceFixHandler.handleFixedTrace(this);
            }
        }
    }

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============
    // These methods is expected to override if it needs.
    public boolean hasIdentity() {
        return false;
    }

    public boolean hasSequence() {
        return false;
    }

    public String getSequenceName() {
        return null;
    }

    public String getSequenceNextValSql() {
        if (!hasSequence()) {
            return null;
        }
        return getCurrentDBDef().dbway().buildSequenceNextValSql(getSequenceName());
    }

    public Integer getSequenceIncrementSize() {
        return null;
    }

    public Integer getSequenceCacheSize() {
        return null;
    }

    public boolean hasVersionNo() {
        return false;
    }

    public ColumnInfo getVersionNoColumnInfo() {
        return null;
    }

    public boolean hasUpdateDate() {
        return false;
    }

    public ColumnInfo getUpdateDateColumnInfo() {
        return null;
    }

    public boolean hasCommonColumn() {
        return false;
    }

    public List<ColumnInfo> getCommonColumnInfoList() {
        return DfCollectionUtil.emptyList();
    }

    public List<ColumnInfo> getCommonColumnInfoBeforeInsertList() {
        return DfCollectionUtil.emptyList();
    }

    public List<ColumnInfo> getCommonColumnInfoBeforeUpdateList() {
        return DfCollectionUtil.emptyList();
    }

    // ===================================================================================
    //                                                                       Name Handling
    //                                                                       =============
    /**
     * {@inheritDoc}
     */
    public boolean hasFlexibleName(String flexibleName) {
        assertStringNotNullAndNotTrimmedEmpty("flexibleName", flexibleName);

        // It uses column before table because column is used much more than table.
        // This is the same consideration at other methods.
        return getColumnInfoFlexibleMap().containsKey(flexibleName)
                || getTableDbNameFlexibleMap().containsKey(flexibleName);
    }

    /**
     * {@inheritDoc}
     */
    public String findDbName(String flexibleName) {
        assertStringNotNullAndNotTrimmedEmpty("flexibleName", flexibleName);
        final ColumnInfo columnInfoMap = getColumnInfoFlexibleMap().get(flexibleName);
        if (columnInfoMap != null) {
            return columnInfoMap.getColumnDbName();
        }
        final String tableDbName = getTableDbNameFlexibleMap().get(flexibleName);
        if (tableDbName != null) {
            return tableDbName;
        }
        String msg = "Not found DB name by the flexible name: flexibleName=" + flexibleName;
        throw new IllegalArgumentException(msg);
    }

    /**
     * {@inheritDoc}
     */
    public String findPropertyName(String flexibleName) {
        assertStringNotNullAndNotTrimmedEmpty("flexibleName", flexibleName);
        final ColumnInfo columnInfoMap = getColumnInfoFlexibleMap().get(flexibleName);
        if (columnInfoMap != null) {
            return columnInfoMap.getPropertyName();
        }
        final String tablePropertyName = getTablePropertyNameFlexibleMap().get(flexibleName);
        if (tablePropertyName != null) {
            return tablePropertyName;
        }
        String msg = "Not found property name by the flexible name: flexibleName=" + flexibleName;
        throw new IllegalArgumentException(msg);
    }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    protected <ENTITY extends Entity> void doAcceptPrimaryKeyMap(ENTITY entity,
            Map<String, ? extends Object> primaryKeyMap, Map<String, Eps<ENTITY>> entityPropertySetupperMap) {
        if (primaryKeyMap == null || primaryKeyMap.isEmpty()) {
            String msg = "The argument 'primaryKeyMap' should not be null or empty:";
            msg = msg + " primaryKeyMap=" + primaryKeyMap;
            throw new IllegalArgumentException(msg);
        }
        entity.clearModifiedInfo();
        final MapStringValueAnalyzer analyzer = new MapStringValueAnalyzer(primaryKeyMap);
        final List<ColumnInfo> columnInfoList = getPrimaryUniqueInfo().getUniqueColumnList();
        for (ColumnInfo columnInfo : columnInfoList) {
            final String columnName = columnInfo.getColumnDbName();
            final String propertyName = columnInfo.getPropertyName();
            final String uncapPropName = initUncap(propertyName);
            final Class<?> propertyType = columnInfo.getPropertyType();
            if (analyzer.init(columnName, uncapPropName, propertyName)) {
                final Object value;
                if (String.class.isAssignableFrom(propertyType)) {
                    value = analyzer.analyzeString(propertyType);
                } else if (Number.class.isAssignableFrom(propertyType)) {
                    value = analyzer.analyzeNumber(propertyType);
                } else if (Date.class.isAssignableFrom(propertyType)) {
                    value = analyzer.analyzeDate(propertyType);
                } else if (Boolean.class.isAssignableFrom(propertyType)) {
                    value = analyzer.analyzeBoolean(propertyType);
                } else if (byte[].class.isAssignableFrom(propertyType)) {
                    value = analyzer.analyzeBinary(propertyType);
                } else if (UUID.class.isAssignableFrom(propertyType)) {
                    value = analyzer.analyzeUUID(propertyType);
                } else {
                    value = analyzer.analyzeOther(propertyType);
                }
                findEps(entityPropertySetupperMap, propertyName).setup(entity, value);
            }
        }
    }

    // -----------------------------------------------------
    //                                               Extract
    //                                               -------
    protected Map<String, Object> doExtractPrimaryKeyMap(Entity entity) {
        return doConvertToColumnValueMap(entity, true);
    }

    protected Map<String, Object> doExtractAllColumnMap(Entity entity) {
        return doConvertToColumnValueMap(entity, false);
    }

    protected Map<String, Object> doConvertToColumnValueMap(Entity entity, boolean pkOnly) {
        final Map<String, Object> valueMap = newLinkedHashMap();
        final List<ColumnInfo> columnInfoList;
        if (pkOnly) {
            columnInfoList = getPrimaryUniqueInfo().getUniqueColumnList();
        } else {
            columnInfoList = getColumnInfoList();
        }
        for (ColumnInfo columnInfo : columnInfoList) {
            final String columnName = columnInfo.getColumnDbName();
            final Object value = columnInfo.read(entity);
            valueMap.put(columnName, value);
        }
        return valueMap;
    }

    // -----------------------------------------------------
    //                                              Analyzer
    //                                              --------
    /**
     * This class is for internal. Don't use this!
     */
    protected static class MapStringValueAnalyzer {
        protected final Map<String, ? extends Object> _valueMap;
        protected String _columnName;
        protected String _uncapPropName;
        protected String _propertyName;

        public MapStringValueAnalyzer(Map<String, ? extends Object> valueMap) {
            this._valueMap = valueMap;
        }

        public boolean init(String columnName, String uncapPropName, String propertyName) {
            this._columnName = columnName;
            this._uncapPropName = uncapPropName;
            this._propertyName = propertyName;
            return _valueMap.containsKey(_columnName);
        }

        @SuppressWarnings("unchecked")
        public <PROPERTY> PROPERTY analyzeString(Class<PROPERTY> javaType) {
            final Object obj = getColumnValue();
            return (PROPERTY) DfTypeUtil.toString(obj);
        }

        @SuppressWarnings("unchecked")
        public <PROPERTY> PROPERTY analyzeNumber(Class<PROPERTY> javaType) {
            final Object obj = getColumnValue();
            return (PROPERTY) DfTypeUtil.toNumber(obj, javaType);
        }

        @SuppressWarnings("unchecked")
        public <PROPERTY> PROPERTY analyzeDate(Class<PROPERTY> javaType) {
            final Object obj = getColumnValue();
            if (Time.class.isAssignableFrom(javaType)) {
                return (PROPERTY) DfTypeUtil.toTime(obj);
            } else if (Timestamp.class.isAssignableFrom(javaType)) {
                return (PROPERTY) DfTypeUtil.toTimestamp(obj);
            } else {
                return (PROPERTY) DfTypeUtil.toDate(obj);
            }
        }

        @SuppressWarnings("unchecked")
        public <PROPERTY> PROPERTY analyzeBoolean(Class<PROPERTY> javaType) {
            final Object obj = getColumnValue();
            return (PROPERTY) DfTypeUtil.toBoolean(obj);
        }

        @SuppressWarnings("unchecked")
        public <PROPERTY> PROPERTY analyzeBinary(Class<PROPERTY> javaType) {
            final Object obj = getColumnValue();
            if (obj == null) {
                return null;
            }
            if (obj instanceof Serializable) {
                return (PROPERTY) DfTypeUtil.toBinary((Serializable) obj);
            }
            throw new UnsupportedOperationException("unsupported binary type: " + obj.getClass());
        }

        @SuppressWarnings("unchecked")
        public <PROPERTY> PROPERTY analyzeUUID(Class<PROPERTY> javaType) {
            final Object obj = getColumnValue();
            return (PROPERTY) DfTypeUtil.toUUID(obj);
        }

        @SuppressWarnings("unchecked")
        public <PROPERTY> PROPERTY analyzeOther(Class<PROPERTY> javaType) {
            final Object obj = getColumnValue();
            if (obj == null) {
                return null;
            }
            if (Classification.class.isAssignableFrom(javaType)) {
                final Class<?>[] argTypes = new Class[] { Object.class };
                final Method method = DfReflectionUtil.getPublicMethod(javaType, "codeOf", argTypes);
                return (PROPERTY) DfReflectionUtil.invokeStatic(method, new Object[] { obj });
            }
            return (PROPERTY) obj;
        }

        protected Object getColumnValue() {
            final Object value = _valueMap.get(_columnName);
            return filterClassificationValue(value);
        }

        protected Object filterClassificationValue(Object value) {
            if (value != null && value instanceof Classification) {
                value = ((Classification) value).code();
            }
            return value;
        }
    }

    // ===================================================================================
    //                                                               Entity Property Setup
    //                                                               =====================
    // It's very INTERNAL!
    protected <ENTITY extends Entity> void setupEps(Map<String, Eps<ENTITY>> entityPropertySetupperMap,
            Eps<ENTITY> setupper, ColumnInfo columnInfo) {
        String columnName = columnInfo.getColumnDbName();
        String propertyName = columnInfo.getPropertyName();
        registerEntityPropertySetupper(columnName, propertyName, setupper, entityPropertySetupperMap);
    }

    protected <ENTITY extends Entity> void registerEntityPropertySetupper(String columnName, String propertyName,
            Eps<ENTITY> setupper, Map<String, Eps<ENTITY>> entityPropertySetupperMap) {
        // Only column name is registered because the map must be flexible map.
        entityPropertySetupperMap.put(columnName, setupper);
    }

    protected <ENTITY extends Entity> Eps<ENTITY> findEps(Map<String, Eps<ENTITY>> entityPropertySetupperMap,
            String propertyName) {
        Eps<ENTITY> setupper = entityPropertySetupperMap.get(propertyName);
        if (setupper == null) {
            String msg = "The propertyName was not found in the map of set-upper of entity property:";
            msg = msg + " propertyName=" + propertyName + " _entityPropertySetupperMap.keySet()="
                    + entityPropertySetupperMap.keySet();
            throw new IllegalStateException(msg);
        }
        return setupper;
    }

    protected Classification gcls(ColumnInfo columnInfo, Object code) { // getClassification
        assertObjectNotNull("columnInfo", columnInfo);
        if (code == null) {
            return null;
        }
        final ClassificationMeta classificationMeta = columnInfo.getClassificationMeta();
        if (classificationMeta == null) {
            return null;
        }
        return classificationMeta.codeOf(code);
    }

    protected void ccls(ColumnInfo columnInfo, Object code) { // checkClassification
        assertObjectNotNull("columnInfo", columnInfo);
        if (code == null) {
            return; // no check null value which means no existence on DB
        }
        final Classification classification = gcls(columnInfo, code);
        if (classification == null) {
            throwIllegalClassificationCodeException(columnInfo, code);
        }
    }

    protected void throwIllegalClassificationCodeException(ColumnInfo columnInfo, Object code) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to get the classification by the code.");
        br.addItem("Advice");
        br.addElement("Please confirm the code value of the classication column on your database.");
        br.addElement("The code may NOT be one of classification code defined on DBFlute.");
        br.addItem("Code");
        br.addElement(code);
        br.addItem("Classication");
        br.addElement(columnInfo.getClassificationMeta());
        br.addItem("Table");
        br.addElement(getTableDbName());
        br.addItem("Column");
        br.addElement(columnInfo.getColumnDbName());
        final String msg = br.buildExceptionMessage();
        throw new IllegalClassificationCodeException(msg);
    }

    protected Integer cti(Object value) { // convertToInteger
        return DfTypeUtil.toInteger(value);
    }

    protected Long ctl(Object value) { // convertToLong
        return DfTypeUtil.toLong(value);
    }

    protected BigDecimal ctb(Object value) { // convertToBigDecimal
        return DfTypeUtil.toBigDecimal(value);
    }

    @SuppressWarnings("unchecked")
    protected <NUMBER extends Number> NUMBER ctn(Object value, Class<NUMBER> type) { // convertToNumber
        return (NUMBER) DfTypeUtil.toNumber(value, type);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    @SuppressWarnings("unchecked")
    protected <ENTITY> ENTITY downcast(Entity entity) {
        checkDowncast(entity);
        return (ENTITY) entity;
    }

    protected void checkDowncast(Entity entity) {
        assertObjectNotNull("entity", entity);
        Class<? extends Entity> entityType = getEntityType();
        Class<? extends Entity> targetType = entity.getClass();
        if (!entityType.isAssignableFrom(targetType)) {
            final String titleName = DfTypeUtil.toClassTitle(entityType);
            String msg = "The entity should be " + titleName + " but it was: " + targetType;
            throw new IllegalStateException(msg);
        }
    }

    protected Map<String, String> setupKeyToLowerMap(boolean dbNameKey) {
        final Map<String, String> map;
        if (dbNameKey) {
            map = newConcurrentHashMap(getTableDbName().toLowerCase(), getTablePropertyName());
        } else {
            map = newConcurrentHashMap(getTablePropertyName().toLowerCase(), getTableDbName());
        }
        final Method[] methods = this.getClass().getMethods();
        final String columnInfoMethodPrefix = "column";
        try {
            for (Method method : methods) {
                final String name = method.getName();
                if (!name.startsWith(columnInfoMethodPrefix)) {
                    continue;
                }
                final ColumnInfo columnInfo = (ColumnInfo) method.invoke(this);
                final String dbName = columnInfo.getColumnDbName();
                final String propertyName = columnInfo.getPropertyName();
                if (dbNameKey) {
                    map.put(dbName.toLowerCase(), propertyName);
                } else {
                    map.put(propertyName.toLowerCase(), dbName);
                }
            }
            return Collections.unmodifiableMap(map);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    // -----------------------------------------------------
    //                                       String Handling
    //                                       ---------------
    protected final String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected final List<String> splitListTrimmed(String str, String delimiter) {
        return Srl.splitListTrimmed(str, delimiter);
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

    // -----------------------------------------------------
    //                                  Collection Generator
    //                                  --------------------
    protected <KEY, VALUE> HashMap<KEY, VALUE> newHashMap() {
        return new HashMap<KEY, VALUE>();
    }

    protected <KEY, VALUE> ConcurrentHashMap<KEY, VALUE> newConcurrentHashMap() {
        return new ConcurrentHashMap<KEY, VALUE>();
    }

    protected <KEY, VALUE> ConcurrentHashMap<KEY, VALUE> newConcurrentHashMap(KEY key, VALUE value) {
        final ConcurrentHashMap<KEY, VALUE> map = newConcurrentHashMap();
        map.put(key, value);
        return map;
    }

    protected <KEY, VALUE> LinkedHashMap<KEY, VALUE> newLinkedHashMap() {
        return new LinkedHashMap<KEY, VALUE>();
    }

    protected <KEY, VALUE> LinkedHashMap<KEY, VALUE> newLinkedHashMap(KEY key, VALUE value) {
        final LinkedHashMap<KEY, VALUE> map = newLinkedHashMap();
        map.put(key, value);
        return map;
    }

    protected <ELEMENT> ArrayList<ELEMENT> newArrayList() {
        return new ArrayList<ELEMENT>();
    }

    protected <ELEMENT> List<ELEMENT> newArrayList(ELEMENT... elements) {
        final List<ELEMENT> list = newArrayList();
        for (ELEMENT element : elements) {
            list.add(element);
        }
        return list;
    }

    protected <ELEMENT> ArrayList<ELEMENT> newArrayList(Collection<ELEMENT> collection) {
        return new ArrayList<ELEMENT>(collection);
    }

    // -----------------------------------------------------
    //                                   Reflection Handling
    //                                   -------------------
    /**
     * Does it have the method?
     * @param methodName The name of method. (NotNull, NotEmpty, PublicMethodOnly)
     * @return Determination.
     */
    protected boolean hasMethod(String methodName) {
        assertStringNotNullAndNotTrimmedEmpty("methodName", methodName);
        if (_methodNameMap.isEmpty()) {
            synchronized (_methodNameMap) {
                if (_methodNameMap.isEmpty()) {
                    final Method[] methods = this.getClass().getMethods();
                    for (Method method : methods) {
                        _methodNameMap.put(method.getName(), DUMMY_VALUE);
                    }
                }
            }
        }
        return _methodNameMap.containsKey(methodName);
    }

    // -----------------------------------------------------
    //                                         Assert Object
    //                                         -------------
    /**
     * Assert that the argument is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        DfAssertUtil.assertObjectNotNull(variableName, value);
    }

    // -----------------------------------------------------
    //                                         Assert String
    //                                         -------------
    /**
     * Assert that the string is not null and not trimmed empty.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     */
    protected void assertStringNotNullAndNotTrimmedEmpty(String variableName, String value) {
        DfAssertUtil.assertStringNotNullAndNotTrimmedEmpty(variableName, value);
    }
}
