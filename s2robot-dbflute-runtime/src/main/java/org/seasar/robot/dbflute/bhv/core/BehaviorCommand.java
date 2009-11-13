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
package org.seasar.robot.dbflute.bhv.core;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlOption;

/**
 * @author jflute
 * @param <RESULT> The type of result.
 */
public interface BehaviorCommand<RESULT> {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getTableDbName();
    public String getCommandName();
    
    /**
     * Get the return type of command.
     * This type is not related to generic type because this is for conversion and check only.
     * @return The return type of command. (NotNull)
     */
    public Class<?> getCommandReturnType();

    public boolean isInitializeOnly();

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    public boolean isConditionBean();
    public boolean isOutsideSql();
    public boolean isProcedure();
    public boolean isSelect();
    public boolean isSelectCount();

    // ===================================================================================
    //                                                                    Process Callback
    //                                                                    ================
    public void beforeGettingSqlExecution();
    public void afterExecuting();

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public String buildSqlExecutionKey();
    public SqlExecutionCreator createSqlExecutionCreator();
    public Object[] getSqlExecutionArgument();

    // ===================================================================================
    //                                                                Argument Information
    //                                                                ====================
    public ConditionBean getConditionBean();
    public String getOutsideSqlPath();
    public OutsideSqlOption getOutsideSqlOption();
}
