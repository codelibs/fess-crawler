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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
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
 * If no BOM is detected, the configured {@link #encoding} is used. When the leading
 * bytes of the stream (up to {@link #PROBE_SIZE}) are not valid in that encoding, for
 * example a Shift_JIS or EUC-JP file read as UTF-8, the charset is detected from the
 * bytes instead (see {@link TikaExtractor#detectCharset(BufferedInputStream, String)}),
 * with the {@code Content-Encoding} parameter, if any, as a hint. Input that is valid
 * in the configured encoding is decoded exactly as before.
 * The text is streamed via a {@code BufferedReader} so that very large inputs do
 * not require buffering the entire file as a byte array. The total number of
 * characters appended to the result can be capped by {@link #maxTextLength}.
 */
public class TextExtractor extends AbstractExtractor {

    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(TextExtractor.class);

    /**
     * The encoding for text.
     */
    protected String encoding = Constants.UTF_8;

    /**
     * The number of leading bytes that are checked against the configured encoding.
     * Bytes after this limit are decoded with the charset chosen from these.
     */
    protected static final int PROBE_SIZE = 1024 * 1024;

    /**
     * The number of bytes handed to the charset detector.
     */
    private static final int DETECT_SIZE = 16384;

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
                InputStream body = bomIn;
                Charset charset;
                if (detected != null) {
                    charset = Charset.forName(detected);
                } else {
                    charset = Charset.forName(getEncoding());
                    final byte[] probe = bomIn.readNBytes(PROBE_SIZE);
                    body = new SequenceInputStream(new ByteArrayInputStream(probe), bomIn);
                    charset = chooseCharset(probe, charset, params);
                }
                try (Reader reader = new InputStreamReader(body, charset)) {
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
     * Returns the charset to decode the text with. The configured charset is kept when it decodes the probe,
     * which is the case for every valid text in it. Otherwise the charset is detected from the bytes.
     *
     * @param probe the leading bytes of the stream
     * @param configured the configured charset
     * @param params the extraction parameters (may be {@code null})
     * @return the charset to decode with
     * @throws IOException if the probe cannot be read
     */
    protected Charset chooseCharset(final byte[] probe, final Charset configured, final Map<String, String> params) throws IOException {
        final int invalid = findMalformed(probe, configured);
        if (invalid < 0) {
            return configured;
        }
        // start at the line of the bad byte: the lines before it are ASCII or valid, and say nothing about the charset
        int start = invalid;
        while (start > 0 && probe[start - 1] != '\n') {
            start--;
        }
        final String hint = params != null ? params.get(ExtractData.CONTENT_ENCODING) : null;
        try (BufferedInputStream window =
                new BufferedInputStream(new ByteArrayInputStream(probe, start, Math.min(DETECT_SIZE, probe.length - start)))) {
            final Charset detected = TikaExtractor.detectCharset(window, hint != null && !hint.isBlank() ? hint : configured.name());
            if (detected != null && findMalformed(probe, detected) < 0) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Detected text charset: {} (configured={}, hint={})", detected, configured, hint);
                }
                return detected;
            }
        }
        return configured;
    }

    /**
     * Returns the offset of the first byte that is not valid in the charset, or -1 when the bytes are valid. A
     * multibyte sequence that the probe cuts off is not an error, unless the probe is the whole stream.
     *
     * @param probe the leading bytes of the stream
     * @param charset the charset to check the bytes against
     * @return the offset of the first invalid byte, or -1
     */
    private int findMalformed(final byte[] probe, final Charset charset) {
        final CharsetDecoder decoder =
                charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        final ByteBuffer in = ByteBuffer.wrap(probe);
        final CharBuffer out = CharBuffer.allocate(8192);
        final boolean whole = probe.length < PROBE_SIZE;
        while (true) {
            CoderResult result = decoder.decode(in, out, whole);
            if (result.isError()) {
                return in.position();
            }
            if (result.isUnderflow()) {
                if (whole) {
                    result = decoder.flush(out);
                    if (result.isError()) {
                        return in.position();
                    }
                }
                return -1;
            }
            out.clear();
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
