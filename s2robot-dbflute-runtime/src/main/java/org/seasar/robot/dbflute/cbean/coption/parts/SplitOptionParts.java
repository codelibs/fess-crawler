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
package org.seasar.robot.dbflute.cbean.coption.parts;

/**
 * The interface of condition-option.
 * @author jflute
 */
public class SplitOptionParts {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected String _split;
    protected String _splitContainedDelimiter;
    protected int _splitLimitCount;

    // =====================================================================================
    //                                                                                  Main
    //                                                                                  ====
    public boolean isSplit() {
        return _split != null;
    }

    public void splitBySpace() {
        _split = " ";
    }

    public void splitBySpace(int splitLimitCount) {
        _split = " ";
        _splitLimitCount = splitLimitCount;
    }

    public void splitBySpaceContainsDoubleByte() {
        _split = " ";
        _splitContainedDelimiter = "\u3000";
    }

    public void splitBySpaceContainsDoubleByte(int splitLimitCount) {
        _split = " ";
        _splitContainedDelimiter = "\u3000";
        _splitLimitCount = splitLimitCount;
    }

    public void splitByPipeLine() {
        _split = "|";
    }

    public void splitByPipeLine(int splitLimitCount) {
        _split = "|";
        _splitLimitCount = splitLimitCount;
    }

    // =====================================================================================
    //                                                                            Real Value
    //                                                                            ==========
    public String[] generateSplitValueArray(String value) {
        if (value == null) {
            String msg = "The argument[value] should not be null!";
            throw new IllegalArgumentException(msg);
        }
        value = repalceContainedDelimiterToRealDelimiter(value);
        final java.util.StringTokenizer st = new java.util.StringTokenizer(value, _split);
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

    protected String repalceContainedDelimiterToRealDelimiter(String value) {
        if (value == null) {
            return value;
        }
        if (_splitContainedDelimiter == null) {
            return value;
        }
        if (_split == null) {
            return value;
        }
        return replace(value, _splitContainedDelimiter, _split);
    }

    protected String[] removeInvalidValue(String[] values) {
        final java.util.List<String> ls = new java.util.ArrayList<String>();
        for (int i = 0; i < values.length; i++) {
            final String value = values[i];
            if (value == null || value.equals("")) {// Don't trim!!!
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

    // =====================================================================================
    //                                                                                Helper
    //                                                                                ======
    protected final String replace(String text, String fromText, String toText) {

        if (text == null || fromText == null || toText == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(100);
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

    // =====================================================================================
    //                                                                              DeepCopy
    //                                                                              ========
    public Object createDeepCopy() {
        final SplitOptionParts deepCopy = new SplitOptionParts();
        deepCopy._split = _split;
        deepCopy._splitContainedDelimiter = _splitContainedDelimiter;
        deepCopy._splitLimitCount = _splitLimitCount;
        return deepCopy;
    }
}
