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

import javax.sql.DataSource;

import org.seasar.robot.dbflute.bhv.InsertOption;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.s2dao.identity.TnIdentifierGenerator;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnInsertEntityHandler;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnInsertEntityDynamicCommand extends TnAbstractEntityDynamicCommand {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnInsertEntityDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
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
        final InsertOption<ConditionBean> option = extractInsertOptionChecked(args);

        final TnBeanMetaData bmd = _beanMetaData;
        final TnPropertyType[] propertyTypes = createInsertPropertyTypes(bmd, bean, _propertyNames, option);
        final String sql = createInsertSql(bmd, propertyTypes, option);
        return doExecute(bean, propertyTypes, sql, option);
    }

    protected InsertOption<ConditionBean> extractInsertOptionChecked(Object[] args) {
        if (args.length < 2 || args[1] == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        final InsertOption<ConditionBean> option = (InsertOption<ConditionBean>) args[1];
        return option;
    }

    protected Object doExecute(Object bean, TnPropertyType[] propertyTypes, String sql,
            InsertOption<ConditionBean> option) {
        final TnInsertEntityHandler handler = createInsertEntityHandler(propertyTypes, sql, option);
        final Object[] realArgs = new Object[] { bean };
        handler.setExceptionMessageSqlArgs(realArgs);
        final int rows = handler.execute(realArgs);
        return Integer.valueOf(rows);
    }

    // ===================================================================================
    //                                                                       Insert Column
    //                                                                       =============
    protected TnPropertyType[] createInsertPropertyTypes(TnBeanMetaData bmd, Object bean, String[] propertyNames,
            InsertOption<ConditionBean> option) {
        if (0 == propertyNames.length) {
            String msg = "The property name was not found in the bean: " + bean;
            throw new IllegalStateException(msg);
        }
        final List<TnPropertyType> typeList = new ArrayList<TnPropertyType>();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();

        for (int i = 0; i < propertyNames.length; ++i) {
            final TnPropertyType pt = bmd.getPropertyType(propertyNames[i]);
            if (pt.isPrimaryKey()) {
                if (option == null || !option.isPrimaryKeyIdentityDisabled()) {
                    final TnIdentifierGenerator generator = bmd.getIdentifierGenerator(pt.getPropertyName());
                    if (!generator.isSelfGenerate()) {
                        continue;
                    }
                }
            } else {
                if (isExceptProperty(bean, pt, timestampPropertyName, versionNoPropertyName)) {
                    continue;
                }
            }
            typeList.add(pt);
        }
        if (typeList.isEmpty()) {
            String msg = "The target property type was not found in the bean: " + bean;
            throw new IllegalStateException(msg);
        }
        return (TnPropertyType[]) typeList.toArray(new TnPropertyType[typeList.size()]);
    }

    protected boolean isExceptProperty(Object bean, TnPropertyType pt, String timestampPropertyName,
            String versionNoPropertyName) {
        if (isOptimisticLockProperty(pt, timestampPropertyName, versionNoPropertyName)) {
            return false;
        }
        return isNullProperty(bean, pt); // as default (only not null columns are target)
    }

    protected boolean isOptimisticLockProperty(TnPropertyType pt, String timestampPropertyName,
            String versionNoPropertyName) {
        final String propertyName = pt.getPropertyName();
        return propertyName.equalsIgnoreCase(timestampPropertyName)
                || propertyName.equalsIgnoreCase(versionNoPropertyName);
    }

    protected boolean isNullProperty(Object bean, TnPropertyType pt) {
        return pt.getPropertyDesc().getValue(bean) == null; // getting by reflection here
    }

    // ===================================================================================
    //                                                                          Insert SQL
    //                                                                          ==========
    protected String createInsertSql(TnBeanMetaData bmd, TnPropertyType[] propertyTypes,
            InsertOption<ConditionBean> option) {
        final StringBuilder sb = new StringBuilder(100);
        sb.append("insert into ");
        sb.append(_targetDBMeta.getTableSqlName());
        sb.append(" (");
        for (int i = 0; i < propertyTypes.length; ++i) {
            final TnPropertyType pt = propertyTypes[i];
            final ColumnSqlName columnSqlName = pt.getColumnSqlName();
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(columnSqlName);
        }
        sb.append(")").append(ln()).append(" values (");
        for (int i = 0; i < propertyTypes.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("?");
        }
        sb.append(")");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                             Handler
    //                                                                             =======
    protected TnInsertEntityHandler createInsertEntityHandler(TnPropertyType[] boundPropTypes, String sql,
            InsertOption<ConditionBean> option) {
        final TnInsertEntityHandler handler = new TnInsertEntityHandler(_dataSource, _statementFactory, sql,
                _beanMetaData, boundPropTypes);
        handler.setInsertOption(option);
        return handler;
    }
}
