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

import javax.sql.DataSource;

import org.seasar.robot.dbflute.bhv.DeleteOption;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnAbstractEntityHandler;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnAbstractEntityStaticCommand extends TnAbstractBasicSqlCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final TnBeanMetaData _beanMetaData;
    protected final DBMeta _targetDBMeta;
    protected final boolean _optimisticLockHandling;
    protected final boolean _versionNoAutoIncrementOnMemory;

    // initialized (required) in a process called by constructor
    protected TnPropertyType[] _propertyTypes;
    protected String _sql;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractEntityStaticCommand(DataSource dataSource, StatementFactory statementFactory,
            TnBeanMetaData beanMetaData, DBMeta targetDBMeta, String[] propertyNames, boolean optimisticLockHandling,
            boolean versionNoAutoIncrementOnMemory) {
        super(dataSource, statementFactory);
        assertObjectNotNull("targetDBMeta", targetDBMeta);
        assertObjectNotNull("propertyNames", propertyNames); // not null but empty allowed
        _targetDBMeta = targetDBMeta;
        _optimisticLockHandling = optimisticLockHandling;
        _versionNoAutoIncrementOnMemory = versionNoAutoIncrementOnMemory;
        _beanMetaData = beanMetaData;
        setupPropertyTypes(propertyNames);
        setupSql();
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) {
        final TnAbstractEntityHandler handler = createEntityHandler(args);
        return doExecute(args, handler);
    }

    protected Object doExecute(Object[] args, TnAbstractEntityHandler handler) {
        handler.setExceptionMessageSqlArgs(args);
        final int rows = handler.execute(args);
        return Integer.valueOf(rows);
    }

    protected TnAbstractEntityHandler createEntityHandler(Object[] args) {
        final TnAbstractEntityHandler handler = newEntityHandler();
        handler.setOptimisticLockHandling(_optimisticLockHandling);
        handler.setVersionNoAutoIncrementOnMemory(_versionNoAutoIncrementOnMemory);
        handler.setExceptionMessageSqlArgs(args);
        return handler;
    }

    protected abstract TnAbstractEntityHandler newEntityHandler();

    protected void setupPropertyTypes(String[] propertyNames) { // called by constructor
        _propertyTypes = new TnPropertyType[] {}; // as default
    }

    protected abstract void setupSql(); // called by constructor

    // ===================================================================================
    //                                                                              Insert
    //                                                                              ======
    // *static insert is unused on DBFlute

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    // *static update is unused on DBFlute

    // ===================================================================================
    //                                                                              Delete
    //                                                                              ======
    protected void setupDeleteSql() {
        checkPrimaryKey();
        final StringBuilder sb = new StringBuilder(100);
        sb.append("delete from ");
        sb.append(_targetDBMeta.getTableSqlName());
        setupDeleteWhere(sb);
        _sql = sb.toString();
    }

    protected void checkPrimaryKey() {
        final TnBeanMetaData bmd = _beanMetaData;
        if (bmd.getPrimaryKeySize() == 0) {
            String msg = "The primary key was not found:";
            msg = msg + " bean=" + bmd.getBeanClass();
            throw new IllegalStateException(msg);
        }
    }

    protected void setupDeleteWhere(StringBuilder sb) {
        final TnBeanMetaData bmd = _beanMetaData;
        sb.append(" where ");
        for (int i = 0; i < bmd.getPrimaryKeySize(); ++i) {
            sb.append(bmd.getPrimaryKeySqlName(i)).append(" = ? and ");
        }
        sb.setLength(sb.length() - 5);
        if (_optimisticLockHandling && bmd.hasVersionNoPropertyType()) {
            TnPropertyType pt = bmd.getVersionNoPropertyType();
            sb.append(" and ").append(pt.getColumnSqlName()).append(" = ?");
        }
        if (_optimisticLockHandling && bmd.hasTimestampPropertyType()) {
            TnPropertyType pt = bmd.getTimestampPropertyType();
            sb.append(" and ").append(pt.getColumnSqlName()).append(" = ?");
        }
    }

    protected DeleteOption<ConditionBean> extractDeleteOption(Object[] args) {
        if (args.length < 2 || args[1] == null) {
            return null;
        }
        // should be same as fixed option about static options,
        // for example, PrimaryKeyIdentityDisabled
        @SuppressWarnings("unchecked")
        final DeleteOption<ConditionBean> option = (DeleteOption<ConditionBean>) args[1];
        return option;
    }
}
