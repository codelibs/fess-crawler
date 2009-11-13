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

import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.robot.dbflute.twowaysql.node.Node;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public abstract class TnAbstractDynamicCommand extends TnAbstractSqlCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Node rootNode;
    protected String[] argNames = new String[0];
    protected Class<?>[] argTypes = new Class[0];

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    // ===================================================================================
    //                                                                        Sql Handling
    //                                                                        ============
    public void setSql(String sql) {
        super.setSql(sql);
        this.rootNode = createSqlAnalyzer(sql).analyze();
    }

    protected SqlAnalyzer createSqlAnalyzer(String sql) {
        return ResourceContext.createSqlAnalyzer(sql, isBlockNullParameter());
    }

    protected boolean isBlockNullParameter() { // Extension Point!
        return false;
    }

    public CommandContext apply(Object[] args) { // It is necessary to be public!
        final CommandContext ctx = createCommandContext(args);
        rootNode.accept(ctx);
        return ctx;
    }

    protected CommandContext createCommandContext(Object[] args) {
        return createCommandContextCreator().createCommandContext(args);
    }

    protected CommandContextCreator createCommandContextCreator() {
        return new CommandContextCreator(argNames, argTypes);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String[] getArgNames() {
        return argNames;
    }

    public void setArgNames(String[] argNames) {
        this.argNames = argNames;
    }

    public Class<?>[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(Class<?>[] argTypes) {
        this.argTypes = argTypes;
    }
}
