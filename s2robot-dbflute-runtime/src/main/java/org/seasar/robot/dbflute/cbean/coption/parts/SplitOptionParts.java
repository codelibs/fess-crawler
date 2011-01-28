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
package org.seasar.robot.dbflute.cbean.coption.parts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.seasar.robot.dbflute.util.Srl;

/**
 * The interface of condition-option.
 * @author jflute
 */
public class SplitOptionParts implements Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _delimiter;
    protected List<String> _subDelimiterList;
    protected int _splitLimitCount;

    // ===================================================================================
    //                                                                               Split
    //                                                                               =====
    public void splitByBlank() {
        _delimiter = " ";
        addSubDelimiter("\u3000");
        addSubDelimiter("\t");
        addSubDelimiter("\r");
        addSubDelimiter("\n");
    }

    public void splitByBlank(int splitLimitCount) {
        splitByBlank();
        _splitLimitCount = splitLimitCount;
    }

    public void splitBySpace() {
        _delimiter = " ";
    }

    public void splitBySpace(int splitLimitCount) {
        splitBySpace();
        _splitLimitCount = splitLimitCount;
    }

    public void splitBySpaceContainsDoubleByte() {
        splitBySpace();
        addSubDelimiter("\u3000");
    }

    public void splitBySpaceContainsDoubleByte(int splitLimitCount) {
        splitBySpaceContainsDoubleByte();
        _splitLimitCount = splitLimitCount;
    }

    public void splitByPipeLine() {
        _delimiter = "|";
    }

    public void splitByPipeLine(int splitLimitCount) {
        splitByPipeLine();
        _splitLimitCount = splitLimitCount;
    }

    public void splitByVarious(List<String> delimiterList) {
        if (delimiterList == null || delimiterList.isEmpty()) {
            String msg = "The delimiterList should not be null or empty:";
            msg = msg + " delimiterList=" + delimiterList;
            throw new IllegalArgumentException(msg);
        }
        final List<String> acceptList = new ArrayList<String>(delimiterList);
        _delimiter = delimiterList.remove(0);
        addSubDelimiter(acceptList);
    }

    public void splitByVarious(List<String> delimiterList, int splitLimitCount) {
        splitByVarious(delimiterList);
        _splitLimitCount = splitLimitCount;
    }

    // ===================================================================================
    //                                                                       Sub Delimiter
    //                                                                       =============
    protected void addSubDelimiter(String delimiter) {
        if (_subDelimiterList == null) {
            _subDelimiterList = new ArrayList<String>();
        }
        _subDelimiterList.add(delimiter);
    }

    protected void addSubDelimiter(List<String> delimiterList) {
        if (_subDelimiterList == null) {
            _subDelimiterList = new ArrayList<String>();
        }
        _subDelimiterList.addAll(delimiterList);
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isSplit() {
        return _delimiter != null;
    }

    // ===================================================================================
    //                                                                          Real Value
    //                                                                          ==========
    public String[] generateSplitValueArray(String value) {
        if (value == null) {
            String msg = "The argument[value] should not be null!";
            throw new IllegalArgumentException(msg);
        }
        value = resolveSubSplit(value);
        final StringTokenizer st = new StringTokenizer(value, _delimiter);
        final String[] tokenizedValues = new String[st.countTokens()];
        int count = 0;
        while (st.hasMoreTokens()) {
            tokenizedValues[count] = st.nextToken();
            count++;
        }
        final String[] values = removeInvalidValue(tokenizedValues);
        if (_splitLimitCount > 0 && values.length > _splitLimitCount) {
            final String[] realValues = new String[_splitLimitCount];
            for (int i = 0; i < values.length; i++) {
                if (i == _splitLimitCount) {
                    break;
                }
                realValues[i] = values[i];
            }
            return realValues;
        } else {
            return values;
        }

    }

    protected String resolveSubSplit(String value) {
        if (value == null || _delimiter == null || _subDelimiterList == null) {
            return value;
        }
        for (String subSplit : _subDelimiterList) {
            value = replace(value, subSplit, _delimiter);
        }
        return value;
    }

    protected String[] removeInvalidValue(String[] values) {
        final List<String> ls = new ArrayList<String>();
        for (int i = 0; i < values.length; i++) {
            final String value = values[i];
            if (value == null || value.equals("")) { // don't trim
                continue;
            }
            ls.add(value);
        }
        final String[] resultArray = new String[ls.size()];
        for (int i = 0; i < ls.size(); i++) {
            resultArray[i] = (String) ls.get(i);
        }
        return resultArray;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected final String replace(String str, String fromStr, String toStr) {
        return Srl.replace(str, fromStr, toStr);
    }

    // =====================================================================================
    //                                                                             Deep Copy
    //                                                                             =========
    public Object createDeepCopy() {
        final SplitOptionParts deepCopy = new SplitOptionParts();
        deepCopy._delimiter = _delimiter;
        deepCopy._subDelimiterList = _subDelimiterList;
        deepCopy._splitLimitCount = _splitLimitCount;
        return deepCopy;
    }
}
