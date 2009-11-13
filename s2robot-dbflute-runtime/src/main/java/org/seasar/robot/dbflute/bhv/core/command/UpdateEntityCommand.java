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
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.sqlcommand.TnUpdateModifiedOnlyCommand;


/**
 * @author jflute
 */
public class UpdateEntityCommand extends AbstractEntityCommand {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "update";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                final TnBeanMetaData bmd = createBeanMetaData();
                return createUpdateEntitySqlExecution(bmd);
            }
        };
    }

    protected SqlExecution createUpdateEntitySqlExecution(TnBeanMetaData bmd) {
        final String[] propertyNames = getPersistentPropertyNames(bmd);
        return createUpdateModifiedOnlyCommand(bmd, propertyNames);
    }

    protected TnUpdateModifiedOnlyCommand createUpdateModifiedOnlyCommand(TnBeanMetaData bmd, String[] propertyNames) {
        final TnUpdateModifiedOnlyCommand cmd = new TnUpdateModifiedOnlyCommand(_dataSource, _statementFactory);
        cmd.setBeanMetaData(bmd);// Extension Point!
        cmd.setTargetDBMeta(findDBMeta());
        cmd.setPropertyNames(propertyNames);
        cmd.setOptimisticLockHandling(isOptimisticLockHandling());
        cmd.setVersionNoAutoIncrementOnMemory(isOptimisticLockHandling());
        return cmd;
    }
    
    protected boolean isOptimisticLockHandling() {
        return true;
    }
}
