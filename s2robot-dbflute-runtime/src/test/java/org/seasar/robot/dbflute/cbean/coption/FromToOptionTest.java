package org.seasar.robot.dbflute.cbean.coption;

import java.util.Date;

import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;
import org.seasar.robot.dbflute.unit.PlainTestCase;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public class FromToOptionTest extends PlainTestCase {

    // ===================================================================================
    //                                                                  Comparison Pattern
    //                                                                  ==================
    public void test_compareAsDate_basic() {
        // ## Arrange ##
        String fromRes = "2008-12-14 12:34:56";
        String toRes = "2008-12-18 18:34:56";
        FromToOption option = createOption();

        // ## Act ##
        option.compareAsDate();
        Date fromDate = option.filterFromDate(DfTypeUtil.toDate(fromRes));
        Date toDate = option.filterToDate(DfTypeUtil.toDate(toRes));

        // ## Assert ##
        assertEquals(ConditionKey.CK_GREATER_EQUAL, option.getFromDateConditionKey());
        assertEquals(ConditionKey.CK_LESS_THAN, option.getToDateConditionKey());
        assertEquals("2008-12-14 00:00:00", DfTypeUtil.toString(fromDate, "yyyy-MM-dd HH:mm:ss"));
        assertEquals("2008-12-19 00:00:00", DfTypeUtil.toString(toDate, "yyyy-MM-dd HH:mm:ss"));
    }

    public void test_compareAsDate_with_greaterThan() {
        // ## Arrange ##
        FromToOption option = createOption();

        // ## Act ##
        option.compareAsDate();
        try {
            option.greaterThan();

            // ## Assert ##
            fail();
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_compareAsDate_with_noon() {
        // ## Arrange ##
        String fromRes = "2008-12-14 12:34:56";
        String toRes = "2008-12-18 18:34:56";
        FromToOption option = createOption();

        // ## Act ##
        option.compareAsDate();
        option.fromDateWithNoon();
        option.toDateWithNoon();
        Date fromDate = option.filterFromDate(DfTypeUtil.toDate(fromRes));
        Date toDate = option.filterToDate(DfTypeUtil.toDate(toRes));

        // ## Assert ##
        assertEquals(ConditionKey.CK_GREATER_EQUAL, option.getFromDateConditionKey());
        assertEquals(ConditionKey.CK_LESS_THAN, option.getToDateConditionKey());
        assertEquals("2008-12-14 12:00:00", DfTypeUtil.toString(fromDate, "yyyy-MM-dd HH:mm:ss"));
        assertEquals("2008-12-19 12:00:00", DfTypeUtil.toString(toDate, "yyyy-MM-dd HH:mm:ss"));
    }

    public void test_compareAsMonth_basic() {
        // ## Arrange ##
        String fromRes = "2008-09-14 12:34:56";
        String toRes = "2008-11-18 18:34:56";
        FromToOption option = createOption();

        // ## Act ##
        option.compareAsMonth();
        Date fromDate = option.filterFromDate(DfTypeUtil.toDate(fromRes));
        Date toDate = option.filterToDate(DfTypeUtil.toDate(toRes));

        // ## Assert ##
        assertEquals(ConditionKey.CK_GREATER_EQUAL, option.getFromDateConditionKey());
        assertEquals(ConditionKey.CK_LESS_THAN, option.getToDateConditionKey());
        assertEquals("2008-09-01 00:00:00", DfTypeUtil.toString(fromDate, "yyyy-MM-dd HH:mm:ss"));
        assertEquals("2008-12-01 00:00:00", DfTypeUtil.toString(toDate, "yyyy-MM-dd HH:mm:ss"));
    }

    public void test_compareAsYear_basic() {
        // ## Arrange ##
        String fromRes = "2008-09-14 12:34:56";
        String toRes = "2008-11-18 18:34:56";
        FromToOption option = createOption();

        // ## Act ##
        option.compareAsYear();
        Date fromDate = option.filterFromDate(DfTypeUtil.toDate(fromRes));
        Date toDate = option.filterToDate(DfTypeUtil.toDate(toRes));

        // ## Assert ##
        assertEquals(ConditionKey.CK_GREATER_EQUAL, option.getFromDateConditionKey());
        assertEquals(ConditionKey.CK_LESS_THAN, option.getToDateConditionKey());
        assertEquals("2008-01-01 00:00:00", DfTypeUtil.toString(fromDate, "yyyy-MM-dd HH:mm:ss"));
        assertEquals("2009-01-01 00:00:00", DfTypeUtil.toString(toDate, "yyyy-MM-dd HH:mm:ss"));
    }

    // ===================================================================================
    //                                                                   Manual Adjustment
    //                                                                   =================
    public void test_no_adjustment_basic() {
        // ## Arrange ##
        String expected = "2008-12-14 12:34:56";
        FromToOption option = createOption();

        // ## Act ##
        Date fromDate = option.filterFromDate(DfTypeUtil.toDate(expected));
        Date toDate = option.filterToDate(DfTypeUtil.toDate(expected));

        // ## Assert ##
        assertEquals(ConditionKey.CK_GREATER_EQUAL, option.getFromDateConditionKey());
        assertEquals(ConditionKey.CK_LESS_EQUAL, option.getToDateConditionKey());
        assertEquals(expected, DfTypeUtil.toString(fromDate, "yyyy-MM-dd HH:mm:ss"));
        assertEquals(expected, DfTypeUtil.toString(toDate, "yyyy-MM-dd HH:mm:ss"));
    }

    public void test_greaterThan_basic() {
        // ## Arrange ##
        FromToOption option = createOption();

        // ## Act ##
        option.greaterThan();

        // ## Assert ##
        assertEquals(ConditionKey.CK_GREATER_THAN, option.getFromDateConditionKey());
    }

    public void test_lessThan_basic() {
        // ## Arrange ##
        FromToOption option = createOption();

        // ## Act ##
        option.lessThan();

        // ## Assert ##
        assertEquals(ConditionKey.CK_LESS_THAN, option.getToDateConditionKey());
    }

    public void test_fromDateWithNoon_basic() {
        // ## Arrange ##
        String fromRes = "2008-12-14 18:34:56";
        String toRes = "2008-12-17 09:34:56";
        FromToOption option = createOption();

        // ## Act ##
        option.fromDateWithNoon();
        Date fromDate = option.filterFromDate(DfTypeUtil.toDate(fromRes));
        Date toDate = option.filterToDate(DfTypeUtil.toDate(toRes));

        // ## Assert ##
        assertEquals(ConditionKey.CK_GREATER_EQUAL, option.getFromDateConditionKey());
        assertEquals(ConditionKey.CK_LESS_EQUAL, option.getToDateConditionKey());
        assertEquals("2008-12-14 12:00:00", DfTypeUtil.toString(fromDate, "yyyy-MM-dd HH:mm:ss"));
        assertEquals(toRes, DfTypeUtil.toString(toDate, "yyyy-MM-dd HH:mm:ss"));
    }

    public void test_toDateWithNoon_basic() {
        // ## Arrange ##
        String fromRes = "2008-12-14 18:34:56";
        String toRes = "2008-12-17 09:34:56";
        FromToOption option = createOption();

        // ## Act ##
        option.toDateWithNoon();
        Date fromDate = option.filterFromDate(DfTypeUtil.toDate(fromRes));
        Date toDate = option.filterToDate(DfTypeUtil.toDate(toRes));

        // ## Assert ##
        assertEquals(ConditionKey.CK_GREATER_EQUAL, option.getFromDateConditionKey());
        assertEquals(ConditionKey.CK_LESS_EQUAL, option.getToDateConditionKey());
        assertEquals(fromRes, DfTypeUtil.toString(fromDate, "yyyy-MM-dd HH:mm:ss"));
        assertEquals("2008-12-17 12:00:00", DfTypeUtil.toString(toDate, "yyyy-MM-dd HH:mm:ss"));
    }

    public void test_fromDateWithHour_basic() {
        // ## Arrange ##
        String fromRes = "2008-12-14 18:34:56.789";
        String toRes = "2008-12-17 09:34:56.789";
        FromToOption option = createOption();

        // ## Act ##
        option.fromDateWithHour(16);
        Date fromDate = option.filterFromDate(DfTypeUtil.toDate(fromRes));
        Date toDate = option.filterToDate(DfTypeUtil.toDate(toRes));

        // ## Assert ##
        assertEquals(ConditionKey.CK_GREATER_EQUAL, option.getFromDateConditionKey());
        assertEquals(ConditionKey.CK_LESS_EQUAL, option.getToDateConditionKey());
        assertEquals("2008-12-14 16:00:00.000", DfTypeUtil.toString(fromDate, "yyyy-MM-dd HH:mm:ss.SSS"));
        assertEquals(toRes, DfTypeUtil.toString(toDate, "yyyy-MM-dd HH:mm:ss.SSS"));
    }

    public void test_toDateWithHour_basic() {
        // ## Arrange ##
        String fromRes = "2008-12-14 18:34:56.789";
        String toRes = "2008-12-17 09:34:56.789";
        FromToOption option = createOption();

        // ## Act ##
        option.toDateWithHour(3);
        Date fromDate = option.filterFromDate(DfTypeUtil.toDate(fromRes));
        Date toDate = option.filterToDate(DfTypeUtil.toDate(toRes));

        // ## Assert ##
        assertEquals(ConditionKey.CK_GREATER_EQUAL, option.getFromDateConditionKey());
        assertEquals(ConditionKey.CK_LESS_EQUAL, option.getToDateConditionKey());
        assertEquals("2008-12-17 03:00:00.000", DfTypeUtil.toString(toDate, "yyyy-MM-dd HH:mm:ss.SSS"));
        assertEquals(fromRes, DfTypeUtil.toString(fromDate, "yyyy-MM-dd HH:mm:ss.SSS"));
    }

    // ===================================================================================
    //                                                                    protected Method
    //                                                                    ================
    public void test_clearAll_pattern() {
        // ## Arrange ##
        String fromRes = "2008-12-14 18:34:56.789";
        String toRes = "2008-12-17 09:34:56.789";
        FromToOption option = createOption();

        // ## Act ##
        option.compareAsDate().clearAll();
        Date fromDate = option.filterFromDate(DfTypeUtil.toDate(fromRes));
        Date toDate = option.filterToDate(DfTypeUtil.toDate(toRes));

        // ## Assert ##
        assertEquals(ConditionKey.CK_GREATER_EQUAL, option.getFromDateConditionKey());
        assertEquals(ConditionKey.CK_LESS_EQUAL, option.getToDateConditionKey());
        assertEquals(fromRes, DfTypeUtil.toString(fromDate, "yyyy-MM-dd HH:mm:ss.SSS"));
        assertEquals(toRes, DfTypeUtil.toString(toDate, "yyyy-MM-dd HH:mm:ss.SSS"));
    }

    public void test_clearAll_manual() {
        // ## Arrange ##
        String fromRes = "2008-12-14 18:34:56.789";
        String toRes = "2008-12-17 09:34:56.789";
        FromToOption option = createOption();

        // ## Act ##
        option.toDateWithNoon().fromPatternDayStart().clearAll();
        Date fromDate = option.filterFromDate(DfTypeUtil.toDate(fromRes));
        Date toDate = option.filterToDate(DfTypeUtil.toDate(toRes));

        // ## Assert ##
        assertEquals(ConditionKey.CK_GREATER_EQUAL, option.getFromDateConditionKey());
        assertEquals(ConditionKey.CK_LESS_EQUAL, option.getToDateConditionKey());
        assertEquals(fromRes, DfTypeUtil.toString(fromDate, "yyyy-MM-dd HH:mm:ss.SSS"));
        assertEquals(toRes, DfTypeUtil.toString(toDate, "yyyy-MM-dd HH:mm:ss.SSS"));
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    public void test_toString() {
        // ## Arrange ##
        FromToOption option = createOption();
        option.compareAsDate();

        // ## Act ##
        String actual = option.toString();

        // ## Assert ##
        log(actual);
        assertTrue(actual.contains("lessThan=true"));
    }

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    protected FromToOption createOption() {
        return new FromToOption();
    }
}
