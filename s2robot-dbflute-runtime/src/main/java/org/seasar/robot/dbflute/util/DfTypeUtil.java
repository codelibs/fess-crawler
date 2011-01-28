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
package org.seasar.robot.dbflute.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public final class DfTypeUtil {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String NULL = "null";
    protected static final long AD_ORIGIN_MILLISECOND;
    static {
        final Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(1, 0, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // AD0001/01/01 00:00:00.000
        AD_ORIGIN_MILLISECOND = cal.getTimeInMillis();

        // *the value of millisecond may depend on JDK implementation
    }

    private static final char[] ENCODE_TABLE = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/' };

    private static final char PAD = '=';

    private static final byte[] DECODE_TABLE = new byte[128];
    static {
        for (int i = 0; i < DECODE_TABLE.length; i++) {
            DECODE_TABLE[i] = Byte.MAX_VALUE;
        }
        for (int i = 0; i < ENCODE_TABLE.length; i++) {
            DECODE_TABLE[ENCODE_TABLE[i]] = (byte) i;
        }
    }

    // ===================================================================================
    //                                                                              String
    //                                                                              ======
    /**
     * Convert the object to the instance that is string. <br />
     * If the object is a byte array, encode as base64.
     * @param obj The parsed object. (NullAllowed)
     * @return The instance of string. (NullAllowed)
     */
    public static String toString(Object obj) {
        return toString(obj, null);
    }

    /**
     * Convert the object to the instance that is string. <br />
     * If the object is a byte array, encode as base64.
     * @param obj The parsed object. (NullAllowed)
     * @param pattern The pattern format to parse. (NullAllowed)
     * @return The instance of string. (NullAllowed)
     */
    public static String toString(Object obj, String pattern) {
        if (obj == null) {
            return null;
        } else if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Date) {
            return toStringFromDate((Date) obj, pattern);
        } else if (obj instanceof Number) {
            return toStringFromNumber((Number) obj, pattern);
        } else if (obj instanceof Calendar) {
            return toStringFromDate(((Calendar) obj).getTime(), pattern);
        } else if (obj instanceof byte[]) {
            return encodeAsBase64((byte[]) obj);
        } else {
            return obj.toString();
        }
    }

    protected static String toStringFromNumber(Number value, String pattern) {
        if (value != null) {
            if (pattern != null) {
                return createDecimalFormat(pattern).format(value);
            }
            return value.toString();
        }
        return null;
    }

    protected static String toStringFromDate(Date value, String pattern) {
        if (value != null) {
            if (pattern != null) {
                return createDateFormat(pattern).format(value);
            }
            return value.toString();
        }
        return null;
    }

    public static byte[] toStringBytes(String str, String encoding) {
        if (str == null) {
            return null;
        }
        try {
            return str.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            String msg = "The encoding is invalid: encoding=" + encoding + " str=" + str;
            throw new IllegalStateException(msg);
        }
    }

    /**
     * Convert the object to class title name.
     * <pre>
     * o com.example.Foo to Foo
     * o com.example.Foo$Bar to Foo$Bar
     * o com.example.Foo$1 to Foo$1
     * o Foo to Foo
     * o Foo$Bar to Foo$Bar
     * </pre>
     * If the object is Class, it uses Class.getName() as convert target string. <br />
     * If the object is String, it uses it directly as convert target string. <br />
     * If the object is the other object, it uses obj.getClass().getName() as convert target string.
     * @param obj The target object. String or Class are treated as special. (NullAllowed: if null, returns null)
     * @return The string as class title. (NullAllowed: when the argument is null)
     */
    public static String toClassTitle(Object obj) {
        if (obj == null) {
            return null;
        }
        final String fqcn;
        if (obj instanceof Class<?>) {
            fqcn = ((Class<?>) obj).getName();
        } else if (obj instanceof String) {
            fqcn = (String) obj;
        } else {
            fqcn = obj.getClass().getName();
        }
        if (fqcn == null || fqcn.trim().length() == 0) {
            return fqcn;
        }
        final int dotLastIndex = fqcn.lastIndexOf(".");
        if (dotLastIndex < 0) {
            return fqcn;
        }
        return fqcn.substring(dotLastIndex + ".".length());
    }

    public static String encodeAsBase64(final byte[] inData) {
        if (inData == null || inData.length == 0) {
            return "";
        }
        int mod = inData.length % 3;
        int num = inData.length / 3;
        char[] outData = null;
        if (mod != 0) {
            outData = new char[(num + 1) * 4];
        } else {
            outData = new char[num * 4];
        }
        for (int i = 0; i < num; i++) {
            encode(inData, i * 3, outData, i * 4);
        }
        switch (mod) {
        case 1:
            encode2pad(inData, num * 3, outData, num * 4);
            break;
        case 2:
            encode1pad(inData, num * 3, outData, num * 4);
            break;
        }
        return new String(outData);
    }

    public static byte[] decodeAsBase64(final String inData) {
        int num = (inData.length() / 4) - 1;
        int lastBytes = getLastBytes(inData);
        byte[] outData = new byte[num * 3 + lastBytes];
        for (int i = 0; i < num; i++) {
            decode(inData, i * 4, outData, i * 3);
        }
        switch (lastBytes) {
        case 1:
            decode1byte(inData, num * 4, outData, num * 3);
            break;
        case 2:
            decode2byte(inData, num * 4, outData, num * 3);
            break;
        default:
            decode(inData, num * 4, outData, num * 3);
        }
        return outData;
    }

    private static void encode(final byte[] inData, final int inIndex, final char[] outData, final int outIndex) {
        int i = ((inData[inIndex] & 0xff) << 16) + ((inData[inIndex + 1] & 0xff) << 8) + (inData[inIndex + 2] & 0xff);
        outData[outIndex] = ENCODE_TABLE[i >> 18];
        outData[outIndex + 1] = ENCODE_TABLE[(i >> 12) & 0x3f];
        outData[outIndex + 2] = ENCODE_TABLE[(i >> 6) & 0x3f];
        outData[outIndex + 3] = ENCODE_TABLE[i & 0x3f];
    }

    private static void encode2pad(final byte[] inData, final int inIndex, final char[] outData, final int outIndex) {
        int i = inData[inIndex] & 0xff;
        outData[outIndex] = ENCODE_TABLE[i >> 2];
        outData[outIndex + 1] = ENCODE_TABLE[(i << 4) & 0x3f];
        outData[outIndex + 2] = PAD;
        outData[outIndex + 3] = PAD;
    }

    private static void encode1pad(final byte[] inData, final int inIndex, final char[] outData, final int outIndex) {
        int i = ((inData[inIndex] & 0xff) << 8) + (inData[inIndex + 1] & 0xff);
        outData[outIndex] = ENCODE_TABLE[i >> 10];
        outData[outIndex + 1] = ENCODE_TABLE[(i >> 4) & 0x3f];
        outData[outIndex + 2] = ENCODE_TABLE[(i << 2) & 0x3f];
        outData[outIndex + 3] = PAD;
    }

    private static void decode(final String inData, final int inIndex, final byte[] outData, final int outIndex) {
        byte b0 = DECODE_TABLE[inData.charAt(inIndex)];
        byte b1 = DECODE_TABLE[inData.charAt(inIndex + 1)];
        byte b2 = DECODE_TABLE[inData.charAt(inIndex + 2)];
        byte b3 = DECODE_TABLE[inData.charAt(inIndex + 3)];
        outData[outIndex] = (byte) (b0 << 2 & 0xfc | b1 >> 4 & 0x3);
        outData[outIndex + 1] = (byte) (b1 << 4 & 0xf0 | b2 >> 2 & 0xf);
        outData[outIndex + 2] = (byte) (b2 << 6 & 0xc0 | b3 & 0x3f);
    }

    private static void decode1byte(final String inData, final int inIndex, final byte[] outData, final int outIndex) {
        byte b0 = DECODE_TABLE[inData.charAt(inIndex)];
        byte b1 = DECODE_TABLE[inData.charAt(inIndex + 1)];
        outData[outIndex] = (byte) (b0 << 2 & 0xfc | b1 >> 4 & 0x3);
    }

    private static void decode2byte(final String inData, final int inIndex, final byte[] outData, final int outIndex) {
        byte b0 = DECODE_TABLE[inData.charAt(inIndex)];
        byte b1 = DECODE_TABLE[inData.charAt(inIndex + 1)];
        byte b2 = DECODE_TABLE[inData.charAt(inIndex + 2)];
        outData[outIndex] = (byte) (b0 << 2 & 0xfc | b1 >> 4 & 0x3);
        outData[outIndex + 1] = (byte) (b1 << 4 & 0xf0 | b2 >> 2 & 0xf);
    }

    private static int getLastBytes(final String inData) {
        int len = inData.length();
        if (inData.charAt(len - 2) == PAD) {
            return 1;
        } else if (inData.charAt(len - 1) == PAD) {
            return 2;
        } else {
            return 3;
        }
    }

    // ===================================================================================
    //                                                                              Number
    //                                                                              ======
    /**
     * Convert to number object.
     * @param obj The resource of number. (NullAllowed: if null, returns null)
     * @param type The type of number. (NotNull)
     * @return The number object from resource. (NullAllowed: if type is not number, returns null)
     */
    public static Number toNumber(Object obj, Class<?> type) {
        if (obj == null) {
            return null;
        }
        // Integer, Long and BigDecimal are prior
        if (type == Integer.class) {
            return toInteger(obj);
        } else if (type == Long.class) {
            return toLong(obj);
        } else if (type == BigDecimal.class) {
            return toBigDecimal(obj);
        } else if (type == Double.class) {
            return toDouble(obj);
        } else if (type == Float.class) {
            return toFloat(obj);
        } else if (type == Short.class) {
            return toShort(obj);
        } else if (type == Byte.class) {
            return toByte(obj);
        } else if (type == BigInteger.class) {
            return toBigInteger(obj);
        }
        return null; // could not convert
    }

    // -----------------------------------------------------
    //                                             Normalize
    //                                             ---------
    protected static String normalize(String value) {
        return normalize(value, Locale.getDefault());
    }

    protected static String normalize(String value, Locale locale) {
        if (value == null) {
            return null;
        }
        final DecimalFormatSymbols symbols = getDecimalFormatSymbols(locale);
        final char groupingSep = symbols.getGroupingSeparator();
        final char decimalSep = symbols.getDecimalSeparator();
        final StringBuilder sb = new StringBuilder(20);
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            if (c == groupingSep) {
                continue;
            } else if (c == decimalSep) {
                c = '.';
            }
            sb.append(c);
        }
        return sb.toString();
    }

    // -----------------------------------------------------
    //                                          NumberFormat
    //                                          ------------
    public static DecimalFormat createDecimalFormat(String pattern) {
        return new DecimalFormat(pattern);
    }

    // ===================================================================================
    //                                                                             Integer
    //                                                                             =======
    /**
     * @param obj The resource value to integer. (NullAllowed)
     * @return The value as integer. (NullAllowed)
     * @throws NumberFormatException
     */
    public static Integer toInteger(Object obj) {
        return toInteger(obj, null);
    }

    public static Integer toInteger(Object obj, String pattern) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Number) {
            return Integer.valueOf(((Number) obj).intValue());
        } else if (obj instanceof String) {
            return toInteger((String) obj);
        } else if (obj instanceof java.util.Date) {
            if (pattern != null) {
                return Integer.valueOf(createDateFormat(pattern).format(obj));
            }
            return Integer.valueOf((int) ((java.util.Date) obj).getTime());
        } else if (obj instanceof Boolean) {
            return ((Boolean) obj).booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
        } else if (obj instanceof byte[]) {
            return toInteger(toSerializable((byte[]) obj)); // recursive
        } else {
            return toInteger(obj.toString());
        }
    }

    protected static Integer toInteger(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        return Integer.valueOf(normalize(str));
    }

    public static int toPrimitiveInt(Object obj) {
        return toPrimitiveInt(obj, null);
    }

    public static int toPrimitiveInt(Object obj, String pattern) {
        Integer wrapper = toInteger(obj, pattern);
        return wrapper != null ? wrapper.intValue() : 0;
    }

    // ===================================================================================
    //                                                                                Long
    //                                                                                ====
    public static Long toLong(Object obj) {
        return toLong(obj, null);
    }

    public static Long toLong(Object obj, String pattern) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Long) {
            return (Long) obj;
        } else if (obj instanceof Number) {
            return Long.valueOf(((Number) obj).longValue());
        } else if (obj instanceof String) {
            return toLong((String) obj);
        } else if (obj instanceof java.util.Date) {
            if (pattern != null) {
                return Long.valueOf(createDateFormat(pattern).format(obj));
            }
            return Long.valueOf(((java.util.Date) obj).getTime());
        } else if (obj instanceof Boolean) {
            return ((Boolean) obj).booleanValue() ? Long.valueOf(1) : Long.valueOf(0);
        } else if (obj instanceof byte[]) {
            return toLong(toSerializable((byte[]) obj)); // recursive
        } else {
            return toLong(obj.toString());
        }
    }

    protected static Long toLong(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        return new Long(normalize(str));
    }

    public static long toPrimitiveLong(Object obj) {
        return toPrimitiveLong(obj, null);
    }

    public static long toPrimitiveLong(Object obj, String pattern) {
        Long wrapper = toLong(obj, pattern);
        return wrapper != null ? wrapper.longValue() : 0L;
    }

    // ===================================================================================
    //                                                                              Double
    //                                                                              ======
    public static Double toDouble(Object obj) {
        return toDouble(obj, null);
    }

    public static Double toDouble(Object obj, String pattern) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Double) {
            return (Double) obj;
        } else if (obj instanceof Number) {
            return Double.valueOf(((Number) obj).doubleValue());
        } else if (obj instanceof String) {
            return toDouble((String) obj);
        } else if (obj instanceof java.util.Date) {
            if (pattern != null) {
                return Double.valueOf(createDateFormat(pattern).format(obj));
            }
            return Double.valueOf(((java.util.Date) obj).getTime());
        } else if (obj instanceof byte[]) {
            return toDouble(toSerializable((byte[]) obj)); // recursive
        } else {
            return toDouble(obj.toString());
        }
    }

    protected static Double toDouble(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        return new Double(normalize(str));
    }

    public static double toPrimitiveDouble(Object obj) {
        return toPrimitiveDouble(obj, null);
    }

    public static double toPrimitiveDouble(Object obj, String pattern) {
        Double wrapper = toDouble(obj, pattern);
        return wrapper != null ? wrapper.doubleValue() : 0;
    }

    // ===================================================================================
    //                                                                               Float
    //                                                                               =====
    public static Float toFloat(Object obj) {
        return toFloat(obj, null);
    }

    public static Float toFloat(Object obj, String pattern) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Float) {
            return (Float) obj;
        } else if (obj instanceof Number) {
            return Float.valueOf(((Number) obj).floatValue());
        } else if (obj instanceof String) {
            return toFloat((String) obj);
        } else if (obj instanceof java.util.Date) {
            if (pattern != null) {
                return Float.valueOf(createDateFormat(pattern).format(obj));
            }
            return Float.valueOf(((java.util.Date) obj).getTime());
        } else if (obj instanceof byte[]) {
            return toFloat(toSerializable((byte[]) obj)); // recursive
        } else {
            return toFloat(obj.toString());
        }
    }

    protected static Float toFloat(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        return new Float(normalize(str));
    }

    public static float toPrimitiveFloat(Object obj) {
        return toPrimitiveFloat(obj, null);
    }

    public static float toPrimitiveFloat(Object obj, String pattern) {
        Float wrapper = toFloat(obj, pattern);
        return wrapper != null ? wrapper.floatValue() : 0;
    }

    // ===================================================================================
    //                                                                               Short
    //                                                                               =====
    public static Short toShort(Object obj) {
        return toShort(obj, null);
    }

    public static Short toShort(Object obj, String pattern) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Short) {
            return (Short) obj;
        } else if (obj instanceof Number) {
            return Short.valueOf(((Number) obj).shortValue());
        } else if (obj instanceof String) {
            return toShort((String) obj);
        } else if (obj instanceof java.util.Date) {
            if (pattern != null) {
                return Short.valueOf(createDateFormat(pattern).format(obj));
            }
            return Short.valueOf((short) ((java.util.Date) obj).getTime());
        } else if (obj instanceof Boolean) {
            return ((Boolean) obj).booleanValue() ? Short.valueOf((short) 1) : Short.valueOf((short) 0);
        } else if (obj instanceof byte[]) {
            return toShort(toSerializable((byte[]) obj)); // recursive
        } else {
            return toShort(obj.toString());
        }
    }

    protected static Short toShort(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        return new Short(normalize(str));
    }

    public static short toPrimitiveShort(Object obj) {
        return toPrimitiveShort(obj, null);
    }

    public static short toPrimitiveShort(Object obj, String pattern) {
        Short wrapper = toShort(obj, pattern);
        return wrapper != null ? wrapper.shortValue() : 0;
    }

    // ===================================================================================
    //                                                                                Byte
    //                                                                                ====
    public static Byte toByte(Object obj) {
        return toByte(obj, null);
    }

    public static Byte toByte(Object obj, String pattern) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Byte) {
            return (Byte) obj;
        } else if (obj instanceof Number) {
            return Byte.valueOf(((Number) obj).byteValue());
        } else if (obj instanceof String) {
            return toByte((String) obj);
        } else if (obj instanceof java.util.Date) {
            if (pattern != null) {
                return Byte.valueOf(createDateFormat(pattern).format(obj));
            }
            return Byte.valueOf((byte) ((java.util.Date) obj).getTime());
        } else if (obj instanceof Boolean) {
            return ((Boolean) obj).booleanValue() ? Byte.valueOf((byte) 1) : Byte.valueOf((byte) 0);
        } else if (obj instanceof byte[]) {
            return toByte(toSerializable((byte[]) obj)); // recursive
        } else {
            return toByte(obj.toString());
        }
    }

    protected static Byte toByte(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        return new Byte(normalize(str));
    }

    public static byte toPrimitiveByte(Object obj) {
        return toPrimitiveByte(obj, null);
    }

    public static byte toPrimitiveByte(Object obj, String pattern) {
        Byte wrapper = toByte(obj, pattern);
        return wrapper != null ? wrapper.byteValue() : 0;
    }

    // ===================================================================================
    //                                                                          BigDecimal
    //                                                                          ==========
    public static BigDecimal toBigDecimal(Object obj) {
        return toBigDecimal(obj, null);
    }

    public static BigDecimal toBigDecimal(Object obj, String pattern) {
        if (obj == null) {
            return null;
        } else if (obj instanceof BigDecimal) {
            final BigDecimal paramBigDecimal = (BigDecimal) obj;
            if (BigDecimal.class.equals(paramBigDecimal.getClass())) { // pure big-decimal
                return paramBigDecimal;
            } else { // sub class
                // because the big-decimal type is not final class.
                return new BigDecimal(paramBigDecimal.toPlainString());
            }
        } else if (obj instanceof java.util.Date) {
            if (pattern != null) {
                return new BigDecimal(createDateFormat(pattern).format(obj));
            }
            return BigDecimal.valueOf(((java.util.Date) obj).getTime());
        } else if (obj instanceof String) {
            String s = (String) obj;
            if (s == null || s.trim().length() == 0) {
                return null;
            }
            return new BigDecimal(new BigDecimal(s).toPlainString());
        } else if (obj instanceof byte[]) {
            return toBigDecimal(toSerializable((byte[]) obj)); // recursive
        } else {
            return new BigDecimal(new BigDecimal(obj.toString()).toPlainString());
        }
    }

    // ===================================================================================
    //                                                                          BigInteger
    //                                                                          ==========
    public static BigInteger toBigInteger(Object obj) {
        return toBigInteger(obj, null);
    }

    public static BigInteger toBigInteger(Object obj, String pattern) {
        if (obj == null) {
            return null;
        } else if (obj instanceof BigInteger) {
            final BigInteger paramBigInteger = (BigInteger) obj;
            if (BigInteger.class.equals(paramBigInteger.getClass())) { // pure big-integer
                return paramBigInteger;
            } else { // sub class
                // because the big-integer type is not final class.
                return BigInteger.valueOf(paramBigInteger.longValue());
            }
        } else {
            Long l = toLong(obj, pattern);
            if (l == null) {
                return null;
            }
            return BigInteger.valueOf(l.longValue());
        }
    }

    // ===================================================================================
    //                                                                          Point Date
    //                                                                          ==========
    /**
     * Convert to point date object.
     * @param obj The resource of number. (NullAllowed: if null, returns null)
     * @param type The type of number. (NotNull)
     * @return The point date object from resource. (NullAllowed: if type is not date, returns null)
     */
    public static Date toPointDate(Object obj, Class<?> type) {
        if (obj == null) {
            return null;
        }
        if (java.sql.Date.class.isAssignableFrom(type)) {
            return toSqlDate(obj);
        } else if (java.sql.Timestamp.class.isAssignableFrom(type)) {
            return toTimestamp(obj);
        } else if (java.sql.Time.class.isAssignableFrom(type)) {
            return toTime(obj);
        } else if (Date.class.isAssignableFrom(type)) {
            return toDate(obj);
        }
        return null; // could not convert
    }

    // ===================================================================================
    //                                                                          (util)Date
    //                                                                          ==========
    /**
     * Convert the object to the instance that is date. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses default date pattern based on 'yyyy-MM-dd HH:mm:ss.SSS'
     * with flexible-parsing if the object is string type.
     * @param obj The parsed object. (NullAllowed)
     * @return The instance of date. (NullAllowed)
     * @throws ParseDateException When it failed to parse the string to date.
     * @throws ParseDateNumberFormatException When it failed to format the elements as number.
     * @throws ParseDateOutOfCalendarException When the date was out of calendar. (if BC, not thrown)
     */
    public static Date toDate(Object obj) {
        return toDate(obj, (String) null);
    }

    /**
     * Convert the object to the instance that is date. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses specified date pattern when the pattern is not null
     * if the object is string type. If it's null, it uses default date pattern
     * with flexible-parsing based on 'yyyy-MM-dd HH:mm:ss.SSS'.
     * @param obj The parsed object. (NullAllowed)
     * @param pattern The pattern format to parse. (NullAllowed)
     * @return The instance of date. (NullAllowed)
     * @throws ParseDateException When it failed to parse the string to date.
     * @throws ParseDateNumberFormatException When it failed to format the elements as number.
     * @throws ParseDateOutOfCalendarException When the date was out of calendar. (if BC, not thrown)
     */
    public static Date toDate(Object obj, String pattern) {
        if (obj == null) {
            return null;
        } else if (obj instanceof String) {
            return toDateFromString((String) obj, pattern);
        } else if (obj instanceof Date) {
            final Date paramDate = (Date) obj;
            if (Date.class.equals(paramDate.getClass())) { // pure date
                return paramDate;
            } else { // sub class
                // because the Date is not final class.
                final Date date = new Date();
                date.setTime(paramDate.getTime());
                return date;
            }
        } else if (obj instanceof Calendar) {
            return ((Calendar) obj).getTime();
        } else if (obj instanceof byte[]) {
            return toDate(toSerializable((byte[]) obj)); // recursive
        } else if (obj instanceof Long) {
            return new Date((Long) obj);
        } else {
            return toDateFromString(obj.toString(), pattern);
        }
    }

    protected static Date toDateFromString(String str, String pattern) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        boolean strict;
        if (pattern == null || pattern.trim().length() == 0) { // flexibly
            // after all, includes when date too
            // because date type can have millisecond formally
            final boolean includeMilli = true;

            str = filterDateStringValueFlexibly(str, includeMilli);
            strict = !str.startsWith("-"); // not BC
            pattern = "yyyy-MM-dd HH:mm:ss.SSS";
        } else {
            strict = true;
        }
        final DateFormat df = createDateFormat(pattern, strict);
        try {
            return df.parse(str);
        } catch (ParseException e) {
            try {
                df.setLenient(true);
                df.parse(str); // no exception means illegal date
                String msg = "The date expression is out of calendar:";
                msg = msg + " string=" + str + " pattern=" + pattern;
                throw new ParseDateOutOfCalendarException(msg, e);
            } catch (ParseException ignored) {
                String msg = "Failed to parse the string to date:";
                msg = msg + " string=" + str + " pattern=" + pattern;
                throw new ParseDateException(msg, e);
            }
        }
    }

    protected static String filterDateStringValueFlexibly(final String pureStr, boolean includeMilli) {
        final String dateLiteralPrefix = "date ";
        final String bcSymbolPrefix = "-";
        final String adLatinPrefix = "AD";
        final String adLatinDotPrefix = "A.D.";
        final String bcLatinPrefix = "BC";
        final String bcLatinDotPrefix = "B.C.";
        final String dateDlm = "-";
        final String dateTimeDlm = " ";
        final String timeDlm = ":";
        final String timeMilliDlm = ".";
        String value = pureStr;
        value = value.trim();

        final boolean dateLiteral = value.startsWith(dateLiteralPrefix);
        if (dateLiteral) {
            value = value.substring(dateLiteralPrefix.length());
            value = value.trim();
        }

        // handling AD/BC prefix
        final boolean bc;
        {
            if (value.startsWith(adLatinPrefix)) {
                value = value.substring(adLatinPrefix.length());
                bc = false;
            } else if (value.startsWith(adLatinDotPrefix)) {
                value = value.substring(adLatinDotPrefix.length());
                bc = false;
            } else if (value.startsWith(bcLatinPrefix)) {
                value = value.substring(bcLatinPrefix.length());
                bc = true;
            } else if (value.startsWith(bcLatinDotPrefix)) {
                value = value.substring(bcLatinDotPrefix.length());
                bc = true;
            } else if (value.startsWith(bcSymbolPrefix)) {
                value = value.substring(bcSymbolPrefix.length());
                bc = true;
            } else {
                bc = false;
            }
            value = value.trim();
        }

        // handling slash delimiter for yyyyMMdd
        value = value.replaceAll("/", dateDlm);

        // handling 'date 20090119' and 'date 8631230' and so on
        if (dateLiteral && value.length() <= 8 && !value.contains(dateDlm)) {
            if (value.length() >= 5) {
                value = resolveDateElementZeroPrefix(value, 8 - value.length());
                final String yyyy = value.substring(0, 4);
                final String mm = value.substring(4, 6);
                final String dd = value.substring(6, 8);
                value = yyyy + dateDlm + mm + dateDlm + dd;
            } else {
                return pureStr; // couldn't filter for example '1234'
            }
        }

        // check whether it can filter
        if (!value.contains("-") || (value.indexOf("-") == value.lastIndexOf("-"))) {
            return pureStr; // couldn't filter for example '123456789' and '1234-123'
        }

        // handling zero prefix
        final int yearEndIndex = value.indexOf(dateDlm);
        String yyyy = value.substring(0, yearEndIndex);
        yyyy = resolveDateElementZeroPrefix(yyyy, 4 - yyyy.length());
        if (bc) {
            final Integer yyyyInt = formatDateElementAsNumber(yyyy, "yyyy", pureStr);
            yyyy = String.valueOf(yyyyInt - 1); // because DateFormat treats '-2007' as 'BC2008'
            yyyy = resolveDateElementZeroPrefix(yyyy, 4 - yyyy.length());
        } else {
            formatDateElementAsNumber(yyyy, "yyyy", pureStr); // check only
        }

        final String startsMon = value.substring(yearEndIndex + dateDlm.length());
        final int monthEndIndex = startsMon.indexOf(dateDlm);
        String mm = startsMon.substring(0, monthEndIndex);
        mm = resolveDateElementZeroPrefix(mm, 2 - mm.length());
        formatDateElementAsNumber(mm, "MM", pureStr); // check only

        final String startsDay = startsMon.substring(monthEndIndex + dateDlm.length());
        final int dayEndIndex = startsDay.indexOf(dateTimeDlm);
        String dd = dayEndIndex >= 0 ? startsDay.substring(0, dayEndIndex) : startsDay;
        dd = resolveDateElementZeroPrefix(dd, 2 - dd.length());
        formatDateElementAsNumber(dd, "dd", pureStr); // check only
        final String yyyy_MM_dd = yyyy + dateDlm + mm + dateDlm + dd;

        if (dayEndIndex >= 0) { // has time parts
            final String time = startsDay.substring(dayEndIndex + dateTimeDlm.length());

            // check whether it can filter
            if (!time.contains(timeDlm) || (time.indexOf(timeDlm) == time.lastIndexOf(timeDlm))) {
                return pureStr; // couldn't filter for example '2009-12-12 123451' and '2009-12-12 123:451'
            }

            value = yyyy_MM_dd + dateTimeDlm + handleTimeZeroPrefix(time, pureStr, includeMilli);
        } else {
            value = yyyy_MM_dd + dateTimeDlm + "00:00:00";
            if (includeMilli) {
                value = value + timeMilliDlm + "000";
            }
        }
        return (bc ? bcSymbolPrefix : "") + value;
    }

    protected static String handleTimeZeroPrefix(String time, String pureStr, boolean includeMilli) {
        final String timeDlm = ":";
        final String timeMilliDlm = ".";

        final int hourEndIndex = time.indexOf(timeDlm);
        String hour = time.substring(0, hourEndIndex);
        hour = resolveDateElementZeroPrefix(hour, 2 - hour.length());
        formatDateElementAsNumber(hour, "HH", pureStr); // check only

        final String startsMin = time.substring(hourEndIndex + timeDlm.length());
        final int minEndIndex = startsMin.indexOf(timeDlm);
        String min = startsMin.substring(0, minEndIndex);
        min = resolveDateElementZeroPrefix(min, 2 - min.length());
        formatDateElementAsNumber(min, "mm", pureStr); // check only

        final String startsSec = startsMin.substring(minEndIndex + timeDlm.length());
        final int secEndIndex = startsSec.indexOf(timeMilliDlm);
        String sec = secEndIndex >= 0 ? startsSec.substring(0, secEndIndex) : startsSec;
        sec = resolveDateElementZeroPrefix(sec, 2 - sec.length());
        formatDateElementAsNumber(sec, "ss", pureStr); // check only

        String value = hour + timeDlm + min + timeDlm + sec;
        if (includeMilli) {
            if (secEndIndex >= 0) {
                final String milli = startsSec.substring(secEndIndex + timeMilliDlm.length());
                resolveDateElementZeroPrefix(milli, 3 - milli.length());
                formatDateElementAsNumber(milli, "SSS", pureStr); // check only
                value = value + timeMilliDlm + milli; // append millisecond
            } else {
                value = value + timeMilliDlm + "000";
            }
        }
        return value;
    }

    protected static Integer formatDateElementAsNumber(String str, String title, String pureValue) {
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            String msg = "Failed to format " + title + " as number:";
            msg = msg + " " + title + "=" + str + " value=" + pureValue;
            throw new ParseDateNumberFormatException(msg, e);
        }
    }

    protected static String resolveDateElementZeroPrefix(String str, int count) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("0");
        }
        return sb.toString() + str;
    }

    public static class ParseDateException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ParseDateException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ParseDateNumberFormatException extends ParseDateException {
        private static final long serialVersionUID = 1L;

        public ParseDateNumberFormatException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ParseDateOutOfCalendarException extends ParseDateException {
        private static final long serialVersionUID = 1L;

        public ParseDateOutOfCalendarException(String msg, Exception e) {
            super(msg, e);
        }
    }

    // -----------------------------------------------------
    //                                         Determination
    //                                         -------------
    public static boolean isDateAD(Date date) {
        return date.getTime() >= AD_ORIGIN_MILLISECOND;
    }

    public static boolean isDateBC(Date date) {
        return date.getTime() < AD_ORIGIN_MILLISECOND;
    }

    // -----------------------------------------------------
    //                                          Manipulation
    //                                          ------------
    public static void addDateYear(Date date, int year) {
        final Calendar cal = toCalendar(date);
        addCalendarYear(cal, year);
        date.setTime(cal.getTimeInMillis());
    }

    public static void addDateMonth(Date date, int month) {
        final Calendar cal = toCalendar(date);
        addCalendarMonth(cal, month);
        date.setTime(cal.getTimeInMillis());
    }

    public static void addDateDate(Date date, int dayOfMonth) {
        final Calendar cal = toCalendar(date);
        addCalendarDate(cal, dayOfMonth);
        date.setTime(cal.getTimeInMillis());
    }

    public static void addDateHourOfDay(Date date, int hourOfDay) {
        final Calendar cal = toCalendar(date);
        addCalendarHourOfDay(cal, hourOfDay);
        date.setTime(cal.getTimeInMillis());
    }

    public static void addDateMinute(Date date, int minute) {
        final Calendar cal = toCalendar(date);
        addCalendarMinute(cal, minute);
        date.setTime(cal.getTimeInMillis());
    }

    public static void addDateSecond(Date date, int second) {
        final Calendar cal = toCalendar(date);
        addCalendarSecond(cal, second);
        date.setTime(cal.getTimeInMillis());
    }

    public static void addDateMillisecond(Date date, int millisecond) {
        final Calendar cal = toCalendar(date);
        addCalendarMillisecond(cal, millisecond);
        date.setTime(cal.getTimeInMillis());
    }

    public static void setDateFirstDateOfMonth(Date date) {
        final Calendar cal = toCalendar(date);
        setCalendarFirstDateOfMonth(cal);
        date.setTime(cal.getTimeInMillis());
    }

    public static void setDateLastDateOfMonth(Date date) {
        final Calendar cal = toCalendar(date);
        setCalendarLastDateOfMonth(cal);
        date.setTime(cal.getTimeInMillis());
    }

    public static void clearDateTimeParts(Date date) {
        final Calendar cal = toCalendar(date);
        clearCalendarTimeParts(cal);
        date.setTime(cal.getTimeInMillis());
    }

    public static void clearDateMillisecond(Date date) {
        final Calendar cal = toCalendar(date);
        clearCalendarMillisecond(cal);
        date.setTime(cal.getTimeInMillis());
    }

    // -----------------------------------------------------
    //                                            DateFormat
    //                                            ----------
    public static DateFormat createDateFormat(String pattern) { // as lenient
        return createDateFormat(pattern, false);
    }

    public static DateFormat createDateFormat(String pattern, boolean strict) {
        if (pattern == null) {
            String msg = "The argument 'pattern' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(!strict);
        return sdf;
    }

    // ===================================================================================
    //                                                                           Timestamp
    //                                                                           =========
    /**
     * Convert the object to the instance that is time-stamp. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses default date pattern based on 'yyyy-MM-dd HH:mm:ss.SSS'
     * with flexible-parsing if the object is string type.
     * @param obj The parsed object. (NullAllowed)
     * @return The instance of time-stamp. (NullAllowed: If the value is null or empty, it returns null.)
     * @throws ParseTimestampException When it failed to parse the string to time-stamp.
     * @throws ParseTimestampNumberFormatException When it failed to format the elements as number.
     * @throws ParseTimestampOutOfCalendarException When the timestamp was out of calendar. (if BC, not thrown)
     */
    public static Timestamp toTimestamp(Object obj) {
        return toTimestamp(obj, null);
    }

    /**
     * Convert the object to the instance that is time-stamp. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses specified timestamp pattern when the pattern is not null
     * if the object is string type. If it's null, it uses default timestamp pattern
     * with flexible-parsing based on 'yyyy-MM-dd HH:mm:ss.SSS'.
     * @param obj The parsed object. (NullAllowed)
     * @param pattern The pattern format to parse. (NullAllowed)
     * @return The instance of time-stamp. (NullAllowed: If the value is null or empty, it returns null.)
     * @throws ParseTimestampException When it failed to parse the string to time-stamp.
     * @throws ParseTimestampNumberFormatException When it failed to format the elements as number.
     * @throws ParseTimestampOutOfCalendarException When the timestamp was out of calendar. (if BC, not thrown)
     */
    public static Timestamp toTimestamp(Object obj, String pattern) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Timestamp) {
            final Timestamp paramTimestamp = (Timestamp) obj;
            if (Timestamp.class.equals(paramTimestamp.getClass())) { // pure time-stamp
                return paramTimestamp;
            } else { // sub class
                // because the time-stamp type is not final class.
                return new Timestamp(paramTimestamp.getTime());
            }
        } else if (obj instanceof Date) {
            return new Timestamp(((Date) obj).getTime());
        } else if (obj instanceof String) {
            return toTimestampFromString((String) obj, pattern);
        } else if (obj instanceof Calendar) {
            return new Timestamp(((Calendar) obj).getTime().getTime());
        } else if (obj instanceof byte[]) {
            return toTimestamp(toSerializable((byte[]) obj)); // recursive
        } else if (obj instanceof Long) {
            return new Timestamp((Long) obj);
        } else {
            return toTimestampFromString(obj.toString(), pattern);
        }
    }

    protected static Timestamp toTimestampFromString(String str, String pattern) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        boolean strict;
        if (pattern == null || pattern.trim().length() == 0) { // flexibly
            str = filterTimestampStringValueFlexibly(str);
            strict = !str.startsWith("-"); // not BC
            pattern = "yyyy-MM-dd HH:mm:ss.SSS";
        } else {
            strict = true;
        }
        DateFormat df = createDateFormat(pattern, strict);
        try {
            return new Timestamp(df.parse(str).getTime());
        } catch (ParseException e) {
            try {
                df.setLenient(true);
                df.parse(str); // no exception means illegal date
                String msg = "The timestamp expression is out of calendar:";
                msg = msg + " string=" + str + " pattern=" + pattern;
                throw new ParseTimestampOutOfCalendarException(msg, e);
            } catch (ParseException ignored) {
                String msg = "Failed to parse the string to timestamp:";
                msg = msg + " string=" + str + " pattern=" + pattern;
                throw new ParseTimestampException(msg, e);
            }
        }
    }

    protected static String filterTimestampStringValueFlexibly(final String pureStr) {
        String str = pureStr;
        try {
            final boolean includeMilli = true;
            str = filterDateStringValueFlexibly(str, includeMilli); // based on date way
        } catch (ParseDateNumberFormatException e) {
            String msg = "Failed to format the timestamp as number:";
            msg = msg + " value=" + pureStr;
            throw new ParseTimestampNumberFormatException(msg, e);
        }
        return str;
    }

    public static class ParseTimestampException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ParseTimestampException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ParseTimestampOutOfCalendarException extends ParseTimestampException {
        private static final long serialVersionUID = 1L;

        public ParseTimestampOutOfCalendarException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ParseTimestampNumberFormatException extends ParseTimestampException {
        private static final long serialVersionUID = 1L;

        public ParseTimestampNumberFormatException(String msg, Exception e) {
            super(msg, e);
        }
    }

    // ===================================================================================
    //                                                                                Time
    //                                                                                ====
    /**
     * Convert the object to the instance that is time. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses default time pattern based on 'HH:mm:ss'
     * with flexible-parsing if the object is string type.
     * @param obj The parsed object. (NullAllowed)
     * @return The instance of time. (NullAllowed: If the value is null or empty, it returns null.)
     * @throws ParseTimeException When it failed to parse the string to time.
     * @throws ParseTimeNumberFormatException When it failed to format the elements as number.
     * @throws ParseTimeOutOfCalendarException When the time is out of calendar.
     */
    public static Time toTime(Object obj) {
        return toTime(obj, null);
    }

    /**
     * Convert the object to the instance that is time. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses specified time pattern when the pattern is not null
     * if the object is string type. If it's null, it uses default time pattern
     * with flexible-parsing based on 'HH:mm:ss'.
     * @param obj The parsed object. (NullAllowed)
     * @param pattern The pattern format to parse. (NullAllowed)
     * @return The instance of time. (NullAllowed: If the value is null or empty, it returns null.)
     * @throws ParseTimeException When it failed to parse the string to time.
     * @throws ParseTimeNumberFormatException When it failed to format the elements as number.
     * @throws ParseTimeOutOfCalendarException When the time is out of calendar.
     */
    public static Time toTime(Object obj, String pattern) {
        if (obj == null) {
            return null;
        } else if (obj instanceof String) {
            return toTimeFromString((String) obj, pattern);
        } else if (obj instanceof Time) {
            final Time paramTime = (Time) obj;
            if (Time.class.equals(paramTime.getClass())) { // pure time
                return paramTime;
            } else { // sub class
                // because the time type is not final class.
                return new Time(paramTime.getTime());
            }
        } else if (obj instanceof Date) {
            Date date = (Date) obj;
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.YEAR, 1970);
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DATE, 1);
            return new Time(cal.getTimeInMillis());
        } else if (obj instanceof Calendar) {
            Calendar cal = (Calendar) obj;
            cal.set(Calendar.YEAR, 1970);
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DATE, 1);
            return new Time(cal.getTimeInMillis());
        } else if (obj instanceof byte[]) {
            return toTime(toSerializable((byte[]) obj)); // recursive
        } else if (obj instanceof Long) {
            return toTime(toDate((Long) obj));
        } else {
            return toTimeFromString(obj.toString(), pattern);
        }
    }

    protected static Time toTimeFromString(String str, String pattern) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        if (pattern == null || pattern.trim().length() == 0) { // flexibly
            str = filterTimeStringValueFlexibly(str);
            pattern = "HH:mm:ss";
        }
        final DateFormat df = createDateFormat(pattern, true);
        try {
            return new Time(df.parse(str).getTime());
        } catch (ParseException e) {
            try {
                df.setLenient(true);
                df.parse(str); // no exception means illegal date
                String msg = "The time expression is out of calendar:";
                msg = msg + " string=" + str + " pattern=" + pattern;
                throw new ParseTimeOutOfCalendarException(msg, e);
            } catch (ParseException ignored) {
                String msg = "Failed to parse the string to time:";
                msg = msg + " string=" + str + " pattern=" + pattern;
                throw new ParseTimeException(msg, e);
            }
        }
    }

    protected static String filterTimeStringValueFlexibly(String pureStr) {
        String str = pureStr;
        str = str.trim();
        final int dateEndIndex = str.indexOf(" ");
        if (dateEndIndex >= 0) {
            // '2008-12-12 12:34:56' to '12:34:56'
            final String time = str.substring(dateEndIndex + " ".length());
            final boolean includeMilli = false;
            try {
                str = handleTimeZeroPrefix(time, pureStr, includeMilli);
            } catch (ParseDateNumberFormatException e) {
                String msg = "Failed to format the time as number:";
                msg = msg + " value=" + pureStr;
                throw new ParseTimeNumberFormatException(msg, e);
            }
        }
        return str;
    }

    public static class ParseTimeException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ParseTimeException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ParseTimeNumberFormatException extends ParseTimeException {
        private static final long serialVersionUID = 1L;

        public ParseTimeNumberFormatException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ParseTimeOutOfCalendarException extends ParseTimeException {
        private static final long serialVersionUID = 1L;

        public ParseTimeOutOfCalendarException(String msg, Exception e) {
            super(msg, e);
        }
    }

    // ===================================================================================
    //                                                                           (sql)Date
    //                                                                           =========
    /**
     * Convert the object to the instance that is SQL-date. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses default date pattern based on 'yyyy-MM-dd'
     * with flexible-parsing if the object is string type.
     * @param obj The parsed object. (NullAllowed)
     * @return The instance of SQL date. (NullAllowed)
     * @throws ParseSqlDateException When it failed to parse the string to SQL date.
     * @throws ParseSqlDateNumberFormatException When it failed to format the elements as number.
     * @throws ParseSqlDateOutOfCalendarException When the time is out of calendar.
     */
    public static java.sql.Date toSqlDate(Object obj) {
        return toSqlDate(obj, null);
    }

    /**
     * Convert the object to the instance that is SQL-date cleared seconds. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses specified SQL-date pattern when the pattern is not null
     * if the object is string type. If it's null, it uses default SQL-date pattern
     * with flexible-parsing based on 'yyyy-MM-dd'.
     * @param obj The parsed object. (NullAllowed)
     * @param pattern The pattern format to parse. (NullAllowed)
     * @return The instance of SQL date. (NullAllowed)
     * @throws ParseSqlDateException When it failed to parse the string to SQL date.
     * @throws ParseSqlDateNumberFormatException When it failed to format the elements as number.
     * @throws ParseSqlDateOutOfCalendarException When the time is out of calendar.
     */
    public static java.sql.Date toSqlDate(Object obj, String pattern) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof java.sql.Date) {
            final java.sql.Date resultDate;
            final java.sql.Date paramSqlDate = (java.sql.Date) obj;
            if (java.sql.Date.class.equals(paramSqlDate.getClass())) { // pure SQL-date
                resultDate = paramSqlDate;
            } else { // sub class
                // because the SQL-date type is not final class.
                resultDate = new java.sql.Date(paramSqlDate.getTime());
            }
            clearDateTimeParts(resultDate);
            return resultDate;
        }
        final Date date;
        try {
            date = toDate(obj, pattern);
        } catch (ParseDateNumberFormatException e) {
            String msg = "Failed to format the time as number:";
            msg = msg + " obj=" + obj + " pattern=" + pattern;
            throw new ParseSqlDateNumberFormatException(msg, e);
        } catch (ParseDateOutOfCalendarException e) {
            String msg = "The SQL-date expression is out of calendar:";
            msg = msg + " obj=" + obj + " pattern=" + pattern;
            throw new ParseSqlDateOutOfCalendarException(msg, e);
        } catch (ParseDateException e) {
            String msg = "Failed to parse the object to SQL-date:";
            msg = msg + " obj=" + obj + " pattern=" + pattern;
            throw new ParseSqlDateException(msg, e);
        }
        if (date != null) {
            clearDateTimeParts(date);
            return new java.sql.Date(date.getTime());
        }
        return null;
    }

    public static class ParseSqlDateException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ParseSqlDateException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ParseSqlDateNumberFormatException extends ParseSqlDateException {
        private static final long serialVersionUID = 1L;

        public ParseSqlDateNumberFormatException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ParseSqlDateOutOfCalendarException extends ParseSqlDateException {
        private static final long serialVersionUID = 1L;

        public ParseSqlDateOutOfCalendarException(String msg, Exception e) {
            super(msg, e);
        }
    }

    // ===================================================================================
    //                                                                            Calendar
    //                                                                            ========
    public static Calendar toCalendar(Object obj) {
        return toCalendar(obj, null);
    }

    public static Calendar toCalendar(Object obj, String pattern) {
        if (obj instanceof Calendar) {
            final Calendar original = ((Calendar) obj);
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(original.getTimeInMillis());
            cal.setTimeZone(original.getTimeZone());
            return cal;// new instance
        }
        final Date date;
        try {
            date = toDate(obj, pattern);
        } catch (ParseDateNumberFormatException e) {
            String msg = "Failed to format the calendar as number:";
            msg = msg + " obj=" + obj + " pattern=" + pattern;
            throw new ParseCalendarNumberFormatException(msg, e);
        } catch (ParseDateOutOfCalendarException e) {
            String msg = "The calendar expression is out of calendar:";
            msg = msg + " obj=" + obj + " pattern=" + pattern;
            throw new ParseCalendarOutOfCalendarException(msg, e);
        } catch (ParseDateException e) {
            String msg = "Failed to parse the object to calendar:";
            msg = msg + " obj=" + obj + " pattern=" + pattern;
            throw new ParseCalendarException(msg, e);
        }
        if (date != null) {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        }
        return null;
    }

    public static class ParseCalendarException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ParseCalendarException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ParseCalendarNumberFormatException extends ParseCalendarException {
        private static final long serialVersionUID = 1L;

        public ParseCalendarNumberFormatException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ParseCalendarOutOfCalendarException extends ParseCalendarException {
        private static final long serialVersionUID = 1L;

        public ParseCalendarOutOfCalendarException(String msg, Exception e) {
            super(msg, e);
        }
    }

    // -----------------------------------------------------
    //                                          Manipulation
    //                                          ------------
    public static void addCalendarYear(Calendar cal, int year) {
        cal.add(Calendar.YEAR, year);
    }

    public static void addCalendarMonth(Calendar cal, int month) {
        cal.add(Calendar.MONTH, month);
    }

    public static void addCalendarDate(Calendar cal, int date) {
        cal.add(Calendar.DATE, date);
    }

    public static void addCalendarHourOfDay(Calendar cal, int hourOfDay) {
        cal.add(Calendar.HOUR_OF_DAY, hourOfDay);
    }

    public static void addCalendarMinute(Calendar cal, int minute) {
        cal.add(Calendar.MINUTE, minute);
    }

    public static void addCalendarSecond(Calendar cal, int second) {
        cal.add(Calendar.SECOND, second);
    }

    public static void addCalendarMillisecond(Calendar cal, int millisecond) {
        cal.add(Calendar.MILLISECOND, millisecond);
    }

    public static void setCalendarFirstDateOfMonth(Calendar cal) {
        cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
    }

    public static void setCalendarLastDateOfMonth(Calendar cal) {
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
    }

    // -----------------------------------------------------
    //                                           Clear Parts
    //                                           -----------
    public static void clearCalendarTimeParts(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        clearCalendarMillisecond(cal);
    }

    public static void clearCalendarMillisecond(Calendar cal) {
        cal.set(Calendar.MILLISECOND, 0);
    }

    // -----------------------------------------------------
    //                                              Localize
    //                                              --------

    public static Calendar localize(Calendar calendar) {
        if (calendar == null) {
            return calendar;
        }
        final Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTimeInMillis(calendar.getTimeInMillis());
        return localCalendar;
    }

    // ===================================================================================
    //                                                                             Boolean
    //                                                                             =======
    public static Boolean toBoolean(Object obj) {
        if (obj == null) {
            return (Boolean) obj;
        } else if (obj instanceof Boolean) {
            return (Boolean) obj;
        } else if (obj instanceof Number) {
            int num = ((Number) obj).intValue();
            return Boolean.valueOf(num != 0);
        } else if (obj instanceof String) {
            final String str = (String) obj;
            if ("true".equalsIgnoreCase(str)) {
                return Boolean.TRUE;
            } else if ("false".equalsIgnoreCase(str)) {
                return Boolean.FALSE;
            } else if (str.equalsIgnoreCase("1")) {
                return Boolean.TRUE;
            } else if (str.equalsIgnoreCase("0")) {
                return Boolean.FALSE;
            } else if (str.equalsIgnoreCase("t")) {
                return Boolean.TRUE;
            } else if (str.equalsIgnoreCase("f")) {
                return Boolean.FALSE;
            } else {
                String msg = "Failed to parse the boolean string:";
                msg = msg + " value=" + str;
                throw new ParseBooleanException(msg);
            }
        } else if (obj instanceof byte[]) {
            return toBoolean(toSerializable((byte[]) obj)); // recursive
        } else {
            return Boolean.FALSE; // couldn't parse
        }
    }

    public static boolean toPrimitiveBoolean(Object obj) {
        Boolean wrapper = toBoolean(obj);
        return wrapper != null ? wrapper.booleanValue() : false;
    }

    public static class ParseBooleanException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ParseBooleanException(String msg) {
            super(msg);
        }
    }

    // ===================================================================================
    //                                                                                UUID
    //                                                                                ====
    public static UUID toUUID(Object obj) {
        if (obj == null) {
            return (UUID) obj;
        } else if (obj instanceof UUID) {
            return (UUID) obj;
        } else if (obj instanceof String) {
            return toUUIDFromString((String) obj);
        } else {
            return toUUIDFromString(obj.toString());
        }
    }

    protected static UUID toUUIDFromString(String str) {
        try {
            return UUID.fromString(str);
        } catch (RuntimeException e) {
            String msg = "Failed to parse the string as UUID:";
            msg = msg + "str=" + str;
            throw new ParseUUIDException(msg);
        }
    }

    public static class ParseUUIDException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ParseUUIDException(String msg) {
            super(msg);
        }

        public ParseUUIDException(String msg, Throwable e) {
            super(msg, e);
        }
    }

    // ===================================================================================
    //                                                                              Binary
    //                                                                              ======
    public static byte[] toBinary(Serializable obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return (byte[]) obj;
        }
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            try {
                return baos.toByteArray();
            } finally {
                oos.close();
            }
        } catch (Exception e) {
            String msg = "Failed to convert the object to binary: obj=" + obj;
            throw new IllegalStateException(msg, e);
        }
    }

    public static Serializable toSerializable(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            final ObjectInputStream ois = new ObjectInputStream(bais);
            try {
                return (Serializable) ois.readObject();
            } finally {
                ois.close();
            }
        } catch (Exception e) {
            String msg = "Failed to convert the object to binary: bytes.length=" + bytes.length;
            throw new IllegalStateException(msg, e);
        }
    }

    // ===================================================================================
    //                                                                             Wrapper
    //                                                                             =======
    public static Object toWrapper(Object obj, Class<?> type) {
        if (type == int.class) {
            Integer i = toInteger(obj);
            if (i != null) {
                return i;
            }
            return Integer.valueOf(0);
        } else if (type == double.class) {
            Double d = toDouble(obj);
            if (d != null) {
                return d;
            }
            return new Double(0);
        } else if (type == long.class) {
            Long l = toLong(obj);
            if (l != null) {
                return l;
            }
            return Long.valueOf(0);
        } else if (type == float.class) {
            Float f = toFloat(obj);
            if (f != null) {
                return f;
            }
            return new Float(0);
        } else if (type == short.class) {
            Short s = toShort(obj);
            if (s != null) {
                return s;
            }
            return Short.valueOf((short) 0);
        } else if (type == boolean.class) {
            Boolean b = toBoolean(obj);
            if (b != null) {
                return b;
            }
            return Boolean.FALSE;
        } else if (type == byte.class) {
            Byte b = toByte(obj);
            if (b != null) {
                return b;
            }
            return Byte.valueOf((byte) 0);
        }
        return obj;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    // -----------------------------------------------------
    //                                  DecimalFormatSymbols
    //                                  --------------------
    protected static Map<Locale, DecimalFormatSymbols> symbolsCache = new ConcurrentHashMap<Locale, DecimalFormatSymbols>();

    protected static DecimalFormatSymbols getDecimalFormatSymbols(Locale locale) {
        DecimalFormatSymbols symbols = (DecimalFormatSymbols) symbolsCache.get(locale);
        if (symbols == null) {
            symbols = new DecimalFormatSymbols(locale);
            symbolsCache.put(locale, symbols);
        }
        return symbols;
    }

    // -----------------------------------------------------
    //                                                String
    //                                                ------
    protected static String replace(String text, String from, String to) {
        if (text == null || from == null || to == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        int pos2 = 0;
        do {
            pos = text.indexOf(from, pos2);
            if (pos == 0) {
                sb.append(to);
                pos2 = from.length();
            } else if (pos > 0) {
                sb.append(text.substring(pos2, pos));
                sb.append(to);
                pos2 = pos + from.length();
            } else {
                sb.append(text.substring(pos2));
                return sb.toString();
            }
        } while (true);
    }

    protected static final String[] EMPTY_STRINGS = new String[0];

    protected static String[] split(final String str, final String delimiter) {
        if (str == null || str.trim().length() == 0) {
            return EMPTY_STRINGS;
        }
        final List<String> list = new ArrayList<String>();
        final StringTokenizer st = new StringTokenizer(str, delimiter);
        while (st.hasMoreElements()) {
            list.add(st.nextToken());
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
}
