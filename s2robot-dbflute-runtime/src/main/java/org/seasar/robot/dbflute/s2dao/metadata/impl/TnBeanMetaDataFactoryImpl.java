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
package org.seasar.robot.dbflute.s2dao.metadata.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.robot.dbflute.resource.SQLExceptionHandler;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaDataFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnModifiedPropertySupport;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyTypeFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyTypeFactoryBuilder;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyTypeFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyTypeFactoryBuilder;
import org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypeFactory;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnBeanMetaDataFactoryImpl implements TnBeanMetaDataFactory {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;
    protected TnValueTypeFactory _valueTypeFactory;

    // ===================================================================================
    //                                                                            Creation
    //                                                                            ========
    public TnBeanMetaData createBeanMetaData(final Class<?> daoInterface, final Class<?> beanClass) {
        return createBeanMetaData(beanClass);
    }

    public TnBeanMetaData createBeanMetaData(final Class<?> beanClass) {
        return createBeanMetaData(beanClass, 0);
    }

    public TnBeanMetaData createBeanMetaData(final Class<?> beanClass, final int relationNestLevel) {
        if (beanClass == null) {
            throw new NullPointerException("beanClass");
        }
        Connection conn = null;
        try {
            conn = _dataSource.getConnection();
            final DatabaseMetaData metaData = conn.getMetaData();
            return createBeanMetaData(metaData, beanClass, relationNestLevel);
        } catch (SQLException e) {
            handleSQLException(e);
            return null; // unreachable
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    handleSQLException(e);
                }
            }
        }
    }

    protected void handleSQLException(SQLException e) {
        new SQLExceptionHandler().handleSQLException(e);
    }

    public TnBeanMetaData createBeanMetaData(final DatabaseMetaData dbMetaData, final Class<?> beanClass,
            final int relationNestLevel) {
        if (beanClass == null) {
            throw new NullPointerException("beanClass");
        }
        final boolean stopRelationCreation = isLimitRelationNestLevel(relationNestLevel);
        final TnBeanAnnotationReader bar = createBeanAnnotationReader(beanClass);
        final String versionNoPropertyName = getVersionNoPropertyName(bar);
        final String timestampPropertyName = getTimestampPropertyName(bar);
        final TnPropertyTypeFactory ptf = createPropertyTypeFactory(beanClass, bar, dbMetaData);
        final TnRelationPropertyTypeFactory rptf = createRelationPropertyTypeFactory(beanClass, bar, dbMetaData,
                relationNestLevel, stopRelationCreation);
        final TnBeanMetaDataImpl bmd = createBeanMetaDataImpl();

        bmd.setBeanAnnotationReader(bar);
        bmd.setVersionNoPropertyName(versionNoPropertyName);
        bmd.setTimestampPropertyName(timestampPropertyName);
        bmd.setBeanClass(beanClass);
        bmd.setPropertyTypeFactory(ptf);
        bmd.setRelationPropertyTypeFactory(rptf);
        bmd.initialize();

        bmd.setModifiedPropertySupport(new TnModifiedPropertySupport() {
            @SuppressWarnings("unchecked")
            public Set<String> getModifiedPropertyNames(Object bean) {
                DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(bean.getClass());
                String propertyName = "modifiedPropertyNames";
                if (!beanDesc.hasPropertyDesc(propertyName)) {
                    return Collections.EMPTY_SET;
                } else {
                    DfPropertyDesc propertyDesc = beanDesc.getPropertyDesc(propertyName);
                    Object value = propertyDesc.getValue(bean);
                    Set<String> names = (Set<String>) value;
                    return names;
                }
            }

        });

        return bmd;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected TnBeanAnnotationReader createBeanAnnotationReader(Class<?> beanClass) {
        return new TnDBMetaBeanAnnotationReader(beanClass);
    }

    protected String getVersionNoPropertyName(TnBeanAnnotationReader beanAnnotationReader) {
        final String defaultName = "versionNo";
        final String name = beanAnnotationReader.getVersionNoPropertyName();
        return name != null ? name : defaultName;
    }

    protected String getTimestampPropertyName(TnBeanAnnotationReader beanAnnotationReader) {
        final String defaultName = "timestamp";
        final String name = beanAnnotationReader.getTimestampPropertyName();
        return name != null ? name : defaultName;
    }

    protected TnPropertyTypeFactory createPropertyTypeFactory(Class<?> originalBeanClass,
            TnBeanAnnotationReader beanAnnotationReader, DatabaseMetaData databaseMetaData) {
        return createPropertyTypeFactoryBuilder().build(originalBeanClass, beanAnnotationReader);
    }

    protected TnPropertyTypeFactoryBuilder createPropertyTypeFactoryBuilder() {
        TnPropertyTypeFactoryBuilderImpl impl = new TnPropertyTypeFactoryBuilderImpl();
        impl.setValueTypeFactory(_valueTypeFactory);
        return impl;
    }

    protected TnRelationPropertyTypeFactory createRelationPropertyTypeFactory(Class<?> originalBeanClass,
            TnBeanAnnotationReader beanAnnotationReader, DatabaseMetaData databaseMetaData, int relationNestLevel,
            boolean isStopRelationCreation) {
        return createRelationPropertyTypeFactoryBuilder().build(originalBeanClass, beanAnnotationReader,
                databaseMetaData, relationNestLevel, isStopRelationCreation);
    }

    protected TnRelationPropertyTypeFactoryBuilder createRelationPropertyTypeFactoryBuilder() {
        TnRelationPropertyTypeFactoryBuilderImpl impl = new TnRelationPropertyTypeFactoryBuilderImpl();
        impl.setBeanMetaDataFactory(this);
        return impl;
    }

    protected TnBeanMetaDataImpl createBeanMetaDataImpl() {
        return new TnBeanMetaDataImpl();
    }

    protected boolean isLimitRelationNestLevel(final int relationNestLevel) {
        return relationNestLevel == getLimitRelationNestLevel();
    }

    protected int getLimitRelationNestLevel() {
        // You can change relation creation range by changing this.
        return 1;
    }

    public void setDataSource(final DataSource dataSource) {
        this._dataSource = dataSource;
    }

    public void setValueTypeFactory(TnValueTypeFactory valueTypeFactory) {
        this._valueTypeFactory = valueTypeFactory;
    }
}
