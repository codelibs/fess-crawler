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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.robot.dbflute.helper.StringKeyMap;
import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanConstructorNotFoundException;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanFieldNotFoundException;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanMethodNotFoundException;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanPropertyNotFoundException;
import org.seasar.robot.dbflute.util.DfReflectionUtil;
import org.seasar.robot.dbflute.util.DfTypeUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class DfBeanDescImpl implements DfBeanDesc {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Object[] EMPTY_ARGS = new Object[0];
    private static final Class<?>[] EMPTY_PARAM_TYPES = new Class<?>[0];

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Class<?> _beanClass;
    private Constructor<?>[] _constructors;

    private StringKeyMap<DfPropertyDesc> _propertyDescMap = StringKeyMap.createAsCaseInsensitive();
    private Map<String, Method[]> _methodsMap = new HashMap<String, Method[]>();
    private Map<String, Field> _fieldMap = new HashMap<String, Field>();

    private transient Set<String> _invalidPropertyNames = new HashSet<String>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfBeanDescImpl(Class<?> beanClass) {
        if (beanClass == null) {
            String msg = "The argument 'beanClass' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        this._beanClass = beanClass;
        _constructors = beanClass.getConstructors();
        setupPropertyDescs();
        setupMethods();
        setupFields();
    }

    // ===================================================================================
    //                                                                                Bean
    //                                                                                ====
    public Class<?> getBeanClass() {
        return _beanClass;
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public Constructor<?> getSuitableConstructor(Object[] args) throws DfBeanConstructorNotFoundException {
        if (args == null) {
            args = EMPTY_ARGS;
        }
        Constructor<?> constructor = findSuitableConstructor(args);
        if (constructor != null) {
            return constructor;
        }
        constructor = findSuitableConstructorAdjustNumber(args);
        if (constructor != null) {
            return constructor;
        }
        throw new DfBeanConstructorNotFoundException(_beanClass, args);
    }

    public Constructor<?> getConstructor(final Class<?>[] paramTypes) {
        for (int i = 0; i < _constructors.length; ++i) {
            if (Arrays.equals(paramTypes, _constructors[i].getParameterTypes())) {
                return _constructors[i];
            }
        }
        throw new DfBeanConstructorNotFoundException(_beanClass, paramTypes);
    }

    // ===================================================================================
    //                                                                            Property
    //                                                                            ========
    public boolean hasPropertyDesc(String propertyName) {
        return getPropertyDescInternally(propertyName) != null;
    }

    public DfPropertyDesc getPropertyDesc(String propertyName) throws DfBeanPropertyNotFoundException {
        DfPropertyDesc pd = getPropertyDescInternally(propertyName);
        if (pd == null) {
            throw new DfBeanPropertyNotFoundException(_beanClass, propertyName);
        }
        return pd;
    }

    private DfPropertyDesc getPropertyDescInternally(String propertyName) {
        return _propertyDescMap.get(propertyName);
    }

    public int getPropertyDescSize() {
        return _propertyDescMap.size();
    }

    public List<String> getProppertyNameList() {
        return new ArrayList<String>(_propertyDescMap.keySet());
    }

    // ===================================================================================
    //                                                                               Field
    //                                                                               =====
    public boolean hasField(String fieldName) {
        return _fieldMap.get(fieldName) != null;
    }

    public Field getField(String fieldName) {
        Field field = (Field) _fieldMap.get(fieldName);
        if (field == null) {
            throw new DfBeanFieldNotFoundException(_beanClass, fieldName);
        }
        return field;
    }

    public int getFieldSize() {
        return _fieldMap.size();
    }

    // ===================================================================================
    //                                                                              Method
    //                                                                              ======
    public Method getMethod(final String methodName) {
        return getMethod(methodName, EMPTY_PARAM_TYPES);
    }

    public Method getMethodNoException(final String methodName) {
        return getMethodNoException(methodName, EMPTY_PARAM_TYPES);
    }

    public Method getMethod(final String methodName, final Class<?>[] paramTypes) {
        Method method = getMethodNoException(methodName, paramTypes);
        if (method != null) {
            return method;
        }
        throw new DfBeanMethodNotFoundException(_beanClass, methodName, paramTypes);
    }

    public Method getMethodNoException(final String methodName, final Class<?>[] paramTypes) {
        final Method[] methods = (Method[]) _methodsMap.get(methodName);
        if (methods == null) {
            return null;
        }
        for (int i = 0; i < methods.length; ++i) {
            if (Arrays.equals(paramTypes, methods[i].getParameterTypes())) {
                return methods[i];
            }
        }
        return null;
    }

    public Method[] getMethods(String methodName) throws DfBeanMethodNotFoundException {
        Method[] methods = (Method[]) _methodsMap.get(methodName);
        if (methods == null) {
            throw new DfBeanMethodNotFoundException(_beanClass, methodName, null);
        }
        return methods;
    }

    public boolean hasMethod(String methodName) {
        return _methodsMap.get(methodName) != null;
    }

    public String[] getMethodNames() {
        return (String[]) _methodsMap.keySet().toArray(new String[_methodsMap.size()]);
    }

    // ===================================================================================
    //                                                                          Reflection
    //                                                                          ==========
    public Object newInstance(Object[] args) throws DfBeanConstructorNotFoundException {
        Constructor<?> constructor = getSuitableConstructor(args);
        return DfReflectionUtil.newInstance(constructor, args);
    }

    public Object getFieldValue(String fieldName, Object target) throws DfBeanFieldNotFoundException {
        Field field = getField(fieldName);
        return DfReflectionUtil.getValue(field, target);
    }

    public Object invoke(Object target, String methodName, Object[] args) {
        Method method = getSuitableMethod(methodName, args);
        return DfReflectionUtil.invoke(method, target, args);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    private Constructor<?> findSuitableConstructor(Object[] args) {
        outerLoop: for (int i = 0; i < _constructors.length; ++i) {
            Class<?>[] paramTypes = _constructors[i].getParameterTypes();
            if (paramTypes.length != args.length) {
                continue;
            }
            for (int j = 0; j < args.length; ++j) {
                if (args[j] == null || DfReflectionUtil.isAssignableFrom(paramTypes[j], args[j].getClass())) {
                    continue;
                }
                continue outerLoop;
            }
            return _constructors[i];
        }
        return null;
    }

    private Constructor<?> findSuitableConstructorAdjustNumber(Object[] args) {
        outerLoop: for (int i = 0; i < _constructors.length; ++i) {
            Class<?>[] paramTypes = _constructors[i].getParameterTypes();
            if (paramTypes.length != args.length) {
                continue;
            }
            for (int j = 0; j < args.length; ++j) {
                if (args[j] == null || DfReflectionUtil.isAssignableFrom(paramTypes[j], args[j].getClass())
                        || adjustNumber(paramTypes, args, j)) {
                    continue;
                }
                continue outerLoop;
            }
            return _constructors[i];
        }
        return null;
    }

    private static boolean adjustNumber(Class<?>[] paramTypes, Object[] args, int index) {
        if (paramTypes[index].isPrimitive()) {
            if (paramTypes[index] == int.class) {
                args[index] = DfTypeUtil.toInteger(args[index]);
                return true;
            } else if (paramTypes[index] == double.class) {
                args[index] = DfTypeUtil.toDouble(args[index]);
                return true;
            } else if (paramTypes[index] == long.class) {
                args[index] = DfTypeUtil.toLong(args[index]);
                return true;
            } else if (paramTypes[index] == short.class) {
                args[index] = DfTypeUtil.toShort(args[index]);
                return true;
            } else if (paramTypes[index] == float.class) {
                args[index] = DfTypeUtil.toFloat(args[index]);
                return true;
            }
        } else {
            if (paramTypes[index] == Integer.class) {
                args[index] = DfTypeUtil.toInteger(args[index]);
                return true;
            } else if (paramTypes[index] == Double.class) {
                args[index] = DfTypeUtil.toDouble(args[index]);
                return true;
            } else if (paramTypes[index] == Long.class) {
                args[index] = DfTypeUtil.toLong(args[index]);
                return true;
            } else if (paramTypes[index] == Short.class) {
                args[index] = DfTypeUtil.toShort(args[index]);
                return true;
            } else if (paramTypes[index] == Float.class) {
                args[index] = DfTypeUtil.toFloat(args[index]);
                return true;
            }
        }
        return false;
    }

    private void setupPropertyDescs() {
        final Method[] methods = _beanClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method m = methods[i];
            if (DfReflectionUtil.isBridgeMethod(m) || DfReflectionUtil.isSyntheticMethod(m)) {
                continue;
            }
            final String methodName = m.getName();
            if (methodName.startsWith("get")) {
                if (m.getParameterTypes().length != 0 || methodName.equals("getClass")
                        || m.getReturnType() == void.class) {
                    continue;
                }
                final String propertyName = initBeansProp(methodName.substring(3));
                setupReadMethod(m, propertyName);
            } else if (methodName.startsWith("is")) {
                if (m.getParameterTypes().length != 0 || !m.getReturnType().equals(Boolean.TYPE)
                        && !m.getReturnType().equals(Boolean.class)) {
                    continue;
                }
                final String propertyName = initBeansProp(methodName.substring(2));
                setupReadMethod(m, propertyName);
            } else if (methodName.startsWith("set")) {
                if (m.getParameterTypes().length != 1 || methodName.equals("setClass")
                        || m.getReturnType() != void.class) {
                    continue;
                }
                final String propertyName = initBeansProp(methodName.substring(3));
                setupWriteMethod(m, propertyName);
            }
        }
        for (Iterator<String> i = _invalidPropertyNames.iterator(); i.hasNext();) {
            _propertyDescMap.remove(i.next());
        }
        _invalidPropertyNames.clear();
    }

    private static String initBeansProp(String name) {
        return Srl.initBeansProp(name);
    }

    private void addPropertyDesc(DfPropertyDesc propertyDesc) {
        if (propertyDesc == null) {
            String msg = "The argument 'propertyDesc' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        _propertyDescMap.put(propertyDesc.getPropertyName(), propertyDesc);
    }

    private void setupReadMethod(Method readMethod, String propertyName) {
        Class<?> propertyType = readMethod.getReturnType();
        DfPropertyDesc propDesc = getPropertyDescInternally(propertyName);
        if (propDesc != null) {
            if (!propDesc.getPropertyType().equals(propertyType)) {
                _invalidPropertyNames.add(propertyName);
            } else {
                propDesc.setReadMethod(readMethod);
            }
        } else {
            propDesc = new DfPropertyDescImpl(propertyName, propertyType, readMethod, null, null, this);
            addPropertyDesc(propDesc);
        }
    }

    private void setupWriteMethod(Method writeMethod, String propertyName) {
        Class<?> propertyType = writeMethod.getParameterTypes()[0];
        DfPropertyDesc propDesc = getPropertyDescInternally(propertyName);
        if (propDesc != null) {
            if (!propDesc.getPropertyType().equals(propertyType)) {
                _invalidPropertyNames.add(propertyName);
            } else {
                propDesc.setWriteMethod(writeMethod);
            }
        } else {
            propDesc = new DfPropertyDescImpl(propertyName, propertyType, null, writeMethod, null, this);
            addPropertyDesc(propDesc);
        }
    }

    private Method getSuitableMethod(String methodName, Object[] args) throws DfBeanMethodNotFoundException {
        if (args == null) {
            args = EMPTY_ARGS;
        }
        Method[] methods = getMethods(methodName);
        Method method = findSuitableMethod(methods, args);
        if (method != null) {
            return method;
        }
        method = findSuitableMethodAdjustNumber(methods, args);
        if (method != null) {
            return method;
        }
        throw new DfBeanMethodNotFoundException(_beanClass, methodName, args);
    }

    private Method findSuitableMethod(Method[] methods, Object[] args) {
        outerLoop: for (int i = 0; i < methods.length; ++i) {
            Class<?>[] paramTypes = methods[i].getParameterTypes();
            if (paramTypes.length != args.length) {
                continue;
            }
            for (int j = 0; j < args.length; ++j) {
                if (args[j] == null || DfReflectionUtil.isAssignableFrom(paramTypes[j], args[j].getClass())) {
                    continue;
                }
                continue outerLoop;
            }
            return methods[i];
        }
        return null;
    }

    private Method findSuitableMethodAdjustNumber(Method[] methods, Object[] args) {
        outerLoop: for (int i = 0; i < methods.length; ++i) {
            Class<?>[] paramTypes = methods[i].getParameterTypes();
            if (paramTypes.length != args.length) {
                continue;
            }
            for (int j = 0; j < args.length; ++j) {
                if (args[j] == null || DfReflectionUtil.isAssignableFrom(paramTypes[j], args[j].getClass())
                        || adjustNumber(paramTypes, args, j)) {
                    continue;
                }
                continue outerLoop;
            }
            return methods[i];
        }
        return null;
    }

    private void setupMethods() {
        final Map<String, List<Method>> methodListMap = new LinkedHashMap<String, List<Method>>();
        final Method[] methods = _beanClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            if (DfReflectionUtil.isBridgeMethod(method) || DfReflectionUtil.isSyntheticMethod(method)) {
                continue;
            }
            String methodName = method.getName();
            List<Method> list = (List<Method>) methodListMap.get(methodName);
            if (list == null) {
                list = new ArrayList<Method>();
                methodListMap.put(methodName, list);
            }
            list.add(method);
        }
        final Set<Entry<String, List<Method>>> entrySet = methodListMap.entrySet();
        for (Entry<String, List<Method>> entry : entrySet) {
            final String key = entry.getKey();
            final List<Method> methodList = entry.getValue();
            _methodsMap.put(key, methodList.toArray(new Method[methodList.size()]));
        }
    }

    /*
     * private void setupField() { for (Class clazz = beanClass_; clazz !=
     * Object.class && clazz != null; clazz = clazz.getSuperclass()) {
     * 
     * Field[] fields = clazz.getDeclaredFields(); for (int i = 0; i <
     * fields.length; ++i) { Field field = fields[i]; String fname =
     * field.getName(); if (!fieldCache_.containsKey(fname)) {
     * fieldCache_.put(fname, field); } } } }
     */
    private void setupFields() {
        setupFields(_beanClass);
    }

    private void setupFields(Class<?> targetClass) {
        if (targetClass.isInterface()) {
            setupFieldsByInterface(targetClass);
        } else {
            setupFieldsByClass(targetClass);
        }
    }

    private void setupFieldsByInterface(Class<?> interfaceClass) {
        addFields(interfaceClass);
        final Class<?>[] interfaces = interfaceClass.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            setupFieldsByInterface(interfaces[i]);
        }
    }

    private void addFields(Class<?> clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            final Field field = fields[i];
            final String fname = field.getName();
            if (_fieldMap.containsKey(fname)) { // target class's fields have priority  
                continue;
            }
            field.setAccessible(true);
            _fieldMap.put(fname, field);
            if (DfReflectionUtil.isInstanceVariableField(field)) {
                if (hasPropertyDesc(fname)) {
                    final DfPropertyDesc pd = getPropertyDesc(fname);
                    pd.setField(field);
                } else if (DfReflectionUtil.isPublicField(field)) {
                    final DfPropertyDesc pd = new DfPropertyDescImpl(fname, field.getType(), null, null, field, this);
                    _propertyDescMap.put(fname, pd);
                }
            }
        }
    }

    private void setupFieldsByClass(Class<?> targetClass) {
        addFields(targetClass); // should be set up at first
        final Class<?>[] interfaces = targetClass.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            setupFieldsByInterface(interfaces[i]);
        }
        final Class<?> superClass = targetClass.getSuperclass();
        if (superClass != Object.class && superClass != null) {
            setupFieldsByClass(superClass);
        }
    }
}
