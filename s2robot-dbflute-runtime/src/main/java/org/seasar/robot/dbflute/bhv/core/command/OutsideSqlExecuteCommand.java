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
import org.seasar.robot.dbflute.bhv.core.execution.OutsideSqlExecuteExecution;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlOption;

/**
 * The behavior command for OutsideSql.execute().
 * @author jflute
 */
public class OutsideSqlExecuteCommand extends AbstractOutsideSqlCommand<Integer> {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "execute";
    }

    public Class<?> getCommandReturnType() {
        return Integer.class;
    }

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    public boolean isProcedure() {
        return false;
    }

    public boolean isSelect() {
        return false;
    }

    // ===================================================================================
    //                                                                    Process Callback
    //                                                                    ================
    public void beforeGettingSqlExecution() {
        assertStatus("beforeGettingSqlExecution");
        OutsideSqlContext.setOutsideSqlContextOnThread(createOutsideSqlContext());
    }

    public void afterExecuting() {
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public String buildSqlExecutionKey() {
        assertStatus("buildSqlExecutionKey");
        return generateSpecifiedOutsideSqlUniqueKey();
    }

    protected String generateSpecifiedOutsideSqlUniqueKey() {
        final String methodName = getCommandName();
        final String path = _outsideSqlPath;
        final Object pmb = _parameterBean;
        final OutsideSqlOption option = _outsideSqlOption;
        return OutsideSqlContext.generateSpecifiedOutsideSqlUniqueKey(methodName, path, pmb, option, null);
    }

    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                final OutsideSqlContext outsideSqlContext = OutsideSqlContext.getOutsideSqlContextOnThread();
                return createOutsideSqlExecuteExecution(outsideSqlContext);
            }
        };
    }

    protected SqlExecution createOutsideSqlExecuteExecution(OutsideSqlContext outsideSqlContext) {
        final Object pmb = outsideSqlContext.getParameterBean();
        final String suffix = buildDbmsSuffix();
        final String sql = outsideSqlContext.readFilteredOutsideSql(_sqlFileEncoding, suffix);

        final OutsideSqlExecuteExecution execution = createOutsideSqlExecuteExecution(pmb, sql);
        execution.setOutsideSqlFilter(_outsideSqlFilter);
        execution.setRemoveBlockComment(isRemoveBlockComment(outsideSqlContext));
        execution.setRemoveLineComment(isRemoveLineComment(outsideSqlContext));
        execution.setFormatSql(outsideSqlContext.isFormatSql());
        return execution;
    }

    public Object[] getSqlExecutionArgument() {
        return new Object[] { _parameterBean };
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertStatus(String methodName) {
        assertBasicProperty(methodName);
        assertComponentProperty(methodName);
        assertOutsideSqlBasic(methodName);
    }
}
