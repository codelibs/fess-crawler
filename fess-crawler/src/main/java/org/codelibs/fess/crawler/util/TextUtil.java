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

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;

/**
 * Utility class for text normalization and processing.
 *
 * This class provides methods to normalize text by reading characters from a provided Reader
 * and processing them according to specific rules. The main functionality is encapsulated
 * within the nested {@link TextNormalizeContext} class.
 *
 * <p>The text normalization process includes:
 * <ul>
 *   <li>Treating ISO control characters and specified space characters as spaces.</li>
 *   <li>Appending alphanumeric characters (0-9, A-Z, a-z) to the buffer.</li>
 *   <li>Appending symbol characters (!-/, :-@, [-`, {-~) to the buffer.</li>
 *   <li>Optionally removing duplicate terms based on a flag.</li>
 *   <li>Limiting the maximum size of alphanumeric and symbol terms.</li>
 * </ul>
 *
 * <p>The {@link TextNormalizeContext} class provides a fluent API to configure the text
 * normalization process, including setting initial buffer capacity, maximum term sizes,
 * duplicate term removal, and custom space characters.
 *
 * <p>Example usage:
 * <pre>{@code
 * Reader reader = new StringReader("Example text to normalize.");
 * String normalizedText = TextUtil.normalizeText(reader)
 *                                  .initialCapacity(5000)
 *                                  .maxAlphanumTermSize(100)
 *                                  .maxSymbolTermSize(50)
 *                                  .duplicateTermRemoved(true)
 *                                  .spaceChars(new int[] { ' ', '\u00a0' })
 *                                  .execute();
 * System.out.println(normalizedText);
 * }</pre>
 *
 * <p>Note: This class is not intended to be instantiated.
 *
 * @see TextNormalizeContext
 */
public final class TextUtil {
    private static final Logger logger = LogManager.getLogger(TextUtil.class);

    private TextUtil() {
    }

    /**
     * This class provides a context for normalizing text.
     */
    public static class TextNormalizeContext {

        private final Reader reader;

        private int initialCapacity = 10000;

        private int maxAlphanumTermSize = -1;

        private int maxSymbolTermSize = -1;

        private boolean duplicateTermRemoved = false;

        /**
         * Array of space characters. Default includes common space characters.
         */
        private int[] spaceChars = { '\u0020', '\u00a0', '\u3000', '\ufffd' };

        /**
         * Constructor.
         * @param reader The reader.
         */
        public TextNormalizeContext(final Reader reader) {
            this.reader = reader;
        }

        /**
         * Executes the text processing operation on the provided reader.
         *
         * This method reads characters from the reader and processes them according to the following rules:
         * - ISO control characters and space characters are treated as spaces.
         * - Alphanumeric characters (0-9, A-Z, a-z) are appended to the buffer.
         * - Symbol characters (!-/, :-@, [-`, {-~) are appended to the buffer.
         * - Duplicate terms can be removed based on the `duplicateTermRemoved` flag.
         * - The maximum size of alphanumeric and symbol terms can be limited by `maxAlphanumTermSize` and `maxSymbolTermSize` respectively.
         *
         * @return A processed string with terms and spaces, or an empty string if the reader is null or an IOException occurs.
         */
        public String execute() {
            if (reader == null) {
                return StringUtil.EMPTY;
            }
            final StringBuilder buf = new StringBuilder(initialCapacity);
            boolean isSpace = false;
            int alphanumSize = 0;
            int symbolSize = 0;
            int c;
            final Set<String> termCache = new HashSet<>(1000);
            try {
                while ((c = reader.read()) != -1) {
                    if (Character.isISOControl(c) || isSpaceChar(c)) {
                        if (duplicateTermRemoved) {
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
                    } else if (c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') {
                        if (duplicateTermRemoved && symbolSize > 0) {
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
                    } else if (c >= '!' && c <= '/' || c >= ':' && c <= '@' || c >= '[' && c <= '`' || c >= '{' && c <= '~') {
                        if (duplicateTermRemoved && alphanumSize > 0) {
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
                        if (duplicateTermRemoved) {
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
                if (duplicateTermRemoved) {
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

            return buf.toString().trim();
        }

        private boolean isSpaceChar(final int c) {
            for (final int spaceChar : spaceChars) {
                if (c == spaceChar) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Sets the initial capacity for the text normalization context.
         *
         * @param initialCapacity the initial capacity to be set
         * @return the updated TextNormalizeContext instance
         */
        public TextNormalizeContext initialCapacity(final int initialCapacity) {
            this.initialCapacity = initialCapacity;
            return this;
        }

        /**
         * Sets the maximum size for alphanumeric terms.
         *
         * @param maxAlphanumTermSize the maximum size for alphanumeric terms
         * @return the current instance of {@code TextNormalizeContext} for method chaining
         */
        public TextNormalizeContext maxAlphanumTermSize(final int maxAlphanumTermSize) {
            this.maxAlphanumTermSize = maxAlphanumTermSize;
            return this;
        }

        /**
         * Sets the maximum size for symbol terms.
         *
         * @param maxSymbolTermSize the maximum size for symbol terms
         * @return the current instance of TextNormalizeContext
         */
        public TextNormalizeContext maxSymbolTermSize(final int maxSymbolTermSize) {
            this.maxSymbolTermSize = maxSymbolTermSize;
            return this;
        }

        /**
         * Sets the flag indicating whether duplicate terms should be removed.
         *
         * @param duplicateTermRemoved true if duplicate terms should be removed, false otherwise
         * @return the current instance of {@link TextNormalizeContext}
         */
        public TextNormalizeContext duplicateTermRemoved(final boolean duplicateTermRemoved) {
            this.duplicateTermRemoved = duplicateTermRemoved;
            return this;
        }

        /**
         * Sets the array of space characters to be used for text normalization.
         *
         * @param spaceChars an array of integers representing space characters
         * @return the current instance of TextNormalizeContext for method chaining
         */
        public TextNormalizeContext spaceChars(final int[] spaceChars) {
            this.spaceChars = spaceChars;
            return this;
        }
    }

    /**
     * Normalizes the text from the given Reader.
     *
     * @param reader the Reader from which to read the text to be normalized
     * @return a TextNormalizeContext containing the normalized text
     */
    public static TextNormalizeContext normalizeText(final Reader reader) {
        return new TextNormalizeContext(reader);
    }

    private static boolean isLastSpaceChar(final StringBuilder buf) {
        if (buf.length() == 0) {
            return false;
        }
        return buf.charAt(buf.length() - 1) == ' ';
    }

    private static boolean removeLastDuplication(final StringBuilder buf, final int size, final boolean isSpace,
            final Set<String> termCache) {
        final String target = rightString(buf, size);
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

    private static String rightString(final StringBuilder buf, final int length) {
        if (length <= 0) {
            return StringUtil.EMPTY;
        }
        if (length >= buf.length()) {
            return buf.toString();
        }
        return buf.substring(buf.length() - length, buf.length());
    }
}
