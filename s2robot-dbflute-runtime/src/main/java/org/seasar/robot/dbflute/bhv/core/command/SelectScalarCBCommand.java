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
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;

/**
 * @author jflute
 * @param <RESULT> The type of result.
 */
public class SelectScalarCBCommand<RESULT> extends AbstractSelectCBCommand<RESULT> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The type of result. (NotNull) */
    protected Class<RESULT> _resultType;

    /** The type of select clause. (NotNull) */
    protected SqlClause.SelectClauseType _selectClauseType;

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        assertStatus("getCommandName");
        final String resultTypeName = _resultType.getSimpleName();
        final String scalarMethodName = _selectClauseType.toString().toLowerCase();
        return "scalarSelect(" + resultTypeName + ")." + scalarMethodName;
    }

    public Class<?> getCommandReturnType() {
        assertStatus("getCommandReturnType");
        return _resultType;
    }

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    public boolean isSelectCount() {
        return false;
    }

    // ===================================================================================
    //                                                                    Process Callback
    //                                                                    ================
    public void beforeGettingSqlExecution() {
        assertStatus("beforeGettingSqlExecution");
        final ConditionBean cb = _conditionBean;
        ConditionBeanContext.setConditionBeanOnThread(cb);
        cb.getSqlClause().classifySelectClauseType(_selectClauseType); // *Point!
    }

    public void afterExecuting() {
        assertStatus("afterExecuting");
        final ConditionBean cb = _conditionBean;
        cb.getSqlClause().rollbackSelectClauseType();
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                TnResultSetHandler handler = createObjectResultSetHandler(getCommandReturnType());
                return createSelectCBExecution(_conditionBeanType, handler);
            }
        };
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    @Override
    protected void assertStatus(String methodName) {
        super.assertStatus(methodName);
        if (_resultType == null) {
            throw new IllegalStateException(buildAssertMessage("_resultType", methodName));
        }
        if (_selectClauseType == null) {
            throw new IllegalStateException(buildAssertMessage("_selectClauseType", methodName));
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setResultType(Class<RESULT> resultType) {
        _resultType = resultType;
    }

    public void setSelectClauseType(SqlClause.SelectClauseType selectClauseType) {
        _selectClauseType = selectClauseType;
    }
}
