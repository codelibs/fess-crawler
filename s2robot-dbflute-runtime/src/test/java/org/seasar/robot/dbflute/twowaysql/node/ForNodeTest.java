package org.seasar.robot.dbflute.twowaysql.node;

import java.util.List;

import org.seasar.robot.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.robot.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.robot.dbflute.twowaysql.exception.BindVariableCommentIllegalParameterBeanSpecificationException;
import org.seasar.robot.dbflute.twowaysql.exception.EndCommentNotFoundException;
import org.seasar.robot.dbflute.twowaysql.exception.ForCommentParameterNullElementException;
import org.seasar.robot.dbflute.twowaysql.exception.LoopCurrentVariableOutOfForCommentException;
import org.seasar.robot.dbflute.twowaysql.pmbean.ParameterBean;
import org.seasar.robot.dbflute.unit.PlainTestCase;
import org.seasar.robot.dbflute.util.DfCollectionUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * @author jflute
 */
public class ForNodeTest extends PlainTestCase {

    // ===================================================================================
    //                                                                               Basic
    //                                                                               =====
    public void test_accept_basic() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" where").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   and MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("select * from MEMBER"));
        assertTrue(actual.contains(" where"));
        assertFalse(actual.contains("/*FOR "));
        assertFalse(actual.contains("/*END*/"));
        assertFalse(actual.contains("FIRST"));
        assertFalse(actual.contains("NEXT"));
        assertFalse(actual.contains("LAST"));
        assertTrue(actual.contains(" and MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertEquals("foo%", ctx.getBindVariables()[0]);
        assertEquals("bar%", ctx.getBindVariables()[1]);
        assertEquals("baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_FirstAnd() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" where").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   /*FIRST*/and /*END*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains(" and MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertEquals("foo%", ctx.getBindVariables()[0]);
        assertEquals("bar%", ctx.getBindVariables()[1]);
        assertEquals("baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_NextAnd() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeSuffix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" where").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   /*NEXT 'and '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" and MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertEquals("%foo", ctx.getBindVariables()[0]);
        assertEquals("%bar", ctx.getBindVariables()[1]);
        assertEquals("%baz", ctx.getBindVariables()[2]);
    }

    public void test_accept_LastAnd() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" where").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   /*LAST*/and /*END*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" and MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertEquals("foo%", ctx.getBindVariables()[0]);
        assertEquals("bar%", ctx.getBindVariables()[1]);
        assertEquals("baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_BEGIN_connectorAdjustment_go() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   and MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" and MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertEquals("foo%", ctx.getBindVariables()[0]);
        assertEquals("bar%", ctx.getBindVariables()[1]);
        assertEquals("baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_BEGIN_connectorAdjustment_not() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberId(3);
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   and MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  MEMBER_ID = ?"));
        assertTrue(actual.contains(" and MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertEquals(3, ctx.getBindVariables()[0]);
        assertEquals("foo%", ctx.getBindVariables()[1]);
        assertEquals("bar%", ctx.getBindVariables()[2]);
        assertEquals("baz%", ctx.getBindVariables()[3]);
    }

    public void test_accept_currentParameter_in_IF() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "bazzzz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeContain());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*IF #current == 'foo'*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("     /*END*/");
        sb.append("     /*IF #current == 'bar'*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_ACCOUNT like /*#current*/'foo%'").append(ln());
        sb.append("     /*END*/");
        sb.append("     /*IF #current.length() == 6*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_BAZ like /*#current*/'foo%'").append(ln());
        sb.append("     /*END*/");
        sb.append("     /*IF #current.length() == 99*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_BAZ like /*#current*/'foo%'").append(ln());
        sb.append("     /*END*/");
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_ACCOUNT like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_BAZ like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 1);
        assertTrue(Srl.count(actual, "MEMBER_ACCOUNT") == 1);
        assertTrue(Srl.count(actual, "MEMBER_BAZ") == 1);
        assertTrue(actual.contains(" )"));
        assertEquals("%foo%", ctx.getBindVariables()[0]);
        assertEquals("%bar%", ctx.getBindVariables()[1]);
        assertEquals("%bazzzz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_loopVariable_in_IF() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeContain());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*IF true*//*FIRST*/and (/*END*//*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" )"));
        assertEquals("%foo%", ctx.getBindVariables()[0]);
        assertEquals("%bar%", ctx.getBindVariables()[1]);
        assertEquals("%baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_nested_BEGIN_in_loop() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*BEGIN*/where").append(ln());
        sb.append("     /*IF false*/").append(ln());
        sb.append("     MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("     /*END*/").append(ln());
        sb.append("     /*IF true*/").append(ln());
        sb.append("     or MEMBER_ACCOUNT like /*#current*/'foo%'").append(ln());
        sb.append("     /*END*/").append(ln());
        sb.append("     /*IF true*/").append(ln());
        sb.append("     or MEMBER_ACCOUNT like /*#current*/'foo%'").append(ln());
        sb.append("     /*END*/").append(ln());
        sb.append("     /*END*/").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains(" MEMBER_ACCOUNT like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 0);
        assertTrue(Srl.count(actual, "MEMBER_ACCOUNT") == 6);
        assertEquals(6, ctx.getBindVariables().length);
        assertEquals("foo%", ctx.getBindVariables()[0]);
        assertEquals("foo%", ctx.getBindVariables()[1]);
        assertEquals("bar%", ctx.getBindVariables()[2]);
        assertEquals("bar%", ctx.getBindVariables()[3]);
        assertEquals("baz%", ctx.getBindVariables()[4]);
        assertEquals("baz%", ctx.getBindVariables()[5]);
    }

    public void test_accept_allStars_all_true() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberId(4);
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("fo|o", "ba%r", "b_a|z"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  MEMBER_ID = ?"));
        assertTrue(actual.contains(" and ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" )"));
        assertEquals(4, ctx.getBindVariables()[0]);
        assertEquals("fo||o%", ctx.getBindVariables()[1]);
        assertEquals("ba|%r%", ctx.getBindVariables()[2]);
        assertEquals("b|_a||z%", ctx.getBindVariables()[3]);
    }

    public void test_accept_allStars_connectorAdjustment() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeContain());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" )"));
        assertEquals("%foo%", ctx.getBindVariables()[0]);
        assertEquals("%bar%", ctx.getBindVariables()[1]);
        assertEquals("%baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_allStars_connectorAdjustment_noLn() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER");
        sb.append(" /*BEGIN*/where");
        sb.append("   /*IF pmb.memberId != null*/");
        sb.append("   MEMBER_ID = /*pmb.memberId*/");
        sb.append("   /*END*/");
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/");
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'");
        sb.append("   /*LAST*/)/*END*//*END*/");
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" )"));
        assertEquals("foo%", ctx.getBindVariables()[0]);
        assertEquals("bar%", ctx.getBindVariables()[1]);
        assertEquals("baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_allStars_connectorAdjustment_LAST() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeContain());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*LAST '@'*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains("  @"));
        assertTrue(Srl.count(actual, "@") == 1);
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" )"));
        assertEquals("%foo%", ctx.getBindVariables()[0]);
        assertEquals("%bar%", ctx.getBindVariables()[1]);
        assertEquals("%baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_emptyList() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        List<String> emptyList = DfCollectionUtil.emptyList();
        pmb.setMemberNameList(emptyList);
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER");
        sb.append(" /*BEGIN*/where");
        sb.append("   /*IF pmb.memberId != null*/");
        sb.append("   MEMBER_ID = /*pmb.memberId*/");
        sb.append("   /*END*/");
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/");
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'");
        sb.append("   /*LAST*/)/*END*//*END*/");
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("select * from MEMBER "));
        assertFalse(actual.contains("where"));
        assertFalse(actual.contains("  ("));
        assertFalse(actual.contains("  MEMBER_NAME like ?"));
        assertFalse(actual.contains(" or MEMBER_NAME like ?"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 0);
        assertFalse(actual.contains(" )"));
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_nullList() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(null);
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER");
        sb.append(" /*BEGIN*/where");
        sb.append("   /*IF pmb.memberId != null*/");
        sb.append("   MEMBER_ID = /*pmb.memberId*/");
        sb.append("   /*END*/");
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/");
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'");
        sb.append("   /*LAST*/)/*END*//*END*/");
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("select * from MEMBER "));
        assertFalse(actual.contains("where"));
        assertFalse(actual.contains("  ("));
        assertFalse(actual.contains("  MEMBER_NAME like ?"));
        assertFalse(actual.contains(" or MEMBER_NAME like ?"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 0);
        assertFalse(actual.contains(" )"));
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_nullElement_in_IF() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", null, "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER");
        sb.append(" /*BEGIN*/where");
        sb.append("   /*IF pmb.memberId != null*/");
        sb.append("   MEMBER_ID = /*pmb.memberId*/");
        sb.append("   /*END*/");
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/");
        sb.append("   /*IF #current != null*/");
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'");
        sb.append("   /*END*/");
        sb.append("   /*LAST*/)/*END*//*END*/");
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), true);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("select * from MEMBER "));
        assertTrue(actual.contains("where"));
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like ?"));
        assertTrue(actual.contains(" or MEMBER_NAME like ?"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 2);
        assertTrue(actual.contains(" )"));
        assertEquals(2, ctx.getBindVariables().length);
        assertEquals("foo%", ctx.getBindVariables()[0]);
        assertEquals("baz%", ctx.getBindVariables()[1]);
    }

    public void test_accept_allStars_embedded_either_true() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*$#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like 'foo%' escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like 'bar%' escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like 'baz%' escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" )"));
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_allStars_several_basic() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeContain());
        pmb.setMemberAccountList(DfCollectionUtil.newArrayList("ab%c", "%def"));
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append("   /*FOR pmb.memberAccountList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_ACCOUNT like /*#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" and ("));
        assertTrue(actual.contains("  MEMBER_ACCOUNT like ?"));
        assertTrue(actual.contains(" or MEMBER_ACCOUNT like ?"));
        assertTrue(Srl.count(actual, "MEMBER_ACCOUNT") == 2);
        assertTrue(actual.contains(" )"));
        assertTrue(Srl.count(actual, " )") == 2);
        assertEquals("%foo%", ctx.getBindVariables()[0]);
        assertEquals("%bar%", ctx.getBindVariables()[1]);
        assertEquals("%baz%", ctx.getBindVariables()[2]);
        assertEquals("ab%c", ctx.getBindVariables()[3]);
        assertEquals("%def", ctx.getBindVariables()[4]);
    }

    // ===================================================================================
    //                                                                              Nested
    //                                                                              ======
    public void test_accept_nested_basic() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeContain());
        pmb.setMemberAccountList(DfCollectionUtil.newArrayList("ab%c", "%def"));
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("     /*FOR pmb.memberAccountList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("       /*NEXT 'or '*/MEMBER_ACCOUNT like /*#current*/'foo%'").append(ln());
        sb.append("     /*LAST*/)/*END*//*END*/").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" and ("));
        assertTrue(actual.contains("  MEMBER_ACCOUNT like ?"));
        assertTrue(actual.contains(" or MEMBER_ACCOUNT like ?"));
        assertTrue(Srl.count(actual, "MEMBER_ACCOUNT") == 6);
        assertTrue(actual.contains(" )"));
        assertTrue(Srl.count(actual, " )") == 4);
        assertEquals("%foo%", ctx.getBindVariables()[0]);
        assertEquals("ab%c", ctx.getBindVariables()[1]);
        assertEquals("%def", ctx.getBindVariables()[2]);
        assertEquals("%bar%", ctx.getBindVariables()[3]);
        assertEquals("ab%c", ctx.getBindVariables()[4]);
        assertEquals("%def", ctx.getBindVariables()[5]);
        assertEquals("%baz%", ctx.getBindVariables()[6]);
        assertEquals("ab%c", ctx.getBindVariables()[7]);
        assertEquals("%def", ctx.getBindVariables()[8]);
    }

    public void test_accept_nested_current_in_FOR() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        List<MockPmb> nestPmbList = DfCollectionUtil.newArrayList();
        {
            MockPmb element = new MockPmb();
            element.setMemberId(3);
            element.setMemberNameList(DfCollectionUtil.newArrayList("fo%o", "b|ar"));
            element.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
            nestPmbList.add(element);
        }
        {
            MockPmb element = new MockPmb();
            element.setMemberId(4);
            element.setMemberNameList(DfCollectionUtil.newArrayList("ba%z", "qu_x"));
            //element.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
            nestPmbList.add(element);
        }
        pmb.setNestPmbList(nestPmbList);
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.nestPmbList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_ID = /*#current.memberId*/99").append(ln());
        sb.append("     /*FOR #current.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("       /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("     /*LAST*/)/*END*//*END*/").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_ID = ?"));
        assertTrue(actual.contains("  MEMBER_ID = ?"));
        assertTrue(actual.contains("  MEMBER_NAME like ?"));
        assertTrue(actual.contains(" or MEMBER_NAME like ?"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 4);
        assertTrue(actual.contains(" and ("));
        assertTrue(Srl.count(actual, " and (") == 2);
        assertTrue(actual.contains(" )"));
        assertTrue(Srl.count(actual, " )") == 3);
        assertEquals(3, ctx.getBindVariables()[0]);
        assertEquals("fo|%o%", ctx.getBindVariables()[1]);
        assertEquals("b||ar%", ctx.getBindVariables()[2]);
        assertEquals(4, ctx.getBindVariables()[3]);
        assertEquals("ba%z", ctx.getBindVariables()[4]);
        assertEquals("qu_x", ctx.getBindVariables()[5]);
    }

    public void test_accept_nested_current_in_FOR_nullElement() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        List<MockPmb> nestPmbList = DfCollectionUtil.newArrayList();
        {
            MockPmb element = new MockPmb();
            element.setMemberId(3);
            element.setMemberNameList(DfCollectionUtil.newArrayList("fo%o", "b|ar"));
            element.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
            nestPmbList.add(element);
        }
        nestPmbList.add(null);
        {
            MockPmb element = new MockPmb();
            element.setMemberId(4);
            element.setMemberNameList(DfCollectionUtil.newArrayList("ba%z", "qu_x"));
            //element.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
            nestPmbList.add(element);
        }
        pmb.setNestPmbList(nestPmbList);
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.nestPmbList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*FOR #current.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("       /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("     /*LAST*/)/*END*//*END*/").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), true);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like ?"));
        assertTrue(actual.contains(" or MEMBER_NAME like ?"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 4);
        assertTrue(actual.contains(" and ("));
        assertTrue(Srl.count(actual, " and (") == 2);
        assertTrue(actual.contains(" )"));
        assertTrue(Srl.count(actual, " )") == 3);
        assertEquals("fo|%o%", ctx.getBindVariables()[0]);
        assertEquals("b||ar%", ctx.getBindVariables()[1]);
        assertEquals("ba%z", ctx.getBindVariables()[2]);
        assertEquals("qu_x", ctx.getBindVariables()[3]);
    }

    public void test_accept_nested_parent_in_FOR() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberName("parent"); // unused
        List<MockPmb> nestPmbList = DfCollectionUtil.newArrayList();
        {
            MockPmb element = new MockPmb();
            element.setMemberId(3);
            element.setMemberName("foo");
            element.setMemberStatusCode("FML");
            List<MockPmb> nestNestPmbList = DfCollectionUtil.newArrayList();
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("baz");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("baz");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            element.setNestPmbList(nestNestPmbList);
            nestPmbList.add(element);
        }
        {
            MockPmb element = new MockPmb();
            element.setMemberId(4);
            element.setMemberName("bar");
            element.setMemberStatusCode("WDL");
            List<MockPmb> nestNestPmbList = DfCollectionUtil.newArrayList();
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("qux");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("qux");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            element.setNestPmbList(nestNestPmbList);
            nestPmbList.add(element);
        }
        pmb.setNestPmbList(nestPmbList);
        pmb.setNestPmbListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.nestPmbList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_STATUS_CODE = /*#current.memberStatusCode*/'test'").append(ln());
        sb.append("     /*FOR #current.nestPmbList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("       /*NEXT 'or '*/MEMBER_NAME like /*#current.parentPmb.memberName*/'foo%'").append(ln());
        sb.append("     /*LAST*/)/*END*//*END*/").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_STATUS_CODE = ? escape '|'"));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 4);
        assertTrue(actual.contains(" and ("));
        assertTrue(Srl.count(actual, " and (") == 2);
        assertTrue(actual.contains(" )"));
        assertTrue(Srl.count(actual, " )") == 3);
        assertEquals("FML%", ctx.getBindVariables()[0]); // because of not using notLike option
        assertEquals("foo%", ctx.getBindVariables()[1]);
        assertEquals("foo%", ctx.getBindVariables()[2]);
        assertEquals("WDL%", ctx.getBindVariables()[3]); // because of not using notLike option
        assertEquals("bar%", ctx.getBindVariables()[4]);
        assertEquals("bar%", ctx.getBindVariables()[5]);
    }

    public void test_accept_nested_inLoopOption_notLike_bind_in_FOR() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberName("parent"); // unused
        List<MockPmb> nestPmbList = DfCollectionUtil.newArrayList();
        {
            MockPmb element = new MockPmb();
            element.setMemberId(3);
            element.setMemberName("fo%o");
            element.setMemberStatusCode("FML");
            List<MockPmb> nestNestPmbList = DfCollectionUtil.newArrayList();
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("baz");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("baz");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            element.setNestPmbList(nestNestPmbList);
            nestPmbList.add(element);
        }
        {
            MockPmb element = new MockPmb();
            element.setMemberId(4);
            element.setMemberName("b|ar");
            element.setMemberStatusCode("WDL");
            List<MockPmb> nestNestPmbList = DfCollectionUtil.newArrayList();
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("qux");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("qux");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            element.setNestPmbList(nestNestPmbList);
            nestPmbList.add(element);
        }
        pmb.setNestPmbList(nestPmbList);
        pmb.setNestPmbListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.nestPmbList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_STATUS_CODE = /*#current.memberStatusCode:notLike*/'test'").append(ln());
        sb.append("     /*FOR #current.nestPmbList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("       /*NEXT 'or '*/MEMBER_NAME like /*#current.parentPmb.memberName*/'foo%'").append(ln());
        sb.append("     /*LAST*/)/*END*//*END*/").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_STATUS_CODE = ?\n"));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 4);
        assertTrue(actual.contains(" and ("));
        assertTrue(Srl.count(actual, " and (") == 2);
        assertTrue(actual.contains(" )"));
        assertTrue(Srl.count(actual, " )") == 3);
        assertEquals("FML", ctx.getBindVariables()[0]);
        assertEquals("fo|%o%", ctx.getBindVariables()[1]);
        assertEquals("fo|%o%", ctx.getBindVariables()[2]);
        assertEquals("WDL", ctx.getBindVariables()[3]);
        assertEquals("b||ar%", ctx.getBindVariables()[4]);
        assertEquals("b||ar%", ctx.getBindVariables()[5]);
    }

    public void test_accept_nested_inLoopOption_likeContain_bind_in_FOR() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberName("parent"); // unused
        List<MockPmb> nestPmbList = DfCollectionUtil.newArrayList();
        {
            MockPmb element = new MockPmb();
            element.setMemberId(3);
            element.setMemberName("fo%o");
            element.setMemberStatusCode("FML");
            List<MockPmb> nestNestPmbList = DfCollectionUtil.newArrayList();
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("baz");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("baz");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            element.setNestPmbList(nestNestPmbList);
            nestPmbList.add(element);
        }
        {
            MockPmb element = new MockPmb();
            element.setMemberId(4);
            element.setMemberName("b|ar");
            element.setMemberStatusCode("WDL");
            List<MockPmb> nestNestPmbList = DfCollectionUtil.newArrayList();
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("qux");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("qux");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            element.setNestPmbList(nestNestPmbList);
            nestPmbList.add(element);
        }
        pmb.setNestPmbList(nestPmbList);
        pmb.setNestPmbListInternalLikeSearchOption(new LikeSearchOption().likePrefix()); // overridden
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.nestPmbList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_STATUS_CODE = /*#current.memberStatusCode:likeSuffix*/'test'")
                .append(ln());
        sb.append("     /*FOR #current.nestPmbList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("       /*NEXT 'or '*/MEMBER_NAME like /*#current.parentPmb.memberName:likeContain*/'foo%'").append(
                ln());
        sb.append("     /*LAST*/)/*END*//*END*/").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_STATUS_CODE = ? escape '|'"));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 4);
        assertTrue(actual.contains(" and ("));
        assertTrue(Srl.count(actual, " and (") == 2);
        assertTrue(actual.contains(" )"));
        assertTrue(Srl.count(actual, " )") == 3);
        assertEquals("%FML", ctx.getBindVariables()[0]);
        assertEquals("%fo|%o%", ctx.getBindVariables()[1]);
        assertEquals("%fo|%o%", ctx.getBindVariables()[2]);
        assertEquals("%WDL", ctx.getBindVariables()[3]);
        assertEquals("%b||ar%", ctx.getBindVariables()[4]);
        assertEquals("%b||ar%", ctx.getBindVariables()[5]);
    }

    public void test_accept_nested_notLike_embedded_in_FOR() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberName("parent"); // unused
        List<MockPmb> nestPmbList = DfCollectionUtil.newArrayList();
        {
            MockPmb element = new MockPmb();
            element.setMemberId(3);
            element.setMemberName("fo%o");
            element.setMemberStatusCode("FML");
            List<MockPmb> nestNestPmbList = DfCollectionUtil.newArrayList();
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("baz");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("baz");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            element.setNestPmbList(nestNestPmbList);
            nestPmbList.add(element);
        }
        {
            MockPmb element = new MockPmb();
            element.setMemberId(4);
            element.setMemberName("b|ar");
            element.setMemberStatusCode("WDL");
            List<MockPmb> nestNestPmbList = DfCollectionUtil.newArrayList();
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("qux");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("qux");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            element.setNestPmbList(nestNestPmbList);
            nestPmbList.add(element);
        }
        pmb.setNestPmbList(nestPmbList);
        pmb.setNestPmbListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.nestPmbList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_STATUS_CODE = /*$#current.memberStatusCode:notLike*/'test'").append(ln());
        sb.append("     /*FOR #current.nestPmbList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("       /*NEXT 'or '*/MEMBER_NAME like /*$#current.parentPmb.memberName*/'foo%'").append(ln());
        sb.append("     /*LAST*/)/*END*//*END*/").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_STATUS_CODE = 'FML'\n"));
        assertTrue(actual.contains("  MEMBER_NAME like 'fo|%o%' escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like 'b||ar%' escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 4);
        assertTrue(actual.contains(" and ("));
        assertTrue(Srl.count(actual, " and (") == 2);
        assertTrue(actual.contains(" )"));
        assertTrue(Srl.count(actual, " )") == 3);
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_nested_bind_overrideOption_in_FOR() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberName("parent"); // unused
        List<MockPmb> nestPmbList = DfCollectionUtil.newArrayList();
        {
            MockPmb element = new MockPmb();
            element.setMemberId(3);
            element.setMemberName("fo%o");
            element.setMemberStatusCode("FML");
            List<MockPmb> nestNestPmbList = DfCollectionUtil.newArrayList();
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("baz");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("baz");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            element.setNestPmbList(nestNestPmbList);
            nestPmbList.add(element);
        }
        {
            MockPmb element = new MockPmb();
            element.setMemberId(4);
            element.setMemberName("b|ar");
            // override
            element.setMemberNameInternalLikeSearchOption(new LikeSearchOption().likeContain());
            element.setMemberStatusCode("WDL");
            List<MockPmb> nestNestPmbList = DfCollectionUtil.newArrayList();
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("qux");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            {
                MockPmb nestElement = new MockPmb();
                nestElement.setMemberName("qux");
                nestElement.setParentPmb(element);
                nestNestPmbList.add(nestElement);
            }
            element.setNestPmbList(nestNestPmbList);
            nestPmbList.add(element);
        }
        pmb.setNestPmbList(nestPmbList);
        pmb.setNestPmbListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.nestPmbList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_STATUS_CODE = /*#current.memberStatusCode:notLike*/'test'").append(ln());
        sb.append("     /*FOR #current.nestPmbList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("       /*NEXT 'or '*/MEMBER_NAME like /*#current.parentPmb.memberName*/'foo%'").append(ln());
        sb.append("     /*LAST*/)/*END*//*END*/").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_STATUS_CODE = ?\n"));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 4);
        assertTrue(actual.contains(" and ("));
        assertTrue(Srl.count(actual, " and (") == 2);
        assertTrue(actual.contains(" )"));
        assertTrue(Srl.count(actual, " )") == 3);
        assertEquals("FML", ctx.getBindVariables()[0]);
        assertEquals("fo|%o%", ctx.getBindVariables()[1]);
        assertEquals("fo|%o%", ctx.getBindVariables()[2]);
        assertEquals("WDL", ctx.getBindVariables()[3]);
        assertEquals("%b||ar%", ctx.getBindVariables()[4]); // override
        assertEquals("%b||ar%", ctx.getBindVariables()[5]); // override
    }

    public void test_accept_nested_bind_notInheritOption_in_FOR() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberId(3);
        pmb.setMemberName("qux");
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeContain());
        pmb.setMemberAccountList(DfCollectionUtil.newArrayList("ab%c", "%def"));
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("     or MEMBER_ACCOUNT like /*pmb.memberName*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains(" and ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" or MEMBER_ACCOUNT like ?"));
        assertTrue(Srl.count(actual, "MEMBER_ACCOUNT") == 3);
        assertTrue(actual.contains(" )"));
        assertEquals(3, ctx.getBindVariables()[0]);
        assertEquals("%foo%", ctx.getBindVariables()[1]);
        assertEquals("qux", ctx.getBindVariables()[2]);
        assertEquals("%bar%", ctx.getBindVariables()[3]);
        assertEquals("qux", ctx.getBindVariables()[4]);
        assertEquals("%baz%", ctx.getBindVariables()[5]);
        assertEquals("qux", ctx.getBindVariables()[6]);
    }

    // ===================================================================================
    //                                                                           Exception
    //                                                                           =========
    public void test_accept_endNotFound() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeContain());
        pmb.setMemberAccountList(DfCollectionUtil.newArrayList("ab%c", "%def"));
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*/").append(ln());
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);

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

    public void test_accept_FIRST_endNotFound() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeContain());
        pmb.setMemberAccountList(DfCollectionUtil.newArrayList("ab%c", "%def"));
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);

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

    public void test_accept_currentParameter_in_Bind_null_allowed() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberId(3);
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", null, "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   and MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx); // expect no exception

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(Srl.count(actual, "and MEMBER_NAME like ? escape '|' ") == 2);
        assertTrue(Srl.count(actual, "and MEMBER_NAME like ?\n") == 1);
        assertEquals(4, ctx.getBindVariables().length);
        assertEquals(3, ctx.getBindVariables()[0]);
        assertEquals("foo%", ctx.getBindVariables()[1]);
        assertEquals(null, ctx.getBindVariables()[2]);
        assertEquals("baz%", ctx.getBindVariables()[3]);
    }

    public void test_accept_currentParameter_in_Bind_null_notAllowed() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberId(3);
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", null, "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   and MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), true);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        try {
            rootNode.accept(ctx);

            // ## Assert ##
            fail();
        } catch (ForCommentParameterNullElementException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_accept_currentParameter_outOfForComment() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberId(3);
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*#current*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   and MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        try {
            rootNode.accept(ctx);

            // ## Assert ##
            fail();
        } catch (LoopCurrentVariableOutOfForCommentException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_accept_FOR_spellMiss() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberId(3);
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FAR pmb.memberNameList*/").append(ln());
        sb.append("   and MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        try {
            rootNode.accept(ctx);

            // ## Assert ##
            fail();
        } catch (BindVariableCommentIllegalParameterBeanSpecificationException e) {
            // OK
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    protected static class MockPmb implements ParameterBean {
        protected Integer _memberId;
        protected String _memberName;
        protected String _memberStatusCode;
        protected LikeSearchOption _memberNameInternalLikeSearchOption;
        protected List<String> _memberNameList;
        protected LikeSearchOption _memberNameListInternalLikeSearchOption;
        protected List<String> _memberAccountList;
        protected MockPmb _nestPmb;
        protected MockPmb _nestLikePmb;
        protected LikeSearchOption _nestLikePmbInternalLikeSearchOption;
        protected List<MockPmb> _nestPmbList;
        protected LikeSearchOption _nestPmbListInternalLikeSearchOption;
        protected MockPmb _parentPmb;

        public Integer getMemberId() {
            return _memberId;
        }

        public void setMemberId(Integer memberId) {
            this._memberId = memberId;
        }

        public String getMemberName() {
            return _memberName;
        }

        public void setMemberName(String memberName) {
            this._memberName = memberName;
        }

        public LikeSearchOption getMemberNameInternalLikeSearchOption() {
            return _memberNameInternalLikeSearchOption;
        }

        public void setMemberNameInternalLikeSearchOption(LikeSearchOption memberNameInternalLikeSearchOption) {
            this._memberNameInternalLikeSearchOption = memberNameInternalLikeSearchOption;
        }

        public String getMemberStatusCode() {
            return _memberStatusCode;
        }

        public void setMemberStatusCode(String memberStatusCode) {
            this._memberStatusCode = memberStatusCode;
        }

        public List<String> getMemberNameList() {
            return _memberNameList;
        }

        public void setMemberNameList(List<String> memberNameList) {
            this._memberNameList = memberNameList;
        }

        public LikeSearchOption getMemberNameListInternalLikeSearchOption() {
            return _memberNameListInternalLikeSearchOption;
        }

        public void setMemberNameListInternalLikeSearchOption(LikeSearchOption memberNameListInternalLikeSearchOption) {
            this._memberNameListInternalLikeSearchOption = memberNameListInternalLikeSearchOption;
        }

        public List<String> getMemberAccountList() {
            return _memberAccountList;
        }

        public void setMemberAccountList(List<String> memberAccountList) {
            this._memberAccountList = memberAccountList;
        }

        public MockPmb getNestPmb() {
            return _nestPmb;
        }

        public void setNestPmb(MockPmb nestPmb) {
            this._nestPmb = nestPmb;
        }

        public MockPmb getNestLikePmb() {
            return _nestLikePmb;
        }

        public void setNestLikePmb(MockPmb nestLikePmb) {
            this._nestLikePmb = nestLikePmb;
        }

        public LikeSearchOption getNestLikePmbInternalLikeSearchOption() {
            return _nestLikePmbInternalLikeSearchOption;
        }

        public void setNestLikePmbInternalLikeSearchOption(LikeSearchOption nestLikePmbInternalLikeSearchOption) {
            this._nestLikePmbInternalLikeSearchOption = nestLikePmbInternalLikeSearchOption;
        }

        public List<MockPmb> getNestPmbList() {
            return _nestPmbList;
        }

        public void setNestPmbList(List<MockPmb> nestPmbList) {
            this._nestPmbList = nestPmbList;
        }

        public LikeSearchOption getNestPmbListInternalLikeSearchOption() {
            return _nestPmbListInternalLikeSearchOption;
        }

        public void setNestPmbListInternalLikeSearchOption(LikeSearchOption nestPmbListInternalLikeSearchOption) {
            this._nestPmbListInternalLikeSearchOption = nestPmbListInternalLikeSearchOption;
        }

        public MockPmb getParentPmb() {
            return _parentPmb;
        }

        public void setParentPmb(MockPmb parentPmb) {
            this._parentPmb = parentPmb;
        }
    }

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
