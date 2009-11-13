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
package org.seasar.robot.dbflute.cbean.coption.parts.local;

import org.seasar.robot.dbflute.helper.character.JapaneseCharacter;
import org.seasar.robot.dbflute.helper.character.impl.JapaneseCharacterImpl;

/**
 * The class of condition-option-parts-agent.
 * @author jflute
 */
public class JapaneseOptionPartsAgent {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected boolean _toDoubleByteKatakana;
    protected boolean _removeLastLongVowel;
    private JapaneseCharacter _japaneseCharacter;

    // =====================================================================================
    //                                                                                  Main
    //                                                                                  ====
    public boolean isToDoubleByteKatakana() {
        return _toDoubleByteKatakana;
    }

    public void toDoubleByteKatakana() {
        _toDoubleByteKatakana = true;
    }

    public boolean isRemoveLastLongVowel() {
        return _removeLastLongVowel;
    }
    public void removeLastLongVowel() {
        _removeLastLongVowel = true;
    }

    // =====================================================================================
    //                                                                            Real Value
    //                                                                            ==========
    public String generateRealValue(String value) {
        if (value == null) {
            return value;
        }

        // To Double Byte
        if (_toDoubleByteKatakana) {
            value = getJapaneseCharacter().toDoubleByteKatakana(value);
        }

        // Remove
        if (_removeLastLongVowel) {
            if (value != null && value.endsWith("\u30fc")) {
                value = value.substring(0, value.length() - "\u30fc".length());
            }
        }
        return value;
    }

    // =====================================================================================
    //                                                                                Helper
    //                                                                                ======
    protected JapaneseCharacter getJapaneseCharacter() {
        if (_japaneseCharacter == null) {
            _japaneseCharacter = new JapaneseCharacterImpl();
        }
        return _japaneseCharacter;
    }

    // =====================================================================================
    //                                                                              DeepCopy
    //                                                                              ========
    public Object createDeepCopy() {
        final JapaneseOptionPartsAgent deepCopy = new JapaneseOptionPartsAgent();
        deepCopy._toDoubleByteKatakana = _toDoubleByteKatakana;
        deepCopy._removeLastLongVowel = _removeLastLongVowel;
        return deepCopy;
    }
}
