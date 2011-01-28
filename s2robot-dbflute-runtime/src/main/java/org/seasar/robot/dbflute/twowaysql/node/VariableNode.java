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

import java.util.List;

import org.seasar.robot.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.twowaysql.node.ValueAndTypeSetupper.CommentType;
import org.seasar.robot.dbflute.util.Srl;

/**
 * @author jflute
 */
public abstract class VariableNode extends AbstractNode implements LoopAcceptable {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _expression;
    protected final String _testValue;
    protected final String _option;
    protected final List<String> _nameList;
    protected final String _specifiedSql;
    protected final boolean _blockNullParameter;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public VariableNode(String expression, String testValue, String specifiedSql, boolean blockNullParameter) {
        if (expression.contains(":")) {
            this._expression = Srl.substringFirstFront(expression, ":").trim();
            this._option = Srl.substringFirstRear(expression, ":").trim();
        } else {
            this._expression = expression;
            this._option = null;
        }
        this._testValue = testValue;
        this._nameList = Srl.splitList(_expression, ".");
        this._specifiedSql = specifiedSql;
        this._blockNullParameter = blockNullParameter;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(CommandContext ctx) {
        doAccept(ctx, null);
    }

    public void accept(CommandContext ctx, LoopInfo loopInfo) { // for FOR comment
        final String firstName = _nameList.get(0);
        if (firstName.equals(ForNode.CURRENT_VARIABLE)) { // use loop element
            final Object parameter = loopInfo.getCurrentParameter();
            final Class<?> parameterType = loopInfo.getCurrentParameterType();
            doAccept(ctx, parameter, parameterType, loopInfo, true);
        } else { // normal
            doAccept(ctx, loopInfo);
        }
    }

    protected void doAccept(CommandContext ctx, LoopInfo loopInfo) {
        final String firstName = _nameList.get(0);
        assertFirstNameAsNormal(ctx, firstName);
        final Object firstValue = ctx.getArg(firstName);
        final Class<?> firstType = ctx.getArgType(firstName);
        doAccept(ctx, firstValue, firstType, loopInfo, false);
    }

    protected void doAccept(CommandContext ctx, Object firstValue, Class<?> firstType, LoopInfo loopInfo,
            boolean inheritLoop) {
        assertInLoopOption(loopInfo);
        final ValueAndType valueAndType = new ValueAndType();
        valueAndType.setFirstValue(firstValue);
        valueAndType.setFirstType(firstType);
        setupValueAndType(valueAndType);
        if (isAcceptableLike()) {
            final LikeSearchOption inLoopLikeSearchOption = getInLoopLikeSearchOption();
            if (inLoopLikeSearchOption != null) { // forced option
                valueAndType.setLikeSearchOption(inLoopLikeSearchOption);
            } else {
                if (inheritLoop) {
                    valueAndType.inheritLikeSearchOptionIfNeeds(loopInfo);
                }
            }
            valueAndType.filterValueByOptionIfNeeds();
        }
        if (_blockNullParameter && valueAndType.getTargetValue() == null) {
            throwBindOrEmbeddedCommentParameterNullValueException(valueAndType);
        }
        doProcess(ctx, valueAndType);
    }

    protected abstract void doProcess(CommandContext ctx, ValueAndType valueAndType);

    protected void assertFirstNameAsNormal(CommandContext ctx, String firstName) {
        if (NodeUtil.isCurrentVariableOutOfScope(firstName, false)) {
            throwLoopCurrentVariableOutOfForCommentException();
        }
        if (NodeUtil.isWrongParameterBeanName(firstName, ctx)) {
            throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException();
        }
    }

    protected void throwLoopCurrentVariableOutOfForCommentException() {
        NodeUtil.throwLoopCurrentVariableOutOfForCommentException(_expression, _specifiedSql);
    }

    protected void setupValueAndType(ValueAndType valueAndType) {
        final CommentType type = getCommentType();
        final ValueAndTypeSetupper setuper = new ValueAndTypeSetupper(_nameList, _expression, _specifiedSql, type);
        setuper.setupValueAndType(valueAndType);
    }

    protected abstract CommentType getCommentType();

    // ===================================================================================
    //                                                                   LikeSearch Helper
    //                                                                   =================
    protected void assertInLoopOption(LoopInfo loopInfo) {
        if (loopInfo == null && Srl.is_NotNull_and_NotTrimmedEmpty(_option)) {
            throwInLoopOptionOutOfLoopException();
        }
    }

    protected boolean isAcceptableLike() { // basically true
        if (Srl.is_Null_or_TrimmedEmpty(_option)) {
            return true;
        }
        final List<String> optionList = Srl.splitListTrimmed(_option, "|");
        for (String option : optionList) {
            if (option.equals("notLike")) {
                return false;
            }
        }
        return true;
    }

    protected LikeSearchOption getInLoopLikeSearchOption() {
        if (Srl.is_Null_or_TrimmedEmpty(_option)) {
            return null;
        }
        final List<String> optionList = Srl.splitListTrimmed(_option, "|");
        for (String option : optionList) {
            if (option.equals("likePrefix")) {
                return new LikeSearchOption().likePrefix();
            } else if (option.equals("likeContain")) {
                return new LikeSearchOption().likeContain();
            } else if (option.equals("likeSuffix")) {
                return new LikeSearchOption().likeSuffix();
            }
        }
        return null;
    }

    protected void setupRearOption(CommandContext ctx, ValueAndType valueAndType) {
        final String rearOption = valueAndType.buildRearOptionOnSql();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(rearOption)) {
            ctx.addSql(rearOption);
        }
    }

    // ===================================================================================
    //                                                                      InScope Helper
    //                                                                      ==============
    protected boolean isInScope() {
        if (_testValue == null) {
            return false;
        }
        return _testValue.startsWith("(") && _testValue.endsWith(")");
    }

    // ===================================================================================
    //                                                                           Exception
    //                                                                           =========
    protected void throwBindOrEmbeddedCommentParameterNullValueException(ValueAndType valueAndType) {
        final Class<?> targetType = valueAndType.getTargetType();
        NodeUtil
                .throwBindOrEmbeddedCommentParameterNullValueException(_expression, targetType, _specifiedSql, isBind());
    }

    protected void throwBindOrEmbeddedCommentInScopeNotListException(ValueAndType valueAndType) {
        final Class<?> targetType = valueAndType.getTargetType();
        NodeUtil.throwBindOrEmbeddedCommentInScopeNotListException(_expression, targetType, _specifiedSql, isBind());
    }

    protected void throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException() {
        NodeUtil.throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException(_expression, _specifiedSql,
                isBind());
    }

    protected void throwBindOrEmbeddedCommentParameterEmptyListException() {
        NodeUtil.throwBindOrEmbeddedCommentParameterEmptyListException(_expression, _specifiedSql, isBind());
    }

    protected void throwBindOrEmbeddedCommentParameterNullOnlyListException() {
        NodeUtil.throwBindOrEmbeddedCommentParameterNullOnlyListException(_expression, _specifiedSql, isBind());
    }

    protected void throwInLoopOptionOutOfLoopException() {
        NodeUtil.throwInLoopOptionOutOfLoopException(_expression, _specifiedSql, _option);
    }

    protected boolean isBind() {
        return getCommentType().equals(CommentType.BIND);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isBlockNullParameter() {
        return _blockNullParameter;
    }
}
