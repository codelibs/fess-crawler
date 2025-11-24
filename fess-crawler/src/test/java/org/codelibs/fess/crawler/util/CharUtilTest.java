/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.util;

import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for CharUtil.
 *
 * @author shinsuke
 */
public class CharUtilTest extends PlainTestCase {

    public void test_isUrlChar_lowercase() {
        // Test lowercase letters
        assertTrue(CharUtil.isUrlChar('a'));
        assertTrue(CharUtil.isUrlChar('m'));
        assertTrue(CharUtil.isUrlChar('z'));
    }

    public void test_isUrlChar_uppercase() {
        // Test uppercase letters
        assertTrue(CharUtil.isUrlChar('A'));
        assertTrue(CharUtil.isUrlChar('M'));
        assertTrue(CharUtil.isUrlChar('Z'));
    }

    public void test_isUrlChar_digits() {
        // Test digits
        assertTrue(CharUtil.isUrlChar('0'));
        assertTrue(CharUtil.isUrlChar('5'));
        assertTrue(CharUtil.isUrlChar('9'));
    }

    public void test_isUrlChar_commonSpecialChars() {
        // Test common special characters
        assertTrue(CharUtil.isUrlChar('.'));
        assertTrue(CharUtil.isUrlChar('-'));
        assertTrue(CharUtil.isUrlChar('*'));
        assertTrue(CharUtil.isUrlChar('_'));
    }

    public void test_isUrlChar_urlSpecialChars() {
        // Test URL-specific special characters
        assertTrue(CharUtil.isUrlChar(':'));
        assertTrue(CharUtil.isUrlChar('/'));
        assertTrue(CharUtil.isUrlChar('+'));
        assertTrue(CharUtil.isUrlChar('%'));
        assertTrue(CharUtil.isUrlChar('='));
        assertTrue(CharUtil.isUrlChar('&'));
        assertTrue(CharUtil.isUrlChar('?'));
        assertTrue(CharUtil.isUrlChar('#'));
    }

    public void test_isUrlChar_brackets() {
        // Test brackets
        assertTrue(CharUtil.isUrlChar('['));
        assertTrue(CharUtil.isUrlChar(']'));
    }

    public void test_isUrlChar_otherAllowedChars() {
        // Test other allowed characters
        assertTrue(CharUtil.isUrlChar('@'));
        assertTrue(CharUtil.isUrlChar('~'));
        assertTrue(CharUtil.isUrlChar('!'));
        assertTrue(CharUtil.isUrlChar('$'));
        assertTrue(CharUtil.isUrlChar('\''));
        assertTrue(CharUtil.isUrlChar('('));
        assertTrue(CharUtil.isUrlChar(')'));
        assertTrue(CharUtil.isUrlChar(','));
        assertTrue(CharUtil.isUrlChar(';'));
    }

    public void test_isUrlChar_notAllowed() {
        // Test characters that are not allowed
        assertFalse(CharUtil.isUrlChar(' ')); // space
        assertFalse(CharUtil.isUrlChar('\t')); // tab
        assertFalse(CharUtil.isUrlChar('\n')); // newline
        assertFalse(CharUtil.isUrlChar('\r')); // carriage return
        assertFalse(CharUtil.isUrlChar('<')); // less than
        assertFalse(CharUtil.isUrlChar('>')); // greater than
        assertFalse(CharUtil.isUrlChar('"')); // double quote
        assertFalse(CharUtil.isUrlChar('\\')); // backslash
        assertFalse(CharUtil.isUrlChar('`')); // backtick
    }

    public void test_isUrlChar_controlCharacters() {
        // Test control characters
        assertFalse(CharUtil.isUrlChar('\u0000')); // null
        assertFalse(CharUtil.isUrlChar('\u0001')); // SOH
        assertFalse(CharUtil.isUrlChar('\u001F')); // US
        assertFalse(CharUtil.isUrlChar('\u007F')); // DEL
    }

    public void test_isUrlChar_extendedAscii() {
        // Test extended ASCII characters (not valid URL chars without encoding)
        assertFalse(CharUtil.isUrlChar('\u00A0')); // non-breaking space
        assertFalse(CharUtil.isUrlChar('\u00FF')); // ÿ
    }

    public void test_isUrlChar_unicode() {
        // Test Unicode characters (not valid URL chars without encoding)
        assertFalse(CharUtil.isUrlChar('\u3042')); // あ (Hiragana)
        assertFalse(CharUtil.isUrlChar('\u4E00')); // 一 (CJK)
        assertFalse(CharUtil.isUrlChar('\u0410')); // А (Cyrillic)
    }

    public void test_isUrlChar_allValidChars() {
        // Test all valid URL characters in a comprehensive way
        String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.-*_:/+%=&?#[]@~!$'(),;";
        for (char c : validChars.toCharArray()) {
            assertTrue("Character '" + c + "' should be a valid URL character", CharUtil.isUrlChar(c));
        }
    }

    public void test_isUrlChar_boundaryChars() {
        // Test boundary characters for ranges
        assertTrue(CharUtil.isUrlChar('a')); // first lowercase
        assertTrue(CharUtil.isUrlChar('z')); // last lowercase
        assertTrue(CharUtil.isUrlChar('A')); // first uppercase
        assertTrue(CharUtil.isUrlChar('Z')); // last uppercase
        assertTrue(CharUtil.isUrlChar('0')); // first digit
        assertTrue(CharUtil.isUrlChar('9')); // last digit

        // Test characters just outside ranges that are not valid
        assertFalse(CharUtil.isUrlChar('`')); // backtick (just before 'a')
        assertFalse(CharUtil.isUrlChar('{')); // left brace (just after 'z')
        assertFalse(CharUtil.isUrlChar('^')); // caret (before 'a' range)
        assertFalse(CharUtil.isUrlChar('|')); // pipe (not in valid set)
    }
}
