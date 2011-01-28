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
package org.seasar.robot.dbflute.cbean.coption;

import java.io.Serializable;
import java.util.List;

import org.seasar.robot.dbflute.cbean.coption.parts.SplitOptionParts;
import org.seasar.robot.dbflute.util.Srl;

/**
 * The class of simple-string-option.
 * @author jflute
 */
public class SimpleStringOption implements ConditionOption, Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected SplitOptionParts _splitOptionParts;

    // ===================================================================================
    //                                                                         Rear Option
    //                                                                         ===========
    public String getRearOption() {
        return "";
    }

    // ===================================================================================
    //                                                                               Split
    //                                                                               =====
    protected SimpleStringOption doSplitByBlank() {
        getSplitOptionParts().splitByBlank();
        return this;
    }

    protected SimpleStringOption doSplitByBlank(int splitLimitCount) {
        getSplitOptionParts().splitByBlank(splitLimitCount);
        return this;
    }

    protected SimpleStringOption doSplitBySpace() {
        getSplitOptionParts().splitBySpace();
        return this;
    }

    protected SimpleStringOption doSplitBySpace(int splitLimitCount) {
        getSplitOptionParts().splitBySpace(splitLimitCount);
        return this;
    }

    protected SimpleStringOption doSplitBySpaceContainsDoubleByte() {
        getSplitOptionParts().splitBySpaceContainsDoubleByte();
        return this;
    }

    protected SimpleStringOption doSplitBySpaceContainsDoubleByte(int splitLimitCount) {
        getSplitOptionParts().splitBySpaceContainsDoubleByte(splitLimitCount);
        return this;
    }

    protected SimpleStringOption doSplitByPipeLine() {
        getSplitOptionParts().splitByPipeLine();
        return this;
    }

    protected SimpleStringOption doSplitByPipeLine(int splitLimitCount) {
        getSplitOptionParts().splitByPipeLine(splitLimitCount);
        return this;
    }

    protected SimpleStringOption doSplitByVarious(List<String> delimiterList) {
        getSplitOptionParts().splitByVarious(delimiterList);
        return this;
    }

    protected SimpleStringOption doSplitByVarious(List<String> delimiterList, int splitLimitCount) {
        getSplitOptionParts().splitByVarious(delimiterList, splitLimitCount);
        return this;
    }

    protected SplitOptionParts getSplitOptionParts() {
        if (_splitOptionParts == null) {
            _splitOptionParts = createSplitOptionParts();
        }
        return _splitOptionParts;
    }

    protected SplitOptionParts createSplitOptionParts() {
        return new SplitOptionParts();
    }

    public boolean isSplit() {
        return getSplitOptionParts().isSplit();
    }

    public String[] generateSplitValueArray(String value) {
        return getSplitOptionParts().generateSplitValueArray(value);
    }

    // ===================================================================================
    //                                                                          Real Value
    //                                                                          ==========
    public String generateRealValue(String value) {
        return value;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replace(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    // ===================================================================================
    //                                                                           Deep Copy
    //                                                                           =========
    public Object createDeepCopy() {
        final SimpleStringOption deepCopy = newDeepCopyInstance();
        if (_splitOptionParts != null) {
            deepCopy._splitOptionParts = (SplitOptionParts) _splitOptionParts;
        }
        return deepCopy;
    }

    protected SimpleStringOption newDeepCopyInstance() {
        return new SimpleStringOption();
    }
}
