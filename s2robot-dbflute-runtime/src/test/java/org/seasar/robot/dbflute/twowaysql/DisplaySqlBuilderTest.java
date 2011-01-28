package org.seasar.robot.dbflute.twowaysql;

import java.sql.Timestamp;
import java.util.Date;

import org.seasar.robot.dbflute.twowaysql.DisplaySqlBuilder.DateFormatResource;
import org.seasar.robot.dbflute.unit.PlainTestCase;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * 
 * @author jflute
 * @since 0.9.6 (2009/10/27 Tuesday)
 */
public class DisplaySqlBuilderTest extends PlainTestCase {

    public void test_getBindVariableText_dateFormat_basic() {
        // ## Arrange ##
        Date date = DfTypeUtil.toDate("2009-10-27");

        // ## Act ##
        String actual = DisplaySqlBuilder.getBindVariableText(date, null, null);

        // ## Assert ##
        assertEquals("'2009-10-27'", actual);
    }

    public void test_getBindVariableText_dateFormat_custom() {
        // ## Arrange ##
        String format = "date $df:{yyyy-MM-dd}";
        Date date = DfTypeUtil.toDate("2009-10-27");

        // ## Act ##
        String actual = DisplaySqlBuilder.getBindVariableText(date, format, null);

        // ## Assert ##
        assertEquals("date '2009-10-27'", actual);
    }

    public void test_getBindVariableText_timestampFormat_basic() {
        // ## Arrange ##
        String format = "yyyy-MM-dd HH:mm:ss.SSS";
        Timestamp timestamp = DfTypeUtil.toTimestamp("2009-10-27 16:22:23.123");

        // ## Act ##
        String actual = DisplaySqlBuilder.getBindVariableText(timestamp, null, format);

        // ## Assert ##
        assertEquals("'2009-10-27 16:22:23.123'", actual);
    }

    public void test_getBindVariableText_timestampFormat_custom() {
        // ## Arrange ##
        String format = "timestamp $df:{yyyy-MM-dd HH:mm:ss.SSS}";
        Timestamp timestamp = DfTypeUtil.toTimestamp("2009-10-27 16:22:23.123");

        // ## Act ##
        String actual = DisplaySqlBuilder.getBindVariableText(timestamp, null, format);

        // ## Assert ##
        assertEquals("timestamp '2009-10-27 16:22:23.123'", actual);
    }

    public void test_analyzeDateFormat_basic() {
        // ## Arrange ##
        String format = "yyyy-MM-dd";

        // ## Act ##
        DateFormatResource resource = DisplaySqlBuilder.analyzeDateFormat(format);

        // ## Assert ##
        assertEquals(format, resource.getFormat());
        assertNull(resource.getPrefix());
        assertNull(resource.getSuffix());
    }

    public void test_analyzeDateFormat_markOnly() {
        // ## Arrange ##
        String format = "$df:{yyyy-MM-dd}";

        // ## Act ##
        DateFormatResource resource = DisplaySqlBuilder.analyzeDateFormat(format);

        // ## Assert ##
        assertEquals("yyyy-MM-dd", resource.getFormat());
        assertEquals("", resource.getPrefix());
        assertEquals("", resource.getSuffix());
    }

    public void test_analyzeDateFormat_prefixOnly() {
        // ## Arrange ##
        String format = "date $df:{yyyy-MM-dd}";

        // ## Act ##
        DateFormatResource resource = DisplaySqlBuilder.analyzeDateFormat(format);

        // ## Assert ##
        assertEquals("yyyy-MM-dd", resource.getFormat());
        assertEquals("date ", resource.getPrefix());
        assertEquals("", resource.getSuffix());
    }

    public void test_analyzeDateFormat_suffixOnly() {
        // ## Arrange ##
        String format = "$df:{yyyy-MM-dd}sufsuf";

        // ## Act ##
        DateFormatResource resource = DisplaySqlBuilder.analyzeDateFormat(format);

        // ## Assert ##
        assertEquals("yyyy-MM-dd", resource.getFormat());
        assertEquals("", resource.getPrefix());
        assertEquals("sufsuf", resource.getSuffix());
    }

    public void test_analyzeDateFormat_prefixSuffix() {
        // ## Arrange ##
        String format = "FOO($df:{yyyy-MM-dd}, 'BAR')";

        // ## Act ##
        DateFormatResource resource = DisplaySqlBuilder.analyzeDateFormat(format);

        // ## Assert ##
        assertEquals("yyyy-MM-dd", resource.getFormat());
        assertEquals("FOO(", resource.getPrefix());
        assertEquals(", 'BAR')", resource.getSuffix());
    }

    public void test_quote_basic() {
        assertEquals("'foo'", DisplaySqlBuilder.quote("foo"));
    }

    public void test_quote_with_DateFormatResource() {
        // ## Arrange ##
        DateFormatResource resource = new DateFormatResource();
        resource.setPrefix("prepre");
        resource.setSuffix("sufsuf");

        // ## Act & Assert ##
        assertEquals("prepre'foo'sufsuf", DisplaySqlBuilder.quote("foo", resource));
    }
}
