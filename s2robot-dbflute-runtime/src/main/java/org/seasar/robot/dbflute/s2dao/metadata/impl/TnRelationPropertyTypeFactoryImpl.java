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

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaDataFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyTypeFactory;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnRelationPropertyTypeFactoryImpl implements TnRelationPropertyTypeFactory {

    protected Class<?> beanClass;

    protected TnBeanAnnotationReader beanAnnotationReader;

    protected TnBeanMetaDataFactory beanMetaDataFactory;

    protected DatabaseMetaData databaseMetaData;

    protected int relationNestLevel;

    protected boolean isStopRelationCreation;

    public TnRelationPropertyTypeFactoryImpl(Class<?> beanClass, TnBeanAnnotationReader beanAnnotationReader,
            TnBeanMetaDataFactory beanMetaDataFactory, DatabaseMetaData databaseMetaData, int relationNestLevel,
            boolean isStopRelationCreation) {
        this.beanClass = beanClass;
        this.beanAnnotationReader = beanAnnotationReader;
        this.beanMetaDataFactory = beanMetaDataFactory;
        this.databaseMetaData = databaseMetaData;
        this.relationNestLevel = relationNestLevel;
        this.isStopRelationCreation = isStopRelationCreation;
    }

    public TnRelationPropertyType[] createRelationPropertyTypes() {
        final List<TnRelationPropertyType> list = new ArrayList<TnRelationPropertyType>();
        final DfBeanDesc beanDesc = getBeanDesc();
        final List<String> proppertyNameList = beanDesc.getProppertyNameList();
        for (String proppertyName : proppertyNameList) {
            final DfPropertyDesc pd = beanDesc.getPropertyDesc(proppertyName);
            if (isStopRelationCreation || !isRelationProperty(pd)) {
                continue;
            }
            TnRelationPropertyType rpt = createRelationPropertyType(pd);
            list.add(rpt);
        }
        return (TnRelationPropertyType[]) list.toArray(new TnRelationPropertyType[list.size()]);
    }

    protected TnRelationPropertyType createRelationPropertyType(DfPropertyDesc propertyDesc) {
        String[] myKeys = new String[0];
        String[] yourKeys = new String[0];
        int relno = beanAnnotationReader.getRelationNo(propertyDesc);
        String relkeys = beanAnnotationReader.getRelationKey(propertyDesc);
        if (relkeys != null) {
            StringTokenizer st = new StringTokenizer(relkeys, " \t\n\r\f,");
            List<String> myKeyList = new ArrayList<String>();
            List<String> yourKeyList = new ArrayList<String>();
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                int index = token.indexOf(':');
                if (index > 0) {
                    myKeyList.add(token.substring(0, index));
                    yourKeyList.add(token.substring(index + 1));
                } else {
                    myKeyList.add(token);
                    yourKeyList.add(token);
                }
            }
            myKeys = (String[]) myKeyList.toArray(new String[myKeyList.size()]);
            yourKeys = (String[]) yourKeyList.toArray(new String[yourKeyList.size()]);
        }
        final TnBeanMetaData beanMetaData = createRelationBeanMetaData(propertyDesc.getPropertyType());
        DfPropertyDesc pd = propertyDesc;
        final TnRelationPropertyType rpt = new TnRelationPropertyTypeImpl(pd, relno, myKeys, yourKeys, beanMetaData);
        return rpt;
    }

    protected TnBeanMetaData createRelationBeanMetaData(final Class<?> relationBeanClass) {
        return beanMetaDataFactory.createBeanMetaData(databaseMetaData, relationBeanClass, relationNestLevel + 1);
    }

    protected boolean isRelationProperty(DfPropertyDesc propertyDesc) {
        return beanAnnotationReader.hasRelationNo(propertyDesc);
    }

    protected DfBeanDesc getBeanDesc() {
        return DfBeanDescFactory.getBeanDesc(beanClass);
    }

}
