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

import org.seasar.robot.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.robot.dbflute.helper.beans.DfPropertyAccessor;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnPropertyTypeImpl implements TnPropertyType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfPropertyDesc _propertyDesc;
    protected final String _propertyName;
    protected final String _columnDbName;
    protected final ColumnSqlName _columnSqlName;
    protected final ValueType _valueType;
    protected boolean _primaryKey = false;
    protected boolean _persistent = true;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnPropertyTypeImpl(DfPropertyDesc propertyDesc) {
        // for non persistent property (for example, relation)
        this(propertyDesc, TnValueTypes.DEFAULT_OBJECT, propertyDesc.getPropertyName(), new ColumnSqlName(propertyDesc
                .getPropertyName()));
    }

    public TnPropertyTypeImpl(DfPropertyDesc propertyDesc, ValueType valueType, String columnDbName,
            ColumnSqlName columnSqlName) {
        this._propertyDesc = propertyDesc;
        this._propertyName = propertyDesc.getPropertyName();
        this._valueType = valueType;
        this._columnDbName = columnDbName;
        this._columnSqlName = columnSqlName;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{" + _propertyName + "(" + _columnDbName + "), "
                + DfTypeUtil.toClassTitle(_valueType) + ", " + _primaryKey + ", " + _persistent + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DfPropertyAccessor getPropertyAccessor() {
        return _propertyDesc;
    }

    public DfPropertyDesc getPropertyDesc() {
        return _propertyDesc;
    }

    public String getPropertyName() {
        return _propertyName;
    }

    public String getColumnDbName() {
        return _columnDbName;
    }

    public ColumnSqlName getColumnSqlName() {
        return _columnSqlName;
    }

    public ValueType getValueType() {
        return _valueType;
    }

    public boolean isPrimaryKey() {
        return _primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this._primaryKey = primaryKey;
    }

    public boolean isPersistent() {
        return _persistent;
    }

    public void setPersistent(boolean persistent) {
        this._persistent = persistent;
    }
}