package org.seasar.robot.dbflute.util;

import junit.framework.TestCase;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/06/20 Saturday)
 */
public class TraceViewUtilTest extends TestCase {

    public void test_convertObjectArrayToStringView() throws Exception {
        // ## Arrange ##
        String actual = DfTraceViewUtil.convertObjectArrayToStringView(new String[] { "aaa", "bbb", "ccc" });

        // ## Act & Assert ##
        assertEquals("aaa, bbb, ccc", actual);
    }
}
