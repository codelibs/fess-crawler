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
package org.seasar.robot.dbflute.s2dao.valuetype;

import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.BIGDECIMAL;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.BIGINTEGER;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.BINARY;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.BINARY_STREAM;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.BOOLEAN;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.BYTE;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.BYTES_OID;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.BYTE_ARRAY_CLASS;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.CHARACTER;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.CLASSIFICATION;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.DEFAULT_OBJECT;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.DOUBLE;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.FIXED_LENGTH_STRING;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.FLOAT;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.INTEGER;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.LONG;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.OBJECT_BINDING_BIGDECIMAL;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.SHORT;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.SQLDATE;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.STRING;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.STRING_CLOB;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.TIME;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.TIMESTAMP;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.UTILDATE_AS_SQLDATE;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.UTILDATE_AS_TIMESTAMP;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.UUID_AS_DIRECT;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.UUID_AS_STRING;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.seasar.robot.dbflute.jdbc.Classification;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.s2dao.valuetype.basic.ObjectType;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnPlainValueTypes {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Map<Class<?>, ValueType> _basicObjectValueTypeMap = new ConcurrentHashMap<Class<?>, ValueType>();
    protected final Map<Class<?>, ValueType> _basicInterfaceValueTypeMap = new ConcurrentHashMap<Class<?>, ValueType>();
    protected final Map<String, ValueType> _pluginValueTypeMap = new ConcurrentHashMap<String, ValueType>();
    protected final Map<Integer, ValueType> _dynamicObjectValueTypeMap = new ConcurrentHashMap<Integer, ValueType>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnPlainValueTypes() {
        initialize();
    }

    protected void initialize() {
        // basic (object)
        registerBasicValueType(String.class, STRING);
        registerBasicValueType(char.class, CHARACTER);
        registerBasicValueType(Character.class, CHARACTER);
        registerBasicValueType(byte.class, BYTE);
        registerBasicValueType(Byte.class, BYTE);
        registerBasicValueType(short.class, SHORT);
        registerBasicValueType(Short.class, SHORT);
        registerBasicValueType(int.class, INTEGER);
        registerBasicValueType(Integer.class, INTEGER);
        registerBasicValueType(long.class, LONG);
        registerBasicValueType(Long.class, LONG);
        registerBasicValueType(float.class, FLOAT);
        registerBasicValueType(Float.class, FLOAT);
        registerBasicValueType(double.class, DOUBLE);
        registerBasicValueType(Double.class, DOUBLE);
        registerBasicValueType(BigInteger.class, BIGINTEGER);
        registerBasicValueType(BigDecimal.class, BIGDECIMAL);
        registerBasicValueType(java.sql.Date.class, SQLDATE);
        registerBasicValueType(java.sql.Time.class, TIME);

        // The (java.util.)date type is treated as As-SqlDate by default.
        // If Oracle, this switches to As-Timestamp when initialization
        // because the date type of Oracle has time parts.
        registerBasicValueType(java.util.Date.class, UTILDATE_AS_SQLDATE);

        registerBasicValueType(Timestamp.class, TIMESTAMP);
        registerBasicValueType(Calendar.class, TIMESTAMP);
        registerBasicValueType(BYTE_ARRAY_CLASS, BINARY);
        registerBasicValueType(InputStream.class, BINARY_STREAM);
        registerBasicValueType(boolean.class, BOOLEAN);
        registerBasicValueType(Boolean.class, BOOLEAN);

        // The (java.util.)UUID type is treated as As-Direct by default.
        // If SQLServer, this switches to As-String when initialization
        // because the UUID type of SQLServer cannot handle the type.
        registerBasicValueType(UUID.class, UUID_AS_DIRECT);

        // basic (interface)
        registerBasicValueType(Classification.class, CLASSIFICATION); // DBFlute original class

        // Because object type is to be handle as special type.
        //registerBasicValueType(Object.class, OBJECT);

        // plug-in (default)
        registerPluginValueType("stringClobType", STRING_CLOB);
        registerPluginValueType("bytesOidType", BYTES_OID);
        registerPluginValueType("fixedLengthStringType", FIXED_LENGTH_STRING);
        registerPluginValueType("objectBindingBigDecimalType", OBJECT_BINDING_BIGDECIMAL);
        registerPluginValueType("oracleDateType", UTILDATE_AS_TIMESTAMP);
        registerPluginValueType("uuidAsStringType", UUID_AS_STRING);
    }

    // ===================================================================================
    //                                                                                Find
    //                                                                                ====
    // -----------------------------------------------------
    //                                                  Find
    //                                                  ----
    /**
     * Find a value type by a class type or an object instance. <br />
     * A class type is a prior searching key.
     * @param type The type of class. (NullAllowed: if null, searching by instance)
     * @param value The object value. (NullAllowed: if null, returns default object type)
     * @return The value type. (NotNull: if not found, returns object type)
     */
    public ValueType findByTypeOrValue(Class<?> type, Object value) {
        final ValueType byType = getValueType(type);
        if (!isDefaultObject(byType)) {
            return byType;
        }
        return getValueType(value);
    }

    /**
     * Find a value type by an object instance or a definition type of JDBC. <br />
     * An object instance is a prior searching key.
     * @param value The object value. (NullAllowed: if null, returns dynamic object type)
     * @param jdbcDefType The definition type of JDBC. (NullAllowed: if null, searching by instance)
     * @return The value type. (NotNull: if not found, returns object type)
     */
    public ValueType findByValueOrJdbcDefType(Object value, int jdbcDefType) {
        final ValueType byValue = getValueType(value);
        if (!isDefaultObject(byValue)) {
            return byValue;
        }
        return getValueType(jdbcDefType);
    }

    // ===================================================================================
    //                                                                                 Get
    //                                                                                 ===
    // -----------------------------------------------------
    //                                               byValue
    //                                               -------
    /**
     * Get the value type by object instance.
     * @param value The object value. (NullAllowed: if null, returns object type)
     * @return The value type. (NotNull: if not found, returns object type)
     */
    public ValueType getValueType(Object value) {
        if (value == null) {
            return DEFAULT_OBJECT;
        }
        return getValueType(value.getClass());
    }

    // -----------------------------------------------------
    //                                                byType
    //                                                ------
    /**
     * Get the value type by class type. <br />
     * The basic objects are prior to the basic interfaces basically,
     * but only when the ENUM is assignable from the class type, interfaces are prior.
     * Because frequently the ENUM has application own interfaces.
     * Actually Classification of DBFlute matches the pattern.
     * @param type The type of class. (NullAllowed: if null, returns object type)
     * @return The value type. (NotNull: if not found, returns object type)
     */
    public ValueType getValueType(Class<?> type) {
        if (type == null) {
            return DEFAULT_OBJECT;
        }
        final boolean interfaceFirst = Enum.class.isAssignableFrom(type);
        ValueType valueType = null;
        if (interfaceFirst) {
            valueType = getBasicInterfaceValueType(type);
            if (valueType == null) {
                valueType = getBasicObjectValueType(type);
            }
        } else {
            valueType = getBasicObjectValueType(type);
            if (valueType == null) {
                valueType = getBasicInterfaceValueType(type);
            }
        }
        return valueType != null ? valueType : DEFAULT_OBJECT;
    }

    protected ValueType getBasicObjectValueType(Class<?> type) {
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            final ValueType valueType = _basicObjectValueTypeMap.get(c);
            if (valueType != null) {
                return valueType;
            }
        }
        return null;
    }

    protected ValueType getBasicInterfaceValueType(Class<?> type) {
        final Set<Entry<Class<?>, ValueType>> entrySet = _basicInterfaceValueTypeMap.entrySet();
        for (Entry<Class<?>, ValueType> entry : entrySet) {
            final Class<?> inf = entry.getKey();
            if (inf.isAssignableFrom(type)) {
                return entry.getValue();
            }
        }
        return null;
    }

    // -----------------------------------------------------
    //                                         byJdbcDefType
    //                                         -------------
    /**
     * @param jdbcDefType The definition type of JDBC.
     * @return The value type. (NotNull)
     */
    public ValueType getValueType(int jdbcDefType) { // for no entity and so on
        final Class<?> type = getType(jdbcDefType);
        if (type.equals(Object.class)) {
            // uses dynamic object
            ValueType valueType = _dynamicObjectValueTypeMap.get(jdbcDefType);
            if (valueType != null) {
                return valueType;
            } else {
                synchronized (_dynamicObjectValueTypeMap) {
                    valueType = _dynamicObjectValueTypeMap.get(jdbcDefType);
                    if (valueType != null) {
                        return valueType;
                    }
                    final ObjectType objectType = new ObjectType(jdbcDefType);
                    _dynamicObjectValueTypeMap.put(jdbcDefType, objectType);
                    return objectType;
                }
            }
        } else {
            return getValueType(type);
        }
    }

    protected Class<?> getType(int jdbcDefType) {
        switch (jdbcDefType) {
        case Types.TINYINT:
            return Byte.class;
        case Types.SMALLINT:
            return Short.class;
        case Types.INTEGER:
            return Integer.class;
        case Types.BIGINT:
            return Long.class;
        case Types.REAL:
        case Types.FLOAT:
            return Float.class;
        case Types.DOUBLE:
            return Double.class;
        case Types.DECIMAL:
        case Types.NUMERIC:
            return BigDecimal.class;
        case Types.DATE:
            return java.sql.Date.class;
        case Types.TIME:
            return java.sql.Time.class;
        case Types.TIMESTAMP:
            return Timestamp.class;
        case Types.BINARY:
        case Types.BLOB:
        case Types.VARBINARY:
        case Types.LONGVARBINARY:
            return BYTE_ARRAY_CLASS;
        case Types.CHAR:
        case Types.LONGVARCHAR:
        case Types.VARCHAR:
            return String.class;
        case Types.BOOLEAN:
            return Boolean.class;
        default:
            return Object.class;
        }
    }

    // -----------------------------------------------------
    //                                      byName (Plug-in)
    //                                      ----------------
    /**
     * @param valueTypeName The name of value type. (NotNull)
     * @return The value type. (NullAllowed)
     */
    public ValueType getPluginValueType(String valueTypeName) {
        assertObjectNotNull("valueTypeName", valueTypeName);
        return _pluginValueTypeMap.get(valueTypeName);
    }

    // -----------------------------------------------------
    //                                               Default
    //                                               -------
    public boolean isDefaultObject(ValueType valueType) {
        if (valueType == null) {
            return false;
        }
        if (!ObjectType.class.equals(valueType.getClass())) {
            return false;
        }
        return ((ObjectType) valueType).isDefaultObject();
    }

    public boolean isDynamicObject(ValueType valueType) {
        if (valueType == null) {
            return false;
        }
        if (!ObjectType.class.equals(valueType.getClass())) {
            return false;
        }
        return !((ObjectType) valueType).isDefaultObject(); // means dynamic
    }

    // ===================================================================================
    //                                                                            Register
    //                                                                            ========
    // *basically should be executed in application's initialization
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    /**
     * Register the basic value type.
     * @param keyType The key as type. (NotNull)
     * @param valueType The value type. (NotNull)
     */
    public synchronized void registerBasicValueType(Class<?> keyType, ValueType valueType) {
        assertObjectNotNull("keyType", keyType);
        assertObjectNotNull("valueType", valueType);
        if (keyType.isInterface()) {
            _basicInterfaceValueTypeMap.put(keyType, valueType);
        } else {
            _basicObjectValueTypeMap.put(keyType, valueType);
        }
    }

    /**
     * Remove the basic value type.
     * @param keyType The key as type. (NotNull)
     */
    public synchronized void removeBasicValueType(Class<?> keyType) {
        assertObjectNotNull("keyType", keyType);
        if (_basicObjectValueTypeMap.containsKey(keyType)) {
            _basicObjectValueTypeMap.remove(keyType);
        }
        if (_basicInterfaceValueTypeMap.containsKey(keyType)) {
            _basicInterfaceValueTypeMap.remove(keyType);
        }
    }

    // -----------------------------------------------------
    //                                               Plug-in
    //                                               -------
    /**
     * Register the plug-in value type.
     * @param keyName The key as name. (NotNull)
     * @param valueType The value type. (NotNull)
     */
    public synchronized void registerPluginValueType(String keyName, ValueType valueType) {
        assertObjectNotNull("keyName", keyName);
        assertObjectNotNull("valueType", valueType);
        _pluginValueTypeMap.put(keyName, valueType);
    }

    /**
     * Remove the plug-in value type.
     * @param keyName The key as name. (NotNull)
     */
    public synchronized void removePluginValueType(String keyName) {
        assertObjectNotNull("keyName", keyName);
        _pluginValueTypeMap.remove(keyName);
    }

    // ===================================================================================
    //                                                                             Restore
    //                                                                             =======
    protected synchronized void restoreDefault() { // as unit test utility
        _basicObjectValueTypeMap.clear();
        _basicInterfaceValueTypeMap.clear();
        _pluginValueTypeMap.clear();
        _dynamicObjectValueTypeMap.clear();
        initialize();
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }
}