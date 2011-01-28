package org.seasar.robot.dbflute.s2dao.valuetype.basic;

import java.util.Date;

import junit.framework.TestCase;

import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 * @since 0.9.6.3 (2009/12/15 Tuesday)
 */
public class UtilDateAsSqlDateTypeTest extends TestCase {

    public void test_toUtilDate() {
        // ## Arrange ##
        UtilDateAsSqlDateType type = new UtilDateAsSqlDateType();
        java.sql.Date sqlDate = DfTypeUtil.toSqlDate(DfTypeUtil.toDate("2009/12/13"));

        // ## Act ##
        java.util.Date utilDate = type.toUtilDate(sqlDate);

        // ## Assert ##
        assertFalse(utilDate instanceof java.sql.Date);
        assertEquals("2009/12/13", DfTypeUtil.toString(utilDate, "yyyy/MM/dd"));
    }

    public void test_toSqlDate() {
        // ## Arrange ##
        UtilDateAsSqlDateType type = new UtilDateAsSqlDateType();
        Date utilDate = DfTypeUtil.toDate("2009/12/13");

        // ## Act ##
        java.sql.Date sqlDate = type.toSqlDate(utilDate);

        // ## Assert ##
        assertEquals("2009/12/13", DfTypeUtil.toString(sqlDate, "yyyy/MM/dd"));
    }
}
