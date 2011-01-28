package org.seasar.robot.dbflute.twowaysql.node;

import org.seasar.robot.dbflute.exception.CommentTerminatorNotFoundException;
import org.seasar.robot.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.robot.dbflute.twowaysql.exception.EndCommentNotFoundException;
import org.seasar.robot.dbflute.twowaysql.exception.IfCommentConditionEmptyException;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.7.0 (2010/05/29 Saturday)
 */
public class IfNodeTest extends PlainTestCase {

    // ===================================================================================
    //                                                                               Basic
    //                                                                               =====
    public void test_parse_IF_true() {
        // ## Arrange ##
        String sql = "/*IF pmb.memberName != null*/and member.MEMBER_NAME = 'TEST'/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        assertEquals("and member.MEMBER_NAME = 'TEST'", ctx.getSql());
    }

    public void test_parse_IF_false() {
        // ## Arrange ##
        String sql = "/*IF pmb.memberName != null*/and member.MEMBER_NAME = 'TEST'/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName(null);
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        assertEquals("", ctx.getSql().trim());
    }

    public void test_parse_IF_for_where_one_true() {
        // ## Arrange ##
        String sql = "where";
        sql = sql + " /*IF pmb.memberId != null*/member.MEMBER_ID = 3/*END*/";
        sql = sql + " /*IF pmb.memberName != null*/and member.MEMBER_NAME = 'TEST'/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where  and member.MEMBER_NAME = 'TEST'";
        assertEquals(expected, ctx.getSql());
    }

    // ===================================================================================
    //                                                                                Else
    //                                                                                ====
    public void test_parse_IF_Else_elseValid() {
        // ## Arrange ##
        String sql = "where";
        sql = sql + " /*IF pmb.memberId != null*/";
        sql = sql + " select foo";
        sql = sql + "  -- ELSE select count(*)";
        sql = sql + " /*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where select count(*) ";
        assertEquals(expected, ctx.getSql());
    }

    public void test_parse_IF_Else_ifValid_in_BEGIN() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " /*IF pmb.memberId != null*/";
        sql = sql + " select foo";
        sql = sql + "  -- ELSE select count(*)";
        sql = sql + " /*END*/";
        sql = sql + " /*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberId(3);
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where  select foo   ";
        assertEquals(expected, ctx.getSql());
    }

    public void test_parse_IF_Else_elseValid_in_BEGIN() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " /*IF pmb.memberId != null*/";
        sql = sql + " select foo";
        sql = sql + "  -- ELSE select count(*)";
        sql = sql + " /*END*/";
        sql = sql + " /*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where select count(*)  ";
        assertEquals(expected, ctx.getSql());
    }

    // ===================================================================================
    //                                                                           Exception
    //                                                                           =========
    public void test_parse_IF_terminatorNotFound() {
        // ## Arrange ##
        String sql = "where";
        sql = sql + " /*IF pmb.memberId*";
        sql = sql + " select foo";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        try {
            analyzer.analyze();

            // ## Assert ##
            fail();
        } catch (CommentTerminatorNotFoundException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_parse_IF_emptyCondition() {
        // ## Arrange ##
        String sql = "where";
        sql = sql + " /*IF */";
        sql = sql + " select foo";
        sql = sql + " /*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        try {
            analyzer.analyze();

            // ## Assert ##
            fail();
        } catch (IfCommentConditionEmptyException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_parse_IF_endNotFound() {
        // ## Arrange ##
        String sql = "where";
        sql = sql + " /*IF pmb.memberId != null*/";
        sql = sql + " select foo";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        try {
            analyzer.analyze();

            // ## Assert ##
            fail();
        } catch (EndCommentNotFoundException e) {
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
