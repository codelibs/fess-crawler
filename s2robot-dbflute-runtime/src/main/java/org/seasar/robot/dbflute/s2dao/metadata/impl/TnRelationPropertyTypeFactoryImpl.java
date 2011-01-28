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

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaDataFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyTypeFactory;
import org.seasar.robot.dbflute.util.DfCollectionUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnRelationPropertyTypeFactoryImpl implements TnRelationPropertyTypeFactory {

    protected Class<?> _beanClass;

    protected TnBeanAnnotationReader _beanAnnotationReader;

    protected TnBeanMetaDataFactory _beanMetaDataFactory;

    protected DatabaseMetaData _databaseMetaData;

    protected int _relationNestLevel;

    protected boolean _stopRelationCreation;

    public TnRelationPropertyTypeFactoryImpl(Class<?> beanClass, TnBeanAnnotationReader beanAnnotationReader,
            TnBeanMetaDataFactory beanMetaDataFactory, DatabaseMetaData databaseMetaData, int relationNestLevel,
            boolean stopRelationCreation) {
        this._beanClass = beanClass;
        this._beanAnnotationReader = beanAnnotationReader;
        this._beanMetaDataFactory = beanMetaDataFactory;
        this._databaseMetaData = databaseMetaData;
        this._relationNestLevel = relationNestLevel;
        this._stopRelationCreation = stopRelationCreation;
    }

    public TnRelationPropertyType[] createRelationPropertyTypes() {
        final List<TnRelationPropertyType> list = new ArrayList<TnRelationPropertyType>();
        final DfBeanDesc beanDesc = getBeanDesc();
        final List<String> proppertyNameList = beanDesc.getProppertyNameList();
        for (String proppertyName : proppertyNameList) {
            final DfPropertyDesc pd = beanDesc.getPropertyDesc(proppertyName);
            if (_stopRelationCreation || !isRelationProperty(pd)) {
                continue;
            }
            TnRelationPropertyType rpt = createRelationPropertyType(pd);
            list.add(rpt);
        }
        return (TnRelationPropertyType[]) list.toArray(new TnRelationPropertyType[list.size()]);
    }

    protected TnRelationPropertyType createRelationPropertyType(DfPropertyDesc propertyDesc) {
        final String[] myKeys;
        final String[] yourKeys;
        final int relno = _beanAnnotationReader.getRelationNo(propertyDesc);
        final String relkeys = _beanAnnotationReader.getRelationKey(propertyDesc);
        if (relkeys != null) {
            final List<String> myKeyList = DfCollectionUtil.newArrayList();
            final List<String> yourKeyList = DfCollectionUtil.newArrayList();
            final List<String> tokenList = Srl.splitListTrimmed(relkeys, ",");
            for (String token : tokenList) {
                final int index = token.indexOf(':');
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
        } else {
            myKeys = new String[0];
            yourKeys = new String[0];
        }
        final TnBeanMetaData beanMetaData = createRelationBeanMetaData(propertyDesc.getPropertyType());
        final DfPropertyDesc pd = propertyDesc;
        final TnRelationPropertyType rpt = new TnRelationPropertyTypeImpl(pd, relno, myKeys, yourKeys, beanMetaData);
        return rpt;
    }

    protected TnBeanMetaData createRelationBeanMetaData(final Class<?> relationBeanClass) {
        return _beanMetaDataFactory.createBeanMetaData(_databaseMetaData, relationBeanClass, _relationNestLevel + 1);
    }

    protected boolean isRelationProperty(DfPropertyDesc propertyDesc) {
        return _beanAnnotationReader.hasRelationNo(propertyDesc);
    }

    protected DfBeanDesc getBeanDesc() {
        return DfBeanDescFactory.getBeanDesc(_beanClass);
    }

}
