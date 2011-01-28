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

import java.lang.reflect.Array;
import java.util.List;

import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.robot.dbflute.twowaysql.exception.EmbeddedVariableCommentContainsBindSymbolException;
import org.seasar.robot.dbflute.twowaysql.node.ValueAndTypeSetupper.CommentType;
import org.seasar.robot.dbflute.util.Srl;
import org.seasar.robot.dbflute.util.Srl.ScopeInfo;

/**
 * @author jflute
 */
public class EmbeddedVariableNode extends VariableNode {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String PREFIX = "$";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public EmbeddedVariableNode(String expression, String testValue, String specifiedSql, boolean blockNullParameter) {
        super(expression, testValue, specifiedSql, blockNullParameter);
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    @Override
    protected void doProcess(CommandContext ctx, ValueAndType valueAndType) {
        final Object finalValue = valueAndType.getTargetValue();
        final Class<?> finalType = valueAndType.getTargetType();
        if (isInScope()) {
            if (finalValue == null) { // in-scope does not allow null value
                throwBindOrEmbeddedCommentParameterNullValueException(valueAndType);
            }
            if (List.class.isAssignableFrom(finalType)) {
                embedArray(ctx, ((List<?>) finalValue).toArray());
            } else if (finalType.isArray()) {
                embedArray(ctx, finalValue);
            } else {
                throwBindOrEmbeddedCommentInScopeNotListException(valueAndType);
            }
        } else {
            if (finalValue == null) {
                ctx.addSql("null");
                return;
            }
            if (!(finalValue instanceof String)) {
                final String embeddedValue = finalValue.toString();
                if (isQuotedScalar()) { // basically for condition value
                    ctx.addSql(quote(embeddedValue));
                } else { // basically for cannot-bound condition (for example, paging)
                    ctx.addSql(embeddedValue);
                }
                return;
            }
            // string type here
            final String embeddedString = (String) finalValue;
            assertNotContainBindSymbol(embeddedString);
            if (isQuotedScalar()) { // basically for condition value
                ctx.addSql(quote(embeddedString));
                if (isAcceptableLike()) {
                    setupRearOption(ctx, valueAndType);
                }
                return;
            }
            final Object firstValue = valueAndType.getFirstValue();
            final Class<?> firstType = valueAndType.getFirstType();
            if (processDynamicBinding(ctx, firstValue, firstType, embeddedString)) {
                return;
            }
            ctx.addSql(embeddedString);
        }
    }

    protected void embedArray(CommandContext ctx, Object array) {
        if (array == null) {
            return;
        }
        final int length = Array.getLength(array);
        if (length == 0) {
            throwBindOrEmbeddedCommentParameterEmptyListException();
        }
        final boolean quotedInScope = isQuotedInScope();
        ctx.addSql("(");
        int validCount = 0;
        for (int i = 0; i < length; ++i) {
            final Object currentElement = Array.get(array, i);
            if (currentElement != null) {
                if (validCount > 0) {
                    ctx.addSql(", ");
                }
                final String currentStr = currentElement.toString();
                assertNotContainBindSymbol(currentStr);
                if (quotedInScope) {
                    ctx.addSql(quote(currentStr));
                } else {
                    ctx.addSql(currentStr);
                }
                ++validCount;
            }
        }
        if (validCount == 0) {
            throwBindOrEmbeddedCommentParameterNullOnlyListException();
        }
        ctx.addSql(")");
    }

    protected void assertNotContainBindSymbol(String value) {
        if (containsBindSymbol(value)) {
            final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
            br.addNotice("The value of embedded comment contained bind symbols.");
            br.addItem("Advice");
            br.addElement("The value of embedded comment should not contain bind symbols.");
            br.addElement("For example, a question mark '?'.");
            br.addItem("Comment Expression");
            br.addElement(_expression);
            br.addItem("Embedded Value");
            br.addElement(value);
            final String msg = br.buildExceptionMessage();
            throw new EmbeddedVariableCommentContainsBindSymbolException(msg);
        }
    }

    protected boolean containsBindSymbol(String value) {
        return value.indexOf("?") > -1;
    }

    protected String quote(String value) {
        return "'" + value + "'";
    }

    protected boolean isQuotedScalar() {
        if (_testValue == null) {
            return false;
        }
        return Srl.count(_testValue, "'") > 1 && _testValue.startsWith("'") && _testValue.endsWith("'");
    }

    protected boolean isQuotedInScope() {
        if (!isInScope()) {
            return false;
        }
        return Srl.count(_testValue, "'") > 1;
    }

    protected boolean processDynamicBinding(CommandContext ctx, Object firstValue, Class<?> firstType,
            String embeddedString) {
        final ScopeInfo first = Srl.extractScopeFirst(embeddedString, "/*", "*/");
        if (first == null) {
            return false;
        }
        final SqlAnalyzer analyzer = new SqlAnalyzer(embeddedString, _blockNullParameter);
        final Node rootNode = analyzer.analyze();
        final CommandContextCreator creator = new CommandContextCreator(new String[] { "pmb" },
                new Class<?>[] { firstType });
        final CommandContext rootCtx = creator.createCommandContext(new Object[] { firstValue });
        rootNode.accept(rootCtx);
        final String sql = rootCtx.getSql();
        ctx.addSql(sql, rootCtx.getBindVariables(), rootCtx.getBindVariableTypes());
        return true;
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    @Override
    protected CommentType getCommentType() {
        return CommentType.EMBEDDED;
    }
}
