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
package org.seasar.robot.dbflute.s2dao.rshandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.seasar.robot.dbflute.s2dao.rowcreator.TnRelationRowCreator;
import org.seasar.robot.dbflute.s2dao.rowcreator.TnRowCreator;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnBeanListMetaDataResultSetHandler extends TnAbstractBeanMetaDataResultSetHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param beanMetaData Bean meta data. (NotNull)
     * @param rowCreator Row creator. (NotNull)
     * @param relationRowCreator Relation row creator. (NotNul)
     */
    public TnBeanListMetaDataResultSetHandler(TnBeanMetaData beanMetaData, TnRowCreator rowCreator,
            TnRelationRowCreator relationRowCreator) {
        super(beanMetaData, rowCreator, relationRowCreator);
    }

    // ===================================================================================
    //                                                                              Handle
    //                                                                              ======
    public Object handle(ResultSet rs) throws SQLException {
        final List<Object> list = new ArrayList<Object>();
        mappingBean(rs, new BeanRowHandler() {
            public void handle(Object row) throws SQLException {
                list.add(row);
            }
        });
        return list;
    }

    // ===================================================================================
    //                                                                             Mapping
    //                                                                             =======
    protected static interface BeanRowHandler {
        void handle(Object row) throws SQLException;
    }

    protected void mappingBean(ResultSet rs, BeanRowHandler handler) throws SQLException {
        // Lazy initialization because if the result is zero, the resources are unused.
        Set<String> columnNames = null; // Set<String(columnName)>
        Map<String, TnPropertyType> propertyCache = null; // Map<String(columnName), PropertyType>
        Map<String, Map<String, TnPropertyType>> relationPropertyCache = null; // Map<String(relationNoSuffix), Map<String(columnName), PropertyType>>
        TnRelationRowCache relRowCache = null;

        final int relSize = getBeanMetaData().getRelationPropertyTypeSize();
        final boolean hasCB = hasConditionBean();
        final boolean skipRelationLoop;
        {
            final boolean emptyRelation = isSelectedForeignInfoEmpty();
            final boolean hasOSC = hasOutsideSqlContext();
            final boolean specifiedOutsideSql = isSpecifiedOutsideSql();

            // If it has condition-bean that has no relation to get
            // or it has outside SQL context that is specified-outside-sql,
            // they are unnecessary to do relation loop!
            skipRelationLoop = (hasCB && emptyRelation) || (hasOSC && specifiedOutsideSql);
        }
        final Map<String, Integer> selectIndexMap = ResourceContext.getSelectIndexMap();

        while (rs.next()) {
            if (columnNames == null) {
                columnNames = createColumnNames(rs);
            }
            if (propertyCache == null) {
                propertyCache = createPropertyCache(columnNames);
            }

            // Create row instance of base table by row property cache.
            final Object row = createRow(rs, propertyCache);

            // If it has condition-bean that has no relation to get
            // or it has outside SQL context that is specified outside SQL,
            // they are unnecessary to do relation loop!
            if (skipRelationLoop) {
                postCreateRow(row);
                handler.handle(row);
                continue;
            }

            if (relationPropertyCache == null) {
                relationPropertyCache = createRelationPropertyCache(columnNames);
            }
            if (relRowCache == null) {
                relRowCache = createRelationRowCache(relSize);
            }
            for (int i = 0; i < relSize; ++i) {
                final TnRelationPropertyType rpt = getBeanMetaData().getRelationPropertyType(i);
                if (rpt == null) {
                    continue;
                }

                // Do only selected foreign property for performance if condition-bean exists.
                if (hasCB && !hasSelectedForeignInfo(buildRelationNoSuffix(rpt))) {
                    continue;
                }

                final Map<String, Object> relKeyValues = new HashMap<String, Object>();
                final TnRelationKey relKey = createRelationKey(rs, rpt, columnNames, relKeyValues, selectIndexMap);
                Object relationRow = null;
                if (relKey != null) {
                    relationRow = relRowCache.getRelationRow(i, relKey);
                    if (relationRow == null) { // when no cache
                        relationRow = createRelationRow(rs, rpt, columnNames, relKeyValues, relationPropertyCache);
                        if (relationRow != null) {
                            relRowCache.addRelationRow(i, relKey, relationRow);
                            postCreateRow(relationRow);
                        }
                    }
                }
                if (relationRow != null) {
                    final DfPropertyDesc pd = rpt.getPropertyDesc();
                    pd.setValue(row, relationRow);
                }
            }
            postCreateRow(row);
            handler.handle(row);
        }
    }

    /**
     * Create the cache of relation row.
     * @param relSize The size of relation.
     * @return The cache of relation row. (NotNull)
     */
    protected TnRelationRowCache createRelationRowCache(int relSize) {
        return new TnRelationRowCache(relSize);
    }

    /**
     * Create the key of relation.
     * @param rs The result set. (NotNull)
     * @param rpt The property type of relation. (NotNull)
     * @param columnNames The names of columns. (NotNull)
     * @param relKeyValues The values of relation keys. (NotNull)
     * @param selectIndexMap The map of select index. (Nullable: If it's null, it doesn't use select index.)
     * @return The key of relation. (NotNull)
     * @throws SQLException
     */
    protected TnRelationKey createRelationKey(ResultSet rs, TnRelationPropertyType rpt, Set<String> columnNames,
            Map<String, Object> relKeyValues, Map<String, Integer> selectIndexMap) throws SQLException {
        final List<Object> keyList = new ArrayList<Object>();
        final TnBeanMetaData bmd = rpt.getBeanMetaData();
        for (int i = 0; i < rpt.getKeySize(); ++i) {
            final ValueType valueType;
            String columnName = rpt.getMyKey(i);
            if (columnNames.contains(columnName)) {
                final TnPropertyType pt = getBeanMetaData().getPropertyTypeByColumnName(columnName);
                valueType = pt.getValueType();
            } else {
                final TnPropertyType pt = bmd.getPropertyTypeByColumnName(rpt.getYourKey(i));
                columnName = pt.getColumnName() + buildRelationNoSuffix(rpt);
                if (columnNames.contains(columnName)) {
                    valueType = pt.getValueType();
                } else {
                    return null;
                }
            }
            final Object value;
            if (selectIndexMap != null) {
                value = ResourceContext.getValue(rs, columnName, valueType, selectIndexMap);
            } else {
                value = valueType.getValue(rs, columnName);
            }
            if (value == null) {
                return null;
            }
            relKeyValues.put(columnName, value);
            keyList.add(value);
        }
        if (keyList.size() > 0) {
            Object[] keys = keyList.toArray();
            return new TnRelationKey(keys);
        } else {
            return null;
        }
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected boolean hasConditionBean() {
        return ConditionBeanContext.isExistConditionBeanOnThread();
    }

    protected boolean isSelectedForeignInfoEmpty() {
        if (!hasConditionBean()) {
            return true;
        }
        ConditionBean cb = ConditionBeanContext.getConditionBeanOnThread();
        if (cb.getSqlClause().isSelectedForeignInfoEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Has it selected foreign information?
     * You should call hasConditionBean() before calling this!
     * @param relationNoSuffix The suffix of relation NO. (NotNull)
     * @return Determination.
     */
    protected boolean hasSelectedForeignInfo(String relationNoSuffix) {
        final ConditionBean cb = ConditionBeanContext.getConditionBeanOnThread();
        if (cb.getSqlClause().hasSelectedForeignInfo(relationNoSuffix)) {
            return true;
        }
        return false;
    }

    /**
     * Build the string of relation No suffix.
     * @param rpt The property type of relation. (NotNull)
     * @return The string of relation No suffix. (NotNull)
     */
    protected String buildRelationNoSuffix(TnRelationPropertyType rpt) {
        return "_" + rpt.getRelationNo();
    }

    protected boolean hasOutsideSqlContext() {
        return OutsideSqlContext.isExistOutsideSqlContextOnThread();
    }

    protected boolean isSpecifiedOutsideSql() {
        if (!hasOutsideSqlContext()) {
            return false;
        }
        final OutsideSqlContext context = OutsideSqlContext.getOutsideSqlContextOnThread();
        return context.isSpecifiedOutsideSql();
    }
}
