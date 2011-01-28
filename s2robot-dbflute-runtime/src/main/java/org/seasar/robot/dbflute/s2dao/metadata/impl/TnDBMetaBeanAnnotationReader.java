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
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.seasar.robot.dbflute.util.DfReflectionUtil;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnDBMetaBeanAnnotationReader implements TnBeanAnnotationReader {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String VALUE_TYPE_SUFFIX = TnFieldBeanAnnotationReader.VALUE_TYPE_SUFFIX;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Class<?> _beanClass;
    protected final boolean _simpleType;
    protected final TnFieldBeanAnnotationReader _fieldBeanAnnotationReader;
    protected final DBMeta _dbmeta;
    protected final DfBeanDesc _beanDesc;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnDBMetaBeanAnnotationReader(Class<?> beanClass) {
        _beanClass = beanClass;
        _simpleType = isSimpleType(beanClass);
        if (_simpleType) {
            _fieldBeanAnnotationReader = null;
            _dbmeta = null;
            _beanDesc = null;
        } else if (!Entity.class.isAssignableFrom(beanClass)) {
            _fieldBeanAnnotationReader = new TnFieldBeanAnnotationReader(beanClass);
            _dbmeta = null;
            _beanDesc = null;
        } else { // mainly here for DBFlute
            _fieldBeanAnnotationReader = null;
            _dbmeta = ((Entity) DfReflectionUtil.newInstance(beanClass)).getDBMeta();
            _beanDesc = DfBeanDescFactory.getBeanDesc(beanClass);
        }
    }

    protected boolean isSimpleType(Class<?> clazz) {
        return clazz == String.class || clazz.isPrimitive() || clazz == Boolean.class || clazz == Character.class
                || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz)
                || Calendar.class.isAssignableFrom(clazz);
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    /**
     * {@inheritDoc}}
     */
    public String getColumnAnnotation(DfPropertyDesc pd) {
        if (_simpleType) {
            return null;
        }
        if (_fieldBeanAnnotationReader != null) {
            return _fieldBeanAnnotationReader.getColumnAnnotation(pd);
        }
        if (!_dbmeta.hasColumn(pd.getPropertyName())) {
            return null;
        }
        return _dbmeta.findColumnInfo(pd.getPropertyName()).getColumnDbName();
    }

    /**
     * {@inheritDoc}}
     */
    public String getTableAnnotation() {
        if (_simpleType) {
            return null;
        }
        if (_fieldBeanAnnotationReader != null) {
            return _fieldBeanAnnotationReader.getTableAnnotation();
        }
        return _dbmeta.getTableDbName();
    }

    public String getVersionNoPropertyName() {
        if (_simpleType) {
            return null;
        }
        if (_fieldBeanAnnotationReader != null) {
            return _fieldBeanAnnotationReader.getVersionNoPropertyName();
        }
        if (!_dbmeta.hasVersionNo()) {
            return null;
        }
        return _dbmeta.getVersionNoColumnInfo().getPropertyName();
    }

    public String getTimestampPropertyName() {
        if (_simpleType) {
            return null;
        }
        if (_fieldBeanAnnotationReader != null) {
            return _fieldBeanAnnotationReader.getTimestampPropertyName();
        }
        if (!_dbmeta.hasUpdateDate()) {
            return null;
        }
        return _dbmeta.getUpdateDateColumnInfo().getPropertyName();
    }

    public String getId(DfPropertyDesc pd) {
        if (_simpleType) {
            return null;
        }
        if (_fieldBeanAnnotationReader != null) {
            return _fieldBeanAnnotationReader.getId(pd);
        }
        if (!_dbmeta.hasColumn(pd.getPropertyName())) {
            return null;
        }
        final ColumnInfo columnInfo = _dbmeta.findColumnInfo(pd.getPropertyName());

        // Identity only here because Sequence is handled by an other component.
        if (_dbmeta.hasIdentity() && columnInfo.isAutoIncrement()) {
            return "identity";
        }
        return null;
    }

    public String getRelationKey(DfPropertyDesc pd) {
        if (_simpleType) {
            return null;
        }
        if (_fieldBeanAnnotationReader != null) {
            return _fieldBeanAnnotationReader.getRelationKey(pd);
        }
        if (!_dbmeta.hasForeign(pd.getPropertyName())) {
            return null;
        }
        final ForeignInfo foreignInfo = _dbmeta.findForeignInfo(pd.getPropertyName());
        final Map<ColumnInfo, ColumnInfo> localForeignColumnInfoMap = foreignInfo.getLocalForeignColumnInfoMap();
        final Set<ColumnInfo> keySet = localForeignColumnInfoMap.keySet();
        final StringBuilder sb = new StringBuilder();
        for (ColumnInfo localColumnInfo : keySet) {
            final ColumnInfo foreignColumnInfo = localForeignColumnInfoMap.get(localColumnInfo);
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(localColumnInfo.getColumnDbName());
            sb.append(":").append(foreignColumnInfo.getColumnDbName());
        }
        return sb.toString();
    }

    public int getRelationNo(DfPropertyDesc pd) {
        if (_simpleType) {
            return 0;
        }
        if (_fieldBeanAnnotationReader != null) {
            return _fieldBeanAnnotationReader.getRelationNo(pd);
        }
        final ForeignInfo foreignInfo = _dbmeta.findForeignInfo(pd.getPropertyName());
        return foreignInfo.getRelationNo();
    }

    public boolean hasRelationNo(DfPropertyDesc pd) {
        if (_simpleType) {
            return false;
        }
        if (_fieldBeanAnnotationReader != null) {
            return _fieldBeanAnnotationReader.hasRelationNo(pd);
        }
        return _dbmeta.hasForeign(pd.getPropertyName());
    }

    public String getValueType(DfPropertyDesc pd) {
        if (_simpleType) {
            return null;
        }
        if (_fieldBeanAnnotationReader != null) {
            return _fieldBeanAnnotationReader.getValueType(pd);
        }

        // ValueType is for user customization so this should not be handled by DBMeta.
        final String valueTypeKey = pd.getPropertyName() + VALUE_TYPE_SUFFIX;
        if (_beanDesc.hasField(valueTypeKey)) {
            Field field = _beanDesc.getField(valueTypeKey);
            return (String) DfReflectionUtil.getValue(field, null);
        }
        return null;
    }
}
