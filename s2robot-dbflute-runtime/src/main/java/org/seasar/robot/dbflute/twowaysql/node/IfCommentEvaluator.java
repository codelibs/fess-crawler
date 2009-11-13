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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.seasar.robot.dbflute.exception.IfCommentDifferentTypeComparisonException;
import org.seasar.robot.dbflute.exception.IfCommentEmptyExpressionException;
import org.seasar.robot.dbflute.exception.IfCommentIllegalParameterBeanSpecificationException;
import org.seasar.robot.dbflute.exception.IfCommentNotBooleanResultException;
import org.seasar.robot.dbflute.exception.IfCommentNotFoundMethodException;
import org.seasar.robot.dbflute.exception.IfCommentNotFoundPropertyException;
import org.seasar.robot.dbflute.exception.IfCommentNullPointerException;
import org.seasar.robot.dbflute.exception.IfCommentUnsupportedExpressionException;
import org.seasar.robot.dbflute.exception.IfCommentUnsupportedTypeComparisonException;
import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanMethodNotFoundException;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanPropertyNotFoundException;
import org.seasar.robot.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.robot.dbflute.twowaysql.node.NodeUtil.IllegalParameterBeanHandler;
import org.seasar.robot.dbflute.twowaysql.pmbean.ParameterBean;
import org.seasar.robot.dbflute.util.DfReflectionUtil;
import org.seasar.robot.dbflute.util.DfStringUtil;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.DfTypeUtil;
import org.seasar.robot.dbflute.util.DfTypeUtil.ToTimestampFlexiblyParseException;

/**
 * @author jflute
 */
public class IfCommentEvaluator {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final String AND = " && ";
    private static final String OR = " || ";
    private static final String EQUAL = " == ";
    private static final String NOT_EQUAL = " != ";
    private static final String GREATER_THAN = " > ";
    private static final String LESS_THAN = " < ";
    private static final String GREATER_EQUAL = " >= ";
    private static final String LESS_EQUAL = " <= ";
    private static final String BOOLEAN_NOT = "!";
    private static final String METHOD_SUFFIX = "()";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private ParameterFinder _finder;
    private String _expression;
    private String _specifiedSql;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public IfCommentEvaluator(ParameterFinder finder, String expression, String specifiedSql) {
        this._finder = finder;
        this._expression = expression;
        this._specifiedSql = specifiedSql;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public boolean evaluate() {
        assertExpression();
        _expression = _expression.trim();
        if (_expression.contains(AND)) {
            final List<String> splitList = splitList(_expression, AND);
            for (String booleanClause : splitList) {
                final boolean result = evaluateBooleanClause(booleanClause);
                if (!result) {
                    return false;
                }
            }
            return true;
        } else if (_expression.contains(OR)) {
            final List<String> splitList = splitList(_expression, OR);
            for (String booleanClause : splitList) {
                final boolean result = evaluateBooleanClause(booleanClause);
                if (result) {
                    return true;
                }
            }
            return false;
        } else {
            return evaluateBooleanClause(_expression);
        }
    }

    public void assertExpression() {
        if (_expression == null || _expression.trim().length() == 0) {
            throwIfCommentEmptyExpressionException();
        }
        String filtered = DfStringUtil.replace(_expression, "()", "");
        if (filtered.contains("(") || filtered.contains(")")) {
            throwIfCommentUnsupportedExpressionException();
        }
        if (_expression.contains(AND) && _expression.contains(OR)) {
            throwIfCommentUnsupportedExpressionException();
        }
        if (_expression.contains(" = ") || _expression.contains(" <> ")) {
            throwIfCommentUnsupportedExpressionException();
        }
        if (_expression.contains("\"")) {
            throwIfCommentUnsupportedExpressionException();
        }
    }

    protected boolean evaluateBooleanClause(final String booleanClause) {
        if (booleanClause.contains(EQUAL)) {
            return evaluateCompareClause(booleanClause, EQUAL, new OperandEvaluator() {
                public boolean evaluate(Object leftResult, Object rightResult) {
                    if (leftResult instanceof Number && rightResult instanceof Number) {
                        leftResult = new BigDecimal(leftResult.toString());
                        rightResult = new BigDecimal(rightResult.toString());
                    }
                    assertCompareType(leftResult, rightResult, booleanClause);
                    return leftResult != null ? leftResult.equals(rightResult) : rightResult == null;
                }
            });
        } else if (booleanClause.contains(NOT_EQUAL)) {
            return evaluateCompareClause(booleanClause, NOT_EQUAL, new OperandEvaluator() {
                public boolean evaluate(Object leftResult, Object rightResult) {
                    if (leftResult instanceof Number && rightResult instanceof Number) {
                        leftResult = new BigDecimal(leftResult.toString());
                        rightResult = new BigDecimal(rightResult.toString());
                    }
                    assertCompareType(leftResult, rightResult, booleanClause);
                    return leftResult != null ? !leftResult.equals(rightResult) : rightResult != null;
                }
            });
        } else if (booleanClause.contains(GREATER_THAN)) {
            return evaluateCompareClause(booleanClause, GREATER_THAN, new OperandEvaluator() {
                public boolean evaluate(Object leftResult, Object rightResult) {
                    if (leftResult == null) {
                        return false;
                    }
                    if (rightResult == null) {
                        return true;
                    }
                    return compareLeftRight(leftResult, rightResult, new ComparaDeterminater() {
                        public boolean compare(int compareResult) {
                            return compareResult > 0;
                        }
                    }, booleanClause);
                }
            });
        } else if (booleanClause.contains(LESS_THAN)) {
            return evaluateCompareClause(booleanClause, LESS_THAN, new OperandEvaluator() {
                public boolean evaluate(Object leftResult, Object rightResult) {
                    if (leftResult == null) {
                        return true;
                    }
                    if (rightResult == null) {
                        return false;
                    }
                    return compareLeftRight(leftResult, rightResult, new ComparaDeterminater() {
                        public boolean compare(int compareResult) {
                            return compareResult < 0;
                        }
                    }, booleanClause);
                }
            });
        } else if (booleanClause.contains(GREATER_EQUAL)) {
            return evaluateCompareClause(booleanClause, GREATER_EQUAL, new OperandEvaluator() {
                public boolean evaluate(Object leftResult, Object rightResult) {
                    if (leftResult == null) {
                        return rightResult == null;
                    }
                    if (rightResult == null) {
                        return true;
                    }
                    return compareLeftRight(leftResult, rightResult, new ComparaDeterminater() {
                        public boolean compare(int compareResult) {
                            return compareResult >= 0;
                        }
                    }, booleanClause);
                }
            });
        } else if (booleanClause.contains(LESS_EQUAL)) {
            return evaluateCompareClause(booleanClause, LESS_EQUAL, new OperandEvaluator() {
                public boolean evaluate(Object leftResult, Object rightResult) {
                    if (leftResult == null) {
                        return true;
                    }
                    if (rightResult == null) {
                        return false;
                    }
                    return compareLeftRight(leftResult, rightResult, new ComparaDeterminater() {
                        public boolean compare(int compareResult) {
                            return compareResult <= 0;
                        }
                    }, booleanClause);
                }
            });
        } else {
            return evaluateStandAloneValue(booleanClause);
        }
    }

    protected boolean compareLeftRight(Object leftResult, Object rightResult, ComparaDeterminater determinater,
            String booleanClause) {
        assertCompareType(leftResult, rightResult, booleanClause);
        if (leftResult instanceof Date) {
            final Date leftDate = (Date) leftResult;
            final Date rightDate = (Date) rightResult;
            return determinater.compare(leftDate.compareTo(rightDate));
        } else if (leftResult instanceof Number) {
            final Number leftNumber = (Number) leftResult;
            final BigDecimal leftDecimal = new BigDecimal(leftNumber.toString());
            final Number rightNumber = (Number) rightResult;
            final BigDecimal rightDecimal = new BigDecimal(rightNumber.toString());
            return determinater.compare(leftDecimal.compareTo(rightDecimal));
        } else {
            throwIfCommentUnsupportedTypeComparisonException(leftResult, rightResult, booleanClause);
            return false; // unreachable
        }
    }

    protected void assertCompareType(Object leftResult, Object rightResult, String booleanClause) {
        if (leftResult != null && rightResult != null && leftResult instanceof Date) {
            if (!(rightResult instanceof Date)) {
                throwIfCommentDifferentTypeComparisonException(leftResult, rightResult, booleanClause);
            }
        } else if (leftResult != null && rightResult != null && leftResult instanceof Number) {
            if (!(rightResult instanceof Number)) {
                throwIfCommentDifferentTypeComparisonException(leftResult, rightResult, booleanClause);
            }
        }
    }

    protected static interface ComparaDeterminater {
        boolean compare(int compareResult);
    }

    protected boolean evaluateCompareClause(String booleanClause, String operand, OperandEvaluator evaluator) {
        final String left = booleanClause.substring(0, booleanClause.indexOf(operand)).trim();
        final String right = booleanClause.substring(booleanClause.indexOf(operand) + operand.length()).trim();
        final Object leftResult = evaluateComparePiece(left, null);
        final Object rightResult = evaluateComparePiece(right, null);
        return evaluator.evaluate(leftResult, rightResult);
    }

    protected static interface OperandEvaluator {
        boolean evaluate(Object leftResult, Object rightRight);
    }

    protected Object evaluateComparePiece(String piece, Object leftResult) {
        piece = piece.trim();
        if (!startsWithParameterBean(piece)) {
            if ("null".equalsIgnoreCase(piece)) {
                return null;
            }
            if ("true".equalsIgnoreCase(piece)) {
                return true;
            }
            if ("false".equalsIgnoreCase(piece)) {
                return false;
            }
            final String quote = "'";
            final int qlen = "'".length();
            if (piece.startsWith(quote) && piece.endsWith(quote)) {
                return piece.substring(qlen, piece.length() - qlen);
            }
            final String dateMark = "date ";
            if (piece.toLowerCase().startsWith(dateMark)) {
                final String rearValue = piece.substring(dateMark.length()).trim();
                if (rearValue.startsWith(quote) && rearValue.endsWith(quote)) {
                    final String literal = rearValue.substring(qlen, rearValue.length() - qlen).trim();
                    try {
                        return DfTypeUtil.toTimestampFlexibly(literal);
                    } catch (ToTimestampFlexiblyParseException ignored) {
                    }
                }
            }
            try {
                return DfTypeUtil.toBigDecimal(piece);
            } catch (NumberFormatException ignored) {
            }
        }
        final List<String> propertyList = new ArrayList<String>();
        String preProperty = setupPropertyList(piece, propertyList);
        Object baseObject = _finder.find(preProperty);
        for (String property : propertyList) {
            baseObject = processOneProperty(baseObject, preProperty, property);
            preProperty = property;
        }
        return baseObject;
    }

    protected boolean evaluateStandAloneValue(String piece) {
        piece = piece.trim();
        boolean not = false;
        if (piece.startsWith(BOOLEAN_NOT)) {
            not = true;
            piece = piece.substring(BOOLEAN_NOT.length());
        }
        if (!startsWithParameterBean(piece)) {
            if ("true".equalsIgnoreCase(piece)) {
                return not ? false : true;
            }
            if ("false".equalsIgnoreCase(piece)) {
                return not ? true : false;
            }
        }
        final List<String> propertyList = new ArrayList<String>();
        String preProperty = setupPropertyList(piece, propertyList);
        Object baseObject = _finder.find(preProperty);
        for (String property : propertyList) {
            baseObject = processOneProperty(baseObject, preProperty, property);
            preProperty = property;
        }
        if (baseObject == null) {
            throwIfCommentNotBooleanResultException();
        }
        final boolean result = Boolean.valueOf(baseObject.toString());
        return not ? !result : result;
    }

    protected boolean startsWithParameterBean(String piece) {
        return piece.startsWith("pmb");
    }

    protected String setupPropertyList(String piece, List<String> emptyPropertyList) {
        final List<String> splitList = splitList(piece, ".");
        String firstName = null;
        for (int i = 0; i < splitList.size(); i++) {
            if (i == 0) {
                assertFirstName(splitList.get(i));
                continue;
            }
            emptyPropertyList.add(splitList.get(i));
        }
        return firstName;
    }

    protected void assertFirstName(String firstName) {
        NodeUtil.assertParameterBeanName(firstName, _finder, new IllegalParameterBeanHandler() {
            public void handle(ParameterBean pmb) {
                throwIfCommentIllegalParameterBeanSpecificationException();
            }
        });
    }

    protected Object processOneProperty(Object baseObject, String preProperty, String property) {
        if (baseObject == null) {
            throwIfCommentNullPointerException(preProperty);
        }
        final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(baseObject.getClass());
        if (property.endsWith(METHOD_SUFFIX)) {
            final String methodName = property.substring(0, property.length() - METHOD_SUFFIX.length());
            try {
                final Method method = beanDesc.getMethod(methodName);
                return DfReflectionUtil.invoke(method, baseObject, (Object[]) null);
            } catch (DfBeanMethodNotFoundException e) {
                throwIfCommentNotFoundMethodException(baseObject, methodName);
                return null; // unreachable
            }
        } else {
            if (Map.class.isInstance(baseObject)) {
                final Map<?, ?> map = (Map<?, ?>) baseObject;
                return map.get(property);
            } else {
                try {
                    final DfPropertyDesc propertyDesc = beanDesc.getPropertyDesc(property);
                    return propertyDesc.getValue(baseObject);
                } catch (DfBeanPropertyNotFoundException e) {
                    throwIfCommentNotFoundPropertyException(baseObject, property);
                    return null; // unreachable
                }
            }
        }
    }

    // ===================================================================================
    //                                                                           Exception
    //                                                                           =========
    protected void throwIfCommentEmptyExpressionException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The IF comment expression was empty!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your IF comment." + ln();
        msg = msg + "  For example, wrong and correct IF comment is as below:" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    (x) - /*IF */" + ln();
        msg = msg + "    (o) - /*IF pmb.memberId != null*/" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _expression + ln();
        msg = msg + ln();
        msg = msg + "[Specified ParameterBean]" + ln() + getDisplayParameterBean() + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentEmptyExpressionException(msg);
    }

    protected void throwIfCommentUnsupportedExpressionException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The IF comment expression was unsupported!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your unsupported IF comment." + ln();
        msg = msg + "  For example, unsupported examples:" + ln();
        msg = msg + "    (x:andOr) - /*IF (pmb.fooId != null || pmb.barId != null) && pmb.fooName != null*/" + ln();
        msg = msg + "    (x:argsMethod) - /*IF pmb.buildFooId(123)*/" + ln();
        msg = msg + "    (x:stringLiteral) - /*IF pmb.fooName == 'Pixy' || pmb.fooName == \"Pixy\"*/" + ln();
        msg = msg + "    (x:singleEqual) - /*IF pmb.fooId = null*/ --> /*IF pmb.fooId == null*/" + ln();
        msg = msg + "    (x:anotherNot) - /*IF pmb.fooId <> null*/ --> /*IF pmb.fooId != null*/" + ln();
        msg = msg + "    (x:doubleQuotation) - /*IF pmb.fooName == \"Pixy\"*/ --> /*IF pmb.fooName == 'Pixy'*/" + ln();
        msg = msg + "    " + ln();
        msg = msg + "If you want to write a complex condition, write an ExParameterBean property." + ln();
        msg = msg + "And use the property in IF comment." + ln();
        msg = msg + "  For example, ExParameterBean original property:" + ln();
        msg = msg + "    ex) ExParameterBean (your original property)" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    public boolean isOriginalMemberProperty() {" + ln();
        msg = msg + "        return (getMemberId() != null || getBirthdate() != null) && getMemberName() != null);"
                + ln();
        msg = msg + "    }" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + "    " + ln();
        msg = msg + "    ex) IF comment" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    /*IF pmb.originalMemberProperty*/" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _expression + ln();
        msg = msg + ln();
        msg = msg + "[Specified ParameterBean]" + ln() + getDisplayParameterBean() + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentUnsupportedExpressionException(msg);
    }

    public void throwIfCommentIllegalParameterBeanSpecificationException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The IF comment had the illegal parameter-bean specification!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your IF comment." + ln();
        msg = msg + "  For example, wrong and correct IF comment is as below:" + ln();
        msg = msg + "    (x) - /*IF pmb,memberId != null*/" + ln();
        msg = msg + "    (x) - /*IF p mb.memberId != null*/" + ln();
        msg = msg + "    (x) - /*IF pmb:memberId != null*/" + ln();
        msg = msg + "    (x) - /*IF pnb.memberId != null*/" + ln();
        msg = msg + "    (o) - /*IF pmb.memberId != null*/" + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _expression + ln();
        msg = msg + ln();
        msg = msg + "[Specified ParameterBean]" + ln() + getDisplayParameterBean() + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentIllegalParameterBeanSpecificationException(msg);
    }

    protected void throwIfCommentNotFoundMethodException(Object baseObject, String notFoundMethod) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The IF comment method was not found!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your IF comment properties." + ln();
        msg = msg + "  For example, wrong and correct IF comment is as below:" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    (x) - /*IF pmb.getMemborWame() != null*/" + ln();
        msg = msg + "    (o) - /*IF pmb.getMemberName() != null*/" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _expression + ln();
        msg = msg + ln();
        msg = msg + "[Not Found Method]" + ln();
        msg = msg + (baseObject != null ? baseObject.getClass().getSimpleName() + "." : "");
        msg = msg + notFoundMethod + "()" + ln();
        msg = msg + ln();
        msg = msg + "[Specified ParameterBean]" + ln() + getDisplayParameterBean() + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentNotFoundMethodException(msg);
    }

    protected void throwIfCommentNotFoundPropertyException(Object baseObject, String notFoundProperty) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The IF comment property was not found!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your IF comment properties." + ln();
        msg = msg + "  For example, wrong and correct IF comment is as below:" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    (x) - /*IF pmb.memderBame != null*/" + ln();
        msg = msg + "    (o) - /*IF pmb.memberName != null*/" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _expression + ln();
        msg = msg + ln();
        msg = msg + "[Not Found Property]" + ln();
        msg = msg + (baseObject != null ? baseObject.getClass().getSimpleName() + "." : "");
        msg = msg + notFoundProperty + ln();
        msg = msg + ln();
        msg = msg + "[Specified ParameterBean]" + ln() + getDisplayParameterBean() + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentNotFoundPropertyException(msg);
    }

    protected void throwIfCommentNullPointerException(String nullProperty) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The IF comment had the null pointer!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your IF comment and its property values." + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _expression + ln();
        msg = msg + ln();
        msg = msg + "[Null Property]" + ln() + nullProperty + ln();
        msg = msg + ln();
        msg = msg + "[Specified ParameterBean]" + ln() + getDisplayParameterBean() + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentNullPointerException(msg);
    }

    protected void throwIfCommentDifferentTypeComparisonException(Object left, Object right, String booleanClause) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The IF comment had the different type comparison!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your IF comment property types." + ln();
        msg = msg + "If the left type is Number, the right type should be Number." + ln();
        msg = msg + "If the left type is Date, the right type should be Date." + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _expression + ln();
        msg = msg + ln();
        msg = msg + "[Target Boolean Clause]" + ln() + booleanClause + ln();
        msg = msg + ln();
        msg = msg + "[Left]" + ln() + (left != null ? left.getClass() : "null") + ln();
        msg = msg + " --> " + left + ln();
        msg = msg + ln();
        msg = msg + "[Right]" + ln() + (right != null ? right.getClass() : "null") + ln();
        msg = msg + " --> " + right + ln();
        msg = msg + ln();
        msg = msg + "[Specified ParameterBean]" + ln() + getDisplayParameterBean() + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentDifferentTypeComparisonException(msg);
    }

    protected void throwIfCommentUnsupportedTypeComparisonException(Object left, Object right, String booleanClause) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The IF comment had the different type comparison!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your IF comment property types." + ln();
        msg = msg + "For example, String type is unsupported at comparison(>, <, >=, <=)." + ln();
        msg = msg + "Number and Date are only supported." + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _expression + ln();
        msg = msg + ln();
        msg = msg + "[Target Boolean Clause]" + ln() + booleanClause + ln();
        msg = msg + ln();
        msg = msg + "[Left]" + ln() + (left != null ? left.getClass() : "null") + ln();
        msg = msg + " --> " + left + ln();
        msg = msg + ln();
        msg = msg + "[Right]" + ln() + (right != null ? right.getClass() : "null") + ln();
        msg = msg + " --> " + right + ln();
        msg = msg + ln();
        msg = msg + "[Specified ParameterBean]" + ln() + getDisplayParameterBean() + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentUnsupportedTypeComparisonException(msg);
    }

    protected void throwIfCommentNotBooleanResultException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The IF comment was not boolean!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your IF comment property." + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _expression + ln();
        msg = msg + ln();
        msg = msg + "[Specified ParameterBean]" + ln() + getDisplayParameterBean() + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentNotBooleanResultException(msg);
    }

    protected Object getDisplayParameterBean() {
        return _finder.find("pmb");
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    protected List<String> splitList(String str, String delimiter) {
        return DfStringUtil.splitList(str, delimiter);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getExpression() {
        return _expression;
    }
}
