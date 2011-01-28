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

import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.twowaysql.exception.LoopVariableCommentOutOfForCommentException;
import org.seasar.robot.dbflute.twowaysql.node.ForNode.LoopVariableType;
import org.seasar.robot.dbflute.util.DfTypeUtil;
import org.seasar.robot.dbflute.util.Srl;
import org.seasar.robot.dbflute.util.Srl.ScopeInfo;

/**
 * @author jflute
 */
public abstract class LoopAbstractNode extends ScopeNode implements LoopAcceptable {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _expression;
    protected final String _replacement;
    protected final String _specifiedSql;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public LoopAbstractNode(String expression, String specifiedSql) {
        this._expression = expression;
        final ScopeInfo scope = Srl.extractScopeWide(_expression, "'", "'");
        _replacement = scope != null ? scope.getContent() : null;
        this._specifiedSql = specifiedSql;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(CommandContext ctx) {
        final LoopVariableType type = getLoopVariableType();
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The " + type.name() + " comment was out of FOR comment!");
        br.addItem("Advice");
        br.addElement("A " + type.name() + " comment should be in FOR comment scope.");
        br.addElement("For example:");
        br.addElement(" (x):");
        br.addElement("   /*" + type.name() + "*/.../*END*/");
        br.addElement("   /*FOR*/.../*END*/");
        br.addElement(" (o):");
        br.addElement("   /*FOR*/");
        br.addElement("   /*" + type.name() + "*/.../*END*/");
        br.addElement("   /*END*/");
        br.addItem(type.name() + " Comment Expression");
        br.addElement(_expression);
        br.addItem("Specified SQL");
        br.addElement(_specifiedSql);
        final String msg = br.buildExceptionMessage();
        throw new LoopVariableCommentOutOfForCommentException(msg);
    }

    protected abstract LoopVariableType getLoopVariableType();

    public void accept(CommandContext ctx, LoopInfo loopInfo) {
        final int loopSize = loopInfo.getLoopSize();
        final int loopIndex = loopInfo.getLoopIndex();
        if (!isValid(loopSize, loopIndex)) {
            return;
        }
        acceptFrontPrefix(ctx);
        processAcceptingChildren(ctx, loopInfo);
    }

    protected abstract boolean isValid(int loopSize, int loopIndex);

    protected void acceptFrontPrefix(CommandContext ctx) {
        if (Srl.is_NotNull_and_NotTrimmedEmpty(_replacement)) {
            ctx.addSql(_replacement);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{" + _expression + "}";
    }
}