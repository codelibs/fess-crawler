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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.robot.dbflute.helper.StringKeyMap;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes;
import org.seasar.robot.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 */
public abstract class TnAbstractMapResultSetHandler implements TnResultSetHandler {

    protected Map<String, ValueType> createPropertyTypeMap(ResultSetMetaData rsmd) throws SQLException {
        final int count = rsmd.getColumnCount();
        final Map<String, ValueType> propertyTypeMap = DfCollectionUtil.newLinkedHashMap();
        for (int i = 0; i < count; ++i) {
            final String propertyName = rsmd.getColumnLabel(i + 1);

            // because it can only use by-JDBC-type value type here 
            final ValueType valueType = TnValueTypes.getValueType(rsmd.getColumnType(i + 1));

            propertyTypeMap.put(propertyName, valueType);
        }
        return propertyTypeMap;
    }

    protected Map<String, Object> createRow(ResultSet rs, Map<String, ValueType> propertyTypeMap) throws SQLException {
        final Map<String, Object> row = StringKeyMap.createAsFlexibleOrdered();
        final Set<Entry<String, ValueType>> entrySet = propertyTypeMap.entrySet();
        int index = 0;
        for (Entry<String, ValueType> entry : entrySet) {
            final String propertyName = entry.getKey();
            final ValueType valueType = entry.getValue();
            final Object value = valueType.getValue(rs, index + 1);
            row.put(propertyName, value);
            ++index;
        }
        return row;
    }
}
