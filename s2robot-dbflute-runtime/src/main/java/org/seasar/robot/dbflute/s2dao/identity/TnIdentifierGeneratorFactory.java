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
package org.seasar.robot.dbflute.s2dao.identity;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.util.DfReflectionUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnIdentifierGeneratorFactory {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private static Map<String, Class<?>> generatorClasses = new HashMap<String, Class<?>>();

    static {
        addIdentifierGeneratorClass("assigned", TnIdentifierAssignedGenerator.class);
        addIdentifierGeneratorClass("identity", TnIdentifierIdentityGenerator.class);
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    private TnIdentifierGeneratorFactory() {
    }

    // ===================================================================================
    //                                                                Identifier Generator
    //                                                                ====================
    public static void addIdentifierGeneratorClass(String name, Class<?> clazz) {
        generatorClasses.put(name, clazz);
    }

    public static TnIdentifierGenerator createIdentifierGenerator(TnPropertyType propertyType) {
        return createIdentifierGenerator(propertyType, null);
    }

    public static TnIdentifierGenerator createIdentifierGenerator(TnPropertyType propertyType, String annotation) {
        if (propertyType == null) {
            String msg = "The argument[propertyType] should not be null: annotation=" + annotation;
            throw new IllegalArgumentException(msg);
        }
        if (annotation == null) {
            return new TnIdentifierAssignedGenerator(propertyType);
        }
        String[] array = tokenize(annotation, "=, ");
        Class<?> clazz = getGeneratorClass(array[0]);
        TnIdentifierGenerator generator = createIdentifierGenerator(clazz, propertyType);
        for (int i = 1; i < array.length; i += 2) {
            setProperty(generator, array[i].trim(), array[i + 1].trim());
        }
        return generator;
    }

    protected static String[] tokenize(final String str, final String delimiter) {
        if (str == null || str.trim().length() == 0) {
            return new String[] {};
        }
        final List<String> list = new ArrayList<String>();
        final StringTokenizer st = new StringTokenizer(str, delimiter);
        while (st.hasMoreElements()) {
            list.add(st.nextToken());
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    protected static Class<?> getGeneratorClass(String name) {
        Class<?> clazz = generatorClasses.get(name);
        if (clazz != null) {
            return clazz;
        }
        return DfReflectionUtil.forName(name);
    }

    protected static TnIdentifierGenerator createIdentifierGenerator(Class<?> clazz, TnPropertyType propertyType) {
        Constructor<?> constructor = DfReflectionUtil.getConstructor(clazz, new Class<?>[] { TnPropertyType.class });
        return (TnIdentifierGenerator) DfReflectionUtil.newInstance(constructor, new Object[] { propertyType });
    }

    protected static void setProperty(TnIdentifierGenerator generator, String propertyName, String value) {
        DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(generator.getClass());
        DfPropertyDesc pd = beanDesc.getPropertyDesc(propertyName);
        pd.setValue(generator, value);
    }
}
