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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;

/**
 * Extracts text content and metadata from CSV files using an RFC 4180 compliant
 * parser ({@code org.apache.commons:commons-csv}).
 *
 * <p>Features:
 * <ul>
 *   <li>RFC 4180 compliant parsing: quoted fields with embedded delimiters,
 *       embedded newlines, and doubled-quote escapes are honored.</li>
 *   <li>Automatic delimiter detection (comma, tab, semicolon, pipe) based on
 *       the first non-blank line.</li>
 *   <li>UTF-8 / UTF-16 BOM detection via {@link BOMInputStream}.</li>
 *   <li>Header row detection and column-name to value association.</li>
 *   <li>Configurable encoding, delimiter, quote character, escape character,
 *       and output separators.</li>
 *   <li>DoS guards via {@code maxRecords} and {@code maxFields} limits.</li>
 *   <li>Malformed CSV (e.g. unterminated quote) surfaces as
 *       {@link ExtractException} with a {@code key=value} context message
 *       including the line number reported by the parser.</li>
 * </ul>
 */
public class CsvExtractor extends AbstractExtractor {
    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(CsvExtractor.class);

    /** Buffer size used when peeking the input stream for BOM detection. */
    protected static final int BOM_BUFFER_SIZE = 4096;

    /** Default encoding for CSV files. */
    protected String encoding = Constants.UTF_8;

    /**
     * Maximum number of data records to extract. Records beyond this limit are
     * silently dropped to bound memory consumption.
     *
     * <p>Backed by {@link #maxRecords}; {@link #setMaxRows(int)} is preserved
     * for backward compatibility.
     */
    protected long maxRecords = 10_000_000L;

    /** Maximum total number of fields (across all records) to accumulate. */
    protected long maxFields = 1_000_000L;

    /** Delimiter character for CSV parsing. {@code null} enables auto-detection. */
    protected Character delimiter = null;

    /** Quote character for CSV parsing. */
    protected Character quoteCharacter = '"';

    /**
     * Optional escape character. {@code null} (the RFC 4180 default) means that
     * quote escaping is performed by doubling the quote character.
     */
    protected Character escapeCharacter = null;

    /** Whether the first row contains headers. */
    protected boolean hasHeader = true;

    /** Whether to auto-detect the delimiter. */
    protected boolean autoDetectDelimiter = true;

    /** Whether to extract column names as metadata. */
    protected boolean extractColumnMetadata = true;

    /** Separator for field values in text output. */
    protected String fieldSeparator = " ";

    /** Line separator for rows in text output. */
    protected String lineSeparator = "\n";

    /** Separator for header-value association in text output. */
    protected String headerValueSeparator = ": ";

    /**
     * Whether to trim leading/trailing whitespace from parsed CSV fields.
     *
     * <p>RFC 4180 does NOT specify trimming, and unquoted leading/trailing
     * whitespace is technically part of the field value. However, the legacy
     * {@code CsvExtractor} (pre commons-csv) trimmed every field via
     * {@code String.trim()} before emitting it. To preserve backward
     * compatibility with downstream consumers that already expect trimmed
     * output (and to keep search-relevance behavior unchanged when CSVs
     * contain incidental whitespace such as {@code "a, b, c"}), the default
     * here is {@code true}.
     *
     * <p>Set to {@code false} via {@link #setTrimFields(boolean)} for
     * byte-exact RFC 4180 behavior.
     */
    protected boolean trimFields = true;

    /**
     * Constructs a new CsvExtractor.
     */
    public CsvExtractor() {
        super();
    }

    @Override
    public int getWeight() {
        return 2; // Higher priority than TikaExtractor (weight=1)
    }

    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        validateInputStream(in);

        final Charset defaultCharset = getCharset(params);

        // Wrap the input so we can (a) detect BOMs, then (b) peek for
        // delimiter auto-detection without consuming bytes for the parser.
        final BufferedInputStream bomBuffered = new BufferedInputStream(in);
        final BomDetectionResult bomResult = detectBomCharset(bomBuffered, defaultCharset);
        final InputStream postBom = bomResult.stream;
        final Charset charset = bomResult.charset;

        // Wrap once more so we can mark/reset for delimiter detection.
        final BufferedInputStream peekBuffered = new BufferedInputStream(postBom);

        final List<String[]> rows = new ArrayList<>();
        String[] headers = null;
        long fieldCount = 0L;

        Character effectiveDelimiter = delimiter;
        if (effectiveDelimiter == null && autoDetectDelimiter) {
            effectiveDelimiter = peekAndDetectDelimiter(peekBuffered, charset);
        }
        if (effectiveDelimiter == null) {
            effectiveDelimiter = ',';
        }

        final CSVFormat format = buildFormat(effectiveDelimiter);

        CSVParser parser = null;
        try {
            final Reader reader = new InputStreamReader(peekBuffered, charset);
            parser = format.parse(reader);

            boolean headerCaptured = false;
            long recordCount = 0L;
            for (final CSVRecord record : parser) {
                final int size = record.size();
                final String[] fields = new String[size];
                for (int i = 0; i < size; i++) {
                    fields[i] = record.get(i);
                }

                if (hasHeader && !headerCaptured) {
                    headers = fields;
                    headerCaptured = true;
                    continue;
                }

                if (recordCount >= maxRecords) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("CSV input exceeded maxRecords={}, truncating", maxRecords);
                    }
                    break;
                }

                if (fieldCount + size > maxFields) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("CSV input exceeded maxFields={}, truncating", maxFields);
                    }
                    break;
                }

                rows.add(fields);
                fieldCount += size;
                recordCount++;
            }
        } catch (final IOException e) {
            final long line = parser != null ? parser.getCurrentLineNumber() : -1L;
            throw new ExtractException("CSV parse error: line=" + line + " error=" + e.getMessage(), e);
        } catch (final RuntimeException e) {
            // commons-csv may wrap parse errors in UncheckedIOException / IllegalStateException
            final long line = parser != null ? parser.getCurrentLineNumber() : -1L;
            throw new ExtractException("CSV parse error: line=" + line + " error=" + e.getMessage(), e);
        } finally {
            if (parser != null) {
                try {
                    parser.close();
                } catch (final IOException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to close CSV parser", e);
                    }
                }
            }
        }

        return buildExtractData(headers, rows);
    }

    /**
     * Builds a {@link CSVFormat} from the configured delimiter, quote, and
     * escape character. Empty lines are ignored. Quotes around fields are
     * stripped during parsing per RFC 4180.
     *
     * @param effectiveDelimiter the delimiter to use
     * @return the configured CSV format
     */
    protected CSVFormat buildFormat(final char effectiveDelimiter) {
        final CSVFormat.Builder builder = CSVFormat.RFC4180.builder() //
                .setDelimiter(effectiveDelimiter) //
                .setQuote(quoteCharacter) //
                .setEscape(escapeCharacter) //
                .setIgnoreEmptyLines(true) //
                .setTrim(trimFields);
        return builder.get();
    }

    /**
     * Holder for the result of BOM detection: the (possibly-wrapped) stream
     * to read CSV bytes from, and the {@link Charset} to use when decoding.
     */
    protected static final class BomDetectionResult {
        /** Stream from which CSV bytes (post-BOM) should be read. */
        public final InputStream stream;
        /** Charset to use when decoding the stream. */
        public final Charset charset;

        BomDetectionResult(final InputStream stream, final Charset charset) {
            this.stream = stream;
            this.charset = charset;
        }
    }

    /**
     * Detects a UTF-8/UTF-16/UTF-32 BOM at the head of {@code bis} and returns
     * a wrapped stream (with the BOM bytes consumed) along with the charset
     * implied by the BOM (or {@code defaultCharset} when no BOM is found).
     *
     * <p>The returned stream MUST be used for further reading; {@code bis}
     * cannot be read directly after this call because {@code BOMInputStream}
     * may have buffered bytes internally.
     *
     * @param bis the buffered input stream
     * @param defaultCharset the charset to return when no BOM is found
     * @return the BOM detection result
     */
    protected BomDetectionResult detectBomCharset(final BufferedInputStream bis, final Charset defaultCharset) {
        try {
            final BOMInputStream bomIn = BOMInputStream.builder() //
                    .setInputStream(bis) //
                    .setInclude(false) //
                    .setByteOrderMarks(ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_32BE,
                            ByteOrderMark.UTF_32LE) //
                    .get();
            if (bomIn.hasBOM()) {
                final String name = bomIn.getBOMCharsetName();
                if (logger.isDebugEnabled()) {
                    logger.debug("Detected BOM charset: {}", name);
                }
                if (name != null && Charset.isSupported(name)) {
                    return new BomDetectionResult(bomIn, Charset.forName(name));
                }
            }
            // No (recognized) BOM — still return the bomIn wrapper because it
            // has already buffered any bytes it peeked at; bis itself has
            // those bytes effectively consumed.
            return new BomDetectionResult(bomIn, defaultCharset);
        } catch (final IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("BOM detection failed; falling back to {}", defaultCharset, e);
            }
            return new BomDetectionResult(bis, defaultCharset);
        }
    }

    /**
     * Peeks at the first non-blank line of the stream (using mark/reset) and
     * returns the auto-detected delimiter. The stream position is restored on
     * exit so the parser still sees the full content.
     *
     * <p>The peek window is decoded as ISO-8859-1 (a byte-identity mapping)
     * rather than the actual file charset. This is intentional: the candidate
     * delimiters ({@code ,}, {@code \t}, {@code ;}, {@code |}) are all
     * single-byte ASCII characters, and decoding as ISO-8859-1 guarantees
     * that each byte maps 1:1 to one Java {@code char}. Decoding as the file
     * charset (e.g. UTF-8 or UTF-16) is unsafe here because the 4096-byte
     * window may slice a multi-byte sequence at the tail, leading to a
     * decoding error or a substitution character that drops a real delimiter
     * byte from the count. ISO-8859-1 sidesteps this entirely.
     *
     * @param bis a mark-supporting buffered input stream
     * @param charset charset to use when decoding the peeked bytes (currently
     *                unused; retained for API stability)
     * @return the detected delimiter, or {@code null} on failure
     */
    protected Character peekAndDetectDelimiter(final BufferedInputStream bis, final Charset charset) {
        bis.mark(BOM_BUFFER_SIZE);
        try {
            final byte[] buf = new byte[BOM_BUFFER_SIZE];
            final int read = bis.read(buf);
            if (read <= 0) {
                return null;
            }
            // Use ISO-8859-1 (byte-identity) so single-byte delimiter counting
            // is unaffected by truncated multi-byte sequences at the window
            // boundary. See JavaDoc above.
            final String head = new String(buf, 0, read, StandardCharsets.ISO_8859_1);
            for (final String line : head.split("\\r?\\n")) {
                if (StringUtil.isNotBlank(line)) {
                    return detectDelimiter(line);
                }
            }
            return null;
        } catch (final IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Delimiter auto-detection failed", e);
            }
            return null;
        } finally {
            try {
                bis.reset();
            } catch (final IOException e) {
                throw new ExtractException("Failed to reset CSV input stream", e);
            }
        }
    }

    /**
     * Detects the delimiter character from a single CSV line by counting
     * occurrences (outside of quoted spans) of common delimiters.
     *
     * @param line the CSV line to analyze
     * @return the detected delimiter character (defaults to {@code ','})
     */
    protected Character detectDelimiter(final String line) {
        final char[] candidates = { ',', '\t', ';', '|' };
        int maxCount = 0;
        char bestDelimiter = ',';

        for (final char candidate : candidates) {
            final int count = countOccurrences(line, candidate);
            if (count > maxCount) {
                maxCount = count;
                bestDelimiter = candidate;
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Detected delimiter: '{}' (count: {})", bestDelimiter, maxCount);
        }

        return bestDelimiter;
    }

    /**
     * Counts occurrences of a character in a string, excluding spans inside
     * doubled-quote-delimited fields.
     *
     * @param line the line to analyze
     * @param ch the character to count
     * @return the number of occurrences
     */
    protected int countOccurrences(final String line, final char ch) {
        int count = 0;
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            final char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ch && !inQuotes) {
                count++;
            }
        }

        return count;
    }

    /**
     * Builds the extract data from headers and rows.
     *
     * @param headers the header row (may be {@code null})
     * @param rows the data rows
     * @return the extract data
     */
    protected ExtractData buildExtractData(final String[] headers, final List<String[]> rows) {
        final StringBuilder textBuilder = new StringBuilder();

        // Add headers to text
        if (headers != null) {
            textBuilder.append(String.join(fieldSeparator, headers)).append(lineSeparator);
        }

        // Add rows to text
        for (final String[] row : rows) {
            if (headers != null && headers.length > 0) {
                // Associate values with column names
                final int minLength = Math.min(headers.length, row.length);
                for (int i = 0; i < minLength; i++) {
                    if (StringUtil.isNotBlank(row[i])) {
                        textBuilder.append(headers[i]).append(headerValueSeparator).append(row[i]).append(fieldSeparator);
                    }
                }
                textBuilder.append(lineSeparator);
            } else {
                // No headers, just concatenate values
                textBuilder.append(String.join(fieldSeparator, row)).append(lineSeparator);
            }
        }

        final ExtractData extractData = new ExtractData(textBuilder.toString().trim());

        // Add column metadata
        if (extractColumnMetadata && headers != null) {
            extractData.putValues("columns", headers);
            extractData.putValue("column_count", String.valueOf(headers.length));
        }

        extractData.putValue("row_count", String.valueOf(rows.size()));

        return extractData;
    }

    /**
     * Gets the character set from parameters or uses the default.
     *
     * @param params the parameters
     * @return the character set
     */
    protected Charset getCharset(final Map<String, String> params) {
        if (params != null && params.containsKey(ExtractData.CONTENT_ENCODING)) {
            final String encodingParam = params.get(ExtractData.CONTENT_ENCODING);
            if (StringUtil.isNotBlank(encodingParam)) {
                try {
                    return Charset.forName(encodingParam);
                } catch (final Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Invalid encoding: {}, using default: {}", encodingParam, encoding, e);
                    }
                }
            }
        }
        return Charset.forName(encoding);
    }

    /**
     * Sets the encoding for CSV files.
     *
     * @param encoding the encoding
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    /**
     * Sets the maximum number of rows to extract. Backward-compatible alias
     * for {@link #setMaxRecords(long)}.
     *
     * @param maxRows the maximum rows
     */
    public void setMaxRows(final int maxRows) {
        this.maxRecords = maxRows;
    }

    /**
     * Sets the maximum number of data records to extract.
     *
     * @param maxRecords the maximum number of records
     */
    public void setMaxRecords(final long maxRecords) {
        this.maxRecords = maxRecords;
    }

    /**
     * Sets the maximum total number of fields to accumulate (across all
     * records). Acts as a DoS guard.
     *
     * @param maxFields the maximum total number of fields
     */
    public void setMaxFields(final long maxFields) {
        this.maxFields = maxFields;
    }

    /**
     * Sets the delimiter character. Pass {@code null} to enable auto-detection
     * (subject to {@link #setAutoDetectDelimiter(boolean)}).
     *
     * @param delimiter the delimiter
     */
    public void setDelimiter(final Character delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Sets the quote character used by the parser.
     *
     * @param quoteCharacter the quote character (must not be {@code null})
     */
    public void setQuoteCharacter(final Character quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
    }

    /**
     * Sets the escape character used by the parser. The default ({@code null})
     * uses RFC 4180's doubled-quote escape.
     *
     * @param escapeCharacter the escape character or {@code null}
     */
    public void setEscapeCharacter(final Character escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    /**
     * Sets whether the first row contains headers.
     *
     * @param hasHeader true if has header
     */
    public void setHasHeader(final boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    /**
     * Sets whether to auto-detect the delimiter.
     *
     * @param autoDetectDelimiter true to auto-detect
     */
    public void setAutoDetectDelimiter(final boolean autoDetectDelimiter) {
        this.autoDetectDelimiter = autoDetectDelimiter;
    }

    /**
     * Sets whether to extract column names as metadata.
     *
     * @param extractColumnMetadata true to extract metadata
     */
    public void setExtractColumnMetadata(final boolean extractColumnMetadata) {
        this.extractColumnMetadata = extractColumnMetadata;
    }

    /**
     * Sets the field separator for text output.
     *
     * @param fieldSeparator the field separator
     */
    public void setFieldSeparator(final String fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    /**
     * Sets the line separator for text output.
     *
     * @param lineSeparator the line separator
     */
    public void setLineSeparator(final String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    /**
     * Backward-compatible alias for {@link #setLineSeparator(String)}.
     *
     * @param recordSeparator the record (line) separator
     */
    public void setRecordSeparator(final String recordSeparator) {
        this.lineSeparator = recordSeparator;
    }

    /**
     * Sets the separator for header-value association in text output.
     *
     * @param headerValueSeparator the header-value separator
     */
    public void setHeaderValueSeparator(final String headerValueSeparator) {
        this.headerValueSeparator = headerValueSeparator;
    }

    /**
     * Sets whether to trim leading/trailing whitespace from each parsed CSV
     * field.
     *
     * <p><b>Note:</b> RFC 4180 does <em>not</em> specify trimming, and
     * unquoted leading/trailing whitespace is technically part of the field
     * value. However, the legacy {@code CsvExtractor} (pre commons-csv)
     * trimmed every field via {@code String.trim()} before emitting it, so
     * the default here is {@code true} to preserve that backward-compatible
     * extraction output (e.g. {@code "a, b, c"} continues to yield fields
     * {@code "a", "b", "c"} rather than {@code "a", " b", " c"}).
     *
     * <p>Set to {@code false} to obtain byte-exact RFC 4180 behavior, which
     * preserves whitespace in unquoted fields.
     *
     * @param trimFields {@code true} (default) to trim every field;
     *                   {@code false} for strict RFC 4180 behavior
     */
    public void setTrimFields(final boolean trimFields) {
        this.trimFields = trimFields;
    }
}
