package org.seasar.robot.dbflute.twowaysql.node;

import org.seasar.robot.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.robot.dbflute.twowaysql.exception.IfCommentNotFoundPropertyException;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.7.0 (2010/05/29 Saturday)
 */
public class BeginNodeTest extends PlainTestCase {

    // ===================================================================================
    //                                                                               Basic
    //                                                                               =====
    public void test_parse_BEGIN_for_where_all_true() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " /*IF pmb.memberId != null*/member.MEMBER_ID = 3/*END*/";
        sql = sql + " /*IF pmb.memberName != null*/and member.MEMBER_NAME = 'TEST'/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where member.MEMBER_ID = 3 and member.MEMBER_NAME = 'TEST'";
        assertEquals(expected, ctx.getSql());
    }

    public void test_parse_BEGIN_for_where_all_false() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " /*IF pmb.memberId != null*/member.MEMBER_ID = 3/*END*/";
        sql = sql + " /*IF pmb.memberName != null*/and member.MEMBER_NAME = 'TEST'/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "";
        assertEquals(expected, ctx.getSql());
    }

    public void test_parse_BEGIN_for_where_either_true() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " /*IF pmb.memberId != null*/member.MEMBER_ID = 3/*END*/";
        sql = sql + " /*IF pmb.memberName != null*/and member.MEMBER_NAME = 'TEST'/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("pmb");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where  member.MEMBER_NAME = 'TEST'";
        assertEquals(expected, ctx.getSql());
    }

    public void test_parse_BEGIN_for_where_either_true_ln() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where" + ln();
        sql = sql + " /*IF pmb.memberId != null*/" + ln();
        sql = sql + " member.MEMBER_ID = 3" + ln();
        sql = sql + " /*END*/" + ln();
        sql = sql + " /*IF pmb.memberName != null*/" + ln();
        sql = sql + " and member.MEMBER_NAME = 'TEST'" + ln();
        sql = sql + " /*END*/" + ln();
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("pmb");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where" + ln() + " " + ln() + " member.MEMBER_NAME = 'TEST'" + ln() + " " + ln();
        assertEquals(expected, ctx.getSql());
    }

    public void test_parse_BEGIN_for_where_either_true_or() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " /*IF pmb.memberId != null*/member.MEMBER_ID = 3/*END*/";
        sql = sql + " /*IF pmb.memberName != null*/or member.MEMBER_NAME = 'TEST'/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("pmb");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where  member.MEMBER_NAME = 'TEST'";
        assertEquals(expected, ctx.getSql());
    }

    public void test_parse_BEGIN_for_select_all_true() {
        // ## Arrange ##
        String sql = "select /*BEGIN*/";
        sql = sql + "/*IF pmb.memberId != null*/member.MEMBER_ID as c1/*END*/";
        sql = sql + "/*IF pmb.memberName != null*/, member.MEMBER_NAME as c2/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "select member.MEMBER_ID as c1, member.MEMBER_NAME as c2";
        assertEquals(expected, ctx.getSql());
    }

    public void test_parse_BEGIN_for_select_all_false() {
        // ## Arrange ##
        String sql = "select /*BEGIN*/";
        sql = sql + "/*IF pmb.memberId != null*/member.MEMBER_ID as c1/*END*/";
        sql = sql + "/*IF pmb.memberName != null*/, member.MEMBER_NAME as c2/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "select ";
        assertEquals(expected, ctx.getSql());
    }

    public void test_parse_BEGIN_for_select_either_true() {
        // ## Arrange ##
        String sql = "select /*BEGIN*/";
        sql = sql + "/*IF pmb.memberId != null*/member.MEMBER_ID as c1/*END*/";
        sql = sql + "/*IF pmb.memberName != null*/, member.MEMBER_NAME as c2/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "select member.MEMBER_NAME as c2";
        assertEquals(expected, ctx.getSql());
    }

    public void test_parse_BEGIN_not_adjustConnector() {
        // ## Arrange ##
        String sql = "select /*BEGIN*/, ";
        sql = sql + "/*IF pmb.memberId != null*/member.MEMBER_ID as c1/*END*/";
        sql = sql + "/*IF pmb.memberName != null*/, member.MEMBER_NAME as c2/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "select , member.MEMBER_NAME as c2";
        assertEquals(expected, ctx.getSql());
    }

    // ===================================================================================
    //                                                                              Nested
    //                                                                              ======
    public void test_parse_BEGIN_that_has_nested_BEGIN_true() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/";
        sql = sql + "FIXED";
        sql = sql + "/*END*/";
        sql = sql + " ";
        sql = sql + "/*BEGIN*/";
        sql = sql + "FIXED2 /*IF true*/and BBB/*END*/ /*IF true*/and CCC/*END*/";
        sql = sql + "/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);

        // Basically Unsupported!
        assertEquals("where FIXED FIXED2 BBB and CCC", ctx.getSql());
    }

    public void test_parse_BEGIN_that_has_nested_BEGIN_false() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/";
        sql = sql + "FIXED";
        sql = sql + "/*END*/";
        sql = sql + " ";
        sql = sql + "/*BEGIN*/";
        sql = sql + "FIXED2 /*IF false*/and BBB/*END*/ /*IF false*/and CCC/*END*/";
        sql = sql + "/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);

        // Basically Unsupported!
        assertEquals("where FIXED ", ctx.getSql());
    }

    public void test_parse_BEGIN_that_has_nested_BEGIN_allnest_false() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/";
        sql = sql + "FIXED";
        sql = sql + "/*END*/";
        sql = sql + " ";
        sql = sql + "/*BEGIN*/";
        sql = sql + "FIXED2 /*IF false*/and BBB/*END*/ /*IF false*/and CCC/*END*/";
        sql = sql + "/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);

        // Basically Unsupported!
        assertEquals("", ctx.getSql());
    }

    public void test_parse_BEGIN_that_has_nested_BEGIN_toponly_false() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/";
        sql = sql + "FIXED";
        sql = sql + "/*END*/";
        sql = sql + " ";
        sql = sql + "/*BEGIN*/";
        sql = sql + "FIXED2 /*IF true*/and BBB/*END*/ /*IF true*/and CCC/*END*/";
        sql = sql + "/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);

        assertEquals("where  FIXED2 BBB and CCC", ctx.getSql());
    }

    public void test_parse_BEGIN_that_has_nested_BEGIN_toponly_false_either_true() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/";
        sql = sql + "FIXED";
        sql = sql + "/*END*/";
        sql = sql + " ";
        sql = sql + "/*BEGIN*/";
        sql = sql + "FIXED2 /*IF false*/and BBB/*END*/ /*IF true*/and CCC/*END*/";
        sql = sql + "/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);

        assertEquals("where  FIXED2  CCC", ctx.getSql());
    }

    public void test_parse_BEGIN_that_has_nested_BEGIN_nest_and_adjustment() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/";
        sql = sql + "FIXED";
        sql = sql + "/*END*/";
        sql = sql + " ";
        sql = sql + "/*BEGIN*/";
        sql = sql + "FIXED2 /*IF false*/and BBB/*END*/ /*IF true*/and CCC/*END*/";
        sql = sql + "/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);

        // Basically Unsupported!
        assertEquals("where FIXED FIXED2  CCC", ctx.getSql());
    }

    public void test_parse_BEGIN_that_has_nested_BEGIN_self_and_adjustment() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/";
        sql = sql + "FIXED";
        sql = sql + "/*END*/";
        sql = sql + " ";
        sql = sql + "/*BEGIN*/";
        sql = sql + "and FIXED2 /*IF false*/and BBB/*END*/ /*IF true*/and CCC/*END*/";
        sql = sql + "/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);

        // Basically Unsupported!
        assertEquals("where  FIXED2  CCC", ctx.getSql());
    }

    // ===================================================================================
    //                                                                           IF Nested
    //                                                                           =========
    public void test_parse_BEGIN_that_has_nested_IFIF_root_has_and() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberId != null*/";
        sql = sql + "FIXED";
        sql = sql + "/*END*/";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/";
        sql = sql + "and AAA /*IF true*/and BBB /*IF true*/and CCC/*END*//*END*/ /*IF true*/and DDD/*END*/";
        sql = sql + "/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where  AAA and BBB and CCC and DDD";
        assertEquals(expected, ctx.getSql());
    }

    public void test_parse_BEGIN_that_has_nested_IFIF_root_has_no_and() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/";
        sql = sql + "AAA /*IF true*/and BBB /*IF true*/and CCC/*END*//*END*/ /*IF true*/and DDD/*END*/";
        sql = sql + "/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where AAA and BBB and CCC and DDD";
        assertEquals(expected, ctx.getSql());
    }

    public void test_parse_BEGIN_that_has_nested_IFIF_root_has_both() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberId != null*/";
        sql = sql + "FIXED";
        sql = sql + "/*END*/";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/";
        sql = sql + "and AAA /*IF true*/and BBB /*IF true*/and CCC/*END*//*END*/ /*IF true*/and DDD/*END*/";
        sql = sql + "/*END*/";
        sql = sql + "/*END*/";
        sql = sql + " ";
        sql = sql + "/*BEGIN*/where";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberId != null*/";
        sql = sql + "FIXED";
        sql = sql + "/*END*/";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/";
        sql = sql + "and AAA /*IF true*/and BBB /*IF true*/and CCC/*END*//*END*/ /*IF true*/and DDD/*END*/";
        sql = sql + "/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where  AAA and BBB and CCC and DDD where  AAA and BBB and CCC and DDD";
        assertEquals(expected, ctx.getSql());
    }

    public void test_parse_BEGIN_that_has_nested_IFIF_fixed_condition_() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " 1 = 1";
        sql = sql + "/*IF pmb.memberId != null*/";
        sql = sql + "and FIXED";
        sql = sql + "/*END*/";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/";
        sql = sql + "and AAA /*IF true*/and BBB /*IF true*/and CCC/*END*//*END*/ /*IF true*/and DDD/*END*/";
        sql = sql + "/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where 1 = 1 AAA and BBB and CCC and DDD";
        assertEquals(expected, ctx.getSql()); // BEGIN comment does not need in this pattern
    }

    public void test_parse_BEGIN_that_has_nested_IFIF_all_false() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberId != null*/";
        sql = sql + "AAA /*IF false*/and BBB /*IF false*/and CCC/*END*//*END*/ /*IF false*/and DDD/*END*/";
        sql = sql + "/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        assertEquals("", ctx.getSql());
    }

    public void test_parse_BEGIN_that_has_nested_IFIF_nonsense_all_true() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberId != null*//*IF true*/and AAA/*END*//*END*/";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/and BBB/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        assertEquals("where AAA and BBB", ctx.getSql()); // basically nonsense
    }

    public void test_parse_BEGIN_that_has_nested_IFIF_nonsense_nested_false() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberId != null*//*IF false*/and AAA/*END*//*END*/";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/and BBB/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        assertEquals("where  and BBB", ctx.getSql()); // basically nonsense
    }

    // ===================================================================================
    //                                                                           UpperCase
    //                                                                           =========
    public void test_parse_BEGIN_where_upperCase_that_has_nested_IFIF_root_has_and() {
        // ## Arrange ##
        String sql = "/*BEGIN*/WHERE";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberId != null*/";
        sql = sql + "FIXED";
        sql = sql + "/*END*/";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/";
        sql = sql + "AND AAA /*IF true*/AND BBB /*IF true*/AND CCC/*END*//*END*/ /*IF true*/AND DDD/*END*/";
        sql = sql + "/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "WHERE  AAA AND BBB AND CCC AND DDD";
        assertEquals(expected, ctx.getSql());
    }

    // ===================================================================================
    //                                                                                  OR
    //                                                                                  ==
    public void test_parse_BEGIN_where_upperCase_that_has_nested_IFIF_root_has_or() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberId != null*/";
        sql = sql + "FIXED";
        sql = sql + "/*END*/";
        sql = sql + " ";
        sql = sql + "/*IF pmb.memberName != null*/";
        sql = sql + "or AAA /*IF true*/and BBB /*IF true*/OR CCC/*END*//*END*/ /*IF true*/or DDD/*END*/";
        sql = sql + "/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.analyze();

        // ## Assert ##
        MockMemberPmb pmb = new MockMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log("ctx:" + ctx);
        String expected = "where  AAA and BBB OR CCC or DDD";
        assertEquals(expected, ctx.getSql());
    }

    // ===================================================================================
    //                                                                   NotFound Property
    //                                                                   =================
    public void test_parse_BEGIN_IF_notFoundProperty_basic() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " /*IF pmb.wrongMemberId != null*/member.MEMBER_ID = 3/*END*/";
        sql = sql + " /*IF pmb.memberName != null*/and member.MEMBER_NAME = 'foo'/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        try {
            MockMemberPmb pmb = new MockMemberPmb();
            pmb.setMemberName("foo");
            Node rootNode = analyzer.analyze();
            CommandContext ctx = createCtx(pmb);
            rootNode.accept(ctx);

            // ## Assert ##
            fail();
        } catch (IfCommentNotFoundPropertyException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_parse_BEGIN_IF_notFoundProperty_with_likeSearch() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " /*IF pmb.wrongMemberId != null*/member.MEMBER_ID = /*pmb.memberId*/3/*END*/";
        sql = sql + " /*IF pmb.memberName != null*/and member.MEMBER_NAME like /*pmb.memberName*/'foo%'/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        try {
            MockLikeSearchMemberPmb pmb = new MockLikeSearchMemberPmb();
            Node rootNode = analyzer.analyze();
            CommandContext ctx = createCtx(pmb);
            rootNode.accept(ctx);

            // ## Assert ##
            fail();
        } catch (IfCommentNotFoundPropertyException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_parse_BEGIN_IF_notFoundProperty_with_parameterMap() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " /*IF pmb.wrongMemberId != null*/member.MEMBER_ID = /*pmb.memberId*/3/*END*/";
        sql = sql + " /*IF pmb.memberName != null*/and member.MEMBER_NAME like /*pmb.memberName*/'foo%'/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        try {
            MockPagingMemberPmb pmb = new MockPagingMemberPmb();
            Node rootNode = analyzer.analyze();
            CommandContext ctx = createCtx(pmb);
            rootNode.accept(ctx);

            // ## Assert ##
            fail();
        } catch (IfCommentNotFoundPropertyException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_parse_BEGIN_BIND_notFoundProperty_IF_false() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where";
        sql = sql + " /*IF pmb.memberId != null*/member.MEMBER_ID = /*pmb.wrongMemberId*/3/*END*/";
        sql = sql + " /*IF pmb.memberName != null*/and member.MEMBER_NAME like /*pmb.memberName*/'foo%'/*END*/";
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        MockMemberPmb pmb = new MockMemberPmb();
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);

        // ## Assert ##
        assertEquals("", ctx.getSql());
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
