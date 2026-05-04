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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;

/**
 * Extracts text content from an input stream as plain text.
 *
 * <p>The extractor honors a Byte Order Mark (BOM) at the start of the stream when
 * present (UTF-8, UTF-16 LE/BE, UTF-32 LE/BE) and decodes the stream accordingly.
 * If no BOM is detected, the configured {@link #encoding} is used.
 * The text is streamed via a {@link BufferedReader} so that very large inputs do
 * not require buffering the entire file as a byte array. The total number of
 * characters appended to the result can be capped by {@link #maxTextLength}.
 */
public class TextExtractor extends AbstractExtractor {

    private static final Logger logger = LogManager.getLogger(TextExtractor.class);

    /**
     * Default read buffer size in characters.
     */
    private static final int READ_BUFFER_SIZE = 8192;

    /**
     * The encoding for text.
     */
    protected String encoding = Constants.UTF_8;

    /**
     * Maximum number of characters to read from the input. Defaults to
     * {@link Long#MAX_VALUE} which effectively imposes no limit. Set to a
     * positive value to cap the resulting text length and avoid excessive heap
     * usage on very large inputs.
     */
    protected long maxTextLength = Long.MAX_VALUE;

    /**
     * Creates a new TextExtractor instance.
     */
    public TextExtractor() {
        super();
    }

    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        validateInputStream(in);
        try {
            final BOMInputStream bomIn = BOMInputStream.builder()
                    .setInputStream(in)
                    .setInclude(false)
                    .setByteOrderMarks(ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE,
                            ByteOrderMark.UTF_32BE)
                    .get();
            final String detected = bomIn.getBOMCharsetName();
            final String charset = detected != null ? detected : getEncoding();
            try (Reader reader = new InputStreamReader(bomIn, charset); BufferedReader br = new BufferedReader(reader)) {
                final StringBuilder sb = new StringBuilder();
                final char[] buf = new char[READ_BUFFER_SIZE];
                long total = 0;
                int n;
                while ((n = br.read(buf)) >= 0) {
                    if (maxTextLength > 0 && total + n > maxTextLength) {
                        final int remaining = (int) (maxTextLength - total);
                        if (remaining > 0) {
                            sb.append(buf, 0, remaining);
                        }
                        if (logger.isDebugEnabled()) {
                            logger.debug("Truncating text content at maxTextLength={} characters", maxTextLength);
                        }
                        break;
                    }
                    sb.append(buf, 0, n);
                    total += n;
                }
                return new ExtractData(sb.toString());
            }
        } catch (final Exception e) {
            throw new ExtractException("Failed to extract text content using encoding: " + getEncoding(), e);
        }
    }

    /**
     * Returns the encoding used for text extraction.
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

    /**
     * Returns the maximum number of characters that will be extracted.
     * @return the maximum text length
     */
    public long getMaxTextLength() {
        return maxTextLength;
    }

    /**
     * Sets the maximum number of characters that will be extracted. The
     * default is {@link Long#MAX_VALUE} which effectively means unlimited.
     * Setting a positive value caps the resulting text and stops reading once
     * the limit is reached. Values less than or equal to zero are interpreted
     * as unlimited.
     *
     * @param maxTextLength the maximum text length in characters
     */
    public void setMaxTextLength(final long maxTextLength) {
        this.maxTextLength = maxTextLength;
    }
}
