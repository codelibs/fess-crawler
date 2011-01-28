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
package org.seasar.robot.dbflute.helper.beans.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;

import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanIllegalPropertyException;
import org.seasar.robot.dbflute.util.DfReflectionUtil;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class DfPropertyDescImpl implements DfPropertyDesc {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Object[] EMPTY_ARGS = new Object[0];

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String propertyName;
    private Class<?> propertyType;
    private Method readMethod;
    private Method writeMethod;
    private Field field;
    private DfBeanDesc beanDesc;
    private Constructor<?> stringConstructor;
    private Method valueOfMethod;
    private boolean readable = false;
    private boolean writable = false;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfPropertyDescImpl(String propertyName, Class<?> propertyType, Method readMethod, Method writeMethod,
            DfBeanDesc beanDesc) {
        this(propertyName, propertyType, readMethod, writeMethod, null, beanDesc);
    }

    public DfPropertyDescImpl(String propertyName, Class<?> propertyType, Method readMethod, Method writeMethod,
            Field field, DfBeanDesc beanDesc) {
        if (propertyName == null) {
            String msg = "The argument 'propertyName' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        if (propertyType == null) {
            String msg = "The argument 'propertyType' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        setReadMethod(readMethod);
        setWriteMethod(writeMethod);
        setField(field);
        this.beanDesc = beanDesc;
        setupStringConstructor();
        setupValueOfMethod();
    }

    private void setupStringConstructor() {
        Constructor<?>[] cons = propertyType.getConstructors();
        for (int i = 0; i < cons.length; ++i) {
            Constructor<?> con = cons[i];
            if (con.getParameterTypes().length == 1 && con.getParameterTypes()[0].equals(String.class)) {
                stringConstructor = con;
                break;
            }
        }
    }

    private void setupValueOfMethod() {
        Method[] methods = propertyType.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            if (DfReflectionUtil.isBridgeMethod(method) || DfReflectionUtil.isSyntheticMethod(method)) {
                continue;
            }
            if (DfReflectionUtil.isStatic(method.getModifiers()) && method.getName().equals("valueOf")
                    && method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(String.class)) {
                valueOfMethod = method;
                break;
            }
        }
    }

    // ===================================================================================
    //                                                                                Bean
    //                                                                                ====
    public DfBeanDesc getBeanDesc() {
        return beanDesc;
    }

    // ===================================================================================
    //                                                                            Property
    //                                                                            ========
    public final String getPropertyName() {
        return propertyName;
    }

    public final Class<?> getPropertyType() {
        return propertyType;
    }

    // ===================================================================================
    //                                                                              Method
    //                                                                              ======
    public final Method getReadMethod() {
        return readMethod;
    }

    public final void setReadMethod(Method readMethod) {
        this.readMethod = readMethod;
        if (readMethod != null) {
            readable = true;
        }
    }

    public final boolean hasReadMethod() {
        return readMethod != null;
    }

    public final Method getWriteMethod() {
        return writeMethod;
    }

    public final void setWriteMethod(Method writeMethod) {
        this.writeMethod = writeMethod;
        if (writeMethod != null) {
            writable = true;
        }
    }

    public final boolean hasWriteMethod() {
        return writeMethod != null;
    }

    // ===================================================================================
    //                                                                               Field
    //                                                                               =====
    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
        if (field != null && DfReflectionUtil.isPublic(field.getModifiers())) {
            readable = true;
            writable = true;
        }
    }

    // ===================================================================================
    //                                                                               Value
    //                                                                               =====
    /**
     * {@inheritDoc}
     */
    public final Object getValue(Object target) {
        try {
            if (!readable) {
                final Class<?> beanClass = beanDesc.getBeanClass();
                String msg = DfTypeUtil.toClassTitle(beanClass) + "." + propertyName;
                msg = msg + " is not readable.";
                throw new IllegalStateException(msg);
            } else if (hasReadMethod()) {
                return DfReflectionUtil.invoke(readMethod, target, EMPTY_ARGS);
            } else {
                return DfReflectionUtil.getValue(field, target);
            }
        } catch (Throwable t) {
            throw new DfBeanIllegalPropertyException(beanDesc.getBeanClass(), propertyName, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(Object target, Object value) {
        try {
            value = convertIfNeed(value);
            if (!writable) {
                final Class<?> beanClass = beanDesc.getBeanClass();
                String msg = DfTypeUtil.toClassTitle(beanClass) + "." + propertyName;
                msg = msg + " is not writable.";
                throw new IllegalStateException(msg);
            } else if (hasWriteMethod()) {
                DfReflectionUtil.invoke(writeMethod, target, new Object[] { value });
            } else {
                DfReflectionUtil.setValue(field, target, value);
            }
        } catch (Throwable t) {
            throw new DfBeanIllegalPropertyException(beanDesc.getBeanClass(), propertyName, t);
        }
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isReadable() {
        return readable;
    }

    public boolean isWritable() {
        return writable;
    }

    // ===================================================================================
    //                                                                             Convert
    //                                                                             =======
    public Object convertIfNeed(Object arg) {
        if (propertyType.isPrimitive()) {
            return convertPrimitiveWrapper(arg);
        } else if (Number.class.isAssignableFrom(propertyType)) {
            return convertNumber(arg);
        } else if (java.util.Date.class.isAssignableFrom(propertyType)) {
            return convertDate(arg);
        } else if (Boolean.class.isAssignableFrom(propertyType)) {
            return DfTypeUtil.toBoolean(arg);
        } else if (arg != null && arg.getClass() != String.class && String.class == propertyType) {
            return arg.toString();
        } else if (arg instanceof String && !String.class.equals(propertyType)) {
            return convertWithString(arg);
        } else if (java.util.Calendar.class.isAssignableFrom(propertyType)) {
            return DfTypeUtil.toCalendar(arg);
        }
        return arg;
    }

    private Object convertPrimitiveWrapper(Object arg) {
        return DfTypeUtil.toWrapper(arg, propertyType);
    }

    private Object convertNumber(Object arg) {
        return DfTypeUtil.toNumber(arg, propertyType);
    }

    private Object convertDate(Object arg) {
        if (propertyType == java.util.Date.class) {
            return DfTypeUtil.toDate(arg);
        } else if (propertyType == Timestamp.class) {
            return DfTypeUtil.toTimestamp(arg);
        } else if (propertyType == java.sql.Date.class) {
            return DfTypeUtil.toDate(arg);
        } else if (propertyType == Time.class) {
            return DfTypeUtil.toTime(arg);
        }
        return arg;
    }

    private Object convertWithString(Object arg) {
        if (stringConstructor != null) {
            return DfReflectionUtil.newInstance(stringConstructor, new Object[] { arg });
        }
        if (valueOfMethod != null) {
            return DfReflectionUtil.invoke(valueOfMethod, null, new Object[] { arg });
        }
        return arg;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public final String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("propertyName=");
        buf.append(propertyName);
        buf.append(",propertyType=");
        buf.append(propertyType.getName());
        buf.append(",readMethod=");
        buf.append(readMethod != null ? readMethod.getName() : "null");
        buf.append(",writeMethod=");
        buf.append(writeMethod != null ? writeMethod.getName() : "null");
        return buf.toString();
    }
}
