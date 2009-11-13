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
package org.seasar.robot.dbflute.cbean.coption;

import org.seasar.robot.dbflute.cbean.coption.parts.SplitOptionParts;
import org.seasar.robot.dbflute.cbean.coption.parts.ToSingleByteOptionParts;
import org.seasar.robot.dbflute.cbean.coption.parts.ToUpperLowerCaseOptionParts;
import org.seasar.robot.dbflute.cbean.coption.parts.local.JapaneseOptionPartsAgent;
import org.seasar.robot.dbflute.util.DfStringUtil;

/**
 * The class of simple-string-option.
 * @author jflute
 */
public class SimpleStringOption implements ConditionOption {

    protected SplitOptionParts _splitOptionParts;
    protected ToUpperLowerCaseOptionParts _toUpperLowerCaseOptionParts;
    protected ToSingleByteOptionParts _toSingleByteCaseOptionParts;
    protected JapaneseOptionPartsAgent _japaneseOptionPartsAgent;

    // =====================================================================================
    //                                                                           Rear Option
    //                                                                           ===========
    public String getRearOption() {
        return "";
    }

    // =====================================================================================
    //                                                                                 Split
    //                                                                                 =====
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

    protected SplitOptionParts getSplitOptionParts() {
        if (_splitOptionParts == null) {
            _splitOptionParts = new SplitOptionParts();
        }
        return _splitOptionParts;
    }

    public boolean isSplit() {
        return getSplitOptionParts().isSplit();
    }

    public String[] generateSplitValueArray(String value) {
        return getSplitOptionParts().generateSplitValueArray(value);
    }

    // =====================================================================================
    //                                                                   To Upper/Lower Case
    //                                                                   ===================
    protected SimpleStringOption doToUpperCase() {
        getToUpperLowerCaseOptionParts().toUpperCase();
        return this;
    }

    protected SimpleStringOption doToLowerCase() {
        getToUpperLowerCaseOptionParts().toLowerCase();
        return this;
    }

    protected ToUpperLowerCaseOptionParts getToUpperLowerCaseOptionParts() {
        if (_toUpperLowerCaseOptionParts == null) {
            _toUpperLowerCaseOptionParts = new ToUpperLowerCaseOptionParts();
        }
        return _toUpperLowerCaseOptionParts;
    }

    // =====================================================================================
    //                                                                        To Single Byte
    //                                                                        ==============
    protected SimpleStringOption doToSingleByteSpace() {
        getToSingleByteOptionParts().toSingleByteSpace();
        return this;
    }

    protected SimpleStringOption doToSingleByteAlphabetNumber() {
        getToSingleByteOptionParts().toSingleByteAlphabetNumber();
        return this;
    }

    protected SimpleStringOption doToSingleByteAlphabetNumberMark() {
        getToSingleByteOptionParts().toSingleByteAlphabetNumberMark();
        return this;
    }

    protected ToSingleByteOptionParts getToSingleByteOptionParts() {
        if (_toSingleByteCaseOptionParts == null) {
            _toSingleByteCaseOptionParts = new ToSingleByteOptionParts();
        }
        return _toSingleByteCaseOptionParts;
    }

    // =====================================================================================
    //                                                                        To Double Byte
    //                                                                        ==============

    // =====================================================================================
    //                                                                              Japanese
    //                                                                              ========
    protected JapaneseOptionPartsAgent doLocalJapanese() {
        return getJapaneseOptionPartsAgent();
    }

    protected JapaneseOptionPartsAgent getJapaneseOptionPartsAgent() {
        if (_japaneseOptionPartsAgent == null) {
            _japaneseOptionPartsAgent = new JapaneseOptionPartsAgent();
        }
        return _japaneseOptionPartsAgent;
    }

    // =====================================================================================
    //                                                                            Real Value
    //                                                                            ==========
    public String generateRealValue(String value) {
        value = getToUpperLowerCaseOptionParts().generateRealValue(value);
        value = getToSingleByteOptionParts().generateRealValue(value);
        value = getJapaneseOptionPartsAgent().generateRealValue(value);
        return value;
    }

    // =====================================================================================
    //                                                                        General Helper
    //                                                                        ==============
    protected String replace(String text, String fromText, String toText) {
	    return DfStringUtil.replace(text, fromText, toText);
    }

    // =====================================================================================
    //                                                                              DeepCopy
    //                                                                              ========
    public Object createDeepCopy() {
        final SimpleStringOption deepCopy = newDeepCopyInstance();
        deepCopy._splitOptionParts = _splitOptionParts != null ? (SplitOptionParts)_splitOptionParts.createDeepCopy() : null;
        deepCopy._toUpperLowerCaseOptionParts = _toUpperLowerCaseOptionParts != null ? (ToUpperLowerCaseOptionParts)_toUpperLowerCaseOptionParts.createDeepCopy() : null;
        deepCopy._toSingleByteCaseOptionParts = _toSingleByteCaseOptionParts != null ? (ToSingleByteOptionParts)_toSingleByteCaseOptionParts.createDeepCopy() : null;
        deepCopy._japaneseOptionPartsAgent = _japaneseOptionPartsAgent != null ? (JapaneseOptionPartsAgent)_japaneseOptionPartsAgent.createDeepCopy() : null;
        return deepCopy;
    }
    protected SimpleStringOption newDeepCopyInstance() {
        return new SimpleStringOption();
    }
}
