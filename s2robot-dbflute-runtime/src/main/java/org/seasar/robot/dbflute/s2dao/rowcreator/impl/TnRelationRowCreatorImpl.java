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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.seasar.robot.dbflute.s2dao.rowcreator.TnRelationRowCreator;
import org.seasar.robot.dbflute.util.DfReflectionUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public abstract class TnRelationRowCreatorImpl implements TnRelationRowCreator {

    // ===================================================================================
    //                                                                        Row Creation
    //                                                                        ============
    /**
     * @param rs Result set. (NotNull)
     * @param rpt The type of relation property. (NotNull)
     * @param columnNames The set of column name. (NotNull)
     * @param relKeyValues The map of relation key values. (Nullable)
     * @param relationPropertyCache The map of relation property cache. Map{String(relationNoSuffix), Map{String(columnName), PropertyType}} (NotNull)
     * @return Created relation row. (Nullable)
     * @throws SQLException
     */
    public Object createRelationRow(ResultSet rs, TnRelationPropertyType rpt, Set<String> columnNames,
            Map<String, Object> relKeyValues, Map<String, Map<String, TnPropertyType>> relationPropertyCache)
            throws SQLException {
        // - - - - - - - 
        // Entry Point!
        // - - - - - - -
        final TnRelationRowCreationResource res = createResourceForRow(rs, rpt, columnNames, relKeyValues,
                relationPropertyCache);
        return createRelationRow(res);
    }

    protected abstract TnRelationRowCreationResource createResourceForRow(ResultSet rs, TnRelationPropertyType rpt,
            Set<String> columnNames, Map<String, Object> relKeyValues,
            Map<String, Map<String, TnPropertyType>> relationPropertyCache) throws SQLException;

    /**
     * @param res The resource of relation row creation. (NotNull)
     * @return Created relation row. (Nullable)
     * @throws SQLException
     */
    protected Object createRelationRow(TnRelationRowCreationResource res) throws SQLException {
        // - - - - - - - - - - - 
        // Recursive Call Point!
        // - - - - - - - - - - -
        if (!res.hasPropertyCacheElement()) {
            return null;
        }
        setupRelationKeyValue(res);
        setupRelationAllValue(res);
        return res.getRow();
    }

    protected void setupRelationKeyValue(TnRelationRowCreationResource res) {
        final TnRelationPropertyType rpt = res.getRelationPropertyType();
        final TnBeanMetaData bmd = rpt.getBeanMetaData();
        for (int i = 0; i < rpt.getKeySize(); ++i) {
            final String columnName = rpt.getMyKey(i) + res.getBaseSuffix();

            if (!res.containsColumnName(columnName)) {
                continue;
            }
            if (!res.hasRowInstance()) {
                res.setRow(newRelationRow(rpt));
            }
            if (!res.containsRelKeyValueIfExists(columnName)) {
                continue;
            }
            final Object value = res.extractRelKeyValue(columnName);
            if (value == null) {
                continue;
            }

            final String yourKey = rpt.getYourKey(i);
            final TnPropertyType pt = bmd.getPropertyTypeByColumnName(yourKey);
            final DfPropertyDesc pd = pt.getPropertyDesc();
            pd.setValue(res.getRow(), value);
            continue;
        }
    }

    protected void setupRelationAllValue(TnRelationRowCreationResource res) throws SQLException {
        final Map<String, TnPropertyType> propertyCacheElement = res.extractPropertyCacheElement();
        final Set<Entry<String, TnPropertyType>> entrySet = propertyCacheElement.entrySet();
        for (Entry<String, TnPropertyType> entry : entrySet) {
            final TnPropertyType pt = entry.getValue();
            res.setCurrentPropertyType(pt);
            if (!isValidRelationPerPropertyLoop(res)) {
                res.clearRowInstance();
                return;
            }
            setupRelationProperty(res);
        }
        if (!isValidRelationAfterPropertyLoop(res)) {
            res.clearRowInstance();
            return;
        }
        res.clearValidValueCount();
        if (res.hasNextRelationProperty() && res.hasNextRelationLevel()) {
            setupNextRelationRow(res);
        }
    }

    protected boolean isValidRelationPerPropertyLoop(TnRelationRowCreationResource res) throws SQLException {
        return true;// Always true as default. This method is for extension(for override).
    }

    protected boolean isValidRelationAfterPropertyLoop(TnRelationRowCreationResource res) throws SQLException {
        if (res.isCreateDeadLink()) {
            return true;
        }
        return res.hasValidValueCount();
    }

    protected void setupRelationProperty(TnRelationRowCreationResource res) throws SQLException {
        final String columnName = res.buildRelationColumnName();
        if (!res.hasRowInstance()) {
            res.setRow(newRelationRow(res));
        }
        registerRelationValue(res, columnName);
    }

    protected abstract void registerRelationValue(TnRelationRowCreationResource res, String columnName)
            throws SQLException;

    protected void registerRelationValidValue(TnRelationRowCreationResource res, TnPropertyType pt, Object value)
            throws SQLException {
        res.incrementValidValueCount();
        final DfPropertyDesc pd = pt.getPropertyDesc();
        pd.setValue(res.getRow(), value);
    }

    // -----------------------------------------------------
    //                                         Next Relation
    //                                         -------------
    protected void setupNextRelationRow(TnRelationRowCreationResource res) throws SQLException {
        final TnBeanMetaData nextBmd = res.getRelationBeanMetaData();
        final Object row = res.getRow();
        res.backupRelationPropertyType();
        res.incrementCurrentRelationNestLevel();
        try {
            for (int i = 0; i < nextBmd.getRelationPropertyTypeSize(); ++i) {
                final TnRelationPropertyType nextRpt = nextBmd.getRelationPropertyType(i);
                setupNextRelationRowElement(res, row, nextRpt);
            }
        } finally {
            res.setRow(row);
            res.restoreRelationPropertyType();
            res.decrementCurrentRelationNestLevel();
        }
    }

    protected void setupNextRelationRowElement(TnRelationRowCreationResource res, Object row,
            TnRelationPropertyType nextRpt) throws SQLException {
        if (nextRpt == null) {
            return;
        }
        res.clearRowInstance();
        res.setRelationPropertyType(nextRpt);

        final String baseSuffix = res.getRelationNoSuffix();
        final String additionalRelationNoSuffix = buildRelationNoSuffix(nextRpt);
        res.backupSuffixAndPrepare(baseSuffix, additionalRelationNoSuffix);
        try {
            final Object relationRow = createRelationRow(res);
            if (relationRow != null) {
                nextRpt.getPropertyDesc().setValue(row, relationRow);
            }
        } finally {
            res.restoreSuffix();
        }
    }

    // ===================================================================================
    //                                                             Property Cache Creation
    //                                                             =======================
    /**
     * @param columnNames The set of column name. (NotNull)
     * @param bmd Bean meta data of base object. (NotNull)
     * @return The map of relation property cache. Map{String(relationNoSuffix), Map{String(columnName), PropertyType}} (NotNull)
     * @throws SQLException
     */
    public Map<String, Map<String, TnPropertyType>> createPropertyCache(Set<String> columnNames, TnBeanMetaData bmd)
            throws SQLException {
        // - - - - - - - 
        // Entry Point!
        // - - - - - - -
        final Map<String, Map<String, TnPropertyType>> relationPropertyCache = newRelationPropertyCache();
        for (int i = 0; i < bmd.getRelationPropertyTypeSize(); ++i) {
            final TnRelationPropertyType rpt = bmd.getRelationPropertyType(i);
            final String baseSuffix = "";
            final String relationNoSuffix = buildRelationNoSuffix(rpt);
            final TnRelationRowCreationResource res = createResourceForPropertyCache(rpt, columnNames,
                    relationPropertyCache, baseSuffix, relationNoSuffix, getLimitRelationNestLevel());
            if (rpt == null) {
                continue;
            }
            setupPropertyCache(res);
        }
        return relationPropertyCache;
    }

    protected abstract TnRelationRowCreationResource createResourceForPropertyCache(TnRelationPropertyType rpt,
            Set<String> columnNames, Map<String, Map<String, TnPropertyType>> relationPropertyCache, String baseSuffix,
            String relationNoSuffix, int limitRelationNestLevel) throws SQLException;

    protected void setupPropertyCache(TnRelationRowCreationResource res) throws SQLException {
        // - - - - - - - - - - - 
        // Recursive Call Point!
        // - - - - - - - - - - -
        res.initializePropertyCacheElement();

        // Check whether the relation is target or not.
        if (!isTargetRelation(res)) {
            return;
        }

        // Set up property cache about current beanMetaData.
        final TnBeanMetaData nextBmd = res.getRelationBeanMetaData();
        final Map<String, TnPropertyType> propertyTypeMap = nextBmd.getPropertyTypeMap();
        final Set<Entry<String, TnPropertyType>> entrySet = propertyTypeMap.entrySet();
        for (Entry<String, TnPropertyType> entry : entrySet) {
            final TnPropertyType pt = entry.getValue();
            res.setCurrentPropertyType(pt);
            if (!isTargetProperty(res)) {
                continue;
            }
            setupPropertyCacheElement(res);
        }

        // Set up next relation.
        if (res.hasNextRelationProperty() && res.hasNextRelationLevel()) {
            res.backupRelationPropertyType();
            res.incrementCurrentRelationNestLevel();
            try {
                setupNextPropertyCache(res, nextBmd);
            } finally {
                res.restoreRelationPropertyType();
                res.decrementCurrentRelationNestLevel();
            }
        }
    }

    protected void setupPropertyCacheElement(TnRelationRowCreationResource res) throws SQLException {
        final String columnName = res.buildRelationColumnName();
        if (!res.containsColumnName(columnName)) {
            return;
        }
        res.savePropertyCacheElement();
    }

    // -----------------------------------------------------
    //                                         Next Relation
    //                                         -------------
    protected void setupNextPropertyCache(TnRelationRowCreationResource res, TnBeanMetaData nextBmd)
            throws SQLException {
        for (int i = 0; i < nextBmd.getRelationPropertyTypeSize(); ++i) {
            final TnRelationPropertyType nextNextRpt = nextBmd.getRelationPropertyType(i);
            res.setRelationPropertyType(nextNextRpt);
            setupNextPropertyCacheElement(res, nextNextRpt);
        }
    }

    protected void setupNextPropertyCacheElement(TnRelationRowCreationResource res, TnRelationPropertyType nextNextRpt)
            throws SQLException {
        final String baseSuffix = res.getRelationNoSuffix();
        final String additionalRelationNoSuffix = buildRelationNoSuffix(nextNextRpt);
        res.backupSuffixAndPrepare(baseSuffix, additionalRelationNoSuffix);
        try {
            setupPropertyCache(res);// Recursive call!
        } finally {
            res.restoreSuffix();
        }
    }

    // -----------------------------------------------------
    //                                                Common
    //                                                ------
    protected Map<String, Map<String, TnPropertyType>> newRelationPropertyCache() {
        return new HashMap<String, Map<String, TnPropertyType>>();
    }

    // ===================================================================================
    //                                                                        Common Logic
    //                                                                        ============
    protected String buildRelationNoSuffix(TnRelationPropertyType rpt) {
        return "_" + rpt.getRelationNo();
    }

    protected Object newRelationRow(TnRelationRowCreationResource res) {
        return newRelationRow(res.getRelationPropertyType());
    }

    protected Object newRelationRow(TnRelationPropertyType rpt) {
        return DfReflectionUtil.newInstance(rpt.getPropertyDesc().getPropertyType());
    }

    // ===================================================================================
    //                                                                     Extension Point
    //                                                                     ===============
    protected boolean isTargetRelation(TnRelationRowCreationResource res) throws SQLException {
        return true;
    }

    protected boolean isTargetProperty(TnRelationRowCreationResource res) throws SQLException {
        final TnPropertyType pt = res.getCurrentPropertyType();
        return pt.getPropertyDesc().isWritable();
    }

    protected boolean isCreateDeadLink() {
        return true;
    }

    protected int getLimitRelationNestLevel() {
        return 1;
    }
}
