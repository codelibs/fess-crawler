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

import java.util.ArrayList;
import java.util.List;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypeFactory;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnPropertyTypeFactoryImpl extends TnAbstractPropertyTypeFactory {

    protected DBMeta _dbmeta;

    public TnPropertyTypeFactoryImpl(Class<?> beanClass, TnBeanAnnotationReader beanAnnotationReader,
            TnValueTypeFactory valueTypeFactory) {
        super(beanClass, beanAnnotationReader, valueTypeFactory);
        initializeResources();
    }

    protected void initializeResources() {
        if (isEntity()) {
            _dbmeta = findDBMeta();
        }
    }

    protected boolean isEntity() {
        return Entity.class.isAssignableFrom(beanClass);
    }

    protected boolean hasDBMeta() {
        return _dbmeta != null;
    }

    protected DBMeta findDBMeta() {
        try {
            final Entity entity = (Entity) beanClass.newInstance();
            return entity.getDBMeta();
        } catch (Exception e) {
            String msg = "beanClass.newInstance() threw the exception: beanClass=" + beanClass;
            throw new RuntimeException(msg, e);
        }
    }

    public TnPropertyType[] createBeanPropertyTypes() {
        final List<TnPropertyType> list = new ArrayList<TnPropertyType>();
        final DfBeanDesc beanDesc = getBeanDesc();
        final List<String> proppertyNameList = beanDesc.getProppertyNameList();
        for (String proppertyName : proppertyNameList) {
            final DfPropertyDesc pd = beanDesc.getPropertyDesc(proppertyName);

            // Read-only property is unnecessary!
            if (!pd.hasWriteMethod()) {
                continue;
            }

            // Relation property is unnecessary!
            if (isRelation(pd)) {
                continue;
            }

            final TnPropertyType pt = createPropertyType(pd);
            pt.setPrimaryKey(isPrimaryKey(pd));
            pt.setPersistent(isPersistent(pt));
            list.add(pt);
        }
        return list.toArray(new TnPropertyType[list.size()]);
    }

    @Override
    protected boolean isRelation(DfPropertyDesc propertyDesc) {
        final String propertyName = propertyDesc.getPropertyName();
        if (hasDBMeta() && (_dbmeta.hasForeign(propertyName) || _dbmeta.hasReferrer(propertyName))) {
            return true;
        }
        return hasRelationNoAnnotation(propertyDesc);
    }

    protected boolean hasRelationNoAnnotation(DfPropertyDesc propertyDesc) {
        return beanAnnotationReader.hasRelationNo(propertyDesc);
    }

    @Override
    protected boolean isPrimaryKey(DfPropertyDesc propertyDesc) {
        final String propertyName = propertyDesc.getPropertyName();
        if (hasDBMeta() && _dbmeta.hasPrimaryKey() && _dbmeta.hasColumn(propertyName)) {
            if (_dbmeta.findColumnInfo(propertyName).isPrimary()) {
                return true;
            }
        }
        return hasIdAnnotation(propertyDesc);
    }

    protected boolean hasIdAnnotation(DfPropertyDesc propertyDesc) {
        return beanAnnotationReader.getId(propertyDesc) != null;
    }

    @Override
    protected boolean isPersistent(TnPropertyType propertyType) {
        final String propertyName = propertyType.getPropertyName();
        final DfPropertyDesc propertyDesc = propertyType.getPropertyDesc();
        if ((hasDBMeta() && _dbmeta.hasColumn(propertyName)) || hasColumnAnnotation(propertyDesc)) {
            return true;
        }
        return false;
    }

    protected boolean hasColumnAnnotation(DfPropertyDesc propertyDesc) {
        return beanAnnotationReader.getColumnAnnotation(propertyDesc) != null;
    }
}
