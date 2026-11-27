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

import java.util.regex.Pattern;

/**
 * Removes the user name and password from a URL, so that a URL like
 * {@code http://user:password@host/} does not reach a log.
 */
public final class UserInfoUtil {

    /**
     * An absolute URL with a userinfo anywhere in a text: group 1 is the scheme with {@code ://}, followed by the
     * userinfo up to its last {@code @} before the path, the query, the fragment or a line break. As in
     * {@link #removeUserInfo}, a space does not end the userinfo, so that a password with a space is removed too.
     */
    private static final Pattern USER_INFO_IN_TEXT = Pattern.compile("([A-Za-z][A-Za-z0-9+.\\-]*://)[^/?#\\r\\n]*@");

    /** Separator between the scheme and the authority of an absolute URL. */
    private static final String SCHEME_SEPARATOR = "://";

    private UserInfoUtil() {
        // utility class
    }

    /**
     * Removes the userinfo ({@code user:password@} or {@code user@}) from the authority of an absolute
     * URL of the form {@code scheme://userinfo@host...} and returns everything else unchanged. It never
     * throws, whatever the input is.
     *
     * @param url the URL (may be {@code null})
     * @return the URL without the userinfo, the URL itself when it has none, or {@code null} for
     *         {@code null}
     */
    public static String removeUserInfo(final String url) {
        if (url == null || url.indexOf('@') < 0) {
            return url;
        }
        final int schemeEnd = url.indexOf(SCHEME_SEPARATOR);
        if (schemeEnd <= 0 || !isScheme(url, schemeEnd)) {
            return url;
        }
        final int authorityStart = schemeEnd + SCHEME_SEPARATOR.length();
        int authorityEnd = url.length();
        for (int i = authorityStart; i < authorityEnd; i++) {
            final char c = url.charAt(i);
            if (c == '/' || c == '?' || c == '#') {
                authorityEnd = i;
                break;
            }
        }
        final int at = url.lastIndexOf('@', authorityEnd - 1);
        if (at < authorityStart) {
            return url;
        }
        return url.substring(0, authorityStart) + url.substring(at + 1);
    }

    /**
     * Removes the userinfo from every absolute URL of the form {@code scheme://userinfo@host...} in a text, such as
     * the message of an exception that names a URL in the middle of a sentence, and returns the rest of the text
     * unchanged. It never throws, whatever the input is.
     * <p>
     * A URL without a path that is followed on the same line by an {@code @}, as in
     * {@code smb://host is down, ask admin@example.com}, loses the text up to that {@code @}: a space cannot end the
     * userinfo, and removing too much is preferred to leaving a password in the text.
     * </p>
     *
     * @param text the text (may be {@code null})
     * @return the text without the userinfo of its URLs, the text itself when none has one, or {@code null} for
     *         {@code null}
     */
    public static String removeUserInfoFromText(final String text) {
        if (text == null || text.indexOf('@') < 0) {
            return text;
        }
        return USER_INFO_IN_TEXT.matcher(text).replaceAll("$1");
    }

    private static boolean isScheme(final String url, final int end) {
        if (!Character.isLetter(url.charAt(0)) || url.charAt(0) > 127) {
            return false;
        }
        for (int i = 1; i < end; i++) {
            final char c = url.charAt(i);
            final boolean letterOrDigit = c < 128 && Character.isLetterOrDigit(c);
            if (!letterOrDigit && c != '+' && c != '-' && c != '.') {
                return false;
            }
        }
        return true;
    }
}
