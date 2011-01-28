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
import org.seasar.robot.dbflute.twowaysql.exception.ForCommentParameterNullElementException;

/**
 * @author jflute
 */
public abstract class ScopeNode extends AbstractNode {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ScopeNode() {
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    /**
     * @param ctx The context of command. (NotNull)
     * @param loopInfo The information of loop which have not-null current parameter. (NotNull)
     */
    protected void processAcceptingChildren(CommandContext ctx, LoopInfo loopInfo) {
        final int childSize = getChildSize();
        for (int i = 0; i < childSize; i++) {
            final Node child = getChild(i);
            if (loopInfo != null) { // in loop
                if (child instanceof LoopAcceptable) { // accepting loop
                    handleLoopElementNullParameter(child, loopInfo);
                    ((LoopAcceptable) child).accept(ctx, loopInfo);
                } else {
                    child.accept(ctx);
                }
            } else {
                child.accept(ctx);
            }
        }
    }

    protected void handleLoopElementNullParameter(Node child, LoopInfo loopInfo) {
        if (child instanceof BindVariableNode && ((BindVariableNode) child).isBlockNullParameter()) {
            final Object parameter = loopInfo.getCurrentParameter();
            if (parameter == null) {
                final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
                br.addNotice("The parameter in list for bind variable was null.");
                br.addItem("Advice");
                br.addElement("Bind variable for select does not allow null value.");
                br.addElement("Confirm your target parameter in the list.");
                br.addItem("Parameter List");
                br.addElement(loopInfo.getParameterList());
                br.addItem("Current Index");
                br.addElement(loopInfo.getLoopIndex());
                br.addItem("FOR Comment Expression");
                br.addElement(loopInfo.getExpression());
                br.addItem("Specified SQL");
                br.addElement(loopInfo.getSpecifiedSql());
                String msg = br.buildExceptionMessage();
                throw new ForCommentParameterNullElementException(msg);
            }
        }
    }
}