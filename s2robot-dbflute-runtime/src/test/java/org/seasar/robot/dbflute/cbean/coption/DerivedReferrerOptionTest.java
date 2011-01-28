package org.seasar.robot.dbflute.cbean.coption;

import org.seasar.robot.dbflute.cbean.sqlclause.subquery.SubQueryIndentProcessor;
import org.seasar.robot.dbflute.unit.PlainTestCase;
import org.seasar.robot.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.7 (2010/12/04 Saturday)
 */
public class DerivedReferrerOptionTest extends PlainTestCase {

    public void test_processSimpleFunction_basic() throws Exception {
        // ## Arrange ##
        DerivedReferrerOption option = new DerivedReferrerOption();
        option.acceptParameterKey("key", "path");

        // ## Act ##
        String actual = option.processSimpleFunction("max(foo.COL)", "value", "func", "bar", null, false);

        // ## Assert ##
        log(actual);
        assertTrue(Srl.startsWith(actual, "func(max(foo.COL), "));
        assertTrue(Srl.endsWith(actual, "/*pmb.path.key.bar*/null)"));
    }

    public void test_processSimpleFunction_thirdArg() throws Exception {
        // ## Arrange ##
        DerivedReferrerOption option = new DerivedReferrerOption();
        option.acceptParameterKey("key", "path");

        // ## Act ##
        String actual = option.processSimpleFunction("max(foo.COL)", "value", "func", "bar", "third", false);

        // ## Assert ##
        log(actual);
        assertTrue(Srl.startsWith(actual, "func(max(foo.COL), "));
        assertTrue(Srl.endsWith(actual, "/*pmb.path.key.bar*/null, third)"));
    }

    public void test_processSimpleFunction_leftArg() throws Exception {
        // ## Arrange ##
        DerivedReferrerOption option = new DerivedReferrerOption();
        option.acceptParameterKey("key", "path");

        // ## Act ##
        String actual = option.processSimpleFunction("max(foo.COL)", "value", "func", "bar", "third", true);

        // ## Assert ##
        log(actual);
        assertTrue(Srl.startsWith(actual, "func(/*pmb.path.key.bar*/null\n"));
        assertTrue(Srl.endsWith(actual, "    , max(foo.COL), third)"));
    }

    public void test_processSimpleFunction_nested_basic() throws Exception {
        // ## Arrange ##
        DerivedReferrerOption option = new DerivedReferrerOption();
        option.acceptParameterKey("key", "path");
        String sqbegin = SubQueryIndentProcessor.BEGIN_MARK_PREFIX;
        String sqend = SubQueryIndentProcessor.END_MARK_PREFIX;
        String identity = "identity";
        StringBuilder sb = new StringBuilder();
        sb.append("max(").append(sqbegin).append(identity);
        sb.append(ln()).append("select max(foo.COL)");
        sb.append(ln()).append("  from FOO foo");
        sb.append(ln()).append(")").append(sqend).append(identity);

        // ## Act ##
        String actual = option.processSimpleFunction(sb.toString(), "value", "func", "bar", null, false);

        // ## Assert ##
        log(ln() + actual);
        assertTrue(Srl.startsWith(actual, "func(max(" + sqbegin + identity));
        assertTrue(Srl.contains(actual, "select max(foo.COL)"));
        assertTrue(Srl.contains(actual, "  from FOO foo"));
        assertTrue(Srl.endsWith(actual, ", /*pmb.path.key.bar*/null)" + sqend + identity));
    }

    public void test_processSimpleFunction_nested_third() throws Exception {
        // ## Arrange ##
        DerivedReferrerOption option = new DerivedReferrerOption();
        option.acceptParameterKey("key", "path");
        String sqbegin = SubQueryIndentProcessor.BEGIN_MARK_PREFIX;
        String sqend = SubQueryIndentProcessor.END_MARK_PREFIX;
        String identity = "identity";
        StringBuilder sb = new StringBuilder();
        sb.append("max(").append(sqbegin).append(identity);
        sb.append(ln()).append("select max(foo.COL)");
        sb.append(ln()).append("  from FOO foo");
        sb.append(ln()).append(")").append(sqend).append(identity);

        // ## Act ##
        String actual = option.processSimpleFunction(sb.toString(), "value", "func", "bar", "third", false);

        // ## Assert ##
        log(ln() + actual);
        assertTrue(Srl.startsWith(actual, "func(max(" + sqbegin + identity));
        assertTrue(Srl.contains(actual, "select max(foo.COL)"));
        assertTrue(Srl.contains(actual, "  from FOO foo"));
        assertTrue(Srl.endsWith(actual, ", /*pmb.path.key.bar*/null, third)" + sqend + identity));
    }

    public void test_processSimpleFunction_nested_leftArg() throws Exception {
        // ## Arrange ##
        DerivedReferrerOption option = new DerivedReferrerOption();
        option.acceptParameterKey("key", "path");
        String sqbegin = SubQueryIndentProcessor.BEGIN_MARK_PREFIX;
        String sqend = SubQueryIndentProcessor.END_MARK_PREFIX;
        String identity = "identity";
        StringBuilder sb = new StringBuilder();
        sb.append("max(").append(sqbegin).append(identity);
        sb.append(ln()).append("select max(foo.COL)");
        sb.append(ln()).append("  from FOO foo");
        sb.append(ln()).append(")").append(sqend).append(identity);

        // ## Act ##
        String actual = option.processSimpleFunction(sb.toString(), "value", "func", "bar", "third", true);

        // ## Assert ##
        log(ln() + actual);
        assertTrue(Srl.startsWith(actual, "func(/*pmb.path.key.bar*/null\n"));
        assertTrue(Srl.contains(actual, "    , max(" + sqbegin + identity));
        assertTrue(Srl.contains(actual, "select max(foo.COL)"));
        assertTrue(Srl.contains(actual, "  from FOO foo"));
        assertTrue(Srl.endsWith(actual, "), third)" + sqend + identity));
    }

    public void test_processSimpleFunction_nested_nested() throws Exception {
        // ## Arrange ##
        DerivedReferrerOption option = new DerivedReferrerOption();
        option.acceptParameterKey("key", "path");
        String sqbegin = SubQueryIndentProcessor.BEGIN_MARK_PREFIX;
        String sqend = SubQueryIndentProcessor.END_MARK_PREFIX;
        String identity = "identity";
        StringBuilder sb = new StringBuilder();
        sb.append("max(").append(sqbegin).append(identity);
        sb.append(ln()).append("select max(").append(sqbegin).append(identity);
        sb.append(ln()).append("select max(foo.COL)");
        sb.append(ln()).append("  from FOO foo");
        sb.append(ln()).append(")").append(sqend).append(identity);
        sb.append(ln()).append("  from FOO foo");
        sb.append(ln()).append(")").append(sqend).append(identity);

        // ## Act ##
        String actual = option.processSimpleFunction(sb.toString(), "value", "func", "bar", null, false);

        // ## Assert ##
        log(ln() + actual);
        assertTrue(Srl.startsWith(actual, "func(max(" + sqbegin + identity));
        assertTrue(Srl.contains(actual, "select max(foo.COL)"));
        assertTrue(Srl.contains(actual, "  from FOO foo"));

        assertEquals(2, Srl.count(actual, "max(" + sqbegin + identity));
        assertEquals(1, Srl.count(actual, "func(max(" + sqbegin + identity));
        assertEquals(2, Srl.count(actual, ")" + sqend + identity));
        assertEquals(1, Srl.count(actual, "/*pmb.path.key.bar*/null)" + sqend + identity));

        assertTrue(Srl.endsWith(actual, ", /*pmb.path.key.bar*/null)" + sqend + identity));
    }

    public void test_needsHandleSubQueryEnd_basic() throws Exception {
        // ## Arrange ##
        DerivedReferrerOption option = new DerivedReferrerOption();
        option.acceptParameterKey("key", "path");
        String sqend = SubQueryIndentProcessor.END_MARK_PREFIX;

        // ## Act & Assert ##
        assertFalse(option.hasSubQueryEndOnLastLine("FOO"));
        assertFalse(option.hasSubQueryEndOnLastLine("FOO" + ln() + "BAR"));
        assertTrue(option.hasSubQueryEndOnLastLine("FOO" + ln() + "BAR" + sqend));
        assertFalse(option.hasSubQueryEndOnLastLine("FOO" + sqend + ln() + "BAR"));
        assertFalse(option.hasSubQueryEndOnLastLine("FOO" + sqend));
    }
}
