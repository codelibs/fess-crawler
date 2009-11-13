package org.seasar.robot.dbflute.helper.token.line.impl;

import java.util.ArrayList;
import java.util.List;

import org.seasar.robot.dbflute.helper.token.line.LineMakingOption;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.4 (2009/03/14 Saturday)
 */
public class LineTokenImplTest extends PlainTestCase {

    public void test_make_quoteAll_basic() {
        // ## Arrange ##
        LineTokenImpl impl = new LineTokenImpl();
        LineMakingOption option = new LineMakingOption();
        option.delimitateByComma();
        option.quoteAll();
        List<String> valueList = new ArrayList<String>();
        valueList.add("a");
        valueList.add("b");
        valueList.add("cc");
        valueList.add("d");
        valueList.add("e");

        // ## Act ##
        String line = impl.make(valueList, option);

        // ## Assert ##
        log(line);
        assertEquals("\"a\",\"b\",\"cc\",\"d\",\"e\"", line);
    }

    public void test_make_quoteAll_escape() {
        // ## Arrange ##
        LineTokenImpl impl = new LineTokenImpl();
        LineMakingOption option = new LineMakingOption();
        option.delimitateByComma();
        option.quoteAll();
        List<String> valueList = new ArrayList<String>();
        valueList.add("a");
        valueList.add("b");
        valueList.add("c\"c");
        valueList.add("d");
        valueList.add("e");

        // ## Act ##
        String line = impl.make(valueList, option);

        // ## Assert ##
        log(line);
        assertEquals("\"a\",\"b\",\"c\"\"c\",\"d\",\"e\"", line);
    }

    public void test_make_quoteMinimally_escape() {
        // ## Arrange ##
        LineTokenImpl impl = new LineTokenImpl();
        LineMakingOption option = new LineMakingOption();
        option.delimitateByComma();
        option.quoteMinimally();
        List<String> valueList = new ArrayList<String>();
        valueList.add("a");
        valueList.add("b");
        valueList.add("c\"c");
        valueList.add("d,d");
        valueList.add("e");

        // ## Act ##
        String line = impl.make(valueList, option);

        // ## Assert ##
        log(line);
        assertEquals("a,b,\"c\"\"c\",\"d,d\",e", line);
    }
}
