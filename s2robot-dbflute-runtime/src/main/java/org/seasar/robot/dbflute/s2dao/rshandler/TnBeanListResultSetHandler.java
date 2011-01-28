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
package org.seasar.robot.dbflute.s2dao.rshandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyMapping;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.seasar.robot.dbflute.s2dao.rowcreator.TnRelationRowCreator;
import org.seasar.robot.dbflute.s2dao.rowcreator.TnRowCreator;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnBeanListResultSetHandler extends TnAbstractBeanResultSetHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param beanMetaData Bean meta data. (NotNull)
     * @param rowCreator Row creator. (NotNull)
     * @param relationRowCreator Relation row creator. (NotNul)
     */
    public TnBeanListResultSetHandler(TnBeanMetaData beanMetaData, TnRowCreator rowCreator,
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
        Map<String, String> selectColumnSet = null;
        Map<String, TnPropertyMapping> propertyCache = null;
        Map<String, Map<String, TnPropertyMapping>> relationPropertyCache = null; // key is relationNoSuffix, columnName
        TnRelationRowCache relRowCache = null;

        final int relSize = getBeanMetaData().getRelationPropertyTypeSize();
        final boolean hasCB = hasConditionBean();
        final boolean skipRelationLoop;
        {
            final boolean emptyRelation = isSelectedRelationEmpty();
            final boolean hasOSC = hasOutsideSqlContext();
            final boolean specifiedOutsideSql = isSpecifiedOutsideSql();

            // If it has condition-bean that has no relation to get
            // or it has outside SQL context that is specified-outside-sql,
            // they are unnecessary to do relation loop!
            skipRelationLoop = (hasCB && emptyRelation) || (hasOSC && specifiedOutsideSql);
        }
        final Map<String, Integer> selectIndexMap = ResourceContext.getSelectIndexMap();

        while (rs.next()) {
            if (selectColumnSet == null) {
                selectColumnSet = createSelectColumnMap(rs);
            }
            if (propertyCache == null) {
                propertyCache = createPropertyCache(selectColumnSet);
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
                relationPropertyCache = createRelationPropertyCache(selectColumnSet);
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
                if (hasCB && !hasSelectedRelation(buildRelationNoSuffix(rpt))) {
                    continue;
                }

                final Map<String, Object> relKeyValues = new HashMap<String, Object>();
                final TnRelationKey relKey = createRelationKey(rs, rpt, selectColumnSet, relKeyValues, selectIndexMap);
                Object relationRow = null;
                if (relKey != null) {
                    relationRow = relRowCache.getRelationRow(i, relKey);
                    if (relationRow == null) { // when no cache
                        relationRow = createRelationRow(rs, rpt, selectColumnSet, relKeyValues, relationPropertyCache);
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
     * @param selectColumnMap The name map of select column. {flexible-name = column-DB-name} (NotNull)
     * @param relKeyValues The values of relation keys. The key is relation column name. (NotNull)
     * @param selectIndexMap The map of select index. (NullAllowed: If it's null, it doesn't use select index.)
     * @return The key of relation. (NotNull)
     * @throws SQLException
     */
    protected TnRelationKey createRelationKey(ResultSet rs, TnRelationPropertyType rpt,
            Map<String, String> selectColumnMap, Map<String, Object> relKeyValues, Map<String, Integer> selectIndexMap)
            throws SQLException {
        final List<Object> keyList = new ArrayList<Object>();
        for (int i = 0; i < rpt.getKeySize(); ++i) {
            final TnPropertyType pt = rpt.getBeanMetaData().getPropertyTypeByColumnName(rpt.getYourKey(i));
            final String relationNoSuffix = buildRelationNoSuffix(rpt);
            final String columnName = pt.getColumnDbName() + relationNoSuffix;
            final ValueType valueType;
            if (selectColumnMap.containsKey(columnName)) {
                valueType = pt.getValueType();
            } else {
                // basically unreachable
                // because the referred column (basically PK or FK) must exist
                // if the relation's select clause is specified
                return null;
            }
            final Object value;
            if (selectIndexMap != null) {
                value = ResourceContext.getValue(rs, columnName, valueType, selectIndexMap);
            } else {
                value = valueType.getValue(rs, columnName);
            }
            if (value == null) {
                // reachable when the referred column data is null
                // (treated as no relation data)
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

    protected boolean isSelectedRelationEmpty() {
        if (!hasConditionBean()) {
            return true;
        }
        final ConditionBean cb = ConditionBeanContext.getConditionBeanOnThread();
        return cb.getSqlClause().isSelectedRelationEmpty();
    }

    /**
     * Does it have the relation as selected?
     * You should call hasConditionBean() before calling this!
     * @param relationNoSuffix The suffix of relation NO. (NotNull)
     * @return Determination.
     */
    protected boolean hasSelectedRelation(String relationNoSuffix) {
        final ConditionBean cb = ConditionBeanContext.getConditionBeanOnThread();
        return cb.getSqlClause().hasSelectedRelation(relationNoSuffix);
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
