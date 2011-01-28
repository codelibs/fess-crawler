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

import org.seasar.robot.dbflute.bhv.core.SqlExecution;
import org.seasar.robot.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public abstract class AbstractQueryEntityCBCommand extends AbstractEntityCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The instance of condition-bean for query. (NotNull) */
    protected ConditionBean _conditionBean;

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    @Override
    public boolean isConditionBean() {
        return true;
    }

    // ===================================================================================
    //                                                                    Process Callback
    //                                                                    ================
    @Override
    public void beforeGettingSqlExecution() {
        assertStatus("beforeGettingSqlExecution");
        final ConditionBean cb = _conditionBean;
        ConditionBeanContext.setConditionBeanOnThread(cb);
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    @Override
    public String buildSqlExecutionKey() {
        assertStatus("buildSqlExecutionKey");
        final String main = _tableDbName + ":" + getCommandName();
        final String entityName = DfTypeUtil.toClassTitle(_entity);
        final String cbName = DfTypeUtil.toClassTitle(_conditionBean);
        final String type = "(" + (entityName != null ? entityName + ", " : "") + cbName + ")";
        return main + type;
    }

    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                return createQueryEntityCBExecution();
            }
        };
    }

    protected abstract SqlExecution createQueryEntityCBExecution();

    // ===================================================================================
    //                                                                Argument Information
    //                                                                ====================
    @Override
    public ConditionBean getConditionBean() {
        return _conditionBean;
    }

    @Override
    public String getOutsideSqlPath() {
        return null;
    }

    @Override
    public OutsideSqlOption getOutsideSqlOption() {
        return null;
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    @Override
    protected void assertStatus(String methodName) {
        super.assertStatus(methodName);
        assertConditionBeanProperty(methodName);
    }

    protected void assertConditionBeanProperty(String methodName) {
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
}
