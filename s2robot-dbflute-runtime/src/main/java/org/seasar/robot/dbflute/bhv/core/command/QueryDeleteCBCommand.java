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

import org.seasar.robot.dbflute.bhv.DeleteOption;
import org.seasar.robot.dbflute.bhv.core.SqlExecution;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.s2dao.sqlcommand.TnQueryDeleteDynamicCommand;

/**
 * @author jflute
 */
public class QueryDeleteCBCommand extends AbstractQueryEntityCBCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The option of delete. (NullAllowed) */
    protected DeleteOption<? extends ConditionBean> _deleteOption;

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "queryDelete";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================

    @Override
    protected SqlExecution createQueryEntityCBExecution() {
        return new TnQueryDeleteDynamicCommand(_dataSource, _statementFactory);
    }

    @Override
    protected Object[] doGetSqlExecutionArgument() {
        return new Object[] { _conditionBean, _deleteOption };
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    @Override
    protected void assertEntityProperty(String methodName) {
        // entity is not used here
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDeleteOption(DeleteOption<? extends ConditionBean> deleteOption) {
        _deleteOption = deleteOption;
    }
}
