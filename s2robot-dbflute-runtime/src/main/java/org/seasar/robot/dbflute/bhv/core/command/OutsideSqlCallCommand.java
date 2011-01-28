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
import org.seasar.robot.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnProcedureMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnProcedureMetaDataFactory;
import org.seasar.robot.dbflute.s2dao.rshandler.TnMapListResultSetHandler;
import org.seasar.robot.dbflute.s2dao.sqlcommand.TnProcedureCommand;
import org.seasar.robot.dbflute.s2dao.sqlcommand.TnProcedureCommand.TnProcedureResultSetHandlerFactory;

/**
 * The behavior command for OutsideSql.execute().
 * @author jflute
 */
public class OutsideSqlCallCommand extends AbstractOutsideSqlCommand<Void> {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "call";
    }

    public Class<?> getCommandReturnType() {
        return void.class;
    }

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    public boolean isProcedure() {
        return true;
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
                return createOutsideSqlProcedureExecution(outsideSqlContext);
            }
        };
    }

    protected SqlExecution createOutsideSqlProcedureExecution(OutsideSqlContext outsideSqlContext) {
        // - - - - - - - - - - - - - - - - - - - - - - -
        // The attribute of Specified-OutsideSqlContext.
        // - - - - - - - - - - - - - - - - - - - - - - -
        final Object pmb = outsideSqlContext.getParameterBean();
        final String procedureName = outsideSqlContext.getOutsideSqlPath();

        // - - - - - - - - - - - - - - -
        // The attribute of SqlCommand.
        // - - - - - - - - - - - - - - -
        final TnProcedureMetaDataFactory factory = createProcedureMetaDataFactory();
        final Class<?> pmbType = (pmb != null ? pmb.getClass() : null);
        final TnProcedureMetaData metaData = factory.createProcedureMetaData(procedureName, pmbType);
        return createProcedureCommand(metaData);
    }

    protected TnProcedureMetaDataFactory createProcedureMetaDataFactory() {
        return new TnProcedureMetaDataFactory();
    }

    protected TnProcedureCommand createProcedureCommand(TnProcedureMetaData metaData) {
        final TnProcedureResultSetHandlerFactory factory = createProcedureResultSetHandlerFactory();
        final TnProcedureCommand cmd = new TnProcedureCommand(_dataSource, _statementFactory, metaData, factory);
        cmd.setOutsideSqlFilter(_outsideSqlFilter);
        return cmd;
    }

    protected TnProcedureResultSetHandlerFactory createProcedureResultSetHandlerFactory() {
        return new TnProcedureResultSetHandlerFactory() {
            public TnResultSetHandler createBeanHandler(Class<?> beanClass) {
                final TnBeanMetaData beanMetaData = _beanMetaDataFactory.createBeanMetaData(beanClass);
                return createBeanListResultSetHandler(beanMetaData);
            }

            public TnResultSetHandler createMapHandler() {
                return new TnMapListResultSetHandler();
            }
        };
    }

    // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // TnProcedureCommand switches argument so this is unnecessary actually!
    // - - - - - - - - - -/
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
        if (_parameterBean == null) {
            String msg = "The property 'parameterBean' should not be null";
            msg = msg + " when you call " + methodName + "().";
            throw new IllegalStateException(msg);
        }
    }
}
