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
package org.seasar.robot.dbflute.s2dao.extension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.helper.beans.DfPropertyAccessor;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyMapping;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.seasar.robot.dbflute.s2dao.rowcreator.impl.TnRelationRowCreationResource;
import org.seasar.robot.dbflute.s2dao.rowcreator.impl.TnRelationRowCreatorImpl;

/**
 * @author jflute
 */
public class TnRelationRowCreatorExtension extends TnRelationRowCreatorImpl {

    public static TnRelationRowCreatorExtension createRelationRowCreator() {
        return new TnRelationRowCreatorExtension();
    }

    @Override
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

    @Override
    protected void setupRelationKeyValue(TnRelationRowCreationResource res) {
        final TnRelationPropertyType rpt = res.getRelationPropertyType();
        final TnBeanMetaData bmd = rpt.getBeanMetaData();
        final DBMeta dbmeta = findDBMeta(bmd.getBeanClass(), bmd.getTableName());
        for (int i = 0; i < rpt.getKeySize(); ++i) {
            final String columnName = rpt.getMyKey(i) + res.getBaseSuffix();

            if (!res.containsSelectColumn(columnName)) {
                continue;
            }
            if (!res.hasRowInstance()) {
                final Object row;
                if (dbmeta != null) {
                    row = dbmeta.newEntity();
                } else {
                    row = newRelationRow(rpt);
                }
                res.setRow(row);
            }
            if (!res.containsRelKeyValueIfExists(columnName)) {
                continue;
            }
            final Object value = res.extractRelKeyValue(columnName);
            if (value == null) {
                // basically no way
                // because this is not called if the referred value
                // is null (then it must be no relation key)
                // @see TnBeanListResultSetHandler
                continue;
            }

            final TnPropertyMapping mapping = bmd.getPropertyTypeByColumnName(rpt.getYourKey(i));
            setValue(res, mapping, dbmeta, value);
        }
    }

    protected Object createRelationRowInstance(DBMeta dbmeta) {
        if (dbmeta != null) {
            return dbmeta.newEntity();
        }
        return null;
    }

    protected DBMeta findDBMeta(Class<?> rowType, String tableName) {
        return TnRowCreatorExtension.findCachedDBMeta(rowType, tableName);
    }

    @Override
    protected void setupRelationAllValue(TnRelationRowCreationResource res) throws SQLException {
        final Map<String, TnPropertyMapping> propertyCacheElement = res.extractPropertyCacheElement();
        final Set<Entry<String, TnPropertyMapping>> entrySet = propertyCacheElement.entrySet();
        for (Entry<String, TnPropertyMapping> entry : entrySet) {
            final TnPropertyMapping pt = entry.getValue();
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
        if (res.hasNextRelationProperty() && (hasConditionBean(res) || res.hasNextRelationLevel())) {
            setupNextRelationRow(res);
        }
    }

    @Override
    protected void registerRelationValue(TnRelationRowCreationResource res, String columnName) throws SQLException {
        final TnPropertyMapping mapping = res.getCurrentPropertyMapping();
        Object value = null;
        if (res.containsRelKeyValueIfExists(columnName)) {
            // if this column is relation key, it gets the value from relation key values
            // for performance and avoiding twice getting same column value
            value = res.extractRelKeyValue(columnName);
        } else {
            final ValueType valueType = mapping.getValueType();
            final Map<String, Integer> selectIndexMap = res.getSelectIndexMap();
            if (selectIndexMap != null) {
                value = ResourceContext.getValue(res.getResultSet(), columnName, valueType, selectIndexMap);
            } else {
                value = valueType.getValue(res.getResultSet(), columnName);
            }
        }

        if (value != null) {
            res.incrementValidValueCount();
            final DBMeta dbmeta = findDBMeta(res.getRow());
            setValue(res, mapping, dbmeta, value);
        }
    }

    /**
     * @param row The instance of row. (NotNull)
     * @return The interface of DBMeta. (NullAllowed: If it's null, it means NotFound.)
     */
    protected DBMeta findDBMeta(Object row) {
        return TnRowCreatorExtension.findCachedDBMeta(row);
    }

    protected void setValue(TnRelationRowCreationResource res, TnPropertyMapping mapping, DBMeta dbmeta, Object value) {
        final String propertyName = mapping.getPropertyName();
        if (dbmeta != null && dbmeta.hasEntityPropertySetupper(propertyName)) {
            dbmeta.setupEntityProperty(propertyName, res.getRow(), value);
        } else {
            final DfPropertyAccessor accessor = mapping.getPropertyAccessor();
            accessor.setValue(res.getRow(), value);
        }
    }

    @Override
    protected void setupPropertyCache(TnRelationRowCreationResource res) throws SQLException {
        // - - - - - - - - - - - 
        // Recursive Call Point!
        // - - - - - - - - - - -
        res.initializePropertyCacheElement();

        // Do only selected foreign property for performance if condition-bean exists.
        if (hasConditionBean(res) && !hasSelectedForeignInfo(res)) {
            return;
        }

        // Set up property cache about current beanMetaData.
        final TnBeanMetaData nextBmd = res.getRelationBeanMetaData();
        final List<TnPropertyType> ptList = nextBmd.getPropertyTypeList();
        for (TnPropertyType pt : ptList) { // already been filtered as target only
            res.setCurrentPropertyType(pt);
            setupPropertyCacheElement(res);
        }

        // Set up next relation.
        if (res.hasNextRelationProperty() && (hasConditionBean(res) || res.hasNextRelationLevel())) {
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

    @Override
    protected boolean isCreateDeadLink() {
        return false;
    }

    @Override
    protected int getLimitRelationNestLevel() {
        // basically unused on DBFlute because only ConditionBean uses relation row,
        // and ConditionBean supports unlimited relation nest level.
        // So this limit size is always used after hasConditionBean().
        return 2; // for Compatible (old parameter)
    }

    @Override
    protected TnRelationRowCreationResource createResourceForRow(ResultSet rs, TnRelationPropertyType rpt,
            Map<String, String> selectColumnMap, Map<String, Object> relKeyValues,
            Map<String, Map<String, TnPropertyMapping>> relationPropertyCache) throws SQLException {
        final TnRelationRowCreationResource res = new TnRelationRowCreationResourceExtension();
        res.setResultSet(rs);
        res.setRelationPropertyType(rpt);
        res.setSelectColumnMap(selectColumnMap);
        res.setRelKeyValues(relKeyValues);
        res.setRelationPropertyCache(relationPropertyCache);
        res.setBaseSuffix("");// as Default
        res.setRelationNoSuffix(buildRelationNoSuffix(rpt));
        res.setLimitRelationNestLevel(getLimitRelationNestLevel());
        res.setCurrentRelationNestLevel(1);// as Default
        res.setCreateDeadLink(isCreateDeadLink());
        res.setSelectIndexMap(ResourceContext.getSelectIndexMap());
        return res;
    }

    @Override
    protected TnRelationRowCreationResource createResourceForPropertyCache(TnRelationPropertyType rpt,
            Map<String, String> selectColumnMap, Map<String, Map<String, TnPropertyMapping>> relationPropertyCache,
            String baseSuffix, String relationNoSuffix, int limitRelationNestLevel) throws SQLException {
        final TnRelationRowCreationResource res = new TnRelationRowCreationResourceExtension();
        res.setRelationPropertyType(rpt);
        res.setSelectColumnMap(selectColumnMap);
        res.setRelationPropertyCache(relationPropertyCache);
        res.setBaseSuffix(baseSuffix);
        res.setRelationNoSuffix(relationNoSuffix);
        res.setLimitRelationNestLevel(limitRelationNestLevel);
        res.setCurrentRelationNestLevel(1);// as Default
        res.setSelectIndexMap(ResourceContext.getSelectIndexMap());
        return res;
    }

    protected boolean isConditionBeanSelectedRelation(TnRelationRowCreationResource res) {
        if (hasConditionBean(res)) {
            final ConditionBean cb = ConditionBeanContext.getConditionBeanOnThread();
            if (cb.getSqlClause().hasSelectedRelation(res.getRelationNoSuffix())) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasConditionBean(TnRelationRowCreationResource res) {
        return ConditionBeanContext.isExistConditionBeanOnThread();
    }

    protected boolean hasSelectedForeignInfo(TnRelationRowCreationResource res) {
        final ConditionBean cb = ConditionBeanContext.getConditionBeanOnThread();
        if (cb.getSqlClause().hasSelectedRelation(res.getRelationNoSuffix())) {
            return true;
        }
        return false;
    }

    protected static class TnRelationRowCreationResourceExtension extends TnRelationRowCreationResource {
        protected Stack<TnRelationPropertyType> backupRelationPropertyType = new Stack<TnRelationPropertyType>();
        protected Stack<String> backupBaseSuffix = new Stack<String>();
        protected Stack<String> backupRelationSuffix = new Stack<String>();

        @Override
        public void backupRelationPropertyType() {
            backupRelationPropertyType.push(getRelationPropertyType());
        }

        @Override
        public void restoreRelationPropertyType() {
            setRelationPropertyType(backupRelationPropertyType.pop());
        }

        @Override
        public void backupSuffixAndPrepare(String baseSuffix, String additionalRelationNoSuffix) {
            backupBaseSuffixExtension();
            backupRelationNoSuffixExtension();
            setBaseSuffix(baseSuffix);
            addRelationNoSuffix(additionalRelationNoSuffix);
        }

        @Override
        public void restoreSuffix() {
            restoreBaseSuffixExtension();
            restoreRelationNoSuffixExtension();
        }

        protected void backupBaseSuffixExtension() {
            backupBaseSuffix.push(getBaseSuffix());
        }

        protected void restoreBaseSuffixExtension() {
            setBaseSuffix(backupBaseSuffix.pop());
        }

        protected void backupRelationNoSuffixExtension() {
            backupRelationSuffix.push(getRelationNoSuffix());
        }

        protected void restoreRelationNoSuffixExtension() {
            setRelationNoSuffix(backupRelationSuffix.pop());
        }
    }
}
