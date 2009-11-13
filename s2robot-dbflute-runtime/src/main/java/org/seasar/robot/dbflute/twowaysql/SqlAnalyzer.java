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
package org.seasar.robot.dbflute.twowaysql;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.seasar.robot.dbflute.exception.EndCommentNotFoundException;
import org.seasar.robot.dbflute.exception.IfCommentConditionNotFoundException;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.robot.dbflute.twowaysql.factory.SqlAnalyzerFactory;
import org.seasar.robot.dbflute.twowaysql.node.BeginNode;
import org.seasar.robot.dbflute.twowaysql.node.BindVariableNode;
import org.seasar.robot.dbflute.twowaysql.node.ContainerNode;
import org.seasar.robot.dbflute.twowaysql.node.ElseNode;
import org.seasar.robot.dbflute.twowaysql.node.EmbeddedValueNode;
import org.seasar.robot.dbflute.twowaysql.node.IfNode;
import org.seasar.robot.dbflute.twowaysql.node.Node;
import org.seasar.robot.dbflute.twowaysql.node.PrefixSqlNode;
import org.seasar.robot.dbflute.twowaysql.node.SqlNode;
import org.seasar.robot.dbflute.util.DfStringUtil;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class SqlAnalyzer {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _specifiedSql;
    protected boolean _blockNullParameter;
    protected SqlTokenizer _tokenizer;
    protected Stack<Node> _nodeStack = new Stack<Node>();
    protected List<String> _researchIfCommentList;
    protected List<String> _researchBindVariableCommentList;
    protected List<String> _researchEmbeddedValueCommentList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public SqlAnalyzer(String sql, boolean blockNullParameter) {
        sql = sql.trim();
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        _specifiedSql = sql;
        _blockNullParameter = blockNullParameter;
        _tokenizer = new SqlTokenizer(sql);
    }

    // ===================================================================================
    //                                                                             Analyze
    //                                                                             =======
    public Node analyze() {
        push(new ContainerNode());
        while (SqlTokenizer.EOF != _tokenizer.next()) {
            parseToken();
        }
        return pop();
    }

    protected void parseToken() {
        switch (_tokenizer.getTokenType()) {
        case SqlTokenizer.SQL:
            parseSql();
            break;
        case SqlTokenizer.COMMENT:
            parseComment();
            break;
        case SqlTokenizer.ELSE:
            parseElse();
            break;
        case SqlTokenizer.BIND_VARIABLE:
            parseBindVariable();
            break;
        }
    }

    protected void parseSql() {
        String sql = _tokenizer.getToken();
        if (isElseMode()) {
            sql = replaceString(sql, "--", "");
        }
        Node node = peek();
        if ((node instanceof IfNode || node instanceof ElseNode) && node.getChildSize() == 0) {
            SqlTokenizer st = new SqlTokenizer(sql);
            st.skipWhitespace();
            String token = st.skipToken();
            st.skipWhitespace();
            if (sql.startsWith(",")) { // is prefix
                if (sql.startsWith(", ")) {
                    node.addChild(createPrefixSqlNode(", ", sql.substring(2)));
                } else {
                    node.addChild(createPrefixSqlNode(",", sql.substring(1)));
                }
            } else if ("AND".equalsIgnoreCase(token) || "OR".equalsIgnoreCase(token)) { // is prefix
                node.addChild(createPrefixSqlNode(st.getBefore(), st.getAfter()));
            } else { // is not prefix
                node.addChild(createSqlNodeAsIfElseChildNode(sql));
            }
        } else {
            node.addChild(createSqlNode(sql));
        }
    }

    protected void parseComment() {
        final String comment = _tokenizer.getToken();
        if (isTargetComment(comment)) {
            if (isIfComment(comment)) {
                parseIf();
            } else if (isBeginComment(comment)) {
                parseBegin();
            } else if (isEndComment(comment)) {
                return;
            } else {
                parseCommentBindVariable();
            }
        } else if (comment != null && 0 < comment.length()) {
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            // [UnderReview]: Should I resolve bind character on scope comment(normal comment)?
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            String before = _tokenizer.getBefore();
            peek().addChild(createSqlNode(before.substring(before.lastIndexOf("/*"))));
        }
    }

    protected void parseIf() {
        final String condition = _tokenizer.getToken().substring(2).trim();
        if (DfStringUtil.isNullOrEmpty(condition)) {
            throwIfCommentConditionNotFoundException();
        }
        final IfNode ifNode = createIfNode(condition);
        peek().addChild(ifNode);
        push(ifNode);
        parseEnd();
    }

    protected void throwIfCommentConditionNotFoundException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The condition of IF comment was not found!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm the IF comment expression." + ln();
        msg = msg + "It may exist the IF comment that DOESN'T have a condition." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x) - /*IF*/XXX_ID = /*pmb.xxxId*/3/*END*/" + ln();
        msg = msg + "    (o) - /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3/*END*/" + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _tokenizer.getToken() + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentConditionNotFoundException(msg);
    }

    protected void parseBegin() {
        BeginNode beginNode = createBeginNode();
        peek().addChild(beginNode);
        push(beginNode);
        parseEnd();
    }

    protected void parseEnd() {
        while (SqlTokenizer.EOF != _tokenizer.next()) {
            if (_tokenizer.getTokenType() == SqlTokenizer.COMMENT && isEndComment(_tokenizer.getToken())) {
                pop();
                return;
            }
            parseToken();
        }
        throwEndCommentNotFoundException();
    }

    protected void throwEndCommentNotFoundException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The end comment was not found!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm the parameter comment logic." + ln();
        msg = msg + "It may exist the parameter comment that DOESN'T have an end comment." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x) - /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3" + ln();
        msg = msg + "    (o) - /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3/*END*/" + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new EndCommentNotFoundException(msg);
    }

    protected void parseElse() {
        final Node parent = peek();
        if (!(parent instanceof IfNode)) {
            return;
        }
        final IfNode ifNode = (IfNode) pop();
        final ElseNode elseNode = new ElseNode();
        ifNode.setElseNode(elseNode);
        push(elseNode);
        _tokenizer.skipWhitespace();
    }

    protected void parseCommentBindVariable() {
        final String expr = _tokenizer.getToken();
        final String testValue = _tokenizer.skipToken(true);
        if (expr.startsWith("$")) {
            peek().addChild(createEmbeddedValueNode(expr.substring(1), testValue));
        } else {
            peek().addChild(createBindVariableNode(expr, testValue));
        }
    }

    protected void parseBindVariable() {
        final String expr = _tokenizer.getToken();
        peek().addChild(createBindVariableNode(expr, null));
    }

    protected Node pop() {
        return (Node) _nodeStack.pop();
    }

    protected Node peek() {
        return (Node) _nodeStack.peek();
    }

    protected void push(Node node) {
        _nodeStack.push(node);
    }

    protected boolean isElseMode() {
        for (int i = 0; i < _nodeStack.size(); ++i) {
            if (_nodeStack.get(i) instanceof ElseNode) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTargetComment(String comment) {
        return comment != null && comment.length() > 0 && Character.isJavaIdentifierStart(comment.charAt(0));
    }

    private static boolean isIfComment(String comment) {
        return comment.startsWith("IF");
    }

    private static boolean isBeginComment(String content) {
        return content != null && "BEGIN".equals(content);
    }

    private static boolean isEndComment(String content) {
        return content != null && "END".equals(content);
    }

    protected BeginNode createBeginNode() {
        return new BeginNode();
    }

    protected IfNode createIfNode(String expr) {
        researchIfNeed(_researchIfCommentList, expr); // for research
        return new IfNode(expr, _specifiedSql);
    }

    protected BindVariableNode createBindVariableNode(String expr, String testValue) {
        researchIfNeed(_researchBindVariableCommentList, expr); // for research
        return new BindVariableNode(expr, testValue, _specifiedSql, _blockNullParameter);
    }

    protected EmbeddedValueNode createEmbeddedValueNode(String expr, String testValue) {
        researchIfNeed(_researchEmbeddedValueCommentList, expr); // for research
        return new EmbeddedValueNode(expr, testValue, _specifiedSql, _blockNullParameter);
    }

    protected SqlNode createSqlNode(String sql) {
        return SqlNode.createSqlNode(sql);
    }

    protected SqlNode createSqlNodeAsIfElseChildNode(String sql) {
        return SqlNode.createSqlNodeAsIfElseChild(sql);
    }

    protected PrefixSqlNode createPrefixSqlNode(String prefix, String sql) {
        return new PrefixSqlNode(prefix, sql);
    }

    // ===================================================================================
    //                                                                            Research
    //                                                                            ========
    /**
     * Research IF comments. (basically for research only, NOT for execution)<br />
     * This method should be called before calling analyze(). <br />
     * The returned list is filled with IF comment after calling analyze().
     * @return The list of IF comment. (NotNull)
     */
    public List<String> researchIfComment() { // should NOT be called with execution
        final List<String> resultList = new ArrayList<String>();
        _researchIfCommentList = resultList;
        return resultList;
    }

    /**
     * Research bind variable comments. (basically for research only, NOT for execution)<br />
     * This method should be called before calling analyze(). <br />
     * The returned list is filled with bind variable comment after calling analyze().
     * @return The list of bind variable comment. (NotNull)
     */
    public List<String> researchBindVariableComment() { // should NOT be called with execution
        final List<String> resultList = new ArrayList<String>();
        _researchBindVariableCommentList = resultList;
        return resultList;
    }

    /**
     * Research embedded value comments. (basically for research only, NOT for execution)<br />
     * This method should be called before calling analyze(). <br />
     * The returned list is filled with embedded value comment after calling analyze().
     * @return The list of embedded value comment. (NotNull)
     */
    public List<String> researchEmbeddedValueComment() { // should NOT be called with execution
        final List<String> resultList = new ArrayList<String>();
        _researchEmbeddedValueCommentList = resultList;
        return resultList;
    }

    protected void researchIfNeed(List<String> researchList, String expr) {
        if (researchList != null) {
            researchList.add(expr);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    protected final String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }

    // ===================================================================================
    //                                                                          DisplaySql
    //                                                                          ==========
    public static String convertTwoWaySql2DisplaySql(SqlAnalyzerFactory factory, String twoWaySql, Object arg,
            String logDateFormat, String logTimestampFormat) {
        final String[] argNames = new String[] { "pmb" };
        final Class<?>[] argTypes = new Class<?>[] { arg.getClass() };
        final Object[] args = new Object[] { arg };
        return convertTwoWaySql2DisplaySql(factory, twoWaySql, argNames, argTypes, args, logDateFormat,
                logTimestampFormat);
    }

    public static String convertTwoWaySql2DisplaySql(SqlAnalyzerFactory factory, String twoWaySql, String[] argNames,
            Class<?>[] argTypes, Object[] args, String logDateFormat, String logTimestampFormat) {
        final CommandContext context;
        {
            final SqlAnalyzer parser = factory.create(twoWaySql, false);
            final Node node = parser.analyze();
            final CommandContextCreator creator = new CommandContextCreator(argNames, argTypes);
            context = creator.createCommandContext(args);
            node.accept(context);
        }
        final String preparedSql = context.getSql();
        return DisplaySqlBuilder.buildDisplaySql(preparedSql, context.getBindVariables(), logDateFormat,
                logTimestampFormat);
    }
}
