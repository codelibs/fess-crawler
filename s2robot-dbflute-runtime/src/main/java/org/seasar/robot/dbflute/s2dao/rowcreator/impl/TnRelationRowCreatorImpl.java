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
package org.seasar.robot.dbflute.s2dao.rowcreator.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.seasar.robot.dbflute.helper.StringKeyMap;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyMapping;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.seasar.robot.dbflute.s2dao.rowcreator.TnRelationRowCreator;
import org.seasar.robot.dbflute.util.DfReflectionUtil;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnRelationRowCreatorImpl implements TnRelationRowCreator {

    // ===================================================================================
    //                                                                        Row Creation
    //                                                                        ============
    /**
     * {@inheritDoc}
     */
    public Object createRelationRow(ResultSet rs, TnRelationPropertyType rpt, Map<String, String> selectColumnMap,
            Map<String, Object> relKeyValues, Map<String, Map<String, TnPropertyMapping>> relationPropertyCache)
            throws SQLException {
        // - - - - - - - 
        // Entry Point!
        // - - - - - - -
        final TnRelationRowCreationResource res = createResourceForRow(rs, rpt, selectColumnMap, relKeyValues,
                relationPropertyCache);
        return createRelationRow(res);
    }

    protected abstract TnRelationRowCreationResource createResourceForRow(ResultSet rs, TnRelationPropertyType rpt,
            Map<String, String> selectColumnMap, Map<String, Object> relKeyValues,
            Map<String, Map<String, TnPropertyMapping>> relationPropertyCache) throws SQLException;

    /**
     * @param res The resource of relation row creation. (NotNull)
     * @return Created relation row. (NullAllowed)
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
        final TnBeanMetaData relBmd = rpt.getBeanMetaData();
        for (int i = 0; i < rpt.getKeySize(); ++i) {
            final String columnName = rpt.getMyKey(i) + res.getBaseSuffix();

            if (!res.containsSelectColumn(columnName)) {
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
            final TnPropertyType pt = relBmd.getPropertyTypeByColumnName(yourKey);
            final DfPropertyDesc pd = pt.getPropertyDesc();
            pd.setValue(res.getRow(), value);
        }
    }

    protected abstract void setupRelationAllValue(TnRelationRowCreationResource res) throws SQLException;

    protected boolean isValidRelationPerPropertyLoop(TnRelationRowCreationResource res) throws SQLException {
        return true; // always true as default (this is for override)
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
     * {@inheritDoc}
     */
    public Map<String, Map<String, TnPropertyMapping>> createPropertyCache(Map<String, String> selectColumnMap,
            TnBeanMetaData bmd) throws SQLException {
        // - - - - - - - 
        // Entry Point!
        // - - - - - - -
        final Map<String, Map<String, TnPropertyMapping>> relationPropertyCache = newRelationPropertyCache();
        for (int i = 0; i < bmd.getRelationPropertyTypeSize(); ++i) {
            final TnRelationPropertyType rpt = bmd.getRelationPropertyType(i);
            final String baseSuffix = "";
            final String relationNoSuffix = buildRelationNoSuffix(rpt);
            final TnRelationRowCreationResource res = createResourceForPropertyCache(rpt, selectColumnMap,
                    relationPropertyCache, baseSuffix, relationNoSuffix, getLimitRelationNestLevel());
            if (rpt == null) {
                continue;
            }
            setupPropertyCache(res);
        }
        return relationPropertyCache;
    }

    protected abstract TnRelationRowCreationResource createResourceForPropertyCache(TnRelationPropertyType rpt,
            Map<String, String> selectColumnMap, Map<String, Map<String, TnPropertyMapping>> relationPropertyCache,
            String baseSuffix, String relationNoSuffix, int limitRelationNestLevel) throws SQLException;

    protected abstract void setupPropertyCache(TnRelationRowCreationResource res) throws SQLException;

    protected void setupPropertyCacheElement(TnRelationRowCreationResource res) throws SQLException {
        final String columnName = res.buildRelationColumnName();
        if (!res.containsSelectColumn(columnName)) {
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
    protected Map<String, Map<String, TnPropertyMapping>> newRelationPropertyCache() {
        return StringKeyMap.createAsCaseInsensitive();
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

    protected abstract boolean isCreateDeadLink();

    protected abstract int getLimitRelationNestLevel();
}
