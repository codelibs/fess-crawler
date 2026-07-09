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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Extracts text content and metadata from JSON files.
 * This extractor provides better structured data extraction compared to Tika's generic text extraction.
 *
 * <p>Features:
 * <ul>
 *   <li>Structured text extraction with key-value pairs</li>
 *   <li>Top-level field extraction as metadata</li>
 *   <li>Nested structure flattening with configurable depth</li>
 *   <li>Array element extraction</li>
 *   <li>Configurable field separator and array formatting</li>
 * </ul>
 *
 * <p>
 * The shared {@link #objectMapper} is configured with explicit
 * {@link StreamReadConstraints} (max nesting depth, max string length, max
 * number length) pinned to Jackson's own out-of-the-box defaults purely to make
 * that ceiling explicit and tunable; they do <em>not</em> reject anything a
 * default-configured {@link ObjectMapper} would otherwise accept and are not the
 * memory guard. The actual protection against pathological memory/CPU use comes
 * from two independent, opt-in bounds (both default to unlimited, so the
 * out-of-the-box behavior is unchanged):
 * </p>
 * <ul>
 *   <li>Input size is bounded by {@link #maxContentLength}: oversized input is
 *       <em>rejected</em> with {@link MaxLengthExceededException} before the
 *       {@link JsonNode} tree is fully materialized, which bounds the dominant
 *       {@code readTree()} allocation.</li>
 *   <li>Accumulated <em>output</em> text is bounded by {@link #maxTextLength}
 *       using the truncate-not-reject convention shared with
 *       {@link TextExtractor} and {@link MarkdownExtractor}: once the cap is
 *       reached, the recursive walk stops early and the returned
 *       {@link ExtractData} carries {@code truncated=true} and
 *       {@code maxTextLength=<value>} metadata entries.</li>
 * </ul>
 */
public class JsonExtractor extends AbstractExtractor {
    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(JsonExtractor.class);

    /**
     * Maximum nesting depth the shared {@link #objectMapper} will accept while
     * parsing. Pinned to Jackson's own {@code StreamReadConstraints} default
     * (also documented as the JVM's typical safe stack-depth ceiling), so
     * out-of-the-box parsing accepts exactly what a default-configured Jackson
     * {@link ObjectMapper} would, rather than being tighter than master.
     */
    protected static final int MAX_NESTING_DEPTH = 1000;

    /**
     * Maximum total string length (in characters) the shared
     * {@link #objectMapper} will accept for any single token/value. Pinned to
     * Jackson's own {@code StreamReadConstraints} default so out-of-the-box
     * parsing accepts exactly what a default-configured Jackson
     * {@link ObjectMapper} would, rather than being tighter than master.
     */
    protected static final int MAX_STRING_LENGTH = 20_000_000;

    /**
     * Maximum number length (in characters) the shared {@link #objectMapper}
     * will accept. This already matches Jackson's own {@code StreamReadConstraints}
     * default, so out-of-the-box parsing accepts exactly what a
     * default-configured Jackson {@link ObjectMapper} would.
     */
    protected static final int MAX_NUMBER_LENGTH = 1000;

    /** Jackson ObjectMapper for JSON parsing. */
    protected final ObjectMapper objectMapper = new ObjectMapper();

    /** Maximum depth for nested structure extraction. */
    protected int maxDepth = 10;

    /** Separator for key-value pairs in extracted text. */
    protected String fieldSeparator = ": ";

    /** Separator between different fields in extracted text. */
    protected String lineSeparator = "\n";

    /** Whether to extract top-level fields as metadata. */
    protected boolean extractMetadata = true;

    /** Prefix for flattened nested keys. */
    protected String nestedKeySeparator = ".";

    /** Maximum number of array elements to extract. */
    protected int maxArrayElements = 100;

    /**
     * Maximum number of characters to accumulate in the extracted text output.
     * The default is {@link Long#MAX_VALUE}, which is effectively unlimited.
     * Values less than or equal to zero explicitly disable the limit.
     *
     * <p>The limit is measured in Java {@code char} units (UTF-16 code units).
     * At the truncation boundary, an unpaired high surrogate is dropped to avoid
     * leaving an invalid string.
     */
    protected long maxTextLength = Long.MAX_VALUE;

    /**
     * Maximum number of input bytes to read before rejecting oversized JSON with
     * {@link MaxLengthExceededException}. Values less than or equal to zero (the
     * default) disable the limit, preserving the previous unbounded behavior.
     * Bounds the dominant {@code readTree()} allocation by capping the input the
     * tree is built from.
     */
    protected long maxContentLength = 0;

    /**
     * Constructs a new JsonExtractor.
     */
    public JsonExtractor() {
        super();
        final StreamReadConstraints constraints = StreamReadConstraints.builder()
                .maxNestingDepth(MAX_NESTING_DEPTH)
                .maxStringLength(MAX_STRING_LENGTH)
                .maxNumberLength(MAX_NUMBER_LENGTH)
                .build();
        objectMapper.getFactory().setStreamReadConstraints(constraints);
    }

    @Override
    public int getWeight() {
        return 2; // Higher priority than TikaExtractor (weight=1)
    }

    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        validateInputStream(in);

        try {
            final JsonNode rootNode = objectMapper.readTree(limitInputStream(in, maxContentLength));
            final TextAccumulator textAccumulator = new TextAccumulator(maxTextLength);
            final Map<String, List<String>> metadataMap = new LinkedHashMap<>();

            extractContent(rootNode, "", textAccumulator, metadataMap, 0);

            final ExtractData extractData = new ExtractData(textAccumulator.builder.toString().trim());

            if (textAccumulator.truncated) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Extracted JSON content truncated: maxTextLength={}", maxTextLength);
                }
                extractData.putValue("truncated", "true");
                extractData.putValue("maxTextLength", Long.toString(maxTextLength));
            }

            if (extractMetadata) {
                for (final Map.Entry<String, List<String>> entry : metadataMap.entrySet()) {
                    final List<String> values = entry.getValue();
                    extractData.putValues(entry.getKey(), values.toArray(new String[0]));
                }
            }

            return extractData;
        } catch (final IOException e) {
            throw new ExtractException("Failed to parse JSON content", e);
        }
    }

    /**
     * Mutable per-invocation accumulator for the recursive JSON walk. Bounds
     * the total number of characters appended to {@code maxLength}, mirroring
     * the {@code maxTextLength} truncate-not-reject convention used by
     * {@link AbstractExtractor#readWithLimit(java.io.Reader, long)}: once the
     * cap is reached, further appends are silently dropped and
     * {@link #truncated} is set so callers can flag the result.
     */
    protected static final class TextAccumulator {
        /** The accumulated text. */
        protected final StringBuilder builder = new StringBuilder();

        /** The maximum number of characters to accumulate; {@code <= 0} means unlimited. */
        protected final long maxLength;

        /** Whether truncation has occurred. */
        protected boolean truncated;

        /**
         * Creates a new accumulator.
         * @param maxLength the maximum number of characters to accumulate
         */
        protected TextAccumulator(final long maxLength) {
            this.maxLength = maxLength;
        }

        /**
         * Appends {@code s} unless doing so would exceed {@link #maxLength}, in
         * which case as much of {@code s} as still fits is appended and no
         * further appends are accepted.
         * @param s the text to append
         */
        protected void append(final String s) {
            if (truncated || s == null || s.isEmpty()) {
                return;
            }
            if (maxLength > 0 && (long) builder.length() + s.length() > maxLength) {
                final int remaining = (int) (maxLength - builder.length());
                if (remaining > 0) {
                    builder.append(s, 0, remaining);
                }
                if (builder.length() > 0 && Character.isHighSurrogate(builder.charAt(builder.length() - 1))) {
                    builder.setLength(builder.length() - 1);
                }
                truncated = true;
                return;
            }
            builder.append(s);
        }
    }

    /**
     * Recursively extracts content from JSON nodes.
     *
     * @param node the JSON node to extract from
     * @param parentKey the parent key path
     * @param textAccumulator the accumulator for text content, bounded by {@link #maxTextLength}
     * @param metadataMap the map for metadata extraction
     * @param depth the current depth in the JSON structure
     */
    protected void extractContent(final JsonNode node, final String parentKey, final TextAccumulator textAccumulator,
            final Map<String, List<String>> metadataMap, final int depth) {
        if (node == null || depth > maxDepth || textAccumulator.truncated) {
            return;
        }

        if (node.isObject()) {
            extractObject((ObjectNode) node, parentKey, textAccumulator, metadataMap, depth);
        } else if (node.isArray()) {
            extractArray((ArrayNode) node, parentKey, textAccumulator, metadataMap, depth);
        } else {
            extractValue(node, parentKey, textAccumulator, metadataMap, depth);
        }
    }

    /**
     * Extracts content from a JSON object node.
     *
     * @param node the object node
     * @param parentKey the parent key path
     * @param textAccumulator the accumulator for text content, bounded by {@link #maxTextLength}
     * @param metadataMap the map for metadata extraction
     * @param depth the current depth
     */
    protected void extractObject(final ObjectNode node, final String parentKey, final TextAccumulator textAccumulator,
            final Map<String, List<String>> metadataMap, final int depth) {
        final Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext() && !textAccumulator.truncated) {
            final Map.Entry<String, JsonNode> field = fields.next();
            final String fieldName = field.getKey();
            final JsonNode fieldValue = field.getValue();

            final String currentKey = buildKey(parentKey, fieldName);
            extractContent(fieldValue, currentKey, textAccumulator, metadataMap, depth + 1);
        }
    }

    /**
     * Extracts content from a JSON array node.
     *
     * @param node the array node
     * @param parentKey the parent key path
     * @param textAccumulator the accumulator for text content, bounded by {@link #maxTextLength}
     * @param metadataMap the map for metadata extraction
     * @param depth the current depth
     */
    protected void extractArray(final ArrayNode node, final String parentKey, final TextAccumulator textAccumulator,
            final Map<String, List<String>> metadataMap, final int depth) {
        final int size = Math.min(node.size(), maxArrayElements);
        for (int i = 0; i < size && !textAccumulator.truncated; i++) {
            final JsonNode element = node.get(i);
            final String currentKey = parentKey + "[" + i + "]";
            extractContent(element, currentKey, textAccumulator, metadataMap, depth + 1);
        }

        if (node.size() > maxArrayElements) {
            if (logger.isDebugEnabled()) {
                logger.debug("Array at {} has {} elements, only first {} extracted", parentKey, node.size(), maxArrayElements);
            }
        }
    }

    /**
     * Extracts a primitive value from a JSON node.
     *
     * @param node the value node
     * @param key the key for this value
     * @param textAccumulator the accumulator for text content, bounded by {@link #maxTextLength}
     * @param metadataMap the map for metadata extraction
     * @param depth the current depth
     */
    protected void extractValue(final JsonNode node, final String key, final TextAccumulator textAccumulator,
            final Map<String, List<String>> metadataMap, final int depth) {
        if (node.isNull()) {
            return;
        }

        final String value = node.asText();
        if (StringUtil.isBlank(value)) {
            return;
        }

        // Add to text content
        if (StringUtil.isNotBlank(key)) {
            textAccumulator.append(key);
            textAccumulator.append(fieldSeparator);
            textAccumulator.append(value);
            textAccumulator.append(lineSeparator);
        } else {
            textAccumulator.append(value);
            textAccumulator.append(lineSeparator);
        }

        // Add to metadata if at top level
        if (depth <= 1 && StringUtil.isNotBlank(key)) {
            metadataMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
    }

    /**
     * Builds a nested key path.
     *
     * @param parentKey the parent key
     * @param currentKey the current key
     * @return the combined key path
     */
    protected String buildKey(final String parentKey, final String currentKey) {
        if (StringUtil.isBlank(parentKey)) {
            return currentKey;
        }
        return parentKey + nestedKeySeparator + currentKey;
    }

    /**
     * Sets the maximum depth for nested structure extraction.
     *
     * @param maxDepth the maximum depth
     */
    public void setMaxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * Sets the field separator for key-value pairs.
     *
     * @param fieldSeparator the field separator
     */
    public void setFieldSeparator(final String fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    /**
     * Sets the line separator between fields.
     *
     * @param lineSeparator the line separator
     */
    public void setLineSeparator(final String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    /**
     * Sets whether to extract top-level fields as metadata.
     *
     * @param extractMetadata true to extract metadata
     */
    public void setExtractMetadata(final boolean extractMetadata) {
        this.extractMetadata = extractMetadata;
    }

    /**
     * Sets the separator for nested keys.
     *
     * @param nestedKeySeparator the nested key separator
     */
    public void setNestedKeySeparator(final String nestedKeySeparator) {
        this.nestedKeySeparator = nestedKeySeparator;
    }

    /**
     * Sets the maximum number of array elements to extract.
     *
     * @param maxArrayElements the maximum array elements
     */
    public void setMaxArrayElements(final int maxArrayElements) {
        this.maxArrayElements = maxArrayElements;
    }

    /**
     * Returns the maximum number of characters that will be accumulated in the
     * extracted text output.
     *
     * @return the maximum text length
     */
    public long getMaxTextLength() {
        return maxTextLength;
    }

    /**
     * Sets the maximum number of characters that will be accumulated in the
     * extracted text output. The default is {@link Long#MAX_VALUE}, which is
     * effectively unlimited. Values less than or equal to zero explicitly
     * disable the limit.
     *
     * <p>The limit is measured in Java {@code char} units (UTF-16 code units).
     * At the truncation boundary, an unpaired high surrogate is dropped to avoid
     * leaving an invalid string.
     *
     * @param maxTextLength the maximum text length
     */
    public void setMaxTextLength(final long maxTextLength) {
        this.maxTextLength = maxTextLength;
    }

    /**
     * Returns the maximum number of input bytes that will be read before
     * oversized JSON is rejected with {@link MaxLengthExceededException}.
     *
     * @return the maximum input content length in bytes
     */
    public long getMaxContentLength() {
        return maxContentLength;
    }

    /**
     * Sets the maximum number of input bytes to read before oversized JSON is
     * rejected with {@link MaxLengthExceededException}. Values less than or equal
     * to zero (the default) disable the limit, preserving the previous unbounded
     * behavior. Bounding the input size bounds the dominant {@code readTree()}
     * allocation, so a hostile or accidentally huge stream cannot exhaust heap.
     *
     * @param maxContentLength the maximum input content length in bytes
     */
    public void setMaxContentLength(final long maxContentLength) {
        this.maxContentLength = maxContentLength;
    }
}
