package org.seasar.robot.dbflute.util;

import static org.seasar.robot.dbflute.util.DfTypeUtil.AD_ORIGIN_MILLISECOND;
import static org.seasar.robot.dbflute.util.DfTypeUtil.toClassTitle;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.seasar.robot.dbflute.unit.PlainTestCase;
import org.seasar.robot.dbflute.util.DfTypeUtil.ParseDateException;
import org.seasar.robot.dbflute.util.DfTypeUtil.ParseDateNumberFormatException;
import org.seasar.robot.dbflute.util.DfTypeUtil.ParseDateOutOfCalendarException;
import org.seasar.robot.dbflute.util.DfTypeUtil.ParseTimestampException;
import org.seasar.robot.dbflute.util.DfTypeUtil.ParseTimestampNumberFormatException;
import org.seasar.robot.dbflute.util.DfTypeUtil.ParseTimestampOutOfCalendarException;

/**
 * @author jflute
 * @since 0.9.0 (2009/01/19 Monday)
 */
public class DfTypeUtilTest extends PlainTestCase {

    // ===================================================================================
    //                                                                          Convert To
    //                                                                          ==========
    // -----------------------------------------------------
    //                                                String
    //                                                ------
    public void test_toString_basic() throws UnsupportedEncodingException {
        // ## Arrange & Act & Assert ##
        assertNull(DfTypeUtil.toString(null));
        assertEquals("", DfTypeUtil.toString(""));
        assertEquals("foo", DfTypeUtil.toString("foo"));
        assertEquals("3", DfTypeUtil.toString(3));
        assertEquals("3", DfTypeUtil.toString(3L));
        assertEquals("3.7", DfTypeUtil.toString(new BigDecimal("3.7")));
    }

    public void test_toClassTitle_basic() {
        assertNull(toClassTitle(null));
        assertEquals("", toClassTitle(""));
        assertEquals("  ", toClassTitle("  "));
        assertEquals("Foo", toClassTitle("com.example.Foo"));
        assertEquals("Foo", toClassTitle("Foo"));
        assertEquals("Foo$Bar", toClassTitle("com.example.Foo$Bar"));
        assertEquals("Foo$1", toClassTitle("com.example.Foo$1"));
        assertEquals("Foo$1", toClassTitle("Foo$1"));
        assertEquals("String", toClassTitle(String.class));
        assertEquals("Object", toClassTitle(Object.class.getName()));
        assertEquals("Object", toClassTitle(Object.class));
        Object inner = new Object() {
        };
        assertEquals(getClass().getSimpleName() + "$1", toClassTitle(inner.getClass().getName()));
        assertEquals(getClass().getSimpleName() + "$1", toClassTitle(inner.getClass()));
        assertEquals(getClass().getSimpleName() + "$1", toClassTitle(inner));
        assertEquals(getClass().getSimpleName() + "$TestTitle", toClassTitle(TestTitle.class.getName()));
        assertEquals(getClass().getSimpleName() + "$TestTitle", toClassTitle(TestTitle.class));
        assertEquals(getClass().getSimpleName() + "$TestTitle", toClassTitle(new TestTitle()));
    }

    private static class TestTitle {
    }

    // -----------------------------------------------------
    //                                               Integer
    //                                               -------
    public void test_toInteger_basic() {
        // ## Arrange & Act & Assert ##
        assertNull(DfTypeUtil.toInteger(null));
        assertNull(DfTypeUtil.toInteger(""));
        assertEquals(Integer.valueOf(3), DfTypeUtil.toInteger("3"));
        assertEquals(Integer.valueOf(33333), DfTypeUtil.toInteger("33333"));
        assertEquals(Integer.valueOf(-33333), DfTypeUtil.toInteger("-33333"));
        assertEquals(Integer.valueOf(33333), DfTypeUtil.toInteger("33,333"));
    }

    public void test_toInteger_notNumber() {
        // ## Arrange ##
        String notNumber = "foo";

        // ## Act ##
        try {
            DfTypeUtil.toInteger(notNumber);

            // ## Assert ##
            fail();
        } catch (NumberFormatException e) {
            // OK
            log(e.getMessage());
        }
    }

    // -----------------------------------------------------
    //                                                  Date
    //                                                  ----
    public void test_toDate_sameClass() {
        // ## Arrange ##
        DateFormat df = DfTypeUtil.createDateFormat("yyyy/MM/dd HH:mm:ss");
        Date pureDate = new Date(DfTypeUtil.toDate("2009-12-13 12:34:56.123").getTime());

        // ## Act ##
        Date date = DfTypeUtil.toDate(pureDate);

        // ## Assert ##
        assertEquals(java.util.Date.class, date.getClass());
        assertFalse(date instanceof Timestamp); // because it returns pure date
        assertEquals("2009/12/13 12:34:56", df.format(date));
    }

    public void test_toDate_subClass() {
        // ## Arrange ##
        DateFormat df = DfTypeUtil.createDateFormat("yyyy/MM/dd HH:mm:ss");
        Timestamp timestamp = Timestamp.valueOf("2009-12-13 12:34:56.123");

        // ## Act ##
        Date date = DfTypeUtil.toDate(timestamp);

        // ## Assert ##
        assertEquals(java.util.Date.class, date.getClass());
        assertFalse(date instanceof Timestamp); // because it returns pure date
        assertEquals("2009/12/13 12:34:56", df.format(date));
    }

    public void test_toDate_string_basic() {
        // ## Arrange ##
        DateFormat df = DfTypeUtil.createDateFormat("yyyy/MM/dd HH:mm:ss");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.toDate(null));
        assertNull(DfTypeUtil.toDate(""));
        assertEquals("2008/12/30 12:34:56", df.format(DfTypeUtil.toDate(" 2008-12-30 12:34:56 ")));
    }

    public void test_toDate_string_instanceType() {
        // ## Arrange & Act & Assert ##
        assertEquals(java.util.Date.class, DfTypeUtil.toDate("2008-12-30 12:34:56.789").getClass());
        assertNotSame(java.sql.Date.class, DfTypeUtil.toDate("2008-12-30 12:34:56.789").getClass());
        assertNotSame(java.sql.Timestamp.class, DfTypeUtil.toDate("2008-12-30 12:34:56.789").getClass());
    }

    public void test_toDate_string_AD() {
        DateFormat df = DfTypeUtil.createDateFormat("yyyy/MM/dd HH:mm:ss");
        DateFormat dfmil = DfTypeUtil.createDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        DateFormat gdf = DfTypeUtil.createDateFormat("Gyyyy/MM/dd HH:mm:ss.SSS");

        // ## Act & Assert ##
        log(gdf.format(DfTypeUtil.toDate("A.D.2008-9-1")));
        assertTrue(DfTypeUtil.toDate("A.D.1-1-1 00:00:00.000").getTime() >= AD_ORIGIN_MILLISECOND);
        assertEquals(AD_ORIGIN_MILLISECOND, DfTypeUtil.toDate("A.D.1-1-1 00:00:00.000").getTime());
        assertTrue(DfTypeUtil.toDate("2008-10-21 12:34:56").getTime() >= AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toDate("AD8-9-1 12:34:56").getTime() >= AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toDate("A.D.8-9-1 12:34:56").getTime() >= AD_ORIGIN_MILLISECOND);
        assertEquals("0008/09/01 12:34:56", df.format(DfTypeUtil.toDate("AD8-9-1 12:34:56")));
        assertEquals("2008/09/01 00:00:00", df.format(DfTypeUtil.toDate("A.D.2008-9-1")));
        assertEquals("0001/01/01 00:00:00.000", dfmil.format(DfTypeUtil.toDate("AD1-1-1 00:00:00.000")));
        assertEquals("0001/01/01 00:00:00.000", dfmil.format(DfTypeUtil.toDate("date 1-1-1 00:00:00.000")));
        assertEquals("0001/01/01 00:00:00.000", dfmil.format(DfTypeUtil.toDate("date AD1-1-1 00:00:00.000")));
    }

    public void test_toDate_string_BC() {
        // ## Arrange ##
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat fullDf = new SimpleDateFormat("Gyyyy/MM/dd HH:mm:ss.SSS");

        // ## Act & Assert ##
        log(fullDf.format(DfTypeUtil.toDate("-2008-09-01 02:04:06")));
        assertTrue(DfTypeUtil.toDate("B.C.0001-12-31 23:59:59.999").getTime() < AD_ORIGIN_MILLISECOND);
        assertEquals(AD_ORIGIN_MILLISECOND - 1L, DfTypeUtil.toDate("B.C.0001-12-31 23:59:59.999").getTime());
        assertTrue(DfTypeUtil.toDate("-8-9-1").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toDate("-1-9-1").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toDate("-8-9-1 2:4:6").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toDate("date -80901").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toDate("-2008-09-01 02:04:06").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toDate("-2008-13-01 02:04:06").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toDate("BC8-9-1 2:4:6").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toDate("BC2008-09-01 02:04:06").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toDate("BC2008-13-01 02:04:06").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toDate("B.C.8-9-1 2:4:6").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toDate("B.C.2008-09-01 02:04:06").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toDate("B.C.2008-13-01 02:04:06").getTime() < AD_ORIGIN_MILLISECOND);
        assertEquals("2008/11/01 02:04:06", df.format(DfTypeUtil.toDate("-2008-11-01 02:04:06")));
        assertEquals("0008/09/01 00:00:00", df.format(DfTypeUtil.toDate("date -80901")));

        // no calendar check when BC 
        assertEquals("2007/01/01 02:04:06", df.format(DfTypeUtil.toDate("-2008-13-01 02:04:06")));
    }

    public void test_toDate_string_various() {
        // ## Arrange ##
        DateFormat df = DfTypeUtil.createDateFormat("yyyy/MM/dd HH:mm:ss");
        DateFormat dfmil = DfTypeUtil.createDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        // ## Act & Assert ##
        assertEquals("0002/01/12 00:00:00", df.format(DfTypeUtil.toDate("date 20112")));
        assertEquals("0012/01/22 00:00:00", df.format(DfTypeUtil.toDate("date 120122")));
        assertEquals("0923/01/27 00:00:00", df.format(DfTypeUtil.toDate("date 9230127")));
        assertEquals("2008/12/30 00:00:00", df.format(DfTypeUtil.toDate("date 20081230")));
        assertEquals("2008/12/30 00:00:00", df.format(DfTypeUtil.toDate("2008/12/30")));
        assertEquals("2008/12/30 00:00:00", df.format(DfTypeUtil.toDate("2008-12-30")));
        assertEquals("2008/12/30 12:34:56", df.format(DfTypeUtil.toDate("2008-12-30 12:34:56")));
        assertEquals("2008/12/30 12:34:56", df.format(DfTypeUtil.toDate("2008-12-30 12:34:56.789")));
        assertEquals("2008/09/30 12:34:56", df.format(DfTypeUtil.toDate("2008-09-30 12:34:56")));
        assertEquals("2008/09/30 12:34:56", df.format(DfTypeUtil.toDate("2008-9-30 12:34:56")));
        assertEquals("2008/09/01 12:34:56", df.format(DfTypeUtil.toDate("2008-9-1 12:34:56")));
        assertEquals("0008/09/01 12:34:56", df.format(DfTypeUtil.toDate("8-9-1 12:34:56")));
        assertEquals("2008/09/01 00:00:00", df.format(DfTypeUtil.toDate("2008-9-1")));
        assertEquals("0008/09/01 02:04:06", df.format(DfTypeUtil.toDate("8-9-1 02:04:06")));
        assertEquals("0008/09/01 02:04:06", df.format(DfTypeUtil.toDate("8-9-1 2:4:6")));
        assertEquals("2008/12/30 12:34:56.789", dfmil.format(DfTypeUtil.toDate("2008-12-30 12:34:56.789")));
    }

    public void test_toDate_long_basic() {
        // ## Arrange ##
        DateFormat dfmil = DfTypeUtil.createDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = DfTypeUtil.toDate("2008-12-30 12:34:56.789");

        // ## Act ##
        Date actual = DfTypeUtil.toDate(date.getTime());

        // ## Assert ##
        assertEquals(dfmil.format(date), dfmil.format(actual));
    }

    public void test_toDate_illegal() {
        try {
            DfTypeUtil.toDate("2009-12");

            fail();
        } catch (ParseDateException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toDate("date 2009");

            fail();
        } catch (ParseDateException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toDate("date 20091");

            fail();
        } catch (ParseDateOutOfCalendarException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toDate("2009-12-09 12:34:60");

            fail();
        } catch (ParseDateOutOfCalendarException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toDate("AD2009-12-09 12:34:60");

            fail();
        } catch (ParseDateOutOfCalendarException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toDate("2009-1209");

            fail();
        } catch (ParseDateException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toDate("2009-12-0-9 12:34:56");

            fail();
        } catch (ParseDateNumberFormatException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toDate("2009-12-a9 12:34:56");

            fail();
        } catch (ParseDateNumberFormatException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toDate("2009-12-09 12:34:a6");

            fail();
        } catch (ParseDateNumberFormatException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toDate("0000-12-09 12:34:26");

            fail();
        } catch (ParseDateOutOfCalendarException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_toDate_isDateAD() {
        // ## Arrange & Act & Assert ##
        assertTrue(DfTypeUtil.isDateAD(DfTypeUtil.toDate("2008-12-30 12:34:56.789")));
        Date before = DfTypeUtil.toDate("BC0001-12-31 23:59:59.999");
        Date after = DfTypeUtil.toDate("0001-01-01 00:00:00.000");
        log("before time = " + before.getTime());
        log("after  time = " + after.getTime());
        assertFalse(DfTypeUtil.isDateAD(before));
        assertTrue(DfTypeUtil.isDateAD(after));
        assertEquals(GregorianCalendar.BC, DfTypeUtil.toCalendar(before).get(Calendar.ERA));
        assertEquals(GregorianCalendar.AD, DfTypeUtil.toCalendar(after).get(Calendar.ERA));

        // extra
        DfTypeUtil.addDateDate(before, 1);
        DfTypeUtil.clearDateTimeParts(before);
        assertEquals(after, before);
    }

    public void test_toDate_isDateBC() {
        // ## Arrange & Act & Assert ##
        assertFalse(DfTypeUtil.isDateBC(DfTypeUtil.toDate("2008-12-30 12:34:56.789")));
        Date before = DfTypeUtil.toDate("BC0001-12-31 23:59:59.999");
        Date after = DfTypeUtil.toDate("0001-01-01 00:00:00.000");
        log("before time = " + before.getTime());
        log("after  time = " + after.getTime());
        assertTrue(DfTypeUtil.isDateBC(before));
        assertFalse(DfTypeUtil.isDateBC(after));
        assertEquals(GregorianCalendar.BC, DfTypeUtil.toCalendar(before).get(Calendar.ERA));
        assertEquals(GregorianCalendar.AD, DfTypeUtil.toCalendar(after).get(Calendar.ERA));
    }

    public void test_setDateFirstDateOfMonth() {
        // ## Arrange ##
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = DfTypeUtil.toDate("2008-12-30 12:34:56.789");

        // ## Act ##
        DfTypeUtil.setDateFirstDateOfMonth(date);

        // ## Assert ##
        assertEquals("2008/12/01 12:34:56.789", df.format(date));
    }

    public void test_setDateLastDateOfMonth() {
        // ## Arrange ##
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = DfTypeUtil.toDate("2008-02-06 12:34:56.789");

        // ## Act ##
        DfTypeUtil.setDateLastDateOfMonth(date);

        // ## Assert ##
        assertEquals("2008/02/29 12:34:56.789", df.format(date));
    }

    public void test_clearDateTimeParts() {
        // ## Arrange ##
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = DfTypeUtil.toDate("2008-12-30 12:34:56.789");

        // ## Act ##
        DfTypeUtil.clearDateTimeParts(date);

        // ## Assert ##
        assertEquals("2008/12/30 00:00:00.000", df.format(date));
    }

    public void test_clearDateMillisecond() {
        // ## Arrange ##
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = DfTypeUtil.toDate("2008-12-30 12:34:56.789");

        // ## Act ##
        DfTypeUtil.clearDateMillisecond(date);

        // ## Assert ##
        assertEquals("2008/12/30 12:34:56.000", df.format(date));
    }

    // -----------------------------------------------------
    //                                             Timestamp
    //                                             ---------
    public void test_toTimestamp_various() {
        // ## Arrange ##
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.toTimestamp(null));
        assertNull(DfTypeUtil.toTimestamp(""));
        assertEquals("0002/01/12 00:00:00.000", df.format(DfTypeUtil.toTimestamp("date 20112")));
        assertEquals("0012/01/22 00:00:00.000", df.format(DfTypeUtil.toTimestamp("date 120122")));
        assertEquals("0923/01/27 00:00:00.000", df.format(DfTypeUtil.toTimestamp("date 9230127")));
        assertEquals("2008/12/30 00:00:00.000", df.format(DfTypeUtil.toTimestamp("date 20081230")));
        assertEquals("2008/12/30 00:00:00.000", df.format(DfTypeUtil.toTimestamp("2008/12/30")));
        assertEquals("2008/12/30 12:34:56.000", df.format(DfTypeUtil.toTimestamp("2008/12/30 12:34:56")));
        assertEquals("2008/12/30 12:34:56.789", df.format(DfTypeUtil.toTimestamp("2008/12/30 12:34:56.789")));
        assertEquals("2008/12/30 00:00:00.000", df.format(DfTypeUtil.toTimestamp("2008-12-30")));
        assertEquals("2008/12/30 12:34:56.000", df.format(DfTypeUtil.toTimestamp("2008-12-30 12:34:56")));
        assertEquals("2008/12/30 12:34:56.789", df.format(DfTypeUtil.toTimestamp("2008-12-30 12:34:56.789")));
        assertEquals("2008/09/30 12:34:56.000", df.format(DfTypeUtil.toTimestamp("2008-09-30 12:34:56")));
        assertEquals("2008/09/30 12:34:56.000", df.format(DfTypeUtil.toTimestamp("2008-9-30 12:34:56")));
        assertEquals("2008/09/01 12:34:56.000", df.format(DfTypeUtil.toTimestamp("2008-9-1 12:34:56")));
        assertEquals("0008/09/01 12:34:56.000", df.format(DfTypeUtil.toTimestamp("8-9-1 12:34:56")));
        assertEquals("2008/09/01 00:00:00.000", df.format(DfTypeUtil.toTimestamp("2008-9-1")));
        assertEquals("0008/09/01 02:04:06.000", df.format(DfTypeUtil.toTimestamp("8-9-1 02:04:06")));
        assertEquals("0008/09/01 02:04:06.000", df.format(DfTypeUtil.toTimestamp("8-9-1 2:4:6")));
        assertEquals("2008/12/30 12:34:56.009", df.format(DfTypeUtil.toTimestamp("2008-12-30 12:34:56.9")));
        assertEquals("0008/09/01 02:04:06.000", df.format(DfTypeUtil.toTimestamp("AD8-9-1 02:04:06")));
        assertEquals("0008/09/01 02:04:06.000", df.format(DfTypeUtil.toTimestamp("A.D.8-9-1 2:4:6")));
        assertEquals("2008/12/30 12:34:56.009", df.format(DfTypeUtil.toTimestamp(" 2008-12-30 12:34:56.9 ")));
        assertEquals("2008/12/30 12:34:56.009", df.format(DfTypeUtil.toTimestamp(" date 2008-12-30 12:34:56.9 ")));
        assertEquals("2008/12/30 12:34:56.009", df.format(DfTypeUtil.toTimestamp(" date A.D.2008-12-30 12:34:56.9 ")));
        assertNotSame(java.util.Date.class, DfTypeUtil.toTimestamp("2008-12-30 12:34:56.789").getClass());
        assertNotSame(java.sql.Date.class, DfTypeUtil.toTimestamp("2008-12-30 12:34:56.789").getClass());
        assertEquals(java.sql.Timestamp.class, DfTypeUtil.toTimestamp("2008-12-30 12:34:56.789").getClass());
    }

    public void test_toTimestamp_various_BC() {
        // ## Arrange ##
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        SimpleDateFormat fullDf = new SimpleDateFormat("Gyyyy/MM/dd HH:mm:ss.SSS");

        // ## Act & Assert ##
        log(fullDf.format(DfTypeUtil.toTimestamp("-2008-09-01 02:04:06.123")));
        assertTrue(DfTypeUtil.toTimestamp("-8-9-1").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toTimestamp("-1-9-1").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toTimestamp("-8-9-1 2:4:6").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toTimestamp("date -80901").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toTimestamp("-2008-09-01 02:04:06.123").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toTimestamp("-2008-13-01 02:04:06").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toTimestamp("BC8-9-1 2:4:6").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toTimestamp("BC2008-09-01 02:04:06").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toTimestamp("BC2008-13-01 02:04:06.123").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toTimestamp("B.C.8-9-1 2:4:6").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toTimestamp("B.C.2008-09-01 02:04:06.123").getTime() < AD_ORIGIN_MILLISECOND);
        assertTrue(DfTypeUtil.toTimestamp("B.C.2008-13-01 02:04:06").getTime() < AD_ORIGIN_MILLISECOND);
        assertEquals("2008/11/01 02:04:06.123", df.format(DfTypeUtil.toTimestamp("-2008-11-01 02:04:06.123")));
        assertEquals("0008/09/01 00:00:00.000", df.format(DfTypeUtil.toTimestamp("date -80901")));

        // no calendar check when BC 
        assertEquals("2007/01/01 02:04:06.123", df.format(DfTypeUtil.toTimestamp("-2008-13-01 02:04:06.123")));
    }

    public void test_toTimestamp_long_basic() {
        // ## Arrange ##
        DateFormat dfmil = DfTypeUtil.createDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = DfTypeUtil.toTimestamp("2008-12-30 12:34:56.789");

        // ## Act ##
        Date actual = DfTypeUtil.toTimestamp(date.getTime());

        // ## Assert ##
        assertEquals(dfmil.format(date), dfmil.format(actual));
    }

    public void test_toTimestamp_illegal() {
        try {
            DfTypeUtil.toTimestamp("2009-12");

            fail();
        } catch (ParseTimestampException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toTimestamp("2009");

            fail();
        } catch (ParseTimestampException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toTimestamp("date 20091");

            fail();
        } catch (ParseTimestampOutOfCalendarException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toTimestamp("2009-12-09 12:34:60");

            fail();
        } catch (ParseTimestampOutOfCalendarException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toTimestamp("AD2009-12-09 12:34:60");

            fail();
        } catch (ParseTimestampOutOfCalendarException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toTimestamp("2009-1209");

            fail();
        } catch (ParseTimestampException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toTimestamp("2009-12-0-9 12:34:56");

            fail();
        } catch (ParseTimestampNumberFormatException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toTimestamp("2009-12-a9 12:34:56");

            fail();
        } catch (ParseTimestampNumberFormatException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toTimestamp("2009-12-09 12:a4:36");

            fail();
        } catch (ParseTimestampNumberFormatException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toTimestamp("2009-12-09 12:34:36.12a");

            fail();
        } catch (ParseTimestampNumberFormatException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toTimestamp("2009-12-09 12:34:36.1234");

            fail();
        } catch (ParseTimestampOutOfCalendarException e) {
            // OK
            log(e.getMessage());
        }
        try {
            DfTypeUtil.toTimestamp("0000-12-09 12:34:26.541");

            fail();
        } catch (ParseTimestampOutOfCalendarException e) {
            // OK
            log(e.getMessage());
        }

    }

    // -----------------------------------------------------
    //                                                  Time
    //                                                  ----
    public void test_toTime_timestamp() {
        // ## Arrange ##
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = DfTypeUtil.toTimestamp("2008-12-30 12:34:56.789");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.toTime(null));
        assertNull(DfTypeUtil.toTime(""));
        assertEquals("1970/01/01 12:34:56.789", df.format(DfTypeUtil.toTime(date)));
    }

    public void test_toTime_long_basic() {
        // ## Arrange ##
        DateFormat dfmil = DfTypeUtil.createDateFormat("HH:mm:ss.SSS");
        Date date = DfTypeUtil.toDate("2008-12-30 12:34:56.789");

        // ## Act ##
        Date actual = DfTypeUtil.toTime(date.getTime());

        // ## Assert ##
        assertEquals(dfmil.format(date), dfmil.format(actual));
    }

    public void test_toTime_various() {
        // ## Arrange ##
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat fullDf = new SimpleDateFormat("HH:mm:ss.SSS");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.toTime(null));
        assertNull(DfTypeUtil.toTime(""));
        assertEquals("12:34:56", df.format(DfTypeUtil.toTime("2009/12/12 12:34:56")));
        assertEquals("12:34:56", df.format(DfTypeUtil.toTime("12:34:56")));
        assertEquals("02:04:06", df.format(DfTypeUtil.toTime("02:04:06")));
        assertEquals("02:04:06", df.format(DfTypeUtil.toTime("2:4:6")));
        assertEquals("12:34:56", df.format(DfTypeUtil.toTime("12:34:56.789")));
        assertEquals("12:34:56.000", fullDf.format(DfTypeUtil.toTime("12:34:56.789")));
    }

    // -----------------------------------------------------
    //                                              SQL Date
    //                                              --------
    public void test_toSqlDate_basic() {
        assertNull(DfTypeUtil.toSqlDate(null));
        assertNull(DfTypeUtil.toSqlDate(""));
    }

    public void test_toSqlDate_same() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        // ## Act ##
        java.sql.Date date = DfTypeUtil.toSqlDate(DfTypeUtil.toDate("2008-12-30 12:34:56.789"));

        // ## Assert ##
        assertEquals("2008/12/30 00:00:00.000", f.format(date));
    }

    public void test_toSqlDate_timestamp() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Timestamp date = DfTypeUtil.toTimestamp("2008-12-30 12:34:56.789");

        // ## Act & Assert ##
        assertEquals("2008/12/30 00:00:00.000", f.format(DfTypeUtil.toSqlDate(date)));
    }

    public void test_toSqlDate_various() {
        // ## Arrange ##
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.toSqlDate(null));
        assertNull(DfTypeUtil.toSqlDate(""));
        assertEquals("0002/01/12", f.format(DfTypeUtil.toSqlDate("date 20112")));
        assertEquals("0012/01/22", f.format(DfTypeUtil.toSqlDate("date 120122")));
        assertEquals("0923/01/27", f.format(DfTypeUtil.toSqlDate("date 9230127")));
        assertEquals("2008/12/30", f.format(DfTypeUtil.toSqlDate("date 20081230")));
        assertEquals("2008/12/30", f.format(DfTypeUtil.toSqlDate("2008/12/30")));
        assertEquals("2008/12/30", f.format(DfTypeUtil.toSqlDate("2008-12-30")));
        assertEquals("2008/12/30", f.format(DfTypeUtil.toSqlDate("2008-12-30 12:34:56")));
        assertEquals("2008/12/30", f.format(DfTypeUtil.toSqlDate("2008-12-30 12:34:56.789")));
        assertEquals("2008/09/30", f.format(DfTypeUtil.toSqlDate("2008-09-30 12:34:56")));
        assertEquals("2008/09/30", f.format(DfTypeUtil.toSqlDate("2008-9-30 12:34:56")));
        assertEquals("2008/09/01", f.format(DfTypeUtil.toSqlDate("2008-9-1 12:34:56")));
        assertEquals("0008/09/01", f.format(DfTypeUtil.toSqlDate("8-9-1 12:34:56")));
        assertEquals("2008/09/01", f.format(DfTypeUtil.toSqlDate("2008-9-1")));
        assertEquals("0008/09/01 00:00:00.000", ft.format(DfTypeUtil.toSqlDate("8-9-1 12:34:56")));
        assertEquals("2008/12/30 00:00:00.000", ft.format(DfTypeUtil.toSqlDate("2008-12-30 12:34:56.789")));
        assertEquals(java.sql.Date.class, DfTypeUtil.toSqlDate("2008-12-30 12:34:56.789").getClass());
        assertNotSame(java.util.Date.class, DfTypeUtil.toSqlDate("2008-12-30 12:34:56.789").getClass());
        assertNotSame(java.sql.Timestamp.class, DfTypeUtil.toSqlDate("2008-12-30 12:34:56.789").getClass());
    }

    // -----------------------------------------------------
    //                                               Boolean
    //                                               -------
    public void test_toBoolean_basic() {
        // ## Arrange & Act & Assert ##
        assertNull(DfTypeUtil.toBoolean(null));
        assertTrue(DfTypeUtil.toBoolean("true"));
        assertFalse(DfTypeUtil.toBoolean("false"));
    }

    // -----------------------------------------------------
    //                                                Binary
    //                                                ------
    public void test_toBinary_basic() {
        // ## Arrange & Act & Assert ##
        assertNull(DfTypeUtil.toBinary(null));
        assertNotNull(DfTypeUtil.toBinary(""));
    }

    public void test_toBinary_byteArray() throws UnsupportedEncodingException {
        // ## Arrange ##
        final byte[] bytes = "foo".getBytes("UTF-8");

        // ## Act & Assert ##
        assertEquals(bytes, DfTypeUtil.toBinary(bytes));
        assertTrue(bytes instanceof Serializable); // confirmation
    }

    public void test_toBinary_serializable() {
        // ## Arrange ##
        Date expected = DfTypeUtil.toDate("2010-03-11");
        String pt = "yyyy-MM-dd";

        // ## Act ##
        byte[] binary = DfTypeUtil.toBinary(expected);

        // ## Assert ##
        Date actual = DfTypeUtil.toDate(binary);
        log(DfTypeUtil.toString(actual, "yyyy-MM-dd"));
        assertEquals(DfTypeUtil.toString(expected, pt), DfTypeUtil.toString(actual, pt));
    }

    // ===================================================================================
    //                                                                              Format
    //                                                                              ======
    public void test_format_Date() {
        // ## Arrange ##
        Date date = DfTypeUtil.toDate("2008/12/30 12:34:56");
        Timestamp timestamp = DfTypeUtil.toTimestamp("2008/12/30 12:34:56");

        // ## Act & Assert ##
        assertNull(DfTypeUtil.toString((Date) null, "yyyy/MM/dd HH:mm:ss"));
        assertEquals("2008/12/30 12:34:56", DfTypeUtil.toString(date, "yyyy/MM/dd HH:mm:ss"));
        assertEquals("2008/12/30", DfTypeUtil.toString(date, "yyyy/MM/dd"));
        assertEquals("2008-12-30", DfTypeUtil.toString(date, "yyyy-MM-dd"));
        assertEquals("2008-12-30 12:34:56.000", DfTypeUtil.toString(date, "yyyy-MM-dd HH:mm:ss.SSS"));
        assertEquals("2008/12/30 12:34:56", DfTypeUtil.toString(timestamp, "yyyy/MM/dd HH:mm:ss"));
        assertEquals("2008/12/30", DfTypeUtil.toString(timestamp, "yyyy/MM/dd"));
        assertEquals("2008-12-30", DfTypeUtil.toString(timestamp, "yyyy-MM-dd"));
        assertEquals("2008-12-30 12:34:56.000", DfTypeUtil.toString(timestamp, "yyyy-MM-dd HH:mm:ss.SSS"));
    }
}
