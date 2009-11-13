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
package org.seasar.robot.dbflute.twowaysql;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.seasar.robot.dbflute.jdbc.ValueType;

/**
 * @author jflute
 */
public class DisplaySqlBuilder {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public static final String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String NULL = "null";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    private DisplaySqlBuilder() {
    }

    // ===================================================================================
    //                                                                        Complete SQL
    //                                                                        ============
    public static String buildDisplaySql(String sql, Object[] args, String logDateFormat, String logTimestampFormat) {
        if (args == null || args.length == 0) {
            return sql;
        }
        return buildDisplaySql(sql, args, new ValueType[args.length], logDateFormat, logTimestampFormat);
    }

    public static String buildDisplaySql(String sql, Object[] args, ValueType[] valueTypes, String logDateFormat,
            String logTimestampFormat) {
        if (args == null || args.length == 0) {
            return sql;
        }
        StringBuilder buf = new StringBuilder(sql.length() + args.length * 15);
        int pos = 0;
        int pos2 = 0;
        int pos3 = 0;
        int pos4 = 0;
        int pos5 = 0;
        int pos6 = 0;
        int index = 0;
        while (true) {
            pos = sql.indexOf('?', pos2);
            pos3 = sql.indexOf('\'', pos2);
            pos4 = sql.indexOf('\'', pos3 + 1);
            pos5 = sql.indexOf("/*", pos2);
            pos6 = sql.indexOf("*/", pos5 + 1);
            if (pos > 0) {
                if (pos3 >= 0 && pos3 < pos && pos < pos4) {
                    buf.append(sql.substring(pos2, pos4 + 1));
                    pos2 = pos4 + 1;
                } else if (pos5 >= 0 && pos5 < pos && pos < pos6) {
                    buf.append(sql.substring(pos2, pos6 + 1));
                    pos2 = pos6 + 1;
                } else {
                    if (args.length <= index) {
                        String msg = "The size of bind arguments is illegal:";
                        msg = msg + " size=" + args.length + " sql=" + sql;
                        throw new IllegalStateException(msg);
                    }
                    buf.append(sql.substring(pos2, pos));
                    buf.append(getBindVariableText(args[index], valueTypes[index], logDateFormat, logTimestampFormat));
                    pos2 = pos + 1;
                    index++;
                }
            } else {
                buf.append(sql.substring(pos2));
                break;
            }
        }
        return buf.toString();
    }

    // ===================================================================================
    //                                                                  Bind Variable Text
    //                                                                  ==================
    // For various seasar's version.
    protected static final Class<?>[] TOTEXT_ARGUMENT_TYPES = new Class<?>[] { Object.class };
    protected static final Method TOTEXT_METHOD;
    static {
        Method method = null;
        try {
            method = ValueType.class.getMethod("toText", TOTEXT_ARGUMENT_TYPES);
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        }
        TOTEXT_METHOD = method;
    }

    public static String getBindVariableText(Object bindVariable, ValueType valueType, String logDateFormat,
            String logTimestampFormat) {
        if (valueType != null && TOTEXT_METHOD != null) {
            try {
                return (String) TOTEXT_METHOD.invoke(valueType, new Object[] { bindVariable });
            } catch (IllegalArgumentException e) {
                String msg = "ValueType.toText() threw the IllegalArgumentException:";
                msg = msg + " valueType=" + valueType + " bindVariable=" + bindVariable;
                throw new IllegalStateException(msg, e);
            } catch (IllegalAccessException e) {
                String msg = "ValueType.toText() threw the IllegalAccessException:";
                msg = msg + " valueType=" + valueType + " bindVariable=" + bindVariable;
                throw new IllegalStateException(msg, e);
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof RuntimeException) {
                    throw (RuntimeException) e.getTargetException();
                } else {
                    String msg = "ValueType.toText() threw the exception:";
                    msg = msg + " valueType=" + valueType + " bindVariable=" + bindVariable;
                    throw new IllegalStateException(msg, e.getTargetException());
                }
            }
        }
        return getBindVariableText(bindVariable, logDateFormat, logTimestampFormat);
    }

    public static String getBindVariableText(Object bindVariable, String logDateFormat, String logTimestampFormat) {
        if (bindVariable instanceof String) {
            return quote(bindVariable.toString());
        } else if (bindVariable instanceof Number) {
            return bindVariable.toString();
        } else if (bindVariable instanceof Timestamp) {
            return buildTimestampText(bindVariable, logTimestampFormat);
        } else if (bindVariable instanceof Time) {
            return buildTimeText(bindVariable);
        } else if (bindVariable instanceof java.util.Date) {
            return buildDateText(bindVariable, logDateFormat);
        } else if (bindVariable instanceof Boolean) {
            return bindVariable.toString();
        } else if (bindVariable == null) {
            return NULL;
        } else {
            return quote(bindVariable.toString());
        }
    }

    protected static String buildTimestampText(Object bindVariable, String logTimestampFormat) {
        final String format = logTimestampFormat != null ? logTimestampFormat : DEFAULT_TIMESTAMP_FORMAT;
        final DateFormatResource resource = analyzeDateFormat(format);
        final DateFormat sdf = createDateFormat(resource);
        return quote(sdf.format((java.util.Date) bindVariable), resource);
    }

    protected static String buildTimeText(Object bindVariable) {
        final String defaultFormat = DEFAULT_TIME_FORMAT;
        final DateFormat sdf = createDateFormat(defaultFormat);
        return quote(sdf.format((java.util.Date) bindVariable));
    }

    protected static String buildDateText(Object bindVariable, String logDateFormat) {
        final String format = logDateFormat != null ? logDateFormat : DEFAULT_DATE_FORMAT;
        final DateFormatResource resource = analyzeDateFormat(format);
        final DateFormat sdf = createDateFormat(resource);
        return quote(sdf.format((java.util.Date) bindVariable), resource);
    }

    protected static DateFormat createDateFormat(String format) {
        return new SimpleDateFormat(format);
    }

    protected static DateFormat createDateFormat(DateFormatResource resource) {
        return new SimpleDateFormat(resource.getFormat());
    }

    /**
     * Analyze date format.
     * <pre>
     * ex)
     * o yyyy-MM-dd = '2009-10-27'
     * o date $df:{yyyy-MM-dd} = date '2009-10-27'
     * o prefix$df:{yyyy-MM-dd}suffix = prefix'2009-10-27'suffix
     * </pre>
     * @param format The string of format. (NotNull)
     * @return The resource of date format. (NotNull)
     */
    protected static DateFormatResource analyzeDateFormat(String format) {
        final DateFormatResource resource = new DateFormatResource();
        final String dfMark = "$df:{";
        final int dfMarkBeginIndex = format.indexOf(dfMark);
        if (dfMarkBeginIndex >= 0) {
            final String rear = format.substring(dfMarkBeginIndex + dfMark.length());
            final int dfMarkEndIndex = rear.indexOf("}");
            if (dfMarkEndIndex >= 0) {
                resource.setFormat(rear.substring(0, dfMarkEndIndex));
                resource.setPrefix(format.substring(0, dfMarkBeginIndex));
                resource.setSuffix(rear.substring(dfMarkEndIndex + "}".length()));
                return resource;
            }
        }
        resource.setFormat(format);
        return resource;
    }

    protected static class DateFormatResource {
        protected String _format;
        protected String _prefix;
        protected String _suffix;

        public String getFormat() {
            return _format;
        }

        public void setFormat(String format) {
            _format = format;
        }

        public String getPrefix() {
            return _prefix;
        }

        public void setPrefix(String prefix) {
            _prefix = prefix;
        }

        public String getSuffix() {
            return _suffix;
        }

        public void setSuffix(String suffix) {
            _suffix = suffix;
        }
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected static String quote(String text) {
        return "'" + text + "'"; // 'text'
    }

    protected static String quote(String text, DateFormatResource resource) {
        String result = quote(text);
        final String prefix = resource.getPrefix();
        if (prefix != null && prefix.trim().length() > 0) {
            result = prefix + result;
        }
        final String suffix = resource.getSuffix();
        if (suffix != null && suffix.trim().length() > 0) {
            result = result + suffix;
        }
        return result;
    }
}
