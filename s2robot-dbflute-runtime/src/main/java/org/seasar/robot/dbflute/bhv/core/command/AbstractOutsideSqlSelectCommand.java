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

import java.util.Map;

import org.seasar.robot.dbflute.bhv.core.SqlExecution;
import org.seasar.robot.dbflute.bhv.core.SqlExecutionCreator;
import org.seasar.robot.dbflute.bhv.core.execution.OutsideSqlSelectExecution;
import org.seasar.robot.dbflute.cbean.FetchAssistContext;
import org.seasar.robot.dbflute.cbean.FetchNarrowingBean;
import org.seasar.robot.dbflute.jdbc.FetchBean;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;

/**
 * The abstract command for OutsideSql.selectSomething().
 * @author jflute
 * @param <RESULT> The type of result.
 */
public abstract class AbstractOutsideSqlSelectCommand<RESULT> extends AbstractOutsideSqlCommand<RESULT> {

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    public boolean isProcedure() {
        return false;
    }

    public boolean isSelect() {
        return true;
    }

    // ===================================================================================
    //                                                                    Process Callback
    //                                                                    ================
    public void beforeGettingSqlExecution() {
        assertStatus("beforeGettingSqlExecution");
        OutsideSqlContext.setOutsideSqlContextOnThread(createOutsideSqlContext());

        // Set up fetchNarrowingBean.
        final Object pmb = _parameterBean;
        final OutsideSqlOption option = _outsideSqlOption;
        setupFetchBean(pmb, option);
    }

    @Override
    protected void setupOutsideSqlContextProperty(OutsideSqlContext outsideSqlContext) {
        super.setupOutsideSqlContextProperty(outsideSqlContext);
        outsideSqlContext.setResultType(getResultType());
        final OutsideSqlOption option = _outsideSqlOption;
        final boolean autoPagingLogging = (option.isAutoPaging() || option.isSourcePagingRequestTypeAuto());
        outsideSqlContext.setAutoPagingLogging(autoPagingLogging); // for logging
    }

    protected void setupFetchBean(Object pmb, OutsideSqlOption option) {
        if (pmb == null) {
            return;
        }
        if (pmb instanceof FetchBean) {
            FetchAssistContext.setFetchBeanOnThread((FetchBean) pmb);
            if (pmb instanceof FetchNarrowingBean && option.isManualPaging()) {
                ((FetchNarrowingBean) pmb).ignoreFetchNarrowing();
            }
        }
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
        final Class<?> resultType = getResultType();
        return OutsideSqlContext.generateSpecifiedOutsideSqlUniqueKey(methodName, path, pmb, option, resultType);
    }

    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                final OutsideSqlContext outsideSqlContext = OutsideSqlContext.getOutsideSqlContextOnThread();
                return createOutsideSqlSelectExecution(outsideSqlContext);
            }
        };
    }

    protected SqlExecution createOutsideSqlSelectExecution(OutsideSqlContext outsideSqlContext) {
        // - - - - - - - - - - - - - - - - - - - - - - -
        // The attribute of Specified-OutsideSqlContext.
        // - - - - - - - - - - - - - - - - - - - - - - -
        final Object pmb = outsideSqlContext.getParameterBean();
        final String suffix = buildDbmsSuffix();
        final String sql = outsideSqlContext.readFilteredOutsideSql(_sqlFileEncoding, suffix);

        // - - - - - - - - - - - - -
        // Create ResultSetHandler.
        // - - - - - - - - - - - - -
        final TnResultSetHandler handler = createOutsideSqlSelectResultSetHandler();

        // - - - - - - - - - - -
        // Create SqlExecution.
        // - - - - - - - - - - -
        final OutsideSqlSelectExecution execution = createOutsideSqlSelectExecution(pmb, sql, handler);
        execution.setRemoveBlockComment(isRemoveBlockComment(outsideSqlContext));
        execution.setRemoveLineComment(isRemoveLineComment(outsideSqlContext));
        execution.setFormatSql(outsideSqlContext.isFormatSql());
        execution.setOutsideSqlFilter(_outsideSqlFilter);
        return execution;
    }

    protected OutsideSqlSelectExecution createOutsideSqlSelectExecution(Object pmbTypeObj, String sql,
            TnResultSetHandler handler) {
        final Map<String, Class<?>> argNameTypeMap = createBeanArgNameTypeMap(pmbTypeObj);
        return new OutsideSqlSelectExecution(_dataSource, _statementFactory, argNameTypeMap, sql, handler);
    }

    public Object[] getSqlExecutionArgument() {
        assertStatus("getSqlExecutionArgument");
        return new Object[] { _parameterBean };
    }

    // ===================================================================================
    //                                                                     Extension Point
    //                                                                     ===============
    protected abstract TnResultSetHandler createOutsideSqlSelectResultSetHandler();

    protected abstract Class<?> getResultType();

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertStatus(String methodName) {
        assertBasicProperty(methodName);
        assertComponentProperty(methodName);
        assertOutsideSqlBasic(methodName);
    }
}
