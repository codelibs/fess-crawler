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
package org.seasar.robot.dbflute.s2dao.valuetype;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.seasar.robot.dbflute.jdbc.Classification;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.s2dao.valuetype.plugin.BytesType;
import org.seasar.robot.dbflute.s2dao.valuetype.plugin.OracleResultSetType;
import org.seasar.robot.dbflute.s2dao.valuetype.plugin.PostgreResultSetType;
import org.seasar.robot.dbflute.s2dao.valuetype.plugin.SerializableType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.BigDecimalType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.BigIntegerType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.BinaryStreamType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.BinaryType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.BooleanType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.ByteType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.CharacterType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.ClassificationType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.DoubleType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.FloatType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.IntegerType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.LongType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.ObjectType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.ShortType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.SqlDateType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.StringType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.TimeType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.TimestampType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.UUIDType;
import org.seasar.robot.dbflute.s2dao.valuetype.registered.UserDefineType;
import org.seasar.robot.dbflute.util.DfReflectionUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnValueTypes {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    // Registered
    public final static ValueType STRING = new StringType();
    public final static ValueType CHARACTER = new CharacterType();
    public final static ValueType BYTE = new ByteType();
    public final static ValueType SHORT = new ShortType();
    public final static ValueType INTEGER = new IntegerType();
    public final static ValueType LONG = new LongType();
    public final static ValueType FLOAT = new FloatType();
    public final static ValueType DOUBLE = new DoubleType();
    public final static ValueType BIGDECIMAL = new BigDecimalType();
    public final static ValueType BIGINTEGER = new BigIntegerType();
    public final static ValueType TIME = new TimeType();
    public final static ValueType SQLDATE = new SqlDateType();
    public final static ValueType TIMESTAMP = new TimestampType();
    public final static ValueType BINARY = new BinaryType();
    public final static ValueType BINARY_STREAM = new BinaryStreamType();
    public final static ValueType BOOLEAN = new BooleanType();
    public final static ValueType UUID = new UUIDType();
    public final static ValueType CLASSIFICATION = new ClassificationType(); // DBFlute original class
    public final static ValueType OBJECT = new ObjectType();

    // Plug-in
    public final static ValueType ORACLE_RESULT_SET = new OracleResultSetType();
    public final static ValueType POSTGRE_RESULT_SET = new PostgreResultSetType();
    public final static ValueType SERIALIZABLE_BYTE_ARRAY = new SerializableType(BytesType.BYTES_TRAIT);

    // Internal
    private static final ValueType NULL = new NullType();

    private static final Class<?> BYTE_ARRAY_CLASS = new byte[0].getClass();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private static Map<Class<?>, ValueType> basicValueTypeMap = new ConcurrentHashMap<Class<?>, ValueType>();
    private static Map<String, ValueType> pluginValueTypeMap = new ConcurrentHashMap<String, ValueType>();
    private static Method isEnumMethod;

    private static Constructor<?> enumDefaultValueTypeConstructor;
    private static Constructor<?> enumOrdinalValueTypeConstructor;
    private static Constructor<?> enumStringValueTypeConstructor;

    private static Map<String, ValueType> valueTypeCache = new ConcurrentHashMap<String, ValueType>(50);

    static {
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
        registerBasicValueType(java.util.Date.class, TIMESTAMP);
        registerBasicValueType(Timestamp.class, TIMESTAMP);
        registerBasicValueType(Calendar.class, TIMESTAMP);
        registerBasicValueType(BYTE_ARRAY_CLASS, BINARY);
        registerBasicValueType(InputStream.class, BINARY_STREAM);
        registerBasicValueType(boolean.class, BOOLEAN);
        registerBasicValueType(Boolean.class, BOOLEAN);
        registerBasicValueType(UUID.class, UUID);
        registerBasicValueType(Classification.class, CLASSIFICATION); // DBFlute original class

        // Because object type is to be handle as special type.
        //registerValueType(Object.class, OBJECT);

        try {
            isEnumMethod = Class.class.getMethod("isEnum", (Class[]) null);
        } catch (Throwable ignore) {
            isEnumMethod = null;
            enumStringValueTypeConstructor = null;
            enumOrdinalValueTypeConstructor = null;
        }
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected TnValueTypes() {
    }

    // ===================================================================================
    //                                                                            Register
    //                                                                            ========
    /**
     * Register the basic value type.
     * @param keyType The key as type. (NotNull)
     * @param valueType The value type. (NotNull)
     */
    public static void registerBasicValueType(Class<?> keyType, ValueType valueType) {
        assertObjectNotNull("keyType", keyType);
        assertObjectNotNull("valueType", valueType);
        basicValueTypeMap.put(keyType, valueType);
    }

    /**
     * Remove the basic value type.
     * @param keyType The key as type. (NotNull)
     */
    public static void removeBasicValueType(Class<?> keyType) {
        assertObjectNotNull("keyType", keyType);
        basicValueTypeMap.remove(keyType);
    }

    /**
     * Register the plug-in value type.
     * @param keyName The key as name. (NotNull)
     * @param valueType The value type. (NotNull)
     */
    public static void registerPluginValueType(String keyName, ValueType valueType) {
        assertObjectNotNull("keyName", keyName);
        assertObjectNotNull("valueType", valueType);
        pluginValueTypeMap.put(keyName, valueType);
    }

    /**
     * Remove the plug-in value type.
     * @param keyName The key as name. (NotNull)
     */
    public static void removePluginValueType(String keyName) {
        assertObjectNotNull("keyName", keyName);
        pluginValueTypeMap.remove(keyName);
    }

    // ===================================================================================
    //                                                                                 Get
    //                                                                                 ===
    public static ValueType getValueType(Object obj) {
        if (obj == null) {
            return OBJECT;
        }
        return getValueType(obj.getClass());
    }

    public static ValueType getValueType(Class<?> clazz) {
        if (clazz == null) {
            return OBJECT;
        }
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            final ValueType valueType = getRegisteredValueType(c);
            if (valueType != null) {
                return valueType;
            }
        }
        if (Classification.class.isAssignableFrom(clazz)) {
            final ValueType valueType = getRegisteredValueType(Classification.class);
            if (valueType != null) {
                return valueType;
            }
        }
        final ValueType valueType = getCachedValueType(clazz);
        if (valueType != null) {
            return valueType;
        }
        return OBJECT;
    }

    private static ValueType getRegisteredValueType(Class<?> clazz) {
        return basicValueTypeMap.get(clazz);
    }

    /**
     * @param valueTypeName The name of value type. (NotNull)
     * @return The value type. (Nullable)
     */
    public static ValueType getPluginValueType(String valueTypeName) {
        assertObjectNotNull("valueTypeName", valueTypeName);
        return pluginValueTypeMap.get(valueTypeName);
    }

    private static boolean hasCachedValueType(Class<?> clazz) {
        return getCachedValueType(clazz) != null;
    }

    private static ValueType getCachedValueType(Class<?> clazz) {
        if (Map.class.isAssignableFrom(clazz)) {
            return null;
        }
        ValueType valueType = (ValueType) valueTypeCache.get(clazz.getName());
        if (valueType == NULL) {
            return null;
        }
        if (valueType != null) {
            return valueType;
        }
        Class<?> normalizedEnumClass = normalizeEnum(clazz);
        if (normalizedEnumClass != null) {
            valueType = getEnumDefaultValueType(normalizedEnumClass);
            valueTypeCache.put(normalizedEnumClass.getName(), valueType);
            return valueType;
        }
        valueType = createUserDefineValueType(clazz);
        if (valueType != null) {
            valueTypeCache.put(clazz.getName(), valueType);
            return valueType;
        }
        valueTypeCache.put(clazz.getName(), NULL);
        return null;
    }

    public static ValueType createUserDefineValueType(Class<?> clazz) {
        List<Method> valueOfMethods = new ArrayList<Method>();
        Method valueMethod = null;
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            if (DfReflectionUtil.isBridgeMethod(method) || DfReflectionUtil.isSyntheticMethod(method)) {
                continue;
            }
            int mod = method.getModifiers();
            if (method.getName().equals("valueOf") && method.getParameterTypes().length == 1
                    && method.getReturnType() == clazz && DfReflectionUtil.isPublic(mod)
                    && DfReflectionUtil.isStatic(mod)) {
                valueOfMethods.add(method);
            } else if (method.getName().equals("value") && method.getParameterTypes().length == 0
                    && DfReflectionUtil.isPublic(mod) && !DfReflectionUtil.isStatic(mod)) {
                valueMethod = method;
            }
        }
        if (valueMethod == null) {
            return null;
        }
        for (int i = 0; i < valueOfMethods.size(); ++i) {
            Method valueOfMethod = (Method) valueOfMethods.get(i);
            if (valueOfMethod.getParameterTypes()[0] == valueMethod.getReturnType()) {
                Class<?> baseClass = valueMethod.getReturnType();
                ValueType baseValueType = getRegisteredValueType(baseClass);
                if (baseValueType == null) {
                    return null;
                }
                return new UserDefineType(baseValueType, valueOfMethod, valueMethod);
            }
        }
        return null;
    }

    public static Class<?> getType(int sqltype) {
        switch (sqltype) {
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
            return Timestamp.class;
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

    public static ValueType getValueType(int sqltype) {
        return getValueType(getType(sqltype));
    }

    // ===================================================================================
    //                                                                                Enum
    //                                                                                ====
    public static void setEnumDefaultValueType(Class<?> enumDefaultValueTypeClass) throws NoSuchMethodException {
        enumDefaultValueTypeConstructor = enumDefaultValueTypeClass.getConstructor(new Class[] { Class.class });
    }

    public static void setEnumOrdinalValueType(Class<?> enumOrdinalValueTypeClass) throws NoSuchMethodException {
        enumOrdinalValueTypeConstructor = enumOrdinalValueTypeClass.getConstructor(new Class[] { Class.class });
    }

    public static void setEnumStringValueType(Class<?> enumStringValueTypeClass) throws NoSuchMethodException {
        enumStringValueTypeConstructor = enumStringValueTypeClass.getConstructor(new Class[] { Class.class });
    }

    private static Class<?> normalizeEnum(Class<?> clazz) {
        if (isEnumMethod == null || enumStringValueTypeConstructor == null) {
            return null;
        }
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            if (DfReflectionUtil.invoke(isEnumMethod, c, null).equals(Boolean.TRUE)) {
                return c;
            }
        }
        return null;
    }

    public static ValueType getEnumDefaultValueType(Class<?> clazz) {
        return (ValueType) DfReflectionUtil.newInstance(enumDefaultValueTypeConstructor, new Class<?>[] { clazz });
    }

    public static ValueType getEnumStringValueType(Class<?> clazz) {
        return (ValueType) DfReflectionUtil.newInstance(enumStringValueTypeConstructor, new Class<?>[] { clazz });
    }

    public static ValueType getEnumOrdinalValueType(Class<?> clazz) {
        return (ValueType) DfReflectionUtil.newInstance(enumOrdinalValueTypeConstructor, new Class<?>[] { clazz });
    }

    // ===================================================================================
    //                                                                               Clear
    //                                                                               =====
    public static void clear() {
        valueTypeCache.clear();
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    public static boolean isSimpleType(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        return clazz == String.class || clazz.isPrimitive() || clazz == Boolean.class || clazz == Character.class
                || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz)
                || Calendar.class.isAssignableFrom(clazz) || clazz == BYTE_ARRAY_CLASS || hasCachedValueType(clazz);
    }

    private static class NullType implements ValueType {

        public void bindValue(CallableStatement cs, String parameterName, Object value) throws SQLException {
            throw new SQLException("not supported");
        }

        public void bindValue(PreparedStatement ps, int index, Object value) throws SQLException {
            throw new SQLException("not supported");
        }

        public Object getValue(CallableStatement cs, int index) throws SQLException {
            throw new SQLException("not supported");
        }

        public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
            throw new SQLException("not supported");
        }

        public Object getValue(ResultSet resultSet, int index) throws SQLException {
            throw new SQLException("not supported");
        }

        public Object getValue(ResultSet resultSet, String columnName) throws SQLException {
            throw new SQLException("not supported");
        }

        public void registerOutParameter(CallableStatement cs, int index) throws SQLException {
            throw new SQLException("not supported");
        }

        public void registerOutParameter(CallableStatement cs, String parameterName) throws SQLException {
            throw new SQLException("not supported");
        }

        public String toText(Object value) {
            throw new UnsupportedOperationException("toText");
        }

        public int getSqlType() {
            return Types.NULL;
        }
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
    protected static void assertObjectNotNull(String variableName, Object value) {
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