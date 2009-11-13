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
 * The if-else child node of prefix SQL.
 * @author jflute
 */
public class PrefixSqlNode extends AbstractNode {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String prefix;
    private String sql;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public PrefixSqlNode(String prefix, String sql) {
        this.prefix = prefix;
        this.sql = sql;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(CommandContext ctx) {
        if (ctx.isEnabled() || ctx.isAlreadySkippedPrefix()) {
            ctx.addSql(prefix);
        } else if (isBeginChildContextAndValidCoondition(ctx, sql)) {
            // To skip prefix should be done only once
            // so it marks that a prefix already skipped.
            ctx.setAlreadySkippedPrefix(true);
        }
        ctx.addSql(sql);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getPrefix() {
        return prefix;
    }

    public String getSql() {
        return sql;
    }
}
