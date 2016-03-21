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
 */
public class TextUtilTest extends PlainTestCase {

    public void test_getContent() {
        assertEquals("", TextUtil.normalizeText((String) null, 100, -1, -1));
        assertEquals("", TextUtil.normalizeText("", 100, -1, -1));
        assertEquals("", TextUtil.normalizeText(" ", 100, -1, -1));
        assertEquals("", TextUtil.normalizeText("  ", 100, -1, -1));
        assertEquals("", TextUtil.normalizeText("\t", 100, -1, -1));
        assertEquals("", TextUtil.normalizeText("\t\t", 100, -1, -1));
        assertEquals("", TextUtil.normalizeText("\t \t", 100, -1, -1));
        assertEquals("aaa bbb", TextUtil.normalizeText("aaa \u00a0 bbb", 100, -1, -1));
        assertEquals("123 abc", TextUtil.normalizeText(" 123 abc ", 100, -1, -1));
        assertEquals("１２３ あいう", TextUtil.normalizeText("　１２３　あいう　", 100, -1, -1));
        assertEquals("123 abc", TextUtil.normalizeText(" 123\nabc ", 100, -1, -1));
    }

    public void test_getContent_maxAlphanum() {
        assertEquals("", TextUtil.normalizeText((String) null, 100, 2, -1));
        assertEquals("", TextUtil.normalizeText("", 100, 2, -1));
        assertEquals("", TextUtil.normalizeText(" ", 100, 2, -1));
        assertEquals("", TextUtil.normalizeText("  ", 100, 2, -1));
        assertEquals("", TextUtil.normalizeText("\t", 100, 2, -1));
        assertEquals("", TextUtil.normalizeText("\t\t", 100, 2, -1));
        assertEquals("", TextUtil.normalizeText("\t \t", 100, 2, -1));
        assertEquals("12 ab", TextUtil.normalizeText(" 123 abc ", 100, 2, -1));
        assertEquals("１２３ あいう", TextUtil.normalizeText("　１２３　あいう　", 100, 2, -1));
        assertEquals("12 ab", TextUtil.normalizeText(" 123\nabc ", 100, 2, -1));
        assertEquals("12", TextUtil.normalizeText(" 123abc ", 100, 2, -1));
    }

    public void test_getContent_maxSymbol() {
        assertEquals("", TextUtil.normalizeText((String) null, 100, -1, 2));
        assertEquals("", TextUtil.normalizeText("", 100, -1, 2));
        assertEquals("", TextUtil.normalizeText(" ", 100, -1, 2));
        assertEquals("", TextUtil.normalizeText("  ", 100, -1, 2));
        assertEquals("", TextUtil.normalizeText("\t", 100, -1, 2));
        assertEquals("", TextUtil.normalizeText("\t\t", 100, -1, 2));
        assertEquals("", TextUtil.normalizeText("\t \t", 100, -1, 2));
        assertEquals("123 abc", TextUtil.normalizeText(" 123 abc ", 100, -1, 2));
        assertEquals("１２３ あいう", TextUtil.normalizeText("　１２３　あいう　", 100, -1, 2));
        assertEquals("123 abc", TextUtil.normalizeText(" 123\nabc ", 100, -1, 2));
        assertEquals("123abc", TextUtil.normalizeText(" 123abc ", 100, -1, 2));

        assertEquals("!!", TextUtil.normalizeText("!!!", 100, -1, 2));
        assertEquals("//", TextUtil.normalizeText("///", 100, -1, 2));
        assertEquals("::", TextUtil.normalizeText(":::", 100, -1, 2));
        assertEquals("@@", TextUtil.normalizeText("@@@", 100, -1, 2));
        assertEquals("[[", TextUtil.normalizeText("[[[", 100, -1, 2));
        assertEquals("``", TextUtil.normalizeText("```", 100, -1, 2));
        assertEquals("{{", TextUtil.normalizeText("{{{", 100, -1, 2));
        assertEquals("~~", TextUtil.normalizeText("~~~", 100, -1, 2));
        assertEquals("!\"", TextUtil.normalizeText("!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~", 100, -1, 2));
    }
}
