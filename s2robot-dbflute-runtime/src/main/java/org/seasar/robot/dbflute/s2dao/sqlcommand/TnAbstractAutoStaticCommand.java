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
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.exception.EntityAlreadyUpdatedException;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.s2dao.identity.TnIdentifierGenerator;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnAbstractAutoHandler;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public abstract class TnAbstractAutoStaticCommand extends TnAbstractStaticCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private DBMeta targetDBMeta;
    private TnPropertyType[] propertyTypes;
    protected boolean optimisticLockHandling;
    protected boolean versionNoAutoIncrementOnMemory;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractAutoStaticCommand(DataSource dataSource, StatementFactory statementFactory,
            TnBeanMetaData beanMetaData, DBMeta targetDBMeta, String[] propertyNames, boolean optimisticLockHandling,
            boolean versionNoAutoIncrementOnMemory) {
        super(dataSource, statementFactory, beanMetaData);
        this.targetDBMeta = targetDBMeta;
        this.optimisticLockHandling = optimisticLockHandling;
        this.versionNoAutoIncrementOnMemory = versionNoAutoIncrementOnMemory;
        setupPropertyTypes(propertyNames);
        setupSql();
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) { // NOT for Batch. Batch should override.
        TnAbstractAutoHandler handler = createAutoHandler();
        handler.setOptimisticLockHandling(optimisticLockHandling);
        handler.setVersionNoAutoIncrementOnMemory(versionNoAutoIncrementOnMemory);
        handler.setSql(getSql());
        handler.setLoggingMessageSqlArgs(args);
        int rows = handler.execute(args);
        return Integer.valueOf(rows);
    }

    protected EntityAlreadyUpdatedException createEntityAlreadyUpdatedException(Object bean, int rows) {
        return new EntityAlreadyUpdatedException(bean, rows);
    }

    protected TnPropertyType[] getPropertyTypes() {
        return propertyTypes;
    }

    protected void setPropertyTypes(TnPropertyType[] propertyTypes) {
        this.propertyTypes = propertyTypes;
    }

    protected abstract TnAbstractAutoHandler createAutoHandler();

    protected abstract void setupPropertyTypes(String[] propertyNames);

    protected void setupInsertPropertyTypes(String[] propertyNames) {
        List<TnPropertyType> types = new ArrayList<TnPropertyType>();
        for (int i = 0; i < propertyNames.length; ++i) {
            TnPropertyType pt = getBeanMetaData().getPropertyType(propertyNames[i]);
            if (isInsertTarget(pt)) {
                types.add(pt);
            }
        }
        propertyTypes = (TnPropertyType[]) types.toArray(new TnPropertyType[types.size()]);
    }

    protected boolean isInsertTarget(TnPropertyType propertyType) {
        if (propertyType.isPrimaryKey()) {
            String name = propertyType.getPropertyName();
            final TnIdentifierGenerator generator = getBeanMetaData().getIdentifierGenerator(name);
            return generator.isSelfGenerate();
        }
        return true;
    }

    protected void setupUpdatePropertyTypes(String[] propertyNames) {
        List<TnPropertyType> types = new ArrayList<TnPropertyType>();
        for (int i = 0; i < propertyNames.length; ++i) {
            TnPropertyType pt = getBeanMetaData().getPropertyType(propertyNames[i]);
            if (pt.isPrimaryKey()) {
                continue;
            }
            types.add(pt);
        }
        if (types.size() == 0) {
            String msg = "The property type that is not primary key was not found:";
            msg = msg + " propertyNames=" + Arrays.asList(propertyNames);
            throw new IllegalStateException(msg);
        }
        propertyTypes = (TnPropertyType[]) types.toArray(new TnPropertyType[types.size()]);
    }

    protected void setupDeletePropertyTypes(String[] propertyNames) {
    }

    protected abstract void setupSql();

    protected void setupInsertSql() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("insert into ");
        sb.append(targetDBMeta.getTableSqlName());
        sb.append(" (");
        for (int i = 0; i < propertyTypes.length; ++i) {
            TnPropertyType pt = propertyTypes[i];
            if (isInsertTarget(pt)) {
                sb.append(pt.getColumnName());
                sb.append(", ");
            }
        }
        sb.setLength(sb.length() - 2);
        sb.append(") values (");
        for (int i = 0; i < propertyTypes.length; ++i) {
            TnPropertyType pt = propertyTypes[i];
            if (isInsertTarget(pt)) {
                sb.append("?, ");
            }
        }
        sb.setLength(sb.length() - 2);
        sb.append(")");
        setSql(sb.toString());
    }

    protected void setupUpdateSql() {
        checkPrimaryKey();
        StringBuilder sb = new StringBuilder(100);
        sb.append("update ");
        sb.append(targetDBMeta.getTableSqlName());
        sb.append(" set ");
        String versionNoPropertyName = getBeanMetaData().getVersionNoPropertyName();
        for (int i = 0; i < propertyTypes.length; ++i) {
            TnPropertyType pt = propertyTypes[i];
            if (pt.getPropertyName().equalsIgnoreCase(versionNoPropertyName) && !versionNoAutoIncrementOnMemory) {
                sb.append(pt.getColumnName()).append(" = ").append(pt.getColumnName()).append(" + 1, ");
                continue;
            }
            sb.append(pt.getColumnName()).append(" = ?, ");
        }
        sb.setLength(sb.length() - 2);
        setupUpdateWhere(sb);
        setSql(sb.toString());
    }

    protected void setupDeleteSql() {
        checkPrimaryKey();
        final StringBuilder sb = new StringBuilder(100);
        sb.append("delete from ");
        sb.append(targetDBMeta.getTableSqlName());
        setupUpdateWhere(sb);
        setSql(sb.toString());
    }

    protected void checkPrimaryKey() {
        TnBeanMetaData bmd = getBeanMetaData();
        if (bmd.getPrimaryKeySize() == 0) {
            String msg = "The primary key was not found:";
            msg = msg + " bean=" + bmd.getBeanClass();
            throw new IllegalStateException(msg);
        }
    }

    protected void setupUpdateWhere(StringBuilder sb) {
        TnBeanMetaData bmd = getBeanMetaData();
        sb.append(" where ");
        for (int i = 0; i < bmd.getPrimaryKeySize(); ++i) {
            sb.append(bmd.getPrimaryKey(i)).append(" = ? and ");
        }
        sb.setLength(sb.length() - 5);
        if (optimisticLockHandling && bmd.hasVersionNoPropertyType()) {
            TnPropertyType pt = bmd.getVersionNoPropertyType();
            sb.append(" and ").append(pt.getColumnName()).append(" = ?");
        }
        if (optimisticLockHandling && bmd.hasTimestampPropertyType()) {
            TnPropertyType pt = bmd.getTimestampPropertyType();
            sb.append(" and ").append(pt.getColumnName()).append(" = ?");
        }
    }
}
