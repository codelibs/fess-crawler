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

import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyType;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnRelationPropertyTypeImpl extends TnPropertyTypeImpl implements TnRelationPropertyType {

    protected int relationNo;

    protected String[] myKeys;

    protected String[] yourKeys;

    protected TnBeanMetaData beanMetaData;

    public TnRelationPropertyTypeImpl(DfPropertyDesc propertyDesc) {
        super(propertyDesc);
    }

    public TnRelationPropertyTypeImpl(DfPropertyDesc propertyDesc, int relationNo, String[] myKeys, String[] yourKeys,
            TnBeanMetaData beanMetaData) {
        super(propertyDesc);
        this.relationNo = relationNo;
        this.myKeys = myKeys;
        this.yourKeys = yourKeys;
        this.beanMetaData = beanMetaData;
    }

    public int getRelationNo() {
        return relationNo;
    }

    public int getKeySize() {
        if (myKeys.length > 0) {
            return myKeys.length;
        } else {
            return beanMetaData.getPrimaryKeySize();
        }

    }

    public String getMyKey(int index) {
        if (myKeys.length > 0) {
            return myKeys[index];
        } else {
            return beanMetaData.getPrimaryKey(index);
        }
    }

    public String getYourKey(int index) {
        if (yourKeys.length > 0) {
            return yourKeys[index];
        } else {
            return beanMetaData.getPrimaryKey(index);
        }
    }

    public boolean isYourKey(String columnName) {
        for (int i = 0; i < getKeySize(); ++i) {
            if (columnName.equalsIgnoreCase(getYourKey(i))) {
                return true;
            }
        }
        return false;
    }

    public TnBeanMetaData getBeanMetaData() {
        return beanMetaData;
    }
}