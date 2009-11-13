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
package org.seasar.robot.dbflute.s2dao.rowcreator.impl;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyType;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnRelationRowCreationResource {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Result set. */
    protected ResultSet resultSet;

    /** Relation row. Initialized at first or initialized after. */
    protected Object row;

    /** Relation property type. */
    protected TnRelationPropertyType relationPropertyType;

    /** The set of column name. */
    protected Set<String> columnNames;

    /** The map of relation key values. */
    protected Map<String, Object> relKeyValues;

    /** The map of relation property cache. */
    protected Map<String, Map<String, TnPropertyType>> relationPropertyCache;// Map<String(relationNoSuffix), Map<String(columnName), PropertyType>>

    /** The suffix of base object. */
    protected String baseSuffix;

    /** The suffix of relation no. */
    protected String relationNoSuffix;

    /** The limit of relation nest level. */
    protected int limitRelationNestLevel;

    /** The current relation nest level. Default is one. */
    protected int currentRelationNestLevel;

    /** Current property type. This variable is temporary. */
    protected TnPropertyType currentPropertyType;

    /** The count of valid value. */
    protected int validValueCount;

    /** Does it create dead link? */
    protected boolean createDeadLink;

    /** The backup of relation property type. The element type is RelationPropertyType. */
    protected Stack<TnRelationPropertyType> relationPropertyTypeBackup;

    /** The backup of base suffix. The element type is String. */
    protected Stack<String> baseSuffixBackup;

    /** The backup of base suffix. The element type is String. */
    protected Stack<String> relationSuffixBackup;
    
    /** The map of select index. (Nullable) */
    protected Map<String, Integer> _selectIndexMap;

    // ===================================================================================
    //                                                                            Behavior
    //                                                                            ========
    // -----------------------------------------------------
    //                                                   row
    //                                                   ---
    public boolean hasRowInstance() {
        return row != null;
    }

    public void clearRowInstance() {
        row = null;
    }

    // -----------------------------------------------------
    //                                  relationPropertyType
    //                                  --------------------
    public TnBeanMetaData getRelationBeanMetaData() {
        return relationPropertyType.getBeanMetaData();
    }

    public boolean hasNextRelationProperty() {
        return getRelationBeanMetaData().getRelationPropertyTypeSize() > 0;
    }

    public void backupRelationPropertyType() {
        getOrCreateRelationPropertyTypeBackup().push(getRelationPropertyType());
    }

    public void restoreRelationPropertyType() {
        setRelationPropertyType((TnRelationPropertyType) getOrCreateRelationPropertyTypeBackup().pop());
    }

    protected Stack<TnRelationPropertyType> getOrCreateRelationPropertyTypeBackup() {
        if (relationPropertyTypeBackup == null) {
            relationPropertyTypeBackup = new Stack<TnRelationPropertyType>();
        }
        return relationPropertyTypeBackup;
    }

    // -----------------------------------------------------
    //                                           columnNames
    //                                           -----------
    public boolean containsColumnName(String columnName) {
        return columnNames.contains(columnName);
    }

    // -----------------------------------------------------
    //                                          relKeyValues
    //                                          ------------
    public boolean existsRelKeyValues() {
        return relKeyValues != null;
    }

    public boolean containsRelKeyValue(String key) {
        return relKeyValues.containsKey(key);
    }

    public boolean containsRelKeyValueIfExists(String key) {
        return existsRelKeyValues() && relKeyValues.containsKey(key);
    }

    public Object extractRelKeyValue(String key) {
        return relKeyValues.get(key);
    }

    // -----------------------------------------------------
    //                                 relationPropertyCache
    //                                 ---------------------
    // The type of relationPropertyCache is Map<String(relationNoSuffix), Map<String(columnName), PropertyType>>.
    public void initializePropertyCacheElement() {
        relationPropertyCache.put(relationNoSuffix, new HashMap<String, TnPropertyType>());
    }

    public boolean hasPropertyCacheElement() {
        final Map<String, TnPropertyType> propertyCacheElement = extractPropertyCacheElement();
        return propertyCacheElement != null && !propertyCacheElement.isEmpty();
    }

    public Map<String, TnPropertyType> extractPropertyCacheElement() {
        return relationPropertyCache.get(relationNoSuffix);
    }

    public void savePropertyCacheElement() {
        if (!hasPropertyCacheElement()) {
            initializePropertyCacheElement();
        }
        final Map<String, TnPropertyType> propertyCacheElement = extractPropertyCacheElement();
        final String columnName = buildRelationColumnName();
        if (propertyCacheElement.containsKey(columnName)) {
            return;
        }
        propertyCacheElement.put(columnName, currentPropertyType);
    }

    // -----------------------------------------------------
    //                                                suffix
    //                                                ------
    public String buildRelationColumnName() {
        return currentPropertyType.getColumnName() + relationNoSuffix;
    }

    public void addRelationNoSuffix(String additionalRelationNoSuffix) {
        relationNoSuffix = relationNoSuffix + additionalRelationNoSuffix;
    }

    public void backupSuffixAndPrepare(String baseSuffix, String additionalRelationNoSuffix) {
        backupBaseSuffix();
        backupRelationNoSuffix();
        this.baseSuffix = baseSuffix;
        addRelationNoSuffix(additionalRelationNoSuffix);
    }

    public void restoreSuffix() {
        restoreBaseSuffix();
        restoreRelationNoSuffix();
    }

    protected void backupBaseSuffix() {
        getOrCreateBaseSuffixBackup().push(getBaseSuffix());
    }

    protected void restoreBaseSuffix() {
        setBaseSuffix((String) getOrCreateBaseSuffixBackup().pop());
    }

    protected Stack<String> getOrCreateBaseSuffixBackup() {
        if (baseSuffixBackup == null) {
            baseSuffixBackup = new Stack<String>();
        }
        return baseSuffixBackup;
    }

    protected void backupRelationNoSuffix() {
        getOrCreateRelationNoSuffixBackup().push(getRelationNoSuffix());
    }

    protected void restoreRelationNoSuffix() {
        setRelationNoSuffix((String) getOrCreateRelationNoSuffixBackup().pop());
    }

    protected Stack<String> getOrCreateRelationNoSuffixBackup() {
        if (relationSuffixBackup == null) {
            relationSuffixBackup = new Stack<String>();
        }
        return relationSuffixBackup;
    }

    // -----------------------------------------------------
    //                                     relationNestLevel
    //                                     -----------------
    public boolean hasNextRelationLevel() {
        return currentRelationNestLevel < limitRelationNestLevel;
    }

    public void incrementCurrentRelationNestLevel() {
        ++currentRelationNestLevel;
    }

    public void decrementCurrentRelationNestLevel() {
        --currentRelationNestLevel;
    }

    // -----------------------------------------------------
    //                                       validValueCount
    //                                       ---------------
    public void incrementValidValueCount() {
        ++validValueCount;
    }

    public void clearValidValueCount() {
        validValueCount = 0;
    }

    public boolean hasValidValueCount() {
        return validValueCount > 0;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public Set<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(Set<String> columnNames) {
        this.columnNames = columnNames;
    }

    public Map<String, Object> getRelKeyValues() {
        return this.relKeyValues;
    }

    public void setRelKeyValues(Map<String, Object> relKeyValues) {
        this.relKeyValues = relKeyValues;
    }

    public Object getRow() {
        return row;
    }

    public void setRow(Object row) {
        this.row = row;
    }

    public TnRelationPropertyType getRelationPropertyType() {
        return relationPropertyType;
    }

    public void setRelationPropertyType(TnRelationPropertyType rpt) {
        this.relationPropertyType = rpt;
    }

    public Map<String, Map<String, TnPropertyType>> getRelationPropertyCache() {
        return relationPropertyCache;
    }

    public void setRelationPropertyCache(Map<String, Map<String, TnPropertyType>> relationPropertyCache) {
        this.relationPropertyCache = relationPropertyCache;
    }

    public String getBaseSuffix() {
        return baseSuffix;
    }

    public void setBaseSuffix(String baseSuffix) {
        this.baseSuffix = baseSuffix;
    }

    public String getRelationNoSuffix() {
        return relationNoSuffix;
    }

    public void setRelationNoSuffix(String relationNoSuffix) {
        this.relationNoSuffix = relationNoSuffix;
    }

    public int getLimitRelationNestLevel() {
        return limitRelationNestLevel;
    }

    public void setLimitRelationNestLevel(int limitRelationNestLevel) {
        this.limitRelationNestLevel = limitRelationNestLevel;
    }

    public int getCurrentRelationNestLevel() {
        return currentRelationNestLevel;
    }

    public void setCurrentRelationNestLevel(int currentRelationNestLevel) {
        this.currentRelationNestLevel = currentRelationNestLevel;
    }

    public TnPropertyType getCurrentPropertyType() {
        return currentPropertyType;
    }

    public void setCurrentPropertyType(TnPropertyType propertyType) {
        this.currentPropertyType = propertyType;
    }

    public boolean isCreateDeadLink() {
        return createDeadLink;
    }

    public void setCreateDeadLink(boolean createDeadLink) {
        this.createDeadLink = createDeadLink;
    }

    public Map<String, Integer> getSelectIndexMap() {
        return _selectIndexMap;
    }

    public void setSelectIndexMap(Map<String, Integer> selectIndexMap) {
        _selectIndexMap = selectIndexMap;
    }
}
