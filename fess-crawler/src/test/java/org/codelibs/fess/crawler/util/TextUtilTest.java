/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
 * @author shinsuke
 * @author kaorufuzita
 */
public class TextUtilTest extends PlainTestCase {

    public void test_getContent() {
        assertEquals("", TextUtil.normalizeText((String) null, 100, -1, -1, false));
        assertEquals("", TextUtil.normalizeText("", 100, -1, -1, false));
        assertEquals("", TextUtil.normalizeText(" ", 100, -1, -1, false));
        assertEquals("", TextUtil.normalizeText("  ", 100, -1, -1, false));
        assertEquals("", TextUtil.normalizeText("\t", 100, -1, -1, false));
        assertEquals("", TextUtil.normalizeText("\t\t", 100, -1, -1, false));
        assertEquals("", TextUtil.normalizeText("\t \t", 100, -1, -1, false));
        assertEquals("aaa bbb", TextUtil.normalizeText("aaa \u00a0 bbb", 100, -1, -1, false));
        assertEquals("123 abc", TextUtil.normalizeText(" 123 abc ", 100, -1, -1, false));
        assertEquals("１２３ あいう", TextUtil.normalizeText("　１２３　あいう　", 100, -1, -1, false));
        assertEquals("123 abc", TextUtil.normalizeText(" 123\nabc ", 100, -1, -1, false));
        assertEquals("1234567890 1234567890", TextUtil.normalizeText("1234567890 1234567890", 100, -1, -1, false));
    }

    public void test_getContent_maxAlphanum() {
        assertEquals("", TextUtil.normalizeText((String) null, 100, 2, -1, false));
        assertEquals("", TextUtil.normalizeText("", 100, 2, -1, false));
        assertEquals("", TextUtil.normalizeText(" ", 100, 2, -1, false));
        assertEquals("", TextUtil.normalizeText("  ", 100, 2, -1, false));
        assertEquals("", TextUtil.normalizeText("\t", 100, 2, -1, false));
        assertEquals("", TextUtil.normalizeText("\t\t", 100, 2, -1, false));
        assertEquals("", TextUtil.normalizeText("\t \t", 100, 2, -1, false));
        assertEquals("12 ab", TextUtil.normalizeText(" 123 abc ", 100, 2, -1, false));
        assertEquals("１２３ あいう", TextUtil.normalizeText("　１２３　あいう　", 100, 2, -1, false));
        assertEquals("12 ab", TextUtil.normalizeText(" 123\nabc ", 100, 2, -1, false));
        assertEquals("12", TextUtil.normalizeText(" 123abc ", 100, 2, -1, false));
    }

    public void test_getContent_maxSymbol() {
        assertEquals("", TextUtil.normalizeText((String) null, 100, -1, 2, false));
        assertEquals("", TextUtil.normalizeText("", 100, -1, 2, false));
        assertEquals("", TextUtil.normalizeText(" ", 100, -1, 2, false));
        assertEquals("", TextUtil.normalizeText("  ", 100, -1, 2, false));
        assertEquals("", TextUtil.normalizeText("\t", 100, -1, 2, false));
        assertEquals("", TextUtil.normalizeText("\t\t", 100, -1, 2, false));
        assertEquals("", TextUtil.normalizeText("\t \t", 100, -1, 2, false));
        assertEquals("123 abc", TextUtil.normalizeText(" 123 abc ", 100, -1, 2, false));
        assertEquals("１２３ あいう", TextUtil.normalizeText("　１２３　あいう　", 100, -1, 2, false));
        assertEquals("123 abc", TextUtil.normalizeText(" 123\nabc ", 100, -1, 2, false));
        assertEquals("123abc", TextUtil.normalizeText(" 123abc ", 100, -1, 2, false));

        assertEquals("!!", TextUtil.normalizeText("!!!", 100, -1, 2, false));
        assertEquals("//", TextUtil.normalizeText("///", 100, -1, 2, false));
        assertEquals("::", TextUtil.normalizeText(":::", 100, -1, 2, false));
        assertEquals("@@", TextUtil.normalizeText("@@@", 100, -1, 2, false));
        assertEquals("[[", TextUtil.normalizeText("[[[", 100, -1, 2, false));
        assertEquals("``", TextUtil.normalizeText("```", 100, -1, 2, false));
        assertEquals("{{", TextUtil.normalizeText("{{{", 100, -1, 2, false));
        assertEquals("~~", TextUtil.normalizeText("~~~", 100, -1, 2, false));
        assertEquals("!\"", TextUtil.normalizeText("!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~", 100, -1, 2, false));
    }

    public void test_getContent_removeDuplication() {
        assertEquals("", TextUtil.normalizeText((String) null, 100, -1, -1, true));
        assertEquals("", TextUtil.normalizeText("", 100, -1, -1, true));
        assertEquals("", TextUtil.normalizeText(" ", 100, -1, -1, true));
        assertEquals("", TextUtil.normalizeText("  ", 100, -1, -1, true));
        assertEquals("", TextUtil.normalizeText("\t", 100, -1, -1, true));
        assertEquals("", TextUtil.normalizeText("\t\t", 100, -1, -1, true));
        assertEquals("", TextUtil.normalizeText("\t \t", 100, -1, -1, true));
        assertEquals("aaa bbb", TextUtil.normalizeText("aaa \u00a0 bbb", 100, -1, -1, true));
        assertEquals("123 abc", TextUtil.normalizeText(" 123 abc ", 100, -1, -1, true));
        assertEquals("１２３ あいう", TextUtil.normalizeText("　１２３　あいう　", 100, -1, -1, true));
        assertEquals("123 abc", TextUtil.normalizeText(" 123\nabc ", 100, -1, -1, true));

        assertEquals("", TextUtil.normalizeText((String) null, 100, 2, -1, true));
        assertEquals("", TextUtil.normalizeText("", 100, 2, -1, true));
        assertEquals("", TextUtil.normalizeText(" ", 100, 2, -1, true));
        assertEquals("", TextUtil.normalizeText("  ", 100, 2, -1, true));
        assertEquals("", TextUtil.normalizeText("\t", 100, 2, -1, true));
        assertEquals("", TextUtil.normalizeText("\t\t", 100, 2, -1, true));
        assertEquals("", TextUtil.normalizeText("\t \t", 100, 2, -1, true));
        assertEquals("12 ab", TextUtil.normalizeText(" 123 abc ", 100, 2, -1, true));
        assertEquals("１２３ あいう", TextUtil.normalizeText("　１２３　あいう　", 100, 2, -1, true));
        assertEquals("12 ab", TextUtil.normalizeText(" 123\nabc ", 100, 2, -1, true));
        assertEquals("12", TextUtil.normalizeText(" 123abc ", 100, 2, -1, true));

        assertEquals("!!", TextUtil.normalizeText("!!!", 100, -1, 2, true));
        assertEquals("//", TextUtil.normalizeText("///", 100, -1, 2, true));
        assertEquals("::", TextUtil.normalizeText(":::", 100, -1, 2, true));
        assertEquals("@@", TextUtil.normalizeText("@@@", 100, -1, 2, true));
        assertEquals("[[", TextUtil.normalizeText("[[[", 100, -1, 2, true));
        assertEquals("``", TextUtil.normalizeText("```", 100, -1, 2, true));
        assertEquals("{{", TextUtil.normalizeText("{{{", 100, -1, 2, true));
        assertEquals("~~", TextUtil.normalizeText("~~~", 100, -1, 2, true));
        assertEquals("!\"", TextUtil.normalizeText("!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~", 100, -1, 2, true));

        assertEquals("aaa bbb ccc", TextUtil.normalizeText("aaa bbb aaa ccc aaa", 100, -1, -1, true));
        assertEquals("aaa? bbb ccc", TextUtil.normalizeText("aaa? bbb aaa? ccc", 100, -1, -1, true));
        assertEquals("aaa #bbb# ccc?", TextUtil.normalizeText("aaa #bbb# aaa ccc? aaa", 100, -1, -1, true));
        assertEquals("123 abc", TextUtil.normalizeText(" 123 abc 123", 100, -1, -1, true));
//        assertEquals("あいう １２３", TextUtil.normalizeText("　あいう　１２３　あいう　", 100, -1, -1, true));
        assertEquals("123 abc", TextUtil.normalizeText(" 123\nabc\n123 ", 100, -1, -1, true));
    }
}
