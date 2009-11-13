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
package org.seasar.robot.dbflute.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class DfReflectionUtil {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static Map<Class<?>, Class<?>> wrapperToPrimitiveMap = new HashMap<Class<?>, Class<?>>();
    private static Map<Class<?>, Class<?>> primitiveToWrapperMap = new HashMap<Class<?>, Class<?>>();
    private static Map<String, Class<?>> primitiveClsssNameMap = new HashMap<String, Class<?>>();
    static {
        wrapperToPrimitiveMap.put(Character.class, Character.TYPE);
        wrapperToPrimitiveMap.put(Byte.class, Byte.TYPE);
        wrapperToPrimitiveMap.put(Short.class, Short.TYPE);
        wrapperToPrimitiveMap.put(Integer.class, Integer.TYPE);
        wrapperToPrimitiveMap.put(Long.class, Long.TYPE);
        wrapperToPrimitiveMap.put(Double.class, Double.TYPE);
        wrapperToPrimitiveMap.put(Float.class, Float.TYPE);
        wrapperToPrimitiveMap.put(Boolean.class, Boolean.TYPE);

        primitiveToWrapperMap.put(Character.TYPE, Character.class);
        primitiveToWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveToWrapperMap.put(Short.TYPE, Short.class);
        primitiveToWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveToWrapperMap.put(Long.TYPE, Long.class);
        primitiveToWrapperMap.put(Double.TYPE, Double.class);
        primitiveToWrapperMap.put(Float.TYPE, Float.class);
        primitiveToWrapperMap.put(Boolean.TYPE, Boolean.class);

        primitiveClsssNameMap.put(Character.TYPE.getName(), Character.TYPE);
        primitiveClsssNameMap.put(Byte.TYPE.getName(), Byte.TYPE);
        primitiveClsssNameMap.put(Short.TYPE.getName(), Short.TYPE);
        primitiveClsssNameMap.put(Integer.TYPE.getName(), Integer.TYPE);
        primitiveClsssNameMap.put(Long.TYPE.getName(), Long.TYPE);
        primitiveClsssNameMap.put(Double.TYPE.getName(), Double.TYPE);
        primitiveClsssNameMap.put(Float.TYPE.getName(), Float.TYPE);
        primitiveClsssNameMap.put(Boolean.TYPE.getName(), Boolean.TYPE);
    }

    private static final Method IS_BRIDGE_METHOD = getIsBridgeMethod();
    private static final Method IS_SYNTHETIC_METHOD = getIsSyntheticMethod();

    private static Method getIsBridgeMethod() {
        try {
            return Method.class.getMethod("isBridge", (Class[]) null);
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }

    private static Method getIsSyntheticMethod() {
        try {
            return Method.class.getMethod("isSynthetic", (Class[]) null);
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }

    // ===================================================================================
    //                                                                               Class
    //                                                                               =====
    public static Class<?> forName(String className) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            return Class.forName(className, true, loader);
        } catch (ClassNotFoundException e) {
            String msg = "The class was not found: class=" + className + " loader=" + loader;
            throw new IllegalStateException(msg, e);
        }
    }

    public static Object newInstance(Class<?> clazz) {
        assertObjectNotNull("clazz", clazz);
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            String msg = "Failed to instantiate the class: " + clazz;
            throw new IllegalStateException(msg, e);
        } catch (IllegalAccessException e) {
            String msg = "Illegal access to the class: " + clazz;
            throw new IllegalStateException(msg, e);
        }
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>[] argTypes) {
        try {
            return clazz.getConstructor(argTypes);
        } catch (NoSuchMethodException e) {
            String msg = "Such a method was not found:";
            msg = msg + " class=" + clazz + " argTypes=" + Arrays.asList(argTypes);
            throw new IllegalStateException(msg, e);
        }
    }

    public static Object newInstance(Constructor<?> constructor, Object[] args) {
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException e) {
            String msg = "Failed to instantiate the class: " + constructor;
            throw new IllegalStateException(msg, e);
        } catch (IllegalAccessException e) {
            String msg = "Illegal access to the class: " + constructor;
            throw new IllegalStateException(msg, e);
        } catch (InvocationTargetException e) {
            String msg = "The InvocationTargetException occurred: " + constructor;
            throw new IllegalStateException(msg, e.getTargetException());
        }
    }

    public static boolean isAssignableFrom(Class<?> toClass, Class<?> fromClass) {
        if (toClass == Object.class && !fromClass.isPrimitive()) {
            return true;
        }
        if (toClass.isPrimitive()) {
            fromClass = getPrimitiveClassIfWrapper(fromClass);
        }
        return toClass.isAssignableFrom(fromClass);
    }

    public static Class<?> getPrimitiveClass(Class<?> clazz) {
        return (Class<?>) wrapperToPrimitiveMap.get(clazz);
    }

    public static Class<?> getPrimitiveClassIfWrapper(Class<?> clazz) {
        Class<?> ret = getPrimitiveClass(clazz);
        if (ret != null) {
            return ret;
        }
        return clazz;
    }

    public static Class<?> getWrapperClass(Class<?> clazz) {
        return (Class<?>) primitiveToWrapperMap.get(clazz);
    }

    // ===================================================================================
    //                                                                               Field
    //                                                                               =====
    public static Object getValue(Field field, Object target) {
        assertObjectNotNull("field", field);
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            String msg = "Illegal access to the field: field=" + field + " target=" + target;
            throw new IllegalStateException(msg, e);
        }
    }

    public static void setValue(Field field, Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            String msg = "Illegal access to the field: field=" + field + " target=" + target;
            throw new IllegalStateException(msg, e);
        }
    }

    public static boolean isInstanceField(Field field) {
        int mod = field.getModifiers();
        return !Modifier.isStatic(mod) && !Modifier.isFinal(mod);
    }

    public static boolean isPublicField(Field field) {
        int mod = field.getModifiers();
        return Modifier.isPublic(mod);
    }

    // ===================================================================================
    //                                                                              Method
    //                                                                              ======
    public static Method getMethod(Class<?> clazz, String name, Class<?>[] parameterTypes) {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            String msg = "Not found the method in the class: ";
            msg = msg + " method=" + name + (parameterTypes != null ? Arrays.asList(parameterTypes) : "");
            msg = msg + " class=" + clazz;
            throw new IllegalStateException(msg, e);
        }
    }

    public static Object invoke(Method method, Object target, Object[] args) {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            Throwable t = ex.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            if (t instanceof Error) {
                throw (Error) t;
            }
            String msg = "The InvocationTargetException occurred: ";
            msg = msg + " method=" + method + " target=" + target;
            msg = msg + " args=" + (args != null ? Arrays.asList(args) : "");
            throw new IllegalStateException(msg, t);
        } catch (IllegalAccessException e) {
            String msg = "Illegal access to the method:";
            msg = msg + " method =" + method;
            msg = msg + " args=" + (args != null ? Arrays.asList(args) : "");
            throw new IllegalStateException(msg, e);
        }
    }

    public static boolean isBridgeMethod(final Method method) {
        if (IS_BRIDGE_METHOD == null) {
            return false;
        }
        return ((Boolean) invoke(IS_BRIDGE_METHOD, method, null)).booleanValue();
    }

    public static boolean isSyntheticMethod(final Method method) {
        if (IS_SYNTHETIC_METHOD == null) {
            return false;
        }
        return ((Boolean) invoke(IS_SYNTHETIC_METHOD, method, null)).booleanValue();
    }

    // ===================================================================================
    //                                                                            Modifier
    //                                                                            ========
    public static boolean isPublic(int modifier) {
        return Modifier.isPublic(modifier);
    }

    public static boolean isStatic(int modifier) {
        return Modifier.isStatic(modifier);
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
