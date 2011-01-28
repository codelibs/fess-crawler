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
package org.seasar.robot.dbflute.bhv.core.command;

import java.util.List;

import org.seasar.robot.dbflute.bhv.InsertOption;
import org.seasar.robot.dbflute.bhv.core.SqlExecution;
import org.seasar.robot.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.sqlcommand.TnInsertEntityDynamicCommand;

/**
 * @author jflute
 */
public class InsertEntityCommand extends AbstractEntityCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The option of insert. (NotRequired) */
    protected InsertOption<? extends ConditionBean> _insertOption;

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "insert";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                final TnBeanMetaData bmd = createBeanMetaData();
                return createInsertEntitySqlExecution(bmd);
            }
        };
    }

    protected SqlExecution createInsertEntitySqlExecution(TnBeanMetaData bmd) {
        final SqlExecution nonPrimaryKeySqlExecution = createNonPrimaryInsertSqlExecution(bmd);
        if (nonPrimaryKeySqlExecution != null) {
            return nonPrimaryKeySqlExecution;
        }
        final String[] propertyNames = getPersistentPropertyNames(bmd);
        return createInsertEntityDynamicCommand(bmd, propertyNames);
    }

    protected TnInsertEntityDynamicCommand createInsertEntityDynamicCommand(TnBeanMetaData bmd, String[] propertyNames) {
        final TnInsertEntityDynamicCommand cmd = new TnInsertEntityDynamicCommand(_dataSource, _statementFactory);
        cmd.setBeanMetaData(bmd);
        cmd.setTargetDBMeta(findDBMeta());
        cmd.setPropertyNames(propertyNames);
        return cmd;
    }

    /**
     * @param bmd The meta data of bean. (NotNull)
     * @return Whether the method is target. (For example if it has primary key, returns false.)
     */
    protected SqlExecution createNonPrimaryInsertSqlExecution(TnBeanMetaData bmd) {
        final DBMeta dbmeta = findDBMeta();
        if (dbmeta.hasPrimaryKey()) {
            return null;
        }
        final List<ColumnInfo> columnInfoList = dbmeta.getColumnInfoList();
        final StringBuilder columnDefSb = new StringBuilder();
        for (org.seasar.robot.dbflute.dbmeta.info.ColumnInfo columnInfo : columnInfoList) {
            columnDefSb.append(", ").append(columnInfo.getColumnSqlName());
        }
        columnDefSb.delete(0, ", ".length()).insert(0, "(").append(")");
        final StringBuilder columnValuesSb = new StringBuilder();
        for (org.seasar.robot.dbflute.dbmeta.info.ColumnInfo columnInfo : columnInfoList) {
            columnValuesSb.append(", /*pmb.").append(columnInfo.getPropertyName()).append("*/null");
        }
        columnValuesSb.delete(0, ", ".length()).insert(0, "(").append(")");
        final String sql = "insert into " + dbmeta.getTableSqlName() + columnDefSb + " values" + columnValuesSb;
        return createOutsideSqlExecuteExecution(_entity.getClass(), sql);
    }

    @Override
    protected Object[] doGetSqlExecutionArgument() {
        return new Object[] { _entity, _insertOption };
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setInsertOption(InsertOption<? extends ConditionBean> insertOption) {
        _insertOption = insertOption;
    }
}
