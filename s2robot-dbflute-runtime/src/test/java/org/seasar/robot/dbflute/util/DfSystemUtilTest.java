package org.seasar.robot.dbflute.util;

import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.7.0 (2010/05/29 Saturday)
 */
public class DfSystemUtilTest extends PlainTestCase {

    public void test_getLineSeparator() {
        assertEquals("\n", DfSystemUtil.getLineSeparator());
    }

    public void test_currentTimeMillis() {
        long millis = DfSystemUtil.currentTimeMillis(); // except no exception
        log(millis + " : " + DfTypeUtil.toTimestamp(millis));
    }

    public void test_arraycopy_basic() {
        // ## Arrange ##
        int[] src = new int[] { 1, 3, 5, 7, 9 };
        int[] dest = new int[3];

        // ## Act ##
        DfSystemUtil.arraycopy(src, 1, dest, 1, 2);

        // ## Assert ##
        assertEquals(0, dest[0]);
        assertEquals(3, dest[1]);
        assertEquals(5, dest[2]);
    }
}
