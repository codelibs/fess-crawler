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

import org.seasar.robot.dbflute.helper.character.GeneralCharacter;
import org.seasar.robot.dbflute.helper.character.impl.GeneralCharacterImpl;

/**
 * The interface of condition-option.
 * @author jflute
 */
public class ToSingleByteOptionParts {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected boolean _toSingleByteSpace;
    protected boolean _toSingleByteAlphabetNumber;
    protected boolean _toSingleByteAlphabetNumberMark;

    private GeneralCharacter _generalCharacter;

    // =====================================================================================
    //                                                                                  Main
    //                                                                                  ====
    public boolean isToSingleByteSpace() {
        return _toSingleByteSpace;
    }
    public void toSingleByteSpace() {
        _toSingleByteSpace = true;
    }

    public void toSingleByteAlphabetNumber() {
        _toSingleByteAlphabetNumber = true;
    }

    public void toSingleByteAlphabetNumberMark() {
        _toSingleByteAlphabetNumberMark = true;
    }

    // =====================================================================================
    //                                                                            Real Value
    //                                                                            ==========
    public String generateRealValue(String value) {
        if (value == null) {
            return value;
        }

        // To Single Byte
        if (_toSingleByteSpace) {
            value = value.replaceAll("\u3000", " ");
        }
        if (_toSingleByteAlphabetNumberMark) {
            value = getGeneralCharacter().toSingleByteAlphabetNumberMark(value);
        } else if (_toSingleByteAlphabetNumber) {
            value = getGeneralCharacter().toSingleByteAlphabetNumber(value);
        }
        return value;
    }

    // =====================================================================================
    //                                                                                Helper
    //                                                                                ======
    protected GeneralCharacter getGeneralCharacter() {
        if (_generalCharacter == null) {
            _generalCharacter = new GeneralCharacterImpl();
        }
        return _generalCharacter;
    }

    // =====================================================================================
    //                                                                              DeepCopy
    //                                                                              ========
    public Object createDeepCopy() {
        final ToSingleByteOptionParts deepCopy = new ToSingleByteOptionParts();
        deepCopy._toSingleByteSpace = _toSingleByteSpace;
        deepCopy._toSingleByteAlphabetNumber = _toSingleByteAlphabetNumber;
        deepCopy._toSingleByteAlphabetNumberMark = _toSingleByteAlphabetNumberMark;
        return deepCopy;
    }
}
