/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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

import java.util.Calendar;
import java.util.Date;

import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;

/**
 * The option of from-to.
 * <pre>
 * ex) fromDate:{2007/04/10 08:24:53} toDate:{2007/04/16 14:36:29}
 *
 *   - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 *   new FromToOption().compareAsDate(); 
 *     --&gt; column &gt;= '2007/04/10 00:00:00' and column &lt; '2007/04/17 00:00:00'
 *   - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 *
 *   new FromToOption(); 
 *     --&gt; column &gt;= '2007/04/10 08:24:53' and column &lt;= '2007/04/16 14:36:29'
 *
 *   new FromToOption().greaterThan(); 
 *     --&gt; column &gt; '2007/04/10 08:24:53' and column &lt;= '2007/04/16 14:36:29'
 *
 *   new FromToOption().lessThan(); 
 *     --&gt; column &gt;= '2007/04/10 08:24:53' and column &lt; '2007/04/16 14:36:29'
 *
 *   new FromToOption().greaterThan().lessThan(); 
 *     --&gt; column &gt; '2007/04/10 08:24:53' and column &lt; '2007/04/16 14:36:29'
 * 
 * </pre>
 * @author jflute
 */
public class FromToOption implements ConditionOption {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _fromDateGreaterThan;

    protected boolean _toDateLessThan;

    protected boolean _compareAsDate;

    // ===================================================================================
    //                                                            Interface Implementation
    //                                                            ========================
    public String getRearOption() {
        String msg = "Thie option does not use getRearOption()!";
        throw new UnsupportedOperationException(msg);
    }

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public FromToOption greaterThan() {
        _fromDateGreaterThan = true; return this;
    }

    public FromToOption lessThan() {
        _toDateLessThan = true; return this;
    }

    /**
     * Compare as date.
     * <pre>
     * ex) fromDate:{2007/04/10 08:24:53} toDate:{2007/04/16 14:36:29}
     *
     *   new FromToOption().compareAsDate();
     *     --&gt; column &gt;= '2007/04/10 00:00:00' and column &lt; '2007/04/17 00:00:00'
     * 
     * This method ignore greaterThan() and lessThan().
     * </pre>
     * @return this. (NotNull)
     */
    public FromToOption compareAsDate() {
        _compareAsDate = true; return this;
    }

    // ===================================================================================
    //                                                                       Internal Main
    //                                                                       =============
    /**
     * Filter the date as From. It requires this method is called before getFromDateConditionKey().
     * @param fromDate The date as From. (Nullable: If the value is null, it returns null)
     * @return The filtered date as From. (Nullable)
     */
    public Date filterFromDate(Date fromDate) {
        if (fromDate == null) {
            return null;
        }
        if (_compareAsDate) {
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(fromDate.getTime());
            clearCalendarHourMinuteSecondMilli(cal);
            final Date cloneDate = (Date) fromDate.clone();
            cloneDate.setTime(cal.getTimeInMillis());
            return cloneDate;
        }
        return fromDate;
    }

    /**
     * Filter the date as To. It requires this method is called before getToDateConditionKey().
     * @param toDate The date as To. (Nullable: If the value is null, it returns null)
     * @return The filtered date as To. (Nullable)
     */
    public Date filterToDate(Date toDate) {
        if (toDate == null) {
            return null;
        }
        if (_compareAsDate) {
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(toDate.getTime());
            clearCalendarHourMinuteSecondMilli(cal);
            addCalendarNextDay(cal);// Key Point!
            final Date cloneDate = (Date) toDate.clone();
            cloneDate.setTime(cal.getTimeInMillis());
            return cloneDate;
        }
        return toDate;
    }

    /**
     * Get the condition-key of the from-date. It requires this method is called after filterFromDate().
     * @return The condition-key of the from-date. (NotNull)
     */
    public ConditionKey getFromDateConditionKey() {
        if (_compareAsDate) {
            return ConditionKey.CK_GREATER_EQUAL;
        }
        if (_fromDateGreaterThan) {
            return ConditionKey.CK_GREATER_THAN;// Default!
        } else {
            return ConditionKey.CK_GREATER_EQUAL;// Default!
        }
    }

    /**
     * Get the condition-key of the to-date. It requires this method is called after filterToDate().
     * @return The condition-key of the to-date. (NotNull)
     */
    public ConditionKey getToDateConditionKey() {
        if (_compareAsDate) {
            return ConditionKey.CK_LESS_THAN;
        }
        if (_toDateLessThan) {
            return ConditionKey.CK_LESS_THAN;// Default!
        } else {
            return ConditionKey.CK_LESS_EQUAL;// Default!
        }
    }

    // ===================================================================================
    //                                                                     Calendar Helper
    //                                                                     ===============
    protected void addCalendarNextDay(Calendar cal) {
        cal.add(Calendar.DAY_OF_MONTH, 1);
    }

    protected void clearCalendarHourMinuteSecondMilli(Calendar cal) {
        cal.clear(Calendar.MILLISECOND);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MINUTE);
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
    }
}
