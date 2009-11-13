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
package org.seasar.robot.dbflute.bhv.core.command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.bhv.core.BehaviorCommand;
import org.seasar.robot.dbflute.bhv.core.BehaviorCommandComponentSetup;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.s2dao.extension.TnRelationRowCreatorExtension;
import org.seasar.robot.dbflute.s2dao.extension.TnRowCreatorExtension;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaDataFactory;
import org.seasar.robot.dbflute.s2dao.rshandler.TnBeanCursorMetaDataResultSetHandler;
import org.seasar.robot.dbflute.s2dao.rshandler.TnBeanListMetaDataResultSetHandler;
import org.seasar.robot.dbflute.s2dao.sqlcommand.TnUpdateDynamicCommand;
import org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypeFactory;
import org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes;

/**
 * @author jflute
 * @param <RESULT> The type of result.
 */
public abstract class AbstractBehaviorCommand<RESULT> implements BehaviorCommand<RESULT>, BehaviorCommandComponentSetup {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                     Basic Information
    //                                     -----------------
    /** The table DB name. (Required) */
    protected String _tableDbName;

    /** Is it initialize only? (Choice) */
    protected boolean _initializeOnly;

    // -----------------------------------------------------
    //                                   Injection Component
    //                                   -------------------
    protected DataSource _dataSource;
    protected StatementFactory _statementFactory;
    protected TnBeanMetaDataFactory _beanMetaDataFactory;
    protected TnValueTypeFactory _valueTypeFactory;
    protected String _sqlFileEncoding;

    // ===================================================================================
    //                                                                             Factory
    //                                                                             =======
    // -----------------------------------------------------
    //                                   UpdateDynamicCommnd
    //                                   -------------------
    protected TnUpdateDynamicCommand createUpdateDynamicCommand(String[] argNames, Class<?>[] argTypes, String sql) {
        final TnUpdateDynamicCommand cmd = new TnUpdateDynamicCommand(_dataSource, _statementFactory);
        cmd.setArgNames(argNames);
        cmd.setArgTypes(argTypes);
        if (sql != null) {
            cmd.setSql(sql);
        }
        return cmd;
    }

    // -----------------------------------------------------
    //                                      ResultSetHandler
    //                                      ----------------
    protected TnResultSetHandler createBeanListMetaDataResultSetHandler(TnBeanMetaData bmd) {
        final TnRowCreatorExtension rowCreator = createInternalRowCreator(bmd);
        final TnRelationRowCreatorExtension relationRowCreator = createInternalRelationRowCreator(bmd);
        return new TnBeanListMetaDataResultSetHandler(bmd, rowCreator, relationRowCreator);
    }
    
    protected TnResultSetHandler createBeanCursorMetaDataResultSetHandler(TnBeanMetaData bmd) {
        final TnRowCreatorExtension rowCreator = createInternalRowCreator(bmd);
        final TnRelationRowCreatorExtension relationRowCreator = createInternalRelationRowCreator(bmd);
        return new TnBeanCursorMetaDataResultSetHandler(bmd, rowCreator, relationRowCreator);
    }

    protected TnResultSetHandler createObjectResultSetHandler(Class<?> objectType) {
        final ValueType valueType = TnValueTypes.getValueType(objectType);
        return new InternalObjectResultSetHandler(valueType);
    }

    protected TnResultSetHandler createObjectListResultSetHandler(Class<?> objectType) {
        final ValueType valueType = TnValueTypes.getValueType(objectType);
        return createObjectListResultSetHandler(valueType);
    }

    protected TnResultSetHandler createObjectListResultSetHandler(ValueType valueType) {
        return new InternalObjectListResultSetHandler(valueType);
    }

    protected static class InternalObjectResultSetHandler implements TnResultSetHandler {
        private ValueType valueType;
        public InternalObjectResultSetHandler(ValueType valueType) {
            this.valueType = valueType;
        }
        public Object handle(ResultSet rs) throws SQLException {
            while (rs.next()) {
                return valueType.getValue(rs, 1);
            }
            return null;
        }
    }

    protected static class InternalObjectListResultSetHandler implements TnResultSetHandler {
        private ValueType valueType;
        public InternalObjectListResultSetHandler(ValueType valueType) {
            this.valueType = valueType;
        }
        public Object handle(ResultSet rs) throws SQLException {
            final List<Object> ret = new ArrayList<Object>();
            while (rs.next()) {
                ret.add(valueType.getValue(rs, 1));
            }
            return ret;
        }
    }

    protected static class InternalNullResultSetHandler implements TnResultSetHandler {
        public Object handle(ResultSet rs) throws SQLException {
            return null;
        }
    }

    protected TnRowCreatorExtension createInternalRowCreator(TnBeanMetaData bmd) {
        final Class<?> clazz = bmd != null ? bmd.getBeanClass() : null;
        return TnRowCreatorExtension.createRowCreator(clazz);
    }

    protected TnRelationRowCreatorExtension createInternalRelationRowCreator(TnBeanMetaData bmd) {
        return new TnRelationRowCreatorExtension(); // Not yet implemented about performance tuning!
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertBasicProperty(String methodName) {
        if (_tableDbName == null) {
            throw new IllegalStateException(buildAssertMessage("_tableDbName", methodName));
        }
    }

    protected void assertComponentProperty(String methodName) {
        if (_dataSource == null) {
            throw new IllegalStateException(buildAssertMessage("_dataSource", methodName));
        }
        if (_statementFactory == null) {
            throw new IllegalStateException(buildAssertMessage("_statementFactory", methodName));
        }
        if (_beanMetaDataFactory == null) {
            throw new IllegalStateException(buildAssertMessage("_beanMetaDataFactory", methodName));
        }
        if (_valueTypeFactory == null) {
            throw new IllegalStateException(buildAssertMessage("_valueTypeFactory", methodName));
        }
        if (_sqlFileEncoding == null) {
            throw new IllegalStateException(buildAssertMessage("_sqlFileEncoding", methodName));
        }
    }

    protected String buildAssertMessage(String propertyName, String methodName) {
        propertyName = propertyName.startsWith("_") ? propertyName.substring("_".length()) : propertyName;
        String msg = "The property '" + propertyName + "' should not be null";
        msg = msg + " when you call " + methodName + "().";
        throw new IllegalStateException(msg);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return getClass().getSimpleName() + ":{" + buildSqlExecutionKey() + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                     Basic Information
    //                                     -----------------
    public String getTableDbName() {
        return _tableDbName;
    }

    public void setTableDbName(String tableDbName) {
        _tableDbName = tableDbName;
    }

    public void setInitializeOnly(boolean initializeOnly) {
        _initializeOnly = initializeOnly;
    }

    public boolean isInitializeOnly() {
        return _initializeOnly;
    }

    // -----------------------------------------------------
    //                                   Injection Component
    //                                   -------------------
    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }

    public void setStatementFactory(StatementFactory statementFactory) {
        _statementFactory = statementFactory;
    }

    public void setBeanMetaDataFactory(TnBeanMetaDataFactory beanMetaDataFactory) {
        _beanMetaDataFactory = beanMetaDataFactory;
    }

    public void setValueTypeFactory(TnValueTypeFactory valueTypeFactory) {
        _valueTypeFactory = valueTypeFactory;
    }

    public void setSqlFileEncoding(String sqlFileEncoding) {
        _sqlFileEncoding = sqlFileEncoding;
    }
}
