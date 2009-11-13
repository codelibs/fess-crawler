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
package org.seasar.robot.dbflute.s2dao.rowcreator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnDtoMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public interface TnRowCreator {

    /**
     * @param rs Result set. (NotNull)
     * @param columnPropertyTypeMap The map of row property cache. The key is String(columnName) and the value is PropertyType. (NotNull)
     * @param beanClass Bean class. (NotNull)
     * @return Created row. (NotNull)
     * @throws SQLException
     */
    Object createRow(ResultSet rs, Map<String, TnPropertyType> columnPropertyTypeMap, Class<?> beanClass)
            throws SQLException;

    /**
     * @param columnNames The set of column name. (NotNull)
     * @param beanMetaData Bean meta data. (NotNull)
     * @return The map of row property cache. The key is String(columnName) and the value is PropertyType. (NotNull)
     * @throws SQLException
     */
    Map<String, TnPropertyType> createPropertyCache(Set<String> columnNames, TnBeanMetaData beanMetaData)
            throws SQLException;

    /**
     * @param columnNames The set of column name. (NotNull)
     * @param dtoMetaData Dto meta data. (NotNull)
     * @return The map of property cache. Map{String(columnName), PropertyType} (NotNull)
     * @throws SQLException
     */
    Map<String, TnPropertyType> createPropertyCache(Set<String> columnNames, TnDtoMetaData dtoMetaData)
            throws SQLException;
}
