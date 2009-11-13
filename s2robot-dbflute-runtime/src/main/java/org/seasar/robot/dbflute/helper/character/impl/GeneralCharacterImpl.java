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
package org.seasar.robot.dbflute.helper.character.impl;

import org.seasar.robot.dbflute.helper.character.GeneralCharacter;

/**
 * The implementation of general character.
 * @author jflute
 */
public class GeneralCharacterImpl implements GeneralCharacter {

    public String toSingleByteAlphabet(String target) {
        if (target == null) {
            return target;
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < target.length(); i++) {
            final char currentChar = target.charAt(i);

            if (currentChar >= 0xff21 && currentChar <= 0xff3a) {
                sb.append(toSingleByteCharacter(currentChar));
            } else if (currentChar >= 0xff41 && currentChar <= 0xff5a) {
                sb.append(toSingleByteCharacter(currentChar));
            } else {
                sb.append(currentChar);
            }
        }
        return sb.toString();
    }

    public String toSingleByteNumber(String target) {
        if (target == null) {
            return target;
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < target.length(); i++) {
            final char currentChar = target.charAt(i);
            if (currentChar >= 0xff10 && currentChar <= 0xff19) {
                sb.append(toSingleByteCharacter(currentChar));
            } else {
                sb.append(currentChar);
            }
        }
        return sb.toString();
    }

    public String toSingleByteAlphabetNumber(String target) {
        if (target == null) {
            return target;
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < target.length(); i++) {
            final char currentChar = target.charAt(i);

            if (currentChar >= 0xff10 && currentChar <= 0xff19) {
                sb.append(toSingleByteCharacter(currentChar));
            } else if (currentChar >= 0xff21 && currentChar <= 0xff3a) {
                sb.append(toSingleByteCharacter(currentChar));
            } else if (currentChar >= 0xff41 && currentChar <= 0xff5a) {
                sb.append(toSingleByteCharacter(currentChar));
            } else {
                sb.append(currentChar);
            }
        }
        return sb.toString();
    }

    public String toSingleByteAlphabetNumberMark(String target) {
        if (target == null) {
            return target;
        }
        final StringBuffer sb = new StringBuffer(target.length());
        for (int i = 0; i < target.length(); i++) {
            final char currentChar = target.charAt(i);
            if (currentChar >= 0xff01 && currentChar <= 0xff5e) {
                sb.append(toSingleByteCharacter(currentChar));

            // It needs to  append more mark...
            } else if (currentChar == '\u2019' || currentChar == '\u2018' || currentChar == '\u2032') {
                sb.append('\'');
            } else if (currentChar == '\u201d' || currentChar == '\u201c' || currentChar == '\u2033') {
                sb.append('\"');
            } else if (currentChar == '\uffe5') {
                sb.append('\\');
            } else if (currentChar == '\u2010') {
                sb.append('-');
            } else if (currentChar == '\uff5e') {
                sb.append('~');
            } else {
                sb.append(currentChar);
            }
        }
        return sb.toString();
    }

    protected char toSingleByteCharacter(final char currentChar) {
        return (char) (currentChar - 0xfee0);
    }
}
