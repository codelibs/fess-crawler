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
package org.seasar.robot.dbflute.s2dao.sqlcommand;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.XLog;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnUpdateAutoHandler;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.DfTraceViewUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnUpdateAutoDynamicCommand extends TnAbstractSqlCommand {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The result for no update as normal execution. */
    private static final Integer NO_UPDATE = Integer.valueOf(1);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private TnBeanMetaData beanMetaData;
    private DBMeta targetDBMeta;
    private String[] propertyNames;
    private boolean optimisticLockHandling;
    private boolean versionNoAutoIncrementOnMemory;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnUpdateAutoDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) {
        final Object bean = args[0];
        final TnBeanMetaData bmd = getBeanMetaData();
        final TnPropertyType[] propertyTypes = createUpdatePropertyTypes(bmd, bean, getPropertyNames());
        if (propertyTypes.length == 0) {
            if (isLogEnabled()) {
                log(createNoUpdateLogMessage(bean, bmd));
            }
            return NO_UPDATE;
        }
        TnUpdateAutoHandler handler = createInternalUpdateAutoHandler(bmd, propertyTypes);
        handler.setSql(createUpdateSql(bmd, propertyTypes, bean));
        handler.setLoggingMessageSqlArgs(args);
        int i = handler.execute(args);

        // [Comment Out]: This statement moved to the handler at [DBFlute-0.8.0].
        //if (isCheckSingleRowUpdate() && i < 1) {
        //    throw createNotSingleRowUpdatedRuntimeException(args[0], i);
        //}

        return Integer.valueOf(i);
    }

    protected TnUpdateAutoHandler createInternalUpdateAutoHandler(TnBeanMetaData bmd, TnPropertyType[] propertyTypes) {
        TnUpdateAutoHandler handler = new TnUpdateAutoHandler(getDataSource(), getStatementFactory(), bmd,
                propertyTypes);
        handler.setOptimisticLockHandling(optimisticLockHandling); // [DBFlute-0.8.0]
        handler.setVersionNoAutoIncrementOnMemory(versionNoAutoIncrementOnMemory);
        return handler;
    }

    protected TnPropertyType[] createUpdatePropertyTypes(TnBeanMetaData bmd, Object bean, String[] propertyNames) {
        final List<TnPropertyType> types = new ArrayList<TnPropertyType>();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < propertyNames.length; ++i) {
            TnPropertyType pt = bmd.getPropertyType(propertyNames[i]);
            if (pt.isPrimaryKey() == false) {
                String propertyName = pt.getPropertyName();
                if (propertyName.equalsIgnoreCase(timestampPropertyName)
                        || propertyName.equalsIgnoreCase(versionNoPropertyName)
                        || pt.getPropertyDesc().getValue(bean) != null) {
                    types.add(pt);
                }
            }
        }
        if (types.isEmpty()) {
            String msg = "The property type for update was not found:";
            msg = msg + " propertyNames=" + DfTraceViewUtil.convertObjectArrayToStringView(propertyNames);
            throw new IllegalStateException(msg);
        }
        TnPropertyType[] propertyTypes = (TnPropertyType[]) types.toArray(new TnPropertyType[types.size()]);
        return propertyTypes;
    }

    protected String createNoUpdateLogMessage(final Object bean, final TnBeanMetaData bmd) {
        final StringBuffer sb = new StringBuffer();
        sb.append("...Skipping update because of no modification: table=").append(targetDBMeta.getTableSqlName());
        final int size = bmd.getPrimaryKeySize();
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                sb.append(", primaryKey={");
            } else {
                sb.append(", ");
            }
            final String keyName = bmd.getPrimaryKey(i);
            sb.append(keyName).append("=");
            sb.append(bmd.getPropertyTypeByColumnName(keyName).getPropertyDesc().getValue(bean));
            if (i == size - 1) {
                sb.append("}");
            }
        }
        final String s = new String(sb);
        return s;
    }

    /**
     * Create update SQL. The update is by the primary keys.
     * @param bmd The meta data of bean. (NotNull & RequiredPrimaryKeys)
     * @param propertyTypes The types of property for update. (NotNull)
     * @param bean A bean for update for handling version no and so on. (NotNull)
     * @return The update SQL. (NotNull)
     */
    protected String createUpdateSql(TnBeanMetaData bmd, TnPropertyType[] propertyTypes, Object bean) {
        if (bmd.getPrimaryKeySize() == 0) {
            String msg = "The table '" + targetDBMeta.getTableSqlName() + "' does not have primary keys!";
            throw new IllegalStateException(msg);
        }
        final StringBuilder sb = new StringBuilder(100);
        sb.append("update ");
        sb.append(targetDBMeta.getTableSqlName());
        sb.append(" set ");
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < propertyTypes.length; ++i) {
            TnPropertyType pt = propertyTypes[i];
            final String columnName = pt.getColumnName();
            if (i > 0) {
                sb.append(", ");
            }
            if (pt.getPropertyName().equalsIgnoreCase(versionNoPropertyName)) {
                if (!isVersionNoAutoIncrementOnMemory()) {
                    setupVersionNoAutoIncrementOnQuery(sb, columnName);
                    continue;
                }
                final Object versionNo = pt.getPropertyDesc().getValue(bean);
                if (versionNo == null) {
                    setupVersionNoAutoIncrementOnQuery(sb, columnName);
                    continue;
                }
            }
            sb.append(columnName).append(" = ?");
        }
        sb.append(getLineSeparator()).append(" where ");
        for (int i = 0; i < bmd.getPrimaryKeySize(); ++i) { // never zero loop
            sb.append(bmd.getPrimaryKey(i)).append(" = ? and ");
        }
        sb.setLength(sb.length() - 5); // for deleting extra ' and '
        if (optimisticLockHandling && bmd.hasVersionNoPropertyType()) {
            TnPropertyType pt = bmd.getVersionNoPropertyType();
            sb.append(" and ").append(pt.getColumnName()).append(" = ?");
        }
        if (optimisticLockHandling && bmd.hasTimestampPropertyType()) {
            TnPropertyType pt = bmd.getTimestampPropertyType();
            sb.append(" and ").append(pt.getColumnName()).append(" = ?");
        }
        return sb.toString();
    }

    protected void setupVersionNoAutoIncrementOnQuery(StringBuilder sb, String columnName) {
        sb.append(columnName).append(" = ").append(columnName).append(" + 1");
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

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String getLineSeparator() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public TnBeanMetaData getBeanMetaData() {
        return beanMetaData;
    }

    public void setBeanMetaData(TnBeanMetaData beanMetaData) {
        this.beanMetaData = beanMetaData;
    }

    public DBMeta getTargetDBMeta() {
        return targetDBMeta;
    }

    public void setTargetDBMeta(DBMeta targetDBMeta) {
        this.targetDBMeta = targetDBMeta;
    }

    public String[] getPropertyNames() {
        return propertyNames;
    }

    public void setPropertyNames(String[] propertyNames) {
        this.propertyNames = propertyNames;
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
