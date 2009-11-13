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

import org.seasar.robot.dbflute.bhv.core.SqlExecution;
import org.seasar.robot.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.sqlcommand.TnUpdateBatchAutoStaticCommand;


/**
 * @author jflute
 */
public class BatchUpdateEntityCommand extends AbstractListEntityCommand {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "batchUpdate";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                final TnBeanMetaData bmd = createBeanMetaData();
                return createBatchUpdateEntitySqlExecution(bmd);
            }
        };
    }

    protected SqlExecution createBatchUpdateEntitySqlExecution(TnBeanMetaData bmd) {
        final String[] propertyNames = getPersistentPropertyNames(bmd);
        return createUpdateBatchAutoStaticCommand(bmd, propertyNames);
    }

    protected TnUpdateBatchAutoStaticCommand createUpdateBatchAutoStaticCommand(TnBeanMetaData bmd, String[] propertyNames) {
        final DBMeta dbmeta = findDBMeta();
        final boolean opt = isOptimisticLockHandling();
        return new TnUpdateBatchAutoStaticCommand(_dataSource, _statementFactory, bmd, dbmeta, propertyNames, opt, opt);
    }

    protected boolean isOptimisticLockHandling() {
        return true;
    }
}
