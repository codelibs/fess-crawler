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

import java.util.List;

import org.seasar.robot.dbflute.bhv.core.SqlExecution;
import org.seasar.robot.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.sqlcommand.TnInsertAutoDynamicCommand;

/**
 * @author jflute
 */
public class InsertEntityCommand extends AbstractEntityCommand {

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
        return createInsertAutoDynamicCommand(bmd, propertyNames);
    }

    protected TnInsertAutoDynamicCommand createInsertAutoDynamicCommand(TnBeanMetaData bmd, String[] propertyNames) {
        final TnInsertAutoDynamicCommand cmd = new TnInsertAutoDynamicCommand();
        cmd.setDataSource(_dataSource);
        cmd.setStatementFactory(_statementFactory);
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
            columnDefSb.append(", ").append(columnInfo.getColumnDbName());
        }
        columnDefSb.delete(0, ", ".length()).insert(0, "(").append(")");
        final StringBuilder columnValuesSb = new StringBuilder();
        for (org.seasar.robot.dbflute.dbmeta.info.ColumnInfo columnInfo : columnInfoList) {
            columnValuesSb.append(", /*pmb.").append(columnInfo.getPropertyName()).append("*/null");
        }
        columnValuesSb.delete(0, ", ".length()).insert(0, "(").append(")");
        final String sql = "insert into " + dbmeta.getTableSqlName() + columnDefSb + " values" + columnValuesSb;
        return createUpdateDynamicCommand(new String[] { "pmb" }, new Class<?>[] { _entityType }, sql);
    }
}
