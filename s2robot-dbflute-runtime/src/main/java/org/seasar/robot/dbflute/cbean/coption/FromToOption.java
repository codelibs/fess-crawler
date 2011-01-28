/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.robot.dbflute.cbean.coption;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * The option of from-to for Date type.
 * <pre>
 * ex) from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
 * 
 * [Comparison Pattern]
 *   new FromToOption().compareAsDate(); 
 *     --&gt; column &gt;= '2007/04/10 00:00:00'
 *     and column &lt; '2007/04/17 00:00:00'
 * 
 *   new FromToOption().compareAsMonth(); 
 *     --&gt; column &gt;= '2007/04/01 00:00:00'
 *     and column &lt; '2007/05/01 00:00:00'
 * 
 *   new FromToOption().compareAsYear(); 
 *     --&gt; column &gt;= '2007/01/01 00:00:00'
 *     and column &lt; '2008/01/01 00:00:00'
 * 
 * [Manual Adjustment]
 *   new FromToOption().greaterThan(); 
 *     --&gt; column &gt; '2007/04/10 08:24:53'
 *     and column &lt;= '2007/04/16 14:36:29'
 * 
 *   new FromToOption().lessThan(); 
 *     --&gt; column &gt;= '2007/04/10 08:24:53'
 *     and column &lt; '2007/04/16 14:36:29'
 * 
 *   new FromToOption().greaterThan().lessThan(); 
 *     --&gt; column &gt; '2007/04/10 08:24:53'
 *     and column &lt; '2007/04/16 14:36:29'
 * 
 *   and so on...
 * 
 * [Default]
 *   new FromToOption(); 
 *     --&gt; column &gt;= '2007/04/10 08:24:53'
 *     and column &lt;= '2007/04/16 14:36:29'
 * </pre>
 * @author jflute
 */
public class FromToOption implements ConditionOption, Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _greaterThan;
    protected boolean _lessThan;

    protected boolean _fromPatternDayStart;
    protected boolean _fromPatternMonthStart;
    protected boolean _fromPatternYearStart;
    protected boolean _fromDateWithNoon;
    protected Integer _fromDateWithHour;

    protected boolean _toPatternNextDayStart;
    protected boolean _toPatternNextMonthStart;
    protected boolean _toPatternNextYearStart;
    protected boolean _toDateWithNoon;
    protected Integer _toDateWithHour;

    protected boolean _usePattern;

    // ===================================================================================
    //                                                            Interface Implementation
    //                                                            ========================
    public String getRearOption() {
        String msg = "Thie option does not use getRearOption()!";
        throw new UnsupportedOperationException(msg);
    }

    // ===================================================================================
    //                                                                  Comparison Pattern
    //                                                                  ==================
    /**
     * Compare as date. <br />
     * This method ignores operand adjustments and other patterns.
     * <pre>
     * ex) from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     * 
     *   new FromToOption().compareAsDate();
     *     --&gt; column &gt;= '2007/04/10 00:00:00'
     *     and column &lt; '2007/04/17 00:00:00'
     * </pre>
     * @return this. (NotNull)
     */
    public FromToOption compareAsDate() {
        fromPatternDayStart();
        toPatternNextDayStart();
        clearOperand();
        lessThan();
        _usePattern = true;
        return this;
    }

    /**
     * Compare as month. <br />
     * This method ignores operand adjustments and other patterns.
     * <pre>
     * ex) from:{2007/04/10 08:24:53} to:{2008/08/16 14:36:29}
     * 
     *   new FromToOption().compareAsMonth();
     *     --&gt; column &gt;= '2007/04/01 00:00:00'
     *     and column &lt; '2008/09/01 00:00:00'
     * </pre>
     * @return this. (NotNull)
     */
    public FromToOption compareAsMonth() {
        fromPatternMonthStart();
        toPatternNextMonthStart();
        clearOperand();
        lessThan();
        _usePattern = true;
        return this;
    }

    /**
     * Compare as year. <br />
     * This method ignores operand adjustments and other patterns.
     * <pre>
     * ex) from:{2007/04/10 08:24:53} to:{2008/08/16 14:36:29}
     * 
     *   new FromToOption().compareAsYear();
     *     --&gt; column &gt;= '2007/01/01 00:00:00'
     *     and column &lt; '2009/01/01 00:00:00'
     * </pre>
     * @return this. (NotNull)
     */
    public FromToOption compareAsYear() {
        fromPatternYearStart();
        toPatternNextYearStart();
        clearOperand();
        lessThan();
        _usePattern = true;
        return this;
    }

    // ===================================================================================
    //                                                                   Manual Adjustment
    //                                                                   =================
    // -----------------------------------------------------
    //                                                   All
    //                                                   ---
    protected void clearAll() {
        clearOperand();
        clearFromPattern();
        clearToPattern();
        clearFromDateWith();
        clearToDateWith();
        _usePattern = false;
    }

    // -----------------------------------------------------
    //                                               Operand
    //                                               -------
    public FromToOption greaterThan() {
        assertNotAdjustmentAfterPattern("greaterThan");
        _greaterThan = true;
        return this;
    }

    public FromToOption lessThan() {
        assertNotAdjustmentAfterPattern("lessThan");
        _lessThan = true;
        return this;
    }

    protected void clearOperand() {
        _greaterThan = false;
        _lessThan = false;
    }

    // -----------------------------------------------------
    //                                             From Date
    //                                             ---------
    public FromToOption fromPatternDayStart() {
        assertNotAdjustmentAfterPattern("fromPatternDayStart");
        clearFromPattern();
        _fromPatternDayStart = true;
        return this;
    }

    public FromToOption fromPatternMonthStart() {
        assertNotAdjustmentAfterPattern("fromPatternMonthStart");
        clearFromPattern();
        _fromPatternMonthStart = true;
        return this;
    }

    public FromToOption fromPatternYearStart() {
        assertNotAdjustmentAfterPattern("fromPatternYearStart");
        clearFromPattern();
        _fromPatternYearStart = true;
        return this;
    }

    protected void clearFromPattern() {
        _fromPatternDayStart = false;
        _fromPatternMonthStart = false;
        _fromPatternYearStart = false;
    }

    public FromToOption fromDateWithNoon() {
        clearFromDateWith();
        _fromDateWithNoon = true;
        return this;
    }

    public FromToOption fromDateWithHour(int hourOfDay) {
        clearFromDateWith();
        _fromDateWithHour = hourOfDay;
        return this;
    }

    protected void clearFromDateWith() {
        _fromDateWithNoon = false;
        _fromDateWithHour = null;
    }

    // -----------------------------------------------------
    //                                               To Date
    //                                               -------
    public FromToOption toPatternNextDayStart() {
        assertNotAdjustmentAfterPattern("toPatternNextDayStart");
        clearToPattern();
        _toPatternNextDayStart = true;
        return this;
    }

    public FromToOption toPatternNextMonthStart() {
        assertNotAdjustmentAfterPattern("toPatternNextMonthStart");
        clearToPattern();
        _toPatternNextMonthStart = true;
        return this;
    }

    public FromToOption toPatternNextYearStart() {
        assertNotAdjustmentAfterPattern("toPatternNextYearStart");
        clearToPattern();
        _toPatternNextYearStart = true;
        return this;
    }

    protected void clearToPattern() {
        _toPatternNextDayStart = false;
        _toPatternNextMonthStart = false;
        _toPatternNextYearStart = false;
    }

    public FromToOption toDateWithNoon() {
        clearToDateWith();
        _toDateWithNoon = true;
        return this;
    }

    public FromToOption toDateWithHour(int hourOfDay) {
        clearToDateWith();
        _toDateWithHour = hourOfDay;
        return this;
    }

    protected void clearToDateWith() {
        _toDateWithNoon = false;
        _toDateWithHour = null;
    }

    // -----------------------------------------------------
    //                                                Assert
    //                                                ------
    protected void assertNotAdjustmentAfterPattern(String option) {
        if (_usePattern) {
            String msg = "The option should not be call after pattern setting:";
            msg = msg + " option=" + option + "()";
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                       Internal Main
    //                                                                       =============
    /**
     * Filter the date as From. It requires this method is called before getFromDateConditionKey().
     * @param fromDate The date as From. (NullAllowed: If the value is null, it returns null)
     * @return The filtered date as From. (NullAllowed)
     */
    public Date filterFromDate(Date fromDate) {
        if (fromDate == null) {
            return null;
        }
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(fromDate.getTime());

        if (_fromPatternDayStart) {
            moveToCalendarDayStart(cal);
        } else if (_fromPatternMonthStart) {
            moveToCalendarMonthStart(cal);
        } else if (_fromPatternYearStart) {
            moveToCalendarYearStart(cal);
        }
        if (_fromDateWithNoon) {
            moveToNoon(cal);
        }
        if (_fromDateWithHour != null) {
            moveToHour(cal, _fromDateWithHour);
        }

        final Date cloneDate = (Date) fromDate.clone();
        cloneDate.setTime(cal.getTimeInMillis());
        fromDate = cloneDate;
        return fromDate;
    }

    /**
     * Filter the date as To. It requires this method is called before getToDateConditionKey().
     * @param toDate The date as To. (NullAllowed: If the value is null, it returns null)
     * @return The filtered date as To. (NullAllowed)
     */
    public Date filterToDate(Date toDate) {
        if (toDate == null) {
            return null;
        }
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(toDate.getTime());

        if (_toPatternNextDayStart) {
            moveToCalendarNextDayStart(cal);
        } else if (_toPatternNextMonthStart) {
            moveToCalendarNextMonthStart(cal);
        } else if (_toPatternNextYearStart) {
            moveToCalendarNextYearStart(cal);
        }
        if (_toDateWithNoon) {
            moveToNoon(cal);
        }
        if (_toDateWithHour != null) {
            moveToHour(cal, _toDateWithHour);
        }

        final Date cloneDate = (Date) toDate.clone();
        cloneDate.setTime(cal.getTimeInMillis());
        toDate = cloneDate;
        return toDate;
    }

    protected Date filterNoon(Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        moveToNoon(cal);
        final Date cloneDate = (Date) date.clone();
        cloneDate.setTime(cal.getTimeInMillis());
        return cloneDate;
    }

    /**
     * Get the condition-key of the from-date. It requires this method is called after filterFromDate().
     * @return The condition-key of the from-date. (NotNull)
     */
    public ConditionKey getFromDateConditionKey() {
        if (_greaterThan) {
            return ConditionKey.CK_GREATER_THAN;
        } else {
            return ConditionKey.CK_GREATER_EQUAL; // as default
        }
    }

    /**
     * Get the condition-key of the to-date. It requires this method is called after filterToDate().
     * @return The condition-key of the to-date. (NotNull)
     */
    public ConditionKey getToDateConditionKey() {
        if (_lessThan) {
            return ConditionKey.CK_LESS_THAN;
        } else {
            return ConditionKey.CK_LESS_EQUAL; // as default
        }
    }

    // ===================================================================================
    //                                                                     Calendar Helper
    //                                                                     ===============
    protected void addCalendarNextDay(Calendar cal) {
        cal.add(Calendar.DAY_OF_MONTH, 1);
    }

    protected void addCalendarNextMonth(Calendar cal) {
        cal.add(Calendar.MONTH, 1);
    }

    protected void addCalendarNextYear(Calendar cal) {
        cal.add(Calendar.YEAR, 1);
    }

    protected void moveToCalendarDayStart(Calendar cal) {
        clearCalendarHourMinuteSecondMilli(cal);
    }

    protected void moveToCalendarNextDayStart(Calendar cal) {
        addCalendarNextDay(cal);
        moveToCalendarDayStart(cal);
    }

    protected void moveToCalendarMonthStart(Calendar cal) {
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        clearCalendarHourMinuteSecondMilli(cal);
    }

    protected void moveToCalendarNextMonthStart(Calendar cal) {
        addCalendarNextMonth(cal);
        moveToCalendarMonthStart(cal);
    }

    protected void moveToCalendarYearStart(Calendar cal) {
        cal.set(Calendar.MONTH, cal.getActualMinimum(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        clearCalendarHourMinuteSecondMilli(cal);
    }

    protected void moveToCalendarNextYearStart(Calendar cal) {
        addCalendarNextYear(cal);
        moveToCalendarYearStart(cal);
    }

    protected void moveToNoon(Calendar cal) {
        cal.clear(Calendar.MILLISECOND);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MINUTE);
        cal.set(Calendar.HOUR_OF_DAY, 12);
    }

    protected void moveToHour(Calendar cal, int hourOfDay) {
        cal.clear(Calendar.MILLISECOND);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MINUTE);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
    }

    protected void clearCalendarHourMinuteSecondMilli(Calendar cal) {
        cal.clear(Calendar.MILLISECOND);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MINUTE);
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final String title = DfTypeUtil.toClassTitle(this);
        return title + ":{greaterThan=" + _greaterThan + ", lessThan=" + _lessThan + ", usePattern=" + _usePattern
                + "}";
    }
}
