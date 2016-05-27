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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.codelibs.core.lang.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 * @author kaorufuzita
 *
 */
public final class TextUtil {
    private static final Logger logger = LoggerFactory.getLogger(TextUtil.class);

    private TextUtil() {
    }

    public static String normalizeText(final String str, final int initialCapacity, final int maxAlphanumTermSize,
            final int maxSymbolTermSize, final boolean removeDuplication) {
        if (str == null) {
            return StringUtil.EMPTY;
        }
        try (final Reader reader = new StringReader(str)) {
            return normalizeText(reader, initialCapacity, maxAlphanumTermSize, maxSymbolTermSize, removeDuplication);
        } catch (final IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to close reader.", e);
            }
            return StringUtil.EMPTY;
        }
    }

    public static String normalizeText(final Reader reader, final int initialCapacity, final int maxAlphanumTermSize,
            final int maxSymbolTermSize, final boolean removeDuplication) {
        if (reader == null) {
            return StringUtil.EMPTY;
        }
        final UnsafeStringBuilder buf = new UnsafeStringBuilder(initialCapacity);
        boolean isSpace = false;
        int alphanumSize = 0;
        int symbolSize = 0;
        int c;
        final Set<String> termCache = new HashSet<>(1000);
        try {
            while ((c = reader.read()) != -1) {
                if (Character.isISOControl(c) || c == '\u0020' || c == '\u00a0' || c == '\u3000' || c == 65533) {
                    if (removeDuplication) {
                        if (alphanumSize > 0) {
                            isSpace = removeLastDuplication(buf, alphanumSize, isSpace, termCache);
                        } else if (symbolSize > 0) {
                            isSpace = removeLastDuplication(buf, symbolSize, isSpace, termCache);
                        }
                    }
                    // space
                    if (!isSpace && !isLastSpaceChar(buf)) {
                        buf.appendCodePoint(' ');
                        isSpace = true;
                    }
                    alphanumSize = 0;
                    symbolSize = 0;
                } else if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                    if (removeDuplication && symbolSize > 0) {
                        isSpace = removeLastDuplication(buf, symbolSize, isSpace, termCache);
                    }
                    // alphanum
                    if (maxAlphanumTermSize >= 0) {
                        if (alphanumSize < maxAlphanumTermSize) {
                            buf.appendCodePoint(c);
                            alphanumSize++;
                        }
                    } else {
                        buf.appendCodePoint(c);
                        alphanumSize++;
                    }
                    isSpace = false;
                    symbolSize = 0;
                } else if ((c >= '!' && c <= '/') || (c >= ':' && c <= '@') || (c >= '[' && c <= '`') || (c >= '{' && c <= '~')) {
                    if (removeDuplication && alphanumSize > 0) {
                        isSpace = removeLastDuplication(buf, alphanumSize, isSpace, termCache);
                    }
                    // symbol
                    if (maxSymbolTermSize >= 0) {
                        if (symbolSize < maxSymbolTermSize) {
                            buf.appendCodePoint(c);
                            symbolSize++;
                        }
                    } else {
                        buf.appendCodePoint(c);
                        symbolSize++;
                    }
                    isSpace = false;
                    alphanumSize = 0;
                } else {
                    if (removeDuplication) {
                        if (alphanumSize > 0) {
                            isSpace = removeLastDuplication(buf, alphanumSize, isSpace, termCache);
                        } else if (symbolSize > 0) {
                            isSpace = removeLastDuplication(buf, symbolSize, isSpace, termCache);
                        }
                    }
                    buf.appendCodePoint(c);
                    isSpace = false;
                    alphanumSize = 0;
                    symbolSize = 0;
                }
            }
            if (removeDuplication) {
                if (alphanumSize > 0) {
                    removeLastDuplication(buf, alphanumSize, isSpace, termCache);
                } else if (symbolSize > 0) {
                    removeLastDuplication(buf, symbolSize, isSpace, termCache);
                }
            }
        } catch (final IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to read data.", e);
            }
            return StringUtil.EMPTY;
        }

        return buf.toUnsafeString().trim();
    }

    private static boolean isLastSpaceChar(final UnsafeStringBuilder buf) {
        if (buf.length() == 0) {
            return false;
        }
        return buf.charAt(buf.length() - 1) == ' ';
    }

    private static boolean removeLastDuplication(final UnsafeStringBuilder buf, final int size, final boolean isSpace,
            final Set<String> termCache) {
        final String target = buf.rightString(size);
        if (!termCache.contains(target)) {
            termCache.add(target);
            return isSpace;
        }
        buf.setLength(buf.length() - size);
        if (!isSpace && !isLastSpaceChar(buf)) {
            buf.appendCodePoint(' ');
            return true;
        }
        return isSpace;
    }
}