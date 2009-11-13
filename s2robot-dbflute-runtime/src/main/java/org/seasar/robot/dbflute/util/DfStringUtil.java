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
package org.seasar.robot.dbflute.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jflute
 */
public class DfStringUtil {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String[] EMPTY_STRINGS = new String[0];

    // ===================================================================================
    //                                                                        Null & Empty
    //                                                                        ============
    public static final boolean isNullOrEmpty(final String text) {
        return text == null || text.length() == 0;
    }

    public static final boolean isNullOrTrimmedEmpty(final String text) {
        return text == null || text.trim().length() == 0;
    }

    public static final boolean isNotNullAndNotEmpty(final String text) {
        return !isNullOrEmpty(text);
    }

    public static final boolean isNotNullAndNotTrimmedEmpty(final String text) {
        return !isNullOrTrimmedEmpty(text);
    }

    // ===================================================================================
    //                                                                             Replace
    //                                                                             =======
    public static String replace(String text, String fromText, String toText) {
        if (text == null || fromText == null || toText == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        int pos2 = 0;
        do {
            pos = text.indexOf(fromText, pos2);
            if (pos == 0) {
                sb.append(toText);
                pos2 = fromText.length();
            } else if (pos > 0) {
                sb.append(text.substring(pos2, pos));
                sb.append(toText);
                pos2 = pos + fromText.length();
            } else {
                sb.append(text.substring(pos2));
                return sb.toString();
            }
        } while (true);
    }

    // ===================================================================================
    //                                                                               Split
    //                                                                               =====
    /**
     * @param str The split target string. (NotNull)
     * @param delimiter The delimiter for split. (NotNull)
     * @return The split list. (NotNull)
     */
    public static List<String> splitList(final String str, final String delimiter) {
        final List<String> list = new ArrayList<String>();
        int i = 0;
        int j = str.indexOf(delimiter);
        for (int h = 0; j >= 0; h++) {
            list.add(str.substring(i, j));
            i = j + delimiter.length();
            j = str.indexOf(delimiter, i);
        }
        list.add(str.substring(i));
        return list;
    }

    // ===================================================================================
    //                                                                                Trim
    //                                                                                ====
    public static final String rtrim(String text) {
        return rtrim(text, null);
    }

    public static final String rtrim(String text, String trimText) {
        if (text == null) {
            return null;
        }

        // for trim target same as String.trim()
        if (trimText == null) {
            final String notTrimmedString = "a";
            return (notTrimmedString + text).trim().substring(notTrimmedString.length());
        }

        // for original trim target
        int pos;
        for (pos = text.length() - 1; pos >= 0 && trimText.indexOf(text.charAt(pos)) >= 0; pos--)
            ;
        return text.substring(0, pos + 1);
    }

    // ===================================================================================
    //                                                                     Initial Convert
    //                                                                     ===============
    public static String initCap(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String initCapAfterTrimming(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        return initCap(str);
    }

    public static String initUncap(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    // ===================================================================================
    //                                                                      Naming Convert
    //                                                                      ==============
    public static String decamelizePropertyName(String s) {
        if (s == null) {
            return null;
        }
        if (s.length() == 1) {
            return s.toUpperCase();
        }
        StringBuilder sb = new StringBuilder(40);
        int pos = 0;
        for (int i = 1; i < s.length(); ++i) {
            if (Character.isUpperCase(s.charAt(i))) {
                if (sb.length() != 0) {
                    sb.append('_');
                }
                sb.append(s.substring(pos, i).toUpperCase());
                pos = i;
            }
        }
        if (sb.length() != 0) {
            sb.append('_');
        }
        sb.append(s.substring(pos, s.length()).toUpperCase());
        return sb.toString();
    }

    public static String decapitalizePropertyName(String propertyName) {
        if (propertyName == null || propertyName.length() == 0) {
            return propertyName;
        }
        if (propertyName.length() > 1 && Character.isUpperCase(propertyName.charAt(1))
                && Character.isUpperCase(propertyName.charAt(0))) {
            return propertyName;
        }
        char chars[] = propertyName.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    // ===================================================================================
    //                                                                       Extract Scope
    //                                                                       =============
    public static String extractFirstScope(String targetStr, String beginMark, String endMark) {
        if (targetStr == null || beginMark == null || endMark == null) {
            return null;
        }
        final String ret;
        {
            String tmp = targetStr;
            final int startIndex = tmp.indexOf(beginMark);
            if (startIndex < 0) {
                return null;
            }
            tmp = tmp.substring(startIndex + beginMark.length());
            if (tmp.indexOf(endMark) < 0) {
                return null;
            }
            ret = tmp.substring(0, tmp.indexOf(endMark)).trim();
        }
        return ret;
    }

    public static List<String> extractAllScope(String targetStr, String beginMark, String endMark) {
        if (targetStr == null || beginMark == null || endMark == null) {
            return new ArrayList<String>();
        }
        final List<String> resultList = new ArrayList<String>();
        String tmp = targetStr;
        while (true) {
            final int startIndex = tmp.indexOf(beginMark);
            if (startIndex < 0) {
                break;
            }
            tmp = tmp.substring(startIndex + beginMark.length());
            if (tmp.indexOf(endMark) < 0) {
                break;
            }
            resultList.add(tmp.substring(0, tmp.indexOf(endMark)).trim());
            tmp = tmp.substring(tmp.indexOf(endMark) + endMark.length());
        }
        return resultList;
    }

    // ===================================================================================
    //                                                                         List String
    //                                                                         ===========
    public static boolean containsIgnoreCase(String target, List<String> strList) {
        if (target == null || strList == null) {
            return false;
        }
        for (String str : strList) {
            if (target.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }
}
