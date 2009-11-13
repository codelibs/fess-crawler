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

import org.seasar.robot.dbflute.exception.IfCommentWrongExpressionException;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public class IfNode extends ContainerNode {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _expression;
    protected ElseNode _elseNode;
    protected String _specifiedSql;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public IfNode(String expression, String specifiedSql) {
        this._expression = expression;
        this._specifiedSql = specifiedSql;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(CommandContext ctx) {
        doAcceptByEvaluator(ctx);
    }

    protected void doAcceptByEvaluator(CommandContext ctx) {
        final IfCommentEvaluator evaluator = createIfCommentEvaluator(ctx, _expression);
        boolean result = false;
        try {
            result = evaluator.evaluate();
        } catch (IfCommentWrongExpressionException e) {
            final String replaced = replace(_expression, "pmb.", "pmb.parameterMap.");
            final IfCommentEvaluator another = createIfCommentEvaluator(ctx, replaced);
            try {
                result = another.evaluate();
            } catch (IfCommentWrongExpressionException ignored) {
                throw e;
            }
        }
        if (result) {
            super.accept(ctx);
            ctx.setEnabled(true);
        } else if (_elseNode != null) {
            _elseNode.accept(ctx);
            ctx.setEnabled(true);
        }
    }

    protected IfCommentEvaluator createIfCommentEvaluator(final CommandContext ctx, String expression) {
        return new IfCommentEvaluator(new ParameterFinder() {
            public Object find(String name) {
                return ctx.getArg(name);
            }
        }, expression, _specifiedSql);
    }

    protected String replace(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getExpression() {
        return _expression;
    }

    public ElseNode getElseNode() {
        return _elseNode;
    }

    public void setElseNode(ElseNode elseNode) {
        this._elseNode = elseNode;
    }
}
