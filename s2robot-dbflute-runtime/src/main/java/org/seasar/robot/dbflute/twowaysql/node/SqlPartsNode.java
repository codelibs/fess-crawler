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
public class SqlPartsNode extends AbstractNode {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String _sqlParts;
    private boolean _independent;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    private SqlPartsNode(String sqlParts) {
        this._sqlParts = sqlParts;
    }

    // -----------------------------------------------------
    //                                               Factory
    //                                               -------
    public static SqlPartsNode createSqlPartsNode(String sqlParts) {
        return new SqlPartsNode(sqlParts);
    }

    public static SqlPartsNode createSqlPartsNodeAsIndependent(String sqlParts) {
        return new SqlPartsNode(sqlParts).asIndependent(); // means it does not mark already-skipped 
    }

    private SqlPartsNode asIndependent() {
        _independent = true;
        return this;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(CommandContext ctx) {
        ctx.addSql(_sqlParts);
        if (isMarkAlreadySkipped(ctx)) {
            // It does not skipped actually but it has not already needed to skip.
            ctx.setAlreadySkippedConnector(true);
        }
    }

    protected boolean isMarkAlreadySkipped(CommandContext ctx) {
        return !_independent && isBeginChildAndValidSql(ctx, _sqlParts);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{" + _sqlParts + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getSqlParts() {
        return _sqlParts;
    }
}
