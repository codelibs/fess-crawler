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
package org.seasar.robot.dbflute.dbmeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.robot.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.robot.dbflute.dbmeta.info.RelationInfo;
import org.seasar.robot.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.robot.dbflute.helper.StringKeyMap;
import org.seasar.robot.dbflute.helper.mapstring.MapListString;
import org.seasar.robot.dbflute.helper.mapstring.MapStringBuilder;
import org.seasar.robot.dbflute.helper.mapstring.impl.MapListStringImpl;
import org.seasar.robot.dbflute.helper.mapstring.impl.MapStringBuilderImpl;
import org.seasar.robot.dbflute.jdbc.Classification;
import org.seasar.robot.dbflute.util.DfAssertUtil;
import org.seasar.robot.dbflute.util.DfStringUtil;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * The abstract class of DB meta.
 * @author jflute
 */
public abstract class AbstractDBMeta implements DBMeta {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
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

    protected ColumnInfo cci(String columnDbName, String columnAlias, String propertyName, Class<?> propertyType,
            boolean primary, boolean autoIncrement, Integer columnSize, Integer columnDecimalDigits,
            boolean commonColumn, OptimisticLockType optimisticLockType) { // createColumnInfo()
        return new ColumnInfo(this, columnDbName, columnAlias, propertyName, propertyType, primary, autoIncrement,
                columnSize, columnDecimalDigits, commonColumn, optimisticLockType);
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
        Method method = null;
        try {
            method = this.getClass().getMethod(methodName, new Class[] {});
        } catch (NoSuchMethodException e) {
            String msg = "Not found foreign by foreignPropertyName: foreignPropertyName=" + foreignPropertyName;
            msg = msg + " tableName=" + getTableDbName() + " methodName=" + methodName;
            throw new IllegalStateException(msg, e);
        }
        try {
            return (ForeignInfo) method.invoke(this, new Object[] {});
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    protected ForeignInfo cfi(String propName, DBMeta localDbm, DBMeta foreignDbm,
            Map<ColumnInfo, ColumnInfo> localForeignColumnInfoMap, int relNo, boolean oneToOne) { // createForeignInfo()
        return new ForeignInfo(propName, localDbm, foreignDbm, localForeignColumnInfoMap, relNo, oneToOne);
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
            Method[] methods = this.getClass().getMethods();
            _foreignInfoList = newArrayList();
            String prefix = "foreign";
            Class<ForeignInfo> returnType = ForeignInfo.class;
            Object[] args = new Object[] {};
            try {
                for (Method method : methods) {
                    if (method.getName().startsWith(prefix) && returnType.equals(method.getReturnType())) {
                        _foreignInfoList.add((ForeignInfo) method.invoke(this, args));
                    }
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
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
        Method method = null;
        try {
            method = this.getClass().getMethod(methodName, new Class[] {});
        } catch (NoSuchMethodException e) {
            String msg = "Not found referrer by referrerPropertyName: referrerPropertyName=" + referrerPropertyName;
            msg = msg + " tableName=" + getTableDbName() + " methodName=" + methodName;
            throw new IllegalStateException(msg, e);
        }
        try {
            return (ReferrerInfo) method.invoke(this, new Object[] {});
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getCause());
        }
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
            Method[] methods = this.getClass().getMethods();
            _referrerInfoList = newArrayList();
            String prefix = "referrer";
            Class<ReferrerInfo> returnType = ReferrerInfo.class;
            Object[] args = new Object[] {};
            try {
                for (Method method : methods) {
                    if (method.getName().startsWith(prefix) && returnType.equals(method.getReturnType())) {
                        _referrerInfoList.add((ReferrerInfo) method.invoke(this, args));
                    }
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
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
         * @param relationTraceFixHandler The handler of fixed relation trace. (Nullable)
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
         * @param traceColumnInfo The trace of column as column info. (Nullable)
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
    //                                                                          Map String
    //                                                                          ==========
    /**
     * {@inheritDoc}
     */
    public MapListString createMapListString() {
        return MapStringUtil.createMapListString();
    }

    /**
     * {@inheritDoc}
     */
    public MapStringBuilder createMapStringBuilder() {
        final List<String> columnDbNameList = new ArrayList<String>();
        for (final Iterator<ColumnInfo> ite = getColumnInfoList().iterator(); ite.hasNext();) {
            final ColumnInfo columnInfo = (ColumnInfo) ite.next();
            columnDbNameList.add(columnInfo.getColumnDbName());
        }
        return MapStringUtil.createMapStringBuilder(columnDbNameList);
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

    public String getSequenceNextValSql() {
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
            Map<String, ? extends Object> columnValueMap, Map<String, Eps<ENTITY>> entityPropertySetupperMap) {
        MapAssertUtil.assertColumnValueMapNotNullAndNotEmpty(columnValueMap);
        entity.clearModifiedPropertyNames();
        MapStringValueAnalyzer analyzer = new MapStringValueAnalyzer(columnValueMap);
        List<ColumnInfo> columnInfoList = getPrimaryUniqueInfo().getUniqueColumnList();
        for (ColumnInfo columnInfo : columnInfoList) {
            String columnName = columnInfo.getColumnDbName();
            String propertyName = columnInfo.getPropertyName();
            String uncapPropName = initUncap(propertyName);
            Class<?> propertyType = columnInfo.getPropertyType();
            if (analyzer.init(columnName, uncapPropName, propertyName)) {
                final Object value;
                if (String.class.isAssignableFrom(propertyType)) {
                    value = analyzer.analyzeString(propertyType);
                } else if (Number.class.isAssignableFrom(propertyType)) {
                    value = analyzer.analyzeNumber(propertyType);
                } else if (java.util.Date.class.isAssignableFrom(propertyType)) {
                    value = analyzer.analyzeDate(propertyType);
                } else {
                    value = analyzer.analyzeOther(propertyType);
                }
                findEps(entityPropertySetupperMap, propertyName).setup(entity, value);
            }
        }
    }

    protected <ENTITY extends Entity> void doAcceptColumnValueMap(ENTITY entity,
            Map<String, ? extends Object> columnValueMap, Map<String, Eps<ENTITY>> entityPropertySetupperMap) {
        MapAssertUtil.assertColumnValueMapNotNullAndNotEmpty(columnValueMap);
        MapStringValueAnalyzer analyzer = new MapStringValueAnalyzer(columnValueMap);
        List<ColumnInfo> columnInfoList = getColumnInfoList();
        for (ColumnInfo columnInfo : columnInfoList) {
            String columnName = columnInfo.getColumnDbName();
            String propertyName = columnInfo.getPropertyName();
            String uncapPropName = initUncap(propertyName);
            Class<?> propertyType = columnInfo.getPropertyType();
            if (analyzer.init(columnName, uncapPropName, propertyName)) {
                final Object value;
                if (String.class.isAssignableFrom(propertyType)) {
                    value = analyzer.analyzeString(propertyType);
                } else if (Number.class.isAssignableFrom(propertyType)) {
                    value = analyzer.analyzeNumber(propertyType);
                } else if (java.util.Date.class.isAssignableFrom(propertyType)) {
                    value = analyzer.analyzeDate(propertyType);
                } else {
                    value = analyzer.analyzeOther(propertyType);
                }
                findEps(entityPropertySetupperMap, propertyName).setup(entity, value);
            }
        }
    }

    protected String doExtractPrimaryKeyMapString(Entity entity, String startBrace, String endBrace, String delimiter,
            String equal) {
        List<ColumnInfo> uniqueColumnList = getPrimaryUniqueInfo().getUniqueColumnList();
        return doExtractValueMapMapString(entity, uniqueColumnList, startBrace, endBrace, delimiter, equal);
    }

    protected String doExtractColumnValueMapString(Entity entity, String startBrace, String endBrace, String delimiter,
            String equal) {
        List<ColumnInfo> columnInfoList = getColumnInfoList();
        return doExtractValueMapMapString(entity, columnInfoList, startBrace, endBrace, delimiter, equal);
    }

    protected String doExtractValueMapMapString(Entity entity, List<ColumnInfo> columnInfoList, String startBrace,
            String endBrace, String delimiter, String equal) {
        String mapMarkAndStartBrace = MAP_STRING_MAP_MARK + startBrace;
        StringBuilder sb = new StringBuilder();
        try {
            for (ColumnInfo columnInfo : columnInfoList) {
                String columnName = columnInfo.getColumnDbName();
                Method getterMethod = columnInfo.findGetter();
                Object value = getterMethod.invoke(entity, (Object[]) null);
                value = filterClassificationValueIfNeeds(value);
                helpAppendingColumnValueString(sb, delimiter, equal, columnName, value);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        sb.delete(0, delimiter.length()).insert(0, mapMarkAndStartBrace).append(endBrace);
        return sb.toString();
    }

    protected Object filterClassificationValueIfNeeds(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Classification) {
            value = ((Classification) value).code();
        }
        return value;
    }

    // -----------------------------------------------------
    //                                               Convert
    //                                               -------
    protected Map<String, Object> doConvertToColumnValueMap(Entity entity) {
        Map<String, Object> valueMap = newLinkedHashMap();
        try {
            List<ColumnInfo> columnInfoList = getColumnInfoList();
            for (ColumnInfo columnInfo : columnInfoList) {
                String columnName = columnInfo.getColumnDbName();
                Method getterMethod = columnInfo.findGetter();
                Object value = getterMethod.invoke(entity, (Object[]) null);
                valueMap.put(columnName, value);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return valueMap;
    }

    protected Map<String, String> doConvertToColumnStringValueMap(Entity entity) {
        Map<String, String> valueMap = newLinkedHashMap();
        try {
            List<ColumnInfo> columnInfoList = getColumnInfoList();
            for (ColumnInfo columnInfo : columnInfoList) {
                String columnName = columnInfo.getColumnDbName();
                Method getterMethod = columnInfo.findGetter();
                Object value = getterMethod.invoke(entity, (Object[]) null);
                valueMap.put(columnName, helpGettingColumnStringValue(value));
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return valueMap;
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
            String msg = "The propertyName was Not Found in the map of setupper of entity property:";
            msg = msg + " propertyName=" + propertyName + " _entityPropertySetupperMap.keySet()="
                    + entityPropertySetupperMap.keySet();
            throw new IllegalStateException(msg);
        }
        return setupper;
    }

    // ===================================================================================
    //                                                                          Util Class
    //                                                                          ==========
    /**
     * This class is for Internal. Don't use this!
     */
    protected static class MapStringUtil {

        public static void acceptPrimaryKeyMapString(String primaryKeyMapString, Entity entity) {
            if (primaryKeyMapString == null) {
                String msg = "The argument[primaryKeyMapString] should not be null.";
                throw new IllegalArgumentException(msg);
            }
            final String prefix = MAP_STRING_MAP_MARK + MAP_STRING_START_BRACE;
            final String suffix = MAP_STRING_END_BRACE;
            if (!primaryKeyMapString.trim().startsWith(prefix)) {
                primaryKeyMapString = prefix + primaryKeyMapString;
            }
            if (!primaryKeyMapString.trim().endsWith(suffix)) {
                primaryKeyMapString = primaryKeyMapString + suffix;
            }
            MapListString mapListString = createMapListString();
            entity.getDBMeta().acceptPrimaryKeyMap(entity, mapListString.generateMap(primaryKeyMapString));
        }

        public static void acceptColumnValueMapString(String columnValueMapString, Entity entity) {
            if (columnValueMapString == null) {
                String msg = "The argument[columnValueMapString] should not be null.";
                throw new IllegalArgumentException(msg);
            }
            final String prefix = MAP_STRING_MAP_MARK + MAP_STRING_START_BRACE;
            final String suffix = MAP_STRING_END_BRACE;
            if (!columnValueMapString.trim().startsWith(prefix)) {
                columnValueMapString = prefix + columnValueMapString;
            }
            if (!columnValueMapString.trim().endsWith(suffix)) {
                columnValueMapString = columnValueMapString + suffix;
            }
            MapListString mapListString = createMapListString();
            entity.getDBMeta().acceptColumnValueMap(entity, mapListString.generateMap(columnValueMapString));
        }

        public static String extractPrimaryKeyMapString(Entity entity) {
            final String startBrace = MAP_STRING_START_BRACE;
            final String endBrace = MAP_STRING_END_BRACE;
            final String delimiter = MAP_STRING_DELIMITER;
            final String equal = MAP_STRING_EQUAL;
            return entity.getDBMeta().extractPrimaryKeyMapString(entity, startBrace, endBrace, delimiter, equal);
        }

        public static String extractColumnValueMapString(Entity entity) {
            final String startBrace = MAP_STRING_START_BRACE;
            final String endBrace = MAP_STRING_END_BRACE;
            final String delimiter = MAP_STRING_DELIMITER;
            final String equal = MAP_STRING_EQUAL;
            return entity.getDBMeta().extractColumnValueMapString(entity, startBrace, endBrace, delimiter, equal);
        }

        public static void checkTypeString(Object value, String propertyName, String typeName) {
            if (value == null) {
                throw new IllegalArgumentException("The value should not be null: " + propertyName);
            }
            if (!(value instanceof String)) {
                String msg = "The value of " + propertyName + " should be " + typeName + " or String: ";
                msg = msg + "valueType=" + value.getClass() + " value=" + value;
                throw new IllegalArgumentException(msg);
            }
        }

        public static long parseDateStringAsMillis(Object value, String propertyName, String typeName) {
            checkTypeString(value, propertyName, typeName);
            try {
                final String valueString = filterTimestampValue(((String) value).trim());
                return java.sql.Timestamp.valueOf(valueString).getTime();
            } catch (RuntimeException e) {
                String msg = "The value of " + propertyName + " should be " + typeName + ". but: " + value;
                throw new RuntimeException(msg + " threw the exception: value=[" + value + "]", e);
            }
        }

        public static String filterTimestampValue(String value) {
            value = value.trim();
            if (value.indexOf("/") == 4 && value.lastIndexOf("/") == 7) {
                value = value.replaceAll("/", "-");
            }
            if (value.indexOf("-") == 4 && value.lastIndexOf("-") == 7) {
                if (value.length() == "2007-07-09".length()) {
                    value = value + " 00:00:00";
                }
            }
            return value;
        }

        public static String formatDate(java.util.Date value) {
            return getFormatDateFormat().format(value);
        }

        public static String formatTimestamp(java.sql.Timestamp value) {
            return getFormatDateFormat().format(value);
        }

        public static java.text.DateFormat getParseDateFormat() {
            return java.text.DateFormat.getDateInstance();
        }

        public static java.text.DateFormat getFormatDateFormat() {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }

        public static MapListString createMapListString() {
            final MapListString mapListString = new MapListStringImpl();
            mapListString.setMapMark(MAP_STRING_MAP_MARK);
            mapListString.setListMark(MAP_STRING_LIST_MARK);
            mapListString.setStartBrace(MAP_STRING_START_BRACE);
            mapListString.setEndBrace(MAP_STRING_END_BRACE);
            mapListString.setEqual(MAP_STRING_EQUAL);
            mapListString.setDelimiter(MAP_STRING_DELIMITER);
            return mapListString;
        }

        public static MapStringBuilder createMapStringBuilder(List<String> columnNameList) {
            MapStringBuilder mapStringBuilder = new MapStringBuilderImpl();
            mapStringBuilder.setMsMapMark(MAP_STRING_MAP_MARK);
            mapStringBuilder.setMsStartBrace(MAP_STRING_START_BRACE);
            mapStringBuilder.setMsEndBrace(MAP_STRING_END_BRACE);
            mapStringBuilder.setMsEqual(MAP_STRING_EQUAL);
            mapStringBuilder.setMsDelimiter(MAP_STRING_DELIMITER);
            mapStringBuilder.setColumnNameList(columnNameList);
            return mapStringBuilder;
        }
    }

    /**
     * This class is for Internal. Don't use this!
     */
    protected static class MapAssertUtil {
        public static void assertPrimaryKeyMapNotNullAndNotEmpty(java.util.Map<String, ? extends Object> primaryKeyMap) {
            if (primaryKeyMap == null) {
                String msg = "The argument[primaryKeyMap] should not be null.";
                throw new IllegalArgumentException(msg);
            }
            if (primaryKeyMap.isEmpty()) {
                String msg = "The argument[primaryKeyMap] should not be empty.";
                throw new IllegalArgumentException(msg);
            }
        }

        public static void assertColumnExistingInPrimaryKeyMap(java.util.Map<String, ? extends Object> primaryKeyMap,
                String columnName) {
            if (!primaryKeyMap.containsKey(columnName)) {
                String msg = "The primaryKeyMap must have the value of " + columnName;
                throw new IllegalStateException(msg + ": primaryKeyMap --> " + primaryKeyMap);
            }
        }

        public static void assertColumnValueMapNotNullAndNotEmpty(java.util.Map<String, ? extends Object> columnValueMap) {
            if (columnValueMap == null) {
                String msg = "The argument[columnValueMap] should not be null.";
                throw new IllegalArgumentException(msg);
            }
            if (columnValueMap.isEmpty()) {
                String msg = "The argument[columnValueMap] should not be empty.";
                throw new IllegalArgumentException(msg);
            }
        }
    }

    /**
     * This class is for Internal. Don't use this!
     */
    @SuppressWarnings("unchecked")
    protected static class MapStringValueAnalyzer {
        protected Map<String, ? extends Object> _valueMap;
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

        public <COLUMN_TYPE> COLUMN_TYPE analyzeString(Class<COLUMN_TYPE> javaType) {
            final Object obj = _valueMap.get(_columnName);
            if (obj == null) {
                return null;
            }
            helpCheckingTypeString(obj, _uncapPropName, javaType.getName());
            return (COLUMN_TYPE) obj;
        }

        public <COLUMN_TYPE> COLUMN_TYPE analyzeNumber(Class<COLUMN_TYPE> javaType) {
            final Object obj = _valueMap.get(_columnName);
            if (obj == null) {
                return null;
            }
            if (javaType.isAssignableFrom(obj.getClass())) {
                return (COLUMN_TYPE) obj;
            }
            return (COLUMN_TYPE) newInstanceByConstructor(javaType, String.class, obj.toString());
        }

        public <COLUMN_TYPE> COLUMN_TYPE analyzeDate(Class<COLUMN_TYPE> javaType) {
            final Object obj = _valueMap.get(_columnName);
            if (obj == null) {
                return null;
            }
            if (javaType.isAssignableFrom(obj.getClass())) {
                return (COLUMN_TYPE) obj;
            }
            return (COLUMN_TYPE) newInstanceByConstructor(javaType, long.class, helpParsingDateString(obj,
                    _uncapPropName, javaType.getName()));
        }

        public <COLUMN_TYPE> COLUMN_TYPE analyzeOther(Class<COLUMN_TYPE> javaType) {
            final Object obj = _valueMap.get(_columnName);
            if (obj == null) {
                return null;
            }
            if (Classification.class.isAssignableFrom(javaType)) {
                try {
                    final Method codeOfMethod = javaType.getMethod("codeOf", new Class[] { Object.class });
                    return (COLUMN_TYPE) codeOfMethod.invoke(null, new Object[] { obj });
                } catch (Exception e) {
                    String msg = "The method threw the exception: javaType=" + javaType + " obj=" + obj;
                    throw new IllegalStateException(msg, e);
                }
            }
            return (COLUMN_TYPE) obj;
        }

        private void helpCheckingTypeString(Object value, String uncapPropName, String typeName) {
            MapStringUtil.checkTypeString(value, uncapPropName, typeName);
        }

        private long helpParsingDateString(Object value, String uncapPropName, String typeName) {
            return MapStringUtil.parseDateStringAsMillis(value, uncapPropName, typeName);
        }

        protected Object newInstanceByConstructor(Class targetType, Class argType, Object arg) {
            java.lang.reflect.Constructor constructor;
            try {
                constructor = targetType.getConstructor(new Class[] { argType });
            } catch (SecurityException e) {
                String msg = "targetType=" + targetType + " argType=" + argType + " arg=" + arg;
                throw new IllegalStateException(msg, e);
            } catch (NoSuchMethodException e) {
                String msg = "targetType=" + targetType + " argType=" + argType + " arg=" + arg;
                throw new IllegalStateException(msg, e);
            }
            try {
                return constructor.newInstance(new Object[] { arg });
            } catch (IllegalArgumentException e) {
                String msg = "targetType=" + targetType + " argType=" + argType + " arg=" + arg;
                throw new IllegalStateException(msg, e);
            } catch (InstantiationException e) {
                String msg = "targetType=" + targetType + " argType=" + argType + " arg=" + arg;
                throw new IllegalStateException(msg, e);
            } catch (IllegalAccessException e) {
                String msg = "targetType=" + targetType + " argType=" + argType + " arg=" + arg;
                throw new IllegalStateException(msg, e);
            } catch (InvocationTargetException e) {
                String msg = "targetType=" + targetType + " argType=" + argType + " arg=" + arg;
                throw new IllegalStateException(msg, e.getCause());
            }
        }
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
            String name = entityType.getSimpleName();
            String msg = "The entity should be " + name + " but it was: " + targetType;
            throw new IllegalStateException(msg);
        }
    }

    protected void helpAppendingColumnValueString(StringBuilder sb, String delimiter, String equal, String colName,
            Object value) {
        sb.append(delimiter).append(colName).append(equal);
        sb.append(helpGettingColumnStringValue(value));
    }

    protected String helpGettingColumnStringValue(Object value) {
        if (value instanceof java.sql.Timestamp) {
            return helpFormatingTimestamp((java.sql.Timestamp) value);
        } else if (value instanceof java.util.Date) {
            return helpFormatingDate((java.util.Date) value);
        } else {
            return value != null ? value.toString() : "";
        }
    }

    protected String helpFormatingDate(java.util.Date date) {
        return MapStringUtil.formatDate(date);
    }

    protected String helpFormatingTimestamp(java.sql.Timestamp timestamp) {
        return MapStringUtil.formatTimestamp(timestamp);
    }

    protected Map<String, String> setupKeyToLowerMap(boolean dbNameKey) {
        final Map<String, String> map;
        if (dbNameKey) {
            map = newConcurrentHashMap(getTableDbName().toLowerCase(), getTablePropertyName());
        } else {
            map = newConcurrentHashMap(getTablePropertyName().toLowerCase(), getTableDbName());
        }
        Method[] methods = this.getClass().getMethods();
        String columnInfoMethodPrefix = "column";
        try {
            for (Method method : methods) {
                String name = method.getName();
                if (!name.startsWith(columnInfoMethodPrefix)) {
                    continue;
                }
                ColumnInfo columnInfo = (ColumnInfo) method.invoke(this);
                String dbName = columnInfo.getColumnDbName();
                String propertyName = columnInfo.getPropertyName();
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
        return DfStringUtil.replace(text, fromText, toText);
    }

    protected String initCap(String str) {
        return DfStringUtil.initCap(str);
    }

    protected String initUncap(String str) {
        return DfStringUtil.initUncap(str);
    }

    protected String getLineSeparator() {
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
        ConcurrentHashMap<KEY, VALUE> map = newConcurrentHashMap();
        map.put(key, value);
        return map;
    }

    protected <KEY, VALUE> LinkedHashMap<KEY, VALUE> newLinkedHashMap() {
        return new LinkedHashMap<KEY, VALUE>();
    }

    protected <KEY, VALUE> LinkedHashMap<KEY, VALUE> newLinkedHashMap(KEY key, VALUE value) {
        LinkedHashMap<KEY, VALUE> map = newLinkedHashMap();
        map.put(key, value);
        return map;
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
