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

import java.lang.reflect.Array;
import java.util.List;

import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.twowaysql.node.NodeUtil.IllegalParameterBeanHandler;
import org.seasar.robot.dbflute.twowaysql.pmbean.ParameterBean;
import org.seasar.robot.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public class EmbeddedValueNode extends AbstractNode {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _expression;
    protected String _testValue;
    protected List<String> _nameList;
    protected String _specifiedSql;
    protected boolean _blockNullParameter;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public EmbeddedValueNode(String expression, String testValue, String specifiedSql, boolean blockNullParameter) {
        this._expression = expression;
        this._testValue = testValue;
        this._nameList = DfStringUtil.splitList(expression, ".");
        this._specifiedSql = specifiedSql;
        this._blockNullParameter = blockNullParameter;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(CommandContext ctx) {
        final String firstName = _nameList.get(0);
        assertFirstName(ctx, firstName);
        final Object value = ctx.getArg(firstName);
        final Class<?> clazz = ctx.getArgType(firstName);
        final ValueAndType valueAndType = new ValueAndType();
        valueAndType.setTargetValue(value);
        valueAndType.setTargetType(clazz);
        setupValueAndType(valueAndType);

        if (_blockNullParameter && valueAndType.getTargetValue() == null) {
            throwBindOrEmbeddedParameterNullValueException(valueAndType);
        }
        if (!isInScope()) {
            // Main Root
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            // [UnderReview]: Should I make an original exception instead of this exception?
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            if (valueAndType.getTargetValue() != null && valueAndType.getTargetValue().toString().indexOf("?") > -1) {
                String msg = "The value of expression for embedded comment should not contain a question mark '?':";
                msg = msg + " value=" + valueAndType.getTargetValue() + " expression=" + _expression;
                throw new IllegalStateException(msg);
            }
            ctx.addSql(valueAndType.getTargetValue().toString());
        } else {
            if (List.class.isAssignableFrom(valueAndType.getTargetType())) {
                embedArray(ctx, ((List<?>) valueAndType.getTargetValue()).toArray());
            } else if (valueAndType.getTargetType().isArray()) {
                embedArray(ctx, valueAndType.getTargetValue());
            } else {
                // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                // [UnderReview]: Should I make an original exception instead of this exception?
                // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                if (valueAndType.getTargetValue() != null && valueAndType.getTargetValue().toString().indexOf("?") > -1) {
                    String msg = "The value of expression for embedded comment should not contain a question mark '?':";
                    msg = msg + " value=" + valueAndType.getTargetValue() + " expression=" + _expression;
                    throw new IllegalStateException(msg);
                }
                ctx.addSql(valueAndType.getTargetValue().toString());
            }
        }
        if (valueAndType.isValidRearOption()) {
            ctx.addSql(valueAndType.buildRearOptionOnSql());
        }
    }

    protected void assertFirstName(final CommandContext ctx, String firstName) {
        NodeUtil.assertParameterBeanName(firstName, new ParameterFinder() {
            public Object find(String name) {
                return ctx.getArg(name);
            }
        }, new IllegalParameterBeanHandler() {
            public void handle(ParameterBean pmb) {
                throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException(pmb);
            }
        });
    }

    protected void setupValueAndType(ValueAndType valueAndType) {
        final ValueAndTypeSetupper setupper = new ValueAndTypeSetupper(_expression, _nameList, _specifiedSql, false);
        setupper.setupValueAndType(valueAndType);
    }

    protected void throwBindOrEmbeddedParameterNullValueException(ValueAndType valueAndType) {
        NodeUtil.throwBindOrEmbeddedCommentParameterNullValueException(_expression, valueAndType.getTargetType(),
                _specifiedSql, false);
    }

    protected boolean isInScope() {
        return _testValue != null && _testValue.startsWith("(") && _testValue.endsWith(")");
    }

    protected void embedArray(CommandContext ctx, Object array) {
        if (array == null) {
            return;
        }
        final int length = Array.getLength(array);
        if (length == 0) {
            throwBindOrEmbeddedCommentParameterEmptyListException();
        }
        String quote = null;
        for (int i = 0; i < length; ++i) {
            final Object currentElement = Array.get(array, i);
            if (currentElement != null) {
                quote = !(currentElement instanceof Number) ? "'" : "";
                break;
            }
        }
        if (quote == null) {
            throwBindOrEmbeddedCommentParameterNullOnlyListException();
        }
        boolean existsValidElements = false;
        ctx.addSql("(");
        for (int i = 0; i < length; ++i) {
            final Object currentElement = Array.get(array, i);
            if (currentElement != null) {
                if (!existsValidElements) {
                    ctx.addSql(quote + currentElement + quote);
                    existsValidElements = true;
                } else {
                    ctx.addSql(", " + quote + currentElement + quote);
                }
            }
        }
        ctx.addSql(")");
    }

    protected void throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException(ParameterBean pmb) {
        NodeUtil.throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException(_expression, _specifiedSql,
                false, pmb);
    }

    protected void throwBindOrEmbeddedCommentParameterEmptyListException() {
        NodeUtil.throwBindOrEmbeddedCommentParameterEmptyListException(_expression, _specifiedSql, false);
    }

    protected void throwBindOrEmbeddedCommentParameterNullOnlyListException() {
        NodeUtil.throwBindOrEmbeddedCommentParameterNullOnlyListException(_expression, _specifiedSql, false);
    }
}
