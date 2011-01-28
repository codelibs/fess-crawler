package org.seasar.robot.dbflute.twowaysql.node;

import org.seasar.robot.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.robot.dbflute.twowaysql.exception.BindVariableCommentInScopeNotListException;
import org.seasar.robot.dbflute.twowaysql.exception.BindVariableCommentParameterNullValueException;
import org.seasar.robot.dbflute.twowaysql.exception.InLoopOptionOutOfLoopException;
import org.seasar.robot.dbflute.unit.PlainTestCase;
import org.seasar.robot.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/05 Saturday)
 */
public class BindVariableNodeTest extends PlainTestCase {

    // ===================================================================================
    //                                                                               Basic
    //                                                                               =====
    public void test_analyze_basic() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " /*IF pmb.memberId != null*/member.MEMBER_ID = /*pmb.memberId*//*END*/";
        sql = sql + " /*IF pmb.memberName != null*/and member.MEMBER_NAME = /*pmb.memberName*/'TEST'/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberId(12);
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where member.MEMBER_ID = ? and member.MEMBER_NAME = ?";
        assertEquals(expected, ctx.getSql());
        assertEquals(2, ctx.getBindVariables().length);
        assertEquals(12, ctx.getBindVariables()[0]);
        assertEquals("foo", ctx.getBindVariables()[1]);
    }

    public void test_accept_string() {
        // ## Arrange ##
        String sql = "= /*pmb.memberName*/'foo'";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("bar");
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("= ?", ctx.getSql());
        assertEquals(1, ctx.getBindVariables().length);
        assertEquals("bar", ctx.getBindVariables()[0]);
    }

    public void test_accept_number() {
        // ## Arrange ##
        String sql = "= /*pmb.memberId*/8";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberId(3);
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("= ?", ctx.getSql());
        assertEquals(1, ctx.getBindVariables().length);
        assertEquals(3, ctx.getBindVariables()[0]);
    }

    public void test_accept_null_allowed() {
        // ## Arrange ##
        String sql = "= /*pmb.memberId*/8";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("= ?", ctx.getSql());
        assertEquals(1, ctx.getBindVariables().length);
        assertEquals(null, ctx.getBindVariables()[0]);
    }

    public void test_accept_null_notAllowed() {
        // ## Arrange ##
        String sql = "= /*pmb.memberId*/8";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, true);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        try {
            rootNode.accept(ctx);

            // ## Assert ##
            fail();
        } catch (BindVariableCommentParameterNullValueException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_accept_bindSymbol() {
        // ## Arrange ##
        String sql = "= /*pmb.memberName*/'foo'";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("ba?r");
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("= ?", ctx.getSql());
        assertEquals(1, ctx.getBindVariables().length);
        assertEquals("ba?r", ctx.getBindVariables()[0]);
    }

    // ===================================================================================
    //                                                                             InScope
    //                                                                             =======
    public void test_accept_inScope_list() {
        // ## Arrange ##
        String sql = "in /*pmb.memberNameList*/('foo', 'bar')";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("baz", "qux"));
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("in (?, ?)", ctx.getSql());
        assertEquals(2, ctx.getBindVariables().length);
        assertEquals("baz", ctx.getBindVariables()[0]);
        assertEquals("qux", ctx.getBindVariables()[1]);
    }

    public void test_accept_inScope_notList() {
        // ## Arrange ##
        String sql = "in /*pmb.memberName*/('foo', 'bar')";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        try {
            rootNode.accept(ctx);

            // ## Assert ##
            fail();
        } catch (BindVariableCommentInScopeNotListException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_accept_inScope_array_string_basic() {
        // ## Arrange ##
        String sql = "in /*pmb.memberNames*/('foo', 'bar')";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberNames(new String[] { "baz", "qux" });
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("in (?, ?)", ctx.getSql());
        assertEquals(2, ctx.getBindVariables().length);
        assertEquals("baz", ctx.getBindVariables()[0]);
        assertEquals("qux", ctx.getBindVariables()[1]);
    }

    public void test_accept_inScope_bindSymbol() {
        // ## Arrange ##
        String sql = "in /*pmb.memberNameList*/('foo', 'bar')";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("baz", "q?ux"));
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("in (?, ?)", ctx.getSql());
        assertEquals(2, ctx.getBindVariables().length);
        assertEquals("baz", ctx.getBindVariables()[0]);
        assertEquals("q?ux", ctx.getBindVariables()[1]);
    }

    // ===================================================================================
    //                                                                      In-Loop Option
    //                                                                      ==============
    public void test_accept_inLoopOption_basic() {
        // ## Arrange ##
        String sql = "= /*FOR pmb.memberNameList*//*#current:likePrefix*/'foo'/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("= ? escape '|' ? escape '|' ? escape '|' ", ctx.getSql());
        assertEquals(3, ctx.getBindVariables().length);
        assertEquals("foo%", ctx.getBindVariables()[0]);
        assertEquals("bar%", ctx.getBindVariables()[1]);
        assertEquals("baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_inLoopOption_outOfLoop() {
        // ## Arrange ##
        String sql = "= /*pmb.memberName:notLike*/'foo'";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("bar");
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        try {
            rootNode.accept(ctx);

            // ## Assert ##
            fail();
        } catch (InLoopOptionOutOfLoopException e) {
            // OK
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    private CommandContext createCtx(Object pmb) {
        return xcreateCommandContext(new Object[] { pmb }, new String[] { "pmb" }, new Class<?>[] { pmb.getClass() });
    }

    private CommandContext xcreateCommandContext(Object[] args, String[] argNames, Class<?>[] argTypes) {
        return xcreateCommandContextCreator(argNames, argTypes).createCommandContext(args);
    }

    private CommandContextCreator xcreateCommandContextCreator(String[] argNames, Class<?>[] argTypes) {
        return new CommandContextCreator(argNames, argTypes);
    }
}
