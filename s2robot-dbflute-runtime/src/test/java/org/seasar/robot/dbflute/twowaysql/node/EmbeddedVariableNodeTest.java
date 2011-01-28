package org.seasar.robot.dbflute.twowaysql.node;

import org.seasar.robot.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.robot.dbflute.twowaysql.exception.EmbeddedVariableCommentContainsBindSymbolException;
import org.seasar.robot.dbflute.twowaysql.exception.EmbeddedVariableCommentInScopeNotListException;
import org.seasar.robot.dbflute.twowaysql.exception.EmbeddedVariableCommentParameterNullValueException;
import org.seasar.robot.dbflute.unit.PlainTestCase;
import org.seasar.robot.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.7.0 (2010/05/29 Saturday)
 */
public class EmbeddedVariableNodeTest extends PlainTestCase {

    // ===================================================================================
    //                                                                               Basic
    //                                                                               =====
    public void test_analyze_basic() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " /*IF pmb.memberId != null*/member.MEMBER_ID = /*$pmb.memberId*//*END*/";
        sql = sql + " /*IF pmb.memberName != null*/and member.MEMBER_NAME = /*$pmb.memberName*/'TEST'/*END*/";
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
        String expected = "where member.MEMBER_ID = 12 and member.MEMBER_NAME = 'foo'";
        assertEquals(expected, ctx.getSql());
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_string_basic() {
        // ## Arrange ##
        String sql = "= /*$pmb.memberName*/'foo'";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("bar");
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("= 'bar'", ctx.getSql());
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_string_notQuoted() {
        // ## Arrange ##
        String sql = "= /*$pmb.memberName*/foo";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("bar");
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("= bar", ctx.getSql());
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_number_basic() {
        // ## Arrange ##
        String sql = "= /*$pmb.memberId*/8";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberId(3);
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("= 3", ctx.getSql());
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_number_quoted() {
        // ## Arrange ##
        String sql = "= /*$pmb.memberId*/'8'";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberId(3);
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("= '3'", ctx.getSql());
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_null_allowed() {
        // ## Arrange ##
        String sql = "= /*$pmb.memberId*/8";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("= null", ctx.getSql());
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_null_notAllowed() {
        // ## Arrange ##
        String sql = "= /*$pmb.memberId*/8";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, true);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        try {
            rootNode.accept(ctx);

            // ## Assert ##
            fail();
        } catch (EmbeddedVariableCommentParameterNullValueException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_accept_contains_bindSymbol() {
        // ## Arrange ##
        String sql = "in /*$pmb.memberName*/'foo'";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("fo?o");
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        try {
            rootNode.accept(ctx);

            // ## Assert ##
            fail();
        } catch (EmbeddedVariableCommentContainsBindSymbolException e) {
            // OK
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                             InScope
    //                                                                             =======
    public void test_accept_inScope_list_string_basic() {
        // ## Arrange ##
        String sql = "in /*$pmb.memberNameList*/('foo', 'bar')";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("baz", "qux"));
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("in ('baz', 'qux')", ctx.getSql());
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_inScope_list_string_notQuoted() {
        // ## Arrange ##
        String sql = "in /*$pmb.memberNameList*/(foo, bar)";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("baz", "qux"));
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("in (baz, qux)", ctx.getSql());
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_inScope_list_integer_basic() {
        // ## Arrange ##
        String sql = "in /*$pmb.memberIdList*/(56, 88)";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberIdList(DfCollectionUtil.newArrayList(123, 456));
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("in (123, 456)", ctx.getSql());
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_inScope_list_integer_quoted() {
        // ## Arrange ##
        String sql = "in /*$pmb.memberIdList*/('56', '88')";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberIdList(DfCollectionUtil.newArrayList(123, 456));
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("in ('123', '456')", ctx.getSql());
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_inScope_list_eitherQuoted() {
        // ## Arrange ##
        String sql = "in /*$pmb.memberNameList*/(foo, 'bar')";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("baz", "qux"));
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("in ('baz', 'qux')", ctx.getSql());
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_inScope_notList() {
        // ## Arrange ##
        String sql = "in /*$pmb.memberName*/('foo', 'bar')";
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
        } catch (EmbeddedVariableCommentInScopeNotListException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_accept_inScope_array_string_basic() {
        // ## Arrange ##
        String sql = "in /*$pmb.memberNames*/('foo', 'bar')";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberNames(new String[] { "baz", "qux" });
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        log("ctx:" + ctx);
        assertEquals("in ('baz', 'qux')", ctx.getSql());
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_inScope_bindSymbol() {
        // ## Arrange ##
        String sql = "in /*$pmb.memberNameList*/('foo', 'bar')";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        Node rootNode = analyzer.analyze();
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("baz", "q?ux"));
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        try {
            rootNode.accept(ctx);

            // ## Assert ##
            fail();
        } catch (EmbeddedVariableCommentContainsBindSymbolException e) {
            // OK
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                     Dynamic Binding
    //                                                                     ===============
    public void test_analyze_dynamicBinding() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " /*IF pmb.memberId != null*/member.MEMBER_ID /*$pmb.memberName*//*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberId(12);
        pmb.setMemberName("= /*pmb.memberId*/99");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where member.MEMBER_ID = ?";
        assertEquals(expected, ctx.getSql());
        assertEquals(1, ctx.getBindVariables().length);
        assertEquals(12, ctx.getBindVariables()[0]);
    }

    public void test_analyze_dynamicBinding_IF() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " /*IF pmb.memberId != null*/member.MEMBER_ID /*$pmb.memberName*//*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberId(12);
        pmb.setMemberName("= /*IF pmb.memberId != null*/foo/*pmb.memberId*/99 bar/*END*/");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where member.MEMBER_ID = foo? bar";
        assertEquals(expected, ctx.getSql());
        assertEquals(1, ctx.getBindVariables().length);
        assertEquals(12, ctx.getBindVariables()[0]);
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
