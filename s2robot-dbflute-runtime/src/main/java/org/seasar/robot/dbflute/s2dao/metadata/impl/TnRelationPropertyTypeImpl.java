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

import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyType;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnRelationPropertyTypeImpl extends TnPropertyTypeImpl implements TnRelationPropertyType {

    protected int _relationNo;
    protected String[] _myKeys;
    protected String[] _yourKeys;
    protected TnBeanMetaData _beanMetaData;

    public TnRelationPropertyTypeImpl(DfPropertyDesc propertyDesc, int relationNo, String[] myKeys, String[] yourKeys,
            TnBeanMetaData beanMetaData) {
        super(propertyDesc);
        this._relationNo = relationNo;
        this._myKeys = myKeys;
        this._yourKeys = yourKeys;
        this._beanMetaData = beanMetaData;
    }

    public int getRelationNo() {
        return _relationNo;
    }

    public int getKeySize() {
        if (_myKeys.length > 0) {
            return _myKeys.length;
        } else {
            return _beanMetaData.getPrimaryKeySize();
        }

    }

    public String getMyKey(int index) {
        if (_myKeys.length > 0) {
            return _myKeys[index];
        } else {
            return _beanMetaData.getPrimaryKeyDbName(index);
        }
    }

    public String getYourKey(int index) {
        if (_yourKeys.length > 0) {
            return _yourKeys[index];
        } else {
            return _beanMetaData.getPrimaryKeyDbName(index);
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
        return _beanMetaData;
    }
}