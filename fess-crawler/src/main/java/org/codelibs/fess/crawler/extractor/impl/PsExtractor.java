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
package org.codelibs.fess.crawler.extractor.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.exception.UnsupportedExtractException;

/**
 * Extracts text content from PostScript (.ps) files.
 *
 * <p>
 * This extractor parses PostScript files and extracts text rendered by
 * show-family operators ({@code show}, {@code ashow}, {@code widthshow},
 * {@code awidthshow}, {@code xshow}, {@code yshow},
 * {@code xyshow}, {@code kshow}). It handles parenthesized string literals with escape
 * sequences and hexadecimal string literals.
 * </p>
 *
 * <p>
 * Limitations: This extractor does not handle dynamically generated text
 * via loops or procedures, font encoding redefinitions, or binary-encoded
 * PostScript files.
 * </p>
 */
public class PsExtractor extends AbstractExtractor {

    private static final Set<String> SHOW_OPERATORS =
            Set.of("show", "ashow", "widthshow", "awidthshow", "xshow", "yshow", "xyshow", "kshow");

    /**
     * The encoding for reading PostScript files.
     */
    protected String encoding = Constants.UTF_8;

    /**
     * Creates a new PsExtractor instance.
     */
    public PsExtractor() {
        super();
    }

    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        validateInputStream(in);
        try {
            final String psContent = new String(InputStreamUtil.getBytes(in), getEncoding());
            final String extractedText = extractText(psContent);
            if (extractedText.isEmpty()) {
                throw new UnsupportedExtractException("No text found in PostScript content.");
            }
            return new ExtractData(extractedText);
        } catch (final UnsupportedExtractException e) {
            throw e;
        } catch (final Exception e) {
            throw new ExtractException("Failed to extract text from PostScript content.", e);
        }
    }

    @Override
    public int getWeight() {
        return 10;
    }

    /**
     * Returns the encoding used for reading PostScript files.
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the encoding.
     * @param encoding The encoding to set.
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    protected String extractText(final String psContent) {
        final List<String> collectedTexts = new ArrayList<>();
        final List<String> stack = new ArrayList<>();
        final int len = psContent.length();
        int pos = 0;

        while (pos < len) {
            final char ch = psContent.charAt(pos);

            if (ch == '%') {
                // Skip comment until end of line
                pos = skipComment(psContent, pos, len);
            } else if (ch == '(') {
                // Parse parenthesized string literal
                final int[] result = new int[1];
                final String str = parseParenString(psContent, pos, len, result);
                if (str != null) {
                    stack.add(str);
                }
                pos = result[0];
            } else if (ch == '<' && pos + 1 < len && psContent.charAt(pos + 1) != '<') {
                // Parse hexadecimal string literal
                final int[] result = new int[1];
                final String str = parseHexString(psContent, pos, len, result);
                if (str != null) {
                    stack.add(str);
                }
                pos = result[0];
            } else if (isWhitespace(ch)) {
                pos++;
            } else if (ch == '/') {
                // PostScript name literal: skip /name
                pos++;
                while (pos < len && !isDelimiter(psContent.charAt(pos))) {
                    pos++;
                }
            } else if (ch == '<') {
                // Dictionary start '<<' (single '<' is handled by hex string above)
                if (pos + 1 < len && psContent.charAt(pos + 1) == '<') {
                    pos += 2;
                } else {
                    pos++;
                }
            } else if (ch == '>') {
                // Dictionary end '>>' or stray '>'
                if (pos + 1 < len && psContent.charAt(pos + 1) == '>') {
                    pos += 2;
                } else {
                    pos++;
                }
            } else if (ch == '[' || ch == ']' || ch == '{' || ch == '}' || ch == ')') {
                // Array/procedure delimiters, stray closing paren: skip
                pos++;
            } else {
                // Parse token
                final int start = pos;
                while (pos < len && !isDelimiter(psContent.charAt(pos))) {
                    pos++;
                }
                if (pos == start) {
                    // Unknown delimiter character: skip to prevent infinite loop
                    pos++;
                    continue;
                }
                final String token = psContent.substring(start, pos);

                if (SHOW_OPERATORS.contains(token) && !stack.isEmpty()) {
                    collectedTexts.add(stack.remove(stack.size() - 1));
                } else if (!isNumeric(token)) {
                    // Non-string, non-numeric token: clear stack
                    stack.clear();
                }
            }
        }

        return String.join(" ", collectedTexts);
    }

    private int skipComment(final String psContent, final int pos, final int len) {
        int i = pos + 1;
        while (i < len && psContent.charAt(i) != '\n' && psContent.charAt(i) != '\r') {
            i++;
        }
        return i;
    }

    private String parseParenString(final String psContent, final int startPos, final int len, final int[] outPos) {
        final StringBuilder sb = new StringBuilder();
        int pos = startPos + 1; // skip opening '('
        int depth = 1;

        while (pos < len && depth > 0) {
            final char ch = psContent.charAt(pos);
            if (ch == '\\') {
                pos++;
                if (pos < len) {
                    final char escaped = psContent.charAt(pos);
                    switch (escaped) {
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '(':
                        sb.append('(');
                        break;
                    case ')':
                        sb.append(')');
                        break;
                    default:
                        if (escaped >= '0' && escaped <= '7') {
                            // Octal escape
                            int octal = escaped - '0';
                            int count = 1;
                            while (count < 3 && pos + 1 < len && psContent.charAt(pos + 1) >= '0' && psContent.charAt(pos + 1) <= '7') {
                                pos++;
                                octal = octal * 8 + (psContent.charAt(pos) - '0');
                                count++;
                            }
                            sb.append((char) octal);
                        } else if (escaped == '\r') {
                            // Line continuation: \<CR> or \<CR><LF>
                            if (pos + 1 < len && psContent.charAt(pos + 1) == '\n') {
                                pos++;
                            }
                        } else if (escaped == '\n') {
                            // Line continuation: \<LF>
                        } else {
                            sb.append(escaped);
                        }
                        break;
                    }
                }
                pos++;
            } else if (ch == '(') {
                depth++;
                sb.append(ch);
                pos++;
            } else if (ch == ')') {
                depth--;
                if (depth > 0) {
                    sb.append(ch);
                }
                pos++;
            } else {
                sb.append(ch);
                pos++;
            }
        }

        outPos[0] = pos;
        return sb.toString();
    }

    private String parseHexString(final String psContent, final int startPos, final int len, final int[] outPos) {
        final StringBuilder hex = new StringBuilder();
        int pos = startPos + 1; // skip opening '<'

        while (pos < len && psContent.charAt(pos) != '>') {
            final char ch = psContent.charAt(pos);
            if (!isWhitespace(ch)) {
                hex.append(ch);
            }
            pos++;
        }
        if (pos < len) {
            pos++; // skip closing '>'
        }

        outPos[0] = pos;

        final String hexStr = hex.toString();
        // Pad with trailing zero if odd length
        final String paddedHex = hexStr.length() % 2 != 0 ? hexStr + "0" : hexStr;

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i + 1 < paddedHex.length(); i += 2) {
            try {
                final int value = Integer.parseInt(paddedHex.substring(i, i + 2), 16);
                sb.append((char) value);
            } catch (final NumberFormatException e) {
                // Skip invalid hex pairs
            }
        }
        return sb.toString();
    }

    private boolean isWhitespace(final char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r' || ch == '\f' || ch == '\0';
    }

    private boolean isDelimiter(final char ch) {
        return isWhitespace(ch) || ch == '(' || ch == ')' || ch == '<' || ch == '>' || ch == '[' || ch == ']' || ch == '{' || ch == '}'
                || ch == '/' || ch == '%';
    }

    private boolean isNumeric(final String token) {
        if (token.isEmpty()) {
            return false;
        }

        // Radix notation: base#digits (e.g. 16#FF, 8#77, 2#1010)
        final int hashIndex = token.indexOf('#');
        if (hashIndex > 0 && hashIndex < token.length() - 1) {
            for (int j = 0; j < hashIndex; j++) {
                final char c = token.charAt(j);
                if (c < '0' || c > '9') {
                    return false;
                }
            }
            for (int j = hashIndex + 1; j < token.length(); j++) {
                final char c = token.charAt(j);
                if (!((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
                    return false;
                }
            }
            return true;
        }

        int i = 0;
        if (token.charAt(0) == '-' || token.charAt(0) == '+') {
            if (token.length() == 1) {
                return false;
            }
            i = 1;
        }
        boolean hasDot = false;
        boolean hasExponent = false;
        for (; i < token.length(); i++) {
            final char ch = token.charAt(i);
            if (ch == '.') {
                if (hasDot || hasExponent) {
                    return false;
                }
                hasDot = true;
            } else if (ch == 'e' || ch == 'E') {
                if (hasExponent || i == 0 || i == token.length() - 1) {
                    return false;
                }
                hasExponent = true;
                // Allow optional sign after exponent
                if (i + 1 < token.length()) {
                    final char next = token.charAt(i + 1);
                    if (next == '+' || next == '-') {
                        i++;
                        if (i == token.length() - 1) {
                            return false;
                        }
                    }
                }
            } else if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }
}
