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
package org.seasar.robot.dbflute.s2dao.valuetype.plugin;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;

import org.seasar.robot.dbflute.s2dao.valuetype.registered.TimeType;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class CalendarTimeType extends TimeType {

    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        return toCalendar(super.getValue(resultSet, index));
    }

    public Object getValue(ResultSet resultSet, String columnName) throws SQLException {
        return toCalendar(super.getValue(resultSet, columnName));
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return toCalendar(super.getValue(cs, index));
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        return toCalendar(super.getValue(cs, parameterName));
    }

    protected Calendar toCalendar(Object value) {
        return DfTypeUtil.toCalendar(value);
    }

    protected Time toTime(Object value) {
        Calendar calendar = DfTypeUtil.localize(toCalendar(value));
        Calendar base = Calendar.getInstance();
        base.set(Calendar.YEAR, 1970);
        base.set(Calendar.MONTH, Calendar.JANUARY);
        base.set(Calendar.DATE, 1);
        base.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        base.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        base.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
        base.set(Calendar.MILLISECOND, calendar.get(Calendar.MILLISECOND));
        return new Time(base.getTimeInMillis());
    }
}
