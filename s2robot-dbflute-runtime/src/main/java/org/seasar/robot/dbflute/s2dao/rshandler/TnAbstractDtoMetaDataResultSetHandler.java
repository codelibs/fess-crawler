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
import java.util.Map;
import java.util.Set;

import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.robot.dbflute.s2dao.metadata.TnDtoMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.rowcreator.TnRowCreator;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
@SuppressWarnings("unchecked")
public abstract class TnAbstractDtoMetaDataResultSetHandler implements TnResultSetHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private TnDtoMetaData dtoMetaData;
    protected TnRowCreator rowCreator; // [DAO-118] (2007/08/25)

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param dtoMetaData Dto meta data. (NotNull)
     * @param rowCreator Row creator. (NotNull)
     */
    public TnAbstractDtoMetaDataResultSetHandler(TnDtoMetaData dtoMetaData, TnRowCreator rowCreator) {
        this.dtoMetaData = dtoMetaData;
        this.rowCreator = rowCreator;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    /**
     * @param columnNames The set of column name. (NotNull)
     * @return The map of row property cache. Map{String(columnName), PropertyType} (NotNull)
     * @throws SQLException
     */
    protected Map<String, TnPropertyType> createPropertyCache(Set<String> columnNames) throws SQLException {
        return rowCreator.createPropertyCache(columnNames, dtoMetaData);
    }

    /**
     * @param rs Result set. (NotNull)
     * @param propertyCache The map of property cache. Map{String(columnName), PropertyType} (NotNull)
     * @return Created row. (NotNull)
     * @throws SQLException
     */
    protected Object createRow(ResultSet rs, Map<String, TnPropertyType> propertyCache) throws SQLException {
        final Class beanClass = dtoMetaData.getBeanClass();
        return rowCreator.createRow(rs, propertyCache, beanClass);
    }

    protected Set<String> createColumnNames(ResultSet rs) throws SQLException {
        return ResourceContext.createSelectColumnNames(rs);
    }

    public TnDtoMetaData getDtoMetaData() {
        return dtoMetaData;
    }
}
