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
import java.util.Map;
import java.util.Set;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyMapping;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.seasar.robot.dbflute.s2dao.rowcreator.TnRelationRowCreator;
import org.seasar.robot.dbflute.s2dao.rowcreator.TnRowCreator;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnAbstractBeanResultSetHandler implements TnResultSetHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private TnBeanMetaData beanMetaData;
    protected TnRowCreator rowCreator;
    protected TnRelationRowCreator relationRowCreator;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param beanMetaData Bean meta data. (NotNull)
     * @param rowCreator Row creator. (NotNull)
     * @param relationRowCreator Relation row creator. (NotNul)
     */
    public TnAbstractBeanResultSetHandler(TnBeanMetaData beanMetaData, TnRowCreator rowCreator,
            TnRelationRowCreator relationRowCreator) {
        this.beanMetaData = beanMetaData;
        this.rowCreator = rowCreator;
        this.relationRowCreator = relationRowCreator;
    }

    // ===================================================================================
    //                                                                      Property Cache
    //                                                                      ==============
    /**
     * @param selectColumnSet The name set of select column. (NotNull)
     * @return The map of row property cache. Map{String(columnName), PropertyMapping} (NotNull)
     * @throws SQLException
     */
    protected Map<String, TnPropertyMapping> createPropertyCache(Map<String, String> selectColumnSet)
            throws SQLException {
        // - - - - - - - - -
        // Override for Bean
        // - - - - - - - - -
        return rowCreator.createPropertyCache(selectColumnSet, beanMetaData);
    }

    // ===================================================================================
    //                                                                          Create Row
    //                                                                          ==========
    /**
     * @param rs Result set. (NotNull)
     * @param propertyCache The map of property cache. Map{String(columnName), PropertyMapping} (NotNull)
     * @return Created row. (NotNull)
     * @throws SQLException
     */
    protected Object createRow(ResultSet rs, Map<String, TnPropertyMapping> propertyCache) throws SQLException {
        // - - - - - - - - -
        // Override for Bean
        // - - - - - - - - -
        final Class<?> beanClass = beanMetaData.getBeanClass();
        return rowCreator.createRow(rs, propertyCache, beanClass);
    }

    /**
     * @param selectColumnMap The name map of select column. map:{flexibleName = columnDbName} (NotNull)
     * @return The map of relation property cache. map:{relationNoSuffix = map:{columnName = PropertyMapping}} (NotNull)
     * @throws SQLException
     */
    protected Map<String, Map<String, TnPropertyMapping>> createRelationPropertyCache(
            Map<String, String> selectColumnMap) throws SQLException {
        return relationRowCreator.createPropertyCache(selectColumnMap, beanMetaData);
    }

    /**
     * @param rs Result set. (NotNull)
     * @param rpt The type of relation property. (NotNull)
     * @param selectColumnMap The name map of select column. map:{flexibleName = columnDbName} (NotNull)
     * @param relKeyValues The map of relation key values. The key is relation column name. (NullAllowed)
     * @param relationPropertyCache The map of relation property cache. map:{relationNoSuffix = map:{columnName = PropertyMapping}} (NotNull)
     * @return Created relation row. (NullAllowed)
     * @throws SQLException
     */
    protected Object createRelationRow(ResultSet rs, TnRelationPropertyType rpt, Map<String, String> selectColumnMap,
            Map<String, Object> relKeyValues, Map<String, Map<String, TnPropertyMapping>> relationPropertyCache)
            throws SQLException {
        return relationRowCreator.createRelationRow(rs, rpt, selectColumnMap, relKeyValues, relationPropertyCache);
    }

    /**
     * @param row The row of result list. (NotNull)
     */
    protected void postCreateRow(final Object row) {
        if (row instanceof Entity) { // DBFlute target
            ((Entity) row).clearModifiedInfo();
        } else { // basically unreachable
            final TnBeanMetaData bmd = getBeanMetaData();
            final Set<String> names = bmd.getModifiedPropertyNames(row);
            names.clear();
        }
    }

    // ===================================================================================
    //                                                                       Select Column
    //                                                                       =============
    protected Map<String, String> createSelectColumnMap(ResultSet rs) throws SQLException {
        return ResourceContext.createSelectColumnMap(rs);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public TnBeanMetaData getBeanMetaData() {
        return beanMetaData;
    }
}
