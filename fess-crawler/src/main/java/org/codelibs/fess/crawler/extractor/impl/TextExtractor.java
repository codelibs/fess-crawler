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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;

/**
 * Extracts text content from an input stream as plain text.
 *
 * <p>The extractor honors a Byte Order Mark (BOM) at the start of the stream when
 * present (UTF-8, UTF-16 LE/BE, UTF-32 LE/BE) and decodes the stream accordingly.
 * If no BOM is detected, the configured {@link #encoding} is used.
 * The text is streamed via a {@code BufferedReader} so that very large inputs do
 * not require buffering the entire file as a byte array. The total number of
 * characters appended to the result can be capped by {@link #maxTextLength}.
 */
public class TextExtractor extends AbstractExtractor {

    /**
     * The encoding for text.
     */
    protected String encoding = Constants.UTF_8;

    /**
     * Maximum number of characters to read from the input. The default is
     * {@link Long#MAX_VALUE}, which is effectively unlimited. Values less than
     * or equal to zero explicitly disable the limit.
     *
     * <p>The limit is measured in Java {@code char} units (UTF-16 code units).
     * At the truncation boundary, an unpaired high surrogate is dropped to avoid
     * leaving an invalid string.
     */
    protected long maxTextLength = Long.MAX_VALUE;

    /**
     * Creates a new TextExtractor instance.
     */
    public TextExtractor() {
        super();
    }

    /**
     * Extracts text from the supplied input stream.
     *
     * <p>The stream is decoded using the configured {@link #encoding}, overridden
     * when a BOM (UTF-8, UTF-16 LE/BE, UTF-32 LE/BE) is detected at the start.
     * The total character count is bounded by {@link #maxTextLength}. When
     * truncation occurs, a WARN-level log message is emitted and the returned
     * {@link ExtractData} carries {@code truncated=true} and
     * {@code maxTextLength=<value>} metadata entries. The supplied {@code in} is
     * closed by this method.
     *
     * @param in the text input stream; must not be {@code null}
     * @param params optional extraction parameters (may be {@code null})
     * @return the extracted text and optional truncation metadata
     * @throws org.codelibs.fess.crawler.exception.CrawlerSystemException if {@code in} is {@code null}
     * @throws ExtractException if reading or decoding fails
     */
    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        validateInputStream(in);
        try {
            try (BOMInputStream bomIn = BOMInputStream.builder()
                    .setInputStream(in)
                    .setInclude(false)
                    .setByteOrderMarks(ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE,
                            ByteOrderMark.UTF_32BE)
                    .get()) {
                final String detected = bomIn.getBOMCharsetName();
                final String charset = detected != null ? detected : getEncoding();
                try (Reader reader = new InputStreamReader(bomIn, charset)) {
                    final TextReadResult result = readWithLimit(reader, maxTextLength);
                    final ExtractData extractData = new ExtractData(result.content);
                    if (result.truncated) {
                        extractData.putValue("truncated", "true");
                        extractData.putValue("maxTextLength", Long.toString(maxTextLength));
                    }
                    return extractData;
                }
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
     * Sets the maximum number of characters that will be extracted. The default
     * is {@link Long#MAX_VALUE}, which is effectively unlimited. Values less
     * than or equal to zero explicitly disable the limit.
     *
     * <p>The limit is measured in Java {@code char} units (UTF-16 code units).
     * At the truncation boundary, an unpaired high surrogate is dropped to avoid
     * leaving an invalid string.
     *
     * @param maxTextLength the maximum text length in characters
     */
    public void setMaxTextLength(final long maxTextLength) {
        this.maxTextLength = maxTextLength;
    }
}
