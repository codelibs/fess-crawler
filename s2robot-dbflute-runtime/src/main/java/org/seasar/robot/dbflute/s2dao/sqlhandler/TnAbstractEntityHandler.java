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
package org.seasar.robot.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.bhv.DeleteOption;
import org.seasar.robot.dbflute.bhv.InsertOption;
import org.seasar.robot.dbflute.bhv.UpdateOption;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbway.WayOfSQLServer;
import org.seasar.robot.dbflute.dbway.WayOfSybase;
import org.seasar.robot.dbflute.exception.EntityAlreadyUpdatedException;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.util.DfCollectionUtil;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnAbstractEntityHandler extends TnAbstractBasicSqlHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final TnBeanMetaData _beanMetaData;
    protected final TnPropertyType[] _boundPropTypes; // not only completely bounds (needs to filter)
    protected boolean _optimisticLockHandling;
    protected boolean _versionNoAutoIncrementOnMemory; // to adjust binding
    protected InsertOption<? extends ConditionBean> _insertOption;
    protected UpdateOption<? extends ConditionBean> _updateOption;
    protected DeleteOption<? extends ConditionBean> _deleteOption;

    protected Object[] _bindVariables;
    protected ValueType[] _bindVariableValueTypes;
    protected List<Timestamp> _newTimestampList;
    protected List<Long> _newVersionNoList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractEntityHandler(DataSource dataSource, StatementFactory statementFactory, String sql,
            TnBeanMetaData beanMetaData, TnPropertyType[] boundPropTypes) {
        super(dataSource, statementFactory, sql);
        assertObjectNotNull("beanMetaData", beanMetaData);
        assertObjectNotNull("boundPropTypes", boundPropTypes); // not null but empty allowed
        _beanMetaData = beanMetaData;
        _boundPropTypes = boundPropTypes;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public int execute(Object[] args) {
        final Connection conn = getConnection();
        try {
            return execute(conn, args[0]);
        } finally {
            close(conn);
        }
    }

    protected int execute(Connection conn, Object bean) {
        processBefore(bean);
        setupBindVariables(bean);
        logSql(_bindVariables, getArgTypes(_bindVariables));
        final PreparedStatement ps = prepareStatement(conn);
        RuntimeException sqlEx = null;
        final int ret;
        try {
            bindArgs(conn, ps, _bindVariables, _bindVariableValueTypes);
            ret = executeUpdate(ps);
            handleUpdateResultWithOptimisticLock(bean, ret);
        } catch (RuntimeException e) {
            // not SQLFailureException because
            // a wrapper of JDBC may throw an other exception
            sqlEx = e;
            throw e;
        } finally {
            close(ps);
            processFinally(bean, sqlEx);
        }
        // a value of exclusive control column should be synchronized
        // after handling optimistic lock
        processSuccess(bean, ret);
        return ret;
    }

    // ===================================================================================
    //                                                                   Extension Process
    //                                                                   =================
    protected void processBefore(Object bean) {
    }

    protected void processFinally(Object bean, RuntimeException sqlEx) {
    }

    protected void processSuccess(Object bean, int ret) {
    }

    // ===================================================================================
    //                                                                     Optimistic Lock
    //                                                                     ===============
    protected void handleUpdateResultWithOptimisticLock(Object bean, int ret) {
        if (_optimisticLockHandling && ret < 1) { // means no update (contains minus just in case)
            throw createEntityAlreadyUpdatedException(bean, ret);
        }
    }

    protected EntityAlreadyUpdatedException createEntityAlreadyUpdatedException(Object bean, int rows) {
        return new EntityAlreadyUpdatedException(bean, rows);
    }

    // ===================================================================================
    //                                                                       Bind Variable
    //                                                                       =============
    protected abstract void setupBindVariables(Object bean);

    protected void setupInsertBindVariables(Object bean) {
        final List<Object> varList = new ArrayList<Object>();
        final List<ValueType> varValueTypeList = new ArrayList<ValueType>();
        final TnBeanMetaData bmd = getBeanMetaData();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < _boundPropTypes.length; ++i) {
            final TnPropertyType pt = _boundPropTypes[i];
            if (pt.getPropertyName().equalsIgnoreCase(timestampPropertyName)) {
                final Timestamp timestamp = ResourceContext.getAccessTimestamp();
                addNewTimestamp(timestamp);
                varList.add(timestamp);
            } else if (pt.getPropertyName().equalsIgnoreCase(versionNoPropertyName)) {
                final Long firstNo = InsertOption.VERSION_NO_FIRST_VALUE;
                addNewVersionNo(firstNo);
                varList.add(firstNo);
            } else {
                varList.add(pt.getPropertyDesc().getValue(bean));
            }
            varValueTypeList.add(pt.getValueType());
        }
        _bindVariables = varList.toArray();
        _bindVariableValueTypes = (ValueType[]) varValueTypeList.toArray(new ValueType[varValueTypeList.size()]);
    }

    protected void setupUpdateBindVariables(Object bean) {
        final List<Object> varList = new ArrayList<Object>();
        final List<ValueType> varValueTypeList = new ArrayList<ValueType>();
        final TnBeanMetaData bmd = getBeanMetaData();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < _boundPropTypes.length; ++i) {
            final TnPropertyType pt = _boundPropTypes[i];
            if (pt.getPropertyName().equalsIgnoreCase(timestampPropertyName)) {
                final Timestamp timestamp = ResourceContext.getAccessTimestamp();
                addNewTimestamp(timestamp);
                varList.add(timestamp);
            } else if (pt.getPropertyName().equalsIgnoreCase(versionNoPropertyName)) {
                if (!_versionNoAutoIncrementOnMemory) { // means OnQuery
                    continue; // because of 'VERSION_NO = VERSION_NO + 1'
                }
                final Object value = pt.getPropertyDesc().getValue(bean); // already null-checked
                final long longValue = DfTypeUtil.toPrimitiveLong(value) + 1L;
                final Long versionNo = Long.valueOf(longValue);
                addNewVersionNo(versionNo);
                varList.add(versionNo);
            } else if (_updateOption != null && _updateOption.hasStatement(pt.getColumnDbName())) {
                continue; // because of 'FOO_COUNT = FOO_COUNT + 1'
            } else {
                varList.add(pt.getPropertyDesc().getValue(bean));
            }
            varValueTypeList.add(pt.getValueType());
        }
        addAutoUpdateWhereBindVariables(varList, varValueTypeList, bean);
        _bindVariables = varList.toArray();
        _bindVariableValueTypes = (ValueType[]) varValueTypeList.toArray(new ValueType[varValueTypeList.size()]);
    }

    protected void setupDeleteBindVariables(Object bean) {
        final List<Object> varList = new ArrayList<Object>();
        final List<ValueType> varValueTypeList = new ArrayList<ValueType>();
        addAutoUpdateWhereBindVariables(varList, varValueTypeList, bean);
        _bindVariables = varList.toArray();
        _bindVariableValueTypes = (ValueType[]) varValueTypeList.toArray(new ValueType[varValueTypeList.size()]);
    }

    protected void addAutoUpdateWhereBindVariables(List<Object> varList, List<ValueType> varValueTypeList, Object bean) {
        final TnBeanMetaData bmd = getBeanMetaData();
        for (int i = 0; i < bmd.getPrimaryKeySize(); ++i) {
            final TnPropertyType pt = bmd.getPropertyTypeByColumnName(bmd.getPrimaryKeyDbName(i));
            final DfPropertyDesc pd = pt.getPropertyDesc();
            varList.add(pd.getValue(bean));
            varValueTypeList.add(pt.getValueType());
        }
        if (_optimisticLockHandling && bmd.hasVersionNoPropertyType()) {
            final TnPropertyType pt = bmd.getVersionNoPropertyType();
            final DfPropertyDesc pd = pt.getPropertyDesc();
            varList.add(pd.getValue(bean));
            varValueTypeList.add(pt.getValueType());
        }
        if (_optimisticLockHandling && bmd.hasTimestampPropertyType()) {
            final TnPropertyType pt = bmd.getTimestampPropertyType();
            final DfPropertyDesc pd = pt.getPropertyDesc();
            varList.add(pd.getValue(bean));
            varValueTypeList.add(pt.getValueType());
        }
    }

    // ===================================================================================
    //                                                                 Timestamp/VersionNo
    //                                                                 ===================
    protected void addNewTimestamp(Timestamp timestamp) {
        if (_newTimestampList == null) {
            _newTimestampList = DfCollectionUtil.newArrayList();
        }
        _newTimestampList.add(timestamp);
    }

    protected void addNewVersionNo(Long versionNo) {
        if (_newVersionNoList == null) {
            _newVersionNoList = DfCollectionUtil.newArrayList();
        }
        _newVersionNoList.add(versionNo);
    }

    protected void updateTimestampIfNeed(Object bean) {
        updateTimestampIfNeed(bean, 0);
    }

    protected void updateTimestampIfNeed(Object bean, int index) {
        final List<Timestamp> newTimestampList = _newTimestampList;
        if (newTimestampList == null || newTimestampList.isEmpty()) {
            return;
        }
        final DfPropertyDesc pd = getBeanMetaData().getTimestampPropertyType().getPropertyDesc();
        pd.setValue(bean, newTimestampList.get(index));
    }

    protected void updateVersionNoIfNeed(Object bean) {
        updateVersionNoIfNeed(bean, 0);
    }

    protected void updateVersionNoIfNeed(Object bean, int index) {
        final List<Long> newVersionNoList = _newVersionNoList;
        if (newVersionNoList == null || newVersionNoList.isEmpty()) {
            return;
        }
        final DfPropertyDesc pd = getBeanMetaData().getVersionNoPropertyType().getPropertyDesc();
        pd.setValue(bean, newVersionNoList.get(index));
    }

    // ===================================================================================
    //                                                                            Identity
    //                                                                            ========
    protected void disableIdentityGeneration() {
        final String tableDbName = _beanMetaData.getTableName();
        delegateDisableIdentityGeneration(tableDbName, _dataSource, _statementFactory);
    }

    protected void enableIdentityGeneration() {
        final String tableDbName = _beanMetaData.getTableName();
        delegateEnableIdentityGeneration(tableDbName, _dataSource, _statementFactory);
    }

    protected boolean isPrimaryKeyIdentityDisabled() {
        return _insertOption != null && _insertOption.isPrimaryKeyIdentityDisabled();
    }

    // "public static" for recycle
    public static void delegateDisableIdentityGeneration(String tableDbName, DataSource dataSource,
            StatementFactory statementFactory) {
        final TnIdentityGenerationHandler handler = new TnIdentityGenerationHandler();
        handler.disableIdentityGeneration(tableDbName, dataSource, statementFactory);
    }

    public static void delegateEnableIdentityGeneration(String tableDbName, DataSource dataSource,
            StatementFactory statementFactory) {
        final TnIdentityGenerationHandler handler = new TnIdentityGenerationHandler();
        handler.enableIdentityGeneration(tableDbName, dataSource, statementFactory);
    }

    protected static class TnIdentityGenerationHandler {

        public void disableIdentityGeneration(String tableDbName, DataSource dataSource,
                StatementFactory statementFactory) {
            if (isDatabaseSQLServer()) {
                final String tableSqlName = findDBMeta(tableDbName).getTableSqlName().toString();
                final String disableSql = getWayOfSQLServer().buildIdentityDisableSql(tableSqlName);
                doExecuteIdentityAdjustment(disableSql, dataSource, statementFactory);
            } else if (isDatabaseSybase()) {
                final String tableSqlName = findDBMeta(tableDbName).getTableSqlName().toString();
                final String disableSql = getWayOfSybase().buildIdentityDisableSql(tableSqlName);
                doExecuteIdentityAdjustment(disableSql, dataSource, statementFactory);
            }
        }

        public void enableIdentityGeneration(String tableDbName, DataSource dataSource,
                StatementFactory statementFactory) {
            if (isDatabaseSQLServer()) {
                final String tableSqlName = findDBMeta(tableDbName).getTableSqlName().toString();
                final String enableSql = getWayOfSQLServer().buildIdentityEnableSql(tableSqlName);
                doExecuteIdentityAdjustment(enableSql, dataSource, statementFactory);
            } else if (isDatabaseSybase()) {
                final String tableSqlName = findDBMeta(tableDbName).getTableSqlName().toString();
                final String enableSql = getWayOfSybase().buildIdentityEnableSql(tableSqlName);
                doExecuteIdentityAdjustment(enableSql, dataSource, statementFactory);
            }
        }

        protected DBMeta findDBMeta(String tableDbName) {
            return ResourceContext.dbmetaProvider().provideDBMeta(tableDbName);
        }

        protected boolean isDatabaseSQLServer() {
            return ResourceContext.isCurrentDBDef(DBDef.SQLServer);
        }

        protected boolean isDatabaseSybase() {
            return ResourceContext.isCurrentDBDef(DBDef.Sybase);
        }

        protected WayOfSQLServer getWayOfSQLServer() {
            return (WayOfSQLServer) ResourceContext.currentDBDef().dbway();
        }

        protected WayOfSybase getWayOfSybase() {
            return (WayOfSybase) ResourceContext.currentDBDef().dbway();
        }

        protected void doExecuteIdentityAdjustment(String sql, DataSource dataSource, StatementFactory statementFactory) {
            final TnIdentityAdjustmentSqlHandler handler = createIdentityAdjustmentSqlHandler(sql, dataSource,
                    statementFactory);
            handler.execute(new Object[] {}); // SQL for identity adjustment does not have a bind-variable
        }

        protected TnIdentityAdjustmentSqlHandler createIdentityAdjustmentSqlHandler(String sql, DataSource dataSource,
                StatementFactory statementFactory) {
            return new TnIdentityAdjustmentSqlHandler(dataSource, statementFactory, sql);
        }
    }

    protected static class TnIdentityAdjustmentSqlHandler extends TnBasicUpdateHandler {

        public TnIdentityAdjustmentSqlHandler(DataSource dataSource, StatementFactory statementFactory, String sql) {
            super(dataSource, statementFactory, sql);
        }

        @Override
        protected Object doExecute(Connection conn, Object[] args, Class<?>[] argTypes) {
            logSql(args, argTypes);
            Statement st = null;
            try {
                // PreparedStatement is not used here
                // because SQLServer do not work by PreparedStatement
                // but it do work well by Statement
                st = conn.createStatement();
                return st.executeUpdate(_sql);
            } catch (SQLException e) {
                handleSQLException(e, st);
                return 0; // unreachable
            } finally {
                close(st);
            }
        };
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public TnBeanMetaData getBeanMetaData() {
        return _beanMetaData;
    }

    public void setOptimisticLockHandling(boolean optimisticLockHandling) {
        this._optimisticLockHandling = optimisticLockHandling;
    }

    public void setVersionNoAutoIncrementOnMemory(boolean versionNoAutoIncrementOnMemory) {
        this._versionNoAutoIncrementOnMemory = versionNoAutoIncrementOnMemory;
    }

    public void setInsertOption(InsertOption<? extends ConditionBean> insertOption) {
        this._insertOption = insertOption;
    }

    public void setUpdateOption(UpdateOption<? extends ConditionBean> updateOption) {
        this._updateOption = updateOption;
    }

    public void setDeleteOption(DeleteOption<? extends ConditionBean> deleteOption) {
        this._deleteOption = deleteOption;
    }
}
