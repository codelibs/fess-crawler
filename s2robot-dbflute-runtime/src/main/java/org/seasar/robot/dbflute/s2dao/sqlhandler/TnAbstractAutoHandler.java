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
package org.seasar.robot.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.exception.EntityAlreadyUpdatedException;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public abstract class TnAbstractAutoHandler extends TnBasicHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected TnBeanMetaData beanMetaData;
    protected Object[] bindVariables;
    protected ValueType[] bindVariableValueTypes;
    protected Timestamp timestamp;
    protected Integer versionNo;
    protected TnPropertyType[] propertyTypes;
    protected boolean optimisticLockHandling;
    protected boolean versionNoAutoIncrementOnMemory;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractAutoHandler(DataSource dataSource, StatementFactory statementFactory, TnBeanMetaData beanMetaData,
            TnPropertyType[] propertyTypes) {
        super(dataSource, statementFactory);
        this.beanMetaData = beanMetaData;
        this.propertyTypes = propertyTypes;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public int execute(Object[] args) {
        Connection connection = getConnection();
        try {
            return execute(connection, args[0]);
        } finally {
            close(connection);
        }
    }

    public int execute(Object[] args, Class<?>[] argTypes) {
        return execute(args);
    }

    protected int execute(Connection connection, Object bean) {
        preUpdateBean(bean);
        setupBindVariables(bean);
        logSql(bindVariables, getArgTypes(bindVariables));
        PreparedStatement ps = prepareStatement(connection);
        int ret = -1;
        try {
            bindArgs(ps, bindVariables, bindVariableValueTypes);
            ret = executeUpdate(ps);
        } finally {
            close(ps);
        }
        if (optimisticLockHandling && ret != 1) {
            throw createEntityAlreadyUpdatedException(bean, ret);
        }
        postUpdateBean(bean, ret);
        return ret;
    }

    protected EntityAlreadyUpdatedException createEntityAlreadyUpdatedException(Object bean, int rows) {
        return new EntityAlreadyUpdatedException(bean, rows);
    }

    // ===================================================================================
    //                                                                       Pre/Post Bean
    //                                                                       =============
    protected void preUpdateBean(Object bean) {
    }

    protected void postUpdateBean(Object bean, int ret) {
    }

    // ===================================================================================
    //                                                                       Bind Setupper
    //                                                                       =============
    protected abstract void setupBindVariables(Object bean);

    protected void setupInsertBindVariables(Object bean) {
        final List<Object> varList = new ArrayList<Object>();
        final List<ValueType> varValueTypeList = new ArrayList<ValueType>();
        final TnBeanMetaData bmd = getBeanMetaData();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < propertyTypes.length; ++i) {
            TnPropertyType pt = propertyTypes[i];
            if (pt.getPropertyName().equalsIgnoreCase(timestampPropertyName)) {
                setTimestamp(ResourceContext.getAccessTimestamp());
                varList.add(getTimestamp());
            } else if (pt.getPropertyName().equalsIgnoreCase(versionNoPropertyName)) {
                setVersionNo(Integer.valueOf(0));
                varList.add(getVersionNo());
            } else {
                varList.add(pt.getPropertyDesc().getValue(bean));
            }
            varValueTypeList.add(pt.getValueType());
        }
        setBindVariables(varList.toArray());
        setBindVariableValueTypes((ValueType[]) varValueTypeList.toArray(new ValueType[varValueTypeList.size()]));
    }

    protected void setupUpdateBindVariables(Object bean) {
        final List<Object> varList = new ArrayList<Object>();
        final List<ValueType> varValueTypeList = new ArrayList<ValueType>();
        final TnBeanMetaData bmd = getBeanMetaData();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < propertyTypes.length; ++i) {
            TnPropertyType pt = propertyTypes[i];
            if (pt.getPropertyName().equalsIgnoreCase(timestampPropertyName)) {
                setTimestamp(ResourceContext.getAccessTimestamp());
                varList.add(getTimestamp());
            } else if (pt.getPropertyName().equalsIgnoreCase(versionNoPropertyName)) {
                if (!isVersionNoAutoIncrementOnMemory()) {
                    continue;// because of always 'VERSION_NO = VERSION_NO + 1'
                }
                Object value = pt.getPropertyDesc().getValue(bean);
                if (value == null) {
                    continue;// because of 'VERSION_NO = VERSION_NO + 1'
                }
                int intValue = DfTypeUtil.toPrimitiveInt(value) + 1;
                setVersionNo(Integer.valueOf(intValue));
                varList.add(getVersionNo());
            } else {
                varList.add(pt.getPropertyDesc().getValue(bean));
            }
            varValueTypeList.add(pt.getValueType());
        }
        addAutoUpdateWhereBindVariables(varList, varValueTypeList, bean);
        setBindVariables(varList.toArray());
        setBindVariableValueTypes((ValueType[]) varValueTypeList.toArray(new ValueType[varValueTypeList.size()]));
    }

    protected void setupDeleteBindVariables(Object bean) {
        final List<Object> varList = new ArrayList<Object>();
        final List<ValueType> varValueTypeList = new ArrayList<ValueType>();
        addAutoUpdateWhereBindVariables(varList, varValueTypeList, bean);
        setBindVariables(varList.toArray());
        setBindVariableValueTypes((ValueType[]) varValueTypeList.toArray(new ValueType[varValueTypeList.size()]));
    }

    protected void addAutoUpdateWhereBindVariables(List<Object> varList, List<ValueType> varValueTypeList, Object bean) {
        TnBeanMetaData bmd = getBeanMetaData();
        for (int i = 0; i < bmd.getPrimaryKeySize(); ++i) {
            TnPropertyType pt = bmd.getPropertyTypeByColumnName(bmd.getPrimaryKey(i));
            DfPropertyDesc pd = pt.getPropertyDesc();
            varList.add(pd.getValue(bean));
            varValueTypeList.add(pt.getValueType());
        }
        if (optimisticLockHandling && bmd.hasVersionNoPropertyType()) {
            TnPropertyType pt = bmd.getVersionNoPropertyType();
            DfPropertyDesc pd = pt.getPropertyDesc();
            varList.add(pd.getValue(bean));
            varValueTypeList.add(pt.getValueType());
        }
        if (optimisticLockHandling && bmd.hasTimestampPropertyType()) {
            TnPropertyType pt = bmd.getTimestampPropertyType();
            DfPropertyDesc pd = pt.getPropertyDesc();
            varList.add(pd.getValue(bean));
            varValueTypeList.add(pt.getValueType());
        }
    }

    protected void updateTimestampIfNeed(Object bean) {
        if (getTimestamp() != null) {
            DfPropertyDesc pd = getBeanMetaData().getTimestampPropertyType().getPropertyDesc();
            pd.setValue(bean, getTimestamp());
        }
    }

    protected void updateVersionNoIfNeed(Object bean) {
        if (getVersionNo() != null) {
            DfPropertyDesc pd = getBeanMetaData().getVersionNoPropertyType().getPropertyDesc();
            pd.setValue(bean, getVersionNo());
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public TnBeanMetaData getBeanMetaData() {
        return beanMetaData;
    }

    protected Object[] getBindVariables() {
        return bindVariables;
    }

    protected void setBindVariables(Object[] bindVariables) {
        this.bindVariables = bindVariables;
    }

    protected ValueType[] getBindVariableValueTypes() {
        return bindVariableValueTypes;
    }

    protected void setBindVariableValueTypes(ValueType[] bindVariableValueTypes) {
        this.bindVariableValueTypes = bindVariableValueTypes;
    }

    protected Timestamp getTimestamp() {
        return timestamp;
    }

    protected void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    protected Integer getVersionNo() {
        return versionNo;
    }

    protected void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    protected TnPropertyType[] getPropertyTypes() {
        return propertyTypes;
    }

    protected void setPropertyTypes(TnPropertyType[] propertyTypes) {
        this.propertyTypes = propertyTypes;
    }

    public boolean isOptimisticLockHandling() {
        return optimisticLockHandling;
    }

    public void setOptimisticLockHandling(boolean optimisticLockHandling) {
        this.optimisticLockHandling = optimisticLockHandling;
    }

    protected boolean isVersionNoAutoIncrementOnMemory() {
        return versionNoAutoIncrementOnMemory;
    }

    public void setVersionNoAutoIncrementOnMemory(boolean versionNoAutoIncrementOnMemory) {
        this.versionNoAutoIncrementOnMemory = versionNoAutoIncrementOnMemory;
    }
}
