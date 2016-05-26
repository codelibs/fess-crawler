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
        } catch (IOException e) {
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
        try {
            while ((c = reader.read()) != -1) {
                if (Character.isISOControl(c) || c == '\u0020' || c == '\u00a0' || c == '\u3000' || c == 65533) {
                    if (removeDuplication) {
                        if (alphanumSize > 0) {
                            isSpace = removeLastDuplication(buf, alphanumSize, isSpace);
                        } else if (symbolSize > 0) {
                            isSpace = removeLastDuplication(buf, symbolSize, isSpace);
                        }
                    }
                    // space
                    if (!isSpace && buf.lastIndexOf(' ') != buf.length() - 1) {
                        buf.appendCodePoint(' ');
                        isSpace = true;
                    }
                    alphanumSize = 0;
                    symbolSize = 0;
                } else if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                    if (removeDuplication && symbolSize > 0) {
                        isSpace = removeLastDuplication(buf, symbolSize, isSpace);
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
                        isSpace = removeLastDuplication(buf, alphanumSize, isSpace);
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
                            isSpace = removeLastDuplication(buf, alphanumSize, isSpace);
                        } else if (symbolSize > 0) {
                            isSpace = removeLastDuplication(buf, symbolSize, isSpace);
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
                    removeLastDuplication(buf, alphanumSize, isSpace);
                } else if (symbolSize > 0) {
                    removeLastDuplication(buf, symbolSize, isSpace);
                }
            }
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to read data.", e);
            }
            return StringUtil.EMPTY;
        }

        return buf.toUnsafeString().trim();
    }

    private static boolean removeLastDuplication(final UnsafeStringBuilder buf, int size, boolean isSpace) {
        final String target = buf.rightString(size);
        final int pos = buf.indexOf(target);
        if (pos >= 0 && pos < buf.length() - size) {
            buf.setLength(buf.length() - size);
            if (!isSpace && buf.lastIndexOf(' ') != buf.length() - 1) {
                buf.appendCodePoint(' ');
                return true;
            }
        }
        return isSpace;
    }

}