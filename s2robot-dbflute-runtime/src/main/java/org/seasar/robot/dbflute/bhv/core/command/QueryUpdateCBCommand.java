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

import org.seasar.robot.dbflute.bhv.UpdateOption;
import org.seasar.robot.dbflute.bhv.core.SqlExecution;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.s2dao.sqlcommand.TnQueryUpdateDynamicCommand;

/**
 * @author jflute
 */
public class QueryUpdateCBCommand extends AbstractQueryEntityCBCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The option of update. (NotRequired) */
    protected UpdateOption<? extends ConditionBean> _updateOption;

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "queryUpdate";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================

    @Override
    protected SqlExecution createQueryEntityCBExecution() {
        final TnQueryUpdateDynamicCommand sqlCommand = new TnQueryUpdateDynamicCommand(_dataSource, _statementFactory);
        sqlCommand.setBeanMetaData(createBeanMetaData());
        return sqlCommand;
    }

    @Override
    protected Object[] doGetSqlExecutionArgument() {
        return new Object[] { _entity, _conditionBean, _updateOption };
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    @Override
    protected void assertStatus(String methodName) {
        super.assertStatus(methodName);
        if (_conditionBean == null) {
            throw new IllegalStateException(buildAssertMessage("_conditionBean", methodName));
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setConditionBean(ConditionBean conditionBean) {
        _conditionBean = conditionBean;
    }

    public void setUpdateOption(UpdateOption<? extends ConditionBean> updateOption) {
        _updateOption = updateOption;
    }
}
