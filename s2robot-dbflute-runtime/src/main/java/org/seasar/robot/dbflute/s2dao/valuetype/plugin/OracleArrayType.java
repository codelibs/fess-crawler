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
package org.seasar.robot.dbflute.s2dao.valuetype.plugin;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;

import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * The type of Oracle's ARRAY for a property of collection type.
 * @author jflute
 */
public abstract class OracleArrayType extends GreatWallOfOracleType {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OracleArrayType(String arrayTypeName, Class<?> elementType) {
        super(Types.ARRAY, arrayTypeName, elementType);
    }

    @Override
    protected String getTitleName() {
        return "Array";
    }

    // ===================================================================================
    //                                                                           Get Value
    //                                                                           =========
    public Object getValue(ResultSet rs, int index) throws SQLException {
        return toPropertyValue(rs.getArray(index));
    }

    public Object getValue(ResultSet rs, String columnName) throws SQLException {
        return toPropertyValue(rs.getArray(columnName));
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return toPropertyValue(cs.getArray(index));
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        return toPropertyValue(cs.getArray(parameterName));
    }

    protected Collection<Object> toPropertyValue(Object oracleArray) throws SQLException {
        return mappingOracleArrayToList(oracleArray, _mainObjectType);
    }

    // ===================================================================================
    //                                                                          Bind Value
    //                                                                          ==========
    @Override
    protected Object toBindValue(Connection conn, Object paramExp, Object value) throws SQLException {
        assertArrayPropertyValueCollection(paramExp, value);
        return mappingListToOracleArray(conn, paramExp, (List<?>) value, _mainTypeName, _mainObjectType);
    }

    protected void assertArrayPropertyValueCollection(Object paramExp, Object value) {
        if (!(value instanceof Collection<?>)) {
            throwArrayPropertyValueNotCollectionException(paramExp, value);
        }
    }

    protected void throwArrayPropertyValueNotCollectionException(Object paramExp, Object value) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The property value for struct should be entity type:");
        br.addItem("Array");
        br.addElement(_mainTypeName);
        br.addItem("Element");
        br.addElement(DfTypeUtil.toClassTitle(_mainObjectType));
        br.addItem("Parameter");
        br.addElement(paramExp);
        br.addItem("Property Value");
        if (value != null) {
            br.addElement(value.getClass());
        }
        br.addElement(value);
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg);
    }
}