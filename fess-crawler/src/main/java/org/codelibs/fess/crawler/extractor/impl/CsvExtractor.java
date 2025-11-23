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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;

/**
 * Extracts text content and metadata from CSV files.
 * This extractor provides better structured data extraction compared to Tika's generic text extraction.
 *
 * <p>Features:
 * <ul>
 *   <li>Automatic delimiter detection (comma, tab, semicolon, pipe)</li>
 *   <li>Header row detection and extraction</li>
 *   <li>Column name to data value association</li>
 *   <li>Quoted field handling</li>
 *   <li>Column names as metadata</li>
 *   <li>Configurable encoding and row limits</li>
 * </ul>
 */
public class CsvExtractor extends AbstractExtractor {
    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(CsvExtractor.class);

    /** Default encoding for CSV files. */
    protected String encoding = Constants.UTF_8;

    /** Maximum number of rows to extract. */
    protected int maxRows = 10000;

    /** Delimiter character for CSV parsing. */
    protected Character delimiter = null;

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

    /** Pattern for detecting quoted fields. */
    private static final Pattern QUOTED_FIELD_PATTERN = Pattern.compile("^\".*\"$");

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

        final Charset charset = getCharset(params);
        final List<String[]> rows = new ArrayList<>();
        String[] headers = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset))) {
            String line;
            Character detectedDelimiter = delimiter;

            // Read header row if needed
            if (hasHeader) {
                while ((line = reader.readLine()) != null) {
                    if (StringUtil.isBlank(line)) {
                        continue;
                    }
                    // Auto-detect delimiter from first non-blank line
                    if (detectedDelimiter == null && autoDetectDelimiter) {
                        detectedDelimiter = detectDelimiter(line);
                    }
                    headers = parseCsvLine(line, detectedDelimiter != null ? detectedDelimiter : ',');
                    break;
                }
            }

            // Read data rows
            int dataRowCount = 0;
            while (dataRowCount < maxRows && (line = reader.readLine()) != null) {
                if (StringUtil.isBlank(line)) {
                    continue;
                }
                // Auto-detect delimiter from first data row if not already set (when no header)
                if (detectedDelimiter == null && autoDetectDelimiter) {
                    detectedDelimiter = detectDelimiter(line);
                }
                final String[] fields = parseCsvLine(line, detectedDelimiter != null ? detectedDelimiter : ',');
                rows.add(fields);
                dataRowCount++;
            }

            if (dataRowCount >= maxRows && logger.isDebugEnabled()) {
                logger.debug("CSV file exceeded max rows ({}), only first {} data rows extracted", maxRows, dataRowCount);
            }

            return buildExtractData(headers, rows);
        } catch (final IOException e) {
            throw new ExtractException("Failed to parse CSV content", e);
        }
    }

    /**
     * Detects the delimiter character from a CSV line.
     *
     * @param line the CSV line to analyze
     * @return the detected delimiter character
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
     * Counts occurrences of a character in a string, excluding quoted sections.
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
     * Parses a CSV line into fields.
     *
     * @param line the CSV line
     * @param delimiter the delimiter character
     * @return array of field values
     */
    protected String[] parseCsvLine(final String line, final char delimiter) {
        final List<String> fields = new ArrayList<>();
        final StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            final char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Escaped quote
                    currentField.append('"');
                    i++; // Skip next quote
                } else {
                    // Toggle quote state
                    inQuotes = !inQuotes;
                }
            } else if (c == delimiter && !inQuotes) {
                // Field separator
                fields.add(unquoteField(currentField.toString().trim()));
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }

        // Add last field
        fields.add(unquoteField(currentField.toString().trim()));

        return fields.toArray(new String[0]);
    }

    /**
     * Removes surrounding quotes from a field value.
     *
     * @param field the field value
     * @return the unquoted field value
     */
    protected String unquoteField(final String field) {
        if (QUOTED_FIELD_PATTERN.matcher(field).matches()) {
            return field.substring(1, field.length() - 1).replace("\"\"", "\"");
        }
        return field;
    }

    /**
     * Builds the extract data from headers and rows.
     *
     * @param headers the header row
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
     * Sets the maximum number of rows to extract.
     *
     * @param maxRows the maximum rows
     */
    public void setMaxRows(final int maxRows) {
        this.maxRows = maxRows;
    }

    /**
     * Sets the delimiter character.
     *
     * @param delimiter the delimiter
     */
    public void setDelimiter(final Character delimiter) {
        this.delimiter = delimiter;
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
     * Sets the separator for header-value association in text output.
     *
     * @param headerValueSeparator the header-value separator
     */
    public void setHeaderValueSeparator(final String headerValueSeparator) {
        this.headerValueSeparator = headerValueSeparator;
    }
}
