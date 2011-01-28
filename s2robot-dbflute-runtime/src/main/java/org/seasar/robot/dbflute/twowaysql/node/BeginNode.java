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
import org.seasar.robot.dbflute.twowaysql.context.impl.CommandContextImpl;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public class BeginNode extends ScopeNode implements LoopAcceptable, SqlConnectorAdjustable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String MARK = "BEGIN";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final boolean _nested;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BeginNode(boolean nested) {
        _nested = nested;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(CommandContext ctx) {
        doAccept(ctx, null);
    }

    public void accept(CommandContext ctx, LoopInfo loopInfo) {
        doAccept(ctx, loopInfo);
    }

    public void doAccept(CommandContext ctx, LoopInfo loopInfo) {
        final CommandContext childCtx = CommandContextImpl.createCommandContextImplAsBeginChild(ctx);
        processAcceptingChildren(childCtx, loopInfo);
        if (childCtx.isEnabled()) {
            ctx.addSql(childCtx.getSql(), childCtx.getBindVariables(), childCtx.getBindVariableTypes());
            if (ctx.isBeginChild()) { // means nested begin-node
                // to tell parent begin-node whether
                // nested begin-node is enabled or not
                ctx.setEnabled(true);
            }
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{" + _nested + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isNested() {
        return _nested;
    }
}