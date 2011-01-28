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
package org.seasar.robot.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnCommandContextHandler;
import org.seasar.robot.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.robot.dbflute.twowaysql.node.Node;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnAbstractQueryDynamicCommand extends TnAbstractBasicSqlCommand {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractQueryDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    // ===================================================================================
    //                                                                      CommandContext
    //                                                                      ==============
    protected TnCommandContextHandler createCommandContextHandler(CommandContext context) {
        return new TnCommandContextHandler(_dataSource, _statementFactory, context.getSql(), context);
    }

    protected CommandContext createCommandContext(String twoWaySql, String[] argNames, Class<?>[] argTypes,
            Object[] args) {
        final CommandContext ctx;
        {
            final SqlAnalyzer analyzer = createSqlAnalyzer(twoWaySql);
            final Node node = analyzer.analyze();
            final CommandContextCreator creator = new CommandContextCreator(argNames, argTypes);
            ctx = creator.createCommandContext(args);
            node.accept(ctx);
        }
        return ctx;
    }

    protected SqlAnalyzer createSqlAnalyzer(String twoWaySql) {
        return ResourceContext.createSqlAnalyzer(twoWaySql, true);
    }
}
