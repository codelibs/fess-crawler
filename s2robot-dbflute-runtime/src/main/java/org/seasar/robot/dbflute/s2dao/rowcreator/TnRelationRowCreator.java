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
package org.seasar.robot.dbflute.s2dao.rowcreator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyMapping;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyType;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public interface TnRelationRowCreator {

    /**
     * @param rs Result set. (NotNull)
     * @param rpt The type of relation property. (NotNull)
     * @param selectColumnMap The name map of select column. map:{flexibleName = columnDbName} (NotNull)
     * @param relKeyValues The map of relation key values. The key is relation column name. (NullAllowed)
     * @param relationPropertyCache The map of relation property cache. map:{relationNoSuffix = map:{columnName = PropertyMapping}} (NotNull)
     * @return Created relation row. (NullAllowed)
     * @throws SQLException
     */
    Object createRelationRow(ResultSet rs, TnRelationPropertyType rpt, Map<String, String> selectColumnMap,
            Map<String, Object> relKeyValues, Map<String, Map<String, TnPropertyMapping>> relationPropertyCache)
            throws SQLException;

    /**
     * @param selectColumnMap The name map of select column. map:{flexibleName = columnDbName} (NotNull)
     * @param bmd Bean meta data of base object. (NotNull)
     * @return The map of relation property cache. map:{relationNoSuffix = map:{columnName = PropertyMapping}} (NotNull)
     * @throws SQLException
     */
    Map<String, Map<String, TnPropertyMapping>> createPropertyCache(Map<String, String> selectColumnMap,
            TnBeanMetaData bmd) throws SQLException;
}
