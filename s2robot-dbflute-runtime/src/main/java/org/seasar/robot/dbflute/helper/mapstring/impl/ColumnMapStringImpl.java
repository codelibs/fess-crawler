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
package org.seasar.robot.dbflute.helper.mapstring.impl;

import java.util.Arrays;
import java.util.List;

import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.helper.mapstring.ColumnMapString;
import org.seasar.robot.dbflute.helper.token.line.LineToken;
import org.seasar.robot.dbflute.helper.token.line.LineTokenizingOption;
import org.seasar.robot.dbflute.helper.token.line.impl.LineTokenImpl;
import org.seasar.robot.dbflute.util.Srl;

/**
 * The implementation of column map-string.
 * @author jflute
 */
public class ColumnMapStringImpl implements ColumnMapString {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _mapMark;
    protected String _startBrace;
    protected String _endBrace;
    protected String _delimiter;
    protected String _equal;
    protected List<String> _columnNameList;

    // ===================================================================================
    //                                                                               Build
    //                                                                               =====
    public String buildMapString(String values, String delimiter) {
        if (values == null) {
            String msg = "The argument[values] should not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (delimiter == null) {
            String msg = "The argument[delimiter] should not be null.";
            throw new IllegalArgumentException(msg);
        }
        assertStringComponent();
        final List<String> valueList = tokenize(values, delimiter);
        return buildMapString(valueList);
    }

    public String buildMapString(List<String> valueList) {
        if (valueList == null) {
            String msg = "The argument[valueList] should not be null.";
            throw new IllegalArgumentException(msg);
        }
        assertStringComponent();
        assertColumnValueList(_columnNameList, valueList);

        final StringBuilder sb = new StringBuilder();
        sb.append(_mapMark).append(_startBrace);
        for (int i = 0; i < _columnNameList.size(); i++) {
            final String columnName = _columnNameList.get(i);
            final String value = valueList.get(i);
            sb.append(columnName).append(_equal).append(value).append(_delimiter);
        }

        sb.delete(sb.length() - _delimiter.length(), sb.length());
        sb.append(_endBrace);
        return sb.toString();
    }

    protected List<String> tokenize(String value, String delimiter) {
        final LineToken lineToken = new LineTokenImpl();
        final LineTokenizingOption lineTokenizingOption = new LineTokenizingOption();
        lineTokenizingOption.setDelimiter(delimiter);
        return lineToken.tokenize(value, lineTokenizingOption);
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertStringComponent() {
        if (_columnNameList == null) {
            String msg = "The columnNameList should not be null.";
            throw new IllegalStateException(msg);
        }
        if (_columnNameList.isEmpty()) {
            String msg = "The columnNameList should not be empty.";
            throw new IllegalStateException(msg);
        }
        if (_mapMark == null) {
            String msg = "The msMapMark should not be null.";
            throw new IllegalStateException(msg);
        }
        if (_startBrace == null) {
            String msg = "The msStartBrace should not be null.";
            throw new IllegalStateException(msg);
        }
        if (_endBrace == null) {
            String msg = "The msEndBrace should not be null.";
            throw new IllegalStateException(msg);
        }
        if (_delimiter == null) {
            String msg = "The msDelimiter should not be null.";
            throw new IllegalStateException(msg);
        }
        if (_equal == null) {
            String msg = "The msEqual should not be null.";
            throw new IllegalStateException(msg);
        }
    }

    protected void assertColumnValueList(List<String> columnNameList, List<String> valueList) {
        if (columnNameList.size() != valueList.size()) {
            final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
            br.addNotice("The length of columnNameList and valueList are different.");
            br.addItem("Column Name List");
            br.addElement(columnNameList.size());
            br.addElement(columnNameList.toString());
            br.addItem("Value List");
            br.addElement(valueList.size());
            br.addElement(valueList.toString());
            final String msg = br.buildExceptionMessage();
            throw new DifferentDelimiterCountException(msg, columnNameList, valueList);
        }
    }

    public static class DifferentDelimiterCountException extends RuntimeException {

        /** Serial version UID. (Default) */
        private static final long serialVersionUID = 1L;

        protected List<String> _columnNameList;
        protected List<String> _valueList;

        public DifferentDelimiterCountException(String msg, List<String> columnNameList,
                java.util.List<String> valueList) {
            super(msg);
            _columnNameList = columnNameList;
            _valueList = valueList;
        }

        public java.util.List<String> getColumnNameList() {
            return _columnNameList;
        }

        public java.util.List<String> getValueList() {
            return _valueList;
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected final String replace(String str, String fromStr, String toStr) {
        return Srl.replace(str, fromStr, toStr);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setMapMark(String value) {
        _mapMark = value;
    }

    public void setStartBrace(String value) {
        _startBrace = value;
    }

    public void setEndBrace(String value) {
        _endBrace = value;
    }

    public void setDelimiter(String value) {
        _delimiter = value;
    }

    public void setEqual(String value) {
        _equal = value;
    }

    public void setColumnNames(String[] columnNames) {
        _columnNameList = Arrays.asList(columnNames);
    }

    public void setColumnNameList(List<String> columnNameList) {
        _columnNameList = columnNameList;
    }
}
