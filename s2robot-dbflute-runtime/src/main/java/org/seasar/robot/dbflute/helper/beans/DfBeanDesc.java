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
package org.seasar.robot.dbflute.helper.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.seasar.robot.dbflute.helper.beans.exception.DfBeanConstructorNotFoundException;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanFieldNotFoundException;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanMethodNotFoundException;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanPropertyNotFoundException;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public interface DfBeanDesc {

    // ===================================================================================
    //                                                                                Bean
    //                                                                                ====
    /**
     * @return The class for bean. (NotNull)
     */
    Class<?> getBeanClass();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    Constructor<?> getSuitableConstructor(Object[] args) throws DfBeanConstructorNotFoundException;

    Constructor<?> getConstructor(Class<?>[] paramTypes);

    // ===================================================================================
    //                                                                            Property
    //                                                                            ========
    boolean hasPropertyDesc(String propertyName);

    DfPropertyDesc getPropertyDesc(String propertyName) throws DfBeanPropertyNotFoundException;

    int getPropertyDescSize();

    List<String> getProppertyNameList();

    // ===================================================================================
    //                                                                               Field
    //                                                                               =====
    boolean hasField(String fieldName);

    Field getField(String fieldName) throws DfBeanFieldNotFoundException;

    int getFieldSize();

    // ===================================================================================
    //                                                                              Method
    //                                                                              ======
    Method getMethod(String methodName) throws DfBeanMethodNotFoundException;

    Method getMethod(String methodName, Class<?>[] paramTypes) throws DfBeanMethodNotFoundException;

    Method getMethodNoException(String methodName);

    Method getMethodNoException(String methodName, Class<?>[] paramTypes);

    Method[] getMethods(String methodName) throws DfBeanMethodNotFoundException;

    boolean hasMethod(String methodName);

    String[] getMethodNames();

    // ===================================================================================
    //                                                                          Reflection
    //                                                                          ==========
    Object newInstance(Object[] args) throws DfBeanConstructorNotFoundException;

    Object invoke(Object target, String methodName, Object[] args) throws DfBeanMethodNotFoundException;

    Object getFieldValue(String fieldName, Object target) throws DfBeanFieldNotFoundException;
}