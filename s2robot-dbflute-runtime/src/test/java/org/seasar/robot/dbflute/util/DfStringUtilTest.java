package org.seasar.robot.dbflute.util;

import java.util.List;

import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/10 Friday)
 */
public class DfStringUtilTest extends PlainTestCase {

    public void test_splitList() {
        String ln = DfSystemUtil.getLineSeparator();
        List<String> splitList = DfStringUtil.splitList("aaa" + ln + "bbb" + ln + "ccc", ln);
        assertEquals("aaa", splitList.get(0));
        assertEquals("bbb", splitList.get(1));
        assertEquals("ccc", splitList.get(2));
    }

    public void test_rtrim_default() {
        assertNull(DfStringUtil.rtrim(null));
        assertEquals(" foo", DfStringUtil.rtrim(" foo "));
        assertEquals(" foo", DfStringUtil.rtrim(" foo \n "));
        assertEquals(" foo", DfStringUtil.rtrim(" foo \n \n"));
        assertEquals(" foo", DfStringUtil.rtrim(" foo \r\n "));
        assertEquals(" foo", DfStringUtil.rtrim(" foo \r\n \r\n"));
    }

    public void test_rtrim_originalTrimTarget() {
        assertNull(DfStringUtil.rtrim(null, "\n"));
        assertEquals(" foo ", DfStringUtil.rtrim(" foo \n", "\n"));
        assertEquals(" foo \n ", DfStringUtil.rtrim(" foo \n ", "\n"));
        assertEquals(" foo \r", DfStringUtil.rtrim(" foo \r\n", "\n"));
        assertEquals(" foo ", DfStringUtil.rtrim(" foo \r\n", "\r\n"));
    }

    public void test_extractFirstScope_basic() {
        assertEquals("BAR", DfStringUtil.extractFirstScope("FOObeginBARendDODO", "begin", "end"));
        assertEquals("BAR", DfStringUtil.extractFirstScope("FOObeginBARend", "begin", "end"));
        assertEquals("BAR", DfStringUtil.extractFirstScope("beginBARendDODO", "begin", "end"));
        assertEquals(null, DfStringUtil.extractFirstScope("beginBARedDODO", "begin", "end"));
        assertEquals(null, DfStringUtil.extractFirstScope("begnBARendDODO", "begin", "end"));
        assertEquals(null, DfStringUtil.extractFirstScope("begnBARedDODO", "begin", "end"));
    }
}
