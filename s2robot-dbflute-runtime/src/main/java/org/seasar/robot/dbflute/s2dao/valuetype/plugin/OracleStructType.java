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

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * The type of Oracle's STRUCT for a property of collection type.
 * @author jflute
 */
public abstract class OracleStructType extends GreatWallOfOracleType {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OracleStructType(String structTypeName, Class<?> entityType) {
        super(Types.STRUCT, structTypeName, entityType);
        if (_mainEntityPrototype == null) {
            String msg = "The entityType should be an instance of Entity: " + entityType;
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    protected String getTitleName() {
        return "Struct";
    }

    // ===================================================================================
    //                                                                           Get Value
    //                                                                           =========
    public Object getValue(ResultSet rs, int index) throws SQLException {
        return toPropertyValue(rs.getObject(index));
    }

    public Object getValue(ResultSet rs, String columnName) throws SQLException {
        return toPropertyValue(rs.getObject(columnName));
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return toPropertyValue(cs.getObject(index));
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        return toPropertyValue(cs.getObject(parameterName));
    }

    protected Entity toPropertyValue(Object oracleStruct) throws SQLException {
        return mappingOracleStructToEntity(oracleStruct, _mainEntityPrototype);
    }

    // ===================================================================================
    //                                                                          Bind Value
    //                                                                          ==========
    @Override
    protected Object toBindValue(Connection conn, Object paramExp, Object value) throws SQLException {
        assertStructPropertyValueNotEntity(paramExp, value);
        return mappingEntityToOracleStruct(conn, paramExp, (Entity) value);
    }

    protected void assertStructPropertyValueNotEntity(Object paramExp, Object value) {
        if (!(value instanceof Entity)) {
            throwStructPropertyValueNotEntityException(paramExp, value);
        }
    }

    protected void throwStructPropertyValueNotEntityException(Object paramExp, Object value) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The property value for struct should be entity type.");
        br.addItem("Struct");
        br.addElement(_mainTypeName);
        br.addItem("Entity");
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