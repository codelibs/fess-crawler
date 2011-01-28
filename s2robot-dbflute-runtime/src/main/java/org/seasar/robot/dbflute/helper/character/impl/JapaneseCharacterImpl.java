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
package org.seasar.robot.dbflute.helper.character.impl;

import org.seasar.robot.dbflute.helper.character.JapaneseCharacter;

/**
 * The implementation of Japanese character.
 * @author jflute
 */
public class JapaneseCharacterImpl implements JapaneseCharacter {

    // Double-byte Katakana
    protected static final String TABLE_ZENKANA = "\u3002\u300c\u300d\u3001\u30fb\u30f2\u30a1\u30a3\u30a5"
            + "\u30a7\u30a9\u30e3\u30e5\u30e7\u30c3\u30fc\u30a2\u30a4"
            + "\u30a6\u30a8\u30aa\u30ab\u30ad\u30af\u30b1\u30b3\u30b5"
            + "\u30b7\u30b9\u30bb\u30bd\u30bf\u30c1\u30c4\u30c6\u30c8"
            + "\u30ca\u30cb\u30cc\u30cd\u30ce\u30cf\u30d2\u30d5\u30d8"
            + "\u30db\u30de\u30df\u30e0\u30e1\u30e2\u30e4\u30e6\u30e8"
            + "\u30e9\u30ea\u30eb\u30ec\u30ed\u30ef\u30f3\u309b\u309c";

    // (uff71 - uff9d)
    protected static final String DEF_DOUBLE_BYTE_VOICED_SOUND_NORMAL_KATAKANA = "\u30a2\u30a4\u30f4\u30a8\u30aa"
            + "\u30ac\u30ae\u30b0\u30b2\u30b4" + "\u30b6\u30b8\u30ba\u30bc\u30be" + "\u30c0\u30c2\u30c5\u30c7\u30c9"
            + "\u30ca\u30cb\u30cc\u30cd\u30ce" + "\u30d0\u30d3\u30d6\u30d9\u30dc" + "\u30de\u30df\u30e0\u30e1\u30e2"
            + "\u30e4\u30e6\u30e8" + "\u30e9\u30ea\u30eb\u30ec\u30ed" + "\u30ef\u30f3";

    // (uff66 - uff6f)
    protected static final String DEF_DOUBLE_BYTE_VOICED_SOUND_SPECIAL_KATAKANA = "\u30fa\u30a1\u30a3\u30a5\u30a7\u30a9\u30e3\u30e5\u30e7";

    // (u30cf - u30dd)
    protected static final String DEF_DOUBLE_BYTE_SEMI_VOICED_SOUND_KATAKANA = "\u30d1\u30d4\u30d7\u30da\u30dd";

    public String toDoubleByteKatakana(String target) {
        if (target == null) {
            return target;
        }

        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < target.length(); i++) {
            final char currentChar = target.charAt(i);
            final char nextChar;
            if (i < target.length() - 1) {
                nextChar = target.charAt(i + 1);
            } else {
                nextChar = ' ';
            }

            if (isVoicedSoundKatakana(currentChar, nextChar)) {
                if (currentChar >= 0xff66 && currentChar <= 0xff6f) {// Voiced sound special Katakana
                    sb.append(DEF_DOUBLE_BYTE_VOICED_SOUND_SPECIAL_KATAKANA.charAt(currentChar - 0xff66));
                    i++;
                } else if (currentChar >= 0xff71 && currentChar <= 0xff9d) {// Voiced sound normal Katakana
                    sb.append(DEF_DOUBLE_BYTE_VOICED_SOUND_NORMAL_KATAKANA.charAt(currentChar - 0xff71));
                    i++;
                }

            } else if (isSemiVoicedSoundKatakana(currentChar, nextChar)) {
                sb.append(DEF_DOUBLE_BYTE_SEMI_VOICED_SOUND_KATAKANA.charAt(currentChar - 0xff8a));
                i++;
            } else if (currentChar != 0xff9e && currentChar != 0xff9f) {
                if (currentChar >= 0xff61 && currentChar <= 0xff9f) {
                    sb.append(TABLE_ZENKANA.charAt(currentChar - 0xff61));
                } else {
                    sb.append(currentChar);
                }
            }
        }
        return sb.toString();
    }

    protected boolean isVoicedSoundKatakana(final char currentChar, final char nextChar) {
        return ((currentChar >= 0xff66 && currentChar <= 0xff6f) || (currentChar >= 0xff71 && (currentChar <= 0xff9d)))
                && (nextChar == 0xff9e);
    }

    protected boolean isSemiVoicedSoundKatakana(final char currentChar, final char nextChar) {
        return (currentChar >= 0xff8a && currentChar <= 0xff8e) && (nextChar == 0xff9f);
    }
}
