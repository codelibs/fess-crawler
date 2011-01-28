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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.robot.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.helper.StringKeyMap;
import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanPropertyNotFoundException;
import org.seasar.robot.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.robot.dbflute.s2dao.identity.TnIdentifierGenerator;
import org.seasar.robot.dbflute.s2dao.identity.TnIdentifierGeneratorFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnModifiedPropertySupport;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyTypeFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.seasar.robot.dbflute.s2dao.metadata.TnRelationPropertyTypeFactory;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnBeanMetaDataImpl implements TnBeanMetaData {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Class<?> _beanClass;
    protected final StringKeyMap<TnPropertyType> _propertyTypeMap = StringKeyMap.createAsCaseInsensitive();
    protected final List<TnPropertyType> _propertyTypeList = new ArrayList<TnPropertyType>();
    protected TnBeanAnnotationReader _beanAnnotationReader;
    protected TnPropertyTypeFactory _propertyTypeFactory;

    /** The name of table. (NotNull: If it's not entity, this value is 'df:Unknown') */
    protected String _tableName;

    /** The array of property type for primary key. */
    protected TnPropertyType[] _primaryKeys;

    // should be initialized in a process synchronized
    protected final Map<String, TnPropertyType> _columnPropertyTypeMap = StringKeyMap.createAsCaseInsensitive();
    protected final List<TnRelationPropertyType> _relationPropertyTypes = new ArrayList<TnRelationPropertyType>();
    protected final List<TnIdentifierGenerator> _identifierGeneratorList = new ArrayList<TnIdentifierGenerator>();
    protected final Map<String, TnIdentifierGenerator> _identifierGeneratorsByPropertyName = StringKeyMap
            .createAsCaseInsensitive();

    protected String _versionNoPropertyName;
    protected String _timestampPropertyName;
    protected TnModifiedPropertySupport _modifiedPropertySupport;
    protected TnRelationPropertyTypeFactory _relationPropertyTypeFactory;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnBeanMetaDataImpl(Class<?> beanClass) {
        this._beanClass = beanClass;
    }

    // ===================================================================================
    //                                                                          Bean Class
    //                                                                          ==========
    public Class<?> getBeanClass() {
        return _beanClass;
    }

    // ===================================================================================
    //                                                                       Property Type
    //                                                                       =============
    public List<TnPropertyType> getPropertyTypeList() {
        return _propertyTypeList;
    }

    public TnPropertyType getPropertyType(String propertyName) {
        final TnPropertyType propertyType = (TnPropertyType) _propertyTypeMap.get(propertyName);
        if (propertyType == null) {
            String msg = "The propertyName was not found in the map:";
            msg = msg + " propertyName=" + propertyName + " propertyTypeMap=" + _propertyTypeMap;
            throw new IllegalStateException(msg);
        }
        return propertyType;
    }

    public boolean hasPropertyType(String propertyName) {
        return _propertyTypeMap.get(propertyName) != null;
    }

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    public void initialize() { // non thread safe so this is called immediately after creation
        final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(getBeanClass());
        setupTableName(beanDesc);
        setupProperty();
        setupPrimaryKey();
    }

    protected void setupTableName(DfBeanDesc beanDesc) { // only called in the initialize() process 
        final String ta = _beanAnnotationReader.getTableAnnotation();
        if (ta != null) {
            _tableName = ta;
        } else {
            _tableName = "df:Unknown";
        }
    }

    protected void setupProperty() { // only called in the initialize() process
        final TnPropertyType[] propertyTypes = _propertyTypeFactory.createBeanPropertyTypes();
        for (int i = 0; i < propertyTypes.length; i++) {
            TnPropertyType pt = propertyTypes[i];
            addPropertyType(pt);
            _columnPropertyTypeMap.put(pt.getColumnDbName(), pt);
        }

        final TnRelationPropertyType[] rptTypes = _relationPropertyTypeFactory.createRelationPropertyTypes();
        for (int i = 0; i < rptTypes.length; i++) {
            TnRelationPropertyType rpt = rptTypes[i];
            addRelationPropertyType(rpt);
        }
    }

    protected void addPropertyType(TnPropertyType propertyType) { // only called in the initialize() process
        _propertyTypeMap.put(propertyType.getPropertyName(), propertyType);
        _propertyTypeList.add(propertyType);
    }

    protected void setupPrimaryKey() { // only called in the initialize() process
        final List<TnPropertyType> keys = new ArrayList<TnPropertyType>();
        for (TnPropertyType pt : _propertyTypeList) {
            if (pt.isPrimaryKey()) {
                keys.add(pt);
                setupIdentifierGenerator(pt);
            }
        }
        _primaryKeys = (TnPropertyType[]) keys.toArray(new TnPropertyType[keys.size()]);
    }

    protected void setupIdentifierGenerator(TnPropertyType pt) { // only called in the initialize() process
        final DfPropertyDesc pd = pt.getPropertyDesc();
        final String propertyName = pt.getPropertyName();
        final String idType = _beanAnnotationReader.getId(pd);
        final TnIdentifierGenerator generator = TnIdentifierGeneratorFactory.createIdentifierGenerator(pt, idType);
        _identifierGeneratorList.add(generator);
        _identifierGeneratorsByPropertyName.put(propertyName, generator);
    }

    protected void addRelationPropertyType(TnRelationPropertyType rpt) { // only called in the initialize() process
        for (int i = _relationPropertyTypes.size(); i <= rpt.getRelationNo(); ++i) {
            _relationPropertyTypes.add(null);
        }
        _relationPropertyTypes.set(rpt.getRelationNo(), rpt);
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    /**
     * @return The name of table. (NotNull: If it's not entity, this value is 'df:Unknown')
     */
    public String getTableName() {
        return _tableName;
    }

    public TnPropertyType getVersionNoPropertyType() throws DfBeanPropertyNotFoundException {
        return getPropertyType(getVersionNoPropertyName());
    }

    public TnPropertyType getTimestampPropertyType() throws DfBeanPropertyNotFoundException {
        return getPropertyType(getTimestampPropertyName());
    }

    public String getVersionNoPropertyName() {
        return _versionNoPropertyName;
    }

    public void setVersionNoPropertyName(String versionNoPropertyName) {
        this._versionNoPropertyName = versionNoPropertyName;
    }

    public String getTimestampPropertyName() {
        return _timestampPropertyName;
    }

    public void setTimestampPropertyName(String timestampPropertyName) {
        this._timestampPropertyName = timestampPropertyName;
    }

    public TnPropertyType getPropertyTypeByColumnName(String columnName) {
        final TnPropertyType propertyType = _columnPropertyTypeMap.get(columnName);
        if (propertyType == null) {
            final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
            br.addNotice("The column was not found in the table.");
            br.addItem("Column");
            br.addElement(_tableName + "." + columnName);
            br.addItem("Mapping");
            final Set<Entry<String, TnPropertyType>> entrySet = _columnPropertyTypeMap.entrySet();
            for (Entry<String, TnPropertyType> entry : entrySet) {
                br.addElement(entry.getKey() + ": " + entry.getValue());
            }
            final String msg = br.buildExceptionMessage();
            throw new IllegalStateException(msg);
        }
        return propertyType;
    }

    public TnPropertyType getPropertyTypeByAliasName(String alias) {
        if (hasPropertyTypeByColumnName(alias)) {
            return getPropertyTypeByColumnName(alias);
        }
        int index = alias.lastIndexOf('_');
        if (index < 0) {
            String msg = "The alias was not found in the table: table=" + _tableName + " alias=" + alias;
            throw new IllegalStateException(msg);
        }
        String columnName = alias.substring(0, index);
        String relnoStr = alias.substring(index + 1);
        int relno = -1;
        try {
            relno = Integer.parseInt(relnoStr);
        } catch (Throwable t) {
            String msg = "The alias was not found in the table: table=" + _tableName + " alias=" + alias;
            throw new IllegalStateException(msg, t);
        }
        TnRelationPropertyType rpt = getRelationPropertyType(relno);
        if (!rpt.getBeanMetaData().hasPropertyTypeByColumnName(columnName)) {
            String msg = "The alias was not found in the table: table=" + _tableName + " alias=" + alias;
            throw new IllegalStateException(msg);
        }
        return rpt.getBeanMetaData().getPropertyTypeByColumnName(columnName);
    }

    public boolean hasPropertyTypeByColumnName(String columnName) {
        return _columnPropertyTypeMap.get(columnName) != null;
    }

    public boolean hasPropertyTypeByAliasName(String alias) {
        if (hasPropertyTypeByColumnName(alias)) {
            return true;
        }
        int index = alias.lastIndexOf('_');
        if (index < 0) {
            return false;
        }
        String columnName = alias.substring(0, index);
        String relnoStr = alias.substring(index + 1);
        int relno = -1;
        try {
            relno = Integer.parseInt(relnoStr);
        } catch (Throwable t) {
            return false;
        }
        if (relno >= getRelationPropertyTypeSize()) {
            return false;
        }
        TnRelationPropertyType rpt = getRelationPropertyType(relno);
        return rpt.getBeanMetaData().hasPropertyTypeByColumnName(columnName);
    }

    public boolean hasVersionNoPropertyType() {
        return hasPropertyType(getVersionNoPropertyName());
    }

    public boolean hasTimestampPropertyType() {
        return hasPropertyType(getTimestampPropertyName());
    }

    public String convertFullColumnName(String alias) {
        if (hasPropertyTypeByColumnName(alias)) {
            return _tableName + "." + alias;
        }
        int index = alias.lastIndexOf('_');
        if (index < 0) {
            String msg = "The alias was not found in the table: table=" + _tableName + " alias=" + alias;
            throw new IllegalStateException(msg);
        }
        String columnName = alias.substring(0, index);
        String relnoStr = alias.substring(index + 1);
        int relno = -1;
        try {
            relno = Integer.parseInt(relnoStr);
        } catch (Throwable t) {
            String msg = "The alias was not found in the table: table=" + _tableName + " alias=" + alias;
            throw new IllegalStateException(msg, t);
        }
        TnRelationPropertyType rpt = getRelationPropertyType(relno);
        if (!rpt.getBeanMetaData().hasPropertyTypeByColumnName(columnName)) {
            String msg = "The alias was not found in the table: table=" + _tableName + " alias=" + alias;
            throw new IllegalStateException(msg);
        }
        return rpt.getPropertyName() + "." + columnName;
    }

    public int getRelationPropertyTypeSize() {
        return _relationPropertyTypes.size();
    }

    public TnRelationPropertyType getRelationPropertyType(int index) {
        return (TnRelationPropertyType) _relationPropertyTypes.get(index);
    }

    public TnRelationPropertyType getRelationPropertyType(String propertyName) throws DfBeanPropertyNotFoundException {
        for (int i = 0; i < getRelationPropertyTypeSize(); i++) {
            TnRelationPropertyType rpt = (TnRelationPropertyType) _relationPropertyTypes.get(i);
            if (rpt != null && rpt.getPropertyName().equalsIgnoreCase(propertyName)) {
                return rpt;
            }
        }
        throw new DfBeanPropertyNotFoundException(getBeanClass(), propertyName);
    }

    public int getPrimaryKeySize() {
        return _primaryKeys.length;
    }

    public String getPrimaryKeyDbName(int index) {
        return _primaryKeys[index].getColumnDbName();
    }

    public ColumnSqlName getPrimaryKeySqlName(int index) {
        return _primaryKeys[index].getColumnSqlName();
    }

    public int getIdentifierGeneratorSize() {
        return _identifierGeneratorList.size();
    }

    public TnIdentifierGenerator getIdentifierGenerator(int index) {
        return (TnIdentifierGenerator) _identifierGeneratorList.get(index);
    }

    public TnIdentifierGenerator getIdentifierGenerator(String propertyName) {
        return (TnIdentifierGenerator) _identifierGeneratorsByPropertyName.get(propertyName);
    }

    public TnModifiedPropertySupport getModifiedPropertySupport() {
        return _modifiedPropertySupport;
    }

    public void setModifiedPropertySupport(final TnModifiedPropertySupport propertyModifiedSupport) {
        this._modifiedPropertySupport = propertyModifiedSupport;
    }

    public Set<String> getModifiedPropertyNames(final Object bean) {
        return getModifiedPropertySupport().getModifiedPropertyNames(bean);
    }

    public void setRelationPropertyTypeFactory(TnRelationPropertyTypeFactory relationPropertyTypeFactory) {
        this._relationPropertyTypeFactory = relationPropertyTypeFactory;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setBeanAnnotationReader(TnBeanAnnotationReader beanAnnotationReader) {
        this._beanAnnotationReader = beanAnnotationReader;
    }

    public void setPropertyTypeFactory(TnPropertyTypeFactory propertyTypeFactory) {
        this._propertyTypeFactory = propertyTypeFactory;
    }
}
