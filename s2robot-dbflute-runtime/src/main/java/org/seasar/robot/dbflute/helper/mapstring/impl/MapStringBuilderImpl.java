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
package org.seasar.robot.dbflute.helper.mapstring.impl;

import java.util.Arrays;
import java.util.List;

import org.seasar.robot.dbflute.helper.mapstring.MapStringBuilder;
import org.seasar.robot.dbflute.helper.token.line.LineToken;
import org.seasar.robot.dbflute.helper.token.line.LineTokenizingOption;
import org.seasar.robot.dbflute.helper.token.line.impl.LineTokenImpl;

/**
 * The implementation of map-string builder.
 * @author jflute
 */
public class MapStringBuilderImpl implements MapStringBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<String> _columnNameList;

    protected String _msMapMark;

    protected String _msStartBrace;

    protected String _msEndBrace;

    protected String _msDelimiter;

    protected String _msEqual;

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public String buildByDelimiter(String values, String delimiter) {
        if (values == null) {
            String msg = "The argument[values] should not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (delimiter == null) {
            String msg = "The argument[delimiter] should not be null.";
            throw new IllegalArgumentException(msg);
        }
        assertStringComponent();

        final java.util.List<String> valueList = tokenize(values, delimiter);
        assertColumnValueList(_columnNameList, valueList);

        final StringBuffer sb = new StringBuffer();
        sb.append(_msMapMark).append(_msStartBrace);
        for (int i = 0; i < _columnNameList.size(); i++) {
            sb.append(_columnNameList.get(i)).append(_msEqual).append(valueList.get(i)).append(_msDelimiter);
        }

        sb.delete(sb.length() - _msDelimiter.length(), sb.length());
        sb.append(_msEndBrace);
        return sb.toString();
    }

    public String buildFromList(List<String> valueList) {
        if (valueList == null) {
            String msg = "The argument[valueList] should not be null.";
            throw new IllegalArgumentException(msg);
        }
        assertStringComponent();
        assertColumnValueList(_columnNameList, valueList);

        final StringBuffer sb = new StringBuffer();
        sb.append(_msMapMark).append(_msStartBrace);
        for (int i = 0; i < _columnNameList.size(); i++) {
            sb.append(_columnNameList.get(i)).append(_msEqual).append(valueList.get(i)).append(_msDelimiter);
        }

        sb.delete(sb.length() - _msDelimiter.length(), sb.length());
        sb.append(_msEndBrace);
        return sb.toString();
    }

    protected List<String> tokenize(String value, String delimiter) {
        final LineToken lineToken = new LineTokenImpl();
        final LineTokenizingOption lineTokenizingOption = new LineTokenizingOption();
        lineTokenizingOption.setDelimiter(delimiter);
        return lineToken.tokenize(value, lineTokenizingOption);
    }

    protected void assertStringComponent() {
        if (_columnNameList == null) {
            String msg = "The columnNameList should not be null.";
            throw new IllegalStateException(msg);
        }
        if (_columnNameList.isEmpty()) {
            String msg = "The columnNameList should not be empty.";
            throw new IllegalStateException(msg);
        }
        if (_msMapMark == null) {
            String msg = "The msMapMark should not be null.";
            throw new IllegalStateException(msg);
        }
        if (_msStartBrace == null) {
            String msg = "The msStartBrace should not be null.";
            throw new IllegalStateException(msg);
        }
        if (_msEndBrace == null) {
            String msg = "The msEndBrace should not be null.";
            throw new IllegalStateException(msg);
        }
        if (_msDelimiter == null) {
            String msg = "The msDelimiter should not be null.";
            throw new IllegalStateException(msg);
        }
        if (_msEqual == null) {
            String msg = "The msEqual should not be null.";
            throw new IllegalStateException(msg);
        }
    }

    protected void assertColumnValueList(List<String> columnNameList, List<String> valueList) {
        if (columnNameList.size() != valueList.size()) {
            String msg = "The length of columnNameList and valueList are difference. (" + columnNameList.size() + ", "
                    + valueList.size() + ")";
            msg = msg + " columnNameList=" + columnNameList;
            msg = msg + " valueList=" + valueList;
            throw new DifferentDelimiterCountException(msg, columnNameList, valueList);
        }
    }

    protected static final String replace(String text, String fromText, String toText) {
        if (text == null || fromText == null || toText == null) {
            return null;
        }
        final StringBuffer buf = new StringBuffer(100);
        int pos = 0;
        int pos2 = 0;
        while (true) {
            pos = text.indexOf(fromText, pos2);
            if (pos == 0) {
                buf.append(toText);
                pos2 = fromText.length();
            } else if (pos > 0) {
                buf.append(text.substring(pos2, pos));
                buf.append(toText);
                pos2 = pos + fromText.length();
            } else {
                buf.append(text.substring(pos2));
                break;
            }
        }
        return buf.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setColumnNames(String[] columnNames) {
        _columnNameList = Arrays.asList(columnNames);
    }

    public void setColumnNameList(List<String> columnNameList) {
        _columnNameList = columnNameList;
    }

    public void setMsMapMark(String value) {
        _msMapMark = value;
    }

    public void setMsStartBrace(String value) {
        _msStartBrace = value;
    }

    public void setMsEndBrace(String value) {
        _msEndBrace = value;
    }

    public void setMsDelimiter(String value) {
        _msDelimiter = value;
    }

    public void setMsEqual(String value) {
        _msEqual = value;
    }
}
