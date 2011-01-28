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
package org.seasar.robot.dbflute.s2dao.sqlcommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.XLog;
import org.seasar.robot.dbflute.bhv.UpdateOption;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.robot.dbflute.dbmeta.name.TableSqlName;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnUpdateEntityHandler;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnUpdateEntityDynamicCommand extends TnAbstractEntityDynamicCommand {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The result for no update as normal execution. */
    private static final Integer NON_UPDATE = Integer.valueOf(1);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _optimisticLockHandling;
    protected boolean _versionNoAutoIncrementOnMemory;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnUpdateEntityDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) {
        if (args == null || args.length == 0) {
            String msg = "The argument 'args' should not be null or empty.";
            throw new IllegalArgumentException(msg);
        }
        final Object bean = args[0];
        final UpdateOption<ConditionBean> option = extractUpdateOptionChecked(args);

        final TnPropertyType[] propertyTypes = createUpdatePropertyTypes(bean, option);
        if (propertyTypes.length == 0) {
            if (isLogEnabled()) {
                log(createNonUpdateLogMessage(bean));
            }
            return getNonUpdateReturn();
        }
        final String sql = createUpdateSql(propertyTypes, option);
        return doExecute(bean, propertyTypes, sql, option);
    }

    protected UpdateOption<ConditionBean> extractUpdateOptionChecked(Object[] args) {
        if (args.length < 2 || args[1] == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        final UpdateOption<ConditionBean> option = (UpdateOption<ConditionBean>) args[1];
        option.xcheckSpecifiedUpdateColumnPrimaryKey();
        return option;
    }

    protected Object doExecute(Object bean, TnPropertyType[] propertyTypes, String sql,
            UpdateOption<ConditionBean> option) {
        final TnUpdateEntityHandler handler = createUpdateEntityHandler(propertyTypes, sql, option);
        final Object[] realArgs = new Object[] { bean };
        handler.setExceptionMessageSqlArgs(realArgs);
        final int result = handler.execute(realArgs);
        return Integer.valueOf(result);
    }

    // ===================================================================================
    //                                                                       Update Column
    //                                                                       =============
    protected TnPropertyType[] createUpdatePropertyTypes(Object bean, UpdateOption<ConditionBean> option) {
        final Set<?> modifiedSet = getModifiedPropertyNames(bean);
        final List<TnPropertyType> typeList = new ArrayList<TnPropertyType>();
        final String timestampProp = _beanMetaData.getTimestampPropertyName();
        final String versionNoProp = _beanMetaData.getVersionNoPropertyName();
        final String[] propertyNames = _propertyNames;
        for (int i = 0; i < propertyNames.length; ++i) {
            final TnPropertyType pt = _beanMetaData.getPropertyType(propertyNames[i]);
            if (pt.isPrimaryKey()) {
                continue;
            }
            if (isOptimisticLockProperty(timestampProp, versionNoProp, pt) // OptimisticLock
                    || isSpecifiedProperty(option, modifiedSet, pt) // Specified
                    || isStatementProperty(option, pt)) { // Statement
                typeList.add(pt);
            }
        }
        return typeList.toArray(new TnPropertyType[typeList.size()]);
    }

    protected Set<?> getModifiedPropertyNames(Object bean) {
        return _beanMetaData.getModifiedPropertyNames(bean);
    }

    protected boolean isOptimisticLockProperty(String timestampProp, String versionNoProp, TnPropertyType pt) {
        final String propertyName = pt.getPropertyName();
        return propertyName.equalsIgnoreCase(timestampProp) || propertyName.equalsIgnoreCase(versionNoProp);
    }

    protected boolean isSpecifiedProperty(UpdateOption<ConditionBean> option, Set<?> modifiedSet, TnPropertyType pt) {
        if (option != null && option.hasSpecifiedUpdateColumn()) {
            return option.isSpecifiedUpdateColumn(pt.getColumnDbName());
        } else { // basically here when UpdateEntity
            return isModifiedProperty(modifiedSet, pt); // process for ModifiedColumnUpdate
        }
    }

    protected boolean isModifiedProperty(Set<?> modifiedSet, TnPropertyType pt) {
        return modifiedSet.contains(pt.getPropertyName());
    }

    protected boolean isStatementProperty(UpdateOption<ConditionBean> option, TnPropertyType pt) {
        return option != null && option.hasStatement(pt.getColumnDbName());
    }

    // ===================================================================================
    //                                                                          Update SQL
    //                                                                          ==========
    /**
     * Create update SQL. The update is by the primary keys.
     * @param propertyTypes The types of property for update. (NotNull)
     * @param option An option of update. (NullAllowed)
     * @return The update SQL. (NotNull)
     */
    protected String createUpdateSql(TnPropertyType[] propertyTypes, UpdateOption<ConditionBean> option) {
        final TableSqlName tableSqlName = _targetDBMeta.getTableSqlName();
        if (_beanMetaData.getPrimaryKeySize() == 0) {
            String msg = "The table '" + tableSqlName + "' does not have primary keys!";
            throw new IllegalStateException(msg);
        }
        final StringBuilder sb = new StringBuilder(100);
        sb.append("update ").append(tableSqlName).append(" set ");
        final String versionNoPropertyName = _beanMetaData.getVersionNoPropertyName();
        for (int i = 0; i < propertyTypes.length; i++) {
            final TnPropertyType pt = propertyTypes[i];
            final String columnDbName = pt.getColumnDbName();
            final ColumnSqlName columnSqlName = pt.getColumnSqlName();
            final String propertyName = pt.getPropertyName();
            if (i > 0) {
                sb.append(", ");
            }
            if (propertyName.equalsIgnoreCase(versionNoPropertyName)) {
                if (!isVersionNoAutoIncrementOnMemory()) {
                    setupVersionNoAutoIncrementOnQuery(sb, columnSqlName);
                    continue;
                }
            }
            if (option != null && option.hasStatement(columnDbName)) {
                final String statement = option.buildStatement(columnDbName);
                sb.append(columnSqlName).append(" = ").append(statement);
                continue;
            }
            sb.append(columnSqlName).append(" = ?");
        }
        sb.append(ln()).append(" where ");
        for (int i = 0; i < _beanMetaData.getPrimaryKeySize(); i++) { // never zero loop
            sb.append(_beanMetaData.getPrimaryKeySqlName(i)).append(" = ? and ");
        }
        sb.setLength(sb.length() - 5); // for deleting extra ' and '
        if (_optimisticLockHandling && _beanMetaData.hasVersionNoPropertyType()) {
            final TnPropertyType pt = _beanMetaData.getVersionNoPropertyType();
            sb.append(" and ").append(pt.getColumnSqlName()).append(" = ?");
        }
        if (_optimisticLockHandling && _beanMetaData.hasTimestampPropertyType()) {
            final TnPropertyType pt = _beanMetaData.getTimestampPropertyType();
            sb.append(" and ").append(pt.getColumnSqlName()).append(" = ?");
        }
        return sb.toString();
    }

    protected void setupVersionNoAutoIncrementOnQuery(StringBuilder sb, ColumnSqlName columnSqlName) {
        sb.append(columnSqlName).append(" = ").append(columnSqlName).append(" + 1");
    }

    // ===================================================================================
    //                                                                             Handler
    //                                                                             =======
    protected TnUpdateEntityHandler createUpdateEntityHandler(TnPropertyType[] boundPropTypes, String sql,
            UpdateOption<ConditionBean> option) {
        final TnUpdateEntityHandler handler = new TnUpdateEntityHandler(_dataSource, _statementFactory, sql,
                _beanMetaData, boundPropTypes);
        handler.setOptimisticLockHandling(_optimisticLockHandling); // [DBFlute-0.8.0]
        handler.setVersionNoAutoIncrementOnMemory(_versionNoAutoIncrementOnMemory);
        handler.setUpdateOption(option);
        return handler;
    }

    // ===================================================================================
    //                                                                  Non Update Message
    //                                                                  ==================
    protected String createNonUpdateLogMessage(final Object bean) {
        final StringBuilder sb = new StringBuilder();
        final String tableDbName = _targetDBMeta.getTableDbName();
        sb.append("...Skipping update because of non-modification: table=").append(tableDbName);
        if (_targetDBMeta.hasPrimaryKey() && (bean instanceof Entity)) {
            final Entity entity = (Entity) bean;
            final Map<String, Object> pkMap = _targetDBMeta.extractPrimaryKeyMap(entity);
            sb.append(", primaryKey=").append(pkMap);
        }
        return sb.toString();
    }

    protected Object getNonUpdateReturn() {
        return NON_UPDATE;
    }

    // ===================================================================================
    //                                                                  Execute Status Log
    //                                                                  ==================
    protected void log(String msg) {
        XLog.log(msg);
    }

    protected boolean isLogEnabled() {
        return XLog.isLogEnabled();
    }

    public void setOptimisticLockHandling(boolean optimisticLockHandling) {
        this._optimisticLockHandling = optimisticLockHandling;
    }

    protected boolean isVersionNoAutoIncrementOnMemory() {
        return _versionNoAutoIncrementOnMemory;
    }

    public void setVersionNoAutoIncrementOnMemory(boolean versionNoAutoIncrementOnMemory) {
        this._versionNoAutoIncrementOnMemory = versionNoAutoIncrementOnMemory;
    }
}
