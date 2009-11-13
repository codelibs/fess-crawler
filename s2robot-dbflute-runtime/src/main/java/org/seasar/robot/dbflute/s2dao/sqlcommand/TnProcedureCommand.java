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
package org.seasar.robot.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.bhv.core.SqlExecution;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.robot.dbflute.s2dao.procedure.TnProcedureMetaData;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnProcedureHandler;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnProcedureCommand implements TnSqlCommand, SqlExecution {

	// ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource dataSource;
    protected TnResultSetHandler resultSetHandler;
    protected StatementFactory statementFactory;
    protected TnProcedureMetaData procedureMetaData;

	// ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnProcedureCommand(DataSource dataSource, TnResultSetHandler resultSetHandler,
            StatementFactory statementFactory, TnProcedureMetaData procedureMetaData) {
        this.dataSource = dataSource;
        this.resultSetHandler = resultSetHandler;
        this.statementFactory = statementFactory;
        this.procedureMetaData = procedureMetaData;
    }

	// ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(final Object[] args) {
        final TnProcedureHandler handler = newArgumentDtoProcedureHandler();
        final OutsideSqlContext outsideSqlContext = OutsideSqlContext.getOutsideSqlContextOnThread();
        final Object pmb = outsideSqlContext.getParameterBean();
        // The logging message SQL of procedure is unnecessary.
        // handler.setLoggingMessageSqlArgs(...);
        return handler.execute(new Object[]{pmb});
    }
    protected TnProcedureHandler newArgumentDtoProcedureHandler() {
        return new TnProcedureHandler(dataSource, createSql(procedureMetaData), resultSetHandler,
                statementFactory, procedureMetaData);
    }
    protected String createSql(final TnProcedureMetaData procedureMetaData) {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        int size = procedureMetaData.parameterTypes().size();
        if (procedureMetaData.hasReturnParameterType()) {
            sb.append("? = ");
            size--;
        }
        sb.append("call ").append(procedureMetaData.getProcedureName()).append("(");
        for (int i = 0; i < size; i++) {
            sb.append("?, ");
        }
        if (size > 0) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(")}");
        return sb.toString();
    }
}
