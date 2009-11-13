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

/**
 * @author jflute
 */
public class DfTraceViewUtil {

    /**
     * Convert to performance view.
     * @param afterMinusBefore The value of after-minus-before millisecond.
     * @return Performance view. (ex. 1m23s456ms) (NotNull)
     */
    public static String convertToPerformanceView(long afterMinusBefore) {
        if (afterMinusBefore < 0) {
            return String.valueOf(afterMinusBefore);
        }

        long sec = afterMinusBefore / 1000;
        final long min = sec / 60;
        sec = sec % 60;
        final long mil = afterMinusBefore % 1000;

        final StringBuffer sb = new StringBuffer();
        if (min >= 10) { // Minute
            sb.append(min).append("m");
        } else if (min < 10 && min >= 0) {
            sb.append("0").append(min).append("m");
        }
        if (sec >= 10) { // Second
            sb.append(sec).append("s");
        } else if (sec < 10 && sec >= 0) {
            sb.append("0").append(sec).append("s");
        }
        if (mil >= 100) { // Millisecond
            sb.append(mil).append("ms");
        } else if (mil < 100 && mil >= 10) {
            sb.append("0").append(mil).append("ms");
        } else if (mil < 10 && mil >= 0) {
            sb.append("00").append(mil).append("ms");
        }

        return sb.toString();
    }

    /**
     * Convert object array to string view.
     * @param objArray The array of object. (Nullable)
     * @return The string divided with comma. (NotNull: If the argument is null, returns empty string.)
     */
    public static String convertObjectArrayToStringView(Object[] objArray) {
        if (objArray == null) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < objArray.length; i++) {
            if (i == 0) {
                sb.append(objArray[i]);
            } else {
                sb.append(", ").append(objArray[i]);
            }
        }
        return sb.toString();
    }
}
