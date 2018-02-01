/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.util.TextUtil.TextNormalizeContext;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * @author kaorufuzita
 */
public class TextUtilTest extends PlainTestCase {

    public void test_getContent() {
        assertEquals("", normalizeText((String) null, 100, -1, -1, false));
        assertEquals("", normalizeText("", 100, -1, -1, false));
        assertEquals("", normalizeText(" ", 100, -1, -1, false));
        assertEquals("", normalizeText("  ", 100, -1, -1, false));
        assertEquals("", normalizeText("\t", 100, -1, -1, false));
        assertEquals("", normalizeText("\t\t", 100, -1, -1, false));
        assertEquals("", normalizeText("\t \t", 100, -1, -1, false));
        assertEquals("aaa bbb", normalizeText("aaa \u00a0 bbb", 100, -1, -1, false));
        assertEquals("123 abc", normalizeText(" 123 abc ", 100, -1, -1, false));
        assertEquals("１２３ あいう", normalizeText("　１２３　あいう　", 100, -1, -1, false));
        assertEquals("123 abc", normalizeText(" 123\nabc ", 100, -1, -1, false));
        assertEquals("1234567890 1234567890", normalizeText("1234567890 1234567890", 100, -1, -1, false));
    }

    public void test_getContent_maxAlphanum() {
        assertEquals("", normalizeText((String) null, 100, 2, -1, false));
        assertEquals("", normalizeText("", 100, 2, -1, false));
        assertEquals("", normalizeText(" ", 100, 2, -1, false));
        assertEquals("", normalizeText("  ", 100, 2, -1, false));
        assertEquals("", normalizeText("\t", 100, 2, -1, false));
        assertEquals("", normalizeText("\t\t", 100, 2, -1, false));
        assertEquals("", normalizeText("\t \t", 100, 2, -1, false));
        assertEquals("12 ab", normalizeText(" 123 abc ", 100, 2, -1, false));
        assertEquals("１２３ あいう", normalizeText("　１２３　あいう　", 100, 2, -1, false));
        assertEquals("12 ab", normalizeText(" 123\nabc ", 100, 2, -1, false));
        assertEquals("12", normalizeText(" 123abc ", 100, 2, -1, false));
    }

    public void test_getContent_maxSymbol() {
        assertEquals("", normalizeText((String) null, 100, -1, 2, false));
        assertEquals("", normalizeText("", 100, -1, 2, false));
        assertEquals("", normalizeText(" ", 100, -1, 2, false));
        assertEquals("", normalizeText("  ", 100, -1, 2, false));
        assertEquals("", normalizeText("\t", 100, -1, 2, false));
        assertEquals("", normalizeText("\t\t", 100, -1, 2, false));
        assertEquals("", normalizeText("\t \t", 100, -1, 2, false));
        assertEquals("123 abc", normalizeText(" 123 abc ", 100, -1, 2, false));
        assertEquals("１２３ あいう", normalizeText("　１２３　あいう　", 100, -1, 2, false));
        assertEquals("123 abc", normalizeText(" 123\nabc ", 100, -1, 2, false));
        assertEquals("123abc", normalizeText(" 123abc ", 100, -1, 2, false));

        assertEquals("!!", normalizeText("!!!", 100, -1, 2, false));
        assertEquals("//", normalizeText("///", 100, -1, 2, false));
        assertEquals("::", normalizeText(":::", 100, -1, 2, false));
        assertEquals("@@", normalizeText("@@@", 100, -1, 2, false));
        assertEquals("[[", normalizeText("[[[", 100, -1, 2, false));
        assertEquals("``", normalizeText("```", 100, -1, 2, false));
        assertEquals("{{", normalizeText("{{{", 100, -1, 2, false));
        assertEquals("~~", normalizeText("~~~", 100, -1, 2, false));
        assertEquals("!\"", normalizeText("!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~", 100, -1, 2, false));
    }

    public void test_getContent_removeDuplication() {
        assertEquals("", normalizeText((String) null, 100, -1, -1, true));
        assertEquals("", normalizeText("", 100, -1, -1, true));
        assertEquals("", normalizeText(" ", 100, -1, -1, true));
        assertEquals("", normalizeText("  ", 100, -1, -1, true));
        assertEquals("", normalizeText("\t", 100, -1, -1, true));
        assertEquals("", normalizeText("\t\t", 100, -1, -1, true));
        assertEquals("", normalizeText("\t \t", 100, -1, -1, true));
        assertEquals("aaa bbb", normalizeText("aaa \u00a0 bbb", 100, -1, -1, true));
        assertEquals("123 abc", normalizeText(" 123 abc ", 100, -1, -1, true));
        assertEquals("１２３ あいう", normalizeText("　１２３　あいう　", 100, -1, -1, true));
        assertEquals("123 abc", normalizeText(" 123\nabc ", 100, -1, -1, true));

        assertEquals("", normalizeText((String) null, 100, 2, -1, true));
        assertEquals("", normalizeText("", 100, 2, -1, true));
        assertEquals("", normalizeText(" ", 100, 2, -1, true));
        assertEquals("", normalizeText("  ", 100, 2, -1, true));
        assertEquals("", normalizeText("\t", 100, 2, -1, true));
        assertEquals("", normalizeText("\t\t", 100, 2, -1, true));
        assertEquals("", normalizeText("\t \t", 100, 2, -1, true));
        assertEquals("12 ab", normalizeText(" 123 abc ", 100, 2, -1, true));
        assertEquals("１２３ あいう", normalizeText("　１２３　あいう　", 100, 2, -1, true));
        assertEquals("12 ab", normalizeText(" 123\nabc ", 100, 2, -1, true));
        assertEquals("12", normalizeText(" 123abc ", 100, 2, -1, true));

        assertEquals("!!", normalizeText("!!!", 100, -1, 2, true));
        assertEquals("//", normalizeText("///", 100, -1, 2, true));
        assertEquals("::", normalizeText(":::", 100, -1, 2, true));
        assertEquals("@@", normalizeText("@@@", 100, -1, 2, true));
        assertEquals("[[", normalizeText("[[[", 100, -1, 2, true));
        assertEquals("``", normalizeText("```", 100, -1, 2, true));
        assertEquals("{{", normalizeText("{{{", 100, -1, 2, true));
        assertEquals("~~", normalizeText("~~~", 100, -1, 2, true));
        assertEquals("!\"", normalizeText("!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~", 100, -1, 2, true));

        assertEquals("aaa bbb ccc", normalizeText("aaa bbb aaa ccc aaa", 100, -1, -1, true));
        assertEquals("aaa? bbb ccc", normalizeText("aaa? bbb aaa? ccc", 100, -1, -1, true));
        assertEquals("aaa #bbb ccc?", normalizeText("aaa #bbb# aaa ccc? aaa", 100, -1, -1, true));
        assertEquals("123 abc", normalizeText(" 123 abc 123", 100, -1, -1, true));
        assertEquals("あいう １２３ あいう", normalizeText("　あいう　１２３　あいう　", 100, -1, -1, true));
        assertEquals("123 abc", normalizeText(" 123\nabc\n123 ", 100, -1, -1, true));
        assertEquals("123# !$", normalizeText(" 123#123!$123  ", 100, -1, -1, true));
    }

    public static String normalizeText(final String str, final int initialCapacity, final int maxAlphanumTermSize,
            final int maxSymbolTermSize, final boolean removeDuplication) {
        if (str == null) {
            return StringUtil.EMPTY;
        }
        try (final Reader reader = new StringReader(str)) {
            return normalizeText(reader, initialCapacity, maxAlphanumTermSize, maxSymbolTermSize, removeDuplication);
        } catch (final IOException e) {
            return StringUtil.EMPTY;
        }
    }

    public static String normalizeText(final Reader reader, final int initialCapacity, final int maxAlphanumTermSize,
            final int maxSymbolTermSize, final boolean removeDuplication) {
        return new TextNormalizeContext(reader).initialCapacity(initialCapacity).maxAlphanumTermSize(maxAlphanumTermSize)
                .maxSymbolTermSize(maxSymbolTermSize).duplicateTermRemoved(removeDuplication).execute();
    }
}
