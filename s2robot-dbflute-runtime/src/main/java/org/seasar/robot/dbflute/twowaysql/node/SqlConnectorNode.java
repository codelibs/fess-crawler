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
package org.seasar.robot.dbflute.twowaysql.node;

import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public class SqlConnectorNode extends AbstractNode {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String _connector;
    private String _sqlParts;
    private boolean _independent;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    private SqlConnectorNode(String connector, String sqlParts) {
        this._connector = connector;
        this._sqlParts = sqlParts;
    }

    // -----------------------------------------------------
    //                                               Factory
    //                                               -------
    public static SqlConnectorNode createSqlConnectorNode(String connector, String sqlParts) {
        return new SqlConnectorNode(connector, sqlParts);
    }

    public static SqlConnectorNode createSqlConnectorNodeAsIndependent(String connector, String sqlParts) {
        return new SqlConnectorNode(connector, sqlParts).asIndependent(); // means it does not mark already-skipped
    }

    private SqlConnectorNode asIndependent() {
        _independent = true;
        return this;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(CommandContext ctx) {
        if (ctx.isEnabled() || ctx.isAlreadySkippedConnector()) {
            ctx.addSql(_connector);
        } else if (isMarkAlreadySkipped(ctx)) {
            // To skip prefix should be done only once
            // so it marks that a prefix already skipped.
            ctx.setAlreadySkippedConnector(true);
        }
        ctx.addSql(_sqlParts);
    }

    protected boolean isMarkAlreadySkipped(CommandContext ctx) {
        return !_independent && isBeginChildAndValidSql(ctx, _sqlParts);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{" + _connector + ", " + _sqlParts + ", " + _independent + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getConnector() {
        return _connector;
    }

    public String getSqlParts() {
        return _sqlParts;
    }
}
