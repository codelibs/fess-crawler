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
package org.seasar.robot.dbflute.twowaysql;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.robot.dbflute.twowaysql.exception.EndCommentNotFoundException;
import org.seasar.robot.dbflute.twowaysql.exception.ForCommentExpressionEmptyException;
import org.seasar.robot.dbflute.twowaysql.exception.IfCommentConditionEmptyException;
import org.seasar.robot.dbflute.twowaysql.factory.SqlAnalyzerFactory;
import org.seasar.robot.dbflute.twowaysql.node.BeginNode;
import org.seasar.robot.dbflute.twowaysql.node.BindVariableNode;
import org.seasar.robot.dbflute.twowaysql.node.ElseNode;
import org.seasar.robot.dbflute.twowaysql.node.EmbeddedVariableNode;
import org.seasar.robot.dbflute.twowaysql.node.ForNode;
import org.seasar.robot.dbflute.twowaysql.node.IfNode;
import org.seasar.robot.dbflute.twowaysql.node.LoopAbstractNode;
import org.seasar.robot.dbflute.twowaysql.node.LoopFirstNode;
import org.seasar.robot.dbflute.twowaysql.node.LoopLastNode;
import org.seasar.robot.dbflute.twowaysql.node.LoopNextNode;
import org.seasar.robot.dbflute.twowaysql.node.Node;
import org.seasar.robot.dbflute.twowaysql.node.RootNode;
import org.seasar.robot.dbflute.twowaysql.node.SqlConnectorAdjustable;
import org.seasar.robot.dbflute.twowaysql.node.SqlConnectorNode;
import org.seasar.robot.dbflute.twowaysql.node.SqlPartsNode;
import org.seasar.robot.dbflute.twowaysql.node.ForNode.LoopVariableType;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.Srl;

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
    protected boolean _inBeginScope;
    protected List<String> _researchIfCommentList;
    protected List<String> _researchForCommentList;
    protected List<String> _researchBindVariableCommentList;
    protected List<String> _researchEmbeddedVariableCommentList;

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
        push(createRootNode()); // root node of all
        while (SqlTokenizer.EOF != _tokenizer.next()) {
            parseToken();
        }
        return pop();
    }

    protected RootNode createRootNode() {
        return new RootNode();
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

    // -----------------------------------------------------
    //                                             SQL Parts
    //                                             ---------
    protected void parseSql() {
        final String sql;
        {
            String token = _tokenizer.getToken();
            if (isElseMode()) {
                token = replaceString(token, "--", "");
            }
            sql = token;
        }
        final Node node = peek();
        if (isSqlConnectorAdjustable(node)) {
            processSqlConnectorAdjustable(node, sql);
        } else {
            node.addChild(createSqlPartsNodeOutOfConnector(node, sql));
        }
    }

    protected void processSqlConnectorAdjustable(Node node, String sql) {
        final SqlTokenizer st = new SqlTokenizer(sql);
        st.skipWhitespace();
        final String token = st.skipToken();
        st.skipWhitespace();
        if (sql.startsWith(",")) { // is connector
            if (sql.startsWith(", ")) {
                node.addChild(createSqlConnectorNode(node, ", ", sql.substring(2)));
            } else {
                node.addChild(createSqlConnectorNode(node, ",", sql.substring(1)));
            }
        } else if ("and".equalsIgnoreCase(token) || "or".equalsIgnoreCase(token)) { // is connector
            node.addChild(createSqlConnectorNode(node, st.getBefore(), st.getAfter()));
        } else { // is not connector
            node.addChild(createSqlPartsNodeThroughConnector(node, sql));
        }
    }

    protected boolean isSqlConnectorAdjustable(Node node) {
        if (node.getChildSize() > 0) {
            return false;
        }
        return (node instanceof SqlConnectorAdjustable) && !isTopBegin(node);
    }

    // -----------------------------------------------------
    //                                               Comment
    //                                               -------
    protected void parseComment() {
        final String comment = _tokenizer.getToken();
        if (isTargetComment(comment)) { // parameter comment
            if (isBeginComment(comment)) {
                parseBegin();
            } else if (isIfComment(comment)) {
                parseIf();
            } else if (isForComment(comment)) {
                parseFor();
            } else if (isLoopVariableComment(comment)) {
                parseLoopVariable();
            } else if (isEndComment(comment)) {
                return;
            } else {
                parseCommentBindVariable();
            }
        } else if (Srl.is_NotNull_and_NotTrimmedEmpty(comment)) { // plain comment
            final String before = _tokenizer.getBefore();
            final String content = before.substring(before.lastIndexOf("/*"));
            peek().addChild(createSqlPartsNode(content));
        }
    }

    protected static boolean isTargetComment(String comment) {
        if (Srl.is_Null_or_TrimmedEmpty(comment)) {
            return false;
        }
        if (!comment.startsWith(ForNode.CURRENT_VARIABLE)) { // except current variable from check
            if (!Character.isJavaIdentifierStart(comment.charAt(0))) {
                return false;
            }
        }
        return true;
    }

    // -----------------------------------------------------
    //                                                 BEGIN
    //                                                 -----
    protected static boolean isBeginComment(String comment) {
        return BeginNode.MARK.equals(comment);
    }

    protected void parseBegin() {
        final BeginNode beginNode = createBeginNode();
        try {
            _inBeginScope = true;
            peek().addChild(beginNode);
            push(beginNode);
            parseEnd();
        } finally {
            _inBeginScope = false;
        }
    }

    protected BeginNode createBeginNode() {
        return new BeginNode(_inBeginScope);
    }

    protected boolean isTopBegin(Node node) {
        if (!(node instanceof BeginNode)) {
            return false;
        }
        return !((BeginNode) node).isNested();
    }

    protected boolean isNestedBegin(Node node) {
        if (!(node instanceof BeginNode)) {
            return false;
        }
        return ((BeginNode) node).isNested();
    }

    // -----------------------------------------------------
    //                                                    IF
    //                                                    --
    private static boolean isIfComment(String comment) {
        return comment.startsWith(IfNode.PREFIX);
    }

    protected void parseIf() {
        final String comment = _tokenizer.getToken();
        final String condition = comment.substring(IfNode.PREFIX.length()).trim();
        if (Srl.is_Null_or_TrimmedEmpty(condition)) {
            throwIfCommentConditionEmptyException();
        }
        final IfNode ifNode = createIfNode(condition);
        peek().addChild(ifNode);
        push(ifNode);
        parseEnd();
    }

    protected IfNode createIfNode(String expr) {
        researchIfNeeds(_researchIfCommentList, expr); // for research
        return new IfNode(expr, _specifiedSql);
    }

    protected void throwIfCommentConditionEmptyException() {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The condition of IF comment was empty!");
        br.addItem("Advice");
        br.addElement("Please confirm the IF comment expression.");
        br.addElement("Your IF comment might not have a condition.");
        br.addElement("For example:");
        br.addElement("  (x) - /*IF */XXX_ID = /*pmb.xxxId*/3/*END*/");
        br.addElement("  (o) - /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3/*END*/");
        br.addItem("IF Comment");
        br.addElement(_tokenizer.getToken());
        br.addItem("Specified SQL");
        br.addElement(_specifiedSql);
        final String msg = br.buildExceptionMessage();
        throw new IfCommentConditionEmptyException(msg);
    }

    // -----------------------------------------------------
    //                                                  ELSE
    //                                                  ----
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

    // -----------------------------------------------------
    //                                                   FOR
    //                                                   ---
    private static boolean isForComment(String comment) {
        return comment.startsWith(ForNode.PREFIX);
    }

    protected void parseFor() {
        final String comment = _tokenizer.getToken();
        final String condition = comment.substring(ForNode.PREFIX.length()).trim();
        if (Srl.is_Null_or_TrimmedEmpty(condition)) {
            throwForCommentExpressionEmptyException();
        }
        final ForNode forNode = createForNode(condition);
        peek().addChild(forNode);
        push(forNode);
        parseEnd();
    }

    protected ForNode createForNode(String expr) {
        researchIfNeeds(_researchForCommentList, expr); // for research
        return new ForNode(expr, _specifiedSql);
    }

    private static boolean isLoopVariableComment(String comment) {
        return comment.startsWith(LoopFirstNode.MARK) || comment.startsWith(LoopNextNode.MARK)
                || comment.startsWith(LoopLastNode.MARK);
    }

    protected void parseLoopVariable() { // should be in FOR comment scope
        final String comment = _tokenizer.getToken();
        final String code = Srl.substringFirstFront(comment, " ");
        if (Srl.is_Null_or_TrimmedEmpty(code)) { // no way
            String msg = "Unknown loop variable comment: " + comment;
            throw new IllegalStateException(msg);
        }
        final LoopVariableType type = LoopVariableType.codeOf(code);
        if (type == null) { // no way
            String msg = "Unknown loop variable comment: " + comment;
            throw new IllegalStateException(msg);
        }
        final String condition = comment.substring(type.name().length()).trim();
        final LoopAbstractNode loopFirstNode = createLoopFirstNode(condition, type);
        peek().addChild(loopFirstNode);
        if (Srl.count(condition, "'") < 2) {
            push(loopFirstNode);
            parseEnd();
        }
    }

    protected LoopAbstractNode createLoopFirstNode(String expr, LoopVariableType type) {
        return type.createNode(expr, _specifiedSql);
    }

    protected void throwForCommentExpressionEmptyException() {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The expression of FOR comment was empty!");
        br.addItem("Advice");
        br.addElement("Please confirm the FOR comment expression.");
        br.addElement("Your FOR comment might not have an expression.");
        br.addElement("For example:");
        br.addElement("  (x) - /*FOR */XXX_ID = /*#element*/3/*END*/");
        br.addElement("  (o) - /*FOR pmb.xxxList*/XXX_ID = /*#element*/3/*END*/");
        br.addItem("FOR Comment");
        br.addElement(_tokenizer.getToken());
        br.addItem("Specified SQL");
        br.addElement(_specifiedSql);
        final String msg = br.buildExceptionMessage();
        throw new ForCommentExpressionEmptyException(msg);
    }

    // -----------------------------------------------------
    //                                                   END
    //                                                   ---
    private static boolean isEndComment(String content) {
        return content != null && "END".equals(content);
    }

    protected void parseEnd() {
        final int commentType = SqlTokenizer.COMMENT;
        while (SqlTokenizer.EOF != _tokenizer.next()) {
            if (_tokenizer.getTokenType() == commentType && isEndComment(_tokenizer.getToken())) {
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
        msg = msg + "For example:" + ln();
        msg = msg + "  (x) - /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3" + ln();
        msg = msg + "  (o) - /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3/*END*/" + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new EndCommentNotFoundException(msg);
    }

    // -----------------------------------------------------
    //                                      Bind or Embedded
    //                                      ----------------
    protected void parseCommentBindVariable() {
        final String expr = _tokenizer.getToken();
        final String testValue = _tokenizer.skipToken(true);
        if (expr.startsWith(EmbeddedVariableNode.PREFIX)) {
            final String realExpr = expr.substring(EmbeddedVariableNode.PREFIX.length());
            peek().addChild(createEmbeddedVariableNode(realExpr, testValue));
        } else {
            peek().addChild(createBindVariableNode(expr, testValue));
        }
    }

    protected void parseBindVariable() {
        final String expr = _tokenizer.getToken();
        peek().addChild(createBindVariableNode(expr, null));
    }

    protected BindVariableNode createBindVariableNode(String expr, String testValue) {
        researchIfNeeds(_researchBindVariableCommentList, expr); // for research
        return new BindVariableNode(expr, testValue, _specifiedSql, _blockNullParameter);
    }

    protected EmbeddedVariableNode createEmbeddedVariableNode(String expr, String testValue) {
        researchIfNeeds(_researchEmbeddedVariableCommentList, expr); // for research
        return new EmbeddedVariableNode(expr, testValue, _specifiedSql, _blockNullParameter);
    }

    // -----------------------------------------------------
    //                                          Various Node
    //                                          ------------
    protected SqlConnectorNode createSqlConnectorNode(Node node, String connector, String sqlParts) {
        if (isNestedBegin(node)) { // basically nested if BEGIN node because checked before
            // connector adjustment of BEGIN is independent 
            return SqlConnectorNode.createSqlConnectorNodeAsIndependent(connector, sqlParts);
        } else {
            return SqlConnectorNode.createSqlConnectorNode(connector, sqlParts);
        }
    }

    protected SqlPartsNode createSqlPartsNodeOutOfConnector(Node node, String sqlParts) {
        if (isTopBegin(node)) { // top BEGIN only (nested goes 'else' statement)
            return SqlPartsNode.createSqlPartsNodeAsIndependent(sqlParts);
        } else {
            return createSqlPartsNode(sqlParts);
        }
    }

    protected SqlPartsNode createSqlPartsNodeThroughConnector(Node node, String sqlParts) {
        if (isNestedBegin(node)) { // basically nested if BEGIN node because checked before
            // connector adjustment of BEGIN is independent
            return SqlPartsNode.createSqlPartsNodeAsIndependent(sqlParts);
        } else {
            return createSqlPartsNode(sqlParts);
        }
    }

    protected SqlPartsNode createSqlPartsNode(String sqlParts) { // as plain
        return SqlPartsNode.createSqlPartsNode(sqlParts);
    }

    // -----------------------------------------------------
    //                                            Node Stack
    //                                            ----------
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
     * Research embedded variable comments. (basically for research only, NOT for execution)<br />
     * This method should be called before calling analyze(). <br />
     * The returned list is filled with embedded variable comment after calling analyze().
     * @return The list of embedded variable comment. (NotNull)
     */
    public List<String> researchEmbeddedVariableComment() { // should NOT be called with execution
        final List<String> resultList = new ArrayList<String>();
        _researchEmbeddedVariableCommentList = resultList;
        return resultList;
    }

    protected void researchIfNeeds(List<String> researchList, String expr) {
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
        return Srl.replace(text, fromText, toText);
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
            final SqlAnalyzer parser = createSqlAnalyzer4DisplaySql(factory, twoWaySql);
            final Node node = parser.analyze();
            final CommandContextCreator creator = new CommandContextCreator(argNames, argTypes);
            context = creator.createCommandContext(args);
            node.accept(context);
        }
        final String preparedSql = context.getSql();
        return DisplaySqlBuilder.buildDisplaySql(preparedSql, context.getBindVariables(), logDateFormat,
                logTimestampFormat);
    }

    protected static SqlAnalyzer createSqlAnalyzer4DisplaySql(SqlAnalyzerFactory factory, String twoWaySql) {
        if (factory == null) {
            String msg = "The factory of SQL analyzer should exist.";
            throw new IllegalStateException(msg);
        }
        final boolean blockNullParameter = false;
        final SqlAnalyzer created = factory.create(twoWaySql, blockNullParameter);
        if (created != null) {
            return created;
        }
        String msg = "The factory should not return null:";
        msg = msg + " sql=" + twoWaySql + " factory=" + factory;
        throw new IllegalStateException(msg);
    }
}
