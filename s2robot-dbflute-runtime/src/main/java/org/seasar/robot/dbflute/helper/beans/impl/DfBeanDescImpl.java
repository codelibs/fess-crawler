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
package org.seasar.robot.dbflute.helper.beans.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.seasar.robot.dbflute.helper.StringKeyMap;
import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanConstructorNotFoundException;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanFieldNotFoundException;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanMethodNotFoundException;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanPropertyNotFoundException;
import org.seasar.robot.dbflute.util.DfReflectionUtil;
import org.seasar.robot.dbflute.util.DfStringUtil;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * {Refers to Seasar and Extends its class}
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
    private Class<?> beanClass;
    private Constructor<?>[] constructors;

    private StringKeyMap<DfPropertyDesc> propertyDescMap = StringKeyMap.createAsCaseInsensitive();
    private Map<String, Method[]> methodsMap = new ConcurrentHashMap<String, Method[]>();
    private Map<String, Field> fieldMap = new ConcurrentHashMap<String, Field>();

    private transient Set<String> invalidPropertyNames = new HashSet<String>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfBeanDescImpl(Class<?> beanClass) {
        if (beanClass == null) {
            String msg = "The argument 'beanClass' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        this.beanClass = beanClass;
        constructors = beanClass.getConstructors();
        setupPropertyDescs();
        setupMethods();
        setupFields();
    }

    // ===================================================================================
    //                                                                                Bean
    //                                                                                ====
    public Class<?> getBeanClass() {
        return beanClass;
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
        throw new DfBeanConstructorNotFoundException(beanClass, args);
    }

    public Constructor<?> getConstructor(final Class<?>[] paramTypes) {
        for (int i = 0; i < constructors.length; ++i) {
            if (Arrays.equals(paramTypes, constructors[i].getParameterTypes())) {
                return constructors[i];
            }
        }
        throw new DfBeanConstructorNotFoundException(beanClass, paramTypes);
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
            throw new DfBeanPropertyNotFoundException(beanClass, propertyName);
        }
        return pd;
    }

    private DfPropertyDesc getPropertyDescInternally(String propertyName) {
        return propertyDescMap.get(propertyName);
    }

    public int getPropertyDescSize() {
        return propertyDescMap.size();
    }

    public List<String> getProppertyNameList() {
        return new ArrayList<String>(propertyDescMap.keySet());
    }

    // ===================================================================================
    //                                                                               Field
    //                                                                               =====
    public boolean hasField(String fieldName) {
        return fieldMap.get(fieldName) != null;
    }

    public Field getField(String fieldName) {
        Field field = (Field) fieldMap.get(fieldName);
        if (field == null) {
            throw new DfBeanFieldNotFoundException(beanClass, fieldName);
        }
        return field;
    }

    public int getFieldSize() {
        return fieldMap.size();
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
        throw new DfBeanMethodNotFoundException(beanClass, methodName, paramTypes);
    }

    public Method getMethodNoException(final String methodName, final Class<?>[] paramTypes) {
        final Method[] methods = (Method[]) methodsMap.get(methodName);
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

        Method[] methods = (Method[]) methodsMap.get(methodName);
        if (methods == null) {
            throw new DfBeanMethodNotFoundException(beanClass, methodName, null);
        }
        return methods;
    }

    public boolean hasMethod(String methodName) {
        return methodsMap.get(methodName) != null;
    }

    public String[] getMethodNames() {
        return (String[]) methodsMap.keySet().toArray(new String[methodsMap.size()]);
    }

    //
    //    public String[] getConstructorParameterNames(final Class[] parameterTypes) {
    //        return getConstructorParameterNames(getConstructor(parameterTypes));
    //    }
    //
    //    public String[] getConstructorParameterNames(final Constructor constructor) {
    //        if (constructorParameterNamesCache == null) {
    //            constructorParameterNamesCache = createConstructorParameterNamesCache();
    //        }
    //
    //        if (!constructorParameterNamesCache.containsKey(constructor)) {
    //            throw new ConstructorNotFoundRuntimeException(beanClass, constructor.getParameterTypes());
    //        }
    //        return (String[]) constructorParameterNamesCache.get(constructor);
    //
    //    }
    //
    //    public String[] getMethodParameterNamesNoException(final String methodName, final Class[] parameterTypes) {
    //        return getMethodParameterNamesNoException(getMethod(methodName, parameterTypes));
    //    }
    //
    //    public String[] getMethodParameterNames(final String methodName, final Class[] parameterTypes) {
    //        return getMethodParameterNames(getMethod(methodName, parameterTypes));
    //    }
    //
    //    public String[] getMethodParameterNames(final Method method) {
    //        String[] names = getMethodParameterNamesNoException(method);
    //        if (names == null || names.length != method.getParameterTypes().length) {
    //            throw new IllegalDiiguRuntimeException();
    //        }
    //        return names;
    //    }
    //
    //    public String[] getMethodParameterNamesNoException(final Method method) {
    //        if (methodParameterNamesCache == null) {
    //            methodParameterNamesCache = createMethodParameterNamesCache();
    //        }
    //
    //        if (!methodParameterNamesCache.containsKey(method)) {
    //            throw new MethodNotFoundRuntimeException(beanClass, method.getName(), method.getParameterTypes());
    //        }
    //        return (String[]) methodParameterNamesCache.get(method);
    //    }
    //
    //    private Map createConstructorParameterNamesCache() {
    //        final Map map = new HashMap();
    //        final ClassPool pool = ClassPoolUtil.getClassPool(beanClass);
    //        for (int i = 0; i < constructors.length; ++i) {
    //            final Constructor constructor = constructors[i];
    //            if (constructor.getParameterTypes().length == 0) {
    //                map.put(constructor, EMPTY_STRING_ARRAY);
    //                continue;
    //            }
    //            final CtClass clazz = ClassPoolUtil.toCtClass(pool, constructor.getDeclaringClass());
    //            final CtClass[] parameterTypes = ClassPoolUtil.toCtClassArray(pool, constructor.getParameterTypes());
    //            try {
    //                final String[] names = getParameterNames(clazz.getDeclaredConstructor(parameterTypes));
    //                map.put(constructor, names);
    //            } catch (final NotFoundException e) {
    //                _log.debug("The constructor was not found: class=" + beanClass.getName() + " constructor="
    //                        + constructor);
    //            }
    //        }
    //        return map;
    //    }
    //
    //    private Map createMethodParameterNamesCache() {
    //        final Map map = new HashMap();
    //        final ClassPool pool = ClassPoolUtil.getClassPool(beanClass);
    //        for (final Iterator it = methodsCache.values().iterator(); it.hasNext();) {
    //            final Method[] methods = (Method[]) it.next();
    //            for (int i = 0; i < methods.length; ++i) {
    //                final Method method = methods[i];
    //                if (method.getParameterTypes().length == 0) {
    //                    map.put(methods[i], EMPTY_STRING_ARRAY);
    //                    continue;
    //                }
    //                final CtClass clazz = ClassPoolUtil.toCtClass(pool, method.getDeclaringClass());
    //                final CtClass[] parameterTypes = ClassPoolUtil.toCtClassArray(pool, method.getParameterTypes());
    //                try {
    //                    final String[] names = getParameterNames(clazz.getDeclaredMethod(method.getName(), parameterTypes));
    //                    map.put(methods[i], names);
    //                } catch (final NotFoundException e) {
    //                    _log.debug("The method was not found: class=" + beanClass.getName() + " method=" + method);
    //                }
    //            }
    //        }
    //        return map;
    //    }
    //
    //    private String[] getParameterNames(final CtBehavior behavior) throws NotFoundException {
    //        final MethodInfo methodInfo = behavior.getMethodInfo();
    //        final ParameterAnnotationsAttribute attribute = (ParameterAnnotationsAttribute) methodInfo
    //                .getAttribute(ParameterAnnotationsAttribute.visibleTag);
    //        if (attribute == null) {
    //            return null;
    //        }
    //        final int numParameters = behavior.getParameterTypes().length;
    //        final String[] parameterNames = new String[numParameters];
    //        final Annotation[][] annotationsArray = attribute.getAnnotations();
    //        if (annotationsArray == null || annotationsArray.length != numParameters) {
    //            return null;
    //        }
    //        for (int i = 0; i < numParameters; ++i) {
    //            final String parameterName = getParameterName(annotationsArray[i]);
    //            if (parameterName == null) {
    //                return null;
    //            }
    //            parameterNames[i] = parameterName;
    //        }
    //        return parameterNames;
    //    }
    //
    //    private String getParameterName(final Annotation[] annotations) {
    //        Annotation nameAnnotation = null;
    //        for (int i = 0; i < annotations.length; ++i) {
    //            final Annotation annotation = annotations[i];
    //            if (PARAMETER_NAME_ANNOTATION.equals(annotation.getTypeName())) {
    //                nameAnnotation = annotation;
    //                break;
    //            }
    //        }
    //        if (nameAnnotation == null) {
    //            return null;
    //        }
    //        return ((StringMemberValue) nameAnnotation.getMemberValue("value")).getValue();
    //    }

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
        outerLoop: for (int i = 0; i < constructors.length; ++i) {
            Class<?>[] paramTypes = constructors[i].getParameterTypes();
            if (paramTypes.length != args.length) {
                continue;
            }
            for (int j = 0; j < args.length; ++j) {
                if (args[j] == null || DfReflectionUtil.isAssignableFrom(paramTypes[j], args[j].getClass())) {
                    continue;
                }
                continue outerLoop;
            }
            return constructors[i];
        }
        return null;
    }

    private Constructor<?> findSuitableConstructorAdjustNumber(Object[] args) {
        outerLoop: for (int i = 0; i < constructors.length; ++i) {
            Class<?>[] paramTypes = constructors[i].getParameterTypes();
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
            return constructors[i];
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
        Method[] methods = beanClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            if (DfReflectionUtil.isBridgeMethod(m) || DfReflectionUtil.isSyntheticMethod(m)) {
                continue;
            }
            String methodName = m.getName();
            if (methodName.startsWith("get")) {
                if (m.getParameterTypes().length != 0 || methodName.equals("getClass")
                        || m.getReturnType() == void.class) {
                    continue;
                }
                String propertyName = decapitalizePropertyName(methodName.substring(3));
                setupReadMethod(m, propertyName);
            } else if (methodName.startsWith("is")) {
                if (m.getParameterTypes().length != 0 || !m.getReturnType().equals(Boolean.TYPE)
                        && !m.getReturnType().equals(Boolean.class)) {
                    continue;
                }
                String propertyName = decapitalizePropertyName(methodName.substring(2));
                setupReadMethod(m, propertyName);
            } else if (methodName.startsWith("set")) {
                if (m.getParameterTypes().length != 1 || methodName.equals("setClass")
                        || m.getReturnType() != void.class) {
                    continue;
                }
                String propertyName = decapitalizePropertyName(methodName.substring(3));
                setupWriteMethod(m, propertyName);
            }
        }
        for (Iterator<String> i = invalidPropertyNames.iterator(); i.hasNext();) {
            propertyDescMap.remove(i.next());
        }
        invalidPropertyNames.clear();
    }

    private static String decapitalizePropertyName(String name) {
        if (DfStringUtil.isNullOrEmpty(name)) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))) {

            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    private void addPropertyDesc(DfPropertyDesc propertyDesc) {
        if (propertyDesc == null) {
            String msg = "The argument 'propertyDesc' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        propertyDescMap.put(propertyDesc.getPropertyName(), propertyDesc);
    }

    private void setupReadMethod(Method readMethod, String propertyName) {
        Class<?> propertyType = readMethod.getReturnType();
        DfPropertyDesc propDesc = getPropertyDescInternally(propertyName);
        if (propDesc != null) {
            if (!propDesc.getPropertyType().equals(propertyType)) {
                invalidPropertyNames.add(propertyName);
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
                invalidPropertyNames.add(propertyName);
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
        throw new DfBeanMethodNotFoundException(beanClass, methodName, args);
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
        final Method[] methods = beanClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
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
        final Set<Entry<String,List<Method>>> entrySet = methodListMap.entrySet();
        for (Entry<String, List<Method>> entry : entrySet) {
            final String key = entry.getKey();
            final List<Method> methodList = entry.getValue();
            methodsMap.put(key, methodList.toArray(new Method[methodList.size()]));
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
        setupFields(beanClass);
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
        Class<?>[] interfaces = interfaceClass.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            setupFieldsByInterface(interfaces[i]);
        }
    }

    private void addFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            String fname = field.getName();
            if (!fieldMap.containsKey(fname)) {
                field.setAccessible(true);
                fieldMap.put(fname, field);
                if (DfReflectionUtil.isInstanceField(field)) {
                    if (hasPropertyDesc(fname)) {
                        DfPropertyDesc pd = getPropertyDesc(field.getName());
                        pd.setField(field);
                    } else if (DfReflectionUtil.isPublicField(field)) {
                        DfPropertyDesc pd = new DfPropertyDescImpl(field.getName(), field.getType(), null, null, field,
                                this);
                        propertyDescMap.put(fname, pd);
                    }
                }
            }
        }
    }

    private void setupFieldsByClass(Class<?> targetClass) {
        addFields(targetClass);
        Class<?>[] interfaces = targetClass.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            setupFieldsByInterface(interfaces[i]);
        }
        Class<?> superClass = targetClass.getSuperclass();
        if (superClass != Object.class && superClass != null) {
            setupFieldsByClass(superClass);
        }
    }
}
