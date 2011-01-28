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
import org.seasar.robot.dbflute.twowaysql.exception.BindVariableCommentIllegalParameterBeanSpecificationException;
import org.seasar.robot.dbflute.twowaysql.exception.BindVariableCommentInScopeNotListException;
import org.seasar.robot.dbflute.twowaysql.exception.BindVariableCommentParameterNullValueException;
import org.seasar.robot.dbflute.twowaysql.exception.EmbeddedVariableCommentIllegalParameterBeanSpecificationException;
import org.seasar.robot.dbflute.twowaysql.exception.EmbeddedVariableCommentInScopeNotListException;
import org.seasar.robot.dbflute.twowaysql.exception.EmbeddedVariableCommentParameterNullValueException;
import org.seasar.robot.dbflute.twowaysql.exception.InLoopOptionOutOfLoopException;
import org.seasar.robot.dbflute.twowaysql.exception.LoopCurrentVariableOutOfForCommentException;
import org.seasar.robot.dbflute.twowaysql.pmbean.ParameterBean;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class NodeUtil {

    public static boolean isCurrentVariableOutOfScope(String firstName, boolean inLoop) {
        return !inLoop && firstName.equals(ForNode.CURRENT_VARIABLE);
    }

    public static boolean isWrongParameterBeanName(String firstName, CommandContext ctx) {
        final Object firstArg = ctx.getArg(firstName);
        return isWrongParameterBeanName(firstName, firstArg);
    }

    public static boolean isWrongParameterBeanName(String firstName, Object firstArg) {
        return firstArg instanceof ParameterBean && !"pmb".equals(firstName);
    }

    public static void throwBindOrEmbeddedCommentParameterNullValueException(String expression, Class<?> targetType,
            String specifiedSql, boolean bind) {
        final String name = (bind ? "bind variable" : "embedded variable");
        final String emmark = (bind ? "" : "$");
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The value of " + name + " was null!");
        br.addItem("Advice");
        br.addElement("Is it within the scope of your assumption?");
        br.addElement("If the answer is YES, please confirm your application logic about the parameter.");
        br.addElement("If the answer is NO, please confirm the logic of parameter comment(especially IF comment).");
        br.addElement("For example:");
        br.addElement("  (x) - XXX_ID = /*" + emmark + "pmb.xxxId*/3");
        br.addElement("  (o) - /*IF pmb.xxxId != null*/XXX_ID = /*" + emmark + "pmb.xxxId*/3/*END*/");
        br.addItem("Comment Expression");
        br.addElement(expression);
        br.addItem("Parameter Type");
        br.addElement(targetType);
        br.addItem("Specified SQL");
        br.addElement(specifiedSql);
        final String msg = br.buildExceptionMessage();
        if (bind) {
            throw new BindVariableCommentParameterNullValueException(msg);
        } else {
            throw new EmbeddedVariableCommentParameterNullValueException(msg);
        }
    }

    public static void throwBindOrEmbeddedCommentInScopeNotListException(String expression, Class<?> targetType,
            String specifiedSql, boolean bind) {
        final String emmark = (bind ? "" : "$");
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The parameter for in-scope condition was not list or array!");
        br.addItem("Advice");
        br.addElement("If a style of a test value is '(...)', parameter should be list or array for in-scope.");
        br.addElement("For example:");
        br.addElement("  (x) - MEMBER_ID in /*" + emmark + "pmb.memberId*/('foo', 'bar')");
        br.addElement("  (o) - MEMBER_ID in /*" + emmark + "pmb.memberIdList*/('foo', 'bar')");
        br.addItem("Comment Expression");
        br.addElement(expression);
        br.addItem("Parameter Type");
        br.addElement(targetType);
        br.addItem("Specified SQL");
        br.addElement(specifiedSql);
        final String msg = br.buildExceptionMessage();
        if (bind) {
            throw new BindVariableCommentInScopeNotListException(msg);
        } else {
            throw new EmbeddedVariableCommentInScopeNotListException(msg);
        }
    }

    public static void throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException(String expression,
            String specifiedSql, boolean bind) {
        final String name = (bind ? "bind variable" : "embedded variable");
        final String emmark = (bind ? "" : "$");
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The " + name + " comment had the illegal parameter-bean specification!");
        br.addItem("Advice");
        br.addElement("At first, is it really " + name + " comment?");
        br.addElement("Have you had a spell miss?");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    /*IE pmb...*/");
        br.addElement("    /*FUOR pmb...*/");
        br.addElement("    /*BIGAN*/");
        br.addElement("  (o):");
        br.addElement("    /*IF pmb...*/");
        br.addElement("    /*FOR pmb...*/");
        br.addElement("    /*BEGIN*/");
        br.addElement("");
        br.addElement("If you want to set " + name + "comment,");
        br.addElement("confirm the spell of parameter-bean expression.");
        br.addElement("(using parameter-bean, it should be named 'pmb')");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    /*" + emmark + "pmb,memberId*/");
        br.addElement("    /*" + emmark + "p mb.memberId*/");
        br.addElement("    /*" + emmark + "pmb:memberId*/");
        br.addElement("    /*" + emmark + "pnb.memberId*/");
        br.addElement("  (o):");
        br.addElement("    /*" + emmark + "pmb.memberId*/");
        br.addItem("Comment Expression");
        br.addElement(expression);
        // *debug to this exception does not need contents of the parameter-bean
        //  (and for security to application data)
        //br.addItem("ParameterBean");
        //br.addElement(pmb);
        br.addItem("Specified SQL");
        br.addElement(specifiedSql);
        final String msg = br.buildExceptionMessage();
        if (bind) {
            throw new BindVariableCommentIllegalParameterBeanSpecificationException(msg);
        } else {
            throw new EmbeddedVariableCommentIllegalParameterBeanSpecificationException(msg);
        }
    }

    public static void throwBindOrEmbeddedCommentParameterEmptyListException(String expression, String specifiedSql,
            boolean bind) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The list of " + (bind ? "bind" : "embedded") + " variable was empty!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your application logic." + ln();
        msg = msg + "For example:" + ln();
        msg = msg + "  (x):" + ln();
        msg = msg + "    List<Integer> xxxIdList = new ArrayList<Integer>();" + ln();
        msg = msg + "    cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);" + ln();
        msg = msg + "  (o):" + ln();
        msg = msg + "    List<Integer> xxxIdList = new ArrayList<Integer>();" + ln();
        msg = msg + "    xxxIdList.add(3);" + ln();
        msg = msg + "    xxxIdList.add(7);" + ln();
        msg = msg + "    cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);" + ln();
        msg = msg + ln();
        msg = msg + "[Comment Expression]" + ln() + expression + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalStateException(msg);
    }

    public static void throwBindOrEmbeddedCommentParameterNullOnlyListException(String expression, String specifiedSql,
            boolean bind) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The list of " + (bind ? "bind" : "embedded") + " variable was null-only list'!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your application logic." + ln();
        msg = msg + "For example:" + ln();
        msg = msg + "  (x):" + ln();
        msg = msg + "    List<Integer> xxxIdList = new ArrayList<Integer>();" + ln();
        msg = msg + "    xxxIdList.add(null);" + ln();
        msg = msg + "    xxxIdList.add(null);" + ln();
        msg = msg + "    cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);" + ln();
        msg = msg + "  (o):" + ln();
        msg = msg + "    List<Integer> xxxIdList = new ArrayList<Integer>();" + ln();
        msg = msg + "    xxxIdList.add(3);" + ln();
        msg = msg + "    xxxIdList.add(7);" + ln();
        msg = msg + "    cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);" + ln();
        msg = msg + ln();
        msg = msg + "[Comment Expression]" + ln() + expression + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalStateException(msg);
    }

    public static void throwInLoopOptionOutOfLoopException(String expression, String specifiedSql, String option) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The in-loop option of variable comment was out of loop.");
        br.addItem("Advice");
        br.addElement("The in-loop options are supported in loop only.");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    /*pmb.memberName:notLike*/");
        br.addElement("    /*FOR ...*/");
        br.addElement("    /*END*/");
        br.addElement("  (o):");
        br.addElement("    /*FOR ...*/");
        br.addElement("    /*pmb.memberName:notLike*/");
        br.addElement("    /*END*/");
        br.addItem("Comment Expression");
        br.addElement(expression);
        br.addItem("In-Loop Option");
        br.addElement(option);
        br.addItem("Specified SQL");
        br.addElement(specifiedSql);
        final String msg = br.buildExceptionMessage();
        throw new InLoopOptionOutOfLoopException(msg);
    }

    public static void throwLoopCurrentVariableOutOfForCommentException(String expression, String specifiedSql) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Loop's current variable was out of FOR comment scope!");
        br.addItem("Advice");
        br.addElement("Loop's current variables should be in FOR comment scope.");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    /*#current*/");
        br.addElement("    /*FOR*/.../*END*/");
        br.addElement("  (o):");
        br.addElement("    /*FOR*/");
        br.addElement("    /*#current*/");
        br.addElement("    /*END*/");
        br.addItem("Comment Expression");
        br.addElement(expression);
        br.addItem("Specified SQL");
        br.addElement(specifiedSql);
        final String msg = br.buildExceptionMessage();
        throw new LoopCurrentVariableOutOfForCommentException(msg);
    }

    protected static String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
