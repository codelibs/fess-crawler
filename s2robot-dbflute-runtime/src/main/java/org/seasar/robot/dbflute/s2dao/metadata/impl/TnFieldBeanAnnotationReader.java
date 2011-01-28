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
package org.seasar.robot.dbflute.s2dao.metadata.impl;

import java.lang.reflect.Field;

import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.seasar.robot.dbflute.util.DfReflectionUtil;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnFieldBeanAnnotationReader implements TnBeanAnnotationReader {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String TABLE = "TABLE";
    public static final String RELNO_SUFFIX = "_RELNO";
    public static final String RELKEYS_SUFFIX = "_RELKEYS";
    public static final String ID_SUFFIX = "_ID";
    public static final String NO_PERSISTENT_PROPS = "NO_PERSISTENT_PROPS";
    public static final String VERSION_NO_PROPERTY = "VERSION_NO_PROPERTY";
    public static final String TIMESTAMP_PROPERTY = "TIMESTAMP_PROPERTY";
    public static final String COLUMN_SUFFIX = "_COLUMN";
    public static final String VALUE_TYPE_SUFFIX = "_VALUE_TYPE";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private DfBeanDesc _beanDesc;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnFieldBeanAnnotationReader(Class<?> beanClass) {
        _beanDesc = DfBeanDescFactory.getBeanDesc(beanClass);
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    public String getColumnAnnotation(DfPropertyDesc pd) {
        final String propertyName = pd.getPropertyName();
        final String columnNameKey = propertyName + COLUMN_SUFFIX;
        return getField(columnNameKey);
    }

    public String getTableAnnotation() {
        if (_beanDesc.hasField(TABLE)) {
            final Field field = _beanDesc.getField(TABLE);
            return (String) DfReflectionUtil.getValue(field, null);
        }
        return null;
    }

    public String getVersionNoPropertyName() {
        if (_beanDesc.hasField(VERSION_NO_PROPERTY)) {
            final Field field = _beanDesc.getField(VERSION_NO_PROPERTY);
            return (String) DfReflectionUtil.getValue(field, null);
        }
        return null;
    }

    public String getTimestampPropertyName() {
        if (_beanDesc.hasField(TIMESTAMP_PROPERTY)) {
            final Field field = _beanDesc.getField(TIMESTAMP_PROPERTY);
            return (String) DfReflectionUtil.getValue(field, null);
        }
        return null;
    }

    public String getId(DfPropertyDesc pd) {
        final String id = getField(pd.getPropertyName() + ID_SUFFIX);
        if (id != null) {
            return id;
        }
        return getField(pd.getPropertyName() + ID_SUFFIX);
    }

    public String getRelationKey(DfPropertyDesc pd) {
        final String propertyName = pd.getPropertyName();
        final String relkeysKey = propertyName + RELKEYS_SUFFIX;
        return getField(relkeysKey);
    }

    public int getRelationNo(DfPropertyDesc pd) {
        final String relnoKey = pd.getPropertyName() + RELNO_SUFFIX;
        final Field field = _beanDesc.getField(relnoKey);
        return (Integer) DfReflectionUtil.getValue(field, null);
    }

    public boolean hasRelationNo(DfPropertyDesc pd) {
        final String relnoKey = pd.getPropertyName() + RELNO_SUFFIX;
        return _beanDesc.hasField(relnoKey);
    }

    public String getValueType(DfPropertyDesc pd) {
        final String valueTypeKey = pd.getPropertyName() + VALUE_TYPE_SUFFIX;
        return getField(valueTypeKey);
    }

    private String getField(String key) {
        if (_beanDesc.hasField(key)) {
            final Field field = _beanDesc.getField(key);
            return (String) DfReflectionUtil.getValue(field, null);
        }
        return null;
    }
}
