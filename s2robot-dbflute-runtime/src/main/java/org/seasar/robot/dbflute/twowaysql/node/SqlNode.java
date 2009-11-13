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
package org.seasar.robot.dbflute.twowaysql.node;

import org.seasar.robot.dbflute.twowaysql.context.CommandContext;

/**
 * @author jflute
 */
public class SqlNode extends AbstractNode {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String sql;
    private boolean ifelseChildNode;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    private SqlNode(String sql) {
        this.sql = sql;
    }

    public static SqlNode createSqlNode(String sql) {
        return new SqlNode(sql);
    }

    public static SqlNode createSqlNodeAsIfElseChild(String sql) {
        return new SqlNode(sql).asIfElseChild();
    }

    private SqlNode asIfElseChild() {
        ifelseChildNode = true;
        return this;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(CommandContext ctx) {
        ctx.addSql(sql);

        if (ifelseChildNode && isBeginChildContextAndValidCoondition(ctx, sql)) {
            // It does not skipped actually but it has not already needed to skip.
            ctx.setAlreadySkippedPrefix(true);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getSql() {
        return sql;
    }
}
