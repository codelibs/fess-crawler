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

/**
 * Utility class for character-related operations.
 */
public final class CharUtil {
    private CharUtil() {
    }

    /**
     * Checks if the given character is a valid URL character.
     *
     * Valid URL characters include:
     * - Lowercase letters (a-z)
     * - Uppercase letters (A-Z)
     * - Digits (0-9)
     * - Special characters: . - * _ : / + % = &amp; ? # [ ] @ ~ ! $ ' ( ) , ;
     *
     * @param c the character to check
     * @return {@code true} if the character is a valid URL character, {@code false} otherwise
     */
    public static boolean isUrlChar(final char c) {
        if (c >= 'a' && c <= 'z' //
                || c >= 'A' && c <= 'Z' //
                || c >= '0' && c <= '9' //
                || c == '.' //
                || c == '-' //
                || c == '*' //
                || c == '_' //
                || c == ':' // added
                || c == '/' // added
                || c == '+' // added
                || c == '%' // added
                || c == '=' // added
                || c == '&' // added
                || c == '?' // added
                || c == '#' // added
                || c == '[' // added
                || c == ']' // added
                || c == '@' // added
                || c == '~' // added
                || c == '!' // added
                || c == '$' // added
                || c == '\'' // added
                || c == '(' // added
                || c == ')' // added
                || c == ',' // added
                || c == ';' // added
        ) {
            return true;
        }
        return false;
    }
}
