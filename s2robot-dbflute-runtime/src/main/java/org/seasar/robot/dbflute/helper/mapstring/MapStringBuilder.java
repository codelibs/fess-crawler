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
package org.seasar.robot.dbflute.helper.mapstring;

/**
 * The interface of map-string-builder.
 * @author jflute
 */
public interface MapStringBuilder {

    // =====================================================================================
    //                                                                                Setter
    //                                                                                ======
    public void setColumnNames(String[] columnNames);

    public void setColumnNameList(java.util.List<String> columnNameList);

    public void setMsMapMark(String value);

    public void setMsStartBrace(String value);

    public void setMsEndBrace(String value);

    public void setMsDelimiter(String value);

    public void setMsEqual(String value);

    // =====================================================================================
    //                                                                                  Main
    //                                                                                  ====
    public String buildByDelimiter(String values, String delimiter);

    public String buildFromList(java.util.List<String> valueList);

    // =====================================================================================
    //                                                                Exception Static Class
    //                                                                ======================
    public static class DifferentDelimiterCountException extends RuntimeException {

        /** Serial version UID. (Default) */
        private static final long serialVersionUID = 1L;

        // =====================================================================================
        //                                                                             Attribute
        //                                                                             =========
        protected java.util.List<String> _columnNameList;
        protected java.util.List<String> _valueList;

        // =====================================================================================
        //                                                                           Constructor
        //                                                                           ===========
        public DifferentDelimiterCountException(String msg, java.util.List<String> columnNameList, java.util.List<String> valueList) {
            super(msg);
            _columnNameList = columnNameList;
            _valueList = valueList;
        }

        // =====================================================================================
        //                                                                              Accessor
        //                                                                              ========
        public java.util.List<String> getColumnNameList() {
            return _columnNameList;
        }
        public java.util.List<String> getValueList() {
            return _valueList;
        }
    }
}
